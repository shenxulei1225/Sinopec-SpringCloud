-- 一次性修数据：补回被误删的多选引用关联行（租户 1）。
--
-- 起因：2026-11-17 删除组织机构目录里编号 1–67 的记录时，删除路径只按记录编号删关联表，
-- 没带目录编码，把别的目录里同编号记录发出的关联、指向同编号记录的关联一起删了。
-- 代码已改成按「编号 + 同一张表的目录编码」删（EntityRelationMapper#deleteBySourceEntity / deleteByTargetEntity）。
--
-- 多选引用以关联表为准，记录的扩展字段里仍留着同一组值，本迁移按扩展字段把缺的行补回：
--   1. 场站「所属管线段」pipeline_segment_ids → 管线目录
--   2. 设备「所属分区」zone_id → 分区目录
--   3. 订阅「订阅的主题」F-9e940cdd2690487b8144bc8a14a812a3 → 主题目录
-- 只补目标记录仍存在、且关联表里还没有的行，可重复执行。
-- 不处理：设备 dji_mounted_payloads 也缺关联行，但那批设备编号都大于 67，不是这次删除造成的，另行核实。
SET search_path TO dynamicbusiness;

DO $$
DECLARE
    added integer;
BEGIN
    -- 1) 场站所属管线段
    IF to_regclass('ent_facility_t1') IS NULL OR to_regclass('ent_pipeline_t1') IS NULL THEN
        RAISE NOTICE 'V229: 没有场站所属管线段的表，跳过';
    ELSE
        INSERT INTO dynamic_entity_relation_t1 (source_entity_id, target_entity_id, relation_type, relation_name,
                                                status, field_code, source_model_code, source_entity_type_code,
                                                target_entity_type_code, creator, updater, tenant_id)
        SELECT DISTINCT e.id, (x->>'id')::bigint, 'ENTITY_REF_MULTI', f.name, 1, f.code, m.code,
               m.entity_type_code, 'pipeline', 'V229', 'V229', 1
        FROM ent_facility_t1 e
        JOIN dynamic_model m ON m.id = e.model_id
        JOIN dynamic_field f ON f.tenant_id = 1 AND f.deleted = false AND f.code = 'pipeline_segment_ids'
        CROSS JOIN LATERAL jsonb_array_elements(e.custom_fields->'pipeline_segment_ids') x
        WHERE e.deleted = false AND jsonb_typeof(e.custom_fields->'pipeline_segment_ids') = 'array'
          AND EXISTS (SELECT 1 FROM ent_pipeline_t1 t WHERE t.id = (x->>'id')::bigint AND t.deleted = false)
          AND NOT EXISTS (SELECT 1 FROM dynamic_entity_relation_t1 r
                          WHERE r.deleted = false AND r.source_entity_id = e.id
                            AND r.source_entity_type_code = m.entity_type_code
                            AND r.field_code = f.code AND r.target_entity_id = (x->>'id')::bigint);
        GET DIAGNOSTICS added = ROW_COUNT;
        RAISE NOTICE 'V229: 场站所属管线段补回 % 行', added;
    END IF;

    -- 2) 设备所属分区
    IF to_regclass('ent_equipment_t1') IS NULL OR to_regclass('ent_zone_t1') IS NULL THEN
        RAISE NOTICE 'V229: 没有设备所属分区的表，跳过';
    ELSE
        INSERT INTO dynamic_entity_relation_t1 (source_entity_id, target_entity_id, relation_type, relation_name,
                                                status, field_code, source_model_code, source_entity_type_code,
                                                target_entity_type_code, creator, updater, tenant_id)
        SELECT DISTINCT e.id, (x->>'id')::bigint, 'ENTITY_REF_MULTI', f.name, 1, f.code, m.code,
               m.entity_type_code, 'zone', 'V229', 'V229', 1
        FROM ent_equipment_t1 e
        JOIN dynamic_model m ON m.id = e.model_id
        JOIN dynamic_field f ON f.tenant_id = 1 AND f.deleted = false AND f.code = 'zone_id'
        CROSS JOIN LATERAL jsonb_array_elements(e.custom_fields->'zone_id') x
        WHERE e.deleted = false AND jsonb_typeof(e.custom_fields->'zone_id') = 'array'
          AND EXISTS (SELECT 1 FROM ent_zone_t1 t WHERE t.id = (x->>'id')::bigint AND t.deleted = false)
          AND NOT EXISTS (SELECT 1 FROM dynamic_entity_relation_t1 r
                          WHERE r.deleted = false AND r.source_entity_id = e.id
                            AND r.source_entity_type_code = m.entity_type_code
                            AND r.field_code = f.code AND r.target_entity_id = (x->>'id')::bigint);
        GET DIAGNOSTICS added = ROW_COUNT;
        RAISE NOTICE 'V229: 设备所属分区补回 % 行', added;
    END IF;

    -- 3) 订阅的主题
    IF to_regclass('ent_push_subscription_t1') IS NULL OR to_regclass('ent_push_topic_t1') IS NULL THEN
        RAISE NOTICE 'V229: 没有订阅的主题的表，跳过';
    ELSE
        INSERT INTO dynamic_entity_relation_t1 (source_entity_id, target_entity_id, relation_type, relation_name,
                                                status, field_code, source_model_code, source_entity_type_code,
                                                target_entity_type_code, creator, updater, tenant_id)
        SELECT DISTINCT e.id, (x->>'id')::bigint, 'ENTITY_REF_MULTI', f.name, 1, f.code, m.code,
               m.entity_type_code, 'event_topic', 'V229', 'V229', 1
        FROM ent_push_subscription_t1 e
        JOIN dynamic_model m ON m.id = e.model_id
        JOIN dynamic_field f ON f.tenant_id = 1 AND f.deleted = false
                            AND f.code = 'F-9e940cdd2690487b8144bc8a14a812a3'
        CROSS JOIN LATERAL jsonb_array_elements(e.custom_fields->'F-9e940cdd2690487b8144bc8a14a812a3') x
        WHERE e.deleted = false AND jsonb_typeof(e.custom_fields->'F-9e940cdd2690487b8144bc8a14a812a3') = 'array'
          AND EXISTS (SELECT 1 FROM ent_push_topic_t1 t WHERE t.id = (x->>'id')::bigint AND t.deleted = false)
          AND NOT EXISTS (SELECT 1 FROM dynamic_entity_relation_t1 r
                          WHERE r.deleted = false AND r.source_entity_id = e.id
                            AND r.source_entity_type_code = m.entity_type_code
                            AND r.field_code = f.code AND r.target_entity_id = (x->>'id')::bigint);
        GET DIAGNOSTICS added = ROW_COUNT;
        RAISE NOTICE 'V229: 订阅的主题补回 % 行', added;
    END IF;
END $$;
