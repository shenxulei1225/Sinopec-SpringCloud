-- 大疆设备详情上的官网规格。
-- 负责：把官网规格页上的机身、摄像机光学参数、机场参数写成型号字段的默认值，并补进还空着的设备。这些数给设备详情看，不交给动作库。
-- 不负责：飞向目标点、悬停等待、拍摄角度、焦距这些一次检查才填的参数；东古；Matrice 300 RTK 的性能（官网规格页没有返回性能表）；禅思云台相机的光学参数（这次没有打开对应规格页）。
-- 某一台设备上已经有数的不覆盖。再次执行会按本文件覆盖型号默认值。
-- 重量单位是克。机场公斤数按官网换算。有两档海拔时，最大飞行海拔记标准桨，高海拔桨另记一行。
-- 出处与 dynamic_equipment_dji_flight_performance.sql 相同，另加：
--   机场 2  https://enterprise.dji.com/dock-2/specs
--   机场 3  https://enterprise.dji.com/dock-3/specs

SET search_path TO dynamicbusiness;

BEGIN;

INSERT INTO dynamic_field (
  code, name, type, description, source, status, index_strategy, options, tenant_id, creator
) VALUES
  ('spec_weight_g', '设备重量', 'NUMBER', '官网规格表上的重量，单位克。飞机含电池和桨、不含第三方负载。机场是不含飞机的重量。', 'USER', 1, 'BTREE', NULL, 1, 'seed'),
  ('spec_max_takeoff_weight_g', '最大起飞重量', 'NUMBER', '官网规格表上的最大起飞重量，单位克。', 'USER', 1, 'BTREE', NULL, 1, 'seed'),
  ('spec_max_payload_g', '最大载重', 'NUMBER', '官网规格表上的最大载重，单位克。该页没有这一行时不分配。', 'USER', 1, 'BTREE', NULL, 1, 'seed'),
  ('spec_wheelbase_mm', '对角轴距', 'NUMBER', '官网规格表上的对角轴距，单位毫米。', 'USER', 1, 'BTREE', NULL, 1, 'seed'),
  ('spec_dimensions', '外形尺寸', 'TEXT', '官网规格表上不含桨的展开尺寸，长×宽×高，单位毫米。机场写下开盖和合盖两行。', 'USER', 1, 'BTREE', NULL, 1, 'seed'),
  ('spec_max_pitch_angle', '最大倾斜角', 'NUMBER', '官网规格表上的最大倾斜角，单位度。有普通档和运动档时记运动档。', 'USER', 1, 'BTREE', NULL, 1, 'seed'),
  ('spec_max_tilt_descent_speed', '最大倾斜下降速度', 'NUMBER', '官网规格表上的最大倾斜下降速度，单位米每秒。该页没有这一行时不分配。', 'USER', 1, 'BTREE', NULL, 1, 'seed'),
  ('spec_max_altitude_m', '最大飞行海拔', 'NUMBER', '官网规格表上的最大飞行海拔，单位米。有标准桨和高海拔桨时记标准桨。', 'USER', 1, 'BTREE', NULL, 1, 'seed'),
  ('spec_max_altitude_high_m', '高海拔桨最大飞行海拔', 'NUMBER', '官网规格表上换高海拔桨后的最大飞行海拔，单位米。该页只有一个海拔时不分配。', 'USER', 1, 'BTREE', NULL, 1, 'seed'),
  ('spec_max_operating_altitude_m', '最大作业海拔', 'NUMBER', '官网规格表上机场的最大作业海拔，单位米。', 'USER', 1, 'BTREE', NULL, 1, 'seed'),
  ('spec_operating_temperature', '工作环境温度', 'TEXT', '官网规格表上的工作环境温度。', 'USER', 1, 'BTREE', NULL, 1, 'seed'),
  ('spec_input_power', '输入功率', 'TEXT', '官网规格表上机场的最大输入功率。', 'USER', 1, 'BTREE', NULL, 1, 'seed'),
  ('spec_charge_time', '充电时间', 'TEXT', '官网规格表上的充电时间，连同测试条件一起写。', 'USER', 1, 'BTREE', NULL, 1, 'seed'),
  ('spec_max_landing_wind', '最大允许降落风速', 'NUMBER', '官网规格表上机场允许降落的最大风速，单位米每秒。', 'USER', 1, 'BTREE', NULL, 1, 'seed'),
  ('wide_sensor', '广角传感器', 'TEXT', '官网规格表上广角相机的传感器。', 'USER', 1, 'BTREE', NULL, 1, 'seed'),
  ('wide_focal_length', '广角等效焦距', 'TEXT', '官网规格表上广角相机的等效焦距。', 'USER', 1, 'BTREE', NULL, 1, 'seed'),
  ('wide_aperture', '广角光圈', 'TEXT', '官网规格表上广角相机的光圈。', 'USER', 1, 'BTREE', NULL, 1, 'seed'),
  ('wide_photo_size', '广角照片尺寸', 'TEXT', '官网规格表上广角相机的最大照片尺寸。', 'USER', 1, 'BTREE', NULL, 1, 'seed'),
  ('zoom_focal_length', '变焦等效焦距', 'TEXT', '官网规格表上变焦相机的等效焦距。', 'USER', 1, 'BTREE', NULL, 1, 'seed'),
  ('zoom_aperture', '变焦光圈', 'TEXT', '官网规格表上变焦相机的光圈。', 'USER', 1, 'BTREE', NULL, 1, 'seed'),
  ('zoom_photo_size', '变焦照片尺寸', 'TEXT', '官网规格表上变焦相机的最大照片尺寸。', 'USER', 1, 'BTREE', NULL, 1, 'seed'),
  ('mid_tele_focal_length', '中长焦等效焦距', 'TEXT', '官网规格表上中长焦相机的等效焦距。', 'USER', 1, 'BTREE', NULL, 1, 'seed'),
  ('mid_tele_aperture', '中长焦光圈', 'TEXT', '官网规格表上中长焦相机的光圈。', 'USER', 1, 'BTREE', NULL, 1, 'seed'),
  ('mid_tele_photo_size', '中长焦照片尺寸', 'TEXT', '官网规格表上中长焦相机的最大照片尺寸。', 'USER', 1, 'BTREE', NULL, 1, 'seed'),
  ('tele_focal_length', '长焦等效焦距', 'TEXT', '官网规格表上长焦相机的等效焦距。', 'USER', 1, 'BTREE', NULL, 1, 'seed'),
  ('tele_aperture', '长焦光圈', 'TEXT', '官网规格表上长焦相机的光圈。', 'USER', 1, 'BTREE', NULL, 1, 'seed'),
  ('tele_photo_size', '长焦照片尺寸', 'TEXT', '官网规格表上长焦相机的最大照片尺寸。', 'USER', 1, 'BTREE', NULL, 1, 'seed'),
  ('thermal_resolution', '红外分辨率', 'TEXT', '官网规格表上红外相机的分辨率。', 'USER', 1, 'BTREE', NULL, 1, 'seed'),
  ('thermal_focal_length', '红外等效焦距', 'TEXT', '官网规格表上红外相机的等效焦距。', 'USER', 1, 'BTREE', NULL, 1, 'seed'),
  ('iso_normal', '感光度范围', 'TEXT', '官网规格表上普通档的感光度范围。夜景档不写在这一行。', 'USER', 1, 'BTREE', NULL, 1, 'seed')
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  description = EXCLUDED.description,
  index_strategy = EXCLUDED.index_strategy,
  status = 1,
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

