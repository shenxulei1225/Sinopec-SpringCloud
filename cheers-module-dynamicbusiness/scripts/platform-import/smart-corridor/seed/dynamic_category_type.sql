-- ============================================================================
-- 管廊 · dynamic_category_type
-- Generated: 2026-07-08 by scripts/export-platform-import.py
--
-- 约定：不写 surrogate id；幂等键为 code / field_code / page_code。
-- 幂等 upsert；关联字段按 code 解析 id，不写 surrogate id
-- ============================================================================

SET search_path TO dynamicbusiness;

-- dynamic_category_type: 1 row(s), upsert by category_type_code

INSERT INTO dynamic_category_type (
  category_type_code, name, description, status, top_level_category_id, tenant_id, creator
) VALUES (
  'view', '视图分类',
  '平台视图库资源分组（categoryTypeCode=view）', 1,
  (SELECT c.id FROM dynamic_category c WHERE c.deleted = false AND c.tenant_id = 1 AND c.code = 'view_root' LIMIT 1), 1, 'seed'
)
ON CONFLICT (category_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  top_level_category_id = EXCLUDED.top_level_category_id,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;
