-- 航线文件动作的参数按大疆 WPML 共用元素 actionActuatorFuncParam（2026-03-19）核对。
-- 动作参数槽的 fieldCode 与指令空包字段同名。对照把槽填进同名路径。
-- 排程用的动作耗时留下，不写进航线文件。
-- 不负责：开始拍照、录像、变焦、点对焦、喊话器。那些仍是单独报文，不改成航线动作。
-- 精准复拍 accurateShoot 官网已停止维护，不新增。
SET client_encoding TO 'UTF8';
SET search_path TO dynamicbusiness, public;

DROP TABLE IF EXISTS wayline_param;
CREATE TEMP TABLE wayline_param (
  action_code text NOT NULL,
  field_code text NOT NULL,
  field_name text NOT NULL,
  field_type text NOT NULL,
  unit text,
  description text NOT NULL,
  options text,
  sort int NOT NULL
);

INSERT INTO wayline_param (action_code, field_code, field_name, field_type, unit, description, options, sort) VALUES
('hover', 'hoverTime', '飞行器悬停等待时间', 'NUMBER', 's', '大于 0。单位秒。', NULL, 1),

('customDirName', 'payloadPositionIndex', '负载挂载位置', 'NUMBER', NULL, '相机的负载索引编号 type-subtype-gimbalindex 中的 gimbalindex。主云台是 0。', NULL, 1),
('customDirName', 'directoryName', '新文件夹的名称', 'STRING', NULL, '新文件夹的名称。', NULL, 2),

('gimbalEvenlyRotate', 'gimbalPitchRotateAngle', '云台俯仰角', 'NUMBER', 'deg', '不同云台可转动范围不同。触发必须是航段间 betweenAdjacentPoints。', NULL, 1),
('gimbalEvenlyRotate', 'payloadPositionIndex', '负载挂载位置', 'NUMBER', NULL, '相机的负载索引编号 type-subtype-gimbalindex 中的 gimbalindex。主云台是 0。', NULL, 2),

('rotateYaw', 'aircraftHeading', '飞行器目标偏航角', 'NUMBER', 'deg', '相对地理北，-180 到 180。0 为正北，90 为正东，-90 为正西，-180 或 180 为正南。', NULL, 1),
('rotateYaw', 'aircraftPathMode', '飞行器偏航角转动模式', 'ENUM', NULL, '顺时针或逆时针。官网该元素支持 M300 RTK、M350 RTK。', '[{"label":"顺时针","value":"clockwise"},{"label":"逆时针","value":"counterClockwise"}]', 2),

('gimbalRotate', 'payloadPositionIndex', '负载挂载位置', 'NUMBER', NULL, '相机的负载索引编号 type-subtype-gimbalindex 中的 gimbalindex。主云台是 0。', NULL, 1),
('gimbalRotate', 'gimbalHeadingYawBase', '云台偏航角转动坐标系', 'ENUM', NULL, 'north：相对地理北。', '[{"label":"相对地理北","value":"north"}]', 2),
('gimbalRotate', 'gimbalRotateMode', '云台转动模式', 'ENUM', NULL, 'absoluteAngle：绝对角度，相对于正北方的角度。', '[{"label":"绝对角度","value":"absoluteAngle"}]', 3),
('gimbalRotate', 'gimbalPitchRotateEnable', '是否转动云台俯仰', 'ENUM', NULL, '不转动或转动云台俯仰。', '[{"label":"不转动","value":"0"},{"label":"转动","value":"1"}]', 4),
('gimbalRotate', 'gimbalPitchRotateAngle', '云台俯仰角', 'NUMBER', 'deg', '不同云台可转动范围不同。', NULL, 5),
('gimbalRotate', 'gimbalRollRotateEnable', '是否转动云台横滚', 'ENUM', NULL, '不转动或转动云台横滚。', '[{"label":"不转动","value":"0"},{"label":"转动","value":"1"}]', 6),
('gimbalRotate', 'gimbalRollRotateAngle', '云台横滚角', 'NUMBER', 'deg', '不同云台可转动范围不同。', NULL, 7),
('gimbalRotate', 'gimbalYawRotateEnable', '是否转动云台偏航', 'ENUM', NULL, '不转动或转动云台偏航。', '[{"label":"不转动","value":"0"},{"label":"转动","value":"1"}]', 8),
('gimbalRotate', 'gimbalYawRotateAngle', '云台偏航角', 'NUMBER', 'deg', '不同云台可转动范围不同。', NULL, 9),
('gimbalRotate', 'gimbalRotateTimeEnable', '是否设置云台转动用时', 'ENUM', NULL, '不设置或设置云台完成转动的用时。', '[{"label":"不设置","value":"0"},{"label":"设置","value":"1"}]', 10),
('gimbalRotate', 'gimbalRotateTime', '云台完成转动用时', 'NUMBER', 's', '云台完成转动用时，单位秒。', NULL, 11),

