-- ============================================================================
-- 大疆航线动作库收敛
-- 1. 协议必需字段继续留在动作参数定义和协议对照中，但标明取值来源；
--    只有 ACTION_PARAM（动作参数）在普通检查参数表单中出现。
-- 2. 点云录制、探照灯按用户实际执行的步骤拆成原子动作，操作枚举由动作自身固定。
-- 3. 不负责实时视频框选（camera_frame_zoom/camera_aim/camera_look_at），那些不是巡检航线步骤。
-- 字段说明的类型和枚举取值跟字段库一致，不写成文本。
-- ============================================================================

SET client_encoding TO 'UTF8';
SET search_path TO dynamicbusiness, public;

-- 旧动作当前没有被检查项、任务或执行快照引用。新动作使用稳定业务编码，
-- WPML 动作类型由协议指令决定，不把协议名当成用户动作名。
INSERT INTO ent_action_t1 (
  tenant_id, entity_type_code, model_id, name, code, status,
  execution_means, param_slots_json, child_action_ids_json, is_composite,
  creator, deleted
)
SELECT
  1, 'action', m.id, v.name, v.code, 1,
  jsonb_build_array('UAV'), '{"version":2,"methods":[]}'::jsonb, '[]'::jsonb, false,
  'seed', false
FROM (VALUES
  ('recordPointCloudStart',  '开始点云录制'),
  ('recordPointCloudPause',  '暂停点云录制'),
  ('recordPointCloudResume', '继续点云录制'),
  ('recordPointCloudStop',   '停止点云录制'),
  ('searchlightOn',          '开启探照灯'),
  ('searchlightStrobe',      '开启探照灯爆闪'),
  ('searchlightOff',         '关闭探照灯')
) AS v(code, name)
JOIN dynamic_model m
  ON m.deleted = false AND m.tenant_id = 1 AND m.code = 'action'
WHERE NOT EXISTS (
  SELECT 1 FROM ent_action_t1 a
  WHERE a.deleted = false AND a.tenant_id = 1 AND a.code = v.code
);

DROP TABLE IF EXISTS refined_wayline_param;
CREATE TEMP TABLE refined_wayline_param (
  action_code text NOT NULL,
  actuator_func text NOT NULL,
  field_code text NOT NULL,
  sort int NOT NULL,
  required boolean NOT NULL,
  value_source text NOT NULL,
  default_value text,
  generator text,
  wire_field boolean NOT NULL DEFAULT true
);

INSERT INTO refined_wayline_param VALUES
-- 悬停、文件夹、航段转云台
('hover','hover','hoverTime',1,true,'ACTION_PARAM','5',NULL,true),
('hover','hover','action_duration',100,false,'ACTION_PARAM',NULL,NULL,false),
('customDirName','customDirName','payloadPositionIndex',1,true,'EXECUTOR_MOUNT',NULL,NULL,true),
('customDirName','customDirName','directoryName',2,true,'ACTION_PARAM',NULL,NULL,true),
('gimbalEvenlyRotate','gimbalEvenlyRotate','gimbalPitchRotateAngle',1,true,'ACTION_PARAM',NULL,NULL,true),
('gimbalEvenlyRotate','gimbalEvenlyRotate','payloadPositionIndex',2,true,'EXECUTOR_MOUNT',NULL,NULL,true),

-- 飞行器偏航：转动方向只对官网支持该字段的机型适用，仍由用户明确选择。
('rotateYaw','rotateYaw','aircraftHeading',1,true,'ACTION_PARAM',NULL,NULL,true),
('rotateYaw','rotateYaw','aircraftPathMode',2,false,'ACTION_PARAM',NULL,NULL,true),
('rotateYaw','rotateYaw','action_duration',100,false,'ACTION_PARAM','5',NULL,false),

