-- 能换相机的飞机，每种云台相机各一架，主云台挂这一款，并带上 FPV。
-- 负责：Matrice 300 RTK、350 RTK、400 上还没挂过的禅思相机。新建飞机时把型号上已有的云台口和俯仰写进这一架。新建相机时把型号上已有的镜头写进这一台。
-- 不负责：一体式相机的飞机（30、3 系、4 系）再复制一架。东古吊装海康吊装相机，见 dynamic_equipment_donggu_hik_camera.sql。右云台和上方云台不预挂。拍照方式、对焦方式、曝光方式型号上没有勾选，不写。
-- 已有的那一架保持原来的相机：300 RTK 仍是 H20T，350 RTK 和 400 仍是 H30T。

SET search_path TO dynamicbusiness;

BEGIN;

CREATE TEMP TABLE extra_airframe (
  host_model text NOT NULL,
  payload_model text NOT NULL,
  suffix text NOT NULL,
  PRIMARY KEY (host_model, payload_model)
) ON COMMIT DROP;

INSERT INTO extra_airframe (host_model, payload_model, suffix) VALUES
  ('dji-m300-rtk', 'dji-pl-h20', 'H20'),
  ('dji-m300-rtk', 'dji-pl-h20n', 'H20N'),
  ('dji-m300-rtk', 'dji-pl-h30', 'H30'),
  ('dji-m300-rtk', 'dji-pl-h30t', 'H30T'),
  ('dji-m300-rtk', 'dji-pl-z30', 'Z30'),
  ('dji-m300-rtk', 'dji-pl-xt2', 'XT2'),
  ('dji-m300-rtk', 'dji-pl-xts', 'XTS'),
  ('dji-m350-rtk', 'dji-pl-h20', 'H20'),
  ('dji-m350-rtk', 'dji-pl-h20t', 'H20T'),
  ('dji-m350-rtk', 'dji-pl-h20n', 'H20N'),
  ('dji-m350-rtk', 'dji-pl-h30', 'H30'),
  ('dji-m400', 'dji-pl-h30', 'H30');

UPDATE ent_equipment_t1 AS host
SET name = renamed.label,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM (
  VALUES
    ('EQ-DJI-M300-RTK', '无人机 · Matrice 300 RTK（搭配禅思 H20T）'),
    ('EQ-DJI-M350-RTK', '无人机 · Matrice 350 RTK（搭配禅思 H30T）'),
    ('EQ-DJI-M400', '无人机 · Matrice 400（搭配禅思 H30T）')
) AS renamed(code, label)
WHERE host.code = renamed.code
  AND host.tenant_id = 1
  AND host.deleted = false;

INSERT INTO ent_equipment_t1 (
  entity_type_code, model_id, name, code, guid, custom_fields,
  tenant_id, facility_id, status, creator
)
SELECT
  'equipment',
  host_model.id,
  '无人机 · ' || replace(host_model.name, '（可换吊舱）', '') || '（搭配禅思 ' || extra.suffix || '）',
  'EQ-' || upper(extra.host_model) || '-' || extra.suffix,
  'guid-eq-' || extra.host_model || '-' || lower(extra.suffix),
  jsonb_strip_nulls(jsonb_build_object(
    'dji_domain', dom.default_value,
    'dji_main_type', mt.default_value,
    'dji_sub_type', st.default_value
  )) || COALESCE(spec.values, '{}'::jsonb),
  1,
  44,
  1,
  'seed'
FROM extra_airframe extra
JOIN dynamic_model host_model
  ON host_model.code = extra.host_model AND host_model.tenant_id = 1 AND host_model.deleted = false
JOIN dynamic_model payload
  ON payload.code = extra.payload_model AND payload.tenant_id = 1 AND payload.deleted = false
JOIN dynamic_model_field_assignment dom
  ON dom.model_id = host_model.id AND dom.field_code = 'dji_domain' AND dom.deleted = false AND dom.tenant_id = 1