('panoShot', 'payloadPositionIndex', '负载挂载位置', 'NUMBER', NULL, '相机的负载索引编号 type-subtype-gimbalindex 中的 gimbalindex。主云台是 0。', NULL, 1),
('panoShot', 'payloadLensIndex', '拍摄照片存储类型', 'STRING', NULL, 'zoom、wide、ir、narrow_band、visable。多个镜头用英文逗号。官网可见光写法是 visable。', NULL, 2),
('panoShot', 'useGlobalPayloadLensIndex', '是否使用全局存储类型', 'ENUM', NULL, '0 不使用全局设置，1 使用全局设置。', '[{"label":"不使用全局设置","value":"0"},{"label":"使用全局设置","value":"1"}]', 3),
('panoShot', 'panoShotSubMode', '全景拍照模式', 'ENUM', NULL, 'panoShot_360：全景模式。', '[{"label":"全景模式","value":"panoShot_360"}]', 4),

('recordPointCloud', 'payloadPositionIndex', '负载挂载位置', 'NUMBER', NULL, '相机的负载索引编号 type-subtype-gimbalindex 中的 gimbalindex。主云台是 0。支持机型 M300 RTK、M350 RTK。', NULL, 1),
('recordPointCloud', 'recordPointCloudOperate', '点云操作', 'ENUM', NULL, 'startRecord 开始，pauseRecord 暂停，resumeRecord 继续，stopRecord 结束。', '[{"label":"开始点云录制","value":"startRecord"},{"label":"暂停点云录制","value":"pauseRecord"},{"label":"继续点云录制","value":"resumeRecord"},{"label":"结束点云录制","value":"stopRecord"}]', 2),

('searchlight', 'payloadPositionIndex', '负载挂载位置', 'NUMBER', NULL, '0 是飞行器 1 号挂载位置，对应主云台。支持机型 M4D、M4TD。', NULL, 1),
('searchlight', 'searchlightOperateType', '探照灯操作类型', 'ENUM', NULL, '0 关灯，1 照明，2 爆闪。', '[{"label":"关灯","value":"0"},{"label":"照明","value":"1"},{"label":"爆闪","value":"2"}]', 2),
('searchlight', 'searchlightBrightness', '亮度', 'NUMBER', '%', '0 到 100。', NULL, 3),

