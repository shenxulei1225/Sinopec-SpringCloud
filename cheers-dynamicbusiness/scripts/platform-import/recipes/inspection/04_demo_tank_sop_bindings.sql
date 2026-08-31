-- ============================================================================
-- recipes/inspection · 04 储罐巡检演示：UAV 方法行 + 设备×检查项实例绑定
--
-- 场景：任务创建选「北/南罐组储罐」+ 原油储罐 6 项检查（2352–2357），
--       MANUAL / UAV 均可解析出 ready，路线规划不再因 SOP 全缺口卡住。
-- 写入：dynamic_sop_method_binding_t1、ent_sop_t1（实例）、dynamic_sop_instance_binding_t1
-- creator = recipe-inspection-demo，便于 remove.sql 幂等清理
-- ============================================================================

SET search_path TO dynamicbusiness;

DO $$
DECLARE
  v_tenant CONSTANT bigint := 1;
  v_creator CONSTANT text := 'recipe-inspection-demo';
  v_host_type CONSTANT text := 'equipment';
  v_subject_type CONSTANT text := 'inspection_item';
  v_dim_key CONSTANT text := 'execution_means';
  rec record;
  v_inst_id bigint;
  v_inst_code text;
BEGIN
  IF to_regclass('dynamicbusiness.dynamic_sop_method_binding_t1') IS NULL THEN
    RAISE NOTICE 'skip 04: dynamic_sop_method_binding_t1 不存在（需 Flyway V80）';
    RETURN;
  END IF;
  IF to_regclass('dynamicbusiness.dynamic_sop_instance_binding_t1') IS NULL THEN
    RAISE NOTICE 'skip 04: dynamic_sop_instance_binding_t1 不存在';
    RETURN;
  END IF;
  IF to_regclass('dynamicbusiness.ent_sop_t1') IS NULL THEN
    RAISE NOTICE 'skip 04: ent_sop_t1 不存在';
    RETURN;
  END IF;

  -- 1) 原油储罐 6 项：补 UAV 方法行（与既有 MANUAL 共用同一模板）
  INSERT INTO dynamic_sop_method_binding_t1 (
    tenant_id, subject_type, subject_id,
    dimension_key, dimension_value, sop_template_id,
    creator, create_time, updater, update_time, deleted
  )
  SELECT
    v_tenant,
    v_subject_type,
    mb.subject_id,
    v_dim_key,
    'UAV',
    mb.sop_template_id,
    v_creator,
    NOW(),
    v_creator,
    NOW(),
    false
  FROM dynamic_sop_method_binding_t1 mb
  WHERE mb.tenant_id = v_tenant
    AND mb.deleted = false
    AND mb.subject_type = v_subject_type
    AND mb.dimension_key = v_dim_key
    AND mb.dimension_value = 'MANUAL'
    AND mb.subject_id IN (2352, 2353, 2354, 2355, 2356, 2357)
    AND NOT EXISTS (
      SELECT 1
      FROM dynamic_sop_method_binding_t1 x
      WHERE x.tenant_id = v_tenant
        AND x.deleted = false
        AND x.subject_type = v_subject_type
        AND x.subject_id = mb.subject_id
        AND x.dimension_key = v_dim_key
        AND x.dimension_value = 'UAV'
    );

  -- 2) 演示设备 × 6 检查项 × MANUAL/UAV：建独占实例 + 实例绑定
  FOR rec IN
    SELECT
      eq.id AS equipment_id,
      eq.name AS equipment_name,
      item.id AS item_id,
      item.name AS item_name,
      means.means AS dimension_value,
      mb.sop_template_id AS template_id,
      tpl.name AS template_name,
      tpl.model_id,
      tpl.domain
    FROM ent_equipment_t1 eq
    CROSS JOIN (
      SELECT unnest(ARRAY[2352, 2353, 2354, 2355, 2356, 2357])::bigint AS id
    ) item_ids
    JOIN ent_inspection_item_t1 item
      ON item.id = item_ids.id
     AND item.deleted = false
     AND item.tenant_id = v_tenant
    CROSS JOIN (
      SELECT unnest(ARRAY['MANUAL', 'UAV'])::text AS means
    ) means
    JOIN dynamic_sop_method_binding_t1 mb
      ON mb.tenant_id = v_tenant
     AND mb.deleted = false
     AND mb.subject_type = v_subject_type
     AND mb.subject_id = item.id
     AND mb.dimension_key = v_dim_key
     AND mb.dimension_value = means.means
    JOIN ent_sop_t1 tpl
      ON tpl.id = mb.sop_template_id
     AND tpl.deleted = false
     AND tpl.tenant_id = v_tenant
     AND tpl.is_template = true
    WHERE eq.deleted = false
      AND eq.tenant_id = v_tenant
      AND eq.id IN (900104, 900114)
  LOOP
    v_inst_code := format(
      'SOP-DEMO-%s-%s-%s',
      rec.equipment_id,
      rec.item_id,
      rec.dimension_value
    );

    SELECT s.id
      INTO v_inst_id
      FROM ent_sop_t1 s
     WHERE s.tenant_id = v_tenant
       AND s.deleted = false
       AND s.code = v_inst_code
     LIMIT 1;

    IF v_inst_id IS NULL THEN
      INSERT INTO ent_sop_t1 (
        tenant_id, entity_type_code, model_id, domain,
        name, code, status,
        version_no, publish_status,
        is_template, sop_template_id,
        action_tree_json, default_params_by_node_json,
        action_tree_override_json, param_override_json,
        step_override_json, default_steps_json, default_params_json,
        steps_json, execution_means,
        creator, create_time, updater, update_time, deleted
      )
      VALUES (
        v_tenant,
        'sop',
        rec.model_id,
        COALESCE(rec.domain, 'inspection'),
        rec.template_name || ' · ' || rec.equipment_name,
        v_inst_code,
        1,
        1,
        'DRAFT',
        false,
        rec.template_id,
        '[]'::jsonb,
        '{}'::jsonb,
        NULL,
        NULL,
        NULL,
        '[]'::jsonb,
        '{}'::jsonb,
        '[]'::jsonb,
        rec.dimension_value,
        v_creator,
        NOW(),
        v_creator,
        NOW(),
        false
      )
      RETURNING id INTO v_inst_id;
    END IF;

    INSERT INTO dynamic_sop_instance_binding_t1 (
      tenant_id, host_type, host_id,
      subject_type, subject_id,
      dimension_key, dimension_value,
      sop_instance_id,
      creator, create_time, updater, update_time, deleted
    )
    SELECT
      v_tenant,
      v_host_type,
      rec.equipment_id,
      v_subject_type,
      rec.item_id,
      v_dim_key,
      rec.dimension_value,
      v_inst_id,
      v_creator,
      NOW(),
      v_creator,
      NOW(),
      false
    WHERE NOT EXISTS (
      SELECT 1
      FROM dynamic_sop_instance_binding_t1 b
      WHERE b.tenant_id = v_tenant
        AND b.deleted = false
        AND b.host_type = v_host_type
        AND b.host_id = rec.equipment_id
        AND b.subject_type = v_subject_type
        AND b.subject_id = rec.item_id
        AND b.dimension_key = v_dim_key
        AND b.dimension_value = rec.dimension_value
    );
  END LOOP;

  RAISE NOTICE 'recipes/inspection 04: 储罐演示 UAV 方法 + 实例绑定已写入（北1# / 南10# × 6 项 × MANUAL/UAV）';
END $$;
