-- V53: 标准检查内容库改回普通 NATIVE 三 Tab；数据区双栏级联分类
-- 宿主：检查分类（inspection_item）；成员：设备分类（equipment）；型号列关；详情开

SET search_path TO dynamicbusiness, public;

-- —— 数据 Tab 布局 ——
-- 关掉旧单栏检查分类（若仍存在）
UPDATE dynamicbusiness.dm_data_tab_layout
SET deleted = TRUE,
    updater = 'V53',
    update_time = CURRENT_TIMESTAMP
WHERE entity_type_code = 'inspection_item'
  AND column_kind = 'CATEGORY'
  AND deleted = FALSE;

-- 型号列保持关闭
UPDATE dynamicbusiness.dm_data_tab_layout
SET enabled = FALSE,
    updater = 'V53',
    update_time = CURRENT_TIMESTAMP
WHERE entity_type_code = 'inspection_item'
  AND column_kind = 'MODEL'
  AND deleted = FALSE;

-- 实体列开启
UPDATE dynamicbusiness.dm_data_tab_layout
SET enabled = TRUE,
    updater = 'V53',
    update_time = CURRENT_TIMESTAMP
WHERE entity_type_code = 'inspection_item'
  AND column_kind = 'ENTITY'
  AND deleted = FALSE;

-- 详情列开启
UPDATE dynamicbusiness.dm_data_tab_layout
SET enabled = TRUE,
    deleted = FALSE,
    updater = 'V53',
    update_time = CURRENT_TIMESTAMP
WHERE entity_type_code = 'inspection_item'
  AND column_kind = 'DETAIL';

-- 宿主：检查分类
INSERT INTO dynamicbusiness.dm_data_tab_layout (
    tenant_id,
    entity_type_code,
    column_kind,
    perspective_id,
    props_id,
    enabled,
    category_column,
    creator,
    deleted
)
SELECT
    1,
    'inspection_item',
    'CATEGORY',
    'inspection_item-host',
    NULL,
    TRUE,
    '{"label": "检查分类", "columnKey": "col-insp-host", "columnOrder": 0, "relationMode": "cascade", "relationRole": "host", "categoryTypeCode": "inspection_item"}'::jsonb,
    'V53',
    FALSE
WHERE NOT EXISTS (
    SELECT 1
    FROM dynamicbusiness.dm_data_tab_layout
    WHERE tenant_id = 1
      AND entity_type_code = 'inspection_item'
      AND column_kind = 'CATEGORY'
      AND COALESCE(perspective_id, '') = 'inspection_item-host'
      AND deleted = FALSE
);

-- 成员：设备分类
INSERT INTO dynamicbusiness.dm_data_tab_layout (
    tenant_id,
    entity_type_code,
    column_kind,
    perspective_id,
    props_id,
    enabled,
    category_column,
    creator,
    deleted
)
SELECT
    1,
    'inspection_item',
    'CATEGORY',
    'inspection_item-equipment-member',
    NULL,
    TRUE,
    '{"label": "设备分类", "columnKey": "col-equip-member", "columnOrder": 1, "relationMode": "cascade", "relationRole": "member", "categoryTypeCode": "equipment"}'::jsonb,
    'V53',
    FALSE
WHERE NOT EXISTS (
    SELECT 1
    FROM dynamicbusiness.dm_data_tab_layout
    WHERE tenant_id = 1
      AND entity_type_code = 'inspection_item'
      AND column_kind = 'CATEGORY'
      AND COALESCE(perspective_id, '') = 'inspection_item-equipment-member'
      AND deleted = FALSE
);

-- 若 DETAIL 行不存在则补一条
INSERT INTO dynamicbusiness.dm_data_tab_layout (
    tenant_id,
    entity_type_code,
    column_kind,
    perspective_id,
    props_id,
    enabled,
    category_column,
    creator,
    deleted
)
SELECT
    1,
    'inspection_item',
    'DETAIL',
    NULL,
    NULL,
    TRUE,
    NULL,
    'V53',
    FALSE
WHERE NOT EXISTS (
    SELECT 1
    FROM dynamicbusiness.dm_data_tab_layout
    WHERE tenant_id = 1
      AND entity_type_code = 'inspection_item'
      AND column_kind = 'DETAIL'
      AND COALESCE(perspective_id, '') = ''
      AND deleted = FALSE
);

-- —— 五维 Who 槽位与布局对齐 ——
UPDATE dynamicbusiness.dm_five_w_who_layout
SET deleted = TRUE,
    updater = 'V53',
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
    'inspection_item-host',
    'inspection_item-host',
    NULL,
    TRUE,
    NULL,
    '{"label": "检查分类", "columnKey": "col-insp-host", "columnOrder": 0, "relationMode": "cascade", "relationRole": "host", "categoryTypeCode": "inspection_item"}'::jsonb,
    'V53',
    1,
    FALSE
WHERE NOT EXISTS (
    SELECT 1
    FROM dynamicbusiness.dm_five_w_who_layout
    WHERE tenant_id = 1
      AND entity_type_code = 'inspection_item'
      AND column_kind = 'CATEGORY'
      AND COALESCE(slot_ref, '') = 'inspection_item-host'
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
    'CATEGORY',
    'inspection_item-equipment-member',
    'inspection_item-equipment-member',
    NULL,
    TRUE,
    NULL,
    '{"label": "设备分类", "columnKey": "col-equip-member", "columnOrder": 1, "relationMode": "cascade", "relationRole": "member", "categoryTypeCode": "equipment"}'::jsonb,
    'V53',
    1,
    FALSE
WHERE NOT EXISTS (
    SELECT 1
    FROM dynamicbusiness.dm_five_w_who_layout
    WHERE tenant_id = 1
      AND entity_type_code = 'inspection_item'
      AND column_kind = 'CATEGORY'
      AND COALESCE(slot_ref, '') = 'inspection_item-equipment-member'
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
    9564,
    TRUE,
    'rowSelection',
    NULL,
    'V53',
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

-- 编排保持实体层 + 看详情（V52）；此处仅注释对齐产品口径
UPDATE dynamicbusiness.dm_five_w_orchestration
SET selection_level = 'ENTITY',
    what_mode = 'VIEW_DETAIL',
    what_config = '{"bindLayer": "ENTITY"}'::jsonb,
    how_mode = 'NONE',
    how_config = '{}'::jsonb,
    updater = 'V53',
    update_time = CURRENT_TIMESTAMP
WHERE entity_type_code = 'inspection_item'
  AND deleted = FALSE;
