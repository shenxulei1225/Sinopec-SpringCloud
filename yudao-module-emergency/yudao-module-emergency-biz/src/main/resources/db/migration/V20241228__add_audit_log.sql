-- 创建审计日志表
CREATE TABLE IF NOT EXISTS emergency_audit_log (
    id BIGSERIAL NOT NULL,
    event_id BIGINT DEFAULT NULL,
    response_id BIGINT DEFAULT NULL,
    task_id BIGINT DEFAULT NULL,
    operation_type VARCHAR(50) NOT NULL,
    operation_module VARCHAR(50) NOT NULL,
    operation_content TEXT,
    operator_id BIGINT NOT NULL,
    operator_name VARCHAR(64) NOT NULL,
    operation_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    operation_result VARCHAR(20) DEFAULT 'success',
    error_message TEXT DEFAULT NULL,
    request_ip VARCHAR(50) DEFAULT NULL,
    user_agent VARCHAR(500) DEFAULT NULL,
    extra_info JSONB DEFAULT NULL,
    creator VARCHAR(64) DEFAULT '',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater VARCHAR(64) DEFAULT '',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_audit_log_event_id ON emergency_audit_log(event_id, tenant_id, deleted);
CREATE INDEX IF NOT EXISTS idx_audit_log_response_id ON emergency_audit_log(response_id, tenant_id, deleted);
CREATE INDEX IF NOT EXISTS idx_audit_log_task_id ON emergency_audit_log(task_id, tenant_id, deleted);
CREATE INDEX IF NOT EXISTS idx_audit_log_operation_type ON emergency_audit_log(operation_type, tenant_id, deleted);
CREATE INDEX IF NOT EXISTS idx_audit_log_operation_module ON emergency_audit_log(operation_module, tenant_id, deleted);
CREATE INDEX IF NOT EXISTS idx_audit_log_operator_id ON emergency_audit_log(operator_id, tenant_id, deleted);
CREATE INDEX IF NOT EXISTS idx_audit_log_operation_time ON emergency_audit_log(operation_time, tenant_id, deleted);
CREATE INDEX IF NOT EXISTS idx_audit_log_tenant_id ON emergency_audit_log(tenant_id);

-- 添加表和列注释
COMMENT ON TABLE emergency_audit_log IS '应急管理系统审计日志表';
COMMENT ON COLUMN emergency_audit_log.id IS '主键ID';
COMMENT ON COLUMN emergency_audit_log.event_id IS '关联的事件ID';
COMMENT ON COLUMN emergency_audit_log.response_id IS '关联的响应ID';
COMMENT ON COLUMN emergency_audit_log.task_id IS '关联的任务ID';
COMMENT ON COLUMN emergency_audit_log.operation_type IS '操作类型：create/update/delete/confirm/assess/start_response/close等';
COMMENT ON COLUMN emergency_audit_log.operation_module IS '操作模块：event/response/task/resource/plan/command等';
COMMENT ON COLUMN emergency_audit_log.operation_content IS '操作内容描述';
COMMENT ON COLUMN emergency_audit_log.operator_id IS '操作人ID';
COMMENT ON COLUMN emergency_audit_log.operator_name IS '操作人姓名（快照）';
COMMENT ON COLUMN emergency_audit_log.operation_time IS '操作时间';
COMMENT ON COLUMN emergency_audit_log.operation_result IS '操作结果：success/failure';
COMMENT ON COLUMN emergency_audit_log.error_message IS '错误信息（操作失败时）';
COMMENT ON COLUMN emergency_audit_log.request_ip IS '请求IP地址';
COMMENT ON COLUMN emergency_audit_log.user_agent IS '用户代理信息';
COMMENT ON COLUMN emergency_audit_log.extra_info IS '额外信息（JSON格式）';
COMMENT ON COLUMN emergency_audit_log.creator IS '创建者';
COMMENT ON COLUMN emergency_audit_log.create_time IS '创建时间';
COMMENT ON COLUMN emergency_audit_log.updater IS '更新者';
COMMENT ON COLUMN emergency_audit_log.update_time IS '更新时间';
COMMENT ON COLUMN emergency_audit_log.deleted IS '是否删除';
COMMENT ON COLUMN emergency_audit_log.tenant_id IS '租户编号';

-- 创建触发器自动更新 update_time（如果需要自动更新时间戳）
CREATE OR REPLACE FUNCTION update_emergency_audit_log_update_time()
RETURNS TRIGGER AS $$
BEGIN
    NEW.update_time = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_emergency_audit_log_update_time
    BEFORE UPDATE ON emergency_audit_log
    FOR EACH ROW
    EXECUTE FUNCTION update_emergency_audit_log_update_time();



