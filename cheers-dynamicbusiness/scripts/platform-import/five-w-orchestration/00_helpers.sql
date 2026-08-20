-- ============================================================================
-- five-w-orchestration · 00 幂等 helper（本包内复用）
-- 只写编排头 dm_five_w_orchestration。开哪些栏认 dm_data_tab_layout，不在这里插槽。
-- ============================================================================

SET search_path TO dynamicbusiness;

DO $$
DECLARE r record;
BEGIN
  FOR r IN
    SELECT n.nspname, p.proname, pg_get_function_identity_arguments(p.oid) AS args
    FROM pg_proc p
    JOIN pg_namespace n ON n.oid = p.pronamespace
    WHERE n.nspname = 'dynamicbusiness'
      AND p.proname IN (
        '_seed_five_w_filter_slot',
        '_seed_five_w_replace_filter_slots',
        '_seed_five_w_who_slot',
        '_seed_five_w_replace_who_slots'
      )
  LOOP
    EXECUTE format('DROP FUNCTION IF EXISTS %I.%I(%s)', r.nspname, r.proname, r.args);
  END LOOP;
END $$;

DROP FUNCTION IF EXISTS dynamicbusiness._seed_five_w_semantic(bigint, text, text, jsonb, text, jsonb);
DROP FUNCTION IF EXISTS dynamicbusiness._seed_five_w_semantic(bigint, text, text, text, jsonb, text, jsonb);

CREATE OR REPLACE FUNCTION dynamicbusiness._seed_five_w_semantic(
  p_tenant_id bigint,
  p_entity_type_code text,
  p_what_mode text,
  p_what_config jsonb,
  p_how_mode text DEFAULT 'NONE',
  p_how_config jsonb DEFAULT '{}'::jsonb,
  p_object_pick_from text DEFAULT 'LIST_ROW'
) RETURNS void
LANGUAGE plpgsql
AS $$
DECLARE
  v_pick text := COALESCE(NULLIF(upper(trim(p_object_pick_from)), ''), 'LIST_ROW');
BEGIN
  UPDATE dm_five_w_orchestration
  SET enabled = true,
      what_mode = p_what_mode,
      what_config = p_what_config,
      how_mode = p_how_mode,
      how_config = p_how_config,
      object_pick_from = v_pick,
      deleted = false,
      updater = 'seed',
      update_time = CURRENT_TIMESTAMP
  WHERE entity_type_code = p_entity_type_code AND tenant_id = p_tenant_id;

  IF NOT FOUND THEN
    INSERT INTO dm_five_w_orchestration (
      entity_type_code, enabled, what_mode, what_config,
      how_mode, how_config, object_pick_from,
      tenant_id, creator, deleted
    ) VALUES (
      p_entity_type_code, true, p_what_mode, p_what_config,
      p_how_mode, p_how_config, v_pick,
      p_tenant_id, 'seed', false
    );
  END IF;
END;
$$;

-- 台账：点列表这一行是当前对象
CREATE OR REPLACE FUNCTION dynamicbusiness._seed_five_w_recipe_ledger_3col(
  p_tenant_id bigint,
  p_registry_code text,
  p_category_type_code text,
  p_category_label text DEFAULT NULL,
  p_what_mode text DEFAULT 'VIEW_DETAIL',
  p_what_config jsonb DEFAULT '{"bindLayer":"ENTITY"}'::jsonb,
  p_how_mode text DEFAULT 'NONE',
  p_how_config jsonb DEFAULT '{}'::jsonb
) RETURNS void
LANGUAGE plpgsql
AS $$
BEGIN
  PERFORM dynamicbusiness._seed_five_w_semantic(
    p_tenant_id, p_registry_code, p_what_mode, p_what_config,
    p_how_mode, p_how_config, 'LIST_ROW');
END;
$$;

-- 分类 + 实体（无型号栏）：开列仍认布局；编排头只写点列表这一行
CREATE OR REPLACE FUNCTION dynamicbusiness._seed_five_w_recipe_category_entity_2col(
  p_tenant_id bigint,
  p_registry_code text,
  p_category_type_code text,
  p_category_label text DEFAULT NULL,
  p_what_mode text DEFAULT 'VIEW_DETAIL',
  p_what_config jsonb DEFAULT '{"bindLayer":"ENTITY"}'::jsonb,
  p_how_mode text DEFAULT 'NONE',
  p_how_config jsonb DEFAULT '{}'::jsonb
) RETURNS void
LANGUAGE plpgsql
AS $$
BEGIN
  PERFORM dynamicbusiness._seed_five_w_semantic(
    p_tenant_id, p_registry_code, p_what_mode, p_what_config,
    p_how_mode, p_how_config, 'LIST_ROW');
END;
$$;

-- 分类树节点即实体（region / department）
CREATE OR REPLACE FUNCTION dynamicbusiness._seed_five_w_recipe_category_as_object(
  p_tenant_id bigint,
  p_registry_code text,
  p_category_type_code text,
  p_category_label text
) RETURNS void
LANGUAGE plpgsql
AS $$
BEGIN
  PERFORM dynamicbusiness._seed_five_w_semantic(
    p_tenant_id, p_registry_code, 'VIEW_DETAIL', '{"bindLayer":"ENTITY"}'::jsonb,
    'NONE', '{}'::jsonb, 'CATEGORY_NODE');
END;
$$;
