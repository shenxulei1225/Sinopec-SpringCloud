CREATE TABLE IF NOT EXISTS scene_platform.facility_scene_binding (
    id BIGINT PRIMARY KEY,
    facility_id BIGINT NOT NULL,
    scene_code VARCHAR(64) NOT NULL,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creator VARCHAR(64),
    updater VARCHAR(64),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT uk_facility_scene_binding_facility_id UNIQUE (facility_id),
    CONSTRAINT uk_facility_scene_binding_scene_code UNIQUE (scene_code)
);
CREATE INDEX IF NOT EXISTS idx_facility_scene_binding_tenant_id
    ON scene_platform.facility_scene_binding (tenant_id) WHERE deleted = FALSE;

INSERT INTO scene_platform.scene (
    id, scene_code, scene_name, scene_type, engine_profile, status, publish_status, tenant_id,
    create_time, update_time, creator, updater, deleted
)
VALUES
    (
        10002,
        'SCENE-JINQIAO',
        '金桥站场场景',
        'OUTDOOR',
        'threejs',
        1,
        'PUBLISHED',
        1,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP,
        'system',
        'system',
        FALSE
    ),
    (
        10003,
        'SCENE-LUOYANG-SHENGRUI',
        '洛阳圣瑞站场场景',
        'OUTDOOR',
        'threejs',
        1,
        'PUBLISHED',
        1,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP,
        'system',
        'system',
        FALSE
    )
ON CONFLICT (scene_code) DO UPDATE SET
    scene_name = EXCLUDED.scene_name,
    scene_type = EXCLUDED.scene_type,
    engine_profile = EXCLUDED.engine_profile,
    status = EXCLUDED.status,
    publish_status = EXCLUDED.publish_status,
    tenant_id = EXCLUDED.tenant_id,
    update_time = CURRENT_TIMESTAMP,
    updater = EXCLUDED.updater,
    deleted = EXCLUDED.deleted;

INSERT INTO scene_platform.facility_scene_binding (
    id, facility_id, scene_code, tenant_id, create_time, update_time, creator, updater, deleted
)
VALUES
    (15001, 44, 'SCENE-JINQIAO', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', FALSE),
    (15002, 45, 'SCENE-LUOYANG-SHENGRUI', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', FALSE)
ON CONFLICT (facility_id) DO UPDATE SET
    scene_code = EXCLUDED.scene_code,
    tenant_id = EXCLUDED.tenant_id,
    update_time = CURRENT_TIMESTAMP,
    updater = EXCLUDED.updater,
    deleted = EXCLUDED.deleted;
