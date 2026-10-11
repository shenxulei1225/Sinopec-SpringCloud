-- 机头相机按官网指标分成型号。
-- 负责：Matrice 30 / 30T、300 RTK、350 RTK 各建一个机身相机型号，沿用已有的 Matrice 400 机身相机，把已挂在飞机上的机头相机改到对应型号。
-- 不负责：云台相机、机场相机、辅助影像。这些型号已存在，本文件不新建、不改名。
-- 禁止：再造一个 Matrice 400 机头型号；给官网没有机头相机的机型建型号。

SET search_path TO dynamicbusiness;

BEGIN;

INSERT INTO dynamic_field (
    code, name, type, source, status, index_strategy,
    creator, updater, deleted, tenant_id, governance_status, description
)
SELECT incoming.code, incoming.name, 'TEXT', 'USER', 1, 'BTREE',
       'seed', 'seed', false, 1, 'COMPANY', incoming.description
FROM (
    VALUES
        ('fpv_fov', '机头视场角', '官网机头相机的视场角。对角、水平、垂直写在同一格。'),
        ('fpv_frame_rate', '帧率', '官网机头相机的帧率。')
) AS incoming(code, name, description)
WHERE NOT EXISTS (
    SELECT 1 FROM dynamic_field AS existing
    WHERE existing.code = incoming.code
      AND existing.tenant_id = 1
      AND existing.deleted = false
);

INSERT INTO dynamic_model (
    code, name, entity_type_code, status, sort, creator, tenant_id, governance_status, deleted
)
SELECT incoming.code, incoming.name, 'equipment', 1, 0, 'seed', 1, 'COMPANY', false
FROM (
    VALUES
        ('dji-m30-body-camera', 'Matrice 30 / 30T 机身相机'),
        ('dji-m300-rtk-body-camera', 'Matrice 300 RTK 机身相机'),
        ('dji-m350-rtk-body-camera', 'Matrice 350 RTK 机身相机')
) AS incoming(code, name)
WHERE NOT EXISTS (
    SELECT 1 FROM dynamic_model AS existing
    WHERE existing.code = incoming.code
      AND existing.tenant_id = 1
      AND existing.deleted = false
);

INSERT INTO dynamic_model_field_assignment (
    model_id, field_id, required, is_searchable, is_filterable, is_sortable,
    default_value, validation_rules, sort, field_source,
    creator, updater, deleted, tenant_id, model_code, field_code
)
SELECT
    body.id, src.field_id, COALESCE(src.required, false),
    COALESCE(src.is_searchable, false), COALESCE(src.is_filterable, false), COALESCE(src.is_sortable, false),
    src.default_value, src.validation_rules, src.sort, NULLIF(src.field_source, ''),
    'seed', 'seed', false, 1, body.code, src.field_code
FROM dynamic_model AS body
JOIN dynamic_model_field_assignment AS src
  ON src.model_id = 1916
 AND src.deleted = false
 AND src.tenant_id = 1
 AND src.field_source IN ('BASE', 'SYSTEM')
WHERE body.tenant_id = 1
  AND body.deleted = false
  AND body.code IN (
      'dji-m30-body-camera',
      'dji-m300-rtk-body-camera',
      'dji-m350-rtk-body-camera'
  )
  AND NOT EXISTS (
      SELECT 1 FROM dynamic_model_field_assignment AS existing
      WHERE existing.model_id = body.id
        AND existing.field_id = src.field_id
        AND existing.deleted = false
  );

INSERT INTO dynamic_model_field_assignment (
    model_id, field_id, required, is_searchable, is_filterable, is_sortable,
    default_value, sort, creator, updater, deleted, tenant_id, model_code, field_code
)
SELECT
    body.id, src.field_id, false, false, false, false,
    src.default_value, src.sort, 'seed', 'seed', false, 1, body.code, src.field_code
FROM dynamic_model AS body
JOIN dynamic_model AS fpv
  ON fpv.code = 'dji-pl-fpv' AND fpv.tenant_id = 1
