-- 按常用配置把相机挂到对应的大疆飞机和机场上。
-- 负责：子设备的上级写成那一架飞机或机场。挂载点编号写在主机的挂载点字段里。
-- 不负责：右云台、上方云台不预挂。可换的禅思相机每种各一架飞机，见 dynamic_equipment_dji_extra_airframes.sql。
-- 同类型设备的从属用 parent_id。摄像机型号上不再有负载位置。

SET search_path TO dynamicbusiness;

BEGIN;

UPDATE dynamic_field
SET deleted = true, status = 0, updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE code = 'dji_gimbal_index' AND deleted = false;

UPDATE dynamic_model_field_assignment
SET deleted = true, updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE field_code = 'dji_gimbal_index' AND deleted = false;

INSERT INTO dynamic_field (
  code, name, type, description, source, status, index_strategy, options, tenant_id, creator
) VALUES (
  'dji_camera_position',
  '机场相机位置',
  'ENUM',
  '官方 camera_position。0 舱内，1 舱外。只用于机场相机。大疆机场只有舱外。发出去的相机的负载索引编号仍是 165-0-7，不把舱内舱外写进这一段。',
  'USER',
  1,
  'NONE',
  '[
    {"label":"0 舱内","value":"0"},
    {"label":"1 舱外","value":"1"}
  ]'::jsonb,
  1,
  'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  status = 1,
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, model_code, field_id, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  tenant_id, creator
)
SELECT m.id, m.code, f.id, f.code, false, false, true, false, 44, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.code = 'dji_camera_position' AND f.tenant_id = 1 AND f.deleted = false
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'dji-pl-dock-cam'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

CREATE TEMP TABLE dji_mount (
  host_code text NOT NULL,
  payload_code text NOT NULL,
  child_code text PRIMARY KEY,
  child_name text NOT NULL,
  gimbal_index text NOT NULL,
  camera_position text
) ON COMMIT DROP;

