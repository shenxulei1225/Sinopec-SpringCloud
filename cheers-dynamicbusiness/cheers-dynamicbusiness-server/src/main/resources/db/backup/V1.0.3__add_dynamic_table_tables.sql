-- =====================================================
-- 基础服务模块数据库迁移脚本
-- 版本: V1.0.3
-- 日期: 2026-01-03
-- 描述: 添加动态表管理相关表（大数据量业务动态建表功能）
-- =====================================================

-- 动态表配置表
CREATE TABLE IF NOT EXISTS dynamic_dynamic_table (
    id BIGSERIAL PRIMARY KEY,
    model_id BIGINT NOT NULL,
    entity_type_code VARCHAR(64),
    table_name VARCHAR(128) NOT NULL,
    table_comment VARCHAR(500),
    column_config TEXT,
    status INT NOT NULL DEFAULT 1,
    version INT NOT NULL DEFAULT 1,
    last_sync_time TIMESTAMP,
    creator VARCHAR(64) NOT NULL,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater VARCHAR(64),
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE,
    tenant_id BIGINT DEFAULT 0
);

-- 动态表配置表索引
CREATE INDEX IF NOT EXISTS idx_dynamic_table_model_id ON dynamic_dynamic_table(model_id);
CREATE INDEX IF NOT EXISTS idx_dynamic_table_business_type ON dynamic_dynamic_table(entity_type_code);
CREATE INDEX IF NOT EXISTS idx_dynamic_table_table_name ON dynamic_dynamic_table(table_name);
CREATE INDEX IF NOT EXISTS idx_dynamic_table_tenant_id ON dynamic_dynamic_table(tenant_id);
CREATE INDEX IF NOT EXISTS idx_dynamic_table_status ON dynamic_dynamic_table(status);

-- 动态表配置表注释
COMMENT ON TABLE dynamic_dynamic_table IS '动态表配置表';
COMMENT ON COLUMN dynamic_dynamic_table.id IS '主键ID';
COMMENT ON COLUMN dynamic_dynamic_table.model_id IS '关联的业务模型ID';
COMMENT ON COLUMN dynamic_dynamic_table.entity_type_code IS '业务类型编码';
COMMENT ON COLUMN dynamic_dynamic_table.table_name IS '物理表名';
COMMENT ON COLUMN dynamic_dynamic_table.table_comment IS '表描述';
COMMENT ON COLUMN dynamic_dynamic_table.column_config IS '字段配置（JSON格式）';
COMMENT ON COLUMN dynamic_dynamic_table.status IS '表状态（1-正常,0-已删除,2-迁移中）';
COMMENT ON COLUMN dynamic_dynamic_table.version IS '当前版本号';
COMMENT ON COLUMN dynamic_dynamic_table.last_sync_time IS '最后同步时间';
COMMENT ON COLUMN dynamic_dynamic_table.tenant_id IS '租户ID';

-- 动态表字段配置表
CREATE TABLE IF NOT EXISTS dynamic_dynamic_table_column (
    id BIGSERIAL PRIMARY KEY,
    dynamic_table_id BIGINT NOT NULL,
    field_id BIGINT NOT NULL,
    column_name VARCHAR(64) NOT NULL,
    data_type VARCHAR(64) NOT NULL,
    nullable BOOLEAN DEFAULT TRUE,
    default_value VARCHAR(500),
    column_comment VARCHAR(500),
    sort_order INT DEFAULT 0,
    status INT NOT NULL DEFAULT 1,
    creator VARCHAR(64) NOT NULL,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater VARCHAR(64),
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE,
    tenant_id BIGINT DEFAULT 0
);

-- 动态表字段配置表索引
CREATE INDEX IF NOT EXISTS idx_dynamic_table_column_table_id ON dynamic_dynamic_table_column(dynamic_table_id);
CREATE INDEX IF NOT EXISTS idx_dynamic_table_column_field_id ON dynamic_dynamic_table_column(field_id);
CREATE INDEX IF NOT EXISTS idx_dynamic_table_column_tenant_id ON dynamic_dynamic_table_column(tenant_id);
CREATE INDEX IF NOT EXISTS idx_dynamic_table_column_status ON dynamic_dynamic_table_column(status);
CREATE UNIQUE INDEX IF NOT EXISTS uk_dynamic_table_column ON dynamic_dynamic_table_column(dynamic_table_id, field_id, tenant_id) WHERE deleted = FALSE;

