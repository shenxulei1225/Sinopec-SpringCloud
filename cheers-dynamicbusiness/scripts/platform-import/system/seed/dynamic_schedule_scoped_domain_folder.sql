-- 在 schedule 主数据分类根下补「巡检排期」域分组，并将已迁移的巡检子分类挂到其下。
-- 约定：DOMAIN 域分组 code = {registryCode}_dir（与 EntityTypeCategoryBootstrapService 一致）。

SET search_path TO dynamicbusiness;

BEGIN;

DO $$
DECLARE
  schedule_root_id bigint;
  patrol_folder_id bigint;
BEGIN
  SELECT id INTO schedule_root_id
  FROM dynamic_category
  WHERE category_type_code = 'schedule'
    AND parent_id IS NULL
    AND deleted = false
  ORDER BY id
  LIMIT 1;

  IF schedule_root_id IS NULL THEN
    RAISE EXCEPTION 'schedule 分类根不存在';
  END IF;

  SELECT id INTO patrol_folder_id
  FROM dynamic_category
  WHERE category_type_code = 'schedule'
    AND code = 'patrol_schedule_dir'
    AND deleted = false
  LIMIT 1;

  IF patrol_folder_id IS NULL THEN
    INSERT INTO dynamic_category (
      parent_id, code, name, category_type_code, level, sort, status,
      creator, create_time, updater, update_time, deleted, tenant_id
    )
    VALUES (
      schedule_root_id,
      'patrol_schedule_dir',
      '巡检排期',
      'schedule',
      2,
      1,
      1,
      'scope-fix',
      CURRENT_TIMESTAMP,
      'scope-fix',
      CURRENT_TIMESTAMP,
      false,
      1
    )
    RETURNING id INTO patrol_folder_id;

    UPDATE dynamic_category
    SET tree_path = '/' || schedule_root_id::text || '/' || patrol_folder_id::text || '/',
        parent_code = (SELECT code FROM dynamic_category WHERE id = schedule_root_id)
    WHERE id = patrol_folder_id;
  END IF;

  UPDATE dynamic_category
  SET parent_id = patrol_folder_id,
      level = 3,
      tree_path = '/' || schedule_root_id::text || '/' || patrol_folder_id::text || '/' || id::text || '/',
      parent_code = 'patrol_schedule_dir',
      updater = 'scope-fix',
      update_time = CURRENT_TIMESTAMP
  WHERE category_type_code = 'schedule'
    AND parent_id = schedule_root_id
    AND id <> patrol_folder_id
    AND deleted = false
    AND code IN (
      'CAT-82069db0b7ab441faec2588f486eae0b',
      'CAT-d6946c1a9107475592cb70558b7087ef',
      'CAT-998ff579a2b24a74975c5268cd20eb73',
      'CAT-624359a997a749e7a1f5a026f5501a5c'
    );

  UPDATE dynamic_category c
  SET level = 4,
      tree_path = '/' || schedule_root_id::text || '/' || patrol_folder_id::text || '/' || c.parent_id::text || '/' || c.id::text || '/',
      parent_code = (SELECT code FROM dynamic_category p WHERE p.id = c.parent_id),
      updater = 'scope-fix',
      update_time = CURRENT_TIMESTAMP
  WHERE c.category_type_code = 'schedule'
    AND c.parent_id IN (4110, 4111, 4112, 4113)
    AND c.deleted = false;
END $$;

COMMIT;
