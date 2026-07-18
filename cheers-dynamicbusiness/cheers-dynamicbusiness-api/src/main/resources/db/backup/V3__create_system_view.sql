-- 视图：现成页面/模块（与 ViewDO 一致）
CREATE TABLE system_view (
    id             BIGSERIAL PRIMARY KEY,
    key            VARCHAR(64)  NOT NULL,
    composition    TEXT,
    label          VARCHAR(100) NOT NULL,
    icon           VARCHAR(64),
    is_template    BOOLEAN      DEFAULT FALSE,
    ui_config      TEXT,
    layout_config  TEXT,
    layouts        TEXT,
    status         SMALLINT     DEFAULT 1,
    sort           INT          DEFAULT 0,
    description    VARCHAR(500),
    creator        VARCHAR(64)  DEFAULT '',
    create_time    TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updater        VARCHAR(64)  DEFAULT '',
    update_time    TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    deleted        BOOLEAN      DEFAULT FALSE,
    tenant_id      BIGINT       DEFAULT 0 NOT NULL,
    CONSTRAINT uk_system_view_key UNIQUE (key)
);

COMMENT ON TABLE system_view IS '视图';
COMMENT ON COLUMN system_view.key IS '视图唯一标识';
COMMENT ON COLUMN system_view.composition IS 'layoutSchema + items[] JSON';
COMMENT ON COLUMN system_view.is_template IS '是否在 Library 中作为模板展示';

CREATE INDEX idx_system_view_status_sort ON system_view (status, sort);
CREATE INDEX idx_system_view_is_template ON system_view (is_template) WHERE is_template = TRUE;