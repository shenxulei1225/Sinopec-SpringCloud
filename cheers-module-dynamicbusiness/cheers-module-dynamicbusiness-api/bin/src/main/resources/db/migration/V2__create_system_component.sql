-- 组件：可复用 UI 单元默认定义（与 ComponentDO 一致）
CREATE TABLE system_component (
    id          BIGSERIAL PRIMARY KEY,
    key         VARCHAR(64)  NOT NULL,
    type        VARCHAR(32)  NOT NULL,
    name        VARCHAR(100) NOT NULL,
    icon        VARCHAR(64),
    props       TEXT,
    data_config TEXT,
    api_config  TEXT,
    ui_config   TEXT,
    status      SMALLINT     DEFAULT 1,
    sort        INT          DEFAULT 0,
    description VARCHAR(500),
    creator     VARCHAR(64)  DEFAULT '',
    create_time TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updater     VARCHAR(64)  DEFAULT '',
    update_time TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    deleted     BOOLEAN      DEFAULT FALSE,
    tenant_id   BIGINT       DEFAULT 0 NOT NULL,
    CONSTRAINT uk_system_component_key UNIQUE (key)
);

COMMENT ON TABLE system_component IS '组件';
COMMENT ON COLUMN system_component.key IS '组件唯一标识';
COMMENT ON COLUMN system_component.type IS 'tree | list | multi-tree 等';
COMMENT ON COLUMN system_component.props IS '默认 props JSON';
COMMENT ON COLUMN system_component.data_config IS '默认数据接口 JSON';
COMMENT ON COLUMN system_component.api_config IS '默认 API JSON';
COMMENT ON COLUMN system_component.ui_config IS '默认扩展元数据 JSON';

CREATE INDEX idx_system_component_status_sort ON system_component (status, sort);