-- V72: layout 旧 relationMode:cascade + host/member → 栏间关系 CATEGORY_CATEGORY filter/write；
--       清 column_meta 上的 relationRole，relationMode 统一为 intersection。
-- 运行时裁树只读 dm_data_tab_column_relation（edgeRole=filter），不再读 layout 级联标记。
-- 幂等：已有同 layout 同端点同 edgeRole 的 CATEGORY_CATEGORY 行则跳过 insert。

SET search_path TO dynamicbusiness, public;

-- 1) 从 layout 行解析 host/member 对（同 layout_id + tenant_id，按 columnOrder 序号配对）
WITH cascade_rows AS (
    SELECT
        l.id,
        l.tenant_id,
        l.layout_id,
        l.entity_type_code,
        COALESCE(NULLIF(TRIM(l.tab_id), ''), 'default') AS tab_id,
        COALESCE(NULLIF(TRIM(l.column_meta ->> 'columnKey'), ''), 'default') AS column_key,
        TRIM(l.column_meta ->> 'categoryTypeCode') AS category_type_code,
        LOWER(TRIM(l.column_meta ->> 'relationRole')) AS relation_role,
        COALESCE(NULLIF(TRIM(l.column_meta ->> 'columnOrder'), '')::INT, 2147483647) AS column_order
    FROM dynamicbusiness.dm_data_tab_layout l
    WHERE l.deleted = FALSE
      AND l.enabled = TRUE
      AND UPPER(TRIM(l.column_kind)) = 'CATEGORY'
      AND LOWER(COALESCE(l.column_meta ->> 'relationMode', '')) = 'cascade'
      AND LOWER(COALESCE(l.column_meta ->> 'relationRole', '')) IN ('host', 'member')
      AND TRIM(l.column_meta ->> 'categoryTypeCode') <> ''
),
hosts AS (
    SELECT
        *,
        ROW_NUMBER() OVER (
            PARTITION BY tenant_id, layout_id
            ORDER BY column_order, id
        ) AS pair_rn
    FROM cascade_rows
    WHERE relation_role = 'host'
),
members AS (
    SELECT
        *,
        ROW_NUMBER() OVER (
            PARTITION BY tenant_id, layout_id
            ORDER BY column_order, id
        ) AS pair_rn
    FROM cascade_rows
    WHERE relation_role = 'member'
),
pairs AS (
    SELECT
        h.tenant_id,
        h.layout_id,
        h.entity_type_code,
        h.column_key AS from_column_key,
        m.column_key AS to_column_key,
        'CATEGORY:' || h.column_key || ':' || h.tab_id AS from_column_identity,
        'CATEGORY:' || m.column_key || ':' || m.tab_id AS to_column_identity,
        h.category_type_code AS from_type_code,
        m.category_type_code AS to_type_code
    FROM hosts h
    INNER JOIN members m
        ON h.tenant_id = m.tenant_id
       AND h.layout_id = m.layout_id
       AND h.pair_rn = m.pair_rn
    WHERE h.category_type_code <> m.category_type_code
),
filter_inserts AS (
    INSERT INTO dynamicbusiness.dm_data_tab_column_relation (
        tenant_id,
        layout_id,
        entity_type_code,
        edge_id,
        from_column_identity,
        to_column_identity,
        relation_kind,
        from_type_code,
        to_type_code,
        relation_meta,
        creator,
        deleted
    )
    SELECT
        p.tenant_id,
        p.layout_id,
        p.entity_type_code,
        'cc-filter-' || p.from_column_key || '-' || p.to_column_key,
        p.from_column_identity,
        p.to_column_identity,
        'CATEGORY_CATEGORY',
        p.from_type_code,
        p.to_type_code,
        jsonb_build_object('edgeRole', 'filter', 'enabledInteractions', '[]'::jsonb),
        'V72',
        FALSE
    FROM pairs p
    WHERE NOT EXISTS (
        SELECT 1
        FROM dynamicbusiness.dm_data_tab_column_relation r
        WHERE r.deleted = FALSE
          AND r.layout_id = p.layout_id
          AND r.relation_kind = 'CATEGORY_CATEGORY'
          AND r.from_column_identity = p.from_column_identity
          AND r.to_column_identity = p.to_column_identity
          AND COALESCE(r.relation_meta ->> 'edgeRole', 'filter') = 'filter'
    )
    RETURNING id
),
write_inserts AS (
    INSERT INTO dynamicbusiness.dm_data_tab_column_relation (
        tenant_id,
        layout_id,
        entity_type_code,
        edge_id,
        from_column_identity,
        to_column_identity,
        relation_kind,
        from_type_code,
        to_type_code,
        relation_meta,
        creator,
        deleted
    )
    SELECT
        p.tenant_id,
        p.layout_id,
        p.entity_type_code,
        'cc-write-' || p.from_column_key || '-' || p.to_column_key,
        p.from_column_identity,
        p.to_column_identity,
        'CATEGORY_CATEGORY',
        p.from_type_code,
        p.to_type_code,
        jsonb_build_object(
            'edgeRole', 'write',
            'enabledInteractions', jsonb_build_array(
                'dragAssociate', 'unbindChecked', 'cascadeClip'
            )
        ),
        'V72',
        FALSE
    FROM pairs p
    WHERE NOT EXISTS (
        SELECT 1
        FROM dynamicbusiness.dm_data_tab_column_relation r
        WHERE r.deleted = FALSE
          AND r.layout_id = p.layout_id
          AND r.relation_kind = 'CATEGORY_CATEGORY'
          AND r.from_column_identity = p.from_column_identity
          AND r.to_column_identity = p.to_column_identity
          AND COALESCE(r.relation_meta ->> 'edgeRole', 'filter') = 'write'
    )
    RETURNING id
)
SELECT
    (SELECT COUNT(*) FROM filter_inserts) AS filter_edges_inserted,
    (SELECT COUNT(*) FROM write_inserts) AS write_edges_inserted;

-- 2) 清 layout 旧级联标记（保留其余 column_meta 字段）
UPDATE dynamicbusiness.dm_data_tab_layout l
SET column_meta = (l.column_meta - 'relationRole')
    || jsonb_build_object('relationMode', 'intersection'),
    updater = 'V72',
    update_time = CURRENT_TIMESTAMP
WHERE l.deleted = FALSE
  AND UPPER(TRIM(l.column_kind)) = 'CATEGORY'
  AND LOWER(COALESCE(l.column_meta ->> 'relationMode', '')) = 'cascade'
  AND LOWER(COALESCE(l.column_meta ->> 'relationRole', '')) IN ('host', 'member');
