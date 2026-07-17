-- 样例：地面巡检机器人设备（动态业务 equipment）
-- 用法：psql -h 127.0.0.1 -U postgres -d sinopec -f seed_ground_robot_equipment.sql
-- 幂等：按 code / category code 判断，已存在则跳过。
-- 设施：45 洛阳圣瑞、44 金桥（若设施行存在才插入设备）。

SET search_path TO dynamicbusiness;

-- 分类：建筑智能化下「地面机器人巡检」+ 叶子「地面巡检机器人」
INSERT INTO dynamic_category (
    id, category_type_code, name, code, parent_id,
    creator, create_time, updater, update_time, deleted, tenant_id, status
)
SELECT
    4125, 'equipment', '地面机器人巡检', 'EQCAT-L2-BMS-GROBOT', 281,
    'seed', CURRENT_TIMESTAMP, 'seed', CURRENT_TIMESTAMP, false, 0, 1
WHERE NOT EXISTS (
    SELECT 1 FROM dynamic_category WHERE code = 'EQCAT-L2-BMS-GROBOT' AND deleted = false
);

INSERT INTO dynamic_category (
    id, category_type_code, name, code, parent_id,
    creator, create_time, updater, update_time, deleted, tenant_id, status
)
SELECT
    4126, 'equipment', '地面巡检机器人', 'EQCAT-DEV-GROBOT',
    (SELECT id FROM dynamic_category WHERE code = 'EQCAT-L2-BMS-GROBOT' AND deleted = false LIMIT 1),
    'seed', CURRENT_TIMESTAMP, 'seed', CURRENT_TIMESTAMP, false, 0, 1
WHERE NOT EXISTS (
    SELECT 1 FROM dynamic_category WHERE code = 'EQCAT-DEV-GROBOT' AND deleted = false
);

-- 模型：地面巡检机器人
INSERT INTO dynamic_model (
    id, entity_type_code, name, code, description, status, sort,
    creator, create_time, updater, update_time, deleted, tenant_id
)
SELECT
    1400, 'equipment', '地面巡检机器人', 'MODEL-GROUND-ROBOT',
    '站内地面巡检执行体；规划默认机动剖面 ground_robot', 1, 0,
    'seed', CURRENT_TIMESTAMP, 'seed', CURRENT_TIMESTAMP, false, 0
WHERE NOT EXISTS (
    SELECT 1 FROM dynamic_model WHERE code = 'MODEL-GROUND-ROBOT' AND deleted = false
);

-- 洛阳圣瑞：2 台
INSERT INTO ent_equipment (
    id, entity_type_code, model_id, name, code, facility_id, zone_id,
    device_code, status, sort, custom_fields,
    creator, create_time, updater, update_time, deleted, tenant_id, guid
)
SELECT
    900101, 'equipment',
    (SELECT id FROM dynamic_model WHERE code = 'MODEL-GROUND-ROBOT' AND deleted = false LIMIT 1),
    '洛阳圣瑞 1 号巡检机器人', 'EQ-GROBOT-LYSR-01', 45, NULL,
    'GROBOT-LYSR-01', 1, 10,
    jsonb_build_object(
        'mobilityProfileId', 'ground_robot',
        'deviceTypeName', '地面巡检机器人',
        'facilityCode', 'FAC-LUOYANG-SHENGRUI'
    ),
    'seed', CURRENT_TIMESTAMP, 'seed', CURRENT_TIMESTAMP, false, 0,
    'guid-eq-grobot-lysr-01'
WHERE EXISTS (SELECT 1 FROM ent_facility WHERE id = 45 AND deleted = false)
  AND NOT EXISTS (
      SELECT 1 FROM ent_equipment WHERE code = 'EQ-GROBOT-LYSR-01' AND deleted = false
  );

INSERT INTO ent_equipment (
    id, entity_type_code, model_id, name, code, facility_id, zone_id,
    device_code, status, sort, custom_fields,
    creator, create_time, updater, update_time, deleted, tenant_id, guid
)
SELECT
    900102, 'equipment',
    (SELECT id FROM dynamic_model WHERE code = 'MODEL-GROUND-ROBOT' AND deleted = false LIMIT 1),
    '洛阳圣瑞 2 号巡检机器人', 'EQ-GROBOT-LYSR-02', 45, NULL,
    'GROBOT-LYSR-02', 1, 20,
    jsonb_build_object(
        'mobilityProfileId', 'ground_robot',
        'deviceTypeName', '地面巡检机器人',
        'facilityCode', 'FAC-LUOYANG-SHENGRUI'
    ),
    'seed', CURRENT_TIMESTAMP, 'seed', CURRENT_TIMESTAMP, false, 0,
    'guid-eq-grobot-lysr-02'
WHERE EXISTS (SELECT 1 FROM ent_facility WHERE id = 45 AND deleted = false)
  AND NOT EXISTS (
      SELECT 1 FROM ent_equipment WHERE code = 'EQ-GROBOT-LYSR-02' AND deleted = false
  );

-- 金桥：1 台
INSERT INTO ent_equipment (
    id, entity_type_code, model_id, name, code, facility_id, zone_id,
    device_code, status, sort, custom_fields,
    creator, create_time, updater, update_time, deleted, tenant_id, guid
)
SELECT
    900103, 'equipment',
    (SELECT id FROM dynamic_model WHERE code = 'MODEL-GROUND-ROBOT' AND deleted = false LIMIT 1),
    '金桥 1 号巡检机器人', 'EQ-GROBOT-JQ-01', 44, NULL,
    'GROBOT-JQ-01', 1, 10,
    jsonb_build_object(
        'mobilityProfileId', 'ground_robot',
        'deviceTypeName', '地面巡检机器人',
        'facilityCode', 'FAC-JINQIAO'
    ),
    'seed', CURRENT_TIMESTAMP, 'seed', CURRENT_TIMESTAMP, false, 0,
    'guid-eq-grobot-jq-01'
WHERE EXISTS (SELECT 1 FROM ent_facility WHERE id = 44 AND deleted = false)
  AND NOT EXISTS (
      SELECT 1 FROM ent_equipment WHERE code = 'EQ-GROBOT-JQ-01' AND deleted = false
  );

-- 分类绑定
INSERT INTO dynamic_category_entity_link (
    id, category_id, entity_id, entity_model_id,
    creator, create_time, updater, update_time, deleted, tenant_id
)
SELECT
    500 + e.id % 1000,
    (SELECT id FROM dynamic_category WHERE code = 'EQCAT-DEV-GROBOT' AND deleted = false LIMIT 1),
    e.id,
    e.model_id,
    'seed', CURRENT_TIMESTAMP, 'seed', CURRENT_TIMESTAMP, false, 0
FROM ent_equipment e
WHERE e.code IN ('EQ-GROBOT-LYSR-01', 'EQ-GROBOT-LYSR-02', 'EQ-GROBOT-JQ-01')
  AND e.deleted = false
  AND NOT EXISTS (
      SELECT 1 FROM dynamic_category_entity_link l
      WHERE l.entity_id = e.id
        AND l.category_id = (SELECT id FROM dynamic_category WHERE code = 'EQCAT-DEV-GROBOT' AND deleted = false LIMIT 1)
        AND l.deleted = false
  );

SELECT e.id, e.name, e.code, e.facility_id, e.custom_fields->>'mobilityProfileId' AS profile
FROM ent_equipment e
WHERE e.code LIKE 'EQ-GROBOT-%' AND e.deleted = false
ORDER BY e.id;