INSERT INTO dji_mount (host_code, payload_code, child_code, child_name, gimbal_index, camera_position) VALUES
  ('dji-m300-rtk', 'dji-pl-fpv', 'EQ-DJI-M300-RTK-FPV', 'Matrice 300 RTK · FPV', '7', NULL),
  ('dji-m300-rtk', 'dji-pl-h20t', 'EQ-DJI-M300-RTK-H20T', 'Matrice 300 RTK · 禅思 H20T', '0', NULL),
  ('dji-m350-rtk', 'dji-pl-fpv', 'EQ-DJI-M350-RTK-FPV', 'Matrice 350 RTK · FPV', '7', NULL),
  ('dji-m350-rtk', 'dji-pl-h30t', 'EQ-DJI-M350-RTK-H30T', 'Matrice 350 RTK · 禅思 H30T', '0', NULL),
  ('dji-m400', 'dji-pl-fpv', 'EQ-DJI-M400-FPV', 'Matrice 400 · FPV', '7', NULL),
  ('dji-m400', 'dji-pl-h30t', 'EQ-DJI-M400-H30T', 'Matrice 400 · 禅思 H30T', '0', NULL),
  ('dji-m30', 'dji-pl-fpv', 'EQ-DJI-M30-FPV', 'Matrice 30 · FPV', '7', NULL),
  ('dji-m30', 'dji-pl-m30-cam', 'EQ-DJI-M30-CAM', 'Matrice 30 · 相机', '0', NULL),
  ('dji-m30t', 'dji-pl-fpv', 'EQ-DJI-M30T-FPV', 'Matrice 30T · FPV', '7', NULL),
  ('dji-m30t', 'dji-pl-m30t-cam', 'EQ-DJI-M30T-CAM', 'Matrice 30T · 相机', '0', NULL),
  ('dji-m3e', 'dji-pl-m3e-cam', 'EQ-DJI-M3E-CAM', 'Mavic 3E · 相机', '0', NULL),
  ('dji-m3t', 'dji-pl-m3t-cam', 'EQ-DJI-M3T-CAM', 'Mavic 3T · 相机', '0', NULL),
  ('dji-m3ta', 'dji-pl-m3ta-cam', 'EQ-DJI-M3TA-CAM', 'Mavic 3TA · 相机', '0', NULL),
  ('dji-m3d', 'dji-pl-m3d-cam', 'EQ-DJI-M3D-CAM', 'Matrice 3D · 相机', '0', NULL),
  ('dji-m3d', 'dji-pl-assist', 'EQ-DJI-M3D-ASSIST', 'Matrice 3D · 辅助影像', '0', NULL),
  ('dji-m3td', 'dji-pl-m3td-cam', 'EQ-DJI-M3TD-CAM', 'Matrice 3TD · 相机', '0', NULL),
  ('dji-m3td', 'dji-pl-assist', 'EQ-DJI-M3TD-ASSIST', 'Matrice 3TD · 辅助影像', '0', NULL),
  ('dji-m4d', 'dji-pl-m4d-cam', 'EQ-DJI-M4D-CAM', 'Matrice 4D · 相机', '0', NULL),
  ('dji-m4d', 'dji-pl-assist', 'EQ-DJI-M4D-ASSIST', 'Matrice 4D · 辅助影像', '0', NULL),
  ('dji-m4td', 'dji-pl-m4td-cam', 'EQ-DJI-M4TD-CAM', 'Matrice 4TD · 相机', '0', NULL),
  ('dji-m4td', 'dji-pl-assist', 'EQ-DJI-M4TD-ASSIST', 'Matrice 4TD · 辅助影像', '0', NULL),
  ('dji-m4e', 'dji-pl-m4e-cam', 'EQ-DJI-M4E-CAM', 'Matrice 4E · 相机', '0', NULL),
  ('dji-m4t', 'dji-pl-m4t-cam', 'EQ-DJI-M4T-CAM', 'Matrice 4T · 相机', '0', NULL),
  ('dji-dock-1', 'dji-pl-dock-cam', 'EQ-DJI-DOCK-1-OUT', '大疆机场 · 舱外相机', '7', '1'),
  ('dji-dock-2', 'dji-pl-dock-cam', 'EQ-DJI-DOCK-2-IN', '大疆机场 2 · 舱内相机', '7', '0'),
  ('dji-dock-2', 'dji-pl-dock-cam', 'EQ-DJI-DOCK-2-OUT', '大疆机场 2 · 舱外相机', '7', '1'),
  ('dji-dock-3', 'dji-pl-dock-cam', 'EQ-DJI-DOCK-3-IN', '大疆机场 3 · 舱内相机', '7', '0'),
  ('dji-dock-3', 'dji-pl-dock-cam', 'EQ-DJI-DOCK-3-OUT', '大疆机场 3 · 舱外相机', '7', '1');

INSERT INTO ent_equipment_t1 (
  entity_type_code, model_id, parent_id, name, code, guid, custom_fields,
  tenant_id, facility_id, status, creator
)
SELECT
  'equipment',
  payload.id,
  host_entity.id,
  CASE
    WHEN mount.camera_position = '0' THEN '相机 · 舱内，挂在 ' || host_entity.name
    WHEN mount.camera_position = '1' THEN '相机 · 舱外，挂在 ' || host_entity.name
    ELSE '相机 · ' || payload.name || '，挂在 ' || host_entity.name
  END,
  mount.child_code,
  'guid-' || lower(mount.child_code),
  jsonb_strip_nulls(jsonb_build_object(
    'dji_domain', dom.default_value,
    'dji_main_type', mt.default_value,
    'dji_sub_type', st.default_value,
    'dji_camera_position', mount.camera_position
  )),
  1,
  44,
  1,
  'seed'
