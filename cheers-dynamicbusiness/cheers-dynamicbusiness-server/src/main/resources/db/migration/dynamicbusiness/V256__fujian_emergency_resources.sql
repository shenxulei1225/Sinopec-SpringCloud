-- 福建各站补器材名册。数量留空，由该站填写。不抄金桥厂区的数量。
-- 车辆要有车牌才保存，专家和队伍是人，这里不编。
-- 福建各段缺的医院、消防、公安，按公开单位名称补上。一条单位只归一段。查不到的不编。电话和床位没有把握的留空。

SELECT setval(
    'dynamicbusiness.ent_emergency_resource_t1_id_seq',
    (SELECT COALESCE(MAX(id), 1) FROM dynamicbusiness.ent_emergency_resource_t1)
);

SELECT setval(
    'dynamicbusiness.ent_external_rescue_resource_t1_id_seq',
    (SELECT COALESCE(MAX(id), 1) FROM dynamicbusiness.ent_external_rescue_resource_t1)
);

SELECT setval(
    'dynamicbusiness.dynamic_entity_category_relation_t1_id_seq',
    (SELECT COALESCE(MAX(id), 1) FROM dynamicbusiness.dynamic_entity_category_relation_t1)
);

INSERT INTO dynamicbusiness.ent_emergency_resource_t1 (
    tenant_id, entity_type_code, model_id, name, code, status, facility_id, creator, deleted
)
SELECT 1,
       'emergency_resource',
       m.id,
       m.name,
       'er-fj-' || station.facility_id || '-' || m.id,
       1,
       station.facility_id,
       'V256',
       false
FROM (
    SELECT DISTINCT f.id AS facility_id
    FROM dynamicbusiness.dynamic_entity_relation_t1 r
    JOIN dynamicbusiness.ent_pipeline_t1 p
      ON p.id = r.target_entity_id AND p.deleted = false
    JOIN dynamicbusiness.ent_facility_t1 f
      ON f.id = r.source_entity_id AND f.deleted = false
    WHERE r.deleted = false
      AND r.field_code = 'pipeline_segment_ids'
      AND r.source_entity_type_code = 'facility'
      AND p.id NOT IN (23, 29, 30, 31)
      AND COALESCE(p.parent_id, 0) <> 23
) station
JOIN dynamicbusiness.dynamic_model m
  ON m.tenant_id = 1
 AND m.deleted = false
 AND m.status = 1
 AND m.entity_type_code = 'emergency_resource'
JOIN dynamicbusiness.dynamic_model_category_relation_t1 mr
  ON mr.model_id = m.id AND mr.deleted = false AND mr.tenant_id = 1
JOIN dynamicbusiness.dynamic_category c
  ON c.id = mr.category_id AND c.deleted = false AND c.name IN ('灭火器', '检测仪', '呼吸器', '物资装备')
WHERE NOT EXISTS (
    SELECT 1
    FROM dynamicbusiness.ent_emergency_resource_t1 existing
    WHERE existing.tenant_id = 1
      AND existing.deleted = false
      AND existing.facility_id = station.facility_id
      AND existing.model_id = m.id
);

INSERT INTO dynamicbusiness.dynamic_entity_category_relation_t1 (
    entity_id, category_id, entity_type_code, sort, creator, updater, deleted, tenant_id
)
SELECT created.id, template_link.category_id, 'emergency_resource', 0, 'V256', 'V256', false, 1
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
  AND created.creator = 'V256'
  AND NOT EXISTS (
      SELECT 1
      FROM dynamicbusiness.dynamic_entity_category_relation_t1 existing
      WHERE existing.deleted = false
        AND existing.entity_type_code = 'emergency_resource'
        AND existing.entity_id = created.id
        AND existing.category_id = template_link.category_id
  );

INSERT INTO dynamicbusiness.ent_external_rescue_resource_t1 (
    tenant_id, entity_type_code, model_id, name, code, status,
    pipeline_segment_id, creator, deleted
)
SELECT 1,
       'external_rescue_resource',
       model.id,
       unit.unit_name,
       'ext-' || segment.id || '-' || substr(md5(unit.unit_name), 1, 12),
       1,
       segment.id,
       'V256',
       false
