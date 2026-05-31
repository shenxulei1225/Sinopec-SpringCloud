-- =====================================================
-- 基础服务模块数据库迁移脚本
-- 版本: V1.0.79
-- 日期: 2026-03-25
-- 描述: 优化 dynamic_entity_category_relation 在分类视图下的有序查询索引
-- =====================================================

-- 查询模式：
-- WHERE category_id IN (...) AND business_type_code = ? AND tenant_id = ? AND deleted = false
-- ORDER BY sort ASC, id ASC
--
-- 该索引用于减少排序回表与额外排序开销，提升分类维度实体ID有序查询性能。
CREATE INDEX IF NOT EXISTS idx_ecr_category_business_tenant_sort_id_not_deleted
ON dynamic_entity_category_relation (category_id, business_type_code, tenant_id, sort, id)
WHERE deleted = false;
