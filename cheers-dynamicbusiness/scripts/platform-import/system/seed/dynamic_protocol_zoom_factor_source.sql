-- 机场变焦对照里，变焦倍数的分组跟动作不一致，打开会显示来源不可用。
-- 动作上这一项是界面操作。只改这一格的分组，不改摄像机和相机类型。
SET client_encoding TO 'UTF8';
SET search_path TO dynamicbusiness, public;

UPDATE ent_protocol_mapping_t1
SET custom_fields = jsonb_set(
      custom_fields,
      '{param_slot_mapping_json}',
      (
        SELECT jsonb_agg(
          CASE
            WHEN item->>'slot' = 'zoom_factor'
              THEN jsonb_set(item, '{sourceKind}', '"INTERACTION"'::jsonb)
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
  AND code = 'map-zoom-dji-dock';
