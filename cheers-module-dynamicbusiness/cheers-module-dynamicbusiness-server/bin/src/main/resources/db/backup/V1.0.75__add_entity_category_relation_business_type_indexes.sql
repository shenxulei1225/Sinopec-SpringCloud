-- =====================================================
-- 为 system_entity_category_relation 补充 business_type_code 相关索引
-- 日期：2026-03-20
-- 说明：
-- 1) 历史库可能缺少 business_type_code 字段，先做幂等补列
-- 2) 为按分类+业务类型查询、按实体+业务类型查询增加部分索引（仅 deleted = FALSE）
-- =====================================================

-- 0. 兼容历史库：补充 business_type_code 字段（如已存在则跳过）
ALTER TABLE system_entity_category_relation
    ADD COLUMN IF NOT EXISTS business_type_code VARCHAR(64);

COMMENT ON COLUMN system_entity_category_relation.business_type_code IS '实体业务类型编码（如 region、equipment）';

-- 1. 场景：按 categoryId + businessTypeCode 查实体（Pattern C 等）
CREATE INDEX IF NOT EXISTS idx_ecr_category_business_tenant_not_deleted
    ON system_entity_category_relation (category_id, business_type_code, tenant_id)
    WHERE deleted = FALSE;

-- 2. 场景：按 entityId + businessTypeCode 反查分类/做关联处理
CREATE INDEX IF NOT EXISTS idx_ecr_entity_business_tenant_not_deleted
    ON system_entity_category_relation (entity_id, business_type_code, tenant_id)
    WHERE deleted = FALSE;
