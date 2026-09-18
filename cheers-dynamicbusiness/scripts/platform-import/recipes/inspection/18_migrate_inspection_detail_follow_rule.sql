-- 巡检数据类型：详情跟随规则迁移
-- 规则口径：
-- 1) 普通数据类型：DETAIL 默认跟随对应实体（ENTITY -> DETAIL，detail_follow）
-- 2) 分类即实体：DETAIL 默认跟随分类（CATEGORY -> DETAIL，detail_follow）
--
-- 说明：
-- - 默认覆盖巡检相关数据类型（可按 target_codes CTE 调整）
-- - 脚本幂等，可重复执行
-- - 不做读路径兜底；只修布局与关系声明

SET search_path TO dynamicbusiness;

BEGIN;

CREATE TEMP TABLE tmp_inspection_detail_target_layouts ON COMMIT DROP AS
WITH target_codes AS (
    SELECT UNNEST(ARRAY[
        'inspection_item',
        'inspection-content',
        'patrol_equipment',
        'patrol_route',
        'patrol_schedule',
        'patrol_target',
        'task_patrol',
        'task_record_patrol'
    ]) AS code
),
target_types AS (
    SELECT
        et.tenant_id,
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
    JOIN target_codes tc ON tc.code = et.code
    WHERE et.deleted = FALSE
)
SELECT
    tenant_id,
    code,
    name,
    entry_kind,
    expected_entity_code,
    data_layout_id AS layout_id,
    'data'::text AS layout_scope
FROM target_types
WHERE data_layout_id IS NOT NULL
UNION ALL
SELECT
    tenant_id,
    code,
    name,
    entry_kind,
    expected_entity_code,
    model_layout_id AS layout_id,
    'model'::text AS layout_scope
FROM target_types
WHERE model_layout_id IS NOT NULL;

-- 1) 补 DETAIL 栏（若缺失，tab_id 统一用 tab-detail-1）
WITH missing_detail AS (
    SELECT t.*
    FROM tmp_inspection_detail_target_layouts t
    WHERE NOT EXISTS (
        SELECT 1
        FROM dm_data_tab_layout x
        WHERE x.tenant_id = t.tenant_id
          AND x.layout_id = t.layout_id
          AND x.column_kind = 'DETAIL'
          AND x.deleted = FALSE
    )
)
INSERT INTO dm_data_tab_layout (
    tenant_id,
    entity_type_code,
    column_kind,
    tab_id,
    props_id,
    enabled,
    column_meta,
    creator,
    create_time,
    updater,
    update_time,
    deleted,
    layout_id
)
SELECT
    m.tenant_id,
    m.code,
    'DETAIL',
    'tab-detail-1',
    NULL,
    TRUE,
    '{"label":"详情","columnSection":"what"}'::jsonb,
    'script:18_migrate_inspection_detail_follow_rule',
    NOW(),
    'script:18_migrate_inspection_detail_follow_rule',
    NOW(),
    FALSE,
    m.layout_id
FROM missing_detail m;

-- 1.1) 选定每个布局的“详情栏身份”（优先最早的有效 DETAIL 行）
CREATE TEMP TABLE tmp_inspection_detail_identity ON COMMIT DROP AS
SELECT DISTINCT ON (t.tenant_id, t.layout_id)
    t.tenant_id,
    t.layout_id,
    d.tab_id AS detail_tab_id,
    'DETAIL:' || d.tab_id AS detail_identity
FROM tmp_inspection_detail_target_layouts t
JOIN dm_data_tab_layout d
  ON d.tenant_id = t.tenant_id
 AND d.layout_id = t.layout_id
 AND d.deleted = FALSE
 AND d.column_kind = 'DETAIL'
 AND COALESCE(d.tab_id, '') <> ''
ORDER BY t.tenant_id, t.layout_id, d.id;

