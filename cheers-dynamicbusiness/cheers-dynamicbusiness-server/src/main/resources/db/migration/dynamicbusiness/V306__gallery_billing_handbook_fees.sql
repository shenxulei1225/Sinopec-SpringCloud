-- 智慧管廊收费按运营维护手册改成两类。
-- 入廊费弥补建设成本，一次性或分期支付。
-- 日常维护费按当年确认的管道长度和占用空间，每年收一次。
-- 只改租户 123。巡检租户上的软件项目费型号不动。这里不编收费记录。
SET search_path TO dynamicbusiness;

ALTER TABLE dynamicbusiness.ent_billing_t123
    ADD COLUMN IF NOT EXISTS customer_id bigint;
ALTER TABLE dynamicbusiness.ent_billing_t123
    ADD COLUMN IF NOT EXISTS amount numeric;
ALTER TABLE dynamicbusiness.ent_billing_t123
    ADD COLUMN IF NOT EXISTS billing_year integer;

INSERT INTO dynamic_field (
    code, name, type, unit, source, status, options, provider_code, semantic_type,
    tenant_id, creator, updater, deleted)
SELECT v.code, v.name, v.type, v.unit, 'SYSTEM', 1, v.options, v.provider_code, v.semantic_type,
       123, 'V306', 'V306', false
FROM (VALUES
    ('customer_id', '入廊客户', 'ENTITY_REF', NULL::varchar, NULL::text, 'dynamic-entity:customer', 'customer'),
    ('amount', '金额', 'NUMBER', '元', NULL, NULL, 'amount'),
    ('billing_year', '计费年度', 'INTEGER', '年', NULL, NULL, 'billing_year'),
    ('payment_method', '支付方式', 'ENUM', NULL, '[{"label": "一次性", "value": "一次性"}, {"label": "分期", "value": "分期"}]', NULL, 'payment_method'),
    ('pipe_length', '管道长度', 'NUMBER', '米', NULL, NULL, 'pipe_length'),
    ('space_ratio', '占用空间占比', 'NUMBER', '%', NULL, NULL, 'space_ratio'),
    ('unit_price', '单价', 'NUMBER', '元/米', NULL, NULL, 'unit_price')
) AS v(code, name, type, unit, options, provider_code, semantic_type)
WHERE NOT EXISTS (
    SELECT 1 FROM dynamic_field own
    WHERE own.tenant_id = 123 AND own.deleted = false AND own.code = v.code
);

INSERT INTO dynamic_entity_type_base_field (
    entity_type_code, library_field_id, field_code, field_name, data_type, required,
    sort_order, status, tenant_id, creator, updater, deleted, is_searchable, is_filterable, is_sortable)
SELECT 'billing', f.id, f.code, f.name,
       CASE f.code WHEN 'customer_id' THEN 'REF' WHEN 'billing_year' THEN 'INTEGER' ELSE 'NUMBER' END,
       true,
       CASE f.code WHEN 'customer_id' THEN 6 WHEN 'amount' THEN 7 ELSE 8 END,
       1, 123, 'V306', 'V306', false,
       false,
       true,
       f.code <> 'customer_id'
FROM dynamic_field f
WHERE f.tenant_id = 123 AND f.deleted = false
  AND f.code IN ('customer_id', 'amount', 'billing_year')
  AND NOT EXISTS (
      SELECT 1 FROM dynamic_entity_type_base_field own
      WHERE own.tenant_id = 123 AND own.deleted = false
        AND own.entity_type_code = 'billing' AND own.field_code = f.code
  );

INSERT INTO dynamic_category (
    parent_id, name, code, category_type_code, level, sort, status, description,
    tree_path, parent_code, creator, updater, deleted, tenant_id)
SELECT parent.id, v.name, v.code, 'billing', 2, v.sort, 1, v.description,
       parent.tree_path, parent.code, 'V306', 'V306', false, 123
FROM dynamic_category parent
JOIN (VALUES
    ('入廊费', 'billing-entry-fee', 1, '弥补管廊建设成本，一次性或分期支付。'),
    ('日常维护费', 'billing-maintenance-fee', 2, '按当年确认的管道长度和占用空间，每年收一次。')
) AS v(name, code, sort, description) ON true
WHERE parent.tenant_id = 123 AND parent.deleted = false AND parent.code = 'billing_root'
  AND NOT EXISTS (
      SELECT 1 FROM dynamic_category own
      WHERE own.tenant_id = 123 AND own.deleted = false AND own.code = v.code
  );

UPDATE dynamic_category child
SET tree_path = parent.tree_path || child.id || '/',
    parent_id = parent.id,
    parent_code = parent.code,
    updater = 'V306',
    update_time = CURRENT_TIMESTAMP
FROM dynamic_category parent
WHERE child.tenant_id = 123 AND child.deleted = false
  AND child.code IN ('billing-entry-fee', 'billing-maintenance-fee')
  AND parent.tenant_id = 123 AND parent.deleted = false AND parent.code = 'billing_root'
  AND child.tree_path IS DISTINCT FROM parent.tree_path || child.id || '/';

INSERT INTO dynamic_model (
    code, name, entity_type_code, description, status, sort,
    creator, updater, deleted, tenant_id, governance_status)
