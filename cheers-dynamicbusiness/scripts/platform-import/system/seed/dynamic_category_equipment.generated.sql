-- Standard equipment category library (EQCAT-*)
-- Source: equipment_category_taxonomy.py
-- Regenerate: python export_equipment_category_library.py
-- Nodes: 429 incl. equipment_root; leaf categories: 341

SET search_path TO dynamicbusiness;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  NULL, 'equipment_root', 'equipment_root',
  'equipment', 0,
  1, 1, 'seed', NULL
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'equipment_root' LIMIT 1), '供配电系统', 'EQCAT-L1-ELPW',
  'equipment', 1,
  1, 1, 'seed', NULL
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L1-ELPW' LIMIT 1), '高压配电', 'EQCAT-L2-ELPW-HV',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L1-ELPW'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-ELPW-HV' LIMIT 1), '高压开关柜', 'EQCAT-DEV-HV-SWGR',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L2-ELPW-HV'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-ELPW-HV' LIMIT 1), '电力变压器', 'EQCAT-DEV-TRANSFORMER',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L2-ELPW-HV'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-ELPW-HV' LIMIT 1), '高压电缆', 'EQCAT-DEV-HV-CABLE',
  'equipment', 3,
  1, 1, 'seed', 'EQCAT-L2-ELPW-HV'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L1-ELPW' LIMIT 1), '低压配电', 'EQCAT-L2-ELPW-LV',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L1-ELPW'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-ELPW-LV' LIMIT 1), '低压配电柜', 'EQCAT-DEV-LV-SWGR',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L2-ELPW-LV'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-ELPW-LV' LIMIT 1), '精密配电柜', 'EQCAT-DEV-PDU-PRECISION',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L2-ELPW-LV'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-ELPW-LV' LIMIT 1), '双电源切换装置', 'EQCAT-DEV-ATS',
  'equipment', 3,
  1, 1, 'seed', 'EQCAT-L2-ELPW-LV'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-ELPW-LV' LIMIT 1), '无功补偿柜', 'EQCAT-DEV-CAPACITOR',
  'equipment', 4,
  1, 1, 'seed', 'EQCAT-L2-ELPW-LV'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L1-ELPW' LIMIT 1), '不间断电源', 'EQCAT-L2-ELPW-UPS',
  'equipment', 3,
  1, 1, 'seed', 'EQCAT-L1-ELPW'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-ELPW-UPS' LIMIT 1), 'UPS电源柜', 'EQCAT-DEV-UPS-CABINET',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L2-ELPW-UPS'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-ELPW-UPS' LIMIT 1), '蓄电池组', 'EQCAT-DEV-BATTERY-BANK',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L2-ELPW-UPS'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L1-ELPW' LIMIT 1), '应急电源', 'EQCAT-L2-ELPW-EMERG',
  'equipment', 4,
  1, 1, 'seed', 'EQCAT-L1-ELPW'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-ELPW-EMERG' LIMIT 1), '柴油发电机组', 'EQCAT-DEV-GENSET',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L2-ELPW-EMERG'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L1-ELPW' LIMIT 1), '电能计量', 'EQCAT-L2-ELPW-METER',
  'equipment', 5,
  1, 1, 'seed', 'EQCAT-L1-ELPW'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-ELPW-METER' LIMIT 1), '电量仪', 'EQCAT-DEV-POWER-METER',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L2-ELPW-METER'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-ELPW-METER' LIMIT 1), '电流互感器', 'EQCAT-DEV-CT',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L2-ELPW-METER'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-ELPW-METER' LIMIT 1), '电压互感器', 'EQCAT-DEV-PT',
  'equipment', 3,
  1, 1, 'seed', 'EQCAT-L2-ELPW-METER'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'equipment_root' LIMIT 1), '电气设备及传动', 'EQCAT-L1-ELEQ',
  'equipment', 2,
  1, 1, 'seed', NULL
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L1-ELEQ' LIMIT 1), '电动机', 'EQCAT-L2-ELEQ-MOTOR',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L1-ELEQ'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-ELEQ-MOTOR' LIMIT 1), '低压电动机', 'EQCAT-DEV-MOTOR-LV',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L2-ELEQ-MOTOR'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-ELEQ-MOTOR' LIMIT 1), '高压电动机', 'EQCAT-DEV-MOTOR-HV',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L2-ELEQ-MOTOR'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L1-ELEQ' LIMIT 1), '变频与传动', 'EQCAT-L2-ELEQ-DRIVE',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L1-ELEQ'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-ELEQ-DRIVE' LIMIT 1), '变频器', 'EQCAT-DEV-VFD',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L2-ELEQ-DRIVE'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-ELEQ-DRIVE' LIMIT 1), '软启动器', 'EQCAT-DEV-SOFT-STARTER',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L2-ELEQ-DRIVE'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L1-ELEQ' LIMIT 1), '开关电器', 'EQCAT-L2-ELEQ-SWITCH',
  'equipment', 3,
  1, 1, 'seed', 'EQCAT-L1-ELEQ'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-ELEQ-SWITCH' LIMIT 1), '断路器', 'EQCAT-DEV-MCCB',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L2-ELEQ-SWITCH'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-ELEQ-SWITCH' LIMIT 1), '隔离开关', 'EQCAT-DEV-ISOLATOR',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L2-ELEQ-SWITCH'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-ELEQ-SWITCH' LIMIT 1), '接触器', 'EQCAT-DEV-CONTACTOR',
  'equipment', 3,
  1, 1, 'seed', 'EQCAT-L2-ELEQ-SWITCH'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-ELEQ-SWITCH' LIMIT 1), '继电器', 'EQCAT-DEV-RELAY',
  'equipment', 4,
  1, 1, 'seed', 'EQCAT-L2-ELEQ-SWITCH'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L1-ELEQ' LIMIT 1), '照明装置', 'EQCAT-L2-ELEQ-LIGHT',
  'equipment', 4,
  1, 1, 'seed', 'EQCAT-L1-ELEQ'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-ELEQ-LIGHT' LIMIT 1), '一般照明灯具', 'EQCAT-DEV-LIGHTING',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L2-ELEQ-LIGHT'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-ELEQ-LIGHT' LIMIT 1), '投光灯', 'EQCAT-DEV-FLOOD-LIGHT',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L2-ELEQ-LIGHT'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-ELEQ-LIGHT' LIMIT 1), '高杆灯', 'EQCAT-DEV-HIGH-MAST-LIGHT',
  'equipment', 3,
  1, 1, 'seed', 'EQCAT-L2-ELEQ-LIGHT'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L1-ELEQ' LIMIT 1), '控制与场站箱柜', 'EQCAT-L2-ELEQ-PANEL',
  'equipment', 5,
  1, 1, 'seed', 'EQCAT-L1-ELEQ'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-ELEQ-PANEL' LIMIT 1), '控制箱', 'EQCAT-DEV-CTRL-BOX',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L2-ELEQ-PANEL'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-ELEQ-PANEL' LIMIT 1), '操作柱', 'EQCAT-DEV-OPERATOR-STATION',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L2-ELEQ-PANEL'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-ELEQ-PANEL' LIMIT 1), '检修箱', 'EQCAT-DEV-MAINT-BOX',
  'equipment', 3,
  1, 1, 'seed', 'EQCAT-L2-ELEQ-PANEL'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-ELEQ-PANEL' LIMIT 1), '按钮箱', 'EQCAT-DEV-BUTTON-BOX',
  'equipment', 4,
  1, 1, 'seed', 'EQCAT-L2-ELEQ-PANEL'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'equipment_root' LIMIT 1), '仪器仪表与自动化', 'EQCAT-L1-INST',
  'equipment', 3,
  1, 1, 'seed', NULL
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L1-INST' LIMIT 1), '压力测量', 'EQCAT-L2-INST-PRESSURE',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L1-INST'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-INST-PRESSURE' LIMIT 1), '就地仪表', 'EQCAT-DEV-INST-LOCAL',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L2-INST-PRESSURE'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-INST-PRESSURE' LIMIT 1), '压力表', 'EQCAT-DEV-PT-GAUGE',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L2-INST-PRESSURE'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-INST-PRESSURE' LIMIT 1), '压力变送器', 'EQCAT-DEV-PT-TX',
  'equipment', 3,
  1, 1, 'seed', 'EQCAT-L2-INST-PRESSURE'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-INST-PRESSURE' LIMIT 1), '差压变送器', 'EQCAT-DEV-DPT-TX',
  'equipment', 4,
  1, 1, 'seed', 'EQCAT-L2-INST-PRESSURE'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L1-INST' LIMIT 1), '流量测量', 'EQCAT-L2-INST-FLOW',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L1-INST'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-INST-FLOW' LIMIT 1), '流量变送器', 'EQCAT-DEV-FT-TX',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L2-INST-FLOW'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-INST-FLOW' LIMIT 1), '超声波流量计', 'EQCAT-DEV-FLOWMETER-ULTRA',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L2-INST-FLOW'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-INST-FLOW' LIMIT 1), '涡轮流量计', 'EQCAT-DEV-FLOWMETER-TURB',
  'equipment', 3,
  1, 1, 'seed', 'EQCAT-L2-INST-FLOW'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-INST-FLOW' LIMIT 1), '质量流量计', 'EQCAT-DEV-FLOWMETER-MASS',
  'equipment', 4,
  1, 1, 'seed', 'EQCAT-L2-INST-FLOW'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L1-INST' LIMIT 1), '液位测量', 'EQCAT-L2-INST-LEVEL',
  'equipment', 3,
  1, 1, 'seed', 'EQCAT-L1-INST'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-INST-LEVEL' LIMIT 1), '液位变送器', 'EQCAT-DEV-LT-TX',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L2-INST-LEVEL'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-INST-LEVEL' LIMIT 1), '液位计', 'EQCAT-DEV-LEVEL-GAUGE',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L2-INST-LEVEL'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L1-INST' LIMIT 1), '温度测量', 'EQCAT-L2-INST-TEMP',
  'equipment', 4,
  1, 1, 'seed', 'EQCAT-L1-INST'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-INST-TEMP' LIMIT 1), '温度变送器', 'EQCAT-DEV-TT-TX',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L2-INST-TEMP'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-INST-TEMP' LIMIT 1), '热电阻', 'EQCAT-DEV-RTD',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L2-INST-TEMP'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-INST-TEMP' LIMIT 1), '热电偶', 'EQCAT-DEV-TC',
  'equipment', 3,
  1, 1, 'seed', 'EQCAT-L2-INST-TEMP'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L1-INST' LIMIT 1), '环境检测仪表', 'EQCAT-L2-INST-ENV',
  'equipment', 5,
  1, 1, 'seed', 'EQCAT-L1-INST'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-INST-ENV' LIMIT 1), '温湿度传感器', 'EQCAT-DEV-SENSOR-TH',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L2-INST-ENV'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-INST-ENV' LIMIT 1), '氧气传感器', 'EQCAT-DEV-SENSOR-O2',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L2-INST-ENV'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-INST-ENV' LIMIT 1), '二氧化碳传感器', 'EQCAT-DEV-SENSOR-CO2',
  'equipment', 3,
  1, 1, 'seed', 'EQCAT-L2-INST-ENV'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-INST-ENV' LIMIT 1), '可燃气体探测器', 'EQCAT-DEV-SENSOR-GAS',
  'equipment', 4,
  1, 1, 'seed', 'EQCAT-L2-INST-ENV'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-INST-ENV' LIMIT 1), '漏水监测器', 'EQCAT-DEV-SENSOR-LEAK',
  'equipment', 5,
  1, 1, 'seed', 'EQCAT-L2-INST-ENV'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L1-INST' LIMIT 1), '分析仪表', 'EQCAT-L2-INST-ANALYZER',
  'equipment', 6,
  1, 1, 'seed', 'EQCAT-L1-INST'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-INST-ANALYZER' LIMIT 1), '气体分析仪', 'EQCAT-DEV-GAS-ANALYZER',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L2-INST-ANALYZER'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-INST-ANALYZER' LIMIT 1), '水质分析仪', 'EQCAT-DEV-WATER-ANALYZER',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L2-INST-ANALYZER'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-INST-ANALYZER' LIMIT 1), '分析小屋', 'EQCAT-DEV-ANALYZER-HOUSE',
  'equipment', 3,
  1, 1, 'seed', 'EQCAT-L2-INST-ANALYZER'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L1-INST' LIMIT 1), '控制阀', 'EQCAT-L2-INST-CTRL-VALVE',
  'equipment', 7,
  1, 1, 'seed', 'EQCAT-L1-INST'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-INST-CTRL-VALVE' LIMIT 1), '电动调节阀', 'EQCAT-DEV-CV-ELECTRIC',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L2-INST-CTRL-VALVE'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-INST-CTRL-VALVE' LIMIT 1), '气动调节阀', 'EQCAT-DEV-CV-PNEUMATIC',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L2-INST-CTRL-VALVE'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-INST-CTRL-VALVE' LIMIT 1), '电磁阀', 'EQCAT-DEV-SOLENOID-VALVE',
  'equipment', 3,
  1, 1, 'seed', 'EQCAT-L2-INST-CTRL-VALVE'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L1-INST' LIMIT 1), '贸易计量', 'EQCAT-L2-INST-METERING',
  'equipment', 8,
  1, 1, 'seed', 'EQCAT-L1-INST'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-INST-METERING' LIMIT 1), '计量橇', 'EQCAT-DEV-METER-SKID',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L2-INST-METERING'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-INST-METERING' LIMIT 1), '气质分析橇', 'EQCAT-DEV-METER-GAS',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L2-INST-METERING'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L1-INST' LIMIT 1), '生产控制系统', 'EQCAT-L2-INST-PCS',
  'equipment', 9,
  1, 1, 'seed', 'EQCAT-L1-INST'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-INST-PCS' LIMIT 1), '站控SCADA系统', 'EQCAT-DEV-SCADA',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L2-INST-PCS'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-INST-PCS' LIMIT 1), 'SIS安全仪表系统', 'EQCAT-DEV-SIS',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L2-INST-PCS'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-INST-PCS' LIMIT 1), 'ESD紧急停车系统', 'EQCAT-DEV-ESD',
  'equipment', 3,
  1, 1, 'seed', 'EQCAT-L2-INST-PCS'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-INST-PCS' LIMIT 1), 'PLC控制器', 'EQCAT-DEV-PLC',
  'equipment', 4,
  1, 1, 'seed', 'EQCAT-L2-INST-PCS'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-INST-PCS' LIMIT 1), '过程控制RTU', 'EQCAT-DEV-RTU-PROC',
  'equipment', 5,
  1, 1, 'seed', 'EQCAT-L2-INST-PCS'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-INST-PCS' LIMIT 1), '操作员站', 'EQCAT-DEV-OPS-WORKSTATION',
  'equipment', 6,
  1, 1, 'seed', 'EQCAT-L2-INST-PCS'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L1-INST' LIMIT 1), '楼宇自控', 'EQCAT-L2-INST-BACS',
  'equipment', 10,
  1, 1, 'seed', 'EQCAT-L1-INST'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-INST-BACS' LIMIT 1), 'ACU控制柜', 'EQCAT-DEV-ACU',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L2-INST-BACS'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-INST-BACS' LIMIT 1), 'DCS控制站', 'EQCAT-DEV-DCS',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L2-INST-BACS'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-INST-BACS' LIMIT 1), '仪表接线盒', 'EQCAT-DEV-JUNCTION-BOX',
  'equipment', 3,
  1, 1, 'seed', 'EQCAT-L2-INST-BACS'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'equipment_root' LIMIT 1), '工艺动设备', 'EQCAT-L1-ROTD',
  'equipment', 4,
  1, 1, 'seed', NULL
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L1-ROTD' LIMIT 1), '泵', 'EQCAT-L2-ROTD-PUMP',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L1-ROTD'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-ROTD-PUMP' LIMIT 1), '离心泵', 'EQCAT-DEV-PUMP-CENT',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L2-ROTD-PUMP'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-ROTD-PUMP' LIMIT 1), '往复泵', 'EQCAT-DEV-PUMP-RECIP',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L2-ROTD-PUMP'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-ROTD-PUMP' LIMIT 1), '螺杆泵', 'EQCAT-DEV-PUMP-SCREW',
  'equipment', 3,
  1, 1, 'seed', 'EQCAT-L2-ROTD-PUMP'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-ROTD-PUMP' LIMIT 1), '齿轮泵', 'EQCAT-DEV-PUMP-GEAR',
  'equipment', 4,
  1, 1, 'seed', 'EQCAT-L2-ROTD-PUMP'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L1-ROTD' LIMIT 1), '压缩机', 'EQCAT-L2-ROTD-COMP',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L1-ROTD'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-ROTD-COMP' LIMIT 1), '离心压缩机', 'EQCAT-DEV-COMP-CENT',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L2-ROTD-COMP'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-ROTD-COMP' LIMIT 1), '往复压缩机', 'EQCAT-DEV-COMP-RECIP',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L2-ROTD-COMP'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-ROTD-COMP' LIMIT 1), '螺杆压缩机', 'EQCAT-DEV-COMP-SCREW',
  'equipment', 3,
  1, 1, 'seed', 'EQCAT-L2-ROTD-COMP'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L1-ROTD' LIMIT 1), '风机', 'EQCAT-L2-ROTD-FAN',
  'equipment', 3,
  1, 1, 'seed', 'EQCAT-L1-ROTD'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-ROTD-FAN' LIMIT 1), '轴流风机', 'EQCAT-DEV-FAN-AXIAL',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L2-ROTD-FAN'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-ROTD-FAN' LIMIT 1), '离心风机', 'EQCAT-DEV-FAN-CENT',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L2-ROTD-FAN'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-ROTD-FAN' LIMIT 1), '罗茨风机', 'EQCAT-DEV-FAN-ROOTS',
  'equipment', 3,
  1, 1, 'seed', 'EQCAT-L2-ROTD-FAN'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L1-ROTD' LIMIT 1), '空气压缩机', 'EQCAT-L2-ROTD-AIR',
  'equipment', 4,
  1, 1, 'seed', 'EQCAT-L1-ROTD'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-ROTD-AIR' LIMIT 1), '空气压缩机', 'EQCAT-DEV-AIR-COMP',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L2-ROTD-AIR'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-ROTD-AIR' LIMIT 1), '空气干燥机', 'EQCAT-DEV-AIR-DRYER',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L2-ROTD-AIR'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L1-ROTD' LIMIT 1), '搅拌混合设备', 'EQCAT-L2-ROTD-MIX',
  'equipment', 5,
  1, 1, 'seed', 'EQCAT-L1-ROTD'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-ROTD-MIX' LIMIT 1), '搅拌器', 'EQCAT-DEV-AGITATOR',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L2-ROTD-MIX'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-ROTD-MIX' LIMIT 1), '混合器', 'EQCAT-DEV-MIXER',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L2-ROTD-MIX'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'equipment_root' LIMIT 1), '工艺静设备', 'EQCAT-L1-STAT',
  'equipment', 5,
  1, 1, 'seed', NULL
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L1-STAT' LIMIT 1), '压力容器与储罐', 'EQCAT-L2-STAT-VESSEL',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L1-STAT'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-STAT-VESSEL' LIMIT 1), '储罐', 'EQCAT-DEV-TANK-STORAGE',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L2-STAT-VESSEL'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-STAT-VESSEL' LIMIT 1), '压力容器', 'EQCAT-DEV-PRESSURE-VESSEL',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L2-STAT-VESSEL'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-STAT-VESSEL' LIMIT 1), '分离器', 'EQCAT-DEV-SEPARATOR',
  'equipment', 3,
  1, 1, 'seed', 'EQCAT-L2-STAT-VESSEL'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L1-STAT' LIMIT 1), '过滤分离设备', 'EQCAT-L2-STAT-FILTER',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L1-STAT'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-STAT-FILTER' LIMIT 1), '过滤分离器', 'EQCAT-DEV-FILTER-SEP',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L2-STAT-FILTER'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-STAT-FILTER' LIMIT 1), '旋风分离器', 'EQCAT-DEV-CYCLONE',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L2-STAT-FILTER'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-STAT-FILTER' LIMIT 1), '除雾器', 'EQCAT-DEV-DEMISTER',
  'equipment', 3,
  1, 1, 'seed', 'EQCAT-L2-STAT-FILTER'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L1-STAT' LIMIT 1), '清管收发设备', 'EQCAT-L2-STAT-PIG',
  'equipment', 3,
  1, 1, 'seed', 'EQCAT-L1-STAT'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-STAT-PIG' LIMIT 1), '清管器收发筒', 'EQCAT-DEV-PIG-TRAP',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L2-STAT-PIG'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-STAT-PIG' LIMIT 1), '发球筒', 'EQCAT-DEV-LAUNCHER',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L2-STAT-PIG'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-STAT-PIG' LIMIT 1), '收球筒', 'EQCAT-DEV-RECEIVER',
  'equipment', 3,
  1, 1, 'seed', 'EQCAT-L2-STAT-PIG'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L1-STAT' LIMIT 1), '换热设备', 'EQCAT-L2-STAT-HEAT',
  'equipment', 4,
  1, 1, 'seed', 'EQCAT-L1-STAT'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-STAT-HEAT' LIMIT 1), '换热器', 'EQCAT-DEV-HEAT-EXCHANGER',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L2-STAT-HEAT'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-STAT-HEAT' LIMIT 1), '冷凝器', 'EQCAT-DEV-CONDENSER',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L2-STAT-HEAT'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-STAT-HEAT' LIMIT 1), '再沸器', 'EQCAT-DEV-REBOILER',
  'equipment', 3,
  1, 1, 'seed', 'EQCAT-L2-STAT-HEAT'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L1-STAT' LIMIT 1), '加热与放空', 'EQCAT-L2-STAT-HEATERS',
  'equipment', 5,
  1, 1, 'seed', 'EQCAT-L1-STAT'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-STAT-HEATERS' LIMIT 1), '电加热器', 'EQCAT-DEV-ELECTRIC-HEATER',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L2-STAT-HEATERS'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-STAT-HEATERS' LIMIT 1), '放空立管', 'EQCAT-DEV-BLOWDOWN',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L2-STAT-HEATERS'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-STAT-HEATERS' LIMIT 1), '火炬', 'EQCAT-DEV-FLARE',
  'equipment', 3,
  1, 1, 'seed', 'EQCAT-L2-STAT-HEATERS'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L1-STAT' LIMIT 1), '工艺阀门', 'EQCAT-L2-STAT-VALVE',
  'equipment', 6,
  1, 1, 'seed', 'EQCAT-L1-STAT'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-STAT-VALVE' LIMIT 1), '闸阀', 'EQCAT-DEV-VALVE-GATE',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L2-STAT-VALVE'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-STAT-VALVE' LIMIT 1), '球阀', 'EQCAT-DEV-VALVE-BALL',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L2-STAT-VALVE'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-STAT-VALVE' LIMIT 1), '蝶阀', 'EQCAT-DEV-VALVE-BUTTERFLY',
  'equipment', 3,
  1, 1, 'seed', 'EQCAT-L2-STAT-VALVE'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-STAT-VALVE' LIMIT 1), '止回阀', 'EQCAT-DEV-VALVE-CHECK',
  'equipment', 4,
  1, 1, 'seed', 'EQCAT-L2-STAT-VALVE'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-STAT-VALVE' LIMIT 1), '截止阀', 'EQCAT-DEV-VALVE-GLOBE',
  'equipment', 5,
  1, 1, 'seed', 'EQCAT-L2-STAT-VALVE'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-STAT-VALVE' LIMIT 1), '旋塞阀', 'EQCAT-DEV-VALVE-PLUG',
  'equipment', 6,
  1, 1, 'seed', 'EQCAT-L2-STAT-VALVE'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-STAT-VALVE' LIMIT 1), '隔膜阀', 'EQCAT-DEV-VALVE-DIAPHRAGM',
  'equipment', 7,
  1, 1, 'seed', 'EQCAT-L2-STAT-VALVE'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-STAT-VALVE' LIMIT 1), '安全阀', 'EQCAT-DEV-VALVE-SAFETY',
  'equipment', 8,
  1, 1, 'seed', 'EQCAT-L2-STAT-VALVE'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-STAT-VALVE' LIMIT 1), '减压阀', 'EQCAT-DEV-VALVE-RELIEF',
  'equipment', 9,
  1, 1, 'seed', 'EQCAT-L2-STAT-VALVE'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-STAT-VALVE' LIMIT 1), '自控阀门', 'EQCAT-DEV-VALVE-CTRL',
  'equipment', 10,
  1, 1, 'seed', 'EQCAT-L2-STAT-VALVE'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L1-STAT' LIMIT 1), '工艺管道与管件', 'EQCAT-L2-STAT-PIPE',
  'equipment', 7,
  1, 1, 'seed', 'EQCAT-L1-STAT'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-STAT-PIPE' LIMIT 1), '工艺管道', 'EQCAT-DEV-PIPE-PROCESS',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L2-STAT-PIPE'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-STAT-PIPE' LIMIT 1), '弯头', 'EQCAT-DEV-FITTING-ELBOW',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L2-STAT-PIPE'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-STAT-PIPE' LIMIT 1), '三通', 'EQCAT-DEV-FITTING-TEE',
  'equipment', 3,
  1, 1, 'seed', 'EQCAT-L2-STAT-PIPE'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-STAT-PIPE' LIMIT 1), '大小头', 'EQCAT-DEV-FITTING-REDUCER',
  'equipment', 4,
  1, 1, 'seed', 'EQCAT-L2-STAT-PIPE'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-STAT-PIPE' LIMIT 1), '管帽', 'EQCAT-DEV-FITTING-CAP',
  'equipment', 5,
  1, 1, 'seed', 'EQCAT-L2-STAT-PIPE'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-STAT-PIPE' LIMIT 1), '管塞', 'EQCAT-DEV-FITTING-PLUG',
  'equipment', 6,
  1, 1, 'seed', 'EQCAT-L2-STAT-PIPE'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-STAT-PIPE' LIMIT 1), '管箍', 'EQCAT-DEV-FITTING-COUP',
  'equipment', 7,
  1, 1, 'seed', 'EQCAT-L2-STAT-PIPE'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-STAT-PIPE' LIMIT 1), '支管台', 'EQCAT-DEV-FITTING-OLET',
  'equipment', 8,
  1, 1, 'seed', 'EQCAT-L2-STAT-PIPE'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-STAT-PIPE' LIMIT 1), '法兰', 'EQCAT-DEV-FLANGE',
  'equipment', 9,
  1, 1, 'seed', 'EQCAT-L2-STAT-PIPE'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-STAT-PIPE' LIMIT 1), '垫片', 'EQCAT-DEV-GASKET',
  'equipment', 10,
  1, 1, 'seed', 'EQCAT-L2-STAT-PIPE'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-STAT-PIPE' LIMIT 1), '8字盲板', 'EQCAT-DEV-BLIND-SPECTACLE',
  'equipment', 11,
  1, 1, 'seed', 'EQCAT-L2-STAT-PIPE'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-STAT-PIPE' LIMIT 1), '插板', 'EQCAT-DEV-BLIND-SLIP',
  'equipment', 12,
  1, 1, 'seed', 'EQCAT-L2-STAT-PIPE'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-STAT-PIPE' LIMIT 1), '垫环', 'EQCAT-DEV-BLIND-SPACER',
  'equipment', 13,
  1, 1, 'seed', 'EQCAT-L2-STAT-PIPE'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'equipment_root' LIMIT 1), '管道完整性监测', 'EQCAT-L1-PIM',
  'equipment', 6,
  1, 1, 'seed', NULL
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L1-PIM' LIMIT 1), '泄漏监测', 'EQCAT-L2-PIM-LEAK',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L1-PIM'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-PIM-LEAK' LIMIT 1), '气体泄漏检测系统', 'EQCAT-DEV-LEAK-GAS',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L2-PIM-LEAK'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-PIM-LEAK' LIMIT 1), '液体泄漏监测系统', 'EQCAT-DEV-LEAK-LIQUID',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L2-PIM-LEAK'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L1-PIM' LIMIT 1), '光纤预警与光缆监测', 'EQCAT-L2-PIM-FIBER',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L1-PIM'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-PIM-FIBER' LIMIT 1), '光纤振动预警系统', 'EQCAT-DEV-FIBER-VIB',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L2-PIM-FIBER'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-PIM-FIBER' LIMIT 1), '光缆在线监测RTU', 'EQCAT-DEV-FIBER-RTU',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L2-PIM-FIBER'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-PIM-FIBER' LIMIT 1), '光缆监测耦合单元', 'EQCAT-DEV-FIBER-FCM',
  'equipment', 3,
  1, 1, 'seed', 'EQCAT-L2-PIM-FIBER'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-PIM-FIBER' LIMIT 1), '光缆监测波分单元', 'EQCAT-DEV-FIBER-WDM',
  'equipment', 4,
  1, 1, 'seed', 'EQCAT-L2-PIM-FIBER'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-PIM-FIBER' LIMIT 1), '光线路保护单元', 'EQCAT-DEV-FIBER-OLP',
  'equipment', 5,
  1, 1, 'seed', 'EQCAT-L2-PIM-FIBER'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-PIM-FIBER' LIMIT 1), '光交叉连接设备', 'EQCAT-DEV-FIBER-OXC',
  'equipment', 6,
  1, 1, 'seed', 'EQCAT-L2-PIM-FIBER'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-PIM-FIBER' LIMIT 1), '光缆在线监测管理软件', 'EQCAT-DEV-FIBER-NMS',
  'equipment', 7,
  1, 1, 'seed', 'EQCAT-L2-PIM-FIBER'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L1-PIM' LIMIT 1), '地质灾害监测', 'EQCAT-L2-PIM-GEO',
  'equipment', 3,
  1, 1, 'seed', 'EQCAT-L1-PIM'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-PIM-GEO' LIMIT 1), '地灾监测系统', 'EQCAT-DEV-GEO-HAZARD',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L2-PIM-GEO'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-PIM-GEO' LIMIT 1), '位移监测仪', 'EQCAT-DEV-DISPLACEMENT',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L2-PIM-GEO'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-PIM-GEO' LIMIT 1), '倾斜监测仪', 'EQCAT-DEV-INCLINOMETER',
  'equipment', 3,
  1, 1, 'seed', 'EQCAT-L2-PIM-GEO'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L1-PIM' LIMIT 1), '智能阴极保护', 'EQCAT-L2-PIM-CP',
  'equipment', 4,
  1, 1, 'seed', 'EQCAT-L1-PIM'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-PIM-CP' LIMIT 1), '阴极保护测试桩', 'EQCAT-DEV-CP-TEST-POST',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L2-PIM-CP'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-PIM-CP' LIMIT 1), '阴保电源', 'EQCAT-DEV-CP-POWER',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L2-PIM-CP'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-PIM-CP' LIMIT 1), '牺牲阳极', 'EQCAT-DEV-CP-ANODE',
  'equipment', 3,
  1, 1, 'seed', 'EQCAT-L2-PIM-CP'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-PIM-CP' LIMIT 1), '辅助阳极地床', 'EQCAT-DEV-CP-GROUND-BED',
  'equipment', 4,
  1, 1, 'seed', 'EQCAT-L2-PIM-CP'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-PIM-CP' LIMIT 1), '排流装置', 'EQCAT-DEV-CP-DRAIN',
  'equipment', 5,
  1, 1, 'seed', 'EQCAT-L2-PIM-CP'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-PIM-CP' LIMIT 1), '阴保电缆', 'EQCAT-DEV-CP-CABLE',
  'equipment', 6,
  1, 1, 'seed', 'EQCAT-L2-PIM-CP'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L1-PIM' LIMIT 1), '应力应变监测', 'EQCAT-L2-PIM-STRESS',
  'equipment', 5,
  1, 1, 'seed', 'EQCAT-L1-PIM'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-PIM-STRESS' LIMIT 1), '应力应变监测系统', 'EQCAT-DEV-STRESS-MON',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L2-PIM-STRESS'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-PIM-STRESS' LIMIT 1), '应变计', 'EQCAT-DEV-STRAIN-GAGE',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L2-PIM-STRESS'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-PIM-STRESS' LIMIT 1), '光纤应变传感器', 'EQCAT-DEV-FIBER-STRAIN',
  'equipment', 3,
  1, 1, 'seed', 'EQCAT-L2-PIM-STRESS'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'equipment_root' LIMIT 1), '暖通空调系统', 'EQCAT-L1-HVAC',
  'equipment', 7,
  1, 1, 'seed', NULL
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L1-HVAC' LIMIT 1), '冷热源', 'EQCAT-L2-HVAC-CHILL',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L1-HVAC'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-HVAC-CHILL' LIMIT 1), '冷水机组', 'EQCAT-DEV-CHILLER',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L2-HVAC-CHILL'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-HVAC-CHILL' LIMIT 1), '冷却塔', 'EQCAT-DEV-COOLING-TOWER',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L2-HVAC-CHILL'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-HVAC-CHILL' LIMIT 1), '锅炉', 'EQCAT-DEV-BOILER',
  'equipment', 3,
  1, 1, 'seed', 'EQCAT-L2-HVAC-CHILL'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-HVAC-CHILL' LIMIT 1), '热泵机组', 'EQCAT-DEV-HEAT-PUMP',
  'equipment', 4,
  1, 1, 'seed', 'EQCAT-L2-HVAC-CHILL'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L1-HVAC' LIMIT 1), '空气处理机组', 'EQCAT-L2-HVAC-AHU',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L1-HVAC'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-HVAC-AHU' LIMIT 1), '组合式空调机组', 'EQCAT-DEV-AHU',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L2-HVAC-AHU'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-HVAC-AHU' LIMIT 1), '新风机组', 'EQCAT-DEV-PAU',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L2-HVAC-AHU'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-HVAC-AHU' LIMIT 1), '风机盘管', 'EQCAT-DEV-FCU',
  'equipment', 3,
  1, 1, 'seed', 'EQCAT-L2-HVAC-AHU'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L1-HVAC' LIMIT 1), '通风与风阀', 'EQCAT-L2-HVAC-DUCT',
  'equipment', 3,
  1, 1, 'seed', 'EQCAT-L1-HVAC'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-HVAC-DUCT' LIMIT 1), '防火阀', 'EQCAT-DEV-DAMPER-FIRE',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L2-HVAC-DUCT'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-HVAC-DUCT' LIMIT 1), '调节风阀', 'EQCAT-DEV-DAMPER-ADJ',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L2-HVAC-DUCT'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-HVAC-DUCT' LIMIT 1), '消声器', 'EQCAT-DEV-MUFFLER',
  'equipment', 3,
  1, 1, 'seed', 'EQCAT-L2-HVAC-DUCT'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'equipment_root' LIMIT 1), '给排水系统', 'EQCAT-L1-PLUM',
  'equipment', 8,
  1, 1, 'seed', NULL
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L1-PLUM' LIMIT 1), '给水设备', 'EQCAT-L2-PLUM-SUPPLY',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L1-PLUM'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-PLUM-SUPPLY' LIMIT 1), '给水泵', 'EQCAT-DEV-PUMP-SUPPLY',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L2-PLUM-SUPPLY'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-PLUM-SUPPLY' LIMIT 1), '水箱', 'EQCAT-DEV-WATER-TANK',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L2-PLUM-SUPPLY'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-PLUM-SUPPLY' LIMIT 1), '加压泵站', 'EQCAT-DEV-PRESSURE-STATION',
  'equipment', 3,
  1, 1, 'seed', 'EQCAT-L2-PLUM-SUPPLY'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L1-PLUM' LIMIT 1), '排水设备', 'EQCAT-L2-PLUM-DRAIN',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L1-PLUM'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-PLUM-DRAIN' LIMIT 1), '排水泵', 'EQCAT-DEV-PUMP-DRAIN',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L2-PLUM-DRAIN'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-PLUM-DRAIN' LIMIT 1), '集水坑', 'EQCAT-DEV-SUMP',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L2-PLUM-DRAIN'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L1-PLUM' LIMIT 1), '建筑阀门', 'EQCAT-L2-PLUM-VALVE',
  'equipment', 3,
  1, 1, 'seed', 'EQCAT-L1-PLUM'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-PLUM-VALVE' LIMIT 1), '建筑闸阀', 'EQCAT-DEV-PLUM-VALVE-GATE',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L2-PLUM-VALVE'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-PLUM-VALVE' LIMIT 1), '建筑球阀', 'EQCAT-DEV-PLUM-VALVE-BALL',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L2-PLUM-VALVE'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-PLUM-VALVE' LIMIT 1), '建筑蝶阀', 'EQCAT-DEV-PLUM-VALVE-BUTTERFLY',
  'equipment', 3,
  1, 1, 'seed', 'EQCAT-L2-PLUM-VALVE'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-PLUM-VALVE' LIMIT 1), '建筑止回阀', 'EQCAT-DEV-PLUM-VALVE-CHECK',
  'equipment', 4,
  1, 1, 'seed', 'EQCAT-L2-PLUM-VALVE'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L1-PLUM' LIMIT 1), '建筑给排水管道', 'EQCAT-L2-PLUM-PIPE',
  'equipment', 4,
  1, 1, 'seed', 'EQCAT-L1-PLUM'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-PLUM-PIPE' LIMIT 1), '再生水管', 'EQCAT-DEV-PIPE-RECLAIM',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L2-PLUM-PIPE'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-PLUM-PIPE' LIMIT 1), '生活给水管', 'EQCAT-DEV-PIPE-DOMESTIC',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L2-PLUM-PIPE'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-PLUM-PIPE' LIMIT 1), '冷热供水管', 'EQCAT-DEV-PIPE-HOT-COLD',
  'equipment', 3,
  1, 1, 'seed', 'EQCAT-L2-PLUM-PIPE'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-PLUM-PIPE' LIMIT 1), '排水管', 'EQCAT-DEV-PIPE-DRAIN',
  'equipment', 4,
  1, 1, 'seed', 'EQCAT-L2-PLUM-PIPE'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'equipment_root' LIMIT 1), '消防系统', 'EQCAT-L1-FIRE',
  'equipment', 9,
  1, 1, 'seed', NULL
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L1-FIRE' LIMIT 1), '火灾自动报警', 'EQCAT-L2-FIRE-ALARM',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L1-FIRE'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-FIRE-ALARM' LIMIT 1), '火灾报警控制器', 'EQCAT-DEV-FIRE-ALARM-HOST',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L2-FIRE-ALARM'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-FIRE-ALARM' LIMIT 1), '感烟探测器', 'EQCAT-DEV-SMOKE-DET',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L2-FIRE-ALARM'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-FIRE-ALARM' LIMIT 1), '感温探测器', 'EQCAT-DEV-HEAT-DET',
  'equipment', 3,
  1, 1, 'seed', 'EQCAT-L2-FIRE-ALARM'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-FIRE-ALARM' LIMIT 1), '火焰探测器', 'EQCAT-DEV-FLAME-DET',
  'equipment', 4,
  1, 1, 'seed', 'EQCAT-L2-FIRE-ALARM'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-FIRE-ALARM' LIMIT 1), '手动报警按钮', 'EQCAT-DEV-MANUAL-ALARM',
  'equipment', 5,
  1, 1, 'seed', 'EQCAT-L2-FIRE-ALARM'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L1-FIRE' LIMIT 1), '灭火设备', 'EQCAT-L2-FIRE-SUPPRESS',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L1-FIRE'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-FIRE-SUPPRESS' LIMIT 1), '气溶胶灭火装置', 'EQCAT-DEV-AEROSOL',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L2-FIRE-SUPPRESS'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-FIRE-SUPPRESS' LIMIT 1), '气体灭火装置', 'EQCAT-DEV-GAS-SUPPRESS',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L2-FIRE-SUPPRESS'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-FIRE-SUPPRESS' LIMIT 1), '自动喷水灭火设备', 'EQCAT-DEV-SPRINKLER',
  'equipment', 3,
  1, 1, 'seed', 'EQCAT-L2-FIRE-SUPPRESS'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-FIRE-SUPPRESS' LIMIT 1), '消火栓', 'EQCAT-DEV-FIRE-HYDRANT',
  'equipment', 4,
  1, 1, 'seed', 'EQCAT-L2-FIRE-SUPPRESS'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-FIRE-SUPPRESS' LIMIT 1), '灭火器', 'EQCAT-DEV-FIRE-EXT',
  'equipment', 5,
  1, 1, 'seed', 'EQCAT-L2-FIRE-SUPPRESS'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-FIRE-SUPPRESS' LIMIT 1), '消防箱', 'EQCAT-DEV-FIRE-BOX',
  'equipment', 6,
  1, 1, 'seed', 'EQCAT-L2-FIRE-SUPPRESS'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L1-FIRE' LIMIT 1), '消防供水', 'EQCAT-L2-FIRE-WATER',
  'equipment', 3,
  1, 1, 'seed', 'EQCAT-L1-FIRE'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-FIRE-WATER' LIMIT 1), '消防泵', 'EQCAT-DEV-PUMP-FIRE',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L2-FIRE-WATER'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-FIRE-WATER' LIMIT 1), '消防水池', 'EQCAT-DEV-FIRE-TANK',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L2-FIRE-WATER'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L1-FIRE' LIMIT 1), '防烟排烟', 'EQCAT-L2-FIRE-SMOKE',
  'equipment', 4,
  1, 1, 'seed', 'EQCAT-L1-FIRE'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-FIRE-SMOKE' LIMIT 1), '排烟风机', 'EQCAT-DEV-EXHAUST-FAN',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L2-FIRE-SMOKE'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-FIRE-SMOKE' LIMIT 1), '正压送风机', 'EQCAT-DEV-PRESS-FAN',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L2-FIRE-SMOKE'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L1-FIRE' LIMIT 1), '应急疏散', 'EQCAT-L2-FIRE-EVAC',
  'equipment', 5,
  1, 1, 'seed', 'EQCAT-L1-FIRE'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-FIRE-EVAC' LIMIT 1), '应急照明灯具', 'EQCAT-DEV-EMERGENCY-LIGHT',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L2-FIRE-EVAC'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-FIRE-EVAC' LIMIT 1), '疏散指示标志', 'EQCAT-DEV-EXIT-SIGN',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L2-FIRE-EVAC'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'equipment_root' LIMIT 1), '建筑智能化系统', 'EQCAT-L1-BMS',
  'equipment', 10,
  1, 1, 'seed', NULL
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L1-BMS' LIMIT 1), '视频监控', 'EQCAT-L2-BMS-VIDEO',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L1-BMS'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-VIDEO' LIMIT 1), '网络摄像机', 'EQCAT-DEV-CAMERA-IP',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L2-BMS-VIDEO'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-VIDEO' LIMIT 1), '半球摄像机', 'EQCAT-DEV-CAMERA-DOME',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L2-BMS-VIDEO'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-VIDEO' LIMIT 1), '云台摄像机', 'EQCAT-DEV-CAMERA-PTZ',
  'equipment', 3,
  1, 1, 'seed', 'EQCAT-L2-BMS-VIDEO'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-VIDEO' LIMIT 1), '枪型摄像机', 'EQCAT-DEV-CAMERA-BULLET',
  'equipment', 4,
  1, 1, 'seed', 'EQCAT-L2-BMS-VIDEO'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-VIDEO' LIMIT 1), '防爆摄像机', 'EQCAT-DEV-CAMERA-EX',
  'equipment', 5,
  1, 1, 'seed', 'EQCAT-L2-BMS-VIDEO'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-VIDEO' LIMIT 1), '工业电视摄像机', 'EQCAT-DEV-INDUSTRIAL-TV',
  'equipment', 6,
  1, 1, 'seed', 'EQCAT-L2-BMS-VIDEO'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-VIDEO' LIMIT 1), '网络硬盘录像机', 'EQCAT-DEV-NVR',
  'equipment', 7,
  1, 1, 'seed', 'EQCAT-L2-BMS-VIDEO'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-VIDEO' LIMIT 1), '硬盘录像机', 'EQCAT-DEV-DVR',
  'equipment', 8,
  1, 1, 'seed', 'EQCAT-L2-BMS-VIDEO'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-VIDEO' LIMIT 1), '视频管理服务器', 'EQCAT-DEV-VIDEO-SERVER',
  'equipment', 9,
  1, 1, 'seed', 'EQCAT-L2-BMS-VIDEO'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-VIDEO' LIMIT 1), '流媒体服务器', 'EQCAT-DEV-STREAM-SERVER',
  'equipment', 10,
  1, 1, 'seed', 'EQCAT-L2-BMS-VIDEO'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-VIDEO' LIMIT 1), '视频存储服务器', 'EQCAT-DEV-STORAGE-SERVER',
  'equipment', 11,
  1, 1, 'seed', 'EQCAT-L2-BMS-VIDEO'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-VIDEO' LIMIT 1), '视频编码器', 'EQCAT-DEV-VIDEO-ENCODER',
  'equipment', 12,
  1, 1, 'seed', 'EQCAT-L2-BMS-VIDEO'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-VIDEO' LIMIT 1), '智能视频分析服务器', 'EQCAT-DEV-VIDEO-ANALYTICS',
  'equipment', 13,
  1, 1, 'seed', 'EQCAT-L2-BMS-VIDEO'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-VIDEO' LIMIT 1), '机器视觉推理服务器', 'EQCAT-DEV-MACHINE-VISION-SERVER',
  'equipment', 14,
  1, 1, 'seed', 'EQCAT-L2-BMS-VIDEO'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-VIDEO' LIMIT 1), '视频监控工作站', 'EQCAT-DEV-VIDEO-WORKSTATION',
  'equipment', 15,
  1, 1, 'seed', 'EQCAT-L2-BMS-VIDEO'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-VIDEO' LIMIT 1), '监视器', 'EQCAT-DEV-MONITOR',
  'equipment', 16,
  1, 1, 'seed', 'EQCAT-L2-BMS-VIDEO'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-VIDEO' LIMIT 1), '监控存储硬盘', 'EQCAT-DEV-STORAGE-DISK',
  'equipment', 17,
  1, 1, 'seed', 'EQCAT-L2-BMS-VIDEO'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-VIDEO' LIMIT 1), '布控球', 'EQCAT-DEV-MOBILE-DOME',
  'equipment', 18,
  1, 1, 'seed', 'EQCAT-L2-BMS-VIDEO'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-VIDEO' LIMIT 1), '监控补光灯', 'EQCAT-DEV-CAMERA-LIGHT',
  'equipment', 19,
  1, 1, 'seed', 'EQCAT-L2-BMS-VIDEO'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-VIDEO' LIMIT 1), '监控立杆', 'EQCAT-DEV-CAMERA-POLE',
  'equipment', 20,
  1, 1, 'seed', 'EQCAT-L2-BMS-VIDEO'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-VIDEO' LIMIT 1), '视频监控防雷器', 'EQCAT-DEV-SPD-VIDEO',
  'equipment', 21,
  1, 1, 'seed', 'EQCAT-L2-BMS-VIDEO'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L1-BMS' LIMIT 1), '大屏与显控', 'EQCAT-L2-BMS-DISPLAY',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L1-BMS'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-DISPLAY' LIMIT 1), 'LED显示屏', 'EQCAT-DEV-LED-PANEL',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L2-BMS-DISPLAY'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-DISPLAY' LIMIT 1), '拼接控制器', 'EQCAT-DEV-VIDEO-WALL-CTRL',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L2-BMS-DISPLAY'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-DISPLAY' LIMIT 1), '视频发送卡', 'EQCAT-DEV-VIDEO-SENDER',
  'equipment', 3,
  1, 1, 'seed', 'EQCAT-L2-BMS-DISPLAY'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-DISPLAY' LIMIT 1), '视频解码器', 'EQCAT-DEV-VIDEO-DECODER',
  'equipment', 4,
  1, 1, 'seed', 'EQCAT-L2-BMS-DISPLAY'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-DISPLAY' LIMIT 1), '大屏安装支架', 'EQCAT-DEV-DISPLAY-MOUNT',
  'equipment', 5,
  1, 1, 'seed', 'EQCAT-L2-BMS-DISPLAY'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-DISPLAY' LIMIT 1), 'KVM切换器', 'EQCAT-DEV-KVM',
  'equipment', 6,
  1, 1, 'seed', 'EQCAT-L2-BMS-DISPLAY'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-DISPLAY' LIMIT 1), '机架式控制台', 'EQCAT-DEV-CONSOLE-LCD',
  'equipment', 7,
  1, 1, 'seed', 'EQCAT-L2-BMS-DISPLAY'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L1-BMS' LIMIT 1), '综合安防管理', 'EQCAT-L2-BMS-INTEG',
  'equipment', 3,
  1, 1, 'seed', 'EQCAT-L1-BMS'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-INTEG' LIMIT 1), '综合安防管理平台服务器', 'EQCAT-DEV-SEC-PLATFORM-SERVER',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L2-BMS-INTEG'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-INTEG' LIMIT 1), '综合安防融合应用服务器', 'EQCAT-DEV-SEC-FUSION-SERVER',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L2-BMS-INTEG'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-INTEG' LIMIT 1), '综合安防管理客户端', 'EQCAT-DEV-SEC-PLATFORM-CLIENT',
  'equipment', 3,
  1, 1, 'seed', 'EQCAT-L2-BMS-INTEG'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-INTEG' LIMIT 1), '安防联动控制器', 'EQCAT-DEV-SEC-LINKAGE',
  'equipment', 4,
  1, 1, 'seed', 'EQCAT-L2-BMS-INTEG'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-INTEG' LIMIT 1), '综合安防管理平台软件', 'EQCAT-DEV-SEC-PLATFORM-SOFTWARE',
  'equipment', 5,
  1, 1, 'seed', 'EQCAT-L2-BMS-INTEG'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L1-BMS' LIMIT 1), '入侵报警', 'EQCAT-L2-BMS-INTRUSION',
  'equipment', 4,
  1, 1, 'seed', 'EQCAT-L1-BMS'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-INTRUSION' LIMIT 1), '入侵报警主机', 'EQCAT-DEV-ALARM-HOST',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L2-BMS-INTRUSION'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-INTRUSION' LIMIT 1), '红外探测器', 'EQCAT-DEV-IR-DETECTOR',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L2-BMS-INTRUSION'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-INTRUSION' LIMIT 1), '复合入侵探测器', 'EQCAT-DEV-INTRUSION-DETECTOR',
  'equipment', 3,
  1, 1, 'seed', 'EQCAT-L2-BMS-INTRUSION'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-INTRUSION' LIMIT 1), '玻璃破碎探测器', 'EQCAT-DEV-GLASS-BREAK',
  'equipment', 4,
  1, 1, 'seed', 'EQCAT-L2-BMS-INTRUSION'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-INTRUSION' LIMIT 1), '振动探测器', 'EQCAT-DEV-VIBRATION-DET',
  'equipment', 5,
  1, 1, 'seed', 'EQCAT-L2-BMS-INTRUSION'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-INTRUSION' LIMIT 1), '声光报警器', 'EQCAT-DEV-SOUNDER',
  'equipment', 6,
  1, 1, 'seed', 'EQCAT-L2-BMS-INTRUSION'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-INTRUSION' LIMIT 1), '报警键盘', 'EQCAT-DEV-ALARM-KEYPAD',
  'equipment', 7,
  1, 1, 'seed', 'EQCAT-L2-BMS-INTRUSION'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-INTRUSION' LIMIT 1), '紧急求助按钮', 'EQCAT-DEV-EMERGENCY-BUTTON',
  'equipment', 8,
  1, 1, 'seed', 'EQCAT-L2-BMS-INTRUSION'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-INTRUSION' LIMIT 1), '入侵报警软件', 'EQCAT-DEV-INTRUSION-SOFTWARE',
  'equipment', 9,
  1, 1, 'seed', 'EQCAT-L2-BMS-INTRUSION'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-INTRUSION' LIMIT 1), '入侵报警工作站', 'EQCAT-DEV-INTRUSION-WS',
  'equipment', 10,
  1, 1, 'seed', 'EQCAT-L2-BMS-INTRUSION'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L1-BMS' LIMIT 1), '周界安防', 'EQCAT-L2-BMS-PERIM',
  'equipment', 5,
  1, 1, 'seed', 'EQCAT-L1-BMS'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-PERIM' LIMIT 1), '周界报警主机', 'EQCAT-DEV-PERIM-ALARM',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L2-BMS-PERIM'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-PERIM' LIMIT 1), '振动光纤周界主机', 'EQCAT-DEV-PERIM-FIBER',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L2-BMS-PERIM'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-PERIM' LIMIT 1), '红外对射探测器', 'EQCAT-DEV-IR-BEAM',
  'equipment', 3,
  1, 1, 'seed', 'EQCAT-L2-BMS-PERIM'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-PERIM' LIMIT 1), '微波对射探测器', 'EQCAT-DEV-MICROWAVE-BEAM',
  'equipment', 4,
  1, 1, 'seed', 'EQCAT-L2-BMS-PERIM'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-PERIM' LIMIT 1), '电子围栏', 'EQCAT-DEV-E-FENCE',
  'equipment', 5,
  1, 1, 'seed', 'EQCAT-L2-BMS-PERIM'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-PERIM' LIMIT 1), '张力围栏', 'EQCAT-DEV-TENSION-FENCE',
  'equipment', 6,
  1, 1, 'seed', 'EQCAT-L2-BMS-PERIM'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-PERIM' LIMIT 1), '地埋式振动传感器', 'EQCAT-DEV-BURIED-VIB',
  'equipment', 7,
  1, 1, 'seed', 'EQCAT-L2-BMS-PERIM'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-PERIM' LIMIT 1), '周界雷达', 'EQCAT-DEV-PERIM-RADAR',
  'equipment', 8,
  1, 1, 'seed', 'EQCAT-L2-BMS-PERIM'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-PERIM' LIMIT 1), '激光云台', 'EQCAT-DEV-LASER-PAN',
  'equipment', 9,
  1, 1, 'seed', 'EQCAT-L2-BMS-PERIM'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-PERIM' LIMIT 1), '周界联动摄像机', 'EQCAT-DEV-PERIM-CAMERA',
  'equipment', 10,
  1, 1, 'seed', 'EQCAT-L2-BMS-PERIM'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-PERIM' LIMIT 1), '周界安防管理软件', 'EQCAT-DEV-PERIM-SOFTWARE',
  'equipment', 11,
  1, 1, 'seed', 'EQCAT-L2-BMS-PERIM'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L1-BMS' LIMIT 1), '出入口控制', 'EQCAT-L2-BMS-ACCESS',
  'equipment', 6,
  1, 1, 'seed', 'EQCAT-L1-BMS'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-ACCESS' LIMIT 1), '单门门禁系统', 'EQCAT-DEV-ACCESS-SINGLE',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L2-BMS-ACCESS'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-ACCESS' LIMIT 1), '双门门禁系统', 'EQCAT-DEV-ACCESS-DOUBLE',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L2-BMS-ACCESS'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-ACCESS' LIMIT 1), '门禁控制器', 'EQCAT-DEV-ACCESS-CTRL',
  'equipment', 3,
  1, 1, 'seed', 'EQCAT-L2-BMS-ACCESS'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-ACCESS' LIMIT 1), '门禁读卡器', 'EQCAT-DEV-ACCESS-READER',
  'equipment', 4,
  1, 1, 'seed', 'EQCAT-L2-BMS-ACCESS'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-ACCESS' LIMIT 1), '门禁电锁', 'EQCAT-DEV-ACCESS-LOCK',
  'equipment', 5,
  1, 1, 'seed', 'EQCAT-L2-BMS-ACCESS'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-ACCESS' LIMIT 1), '开门出门按钮', 'EQCAT-DEV-ACCESS-BUTTON',
  'equipment', 6,
  1, 1, 'seed', 'EQCAT-L2-BMS-ACCESS'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-ACCESS' LIMIT 1), '人行通道闸机', 'EQCAT-DEV-ACCESS-GATE',
  'equipment', 7,
  1, 1, 'seed', 'EQCAT-L2-BMS-ACCESS'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-ACCESS' LIMIT 1), '三辊闸摆闸', 'EQCAT-DEV-TURNSTILE',
  'equipment', 8,
  1, 1, 'seed', 'EQCAT-L2-BMS-ACCESS'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-ACCESS' LIMIT 1), '人脸识别终端', 'EQCAT-DEV-FACE-TERMINAL',
  'equipment', 9,
  1, 1, 'seed', 'EQCAT-L2-BMS-ACCESS'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-ACCESS' LIMIT 1), '访客机', 'EQCAT-DEV-VISITOR-KIOSK',
  'equipment', 10,
  1, 1, 'seed', 'EQCAT-L2-BMS-ACCESS'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-ACCESS' LIMIT 1), '发卡器', 'EQCAT-DEV-CARD-DISPENSER',
  'equipment', 11,
  1, 1, 'seed', 'EQCAT-L2-BMS-ACCESS'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-ACCESS' LIMIT 1), '出门释放装置', 'EQCAT-DEV-ACCESS-EXIT-DEVICE',
  'equipment', 12,
  1, 1, 'seed', 'EQCAT-L2-BMS-ACCESS'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-ACCESS' LIMIT 1), '门禁管理服务器', 'EQCAT-DEV-ACCESS-SERVER',
  'equipment', 13,
  1, 1, 'seed', 'EQCAT-L2-BMS-ACCESS'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-ACCESS' LIMIT 1), '门禁管理软件', 'EQCAT-DEV-ACCESS-SOFTWARE',
  'equipment', 14,
  1, 1, 'seed', 'EQCAT-L2-BMS-ACCESS'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L1-BMS' LIMIT 1), '可视对讲', 'EQCAT-L2-BMS-INTERCOM',
  'equipment', 7,
  1, 1, 'seed', 'EQCAT-L1-BMS'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-INTERCOM' LIMIT 1), '可视对讲管理主机', 'EQCAT-DEV-INTERCOM-HOST',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L2-BMS-INTERCOM'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-INTERCOM' LIMIT 1), '门口对讲主机', 'EQCAT-DEV-INTERCOM-OUTDOOR',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L2-BMS-INTERCOM'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-INTERCOM' LIMIT 1), '室内对讲分机', 'EQCAT-DEV-INTERCOM-INDOOR',
  'equipment', 3,
  1, 1, 'seed', 'EQCAT-L2-BMS-INTERCOM'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-INTERCOM' LIMIT 1), '可视对讲服务器', 'EQCAT-DEV-INTERCOM-SERVER',
  'equipment', 4,
  1, 1, 'seed', 'EQCAT-L2-BMS-INTERCOM'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-INTERCOM' LIMIT 1), '可视对讲管理软件', 'EQCAT-DEV-INTERCOM-SOFTWARE',
  'equipment', 5,
  1, 1, 'seed', 'EQCAT-L2-BMS-INTERCOM'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L1-BMS' LIMIT 1), '人员定位', 'EQCAT-L2-BMS-POSITION',
  'equipment', 8,
  1, 1, 'seed', 'EQCAT-L1-BMS'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-POSITION' LIMIT 1), '定位读写主机', 'EQCAT-DEV-POS-READER',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L2-BMS-POSITION'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-POSITION' LIMIT 1), '定位配套附件', 'EQCAT-DEV-POS-ACCESSORY',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L2-BMS-POSITION'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-POSITION' LIMIT 1), '定位移动终端', 'EQCAT-DEV-POS-TERMINAL',
  'equipment', 3,
  1, 1, 'seed', 'EQCAT-L2-BMS-POSITION'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-POSITION' LIMIT 1), '定位系统服务器', 'EQCAT-DEV-POS-SERVER',
  'equipment', 4,
  1, 1, 'seed', 'EQCAT-L2-BMS-POSITION'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-POSITION' LIMIT 1), '定位系统软件', 'EQCAT-DEV-POS-SOFTWARE',
  'equipment', 5,
  1, 1, 'seed', 'EQCAT-L2-BMS-POSITION'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-POSITION' LIMIT 1), '定位系统工作站', 'EQCAT-DEV-POS-WS',
  'equipment', 6,
  1, 1, 'seed', 'EQCAT-L2-BMS-POSITION'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L1-BMS' LIMIT 1), '电子巡更', 'EQCAT-L2-BMS-PATROL',
  'equipment', 9,
  1, 1, 'seed', 'EQCAT-L1-BMS'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-PATROL' LIMIT 1), '巡更读卡器', 'EQCAT-DEV-PATROL-READER',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L2-BMS-PATROL'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-PATROL' LIMIT 1), '巡更棒', 'EQCAT-DEV-PATROL-STICK',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L2-BMS-PATROL'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-PATROL' LIMIT 1), '巡更点', 'EQCAT-DEV-PATROL-POINT',
  'equipment', 3,
  1, 1, 'seed', 'EQCAT-L2-BMS-PATROL'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-PATROL' LIMIT 1), '巡更管理服务器', 'EQCAT-DEV-PATROL-SERVER',
  'equipment', 4,
  1, 1, 'seed', 'EQCAT-L2-BMS-PATROL'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-PATROL' LIMIT 1), '巡更管理软件', 'EQCAT-DEV-PATROL-SOFTWARE',
  'equipment', 5,
  1, 1, 'seed', 'EQCAT-L2-BMS-PATROL'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L1-BMS' LIMIT 1), '无人机巡检', 'EQCAT-L2-BMS-UAV',
  'equipment', 10,
  1, 1, 'seed', 'EQCAT-L1-BMS'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-UAV' LIMIT 1), '巡检无人机', 'EQCAT-DEV-UAV',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L2-BMS-UAV'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-UAV' LIMIT 1), '无人机机库', 'EQCAT-DEV-UAV-DOCK',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L2-BMS-UAV'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-UAV' LIMIT 1), '无人机巡检管理软件', 'EQCAT-DEV-UAV-NMS',
  'equipment', 3,
  1, 1, 'seed', 'EQCAT-L2-BMS-UAV'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L1-BMS' LIMIT 1), '停车场管理', 'EQCAT-L2-BMS-PARKING',
  'equipment', 11,
  1, 1, 'seed', 'EQCAT-L1-BMS'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-PARKING' LIMIT 1), '道闸', 'EQCAT-DEV-PARKING-BARRIER',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L2-BMS-PARKING'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-PARKING' LIMIT 1), '车牌识别摄像机', 'EQCAT-DEV-LPR-CAMERA',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L2-BMS-PARKING'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-PARKING' LIMIT 1), '停车收费终端', 'EQCAT-DEV-PARKING-TERMINAL',
  'equipment', 3,
  1, 1, 'seed', 'EQCAT-L2-BMS-PARKING'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-PARKING' LIMIT 1), '停车收费岗亭', 'EQCAT-DEV-PARKING-BOOTH',
  'equipment', 4,
  1, 1, 'seed', 'EQCAT-L2-BMS-PARKING'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-PARKING' LIMIT 1), '车位信息显示屏', 'EQCAT-DEV-PARKING-DISPLAY',
  'equipment', 5,
  1, 1, 'seed', 'EQCAT-L2-BMS-PARKING'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-PARKING' LIMIT 1), '车位引导系统', 'EQCAT-DEV-PARKING-GUIDANCE',
  'equipment', 6,
  1, 1, 'seed', 'EQCAT-L2-BMS-PARKING'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-PARKING' LIMIT 1), '地磁车位探测器', 'EQCAT-DEV-PARKING-MAGNET',
  'equipment', 7,
  1, 1, 'seed', 'EQCAT-L2-BMS-PARKING'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-PARKING' LIMIT 1), '停车场管理服务器', 'EQCAT-DEV-PARKING-SERVER',
  'equipment', 8,
  1, 1, 'seed', 'EQCAT-L2-BMS-PARKING'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-PARKING' LIMIT 1), '停车场管理软件', 'EQCAT-DEV-PARKING-SOFTWARE',
  'equipment', 9,
  1, 1, 'seed', 'EQCAT-L2-BMS-PARKING'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L1-BMS' LIMIT 1), '公共广播', 'EQCAT-L2-BMS-PA',
  'equipment', 12,
  1, 1, 'seed', 'EQCAT-L1-BMS'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-PA' LIMIT 1), '广播主机', 'EQCAT-DEV-PA-HOST',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L2-BMS-PA'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-PA' LIMIT 1), '广播功放', 'EQCAT-DEV-PA-AMP',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L2-BMS-PA'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-PA' LIMIT 1), '广播扬声器', 'EQCAT-DEV-PA-SPEAKER',
  'equipment', 3,
  1, 1, 'seed', 'EQCAT-L2-BMS-PA'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-PA' LIMIT 1), '防爆扩音扬声器', 'EQCAT-DEV-PA-EX-SPEAKER',
  'equipment', 4,
  1, 1, 'seed', 'EQCAT-L2-BMS-PA'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-PA' LIMIT 1), '广播终端', 'EQCAT-DEV-PA-TERMINAL',
  'equipment', 5,
  1, 1, 'seed', 'EQCAT-L2-BMS-PA'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-PA' LIMIT 1), '应急广播话筒', 'EQCAT-DEV-PA-EMERGENCY',
  'equipment', 6,
  1, 1, 'seed', 'EQCAT-L2-BMS-PA'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-PA' LIMIT 1), '消防广播联动模块', 'EQCAT-DEV-PA-FIRE-LINK',
  'equipment', 7,
  1, 1, 'seed', 'EQCAT-L2-BMS-PA'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-PA' LIMIT 1), '广播管理软件', 'EQCAT-DEV-PA-SOFTWARE',
  'equipment', 8,
  1, 1, 'seed', 'EQCAT-L2-BMS-PA'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L1-BMS' LIMIT 1), '一卡通与考勤', 'EQCAT-L2-BMS-CARD',
  'equipment', 13,
  1, 1, 'seed', 'EQCAT-L1-BMS'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-CARD' LIMIT 1), '考勤终端', 'EQCAT-DEV-ATTENDANCE-TERMINAL',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L2-BMS-CARD'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-CARD' LIMIT 1), '消费终端', 'EQCAT-DEV-CONSUME-TERMINAL',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L2-BMS-CARD'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-CARD' LIMIT 1), '一卡通管理服务器', 'EQCAT-DEV-ONE-CARD-SERVER',
  'equipment', 3,
  1, 1, 'seed', 'EQCAT-L2-BMS-CARD'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-CARD' LIMIT 1), '一卡通管理软件', 'EQCAT-DEV-ONE-CARD-SOFTWARE',
  'equipment', 4,
  1, 1, 'seed', 'EQCAT-L2-BMS-CARD'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L1-BMS' LIMIT 1), '安检与违禁品检测', 'EQCAT-L2-BMS-SAFETY',
  'equipment', 14,
  1, 1, 'seed', 'EQCAT-L1-BMS'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-SAFETY' LIMIT 1), '金属探测门', 'EQCAT-DEV-METAL-DETECTOR',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L2-BMS-SAFETY'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-SAFETY' LIMIT 1), 'X光安检机', 'EQCAT-DEV-XRAY-SCANNER',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L2-BMS-SAFETY'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-SAFETY' LIMIT 1), '爆炸物探测器', 'EQCAT-DEV-EXPLOSIVE-DETECTOR',
  'equipment', 3,
  1, 1, 'seed', 'EQCAT-L2-BMS-SAFETY'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L1-BMS' LIMIT 1), '安防传输与机柜', 'EQCAT-L2-BMS-INFRA',
  'equipment', 15,
  1, 1, 'seed', 'EQCAT-L1-BMS'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-INFRA' LIMIT 1), '监控控制柜', 'EQCAT-DEV-SECURITY-CABINET',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L2-BMS-INFRA'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-INFRA' LIMIT 1), '光纤收发器', 'EQCAT-DEV-MEDIA-CONVERTER',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L2-BMS-INFRA'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-INFRA' LIMIT 1), '监控专用电源箱', 'EQCAT-DEV-POWER-BOX-CCTV',
  'equipment', 3,
  1, 1, 'seed', 'EQCAT-L2-BMS-INFRA'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-INFRA' LIMIT 1), '监控接线箱', 'EQCAT-DEV-JUNCTION-BOX-CCTV',
  'equipment', 4,
  1, 1, 'seed', 'EQCAT-L2-BMS-INFRA'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-INFRA' LIMIT 1), '视频配线架', 'EQCAT-DEV-VIDEO-PATCH-PANEL',
  'equipment', 5,
  1, 1, 'seed', 'EQCAT-L2-BMS-INFRA'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-BMS-INFRA' LIMIT 1), '安防专用交换机', 'EQCAT-DEV-SECURITY-SW',
  'equipment', 6,
  1, 1, 'seed', 'EQCAT-L2-BMS-INFRA'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'equipment_root' LIMIT 1), '信息通信系统', 'EQCAT-L1-ICT',
  'equipment', 11,
  1, 1, 'seed', NULL
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L1-ICT' LIMIT 1), '数据通信网络', 'EQCAT-L2-ICT-DATA',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L1-ICT'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-ICT-DATA' LIMIT 1), '核心交换机', 'EQCAT-DEV-SW-CORE',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L2-ICT-DATA'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-ICT-DATA' LIMIT 1), '接入交换机', 'EQCAT-DEV-SW-ACCESS',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L2-ICT-DATA'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-ICT-DATA' LIMIT 1), '环网交换机', 'EQCAT-DEV-SW-RING',
  'equipment', 3,
  1, 1, 'seed', 'EQCAT-L2-ICT-DATA'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-ICT-DATA' LIMIT 1), '工业以太网交换机', 'EQCAT-DEV-SW-INDUST',
  'equipment', 4,
  1, 1, 'seed', 'EQCAT-L2-ICT-DATA'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-ICT-DATA' LIMIT 1), '路由器', 'EQCAT-DEV-NET-ROUTER',
  'equipment', 5,
  1, 1, 'seed', 'EQCAT-L2-ICT-DATA'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-ICT-DATA' LIMIT 1), '上网行为管理', 'EQCAT-DEV-NET-UTM',
  'equipment', 6,
  1, 1, 'seed', 'EQCAT-L2-ICT-DATA'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-ICT-DATA' LIMIT 1), '防火墙', 'EQCAT-DEV-NET-FW',
  'equipment', 7,
  1, 1, 'seed', 'EQCAT-L2-ICT-DATA'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-ICT-DATA' LIMIT 1), '光模块', 'EQCAT-DEV-OPTICAL-MODULE',
  'equipment', 8,
  1, 1, 'seed', 'EQCAT-L2-ICT-DATA'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L1-ICT' LIMIT 1), 'IP语音通信', 'EQCAT-L2-ICT-VOICE',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L1-ICT'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-ICT-VOICE' LIMIT 1), 'IP电话终端', 'EQCAT-DEV-IP-PHONE',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L2-ICT-VOICE'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-ICT-VOICE' LIMIT 1), '语音网关', 'EQCAT-DEV-VOIP-GW',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L2-ICT-VOICE'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-ICT-VOICE' LIMIT 1), 'IP语音交换服务器', 'EQCAT-DEV-VOIP-SERVER',
  'equipment', 3,
  1, 1, 'seed', 'EQCAT-L2-ICT-VOICE'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-ICT-VOICE' LIMIT 1), 'IP电话控制主机', 'EQCAT-DEV-VOIP-CTRL',
  'equipment', 4,
  1, 1, 'seed', 'EQCAT-L2-ICT-VOICE'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-ICT-VOICE' LIMIT 1), 'IP电话系统软件', 'EQCAT-DEV-VOIP-SOFTWARE',
  'equipment', 5,
  1, 1, 'seed', 'EQCAT-L2-ICT-VOICE'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-ICT-VOICE' LIMIT 1), '语音通信工作站', 'EQCAT-DEV-VOIP-WS',
  'equipment', 6,
  1, 1, 'seed', 'EQCAT-L2-ICT-VOICE'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L1-ICT' LIMIT 1), '无线对讲', 'EQCAT-L2-ICT-RADIO',
  'equipment', 3,
  1, 1, 'seed', 'EQCAT-L1-ICT'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-ICT-RADIO' LIMIT 1), '对讲中继主机', 'EQCAT-DEV-RADIO-REPEATER',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L2-ICT-RADIO'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-ICT-RADIO' LIMIT 1), '对讲远端设备', 'EQCAT-DEV-RADIO-REMOTE',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L2-ICT-RADIO'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-ICT-RADIO' LIMIT 1), '对讲耦合器', 'EQCAT-DEV-RADIO-COUPLER',
  'equipment', 3,
  1, 1, 'seed', 'EQCAT-L2-ICT-RADIO'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-ICT-RADIO' LIMIT 1), '对讲功分器', 'EQCAT-DEV-RADIO-SPLITTER',
  'equipment', 4,
  1, 1, 'seed', 'EQCAT-L2-ICT-RADIO'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-ICT-RADIO' LIMIT 1), '对讲天线', 'EQCAT-DEV-RADIO-ANTENNA',
  'equipment', 5,
  1, 1, 'seed', 'EQCAT-L2-ICT-RADIO'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-ICT-RADIO' LIMIT 1), '对讲双工器', 'EQCAT-DEV-RADIO-DUPLEXER',
  'equipment', 6,
  1, 1, 'seed', 'EQCAT-L2-ICT-RADIO'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-ICT-RADIO' LIMIT 1), '对讲手持机', 'EQCAT-DEV-RADIO-HANDSET',
  'equipment', 7,
  1, 1, 'seed', 'EQCAT-L2-ICT-RADIO'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-ICT-RADIO' LIMIT 1), '对讲网管软件', 'EQCAT-DEV-RADIO-NMS',
  'equipment', 8,
  1, 1, 'seed', 'EQCAT-L2-ICT-RADIO'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L1-ICT' LIMIT 1), '通信光缆线路', 'EQCAT-L2-ICT-FIBER',
  'equipment', 4,
  1, 1, 'seed', 'EQCAT-L1-ICT'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-ICT-FIBER' LIMIT 1), '通信光缆', 'EQCAT-DEV-CABLE-COMM',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L2-ICT-FIBER'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-ICT-FIBER' LIMIT 1), '通信电缆', 'EQCAT-DEV-CABLE-LV',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L2-ICT-FIBER'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-ICT-FIBER' LIMIT 1), '光缆接头盒', 'EQCAT-DEV-FIBER-JOINT',
  'equipment', 3,
  1, 1, 'seed', 'EQCAT-L2-ICT-FIBER'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-ICT-FIBER' LIMIT 1), '光纤配线架', 'EQCAT-DEV-ODF',
  'equipment', 4,
  1, 1, 'seed', 'EQCAT-L2-ICT-FIBER'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L1-ICT' LIMIT 1), '机柜与综合布线', 'EQCAT-L2-ICT-RACK',
  'equipment', 5,
  1, 1, 'seed', 'EQCAT-L1-ICT'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-ICT-RACK' LIMIT 1), '服务器机柜', 'EQCAT-DEV-RACK-SERVER',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L2-ICT-RACK'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-ICT-RACK' LIMIT 1), '网络机柜', 'EQCAT-DEV-RACK-NET',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L2-ICT-RACK'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'equipment_root' LIMIT 1), '机房工程与动环监控', 'EQCAT-L1-DCIM',
  'equipment', 12,
  1, 1, 'seed', NULL
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L1-DCIM' LIMIT 1), '机房精密空调', 'EQCAT-L2-DCIM-CRAC',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L1-DCIM'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-DCIM-CRAC' LIMIT 1), '机房专用空调', 'EQCAT-DEV-CRAC',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L2-DCIM-CRAC'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-DCIM-CRAC' LIMIT 1), '行间空调', 'EQCAT-DEV-INROW-AC',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L2-DCIM-CRAC'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L1-DCIM' LIMIT 1), '动力环境监控', 'EQCAT-L2-DCIM-MONITOR',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L1-DCIM'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-DCIM-MONITOR' LIMIT 1), '空调监控模块', 'EQCAT-DEV-DCIM-HVAC',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L2-DCIM-MONITOR'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-DCIM-MONITOR' LIMIT 1), '新风监控模块', 'EQCAT-DEV-DCIM-FRESH-AIR',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L2-DCIM-MONITOR'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-DCIM-MONITOR' LIMIT 1), 'UPS监控模块', 'EQCAT-DEV-DCIM-UPS',
  'equipment', 3,
  1, 1, 'seed', 'EQCAT-L2-DCIM-MONITOR'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-DCIM-MONITOR' LIMIT 1), '动环短信模块', 'EQCAT-DEV-DCIM-SMS',
  'equipment', 4,
  1, 1, 'seed', 'EQCAT-L2-DCIM-MONITOR'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-DCIM-MONITOR' LIMIT 1), '动环监控软件', 'EQCAT-DEV-DCIM-SOFTWARE',
  'equipment', 5,
  1, 1, 'seed', 'EQCAT-L2-DCIM-MONITOR'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-DCIM-MONITOR' LIMIT 1), '动环监控主机', 'EQCAT-DEV-DCIM-HOST',
  'equipment', 6,
  1, 1, 'seed', 'EQCAT-L2-DCIM-MONITOR'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-DCIM-MONITOR' LIMIT 1), '动环管理终端', 'EQCAT-DEV-DCIM-TERMINAL',
  'equipment', 7,
  1, 1, 'seed', 'EQCAT-L2-DCIM-MONITOR'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-DCIM-MONITOR' LIMIT 1), '机房监控服务器', 'EQCAT-DEV-DCIM-SERVER',
  'equipment', 8,
  1, 1, 'seed', 'EQCAT-L2-DCIM-MONITOR'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'equipment_root' LIMIT 1), '计算与信息设备', 'EQCAT-L1-IT',
  'equipment', 13,
  1, 1, 'seed', NULL
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L1-IT' LIMIT 1), '服务器', 'EQCAT-L2-IT-SERVER',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L1-IT'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-IT-SERVER' LIMIT 1), '应用服务器', 'EQCAT-DEV-SERVER-APP',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L2-IT-SERVER'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-IT-SERVER' LIMIT 1), '数据库服务器', 'EQCAT-DEV-SERVER-DB',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L2-IT-SERVER'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-IT-SERVER' LIMIT 1), 'GIS服务器', 'EQCAT-DEV-SERVER-GIS',
  'equipment', 3,
  1, 1, 'seed', 'EQCAT-L2-IT-SERVER'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-IT-SERVER' LIMIT 1), '超融合服务器', 'EQCAT-DEV-SERVER-HCI',
  'equipment', 4,
  1, 1, 'seed', 'EQCAT-L2-IT-SERVER'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-IT-SERVER' LIMIT 1), 'GPU计算节点', 'EQCAT-DEV-SERVER-GPU',
  'equipment', 5,
  1, 1, 'seed', 'EQCAT-L2-IT-SERVER'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L1-IT' LIMIT 1), '终端与外设', 'EQCAT-L2-IT-CLIENT',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L1-IT'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-IT-CLIENT' LIMIT 1), '工作站', 'EQCAT-DEV-WS',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L2-IT-CLIENT'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-IT-CLIENT' LIMIT 1), '便携式计算机', 'EQCAT-DEV-LAPTOP',
  'equipment', 2,
  1, 1, 'seed', 'EQCAT-L2-IT-CLIENT'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-IT-CLIENT' LIMIT 1), '彩色打印机', 'EQCAT-DEV-PRINTER-COLOR',
  'equipment', 3,
  1, 1, 'seed', 'EQCAT-L2-IT-CLIENT'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L2-IT-CLIENT' LIMIT 1), '黑白打印机', 'EQCAT-DEV-PRINTER-MONO',
  'equipment', 4,
  1, 1, 'seed', 'EQCAT-L2-IT-CLIENT'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'equipment_root' LIMIT 1), '其他设备', 'EQCAT-L1-MISC',
  'equipment', 14,
  1, 1, 'seed', NULL
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'EQCAT-L1-MISC' LIMIT 1), '未分类设备', 'EQCAT-DEV-UNCATEGORIZED',
  'equipment', 1,
  1, 1, 'seed', 'EQCAT-L1-MISC'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  parent_code = EXCLUDED.parent_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

