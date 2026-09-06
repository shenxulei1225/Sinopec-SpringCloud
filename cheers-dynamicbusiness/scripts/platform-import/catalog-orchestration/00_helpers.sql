-- ============================================================================
-- catalog-orchestration · 00 幂等 helper
-- 只写编排头 dm_catalog_orchestration：启用 + 点树还是点列表行。
-- 开哪些栏认页面布局，本包不写槽、不写 What/How。
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
        '_seed_five_w_replace_who_slots',
        '_seed_five_w_semantic',
        '_seed_five_w_recipe_ledger_3col',
        '_seed_five_w_recipe_category_entity_2col',
        '_seed_five_w_recipe_category_as_object'
      )
  LOOP
    EXECUTE format('DROP FUNCTION IF EXISTS %I.%I(%s)', r.nspname, r.proname, r.args);
  END LOOP;
END $$;

CREATE OR REPLACE FUNCTION dynamicbusiness._seed_catalog_orchestration(
  p_tenant_id bigint,
  p_entity_type_code text,
  p_object_pick_from text DEFAULT 'LIST_ROW'
) RETURNS void
LANGUAGE plpgsql
AS $$
DECLARE
  v_pick text := COALESCE(NULLIF(upper(trim(p_object_pick_from)), ''), 'LIST_ROW');
BEGIN
  UPDATE dm_catalog_orchestration
  SET enabled = true,
      object_pick_from = v_pick,
      deleted = false,
      updater = 'seed',
      update_time = CURRENT_TIMESTAMP
  WHERE entity_type_code = p_entity_type_code AND tenant_id = p_tenant_id;

  IF NOT FOUND THEN
    INSERT INTO dm_catalog_orchestration (
      entity_type_code, enabled, object_pick_from,
      tenant_id, creator, deleted
    ) VALUES (
      p_entity_type_code, true, v_pick,
      p_tenant_id, 'seed', false
    );
  END IF;
END;
$$;
