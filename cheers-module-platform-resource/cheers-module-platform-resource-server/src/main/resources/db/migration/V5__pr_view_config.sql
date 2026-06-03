-- pr_view_config：视图配置（模板 / 实例）
-- 与 pr_component_props 遵循相同的 template/instance 模式。
CREATE TABLE IF NOT EXISTS pr_view_config
(
    id              BIGSERIAL PRIMARY KEY,

    -- 模板/实例区分
    is_template     BOOLEAN          NOT NULL DEFAULT TRUE,
    template_id     BIGINT,                             -- 实例指向模板，模板为 NULL

    -- 视图元信息
    view_type       VARCHAR(50)      NOT NULL,           -- TreeView | TreeListView | DataManagementView | ListView
    view_code       VARCHAR(100),                        -- 业务唯一标识，可为 NULL
    name            VARCHAR(200)     NOT NULL,
    description     TEXT,

    -- 配置 JSON
    -- 模板：完整配置（slots + relations）
    -- 实例：差量配置（仅覆盖的 slots / relations）
    config_json     TEXT             NOT NULL DEFAULT '{}',
    config_override TEXT,                               -- 仅实例使用

    status          SMALLINT         NOT NULL DEFAULT 1,
    sort            INTEGER          NOT NULL DEFAULT 0,

    -- BaseDO 公共字段
    creator         VARCHAR(64)      NOT NULL DEFAULT '',
    create_time     TIMESTAMP        NOT NULL DEFAULT NOW(),
    updater         VARCHAR(64)      NOT NULL DEFAULT '',
    update_time     TIMESTAMP        NOT NULL DEFAULT NOW(),
    deleted         SMALLINT         NOT NULL DEFAULT 0,
    tenant_id       BIGINT           NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX IF NOT EXISTS uidx_pr_view_config_code
    ON pr_view_config (view_code)
    WHERE view_code IS NOT NULL AND deleted = 0;

COMMENT ON TABLE  pr_view_config                IS '视图配置（模板/实例）';
COMMENT ON COLUMN pr_view_config.is_template    IS 'true=视图模板；false=视图实例';
COMMENT ON COLUMN pr_view_config.template_id    IS '实例引用的模板 id';
COMMENT ON COLUMN pr_view_config.view_type      IS '视图类型：TreeView/TreeListView/DataManagementView/ListView';
COMMENT ON COLUMN pr_view_config.view_code      IS '业务唯一标识，如 inspection:facility-by-category';
COMMENT ON COLUMN pr_view_config.config_json    IS '模板完整配置 JSON（slots + relations）';
COMMENT ON COLUMN pr_view_config.config_override IS '实例差量配置 JSON';
