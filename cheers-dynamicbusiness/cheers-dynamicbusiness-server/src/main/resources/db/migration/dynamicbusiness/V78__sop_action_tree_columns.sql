-- V78: SOP 动作树列（action_tree_json 等）；从 default_steps_json 迁数据
-- 设计：docs/superpowers/specs/2026-08-29-action-library-sop-task-tree-design.md
-- 注意：本版不 DROP default_steps_json（V79 才清理步骤模板实体）；列可保留作历史，权威改为动作树

SET search_path TO dynamicbusiness, public;

-- 1) 加列：ent_sop 与 ent_sop_t*
DO $$
DECLARE
  tbl text;
BEGIN
  FOR tbl IN
    SELECT t.table_name
    FROM information_schema.tables t
    WHERE t.table_schema = 'dynamicbusiness'
      AND t.table_type = 'BASE TABLE'
      AND t.table_name ~ '^ent_sop(_t[0-9]+)?$'
  LOOP
    EXECUTE format(
      'ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS action_tree_json JSONB NOT NULL DEFAULT ''[]''::jsonb',
      tbl);
    EXECUTE format(
      'ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS action_tree_override_json JSONB',
      tbl);
    EXECUTE format(
      'ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS default_params_by_node_json JSONB NOT NULL DEFAULT ''{}''::jsonb',
      tbl);

    EXECUTE format(
      'COMMENT ON COLUMN dynamicbusiness.%I.action_tree_json IS %L',
      tbl, '模板默认动作树 JSON（节点：nodeKey/actionId/order/parentNodeKey?；merge 真源）');
    EXECUTE format(
      'COMMENT ON COLUMN dynamicbusiness.%I.action_tree_override_json IS %L',
      tbl, '实例整树替换差量 JSON（可选）');
    EXECUTE format(
      'COMMENT ON COLUMN dynamicbusiness.%I.default_params_by_node_json IS %L',
      tbl, '模板默认参数（按 nodeKey → params 对象）');

    -- 软废弃标注：旧步骤列表列仍在，权威已迁至 action_tree_json（V79 清理步骤模板）
    IF EXISTS (
      SELECT 1 FROM information_schema.columns
      WHERE table_schema = 'dynamicbusiness' AND table_name = tbl AND column_name = 'default_steps_json'
    ) THEN
      EXECUTE format(
        'COMMENT ON COLUMN dynamicbusiness.%I.default_steps_json IS %L',
        tbl, '【废弃·V78】原模板默认步骤列表；已迁 action_tree_json；勿再作读权威（V79 清理步骤模板）');
    END IF;
    IF EXISTS (
      SELECT 1 FROM information_schema.columns
      WHERE table_schema = 'dynamicbusiness' AND table_name = tbl AND column_name = 'default_params_json'
    ) THEN
      EXECUTE format(
        'COMMENT ON COLUMN dynamicbusiness.%I.default_params_json IS %L',
        tbl, '【废弃·V78】原扁平默认参数；已迁 default_params_by_node_json；勿再作读权威');
    END IF;
    IF EXISTS (
      SELECT 1 FROM information_schema.columns
      WHERE table_schema = 'dynamicbusiness' AND table_name = tbl AND column_name = 'step_override_json'
    ) THEN
      EXECUTE format(
        'COMMENT ON COLUMN dynamicbusiness.%I.step_override_json IS %L',
        tbl, '【废弃·V78】原实例步骤差量；新权威为 action_tree_override_json');
    END IF;
  END LOOP;
END $$;

