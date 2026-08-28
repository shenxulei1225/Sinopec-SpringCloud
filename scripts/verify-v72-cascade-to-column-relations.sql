-- V72 验收：layout cascade → 栏间关系 + column_meta 清级联标记
-- 用法：psql ... -f scripts/verify-v72-cascade-to-column-relations.sql

SET search_path TO dynamicbusiness, public;

\echo '--- 1) 仍含 relationMode:cascade 的分类栏（期望 0）---'
SELECT layout_id, entity_type_code, tab_id, column_meta
FROM dm_data_tab_layout
WHERE deleted = FALSE
  AND enabled = TRUE
  AND column_kind = 'CATEGORY'
  AND LOWER(COALESCE(column_meta ->> 'relationMode', '')) = 'cascade';

\echo '--- 2) 含 host/member 仍残留的 relationRole（期望 0）---'
SELECT layout_id, entity_type_code, tab_id, column_meta ->> 'relationRole' AS relation_role
FROM dm_data_tab_layout
WHERE deleted = FALSE
  AND enabled = TRUE
  AND column_kind = 'CATEGORY'
  AND LOWER(COALESCE(column_meta ->> 'relationRole', '')) IN ('host', 'member');

\echo '--- 3) 双分类栏 layout 缺 CATEGORY_CATEGORY filter 边（期望 0 行）---'
WITH cat_layouts AS (
    SELECT layout_id, tenant_id, COUNT(*) AS cat_cols
    FROM dm_data_tab_layout
    WHERE deleted = FALSE AND enabled = TRUE AND column_kind = 'CATEGORY'
    GROUP BY layout_id, tenant_id
    HAVING COUNT(*) >= 2
),
legacy_was_cascade AS (
    SELECT DISTINCT layout_id, tenant_id
    FROM dm_data_tab_layout
    WHERE updater = 'V72'
       OR creator = 'V72'
),
needs_filter AS (
    SELECT c.layout_id, c.tenant_id
    FROM cat_layouts c
    WHERE EXISTS (
        SELECT 1 FROM dm_data_tab_layout l
        WHERE l.layout_id = c.layout_id
          AND l.deleted = FALSE
          AND l.enabled = TRUE
          AND l.column_kind = 'CATEGORY'
          AND l.updater = 'V72'
    )
)
SELECT n.layout_id, n.tenant_id
FROM needs_filter n
WHERE NOT EXISTS (
    SELECT 1
    FROM dm_data_tab_column_relation r
    WHERE r.deleted = FALSE
      AND r.layout_id = n.layout_id
      AND r.relation_kind = 'CATEGORY_CATEGORY'
      AND COALESCE(r.relation_meta ->> 'edgeRole', 'filter') = 'filter'
);

\echo '--- 4) V72 写入的 CATEGORY_CATEGORY 边（审计）---'
SELECT layout_id, edge_id, relation_kind,
       from_column_identity, to_column_identity,
       relation_meta ->> 'edgeRole' AS edge_role,
       creator
FROM dm_data_tab_column_relation
WHERE creator = 'V72' AND deleted = FALSE
ORDER BY layout_id, edge_id;
