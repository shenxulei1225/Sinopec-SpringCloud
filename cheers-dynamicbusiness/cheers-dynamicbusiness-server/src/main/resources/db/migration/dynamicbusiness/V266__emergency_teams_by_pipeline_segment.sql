-- 应急队伍改按管线段管理，和外部救援一样点一段只看这一段。
-- 新目录「应急队伍」。一条队伍归一个管线段。名称是段名加队伍名。
-- 队员留空，不从场站部门带入。站内应急资源里按场站复制的队伍作废。

CREATE OR REPLACE FUNCTION dynamicbusiness.v266_clone_layout(
    p_source_layout bigint,
    p_name text,
    p_tenant bigint
) RETURNS bigint
LANGUAGE plpgsql
AS $$
DECLARE
    v_layout bigint;
    v_row record;
    v_props bigint;
    v_tab_id bigint;
BEGIN
    CREATE TEMP TABLE v266_layout ON COMMIT DROP AS
        SELECT * FROM dynamicbusiness.dm_workbench_layout WHERE id = p_source_layout;
    UPDATE v266_layout SET
        id = nextval('dynamicbusiness.dm_workbench_layout_id_seq'),
        name = p_name,
        is_template = false,
        tenant_id = p_tenant,
        creator = 'V266',
        create_time = CURRENT_TIMESTAMP,
        updater = 'V266',
        update_time = CURRENT_TIMESTAMP,
        deleted = false;
    INSERT INTO dynamicbusiness.dm_workbench_layout SELECT * FROM v266_layout;
    SELECT id INTO v_layout FROM v266_layout;

    CREATE TEMP TABLE v266_tabs ON COMMIT DROP AS
        SELECT * FROM dynamicbusiness.dm_data_tab_layout
        WHERE layout_id = p_source_layout AND deleted = false;
    CREATE TEMP TABLE v266_tab_ids ON COMMIT DROP AS
        SELECT id AS old_id FROM v266_tabs;

    PERFORM pg_advisory_xact_lock(26601);
    SELECT COALESCE(MAX(id), 0) INTO v_tab_id FROM dynamicbusiness.dm_data_tab_layout;

    FOR v_row IN SELECT old_id FROM v266_tab_ids LOOP
        v_props := NULL;
        IF (SELECT props_id FROM v266_tabs WHERE id = v_row.old_id) IS NOT NULL THEN
            CREATE TEMP TABLE v266_props ON COMMIT DROP AS
                SELECT * FROM platformresource.pr_component_props
                WHERE id = (SELECT props_id FROM v266_tabs WHERE id = v_row.old_id);
            UPDATE v266_props SET
                id = nextval('platformresource.pr_component_props_id_seq'),
                name = replace(replace(coalesce(name, ''), '外部救援资源', '应急队伍'), 'external_rescue_resource', 'emergency_team'),
                props = replace(replace(coalesce(props, ''), '外部救援资源', '应急队伍'), 'external_rescue_resource', 'emergency_team'),
                data_source = replace(replace(coalesce(data_source, ''), '外部救援资源', '应急队伍'), 'external_rescue_resource', 'emergency_team'),
                tenant_id = p_tenant,
                creator = 'V266',
                create_time = CURRENT_TIMESTAMP,
                updater = 'V266',
                update_time = CURRENT_TIMESTAMP,
                deleted = false;
            INSERT INTO platformresource.pr_component_props SELECT * FROM v266_props;
            SELECT id INTO v_props FROM v266_props;
            DROP TABLE v266_props;
        END IF;

        v_tab_id := v_tab_id + 1;
        UPDATE v266_tabs SET
            id = v_tab_id,
            layout_id = v_layout,
            entity_type_code = 'emergency_team',
            props_id = v_props,
            tenant_id = p_tenant,
            column_meta = replace(replace(coalesce(column_meta::text, '{}'), 'external_rescue_resource', 'emergency_team'), '外部救援资源', '应急队伍')::jsonb,
            creator = 'V266',
            create_time = CURRENT_TIMESTAMP,
            updater = 'V266',
            update_time = CURRENT_TIMESTAMP,
            deleted = false
        WHERE id = v_row.old_id;
    END LOOP;
    DROP TABLE v266_tab_ids;
    INSERT INTO dynamicbusiness.dm_data_tab_layout SELECT * FROM v266_tabs;

    INSERT INTO dynamicbusiness.dm_data_tab_column_relation (
        entity_type_code, edge_id, from_column_identity, to_column_identity, relation_kind,
        from_type_code, to_type_code, relation_meta, creator, tenant_id, layout_id, deleted
    )
    SELECT
        'emergency_team',
        'edge-' || substr(md5(edge_id || v_layout::text), 1, 12),
        from_column_identity,
        to_column_identity,
        relation_kind,
        'emergency_team',
        'emergency_team',
        relation_meta,
        'V266',
        p_tenant,
        v_layout,
        false
    FROM dynamicbusiness.dm_data_tab_column_relation
    WHERE layout_id = p_source_layout AND deleted = false AND tenant_id = p_tenant;

    DROP TABLE v266_tabs;
    DROP TABLE v266_layout;
    RETURN v_layout;
