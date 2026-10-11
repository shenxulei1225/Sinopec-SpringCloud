-- 机器人对接：动作参数按来源分组写进 200102 / 200301。
-- 移动读选择位置上的点位和坐标。拍照读偏航角、俯仰角、焦距、张数；停靠点由上一条移动在组包时写入，不在拍照上再配一遍。
-- 不负责：给旋转云台另造一条报文。机器人没有单独的转云台指令，角度写在拍照上。
SET client_encoding TO 'UTF8';
SET search_path TO dynamicbusiness, public;

UPDATE ent_data_protocol_t1
SET custom_fields = jsonb_set(
      custom_fields,
      '{command_json}',
      command_json,
      true
    ),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND tenant_id = 1
  AND code = 'proto-robot-200301-photo'
  AND command_json <> '{}'::jsonb;

UPDATE ent_data_protocol_t1
SET custom_fields = jsonb_set(
      custom_fields,
      '{field_description_json,outbound}',
      $desc$[
        {"code":"sequence","path":"sequence","type":"integer","label":"动作序号","required":true,"description":"包内第几步，组包时写入"},
        {"code":"opcode","path":"opcode","type":"integer","label":"内层动作码","required":true,"description":"200301 拍照"},
        {"code":"request","path":"request","type":"object","label":"动作请求参数","required":true,"description":null},
        {"code":"pointId","path":"request.pointId","type":"string","label":"停靠点","required":false,"description":"上一条移动的点位。拍照动作上不再配一遍"},
        {"code":"lat","path":"request.lat","type":"string","label":"停靠点纬度","required":false,"description":"停靠点坐标，不是另一套拍摄目标"},
        {"code":"lng","path":"request.lng","type":"string","label":"停靠点经度","required":false,"description":"停靠点坐标，不是另一套拍摄目标"},
        {"code":"height","path":"request.height","type":"string","label":"停靠点地面高度","required":false,"description":"组包时不从移动抄地面高度"},
        {"code":"heading","path":"request.heading","type":"string","label":"朝向","required":false,"description":"不要求必填"},
        {"code":"angleLevel","path":"request.angleLevel","type":"string","label":"偏航角","required":true,"description":"每次到点拍照都要重新对准"},
        {"code":"angleVertical","path":"request.angleVertical","type":"string","label":"俯仰角","required":true,"description":"每次到点拍照都要重新对准"},
        {"code":"focuses","path":"request.focuses","type":"string","label":"焦距","required":true,"description":null},
        {"code":"number","path":"request.number","type":"string","label":"拍照张数","required":true,"description":null},
        {"code":"parking","path":"request.parking","type":"string","label":"是否驻停","required":false,"description":"不驻留可以空着"},
        {"code":"time","path":"request.time","type":"string","label":"驻停秒数","required":false,"description":"不驻留可以空着"}
      ]$desc$::jsonb,
      true
    ),
    field_description_json = jsonb_set(
      COALESCE(field_description_json, '{}'::jsonb),
      '{outbound}',
      $desc$[
        {"code":"sequence","path":"sequence","type":"integer","label":"动作序号","required":true,"description":"包内第几步，组包时写入"},
        {"code":"opcode","path":"opcode","type":"integer","label":"内层动作码","required":true,"description":"200301 拍照"},
        {"code":"request","path":"request","type":"object","label":"动作请求参数","required":true,"description":null},
        {"code":"pointId","path":"request.pointId","type":"string","label":"停靠点","required":false,"description":"上一条移动的点位。拍照动作上不再配一遍"},
        {"code":"lat","path":"request.lat","type":"string","label":"停靠点纬度","required":false,"description":"停靠点坐标，不是另一套拍摄目标"},
        {"code":"lng","path":"request.lng","type":"string","label":"停靠点经度","required":false,"description":"停靠点坐标，不是另一套拍摄目标"},
        {"code":"height","path":"request.height","type":"string","label":"停靠点地面高度","required":false,"description":"组包时不从移动抄地面高度"},
        {"code":"heading","path":"request.heading","type":"string","label":"朝向","required":false,"description":"不要求必填"},
        {"code":"angleLevel","path":"request.angleLevel","type":"string","label":"偏航角","required":true,"description":"每次到点拍照都要重新对准"},
        {"code":"angleVertical","path":"request.angleVertical","type":"string","label":"俯仰角","required":true,"description":"每次到点拍照都要重新对准"},
        {"code":"focuses","path":"request.focuses","type":"string","label":"焦距","required":true,"description":null},
        {"code":"number","path":"request.number","type":"string","label":"拍照张数","required":true,"description":null},
        {"code":"parking","path":"request.parking","type":"string","label":"是否驻停","required":false,"description":"不驻留可以空着"},
        {"code":"time","path":"request.time","type":"string","label":"驻停秒数","required":false,"description":"不驻留可以空着"}
      ]$desc$::jsonb,
      true
    ),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND tenant_id = 1
  AND code = 'proto-robot-200301-photo';

