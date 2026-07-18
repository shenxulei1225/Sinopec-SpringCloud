INSERT INTO twin.twin_mapping (
    id, mapping_code, mapping_type, relation_mode, facility_id, actor_instance_id, scene_id, scene_code,
    bind_source, status, version, remark, ext_json, create_time, update_time, creator, updater, deleted
)
SELECT
    15001,
    'facility-demo-GD-1001-1001-001',
    1,
    1,
    f.id,
    13001,
    10001,
    'facility-demo-scene',
    'demo-seed',
    1,
    0,
    '设施工作台演示绑定-储罐',
    '{"modelAssetCode":"facility-tank-demo-glb","pendingLegacyReplacement":true}'::jsonb,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    'system',
    'system',
    FALSE
FROM facility_management.facility f
WHERE f.facility_code = 'GD-1001-1001-001'
ON CONFLICT (id) DO UPDATE SET
    mapping_code = EXCLUDED.mapping_code,
    mapping_type = EXCLUDED.mapping_type,
    relation_mode = EXCLUDED.relation_mode,
    facility_id = EXCLUDED.facility_id,
    actor_instance_id = EXCLUDED.actor_instance_id,
    scene_id = EXCLUDED.scene_id,
    scene_code = EXCLUDED.scene_code,
    bind_source = EXCLUDED.bind_source,
    status = EXCLUDED.status,
    version = EXCLUDED.version,
    remark = EXCLUDED.remark,
    ext_json = EXCLUDED.ext_json,
    update_time = CURRENT_TIMESTAMP,
    updater = EXCLUDED.updater,
    deleted = EXCLUDED.deleted;

INSERT INTO twin.twin_mapping (
    id, mapping_code, mapping_type, relation_mode, facility_id, actor_instance_id, scene_id, scene_code,
    bind_source, status, version, remark, ext_json, create_time, update_time, creator, updater, deleted
)
SELECT
    15002,
    'facility-demo-FM-1001-1002-001',
    1,
    1,
    f.id,
    13002,
    10001,
    'facility-demo-scene',
    'demo-seed',
    1,
    0,
    '设施工作台演示绑定-阀门',
    '{"modelAssetCode":"facility-valve-demo-glb","pendingLegacyReplacement":true}'::jsonb,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    'system',
    'system',
    FALSE
FROM facility_management.facility f
WHERE f.facility_code = 'FM-1001-1002-001'
ON CONFLICT (id) DO UPDATE SET
    mapping_code = EXCLUDED.mapping_code,
    mapping_type = EXCLUDED.mapping_type,
    relation_mode = EXCLUDED.relation_mode,
    facility_id = EXCLUDED.facility_id,
    actor_instance_id = EXCLUDED.actor_instance_id,
    scene_id = EXCLUDED.scene_id,
    scene_code = EXCLUDED.scene_code,
    bind_source = EXCLUDED.bind_source,
    status = EXCLUDED.status,
    version = EXCLUDED.version,
    remark = EXCLUDED.remark,
    ext_json = EXCLUDED.ext_json,
    update_time = CURRENT_TIMESTAMP,
    updater = EXCLUDED.updater,
    deleted = EXCLUDED.deleted;
