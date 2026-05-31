CREATE SCHEMA IF NOT EXISTS scene_platform;
CREATE EXTENSION IF NOT EXISTS pg_trgm WITH SCHEMA public;
SET search_path TO scene_platform, public;

CREATE TABLE IF NOT EXISTS scene_platform.scene (
    id BIGINT PRIMARY KEY,
    scene_code VARCHAR(64) NOT NULL,
    scene_name VARCHAR(128) NOT NULL,
    scene_type VARCHAR(32),
    engine_profile VARCHAR(32),
    capabilities_json TEXT,
    layer_config_json TEXT,
    default_viewpoint_json TEXT,
    project_code VARCHAR(64),
    business_key VARCHAR(128),
    status INTEGER,
    publish_status VARCHAR(32),
    published_at TIMESTAMP,
    published_by VARCHAR(64),
    actor_instance_codes_json JSONB DEFAULT '[]'::jsonb,
    actor_states_json JSONB DEFAULT '{}'::jsonb,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creator VARCHAR(64),
    updater VARCHAR(64),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT uk_scene_scene_code UNIQUE (scene_code)
);
CREATE INDEX IF NOT EXISTS idx_scene_project_code ON scene_platform.scene (project_code) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_scene_business_key ON scene_platform.scene (business_key) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_scene_status ON scene_platform.scene (status) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_scene_tenant_id ON scene_platform.scene (tenant_id) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_scene_scene_name_trgm ON scene_platform.scene USING GIN (scene_name gin_trgm_ops) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_scene_scene_code_trgm ON scene_platform.scene USING GIN (scene_code gin_trgm_ops) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_scene_capabilities_json_trgm ON scene_platform.scene USING GIN (capabilities_json gin_trgm_ops) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_scene_actor_instance_codes_json ON scene_platform.scene USING GIN (actor_instance_codes_json);

CREATE TABLE IF NOT EXISTS scene_platform.actor (
    id BIGINT PRIMARY KEY,
    actor_code VARCHAR(64) NOT NULL,
    actor_name VARCHAR(128) NOT NULL,
    actor_class VARCHAR(128),
    parent_actor_code VARCHAR(64),
    actor_category VARCHAR(64),
    engine_profile VARCHAR(32),
    abstract_flag BOOLEAN,
    lifecycle_status VARCHAR(32),
    component_tree TEXT,
    metadata_json TEXT,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creator VARCHAR(64),
    updater VARCHAR(64),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT uk_actor_actor_code UNIQUE (actor_code)
);
CREATE INDEX IF NOT EXISTS idx_actor_parent_actor_code ON scene_platform.actor (parent_actor_code) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_actor_component_tree ON scene_platform.actor USING GIN (component_tree);

CREATE TABLE IF NOT EXISTS scene_platform.component (
    id BIGINT PRIMARY KEY,
    component_code VARCHAR(64) NOT NULL,
    component_name VARCHAR(128) NOT NULL,
    display_name VARCHAR(128),
    component_class VARCHAR(128),
    component_category VARCHAR(64),
    parent_component_code VARCHAR(64),
    engine_profile VARCHAR(32),
    abstract_flag BOOLEAN,
    editable_flag BOOLEAN,
    spawnable_flag BOOLEAN,
    default_json JSONB,
    property_schema_json JSONB,
    lifecycle_status VARCHAR(32),
    metadata_json JSONB,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creator VARCHAR(64),
    updater VARCHAR(64),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT uk_component_component_code UNIQUE (component_code)
);
CREATE INDEX IF NOT EXISTS idx_component_parent_component_code ON scene_platform.component (parent_component_code) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_component_default_json ON scene_platform.component USING GIN (default_json);
CREATE INDEX IF NOT EXISTS idx_component_property_schema_json ON scene_platform.component USING GIN (property_schema_json);

CREATE TABLE IF NOT EXISTS scene_platform.asset_resource (
    id BIGINT PRIMARY KEY,
    asset_code VARCHAR(64) NOT NULL,
    asset_name VARCHAR(128) NOT NULL,
    asset_type VARCHAR(64),
    asset_url TEXT,
    preview_url TEXT,
    format VARCHAR(64),
    engine_profile VARCHAR(32),
    metadata_json JSONB,
    status INTEGER,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creator VARCHAR(64),
    updater VARCHAR(64),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT uk_asset_resource_asset_code UNIQUE (asset_code)
);
CREATE INDEX IF NOT EXISTS idx_asset_resource_asset_type ON scene_platform.asset_resource (asset_type) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_asset_resource_status ON scene_platform.asset_resource (status) WHERE deleted = FALSE;

CREATE TABLE IF NOT EXISTS scene_platform.coordinate_transform_profile (
    id BIGINT PRIMARY KEY,
    profile_code VARCHAR(64) NOT NULL,
    profile_name VARCHAR(128) NOT NULL,
    geographic_crs_code VARCHAR(64),
    projected_crs_code VARCHAR(64),
    datum_code VARCHAR(64),
    ellipsoid_code VARCHAR(64),
    planet_shape VARCHAR(64),
    reference_frame_type VARCHAR(64),
    local_frame_type VARCHAR(64),
    engine_frame_type VARCHAR(64),
    axis_order VARCHAR(64),
    handedness VARCHAR(32),
    linear_unit VARCHAR(32),
    angular_unit VARCHAR(32),
    transform_pipeline_json JSONB,
    remark TEXT,
    status INTEGER,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creator VARCHAR(64),
    updater VARCHAR(64),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT uk_coordinate_transform_profile_profile_code UNIQUE (profile_code)
);
CREATE INDEX IF NOT EXISTS idx_coordinate_transform_profile_status ON scene_platform.coordinate_transform_profile (status) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_coordinate_transform_profile_pipeline_json ON scene_platform.coordinate_transform_profile USING GIN (transform_pipeline_json);

CREATE TABLE IF NOT EXISTS scene_platform.coordinate_crs_catalog (
    id BIGINT PRIMARY KEY,
    crs_code VARCHAR(64) NOT NULL,
    crs_name VARCHAR(128) NOT NULL,
    crs_type VARCHAR(64),
    datum_code VARCHAR(64),
    ellipsoid_code VARCHAR(64),
    area_scope TEXT,
    recommended_engine VARCHAR(64),
    recommended_scene_type VARCHAR(64),
    warning_message TEXT,
    enabled_flag BOOLEAN,
    sort_no INTEGER,
    metadata_json JSONB,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creator VARCHAR(64),
    updater VARCHAR(64),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT uk_coordinate_crs_catalog_crs_code UNIQUE (crs_code)
);
CREATE INDEX IF NOT EXISTS idx_coordinate_crs_catalog_enabled_flag ON scene_platform.coordinate_crs_catalog (enabled_flag) WHERE deleted = FALSE;

