-- 智慧管廊站内应急资源按预案重新建账。
-- 分类是预案。型号是器材种类，不再保留「应急资源」这一个型号。
-- 个、套、只这类按清单数量各建一条实体。米是长度，警戒带 80 米只建一条，数量记在数量列。
-- 每份预案一个存放点，挂到点位。预案没有写房间和坐标，点上不写经纬度。
-- 事故现场处置方案没有器材数量清单，不建这一类。

ALTER TABLE dynamicbusiness.ent_emergency_resource_t123
    ADD COLUMN IF NOT EXISTS location_point_id bigint;

CREATE TABLE IF NOT EXISTS dynamicbusiness.ent_point_t123
    (LIKE dynamicbusiness.ent_point INCLUDING DEFAULTS);
DO $$
BEGIN
    ALTER TABLE dynamicbusiness.ent_point_t123 ADD PRIMARY KEY (id);
EXCEPTION WHEN duplicate_object OR invalid_table_definition THEN
    NULL;
END $$;

INSERT INTO dynamicbusiness.dynamic_field (
    code, name, type, description, source, status, provider_code, creator, tenant_id
)
SELECT 'location_point_id', '放置位置', 'ENTITY_REF', '应急资源放在哪个点位', 'USER', 1,
       'dynamic-entity:point', 'V271', 123
WHERE NOT EXISTS (
    SELECT 1 FROM dynamicbusiness.dynamic_field
    WHERE tenant_id = 123 AND code = 'location_point_id' AND deleted = false
);

INSERT INTO dynamicbusiness.dynamic_entity_type_base_field (
    entity_type_code, field_code, field_name, data_type, required, description, status,
    creator, tenant_id, library_field_id
)
SELECT 'emergency_resource', 'location_point_id', '放置位置', 'ENTITY_REF', false,
       '应急资源放在哪个点位', 1, 'V271', 123, f.id
FROM dynamicbusiness.dynamic_field f
WHERE f.tenant_id = 123 AND f.code = 'location_point_id' AND f.deleted = false
  AND NOT EXISTS (
      SELECT 1 FROM dynamicbusiness.dynamic_entity_type_base_field b
      WHERE b.tenant_id = 123 AND b.entity_type_code = 'emergency_resource'
        AND b.field_code = 'location_point_id' AND b.deleted = false
  );

-- 旧的器材分类、粗型号和按清单合并的实体作废。改由预案分类和按件实体接上。
UPDATE dynamicbusiness.dynamic_category
SET deleted = true, updater = 'V271', update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 123 AND deleted = false AND category_type_code = 'emergency_resource'
  AND code <> 'emergency_resource_root'
  AND code NOT LIKE 'er-plan-%';

UPDATE dynamicbusiness.dynamic_model
SET deleted = true, updater = 'V271', update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 123 AND deleted = false AND entity_type_code = 'emergency_resource'
  AND code IN ('MODEL-emergency-resource', 'er-extinguisher', 'er-detector', 'er-respirator', 'er-vehicle');

UPDATE dynamicbusiness.dynamic_model_category_relation
SET deleted = true, updater = 'V271', update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 123 AND deleted = false AND entity_type_code = 'emergency_resource';
UPDATE dynamicbusiness.dynamic_model_category_relation_t123
SET deleted = true, updater = 'V271', update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 123 AND deleted = false AND entity_type_code = 'emergency_resource';

UPDATE dynamicbusiness.ent_emergency_resource_t123
SET deleted = true, updater = 'V271', update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 123 AND deleted = false;

DO $$
DECLARE
    v_root bigint;
    v_plan record;
    v_model record;
    v_cat bigint;
    v_point_model bigint;
    v_point bigint;
    v_entity bigint;
    v_n int;
    v_field bigint;
BEGIN
    SELECT id INTO v_root
    FROM dynamicbusiness.dynamic_category
    WHERE tenant_id = 123 AND deleted = false AND category_type_code = 'emergency_resource'
      AND code = 'emergency_resource_root';
    IF v_root IS NULL THEN
        RAISE EXCEPTION '站内应急资源没有分类根';
    END IF;

    CREATE TEMP TABLE v271_line (
        plan_code text,
        plan_name text,
        item_name text,
        model_code text,
        unit text,
        copies int,
        stock numeric
    ) ON COMMIT DROP;
    INSERT INTO v271_line VALUES
