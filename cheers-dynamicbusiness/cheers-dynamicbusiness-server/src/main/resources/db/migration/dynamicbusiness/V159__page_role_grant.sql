-- 角色在数据目录以外的页面上的勾选。
-- 一条记录是某个角色对某个页面的一项动作。
-- 还没有这类记录的租户管理员，按当前已能使用的范围写入全部页面动作。已经有记录的不覆盖。
SET search_path TO dynamicbusiness;

CREATE SEQUENCE IF NOT EXISTS dynamic_page_role_grant_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE IF NOT EXISTS dynamic_page_role_grant (
    id bigint DEFAULT nextval('dynamicbusiness.dynamic_page_role_grant_id_seq'::regclass) NOT NULL,
    role_id bigint NOT NULL,
    page_code character varying(64) NOT NULL,
    action character varying(32) NOT NULL,
    creator character varying(64) DEFAULT ''::character varying,
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updater character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted boolean DEFAULT false NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL,
    CONSTRAINT dynamic_page_role_grant_pkey PRIMARY KEY (id)
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_dynamic_page_role_grant
    ON dynamic_page_role_grant (role_id, page_code, action, tenant_id)
    WHERE (deleted = false);

CREATE INDEX IF NOT EXISTS idx_dynamic_page_role_grant_role
    ON dynamic_page_role_grant (role_id)
    WHERE (deleted = false);

INSERT INTO dynamic_page_role_grant (
    role_id, page_code, action, creator, updater, tenant_id
)
SELECT r.id, act.page_code, act.action, '1', '1', r.tenant_id
FROM public.system_role r
CROSS JOIN (
    VALUES
        ('live-monitor', 'view'),
        ('live-monitor', 'command'),
        ('inspection-task', 'view'),
        ('inspection-task', 'maintain'),
        ('inspection-task', 'enable'),
        ('inspection-schedule', 'view'),
        ('emergency-event', 'view'),
        ('emergency-event', 'create'),
        ('emergency-event', 'confirm'),
        ('emergency-event', 'assess'),
        ('emergency-event', 'start'),
        ('emergency-event', 'supplement'),
        ('emergency-event', 'command'),
        ('emergency-resource', 'view'),
        ('emergency-resource', 'maintain'),
        ('emergency-guarantee', 'view'),
        ('emergency-guarantee', 'maintain'),
        ('emergency-command-template', 'view'),
        ('emergency-command-template', 'maintain'),
        ('emergency-command-template', 'enable'),
        ('analysis-capability', 'view'),
        ('analysis-capability', 'maintain'),
        ('analysis-capability', 'enable'),
        ('analysis-job', 'view'),
        ('analysis-provider', 'view'),
        ('analysis-provider', 'maintain'),
        ('analysis-provider', 'enable'),
        ('road-network', 'view'),
        ('facility-gis', 'view'),
        ('stats', 'view'),
        ('catalog-admin', 'view'),
        ('catalog-admin', 'create'),
        ('catalog-admin', 'update'),
        ('catalog-admin', 'delete'),
        ('organization', 'view'),
        ('organization', 'maintain-dept'),
        ('organization', 'maintain-user'),
        ('organization', 'reset-password'),
        ('portal', 'view'),
        ('portal', 'maintain'),
        ('role-permission', 'view'),
        ('role-permission', 'maintain-role'),
        ('role-permission', 'save-grant'),
        ('system-account', 'view'),
        ('system-account', 'maintain'),
        ('resource-library', 'view'),
        ('resource-library', 'maintain'),
        ('plugin', 'view'),
        ('plugin', 'maintain'),
        ('capability-config', 'view'),
        ('capability-config', 'maintain'),
        ('station-config', 'view'),
        ('station-config', 'maintain'),
        ('gis-admin', 'view'),
        ('gis-admin', 'maintain'),
        ('business-list', 'view'),
        ('business-list', 'maintain'),
        ('mind-map', 'view'),
        ('mind-map', 'maintain'),
        ('workflow', 'view'),
        ('workflow', 'maintain')
) AS act(page_code, action)
WHERE r.deleted = false
  AND r.code = 'tenant_admin'
  AND NOT EXISTS (
      SELECT 1
      FROM dynamic_page_role_grant g
      WHERE g.role_id = r.id
        AND g.deleted = false
  );
