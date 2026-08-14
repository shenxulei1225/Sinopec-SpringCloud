-- V52: 标准检查内容库 inspection_item 恢复为纯标准库编排
-- Who：本类分类 + 本类实体；What：看详情；How：无
-- 型号适用项分配另开入口（不在本目录页）

SET search_path TO dynamicbusiness, public;

UPDATE dynamicbusiness.dm_five_w_orchestration
SET selection_level = 'ENTITY',
    what_mode = 'VIEW_DETAIL',
    what_config = '{"bindLayer": "ENTITY"}'::jsonb,
    how_mode = 'NONE',
    how_config = '{}'::jsonb,
    updater = 'V52',
    update_time = CURRENT_TIMESTAMP
WHERE entity_type_code = 'inspection_item'
  AND deleted = FALSE;

-- 替换 Who 槽位：去掉设备分类/设备型号
UPDATE dynamicbusiness.dm_five_w_who_layout
SET deleted = TRUE,
    updater = 'V52',
    update_time = CURRENT_TIMESTAMP
WHERE entity_type_code = 'inspection_item'
  AND deleted = FALSE;

INSERT INTO dynamicbusiness.dm_five_w_who_layout (
    entity_type_code,
    column_kind,
    slot_ref,
    perspective_id,
    props_id,
    enabled,
    entity_id_rule,
    category_column,
    creator,
    tenant_id,
    deleted
)
SELECT
    'inspection_item',
    'CATEGORY',
    'inspection_item-category',
    NULL,
    NULL,
    TRUE,
    NULL,
    '{"label": "检查内容分类", "categoryTypeCode": "inspection_item"}'::jsonb,
    'V52',
    1,
    FALSE
WHERE NOT EXISTS (
    SELECT 1
    FROM dynamicbusiness.dm_five_w_who_layout
    WHERE tenant_id = 1
      AND entity_type_code = 'inspection_item'
      AND column_kind = 'CATEGORY'
      AND COALESCE(slot_ref, '') = 'inspection_item-category'
      AND deleted = FALSE
);

INSERT INTO dynamicbusiness.dm_five_w_who_layout (
    entity_type_code,
    column_kind,
    slot_ref,
    perspective_id,
    props_id,
    enabled,
    entity_id_rule,
    category_column,
    creator,
    tenant_id,
    deleted
)
SELECT
    'inspection_item',
    'ENTITY',
    'inspection_item-entity',
    NULL,
    8830,
    TRUE,
    'rowSelection',
    NULL,
    'V52',
    1,
    FALSE
WHERE NOT EXISTS (
    SELECT 1
    FROM dynamicbusiness.dm_five_w_who_layout
    WHERE tenant_id = 1
      AND entity_type_code = 'inspection_item'
      AND column_kind = 'ENTITY'
      AND COALESCE(slot_ref, '') = 'inspection_item-entity'
      AND deleted = FALSE
);
