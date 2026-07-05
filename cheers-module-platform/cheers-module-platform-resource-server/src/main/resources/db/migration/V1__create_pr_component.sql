SET search_path TO platformresource;

-- 组件库目录：登记可复用 UI 单元（编码、分类、展示信息）
-- 用户偏好见 pr_component_props；接口契约见业务能力模块 component_interface
-- 前端实现路径不在此表，由前端组件注册表维护
CREATE TABLE pr_component (
    id              BIGSERIAL PRIMARY KEY,
    component_code  VARCHAR(64)  NOT NULL,
    type            VARCHAR(32)  NOT NULL,
    name            VARCHAR(100) NOT NULL,
    icon            VARCHAR(64),
    status          SMALLINT     DEFAULT 1,
    sort            INT          DEFAULT 0,
    description     VARCHAR(500),
    creator         VARCHAR(64)  DEFAULT '',
    create_time     TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updater         VARCHAR(64)  DEFAULT '',
    update_time     TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    deleted         BOOLEAN      DEFAULT FALSE,
    tenant_id       BIGINT       DEFAULT 0 NOT NULL,
    CONSTRAINT uk_pr_component_code UNIQUE (component_code)
);

COMMENT ON TABLE pr_component IS '组件库目录：可复用 UI 单元登记';
COMMENT ON COLUMN pr_component.component_code IS '组件唯一编码，与 pr_component_props.component_code 一致';
COMMENT ON COLUMN pr_component.type IS '组件分类：list / tree / primitive / layout 等';

CREATE INDEX idx_pr_component_status
    ON pr_component (status, sort)
    WHERE deleted = FALSE;
