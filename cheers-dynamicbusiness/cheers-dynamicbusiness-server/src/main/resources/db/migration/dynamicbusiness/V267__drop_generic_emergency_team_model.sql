-- 应急队伍不留通用型号。每段只保留已经点名的队伍种类。

UPDATE dynamicbusiness.ent_emergency_team_t1
SET deleted = true, updater = 'V267', update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1 AND deleted = false AND name LIKE '%通用队伍模型';

UPDATE dynamicbusiness.dynamic_model
SET deleted = true, updater = 'V267', update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1 AND deleted = false AND entity_type_code = 'emergency_team' AND name = '通用队伍模型';

UPDATE dynamicbusiness.dynamic_category
SET deleted = true, updater = 'V267', update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1 AND deleted = false AND category_type_code = 'emergency_team' AND name = '通用队伍模型';

UPDATE dynamicbusiness.dynamic_model_category_relation r
SET deleted = true, updater = 'V267', update_time = CURRENT_TIMESTAMP
WHERE r.deleted = false AND r.tenant_id = 1 AND r.entity_type_code = 'emergency_team'
  AND r.model_id IN (
      SELECT id FROM dynamicbusiness.dynamic_model
      WHERE entity_type_code = 'emergency_team' AND name = '通用队伍模型'
  );

UPDATE dynamicbusiness.dynamic_model_category_relation_t1 r
SET deleted = true, updater = 'V267', update_time = CURRENT_TIMESTAMP
WHERE r.deleted = false AND r.tenant_id = 1 AND r.entity_type_code = 'emergency_team'
  AND r.model_id IN (
      SELECT id FROM dynamicbusiness.dynamic_model
      WHERE entity_type_code = 'emergency_team' AND name = '通用队伍模型'
  );
