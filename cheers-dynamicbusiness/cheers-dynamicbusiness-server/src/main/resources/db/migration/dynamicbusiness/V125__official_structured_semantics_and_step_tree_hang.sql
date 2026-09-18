-- V125: 结构化列正式用途收口 + 步骤树挂载写进目录配置 + 能力插件清单对齐
--
-- 负责：一次性把历史别名/列名用途改成平台清单；把目录字段 type_config 收成 jsonb；
--       给已知目录写下挂载；登记步骤树/协议解析插件。
-- 不负责：把动作编码列表 JSON 在读路径改写成步骤节点。

SET search_path TO dynamicbusiness, public;

-- 1) 字段库用途收口（只改 semantic_type，不改物理列名）
UPDATE dynamic_field
SET semantic_type = 'STEP_TREE',
    updater = 'flyway-v125',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND (
    semantic_type IN ('ACTION_TREE', 'INSPECTION_SOP_HOW', 'step_tree_json', 'action_tree_json')
    OR (code IN ('step_tree_json', 'action_tree_json') AND (semantic_type IS NULL OR btrim(semantic_type) = ''))
  );

UPDATE dynamic_field
SET semantic_type = 'PARAM_SCHEMA',
    updater = 'flyway-v125',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND (
    semantic_type IN ('ACTION_PARAM_SCHEMA', 'param_slots_json')
    OR (code = 'param_slots_json' AND (semantic_type IS NULL OR btrim(semantic_type) = ''))
  );

UPDATE dynamic_field
SET semantic_type = 'FLOW_GRAPH',
    updater = 'flyway-v125',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND (
    semantic_type IN ('SOP_FLOW_GRAPH', 'flow_graph_json')
    OR (code = 'flow_graph_json' AND (semantic_type IS NULL OR btrim(semantic_type) = ''))
  );

UPDATE dynamic_field
SET semantic_type = 'FIELD_DESCRIPTION',
    updater = 'flyway-v125',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND (
    semantic_type IN ('field_description_json')
    OR (code = 'field_description_json' AND (semantic_type IS NULL OR btrim(semantic_type) = ''))
  );

UPDATE dynamic_field
SET semantic_type = 'COMMAND_JSON',
    updater = 'flyway-v125',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND (
    semantic_type IN ('command_json')
    OR (code = 'command_json' AND (semantic_type IS NULL OR btrim(semantic_type) = ''))
  );

UPDATE dynamic_field
SET semantic_type = 'SAMPLE_JSON',
    updater = 'flyway-v125',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND (
    semantic_type IN ('sample_json', 'sample_messages_json')
    OR (code IN ('sample_json', 'sample_messages_json') AND (semantic_type IS NULL OR btrim(semantic_type) = ''))
  );

-- 2) type_config 是目录字段规则文档（可见性、挂载等），按键合并，列类型收成 jsonb。
--    空串/空值保持 NULL，不发明 '{}'; 非空必须是合法 JSON。
ALTER TABLE dynamic_entity_type_base_field
  ALTER COLUMN type_config TYPE jsonb
  USING (
    CASE
      WHEN type_config IS NULL OR btrim(type_config) = '' THEN NULL
      ELSE type_config::jsonb
    END
  );

COMMENT ON COLUMN dynamic_entity_type_base_field.type_config IS
  '目录字段规则文档（jsonb）：可见性、数值边界、步骤树挂载等；不是散文。';

-- 3) 已知目录写下步骤树挂载（存盘形态是写入时记下的，不是读路径猜列名）
UPDATE dynamic_entity_type_base_field bf
SET type_config = jsonb_strip_nulls(
      COALESCE(bf.type_config, '{}'::jsonb)
      || '{"createVisible":false,"editVisible":false,"detailVisible":false}'::jsonb
      || jsonb_build_object(
           'hangableTypeCodes', '["action"]'::jsonb,
           'allowMixed', false,
           'editorKind', 'child_action_tree'
         )
    ),
    updater = 'flyway-v125',
    update_time = CURRENT_TIMESTAMP
WHERE bf.deleted = false
  AND bf.entity_type_code = 'action'
  AND bf.field_code IN ('action_tree_json', 'step_tree_json');

UPDATE dynamic_entity_type_base_field bf
SET type_config = jsonb_strip_nulls(
      COALESCE(bf.type_config, '{}'::jsonb)
      || '{"createVisible":false,"editVisible":false,"detailVisible":false}'::jsonb
      || jsonb_build_object(
           'hangableTypeCodes', '["action"]'::jsonb,
           'allowMixed', false,
           'editorKind', 'methods_by_means'
         )
    ),
    updater = 'flyway-v125',
    update_time = CURRENT_TIMESTAMP
WHERE bf.deleted = false
  AND bf.entity_type_code = 'inspection_item'
  AND bf.field_code IN ('step_tree_json', 'action_tree_json');

