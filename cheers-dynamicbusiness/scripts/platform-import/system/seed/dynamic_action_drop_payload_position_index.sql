-- 动作参数只保留完成这一动作要定义的项。
-- 负载挂载位置属于协议报文，随执行设备和摄像机确定，不写进各型号的动作参数。
-- 不负责：改协议空包；改用户要填的角度、摄像机、焦距；给没选摄像机的步骤编序号。
SET client_encoding TO 'UTF8';
SET search_path TO dynamicbusiness, public;

BEGIN;

UPDATE ent_action_t1 a
SET param_slots_json = jsonb_set(
      a.param_slots_json,
      '{methods}',
      (
        SELECT jsonb_agg(
          jsonb_set(
            method.value,
            '{fields}',
            COALESCE(
              (
                SELECT jsonb_agg(field.value ORDER BY field.ordinal)
                FROM jsonb_array_elements(COALESCE(method.value->'fields', '[]'::jsonb))
                  WITH ORDINALITY AS field(value, ordinal)
                WHERE field.value->>'fieldCode' IS DISTINCT FROM 'payloadPositionIndex'
              ),
              '[]'::jsonb
            )
          )
          ORDER BY method.ordinal
        )
        FROM jsonb_array_elements(a.param_slots_json->'methods')
          WITH ORDINALITY AS method(value, ordinal)
      )
    ),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE a.deleted = false
  AND a.tenant_id = 1
  AND jsonb_typeof(a.param_slots_json->'methods') = 'array'
  AND a.param_slots_json::text LIKE '%payloadPositionIndex%';

COMMIT;