JOIN dynamic_model_field_assignment mt
  ON mt.model_id = host_model.id AND mt.field_code = 'dji_main_type' AND mt.deleted = false AND mt.tenant_id = 1
JOIN dynamic_model_field_assignment st
  ON st.model_id = host_model.id AND st.field_code = 'dji_sub_type' AND st.deleted = false AND st.tenant_id = 1
LEFT JOIN LATERAL (
  SELECT jsonb_object_agg(
    a.field_code,
    CASE
      WHEN left(btrim(a.default_value), 1) = '[' THEN a.default_value::jsonb
      WHEN a.field_code IN ('gimbal_pitch_min', 'gimbal_pitch_max') THEN to_jsonb(a.default_value::numeric)
      ELSE to_jsonb(a.default_value)
    END
  ) AS values
  FROM dynamic_model_field_assignment a
  WHERE a.model_id = host_model.id
    AND a.deleted = false
    AND a.tenant_id = 1
    AND a.default_value IS NOT NULL
    AND btrim(a.default_value) <> ''
    AND a.field_code IN ('allowed_gimbal_ports', 'gimbal_pitch_min', 'gimbal_pitch_max')
) spec ON true
ON CONFLICT (code, tenant_id) WHERE deleted = false AND code IS NOT NULL
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  name = EXCLUDED.name,
  custom_fields = COALESCE(ent_equipment_t1.custom_fields, '{}'::jsonb) || EXCLUDED.custom_fields,
  facility_id = EXCLUDED.facility_id,
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO ent_equipment_t1 (
  entity_type_code, model_id, parent_id, name, code, guid, custom_fields,
  tenant_id, facility_id, status, creator
)
SELECT
  'equipment',
  payload.id,
  host.id,
  '相机 · ' || payload.name || '，挂在 ' || host.name,
  host.code || '-CAM',
  'guid-' || lower(host.code || '-CAM'),
  jsonb_strip_nulls(jsonb_build_object(
    'dji_domain', dom.default_value,
    'dji_main_type', mt.default_value,
    'dji_sub_type', st.default_value
  )) || COALESCE(spec.values, '{}'::jsonb),
  1,
  44,
  1,
  'seed'
FROM extra_airframe extra
JOIN dynamic_model host_model
  ON host_model.code = extra.host_model AND host_model.tenant_id = 1 AND host_model.deleted = false
JOIN ent_equipment_t1 host
  ON host.code = 'EQ-' || upper(extra.host_model) || '-' || extra.suffix
 AND host.tenant_id = 1
 AND host.deleted = false
JOIN dynamic_model payload
  ON payload.code = extra.payload_model AND payload.tenant_id = 1 AND payload.deleted = false
JOIN dynamic_model_field_assignment dom
  ON dom.model_id = payload.id AND dom.field_code = 'dji_domain' AND dom.deleted = false AND dom.tenant_id = 1
JOIN dynamic_model_field_assignment mt
  ON mt.model_id = payload.id AND mt.field_code = 'dji_main_type' AND mt.deleted = false AND mt.tenant_id = 1
JOIN dynamic_model_field_assignment st
  ON st.model_id = payload.id AND st.field_code = 'dji_sub_type' AND st.deleted = false AND st.tenant_id = 1
LEFT JOIN LATERAL (
  SELECT jsonb_object_agg(
    a.field_code,
    CASE
      WHEN left(btrim(a.default_value), 1) = '[' THEN a.default_value::jsonb
      ELSE to_jsonb(a.default_value)
    END
  ) AS values
  FROM dynamic_model_field_assignment a
  WHERE a.model_id = payload.id
    AND a.deleted = false
    AND a.tenant_id = 1
    AND a.default_value IS NOT NULL
    AND btrim(a.default_value) <> ''
    AND a.field_code IN ('supported_lenses', 'default_lens')
) spec ON true
ON CONFLICT (code, tenant_id) WHERE deleted = false AND code IS NOT NULL
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  custom_fields = COALESCE(ent_equipment_t1.custom_fields, '{}'::jsonb) || EXCLUDED.custom_fields,
  facility_id = EXCLUDED.facility_id,
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO ent_equipment_t1 (
  entity_type_code, model_id, parent_id, name, code, guid, custom_fields,
  tenant_id, facility_id, status, creator
)
SELECT
  'equipment',
  fpv.id,
  host.id,
  '相机 · FPV（机头观察），挂在 ' || host.name,
  host.code || '-FPV',
  'guid-' || lower(host.code || '-FPV'),
  jsonb_strip_nulls(jsonb_build_object(
    'dji_domain', dom.default_value,
    'dji_main_type', mt.default_value,
    'dji_sub_type', st.default_value
  )),
  1,
  44,
  1,
  'seed'
