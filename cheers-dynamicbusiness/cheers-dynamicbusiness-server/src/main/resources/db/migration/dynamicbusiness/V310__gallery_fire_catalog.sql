-- 数据目录「消防」。消防分区写在这一本目录里，不写进管廊分区的防火区。
-- 以后再把一条消防分区关联到防火区。这一版不建那条关联，也不猜神墩五路 7 个防火区分别属于哪一个消防分区。
-- 神墩五路说明只给出整段 K0+680～K1+422 共 4 个分区。清湾路只给出 K0+146.1～K0+687.1 共 3 个分区。
-- 中间分界没有。起点只写在该段第 1 条，终点只写在该段最后一条。
SET search_path TO dynamicbusiness;

CREATE TABLE IF NOT EXISTS dynamicbusiness.ent_gallery_fire_t123 (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    entity_type_code VARCHAR(64),
    model_id BIGINT NOT NULL,
    domain VARCHAR(128),
    name VARCHAR(255) NOT NULL,
    code VARCHAR(100),
    parent_id BIGINT DEFAULT 0,
    tree_path VARCHAR(500),
    sort INTEGER DEFAULT 0,
    status INTEGER DEFAULT 1,
    facility_id BIGINT,
    custom_fields JSONB DEFAULT '{}',
    creator VARCHAR(64),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updater VARCHAR(64),
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_ent_gallery_fire_t123_tenant ON dynamicbusiness.ent_gallery_fire_t123 (tenant_id);
CREATE INDEX IF NOT EXISTS idx_ent_gallery_fire_t123_model ON dynamicbusiness.ent_gallery_fire_t123 (model_id);
CREATE INDEX IF NOT EXISTS idx_ent_gallery_fire_t123_deleted ON dynamicbusiness.ent_gallery_fire_t123 (deleted);

INSERT INTO dynamic_entity_type (
    code, name, description, alias, sort, status, type_level,
    association_fields, storage_type, dedicated_table_name, enable_rule_engine,
    creator, updater, deleted, tenant_id, group_name, entry_kind, model_workbench_mode
) VALUES (
    'gallery_fire', '消防',
    '消防分区在这一本目录里维护。以后可以关联防火区。',
    '消防', 25, 'active', 'USER',
    '{}'::jsonb, 'DEDICATED', 'ent_gallery_fire_t123', false,
    'V310', 'V310', false, 123, '智慧管网', 'NATIVE', 'MULTI'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
    name = EXCLUDED.name,
    description = EXCLUDED.description,
    dedicated_table_name = EXCLUDED.dedicated_table_name,
    group_name = EXCLUDED.group_name,
    updater = 'V310',
    update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
    entity_type_code, field_code, field_name, data_type, required, description,
    sort_order, status, creator, updater, deleted, tenant_id, library_field_id,
    is_searchable, is_filterable, is_sortable)
SELECT 'gallery_fire', src.field_code, src.field_name, src.data_type, src.required, src.description,
       src.sort_order, src.status, 'V310', 'V310', false, 123, src.library_field_id,
       src.is_searchable, src.is_filterable, src.is_sortable
FROM dynamic_entity_type_base_field src
WHERE src.tenant_id = 123
  AND src.entity_type_code = 'zone'
  AND src.deleted = false
  AND src.field_code IN ('parentId', 'facility_id')
  AND NOT EXISTS (
      SELECT 1 FROM dynamic_entity_type_base_field own
      WHERE own.tenant_id = 123
        AND own.deleted = false
        AND own.entity_type_code = 'gallery_fire'
        AND own.field_code = src.field_code
  );

INSERT INTO dynamic_catalog_role_grant (
    role_id, registry_code, operation, creator, updater, deleted, tenant_id, subject)
SELECT role_id, 'gallery_fire', operation, 'V310', 'V310', false, tenant_id, subject
FROM dynamic_catalog_role_grant src
WHERE src.tenant_id = 123
  AND src.deleted = false
  AND src.registry_code = 'zone'
  AND NOT EXISTS (
      SELECT 1 FROM dynamic_catalog_role_grant own
      WHERE own.tenant_id = src.tenant_id
        AND own.deleted = false
        AND own.role_id = src.role_id
        AND own.registry_code = 'gallery_fire'
        AND own.subject IS NOT DISTINCT FROM src.subject
        AND own.operation = src.operation
  );

INSERT INTO dynamic_management_unit_catalog (
    tenant_id, management_unit_registry_code, management_unit_id, registry_code,
    creator, updater, deleted)
SELECT tenant_id, management_unit_registry_code, management_unit_id, 'gallery_fire',
       'V310', 'V310', false
FROM dynamic_management_unit_catalog src
WHERE src.tenant_id = 123
  AND src.deleted = false
  AND src.registry_code = 'zone'
  AND NOT EXISTS (
      SELECT 1 FROM dynamic_management_unit_catalog own
      WHERE own.tenant_id = src.tenant_id
        AND own.deleted = false
        AND own.management_unit_registry_code = src.management_unit_registry_code
        AND own.management_unit_id = src.management_unit_id
        AND own.registry_code = 'gallery_fire'
  );

DO $$
DECLARE
    v_category bigint;
    v_model bigint;
    v_model_code text;
    v_data bigint;
    v_model_layout bigint;
    v_row dynamicbusiness.dm_data_tab_layout%ROWTYPE;
    v_rel dynamicbusiness.dm_data_tab_column_relation%ROWTYPE;
    v_props bigint;
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM dynamicbusiness.dynamic_category
        WHERE tenant_id = 123 AND deleted = false AND category_type_code = 'gallery_fire' AND level = 1
    ) THEN
        INSERT INTO dynamicbusiness.dynamic_category (
            name, code, category_type_code, level, sort, status, description,
            creator, updater, deleted, tenant_id)
        VALUES (
            '消防', 'gallery_fire_root', 'gallery_fire', 1, 1, 1, '数据目录「消防」',
            'V310', 'V310', false, 123)
        RETURNING id INTO v_category;
        UPDATE dynamicbusiness.dynamic_category
        SET tree_path = '/' || v_category || '/'
        WHERE id = v_category;
    ELSE
        SELECT id INTO v_category
        FROM dynamicbusiness.dynamic_category
        WHERE tenant_id = 123 AND deleted = false AND category_type_code = 'gallery_fire' AND level = 1
        ORDER BY id
        LIMIT 1;
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM dynamicbusiness.dynamic_category_type
        WHERE tenant_id = 123 AND deleted = false AND category_type_code = 'gallery_fire'
    ) THEN
        INSERT INTO dynamicbusiness.dynamic_category_type (
            category_type_code, name, description, status, top_level_category_id,
            creator, updater, deleted, tenant_id, category_mode, entity_association_mode)
        VALUES (
            'gallery_fire', '消防', '数据目录「消防」', 1, v_category,
            'V310', 'V310', false, 123, 'SIMPLE', 'MULTI');
    END IF;

    SELECT id, code INTO v_model, v_model_code
    FROM dynamicbusiness.dynamic_model
    WHERE tenant_id = 123 AND deleted = false AND entity_type_code = 'gallery_fire' AND name = '消防分区'
    ORDER BY id
    LIMIT 1;

    IF v_model IS NULL THEN
        v_model_code := 'MODEL-' || replace(gen_random_uuid()::text, '-', '');
        INSERT INTO dynamicbusiness.dynamic_model (
            code, name, entity_type_code, description, status, sort, field_groups_config,
            creator, updater, deleted, tenant_id, governance_status)
        VALUES (
            v_model_code, '消防分区', 'gallery_fire',
            '设计说明里的一个消防分区。以后可以关联防火区。',
            1, 1,
            '{"groups":[{"id":"group-gallery-fire","name":"消防分区","color":"#409eff","sort":1,"parentId":null,"fields":[]}]}'::jsonb,
            'V310', 'V310', false, 123, 'COMPANY')
        RETURNING id INTO v_model;
    END IF;

    INSERT INTO dynamicbusiness.dynamic_model_field_assignment (
        model_id, field_id, required, is_searchable, is_filterable, is_sortable,
        sort, field_source, creator, updater, deleted, tenant_id,
        model_code, field_code, create_visible, edit_visible, detail_visible, collection_point)
    SELECT v_model, src.field_id, src.required, src.is_searchable, src.is_filterable, src.is_sortable,
           src.sort, src.field_source, 'V310', 'V310', false, 123,
           v_model_code, src.field_code, src.create_visible, src.edit_visible, src.detail_visible, false
    FROM dynamicbusiness.dynamic_model_field_assignment src
    WHERE src.tenant_id = 123
      AND src.deleted = false
      AND src.model_id = 2313
      AND src.field_code IN ('facility_id', 'FLD-LOC-006', 'FLD-LOC-007')
      AND NOT EXISTS (
          SELECT 1 FROM dynamicbusiness.dynamic_model_field_assignment own
          WHERE own.tenant_id = 123 AND own.deleted = false
            AND own.model_id = v_model AND own.field_code = src.field_code
      );

    INSERT INTO dynamicbusiness.dynamic_model_category_relation_t123 (
        model_id, category_id, entity_type_code, sort, creator, updater, deleted, tenant_id,
        model_code, category_code)
    SELECT v_model, v_category, 'gallery_fire', 1, 'V310', 'V310', false, 123,
           v_model_code, 'gallery_fire_root'
    WHERE NOT EXISTS (
        SELECT 1 FROM dynamicbusiness.dynamic_model_category_relation_t123 own
        WHERE own.tenant_id = 123 AND own.deleted = false
          AND own.model_code = v_model_code AND own.category_code = 'gallery_fire_root'
          AND own.entity_type_code = 'gallery_fire'
    );

    INSERT INTO dynamicbusiness.ent_gallery_fire_t123 (
        tenant_id, entity_type_code, model_id, name, code, parent_id, sort, status,
        facility_id, custom_fields, creator, updater, deleted)
    SELECT 123, 'gallery_fire', v_model, row.name,
           upper(replace(gen_random_uuid()::text, '-', '')),
           0, row.sort, 1, 1, row.fields, 'V310', 'V310', false
    FROM (VALUES
        ('神墩五路消防分区1#', 1, '{"FLD-LOC-006":"K0+680"}'::jsonb),
        ('神墩五路消防分区2#', 2, '{}'::jsonb),
        ('神墩五路消防分区3#', 3, '{}'::jsonb),
        ('神墩五路消防分区4#', 4, '{"FLD-LOC-007":"K1+422"}'::jsonb),
        ('清湾路消防分区1#', 1, '{"FLD-LOC-006":"K0+146.1"}'::jsonb),
        ('清湾路消防分区2#', 2, '{}'::jsonb),
        ('清湾路消防分区3#', 3, '{"FLD-LOC-007":"K0+687.1"}'::jsonb)
    ) AS row(name, sort, fields)
    WHERE NOT EXISTS (
        SELECT 1 FROM dynamicbusiness.ent_gallery_fire_t123 own
        WHERE own.tenant_id = 123 AND own.deleted = false AND own.name = row.name
    );

    INSERT INTO dynamicbusiness.dynamic_entity_category_relation_t123 (
        entity_id, category_id, entity_type_code, sort, creator, updater, deleted, tenant_id)
    SELECT e.id, v_category, 'gallery_fire', e.sort, 'V310', 'V310', false, 123
    FROM dynamicbusiness.ent_gallery_fire_t123 e
    WHERE e.tenant_id = 123 AND e.deleted = false AND e.creator = 'V310'
      AND NOT EXISTS (
          SELECT 1 FROM dynamicbusiness.dynamic_entity_category_relation_t123 own
          WHERE own.tenant_id = 123 AND own.entity_type_code = 'gallery_fire'
            AND own.entity_id = e.id AND own.category_id = v_category
      );

    IF EXISTS (
        SELECT 1 FROM dynamicbusiness.dynamic_entity_type
        WHERE tenant_id = 123 AND code = 'gallery_fire' AND deleted = false
          AND data_layout_id IS NULL
    ) THEN
        INSERT INTO dynamicbusiness.dm_workbench_layout (
            name, is_template, source_template_id, creator, updater, deleted, tenant_id, settings_json)
        SELECT '数据页签·gallery_fire', false, source_template_id, 'V310', 'V310', false, 123, settings_json
        FROM dynamicbusiness.dm_workbench_layout
        WHERE id = 196
        RETURNING id INTO v_data;

        INSERT INTO dynamicbusiness.dm_workbench_layout (
            name, is_template, source_template_id, creator, updater, deleted, tenant_id, settings_json)
        SELECT '模型管理·gallery_fire', false, source_template_id, 'V310', 'V310', false, 123, settings_json
        FROM dynamicbusiness.dm_workbench_layout
        WHERE id = 197
        RETURNING id INTO v_model_layout;

        FOR v_row IN
            SELECT * FROM dynamicbusiness.dm_data_tab_layout
            WHERE layout_id IN (196, 197) AND deleted = false
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
                       'V310', 'V310', false, 123
                FROM platformresource.pr_component_props
                WHERE id = v_row.props_id
                RETURNING id INTO v_props;
            END IF;
            INSERT INTO dynamicbusiness.dm_data_tab_layout (
                tenant_id, entity_type_code, column_kind, tab_id, props_id, enabled,
                column_meta, creator, updater, deleted, layout_id)
            VALUES (
                123, 'gallery_fire', v_row.column_kind, v_row.tab_id, v_props, v_row.enabled,
                v_row.column_meta, 'V310', 'V310', false,
                CASE WHEN v_row.layout_id = 196 THEN v_data ELSE v_model_layout END);
        END LOOP;

        FOR v_rel IN
            SELECT * FROM dynamicbusiness.dm_data_tab_column_relation
            WHERE layout_id IN (196, 197) AND deleted = false
        LOOP
            INSERT INTO dynamicbusiness.dm_data_tab_column_relation (
                entity_type_code, edge_id, from_column_identity, to_column_identity, relation_kind,
                from_type_code, to_type_code, relation_meta, creator, updater, deleted, tenant_id, layout_id)
            VALUES (
                'gallery_fire', v_rel.edge_id, v_rel.from_column_identity, v_rel.to_column_identity,
                v_rel.relation_kind,
                CASE WHEN v_rel.from_type_code = 'zone' THEN 'gallery_fire' ELSE v_rel.from_type_code END,
                CASE WHEN v_rel.to_type_code = 'zone' THEN 'gallery_fire' ELSE v_rel.to_type_code END,
                v_rel.relation_meta, 'V310', 'V310', false, 123,
                CASE WHEN v_rel.layout_id = 196 THEN v_data ELSE v_model_layout END);
        END LOOP;

        UPDATE dynamicbusiness.dynamic_entity_type
        SET data_layout_id = v_data,
            model_layout_id = v_model_layout,
            updater = 'V310',
            update_time = CURRENT_TIMESTAMP
        WHERE tenant_id = 123 AND code = 'gallery_fire' AND deleted = false;
    END IF;
END $$;
