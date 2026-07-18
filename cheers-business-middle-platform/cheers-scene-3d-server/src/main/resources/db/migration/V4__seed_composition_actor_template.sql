-- 场景编排通用 Actor 模板（spawn 依赖 actor 表存在）
INSERT INTO scene_platform.actor (
    id, actor_code, actor_name, actor_class, actor_category, engine_profile, abstract_flag,
    lifecycle_status, component_tree, metadata_json, create_time, update_time, creator, updater, deleted
)
VALUES (
    12100,
    'composition_structure',
    '场景编排构筑物',
    'StaticMeshActor',
    'composition',
    'threejs',
    FALSE,
    'ACTIVE',
    NULL,
    '{"purpose":"scene-composition","renderAssetCodeFromInstanceMetadata":true}',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    'system',
    'system',
    FALSE
)
ON CONFLICT (id) DO UPDATE SET
    actor_code = EXCLUDED.actor_code,
    actor_name = EXCLUDED.actor_name,
    actor_category = EXCLUDED.actor_category,
    metadata_json = EXCLUDED.metadata_json,
    update_time = CURRENT_TIMESTAMP,
    updater = EXCLUDED.updater,
    deleted = FALSE;
