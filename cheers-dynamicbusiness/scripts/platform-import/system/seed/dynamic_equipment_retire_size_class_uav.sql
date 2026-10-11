-- 按体型登记的型号不是产品型号。
-- 负责：金桥大疆机场这台若还挂在「小型无人机」上，改挂产品型号「大疆机场」（名称里没有 2、3）。
--       金桥大疆 Pilot 这台若还挂在「小型无人机」上，改挂产品型号 Matrice 30（dji-m30）。台账没有写明是哪一款飞机，按「任意一款大疆飞机」落在这一款。
--       「小型无人机」「大型无人机」没有任何设备在用时，从型号名单停用，设备表单不再列出。
-- 不负责：改协议、接入方式、名称、场站；覆盖设备上已经填过的规格；改东古两台和两台机器人。

SET search_path TO dynamicbusiness;

BEGIN;

UPDATE ent_equipment_t1 AS e
SET
  model_id = dock.id,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
FROM dynamic_model AS dock
WHERE e.code = 'EQ-DJI-DOCK-JQ-01'
  AND e.tenant_id = 1
  AND e.deleted = false
  AND dock.code = 'dji-dock-1'
  AND dock.tenant_id = 1
  AND dock.deleted = false
  AND e.model_id IS DISTINCT FROM dock.id
  AND EXISTS (
    SELECT 1
    FROM dynamic_model AS size_class
    WHERE size_class.id = e.model_id
      AND size_class.tenant_id = 1
      AND size_class.name = '小型无人机'
  );

UPDATE ent_equipment_t1 AS e
SET
  model_id = aircraft.id,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
FROM dynamic_model AS aircraft
WHERE e.code = 'EQ-DJI-PILOT-JQ-01'
  AND e.tenant_id = 1
  AND e.deleted = false
  AND aircraft.code = 'dji-m30'
  AND aircraft.tenant_id = 1
  AND aircraft.deleted = false
  AND e.model_id IS DISTINCT FROM aircraft.id
  AND EXISTS (
    SELECT 1
    FROM dynamic_model AS size_class
    WHERE size_class.id = e.model_id
      AND size_class.tenant_id = 1
      AND size_class.name = '小型无人机'
  );

UPDATE dynamic_model AS size_class
SET
  status = 0,
  deleted = true,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
WHERE size_class.tenant_id = 1
  AND size_class.entity_type_code = 'equipment'
  AND size_class.name IN ('小型无人机', '大型无人机')
  AND size_class.deleted = false
  AND NOT EXISTS (
    SELECT 1
    FROM ent_equipment_t1 AS e
    WHERE e.model_id = size_class.id
      AND e.deleted = false
  );

COMMIT;
