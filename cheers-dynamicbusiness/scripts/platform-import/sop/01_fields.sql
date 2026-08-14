-- ============================================================================
-- sop · 01 字段库：version_no / publish_status / steps_json
-- ============================================================================

SET search_path TO dynamicbusiness;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES
  (
    'version_no', '版本号', 'INTEGER', NULL,
    'SOP 版本号；同 code 下递增', 'BASE', 1, 1, 'NONE',
    NULL, NULL, 'version_no', 1, 'seed'
  ),
  (
    'publish_status', '发布状态', 'TEXT', NULL,
    'DRAFT=草稿；PUBLISHED=已发布', 'BASE', 1, 1, 'NONE',
    NULL, NULL, 'publish_status', 1, 'seed'
  ),
  (
    'steps_json', '步骤定义', 'TEXT', NULL,
    '有序步骤 JSON 数组（怎么做唯一正文）', 'BASE', 1, 1, 'NONE',
    NULL, NULL, 'steps_json', 1, 'seed'
  )
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;
