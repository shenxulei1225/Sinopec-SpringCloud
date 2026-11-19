-- 同一种器材只建一份。数量取各预案清单里的最大数，不把各预案的数量加在一起。
-- 一份预案写得少，就只把前若干件挂到这份预案上。防恐头盔 10、防盗头盔 20，一共 20 件，前 10 件两份预案都挂。
-- 防恐预案的警戒带写的是 80 米，其它预案是 15 卷。按同一批 15 卷挂到防恐，不再另建一条。
-- 车辆放在停车场。对讲机放在监控中心办公室。预案里没有写出房间的器材不编存放点。
-- 按预案起名的存放点作废。

DO $$
DECLARE
    v_point_model bigint;
    v_park bigint;
    v_office bigint;
    v_model record;
    v_model_id bigint;
    v_name text;
    v_point bigint;
    v_entity bigint;
    v_n int;
BEGIN
    IF EXISTS (
        SELECT 1 FROM dynamicbusiness.ent_emergency_resource_t123
        WHERE tenant_id = 123 AND deleted = false AND creator = 'V274'
    ) THEN
        RETURN;
    END IF;

    UPDATE dynamicbusiness.dynamic_category_entity_link_t123 l
    SET deleted = true, updater = 'V274', update_time = CURRENT_TIMESTAMP
    WHERE l.deleted = false
      AND l.tenant_id = 123
      AND l.entity_id IN (
          SELECT id FROM dynamicbusiness.ent_emergency_resource_t123
          WHERE tenant_id = 123 AND deleted = false AND creator = 'V271'
      );

    UPDATE dynamicbusiness.ent_emergency_resource_t123
    SET deleted = true, updater = 'V274', update_time = CURRENT_TIMESTAMP
    WHERE tenant_id = 123 AND deleted = false AND creator = 'V271';

    UPDATE dynamicbusiness.ent_point_t123
    SET deleted = true, updater = 'V274', update_time = CURRENT_TIMESTAMP
    WHERE tenant_id = 123 AND deleted = false AND creator = 'V271'
      AND code LIKE 'er-point-%';

    SELECT id INTO v_point_model
    FROM dynamicbusiness.dynamic_model
    WHERE tenant_id = 123 AND deleted = false AND code = 'point-emergency-location';

    v_park := nextval('dynamicbusiness.ent_point_id_seq');
    INSERT INTO dynamicbusiness.ent_point_t123 (
        id, tenant_id, entity_type_code, model_id, name, code, status, facility_id,
        creator, updater, deleted, tree_path
    ) VALUES (
        v_park, 123, 'point', v_point_model, '停车场', 'er-point-parking',
        1, 1, 'V274', 'V274', false, '/' || v_park::text || '/'
    );

    v_office := nextval('dynamicbusiness.ent_point_id_seq');
    INSERT INTO dynamicbusiness.ent_point_t123 (
        id, tenant_id, entity_type_code, model_id, name, code, status, facility_id,
        creator, updater, deleted, tree_path
    ) VALUES (
        v_office, 123, 'point', v_point_model, '监控中心办公室', 'er-point-monitor-office',
        1, 1, 'V274', 'V274', false, '/' || v_office::text || '/'
    );

    CREATE TEMP TABLE v274_need (
        model_code text,
        plan_code text,
        copies int,
        unit text
    ) ON COMMIT DROP;

    INSERT INTO v274_need (model_code, plan_code, copies, unit) VALUES
        ('er-type-01', 'er-plan-confined', 10, '个'),
        ('er-type-01', 'er-plan-fire', 10, '个'),
        ('er-type-02', 'er-plan-confined', 2, '套'),
        ('er-type-02', 'er-plan-fire', 2, '套'),
        ('er-type-03', 'er-plan-confined', 12, '双'),
        ('er-type-03', 'er-plan-fire', 12, '双'),
        ('er-type-04', 'er-plan-confined', 2, '个'),
        ('er-type-04', 'er-plan-fire', 2, '个'),
        ('er-type-05', 'er-plan-confined', 10, '个'),
        ('er-type-05', 'er-plan-fire', 10, '个'),
        ('er-type-06', 'er-plan-burst', 18, '把'),
        ('er-type-06', 'er-plan-confined', 10, '把'),
        ('er-type-06', 'er-plan-fire', 18, '把'),
        ('er-type-06', 'er-plan-flood', 10, '把'),
        ('er-type-06', 'er-plan-quake', 18, '把'),
        ('er-type-07', 'er-plan-burst', 1, '辆'),
        ('er-type-07', 'er-plan-confined', 1, '辆'),
        ('er-type-07', 'er-plan-fire', 1, '辆'),
        ('er-type-07', 'er-plan-quake', 1, '辆'),
        ('er-type-07', 'er-plan-shock', 1, '辆'),
        ('er-type-07', 'er-plan-terror', 1, '辆'),
        ('er-type-07', 'er-plan-theft', 1, '辆'),
        ('er-type-07', 'er-plan-traffic', 1, '辆'),
        ('er-type-08', 'er-plan-confined', 10, '部'),
        ('er-type-08', 'er-plan-fire', 10, '部'),
        ('er-type-08', 'er-plan-terror', 10, '部'),
        ('er-type-08', 'er-plan-theft', 10, '部'),
        ('er-type-09', 'er-plan-burst', 1, '具'),
        ('er-type-09', 'er-plan-confined', 1, '具'),
        ('er-type-09', 'er-plan-fire', 1, '具'),
        ('er-type-09', 'er-plan-flood', 1, '具'),
        ('er-type-09', 'er-plan-quake', 1, '具'),
        ('er-type-09', 'er-plan-shock', 1, '具'),
        ('er-type-09', 'er-plan-theft', 1, '具'),
        ('er-type-09', 'er-plan-traffic', 1, '具'),
        ('er-type-10', 'er-plan-burst', 1, '台'),
        ('er-type-10', 'er-plan-confined', 1, '台'),
        ('er-type-10', 'er-plan-fire', 1, '台'),
        ('er-type-10', 'er-plan-flood', 1, '台'),
        ('er-type-10', 'er-plan-quake', 1, '台'),
        ('er-type-11', 'er-plan-burst', 1, '个'),
        ('er-type-11', 'er-plan-confined', 1, '个'),
        ('er-type-11', 'er-plan-fire', 1, '个'),
        ('er-type-11', 'er-plan-flood', 1, '个'),
        ('er-type-11', 'er-plan-quake', 1, '个'),
        ('er-type-11', 'er-plan-shock', 1, '个'),
        ('er-type-11', 'er-plan-terror', 3, '个'),
        ('er-type-11', 'er-plan-theft', 1, '个'),
        ('er-type-11', 'er-plan-traffic', 1, '个'),
        ('er-type-12', 'er-plan-burst', 15, '卷'),
        ('er-type-12', 'er-plan-confined', 15, '卷'),
        ('er-type-12', 'er-plan-fire', 15, '卷'),
        ('er-type-12', 'er-plan-flood', 15, '卷'),
        ('er-type-12', 'er-plan-quake', 15, '卷'),
        ('er-type-12', 'er-plan-shock', 15, '卷'),
        ('er-type-12', 'er-plan-terror', 15, '卷'),
        ('er-type-12', 'er-plan-theft', 15, '卷'),
        ('er-type-12', 'er-plan-traffic', 15, '卷'),
        ('er-type-13', 'er-plan-burst', 10, '个'),
        ('er-type-13', 'er-plan-confined', 10, '个'),
        ('er-type-13', 'er-plan-fire', 10, '个'),
        ('er-type-13', 'er-plan-flood', 10, '个'),
        ('er-type-13', 'er-plan-quake', 10, '个'),
        ('er-type-13', 'er-plan-shock', 10, '个'),
        ('er-type-13', 'er-plan-terror', 10, '个'),
        ('er-type-13', 'er-plan-theft', 10, '个'),
        ('er-type-13', 'er-plan-traffic', 10, '个'),
        ('er-type-14', 'er-plan-burst', 1, '台'),
        ('er-type-14', 'er-plan-confined', 1, '台'),
        ('er-type-14', 'er-plan-fire', 1, '台'),
        ('er-type-14', 'er-plan-flood', 1, '台'),
        ('er-type-15', 'er-plan-confined', 1, '条'),
        ('er-type-15', 'er-plan-shock', 1, '条'),
        ('er-type-15', 'er-plan-theft', 1, '条'),
        ('er-type-15', 'er-plan-traffic', 1, '条'),
        ('er-type-16', 'er-plan-confined', 2, '个'),
        ('er-type-17', 'er-plan-burst', 2, '台'),
        ('er-type-17', 'er-plan-flood', 2, '台'),
        ('er-type-17', 'er-plan-quake', 2, '台'),
        ('er-type-18', 'er-plan-flood', 21, '套'),
        ('er-type-19', 'er-plan-burst', 200, '只'),
        ('er-type-19', 'er-plan-flood', 200, '只'),
        ('er-type-19', 'er-plan-quake', 200, '只'),
        ('er-type-20', 'er-plan-burst', 10, '个'),
        ('er-type-20', 'er-plan-flood', 10, '个'),
        ('er-type-20', 'er-plan-quake', 10, '个'),
        ('er-type-21', 'er-plan-burst', 10, '部'),
        ('er-type-21', 'er-plan-flood', 10, '部'),
        ('er-type-21', 'er-plan-quake', 10, '部'),
        ('er-type-21', 'er-plan-shock', 10, '部'),
        ('er-type-21', 'er-plan-traffic', 10, '部'),
        ('er-type-22', 'er-plan-burst', 1, '辆'),
        ('er-type-22', 'er-plan-flood', 1, '辆'),
        ('er-type-22', 'er-plan-quake', 1, '辆'),
        ('er-type-23', 'er-plan-burst', 2, '件'),
        ('er-type-23', 'er-plan-flood', 2, '件'),
        ('er-type-24', 'er-plan-flood', 12, '双'),
        ('er-type-25', 'er-plan-flood', 2, '个'),
        ('er-type-26', 'er-plan-terror', 10, '个'),
        ('er-type-26', 'er-plan-theft', 20, '个'),
        ('er-type-27', 'er-plan-burst', 21, '套'),
        ('er-type-27', 'er-plan-quake', 21, '套'),
        ('er-type-28', 'er-plan-shock', 2, '个'),
        ('er-type-28', 'er-plan-traffic', 2, '个'),
        ('er-type-29', 'er-plan-shock', 10, '个'),
        ('er-type-29', 'er-plan-traffic', 10, '个'),
        ('er-type-30', 'er-plan-shock', 2, '双'),
        ('er-type-30', 'er-plan-traffic', 2, '双'),
        ('er-type-31', 'er-plan-shock', 10, '双'),
        ('er-type-31', 'er-plan-traffic', 10, '双'),
        ('er-type-32', 'er-plan-shock', 2, '个'),
        ('er-type-32', 'er-plan-traffic', 2, '个'),
        ('er-type-33', 'er-plan-shock', 10, '个'),
        ('er-type-33', 'er-plan-traffic', 10, '个'),
        ('er-type-34', 'er-plan-theft', 2, '个'),
        ('er-type-35', 'er-plan-theft', 2, '个'),
        ('er-type-36', 'er-plan-theft', 2, '个'),
        ('er-type-37', 'er-plan-theft', 2, '个'),
        ('er-type-38', 'er-plan-theft', 2, '双'),
        ('er-type-39', 'er-plan-theft', 2, '个'),
        ('er-type-40', 'er-plan-theft', 2, '个');

    FOR v_model IN
        SELECT model_code, max(copies) AS copies,
               (array_agg(unit ORDER BY copies DESC, unit))[1] AS unit
        FROM v274_need
        GROUP BY model_code
    LOOP
        SELECT id, name INTO v_model_id, v_name
        FROM dynamicbusiness.dynamic_model
        WHERE tenant_id = 123 AND deleted = false AND code = v_model.model_code;

        v_point := CASE
            WHEN v_model.model_code IN ('er-type-07', 'er-type-22') THEN v_park
            WHEN v_model.model_code IN ('er-type-21', 'er-type-08') THEN v_office
            ELSE NULL
        END;

        FOR v_n IN 1..v_model.copies LOOP
            v_entity := nextval('dynamicbusiness.ent_emergency_resource_id_seq');
            INSERT INTO dynamicbusiness.ent_emergency_resource_t123 (
                id, tenant_id, entity_type_code, model_id, name, code, status,
                facility_id, location_point_id, total_quantity, available_quantity,
                custom_fields, creator, updater, deleted, tree_path
            ) VALUES (
                v_entity, 123, 'emergency_resource', v_model_id, v_name,
                'er-' || v_model.model_code || '-' || v_n::text,
                1, 1, v_point, 1, 1,
                jsonb_build_object('unit', v_model.unit),
                'V274', 'V274', false, '/' || v_entity::text || '/'
            );

            INSERT INTO dynamicbusiness.dynamic_category_entity_link_t123 (
                category_id, entity_id, entity_model_id, creator, tenant_id, entity_type_code
            )
            SELECT c.id, v_entity, v_model_id, 'V274', 123, 'emergency_resource'
            FROM v274_need n
            JOIN dynamicbusiness.dynamic_category c
              ON c.tenant_id = 123 AND c.deleted = false AND c.code = n.plan_code
            WHERE n.model_code = v_model.model_code
              AND n.copies >= v_n;
        END LOOP;
    END LOOP;
END $$;
