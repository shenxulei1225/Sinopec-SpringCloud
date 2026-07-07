-- ============================================================================
-- 系统 · dynamic_group
-- Generated: 2026-07-08 by scripts/export-platform-import.py
--
-- 约定：不写 surrogate id；幂等键为 code / field_code / page_code。
-- 幂等 upsert；关联字段按 code 解析 id，不写 surrogate id
-- ============================================================================

SET search_path TO dynamicbusiness;

-- dynamic_group(FIELD): 20 row(s), upsert by (group_type, code)

INSERT INTO dynamic_group (
  group_type, code, name, description, parent_id, path, level, sort, status, tenant_id, creator
)
SELECT
  'FIELD', 'FG-BIZ-COMMON', '公共通用',
  '各业务模型均可能用到的基础属性', NULL,
  NULL, 1,
  10, 1,
  1, 'zhgl-seed'
WHERE NOT EXISTS (
  SELECT 1 FROM dynamic_group g
  WHERE g.deleted = false AND g.tenant_id = 1
    AND g.group_type = 'FIELD' AND g.code = 'FG-BIZ-COMMON'
);

UPDATE dynamic_group g SET
  name = '公共通用',
  description = '各业务模型均可能用到的基础属性',
  sort = 10,
  status = 1,
  updater = 'zhgl-seed',
  update_time = CURRENT_TIMESTAMP
WHERE g.deleted = false AND g.tenant_id = 1
  AND g.group_type = 'FIELD' AND g.code = 'FG-BIZ-COMMON';

INSERT INTO dynamic_group (
  group_type, code, name, description, parent_id, path, level, sort, status, tenant_id, creator
)
SELECT
  'FIELD', 'FG-BIZ-EQUIPMENT', '设备管理',
  '设备台账、资产与维保常用字段', NULL,
  NULL, 1,
  20, 1,
  1, 'zhgl-seed'
WHERE NOT EXISTS (
  SELECT 1 FROM dynamic_group g
  WHERE g.deleted = false AND g.tenant_id = 1
    AND g.group_type = 'FIELD' AND g.code = 'FG-BIZ-EQUIPMENT'
);

UPDATE dynamic_group g SET
  name = '设备管理',
  description = '设备台账、资产与维保常用字段',
  sort = 20,
  status = 1,
  updater = 'zhgl-seed',
  update_time = CURRENT_TIMESTAMP
WHERE g.deleted = false AND g.tenant_id = 1
  AND g.group_type = 'FIELD' AND g.code = 'FG-BIZ-EQUIPMENT';

INSERT INTO dynamic_group (
  group_type, code, name, description, parent_id, path, level, sort, status, tenant_id, creator
)
SELECT
  'FIELD', 'FG-BIZ-ALARM', '告警管理',
  '告警事件、处置与气体监测', NULL,
  NULL, 1,
  30, 1,
  1, 'zhgl-seed'
WHERE NOT EXISTS (
  SELECT 1 FROM dynamic_group g
  WHERE g.deleted = false AND g.tenant_id = 1
    AND g.group_type = 'FIELD' AND g.code = 'FG-BIZ-ALARM'
);

UPDATE dynamic_group g SET
  name = '告警管理',
  description = '告警事件、处置与气体监测',
  sort = 30,
  status = 1,
  updater = 'zhgl-seed',
  update_time = CURRENT_TIMESTAMP
WHERE g.deleted = false AND g.tenant_id = 1
  AND g.group_type = 'FIELD' AND g.code = 'FG-BIZ-ALARM';

INSERT INTO dynamic_group (
  group_type, code, name, description, parent_id, path, level, sort, status, tenant_id, creator
)
SELECT
  'FIELD', 'FG-BIZ-INSPECTION', '巡检管理',
  '巡检计划、点检项与结果', NULL,
  NULL, 1,
  40, 1,
  1, 'zhgl-seed'
WHERE NOT EXISTS (
  SELECT 1 FROM dynamic_group g
  WHERE g.deleted = false AND g.tenant_id = 1
    AND g.group_type = 'FIELD' AND g.code = 'FG-BIZ-INSPECTION'
);

