-- ============================================================================
-- action · 18 本地联调仪表读数能力
-- 只用于验证巡检任务的智能分析提交与结果交付链。
-- 不调用跑冒滴漏算法，不作为生产服务接入。正式算法接入后替换 provider 绑定。
-- ============================================================================

SET search_path TO dynamicbusiness;

INSERT INTO intelligent_analysis_provider (
  tenant_id, code, name, adapter_type, config_json, enabled, creator
) VALUES (
  1,
  'LOCAL_METER_READING_SIMULATION',
  '本地仪表读数模拟服务',
  'SIMULATED_METER_READING',
  '{"demoOnly":true,"simulatedValue":42.0,"unit":""}'::jsonb,
  true,
  'seed'
)
ON CONFLICT (tenant_id, code) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  adapter_type = EXCLUDED.adapter_type,
  base_url = NULL,
  credential_ref = NULL,
  config_json = EXCLUDED.config_json,
  enabled = true,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO intelligent_analysis_capability (
  tenant_id, code, name, description, provider_id, provider_capability_code,
  capability_version, supported_media, parameter_schema_json, result_schema_json,
  enabled, creator
)
SELECT
  1,
  'METER_READING',
  '仪表读数识别',
  '本地联调能力。返回明确标记为模拟的仪表读数，不调用生产算法',
  provider.id,
  'SIMULATED_METER_READING_V1',
  'demo-1',
  '["IMAGE","VIDEO"]'::jsonb,
  '{"type":"object","properties":{}}'::jsonb,
  '{"type":"object","properties":{"verdict":{"enum":["NORMAL","ALARM","FAILED"]},"message":{"type":"string"},"readings":{"type":"array"},"simulated":{"const":true}}}'::jsonb,
  true,
  'seed'
FROM intelligent_analysis_provider provider
WHERE provider.tenant_id = 1
  AND provider.code = 'LOCAL_METER_READING_SIMULATION'
  AND provider.deleted = false
ON CONFLICT (tenant_id, code) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  provider_id = EXCLUDED.provider_id,
  provider_capability_code = EXCLUDED.provider_capability_code,
  capability_version = EXCLUDED.capability_version,
  supported_media = EXCLUDED.supported_media,
  parameter_schema_json = EXCLUDED.parameter_schema_json,
  result_schema_json = EXCLUDED.result_schema_json,
  enabled = true,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

-- 仪表读数的开启与关闭步骤属于同一分析能力。绑定只按已核对的稳定编码完成。
UPDATE ent_action_t1 action
SET analysis_capability_id = capability.id,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM intelligent_analysis_capability capability
WHERE action.tenant_id = 1
  AND action.code IN (
    'action-990b11dac60e489d89813bae8c8c80da',
    'action-59d4ad5dff49463592f5b79227213a82'
  )
  AND action.action_domain = 'INTELLIGENT_ANALYSIS'
  AND action.deleted = false
  AND capability.tenant_id = action.tenant_id
  AND capability.code = 'METER_READING'
  AND capability.deleted = false;
