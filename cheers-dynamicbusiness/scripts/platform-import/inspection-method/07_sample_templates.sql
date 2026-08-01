-- ============================================================================
-- inspection-method · 07 标准库方法模板样例（幂等）
-- 目的：用多种常见检查手段把「模板 / 耗时 / 绑到检查内容」展示清楚
-- 前置：06_sample_bind 可先可后；型号 inspection_method 已存在
-- 说明：仍只有 is_template / action_duration_sec 两列业务字段；角度等参数待后续字段契约
-- ============================================================================

SET search_path TO dynamicbusiness;

-- ---------------------------------------------------------------------------
-- 1) 方法模板清单（code 稳定；name / 耗时可按验收需要微调）
-- ---------------------------------------------------------------------------
WITH templates(code, name, sort, action_duration_sec) AS (
  VALUES
    -- 通用外观 / 记录
    ('IM-TPL-VISUAL-01',          '目视检查模板',           10,  60),
    ('IM-TPL-PHOTO-RECORD',       '拍照取证模板',           20,  90),
    ('IM-TPL-MARKING-READ',       '标识铭牌核对模板',       30,  45),
    -- 仪表 / 读数
    ('IM-TPL-GAUGE-READ',         '就地仪表读数模板',       40,  75),
    ('IM-TPL-LEVEL-TEMP-READ',    '液位温度指示核对模板',   50,  90),
    ('IM-TPL-PRESSURE-READ',      '进出口压力核对模板',     60,  80),
    -- 设备状态（机泵阀门）
    ('IM-TPL-LEAK-CHECK',         '泄漏巡查模板',           70, 120),
    ('IM-TPL-VALVE-POSITION',     '阀门状态核对模板',       80,  90),
    ('IM-TPL-VALVE-OPERATE',      '阀门开关灵活度检查模板', 90, 150),
    ('IM-TPL-VIBRATION-LISTEN',   '振动异响听诊模板',      100, 100),
    ('IM-TPL-MOTOR-TEMP',         '电机温升检查模板',      110, 120),
    ('IM-TPL-LUBE-LEVEL',         '润滑油位检查模板',      120,  60),
    ('IM-TPL-GROUNDING-VISUAL',   '接地防雷外观检查模板',  130,  90),
    ('IM-TPL-FIRE-DIKE',          '防火堤与排水检查模板',  140, 120),
    ('IM-TPL-RELIEF-VALVE',       '呼吸阀安全附件检查模板',150, 100),
    -- 安防视频 / 通信
    ('IM-TPL-CAM-LENS',           '摄像机镜头清洁检查模板',160,  80),
    ('IM-TPL-CAM-IMAGE',          '视频画面质量检查模板',  170,  90),
    ('IM-TPL-CAM-PTZ',            '云台变焦功能检查模板',  180, 150),
    ('IM-TPL-CAM-IR',             '补光红外工作检查模板',  190,  70),
    ('IM-TPL-CAM-OSD',            '时间 OSD 核对模板',     200,  45),
    ('IM-TPL-INTERCOM',           '对讲通话清晰度检查模板',210, 120),
    ('IM-TPL-NETWORK-LINK',       '网络连接状态检查模板',  220,  60),
    -- 门禁 / 应急
    ('IM-TPL-ACCESS-CARD',        '门禁刷卡识别检查模板',  230,  90),
    ('IM-TPL-DOOR-LOCK',          '门锁开合检查模板',      240,  75),
    ('IM-TPL-EMERGENCY-LIGHT',    '应急照明切换检查模板',  250, 100),
    ('IM-TPL-BATTERY-STATUS',     '蓄电池状态检查模板',    260,  80)
)
INSERT INTO ent_inspection_method_t1 (
  tenant_id, entity_type_code, model_id, name, code, status,
  parent_id, tree_path, sort, is_template, action_duration_sec,
  attrs, custom_fields, creator, deleted
)
SELECT
  1,
  'inspection_method',
  m.id,
  t.name,
  t.code,
  1,
  0,
  NULL,
  t.sort,
  TRUE,
  t.action_duration_sec,
  '{}'::jsonb,
  '{}'::jsonb,
  'seed',
  FALSE
