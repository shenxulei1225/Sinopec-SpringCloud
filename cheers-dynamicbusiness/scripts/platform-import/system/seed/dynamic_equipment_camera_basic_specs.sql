SET search_path TO dynamicbusiness;

BEGIN;

CREATE TEMP TABLE new_cam_field (
    code text PRIMARY KEY,
    name text NOT NULL,
    type text NOT NULL,
    unit text
) ON COMMIT DROP;

INSERT INTO new_cam_field (code, name, type, unit) VALUES
    ('wide_fov', '广角视场角', 'RANGE', '°'),
    ('zoom_fov', '变焦视场角', 'RANGE', '°'),
    ('mid_tele_fov', '中长焦视场角', 'RANGE', '°'),
    ('tele_fov', '长焦视场角', 'RANGE', '°'),
    ('thermal_fov', '红外视场角', 'RANGE', '°'),
    ('wide_actual_focal_length', '广角实际焦距', 'RANGE', 'mm'),
    ('zoom_actual_focal_length', '变焦实际焦距', 'RANGE', 'mm'),
    ('mid_tele_actual_focal_length', '中长焦实际焦距', 'RANGE', 'mm'),
    ('tele_actual_focal_length', '长焦实际焦距', 'RANGE', 'mm'),
    ('thermal_actual_focal_length', '红外实际焦距', 'RANGE', 'mm'),
    ('zoom_sensor', '变焦传感器', 'TEXT', NULL),
    ('mid_tele_sensor', '中长焦传感器', 'TEXT', NULL),
    ('tele_sensor', '长焦传感器', 'TEXT', NULL),
    ('thermal_sensor', '红外传感器', 'TEXT', NULL),
    ('thermal_aperture', '红外光圈', 'TEXT', NULL),
    ('wide_photo_format', '广角照片格式', 'TEXT', NULL),
    ('zoom_photo_format', '变焦照片格式', 'TEXT', NULL),
    ('tele_photo_format', '长焦照片格式', 'TEXT', NULL),
    ('thermal_photo_format', '红外照片格式', 'TEXT', NULL),
    ('wide_video_resolution', '广角视频分辨率', 'TEXT', NULL),
    ('zoom_video_resolution', '变焦视频分辨率', 'TEXT', NULL),
    ('mid_tele_video_resolution', '中长焦视频分辨率', 'TEXT', NULL),
    ('tele_video_resolution', '长焦视频分辨率', 'TEXT', NULL),
    ('thermal_video_resolution', '红外视频分辨率', 'TEXT', NULL),
    ('wide_video_format', '广角视频格式', 'TEXT', NULL),
    ('zoom_video_format', '变焦视频格式', 'TEXT', NULL),
    ('thermal_video_format', '红外视频格式', 'TEXT', NULL),
    ('wide_focus_distance', '广角对焦距离', 'TEXT', NULL),
    ('zoom_focus_distance', '变焦对焦距离', 'TEXT', NULL),
    ('mid_tele_focus_distance', '中长焦对焦距离', 'TEXT', NULL),
    ('tele_focus_distance', '长焦对焦距离', 'TEXT', NULL),
    ('thermal_focus_distance', '红外对焦距离', 'TEXT', NULL),
    ('max_zoom_factor', '最大变焦倍数', 'TEXT', NULL),
    ('gimbal_yaw_min', '偏航最小角度', 'NUMBER', NULL),
    ('gimbal_yaw_max', '偏航最大角度', 'NUMBER', NULL),
    ('gimbal_angular_vibration', '角振动范围', 'TEXT', NULL),
    ('spec_storage_temperature', '存储温度', 'RANGE', '°C'),
    ('spec_ingress_protection', '防护等级', 'TEXT', NULL),
    ('thermal_measure_range', '测温范围', 'TEXT', NULL),
    ('thermal_digital_zoom', '红外数码变焦', 'TEXT', NULL),
    ('thermal_pixel_pitch', '红外像元间距', 'TEXT', NULL),
    ('laser_measure_range', '激光测距范围', 'TEXT', NULL);

INSERT INTO dynamic_field (
    code, name, type, unit, source, status, index_strategy,
    creator, updater, deleted, tenant_id, governance_status
)
SELECT code, name, type, unit, 'USER', 1, 'BTREE', 'seed', 'seed', false, 1, 'COMPANY'
FROM new_cam_field AS incoming
WHERE NOT EXISTS (
    SELECT 1 FROM dynamic_field AS existing
    WHERE existing.code = incoming.code
      AND existing.tenant_id = 1
      AND existing.deleted = false
);

CREATE TEMP TABLE cam_spec (
    model_code text NOT NULL,
    field_code text NOT NULL,
    default_value text NOT NULL,
    PRIMARY KEY (model_code, field_code)
) ON COMMIT DROP;

