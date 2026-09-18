-- ============================================================================
-- task · 03 存量任务补所属场站（金桥）
--
-- 站场级目录创建时必须写所属场站。旧任务 facility_id 为空，金桥厂区下列表曾看不到它们。
-- 本脚本一次性把空场站写成金桥（FAC-JINQIAO），不改已经有场站的行。
-- 同时清 task / task_patrol 表单缓存，下次打开按用途重建，步骤树列才能进详情。
-- 不负责：读路径猜场站；不改 Flyway。
-- ============================================================================

SET search_path TO dynamicbusiness, public;

DO $$
DECLARE
  v_facility_id bigint;
  v_updated integer;
BEGIN
  SELECT id INTO v_facility_id
  FROM ent_facility_t1
  WHERE deleted = FALSE AND code = 'FAC-JINQIAO'
  LIMIT 1;
  IF v_facility_id IS NULL THEN
    RAISE EXCEPTION '金桥厂区不存在（FAC-JINQIAO），不能补任务所属场站';
  END IF;

  UPDATE ent_task_t1
  SET facility_id = v_facility_id,
      updater = 'seed-task-facility',
      update_time = NOW()
  WHERE deleted = FALSE
    AND facility_id IS NULL;

  GET DIAGNOSTICS v_updated = ROW_COUNT;
  RAISE NOTICE '已把 % 条空场站任务写成金桥 %', v_updated, v_facility_id;
END $$;

DELETE FROM model_crud_form_definition
WHERE entity_type_code IN ('task', 'task_patrol');
