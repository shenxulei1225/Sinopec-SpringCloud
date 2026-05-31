-- 为 system_entity_field_index 表添加 tenant_id 字段
-- 该字段用于多租户支持,MyBatis Plus TenantLineInnerInterceptor 会自动添加租户条件

-- 添加 tenant_id 字段
ALTER TABLE system_entity_field_index ADD COLUMN IF NOT EXISTS tenant_id BIGINT NOT NULL DEFAULT 0;

-- 添加注释
COMMENT ON COLUMN system_entity_field_index.tenant_id IS '租户编号';

-- 创建索引以优化租户查询
CREATE INDEX IF NOT EXISTS idx_entity_field_index_tenant_id ON system_entity_field_index(tenant_id);