INSERT INTO cam_spec (model_code, field_code, default_value) VALUES
    ('dji-pl-h20', 'spec_weight_g', '678'),
    ('dji-pl-h20', 'spec_dimensions', '150×114×151 mm'),
    ('dji-pl-h20', 'spec_ingress_protection', 'IP44'),
    ('dji-pl-h20', 'spec_operating_temperature', '{"min":-20,"max":50}'),
    ('dji-pl-h20', 'spec_storage_temperature', '{"min":-20,"max":60}'),
    ('dji-pl-h20', 'gimbal_angular_vibration', '±0.01°'),
    ('dji-pl-h20', 'gimbal_yaw_min', '-320'),
    ('dji-pl-h20', 'gimbal_yaw_max', '320'),
    ('dji-pl-h20', 'zoom_sensor', '1/1.7 英寸 CMOS，2000 万像素'),
    ('dji-pl-h20', 'zoom_actual_focal_length', '{"min":6.83,"max":119.94}'),
    ('dji-pl-h20', 'zoom_fov', '{"min":4,"max":66.6}'),
    ('dji-pl-h20', 'zoom_focus_distance', '1 m 至无穷远（广角端），8 m 至无穷远（长焦端）'),
    ('dji-pl-h20', 'zoom_photo_format', 'JPEG'),
    ('dji-pl-h20', 'zoom_video_resolution', '3840×2160@30fps，1920×1080@30fps'),
    ('dji-pl-h20', 'zoom_video_format', 'MP4'),
    ('dji-pl-h20', 'wide_actual_focal_length', '{"min":4.5,"max":4.5}'),
    ('dji-pl-h20', 'wide_fov', '{"min":82.9,"max":82.9}'),
    ('dji-pl-h20', 'wide_focus_distance', '1 m 至无穷远'),
    ('dji-pl-h20', 'wide_photo_format', 'JPEG'),
    ('dji-pl-h20', 'wide_video_resolution', '1920×1080@30fps'),
    ('dji-pl-h20', 'wide_video_format', 'MP4'),
    ('dji-pl-h20', 'max_zoom_factor', '200×'),
    ('dji-pl-h20', 'laser_measure_range', '3-1200 m'),
    ('dji-pl-h20t', 'spec_weight_g', '828'),
    ('dji-pl-h20t', 'spec_dimensions', '167×135×161 mm'),
    ('dji-pl-h20t', 'spec_ingress_protection', 'IP44'),
    ('dji-pl-h20t', 'spec_operating_temperature', '{"min":-20,"max":50}'),
    ('dji-pl-h20t', 'spec_storage_temperature', '{"min":-20,"max":60}'),
    ('dji-pl-h20t', 'gimbal_angular_vibration', '±0.01°'),
    ('dji-pl-h20t', 'gimbal_yaw_min', '-320'),
    ('dji-pl-h20t', 'gimbal_yaw_max', '320'),
    ('dji-pl-h20t', 'zoom_sensor', '1/1.7 英寸 CMOS，2000 万像素'),
    ('dji-pl-h20t', 'zoom_actual_focal_length', '{"min":6.83,"max":119.94}'),
    ('dji-pl-h20t', 'zoom_fov', '{"min":4,"max":66.6}'),
    ('dji-pl-h20t', 'zoom_focus_distance', '1 m 至无穷远（广角端），8 m 至无穷远（长焦端）'),
    ('dji-pl-h20t', 'zoom_photo_format', 'JPEG'),
    ('dji-pl-h20t', 'zoom_video_resolution', '3840×2160@30fps，1920×1080@30fps'),
    ('dji-pl-h20t', 'zoom_video_format', 'MP4'),
    ('dji-pl-h20t', 'wide_actual_focal_length', '{"min":4.5,"max":4.5}'),
    ('dji-pl-h20t', 'wide_fov', '{"min":82.9,"max":82.9}'),
    ('dji-pl-h20t', 'wide_focus_distance', '1 m 至无穷远'),
    ('dji-pl-h20t', 'wide_photo_format', 'JPEG'),
    ('dji-pl-h20t', 'wide_video_resolution', '1920×1080@30fps'),
    ('dji-pl-h20t', 'wide_video_format', 'MP4'),
    ('dji-pl-h20t', 'max_zoom_factor', '200×'),
    ('dji-pl-h20t', 'laser_measure_range', '3-1200 m'),
    ('dji-pl-h20t', 'thermal_sensor', '非制冷氧化钒微测辐射热计'),
    ('dji-pl-h20t', 'thermal_actual_focal_length', '{"min":13.5,"max":13.5}'),
    ('dji-pl-h20t', 'thermal_fov', '{"min":40.6,"max":40.6}'),
    ('dji-pl-h20t', 'thermal_aperture', '{"min":1,"max":1,"text":"f/1.0"}'),
    ('dji-pl-h20t', 'thermal_focus_distance', '5 m 至无穷远'),
    ('dji-pl-h20t', 'thermal_photo_format', 'R-JPEG（16 bit）'),
    ('dji-pl-h20t', 'thermal_video_resolution', '640×512@30Hz'),
    ('dji-pl-h20t', 'thermal_video_format', 'MP4'),
    ('dji-pl-h20t', 'thermal_digital_zoom', '1×、2×、4×、8×'),
    ('dji-pl-h20t', 'thermal_pixel_pitch', '12 μm'),
    ('dji-pl-h20t', 'thermal_measure_range', '高增益 -40°C 至 150°C，低增益 -40°C 至 550°C'),
    ('dji-pl-h30', 'spec_weight_g', '920'),
    ('dji-pl-h30', 'spec_dimensions', '170×145×165 mm'),
    ('dji-pl-h30', 'spec_ingress_protection', 'IP54'),
    ('dji-pl-h30', 'spec_operating_temperature', '{"min":-20,"max":50}'),
    ('dji-pl-h30', 'spec_storage_temperature', '{"min":-20,"max":60}'),
    ('dji-pl-h30', 'gimbal_angular_vibration', '悬停 ±0.002°，飞行 ±0.004°'),
    ('dji-pl-h30', 'gimbal_yaw_min', '-320'),
    ('dji-pl-h30', 'gimbal_yaw_max', '320'),
    ('dji-pl-h30', 'zoom_sensor', '1/1.8 英寸 CMOS，有效像素 4000 万'),
    ('dji-pl-h30', 'zoom_actual_focal_length', '{"min":7.1,"max":172}'),
    ('dji-pl-h30', 'zoom_fov', '{"min":2.9,"max":66.7}'),
    ('dji-pl-h30', 'zoom_photo_format', 'JPG'),
    ('dji-pl-h30', 'zoom_video_resolution', '3840×2160@30fps，1920×1080@30fps'),
    ('dji-pl-h30', 'zoom_video_format', 'MP4'),
    ('dji-pl-h30', 'wide_actual_focal_length', '{"min":6.72,"max":6.72}'),
    ('dji-pl-h30', 'wide_fov', '{"min":82.1,"max":82.1}'),
    ('dji-pl-h30', 'wide_photo_format', 'JPG'),
    ('dji-pl-h30', 'wide_video_resolution', '3840×2160@30fps，1920×1080@30fps'),
    ('dji-pl-h30', 'wide_video_format', 'MP4'),
    ('dji-pl-h30', 'max_zoom_factor', '400×'),
    ('dji-pl-h30', 'laser_measure_range', '3-3000 m'),
    ('dji-pl-h30t', 'spec_weight_g', '920'),
    ('dji-pl-h30t', 'spec_dimensions', '170×145×165 mm'),
    ('dji-pl-h30t', 'spec_ingress_protection', 'IP54'),
    ('dji-pl-h30t', 'spec_operating_temperature', '{"min":-20,"max":50}'),
    ('dji-pl-h30t', 'spec_storage_temperature', '{"min":-20,"max":60}'),
    ('dji-pl-h30t', 'gimbal_angular_vibration', '悬停 ±0.002°，飞行 ±0.004°'),
    ('dji-pl-h30t', 'gimbal_yaw_min', '-320'),
    ('dji-pl-h30t', 'gimbal_yaw_max', '320'),
    ('dji-pl-h30t', 'zoom_sensor', '1/1.8 英寸 CMOS，有效像素 4000 万'),
    ('dji-pl-h30t', 'zoom_actual_focal_length', '{"min":7.1,"max":172}'),
    ('dji-pl-h30t', 'zoom_fov', '{"min":2.9,"max":66.7}'),
    ('dji-pl-h30t', 'zoom_photo_format', 'JPG'),
    ('dji-pl-h30t', 'zoom_video_resolution', '3840×2160@30fps，1920×1080@30fps'),
    ('dji-pl-h30t', 'zoom_video_format', 'MP4'),
    ('dji-pl-h30t', 'wide_actual_focal_length', '{"min":6.72,"max":6.72}'),
    ('dji-pl-h30t', 'wide_fov', '{"min":82.1,"max":82.1}'),
    ('dji-pl-h30t', 'wide_photo_format', 'JPG'),
    ('dji-pl-h30t', 'wide_video_resolution', '3840×2160@30fps，1920×1080@30fps'),
    ('dji-pl-h30t', 'wide_video_format', 'MP4'),
    ('dji-pl-h30t', 'max_zoom_factor', '400×'),
    ('dji-pl-h30t', 'laser_measure_range', '3-3000 m'),
    ('dji-pl-h30t', 'thermal_sensor', '非制冷氧化钒微测辐射热计'),
    ('dji-pl-h30t', 'thermal_actual_focal_length', '{"min":24,"max":24}'),
    ('dji-pl-h30t', 'thermal_fov', '{"min":45.2,"max":45.2}'),
    ('dji-pl-h30t', 'thermal_aperture', '{"min":0.95,"max":0.95,"text":"f/0.95"}'),
    ('dji-pl-h30t', 'thermal_photo_format', 'R-JPEG'),
    ('dji-pl-h30t', 'thermal_video_resolution', '1280×1024@30fps'),
    ('dji-pl-h30t', 'thermal_video_format', 'MP4'),
    ('dji-pl-h30t', 'thermal_digital_zoom', '32×'),
    ('dji-pl-h30t', 'thermal_pixel_pitch', '12 μm'),
    ('dji-pl-h30t', 'thermal_measure_range', '高增益 -20°C 至 150°C，低增益 0°C 至 600°C'),
    ('dji-pl-m30-cam', 'gimbal_angular_vibration', '±0.01°'),
    ('dji-pl-m30-cam', 'gimbal_yaw_min', '-90'),
    ('dji-pl-m30-cam', 'gimbal_yaw_max', '90'),
    ('dji-pl-m30-cam', 'zoom_sensor', '1/2 英寸 CMOS，有效像素 4800 万'),
    ('dji-pl-m30-cam', 'zoom_actual_focal_length', '{"min":21,"max":75}'),
    ('dji-pl-m30-cam', 'zoom_focus_distance', '5 m 至无穷远'),
    ('dji-pl-m30-cam', 'zoom_video_resolution', '3840×2160'),
    ('dji-pl-m30-cam', 'wide_actual_focal_length', '{"min":4.5,"max":4.5}'),
    ('dji-pl-m30-cam', 'wide_fov', '{"min":84,"max":84}'),
    ('dji-pl-m30-cam', 'wide_focus_distance', '1 m 至无穷远'),
    ('dji-pl-m30-cam', 'wide_video_resolution', '3840×2160'),
    ('dji-pl-m30t-cam', 'gimbal_angular_vibration', '±0.01°'),
    ('dji-pl-m30t-cam', 'gimbal_yaw_min', '-90'),
    ('dji-pl-m30t-cam', 'gimbal_yaw_max', '90'),
    ('dji-pl-m30t-cam', 'zoom_sensor', '1/2 英寸 CMOS，有效像素 4800 万'),
    ('dji-pl-m30t-cam', 'zoom_actual_focal_length', '{"min":21,"max":75}'),
    ('dji-pl-m30t-cam', 'zoom_focus_distance', '5 m 至无穷远'),
    ('dji-pl-m30t-cam', 'zoom_video_resolution', '3840×2160'),
    ('dji-pl-m30t-cam', 'wide_actual_focal_length', '{"min":4.5,"max":4.5}'),
    ('dji-pl-m30t-cam', 'wide_fov', '{"min":84,"max":84}'),
    ('dji-pl-m30t-cam', 'wide_focus_distance', '1 m 至无穷远'),
    ('dji-pl-m30t-cam', 'wide_video_resolution', '3840×2160'),
    ('dji-pl-m30t-cam', 'thermal_sensor', '非制冷氧化钒微测辐射热计'),
    ('dji-pl-m30t-cam', 'thermal_actual_focal_length', '{"min":9.1,"max":9.1}'),
    ('dji-pl-m30t-cam', 'thermal_fov', '{"min":61,"max":61}'),
    ('dji-pl-m30t-cam', 'thermal_aperture', '{"min":1,"max":1,"text":"f/1.0"}'),
    ('dji-pl-m30t-cam', 'thermal_focus_distance', '5 m 至无穷远'),
    ('dji-pl-m30t-cam', 'thermal_video_resolution', '普通 640×512，超分辨率 1280×1024'),
    ('dji-pl-m30t-cam', 'thermal_pixel_pitch', '12 μm'),
    ('dji-pl-m30t-cam', 'thermal_measure_range', '高增益 -20°C 至 150°C，低增益 0°C 至 500°C'),
    ('dji-pl-m30t-cam', 'laser_measure_range', '3-1200 m'),
    ('dji-pl-m3e-cam', 'gimbal_angular_vibration', '±0.007°'),
    ('dji-pl-m3e-cam', 'wide_sensor', '4/3 英寸 CMOS，有效像素 2000 万'),
    ('dji-pl-m3e-cam', 'wide_fov', '{"min":84,"max":84}'),
    ('dji-pl-m3e-cam', 'wide_focus_distance', '1 m 至无穷远'),
    ('dji-pl-m3e-cam', 'wide_photo_format', 'JPEG/DNG（RAW）'),
    ('dji-pl-m3e-cam', 'wide_video_resolution', '3840×2160@30fps，1920×1080@30fps'),
    ('dji-pl-m3e-cam', 'wide_video_format', 'MP4'),
    ('dji-pl-m3e-cam', 'tele_sensor', '1/2 英寸 CMOS，有效像素 1200 万'),
    ('dji-pl-m3e-cam', 'tele_fov', '{"min":15,"max":15}'),
    ('dji-pl-m3e-cam', 'tele_focus_distance', '3 m 至无穷远'),
    ('dji-pl-m3e-cam', 'tele_photo_format', 'JPEG'),
    ('dji-pl-m3e-cam', 'tele_video_resolution', '3840×2160@30fps，1920×1080@30fps'),
    ('dji-pl-m3e-cam', 'max_zoom_factor', '8×（混合 56×）'),
    ('dji-pl-m3t-cam', 'gimbal_angular_vibration', '±0.007°'),
    ('dji-pl-m3t-cam', 'wide_sensor', '1/2 英寸 CMOS，有效像素 4800 万'),
    ('dji-pl-m3t-cam', 'wide_fov', '{"min":84,"max":84}'),
    ('dji-pl-m3t-cam', 'wide_focus_distance', '1 m 至无穷远'),
    ('dji-pl-m3t-cam', 'wide_photo_format', 'JPEG'),
    ('dji-pl-m3t-cam', 'wide_video_resolution', '3840×2160@30fps，1920×1080@30fps'),
    ('dji-pl-m3t-cam', 'wide_video_format', 'MP4'),
    ('dji-pl-m3t-cam', 'tele_sensor', '1/2 英寸 CMOS，有效像素 1200 万'),
    ('dji-pl-m3t-cam', 'tele_fov', '{"min":15,"max":15}'),
    ('dji-pl-m3t-cam', 'tele_focus_distance', '3 m 至无穷远'),
    ('dji-pl-m3t-cam', 'tele_photo_format', 'JPEG'),
    ('dji-pl-m3t-cam', 'tele_video_resolution', '3840×2160@30fps，1920×1080@30fps'),
    ('dji-pl-m3t-cam', 'max_zoom_factor', '8×（混合 56×）'),
    ('dji-pl-m3t-cam', 'thermal_sensor', '非制冷氧化钒微测辐射热计'),
    ('dji-pl-m3t-cam', 'thermal_fov', '{"min":61,"max":61}'),
    ('dji-pl-m3t-cam', 'thermal_aperture', '{"min":1,"max":1,"text":"f/1.0"}'),
    ('dji-pl-m3t-cam', 'thermal_focus_distance', '5 m 至无穷远'),
    ('dji-pl-m3t-cam', 'thermal_photo_format', 'JPEG（8 bit）、R-JPEG（16 bit）'),
    ('dji-pl-m3t-cam', 'thermal_video_resolution', '640×512@30fps'),
    ('dji-pl-m3t-cam', 'thermal_video_format', 'MP4'),
    ('dji-pl-m3t-cam', 'thermal_digital_zoom', '28×'),
    ('dji-pl-m3t-cam', 'thermal_pixel_pitch', '12 μm'),
    ('dji-pl-m3t-cam', 'thermal_measure_range', '高增益 -20°C 至 150°C，低增益 0°C 至 500°C'),
    ('dji-pl-m3ta-cam', 'gimbal_angular_vibration', '±0.007°'),
    ('dji-pl-m3ta-cam', 'wide_sensor', '1/2 英寸 CMOS，有效像素 4800 万'),
    ('dji-pl-m3ta-cam', 'wide_fov', '{"min":84,"max":84}'),
    ('dji-pl-m3ta-cam', 'wide_focus_distance', '1 m 至无穷远'),
    ('dji-pl-m3ta-cam', 'wide_photo_format', 'JPEG'),
    ('dji-pl-m3ta-cam', 'wide_video_resolution', '3840×2160@30fps，1920×1080@30fps'),
    ('dji-pl-m3ta-cam', 'wide_video_format', 'MP4'),
    ('dji-pl-m3ta-cam', 'tele_sensor', '1/2 英寸 CMOS，有效像素 1200 万'),
    ('dji-pl-m3ta-cam', 'tele_fov', '{"min":15,"max":15}'),
    ('dji-pl-m3ta-cam', 'tele_focus_distance', '3 m 至无穷远'),
    ('dji-pl-m3ta-cam', 'tele_photo_format', 'JPEG'),
    ('dji-pl-m3ta-cam', 'tele_video_resolution', '3840×2160@30fps，1920×1080@30fps'),
    ('dji-pl-m3ta-cam', 'max_zoom_factor', '8×（混合 56×）'),
    ('dji-pl-m3ta-cam', 'thermal_sensor', '非制冷氧化钒微测辐射热计'),
    ('dji-pl-m3ta-cam', 'thermal_fov', '{"min":41.2,"max":41.2}'),
    ('dji-pl-m3ta-cam', 'thermal_aperture', '{"min":1,"max":1,"text":"f/1.0"}'),
    ('dji-pl-m3ta-cam', 'thermal_focus_distance', '5 m 至无穷远'),
    ('dji-pl-m3ta-cam', 'thermal_photo_format', 'JPEG（8 bit）、R-JPEG（16 bit）'),
    ('dji-pl-m3ta-cam', 'thermal_video_resolution', '640×512@30fps'),
    ('dji-pl-m3ta-cam', 'thermal_video_format', 'MP4'),
    ('dji-pl-m3ta-cam', 'thermal_digital_zoom', '28×'),
    ('dji-pl-m3ta-cam', 'thermal_pixel_pitch', '8 μm'),
    ('dji-pl-m3ta-cam', 'thermal_measure_range', '高增益 -20°C 至 150°C，低增益 0°C 至 500°C'),
    ('dji-pl-m4e-cam', 'gimbal_pitch_max', '35'),
    ('dji-pl-m4e-cam', 'gimbal_angular_vibration', '±0.007°'),
    ('dji-pl-m4e-cam', 'spec_operating_temperature', '{"min":-10,"max":40}'),
    ('dji-pl-m4e-cam', 'wide_sensor', '4/3 英寸 CMOS，有效像素 2000 万'),
    ('dji-pl-m4e-cam', 'wide_fov', '{"min":84,"max":84}'),
    ('dji-pl-m4e-cam', 'wide_focus_distance', '1 m 至无穷远'),
    ('dji-pl-m4e-cam', 'wide_photo_size', '5280×3956'),
    ('dji-pl-m4e-cam', 'wide_photo_format', 'JPEG/DNG（RAW）'),
    ('dji-pl-m4e-cam', 'wide_video_resolution', '3840×2160@30fps，1920×1080@30fps'),
    ('dji-pl-m4e-cam', 'wide_video_format', 'MP4'),
    ('dji-pl-m4e-cam', 'mid_tele_sensor', '1/1.3 英寸 CMOS，有效像素 4800 万'),
    ('dji-pl-m4e-cam', 'mid_tele_fov', '{"min":35,"max":35}'),
    ('dji-pl-m4e-cam', 'mid_tele_focus_distance', '3 m 至无穷远'),
    ('dji-pl-m4e-cam', 'mid_tele_photo_size', '8064×6048'),
    ('dji-pl-m4e-cam', 'tele_sensor', '1/1.5 英寸 CMOS，有效像素 4800 万'),
    ('dji-pl-m4e-cam', 'tele_fov', '{"min":15,"max":15}'),
    ('dji-pl-m4e-cam', 'tele_focus_distance', '3 m 至无穷远'),
    ('dji-pl-m4e-cam', 'tele_photo_size', '8192×6144'),
    ('dji-pl-m4e-cam', 'tele_photo_format', 'JPEG'),
    ('dji-pl-m4e-cam', 'tele_video_resolution', '3840×2160@30fps，1920×1080@30fps'),
    ('dji-pl-m4e-cam', 'max_zoom_factor', '16×（混合 112×）'),
    ('dji-pl-m4t-cam', 'gimbal_angular_vibration', '±0.007°'),
    ('dji-pl-m4t-cam', 'spec_operating_temperature', '{"min":-10,"max":40}'),
    ('dji-pl-m4t-cam', 'spec_ingress_protection', '无标准防护等级'),
    ('dji-pl-m4t-cam', 'wide_sensor', '1/1.3 英寸 CMOS，有效像素 4800 万'),
    ('dji-pl-m4t-cam', 'wide_fov', '{"min":82,"max":82}'),
    ('dji-pl-m4t-cam', 'wide_focus_distance', '1 m 至无穷远'),
    ('dji-pl-m4t-cam', 'wide_photo_size', '8064×6048'),
    ('dji-pl-m4t-cam', 'wide_photo_format', 'JPEG'),
    ('dji-pl-m4t-cam', 'wide_video_resolution', '3840×2160@30fps，1920×1080@30fps'),
    ('dji-pl-m4t-cam', 'wide_video_format', 'MP4'),
    ('dji-pl-m4t-cam', 'mid_tele_sensor', '1/1.3 英寸 CMOS，有效像素 4800 万'),
    ('dji-pl-m4t-cam', 'mid_tele_fov', '{"min":35,"max":35}'),
    ('dji-pl-m4t-cam', 'mid_tele_focus_distance', '3 m 至无穷远'),
    ('dji-pl-m4t-cam', 'mid_tele_photo_size', '8064×6048'),
    ('dji-pl-m4t-cam', 'tele_sensor', '1/1.5 英寸 CMOS，有效像素 4800 万'),
    ('dji-pl-m4t-cam', 'tele_fov', '{"min":15,"max":15}'),
    ('dji-pl-m4t-cam', 'tele_focus_distance', '3 m 至无穷远'),
    ('dji-pl-m4t-cam', 'tele_photo_size', '8192×6144'),
    ('dji-pl-m4t-cam', 'tele_photo_format', 'JPEG'),
    ('dji-pl-m4t-cam', 'tele_video_resolution', '3840×2160@30fps，1920×1080@30fps'),
    ('dji-pl-m4t-cam', 'max_zoom_factor', '16×（混合 112×）'),
    ('dji-pl-m4t-cam', 'thermal_sensor', '非制冷氧化钒'),
    ('dji-pl-m4t-cam', 'thermal_fov', '{"min":45,"max":45}'),
    ('dji-pl-m4t-cam', 'thermal_aperture', '{"min":1,"max":1,"text":"f/1.0"}'),
    ('dji-pl-m4t-cam', 'thermal_focus_distance', '5 m 至无穷远'),
    ('dji-pl-m4t-cam', 'thermal_photo_format', 'JPEG（8 bit）、R-JPEG（16 bit）'),
    ('dji-pl-m4t-cam', 'thermal_video_resolution', '超分辨率 1280×1024@30fps，普通 640×512@30fps'),
    ('dji-pl-m4t-cam', 'thermal_video_format', 'MP4'),
    ('dji-pl-m4t-cam', 'thermal_digital_zoom', '28×'),
    ('dji-pl-m4t-cam', 'thermal_pixel_pitch', '12 μm'),
    ('dji-pl-m4t-cam', 'thermal_measure_range', '高增益 -20°C 至 150°C，低增益 0°C 至 550°C'),
    ('dji-pl-m4t-cam', 'laser_measure_range', '1800 m（1 Hz，20% 反射率）'),
    ('dji-pl-h20n', 'zoom_sensor', '1/1.8 英寸 CMOS，有效像素 400 万'),
    ('dji-pl-h20n', 'zoom_actual_focal_length', '{"min":6.8,"max":119.9}'),
    ('dji-pl-h20n', 'wide_sensor', '1/2.7 英寸 CMOS，有效像素 200 万'),
    ('dji-pl-h20n', 'wide_actual_focal_length', '{"min":4.5,"max":4.5}'),
    ('dji-pl-h20n', 'thermal_actual_focal_length', '{"min":12,"max":12}'),
    ('dji-pl-z30', 'spec_weight_g', '556'),
    ('dji-pl-z30', 'spec_dimensions', '152×137×61 mm'),
    ('dji-pl-z30', 'zoom_actual_focal_length', '{"min":4.3,"max":129}'),
    ('dji-pl-z30', 'zoom_fov', '{"min":2.3,"max":63.7}');