UPDATE ent_data_protocol_t1
SET custom_fields = jsonb_set(
      custom_fields,
      '{field_description_json,outbound}',
      $desc$[
        {"code":"sequence","path":"sequence","type":"integer","label":"动作序号","required":true,"description":null},
        {"code":"opcode","path":"opcode","type":"integer","label":"内层动作码","required":true,"description":"200102 移动"},
        {"code":"request","path":"request","type":"object","label":"动作请求参数","required":true,"description":null},
        {"code":"pointId","path":"request.pointId","type":"string","label":"点位","required":true,"description":"只有点位也能移动"},
        {"code":"lat","path":"request.lat","type":"string","label":"纬度","required":false,"description":null},
        {"code":"lng","path":"request.lng","type":"string","label":"经度","required":false,"description":null},
        {"code":"height","path":"request.height","type":"string","label":"地面高度","required":false,"description":"RTK 导航高度"},
        {"code":"heading","path":"request.heading","type":"string","label":"朝向","required":false,"description":"后补，不要求必填"}
      ]$desc$::jsonb,
      true
    ),
    command_json = custom_fields->'command_json',
    field_description_json = jsonb_set(
      COALESCE(field_description_json, '{}'::jsonb),
      '{outbound}',
      $desc$[
        {"code":"sequence","path":"sequence","type":"integer","label":"动作序号","required":true,"description":null},
        {"code":"opcode","path":"opcode","type":"integer","label":"内层动作码","required":true,"description":"200102 移动"},
        {"code":"request","path":"request","type":"object","label":"动作请求参数","required":true,"description":null},
        {"code":"pointId","path":"request.pointId","type":"string","label":"点位","required":true,"description":"只有点位也能移动"},
        {"code":"lat","path":"request.lat","type":"string","label":"纬度","required":false,"description":null},
        {"code":"lng","path":"request.lng","type":"string","label":"经度","required":false,"description":null},
        {"code":"height","path":"request.height","type":"string","label":"地面高度","required":false,"description":"RTK 导航高度"},
        {"code":"heading","path":"request.heading","type":"string","label":"朝向","required":false,"description":"后补，不要求必填"}
      ]$desc$::jsonb,
      true
    ),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND tenant_id = 1
  AND code = 'proto-robot-200102-move';

-- 选择位置：点位编号、坐标的经度纬度高程。朝向没有对应字段，不配。
UPDATE ent_protocol_mapping_t1
SET custom_fields = jsonb_set(
      custom_fields,
      '{param_slot_mapping_json}',
      $slots$[
        {"slot":"F-e49f76bdca3e4e1393e8e2f1cc0e7867","sourceKind":"ACTION_PARAM","sourcePath":"FLD-PNT-002","path":"request.pointId"},
        {"slot":"F-e49f76bdca3e4e1393e8e2f1cc0e7867","sourceKind":"ACTION_PARAM","sourcePath":"FLD-LOC-014.latitude","path":"request.lat"},
        {"slot":"F-e49f76bdca3e4e1393e8e2f1cc0e7867","sourceKind":"ACTION_PARAM","sourcePath":"FLD-LOC-014.longitude","path":"request.lng"},
        {"slot":"F-e49f76bdca3e4e1393e8e2f1cc0e7867","sourceKind":"ACTION_PARAM","sourcePath":"FLD-LOC-014.height","path":"request.height"}
      ]$slots$::jsonb,
      true
    ),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND tenant_id = 1
  AND code IN ('map-arrive-robot-ws', 'map-return-robot-ws');

-- 拍照恢复为可选用动作。机器人这一档的角度、焦距、张数由用户配置；不把 0 和 30 当成没填时的角度。
UPDATE ent_action_t1
SET deleted = false,
    param_slots_json = jsonb_set(
      param_slots_json,
      '{methods}',
      (
        SELECT jsonb_agg(rewritten.method ORDER BY rewritten.ordinal)
        FROM (
          SELECT method.ordinal,
                 CASE
                   WHEN method.value->>'executionMeans' = 'ROBOT'
                     THEN jsonb_set(method.value, '{fields}', $fields$[
                       {"fieldCode":"yaw","required":true,"valueSource":"ACTION_PARAM","defaultValue":null},
                       {"fieldCode":"pitch","required":true,"valueSource":"ACTION_PARAM","defaultValue":null},
                       {"fieldCode":"focal_length","required":true,"valueSource":"ACTION_PARAM","defaultValue":null},
                       {"fieldCode":"shot_count","required":true,"valueSource":"ACTION_PARAM","defaultValue":null},
                       {"fieldCode":"F-2b8fa8bda0b94663979c112af85325dc","required":false,"valueSource":"ACTION_PARAM","defaultValue":null},
                       {"fieldCode":"action_duration","required":false,"valueSource":"ACTION_PARAM","defaultValue":null,"durationUnit":"s"}
                     ]$fields$::jsonb)
                   ELSE method.value
                 END AS method
          FROM jsonb_array_elements(param_slots_json->'methods') WITH ORDINALITY AS method(value, ordinal)
        ) rewritten
      )
    ),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1
  AND code = 'act-robot-shoot';