-- 云台旋转：挂载、坐标系和绝对角度模式不让用户重复填写；各轴是否启用仍由用户明确选择。
('gimbalRotate','gimbalRotate','payloadPositionIndex',1,true,'EXECUTOR_MOUNT',NULL,NULL,true),
('gimbalRotate','gimbalRotate','gimbalHeadingYawBase',2,true,'SYSTEM','"north"',NULL,true),
('gimbalRotate','gimbalRotate','gimbalRotateMode',3,true,'SYSTEM','"absoluteAngle"',NULL,true),
('gimbalRotate','gimbalRotate','gimbalPitchRotateEnable',4,true,'ACTION_PARAM',NULL,NULL,true),
('gimbalRotate','gimbalRotate','gimbalPitchRotateAngle',5,false,'ACTION_PARAM',NULL,NULL,true),
('gimbalRotate','gimbalRotate','gimbalRollRotateEnable',6,true,'ACTION_PARAM',NULL,NULL,true),
('gimbalRotate','gimbalRotate','gimbalRollRotateAngle',7,false,'ACTION_PARAM',NULL,NULL,true),
('gimbalRotate','gimbalRotate','gimbalYawRotateEnable',8,true,'ACTION_PARAM',NULL,NULL,true),
('gimbalRotate','gimbalRotate','gimbalYawRotateAngle',9,false,'ACTION_PARAM',NULL,NULL,true),
('gimbalRotate','gimbalRotate','gimbalRotateTimeEnable',10,true,'ACTION_PARAM',NULL,NULL,true),
('gimbalRotate','gimbalRotate','gimbalRotateTime',11,false,'ACTION_PARAM',NULL,NULL,true),
('gimbalRotate','gimbalRotate','action_duration',100,false,'ACTION_PARAM','5',NULL,false),

-- 全景拍照
('panoShot','panoShot','payloadPositionIndex',1,true,'EXECUTOR_MOUNT',NULL,NULL,true),
('panoShot','panoShot','payloadLensIndex',2,true,'ACTION_PARAM',NULL,NULL,true),
('panoShot','panoShot','useGlobalPayloadLensIndex',3,true,'ACTION_PARAM',NULL,NULL,true),
('panoShot','panoShot','panoShotSubMode',4,true,'SYSTEM','"panoShot_360"',NULL,true),

