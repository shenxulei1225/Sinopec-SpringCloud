SET search_path TO platformresource;

-- 组件库元数据（可复用 UI 单元默认定义，与 component_props 分离）
CREATE TABLE pr_component (
    id           BIGSERIAL PRIMARY KEY,
    "key"        VARCHAR(64)  NOT NULL,
    type         VARCHAR(32)  NOT NULL,
    name         VARCHAR(100) NOT NULL,
    icon         VARCHAR(64),
    props        TEXT,
    data_config  TEXT,
    api_config   TEXT,
    ui_config    TEXT,
    status       SMALLINT     DEFAULT 1,
    sort         INT          DEFAULT 0,
    description  VARCHAR(500),
    creator      VARCHAR(64)  DEFAULT '',
    create_time  TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updater      VARCHAR(64)  DEFAULT '',
    update_time  TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    deleted      BOOLEAN      DEFAULT FALSE,
    tenant_id    BIGINT       DEFAULT 0 NOT NULL,
    CONSTRAINT uk_pr_component_key UNIQUE ("key")
);

COMMENT ON TABLE pr_component IS '组件库：可复用 UI 单元默认定义';
COMMENT ON COLUMN pr_component."key" IS '组件唯一编码（与 component_code 一致）';
COMMENT ON COLUMN pr_component.type IS '组件分类：list / tree / primitive / layout 等';
COMMENT ON COLUMN pr_component.props IS '默认 props JSON（历史兜底，运行时以 component_props 为准）';

CREATE INDEX idx_pr_component_status
    ON pr_component (status, sort)
    WHERE deleted = FALSE;
