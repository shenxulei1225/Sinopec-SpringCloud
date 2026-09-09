-- V102: 注册检查域插件清单（标准检查库详情 · 怎么查语义）
-- 与前端 inspection-library 插件对齐；启停以后端清单为准。

SET search_path TO dynamicbusiness, public;

UPDATE dynamic_business_plugin_manifest
SET
  version = '1.0.0',
  enabled = TRUE,
  platform_range = '>=1.0.0',
  entry = 'features/inspection/plugin/manifest.ts',
  depends_on = '[]'::jsonb,
  permissions = '["work-blocks:detail","structured-fields:register"]'::jsonb,
  semantic_contributions = '["INSPECTION_SOP_HOW"]'::jsonb,
  updater = 'flyway',
  update_time = CURRENT_TIMESTAMP
WHERE deleted = FALSE
  AND tenant_id = 1
  AND plugin_id = 'inspection-library';

INSERT INTO dynamic_business_plugin_manifest (
  tenant_id, plugin_id, version, enabled, platform_range, entry,
  depends_on, permissions, semantic_contributions,
  creator, create_time, updater, update_time, deleted
)
SELECT
  1, 'inspection-library', '1.0.0', TRUE, '>=1.0.0', 'features/inspection/plugin/manifest.ts',
  '[]'::jsonb,
  '["work-blocks:detail","structured-fields:register"]'::jsonb,
  '["INSPECTION_SOP_HOW"]'::jsonb,
  'flyway', CURRENT_TIMESTAMP, 'flyway', CURRENT_TIMESTAMP, FALSE
WHERE NOT EXISTS (
  SELECT 1
  FROM dynamic_business_plugin_manifest
  WHERE deleted = FALSE
    AND tenant_id = 1
    AND plugin_id = 'inspection-library'
);
