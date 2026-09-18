-- ============================================================================
-- action · 12 纠偏：只用规范型号 action；参数不是实体 CRUD 字段
--
-- 现象：新建弹出「执行手段」「到达位置」——不是前端写死表单，而是：
--   1) 规范型号 action 被停用 (status=0)，新建落到历史 action_param_*（LIBRARY 挂了 location_ref）
--   2) execution_means 曾以可见字段投影进新建弹窗
--
-- 定稿：动作参数只在 param_slots_json（详情「配置参数」）。
-- 适用手段仍是隐藏基础字段 execution_means（详情勾选，createVisible/editVisible/detailVisible=false）。
-- 本脚本幂等。
-- ============================================================================

SET search_path TO dynamicbusiness;

-- 1) 启用唯一规范型号；停用全部历史「参数型号」
UPDATE dynamic_model
SET
  status = 1,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND tenant_id = 1
  AND entity_type_code = 'action'
  AND code = 'action';

UPDATE dynamic_model
SET
  status = 0,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND tenant_id = 1
  AND entity_type_code = 'action'
  AND code LIKE 'action_param_%';

-- 2) 软删参数型号上的 LIBRARY 分配（到达位置等不再进任何 CRUD 投影）
UPDATE dynamic_model_field_assignment
SET
  deleted = true,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND tenant_id = 1
  AND model_code LIKE 'action_param_%';

-- 3) 适用手段留在隐藏基础字段（详情勾选，不进新建弹窗），不再软删
-- 4) 结构字段保留（写路径 / 专用面板），并补齐默认值 + 显式 createVisible=false
UPDATE dynamic_entity_type_base_field
SET
  field_name = CASE field_code
    WHEN 'param_slots_json' THEN '动作参数定义'
    ELSE field_name
  END,
  default_value = CASE field_code
    WHEN 'param_slots_json' THEN COALESCE(NULLIF(TRIM(default_value), ''), '{"version":1,"fields":[]}')
    WHEN 'child_action_ids_json' THEN COALESCE(NULLIF(TRIM(default_value), ''), '[]')
    WHEN 'is_composite' THEN COALESCE(NULLIF(TRIM(default_value), ''), 'false')
    ELSE default_value
  END,
  type_config = '{"createVisible":false}',
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND tenant_id = 1
  AND entity_type_code = 'action'
  AND field_code IN ('param_slots_json', 'child_action_ids_json', 'is_composite');

UPDATE dynamic_model_field_assignment a
SET
  default_value = bf.default_value,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
FROM dynamic_entity_type_base_field bf
WHERE a.deleted = false
  AND a.tenant_id = 1
  AND a.model_code = 'action'
  AND bf.deleted = false
  AND bf.tenant_id = 1
  AND bf.entity_type_code = 'action'
  AND bf.field_code = a.field_code
  AND a.field_code IN ('param_slots_json', 'child_action_ids_json', 'is_composite');

-- 5) 清 CRUD 表单缓存，下次请求按型号重算投影
DELETE FROM model_crud_form_definition
WHERE entity_type_code = 'action';
