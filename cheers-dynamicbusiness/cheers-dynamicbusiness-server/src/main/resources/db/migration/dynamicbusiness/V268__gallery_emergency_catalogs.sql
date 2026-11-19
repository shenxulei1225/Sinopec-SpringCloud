-- 智慧管廊（租户 123）补应急目录的页面。
-- 站内应急资源和应急事件只有目录行，没有页面布局，数据页打不开。
-- 应急队伍这本目录没有建。布局从本租户已经由创建路径生成的页面复制栏，不手写栏种类。
-- 不编医院、消防、公安的名称和坐标。不编队伍人员。不写归属声明：
-- 管线段还没有记录，写上之后这本目录谁都打不开。

CREATE OR REPLACE FUNCTION dynamicbusiness.v268_clone_layout(
    p_source_layout bigint,
    p_name text,
    p_tenant bigint,
    p_from_code text,
    p_to_code text,
    p_from_name text,
    p_to_name text
) RETURNS bigint
LANGUAGE plpgsql
AS $$
DECLARE
    v_layout bigint;
    v_row record;
    v_props bigint;
    v_tab_id bigint;
BEGIN
    CREATE TEMP TABLE v268_layout ON COMMIT DROP AS
        SELECT * FROM dynamicbusiness.dm_workbench_layout WHERE id = p_source_layout;
    UPDATE v268_layout SET
        id = nextval('dynamicbusiness.dm_workbench_layout_id_seq'),
        name = p_name,
        is_template = false,
        tenant_id = p_tenant,
        creator = 'V268',
        create_time = CURRENT_TIMESTAMP,
        updater = 'V268',
        update_time = CURRENT_TIMESTAMP,
        deleted = false;
    INSERT INTO dynamicbusiness.dm_workbench_layout SELECT * FROM v268_layout;
    SELECT id INTO v_layout FROM v268_layout;

    CREATE TEMP TABLE v268_tabs ON COMMIT DROP AS
        SELECT * FROM dynamicbusiness.dm_data_tab_layout
        WHERE layout_id = p_source_layout AND deleted = false;
    CREATE TEMP TABLE v268_tab_ids ON COMMIT DROP AS
        SELECT id AS old_id FROM v268_tabs;

    PERFORM pg_advisory_xact_lock(26801);
    SELECT COALESCE(MAX(id), 0) INTO v_tab_id FROM dynamicbusiness.dm_data_tab_layout;

    FOR v_row IN SELECT old_id FROM v268_tab_ids LOOP
        v_props := NULL;
        IF (SELECT props_id FROM v268_tabs WHERE id = v_row.old_id) IS NOT NULL THEN
            CREATE TEMP TABLE v268_props ON COMMIT DROP AS
                SELECT * FROM platformresource.pr_component_props
                WHERE id = (SELECT props_id FROM v268_tabs WHERE id = v_row.old_id);
            UPDATE v268_props SET
                id = nextval('platformresource.pr_component_props_id_seq'),
                name = replace(replace(coalesce(name, ''), p_from_name, p_to_name), p_from_code, p_to_code),
                props = replace(replace(coalesce(props, ''), p_from_name, p_to_name), p_from_code, p_to_code),
                data_source = replace(replace(coalesce(data_source, ''), p_from_name, p_to_name), p_from_code, p_to_code),
                tenant_id = p_tenant,
                creator = 'V268',
                create_time = CURRENT_TIMESTAMP,
                updater = 'V268',
                update_time = CURRENT_TIMESTAMP,
                deleted = false;
            INSERT INTO platformresource.pr_component_props SELECT * FROM v268_props;
            SELECT id INTO v_props FROM v268_props;
            DROP TABLE v268_props;
        END IF;

        v_tab_id := v_tab_id + 1;
        UPDATE v268_tabs SET
            id = v_tab_id,
            layout_id = v_layout,
            entity_type_code = p_to_code,
            props_id = v_props,
            tenant_id = p_tenant,
            column_meta = replace(replace(coalesce(column_meta::text, '{}'), p_from_code, p_to_code), p_from_name, p_to_name)::jsonb,
            creator = 'V268',
            create_time = CURRENT_TIMESTAMP,
            updater = 'V268',
            update_time = CURRENT_TIMESTAMP,
            deleted = false
        WHERE id = v_row.old_id;
    END LOOP;
    DROP TABLE v268_tab_ids;
    INSERT INTO dynamicbusiness.dm_data_tab_layout SELECT * FROM v268_tabs;

    INSERT INTO dynamicbusiness.dm_data_tab_column_relation (
        entity_type_code, edge_id, from_column_identity, to_column_identity, relation_kind,
        from_type_code, to_type_code, relation_meta, creator, tenant_id, layout_id, deleted
    )
    SELECT
        p_to_code,
        'edge-' || substr(md5(edge_id || v_layout::text), 1, 12),
        from_column_identity,
        to_column_identity,
        relation_kind,
        p_to_code,
        p_to_code,
        relation_meta,
        'V268',
        p_tenant,
        v_layout,
        false
    FROM dynamicbusiness.dm_data_tab_column_relation
    WHERE layout_id = p_source_layout AND deleted = false AND tenant_id = p_tenant;

    DROP TABLE v268_tabs;
    DROP TABLE v268_layout;
    RETURN v_layout;
