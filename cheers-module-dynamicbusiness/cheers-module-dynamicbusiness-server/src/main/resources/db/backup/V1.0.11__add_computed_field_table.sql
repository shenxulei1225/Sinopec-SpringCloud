-- =====================================================
-- 基础服务模块数据库迁移脚本
-- 版本: V1.0.11
-- 日期: 2026-01-05
-- 描述: 添加计算字段配置表
-- 需求: FR-BDA-050~055
-- =====================================================

-- 计算字段配置表
CREATE TABLE IF NOT EXISTS dynamic_computed_field (
    id BIGSERIAL PRIMARY KEY,
    
    -- 基本信息
    model_id BIGINT NOT NULL,
    field_name VARCHAR(64) NOT NULL,
    field_code VARCHAR(64) NOT NULL,
    
    -- 计算类型
    compute_type VARCHAR(32) NOT NULL,
    
    -- 聚合统计配置
    aggregate_function VARCHAR(16),
    target_business_type VARCHAR(64),
    target_model_code VARCHAR(64),
    target_field_code VARCHAR(64),
    relation_condition JSONB,
    filter_condition JSONB,
    
    -- 公式计算配置
    formula_expression VARCHAR(512),
    formula_fields JSONB,
    
    -- 结果配置
    result_type VARCHAR(32) NOT NULL,
    decimal_places INT DEFAULT 0,
    null_display VARCHAR(32) DEFAULT '0',
    
    -- 性能配置
    compute_strategy VARCHAR(32) DEFAULT 'REALTIME',
    cache_ttl_minutes INT DEFAULT 5,
    
    -- 元数据
    description VARCHAR(512),
    sort_order INT DEFAULT 0,
    
    -- 通用字段
    tenant_id BIGINT NOT NULL DEFAULT 0,
    creator VARCHAR(64) DEFAULT '',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater VARCHAR(64) DEFAULT '',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

-- 索引
CREATE INDEX IF NOT EXISTS idx_computed_field_model 
    ON dynamic_computed_field(model_id) 
    WHERE deleted = FALSE;

CREATE UNIQUE INDEX IF NOT EXISTS idx_computed_field_code 
    ON dynamic_computed_field(model_id, field_code, tenant_id) 
    WHERE deleted = FALSE;

CREATE INDEX IF NOT EXISTS idx_computed_field_type 
    ON dynamic_computed_field(compute_type) 
    WHERE deleted = FALSE;

CREATE INDEX IF NOT EXISTS idx_computed_field_strategy 
    ON dynamic_computed_field(compute_strategy) 
    WHERE deleted = FALSE;

CREATE INDEX IF NOT EXISTS idx_computed_field_target 
    ON dynamic_computed_field(target_business_type, target_model_code) 
    WHERE deleted = FALSE AND compute_type = 'AGGREGATE';

CREATE INDEX IF NOT EXISTS idx_computed_field_tenant 
    ON dynamic_computed_field(tenant_id) 
    WHERE deleted = FALSE;

-- 注释
COMMENT ON TABLE dynamic_computed_field IS '计算字段配置表';
COMMENT ON COLUMN dynamic_computed_field.model_id IS '所属 Model ID';
COMMENT ON COLUMN dynamic_computed_field.field_name IS '字段名称';
COMMENT ON COLUMN dynamic_computed_field.field_code IS '字段编码';
COMMENT ON COLUMN dynamic_computed_field.compute_type IS '计算类型：AGGREGATE/FORMULA';
COMMENT ON COLUMN dynamic_computed_field.aggregate_function IS '聚合函数：COUNT/SUM/AVG/MAX/MIN';
COMMENT ON COLUMN dynamic_computed_field.target_business_type IS '统计目标业务类型';
COMMENT ON COLUMN dynamic_computed_field.target_model_code IS '统计目标 Model';
COMMENT ON COLUMN dynamic_computed_field.target_field_code IS '统计目标字段';
COMMENT ON COLUMN dynamic_computed_field.relation_condition IS '关联条件（JSON格式）';
COMMENT ON COLUMN dynamic_computed_field.filter_condition IS '筛选条件（JSON格式）';
COMMENT ON COLUMN dynamic_computed_field.formula_expression IS '公式表达式';
COMMENT ON COLUMN dynamic_computed_field.formula_fields IS '公式引用的字段列表';
COMMENT ON COLUMN dynamic_computed_field.result_type IS '结果类型：NUMBER/PERCENTAGE/DECIMAL';
COMMENT ON COLUMN dynamic_computed_field.decimal_places IS '小数位数';
COMMENT ON COLUMN dynamic_computed_field.null_display IS '空值显示';
COMMENT ON COLUMN dynamic_computed_field.compute_strategy IS '计算策略：REALTIME/CACHED/PRECOMPUTED';
COMMENT ON COLUMN dynamic_computed_field.cache_ttl_minutes IS '缓存时间（分钟）';

