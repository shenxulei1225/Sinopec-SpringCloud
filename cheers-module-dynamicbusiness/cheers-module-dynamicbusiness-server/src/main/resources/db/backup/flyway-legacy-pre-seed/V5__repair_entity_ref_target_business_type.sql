-- 所属区域（ENTITY_REF）应关联 region，而非默认当前业务类型 equipment
SET search_path TO dynamicbusiness;

UPDATE dynamic_model_field_assignment mfa
SET target_entity_type = 'region',
    updater = 'migration',
    update_time = NOW()
FROM dynamic_field f
WHERE mfa.field_id = f.id
  AND f.code = 'F-cc746ce0224145af88d5428d0b03213a'
  AND f.type = 'ENTITY_REF'
  AND (mfa.target_entity_type IS NULL OR TRIM(mfa.target_entity_type) = '');