UPDATE dynamic_field
SET
  options = '[
    {"label":"IP54","value":"IP54"},
    {"label":"IP55","value":"IP55"},
    {"label":"IP56","value":"IP56"}
  ]',
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
WHERE code = 'waterproof_grade'
  AND tenant_id = 1
  AND deleted = false
  AND (options IS NULL OR options NOT LIKE '%IP56%');

CREATE TEMP TABLE spec_row (
  model_code text NOT NULL,
  field_code text NOT NULL,
  default_value text NOT NULL,
  sort_no int NOT NULL,
  PRIMARY KEY (model_code, field_code)
) ON COMMIT DROP;

INSERT INTO spec_row (model_code, field_code, default_value, sort_no) VALUES
  ('dji-m30', 'equipment_model', 'Matrice 30', 4),
  ('dji-m30', 'spec_weight_g', '3770', 70),
  ('dji-m30', 'spec_max_takeoff_weight_g', '4069', 71),
  ('dji-m30', 'spec_wheelbase_mm', '668', 72),
  ('dji-m30', 'spec_dimensions', '展开 470×585×215 mm', 73),
  ('dji-m30', 'spec_max_pitch_angle', '35', 74),
  ('dji-m30', 'spec_max_tilt_descent_speed', '7', 75),
  ('dji-m30', 'spec_max_altitude_m', '5000', 76),
  ('dji-m30', 'spec_max_altitude_high_m', '7000', 77),
  ('dji-m30', 'spec_operating_temperature', '-20°C 到 50°C', 78),

  ('dji-m30t', 'equipment_model', 'Matrice 30T', 4),
  ('dji-m30t', 'spec_weight_g', '3770', 70),
  ('dji-m30t', 'spec_max_takeoff_weight_g', '4069', 71),
  ('dji-m30t', 'spec_wheelbase_mm', '668', 72),
  ('dji-m30t', 'spec_dimensions', '展开 470×585×215 mm', 73),
  ('dji-m30t', 'spec_max_pitch_angle', '35', 74),
  ('dji-m30t', 'spec_max_tilt_descent_speed', '7', 75),
  ('dji-m30t', 'spec_max_altitude_m', '5000', 76),
  ('dji-m30t', 'spec_max_altitude_high_m', '7000', 77),
  ('dji-m30t', 'spec_operating_temperature', '-20°C 到 50°C', 78),

  ('dji-m350-rtk', 'equipment_model', 'Matrice 350 RTK', 4),
  ('dji-m350-rtk', 'spec_weight_g', '6470', 70),
  ('dji-m350-rtk', 'spec_max_takeoff_weight_g', '9200', 71),
  ('dji-m350-rtk', 'spec_wheelbase_mm', '895', 72),
  ('dji-m350-rtk', 'spec_dimensions', '展开 810×670×430 mm', 73),
  ('dji-m350-rtk', 'spec_max_pitch_angle', '30', 74),
  ('dji-m350-rtk', 'spec_max_tilt_descent_speed', '7', 75),
  ('dji-m350-rtk', 'spec_max_altitude_m', '5000', 76),
  ('dji-m350-rtk', 'spec_max_altitude_high_m', '7000', 77),
  ('dji-m350-rtk', 'spec_operating_temperature', '-20°C 到 50°C', 78),

  ('dji-m3e', 'equipment_model', 'Mavic 3E', 4),
  ('dji-m3e', 'spec_weight_g', '915', 70),
  ('dji-m3e', 'spec_max_takeoff_weight_g', '1050', 71),
  ('dji-m3e', 'spec_wheelbase_mm', '380.1', 72),
  ('dji-m3e', 'spec_dimensions', '展开 347.5×283×107.7 mm', 73),
  ('dji-m3e', 'spec_max_pitch_angle', '35', 74),
  ('dji-m3e', 'spec_max_altitude_m', '6000', 76),
  ('dji-m3e', 'spec_operating_temperature', '-10°C 到 40°C', 78),

  ('dji-m3t', 'equipment_model', 'Mavic 3T', 4),
  ('dji-m3t', 'spec_weight_g', '920', 70),
  ('dji-m3t', 'spec_max_takeoff_weight_g', '1050', 71),
  ('dji-m3t', 'spec_wheelbase_mm', '380.1', 72),
  ('dji-m3t', 'spec_dimensions', '展开 347.5×283×107.7 mm', 73),
  ('dji-m3t', 'spec_max_pitch_angle', '35', 74),
  ('dji-m3t', 'spec_max_altitude_m', '6000', 76),
  ('dji-m3t', 'spec_operating_temperature', '-10°C 到 40°C', 78),

  ('dji-m3ta', 'equipment_model', 'Mavic 3TA', 4),
  ('dji-m3ta', 'spec_weight_g', '920', 70),
  ('dji-m3ta', 'spec_max_takeoff_weight_g', '1050', 71),
  ('dji-m3ta', 'spec_wheelbase_mm', '380.1', 72),
  ('dji-m3ta', 'spec_dimensions', '展开 347.5×283×107.7 mm', 73),
  ('dji-m3ta', 'spec_max_pitch_angle', '35', 74),
  ('dji-m3ta', 'spec_max_altitude_m', '6000', 76),
  ('dji-m3ta', 'spec_operating_temperature', '-10°C 到 40°C', 78),

  ('dji-m3d', 'equipment_model', 'Matrice 3D', 4),
  ('dji-m3d', 'spec_weight_g', '1410', 70),
  ('dji-m3d', 'spec_max_takeoff_weight_g', '1610', 71),
  ('dji-m3d', 'spec_wheelbase_mm', '463.2', 72),
  ('dji-m3d', 'spec_dimensions', '335×398×153 mm', 73),
  ('dji-m3d', 'spec_max_pitch_angle', '25', 74),
  ('dji-m3d', 'spec_max_altitude_m', '4000', 76),
  ('dji-m3d', 'spec_operating_temperature', '-20°C 到 45°C', 78),

  ('dji-m3td', 'equipment_model', 'Matrice 3TD', 4),
  ('dji-m3td', 'spec_weight_g', '1410', 70),
  ('dji-m3td', 'spec_max_takeoff_weight_g', '1610', 71),
  ('dji-m3td', 'spec_wheelbase_mm', '463.2', 72),
  ('dji-m3td', 'spec_dimensions', '335×398×153 mm', 73),
  ('dji-m3td', 'spec_max_pitch_angle', '25', 74),
  ('dji-m3td', 'spec_max_altitude_m', '4000', 76),
  ('dji-m3td', 'spec_operating_temperature', '-20°C 到 45°C', 78),

  ('dji-m4e', 'equipment_model', 'Matrice 4E', 4),
  ('dji-m4e', 'spec_weight_g', '1219', 70),
  ('dji-m4e', 'spec_max_takeoff_weight_g', '1420', 71),
  ('dji-m4e', 'spec_max_payload_g', '200', 71),
  ('dji-m4e', 'spec_wheelbase_mm', '438.8', 72),
  ('dji-m4e', 'spec_dimensions', '展开 307.0×387.5×149.5 mm', 73),
  ('dji-m4e', 'spec_max_pitch_angle', '35', 74),
  ('dji-m4e', 'spec_max_altitude_m', '6000', 76),
  ('dji-m4e', 'spec_operating_temperature', '-10°C 到 40°C', 78),

  ('dji-m4t', 'equipment_model', 'Matrice 4T', 4),
  ('dji-m4t', 'spec_weight_g', '1219', 70),
  ('dji-m4t', 'spec_max_takeoff_weight_g', '1420', 71),
  ('dji-m4t', 'spec_max_payload_g', '200', 71),
  ('dji-m4t', 'spec_wheelbase_mm', '438.8', 72),
  ('dji-m4t', 'spec_dimensions', '展开 307.0×387.5×149.5 mm', 73),
  ('dji-m4t', 'spec_max_pitch_angle', '35', 74),
  ('dji-m4t', 'spec_max_altitude_m', '6000', 76),
  ('dji-m4t', 'spec_operating_temperature', '-10°C 到 40°C', 78),

  ('dji-m4d', 'equipment_model', 'Matrice 4D', 4),
  ('dji-m4d', 'spec_weight_g', '1850', 70),
  ('dji-m4d', 'spec_max_takeoff_weight_g', '2090', 71),
  ('dji-m4d', 'spec_wheelbase_mm', '498.5', 72),
  ('dji-m4d', 'spec_dimensions', '377.7×416.2×212.5 mm', 73),
  ('dji-m4d', 'spec_max_pitch_angle', '30', 74),
  ('dji-m4d', 'spec_max_altitude_m', '6500', 76),
  ('dji-m4d', 'spec_operating_temperature', '-20°C 到 50°C', 78),

  ('dji-m4td', 'equipment_model', 'Matrice 4TD', 4),
  ('dji-m4td', 'spec_weight_g', '1850', 70),
  ('dji-m4td', 'spec_max_takeoff_weight_g', '2090', 71),
  ('dji-m4td', 'spec_wheelbase_mm', '498.5', 72),
  ('dji-m4td', 'spec_dimensions', '377.7×416.2×212.5 mm', 73),
  ('dji-m4td', 'spec_max_pitch_angle', '30', 74),
  ('dji-m4td', 'spec_max_altitude_m', '6500', 76),
  ('dji-m4td', 'spec_operating_temperature', '-20°C 到 50°C', 78),

  ('dji-m400', 'equipment_model', 'Matrice 400', 4),
  ('dji-m400', 'spec_weight_g', '9740', 70),
  ('dji-m400', 'spec_max_takeoff_weight_g', '15800', 71),
  ('dji-m400', 'spec_max_payload_g', '6000', 71),
  ('dji-m400', 'spec_wheelbase_mm', '1070', 72),
  ('dji-m400', 'spec_dimensions', '展开 980×760×480 mm', 73),
  ('dji-m400', 'spec_max_pitch_angle', '35', 74),
  ('dji-m400', 'spec_max_altitude_m', '7000', 76),
  ('dji-m400', 'spec_operating_temperature', '-20°C 到 50°C', 78),

  ('dji-m300-rtk', 'equipment_model', 'Matrice 300 RTK', 4),
  ('dji-dock-1', 'equipment_model', 'DJI Dock', 4),

  ('dji-dock-2', 'equipment_model', 'DJI Dock 2', 4),
  ('dji-dock-2', 'spec_weight_g', '34000', 70),
  ('dji-dock-2', 'spec_dimensions', '开盖 1228×583×412 mm；合盖 570×583×465 mm', 73),
  ('dji-dock-2', 'spec_input_power', '最大 1000 W', 79),
  ('dji-dock-2', 'spec_operating_temperature', '-25°C 到 45°C', 78),
  ('dji-dock-2', 'spec_max_landing_wind', '8', 80),
  ('dji-dock-2', 'spec_max_operating_altitude_m', '4000', 76),
  ('dji-dock-2', 'spec_charge_time', '32 分钟（25°C，飞机关机，从 20% 充到 90%）', 81),
  ('dji-dock-2', 'waterproof_grade', 'IP55', 22),

  ('dji-dock-3', 'equipment_model', 'DJI Dock 3', 4),
  ('dji-dock-3', 'spec_weight_g', '55000', 70),
  ('dji-dock-3', 'spec_dimensions', '开盖 1760×745×485 mm；合盖 640×745×770 mm', 73),
  ('dji-dock-3', 'spec_input_power', '最大 800 W', 79),
  ('dji-dock-3', 'spec_operating_temperature', '-30°C 到 50°C', 78),
  ('dji-dock-3', 'spec_max_landing_wind', '12', 80),
  ('dji-dock-3', 'spec_max_operating_altitude_m', '4500', 76),
  ('dji-dock-3', 'spec_charge_time', '27 分钟（25°C，飞机关机，从 15% 充到 95%）', 81),
  ('dji-dock-3', 'waterproof_grade', 'IP56', 22),

  ('dji-pl-m30-cam', 'wide_sensor', '1/2 英寸 CMOS，有效像素 1200 万', 90),
  ('dji-pl-m30-cam', 'wide_focal_length', '24 mm', 91),
  ('dji-pl-m30-cam', 'wide_aperture', 'f/2.8', 92),
  ('dji-pl-m30-cam', 'wide_photo_size', '4000×3000', 93),
  ('dji-pl-m30-cam', 'zoom_focal_length', '113–405 mm', 94),
  ('dji-pl-m30-cam', 'zoom_aperture', 'f/2.8–f/4.2', 95),
  ('dji-pl-m30-cam', 'zoom_photo_size', '8000×6000', 96),
  ('dji-pl-m30-cam', 'iso_normal', '100–25600', 97),

  ('dji-pl-m30t-cam', 'wide_sensor', '1/2 英寸 CMOS，有效像素 1200 万', 90),
  ('dji-pl-m30t-cam', 'wide_focal_length', '24 mm', 91),
  ('dji-pl-m30t-cam', 'wide_aperture', 'f/2.8', 92),
  ('dji-pl-m30t-cam', 'wide_photo_size', '4000×3000', 93),
  ('dji-pl-m30t-cam', 'zoom_focal_length', '113–405 mm', 94),
  ('dji-pl-m30t-cam', 'zoom_aperture', 'f/2.8–f/4.2', 95),
  ('dji-pl-m30t-cam', 'zoom_photo_size', '8000×6000', 96),
  ('dji-pl-m30t-cam', 'thermal_resolution', '640×512，超分辨率 1280×1024', 98),
  ('dji-pl-m30t-cam', 'thermal_focal_length', '40 mm', 99),
  ('dji-pl-m30t-cam', 'iso_normal', '100–25600', 97),

  ('dji-pl-m3e-cam', 'wide_sensor', '4/3 CMOS，有效像素 2000 万', 90),
  ('dji-pl-m3e-cam', 'wide_focal_length', '24 mm', 91),
  ('dji-pl-m3e-cam', 'wide_aperture', 'f/2.8–f/11', 92),
  ('dji-pl-m3e-cam', 'wide_photo_size', '5280×3956', 93),
  ('dji-pl-m3e-cam', 'tele_focal_length', '162 mm', 100),
  ('dji-pl-m3e-cam', 'tele_aperture', 'f/4.4', 101),
  ('dji-pl-m3e-cam', 'tele_photo_size', '4000×3000', 102),
  ('dji-pl-m3e-cam', 'iso_normal', '100–6400', 97),

  ('dji-pl-m3t-cam', 'wide_sensor', '1/2 英寸 CMOS，有效像素 4800 万', 90),
  ('dji-pl-m3t-cam', 'wide_focal_length', '24 mm', 91),
  ('dji-pl-m3t-cam', 'wide_aperture', 'f/2.8', 92),
  ('dji-pl-m3t-cam', 'wide_photo_size', '8000×6000', 93),
  ('dji-pl-m3t-cam', 'tele_focal_length', '162 mm', 100),
  ('dji-pl-m3t-cam', 'tele_aperture', 'f/4.4', 101),
  ('dji-pl-m3t-cam', 'tele_photo_size', '4000×3000', 102),
  ('dji-pl-m3t-cam', 'thermal_resolution', '640×512', 98),
  ('dji-pl-m3t-cam', 'thermal_focal_length', '40 mm', 99),
  ('dji-pl-m3t-cam', 'iso_normal', '100–25600', 97),

  ('dji-pl-m3ta-cam', 'wide_sensor', '1/2 英寸 CMOS，有效像素 4800 万', 90),
  ('dji-pl-m3ta-cam', 'wide_focal_length', '24 mm', 91),
  ('dji-pl-m3ta-cam', 'wide_aperture', 'f/2.8', 92),
  ('dji-pl-m3ta-cam', 'wide_photo_size', '8000×6000', 93),
  ('dji-pl-m3ta-cam', 'tele_focal_length', '162 mm', 100),
  ('dji-pl-m3ta-cam', 'tele_aperture', 'f/4.4', 101),
  ('dji-pl-m3ta-cam', 'tele_photo_size', '4000×3000', 102),
  ('dji-pl-m3ta-cam', 'thermal_resolution', '640×512', 98),
  ('dji-pl-m3ta-cam', 'thermal_focal_length', '60 mm', 99),
  ('dji-pl-m3ta-cam', 'iso_normal', '100–25600', 97),

  ('dji-pl-m3d-cam', 'wide_sensor', '4/3 CMOS，有效像素 2000 万', 90),
  ('dji-pl-m3d-cam', 'wide_focal_length', '24 mm', 91),
  ('dji-pl-m3d-cam', 'wide_aperture', 'f/2.8–f/11', 92),
  ('dji-pl-m3d-cam', 'wide_photo_size', '5280×3956', 93),
  ('dji-pl-m3d-cam', 'tele_focal_length', '162 mm', 100),
  ('dji-pl-m3d-cam', 'tele_aperture', 'f/4.4', 101),
  ('dji-pl-m3d-cam', 'iso_normal', '100–6400', 97),

  ('dji-pl-m3td-cam', 'wide_sensor', '1/1.32 英寸 CMOS，有效像素 4800 万', 90),
  ('dji-pl-m3td-cam', 'wide_focal_length', '24 mm', 91),
  ('dji-pl-m3td-cam', 'wide_aperture', 'f/1.7', 92),
  ('dji-pl-m3td-cam', 'wide_photo_size', '8064×6048', 93),
  ('dji-pl-m3td-cam', 'tele_focal_length', '162 mm', 100),
  ('dji-pl-m3td-cam', 'tele_aperture', 'f/4.4', 101),
  ('dji-pl-m3td-cam', 'thermal_resolution', '640×512，超分辨率 1280×1024', 98),
  ('dji-pl-m3td-cam', 'thermal_focal_length', '40 mm', 99),
  ('dji-pl-m3td-cam', 'iso_normal', '100–25600', 97),

  ('dji-pl-m4e-cam', 'wide_sensor', '4/3 CMOS，有效像素 2000 万', 90),
  ('dji-pl-m4e-cam', 'wide_focal_length', '24 mm', 91),
  ('dji-pl-m4e-cam', 'wide_aperture', 'f/2.8–f/11', 92),
  ('dji-pl-m4e-cam', 'wide_photo_size', '5280×3956', 93),
  ('dji-pl-m4e-cam', 'mid_tele_focal_length', '70 mm', 94),
  ('dji-pl-m4e-cam', 'mid_tele_aperture', 'f/2.8', 95),
  ('dji-pl-m4e-cam', 'mid_tele_photo_size', '8064×6048', 96),
  ('dji-pl-m4e-cam', 'tele_focal_length', '168 mm', 100),
  ('dji-pl-m4e-cam', 'tele_aperture', 'f/2.8', 101),
  ('dji-pl-m4e-cam', 'tele_photo_size', '8192×6144', 102),
  ('dji-pl-m4e-cam', 'iso_normal', '100–25600', 97),

  ('dji-pl-m4t-cam', 'wide_sensor', '1/1.3 英寸 CMOS，有效像素 4800 万', 90),
  ('dji-pl-m4t-cam', 'wide_focal_length', '24 mm', 91),
  ('dji-pl-m4t-cam', 'wide_aperture', 'f/1.7', 92),
  ('dji-pl-m4t-cam', 'wide_photo_size', '8064×6048', 93),
  ('dji-pl-m4t-cam', 'mid_tele_focal_length', '70 mm', 94),
  ('dji-pl-m4t-cam', 'mid_tele_aperture', 'f/2.8', 95),
  ('dji-pl-m4t-cam', 'mid_tele_photo_size', '8064×6048', 96),
  ('dji-pl-m4t-cam', 'tele_focal_length', '168 mm', 100),
  ('dji-pl-m4t-cam', 'tele_aperture', 'f/2.8', 101),
  ('dji-pl-m4t-cam', 'tele_photo_size', '8192×6144', 102),
  ('dji-pl-m4t-cam', 'thermal_resolution', '640×512', 98),
  ('dji-pl-m4t-cam', 'thermal_focal_length', '53 mm', 99),
  ('dji-pl-m4t-cam', 'iso_normal', '100–25600', 97),

  ('dji-pl-m4d-cam', 'wide_sensor', '4/3 CMOS，有效像素 2000 万', 90),
  ('dji-pl-m4d-cam', 'wide_focal_length', '24 mm', 91),
  ('dji-pl-m4d-cam', 'wide_aperture', 'f/2.8–f/11', 92),
  ('dji-pl-m4d-cam', 'wide_photo_size', '5280×3956', 93),
  ('dji-pl-m4d-cam', 'mid_tele_focal_length', '70 mm', 94),
  ('dji-pl-m4d-cam', 'mid_tele_aperture', 'f/2.8', 95),
  ('dji-pl-m4d-cam', 'mid_tele_photo_size', '8064×6048', 96),
  ('dji-pl-m4d-cam', 'tele_focal_length', '168 mm', 100),
  ('dji-pl-m4d-cam', 'tele_aperture', 'f/2.8', 101),
  ('dji-pl-m4d-cam', 'tele_photo_size', '8192×6144', 102),
  ('dji-pl-m4d-cam', 'iso_normal', '100–25600', 97),

  ('dji-pl-m4td-cam', 'wide_sensor', '1/1.3 英寸 CMOS，有效像素 4800 万', 90),
  ('dji-pl-m4td-cam', 'wide_focal_length', '24 mm', 91),
  ('dji-pl-m4td-cam', 'wide_aperture', 'f/1.7', 92),
  ('dji-pl-m4td-cam', 'wide_photo_size', '8064×6048', 93),
  ('dji-pl-m4td-cam', 'mid_tele_focal_length', '70 mm', 94),
  ('dji-pl-m4td-cam', 'mid_tele_aperture', 'f/2.8', 95),
  ('dji-pl-m4td-cam', 'mid_tele_photo_size', '8064×6048', 96),
  ('dji-pl-m4td-cam', 'tele_focal_length', '168 mm', 100),
  ('dji-pl-m4td-cam', 'tele_aperture', 'f/2.8', 101),
  ('dji-pl-m4td-cam', 'tele_photo_size', '8192×6144', 102),
  ('dji-pl-m4td-cam', 'thermal_resolution', '640×512', 98),
  ('dji-pl-m4td-cam', 'thermal_focal_length', '53 mm', 99),
  ('dji-pl-m4td-cam', 'iso_normal', '100–25600', 97);