FROM templates t
CROSS JOIN dynamic_model m
WHERE m.deleted = false
  AND m.tenant_id = 1
  AND m.code = 'inspection_method'
  AND NOT EXISTS (
    SELECT 1 FROM ent_inspection_method_t1 e
    WHERE e.deleted = false AND e.tenant_id = 1 AND e.code = t.code
  );

-- 已存在的模板：同步名称 / 排序 / 耗时（便于重复执行）
UPDATE ent_inspection_method_t1 e
SET
  name = v.name,
  sort = v.sort,
  action_duration_sec = v.action_duration_sec,
  is_template = TRUE,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
FROM (
  VALUES
    ('IM-TPL-VISUAL-01',          '目视检查模板',           10,  60),
    ('IM-TPL-PHOTO-RECORD',       '拍照取证模板',           20,  90),
    ('IM-TPL-MARKING-READ',       '标识铭牌核对模板',       30,  45),
    ('IM-TPL-GAUGE-READ',         '就地仪表读数模板',       40,  75),
    ('IM-TPL-LEVEL-TEMP-READ',    '液位温度指示核对模板',   50,  90),
    ('IM-TPL-PRESSURE-READ',      '进出口压力核对模板',     60,  80),
    ('IM-TPL-LEAK-CHECK',         '泄漏巡查模板',           70, 120),
    ('IM-TPL-VALVE-POSITION',     '阀门状态核对模板',       80,  90),
    ('IM-TPL-VALVE-OPERATE',      '阀门开关灵活度检查模板', 90, 150),
    ('IM-TPL-VIBRATION-LISTEN',   '振动异响听诊模板',      100, 100),
    ('IM-TPL-MOTOR-TEMP',         '电机温升检查模板',      110, 120),
    ('IM-TPL-LUBE-LEVEL',         '润滑油位检查模板',      120,  60),
    ('IM-TPL-GROUNDING-VISUAL',   '接地防雷外观检查模板',  130,  90),
    ('IM-TPL-FIRE-DIKE',          '防火堤与排水检查模板',  140, 120),
    ('IM-TPL-RELIEF-VALVE',       '呼吸阀安全附件检查模板',150, 100),
    ('IM-TPL-CAM-LENS',           '摄像机镜头清洁检查模板',160,  80),
    ('IM-TPL-CAM-IMAGE',          '视频画面质量检查模板',  170,  90),
    ('IM-TPL-CAM-PTZ',            '云台变焦功能检查模板',  180, 150),
    ('IM-TPL-CAM-IR',             '补光红外工作检查模板',  190,  70),
    ('IM-TPL-CAM-OSD',            '时间 OSD 核对模板',     200,  45),
    ('IM-TPL-INTERCOM',           '对讲通话清晰度检查模板',210, 120),
    ('IM-TPL-NETWORK-LINK',       '网络连接状态检查模板',  220,  60),
    ('IM-TPL-ACCESS-CARD',        '门禁刷卡识别检查模板',  230,  90),
    ('IM-TPL-DOOR-LOCK',          '门锁开合检查模板',      240,  75),
    ('IM-TPL-EMERGENCY-LIGHT',    '应急照明切换检查模板',  250, 100),
    ('IM-TPL-BATTERY-STATUS',     '蓄电池状态检查模板',    260,  80)
) AS v(code, name, sort, action_duration_sec)
WHERE e.deleted = false
  AND e.tenant_id = 1
  AND e.code = v.code;

-- ---------------------------------------------------------------------------
-- 2) 检查内容 → 方法模板（按编码/名称语义；仅填空绑定，不覆盖已有人工选择）
-- ---------------------------------------------------------------------------
WITH binds(item_code, method_code) AS (
  VALUES
    ('INS-ITEM-101',        'IM-TPL-VISUAL-01'),
    ('INS-ITEM-102',        'IM-TPL-FIRE-DIKE'),
    ('INS-ITEM-CAM-01',     'IM-TPL-CAM-LENS'),
    ('INS-ITEM-CAM-02',     'IM-TPL-CAM-IMAGE'),
    ('INS-ITEM-CAM-03',     'IM-TPL-CAM-PTZ'),
    ('INS-ITEM-CAM-04',     'IM-TPL-CAM-IR'),
    ('INS-ITEM-CAM-05',     'IM-TPL-VISUAL-01')
)
UPDATE ent_inspection_item_t1 i
SET
  method_template_id = t.id,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
