-- ============================================================================
-- sop · 06 样例 SOP（目视检查）
-- 检查项—SOP 绑定走矩阵 dynamic_entity_relation + REF 字段，不在此 seed
-- ============================================================================

SET search_path TO dynamicbusiness;

INSERT INTO ent_sop_t1 (
  tenant_id, entity_type_code, model_id, name, code, status,
  version_no, publish_status, steps_json, creator, deleted
)
SELECT
  1,
  'sop',
  m.id,
  '目视检查',
  'SOP-VISUAL-01',
  1,
  1,
  'PUBLISHED',
  '[
    {"code":"step-1","order":1,"title":"确认检查对象","required":true,"stepType":"confirm","description":"核对设备标识与检查项范围"},
    {"code":"step-2","order":2,"title":"目视检查","required":true,"stepType":"inspect","description":"按标准观察外观、泄漏、异常声响等"},
    {"code":"step-3","order":3,"title":"记录结论","required":true,"stepType":"confirm","description":"填写正常/异常及必要说明"}
  ]'::jsonb,
  'seed',
  false
FROM dynamic_model m
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'sop'
  AND NOT EXISTS (
    SELECT 1 FROM ent_sop_t1 s
    WHERE s.deleted = false AND s.tenant_id = 1 AND s.code = 'SOP-VISUAL-01'
  );