CREATE TABLE IF NOT EXISTS scene_platform.actor_instance (
    id BIGINT PRIMARY KEY,
    scene_id BIGINT NOT NULL,
    actor_code VARCHAR(64) NOT NULL,
    instance_code VARCHAR(64) NOT NULL,
    instance_name VARCHAR(128),
    parent_instance_code VARCHAR(64),
    instance_status VARCHAR(32),
    visible_flag BOOLEAN,
    version_no INTEGER,
    transform JSONB,
    metadata_json JSONB,
    path TEXT,
    layer_keys JSONB,
    position_timestamp BIGINT,
    animation_enabled BOOLEAN,
    animation_duration INTEGER,
    animation_type VARCHAR(64),
    tenant_id BIGINT NOT NULL DEFAULT 0,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creator VARCHAR(64),
    updater VARCHAR(64),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT uk_actor_instance_instance_code UNIQUE (instance_code),
    CONSTRAINT fk_actor_instance_scene_id FOREIGN KEY (scene_id) REFERENCES scene_platform.scene (id)
);
CREATE INDEX IF NOT EXISTS idx_actor_instance_scene_id ON scene_platform.actor_instance (scene_id) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_actor_instance_actor_code ON scene_platform.actor_instance (actor_code) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_actor_instance_parent_instance_code ON scene_platform.actor_instance (parent_instance_code) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_actor_instance_tenant_scene_id ON scene_platform.actor_instance (tenant_id, scene_id) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_actor_instance_tenant_instance_code ON scene_platform.actor_instance (tenant_id, instance_code) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_actor_instance_transform ON scene_platform.actor_instance USING GIN (transform);
CREATE INDEX IF NOT EXISTS idx_actor_instance_layer_keys ON scene_platform.actor_instance USING GIN (layer_keys);

CREATE TABLE IF NOT EXISTS scene_platform.scene_layer (
    id BIGINT PRIMARY KEY,
    scene_id BIGINT NOT NULL,
    layer_code VARCHAR(64) NOT NULL,
    layer_name VARCHAR(128) NOT NULL,
    layer_display_name VARCHAR(128),
    layer_type VARCHAR(32),
    layer_config_json JSONB,
    visible_flag BOOLEAN,
    interactive_flag BOOLEAN,
    sort_no INTEGER,
    instance_ids_json JSONB,
    filter_rules_json JSONB,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creator VARCHAR(64),
    updater VARCHAR(64),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT uk_scene_layer_scene_id_layer_code UNIQUE (scene_id, layer_code),
    CONSTRAINT fk_scene_layer_scene_id FOREIGN KEY (scene_id) REFERENCES scene_platform.scene (id)
);
CREATE INDEX IF NOT EXISTS idx_scene_layer_scene_id ON scene_platform.scene_layer (scene_id) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_scene_layer_tenant_scene_id ON scene_platform.scene_layer (tenant_id, scene_id) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_scene_layer_tenant_layer_code ON scene_platform.scene_layer (tenant_id, layer_code) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_scene_layer_instance_ids_json ON scene_platform.scene_layer USING GIN (instance_ids_json);
CREATE INDEX IF NOT EXISTS idx_scene_layer_filter_rules_json ON scene_platform.scene_layer USING GIN (filter_rules_json);

CREATE TABLE IF NOT EXISTS scene_platform.actor_instance_component (
    id BIGINT PRIMARY KEY,
    actor_instance_id BIGINT NOT NULL,
    actor_code VARCHAR(64) NOT NULL,
    instance_code VARCHAR(64) NOT NULL,
    component_code VARCHAR(64) NOT NULL,
    component_type_name VARCHAR(128),
    enabled_flag BOOLEAN,
    sort_no INTEGER,
    relative_transform JSONB,
    parent_component_code VARCHAR(64),
    position_json JSONB,
    rotation_json JSONB,
    scale_json JSONB,
    properties_json JSONB,
    override_json JSONB,
    construct_args_json JSONB,
    metadata_json JSONB,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creator VARCHAR(64),
    updater VARCHAR(64),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT uk_actor_instance_component_instance_code_component_code UNIQUE (instance_code, component_code),
    CONSTRAINT fk_actor_instance_component_actor_instance_id FOREIGN KEY (actor_instance_id) REFERENCES scene_platform.actor_instance (id)
);
CREATE INDEX IF NOT EXISTS idx_actor_instance_component_actor_instance_id ON scene_platform.actor_instance_component (actor_instance_id) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_actor_instance_component_actor_code ON scene_platform.actor_instance_component (actor_code) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_actor_instance_component_tenant_actor_instance_id ON scene_platform.actor_instance_component (tenant_id, actor_instance_id) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_actor_instance_component_tenant_instance_code ON scene_platform.actor_instance_component (tenant_id, instance_code) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_actor_instance_component_relative_transform ON scene_platform.actor_instance_component USING GIN (relative_transform);
CREATE INDEX IF NOT EXISTS idx_actor_instance_component_properties_json ON scene_platform.actor_instance_component USING GIN (properties_json);
CREATE INDEX IF NOT EXISTS idx_actor_instance_component_override_json ON scene_platform.actor_instance_component USING GIN (override_json);

CREATE TABLE IF NOT EXISTS scene_platform.scene_component (
    id BIGINT PRIMARY KEY,
    component_type VARCHAR(64) NOT NULL,
    scene_id BIGINT NOT NULL,
    component_name VARCHAR(128) NOT NULL,
    display_name VARCHAR(128),
    relative_transform JSONB,
    enabled BOOLEAN,
    visible BOOLEAN,
    render_order INTEGER,
    config_json JSONB,
    associated_actor_ids JSONB,
    metadata_json JSONB,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creator VARCHAR(64),
    updater VARCHAR(64),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT uk_scene_component_scene_id_component_name UNIQUE (scene_id, component_name),
    CONSTRAINT fk_scene_component_scene_id FOREIGN KEY (scene_id) REFERENCES scene_platform.scene (id)
);
CREATE INDEX IF NOT EXISTS idx_scene_component_scene_id ON scene_platform.scene_component (scene_id) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_scene_component_type ON scene_platform.scene_component (component_type) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_scene_component_tenant_scene_id ON scene_platform.scene_component (tenant_id, scene_id) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_scene_component_tenant_component_name ON scene_platform.scene_component (tenant_id, component_name) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_scene_component_config_json ON scene_platform.scene_component USING GIN (config_json);
CREATE INDEX IF NOT EXISTS idx_scene_component_associated_actor_ids ON scene_platform.scene_component USING GIN (associated_actor_ids);