FROM binds b
JOIN ent_inspection_method_t1 t
  ON t.deleted = false AND t.tenant_id = 1 AND t.code = b.method_code
WHERE i.deleted = false
  AND i.tenant_id = 1
  AND i.code = b.item_code
  AND i.method_template_id IS NULL;

WITH name_binds(name_pattern, method_code, priority) AS (
  VALUES
    -- priority 越小越优先（一项只绑一次）
    ('%镜头清洁%',           'IM-TPL-CAM-LENS', 10),
    ('%视频画面%',           'IM-TPL-CAM-IMAGE', 10),
    ('%云台%',               'IM-TPL-CAM-PTZ', 10),
    ('%补光灯%',             'IM-TPL-CAM-IR', 10),
    ('%时间OSD%',            'IM-TPL-CAM-OSD', 10),
    ('%通话清晰%',           'IM-TPL-INTERCOM', 10),
    ('%网络连接%',           'IM-TPL-NETWORK-LINK', 10),
    ('%刷卡%',               'IM-TPL-ACCESS-CARD', 10),
    ('%门锁开合%',           'IM-TPL-DOOR-LOCK', 10),
    ('%点亮与应急%',         'IM-TPL-EMERGENCY-LIGHT', 10),
    ('%电池状态%',           'IM-TPL-BATTERY-STATUS', 10),
    ('%液位/温度%',          'IM-TPL-LEVEL-TEMP-READ', 10),
    ('%呼吸阀%',             'IM-TPL-RELIEF-VALVE', 10),
    ('%防火堤%',             'IM-TPL-FIRE-DIKE', 10),
    ('%进出油阀门%',         'IM-TPL-VALVE-POSITION', 10),
    ('%阀体及法兰%泄漏%',    'IM-TPL-LEAK-CHECK', 10),
    ('%密封处%泄漏%',        'IM-TPL-LEAK-CHECK', 10),
    ('%开关灵活%',           'IM-TPL-VALVE-OPERATE', 10),
    ('%阀位指示%',           'IM-TPL-VALVE-POSITION', 10),
    ('%泵体及基础%',         'IM-TPL-VIBRATION-LISTEN', 10),
    ('%运行声音%',           'IM-TPL-VIBRATION-LISTEN', 10),
    ('%进出口压力%',         'IM-TPL-PRESSURE-READ', 10),
    ('%润滑油位%',           'IM-TPL-LUBE-LEVEL', 10),
    ('%电机温度%',           'IM-TPL-MOTOR-TEMP', 10),
    ('%接地与防雷%',         'IM-TPL-GROUNDING-VISUAL', 10),
    ('%安装位置正确%',       'IM-TPL-MARKING-READ', 20),
    ('%标识清晰%',           'IM-TPL-MARKING-READ', 20),
    ('%罐体外观%',           'IM-TPL-VISUAL-01', 30),
    ('%外观完好%',           'IM-TPL-VISUAL-01', 40)
)
UPDATE ent_inspection_item_t1 i
SET
  method_template_id = x.method_id,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
FROM (
  SELECT DISTINCT ON (i2.id)
    i2.id AS item_id,
    t.id AS method_id
  FROM ent_inspection_item_t1 i2
  JOIN name_binds nb ON i2.name LIKE nb.name_pattern
  JOIN ent_inspection_method_t1 t
    ON t.deleted = false AND t.tenant_id = 1 AND t.code = nb.method_code
  WHERE i2.deleted = false
    AND i2.tenant_id = 1
    AND i2.method_template_id IS NULL
  ORDER BY i2.id, nb.priority, nb.method_code
) x
WHERE i.id = x.item_id;
