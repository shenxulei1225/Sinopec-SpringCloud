-- ============================================================================
-- five-w-orchestration · 00 幂等 helper（本包内复用）
-- ============================================================================

SET search_path TO dynamicbusiness;

CREATE OR REPLACE FUNCTION dynamicbusiness._seed_five_w_filter_slot(
  p_tenant_id bigint,
  p_entity_type_code text,
  p_slot_ref text,
  p_perspective_id text,
  p_enabled boolean,
  p_entity_id_rule text,
  p_category_column jsonb,
  p_column_kind text DEFAULT 'CATEGORY',
  p_context_outputs jsonb DEFAULT NULL
) RETURNS void
LANGUAGE plpgsql
AS $$
DECLARE
  v_kind text := COALESCE(NULLIF(upper(trim(p_column_kind)), ''), 'CATEGORY');
  v_outputs jsonb := COALESCE(
    p_context_outputs,
    CASE v_kind
      WHEN 'MODEL' THEN '["modelId"]'::jsonb
      WHEN 'ENTITY' THEN '["entityId"]'::jsonb
      ELSE '["categoryId"]'::jsonb
    END
  );
BEGIN
  UPDATE dm_five_w_filter_layout
  SET enabled = p_enabled,
      context_outputs = v_outputs,
      entity_id_rule = p_entity_id_rule,
      category_column = p_category_column,
      perspective_id = p_perspective_id,
      deleted = false,
      updater = 'seed',
      update_time = CURRENT_TIMESTAMP
  WHERE tenant_id = p_tenant_id
    AND entity_type_code = p_entity_type_code
    AND column_kind = v_kind
    AND COALESCE(perspective_id, '') = COALESCE(p_perspective_id, '')
    AND COALESCE(slot_ref, '') = COALESCE(p_slot_ref, '')
    AND deleted = false;

  IF NOT FOUND THEN
    INSERT INTO dm_five_w_filter_layout (
      entity_type_code, column_kind, slot_ref, perspective_id, props_id,
      enabled, context_outputs, entity_id_rule, category_column,
      tenant_id, creator, deleted
    ) VALUES (
      p_entity_type_code, v_kind, p_slot_ref, p_perspective_id, NULL,
      p_enabled, v_outputs, p_entity_id_rule, p_category_column,
      p_tenant_id, 'seed', false
    );
  END IF;
END;
$$;

CREATE OR REPLACE FUNCTION dynamicbusiness._seed_five_w_replace_filter_slots(
  p_tenant_id bigint,
  p_entity_type_code text
) RETURNS void
LANGUAGE plpgsql
AS $$
BEGIN
  UPDATE dm_five_w_filter_layout
  SET deleted = true, updater = 'seed-replace', update_time = CURRENT_TIMESTAMP
  WHERE entity_type_code = p_entity_type_code AND tenant_id = p_tenant_id AND deleted = false;
END;
$$;

CREATE OR REPLACE FUNCTION dynamicbusiness._seed_five_w_who_slot(
  p_tenant_id bigint,
  p_entity_type_code text,
  p_column_kind text,
  p_slot_ref text,
  p_perspective_id text,
  p_enabled boolean,
  p_context_outputs jsonb,
  p_entity_id_rule text,
  p_category_column jsonb
) RETURNS void
LANGUAGE plpgsql
AS $$
BEGIN
  UPDATE dm_five_w_who_layout
  SET enabled = p_enabled,
      context_outputs = p_context_outputs,
      entity_id_rule = p_entity_id_rule,
      category_column = p_category_column,
      perspective_id = p_perspective_id,
      deleted = false,
      updater = 'seed',
      update_time = CURRENT_TIMESTAMP
  WHERE tenant_id = p_tenant_id
    AND entity_type_code = p_entity_type_code
    AND column_kind = p_column_kind
    AND COALESCE(perspective_id, '') = COALESCE(p_perspective_id, '')
    AND COALESCE(slot_ref, '') = COALESCE(p_slot_ref, '')
    AND deleted = false;

  IF NOT FOUND THEN
    INSERT INTO dm_five_w_who_layout (
      entity_type_code, column_kind, slot_ref, perspective_id, props_id,
      enabled, context_outputs, entity_id_rule, category_column,
      tenant_id, creator, deleted
    ) VALUES (
      p_entity_type_code, p_column_kind, p_slot_ref, p_perspective_id, NULL,
      p_enabled, p_context_outputs, p_entity_id_rule, p_category_column,
      p_tenant_id, 'seed', false
    );
  END IF;
END;
$$;

CREATE OR REPLACE FUNCTION dynamicbusiness._seed_five_w_semantic(
  p_tenant_id bigint,
  p_entity_type_code text,
  p_selection_level text,
  p_what_mode text,
  p_what_config jsonb,
  p_how_mode text DEFAULT 'NONE',
  p_how_config jsonb DEFAULT '{}'::jsonb
) RETURNS void
LANGUAGE plpgsql
AS $$
BEGIN
  UPDATE dm_five_w_orchestration
  SET enabled = true,
      selection_level = p_selection_level,
      what_mode = p_what_mode,
      what_config = p_what_config,
      how_mode = p_how_mode,
      how_config = p_how_config,
      deleted = false,
      updater = 'seed',
      update_time = CURRENT_TIMESTAMP
  WHERE entity_type_code = p_entity_type_code AND tenant_id = p_tenant_id;

  IF NOT FOUND THEN
    INSERT INTO dm_five_w_orchestration (
      entity_type_code, enabled, selection_level, what_mode, what_config,
      how_mode, how_config, tenant_id, creator, deleted
    ) VALUES (
      p_entity_type_code, true, p_selection_level, p_what_mode, p_what_config,
      p_how_mode, p_how_config, p_tenant_id, 'seed', false
    );
  END IF;
