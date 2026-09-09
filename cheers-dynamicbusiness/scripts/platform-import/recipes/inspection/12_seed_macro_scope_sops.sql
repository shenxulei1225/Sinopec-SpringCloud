-- ============================================================================
-- recipes/inspection · 12 按“大范围”初始化 SOP（手工执行）
--
-- 目标：不再按“检查项一条一个 SOP”建库，改为按业务范围建标准 SOP。
-- 本脚本初始化三条示例：
--   - 储罐检查标准流程
--   - 生产工艺检查标准流程
--   - 安防检查标准流程
--
-- 同步写入：
--   1) ent_sop_t1：SOP 主表
--   2) dynamic_entity_category_relation_t1：SOP 分类挂接
--   3) dynamic_sop_scope_rule：适用范围（CATEGORY）
--   4) dynamic_sop_item_pack：标准检查项包（按关键词抓取示例项）
-- ============================================================================

SET search_path TO dynamicbusiness, public;

DO $$
DECLARE
  v_tenant CONSTANT bigint := 1;
  v_sop_model_id bigint;
BEGIN
  SELECT id INTO v_sop_model_id
  FROM dynamic_model
  WHERE deleted = FALSE
    AND tenant_id = v_tenant
    AND code = 'sop'
  ORDER BY id
  LIMIT 1;

  IF v_sop_model_id IS NULL THEN
    RAISE EXCEPTION '缺少 SOP 型号（dynamic_model.code=sop），请先执行 scripts/platform-import/sop/import.sh';
  END IF;

  -- A) 先恢复/更新，再补插入（幂等）
  WITH seed AS (
    SELECT *
    FROM (VALUES
      ('SOP-STD-TANK-INSPECTION', '储罐检查标准流程', 'inspection', 'cat-sop-inspection'),
      ('SOP-STD-PROCESS-INSPECTION', '生产工艺检查标准流程', 'inspection', 'cat-sop-production'),
      ('SOP-STD-SECURITY-INSPECTION', '安防检查标准流程', 'inspection', 'cat-sop-inspection')
    ) AS t(code, name, domain, sop_category_code)
  )
  UPDATE ent_sop_t1 s
  SET deleted = FALSE,
      name = seed.name,
      domain = seed.domain,
      entity_type_code = 'sop',
      model_id = v_sop_model_id,
      status = 1,
      version_no = COALESCE(s.version_no, 1),
      publish_status = COALESCE(NULLIF(s.publish_status, ''), 'DRAFT'),
      is_template = FALSE,
      sop_template_id = NULL,
      action_tree_json = COALESCE(s.action_tree_json, '[]'::jsonb),
      default_params_by_node_json = COALESCE(s.default_params_by_node_json, '{}'::jsonb),
      creator = COALESCE(s.creator, 'recipe-inspection-seed'),
      updater = 'recipe-inspection-seed',
      update_time = CURRENT_TIMESTAMP
  FROM seed
  WHERE s.tenant_id = v_tenant
    AND s.code = seed.code;

  INSERT INTO ent_sop_t1 (
    tenant_id, entity_type_code, model_id, domain,
    name, code, status,
    version_no, publish_status,
    is_template, sop_template_id,
    action_tree_json, default_params_by_node_json,
    default_steps_json, default_params_json,
    creator, create_time, updater, update_time, deleted
  )
  SELECT
    v_tenant,
    'sop',
    v_sop_model_id,
    seed.domain,
    seed.name,
    seed.code,
    1,
    1,
    'DRAFT',
    FALSE,
    NULL,
    '[]'::jsonb,
    '{}'::jsonb,
    '[]'::jsonb,
    '{}'::jsonb,
    'recipe-inspection-seed',
    CURRENT_TIMESTAMP,
    'recipe-inspection-seed',
    CURRENT_TIMESTAMP,
    FALSE
  FROM (
    SELECT *
    FROM (VALUES
      ('SOP-STD-TANK-INSPECTION', '储罐检查标准流程', 'inspection', 'cat-sop-inspection'),
      ('SOP-STD-PROCESS-INSPECTION', '生产工艺检查标准流程', 'inspection', 'cat-sop-production'),
      ('SOP-STD-SECURITY-INSPECTION', '安防检查标准流程', 'inspection', 'cat-sop-inspection')
    ) AS t(code, name, domain, sop_category_code)
  ) seed
  WHERE NOT EXISTS (
    SELECT 1
    FROM ent_sop_t1 s
    WHERE s.tenant_id = v_tenant
      AND s.code = seed.code
      AND s.deleted = FALSE
  );

  -- B) SOP 分类挂接（先删后重建，避免重复/脏关系）
  WITH sop_rows AS (
    SELECT id, code
    FROM ent_sop_t1
    WHERE tenant_id = v_tenant
      AND deleted = FALSE
      AND code IN (
        'SOP-STD-TANK-INSPECTION',
        'SOP-STD-PROCESS-INSPECTION',
        'SOP-STD-SECURITY-INSPECTION'
      )
  )
  DELETE FROM dynamic_entity_category_relation_t1 r
  USING sop_rows s
  WHERE r.tenant_id = v_tenant
    AND r.deleted = FALSE
    AND r.entity_type_code = 'sop'
    AND r.entity_id = s.id;

  INSERT INTO dynamic_entity_category_relation_t1 (
    tenant_id, category_id, entity_type_code, entity_id, domain, sort, creator, deleted
  )
  SELECT
    v_tenant,
    cat.id,
    'sop',
    sop.id,
    NULL,
    0,
    'recipe-inspection-seed',
    FALSE
  FROM (
    VALUES
      ('SOP-STD-TANK-INSPECTION', 'cat-sop-inspection'),
      ('SOP-STD-PROCESS-INSPECTION', 'cat-sop-production'),
      ('SOP-STD-SECURITY-INSPECTION', 'cat-sop-inspection')
  ) seed(sop_code, sop_category_code)
  JOIN ent_sop_t1 sop
    ON sop.tenant_id = v_tenant
   AND sop.deleted = FALSE
   AND sop.code = seed.sop_code
  JOIN dynamic_category cat
    ON cat.tenant_id = v_tenant
   AND cat.deleted = FALSE
   AND cat.code = seed.sop_category_code
   AND cat.category_type_code = 'sop';

  -- C) 标准包：先删再按关键词写入
  DELETE FROM dynamic_sop_scope_rule
  WHERE tenant_id = v_tenant
    AND deleted = FALSE
    AND sop_id IN (
      SELECT id
      FROM ent_sop_t1
      WHERE tenant_id = v_tenant
        AND deleted = FALSE
        AND code IN (
          'SOP-STD-TANK-INSPECTION',
          'SOP-STD-PROCESS-INSPECTION',
          'SOP-STD-SECURITY-INSPECTION'
        )
    );

  DELETE FROM dynamic_sop_item_pack
  WHERE tenant_id = v_tenant
    AND deleted = FALSE
    AND sop_id IN (
      SELECT id
      FROM ent_sop_t1
      WHERE tenant_id = v_tenant
        AND deleted = FALSE
        AND code IN (
          'SOP-STD-TANK-INSPECTION',
          'SOP-STD-PROCESS-INSPECTION',
          'SOP-STD-SECURITY-INSPECTION'
        )
    );

  -- C1) 适用范围：按设备分类关键词抓一个代表分类
  INSERT INTO dynamic_sop_scope_rule (
    tenant_id, sop_id, scope_type, target_id, sort_no, note,
    creator, create_time, updater, update_time, deleted
  )
  SELECT
    v_tenant,
    sop.id,
    'CATEGORY',
    target.id,
    10,
    'seed: 按关键词自动匹配设备分类',
    'recipe-inspection-seed',
    CURRENT_TIMESTAMP,
    'recipe-inspection-seed',
    CURRENT_TIMESTAMP,
    FALSE
  FROM (
    VALUES
      ('SOP-STD-TANK-INSPECTION', '%储罐%'),
      ('SOP-STD-PROCESS-INSPECTION', '%工艺%'),
      ('SOP-STD-SECURITY-INSPECTION', '%安防%')
  ) seed(sop_code, eq_category_name_like)
  JOIN ent_sop_t1 sop
    ON sop.tenant_id = v_tenant
   AND sop.deleted = FALSE
   AND sop.code = seed.sop_code
  JOIN LATERAL (
    SELECT c.id
    FROM dynamic_category c
    WHERE c.tenant_id = v_tenant
      AND c.deleted = FALSE
      AND c.category_type_code = 'equipment'
      AND c.name LIKE seed.eq_category_name_like
    ORDER BY c.id
    LIMIT 1
  ) target ON TRUE;

  -- C2) 标准检查项包：按名称关键词抓示例项（每类最多 8 条）
  INSERT INTO dynamic_sop_item_pack (
    tenant_id, sop_id, inspection_item_id, required, sort_no, note,
    creator, create_time, updater, update_time, deleted
  )
  SELECT
    v_tenant,
    sop.id,
    items.item_id,
    TRUE,
    items.sort_no,
    'seed: 按关键词自动匹配检查项',
    'recipe-inspection-seed',
    CURRENT_TIMESTAMP,
    'recipe-inspection-seed',
    CURRENT_TIMESTAMP,
    FALSE
  FROM (
    VALUES
      ('SOP-STD-TANK-INSPECTION', ARRAY['%储罐%','%液位%','%泄漏%']::text[]),
      ('SOP-STD-PROCESS-INSPECTION', ARRAY['%工艺%','%阀%','%管线%']::text[]),
      ('SOP-STD-SECURITY-INSPECTION', ARRAY['%安防%','%报警%','%摄像%','%门禁%']::text[])
  ) seed(sop_code, patterns)
  JOIN ent_sop_t1 sop
    ON sop.tenant_id = v_tenant
   AND sop.deleted = FALSE
   AND sop.code = seed.sop_code
  JOIN LATERAL (
    SELECT
      i.id AS item_id,
      ROW_NUMBER() OVER (ORDER BY i.id) * 10 AS sort_no
    FROM ent_inspection_item_t1 i
    WHERE i.tenant_id = v_tenant
      AND i.deleted = FALSE
      AND EXISTS (
        SELECT 1
        FROM unnest(seed.patterns) p
        WHERE i.name LIKE p
      )
    ORDER BY i.id
    LIMIT 8
  ) items ON TRUE;

  RAISE NOTICE 'seed macro SOPs done: 储罐/生产工艺/安防 三类标准流程已初始化';
END $$;
