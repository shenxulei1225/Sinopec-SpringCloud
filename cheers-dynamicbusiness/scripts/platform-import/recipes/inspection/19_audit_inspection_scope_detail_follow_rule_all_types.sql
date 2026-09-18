-- 巡检范围全量审计：详情默认连线规则
-- 目标：一次性扫出巡检范围内所有“不符合详情默认连线规则”的数据类型
--
-- 规则口径：
-- 1) 普通类型（NATIVE / DOMAIN / SCOPE / REUSE）：
--    详情默认跟随对应实体栏（ENTITY -> DETAIL, edgeAction=detail_follow）
-- 2) 分类即实体（CATEGORY）：
--    详情默认跟随分类栏（CATEGORY -> DETAIL, edgeAction=detail_follow）
--
-- 输出：
-- - violation_code / violation_reason：违规项
-- - suggested_fix：建议修复动作（用于整改清单）

SET search_path TO dynamicbusiness;

WITH RECURSIVE seed_types AS (
    SELECT DISTINCT et.code
    FROM dynamic_entity_type et
    WHERE et.deleted = FALSE
      AND (
          et.code ILIKE '%inspection%'
          OR et.code ILIKE '%patrol%'
          OR et.name LIKE '%巡检%'
          OR et.name LIKE '%检查%'
      )
),
type_edges AS (
    SELECT et.code AS src_code, et.base_entity_type_code AS dst_code
    FROM dynamic_entity_type et
    WHERE et.deleted = FALSE
      AND COALESCE(et.base_entity_type_code, '') <> ''
    UNION ALL
    SELECT et.base_entity_type_code AS src_code, et.code AS dst_code
    FROM dynamic_entity_type et
    WHERE et.deleted = FALSE
      AND COALESCE(et.base_entity_type_code, '') <> ''
),
scope_codes AS (
    SELECT s.code
    FROM seed_types s
    UNION
    SELECT te.dst_code
    FROM type_edges te
    JOIN scope_codes sc ON sc.code = te.src_code
),
target_types AS (
    SELECT
        et.tenant_id,
        et.id AS entity_type_id,
        et.code,
        et.name,
        et.entry_kind,
        CASE
            WHEN et.entry_kind IN ('REUSE', 'SCOPE', 'DOMAIN')
                 AND COALESCE(et.base_entity_type_code, '') <> ''
            THEN et.base_entity_type_code
            ELSE et.code
        END AS expected_entity_code,
        et.data_layout_id,
        et.model_layout_id
    FROM dynamic_entity_type et
    JOIN scope_codes sc ON sc.code = et.code
    WHERE et.deleted = FALSE
),
target_layouts AS (
    SELECT tenant_id, entity_type_id, code, name, entry_kind, expected_entity_code, data_layout_id AS layout_id, 'data'::text AS layout_scope
    FROM target_types WHERE data_layout_id IS NOT NULL
    UNION ALL
    SELECT tenant_id, entity_type_id, code, name, entry_kind, expected_entity_code, model_layout_id AS layout_id, 'model'::text AS layout_scope
    FROM target_types WHERE model_layout_id IS NOT NULL
),
detail_pick AS (
    SELECT DISTINCT ON (l.tenant_id, l.layout_id)
        l.tenant_id,
        l.layout_id,
        d.tab_id AS detail_tab_id,
        'DETAIL:' || d.tab_id AS detail_identity
    FROM target_layouts l
    JOIN dm_data_tab_layout d
      ON d.tenant_id = l.tenant_id
     AND d.layout_id = l.layout_id
     AND d.deleted = FALSE
     AND d.column_kind = 'DETAIL'
     AND COALESCE(d.tab_id, '') <> ''
    ORDER BY l.tenant_id, l.layout_id, d.id
),
layout_flags AS (
    SELECT
        l.*,
        COUNT(*) FILTER (WHERE c.deleted = FALSE AND c.column_kind = 'DETAIL') AS detail_col_cnt,
        COUNT(*) FILTER (WHERE c.deleted = FALSE AND c.column_kind = 'ENTITY' AND c.tab_id = l.expected_entity_code) AS expected_entity_col_cnt,
        COUNT(*) FILTER (WHERE c.deleted = FALSE AND c.column_kind = 'CATEGORY') AS category_col_cnt
    FROM target_layouts l
    LEFT JOIN dm_data_tab_layout c
      ON c.tenant_id = l.tenant_id
     AND c.layout_id = l.layout_id
    GROUP BY l.tenant_id, l.entity_type_id, l.code, l.name, l.entry_kind, l.expected_entity_code, l.layout_id, l.layout_scope
),
detail_edges AS (
    SELECT
        l.tenant_id,
        l.layout_id,
        p.detail_identity,
        COUNT(*) FILTER (
            WHERE r.deleted = FALSE
              AND p.detail_identity IS NOT NULL
              AND r.to_column_identity = p.detail_identity
              AND r.relation_meta ->> 'edgeAction' = 'detail_follow'
        ) AS detail_follow_total,
        COUNT(*) FILTER (
            WHERE r.deleted = FALSE
              AND p.detail_identity IS NOT NULL
              AND r.to_column_identity = p.detail_identity
              AND r.relation_meta ->> 'edgeAction' = 'detail_follow'
              AND r.from_column_identity = ('ENTITY:' || l.expected_entity_code)
        ) AS expected_entity_edge_cnt,
        COUNT(*) FILTER (
            WHERE r.deleted = FALSE
              AND p.detail_identity IS NOT NULL
              AND r.to_column_identity = p.detail_identity
              AND r.relation_meta ->> 'edgeAction' = 'detail_follow'
              AND r.from_column_identity LIKE 'CATEGORY:%'
        ) AS category_edge_cnt
    FROM target_layouts l
    LEFT JOIN detail_pick p
      ON p.tenant_id = l.tenant_id
     AND p.layout_id = l.layout_id
    LEFT JOIN dm_data_tab_column_relation r
      ON r.tenant_id = l.tenant_id
     AND r.layout_id = l.layout_id
    GROUP BY l.tenant_id, l.layout_id, l.expected_entity_code, p.detail_identity
),
violations AS (
    SELECT
        f.tenant_id, f.code, f.name, f.entry_kind, f.layout_scope, f.layout_id, f.expected_entity_code,
        'MISSING_DETAIL_COLUMN'::text AS violation_code,
        '缺 DETAIL 栏，无法承载详情跟随'::text AS violation_reason,
        '先补 DETAIL 栏，再补默认 detail_follow 入边'::text AS suggested_fix
    FROM layout_flags f
    WHERE f.detail_col_cnt = 0

    UNION ALL

    SELECT
        f.tenant_id, f.code, f.name, f.entry_kind, f.layout_scope, f.layout_id, f.expected_entity_code,
        'MISSING_EXPECTED_ENTITY_COLUMN',
        '普通类型缺对应 ENTITY 栏，无法按默认规则 ENTITY -> DETAIL',
        '补 ENTITY:' || f.expected_entity_code || ' 栏，再补 ENTITY -> DETAIL 的 detail_follow'
    FROM layout_flags f
    WHERE f.entry_kind <> 'CATEGORY'
      AND f.expected_entity_col_cnt = 0

    UNION ALL

    SELECT
        f.tenant_id, f.code, f.name, f.entry_kind, f.layout_scope, f.layout_id, f.expected_entity_code,
        'MISSING_DETAIL_FOLLOW_EDGE',
        '缺 detail_follow 入边',
        CASE
            WHEN f.entry_kind = 'CATEGORY'
            THEN '补 CATEGORY -> DETAIL 的 detail_follow'
            ELSE '补 ENTITY:' || f.expected_entity_code || ' -> DETAIL 的 detail_follow'
        END
    FROM layout_flags f
    JOIN detail_edges e ON e.tenant_id = f.tenant_id AND e.layout_id = f.layout_id
    WHERE f.detail_col_cnt > 0
      AND e.detail_follow_total = 0

    UNION ALL

    SELECT
        f.tenant_id, f.code, f.name, f.entry_kind, f.layout_scope, f.layout_id, f.expected_entity_code,
        'TOO_MANY_DETAIL_FOLLOW_EDGES',
        'DETAIL 栏存在多条 detail_follow 入边，应收敛为 1 条',
        '保留符合口径的一条，软删其余 detail_follow 入边'
    FROM layout_flags f
    JOIN detail_edges e ON e.tenant_id = f.tenant_id AND e.layout_id = f.layout_id
    WHERE e.detail_follow_total > 1

    UNION ALL

    SELECT
        f.tenant_id, f.code, f.name, f.entry_kind, f.layout_scope, f.layout_id, f.expected_entity_code,
        'CATEGORY_KIND_SHOULD_USE_CATEGORY_SOURCE',
        '分类即实体应使用 CATEGORY -> DETAIL',
        '将详情来源改为 CATEGORY -> DETAIL（detail_follow）'
    FROM layout_flags f
    JOIN detail_edges e ON e.tenant_id = f.tenant_id AND e.layout_id = f.layout_id
    WHERE f.entry_kind = 'CATEGORY'
      AND f.detail_col_cnt > 0
      AND e.category_edge_cnt = 0

    UNION ALL

    SELECT
        f.tenant_id, f.code, f.name, f.entry_kind, f.layout_scope, f.layout_id, f.expected_entity_code,
        'NON_CATEGORY_SHOULD_USE_ENTITY_SOURCE',
        '普通类型应使用 ENTITY -> DETAIL，不能走 CATEGORY -> DETAIL',
        '删 CATEGORY -> DETAIL，补 ENTITY:' || f.expected_entity_code || ' -> DETAIL'
    FROM layout_flags f
    JOIN detail_edges e ON e.tenant_id = f.tenant_id AND e.layout_id = f.layout_id
    WHERE f.entry_kind <> 'CATEGORY'
      AND f.detail_col_cnt > 0
      AND e.expected_entity_edge_cnt = 0
)
SELECT
    v.tenant_id,
    v.code,
    v.name,
    v.entry_kind,
    v.layout_scope,
    v.layout_id,
    v.expected_entity_code,
    v.violation_code,
    v.violation_reason,
    v.suggested_fix
FROM violations v
ORDER BY
    v.code,
    CASE v.layout_scope WHEN 'data' THEN 0 ELSE 1 END,
    v.layout_id,
    v.violation_code;
