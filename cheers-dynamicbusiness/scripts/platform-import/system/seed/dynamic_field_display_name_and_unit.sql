-- 设备检查配置上的参数名用中文，数字带上单位字典里的中文单位。
-- 不改字段编码。已经填在设备上的数仍在原格子里。
-- 定点拍照是动作库里这条动作给人看的名称，编码仍是 orientedShoot。

SET client_encoding TO 'UTF8';
SET search_path TO dynamicbusiness, public;

INSERT INTO dynamic_unit (name, code, unit_type, sort, status, creator, tenant_id, deleted)
SELECT v.name, v.code, v.unit_type, v.sort, 1, 'seed', t.tenant_id, false
FROM (VALUES
  ('像素', 'px', 'LENGTH', 12),
  ('米/秒', 'm/s', 'SPEED', 70)
) AS v(name, code, unit_type, sort)
CROSS JOIN (VALUES (1), (2)) AS t(tenant_id)
WHERE NOT EXISTS (
  SELECT 1 FROM dynamic_unit u
  WHERE u.deleted = false AND u.tenant_id = t.tenant_id AND u.code = v.code
);

UPDATE dynamic_field AS f SET
  name = v.name,
  unit = v.unit,
  description = COALESCE(v.description, f.description),
  options = COALESCE(v.options, f.options),
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
FROM (VALUES
  ('gimbalPitchRotateAngle', '云台俯仰角', 'deg', NULL, NULL),
  ('gimbalYawRotateAngle', '云台偏航角', 'deg', NULL, NULL),
  ('gimbalRollRotateAngle', '云台横滚角', 'deg', NULL, NULL),
  ('gimbalPitchRotateEnable', '是否转动云台俯仰', NULL, '不转动或转动云台俯仰。', '[{"label":"不转动","value":"0"},{"label":"转动","value":"1"}]'),
  ('gimbalRollRotateEnable', '是否转动云台横滚', NULL, '不转动或转动云台横滚。', '[{"label":"不转动","value":"0"},{"label":"转动","value":"1"}]'),
  ('gimbalYawRotateEnable', '是否转动云台偏航', NULL, '不转动或转动云台偏航。', '[{"label":"不转动","value":"0"},{"label":"转动","value":"1"}]'),
  ('gimbalRotateTimeEnable', '是否设置云台转动用时', NULL, '不设置或设置云台完成转动的用时。', '[{"label":"不设置","value":"0"},{"label":"设置","value":"1"}]'),
  ('security_takeoff_height', '安全起飞高度', 'm', NULL, NULL),
  ('rth_altitude', '返航高度', 'm', NULL, NULL),
  ('max_speed', '最大速度', 'm/s', NULL, NULL),
  ('gas_frequency', '气体上报频率', 's', NULL, NULL),
  ('searchlightBrightness', '亮度', '%', NULL, NULL),
  ('psdk_index', '喊话器负载序号', NULL, NULL, NULL),
  ('fly_to_id', '飞向目标点编号', NULL, '已选位置的点位编号。选了位置后写入，用来对上这次飞行的回执。不用另行填写。', NULL),
  ('video_id', '直播视频流编号', NULL, NULL, NULL),
  ('AFPos', '自动对焦电机位置', NULL, '自动对焦电机位置。', NULL),
  ('orientedFileMD5', '照片文件校验值', NULL, '照片文件校验值。', NULL),
  ('orientedCameraISO', '感光度', NULL, '感光度。', NULL)
) AS v(code, name, unit, description, options)
WHERE f.deleted = false
  AND f.tenant_id = 1
  AND f.code = v.code;

UPDATE ent_action_t1
SET name = '定点拍照',
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND tenant_id = 1
  AND code = 'orientedShoot';