INSERT INTO dynamic_model_field_assignment (
    model_id, field_id, required, is_searchable, is_filterable, is_sortable,
    default_value, sort, creator, updater, deleted, tenant_id, model_code, field_code
)
SELECT model.id, field.id, false, false, false, false,
       spec.default_value, 120, 'seed', 'seed', false, 1, model.code, field.code
FROM cam_spec AS spec
JOIN dynamic_model AS model ON model.code = spec.model_code AND model.deleted = false
JOIN dynamic_field AS field ON field.code = spec.field_code AND field.tenant_id = 1 AND field.deleted = false
WHERE NOT EXISTS (
    SELECT 1 FROM dynamic_model_field_assignment AS existing
    WHERE existing.model_id = model.id
      AND existing.field_id = field.id
      AND existing.deleted = false
);

UPDATE dynamic_model_field_assignment AS assignment
SET default_value = spec.default_value,
    update_time = CURRENT_TIMESTAMP
FROM cam_spec AS spec
JOIN dynamic_model AS model ON model.code = spec.model_code AND model.deleted = false
WHERE assignment.model_id = model.id
  AND assignment.field_code = spec.field_code
  AND assignment.deleted = false
  AND (
      assignment.default_value IS NULL
      OR btrim(assignment.default_value) = ''
      OR (spec.model_code = 'dji-pl-m4e-cam' AND spec.field_code = 'gimbal_pitch_max')
  );

