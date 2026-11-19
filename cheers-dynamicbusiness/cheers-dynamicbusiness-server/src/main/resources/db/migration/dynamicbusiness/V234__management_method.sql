-- 可选的数据管理方式：租户先登记数据能按哪几本目录管理（按场站、按管线、按组织机构……），
-- 各数据目录再从这几种里选。避免每本目录各自从几十本目录里挑，选出不该当管理方式的目录。
-- 权威：docs/中台/能力定义/数据权限能力/数据权限能力（权威）.md 第 2.3 节。
--
-- 本迁移做的事：
--   1. 新建可选管理方式表：每行是一本可以用来管理数据的目录，带排序。在平台配置页「数据资源管理」里维护。
--   2. 一次性把各租户归属声明里已经在用的目录登记为可选方式，让已有配置在新页面上照常显示。
--      以后新增方式只在页面上登记，运行中的代码不补登记。
SET search_path TO dynamicbusiness;

CREATE SEQUENCE IF NOT EXISTS dynamic_management_method_id_seq
    START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;

CREATE TABLE IF NOT EXISTS dynamic_management_method (
    id bigint DEFAULT nextval('dynamicbusiness.dynamic_management_method_id_seq'::regclass) NOT NULL,
    registry_code character varying(64) NOT NULL,
    sort integer DEFAULT 0 NOT NULL,
    creator character varying(64) DEFAULT ''::character varying,
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updater character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted boolean DEFAULT false NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL,
    CONSTRAINT dynamic_management_method_pkey PRIMARY KEY (id)
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_dynamic_management_method
    ON dynamic_management_method (tenant_id, registry_code)
    WHERE (deleted = false);

COMMENT ON TABLE dynamic_management_method IS
    '可选的数据管理方式：数据目录能按哪几本目录管理。页面显示为「按<目录名称>管理」。';
COMMENT ON COLUMN dynamic_management_method.registry_code IS
    '用来管理数据的目录注册编码，例如场站、管线、组织机构';
COMMENT ON COLUMN dynamic_management_method.sort IS
    '在「数据管理方式」表格里的列顺序，从小到大';

INSERT INTO dynamic_management_method (registry_code, sort, tenant_id)
SELECT used.target_registry_code,
       (ROW_NUMBER() OVER (PARTITION BY used.tenant_id ORDER BY used.usage_count DESC, used.target_registry_code))::integer,
       used.tenant_id
FROM (
    SELECT o.tenant_id, o.target_registry_code, count(*) AS usage_count
    FROM dynamic_catalog_ownership o
    WHERE o.deleted = false
    GROUP BY o.tenant_id, o.target_registry_code
) used
WHERE NOT EXISTS (
    SELECT 1 FROM dynamic_management_method m
    WHERE m.deleted = false AND m.tenant_id = used.tenant_id AND m.registry_code = used.target_registry_code
);
