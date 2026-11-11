-- 每个业务租户自己的顶栏菜单。只存层级和显示名，不存角色动作。
SET search_path TO dynamicbusiness;

CREATE TABLE IF NOT EXISTS tenant_portal_menu (
    id          BIGSERIAL PRIMARY KEY,
    tenant_id   BIGINT       NOT NULL,
    parent_id   BIGINT,
    title       VARCHAR(64)  NOT NULL,
    page_code   VARCHAR(64),
    sort        INTEGER      NOT NULL DEFAULT 0,
    creator     VARCHAR(64)  DEFAULT '',
    create_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater     VARCHAR(64)  DEFAULT '',
    update_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted     BOOLEAN      NOT NULL DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_tenant_portal_menu_tenant
    ON tenant_portal_menu (tenant_id)
    WHERE deleted = FALSE;

CREATE UNIQUE INDEX IF NOT EXISTS uk_tenant_portal_menu_page
    ON tenant_portal_menu (tenant_id, page_code)
    WHERE deleted = FALSE AND page_code IS NOT NULL AND page_code <> '';