SELECT v.code, v.name, 'billing', v.description, 1, v.sort, 'V306', 'V306', false, 123, 'COMPANY'
FROM (VALUES
    ('MODEL-billing-entry-fee', '入廊费', '弥补管廊建设成本，一次性或分期支付。', 0),
    ('MODEL-billing-maintenance-fee', '日常维护费', '按当年确认的管道长度和占用空间，每年收一次。', 1)
) AS v(code, name, description, sort)
WHERE NOT EXISTS (
    SELECT 1 FROM dynamic_model own
    WHERE own.tenant_id = 123 AND own.deleted = false AND own.code = v.code
);

INSERT INTO dynamic_model_category_relation (
    model_id, category_id, entity_type_code, sort, creator, updater, deleted, tenant_id,
    model_code, category_code)
SELECT model.id, category.id, 'billing', 0, 'V306', 'V306', false, 123, model.code, category.code
FROM dynamic_model model
JOIN dynamic_category category
  ON category.tenant_id = 123 AND category.deleted = false
 AND (
      (model.code = 'MODEL-billing-entry-fee' AND category.code = 'billing-entry-fee')
      OR (model.code = 'MODEL-billing-maintenance-fee' AND category.code = 'billing-maintenance-fee')
 )
WHERE model.tenant_id = 123 AND model.deleted = false
  AND model.code IN ('MODEL-billing-entry-fee', 'MODEL-billing-maintenance-fee')
  AND NOT EXISTS (
      SELECT 1 FROM dynamic_model_category_relation own
      WHERE own.tenant_id = 123 AND own.deleted = false
        AND own.model_code = model.code AND own.category_code = category.code
        AND own.entity_type_code = 'billing'
  );

INSERT INTO dynamic_model_field_assignment (
    model_id, field_id, required, is_searchable, is_filterable, is_sortable, sort,
    field_source, target_entity_type, creator, updater, deleted, tenant_id,
    model_code, field_code, create_visible, edit_visible, detail_visible)
SELECT model.id, src.field_id, src.required, src.is_searchable, src.is_filterable, src.is_sortable,
       src.sort, src.field_source, src.target_entity_type, 'V306', 'V306', false, 123,
       model.code, src.field_code, true, true, true
FROM dynamic_model model
JOIN dynamic_model_field_assignment src
  ON src.tenant_id = 123 AND src.deleted = false
 AND src.model_id = (
      SELECT id FROM dynamic_model old
      WHERE old.tenant_id = 123 AND old.entity_type_code = 'billing' AND old.name = '部署安装费'
      ORDER BY old.deleted, old.id
      LIMIT 1
 )
WHERE model.tenant_id = 123 AND model.deleted = false
  AND model.code IN ('MODEL-billing-entry-fee', 'MODEL-billing-maintenance-fee')
  AND NOT EXISTS (
      SELECT 1 FROM dynamic_model_field_assignment own
      WHERE own.tenant_id = 123 AND own.deleted = false
        AND own.model_code = model.code AND own.field_code = src.field_code
  );

INSERT INTO dynamic_model_field_assignment (
    model_id, field_id, required, is_searchable, is_filterable, is_sortable, sort,
    field_source, target_entity_type, creator, updater, deleted, tenant_id,
    model_code, field_code, create_visible, edit_visible, detail_visible)
SELECT model.id, field.id, true,
       false,
       true,
       field.code <> 'customer_id' AND field.code <> 'payment_method',
       CASE field.code
           WHEN 'customer_id' THEN 6
           WHEN 'amount' THEN 7
           WHEN 'billing_year' THEN 8
           WHEN 'payment_method' THEN 9
           WHEN 'pipe_length' THEN 9
           WHEN 'space_ratio' THEN 10
           ELSE 11
       END,
       CASE WHEN field.code IN ('customer_id', 'amount', 'billing_year') THEN 'BASE' ELSE 'CUSTOM' END,
       CASE WHEN field.code = 'customer_id' THEN 'customer' ELSE NULL END,
       'V306', 'V306', false, 123, model.code, field.code, true, true, true
FROM dynamic_model model
JOIN dynamic_field field
  ON field.tenant_id = 123 AND field.deleted = false
 AND (
      field.code IN ('customer_id', 'amount', 'billing_year')
      OR (model.code = 'MODEL-billing-entry-fee' AND field.code = 'payment_method')
      OR (model.code = 'MODEL-billing-maintenance-fee' AND field.code IN ('pipe_length', 'space_ratio', 'unit_price'))
 )
WHERE model.tenant_id = 123 AND model.deleted = false
  AND model.code IN ('MODEL-billing-entry-fee', 'MODEL-billing-maintenance-fee')
  AND NOT EXISTS (
      SELECT 1 FROM dynamic_model_field_assignment own
      WHERE own.tenant_id = 123 AND own.deleted = false
        AND own.model_code = model.code AND own.field_code = field.code
  );

UPDATE dynamic_model
SET deleted = true, updater = 'V306', update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 123 AND deleted = false AND entity_type_code = 'billing'
  AND name IN ('部署安装费', '业务咨询费', '驻场维护费');

UPDATE dynamic_model_field_assignment assignment
SET deleted = true, updater = 'V306', update_time = CURRENT_TIMESTAMP
FROM dynamic_model model
WHERE assignment.tenant_id = 123 AND assignment.deleted = false
  AND model.id = assignment.model_id
  AND model.tenant_id = 123 AND model.entity_type_code = 'billing'
  AND model.name IN ('部署安装费', '业务咨询费', '驻场维护费');

UPDATE dm_data_tab_layout
SET enabled = true, updater = 'V306', update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 123 AND deleted = false AND layout_id = 697 AND column_kind = 'MODEL' AND enabled = false;
