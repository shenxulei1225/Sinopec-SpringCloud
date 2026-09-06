-- ============================================================================
-- action · 01 字段库：param_slots_json / child_action_ids_json / is_composite
-- 前置：Flyway V76（ent_action 固定列）；V91 起 param_slots_json 语义为「动作参数定义」
-- 定稿：docs/动态业务/宿主SOP参数包与动作参数定稿.md
-- 禁止：把 execution_means / 到达位置等执行参数挂成动作实体字段（参数只进 param_slots_json）
-- ============================================================================

SET search_path TO dynamicbusiness;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
)
SELECT
  v.code, v.name, v.type, NULL, v.description, 'BASE', 1, 1, 'NONE',
  NULL, NULL, v.code, 1, 'seed'
FROM (
  VALUES
    (
      'param_slots_json',
      '动作参数定义',
      'JSON',
      '动作参数定义（复用列 param_slots_json）。外形 {version,fields[{fieldCode,required,defaultValue}]}；兼容旧数据：JSON 字符串数组视为 fieldCode 列表'
    ),
    ('child_action_ids_json', '子动作列表', 'TEXT', '复合动作的有序子动作 code 列表 JSON 数组'),
    ('is_composite', '是否复合动作', 'BOOLEAN', 'true=开跑时按 child_action_ids_json 展开')
) AS v(code, name, type, description)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

-- 历史：execution_means 曾作动作基础字段；现改为不挂类型（物理列可留）。软删字段库行避免再被挂回。
UPDATE dynamic_field
SET
  deleted = true,
  status = 0,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND tenant_id = 1
  AND code = 'execution_means';
