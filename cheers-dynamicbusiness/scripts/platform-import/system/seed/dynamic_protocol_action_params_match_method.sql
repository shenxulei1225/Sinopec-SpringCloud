-- 可执行动作的参数改成机场方法真正要的字段。
-- 相机的负载索引编号是「类型-子类型-云台位置」，不是负载挂载位置，也不是相机类型。变焦倍数不是焦距。
SET search_path TO dynamicbusiness;
BEGIN;

INSERT INTO dynamic_field (code, name, type, description, source, status, tenant_id, creator)
VALUES
  ('payload_index', '相机的负载索引编号', 'STRING', '类型-子类型-云台位置。来自所选执行设备上报的负载索引，不是相机类型。39-0-7 只是官网举例，不是默认值。', 'LIBRARY', 1, 1, 'seed'),
  ('zoom_factor', '变焦倍数', 'NUMBER', '负载控制—变焦要的倍数。可见光是 2 到 200，红外是 2 到 20。', 'LIBRARY', 1, 1, 'seed')
ON CONFLICT (code, tenant_id) WHERE deleted = false DO NOTHING;

UPDATE ent_action_t1 a
SET param_slots_json = jsonb_set(
      a.param_slots_json,
      '{methods}',
      (
        SELECT jsonb_agg(
          CASE
            WHEN elem->>'executionMeans' = 'UAV' THEN jsonb_set(
              elem,
              '{fields}',
              COALESCE(elem->'fields', '[]'::jsonb) || (
                SELECT COALESCE(jsonb_agg(add.field), '[]'::jsonb)
                FROM (VALUES
                  ('{"fieldCode":"payload_index","required":true,"defaultValue":null}'::jsonb)
                ) AS add(field)
                WHERE NOT EXISTS (
                  SELECT 1 FROM jsonb_array_elements(COALESCE(elem->'fields', '[]'::jsonb)) have
                  WHERE have->>'fieldCode' = 'payload_index'
                )
              )
            )
            ELSE elem
          END
        )
        FROM jsonb_array_elements(a.param_slots_json->'methods') elem
      )
    ),
    updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE a.deleted = false AND a.tenant_id = 1
  AND jsonb_typeof(a.param_slots_json->'methods') = 'array'
  AND a.code IN ('act-robot-shoot', 'takePhoto', 'zoom', 'focus', 'startRecord', 'stopRecord', 'panoShot');

UPDATE ent_action_t1 a
SET param_slots_json = jsonb_set(
      a.param_slots_json,
      '{methods}',
      (
        SELECT jsonb_agg(
          jsonb_set(
            elem,
            '{fields}',
            (
              SELECT COALESCE(jsonb_agg(
                CASE
                  WHEN f->>'fieldCode' = 'focal_length'
                    THEN '{"fieldCode":"zoom_factor","required":true,"defaultValue":null}'::jsonb
                  ELSE f
                END
              ), '[]'::jsonb)
              FROM jsonb_array_elements(COALESCE(elem->'fields', '[]'::jsonb)) f
              WHERE NOT (f->>'fieldCode' = 'zoom_factor' AND EXISTS (
                SELECT 1 FROM jsonb_array_elements(COALESCE(elem->'fields', '[]'::jsonb)) other
                WHERE other->>'fieldCode' = 'zoom_factor' AND f->>'fieldCode' = 'focal_length'
              ))
            )
          )
        )
        FROM jsonb_array_elements(a.param_slots_json->'methods') elem
      )
    ),
    updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE a.deleted = false AND a.tenant_id = 1 AND a.code = 'zoom'
  AND jsonb_typeof(a.param_slots_json->'methods') = 'array';

UPDATE ent_protocol_mapping_t1
SET custom_fields = jsonb_set(custom_fields, '{param_slot_mapping_json}',
      '[{"slot":"payload_index","path":"data.payload_index"}]'::jsonb),
    updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE deleted = false AND tenant_id = 1 AND code IN (
  'map-photo-dji-dock', 'map-photo-dji-pilot',
  'map-takephoto-dji-dock', 'map-takephoto-dji-pilot',
  'map-start-record-dji-dock', 'map-start-record-dji-pilot',
  'map-stop-record-dji-dock', 'map-stop-record-dji-pilot'
);

UPDATE ent_protocol_mapping_t1
SET custom_fields = jsonb_set(custom_fields, '{param_slot_mapping_json}',
      '[{"slot":"payload_index","path":"data.payload_index"},{"slot":"camera_type","path":"data.camera_type"},{"slot":"zoom_factor","path":"data.zoom_factor"}]'::jsonb),
    updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE deleted = false AND tenant_id = 1 AND code IN ('map-zoom-dji-dock', 'map-zoom-dji-pilot');

UPDATE ent_protocol_mapping_t1
SET custom_fields = jsonb_set(custom_fields, '{param_slot_mapping_json}',
      '[{"slot":"payload_index","path":"data.payload_index"},{"slot":"camera_type","path":"data.camera_type"},{"slot":"focus_x","path":"data.x"},{"slot":"focus_y","path":"data.y"}]'::jsonb),
    updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE deleted = false AND tenant_id = 1 AND code IN ('map-focus-dji-dock', 'map-focus-dji-pilot');

UPDATE ent_protocol_mapping_t1
SET custom_fields = jsonb_set(custom_fields, '{param_slot_mapping_json}',
      '[{"slot":"payload_index","path":"data.payload_index"},{"slot":"camera_mode","path":"data.camera_mode"}]'::jsonb),
    updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE deleted = false AND tenant_id = 1 AND code IN ('map-pano-dji-dock', 'map-pano-dji-pilot');

COMMIT;
