-- 角色在某个数据目录上的场站范围。
-- 查看和写入分开。取值只有不限制、全部场站、仅当前站。
-- 不写角色表上的数据范围，那一列仍是系统部门范围。
SET search_path TO dynamicbusiness;

CREATE SEQUENCE IF NOT EXISTS dynamic_catalog_role_station_scope_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE IF NOT EXISTS dynamic_catalog_role_station_scope (
    id bigint DEFAULT nextval('dynamicbusiness.dynamic_catalog_role_station_scope_id_seq'::regclass) NOT NULL,
    role_id bigint NOT NULL,
    registry_code character varying(64) NOT NULL,
    view_scope character varying(16) NOT NULL,
    write_scope character varying(16) NOT NULL,
    creator character varying(64) DEFAULT ''::character varying,
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updater character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted boolean DEFAULT false NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL,
    CONSTRAINT dynamic_catalog_role_station_scope_pkey PRIMARY KEY (id),
    CONSTRAINT ck_catalog_role_station_view_scope
        CHECK (view_scope IN ('UNRESTRICTED', 'ALL', 'CURRENT')),
    CONSTRAINT ck_catalog_role_station_write_scope
        CHECK (write_scope IN ('UNRESTRICTED', 'ALL', 'CURRENT'))
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_dynamic_catalog_role_station_scope
    ON dynamic_catalog_role_station_scope (role_id, registry_code, tenant_id)
    WHERE (deleted = false);

CREATE INDEX IF NOT EXISTS idx_dynamic_catalog_role_station_scope_role
    ON dynamic_catalog_role_station_scope (role_id)
    WHERE (deleted = false);
