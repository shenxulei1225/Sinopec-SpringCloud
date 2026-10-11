-- 定向拍照里，配置时取景生成的云台角度、焦距和目标框归用户配置。
-- 光圈、环境亮度、快门时间、感光度也归用户配置，记在这台设备的检查项上。
-- 照片路径、大小、校验保持资源文件，不在这里改。
SET client_encoding TO 'UTF8';
SET search_path TO dynamicbusiness, public;

UPDATE ent_action_t1
SET param_slots_json = jsonb_set(
      param_slots_json,
      '{methods}',
      (
        SELECT jsonb_agg(
          jsonb_set(
            method,
            '{fields}',
            (
              SELECT jsonb_agg(
                CASE
                  WHEN item->>'fieldCode' IN (
                    'gimbalPitchRotateAngle', 'gimbalYawRotateAngle', 'focalLength',
                    'focusX', 'focusY', 'focusRegionWidth', 'focusRegionHeight', 'targetAngle',
                    'orientedCameraApertue', 'orientedCameraLuminance',
                    'orientedCameraShutterTime', 'orientedCameraISO'
                  )
                    THEN jsonb_set(item, '{valueSource}', '"ACTION_PARAM"'::jsonb)
                  ELSE item
                END
              )
              FROM jsonb_array_elements(method->'fields') item
            )
          )
        )
        FROM jsonb_array_elements(param_slots_json->'methods') method
      )
    ),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND tenant_id = 1
  AND code = 'orientedShoot';

UPDATE ent_protocol_mapping_t1
SET custom_fields = jsonb_set(
      custom_fields,
      '{param_slot_mapping_json}',
      (
        SELECT jsonb_agg(
          CASE
            WHEN item->>'slot' IN (
              'gimbalPitchRotateAngle', 'gimbalYawRotateAngle', 'focalLength',
              'focusX', 'focusY', 'focusRegionWidth', 'focusRegionHeight', 'targetAngle',
              'orientedCameraApertue', 'orientedCameraLuminance',
              'orientedCameraShutterTime', 'orientedCameraISO'
            )
              THEN jsonb_set(item, '{sourceKind}', '"ACTION_PARAM"'::jsonb)
            ELSE item
          END
        )
        FROM jsonb_array_elements(custom_fields->'param_slot_mapping_json') item
      )
    ),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND tenant_id = 1
  AND code IN ('map-dji-dock-wpml-orientedShoot', 'map-dji-pilot-wpml-orientedShoot');
