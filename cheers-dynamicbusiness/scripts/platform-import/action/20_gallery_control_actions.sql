-- ============================================================================
-- action · 20 管廊可控制设备的共用动作
-- 负责：智慧管廊租户的动作库里写入 8 条设备动作。
-- 水泵、照明、风机、井盖各有打开和关闭。同一条给该型号全部设备共用。
-- 不负责：不建数据目录。目录由创建接口写入租户 123、分组「智慧管网」。
-- 不负责：不写执行手段，不写指令对照，不按每一台设备复制，不写节点标识。
-- 每条动作在 attrs.industrialEquipment 里写明管哪个设备型号、写成哪个字段、写成 1 还是 0。
-- 数据页已有实体栏和详情栏时，补一条「点中动作，详情栏跟上」的跟随线。
-- 智慧巡检租户里若已有同编码的这 8 条，标为删除，避免两本库各有一份。
-- 管廊动作库还没建立时跳过，不打断平台动作库种子。
-- ============================================================================

SET search_path TO dynamicbusiness;

SELECT to_regclass('dynamicbusiness.ent_action_t123') IS NOT NULL AS gallery_action_ready \gset

\if :gallery_action_ready

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, creator, deleted, tenant_id,
  library_field_id, is_searchable, is_filterable, is_sortable
)
SELECT
  src.entity_type_code,
  src.field_code,
  src.field_name,
  src.data_type,
  src.required,
  src.default_value,
  src.description,
  src.type_config,
  src.sort_order,
  src.status,
  'seed',
  false,
  123,
  COALESCE(local_field.id, src.library_field_id),
  src.is_searchable,
  src.is_filterable,
  src.is_sortable
FROM dynamic_entity_type_base_field src
LEFT JOIN dynamic_field local_field
  ON local_field.deleted = false
 AND local_field.tenant_id = 123
 AND local_field.code = src.field_code
WHERE src.deleted = false
  AND src.tenant_id = 1
  AND src.entity_type_code = 'action'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_entity_type_base_field existing
    WHERE existing.deleted = false
      AND existing.tenant_id = 123
      AND existing.entity_type_code = 'action'
      AND existing.field_code = src.field_code
  );

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator, governance_status
)
SELECT
  'action_device',
  '设备动作',
  'action',
  '管廊控制动作。打开和关闭由这一型号的实体表示。',
  1,
  0,
  123,
  'seed',
  'COMPANY'
WHERE NOT EXISTS (
  SELECT 1 FROM dynamic_model model
  WHERE model.deleted = false
    AND model.tenant_id = 123
    AND model.code = 'action_device'
);

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, model_code, field_code, required,
  is_searchable, is_filterable, is_sortable, default_value,
  sort, field_source, tenant_id, creator
)
SELECT
  model.id,
  field.id,
  model.code,
  spec.field_code,
  false,
  false,
  false,
  false,
  spec.default_value,
  spec.sort,
  'BASE',
  123,
  'seed'
FROM (
  VALUES
    ('action_domain', 'DEVICE', 5),
    ('execution_means', NULL, 10),
    ('param_slots_json', NULL, 20),
    ('action_tree_json', '[]', 930)
) AS spec(field_code, default_value, sort)
JOIN dynamic_model model
  ON model.deleted = false
 AND model.tenant_id = 123
 AND model.code = 'action_device'
JOIN dynamic_field field
  ON field.deleted = false
 AND field.tenant_id = 123
 AND field.code = spec.field_code
ON CONFLICT (tenant_id, model_code, field_code) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  default_value = EXCLUDED.default_value,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO ent_action_t123 (
  tenant_id, entity_type_code, model_id, name, code, status,
  execution_means, param_slots_json, child_action_ids_json, is_composite,
  action_domain, creator, deleted
)
SELECT
  123,
  'action',
  model.id,
  v.name,
  v.code,
  1,
  '[]'::jsonb,
  '{"version":2,"methods":[]}'::jsonb,
  '[]'::jsonb,
  false,
  'DEVICE',
  'seed',
  false
