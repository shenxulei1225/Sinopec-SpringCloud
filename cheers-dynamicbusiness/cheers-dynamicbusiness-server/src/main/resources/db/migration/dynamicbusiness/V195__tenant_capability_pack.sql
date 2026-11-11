-- 租户已开通的业务能力包；开通编排读写本表。
-- 顺带补上平台超级管理员与 super_admin 角色的关联（缺关联则租户管理鉴权失败）。
SET search_path TO dynamicbusiness;

CREATE TABLE IF NOT EXISTS tenant_capability_pack (
    id          BIGSERIAL PRIMARY KEY,
    tenant_id   BIGINT       NOT NULL,
    pack_code   VARCHAR(64)  NOT NULL,
    creator     VARCHAR(64)  DEFAULT '',
    create_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater     VARCHAR(64)  DEFAULT '',
    update_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted     BOOLEAN      NOT NULL DEFAULT FALSE
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_tenant_capability_pack
    ON tenant_capability_pack (tenant_id, pack_code)
    WHERE deleted = FALSE;

CREATE INDEX IF NOT EXISTS idx_tenant_capability_pack_tenant
    ON tenant_capability_pack (tenant_id)
    WHERE deleted = FALSE;

-- 登录令牌用的是用户目录编号，不是 system_users.id。按平台租户物理表挂 super_admin。
DO $$
DECLARE
    platform_id bigint;
    admin_role_id bigint;
    account_id bigint;
    user_table text;
BEGIN
    SELECT id INTO platform_id
    FROM public.system_tenant
    WHERE scope = 'PLATFORM' AND deleted = FALSE
    ORDER BY id
    LIMIT 1;
    IF platform_id IS NULL THEN
        RETURN;
    END IF;

    SELECT id INTO admin_role_id
    FROM public.system_role
    WHERE tenant_id = platform_id
      AND code = 'super_admin'
      AND deleted = FALSE
    ORDER BY id
    LIMIT 1;
    IF admin_role_id IS NULL THEN
        RETURN;
    END IF;

    user_table := format('dynamicbusiness.ent_user_account_t%s', platform_id);
    IF to_regclass(user_table) IS NULL THEN
        RETURN;
    END IF;

    EXECUTE format(
        'SELECT id FROM %s WHERE code = %L AND deleted = FALSE ORDER BY id LIMIT 1',
        user_table,
        'super'
    ) INTO account_id;
    IF account_id IS NULL THEN
        RETURN;
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM public.system_user_role ur
        WHERE ur.user_id = account_id
          AND ur.role_id = admin_role_id
          AND ur.deleted = FALSE
    ) THEN
        INSERT INTO public.system_user_role (user_id, role_id, creator, updater, deleted, tenant_id)
        VALUES (account_id, admin_role_id, 'migration', 'migration', FALSE, platform_id);
    END IF;
END $$;