('orientedShoot', 'gimbalPitchRotateAngle', '云台俯仰角', 'NUMBER', 'deg', 'M30/M30T 为 -120 到 45。M3E/M3T 为 -90 到 35。M3D/M3TD、M4D/M4TD、M4E/M4T 为 -90 到 30。', NULL, 1),
('orientedShoot', 'gimbalYawRotateAngle', '云台偏航角', 'NUMBER', 'deg', '相对地理北，-180 到 180。M3E/M3T、M3D/M3TD、M4D/M4TD、M4E/M4T 须与飞行器目标偏航角一致。', NULL, 2),
('orientedShoot', 'focusX', '目标选中框中心水平坐标', 'NUMBER', 'px', '照片左上角为原点，水平方向为 X。范围 (0, 960)。', NULL, 3),
('orientedShoot', 'focusY', '目标选中框中心竖直坐标', 'NUMBER', 'px', '照片左上角为原点，竖直方向为 Y。范围 (0, 720)。', NULL, 4),
('orientedShoot', 'focusRegionWidth', '目标选中框宽', 'NUMBER', 'px', '范围 (0, 960)。', NULL, 5),
('orientedShoot', 'focusRegionHeight', '目标选中框高', 'NUMBER', 'px', '范围 (0, 720)。', NULL, 6),
('orientedShoot', 'focalLength', '变焦焦距', 'NUMBER', 'mm', '大于 0。单位毫米。', NULL, 7),
('orientedShoot', 'aircraftHeading', '飞行器目标偏航角', 'NUMBER', 'deg', '相对地理北，-180 到 180。0 为正北，90 为正东，-90 为正西，-180 或 180 为正南。', NULL, 8),
('orientedShoot', 'accurateFrameValid', '是否框选精准复拍目标', 'ENUM', NULL, '1 已框选目标物，复拍时飞机会寻找目标。0 未框选，只重复姿态。', '[{"label":"未框选目标物","value":"0"},{"label":"已框选目标物","value":"1"}]', 9),
('orientedShoot', 'payloadPositionIndex', '负载挂载位置', 'NUMBER', NULL, '相机的负载索引编号 type-subtype-gimbalindex 中的 gimbalindex。主云台是 0。', NULL, 10),
('orientedShoot', 'payloadLensIndex', '拍摄照片存储类型', 'STRING', NULL, 'zoom、wide、ir、visable。多个镜头用英文逗号。官网可见光写法是 visable。', NULL, 11),
('orientedShoot', 'useGlobalPayloadLensIndex', '是否使用全局存储类型', 'ENUM', NULL, '0 不使用全局设置，1 使用全局设置。', '[{"label":"不使用全局设置","value":"0"},{"label":"使用全局设置","value":"1"}]', 12),
('orientedShoot', 'targetAngle', '目标框角度', 'NUMBER', 'deg', '0 到 360。以 Y 轴为基准顺时针旋转。', NULL, 13),
('orientedShoot', 'actionUUID', '动作唯一标识', 'STRING', NULL, '拍照时写入照片文件，用来关联动作和照片。', NULL, 14),
('orientedShoot', 'imageWidth', '照片宽度', 'NUMBER', 'px', '官网示例为 960。', NULL, 15),
('orientedShoot', 'imageHeight', '照片高度', 'NUMBER', 'px', '官网示例为 720。', NULL, 16),
('orientedShoot', 'AFPos', '自动对焦电机位置', 'NUMBER', NULL, '自动对焦电机位置。', NULL, 17),
('orientedShoot', 'gimbalPort', '云台端口号', 'NUMBER', NULL, '拍摄照片的相机安装位置。M30/M30T 固定为 0。', NULL, 18),
('orientedShoot', 'orientedCameraType', '相机类型', 'NUMBER', NULL, '52 M30 双光，53 M30T 三光，66 Mavic 3E，67 Mavic 3T，80 Matrice 3D，81 Matrice 3TD。', NULL, 19),
('orientedShoot', 'orientedFilePath', '照片文件路径', 'STRING', NULL, '照片文件名。', NULL, 20),
('orientedShoot', 'orientedFileMD5', '照片文件校验值', 'STRING', NULL, '照片文件校验值。', NULL, 21),
('orientedShoot', 'orientedFileSize', '照片文件大小', 'NUMBER', NULL, '照片文件实际大小，单位字节。', NULL, 22),
('orientedShoot', 'orientedFileSuffix', '照片文件后缀', 'STRING', NULL, '生成媒体文件时额外附带的后缀。', NULL, 23),
('orientedShoot', 'orientedCameraApertue', '光圈大小', 'NUMBER', NULL, '真实光圈乘以 100。官网元素名是 orientedCameraApertue。', NULL, 24),
('orientedShoot', 'orientedCameraLuminance', '环境亮度', 'NUMBER', NULL, '环境亮度。', NULL, 25),
('orientedShoot', 'orientedCameraShutterTime', '快门时间', 'NUMBER', 's', '快门时间，单位秒。', NULL, 26),
('orientedShoot', 'orientedCameraISO', '感光度', 'NUMBER', NULL, '感光度。', NULL, 27),
('orientedShoot', 'orientedPhotoMode', '拍照模式', 'ENUM', NULL, 'normalPhoto 普通拍照，lowLightSmartShooting 智能低光拍照。', '[{"label":"普通拍照","value":"normalPhoto"},{"label":"智能低光拍照","value":"lowLightSmartShooting"}]', 28);

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
)
SELECT DISTINCT ON (field_code)
  field_code, field_name, field_type, unit, description, 'LIBRARY', 1, 1,
  'NONE', CASE WHEN options IS NULL THEN NULL ELSE options::jsonb END, NULL, field_code, 1, 'seed'
FROM wayline_param
ORDER BY field_code, sort
ON CONFLICT (code, tenant_id) WHERE deleted = false DO UPDATE
SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  source = 'LIBRARY',
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

