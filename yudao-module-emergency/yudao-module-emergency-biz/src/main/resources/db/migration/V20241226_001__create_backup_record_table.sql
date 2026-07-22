-- 创建备份记录表
CREATE TABLE IF NOT EXISTS emergency_backup_record (
    id                  BIGSERIAL PRIMARY KEY,
    backup_name         VARCHAR(200) NOT NULL,
    backup_type         VARCHAR(50) NOT NULL,  -- FULL/TENANT/MODULE
    backup_path         VARCHAR(500) NOT NULL,
    file_size           BIGINT,
    tenant_id           BIGINT,
    module_name         VARCHAR(100),  -- 模块名称（当backup_type为MODULE时）
    status              VARCHAR(50) NOT NULL DEFAULT 'IN_PROGRESS',  -- SUCCESS/FAILED/IN_PROGRESS
    error_message       TEXT,
    description         TEXT,
    create_time         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    creator             VARCHAR(64),
    deleted             BOOLEAN NOT NULL DEFAULT FALSE,
    tenant_id_backup    BIGINT DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_backup_record_type ON emergency_backup_record(backup_type, deleted);
CREATE INDEX IF NOT EXISTS idx_backup_record_status ON emergency_backup_record(status, deleted);
CREATE INDEX IF NOT EXISTS idx_backup_record_tenant ON emergency_backup_record(tenant_id, deleted);
CREATE INDEX IF NOT EXISTS idx_backup_record_time ON emergency_backup_record(create_time, deleted);

COMMENT ON TABLE emergency_backup_record IS '数据备份记录表';
COMMENT ON COLUMN emergency_backup_record.backup_name IS '备份名称';
COMMENT ON COLUMN emergency_backup_record.backup_type IS '备份类型：FULL-全量备份，TENANT-按租户备份，MODULE-按模块备份';
COMMENT ON COLUMN emergency_backup_record.backup_path IS '备份文件路径';
COMMENT ON COLUMN emergency_backup_record.file_size IS '备份文件大小（字节）';
COMMENT ON COLUMN emergency_backup_record.tenant_id IS '租户ID（当backup_type为TENANT时）';
COMMENT ON COLUMN emergency_backup_record.module_name IS '模块名称（当backup_type为MODULE时）';
COMMENT ON COLUMN emergency_backup_record.status IS '备份状态：SUCCESS-成功，FAILED-失败，IN_PROGRESS-进行中';
COMMENT ON COLUMN emergency_backup_record.error_message IS '错误信息（备份失败时）';
COMMENT ON COLUMN emergency_backup_record.description IS '备份描述';








