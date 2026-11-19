-- 没有队员的应急队伍，规模留空。
-- 规模只等于队员人数。金桥厂区原先写在医疗救护组、气体防护组上的人数，没有对应的人，清掉。

UPDATE dynamicbusiness.ent_emergency_resource_t1 team
SET total_quantity = NULL,
    available_quantity = NULL,
    updater = 'V259',
    update_time = CURRENT_TIMESTAMP
WHERE team.tenant_id = 1
  AND team.deleted = false
  AND team.entity_type_code = 'emergency_resource'
  AND NOT (coalesce(team.custom_fields, '{}'::jsonb) ? 'team_member_ids')
  AND (team.total_quantity IS NOT NULL OR team.available_quantity IS NOT NULL)
  AND EXISTS (
      SELECT 1
      FROM dynamicbusiness.dynamic_model_category_relation_t1 mr
      JOIN dynamicbusiness.dynamic_category c ON c.id = mr.category_id
      WHERE mr.model_id = team.model_id
        AND mr.deleted = false
        AND c.deleted = false
        AND c.name = '应急队伍'
  );
