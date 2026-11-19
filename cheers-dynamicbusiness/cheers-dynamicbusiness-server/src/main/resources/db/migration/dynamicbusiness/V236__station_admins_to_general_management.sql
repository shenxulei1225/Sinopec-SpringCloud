-- 一次性修数据（租户 1）：站管理员改挂各站已有的「综合管理」，并去掉「管理人员」部门。
--
-- 起因：V235 为每站新建了「管理人员」。产品改为站长直接放在「综合管理」，不再单独建部门。
-- 可重复执行：已挂在综合管理的不改；管理人员部门已删的跳过。
SET search_path TO dynamicbusiness;

DO $$
DECLARE
    moved_users integer := 0;
    removed_depts integer := 0;
BEGIN
    IF to_regclass('ent_department_t1') IS NULL OR to_regclass('ent_user_account_t1') IS NULL THEN
        RAISE NOTICE 'V236: 租户 1 缺少部门或用户表，跳过';
        RETURN;
    END IF;

    WITH targets AS (
        SELECT a.id AS user_id, general.id AS general_dept_id
        FROM ent_user_account_t1 a
        JOIN ent_department_t1 mgr
          ON mgr.deleted = false
         AND mgr.id = a.department_id
         AND mgr.name = '管理人员'
         AND mgr.facility_id IS NOT NULL
        JOIN ent_department_t1 general
          ON general.deleted = false
         AND general.facility_id = mgr.facility_id
         AND general.name = '综合管理'
        WHERE a.deleted = false
          AND a.department_id IS DISTINCT FROM general.id
    )
    UPDATE ent_user_account_t1 a
    SET department_id = targets.general_dept_id,
        updater = 'V236',
        update_time = CURRENT_TIMESTAMP
    FROM targets
    WHERE a.id = targets.user_id;

    GET DIAGNOSTICS moved_users = ROW_COUNT;

    -- 仍挂在管理人员、但能按「站名+管理员」对上综合管理的，一并挪过去（含从未走 V235 部门的）。
    WITH matched AS (
        SELECT a.id AS user_id, general.id AS general_dept_id
        FROM ent_user_account_t1 a
        JOIN ent_facility_t1 f
          ON f.deleted = false
         AND (
                a.name = f.name || '管理员'
             OR (a.name = '角美站管理员' AND f.name = '角美分输站')
         )
        JOIN ent_department_t1 general
          ON general.deleted = false
         AND general.facility_id = f.id
         AND general.name = '综合管理'
        WHERE a.deleted = false
          AND a.department_id IS DISTINCT FROM general.id
    )
    UPDATE ent_user_account_t1 a
    SET department_id = matched.general_dept_id,
        updater = 'V236',
        update_time = CURRENT_TIMESTAMP
    FROM matched
    WHERE a.id = matched.user_id;

    UPDATE ent_department_t1
    SET deleted = true,
        updater = 'V236',
        update_time = CURRENT_TIMESTAMP
    WHERE deleted = false
      AND name = '管理人员'
      AND facility_id IS NOT NULL;

    GET DIAGNOSTICS removed_depts = ROW_COUNT;

    RAISE NOTICE 'V236: 站管理员改挂综合管理（含从管理人员挪入）合计处理完成；软删管理人员部门 % 个；本段第一次 UPDATE 挪动 % 个', removed_depts, moved_users;
END $$;
