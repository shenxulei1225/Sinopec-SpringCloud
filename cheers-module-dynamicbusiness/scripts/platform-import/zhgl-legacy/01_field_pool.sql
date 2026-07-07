-- ============================================================================
-- ZHGL 遗留 · 01 字段池与分组
-- Generated: 2026-07-08 by scripts/export-zhgl-seed.py
-- Source: PostgreSQL database `zhgl_import_temp` ← zhgl_20260401_132252.backup
--
-- 约定：不写 surrogate id；幂等键为 code / (group_type, code) / (model_code, field_code)。
-- 依赖：dynamicbusiness schema 已初始化
-- 含 dynamic_field、dynamic_group(FIELD)、dynamic_group_relation
-- ============================================================================

SET search_path TO dynamicbusiness;

-- dynamic_field: 205 row(s), upsert by code

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-001e86fbdfe7468eb822e9330abfb06d', '关联测建表', 'REF_Multi',
  NULL, '系统自动创建的关联字段，用于被其他业务类型关联到测建表',
  'SYSTEM', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-0054227dff284d18be8cb7a59c9f9208', '描述', 'LONG_TEXT',
  NULL, NULL,
  'USER', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-018f18b9116c4e7a8abe7878145aa98b', '关联巡检管理', 'REF_Multi',
  NULL, '系统自动创建的关联字段，用于被其他业务类型关联到巡检管理',
  'SYSTEM', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-024b45dee6d948c7b703cd33a787d81a', '产品图片', 'IMAGE',
  NULL, NULL,
  'CUSTOM', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-03c491bf9ef745c3859042ebbc8ba350', '管线地面有无塌陷', 'BOOLEAN',
  NULL, NULL,
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-0436a31b31dc4b4d8a5314177e254312', 'SLA截止', 'DATETIME',
  NULL, 'SLA 约定的最晚完成时间',
  'USER', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-048afd8b2fef478a8174b77a5daa6cff', '防火区编码', 'STRING',
  NULL, NULL,
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-06cf3dcb998f4c4392cb6a5a67fa1289', '镜头类型', 'ENUM',
  NULL, '摄像机镜头类型',
  'USER', 1,
  NULL, 'GIN',
  '[{"label":"定焦","value":"fixed"},{"label":"变焦","value":"zoom"},{"label":"电动变焦","value":"motorized_zoom"}]', NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-07de4c93dbf44a7ab7f0ab74f4393115', '建成日期', 'DATE',
  NULL, NULL,
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-08a164e893064fc090f611deca4654ab', '所属管廊段', 'REFERENCE',
  NULL, NULL,
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-0970197515b249e1b6ba44002abb11e8', '底径长度', 'STRING',
  NULL, NULL,
  'CUSTOM', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-0b1047d4121b4b23af936cf6e6f06b7b', '附件', 'LONG_TEXT',
  NULL, '附件列表（JSON或文本）',
  'USER', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-0b5a1e6027a44d7381d4be263c4d83c6', '外观是否完好', 'BOOLEAN',
  NULL, NULL,
  'CUSTOM', 1,
  NULL, 'GIN',
  '[{"label":"是","value":"是"},{"label":"否","value":"否"}]', NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-0d3b7aafc63c4c1ebfd8dc6681d0bd41', '时长', 'INTEGER',
  'min', NULL,
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-0ddaa2df8e4645fcbb71d0a425e25e5e', '管廊区间', 'TEXT',
  NULL, NULL,
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-1011bb8c2edb4ee38e7502fb1435b591', '防火区起点路名', 'TEXT',
  NULL, NULL,
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-154c668b39c14dffb5565cd44817549b', '氧气浓度', 'FLOAT',
  '%', NULL,
  'CUSTOM', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-18af5bff1c894e2da694e5fa35d8c7af', '设备编号', 'TEXT',
  NULL, '设备唯一标识编号',
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-18d0ce546f434b9e96b799d1ade2b46b', '联系方式', 'STRING',
  NULL, NULL,
  'CUSTOM', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-1920066551d944b3b7fd8b0fc7e1c094', '宽度', 'DECIMAL',
  '米', NULL,
  'USER', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-1ae635196ab64cd7b30de03e2c1ddb9c', '测试通用字段', 'STRING',
  NULL, NULL,
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-1d3bcec7a5df4ff985610db69a2c467b', '报修时间', 'DATETIME',
  NULL, '报修时间（ISO）',
  'USER', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-1d6369c7d59a4598b2346df632babed9', '关联设备故障', 'REF_Multi',
  NULL, '系统自动创建的关联字段，用于被其他业务类型关联到设备故障',
  'SYSTEM', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-1d9efb587ce0496aa8a967ebd7216627', '接单人', 'TEXT',
  NULL, '实际接单处理人',
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-1ee037ca81bb475f94b3526134ea85bb', '工作温度', 'NUMBER',
  '℃', NULL,
  'USER', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-2018852d847d4ae3a29d143ef78a921a', '咨询业务详情', 'TEXT',
  NULL, NULL,
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-209f806919c0492b97ee9a8aa98aaee9', '处理班组', 'ENUM',
  NULL, '班组/外委单位（示例枚举）',
  'USER', 1,
  NULL, 'GIN',
  '[{"label": "电力运维班", "value": "power_team"}, {"label": "环控运维班", "value": "hvac_team"}, {"label": "消防维保（外委）", "value": "fire_vendor"}, {"label": "通信运维班", "value": "comm_team"}, {"label": "综合运维班", "value": "general_team"}]', NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-224b4af4e1474d5d85002c03d0bdf89a', '转单原因', 'LONG_TEXT',
  NULL, '转单原因说明',
  'USER', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-2407b65c556643adb72f94c0efbbba47', '容量', 'NUMBER',
  'L', NULL,
  'USER', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-24442601845a4c7eac78f96b037da45c', '所在路段', 'TEXT',
  NULL, NULL,
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-2459441906934083b0a9379196bc1772', '结束时间', 'DATE',
  NULL, NULL,
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-24c6fd9ee4b54789825ed96a82dd49b5', '舱室类型', 'STRING',
  NULL, NULL,
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-254fc29f33e0408096351d37ca55afd1', '人员', 'BATCH_ENTITY_REF',
  NULL, NULL,
  'CUSTOM', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-26bab227792c44fe8c8c4d6bb6037f3e', '阀门有无锈蚀渗漏', 'BOOLEAN',
  NULL, NULL,
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-28655458c4bc41d680b78576cc9cfd70', '用途', 'TEXT',
  NULL, NULL,
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-2908d7224a2341e39f413e4905c6a46a', '宽度（毫米）', 'INTEGER',
  NULL, NULL,
  'USER', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-2feb012ccc244e35913d38f901d107f8', '面积', 'FLOAT',
  '㎡', NULL,
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-3071c55d68ac4e03872d9758512aa17e', '故障描述', 'LONG_TEXT',
  NULL, '故障现象、影响范围等详细描述',
  'USER', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-31cd60a2e6194fa5903b92a873af8e1f', '测试', 'BOOLEAN',
  NULL, '哦哦',
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-31d7dc332d2846c9837fb0d7f9e2ec51', '工作记录', 'LONG_TEXT',
  NULL, '维修过程中的详细工作记录',
  'USER', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-32c5283caa6a4388a3c2b3e69e68f1a8', '关联应急资源', 'REF_Multi',
  NULL, '系统自动创建的关联字段，用于被其他业务类型关联到应急资源',
  'SYSTEM', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-3775e93edf70456db41c72ecc15d5c9b', '照明设备数量', 'INTEGER',
  NULL, NULL,
  'USER', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-39ab7f71e1d945fcbe6af9c4fbae1b25', '工时(小时)', 'NUMBER',
  '小时', '实际工时统计',
  'USER', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-3c6a384288b84d4e9086206ff2673eb3', '协作人员', 'LONG_TEXT',
  NULL, '协作处理人员列表（JSON或文本）',
  'USER', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-3d1012a56e8e448aa096bae0f353c21a', '关联备件管理', 'REF_Multi',
  NULL, '系统自动创建的关联字段，用于被其他业务类型关联到备件管理',
  'SYSTEM', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-3d37caf40b634e3c9527bba283d92622', '升级原因', 'LONG_TEXT',
  NULL, '紧急程度升级的原因说明',
  'USER', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-3d7fedf4c352424abeb1b11805dfd331', '精度', 'NUMBER',
  '%', NULL,
  'USER', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-3dc0dc43a3894d698a38c2550ce47fdb', '根因分析', 'LONG_TEXT',
  NULL, '根因、复发原因、整改建议',
  'USER', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-3fc2a30d4e4a4114878b4f77c331e76f', '敷设方式', 'ENUM',
  NULL, NULL,
  'USER', 1,
  NULL, 'GIN',
  '[{"label": "直埋", "value": "直埋"}, {"label": "架空", "value": "架空"}, {"label": "管廊", "value": "管廊"}]', NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-40617730a826424faa4b40ccd8a58414', '派工人', 'TEXT',
  NULL, '派工操作人',
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-40d48dc5caec46ac9e482f03f8102f59', '客户', 'ENTITY_REF',
  NULL, NULL,
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-433ed7705e5f476fa193a26b642d43ab', '存放坐标', 'COORDINATE',
  NULL, NULL,
  'CUSTOM', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-43ba10972c084c44a24a22ffb0b2185f', '电流', 'NUMBER',
  'A', NULL,
  'USER', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-443c8879f02b479e8af045a80c9fdcca', '故障位置', 'TEXT',
  NULL, '设备位置/区域/管廊段',
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-44fa8c983fa04bdba625ae21bed55ebe', '所在路名', 'STRING',
  NULL, NULL,
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-4651095e38ca4f1cbe8cb32beb8c2142', '测量精度', 'NUMBER',
  '%', '传感器测量精度百分比',
  'USER', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-49523f29afa94650ac591f607043dd65', '压力等级', 'NUMBER',
  'MPa', NULL,
  'USER', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-49e264c6d87f4498b3c8e110f423b3a0', '排水泵数量', 'INTEGER',
  NULL, NULL,
  'USER', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-4a5dd27e3302418cb54352c0f95dd473', '关联啊实打实', 'REF_Multi',
  NULL, '系统自动创建的关联字段，用于被其他业务类型关联到啊实打实',
  'SYSTEM', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-4b75b7f9889d4f1d90afd19c03c934af', '运行状态', 'ENUM',
  NULL, '设备当前运行状态',
  'USER', 1,
  NULL, 'GIN',
  '[{"label":"运行中","value":"running"},{"label":"停用","value":"stopped"},{"label":"维修中","value":"maintenance"},{"label":"故障","value":"fault"}]', NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-4c2787cd47d74621a3a8868c9bad729b', '舱间间隔（毫米）', 'INTEGER',
  NULL, NULL,
  'USER', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-4d6aa265bbff4ed69227aa623f9dae8f', '电压等级', 'NUMBER',
  'kV', NULL,
  'USER', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-4f3b40defe104f1b94c3462c568bfb79', '报修人', 'TEXT',
  NULL, '报修人姓名/账号',
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-51b7d63aa97942439611cff6370fe65e', '管径', 'NUMBER',
  'mm', NULL,
  'USER', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-524fd7c27947494384391f83aabbb9e6', '转单目标', 'TEXT',
  NULL, '转单后的接单人/班组',
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-559d5ad091e644a798aa9efef8d06c9c', '测量范围', 'TEXT',
  NULL, '传感器测量范围（如：0-100%、-40~85℃）',
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-57d6bdf06b284821bd003d0fdba16157', '材质', 'ENUM',
  NULL, NULL,
  'USER', 1,
  NULL, 'GIN',
  '[{"label": "铜", "value": "铜"}, {"label": "铝", "value": "铝"}, {"label": "其他", "value": "其他"}]', NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-58b6dbbc32f1438b96188087aa68ae4e', '安装日期', 'DATE',
  NULL, '设备安装日期',
  'USER', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-59e3752f5d954a818b71effb661ed0a0', '舱室配置', 'TEXT',
  NULL, NULL,
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-59e5799b2eaa4b0aa9c164f8d440409b', '检测范围', 'NUMBER',
  'ppm', NULL,
  'USER', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-5aa8a20200ca4227962015c4ea17e09f', '标准段覆土深度', 'DECIMAL',
  '米', NULL,
  'USER', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-5b561c0ac2fe41d59a7b1831a93d1637', '品牌', 'TEXT',
  NULL, '设备品牌/厂商',
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-5bcfff95f7a74c38ab5af0dd75c12e1c', '关联管线管理', 'REF_Multi',
  NULL, '系统自动创建的关联字段，用于被其他业务类型关联到管线管理',
  'SYSTEM', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-5bf95bdfcf4b459a84aa21b76a3ad965', '本段实际长度', 'DECIMAL',
  '米', NULL,
  'USER', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-5c4fbd6a48ca457ba09a97c4ccea8e71', '满意度', 'ENUM',
  NULL, '回访/评价',
  'USER', 1,
  NULL, 'GIN',
  '[{"label": "1-非常不满意", "value": "1"}, {"label": "2-不满意", "value": "2"}, {"label": "3-一般", "value": "3"}, {"label": "4-满意", "value": "4"}, {"label": "5-非常满意", "value": "5"}]', NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-5d200e3b62354fc9854878532336ec63', '关联数据点', 'REF_Multi',
  NULL, '系统自动创建的关联字段，用于被其他业务类型关联到数据点',
  'SYSTEM', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-5d2f22057f0f4b50b6b0e24d2dc86a66', '距离路中心距离', 'DECIMAL',
  '米', NULL,
  'USER', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-5e33e7804dd746958c4f8c97203b1f16', '职务', 'ENUM',
  NULL, '用于队伍管理的职务选项',
  'CUSTOM', 1,
  NULL, 'GIN',
  '[{"label":"队长","value":"队长"},{"label":"副队长","value":"副队长"},{"label":"成员","value":"成员"}]', NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-6116465add0949b593f1dcd3f2b6e29d', '挂起原因', 'LONG_TEXT',
  NULL, '挂起原因（等待备件/等待许可/天气等）',
  'USER', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-6174ace4a9dc43d3a8ee84f6d9a2ceef', '路线规划', 'BATCH_ENTITY_REF',
  NULL, NULL,
  'CUSTOM', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-64a9042c3a464ffda9040119e4c8f0af', '起始桩号', 'TEXT',
  NULL, NULL,
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-652ef6ef44c247958f9d937722aa4e18', '高度（毫米）', 'INTEGER',
  NULL, NULL,
  'USER', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-65e1d5ebc4e4442d9483aa3f4313f7a9', '验收结果', 'ENUM',
  NULL, '验收结果',
  'USER', 1,
  NULL, 'GIN',
  '[{"label": "通过", "value": "passed"}, {"label": "不通过", "value": "rejected"}, {"label": "待验收", "value": "pending"}]', NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-66264ae122c54504a8fa0679079772fc', '分辨率', 'ENUM',
  NULL, '摄像机分辨率',
  'USER', 1,
  NULL, 'GIN',
  '[{"label":"720P","value":"720p"},{"label":"1080P","value":"1080p"},{"label":"2K","value":"2k"},{"label":"4K","value":"4k"},{"label":"8K","value":"8k"}]', NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-66324f09cb7e4913a6af86b1e4f7c7b7', '墙体厚度（毫米）', 'INTEGER',
  NULL, NULL,
  'USER', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-672e3aca3c9c46cb8f0c8fe85217b708', '供电方式', 'ENUM',
  NULL, NULL,
  'USER', 1,
  NULL, 'GIN',
  '[{"label": "AC", "value": "AC"}, {"label": "DC", "value": "DC"}, {"label": "电池", "value": "电池"}]', NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-67aeb9609c7945cfaa3f5806e27e6d90', '排序号', 'INTEGER',
  NULL, NULL,
  'USER', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-67e85d7f261c43b1b99dab1cc0a8885a', '收费标准', 'FLOAT',
  '元/(人·天)', NULL,
  'CUSTOM', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-684f5feb7c5d4988871dcf14ad06812f', '特殊说明', 'LONG_TEXT',
  NULL, NULL,
  'USER', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-68ef79eb88454c8fa11a1e9baa987d88', '湿度记录', 'FLOAT',
  '%', NULL,
  'CUSTOM', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-6bb56dc7ac014d1ea68632a1ac606dfc', '防火分区数量', 'INTEGER',
  NULL, NULL,
  'USER', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-6bd033593d1d4c3b98b82fab6fa2d866', '关联路线管理', 'REF_Multi',
  NULL, '系统自动创建的关联字段，用于被其他业务类型关联到路线管理',
  'SYSTEM', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-6c74f28ef78e4a6d84566f97e072b7cf', '关联点位管理', 'REF_Multi',
  NULL, '系统自动创建的关联字段，用于被其他业务类型关联到点位管理',
  'SYSTEM', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-6e001ac5256943b6840f5b9a3a17ac0b', '转单来源', 'TEXT',
  NULL, '转单前的接单人/班组',
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-6e612c0931ad42b3b308898469497eec', '定位线距离道路中心线', 'DECIMAL',
  '米', NULL,
  'USER', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-72697688b36e4399b25121108ef76b9a', '名称', 'STRING',
  NULL, NULL,
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-737b95fd043748b3a565edbd6735733e', '管廊段标号', 'TEXT',
  NULL, NULL,
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-771506dcac4d4ee08dc3e39ab5cf3a18', '防火区终点路名', 'TEXT',
  NULL, NULL,
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-778eb98265914d7b8e65932a1aa0c355', '埋地深度（毫米）', 'INTEGER',
  NULL, NULL,
  'USER', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-7afc7657a00849c99087fad813f904b6', '舱室数量', 'INTEGER',
  NULL, NULL,
  'USER', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-7b89b48f8a854b72af3e686857e118f5', '关联测测1', 'REF_Multi',
  NULL, '系统自动创建的关联字段模板,用于关联测测1',
  'SYSTEM', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-7c115d3b6ced4f4d924ef114e12ada27', '温度记录', 'FLOAT',
  '℃', NULL,
  'CUSTOM', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-7c6e9af941d04fcfbb36dbe80134f119', '现场照片', 'LONG_TEXT',
  NULL, '现场照片URL列表（JSON或文本）',
  'USER', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-7c79907d01cc44e5bafe7fae273a336c', '截面积', 'NUMBER',
  'mm²', NULL,
  'USER', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-7e858ec039604fd5841722ddf3d3176a', '关联设备', 'ENTITY_REF',
  NULL, '关联的设备实体',
  'USER', 1,
  NULL, 'GIN',
  '["equipment"]', NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-8a103735cdc646d8a454e98913c1a6b4', '房间卫生是否达标', 'BOOLEAN',
  NULL, NULL,
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-8ab9c1773cb94097aeeaa4427115f30e', '管廊类型', 'STRING',
  NULL, NULL,
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-8ae17b99e1b44e8dbacbb68645bf4d7c', '长度显示', 'TEXT',
  NULL, NULL,
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-8bf33d80787a411cb398f74c54b7843b', '关联告警', 'ENTITY_REF',
  NULL, '关联的告警实体',
  'USER', 1,
  NULL, 'GIN',
  '["alarm"]', NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-8c030a9ea7434ae293cdabc3e379c1c2', '企业地址', 'TEXT',
  NULL, NULL,
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-8e92b92f84e74cfd853cdd8cf4f6361a', '故障等级', 'ENUM',
  NULL, '故障影响等级',
  'USER', 1,
  NULL, 'GIN',
  '[{"label": "一般", "value": "minor"}, {"label": "较重", "value": "major"}, {"label": "严重", "value": "critical"}]', NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-910c98bffcab46c7a80c3def7cf5a953', '是否能云台控制、变焦', 'BOOLEAN',
  NULL, NULL,
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-92d768ce991649fca38c345cf9959e05', '验收时间', 'DATETIME',
  NULL, '验收时间',
  'USER', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-94807db40a4b4e59a482888fca4b40df', '夜视功能', 'BOOLEAN',
  NULL, '是否支持夜视/红外功能',
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-9489c5ee50f5496d9128e6202987f83c', '管廊段起点', 'TEXT',
  NULL, NULL,
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-95373b75983148d7aaa6b83e36cd6c5b', '区间长度', 'DECIMAL',
  '米', NULL,
  'USER', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-981ca5abfa2840eeb5b4a97918029e9f', '费用(元)', 'NUMBER',
  '元', '维修费用（含人工/材料/外委）',
  'USER', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-984f2b3cfb6b4931a4dbc4506c116522', 'IP地址', 'TEXT',
  NULL, '设备网络IP地址',
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-9abe403c0a71458586572a4ea0837b03', '是否共设', 'BOOLEAN',
  NULL, NULL,
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-9c3d7e67947e470dbdecc30c6932f0db', '二氧化碳浓度', 'FLOAT',
  '%', NULL,
  'CUSTOM', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-9c447cc5208a496289c108e57289df94', '队伍位置', 'COORDINATE',
  NULL, NULL,
  'CUSTOM', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-9d05b9b3a3d24a6da5a41c8143bd448e', '外委单位', 'TEXT',
  NULL, '外委单位名称/联系人/电话',
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-9d6424ad23d94095a2421741cee572e5', '关联客户管理', 'REF_Multi',
  NULL, '系统自动创建的关联字段，用于被其他业务类型关联到客户管理',
  'SYSTEM', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-9f5694f45fa34c72a667bbf2907859d2', '关联测试存储', 'REF_Multi',
  NULL, '系统自动创建的关联字段模板,用于关联测试存储',
  'SYSTEM', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-9f8725e4340e4dec9f5aba94499e48dd', '链接', 'LINK',
  NULL, NULL,
  'CUSTOM', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-a4922b3105df498d94557170f8b2c9da', '门窗是否关闭', 'BOOLEAN',
  NULL, NULL,
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ace8c92532f7469d8026253f71b94ed3', '泵体及基础是否牢固', 'BOOLEAN',
  NULL, NULL,
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ada5d166441647b39b0fa3a0aa45def7', '防火分区编号范围', 'TEXT',
  NULL, NULL,
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-aedecf6a99c248b689c6647ac230ac91', '电缆电压等级', 'TEXT',
  NULL, NULL,
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-b035eeaa884243269178a06374ae8438', '优先级', 'ENUM',
  NULL, '工单优先级',
  'USER', 1,
  NULL, 'GIN',
  '[{"label": "低", "value": "low"}, {"label": "中", "value": "medium"}, {"label": "高", "value": "high"}, {"label": "紧急", "value": "urgent"}]', NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-b20b046c96044c61998ade2424c45c20', 'ACU设备数量', 'INTEGER',
  NULL, NULL,
  'USER', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-b32284d1d6f743b288c6b3244dc668bf', '存放地址', 'STRING',
  NULL, NULL,
  'CUSTOM', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-b42b6e004eb944e993e00f5fa734a37a', '响应时间', 'NUMBER',
  '秒', '传感器响应时间',
  'USER', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-b4677ba419b14289824604c3f30d2ea5', '显示编号', 'TEXT',
  NULL, NULL,
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-b5fdba42c7814e93a0a4966af64af608', '所属防火区', 'REFERENCE',
  NULL, NULL,
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-b86dc8d38487498d98f7e6901c459836', 'ACU设备ID', 'TEXT',
  NULL, NULL,
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-b8a976c165824f848daac3c7bbe0fa1f', '评价备注', 'LONG_TEXT',
  NULL, '评价/回访备注',
  'USER', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-b8b4fa8b0a4342348f69257aaa2101df', '结束位置', 'TEXT',
  NULL, NULL,
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-b93592352cb5449191b7d6e0037afd9b', '总部地址', 'STRING',
  NULL, NULL,
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-bcb94455381c4776a9f2ccadf0a04897', '管廊段终点', 'TEXT',
  NULL, NULL,
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-c053cdcd97ef45b19888124a89428550', '难度', 'ENUM',
  NULL, NULL,
  'CUSTOM', 1,
  NULL, 'GIN',
  '[{"label":"简单","value":"简单"},{"label":"困难","value":"困难"}]', NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-c0598f91180140e387382ebb2b5855de', '开始时间', 'DATE',
  NULL, NULL,
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-c140d56fbb634e88a6a2feeb63f3b764', '维保到期', 'DATE',
  NULL, '设备维保到期日期',
  'USER', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-c22a9c186dcc452f98cd7eaa5f3f565c', '负责人', 'TEXT',
  NULL, NULL,
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-c367374d455f4024b29560be44f7c031', '维修类型', 'ENUM',
  NULL, '维修类型分类',
  'USER', 1,
  NULL, 'GIN',
  '[{"label": "故障维修", "value": "fault"}, {"label": "预防性维护", "value": "preventive"}, {"label": "紧急抢修", "value": "emergency"}, {"label": "整改工单", "value": "rectification"}]', NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-c5627169a713492ca43b67a8f8a1f57c', '壁厚', 'NUMBER',
  'mm', NULL,
  'USER', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-c59629795d7444949eb1f8d5e57e1492', '关联应急队伍', 'REF_Multi',
  NULL, '系统自动创建的关联字段，用于被其他业务类型关联到应急队伍',
  'SYSTEM', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-c7d5a8c7a9904e5a9d11492072e922b6', '中心位置桩号', 'TEXT',
  NULL, NULL,
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-c89855b21acd4cae870535cc1e29b3f9', '所在道路名称', 'TEXT',
  NULL, NULL,
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-c8dc3d61b1494b61a1197065fa41501d', '验收人', 'TEXT',
  NULL, '验收操作人',
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-c95d3b404925410fb33a523369f80dce', '附件上传', 'UPLOAD',
  NULL, NULL,
  'CUSTOM', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-c9c3f651b8e048c0a78521ae34a18f60', '共设长度', 'DECIMAL',
  '米', NULL,
  'USER', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-cb25c908b0ff485199c1800d353d3db8', '联系人', 'STRING',
  NULL, NULL,
  'CUSTOM', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-cc746ce0224145af88d5428d0b03213a', '所属区域', 'ENTITY_REF',
  NULL, '设备所属区域（关联区域分类）',
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-cd09db6ca3134996a6468594f64bc97e', '管廊尺寸', 'TEXT',
  NULL, NULL,
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ce35984ef8ac41db865b11d5a7c726c2', '长度', 'FLOAT',
  'cm', NULL,
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-cefb517417c643cdbd044929d1fe03f5', '处置结果', 'LONG_TEXT',
  NULL, '维修处置说明/更换部件/参数调整等',
  'USER', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-d2660012902946e383052a6e3186cbca', '关联收费管理', 'REF_Multi',
  NULL, '系统自动创建的关联字段，用于被其他业务类型关联到收费管理',
  'SYSTEM', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-d548b8e0ca654ab68222cbda0b08d17b', '关联测试业务', 'REF_Multi',
  NULL, '系统自动创建的关联字段模板,用于关联测试业务',
  'SYSTEM', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-d55683af52444a7587242fbb3542a932', '工作湿度', 'NUMBER',
  '%', NULL,
  'USER', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-d81ed32c5c2a4e6288c70e76865dc063', '任务名称', 'STRING',
  NULL, NULL,
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-d8cf14b223bd41189e6dc48efd36b8bb', '有无泄露', 'BOOLEAN',
  NULL, NULL,
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-d9b5d2f7cb524d17bb2223a9e5a56f4c', '到场时间', 'DATETIME',
  NULL, '到场时间',
  'USER', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-dba78a08bcb04c3ca6a6a07c62dfd671', '功率', 'NUMBER',
  'kW', NULL,
  'USER', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-dc7aaac23a384e4b85ea70d2c35efe4a', '使用备件', 'LONG_TEXT',
  NULL, '备件清单（可写 JSON/文本）',
  'USER', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ded6261e8b17477c9873ec0fd9d6c4a5', '关联检查内容', 'REF_Multi',
  NULL, '系统自动创建的关联字段，用于被其他业务类型关联到检查内容',
  'SYSTEM', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-e0c3c64565e748d3b5a811014384e1e6', '电压', 'NUMBER',
  'V', NULL,
  'USER', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-e18365a3a3ec4485ab699b46d6f820c2', '规格型号', 'TEXT',
  NULL, '设备规格型号',
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-e30a052673b6438d86ee0a99585cb9bb', '质量检查', 'LONG_TEXT',
  NULL, '质量检查结果、测试数据等',
  'USER', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-e3392e0afa374691a4795dd236cc6cb0', '安防人员是否在位', 'BOOLEAN',
  NULL, NULL,
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-e43b835f6f59492ab90a33abd53d9062', '入廊管线类型', 'TEXT',
  NULL, NULL,
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-e4b8535544194dcf9de2cf178e316ea6', '安全员', 'ENTITY_REF',
  NULL, NULL,
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-e78e5b908e2a461f81776991f85863ed', '接线是否松动', 'BOOLEAN',
  NULL, NULL,
  'CUSTOM', 1,
  NULL, 'GIN',
  '[{"label":"是","value":"是"},{"label":"否","value":"否"}]', NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-e81062a8ef8d4e8eb847543ca50d9279', '结束桩号', 'TEXT',
  NULL, NULL,
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-e8289a51a3dd41c6b7f1539efd003258', '状态', 'ENUM',
  NULL, '工单状态（简化）',
  'USER', 1,
  NULL, 'GIN',
  '[{"label": "待受理", "value": "new"}, {"label": "待派工", "value": "to_dispatch"}, {"label": "已派工", "value": "dispatched"}, {"label": "处理中", "value": "in_progress"}, {"label": "已挂起", "value": "paused"}, {"label": "待验收", "value": "to_accept"}, {"label": "验收不通过", "value": "rejected"}, {"label": "已关闭", "value": "closed"}, {"label": "已取消", "value": "cancelled"}]', NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-e8b81a761f914a6d9384a620c4ec9565', '起始位置', 'TEXT',
  NULL, NULL,
  'USER', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-e8c7851b5ade42feb7f72eb1ab59b3b5', '高度', 'DECIMAL',
  '米', NULL,
  'USER', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-e9a39f2dbe0d425abea3ee1d4e911b6d', '备注', 'LONG_TEXT',
  NULL, NULL,
  'USER', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ec6b7dec58434d76a6b0e9aeb79c197b', '完工时间', 'DATETIME',
  NULL, '维修完成时间',
  'USER', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-ed97f195ccbe495d8766026635e4f5e3', '来源', 'ENUM',
  NULL, '工单来源',
  'USER', 1,
  NULL, 'GIN',
  '[{"label": "告警联动", "value": "alarm"}, {"label": "巡检发现", "value": "inspection"}, {"label": "人工报修", "value": "manual"}, {"label": "计划维护", "value": "plan"}]', NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-f2bbdc83208843e28026f785c7195ce5', '通信方式', 'ENUM',
  NULL, NULL,
  'USER', 1,
  NULL, 'GIN',
  '[{"label": "RS485", "value": "RS485"}, {"label": "Modbus", "value": "Modbus"}, {"label": "4G", "value": "4G"}, {"label": "WiFi", "value": "WiFi"}]', NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-f3dd829ea93b4666963866d6340d9d8a', '排风机数量', 'INTEGER',
  NULL, NULL,
  'USER', 1,
  NULL, 'BTREE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-f79945273262403ca68c2894da465a63', '部署安装内容', 'TEXT',
  NULL, NULL,
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-f8b34842206643ddaf086605f15d2288', '点位位置', 'ENTITY_REF',
  NULL, NULL,
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-f8d26649047f4f719c6835ce513a8748', '关联应急管理', 'REF_Multi',
  NULL, '系统自动创建的关联字段模板,用于关联应急管理',
  'SYSTEM', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-f91ec5e752884faa8a792860f2086e0f', '有无腐蚀', 'BOOLEAN',
  NULL, NULL,
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-f96d271a9acf48f894d9aff1a06d2a9e', '客户住址', 'TEXT',
  NULL, NULL,
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'F-f9afe7748fb040f29c8f3450e48c30d3', '队伍状态', 'ENUM',
  NULL, NULL,
  'CUSTOM', 1,
  NULL, 'GIN',
  '[{"label":"出动","value":"出动"},{"label":"待命","value":"待命"}]', NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'FIELD_00000001', '名称', 'TEXT',
  NULL, '通用名称字段',
  'SYSTEM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'FIELD_00000002', '描述', 'TEXT',
  NULL, '通用描述字段',
  'SYSTEM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'FIELD_00000003', '备注', 'TEXT',
  NULL, '通用备注字段',
  'SYSTEM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'FIELD_00000004', '创建时间', 'DATE',
  NULL, '创建时间字段',
  'SYSTEM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'FIELD_00000005', '更新时间', 'DATE',
  NULL, '更新时间字段',
  'SYSTEM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'FIELD_00000006', '生产日期', 'DATE',
  NULL, '生产日期字段',
  'SYSTEM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'FIELD_00000007', '安装日期', 'DATE',
  NULL, '安装日期字段',
  'SYSTEM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'FIELD_00000008', '长度', 'NUMBER',
  '米', '长度字段（单位：米）',
  'SYSTEM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'FIELD_00000009', '宽度', 'NUMBER',
  '米', '宽度字段（单位：米）',
  'SYSTEM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'FIELD_00000010', '高度', 'NUMBER',
  '米', '高度字段（单位：米）',
  'SYSTEM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'FIELD_00000011', '重量', 'NUMBER',
  '千克', '重量字段（单位：千克）',
  'SYSTEM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'FIELD_00000012', '数量', 'NUMBER',
  '个', '数量字段（单位：个）',
  'SYSTEM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'FIELD_00000013', '是否启用', 'BOOLEAN',
  NULL, '是否启用字段',
  'SYSTEM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'FIELD_00000014', '是否删除', 'BOOLEAN',
  NULL, '是否删除字段',
  'SYSTEM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'FIELD_00000016', '生产日期', 'DATE',
  NULL, NULL,
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'FIELD_00000017', '购买金额', 'FLOAT',
  '元', NULL,
  'CUSTOM', 1,
  NULL, 'GIN',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'REF-MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id', '所属设备管理', 'ENTITY_REF',
  NULL, '系统自动创建的关联字段，关联到 可燃气体探测器',
  'CUSTOM', 1,
  NULL, 'NONE',
  NULL, NULL,
  NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;


