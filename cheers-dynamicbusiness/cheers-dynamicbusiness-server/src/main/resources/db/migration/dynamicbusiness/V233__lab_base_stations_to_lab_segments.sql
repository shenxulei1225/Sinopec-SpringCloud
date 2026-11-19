-- 一次性修数据：实验基地三座站改挂到各自基地的管线段（租户 1）。
--
-- 起因：东营油库、金桥厂区、洛阳圣瑞挂在管线「实验基地」上，实验基地不属于任何管理单元。
-- 场站按「所属管线段 → 所属作业区」找管理单元，链在这里断了，三座站谁都打不开，界面上改不回来。
-- 组织机构已经接口建好实验基地下的金桥、洛阳、东营三个单元，实验基地下也已建好同名三条管线段，并填好所属单元。
-- 本迁移只把三座站的所属管线段从实验基地改成对应的段。所属管线仍是实验基地，不变。
--
-- 多选引用以关联表为准，扩展字段里留着同一组值，两处一起改。
-- 只在三条段都存在、且场站仍挂在实验基地时改，可重复执行。
SET search_path TO dynamicbusiness;

DO $$
DECLARE
    lab_id bigint;
    moved integer;
    station record;
BEGIN
    IF to_regclass('ent_facility_t1') IS NULL OR to_regclass('ent_pipeline_t1') IS NULL
        OR to_regclass('dynamic_entity_relation_t1') IS NULL THEN
        RAISE NOTICE 'V233: 租户 1 没有场站或管线表，跳过';
        RETURN;
    END IF;

    SELECT id INTO lab_id FROM ent_pipeline_t1 WHERE deleted = false AND code = 'PIPE-LAB-BASE';
    IF lab_id IS NULL THEN
        RAISE NOTICE 'V233: 没有实验基地管线，跳过';
        RETURN;
    END IF;

    FOR station IN
        SELECT f.id AS facility_id, s.id AS segment_id
        FROM (VALUES ('FAC-JINQIAO', 'PIPE-LAB-JQ'),
                     ('FAC-LUOYANG-SHENGRUI', 'PIPE-LAB-LY'),
                     ('FAC-DEPOT-001', 'PIPE-LAB-DY')) AS v(facility_code, segment_code)
        JOIN ent_facility_t1 f ON f.deleted = false AND f.code = v.facility_code
        JOIN ent_pipeline_t1 s ON s.deleted = false AND s.code = v.segment_code AND s.parent_id = lab_id
    LOOP
        UPDATE dynamic_entity_relation_t1
        SET target_entity_id = station.segment_id, updater = 'V233', update_time = CURRENT_TIMESTAMP
        WHERE deleted = false AND source_entity_type_code = 'facility' AND source_entity_id = station.facility_id
          AND field_code = 'pipeline_segment_ids' AND target_entity_id = lab_id;
        GET DIAGNOSTICS moved = ROW_COUNT;

        UPDATE ent_facility_t1
        SET custom_fields = jsonb_set(custom_fields, '{pipeline_segment_ids}',
                                      jsonb_build_array(jsonb_build_object('id', station.segment_id,
                                                                           'entityTypeCode', 'pipeline'))),
            updater = 'V233', update_time = CURRENT_TIMESTAMP
        WHERE id = station.facility_id
          AND custom_fields->'pipeline_segment_ids' = jsonb_build_array(
                  jsonb_build_object('id', lab_id, 'entityTypeCode', 'pipeline'));

        RAISE NOTICE 'V233: 场站 % 改挂到管线段 %，关联行 % 条', station.facility_id, station.segment_id, moved;
    END LOOP;
END $$;
