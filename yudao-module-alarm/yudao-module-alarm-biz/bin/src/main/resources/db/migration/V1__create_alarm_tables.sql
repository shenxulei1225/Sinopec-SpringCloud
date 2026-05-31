CREATE TABLE IF NOT EXISTS alarm (
    id BIGINT PRIMARY KEY,
    alarm_code VARCHAR(32) NOT NULL,
    alarm_type_id BIGINT NOT NULL,
    alarm_category_id BIGINT,
    alarm_model_id BIGINT,
    alarm_type_path VARCHAR(256),
    alarm_level VARCHAR(16) NOT NULL,
    alarm_status VARCHAR(16) NOT NULL,
    alarm_source VARCHAR(16) NOT NULL,
    alarm_content TEXT NOT NULL,
    device_id BIGINT,
    device_name VARCHAR(128),
    location_id BIGINT,
    location_name VARCHAR(256),
    trigger_value VARCHAR(128),
    threshold_value VARCHAR(128),
    trigger_count INT DEFAULT 1,
    rule_id BIGINT,
    escalation_level INT DEFAULT 0,
    escalation_time TIMESTAMP,
    acknowledge_time TIMESTAMP,
    acknowledge_user_id BIGINT,
    acknowledge_user_name VARCHAR(64),
    acknowledge_remark TEXT,
    handle_time TIMESTAMP,
    handle_user_id BIGINT,
    handle_user_name VARCHAR(64),
    handle_measure TEXT,
    handle_result VARCHAR(16),
    close_time TIMESTAMP,
    close_user_id BIGINT,
    close_user_name VARCHAR(64),
    close_reason VARCHAR(16),
    close_remark TEXT,
    duration_seconds BIGINT,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    creator VARCHAR(64) DEFAULT '',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater VARCHAR(64) DEFAULT '',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_alarm_alarm_code ON alarm (alarm_code);
CREATE INDEX IF NOT EXISTS idx_alarm_status_time ON alarm (alarm_status, create_time DESC);
CREATE INDEX IF NOT EXISTS idx_alarm_type_time ON alarm (alarm_type_id, create_time DESC);
CREATE INDEX IF NOT EXISTS idx_alarm_device_time ON alarm (device_id, create_time DESC);
CREATE INDEX IF NOT EXISTS idx_alarm_tenant_deleted ON alarm (tenant_id, deleted);

CREATE TABLE IF NOT EXISTS alarm_attachment (
    id BIGINT PRIMARY KEY,
    alarm_id BIGINT NOT NULL,
    file_name VARCHAR(256) NOT NULL,
    file_path VARCHAR(512) NOT NULL,
    file_type VARCHAR(32) NOT NULL,
    file_size BIGINT NOT NULL,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    creator VARCHAR(64) DEFAULT '',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater VARCHAR(64) DEFAULT '',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_alarm_attachment_alarm_id ON alarm_attachment (alarm_id);
CREATE INDEX IF NOT EXISTS idx_alarm_attachment_tenant_deleted ON alarm_attachment (tenant_id, deleted);

CREATE TABLE IF NOT EXISTS alarm_rule (
    id BIGINT PRIMARY KEY,
    rule_name VARCHAR(128) NOT NULL,
    rule_code VARCHAR(64) NOT NULL,
    rule_type VARCHAR(32) NOT NULL,
    alarm_type_id BIGINT NOT NULL,
    alarm_category_id BIGINT NOT NULL,
    alarm_model_id BIGINT NOT NULL,
    alarm_level VARCHAR(16) NOT NULL,
    device_type VARCHAR(64),
    condition_expression TEXT NOT NULL,
    alarm_content_template TEXT,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    priority INT DEFAULT 0,
    description TEXT,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    creator VARCHAR(64) DEFAULT '',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater VARCHAR(64) DEFAULT '',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_alarm_rule_rule_code ON alarm_rule (rule_code);
CREATE INDEX IF NOT EXISTS idx_alarm_rule_type_enabled ON alarm_rule (alarm_type_id, enabled);
CREATE INDEX IF NOT EXISTS idx_alarm_rule_tenant_deleted ON alarm_rule (tenant_id, deleted);

CREATE TABLE IF NOT EXISTS linkage_rule (
    id BIGINT PRIMARY KEY,
    rule_name VARCHAR(128) NOT NULL,
    rule_code VARCHAR(64) NOT NULL,
    alarm_rule_id BIGINT,
    alarm_type_id BIGINT,
    alarm_category_id BIGINT,
    alarm_level VARCHAR(16),
    condition_expression TEXT,
    actions TEXT NOT NULL,
    execution_mode VARCHAR(16) DEFAULT 'SERIAL',
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    priority INT DEFAULT 0,
    description TEXT,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    creator VARCHAR(64) DEFAULT '',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater VARCHAR(64) DEFAULT '',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_linkage_rule_rule_code ON linkage_rule (rule_code);
CREATE INDEX IF NOT EXISTS idx_linkage_rule_alarm_rule_id ON linkage_rule (alarm_rule_id);
CREATE INDEX IF NOT EXISTS idx_linkage_rule_tenant_deleted ON linkage_rule (tenant_id, deleted);

CREATE TABLE IF NOT EXISTS linkage_execution (
    id BIGINT PRIMARY KEY,
    alarm_id BIGINT NOT NULL,
    linkage_rule_id BIGINT NOT NULL,
    action_type VARCHAR(32) NOT NULL,
    action_config TEXT,
    target_device_id BIGINT,
    target_device_name VARCHAR(128),
    execution_status VARCHAR(16) NOT NULL,
    retry_count INT DEFAULT 0,
    execution_result TEXT,
    error_message TEXT,
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    duration_ms BIGINT,
    manual_intervention BOOLEAN DEFAULT FALSE,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    creator VARCHAR(64) DEFAULT '',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater VARCHAR(64) DEFAULT '',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_linkage_execution_alarm_id ON linkage_execution (alarm_id);
CREATE INDEX IF NOT EXISTS idx_linkage_execution_rule_id ON linkage_execution (linkage_rule_id);
CREATE INDEX IF NOT EXISTS idx_linkage_execution_status_time ON linkage_execution (execution_status, create_time DESC);
CREATE INDEX IF NOT EXISTS idx_linkage_execution_tenant_deleted ON linkage_execution (tenant_id, deleted);

CREATE TABLE IF NOT EXISTS alarm_audit_log (
    id BIGINT PRIMARY KEY,
    alarm_id BIGINT NOT NULL,
    operation_type VARCHAR(32) NOT NULL,
    operation_content TEXT,
    operator_id BIGINT,
    operator_name VARCHAR(64),
    operation_time TIMESTAMP NOT NULL,
    ip_address VARCHAR(64),
    tenant_id BIGINT NOT NULL DEFAULT 0,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_alarm_audit_log_alarm_id ON alarm_audit_log (alarm_id);
CREATE INDEX IF NOT EXISTS idx_alarm_audit_log_operation_time ON alarm_audit_log (operation_time DESC);
CREATE INDEX IF NOT EXISTS idx_alarm_audit_log_tenant_id ON alarm_audit_log (tenant_id);

CREATE SEQUENCE IF NOT EXISTS alarm_seq START WITH 1000 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS alarm_attachment_seq START WITH 1000 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS alarm_rule_seq START WITH 1000 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS linkage_rule_seq START WITH 1000 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS linkage_execution_seq START WITH 1000 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS alarm_audit_log_seq START WITH 1000 INCREMENT BY 1;
