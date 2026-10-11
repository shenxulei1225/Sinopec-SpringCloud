-- 删掉和大疆标准动作重复的旧动作。
-- 保留：开始拍照、悬停等待、一键返航（原来的无人机返航）、旋转云台。
-- 删除：拍照、悬停观察、返回充电改名后的第二条一键返航、调整拍摄角度。
-- 不负责：安全帽、工服等巡检动作。那些不是大疆飞行动作的重复项。

SET search_path TO dynamicbusiness;

BEGIN;

CREATE TEMP TABLE drop_action ON COMMIT DROP AS
SELECT id, code
FROM ent_action_t1
WHERE tenant_id = 1
  AND deleted = false
  AND code IN (
    'act-robot-shoot',
    'act-hover',
    'act-uav-return-charge',
    'act-robot-aim'
  );

UPDATE dynamic_entity_field_index_t1 idx
SET deleted = true, updater = 'seed', update_time = CURRENT_TIMESTAMP
FROM ent_protocol_mapping_t1 m
JOIN drop_action a ON a.id::text = (m.custom_fields->'action_ref'->>'id')
WHERE idx.tenant_id = 1 AND idx.deleted = false AND idx.entity_id = m.id;

UPDATE dynamic_entity_category_relation_t1 r
SET deleted = true, updater = 'seed', update_time = CURRENT_TIMESTAMP
FROM ent_protocol_mapping_t1 m
JOIN drop_action a ON a.id::text = (m.custom_fields->'action_ref'->>'id')
WHERE r.tenant_id = 1 AND r.deleted = false
  AND r.entity_type_code = 'protocol_mapping'
  AND r.entity_id = m.id;

UPDATE ent_protocol_mapping_t1 m
SET deleted = true, updater = 'seed', update_time = CURRENT_TIMESTAMP
FROM drop_action a
WHERE m.deleted = false
  AND a.id::text = (m.custom_fields->'action_ref'->>'id');

UPDATE dynamic_entity_field_index_t1 idx
SET deleted = true, updater = 'seed', update_time = CURRENT_TIMESTAMP
FROM drop_action a
WHERE idx.tenant_id = 1 AND idx.deleted = false AND idx.entity_id = a.id;

UPDATE dynamic_entity_category_relation_t1 r
SET deleted = true, updater = 'seed', update_time = CURRENT_TIMESTAMP
FROM drop_action a
WHERE r.tenant_id = 1 AND r.deleted = false
  AND r.entity_type_code = 'action'
  AND r.entity_id = a.id;

UPDATE ent_action_t1 a
SET deleted = true, updater = 'seed', update_time = CURRENT_TIMESTAMP
FROM drop_action d
WHERE a.id = d.id AND a.deleted = false;

COMMIT;