END;
$$;

CREATE OR REPLACE FUNCTION dynamicbusiness._seed_five_w_replace_who_slots(
  p_tenant_id bigint,
  p_entity_type_code text
) RETURNS void
LANGUAGE plpgsql
AS $$
BEGIN
  UPDATE dm_five_w_who_layout
  SET deleted = true, updater = 'seed-replace', update_time = CURRENT_TIMESTAMP
  WHERE entity_type_code = p_entity_type_code AND tenant_id = p_tenant_id AND deleted = false;
END;
$$;

-- 台账三列：分类 + 型号 + 实体
CREATE OR REPLACE FUNCTION dynamicbusiness._seed_five_w_recipe_ledger_3col(
  p_tenant_id bigint,
  p_registry_code text,
  p_category_type_code text,
  p_category_label text DEFAULT NULL,
  p_selection_level text DEFAULT 'ENTITY',
  p_what_mode text DEFAULT 'VIEW_DETAIL',
  p_what_config jsonb DEFAULT '{"bindLayer":"ENTITY"}'::jsonb,
  p_how_mode text DEFAULT 'NONE',
  p_how_config jsonb DEFAULT '{}'::jsonb
) RETURNS void
LANGUAGE plpgsql
AS $$
DECLARE
  v_label text := COALESCE(NULLIF(trim(p_category_label), ''), p_registry_code);
BEGIN
  PERFORM dynamicbusiness._seed_five_w_semantic(
    p_tenant_id, p_registry_code, p_selection_level, p_what_mode, p_what_config, p_how_mode, p_how_config);
  PERFORM dynamicbusiness._seed_five_w_replace_who_slots(p_tenant_id, p_registry_code);
  PERFORM dynamicbusiness._seed_five_w_replace_filter_slots(p_tenant_id, p_registry_code);
  PERFORM dynamicbusiness._seed_five_w_filter_slot(
    p_tenant_id, p_registry_code, p_registry_code || '-category', NULL,
    true, NULL,
    jsonb_build_object('label', v_label || '分类', 'categoryTypeCode', p_category_type_code));
  PERFORM dynamicbusiness._seed_five_w_who_slot(
    p_tenant_id, p_registry_code, 'MODEL', p_registry_code || '-model', NULL,
    true, '["modelId"]'::jsonb, NULL, NULL);
  PERFORM dynamicbusiness._seed_five_w_who_slot(
    p_tenant_id, p_registry_code, 'ENTITY', p_registry_code || '-entity', NULL,
    true, '["entityId"]'::jsonb, 'rowSelection', NULL);
END;
$$;

-- 分类 + 实体（无型号栏）
CREATE OR REPLACE FUNCTION dynamicbusiness._seed_five_w_recipe_category_entity_2col(
  p_tenant_id bigint,
  p_registry_code text,
  p_category_type_code text,
  p_category_label text DEFAULT NULL,
  p_selection_level text DEFAULT 'ENTITY',
  p_what_mode text DEFAULT 'VIEW_DETAIL',
  p_what_config jsonb DEFAULT '{"bindLayer":"ENTITY"}'::jsonb,
  p_how_mode text DEFAULT 'NONE',
  p_how_config jsonb DEFAULT '{}'::jsonb
) RETURNS void
LANGUAGE plpgsql
AS $$
DECLARE
  v_label text := COALESCE(NULLIF(trim(p_category_label), ''), p_registry_code);
BEGIN
  PERFORM dynamicbusiness._seed_five_w_semantic(
    p_tenant_id, p_registry_code, p_selection_level, p_what_mode, p_what_config, p_how_mode, p_how_config);
  PERFORM dynamicbusiness._seed_five_w_replace_who_slots(p_tenant_id, p_registry_code);
  PERFORM dynamicbusiness._seed_five_w_replace_filter_slots(p_tenant_id, p_registry_code);
  PERFORM dynamicbusiness._seed_five_w_filter_slot(
    p_tenant_id, p_registry_code, p_registry_code || '-category', NULL,
    true, NULL,
    jsonb_build_object('label', v_label, 'categoryTypeCode', p_category_type_code));
  PERFORM dynamicbusiness._seed_five_w_who_slot(
    p_tenant_id, p_registry_code, 'ENTITY', p_registry_code || '-entity', NULL,
    true, '["entityId"]'::jsonb, 'rowSelection', NULL);
END;
$$;

-- 分类树节点即实体（region / department）
CREATE OR REPLACE FUNCTION dynamicbusiness._seed_five_w_recipe_category_linked_entity(
  p_tenant_id bigint,
  p_registry_code text,
  p_category_type_code text,
  p_category_label text
) RETURNS void
LANGUAGE plpgsql
AS $$
BEGIN
  PERFORM dynamicbusiness._seed_five_w_semantic(
    p_tenant_id, p_registry_code, 'ENTITY', 'VIEW_DETAIL', '{"bindLayer":"ENTITY"}'::jsonb);
  PERFORM dynamicbusiness._seed_five_w_replace_who_slots(p_tenant_id, p_registry_code);
  PERFORM dynamicbusiness._seed_five_w_replace_filter_slots(p_tenant_id, p_registry_code);
  PERFORM dynamicbusiness._seed_five_w_filter_slot(
    p_tenant_id, p_registry_code, p_registry_code || '-tree', NULL,
    true, 'categoryLinkedEntity',
    jsonb_build_object('label', p_category_label, 'categoryTypeCode', p_category_type_code));
END;
$$;
