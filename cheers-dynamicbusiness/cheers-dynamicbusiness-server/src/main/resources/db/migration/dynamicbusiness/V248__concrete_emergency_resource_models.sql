-- 站内应急资源：灭火器、检测仪、呼吸器、车辆是分类，不是型号。
-- 每条已有资源按名称建型号，并挂到自己的记录上。
-- 应急预案步骤里点名、名册里还没有的器材，在金桥厂区补一条记录，数量留空。
-- 外部救援资源的型号改为属地医院、属地消防救援站、属地公安。医院、消防、公安仍是分类。

DO $$
DECLARE
    v_item record;
    v_model bigint;
    v_code text;
    v_field record;
    v_field_id bigint;
    v_category bigint;
    v_root bigint;
    v_supply bigint;
BEGIN
    SELECT id INTO v_root
    FROM dynamicbusiness.dynamic_category
    WHERE code = 'emergency_resource_root' AND tenant_id = 1 AND deleted = false
    ORDER BY id LIMIT 1;
    SELECT id INTO v_supply
    FROM dynamicbusiness.dynamic_category
    WHERE code = 'er-supply' AND tenant_id = 1 AND deleted = false
    ORDER BY id LIMIT 1;

    CREATE TEMP TABLE v247_kind (
        code text PRIMARY KEY,
        kind_name text
    ) ON COMMIT DROP;
    INSERT INTO v247_kind (code, kind_name) VALUES
        ('er-kind-extinguisher', '灭火器'),
        ('er-kind-detector', '检测仪'),
        ('er-kind-respirator', '呼吸器');

    FOR v_item IN SELECT * FROM v247_kind LOOP
        IF EXISTS (
            SELECT 1 FROM dynamicbusiness.dynamic_category
            WHERE code = v_item.code AND tenant_id = 1 AND deleted = false
        ) THEN
            CONTINUE;
        END IF;
        CREATE TEMP TABLE v247_cat ON COMMIT DROP AS
            SELECT * FROM dynamicbusiness.dynamic_category WHERE id = v_supply;
        UPDATE v247_cat SET
            id = nextval('dynamicbusiness.dynamic_category_id_seq'),
            code = v_item.code,
            name = v_item.kind_name,
            parent_id = v_supply,
            creator = 'emergency-resource-models',
            create_time = CURRENT_TIMESTAMP,
            updater = 'emergency-resource-models',
            update_time = CURRENT_TIMESTAMP,
            deleted = false,
            tenant_id = 1;
        UPDATE v247_cat SET tree_path = '/' || v_root::text || '/' || v_supply::text || '/' || id::text || '/';
        INSERT INTO dynamicbusiness.dynamic_category SELECT * FROM v247_cat;
        DROP TABLE v247_cat;
    END LOOP;

    CREATE TEMP TABLE v247_item (
        resource_name text PRIMARY KEY,
        category_code text NOT NULL,
        profile text NOT NULL,
        from_plan boolean NOT NULL
    ) ON COMMIT DROP;

    INSERT INTO v247_item (resource_name, category_code, profile, from_plan) VALUES
        ('便携式灭火器', 'er-kind-extinguisher', 'extinguisher', false),
        ('干粉灭火器', 'er-kind-extinguisher', 'extinguisher', false),
        ('灭火器', 'er-kind-extinguisher', 'extinguisher', false),
        ('二氧化碳灭火器', 'er-kind-extinguisher', 'extinguisher', true),
        ('卤代烷灭火器', 'er-kind-extinguisher', 'extinguisher', true),
        ('可燃气体检测仪', 'er-kind-detector', 'detector', false),
        ('手持式气体检测仪', 'er-kind-detector', 'detector', false),
        ('正压式空气呼吸器', 'er-kind-respirator', 'respirator', false),
        ('空气呼吸器', 'er-kind-respirator', 'respirator', false),
        ('工程抢险车', 'er-vehicle', 'vehicle', false),
        ('救护车', 'er-vehicle', 'vehicle', false),
        ('救援指挥车', 'er-vehicle', 'vehicle', false),
        ('消防指挥车', 'er-vehicle', 'vehicle', false),
        ('消防车', 'er-vehicle', 'vehicle', false),
        ('设备运输车辆', 'er-vehicle', 'vehicle', false),
        ('吊车', 'er-vehicle', 'vehicle', true),
        ('挖掘机', 'er-vehicle', 'vehicle', true),
        ('专职消防队', 'er-team', 'team', false),
        ('业务应用应急保障队伍', 'er-team', 'team', false),
        ('工程应急保障队伍', 'er-team', 'team', false),
        ('工程抢险组', 'er-team', 'team', false),
        ('气体防护组', 'er-team', 'team', false),
        ('医疗救护组', 'er-team', 'team', false),
        ('第三方应急保障队伍', 'er-team', 'team', false),
        ('管道抢修队', 'er-team', 'team', false),
        ('网络安全管理应急保障队伍', 'er-team', 'team', false),
        ('运行维护应急保障队伍', 'er-team', 'team', false),
        ('属地武警', 'er-team', 'team', false),
        ('公共卫生突发事件专家', 'er-expert', 'expert', false),
        ('医疗救护专家', 'er-expert', 'expert', false),
        ('管道抢修专家', 'er-expert', 'expert', false),
        ('千斤顶', 'er-supply', 'supply', false),
        ('堵漏工具', 'er-supply', 'supply', false),
        ('头盔', 'er-supply', 'supply', false),
        ('安全绳、安全带', 'er-supply', 'supply', false),
        ('对讲机', 'er-supply', 'supply', false),
        ('应急手电筒', 'er-supply', 'supply', false),
        ('应急照明灯', 'er-supply', 'supply', false),
        ('应急防爆电筒', 'er-supply', 'supply', false),
        ('强光手电筒', 'er-supply', 'supply', false),
        ('急救医疗箱', 'er-supply', 'supply', false),
        ('急救药箱', 'er-supply', 'supply', false),
        ('手电筒', 'er-supply', 'supply', false),
        ('扩音设备', 'er-supply', 'supply', false),
        ('担架', 'er-supply', 'supply', false),
        ('撬棍、锹镐', 'er-supply', 'supply', false),
        ('柴油发电机', 'er-supply', 'supply', false),
        ('橡胶鞋', 'er-supply', 'supply', false),
        ('沙袋', 'er-supply', 'supply', false),
        ('消防水带', 'er-supply', 'supply', false),
        ('潜水泵', 'er-supply', 'supply', false),
        ('照相机', 'er-supply', 'supply', false),
        ('电工斜口钳', 'er-supply', 'supply', false),
        ('绝缘手套', 'er-supply', 'supply', false),
        ('绝缘鞋', 'er-supply', 'supply', false),
        ('绳索', 'er-supply', 'supply', false),
        ('警戒哨臂章', 'er-supply', 'supply', false),
        ('警戒带', 'er-supply', 'supply', false),
        ('警棍', 'er-supply', 'supply', false),
        ('连体雨裤', 'er-supply', 'supply', false),
        ('逃生指示标识', 'er-supply', 'supply', false),
        ('防刺手套', 'er-supply', 'supply', false),
        ('防刺背心', 'er-supply', 'supply', false),
        ('防毒面具', 'er-supply', 'supply', false),
        ('防爆头盔', 'er-supply', 'supply', false),
        ('防爆对讲机', 'er-supply', 'supply', false),
        ('防爆盾牌', 'er-supply', 'supply', false),
        ('防爆钢叉', 'er-supply', 'supply', false),
        ('防雨衣', 'er-supply', 'supply', false),
        ('雨鞋', 'er-supply', 'supply', false),
        ('验电笔', 'er-supply', 'supply', false),
        ('绝缘柄工具', 'er-supply', 'supply', true),
        ('木棍', 'er-supply', 'supply', true),
        ('木板', 'er-supply', 'supply', true),
        ('湿毛巾', 'er-supply', 'supply', true),
        ('通风设备', 'er-supply', 'supply', true),
        ('止血带', 'er-supply', 'supply', true),
        ('警示标志', 'er-supply', 'supply', true),
        ('排水泵', 'er-supply', 'supply', true);

    CREATE TEMP TABLE v247_profile_field (
        profile text NOT NULL,
        field_code text NOT NULL,
        sort integer NOT NULL
    ) ON COMMIT DROP;
    INSERT INTO v247_profile_field (profile, field_code, sort) VALUES
        ('extinguisher', 'total_quantity', 1),
        ('extinguisher', 'available_quantity', 2),
        ('extinguisher', 'agent_type', 3),
        ('extinguisher', 'facility_id', 4),
        ('extinguisher', 'coordinate_gis', 5),
        ('detector', 'total_quantity', 1),
        ('detector', 'available_quantity', 2),
        ('detector', 'measure_range', 3),
        ('detector', 'usable_status', 4),
        ('detector', 'facility_id', 5),
        ('detector', 'coordinate_gis', 6),
        ('respirator', 'total_quantity', 1),
        ('respirator', 'available_quantity', 2),
        ('respirator', 'usable_status', 3),
        ('respirator', 'facility_id', 4),
        ('respirator', 'coordinate_gis', 5),
        ('vehicle', 'plate_no', 1),
        ('vehicle', 'vehicle_load', 2),
        ('vehicle', 'license_valid_until', 3),
        ('vehicle', 'current_location', 4),
        ('vehicle', 'facility_id', 5),
        ('vehicle', 'coordinate_gis', 6),
        ('supply', 'total_quantity', 1),
        ('supply', 'available_quantity', 2),
        ('supply', 'facility_id', 3),
        ('supply', 'coordinate_gis', 4),
        ('team', 'facility_id', 1),
        ('team', 'coordinate_gis', 2),
        ('expert', 'facility_id', 1),
        ('expert', 'coordinate_gis', 2);

    FOR v_item IN SELECT * FROM v247_item ORDER BY resource_name LOOP
        v_code := 'er-' || substr(md5(v_item.resource_name), 1, 16);
        SELECT id INTO v_model
        FROM dynamicbusiness.dynamic_model
        WHERE code = v_code AND tenant_id = 1 AND deleted = false;
        IF v_model IS NULL THEN
            INSERT INTO dynamicbusiness.dynamic_model (
                code, name, entity_type_code, description, status, sort, creator, tenant_id, governance_status
            ) VALUES (
                v_code,
                v_item.resource_name,
                'emergency_resource',
                CASE WHEN v_item.from_plan THEN '应急预案步骤点名的器材' ELSE v_item.resource_name END,
                1, 0, 'emergency-resource-models', 1, 'COMPANY'
            )
            RETURNING id INTO v_model;
        END IF;

        SELECT id INTO v_category
        FROM dynamicbusiness.dynamic_category
        WHERE code = v_item.category_code AND tenant_id = 1 AND deleted = false
        ORDER BY id LIMIT 1;
        IF v_category IS NOT NULL AND NOT EXISTS (
            SELECT 1 FROM dynamicbusiness.dynamic_model_category_relation
            WHERE model_id = v_model AND category_id = v_category AND deleted = false
        ) THEN
            INSERT INTO dynamicbusiness.dynamic_model_category_relation (
                model_id, category_id, entity_type_code, sort, creator, tenant_id, model_code, category_code
            ) VALUES (
                v_model, v_category, 'emergency_resource', 0, 'emergency-resource-models', 1, v_code, v_item.category_code
            );
        END IF;

        FOR v_field IN
            SELECT field_code, sort FROM v247_profile_field WHERE profile = v_item.profile ORDER BY sort
        LOOP
            SELECT id INTO v_field_id
            FROM dynamicbusiness.dynamic_field
            WHERE code = v_field.field_code AND tenant_id = 1 AND deleted = false
            ORDER BY id LIMIT 1;
            IF v_field_id IS NULL THEN
                CONTINUE;
            END IF;
            IF NOT EXISTS (
                SELECT 1 FROM dynamicbusiness.dynamic_model_field_assignment
                WHERE model_code = v_code AND field_code = v_field.field_code AND tenant_id = 1 AND deleted = false
            ) THEN
                INSERT INTO dynamicbusiness.dynamic_model_field_assignment (
                    model_id, field_id, model_code, field_code, required, is_searchable, is_filterable, is_sortable,
                    sort, field_source, creator, tenant_id
                ) VALUES (
                    v_model, v_field_id, v_code, v_field.field_code, false, false, true, false,
                    v_field.sort, 'LIBRARY', 'emergency-resource-models', 1
                );
            END IF;
        END LOOP;

        UPDATE dynamicbusiness.ent_emergency_resource_t1
        SET model_id = v_model,
            updater = 'emergency-resource-models',
            update_time = CURRENT_TIMESTAMP
        WHERE tenant_id = 1 AND deleted = false AND name = v_item.resource_name;

        IF NOT EXISTS (
            SELECT 1 FROM dynamicbusiness.ent_emergency_resource_t1
            WHERE tenant_id = 1 AND deleted = false AND name = v_item.resource_name
        ) THEN
            INSERT INTO dynamicbusiness.ent_emergency_resource_t1 (
                tenant_id, entity_type_code, model_id, name, code, status, facility_id,
                creator, updater, deleted
            ) VALUES (
                1, 'emergency_resource', v_model, v_item.resource_name,
                'plan-' || substr(md5(v_item.resource_name), 1, 12),
                1, 44,
                'emergency-resource-models', 'emergency-resource-models', false
            );
        END IF;
    END LOOP;

    UPDATE dynamicbusiness.dynamic_model
    SET deleted = true, updater = 'emergency-resource-models', update_time = CURRENT_TIMESTAMP
    WHERE tenant_id = 1 AND deleted = false AND entity_type_code = 'emergency_resource'
      AND code IN (
          'MODEL-3f3d3918c11148b6b67d1470ac6e8fdc',
          'er-extinguisher', 'er-detector', 'er-respirator', 'er-vehicle'
      )
      AND NOT EXISTS (
          SELECT 1 FROM dynamicbusiness.ent_emergency_resource_t1 e
          WHERE e.model_id = dynamic_model.id AND e.deleted = false
      );

    UPDATE dynamicbusiness.dynamic_model
    SET name = '属地医院', description = '属地医院', updater = 'emergency-resource-models', update_time = CURRENT_TIMESTAMP
    WHERE code = 'ex-hospital' AND tenant_id = 1 AND deleted = false AND name = '医院';
    UPDATE dynamicbusiness.dynamic_model
    SET name = '属地消防救援站', description = '属地消防救援站', updater = 'emergency-resource-models', update_time = CURRENT_TIMESTAMP
    WHERE code = 'ex-fire' AND tenant_id = 1 AND deleted = false AND name = '消防';
    UPDATE dynamicbusiness.dynamic_model
    SET name = '属地公安', description = '属地公安', updater = 'emergency-resource-models', update_time = CURRENT_TIMESTAMP
    WHERE code = 'ex-police' AND tenant_id = 1 AND deleted = false AND name = '公安';
END;
$$;
