-- 各站补应急队伍。队员只存人员编号，姓名和证书不写在这条资源上。
-- 运行班记入运行维护应急保障队伍，维修班记入管道抢修队，安全环保记入专职消防队。
-- 同一个人不记进第二支队伍。其余队伍只写所在场站，队员留空。
-- 这座站有经纬度时写入坐标。没有经纬度时坐标留空。不编造证书，不抄金桥厂区的人数。

SELECT setval(
    'dynamicbusiness.ent_emergency_resource_t1_id_seq',
    (SELECT COALESCE(MAX(id), 1) FROM dynamicbusiness.ent_emergency_resource_t1)
);
SELECT setval(
    'dynamicbusiness.dynamic_entity_category_relation_t1_id_seq',
    (SELECT COALESCE(MAX(id), 1) FROM dynamicbusiness.dynamic_entity_category_relation_t1)
);
SELECT setval(
    'dynamicbusiness.dynamic_entity_relation_t1_id_seq',
    (SELECT COALESCE(MAX(id), 1) FROM dynamicbusiness.dynamic_entity_relation_t1)
);

INSERT INTO dynamicbusiness.dynamic_field (
    code, name, type, description, source, status, provider_code,
    governance_status, creator, updater, tenant_id
)
SELECT 'team_member_ids', '队员', 'ENTITY_REF_MULTI',
       '这支队伍里的人。只记人员编号。姓名从用户管理读，证书从人员资质读。',
       'CUSTOM', 1, 'dynamic-entity:user_account', 'COMPANY', 'V258', 'V258', 1
WHERE NOT EXISTS (
    SELECT 1 FROM dynamicbusiness.dynamic_field
    WHERE tenant_id = 1 AND deleted = false AND code = 'team_member_ids'
);

INSERT INTO dynamicbusiness.dynamic_model_field_assignment (
    model_id, field_id, model_code, field_code, required,
    is_searchable, is_filterable, is_sortable, sort, field_source,
    creator, tenant_id, create_visible, edit_visible, detail_visible
)
SELECT m.id, f.id, m.code, f.code, false,
       false, false, false, 3, 'LIBRARY',
       'V258', 1, true, true, true
FROM dynamicbusiness.dynamic_model m
JOIN dynamicbusiness.dynamic_model_category_relation_t1 mr
  ON mr.model_id = m.id AND mr.deleted = false AND mr.tenant_id = 1
JOIN dynamicbusiness.dynamic_category c
  ON c.id = mr.category_id AND c.deleted = false AND c.name = '应急队伍'
JOIN dynamicbusiness.dynamic_field f
  ON f.tenant_id = 1 AND f.deleted = false AND f.code = 'team_member_ids'
WHERE m.tenant_id = 1
  AND m.deleted = false
  AND m.entity_type_code = 'emergency_resource'
  AND NOT EXISTS (
      SELECT 1 FROM dynamicbusiness.dynamic_model_field_assignment existing
      WHERE existing.tenant_id = 1
        AND existing.deleted = false
        AND existing.model_id = m.id
        AND existing.field_code = 'team_member_ids'
  );

DELETE FROM dynamicbusiness.model_crud_form_definition form
USING dynamicbusiness.dynamic_model m,
      dynamicbusiness.dynamic_model_category_relation_t1 mr,
      dynamicbusiness.dynamic_category c
WHERE form.tenant_id = 1
  AND form.entity_type_code = 'emergency_resource'
  AND form.model_id = m.id
  AND mr.model_id = m.id
  AND mr.deleted = false
  AND mr.tenant_id = 1
  AND c.id = mr.category_id
  AND c.deleted = false
  AND c.name = '应急队伍';

INSERT INTO dynamicbusiness.ent_emergency_resource_t1 (
    tenant_id, entity_type_code, model_id, name, code, status, facility_id,
    current_location, creator, updater, deleted
)
SELECT 1,
       'emergency_resource',
       m.id,
       station.name || m.name,
       'er-team-' || station.id || '-' || m.id,
       1,
       station.id,
       station.name,
       'V258',
       'V258',
       false
FROM dynamicbusiness.ent_facility_t1 station
JOIN dynamicbusiness.dynamic_model m
  ON m.tenant_id = 1
 AND m.deleted = false
 AND m.status = 1
 AND m.entity_type_code = 'emergency_resource'
JOIN dynamicbusiness.dynamic_model_category_relation_t1 mr
  ON mr.model_id = m.id AND mr.deleted = false AND mr.tenant_id = 1
JOIN dynamicbusiness.dynamic_category c
  ON c.id = mr.category_id AND c.deleted = false AND c.name = '应急队伍'
WHERE station.deleted = false
  AND station.tenant_id = 1
  AND EXISTS (
      SELECT 1 FROM dynamicbusiness.ent_user_account_t1 u
      WHERE u.deleted = false AND u.facility_id = station.id
  )
  AND NOT EXISTS (
      SELECT 1 FROM dynamicbusiness.ent_emergency_resource_t1 existing
      WHERE existing.tenant_id = 1
        AND existing.deleted = false
        AND existing.facility_id = station.id
        AND existing.model_id = m.id
  );

