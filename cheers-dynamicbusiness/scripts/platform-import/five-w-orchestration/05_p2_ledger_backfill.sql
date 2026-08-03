-- ============================================================================
-- five-w-orchestration · 05 P2 批量回填
-- 为尚未有 dm_five_w_orchestration 行的目录补默认 bundle（幂等；不覆盖已有行）
-- 规则对齐 EntityTypeOrchestrationBootstrapService + 产品配置表 §2～§36
-- ============================================================================

SET search_path TO dynamicbusiness;

DO $$
DECLARE
  v_tenant CONSTANT bigint := 1;
  r RECORD;
  v_base text;
  v_name text;
BEGIN
  FOR r IN
    SELECT et.code, et.name, et.entry_kind, et.base_entity_type_code
    FROM dynamic_entity_type et
    WHERE et.tenant_id = v_tenant
      AND et.deleted = false
      AND NOT EXISTS (
        SELECT 1 FROM dm_five_w_orchestration o
        WHERE o.entity_type_code = et.code
          AND o.tenant_id = v_tenant
          AND o.deleted = false
      )
    ORDER BY et.code
  LOOP
    v_base := NULLIF(trim(r.base_entity_type_code), '');
    v_name := COALESCE(NULLIF(trim(r.name), ''), r.code);

    RAISE NOTICE 'backfill five-w bundle: % (entry_kind=%)', r.code, r.entry_kind;

    IF r.entry_kind = 'CATEGORY' THEN
      PERFORM dynamicbusiness._seed_five_w_recipe_category_linked_entity(
        v_tenant, r.code, r.code, v_name);

    ELSIF r.code = 'Inspection_content' THEN
      PERFORM dynamicbusiness._seed_five_w_semantic(
        v_tenant, r.code, 'ENTITY', 'SITE_PREP',
        '{"bindLayer":"ENTITY","candidateEntityTypeCode":"inspection_item"}'::jsonb,
        'AFTER_WHAT_ITEM', '{}'::jsonb);
      PERFORM dynamicbusiness._seed_five_w_replace_who_slots(v_tenant, r.code);
      PERFORM dynamicbusiness._seed_five_w_who_slot(
        v_tenant, r.code, 'CATEGORY', 'Inspection_content-category', NULL,
        true, '["categoryId"]'::jsonb, NULL,
        '{"label":"设备分类","categoryTypeCode":"equipment"}'::jsonb);
      PERFORM dynamicbusiness._seed_five_w_who_slot(
        v_tenant, r.code, 'MODEL', 'Inspection_content-model', NULL,
        true, '["modelId"]'::jsonb, NULL, NULL);
      PERFORM dynamicbusiness._seed_five_w_who_slot(
        v_tenant, r.code, 'ENTITY', 'Inspection_content-entity', NULL,
        true, '["entityId"]'::jsonb, 'rowSelection', NULL);

    ELSIF r.entry_kind IN ('SCOPE', 'DOMAIN', 'REUSE') AND v_base IS NOT NULL THEN
      IF r.code IN (
        'patrol_point', 'patrol_route',
        'maintanence_schedule', 'patrol_schedule',
        'task_record_maintenance', 'task_record_patrol'
      ) THEN
        PERFORM dynamicbusiness._seed_five_w_recipe_category_entity_2col(
          v_tenant, r.code, v_base, v_name);
      ELSE
        PERFORM dynamicbusiness._seed_five_w_recipe_ledger_3col(
          v_tenant, r.code, v_base, v_name);
      END IF;

    ELSIF r.code IN (
      'standard', 'Document', 'point', 'route', 'scene', 'scene_placement',
      'customer', 'billing',
      'emergency', 'emergentcy_plan', 'emergency_response',
      'emergency_resource', 'emergency_team',
      'inspection_method'
    ) THEN
      PERFORM dynamicbusiness._seed_five_w_recipe_category_entity_2col(
        v_tenant, r.code, r.code, v_name);

    ELSE
      PERFORM dynamicbusiness._seed_five_w_recipe_ledger_3col(
        v_tenant, r.code, r.code, v_name);
    END IF;
  END LOOP;
END $$;
