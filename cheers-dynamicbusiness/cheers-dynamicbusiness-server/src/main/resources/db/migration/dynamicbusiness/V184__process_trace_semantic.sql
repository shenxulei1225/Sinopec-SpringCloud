-- 存量「处理脉络」列一次性写上用途。运行时只认用途，不再按中文名认。
-- 不另建列。告警处置仍往原来那一列追加原文。

UPDATE dynamic_field
SET semantic_type = 'PROCESS_TRACE',
    updater = 'flyway-v184',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND name = '处理脉络'
  AND (semantic_type IS NULL OR btrim(semantic_type) = '');

INSERT INTO dynamic_business_plugin_manifest (
  tenant_id, plugin_id, version, enabled, platform_range, entry,
  depends_on, permissions, semantic_contributions,
  creator, create_time, updater, update_time, deleted
)
SELECT
  t.tenant_id, 'process-trace', '1.0.0', TRUE, '>=1.0.0', 'framework/process-trace/plugin/manifest.ts',
  '[]'::jsonb,
  '["structured-fields:register"]'::jsonb,
  '["PROCESS_TRACE"]'::jsonb,
  'flyway-v184', CURRENT_TIMESTAMP, 'flyway-v184', CURRENT_TIMESTAMP, FALSE
FROM (
  SELECT DISTINCT tenant_id
  FROM dynamic_business_plugin_manifest
  WHERE deleted = false
  UNION
  SELECT DISTINCT tenant_id
  FROM dynamic_field
  WHERE deleted = false
    AND name = '处理脉络'
) t
WHERE NOT EXISTS (
  SELECT 1
  FROM dynamic_business_plugin_manifest existing
  WHERE existing.deleted = false
    AND existing.tenant_id = t.tenant_id
    AND existing.plugin_id = 'process-trace'
);