('fire','火灾事故预案','应急防爆电筒','er-type-01','个',10,1),
('fire','火灾事故预案','空气呼吸器','er-type-02','套',2,1),
('fire','火灾事故预案','橡胶鞋','er-type-03','双',12,1),
('fire','火灾事故预案','防毒面具','er-type-04','个',2,1),
('fire','火灾事故预案','便携式灭火器','er-type-05','个',10,1),
('fire','火灾事故预案','撬棍、锹镐','er-type-06','个',18,1),
('fire','火灾事故预案','救援指挥车','er-type-07','辆',1,1),
('fire','火灾事故预案','防爆对讲机','er-type-08','部',10,1),
('fire','火灾事故预案','担架','er-type-09','具',1,1),
('fire','火灾事故预案','柴油发电机','er-type-10','台',1,1),
('fire','火灾事故预案','急救医疗箱','er-type-11','个',1,1),
('fire','火灾事故预案','警戒带','er-type-12','卷',15,1),
('fire','火灾事故预案','警戒哨臂章','er-type-13','个',10,1),
('fire','火灾事故预案','照相机','er-type-14','台',1,1),
('confined','有限空间中毒窒息预案','应急防爆电筒','er-type-01','个',10,1),
('confined','有限空间中毒窒息预案','空气呼吸器','er-type-02','套',2,1),
('confined','有限空间中毒窒息预案','橡胶鞋','er-type-03','双',12,1),
('confined','有限空间中毒窒息预案','防毒面具','er-type-04','个',2,1),
('confined','有限空间中毒窒息预案','便携式灭火器','er-type-05','个',10,1),
('confined','有限空间中毒窒息预案','撬棍、锹镐','er-type-06','把',10,1),
('confined','有限空间中毒窒息预案','救援指挥车','er-type-07','辆',1,1),
('confined','有限空间中毒窒息预案','防爆对讲机','er-type-08','部',10,1),
('confined','有限空间中毒窒息预案','担架','er-type-09','具',1,1),
('confined','有限空间中毒窒息预案','柴油发电机','er-type-10','台',1,1),
('confined','有限空间中毒窒息预案','急救医疗箱','er-type-11','个',1,1),
('confined','有限空间中毒窒息预案','警戒带','er-type-12','卷',15,1),
('confined','有限空间中毒窒息预案','警戒哨臂章','er-type-13','个',10,1),
('confined','有限空间中毒窒息预案','照相机','er-type-14','台',1,1),
('confined','有限空间中毒窒息预案','绳索','er-type-15','条',1,1),
('confined','有限空间中毒窒息预案','手持式气体检测仪','er-type-16','个',2,1),
('flood','防汛防暴雨预案','潜水泵（功率3Kw）','er-type-17','台',2,1),
('flood','防汛防暴雨预案','柴油发电机','er-type-10','台',1,1),
('flood','防汛防暴雨预案','雨衣','er-type-18','套',21,1),
('flood','防汛防暴雨预案','沙袋','er-type-19','只',200,1),
('flood','防汛防暴雨预案','应急手电筒','er-type-20','个',10,1),
('flood','防汛防暴雨预案','撬棍、锹镐','er-type-06','把',10,1),
('flood','防汛防暴雨预案','对讲机','er-type-21','部',10,1),
('flood','防汛防暴雨预案','担架','er-type-09','具',1,1),
('flood','防汛防暴雨预案','设备运输车辆','er-type-22','辆',1,1),
('flood','防汛防暴雨预案','急救医疗箱','er-type-11','个',1,1),
('flood','防汛防暴雨预案','警戒带','er-type-12','卷',15,1),
('flood','防汛防暴雨预案','警戒哨臂章','er-type-13','个',10,1),
('flood','防汛防暴雨预案','照相机','er-type-14','台',1,1),
('flood','防汛防暴雨预案','连体雨裤','er-type-23','件',2,1),
('flood','防汛防暴雨预案','雨鞋','er-type-24','双',12,1),
('flood','防汛防暴雨预案','安全绳、安全带','er-type-25','个',2,1),
('terror','防恐预案','救援指挥车','er-type-07','辆',1,1),
('terror','防恐预案','防爆对讲机','er-type-08','部',10,1),
('terror','防恐预案','头盔','er-type-26','个',10,1),
('terror','防恐预案','急救医疗箱','er-type-11','个',3,1),
('terror','防恐预案','警戒带','er-type-12','米',1,80),
('terror','防恐预案','警戒哨臂章','er-type-13','个',10,1),
('burst','防爆管预案','潜水泵（功率3Kw）','er-type-17','台',2,1),
('burst','防爆管预案','柴油发电机','er-type-10','台',1,1),
('burst','防爆管预案','防雨衣','er-type-27','套',21,1),
('burst','防爆管预案','沙袋','er-type-19','只',200,1),
('burst','防爆管预案','应急手电筒','er-type-20','个',10,1),
('burst','防爆管预案','撬棍、锹镐','er-type-06','把',18,1),
('burst','防爆管预案','救援指挥车','er-type-07','辆',1,1),
('burst','防爆管预案','对讲机','er-type-21','部',10,1),
('burst','防爆管预案','担架','er-type-09','具',1,1),
('burst','防爆管预案','设备运输车辆','er-type-22','辆',1,1),
('burst','防爆管预案','急救医疗箱','er-type-11','个',1,1),
('burst','防爆管预案','警戒带','er-type-12','卷',15,1),
('burst','防爆管预案','警戒哨臂章','er-type-13','个',10,1),
('burst','防爆管预案','照相机','er-type-14','台',1,1),
('burst','防爆管预案','连体雨裤','er-type-23','件',2,1),
('traffic','交通事故预案','救援指挥车','er-type-07','辆',1,1),
('traffic','交通事故预案','对讲机','er-type-21','部',10,1),
('traffic','交通事故预案','验电笔','er-type-28','个',2,1),
('traffic','交通事故预案','急救医疗箱','er-type-11','个',1,1),
('traffic','交通事故预案','警戒带','er-type-12','卷',15,1),
('traffic','交通事故预案','警戒哨臂章','er-type-13','个',10,1),
('traffic','交通事故预案','手电筒','er-type-29','个',10,1),
('traffic','交通事故预案','担架','er-type-09','具',1,1),
('traffic','交通事故预案','绝缘手套','er-type-30','双',2,1),
('traffic','交通事故预案','绝缘鞋','er-type-31','双',10,1),
('traffic','交通事故预案','电工斜口钳','er-type-32','个',2,1),
('traffic','交通事故预案','绳索','er-type-15','个',1,1),
('traffic','交通事故预案','灭火器','er-type-33','个',10,1),
('shock','触电事故预案','救援指挥车','er-type-07','辆',1,1),
('shock','触电事故预案','对讲机','er-type-21','部',10,1),
('shock','触电事故预案','验电笔','er-type-28','个',2,1),
('shock','触电事故预案','急救医疗箱','er-type-11','个',1,1),
('shock','触电事故预案','警戒带','er-type-12','卷',15,1),
('shock','触电事故预案','警戒哨臂章','er-type-13','个',10,1),
('shock','触电事故预案','手电筒','er-type-29','个',10,1),
('shock','触电事故预案','担架','er-type-09','具',1,1),
('shock','触电事故预案','绝缘手套','er-type-30','双',2,1),
('shock','触电事故预案','绝缘鞋','er-type-31','双',10,1),
('shock','触电事故预案','电工斜口钳','er-type-32','个',2,1),
('shock','触电事故预案','绳索','er-type-15','个',1,1),
('shock','触电事故预案','灭火器','er-type-33','个',10,1),
('theft','防盗预案','救援指挥车','er-type-07','辆',1,1),
('theft','防盗预案','防爆对讲机','er-type-08','部',10,1),
('theft','防盗预案','头盔','er-type-26','个',20,1),
('theft','防盗预案','急救医疗箱','er-type-11','个',1,1),
('theft','防盗预案','警戒带','er-type-12','卷',15,1),
('theft','防盗预案','警戒哨臂章','er-type-13','个',10,1),
('theft','防盗预案','防爆头盔','er-type-34','个',2,1),
('theft','防盗预案','防爆钢叉','er-type-35','个',2,1),
('theft','防盗预案','防爆盾牌','er-type-36','个',2,1),
('theft','防盗预案','防刺背心','er-type-37','个',2,1),
('theft','防盗预案','防刺手套','er-type-38','双',2,1),
('theft','防盗预案','警棍','er-type-39','个',2,1),
('theft','防盗预案','强光手电筒','er-type-40','个',2,1),
('theft','防盗预案','担架','er-type-09','个',1,1),
('theft','防盗预案','绳索','er-type-15','条',1,1),
('quake','地震塌方预案','潜水泵（功率3Kw）','er-type-17','台',2,1),
('quake','地震塌方预案','柴油发电机','er-type-10','台',1,1),
('quake','地震塌方预案','防雨衣','er-type-27','套',21,1),
('quake','地震塌方预案','沙袋','er-type-19','只',200,1),
('quake','地震塌方预案','应急手电筒','er-type-20','个',10,1),
('quake','地震塌方预案','撬棍、锹镐','er-type-06','把',18,1),
('quake','地震塌方预案','救援指挥车','er-type-07','辆',1,1),
('quake','地震塌方预案','对讲机','er-type-21','部',10,1),
('quake','地震塌方预案','担架','er-type-09','具',1,1),
('quake','地震塌方预案','设备运输车辆','er-type-22','辆',1,1),
('quake','地震塌方预案','急救医疗箱','er-type-11','个',1,1),
('quake','地震塌方预案','警戒带','er-type-12','卷',15,1),
('quake','地震塌方预案','警戒哨臂章','er-type-13','个',10,1);

    FOR v_plan IN SELECT DISTINCT plan_code, plan_name FROM v271_line ORDER BY plan_code LOOP
        INSERT INTO dynamicbusiness.dynamic_category (
            name, code, category_type_code, parent_id, status, level, creator, tenant_id
        ) VALUES (
            v_plan.plan_name, 'er-plan-' || v_plan.plan_code, 'emergency_resource', v_root, 1, 2, 'V271', 123
        ) RETURNING id INTO v_cat;
        UPDATE dynamicbusiness.dynamic_category
        SET tree_path = '/' || v_root::text || '/' || v_cat::text || '/'
        WHERE id = v_cat;
    END LOOP;

    FOR v_model IN SELECT DISTINCT item_name, model_code FROM v271_line ORDER BY model_code LOOP
        INSERT INTO dynamicbusiness.dynamic_model (
            code, name, entity_type_code, status, sort, creator, tenant_id
        ) VALUES (
            v_model.model_code, v_model.item_name, 'emergency_resource', 1, 0, 'V271', 123
        );
    END LOOP;

    INSERT INTO dynamicbusiness.dynamic_model_category_relation (
        model_id, category_id, entity_type_code, sort, creator, tenant_id, model_code, category_code
    )
    SELECT m.id, c.id, 'emergency_resource', 0, 'V271', 123, m.code, c.code
    FROM (
        SELECT DISTINCT plan_code, model_code FROM v271_line
    ) x
    JOIN dynamicbusiness.dynamic_model m
      ON m.tenant_id = 123 AND m.deleted = false AND m.code = x.model_code
    JOIN dynamicbusiness.dynamic_category c
      ON c.tenant_id = 123 AND c.deleted = false AND c.code = 'er-plan-' || x.plan_code;
    INSERT INTO dynamicbusiness.dynamic_model_category_relation_t123 (
        model_id, category_id, entity_type_code, sort, creator, tenant_id, model_code, category_code
    )
    SELECT model_id, category_id, entity_type_code, sort, creator, tenant_id, model_code, category_code
    FROM dynamicbusiness.dynamic_model_category_relation
    WHERE tenant_id = 123 AND deleted = false AND creator = 'V271' AND entity_type_code = 'emergency_resource';

    SELECT id INTO v_field
    FROM dynamicbusiness.dynamic_field
    WHERE tenant_id = 123 AND code = 'location_point_id' AND deleted = false;

    INSERT INTO dynamicbusiness.dynamic_model_field_assignment (
        model_id, field_id, required, sort, field_source, creator, tenant_id, model_code, field_code,
        detail_visible, create_visible, edit_visible, target_entity_type
    )
    SELECT m.id, v_field, false, 10, 'BASE', 'V271', 123, m.code, 'location_point_id',
           true, true, true, 'point'
    FROM dynamicbusiness.dynamic_model m
    WHERE m.tenant_id = 123 AND m.deleted = false AND m.entity_type_code = 'emergency_resource'
      AND m.creator = 'V271';

    INSERT INTO dynamicbusiness.dynamic_model (
        code, name, entity_type_code, description, status, sort, creator, tenant_id
    ) VALUES (
        'point-emergency-location', '应急资源位置', 'point', '应急资源的存放点', 1, 0, 'V271', 123
    ) RETURNING id INTO v_point_model;

    FOR v_plan IN SELECT DISTINCT plan_code, plan_name FROM v271_line ORDER BY plan_code LOOP
        v_point := nextval('dynamicbusiness.ent_point_id_seq');
        INSERT INTO dynamicbusiness.ent_point_t123 (
            id, tenant_id, entity_type_code, model_id, name, code, status, facility_id,
            creator, updater, deleted, tree_path
        ) VALUES (
            v_point, 123, 'point', v_point_model,
            v_plan.plan_name || '存放点', 'er-point-' || v_plan.plan_code,
            1, 1, 'V271', 'V271', false, '/' || v_point::text || '/'
        );

        FOR v_model IN
            SELECT item_name, model_code, unit, copies, stock
            FROM v271_line WHERE plan_code = v_plan.plan_code
        LOOP
            FOR v_n IN 1..v_model.copies LOOP
                v_entity := nextval('dynamicbusiness.ent_emergency_resource_id_seq');
                INSERT INTO dynamicbusiness.ent_emergency_resource_t123 (
                    id, tenant_id, entity_type_code, model_id, name, code, status,
                    facility_id, location_point_id, total_quantity, available_quantity,
                    custom_fields, creator, updater, deleted, tree_path
                ) VALUES (
                    v_entity, 123, 'emergency_resource',
                    (SELECT id FROM dynamicbusiness.dynamic_model
                     WHERE tenant_id = 123 AND deleted = false AND code = v_model.model_code),
                    v_model.item_name,
                    'er-' || v_plan.plan_code || '-' || v_model.model_code || '-' || v_n::text,
                    1, 1, v_point, v_model.stock, v_model.stock,
                    jsonb_build_object('unit', v_model.unit, 'plan', v_plan.plan_name),
                    'V271', 'V271', false, '/' || v_entity::text || '/'
                );
                INSERT INTO dynamicbusiness.dynamic_category_entity_link_t123 (
                    category_id, entity_id, entity_model_id, creator, tenant_id, entity_type_code
                )
                SELECT c.id, v_entity,
                       (SELECT id FROM dynamicbusiness.dynamic_model
                        WHERE tenant_id = 123 AND deleted = false AND code = v_model.model_code),
                       'V271', 123, 'emergency_resource'
                FROM dynamicbusiness.dynamic_category c
                WHERE c.tenant_id = 123 AND c.deleted = false AND c.code = 'er-plan-' || v_plan.plan_code;
            END LOOP;
        END LOOP;
    END LOOP;
