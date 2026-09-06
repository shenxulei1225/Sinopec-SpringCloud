-- ============================================================================
-- sop · 13 SOP 分类子树 + 动作树示例模板（不再插入步骤模板）
-- 前置：V76–V78、action seed（act-*）、12 seed（含 action_tree_* 字段）
-- ============================================================================

SET search_path TO dynamicbusiness;

-- SOP 库内业务分类（检查 / 生产 / 应急 / 抢维）
INSERT INTO dynamic_category (
  code, name, category_type_code, parent_id, status, sort, tenant_id, creator, deleted
)
SELECT v.code, v.name, 'sop', p.id, 1, v.sort, 1, 'seed', false
FROM (
  VALUES
    ('cat-sop-inspection', '检查', 10),
    ('cat-sop-production', '生产', 20),
    ('cat-sop-emergency', '应急', 30),
    ('cat-sop-rush-repair', '抢维', 40)
) AS v(code, name, sort)
JOIN dynamic_category p
  ON p.deleted = false AND p.tenant_id = 1 AND p.code = 'sop_root'
WHERE NOT EXISTS (
  SELECT 1 FROM dynamic_category c
  WHERE c.deleted = false AND c.tenant_id = 1 AND c.code = v.code
);

-- 新模型示例 SOP 模板（action_tree_json + default_params_by_node_json；挂「检查」分类）
INSERT INTO ent_sop_t1 (
  tenant_id, entity_type_code, model_id, name, code, status, domain,
  version_no, publish_status,
  is_template, execution_means, procedure_kind,
  action_tree_json, default_params_by_node_json,
  default_steps_json, default_params_json,
  steps_json,
  creator, deleted
)
SELECT
  1,
  'sop',
  m.id,
  v.name,
  v.code,
  1,
  'inspection',
  1,
  'PUBLISHED',
  true,
  v.execution_means,
  v.procedure_kind,
  v.action_tree_json::jsonb,
  v.default_params_by_node_json::jsonb,
  '[]'::jsonb,
  '{}'::jsonb,
  '[]'::jsonb,
  'seed',
  false
FROM dynamic_model m
CROSS JOIN (
  VALUES
    (
      'SOP-TPL-UAV-LEAK',
      '无人机·泄漏检查',
      'UAV',
      'leak',
      '[
        {"nodeKey":"n-1","actionId":"act-arrive","order":1,"paramSlots":["location_ref"]},
        {"nodeKey":"n-2","actionId":"act-hover","order":2,"paramSlots":[]},
        {"nodeKey":"n-3","actionId":"act-aim","order":3,"paramSlots":["yaw","pitch","roll","focal_length"]},
        {"nodeKey":"n-4","actionId":"act-shoot","order":4,"paramSlots":["shot_count"]}
      ]',
      '{
        "n-1":{"location_ref":"point-default"},
        "n-3":{"yaw":0,"pitch":0},
        "n-4":{"shot_count":3}
      }'
    ),
    (
      'SOP-TPL-MANUAL-LEAK',
      '人工·泄漏检查',
      'MANUAL',
      'leak',
      '[
        {"nodeKey":"n-1","actionId":"act-arrive","order":1,"paramSlots":["location_ref"]},
        {"nodeKey":"n-2","actionId":"act-aim","order":2,"paramSlots":["yaw","pitch","roll","focal_length"]}
      ]',
      '{
        "n-1":{"location_ref":"point-default"},
        "n-2":{"yaw":0,"pitch":0}
      }'
    )
) AS v(code, name, execution_means, procedure_kind, action_tree_json, default_params_by_node_json)
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'sop'
  AND NOT EXISTS (
    SELECT 1 FROM ent_sop_t1 s
    WHERE s.deleted = false AND s.tenant_id = 1 AND s.code = v.code
  );

-- 已有旧行：若仍只有 default_steps_json、动作树为空，补写动作树（幂等；与 V78 迁移同语义）
UPDATE ent_sop_t1 s
SET action_tree_json = v.action_tree_json::jsonb,
    default_params_by_node_json = v.default_params_by_node_json::jsonb,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM (
  VALUES
    (
      'SOP-TPL-UAV-LEAK',
      '[
        {"nodeKey":"n-1","actionId":"act-arrive","order":1,"paramSlots":["location_ref"]},
        {"nodeKey":"n-2","actionId":"act-hover","order":2,"paramSlots":[]},
        {"nodeKey":"n-3","actionId":"act-aim","order":3,"paramSlots":["yaw","pitch","roll","focal_length"]},
        {"nodeKey":"n-4","actionId":"act-shoot","order":4,"paramSlots":["shot_count"]}
      ]',
      '{
        "n-1":{"location_ref":"point-default"},
        "n-3":{"yaw":0,"pitch":0},
        "n-4":{"shot_count":3}
      }'
    ),
    (
      'SOP-TPL-MANUAL-LEAK',
      '[
        {"nodeKey":"n-1","actionId":"act-arrive","order":1,"paramSlots":["location_ref"]},
        {"nodeKey":"n-2","actionId":"act-aim","order":2,"paramSlots":["yaw","pitch","roll","focal_length"]}
      ]',
      '{
        "n-1":{"location_ref":"point-default"},
        "n-2":{"yaw":0,"pitch":0}
      }'
    )
) AS v(code, action_tree_json, default_params_by_node_json)
WHERE s.deleted = false AND s.tenant_id = 1 AND s.code = v.code
  AND (
    s.action_tree_json IS NULL
    OR s.action_tree_json = '[]'::jsonb
    OR jsonb_array_length(s.action_tree_json) = 0
  );

-- 挂「检查」分类：SOP 种类是简单分类，写分类–实体，禁止写分类–台账绑定
INSERT INTO dynamic_entity_category_relation_t1 (
  tenant_id, category_id, entity_type_code, entity_id, domain, sort, creator, deleted
)
SELECT
  1,
  cat.id,
  'sop',
  s.id,
  NULL,
  0,
  'seed',
  false
FROM dynamic_category cat
JOIN ent_sop_t1 s
  ON s.deleted = false AND s.tenant_id = 1
  AND s.code IN ('SOP-TPL-UAV-LEAK', 'SOP-TPL-MANUAL-LEAK')
WHERE cat.deleted = false AND cat.tenant_id = 1 AND cat.code = 'cat-sop-inspection'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_entity_category_relation_t1 r
    WHERE r.tenant_id = 1
      AND r.category_id = cat.id
      AND r.entity_type_code = 'sop'
      AND r.entity_id = s.id
  );

UPDATE dynamic_category_entity_link_t1 l
SET deleted = true, updater = 'seed', update_time = CURRENT_TIMESTAMP
FROM dynamic_category cat
WHERE l.deleted = false
  AND l.tenant_id = 1
  AND l.entity_type_code = 'sop'
  AND l.category_id = cat.id
  AND cat.deleted = false
  AND cat.tenant_id = 1
  AND cat.code = 'cat-sop-inspection';
