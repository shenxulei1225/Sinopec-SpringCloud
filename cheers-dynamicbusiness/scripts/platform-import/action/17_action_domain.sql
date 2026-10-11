-- ============================================================================
-- action · 17 动作模型的执行归属投影
-- 字段库 action_domain 定义可选值；每个动作模型在字段分配上配置自己的默认值。
-- 动作实体只保存模型派生的投影。禁止按动作标题、编码或适用检查方式猜测。
-- ============================================================================

SET search_path TO dynamicbusiness;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, model_code, field_code, required,
  is_searchable, is_filterable, is_sortable, default_value,
  sort, field_source, tenant_id, creator
)
SELECT
  model.id,
  field.id,
  model.code,
  field.code,
  false,
  false,
  false,
  false,
  config.domain,
  5,
  'BASE',
  1,
  'seed'
FROM (
  VALUES
    ('action_device', 'DEVICE'),
    ('action_intelligent_analysis', 'INTELLIGENT_ANALYSIS'),
    ('action_manual', 'MANUAL'),
    ('action_platform', 'PLATFORM')
) AS config(model_code, domain)
JOIN dynamic_model model
  ON model.tenant_id = 1
 AND model.entity_type_code = 'action'
 AND model.code = config.model_code
 AND model.deleted = false
JOIN dynamic_field field
  ON field.tenant_id = model.tenant_id
 AND field.code = 'action_domain'
 AND field.deleted = false
ON CONFLICT (tenant_id, model_code, field_code) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  default_value = EXCLUDED.default_value,
  required = false,
  field_source = 'BASE',
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

-- 只按动作选择的模型写投影。模型没配置动作类型时保持缺口，交由保存校验阻断。
UPDATE ent_action_t1 action
SET action_domain = assignment.default_value,
    analysis_capability_id = CASE
      WHEN assignment.default_value = 'INTELLIGENT_ANALYSIS' THEN action.analysis_capability_id
      ELSE NULL
    END,
    custom_fields = COALESCE(action.custom_fields, '{}'::jsonb)
      - 'action_domain'
      - 'recognition_capability_code'
      - 'protocol_dispatch_role'
      - 'protocol_dispatch_role_by_version',
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM dynamic_model model
JOIN dynamic_model_field_assignment assignment
  ON assignment.model_id = model.id
 AND assignment.field_code = 'action_domain'
 AND assignment.deleted = false
WHERE action.model_id = model.id
  AND action.deleted = false
  AND action.tenant_id = 1
  AND model.tenant_id = action.tenant_id
  AND model.entity_type_code = 'action'
  AND model.deleted = false;

-- 工作区 detection 官方资料只明确证明这条动作对应 LIQUID_LEAKAGE。
UPDATE ent_action_t1 action
SET analysis_capability_id = capability.id,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM intelligent_analysis_capability capability
WHERE action.deleted = false
  AND action.tenant_id = 1
  AND action.code = 'action-fa4e3366ba554a73a7cd50234aaf78d8'
  AND capability.tenant_id = action.tenant_id
  AND capability.code = 'LIQUID_LEAKAGE'
  AND capability.deleted = false;

-- 其它智能分析动作的能力由管理员从能力目录明确选择。本脚本不按名称补编号。
