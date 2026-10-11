-- 字段库分组：按使用习惯补无人机、机器人、摄像机等组。
-- 只补尚未进组的字段；已有分组保持不动。幂等，可重复执行。
SET search_path TO dynamicbusiness;

INSERT INTO dynamic_group (
  group_type, code, name, description, level, sort, status, tenant_id, creator
)
SELECT 'FIELD', 'FG-DEV-UAV', '无人机', '飞行、航线、云台、挂载和上云里跟无人机有关的参数', 1, 100, 1, 1, 'zhgl-seed'
WHERE NOT EXISTS (
  SELECT 1 FROM dynamic_group g
  WHERE g.deleted = false AND g.tenant_id = 1
    AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
);

INSERT INTO dynamic_group (
  group_type, code, name, description, level, sort, status, tenant_id, creator
)
SELECT 'FIELD', 'FG-DEV-UAV', '无人机', '飞行、航线、云台、挂载和上云里跟无人机有关的参数', 1, 100, 1, 2, 'zhgl-seed'
WHERE NOT EXISTS (
  SELECT 1 FROM dynamic_group g
  WHERE g.deleted = false AND g.tenant_id = 2
    AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
);

INSERT INTO dynamic_group (
  group_type, code, name, description, level, sort, status, tenant_id, creator
)
SELECT 'FIELD', 'FG-DEV-ROBOT', '机器人', '机器人巡检会用到的驻留、云台、直播和位置参数', 1, 110, 1, 1, 'zhgl-seed'
WHERE NOT EXISTS (
  SELECT 1 FROM dynamic_group g
  WHERE g.deleted = false AND g.tenant_id = 1
    AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-ROBOT'
);

INSERT INTO dynamic_group (
  group_type, code, name, description, level, sort, status, tenant_id, creator
)
SELECT 'FIELD', 'FG-DEV-ROBOT', '机器人', '机器人巡检会用到的驻留、云台、直播和位置参数', 1, 110, 1, 2, 'zhgl-seed'
WHERE NOT EXISTS (
  SELECT 1 FROM dynamic_group g
  WHERE g.deleted = false AND g.tenant_id = 2
    AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-ROBOT'
);

INSERT INTO dynamic_group (
  group_type, code, name, description, level, sort, status, tenant_id, creator
)
SELECT 'FIELD', 'FG-DEV-CAMERA', '摄像机', '固定摄像机的预置位、变焦对焦、测温和直播参数', 1, 120, 1, 1, 'zhgl-seed'
WHERE NOT EXISTS (
  SELECT 1 FROM dynamic_group g
  WHERE g.deleted = false AND g.tenant_id = 1
    AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
);

INSERT INTO dynamic_group (
  group_type, code, name, description, level, sort, status, tenant_id, creator
)
SELECT 'FIELD', 'FG-DEV-CAMERA', '摄像机', '固定摄像机的预置位、变焦对焦、测温和直播参数', 1, 120, 1, 2, 'zhgl-seed'
WHERE NOT EXISTS (
  SELECT 1 FROM dynamic_group g
  WHERE g.deleted = false AND g.tenant_id = 2
    AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
);

INSERT INTO dynamic_group (
  group_type, code, name, description, level, sort, status, tenant_id, creator
)
SELECT 'FIELD', 'FG-DEV-MANUAL', '人工', '人工执行时填写的内容', 1, 130, 1, 1, 'zhgl-seed'
WHERE NOT EXISTS (
  SELECT 1 FROM dynamic_group g
  WHERE g.deleted = false AND g.tenant_id = 1
    AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-MANUAL'
);

INSERT INTO dynamic_group (
  group_type, code, name, description, level, sort, status, tenant_id, creator
)
SELECT 'FIELD', 'FG-DEV-MANUAL', '人工', '人工执行时填写的内容', 1, 130, 1, 2, 'zhgl-seed'
WHERE NOT EXISTS (
  SELECT 1 FROM dynamic_group g
  WHERE g.deleted = false AND g.tenant_id = 2
    AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-MANUAL'
);

INSERT INTO dynamic_group (
  group_type, code, name, description, level, sort, status, tenant_id, creator
)
SELECT 'FIELD', 'FG-ACT-STEP', '动作与步骤', '动作定义、步骤树、检查内容和参数清单', 1, 140, 1, 1, 'zhgl-seed'
WHERE NOT EXISTS (
  SELECT 1 FROM dynamic_group g
  WHERE g.deleted = false AND g.tenant_id = 1
    AND g.group_type = 'FIELD' AND g.code = 'FG-ACT-STEP'
);