JOIN dynamic_model_field_assignment AS src
  ON src.model_id = fpv.id
 AND src.deleted = false
 AND src.tenant_id = 1
 AND src.field_code IN ('dji_domain', 'dji_main_type', 'dji_sub_type')
WHERE body.tenant_id = 1
  AND body.deleted = false
  AND body.code IN (
      'dji-m30-body-camera',
      'dji-m300-rtk-body-camera',
      'dji-m350-rtk-body-camera',
      'dji-m400-body-camera'
  )
  AND NOT EXISTS (
      SELECT 1 FROM dynamic_model_field_assignment AS existing
      WHERE existing.model_id = body.id
        AND existing.field_id = src.field_id
        AND existing.deleted = false
  );

INSERT INTO dynamic_model_category_relation_t1 (
    model_id, category_id, entity_type_code, sort,
    creator, updater, deleted, tenant_id, model_code, category_code
)
SELECT
    body.id, src.category_id, src.entity_type_code, src.sort,
    'seed', 'seed', false, 1, body.code, src.category_code
FROM dynamic_model AS body
JOIN dynamic_model_category_relation_t1 AS src
  ON src.model_id = 1916 AND src.deleted = false AND src.tenant_id = 1
WHERE body.tenant_id = 1
  AND body.deleted = false
  AND body.code IN (
      'dji-m30-body-camera',
      'dji-m300-rtk-body-camera',
      'dji-m350-rtk-body-camera'
  )
  AND NOT EXISTS (
      SELECT 1 FROM dynamic_model_category_relation_t1 AS existing
      WHERE existing.model_id = body.id
        AND existing.category_id = src.category_id
        AND existing.deleted = false
  );

CREATE TEMP TABLE fpv_spec (
    model_code text NOT NULL,
    field_code text NOT NULL,
    default_value text NOT NULL,
    PRIMARY KEY (model_code, field_code)
) ON COMMIT DROP;

INSERT INTO fpv_spec (model_code, field_code, default_value) VALUES
    ('dji-m30-body-camera', 'fpv_resolution', '1920×1080'),
    ('dji-m30-body-camera', 'fpv_fov', '对角 161°'),
    ('dji-m30-body-camera', 'fpv_frame_rate', '30 fps'),
    ('dji-m30-body-camera', 'fpv_night_vision', '星光级'),
    ('dji-m300-rtk-body-camera', 'fpv_resolution', '960p'),
    ('dji-m300-rtk-body-camera', 'fpv_fov', '145°'),
    ('dji-m300-rtk-body-camera', 'fpv_frame_rate', '30 fps'),
    ('dji-m350-rtk-body-camera', 'fpv_resolution', '1080p'),
    ('dji-m350-rtk-body-camera', 'fpv_fov', '142°'),
    ('dji-m350-rtk-body-camera', 'fpv_frame_rate', '30 fps'),
    ('dji-m350-rtk-body-camera', 'fpv_night_vision', '星光级'),
    ('dji-m400-body-camera', 'fpv_resolution', '1080p'),
    ('dji-m400-body-camera', 'fpv_fov', '对角 150°，水平 139.6°，垂直 95.3°'),
    ('dji-m400-body-camera', 'fpv_frame_rate', '30 fps'),
    ('dji-m400-body-camera', 'fpv_night_vision', '星光级');

INSERT INTO dynamic_model_field_assignment (
    model_id, field_id, required, is_searchable, is_filterable, is_sortable,
    default_value, sort, creator, updater, deleted, tenant_id, model_code, field_code
)
SELECT model.id, field.id, false, false, false, false,
       spec.default_value, 120, 'seed', 'seed', false, 1, model.code, field.code
FROM fpv_spec AS spec
JOIN dynamic_model AS model
  ON model.code = spec.model_code AND model.tenant_id = 1 AND model.deleted = false
JOIN dynamic_field AS field
  ON field.code = spec.field_code AND field.tenant_id = 1 AND field.deleted = false
WHERE NOT EXISTS (
    SELECT 1 FROM dynamic_model_field_assignment AS existing
    WHERE existing.model_id = model.id
      AND existing.field_id = field.id
      AND existing.deleted = false
);