CREATE TABLE IF NOT EXISTS scene_platform.scene_component_instance (
    id BIGINT PRIMARY KEY,
    scene_id BIGINT NOT NULL,
    scene_code VARCHAR(64),
    instance_code VARCHAR(64) NOT NULL,
    component_code VARCHAR(64) NOT NULL,
    enabled_flag BOOLEAN,
    config_json JSONB,
    metadata_json JSONB,
    sort_no INTEGER,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creator VARCHAR(64),
    updater VARCHAR(64),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT uk_scene_component_instance_scene_id_instance_code UNIQUE (scene_id, instance_code),
    CONSTRAINT fk_scene_component_instance_scene_id FOREIGN KEY (scene_id) REFERENCES scene_platform.scene (id)
);
CREATE INDEX IF NOT EXISTS idx_scene_component_instance_scene_code ON scene_platform.scene_component_instance (scene_code) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_scene_component_instance_component_code ON scene_platform.scene_component_instance (component_code) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_scene_component_instance_tenant_scene_id ON scene_platform.scene_component_instance (tenant_id, scene_id) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_scene_component_instance_tenant_instance_code ON scene_platform.scene_component_instance (tenant_id, instance_code) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_scene_component_instance_config_json ON scene_platform.scene_component_instance USING GIN (config_json);

CREATE TABLE IF NOT EXISTS scene_platform.scene_asset_binding (
    id BIGINT PRIMARY KEY,
    scene_id BIGINT NOT NULL,
    asset_id BIGINT NOT NULL,
    metadata_json JSONB,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uk_scene_asset_binding_scene_id_asset_id UNIQUE (scene_id, asset_id),
    CONSTRAINT fk_scene_asset_binding_scene_id FOREIGN KEY (scene_id) REFERENCES scene_platform.scene (id),
    CONSTRAINT fk_scene_asset_binding_asset_id FOREIGN KEY (asset_id) REFERENCES scene_platform.asset_resource (id)
);
CREATE INDEX IF NOT EXISTS idx_scene_asset_binding_scene_id ON scene_platform.scene_asset_binding (scene_id);
CREATE INDEX IF NOT EXISTS idx_scene_asset_binding_asset_id ON scene_platform.scene_asset_binding (asset_id);
CREATE INDEX IF NOT EXISTS idx_scene_asset_binding_tenant_scene_id ON scene_platform.scene_asset_binding (tenant_id, scene_id);
CREATE INDEX IF NOT EXISTS idx_scene_asset_binding_tenant_asset_id ON scene_platform.scene_asset_binding (tenant_id, asset_id);

CREATE TABLE IF NOT EXISTS scene_platform.geo_layer_config (
    id BIGINT PRIMARY KEY,
    scene_id BIGINT NOT NULL,
    layer_key VARCHAR(64) NOT NULL,
    layer_name VARCHAR(128),
    layer_type VARCHAR(64),
    provider_type VARCHAR(64),
    engine_profile VARCHAR(32),
    enabled_flag BOOLEAN,
    sort_no INTEGER,
    config_json JSONB,
    metadata_json JSONB,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creator VARCHAR(64),
    updater VARCHAR(64),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT uk_geo_layer_config_scene_id_layer_key UNIQUE (scene_id, layer_key),
    CONSTRAINT fk_geo_layer_config_scene_id FOREIGN KEY (scene_id) REFERENCES scene_platform.scene (id)
);
CREATE INDEX IF NOT EXISTS idx_geo_layer_config_scene_id ON scene_platform.geo_layer_config (scene_id) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_geo_layer_config_tenant_scene_id ON scene_platform.geo_layer_config (tenant_id, scene_id) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_geo_layer_config_tenant_layer_key ON scene_platform.geo_layer_config (tenant_id, layer_key) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_geo_layer_config_config_json ON scene_platform.geo_layer_config USING GIN (config_json);

CREATE TABLE IF NOT EXISTS scene_platform.coordinate_reference (
    id BIGINT PRIMARY KEY,
    scene_id BIGINT NOT NULL,
    origin_lng NUMERIC(18, 8),
    origin_lat NUMERIC(18, 8),
    origin_height NUMERIC(18, 8),
    geographic_crs_code VARCHAR(64),
    projected_crs_code VARCHAR(64),
    datum_code VARCHAR(64),
    ellipsoid_code VARCHAR(64),
    planet_shape VARCHAR(64),
    reference_frame_type VARCHAR(64),
    local_frame_type VARCHAR(64),
    engine_frame_type VARCHAR(64),
    axis_order VARCHAR(64),
    handedness VARCHAR(32),
    linear_unit VARCHAR(32),
    angular_unit VARCHAR(32),
    origin_projected_x NUMERIC(18, 8),
    origin_projected_y NUMERIC(18, 8),
    origin_projected_z NUMERIC(18, 8),
    origin_ecef_x NUMERIC(18, 8),
    origin_ecef_y NUMERIC(18, 8),
    origin_ecef_z NUMERIC(18, 8),
    origin_heading NUMERIC(18, 8),
    origin_pitch NUMERIC(18, 8),
    origin_roll NUMERIC(18, 8),
    transform_profile_code VARCHAR(64),
    transform_config_json JSONB,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creator VARCHAR(64),
    updater VARCHAR(64),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT uk_coordinate_reference_scene_id UNIQUE (scene_id),
    CONSTRAINT fk_coordinate_reference_scene_id FOREIGN KEY (scene_id) REFERENCES scene_platform.scene (id),
    CONSTRAINT fk_coordinate_reference_transform_profile_code FOREIGN KEY (transform_profile_code) REFERENCES scene_platform.coordinate_transform_profile (profile_code)
);
CREATE INDEX IF NOT EXISTS idx_coordinate_reference_transform_profile_code ON scene_platform.coordinate_reference (transform_profile_code) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_coordinate_reference_tenant_scene_id ON scene_platform.coordinate_reference (tenant_id, scene_id) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_coordinate_reference_transform_config_json ON scene_platform.coordinate_reference USING GIN (transform_config_json);