END;
$$;

DO $$
DECLARE
    v_data_src bigint;
    v_model_src bigint;
    v_data_layout bigint;
    v_model_layout bigint;
    v_root bigint;
    v_child bigint;
    v_model bigint;
    v_field bigint;
    v_name text;
    v_code text;
BEGIN
    IF EXISTS (
        SELECT 1 FROM dynamicbusiness.dynamic_entity_type
        WHERE code = 'emergency_team' AND tenant_id = 1 AND deleted = false
    ) THEN
        RETURN;
    END IF;

    SELECT data_layout_id, model_layout_id INTO v_data_src, v_model_src
    FROM dynamicbusiness.dynamic_entity_type
    WHERE code = 'external_rescue_resource' AND tenant_id = 1 AND deleted = false;
    IF v_data_src IS NULL OR v_model_src IS NULL THEN
        RAISE EXCEPTION '租户 1 没有外部救援资源的页面布局，不能按同样的栏建立应急队伍';
    END IF;

    v_data_layout := dynamicbusiness.v266_clone_layout(v_data_src, '数据页签·emergency_team', 1);
    v_model_layout := dynamicbusiness.v266_clone_layout(v_model_src, '模型管理·emergency_team', 1);

    EXECUTE 'CREATE TABLE IF NOT EXISTS dynamicbusiness.ent_emergency_team_t1 (LIKE dynamicbusiness.ent_external_rescue_resource_t1 INCLUDING DEFAULTS)';
    EXECUTE 'ALTER TABLE dynamicbusiness.ent_emergency_team_t1 ADD COLUMN IF NOT EXISTS pipeline_segment_id bigint';
    EXECUTE 'CREATE SEQUENCE IF NOT EXISTS dynamicbusiness.ent_emergency_team_t1_id_seq';
    EXECUTE 'ALTER TABLE dynamicbusiness.ent_emergency_team_t1 ALTER COLUMN id SET DEFAULT nextval(''dynamicbusiness.ent_emergency_team_t1_id_seq'')';
    PERFORM setval('dynamicbusiness.ent_emergency_team_t1_id_seq', (SELECT COALESCE(MAX(id), 1) FROM dynamicbusiness.ent_emergency_team_t1));
    BEGIN
        EXECUTE 'ALTER TABLE dynamicbusiness.ent_emergency_team_t1 ADD PRIMARY KEY (id)';
    EXCEPTION WHEN duplicate_object OR invalid_table_definition THEN
        NULL;
    END;

    CREATE TEMP TABLE v266_type ON COMMIT DROP AS
        SELECT * FROM dynamicbusiness.dynamic_entity_type
        WHERE code = 'external_rescue_resource' AND tenant_id = 1 AND deleted = false;
    UPDATE v266_type SET
        id = nextval('dynamicbusiness.dynamic_entity_type_id_seq'),
        code = 'emergency_team',
        name = '应急队伍',
        description = '按管线段管理的应急队伍。一条队伍归一段。',
        dedicated_table_name = 'ent_emergency_team_t1',
        data_layout_id = v_data_layout,
        model_layout_id = v_model_layout,
        sort = 12,
        group_name = '应急管理',
        creator = 'V266',
        create_time = CURRENT_TIMESTAMP,
        updater = 'V266',
        update_time = CURRENT_TIMESTAMP,
        deleted = false,
        tenant_id = 1;
    INSERT INTO dynamicbusiness.dynamic_entity_type SELECT * FROM v266_type;
    DROP TABLE v266_type;

    SELECT id INTO v_field
    FROM dynamicbusiness.dynamic_field
    WHERE tenant_id = 1 AND deleted = false AND code = 'pipeline_segment_id'
    ORDER BY id LIMIT 1;
    INSERT INTO dynamicbusiness.dynamic_entity_type_base_field (
        entity_type_code, field_code, field_name, data_type, required, description,
        sort_order, status, creator, tenant_id, library_field_id, is_searchable, is_filterable, is_sortable
    )
    SELECT 'emergency_team', 'pipeline_segment_id', '所属管线段', 'REF', true, '这条队伍归哪一段管线。',
           1, 1, 'V266', 1, v_field, false, true, false
    WHERE v_field IS NOT NULL
      AND NOT EXISTS (
          SELECT 1 FROM dynamicbusiness.dynamic_entity_type_base_field
          WHERE entity_type_code = 'emergency_team' AND field_code = 'pipeline_segment_id'
            AND tenant_id = 1 AND deleted = false
      );

    SELECT id INTO v_field
    FROM dynamicbusiness.dynamic_field
    WHERE tenant_id = 1 AND deleted = false AND code = 'team_member_ids'
    ORDER BY id LIMIT 1;
    INSERT INTO dynamicbusiness.dynamic_entity_type_base_field (
        entity_type_code, field_code, field_name, data_type, required, description,
        sort_order, status, creator, tenant_id, library_field_id, is_searchable, is_filterable, is_sortable
    )
    SELECT 'emergency_team', 'team_member_ids', '队员', 'REF_Multi', false,
           '这支队伍里的人。只记人员编号。同一个人可以同时在多支队伍里。',
           2, 1, 'V266', 1, v_field, false, false, false
    WHERE v_field IS NOT NULL
      AND NOT EXISTS (
          SELECT 1 FROM dynamicbusiness.dynamic_entity_type_base_field
          WHERE entity_type_code = 'emergency_team' AND field_code = 'team_member_ids'
            AND tenant_id = 1 AND deleted = false
      );

    CREATE TEMP TABLE v266_cat_type ON COMMIT DROP AS
        SELECT * FROM dynamicbusiness.dynamic_category_type
        WHERE category_type_code = 'external_rescue_resource' AND tenant_id = 1 AND deleted = false
        ORDER BY id LIMIT 1;
    UPDATE v266_cat_type SET
        id = nextval('dynamicbusiness.dynamic_category_type_id_seq'),
        category_type_code = 'emergency_team',
        name = '应急队伍',
        description = '应急队伍默认分类',
        top_level_category_id = NULL,
        creator = 'V266',
        create_time = CURRENT_TIMESTAMP,
        updater = 'V266',
        update_time = CURRENT_TIMESTAMP,
        deleted = false,
        tenant_id = 1;
    INSERT INTO dynamicbusiness.dynamic_category_type SELECT * FROM v266_cat_type;
    DROP TABLE v266_cat_type;

    CREATE TEMP TABLE v266_root ON COMMIT DROP AS
        SELECT * FROM dynamicbusiness.dynamic_category
        WHERE code = 'external_rescue_resource_root' AND tenant_id = 1 AND deleted = false
        ORDER BY id LIMIT 1;
    UPDATE v266_root SET
        id = nextval('dynamicbusiness.dynamic_category_id_seq'),
        code = 'emergency_team_root',
        name = '应急队伍',
        category_type_code = 'emergency_team',
        parent_id = NULL,
        level = 1,
        creator = 'V266',
        create_time = CURRENT_TIMESTAMP,
        updater = 'V266',
        update_time = CURRENT_TIMESTAMP,
        deleted = false,
        tenant_id = 1;
    UPDATE v266_root SET tree_path = '/' || id::text || '/';
    INSERT INTO dynamicbusiness.dynamic_category SELECT * FROM v266_root;
    SELECT id INTO v_root FROM v266_root;
    DROP TABLE v266_root;

    UPDATE dynamicbusiness.dynamic_category_type
    SET top_level_category_id = v_root
    WHERE category_type_code = 'emergency_team' AND tenant_id = 1 AND deleted = false;

    CREATE TEMP TABLE v266_team_name ON COMMIT DROP AS
        SELECT DISTINCT m.name
        FROM dynamicbusiness.dynamic_model m
        JOIN dynamicbusiness.dynamic_model_category_relation_t1 r
          ON r.model_id = m.id AND r.deleted = false AND r.tenant_id = 1
        JOIN dynamicbusiness.dynamic_category c
          ON c.id = r.category_id AND c.deleted = false AND c.name = '应急队伍'
         AND c.category_type_code = 'emergency_resource'
        WHERE m.deleted = false AND m.tenant_id = 1 AND m.entity_type_code = 'emergency_resource';

    FOR v_name IN SELECT name FROM v266_team_name ORDER BY name LOOP
        v_code := 'et-' || substr(md5(v_name), 1, 16);
        INSERT INTO dynamicbusiness.dynamic_model (
            code, name, entity_type_code, description, status, sort, creator, tenant_id, governance_status
        )
        SELECT v_code, v_name, 'emergency_team', v_name, 1, 0, 'V266', 1, 'COMPANY'
        WHERE NOT EXISTS (
            SELECT 1 FROM dynamicbusiness.dynamic_model
            WHERE code = v_code AND tenant_id = 1 AND deleted = false
        );
        SELECT id INTO v_model FROM dynamicbusiness.dynamic_model
        WHERE code = v_code AND tenant_id = 1 AND deleted = false;

        CREATE TEMP TABLE v266_child ON COMMIT DROP AS
            SELECT * FROM dynamicbusiness.dynamic_category WHERE id = v_root;
        UPDATE v266_child SET
            id = nextval('dynamicbusiness.dynamic_category_id_seq'),
            code = 'et-cat-' || substr(md5(v_name), 1, 12),
            name = v_name,
            parent_id = v_root,
            level = 1,
            creator = 'V266',
            create_time = CURRENT_TIMESTAMP,
            updater = 'V266',
            update_time = CURRENT_TIMESTAMP,
            deleted = false,
            tenant_id = 1;
        UPDATE v266_child SET tree_path = '/' || v_root::text || '/' || id::text || '/';
        INSERT INTO dynamicbusiness.dynamic_category SELECT * FROM v266_child;
        SELECT id INTO v_child FROM v266_child;
        DROP TABLE v266_child;

        INSERT INTO dynamicbusiness.dynamic_model_category_relation (
            model_id, category_id, entity_type_code, sort, creator, tenant_id, model_code, category_code
        )
        SELECT v_model, v_child, 'emergency_team', 0, 'V266', 1, v_code, 'et-cat-' || substr(md5(v_name), 1, 12)
        WHERE NOT EXISTS (
            SELECT 1 FROM dynamicbusiness.dynamic_model_category_relation r
            WHERE r.model_id = v_model AND r.category_id = v_child AND r.deleted = false
        );
        INSERT INTO dynamicbusiness.dynamic_model_category_relation_t1 (
            model_id, category_id, entity_type_code, sort, creator, tenant_id, model_code, category_code
        )
        SELECT v_model, v_child, 'emergency_team', 0, 'V266', 1, v_code, 'et-cat-' || substr(md5(v_name), 1, 12)
        WHERE NOT EXISTS (
            SELECT 1 FROM dynamicbusiness.dynamic_model_category_relation_t1 r
            WHERE r.model_id = v_model AND r.category_id = v_child AND r.deleted = false
        );

        INSERT INTO dynamicbusiness.dynamic_model_field_assignment (
            model_id, field_id, model_code, field_code, required,
            is_searchable, is_filterable, is_sortable, sort, field_source,
            creator, tenant_id, create_visible, edit_visible, detail_visible
        )
        SELECT v_model, f.id, v_code, f.code, f.code = 'pipeline_segment_id',
               false, f.code = 'pipeline_segment_id', false,
               CASE f.code WHEN 'pipeline_segment_id' THEN 1 ELSE 2 END,
               'LIBRARY', 'V266', 1, true, true, true
        FROM dynamicbusiness.dynamic_field f
        WHERE f.tenant_id = 1 AND f.deleted = false
          AND f.code IN ('pipeline_segment_id', 'team_member_ids')
          AND NOT EXISTS (
              SELECT 1 FROM dynamicbusiness.dynamic_model_field_assignment a
              WHERE a.model_id = v_model AND a.field_code = f.code AND a.tenant_id = 1 AND a.deleted = false
          );
    END LOOP;

    INSERT INTO dynamicbusiness.dynamic_catalog_ownership (
        registry_code, field_code, target_registry_code, required, multi_valued, creator, tenant_id, deleted
    )
    SELECT 'emergency_team', 'pipeline_segment_id', 'pipeline', true, false, 'V266', 1, false
    WHERE NOT EXISTS (
        SELECT 1 FROM dynamicbusiness.dynamic_catalog_ownership
        WHERE registry_code = 'emergency_team' AND tenant_id = 1 AND deleted = false
    );

    INSERT INTO dynamicbusiness.dynamic_catalog_role_grant (
        role_id, registry_code, operation, creator, tenant_id, subject, deleted
    )
    SELECT g.role_id, 'emergency_team', g.operation, 'V266', g.tenant_id, g.subject, false
    FROM dynamicbusiness.dynamic_catalog_role_grant g
    WHERE g.registry_code = 'external_rescue_resource' AND g.tenant_id = 1 AND g.deleted = false
      AND NOT EXISTS (
          SELECT 1 FROM dynamicbusiness.dynamic_catalog_role_grant x
          WHERE x.role_id = g.role_id AND x.registry_code = 'emergency_team'
            AND x.subject = g.subject AND x.operation = g.operation
            AND x.tenant_id = g.tenant_id AND x.deleted = false
      );

    INSERT INTO dynamicbusiness.dynamic_management_unit_catalog (
        management_unit_model_id, registry_code, creator, tenant_id, deleted
    )
    SELECT c.management_unit_model_id, 'emergency_team', 'V266', c.tenant_id, false
    FROM dynamicbusiness.dynamic_management_unit_catalog c
    WHERE c.deleted = false AND c.tenant_id = 1 AND c.registry_code = 'external_rescue_resource'
      AND NOT EXISTS (
          SELECT 1 FROM dynamicbusiness.dynamic_management_unit_catalog x
          WHERE x.deleted = false AND x.tenant_id = c.tenant_id
            AND x.management_unit_model_id = c.management_unit_model_id
            AND x.registry_code = 'emergency_team'
      );

    INSERT INTO dynamicbusiness.ent_emergency_team_t1 (
        tenant_id, entity_type_code, model_id, name, code, status, pipeline_segment_id,
        creator, updater, deleted
    )
    SELECT 1, 'emergency_team', m.id, segment.name || m.name,
           'et-' || segment.id || '-' || m.id, 1, segment.id, 'V266', 'V266', false
    FROM dynamicbusiness.ent_pipeline_t1 segment
    JOIN dynamicbusiness.dynamic_model m
      ON m.tenant_id = 1 AND m.deleted = false AND m.entity_type_code = 'emergency_team'
    WHERE segment.tenant_id = 1 AND segment.deleted = false AND coalesce(segment.status, 1) = 1
      AND NOT EXISTS (
          SELECT 1 FROM dynamicbusiness.ent_pipeline_t1 child
          WHERE child.tenant_id = 1 AND child.deleted = false AND child.parent_id = segment.id
      )
      AND NOT EXISTS (
          SELECT 1 FROM dynamicbusiness.ent_emergency_team_t1 existing
          WHERE existing.tenant_id = 1 AND existing.deleted = false
            AND existing.pipeline_segment_id = segment.id AND existing.model_id = m.id
      );

    INSERT INTO dynamicbusiness.dynamic_entity_category_relation (
        entity_id, category_id, entity_type_code, sort, creator, updater, deleted, tenant_id
    )
    SELECT e.id, c.id, 'emergency_team', 0, 'V266', 'V266', false, 1
    FROM dynamicbusiness.ent_emergency_team_t1 e
    JOIN dynamicbusiness.dynamic_model m ON m.id = e.model_id
    JOIN dynamicbusiness.dynamic_category c
      ON c.tenant_id = 1 AND c.deleted = false AND c.category_type_code = 'emergency_team'
     AND c.name = m.name AND c.parent_id IS NOT NULL
    WHERE e.deleted = false AND e.creator = 'V266'
      AND NOT EXISTS (
          SELECT 1 FROM dynamicbusiness.dynamic_entity_category_relation r
          WHERE r.deleted = false AND r.entity_id = e.id AND r.category_id = c.id
            AND r.entity_type_code = 'emergency_team'
      );

    INSERT INTO dynamicbusiness.dynamic_entity_category_relation_t1 (
        entity_id, category_id, entity_type_code, sort, creator, updater, deleted, tenant_id
    )
    SELECT e.id, c.id, 'emergency_team', 0, 'V266', 'V266', false, 1
    FROM dynamicbusiness.ent_emergency_team_t1 e
    JOIN dynamicbusiness.dynamic_model m ON m.id = e.model_id
    JOIN dynamicbusiness.dynamic_category c
      ON c.tenant_id = 1 AND c.deleted = false AND c.category_type_code = 'emergency_team'
     AND c.name = m.name AND c.parent_id IS NOT NULL
    WHERE e.deleted = false AND e.creator = 'V266'
      AND NOT EXISTS (
          SELECT 1 FROM dynamicbusiness.dynamic_entity_category_relation_t1 r
          WHERE r.deleted = false AND r.entity_id = e.id AND r.category_id = c.id
            AND r.entity_type_code = 'emergency_team'
      );

    UPDATE dynamicbusiness.ent_emergency_resource_t1 e
    SET deleted = true, updater = 'V266', update_time = CURRENT_TIMESTAMP
    WHERE e.tenant_id = 1 AND e.deleted = false
      AND e.model_id IN (
          SELECT m.id FROM dynamicbusiness.dynamic_model m
          JOIN dynamicbusiness.dynamic_model_category_relation_t1 r
            ON r.model_id = m.id AND r.deleted = false
          JOIN dynamicbusiness.dynamic_category c
            ON c.id = r.category_id AND c.deleted = false AND c.name = '应急队伍'
           AND c.category_type_code = 'emergency_resource'
          WHERE m.entity_type_code = 'emergency_resource'
      );

    UPDATE dynamicbusiness.dynamic_entity_category_relation_t1 r
    SET deleted = true, updater = 'V266', update_time = CURRENT_TIMESTAMP
    FROM dynamicbusiness.dynamic_category c
    WHERE r.category_id = c.id AND r.deleted = false AND r.tenant_id = 1
      AND c.name = '应急队伍' AND c.category_type_code = 'emergency_resource';

    UPDATE dynamicbusiness.dynamic_entity_category_relation r
    SET deleted = true, updater = 'V266', update_time = CURRENT_TIMESTAMP
    FROM dynamicbusiness.dynamic_category c
    WHERE r.category_id = c.id AND r.deleted = false AND r.tenant_id = 1
      AND c.name = '应急队伍' AND c.category_type_code = 'emergency_resource';

    UPDATE dynamicbusiness.dynamic_model_category_relation_t1 r
    SET deleted = true, updater = 'V266', update_time = CURRENT_TIMESTAMP
    FROM dynamicbusiness.dynamic_category c
    WHERE r.category_id = c.id AND r.deleted = false AND r.tenant_id = 1
      AND c.name = '应急队伍' AND c.category_type_code = 'emergency_resource';

    UPDATE dynamicbusiness.dynamic_model_category_relation r
    SET deleted = true, updater = 'V266', update_time = CURRENT_TIMESTAMP
    FROM dynamicbusiness.dynamic_category c
    WHERE r.category_id = c.id AND r.deleted = false AND r.tenant_id = 1
      AND c.name = '应急队伍' AND c.category_type_code = 'emergency_resource';

    UPDATE dynamicbusiness.dynamic_model m
    SET deleted = true, updater = 'V266', update_time = CURRENT_TIMESTAMP
    WHERE m.tenant_id = 1 AND m.deleted = false AND m.entity_type_code = 'emergency_resource'
      AND m.name IN (SELECT name FROM v266_team_name);

    UPDATE dynamicbusiness.dynamic_category
    SET deleted = true, updater = 'V266', update_time = CURRENT_TIMESTAMP
    WHERE tenant_id = 1 AND deleted = false AND name = '应急队伍'
      AND category_type_code = 'emergency_resource';
END $$;

DROP FUNCTION dynamicbusiness.v266_clone_layout(bigint, text, bigint);