UPDATE dynamic_group g SET
  name = '巡检管理',
  description = '巡检计划、点检项与结果',
  sort = 40,
  status = 1,
  updater = 'zhgl-seed',
  update_time = CURRENT_TIMESTAMP
WHERE g.deleted = false AND g.tenant_id = 1
  AND g.group_type = 'FIELD' AND g.code = 'FG-BIZ-INSPECTION';

INSERT INTO dynamic_group (
  group_type, code, name, description, parent_id, path, level, sort, status, tenant_id, creator
)
SELECT
  'FIELD', 'FG-BIZ-TASK', '任务管理',
  '工单、任务派工与验收', NULL,
  NULL, 1,
  50, 1,
  1, 'zhgl-seed'
WHERE NOT EXISTS (
  SELECT 1 FROM dynamic_group g
  WHERE g.deleted = false AND g.tenant_id = 1
    AND g.group_type = 'FIELD' AND g.code = 'FG-BIZ-TASK'
);

UPDATE dynamic_group g SET
  name = '任务管理',
  description = '工单、任务派工与验收',
  sort = 50,
  status = 1,
  updater = 'zhgl-seed',
  update_time = CURRENT_TIMESTAMP
WHERE g.deleted = false AND g.tenant_id = 1
  AND g.group_type = 'FIELD' AND g.code = 'FG-BIZ-TASK';

INSERT INTO dynamic_group (
  group_type, code, name, description, parent_id, path, level, sort, status, tenant_id, creator
)
SELECT
  'FIELD', 'FG-BIZ-LOCATION', '位置空间',
  '地理区域、设施、分区与定位', NULL,
  NULL, 1,
  60, 1,
  1, 'zhgl-seed'
WHERE NOT EXISTS (
  SELECT 1 FROM dynamic_group g
  WHERE g.deleted = false AND g.tenant_id = 1
    AND g.group_type = 'FIELD' AND g.code = 'FG-BIZ-LOCATION'
);

UPDATE dynamic_group g SET
  name = '位置空间',
  description = '地理区域、设施、分区与定位',
  sort = 60,
  status = 1,
  updater = 'zhgl-seed',
  update_time = CURRENT_TIMESTAMP
WHERE g.deleted = false AND g.tenant_id = 1
  AND g.group_type = 'FIELD' AND g.code = 'FG-BIZ-LOCATION';

INSERT INTO dynamic_group (
  group_type, code, name, description, parent_id, path, level, sort, status, tenant_id, creator
)
SELECT
  'FIELD', 'FG-BIZ-GALLERY', '管廊管理',
  '综合管廊与入廊管线', NULL,
  NULL, 1,
  70, 1,
  1, 'zhgl-seed'
WHERE NOT EXISTS (
  SELECT 1 FROM dynamic_group g
  WHERE g.deleted = false AND g.tenant_id = 1
    AND g.group_type = 'FIELD' AND g.code = 'FG-BIZ-GALLERY'
);

UPDATE dynamic_group g SET
  name = '管廊管理',
  description = '综合管廊与入廊管线',
  sort = 70,
  status = 1,
  updater = 'zhgl-seed',
  update_time = CURRENT_TIMESTAMP
WHERE g.deleted = false AND g.tenant_id = 1
  AND g.group_type = 'FIELD' AND g.code = 'FG-BIZ-GALLERY';

INSERT INTO dynamic_group (
  group_type, code, name, description, parent_id, path, level, sort, status, tenant_id, creator
)
SELECT
  'FIELD', 'FG-BIZ-PERSONNEL', '人员组织',
  '人员、部门与联系方式', NULL,
  NULL, 1,
  80, 1,
  1, 'zhgl-seed'
WHERE NOT EXISTS (
  SELECT 1 FROM dynamic_group g
  WHERE g.deleted = false AND g.tenant_id = 1
    AND g.group_type = 'FIELD' AND g.code = 'FG-BIZ-PERSONNEL'
);

