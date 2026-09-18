-- V131：条件策略主表。平台预置一条「采集结果到了就往执行账里记一条过程」。
SET search_path TO dynamicbusiness, public;

CREATE TABLE IF NOT EXISTS dynamic_condition_strategy (
    id                  BIGSERIAL PRIMARY KEY,
    tenant_id           BIGINT       NOT NULL DEFAULT 0,
    is_platform         BOOLEAN      NOT NULL DEFAULT FALSE,
    name                VARCHAR(128) NOT NULL,
    enabled             BOOLEAN      NOT NULL DEFAULT TRUE,
    event_type          VARCHAR(64)  NOT NULL,
    condition_json      TEXT         NOT NULL,
    action_code         VARCHAR(64)  NOT NULL,
    action_params_json  TEXT,
    priority            INTEGER      NOT NULL DEFAULT 100,
    creator             VARCHAR(64),
    create_time         TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updater             VARCHAR(64),
    update_time         TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    deleted             BOOLEAN      DEFAULT FALSE
);

COMMENT ON TABLE dynamic_condition_strategy IS
    '条件策略：当什么发生、满足哪些条件、做哪件已登记的事';
COMMENT ON COLUMN dynamic_condition_strategy.is_platform IS
    '平台预置，所有租户可见；用户自建为 false';
COMMENT ON COLUMN dynamic_condition_strategy.event_type IS
    '事件类型，如 COLLECTION_RECEIVED（采集结果到了）';
COMMENT ON COLUMN dynamic_condition_strategy.condition_json IS
    '结构化条件，不是脚本';
COMMENT ON COLUMN dynamic_condition_strategy.action_code IS
    '已登记动作编码：APPEND_PROCESS / RAISE_ALARM';

CREATE INDEX IF NOT EXISTS idx_condition_strategy_event
    ON dynamic_condition_strategy (event_type, enabled, priority, id)
    WHERE deleted = FALSE;

INSERT INTO dynamic_condition_strategy (
    tenant_id, is_platform, name, enabled, event_type, condition_json, action_code, priority
) VALUES (
    0,
    TRUE,
    '采集结果到了就往执行账里记一条过程',
    TRUE,
    'COLLECTION_RECEIVED',
    '{"all":[{"type":"HAS_EXECUTION_RECORD"}]}',
    'APPEND_PROCESS',
    10
);
