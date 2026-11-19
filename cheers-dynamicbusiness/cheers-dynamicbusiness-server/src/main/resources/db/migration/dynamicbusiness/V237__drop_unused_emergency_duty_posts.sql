-- 一次性修数据（租户 1）：删掉误进职务目录的两条应急职务名。
--
-- 起因：「企业文化工作分管领导」「党委组织与宣传部（人力资源部、群众工作部）主要负责人」
-- 是应急管理里公司级说法，不是数据权限职务目录里作业区要用的职务。
-- 两条均无任职、无职务绑角色，软删即可；「站长」「系统管理员」保留。
-- 可重复执行。
SET search_path TO dynamicbusiness;

DO $$
DECLARE
    removed integer := 0;
BEGIN
    IF to_regclass('ent_duty_post_t1') IS NULL THEN
        RAISE NOTICE 'V237: 没有职务表，跳过';
        RETURN;
    END IF;

    UPDATE ent_duty_post_t1
    SET deleted = true,
        updater = 'V237',
        update_time = CURRENT_TIMESTAMP
    WHERE deleted = false
      AND name IN (
          '企业文化工作分管领导',
          '党委组织与宣传部（人力资源部、群众工作部）主要负责人'
      )
      AND NOT EXISTS (
          SELECT 1
          FROM dynamic_post_assignment pa
          WHERE pa.deleted = false AND pa.duty_post_id = ent_duty_post_t1.id
      );

    GET DIAGNOSTICS removed = ROW_COUNT;
    RAISE NOTICE 'V237: 软删多余职务 % 条', removed;
END $$;
