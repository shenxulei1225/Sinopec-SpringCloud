-- 动作目录按标准模型区分四类可执行动作与动作组。
-- 模型是创建类型的唯一权威；action_domain 只保留为跨模块执行快照投影。
-- 动作组只认真实成员结构。历史 is_composite 不再参与判断。
SET search_path TO dynamicbusiness, public;

INSERT INTO dynamic_model (
    code, name, entity_type_code, domain, description, status, sort,
    field_groups_config, tenant_id, creator, governance_status
)
SELECT
    v.code,
    v.name,
    'action',
    NULL,
    v.description,
    1,
    v.sort,
    source.field_groups_config,
    1,
    'flyway-v155',
    COALESCE(source.governance_status, 'COMPANY')
FROM (
    VALUES
        ('action_device', '设备动作', '由执行设备完成。具体协议支持、指令和字段映射只在指令协议对接中维护。', 10),
        ('action_intelligent_analysis', '智能分析动作', '调用智能分析能力目录中的能力，不参加设备协议对接。', 20),
        ('action_manual', '人工动作', '由人员执行和确认，不参加设备协议对接。', 30),
        ('action_platform', '平台动作', '由平台服务执行，不参加设备协议对接。', 40),
        ('action_group', '动作组', '按顺序引用可执行动作；动作组自身不执行，生成任务时展开成员。', 50)
) AS v(code, name, description, sort)
CROSS JOIN LATERAL (
    SELECT field_groups_config, governance_status
    FROM dynamic_model
    WHERE tenant_id = 1
      AND entity_type_code = 'action'
      AND deleted = FALSE
    ORDER BY CASE WHEN code = 'action' THEN 0 ELSE 1 END, id
    LIMIT 1
) source
ON CONFLICT (code, tenant_id) WHERE deleted = FALSE
DO UPDATE SET
    name = EXCLUDED.name,
    description = EXCLUDED.description,
    status = 1,
    sort = EXCLUDED.sort,
    governance_status = EXCLUDED.governance_status,
    deleted = FALSE,
    updater = 'flyway-v155',
    update_time = CURRENT_TIMESTAMP;

-- 普通动作继续定义协议无关的业务参数。智能分析动作另带能力编号。
-- 动作组只保存步骤树；child_action_ids_json 仅留作本次迁移读取，不再分配给模型。
INSERT INTO dynamic_model_field_assignment (
    model_id, field_id, model_code, field_code, required,
    is_searchable, is_filterable, is_sortable, sort, field_source,
    tenant_id, creator
)
SELECT
    m.id,
    f.id,
    m.code,
    f.code,
    CASE
        WHEN m.code = 'action_intelligent_analysis' AND f.code = 'analysis_capability_id' THEN TRUE
        ELSE FALSE
    END,
    FALSE,
    FALSE,
    FALSE,
    cfg.sort,
    'BASE',
    1,
    'flyway-v155'
FROM dynamic_model m
JOIN (
    VALUES
        ('action_device', 'execution_means', 10),
        ('action_device', 'param_slots_json', 20),
        ('action_intelligent_analysis', 'execution_means', 10),
        ('action_intelligent_analysis', 'analysis_capability_id', 12),
        ('action_intelligent_analysis', 'param_slots_json', 20),
        ('action_manual', 'execution_means', 10),
        ('action_manual', 'param_slots_json', 20),
        ('action_platform', 'execution_means', 10),
        ('action_platform', 'param_slots_json', 20),
        ('action_group', 'execution_means', 10),
        ('action_group', 'action_tree_json', 20)
) AS cfg(model_code, field_code, sort)
  ON cfg.model_code = m.code
JOIN dynamic_field f
  ON f.tenant_id = 1
 AND f.code = cfg.field_code
 AND f.deleted = FALSE
WHERE m.tenant_id = 1
  AND m.entity_type_code = 'action'
  AND m.deleted = FALSE
ON CONFLICT (tenant_id, model_code, field_code) WHERE deleted = FALSE
DO UPDATE SET
    model_id = EXCLUDED.model_id,
    field_id = EXCLUDED.field_id,
    required = EXCLUDED.required,
    sort = EXCLUDED.sort,
    field_source = EXCLUDED.field_source,
    deleted = FALSE,
    updater = 'flyway-v155',
    update_time = CURRENT_TIMESTAMP;

-- 新模型沿用动作目录已有分类入口，避免模型存在但无法在当前分类中创建。
INSERT INTO dynamic_model_category_relation_t1 (
    model_id, category_id, entity_type_code, sort, creator, tenant_id,
    model_code, category_code, deleted
)
SELECT
    target.id,
    relation.category_id,
    'action',
    target.sort,
    'flyway-v155',
    1,
    target.code,
    relation.category_code,
    FALSE
FROM dynamic_model_category_relation_t1 relation
JOIN dynamic_model source
  ON source.id = relation.model_id
 AND source.tenant_id = 1
 AND source.entity_type_code = 'action'
JOIN dynamic_model target
  ON target.tenant_id = 1
 AND target.entity_type_code = 'action'
 AND target.code IN (
    'action_device',
    'action_intelligent_analysis',
    'action_manual',
    'action_platform',
    'action_group'
 )
 AND target.deleted = FALSE
