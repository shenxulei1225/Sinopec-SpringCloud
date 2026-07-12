-- 巡检管理实体专用表：patrol_schedule / patrol_object / patrol_point
-- 与 ent_task、ent_inspection_item 同形；业务字段存 custom_fields（JSON 文本）

SET search_path TO dynamicbusiness;

CREATE SEQUENCE IF NOT EXISTS dynamicbusiness.ent_patrol_schedule_id_seq
    START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;

CREATE TABLE IF NOT EXISTS dynamicbusiness.ent_patrol_schedule (
    id bigint DEFAULT nextval('dynamicbusiness.ent_patrol_schedule_id_seq'::regclass) NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL,
    entity_type_code character varying(64),
    model_id bigint NOT NULL,
    name character varying(255) NOT NULL,
    code character varying(100),
    status integer DEFAULT 1,
    area_id bigint,
    parent_id bigint DEFAULT 0,
    attrs jsonb DEFAULT '{}'::jsonb,
    custom_fields text,
    creator character varying(64),
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updater character varying(64),
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    deleted boolean DEFAULT false,
    tree_path character varying(500),
    sort integer DEFAULT 0
);

COMMENT ON TABLE dynamicbusiness.ent_patrol_schedule IS '巡检排期（含模板）；entityTypeCode=patrol_schedule';

CREATE SEQUENCE IF NOT EXISTS dynamicbusiness.ent_patrol_object_id_seq
    START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;

CREATE TABLE IF NOT EXISTS dynamicbusiness.ent_patrol_object (
    id bigint DEFAULT nextval('dynamicbusiness.ent_patrol_object_id_seq'::regclass) NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL,
    entity_type_code character varying(64),
    model_id bigint NOT NULL,
    name character varying(255) NOT NULL,
    code character varying(100),
    status integer DEFAULT 1,
    area_id bigint,
    parent_id bigint DEFAULT 0,
    attrs jsonb DEFAULT '{}'::jsonb,
    custom_fields text,
    creator character varying(64),
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updater character varying(64),
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    deleted boolean DEFAULT false,
    tree_path character varying(500),
    sort integer DEFAULT 0
);

COMMENT ON TABLE dynamicbusiness.ent_patrol_object IS '巡检对象（含模板）；entityTypeCode=patrol_object';

CREATE SEQUENCE IF NOT EXISTS dynamicbusiness.ent_patrol_point_id_seq
    START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;

CREATE TABLE IF NOT EXISTS dynamicbusiness.ent_patrol_point (
    id bigint DEFAULT nextval('dynamicbusiness.ent_patrol_point_id_seq'::regclass) NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL,
    entity_type_code character varying(64),
    model_id bigint NOT NULL,
    name character varying(255) NOT NULL,
    code character varying(100),
    status integer DEFAULT 1,
    area_id bigint,
    parent_id bigint DEFAULT 0,
    attrs jsonb DEFAULT '{}'::jsonb,
    custom_fields text,
    creator character varying(64),
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updater character varying(64),
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    deleted boolean DEFAULT false,
    tree_path character varying(500),
    sort integer DEFAULT 0
);

COMMENT ON TABLE dynamicbusiness.ent_patrol_point IS '巡检点（含模板）；entityTypeCode=patrol_point；stop_ids 为 topology Station nodeId 列表';

ALTER TABLE ONLY dynamicbusiness.ent_patrol_schedule
    DROP CONSTRAINT IF EXISTS ent_patrol_schedule_pkey;
ALTER TABLE ONLY dynamicbusiness.ent_patrol_schedule
    ADD CONSTRAINT ent_patrol_schedule_pkey PRIMARY KEY (id);

ALTER TABLE ONLY dynamicbusiness.ent_patrol_object
    DROP CONSTRAINT IF EXISTS ent_patrol_object_pkey;
ALTER TABLE ONLY dynamicbusiness.ent_patrol_object
    ADD CONSTRAINT ent_patrol_object_pkey PRIMARY KEY (id);

ALTER TABLE ONLY dynamicbusiness.ent_patrol_point
    DROP CONSTRAINT IF EXISTS ent_patrol_point_pkey;
ALTER TABLE ONLY dynamicbusiness.ent_patrol_point
    ADD CONSTRAINT ent_patrol_point_pkey PRIMARY KEY (id);

CREATE INDEX IF NOT EXISTS ent_patrol_schedule_tree_path_idx
    ON dynamicbusiness.ent_patrol_schedule USING btree (tree_path);
CREATE INDEX IF NOT EXISTS ent_patrol_schedule_deleted_idx
    ON dynamicbusiness.ent_patrol_schedule USING btree (deleted);
CREATE INDEX IF NOT EXISTS ent_patrol_schedule_ent_type_idx
    ON dynamicbusiness.ent_patrol_schedule USING btree (entity_type_code);

CREATE INDEX IF NOT EXISTS ent_patrol_object_tree_path_idx
    ON dynamicbusiness.ent_patrol_object USING btree (tree_path);
CREATE INDEX IF NOT EXISTS ent_patrol_object_deleted_idx
    ON dynamicbusiness.ent_patrol_object USING btree (deleted);
CREATE INDEX IF NOT EXISTS ent_patrol_object_ent_type_idx
    ON dynamicbusiness.ent_patrol_object USING btree (entity_type_code);

CREATE INDEX IF NOT EXISTS ent_patrol_point_tree_path_idx
    ON dynamicbusiness.ent_patrol_point USING btree (tree_path);
CREATE INDEX IF NOT EXISTS ent_patrol_point_deleted_idx
    ON dynamicbusiness.ent_patrol_point USING btree (deleted);
CREATE INDEX IF NOT EXISTS ent_patrol_point_ent_type_idx
    ON dynamicbusiness.ent_patrol_point USING btree (entity_type_code);
