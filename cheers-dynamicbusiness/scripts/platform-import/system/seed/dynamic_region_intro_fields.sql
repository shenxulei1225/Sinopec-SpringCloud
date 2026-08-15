-- ============================================================================
-- 运营区域介绍页字段补强（2026-07-27）
-- 依赖：dynamic_entity_type_base_field / dynamic_field / dynamic_model(region) 已导入
-- 幂等：可重复执行；不写 surrogate id
-- 物理列不在本文件创建（seed 禁止 DDL）。专用表列见 Flyway V58。
-- ============================================================================

SET search_path TO dynamicbusiness;

-- 1) 实体类型基础字段
INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, library_field_id, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES
  ('region', NULL, 'FLD-BASE-region-cover_url', '头图', 'TEXT', FALSE, NULL,
   '介绍页头图 URL', NULL, 10, 1, 1, 'seed'),
  ('region', NULL, 'FLD-BASE-region-hq_location', '驻地', 'TEXT', FALSE, NULL,
   '本部 / 驻地所在地', NULL, 22, 1, 1, 'seed'),
  ('region', NULL, 'FLD-BASE-region-mission_summary', '宗旨定位', 'TEXT', FALSE, NULL,
   '一句话宗旨或定位，与区域说明分工', NULL, 23, 1, 1, 'seed'),
  ('region', NULL, 'FLD-BASE-region-pipeline_km_total', '管线里程', 'NUMBER', FALSE, NULL,
   '管线总里程（公里）', NULL, 30, 1, 1, 'seed'),
  ('region', NULL, 'FLD-BASE-region-pipeline_km_ng', '天然气里程', 'NUMBER', FALSE, NULL,
   '天然气管线里程（公里）', NULL, 31, 1, 1, 'seed'),
  ('region', NULL, 'FLD-BASE-region-pipeline_km_cr', '原油里程', 'NUMBER', FALSE, NULL,
   '原油管线里程（公里）', NULL, 32, 1, 1, 'seed'),
  ('region', NULL, 'FLD-BASE-region-pipeline_km_cp', '成品油里程', 'NUMBER', FALSE, NULL,
   '成品油管线里程（公里）', NULL, 33, 1, 1, 'seed'),
  ('region', NULL, 'FLD-BASE-region-storage_count', '储气库数量', 'INTEGER', FALSE, NULL,
   '地下储气库数量', NULL, 34, 1, 1, 'seed'),
  ('region', NULL, 'FLD-BASE-region-lng_terminal_count', 'LNG接收站数量', 'INTEGER', FALSE, NULL,
   'LNG 接收站数量', NULL, 35, 1, 1, 'seed'),
  ('region', NULL, 'FLD-BASE-region-coverage_note', '覆盖范围说明', 'TEXT', FALSE, NULL,
   '无边界几何时的范围文案', NULL, 36, 1, 1, 'seed'),
  ('region', NULL, 'FLD-BASE-region-org_mode_note', '管理模式说明', 'TEXT', FALSE, NULL,
   '如省公司—作业区两级', NULL, 37, 1, 1, 'seed'),
  ('region', NULL, 'FLD-BASE-region-service_radius_km', '管辖半径', 'NUMBER', FALSE, NULL,
   '管辖半径（公里）', NULL, 38, 1, 1, 'seed')
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  description = EXCLUDED.description,
  sort_order = EXCLUDED.sort_order,
  status = EXCLUDED.status,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

-- 2) 同步进字段库
INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
)
SELECT
  bf.field_code,
  bf.field_name,
  CASE bf.data_type
    WHEN 'TEXT' THEN 'TEXT'
    WHEN 'NUMBER' THEN 'NUMBER'
    WHEN 'INTEGER' THEN 'INTEGER'
    WHEN 'ENUM' THEN 'ENUM'
    WHEN 'DATE' THEN 'DATE'
    WHEN 'JSON' THEN 'JSON'
    ELSE 'TEXT'
  END,
  NULL,
  bf.description,
  'BASE',
  bf.status,
  NULL,
  'NONE',
  bf.type_config,
  NULL,
  NULL,
  bf.tenant_id,
  'seed'
FROM dynamic_entity_type_base_field bf
WHERE bf.deleted = false
  AND bf.tenant_id = 1
  AND bf.entity_type_code = 'region'
  AND bf.field_code LIKE 'FLD-BASE-region-%'
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  description = EXCLUDED.description,
  source = EXCLUDED.source,
  status = EXCLUDED.status,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

UPDATE dynamic_entity_type_base_field bf
SET
  library_field_id = f.id,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
FROM dynamic_field f
WHERE bf.deleted = false
  AND bf.tenant_id = f.tenant_id
  AND f.deleted = false
  AND bf.entity_type_code = 'region'
  AND f.code = bf.field_code
  AND (bf.library_field_id IS NULL OR bf.library_field_id <> f.id);

-- 3) 挂到全部 region 模型
INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, model_code, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  field_source, tenant_id, creator
)
SELECT
  m.id,
  f.id,
  m.code,
  f.code,
  COALESCE(bf.required, false),
  true,
  true,
  true,
  bf.sort_order,
  'BASE',
  1,
  'seed'
FROM dynamic_entity_type_base_field bf
JOIN dynamic_model m
  ON m.deleted = false
 AND m.tenant_id = 1
 AND m.entity_type_code = 'region'
JOIN dynamic_field f
  ON f.deleted = false
 AND f.tenant_id = 1
 AND f.id = bf.library_field_id
WHERE bf.deleted = false
  AND bf.tenant_id = 1
  AND bf.entity_type_code = 'region'
  AND bf.library_field_id IS NOT NULL
  AND bf.field_code IN (
    'FLD-BASE-region-cover_url',
    'FLD-BASE-region-hq_location',
    'FLD-BASE-region-mission_summary',
    'FLD-BASE-region-pipeline_km_total',
    'FLD-BASE-region-pipeline_km_ng',
    'FLD-BASE-region-pipeline_km_cr',
    'FLD-BASE-region-pipeline_km_cp',
    'FLD-BASE-region-storage_count',
    'FLD-BASE-region-lng_terminal_count',
    'FLD-BASE-region-coverage_note',
    'FLD-BASE-region-org_mode_note',
    'FLD-BASE-region-service_radius_km'
  )
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  field_id = EXCLUDED.field_id,
  model_id = EXCLUDED.model_id,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;
