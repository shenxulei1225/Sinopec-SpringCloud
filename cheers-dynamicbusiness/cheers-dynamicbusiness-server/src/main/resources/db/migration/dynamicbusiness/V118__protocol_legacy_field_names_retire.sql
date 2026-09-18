-- ============================================================================
-- 协议解析能力：旧字段命名退役（一步到位）
--
-- 目标：
-- 1) 退役旧命名字段（protocol_schema_json 等）在 data_protocol 下的所有激活引用；
-- 2) 保证运行时只会看到新命名字段（protocol_command_schema_json 等）。
-- ============================================================================

SET search_path TO dynamicbusiness;

-- 1) data_protocol 基础字段：旧命名全部禁用并软删
UPDATE dynamic_entity_type_base_field
SET deleted = true,
    status = 0,
    updater = 'flyway',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND tenant_id = 1
  AND entity_type_code = 'data_protocol'
  AND field_code IN (
    'protocol_schema_json',
    'protocol_mapping_rules_json',
    'protocol_step_signal_rules_json',
    'protocol_sample_payload_json'
  );

-- 2) data_protocol 型号字段分配：旧命名全部禁用并软删
UPDATE dynamic_model_field_assignment
SET deleted = true,
    updater = 'flyway',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND tenant_id = 1
  AND model_code LIKE 'data_protocol%'
  AND field_code IN (
    'protocol_schema_json',
    'protocol_mapping_rules_json',
    'protocol_step_signal_rules_json',
    'protocol_sample_payload_json'
  );

-- 3) 字段库：旧命名字段统一退役（避免后续再次被引用）
UPDATE dynamic_field
SET deleted = true,
    status = 0,
    updater = 'flyway',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND tenant_id = 1
  AND code IN (
    'protocol_schema_json',
    'protocol_mapping_rules_json',
    'protocol_step_signal_rules_json',
    'protocol_sample_payload_json'
  );

-- 4) 一致性校验：不允许旧命名仍以激活态挂在 data_protocol
DO $$
BEGIN
  IF EXISTS (
    SELECT 1
    FROM dynamic_entity_type_base_field
    WHERE deleted = false
      AND tenant_id = 1
      AND entity_type_code = 'data_protocol'
      AND field_code IN (
        'protocol_schema_json',
        'protocol_mapping_rules_json',
        'protocol_step_signal_rules_json',
        'protocol_sample_payload_json'
      )
  ) THEN
    RAISE EXCEPTION 'V118 failed: legacy protocol base fields are still active for data_protocol';
  END IF;

  IF EXISTS (
    SELECT 1
    FROM dynamic_model_field_assignment
    WHERE deleted = false
      AND tenant_id = 1
      AND model_code LIKE 'data_protocol%'
      AND field_code IN (
        'protocol_schema_json',
        'protocol_mapping_rules_json',
        'protocol_step_signal_rules_json',
        'protocol_sample_payload_json'
      )
  ) THEN
    RAISE EXCEPTION 'V118 failed: legacy protocol model assignments are still active for data_protocol';
  END IF;
END $$;

