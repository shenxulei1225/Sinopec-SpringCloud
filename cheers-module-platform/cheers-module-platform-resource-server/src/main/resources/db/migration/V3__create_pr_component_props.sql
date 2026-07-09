SET search_path TO platformresource;

-- 组件配置：模板（完整 props）与实例（template_id + props_override）
-- props 仅存用户 UI 偏好；接口契约由业务能力模块 component_interface 提供，运行时合并
-- data_source JSON：{ businessCategory, entityTypeCode, dataKind }
CREATE TABLE pr_component_props (
    id                  BIGSERIAL PRIMARY KEY,
    is_template         BOOLEAN      NOT NULL DEFAULT TRUE,
    component_id        BIGINT       NOT NULL,
    component_code      VARCHAR(64)  NOT NULL,
    data_source         TEXT,
    template_id         BIGINT,
    schema_version      VARCHAR(32)  NOT NULL DEFAULT '1',
    props               TEXT         NOT NULL DEFAULT '{}',
    props_override      TEXT,
    name                VARCHAR(100),
    status              SMALLINT     DEFAULT 1,
    sort                INT          DEFAULT 0,
    description         VARCHAR(500),
    creator             VARCHAR(64)  DEFAULT '',
    create_time         TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updater             VARCHAR(64)  DEFAULT '',
    update_time         TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    deleted             BOOLEAN      DEFAULT FALSE,
    tenant_id           BIGINT       DEFAULT 0 NOT NULL,
    CONSTRAINT fk_pr_component_props_component
        FOREIGN KEY (component_id) REFERENCES pr_component (id),
    CONSTRAINT fk_pr_component_props_template
        FOREIGN KEY (template_id) REFERENCES pr_component_props (id)
);

COMMENT ON TABLE pr_component_props IS '组件配置：模板与实例（用户偏好 props）';
COMMENT ON COLUMN pr_component_props.id IS 'propsId，模板/实例主键';
COMMENT ON COLUMN pr_component_props.data_source IS '数据来源 JSON：businessCategory + entityTypeCode + dataKind';
COMMENT ON COLUMN pr_component_props.props IS '模板：完整用户偏好 props；实例：通常为空对象';

CREATE INDEX idx_pr_component_props_code_template
    ON pr_component_props (component_code, is_template)
    WHERE deleted = FALSE;

CREATE INDEX idx_pr_component_props_component_id
    ON pr_component_props (component_id)
    WHERE deleted = FALSE;

CREATE INDEX idx_pr_component_props_template_id
    ON pr_component_props (template_id)
    WHERE deleted = FALSE AND is_template = FALSE;
