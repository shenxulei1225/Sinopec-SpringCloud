SET search_path TO dynamicbusiness;

BEGIN;

INSERT INTO dynamic_model (
    code, name, entity_type_code, status, sort, creator, tenant_id, governance_status, deleted
)
SELECT 'dji-m400-body-camera', 'Matrice 400 机身相机', 'equipment', 1, 0, 'seed', 1, 'COMPANY', false
WHERE NOT EXISTS (
    SELECT 1 FROM dynamic_model
    WHERE code = 'dji-m400-body-camera' AND tenant_id = 1 AND deleted = false
);

INSERT INTO dynamic_model_field_assignment (
    model_id, field_id, required, is_searchable, is_filterable, is_sortable,
    default_value, validation_rules, sort, field_source,
    creator, create_time, updater, update_time, deleted, tenant_id, model_code, field_code
)
SELECT
    body.id, src.field_id, COALESCE(src.required, false),
    COALESCE(src.is_searchable, false), COALESCE(src.is_filterable, false), COALESCE(src.is_sortable, false),
    src.default_value, src.validation_rules, src.sort, NULLIF(src.field_source, ''),
    'seed', CURRENT_TIMESTAMP, 'seed', CURRENT_TIMESTAMP, false, 1, body.code, src.field_code
FROM dynamic_model body
JOIN dynamic_model_field_assignment src
  ON src.model_code = 'dji-m400'
 AND src.tenant_id = 1
 AND src.deleted = false
 AND src.field_code IN ('fpv_resolution', 'fpv_night_vision', 'gimbal_pitch_min', 'gimbal_pitch_max')
WHERE body.code = 'dji-m400-body-camera'
  AND body.tenant_id = 1
  AND body.deleted = false
  AND NOT EXISTS (
      SELECT 1 FROM dynamic_model_field_assignment existing
      WHERE existing.model_code = body.code
        AND existing.field_code = src.field_code
        AND existing.tenant_id = 1
        AND existing.deleted = false
  );

INSERT INTO dynamic_field (
    code, name, type, source, status, options, provider_code, index_strategy,
    governance_status, creator, create_time, updater, update_time, deleted, tenant_id, description
)
SELECT
    'body_camera_group',
    '机身分组',
    'REF_MODEL',
    'USER',
    1,
    jsonb_build_object(
        'entityTypeCode', 'equipment',
        'sourceModelId', body.id,
        'sourceFieldIds', jsonb_build_array(6816, 6817, 6634, 6635)
    )::text,
    'dynamic-entity:equipment',
    'NONE',
    'COMPANY',
    'seed',
    CURRENT_TIMESTAMP,
    'seed',
    CURRENT_TIMESTAMP,
    false,
    1,
    '机身相机收成的引用型号字段。数值写在机身相机型号上。'
FROM dynamic_model body
WHERE body.code = 'dji-m400-body-camera'
  AND body.tenant_id = 1
  AND body.deleted = false
  AND NOT EXISTS (
      SELECT 1 FROM dynamic_field
      WHERE code = 'body_camera_group' AND tenant_id = 1 AND deleted = false
  );

INSERT INTO dynamic_model_field_assignment (
    model_id, field_id, required, is_searchable, is_filterable, is_sortable,
    sort, field_source, creator, create_time, updater, update_time, deleted, tenant_id, model_code, field_code
)
SELECT
    aircraft.id, field.id, false, false, false, false,
    5, 'CUSTOM', 'seed', CURRENT_TIMESTAMP, 'seed', CURRENT_TIMESTAMP, false, 1, aircraft.code, field.code
FROM dynamic_model aircraft
JOIN dynamic_field field
  ON field.code = 'body_camera_group' AND field.tenant_id = 1 AND field.deleted = false
WHERE aircraft.code = 'dji-m400'
  AND aircraft.tenant_id = 1
  AND aircraft.deleted = false
  AND NOT EXISTS (
      SELECT 1 FROM dynamic_model_field_assignment existing
      WHERE existing.model_code = aircraft.code
        AND existing.field_code = field.code
        AND existing.tenant_id = 1
        AND existing.deleted = false
  );

UPDATE dynamic_model_field_assignment
SET deleted = true,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE model_code = 'dji-m400'
  AND tenant_id = 1
  AND deleted = false
  AND field_code IN ('fpv_resolution', 'fpv_night_vision', 'gimbal_pitch_min', 'gimbal_pitch_max');