WHERE relation.tenant_id = 1
  AND relation.deleted = FALSE
  AND source.code = 'action'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = FALSE
DO UPDATE SET
    model_id = EXCLUDED.model_id,
    category_id = EXCLUDED.category_id,
    sort = EXCLUDED.sort,
    deleted = FALSE,
    updater = 'flyway-v155',
    update_time = CURRENT_TIMESTAMP;

-- 有真实成员的记录迁为动作组。空成员记录按 action_domain 迁到可执行动作模型。
UPDATE ent_action_t1 action
SET model_id = model.id,
    action_domain = NULL,
    analysis_capability_id = NULL,
    action_tree_json = jsonb_build_object(
        'version', 1,
        'members', (
            SELECT jsonb_agg(
                jsonb_build_object(
                    'memberKey', action.code || ':' || member.ordinality,
                    'actionCode', member.action_code,
                    'order', member.ordinality - 1,
                    'params', '{}'::jsonb,
                    'required', TRUE
                )
                ORDER BY member.ordinality
            )
            FROM jsonb_array_elements_text(action.child_action_ids_json)
                WITH ORDINALITY AS member(action_code, ordinality)
        )
    ),
    updater = 'flyway-v155',
    update_time = CURRENT_TIMESTAMP
FROM dynamic_model model
WHERE action.deleted = FALSE
  AND action.tenant_id = 1
  AND jsonb_typeof(COALESCE(action.child_action_ids_json, '[]'::jsonb)) = 'array'
  AND jsonb_array_length(COALESCE(action.child_action_ids_json, '[]'::jsonb)) > 0
  AND model.tenant_id = 1
  AND model.code = 'action_group'
  AND model.deleted = FALSE;

UPDATE ent_action_t1 action
SET model_id = model.id,
    updater = 'flyway-v155',
    update_time = CURRENT_TIMESTAMP
FROM dynamic_model model
WHERE action.deleted = FALSE
  AND action.tenant_id = 1
  AND (
      action.child_action_ids_json IS NULL
      OR (
          jsonb_typeof(action.child_action_ids_json) = 'array'
          AND jsonb_array_length(action.child_action_ids_json) = 0
      )
  )
  AND model.tenant_id = 1
  AND model.deleted = FALSE
  AND model.code = CASE action.action_domain
      WHEN 'DEVICE' THEN 'action_device'
      WHEN 'INTELLIGENT_ANALYSIS' THEN 'action_intelligent_analysis'
      WHEN 'MANUAL' THEN 'action_manual'
      WHEN 'PLATFORM' THEN 'action_platform'
      ELSE '__invalid_action_model__'
  END;

-- 旧布尔字段已经与真实成员结构发生冲突。停止展示和模型分配，但保留物理列供回滚核对。
UPDATE dynamic_entity_type_base_field
SET deleted = TRUE,
    status = 0,
    updater = 'flyway-v155',
    update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1
  AND entity_type_code = 'action'
  AND field_code IN ('is_composite', 'child_action_ids_json')
  AND deleted = FALSE;

UPDATE dynamic_entity_type_base_field
SET required = FALSE,
    description = '由动作模型派生的执行投影。用户不直接维护。',
    type_config = '{"createVisible":false,"editVisible":false,"detailVisible":false}'::jsonb,
    updater = 'flyway-v155',
    update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1
  AND entity_type_code = 'action'
  AND field_code = 'action_domain'
  AND deleted = FALSE;

UPDATE dynamic_entity_type_base_field
SET type_config = '{"createVisible":true,"editVisible":true,"detailVisible":true}'::jsonb,
    updater = 'flyway-v155',
    update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1
  AND entity_type_code = 'action'
  AND field_code = 'analysis_capability_id'
  AND deleted = FALSE;

UPDATE dynamic_model_field_assignment
SET deleted = TRUE,
    updater = 'flyway-v155',
    update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1
  AND model_code = 'action'
  AND deleted = FALSE;

-- 协议参与方式只在指令协议对接维护。动作行不再保留第二份协议判断。
UPDATE ent_action_t1
SET custom_fields = COALESCE(custom_fields, '{}'::jsonb) - 'protocol_dispatch_role',
    updater = 'flyway-v155',
    update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1
  AND deleted = FALSE
  AND COALESCE(custom_fields, '{}'::jsonb) ? 'protocol_dispatch_role';

UPDATE dynamic_entity_type_base_field
SET deleted = TRUE,
    status = 0,
    updater = 'flyway-v155',
    update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1
  AND entity_type_code = 'action'
  AND field_code = 'protocol_dispatch_role'
  AND deleted = FALSE;

UPDATE dynamic_model
SET deleted = TRUE,
    status = 0,
    updater = 'flyway-v155',
    update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1
  AND entity_type_code = 'action'
  AND code = 'action'
  AND deleted = FALSE;

COMMENT ON COLUMN ent_action.action_domain IS
    '由动作模型派生的执行快照投影；动作组为空。用户不直接维护。';
COMMENT ON COLUMN ent_action.is_composite IS
    '历史列，V155 起不参与运行时判断。动作组只认 model_id 与 action_tree_json。';