UPDATE dynamic_group g SET
  name = '人员组织',
  description = '人员、部门与联系方式',
  sort = 80,
  status = 1,
  updater = 'zhgl-seed',
  update_time = CURRENT_TIMESTAMP
WHERE g.deleted = false AND g.tenant_id = 1
  AND g.group_type = 'FIELD' AND g.code = 'FG-BIZ-PERSONNEL';

INSERT INTO dynamic_group (
  group_type, code, name, description, parent_id, path, level, sort, status, tenant_id, creator
)
SELECT
  'FIELD', 'FG-BIZ-CUSTOMER', '客户管理',
  '客户档案与信用', NULL,
  NULL, 1,
  90, 1,
  1, 'zhgl-seed'
WHERE NOT EXISTS (
  SELECT 1 FROM dynamic_group g
  WHERE g.deleted = false AND g.tenant_id = 1
    AND g.group_type = 'FIELD' AND g.code = 'FG-BIZ-CUSTOMER'
);

UPDATE dynamic_group g SET
  name = '客户管理',
  description = '客户档案与信用',
  sort = 90,
  status = 1,
  updater = 'zhgl-seed',
  update_time = CURRENT_TIMESTAMP
WHERE g.deleted = false AND g.tenant_id = 1
  AND g.group_type = 'FIELD' AND g.code = 'FG-BIZ-CUSTOMER';

INSERT INTO dynamic_group (
  group_type, code, name, description, parent_id, path, level, sort, status, tenant_id, creator
)
SELECT
  'FIELD', 'FG-BIZ-CONTRACT', '合同管理',
  '合同签订与履约', NULL,
  NULL, 1,
  100, 1,
  1, 'zhgl-seed'
WHERE NOT EXISTS (
  SELECT 1 FROM dynamic_group g
  WHERE g.deleted = false AND g.tenant_id = 1
    AND g.group_type = 'FIELD' AND g.code = 'FG-BIZ-CONTRACT'
);

UPDATE dynamic_group g SET
  name = '合同管理',
  description = '合同签订与履约',
  sort = 100,
  status = 1,
  updater = 'zhgl-seed',
  update_time = CURRENT_TIMESTAMP
WHERE g.deleted = false AND g.tenant_id = 1
  AND g.group_type = 'FIELD' AND g.code = 'FG-BIZ-CONTRACT';

INSERT INTO dynamic_group (
  group_type, code, name, description, parent_id, path, level, sort, status, tenant_id, creator
)
SELECT
  'FIELD', 'FG-BIZ-BILLING', '收费管理',
  '收费、账期与票据', NULL,
  NULL, 1,
  110, 1,
  1, 'zhgl-seed'
WHERE NOT EXISTS (
  SELECT 1 FROM dynamic_group g
  WHERE g.deleted = false AND g.tenant_id = 1
    AND g.group_type = 'FIELD' AND g.code = 'FG-BIZ-BILLING'
);

UPDATE dynamic_group g SET
  name = '收费管理',
  description = '收费、账期与票据',
  sort = 110,
  status = 1,
  updater = 'zhgl-seed',
  update_time = CURRENT_TIMESTAMP
WHERE g.deleted = false AND g.tenant_id = 1
  AND g.group_type = 'FIELD' AND g.code = 'FG-BIZ-BILLING';

INSERT INTO dynamic_group (
  group_type, code, name, description, parent_id, path, level, sort, status, tenant_id, creator
)
SELECT
  'FIELD', 'FG-BIZ-SPARE', '备件管理',
  '备件库存与领用', NULL,
  NULL, 1,
  120, 1,
  1, 'zhgl-seed'
WHERE NOT EXISTS (
  SELECT 1 FROM dynamic_group g
  WHERE g.deleted = false AND g.tenant_id = 1
    AND g.group_type = 'FIELD' AND g.code = 'FG-BIZ-SPARE'
);

UPDATE dynamic_group g SET
  name = '备件管理',
  description = '备件库存与领用',
  sort = 120,
  status = 1,
  updater = 'zhgl-seed',
  update_time = CURRENT_TIMESTAMP