-- 动态表字段配置表注释
COMMENT ON TABLE dynamic_dynamic_table_column IS '动态表字段配置表';
COMMENT ON COLUMN dynamic_dynamic_table_column.id IS '主键ID';
COMMENT ON COLUMN dynamic_dynamic_table_column.dynamic_table_id IS '关联的动态表ID';
COMMENT ON COLUMN dynamic_dynamic_table_column.field_id IS '关联的字段ID';
COMMENT ON COLUMN dynamic_dynamic_table_column.column_name IS '物理列名';
COMMENT ON COLUMN dynamic_dynamic_table_column.data_type IS '数据库数据类型';
COMMENT ON COLUMN dynamic_dynamic_table_column.nullable IS '是否可空';
COMMENT ON COLUMN dynamic_dynamic_table_column.default_value IS '默认值';
COMMENT ON COLUMN dynamic_dynamic_table_column.column_comment IS '列注释';
COMMENT ON COLUMN dynamic_dynamic_table_column.sort_order IS '排序顺序';
COMMENT ON COLUMN dynamic_dynamic_table_column.status IS '状态（1-正常,0-已删除）';
COMMENT ON COLUMN dynamic_dynamic_table_column.tenant_id IS '租户ID';

-- 动态表审计日志表
CREATE TABLE IF NOT EXISTS dynamic_dynamic_table_audit_log (
    id BIGSERIAL PRIMARY KEY,
    dynamic_table_id BIGINT NOT NULL,
    operation_type VARCHAR(32) NOT NULL,
    operation_desc VARCHAR(500),
    before_config TEXT,
    after_config TEXT,
    executed_sql TEXT,
    execute_result VARCHAR(32) NOT NULL,
    error_message TEXT,
    operation_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    operator_id BIGINT,
    operator_name VARCHAR(64),
    creator VARCHAR(64) NOT NULL,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater VARCHAR(64),
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE,
    tenant_id BIGINT DEFAULT 0
);

-- 动态表审计日志表索引
CREATE INDEX IF NOT EXISTS idx_dynamic_table_audit_log_table_id ON dynamic_dynamic_table_audit_log(dynamic_table_id);
CREATE INDEX IF NOT EXISTS idx_dynamic_table_audit_log_operation_type ON dynamic_dynamic_table_audit_log(operation_type);
CREATE INDEX IF NOT EXISTS idx_dynamic_table_audit_log_operation_time ON dynamic_dynamic_table_audit_log(operation_time);
CREATE INDEX IF NOT EXISTS idx_dynamic_table_audit_log_tenant_id ON dynamic_dynamic_table_audit_log(tenant_id);

-- 动态表审计日志表注释
COMMENT ON TABLE dynamic_dynamic_table_audit_log IS '动态表审计日志表';
COMMENT ON COLUMN dynamic_dynamic_table_audit_log.id IS '主键ID';
COMMENT ON COLUMN dynamic_dynamic_table_audit_log.dynamic_table_id IS '关联的动态表ID';
COMMENT ON COLUMN dynamic_dynamic_table_audit_log.operation_type IS '操作类型（CREATE_TABLE/ADD_COLUMN/MODIFY_COLUMN/DROP_COLUMN/DROP_TABLE）';
COMMENT ON COLUMN dynamic_dynamic_table_audit_log.operation_desc IS '操作描述';
COMMENT ON COLUMN dynamic_dynamic_table_audit_log.before_config IS '变更前的配置（JSON格式）';
COMMENT ON COLUMN dynamic_dynamic_table_audit_log.after_config IS '变更后的配置（JSON格式）';
COMMENT ON COLUMN dynamic_dynamic_table_audit_log.executed_sql IS '执行的SQL语句';
COMMENT ON COLUMN dynamic_dynamic_table_audit_log.execute_result IS '执行结果（SUCCESS/FAILED）';
COMMENT ON COLUMN dynamic_dynamic_table_audit_log.error_message IS '错误信息';
COMMENT ON COLUMN dynamic_dynamic_table_audit_log.operation_time IS '操作时间';
COMMENT ON COLUMN dynamic_dynamic_table_audit_log.operator_id IS '操作人ID';
COMMENT ON COLUMN dynamic_dynamic_table_audit_log.operator_name IS '操作人名称';
COMMENT ON COLUMN dynamic_dynamic_table_audit_log.tenant_id IS '租户ID';