INSERT INTO dynamic_group (
  group_type, code, name, description, level, sort, status, tenant_id, creator
)
SELECT 'FIELD', 'FG-ACT-STEP', '动作与步骤', '动作定义、步骤树、检查内容和参数清单', 1, 140, 1, 2, 'zhgl-seed'
WHERE NOT EXISTS (
  SELECT 1 FROM dynamic_group g
  WHERE g.deleted = false AND g.tenant_id = 2
    AND g.group_type = 'FIELD' AND g.code = 'FG-ACT-STEP'
);

INSERT INTO dynamic_group (
  group_type, code, name, description, level, sort, status, tenant_id, creator
)
SELECT 'FIELD', 'FG-ACT-ROUTE', '路线', '停靠、路网、机动剖面和路线规划', 1, 150, 1, 1, 'zhgl-seed'
WHERE NOT EXISTS (
  SELECT 1 FROM dynamic_group g
  WHERE g.deleted = false AND g.tenant_id = 1
    AND g.group_type = 'FIELD' AND g.code = 'FG-ACT-ROUTE'
);

INSERT INTO dynamic_group (
  group_type, code, name, description, level, sort, status, tenant_id, creator
)
SELECT 'FIELD', 'FG-ACT-ROUTE', '路线', '停靠、路网、机动剖面和路线规划', 1, 150, 1, 2, 'zhgl-seed'
WHERE NOT EXISTS (
  SELECT 1 FROM dynamic_group g
  WHERE g.deleted = false AND g.tenant_id = 2
    AND g.group_type = 'FIELD' AND g.code = 'FG-ACT-ROUTE'
);

INSERT INTO dynamic_group (
  group_type, code, name, description, level, sort, status, tenant_id, creator
)
SELECT 'FIELD', 'FG-ACT-SCHEDULE', '排期', '排期模式、Cron 和排期快照', 1, 160, 1, 1, 'zhgl-seed'
WHERE NOT EXISTS (
  SELECT 1 FROM dynamic_group g
  WHERE g.deleted = false AND g.tenant_id = 1
    AND g.group_type = 'FIELD' AND g.code = 'FG-ACT-SCHEDULE'
);

INSERT INTO dynamic_group (
  group_type, code, name, description, level, sort, status, tenant_id, creator
)
SELECT 'FIELD', 'FG-ACT-SCHEDULE', '排期', '排期模式、Cron 和排期快照', 1, 160, 1, 2, 'zhgl-seed'
WHERE NOT EXISTS (
  SELECT 1 FROM dynamic_group g
  WHERE g.deleted = false AND g.tenant_id = 2
    AND g.group_type = 'FIELD' AND g.code = 'FG-ACT-SCHEDULE'
);

INSERT INTO dynamic_group (
  group_type, code, name, description, level, sort, status, tenant_id, creator
)
SELECT 'FIELD', 'FG-PROTO-LINK', '协议接入', 'Modbus、OPC、MQTT 等点位怎么对上协议地址', 1, 170, 1, 1, 'zhgl-seed'
WHERE NOT EXISTS (
  SELECT 1 FROM dynamic_group g
  WHERE g.deleted = false AND g.tenant_id = 1
    AND g.group_type = 'FIELD' AND g.code = 'FG-PROTO-LINK'
);

INSERT INTO dynamic_group (
  group_type, code, name, description, level, sort, status, tenant_id, creator
)
SELECT 'FIELD', 'FG-PROTO-LINK', '协议接入', 'Modbus、OPC、MQTT 等点位怎么对上协议地址', 1, 170, 1, 2, 'zhgl-seed'
WHERE NOT EXISTS (
  SELECT 1 FROM dynamic_group g
  WHERE g.deleted = false AND g.tenant_id = 2
    AND g.group_type = 'FIELD' AND g.code = 'FG-PROTO-LINK'
);

INSERT INTO dynamic_group (
  group_type, code, name, description, level, sort, status, tenant_id, creator
)
SELECT 'FIELD', 'FG-PROTO-MAP', '协议对照', '指令编码、协议版本和字段对照', 1, 180, 1, 1, 'zhgl-seed'
WHERE NOT EXISTS (
  SELECT 1 FROM dynamic_group g
  WHERE g.deleted = false AND g.tenant_id = 1
    AND g.group_type = 'FIELD' AND g.code = 'FG-PROTO-MAP'
);

INSERT INTO dynamic_group (
  group_type, code, name, description, level, sort, status, tenant_id, creator
)
SELECT 'FIELD', 'FG-PROTO-MAP', '协议对照', '指令编码、协议版本和字段对照', 1, 180, 1, 2, 'zhgl-seed'
WHERE NOT EXISTS (
  SELECT 1 FROM dynamic_group g
  WHERE g.deleted = false AND g.tenant_id = 2
    AND g.group_type = 'FIELD' AND g.code = 'FG-PROTO-MAP'
);