-- dynamic_group(FIELD): 4 row(s), upsert by (group_type, code)

INSERT INTO dynamic_group (
  group_type, code, name, description, parent_id, path, level, sort, status, tenant_id, creator
)
SELECT
  'FIELD', 'FG-2011499868945776640', '设备类',
  NULL, NULL,
  NULL, 1,
  0, 1,
  1, 'zhgl-seed'
WHERE NOT EXISTS (
  SELECT 1 FROM dynamic_group g
  WHERE g.deleted = false AND g.tenant_id = 1
    AND g.group_type = 'FIELD' AND g.code = 'FG-2011499868945776640'
);

UPDATE dynamic_group g SET
  name = '设备类',
  description = NULL,
  sort = 0,
  status = 1,
  updater = 'zhgl-seed',
  update_time = CURRENT_TIMESTAMP
WHERE g.deleted = false AND g.tenant_id = 1
  AND g.group_type = 'FIELD' AND g.code = 'FG-2011499868945776640';

INSERT INTO dynamic_group (
  group_type, code, name, description, parent_id, path, level, sort, status, tenant_id, creator
)
SELECT
  'FIELD', 'FG-2034035819643777024', '单位',
  NULL, NULL,
  NULL, 1,
  0, 1,
  1, 'zhgl-seed'