FROM (
  VALUES
    ('act-pump-on', '打开水泵'),
    ('act-pump-off', '关闭水泵'),
    ('act-lighting-on', '打开照明'),
    ('act-lighting-off', '关闭照明'),
    ('act-fan-on', '打开风机'),
    ('act-fan-off', '关闭风机'),
    ('act-cover-on', '打开井盖'),
    ('act-cover-off', '关闭井盖')
) AS v(code, name)
JOIN dynamic_model model
  ON model.deleted = false
 AND model.tenant_id = 123
 AND model.code = 'action_device'
WHERE NOT EXISTS (
  SELECT 1 FROM ent_action_t123 action
  WHERE action.deleted = false
    AND action.tenant_id = 123
    AND action.code = v.code
);

UPDATE ent_action_t1 action
SET deleted = true,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE action.deleted = false
  AND action.tenant_id = 1
  AND action.code IN (
    'act-pump-on', 'act-pump-off',
    'act-lighting-on', 'act-lighting-off',
    'act-fan-on', 'act-fan-off',
    'act-cover-on', 'act-cover-off'
  );

DO $$
DECLARE
  found_count integer;
BEGIN
  SELECT count(*) INTO found_count
  FROM ent_action_t123 action
  JOIN dynamic_model model ON model.id = action.model_id AND model.deleted = false
  WHERE action.deleted = false
    AND action.tenant_id = 123
    AND model.code = 'action_device'
    AND action.action_domain = 'DEVICE'
    AND action.execution_means = '[]'::jsonb
    AND (action.code, action.name) IN (
      ('act-pump-on', '打开水泵'),
      ('act-pump-off', '关闭水泵'),
      ('act-lighting-on', '打开照明'),
      ('act-lighting-off', '关闭照明'),
      ('act-fan-on', '打开风机'),
      ('act-fan-off', '关闭风机'),
      ('act-cover-on', '打开井盖'),
      ('act-cover-off', '关闭井盖')
    );
  IF found_count <> 8 THEN
    RAISE EXCEPTION '管廊控制动作未按约定写入，当前 % 条', found_count;
  END IF;
END $$;

-- 点中动作后，右侧详情栏跟上这一条。没有这条线时，详情栏一直停在空提示。
INSERT INTO dm_data_tab_column_relation (
  entity_type_code, edge_id, from_column_identity, to_column_identity,
  relation_kind, from_type_code, to_type_code, relation_meta,
  tenant_id, layout_id, deleted, creator
)
SELECT
  'action',
  'edge-action-detail-' || catalog.data_layout_id,
  'ENTITY:' || entity_col.tab_id,
  'DETAIL:' || detail_col.tab_id,
  'ENTITY_DETAIL',
  'action',
  'action',
  '{"edgeAction":"detail_follow","enabledInteractions":[]}'::jsonb,
  123,
  catalog.data_layout_id,
  false,
  'seed'
FROM dynamic_entity_type catalog
JOIN dm_data_tab_layout entity_col
  ON entity_col.deleted = false
 AND entity_col.tenant_id = catalog.tenant_id
 AND entity_col.layout_id = catalog.data_layout_id
 AND entity_col.column_kind = 'ENTITY'
JOIN dm_data_tab_layout detail_col
  ON detail_col.deleted = false
 AND detail_col.tenant_id = catalog.tenant_id
 AND detail_col.layout_id = catalog.data_layout_id
 AND detail_col.column_kind = 'DETAIL'
WHERE catalog.deleted = false
  AND catalog.tenant_id = 123
  AND catalog.code = 'action'
  AND catalog.data_layout_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1
    FROM dm_data_tab_column_relation rel
    WHERE rel.deleted = false
      AND rel.tenant_id = 123
      AND rel.layout_id = catalog.data_layout_id
      AND rel.relation_kind = 'ENTITY_DETAIL'
      AND rel.relation_meta->>'edgeAction' = 'detail_follow'
  );

