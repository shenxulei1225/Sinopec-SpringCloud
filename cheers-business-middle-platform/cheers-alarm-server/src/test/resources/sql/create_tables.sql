-- =====================================================
-- 告警管理模块测试数据库初始化脚本 (H2)
-- =====================================================

-- =====================================================
-- 1. 告警表 (alarm)
-- =====================================================
CREATE TABLE IF NOT EXISTS alarm (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    alarm_code VARCHAR(32) NOT NULL,
    alarm_type_id BIGINT NOT NULL,
    alarm_category_id BIGINT,
    alarm_model_id BIGINT,
    alarm_type_path VARCHAR(256),
    alarm_level VARCHAR(16) NOT NULL,
    alarm_status VARCHAR(16) NOT NULL,
    alarm_source VARCHAR(16) NOT NULL,
    alarm_content CLOB NOT NULL,
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
    acknowledge_remark CLOB,
    handle_time TIMESTAMP,
    handle_user_id BIGINT,
    handle_user_name VARCHAR(64),
    handle_measure CLOB,
    handle_result VARCHAR(16),
    close_time TIMESTAMP,
    close_user_id BIGINT,
    close_user_name VARCHAR(64),
    close_reason VARCHAR(16),
    close_remark CLOB,
    duration_seconds BIGINT,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    creator VARCHAR(64) DEFAULT '',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater VARCHAR(64) DEFAULT '',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

-- =====================================================
-- 2. 告警附件表 (alarm_attachment)
-- =====================================================
CREATE TABLE IF NOT EXISTS alarm_attachment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
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

-- =====================================================
-- 3. 告警规则表 (alarm_rule)
-- =====================================================
CREATE TABLE IF NOT EXISTS alarm_rule (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    rule_name VARCHAR(128) NOT NULL,
    rule_code VARCHAR(64) NOT NULL,
    rule_type VARCHAR(32) NOT NULL,
    alarm_type_id BIGINT NOT NULL,
    alarm_category_id BIGINT NOT NULL,
    alarm_model_id BIGINT NOT NULL,
    alarm_level VARCHAR(16) NOT NULL,
    device_type VARCHAR(64),
    condition_expression CLOB NOT NULL,
    alarm_content_template CLOB,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    priority INT DEFAULT 0,
    description CLOB,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    creator VARCHAR(64) DEFAULT '',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater VARCHAR(64) DEFAULT '',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

-- =====================================================
-- 4. 联动规则表 (linkage_rule)
-- =====================================================
CREATE TABLE IF NOT EXISTS linkage_rule (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    rule_name VARCHAR(128) NOT NULL,
    rule_code VARCHAR(64) NOT NULL,
    alarm_rule_id BIGINT,
    alarm_type_id BIGINT,
    alarm_category_id BIGINT,
    alarm_level VARCHAR(16),
    condition_expression CLOB,
    actions CLOB NOT NULL,
    execution_mode VARCHAR(16) DEFAULT 'SERIAL',
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    priority INT DEFAULT 0,
    description CLOB,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    creator VARCHAR(64) DEFAULT '',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater VARCHAR(64) DEFAULT '',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

-- =====================================================
-- 5. 联动执行记录表 (linkage_execution)
-- =====================================================
CREATE TABLE IF NOT EXISTS linkage_execution (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    alarm_id BIGINT NOT NULL,
    linkage_rule_id BIGINT NOT NULL,
    action_type VARCHAR(32) NOT NULL,
    action_config CLOB,
    target_device_id BIGINT,
    target_device_name VARCHAR(128),
    execution_status VARCHAR(16) NOT NULL,
    retry_count INT DEFAULT 0,
    execution_result CLOB,
    error_message CLOB,
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

-- =====================================================
-- 6. 告警审计日志表 (alarm_audit_log)
-- =====================================================
CREATE TABLE IF NOT EXISTS alarm_audit_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    alarm_id BIGINT NOT NULL,
    operation_type VARCHAR(32) NOT NULL,
    operation_content CLOB,
    operator_id BIGINT,
    operator_name VARCHAR(64),
    operation_time TIMESTAMP NOT NULL,
    ip_address VARCHAR(64),
    tenant_id BIGINT NOT NULL DEFAULT 0,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
