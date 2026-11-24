-- 智慧管廊：客户管理两页，并补齐收费目录。
-- 收费目录壳已在，专用表仍指向巡检租户的空表，没有数据页布局、分类、型号和基础字段。
-- 本租户自己的表按巡检租户已有的收费定义补齐。巡检租户没有收费记录，这里不编造记录。
-- 已保存的管廊菜单补上客户管理分组。入廊客户管理和收费管理各打开已有的那一本目录。
SET search_path TO dynamicbusiness;

CREATE SEQUENCE IF NOT EXISTS dynamicbusiness.ent_billing_t123_id_seq;

CREATE TABLE IF NOT EXISTS dynamicbusiness.ent_billing_t123
    (LIKE dynamicbusiness.ent_billing_t1 INCLUDING DEFAULTS);

ALTER TABLE dynamicbusiness.ent_billing_t123
    ALTER COLUMN id SET DEFAULT nextval('dynamicbusiness.ent_billing_t123_id_seq');

ALTER SEQUENCE dynamicbusiness.ent_billing_t123_id_seq
    OWNED BY dynamicbusiness.ent_billing_t123.id;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'ent_billing_t123_pkey'
    ) THEN
        ALTER TABLE dynamicbusiness.ent_billing_t123
            ADD CONSTRAINT ent_billing_t123_pkey PRIMARY KEY (id);
    END IF;
END $$;

CREATE INDEX IF NOT EXISTS ent_billing_t123_tree_path_idx
    ON dynamicbusiness.ent_billing_t123 (tree_path);

SELECT setval(
    'dynamicbusiness.ent_billing_t123_id_seq',
    COALESCE((SELECT MAX(id) FROM dynamicbusiness.ent_billing_t123), 1),
    (SELECT MAX(id) FROM dynamicbusiness.ent_billing_t123) IS NOT NULL
);

UPDATE dynamic_entity_type
SET dedicated_table_name = 'ent_billing_t123',
    updater = 'V305',
    update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 123
  AND code = 'billing'
  AND deleted = false
  AND dedicated_table_name IS DISTINCT FROM 'ent_billing_t123';

INSERT INTO dynamic_entity_type_base_field (
    entity_type_code, field_code, field_name, data_type, required, default_value,
    description, type_config, sort_order, status, creator, updater, deleted, tenant_id,
    library_field_id, is_searchable, is_filterable, is_sortable)
SELECT entity_type_code, field_code, field_name, data_type, required, default_value,
       description, type_config, sort_order, status, 'V305', 'V305', false, 123,
       library_field_id, is_searchable, is_filterable, is_sortable
FROM dynamic_entity_type_base_field src
WHERE src.tenant_id = 1
  AND src.entity_type_code = 'billing'
  AND src.deleted = false
  AND NOT EXISTS (
      SELECT 1 FROM dynamic_entity_type_base_field own
      WHERE own.tenant_id = 123
        AND own.deleted = false
        AND own.entity_type_code = 'billing'
        AND own.field_code = src.field_code
  );

INSERT INTO dynamic_catalog_ownership (
    registry_code, field_code, target_registry_code, required, multi_valued,
    creator, updater, deleted, tenant_id)
SELECT registry_code, field_code, target_registry_code, required, multi_valued,
       'V305', 'V305', false, 123
FROM dynamic_catalog_ownership src
WHERE src.tenant_id = 1
  AND src.deleted = false
  AND src.registry_code = 'billing'
  AND NOT EXISTS (
      SELECT 1 FROM dynamic_catalog_ownership own
      WHERE own.tenant_id = 123
        AND own.deleted = false
        AND own.registry_code = src.registry_code
        AND own.field_code = src.field_code
  );

CREATE OR REPLACE FUNCTION dynamicbusiness.v305_clone_billing_layout(
    p_source bigint,
    p_name text,
    p_tenant bigint
) RETURNS bigint
LANGUAGE plpgsql
AS $$
DECLARE
    v_layout bigint;
    v_row dynamicbusiness.dm_data_tab_layout%ROWTYPE;
    v_props bigint;
    v_tab_id bigint;
