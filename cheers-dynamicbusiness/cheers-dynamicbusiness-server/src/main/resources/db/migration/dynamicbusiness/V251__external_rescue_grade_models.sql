-- 外部救援资源的型号按应急时要区分的等级建。
-- 医院是三甲医院、二甲医院。消防是消防大队、消防中队。公安是公安局、派出所。
-- 有站的管线段上，每个等级各有一条记录。属地医院、属地消防救援站、属地公安不再保留。

DO $$
DECLARE
    v_grade record;
    v_model bigint;
    v_category bigint;
BEGIN
    CREATE TEMP TABLE v251_grade (
        old_name text,
        new_name text,
        new_code text,
        category_code text,
        source_code text,
        lon_delta numeric,
        lat_delta numeric
    ) ON COMMIT DROP;

    INSERT INTO v251_grade (old_name, new_name, new_code, category_code, source_code, lon_delta, lat_delta) VALUES
        ('属地医院', '三甲医院', 'ex-hospital-3a', 'ex-hospital', 'ex-hospital', 0.004, 0.002),
        ('属地医院', '二甲医院', 'ex-hospital-2a', 'ex-hospital', 'ex-hospital', -0.004, -0.002),
        ('属地消防救援站', '消防大队', 'ex-fire-dadui', 'ex-fire', 'ex-fire', 0.004, 0.002),
        ('属地消防救援站', '消防中队', 'ex-fire-zhongdui', 'ex-fire', 'ex-fire', -0.004, -0.002),
        ('属地公安', '公安局', 'ex-police-bureau', 'ex-police', 'ex-police', 0.004, 0.002),
        ('属地公安', '派出所', 'ex-police-station', 'ex-police', 'ex-police', -0.004, -0.002);

    PERFORM setval(
        'dynamicbusiness.ent_external_rescue_resource_t1_id_seq',
        (SELECT COALESCE(MAX(id), 1) FROM dynamicbusiness.ent_external_rescue_resource_t1)
    );

    FOR v_grade IN SELECT * FROM v251_grade LOOP
        SELECT id INTO v_category
        FROM dynamicbusiness.dynamic_category
        WHERE code = v_grade.category_code AND tenant_id = 1 AND deleted = false
        ORDER BY id LIMIT 1;

        SELECT id INTO v_model
        FROM dynamicbusiness.dynamic_model
        WHERE code = v_grade.new_code AND tenant_id = 1 AND deleted = false;

        IF v_model IS NULL THEN
            INSERT INTO dynamicbusiness.dynamic_model (
                code, name, entity_type_code, status, sort, creator, tenant_id, deleted
            ) VALUES (
                v_grade.new_code, v_grade.new_name, 'external_rescue_resource', 1, 0,
                'external-rescue-grades', 1, false
            )
            RETURNING id INTO v_model;
        END IF;

        INSERT INTO dynamicbusiness.dynamic_model_category_relation (
            model_id, category_id, entity_type_code, sort, creator, tenant_id, model_code, category_code
        )
        SELECT v_model, v_category, 'external_rescue_resource', 0, 'external-rescue-grades', 1,
               v_grade.new_code, v_grade.category_code
        WHERE v_category IS NOT NULL
          AND NOT EXISTS (
              SELECT 1 FROM dynamicbusiness.dynamic_model_category_relation r
              WHERE r.model_id = v_model AND r.category_id = v_category AND r.deleted = false
          );

        INSERT INTO dynamicbusiness.dynamic_model_field_assignment (
            model_id, field_id, required, sort, creator, tenant_id, model_code, field_code
        )
        SELECT v_model, a.field_id, a.required, a.sort, 'external-rescue-grades', 1,
               v_grade.new_code, a.field_code
        FROM dynamicbusiness.dynamic_model_field_assignment a
        WHERE a.model_code = v_grade.source_code AND a.tenant_id = 1 AND a.deleted = false
          AND NOT EXISTS (
              SELECT 1 FROM dynamicbusiness.dynamic_model_field_assignment x
              WHERE x.model_code = v_grade.new_code AND x.field_code = a.field_code
                AND x.tenant_id = 1 AND x.deleted = false
          );

        INSERT INTO dynamicbusiness.ent_external_rescue_resource_t1 (
            tenant_id, entity_type_code, model_id, name, code, status,
            pipeline_segment_id, longitude, latitude, creator, deleted
        )
        SELECT 1, 'external_rescue_resource', v_model, v_grade.new_name,
               'ext-' || e.pipeline_segment_id || '-' || v_grade.new_code,
               1, e.pipeline_segment_id,
               CASE WHEN e.longitude IS NULL THEN NULL ELSE e.longitude + v_grade.lon_delta END,
               CASE WHEN e.latitude IS NULL THEN NULL ELSE e.latitude + v_grade.lat_delta END,
               'external-rescue-grades', false
        FROM dynamicbusiness.ent_external_rescue_resource_t1 e
        WHERE e.tenant_id = 1 AND e.deleted = false AND e.name = v_grade.old_name
          AND NOT EXISTS (
              SELECT 1 FROM dynamicbusiness.ent_external_rescue_resource_t1 x
              WHERE x.tenant_id = 1 AND x.deleted = false
                AND x.pipeline_segment_id = e.pipeline_segment_id
                AND x.name = v_grade.new_name
          );
    END LOOP;

    UPDATE dynamicbusiness.ent_external_rescue_resource_t1
    SET deleted = true, updater = 'external-rescue-grades', update_time = CURRENT_TIMESTAMP
    WHERE tenant_id = 1 AND deleted = false
      AND name IN ('属地医院', '属地消防救援站', '属地公安');

    UPDATE dynamicbusiness.dynamic_model m
    SET deleted = true, status = 0, updater = 'external-rescue-grades', update_time = CURRENT_TIMESTAMP
    WHERE m.tenant_id = 1 AND m.deleted = false
      AND m.code IN ('ex-hospital', 'ex-fire', 'ex-police')
      AND NOT EXISTS (
          SELECT 1 FROM dynamicbusiness.ent_external_rescue_resource_t1 e
          WHERE e.model_id = m.id AND e.deleted = false
      );

    UPDATE dynamicbusiness.dynamic_model_field_assignment a
    SET deleted = true, updater = 'external-rescue-grades', update_time = CURRENT_TIMESTAMP
    WHERE a.tenant_id = 1 AND a.deleted = false
      AND a.model_code IN ('ex-hospital', 'ex-fire', 'ex-police');

    UPDATE dynamicbusiness.dynamic_model_category_relation r
    SET deleted = true, updater = 'external-rescue-grades', update_time = CURRENT_TIMESTAMP
    WHERE r.tenant_id = 1 AND r.deleted = false
      AND r.model_code IN ('ex-hospital', 'ex-fire', 'ex-police');
END $$;
