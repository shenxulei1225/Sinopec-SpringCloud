-- 门户业务（Business）与业务入口（BusinessEntry），与实体类型（EntityType）分层

SET search_path TO dynamicbusiness;

CREATE SEQUENCE IF NOT EXISTS dynamicbusiness.dynamic_business_id_seq
    START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;

CREATE SEQUENCE IF NOT EXISTS dynamicbusiness.dynamic_business_entry_id_seq
    START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;

CREATE TABLE IF NOT EXISTS dynamicbusiness.dynamic_business (
    id bigint DEFAULT nextval('dynamicbusiness.dynamic_business_id_seq'::regclass) NOT NULL,
    code character varying(64) NOT NULL,
    name character varying(200) NOT NULL,
    parent_id bigint,
    node_kind character varying(32) DEFAULT 'LEAF'::character varying NOT NULL,
    description character varying(500),
    icon character varying(128),
    alias character varying(128),
    sort integer DEFAULT 0 NOT NULL,
    status character varying(32) DEFAULT 'active'::character varying NOT NULL,
    creator character varying(64) DEFAULT ''::character varying NOT NULL,
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updater character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted boolean DEFAULT false NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL,
    CONSTRAINT dynamic_business_pkey PRIMARY KEY (id)
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_dynamic_business_code_tenant
    ON dynamicbusiness.dynamic_business (code, tenant_id)
    WHERE deleted = false;

CREATE INDEX IF NOT EXISTS idx_dynamic_business_parent
    ON dynamicbusiness.dynamic_business (parent_id, sort)
    WHERE deleted = false;

COMMENT ON TABLE dynamicbusiness.dynamic_business IS '门户业务注册（与实体类型分层）';
COMMENT ON COLUMN dynamicbusiness.dynamic_business.code IS '业务编码 businessCode';
COMMENT ON COLUMN dynamicbusiness.dynamic_business.node_kind IS 'GROUP=仅分组；LEAF=可有业务入口';

CREATE TABLE IF NOT EXISTS dynamicbusiness.dynamic_business_entry (
    id bigint DEFAULT nextval('dynamicbusiness.dynamic_business_entry_id_seq'::regclass) NOT NULL,
    business_id bigint NOT NULL,
    code character varying(64) NOT NULL,
    name character varying(200) NOT NULL,
    entry_type character varying(32) NOT NULL,
    entity_type_code character varying(64),
    scope_config jsonb,
    page_config_id bigint,
    sort integer DEFAULT 0 NOT NULL,
    status character varying(32) DEFAULT 'active'::character varying NOT NULL,
    creator character varying(64) DEFAULT ''::character varying NOT NULL,
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updater character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted boolean DEFAULT false NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL,
    CONSTRAINT dynamic_business_entry_pkey PRIMARY KEY (id)
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_dynamic_business_entry_code
    ON dynamicbusiness.dynamic_business_entry (business_id, code, tenant_id)
    WHERE deleted = false;

CREATE INDEX IF NOT EXISTS idx_dynamic_business_entry_business
    ON dynamicbusiness.dynamic_business_entry (business_id, sort)
    WHERE deleted = false;

COMMENT ON TABLE dynamicbusiness.dynamic_business_entry IS '业务入口：台账管理、scoped 列表等';
COMMENT ON COLUMN dynamicbusiness.dynamic_business_entry.entity_type_code IS '可选绑定的实体类型编码 entityTypeCode';

-- 存量：从 dynamic_entity_type 回填门户业务（保留 id/parent_id 以便树结构一致）
INSERT INTO dynamicbusiness.dynamic_business (
    id, code, name, parent_id, node_kind, description, icon, alias, sort, status,
    creator, create_time, updater, update_time, deleted, tenant_id
)
SELECT
    et.id, et.code, et.name, et.parent_id, 'LEAF', et.description, et.icon, et.alias,
    et.sort, et.status, et.creator, et.create_time, et.updater, et.update_time, et.deleted, et.tenant_id
FROM dynamicbusiness.dynamic_entity_type et
WHERE et.deleted = false
  AND NOT EXISTS (
    SELECT 1 FROM dynamicbusiness.dynamic_business b
    WHERE b.code = et.code AND b.tenant_id = et.tenant_id AND b.deleted = false
  );

SELECT setval(
    'dynamicbusiness.dynamic_business_id_seq',
    GREATEST(COALESCE((SELECT MAX(id) FROM dynamicbusiness.dynamic_business), 1), 1)
);

-- 默认入口：ENTITY_ADMIN，entity_type_code 与业务 code 相同（过渡期）
INSERT INTO dynamicbusiness.dynamic_business_entry (
    business_id, code, name, entry_type, entity_type_code, sort, status, tenant_id, creator
)
SELECT
    b.id,
    'default-admin',
    b.name || '管理',
    'ENTITY_ADMIN',
    b.code,
    0,
    'active',
    b.tenant_id,
    'flyway'
FROM dynamicbusiness.dynamic_business b
WHERE b.deleted = false
  AND NOT EXISTS (
    SELECT 1 FROM dynamicbusiness.dynamic_business_entry e
    WHERE e.business_id = b.id AND e.code = 'default-admin' AND e.deleted = false
  );