UPDATE ent_action_t1 a
SET
  param_slots_json = jsonb_set(
    a.param_slots_json,
    '{methods,0,fields}',
    built.fields || COALESCE((
      SELECT jsonb_agg(slot)
      FROM jsonb_array_elements(a.param_slots_json->'methods'->0->'fields') slot
      WHERE slot->>'fieldCode' = 'action_duration'
    ), '[]'::jsonb)
  ),
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
FROM (
  SELECT action_code,
         jsonb_agg(
           jsonb_build_object('required', true, 'fieldCode', field_code, 'defaultValue', NULL)
           ORDER BY sort
         ) AS fields
  FROM wayline_param
  GROUP BY action_code
) built
WHERE a.deleted = false AND a.tenant_id = 1 AND a.code = built.action_code;

INSERT INTO ent_data_protocol_t1 (
  tenant_id, entity_type_code, model_id, name, code, status, custom_fields, command_direction, creator, deleted
)
SELECT
  1, 'data_protocol', m.id, a.name,
  'proto-' || v.book || '-wpml-' || a.code,
  1,
  jsonb_build_object(
    'opcode', 0,
    'command_direction', 'downlink',
    'protocol_version', jsonb_build_array(v.release),
    'communication_protocol', 'wayline',
    'command_json', jsonb_build_object(
      'outbound', jsonb_build_object('actionActuatorFunc', built.action_code, 'params', built.outbound)
    ),
    'field_description_json', jsonb_build_object('outbound', built.descriptions)
  ),
  'downlink', 'seed', false
FROM (VALUES ('dji-dock', 'dji-dock-1.16.1', 'data_protocol_dji_dock_wayline'), ('dji-pilot', 'dji-pilot-1.16.1', 'data_protocol_dji_pilot_wayline')) AS v(book, release, model_code)
JOIN dynamic_model m ON m.deleted = false AND m.tenant_id = 1 AND m.code = v.model_code
JOIN (
  SELECT action_code,
         jsonb_object_agg(field_code, ''::text) AS outbound,
         jsonb_agg(
           jsonb_strip_nulls(jsonb_build_object(
             'code', field_code,
             'path', 'params.' || field_code,
             'type', field_type,
             'label', field_name,
             'required', true,
             'description', description,
             'options', CASE WHEN options IS NULL OR btrim(options) = '' THEN NULL ELSE options::jsonb END
           ))
           ORDER BY sort
         ) AS descriptions
  FROM wayline_param
  GROUP BY action_code
) built ON true
JOIN ent_action_t1 a ON a.deleted = false AND a.tenant_id = 1 AND a.code = built.action_code
WHERE NOT EXISTS (
  SELECT 1 FROM ent_data_protocol_t1 e
  WHERE e.deleted = false AND e.tenant_id = 1 AND e.code = 'proto-' || v.book || '-wpml-' || a.code
);

UPDATE ent_data_protocol_t1 e
SET
  name = a.name,
  model_id = m.id,
  command_direction = 'downlink',
  custom_fields = jsonb_build_object(
    'opcode', 0,
    'command_direction', 'downlink',
    'protocol_version', jsonb_build_array(v.release),
    'communication_protocol', 'wayline',
    'command_json', jsonb_build_object(
      'outbound', jsonb_build_object('actionActuatorFunc', built.action_code, 'params', built.outbound)
    ),
    'field_description_json', jsonb_build_object('outbound', built.descriptions)
  ),
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
FROM (VALUES ('dji-dock', 'dji-dock-1.16.1', 'data_protocol_dji_dock_wayline'), ('dji-pilot', 'dji-pilot-1.16.1', 'data_protocol_dji_pilot_wayline')) AS v(book, release, model_code)
JOIN dynamic_model m ON m.deleted = false AND m.tenant_id = 1 AND m.code = v.model_code
JOIN (
  SELECT action_code,
         jsonb_object_agg(field_code, ''::text) AS outbound,
         jsonb_agg(
           jsonb_strip_nulls(jsonb_build_object(
             'code', field_code,
             'path', 'params.' || field_code,
             'type', field_type,
             'label', field_name,
             'required', true,
             'description', description,
             'options', CASE WHEN options IS NULL OR btrim(options) = '' THEN NULL ELSE options::jsonb END
           ))
           ORDER BY sort
         ) AS descriptions
  FROM wayline_param
  GROUP BY action_code
) built ON true
JOIN ent_action_t1 a ON a.deleted = false AND a.tenant_id = 1 AND a.code = built.action_code
WHERE e.deleted = false AND e.tenant_id = 1
  AND e.code = 'proto-' || v.book || '-wpml-' || a.code;