FROM dji_mount mount
JOIN dynamic_model host_model
  ON host_model.code = mount.host_code AND host_model.tenant_id = 1 AND host_model.deleted = false
JOIN ent_equipment_t1 host_entity
  ON host_entity.code = 'EQ-' || upper(host_model.code)
 AND host_entity.tenant_id = 1
 AND host_entity.deleted = false
JOIN dynamic_model payload
  ON payload.code = mount.payload_code AND payload.tenant_id = 1 AND payload.deleted = false
JOIN dynamic_model_field_assignment dom
  ON dom.model_id = payload.id AND dom.field_code = 'dji_domain' AND dom.deleted = false
JOIN dynamic_model_field_assignment mt
  ON mt.model_id = payload.id AND mt.field_code = 'dji_main_type' AND mt.deleted = false
JOIN dynamic_model_field_assignment st
  ON st.model_id = payload.id AND st.field_code = 'dji_sub_type' AND st.deleted = false
ON CONFLICT (code, tenant_id) WHERE deleted = false AND code IS NOT NULL
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  custom_fields = EXCLUDED.custom_fields,
  facility_id = EXCLUDED.facility_id,
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

UPDATE ent_equipment_t1 child
SET tree_path = '/' || child.parent_id || '/' || child.id || '/',
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM dji_mount mount
WHERE child.code = mount.child_code
  AND child.tenant_id = 1
  AND child.deleted = false
  AND child.parent_id IS NOT NULL;

INSERT INTO dynamic_entity_category_relation_t1 (
  entity_id, category_id, entity_type_code, tenant_id, creator
)
SELECT child.id, 409, 'equipment', 1, 'seed'
FROM dji_mount mount
JOIN ent_equipment_t1 child
  ON child.code = mount.child_code AND child.tenant_id = 1 AND child.deleted = false
ON CONFLICT (tenant_id, entity_type_code, entity_id, category_id)
DO UPDATE SET
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

DELETE FROM dynamic_entity_field_index i
USING dji_mount mount
JOIN ent_equipment_t1 child
  ON child.code = mount.child_code AND child.tenant_id = 1 AND child.deleted = false
WHERE i.entity_id = child.id
  AND i.tenant_id = 1
  AND i.field_code IN ('dji_domain', 'dji_main_type', 'dji_sub_type', 'dji_camera_position');

INSERT INTO dynamic_entity_field_index (
  entity_id, model_id, field_code, value_string, tenant_id, creator
)
SELECT child.id, child.model_id, kv.key, kv.value, 1, 'seed'
FROM dji_mount mount
JOIN ent_equipment_t1 child
  ON child.code = mount.child_code AND child.tenant_id = 1 AND child.deleted = false
CROSS JOIN LATERAL jsonb_each_text(child.custom_fields) kv
WHERE kv.key IN ('dji_domain', 'dji_main_type', 'dji_sub_type', 'dji_camera_position');

UPDATE ent_equipment_t1 host
SET custom_fields = COALESCE(host.custom_fields, '{}'::jsonb) || jsonb_build_object(
  'payload_mounts',
  rows.mounts
)
FROM (
  SELECT host_row.id AS host_id,
         jsonb_agg(
           jsonb_build_object('mountPoint', mount.gimbal_index, 'equipmentId', child.id)
           ORDER BY mount.gimbal_index, child.id
         ) AS mounts
  FROM dji_mount mount
  JOIN dynamic_model host_model
    ON host_model.code = mount.host_code AND host_model.tenant_id = 1 AND host_model.deleted = false
  JOIN ent_equipment_t1 host_row
    ON host_row.code = 'EQ-' || upper(host_model.code)
   AND host_row.tenant_id = 1
   AND host_row.deleted = false
  JOIN ent_equipment_t1 child
    ON child.code = mount.child_code AND child.tenant_id = 1 AND child.deleted = false
  GROUP BY host_row.id
) rows
WHERE host.id = rows.host_id;

COMMIT;