WHERE g.deleted = false AND g.tenant_id = 1
  AND g.group_type = 'FIELD' AND g.code = 'FG-BIZ-SPARE';

INSERT INTO dynamic_group (
  group_type, code, name, description, parent_id, path, level, sort, status, tenant_id, creator
)
SELECT
  'FIELD', 'FG-BIZ-MONITOR', '运行监测',
  '实时监测类数值（温压流等）', NULL,
  NULL, 1,
  130, 1,
  1, 'zhgl-seed'
WHERE NOT EXISTS (
  SELECT 1 FROM dynamic_group g
  WHERE g.deleted = false AND g.tenant_id = 1
    AND g.group_type = 'FIELD' AND g.code = 'FG-BIZ-MONITOR'
);

UPDATE dynamic_group g SET
  name = '运行监测',
  description = '实时监测类数值（温压流等）',
  sort = 130,
  status = 1,
  updater = 'zhgl-seed',
  update_time = CURRENT_TIMESTAMP
WHERE g.deleted = false AND g.tenant_id = 1
  AND g.group_type = 'FIELD' AND g.code = 'FG-BIZ-MONITOR';

INSERT INTO dynamic_group (
  group_type, code, name, description, parent_id, path, level, sort, status, tenant_id, creator
)
SELECT
  'FIELD', 'FG-BIZ-INSTRUMENT', '仪表管理',
  '工业仪表台账、位号与校准', NULL,
  NULL, 1,
  140, 1,
  1, 'zhgl-seed'
WHERE NOT EXISTS (
  SELECT 1 FROM dynamic_group g
  WHERE g.deleted = false AND g.tenant_id = 1
    AND g.group_type = 'FIELD' AND g.code = 'FG-BIZ-INSTRUMENT'
);

UPDATE dynamic_group g SET
  name = '仪表管理',
  description = '工业仪表台账、位号与校准',
  sort = 140,
  status = 1,
  updater = 'zhgl-seed',
  update_time = CURRENT_TIMESTAMP
WHERE g.deleted = false AND g.tenant_id = 1
  AND g.group_type = 'FIELD' AND g.code = 'FG-BIZ-INSTRUMENT';

INSERT INTO dynamic_group (
  group_type, code, name, description, parent_id, path, level, sort, status, tenant_id, creator
)
SELECT
  'FIELD', 'FG-BIZ-FIRE', '消防管理',
  '消防设施、分区与演练', NULL,
  NULL, 1,
  150, 1,
  1, 'zhgl-seed'
WHERE NOT EXISTS (
  SELECT 1 FROM dynamic_group g
  WHERE g.deleted = false AND g.tenant_id = 1
    AND g.group_type = 'FIELD' AND g.code = 'FG-BIZ-FIRE'
);

UPDATE dynamic_group g SET
  name = '消防管理',
  description = '消防设施、分区与演练',
  sort = 150,
  status = 1,
  updater = 'zhgl-seed',
  update_time = CURRENT_TIMESTAMP
WHERE g.deleted = false AND g.tenant_id = 1
  AND g.group_type = 'FIELD' AND g.code = 'FG-BIZ-FIRE';

INSERT INTO dynamic_group (
  group_type, code, name, description, parent_id, path, level, sort, status, tenant_id, creator
)
SELECT
  'FIELD', 'FG-BIZ-ENERGY', '能耗管理',
  '水电气热等能耗计量与分析', NULL,
  NULL, 1,
  160, 1,
  1, 'zhgl-seed'
WHERE NOT EXISTS (
  SELECT 1 FROM dynamic_group g
  WHERE g.deleted = false AND g.tenant_id = 1
    AND g.group_type = 'FIELD' AND g.code = 'FG-BIZ-ENERGY'
);

UPDATE dynamic_group g SET
  name = '能耗管理',
  description = '水电气热等能耗计量与分析',
  sort = 160,
  status = 1,
  updater = 'zhgl-seed',
  update_time = CURRENT_TIMESTAMP
