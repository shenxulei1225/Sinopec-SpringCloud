-- 一次性修数据（租户 1）：补作业区常用职务进职务目录。
--
-- 起因：目录里原先只有「站长」「系统管理员」。管输作业区还需要负责人、副站长、班组长、巡检员等。
-- 职务全租户一张清单，不按作业区各建一套。副站长与站长同绑「场站管理员」角色；其余职务只建名，角色在角色页再绑。
-- 可重复执行：同名未删记录已存在则跳过。
SET search_path TO dynamicbusiness;

DO $$
DECLARE
    duty_model_id bigint;
    station_admin_role_id bigint;
    inserted_id bigint;
    item record;
BEGIN
    IF to_regclass('ent_duty_post_t1') IS NULL THEN
        RAISE NOTICE 'V238: 没有职务表，跳过';
        RETURN;
    END IF;

    SELECT id INTO duty_model_id
    FROM dynamic_model
    WHERE deleted = false
      AND entity_type_code = 'duty_post'
      AND name = '职务'
    ORDER BY id
    LIMIT 1;

    IF duty_model_id IS NULL THEN
        RAISE NOTICE 'V238: 没有职务型号，跳过';
        RETURN;
    END IF;

    SELECT r.id INTO station_admin_role_id
    FROM public.system_role r
    WHERE r.deleted = false
      AND r.code = 'station_admin'
      AND r.name = '场站管理员'
    ORDER BY r.id DESC
    LIMIT 1;

    FOR item IN
        SELECT * FROM (VALUES
            ('作业区负责人', 'area_manager', 10),
            ('副站长', 'deputy_station_master', 20),
            ('值班长', 'shift_leader', 30),
            ('运行班长', 'ops_team_leader', 40),
            ('维修班长', 'maint_team_leader', 50),
            ('巡检员', 'inspector', 60),
            ('调度员', 'dispatcher', 70),
            ('安全员', 'safety_officer', 80),
            ('技术员', 'technician', 90),
            ('分管领导', 'division_leader', 100),
            ('生产运行主管', 'production_ops_manager', 110),
            ('安全环保主管', 'hse_manager', 120)
        ) AS t(name, code, sort_no)
    LOOP
        IF EXISTS (
            SELECT 1 FROM ent_duty_post_t1
            WHERE deleted = false AND name = item.name
        ) THEN
            CONTINUE;
        END IF;

        INSERT INTO ent_duty_post_t1 (
            tenant_id, entity_type_code, model_id, domain, name, code,
            parent_id, tree_path, sort, status, custom_fields,
            creator, create_time, updater, update_time, deleted
        ) VALUES (
            1, 'duty_post', duty_model_id, '', item.name, item.code,
            0, NULL, item.sort_no, 1, '{}'::jsonb,
            'V238', CURRENT_TIMESTAMP, 'V238', CURRENT_TIMESTAMP, false
        )
        RETURNING id INTO inserted_id;

        IF item.code = 'deputy_station_master'
           AND station_admin_role_id IS NOT NULL
           AND to_regclass('dynamic_duty_post_role') IS NOT NULL
           AND NOT EXISTS (
               SELECT 1 FROM dynamic_duty_post_role
               WHERE deleted = false
                 AND duty_post_id = inserted_id
                 AND role_id = station_admin_role_id
           )
        THEN
            INSERT INTO dynamic_duty_post_role (
                duty_post_id, role_id, creator, create_time, updater, update_time, deleted, tenant_id
            ) VALUES (
                inserted_id, station_admin_role_id, 'V238', CURRENT_TIMESTAMP, 'V238', CURRENT_TIMESTAMP, false, 1
            );
        END IF;
    END LOOP;

    RAISE NOTICE 'V238: 作业区常用职务已补齐（已有同名则跳过）';
END $$;
