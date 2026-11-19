-- 场站的所属管线段改成多选：交汇处的分输站同时在干线段和支线段上。
-- 权威：docs/中台/能力定义/数据权限能力/数据权限能力（权威）.md 第 3 节「多值只用于共享」。
--
-- 管线段是管线目录（pipeline）里管线的下级节点，多选仍指向管线目录，不建新目录。
-- 一个字段定义只能是单选或多选。管道、构筑物、应急事件的「所属管线段」保持单选，
-- 所以场站换成另一条字段定义：编码 pipeline_segment_ids，中文名仍是「所属管线段」。
--
-- 本迁移做的事（租户 1，一次性；租户 123 不动）：
--   1. 字段库加「所属管线段」多选定义 pipeline_segment_ids，指向管线目录。
--   2. 场站的基础字段、各场站型号上的这一格，改用新定义。
--   3. 每座站专用列里的段，写成多选值（custom_fields）和关系表的多选行；旧的单选关系行作废。
--   4. 删除场站上的专用列 pipeline_segment_id。多选只以关系表为准，不两处并存。
--   5. 场站的归属声明改成「所属管线段（pipeline_segment_ids）→ 管线，必填，多值」。
--   6. 清掉场站型号的表单快照，打开表单时按新定义重建。列表投影由迁移后「按业务类型重建能力」刷新。
SET search_path TO dynamicbusiness;

DO $$
DECLARE
    old_field_id bigint;
    new_field_id bigint;
    moved integer;
BEGIN
    SELECT id INTO old_field_id FROM dynamic_field
    WHERE tenant_id = 1 AND deleted = false AND code = 'pipeline_segment_id';
    IF old_field_id IS NULL THEN
        RAISE NOTICE 'V226: 租户 1 没有所属管线段字段，跳过';
        RETURN;
    END IF;
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'dynamicbusiness' AND table_name = 'ent_facility_t1'
          AND column_name = 'pipeline_segment_id'
    ) THEN
        RAISE NOTICE 'V226: 场站表没有所属管线段专用列，跳过';
        RETURN;
    END IF;

    -- 1) 字段库：多选定义，指向管线目录
    INSERT INTO dynamic_field (code, name, type, description, source, status, provider_code,
                               governance_status, creator, updater, tenant_id)
    VALUES ('pipeline_segment_ids', '所属管线段', 'ENTITY_REF_MULTI',
            '管线目录里的段，可以选多段。交汇处的场站同时在几段上时都选上。',
            'CUSTOM', 1, 'dynamic-entity:pipeline', 'COMPANY', 'V226', 'V226', 1)
    RETURNING id INTO new_field_id;

    -- 2) 场站基础字段与各型号上的这一格
    UPDATE dynamic_entity_type_base_field
    SET field_code = 'pipeline_segment_ids', data_type = 'REF_Multi', library_field_id = new_field_id,
        description = '这座站在哪几个管线段上。', updater = 'V226', update_time = CURRENT_TIMESTAMP
    WHERE tenant_id = 1 AND deleted = false AND entity_type_code = 'facility'
      AND field_code = 'pipeline_segment_id';

    UPDATE dynamic_model_field_assignment a
    SET field_id = new_field_id, field_code = 'pipeline_segment_ids', updater = 'V226',
        update_time = CURRENT_TIMESTAMP
    FROM dynamic_model m
    WHERE a.model_id = m.id AND a.tenant_id = 1 AND a.deleted = false
      AND m.deleted = false AND m.entity_type_code = 'facility' AND a.field_id = old_field_id;

    -- 3) 值：专用列 → 多选值 + 关系表多选行
    UPDATE ent_facility_t1
    SET custom_fields = coalesce(custom_fields, '{}'::jsonb) || jsonb_build_object('pipeline_segment_ids',
            jsonb_build_array(jsonb_build_object('id', pipeline_segment_id, 'entityTypeCode', 'pipeline'))),
        updater = 'V226', update_time = CURRENT_TIMESTAMP
    WHERE deleted = false AND pipeline_segment_id IS NOT NULL;
    GET DIAGNOSTICS moved = ROW_COUNT;

    UPDATE dynamic_entity_relation_t1
    SET deleted = true, updater = 'V226', update_time = CURRENT_TIMESTAMP
    WHERE deleted = false AND source_entity_type_code = 'facility' AND field_code = 'pipeline_segment_id';

    INSERT INTO dynamic_entity_relation_t1 (source_entity_id, target_entity_id, relation_type, relation_name,
                                            status, field_code, source_model_code, source_entity_type_code,
                                            target_entity_type_code, creator, updater, tenant_id)
    SELECT f.id, f.pipeline_segment_id, 'ENTITY_REF_MULTI', '所属管线段', 1, 'pipeline_segment_ids', m.code,
           'facility', 'pipeline', 'V226', 'V226', 1
    FROM ent_facility_t1 f
    JOIN dynamic_model m ON m.id = f.model_id
    WHERE f.deleted = false AND f.pipeline_segment_id IS NOT NULL;

    -- 4) 删除专用列
    ALTER TABLE ent_facility_t1 DROP COLUMN pipeline_segment_id;

    -- 5) 归属声明
    UPDATE dynamic_catalog_ownership
    SET field_code = 'pipeline_segment_ids', multi_valued = true, updater = 'V226',
        update_time = CURRENT_TIMESTAMP
    WHERE tenant_id = 1 AND deleted = false AND registry_code = 'facility'
      AND field_code = 'pipeline_segment_id';

    -- 6) 场站型号表单快照
    DELETE FROM model_crud_form_definition WHERE tenant_id = 1 AND entity_type_code = 'facility';

    RAISE NOTICE 'V226: 场站所属管线段改成多选，搬了 % 座站', moved;
END $$;
