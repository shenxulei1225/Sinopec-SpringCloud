-- 应急队伍和场站部门是两份数据。清掉按部门写入的队员。
-- 队员仍是人员编号的多选。同一个人可以同时记在多支队伍里。
-- 没有选定队员时，队员和规模留空。不改队伍名称和所在场站。

UPDATE dynamicbusiness.dynamic_field
SET description = '这支队伍里的人。只记人员编号。同一个人可以同时在多支队伍里。不从场站部门带入。',
    updater = 'V265',
    update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1
  AND deleted = false
  AND code = 'team_member_ids';

UPDATE dynamicbusiness.dynamic_entity_relation_t1
SET deleted = true,
    updater = 'V265',
    update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1
  AND deleted = false
  AND field_code = 'team_member_ids'
  AND creator = 'V258';

UPDATE dynamicbusiness.ent_emergency_resource_t1 team
SET custom_fields = coalesce(team.custom_fields, '{}'::jsonb) - 'team_member_ids',
    total_quantity = NULL,
    available_quantity = NULL,
    updater = 'V265',
    update_time = CURRENT_TIMESTAMP
WHERE team.tenant_id = 1
  AND team.deleted = false
  AND team.entity_type_code = 'emergency_resource'
  AND coalesce(team.custom_fields, '{}'::jsonb) ? 'team_member_ids'
  AND NOT EXISTS (
      SELECT 1
      FROM dynamicbusiness.dynamic_entity_relation_t1 remain
      WHERE remain.tenant_id = 1
        AND remain.deleted = false
        AND remain.field_code = 'team_member_ids'
        AND remain.source_entity_id = team.id
  );

UPDATE dynamicbusiness.ent_emergency_resource_t1 team
SET custom_fields = coalesce(team.custom_fields, '{}'::jsonb)
        || jsonb_build_object('team_member_ids', roster.members),
    total_quantity = roster.member_count,
    available_quantity = roster.member_count,
    updater = 'V265',
    update_time = CURRENT_TIMESTAMP
FROM (
    SELECT source_entity_id,
           count(*)::numeric AS member_count,
           jsonb_agg(
               jsonb_build_object('id', target_entity_id, 'entityTypeCode', 'user_account')
               ORDER BY target_entity_id
           ) AS members
    FROM dynamicbusiness.dynamic_entity_relation_t1
    WHERE tenant_id = 1
      AND deleted = false
      AND field_code = 'team_member_ids'
    GROUP BY source_entity_id
) roster
WHERE team.id = roster.source_entity_id
  AND team.tenant_id = 1
  AND team.deleted = false;
