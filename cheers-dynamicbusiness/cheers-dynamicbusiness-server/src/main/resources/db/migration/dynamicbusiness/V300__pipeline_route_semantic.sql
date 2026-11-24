-- 存量「走向」列一次性写上用途。运行时只认用途，不再按目录类型画地图。
-- 不另建列。测过的线路仍写原来那一列。场站不抄进这一列。

UPDATE dynamic_field
SET semantic_type = 'PIPELINE_ROUTE',
    updater = 'flyway-v300',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND code = 'route_geojson'
  AND (semantic_type IS NULL OR btrim(semantic_type) = '');

INSERT INTO dynamic_business_plugin_manifest (
  tenant_id, plugin_id, version, enabled, platform_range, entry,
  depends_on, permissions, semantic_contributions,
  creator, create_time, updater, update_time, deleted
)
SELECT
  t.tenant_id, 'pipeline-route', '1.0.0', TRUE, '>=1.0.0', 'framework/pipeline-route/plugin/manifest.ts',
  '[]'::jsonb,
  '["structured-fields:register"]'::jsonb,
  '["PIPELINE_ROUTE"]'::jsonb,
  'flyway-v300', CURRENT_TIMESTAMP, 'flyway-v300', CURRENT_TIMESTAMP, FALSE
FROM (
  SELECT DISTINCT tenant_id
  FROM dynamic_business_plugin_manifest
  WHERE deleted = false
  UNION
  SELECT DISTINCT tenant_id
  FROM dynamic_field
  WHERE deleted = false
    AND code = 'route_geojson'
) t
WHERE NOT EXISTS (
  SELECT 1
  FROM dynamic_business_plugin_manifest existing
  WHERE existing.deleted = false
    AND existing.tenant_id = t.tenant_id
    AND existing.plugin_id = 'pipeline-route'
);