WHERE NOT EXISTS (
  SELECT 1 FROM dynamic_group g
  WHERE g.deleted = false AND g.tenant_id = 1
    AND g.group_type = 'FIELD' AND g.code = 'FG-2034035819643777024'
);

UPDATE dynamic_group g SET
  name = '单位',
  description = NULL,
  sort = 0,
  status = 1,
  updater = 'zhgl-seed',
  update_time = CURRENT_TIMESTAMP
WHERE g.deleted = false AND g.tenant_id = 1
  AND g.group_type = 'FIELD' AND g.code = 'FG-2034035819643777024';

INSERT INTO dynamic_group (
  group_type, code, name, description, parent_id, path, level, sort, status, tenant_id, creator
)
SELECT
  'FIELD', 'FG-2034036042340347904', '巡检类',
  NULL, NULL,
  NULL, 1,
  0, 1,
  1, 'zhgl-seed'
WHERE NOT EXISTS (
  SELECT 1 FROM dynamic_group g
  WHERE g.deleted = false AND g.tenant_id = 1
    AND g.group_type = 'FIELD' AND g.code = 'FG-2034036042340347904'
);

UPDATE dynamic_group g SET
  name = '巡检类',
  description = NULL,
  sort = 0,
  status = 1,
  updater = 'zhgl-seed',
  update_time = CURRENT_TIMESTAMP