UPDATE dynamic_model_field_assignment AS assignment
SET default_value = spec.default_value,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM fpv_spec AS spec
JOIN dynamic_model AS model
  ON model.code = spec.model_code AND model.tenant_id = 1 AND model.deleted = false
WHERE assignment.model_id = model.id
  AND assignment.field_code = spec.field_code
  AND assignment.deleted = false
  AND (assignment.default_value IS NULL OR btrim(assignment.default_value) = '');

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
WHERE model.tenant_id = 1
  AND model.deleted = false
  AND model.code IN (
      'dji-m30-body-camera',
      'dji-m300-rtk-body-camera',
      'dji-m350-rtk-body-camera',
      'dji-m400-body-camera'
  )
  AND NOT EXISTS (
      SELECT 1 FROM dynamic_model_field_assignment AS existing
      WHERE existing.model_id = model.id
        AND existing.field_id = field.id
        AND existing.deleted = false
  );

UPDATE dynamic_model_field_assignment AS assignment
SET default_value = '大疆',
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM dynamic_model AS model
WHERE assignment.model_id = model.id
  AND assignment.deleted = false
  AND assignment.field_code IN ('brand', 'manufacturer')
  AND model.code IN (
      'dji-m30-body-camera',
      'dji-m300-rtk-body-camera',
      'dji-m350-rtk-body-camera',
      'dji-m400-body-camera'
  )
  AND (assignment.default_value IS NULL OR btrim(assignment.default_value) = '');

UPDATE dynamic_model AS model
SET field_groups_config = jsonb_build_object(
        'groups', jsonb_build_array(
            jsonb_build_object(
                'id', 'group-body-camera',
                'name', '机身相机',
                'sort', 100,
                'fields', (
                    SELECT COALESCE(jsonb_agg(
                        jsonb_build_object('fieldId', picked.field_id, 'sort', picked.ord)
                        ORDER BY picked.ord
                    ), '[]'::jsonb)
                    FROM (
                        SELECT assignment.field_id,
                               row_number() OVER (ORDER BY assignment.field_code) AS ord
                        FROM dynamic_model_field_assignment AS assignment
                        WHERE assignment.model_id = model.id
                          AND assignment.deleted = false
                          AND assignment.tenant_id = 1
                          AND assignment.field_code IN (
                              'fpv_resolution', 'fpv_fov', 'fpv_frame_rate', 'fpv_night_vision'
                          )
                    ) AS picked
                )
            )
        )
    )::text,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE model.tenant_id = 1
  AND model.deleted = false
  AND model.code IN (
      'dji-m30-body-camera',
      'dji-m300-rtk-body-camera',
      'dji-m350-rtk-body-camera',
      'dji-m400-body-camera'
  )
  AND (
      model.field_groups_config IS NULL
      OR btrim(model.field_groups_config) = ''
      OR NOT EXISTS (
          SELECT 1
          FROM jsonb_array_elements(model.field_groups_config::jsonb->'groups') AS grp
          WHERE COALESCE(grp->>'name', '') <> '机身相机'
      )
  );

UPDATE ent_equipment_t1 AS camera
SET model_id = body.id,
    name = '相机 · ' || body.name,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM ent_equipment_t1 AS host
JOIN dynamic_model AS host_model
  ON host_model.id = host.model_id AND host_model.deleted = false
JOIN dynamic_model AS body
  ON body.tenant_id = 1
 AND body.deleted = false
 AND body.code = CASE host_model.code
      WHEN 'dji-m400' THEN 'dji-m400-body-camera'
      WHEN 'dji-m350-rtk' THEN 'dji-m350-rtk-body-camera'
      WHEN 'dji-m300-rtk' THEN 'dji-m300-rtk-body-camera'
      WHEN 'dji-m30' THEN 'dji-m30-body-camera'
      WHEN 'dji-m30t' THEN 'dji-m30-body-camera'
    END