UPDATE dynamic_model_field_assignment AS assignment
SET default_value = '',
    update_time = CURRENT_TIMESTAMP
FROM dynamic_model AS model
WHERE model.id = assignment.model_id
  AND model.code = 'dji-pl-z30'
  AND assignment.field_code = 'zoom_focal_length'
  AND assignment.deleted = false
  AND assignment.default_value LIKE '%4.3%';

INSERT INTO dynamic_model_field_assignment (
    model_id, field_id, required, is_searchable, is_filterable, is_sortable,
    default_value, sort, creator, updater, deleted, tenant_id, model_code, field_code
)
SELECT model.id, field.id, false, false, false, false,
       '大疆', 10, 'seed', 'seed', false, 1, model.code, field.code
FROM dynamic_model AS model
JOIN dynamic_field AS field
  ON field.code IN ('brand', 'manufacturer')
 AND field.tenant_id = 1
 AND field.deleted = false
WHERE model.deleted = false
  AND (model.code LIKE 'dji-pl-%' OR model.code = 'dji-m400-body-camera')
  AND NOT EXISTS (
      SELECT 1 FROM dynamic_model_field_assignment AS existing
      WHERE existing.model_id = model.id
        AND existing.field_id = field.id
        AND existing.deleted = false
  );

UPDATE dynamic_model_field_assignment AS assignment
SET default_value = '大疆',
    update_time = CURRENT_TIMESTAMP
