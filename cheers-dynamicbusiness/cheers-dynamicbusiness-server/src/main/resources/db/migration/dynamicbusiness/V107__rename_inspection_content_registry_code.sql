-- 检查项管理口径清理（不改目录注册码）：
-- - 保留 REUSE 目录注册码 inspection-content；
-- - 底座 inspection_item 的分类/型号/实体显示名统一成“检查项”语义；
-- - 关系图不再出现“检查内容”标签。

SET search_path TO dynamicbusiness, public;

-- 1) 权威名称统一：底座与分类种类
UPDATE dynamicbusiness.dynamic_entity_type
SET name = '检查项',
    updater = 'flyway-v107',
    update_time = CURRENT_TIMESTAMP
WHERE code = 'inspection_item'
  AND deleted = FALSE
  AND COALESCE(name, '') <> '检查项';

UPDATE dynamicbusiness.dynamic_entity_type
SET name = '检查项管理',
    updater = 'flyway-v107',
    update_time = CURRENT_TIMESTAMP
WHERE code = 'inspection-content'
  AND deleted = FALSE
  AND COALESCE(name, '') <> '检查项管理';

UPDATE dynamicbusiness.dynamic_category_type
SET name = '检查分类',
    updater = 'flyway-v107',
    update_time = CURRENT_TIMESTAMP
WHERE category_type_code = 'inspection_item'
  AND deleted = FALSE
  AND COALESCE(name, '') <> '检查分类';

-- 2) 回填检查项管理目录的布局标签与类型码（只改对应目录 layout，不动目录注册码）
WITH target_layouts AS (
    SELECT tenant_id, data_layout_id AS layout_id
    FROM dynamicbusiness.dynamic_entity_type
    WHERE code = 'inspection-content'
      AND deleted = FALSE
      AND data_layout_id IS NOT NULL
    UNION
    SELECT tenant_id, model_layout_id AS layout_id
    FROM dynamicbusiness.dynamic_entity_type
    WHERE code = 'inspection-content'
      AND deleted = FALSE
      AND model_layout_id IS NOT NULL
),
category_rows AS (
    UPDATE dynamicbusiness.dm_data_tab_layout t
    SET column_meta = jsonb_set(
            jsonb_set(
                COALESCE(t.column_meta, '{}'::jsonb),
                '{categoryTypeCode}',
                to_jsonb('inspection_item'::text),
                TRUE
            ),
            '{label}',
            to_jsonb('检查分类'::text),
            TRUE
        ),
        tab_id = CASE
            WHEN t.tab_id IS NULL OR btrim(t.tab_id) = '' OR lower(btrim(t.tab_id)) = 'default'
                THEN 'inspection_item-1'
            ELSE t.tab_id
        END,
        updater = 'flyway-v107',
        update_time = CURRENT_TIMESTAMP
    FROM target_layouts l
    WHERE t.layout_id = l.layout_id
      AND t.tenant_id = l.tenant_id
      AND t.deleted = FALSE
      AND upper(COALESCE(t.column_kind, '')) = 'CATEGORY'
      AND (
        COALESCE(t.column_meta ->> 'categoryTypeCode', '') IN ('inspection-content', 'inspection_content', 'inspection_item')
        OR COALESCE(t.column_meta ->> 'label', '') LIKE '%检查内容%'
      )
    RETURNING t.id
),
model_rows AS (
    UPDATE dynamicbusiness.dm_data_tab_layout t
    SET column_meta = jsonb_set(
            jsonb_set(
                COALESCE(t.column_meta, '{}'::jsonb),
                '{modelEntityTypeCode}',
                to_jsonb('inspection_item'::text),
                TRUE
            ),
            '{label}',
            to_jsonb('检查项'::text),
            TRUE
        ),
        tab_id = CASE
            WHEN t.tab_id IS NULL OR btrim(t.tab_id) = '' OR lower(btrim(t.tab_id)) = 'default'
                THEN 'inspection_item'
            ELSE t.tab_id
        END,
        updater = 'flyway-v107',
        update_time = CURRENT_TIMESTAMP
    FROM target_layouts l
    WHERE t.layout_id = l.layout_id
      AND t.tenant_id = l.tenant_id
      AND t.deleted = FALSE
      AND upper(COALESCE(t.column_kind, '')) = 'MODEL'
      AND (
        COALESCE(t.column_meta ->> 'modelEntityTypeCode', '') IN ('inspection-content', 'inspection_content', 'inspection_item')
        OR COALESCE(t.column_meta ->> 'label', '') LIKE '%检查内容%'
      )
    RETURNING t.id
),
entity_rows AS (
    UPDATE dynamicbusiness.dm_data_tab_layout t
    SET column_meta = jsonb_set(
            jsonb_set(
                COALESCE(t.column_meta, '{}'::jsonb),
                '{entityEntityTypeCode}',
                to_jsonb('inspection_item'::text),
                TRUE
            ),
            '{label}',
            to_jsonb('检查项管理'::text),
            TRUE
        ),
        tab_id = CASE
            WHEN t.tab_id IS NULL OR btrim(t.tab_id) = '' OR lower(btrim(t.tab_id)) = 'default'
                THEN 'inspection_item'
            ELSE t.tab_id
        END,
        updater = 'flyway-v107',
        update_time = CURRENT_TIMESTAMP
    FROM target_layouts l
    WHERE t.layout_id = l.layout_id
      AND t.tenant_id = l.tenant_id
      AND t.deleted = FALSE
      AND upper(COALESCE(t.column_kind, '')) = 'ENTITY'
      AND (
        COALESCE(t.column_meta ->> 'entityEntityTypeCode', '') IN ('inspection-content', 'inspection_content', 'inspection_item')
        OR COALESCE(t.column_meta ->> 'label', '') LIKE '%检查内容%'
      )
    RETURNING t.id
)
SELECT 1;

-- 3) 栏间关系类型码清理（仅限检查项管理目录对应布局）
WITH target_layouts AS (
    SELECT tenant_id, data_layout_id AS layout_id
    FROM dynamicbusiness.dynamic_entity_type
    WHERE code = 'inspection-content'
      AND deleted = FALSE
      AND data_layout_id IS NOT NULL
    UNION
    SELECT tenant_id, model_layout_id AS layout_id
    FROM dynamicbusiness.dynamic_entity_type
    WHERE code = 'inspection-content'
      AND deleted = FALSE
      AND model_layout_id IS NOT NULL
)
UPDATE dynamicbusiness.dm_data_tab_column_relation r
SET from_type_code = CASE
        WHEN r.from_type_code IN ('inspection-content', 'inspection_content') THEN 'inspection_item'
        ELSE r.from_type_code
    END,
    to_type_code = CASE
        WHEN r.to_type_code IN ('inspection-content', 'inspection_content') THEN 'inspection_item'
        ELSE r.to_type_code
    END,
    updater = 'flyway-v107',
    update_time = CURRENT_TIMESTAMP
FROM target_layouts l
WHERE r.layout_id = l.layout_id
  AND r.tenant_id = l.tenant_id
  AND r.deleted = FALSE
  AND (
    r.from_type_code IN ('inspection-content', 'inspection_content')
    OR r.to_type_code IN ('inspection-content', 'inspection_content')
  );