-- 2) 普通数据类型补对应 ENTITY 栏（若缺失）
WITH missing_entity AS (
    SELECT t.*
    FROM tmp_inspection_detail_target_layouts t
    WHERE t.entry_kind <> 'CATEGORY'
      AND NOT EXISTS (
          SELECT 1
          FROM dm_data_tab_layout x
          WHERE x.tenant_id = t.tenant_id
            AND x.layout_id = t.layout_id
            AND x.column_kind = 'ENTITY'
            AND x.tab_id = t.expected_entity_code
            AND x.deleted = FALSE
      )
)
INSERT INTO dm_data_tab_layout (
    tenant_id,
    entity_type_code,
    column_kind,
    tab_id,
    props_id,
    enabled,
    column_meta,
    creator,
    create_time,
    updater,
    update_time,
    deleted,
    layout_id
)
SELECT
    m.tenant_id,
    m.code,
    'ENTITY',
    m.expected_entity_code,
    NULL,
    TRUE,
    jsonb_build_object(
        'label', m.expected_entity_code,
        'columnSection', 'who',
        'entityEntityTypeCode', m.expected_entity_code
    ),
    'script:18_migrate_inspection_detail_follow_rule',
    NOW(),
    'script:18_migrate_inspection_detail_follow_rule',
    NOW(),
    FALSE,
    m.layout_id
FROM missing_entity m;

-- 3) 清理不符合口径的 detail_follow 入边
UPDATE dm_data_tab_column_relation r
SET deleted = TRUE,
    updater = 'script:18_migrate_inspection_detail_follow_rule',
    update_time = NOW()
FROM tmp_inspection_detail_target_layouts t
JOIN tmp_inspection_detail_identity di
  ON di.tenant_id = t.tenant_id
 AND di.layout_id = t.layout_id
WHERE r.tenant_id = t.tenant_id
  AND r.layout_id = t.layout_id
  AND r.deleted = FALSE
  AND r.to_column_identity = di.detail_identity
  AND r.relation_meta ->> 'edgeAction' = 'detail_follow'
  AND (
      (t.entry_kind = 'CATEGORY' AND r.from_column_identity NOT LIKE 'CATEGORY:%')
      OR
      (t.entry_kind <> 'CATEGORY' AND r.from_column_identity <> ('ENTITY:' || t.expected_entity_code))
  );

-- 4) 去重：同一来源到 DETAIL 的 detail_follow 只保留一条
WITH ranked AS (
    SELECT
        r.id,
        ROW_NUMBER() OVER (
            PARTITION BY r.tenant_id, r.layout_id, r.from_column_identity, r.to_column_identity
            ORDER BY r.id
        ) AS rn
    FROM dm_data_tab_column_relation r
    JOIN tmp_inspection_detail_target_layouts t
      ON t.tenant_id = r.tenant_id
     AND t.layout_id = r.layout_id
    JOIN tmp_inspection_detail_identity di
      ON di.tenant_id = r.tenant_id
     AND di.layout_id = r.layout_id
    WHERE r.deleted = FALSE
      AND r.to_column_identity = di.detail_identity
      AND r.relation_meta ->> 'edgeAction' = 'detail_follow'
)
UPDATE dm_data_tab_column_relation r
SET deleted = TRUE,
    updater = 'script:18_migrate_inspection_detail_follow_rule',
    update_time = NOW()
FROM ranked k
WHERE r.id = k.id
  AND k.rn > 1;

-- 5) 普通数据类型补 ENTITY -> DETAIL detail_follow
INSERT INTO dm_data_tab_column_relation (
    entity_type_code,
    edge_id,
    from_column_identity,
    to_column_identity,
    relation_kind,
    from_type_code,
    to_type_code,
    relation_meta,
    creator,
    create_time,
    updater,
    update_time,
    deleted,
    tenant_id,
    layout_id
)
SELECT
    t.code,
    'ed-rule-' || t.layout_id || '-' || t.expected_entity_code,
    'ENTITY:' || t.expected_entity_code,
    di.detail_identity,
    'ENTITY_DETAIL',
    t.expected_entity_code,
    t.expected_entity_code,
    '{"edgeAction":"detail_follow","enabledInteractions":[]}'::jsonb,
    'script:18_migrate_inspection_detail_follow_rule',
    NOW(),
    'script:18_migrate_inspection_detail_follow_rule',
    NOW(),
    FALSE,
    t.tenant_id,
    t.layout_id
