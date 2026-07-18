-- 动态 SQL 执行审计日志表
-- 用于记录所有动态 SQL 的执行情况,支持安全审计和问题排查
-- 业务规则：BR-STG-035

CREATE TABLE IF NOT EXISTS dynamic_dynamic_sql_audit_log (
    id BIGSERIAL PRIMARY KEY,
    table_name VARCHAR(128) NOT NULL,
    operation_type VARCHAR(32) NOT NULL,
    executed_sql TEXT,
    parameters TEXT,
    success BOOLEAN NOT NULL DEFAULT TRUE,
    error_message TEXT,
    affected_rows INTEGER,
    execution_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    operator_id BIGINT,
    operator_name VARCHAR(64),
    client_ip VARCHAR(64),
    request_id VARCHAR(64),
    tenant_id BIGINT NOT NULL DEFAULT 0,
    creator VARCHAR(64) DEFAULT '',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater VARCHAR(64) DEFAULT '',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

-- 添加注释
COMMENT ON TABLE dynamic_dynamic_sql_audit_log IS '动态 SQL 执行审计日志表';
COMMENT ON COLUMN dynamic_dynamic_sql_audit_log.id IS '主键ID';
COMMENT ON COLUMN dynamic_dynamic_sql_audit_log.table_name IS '操作的表名';
COMMENT ON COLUMN dynamic_dynamic_sql_audit_log.operation_type IS '操作类型（SELECT/INSERT/UPDATE/DELETE/CREATE_TABLE/ALTER_TABLE）';
COMMENT ON COLUMN dynamic_dynamic_sql_audit_log.executed_sql IS '执行的 SQL 语句';
COMMENT ON COLUMN dynamic_dynamic_sql_audit_log.parameters IS 'SQL 参数（JSON 格式）';
COMMENT ON COLUMN dynamic_dynamic_sql_audit_log.success IS '是否执行成功';
COMMENT ON COLUMN dynamic_dynamic_sql_audit_log.error_message IS '错误信息（执行失败时记录）';
COMMENT ON COLUMN dynamic_dynamic_sql_audit_log.affected_rows IS '影响的行数';
COMMENT ON COLUMN dynamic_dynamic_sql_audit_log.execution_time IS '执行时间';
COMMENT ON COLUMN dynamic_dynamic_sql_audit_log.operator_id IS '操作人ID';
COMMENT ON COLUMN dynamic_dynamic_sql_audit_log.operator_name IS '操作人名称';
COMMENT ON COLUMN dynamic_dynamic_sql_audit_log.client_ip IS '客户端 IP 地址';
COMMENT ON COLUMN dynamic_dynamic_sql_audit_log.request_id IS '请求 ID（用于关联请求链路）';
COMMENT ON COLUMN dynamic_dynamic_sql_audit_log.tenant_id IS '租户编号';

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_dynamic_sql_audit_log_table_name ON dynamic_dynamic_sql_audit_log(table_name);
CREATE INDEX IF NOT EXISTS idx_dynamic_sql_audit_log_operation_type ON dynamic_dynamic_sql_audit_log(operation_type);
CREATE INDEX IF NOT EXISTS idx_dynamic_sql_audit_log_execution_time ON dynamic_dynamic_sql_audit_log(execution_time);
CREATE INDEX IF NOT EXISTS idx_dynamic_sql_audit_log_success ON dynamic_dynamic_sql_audit_log(success);
CREATE INDEX IF NOT EXISTS idx_dynamic_sql_audit_log_operator_id ON dynamic_dynamic_sql_audit_log(operator_id);
CREATE INDEX IF NOT EXISTS idx_dynamic_sql_audit_log_tenant_id ON dynamic_dynamic_sql_audit_log(tenant_id);
