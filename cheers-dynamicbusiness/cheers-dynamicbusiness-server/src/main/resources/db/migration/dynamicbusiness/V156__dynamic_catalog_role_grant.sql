-- 自定义角色对数据目录的四项操作授权。
-- 目录身份是目录注册编码。不写 system_menu，也不按存数编码授权。
SET search_path TO dynamicbusiness;

CREATE SEQUENCE IF NOT EXISTS dynamic_catalog_role_grant_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE IF NOT EXISTS dynamic_catalog_role_grant (
    id bigint DEFAULT nextval('dynamicbusiness.dynamic_catalog_role_grant_id_seq'::regclass) NOT NULL,
    role_id bigint NOT NULL,
    registry_code character varying(64) NOT NULL,
    operation character varying(16) NOT NULL,
    creator character varying(64) DEFAULT ''::character varying,
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updater character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted boolean DEFAULT false NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL,
    CONSTRAINT dynamic_catalog_role_grant_pkey PRIMARY KEY (id)
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_dynamic_catalog_role_grant
    ON dynamic_catalog_role_grant (role_id, registry_code, operation, tenant_id)
    WHERE (deleted = false);

CREATE INDEX IF NOT EXISTS idx_dynamic_catalog_role_grant_role
    ON dynamic_catalog_role_grant (role_id)
    WHERE (deleted = false);