UPDATE ent_protocol_mapping_t1
SET deleted = false,
    custom_fields = jsonb_set(
      custom_fields,
      '{param_slot_mapping_json}',
      $slots$[
        {"slot":"yaw","sourceKind":"ACTION_PARAM","path":"request.angleLevel"},
        {"slot":"pitch","sourceKind":"ACTION_PARAM","path":"request.angleVertical"},
        {"slot":"focal_length","sourceKind":"ACTION_PARAM","path":"request.focuses"},
        {"slot":"shot_count","sourceKind":"ACTION_PARAM","path":"request.number"},
        {"slot":"F-2b8fa8bda0b94663979c112af85325dc","sourceKind":"ACTION_PARAM","path":"request.parking"},
        {"slot":"action_duration","sourceKind":"ACTION_PARAM","path":"request.time"}
      ]$slots$::jsonb,
      true
    ),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1
  AND code = 'map-photo-robot-ws';

-- 停留观察已经带同一组拍照参数，各发各的一条，不并进拍照那一步。
UPDATE ent_protocol_mapping_t1
SET custom_fields = jsonb_set(
      custom_fields,
      '{param_slot_mapping_json}',
      $slots$[
        {"slot":"yaw","sourceKind":"ACTION_PARAM","path":"request.angleLevel"},
        {"slot":"pitch","sourceKind":"ACTION_PARAM","path":"request.angleVertical"},
        {"slot":"focal_length","sourceKind":"ACTION_PARAM","path":"request.focuses"},
        {"slot":"shot_count","sourceKind":"ACTION_PARAM","path":"request.number"},
        {"slot":"F-2b8fa8bda0b94663979c112af85325dc","sourceKind":"ACTION_PARAM","path":"request.parking"},
        {"slot":"action_duration","sourceKind":"ACTION_PARAM","path":"request.time"}
      ]$slots$::jsonb,
      true
    ),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND tenant_id = 1
  AND code = 'map-act-robot-hold-robot-ws';

UPDATE ent_action_t1
SET param_slots_json = jsonb_set(
      param_slots_json,
      '{methods}',
      (
        SELECT jsonb_agg(
          CASE
            WHEN method.value->>'executionMeans' = 'ROBOT'
              THEN jsonb_set(
                method.value,
                '{fields}',
                (
                  SELECT COALESCE(jsonb_agg(
                    field.value || jsonb_build_object('valueSource', 'ACTION_PARAM')
                    ORDER BY field.ordinal
                  ), '[]'::jsonb)
                  FROM jsonb_array_elements(method.value->'fields') WITH ORDINALITY AS field(value, ordinal)
                )
              )
            ELSE method.value
          END
          ORDER BY method.ordinal
        )
        FROM jsonb_array_elements(param_slots_json->'methods') WITH ORDINALITY AS method(value, ordinal)
      )
    ),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1
  AND deleted = false
  AND code = 'act-robot-hold';

-- 旋转云台对不上拍照必填的张数和焦距，单独下发组不出 200301。机器人方法里拿掉这一步，角度用拍照上的偏航角、俯仰角。
UPDATE ent_inspection_item_t1 item
SET step_tree_json = jsonb_set(
      item.step_tree_json,
      '{methods}'::text[],
      (
        SELECT jsonb_agg(rewritten.method ORDER BY rewritten.ordinal)
        FROM (
          SELECT method.ordinal,
                 CASE
                   WHEN COALESCE(method.value->>'methodKey', method.value->>'executionMeans') = 'ROBOT'
                     THEN jsonb_set(
                       method.value,
                       CASE
                         WHEN method.value ? 'stepTree' THEN '{stepTree}'::text[]
                         ELSE '{actionTree}'::text[]
                       END,
                       (
                         SELECT COALESCE(jsonb_agg(node.value ORDER BY node.ordinal), '[]'::jsonb)
                         FROM jsonb_array_elements(
                           COALESCE(method.value->'stepTree', method.value->'actionTree')
                         ) WITH ORDINALITY AS node(value, ordinal)
                         WHERE node.value->>'actionId' IS DISTINCT FROM 'gimbalRotate'
                       )
                     )
                   ELSE method.value
                 END AS method
          FROM jsonb_array_elements(item.step_tree_json->'methods') WITH ORDINALITY AS method(value, ordinal)
        ) rewritten
      )
    ),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE item.deleted = false
  AND item.tenant_id = 1
  AND jsonb_typeof(item.step_tree_json) = 'object'
  AND item.step_tree_json::text LIKE '%gimbalRotate%';