FROM (
    VALUES
        ('南线', 'ex-police-bureau', '厦门市公安局集美分局'),
        ('漳厦', 'ex-police-bureau', '厦门市公安局同安分局'),
        ('漳厦', 'ex-police-bureau', '厦门市公安局海沧分局'),
        ('汀龙', 'ex-police-bureau', '长汀县公安局'),
        ('龙漳', 'ex-police-bureau', '漳州市公安局芗城分局'),
        ('龙漳', 'ex-police-station', '龙海区公安局角美派出所'),
        ('龙漳', 'ex-fire-zhongdui', '角美消防救援站'),
        ('漳龙', 'ex-police-bureau', '南靖县公安局'),
        ('漳龙', 'ex-police-bureau', '龙岩市公安局新罗分局'),
        ('漳诏', 'ex-hospital-2a', '漳浦县医院'),
        ('漳诏', 'ex-police-bureau', '漳浦县公安局'),
        ('漳诏', 'ex-fire-dadui', '漳浦县消防救援大队'),
        ('漳诏', 'ex-hospital-2a', '云霄县医院'),
        ('漳诏', 'ex-police-bureau', '云霄县公安局'),
        ('漳诏', 'ex-fire-dadui', '云霄县消防救援大队'),
        ('漳程', 'ex-hospital-2a', '龙海区第一医院'),
        ('漳程', 'ex-police-station', '龙海区公安局港尾派出所'),
        ('漳程', 'ex-fire-zhongdui', '港尾消防救援站'),
        ('漳程', 'ex-fire-dadui', '龙海区消防救援大队'),
        ('福鼎', 'ex-hospital-2a', '福鼎市医院'),
        ('福鼎', 'ex-hospital-2a', '福安市医院'),
        ('福鼎', 'ex-police-bureau', '福鼎市公安局'),
        ('福鼎', 'ex-police-bureau', '福安市公安局'),
        ('福鼎', 'ex-police-bureau', '宁德市公安局蕉城分局'),
        ('福鼎', 'ex-fire-dadui', '福鼎市消防救援大队'),
        ('福鼎', 'ex-fire-dadui', '宁德市蕉城区消防救援大队'),
        ('莆福', 'ex-hospital-2a', '福州市长乐区医院'),
        ('莆福', 'ex-police-bureau', '福州市公安局长乐分局'),
        ('莆福', 'ex-police-station', '福清市公安局宏路派出所'),
        ('莆福', 'ex-fire-zhongdui', '福清市宏路消防救援站'),
        ('厦莆', 'ex-fire-dadui', '仙游县消防救援大队'),
        ('厦莆', 'ex-police-bureau', '仙游县公安局'),
        ('水德', 'ex-hospital-2a', '安溪县医院'),
        ('水德', 'ex-police-bureau', '安溪县公安局'),
        ('水德', 'ex-fire-dadui', '安溪县消防救援大队'),
        ('水德', 'ex-police-bureau', '德化县公安局'),
        ('水德', 'ex-police-bureau', '永春县公安局')
) AS unit(segment_name, model_code, unit_name)
JOIN dynamicbusiness.ent_pipeline_t1 segment
  ON segment.name = unit.segment_name
 AND segment.deleted = false
 AND COALESCE(segment.parent_id, 0) > 0
JOIN dynamicbusiness.dynamic_model model
  ON model.code = unit.model_code
 AND model.tenant_id = 1
 AND model.deleted = false
WHERE NOT EXISTS (
    SELECT 1
    FROM dynamicbusiness.ent_external_rescue_resource_t1 existing
    WHERE existing.tenant_id = 1
      AND existing.deleted = false
      AND existing.pipeline_segment_id = segment.id
      AND existing.name = unit.unit_name
);

INSERT INTO dynamicbusiness.dynamic_entity_category_relation_t1 (
    entity_id, category_id, entity_type_code, sort, creator, updater, deleted, tenant_id
)
SELECT created.id,
       CASE
           WHEN model.code LIKE 'ex-hospital-%' THEN hospital.id
           WHEN model.code LIKE 'ex-fire-%' THEN fire.id
           ELSE police.id
       END,
       'external_rescue_resource',
       0,
       'V256',
       'V256',
       false,
       1
FROM dynamicbusiness.ent_external_rescue_resource_t1 created
JOIN dynamicbusiness.dynamic_model model ON model.id = created.model_id
JOIN dynamicbusiness.dynamic_category hospital
  ON hospital.code = 'ex-hospital' AND hospital.tenant_id = 1 AND hospital.deleted = false
JOIN dynamicbusiness.dynamic_category fire
  ON fire.code = 'ex-fire' AND fire.tenant_id = 1 AND fire.deleted = false
JOIN dynamicbusiness.dynamic_category police
  ON police.code = 'ex-police' AND police.tenant_id = 1 AND police.deleted = false
WHERE created.tenant_id = 1
  AND created.deleted = false
  AND created.creator = 'V256'
  AND NOT EXISTS (
      SELECT 1
      FROM dynamicbusiness.dynamic_entity_category_relation_t1 existing
      WHERE existing.deleted = false
        AND existing.entity_type_code = 'external_rescue_resource'
        AND existing.entity_id = created.id
  );