END;
$$;

DO $$
DECLARE
    v_cert_data bigint;
    v_cert_model bigint;
    v_ext_data bigint;
    v_ext_model bigint;
    v_data bigint;
    v_model bigint;
    v_field bigint;
    v_root bigint;
    v_supply bigint;
    v_vehicle_cat bigint;
    v_child bigint;
    v_model_id bigint;
    v_model_code text;
    v_cat_id bigint;
    v_cat_code text;
BEGIN
    SELECT data_layout_id, model_layout_id INTO v_cert_data, v_cert_model
    FROM dynamicbusiness.dynamic_entity_type
    WHERE tenant_id = 123 AND code = 'person_certificate' AND deleted = false;
    IF v_cert_data IS NULL OR v_cert_model IS NULL THEN
        RAISE EXCEPTION '租户 123 没有人员资质页面布局，不能按同样的栏补应急目录';
    END IF;

    SELECT data_layout_id, model_layout_id INTO v_ext_data, v_ext_model
    FROM dynamicbusiness.dynamic_entity_type
    WHERE tenant_id = 123 AND code = 'external_rescue_resource' AND deleted = false;
    IF v_ext_data IS NULL OR v_ext_model IS NULL THEN
        RAISE EXCEPTION '租户 123 没有外部救援资源页面布局，不能按同样的栏建立应急队伍';
    END IF;

    IF EXISTS (
        SELECT 1 FROM dynamicbusiness.dynamic_entity_type
        WHERE tenant_id = 123 AND code = 'emergency_resource' AND deleted = false AND data_layout_id IS NULL
    ) THEN
        v_data := dynamicbusiness.v268_clone_layout(
            v_cert_data, '数据页签·emergency_resource', 123,
            'person_certificate', 'emergency_resource', '人员资质', '站内应急资源');
        v_model := dynamicbusiness.v268_clone_layout(
            v_cert_model, '模型管理·emergency_resource', 123,
            'person_certificate', 'emergency_resource', '人员资质', '站内应急资源');
        UPDATE dynamicbusiness.dynamic_entity_type
        SET data_layout_id = v_data, model_layout_id = v_model, updater = 'V268', update_time = CURRENT_TIMESTAMP
        WHERE tenant_id = 123 AND code = 'emergency_resource' AND deleted = false;
    END IF;

    IF EXISTS (
        SELECT 1 FROM dynamicbusiness.dynamic_entity_type
        WHERE tenant_id = 123 AND code = 'emergency' AND deleted = false AND data_layout_id IS NULL
    ) THEN
        v_data := dynamicbusiness.v268_clone_layout(
            v_cert_data, '数据页签·emergency', 123,
            'person_certificate', 'emergency', '人员资质', '应急事件');
        v_model := dynamicbusiness.v268_clone_layout(
            v_cert_model, '模型管理·emergency', 123,
            'person_certificate', 'emergency', '人员资质', '应急事件');
        UPDATE dynamicbusiness.dynamic_entity_type
        SET data_layout_id = v_data, model_layout_id = v_model, updater = 'V268', update_time = CURRENT_TIMESTAMP
        WHERE tenant_id = 123 AND code = 'emergency' AND deleted = false;
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM dynamicbusiness.dynamic_entity_type
        WHERE tenant_id = 123 AND code = 'emergency_team' AND deleted = false
    ) THEN
        v_data := dynamicbusiness.v268_clone_layout(
            v_ext_data, '数据页签·emergency_team', 123,
            'external_rescue_resource', 'emergency_team', '外部救援资源', '应急队伍');
        v_model := dynamicbusiness.v268_clone_layout(
            v_ext_model, '模型管理·emergency_team', 123,
            'external_rescue_resource', 'emergency_team', '外部救援资源', '应急队伍');

        EXECUTE 'CREATE TABLE IF NOT EXISTS dynamicbusiness.ent_emergency_team_t123 (LIKE dynamicbusiness.ent_external_rescue_resource_t123 INCLUDING DEFAULTS)';
        EXECUTE 'ALTER TABLE dynamicbusiness.ent_emergency_team_t123 ADD COLUMN IF NOT EXISTS pipeline_segment_id bigint';
        EXECUTE 'CREATE SEQUENCE IF NOT EXISTS dynamicbusiness.ent_emergency_team_t123_id_seq';
        EXECUTE 'ALTER TABLE dynamicbusiness.ent_emergency_team_t123 ALTER COLUMN id SET DEFAULT nextval(''dynamicbusiness.ent_emergency_team_t123_id_seq'')';
        BEGIN
            EXECUTE 'ALTER TABLE dynamicbusiness.ent_emergency_team_t123 ADD PRIMARY KEY (id)';
        EXCEPTION WHEN duplicate_object OR invalid_table_definition THEN
            NULL;
        END;

        CREATE TEMP TABLE v268_type ON COMMIT DROP AS
            SELECT * FROM dynamicbusiness.dynamic_entity_type
            WHERE tenant_id = 123 AND code = 'external_rescue_resource' AND deleted = false;
        UPDATE v268_type SET
            id = nextval('dynamicbusiness.dynamic_entity_type_id_seq'),
            code = 'emergency_team',
            name = '应急队伍',
            description = '按管线段管理的应急队伍。队员只记人员编号。',
            dedicated_table_name = 'ent_emergency_team_t123',
            data_layout_id = v_data,
            model_layout_id = v_model,
            sort = 12,
            creator = 'V268',
            create_time = CURRENT_TIMESTAMP,
            updater = 'V268',
            update_time = CURRENT_TIMESTAMP,
            deleted = false,
            tenant_id = 123;
        INSERT INTO dynamicbusiness.dynamic_entity_type SELECT * FROM v268_type;
        DROP TABLE v268_type;

        SELECT id INTO v_field
        FROM dynamicbusiness.dynamic_field
        WHERE tenant_id = 123 AND deleted = false AND code = 'pipeline_segment_id'
        ORDER BY id LIMIT 1;
        INSERT INTO dynamicbusiness.dynamic_entity_type_base_field (
            entity_type_code, field_code, field_name, data_type, required, description,
            sort_order, status, creator, tenant_id, library_field_id, is_searchable, is_filterable, is_sortable
        )
        SELECT 'emergency_team', 'pipeline_segment_id', '所属管线段', 'REF', true,
               '这条队伍归哪一段管线。',
               1, 1, 'V268', 123, v_field, false, true, false
        WHERE v_field IS NOT NULL
          AND NOT EXISTS (
              SELECT 1 FROM dynamicbusiness.dynamic_entity_type_base_field
              WHERE entity_type_code = 'emergency_team' AND field_code = 'pipeline_segment_id'
                AND tenant_id = 123 AND deleted = false
          );

        IF NOT EXISTS (
            SELECT 1 FROM dynamicbusiness.dynamic_field
            WHERE tenant_id = 123 AND deleted = false AND code = 'team_member_ids'
        ) THEN
            CREATE TEMP TABLE v268_member_field ON COMMIT DROP AS
                SELECT * FROM dynamicbusiness.dynamic_field
                WHERE tenant_id = 1 AND deleted = false AND code = 'team_member_ids'
                ORDER BY id LIMIT 1;
            UPDATE v268_member_field SET
                id = nextval('dynamicbusiness.dynamic_field_id_seq'),
                tenant_id = 123,
                creator = 'V268',
                create_time = CURRENT_TIMESTAMP,
                updater = 'V268',
                update_time = CURRENT_TIMESTAMP,
                deleted = false;
            INSERT INTO dynamicbusiness.dynamic_field SELECT * FROM v268_member_field;
            DROP TABLE v268_member_field;
        END IF;
        SELECT id INTO v_field
        FROM dynamicbusiness.dynamic_field
        WHERE tenant_id = 123 AND deleted = false AND code = 'team_member_ids'
        ORDER BY id LIMIT 1;
        INSERT INTO dynamicbusiness.dynamic_entity_type_base_field (
            entity_type_code, field_code, field_name, data_type, required, description,
            sort_order, status, creator, tenant_id, library_field_id, is_searchable, is_filterable, is_sortable
        )
        SELECT 'emergency_team', 'team_member_ids', '队员', 'REF_Multi', false,
               '这支队伍里的人。只记人员编号。同一个人可以同时在多支队伍里。',
               2, 1, 'V268', 123, v_field, false, false, false
        WHERE v_field IS NOT NULL
          AND NOT EXISTS (
              SELECT 1 FROM dynamicbusiness.dynamic_entity_type_base_field
              WHERE entity_type_code = 'emergency_team' AND field_code = 'team_member_ids'
                AND tenant_id = 123 AND deleted = false
          );

        INSERT INTO dynamicbusiness.dynamic_catalog_role_grant (
            role_id, registry_code, subject, operation, tenant_id,
            creator, create_time, updater, update_time, deleted
        )
        SELECT g.role_id, 'emergency_team', g.subject, g.operation, 123,
               'V268', CURRENT_TIMESTAMP, 'V268', CURRENT_TIMESTAMP, false
        FROM dynamicbusiness.dynamic_catalog_role_grant g
        WHERE g.tenant_id = 123 AND g.deleted = false AND g.registry_code = 'emergency_resource'
          AND NOT EXISTS (
              SELECT 1 FROM dynamicbusiness.dynamic_catalog_role_grant x
              WHERE x.tenant_id = 123 AND x.deleted = false
                AND x.role_id = g.role_id AND x.registry_code = 'emergency_team'
                AND x.subject = g.subject AND x.operation = g.operation
          );
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM dynamicbusiness.dynamic_category_type
        WHERE tenant_id = 123 AND deleted = false AND category_type_code = 'emergency_resource'
    ) THEN
        INSERT INTO dynamicbusiness.dynamic_category_type (
            category_type_code, name, description, status, creator, tenant_id, category_mode
        ) VALUES (
            'emergency_resource', '站内应急资源', '站内应急资源分类', 1, 'V268', 123, 'SIMPLE'
        );
        INSERT INTO dynamicbusiness.dynamic_category (
            name, code, category_type_code, status, level, creator, tenant_id
        ) VALUES (
            '站内应急资源', 'emergency_resource_root', 'emergency_resource', 1, 1, 'V268', 123
        ) RETURNING id INTO v_root;
        UPDATE dynamicbusiness.dynamic_category
        SET tree_path = '/' || v_root::text || '/'
        WHERE id = v_root;
        UPDATE dynamicbusiness.dynamic_category_type
        SET top_level_category_id = v_root
        WHERE tenant_id = 123 AND deleted = false AND category_type_code = 'emergency_resource';

        INSERT INTO dynamicbusiness.dynamic_category (
            name, code, category_type_code, parent_id, status, level, creator, tenant_id
        ) VALUES (
            '物资装备', 'er-supply', 'emergency_resource', v_root, 1, 2, 'V268', 123
        ) RETURNING id INTO v_supply;
        UPDATE dynamicbusiness.dynamic_category
        SET tree_path = '/' || v_root::text || '/' || v_supply::text || '/'
        WHERE id = v_supply;

        INSERT INTO dynamicbusiness.dynamic_category (
            name, code, category_type_code, parent_id, status, level, creator, tenant_id
        ) VALUES (
            '车辆和大型装备', 'er-vehicle', 'emergency_resource', v_root, 1, 2, 'V268', 123
        ) RETURNING id INTO v_vehicle_cat;
        UPDATE dynamicbusiness.dynamic_category
        SET tree_path = '/' || v_root::text || '/' || v_vehicle_cat::text || '/'
        WHERE id = v_vehicle_cat;

        FOREACH v_model_code IN ARRAY ARRAY['er-extinguisher', 'er-detector', 'er-respirator'] LOOP
            SELECT id INTO v_model_id
            FROM dynamicbusiness.dynamic_model
            WHERE tenant_id = 123 AND deleted = false AND entity_type_code = 'emergency_resource' AND code = v_model_code;
            IF v_model_id IS NULL THEN
                CONTINUE;
            END IF;
            INSERT INTO dynamicbusiness.dynamic_category (
                name, code, category_type_code, parent_id, status, level, creator, tenant_id
            )
            SELECT m.name, 'er-cat-' || m.code, 'emergency_resource', v_supply, 1, 3, 'V268', 123
            FROM dynamicbusiness.dynamic_model m
            WHERE m.id = v_model_id
            RETURNING id, code INTO v_child, v_cat_code;
            UPDATE dynamicbusiness.dynamic_category
            SET tree_path = '/' || v_root::text || '/' || v_supply::text || '/' || v_child::text || '/'
            WHERE id = v_child;
            INSERT INTO dynamicbusiness.dynamic_model_category_relation (
                model_id, category_id, entity_type_code, sort, creator, tenant_id, model_code, category_code
            ) VALUES (
                v_model_id, v_child, 'emergency_resource', 0, 'V268', 123, v_model_code, v_cat_code
            );
            INSERT INTO dynamicbusiness.dynamic_model_category_relation_t123 (
                model_id, category_id, entity_type_code, sort, creator, tenant_id, model_code, category_code
            ) VALUES (
                v_model_id, v_child, 'emergency_resource', 0, 'V268', 123, v_model_code, v_cat_code
            );
        END LOOP;

        SELECT id, code INTO v_model_id, v_model_code
        FROM dynamicbusiness.dynamic_model
        WHERE tenant_id = 123 AND deleted = false AND entity_type_code = 'emergency_resource'
          AND code = 'MODEL-emergency-resource';
        IF v_model_id IS NOT NULL THEN
            INSERT INTO dynamicbusiness.dynamic_model_category_relation (
                model_id, category_id, entity_type_code, sort, creator, tenant_id, model_code, category_code
            ) VALUES (
                v_model_id, v_supply, 'emergency_resource', 0, 'V268', 123, v_model_code, 'er-supply'
            );
            INSERT INTO dynamicbusiness.dynamic_model_category_relation_t123 (
                model_id, category_id, entity_type_code, sort, creator, tenant_id, model_code, category_code
            ) VALUES (
                v_model_id, v_supply, 'emergency_resource', 0, 'V268', 123, v_model_code, 'er-supply'
            );
        END IF;

        SELECT id, code INTO v_model_id, v_model_code
        FROM dynamicbusiness.dynamic_model
        WHERE tenant_id = 123 AND deleted = false AND entity_type_code = 'emergency_resource' AND code = 'er-vehicle';
        IF v_model_id IS NOT NULL THEN
            INSERT INTO dynamicbusiness.dynamic_model_category_relation (
                model_id, category_id, entity_type_code, sort, creator, tenant_id, model_code, category_code
            ) VALUES (
                v_model_id, v_vehicle_cat, 'emergency_resource', 0, 'V268', 123, v_model_code, 'er-vehicle'
            );
            INSERT INTO dynamicbusiness.dynamic_model_category_relation_t123 (
                model_id, category_id, entity_type_code, sort, creator, tenant_id, model_code, category_code
            ) VALUES (
                v_model_id, v_vehicle_cat, 'emergency_resource', 0, 'V268', 123, v_model_code, 'er-vehicle'
            );
        END IF;
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM dynamicbusiness.dynamic_category_type
        WHERE tenant_id = 123 AND deleted = false AND category_type_code = 'emergency'
    ) THEN
        INSERT INTO dynamicbusiness.dynamic_category_type (
            category_type_code, name, description, status, creator, tenant_id, category_mode
        ) VALUES (
            'emergency', '应急事件', '应急事件分类', 1, 'V268', 123, 'SIMPLE'
        );
        INSERT INTO dynamicbusiness.dynamic_category (
            name, code, category_type_code, status, level, creator, tenant_id
        ) VALUES (
            '应急事件', 'emergency_root', 'emergency', 1, 1, 'V268', 123
        ) RETURNING id INTO v_root;
        UPDATE dynamicbusiness.dynamic_category SET tree_path = '/' || id::text || '/' WHERE id = v_root;
        UPDATE dynamicbusiness.dynamic_category_type
        SET top_level_category_id = v_root
        WHERE tenant_id = 123 AND deleted = false AND category_type_code = 'emergency';
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM dynamicbusiness.dynamic_category_type
        WHERE tenant_id = 123 AND deleted = false AND category_type_code = 'emergency_team'
    ) THEN
        INSERT INTO dynamicbusiness.dynamic_category_type (
            category_type_code, name, description, status, creator, tenant_id, category_mode
        ) VALUES (
            'emergency_team', '应急队伍', '应急队伍分类', 1, 'V268', 123, 'SIMPLE'
        );
        INSERT INTO dynamicbusiness.dynamic_category (
            name, code, category_type_code, status, level, creator, tenant_id
        ) VALUES (
            '应急队伍', 'emergency_team_root', 'emergency_team', 1, 1, 'V268', 123
        ) RETURNING id INTO v_root;
        UPDATE dynamicbusiness.dynamic_category SET tree_path = '/' || id::text || '/' WHERE id = v_root;
        UPDATE dynamicbusiness.dynamic_category_type
        SET top_level_category_id = v_root
        WHERE tenant_id = 123 AND deleted = false AND category_type_code = 'emergency_team';
    END IF;

    UPDATE dynamicbusiness.ent_emergency_resource_t123 e
    SET model_id = m.id, updater = 'V268', update_time = CURRENT_TIMESTAMP
    FROM dynamicbusiness.dynamic_model m
    WHERE e.deleted = false AND e.tenant_id = 123 AND e.model_id = 1139
      AND m.tenant_id = 123 AND m.deleted = false AND m.entity_type_code = 'emergency_resource'
      AND (
          (e.name LIKE '%灭火器%' AND m.code = 'er-extinguisher')
          OR (e.name LIKE '%检测仪%' AND m.code = 'er-detector')
          OR (e.name LIKE '%呼吸器%' AND m.code = 'er-respirator')
          OR (e.name LIKE '%车%' AND e.name NOT LIKE '%灭火器%' AND m.code = 'er-vehicle')
      );

    UPDATE dynamicbusiness.ent_emergency_resource_t123
    SET model_id = (
            SELECT id FROM dynamicbusiness.dynamic_model
            WHERE tenant_id = 123 AND deleted = false AND code = 'MODEL-emergency-resource'
        ),
        updater = 'V268',
        update_time = CURRENT_TIMESTAMP
    WHERE deleted = false AND tenant_id = 123 AND model_id = 1139;

    CREATE TEMP TABLE v268_new_item ON COMMIT DROP AS
        SELECT * FROM dynamicbusiness.ent_emergency_resource_t123
        WHERE tenant_id = 123 AND deleted = false
        ORDER BY id LIMIT 1;

    -- 防汛、防爆管预案写了这三样，现有账上没有。单位写在扩展字段。不沿用被复制那一行的预案编号。
    IF NOT EXISTS (
        SELECT 1 FROM dynamicbusiness.ent_emergency_resource_t123
        WHERE tenant_id = 123 AND deleted = false AND name = '连体雨裤'
    ) THEN
        UPDATE v268_new_item SET
            id = nextval('dynamicbusiness.ent_emergency_resource_id_seq'),
            name = '连体雨裤',
            code = 'gl-plan-rain-pants',
            model_id = (SELECT id FROM dynamicbusiness.dynamic_model WHERE tenant_id = 123 AND deleted = false AND code = 'MODEL-emergency-resource'),
            parent_id = NULL,
            total_quantity = 2,
            available_quantity = 2,
            custom_fields = '{"unit": "件", "source": "防汛防暴雨、防爆管预案"}'::jsonb,
            creator = 'V268',
            updater = 'V268',
            create_time = CURRENT_TIMESTAMP,
            update_time = CURRENT_TIMESTAMP;
        UPDATE v268_new_item SET tree_path = '/' || id::text || '/';
        INSERT INTO dynamicbusiness.ent_emergency_resource_t123 SELECT * FROM v268_new_item;
    END IF;
    IF NOT EXISTS (
        SELECT 1 FROM dynamicbusiness.ent_emergency_resource_t123
        WHERE tenant_id = 123 AND deleted = false AND name = '雨鞋'
    ) THEN
        UPDATE v268_new_item SET
            id = nextval('dynamicbusiness.ent_emergency_resource_id_seq'),
            name = '雨鞋',
            code = 'gl-plan-rain-boots',
            total_quantity = 12,
            available_quantity = 12,
            custom_fields = '{"unit": "双", "source": "防汛防暴雨预案"}'::jsonb,
            create_time = CURRENT_TIMESTAMP,
            update_time = CURRENT_TIMESTAMP;
        UPDATE v268_new_item SET tree_path = '/' || id::text || '/';
        INSERT INTO dynamicbusiness.ent_emergency_resource_t123 SELECT * FROM v268_new_item;
    END IF;
    IF NOT EXISTS (
        SELECT 1 FROM dynamicbusiness.ent_emergency_resource_t123
        WHERE tenant_id = 123 AND deleted = false AND name = '安全绳、安全带'
    ) THEN
        UPDATE v268_new_item SET
            id = nextval('dynamicbusiness.ent_emergency_resource_id_seq'),
            name = '安全绳、安全带',
            code = 'gl-plan-safety-rope',
            total_quantity = 2,
            available_quantity = 2,
            custom_fields = '{"unit": "个", "source": "防汛防暴雨预案"}'::jsonb,
            create_time = CURRENT_TIMESTAMP,
            update_time = CURRENT_TIMESTAMP;
        UPDATE v268_new_item SET tree_path = '/' || id::text || '/';
        INSERT INTO dynamicbusiness.ent_emergency_resource_t123 SELECT * FROM v268_new_item;
    END IF;
END;
$$;

DROP FUNCTION dynamicbusiness.v268_clone_layout(
    bigint, text, bigint, text, text, text, text
);