-- 定向拍照：配置时在视频上取景生成的云台角度、焦距和目标框归用户配置。
-- 光圈、环境亮度、快门时间、感光度也归用户配置，记在这台设备的检查项上。
-- 照片路径、大小、校验仍是资源文件。挂载来自本次执行设备。执行时的画面操作不写入这条动作。
('orientedShoot','orientedShoot','gimbalPitchRotateAngle',1,true,'ACTION_PARAM',NULL,NULL,true),
('orientedShoot','orientedShoot','gimbalYawRotateAngle',2,true,'ACTION_PARAM',NULL,NULL,true),
('orientedShoot','orientedShoot','focusX',3,true,'ACTION_PARAM',NULL,NULL,true),
('orientedShoot','orientedShoot','focusY',4,true,'ACTION_PARAM',NULL,NULL,true),
('orientedShoot','orientedShoot','focusRegionWidth',5,true,'ACTION_PARAM',NULL,NULL,true),
('orientedShoot','orientedShoot','focusRegionHeight',6,true,'ACTION_PARAM',NULL,NULL,true),
('orientedShoot','orientedShoot','focalLength',7,true,'ACTION_PARAM',NULL,NULL,true),
('orientedShoot','orientedShoot','aircraftHeading',8,true,'REFERENCE_RESOURCE',NULL,NULL,true),
('orientedShoot','orientedShoot','accurateFrameValid',9,true,'ACTION_PARAM',NULL,NULL,true),
('orientedShoot','orientedShoot','payloadPositionIndex',10,true,'EXECUTOR_MOUNT',NULL,NULL,true),
('orientedShoot','orientedShoot','payloadLensIndex',11,true,'ACTION_PARAM',NULL,NULL,true),
('orientedShoot','orientedShoot','useGlobalPayloadLensIndex',12,true,'ACTION_PARAM',NULL,NULL,true),
('orientedShoot','orientedShoot','targetAngle',13,true,'ACTION_PARAM',NULL,NULL,true),
('orientedShoot','orientedShoot','actionUUID',14,true,'SYSTEM',NULL,'UUID_V4',true),
('orientedShoot','orientedShoot','imageWidth',15,true,'SYSTEM','960',NULL,true),
('orientedShoot','orientedShoot','imageHeight',16,true,'SYSTEM','720',NULL,true),
('orientedShoot','orientedShoot','AFPos',17,true,'REFERENCE_RESOURCE',NULL,NULL,true),
('orientedShoot','orientedShoot','gimbalPort',18,true,'EXECUTOR_MOUNT',NULL,NULL,true),
('orientedShoot','orientedShoot','orientedCameraType',19,true,'EXECUTOR_MOUNT',NULL,NULL,true),
('orientedShoot','orientedShoot','orientedFilePath',20,true,'REFERENCE_RESOURCE',NULL,NULL,true),
('orientedShoot','orientedShoot','orientedFileMD5',21,true,'REFERENCE_RESOURCE',NULL,NULL,true),
('orientedShoot','orientedShoot','orientedFileSize',22,true,'REFERENCE_RESOURCE',NULL,NULL,true),
('orientedShoot','orientedShoot','orientedFileSuffix',23,false,'ACTION_PARAM',NULL,NULL,true),
('orientedShoot','orientedShoot','orientedCameraApertue',24,true,'ACTION_PARAM',NULL,NULL,true),
('orientedShoot','orientedShoot','orientedCameraLuminance',25,true,'ACTION_PARAM',NULL,NULL,true),
('orientedShoot','orientedShoot','orientedCameraShutterTime',26,true,'ACTION_PARAM',NULL,NULL,true),
('orientedShoot','orientedShoot','orientedCameraISO',27,true,'ACTION_PARAM',NULL,NULL,true),
('orientedShoot','orientedShoot','orientedPhotoMode',28,true,'ACTION_PARAM',NULL,NULL,true),
('orientedShoot','orientedShoot','action_duration',100,false,'ACTION_PARAM','10',NULL,false),

-- 点云录制：动作名就是操作，用户不再填写操作枚举。
('recordPointCloudStart','recordPointCloud','payloadPositionIndex',1,true,'EXECUTOR_MOUNT',NULL,NULL,true),
('recordPointCloudStart','recordPointCloud','recordPointCloudOperate',2,true,'SYSTEM','"startRecord"',NULL,true),
('recordPointCloudStart','recordPointCloud','action_duration',100,false,'ACTION_PARAM',NULL,NULL,false),
('recordPointCloudPause','recordPointCloud','payloadPositionIndex',1,true,'EXECUTOR_MOUNT',NULL,NULL,true),
('recordPointCloudPause','recordPointCloud','recordPointCloudOperate',2,true,'SYSTEM','"pauseRecord"',NULL,true),
('recordPointCloudPause','recordPointCloud','action_duration',100,false,'ACTION_PARAM',NULL,NULL,false),
('recordPointCloudResume','recordPointCloud','payloadPositionIndex',1,true,'EXECUTOR_MOUNT',NULL,NULL,true),
('recordPointCloudResume','recordPointCloud','recordPointCloudOperate',2,true,'SYSTEM','"resumeRecord"',NULL,true),
('recordPointCloudResume','recordPointCloud','action_duration',100,false,'ACTION_PARAM',NULL,NULL,false),
('recordPointCloudStop','recordPointCloud','payloadPositionIndex',1,true,'EXECUTOR_MOUNT',NULL,NULL,true),
('recordPointCloudStop','recordPointCloud','recordPointCloudOperate',2,true,'SYSTEM','"stopRecord"',NULL,true),
('recordPointCloudStop','recordPointCloud','action_duration',100,false,'ACTION_PARAM',NULL,NULL,false),

