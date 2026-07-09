-- ============================================================================
-- 智慧站场产品包 · Generated: 2026-07-08 by scripts/generate-smart-station-import.py
--
-- 约定：幂等键 model.code / field.code；关联 INSERT 解析 id，不写 surrogate id。
-- 依赖：先导入 platform-import/system/（实体类型、基础字段、设备模型等）
-- ============================================================================

SET search_path TO dynamicbusiness;

-- dynamic_group(FIELD): 1 row(s), upsert by (group_type, code)

INSERT INTO dynamic_group (
  group_type, code, name, description, parent_id, parent_code, path, level, sort, status, tenant_id, creator
)
SELECT
  'FIELD', 'FG-BIZ-SPATIAL', '空间与设施',
  '站场/设施/分区模型扩展字段', NULL, NULL,
  NULL, 1,
  15, 1,
  1, 'zhgl-seed'
WHERE NOT EXISTS (
  SELECT 1 FROM dynamic_group g
  WHERE g.deleted = false AND g.tenant_id = 1
    AND g.group_type = 'FIELD' AND g.code = 'FG-BIZ-SPATIAL'
);

UPDATE dynamic_group g SET
  name = '空间与设施',
  description = '站场/设施/分区模型扩展字段',
  parent_id = NULL,
  parent_code = NULL,
  sort = 15,
  status = 1,
  updater = 'zhgl-seed',
  update_time = CURRENT_TIMESTAMP
WHERE g.deleted = false AND g.tenant_id = 1
  AND g.group_type = 'FIELD' AND g.code = 'FG-BIZ-SPATIAL';


-- dynamic_field: 6 row(s), upsert by code

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'FLD-FAC-EXT-001', '设计罐容', 'DECIMAL',
  'm³', '站场储罐总设计容量',
  'USER', 1,
  NULL, NULL,
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'FLD-FAC-EXT-002', '投运日期', 'DATE',
  NULL, '设施正式投运日期',
  'USER', 1,
  NULL, NULL,
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'FLD-FAC-EXT-003', '危化等级', 'ENUM',
  NULL, '厂区危险化学品等级',
  'USER', 1,
  NULL, NULL,
  '[{"label": "一般", "value": "一般"}, {"label": "较大", "value": "较大"}, {"label": "重大", "value": "重大"}, {"label": "特别重大", "value": "特别重大"}]', NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'FLD-FAC-EXT-004', '库容', 'DECIMAL',
  't', '油库设计库容',
  'USER', 1,
  NULL, NULL,
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'FLD-FAC-EXT-005', '建筑面积', 'DECIMAL',
  'm²', '机关/楼宇建筑面积',
  'USER', 1,
  NULL, NULL,
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'FLD-ZONE-EXT-001', '占地面积', 'DECIMAL',
  'm²', '分区占地面积（模型扩展）',
  'USER', 1,
  NULL, NULL,
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;


-- dynamic_group_relation(FIELD): 6 row(s), resolve by group_code + field_code

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.code, f.code, g.id, f.id, 0, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-SPATIAL'
  AND f.code = 'FLD-FAC-EXT-001'
ON CONFLICT (group_type, group_code, target_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  group_id = EXCLUDED.group_id,
  target_id = EXCLUDED.target_id,
  sort = EXCLUDED.sort,
  updater = 'zhgl-seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.code, f.code, g.id, f.id, 0, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-SPATIAL'
  AND f.code = 'FLD-FAC-EXT-002'
ON CONFLICT (group_type, group_code, target_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  group_id = EXCLUDED.group_id,
  target_id = EXCLUDED.target_id,
  sort = EXCLUDED.sort,
  updater = 'zhgl-seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.code, f.code, g.id, f.id, 0, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-SPATIAL'
  AND f.code = 'FLD-FAC-EXT-003'
ON CONFLICT (group_type, group_code, target_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  group_id = EXCLUDED.group_id,
  target_id = EXCLUDED.target_id,
  sort = EXCLUDED.sort,
  updater = 'zhgl-seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.code, f.code, g.id, f.id, 0, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-SPATIAL'
  AND f.code = 'FLD-FAC-EXT-004'
ON CONFLICT (group_type, group_code, target_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  group_id = EXCLUDED.group_id,
  target_id = EXCLUDED.target_id,
  sort = EXCLUDED.sort,
  updater = 'zhgl-seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.code, f.code, g.id, f.id, 0, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-SPATIAL'
  AND f.code = 'FLD-FAC-EXT-005'
ON CONFLICT (group_type, group_code, target_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  group_id = EXCLUDED.group_id,
  target_id = EXCLUDED.target_id,
  sort = EXCLUDED.sort,
  updater = 'zhgl-seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.code, f.code, g.id, f.id, 0, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-BIZ-SPATIAL'
  AND f.code = 'FLD-ZONE-EXT-001'
ON CONFLICT (group_type, group_code, target_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  group_id = EXCLUDED.group_id,
  target_id = EXCLUDED.target_id,
  sort = EXCLUDED.sort,
  updater = 'zhgl-seed',
  update_time = CURRENT_TIMESTAMP;