INSERT INTO dynamic_model_field_assignment (
  model_id, model_code, field_id, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  default_value, tenant_id, creator
)
SELECT
  m.id, m.code, f.id, f.code,
  false,
  f.type = 'TEXT',
  f.type = 'NUMBER' OR f.type = 'ENUM',
  f.type = 'NUMBER',
  spec.sort_no,
  spec.default_value,
  1,
  'seed'
FROM spec_row spec
JOIN dynamic_model m ON m.code = spec.model_code AND m.deleted = false AND m.tenant_id = 1
JOIN dynamic_field f ON f.code = spec.field_code AND f.tenant_id = 1 AND f.deleted = false
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  default_value = EXCLUDED.default_value,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  sort = EXCLUDED.sort,
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

UPDATE dynamic_model_field_assignment a
SET
  default_value = '大疆',
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
FROM dynamic_model m
WHERE a.model_id = m.id
  AND m.deleted = false
  AND m.tenant_id = 1
  AND m.code LIKE 'dji-%'
  AND a.field_code IN ('brand', 'manufacturer')
  AND a.tenant_id = 1
  AND a.deleted = false
  AND (a.default_value IS NULL OR btrim(a.default_value) = '');

DO $$
BEGIN
  IF (SELECT count(DISTINCT model_code) FROM spec_row) <> 28 THEN
    RAISE EXCEPTION 'spec model count is not 28';
  END IF;
  IF (
    SELECT count(*)
    FROM spec_row spec
    JOIN dynamic_model m ON m.code = spec.model_code AND m.deleted = false AND m.tenant_id = 1
  ) <> (SELECT count(*) FROM spec_row) THEN
    RAISE EXCEPTION 'a spec model code is missing';
  END IF;