END $$;

INSERT INTO dynamicbusiness.dm_data_tab_column_relation (
    entity_type_code, edge_id, from_column_identity, to_column_identity, relation_kind,
    from_type_code, to_type_code, relation_meta, creator, tenant_id, layout_id, deleted
)
SELECT 'emergency_resource', 'edge-v271-plan-entity',
       'CATEGORY:category:tab-category-1', 'ENTITY:tab-58170f', 'CATEGORY_ENTITY',
       'emergency_resource', 'emergency_resource',
       '{"edgeAction": "filter", "enabledInteractions": ["filterEmptySkip"]}'::jsonb,
       'V271', 123, 678, false
WHERE NOT EXISTS (
    SELECT 1 FROM dynamicbusiness.dm_data_tab_column_relation
    WHERE tenant_id = 123 AND layout_id = 678 AND deleted = false
      AND relation_kind = 'CATEGORY_ENTITY'
      AND relation_meta->>'edgeAction' = 'filter'
      AND from_column_identity LIKE 'CATEGORY:%'
);

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
    v_data bigint;
    v_model bigint;
    v_src_data bigint;
    v_src_model bigint;
BEGIN
    SELECT data_layout_id, model_layout_id INTO v_src_data, v_src_model
    FROM dynamicbusiness.dynamic_entity_type
    WHERE tenant_id = 123 AND code = 'facility' AND deleted = false;
    IF v_src_data IS NULL OR v_src_model IS NULL THEN
        RAISE EXCEPTION '管廊设施没有页面，不能给点位补页面';
    END IF;
    IF EXISTS (
        SELECT 1 FROM dynamicbusiness.dynamic_entity_type
        WHERE tenant_id = 123 AND code = 'point' AND deleted = false AND data_layout_id IS NULL
    ) THEN
        v_data := dynamicbusiness.v268_clone_layout(
            v_src_data, '数据页签·point', 123, 'facility', 'point', '管廊设施', '点位');
        v_model := dynamicbusiness.v268_clone_layout(
            v_src_model, '模型管理·point', 123, 'facility', 'point', '管廊设施', '点位');
        UPDATE dynamicbusiness.dynamic_entity_type
        SET data_layout_id = v_data, model_layout_id = v_model, updater = 'V271', update_time = CURRENT_TIMESTAMP
        WHERE tenant_id = 123 AND code = 'point' AND deleted = false;
    END IF;
END $$;

DROP FUNCTION dynamicbusiness.v268_clone_layout(bigint, text, bigint, text, text, text, text);
