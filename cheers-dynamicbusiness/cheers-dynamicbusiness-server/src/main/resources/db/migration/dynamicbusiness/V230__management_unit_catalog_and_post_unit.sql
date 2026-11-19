-- 数据范围改由管理单元决定：新建可管目录表，任职只记管理单元。
-- 权威：docs/中台/能力定义/数据权限能力/数据权限能力（权威）.md 第 2.1、2.2、2.5 节。
-- 术语：docs/治理机制/能力体系/术语对照与命名决议-数据权限分配-20261116.md 第 5.1、5.3 节。
--
-- 本迁移做的事：
--   1. 新建可管目录表：每行是「管理单元类型（型号编号）· 数据目录」。在平台配置页「数据资源管理」里维护。
--   2. 任职表：scope_entity_id 改名 management_unit_id；删除 scope_registry_code、include_descendants、kind。
--      管理单元目录只有一本，不必再写目录；范围总是含下级；主岗、兼任、借调不影响权限。
--      任职表上线以来没有写过记录，改列不丢数据。
-- 授权分配表和权限维度列在 44 条授权迁成任职之后，由下一份迁移删除。
SET search_path TO dynamicbusiness;

CREATE SEQUENCE IF NOT EXISTS dynamic_management_unit_catalog_id_seq
    START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;

CREATE TABLE IF NOT EXISTS dynamic_management_unit_catalog (
    id bigint DEFAULT nextval('dynamicbusiness.dynamic_management_unit_catalog_id_seq'::regclass) NOT NULL,
    management_unit_model_id bigint NOT NULL,
    registry_code character varying(64) NOT NULL,
    creator character varying(64) DEFAULT ''::character varying,
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updater character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted boolean DEFAULT false NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL,
    CONSTRAINT dynamic_management_unit_catalog_pkey PRIMARY KEY (id)
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_dynamic_management_unit_catalog
    ON dynamic_management_unit_catalog (tenant_id, management_unit_model_id, registry_code)
    WHERE (deleted = false);

COMMENT ON TABLE dynamic_management_unit_catalog IS
    '可管目录：某个管理单元类型能管哪几本数据目录。登记了管理单元类型的那本目录就是管理单元目录。';
COMMENT ON COLUMN dynamic_management_unit_catalog.management_unit_model_id IS
    '管理单元类型：管理单元目录里的型号编号';
COMMENT ON COLUMN dynamic_management_unit_catalog.registry_code IS
    '能管的数据目录注册编码';

DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema = 'dynamicbusiness'
               AND table_name = 'dynamic_post_assignment' AND column_name = 'scope_entity_id') THEN
        ALTER TABLE dynamic_post_assignment RENAME COLUMN scope_entity_id TO management_unit_id;
    END IF;
END $$;

ALTER TABLE dynamic_post_assignment DROP COLUMN IF EXISTS scope_registry_code;
ALTER TABLE dynamic_post_assignment DROP COLUMN IF EXISTS include_descendants;
ALTER TABLE dynamic_post_assignment DROP COLUMN IF EXISTS kind;

COMMENT ON COLUMN dynamic_post_assignment.management_unit_id IS
    '任职所在的管理单元：管理单元目录里的记录编号。范围总是包含它的下级单元。';
