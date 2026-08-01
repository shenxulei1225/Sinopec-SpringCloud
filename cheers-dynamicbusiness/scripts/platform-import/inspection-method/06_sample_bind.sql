-- ============================================================================
-- inspection-method · 06 样例：一条方法模板 + 绑定一条检查内容（手工验收用，幂等）
-- 前置：05_patch + V44 已执行；inspection_method 型号存在
-- ============================================================================

SET search_path TO dynamicbusiness;

-- 方法模板（is_template=true）
INSERT INTO ent_inspection_method_t1 (
  tenant_id, entity_type_code, model_id, name, code, status,
  parent_id, tree_path, sort, is_template, action_duration_sec,
  attrs, custom_fields, creator, deleted
)
SELECT
  1,
  'inspection_method',
  m.id,
  '目视检查模板',
  'IM-TPL-VISUAL-01',
  1,
  0,
  NULL,
  0,
  TRUE,
  60,
  '{}'::jsonb,
  '{}'::jsonb,
  'seed',
  FALSE
FROM dynamic_model m
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'inspection_method'
  AND NOT EXISTS (
    SELECT 1 FROM ent_inspection_method_t1 e
    WHERE e.deleted = false AND e.tenant_id = 1 AND e.code = 'IM-TPL-VISUAL-01'
  );

-- 绑定检查内容 INS-ITEM-101（若存在）
UPDATE ent_inspection_item_t1 i
SET
  method_template_id = t.id,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
FROM ent_inspection_method_t1 t
WHERE i.deleted = false
  AND i.tenant_id = 1
  AND i.code = 'INS-ITEM-101'
  AND t.deleted = false
  AND t.tenant_id = 1
  AND t.code = 'IM-TPL-VISUAL-01';