WHERE g.deleted = false AND g.tenant_id = 1
  AND g.group_type = 'FIELD' AND g.code = 'FG-BIZ-ENERGY';

INSERT INTO dynamic_group (
  group_type, code, name, description, parent_id, path, level, sort, status, tenant_id, creator
)
SELECT
  'FIELD', 'FG-BIZ-FAULT', '故障管理',
  '故障记录、分析与闭环', NULL,
  NULL, 1,
  170, 1,
  1, 'zhgl-seed'
WHERE NOT EXISTS (
  SELECT 1 FROM dynamic_group g
  WHERE g.deleted = false AND g.tenant_id = 1
    AND g.group_type = 'FIELD' AND g.code = 'FG-BIZ-FAULT'
);

UPDATE dynamic_group g SET
  name = '故障管理',
  description = '故障记录、分析与闭环',
  sort = 170,
  status = 1,
  updater = 'zhgl-seed',
  update_time = CURRENT_TIMESTAMP
WHERE g.deleted = false AND g.tenant_id = 1
  AND g.group_type = 'FIELD' AND g.code = 'FG-BIZ-FAULT';

INSERT INTO dynamic_group (
  group_type, code, name, description, parent_id, path, level, sort, status, tenant_id, creator
)
SELECT
  'FIELD', 'FG-BIZ-MAINTENANCE', '维修管理',
  '维修工单与维保计划', NULL,
  NULL, 1,
  180, 1,
  1, 'zhgl-seed'
WHERE NOT EXISTS (
  SELECT 1 FROM dynamic_group g
  WHERE g.deleted = false AND g.tenant_id = 1
    AND g.group_type = 'FIELD' AND g.code = 'FG-BIZ-MAINTENANCE'
);

UPDATE dynamic_group g SET
  name = '维修管理',
  description = '维修工单与维保计划',
  sort = 180,
  status = 1,
  updater = 'zhgl-seed',
  update_time = CURRENT_TIMESTAMP
WHERE g.deleted = false AND g.tenant_id = 1
  AND g.group_type = 'FIELD' AND g.code = 'FG-BIZ-MAINTENANCE';

INSERT INTO dynamic_group (
  group_type, code, name, description, parent_id, path, level, sort, status, tenant_id, creator
)
SELECT
  'FIELD', 'FG-BIZ-EMERGENCY', '应急管理',
  '应急事件、资源与处置', NULL,
  NULL, 1,
  190, 1,
  1, 'zhgl-seed'
WHERE NOT EXISTS (
  SELECT 1 FROM dynamic_group g
  WHERE g.deleted = false AND g.tenant_id = 1
    AND g.group_type = 'FIELD' AND g.code = 'FG-BIZ-EMERGENCY'
);

UPDATE dynamic_group g SET
  name = '应急管理',
  description = '应急事件、资源与处置',
  sort = 190,
  status = 1,
  updater = 'zhgl-seed',
  update_time = CURRENT_TIMESTAMP
WHERE g.deleted = false AND g.tenant_id = 1
  AND g.group_type = 'FIELD' AND g.code = 'FG-BIZ-EMERGENCY';

INSERT INTO dynamic_group (
  group_type, code, name, description, parent_id, path, level, sort, status, tenant_id, creator
)
SELECT
  'FIELD', 'FG-BIZ-PIPELINE', '管线管理',
  '管线台账与运行参数', NULL,
  NULL, 1,
  200, 1,
  1, 'zhgl-seed'
WHERE NOT EXISTS (
  SELECT 1 FROM dynamic_group g
  WHERE g.deleted = false AND g.tenant_id = 1
    AND g.group_type = 'FIELD' AND g.code = 'FG-BIZ-PIPELINE'
);

UPDATE dynamic_group g SET
  name = '管线管理',
  description = '管线台账与运行参数',
  sort = 200,
  status = 1,
  updater = 'zhgl-seed',
  update_time = CURRENT_TIMESTAMP
WHERE g.deleted = false AND g.tenant_id = 1
  AND g.group_type = 'FIELD' AND g.code = 'FG-BIZ-PIPELINE';