-- 探照灯：关闭没有亮度；照明和爆闪只让用户填写亮度。
('searchlightOn','searchlight','payloadPositionIndex',1,true,'EXECUTOR_MOUNT',NULL,NULL,true),
('searchlightOn','searchlight','searchlightOperateType',2,true,'SYSTEM','1',NULL,true),
('searchlightOn','searchlight','searchlightBrightness',3,true,'ACTION_PARAM',NULL,NULL,true),
('searchlightStrobe','searchlight','payloadPositionIndex',1,true,'EXECUTOR_MOUNT',NULL,NULL,true),
('searchlightStrobe','searchlight','searchlightOperateType',2,true,'SYSTEM','2',NULL,true),
('searchlightStrobe','searchlight','searchlightBrightness',3,true,'ACTION_PARAM',NULL,NULL,true),
('searchlightOff','searchlight','payloadPositionIndex',1,true,'EXECUTOR_MOUNT',NULL,NULL,true),
('searchlightOff','searchlight','searchlightOperateType',2,true,'SYSTEM','0',NULL,true);

-- 参数定义是动作库权威：全部字段都保留，普通填参界面只消费 valueSource=ACTION_PARAM。
UPDATE ent_action_t1 a
SET execution_means = CASE
      WHEN built.action_code = 'gimbalRotate'
        THEN '["UAV","ROBOT","MANUAL","FIXED_CAMERA"]'::jsonb
      ELSE a.execution_means
    END,
    param_slots_json = jsonb_build_object(
      'version', 2,
      'methods', (
        SELECT jsonb_agg(
          jsonb_build_object('executionMeans', means.means, 'fields', built.fields)
          ORDER BY means.ord
        )
        FROM (
          SELECT 'UAV'::text AS means, 1 AS ord
          UNION ALL
          SELECT extra.means, extra.ord
          FROM (VALUES ('ROBOT', 2), ('MANUAL', 3), ('FIXED_CAMERA', 4)) AS extra(means, ord)
          WHERE built.action_code = 'gimbalRotate'
        ) means
      )
    ),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM (
  SELECT action_code,
         jsonb_agg(
           jsonb_strip_nulls(jsonb_build_object(
             'fieldCode', field_code,
             'required', required,
             'defaultValue', CASE WHEN default_value IS NULL THEN NULL ELSE default_value::jsonb END,
             'valueSource', value_source,
             'generator', generator
           ))
           ORDER BY sort
         ) AS fields
  FROM refined_wayline_param
  GROUP BY action_code
) built
WHERE a.deleted = false AND a.tenant_id = 1 AND a.code = built.action_code;

-- 新动作沿用原动作的分类。点云原来还有“拍照拍视频”子分类，拆分后保持可见位置不变。
INSERT INTO dynamic_entity_category_relation_t1 (
  tenant_id, category_id, entity_type_code, entity_id, domain, sort, creator, deleted
)
SELECT 1, old_rel.category_id, 'action', target.id, old_rel.domain, old_rel.sort, 'seed', false
FROM (VALUES
  ('recordPointCloud', 'recordPointCloudStart'),
  ('recordPointCloud', 'recordPointCloudPause'),
  ('recordPointCloud', 'recordPointCloudResume'),
  ('recordPointCloud', 'recordPointCloudStop'),
  ('searchlight', 'searchlightOn'),
  ('searchlight', 'searchlightStrobe'),
  ('searchlight', 'searchlightOff')
) x(source_code, target_code)
JOIN ent_action_t1 source
  ON source.tenant_id = 1 AND source.code = x.source_code
JOIN ent_action_t1 target
  ON target.deleted = false AND target.tenant_id = 1 AND target.code = x.target_code
JOIN dynamic_entity_category_relation_t1 old_rel
  ON old_rel.deleted = false AND old_rel.tenant_id = 1
 AND old_rel.entity_type_code = 'action' AND old_rel.entity_id = source.id
WHERE NOT EXISTS (
  SELECT 1 FROM dynamic_entity_category_relation_t1 r
  WHERE r.deleted = false AND r.tenant_id = 1 AND r.entity_type_code = 'action'
    AND r.entity_id = target.id AND r.category_id = old_rel.category_id
);