BEGIN
    INSERT INTO dynamicbusiness.dm_workbench_layout (
        name, is_template, source_template_id, creator, updater, deleted, tenant_id, settings_json)
    SELECT p_name, false, source_template_id, 'V305', 'V305', false, p_tenant, settings_json
    FROM dynamicbusiness.dm_workbench_layout
    WHERE id = p_source
    RETURNING id INTO v_layout;

    FOR v_row IN
        SELECT * FROM dynamicbusiness.dm_data_tab_layout
        WHERE layout_id = p_source AND deleted = false
        ORDER BY id
    LOOP
        v_props := NULL;
        IF v_row.props_id IS NOT NULL THEN
            INSERT INTO platformresource.pr_component_props (
                is_template, component_id, component_code, data_source, template_id,
                schema_version, props, props_override, name, status, sort, description,
                creator, updater, deleted, tenant_id)
            SELECT is_template, component_id, component_code, data_source, template_id,
                   schema_version, props, props_override, name, status, sort, description,
                   'V305', 'V305', false, p_tenant
            FROM platformresource.pr_component_props
            WHERE id = v_row.props_id
            RETURNING id INTO v_props;
        END IF;

        v_tab_id := nextval('dynamicbusiness.dm_entity_dimension_id_seq');
        INSERT INTO dynamicbusiness.dm_data_tab_layout (
            id, tenant_id, entity_type_code, column_kind, tab_id, props_id, enabled,
            column_meta, creator, updater, deleted, layout_id)
        VALUES (
            v_tab_id, p_tenant, v_row.entity_type_code, v_row.column_kind, v_row.tab_id,
            v_props, v_row.enabled, v_row.column_meta, 'V305', 'V305', false, v_layout);
    END LOOP;

    INSERT INTO dynamicbusiness.dm_data_tab_column_relation (
        entity_type_code, edge_id, from_column_identity, to_column_identity, relation_kind,
        from_type_code, to_type_code, relation_meta, creator, updater, deleted, tenant_id, layout_id)
    SELECT entity_type_code, edge_id, from_column_identity, to_column_identity, relation_kind,
           from_type_code, to_type_code, relation_meta, 'V305', 'V305', false, p_tenant, v_layout
    FROM dynamicbusiness.dm_data_tab_column_relation
    WHERE layout_id = p_source AND deleted = false;

    RETURN v_layout;
END;
$$;

DO $$
DECLARE
    v_category bigint;
    v_data bigint;
    v_model_layout bigint;
    v_src record;
    v_new_id bigint;
    v_new_code text;
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM dynamicbusiness.dynamic_category
        WHERE tenant_id = 123 AND deleted = false AND category_type_code = 'billing'
    ) THEN
        INSERT INTO dynamicbusiness.dynamic_category (
            name, code, category_type_code, level, sort, status, description,
            creator, updater, deleted, tenant_id)
        VALUES (
            '收费管理', 'billing_root', 'billing', 1, 1, 1, '数据类型「收费管理」默认分类',
            'V305', 'V305', false, 123)
        RETURNING id INTO v_category;
        UPDATE dynamicbusiness.dynamic_category
        SET tree_path = '/' || v_category || '/'
        WHERE id = v_category;
    ELSE
        SELECT id INTO v_category
        FROM dynamicbusiness.dynamic_category
        WHERE tenant_id = 123 AND deleted = false AND category_type_code = 'billing'
        ORDER BY id
        LIMIT 1;
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM dynamicbusiness.dynamic_category_type
        WHERE tenant_id = 123 AND deleted = false AND category_type_code = 'billing'
    ) THEN
        INSERT INTO dynamicbusiness.dynamic_category_type (
            category_type_code, name, description, status, top_level_category_id,
            creator, updater, deleted, tenant_id, category_mode, entity_association_mode)
        VALUES (
            'billing', '收费管理', '数据类型「收费管理」默认分类', 1, v_category,
            'V305', 'V305', false, 123, 'SIMPLE', 'MULTI');
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM dynamicbusiness.dynamic_model
        WHERE tenant_id = 123 AND deleted = false AND entity_type_code = 'billing'
    ) THEN
        CREATE TEMP TABLE v305_model_map (
            old_id bigint PRIMARY KEY,
            new_id bigint NOT NULL,
            new_code text NOT NULL
        ) ON COMMIT DROP;
        FOR v_src IN
            SELECT * FROM dynamicbusiness.dynamic_model
            WHERE tenant_id = 1 AND deleted = false AND entity_type_code = 'billing'
            ORDER BY id
        LOOP
            v_new_code := 'MODEL-' || replace(gen_random_uuid()::text, '-', '');
            INSERT INTO dynamicbusiness.dynamic_model (
                code, name, entity_type_code, description, status, sort, field_groups_config,
                creator, updater, deleted, tenant_id, domain, governance_status,
                auto_param_defaults)
            VALUES (
                v_new_code, v_src.name, 'billing', v_src.description, v_src.status, v_src.sort,
                v_src.field_groups_config, 'V305', 'V305', false, 123, v_src.domain,
                v_src.governance_status, v_src.auto_param_defaults)
            RETURNING id INTO v_new_id;
            INSERT INTO v305_model_map (old_id, new_id, new_code)
            VALUES (v_src.id, v_new_id, v_new_code);
        END LOOP;

        INSERT INTO dynamicbusiness.dynamic_model_field_assignment (
            model_id, field_id, required, is_searchable, is_filterable, is_sortable,
            default_value, validation_rules, sort, field_group_id, field_source, ref_library_id,
            model_relation_id, target_entity_type, creator, updater, deleted, tenant_id,
            model_code, field_code, alias, create_visible, edit_visible, detail_visible,
            collection_point)
        SELECT map.new_id, src.field_id, src.required, src.is_searchable, src.is_filterable,
               src.is_sortable, src.default_value, src.validation_rules, src.sort,
               src.field_group_id, src.field_source, src.ref_library_id, src.model_relation_id,
               src.target_entity_type, 'V305', 'V305', false, 123, map.new_code, src.field_code,
               src.alias, src.create_visible, src.edit_visible, src.detail_visible,
               src.collection_point
        FROM dynamicbusiness.dynamic_model_field_assignment src
        JOIN v305_model_map map ON map.old_id = src.model_id
        WHERE src.tenant_id = 1 AND src.deleted = false;
    END IF;

    IF EXISTS (
        SELECT 1 FROM dynamicbusiness.dynamic_entity_type
        WHERE tenant_id = 123 AND code = 'billing' AND deleted = false
          AND data_layout_id IS NULL
    ) THEN
        v_data := dynamicbusiness.v305_clone_billing_layout(2, '数据页签·billing', 123);
        v_model_layout := dynamicbusiness.v305_clone_billing_layout(2, '模型管理·billing', 123);
        UPDATE dynamicbusiness.dynamic_entity_type
        SET data_layout_id = v_data,
            model_layout_id = v_model_layout,
            updater = 'V305',
            update_time = CURRENT_TIMESTAMP
        WHERE tenant_id = 123 AND code = 'billing' AND deleted = false;
    END IF;
