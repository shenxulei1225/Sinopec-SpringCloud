-- =====================================================
-- 数据迁移支持脚本
-- 版本: V1.0.23
-- 描述: 为业务动态关联功能提供数据迁移支持
-- 需求: 数据迁移
-- =====================================================

-- 1. 为 system_relation_field_library 添加迁移标记字段
-- 用于标识记录是通过迁移创建的还是手动创建的
ALTER TABLE system_relation_field_library 
ADD COLUMN IF NOT EXISTS migration_source VARCHAR(64);

COMMENT ON COLUMN system_relation_field_library.migration_source IS '迁移来源：MIGRATION-迁移创建,MANUAL-手动创建,NULL-未知';

-- 2. 为 system_model_field_assignment 添加迁移状态字段
-- 用于跟踪字段分配的迁移状态
ALTER TABLE system_model_field_assignment 
ADD COLUMN IF NOT EXISTS migration_status VARCHAR(32);

COMMENT ON COLUMN system_model_field_assignment.migration_status IS '迁移状态：MIGRATED-已迁移,PENDING-待迁移,NULL-未迁移';

-- 3. 创建数据迁移日志表
-- 记录每次迁移的执行情况
CREATE TABLE IF NOT EXISTS system_data_migration_log (
    id BIGSERIAL PRIMARY KEY,
    
    -- 迁移信息
    migration_type VARCHAR(64) NOT NULL,           -- 迁移类型
    migration_version VARCHAR(32),                  -- 迁移版本
    
    -- 执行信息
    dry_run BOOLEAN NOT NULL DEFAULT FALSE,        -- 是否试运行
    start_time TIMESTAMP NOT NULL,                  -- 开始时间
    end_time TIMESTAMP,                             -- 结束时间
    duration_ms BIGINT,                             -- 耗时（毫秒）
    
    -- 结果统计
    total_count INTEGER DEFAULT 0,                  -- 处理总数
    success_count INTEGER DEFAULT 0,                -- 成功数
    skipped_count INTEGER DEFAULT 0,                -- 跳过数
    failed_count INTEGER DEFAULT 0,                 -- 失败数
    
    -- 状态
    status VARCHAR(32) NOT NULL DEFAULT 'RUNNING', -- 状态：RUNNING/SUCCESS/FAILED/ROLLED_BACK
    error_message TEXT,                             -- 错误信息
    
    -- 详情（JSON 格式）
    details JSONB,                                  -- 迁移详情
    
    -- 通用字段
    tenant_id BIGINT NOT NULL DEFAULT 0,
    creator VARCHAR(64) DEFAULT '',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater VARCHAR(64) DEFAULT '',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

-- 索引
CREATE INDEX IF NOT EXISTS idx_migration_log_type ON system_data_migration_log(migration_type) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_migration_log_status ON system_data_migration_log(status) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_migration_log_time ON system_data_migration_log(start_time DESC) WHERE deleted = FALSE;

-- 注释
COMMENT ON TABLE system_data_migration_log IS '数据迁移日志表';
COMMENT ON COLUMN system_data_migration_log.migration_type IS '迁移类型：RELATION_FIELD_MIGRATION/SMART_DEFAULTS_APPLICATION/FULL_MIGRATION';
COMMENT ON COLUMN system_data_migration_log.status IS '状态：RUNNING-执行中,SUCCESS-成功,FAILED-失败,ROLLED_BACK-已回滚';
