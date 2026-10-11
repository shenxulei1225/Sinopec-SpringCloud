-- 旋转云台的动作参数不再单列负载挂载位置。
-- 转哪台摄像机、转到哪个角度仍留在动作上。挂载序号在生成步骤时从选中摄像机的编号最后一段写入。
-- 不负责：改协议空包里的同名字段；改其它动作；给没选摄像机的步骤编一个序号。
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
  AND a.code = 'gimbalRotate'
  AND a.param_slots_json::text LIKE '%payloadPositionIndex%';

COMMIT;