CREATE TABLE IF NOT EXISTS scene_platform.primitive_component (
    id BIGINT PRIMARY KEY,
    component_code VARCHAR(64) NOT NULL,
    display_name VARCHAR(128),
    collision_enabled VARCHAR(32),
    object_type VARCHAR(64),
    generate_overlap_events BOOLEAN DEFAULT TRUE,
    simulation_generates_hit_events BOOLEAN DEFAULT FALSE,
    can_character_step_up_on BOOLEAN DEFAULT FALSE,
    use_default_collision BOOLEAN DEFAULT TRUE,
    physics_material_json JSONB DEFAULT '{}'::jsonb,
    bounds_json JSONB DEFAULT '{}'::jsonb,
    collision_response_json JSONB DEFAULT '{}'::jsonb,
    metadata_json JSONB DEFAULT '{}'::jsonb,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creator VARCHAR(64),
    updater VARCHAR(64),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT uk_primitive_component_component_code UNIQUE (component_code),
    CONSTRAINT fk_primitive_component_component_code FOREIGN KEY (component_code) REFERENCES scene_platform.component (component_code)
);

CREATE TABLE IF NOT EXISTS scene_platform.mesh_component (
    id BIGINT PRIMARY KEY,
    component_code VARCHAR(64) NOT NULL,
    display_name VARCHAR(128),
    mesh_description TEXT,
    materials_json JSONB DEFAULT '{}'::jsonb,
    cast_shadow BOOLEAN DEFAULT TRUE,
    receive_shadow BOOLEAN DEFAULT TRUE,
    mobility VARCHAR(32) DEFAULT 'Movable',
    metadata_json JSONB DEFAULT '{}'::jsonb,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creator VARCHAR(64),
    updater VARCHAR(64),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT uk_mesh_component_component_code UNIQUE (component_code),
    CONSTRAINT fk_mesh_component_component_code FOREIGN KEY (component_code) REFERENCES scene_platform.component (component_code)
);

CREATE TABLE IF NOT EXISTS scene_platform.static_mesh_component (
    id BIGINT PRIMARY KEY,
    component_code VARCHAR(64) NOT NULL,
    display_name VARCHAR(128),
    mesh_code VARCHAR(64),
    materials_json JSONB DEFAULT '{}'::jsonb,
    cast_shadow BOOLEAN DEFAULT TRUE,
    receive_shadow BOOLEAN DEFAULT TRUE,
    generate_overlap_events BOOLEAN DEFAULT FALSE,
    mobility VARCHAR(32) DEFAULT 'Movable',
    metadata_json JSONB DEFAULT '{}'::jsonb,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creator VARCHAR(64),
    updater VARCHAR(64),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT uk_static_mesh_component_component_code UNIQUE (component_code),
    CONSTRAINT fk_static_mesh_component_component_code FOREIGN KEY (component_code) REFERENCES scene_platform.component (component_code)
);
CREATE INDEX IF NOT EXISTS idx_static_mesh_component_mesh_code ON scene_platform.static_mesh_component (mesh_code) WHERE deleted = FALSE;

CREATE TABLE IF NOT EXISTS scene_platform.skeletal_mesh_component (
    id BIGINT PRIMARY KEY,
    component_code VARCHAR(64) NOT NULL,
    display_name VARCHAR(128),
    skeleton_code VARCHAR(64),
    animation_blueprint_code VARCHAR(64),
    materials_json JSONB DEFAULT '{}'::jsonb,
    cast_shadow BOOLEAN DEFAULT TRUE,
    receive_shadow BOOLEAN DEFAULT TRUE,
    use_animation_blueprint BOOLEAN DEFAULT TRUE,
    metadata_json JSONB DEFAULT '{}'::jsonb,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creator VARCHAR(64),
    updater VARCHAR(64),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT uk_skeletal_mesh_component_component_code UNIQUE (component_code),
    CONSTRAINT fk_skeletal_mesh_component_component_code FOREIGN KEY (component_code) REFERENCES scene_platform.component (component_code)
);
CREATE INDEX IF NOT EXISTS idx_skeletal_mesh_component_skeleton_code ON scene_platform.skeletal_mesh_component (skeleton_code) WHERE deleted = FALSE;

CREATE TABLE IF NOT EXISTS scene_platform.audio_component (
    id BIGINT PRIMARY KEY,
    component_code VARCHAR(64) NOT NULL,
    display_name VARCHAR(128) DEFAULT 'Audio',
    sound_code VARCHAR(64),
    auto_activate BOOLEAN DEFAULT TRUE,
    allow_spatialisation BOOLEAN DEFAULT TRUE,
    stop_when_owner_destroyed BOOLEAN DEFAULT TRUE,
    volume_multiplier REAL DEFAULT 1.0,
    pitch_multiplier REAL DEFAULT 1.0,
    override_attenuation BOOLEAN DEFAULT FALSE,
    attenuation_json JSONB DEFAULT '{}'::jsonb,
    concurrency_json JSONB DEFAULT '{}'::jsonb,
    metadata_json JSONB DEFAULT '{}'::jsonb,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creator VARCHAR(64),
    updater VARCHAR(64),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT uk_audio_component_component_code UNIQUE (component_code),
    CONSTRAINT fk_audio_component_component_code FOREIGN KEY (component_code) REFERENCES scene_platform.component (component_code)
);
CREATE INDEX IF NOT EXISTS idx_audio_component_sound_code ON scene_platform.audio_component (sound_code) WHERE deleted = FALSE;

CREATE TABLE IF NOT EXISTS scene_platform.light_component (
    id BIGINT PRIMARY KEY,
    component_code VARCHAR(64) NOT NULL,
    display_name VARCHAR(128) DEFAULT 'Light',
    intensity REAL DEFAULT 5000.0,
    light_color VARCHAR(32) DEFAULT '#FFFFFF',
    cast_shadows BOOLEAN DEFAULT TRUE,
    mobility VARCHAR(32) DEFAULT 'Movable',
    metadata_json JSONB DEFAULT '{}'::jsonb,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creator VARCHAR(64),
    updater VARCHAR(64),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT uk_light_component_component_code UNIQUE (component_code),
    CONSTRAINT fk_light_component_component_code FOREIGN KEY (component_code) REFERENCES scene_platform.component (component_code)
);