FROM dynamic_model AS model
WHERE assignment.model_id = model.id
  AND assignment.deleted = false
  AND assignment.field_code IN ('brand', 'manufacturer')
  AND (model.code LIKE 'dji-pl-%' OR model.code = 'dji-m400-body-camera')
  AND (assignment.default_value IS NULL OR btrim(assignment.default_value) = '');

UPDATE ent_equipment_t1 AS equipment
SET brand = '大疆',
    manufacturer = '大疆',
    custom_fields = COALESCE(equipment.custom_fields, '{}'::jsonb) || filled.fields,
    update_time = CURRENT_TIMESTAMP
FROM (
    SELECT assignment.model_id,
           jsonb_object_agg(
               assignment.field_code,
               CASE
                   WHEN btrim(assignment.default_value) ~ '^-?\d+(?:\.\d+)?$'
                       THEN to_jsonb(btrim(assignment.default_value)::numeric)
                   WHEN left(btrim(assignment.default_value), 1) = '{'
                       THEN assignment.default_value::jsonb
                   ELSE to_jsonb(btrim(assignment.default_value))
               END
           ) AS fields
    FROM dynamic_model_field_assignment AS assignment
    WHERE assignment.deleted = false
      AND COALESCE(btrim(assignment.default_value), '') <> ''
      AND assignment.field_code IN (
          'wide_sensor', 'wide_focal_length', 'wide_actual_focal_length', 'wide_fov', 'wide_aperture',
          'wide_photo_size', 'wide_photo_format', 'wide_video_resolution', 'wide_video_format', 'wide_focus_distance',
          'zoom_sensor', 'zoom_focal_length', 'zoom_actual_focal_length', 'zoom_fov', 'zoom_aperture',
          'zoom_photo_size', 'zoom_photo_format', 'zoom_video_resolution', 'zoom_video_format', 'zoom_focus_distance',
          'mid_tele_sensor', 'mid_tele_focal_length', 'mid_tele_fov', 'mid_tele_aperture', 'mid_tele_photo_size', 'mid_tele_focus_distance',
          'tele_sensor', 'tele_focal_length', 'tele_fov', 'tele_aperture', 'tele_photo_size', 'tele_photo_format',
          'tele_video_resolution', 'tele_focus_distance',
          'thermal_sensor', 'thermal_focal_length', 'thermal_actual_focal_length', 'thermal_fov', 'thermal_aperture',
          'thermal_resolution', 'thermal_photo_format', 'thermal_video_resolution', 'thermal_video_format',
          'thermal_focus_distance', 'thermal_digital_zoom', 'thermal_pixel_pitch', 'thermal_measure_range',
          'iso_normal', 'shutter_speed', 'gimbal_pitch_min', 'gimbal_pitch_max', 'gimbal_yaw_min', 'gimbal_yaw_max',
          'gimbal_angular_vibration', 'max_zoom_factor', 'laser_measure_range',
          'spec_weight_g', 'spec_dimensions', 'spec_operating_temperature', 'spec_storage_temperature', 'spec_ingress_protection',
          'fpv_resolution', 'fpv_night_vision'
      )
    GROUP BY assignment.model_id
) AS filled
WHERE equipment.model_id = filled.model_id
  AND equipment.deleted = false
  AND equipment.code LIKE 'equipment-cam-spec-%';

COMMIT;