-- 每个标准动作各有自己的协议指令记录；多个动作可以使用同一 WPML actionActuatorFunc。
INSERT INTO ent_data_protocol_t1 (
  tenant_id, entity_type_code, model_id, name, code, status,
  custom_fields, command_direction, creator, deleted
)
SELECT
  1, 'data_protocol', model.id, action.name,
  'proto-' || version.book || '-wpml-' || action.code,
  1,
  jsonb_build_object(
    'opcode', 0,
    'command_direction', 'downlink',
    'protocol_version', jsonb_build_array(version.release),
    'communication_protocol', 'wayline',
    'command_json', jsonb_build_object(
      'outbound', jsonb_build_object(
        'actionActuatorFunc', built.actuator_func,
        'params', built.outbound
      )
    ),
    'field_description_json', jsonb_build_object('outbound', built.descriptions)
  ),
  'downlink', 'seed', false
FROM (VALUES
  ('dji-dock', 'dji-dock-1.16.1', 'data_protocol_dji_dock_wayline'),
  ('dji-pilot', 'dji-pilot-1.16.1', 'data_protocol_dji_pilot_wayline')
) version(book, release, model_code)
JOIN dynamic_model model
  ON model.deleted = false AND model.tenant_id = 1 AND model.code = version.model_code
JOIN (
  SELECT p.action_code,
         min(p.actuator_func) AS actuator_func,
         jsonb_object_agg(
           p.field_code,
           CASE WHEN p.default_value IS NULL THEN '""'::jsonb ELSE p.default_value::jsonb END
           ORDER BY p.sort
         ) FILTER (WHERE p.wire_field) AS outbound,
         jsonb_agg(jsonb_strip_nulls(jsonb_build_object(
           'code', p.field_code,
           'path', 'params.' || p.field_code,
           'type', COALESCE(NULLIF(btrim(f.type), ''), 'string'),
           'label', COALESCE(f.name, p.field_code),
           'required', p.required,
           'description', COALESCE(f.description, ''),
           'options', CASE WHEN f.options IS NULL OR btrim(f.options) = '' THEN NULL ELSE f.options::jsonb END
         )) ORDER BY p.sort) FILTER (WHERE p.wire_field) AS descriptions
  FROM refined_wayline_param p
  LEFT JOIN dynamic_field f
    ON f.deleted = false AND f.tenant_id = 1 AND f.code = p.field_code
  GROUP BY p.action_code
) built ON true
JOIN ent_action_t1 action
  ON action.deleted = false AND action.tenant_id = 1 AND action.code = built.action_code
WHERE NOT EXISTS (
  SELECT 1 FROM ent_data_protocol_t1 protocol
  WHERE protocol.deleted = false AND protocol.tenant_id = 1
    AND protocol.code = 'proto-' || version.book || '-wpml-' || action.code
);

UPDATE ent_data_protocol_t1 protocol
SET name = action.name,
    model_id = model.id,
    command_direction = 'downlink',
    custom_fields = jsonb_build_object(
      'opcode', 0,
      'command_direction', 'downlink',
      'protocol_version', jsonb_build_array(version.release),
    'communication_protocol', 'wayline',
      'command_json', jsonb_build_object(
        'outbound', jsonb_build_object(
          'actionActuatorFunc', built.actuator_func,
          'params', built.outbound
        )
      ),
      'field_description_json', jsonb_build_object('outbound', built.descriptions)
    ),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM (VALUES
  ('dji-dock', 'dji-dock-1.16.1', 'data_protocol_dji_dock_wayline'),
  ('dji-pilot', 'dji-pilot-1.16.1', 'data_protocol_dji_pilot_wayline')
) version(book, release, model_code)
JOIN dynamic_model model
  ON model.deleted = false AND model.tenant_id = 1 AND model.code = version.model_code
