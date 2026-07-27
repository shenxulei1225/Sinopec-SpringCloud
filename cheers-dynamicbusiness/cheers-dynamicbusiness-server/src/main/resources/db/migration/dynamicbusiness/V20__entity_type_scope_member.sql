-- 划分数据（SCOPE）实体成员：入口编码 + 实体 id；不复用 dynamic_category_entity_link
SET search_path TO dynamicbusiness;

CREATE SEQUENCE IF NOT EXISTS dynamic_entity_type_scope_member_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE IF NOT EXISTS dynamic_entity_type_scope_member (
    id bigint DEFAULT nextval('dynamicbusiness.dynamic_entity_type_scope_member_id_seq'::regclass) NOT NULL,
    scope_entity_type_code character varying(64) NOT NULL,
    entity_id bigint NOT NULL,
    creator character varying(64) DEFAULT ''::character varying NOT NULL,
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updater character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted boolean DEFAULT false NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL,
    CONSTRAINT dynamic_entity_type_scope_member_pkey PRIMARY KEY (id)
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_dynamic_entity_type_scope_member
    ON dynamic_entity_type_scope_member (scope_entity_type_code, entity_id, tenant_id)
    WHERE (deleted = false);

CREATE INDEX IF NOT EXISTS idx_dynamic_entity_type_scope_member_code
    ON dynamic_entity_type_scope_member (scope_entity_type_code)
    WHERE (deleted = false);

CREATE INDEX IF NOT EXISTS idx_dynamic_entity_type_scope_member_entity
    ON dynamic_entity_type_scope_member (entity_id)
    WHERE (deleted = false);