INSERT INTO dynamic_entity_category_relation_t1 (
  tenant_id, category_id, entity_type_code, entity_id, domain, sort, creator, deleted
)
SELECT 1, c.id, 'data_protocol', e.id, NULL, 0, 'seed', false
FROM ent_data_protocol_t1 e
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'cat-protocol-dji-uav'
WHERE e.deleted = false AND e.tenant_id = 1 AND e.code LIKE 'proto-dji-%-wpml-%'
AND NOT EXISTS (
  SELECT 1 FROM dynamic_entity_category_relation_t1 r
  WHERE r.deleted = false AND r.tenant_id = 1 AND r.entity_type_code = 'data_protocol'
    AND r.entity_id = e.id AND r.category_id = c.id
);

INSERT INTO dynamic_entity_field_index_t1
  (entity_id, model_id, field_code, value_string, value_number, creator, tenant_id, deleted)
SELECT e.id, e.model_id, 'opcode', NULL, 0, 'seed', 1, false
FROM ent_data_protocol_t1 e
WHERE e.deleted = false AND e.tenant_id = 1 AND e.code LIKE 'proto-dji-%-wpml-%'
AND NOT EXISTS (
  SELECT 1 FROM dynamic_entity_field_index_t1 idx
  WHERE idx.deleted = false AND idx.tenant_id = 1 AND idx.entity_id = e.id AND idx.field_code = 'opcode'
);

INSERT INTO dynamic_entity_field_index_t1
  (entity_id, model_id, field_code, value_string, value_number, creator, tenant_id, deleted)
SELECT e.id, e.model_id, 'protocol_version', e.custom_fields->'protocol_version'->>0, NULL, 'seed', 1, false
FROM ent_data_protocol_t1 e
WHERE e.deleted = false AND e.tenant_id = 1 AND e.code LIKE 'proto-dji-%-wpml-%'
AND NOT EXISTS (
  SELECT 1 FROM dynamic_entity_field_index_t1 idx
  WHERE idx.deleted = false AND idx.tenant_id = 1 AND idx.entity_id = e.id AND idx.field_code = 'protocol_version'
);

INSERT INTO ent_protocol_mapping_t1 (
  tenant_id, entity_type_code, model_id, name, code, status, custom_fields, creator, deleted
)
SELECT
  1, 'protocol_mapping', m.id,
  a.name || ' · ' || v.label,
  'map-' || v.book || '-wpml-' || a.code,
  1,
  jsonb_build_object(
    'action_ref', jsonb_build_object('id', a.id, 'entityTypeCode', 'action'),
    'mapped_protocol_version', v.release,
    'protocol_instruction_ref', jsonb_build_object('id', p.id, 'entityTypeCode', 'data_protocol'),
    'param_slot_mapping_json', built.slots
  ),
  'seed', false
FROM (VALUES
  ('dji-dock', 'dji-dock-1.16.1', '大疆机场 1.16.1'),
  ('dji-pilot', 'dji-pilot-1.16.1', '大疆 Pilot 1.16.1')
) AS v(book, release, label)
JOIN dynamic_model m ON m.deleted = false AND m.tenant_id = 1 AND m.code = 'protocol_mapping_standard'
JOIN (
  SELECT action_code,
         jsonb_agg(jsonb_build_object('slot', field_code, 'path', 'params.' || field_code) ORDER BY sort) AS slots
  FROM wayline_param
  GROUP BY action_code
) built ON true
JOIN ent_action_t1 a ON a.deleted = false AND a.tenant_id = 1 AND a.code = built.action_code
JOIN ent_data_protocol_t1 p
  ON p.deleted = false AND p.tenant_id = 1 AND p.code = 'proto-' || v.book || '-wpml-' || a.code
WHERE NOT EXISTS (
  SELECT 1 FROM ent_protocol_mapping_t1 e
  WHERE e.deleted = false AND e.tenant_id = 1 AND e.code = 'map-' || v.book || '-wpml-' || a.code
);