INSERT INTO dynamicbusiness.dynamic_entity_category_relation_t1 (
    entity_id, category_id, entity_type_code, sort, creator, updater, deleted, tenant_id
)
SELECT created.id, template_link.category_id, 'emergency_resource', 0, 'V258', 'V258', false, 1
FROM dynamicbusiness.ent_emergency_resource_t1 created
JOIN dynamicbusiness.ent_emergency_resource_t1 template
  ON template.tenant_id = 1
 AND template.deleted = false
 AND template.facility_id = 44
 AND template.model_id = created.model_id
JOIN dynamicbusiness.dynamic_entity_category_relation_t1 template_link
  ON template_link.entity_id = template.id
 AND template_link.deleted = false
 AND template_link.entity_type_code = 'emergency_resource'
WHERE created.tenant_id = 1
  AND created.deleted = false
  AND created.creator = 'V258'
  AND NOT EXISTS (
      SELECT 1 FROM dynamicbusiness.dynamic_entity_category_relation_t1 existing
      WHERE existing.deleted = false
        AND existing.entity_type_code = 'emergency_resource'
        AND existing.entity_id = created.id
        AND existing.category_id = template_link.category_id
  );

UPDATE dynamicbusiness.ent_emergency_resource_t1 team
SET name = station.name || model.name,
    current_location = station.name,
    custom_fields = coalesce(team.custom_fields, '{}'::jsonb)
        || CASE
            WHEN station.longitude IS NOT NULL AND station.latitude IS NOT NULL
            THEN jsonb_build_object(
                'coordinate_gis',
                jsonb_build_object('longitude', station.longitude, 'latitude', station.latitude)
            )
            ELSE '{}'::jsonb
        END,
    updater = 'V258',
    update_time = CURRENT_TIMESTAMP
FROM dynamicbusiness.ent_facility_t1 station,
     dynamicbusiness.dynamic_model model
WHERE team.tenant_id = 1
  AND team.deleted = false
  AND team.entity_type_code = 'emergency_resource'
  AND team.facility_id = station.id
  AND station.deleted = false
  AND team.model_id = model.id
  AND model.deleted = false
  AND EXISTS (
      SELECT 1
      FROM dynamicbusiness.dynamic_model_category_relation_t1 mr
      JOIN dynamicbusiness.dynamic_category c ON c.id = mr.category_id
      WHERE mr.model_id = model.id
        AND mr.deleted = false
        AND c.deleted = false
        AND c.name = '应急队伍'
  );

UPDATE dynamicbusiness.ent_emergency_resource_t1 team
SET custom_fields = coalesce(team.custom_fields, '{}'::jsonb)
        || jsonb_build_object('team_member_ids', roster.members),
    total_quantity = roster.member_count,
    available_quantity = roster.member_count,
    updater = 'V258',
    update_time = CURRENT_TIMESTAMP
FROM (
    SELECT u.facility_id,
           CASE d.name
               WHEN '安全环保' THEN '专职消防队'
               WHEN '维修班' THEN '管道抢修队'
               WHEN '运行班' THEN '运行维护应急保障队伍'
           END AS team_name,
           count(*)::numeric AS member_count,
           jsonb_agg(
               jsonb_build_object('id', u.id, 'entityTypeCode', 'user_account')
               ORDER BY u.id
           ) AS members
    FROM dynamicbusiness.ent_user_account_t1 u
    JOIN dynamicbusiness.ent_department_t1 d
      ON d.id = u.department_id AND d.deleted = false
    WHERE u.deleted = false
      AND u.facility_id IS NOT NULL
      AND d.name IN ('安全环保', '维修班', '运行班')
    GROUP BY u.facility_id, d.name
) roster,
     dynamicbusiness.dynamic_model model
WHERE team.tenant_id = 1
  AND team.deleted = false
  AND team.facility_id = roster.facility_id
  AND team.model_id = model.id
  AND model.name = roster.team_name
  AND model.deleted = false;

INSERT INTO dynamicbusiness.dynamic_entity_relation_t1 (
    source_entity_id, target_entity_id, relation_type, relation_name, status,
    field_code, source_model_code, source_entity_type_code, target_entity_type_code,
    creator, updater, tenant_id
)
SELECT team.id, member.person_id, 'ENTITY_REF_MULTI', '队员', 1,
       'team_member_ids', model.code, 'emergency_resource', 'user_account',
       'V258', 'V258', 1
FROM dynamicbusiness.ent_emergency_resource_t1 team
JOIN dynamicbusiness.dynamic_model model ON model.id = team.model_id
JOIN (
    SELECT u.id AS person_id,
           u.facility_id,
           CASE d.name
               WHEN '安全环保' THEN '专职消防队'
               WHEN '维修班' THEN '管道抢修队'
               WHEN '运行班' THEN '运行维护应急保障队伍'
           END AS team_name
    FROM dynamicbusiness.ent_user_account_t1 u
    JOIN dynamicbusiness.ent_department_t1 d
      ON d.id = u.department_id AND d.deleted = false
    WHERE u.deleted = false
      AND u.facility_id IS NOT NULL
      AND d.name IN ('安全环保', '维修班', '运行班')
) member ON member.facility_id = team.facility_id AND member.team_name = model.name
WHERE team.tenant_id = 1
  AND team.deleted = false
  AND team.entity_type_code = 'emergency_resource'
  AND NOT EXISTS (
      SELECT 1 FROM dynamicbusiness.dynamic_entity_relation_t1 existing
      WHERE existing.deleted = false
        AND existing.tenant_id = 1
        AND existing.source_entity_id = team.id
        AND existing.target_entity_id = member.person_id
        AND existing.field_code = 'team_member_ids'
  );
