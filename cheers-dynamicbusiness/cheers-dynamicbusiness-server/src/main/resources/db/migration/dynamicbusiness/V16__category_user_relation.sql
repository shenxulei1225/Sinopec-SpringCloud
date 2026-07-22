-- 分类节点与 system 用户多对多关联（人员编组）；不改 system_users
SET search_path TO dynamicbusiness;

CREATE SEQUENCE IF NOT EXISTS dynamic_category_user_relation_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE IF NOT EXISTS dynamic_category_user_relation (
    id bigint DEFAULT nextval('dynamicbusiness.dynamic_category_user_relation_id_seq'::regclass) NOT NULL,
    category_id bigint NOT NULL,
    user_id bigint NOT NULL,
    sort integer DEFAULT 0 NOT NULL,
    creator character varying(64) DEFAULT ''::character varying NOT NULL,
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updater character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted boolean DEFAULT false NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL,
    CONSTRAINT dynamic_category_user_relation_pkey PRIMARY KEY (id)
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_dynamic_category_user_relation
    ON dynamic_category_user_relation (category_id, user_id, tenant_id)
    WHERE (deleted = false);

CREATE INDEX IF NOT EXISTS idx_dynamic_category_user_relation_category
    ON dynamic_category_user_relation (category_id)
    WHERE (deleted = false);

CREATE INDEX IF NOT EXISTS idx_dynamic_category_user_relation_user
    ON dynamic_category_user_relation (user_id)
    WHERE (deleted = false);

-- 人员编组分类种类（按租户幂等；常见 tenant 0/1）
INSERT INTO dynamic_category_type (
    category_type_code, name, description, status,
    creator, create_time, updater, update_time, deleted, tenant_id
)
SELECT
    'person_group',
    '人员编组',
    '组织架构「组」：分类节点关联 system 用户',
    1,
    'system',
    CURRENT_TIMESTAMP,
    'system',
    CURRENT_TIMESTAMP,
    false,
    t.tenant_id
FROM (VALUES (0::bigint), (1::bigint)) AS t(tenant_id)
WHERE NOT EXISTS (
    SELECT 1
    FROM dynamic_category_type ct
    WHERE ct.deleted = false
      AND ct.category_type_code = 'person_group'
      AND ct.tenant_id = t.tenant_id
);