CREATE TABLE IF NOT EXISTS scene_platform.camera_component (
    id BIGINT PRIMARY KEY,
    component_code VARCHAR(64) NOT NULL,
    display_name VARCHAR(128) DEFAULT 'Camera',
    field_of_view REAL DEFAULT 90.0,
    aspect_ratio REAL DEFAULT 1.7777778,
    near_clip_plane REAL DEFAULT 10.0,
    far_clip_plane REAL DEFAULT 10000.0,
    constrain_aspect_ratio BOOLEAN DEFAULT FALSE,
    auto_activate BOOLEAN DEFAULT TRUE,
    metadata_json JSONB DEFAULT '{}'::jsonb,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creator VARCHAR(64),
    updater VARCHAR(64),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT uk_camera_component_component_code UNIQUE (component_code),
    CONSTRAINT fk_camera_component_component_code FOREIGN KEY (component_code) REFERENCES scene_platform.component (component_code)
);

CREATE TABLE IF NOT EXISTS scene_platform.box_component (
    id BIGINT PRIMARY KEY,
    component_code VARCHAR(64) NOT NULL,
    display_name VARCHAR(128) DEFAULT 'Box',
    box_extent_x REAL DEFAULT 50.0,
    box_extent_y REAL DEFAULT 50.0,
    box_extent_z REAL DEFAULT 50.0,
    collision_enabled VARCHAR(32) DEFAULT 'QueryAndPhysics',
    generate_overlap_events BOOLEAN DEFAULT TRUE,
    metadata_json JSONB DEFAULT '{}'::jsonb,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creator VARCHAR(64),
    updater VARCHAR(64),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT uk_box_component_component_code UNIQUE (component_code),
    CONSTRAINT fk_box_component_component_code FOREIGN KEY (component_code) REFERENCES scene_platform.component (component_code)
);

CREATE TABLE IF NOT EXISTS scene_platform.sphere_component (
    id BIGINT PRIMARY KEY,
    component_code VARCHAR(64) NOT NULL,
    display_name VARCHAR(128) DEFAULT 'Sphere',
    sphere_radius REAL DEFAULT 32.0,
    collision_enabled VARCHAR(32) DEFAULT 'QueryAndPhysics',
    generate_overlap_events BOOLEAN DEFAULT TRUE,
    metadata_json JSONB DEFAULT '{}'::jsonb,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creator VARCHAR(64),
    updater VARCHAR(64),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT uk_sphere_component_component_code UNIQUE (component_code),
    CONSTRAINT fk_sphere_component_component_code FOREIGN KEY (component_code) REFERENCES scene_platform.component (component_code)
);

CREATE TABLE IF NOT EXISTS scene_platform.capsule_component (
    id BIGINT PRIMARY KEY,
    component_code VARCHAR(64) NOT NULL,
    display_name VARCHAR(128) DEFAULT 'Capsule',
    capsule_radius REAL DEFAULT 42.0,
    capsule_half_height REAL DEFAULT 96.0,
    collision_enabled VARCHAR(32) DEFAULT 'QueryAndPhysics',
    generate_overlap_events BOOLEAN DEFAULT TRUE,
    metadata_json JSONB DEFAULT '{}'::jsonb,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creator VARCHAR(64),
    updater VARCHAR(64),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT uk_capsule_component_component_code UNIQUE (component_code),
    CONSTRAINT fk_capsule_component_component_code FOREIGN KEY (component_code) REFERENCES scene_platform.component (component_code)
);

CREATE TABLE IF NOT EXISTS scene_platform.widget_component (
    id BIGINT PRIMARY KEY,
    component_code VARCHAR(64) NOT NULL,
    display_name VARCHAR(128) DEFAULT 'Widget',
    widget_code VARCHAR(64),
    ui_config_json JSONB DEFAULT '{}'::jsonb,
    draw_at_desired_size BOOLEAN DEFAULT TRUE,
    receive_hardware_input BOOLEAN DEFAULT FALSE,
    metadata_json JSONB DEFAULT '{}'::jsonb,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creator VARCHAR(64),
    updater VARCHAR(64),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT uk_widget_component_component_code UNIQUE (component_code),
    CONSTRAINT fk_widget_component_component_code FOREIGN KEY (component_code) REFERENCES scene_platform.component (component_code)
);
CREATE INDEX IF NOT EXISTS idx_widget_component_widget_code ON scene_platform.widget_component (widget_code) WHERE deleted = FALSE;

CREATE TABLE IF NOT EXISTS scene_platform.spline_component (
    id BIGINT PRIMARY KEY,
    component_code VARCHAR(64) NOT NULL,
    display_name VARCHAR(128) DEFAULT 'Spline',
    spline_points_json JSONB DEFAULT '[]'::jsonb,
    closed_loop BOOLEAN DEFAULT FALSE,
    mobility VARCHAR(32) DEFAULT 'Movable',
    metadata_json JSONB DEFAULT '{}'::jsonb,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creator VARCHAR(64),
    updater VARCHAR(64),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT uk_spline_component_component_code UNIQUE (component_code),
    CONSTRAINT fk_spline_component_component_code FOREIGN KEY (component_code) REFERENCES scene_platform.component (component_code)
);
CREATE INDEX IF NOT EXISTS idx_spline_component_points_json ON scene_platform.spline_component USING GIN (spline_points_json);

CREATE TABLE IF NOT EXISTS scene_platform.spline_mesh_component (
    id BIGINT PRIMARY KEY,
    component_code VARCHAR(64) NOT NULL,
    display_name VARCHAR(128) DEFAULT 'Spline Mesh',
    source_spline_code VARCHAR(64),
    mesh_code VARCHAR(64),
    cast_shadow BOOLEAN DEFAULT TRUE,
    receive_shadow BOOLEAN DEFAULT TRUE,
    metadata_json JSONB DEFAULT '{}'::jsonb,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creator VARCHAR(64),
    updater VARCHAR(64),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT uk_spline_mesh_component_component_code UNIQUE (component_code),
    CONSTRAINT fk_spline_mesh_component_component_code FOREIGN KEY (component_code) REFERENCES scene_platform.component (component_code)
);
CREATE INDEX IF NOT EXISTS idx_spline_mesh_component_source_spline_code ON scene_platform.spline_mesh_component (source_spline_code) WHERE deleted = FALSE;

