-- V15: 标准点位存储表 ent_point；路线业务编码索引
-- 关联设计：docs/superpowers/specs/2026-07-14-legacy-site-import-design.md
-- 巡检域入口 patrol_point / patrol_route 复用 ent_point / ent_route（SCOPED），不另建 ent_patrol_*。

CREATE SEQUENCE IF NOT EXISTS dynamicbusiness.ent_point_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE IF NOT EXISTS dynamicbusiness.ent_point (
    id              bigint DEFAULT nextval('dynamicbusiness.ent_point_id_seq'::regclass) NOT NULL,
    tenant_id       bigint DEFAULT 0 NOT NULL,
    entity_type_code character varying(64) DEFAULT 'point',
    model_id        bigint NOT NULL,
    name            character varying(255) NOT NULL,
    code            character varying(100) NOT NULL,
    status          integer DEFAULT 1,
    parent_id       bigint DEFAULT 0,
    attrs           jsonb DEFAULT '{}'::jsonb,
    custom_fields   jsonb DEFAULT '{}'::jsonb,
    creator         character varying(64),
    create_time     timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updater         character varying(64),
    update_time     timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    deleted         boolean DEFAULT false,
    tree_path       character varying(500),
    sort            integer DEFAULT 0,
    PRIMARY KEY (id)
);

COMMENT ON TABLE dynamicbusiness.ent_point IS '标准点位存储；巡检点为 SCOPED 域入口（code=patrol_point）';
COMMENT ON COLUMN dynamicbusiness.ent_point.code IS '跨平台业务编码';

CREATE INDEX IF NOT EXISTS idx_ent_point_tenant
    ON dynamicbusiness.ent_point USING btree (tenant_id) WHERE deleted = false;

CREATE INDEX IF NOT EXISTS idx_ent_point_model
    ON dynamicbusiness.ent_point USING btree (model_id) WHERE deleted = false;

CREATE UNIQUE INDEX IF NOT EXISTS uk_ent_point_code_tenant
    ON dynamicbusiness.ent_point USING btree (code, tenant_id)
    WHERE deleted = false AND code IS NOT NULL;

-- 路线：跨平台业务编码检索（表已在 V1 存在）
CREATE INDEX IF NOT EXISTS idx_ent_route_tenant
    ON dynamicbusiness.ent_route USING btree (tenant_id) WHERE deleted = false;

CREATE UNIQUE INDEX IF NOT EXISTS uk_ent_route_code_tenant
    ON dynamicbusiness.ent_route USING btree (code, tenant_id)
    WHERE deleted = false AND code IS NOT NULL;