FROM tmp_inspection_detail_target_layouts t
JOIN tmp_inspection_detail_identity di
  ON di.tenant_id = t.tenant_id
 AND di.layout_id = t.layout_id
WHERE t.entry_kind <> 'CATEGORY'
  AND EXISTS (
      SELECT 1
      FROM dm_data_tab_layout e
      WHERE e.tenant_id = t.tenant_id
        AND e.layout_id = t.layout_id
        AND e.column_kind = 'ENTITY'
        AND e.tab_id = t.expected_entity_code
        AND e.deleted = FALSE
  )
  AND NOT EXISTS (
      SELECT 1
      FROM dm_data_tab_column_relation r
      WHERE r.tenant_id = t.tenant_id
        AND r.layout_id = t.layout_id
        AND r.deleted = FALSE
        AND r.from_column_identity = 'ENTITY:' || t.expected_entity_code
        AND r.to_column_identity = di.detail_identity
        AND r.relation_meta ->> 'edgeAction' = 'detail_follow'
  );

-- 6) 分类即实体补 CATEGORY -> DETAIL detail_follow（取首个有效分类栏）
WITH category_source AS (
    SELECT DISTINCT ON (t.tenant_id, t.layout_id)
        t.tenant_id,
        t.layout_id,
        t.code,
        t.expected_entity_code,
        'CATEGORY:' ||
        COALESCE(NULLIF(c.column_meta ->> 'columnKey', ''), 'category') ||
        ':' || c.tab_id AS from_column_identity,
        COALESCE(NULLIF(c.column_meta ->> 'categoryTypeCode', ''), t.expected_entity_code) AS from_type_code
    FROM tmp_inspection_detail_target_layouts t
    JOIN dm_data_tab_layout c
      ON c.tenant_id = t.tenant_id
     AND c.layout_id = t.layout_id
     AND c.deleted = FALSE
     AND c.column_kind = 'CATEGORY'
     AND COALESCE(c.tab_id, '') <> ''
    WHERE t.entry_kind = 'CATEGORY'
    ORDER BY t.tenant_id, t.layout_id, c.id
)
INSERT INTO dm_data_tab_column_relation (
    entity_type_code,
    edge_id,
    from_column_identity,
    to_column_identity,
    relation_kind,
    from_type_code,
    to_type_code,
    relation_meta,
    creator,
    create_time,
    updater,
    update_time,
    deleted,
    tenant_id,
    layout_id
)
SELECT
    c.code,
    'cd-rule-' || c.layout_id || '-' || REPLACE(c.from_type_code, ' ', '_'),
    c.from_column_identity,
    di.detail_identity,
    'CATEGORY_DETAIL',
    c.from_type_code,
    c.from_type_code,
    '{"edgeAction":"detail_follow","enabledInteractions":[]}'::jsonb,
    'script:18_migrate_inspection_detail_follow_rule',
    NOW(),
    'script:18_migrate_inspection_detail_follow_rule',
    NOW(),
    FALSE,
    c.tenant_id,
    c.layout_id
FROM category_source c
JOIN tmp_inspection_detail_identity di
  ON di.tenant_id = c.tenant_id
 AND di.layout_id = c.layout_id
WHERE 1 = 1
AND NOT EXISTS (
    SELECT 1
    FROM dm_data_tab_column_relation r
    WHERE r.tenant_id = c.tenant_id
      AND r.layout_id = c.layout_id
      AND r.deleted = FALSE
      AND r.from_column_identity = c.from_column_identity
      AND r.to_column_identity = di.detail_identity
      AND r.relation_meta ->> 'edgeAction' = 'detail_follow'
);

COMMIT;

-- 建议执行后立刻跑：
-- scripts/platform-import/recipes/inspection/17_audit_inspection_detail_follow_rule.sql