CREATE TABLE IF NOT EXISTS scene_platform.timeline_component (
    id BIGINT PRIMARY KEY,
    component_code VARCHAR(64) NOT NULL,
    display_name VARCHAR(128) DEFAULT 'Timeline',
    timeline_config_json JSONB DEFAULT '{}'::jsonb,
    auto_play BOOLEAN DEFAULT FALSE,
    loop BOOLEAN DEFAULT FALSE,
    metadata_json JSONB DEFAULT '{}'::jsonb,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creator VARCHAR(64),
    updater VARCHAR(64),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT uk_timeline_component_component_code UNIQUE (component_code),
    CONSTRAINT fk_timeline_component_component_code FOREIGN KEY (component_code) REFERENCES scene_platform.component (component_code)
);
CREATE INDEX IF NOT EXISTS idx_timeline_component_config_json ON scene_platform.timeline_component USING GIN (timeline_config_json);

CREATE TABLE IF NOT EXISTS scene_platform.child_actor_component (
    id BIGINT PRIMARY KEY,
    component_code VARCHAR(64) NOT NULL,
    display_name VARCHAR(128) DEFAULT 'Child Actor',
    child_actor_code VARCHAR(64),
    inherit_transform BOOLEAN DEFAULT TRUE,
    child_actor_editable BOOLEAN DEFAULT FALSE,
    metadata_json JSONB DEFAULT '{}'::jsonb,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creator VARCHAR(64),
    updater VARCHAR(64),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT uk_child_actor_component_component_code UNIQUE (component_code),
    CONSTRAINT fk_child_actor_component_component_code FOREIGN KEY (component_code) REFERENCES scene_platform.component (component_code)
);
CREATE INDEX IF NOT EXISTS idx_child_actor_component_child_actor_code ON scene_platform.child_actor_component (child_actor_code) WHERE deleted = FALSE;

COMMENT ON SCHEMA scene_platform IS '场景平台模块独立 schema';
COMMENT ON TABLE scene_platform.scene IS '场景主表';
COMMENT ON TABLE scene_platform.actor IS 'Actor 模板表';
COMMENT ON TABLE scene_platform.component IS '组件模板表';
COMMENT ON TABLE scene_platform.actor_instance IS 'Actor 实例表';
COMMENT ON TABLE scene_platform.actor_instance_component IS 'Actor 实例组件表';
COMMENT ON TABLE scene_platform.scene_layer IS '场景图层表';
COMMENT ON TABLE scene_platform.scene_component IS '场景级组件表';
COMMENT ON TABLE scene_platform.scene_component_instance IS '场景组件实例表';
COMMENT ON TABLE scene_platform.scene_asset_binding IS '场景资源绑定表';
COMMENT ON TABLE scene_platform.asset_resource IS '资源台账表';
COMMENT ON TABLE scene_platform.geo_layer_config IS 'GIS 图层配置表';
COMMENT ON TABLE scene_platform.coordinate_reference IS '场景坐标参考表';
COMMENT ON TABLE scene_platform.coordinate_transform_profile IS '坐标转换配置表';
COMMENT ON TABLE scene_platform.coordinate_crs_catalog IS '坐标系目录表';
COMMENT ON TABLE scene_platform.primitive_component IS '基础碰撞组件表';
COMMENT ON TABLE scene_platform.mesh_component IS '网格组件基表';
COMMENT ON TABLE scene_platform.static_mesh_component IS '静态网格组件表';
COMMENT ON TABLE scene_platform.skeletal_mesh_component IS '骨骼网格组件表';
COMMENT ON TABLE scene_platform.audio_component IS '音频组件表';
COMMENT ON TABLE scene_platform.light_component IS '灯光组件表';
COMMENT ON TABLE scene_platform.camera_component IS '相机组件表';
COMMENT ON TABLE scene_platform.box_component IS '盒体组件表';
COMMENT ON TABLE scene_platform.sphere_component IS '球体组件表';
COMMENT ON TABLE scene_platform.capsule_component IS '胶囊体组件表';
COMMENT ON TABLE scene_platform.widget_component IS 'Widget 组件表';
COMMENT ON TABLE scene_platform.spline_component IS 'Spline 组件表';
COMMENT ON TABLE scene_platform.spline_mesh_component IS 'Spline Mesh 组件表';
COMMENT ON TABLE scene_platform.timeline_component IS '时间轴组件表';
COMMENT ON TABLE scene_platform.child_actor_component IS '子 Actor 组件表';

COMMENT ON COLUMN scene_platform.scene.id IS '场景主键';
COMMENT ON COLUMN scene_platform.scene.scene_code IS '场景编码';
COMMENT ON COLUMN scene_platform.scene.scene_name IS '场景名称';
COMMENT ON COLUMN scene_platform.scene.scene_type IS '场景类型';
COMMENT ON COLUMN scene_platform.scene.engine_profile IS '引擎配置';
COMMENT ON COLUMN scene_platform.scene.capabilities_json IS '场景能力配置 JSON';
COMMENT ON COLUMN scene_platform.scene.layer_config_json IS '图层配置 JSON';
COMMENT ON COLUMN scene_platform.scene.default_viewpoint_json IS '默认视角配置 JSON';
COMMENT ON COLUMN scene_platform.scene.project_code IS '所属项目编码';
COMMENT ON COLUMN scene_platform.scene.business_key IS '业务键';
COMMENT ON COLUMN scene_platform.scene.status IS '状态';
COMMENT ON COLUMN scene_platform.scene.publish_status IS '发布状态';
COMMENT ON COLUMN scene_platform.scene.published_at IS '发布时间';
COMMENT ON COLUMN scene_platform.scene.published_by IS '发布人';
COMMENT ON COLUMN scene_platform.scene.actor_instance_codes_json IS '关联 Actor 实例编码列表';
COMMENT ON COLUMN scene_platform.scene.actor_states_json IS '运行时状态快照 JSON';
COMMENT ON COLUMN scene_platform.scene.tenant_id IS '租户编号';
COMMENT ON COLUMN scene_platform.scene.create_time IS '创建时间';
COMMENT ON COLUMN scene_platform.scene.update_time IS '更新时间';
COMMENT ON COLUMN scene_platform.scene.creator IS '创建者';
COMMENT ON COLUMN scene_platform.scene.updater IS '更新者';
COMMENT ON COLUMN scene_platform.scene.deleted IS '是否删除';