UPDATE dynamic_entity_type_base_field bf
SET type_config = jsonb_strip_nulls(
      COALESCE(bf.type_config, '{}'::jsonb)
      || '{"createVisible":false,"editVisible":false,"detailVisible":false}'::jsonb
      || jsonb_build_object(
           'hangableTypeCodes', '["inspection_item"]'::jsonb,
           'allowMixed', false,
           'editorKind', 'step_nodes'
         )
    ),
    updater = 'flyway-v125',
    update_time = CURRENT_TIMESTAMP
WHERE bf.deleted = false
  AND bf.entity_type_code = 'sop'
  AND bf.field_code IN ('step_tree_json', 'action_tree_json');

-- 4) 已知目录补能力开关（不覆盖已有行）
INSERT INTO dynamic_entity_type_capability (
  tenant_id, entity_type_code, capability_code, enabled, deleted, creator, create_time, updater, update_time
)
SELECT et.tenant_id, et.code, cap.capability_code, TRUE, FALSE,
       'flyway-v125', CURRENT_TIMESTAMP, 'flyway-v125', CURRENT_TIMESTAMP
FROM dynamic_entity_type et
JOIN (
  VALUES
    ('action', 'step-structure'),
    ('action', 'param-schema'),
    ('inspection_item', 'step-structure'),
    ('sop', 'step-structure'),
    ('sop', 'flow-graph'),
    ('data_protocol', 'protocol-parser')
) AS cap(entity_type_code, capability_code)
  ON lower(et.code) = cap.entity_type_code
WHERE et.deleted = false
  AND NOT EXISTS (
    SELECT 1
    FROM dynamic_entity_type_capability existing
    WHERE existing.deleted = false
      AND existing.tenant_id = et.tenant_id
      AND existing.entity_type_code = et.code
      AND existing.capability_code = cap.capability_code
  );

-- 5) 插件清单：步骤树、协议解析；动作/检查改为依赖步骤树且不再认领树用途
UPDATE dynamic_business_plugin_manifest
SET
  depends_on = '["step-tree"]'::jsonb,
  semantic_contributions = '["PARAM_SCHEMA"]'::jsonb,
  updater = 'flyway-v125',
  update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND plugin_id = 'action-library';

UPDATE dynamic_business_plugin_manifest
SET
  depends_on = '["step-tree"]'::jsonb,
  semantic_contributions = '[]'::jsonb,
  updater = 'flyway-v125',
  update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND plugin_id = 'inspection-library';

INSERT INTO dynamic_business_plugin_manifest (
  tenant_id, plugin_id, version, enabled, platform_range, entry,
  depends_on, permissions, semantic_contributions,
  creator, create_time, updater, update_time, deleted
)
SELECT
  t.tenant_id, 'step-tree', '1.0.0', TRUE, '>=1.0.0', 'framework/step-tree/plugin/manifest.ts',
  '[]'::jsonb,
  '["structured-fields:register"]'::jsonb,
  '["STEP_TREE"]'::jsonb,
  'flyway-v125', CURRENT_TIMESTAMP, 'flyway-v125', CURRENT_TIMESTAMP, FALSE
FROM (SELECT DISTINCT tenant_id FROM dynamic_business_plugin_manifest WHERE deleted = false) t
WHERE NOT EXISTS (
  SELECT 1
  FROM dynamic_business_plugin_manifest existing
  WHERE existing.deleted = false
    AND existing.tenant_id = t.tenant_id
    AND existing.plugin_id = 'step-tree'
);

INSERT INTO dynamic_business_plugin_manifest (
  tenant_id, plugin_id, version, enabled, platform_range, entry,
  depends_on, permissions, semantic_contributions,
  creator, create_time, updater, update_time, deleted
)
SELECT
  t.tenant_id, 'protocol-parser', '1.0.0', TRUE, '>=1.0.0', 'features/protocol-management/plugin/manifest.ts',
  '[]'::jsonb,
  '["work-blocks:detail","structured-fields:register"]'::jsonb,
  '["FIELD_DESCRIPTION","COMMAND_JSON","SAMPLE_JSON"]'::jsonb,
  'flyway-v125', CURRENT_TIMESTAMP, 'flyway-v125', CURRENT_TIMESTAMP, FALSE
FROM (SELECT DISTINCT tenant_id FROM dynamic_business_plugin_manifest WHERE deleted = false) t
WHERE NOT EXISTS (
  SELECT 1
  FROM dynamic_business_plugin_manifest existing
  WHERE existing.deleted = false
    AND existing.tenant_id = t.tenant_id
    AND existing.plugin_id = 'protocol-parser'
);

-- 6) 清型号表单缓存，让正式用途与挂载进入下一轮投影
DELETE FROM model_crud_form_definition
WHERE entity_type_code IN ('action', 'inspection_item', 'sop', 'data_protocol');