INSERT INTO dynamic_group (
  group_type, code, name, description, level, sort, status, tenant_id, creator
)
SELECT 'FIELD', 'FG-REGION', '区域', '区域概况、驻地和管线里程', 1, 190, 1, 1, 'zhgl-seed'
WHERE NOT EXISTS (
  SELECT 1 FROM dynamic_group g
  WHERE g.deleted = false AND g.tenant_id = 1
    AND g.group_type = 'FIELD' AND g.code = 'FG-REGION'
);

INSERT INTO dynamic_group (
  group_type, code, name, description, level, sort, status, tenant_id, creator
)
SELECT 'FIELD', 'FG-REGION', '区域', '区域概况、驻地和管线里程', 1, 190, 1, 2, 'zhgl-seed'
WHERE NOT EXISTS (
  SELECT 1 FROM dynamic_group g
  WHERE g.deleted = false AND g.tenant_id = 2
    AND g.group_type = 'FIELD' AND g.code = 'FG-REGION'
);

INSERT INTO dynamic_group (
  group_type, code, name, description, level, sort, status, tenant_id, creator
)
SELECT 'FIELD', 'FG-LINK', '业务关联', '跨台账的关联引用', 1, 200, 1, 1, 'zhgl-seed'
WHERE NOT EXISTS (
  SELECT 1 FROM dynamic_group g
  WHERE g.deleted = false AND g.tenant_id = 1
    AND g.group_type = 'FIELD' AND g.code = 'FG-LINK'
);

INSERT INTO dynamic_group (
  group_type, code, name, description, level, sort, status, tenant_id, creator
)
SELECT 'FIELD', 'FG-LINK', '业务关联', '跨台账的关联引用', 1, 200, 1, 2, 'zhgl-seed'
WHERE NOT EXISTS (
  SELECT 1 FROM dynamic_group g
  WHERE g.deleted = false AND g.tenant_id = 2
    AND g.group_type = 'FIELD' AND g.code = 'FG-LINK'
);

INSERT INTO dynamic_group (
  group_type, code, name, description, level, sort, status, tenant_id, creator
)
SELECT 'FIELD', 'FG-COMMON-EXTRA', '通用', '版本、摘要、附件这类各处都会用到的字段', 1, 210, 1, 1, 'zhgl-seed'
WHERE NOT EXISTS (
  SELECT 1 FROM dynamic_group g
  WHERE g.deleted = false AND g.tenant_id = 1
    AND g.group_type = 'FIELD' AND g.code = 'FG-COMMON-EXTRA'
);

