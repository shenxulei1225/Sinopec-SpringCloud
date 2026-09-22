-- ============================================================================
-- action · 01 字段库：param_slots_json / child_action_ids_json / is_composite
-- 前置：Flyway V76（ent_action 固定列）；V91 起 param_slots_json 语义为「动作参数定义」
-- 定稿：docs/动态业务/宿主SOP参数包与动作参数定稿.md
-- 禁止：把到达位置等执行参数挂成新建表单字段（参数只进 param_slots_json）
-- 适用手段 execution_means 是字段库枚举词表；动作详情勾选编码数组，不进新建弹窗
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
    ('is_composite', '是否复合动作', 'BOOLEAN', 'true=开跑时按 child_action_ids_json 展开'),
    ('execution_means', '适用手段', 'ENUM', '执行方式词表。选项身份是编码；动作/检查项/任务/设备都读这一份。')
) AS v(code, name, type, description)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

UPDATE dynamic_field
SET
  deleted = false,
  status = 1,
  type = 'ENUM',
  name = '适用手段',
  description = '执行方式词表。选项身份是编码；动作/检查项/任务/设备都读这一份。',
  options = COALESCE(
    NULLIF(options, ''),
    '[{"label":"人工","value":"MANUAL"},{"label":"无人机","value":"UAV"},{"label":"机器人","value":"ROBOT"},{"label":"固定摄像机","value":"FIXED_CAMERA"}]'
  ),
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1
  AND code = 'execution_means';