JOIN (
  SELECT p.action_code,
         min(p.actuator_func) AS actuator_func,
         jsonb_object_agg(
           p.field_code,
           CASE WHEN p.default_value IS NULL THEN '""'::jsonb ELSE p.default_value::jsonb END
           ORDER BY p.sort
         ) FILTER (WHERE p.wire_field) AS outbound,
         jsonb_agg(jsonb_strip_nulls(jsonb_build_object(
           'code', p.field_code,
           'path', 'params.' || p.field_code,
           'type', COALESCE(NULLIF(btrim(f.type), ''), 'string'),
           'label', COALESCE(f.name, p.field_code),
           'required', p.required,
           'description', COALESCE(f.description, ''),
           'options', CASE WHEN f.options IS NULL OR btrim(f.options) = '' THEN NULL ELSE f.options::jsonb END
         )) ORDER BY p.sort) FILTER (WHERE p.wire_field) AS descriptions
  FROM refined_wayline_param p
  LEFT JOIN dynamic_field f
    ON f.deleted = false AND f.tenant_id = 1 AND f.code = p.field_code
  GROUP BY p.action_code
) built ON true
JOIN ent_action_t1 action
  ON action.deleted = false AND action.tenant_id = 1 AND action.code = built.action_code
WHERE protocol.deleted = false AND protocol.tenant_id = 1
  AND protocol.code = 'proto-' || version.book || '-wpml-' || action.code;

-- 协议指令仍归大疆无人机目录。
INSERT INTO dynamic_entity_category_relation_t1 (
  tenant_id, category_id, entity_type_code, entity_id, domain, sort, creator, deleted
)
SELECT 1, category.id, 'data_protocol', protocol.id, NULL, 0, 'seed', false
FROM ent_data_protocol_t1 protocol
JOIN dynamic_category category
  ON category.deleted = false AND category.tenant_id = 1 AND category.code = 'cat-protocol-dji-uav'
WHERE protocol.deleted = false AND protocol.tenant_id = 1
  AND EXISTS (
    SELECT 1 FROM refined_wayline_param p
    WHERE protocol.code LIKE 'proto-dji-%-wpml-' || p.action_code
  )
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_entity_category_relation_t1 relation
    WHERE relation.deleted = false AND relation.tenant_id = 1
      AND relation.entity_type_code = 'data_protocol'
      AND relation.entity_id = protocol.id AND relation.category_id = category.id
  );

INSERT INTO dynamic_entity_field_index_t1
  (entity_id, model_id, field_code, value_string, value_number, creator, tenant_id, deleted)
SELECT protocol.id, protocol.model_id, 'opcode', NULL, 0, 'seed', 1, false
FROM ent_data_protocol_t1 protocol
WHERE protocol.deleted = false AND protocol.tenant_id = 1
  AND EXISTS (
    SELECT 1 FROM refined_wayline_param p
    WHERE protocol.code LIKE 'proto-dji-%-wpml-' || p.action_code
  )
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_entity_field_index_t1 idx
    WHERE idx.deleted = false AND idx.tenant_id = 1
      AND idx.entity_id = protocol.id AND idx.field_code = 'opcode'
  );

INSERT INTO dynamic_entity_field_index_t1
  (entity_id, model_id, field_code, value_string, value_number, creator, tenant_id, deleted)
SELECT protocol.id, protocol.model_id, 'protocol_version',
       protocol.custom_fields->'protocol_version'->>0, NULL, 'seed', 1, false
FROM ent_data_protocol_t1 protocol
WHERE protocol.deleted = false AND protocol.tenant_id = 1
  AND EXISTS (
    SELECT 1 FROM refined_wayline_param p
    WHERE protocol.code LIKE 'proto-dji-%-wpml-' || p.action_code
  )
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_entity_field_index_t1 idx
    WHERE idx.deleted = false AND idx.tenant_id = 1
      AND idx.entity_id = protocol.id AND idx.field_code = 'protocol_version'
  );