COMMENT ON COLUMN scene_platform.actor.id IS 'Actor 主键';
COMMENT ON COLUMN scene_platform.actor.actor_code IS 'Actor 编码';
COMMENT ON COLUMN scene_platform.actor.actor_name IS 'Actor 名称';
COMMENT ON COLUMN scene_platform.actor.actor_class IS 'Actor 类名';
COMMENT ON COLUMN scene_platform.actor.parent_actor_code IS '父 Actor 编码';
COMMENT ON COLUMN scene_platform.actor.actor_category IS 'Actor 分类';
COMMENT ON COLUMN scene_platform.actor.engine_profile IS '引擎配置';
COMMENT ON COLUMN scene_platform.actor.abstract_flag IS '是否抽象模板';
COMMENT ON COLUMN scene_platform.actor.lifecycle_status IS '生命周期状态';
COMMENT ON COLUMN scene_platform.actor.component_tree IS '默认组件树 JSON';
COMMENT ON COLUMN scene_platform.actor.metadata_json IS '扩展元数据';

COMMENT ON COLUMN scene_platform.actor_instance.id IS 'Actor 实例主键';
COMMENT ON COLUMN scene_platform.actor_instance.scene_id IS '所属场景 ID';
COMMENT ON COLUMN scene_platform.actor_instance.actor_code IS 'Actor 编码';
COMMENT ON COLUMN scene_platform.actor_instance.instance_code IS '实例编码';
COMMENT ON COLUMN scene_platform.actor_instance.instance_name IS '实例名称';
COMMENT ON COLUMN scene_platform.actor_instance.parent_instance_code IS '父实例编码';
COMMENT ON COLUMN scene_platform.actor_instance.instance_status IS '实例状态';
COMMENT ON COLUMN scene_platform.actor_instance.visible_flag IS '是否可见';
COMMENT ON COLUMN scene_platform.actor_instance.version_no IS '版本号';
COMMENT ON COLUMN scene_platform.actor_instance.transform IS '变换信息 JSON';
COMMENT ON COLUMN scene_platform.actor_instance.metadata_json IS '扩展元数据';
COMMENT ON COLUMN scene_platform.actor_instance.path IS '层级路径';
COMMENT ON COLUMN scene_platform.actor_instance.layer_keys IS '所属图层编码列表 JSON';
COMMENT ON COLUMN scene_platform.actor_instance.position_timestamp IS '位置时间戳';
COMMENT ON COLUMN scene_platform.actor_instance.animation_enabled IS '是否启用动画';
COMMENT ON COLUMN scene_platform.actor_instance.animation_duration IS '动画时长毫秒';
COMMENT ON COLUMN scene_platform.actor_instance.animation_type IS '动画类型';
COMMENT ON COLUMN scene_platform.actor_instance.tenant_id IS '租户编号';

COMMENT ON COLUMN scene_platform.scene_layer.id IS '图层主键';
COMMENT ON COLUMN scene_platform.scene_layer.scene_id IS '所属场景 ID';
COMMENT ON COLUMN scene_platform.scene_layer.layer_code IS '图层编码';
COMMENT ON COLUMN scene_platform.scene_layer.layer_name IS '图层名称';
COMMENT ON COLUMN scene_platform.scene_layer.layer_display_name IS '图层显示名称';
COMMENT ON COLUMN scene_platform.scene_layer.layer_type IS '图层类型';
COMMENT ON COLUMN scene_platform.scene_layer.layer_config_json IS '图层配置 JSON';
COMMENT ON COLUMN scene_platform.scene_layer.visible_flag IS '是否可见';
COMMENT ON COLUMN scene_platform.scene_layer.interactive_flag IS '是否可交互';
COMMENT ON COLUMN scene_platform.scene_layer.sort_no IS '排序号';
COMMENT ON COLUMN scene_platform.scene_layer.instance_ids_json IS '实例 ID 列表 JSON';
COMMENT ON COLUMN scene_platform.scene_layer.filter_rules_json IS '筛选规则 JSON';
COMMENT ON COLUMN scene_platform.scene_layer.tenant_id IS '租户编号';

COMMENT ON COLUMN scene_platform.actor_instance_component.id IS '实例组件主键';
COMMENT ON COLUMN scene_platform.actor_instance_component.actor_instance_id IS '所属 Actor 实例 ID';
COMMENT ON COLUMN scene_platform.actor_instance_component.actor_code IS 'Actor 编码';
COMMENT ON COLUMN scene_platform.actor_instance_component.instance_code IS '实例编码';
COMMENT ON COLUMN scene_platform.actor_instance_component.component_code IS '组件编码';
COMMENT ON COLUMN scene_platform.actor_instance_component.component_type_name IS '组件类型名称';
COMMENT ON COLUMN scene_platform.actor_instance_component.enabled_flag IS '是否启用';
COMMENT ON COLUMN scene_platform.actor_instance_component.sort_no IS '排序号';
COMMENT ON COLUMN scene_platform.actor_instance_component.relative_transform IS '相对变换 JSON';
COMMENT ON COLUMN scene_platform.actor_instance_component.parent_component_code IS '父组件编码';
COMMENT ON COLUMN scene_platform.actor_instance_component.position_json IS '位置 JSON';
COMMENT ON COLUMN scene_platform.actor_instance_component.rotation_json IS '旋转 JSON';
COMMENT ON COLUMN scene_platform.actor_instance_component.scale_json IS '缩放 JSON';
COMMENT ON COLUMN scene_platform.actor_instance_component.properties_json IS '模板属性 JSON';
COMMENT ON COLUMN scene_platform.actor_instance_component.override_json IS '覆盖属性 JSON';
COMMENT ON COLUMN scene_platform.actor_instance_component.construct_args_json IS '构造参数 JSON';
COMMENT ON COLUMN scene_platform.actor_instance_component.metadata_json IS '扩展元数据';
COMMENT ON COLUMN scene_platform.actor_instance_component.tenant_id IS '租户编号';

COMMENT ON COLUMN scene_platform.scene_component.id IS '场景组件主键';
COMMENT ON COLUMN scene_platform.scene_component.component_type IS '组件类型';
COMMENT ON COLUMN scene_platform.scene_component.scene_id IS '所属场景 ID';
COMMENT ON COLUMN scene_platform.scene_component.component_name IS '组件实例名称';
COMMENT ON COLUMN scene_platform.scene_component.display_name IS '显示名称';
COMMENT ON COLUMN scene_platform.scene_component.relative_transform IS '相对变换 JSON';
COMMENT ON COLUMN scene_platform.scene_component.enabled IS '是否启用';
COMMENT ON COLUMN scene_platform.scene_component.visible IS '是否可见';
COMMENT ON COLUMN scene_platform.scene_component.render_order IS '渲染顺序';
COMMENT ON COLUMN scene_platform.scene_component.config_json IS '组件配置 JSON';
COMMENT ON COLUMN scene_platform.scene_component.associated_actor_ids IS '关联 Actor ID 列表 JSON';
COMMENT ON COLUMN scene_platform.scene_component.metadata_json IS '扩展元数据';
COMMENT ON COLUMN scene_platform.scene_component.tenant_id IS '租户编号';

