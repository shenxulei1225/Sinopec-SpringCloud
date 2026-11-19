-- 给还没有消防、公安实体的管线段补公开资料里的单位。
-- 一条单位只归一段。电话只写政府网页上的值班或办公电话。

DO $$
DECLARE
    v_item record;
    v_segment bigint;
    v_model bigint;
    v_category bigint;
    v_entity bigint;
BEGIN
    PERFORM setval(
        'dynamicbusiness.ent_external_rescue_resource_t1_id_seq',
        (SELECT COALESCE(MAX(id), 1) FROM dynamicbusiness.ent_external_rescue_resource_t1)
    );
    PERFORM setval(
        'dynamicbusiness.dynamic_entity_category_relation_id_seq',
        (SELECT COALESCE(MAX(id), 1) FROM dynamicbusiness.dynamic_entity_category_relation_t1)
    );

    CREATE TEMP TABLE v255_unit (
        segment_name text,
        model_code text,
        category_code text,
        unit_name text,
        phone text
    ) ON COMMIT DROP;

    INSERT INTO v255_unit (segment_name, model_code, category_code, unit_name, phone) VALUES
        ('金桥', 'ex-fire-dadui', 'ex-fire', '泉山区消防救援大队', NULL),
        ('金桥', 'ex-police-bureau', 'ex-police', '徐州市公安局泉山分局', NULL),
        ('洛阳', 'ex-fire-dadui', 'ex-fire', '洛阳市西工区消防救援大队', '0379-63232119'),
        ('洛阳', 'ex-fire-dadui', 'ex-fire', '洛阳市涧西区消防救援大队', NULL),
        ('洛阳', 'ex-fire-zhongdui', 'ex-fire', '唐宫路消防救援站', NULL),
        ('洛阳', 'ex-fire-zhongdui', 'ex-fire', '汉宫路消防救援站', NULL),
        ('洛阳', 'ex-fire-zhongdui', 'ex-fire', '春都路消防救援站', NULL),
        ('洛阳', 'ex-police-bureau', 'ex-police', '洛阳市公安局西工分局', NULL),
        ('洛阳', 'ex-police-bureau', 'ex-police', '洛阳市公安局涧西分局', NULL),
        ('东营', 'ex-fire-dadui', 'ex-fire', '东营市东营区消防救援大队', NULL),
        ('东营', 'ex-police-bureau', 'ex-police', '东营市公安局东营分局', NULL),
        ('南线', 'ex-fire-dadui', 'ex-fire', '厦门市思明区消防救援大队', NULL),
        ('漳厦', 'ex-fire-dadui', 'ex-fire', '厦门市海沧区消防救援大队', NULL),
        ('漳厦', 'ex-fire-dadui', 'ex-fire', '厦门市同安区消防救援大队', NULL),
        ('龙漳', 'ex-fire-dadui', 'ex-fire', '漳州市芗城区消防救援大队', NULL),
        ('漳龙', 'ex-fire-dadui', 'ex-fire', '龙岩市新罗区消防救援大队', NULL),
        ('汀龙', 'ex-fire-dadui', 'ex-fire', '长汀县消防救援大队', NULL),
        ('厦莆', 'ex-fire-dadui', 'ex-fire', '泉州市丰泽区消防救援大队', NULL),
        ('厦莆', 'ex-police-bureau', 'ex-police', '莆田市公安局城厢分局', NULL),
        ('厦莆', 'ex-police-bureau', 'ex-police', '莆田市公安局荔城分局', NULL),
        ('福鼎', 'ex-fire-dadui', 'ex-fire', '福安市消防救援大队', NULL),
        ('福鼎', 'ex-police-bureau', 'ex-police', '罗源县公安局', '0591-26873312'),
        ('福鼎', 'ex-police-bureau', 'ex-police', '连江县公安局', NULL),
        ('漳诏', 'ex-police-bureau', 'ex-police', '诏安县公安局', NULL),
        ('莆福', 'ex-fire-dadui', 'ex-fire', '福州市鼓楼区消防救援大队', NULL);

    FOR v_item IN SELECT * FROM v255_unit LOOP
        SELECT id INTO v_segment
        FROM dynamicbusiness.ent_pipeline_t1
        WHERE name = v_item.segment_name AND deleted = false AND coalesce(parent_id, 0) > 0
        ORDER BY id LIMIT 1;
        SELECT id INTO v_model
        FROM dynamicbusiness.dynamic_model
        WHERE code = v_item.model_code AND tenant_id = 1 AND deleted = false
        ORDER BY id LIMIT 1;
        SELECT id INTO v_category
        FROM dynamicbusiness.dynamic_category
        WHERE code = v_item.category_code AND tenant_id = 1 AND deleted = false
        ORDER BY id LIMIT 1;
        IF v_segment IS NULL OR v_model IS NULL OR v_category IS NULL THEN
            RAISE EXCEPTION '外部救援单位对不上管线段、型号或分类: % / %', v_item.segment_name, v_item.unit_name;
        END IF;

        INSERT INTO dynamicbusiness.ent_external_rescue_resource_t1 (
            tenant_id, entity_type_code, model_id, name, code, status,
            pipeline_segment_id, contact_phone, creator, deleted
        )
        SELECT 1, 'external_rescue_resource', v_model, v_item.unit_name,
               'ext-' || v_segment || '-' || substr(md5(v_item.unit_name), 1, 12),
               1, v_segment, v_item.phone, 'external-rescue-named', false
        WHERE NOT EXISTS (
            SELECT 1 FROM dynamicbusiness.ent_external_rescue_resource_t1 x
            WHERE x.tenant_id = 1 AND x.deleted = false
              AND x.pipeline_segment_id = v_segment
              AND x.name = v_item.unit_name
        );

        SELECT id INTO v_entity
        FROM dynamicbusiness.ent_external_rescue_resource_t1
        WHERE tenant_id = 1 AND deleted = false
          AND pipeline_segment_id = v_segment
          AND name = v_item.unit_name
        ORDER BY id LIMIT 1;

        INSERT INTO dynamicbusiness.dynamic_entity_category_relation_t1 (
            entity_id, category_id, entity_type_code, sort, creator, updater, deleted, tenant_id
        )
        SELECT v_entity, v_category, 'external_rescue_resource', 0, '1', '1', false, 1
        WHERE NOT EXISTS (
            SELECT 1 FROM dynamicbusiness.dynamic_entity_category_relation_t1 r
            WHERE r.deleted = false
              AND r.entity_id = v_entity
              AND r.entity_type_code = 'external_rescue_resource'
              AND r.category_id = v_category
        );
    END LOOP;
END $$;