INSERT INTO ent_protocol_mapping_t1 (
  tenant_id, entity_type_code, model_id, name, code, status,
  custom_fields, creator, deleted
)
SELECT
  1, 'protocol_mapping', model.id,
  action.name || ' · ' || version.label,
  'map-' || version.book || '-wpml-' || action.code,
  1,
  jsonb_build_object(
    'action_ref', jsonb_build_object('id', action.id, 'entityTypeCode', 'action'),
    'mapped_protocol_version', version.release,
    'protocol_instruction_ref', jsonb_build_object('id', protocol.id, 'entityTypeCode', 'data_protocol'),
    'param_slot_mapping_json', built.slots
  ),
  'seed', false
FROM (VALUES
  ('dji-dock', 'dji-dock-1.16.1', '大疆机场 1.16.1'),
  ('dji-pilot', 'dji-pilot-1.16.1', '大疆 Pilot 1.16.1')
) version(book, release, label)
JOIN dynamic_model model
  ON model.deleted = false AND model.tenant_id = 1 AND model.code = 'protocol_mapping_standard'
JOIN (
  SELECT action_code,
         jsonb_agg(jsonb_build_object(
           'slot', field_code,
           'sourceKind', value_source,
           'path', 'params.' || field_code
         ) ORDER BY sort) FILTER (WHERE wire_field) AS slots
  FROM refined_wayline_param
  GROUP BY action_code
) built ON true
JOIN ent_action_t1 action
  ON action.deleted = false AND action.tenant_id = 1 AND action.code = built.action_code
JOIN ent_data_protocol_t1 protocol
  ON protocol.deleted = false AND protocol.tenant_id = 1
 AND protocol.code = 'proto-' || version.book || '-wpml-' || action.code
WHERE NOT EXISTS (
  SELECT 1 FROM ent_protocol_mapping_t1 mapping
  WHERE mapping.deleted = false AND mapping.tenant_id = 1
    AND mapping.code = 'map-' || version.book || '-wpml-' || action.code
);

UPDATE ent_protocol_mapping_t1 mapping
SET name = action.name || ' · ' || version.label,
    custom_fields = jsonb_build_object(
      'action_ref', jsonb_build_object('id', action.id, 'entityTypeCode', 'action'),
      'mapped_protocol_version', version.release,
      'protocol_instruction_ref', jsonb_build_object('id', protocol.id, 'entityTypeCode', 'data_protocol'),
      'param_slot_mapping_json', built.slots
    ),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM (VALUES
  ('dji-dock', 'dji-dock-1.16.1', '大疆机场 1.16.1'),
  ('dji-pilot', 'dji-pilot-1.16.1', '大疆 Pilot 1.16.1')
) version(book, release, label)
JOIN (
  SELECT action_code,
         jsonb_agg(jsonb_build_object(
           'slot', field_code,
           'sourceKind', value_source,
           'path', 'params.' || field_code
         ) ORDER BY sort) FILTER (WHERE wire_field) AS slots
  FROM refined_wayline_param
  GROUP BY action_code
) built ON true
JOIN ent_action_t1 action
  ON action.deleted = false AND action.tenant_id = 1 AND action.code = built.action_code
JOIN ent_data_protocol_t1 protocol
  ON protocol.deleted = false AND protocol.tenant_id = 1
 AND protocol.code = 'proto-' || version.book || '-wpml-' || action.code
WHERE mapping.deleted = false AND mapping.tenant_id = 1
  AND mapping.code = 'map-' || version.book || '-wpml-' || action.code;

INSERT INTO dynamic_entity_category_relation_t1 (
  tenant_id, category_id, entity_type_code, entity_id, domain, sort, creator, deleted
)
SELECT 1, category.id, 'protocol_mapping', mapping.id, NULL, 0, 'seed', false
FROM ent_protocol_mapping_t1 mapping
JOIN dynamic_category category
  ON category.deleted = false AND category.tenant_id = 1 AND category.code = 'protocol_mapping_root'