INSERT INTO dynamic_group (
  group_type, code, name, description, level, sort, status, tenant_id, creator
)
SELECT 'FIELD', 'FG-COMMON-EXTRA', '通用', '版本、摘要、附件这类各处都会用到的字段', 1, 210, 1, 2, 'zhgl-seed'
WHERE NOT EXISTS (
  SELECT 1 FROM dynamic_group g
  WHERE g.deleted = false AND g.tenant_id = 2
    AND g.group_type = 'FIELD' AND g.code = 'FG-COMMON-EXTRA'
);

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 1, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'yaw'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 2, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'pitch'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 3, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'roll'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 4, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'focal_length'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 5, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'shot_count'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 6, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'gas_threshold'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 7, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'F-e49f76bdca3e4e1393e8e2f1cc0e7867'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 8, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'F-3c08eacdd4344642857093c10d71c03e'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 9, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'F-83c7856bb9984e62904707cd9917d405'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 10, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'F-2b8fa8bda0b94663979c112af85325dc'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 11, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'ground_height'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 12, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'payload_position_index'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 13, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'file_suffix'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 14, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'payload_lens_index'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 15, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'use_global_payload_lens'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 16, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'point_focus'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 17, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'focus_x'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 18, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'focus_y'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 19, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'focus_region_width'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 20, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'focus_region_height'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 21, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'infinite_focus'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 22, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'directory_name'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 23, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'gimbal_heading_yaw_base'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 24, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'gimbal_rotate_mode'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 25, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'gimbal_pitch_rotate_enable'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 26, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'gimbal_roll_rotate_enable'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 27, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'gimbal_yaw_rotate_enable'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 28, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'gimbal_rotate_time_enable'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 29, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'gimbal_rotate_time'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 30, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'aircraft_heading'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 31, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'aircraft_path_mode'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 32, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'pano_shot_sub_mode'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 33, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'record_point_cloud_operate'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 34, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'target_angle'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 35, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'accurate_frame_valid'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 36, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'megaphone_operate_type'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 37, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'megaphone_volume'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 38, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'megaphone_loop'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 39, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'megaphone_file_path'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 40, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'megaphone_file_name'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 41, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'megaphone_file_original_name'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 42, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'megaphone_file_md5'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 43, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'megaphone_file_bitrate'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 44, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'searchlight_operate_type'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 45, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'searchlight_brightness'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 46, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'action_uuid'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 47, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'cloud_access_mode'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 48, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'gateway_sn'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 49, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'aircraft_sn'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 50, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'gimbal_yaw_rotate_angle'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 51, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'oriented_photo_mode'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 52, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_data_output_file_url'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 53, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_data_output_file_fingerprint'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 54, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_data_output_bucket'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 55, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_data_output_credentials_access_key_id'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 56, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_data_output_credentials_access_key_secret'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 57, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_data_output_credentials_expire'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 58, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_data_output_credentials_security_token'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 59, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_data_output_endpoint'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 60, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_data_output_provider'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 61, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_data_output_region'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 62, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_data_output_object_key_prefix'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 63, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_path_params_workspace_id'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 64, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_path_params_id'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 65, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_header_x_auth_token'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 66, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_body_content_geometry_coordinates'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 67, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_body_content_geometry_type'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 68, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_body_content_properties_clamp_to_ground'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 69, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_body_content_type'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 70, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_body_name'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 71, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_query_group_id'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 72, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_query_is_distributed'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 73, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_path_params_group_id'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 74, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_body_id'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 75, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_body_resource_content_geometry_coordinates'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 76, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_body_resource_content_geometry_type'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 77, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_body_resource_content_properties_clamp_to_ground'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 78, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_body_resource_content_properties_color'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 79, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_body_resource_content_type'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 80, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_body_resource_type'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 81, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_body_file_group_id'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 82, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_body_file_count'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 83, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_body_file_uploaded_count'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 84, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_query_name'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 85, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_query_favorited'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 86, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_query_order_by'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 87, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_query_page'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 88, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_query_page_size'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 89, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_query_template_type'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 90, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_query_action_type'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 91, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_query_drone_model_keys'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 92, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_query_payload_model_key'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 93, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_body_metadata_drone_model_key'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 94, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_body_metadata_payload_model_keys'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 95, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_body_metadata_template_types'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 96, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_body_object_key'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 97, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_args_token'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 98, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_args_type'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 99, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_args_params'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 100, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_args_js_callback'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 101, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_args_boolean'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 102, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_args_int'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 103, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_args_pkg_name'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 104, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_args_name'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 105, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_args_param'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 106, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_args_platform_name'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 107, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_args_workspace_name'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 108, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_args_desc'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 109, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_args_key'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 110, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_args_uuid'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 111, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_args_app_id'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 112, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_args_app_key'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 113, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_args_license'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 114, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_args_user_name'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 115, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_args_passwd'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 116, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_args_callback'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 117, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_args_host'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 118, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_args_msg'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 119, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_data'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 120, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_data_sn'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 121, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_data_host_attitude_head'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 122, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_data_host_elevation'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 123, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_data_host_horizontal_speed'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 124, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_data_host_vertical_speed'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 125, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_data_id'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 126, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_data_group_id'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 127, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_data_name'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 128, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_data_resource_user_name'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 129, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_data_resource_content'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 130, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_data_resource_type'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 131, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_data_ids'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 132, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_data_ids_singleitem'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 133, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'camera_type'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 134, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'security_takeoff_height'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 135, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'rth_mode'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 136, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'rth_altitude'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 137, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'rc_lost_action'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 138, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'commander_mode_lost_action'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 139, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'commander_flight_mode'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 140, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'commander_flight_height'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 141, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'flight_id'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 142, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'max_speed'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 143, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'stream_url'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 144, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'record_rotate'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 145, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'gas_frequency'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 146, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'url_type'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 147, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'video_id'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 148, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'video_quality'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 149, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'fly_to_id'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 150, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'camera_mode'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 151, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'payload_index'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 152, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'zoom_factor'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 153, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_domain'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 154, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_main_type'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 155, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_sub_type'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 156, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'payload_mounts'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 157, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_camera_position'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 158, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dji_mounted_payloads'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 159, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'psdk_index'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 160, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'point_focus_camera_type'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 161, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'point_focus_x'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 162, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'point_focus_y'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 163, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'accurateFrameValid'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 164, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'actionUUID'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 165, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'AFPos'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 166, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'aircraftHeading'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 167, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'aircraftPathMode'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 168, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'directoryName'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 169, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'focalLength'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 170, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'focusRegionHeight'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 171, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'focusRegionWidth'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 172, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'focusX'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 173, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'focusY'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 174, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'gimbalHeadingYawBase'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 175, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'gimbalPitchRotateAngle'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 176, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'gimbalPitchRotateEnable'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 177, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'gimbalPort'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 178, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'gimbalRollRotateAngle'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 179, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'gimbalRollRotateEnable'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 180, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'gimbalRotateMode'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 181, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'gimbalRotateTime'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 182, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'gimbalRotateTimeEnable'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 183, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'gimbalYawRotateAngle'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 184, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'gimbalYawRotateEnable'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 185, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'hoverTime'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 186, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'imageHeight'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 187, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'imageWidth'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 188, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'orientedCameraApertue'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 189, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'orientedCameraISO'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 190, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'orientedCameraLuminance'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 191, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'orientedCameraShutterTime'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 192, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'orientedCameraType'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 193, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'orientedFileMD5'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 194, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'orientedFilePath'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 195, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'orientedFileSize'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 196, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'orientedFileSuffix'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 197, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'orientedPhotoMode'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 198, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'panoShotSubMode'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 199, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'payloadLensIndex'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 200, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'payloadPositionIndex'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 201, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'recordPointCloudOperate'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 202, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'searchlightBrightness'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 203, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'searchlightOperateType'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 204, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'targetAngle'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 205, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'useGlobalPayloadLensIndex'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 206, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'selected_camera'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 207, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'frame_x'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 208, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'frame_y'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 209, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'frame_width'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 210, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'frame_height'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 211, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'pitch_speed'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 212, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'yaw_speed'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 213, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'aim_x'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 214, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'aim_y'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 215, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'focus_value'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 216, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'focus_mode'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 217, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'locked'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 218, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'reset_mode'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 219, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'split_enabled'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 220, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'exposure_mode'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 221, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'exposure_value'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 222, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'photo_storage_settings'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 223, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'video_storage_settings'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 224, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'meter_mode'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 225, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'meter_x'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 226, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'meter_y'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 227, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'meter_width'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 228, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'meter_height'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 229, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'wide_live_address'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 230, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'ir_live_address'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 231, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'fpv_live_address'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 1, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'yaw'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-ROBOT'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 2, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'pitch'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-ROBOT'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 3, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'roll'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-ROBOT'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 4, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'focal_length'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-ROBOT'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 5, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'shot_count'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-ROBOT'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 6, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'gas_threshold'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-ROBOT'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 7, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'F-e49f76bdca3e4e1393e8e2f1cc0e7867'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-ROBOT'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 8, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'F-2b8fa8bda0b94663979c112af85325dc'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-ROBOT'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 9, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'gimbal_heading_yaw_base'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-ROBOT'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 10, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'gimbal_rotate_mode'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-ROBOT'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 11, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'gimbal_pitch_rotate_enable'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-ROBOT'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 12, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'gimbal_roll_rotate_enable'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-ROBOT'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 13, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'gimbal_yaw_rotate_enable'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-ROBOT'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 14, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'gimbal_rotate_time_enable'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-ROBOT'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 15, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'gimbal_rotate_time'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-ROBOT'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 16, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'gimbal_yaw_rotate_angle'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-ROBOT'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 17, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'stream_url'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-ROBOT'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 18, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'gas_frequency'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-ROBOT'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 19, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'url_type'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-ROBOT'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 20, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'video_id'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-ROBOT'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 21, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'video_quality'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-ROBOT'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 22, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'gimbalHeadingYawBase'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-ROBOT'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 23, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'gimbalPitchRotateAngle'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-ROBOT'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 24, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'gimbalPitchRotateEnable'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-ROBOT'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 25, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'gimbalPort'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-ROBOT'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 26, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'gimbalRollRotateAngle'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-ROBOT'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 27, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'gimbalRollRotateEnable'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-ROBOT'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 28, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'gimbalRotateMode'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-ROBOT'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 29, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'gimbalRotateTime'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-ROBOT'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 30, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'gimbalRotateTimeEnable'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-ROBOT'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 31, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'gimbalYawRotateAngle'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-ROBOT'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 32, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'gimbalYawRotateEnable'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-ROBOT'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 33, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'payloadPositionIndex'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-ROBOT'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 34, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'selected_camera'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-ROBOT'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 35, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'reset_mode'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-ROBOT'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 36, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'wide_live_address'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-ROBOT'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 37, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'ir_live_address'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-ROBOT'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 38, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'fpv_live_address'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-ROBOT'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 1, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'yaw'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 2, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'pitch'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 3, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'roll'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 4, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'focal_length'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 5, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'shot_count'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 6, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'F-e49f76bdca3e4e1393e8e2f1cc0e7867'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 7, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'F-7541a37277ad4badb6a2330671670de5'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 8, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'F-e9e82edb5f16466a82a62ff948d26a15'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 9, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'F-33ed046878b3408ca6c7ab73d47c0fdb'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 10, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'F-2927f6f5ee5f4896a066fd0a4faa306f'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 11, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'F-dd38ec586d1c4b67bcbe4bfeb37b4f2e'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 12, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'F-05307843641d4c5fae8caa068586bc37'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 13, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'file_suffix'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 14, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'use_global_payload_lens'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 15, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'point_focus'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 16, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'focus_x'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 17, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'focus_y'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 18, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'focus_region_width'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 19, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'focus_region_height'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 20, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'infinite_focus'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 21, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'gimbal_heading_yaw_base'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 22, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'gimbal_rotate_mode'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 23, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'gimbal_pitch_rotate_enable'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 24, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'gimbal_roll_rotate_enable'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 25, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'gimbal_yaw_rotate_enable'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 26, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'gimbal_rotate_time_enable'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 27, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'gimbal_rotate_time'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 28, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'pano_shot_sub_mode'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 29, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'target_angle'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 30, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'accurate_frame_valid'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 31, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'gimbal_yaw_rotate_angle'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 32, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'oriented_photo_mode'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 33, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'camera_type'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 34, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'stream_url'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 35, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'record_rotate'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 36, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'url_type'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 37, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'video_id'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 38, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'video_quality'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 39, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'camera_mode'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 40, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'zoom_factor'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 41, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'point_focus_camera_type'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 42, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'point_focus_x'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 43, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'point_focus_y'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 44, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'accurateFrameValid'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 45, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'AFPos'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 46, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'focalLength'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 47, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'focusRegionHeight'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 48, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'focusRegionWidth'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 49, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'focusX'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 50, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'focusY'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 51, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'gimbalHeadingYawBase'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 52, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'gimbalPitchRotateAngle'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 53, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'gimbalPitchRotateEnable'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 54, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'gimbalPort'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 55, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'gimbalRollRotateAngle'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 56, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'gimbalRollRotateEnable'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 57, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'gimbalRotateMode'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 58, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'gimbalRotateTime'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 59, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'gimbalRotateTimeEnable'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 60, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'gimbalYawRotateAngle'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 61, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'gimbalYawRotateEnable'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 62, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'imageHeight'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 63, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'imageWidth'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 64, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'orientedFileMD5'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 65, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'orientedFilePath'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 66, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'orientedFileSize'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 67, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'orientedFileSuffix'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 68, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'orientedPhotoMode'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 69, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'panoShotSubMode'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 70, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'payloadLensIndex'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 71, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'targetAngle'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 72, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'useGlobalPayloadLensIndex'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 73, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'selected_camera'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 74, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'frame_x'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 75, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'frame_y'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 76, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'frame_width'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 77, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'frame_height'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 78, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'pitch_speed'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 79, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'yaw_speed'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 80, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'aim_x'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 81, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'aim_y'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 82, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'focus_value'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 83, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'focus_mode'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 84, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'reset_mode'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 85, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'split_enabled'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 86, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'exposure_mode'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 87, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'exposure_value'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 88, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'photo_storage_settings'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 89, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'video_storage_settings'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 90, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'meter_mode'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 91, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'meter_x'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 92, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'meter_y'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 93, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'meter_width'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 94, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'meter_height'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 95, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'wide_live_address'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 96, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'ir_live_address'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 97, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'fpv_live_address'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-CAMERA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 1, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'F-e49f76bdca3e4e1393e8e2f1cc0e7867'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-MANUAL'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 2, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'F-c6508863aa6742ad992657f97c54c11e'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-MANUAL'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 1, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'FLD-TSK-029'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-ACT-STEP'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 2, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'FLD-TSK-030'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-ACT-STEP'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 3, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'FLD-TSK-031'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-ACT-STEP'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 4, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'FLD-TSK-033'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-ACT-STEP'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 5, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'FLD-TSK-019'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-ACT-STEP'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 6, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'inspection_item_id'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-ACT-STEP'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 7, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'execution_record_id'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-ACT-STEP'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 8, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'step_code'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-ACT-STEP'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 9, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'step_order'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-ACT-STEP'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 10, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'step_title'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-ACT-STEP'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 11, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'step_type'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-ACT-STEP'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 12, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'step_required'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-ACT-STEP'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 13, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'step_status'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-ACT-STEP'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 14, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'result_payload'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-ACT-STEP'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 15, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'param_slots_json'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-ACT-STEP'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 16, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'sop_template_id'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-ACT-STEP'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 17, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'step_override_json'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-ACT-STEP'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 18, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'param_override_json'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-ACT-STEP'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 19, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'default_steps_json'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-ACT-STEP'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 20, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'default_params_json'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-ACT-STEP'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 21, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'execution_means'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-ACT-STEP'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 22, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'procedure_kind'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-ACT-STEP'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 23, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'child_action_ids_json'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-ACT-STEP'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 24, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'is_composite'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-ACT-STEP'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 25, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'action_tree_json'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-ACT-STEP'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 26, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'action_tree_override_json'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-ACT-STEP'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 27, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'default_params_by_node_json'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-ACT-STEP'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 28, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'sop_object_category_type_code'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-ACT-STEP'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 29, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'sop_content_entity_type_code'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-ACT-STEP'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 30, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'sop_object_content_pack_json'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-ACT-STEP'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 31, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'step_tree_json'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-ACT-STEP'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 32, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'flow_graph_json'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-ACT-STEP'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 33, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'action_ref'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-ACT-STEP'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 34, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'param_slot_mapping_json'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-ACT-STEP'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 35, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'action_duration'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-ACT-STEP'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 1, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'FLD-POB-001'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-ACT-ROUTE'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 2, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'FLD-POB-002'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-ACT-ROUTE'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 3, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'FLD-POB-003'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-ACT-ROUTE'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 4, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'FLD-PPT-001'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-ACT-ROUTE'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 5, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'FLD-PPT-002'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-ACT-ROUTE'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 6, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'FLD-PPT-003'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-ACT-ROUTE'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 7, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'FLD-PPT-004'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-ACT-ROUTE'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 8, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'FLD-TSK-034'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-ACT-ROUTE'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 9, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'FLD-TSK-021'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-ACT-ROUTE'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 10, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'FLD-TSK-025'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-ACT-ROUTE'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 11, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'FLD-TSK-026'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-ACT-ROUTE'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 12, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'FLD-PNT-001'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-ACT-ROUTE'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 13, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'FLD-PNT-002'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-ACT-ROUTE'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 14, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'FLD-PNT-003'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-ACT-ROUTE'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 15, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'FLD-RTE-010'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-ACT-ROUTE'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 16, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'FLD-RTE-011'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-ACT-ROUTE'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 17, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'FLD-RTE-012'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-ACT-ROUTE'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 18, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'FLD-RTE-013'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-ACT-ROUTE'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 19, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'FLD-RTE-014'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-ACT-ROUTE'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 20, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'FLD-RTE-015'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-ACT-ROUTE'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 21, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'FLD-PNT-004'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-ACT-ROUTE'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 22, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'FLD-PNT-005'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-ACT-ROUTE'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 23, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'route_ref'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-ACT-ROUTE'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 24, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dwell_duration'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-ACT-ROUTE'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 1, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'FLD-PSC-001'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-ACT-SCHEDULE'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 2, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'FLD-PSC-002'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-ACT-SCHEDULE'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 3, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'FLD-PSC-003'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-ACT-SCHEDULE'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 4, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'FLD-PSC-004'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-ACT-SCHEDULE'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 5, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'FLD-TSK-028'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-ACT-SCHEDULE'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 6, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'FLD-TSK-032'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-ACT-SCHEDULE'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 7, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'FLD-TSK-018'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-ACT-SCHEDULE'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 8, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'F-8c3b0ac87d744418be763201aaec8f49'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-ACT-SCHEDULE'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 1, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'protocol_qos_level'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-PROTO-LINK'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 2, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'mqtt_topic'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-PROTO-LINK'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 3, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'opcua_node_id'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-PROTO-LINK'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 4, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'opcua_namespace'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-PROTO-LINK'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 5, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'opcda_item_id'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-PROTO-LINK'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 6, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'modbus_slave_id'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-PROTO-LINK'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 7, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'function_code'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-PROTO-LINK'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 8, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'modbus_register_address'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-PROTO-LINK'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 9, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'http_method'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-PROTO-LINK'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 10, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'http_path'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-PROTO-LINK'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 11, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'bacnet_object_type'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-PROTO-LINK'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 12, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'bacnet_instance_number'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-PROTO-LINK'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 13, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'asdu_type'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-PROTO-LINK'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 14, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'iec61850_ln_ref'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-PROTO-LINK'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 15, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dnp3_point_index'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-PROTO-LINK'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 16, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'dlt645_data_identifier'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-PROTO-LINK'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 17, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'communication_protocol'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-PROTO-LINK'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 1, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'command_json'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-PROTO-MAP'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 2, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'field_description_json'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-PROTO-MAP'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 3, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'protocol_qos_level'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-PROTO-MAP'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 4, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'opcode'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-PROTO-MAP'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 5, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'sample_json'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-PROTO-MAP'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 6, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'command_direction'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-PROTO-MAP'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 7, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'exchange_mode'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-PROTO-MAP'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 8, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'protocol_version'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-PROTO-MAP'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 9, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'F-9dbc3ff0ecc044948815ebf68f2af8a2'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-PROTO-MAP'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 10, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'mapped_protocol_version'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-PROTO-MAP'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 11, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'protocol_instruction_ref'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-PROTO-MAP'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 12, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'param_slot_mapping_json'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-PROTO-MAP'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 13, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'protocol_versions'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-PROTO-MAP'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 14, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'platform_protocol'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-PROTO-MAP'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 15, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'protocol_channel'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-PROTO-MAP'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 1, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'FLD-BASE-region-cover_url'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-REGION'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 2, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'FLD-BASE-region-hq_location'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-REGION'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 3, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'FLD-BASE-region-mission_summary'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-REGION'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 4, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'FLD-BASE-region-pipeline_km_total'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-REGION'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 5, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'FLD-BASE-region-pipeline_km_ng'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-REGION'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 6, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'FLD-BASE-region-pipeline_km_cr'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-REGION'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 7, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'FLD-BASE-region-pipeline_km_cp'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-REGION'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 8, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'FLD-BASE-region-storage_count'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-REGION'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 9, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'FLD-BASE-region-lng_terminal_count'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-REGION'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 10, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'FLD-BASE-region-coverage_note'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-REGION'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 11, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'FLD-BASE-region-org_mode_note'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-REGION'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 12, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'FLD-BASE-region-service_radius_km'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-REGION'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 13, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'FLD-UT-zone-ue_entity_code'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-REGION'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 14, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'FLD-UT-zone-segment_road_name'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-REGION'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 15, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'FLD-UT-fire_compartment_no'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-REGION'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 16, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'FLD-UT-fire_compartment_code'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-REGION'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 17, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'FLD-UT-route_length_m'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-REGION'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 18, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'FLD-UT-pipeline_total'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-REGION'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 19, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'FLD-UT-structure_total'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-REGION'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 20, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'FLD-UT-structure_type_stats'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-REGION'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 21, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'FLD-UT-pipeline_count'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-REGION'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 22, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'FLD-UT-legacy_biz_region_id'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-REGION'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 23, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'FLD-UT-structure-length_mm'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-REGION'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 24, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'FLD-UT-structure-width_mm'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-REGION'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 25, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'FLD-UT-structure-height_mm'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-REGION'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 26, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'FLD-UT-structure-floor_clearance_mm'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-REGION'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 27, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'FLD-UT-structure-fire_compartment_no'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-REGION'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 28, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'cover_url'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-REGION'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 29, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'hq_location'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-REGION'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 30, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'mission_summary'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-REGION'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 31, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'pipeline_km_total'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-REGION'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 32, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'pipeline_km_ng'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-REGION'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 33, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'pipeline_km_cr'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-REGION'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 34, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'pipeline_km_cp'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-REGION'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 35, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'storage_count'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-REGION'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 36, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'lng_terminal_count'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-REGION'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 37, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'coverage_note'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-REGION'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 38, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'org_mode_note'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-REGION'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 39, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'service_radius_km'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-REGION'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 1, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'F-6e30c29dd3db4b9f94ccd12750835292'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-LINK'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 2, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'F-8c3b0ac87d744418be763201aaec8f49'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-LINK'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 3, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'F-0eb3256b867745859a4f15ebca35d42d'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-LINK'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 4, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'F-740a88905d07431ebafa95e403f551bb'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-LINK'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 5, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'F-f4f0807ebf064059adc2b526593f5ffe'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-LINK'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 6, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'F-c7c6a0d552224038874b8c62fc7d5aff'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-LINK'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 7, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'F-85a1c8462bb449e298e73d52c8fb4108'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-LINK'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 8, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'F-c677e05413a443ed8d7062227e552b9b'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-LINK'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 9, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'F-cf9d757b952c4bcabee441d2be169c7c'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-LINK'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 10, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'F-53558afe1eb0415e88259b0ddfc57881'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-LINK'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 11, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'F-9dbc3ff0ecc044948815ebf68f2af8a2'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-LINK'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 12, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'F-0e1d488b60a147569a326530ae7f4f25'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-LINK'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 13, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'F-cff18eed07f74c2e87724554671ea6d9'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-LINK'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 14, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'F-1104009eed0b4b18b80eb561d29848cc'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-LINK'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 15, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'F-38a65b6257ff4e8ea8f643e75d1a7947'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-LINK'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 16, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'F-2c9a36d3816544d986d3ada71c344dfa'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-LINK'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 1, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'establish_date'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-COMMON-EXTRA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 2, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'FLD-LOC-001'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-COMMON-EXTRA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 3, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'equipment_id'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-COMMON-EXTRA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 4, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'completed_at'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-COMMON-EXTRA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 5, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'completed_by'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-COMMON-EXTRA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 6, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'version_no'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-COMMON-EXTRA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 7, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'publish_status'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-COMMON-EXTRA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 8, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'parentId'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-COMMON-EXTRA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 9, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'standard_no'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-COMMON-EXTRA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 10, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'standard_level'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-COMMON-EXTRA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 11, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'issuing_body'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-COMMON-EXTRA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 12, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'publish_year'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-COMMON-EXTRA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 13, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'summary'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-COMMON-EXTRA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 14, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'attachments'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-COMMON-EXTRA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 15, f.tenant_id, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'remark'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-COMMON-EXTRA'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );
