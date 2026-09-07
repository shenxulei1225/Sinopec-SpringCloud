-- V99: 退役 steps_json 元数据；将旧步骤 JSON 迁入 action_tree_json
-- 权威：步骤树只认 action_tree_json（节点须有 actionId → 动作库）
-- 旧源：_deprecated_steps_json 或尚未改名的 steps_json（形状 code/title/description/…）
-- 禁止：读路径再读旧列；本迁移幂等，已有非空动作树不覆盖

SET search_path TO dynamicbusiness, public;

-- ---------------------------------------------------------------------------
-- 1) 元数据退役（幂等）
-- ---------------------------------------------------------------------------
UPDATE dynamic_field
SET
  deleted = TRUE,
  status = 0,
  description = '【废弃·V99】原步骤定义 JSON；编排权威为 action_tree_json',
  updater = 'flyway-v99',
  update_time = CURRENT_TIMESTAMP
WHERE code = 'steps_json'
  AND deleted = FALSE;

UPDATE dynamic_entity_type_base_field
SET
  deleted = TRUE,
  status = 0,
  updater = 'flyway-v99',
  update_time = CURRENT_TIMESTAMP
WHERE entity_type_code = 'sop'
  AND field_code = 'steps_json'
  AND deleted = FALSE;

UPDATE dynamic_model_field_assignment mfa
SET
  deleted = TRUE,
  updater = 'flyway-v99',
  update_time = CURRENT_TIMESTAMP
FROM dynamic_model m
WHERE m.id = mfa.model_id
  AND m.entity_type_code = 'sop'
  AND mfa.field_code = 'steps_json'
  AND mfa.deleted = FALSE;

-- ---------------------------------------------------------------------------
-- 2) 按标题确保迁移动作存在（同标题共用一条；编码 act-mig-{md5 前 16 位}）
-- ---------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION dynamicbusiness._v99_ensure_migrated_action(
  p_tenant_id bigint,
  p_title text
) RETURNS text
LANGUAGE plpgsql
AS $$
DECLARE
  v_title text := NULLIF(btrim(COALESCE(p_title, '')), '');
  v_code text;
  v_model_id bigint;
  v_existing text;
BEGIN
  IF v_title IS NULL THEN
    RETURN NULL;
  END IF;

  -- 已有同名动作优先复用（含种子 act-*）
  SELECT a.code INTO v_existing
  FROM dynamicbusiness.ent_action_t1 a
  WHERE a.deleted = FALSE
    AND a.tenant_id = p_tenant_id
    AND a.name = v_title
  ORDER BY a.id
  LIMIT 1;
  IF v_existing IS NOT NULL THEN
    RETURN v_existing;
  END IF;

  -- 亦查模板表 ent_action（无分表时）
  IF to_regclass('dynamicbusiness.ent_action') IS NOT NULL THEN
    SELECT a.code INTO v_existing
    FROM dynamicbusiness.ent_action a
    WHERE a.deleted = FALSE
      AND a.tenant_id = p_tenant_id
      AND a.name = v_title
    ORDER BY a.id
    LIMIT 1;
    IF v_existing IS NOT NULL THEN
      RETURN v_existing;
    END IF;
  END IF;

  v_code := 'act-mig-' || substr(md5(lower(v_title)), 1, 16);

  SELECT a.code INTO v_existing
  FROM dynamicbusiness.ent_action_t1 a
  WHERE a.deleted = FALSE
    AND a.tenant_id = p_tenant_id
    AND a.code = v_code
  LIMIT 1;
  IF v_existing IS NOT NULL THEN
    RETURN v_existing;
  END IF;

  SELECT m.id INTO v_model_id
  FROM dynamicbusiness.dynamic_model m
  WHERE m.deleted = FALSE
    AND m.tenant_id = p_tenant_id
    AND m.entity_type_code = 'action'
    AND m.code = 'action'
  ORDER BY m.id
  LIMIT 1;

  IF v_model_id IS NULL THEN
    RAISE EXCEPTION 'V99: 缺少动作库规范型号 action（tenant_id=%）', p_tenant_id;
  END IF;

  INSERT INTO dynamicbusiness.ent_action_t1 (
    tenant_id, entity_type_code, model_id, name, code, status,
    param_slots_json, child_action_ids_json, is_composite,
    creator, deleted
  )
  SELECT
    p_tenant_id,
    'action',
    v_model_id,
    v_title,
    v_code,
    1,
    '{"version":1,"fields":[]}'::jsonb,
    '[]'::jsonb,
    FALSE,
    'flyway-v99',
    FALSE
  WHERE NOT EXISTS (
    SELECT 1 FROM dynamicbusiness.ent_action_t1 a
    WHERE a.deleted = FALSE
      AND a.tenant_id = p_tenant_id
      AND (a.code = v_code OR a.name = v_title)
  );

  -- 无唯一约束时可能重复插入失败；再读一次
  SELECT a.code INTO v_existing
  FROM dynamicbusiness.ent_action_t1 a
  WHERE a.deleted = FALSE
    AND a.tenant_id = p_tenant_id
    AND (a.code = v_code OR a.name = v_title)
  ORDER BY CASE WHEN a.code = v_code THEN 0 ELSE 1 END, a.id
  LIMIT 1;

  RETURN v_existing;
END;
$$;

-- ---------------------------------------------------------------------------
-- 3) 单表：旧步骤数组 → action_tree_json
-- ---------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION dynamicbusiness._v99_migrate_sop_steps_to_action_tree(p_table text)
RETURNS void
LANGUAGE plpgsql
AS $$
DECLARE
  src_col text;
  rec record;
  steps jsonb;
  tree jsonb;
  elem jsonb;
  i int;
  arr_len int;
  ord int;
  node_key text;
  action_id text;
  step_title text;
  step_desc text;
  step_code text;
  required_val boolean;
  slots jsonb;
  slot_item jsonb;
  j int;
BEGIN
  IF to_regclass('dynamicbusiness.' || p_table) IS NULL THEN
    RETURN;
  END IF;

  IF EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = 'dynamicbusiness'
      AND table_name = lower(p_table)
      AND column_name = '_deprecated_steps_json'
  ) THEN
    src_col := '_deprecated_steps_json';
  ELSIF EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = 'dynamicbusiness'
      AND table_name = lower(p_table)
      AND column_name = 'steps_json'
  ) THEN
    src_col := 'steps_json';
  ELSE
    RETURN;
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = 'dynamicbusiness'
      AND table_name = lower(p_table)
      AND column_name = 'action_tree_json'
  ) THEN
    RETURN;
  END IF;

  FOR rec IN EXECUTE format(
    $q$
      SELECT id, tenant_id, %I AS steps_src, action_tree_json
      FROM dynamicbusiness.%I
      WHERE deleted = false
        AND %I IS NOT NULL
        AND jsonb_typeof(%I) = 'array'
        AND jsonb_array_length(%I) > 0
        AND (
          action_tree_json IS NULL
          OR action_tree_json = '[]'::jsonb
          OR jsonb_typeof(action_tree_json) <> 'array'
          OR jsonb_array_length(action_tree_json) = 0
        )
    $q$,
    src_col, p_table, src_col, src_col, src_col
  )
  LOOP
    steps := rec.steps_src;
    tree := '[]'::jsonb;
    arr_len := jsonb_array_length(steps);

    FOR i IN 0 .. arr_len - 1 LOOP
      elem := steps -> i;
      IF elem IS NULL OR jsonb_typeof(elem) <> 'object' THEN
        CONTINUE;
      END IF;

      ord := COALESCE(NULLIF(elem->>'order', '')::int, i + 1);
      node_key := COALESCE(
        NULLIF(elem->>'nodeKey', ''),
        NULLIF(elem->>'node_key', ''),
        'n-' || ord::text
      );

      action_id := NULLIF(btrim(COALESCE(elem->>'actionId', elem->>'action_id', '')), '');
      step_code := NULLIF(btrim(COALESCE(elem->>'code', '')), '');
      IF action_id IS NULL AND step_code IS NOT NULL AND step_code LIKE 'act-%' THEN
        action_id := step_code;
      END IF;

      step_title := NULLIF(btrim(COALESCE(elem->>'title', elem->>'name', '')), '');
      IF action_id IS NULL THEN
        action_id := dynamicbusiness._v99_ensure_migrated_action(
          COALESCE(rec.tenant_id, 1),
          COALESCE(step_title, step_code, '迁移步骤')
        );
      END IF;
      IF action_id IS NULL OR btrim(action_id) = '' THEN
        CONTINUE;
      END IF;

      step_desc := NULLIF(btrim(COALESCE(
        elem->>'stepDescription',
        elem->>'step_description',
        elem->>'description',
        ''
      )), '');

      IF elem ? 'required' THEN
        required_val := CASE
          WHEN jsonb_typeof(elem->'required') = 'boolean' THEN (elem->>'required')::boolean
          WHEN lower(elem->>'required') IN ('false', '0', 'no') THEN FALSE
          ELSE TRUE
        END;
      ELSE
        required_val := TRUE;
      END IF;

      slots := '[]'::jsonb;
      IF jsonb_typeof(elem->'paramSlots') = 'array' THEN
        slots := elem->'paramSlots';
      ELSIF jsonb_typeof(elem->'parameterSlots') = 'array' THEN
        FOR j IN 0 .. jsonb_array_length(elem->'parameterSlots') - 1 LOOP
          slot_item := elem->'parameterSlots'->j;
          IF jsonb_typeof(slot_item) = 'object' AND NULLIF(slot_item->>'slotKey', '') IS NOT NULL THEN
            slots := slots || jsonb_build_array(slot_item->>'slotKey');
          ELSIF jsonb_typeof(slot_item) = 'string' THEN
            slots := slots || jsonb_build_array(slot_item #>> '{}');
          END IF;
        END LOOP;
      END IF;

      tree := tree || jsonb_build_array(
        jsonb_strip_nulls(jsonb_build_object(
          'nodeKey', node_key,
          'actionId', action_id,
          'order', ord,
          'paramSlots', slots,
          'required', required_val,
          'stepDescription', step_desc
        ))
      );
    END LOOP;

    IF jsonb_array_length(tree) = 0 THEN
      CONTINUE;
    END IF;

    EXECUTE format(
      $q$
        UPDATE dynamicbusiness.%I
        SET action_tree_json = $1,
            updater = 'flyway-v99',
            update_time = CURRENT_TIMESTAMP
        WHERE id = $2
      $q$,
      p_table
    ) USING tree, rec.id;
  END LOOP;
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
    ORDER BY t.table_name
  LOOP
    PERFORM dynamicbusiness._v99_migrate_sop_steps_to_action_tree(tbl);
  END LOOP;
END $$;

DROP FUNCTION IF EXISTS dynamicbusiness._v99_migrate_sop_steps_to_action_tree(text);
DROP FUNCTION IF EXISTS dynamicbusiness._v99_ensure_migrated_action(bigint, text);

-- 清 SOP CRUD 表单缓存，下次按退役后的字段重算
DELETE FROM model_crud_form_definition
WHERE entity_type_code = 'sop';