WHERE camera.parent_id = host.id
  AND camera.tenant_id = 1
  AND camera.deleted = false
  AND camera.code LIKE 'EQ-DJI-%-FPV';

INSERT INTO ent_equipment_t1 (
    entity_type_code, model_id, parent_id, name, code, guid,
    tenant_id, facility_id, status, creator, brand, manufacturer
)
SELECT
    'equipment',
    body.id,
    host.id,
    '相机 · ' || body.name,
    host.code || '-FPV',
    'guid-' || lower(host.code || '-FPV'),
    1,
    host.facility_id,
    1,
    'seed',
    '大疆',
    '大疆'
FROM ent_equipment_t1 AS host
JOIN dynamic_model AS body
  ON body.code = 'dji-m30-body-camera' AND body.tenant_id = 1 AND body.deleted = false
WHERE host.id = 900158
  AND host.deleted = false
  AND NOT EXISTS (
      SELECT 1 FROM ent_equipment_t1 AS existing
      WHERE existing.parent_id = host.id
        AND existing.deleted = false
        AND existing.code LIKE '%-FPV'
  );

UPDATE ent_equipment_t1 AS camera
SET tree_path = '/' || camera.parent_id || '/' || camera.id || '/',
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE camera.tenant_id = 1
  AND camera.deleted = false
  AND camera.code = 'EQ-DJI-PILOT-JQ-01-FPV'
  AND camera.parent_id IS NOT NULL;

INSERT INTO dynamic_entity_category_relation_t1 (
    entity_id, category_id, entity_type_code, sort, creator, updater, deleted, tenant_id
)
SELECT camera.id, category.category_id, 'equipment', 0, 'seed', 'seed', false, 1
FROM ent_equipment_t1 AS camera
CROSS JOIN (
    VALUES (409), (2104809)
) AS category(category_id)
WHERE camera.code = 'EQ-DJI-PILOT-JQ-01-FPV'
  AND camera.tenant_id = 1
  AND camera.deleted = false
  AND NOT EXISTS (
      SELECT 1 FROM dynamic_entity_category_relation_t1 AS existing
      WHERE existing.entity_id = camera.id
        AND existing.category_id = category.category_id
        AND existing.deleted = false
  );

UPDATE ent_equipment_t1 AS equipment
SET brand = COALESCE(NULLIF(btrim(equipment.brand), ''), '大疆'),
    manufacturer = COALESCE(NULLIF(btrim(equipment.manufacturer), ''), '大疆'),
    custom_fields = COALESCE(equipment.custom_fields, '{}'::jsonb) || filled.fields,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM (
    SELECT assignment.model_id,
           jsonb_object_agg(
               assignment.field_code,
               to_jsonb(btrim(assignment.default_value))
           ) AS fields
    FROM dynamic_model_field_assignment AS assignment
    JOIN dynamic_model AS model ON model.id = assignment.model_id
    WHERE assignment.deleted = false
      AND COALESCE(btrim(assignment.default_value), '') <> ''
      AND model.code IN (
          'dji-m30-body-camera',
          'dji-m300-rtk-body-camera',
          'dji-m350-rtk-body-camera',
          'dji-m400-body-camera'
      )
      AND assignment.field_code IN (
          'fpv_resolution', 'fpv_fov', 'fpv_frame_rate', 'fpv_night_vision',
          'dji_domain', 'dji_main_type', 'dji_sub_type'
      )
    GROUP BY assignment.model_id
) AS filled
WHERE equipment.model_id = filled.model_id
  AND equipment.deleted = false
  AND (
      equipment.code LIKE 'EQ-DJI-%-FPV'
      OR equipment.code = 'equipment-cam-spec-dji-m400-body-camera'
  );

