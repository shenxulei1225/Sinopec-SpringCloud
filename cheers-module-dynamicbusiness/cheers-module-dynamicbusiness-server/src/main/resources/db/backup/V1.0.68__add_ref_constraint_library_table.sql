-- =====================================================
-- 基础服务模块数据库迁移脚本
-- 版本: V1.0.68
-- 日期: 2026-03-17
-- 描述: 添加约束器库表
-- =====================================================

CREATE TABLE IF NOT EXISTS dynamic_ref_constraint_library (
    id BIGSERIAL PRIMARY KEY,

    -- 业务类型维度
    entity_type_code VARCHAR(64) NOT NULL,
    ref_target_type VARCHAR(64),

    -- 约束器定义
    constraint_type VARCHAR(64) NOT NULL,
    constraint_name VARCHAR(64) NOT NULL,
    status INT NOT NULL DEFAULT 0,
    sort INT DEFAULT 0,

    -- 通用字段
    tenant_id BIGINT NOT NULL DEFAULT 0,
    creator VARCHAR(64) DEFAULT '',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater VARCHAR(64) DEFAULT '',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_ref_constraint_library_business
    ON dynamic_ref_constraint_library(entity_type_code, ref_target_type)
    WHERE deleted = FALSE;

CREATE UNIQUE INDEX IF NOT EXISTS idx_ref_constraint_library_unique
    ON dynamic_ref_constraint_library(entity_type_code, ref_target_type, constraint_type, tenant_id)
    WHERE deleted = FALSE;

COMMENT ON TABLE dynamic_ref_constraint_library IS 'Ref 约束器库表';
COMMENT ON COLUMN dynamic_ref_constraint_library.entity_type_code IS '业务类型编码';
COMMENT ON COLUMN dynamic_ref_constraint_library.ref_target_type IS 'Ref 目标类型（可选）';
COMMENT ON COLUMN dynamic_ref_constraint_library.constraint_type IS '约束器类型';
COMMENT ON COLUMN dynamic_ref_constraint_library.constraint_name IS '约束器名称';
COMMENT ON COLUMN dynamic_ref_constraint_library.status IS '状态：0-开启，1-关闭';
COMMENT ON COLUMN dynamic_ref_constraint_library.sort IS '排序值';
