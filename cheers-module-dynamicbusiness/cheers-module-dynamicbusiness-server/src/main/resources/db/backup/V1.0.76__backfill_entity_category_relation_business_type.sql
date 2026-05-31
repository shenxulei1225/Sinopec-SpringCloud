-- =====================================================
-- 回填 dynamic_entity_category_relation.business_type_code 历史空值
-- 日期：2026-03-20
-- 说明：
-- 1) 仅回填 business_type_code 为空的记录
-- 2) 从 dynamic_entity.business_type_code 回填
-- 3) 按 tenant_id 对齐，避免跨租户误更新
-- =====================================================

UPDATE dynamic_entity_category_relation r
SET business_type_code = e.business_type_code,
    update_time = CURRENT_TIMESTAMP,
    updater = 'flyway-backfill'
FROM dynamic_entity e
WHERE r.entity_id = e.id
  AND COALESCE(r.tenant_id, 0) = COALESCE(e.tenant_id, 0)
  AND r.deleted = FALSE
  AND e.deleted = FALSE
  AND (r.business_type_code IS NULL OR btrim(r.business_type_code) = '')
  AND e.business_type_code IS NOT NULL
  AND btrim(e.business_type_code) <> '';
