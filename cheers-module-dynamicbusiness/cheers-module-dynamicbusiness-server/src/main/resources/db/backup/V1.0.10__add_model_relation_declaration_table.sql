-- =====================================================
-- 基础服务模块数据库迁移脚本
-- 版本: V1.0.10
-- 日期: 2026-01-05
-- 描述: 添加 Model 关联声明表
-- 需求: FR-BDA-020~023
-- =====================================================

-- Model 关联声明表
CREATE TABLE IF NOT EXISTS dynamic_model_relation_declaration (
    id BIGSERIAL PRIMARY KEY,
    
    -- 关联定义
    model_id BIGINT NOT NULL,
    target_business_type VARCHAR(64) NOT NULL,
    
    -- 继承标记
    is_inherited BOOLEAN DEFAULT FALSE,
    source_model_id BIGINT,
    
    -- 通用字段
    tenant_id BIGINT NOT NULL DEFAULT 0,
    creator VARCHAR(64) DEFAULT '',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater VARCHAR(64) DEFAULT '',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

-- 索引
CREATE UNIQUE INDEX IF NOT EXISTS idx_mrd_model_target 
    ON dynamic_model_relation_declaration(model_id, target_business_type, tenant_id) 
    WHERE deleted = FALSE;

CREATE INDEX IF NOT EXISTS idx_mrd_model 
    ON dynamic_model_relation_declaration(model_id) 
    WHERE deleted = FALSE;

CREATE INDEX IF NOT EXISTS idx_mrd_target 
    ON dynamic_model_relation_declaration(target_business_type) 
    WHERE deleted = FALSE;

CREATE INDEX IF NOT EXISTS idx_mrd_inherited 
    ON dynamic_model_relation_declaration(is_inherited) 
    WHERE deleted = FALSE AND is_inherited = TRUE;

CREATE INDEX IF NOT EXISTS idx_mrd_source_model 
    ON dynamic_model_relation_declaration(source_model_id) 
    WHERE deleted = FALSE AND source_model_id IS NOT NULL;

-- 注释
COMMENT ON TABLE dynamic_model_relation_declaration IS 'Model 关联声明表';
COMMENT ON COLUMN dynamic_model_relation_declaration.model_id IS 'Model ID';
COMMENT ON COLUMN dynamic_model_relation_declaration.target_business_type IS '可关联的业务类型编码';
COMMENT ON COLUMN dynamic_model_relation_declaration.is_inherited IS '是否继承自模板';
COMMENT ON COLUMN dynamic_model_relation_declaration.source_model_id IS '来源模板 ID';