UPDATE dynamic_model AS aircraft
SET field_groups_config = (
        SELECT jsonb_set(
            aircraft.field_groups_config::jsonb,
            '{groups}',
            (
                SELECT COALESCE(jsonb_agg(patched ORDER BY ord), '[]'::jsonb)
                FROM (
                    SELECT ord,
                        CASE
                            WHEN grp->>'name' = '设备指标' THEN jsonb_set(
                                grp,
                                '{fields}',
                                (
                                    SELECT COALESCE(jsonb_agg(patched_field ORDER BY field_ord), '[]'::jsonb)
                                    FROM (
                                        SELECT field_ord,
                                            CASE
                                                WHEN (field_ref->>'fieldId')::bigint = ref.field_id THEN jsonb_set(
                                                    field_ref,
                                                    '{extra,displayFieldIds}',
                                                    jsonb_build_array(
                                                        6816,
                                                        6817,
                                                        fov.id,
                                                        frame_rate.id,
                                                        6634,
                                                        6635
                                                    )
                                                )
                                                ELSE field_ref
                                            END AS patched_field
                                        FROM jsonb_array_elements(COALESCE(grp->'fields', '[]'::jsonb))
                                            WITH ORDINALITY AS fields(field_ref, field_ord)
                                    ) AS field_rows
                                )
                            )
                            ELSE grp
                        END AS patched
                    FROM jsonb_array_elements(aircraft.field_groups_config::jsonb->'groups')
                        WITH ORDINALITY AS groups(grp, ord)
                ) AS rows
            )
        )::text
    ),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM (
    SELECT id AS field_id
    FROM dynamic_field
    WHERE code = 'body_camera_group' AND tenant_id = 1 AND deleted = false
) AS ref
CROSS JOIN (
    SELECT id FROM dynamic_field
    WHERE code = 'fpv_fov' AND tenant_id = 1 AND deleted = false
) AS fov
CROSS JOIN (
    SELECT id FROM dynamic_field
    WHERE code = 'fpv_frame_rate' AND tenant_id = 1 AND deleted = false
) AS frame_rate
WHERE aircraft.code = 'dji-m400'
  AND aircraft.tenant_id = 1
  AND aircraft.deleted = false;

UPDATE ent_equipment_t1
SET deleted = true,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE code = 'equipment-cam-spec-dji-pl-fpv'
  AND tenant_id = 1
  AND deleted = false;

UPDATE dynamic_entity_category_relation_t1 AS relation
SET deleted = true,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM ent_equipment_t1 AS equipment
WHERE relation.entity_id = equipment.id
  AND equipment.code = 'equipment-cam-spec-dji-pl-fpv'
  AND relation.deleted = false;

UPDATE dynamic_model AS model
SET deleted = true,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE model.code = 'dji-pl-fpv'
  AND model.tenant_id = 1
  AND model.deleted = false
  AND NOT EXISTS (
      SELECT 1 FROM ent_equipment_t1 AS equipment
      WHERE equipment.model_id = model.id
        AND equipment.deleted = false
  );

UPDATE dynamic_model_category_relation_t1
SET deleted = true,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE model_code = 'dji-pl-fpv'
  AND deleted = false
  AND NOT EXISTS (
      SELECT 1 FROM dynamic_model AS model
      JOIN ent_equipment_t1 AS equipment ON equipment.model_id = model.id AND equipment.deleted = false
      WHERE model.code = 'dji-pl-fpv' AND model.tenant_id = 1 AND model.deleted = false
  );

COMMIT;

SELECT model.code, model.name, model.deleted
FROM dynamic_model AS model
WHERE model.tenant_id = 1
  AND model.code IN (
      'dji-pl-fpv',
      'dji-m30-body-camera',
      'dji-m300-rtk-body-camera',
      'dji-m350-rtk-body-camera',
      'dji-m400-body-camera'
  )
ORDER BY model.code;

SELECT host_model.code AS aircraft, camera.name, body.code AS camera_model, count(*)
FROM ent_equipment_t1 AS camera
JOIN dynamic_model AS body ON body.id = camera.model_id
JOIN ent_equipment_t1 AS host ON host.id = camera.parent_id
JOIN dynamic_model AS host_model ON host_model.id = host.model_id
WHERE camera.deleted = false
  AND camera.code LIKE 'EQ-DJI-%-FPV'
GROUP BY host_model.code, camera.name, body.code
ORDER BY host_model.code;