-- 2) 数据迁移：default_steps_json(stepTemplateId) → action_tree_json(actionId + nodeKey)
--    default_params_json(扁平) → default_params_by_node_json（按节点 paramSlots 交集）
CREATE OR REPLACE FUNCTION dynamicbusiness._v78_migrate_sop_action_tree(p_table text)
RETURNS void
LANGUAGE plpgsql
AS $$
DECLARE
  rec record;
  steps jsonb;
  flat_params jsonb;
  tree jsonb;
  params_by_node jsonb;
  elem jsonb;
  ord int;
  node_key text;
  action_id text;
  step_tpl text;
  slots jsonb;
  node_params jsonb;
  k text;
  i int;
  arr_len int;
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM information_schema.tables
    WHERE table_schema = 'dynamicbusiness' AND table_name = lower(p_table)
  ) THEN
    RETURN;
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = 'dynamicbusiness' AND table_name = lower(p_table)
      AND column_name = 'default_steps_json'
  ) THEN
    RETURN;
  END IF;

  FOR rec IN EXECUTE format(
    $q$
      SELECT id,
             default_steps_json,
             COALESCE(default_params_json, '{}'::jsonb) AS default_params_json,
             action_tree_json,
             default_params_by_node_json
      FROM dynamicbusiness.%I
      WHERE deleted = false
        AND default_steps_json IS NOT NULL
        AND jsonb_typeof(default_steps_json) = 'array'
        AND jsonb_array_length(default_steps_json) > 0
        AND (
          action_tree_json IS NULL
          OR action_tree_json = '[]'::jsonb
          OR jsonb_typeof(action_tree_json) <> 'array'
          OR jsonb_array_length(action_tree_json) = 0
        )
    $q$,
    p_table
  )
  LOOP
    steps := rec.default_steps_json;
    flat_params := COALESCE(rec.default_params_json, '{}'::jsonb);
    IF jsonb_typeof(flat_params) <> 'object' THEN
      flat_params := '{}'::jsonb;
    END IF;

    tree := '[]'::jsonb;
    params_by_node := '{}'::jsonb;
    arr_len := jsonb_array_length(steps);

    FOR i IN 0 .. arr_len - 1 LOOP
      elem := steps -> i;
      IF elem IS NULL OR jsonb_typeof(elem) <> 'object' THEN
        CONTINUE;
      END IF;

      ord := COALESCE((elem->>'order')::int, i + 1);
      node_key := COALESCE(NULLIF(elem->>'nodeKey', ''), 'n-' || ord::text);

      action_id := NULLIF(elem->>'actionId', '');
      IF action_id IS NULL THEN
        step_tpl := NULLIF(elem->>'stepTemplateId', '');
        IF step_tpl IS NOT NULL THEN
          action_id := regexp_replace(step_tpl, '^st-', 'act-');
        END IF;
      END IF;
      IF action_id IS NULL THEN
        CONTINUE;
      END IF;

      tree := tree || jsonb_build_array(
        jsonb_strip_nulls(jsonb_build_object(
          'nodeKey', node_key,
          'actionId', action_id,
          'order', ord,
          'parentNodeKey', elem->'parentNodeKey'
        ))
      );

      -- 扁平默认参数：只写入与该节点 paramSlots 有交集的键
      slots := COALESCE(elem->'paramSlots', '[]'::jsonb);
      node_params := '{}'::jsonb;
      IF jsonb_typeof(slots) = 'array' AND jsonb_typeof(flat_params) = 'object' THEN
        FOR k IN SELECT jsonb_object_keys(flat_params)
        LOOP
          IF EXISTS (
            SELECT 1
            FROM jsonb_array_elements_text(slots) s(v)
            WHERE s.v = k
          ) THEN
            node_params := node_params || jsonb_build_object(k, flat_params -> k);
          END IF;
        END LOOP;
      END IF;
      IF node_params <> '{}'::jsonb THEN
        params_by_node := params_by_node || jsonb_build_object(node_key, node_params);
      END IF;
    END LOOP;

    -- 若节点均无 paramSlots 但扁平参数非空：整包挂到第一个节点（避免丢参）
    IF params_by_node = '{}'::jsonb
       AND flat_params <> '{}'::jsonb
       AND jsonb_array_length(tree) > 0
    THEN
      params_by_node := jsonb_build_object(tree->0->>'nodeKey', flat_params);
    END IF;

    EXECUTE format(
      $q$
        UPDATE dynamicbusiness.%I
        SET action_tree_json = $1,
            default_params_by_node_json = $2,
            updater = COALESCE(updater, 'flyway-v78'),
            update_time = CURRENT_TIMESTAMP
        WHERE id = $3
      $q$,
      p_table
    ) USING tree, params_by_node, rec.id;
  END LOOP;

  -- step_override_json（步骤数组形）→ action_tree_override_json
  IF EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = 'dynamicbusiness' AND table_name = lower(p_table)
      AND column_name = 'step_override_json'
  ) THEN
    FOR rec IN EXECUTE format(
      $q$
        SELECT id, step_override_json
        FROM dynamicbusiness.%I
        WHERE deleted = false
          AND step_override_json IS NOT NULL
          AND jsonb_typeof(step_override_json) = 'array'
          AND jsonb_array_length(step_override_json) > 0
          AND (
            action_tree_override_json IS NULL
            OR action_tree_override_json = '[]'::jsonb
          )
      $q$,
      p_table
    )
    LOOP
      steps := rec.step_override_json;
      tree := '[]'::jsonb;
      arr_len := jsonb_array_length(steps);
      FOR i IN 0 .. arr_len - 1 LOOP
        elem := steps -> i;
        IF elem IS NULL OR jsonb_typeof(elem) <> 'object' THEN
          CONTINUE;
        END IF;
        ord := COALESCE((elem->>'order')::int, i + 1);
        node_key := COALESCE(NULLIF(elem->>'nodeKey', ''), 'n-' || ord::text);
        action_id := NULLIF(elem->>'actionId', '');
        IF action_id IS NULL THEN
          step_tpl := NULLIF(elem->>'stepTemplateId', '');
          IF step_tpl IS NOT NULL THEN
            action_id := regexp_replace(step_tpl, '^st-', 'act-');
          END IF;
        END IF;
        IF action_id IS NULL THEN
          CONTINUE;
        END IF;
        tree := tree || jsonb_build_array(
          jsonb_strip_nulls(jsonb_build_object(
            'nodeKey', node_key,
            'actionId', action_id,
            'order', ord,
            'parentNodeKey', elem->'parentNodeKey'
          ))
        );
      END LOOP;

      IF jsonb_array_length(tree) > 0 THEN
        EXECUTE format(
          $q$
            UPDATE dynamicbusiness.%I
            SET action_tree_override_json = $1,
                updater = COALESCE(updater, 'flyway-v78'),
                update_time = CURRENT_TIMESTAMP
            WHERE id = $2
          $q$,
          p_table
        ) USING tree, rec.id;
      END IF;
    END LOOP;
  END IF;
END;
$$;

DO $$
DECLARE
  tbl text;
BEGIN
  FOR tbl IN
    SELECT t.table_name
    FROM information_schema.tables t
    WHERE t.table_schema = 'dynamicbusiness'
      AND t.table_type = 'BASE TABLE'
      AND t.table_name ~ '^ent_sop(_t[0-9]+)?$'
  LOOP
    PERFORM dynamicbusiness._v78_migrate_sop_action_tree(tbl);
  END LOOP;
END $$;

DROP FUNCTION IF EXISTS dynamicbusiness._v78_migrate_sop_action_tree(text);
