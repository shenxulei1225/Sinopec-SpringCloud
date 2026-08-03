-- ============================================================================
-- inspection-method · 13 目录收口：检查方法迁回知识库
-- 纠正误标为「检查内容」且挂在「站场管理」的 inspection_method
-- 定稿：知识库并列「标准检查内容库」+「检查方法」；二者均为 NETWORK；内容经 method_template_id 绑方法
-- ============================================================================

SET search_path TO dynamicbusiness;

-- 确保 ENTITY_TYPE 分组「知识库」存在（幂等）
INSERT INTO dynamic_group (
  group_type, code, name, description, parent_id, sort, status,
  tenant_id, creator, deleted
)
SELECT
  'ENTITY_TYPE',
  'ETG-KNOWLEDGE-BASE',
  '知识库',
  '标准检查内容、检查方法等全网知识对象',
  NULL,
  15,
  1,
  1,
  'seed',
  false
WHERE NOT EXISTS (
  SELECT 1 FROM dynamic_group g
  WHERE g.deleted = false AND g.tenant_id = 1
    AND g.group_type = 'ENTITY_TYPE' AND g.name = '知识库'
);

UPDATE dynamic_entity_type
SET
  name = '检查方法',
  alias = '检查方法',
  group_name = '知识库',
  work_scope = 'NETWORK',
  entry_kind = 'NATIVE',
  base_entity_type_code = NULL,
  domain = NULL,
  description = COALESCE(
    NULLIF(btrim(description), ''),
    '全网通用标准检查方法（模板/实例同表）；标准库只展示 is_template=true；由检查内容 method_template_id 引用'
  ),
  status = 'active',
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND code = 'inspection_method';

UPDATE dynamic_entity_type_config
SET
  name = '检查方法',
  description = COALESCE(
    NULLIF(btrim(description), ''),
    '检查方法专用表；固定列 is_template / action_duration_sec'
  ),
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND entity_type_code = 'inspection_method';
