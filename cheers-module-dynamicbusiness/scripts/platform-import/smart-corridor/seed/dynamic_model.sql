-- ============================================================================
-- 管廊 · dynamic_model
-- Generated: 2026-07-08 by scripts/export-platform-import.py
--
-- 约定：不写 surrogate id；幂等键为 code / field_code / page_code。
-- 幂等 upsert；关联字段按 code 解析 id，不写 surrogate id
-- ============================================================================

SET search_path TO dynamicbusiness;

-- dynamic_model: 70 row(s), upsert by code

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-07c955e48d9e4cc09916238a4905a36f', '集水坑', 'region',
  '集水坑区域分类模型，用于定义集水坑的属性', 1,
  0, NULL,
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  field_groups_config = EXCLUDED.field_groups_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-089b257b8e9a42748e0cc478520d0897', '业务啊的模型1', 'biz_1767930708219',
  '', 1,
  0, NULL,
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  field_groups_config = EXCLUDED.field_groups_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-0d966e1eedda42c5b3c7193571f3c2b3', '温度记录', 'jian_cha_nei_rong',
  '', 1,
  0, NULL,
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  field_groups_config = EXCLUDED.field_groups_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-12fa59cf4469493a8b0a481575f5ea7e', '外观是否完好', 'jian_cha_nei_rong',
  '', 1,
  0, NULL,
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  field_groups_config = EXCLUDED.field_groups_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-1418ef020a03466fbb58f73fe3e7562e', '通信舱', 'region',
  '通信舱管理模型', 1,
  0, NULL,
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  field_groups_config = EXCLUDED.field_groups_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-17c15377760a4656b487fb23d072c1a4', '引出门', 'region',
  '引出门区域分类模型，用于定义引出门的属性', 1,
  0, NULL,
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  field_groups_config = EXCLUDED.field_groups_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-1b242833ff0e4d81b741e37ba125d543', '电力信息仓', 'region',
  '电力信息仓管理模型', 1,
  0, NULL,
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  field_groups_config = EXCLUDED.field_groups_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-1e1d4c6cfd724c99ab1ff9f1a9e73e98', '是否能云台控制、变焦', 'jian_cha_nei_rong',
  '', 1,
  0, NULL,
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  field_groups_config = EXCLUDED.field_groups_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-25035f3924054db1a5763651feef6c1c', '部署安装费', 'shou_fei',
  '', 1,
  0, NULL,
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  field_groups_config = EXCLUDED.field_groups_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-26eabac35cb644cc84ae64dfb5ff9ba4', '冷热供水管', 'pipeline',
  '设备类型: 冷热供水管 (设备数量: 681)', 1,
  0, NULL,
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  field_groups_config = EXCLUDED.field_groups_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-2fe725af947441cd93c590df6275e551', '接线是否松动', 'jian_cha_nei_rong',
  '', 1,
  0, NULL,
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  field_groups_config = EXCLUDED.field_groups_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-38b16e542f144607823a23a297b4a771', '生活给水管', 'pipeline',
  '设备类型: 生活给水管 (设备数量: 250)', 1,
  0, NULL,
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  field_groups_config = EXCLUDED.field_groups_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-3aba3622dd75479f8dfc02228540cc80', '吊装口', 'region',
  '吊装口区域分类模型，用于定义吊装口的属性', 1,
  0, NULL,
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  field_groups_config = EXCLUDED.field_groups_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-3c262bb09fed4db6bd04a13bc5ce1309', '安防人员是否在位', 'jian_cha_nei_rong',
  '', 1,
  0, NULL,
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  field_groups_config = EXCLUDED.field_groups_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-3ca13297c904476db91aac9880088975', '泵体及基础是否牢固', 'jian_cha_nei_rong',
  '', 1,
  0, NULL,
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  field_groups_config = EXCLUDED.field_groups_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-3f3d3918c11148b6b67d1470ac6e8fdc', '通用模型', 'ying_ji_zi_yuan',
  '', 1,
  0, NULL,
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  field_groups_config = EXCLUDED.field_groups_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-4304a9f0d8fc49c9af7acb5baa74b0f0', '个人客户', 'ke_hu',
  '', 1,
  0, NULL,
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  field_groups_config = EXCLUDED.field_groups_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-51f858c2280c4935b96055fd1b60a5e0', '管廊段', 'region',
  '管廊段管理模型', 1,
  0, NULL,
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  field_groups_config = EXCLUDED.field_groups_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-556a2a0bd63944a79f97a936f3ffeaaa', '门窗是否关闭', 'jian_cha_nei_rong',
  '', 1,
  0, NULL,
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  field_groups_config = EXCLUDED.field_groups_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2', '倒虹', 'region',
  '倒虹区域分类模型，用于定义倒虹的属性', 1,
  0, NULL,
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  field_groups_config = EXCLUDED.field_groups_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-58ca87771dfe4156b75aeac50211117f', '房间卫生是否达标', 'jian_cha_nei_rong',
  '', 1,
  0, NULL,
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  field_groups_config = EXCLUDED.field_groups_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-5bf9561d71624c43af36d4411520bb8d', '端井', 'region',
  '端井区域分类模型，用于定义端井的属性', 1,
  0, NULL,
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  field_groups_config = EXCLUDED.field_groups_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-5de5a74896754c4e93786884405b9d89', '机械通风口', 'region',
  '机械通风口区域分类模型，用于定义机械通风口的属性', 1,
  0, NULL,
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  field_groups_config = EXCLUDED.field_groups_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-5e1c294d86fd47f3bcf5158039294476', '维护工单', 'maintenance',
  '维护工单模型', 1,
  0, NULL,
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  field_groups_config = EXCLUDED.field_groups_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad', '分变电所', 'region',
  '分变电所区域分类模型，用于定义分变电所的属性', 1,
  0, NULL,
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  field_groups_config = EXCLUDED.field_groups_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-5f185cf77c48489192396bb3b745f692', '给水管', 'pipeline',
  '给水管管线模型', 1,
  0, NULL,
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  field_groups_config = EXCLUDED.field_groups_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-61cc9a25576a4d07b304337a3eac966a', '自用电缆', 'pipeline',
  '设备类型: 自用电缆 (设备数量: 1067)', 1,
  0, NULL,
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  field_groups_config = EXCLUDED.field_groups_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-6b12a00d513b4840973d647926fa422d', '业务咨询费', 'shou_fei',
  '', 1,
  0, NULL,
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  field_groups_config = EXCLUDED.field_groups_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-6bfa9a8c9be648cebc21737115690e21', '[SEED-维修管理] 维修工单（通用）', 'maintenance',
  '用于覆盖维修管理全流程的通用工单模型', 1,
  0, NULL,
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  field_groups_config = EXCLUDED.field_groups_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-6f334d0329224744ab4d8c800ea6b0e0', '污水舱', 'region',
  '污水舱管理模型', 1,
  0, NULL,
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  field_groups_config = EXCLUDED.field_groups_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-7bf4135c3ca24be89d6ca7eb7f2090bb', '排水管', 'pipeline',
  '排水管管线模型', 1,
  0, NULL,
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  field_groups_config = EXCLUDED.field_groups_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-88a38000121341e2950132da32baad90', '通信电缆', 'pipeline',
  '设备类型: 通信电缆 (设备数量: 1078)', 1,
  0, NULL,
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  field_groups_config = EXCLUDED.field_groups_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-8a091acfa63941459916e380e1e1020e', '交叉口', 'region',
  '交叉口区域分类模型，用于定义交叉口的属性', 1,
  0, NULL,
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  field_groups_config = EXCLUDED.field_groups_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-8c85f68408244a2db7c04cfc00345e2f', '自然进风口', 'region',
  '自然进风口区域分类模型，用于定义自然进风口的属性', 1,
  0, NULL,
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  field_groups_config = EXCLUDED.field_groups_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-91961708428e4caea9546a3769095867', '业务B的实体1', 'biz_1767930723118',
  '', 1,
  0, NULL,
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  field_groups_config = EXCLUDED.field_groups_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-91b5154ccebb415b8a630fcec479bd0a', '再生水管', 'pipeline',
  '设备类型: 再生水管 (设备数量: 276)', 1,
  0, NULL,
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  field_groups_config = EXCLUDED.field_groups_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-92b4b6a9bc954beb80c3a4c248a6fc4a', '防火区', 'region',
  '防火区管理模型', 1,
  0, NULL,
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  field_groups_config = EXCLUDED.field_groups_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-93c3419d952f45a0846a2608a2719b4e', '给水舱', 'region',
  '给水舱管理模型', 1,
  0, NULL,
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  field_groups_config = EXCLUDED.field_groups_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-93d60dad3e834d518ec19cafe1bc301d', '常规点位', 'dian_wei',
  '', 1,
  0, NULL,
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  field_groups_config = EXCLUDED.field_groups_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-947e1415c0514a9d9001eba2d58fb22c', '企业客户', 'ke_hu',
  '', 1,
  0, NULL,
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  field_groups_config = EXCLUDED.field_groups_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-9ce1710bf842479db96b2e355746d289', '燃气舱', 'region',
  '燃气舱管理模型', 1,
  0, NULL,
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  field_groups_config = EXCLUDED.field_groups_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-9e6a1e6e4a5d4b378a9ec7abfd0a0fd9', '湿度记录', 'jian_cha_nei_rong',
  '', 1,
  0, NULL,
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  field_groups_config = EXCLUDED.field_groups_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-9f00c2ed9ca74a888364669af37523b0', '电力专用舱', 'region',
  '电力专用舱管理模型', 1,
  0, NULL,
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  field_groups_config = EXCLUDED.field_groups_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-a8470fe38e9d4d68b7e11989e361e388', '20KV电缆', 'pipeline',
  '设备类型: 20KV电缆 (设备数量: 5121)', 1,
  0, NULL,
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  field_groups_config = EXCLUDED.field_groups_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-b34597144e524aeb9b969b4b8357f6cd', '驻场维护费', 'shou_fei',
  '', 1,
  0, '{"groups":[{"id":"group-1770041732814","name":"人员","color":"#409eff","sort":1,"fields":[]}]}',
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  field_groups_config = EXCLUDED.field_groups_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-b83f05dc94f34fb4807458c0a48ba488', '110KV电缆', 'pipeline',
  '设备类型: 110KV电缆 (设备数量: 693)', 1,
  0, NULL,
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  field_groups_config = EXCLUDED.field_groups_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-bc167ec7fa82422cb036804214c0c3ac', '路线通用模型', 'lu_xian_guan_li',
  '', 1,
  0, NULL,
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  field_groups_config = EXCLUDED.field_groups_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-c3470e3a96e9493880a401f79e3f98f3', '通用队伍模型', 'ying_ji_dui_wu',
  '', 1,
  0, NULL,
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  field_groups_config = EXCLUDED.field_groups_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-cbbeb5ee8dd24796899408a96d38bbbc', '故障记录', 'fault',
  '故障记录模型', 1,
  0, NULL,
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  field_groups_config = EXCLUDED.field_groups_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-d0c46c54a32f49279e49113ae9f1a6a2', '其他管线', 'pipeline',
  '其他管线模型', 1,
  0, NULL,
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  field_groups_config = EXCLUDED.field_groups_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7', '机械排风口', 'region',
  '机械排风口区域分类模型，用于定义机械排风口的属性', 1,
  0, NULL,
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  field_groups_config = EXCLUDED.field_groups_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-ea08ef31378f4e488e49b53093b6edee', '排水坑', 'region',
  '', 1,
  0, '{"groups":[{"id":"group-1773126482537","name":"基础信息","color":"#409eff","sort":1,"fields":[{"fieldId":52,"sort":1,"extra":{}},{"fieldId":45,"sort":2,"extra":{}}]},{"id":"group-1773129519552","name":"其他信息","color":"#409eff","sort":2,"fields":[{"fieldId":72,"sort":1,"extra":{}},{"fieldId":93,"sort":2,"extra":{}},{"fieldId":46,"sort":3,"extra":{}},{"fieldId":47,"sort":4,"extra":{}},{"fieldId":84,"sort":5,"extra":{}}]}]}',
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  field_groups_config = EXCLUDED.field_groups_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-eeb6defc531844b2ad96ca2afe48b29e', '备件', 'spare_part',
  '备件模型', 1,
  0, NULL,
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  field_groups_config = EXCLUDED.field_groups_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-ef8378fb5b6f41619866e90ef82bc4d9', '管道舱', 'region',
  '管道舱管理模型', 1,
  0, NULL,
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  field_groups_config = EXCLUDED.field_groups_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-f1abcb10cae34b459f85e4135505a6a4', '人员出入口', 'region',
  '人员出入口区域分类模型，用于定义人员出入口的属性', 1,
  0, NULL,
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  field_groups_config = EXCLUDED.field_groups_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-f55ce56d31f741caa5a69801556834fe', '220KV电缆', 'pipeline',
  '设备类型: 220KV电缆 (设备数量: 957)', 1,
  0, NULL,
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  field_groups_config = EXCLUDED.field_groups_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-FACILITY-DEPOT', '油库', 'facility',
  '油库设施点', 1,
  3, NULL,
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  field_groups_config = EXCLUDED.field_groups_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-FACILITY-OFFICE', '机关/楼宇', 'facility',
  '机关办公与楼宇设施点', 1,
  4, NULL,
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  field_groups_config = EXCLUDED.field_groups_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-FACILITY-PLANT', '厂区', 'facility',
  '工厂、化工园区厂区', 1,
  2, NULL,
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  field_groups_config = EXCLUDED.field_groups_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-FACILITY-STATION', '站场', 'facility',
  '油气储运站场等设施点', 1,
  1, NULL,
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  field_groups_config = EXCLUDED.field_groups_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-fb102aef77624c2d975a545e421ad534', '日常巡检', 'xun_jian',
  '', 1,
  0, NULL,
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  field_groups_config = EXCLUDED.field_groups_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-fb322a4355994daa8f1b2e2c2f9adba7', '综合管道舱', 'region',
  '综合管道舱管理模型', 1,
  0, NULL,
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  field_groups_config = EXCLUDED.field_groups_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-REGION-CITY', '地市', 'region',
  '行政区划：地级市 [已废弃 2026-07-07：region 改由 category 树 + Pattern C 配置，不按省市区预置]', 0,
  3, NULL,
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  field_groups_config = EXCLUDED.field_groups_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-REGION-COUNTRY', '全国', 'region',
  '行政区划：全国根节点 [已废弃 2026-07-07：region 改由 category 树 + Pattern C 配置，不按省市区预置]', 0,
  1, NULL,
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  field_groups_config = EXCLUDED.field_groups_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-REGION-DISTRICT', '区', 'region',
  '行政区划：区县级 [已废弃 2026-07-07：region 改由 category 树 + Pattern C 配置，不按省市区预置]', 0,
  4, NULL,
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  field_groups_config = EXCLUDED.field_groups_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-REGION-PROVINCE', '省', 'region',
  '行政区划：省级 [已废弃 2026-07-07：region 改由 category 树 + Pattern C 配置，不按省市区预置]', 0,
  2, NULL,
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  field_groups_config = EXCLUDED.field_groups_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-ZONE-BUILDING', '楼栋', 'zone',
  '厂区内楼栋', 1,
  4, '{"groups":[{"id":"group-1783442442497","name":"基础信息","color":"#409eff","sort":1024,"fields":[]}]}',
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  field_groups_config = EXCLUDED.field_groups_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-ZONE-FUNCTIONAL', '功能分区', 'zone',
  '装卸区、消防分区等', 1,
  3, '{"groups":[{"id":"group-1783442442494","name":"基础信息","color":"#409eff","sort":1024,"fields":[]}]}',
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  field_groups_config = EXCLUDED.field_groups_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-ZONE-TANK-GROUP', '罐组', 'zone',
  '储罐分区', 1,
  1, '{"groups":[{"id":"group-1783442442478","name":"基础信息","color":"#409eff","sort":1024,"fields":[]}]}',
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  field_groups_config = EXCLUDED.field_groups_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-ZONE-WAREHOUSE', '库棚', 'zone',
  '仓储分区', 1,
  2, '{"groups":[{"id":"group-1783442442490","name":"基础信息","color":"#409eff","sort":1024,"fields":[]}]}',
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  field_groups_config = EXCLUDED.field_groups_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;
