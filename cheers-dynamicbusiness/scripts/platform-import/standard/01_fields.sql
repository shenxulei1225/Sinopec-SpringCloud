-- ============================================================================
-- standard · 01 字段库：规范标准 6 个业务字段
-- ============================================================================

SET search_path TO dynamicbusiness;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES
  (
    'standard_no', '标准编号', 'STRING', NULL,
    '标准编号（如 GB50251-2015）；实体 code 用稳定短码', 'BASE', 1, 1, 'NONE',
    NULL, NULL, 'standard_no', 1, 'seed'
  ),
  (
    'standard_level', '标准级别', 'ENUM', NULL,
    'NATIONAL=国标；INDUSTRY=行标；ENTERPRISE=企标', 'BASE', 1, 1, 'NONE',
    '[{"label":"国标","value":"NATIONAL"},{"label":"行标","value":"INDUSTRY"},{"label":"企标","value":"ENTERPRISE"}]'::jsonb,
    NULL, 'standard_level', 1, 'seed'
  ),
  (
    'issuing_body', '发布单位', 'STRING', NULL,
    '标准发布单位', 'BASE', 1, 1, 'NONE',
    NULL, NULL, 'issuing_body', 1, 'seed'
  ),
  (
    'publish_year', '发布年份', 'INTEGER', NULL,
    '标准发布年份', 'BASE', 1, 1, 'NONE',
    NULL, NULL, 'publish_year', 1, 'seed'
  ),
  (
    'summary', '摘要说明', 'TEXT', NULL,
    '来自参考资料的用途简述', 'BASE', 1, 1, 'NONE',
    NULL, NULL, 'summary', 1, 'seed'
  ),
  (
    'source_ref', '引用出处', 'TEXT', NULL,
    '哪份规格书/招标文件中出现', 'BASE', 1, 1, 'NONE',
    NULL, NULL, 'source_ref', 1, 'seed'
  )
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;