END $$;

UPDATE ent_equipment_t1 e
SET custom_fields = COALESCE(e.custom_fields, '{}'::jsonb) || patch.values,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM (
  SELECT e2.id,
    jsonb_object_agg(
      a.field_code,
      CASE
        WHEN f.type = 'NUMBER' THEN to_jsonb(a.default_value::numeric)
        ELSE to_jsonb(a.default_value)
      END
    ) AS values
  FROM ent_equipment_t1 e2
  JOIN dynamic_model_field_assignment a
    ON a.model_id = e2.model_id AND a.deleted = false AND a.tenant_id = 1
  JOIN dynamic_field f ON f.id = a.field_id AND f.deleted = false
  WHERE e2.deleted = false
    AND e2.tenant_id = 1
    AND a.default_value IS NOT NULL
    AND btrim(a.default_value) <> ''
    AND a.field_code IN (
      'spec_weight_g', 'spec_max_takeoff_weight_g', 'spec_max_payload_g', 'spec_wheelbase_mm',
      'spec_dimensions', 'spec_max_pitch_angle', 'spec_max_tilt_descent_speed',
      'spec_max_altitude_m', 'spec_max_altitude_high_m', 'spec_max_operating_altitude_m',
      'spec_operating_temperature', 'spec_input_power', 'spec_charge_time', 'spec_max_landing_wind',
      'wide_sensor', 'wide_focal_length', 'wide_aperture', 'wide_photo_size',
      'zoom_focal_length', 'zoom_aperture', 'zoom_photo_size',
      'mid_tele_focal_length', 'mid_tele_aperture', 'mid_tele_photo_size',
      'tele_focal_length', 'tele_aperture', 'tele_photo_size',
      'thermal_resolution', 'thermal_focal_length', 'iso_normal',
      'equipment_model', 'brand', 'manufacturer', 'waterproof_grade'
    )
    AND NULLIF(btrim(e2.custom_fields ->> a.field_code), '') IS NULL
  GROUP BY e2.id
) patch
WHERE e.id = patch.id;

