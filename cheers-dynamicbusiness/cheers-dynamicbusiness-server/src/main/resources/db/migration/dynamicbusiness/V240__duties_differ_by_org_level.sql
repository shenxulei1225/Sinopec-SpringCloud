-- 一次性修数据（租户 1）：集团、省公司、分公司各用各的职务，不再共用同一套名称。
--
-- 作业区、实验基地沿用已有现场职务。公司三级里没有任职的通用名（主要负责人、分管领导等）软删。
-- 可重复执行。
SET search_path TO dynamicbusiness;

DO $$
DECLARE
    duty_model_id bigint;
    group_id bigint;
    province_id bigint;
    branch_id bigint;
    item record;
    target_ids jsonb;
BEGIN
    IF to_regclass('ent_duty_post_t1') IS NULL THEN
        RAISE NOTICE 'V240: 没有职务表，跳过';
        RETURN;
    END IF;

    SELECT id INTO duty_model_id
    FROM dynamic_model
    WHERE deleted = false AND entity_type_code = 'duty_post' AND name = '职务'
    ORDER BY id LIMIT 1;
    IF duty_model_id IS NULL THEN
        RAISE NOTICE 'V240: 没有职务型号，跳过';
        RETURN;
    END IF;

    SELECT id INTO group_id FROM dynamic_model
    WHERE deleted = false AND entity_type_code = 'organization' AND name = '集团' ORDER BY id LIMIT 1;
    SELECT id INTO province_id FROM dynamic_model
    WHERE deleted = false AND entity_type_code = 'organization' AND name = '省公司' ORDER BY id LIMIT 1;
    SELECT id INTO branch_id FROM dynamic_model
    WHERE deleted = false AND entity_type_code = 'organization' AND name = '分公司' ORDER BY id LIMIT 1;

    FOR item IN
        SELECT * FROM (VALUES
            ('集团主要负责人', 'group_principal', 1, group_id),
            ('集团分管领导', 'group_division_leader', 2, group_id),
            ('集团生产运行负责人', 'group_production_lead', 4, group_id),
            ('集团安全环保负责人', 'group_hse_lead', 5, group_id),
            ('省公司主要负责人', 'province_principal', 11, province_id),
            ('省公司分管领导', 'province_division_leader', 12, province_id),
            ('省公司生产运行负责人', 'province_production_lead', 13, province_id),
            ('省公司安全环保负责人', 'province_hse_lead', 14, province_id),
            ('分公司主要负责人', 'branch_principal', 21, branch_id),
            ('分公司分管领导', 'branch_division_leader', 22, branch_id),
            ('分公司生产调度负责人', 'branch_dispatch_lead', 23, branch_id),
            ('分公司安全负责人', 'branch_safety_lead', 24, branch_id)
        ) AS t(name, code, sort_no, org_model_id)
    LOOP
        IF item.org_model_id IS NULL THEN
            CONTINUE;
        END IF;
        target_ids := jsonb_build_array(item.org_model_id);
        IF EXISTS (SELECT 1 FROM ent_duty_post_t1 WHERE deleted = false AND name = item.name) THEN
            UPDATE ent_duty_post_t1
            SET custom_fields = COALESCE(custom_fields, '{}'::jsonb)
                    || jsonb_build_object('applicableOrgModelIds', target_ids),
                updater = 'V240',
                update_time = CURRENT_TIMESTAMP
            WHERE deleted = false AND name = item.name;
            CONTINUE;
        END IF;
        INSERT INTO ent_duty_post_t1 (
            tenant_id, entity_type_code, model_id, domain, name, code,
            parent_id, sort, status, custom_fields,
            creator, create_time, updater, update_time, deleted
        ) VALUES (
            1, 'duty_post', duty_model_id, '', item.name, item.code,
            0, item.sort_no, 1,
            jsonb_build_object('applicableOrgModelIds', target_ids),
            'V240', CURRENT_TIMESTAMP, 'V240', CURRENT_TIMESTAMP, false
        );
    END LOOP;

    IF group_id IS NOT NULL THEN
        UPDATE ent_duty_post_t1
        SET custom_fields = COALESCE(custom_fields, '{}'::jsonb)
                || jsonb_build_object('applicableOrgModelIds', jsonb_build_array(group_id)),
            updater = 'V240',
            update_time = CURRENT_TIMESTAMP
        WHERE deleted = false AND name IN ('总工程师', '系统管理员');
    END IF;

    UPDATE ent_duty_post_t1
    SET deleted = true, updater = 'V240', update_time = CURRENT_TIMESTAMP
    WHERE deleted = false
      AND name IN ('主要负责人', '分管领导', '生产运行主管', '安全环保主管')
      AND NOT EXISTS (
          SELECT 1 FROM dynamic_post_assignment pa
          WHERE pa.deleted = false AND pa.duty_post_id = ent_duty_post_t1.id
      );

    RAISE NOTICE 'V240: 公司三级职务已按级别拆开';
END $$;