COMMENT ON COLUMN scene_platform.scene_component_instance.id IS '场景组件实例主键';
COMMENT ON COLUMN scene_platform.scene_component_instance.scene_id IS '所属场景 ID';
COMMENT ON COLUMN scene_platform.scene_component_instance.scene_code IS '场景编码';
COMMENT ON COLUMN scene_platform.scene_component_instance.instance_code IS '实例编码';
COMMENT ON COLUMN scene_platform.scene_component_instance.component_code IS '组件编码';
COMMENT ON COLUMN scene_platform.scene_component_instance.enabled_flag IS '是否启用';
COMMENT ON COLUMN scene_platform.scene_component_instance.config_json IS '实例配置 JSON';
COMMENT ON COLUMN scene_platform.scene_component_instance.metadata_json IS '扩展元数据';
COMMENT ON COLUMN scene_platform.scene_component_instance.sort_no IS '排序号';
COMMENT ON COLUMN scene_platform.scene_component_instance.tenant_id IS '租户编号';

COMMENT ON COLUMN scene_platform.scene_asset_binding.id IS '场景资源绑定主键';
COMMENT ON COLUMN scene_platform.scene_asset_binding.scene_id IS '所属场景 ID';
COMMENT ON COLUMN scene_platform.scene_asset_binding.asset_id IS '资源 ID';
COMMENT ON COLUMN scene_platform.scene_asset_binding.metadata_json IS '扩展元数据';
COMMENT ON COLUMN scene_platform.scene_asset_binding.tenant_id IS '租户编号';

COMMENT ON COLUMN scene_platform.geo_layer_config.id IS 'GIS 图层配置主键';
COMMENT ON COLUMN scene_platform.geo_layer_config.scene_id IS '所属场景 ID';
COMMENT ON COLUMN scene_platform.geo_layer_config.layer_key IS '图层键';
COMMENT ON COLUMN scene_platform.geo_layer_config.layer_name IS '图层名称';
COMMENT ON COLUMN scene_platform.geo_layer_config.layer_type IS '图层类型';
COMMENT ON COLUMN scene_platform.geo_layer_config.provider_type IS '提供者类型';
COMMENT ON COLUMN scene_platform.geo_layer_config.engine_profile IS '引擎配置';
COMMENT ON COLUMN scene_platform.geo_layer_config.enabled_flag IS '是否启用';
COMMENT ON COLUMN scene_platform.geo_layer_config.sort_no IS '排序号';
COMMENT ON COLUMN scene_platform.geo_layer_config.config_json IS '图层配置 JSON';
COMMENT ON COLUMN scene_platform.geo_layer_config.metadata_json IS '扩展元数据';
COMMENT ON COLUMN scene_platform.geo_layer_config.tenant_id IS '租户编号';

COMMENT ON COLUMN scene_platform.coordinate_reference.id IS '坐标参考主键';
COMMENT ON COLUMN scene_platform.coordinate_reference.scene_id IS '所属场景 ID';
COMMENT ON COLUMN scene_platform.coordinate_reference.origin_lng IS '原点经度';
COMMENT ON COLUMN scene_platform.coordinate_reference.origin_lat IS '原点纬度';
COMMENT ON COLUMN scene_platform.coordinate_reference.origin_height IS '原点高程';
COMMENT ON COLUMN scene_platform.coordinate_reference.geographic_crs_code IS '地理坐标系编码';
COMMENT ON COLUMN scene_platform.coordinate_reference.projected_crs_code IS '投影坐标系编码';
COMMENT ON COLUMN scene_platform.coordinate_reference.datum_code IS '基准面编码';
COMMENT ON COLUMN scene_platform.coordinate_reference.ellipsoid_code IS '椭球编码';
COMMENT ON COLUMN scene_platform.coordinate_reference.planet_shape IS '星体形状';
COMMENT ON COLUMN scene_platform.coordinate_reference.reference_frame_type IS '参考框架类型';
COMMENT ON COLUMN scene_platform.coordinate_reference.local_frame_type IS '本地坐标框架类型';
COMMENT ON COLUMN scene_platform.coordinate_reference.engine_frame_type IS '引擎坐标框架类型';
COMMENT ON COLUMN scene_platform.coordinate_reference.axis_order IS '轴顺序';
COMMENT ON COLUMN scene_platform.coordinate_reference.handedness IS '左右手系';
COMMENT ON COLUMN scene_platform.coordinate_reference.linear_unit IS '线性单位';
COMMENT ON COLUMN scene_platform.coordinate_reference.angular_unit IS '角度单位';
COMMENT ON COLUMN scene_platform.coordinate_reference.origin_projected_x IS '投影原点 X';
COMMENT ON COLUMN scene_platform.coordinate_reference.origin_projected_y IS '投影原点 Y';
COMMENT ON COLUMN scene_platform.coordinate_reference.origin_projected_z IS '投影原点 Z';
COMMENT ON COLUMN scene_platform.coordinate_reference.origin_ecef_x IS 'ECEF 原点 X';
COMMENT ON COLUMN scene_platform.coordinate_reference.origin_ecef_y IS 'ECEF 原点 Y';
COMMENT ON COLUMN scene_platform.coordinate_reference.origin_ecef_z IS 'ECEF 原点 Z';
COMMENT ON COLUMN scene_platform.coordinate_reference.origin_heading IS '原点 Heading';
COMMENT ON COLUMN scene_platform.coordinate_reference.origin_pitch IS '原点 Pitch';
COMMENT ON COLUMN scene_platform.coordinate_reference.origin_roll IS '原点 Roll';
COMMENT ON COLUMN scene_platform.coordinate_reference.transform_profile_code IS '坐标转换配置编码';
COMMENT ON COLUMN scene_platform.coordinate_reference.transform_config_json IS '转换配置 JSON';
COMMENT ON COLUMN scene_platform.coordinate_reference.tenant_id IS '租户编号';