FROM extra_airframe extra
JOIN ent_equipment_t1 host
  ON host.code = 'EQ-' || upper(extra.host_model) || '-' || extra.suffix
 AND host.tenant_id = 1
 AND host.deleted = false
JOIN dynamic_model fpv
  ON fpv.code = 'dji-pl-fpv' AND fpv.tenant_id = 1 AND fpv.deleted = false
JOIN dynamic_model_field_assignment dom
  ON dom.model_id = fpv.id AND dom.field_code = 'dji_domain' AND dom.deleted = false AND dom.tenant_id = 1
JOIN dynamic_model_field_assignment mt
  ON mt.model_id = fpv.id AND mt.field_code = 'dji_main_type' AND mt.deleted = false AND mt.tenant_id = 1
JOIN dynamic_model_field_assignment st
  ON st.model_id = fpv.id AND st.field_code = 'dji_sub_type' AND st.deleted = false AND st.tenant_id = 1
ON CONFLICT (code, tenant_id) WHERE deleted = false AND code IS NOT NULL
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  custom_fields = COALESCE(ent_equipment_t1.custom_fields, '{}'::jsonb) || EXCLUDED.custom_fields,
  facility_id = EXCLUDED.facility_id,
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

UPDATE ent_equipment_t1 child
SET tree_path = '/' || child.parent_id || '/' || child.id || '/',
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE child.tenant_id = 1
  AND child.deleted = false
  AND child.parent_id IS NOT NULL
  AND (child.code LIKE 'EQ-DJI-M300-RTK-%-CAM'
    OR child.code LIKE 'EQ-DJI-M300-RTK-%-FPV'
    OR child.code LIKE 'EQ-DJI-M350-RTK-%-CAM'
    OR child.code LIKE 'EQ-DJI-M350-RTK-%-FPV'
    OR child.code LIKE 'EQ-DJI-M400-%-CAM'
    OR child.code LIKE 'EQ-DJI-M400-%-FPV')
  AND child.code NOT IN ('EQ-DJI-M300-RTK-FPV', 'EQ-DJI-M350-RTK-FPV', 'EQ-DJI-M400-FPV');

INSERT INTO dynamic_entity_category_relation_t1 (
  entity_id, category_id, entity_type_code, tenant_id, creator
)
SELECT e.id, 409, 'equipment', 1, 'seed'
FROM ent_equipment_t1 e
WHERE e.tenant_id = 1
  AND e.deleted = false
  AND (
    e.code ~ '^EQ-DJI-M300-RTK-(H20|H20N|H30|H30T|Z30|XT2|XTS)(-CAM|-FPV)?$'
    OR e.code ~ '^EQ-DJI-M350-RTK-(H20|H20T|H20N|H30)(-CAM|-FPV)?$'
    OR e.code ~ '^EQ-DJI-M400-H30(-CAM|-FPV)?$'
  )
ON CONFLICT (tenant_id, entity_type_code, entity_id, category_id)
DO UPDATE SET
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

