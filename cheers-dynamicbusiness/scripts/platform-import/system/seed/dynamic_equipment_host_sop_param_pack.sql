-- ============================================================================
-- 设备 · 宿主 SOP 参数包基础字段（增量；不删其它设备字段）
-- 前置：Flyway V91（ent_equipment* 已有 host_sop_param_pack 列）；dynamic_field / base_field 表已存在
-- 定稿：docs/动态业务/宿主SOP参数包与动作参数定稿.md
--
-- 字段编码 host_sop_param_pack。显示名是「设备检查参数」，设备类型基础字段。
-- ============================================================================

SET search_path TO dynamicbusiness;

-- 1) 字段库
INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
)
VALUES (
  'host_sop_param_pack',
  '设备检查参数',
  'JSON',
  NULL,
  '每台设备上的检查参数。一条是一个检查项加一种执行方式，步骤上填的数写在这一条里。',
  'BASE',
  1,
  1,
  'NONE',
  NULL,
  NULL,
  'host_sop_param_pack',
  1,
  'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  description = EXCLUDED.description,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

-- 2) 挂到 equipment 基础字段（增量；与既有 FLD-BASE-* 并存）
INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, library_field_id, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, is_searchable, is_filterable, is_sortable,
  tenant_id, creator
)
SELECT
  'equipment',
  f.id,
  'host_sop_param_pack',
  '设备检查参数',
  'JSON',
  false,
  '{"version":1,"entries":[]}',
  '每台设备上的检查参数。一条是一个检查项加一种执行方式，步骤上填的数写在这一条里。',
  '{"createVisible":false,"editVisible":false,"detailVisible":false}'::jsonb,
  200,
  1,
  false,
  false,
  false,
  1,
  'seed'
FROM dynamic_field f
WHERE f.code = 'host_sop_param_pack'
  AND f.deleted = false
  AND f.tenant_id = 1
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  library_field_id = EXCLUDED.library_field_id,
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  default_value = EXCLUDED.default_value,
  description = EXCLUDED.description,
  type_config = EXCLUDED.type_config,
  is_searchable = false,
  is_filterable = false,
  is_sortable = false,
  sort_order = EXCLUDED.sort_order,
  status = EXCLUDED.status,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;