WHERE mapping.deleted = false AND mapping.tenant_id = 1
  AND EXISTS (
    SELECT 1 FROM refined_wayline_param p
    WHERE mapping.code LIKE 'map-dji-%-wpml-' || p.action_code
  )
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_entity_category_relation_t1 relation
    WHERE relation.deleted = false AND relation.tenant_id = 1
      AND relation.entity_type_code = 'protocol_mapping'
      AND relation.entity_id = mapping.id AND relation.category_id = category.id
  );

INSERT INTO dynamic_entity_field_index_t1
  (entity_id, model_id, field_code, value_string, value_number, creator, tenant_id, deleted)
SELECT mapping.id, mapping.model_id, 'action_ref', NULL,
       (mapping.custom_fields->'action_ref'->>'id')::bigint, 'seed', 1, false
FROM ent_protocol_mapping_t1 mapping
WHERE mapping.deleted = false AND mapping.tenant_id = 1
  AND EXISTS (
    SELECT 1 FROM refined_wayline_param p
    WHERE mapping.code LIKE 'map-dji-%-wpml-' || p.action_code
  )
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_entity_field_index_t1 idx
    WHERE idx.deleted = false AND idx.tenant_id = 1
      AND idx.entity_id = mapping.id AND idx.field_code = 'action_ref'
  );

INSERT INTO dynamic_entity_field_index_t1
  (entity_id, model_id, field_code, value_string, value_number, creator, tenant_id, deleted)
SELECT mapping.id, mapping.model_id, 'mapped_protocol_version',
       mapping.custom_fields->>'mapped_protocol_version', NULL, 'seed', 1, false
FROM ent_protocol_mapping_t1 mapping
WHERE mapping.deleted = false AND mapping.tenant_id = 1
  AND EXISTS (
    SELECT 1 FROM refined_wayline_param p
    WHERE mapping.code LIKE 'map-dji-%-wpml-' || p.action_code
  )
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_entity_field_index_t1 idx
    WHERE idx.deleted = false AND idx.tenant_id = 1
      AND idx.entity_id = mapping.id AND idx.field_code = 'mapped_protocol_version'
  );

INSERT INTO dynamic_entity_field_index_t1
  (entity_id, model_id, field_code, value_string, value_number, creator, tenant_id, deleted)
SELECT mapping.id, mapping.model_id, 'protocol_instruction_ref', NULL,
       (mapping.custom_fields->'protocol_instruction_ref'->>'id')::bigint, 'seed', 1, false
FROM ent_protocol_mapping_t1 mapping
WHERE mapping.deleted = false AND mapping.tenant_id = 1
  AND EXISTS (
    SELECT 1 FROM refined_wayline_param p
    WHERE mapping.code LIKE 'map-dji-%-wpml-' || p.action_code
  )
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_entity_field_index_t1 idx
    WHERE idx.deleted = false AND idx.tenant_id = 1
      AND idx.entity_id = mapping.id AND idx.field_code = 'protocol_instruction_ref'
  );

-- 旧的两条“动作 + 操作枚举”不再是正式入口；没有调用方，因此直接软删除。
UPDATE ent_protocol_mapping_t1
SET deleted = true, updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1 AND deleted = false
  AND code IN (
    'map-dji-dock-wpml-recordPointCloud',
    'map-dji-pilot-wpml-recordPointCloud',
    'map-dji-dock-wpml-searchlight',
    'map-dji-pilot-wpml-searchlight'
  );

UPDATE ent_data_protocol_t1
SET deleted = true, updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1 AND deleted = false
  AND code IN (
    'proto-dji-dock-wpml-recordPointCloud',
    'proto-dji-pilot-wpml-recordPointCloud',
    'proto-dji-dock-wpml-searchlight',
    'proto-dji-pilot-wpml-searchlight'
  );

UPDATE ent_action_t1
SET deleted = true, updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1 AND deleted = false
  AND code IN ('recordPointCloud', 'searchlight');

DROP TABLE IF EXISTS refined_wayline_param;