DELETE FROM dynamic_entity_field_index i
USING ent_equipment_t1 e
WHERE i.entity_id = e.id
  AND i.tenant_id = 1
  AND e.tenant_id = 1
  AND e.deleted = false
  AND (
    e.code ~ '^EQ-DJI-M300-RTK-(H20|H20N|H30|H30T|Z30|XT2|XTS)(-CAM|-FPV)?$'
    OR e.code ~ '^EQ-DJI-M350-RTK-(H20|H20T|H20N|H30)(-CAM|-FPV)?$'
    OR e.code ~ '^EQ-DJI-M400-H30(-CAM|-FPV)?$'
  )
  AND i.field_code IN ('dji_domain', 'dji_main_type', 'dji_sub_type');

INSERT INTO dynamic_entity_field_index (
  entity_id, model_id, field_code, value_string, tenant_id, creator
)
SELECT e.id, e.model_id, kv.key, kv.value, 1, 'seed'
FROM ent_equipment_t1 e
CROSS JOIN LATERAL jsonb_each_text(e.custom_fields) kv
WHERE e.tenant_id = 1
  AND e.deleted = false
  AND (
    e.code ~ '^EQ-DJI-M300-RTK-(H20|H20N|H30|H30T|Z30|XT2|XTS)(-CAM|-FPV)?$'
    OR e.code ~ '^EQ-DJI-M350-RTK-(H20|H20T|H20N|H30)(-CAM|-FPV)?$'
    OR e.code ~ '^EQ-DJI-M400-H30(-CAM|-FPV)?$'
  )
  AND kv.key IN ('dji_domain', 'dji_main_type', 'dji_sub_type');

-- 已经在库里的飞机和相机：型号上有默认值、这一台还空着的，写进这一台。
UPDATE ent_equipment_t1 e
SET custom_fields = COALESCE(e.custom_fields, '{}'::jsonb) || patch.values,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM (
  SELECT e2.id,
    jsonb_object_agg(
      a.field_code,
      CASE
        WHEN left(btrim(a.default_value), 1) = '[' THEN a.default_value::jsonb
        WHEN a.field_code IN ('gimbal_pitch_min', 'gimbal_pitch_max') THEN to_jsonb(a.default_value::numeric)
        ELSE to_jsonb(a.default_value)
      END
    ) AS values
  FROM ent_equipment_t1 e2
  JOIN dynamic_model_field_assignment a
    ON a.model_id = e2.model_id AND a.deleted = false AND a.tenant_id = 1
  WHERE e2.deleted = false
    AND e2.tenant_id = 1
    AND a.default_value IS NOT NULL
    AND btrim(a.default_value) <> ''
    AND a.field_code IN (
      'supported_lenses', 'default_lens',
      'allowed_gimbal_ports', 'gimbal_pitch_min', 'gimbal_pitch_max'
    )
    AND NULLIF(btrim(e2.custom_fields ->> a.field_code), '') IS NULL
  GROUP BY e2.id
) patch
WHERE e.id = patch.id;

UPDATE ent_equipment_t1 host
SET custom_fields = COALESCE(host.custom_fields, '{}'::jsonb) || jsonb_build_object(
  'payload_mounts',
  (
    SELECT jsonb_agg(
      jsonb_build_object(
        'mountPoint', CASE WHEN child.code LIKE '%-FPV' THEN '7' ELSE '0' END,
        'equipmentId', child.id
      )
      ORDER BY child.code
    )
    FROM ent_equipment_t1 child
    WHERE child.deleted = false
      AND child.tenant_id = 1
      AND child.code IN (host.code || '-CAM', host.code || '-FPV')
  )
)
FROM extra_airframe extra
JOIN dynamic_model host_model
  ON host_model.code = extra.host_model AND host_model.tenant_id = 1 AND host_model.deleted = false
WHERE host.code = 'EQ-' || upper(extra.host_model) || '-' || extra.suffix
  AND host.tenant_id = 1
  AND host.deleted = false
  AND EXISTS (
    SELECT 1
    FROM ent_equipment_t1 child
    WHERE child.deleted = false
      AND child.tenant_id = 1
      AND child.code IN (host.code || '-CAM', host.code || '-FPV')
  );

COMMIT;
