-- 型号名称上直接写出用途和镜头组合，打开型号列表就能分辨。
-- 负责：dynamic_model.name。
-- 不负责：设备台账上每台设备自己的名字；型号编码。

SET search_path TO dynamicbusiness;

BEGIN;

UPDATE dynamic_model AS m
SET name = v.name,
    update_time = CURRENT_TIMESTAMP
FROM (VALUES
  ('dji-m30', 'Matrice 30（广角 + 变焦）'),
  ('dji-m30t', 'Matrice 30T（广角 + 变焦 + 红外）'),
  ('dji-m300-rtk', 'Matrice 300 RTK（可换吊舱）'),
  ('dji-m350-rtk', 'Matrice 350 RTK（可换吊舱）'),
  ('dji-m400', 'Matrice 400（可换吊舱）'),
  ('dji-m3e', 'Mavic 3 行业系列（M3E 测绘）'),
  ('dji-m3t', 'Mavic 3 行业系列（M3T 安防）'),
  ('dji-m3ta', 'Mavic 3 行业系列（M3TA 安防）'),
  ('dji-m3d', 'Matrice 3D（测绘）'),
  ('dji-m3td', 'Matrice 3TD（安防）'),
  ('dji-m4e', 'DJI Matrice 4 系列（M4E 测绘）'),
  ('dji-m4t', 'DJI Matrice 4 系列（M4T 安防）'),
  ('dji-m4d', 'Matrice 4D（测绘）'),
  ('dji-m4td', 'Matrice 4TD（安防）'),
  ('dji-pl-m30-cam', 'M30 双光相机（广角 + 变焦）'),
  ('dji-pl-m30t-cam', 'M30T 三光相机（广角 + 变焦 + 红外）'),
  ('dji-pl-m3e-cam', 'Mavic 3E 测绘相机（广角 + 变焦）'),
  ('dji-pl-m3t-cam', 'Mavic 3T 安防相机（广角 + 变焦 + 红外）'),
  ('dji-pl-m3ta-cam', 'Mavic 3TA 安防相机（广角 + 变焦 + 红外）'),
  ('dji-pl-m3d-cam', 'Matrice 3D 测绘相机（广角 + 变焦）'),
  ('dji-pl-m3td-cam', 'Matrice 3TD 安防相机（广角 + 变焦 + 红外）'),
  ('dji-pl-m4e-cam', 'Matrice 4E 测绘相机（广角 + 中长焦 + 长焦）'),
  ('dji-pl-m4t-cam', 'Matrice 4T 安防相机（广角 + 中长焦 + 长焦 + 红外）'),
  ('dji-pl-m4d-cam', 'Matrice 4D 测绘相机（广角 + 中长焦 + 长焦）'),
  ('dji-pl-m4td-cam', 'Matrice 4TD 安防相机（广角 + 中长焦 + 长焦 + 红外）'),
  ('dji-pl-h20', '禅思 H20（广角 + 变焦）'),
  ('dji-pl-h20t', '禅思 H20T（广角 + 变焦 + 红外）'),
  ('dji-pl-h20n', '禅思 H20N（夜视）'),
  ('dji-pl-h30', '禅思 H30（广角 + 变焦）'),
  ('dji-pl-h30t', '禅思 H30T（广角 + 变焦 + 红外）'),
  ('dji-pl-z30', '禅思 Z30（变焦）'),
  ('dji-pl-xt2', '禅思 XT2（含红外）'),
  ('dji-pl-xts', '禅思 XTS（红外）'),
  ('dji-pl-fpv', 'FPV（机头观察）'),
  ('dji-pl-assist', '辅助影像（机场飞机共用）'),
  ('dji-pl-dock-cam', '机场相机（舱内 / 舱外）')
) AS v(code, name)
WHERE m.deleted = false
  AND m.code = v.code;

COMMIT;