-- 动作对上设备型号和写字段。节点标识不写在这里，浏览对上之后才有。
UPDATE ent_action_t123 AS action
SET attrs = COALESCE(action.attrs, '{}'::jsonb) || jsonb_build_object(
      'industrialEquipment',
      jsonb_build_object(
        'equipmentModelCode', model.code,
        'writes', jsonb_build_array(jsonb_build_object('fieldCode', field.code, 'value', spec.value))
      )
    ),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM (VALUES
  ('act-pump-on', '水泵', 'F-ab068b1dd098457cba93ad89cf095f90', 1),
  ('act-pump-off', '水泵', 'F-ab068b1dd098457cba93ad89cf095f90', 0),
  ('act-lighting-on', '开关', 'F-ab068b1dd098457cba93ad89cf095f90', 1),
  ('act-lighting-off', '开关', 'F-ab068b1dd098457cba93ad89cf095f90', 0),
  ('act-fan-on', '风机', 'F-a11c0e0000014e0a8f0e00000000c002', 1),
  ('act-fan-off', '风机', 'F-a11c0e0000014e0a8f0e00000000c004', 1)
) AS spec(action_code, model_name, field_code, value)
JOIN dynamic_model AS model
  ON model.deleted = false AND model.tenant_id = 123 AND model.name = spec.model_name
JOIN dynamic_field AS field
  ON field.deleted = false AND field.tenant_id = 123 AND field.code = spec.field_code
WHERE action.tenant_id = 123
  AND action.deleted = false
  AND action.code = spec.action_code;

UPDATE ent_action_t123 AS action
SET attrs = COALESCE(action.attrs, '{}'::jsonb) || jsonb_build_object(
      'industrialEquipment',
      jsonb_build_object(
        'equipmentModelCode', model.code,
        'writes', jsonb_build_array(
          jsonb_build_object('fieldCode', first_field.code, 'value', spec.first_value),
          jsonb_build_object('fieldCode', second_field.code, 'value', spec.second_value)
        )
      )
    ),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM (VALUES
  ('act-cover-on', '液压井盖', 'F-a11c0e0000014e0a8f0e00000000c003', 1, 'F-a11c0e0000014e0a8f0e00000000c005', 0),
  ('act-cover-off', '液压井盖', 'F-a11c0e0000014e0a8f0e00000000c005', 1, 'F-a11c0e0000014e0a8f0e00000000c003', 0)
) AS spec(action_code, model_name, first_code, first_value, second_code, second_value)
JOIN dynamic_model AS model
  ON model.deleted = false AND model.tenant_id = 123 AND model.name = spec.model_name
JOIN dynamic_field AS first_field
  ON first_field.deleted = false AND first_field.tenant_id = 123 AND first_field.code = spec.first_code
JOIN dynamic_field AS second_field
  ON second_field.deleted = false AND second_field.tenant_id = 123 AND second_field.code = spec.second_code
WHERE action.tenant_id = 123
  AND action.deleted = false
  AND action.code = spec.action_code;

DO $$
DECLARE
  linked_count integer;
BEGIN
  SELECT count(*) INTO linked_count
  FROM ent_action_t123
  WHERE tenant_id = 123
    AND deleted = false
    AND code IN (
      'act-pump-on', 'act-pump-off', 'act-lighting-on', 'act-lighting-off',
      'act-fan-on', 'act-fan-off', 'act-cover-on', 'act-cover-off'
    )
    AND attrs->'industrialEquipment'->'writes' IS NOT NULL
    AND jsonb_array_length(attrs->'industrialEquipment'->'writes') >= 1;
  IF linked_count <> 8 THEN
    RAISE EXCEPTION '管廊控制动作未对上写点，当前 % 条', linked_count;
  END IF;
END $$;

\endif