UPDATE dynamic_model aircraft
SET field_groups_config = (
        SELECT jsonb_set(
            aircraft.field_groups_config::jsonb,
            '{groups}',
            (
                SELECT COALESCE(jsonb_agg(patched ORDER BY ord), '[]'::jsonb)
                FROM (
                    SELECT ord,
                        CASE
                            WHEN g->>'name' = '设备指标'
                             AND NOT EXISTS (
                                 SELECT 1
                                 FROM jsonb_array_elements(COALESCE(g->'fields', '[]'::jsonb)) f
                                 WHERE (f->>'fieldId')::bigint = ref.field_id
                             )
                            THEN jsonb_set(
                                g,
                                '{fields}',
                                COALESCE(g->'fields', '[]'::jsonb) || jsonb_build_array(
                                    jsonb_build_object('fieldId', ref.field_id, 'sort', 5, 'extra', '{}'::jsonb)
                                )
                            )
                            ELSE g
                        END AS patched
                    FROM jsonb_array_elements(aircraft.field_groups_config::jsonb->'groups') WITH ORDINALITY AS t(g, ord)
                    WHERE COALESCE(g->>'name', '') <> '机身相机'
                ) rows
            )
        )::text
    ),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM (
    SELECT id AS field_id
    FROM dynamic_field
    WHERE code = 'body_camera_group' AND tenant_id = 1 AND deleted = false
) ref
WHERE aircraft.code = 'dji-m400'
  AND aircraft.tenant_id = 1
  AND aircraft.deleted = false;

INSERT INTO dynamic_model_category_relation_t1 (
    model_id, category_id, entity_type_code, sort,
    creator, create_time, updater, update_time, deleted, tenant_id, model_code, category_code
)
SELECT
    body.id, src.category_id, src.entity_type_code, src.sort,
    'seed', CURRENT_TIMESTAMP, 'seed', CURRENT_TIMESTAMP, false, 1, body.code, src.category_code
FROM dynamic_model body
JOIN dynamic_model_category_relation_t1 src
  ON src.model_code = 'dji-m400' AND src.deleted = false AND src.tenant_id = 1
WHERE body.code = 'dji-m400-body-camera'
  AND body.tenant_id = 1
  AND body.deleted = false
  AND NOT EXISTS (
      SELECT 1 FROM dynamic_model_category_relation_t1 existing
      WHERE existing.model_code = body.code
        AND existing.category_id = src.category_id
        AND existing.deleted = false
  );

INSERT INTO dynamic_model_field_assignment (
    model_id, field_id, required, is_searchable, is_filterable, is_sortable,
    sort, field_source, creator, create_time, updater, update_time, deleted, tenant_id, model_code, field_code
)
SELECT
    body.id,
    lib.id,
    COALESCE(base.required, false),
    COALESCE(base.is_searchable, true),
    COALESCE(base.is_filterable, true),
    COALESCE(base.is_sortable, true),
    base.sort_order,
    CASE
        WHEN base.field_code = 'facility_id' OR lower(base.field_code) = 'parentid' THEN 'SYSTEM'
        ELSE 'BASE'
    END,
    'seed', CURRENT_TIMESTAMP, 'seed', CURRENT_TIMESTAMP, false, body.tenant_id, body.code, lib.code
FROM dynamic_model body
JOIN dynamic_entity_type_base_field base
  ON base.entity_type_code = body.entity_type_code
 AND base.tenant_id = body.tenant_id
 AND base.deleted = false
 AND base.status = 1
JOIN LATERAL (
    SELECT picked.id, picked.code
    FROM (
        SELECT f.id, f.code, 0 AS pref
        FROM dynamic_field f
        WHERE base.library_field_id IS NOT NULL
          AND f.id = base.library_field_id
          AND f.deleted = false
        UNION ALL
        SELECT f.id, f.code, 1 AS pref
        FROM dynamic_field f
        WHERE f.code = base.field_code
          AND f.tenant_id = base.tenant_id
          AND f.deleted = false
          AND NOT EXISTS (
              SELECT 1 FROM dynamic_field kept
              WHERE kept.id = base.library_field_id AND kept.deleted = false
          )
    ) picked
    ORDER BY picked.pref, picked.id
    LIMIT 1
) lib ON true
WHERE body.code = 'dji-m400-body-camera'
  AND body.tenant_id = 1
  AND body.deleted = false
  AND NOT EXISTS (
      SELECT 1 FROM dynamic_model_field_assignment existing
      WHERE existing.deleted = false
        AND existing.tenant_id = body.tenant_id
        AND (
            (existing.model_id = body.id AND existing.field_id = lib.id)
            OR (existing.model_code = body.code AND existing.field_code = lib.code)
        )
  );

COMMIT;
