-- 事件主题的字段说明要由「事件主题字段说明」插件画成内容表。
-- 清单里没有这一条时，前端会关掉插件，详情只剩 JSON 原文。
SET search_path TO dynamicbusiness, public;

INSERT INTO dynamic_business_plugin_manifest (
  tenant_id, plugin_id, version, enabled, platform_range, entry,
  depends_on, permissions, semantic_contributions,
  creator, create_time, updater, update_time, deleted
)
SELECT
  t.tenant_id, 'event-payload-schema', '1.0.0', TRUE, '>=1.0.0',
  'framework/event-payload/plugin/manifest.ts',
  '[]'::jsonb,
  '["structured-fields:register"]'::jsonb,
  '["EVENT_PAYLOAD_SCHEMA"]'::jsonb,
  'flyway-v190', CURRENT_TIMESTAMP, 'flyway-v190', CURRENT_TIMESTAMP, FALSE
FROM (
  SELECT DISTINCT tenant_id
  FROM dynamic_business_plugin_manifest
  WHERE deleted = false
  UNION
  SELECT DISTINCT tenant_id
  FROM dynamic_field
  WHERE deleted = false
    AND code = 'event_payload_schema'
) t
WHERE NOT EXISTS (
  SELECT 1
  FROM dynamic_business_plugin_manifest existing
  WHERE existing.deleted = false
    AND existing.tenant_id = t.tenant_id
    AND existing.plugin_id = 'event-payload-schema'
);