DELETE FROM dynamic_entity_field_index i
USING ent_equipment_t1 e
JOIN dynamic_model m ON m.id = e.model_id AND m.deleted = false
WHERE i.entity_id = e.id
  AND i.tenant_id = 1
  AND e.tenant_id = 1
  AND e.deleted = false
  AND m.code LIKE 'dji-%'
  AND i.field_code IN (
    'spec_weight_g', 'spec_max_takeoff_weight_g', 'spec_max_payload_g', 'spec_wheelbase_mm',
    'spec_dimensions', 'spec_max_pitch_angle', 'spec_max_tilt_descent_speed',
    'spec_max_altitude_m', 'spec_max_altitude_high_m', 'spec_max_operating_altitude_m',
    'spec_operating_temperature', 'spec_input_power', 'spec_charge_time', 'spec_max_landing_wind',
    'wide_sensor', 'wide_focal_length', 'wide_aperture', 'wide_photo_size',
    'zoom_focal_length', 'zoom_aperture', 'zoom_photo_size',
    'mid_tele_focal_length', 'mid_tele_aperture', 'mid_tele_photo_size',
    'tele_focal_length', 'tele_aperture', 'tele_photo_size',
    'thermal_resolution', 'thermal_focal_length', 'iso_normal',
    'equipment_model', 'brand', 'manufacturer', 'waterproof_grade'
  );

