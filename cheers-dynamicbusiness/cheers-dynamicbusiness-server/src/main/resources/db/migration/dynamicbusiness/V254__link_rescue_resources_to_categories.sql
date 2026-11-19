-- 站内应急资源和外部救援资源按分类挂上。
-- 点开灭火器、检测仪、呼吸器、应急队伍、车辆和大型装备、专家、医院、消防、公安时，能看到这一类的型号和资源。
-- 不改数量，不把金桥厂区的库存复制到其它站。

SELECT setval(
    'dynamicbusiness.dynamic_model_category_relation_id_seq',
    GREATEST(
        (SELECT COALESCE(MAX(id), 1) FROM dynamicbusiness.dynamic_model_category_relation),
        (SELECT COALESCE(MAX(id), 1) FROM dynamicbusiness.dynamic_model_category_relation_t1)
    )
);

SELECT setval(
    'dynamicbusiness.dynamic_entity_category_relation_id_seq',
    GREATEST(
        (SELECT COALESCE(MAX(id), 1) FROM dynamicbusiness.dynamic_entity_category_relation),
        (SELECT COALESCE(MAX(id), 1) FROM dynamicbusiness.dynamic_entity_category_relation_t1)
    )
);

INSERT INTO dynamicbusiness.dynamic_model_category_relation_t1 (
    model_id, category_id, entity_type_code, sort, creator, updater, deleted, tenant_id, model_code, category_code
)
SELECT m.id,
       c.id,
       m.entity_type_code,
       0,
       '1',
       '1',
       false,
       1,
       m.code,
       c.code
FROM dynamicbusiness.dynamic_model m
JOIN dynamicbusiness.dynamic_category c
  ON c.tenant_id = 1
 AND c.deleted = false
 AND c.code = CASE
        WHEN m.entity_type_code = 'external_rescue_resource' AND m.code IN ('ex-hospital-3a', 'ex-hospital-2a') THEN 'ex-hospital'
        WHEN m.entity_type_code = 'external_rescue_resource' AND m.code IN ('ex-fire-dadui', 'ex-fire-zhongdui') THEN 'ex-fire'
        WHEN m.entity_type_code = 'external_rescue_resource' AND m.code IN ('ex-police-bureau', 'ex-police-station') THEN 'ex-police'
        WHEN m.entity_type_code = 'emergency_resource' AND m.name LIKE '%专家%' THEN 'er-expert'
        WHEN m.entity_type_code = 'emergency_resource' AND (m.name LIKE '%队%' OR m.name LIKE '%组%' OR m.name LIKE '%武警%') THEN 'er-team'
        WHEN m.entity_type_code = 'emergency_resource' AND m.name LIKE '%灭火器%' THEN 'er-kind-extinguisher'
        WHEN m.entity_type_code = 'emergency_resource' AND m.name LIKE '%检测仪%' THEN 'er-kind-detector'
        WHEN m.entity_type_code = 'emergency_resource' AND m.name LIKE '%呼吸器%' THEN 'er-kind-respirator'
        WHEN m.entity_type_code = 'emergency_resource' AND (m.name LIKE '%车%' OR m.name = '挖掘机') THEN 'er-vehicle'
        WHEN m.entity_type_code = 'emergency_resource' THEN 'er-supply'
        ELSE NULL
     END
WHERE m.tenant_id = 1
  AND m.deleted = false
  AND m.status = 1
  AND m.entity_type_code IN ('emergency_resource', 'external_rescue_resource')
  AND NOT EXISTS (
      SELECT 1
      FROM dynamicbusiness.dynamic_model_category_relation_t1 r
      WHERE r.deleted = false
        AND r.tenant_id = 1
        AND r.model_id = m.id
        AND r.category_id = c.id
  );

INSERT INTO dynamicbusiness.dynamic_entity_category_relation_t1 (
    entity_id, category_id, entity_type_code, sort, creator, updater, deleted, tenant_id
)
SELECT e.id,
       c.id,
       'emergency_resource',
       0,
       '1',
       '1',
       false,
       1
FROM dynamicbusiness.ent_emergency_resource_t1 e
JOIN dynamicbusiness.dynamic_model m ON m.id = e.model_id AND m.deleted = false
JOIN dynamicbusiness.dynamic_category c
  ON c.tenant_id = 1
 AND c.deleted = false
 AND c.code = CASE
        WHEN m.name LIKE '%专家%' THEN 'er-expert'
        WHEN m.name LIKE '%队%' OR m.name LIKE '%组%' OR m.name LIKE '%武警%' THEN 'er-team'
        WHEN m.name LIKE '%灭火器%' THEN 'er-kind-extinguisher'
        WHEN m.name LIKE '%检测仪%' THEN 'er-kind-detector'
        WHEN m.name LIKE '%呼吸器%' THEN 'er-kind-respirator'
        WHEN m.name LIKE '%车%' OR m.name = '挖掘机' THEN 'er-vehicle'
        ELSE 'er-supply'
     END
WHERE e.tenant_id = 1
  AND e.deleted = false
  AND NOT EXISTS (
      SELECT 1
      FROM dynamicbusiness.dynamic_entity_category_relation_t1 r
      WHERE r.deleted = false
        AND r.entity_id = e.id
        AND r.entity_type_code = 'emergency_resource'
        AND r.category_id = c.id
  );

INSERT INTO dynamicbusiness.dynamic_entity_category_relation_t1 (
    entity_id, category_id, entity_type_code, sort, creator, updater, deleted, tenant_id
)
SELECT e.id,
       c.id,
       'external_rescue_resource',
       0,
       '1',
       '1',
       false,
       1
FROM dynamicbusiness.ent_external_rescue_resource_t1 e
JOIN dynamicbusiness.dynamic_model m ON m.id = e.model_id AND m.deleted = false
JOIN dynamicbusiness.dynamic_category c
  ON c.tenant_id = 1
 AND c.deleted = false
 AND c.code = CASE
        WHEN m.code IN ('ex-hospital-3a', 'ex-hospital-2a') THEN 'ex-hospital'
        WHEN m.code IN ('ex-fire-dadui', 'ex-fire-zhongdui') THEN 'ex-fire'
        WHEN m.code IN ('ex-police-bureau', 'ex-police-station') THEN 'ex-police'
        ELSE NULL
     END
WHERE e.tenant_id = 1
  AND e.deleted = false
  AND NOT EXISTS (
      SELECT 1
      FROM dynamicbusiness.dynamic_entity_category_relation_t1 r
      WHERE r.deleted = false
        AND r.entity_id = e.id
        AND r.entity_type_code = 'external_rescue_resource'
        AND r.category_id = c.id
  );
