-- 外部救援资源：型号是等级和建制，实体是具体单位名称。
-- 删掉用型号名充当作实体的记录。按公开资料写入医院、消防队和公安单位，一条单位归一段。

DO $$
DECLARE
    v_item record;
    v_segment bigint;
    v_model bigint;
BEGIN
    PERFORM setval(
        'dynamicbusiness.ent_external_rescue_resource_t1_id_seq',
        (SELECT COALESCE(MAX(id), 1) FROM dynamicbusiness.ent_external_rescue_resource_t1)
    );

    UPDATE dynamicbusiness.ent_external_rescue_resource_t1
    SET deleted = true, updater = 'external-rescue-named', update_time = CURRENT_TIMESTAMP
    WHERE tenant_id = 1 AND deleted = false
      AND name IN ('三甲医院', '二甲医院', '消防大队', '消防中队', '公安局', '派出所');

    CREATE TEMP TABLE v252_unit (
        segment_name text,
        model_code text,
        unit_name text,
        phone text
    ) ON COMMIT DROP;

    INSERT INTO v252_unit (segment_name, model_code, unit_name, phone) VALUES
        ('莆福', 'ex-hospital-3a', '福建医科大学附属协和医院', NULL),
        ('莆福', 'ex-hospital-3a', '福清市医院', NULL),
        ('莆福', 'ex-hospital-2a', '福建省级机关医院', NULL),
        ('福鼎', 'ex-hospital-3a', '宁德市闽东医院', NULL),
        ('福鼎', 'ex-hospital-3a', '宁德师范学院附属宁德市医院', NULL),
        ('福鼎', 'ex-hospital-2a', '连江县医院', NULL),
        ('福鼎', 'ex-hospital-2a', '罗源县医院', NULL),
        ('厦莆', 'ex-hospital-3a', '莆田市第一医院', NULL),
        ('厦莆', 'ex-hospital-3a', '莆田学院附属医院', NULL),
        ('厦莆', 'ex-hospital-3a', '泉州市第一医院', NULL),
        ('厦莆', 'ex-hospital-3a', '福建医科大学附属第二医院', NULL),
        ('厦莆', 'ex-hospital-2a', '仙游县医院', NULL),
        ('南线', 'ex-hospital-3a', '厦门大学附属第一医院', NULL),
        ('南线', 'ex-hospital-3a', '厦门大学附属中山医院', NULL),
        ('漳厦', 'ex-hospital-3a', '复旦大学附属中山医院厦门医院', NULL),
        ('漳厦', 'ex-hospital-2a', '厦门市同安区中医医院', NULL),
        ('龙漳', 'ex-hospital-3a', '漳州市医院', NULL),
        ('龙漳', 'ex-hospital-2a', '漳州市人民医院', NULL),
        ('漳龙', 'ex-hospital-3a', '龙岩市第一医院', NULL),
        ('漳龙', 'ex-hospital-2a', '南靖县医院', NULL),
        ('汀龙', 'ex-hospital-3a', '龙岩市第二医院', NULL),
        ('汀龙', 'ex-hospital-2a', '长汀县汀洲医院', NULL),
        ('漳诏', 'ex-hospital-2a', '诏安县医院', NULL),
        ('漳程', 'ex-hospital-2a', '漳州市芗城区妇幼保健院', NULL),
        ('水德', 'ex-hospital-3a', '南安市医院', NULL),
        ('水德', 'ex-hospital-2a', '德化县医院', NULL),
        ('水德', 'ex-hospital-2a', '永春县医院', NULL),
        ('北线', 'ex-hospital-2a', '泉州市泉港区医院', NULL),
        ('金桥', 'ex-hospital-3a', '徐州市中心医院', NULL),
        ('金桥', 'ex-hospital-3a', '徐州医科大学附属医院', NULL),
        ('金桥', 'ex-hospital-2a', '徐州市第六人民医院', NULL),
        ('洛阳', 'ex-hospital-3a', '河南科技大学第一附属医院', NULL),
        ('洛阳', 'ex-hospital-3a', '洛阳市中心医院', NULL),
        ('洛阳', 'ex-hospital-2a', '洛阳市第六人民医院', NULL),
        ('东营', 'ex-hospital-3a', '东营市人民医院', NULL),
        ('东营', 'ex-hospital-3a', '胜利油田中心医院', NULL),
        ('东营', 'ex-hospital-2a', '东营市东营区人民医院', NULL),
        ('莆福', 'ex-fire-dadui', '福清市消防救援大队', NULL),
        ('福鼎', 'ex-fire-dadui', '连江县消防救援大队', NULL),
        ('福鼎', 'ex-fire-dadui', '罗源县消防救援大队', NULL),
        ('水德', 'ex-fire-dadui', '南安市消防救援大队', NULL),
        ('水德', 'ex-fire-zhongdui', '水头消防救援站', NULL),
        ('水德', 'ex-fire-zhongdui', '美林消防救援站', NULL),
        ('水德', 'ex-fire-zhongdui', '梅山消防救援站', NULL),
        ('漳诏', 'ex-fire-dadui', '诏安县消防救援大队', NULL),
        ('北线', 'ex-fire-dadui', '泉州市泉港石化工业区消防大队', NULL),
        ('莆福', 'ex-police-bureau', '福清市公安局', '0591-85195114'),
        ('水德', 'ex-police-bureau', '南安市公安局', NULL),
        ('北线', 'ex-police-bureau', '泉州市公安局泉港分局', NULL),
        ('厦莆', 'ex-police-station', '南安市公安局东田派出所', '0595-86210103'),
        ('水德', 'ex-police-station', '南安市公安局水头派出所', '0595-86987316'),
        ('北线', 'ex-police-station', '泉州市公安局山腰派出所', '0595-87983110');

    FOR v_item IN SELECT * FROM v252_unit LOOP
        SELECT id INTO v_segment
        FROM dynamicbusiness.ent_pipeline_t1
        WHERE name = v_item.segment_name AND deleted = false AND coalesce(parent_id, 0) > 0
        ORDER BY id LIMIT 1;
        SELECT id INTO v_model
        FROM dynamicbusiness.dynamic_model
        WHERE code = v_item.model_code AND tenant_id = 1 AND deleted = false
        ORDER BY id LIMIT 1;
        IF v_segment IS NULL OR v_model IS NULL THEN
            RAISE EXCEPTION '外部救援单位对不上管线段或型号: % / %', v_item.segment_name, v_item.model_code;
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
    END LOOP;
END $$;
