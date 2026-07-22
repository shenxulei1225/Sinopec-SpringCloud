-- V17: 三维场景 / 三维摆放专用表（动态业务收编阶段 1）
-- 设计：docs/superpowers/specs/2026-07-22-dynamic-business-scene-3d-design.md
-- 计划：docs/superpowers/plans/2026-07-22-dynamic-business-scene-3d-phase1.md

CREATE SEQUENCE IF NOT EXISTS dynamicbusiness.ent_scene_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE IF NOT EXISTS dynamicbusiness.ent_scene (
    id                   bigint DEFAULT nextval('dynamicbusiness.ent_scene_id_seq'::regclass) NOT NULL,
    tenant_id            bigint DEFAULT 0 NOT NULL,
    entity_type_code     character varying(64) DEFAULT 'scene',
    model_id             bigint NOT NULL,
    name                 character varying(255) NOT NULL,
    code                 character varying(100) NOT NULL,
    status               integer DEFAULT 1,
    facility_id          bigint,
    scene_code           character varying(64),
    origin_lng           numeric(18, 8),
    origin_lat           numeric(18, 8),
    origin_height        numeric(18, 8),
    origin_height_source character varying(32),
    publish_status       character varying(32),
    custom_fields        jsonb DEFAULT '{}'::jsonb,
    creator              character varying(64),
    create_time          timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updater              character varying(64),
    update_time          timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    deleted              boolean DEFAULT false,
    sort                 integer DEFAULT 0,
    PRIMARY KEY (id)
);

COMMENT ON TABLE dynamicbusiness.ent_scene IS '三维场景实体（分类即实体配对）；对账 scene_platform.scene.scene_code';
COMMENT ON COLUMN dynamicbusiness.ent_scene.scene_code IS '与场景平台 scene.scene_code 对账';
COMMENT ON COLUMN dynamicbusiness.ent_scene.facility_id IS '绑定站场 ent_facility.id';

CREATE INDEX IF NOT EXISTS idx_ent_scene_tenant
    ON dynamicbusiness.ent_scene USING btree (tenant_id) WHERE deleted = false;

CREATE INDEX IF NOT EXISTS idx_ent_scene_facility
    ON dynamicbusiness.ent_scene USING btree (facility_id) WHERE deleted = false;

CREATE INDEX IF NOT EXISTS idx_ent_scene_scene_code
    ON dynamicbusiness.ent_scene USING btree (scene_code) WHERE deleted = false;

CREATE UNIQUE INDEX IF NOT EXISTS uk_ent_scene_code_tenant
    ON dynamicbusiness.ent_scene USING btree (code, tenant_id)
    WHERE deleted = false AND code IS NOT NULL;

CREATE SEQUENCE IF NOT EXISTS dynamicbusiness.ent_scene_placement_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE IF NOT EXISTS dynamicbusiness.ent_scene_placement (
    id                    bigint DEFAULT nextval('dynamicbusiness.ent_scene_placement_id_seq'::regclass) NOT NULL,
    tenant_id             bigint DEFAULT 0 NOT NULL,
    entity_type_code      character varying(64) DEFAULT 'scene_placement',
    model_id              bigint NOT NULL,
    name                  character varying(255) NOT NULL,
    code                  character varying(100) NOT NULL,
    status                integer DEFAULT 1,
    scene_id              bigint,
    render_asset_code     character varying(128),
    transform             jsonb DEFAULT '{}'::jsonb,
    material_override     jsonb DEFAULT '{}'::jsonb,
    metadata_json         jsonb DEFAULT '{}'::jsonb,
    visible_flag          boolean DEFAULT true,
    parent_placement_code character varying(100),
    gps_lng               numeric(18, 8),
    gps_lat               numeric(18, 8),
    gps_height            numeric(18, 8),
    gps_height_source     character varying(32),
    custom_fields         jsonb DEFAULT '{}'::jsonb,
    creator               character varying(64),
    create_time           timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updater               character varying(64),
    update_time           timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    deleted               boolean DEFAULT false,
    sort                  integer DEFAULT 0,
    PRIMARY KEY (id)
);

COMMENT ON TABLE dynamicbusiness.ent_scene_placement IS '三维摆放实体；code 对账 actor_instance.instance_code';
COMMENT ON COLUMN dynamicbusiness.ent_scene_placement.code IS '稳定业务编码，对账 instance_code';
COMMENT ON COLUMN dynamicbusiness.ent_scene_placement.scene_id IS '所属三维场景 ent_scene.id';
COMMENT ON COLUMN dynamicbusiness.ent_scene_placement.transform IS '位置/旋转/缩放 JSON';

CREATE INDEX IF NOT EXISTS idx_ent_scene_placement_tenant
    ON dynamicbusiness.ent_scene_placement USING btree (tenant_id) WHERE deleted = false;

CREATE INDEX IF NOT EXISTS idx_ent_scene_placement_scene
    ON dynamicbusiness.ent_scene_placement USING btree (scene_id) WHERE deleted = false;

CREATE INDEX IF NOT EXISTS idx_ent_scene_placement_model
    ON dynamicbusiness.ent_scene_placement USING btree (model_id) WHERE deleted = false;

CREATE UNIQUE INDEX IF NOT EXISTS uk_ent_scene_placement_code_tenant
    ON dynamicbusiness.ent_scene_placement USING btree (code, tenant_id)
    WHERE deleted = false AND code IS NOT NULL;
