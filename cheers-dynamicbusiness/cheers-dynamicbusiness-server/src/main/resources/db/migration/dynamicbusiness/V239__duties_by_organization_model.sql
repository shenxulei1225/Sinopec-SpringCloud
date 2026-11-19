-- 一次性修数据（租户 1）：按组织类型补全职务，并写上适用的组织类型。
--
-- 职务仍是全租户一张清单。点某个组织时，页面只列 custom_fields.applicableOrgModelIds
-- 含这个组织型号的职务。集团、省公司、分公司用公司级职务；作业区、实验基地用现场职务。
-- 可重复执行：同名已有则不插；适用组织类型按名称重写。
SET search_path TO dynamicbusiness;

DO $$
DECLARE
    duty_model_id bigint;
    company_ids jsonb;
    area_ids jsonb;
    base_ids jsonb;
    item record;
BEGIN
    IF to_regclass('ent_duty_post_t1') IS NULL THEN
        RAISE NOTICE 'V239: 没有职务表，跳过';
        RETURN;
    END IF;

    SELECT id INTO duty_model_id
    FROM dynamic_model
    WHERE deleted = false AND entity_type_code = 'duty_post' AND name = '职务'
    ORDER BY id
    LIMIT 1;
    IF duty_model_id IS NULL THEN
        RAISE NOTICE 'V239: 没有职务型号，跳过';
        RETURN;
    END IF;

    SELECT COALESCE(jsonb_agg(id ORDER BY id), '[]'::jsonb) INTO company_ids
    FROM dynamic_model
    WHERE deleted = false AND entity_type_code = 'organization' AND name IN ('集团', '省公司', '分公司');

    SELECT COALESCE(jsonb_agg(id ORDER BY id), '[]'::jsonb) INTO area_ids
    FROM dynamic_model
    WHERE deleted = false AND entity_type_code = 'organization' AND name = '作业区';

    SELECT COALESCE(jsonb_agg(id ORDER BY id), '[]'::jsonb) INTO base_ids
    FROM dynamic_model
    WHERE deleted = false AND entity_type_code = 'organization' AND name = '实验基地';

    FOR item IN
        SELECT * FROM (VALUES
            ('主要负责人', 'org_principal', 1),
            ('总工程师', 'chief_engineer', 2),
            ('作业区副职', 'area_deputy', 15),
            ('基地负责人', 'base_manager', 16),
            ('设备员', 'equipment_officer', 95)
        ) AS t(name, code, sort_no)
    LOOP
        IF EXISTS (SELECT 1 FROM ent_duty_post_t1 WHERE deleted = false AND name = item.name) THEN
            CONTINUE;
        END IF;
        INSERT INTO ent_duty_post_t1 (
            tenant_id, entity_type_code, model_id, domain, name, code,
            parent_id, sort, status, custom_fields,
            creator, create_time, updater, update_time, deleted
        ) VALUES (
            1, 'duty_post', duty_model_id, '', item.name, item.code,
            0, item.sort_no, 1, '{}'::jsonb,
            'V239', CURRENT_TIMESTAMP, 'V239', CURRENT_TIMESTAMP, false
        );
    END LOOP;

    UPDATE ent_duty_post_t1
    SET custom_fields = COALESCE(custom_fields, '{}'::jsonb)
            || jsonb_build_object('applicableOrgModelIds', company_ids),
        updater = 'V239',
        update_time = CURRENT_TIMESTAMP
    WHERE deleted = false
      AND name IN ('主要负责人', '分管领导', '总工程师', '生产运行主管', '安全环保主管', '系统管理员');

    UPDATE ent_duty_post_t1
    SET custom_fields = COALESCE(custom_fields, '{}'::jsonb)
            || jsonb_build_object('applicableOrgModelIds', area_ids),
        updater = 'V239',
        update_time = CURRENT_TIMESTAMP
    WHERE deleted = false
      AND name IN (
          '作业区负责人', '作业区副职', '站长', '副站长', '值班长',
          '运行班长', '维修班长', '巡检员', '调度员', '安全员', '技术员', '设备员'
      );

    -- 实验基地在现场，沿用站上职务，另加基地负责人。站长等已写作业区或公司，这里再并上基地型号。
    UPDATE ent_duty_post_t1 AS duty
    SET custom_fields = jsonb_set(
            COALESCE(duty.custom_fields, '{}'::jsonb),
            '{applicableOrgModelIds}',
            (
                SELECT COALESCE(jsonb_agg(model_id ORDER BY model_id), '[]'::jsonb)
                FROM (
                    SELECT DISTINCT elem::bigint AS model_id
                    FROM jsonb_array_elements_text(
                        COALESCE(duty.custom_fields->'applicableOrgModelIds', '[]'::jsonb) || base_ids
                    ) AS elem
                ) merged
            )
        ),
        updater = 'V239',
        update_time = CURRENT_TIMESTAMP
    WHERE duty.deleted = false
      AND duty.name IN ('基地负责人', '站长', '副站长', '值班长', '安全员', '技术员', '设备员', '系统管理员');

    RAISE NOTICE 'V239: 已按组织类型写上适用职务';
END $$;
