-- ============================================================================
-- 字段库单位表（dynamic_unit）
-- 字段编辑里的「选择单位」只读这张表。编码与字段上已经保存的 unit 一致，名称用中文。
-- 幂等：同一租户、同一编码已存在则跳过。不改字段上已有的单位文字。
-- ============================================================================

SET search_path TO dynamicbusiness;

INSERT INTO dynamic_unit (
  name, code, unit_type, sort, status, remark, creator, updater, deleted, tenant_id
)
SELECT v.name, v.code, v.unit_type, v.sort, 1, '', 'seed', 'seed', false, t.tenant_id
FROM (
  VALUES
    -- 时间：动作耗时存 min，停留时长存 s，停机时长存「小时」
    (1,  '秒',           's',    'TIME'),
    (2,  '分',           'min',  'TIME'),
    (3,  '小时',         'h',    'TIME'),
    (4,  '小时',         '小时', 'TIME'),
    (5,  '天',           '天',   'TIME'),
    (6,  '月',           '月',   'TIME'),
    (7,  '年',           '年',   'TIME'),
    -- 长度：起飞高度等
    (11, '毫米',         'mm',   'LENGTH'),
    (12, '厘米',         'cm',   'LENGTH'),
    (13, '米',           'm',    'LENGTH'),
    (14, '米',           '米',   'LENGTH'),
    (15, '千米',         'km',   'LENGTH'),
    -- 面积、体积、重量、温度
    (21, '平方米',       'm²',   'AREA'),
    (31, '立方米',       'm³',   'VOLUME'),
    (41, '克',           'g',    'WEIGHT'),
    (42, '千克',         'kg',   'WEIGHT'),
    (43, '吨',           't',    'WEIGHT'),
    (51, '摄氏度',       '℃',    'TEMPERATURE'),
    -- 字段上已经在用、上面几类装不下的
    (61, '百分比',       '%',    'RATIO'),
    (62, '件',           '件',   'COUNT'),
    (63, '人',           '人',   'COUNT'),
    (64, '元',           '元',   'CURRENCY'),
    (65, '度',           'deg',  'ANGLE'),
    (66, '伏特',         'V',    'ELECTRIC'),
    (67, '安培',         'A',    'ELECTRIC'),
    (68, '千瓦',         'kW',   'ELECTRIC'),
    (69, '兆帕',         'MPa',  'PRESSURE'),
    (70, '分贝',         'dB',   'SOUND'),
    (71, '转每分钟',     'rpm',  'ROTATION'),
    (72, '百万分率',     'ppm',  'CONCENTRATION'),
    (73, '立方米每小时', 'm³/h', 'FLOW'),
    (74, '吨标准煤',     'tce',  'OTHER'),
    (75, '吨二氧化碳',   'tCO₂', 'OTHER')
) AS v(sort, name, code, unit_type)
CROSS JOIN (
  SELECT DISTINCT tenant_id
  FROM dynamic_field
  WHERE deleted = false
) AS t
WHERE NOT EXISTS (
  SELECT 1
  FROM dynamic_unit u
  WHERE u.tenant_id = t.tenant_id
    AND u.code = v.code
    AND u.deleted = false
);

-- 已写入的单位按查找分组归类。下拉按 unit_type 分组显示。
UPDATE dynamic_unit AS u
SET unit_type = v.unit_type,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM (
  VALUES
    ('%', 'RATIO'),
    ('件', 'COUNT'),
    ('人', 'COUNT'),
    ('元', 'CURRENCY'),
    ('deg', 'ANGLE'),
    ('V', 'ELECTRIC'),
    ('A', 'ELECTRIC'),
    ('kW', 'ELECTRIC'),
    ('MPa', 'PRESSURE'),
    ('dB', 'SOUND'),
    ('rpm', 'ROTATION'),
    ('ppm', 'CONCENTRATION'),
    ('m³/h', 'FLOW')
) AS v(code, unit_type)
WHERE u.code = v.code
  AND u.deleted = false
  AND u.unit_type IS DISTINCT FROM v.unit_type;

-- 名称已经写明度量的数字字段，且单位还空着，才写入。不改已经有单位的字段。
-- 地址、序号、版本号、类型码、系数、读数、量程不写：看不出该用哪一个单位。
UPDATE dynamic_field AS f
SET unit = v.unit,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM (
  VALUES
    ('FLD-BASE-region-pipeline_km_total', 'km'),
    ('FLD-BASE-region-pipeline_km_ng', 'km'),
    ('FLD-BASE-region-pipeline_km_cr', 'km'),
    ('FLD-BASE-region-pipeline_km_cp', 'km'),
    ('FLD-BASE-region-service_radius_km', 'km'),
    ('estimated_duration', 'h'),
    ('expected_service_life', '年'),
    ('estimated_cost', '元'),
    ('max_height_m', 'm'),
    ('min_height_m', 'm'),
    ('longitude', 'deg'),
    ('latitude', 'deg'),
    ('centroid_lng', 'deg'),
    ('centroid_lat', 'deg'),
    ('available_quantity', '件'),
    ('stock_quantity', '件'),
    ('total_quantity', '件'),
    ('min_stock', '件')
) AS v(code, unit)
WHERE f.code = v.code
  AND f.deleted = false
  AND (f.unit IS NULL OR btrim(f.unit) = '');
