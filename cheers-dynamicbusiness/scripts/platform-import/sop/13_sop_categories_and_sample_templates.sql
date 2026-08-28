-- ============================================================================
-- sop · 13 SOP 分类子树 + 步骤模板实体 + 新模型示例模板
-- 前置：V73–V74、10–12 seed
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

-- 步骤模板实体（code 与 merge 契约一致）
INSERT INTO ent_sop_step_template_t1 (
  tenant_id, entity_type_code, model_id, name, code, status,
  param_slots_json, creator, deleted
)
SELECT
  1,
  'sop_step_template',
  m.id,
  v.name,
  v.code,
  1,
  v.param_slots_json::jsonb,
  'seed',
  false
FROM dynamic_model m
CROSS JOIN (
  VALUES
    ('st-arrive', '到达作业位置', '["location_ref"]'),
    ('st-hover', '悬停观察', '[]'),
    ('st-aim', '对准拍摄', '["yaw", "pitch"]'),
    ('st-shoot', '拍摄取证', '[]'),
    ('st-camera-view', '固定机位查看', '["camera_preset_id"]')
) AS v(code, name, param_slots_json)
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'sop_step_template'
  AND NOT EXISTS (
    SELECT 1 FROM ent_sop_step_template_t1 s
    WHERE s.deleted = false AND s.tenant_id = 1 AND s.code = v.code
  );

-- 新模型示例 SOP 模板（default_steps_json；挂「检查」分类）
INSERT INTO ent_sop_t1 (
  tenant_id, entity_type_code, model_id, name, code, status, domain,
  version_no, publish_status,
  is_template, execution_means, procedure_kind,
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
  v.default_steps_json::jsonb,
  v.default_params_json::jsonb,
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
        {"stepTemplateId":"st-arrive","order":1,"paramSlots":["location_ref"]},
        {"stepTemplateId":"st-hover","order":2,"paramSlots":[]},
        {"stepTemplateId":"st-aim","order":3,"paramSlots":["yaw","pitch"]},
        {"stepTemplateId":"st-shoot","order":4,"paramSlots":[]}
      ]',
      '{"yaw":0,"pitch":0}'
    ),
    (
      'SOP-TPL-MANUAL-LEAK',
      '人工·泄漏检查',
      'MANUAL',
      'leak',
      '[
        {"stepTemplateId":"st-arrive","order":1,"paramSlots":["location_ref"]},
        {"stepTemplateId":"st-aim","order":2,"paramSlots":["yaw","pitch"]}
      ]',
      '{"yaw":0,"pitch":0}'
    )
) AS v(code, name, execution_means, procedure_kind, default_steps_json, default_params_json)
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'sop'
  AND NOT EXISTS (
    SELECT 1 FROM ent_sop_t1 s
    WHERE s.deleted = false AND s.tenant_id = 1 AND s.code = v.code
  );

-- 挂「检查」分类
INSERT INTO dynamic_category_entity_link_t1 (
  tenant_id, category_id, entity_type_code, entity_id, entity_model_id, creator, deleted
)
SELECT
  1,
  cat.id,
  'sop',
  s.id,
  m.id,
  'seed',
  false
FROM dynamic_category cat
JOIN ent_sop_t1 s
  ON s.deleted = false AND s.tenant_id = 1
  AND s.code IN ('SOP-TPL-UAV-LEAK', 'SOP-TPL-MANUAL-LEAK')
JOIN dynamic_model m
  ON m.deleted = false AND m.tenant_id = 1 AND m.code = 'sop'
WHERE cat.deleted = false AND cat.tenant_id = 1 AND cat.code = 'cat-sop-inspection'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_category_entity_link_t1 l
    WHERE l.deleted = false
      AND l.tenant_id = 1
      AND l.category_id = cat.id
      AND l.entity_type_code = 'sop'
      AND l.entity_id = s.id
  );
