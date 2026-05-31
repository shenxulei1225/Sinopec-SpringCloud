SET search_path TO platformresource;

-- 组件 Props：模板（完整 props_json）与实例（template_id + props_override）
CREATE TABLE pr_component_props (
    id                  BIGSERIAL PRIMARY KEY,
    is_template         BOOLEAN      NOT NULL DEFAULT TRUE,
    component_id        BIGINT       NOT NULL,
    component_code      VARCHAR(64)  NOT NULL,
    data_source_key     VARCHAR(128),
    template_id         BIGINT,
    schema_version      VARCHAR(32)  NOT NULL DEFAULT '1',
    props_json          TEXT         NOT NULL DEFAULT '{}',
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

COMMENT ON TABLE pr_component_props IS '组件 Props 模板与实例';
COMMENT ON COLUMN pr_component_props.id IS 'propsId，模板/实例主键';
COMMENT ON COLUMN pr_component_props.is_template IS 'true=模板（props_json 全量）；false=实例（props_override 差异）';
COMMENT ON COLUMN pr_component_props.component_id IS '关联 pr_component.id';
COMMENT ON COLUMN pr_component_props.component_code IS '语义化组件编码，与 pr_component.key 一致';
COMMENT ON COLUMN pr_component_props.data_source_key IS '数据来源能力键（与 props_json.dataSourceKey 同步）';
COMMENT ON COLUMN pr_component_props.template_id IS '实例引用的模板 propsId';
COMMENT ON COLUMN pr_component_props.props_json IS '模板：完整 props；实例：通常为空对象';
COMMENT ON COLUMN pr_component_props.props_override IS '实例相对模板的差异 JSON';

CREATE INDEX idx_pr_component_props_code_template
    ON pr_component_props (component_code, is_template)
    WHERE deleted = FALSE;

CREATE INDEX idx_pr_component_props_component_id
    ON pr_component_props (component_id)
    WHERE deleted = FALSE;

CREATE INDEX idx_pr_component_props_template_id
    ON pr_component_props (template_id)
    WHERE deleted = FALSE AND is_template = FALSE;

CREATE INDEX idx_pr_component_props_data_source_key
    ON pr_component_props (data_source_key)
    WHERE deleted = FALSE AND data_source_key IS NOT NULL;
