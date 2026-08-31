-- ============================================================================
-- action · 04 规范型号 action（SINGLE）+ 按执行手段分类
-- ============================================================================

SET search_path TO dynamicbusiness;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES (
  'action', '动作库', 'action',
  '动作规范型号（SINGLE）；具体动作建为实体实例，勿再建第二型号', 1, 0, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, model_code, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  default_value, target_entity_type, field_source, tenant_id, creator
)
SELECT
  m.id, f.id, m.code, bf.field_code,
  bf.required, true, true, false, bf.sort_order,
  bf.default_value, NULL, 'BASE', 1, 'seed'
FROM dynamic_model m
JOIN dynamic_entity_type_base_field bf
  ON bf.deleted = false AND bf.tenant_id = 1 AND bf.entity_type_code = 'action'
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = 1 AND f.code = bf.field_code
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'action'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  sort = EXCLUDED.sort,
  default_value = EXCLUDED.default_value,
  field_source = EXCLUDED.field_source,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category_type (
  category_type_code, name, description, status, tenant_id, creator,
  category_mode, entity_association_mode
)
SELECT
  'action', '动作库', '动作库分类（按执行手段）', 1, 1, 'seed',
  'SIMPLE', 'MULTI'
WHERE NOT EXISTS (
  SELECT 1 FROM dynamic_category_type ct
  WHERE ct.deleted = false AND ct.tenant_id = 1 AND ct.category_type_code = 'action'
);

INSERT INTO dynamic_category (
  code, name, category_type_code, parent_id, status, sort, tenant_id, creator, deleted
)
SELECT
  'action_root', '动作库', 'action', NULL, 1, 0, 1, 'seed', false
WHERE NOT EXISTS (
  SELECT 1 FROM dynamic_category c
  WHERE c.deleted = false AND c.tenant_id = 1 AND c.code = 'action_root'
);

-- 手段子分类
INSERT INTO dynamic_category (
  code, name, category_type_code, parent_id, status, sort, tenant_id, creator, deleted
)
SELECT v.code, v.name, 'action', p.id, 1, v.sort, 1, 'seed', false
FROM (
  VALUES
    ('cat-action-uav', '无人机', 10),
    ('cat-action-robot', '机器人', 20),
    ('cat-action-manual', '人工', 30),
    ('cat-action-fixed-camera', '固定机位', 40)
) AS v(code, name, sort)
JOIN dynamic_category p
  ON p.deleted = false AND p.tenant_id = 1 AND p.code = 'action_root'
WHERE NOT EXISTS (
  SELECT 1 FROM dynamic_category c
  WHERE c.deleted = false AND c.tenant_id = 1 AND c.code = v.code
);