UPDATE ent_protocol_mapping_t1 e
SET
  name = a.name || ' · ' || v.label,
  custom_fields = jsonb_build_object(
    'action_ref', jsonb_build_object('id', a.id, 'entityTypeCode', 'action'),
    'mapped_protocol_version', v.release,
    'protocol_instruction_ref', jsonb_build_object('id', p.id, 'entityTypeCode', 'data_protocol'),
    'param_slot_mapping_json', built.slots
  ),
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
FROM (VALUES
  ('dji-dock', 'dji-dock-1.16.1', '大疆机场 1.16.1'),
  ('dji-pilot', 'dji-pilot-1.16.1', '大疆 Pilot 1.16.1')
) AS v(book, release, label)
JOIN (
  SELECT action_code,
         jsonb_agg(jsonb_build_object('slot', field_code, 'path', 'params.' || field_code) ORDER BY sort) AS slots
  FROM wayline_param
  GROUP BY action_code
) built ON true
JOIN ent_action_t1 a ON a.deleted = false AND a.tenant_id = 1 AND a.code = built.action_code
JOIN ent_data_protocol_t1 p
  ON p.deleted = false AND p.tenant_id = 1 AND p.code = 'proto-' || v.book || '-wpml-' || a.code
WHERE e.deleted = false AND e.tenant_id = 1
  AND e.code = 'map-' || v.book || '-wpml-' || a.code;

INSERT INTO dynamic_entity_category_relation_t1 (
  tenant_id, category_id, entity_type_code, entity_id, domain, sort, creator, deleted
)
SELECT 1, c.id, 'protocol_mapping', e.id, NULL, 0, 'seed', false
FROM ent_protocol_mapping_t1 e
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'protocol_mapping_root'
WHERE e.deleted = false AND e.tenant_id = 1 AND e.code LIKE 'map-dji-%-wpml-%'
AND NOT EXISTS (
  SELECT 1 FROM dynamic_entity_category_relation_t1 r
  WHERE r.tenant_id = 1 AND r.entity_type_code = 'protocol_mapping'
    AND r.entity_id = e.id AND r.category_id = c.id
);

INSERT INTO dynamic_entity_field_index_t1
  (entity_id, model_id, field_code, value_string, value_number, creator, tenant_id, deleted)
SELECT e.id, e.model_id, 'action_ref', NULL, (e.custom_fields->'action_ref'->>'id')::bigint, 'seed', 1, false
FROM ent_protocol_mapping_t1 e
WHERE e.deleted = false AND e.tenant_id = 1 AND e.code LIKE 'map-dji-%-wpml-%'
AND NOT EXISTS (
  SELECT 1 FROM dynamic_entity_field_index_t1 idx
  WHERE idx.deleted = false AND idx.tenant_id = 1 AND idx.entity_id = e.id AND idx.field_code = 'action_ref'
);

INSERT INTO dynamic_entity_field_index_t1
  (entity_id, model_id, field_code, value_string, value_number, creator, tenant_id, deleted)
SELECT e.id, e.model_id, 'mapped_protocol_version', e.custom_fields->>'mapped_protocol_version', NULL, 'seed', 1, false
FROM ent_protocol_mapping_t1 e
WHERE e.deleted = false AND e.tenant_id = 1 AND e.code LIKE 'map-dji-%-wpml-%'
AND NOT EXISTS (
  SELECT 1 FROM dynamic_entity_field_index_t1 idx
  WHERE idx.deleted = false AND idx.tenant_id = 1 AND idx.entity_id = e.id AND idx.field_code = 'mapped_protocol_version'
);

INSERT INTO dynamic_entity_field_index_t1
  (entity_id, model_id, field_code, value_string, value_number, creator, tenant_id, deleted)
SELECT e.id, e.model_id, 'protocol_instruction_ref', NULL, (e.custom_fields->'protocol_instruction_ref'->>'id')::bigint, 'seed', 1, false
FROM ent_protocol_mapping_t1 e
WHERE e.deleted = false AND e.tenant_id = 1 AND e.code LIKE 'map-dji-%-wpml-%'
AND NOT EXISTS (
  SELECT 1 FROM dynamic_entity_field_index_t1 idx
  WHERE idx.deleted = false AND idx.tenant_id = 1     AND idx.entity_id = e.id AND idx.field_code = 'protocol_instruction_ref'
);

DROP TABLE IF EXISTS wayline_param;
