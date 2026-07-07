-- ============================================================================
-- 系统共用 · 04 字段分组（dynamic_group FIELD + dynamic_group_relation）
-- Generated: 2026-07-08 by scripts/export-platform-import.py
--
-- 约定：不写 surrogate id；幂等键为 code / field_code / page_code。
-- 依赖：system/03_fields.sql
-- 幂等 upsert；与字段库一一/多对多关联，按 group_code + field_code 解析
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


-- dynamic_group_relation(FIELD): 291 row(s), resolve by group_code + field_code

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-COMMON'
  AND f.code = 'FLD-COM-001'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-COMMON'
  AND f.code = 'FLD-COM-002'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 3, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-COMMON'
  AND f.code = 'FLD-COM-003'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 4, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-COMMON'
  AND f.code = 'FLD-COM-004'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 5, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-COMMON'
  AND f.code = 'FLD-COM-005'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 6, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-COMMON'
  AND f.code = 'FLD-COM-006'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 7, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-COMMON'
  AND f.code = 'FLD-COM-007'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 8, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-COMMON'
  AND f.code = 'FLD-COM-008'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 9, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-COMMON'
  AND f.code = 'FLD-COM-009'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 10, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-COMMON'
  AND f.code = 'FLD-COM-010'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 11, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-COMMON'
  AND f.code = 'FLD-COM-011'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 12, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-COMMON'
  AND f.code = 'FLD-COM-012'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 130, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-EQUIPMENT'
  AND f.code = 'FLD-EQP-001'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 140, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-EQUIPMENT'
  AND f.code = 'FLD-EQP-002'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 150, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-EQUIPMENT'
  AND f.code = 'FLD-EQP-003'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 160, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-EQUIPMENT'
  AND f.code = 'FLD-EQP-004'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 170, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-EQUIPMENT'
  AND f.code = 'FLD-EQP-005'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 180, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-EQUIPMENT'
  AND f.code = 'FLD-EQP-006'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 190, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-EQUIPMENT'
  AND f.code = 'FLD-EQP-007'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 200, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-EQUIPMENT'
  AND f.code = 'FLD-EQP-008'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 210, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-EQUIPMENT'
  AND f.code = 'FLD-EQP-009'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 220, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-EQUIPMENT'
  AND f.code = 'FLD-EQP-010'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 230, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-EQUIPMENT'
  AND f.code = 'FLD-EQP-011'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 240, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-EQUIPMENT'
  AND f.code = 'FLD-EQP-012'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 250, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-EQUIPMENT'
  AND f.code = 'FLD-EQP-013'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 260, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-EQUIPMENT'
  AND f.code = 'FLD-EQP-014'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 270, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-EQUIPMENT'
  AND f.code = 'FLD-EQP-015'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 280, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-EQUIPMENT'
  AND f.code = 'FLD-EQP-016'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 290, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-EQUIPMENT'
  AND f.code = 'FLD-EQP-017'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 300, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-EQUIPMENT'
  AND f.code = 'FLD-EQP-018'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 310, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-EQUIPMENT'
  AND f.code = 'FLD-EQP-019'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 320, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-EQUIPMENT'
  AND f.code = 'FLD-EQP-020'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 330, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-EQUIPMENT'
  AND f.code = 'FLD-EQP-021'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 340, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-EQUIPMENT'
  AND f.code = 'FLD-EQP-022'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 350, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-EQUIPMENT'
  AND f.code = 'FLD-EQP-023'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 360, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-EQUIPMENT'
  AND f.code = 'FLD-EQP-024'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 370, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-EQUIPMENT'
  AND f.code = 'FLD-EQP-025'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 380, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-EQUIPMENT'
  AND f.code = 'FLD-EQP-026'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 390, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-EQUIPMENT'
  AND f.code = 'FLD-EQP-027'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 400, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-EQUIPMENT'
  AND f.code = 'FLD-EQP-028'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 410, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-EQUIPMENT'
  AND f.code = 'FLD-EQP-029'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 420, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-EQUIPMENT'
  AND f.code = 'FLD-EQP-030'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 430, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-EQUIPMENT'
  AND f.code = 'FLD-EQP-031'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 440, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-EQUIPMENT'
  AND f.code = 'FLD-EQP-032'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 450, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-EQUIPMENT'
  AND f.code = 'FLD-EQP-033'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 460, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-EQUIPMENT'
  AND f.code = 'FLD-EQP-034'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 470, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-EQUIPMENT'
  AND f.code = 'FLD-EQP-035'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 480, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-EQUIPMENT'
  AND f.code = 'FLD-EQP-036'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, NULL, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-EQUIPMENT'
  AND f.code = 'FLD-COM-001'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 490, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-ALARM'
  AND f.code = 'FLD-ALM-001'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 500, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-ALARM'
  AND f.code = 'FLD-ALM-002'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 510, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-ALARM'
  AND f.code = 'FLD-ALM-003'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 520, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-ALARM'
  AND f.code = 'FLD-ALM-004'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 530, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-ALARM'
  AND f.code = 'FLD-ALM-005'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 540, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-ALARM'
  AND f.code = 'FLD-ALM-006'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 550, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-ALARM'
  AND f.code = 'FLD-ALM-007'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 560, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-ALARM'
  AND f.code = 'FLD-ALM-008'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 570, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-ALARM'
  AND f.code = 'FLD-ALM-009'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 580, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-ALARM'
  AND f.code = 'FLD-ALM-010'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 590, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-ALARM'
  AND f.code = 'FLD-ALM-011'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 600, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-ALARM'
  AND f.code = 'FLD-ALM-012'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 610, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-ALARM'
  AND f.code = 'FLD-ALM-013'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 620, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-ALARM'
  AND f.code = 'FLD-ALM-014'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 630, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-ALARM'
  AND f.code = 'FLD-ALM-015'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 640, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-ALARM'
  AND f.code = 'FLD-ALM-016'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 650, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-ALARM'
  AND f.code = 'FLD-ALM-017'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 660, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-ALARM'
  AND f.code = 'FLD-ALM-018'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 670, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-ALARM'
  AND f.code = 'FLD-ALM-019'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-INSPECTION'
  AND f.code = 'FLD-INS-001'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-INSPECTION'
  AND f.code = 'FLD-INS-002'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 3, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-INSPECTION'
  AND f.code = 'FLD-INS-003'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 4, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-INSPECTION'
  AND f.code = 'FLD-INS-004'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 5, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-INSPECTION'
  AND f.code = 'FLD-INS-005'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 6, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-INSPECTION'
  AND f.code = 'FLD-INS-006'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 7, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-INSPECTION'
  AND f.code = 'FLD-INS-007'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 8, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-INSPECTION'
  AND f.code = 'FLD-INS-008'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 9, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-INSPECTION'
  AND f.code = 'FLD-INS-009'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 10, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-INSPECTION'
  AND f.code = 'FLD-INS-010'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 11, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-INSPECTION'
  AND f.code = 'FLD-INS-011'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 12, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-INSPECTION'
  AND f.code = 'FLD-INS-012'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 13, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-INSPECTION'
  AND f.code = 'FLD-INS-013'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 14, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-INSPECTION'
  AND f.code = 'FLD-INS-014'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 15, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-INSPECTION'
  AND f.code = 'FLD-INS-015'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 16, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-INSPECTION'
  AND f.code = 'FLD-INS-016'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 17, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-INSPECTION'
  AND f.code = 'FLD-INS-017'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 850, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-TASK'
  AND f.code = 'FLD-TSK-001'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 860, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-TASK'
  AND f.code = 'FLD-TSK-002'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 870, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-TASK'
  AND f.code = 'FLD-TSK-003'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 880, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-TASK'
  AND f.code = 'FLD-TSK-004'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 890, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-TASK'
  AND f.code = 'FLD-TSK-005'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 900, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-TASK'
  AND f.code = 'FLD-TSK-006'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 910, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-TASK'
  AND f.code = 'FLD-TSK-007'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 920, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-TASK'
  AND f.code = 'FLD-TSK-008'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 930, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-TASK'
  AND f.code = 'FLD-TSK-009'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 940, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-TASK'
  AND f.code = 'FLD-TSK-010'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 950, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-TASK'
  AND f.code = 'FLD-TSK-011'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 960, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-TASK'
  AND f.code = 'FLD-TSK-012'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 970, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-TASK'
  AND f.code = 'FLD-TSK-013'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 980, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-TASK'
  AND f.code = 'FLD-TSK-014'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 990, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-TASK'
  AND f.code = 'FLD-TSK-015'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1000, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-TASK'
  AND f.code = 'FLD-TSK-016'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1010, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-TASK'
  AND f.code = 'FLD-TSK-017'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1020, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-LOCATION'
  AND f.code = 'FLD-LOC-001'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1030, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-LOCATION'
  AND f.code = 'FLD-LOC-002'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1040, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-LOCATION'
  AND f.code = 'FLD-LOC-003'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1050, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-LOCATION'
  AND f.code = 'FLD-LOC-004'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1060, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-LOCATION'
  AND f.code = 'FLD-LOC-005'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1070, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-LOCATION'
  AND f.code = 'FLD-LOC-006'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1080, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-LOCATION'
  AND f.code = 'FLD-LOC-007'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1090, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-LOCATION'
  AND f.code = 'FLD-LOC-008'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1100, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-LOCATION'
  AND f.code = 'FLD-LOC-009'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1110, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-LOCATION'
  AND f.code = 'FLD-LOC-010'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1120, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-LOCATION'
  AND f.code = 'FLD-LOC-011'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1130, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-LOCATION'
  AND f.code = 'FLD-LOC-012'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1140, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-LOCATION'
  AND f.code = 'FLD-LOC-013'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1150, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-LOCATION'
  AND f.code = 'FLD-LOC-014'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1160, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-LOCATION'
  AND f.code = 'FLD-LOC-015'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1170, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-LOCATION'
  AND f.code = 'FLD-LOC-016'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1180, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-GALLERY'
  AND f.code = 'FLD-GAL-001'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1190, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-GALLERY'
  AND f.code = 'FLD-GAL-002'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1200, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-GALLERY'
  AND f.code = 'FLD-GAL-003'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1210, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-GALLERY'
  AND f.code = 'FLD-GAL-004'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1220, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-GALLERY'
  AND f.code = 'FLD-GAL-005'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1230, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-GALLERY'
  AND f.code = 'FLD-GAL-006'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1240, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-GALLERY'
  AND f.code = 'FLD-GAL-007'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1250, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-GALLERY'
  AND f.code = 'FLD-GAL-008'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1260, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-GALLERY'
  AND f.code = 'FLD-GAL-009'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1270, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-GALLERY'
  AND f.code = 'FLD-GAL-010'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1280, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-GALLERY'
  AND f.code = 'FLD-GAL-011'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1290, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-GALLERY'
  AND f.code = 'FLD-GAL-012'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1300, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-GALLERY'
  AND f.code = 'FLD-GAL-013'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1310, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-PERSONNEL'
  AND f.code = 'FLD-PER-001'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1320, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-PERSONNEL'
  AND f.code = 'FLD-PER-002'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1330, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-PERSONNEL'
  AND f.code = 'FLD-PER-003'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1340, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-PERSONNEL'
  AND f.code = 'FLD-PER-004'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1350, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-PERSONNEL'
  AND f.code = 'FLD-PER-005'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1360, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-PERSONNEL'
  AND f.code = 'FLD-PER-006'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1370, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-PERSONNEL'
  AND f.code = 'FLD-PER-007'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1380, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-PERSONNEL'
  AND f.code = 'FLD-PER-008'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1390, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-PERSONNEL'
  AND f.code = 'FLD-PER-009'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1400, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-PERSONNEL'
  AND f.code = 'FLD-PER-010'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1410, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-CUSTOMER'
  AND f.code = 'FLD-CUS-001'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1420, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-CUSTOMER'
  AND f.code = 'FLD-CUS-002'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1430, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-CUSTOMER'
  AND f.code = 'FLD-CUS-003'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1440, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-CUSTOMER'
  AND f.code = 'FLD-CUS-004'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1450, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-CUSTOMER'
  AND f.code = 'FLD-CUS-005'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1460, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-CUSTOMER'
  AND f.code = 'FLD-CUS-006'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1470, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-CUSTOMER'
  AND f.code = 'FLD-CUS-007'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1480, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-CUSTOMER'
  AND f.code = 'FLD-CUS-008'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1490, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-CONTRACT'
  AND f.code = 'FLD-CON-001'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1500, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-CONTRACT'
  AND f.code = 'FLD-CON-002'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1510, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-CONTRACT'
  AND f.code = 'FLD-CON-003'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1520, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-CONTRACT'
  AND f.code = 'FLD-CON-004'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1530, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-CONTRACT'
  AND f.code = 'FLD-CON-005'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1540, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-CONTRACT'
  AND f.code = 'FLD-CON-006'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1550, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-CONTRACT'
  AND f.code = 'FLD-CON-007'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1560, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-CONTRACT'
  AND f.code = 'FLD-CON-008'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1570, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-CONTRACT'
  AND f.code = 'FLD-CON-009'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1580, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-CONTRACT'
  AND f.code = 'FLD-CON-010'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1590, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-CONTRACT'
  AND f.code = 'FLD-CON-011'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1600, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-BILLING'
  AND f.code = 'FLD-BIL-001'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1610, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-BILLING'
  AND f.code = 'FLD-BIL-002'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1620, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-BILLING'
  AND f.code = 'FLD-BIL-003'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1630, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-BILLING'
  AND f.code = 'FLD-BIL-004'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1640, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-BILLING'
  AND f.code = 'FLD-BIL-005'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1650, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-BILLING'
  AND f.code = 'FLD-BIL-006'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1660, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-SPARE'
  AND f.code = 'FLD-SPR-001'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1670, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-SPARE'
  AND f.code = 'FLD-SPR-002'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1680, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-SPARE'
  AND f.code = 'FLD-SPR-003'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1690, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-SPARE'
  AND f.code = 'FLD-SPR-004'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1700, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-SPARE'
  AND f.code = 'FLD-SPR-005'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1710, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-SPARE'
  AND f.code = 'FLD-SPR-006'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1720, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-SPARE'
  AND f.code = 'FLD-SPR-007'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1730, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-SPARE'
  AND f.code = 'FLD-SPR-008'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1740, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-MONITOR'
  AND f.code = 'FLD-MON-001'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1750, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-MONITOR'
  AND f.code = 'FLD-MON-002'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1760, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-MONITOR'
  AND f.code = 'FLD-MON-003'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1770, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-MONITOR'
  AND f.code = 'FLD-MON-004'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1780, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-MONITOR'
  AND f.code = 'FLD-MON-005'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1790, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-MONITOR'
  AND f.code = 'FLD-MON-006'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1800, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-MONITOR'
  AND f.code = 'FLD-MON-007'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1810, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-MONITOR'
  AND f.code = 'FLD-MON-008'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1820, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-MONITOR'
  AND f.code = 'FLD-MON-009'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1830, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-MONITOR'
  AND f.code = 'FLD-MON-010'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1840, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-MONITOR'
  AND f.code = 'FLD-MON-011'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1850, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-MONITOR'
  AND f.code = 'FLD-MON-012'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1860, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-INSTRUMENT'
  AND f.code = 'FLD-INS-T-001'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1870, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-INSTRUMENT'
  AND f.code = 'FLD-INS-T-002'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1880, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-INSTRUMENT'
  AND f.code = 'FLD-INS-T-003'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1890, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-INSTRUMENT'
  AND f.code = 'FLD-INS-T-004'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1900, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-INSTRUMENT'
  AND f.code = 'FLD-INS-T-005'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1910, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-INSTRUMENT'
  AND f.code = 'FLD-INS-T-006'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1920, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-INSTRUMENT'
  AND f.code = 'FLD-INS-T-007'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1930, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-INSTRUMENT'
  AND f.code = 'FLD-INS-T-008'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1940, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-INSTRUMENT'
  AND f.code = 'FLD-INS-T-009'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1950, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-INSTRUMENT'
  AND f.code = 'FLD-INS-T-010'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1960, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-INSTRUMENT'
  AND f.code = 'FLD-INS-T-011'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1970, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-INSTRUMENT'
  AND f.code = 'FLD-INS-T-012'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1980, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-INSTRUMENT'
  AND f.code = 'FLD-INS-T-013'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1990, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-INSTRUMENT'
  AND f.code = 'FLD-INS-T-014'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2000, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-INSTRUMENT'
  AND f.code = 'FLD-INS-T-015'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2010, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-INSTRUMENT'
  AND f.code = 'FLD-INS-T-016'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2020, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-INSTRUMENT'
  AND f.code = 'FLD-INS-T-017'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2030, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-INSTRUMENT'
  AND f.code = 'FLD-INS-T-018'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2040, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-FIRE'
  AND f.code = 'FLD-FIR-001'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2050, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-FIRE'
  AND f.code = 'FLD-FIR-002'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2060, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-FIRE'
  AND f.code = 'FLD-FIR-003'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2070, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-FIRE'
  AND f.code = 'FLD-FIR-004'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2080, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-FIRE'
  AND f.code = 'FLD-FIR-005'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2090, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-FIRE'
  AND f.code = 'FLD-FIR-006'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2100, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-FIRE'
  AND f.code = 'FLD-FIR-007'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2110, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-FIRE'
  AND f.code = 'FLD-FIR-008'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2120, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-FIRE'
  AND f.code = 'FLD-FIR-009'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2130, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-FIRE'
  AND f.code = 'FLD-FIR-010'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2140, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-FIRE'
  AND f.code = 'FLD-FIR-011'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2150, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-FIRE'
  AND f.code = 'FLD-FIR-012'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2160, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-FIRE'
  AND f.code = 'FLD-FIR-013'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2170, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-FIRE'
  AND f.code = 'FLD-FIR-014'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2180, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-FIRE'
  AND f.code = 'FLD-FIR-015'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2190, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-FIRE'
  AND f.code = 'FLD-FIR-016'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2200, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-FIRE'
  AND f.code = 'FLD-FIR-017'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2210, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-FIRE'
  AND f.code = 'FLD-FIR-018'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2220, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-ENERGY'
  AND f.code = 'FLD-ENG-001'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2230, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-ENERGY'
  AND f.code = 'FLD-ENG-002'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2240, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-ENERGY'
  AND f.code = 'FLD-ENG-003'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2250, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-ENERGY'
  AND f.code = 'FLD-ENG-004'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2260, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-ENERGY'
  AND f.code = 'FLD-ENG-005'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2270, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-ENERGY'
  AND f.code = 'FLD-ENG-006'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2280, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-ENERGY'
  AND f.code = 'FLD-ENG-007'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2290, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-ENERGY'
  AND f.code = 'FLD-ENG-008'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2300, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-ENERGY'
  AND f.code = 'FLD-ENG-009'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2310, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-ENERGY'
  AND f.code = 'FLD-ENG-010'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2320, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-ENERGY'
  AND f.code = 'FLD-ENG-011'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2330, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-ENERGY'
  AND f.code = 'FLD-ENG-012'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2340, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-ENERGY'
  AND f.code = 'FLD-ENG-013'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2350, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-ENERGY'
  AND f.code = 'FLD-ENG-014'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2360, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-ENERGY'
  AND f.code = 'FLD-ENG-015'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2370, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-ENERGY'
  AND f.code = 'FLD-ENG-016'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2380, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-ENERGY'
  AND f.code = 'FLD-ENG-017'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2390, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-ENERGY'
  AND f.code = 'FLD-ENG-018'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2400, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-FAULT'
  AND f.code = 'FLD-FLT-001'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2410, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-FAULT'
  AND f.code = 'FLD-FLT-002'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2420, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-FAULT'
  AND f.code = 'FLD-FLT-003'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2430, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-FAULT'
  AND f.code = 'FLD-FLT-004'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2440, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-FAULT'
  AND f.code = 'FLD-FLT-005'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2450, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-FAULT'
  AND f.code = 'FLD-FLT-006'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2460, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-FAULT'
  AND f.code = 'FLD-FLT-007'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2470, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-FAULT'
  AND f.code = 'FLD-FLT-008'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2480, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-FAULT'
  AND f.code = 'FLD-FLT-009'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2490, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-FAULT'
  AND f.code = 'FLD-FLT-010'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2500, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-FAULT'
  AND f.code = 'FLD-FLT-011'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2510, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-FAULT'
  AND f.code = 'FLD-FLT-012'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2520, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-MAINTENANCE'
  AND f.code = 'FLD-MNT-001'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2530, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-MAINTENANCE'
  AND f.code = 'FLD-MNT-002'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2540, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-MAINTENANCE'
  AND f.code = 'FLD-MNT-003'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2550, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-MAINTENANCE'
  AND f.code = 'FLD-MNT-004'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2560, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-MAINTENANCE'
  AND f.code = 'FLD-MNT-005'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2570, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-MAINTENANCE'
  AND f.code = 'FLD-MNT-006'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2580, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-MAINTENANCE'
  AND f.code = 'FLD-MNT-007'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2590, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-MAINTENANCE'
  AND f.code = 'FLD-MNT-008'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2600, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-MAINTENANCE'
  AND f.code = 'FLD-MNT-009'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2610, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-MAINTENANCE'
  AND f.code = 'FLD-MNT-010'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2620, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-MAINTENANCE'
  AND f.code = 'FLD-MNT-011'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2630, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-MAINTENANCE'
  AND f.code = 'FLD-MNT-012'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2640, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-EMERGENCY'
  AND f.code = 'FLD-EMG-001'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2650, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-EMERGENCY'
  AND f.code = 'FLD-EMG-002'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2660, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-EMERGENCY'
  AND f.code = 'FLD-EMG-003'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2670, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-EMERGENCY'
  AND f.code = 'FLD-EMG-004'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2680, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-EMERGENCY'
  AND f.code = 'FLD-EMG-005'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2690, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-EMERGENCY'
  AND f.code = 'FLD-EMG-006'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2700, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-EMERGENCY'
  AND f.code = 'FLD-EMG-007'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2710, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-EMERGENCY'
  AND f.code = 'FLD-EMG-008'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2720, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-EMERGENCY'
  AND f.code = 'FLD-EMG-009'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2730, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-EMERGENCY'
  AND f.code = 'FLD-EMG-010'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2740, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-EMERGENCY'
  AND f.code = 'FLD-EMG-011'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2750, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-EMERGENCY'
  AND f.code = 'FLD-EMG-012'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2760, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-EMERGENCY'
  AND f.code = 'FLD-EMG-013'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2770, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-EMERGENCY'
  AND f.code = 'FLD-EMG-014'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2780, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-PIPELINE'
  AND f.code = 'FLD-PIP-001'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2790, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-PIPELINE'
  AND f.code = 'FLD-PIP-002'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2800, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-PIPELINE'
  AND f.code = 'FLD-PIP-003'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2810, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-PIPELINE'
  AND f.code = 'FLD-PIP-004'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2820, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-PIPELINE'
  AND f.code = 'FLD-PIP-005'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2830, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-PIPELINE'
  AND f.code = 'FLD-PIP-006'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2840, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-PIPELINE'
  AND f.code = 'FLD-PIP-007'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2850, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-PIPELINE'
  AND f.code = 'FLD-PIP-008'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2860, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-PIPELINE'
  AND f.code = 'FLD-PIP-009'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2870, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-PIPELINE'
  AND f.code = 'FLD-PIP-010'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2880, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-PIPELINE'
  AND f.code = 'FLD-PIP-011'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2890, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-PIPELINE'
  AND f.code = 'FLD-PIP-012'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2900, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-PIPELINE'
  AND f.code = 'FLD-PIP-013'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );
