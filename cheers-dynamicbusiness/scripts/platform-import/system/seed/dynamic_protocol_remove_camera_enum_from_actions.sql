-- 相机的负载索引编号不是动作参数。
-- 负责：从动作和对照里去掉 payload_index。报文空包里的这一列保留，发出时由挂载的相机填写。
-- 不负责：改东古仍在用的偏航、俯仰、焦距；不改变焦倍数、相机类型、对焦点。

SET search_path TO dynamicbusiness;

BEGIN;

UPDATE ent_protocol_mapping_t1 m
SET custom_fields = jsonb_set(
      m.custom_fields,
      '{param_slot_mapping_json}',
      COALESCE(
        (
          SELECT jsonb_agg(item)
          FROM jsonb_array_elements(COALESCE(m.custom_fields->'param_slot_mapping_json', '[]'::jsonb)) item
          WHERE item->>'slot' <> 'payload_index'
            AND item->>'path' <> 'data.payload_index'
        ),
        '[]'::jsonb
      )
    ),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE m.deleted = false
  AND m.tenant_id = 1
  AND m.custom_fields->>'mapped_protocol_version' IN ('dji-dock-1.16.1', 'dji-pilot-1.16.1')
  AND EXISTS (
    SELECT 1
    FROM jsonb_array_elements(COALESCE(m.custom_fields->'param_slot_mapping_json', '[]'::jsonb)) item
    WHERE item->>'slot' = 'payload_index' OR item->>'path' = 'data.payload_index'
  );

UPDATE ent_action_t1 a
SET param_slots_json = jsonb_set(
      a.param_slots_json,
      '{methods}',
      (
        SELECT jsonb_agg(
          jsonb_set(
            elem,
            '{fields}',
            COALESCE(
              (
                SELECT jsonb_agg(f)
                FROM jsonb_array_elements(COALESCE(elem->'fields', '[]'::jsonb)) f
                WHERE f->>'fieldCode' <> 'payload_index'
              ),
              '[]'::jsonb
            )
          )
        )
        FROM jsonb_array_elements(a.param_slots_json->'methods') elem
      )
    ),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE a.deleted = false
  AND a.tenant_id = 1
  AND jsonb_typeof(a.param_slots_json->'methods') = 'array'
  AND EXISTS (
    SELECT 1
    FROM jsonb_array_elements(a.param_slots_json->'methods') elem,
         jsonb_array_elements(COALESCE(elem->'fields', '[]'::jsonb)) f
    WHERE f->>'fieldCode' = 'payload_index'
  );

COMMIT;
