-- ============================================================================
-- task · 02 金桥厂区可见的一条巡检任务（带步骤树样例）
--
-- 站场级目录只认所属场站。库里旧任务 facility_id 为空，当前场站「金桥厂区」下列表是空的。
-- 本脚本按创建口径写：底座 task 表、业务域巡检、所属场站金桥、步骤树混挂头/检查项/尾。
-- 不负责：按路径自动串包。旧任务空场站见 03_assign_jinqiao_facility_to_orphan_tasks.sql。
-- ============================================================================

SET search_path TO dynamicbusiness, public;

DO $$
DECLARE
  v_facility_id bigint;
  v_model_id bigint;
  v_item_id bigint;
  v_item_code text;
  v_item_name text;
  v_head_id bigint;
  v_tail_id bigint;
BEGIN
  SELECT id INTO v_facility_id
  FROM ent_facility_t1
  WHERE deleted = FALSE AND code = 'FAC-JINQIAO'
  LIMIT 1;
  IF v_facility_id IS NULL THEN
    RAISE EXCEPTION '金桥厂区不存在（FAC-JINQIAO），不能创建可见任务';
  END IF;

  SELECT id INTO v_model_id
  FROM dynamic_model
  WHERE deleted = FALSE AND entity_type_code = 'task' AND code = 'patrol_task'
  LIMIT 1;
  IF v_model_id IS NULL THEN
    RAISE EXCEPTION '型号 patrol_task 不存在，不能创建巡检任务';
  END IF;

  SELECT id INTO v_head_id FROM ent_action_t1
  WHERE deleted = FALSE AND code = 'act-robot-head' LIMIT 1;
  SELECT id INTO v_tail_id FROM ent_action_t1
  WHERE deleted = FALSE AND code = 'act-robot-tail' LIMIT 1;
  IF v_head_id IS NULL OR v_tail_id IS NULL THEN
    RAISE EXCEPTION '机器人头尾动作不存在，请先导入动作库头尾';
  END IF;

  SELECT id, code, name INTO v_item_id, v_item_code, v_item_name
  FROM ent_inspection_item_t1
  WHERE deleted = FALSE AND code = 'INSP-9959036'
  LIMIT 1;
  IF v_item_id IS NULL THEN
    RAISE EXCEPTION '检查项 INSP-9959036 不存在，不能挂步骤树样例';
  END IF;

  INSERT INTO ent_task_t1 (
    tenant_id, entity_type_code, model_id, name, code, status,
    domain, facility_id, step_tree_json,
    creator, updater, deleted
  )
  SELECT
    1,
    'task',
    v_model_id,
    '金桥储罐区步骤树验收',
    'task-step-tree-preview-jinqiao',
    1,
    '巡检',
    v_facility_id,
    jsonb_build_object(
      'version', 1,
      'nodes', jsonb_build_array(
        jsonb_build_object(
          'nodeKey', 'n-head',
          'order', 1,
          'parentNodeKey', NULL,
          'hangTypeCode', 'action',
          'refCode', 'act-robot-head',
          'refId', v_head_id,
          'title', '机器人任务头'
        ),
        jsonb_build_object(
          'nodeKey', 'n-item',
          'order', 2,
          'parentNodeKey', NULL,
          'hangTypeCode', 'inspection_item',
          'refCode', v_item_code,
          'refId', v_item_id,
          'title', v_item_name
        ),
        jsonb_build_object(
          'nodeKey', 'n-tail',
          'order', 3,
          'parentNodeKey', NULL,
          'hangTypeCode', 'action',
          'refCode', 'act-robot-tail',
          'refId', v_tail_id,
          'title', '机器人任务尾'
        )
      )
    ),
    'seed-task-preview',
    'seed-task-preview',
    FALSE
  WHERE NOT EXISTS (
    SELECT 1 FROM ent_task_t1
    WHERE deleted = FALSE AND code = 'task-step-tree-preview-jinqiao'
  );
END $$;