INSERT INTO dynamic_entity_field_index (
  entity_id, model_id, field_code, value_string, value_number, tenant_id, creator
)
SELECT
  e.id,
  e.model_id,
  kv.key,
  CASE WHEN f.type = 'NUMBER' THEN NULL ELSE kv.value END,
  CASE WHEN f.type = 'NUMBER' THEN kv.value::numeric ELSE NULL END,
  1,
  'seed'
FROM ent_equipment_t1 e
JOIN dynamic_model m ON m.id = e.model_id AND m.deleted = false
CROSS JOIN LATERAL jsonb_each_text(e.custom_fields) kv
JOIN dynamic_field f ON f.code = kv.key AND f.tenant_id = 1 AND f.deleted = false
WHERE e.tenant_id = 1
  AND e.deleted = false
  AND m.code LIKE 'dji-%'
  AND kv.key IN (
    'spec_weight_g', 'spec_max_takeoff_weight_g', 'spec_max_payload_g', 'spec_wheelbase_mm',
    'spec_dimensions', 'spec_max_pitch_angle', 'spec_max_tilt_descent_speed',
    'spec_max_altitude_m', 'spec_max_altitude_high_m', 'spec_max_operating_altitude_m',
    'spec_operating_temperature', 'spec_input_power', 'spec_charge_time', 'spec_max_landing_wind',
    'wide_sensor', 'wide_focal_length', 'wide_aperture', 'wide_photo_size',
    'zoom_focal_length', 'zoom_aperture', 'zoom_photo_size',
    'mid_tele_focal_length', 'mid_tele_aperture', 'mid_tele_photo_size',
    'tele_focal_length', 'tele_aperture', 'tele_photo_size',
    'thermal_resolution', 'thermal_focal_length', 'iso_normal',
    'equipment_model', 'brand', 'manufacturer', 'waterproof_grade'
  )
  AND NULLIF(btrim(kv.value), '') IS NOT NULL;

COMMIT;
