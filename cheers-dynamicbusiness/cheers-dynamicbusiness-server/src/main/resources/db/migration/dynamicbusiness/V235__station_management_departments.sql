-- 一次性修数据（租户 1）：每座站补「管理人员」部门，并把站管理员账号挂进该部门。
--
-- 起因：站管理员只有作业区上的「站长」任职、所属部门为空。按人员点具体班组时看不到他们；
-- 若在读路径把全作业区任职人硬并进每个班组，又会在点「维修班」时带出全作业区站长。
-- 正确做法：站下有「管理人员」部门，账号所属部门指向它，点该部门才看到该站站长。
--
-- 匹配规则：账号名 = 场站名 || '管理员'；另有「角美站管理员」对「角美分输站」。
-- 可重复执行：已有「管理人员」的站不重复建；已挂对部门的账号不改。
SET search_path TO dynamicbusiness;

DO $$
DECLARE
    dept_model_id bigint;
    inserted_depts integer := 0;
    linked_users integer := 0;
BEGIN
    IF to_regclass('ent_department_t1') IS NULL
        OR to_regclass('ent_facility_t1') IS NULL
        OR to_regclass('ent_user_account_t1') IS NULL THEN
        RAISE NOTICE 'V235: 租户 1 缺少部门/场站/用户表，跳过';
        RETURN;
    END IF;

    SELECT id INTO dept_model_id
    FROM dynamic_model
    WHERE deleted = false AND tenant_id = 1 AND entity_type_code = 'department'
    ORDER BY id
    LIMIT 1;

    IF dept_model_id IS NULL THEN
        RAISE NOTICE 'V235: 没有部门型号，跳过';
        RETURN;
    END IF;

    WITH missing AS (
        SELECT f.id AS facility_id, f.name AS facility_name
        FROM ent_facility_t1 f
        WHERE f.deleted = false
          AND NOT EXISTS (
              SELECT 1
              FROM ent_department_t1 d
              WHERE d.deleted = false
                AND d.facility_id = f.id
                AND d.name = '管理人员'
          )
    ),
    inserted AS (
        INSERT INTO ent_department_t1 (
            tenant_id, entity_type_code, model_id, name, code, status,
            facility_id, organization_id, sort, deleted, creator, create_time, updater, update_time
        )
        SELECT
            1,
            'department',
            dept_model_id,
            '管理人员',
            'DEPT-F' || missing.facility_id::text || '-MGR',
            1,
            missing.facility_id,
            NULL,
            0,
            false,
            'V235',
            CURRENT_TIMESTAMP,
            'V235',
            CURRENT_TIMESTAMP
        FROM missing
        RETURNING id
    )
    SELECT COUNT(*) INTO inserted_depts FROM inserted;

    WITH matched AS (
        SELECT a.id AS user_id, d.id AS department_id
        FROM ent_user_account_t1 a
        JOIN ent_facility_t1 f
          ON f.deleted = false
         AND (
                a.name = f.name || '管理员'
             OR (a.name = '角美站管理员' AND f.name = '角美分输站')
         )
        JOIN ent_department_t1 d
          ON d.deleted = false
         AND d.facility_id = f.id
         AND d.name = '管理人员'
        WHERE a.deleted = false
          AND (a.department_id IS DISTINCT FROM d.id)
    )
    UPDATE ent_user_account_t1 a
    SET department_id = matched.department_id,
        updater = 'V235',
        update_time = CURRENT_TIMESTAMP
    FROM matched
    WHERE a.id = matched.user_id;

    GET DIAGNOSTICS linked_users = ROW_COUNT;

    RAISE NOTICE 'V235: 新建管理人员部门 % 个，挂上站管理员账号 % 个', inserted_depts, linked_users;
END $$;
