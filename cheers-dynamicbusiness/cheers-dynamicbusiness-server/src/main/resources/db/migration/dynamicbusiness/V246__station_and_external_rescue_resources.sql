-- 站内应急资源改名并拆出具体型号。另建外部救援资源目录，按管线段管理。
-- 管线段仍是管线目录的下级，不新建管线段落目录。
-- 布局从同一租户已经由创建路径生成的人员资质布局复制栏身份，避免只插目录行、数据页没有区域清单。

CREATE OR REPLACE FUNCTION dynamicbusiness.v246_clone_layout(
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
    CREATE TEMP TABLE v246_layout ON COMMIT DROP AS
        SELECT * FROM dynamicbusiness.dm_workbench_layout WHERE id = p_source_layout;
    UPDATE v246_layout SET
        id = nextval('dynamicbusiness.dm_workbench_layout_id_seq'),
        name = p_name,
        is_template = false,
        tenant_id = p_tenant,
        creator = 'emergency-resource-split',
        create_time = CURRENT_TIMESTAMP,
        updater = 'emergency-resource-split',
        update_time = CURRENT_TIMESTAMP,
        deleted = false;
    INSERT INTO dynamicbusiness.dm_workbench_layout SELECT * FROM v246_layout;
    SELECT id INTO v_layout FROM v246_layout;

    CREATE TEMP TABLE v246_tabs ON COMMIT DROP AS
        SELECT * FROM dynamicbusiness.dm_data_tab_layout
        WHERE layout_id = p_source_layout AND deleted = false;
    CREATE TEMP TABLE v246_tab_ids ON COMMIT DROP AS
        SELECT id AS old_id FROM v246_tabs;

    -- 栏行编号由应用写入，这张表没有自增序列。迁移里按当前最大编号往后排。
    PERFORM pg_advisory_xact_lock(24601);
    SELECT COALESCE(MAX(id), 0) INTO v_tab_id FROM dynamicbusiness.dm_data_tab_layout;

    FOR v_row IN SELECT old_id FROM v246_tab_ids LOOP
        v_props := NULL;
        IF (SELECT props_id FROM v246_tabs WHERE id = v_row.old_id) IS NOT NULL THEN
            CREATE TEMP TABLE v246_props ON COMMIT DROP AS
                SELECT * FROM platformresource.pr_component_props
                WHERE id = (SELECT props_id FROM v246_tabs WHERE id = v_row.old_id);
            UPDATE v246_props SET
                id = nextval('platformresource.pr_component_props_id_seq'),
                name = replace(replace(coalesce(name, ''), '人员资质', '外部救援资源'), 'person_certificate', 'external_rescue_resource'),
                props = replace(replace(coalesce(props, ''), '人员资质', '外部救援资源'), 'person_certificate', 'external_rescue_resource'),
                data_source = replace(replace(coalesce(data_source, ''), '人员资质', '外部救援资源'), 'person_certificate', 'external_rescue_resource'),
                tenant_id = p_tenant,
                creator = 'emergency-resource-split',
                create_time = CURRENT_TIMESTAMP,
                updater = 'emergency-resource-split',
                update_time = CURRENT_TIMESTAMP,
                deleted = false;
            INSERT INTO platformresource.pr_component_props SELECT * FROM v246_props;
            SELECT id INTO v_props FROM v246_props;
            DROP TABLE v246_props;
        END IF;

        v_tab_id := v_tab_id + 1;
        UPDATE v246_tabs SET
            id = v_tab_id,
            layout_id = v_layout,
            entity_type_code = 'external_rescue_resource',
            props_id = v_props,
            tenant_id = p_tenant,
            column_meta = replace(replace(column_meta::text, 'person_certificate', 'external_rescue_resource'), '人员资质', '外部救援资源')::jsonb,
            creator = 'emergency-resource-split',
            create_time = CURRENT_TIMESTAMP,
            updater = 'emergency-resource-split',
            update_time = CURRENT_TIMESTAMP,
            deleted = false
        WHERE id = v_row.old_id;
    END LOOP;
    DROP TABLE v246_tab_ids;

    INSERT INTO dynamicbusiness.dm_data_tab_layout SELECT * FROM v246_tabs;

    INSERT INTO dynamicbusiness.dm_data_tab_column_relation (
        entity_type_code, edge_id, from_column_identity, to_column_identity, relation_kind,
        from_type_code, to_type_code, relation_meta, creator, tenant_id, layout_id, deleted
    )
    SELECT
        'external_rescue_resource',
        'edge-' || substr(md5(edge_id || v_layout::text), 1, 12),
        from_column_identity,
        to_column_identity,
        relation_kind,
        'external_rescue_resource',
        'external_rescue_resource',
        relation_meta,
        'emergency-resource-split',
        p_tenant,
        v_layout,
        false
    FROM dynamicbusiness.dm_data_tab_column_relation
    WHERE layout_id = p_source_layout AND deleted = false AND tenant_id = p_tenant;

    DROP TABLE v246_tabs;
    DROP TABLE v246_layout;
    RETURN v_layout;
END;
$$;

DO $$
DECLARE
    v_tenant bigint;
    v_data_src bigint;
    v_model_src bigint;
    v_data_layout bigint;
    v_model_layout bigint;
    v_root bigint;
    v_child bigint;
    v_type_id bigint;
    v_table text;
    v_station_table text;
    v_segment bigint;
    v_model bigint;
    v_field bigint;
    v_src_field bigint;
    v_pair record;
BEGIN
    FOR v_tenant IN
        SELECT tenant_id
        FROM dynamicbusiness.dynamic_entity_type
        WHERE code = 'emergency_resource' AND deleted = false
        ORDER BY tenant_id
    LOOP
        UPDATE dynamicbusiness.dynamic_entity_type
        SET name = '站内应急资源', updater = 'emergency-resource-split', update_time = CURRENT_TIMESTAMP
        WHERE code = 'emergency_resource' AND tenant_id = v_tenant AND deleted = false AND name <> '站内应急资源';

        UPDATE dynamicbusiness.dynamic_category_type
        SET name = '站内应急资源', updater = 'emergency-resource-split', update_time = CURRENT_TIMESTAMP
        WHERE category_type_code = 'emergency_resource' AND tenant_id = v_tenant AND deleted = false AND name <> '站内应急资源';

        UPDATE dynamicbusiness.dynamic_category
        SET name = '站内应急资源', updater = 'emergency-resource-split', update_time = CURRENT_TIMESTAMP
        WHERE category_type_code = 'emergency_resource' AND parent_id IS NULL AND tenant_id = v_tenant AND deleted = false
          AND name <> '站内应急资源';

        UPDATE dynamicbusiness.dynamic_model
        SET name = '未归类', updater = 'emergency-resource-split', update_time = CURRENT_TIMESTAMP
        WHERE entity_type_code = 'emergency_resource' AND tenant_id = v_tenant AND deleted = false AND name = '通用模型';

        v_station_table := 'ent_emergency_resource_t' || v_tenant;
        IF EXISTS (
            SELECT 1 FROM information_schema.tables
            WHERE table_schema = 'dynamicbusiness' AND table_name = v_station_table
        ) THEN
            EXECUTE format('ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS agent_type varchar(255)', v_station_table);
            EXECUTE format('ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS measure_range varchar(255)', v_station_table);
            EXECUTE format('ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS usable_status varchar(255)', v_station_table);
            EXECUTE format('ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS current_location varchar(255)', v_station_table);
        END IF;

        -- 站内新型号各自的字段。字段库按本租户已有文本字段复制一行再改编码。
        FOR v_pair IN
            SELECT * FROM (VALUES
                ('agent_type', '药剂类型', 'TEXT'),
                ('measure_range', '量程', 'TEXT'),
                ('usable_status', '是否可用', 'TEXT'),
                ('current_location', '当前位置', 'TEXT')
            ) AS t(code, name, data_type)
        LOOP
            SELECT id INTO v_src_field
            FROM dynamicbusiness.dynamic_field
            WHERE tenant_id = v_tenant AND deleted = false AND type = 'TEXT'
            ORDER BY id LIMIT 1;
            IF v_src_field IS NULL THEN
                CONTINUE;
            END IF;
            IF NOT EXISTS (
                SELECT 1 FROM dynamicbusiness.dynamic_field
                WHERE code = v_pair.code AND tenant_id = v_tenant AND deleted = false
            ) THEN
                CREATE TEMP TABLE v246_field ON COMMIT DROP AS
                    SELECT * FROM dynamicbusiness.dynamic_field WHERE id = v_src_field;
                UPDATE v246_field SET
                    id = nextval('dynamicbusiness.dynamic_field_id_seq'),
                    code = v_pair.code,
                    name = v_pair.name,
                    type = 'TEXT',
                    description = v_pair.name,
                    options = NULL,
                    provider_code = NULL,
                    creator = 'emergency-resource-split',
                    create_time = CURRENT_TIMESTAMP,
                    updater = 'emergency-resource-split',
                    update_time = CURRENT_TIMESTAMP,
                    deleted = false,
                    tenant_id = v_tenant;
                INSERT INTO dynamicbusiness.dynamic_field SELECT * FROM v246_field;
                DROP TABLE v246_field;
            END IF;
            SELECT id INTO v_field
            FROM dynamicbusiness.dynamic_field
            WHERE code = v_pair.code AND tenant_id = v_tenant AND deleted = false
            ORDER BY id LIMIT 1;
            IF NOT EXISTS (
                SELECT 1 FROM dynamicbusiness.dynamic_entity_type_base_field
                WHERE entity_type_code = 'emergency_resource' AND field_code = v_pair.code
                  AND tenant_id = v_tenant AND deleted = false
            ) THEN
                INSERT INTO dynamicbusiness.dynamic_entity_type_base_field (
                    entity_type_code, field_code, field_name, data_type, required, description,
                    sort_order, status, creator, tenant_id, library_field_id, is_searchable, is_filterable, is_sortable
                ) VALUES (
                    'emergency_resource', v_pair.code, v_pair.name, 'TEXT', false, v_pair.name,
                    50, 1, 'emergency-resource-split', v_tenant, v_field, false, true, false
                );
            END IF;
        END LOOP;

        FOR v_pair IN
            SELECT * FROM (VALUES
                ('er-extinguisher', '灭火器', 1),
                ('er-detector', '检测仪', 2),
                ('er-respirator', '呼吸器', 3),
                ('er-vehicle', '车辆', 4)
            ) AS t(code, name, sort)
        LOOP
            IF NOT EXISTS (
                SELECT 1 FROM dynamicbusiness.dynamic_model
                WHERE code = v_pair.code AND tenant_id = v_tenant AND deleted = false
            ) THEN
                INSERT INTO dynamicbusiness.dynamic_model (
                    code, name, entity_type_code, description, status, sort, creator, tenant_id, governance_status
                ) VALUES (
                    v_pair.code, v_pair.name, 'emergency_resource', v_pair.name, 1, v_pair.sort,
                    'emergency-resource-split', v_tenant, 'COMPANY'
                );
            END IF;
        END LOOP;

        -- 型号只挂自己的字段。未归类型号保持原有字段，给对不上名称的旧记录用。
        FOR v_pair IN
            SELECT * FROM (VALUES
                ('er-extinguisher', 'total_quantity', 1),
                ('er-extinguisher', 'available_quantity', 2),
                ('er-extinguisher', 'agent_type', 3),
                ('er-extinguisher', 'facility_id', 4),
                ('er-extinguisher', 'coordinate_gis', 5),
                ('er-detector', 'total_quantity', 1),
                ('er-detector', 'available_quantity', 2),
                ('er-detector', 'measure_range', 3),
                ('er-detector', 'usable_status', 4),
                ('er-detector', 'facility_id', 5),
                ('er-detector', 'coordinate_gis', 6),
                ('er-respirator', 'total_quantity', 1),
                ('er-respirator', 'available_quantity', 2),
                ('er-respirator', 'usable_status', 3),
                ('er-respirator', 'facility_id', 4),
                ('er-respirator', 'coordinate_gis', 5),
                ('er-vehicle', 'plate_no', 1),
                ('er-vehicle', 'vehicle_load', 2),
                ('er-vehicle', 'license_valid_until', 3),
                ('er-vehicle', 'current_location', 4),
                ('er-vehicle', 'facility_id', 5),
                ('er-vehicle', 'coordinate_gis', 6)
            ) AS t(model_code, field_code, sort)
        LOOP
            SELECT id INTO v_model
            FROM dynamicbusiness.dynamic_model
            WHERE code = v_pair.model_code AND tenant_id = v_tenant AND deleted = false
            ORDER BY id LIMIT 1;
            SELECT id INTO v_field
            FROM dynamicbusiness.dynamic_field
            WHERE code = v_pair.field_code AND tenant_id = v_tenant AND deleted = false
            ORDER BY id LIMIT 1;
            IF v_model IS NULL OR v_field IS NULL THEN
                CONTINUE;
            END IF;
            IF NOT EXISTS (
                SELECT 1 FROM dynamicbusiness.dynamic_model_field_assignment
                WHERE model_code = v_pair.model_code AND field_code = v_pair.field_code
                  AND tenant_id = v_tenant AND deleted = false
            ) THEN
                INSERT INTO dynamicbusiness.dynamic_model_field_assignment (
                    model_id, field_id, model_code, field_code, required, is_searchable, is_filterable, is_sortable, sort, field_source, creator, tenant_id
                ) VALUES (
                    v_model, v_field, v_pair.model_code, v_pair.field_code, false, false, true, false, v_pair.sort, 'LIBRARY', 'emergency-resource-split', v_tenant
                );
            END IF;
        END LOOP;

        -- 型号挂到已有分类，方便数据页分类栏筛到。
        INSERT INTO dynamicbusiness.dynamic_model_category_relation (
            model_id, category_id, entity_type_code, sort, creator, tenant_id, model_code, category_code
        )
        SELECT m.id, c.id, 'emergency_resource', 0, 'emergency-resource-split', v_tenant, m.code, c.code
        FROM dynamicbusiness.dynamic_model m
        JOIN dynamicbusiness.dynamic_category c
          ON c.tenant_id = v_tenant AND c.deleted = false AND c.category_type_code = 'emergency_resource'
         AND (
            (m.code IN ('er-extinguisher', 'er-detector', 'er-respirator') AND c.code = 'er-supply')
            OR (m.code = 'er-vehicle' AND c.code = 'er-vehicle')
         )
        WHERE m.tenant_id = v_tenant AND m.deleted = false AND m.entity_type_code = 'emergency_resource'
          AND NOT EXISTS (
            SELECT 1 FROM dynamicbusiness.dynamic_model_category_relation r
            WHERE r.model_id = m.id AND r.category_id = c.id AND r.deleted = false
          );

        IF v_tenant = 1 AND EXISTS (
            SELECT 1 FROM information_schema.tables
            WHERE table_schema = 'dynamicbusiness' AND table_name = 'ent_emergency_resource_t1'
        ) THEN
            UPDATE dynamicbusiness.ent_emergency_resource_t1 e
            SET model_id = m.id, updater = 'emergency-resource-split', update_time = CURRENT_TIMESTAMP
            FROM dynamicbusiness.dynamic_model m
            WHERE e.deleted = false AND e.tenant_id = 1 AND m.tenant_id = 1 AND m.deleted = false
              AND m.entity_type_code = 'emergency_resource'
              AND (
                (e.name LIKE '%灭火器%' AND m.code = 'er-extinguisher')
                OR (e.name LIKE '%检测仪%' AND m.code = 'er-detector')
                OR (e.name LIKE '%呼吸器%' AND m.code = 'er-respirator')
                OR (e.name LIKE '%车%' AND e.name NOT LIKE '%灭火器%' AND m.code = 'er-vehicle')
              );
        END IF;

        IF EXISTS (
            SELECT 1 FROM dynamicbusiness.dynamic_entity_type
            WHERE code = 'external_rescue_resource' AND tenant_id = v_tenant AND deleted = false
        ) THEN
            CONTINUE;
        END IF;

        SELECT data_layout_id, model_layout_id INTO v_data_src, v_model_src
        FROM dynamicbusiness.dynamic_entity_type
        WHERE code = 'person_certificate' AND tenant_id = v_tenant AND deleted = false;

        IF v_data_src IS NULL OR v_model_src IS NULL THEN
            RAISE EXCEPTION '租户 % 没有人员资质页面布局，不能按同样的栏复制外部救援资源', v_tenant;
        END IF;

        v_data_layout := dynamicbusiness.v246_clone_layout(v_data_src, '数据页签·external_rescue_resource', v_tenant);
        v_model_layout := dynamicbusiness.v246_clone_layout(v_model_src, '模型管理·external_rescue_resource', v_tenant);

        v_table := 'ent_external_rescue_resource_t' || v_tenant;
        EXECUTE format(
            'CREATE TABLE IF NOT EXISTS dynamicbusiness.%I (LIKE dynamicbusiness.%I INCLUDING DEFAULTS)',
            v_table, v_station_table
        );
        EXECUTE format('CREATE SEQUENCE IF NOT EXISTS dynamicbusiness.%I', v_table || '_id_seq');
        EXECUTE format(
            'ALTER TABLE dynamicbusiness.%I ALTER COLUMN id SET DEFAULT nextval(%L)',
            v_table, 'dynamicbusiness.' || v_table || '_id_seq'
        );
        BEGIN
            EXECUTE format('ALTER TABLE dynamicbusiness.%I ADD PRIMARY KEY (id)', v_table);
        EXCEPTION WHEN duplicate_object OR invalid_table_definition THEN
            NULL;
        END;
        EXECUTE format('ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS pipeline_segment_id bigint', v_table);
        EXECUTE format('ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS longitude numeric(18,4)', v_table);
        EXECUTE format('ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS latitude numeric(18,4)', v_table);
        EXECUTE format('ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS emergency_beds numeric(18,4)', v_table);
        EXECUTE format('ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS receivable_count numeric(18,4)', v_table);
        EXECUTE format('ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS team_size numeric(18,4)', v_table);
        EXECUTE format('ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS arrival_note varchar(255)', v_table);
        EXECUTE format('ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS contact_phone varchar(255)', v_table);

        CREATE TEMP TABLE v246_type ON COMMIT DROP AS
            SELECT * FROM dynamicbusiness.dynamic_entity_type
            WHERE code = 'emergency_resource' AND tenant_id = v_tenant AND deleted = false;
        UPDATE v246_type SET
            id = nextval('dynamicbusiness.dynamic_entity_type_id_seq'),
            code = 'external_rescue_resource',
            name = '外部救援资源',
            description = '医院、消防、公安。一条资源归一个管线段。',
            dedicated_table_name = v_table,
            data_layout_id = v_data_layout,
            model_layout_id = v_model_layout,
            creator = 'emergency-resource-split',
            create_time = CURRENT_TIMESTAMP,
            updater = 'emergency-resource-split',
            update_time = CURRENT_TIMESTAMP,
            deleted = false,
            tenant_id = v_tenant;
        INSERT INTO dynamicbusiness.dynamic_entity_type SELECT * FROM v246_type;
        DROP TABLE v246_type;

        SELECT id INTO v_src_field
        FROM dynamicbusiness.dynamic_field
        WHERE tenant_id = v_tenant AND deleted = false AND code = 'facility_id'
        ORDER BY id LIMIT 1;
        IF v_src_field IS NOT NULL AND NOT EXISTS (
            SELECT 1 FROM dynamicbusiness.dynamic_field
            WHERE code = 'pipeline_segment_id' AND tenant_id = v_tenant AND deleted = false
        ) THEN
            CREATE TEMP TABLE v246_ref ON COMMIT DROP AS
                SELECT * FROM dynamicbusiness.dynamic_field WHERE id = v_src_field;
            UPDATE v246_ref SET
                id = nextval('dynamicbusiness.dynamic_field_id_seq'),
                code = 'pipeline_segment_id',
                name = '所属管线段',
                type = 'ENTITY_REF',
                description = '所属管线段',
                options = NULL,
                provider_code = 'dynamic-entity:pipeline',
                creator = 'emergency-resource-split',
                create_time = CURRENT_TIMESTAMP,
                updater = 'emergency-resource-split',
                update_time = CURRENT_TIMESTAMP,
                deleted = false,
                tenant_id = v_tenant;
            INSERT INTO dynamicbusiness.dynamic_field SELECT * FROM v246_ref;
            DROP TABLE v246_ref;
        END IF;

        FOR v_pair IN
            SELECT * FROM (VALUES
                ('pipeline_segment_id', '所属管线段', 'REF', true, 1),
                ('longitude', '经度', 'NUMBER', false, 2),
                ('latitude', '纬度', 'NUMBER', false, 3),
                ('emergency_beds', '急诊床位', 'NUMBER', false, 4),
                ('receivable_count', '可接收人数', 'NUMBER', false, 5),
                ('team_size', '队伍人数', 'NUMBER', false, 6),
                ('arrival_note', '到达条件', 'TEXT', false, 7),
                ('contact_phone', '联系方式', 'TEXT', false, 8)
            ) AS t(code, name, data_type, required, sort)
        LOOP
            IF v_pair.data_type = 'TEXT' OR v_pair.code IN ('longitude','latitude','emergency_beds','receivable_count','team_size') THEN
                IF v_pair.data_type = 'NUMBER' THEN
                    SELECT id INTO v_src_field
                    FROM dynamicbusiness.dynamic_field
                    WHERE tenant_id = v_tenant AND deleted = false AND type = 'NUMBER'
                    ORDER BY id LIMIT 1;
                ELSIF v_pair.code <> 'pipeline_segment_id' THEN
                    SELECT id INTO v_src_field
                    FROM dynamicbusiness.dynamic_field
                    WHERE tenant_id = v_tenant AND deleted = false AND type = 'TEXT'
                    ORDER BY id LIMIT 1;
                END IF;
                IF v_pair.code <> 'pipeline_segment_id' AND v_src_field IS NOT NULL AND NOT EXISTS (
                    SELECT 1 FROM dynamicbusiness.dynamic_field
                    WHERE code = v_pair.code AND tenant_id = v_tenant AND deleted = false
                ) THEN
                    CREATE TEMP TABLE v246_lib ON COMMIT DROP AS
                        SELECT * FROM dynamicbusiness.dynamic_field WHERE id = v_src_field;
                    UPDATE v246_lib SET
                        id = nextval('dynamicbusiness.dynamic_field_id_seq'),
                        code = v_pair.code,
                        name = v_pair.name,
                        type = CASE WHEN v_pair.data_type = 'NUMBER' THEN 'NUMBER' ELSE 'TEXT' END,
                        description = v_pair.name,
                        options = NULL,
                        provider_code = NULL,
                        creator = 'emergency-resource-split',
                        create_time = CURRENT_TIMESTAMP,
                        updater = 'emergency-resource-split',
                        update_time = CURRENT_TIMESTAMP,
                        deleted = false,
                        tenant_id = v_tenant;
                    INSERT INTO dynamicbusiness.dynamic_field SELECT * FROM v246_lib;
                    DROP TABLE v246_lib;
                END IF;
            END IF;
            SELECT id INTO v_field
            FROM dynamicbusiness.dynamic_field
            WHERE code = v_pair.code AND tenant_id = v_tenant AND deleted = false
            ORDER BY id LIMIT 1;
            IF v_field IS NULL THEN
                CONTINUE;
            END IF;
            IF NOT EXISTS (
                SELECT 1 FROM dynamicbusiness.dynamic_entity_type_base_field
                WHERE entity_type_code = 'external_rescue_resource' AND field_code = v_pair.code
                  AND tenant_id = v_tenant AND deleted = false
            ) THEN
                INSERT INTO dynamicbusiness.dynamic_entity_type_base_field (
                    entity_type_code, field_code, field_name, data_type, required, description,
                    sort_order, status, creator, tenant_id, library_field_id, is_searchable, is_filterable, is_sortable
                ) VALUES (
                    'external_rescue_resource', v_pair.code, v_pair.name, v_pair.data_type, v_pair.required, v_pair.name,
                    v_pair.sort, 1, 'emergency-resource-split', v_tenant, v_field, false, true, false
                );
            END IF;
        END LOOP;

        FOR v_pair IN
            SELECT * FROM (VALUES
                ('ex-hospital', '医院', 1),
                ('ex-fire', '消防', 2),
                ('ex-police', '公安', 3)
            ) AS t(code, name, sort)
        LOOP
            INSERT INTO dynamicbusiness.dynamic_model (
                code, name, entity_type_code, description, status, sort, creator, tenant_id, governance_status
            )
            SELECT v_pair.code, v_pair.name, 'external_rescue_resource', v_pair.name, 1, v_pair.sort,
                   'emergency-resource-split', v_tenant, 'COMPANY'
            WHERE NOT EXISTS (
                SELECT 1 FROM dynamicbusiness.dynamic_model
                WHERE code = v_pair.code AND tenant_id = v_tenant AND deleted = false
            );
        END LOOP;

        FOR v_pair IN
            SELECT * FROM (VALUES
                ('ex-hospital', 'pipeline_segment_id', 1),
                ('ex-hospital', 'longitude', 2),
                ('ex-hospital', 'latitude', 3),
                ('ex-hospital', 'emergency_beds', 4),
                ('ex-hospital', 'receivable_count', 5),
                ('ex-fire', 'pipeline_segment_id', 1),
                ('ex-fire', 'longitude', 2),
                ('ex-fire', 'latitude', 3),
                ('ex-fire', 'team_size', 4),
                ('ex-fire', 'arrival_note', 5),
                ('ex-police', 'pipeline_segment_id', 1),
                ('ex-police', 'longitude', 2),
                ('ex-police', 'latitude', 3),
                ('ex-police', 'contact_phone', 4),
                ('ex-police', 'arrival_note', 5)
            ) AS t(model_code, field_code, sort)
        LOOP
            SELECT id INTO v_model FROM dynamicbusiness.dynamic_model
            WHERE code = v_pair.model_code AND tenant_id = v_tenant AND deleted = false ORDER BY id LIMIT 1;
            SELECT id INTO v_field FROM dynamicbusiness.dynamic_field
            WHERE code = v_pair.field_code AND tenant_id = v_tenant AND deleted = false ORDER BY id LIMIT 1;
            IF v_model IS NULL OR v_field IS NULL THEN
                CONTINUE;
            END IF;
            IF NOT EXISTS (
                SELECT 1 FROM dynamicbusiness.dynamic_model_field_assignment
                WHERE model_code = v_pair.model_code AND field_code = v_pair.field_code
                  AND tenant_id = v_tenant AND deleted = false
            ) THEN
                INSERT INTO dynamicbusiness.dynamic_model_field_assignment (
                    model_id, field_id, model_code, field_code, required, is_searchable, is_filterable, is_sortable, sort, field_source, creator, tenant_id
                ) VALUES (
                    v_model, v_field, v_pair.model_code, v_pair.field_code, v_pair.field_code = 'pipeline_segment_id',
                    false, true, false, v_pair.sort, 'LIBRARY', 'emergency-resource-split', v_tenant
                );
            END IF;
        END LOOP;

        CREATE TEMP TABLE v246_cat_type ON COMMIT DROP AS
            SELECT * FROM dynamicbusiness.dynamic_category_type
            WHERE category_type_code = 'emergency_resource' AND deleted = false
            ORDER BY tenant_id, id LIMIT 1;
        UPDATE v246_cat_type SET
            id = nextval('dynamicbusiness.dynamic_category_type_id_seq'),
            category_type_code = 'external_rescue_resource',
            name = '外部救援资源',
            description = '外部救援资源默认分类',
            top_level_category_id = NULL,
            creator = 'emergency-resource-split',
            create_time = CURRENT_TIMESTAMP,
            updater = 'emergency-resource-split',
            update_time = CURRENT_TIMESTAMP,
            deleted = false,
            tenant_id = v_tenant;
        INSERT INTO dynamicbusiness.dynamic_category_type SELECT * FROM v246_cat_type;
        DROP TABLE v246_cat_type;

        CREATE TEMP TABLE v246_root ON COMMIT DROP AS
            SELECT * FROM dynamicbusiness.dynamic_category
            WHERE category_type_code = 'emergency_resource' AND parent_id IS NULL AND deleted = false
            ORDER BY tenant_id, id LIMIT 1;
        UPDATE v246_root SET
            id = nextval('dynamicbusiness.dynamic_category_id_seq'),
            code = 'external_rescue_resource_root',
            name = '外部救援资源',
            category_type_code = 'external_rescue_resource',
            parent_id = NULL,
            creator = 'emergency-resource-split',
            create_time = CURRENT_TIMESTAMP,
            updater = 'emergency-resource-split',
            update_time = CURRENT_TIMESTAMP,
            deleted = false,
            tenant_id = v_tenant;
        UPDATE v246_root SET tree_path = '/' || id::text || '/';
        INSERT INTO dynamicbusiness.dynamic_category SELECT * FROM v246_root;
        SELECT id INTO v_root FROM v246_root;
        DROP TABLE v246_root;

        UPDATE dynamicbusiness.dynamic_category_type
        SET top_level_category_id = v_root
        WHERE category_type_code = 'external_rescue_resource' AND tenant_id = v_tenant AND deleted = false;

        FOR v_pair IN
            SELECT * FROM (VALUES
                ('ex-hospital', '医院'),
                ('ex-fire', '消防'),
                ('ex-police', '公安')
            ) AS t(code, name)
        LOOP
            CREATE TEMP TABLE v246_child ON COMMIT DROP AS
                SELECT * FROM dynamicbusiness.dynamic_category WHERE id = v_root;
            UPDATE v246_child SET
                id = nextval('dynamicbusiness.dynamic_category_id_seq'),
                code = v_pair.code,
                name = v_pair.name,
                parent_id = v_root,
                creator = 'emergency-resource-split',
                create_time = CURRENT_TIMESTAMP,
                updater = 'emergency-resource-split',
                update_time = CURRENT_TIMESTAMP;
            UPDATE v246_child SET tree_path = '/' || v_root::text || '/' || id::text || '/';
            INSERT INTO dynamicbusiness.dynamic_category SELECT * FROM v246_child;
            SELECT id INTO v_child FROM v246_child;
            DROP TABLE v246_child;

            INSERT INTO dynamicbusiness.dynamic_model_category_relation (
                model_id, category_id, entity_type_code, sort, creator, tenant_id, model_code, category_code
            )
            SELECT m.id, v_child, 'external_rescue_resource', 0, 'emergency-resource-split', v_tenant, m.code, v_pair.code
            FROM dynamicbusiness.dynamic_model m
            WHERE m.code = v_pair.code AND m.tenant_id = v_tenant AND m.deleted = false
              AND NOT EXISTS (
                SELECT 1 FROM dynamicbusiness.dynamic_model_category_relation r
                WHERE r.model_id = m.id AND r.category_id = v_child AND r.deleted = false
              );
        END LOOP;

        INSERT INTO dynamicbusiness.dynamic_catalog_ownership (
            registry_code, field_code, target_registry_code, required, multi_valued, creator, tenant_id, deleted
        )
        SELECT 'external_rescue_resource', 'pipeline_segment_id', 'pipeline', true, false,
               'emergency-resource-split', v_tenant, false
        WHERE NOT EXISTS (
            SELECT 1 FROM dynamicbusiness.dynamic_catalog_ownership
            WHERE registry_code = 'external_rescue_resource' AND tenant_id = v_tenant AND deleted = false
        );

        INSERT INTO dynamicbusiness.dynamic_catalog_role_grant (
            role_id, registry_code, operation, creator, tenant_id, subject, deleted
        )
        SELECT g.role_id, 'external_rescue_resource', g.operation, 'emergency-resource-split', g.tenant_id, g.subject, false
        FROM dynamicbusiness.dynamic_catalog_role_grant g
        WHERE g.registry_code = 'emergency_resource' AND g.tenant_id = v_tenant AND g.deleted = false
          AND NOT EXISTS (
            SELECT 1 FROM dynamicbusiness.dynamic_catalog_role_grant x
            WHERE x.role_id = g.role_id AND x.registry_code = 'external_rescue_resource'
              AND x.subject = g.subject AND x.operation = g.operation
              AND x.tenant_id = g.tenant_id AND x.deleted = false
          );
    END LOOP;

    -- 属地医院、消防、公安不属于金桥厂区自己的器材，改记到外部目录，管线段用这座站当时挂的那段。
    IF EXISTS (
        SELECT 1 FROM information_schema.tables
        WHERE table_schema = 'dynamicbusiness' AND table_name = 'ent_external_rescue_resource_t1'
    ) THEN
        SELECT r.target_entity_id INTO v_segment
        FROM dynamicbusiness.dynamic_entity_relation_t1 r
        WHERE r.deleted = false AND r.field_code = 'pipeline_segment_ids'
          AND r.source_entity_type_code = 'facility' AND r.source_entity_id = 44
        ORDER BY r.target_entity_id LIMIT 1;

        EXECUTE format($move$
            INSERT INTO dynamicbusiness.ent_external_rescue_resource_t1
            SELECT e.*
            FROM dynamicbusiness.ent_emergency_resource_t1 e
            WHERE e.deleted = false AND e.tenant_id = 1
              AND e.name IN ('属地医院', '属地消防救援站', '属地公安')
              AND NOT EXISTS (
                SELECT 1 FROM dynamicbusiness.ent_external_rescue_resource_t1 x
                WHERE x.tenant_id = 1 AND x.deleted = false AND x.name = e.name
              )
        $move$);

        UPDATE dynamicbusiness.ent_external_rescue_resource_t1 e
        SET entity_type_code = 'external_rescue_resource',
            model_id = m.id,
            pipeline_segment_id = v_segment,
            longitude = NULLIF(e.custom_fields #>> '{coordinate_gis,longitude}', '')::numeric,
            latitude = NULLIF(e.custom_fields #>> '{coordinate_gis,latitude}', '')::numeric,
            facility_id = NULL,
            updater = 'emergency-resource-split',
            update_time = CURRENT_TIMESTAMP
        FROM dynamicbusiness.dynamic_model m
        WHERE e.deleted = false AND e.tenant_id = 1 AND e.entity_type_code = 'emergency_resource'
          AND m.tenant_id = 1 AND m.deleted = false AND m.entity_type_code = 'external_rescue_resource'
          AND (
            (e.name = '属地医院' AND m.code = 'ex-hospital')
            OR (e.name = '属地消防救援站' AND m.code = 'ex-fire')
            OR (e.name = '属地公安' AND m.code = 'ex-police')
          );

        UPDATE dynamicbusiness.ent_emergency_resource_t1
        SET deleted = true, updater = 'emergency-resource-split', update_time = CURRENT_TIMESTAMP
        WHERE tenant_id = 1 AND deleted = false
          AND name IN ('属地医院', '属地消防救援站', '属地公安')
          AND EXISTS (
            SELECT 1 FROM dynamicbusiness.ent_external_rescue_resource_t1 x
            WHERE x.tenant_id = 1 AND x.deleted = false AND x.name = ent_emergency_resource_t1.name
          );
    END IF;
END;
$$;

DROP FUNCTION dynamicbusiness.v246_clone_layout(bigint, text, bigint);
