-- ============================================================================
-- 智慧站场 · 05_models.sql
-- Generated: 2026-07-05 by scripts/export-platform-import.py
--
-- 约定：不写 surrogate id；幂等键为 code / field_code / page_code。
-- 依赖：先导入 system/ 全包（字段库已在 system/03_fields.sql）
-- 本包仅含站场 Region 模型与分类
-- ============================================================================

SET search_path TO dynamicbusiness;

-- dynamic_model: 2 row(s), upsert by code

INSERT INTO dynamic_model (
  code, name, business_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-REGION-SITE', '库区/站场', 'region',
  '油库站场业务边界（site）；与 fac_site 组织信息分离，同表异 type', 1,
  1, NULL,
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  field_groups_config = EXCLUDED.field_groups_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, business_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-REGION-TANK-GROUP', '罐组', 'region',
  '罐组分区（tank_group）；parent_id 指向库区 site', 1,
  2, NULL,
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  field_groups_config = EXCLUDED.field_groups_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;


-- dynamic_model_relation: (empty)


-- dynamic_model_relation_declaration: (empty)


-- dynamic_model_field_assignment: (empty)
