-- 数据标签页布局按场景分份；分类栏补齐栏位键（columnKey），去掉读路径猜测。

SET search_path TO dynamicbusiness;

ALTER TABLE dynamicbusiness.dm_data_tab_layout
    ADD COLUMN IF NOT EXISTS layout_scene varchar(32) NOT NULL DEFAULT 'DATA_TAB';

COMMENT ON COLUMN dynamicbusiness.dm_data_tab_layout.layout_scene IS
    '布局场景：DATA_TAB=数据管理数据 Tab；INSPECTION_PICKER=任务创建/巡检勾选';

UPDATE dynamicbusiness.dm_data_tab_layout
SET layout_scene = 'DATA_TAB'
WHERE layout_scene IS NULL OR btrim(layout_scene) = '';

DROP INDEX IF EXISTS dynamicbusiness.uk_dm_data_tab_layout_scope;

CREATE UNIQUE INDEX IF NOT EXISTS uk_dm_data_tab_layout_scope
    ON dynamicbusiness.dm_data_tab_layout (
        tenant_id,
        entity_type_code,
        layout_scene,
        column_kind,
        COALESCE(perspective_id, '')
    )
    WHERE deleted = false;

-- 分类行没有栏位键时：同一数据类型+场景下无键的分类行共用一个栏位键（取最小视角 id），
-- 保持「一栏多 Tab」，不要把每个视角拆成单独一栏。
UPDATE dynamicbusiness.dm_data_tab_layout AS t
SET category_column = jsonb_set(
        CASE
            WHEN t.category_column IS NOT NULL AND jsonb_typeof(t.category_column) = 'object'
                THEN t.category_column
            ELSE '{}'::jsonb
        END,
        '{columnKey}',
        to_jsonb(k.column_key),
        true
    )
FROM (
    SELECT
        tenant_id,
        entity_type_code,
        layout_scene,
        min(perspective_id) AS column_key
    FROM dynamicbusiness.dm_data_tab_layout
    WHERE deleted = false
      AND column_kind = 'CATEGORY'
      AND perspective_id IS NOT NULL
      AND btrim(perspective_id) <> ''
      AND (
          category_column IS NULL
          OR jsonb_typeof(category_column) <> 'object'
          OR COALESCE(btrim(category_column->>'columnKey'), '') = ''
      )
    GROUP BY tenant_id, entity_type_code, layout_scene
) AS k
WHERE t.deleted = false
  AND t.column_kind = 'CATEGORY'
  AND t.tenant_id = k.tenant_id
  AND t.entity_type_code = k.entity_type_code
  AND t.layout_scene = k.layout_scene
  AND t.perspective_id IS NOT NULL
  AND btrim(t.perspective_id) <> ''
  AND (
      t.category_column IS NULL
      OR jsonb_typeof(t.category_column) <> 'object'
      OR COALESCE(btrim(t.category_column->>'columnKey'), '') = ''
  );
