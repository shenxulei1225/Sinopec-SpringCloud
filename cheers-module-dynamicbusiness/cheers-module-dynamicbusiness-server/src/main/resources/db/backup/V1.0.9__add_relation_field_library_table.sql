-- =====================================================
-- 基础服务模块数据库迁移脚本
-- 版本: V1.0.9
-- 日期: 2026-01-05
-- 描述: 添加关联字段库表
-- 需求: FR-BDA-010~016
-- =====================================================

-- 关联字段库表
CREATE TABLE IF NOT EXISTS dynamic_relation_field_library (
    id BIGSERIAL PRIMARY KEY,
    
    -- 字段定义
    field_name VARCHAR(64) NOT NULL,
    field_code VARCHAR(64) NOT NULL,
    
    -- 关联目标（松散引用）
    target_business_type VARCHAR(64) NOT NULL,
    target_model_code VARCHAR(64) NOT NULL,
    
    -- 展示配置
    display_field_code VARCHAR(64),

    -- 约束器配置
    constraint_enabled BOOLEAN DEFAULT FALSE,
    constraint_type VARCHAR(64) DEFAULT 'NONE',
    
    -- 元数据
    description VARCHAR(512),
    usage_count INT DEFAULT 0,
    is_system BOOLEAN DEFAULT FALSE,
    
    -- 通用字段
    tenant_id BIGINT NOT NULL DEFAULT 0,
    creator VARCHAR(64) DEFAULT '',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater VARCHAR(64) DEFAULT '',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

-- 索引
CREATE UNIQUE INDEX IF NOT EXISTS idx_relation_field_code 
    ON dynamic_relation_field_library(field_code, tenant_id) 
    WHERE deleted = FALSE;

CREATE INDEX IF NOT EXISTS idx_relation_field_target 
    ON dynamic_relation_field_library(target_business_type, target_model_code) 
    WHERE deleted = FALSE;

CREATE INDEX IF NOT EXISTS idx_relation_field_system 
    ON dynamic_relation_field_library(is_system) 
    WHERE deleted = FALSE AND is_system = TRUE;

CREATE INDEX IF NOT EXISTS idx_relation_field_tenant 
    ON dynamic_relation_field_library(tenant_id) 
    WHERE deleted = FALSE;

-- 注释
COMMENT ON TABLE dynamic_relation_field_library IS '关联字段库表';
COMMENT ON COLUMN dynamic_relation_field_library.field_name IS '字段名称';
COMMENT ON COLUMN dynamic_relation_field_library.field_code IS '字段编码（全局唯一）';
COMMENT ON COLUMN dynamic_relation_field_library.target_business_type IS '关联目标业务类型编码';
COMMENT ON COLUMN dynamic_relation_field_library.target_model_code IS '关联目标 Model 编码';
COMMENT ON COLUMN dynamic_relation_field_library.display_field_code IS '展示字段编码';
COMMENT ON COLUMN dynamic_relation_field_library.constraint_enabled IS '是否启用约束器';
COMMENT ON COLUMN dynamic_relation_field_library.constraint_type IS '约束器类型';
COMMENT ON COLUMN dynamic_relation_field_library.description IS '字段说明';
COMMENT ON COLUMN dynamic_relation_field_library.usage_count IS '使用次数';
COMMENT ON COLUMN dynamic_relation_field_library.is_system IS '是否系统预置';