END $$;

DROP FUNCTION dynamicbusiness.v305_clone_billing_layout(bigint, text, bigint);

INSERT INTO tenant_portal_menu (tenant_id, parent_id, title, page_code, sort, creator, updater)
SELECT 123, gallery.id, '客户管理', NULL,
       COALESCE((
           SELECT MAX(child.sort) FROM tenant_portal_menu child
           WHERE child.tenant_id = 123 AND child.parent_id = gallery.id AND child.deleted = false
       ), -1) + 1,
       'V305', 'V305'
FROM tenant_portal_menu gallery
WHERE gallery.tenant_id = 123
  AND gallery.deleted = false
  AND gallery.parent_id IS NULL
  AND gallery.title = '管廊管理'
  AND NOT EXISTS (
      SELECT 1 FROM tenant_portal_menu existing
      WHERE existing.tenant_id = 123
        AND existing.deleted = false
        AND existing.parent_id = gallery.id
        AND existing.title = '客户管理'
        AND existing.page_code IS NULL
  );

INSERT INTO tenant_portal_menu (tenant_id, parent_id, title, page_code, sort, creator, updater)
SELECT 123, customer_group.id, page.title, page.page_code, page.sort, 'V305', 'V305'
FROM tenant_portal_menu customer_group
JOIN (VALUES
    ('入廊客户管理', 'gallery-customer', 0),
    ('收费管理', 'gallery-billing', 1)
) AS page(title, page_code, sort) ON true
WHERE customer_group.tenant_id = 123
  AND customer_group.deleted = false
  AND customer_group.title = '客户管理'
  AND customer_group.page_code IS NULL
  AND customer_group.parent_id IN (
      SELECT id FROM tenant_portal_menu
      WHERE tenant_id = 123 AND deleted = false AND parent_id IS NULL AND title = '管廊管理'
  )
  AND NOT EXISTS (
      SELECT 1 FROM tenant_portal_menu existing
      WHERE existing.tenant_id = 123
        AND existing.deleted = false
        AND existing.page_code = page.page_code
  );

UPDATE tenant_capability_pack
SET opened_page_codes = opened_page_codes || ',gallery-customer,gallery-billing',
    updater = 'V305',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND pack_code = 'pipe-gallery'
  AND opened_page_codes IS NOT NULL
  AND opened_page_codes <> ''
  AND position('gallery-customer' IN opened_page_codes) = 0;

INSERT INTO dynamic_page_role_grant (role_id, page_code, action, creator, updater, tenant_id)
SELECT DISTINCT source.role_id, page.page_code, 'view', 'V305', 'V305', source.tenant_id
FROM dynamic_page_role_grant source
JOIN (VALUES ('gallery-customer'), ('gallery-billing')) AS page(page_code) ON true
WHERE source.deleted = false
  AND source.page_code = 'gallery-structure'
  AND source.action = 'view'
  AND NOT EXISTS (
      SELECT 1 FROM dynamic_page_role_grant existing
      WHERE existing.role_id = source.role_id
        AND existing.page_code = page.page_code
        AND existing.action = 'view'
        AND existing.tenant_id = source.tenant_id
        AND existing.deleted = false
  );

INSERT INTO dynamic_catalog_role_grant (
    role_id, registry_code, subject, operation, creator, updater, tenant_id)
SELECT DISTINCT source.role_id, catalog.registry_code, subject.subject, 'query',
       'V305', 'V305', source.tenant_id
FROM dynamic_page_role_grant source
JOIN (VALUES ('customer'), ('billing')) AS catalog(registry_code) ON true
JOIN (VALUES ('entity'), ('category'), ('detail'), ('model')) AS subject(subject) ON true
WHERE source.deleted = false
  AND source.page_code IN ('gallery-customer', 'gallery-billing')
  AND source.action = 'view'
  AND NOT EXISTS (
      SELECT 1 FROM dynamic_catalog_role_grant existing
      WHERE existing.role_id = source.role_id
        AND existing.registry_code = catalog.registry_code
        AND existing.subject = subject.subject
        AND existing.operation = 'query'
        AND existing.tenant_id = source.tenant_id
        AND existing.deleted = false
  );