WHERE g.deleted = false AND g.tenant_id = 1
  AND g.group_type = 'FIELD' AND g.code = 'FG-2034036042340347904';

INSERT INTO dynamic_group (
  group_type, code, name, description, parent_id, path, level, sort, status, tenant_id, creator
)
SELECT
  'FIELD', 'FG-2034125864576856064', '任务',
  NULL, NULL,
  NULL, 1,
  0, 1,
  1, 'zhgl-seed'
WHERE NOT EXISTS (
  SELECT 1 FROM dynamic_group g
  WHERE g.deleted = false AND g.tenant_id = 1
    AND g.group_type = 'FIELD' AND g.code = 'FG-2034125864576856064'
);

UPDATE dynamic_group g SET
  name = '任务',
  description = NULL,
  sort = 0,
  status = 1,
  updater = 'zhgl-seed',
  update_time = CURRENT_TIMESTAMP
WHERE g.deleted = false AND g.tenant_id = 1
  AND g.group_type = 'FIELD' AND g.code = 'FG-2034125864576856064';


-- dynamic_group_relation(FIELD): 16 row(s), resolve by group_code + field_code

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 0, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-2011499868945776640'
  AND f.code = 'F-048afd8b2fef478a8174b77a5daa6cff'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 0, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-2011499868945776640'
  AND f.code = 'F-72697688b36e4399b25121108ef76b9a'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 0, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-2011499868945776640'
  AND f.code = 'FIELD_00000016'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 0, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-2011499868945776640'
  AND f.code = 'FIELD_00000017'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 0, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-2034035819643777024'
  AND f.code = 'F-048afd8b2fef478a8174b77a5daa6cff'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 0, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-2034035819643777024'
  AND f.code = 'F-2feb012ccc244e35913d38f901d107f8'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 0, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-2034035819643777024'
  AND f.code = 'FIELD_00000016'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 0, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-2034035819643777024'
  AND f.code = 'FIELD_00000017'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 0, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-2034036042340347904'
  AND f.code = 'FIELD_00000016'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 1, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-2034125864576856064'
  AND f.code = 'F-d81ed32c5c2a4e6288c70e76865dc063'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 2, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-2034125864576856064'
  AND f.code = 'FIELD_00000017'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 3, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-2034125864576856064'
  AND f.code = 'FIELD_00000016'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 4, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-2034125864576856064'
  AND f.code = 'F-048afd8b2fef478a8174b77a5daa6cff'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 5, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-2034125864576856064'
  AND f.code = 'F-c0598f91180140e387382ebb2b5855de'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 6, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-2034125864576856064'
  AND f.code = 'F-2459441906934083b0a9379196bc1772'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );

INSERT INTO dynamic_group_relation (
  group_type, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.id, f.id, 7, 1, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD'
  AND g.code = 'FG-2034125864576856064'
  AND f.code = 'F-07de4c93dbf44a7ab7f0ab74f4393115'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.deleted = false AND gr.tenant_id = 1
      AND gr.group_type = 'FIELD' AND gr.group_id = g.id AND gr.target_id = f.id
  );
