-- ============================================================================
-- 智慧管廊 · 05_models.sql
-- Generated: 2026-07-05 by scripts/export-platform-import.py
--
-- 约定：不写 surrogate id；幂等键为 code / field_code / page_code。
-- 依赖：先导入 system/ 全包（设备模型库已在 system/05_models.sql）
-- 含 region→equipment 等跨业务模型关联
-- ============================================================================

SET search_path TO dynamicbusiness;

-- dynamic_model: 56 row(s), upsert by code

INSERT INTO dynamic_model (
  code, name, business_type_code, description, status, sort, field_groups_config, tenant_id, creator
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
  code, name, business_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-0d966e1eedda42c5b3c7193571f3c2b3', '温度记录', 'inspection_item',
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
  code, name, business_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-12fa59cf4469493a8b0a481575f5ea7e', '外观是否完好', 'inspection_item',
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
  code, name, business_type_code, description, status, sort, field_groups_config, tenant_id, creator
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
  code, name, business_type_code, description, status, sort, field_groups_config, tenant_id, creator
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
  code, name, business_type_code, description, status, sort, field_groups_config, tenant_id, creator
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
  code, name, business_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-1e1d4c6cfd724c99ab1ff9f1a9e73e98', '是否能云台控制、变焦', 'inspection_item',
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
  code, name, business_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-25035f3924054db1a5763651feef6c1c', '部署安装费', 'billing',
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
  code, name, business_type_code, description, status, sort, field_groups_config, tenant_id, creator
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
  code, name, business_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-2fe725af947441cd93c590df6275e551', '接线是否松动', 'inspection_item',
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
  code, name, business_type_code, description, status, sort, field_groups_config, tenant_id, creator
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
  code, name, business_type_code, description, status, sort, field_groups_config, tenant_id, creator
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
  code, name, business_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-3c262bb09fed4db6bd04a13bc5ce1309', '安防人员是否在位', 'inspection_item',
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
  code, name, business_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-3ca13297c904476db91aac9880088975', '泵体及基础是否牢固', 'inspection_item',
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
  code, name, business_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-3f3d3918c11148b6b67d1470ac6e8fdc', '通用模型', 'emergency_resource',
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
  code, name, business_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-4304a9f0d8fc49c9af7acb5baa74b0f0', '个人客户', 'customer',
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
  code, name, business_type_code, description, status, sort, field_groups_config, tenant_id, creator
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
  code, name, business_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-556a2a0bd63944a79f97a936f3ffeaaa', '门窗是否关闭', 'inspection_item',
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
  code, name, business_type_code, description, status, sort, field_groups_config, tenant_id, creator
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
  code, name, business_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-58ca87771dfe4156b75aeac50211117f', '房间卫生是否达标', 'inspection_item',
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
  code, name, business_type_code, description, status, sort, field_groups_config, tenant_id, creator
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
  code, name, business_type_code, description, status, sort, field_groups_config, tenant_id, creator
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
  code, name, business_type_code, description, status, sort, field_groups_config, tenant_id, creator
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
  code, name, business_type_code, description, status, sort, field_groups_config, tenant_id, creator
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
  code, name, business_type_code, description, status, sort, field_groups_config, tenant_id, creator
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
  code, name, business_type_code, description, status, sort, field_groups_config, tenant_id, creator
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
  code, name, business_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-6b12a00d513b4840973d647926fa422d', '业务咨询费', 'billing',
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
  code, name, business_type_code, description, status, sort, field_groups_config, tenant_id, creator
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
  code, name, business_type_code, description, status, sort, field_groups_config, tenant_id, creator
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
  code, name, business_type_code, description, status, sort, field_groups_config, tenant_id, creator
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
  code, name, business_type_code, description, status, sort, field_groups_config, tenant_id, creator
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
  code, name, business_type_code, description, status, sort, field_groups_config, tenant_id, creator
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
  code, name, business_type_code, description, status, sort, field_groups_config, tenant_id, creator
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
  code, name, business_type_code, description, status, sort, field_groups_config, tenant_id, creator
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
  code, name, business_type_code, description, status, sort, field_groups_config, tenant_id, creator
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
  code, name, business_type_code, description, status, sort, field_groups_config, tenant_id, creator
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
  code, name, business_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-93d60dad3e834d518ec19cafe1bc301d', '常规点位', 'inspection_point',
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
  code, name, business_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-947e1415c0514a9d9001eba2d58fb22c', '企业客户', 'customer',
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
  code, name, business_type_code, description, status, sort, field_groups_config, tenant_id, creator
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
  code, name, business_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-9e6a1e6e4a5d4b378a9ec7abfd0a0fd9', '湿度记录', 'inspection_item',
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
  code, name, business_type_code, description, status, sort, field_groups_config, tenant_id, creator
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
  code, name, business_type_code, description, status, sort, field_groups_config, tenant_id, creator
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
  code, name, business_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-b34597144e524aeb9b969b4b8357f6cd', '驻场维护费', 'billing',
  '', 1,
  0, '{"groups": [{"id": "group-1770041732814", "name": "人员", "sort": 1, "color": "#409eff", "fields": []}]}',
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
  code, name, business_type_code, description, status, sort, field_groups_config, tenant_id, creator
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
  code, name, business_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-bc167ec7fa82422cb036804214c0c3ac', '路线通用模型', 'route',
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
  code, name, business_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-c3470e3a96e9493880a401f79e3f98f3', '通用队伍模型', 'emergency_team',
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
  code, name, business_type_code, description, status, sort, field_groups_config, tenant_id, creator
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
  code, name, business_type_code, description, status, sort, field_groups_config, tenant_id, creator
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
  code, name, business_type_code, description, status, sort, field_groups_config, tenant_id, creator
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
  code, name, business_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-ea08ef31378f4e488e49b53093b6edee', '排水坑', 'region',
  '', 1,
  0, '{"groups": [{"id": "group-1773129519552", "name": "其他信息", "sort": 1, "color": "#409eff", "fields": []}, {"id": "group-1773126482537", "name": "基础信息", "sort": 2, "color": "#409eff", "fields": []}]}',
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
  code, name, business_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-eeb6defc531844b2ad96ca2afe48b29e', '备件', 'spare_parts',
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
  code, name, business_type_code, description, status, sort, field_groups_config, tenant_id, creator
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
  code, name, business_type_code, description, status, sort, field_groups_config, tenant_id, creator
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
  code, name, business_type_code, description, status, sort, field_groups_config, tenant_id, creator
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
  code, name, business_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-fb102aef77624c2d975a545e421ad534', '日常巡检', 'patrol',
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
  code, name, business_type_code, description, status, sort, field_groups_config, tenant_id, creator
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


-- dynamic_model_relation: 594 row(s), resolve model id by code

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-07c955e48d9e4cc09916238a4905a36f',
  tm.id, 'MODEL-05e5c6a6c8fe4f3c9bd1dd86dd24a659',
  '所属设备管理', 'MODEL_05e5c6a6c8fe4f3c9bd1dd86dd24a659_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-07c955e48d9e4cc09916238a4905a36f'
  AND tm.code = 'MODEL-05e5c6a6c8fe4f3c9bd1dd86dd24a659'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-07c955e48d9e4cc09916238a4905a36f'
      AND r.target_model_code = 'MODEL-05e5c6a6c8fe4f3c9bd1dd86dd24a659'
      AND r.field_code = 'MODEL_05e5c6a6c8fe4f3c9bd1dd86dd24a659_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-07c955e48d9e4cc09916238a4905a36f',
  tm.id, 'MODEL-123328b7bfe745ceb337d286ac64aa17',
  '所属设备管理', 'MODEL_123328b7bfe745ceb337d286ac64aa17_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-07c955e48d9e4cc09916238a4905a36f'
  AND tm.code = 'MODEL-123328b7bfe745ceb337d286ac64aa17'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-07c955e48d9e4cc09916238a4905a36f'
      AND r.target_model_code = 'MODEL-123328b7bfe745ceb337d286ac64aa17'
      AND r.field_code = 'MODEL_123328b7bfe745ceb337d286ac64aa17_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-07c955e48d9e4cc09916238a4905a36f',
  tm.id, 'MODEL-17eb9be1c7404734af570ce040f1783b',
  '所属设备管理', 'MODEL_17eb9be1c7404734af570ce040f1783b_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-07c955e48d9e4cc09916238a4905a36f'
  AND tm.code = 'MODEL-17eb9be1c7404734af570ce040f1783b'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-07c955e48d9e4cc09916238a4905a36f'
      AND r.target_model_code = 'MODEL-17eb9be1c7404734af570ce040f1783b'
      AND r.field_code = 'MODEL_17eb9be1c7404734af570ce040f1783b_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-07c955e48d9e4cc09916238a4905a36f',
  tm.id, 'MODEL-26eabac35cb644cc84ae64dfb5ff9ba4',
  '所属设备管理', 'MODEL_26eabac35cb644cc84ae64dfb5ff9ba4_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-07c955e48d9e4cc09916238a4905a36f'
  AND tm.code = 'MODEL-26eabac35cb644cc84ae64dfb5ff9ba4'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-07c955e48d9e4cc09916238a4905a36f'
      AND r.target_model_code = 'MODEL-26eabac35cb644cc84ae64dfb5ff9ba4'
      AND r.field_code = 'MODEL_26eabac35cb644cc84ae64dfb5ff9ba4_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-07c955e48d9e4cc09916238a4905a36f',
  tm.id, 'MODEL-2af0515a5a36420086980b2322037edb',
  '所属设备管理', 'MODEL_2af0515a5a36420086980b2322037edb_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-07c955e48d9e4cc09916238a4905a36f'
  AND tm.code = 'MODEL-2af0515a5a36420086980b2322037edb'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-07c955e48d9e4cc09916238a4905a36f'
      AND r.target_model_code = 'MODEL-2af0515a5a36420086980b2322037edb'
      AND r.field_code = 'MODEL_2af0515a5a36420086980b2322037edb_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-07c955e48d9e4cc09916238a4905a36f',
  tm.id, 'MODEL-3130eba3747e4a5aa0862c94807822ba',
  '所属设备管理', 'MODEL_3130eba3747e4a5aa0862c94807822ba_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-07c955e48d9e4cc09916238a4905a36f'
  AND tm.code = 'MODEL-3130eba3747e4a5aa0862c94807822ba'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-07c955e48d9e4cc09916238a4905a36f'
      AND r.target_model_code = 'MODEL-3130eba3747e4a5aa0862c94807822ba'
      AND r.field_code = 'MODEL_3130eba3747e4a5aa0862c94807822ba_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-07c955e48d9e4cc09916238a4905a36f',
  tm.id, 'MODEL-44cc7511fc224085a51e2887569c933a',
  '所属设备管理', 'MODEL_44cc7511fc224085a51e2887569c933a_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-07c955e48d9e4cc09916238a4905a36f'
  AND tm.code = 'MODEL-44cc7511fc224085a51e2887569c933a'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-07c955e48d9e4cc09916238a4905a36f'
      AND r.target_model_code = 'MODEL-44cc7511fc224085a51e2887569c933a'
      AND r.field_code = 'MODEL_44cc7511fc224085a51e2887569c933a_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-07c955e48d9e4cc09916238a4905a36f',
  tm.id, 'MODEL-50991eab2a714ed288d00c58ff01c810',
  '所属设备管理', 'MODEL_50991eab2a714ed288d00c58ff01c810_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-07c955e48d9e4cc09916238a4905a36f'
  AND tm.code = 'MODEL-50991eab2a714ed288d00c58ff01c810'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-07c955e48d9e4cc09916238a4905a36f'
      AND r.target_model_code = 'MODEL-50991eab2a714ed288d00c58ff01c810'
      AND r.field_code = 'MODEL_50991eab2a714ed288d00c58ff01c810_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-07c955e48d9e4cc09916238a4905a36f',
  tm.id, 'MODEL-5a9b87c42571486998d363101c54ec8c',
  '所属设备管理', 'MODEL_5a9b87c42571486998d363101c54ec8c_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-07c955e48d9e4cc09916238a4905a36f'
  AND tm.code = 'MODEL-5a9b87c42571486998d363101c54ec8c'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-07c955e48d9e4cc09916238a4905a36f'
      AND r.target_model_code = 'MODEL-5a9b87c42571486998d363101c54ec8c'
      AND r.field_code = 'MODEL_5a9b87c42571486998d363101c54ec8c_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-07c955e48d9e4cc09916238a4905a36f',
  tm.id, 'MODEL-5f6f9243c7bb4e23bfed2fc01b48cf06',
  '所属设备管理', 'MODEL_5f6f9243c7bb4e23bfed2fc01b48cf06_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-07c955e48d9e4cc09916238a4905a36f'
  AND tm.code = 'MODEL-5f6f9243c7bb4e23bfed2fc01b48cf06'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-07c955e48d9e4cc09916238a4905a36f'
      AND r.target_model_code = 'MODEL-5f6f9243c7bb4e23bfed2fc01b48cf06'
      AND r.field_code = 'MODEL_5f6f9243c7bb4e23bfed2fc01b48cf06_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-07c955e48d9e4cc09916238a4905a36f',
  tm.id, 'MODEL-68554821800248cfacacbac1a53cdf37',
  '所属设备管理', 'MODEL_68554821800248cfacacbac1a53cdf37_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-07c955e48d9e4cc09916238a4905a36f'
  AND tm.code = 'MODEL-68554821800248cfacacbac1a53cdf37'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-07c955e48d9e4cc09916238a4905a36f'
      AND r.target_model_code = 'MODEL-68554821800248cfacacbac1a53cdf37'
      AND r.field_code = 'MODEL_68554821800248cfacacbac1a53cdf37_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-07c955e48d9e4cc09916238a4905a36f',
  tm.id, 'MODEL-6fd43ab74f6f425f81f0ad547603a849',
  '所属设备管理', 'MODEL_6fd43ab74f6f425f81f0ad547603a849_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-07c955e48d9e4cc09916238a4905a36f'
  AND tm.code = 'MODEL-6fd43ab74f6f425f81f0ad547603a849'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-07c955e48d9e4cc09916238a4905a36f'
      AND r.target_model_code = 'MODEL-6fd43ab74f6f425f81f0ad547603a849'
      AND r.field_code = 'MODEL_6fd43ab74f6f425f81f0ad547603a849_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-07c955e48d9e4cc09916238a4905a36f',
  tm.id, 'MODEL-70d2b066120c4b969f0b661d1d2fb664',
  '所属设备管理', 'MODEL_70d2b066120c4b969f0b661d1d2fb664_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-07c955e48d9e4cc09916238a4905a36f'
  AND tm.code = 'MODEL-70d2b066120c4b969f0b661d1d2fb664'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-07c955e48d9e4cc09916238a4905a36f'
      AND r.target_model_code = 'MODEL-70d2b066120c4b969f0b661d1d2fb664'
      AND r.field_code = 'MODEL_70d2b066120c4b969f0b661d1d2fb664_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-07c955e48d9e4cc09916238a4905a36f',
  tm.id, 'MODEL-7c4fe9b2d52d41cca5383488a4a400a5',
  '所属设备管理', 'MODEL_7c4fe9b2d52d41cca5383488a4a400a5_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-07c955e48d9e4cc09916238a4905a36f'
  AND tm.code = 'MODEL-7c4fe9b2d52d41cca5383488a4a400a5'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-07c955e48d9e4cc09916238a4905a36f'
      AND r.target_model_code = 'MODEL-7c4fe9b2d52d41cca5383488a4a400a5'
      AND r.field_code = 'MODEL_7c4fe9b2d52d41cca5383488a4a400a5_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-07c955e48d9e4cc09916238a4905a36f',
  tm.id, 'MODEL-7c746a60520347baaf64334625f07f31',
  '所属设备管理', 'MODEL_7c746a60520347baaf64334625f07f31_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-07c955e48d9e4cc09916238a4905a36f'
  AND tm.code = 'MODEL-7c746a60520347baaf64334625f07f31'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-07c955e48d9e4cc09916238a4905a36f'
      AND r.target_model_code = 'MODEL-7c746a60520347baaf64334625f07f31'
      AND r.field_code = 'MODEL_7c746a60520347baaf64334625f07f31_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-07c955e48d9e4cc09916238a4905a36f',
  tm.id, 'MODEL-81132ce00d2041c6a4ee06ec1c01dac8',
  '所属设备管理', 'MODEL_81132ce00d2041c6a4ee06ec1c01dac8_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-07c955e48d9e4cc09916238a4905a36f'
  AND tm.code = 'MODEL-81132ce00d2041c6a4ee06ec1c01dac8'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-07c955e48d9e4cc09916238a4905a36f'
      AND r.target_model_code = 'MODEL-81132ce00d2041c6a4ee06ec1c01dac8'
      AND r.field_code = 'MODEL_81132ce00d2041c6a4ee06ec1c01dac8_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-07c955e48d9e4cc09916238a4905a36f',
  tm.id, 'MODEL-88a38000121341e2950132da32baad90',
  '所属设备管理', 'MODEL_88a38000121341e2950132da32baad90_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-07c955e48d9e4cc09916238a4905a36f'
  AND tm.code = 'MODEL-88a38000121341e2950132da32baad90'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-07c955e48d9e4cc09916238a4905a36f'
      AND r.target_model_code = 'MODEL-88a38000121341e2950132da32baad90'
      AND r.field_code = 'MODEL_88a38000121341e2950132da32baad90_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-07c955e48d9e4cc09916238a4905a36f',
  tm.id, 'MODEL-990370e140aa481a82158bc96dc0b006',
  '所属设备管理', 'MODEL_990370e140aa481a82158bc96dc0b006_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-07c955e48d9e4cc09916238a4905a36f'
  AND tm.code = 'MODEL-990370e140aa481a82158bc96dc0b006'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-07c955e48d9e4cc09916238a4905a36f'
      AND r.target_model_code = 'MODEL-990370e140aa481a82158bc96dc0b006'
      AND r.field_code = 'MODEL_990370e140aa481a82158bc96dc0b006_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-07c955e48d9e4cc09916238a4905a36f',
  tm.id, 'MODEL-a4ce7668efe94d2fbc47a658be1a35d2',
  '所属设备管理', 'MODEL_a4ce7668efe94d2fbc47a658be1a35d2_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-07c955e48d9e4cc09916238a4905a36f'
  AND tm.code = 'MODEL-a4ce7668efe94d2fbc47a658be1a35d2'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-07c955e48d9e4cc09916238a4905a36f'
      AND r.target_model_code = 'MODEL-a4ce7668efe94d2fbc47a658be1a35d2'
      AND r.field_code = 'MODEL_a4ce7668efe94d2fbc47a658be1a35d2_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-07c955e48d9e4cc09916238a4905a36f',
  tm.id, 'MODEL-a62c727c0a024e19a8221c68fcf0c09a',
  '所属设备管理', 'MODEL_a62c727c0a024e19a8221c68fcf0c09a_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-07c955e48d9e4cc09916238a4905a36f'
  AND tm.code = 'MODEL-a62c727c0a024e19a8221c68fcf0c09a'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-07c955e48d9e4cc09916238a4905a36f'
      AND r.target_model_code = 'MODEL-a62c727c0a024e19a8221c68fcf0c09a'
      AND r.field_code = 'MODEL_a62c727c0a024e19a8221c68fcf0c09a_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-07c955e48d9e4cc09916238a4905a36f',
  tm.id, 'MODEL-b8251da851514341adef7f6f72995cf3',
  '所属设备管理', 'MODEL_b8251da851514341adef7f6f72995cf3_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-07c955e48d9e4cc09916238a4905a36f'
  AND tm.code = 'MODEL-b8251da851514341adef7f6f72995cf3'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-07c955e48d9e4cc09916238a4905a36f'
      AND r.target_model_code = 'MODEL-b8251da851514341adef7f6f72995cf3'
      AND r.field_code = 'MODEL_b8251da851514341adef7f6f72995cf3_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-07c955e48d9e4cc09916238a4905a36f',
  tm.id, 'MODEL-b83f05dc94f34fb4807458c0a48ba488',
  '所属设备管理', 'MODEL_b83f05dc94f34fb4807458c0a48ba488_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-07c955e48d9e4cc09916238a4905a36f'
  AND tm.code = 'MODEL-b83f05dc94f34fb4807458c0a48ba488'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-07c955e48d9e4cc09916238a4905a36f'
      AND r.target_model_code = 'MODEL-b83f05dc94f34fb4807458c0a48ba488'
      AND r.field_code = 'MODEL_b83f05dc94f34fb4807458c0a48ba488_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-07c955e48d9e4cc09916238a4905a36f',
  tm.id, 'MODEL-bc2cdffeab434f4d862c847fa99938ba',
  '所属设备管理', 'MODEL_bc2cdffeab434f4d862c847fa99938ba_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-07c955e48d9e4cc09916238a4905a36f'
  AND tm.code = 'MODEL-bc2cdffeab434f4d862c847fa99938ba'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-07c955e48d9e4cc09916238a4905a36f'
      AND r.target_model_code = 'MODEL-bc2cdffeab434f4d862c847fa99938ba'
      AND r.field_code = 'MODEL_bc2cdffeab434f4d862c847fa99938ba_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-07c955e48d9e4cc09916238a4905a36f',
  tm.id, 'MODEL-bf41bd50df044f5faf43c4759353ccd1',
  '所属设备管理', 'MODEL_bf41bd50df044f5faf43c4759353ccd1_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-07c955e48d9e4cc09916238a4905a36f'
  AND tm.code = 'MODEL-bf41bd50df044f5faf43c4759353ccd1'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-07c955e48d9e4cc09916238a4905a36f'
      AND r.target_model_code = 'MODEL-bf41bd50df044f5faf43c4759353ccd1'
      AND r.field_code = 'MODEL_bf41bd50df044f5faf43c4759353ccd1_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-07c955e48d9e4cc09916238a4905a36f',
  tm.id, 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9',
  '所属设备管理', 'MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-07c955e48d9e4cc09916238a4905a36f'
  AND tm.code = 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-07c955e48d9e4cc09916238a4905a36f'
      AND r.target_model_code = 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9'
      AND r.field_code = 'MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-07c955e48d9e4cc09916238a4905a36f',
  tm.id, 'MODEL-c6bc32646f3e4bde8409120c5c290eee',
  '所属设备管理', 'MODEL_c6bc32646f3e4bde8409120c5c290eee_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-07c955e48d9e4cc09916238a4905a36f'
  AND tm.code = 'MODEL-c6bc32646f3e4bde8409120c5c290eee'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-07c955e48d9e4cc09916238a4905a36f'
      AND r.target_model_code = 'MODEL-c6bc32646f3e4bde8409120c5c290eee'
      AND r.field_code = 'MODEL_c6bc32646f3e4bde8409120c5c290eee_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-07c955e48d9e4cc09916238a4905a36f',
  tm.id, 'MODEL-d446eef7329f45b4a374347e215fec5d',
  '所属设备管理', 'MODEL_d446eef7329f45b4a374347e215fec5d_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-07c955e48d9e4cc09916238a4905a36f'
  AND tm.code = 'MODEL-d446eef7329f45b4a374347e215fec5d'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-07c955e48d9e4cc09916238a4905a36f'
      AND r.target_model_code = 'MODEL-d446eef7329f45b4a374347e215fec5d'
      AND r.field_code = 'MODEL_d446eef7329f45b4a374347e215fec5d_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-07c955e48d9e4cc09916238a4905a36f',
  tm.id, 'MODEL-e25f3089a99d4878a1ddd326ba0910bf',
  '所属设备管理', 'MODEL_e25f3089a99d4878a1ddd326ba0910bf_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-07c955e48d9e4cc09916238a4905a36f'
  AND tm.code = 'MODEL-e25f3089a99d4878a1ddd326ba0910bf'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-07c955e48d9e4cc09916238a4905a36f'
      AND r.target_model_code = 'MODEL-e25f3089a99d4878a1ddd326ba0910bf'
      AND r.field_code = 'MODEL_e25f3089a99d4878a1ddd326ba0910bf_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-07c955e48d9e4cc09916238a4905a36f',
  tm.id, 'MODEL-e5af81c45bbc44d48374d8036032cc5d',
  '所属设备管理', 'MODEL_e5af81c45bbc44d48374d8036032cc5d_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-07c955e48d9e4cc09916238a4905a36f'
  AND tm.code = 'MODEL-e5af81c45bbc44d48374d8036032cc5d'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-07c955e48d9e4cc09916238a4905a36f'
      AND r.target_model_code = 'MODEL-e5af81c45bbc44d48374d8036032cc5d'
      AND r.field_code = 'MODEL_e5af81c45bbc44d48374d8036032cc5d_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-07c955e48d9e4cc09916238a4905a36f',
  tm.id, 'MODEL-e5c45ff8ca074102829bba872e091bd1',
  '所属设备管理', 'MODEL_e5c45ff8ca074102829bba872e091bd1_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-07c955e48d9e4cc09916238a4905a36f'
  AND tm.code = 'MODEL-e5c45ff8ca074102829bba872e091bd1'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-07c955e48d9e4cc09916238a4905a36f'
      AND r.target_model_code = 'MODEL-e5c45ff8ca074102829bba872e091bd1'
      AND r.field_code = 'MODEL_e5c45ff8ca074102829bba872e091bd1_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-07c955e48d9e4cc09916238a4905a36f',
  tm.id, 'MODEL-f5270d478dd849389e59a178cd512629',
  '所属设备管理', 'MODEL_f5270d478dd849389e59a178cd512629_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-07c955e48d9e4cc09916238a4905a36f'
  AND tm.code = 'MODEL-f5270d478dd849389e59a178cd512629'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-07c955e48d9e4cc09916238a4905a36f'
      AND r.target_model_code = 'MODEL-f5270d478dd849389e59a178cd512629'
      AND r.field_code = 'MODEL_f5270d478dd849389e59a178cd512629_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-07c955e48d9e4cc09916238a4905a36f',
  tm.id, 'MODEL-f55ce56d31f741caa5a69801556834fe',
  '所属设备管理', 'MODEL_f55ce56d31f741caa5a69801556834fe_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-07c955e48d9e4cc09916238a4905a36f'
  AND tm.code = 'MODEL-f55ce56d31f741caa5a69801556834fe'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-07c955e48d9e4cc09916238a4905a36f'
      AND r.target_model_code = 'MODEL-f55ce56d31f741caa5a69801556834fe'
      AND r.field_code = 'MODEL_f55ce56d31f741caa5a69801556834fe_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-1418ef020a03466fbb58f73fe3e7562e',
  tm.id, 'MODEL-05e5c6a6c8fe4f3c9bd1dd86dd24a659',
  '所属设备管理', 'MODEL_05e5c6a6c8fe4f3c9bd1dd86dd24a659_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
  AND tm.code = 'MODEL-05e5c6a6c8fe4f3c9bd1dd86dd24a659'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
      AND r.target_model_code = 'MODEL-05e5c6a6c8fe4f3c9bd1dd86dd24a659'
      AND r.field_code = 'MODEL_05e5c6a6c8fe4f3c9bd1dd86dd24a659_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-1418ef020a03466fbb58f73fe3e7562e',
  tm.id, 'MODEL-123328b7bfe745ceb337d286ac64aa17',
  '所属设备管理', 'MODEL_123328b7bfe745ceb337d286ac64aa17_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
  AND tm.code = 'MODEL-123328b7bfe745ceb337d286ac64aa17'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
      AND r.target_model_code = 'MODEL-123328b7bfe745ceb337d286ac64aa17'
      AND r.field_code = 'MODEL_123328b7bfe745ceb337d286ac64aa17_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-1418ef020a03466fbb58f73fe3e7562e',
  tm.id, 'MODEL-17eb9be1c7404734af570ce040f1783b',
  '所属设备管理', 'MODEL_17eb9be1c7404734af570ce040f1783b_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
  AND tm.code = 'MODEL-17eb9be1c7404734af570ce040f1783b'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
      AND r.target_model_code = 'MODEL-17eb9be1c7404734af570ce040f1783b'
      AND r.field_code = 'MODEL_17eb9be1c7404734af570ce040f1783b_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-1418ef020a03466fbb58f73fe3e7562e',
  tm.id, 'MODEL-26eabac35cb644cc84ae64dfb5ff9ba4',
  '所属设备管理', 'MODEL_26eabac35cb644cc84ae64dfb5ff9ba4_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
  AND tm.code = 'MODEL-26eabac35cb644cc84ae64dfb5ff9ba4'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
      AND r.target_model_code = 'MODEL-26eabac35cb644cc84ae64dfb5ff9ba4'
      AND r.field_code = 'MODEL_26eabac35cb644cc84ae64dfb5ff9ba4_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-1418ef020a03466fbb58f73fe3e7562e',
  tm.id, 'MODEL-2af0515a5a36420086980b2322037edb',
  '所属设备管理', 'MODEL_2af0515a5a36420086980b2322037edb_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
  AND tm.code = 'MODEL-2af0515a5a36420086980b2322037edb'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
      AND r.target_model_code = 'MODEL-2af0515a5a36420086980b2322037edb'
      AND r.field_code = 'MODEL_2af0515a5a36420086980b2322037edb_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-1418ef020a03466fbb58f73fe3e7562e',
  tm.id, 'MODEL-3130eba3747e4a5aa0862c94807822ba',
  '所属设备管理', 'MODEL_3130eba3747e4a5aa0862c94807822ba_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
  AND tm.code = 'MODEL-3130eba3747e4a5aa0862c94807822ba'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
      AND r.target_model_code = 'MODEL-3130eba3747e4a5aa0862c94807822ba'
      AND r.field_code = 'MODEL_3130eba3747e4a5aa0862c94807822ba_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-1418ef020a03466fbb58f73fe3e7562e',
  tm.id, 'MODEL-44cc7511fc224085a51e2887569c933a',
  '所属设备管理', 'MODEL_44cc7511fc224085a51e2887569c933a_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
  AND tm.code = 'MODEL-44cc7511fc224085a51e2887569c933a'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
      AND r.target_model_code = 'MODEL-44cc7511fc224085a51e2887569c933a'
      AND r.field_code = 'MODEL_44cc7511fc224085a51e2887569c933a_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-1418ef020a03466fbb58f73fe3e7562e',
  tm.id, 'MODEL-50991eab2a714ed288d00c58ff01c810',
  '所属设备管理', 'MODEL_50991eab2a714ed288d00c58ff01c810_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
  AND tm.code = 'MODEL-50991eab2a714ed288d00c58ff01c810'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
      AND r.target_model_code = 'MODEL-50991eab2a714ed288d00c58ff01c810'
      AND r.field_code = 'MODEL_50991eab2a714ed288d00c58ff01c810_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-1418ef020a03466fbb58f73fe3e7562e',
  tm.id, 'MODEL-5a9b87c42571486998d363101c54ec8c',
  '所属设备管理', 'MODEL_5a9b87c42571486998d363101c54ec8c_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
  AND tm.code = 'MODEL-5a9b87c42571486998d363101c54ec8c'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
      AND r.target_model_code = 'MODEL-5a9b87c42571486998d363101c54ec8c'
      AND r.field_code = 'MODEL_5a9b87c42571486998d363101c54ec8c_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-1418ef020a03466fbb58f73fe3e7562e',
  tm.id, 'MODEL-5f6f9243c7bb4e23bfed2fc01b48cf06',
  '所属设备管理', 'MODEL_5f6f9243c7bb4e23bfed2fc01b48cf06_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
  AND tm.code = 'MODEL-5f6f9243c7bb4e23bfed2fc01b48cf06'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
      AND r.target_model_code = 'MODEL-5f6f9243c7bb4e23bfed2fc01b48cf06'
      AND r.field_code = 'MODEL_5f6f9243c7bb4e23bfed2fc01b48cf06_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-1418ef020a03466fbb58f73fe3e7562e',
  tm.id, 'MODEL-68554821800248cfacacbac1a53cdf37',
  '所属设备管理', 'MODEL_68554821800248cfacacbac1a53cdf37_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
  AND tm.code = 'MODEL-68554821800248cfacacbac1a53cdf37'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
      AND r.target_model_code = 'MODEL-68554821800248cfacacbac1a53cdf37'
      AND r.field_code = 'MODEL_68554821800248cfacacbac1a53cdf37_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-1418ef020a03466fbb58f73fe3e7562e',
  tm.id, 'MODEL-6fd43ab74f6f425f81f0ad547603a849',
  '所属设备管理', 'MODEL_6fd43ab74f6f425f81f0ad547603a849_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
  AND tm.code = 'MODEL-6fd43ab74f6f425f81f0ad547603a849'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
      AND r.target_model_code = 'MODEL-6fd43ab74f6f425f81f0ad547603a849'
      AND r.field_code = 'MODEL_6fd43ab74f6f425f81f0ad547603a849_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-1418ef020a03466fbb58f73fe3e7562e',
  tm.id, 'MODEL-70d2b066120c4b969f0b661d1d2fb664',
  '所属设备管理', 'MODEL_70d2b066120c4b969f0b661d1d2fb664_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
  AND tm.code = 'MODEL-70d2b066120c4b969f0b661d1d2fb664'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
      AND r.target_model_code = 'MODEL-70d2b066120c4b969f0b661d1d2fb664'
      AND r.field_code = 'MODEL_70d2b066120c4b969f0b661d1d2fb664_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-1418ef020a03466fbb58f73fe3e7562e',
  tm.id, 'MODEL-7c4fe9b2d52d41cca5383488a4a400a5',
  '所属设备管理', 'MODEL_7c4fe9b2d52d41cca5383488a4a400a5_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
  AND tm.code = 'MODEL-7c4fe9b2d52d41cca5383488a4a400a5'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
      AND r.target_model_code = 'MODEL-7c4fe9b2d52d41cca5383488a4a400a5'
      AND r.field_code = 'MODEL_7c4fe9b2d52d41cca5383488a4a400a5_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-1418ef020a03466fbb58f73fe3e7562e',
  tm.id, 'MODEL-7c746a60520347baaf64334625f07f31',
  '所属设备管理', 'MODEL_7c746a60520347baaf64334625f07f31_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
  AND tm.code = 'MODEL-7c746a60520347baaf64334625f07f31'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
      AND r.target_model_code = 'MODEL-7c746a60520347baaf64334625f07f31'
      AND r.field_code = 'MODEL_7c746a60520347baaf64334625f07f31_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-1418ef020a03466fbb58f73fe3e7562e',
  tm.id, 'MODEL-81132ce00d2041c6a4ee06ec1c01dac8',
  '所属设备管理', 'MODEL_81132ce00d2041c6a4ee06ec1c01dac8_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
  AND tm.code = 'MODEL-81132ce00d2041c6a4ee06ec1c01dac8'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
      AND r.target_model_code = 'MODEL-81132ce00d2041c6a4ee06ec1c01dac8'
      AND r.field_code = 'MODEL_81132ce00d2041c6a4ee06ec1c01dac8_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-1418ef020a03466fbb58f73fe3e7562e',
  tm.id, 'MODEL-88a38000121341e2950132da32baad90',
  '所属设备管理', 'MODEL_88a38000121341e2950132da32baad90_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
  AND tm.code = 'MODEL-88a38000121341e2950132da32baad90'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
      AND r.target_model_code = 'MODEL-88a38000121341e2950132da32baad90'
      AND r.field_code = 'MODEL_88a38000121341e2950132da32baad90_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-1418ef020a03466fbb58f73fe3e7562e',
  tm.id, 'MODEL-990370e140aa481a82158bc96dc0b006',
  '所属设备管理', 'MODEL_990370e140aa481a82158bc96dc0b006_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
  AND tm.code = 'MODEL-990370e140aa481a82158bc96dc0b006'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
      AND r.target_model_code = 'MODEL-990370e140aa481a82158bc96dc0b006'
      AND r.field_code = 'MODEL_990370e140aa481a82158bc96dc0b006_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-1418ef020a03466fbb58f73fe3e7562e',
  tm.id, 'MODEL-a4ce7668efe94d2fbc47a658be1a35d2',
  '所属设备管理', 'MODEL_a4ce7668efe94d2fbc47a658be1a35d2_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
  AND tm.code = 'MODEL-a4ce7668efe94d2fbc47a658be1a35d2'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
      AND r.target_model_code = 'MODEL-a4ce7668efe94d2fbc47a658be1a35d2'
      AND r.field_code = 'MODEL_a4ce7668efe94d2fbc47a658be1a35d2_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-1418ef020a03466fbb58f73fe3e7562e',
  tm.id, 'MODEL-a62c727c0a024e19a8221c68fcf0c09a',
  '所属设备管理', 'MODEL_a62c727c0a024e19a8221c68fcf0c09a_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
  AND tm.code = 'MODEL-a62c727c0a024e19a8221c68fcf0c09a'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
      AND r.target_model_code = 'MODEL-a62c727c0a024e19a8221c68fcf0c09a'
      AND r.field_code = 'MODEL_a62c727c0a024e19a8221c68fcf0c09a_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-1418ef020a03466fbb58f73fe3e7562e',
  tm.id, 'MODEL-b8251da851514341adef7f6f72995cf3',
  '所属设备管理', 'MODEL_b8251da851514341adef7f6f72995cf3_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
  AND tm.code = 'MODEL-b8251da851514341adef7f6f72995cf3'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
      AND r.target_model_code = 'MODEL-b8251da851514341adef7f6f72995cf3'
      AND r.field_code = 'MODEL_b8251da851514341adef7f6f72995cf3_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-1418ef020a03466fbb58f73fe3e7562e',
  tm.id, 'MODEL-b83f05dc94f34fb4807458c0a48ba488',
  '所属设备管理', 'MODEL_b83f05dc94f34fb4807458c0a48ba488_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
  AND tm.code = 'MODEL-b83f05dc94f34fb4807458c0a48ba488'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
      AND r.target_model_code = 'MODEL-b83f05dc94f34fb4807458c0a48ba488'
      AND r.field_code = 'MODEL_b83f05dc94f34fb4807458c0a48ba488_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-1418ef020a03466fbb58f73fe3e7562e',
  tm.id, 'MODEL-bc2cdffeab434f4d862c847fa99938ba',
  '所属设备管理', 'MODEL_bc2cdffeab434f4d862c847fa99938ba_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
  AND tm.code = 'MODEL-bc2cdffeab434f4d862c847fa99938ba'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
      AND r.target_model_code = 'MODEL-bc2cdffeab434f4d862c847fa99938ba'
      AND r.field_code = 'MODEL_bc2cdffeab434f4d862c847fa99938ba_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-1418ef020a03466fbb58f73fe3e7562e',
  tm.id, 'MODEL-bf41bd50df044f5faf43c4759353ccd1',
  '所属设备管理', 'MODEL_bf41bd50df044f5faf43c4759353ccd1_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
  AND tm.code = 'MODEL-bf41bd50df044f5faf43c4759353ccd1'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
      AND r.target_model_code = 'MODEL-bf41bd50df044f5faf43c4759353ccd1'
      AND r.field_code = 'MODEL_bf41bd50df044f5faf43c4759353ccd1_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-1418ef020a03466fbb58f73fe3e7562e',
  tm.id, 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9',
  '所属设备管理', 'MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
  AND tm.code = 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
      AND r.target_model_code = 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9'
      AND r.field_code = 'MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-1418ef020a03466fbb58f73fe3e7562e',
  tm.id, 'MODEL-c6bc32646f3e4bde8409120c5c290eee',
  '所属设备管理', 'MODEL_c6bc32646f3e4bde8409120c5c290eee_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
  AND tm.code = 'MODEL-c6bc32646f3e4bde8409120c5c290eee'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
      AND r.target_model_code = 'MODEL-c6bc32646f3e4bde8409120c5c290eee'
      AND r.field_code = 'MODEL_c6bc32646f3e4bde8409120c5c290eee_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-1418ef020a03466fbb58f73fe3e7562e',
  tm.id, 'MODEL-d446eef7329f45b4a374347e215fec5d',
  '所属设备管理', 'MODEL_d446eef7329f45b4a374347e215fec5d_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
  AND tm.code = 'MODEL-d446eef7329f45b4a374347e215fec5d'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
      AND r.target_model_code = 'MODEL-d446eef7329f45b4a374347e215fec5d'
      AND r.field_code = 'MODEL_d446eef7329f45b4a374347e215fec5d_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-1418ef020a03466fbb58f73fe3e7562e',
  tm.id, 'MODEL-e25f3089a99d4878a1ddd326ba0910bf',
  '所属设备管理', 'MODEL_e25f3089a99d4878a1ddd326ba0910bf_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
  AND tm.code = 'MODEL-e25f3089a99d4878a1ddd326ba0910bf'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
      AND r.target_model_code = 'MODEL-e25f3089a99d4878a1ddd326ba0910bf'
      AND r.field_code = 'MODEL_e25f3089a99d4878a1ddd326ba0910bf_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-1418ef020a03466fbb58f73fe3e7562e',
  tm.id, 'MODEL-e5af81c45bbc44d48374d8036032cc5d',
  '所属设备管理', 'MODEL_e5af81c45bbc44d48374d8036032cc5d_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
  AND tm.code = 'MODEL-e5af81c45bbc44d48374d8036032cc5d'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
      AND r.target_model_code = 'MODEL-e5af81c45bbc44d48374d8036032cc5d'
      AND r.field_code = 'MODEL_e5af81c45bbc44d48374d8036032cc5d_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-1418ef020a03466fbb58f73fe3e7562e',
  tm.id, 'MODEL-e5c45ff8ca074102829bba872e091bd1',
  '所属设备管理', 'MODEL_e5c45ff8ca074102829bba872e091bd1_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
  AND tm.code = 'MODEL-e5c45ff8ca074102829bba872e091bd1'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
      AND r.target_model_code = 'MODEL-e5c45ff8ca074102829bba872e091bd1'
      AND r.field_code = 'MODEL_e5c45ff8ca074102829bba872e091bd1_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-1418ef020a03466fbb58f73fe3e7562e',
  tm.id, 'MODEL-f5270d478dd849389e59a178cd512629',
  '所属设备管理', 'MODEL_f5270d478dd849389e59a178cd512629_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
  AND tm.code = 'MODEL-f5270d478dd849389e59a178cd512629'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
      AND r.target_model_code = 'MODEL-f5270d478dd849389e59a178cd512629'
      AND r.field_code = 'MODEL_f5270d478dd849389e59a178cd512629_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-1418ef020a03466fbb58f73fe3e7562e',
  tm.id, 'MODEL-f55ce56d31f741caa5a69801556834fe',
  '所属设备管理', 'MODEL_f55ce56d31f741caa5a69801556834fe_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
  AND tm.code = 'MODEL-f55ce56d31f741caa5a69801556834fe'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
      AND r.target_model_code = 'MODEL-f55ce56d31f741caa5a69801556834fe'
      AND r.field_code = 'MODEL_f55ce56d31f741caa5a69801556834fe_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-17c15377760a4656b487fb23d072c1a4',
  tm.id, 'MODEL-05e5c6a6c8fe4f3c9bd1dd86dd24a659',
  '所属设备管理', 'MODEL_05e5c6a6c8fe4f3c9bd1dd86dd24a659_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-17c15377760a4656b487fb23d072c1a4'
  AND tm.code = 'MODEL-05e5c6a6c8fe4f3c9bd1dd86dd24a659'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-17c15377760a4656b487fb23d072c1a4'
      AND r.target_model_code = 'MODEL-05e5c6a6c8fe4f3c9bd1dd86dd24a659'
      AND r.field_code = 'MODEL_05e5c6a6c8fe4f3c9bd1dd86dd24a659_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-17c15377760a4656b487fb23d072c1a4',
  tm.id, 'MODEL-123328b7bfe745ceb337d286ac64aa17',
  '所属设备管理', 'MODEL_123328b7bfe745ceb337d286ac64aa17_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-17c15377760a4656b487fb23d072c1a4'
  AND tm.code = 'MODEL-123328b7bfe745ceb337d286ac64aa17'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-17c15377760a4656b487fb23d072c1a4'
      AND r.target_model_code = 'MODEL-123328b7bfe745ceb337d286ac64aa17'
      AND r.field_code = 'MODEL_123328b7bfe745ceb337d286ac64aa17_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-17c15377760a4656b487fb23d072c1a4',
  tm.id, 'MODEL-17eb9be1c7404734af570ce040f1783b',
  '所属设备管理', 'MODEL_17eb9be1c7404734af570ce040f1783b_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-17c15377760a4656b487fb23d072c1a4'
  AND tm.code = 'MODEL-17eb9be1c7404734af570ce040f1783b'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-17c15377760a4656b487fb23d072c1a4'
      AND r.target_model_code = 'MODEL-17eb9be1c7404734af570ce040f1783b'
      AND r.field_code = 'MODEL_17eb9be1c7404734af570ce040f1783b_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-17c15377760a4656b487fb23d072c1a4',
  tm.id, 'MODEL-26eabac35cb644cc84ae64dfb5ff9ba4',
  '所属设备管理', 'MODEL_26eabac35cb644cc84ae64dfb5ff9ba4_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-17c15377760a4656b487fb23d072c1a4'
  AND tm.code = 'MODEL-26eabac35cb644cc84ae64dfb5ff9ba4'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-17c15377760a4656b487fb23d072c1a4'
      AND r.target_model_code = 'MODEL-26eabac35cb644cc84ae64dfb5ff9ba4'
      AND r.field_code = 'MODEL_26eabac35cb644cc84ae64dfb5ff9ba4_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-17c15377760a4656b487fb23d072c1a4',
  tm.id, 'MODEL-2af0515a5a36420086980b2322037edb',
  '所属设备管理', 'MODEL_2af0515a5a36420086980b2322037edb_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-17c15377760a4656b487fb23d072c1a4'
  AND tm.code = 'MODEL-2af0515a5a36420086980b2322037edb'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-17c15377760a4656b487fb23d072c1a4'
      AND r.target_model_code = 'MODEL-2af0515a5a36420086980b2322037edb'
      AND r.field_code = 'MODEL_2af0515a5a36420086980b2322037edb_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-17c15377760a4656b487fb23d072c1a4',
  tm.id, 'MODEL-3130eba3747e4a5aa0862c94807822ba',
  '所属设备管理', 'MODEL_3130eba3747e4a5aa0862c94807822ba_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-17c15377760a4656b487fb23d072c1a4'
  AND tm.code = 'MODEL-3130eba3747e4a5aa0862c94807822ba'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-17c15377760a4656b487fb23d072c1a4'
      AND r.target_model_code = 'MODEL-3130eba3747e4a5aa0862c94807822ba'
      AND r.field_code = 'MODEL_3130eba3747e4a5aa0862c94807822ba_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-17c15377760a4656b487fb23d072c1a4',
  tm.id, 'MODEL-44cc7511fc224085a51e2887569c933a',
  '所属设备管理', 'MODEL_44cc7511fc224085a51e2887569c933a_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-17c15377760a4656b487fb23d072c1a4'
  AND tm.code = 'MODEL-44cc7511fc224085a51e2887569c933a'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-17c15377760a4656b487fb23d072c1a4'
      AND r.target_model_code = 'MODEL-44cc7511fc224085a51e2887569c933a'
      AND r.field_code = 'MODEL_44cc7511fc224085a51e2887569c933a_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-17c15377760a4656b487fb23d072c1a4',
  tm.id, 'MODEL-50991eab2a714ed288d00c58ff01c810',
  '所属设备管理', 'MODEL_50991eab2a714ed288d00c58ff01c810_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-17c15377760a4656b487fb23d072c1a4'
  AND tm.code = 'MODEL-50991eab2a714ed288d00c58ff01c810'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-17c15377760a4656b487fb23d072c1a4'
      AND r.target_model_code = 'MODEL-50991eab2a714ed288d00c58ff01c810'
      AND r.field_code = 'MODEL_50991eab2a714ed288d00c58ff01c810_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-17c15377760a4656b487fb23d072c1a4',
  tm.id, 'MODEL-5a9b87c42571486998d363101c54ec8c',
  '所属设备管理', 'MODEL_5a9b87c42571486998d363101c54ec8c_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-17c15377760a4656b487fb23d072c1a4'
  AND tm.code = 'MODEL-5a9b87c42571486998d363101c54ec8c'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-17c15377760a4656b487fb23d072c1a4'
      AND r.target_model_code = 'MODEL-5a9b87c42571486998d363101c54ec8c'
      AND r.field_code = 'MODEL_5a9b87c42571486998d363101c54ec8c_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-17c15377760a4656b487fb23d072c1a4',
  tm.id, 'MODEL-5f6f9243c7bb4e23bfed2fc01b48cf06',
  '所属设备管理', 'MODEL_5f6f9243c7bb4e23bfed2fc01b48cf06_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-17c15377760a4656b487fb23d072c1a4'
  AND tm.code = 'MODEL-5f6f9243c7bb4e23bfed2fc01b48cf06'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-17c15377760a4656b487fb23d072c1a4'
      AND r.target_model_code = 'MODEL-5f6f9243c7bb4e23bfed2fc01b48cf06'
      AND r.field_code = 'MODEL_5f6f9243c7bb4e23bfed2fc01b48cf06_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-17c15377760a4656b487fb23d072c1a4',
  tm.id, 'MODEL-68554821800248cfacacbac1a53cdf37',
  '所属设备管理', 'MODEL_68554821800248cfacacbac1a53cdf37_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-17c15377760a4656b487fb23d072c1a4'
  AND tm.code = 'MODEL-68554821800248cfacacbac1a53cdf37'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-17c15377760a4656b487fb23d072c1a4'
      AND r.target_model_code = 'MODEL-68554821800248cfacacbac1a53cdf37'
      AND r.field_code = 'MODEL_68554821800248cfacacbac1a53cdf37_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-17c15377760a4656b487fb23d072c1a4',
  tm.id, 'MODEL-6fd43ab74f6f425f81f0ad547603a849',
  '所属设备管理', 'MODEL_6fd43ab74f6f425f81f0ad547603a849_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-17c15377760a4656b487fb23d072c1a4'
  AND tm.code = 'MODEL-6fd43ab74f6f425f81f0ad547603a849'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-17c15377760a4656b487fb23d072c1a4'
      AND r.target_model_code = 'MODEL-6fd43ab74f6f425f81f0ad547603a849'
      AND r.field_code = 'MODEL_6fd43ab74f6f425f81f0ad547603a849_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-17c15377760a4656b487fb23d072c1a4',
  tm.id, 'MODEL-70d2b066120c4b969f0b661d1d2fb664',
  '所属设备管理', 'MODEL_70d2b066120c4b969f0b661d1d2fb664_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-17c15377760a4656b487fb23d072c1a4'
  AND tm.code = 'MODEL-70d2b066120c4b969f0b661d1d2fb664'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-17c15377760a4656b487fb23d072c1a4'
      AND r.target_model_code = 'MODEL-70d2b066120c4b969f0b661d1d2fb664'
      AND r.field_code = 'MODEL_70d2b066120c4b969f0b661d1d2fb664_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-17c15377760a4656b487fb23d072c1a4',
  tm.id, 'MODEL-7c4fe9b2d52d41cca5383488a4a400a5',
  '所属设备管理', 'MODEL_7c4fe9b2d52d41cca5383488a4a400a5_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-17c15377760a4656b487fb23d072c1a4'
  AND tm.code = 'MODEL-7c4fe9b2d52d41cca5383488a4a400a5'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-17c15377760a4656b487fb23d072c1a4'
      AND r.target_model_code = 'MODEL-7c4fe9b2d52d41cca5383488a4a400a5'
      AND r.field_code = 'MODEL_7c4fe9b2d52d41cca5383488a4a400a5_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-17c15377760a4656b487fb23d072c1a4',
  tm.id, 'MODEL-7c746a60520347baaf64334625f07f31',
  '所属设备管理', 'MODEL_7c746a60520347baaf64334625f07f31_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-17c15377760a4656b487fb23d072c1a4'
  AND tm.code = 'MODEL-7c746a60520347baaf64334625f07f31'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-17c15377760a4656b487fb23d072c1a4'
      AND r.target_model_code = 'MODEL-7c746a60520347baaf64334625f07f31'
      AND r.field_code = 'MODEL_7c746a60520347baaf64334625f07f31_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-17c15377760a4656b487fb23d072c1a4',
  tm.id, 'MODEL-81132ce00d2041c6a4ee06ec1c01dac8',
  '所属设备管理', 'MODEL_81132ce00d2041c6a4ee06ec1c01dac8_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-17c15377760a4656b487fb23d072c1a4'
  AND tm.code = 'MODEL-81132ce00d2041c6a4ee06ec1c01dac8'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-17c15377760a4656b487fb23d072c1a4'
      AND r.target_model_code = 'MODEL-81132ce00d2041c6a4ee06ec1c01dac8'
      AND r.field_code = 'MODEL_81132ce00d2041c6a4ee06ec1c01dac8_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-17c15377760a4656b487fb23d072c1a4',
  tm.id, 'MODEL-88a38000121341e2950132da32baad90',
  '所属设备管理', 'MODEL_88a38000121341e2950132da32baad90_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-17c15377760a4656b487fb23d072c1a4'
  AND tm.code = 'MODEL-88a38000121341e2950132da32baad90'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-17c15377760a4656b487fb23d072c1a4'
      AND r.target_model_code = 'MODEL-88a38000121341e2950132da32baad90'
      AND r.field_code = 'MODEL_88a38000121341e2950132da32baad90_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-17c15377760a4656b487fb23d072c1a4',
  tm.id, 'MODEL-990370e140aa481a82158bc96dc0b006',
  '所属设备管理', 'MODEL_990370e140aa481a82158bc96dc0b006_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-17c15377760a4656b487fb23d072c1a4'
  AND tm.code = 'MODEL-990370e140aa481a82158bc96dc0b006'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-17c15377760a4656b487fb23d072c1a4'
      AND r.target_model_code = 'MODEL-990370e140aa481a82158bc96dc0b006'
      AND r.field_code = 'MODEL_990370e140aa481a82158bc96dc0b006_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-17c15377760a4656b487fb23d072c1a4',
  tm.id, 'MODEL-a4ce7668efe94d2fbc47a658be1a35d2',
  '所属设备管理', 'MODEL_a4ce7668efe94d2fbc47a658be1a35d2_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-17c15377760a4656b487fb23d072c1a4'
  AND tm.code = 'MODEL-a4ce7668efe94d2fbc47a658be1a35d2'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-17c15377760a4656b487fb23d072c1a4'
      AND r.target_model_code = 'MODEL-a4ce7668efe94d2fbc47a658be1a35d2'
      AND r.field_code = 'MODEL_a4ce7668efe94d2fbc47a658be1a35d2_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-17c15377760a4656b487fb23d072c1a4',
  tm.id, 'MODEL-a62c727c0a024e19a8221c68fcf0c09a',
  '所属设备管理', 'MODEL_a62c727c0a024e19a8221c68fcf0c09a_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-17c15377760a4656b487fb23d072c1a4'
  AND tm.code = 'MODEL-a62c727c0a024e19a8221c68fcf0c09a'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-17c15377760a4656b487fb23d072c1a4'
      AND r.target_model_code = 'MODEL-a62c727c0a024e19a8221c68fcf0c09a'
      AND r.field_code = 'MODEL_a62c727c0a024e19a8221c68fcf0c09a_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-17c15377760a4656b487fb23d072c1a4',
  tm.id, 'MODEL-b8251da851514341adef7f6f72995cf3',
  '所属设备管理', 'MODEL_b8251da851514341adef7f6f72995cf3_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-17c15377760a4656b487fb23d072c1a4'
  AND tm.code = 'MODEL-b8251da851514341adef7f6f72995cf3'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-17c15377760a4656b487fb23d072c1a4'
      AND r.target_model_code = 'MODEL-b8251da851514341adef7f6f72995cf3'
      AND r.field_code = 'MODEL_b8251da851514341adef7f6f72995cf3_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-17c15377760a4656b487fb23d072c1a4',
  tm.id, 'MODEL-b83f05dc94f34fb4807458c0a48ba488',
  '所属设备管理', 'MODEL_b83f05dc94f34fb4807458c0a48ba488_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-17c15377760a4656b487fb23d072c1a4'
  AND tm.code = 'MODEL-b83f05dc94f34fb4807458c0a48ba488'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-17c15377760a4656b487fb23d072c1a4'
      AND r.target_model_code = 'MODEL-b83f05dc94f34fb4807458c0a48ba488'
      AND r.field_code = 'MODEL_b83f05dc94f34fb4807458c0a48ba488_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-17c15377760a4656b487fb23d072c1a4',
  tm.id, 'MODEL-bc2cdffeab434f4d862c847fa99938ba',
  '所属设备管理', 'MODEL_bc2cdffeab434f4d862c847fa99938ba_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-17c15377760a4656b487fb23d072c1a4'
  AND tm.code = 'MODEL-bc2cdffeab434f4d862c847fa99938ba'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-17c15377760a4656b487fb23d072c1a4'
      AND r.target_model_code = 'MODEL-bc2cdffeab434f4d862c847fa99938ba'
      AND r.field_code = 'MODEL_bc2cdffeab434f4d862c847fa99938ba_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-17c15377760a4656b487fb23d072c1a4',
  tm.id, 'MODEL-bf41bd50df044f5faf43c4759353ccd1',
  '所属设备管理', 'MODEL_bf41bd50df044f5faf43c4759353ccd1_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-17c15377760a4656b487fb23d072c1a4'
  AND tm.code = 'MODEL-bf41bd50df044f5faf43c4759353ccd1'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-17c15377760a4656b487fb23d072c1a4'
      AND r.target_model_code = 'MODEL-bf41bd50df044f5faf43c4759353ccd1'
      AND r.field_code = 'MODEL_bf41bd50df044f5faf43c4759353ccd1_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-17c15377760a4656b487fb23d072c1a4',
  tm.id, 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9',
  '所属设备管理', 'MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-17c15377760a4656b487fb23d072c1a4'
  AND tm.code = 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-17c15377760a4656b487fb23d072c1a4'
      AND r.target_model_code = 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9'
      AND r.field_code = 'MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-17c15377760a4656b487fb23d072c1a4',
  tm.id, 'MODEL-c6bc32646f3e4bde8409120c5c290eee',
  '所属设备管理', 'MODEL_c6bc32646f3e4bde8409120c5c290eee_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-17c15377760a4656b487fb23d072c1a4'
  AND tm.code = 'MODEL-c6bc32646f3e4bde8409120c5c290eee'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-17c15377760a4656b487fb23d072c1a4'
      AND r.target_model_code = 'MODEL-c6bc32646f3e4bde8409120c5c290eee'
      AND r.field_code = 'MODEL_c6bc32646f3e4bde8409120c5c290eee_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-17c15377760a4656b487fb23d072c1a4',
  tm.id, 'MODEL-d446eef7329f45b4a374347e215fec5d',
  '所属设备管理', 'MODEL_d446eef7329f45b4a374347e215fec5d_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-17c15377760a4656b487fb23d072c1a4'
  AND tm.code = 'MODEL-d446eef7329f45b4a374347e215fec5d'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-17c15377760a4656b487fb23d072c1a4'
      AND r.target_model_code = 'MODEL-d446eef7329f45b4a374347e215fec5d'
      AND r.field_code = 'MODEL_d446eef7329f45b4a374347e215fec5d_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-17c15377760a4656b487fb23d072c1a4',
  tm.id, 'MODEL-e25f3089a99d4878a1ddd326ba0910bf',
  '所属设备管理', 'MODEL_e25f3089a99d4878a1ddd326ba0910bf_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-17c15377760a4656b487fb23d072c1a4'
  AND tm.code = 'MODEL-e25f3089a99d4878a1ddd326ba0910bf'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-17c15377760a4656b487fb23d072c1a4'
      AND r.target_model_code = 'MODEL-e25f3089a99d4878a1ddd326ba0910bf'
      AND r.field_code = 'MODEL_e25f3089a99d4878a1ddd326ba0910bf_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-17c15377760a4656b487fb23d072c1a4',
  tm.id, 'MODEL-e5af81c45bbc44d48374d8036032cc5d',
  '所属设备管理', 'MODEL_e5af81c45bbc44d48374d8036032cc5d_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-17c15377760a4656b487fb23d072c1a4'
  AND tm.code = 'MODEL-e5af81c45bbc44d48374d8036032cc5d'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-17c15377760a4656b487fb23d072c1a4'
      AND r.target_model_code = 'MODEL-e5af81c45bbc44d48374d8036032cc5d'
      AND r.field_code = 'MODEL_e5af81c45bbc44d48374d8036032cc5d_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-17c15377760a4656b487fb23d072c1a4',
  tm.id, 'MODEL-e5c45ff8ca074102829bba872e091bd1',
  '所属设备管理', 'MODEL_e5c45ff8ca074102829bba872e091bd1_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-17c15377760a4656b487fb23d072c1a4'
  AND tm.code = 'MODEL-e5c45ff8ca074102829bba872e091bd1'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-17c15377760a4656b487fb23d072c1a4'
      AND r.target_model_code = 'MODEL-e5c45ff8ca074102829bba872e091bd1'
      AND r.field_code = 'MODEL_e5c45ff8ca074102829bba872e091bd1_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-17c15377760a4656b487fb23d072c1a4',
  tm.id, 'MODEL-f5270d478dd849389e59a178cd512629',
  '所属设备管理', 'MODEL_f5270d478dd849389e59a178cd512629_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-17c15377760a4656b487fb23d072c1a4'
  AND tm.code = 'MODEL-f5270d478dd849389e59a178cd512629'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-17c15377760a4656b487fb23d072c1a4'
      AND r.target_model_code = 'MODEL-f5270d478dd849389e59a178cd512629'
      AND r.field_code = 'MODEL_f5270d478dd849389e59a178cd512629_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-17c15377760a4656b487fb23d072c1a4',
  tm.id, 'MODEL-f55ce56d31f741caa5a69801556834fe',
  '所属设备管理', 'MODEL_f55ce56d31f741caa5a69801556834fe_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-17c15377760a4656b487fb23d072c1a4'
  AND tm.code = 'MODEL-f55ce56d31f741caa5a69801556834fe'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-17c15377760a4656b487fb23d072c1a4'
      AND r.target_model_code = 'MODEL-f55ce56d31f741caa5a69801556834fe'
      AND r.field_code = 'MODEL_f55ce56d31f741caa5a69801556834fe_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-1b242833ff0e4d81b741e37ba125d543',
  tm.id, 'MODEL-17eb9be1c7404734af570ce040f1783b',
  '所属设备管理', 'MODEL_17eb9be1c7404734af570ce040f1783b_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-1b242833ff0e4d81b741e37ba125d543'
  AND tm.code = 'MODEL-17eb9be1c7404734af570ce040f1783b'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-1b242833ff0e4d81b741e37ba125d543'
      AND r.target_model_code = 'MODEL-17eb9be1c7404734af570ce040f1783b'
      AND r.field_code = 'MODEL_17eb9be1c7404734af570ce040f1783b_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-1b242833ff0e4d81b741e37ba125d543',
  tm.id, 'MODEL-26eabac35cb644cc84ae64dfb5ff9ba4',
  '所属设备管理', 'MODEL_26eabac35cb644cc84ae64dfb5ff9ba4_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-1b242833ff0e4d81b741e37ba125d543'
  AND tm.code = 'MODEL-26eabac35cb644cc84ae64dfb5ff9ba4'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-1b242833ff0e4d81b741e37ba125d543'
      AND r.target_model_code = 'MODEL-26eabac35cb644cc84ae64dfb5ff9ba4'
      AND r.field_code = 'MODEL_26eabac35cb644cc84ae64dfb5ff9ba4_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-1b242833ff0e4d81b741e37ba125d543',
  tm.id, 'MODEL-7c4fe9b2d52d41cca5383488a4a400a5',
  '所属设备管理', 'MODEL_7c4fe9b2d52d41cca5383488a4a400a5_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-1b242833ff0e4d81b741e37ba125d543'
  AND tm.code = 'MODEL-7c4fe9b2d52d41cca5383488a4a400a5'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-1b242833ff0e4d81b741e37ba125d543'
      AND r.target_model_code = 'MODEL-7c4fe9b2d52d41cca5383488a4a400a5'
      AND r.field_code = 'MODEL_7c4fe9b2d52d41cca5383488a4a400a5_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-1b242833ff0e4d81b741e37ba125d543',
  tm.id, 'MODEL-88a38000121341e2950132da32baad90',
  '所属设备管理', 'MODEL_88a38000121341e2950132da32baad90_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-1b242833ff0e4d81b741e37ba125d543'
  AND tm.code = 'MODEL-88a38000121341e2950132da32baad90'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-1b242833ff0e4d81b741e37ba125d543'
      AND r.target_model_code = 'MODEL-88a38000121341e2950132da32baad90'
      AND r.field_code = 'MODEL_88a38000121341e2950132da32baad90_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-1b242833ff0e4d81b741e37ba125d543',
  tm.id, 'MODEL-b83f05dc94f34fb4807458c0a48ba488',
  '所属设备管理', 'MODEL_b83f05dc94f34fb4807458c0a48ba488_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-1b242833ff0e4d81b741e37ba125d543'
  AND tm.code = 'MODEL-b83f05dc94f34fb4807458c0a48ba488'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-1b242833ff0e4d81b741e37ba125d543'
      AND r.target_model_code = 'MODEL-b83f05dc94f34fb4807458c0a48ba488'
      AND r.field_code = 'MODEL_b83f05dc94f34fb4807458c0a48ba488_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-1b242833ff0e4d81b741e37ba125d543',
  tm.id, 'MODEL-f55ce56d31f741caa5a69801556834fe',
  '所属设备管理', 'MODEL_f55ce56d31f741caa5a69801556834fe_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-1b242833ff0e4d81b741e37ba125d543'
  AND tm.code = 'MODEL-f55ce56d31f741caa5a69801556834fe'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-1b242833ff0e4d81b741e37ba125d543'
      AND r.target_model_code = 'MODEL-f55ce56d31f741caa5a69801556834fe'
      AND r.field_code = 'MODEL_f55ce56d31f741caa5a69801556834fe_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-3aba3622dd75479f8dfc02228540cc80',
  tm.id, 'MODEL-05e5c6a6c8fe4f3c9bd1dd86dd24a659',
  '所属设备管理', 'MODEL_05e5c6a6c8fe4f3c9bd1dd86dd24a659_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-3aba3622dd75479f8dfc02228540cc80'
  AND tm.code = 'MODEL-05e5c6a6c8fe4f3c9bd1dd86dd24a659'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-3aba3622dd75479f8dfc02228540cc80'
      AND r.target_model_code = 'MODEL-05e5c6a6c8fe4f3c9bd1dd86dd24a659'
      AND r.field_code = 'MODEL_05e5c6a6c8fe4f3c9bd1dd86dd24a659_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-3aba3622dd75479f8dfc02228540cc80',
  tm.id, 'MODEL-123328b7bfe745ceb337d286ac64aa17',
  '所属设备管理', 'MODEL_123328b7bfe745ceb337d286ac64aa17_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-3aba3622dd75479f8dfc02228540cc80'
  AND tm.code = 'MODEL-123328b7bfe745ceb337d286ac64aa17'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-3aba3622dd75479f8dfc02228540cc80'
      AND r.target_model_code = 'MODEL-123328b7bfe745ceb337d286ac64aa17'
      AND r.field_code = 'MODEL_123328b7bfe745ceb337d286ac64aa17_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-3aba3622dd75479f8dfc02228540cc80',
  tm.id, 'MODEL-17eb9be1c7404734af570ce040f1783b',
  '所属设备管理', 'MODEL_17eb9be1c7404734af570ce040f1783b_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-3aba3622dd75479f8dfc02228540cc80'
  AND tm.code = 'MODEL-17eb9be1c7404734af570ce040f1783b'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-3aba3622dd75479f8dfc02228540cc80'
      AND r.target_model_code = 'MODEL-17eb9be1c7404734af570ce040f1783b'
      AND r.field_code = 'MODEL_17eb9be1c7404734af570ce040f1783b_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-3aba3622dd75479f8dfc02228540cc80',
  tm.id, 'MODEL-26eabac35cb644cc84ae64dfb5ff9ba4',
  '所属设备管理', 'MODEL_26eabac35cb644cc84ae64dfb5ff9ba4_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-3aba3622dd75479f8dfc02228540cc80'
  AND tm.code = 'MODEL-26eabac35cb644cc84ae64dfb5ff9ba4'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-3aba3622dd75479f8dfc02228540cc80'
      AND r.target_model_code = 'MODEL-26eabac35cb644cc84ae64dfb5ff9ba4'
      AND r.field_code = 'MODEL_26eabac35cb644cc84ae64dfb5ff9ba4_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-3aba3622dd75479f8dfc02228540cc80',
  tm.id, 'MODEL-2af0515a5a36420086980b2322037edb',
  '所属设备管理', 'MODEL_2af0515a5a36420086980b2322037edb_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-3aba3622dd75479f8dfc02228540cc80'
  AND tm.code = 'MODEL-2af0515a5a36420086980b2322037edb'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-3aba3622dd75479f8dfc02228540cc80'
      AND r.target_model_code = 'MODEL-2af0515a5a36420086980b2322037edb'
      AND r.field_code = 'MODEL_2af0515a5a36420086980b2322037edb_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-3aba3622dd75479f8dfc02228540cc80',
  tm.id, 'MODEL-3130eba3747e4a5aa0862c94807822ba',
  '所属设备管理', 'MODEL_3130eba3747e4a5aa0862c94807822ba_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-3aba3622dd75479f8dfc02228540cc80'
  AND tm.code = 'MODEL-3130eba3747e4a5aa0862c94807822ba'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-3aba3622dd75479f8dfc02228540cc80'
      AND r.target_model_code = 'MODEL-3130eba3747e4a5aa0862c94807822ba'
      AND r.field_code = 'MODEL_3130eba3747e4a5aa0862c94807822ba_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-3aba3622dd75479f8dfc02228540cc80',
  tm.id, 'MODEL-44cc7511fc224085a51e2887569c933a',
  '所属设备管理', 'MODEL_44cc7511fc224085a51e2887569c933a_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-3aba3622dd75479f8dfc02228540cc80'
  AND tm.code = 'MODEL-44cc7511fc224085a51e2887569c933a'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-3aba3622dd75479f8dfc02228540cc80'
      AND r.target_model_code = 'MODEL-44cc7511fc224085a51e2887569c933a'
      AND r.field_code = 'MODEL_44cc7511fc224085a51e2887569c933a_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-3aba3622dd75479f8dfc02228540cc80',
  tm.id, 'MODEL-50991eab2a714ed288d00c58ff01c810',
  '所属设备管理', 'MODEL_50991eab2a714ed288d00c58ff01c810_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-3aba3622dd75479f8dfc02228540cc80'
  AND tm.code = 'MODEL-50991eab2a714ed288d00c58ff01c810'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-3aba3622dd75479f8dfc02228540cc80'
      AND r.target_model_code = 'MODEL-50991eab2a714ed288d00c58ff01c810'
      AND r.field_code = 'MODEL_50991eab2a714ed288d00c58ff01c810_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-3aba3622dd75479f8dfc02228540cc80',
  tm.id, 'MODEL-5a9b87c42571486998d363101c54ec8c',
  '所属设备管理', 'MODEL_5a9b87c42571486998d363101c54ec8c_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-3aba3622dd75479f8dfc02228540cc80'
  AND tm.code = 'MODEL-5a9b87c42571486998d363101c54ec8c'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-3aba3622dd75479f8dfc02228540cc80'
      AND r.target_model_code = 'MODEL-5a9b87c42571486998d363101c54ec8c'
      AND r.field_code = 'MODEL_5a9b87c42571486998d363101c54ec8c_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-3aba3622dd75479f8dfc02228540cc80',
  tm.id, 'MODEL-5f6f9243c7bb4e23bfed2fc01b48cf06',
  '所属设备管理', 'MODEL_5f6f9243c7bb4e23bfed2fc01b48cf06_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-3aba3622dd75479f8dfc02228540cc80'
  AND tm.code = 'MODEL-5f6f9243c7bb4e23bfed2fc01b48cf06'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-3aba3622dd75479f8dfc02228540cc80'
      AND r.target_model_code = 'MODEL-5f6f9243c7bb4e23bfed2fc01b48cf06'
      AND r.field_code = 'MODEL_5f6f9243c7bb4e23bfed2fc01b48cf06_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-3aba3622dd75479f8dfc02228540cc80',
  tm.id, 'MODEL-68554821800248cfacacbac1a53cdf37',
  '所属设备管理', 'MODEL_68554821800248cfacacbac1a53cdf37_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-3aba3622dd75479f8dfc02228540cc80'
  AND tm.code = 'MODEL-68554821800248cfacacbac1a53cdf37'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-3aba3622dd75479f8dfc02228540cc80'
      AND r.target_model_code = 'MODEL-68554821800248cfacacbac1a53cdf37'
      AND r.field_code = 'MODEL_68554821800248cfacacbac1a53cdf37_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-3aba3622dd75479f8dfc02228540cc80',
  tm.id, 'MODEL-6fd43ab74f6f425f81f0ad547603a849',
  '所属设备管理', 'MODEL_6fd43ab74f6f425f81f0ad547603a849_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-3aba3622dd75479f8dfc02228540cc80'
  AND tm.code = 'MODEL-6fd43ab74f6f425f81f0ad547603a849'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-3aba3622dd75479f8dfc02228540cc80'
      AND r.target_model_code = 'MODEL-6fd43ab74f6f425f81f0ad547603a849'
      AND r.field_code = 'MODEL_6fd43ab74f6f425f81f0ad547603a849_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-3aba3622dd75479f8dfc02228540cc80',
  tm.id, 'MODEL-70d2b066120c4b969f0b661d1d2fb664',
  '所属设备管理', 'MODEL_70d2b066120c4b969f0b661d1d2fb664_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-3aba3622dd75479f8dfc02228540cc80'
  AND tm.code = 'MODEL-70d2b066120c4b969f0b661d1d2fb664'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-3aba3622dd75479f8dfc02228540cc80'
      AND r.target_model_code = 'MODEL-70d2b066120c4b969f0b661d1d2fb664'
      AND r.field_code = 'MODEL_70d2b066120c4b969f0b661d1d2fb664_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-3aba3622dd75479f8dfc02228540cc80',
  tm.id, 'MODEL-7c4fe9b2d52d41cca5383488a4a400a5',
  '所属设备管理', 'MODEL_7c4fe9b2d52d41cca5383488a4a400a5_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-3aba3622dd75479f8dfc02228540cc80'
  AND tm.code = 'MODEL-7c4fe9b2d52d41cca5383488a4a400a5'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-3aba3622dd75479f8dfc02228540cc80'
      AND r.target_model_code = 'MODEL-7c4fe9b2d52d41cca5383488a4a400a5'
      AND r.field_code = 'MODEL_7c4fe9b2d52d41cca5383488a4a400a5_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-3aba3622dd75479f8dfc02228540cc80',
  tm.id, 'MODEL-7c746a60520347baaf64334625f07f31',
  '所属设备管理', 'MODEL_7c746a60520347baaf64334625f07f31_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-3aba3622dd75479f8dfc02228540cc80'
  AND tm.code = 'MODEL-7c746a60520347baaf64334625f07f31'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-3aba3622dd75479f8dfc02228540cc80'
      AND r.target_model_code = 'MODEL-7c746a60520347baaf64334625f07f31'
      AND r.field_code = 'MODEL_7c746a60520347baaf64334625f07f31_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-3aba3622dd75479f8dfc02228540cc80',
  tm.id, 'MODEL-81132ce00d2041c6a4ee06ec1c01dac8',
  '所属设备管理', 'MODEL_81132ce00d2041c6a4ee06ec1c01dac8_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-3aba3622dd75479f8dfc02228540cc80'
  AND tm.code = 'MODEL-81132ce00d2041c6a4ee06ec1c01dac8'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-3aba3622dd75479f8dfc02228540cc80'
      AND r.target_model_code = 'MODEL-81132ce00d2041c6a4ee06ec1c01dac8'
      AND r.field_code = 'MODEL_81132ce00d2041c6a4ee06ec1c01dac8_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-3aba3622dd75479f8dfc02228540cc80',
  tm.id, 'MODEL-88a38000121341e2950132da32baad90',
  '所属设备管理', 'MODEL_88a38000121341e2950132da32baad90_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-3aba3622dd75479f8dfc02228540cc80'
  AND tm.code = 'MODEL-88a38000121341e2950132da32baad90'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-3aba3622dd75479f8dfc02228540cc80'
      AND r.target_model_code = 'MODEL-88a38000121341e2950132da32baad90'
      AND r.field_code = 'MODEL_88a38000121341e2950132da32baad90_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-3aba3622dd75479f8dfc02228540cc80',
  tm.id, 'MODEL-990370e140aa481a82158bc96dc0b006',
  '所属设备管理', 'MODEL_990370e140aa481a82158bc96dc0b006_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-3aba3622dd75479f8dfc02228540cc80'
  AND tm.code = 'MODEL-990370e140aa481a82158bc96dc0b006'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-3aba3622dd75479f8dfc02228540cc80'
      AND r.target_model_code = 'MODEL-990370e140aa481a82158bc96dc0b006'
      AND r.field_code = 'MODEL_990370e140aa481a82158bc96dc0b006_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-3aba3622dd75479f8dfc02228540cc80',
  tm.id, 'MODEL-a4ce7668efe94d2fbc47a658be1a35d2',
  '所属设备管理', 'MODEL_a4ce7668efe94d2fbc47a658be1a35d2_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-3aba3622dd75479f8dfc02228540cc80'
  AND tm.code = 'MODEL-a4ce7668efe94d2fbc47a658be1a35d2'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-3aba3622dd75479f8dfc02228540cc80'
      AND r.target_model_code = 'MODEL-a4ce7668efe94d2fbc47a658be1a35d2'
      AND r.field_code = 'MODEL_a4ce7668efe94d2fbc47a658be1a35d2_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-3aba3622dd75479f8dfc02228540cc80',
  tm.id, 'MODEL-a62c727c0a024e19a8221c68fcf0c09a',
  '所属设备管理', 'MODEL_a62c727c0a024e19a8221c68fcf0c09a_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-3aba3622dd75479f8dfc02228540cc80'
  AND tm.code = 'MODEL-a62c727c0a024e19a8221c68fcf0c09a'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-3aba3622dd75479f8dfc02228540cc80'
      AND r.target_model_code = 'MODEL-a62c727c0a024e19a8221c68fcf0c09a'
      AND r.field_code = 'MODEL_a62c727c0a024e19a8221c68fcf0c09a_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-3aba3622dd75479f8dfc02228540cc80',
  tm.id, 'MODEL-b8251da851514341adef7f6f72995cf3',
  '所属设备管理', 'MODEL_b8251da851514341adef7f6f72995cf3_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-3aba3622dd75479f8dfc02228540cc80'
  AND tm.code = 'MODEL-b8251da851514341adef7f6f72995cf3'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-3aba3622dd75479f8dfc02228540cc80'
      AND r.target_model_code = 'MODEL-b8251da851514341adef7f6f72995cf3'
      AND r.field_code = 'MODEL_b8251da851514341adef7f6f72995cf3_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-3aba3622dd75479f8dfc02228540cc80',
  tm.id, 'MODEL-b83f05dc94f34fb4807458c0a48ba488',
  '所属设备管理', 'MODEL_b83f05dc94f34fb4807458c0a48ba488_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-3aba3622dd75479f8dfc02228540cc80'
  AND tm.code = 'MODEL-b83f05dc94f34fb4807458c0a48ba488'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-3aba3622dd75479f8dfc02228540cc80'
      AND r.target_model_code = 'MODEL-b83f05dc94f34fb4807458c0a48ba488'
      AND r.field_code = 'MODEL_b83f05dc94f34fb4807458c0a48ba488_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-3aba3622dd75479f8dfc02228540cc80',
  tm.id, 'MODEL-bc2cdffeab434f4d862c847fa99938ba',
  '所属设备管理', 'MODEL_bc2cdffeab434f4d862c847fa99938ba_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-3aba3622dd75479f8dfc02228540cc80'
  AND tm.code = 'MODEL-bc2cdffeab434f4d862c847fa99938ba'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-3aba3622dd75479f8dfc02228540cc80'
      AND r.target_model_code = 'MODEL-bc2cdffeab434f4d862c847fa99938ba'
      AND r.field_code = 'MODEL_bc2cdffeab434f4d862c847fa99938ba_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-3aba3622dd75479f8dfc02228540cc80',
  tm.id, 'MODEL-bf41bd50df044f5faf43c4759353ccd1',
  '所属设备管理', 'MODEL_bf41bd50df044f5faf43c4759353ccd1_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-3aba3622dd75479f8dfc02228540cc80'
  AND tm.code = 'MODEL-bf41bd50df044f5faf43c4759353ccd1'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-3aba3622dd75479f8dfc02228540cc80'
      AND r.target_model_code = 'MODEL-bf41bd50df044f5faf43c4759353ccd1'
      AND r.field_code = 'MODEL_bf41bd50df044f5faf43c4759353ccd1_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-3aba3622dd75479f8dfc02228540cc80',
  tm.id, 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9',
  '所属设备管理', 'MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-3aba3622dd75479f8dfc02228540cc80'
  AND tm.code = 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-3aba3622dd75479f8dfc02228540cc80'
      AND r.target_model_code = 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9'
      AND r.field_code = 'MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-3aba3622dd75479f8dfc02228540cc80',
  tm.id, 'MODEL-c6bc32646f3e4bde8409120c5c290eee',
  '所属设备管理', 'MODEL_c6bc32646f3e4bde8409120c5c290eee_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-3aba3622dd75479f8dfc02228540cc80'
  AND tm.code = 'MODEL-c6bc32646f3e4bde8409120c5c290eee'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-3aba3622dd75479f8dfc02228540cc80'
      AND r.target_model_code = 'MODEL-c6bc32646f3e4bde8409120c5c290eee'
      AND r.field_code = 'MODEL_c6bc32646f3e4bde8409120c5c290eee_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-3aba3622dd75479f8dfc02228540cc80',
  tm.id, 'MODEL-d446eef7329f45b4a374347e215fec5d',
  '所属设备管理', 'MODEL_d446eef7329f45b4a374347e215fec5d_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-3aba3622dd75479f8dfc02228540cc80'
  AND tm.code = 'MODEL-d446eef7329f45b4a374347e215fec5d'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-3aba3622dd75479f8dfc02228540cc80'
      AND r.target_model_code = 'MODEL-d446eef7329f45b4a374347e215fec5d'
      AND r.field_code = 'MODEL_d446eef7329f45b4a374347e215fec5d_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-3aba3622dd75479f8dfc02228540cc80',
  tm.id, 'MODEL-e25f3089a99d4878a1ddd326ba0910bf',
  '所属设备管理', 'MODEL_e25f3089a99d4878a1ddd326ba0910bf_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-3aba3622dd75479f8dfc02228540cc80'
  AND tm.code = 'MODEL-e25f3089a99d4878a1ddd326ba0910bf'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-3aba3622dd75479f8dfc02228540cc80'
      AND r.target_model_code = 'MODEL-e25f3089a99d4878a1ddd326ba0910bf'
      AND r.field_code = 'MODEL_e25f3089a99d4878a1ddd326ba0910bf_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-3aba3622dd75479f8dfc02228540cc80',
  tm.id, 'MODEL-e5af81c45bbc44d48374d8036032cc5d',
  '所属设备管理', 'MODEL_e5af81c45bbc44d48374d8036032cc5d_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-3aba3622dd75479f8dfc02228540cc80'
  AND tm.code = 'MODEL-e5af81c45bbc44d48374d8036032cc5d'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-3aba3622dd75479f8dfc02228540cc80'
      AND r.target_model_code = 'MODEL-e5af81c45bbc44d48374d8036032cc5d'
      AND r.field_code = 'MODEL_e5af81c45bbc44d48374d8036032cc5d_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-3aba3622dd75479f8dfc02228540cc80',
  tm.id, 'MODEL-e5c45ff8ca074102829bba872e091bd1',
  '所属设备管理', 'MODEL_e5c45ff8ca074102829bba872e091bd1_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-3aba3622dd75479f8dfc02228540cc80'
  AND tm.code = 'MODEL-e5c45ff8ca074102829bba872e091bd1'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-3aba3622dd75479f8dfc02228540cc80'
      AND r.target_model_code = 'MODEL-e5c45ff8ca074102829bba872e091bd1'
      AND r.field_code = 'MODEL_e5c45ff8ca074102829bba872e091bd1_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-3aba3622dd75479f8dfc02228540cc80',
  tm.id, 'MODEL-f5270d478dd849389e59a178cd512629',
  '所属设备管理', 'MODEL_f5270d478dd849389e59a178cd512629_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-3aba3622dd75479f8dfc02228540cc80'
  AND tm.code = 'MODEL-f5270d478dd849389e59a178cd512629'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-3aba3622dd75479f8dfc02228540cc80'
      AND r.target_model_code = 'MODEL-f5270d478dd849389e59a178cd512629'
      AND r.field_code = 'MODEL_f5270d478dd849389e59a178cd512629_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-3aba3622dd75479f8dfc02228540cc80',
  tm.id, 'MODEL-f55ce56d31f741caa5a69801556834fe',
  '所属设备管理', 'MODEL_f55ce56d31f741caa5a69801556834fe_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-3aba3622dd75479f8dfc02228540cc80'
  AND tm.code = 'MODEL-f55ce56d31f741caa5a69801556834fe'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-3aba3622dd75479f8dfc02228540cc80'
      AND r.target_model_code = 'MODEL-f55ce56d31f741caa5a69801556834fe'
      AND r.field_code = 'MODEL_f55ce56d31f741caa5a69801556834fe_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-51f858c2280c4935b96055fd1b60a5e0',
  tm.id, 'MODEL-05e5c6a6c8fe4f3c9bd1dd86dd24a659',
  '所属设备管理', 'MODEL_05e5c6a6c8fe4f3c9bd1dd86dd24a659_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
  AND tm.code = 'MODEL-05e5c6a6c8fe4f3c9bd1dd86dd24a659'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
      AND r.target_model_code = 'MODEL-05e5c6a6c8fe4f3c9bd1dd86dd24a659'
      AND r.field_code = 'MODEL_05e5c6a6c8fe4f3c9bd1dd86dd24a659_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-51f858c2280c4935b96055fd1b60a5e0',
  tm.id, 'MODEL-123328b7bfe745ceb337d286ac64aa17',
  '所属设备管理', 'MODEL_123328b7bfe745ceb337d286ac64aa17_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
  AND tm.code = 'MODEL-123328b7bfe745ceb337d286ac64aa17'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
      AND r.target_model_code = 'MODEL-123328b7bfe745ceb337d286ac64aa17'
      AND r.field_code = 'MODEL_123328b7bfe745ceb337d286ac64aa17_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-51f858c2280c4935b96055fd1b60a5e0',
  tm.id, 'MODEL-17eb9be1c7404734af570ce040f1783b',
  '所属设备管理', 'MODEL_17eb9be1c7404734af570ce040f1783b_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
  AND tm.code = 'MODEL-17eb9be1c7404734af570ce040f1783b'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
      AND r.target_model_code = 'MODEL-17eb9be1c7404734af570ce040f1783b'
      AND r.field_code = 'MODEL_17eb9be1c7404734af570ce040f1783b_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-51f858c2280c4935b96055fd1b60a5e0',
  tm.id, 'MODEL-26eabac35cb644cc84ae64dfb5ff9ba4',
  '所属设备管理', 'MODEL_26eabac35cb644cc84ae64dfb5ff9ba4_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
  AND tm.code = 'MODEL-26eabac35cb644cc84ae64dfb5ff9ba4'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
      AND r.target_model_code = 'MODEL-26eabac35cb644cc84ae64dfb5ff9ba4'
      AND r.field_code = 'MODEL_26eabac35cb644cc84ae64dfb5ff9ba4_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-51f858c2280c4935b96055fd1b60a5e0',
  tm.id, 'MODEL-2af0515a5a36420086980b2322037edb',
  '所属设备管理', 'MODEL_2af0515a5a36420086980b2322037edb_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
  AND tm.code = 'MODEL-2af0515a5a36420086980b2322037edb'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
      AND r.target_model_code = 'MODEL-2af0515a5a36420086980b2322037edb'
      AND r.field_code = 'MODEL_2af0515a5a36420086980b2322037edb_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-51f858c2280c4935b96055fd1b60a5e0',
  tm.id, 'MODEL-3130eba3747e4a5aa0862c94807822ba',
  '所属设备管理', 'MODEL_3130eba3747e4a5aa0862c94807822ba_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
  AND tm.code = 'MODEL-3130eba3747e4a5aa0862c94807822ba'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
      AND r.target_model_code = 'MODEL-3130eba3747e4a5aa0862c94807822ba'
      AND r.field_code = 'MODEL_3130eba3747e4a5aa0862c94807822ba_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-51f858c2280c4935b96055fd1b60a5e0',
  tm.id, 'MODEL-44cc7511fc224085a51e2887569c933a',
  '所属设备管理', 'MODEL_44cc7511fc224085a51e2887569c933a_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
  AND tm.code = 'MODEL-44cc7511fc224085a51e2887569c933a'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
      AND r.target_model_code = 'MODEL-44cc7511fc224085a51e2887569c933a'
      AND r.field_code = 'MODEL_44cc7511fc224085a51e2887569c933a_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-51f858c2280c4935b96055fd1b60a5e0',
  tm.id, 'MODEL-50991eab2a714ed288d00c58ff01c810',
  '所属设备管理', 'MODEL_50991eab2a714ed288d00c58ff01c810_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
  AND tm.code = 'MODEL-50991eab2a714ed288d00c58ff01c810'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
      AND r.target_model_code = 'MODEL-50991eab2a714ed288d00c58ff01c810'
      AND r.field_code = 'MODEL_50991eab2a714ed288d00c58ff01c810_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-51f858c2280c4935b96055fd1b60a5e0',
  tm.id, 'MODEL-5a9b87c42571486998d363101c54ec8c',
  '所属设备管理', 'MODEL_5a9b87c42571486998d363101c54ec8c_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
  AND tm.code = 'MODEL-5a9b87c42571486998d363101c54ec8c'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
      AND r.target_model_code = 'MODEL-5a9b87c42571486998d363101c54ec8c'
      AND r.field_code = 'MODEL_5a9b87c42571486998d363101c54ec8c_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-51f858c2280c4935b96055fd1b60a5e0',
  tm.id, 'MODEL-5f6f9243c7bb4e23bfed2fc01b48cf06',
  '所属设备管理', 'MODEL_5f6f9243c7bb4e23bfed2fc01b48cf06_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
  AND tm.code = 'MODEL-5f6f9243c7bb4e23bfed2fc01b48cf06'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
      AND r.target_model_code = 'MODEL-5f6f9243c7bb4e23bfed2fc01b48cf06'
      AND r.field_code = 'MODEL_5f6f9243c7bb4e23bfed2fc01b48cf06_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-51f858c2280c4935b96055fd1b60a5e0',
  tm.id, 'MODEL-68554821800248cfacacbac1a53cdf37',
  '所属设备管理', 'MODEL_68554821800248cfacacbac1a53cdf37_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
  AND tm.code = 'MODEL-68554821800248cfacacbac1a53cdf37'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
      AND r.target_model_code = 'MODEL-68554821800248cfacacbac1a53cdf37'
      AND r.field_code = 'MODEL_68554821800248cfacacbac1a53cdf37_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-51f858c2280c4935b96055fd1b60a5e0',
  tm.id, 'MODEL-6fd43ab74f6f425f81f0ad547603a849',
  '所属设备管理', 'MODEL_6fd43ab74f6f425f81f0ad547603a849_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
  AND tm.code = 'MODEL-6fd43ab74f6f425f81f0ad547603a849'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
      AND r.target_model_code = 'MODEL-6fd43ab74f6f425f81f0ad547603a849'
      AND r.field_code = 'MODEL_6fd43ab74f6f425f81f0ad547603a849_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-51f858c2280c4935b96055fd1b60a5e0',
  tm.id, 'MODEL-70d2b066120c4b969f0b661d1d2fb664',
  '所属设备管理', 'MODEL_70d2b066120c4b969f0b661d1d2fb664_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
  AND tm.code = 'MODEL-70d2b066120c4b969f0b661d1d2fb664'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
      AND r.target_model_code = 'MODEL-70d2b066120c4b969f0b661d1d2fb664'
      AND r.field_code = 'MODEL_70d2b066120c4b969f0b661d1d2fb664_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-51f858c2280c4935b96055fd1b60a5e0',
  tm.id, 'MODEL-7c4fe9b2d52d41cca5383488a4a400a5',
  '所属设备管理', 'MODEL_7c4fe9b2d52d41cca5383488a4a400a5_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
  AND tm.code = 'MODEL-7c4fe9b2d52d41cca5383488a4a400a5'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
      AND r.target_model_code = 'MODEL-7c4fe9b2d52d41cca5383488a4a400a5'
      AND r.field_code = 'MODEL_7c4fe9b2d52d41cca5383488a4a400a5_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-51f858c2280c4935b96055fd1b60a5e0',
  tm.id, 'MODEL-7c746a60520347baaf64334625f07f31',
  '所属设备管理', 'MODEL_7c746a60520347baaf64334625f07f31_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
  AND tm.code = 'MODEL-7c746a60520347baaf64334625f07f31'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
      AND r.target_model_code = 'MODEL-7c746a60520347baaf64334625f07f31'
      AND r.field_code = 'MODEL_7c746a60520347baaf64334625f07f31_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-51f858c2280c4935b96055fd1b60a5e0',
  tm.id, 'MODEL-81132ce00d2041c6a4ee06ec1c01dac8',
  '所属设备管理', 'MODEL_81132ce00d2041c6a4ee06ec1c01dac8_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
  AND tm.code = 'MODEL-81132ce00d2041c6a4ee06ec1c01dac8'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
      AND r.target_model_code = 'MODEL-81132ce00d2041c6a4ee06ec1c01dac8'
      AND r.field_code = 'MODEL_81132ce00d2041c6a4ee06ec1c01dac8_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-51f858c2280c4935b96055fd1b60a5e0',
  tm.id, 'MODEL-88a38000121341e2950132da32baad90',
  '所属设备管理', 'MODEL_88a38000121341e2950132da32baad90_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
  AND tm.code = 'MODEL-88a38000121341e2950132da32baad90'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
      AND r.target_model_code = 'MODEL-88a38000121341e2950132da32baad90'
      AND r.field_code = 'MODEL_88a38000121341e2950132da32baad90_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-51f858c2280c4935b96055fd1b60a5e0',
  tm.id, 'MODEL-990370e140aa481a82158bc96dc0b006',
  '所属设备管理', 'MODEL_990370e140aa481a82158bc96dc0b006_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
  AND tm.code = 'MODEL-990370e140aa481a82158bc96dc0b006'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
      AND r.target_model_code = 'MODEL-990370e140aa481a82158bc96dc0b006'
      AND r.field_code = 'MODEL_990370e140aa481a82158bc96dc0b006_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-51f858c2280c4935b96055fd1b60a5e0',
  tm.id, 'MODEL-a4ce7668efe94d2fbc47a658be1a35d2',
  '所属设备管理', 'MODEL_a4ce7668efe94d2fbc47a658be1a35d2_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
  AND tm.code = 'MODEL-a4ce7668efe94d2fbc47a658be1a35d2'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
      AND r.target_model_code = 'MODEL-a4ce7668efe94d2fbc47a658be1a35d2'
      AND r.field_code = 'MODEL_a4ce7668efe94d2fbc47a658be1a35d2_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-51f858c2280c4935b96055fd1b60a5e0',
  tm.id, 'MODEL-a62c727c0a024e19a8221c68fcf0c09a',
  '所属设备管理', 'MODEL_a62c727c0a024e19a8221c68fcf0c09a_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
  AND tm.code = 'MODEL-a62c727c0a024e19a8221c68fcf0c09a'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
      AND r.target_model_code = 'MODEL-a62c727c0a024e19a8221c68fcf0c09a'
      AND r.field_code = 'MODEL_a62c727c0a024e19a8221c68fcf0c09a_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-51f858c2280c4935b96055fd1b60a5e0',
  tm.id, 'MODEL-b8251da851514341adef7f6f72995cf3',
  '所属设备管理', 'MODEL_b8251da851514341adef7f6f72995cf3_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
  AND tm.code = 'MODEL-b8251da851514341adef7f6f72995cf3'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
      AND r.target_model_code = 'MODEL-b8251da851514341adef7f6f72995cf3'
      AND r.field_code = 'MODEL_b8251da851514341adef7f6f72995cf3_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-51f858c2280c4935b96055fd1b60a5e0',
  tm.id, 'MODEL-b83f05dc94f34fb4807458c0a48ba488',
  '所属设备管理', 'MODEL_b83f05dc94f34fb4807458c0a48ba488_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
  AND tm.code = 'MODEL-b83f05dc94f34fb4807458c0a48ba488'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
      AND r.target_model_code = 'MODEL-b83f05dc94f34fb4807458c0a48ba488'
      AND r.field_code = 'MODEL_b83f05dc94f34fb4807458c0a48ba488_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-51f858c2280c4935b96055fd1b60a5e0',
  tm.id, 'MODEL-bc2cdffeab434f4d862c847fa99938ba',
  '所属设备管理', 'MODEL_bc2cdffeab434f4d862c847fa99938ba_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
  AND tm.code = 'MODEL-bc2cdffeab434f4d862c847fa99938ba'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
      AND r.target_model_code = 'MODEL-bc2cdffeab434f4d862c847fa99938ba'
      AND r.field_code = 'MODEL_bc2cdffeab434f4d862c847fa99938ba_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-51f858c2280c4935b96055fd1b60a5e0',
  tm.id, 'MODEL-bf41bd50df044f5faf43c4759353ccd1',
  '所属设备管理', 'MODEL_bf41bd50df044f5faf43c4759353ccd1_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
  AND tm.code = 'MODEL-bf41bd50df044f5faf43c4759353ccd1'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
      AND r.target_model_code = 'MODEL-bf41bd50df044f5faf43c4759353ccd1'
      AND r.field_code = 'MODEL_bf41bd50df044f5faf43c4759353ccd1_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-51f858c2280c4935b96055fd1b60a5e0',
  tm.id, 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9',
  '所属设备管理', 'MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
  AND tm.code = 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
      AND r.target_model_code = 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9'
      AND r.field_code = 'MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-51f858c2280c4935b96055fd1b60a5e0',
  tm.id, 'MODEL-c6bc32646f3e4bde8409120c5c290eee',
  '所属设备管理', 'MODEL_c6bc32646f3e4bde8409120c5c290eee_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
  AND tm.code = 'MODEL-c6bc32646f3e4bde8409120c5c290eee'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
      AND r.target_model_code = 'MODEL-c6bc32646f3e4bde8409120c5c290eee'
      AND r.field_code = 'MODEL_c6bc32646f3e4bde8409120c5c290eee_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-51f858c2280c4935b96055fd1b60a5e0',
  tm.id, 'MODEL-d446eef7329f45b4a374347e215fec5d',
  '所属设备管理', 'MODEL_d446eef7329f45b4a374347e215fec5d_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
  AND tm.code = 'MODEL-d446eef7329f45b4a374347e215fec5d'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
      AND r.target_model_code = 'MODEL-d446eef7329f45b4a374347e215fec5d'
      AND r.field_code = 'MODEL_d446eef7329f45b4a374347e215fec5d_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-51f858c2280c4935b96055fd1b60a5e0',
  tm.id, 'MODEL-e25f3089a99d4878a1ddd326ba0910bf',
  '所属设备管理', 'MODEL_e25f3089a99d4878a1ddd326ba0910bf_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
  AND tm.code = 'MODEL-e25f3089a99d4878a1ddd326ba0910bf'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
      AND r.target_model_code = 'MODEL-e25f3089a99d4878a1ddd326ba0910bf'
      AND r.field_code = 'MODEL_e25f3089a99d4878a1ddd326ba0910bf_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-51f858c2280c4935b96055fd1b60a5e0',
  tm.id, 'MODEL-e5af81c45bbc44d48374d8036032cc5d',
  '所属设备管理', 'MODEL_e5af81c45bbc44d48374d8036032cc5d_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
  AND tm.code = 'MODEL-e5af81c45bbc44d48374d8036032cc5d'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
      AND r.target_model_code = 'MODEL-e5af81c45bbc44d48374d8036032cc5d'
      AND r.field_code = 'MODEL_e5af81c45bbc44d48374d8036032cc5d_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-51f858c2280c4935b96055fd1b60a5e0',
  tm.id, 'MODEL-e5c45ff8ca074102829bba872e091bd1',
  '所属设备管理', 'MODEL_e5c45ff8ca074102829bba872e091bd1_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
  AND tm.code = 'MODEL-e5c45ff8ca074102829bba872e091bd1'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
      AND r.target_model_code = 'MODEL-e5c45ff8ca074102829bba872e091bd1'
      AND r.field_code = 'MODEL_e5c45ff8ca074102829bba872e091bd1_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-51f858c2280c4935b96055fd1b60a5e0',
  tm.id, 'MODEL-f5270d478dd849389e59a178cd512629',
  '所属设备管理', 'MODEL_f5270d478dd849389e59a178cd512629_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
  AND tm.code = 'MODEL-f5270d478dd849389e59a178cd512629'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
      AND r.target_model_code = 'MODEL-f5270d478dd849389e59a178cd512629'
      AND r.field_code = 'MODEL_f5270d478dd849389e59a178cd512629_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-51f858c2280c4935b96055fd1b60a5e0',
  tm.id, 'MODEL-f55ce56d31f741caa5a69801556834fe',
  '所属设备管理', 'MODEL_f55ce56d31f741caa5a69801556834fe_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
  AND tm.code = 'MODEL-f55ce56d31f741caa5a69801556834fe'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
      AND r.target_model_code = 'MODEL-f55ce56d31f741caa5a69801556834fe'
      AND r.field_code = 'MODEL_f55ce56d31f741caa5a69801556834fe_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2',
  tm.id, 'MODEL-05e5c6a6c8fe4f3c9bd1dd86dd24a659',
  '所属设备管理', 'MODEL_05e5c6a6c8fe4f3c9bd1dd86dd24a659_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2'
  AND tm.code = 'MODEL-05e5c6a6c8fe4f3c9bd1dd86dd24a659'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2'
      AND r.target_model_code = 'MODEL-05e5c6a6c8fe4f3c9bd1dd86dd24a659'
      AND r.field_code = 'MODEL_05e5c6a6c8fe4f3c9bd1dd86dd24a659_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2',
  tm.id, 'MODEL-123328b7bfe745ceb337d286ac64aa17',
  '所属设备管理', 'MODEL_123328b7bfe745ceb337d286ac64aa17_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2'
  AND tm.code = 'MODEL-123328b7bfe745ceb337d286ac64aa17'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2'
      AND r.target_model_code = 'MODEL-123328b7bfe745ceb337d286ac64aa17'
      AND r.field_code = 'MODEL_123328b7bfe745ceb337d286ac64aa17_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2',
  tm.id, 'MODEL-17eb9be1c7404734af570ce040f1783b',
  '所属设备管理', 'MODEL_17eb9be1c7404734af570ce040f1783b_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2'
  AND tm.code = 'MODEL-17eb9be1c7404734af570ce040f1783b'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2'
      AND r.target_model_code = 'MODEL-17eb9be1c7404734af570ce040f1783b'
      AND r.field_code = 'MODEL_17eb9be1c7404734af570ce040f1783b_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2',
  tm.id, 'MODEL-26eabac35cb644cc84ae64dfb5ff9ba4',
  '所属设备管理', 'MODEL_26eabac35cb644cc84ae64dfb5ff9ba4_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2'
  AND tm.code = 'MODEL-26eabac35cb644cc84ae64dfb5ff9ba4'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2'
      AND r.target_model_code = 'MODEL-26eabac35cb644cc84ae64dfb5ff9ba4'
      AND r.field_code = 'MODEL_26eabac35cb644cc84ae64dfb5ff9ba4_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2',
  tm.id, 'MODEL-2af0515a5a36420086980b2322037edb',
  '所属设备管理', 'MODEL_2af0515a5a36420086980b2322037edb_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2'
  AND tm.code = 'MODEL-2af0515a5a36420086980b2322037edb'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2'
      AND r.target_model_code = 'MODEL-2af0515a5a36420086980b2322037edb'
      AND r.field_code = 'MODEL_2af0515a5a36420086980b2322037edb_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2',
  tm.id, 'MODEL-3130eba3747e4a5aa0862c94807822ba',
  '所属设备管理', 'MODEL_3130eba3747e4a5aa0862c94807822ba_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2'
  AND tm.code = 'MODEL-3130eba3747e4a5aa0862c94807822ba'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2'
      AND r.target_model_code = 'MODEL-3130eba3747e4a5aa0862c94807822ba'
      AND r.field_code = 'MODEL_3130eba3747e4a5aa0862c94807822ba_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2',
  tm.id, 'MODEL-44cc7511fc224085a51e2887569c933a',
  '所属设备管理', 'MODEL_44cc7511fc224085a51e2887569c933a_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2'
  AND tm.code = 'MODEL-44cc7511fc224085a51e2887569c933a'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2'
      AND r.target_model_code = 'MODEL-44cc7511fc224085a51e2887569c933a'
      AND r.field_code = 'MODEL_44cc7511fc224085a51e2887569c933a_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2',
  tm.id, 'MODEL-50991eab2a714ed288d00c58ff01c810',
  '所属设备管理', 'MODEL_50991eab2a714ed288d00c58ff01c810_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2'
  AND tm.code = 'MODEL-50991eab2a714ed288d00c58ff01c810'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2'
      AND r.target_model_code = 'MODEL-50991eab2a714ed288d00c58ff01c810'
      AND r.field_code = 'MODEL_50991eab2a714ed288d00c58ff01c810_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2',
  tm.id, 'MODEL-5a9b87c42571486998d363101c54ec8c',
  '所属设备管理', 'MODEL_5a9b87c42571486998d363101c54ec8c_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2'
  AND tm.code = 'MODEL-5a9b87c42571486998d363101c54ec8c'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2'
      AND r.target_model_code = 'MODEL-5a9b87c42571486998d363101c54ec8c'
      AND r.field_code = 'MODEL_5a9b87c42571486998d363101c54ec8c_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2',
  tm.id, 'MODEL-5f6f9243c7bb4e23bfed2fc01b48cf06',
  '所属设备管理', 'MODEL_5f6f9243c7bb4e23bfed2fc01b48cf06_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2'
  AND tm.code = 'MODEL-5f6f9243c7bb4e23bfed2fc01b48cf06'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2'
      AND r.target_model_code = 'MODEL-5f6f9243c7bb4e23bfed2fc01b48cf06'
      AND r.field_code = 'MODEL_5f6f9243c7bb4e23bfed2fc01b48cf06_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2',
  tm.id, 'MODEL-68554821800248cfacacbac1a53cdf37',
  '所属设备管理', 'MODEL_68554821800248cfacacbac1a53cdf37_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2'
  AND tm.code = 'MODEL-68554821800248cfacacbac1a53cdf37'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2'
      AND r.target_model_code = 'MODEL-68554821800248cfacacbac1a53cdf37'
      AND r.field_code = 'MODEL_68554821800248cfacacbac1a53cdf37_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2',
  tm.id, 'MODEL-6fd43ab74f6f425f81f0ad547603a849',
  '所属设备管理', 'MODEL_6fd43ab74f6f425f81f0ad547603a849_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2'
  AND tm.code = 'MODEL-6fd43ab74f6f425f81f0ad547603a849'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2'
      AND r.target_model_code = 'MODEL-6fd43ab74f6f425f81f0ad547603a849'
      AND r.field_code = 'MODEL_6fd43ab74f6f425f81f0ad547603a849_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2',
  tm.id, 'MODEL-70d2b066120c4b969f0b661d1d2fb664',
  '所属设备管理', 'MODEL_70d2b066120c4b969f0b661d1d2fb664_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2'
  AND tm.code = 'MODEL-70d2b066120c4b969f0b661d1d2fb664'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2'
      AND r.target_model_code = 'MODEL-70d2b066120c4b969f0b661d1d2fb664'
      AND r.field_code = 'MODEL_70d2b066120c4b969f0b661d1d2fb664_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2',
  tm.id, 'MODEL-7c4fe9b2d52d41cca5383488a4a400a5',
  '所属设备管理', 'MODEL_7c4fe9b2d52d41cca5383488a4a400a5_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2'
  AND tm.code = 'MODEL-7c4fe9b2d52d41cca5383488a4a400a5'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2'
      AND r.target_model_code = 'MODEL-7c4fe9b2d52d41cca5383488a4a400a5'
      AND r.field_code = 'MODEL_7c4fe9b2d52d41cca5383488a4a400a5_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2',
  tm.id, 'MODEL-7c746a60520347baaf64334625f07f31',
  '所属设备管理', 'MODEL_7c746a60520347baaf64334625f07f31_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2'
  AND tm.code = 'MODEL-7c746a60520347baaf64334625f07f31'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2'
      AND r.target_model_code = 'MODEL-7c746a60520347baaf64334625f07f31'
      AND r.field_code = 'MODEL_7c746a60520347baaf64334625f07f31_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2',
  tm.id, 'MODEL-81132ce00d2041c6a4ee06ec1c01dac8',
  '所属设备管理', 'MODEL_81132ce00d2041c6a4ee06ec1c01dac8_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2'
  AND tm.code = 'MODEL-81132ce00d2041c6a4ee06ec1c01dac8'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2'
      AND r.target_model_code = 'MODEL-81132ce00d2041c6a4ee06ec1c01dac8'
      AND r.field_code = 'MODEL_81132ce00d2041c6a4ee06ec1c01dac8_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2',
  tm.id, 'MODEL-88a38000121341e2950132da32baad90',
  '所属设备管理', 'MODEL_88a38000121341e2950132da32baad90_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2'
  AND tm.code = 'MODEL-88a38000121341e2950132da32baad90'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2'
      AND r.target_model_code = 'MODEL-88a38000121341e2950132da32baad90'
      AND r.field_code = 'MODEL_88a38000121341e2950132da32baad90_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2',
  tm.id, 'MODEL-990370e140aa481a82158bc96dc0b006',
  '所属设备管理', 'MODEL_990370e140aa481a82158bc96dc0b006_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2'
  AND tm.code = 'MODEL-990370e140aa481a82158bc96dc0b006'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2'
      AND r.target_model_code = 'MODEL-990370e140aa481a82158bc96dc0b006'
      AND r.field_code = 'MODEL_990370e140aa481a82158bc96dc0b006_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2',
  tm.id, 'MODEL-a4ce7668efe94d2fbc47a658be1a35d2',
  '所属设备管理', 'MODEL_a4ce7668efe94d2fbc47a658be1a35d2_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2'
  AND tm.code = 'MODEL-a4ce7668efe94d2fbc47a658be1a35d2'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2'
      AND r.target_model_code = 'MODEL-a4ce7668efe94d2fbc47a658be1a35d2'
      AND r.field_code = 'MODEL_a4ce7668efe94d2fbc47a658be1a35d2_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2',
  tm.id, 'MODEL-a62c727c0a024e19a8221c68fcf0c09a',
  '所属设备管理', 'MODEL_a62c727c0a024e19a8221c68fcf0c09a_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2'
  AND tm.code = 'MODEL-a62c727c0a024e19a8221c68fcf0c09a'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2'
      AND r.target_model_code = 'MODEL-a62c727c0a024e19a8221c68fcf0c09a'
      AND r.field_code = 'MODEL_a62c727c0a024e19a8221c68fcf0c09a_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2',
  tm.id, 'MODEL-b8251da851514341adef7f6f72995cf3',
  '所属设备管理', 'MODEL_b8251da851514341adef7f6f72995cf3_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2'
  AND tm.code = 'MODEL-b8251da851514341adef7f6f72995cf3'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2'
      AND r.target_model_code = 'MODEL-b8251da851514341adef7f6f72995cf3'
      AND r.field_code = 'MODEL_b8251da851514341adef7f6f72995cf3_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2',
  tm.id, 'MODEL-b83f05dc94f34fb4807458c0a48ba488',
  '所属设备管理', 'MODEL_b83f05dc94f34fb4807458c0a48ba488_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2'
  AND tm.code = 'MODEL-b83f05dc94f34fb4807458c0a48ba488'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2'
      AND r.target_model_code = 'MODEL-b83f05dc94f34fb4807458c0a48ba488'
      AND r.field_code = 'MODEL_b83f05dc94f34fb4807458c0a48ba488_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2',
  tm.id, 'MODEL-bc2cdffeab434f4d862c847fa99938ba',
  '所属设备管理', 'MODEL_bc2cdffeab434f4d862c847fa99938ba_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2'
  AND tm.code = 'MODEL-bc2cdffeab434f4d862c847fa99938ba'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2'
      AND r.target_model_code = 'MODEL-bc2cdffeab434f4d862c847fa99938ba'
      AND r.field_code = 'MODEL_bc2cdffeab434f4d862c847fa99938ba_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2',
  tm.id, 'MODEL-bf41bd50df044f5faf43c4759353ccd1',
  '所属设备管理', 'MODEL_bf41bd50df044f5faf43c4759353ccd1_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2'
  AND tm.code = 'MODEL-bf41bd50df044f5faf43c4759353ccd1'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2'
      AND r.target_model_code = 'MODEL-bf41bd50df044f5faf43c4759353ccd1'
      AND r.field_code = 'MODEL_bf41bd50df044f5faf43c4759353ccd1_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2',
  tm.id, 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9',
  '所属设备管理', 'MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2'
  AND tm.code = 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2'
      AND r.target_model_code = 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9'
      AND r.field_code = 'MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2',
  tm.id, 'MODEL-c6bc32646f3e4bde8409120c5c290eee',
  '所属设备管理', 'MODEL_c6bc32646f3e4bde8409120c5c290eee_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2'
  AND tm.code = 'MODEL-c6bc32646f3e4bde8409120c5c290eee'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2'
      AND r.target_model_code = 'MODEL-c6bc32646f3e4bde8409120c5c290eee'
      AND r.field_code = 'MODEL_c6bc32646f3e4bde8409120c5c290eee_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2',
  tm.id, 'MODEL-d446eef7329f45b4a374347e215fec5d',
  '所属设备管理', 'MODEL_d446eef7329f45b4a374347e215fec5d_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2'
  AND tm.code = 'MODEL-d446eef7329f45b4a374347e215fec5d'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2'
      AND r.target_model_code = 'MODEL-d446eef7329f45b4a374347e215fec5d'
      AND r.field_code = 'MODEL_d446eef7329f45b4a374347e215fec5d_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2',
  tm.id, 'MODEL-e25f3089a99d4878a1ddd326ba0910bf',
  '所属设备管理', 'MODEL_e25f3089a99d4878a1ddd326ba0910bf_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2'
  AND tm.code = 'MODEL-e25f3089a99d4878a1ddd326ba0910bf'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2'
      AND r.target_model_code = 'MODEL-e25f3089a99d4878a1ddd326ba0910bf'
      AND r.field_code = 'MODEL_e25f3089a99d4878a1ddd326ba0910bf_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2',
  tm.id, 'MODEL-e5af81c45bbc44d48374d8036032cc5d',
  '所属设备管理', 'MODEL_e5af81c45bbc44d48374d8036032cc5d_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2'
  AND tm.code = 'MODEL-e5af81c45bbc44d48374d8036032cc5d'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2'
      AND r.target_model_code = 'MODEL-e5af81c45bbc44d48374d8036032cc5d'
      AND r.field_code = 'MODEL_e5af81c45bbc44d48374d8036032cc5d_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2',
  tm.id, 'MODEL-e5c45ff8ca074102829bba872e091bd1',
  '所属设备管理', 'MODEL_e5c45ff8ca074102829bba872e091bd1_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2'
  AND tm.code = 'MODEL-e5c45ff8ca074102829bba872e091bd1'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2'
      AND r.target_model_code = 'MODEL-e5c45ff8ca074102829bba872e091bd1'
      AND r.field_code = 'MODEL_e5c45ff8ca074102829bba872e091bd1_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2',
  tm.id, 'MODEL-f5270d478dd849389e59a178cd512629',
  '所属设备管理', 'MODEL_f5270d478dd849389e59a178cd512629_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2'
  AND tm.code = 'MODEL-f5270d478dd849389e59a178cd512629'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2'
      AND r.target_model_code = 'MODEL-f5270d478dd849389e59a178cd512629'
      AND r.field_code = 'MODEL_f5270d478dd849389e59a178cd512629_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2',
  tm.id, 'MODEL-f55ce56d31f741caa5a69801556834fe',
  '所属设备管理', 'MODEL_f55ce56d31f741caa5a69801556834fe_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2'
  AND tm.code = 'MODEL-f55ce56d31f741caa5a69801556834fe'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2'
      AND r.target_model_code = 'MODEL-f55ce56d31f741caa5a69801556834fe'
      AND r.field_code = 'MODEL_f55ce56d31f741caa5a69801556834fe_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5bf9561d71624c43af36d4411520bb8d',
  tm.id, 'MODEL-05e5c6a6c8fe4f3c9bd1dd86dd24a659',
  '所属设备管理', 'MODEL_05e5c6a6c8fe4f3c9bd1dd86dd24a659_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5bf9561d71624c43af36d4411520bb8d'
  AND tm.code = 'MODEL-05e5c6a6c8fe4f3c9bd1dd86dd24a659'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5bf9561d71624c43af36d4411520bb8d'
      AND r.target_model_code = 'MODEL-05e5c6a6c8fe4f3c9bd1dd86dd24a659'
      AND r.field_code = 'MODEL_05e5c6a6c8fe4f3c9bd1dd86dd24a659_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5bf9561d71624c43af36d4411520bb8d',
  tm.id, 'MODEL-123328b7bfe745ceb337d286ac64aa17',
  '所属设备管理', 'MODEL_123328b7bfe745ceb337d286ac64aa17_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5bf9561d71624c43af36d4411520bb8d'
  AND tm.code = 'MODEL-123328b7bfe745ceb337d286ac64aa17'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5bf9561d71624c43af36d4411520bb8d'
      AND r.target_model_code = 'MODEL-123328b7bfe745ceb337d286ac64aa17'
      AND r.field_code = 'MODEL_123328b7bfe745ceb337d286ac64aa17_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5bf9561d71624c43af36d4411520bb8d',
  tm.id, 'MODEL-17eb9be1c7404734af570ce040f1783b',
  '所属设备管理', 'MODEL_17eb9be1c7404734af570ce040f1783b_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5bf9561d71624c43af36d4411520bb8d'
  AND tm.code = 'MODEL-17eb9be1c7404734af570ce040f1783b'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5bf9561d71624c43af36d4411520bb8d'
      AND r.target_model_code = 'MODEL-17eb9be1c7404734af570ce040f1783b'
      AND r.field_code = 'MODEL_17eb9be1c7404734af570ce040f1783b_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5bf9561d71624c43af36d4411520bb8d',
  tm.id, 'MODEL-26eabac35cb644cc84ae64dfb5ff9ba4',
  '所属设备管理', 'MODEL_26eabac35cb644cc84ae64dfb5ff9ba4_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5bf9561d71624c43af36d4411520bb8d'
  AND tm.code = 'MODEL-26eabac35cb644cc84ae64dfb5ff9ba4'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5bf9561d71624c43af36d4411520bb8d'
      AND r.target_model_code = 'MODEL-26eabac35cb644cc84ae64dfb5ff9ba4'
      AND r.field_code = 'MODEL_26eabac35cb644cc84ae64dfb5ff9ba4_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5bf9561d71624c43af36d4411520bb8d',
  tm.id, 'MODEL-2af0515a5a36420086980b2322037edb',
  '所属设备管理', 'MODEL_2af0515a5a36420086980b2322037edb_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5bf9561d71624c43af36d4411520bb8d'
  AND tm.code = 'MODEL-2af0515a5a36420086980b2322037edb'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5bf9561d71624c43af36d4411520bb8d'
      AND r.target_model_code = 'MODEL-2af0515a5a36420086980b2322037edb'
      AND r.field_code = 'MODEL_2af0515a5a36420086980b2322037edb_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5bf9561d71624c43af36d4411520bb8d',
  tm.id, 'MODEL-3130eba3747e4a5aa0862c94807822ba',
  '所属设备管理', 'MODEL_3130eba3747e4a5aa0862c94807822ba_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5bf9561d71624c43af36d4411520bb8d'
  AND tm.code = 'MODEL-3130eba3747e4a5aa0862c94807822ba'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5bf9561d71624c43af36d4411520bb8d'
      AND r.target_model_code = 'MODEL-3130eba3747e4a5aa0862c94807822ba'
      AND r.field_code = 'MODEL_3130eba3747e4a5aa0862c94807822ba_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5bf9561d71624c43af36d4411520bb8d',
  tm.id, 'MODEL-44cc7511fc224085a51e2887569c933a',
  '所属设备管理', 'MODEL_44cc7511fc224085a51e2887569c933a_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5bf9561d71624c43af36d4411520bb8d'
  AND tm.code = 'MODEL-44cc7511fc224085a51e2887569c933a'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5bf9561d71624c43af36d4411520bb8d'
      AND r.target_model_code = 'MODEL-44cc7511fc224085a51e2887569c933a'
      AND r.field_code = 'MODEL_44cc7511fc224085a51e2887569c933a_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5bf9561d71624c43af36d4411520bb8d',
  tm.id, 'MODEL-50991eab2a714ed288d00c58ff01c810',
  '所属设备管理', 'MODEL_50991eab2a714ed288d00c58ff01c810_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5bf9561d71624c43af36d4411520bb8d'
  AND tm.code = 'MODEL-50991eab2a714ed288d00c58ff01c810'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5bf9561d71624c43af36d4411520bb8d'
      AND r.target_model_code = 'MODEL-50991eab2a714ed288d00c58ff01c810'
      AND r.field_code = 'MODEL_50991eab2a714ed288d00c58ff01c810_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5bf9561d71624c43af36d4411520bb8d',
  tm.id, 'MODEL-5a9b87c42571486998d363101c54ec8c',
  '所属设备管理', 'MODEL_5a9b87c42571486998d363101c54ec8c_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5bf9561d71624c43af36d4411520bb8d'
  AND tm.code = 'MODEL-5a9b87c42571486998d363101c54ec8c'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5bf9561d71624c43af36d4411520bb8d'
      AND r.target_model_code = 'MODEL-5a9b87c42571486998d363101c54ec8c'
      AND r.field_code = 'MODEL_5a9b87c42571486998d363101c54ec8c_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5bf9561d71624c43af36d4411520bb8d',
  tm.id, 'MODEL-5f6f9243c7bb4e23bfed2fc01b48cf06',
  '所属设备管理', 'MODEL_5f6f9243c7bb4e23bfed2fc01b48cf06_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5bf9561d71624c43af36d4411520bb8d'
  AND tm.code = 'MODEL-5f6f9243c7bb4e23bfed2fc01b48cf06'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5bf9561d71624c43af36d4411520bb8d'
      AND r.target_model_code = 'MODEL-5f6f9243c7bb4e23bfed2fc01b48cf06'
      AND r.field_code = 'MODEL_5f6f9243c7bb4e23bfed2fc01b48cf06_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5bf9561d71624c43af36d4411520bb8d',
  tm.id, 'MODEL-68554821800248cfacacbac1a53cdf37',
  '所属设备管理', 'MODEL_68554821800248cfacacbac1a53cdf37_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5bf9561d71624c43af36d4411520bb8d'
  AND tm.code = 'MODEL-68554821800248cfacacbac1a53cdf37'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5bf9561d71624c43af36d4411520bb8d'
      AND r.target_model_code = 'MODEL-68554821800248cfacacbac1a53cdf37'
      AND r.field_code = 'MODEL_68554821800248cfacacbac1a53cdf37_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5bf9561d71624c43af36d4411520bb8d',
  tm.id, 'MODEL-6fd43ab74f6f425f81f0ad547603a849',
  '所属设备管理', 'MODEL_6fd43ab74f6f425f81f0ad547603a849_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5bf9561d71624c43af36d4411520bb8d'
  AND tm.code = 'MODEL-6fd43ab74f6f425f81f0ad547603a849'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5bf9561d71624c43af36d4411520bb8d'
      AND r.target_model_code = 'MODEL-6fd43ab74f6f425f81f0ad547603a849'
      AND r.field_code = 'MODEL_6fd43ab74f6f425f81f0ad547603a849_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5bf9561d71624c43af36d4411520bb8d',
  tm.id, 'MODEL-70d2b066120c4b969f0b661d1d2fb664',
  '所属设备管理', 'MODEL_70d2b066120c4b969f0b661d1d2fb664_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5bf9561d71624c43af36d4411520bb8d'
  AND tm.code = 'MODEL-70d2b066120c4b969f0b661d1d2fb664'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5bf9561d71624c43af36d4411520bb8d'
      AND r.target_model_code = 'MODEL-70d2b066120c4b969f0b661d1d2fb664'
      AND r.field_code = 'MODEL_70d2b066120c4b969f0b661d1d2fb664_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5bf9561d71624c43af36d4411520bb8d',
  tm.id, 'MODEL-7c4fe9b2d52d41cca5383488a4a400a5',
  '所属设备管理', 'MODEL_7c4fe9b2d52d41cca5383488a4a400a5_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5bf9561d71624c43af36d4411520bb8d'
  AND tm.code = 'MODEL-7c4fe9b2d52d41cca5383488a4a400a5'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5bf9561d71624c43af36d4411520bb8d'
      AND r.target_model_code = 'MODEL-7c4fe9b2d52d41cca5383488a4a400a5'
      AND r.field_code = 'MODEL_7c4fe9b2d52d41cca5383488a4a400a5_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5bf9561d71624c43af36d4411520bb8d',
  tm.id, 'MODEL-7c746a60520347baaf64334625f07f31',
  '所属设备管理', 'MODEL_7c746a60520347baaf64334625f07f31_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5bf9561d71624c43af36d4411520bb8d'
  AND tm.code = 'MODEL-7c746a60520347baaf64334625f07f31'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5bf9561d71624c43af36d4411520bb8d'
      AND r.target_model_code = 'MODEL-7c746a60520347baaf64334625f07f31'
      AND r.field_code = 'MODEL_7c746a60520347baaf64334625f07f31_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5bf9561d71624c43af36d4411520bb8d',
  tm.id, 'MODEL-81132ce00d2041c6a4ee06ec1c01dac8',
  '所属设备管理', 'MODEL_81132ce00d2041c6a4ee06ec1c01dac8_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5bf9561d71624c43af36d4411520bb8d'
  AND tm.code = 'MODEL-81132ce00d2041c6a4ee06ec1c01dac8'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5bf9561d71624c43af36d4411520bb8d'
      AND r.target_model_code = 'MODEL-81132ce00d2041c6a4ee06ec1c01dac8'
      AND r.field_code = 'MODEL_81132ce00d2041c6a4ee06ec1c01dac8_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5bf9561d71624c43af36d4411520bb8d',
  tm.id, 'MODEL-88a38000121341e2950132da32baad90',
  '所属设备管理', 'MODEL_88a38000121341e2950132da32baad90_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5bf9561d71624c43af36d4411520bb8d'
  AND tm.code = 'MODEL-88a38000121341e2950132da32baad90'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5bf9561d71624c43af36d4411520bb8d'
      AND r.target_model_code = 'MODEL-88a38000121341e2950132da32baad90'
      AND r.field_code = 'MODEL_88a38000121341e2950132da32baad90_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5bf9561d71624c43af36d4411520bb8d',
  tm.id, 'MODEL-990370e140aa481a82158bc96dc0b006',
  '所属设备管理', 'MODEL_990370e140aa481a82158bc96dc0b006_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5bf9561d71624c43af36d4411520bb8d'
  AND tm.code = 'MODEL-990370e140aa481a82158bc96dc0b006'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5bf9561d71624c43af36d4411520bb8d'
      AND r.target_model_code = 'MODEL-990370e140aa481a82158bc96dc0b006'
      AND r.field_code = 'MODEL_990370e140aa481a82158bc96dc0b006_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5bf9561d71624c43af36d4411520bb8d',
  tm.id, 'MODEL-a4ce7668efe94d2fbc47a658be1a35d2',
  '所属设备管理', 'MODEL_a4ce7668efe94d2fbc47a658be1a35d2_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5bf9561d71624c43af36d4411520bb8d'
  AND tm.code = 'MODEL-a4ce7668efe94d2fbc47a658be1a35d2'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5bf9561d71624c43af36d4411520bb8d'
      AND r.target_model_code = 'MODEL-a4ce7668efe94d2fbc47a658be1a35d2'
      AND r.field_code = 'MODEL_a4ce7668efe94d2fbc47a658be1a35d2_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5bf9561d71624c43af36d4411520bb8d',
  tm.id, 'MODEL-a62c727c0a024e19a8221c68fcf0c09a',
  '所属设备管理', 'MODEL_a62c727c0a024e19a8221c68fcf0c09a_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5bf9561d71624c43af36d4411520bb8d'
  AND tm.code = 'MODEL-a62c727c0a024e19a8221c68fcf0c09a'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5bf9561d71624c43af36d4411520bb8d'
      AND r.target_model_code = 'MODEL-a62c727c0a024e19a8221c68fcf0c09a'
      AND r.field_code = 'MODEL_a62c727c0a024e19a8221c68fcf0c09a_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5bf9561d71624c43af36d4411520bb8d',
  tm.id, 'MODEL-b8251da851514341adef7f6f72995cf3',
  '所属设备管理', 'MODEL_b8251da851514341adef7f6f72995cf3_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5bf9561d71624c43af36d4411520bb8d'
  AND tm.code = 'MODEL-b8251da851514341adef7f6f72995cf3'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5bf9561d71624c43af36d4411520bb8d'
      AND r.target_model_code = 'MODEL-b8251da851514341adef7f6f72995cf3'
      AND r.field_code = 'MODEL_b8251da851514341adef7f6f72995cf3_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5bf9561d71624c43af36d4411520bb8d',
  tm.id, 'MODEL-b83f05dc94f34fb4807458c0a48ba488',
  '所属设备管理', 'MODEL_b83f05dc94f34fb4807458c0a48ba488_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5bf9561d71624c43af36d4411520bb8d'
  AND tm.code = 'MODEL-b83f05dc94f34fb4807458c0a48ba488'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5bf9561d71624c43af36d4411520bb8d'
      AND r.target_model_code = 'MODEL-b83f05dc94f34fb4807458c0a48ba488'
      AND r.field_code = 'MODEL_b83f05dc94f34fb4807458c0a48ba488_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5bf9561d71624c43af36d4411520bb8d',
  tm.id, 'MODEL-bc2cdffeab434f4d862c847fa99938ba',
  '所属设备管理', 'MODEL_bc2cdffeab434f4d862c847fa99938ba_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5bf9561d71624c43af36d4411520bb8d'
  AND tm.code = 'MODEL-bc2cdffeab434f4d862c847fa99938ba'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5bf9561d71624c43af36d4411520bb8d'
      AND r.target_model_code = 'MODEL-bc2cdffeab434f4d862c847fa99938ba'
      AND r.field_code = 'MODEL_bc2cdffeab434f4d862c847fa99938ba_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5bf9561d71624c43af36d4411520bb8d',
  tm.id, 'MODEL-bf41bd50df044f5faf43c4759353ccd1',
  '所属设备管理', 'MODEL_bf41bd50df044f5faf43c4759353ccd1_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5bf9561d71624c43af36d4411520bb8d'
  AND tm.code = 'MODEL-bf41bd50df044f5faf43c4759353ccd1'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5bf9561d71624c43af36d4411520bb8d'
      AND r.target_model_code = 'MODEL-bf41bd50df044f5faf43c4759353ccd1'
      AND r.field_code = 'MODEL_bf41bd50df044f5faf43c4759353ccd1_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5bf9561d71624c43af36d4411520bb8d',
  tm.id, 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9',
  '所属设备管理', 'MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5bf9561d71624c43af36d4411520bb8d'
  AND tm.code = 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5bf9561d71624c43af36d4411520bb8d'
      AND r.target_model_code = 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9'
      AND r.field_code = 'MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5bf9561d71624c43af36d4411520bb8d',
  tm.id, 'MODEL-c6bc32646f3e4bde8409120c5c290eee',
  '所属设备管理', 'MODEL_c6bc32646f3e4bde8409120c5c290eee_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5bf9561d71624c43af36d4411520bb8d'
  AND tm.code = 'MODEL-c6bc32646f3e4bde8409120c5c290eee'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5bf9561d71624c43af36d4411520bb8d'
      AND r.target_model_code = 'MODEL-c6bc32646f3e4bde8409120c5c290eee'
      AND r.field_code = 'MODEL_c6bc32646f3e4bde8409120c5c290eee_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5bf9561d71624c43af36d4411520bb8d',
  tm.id, 'MODEL-d446eef7329f45b4a374347e215fec5d',
  '所属设备管理', 'MODEL_d446eef7329f45b4a374347e215fec5d_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5bf9561d71624c43af36d4411520bb8d'
  AND tm.code = 'MODEL-d446eef7329f45b4a374347e215fec5d'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5bf9561d71624c43af36d4411520bb8d'
      AND r.target_model_code = 'MODEL-d446eef7329f45b4a374347e215fec5d'
      AND r.field_code = 'MODEL_d446eef7329f45b4a374347e215fec5d_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5bf9561d71624c43af36d4411520bb8d',
  tm.id, 'MODEL-e25f3089a99d4878a1ddd326ba0910bf',
  '所属设备管理', 'MODEL_e25f3089a99d4878a1ddd326ba0910bf_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5bf9561d71624c43af36d4411520bb8d'
  AND tm.code = 'MODEL-e25f3089a99d4878a1ddd326ba0910bf'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5bf9561d71624c43af36d4411520bb8d'
      AND r.target_model_code = 'MODEL-e25f3089a99d4878a1ddd326ba0910bf'
      AND r.field_code = 'MODEL_e25f3089a99d4878a1ddd326ba0910bf_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5bf9561d71624c43af36d4411520bb8d',
  tm.id, 'MODEL-e5af81c45bbc44d48374d8036032cc5d',
  '所属设备管理', 'MODEL_e5af81c45bbc44d48374d8036032cc5d_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5bf9561d71624c43af36d4411520bb8d'
  AND tm.code = 'MODEL-e5af81c45bbc44d48374d8036032cc5d'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5bf9561d71624c43af36d4411520bb8d'
      AND r.target_model_code = 'MODEL-e5af81c45bbc44d48374d8036032cc5d'
      AND r.field_code = 'MODEL_e5af81c45bbc44d48374d8036032cc5d_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5bf9561d71624c43af36d4411520bb8d',
  tm.id, 'MODEL-e5c45ff8ca074102829bba872e091bd1',
  '所属设备管理', 'MODEL_e5c45ff8ca074102829bba872e091bd1_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5bf9561d71624c43af36d4411520bb8d'
  AND tm.code = 'MODEL-e5c45ff8ca074102829bba872e091bd1'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5bf9561d71624c43af36d4411520bb8d'
      AND r.target_model_code = 'MODEL-e5c45ff8ca074102829bba872e091bd1'
      AND r.field_code = 'MODEL_e5c45ff8ca074102829bba872e091bd1_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5bf9561d71624c43af36d4411520bb8d',
  tm.id, 'MODEL-f5270d478dd849389e59a178cd512629',
  '所属设备管理', 'MODEL_f5270d478dd849389e59a178cd512629_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5bf9561d71624c43af36d4411520bb8d'
  AND tm.code = 'MODEL-f5270d478dd849389e59a178cd512629'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5bf9561d71624c43af36d4411520bb8d'
      AND r.target_model_code = 'MODEL-f5270d478dd849389e59a178cd512629'
      AND r.field_code = 'MODEL_f5270d478dd849389e59a178cd512629_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5bf9561d71624c43af36d4411520bb8d',
  tm.id, 'MODEL-f55ce56d31f741caa5a69801556834fe',
  '所属设备管理', 'MODEL_f55ce56d31f741caa5a69801556834fe_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5bf9561d71624c43af36d4411520bb8d'
  AND tm.code = 'MODEL-f55ce56d31f741caa5a69801556834fe'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5bf9561d71624c43af36d4411520bb8d'
      AND r.target_model_code = 'MODEL-f55ce56d31f741caa5a69801556834fe'
      AND r.field_code = 'MODEL_f55ce56d31f741caa5a69801556834fe_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5de5a74896754c4e93786884405b9d89',
  tm.id, 'MODEL-05e5c6a6c8fe4f3c9bd1dd86dd24a659',
  '所属设备管理', 'MODEL_05e5c6a6c8fe4f3c9bd1dd86dd24a659_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5de5a74896754c4e93786884405b9d89'
  AND tm.code = 'MODEL-05e5c6a6c8fe4f3c9bd1dd86dd24a659'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5de5a74896754c4e93786884405b9d89'
      AND r.target_model_code = 'MODEL-05e5c6a6c8fe4f3c9bd1dd86dd24a659'
      AND r.field_code = 'MODEL_05e5c6a6c8fe4f3c9bd1dd86dd24a659_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5de5a74896754c4e93786884405b9d89',
  tm.id, 'MODEL-123328b7bfe745ceb337d286ac64aa17',
  '所属设备管理', 'MODEL_123328b7bfe745ceb337d286ac64aa17_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5de5a74896754c4e93786884405b9d89'
  AND tm.code = 'MODEL-123328b7bfe745ceb337d286ac64aa17'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5de5a74896754c4e93786884405b9d89'
      AND r.target_model_code = 'MODEL-123328b7bfe745ceb337d286ac64aa17'
      AND r.field_code = 'MODEL_123328b7bfe745ceb337d286ac64aa17_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5de5a74896754c4e93786884405b9d89',
  tm.id, 'MODEL-17eb9be1c7404734af570ce040f1783b',
  '所属设备管理', 'MODEL_17eb9be1c7404734af570ce040f1783b_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5de5a74896754c4e93786884405b9d89'
  AND tm.code = 'MODEL-17eb9be1c7404734af570ce040f1783b'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5de5a74896754c4e93786884405b9d89'
      AND r.target_model_code = 'MODEL-17eb9be1c7404734af570ce040f1783b'
      AND r.field_code = 'MODEL_17eb9be1c7404734af570ce040f1783b_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5de5a74896754c4e93786884405b9d89',
  tm.id, 'MODEL-26eabac35cb644cc84ae64dfb5ff9ba4',
  '所属设备管理', 'MODEL_26eabac35cb644cc84ae64dfb5ff9ba4_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5de5a74896754c4e93786884405b9d89'
  AND tm.code = 'MODEL-26eabac35cb644cc84ae64dfb5ff9ba4'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5de5a74896754c4e93786884405b9d89'
      AND r.target_model_code = 'MODEL-26eabac35cb644cc84ae64dfb5ff9ba4'
      AND r.field_code = 'MODEL_26eabac35cb644cc84ae64dfb5ff9ba4_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5de5a74896754c4e93786884405b9d89',
  tm.id, 'MODEL-2af0515a5a36420086980b2322037edb',
  '所属设备管理', 'MODEL_2af0515a5a36420086980b2322037edb_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5de5a74896754c4e93786884405b9d89'
  AND tm.code = 'MODEL-2af0515a5a36420086980b2322037edb'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5de5a74896754c4e93786884405b9d89'
      AND r.target_model_code = 'MODEL-2af0515a5a36420086980b2322037edb'
      AND r.field_code = 'MODEL_2af0515a5a36420086980b2322037edb_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5de5a74896754c4e93786884405b9d89',
  tm.id, 'MODEL-3130eba3747e4a5aa0862c94807822ba',
  '所属设备管理', 'MODEL_3130eba3747e4a5aa0862c94807822ba_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5de5a74896754c4e93786884405b9d89'
  AND tm.code = 'MODEL-3130eba3747e4a5aa0862c94807822ba'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5de5a74896754c4e93786884405b9d89'
      AND r.target_model_code = 'MODEL-3130eba3747e4a5aa0862c94807822ba'
      AND r.field_code = 'MODEL_3130eba3747e4a5aa0862c94807822ba_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5de5a74896754c4e93786884405b9d89',
  tm.id, 'MODEL-44cc7511fc224085a51e2887569c933a',
  '所属设备管理', 'MODEL_44cc7511fc224085a51e2887569c933a_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5de5a74896754c4e93786884405b9d89'
  AND tm.code = 'MODEL-44cc7511fc224085a51e2887569c933a'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5de5a74896754c4e93786884405b9d89'
      AND r.target_model_code = 'MODEL-44cc7511fc224085a51e2887569c933a'
      AND r.field_code = 'MODEL_44cc7511fc224085a51e2887569c933a_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5de5a74896754c4e93786884405b9d89',
  tm.id, 'MODEL-50991eab2a714ed288d00c58ff01c810',
  '所属设备管理', 'MODEL_50991eab2a714ed288d00c58ff01c810_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5de5a74896754c4e93786884405b9d89'
  AND tm.code = 'MODEL-50991eab2a714ed288d00c58ff01c810'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5de5a74896754c4e93786884405b9d89'
      AND r.target_model_code = 'MODEL-50991eab2a714ed288d00c58ff01c810'
      AND r.field_code = 'MODEL_50991eab2a714ed288d00c58ff01c810_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5de5a74896754c4e93786884405b9d89',
  tm.id, 'MODEL-5a9b87c42571486998d363101c54ec8c',
  '所属设备管理', 'MODEL_5a9b87c42571486998d363101c54ec8c_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5de5a74896754c4e93786884405b9d89'
  AND tm.code = 'MODEL-5a9b87c42571486998d363101c54ec8c'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5de5a74896754c4e93786884405b9d89'
      AND r.target_model_code = 'MODEL-5a9b87c42571486998d363101c54ec8c'
      AND r.field_code = 'MODEL_5a9b87c42571486998d363101c54ec8c_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5de5a74896754c4e93786884405b9d89',
  tm.id, 'MODEL-5f6f9243c7bb4e23bfed2fc01b48cf06',
  '所属设备管理', 'MODEL_5f6f9243c7bb4e23bfed2fc01b48cf06_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5de5a74896754c4e93786884405b9d89'
  AND tm.code = 'MODEL-5f6f9243c7bb4e23bfed2fc01b48cf06'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5de5a74896754c4e93786884405b9d89'
      AND r.target_model_code = 'MODEL-5f6f9243c7bb4e23bfed2fc01b48cf06'
      AND r.field_code = 'MODEL_5f6f9243c7bb4e23bfed2fc01b48cf06_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5de5a74896754c4e93786884405b9d89',
  tm.id, 'MODEL-68554821800248cfacacbac1a53cdf37',
  '所属设备管理', 'MODEL_68554821800248cfacacbac1a53cdf37_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5de5a74896754c4e93786884405b9d89'
  AND tm.code = 'MODEL-68554821800248cfacacbac1a53cdf37'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5de5a74896754c4e93786884405b9d89'
      AND r.target_model_code = 'MODEL-68554821800248cfacacbac1a53cdf37'
      AND r.field_code = 'MODEL_68554821800248cfacacbac1a53cdf37_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5de5a74896754c4e93786884405b9d89',
  tm.id, 'MODEL-6fd43ab74f6f425f81f0ad547603a849',
  '所属设备管理', 'MODEL_6fd43ab74f6f425f81f0ad547603a849_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5de5a74896754c4e93786884405b9d89'
  AND tm.code = 'MODEL-6fd43ab74f6f425f81f0ad547603a849'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5de5a74896754c4e93786884405b9d89'
      AND r.target_model_code = 'MODEL-6fd43ab74f6f425f81f0ad547603a849'
      AND r.field_code = 'MODEL_6fd43ab74f6f425f81f0ad547603a849_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5de5a74896754c4e93786884405b9d89',
  tm.id, 'MODEL-70d2b066120c4b969f0b661d1d2fb664',
  '所属设备管理', 'MODEL_70d2b066120c4b969f0b661d1d2fb664_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5de5a74896754c4e93786884405b9d89'
  AND tm.code = 'MODEL-70d2b066120c4b969f0b661d1d2fb664'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5de5a74896754c4e93786884405b9d89'
      AND r.target_model_code = 'MODEL-70d2b066120c4b969f0b661d1d2fb664'
      AND r.field_code = 'MODEL_70d2b066120c4b969f0b661d1d2fb664_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5de5a74896754c4e93786884405b9d89',
  tm.id, 'MODEL-7c4fe9b2d52d41cca5383488a4a400a5',
  '所属设备管理', 'MODEL_7c4fe9b2d52d41cca5383488a4a400a5_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5de5a74896754c4e93786884405b9d89'
  AND tm.code = 'MODEL-7c4fe9b2d52d41cca5383488a4a400a5'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5de5a74896754c4e93786884405b9d89'
      AND r.target_model_code = 'MODEL-7c4fe9b2d52d41cca5383488a4a400a5'
      AND r.field_code = 'MODEL_7c4fe9b2d52d41cca5383488a4a400a5_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5de5a74896754c4e93786884405b9d89',
  tm.id, 'MODEL-7c746a60520347baaf64334625f07f31',
  '所属设备管理', 'MODEL_7c746a60520347baaf64334625f07f31_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5de5a74896754c4e93786884405b9d89'
  AND tm.code = 'MODEL-7c746a60520347baaf64334625f07f31'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5de5a74896754c4e93786884405b9d89'
      AND r.target_model_code = 'MODEL-7c746a60520347baaf64334625f07f31'
      AND r.field_code = 'MODEL_7c746a60520347baaf64334625f07f31_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5de5a74896754c4e93786884405b9d89',
  tm.id, 'MODEL-81132ce00d2041c6a4ee06ec1c01dac8',
  '所属设备管理', 'MODEL_81132ce00d2041c6a4ee06ec1c01dac8_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5de5a74896754c4e93786884405b9d89'
  AND tm.code = 'MODEL-81132ce00d2041c6a4ee06ec1c01dac8'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5de5a74896754c4e93786884405b9d89'
      AND r.target_model_code = 'MODEL-81132ce00d2041c6a4ee06ec1c01dac8'
      AND r.field_code = 'MODEL_81132ce00d2041c6a4ee06ec1c01dac8_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5de5a74896754c4e93786884405b9d89',
  tm.id, 'MODEL-88a38000121341e2950132da32baad90',
  '所属设备管理', 'MODEL_88a38000121341e2950132da32baad90_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5de5a74896754c4e93786884405b9d89'
  AND tm.code = 'MODEL-88a38000121341e2950132da32baad90'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5de5a74896754c4e93786884405b9d89'
      AND r.target_model_code = 'MODEL-88a38000121341e2950132da32baad90'
      AND r.field_code = 'MODEL_88a38000121341e2950132da32baad90_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5de5a74896754c4e93786884405b9d89',
  tm.id, 'MODEL-990370e140aa481a82158bc96dc0b006',
  '所属设备管理', 'MODEL_990370e140aa481a82158bc96dc0b006_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5de5a74896754c4e93786884405b9d89'
  AND tm.code = 'MODEL-990370e140aa481a82158bc96dc0b006'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5de5a74896754c4e93786884405b9d89'
      AND r.target_model_code = 'MODEL-990370e140aa481a82158bc96dc0b006'
      AND r.field_code = 'MODEL_990370e140aa481a82158bc96dc0b006_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5de5a74896754c4e93786884405b9d89',
  tm.id, 'MODEL-a4ce7668efe94d2fbc47a658be1a35d2',
  '所属设备管理', 'MODEL_a4ce7668efe94d2fbc47a658be1a35d2_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5de5a74896754c4e93786884405b9d89'
  AND tm.code = 'MODEL-a4ce7668efe94d2fbc47a658be1a35d2'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5de5a74896754c4e93786884405b9d89'
      AND r.target_model_code = 'MODEL-a4ce7668efe94d2fbc47a658be1a35d2'
      AND r.field_code = 'MODEL_a4ce7668efe94d2fbc47a658be1a35d2_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5de5a74896754c4e93786884405b9d89',
  tm.id, 'MODEL-a62c727c0a024e19a8221c68fcf0c09a',
  '所属设备管理', 'MODEL_a62c727c0a024e19a8221c68fcf0c09a_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5de5a74896754c4e93786884405b9d89'
  AND tm.code = 'MODEL-a62c727c0a024e19a8221c68fcf0c09a'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5de5a74896754c4e93786884405b9d89'
      AND r.target_model_code = 'MODEL-a62c727c0a024e19a8221c68fcf0c09a'
      AND r.field_code = 'MODEL_a62c727c0a024e19a8221c68fcf0c09a_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5de5a74896754c4e93786884405b9d89',
  tm.id, 'MODEL-b8251da851514341adef7f6f72995cf3',
  '所属设备管理', 'MODEL_b8251da851514341adef7f6f72995cf3_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5de5a74896754c4e93786884405b9d89'
  AND tm.code = 'MODEL-b8251da851514341adef7f6f72995cf3'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5de5a74896754c4e93786884405b9d89'
      AND r.target_model_code = 'MODEL-b8251da851514341adef7f6f72995cf3'
      AND r.field_code = 'MODEL_b8251da851514341adef7f6f72995cf3_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5de5a74896754c4e93786884405b9d89',
  tm.id, 'MODEL-b83f05dc94f34fb4807458c0a48ba488',
  '所属设备管理', 'MODEL_b83f05dc94f34fb4807458c0a48ba488_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5de5a74896754c4e93786884405b9d89'
  AND tm.code = 'MODEL-b83f05dc94f34fb4807458c0a48ba488'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5de5a74896754c4e93786884405b9d89'
      AND r.target_model_code = 'MODEL-b83f05dc94f34fb4807458c0a48ba488'
      AND r.field_code = 'MODEL_b83f05dc94f34fb4807458c0a48ba488_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5de5a74896754c4e93786884405b9d89',
  tm.id, 'MODEL-bc2cdffeab434f4d862c847fa99938ba',
  '所属设备管理', 'MODEL_bc2cdffeab434f4d862c847fa99938ba_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5de5a74896754c4e93786884405b9d89'
  AND tm.code = 'MODEL-bc2cdffeab434f4d862c847fa99938ba'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5de5a74896754c4e93786884405b9d89'
      AND r.target_model_code = 'MODEL-bc2cdffeab434f4d862c847fa99938ba'
      AND r.field_code = 'MODEL_bc2cdffeab434f4d862c847fa99938ba_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5de5a74896754c4e93786884405b9d89',
  tm.id, 'MODEL-bf41bd50df044f5faf43c4759353ccd1',
  '所属设备管理', 'MODEL_bf41bd50df044f5faf43c4759353ccd1_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5de5a74896754c4e93786884405b9d89'
  AND tm.code = 'MODEL-bf41bd50df044f5faf43c4759353ccd1'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5de5a74896754c4e93786884405b9d89'
      AND r.target_model_code = 'MODEL-bf41bd50df044f5faf43c4759353ccd1'
      AND r.field_code = 'MODEL_bf41bd50df044f5faf43c4759353ccd1_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5de5a74896754c4e93786884405b9d89',
  tm.id, 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9',
  '所属设备管理', 'MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5de5a74896754c4e93786884405b9d89'
  AND tm.code = 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5de5a74896754c4e93786884405b9d89'
      AND r.target_model_code = 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9'
      AND r.field_code = 'MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5de5a74896754c4e93786884405b9d89',
  tm.id, 'MODEL-c6bc32646f3e4bde8409120c5c290eee',
  '所属设备管理', 'MODEL_c6bc32646f3e4bde8409120c5c290eee_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5de5a74896754c4e93786884405b9d89'
  AND tm.code = 'MODEL-c6bc32646f3e4bde8409120c5c290eee'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5de5a74896754c4e93786884405b9d89'
      AND r.target_model_code = 'MODEL-c6bc32646f3e4bde8409120c5c290eee'
      AND r.field_code = 'MODEL_c6bc32646f3e4bde8409120c5c290eee_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5de5a74896754c4e93786884405b9d89',
  tm.id, 'MODEL-d446eef7329f45b4a374347e215fec5d',
  '所属设备管理', 'MODEL_d446eef7329f45b4a374347e215fec5d_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5de5a74896754c4e93786884405b9d89'
  AND tm.code = 'MODEL-d446eef7329f45b4a374347e215fec5d'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5de5a74896754c4e93786884405b9d89'
      AND r.target_model_code = 'MODEL-d446eef7329f45b4a374347e215fec5d'
      AND r.field_code = 'MODEL_d446eef7329f45b4a374347e215fec5d_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5de5a74896754c4e93786884405b9d89',
  tm.id, 'MODEL-e25f3089a99d4878a1ddd326ba0910bf',
  '所属设备管理', 'MODEL_e25f3089a99d4878a1ddd326ba0910bf_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5de5a74896754c4e93786884405b9d89'
  AND tm.code = 'MODEL-e25f3089a99d4878a1ddd326ba0910bf'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5de5a74896754c4e93786884405b9d89'
      AND r.target_model_code = 'MODEL-e25f3089a99d4878a1ddd326ba0910bf'
      AND r.field_code = 'MODEL_e25f3089a99d4878a1ddd326ba0910bf_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5de5a74896754c4e93786884405b9d89',
  tm.id, 'MODEL-e5af81c45bbc44d48374d8036032cc5d',
  '所属设备管理', 'MODEL_e5af81c45bbc44d48374d8036032cc5d_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5de5a74896754c4e93786884405b9d89'
  AND tm.code = 'MODEL-e5af81c45bbc44d48374d8036032cc5d'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5de5a74896754c4e93786884405b9d89'
      AND r.target_model_code = 'MODEL-e5af81c45bbc44d48374d8036032cc5d'
      AND r.field_code = 'MODEL_e5af81c45bbc44d48374d8036032cc5d_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5de5a74896754c4e93786884405b9d89',
  tm.id, 'MODEL-e5c45ff8ca074102829bba872e091bd1',
  '所属设备管理', 'MODEL_e5c45ff8ca074102829bba872e091bd1_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5de5a74896754c4e93786884405b9d89'
  AND tm.code = 'MODEL-e5c45ff8ca074102829bba872e091bd1'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5de5a74896754c4e93786884405b9d89'
      AND r.target_model_code = 'MODEL-e5c45ff8ca074102829bba872e091bd1'
      AND r.field_code = 'MODEL_e5c45ff8ca074102829bba872e091bd1_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5de5a74896754c4e93786884405b9d89',
  tm.id, 'MODEL-f5270d478dd849389e59a178cd512629',
  '所属设备管理', 'MODEL_f5270d478dd849389e59a178cd512629_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5de5a74896754c4e93786884405b9d89'
  AND tm.code = 'MODEL-f5270d478dd849389e59a178cd512629'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5de5a74896754c4e93786884405b9d89'
      AND r.target_model_code = 'MODEL-f5270d478dd849389e59a178cd512629'
      AND r.field_code = 'MODEL_f5270d478dd849389e59a178cd512629_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5de5a74896754c4e93786884405b9d89',
  tm.id, 'MODEL-f55ce56d31f741caa5a69801556834fe',
  '所属设备管理', 'MODEL_f55ce56d31f741caa5a69801556834fe_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5de5a74896754c4e93786884405b9d89'
  AND tm.code = 'MODEL-f55ce56d31f741caa5a69801556834fe'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5de5a74896754c4e93786884405b9d89'
      AND r.target_model_code = 'MODEL-f55ce56d31f741caa5a69801556834fe'
      AND r.field_code = 'MODEL_f55ce56d31f741caa5a69801556834fe_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad',
  tm.id, 'MODEL-05e5c6a6c8fe4f3c9bd1dd86dd24a659',
  '所属设备管理', 'MODEL_05e5c6a6c8fe4f3c9bd1dd86dd24a659_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad'
  AND tm.code = 'MODEL-05e5c6a6c8fe4f3c9bd1dd86dd24a659'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad'
      AND r.target_model_code = 'MODEL-05e5c6a6c8fe4f3c9bd1dd86dd24a659'
      AND r.field_code = 'MODEL_05e5c6a6c8fe4f3c9bd1dd86dd24a659_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad',
  tm.id, 'MODEL-123328b7bfe745ceb337d286ac64aa17',
  '所属设备管理', 'MODEL_123328b7bfe745ceb337d286ac64aa17_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad'
  AND tm.code = 'MODEL-123328b7bfe745ceb337d286ac64aa17'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad'
      AND r.target_model_code = 'MODEL-123328b7bfe745ceb337d286ac64aa17'
      AND r.field_code = 'MODEL_123328b7bfe745ceb337d286ac64aa17_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad',
  tm.id, 'MODEL-17eb9be1c7404734af570ce040f1783b',
  '所属设备管理', 'MODEL_17eb9be1c7404734af570ce040f1783b_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad'
  AND tm.code = 'MODEL-17eb9be1c7404734af570ce040f1783b'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad'
      AND r.target_model_code = 'MODEL-17eb9be1c7404734af570ce040f1783b'
      AND r.field_code = 'MODEL_17eb9be1c7404734af570ce040f1783b_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad',
  tm.id, 'MODEL-26eabac35cb644cc84ae64dfb5ff9ba4',
  '所属设备管理', 'MODEL_26eabac35cb644cc84ae64dfb5ff9ba4_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad'
  AND tm.code = 'MODEL-26eabac35cb644cc84ae64dfb5ff9ba4'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad'
      AND r.target_model_code = 'MODEL-26eabac35cb644cc84ae64dfb5ff9ba4'
      AND r.field_code = 'MODEL_26eabac35cb644cc84ae64dfb5ff9ba4_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad',
  tm.id, 'MODEL-2af0515a5a36420086980b2322037edb',
  '所属设备管理', 'MODEL_2af0515a5a36420086980b2322037edb_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad'
  AND tm.code = 'MODEL-2af0515a5a36420086980b2322037edb'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad'
      AND r.target_model_code = 'MODEL-2af0515a5a36420086980b2322037edb'
      AND r.field_code = 'MODEL_2af0515a5a36420086980b2322037edb_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad',
  tm.id, 'MODEL-3130eba3747e4a5aa0862c94807822ba',
  '所属设备管理', 'MODEL_3130eba3747e4a5aa0862c94807822ba_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad'
  AND tm.code = 'MODEL-3130eba3747e4a5aa0862c94807822ba'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad'
      AND r.target_model_code = 'MODEL-3130eba3747e4a5aa0862c94807822ba'
      AND r.field_code = 'MODEL_3130eba3747e4a5aa0862c94807822ba_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad',
  tm.id, 'MODEL-44cc7511fc224085a51e2887569c933a',
  '所属设备管理', 'MODEL_44cc7511fc224085a51e2887569c933a_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad'
  AND tm.code = 'MODEL-44cc7511fc224085a51e2887569c933a'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad'
      AND r.target_model_code = 'MODEL-44cc7511fc224085a51e2887569c933a'
      AND r.field_code = 'MODEL_44cc7511fc224085a51e2887569c933a_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad',
  tm.id, 'MODEL-50991eab2a714ed288d00c58ff01c810',
  '所属设备管理', 'MODEL_50991eab2a714ed288d00c58ff01c810_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad'
  AND tm.code = 'MODEL-50991eab2a714ed288d00c58ff01c810'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad'
      AND r.target_model_code = 'MODEL-50991eab2a714ed288d00c58ff01c810'
      AND r.field_code = 'MODEL_50991eab2a714ed288d00c58ff01c810_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad',
  tm.id, 'MODEL-5a9b87c42571486998d363101c54ec8c',
  '所属设备管理', 'MODEL_5a9b87c42571486998d363101c54ec8c_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad'
  AND tm.code = 'MODEL-5a9b87c42571486998d363101c54ec8c'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad'
      AND r.target_model_code = 'MODEL-5a9b87c42571486998d363101c54ec8c'
      AND r.field_code = 'MODEL_5a9b87c42571486998d363101c54ec8c_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad',
  tm.id, 'MODEL-5f6f9243c7bb4e23bfed2fc01b48cf06',
  '所属设备管理', 'MODEL_5f6f9243c7bb4e23bfed2fc01b48cf06_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad'
  AND tm.code = 'MODEL-5f6f9243c7bb4e23bfed2fc01b48cf06'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad'
      AND r.target_model_code = 'MODEL-5f6f9243c7bb4e23bfed2fc01b48cf06'
      AND r.field_code = 'MODEL_5f6f9243c7bb4e23bfed2fc01b48cf06_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad',
  tm.id, 'MODEL-68554821800248cfacacbac1a53cdf37',
  '所属设备管理', 'MODEL_68554821800248cfacacbac1a53cdf37_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad'
  AND tm.code = 'MODEL-68554821800248cfacacbac1a53cdf37'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad'
      AND r.target_model_code = 'MODEL-68554821800248cfacacbac1a53cdf37'
      AND r.field_code = 'MODEL_68554821800248cfacacbac1a53cdf37_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad',
  tm.id, 'MODEL-6fd43ab74f6f425f81f0ad547603a849',
  '所属设备管理', 'MODEL_6fd43ab74f6f425f81f0ad547603a849_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad'
  AND tm.code = 'MODEL-6fd43ab74f6f425f81f0ad547603a849'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad'
      AND r.target_model_code = 'MODEL-6fd43ab74f6f425f81f0ad547603a849'
      AND r.field_code = 'MODEL_6fd43ab74f6f425f81f0ad547603a849_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad',
  tm.id, 'MODEL-70d2b066120c4b969f0b661d1d2fb664',
  '所属设备管理', 'MODEL_70d2b066120c4b969f0b661d1d2fb664_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad'
  AND tm.code = 'MODEL-70d2b066120c4b969f0b661d1d2fb664'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad'
      AND r.target_model_code = 'MODEL-70d2b066120c4b969f0b661d1d2fb664'
      AND r.field_code = 'MODEL_70d2b066120c4b969f0b661d1d2fb664_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad',
  tm.id, 'MODEL-7c4fe9b2d52d41cca5383488a4a400a5',
  '所属设备管理', 'MODEL_7c4fe9b2d52d41cca5383488a4a400a5_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad'
  AND tm.code = 'MODEL-7c4fe9b2d52d41cca5383488a4a400a5'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad'
      AND r.target_model_code = 'MODEL-7c4fe9b2d52d41cca5383488a4a400a5'
      AND r.field_code = 'MODEL_7c4fe9b2d52d41cca5383488a4a400a5_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad',
  tm.id, 'MODEL-7c746a60520347baaf64334625f07f31',
  '所属设备管理', 'MODEL_7c746a60520347baaf64334625f07f31_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad'
  AND tm.code = 'MODEL-7c746a60520347baaf64334625f07f31'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad'
      AND r.target_model_code = 'MODEL-7c746a60520347baaf64334625f07f31'
      AND r.field_code = 'MODEL_7c746a60520347baaf64334625f07f31_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad',
  tm.id, 'MODEL-81132ce00d2041c6a4ee06ec1c01dac8',
  '所属设备管理', 'MODEL_81132ce00d2041c6a4ee06ec1c01dac8_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad'
  AND tm.code = 'MODEL-81132ce00d2041c6a4ee06ec1c01dac8'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad'
      AND r.target_model_code = 'MODEL-81132ce00d2041c6a4ee06ec1c01dac8'
      AND r.field_code = 'MODEL_81132ce00d2041c6a4ee06ec1c01dac8_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad',
  tm.id, 'MODEL-88a38000121341e2950132da32baad90',
  '所属设备管理', 'MODEL_88a38000121341e2950132da32baad90_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad'
  AND tm.code = 'MODEL-88a38000121341e2950132da32baad90'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad'
      AND r.target_model_code = 'MODEL-88a38000121341e2950132da32baad90'
      AND r.field_code = 'MODEL_88a38000121341e2950132da32baad90_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad',
  tm.id, 'MODEL-990370e140aa481a82158bc96dc0b006',
  '所属设备管理', 'MODEL_990370e140aa481a82158bc96dc0b006_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad'
  AND tm.code = 'MODEL-990370e140aa481a82158bc96dc0b006'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad'
      AND r.target_model_code = 'MODEL-990370e140aa481a82158bc96dc0b006'
      AND r.field_code = 'MODEL_990370e140aa481a82158bc96dc0b006_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad',
  tm.id, 'MODEL-a4ce7668efe94d2fbc47a658be1a35d2',
  '所属设备管理', 'MODEL_a4ce7668efe94d2fbc47a658be1a35d2_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad'
  AND tm.code = 'MODEL-a4ce7668efe94d2fbc47a658be1a35d2'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad'
      AND r.target_model_code = 'MODEL-a4ce7668efe94d2fbc47a658be1a35d2'
      AND r.field_code = 'MODEL_a4ce7668efe94d2fbc47a658be1a35d2_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad',
  tm.id, 'MODEL-a62c727c0a024e19a8221c68fcf0c09a',
  '所属设备管理', 'MODEL_a62c727c0a024e19a8221c68fcf0c09a_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad'
  AND tm.code = 'MODEL-a62c727c0a024e19a8221c68fcf0c09a'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad'
      AND r.target_model_code = 'MODEL-a62c727c0a024e19a8221c68fcf0c09a'
      AND r.field_code = 'MODEL_a62c727c0a024e19a8221c68fcf0c09a_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad',
  tm.id, 'MODEL-b8251da851514341adef7f6f72995cf3',
  '所属设备管理', 'MODEL_b8251da851514341adef7f6f72995cf3_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad'
  AND tm.code = 'MODEL-b8251da851514341adef7f6f72995cf3'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad'
      AND r.target_model_code = 'MODEL-b8251da851514341adef7f6f72995cf3'
      AND r.field_code = 'MODEL_b8251da851514341adef7f6f72995cf3_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad',
  tm.id, 'MODEL-b83f05dc94f34fb4807458c0a48ba488',
  '所属设备管理', 'MODEL_b83f05dc94f34fb4807458c0a48ba488_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad'
  AND tm.code = 'MODEL-b83f05dc94f34fb4807458c0a48ba488'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad'
      AND r.target_model_code = 'MODEL-b83f05dc94f34fb4807458c0a48ba488'
      AND r.field_code = 'MODEL_b83f05dc94f34fb4807458c0a48ba488_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad',
  tm.id, 'MODEL-bc2cdffeab434f4d862c847fa99938ba',
  '所属设备管理', 'MODEL_bc2cdffeab434f4d862c847fa99938ba_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad'
  AND tm.code = 'MODEL-bc2cdffeab434f4d862c847fa99938ba'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad'
      AND r.target_model_code = 'MODEL-bc2cdffeab434f4d862c847fa99938ba'
      AND r.field_code = 'MODEL_bc2cdffeab434f4d862c847fa99938ba_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad',
  tm.id, 'MODEL-bf41bd50df044f5faf43c4759353ccd1',
  '所属设备管理', 'MODEL_bf41bd50df044f5faf43c4759353ccd1_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad'
  AND tm.code = 'MODEL-bf41bd50df044f5faf43c4759353ccd1'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad'
      AND r.target_model_code = 'MODEL-bf41bd50df044f5faf43c4759353ccd1'
      AND r.field_code = 'MODEL_bf41bd50df044f5faf43c4759353ccd1_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad',
  tm.id, 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9',
  '所属设备管理', 'MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad'
  AND tm.code = 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad'
      AND r.target_model_code = 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9'
      AND r.field_code = 'MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad',
  tm.id, 'MODEL-c6bc32646f3e4bde8409120c5c290eee',
  '所属设备管理', 'MODEL_c6bc32646f3e4bde8409120c5c290eee_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad'
  AND tm.code = 'MODEL-c6bc32646f3e4bde8409120c5c290eee'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad'
      AND r.target_model_code = 'MODEL-c6bc32646f3e4bde8409120c5c290eee'
      AND r.field_code = 'MODEL_c6bc32646f3e4bde8409120c5c290eee_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad',
  tm.id, 'MODEL-d446eef7329f45b4a374347e215fec5d',
  '所属设备管理', 'MODEL_d446eef7329f45b4a374347e215fec5d_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad'
  AND tm.code = 'MODEL-d446eef7329f45b4a374347e215fec5d'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad'
      AND r.target_model_code = 'MODEL-d446eef7329f45b4a374347e215fec5d'
      AND r.field_code = 'MODEL_d446eef7329f45b4a374347e215fec5d_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad',
  tm.id, 'MODEL-e25f3089a99d4878a1ddd326ba0910bf',
  '所属设备管理', 'MODEL_e25f3089a99d4878a1ddd326ba0910bf_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad'
  AND tm.code = 'MODEL-e25f3089a99d4878a1ddd326ba0910bf'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad'
      AND r.target_model_code = 'MODEL-e25f3089a99d4878a1ddd326ba0910bf'
      AND r.field_code = 'MODEL_e25f3089a99d4878a1ddd326ba0910bf_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad',
  tm.id, 'MODEL-e5af81c45bbc44d48374d8036032cc5d',
  '所属设备管理', 'MODEL_e5af81c45bbc44d48374d8036032cc5d_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad'
  AND tm.code = 'MODEL-e5af81c45bbc44d48374d8036032cc5d'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad'
      AND r.target_model_code = 'MODEL-e5af81c45bbc44d48374d8036032cc5d'
      AND r.field_code = 'MODEL_e5af81c45bbc44d48374d8036032cc5d_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad',
  tm.id, 'MODEL-e5c45ff8ca074102829bba872e091bd1',
  '所属设备管理', 'MODEL_e5c45ff8ca074102829bba872e091bd1_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad'
  AND tm.code = 'MODEL-e5c45ff8ca074102829bba872e091bd1'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad'
      AND r.target_model_code = 'MODEL-e5c45ff8ca074102829bba872e091bd1'
      AND r.field_code = 'MODEL_e5c45ff8ca074102829bba872e091bd1_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad',
  tm.id, 'MODEL-f5270d478dd849389e59a178cd512629',
  '所属设备管理', 'MODEL_f5270d478dd849389e59a178cd512629_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad'
  AND tm.code = 'MODEL-f5270d478dd849389e59a178cd512629'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad'
      AND r.target_model_code = 'MODEL-f5270d478dd849389e59a178cd512629'
      AND r.field_code = 'MODEL_f5270d478dd849389e59a178cd512629_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad',
  tm.id, 'MODEL-f55ce56d31f741caa5a69801556834fe',
  '所属设备管理', 'MODEL_f55ce56d31f741caa5a69801556834fe_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad'
  AND tm.code = 'MODEL-f55ce56d31f741caa5a69801556834fe'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad'
      AND r.target_model_code = 'MODEL-f55ce56d31f741caa5a69801556834fe'
      AND r.field_code = 'MODEL_f55ce56d31f741caa5a69801556834fe_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-6f334d0329224744ab4d8c800ea6b0e0',
  tm.id, 'MODEL-05e5c6a6c8fe4f3c9bd1dd86dd24a659',
  '所属设备管理', 'MODEL_05e5c6a6c8fe4f3c9bd1dd86dd24a659_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
  AND tm.code = 'MODEL-05e5c6a6c8fe4f3c9bd1dd86dd24a659'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
      AND r.target_model_code = 'MODEL-05e5c6a6c8fe4f3c9bd1dd86dd24a659'
      AND r.field_code = 'MODEL_05e5c6a6c8fe4f3c9bd1dd86dd24a659_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-6f334d0329224744ab4d8c800ea6b0e0',
  tm.id, 'MODEL-123328b7bfe745ceb337d286ac64aa17',
  '所属设备管理', 'MODEL_123328b7bfe745ceb337d286ac64aa17_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
  AND tm.code = 'MODEL-123328b7bfe745ceb337d286ac64aa17'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
      AND r.target_model_code = 'MODEL-123328b7bfe745ceb337d286ac64aa17'
      AND r.field_code = 'MODEL_123328b7bfe745ceb337d286ac64aa17_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-6f334d0329224744ab4d8c800ea6b0e0',
  tm.id, 'MODEL-17eb9be1c7404734af570ce040f1783b',
  '所属设备管理', 'MODEL_17eb9be1c7404734af570ce040f1783b_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
  AND tm.code = 'MODEL-17eb9be1c7404734af570ce040f1783b'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
      AND r.target_model_code = 'MODEL-17eb9be1c7404734af570ce040f1783b'
      AND r.field_code = 'MODEL_17eb9be1c7404734af570ce040f1783b_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-6f334d0329224744ab4d8c800ea6b0e0',
  tm.id, 'MODEL-26eabac35cb644cc84ae64dfb5ff9ba4',
  '所属设备管理', 'MODEL_26eabac35cb644cc84ae64dfb5ff9ba4_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
  AND tm.code = 'MODEL-26eabac35cb644cc84ae64dfb5ff9ba4'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
      AND r.target_model_code = 'MODEL-26eabac35cb644cc84ae64dfb5ff9ba4'
      AND r.field_code = 'MODEL_26eabac35cb644cc84ae64dfb5ff9ba4_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-6f334d0329224744ab4d8c800ea6b0e0',
  tm.id, 'MODEL-2af0515a5a36420086980b2322037edb',
  '所属设备管理', 'MODEL_2af0515a5a36420086980b2322037edb_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
  AND tm.code = 'MODEL-2af0515a5a36420086980b2322037edb'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
      AND r.target_model_code = 'MODEL-2af0515a5a36420086980b2322037edb'
      AND r.field_code = 'MODEL_2af0515a5a36420086980b2322037edb_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-6f334d0329224744ab4d8c800ea6b0e0',
  tm.id, 'MODEL-3130eba3747e4a5aa0862c94807822ba',
  '所属设备管理', 'MODEL_3130eba3747e4a5aa0862c94807822ba_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
  AND tm.code = 'MODEL-3130eba3747e4a5aa0862c94807822ba'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
      AND r.target_model_code = 'MODEL-3130eba3747e4a5aa0862c94807822ba'
      AND r.field_code = 'MODEL_3130eba3747e4a5aa0862c94807822ba_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-6f334d0329224744ab4d8c800ea6b0e0',
  tm.id, 'MODEL-44cc7511fc224085a51e2887569c933a',
  '所属设备管理', 'MODEL_44cc7511fc224085a51e2887569c933a_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
  AND tm.code = 'MODEL-44cc7511fc224085a51e2887569c933a'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
      AND r.target_model_code = 'MODEL-44cc7511fc224085a51e2887569c933a'
      AND r.field_code = 'MODEL_44cc7511fc224085a51e2887569c933a_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-6f334d0329224744ab4d8c800ea6b0e0',
  tm.id, 'MODEL-50991eab2a714ed288d00c58ff01c810',
  '所属设备管理', 'MODEL_50991eab2a714ed288d00c58ff01c810_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
  AND tm.code = 'MODEL-50991eab2a714ed288d00c58ff01c810'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
      AND r.target_model_code = 'MODEL-50991eab2a714ed288d00c58ff01c810'
      AND r.field_code = 'MODEL_50991eab2a714ed288d00c58ff01c810_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-6f334d0329224744ab4d8c800ea6b0e0',
  tm.id, 'MODEL-5a9b87c42571486998d363101c54ec8c',
  '所属设备管理', 'MODEL_5a9b87c42571486998d363101c54ec8c_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
  AND tm.code = 'MODEL-5a9b87c42571486998d363101c54ec8c'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
      AND r.target_model_code = 'MODEL-5a9b87c42571486998d363101c54ec8c'
      AND r.field_code = 'MODEL_5a9b87c42571486998d363101c54ec8c_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-6f334d0329224744ab4d8c800ea6b0e0',
  tm.id, 'MODEL-5f6f9243c7bb4e23bfed2fc01b48cf06',
  '所属设备管理', 'MODEL_5f6f9243c7bb4e23bfed2fc01b48cf06_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
  AND tm.code = 'MODEL-5f6f9243c7bb4e23bfed2fc01b48cf06'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
      AND r.target_model_code = 'MODEL-5f6f9243c7bb4e23bfed2fc01b48cf06'
      AND r.field_code = 'MODEL_5f6f9243c7bb4e23bfed2fc01b48cf06_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-6f334d0329224744ab4d8c800ea6b0e0',
  tm.id, 'MODEL-68554821800248cfacacbac1a53cdf37',
  '所属设备管理', 'MODEL_68554821800248cfacacbac1a53cdf37_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
  AND tm.code = 'MODEL-68554821800248cfacacbac1a53cdf37'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
      AND r.target_model_code = 'MODEL-68554821800248cfacacbac1a53cdf37'
      AND r.field_code = 'MODEL_68554821800248cfacacbac1a53cdf37_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-6f334d0329224744ab4d8c800ea6b0e0',
  tm.id, 'MODEL-6fd43ab74f6f425f81f0ad547603a849',
  '所属设备管理', 'MODEL_6fd43ab74f6f425f81f0ad547603a849_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
  AND tm.code = 'MODEL-6fd43ab74f6f425f81f0ad547603a849'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
      AND r.target_model_code = 'MODEL-6fd43ab74f6f425f81f0ad547603a849'
      AND r.field_code = 'MODEL_6fd43ab74f6f425f81f0ad547603a849_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-6f334d0329224744ab4d8c800ea6b0e0',
  tm.id, 'MODEL-70d2b066120c4b969f0b661d1d2fb664',
  '所属设备管理', 'MODEL_70d2b066120c4b969f0b661d1d2fb664_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
  AND tm.code = 'MODEL-70d2b066120c4b969f0b661d1d2fb664'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
      AND r.target_model_code = 'MODEL-70d2b066120c4b969f0b661d1d2fb664'
      AND r.field_code = 'MODEL_70d2b066120c4b969f0b661d1d2fb664_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-6f334d0329224744ab4d8c800ea6b0e0',
  tm.id, 'MODEL-7c4fe9b2d52d41cca5383488a4a400a5',
  '所属设备管理', 'MODEL_7c4fe9b2d52d41cca5383488a4a400a5_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
  AND tm.code = 'MODEL-7c4fe9b2d52d41cca5383488a4a400a5'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
      AND r.target_model_code = 'MODEL-7c4fe9b2d52d41cca5383488a4a400a5'
      AND r.field_code = 'MODEL_7c4fe9b2d52d41cca5383488a4a400a5_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-6f334d0329224744ab4d8c800ea6b0e0',
  tm.id, 'MODEL-7c746a60520347baaf64334625f07f31',
  '所属设备管理', 'MODEL_7c746a60520347baaf64334625f07f31_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
  AND tm.code = 'MODEL-7c746a60520347baaf64334625f07f31'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
      AND r.target_model_code = 'MODEL-7c746a60520347baaf64334625f07f31'
      AND r.field_code = 'MODEL_7c746a60520347baaf64334625f07f31_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-6f334d0329224744ab4d8c800ea6b0e0',
  tm.id, 'MODEL-81132ce00d2041c6a4ee06ec1c01dac8',
  '所属设备管理', 'MODEL_81132ce00d2041c6a4ee06ec1c01dac8_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
  AND tm.code = 'MODEL-81132ce00d2041c6a4ee06ec1c01dac8'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
      AND r.target_model_code = 'MODEL-81132ce00d2041c6a4ee06ec1c01dac8'
      AND r.field_code = 'MODEL_81132ce00d2041c6a4ee06ec1c01dac8_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-6f334d0329224744ab4d8c800ea6b0e0',
  tm.id, 'MODEL-88a38000121341e2950132da32baad90',
  '所属设备管理', 'MODEL_88a38000121341e2950132da32baad90_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
  AND tm.code = 'MODEL-88a38000121341e2950132da32baad90'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
      AND r.target_model_code = 'MODEL-88a38000121341e2950132da32baad90'
      AND r.field_code = 'MODEL_88a38000121341e2950132da32baad90_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-6f334d0329224744ab4d8c800ea6b0e0',
  tm.id, 'MODEL-990370e140aa481a82158bc96dc0b006',
  '所属设备管理', 'MODEL_990370e140aa481a82158bc96dc0b006_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
  AND tm.code = 'MODEL-990370e140aa481a82158bc96dc0b006'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
      AND r.target_model_code = 'MODEL-990370e140aa481a82158bc96dc0b006'
      AND r.field_code = 'MODEL_990370e140aa481a82158bc96dc0b006_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-6f334d0329224744ab4d8c800ea6b0e0',
  tm.id, 'MODEL-a4ce7668efe94d2fbc47a658be1a35d2',
  '所属设备管理', 'MODEL_a4ce7668efe94d2fbc47a658be1a35d2_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
  AND tm.code = 'MODEL-a4ce7668efe94d2fbc47a658be1a35d2'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
      AND r.target_model_code = 'MODEL-a4ce7668efe94d2fbc47a658be1a35d2'
      AND r.field_code = 'MODEL_a4ce7668efe94d2fbc47a658be1a35d2_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-6f334d0329224744ab4d8c800ea6b0e0',
  tm.id, 'MODEL-a62c727c0a024e19a8221c68fcf0c09a',
  '所属设备管理', 'MODEL_a62c727c0a024e19a8221c68fcf0c09a_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
  AND tm.code = 'MODEL-a62c727c0a024e19a8221c68fcf0c09a'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
      AND r.target_model_code = 'MODEL-a62c727c0a024e19a8221c68fcf0c09a'
      AND r.field_code = 'MODEL_a62c727c0a024e19a8221c68fcf0c09a_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-6f334d0329224744ab4d8c800ea6b0e0',
  tm.id, 'MODEL-b8251da851514341adef7f6f72995cf3',
  '所属设备管理', 'MODEL_b8251da851514341adef7f6f72995cf3_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
  AND tm.code = 'MODEL-b8251da851514341adef7f6f72995cf3'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
      AND r.target_model_code = 'MODEL-b8251da851514341adef7f6f72995cf3'
      AND r.field_code = 'MODEL_b8251da851514341adef7f6f72995cf3_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-6f334d0329224744ab4d8c800ea6b0e0',
  tm.id, 'MODEL-b83f05dc94f34fb4807458c0a48ba488',
  '所属设备管理', 'MODEL_b83f05dc94f34fb4807458c0a48ba488_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
  AND tm.code = 'MODEL-b83f05dc94f34fb4807458c0a48ba488'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
      AND r.target_model_code = 'MODEL-b83f05dc94f34fb4807458c0a48ba488'
      AND r.field_code = 'MODEL_b83f05dc94f34fb4807458c0a48ba488_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-6f334d0329224744ab4d8c800ea6b0e0',
  tm.id, 'MODEL-bc2cdffeab434f4d862c847fa99938ba',
  '所属设备管理', 'MODEL_bc2cdffeab434f4d862c847fa99938ba_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
  AND tm.code = 'MODEL-bc2cdffeab434f4d862c847fa99938ba'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
      AND r.target_model_code = 'MODEL-bc2cdffeab434f4d862c847fa99938ba'
      AND r.field_code = 'MODEL_bc2cdffeab434f4d862c847fa99938ba_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-6f334d0329224744ab4d8c800ea6b0e0',
  tm.id, 'MODEL-bf41bd50df044f5faf43c4759353ccd1',
  '所属设备管理', 'MODEL_bf41bd50df044f5faf43c4759353ccd1_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
  AND tm.code = 'MODEL-bf41bd50df044f5faf43c4759353ccd1'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
      AND r.target_model_code = 'MODEL-bf41bd50df044f5faf43c4759353ccd1'
      AND r.field_code = 'MODEL_bf41bd50df044f5faf43c4759353ccd1_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-6f334d0329224744ab4d8c800ea6b0e0',
  tm.id, 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9',
  '所属设备管理', 'MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
  AND tm.code = 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
      AND r.target_model_code = 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9'
      AND r.field_code = 'MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-6f334d0329224744ab4d8c800ea6b0e0',
  tm.id, 'MODEL-c6bc32646f3e4bde8409120c5c290eee',
  '所属设备管理', 'MODEL_c6bc32646f3e4bde8409120c5c290eee_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
  AND tm.code = 'MODEL-c6bc32646f3e4bde8409120c5c290eee'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
      AND r.target_model_code = 'MODEL-c6bc32646f3e4bde8409120c5c290eee'
      AND r.field_code = 'MODEL_c6bc32646f3e4bde8409120c5c290eee_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-6f334d0329224744ab4d8c800ea6b0e0',
  tm.id, 'MODEL-d446eef7329f45b4a374347e215fec5d',
  '所属设备管理', 'MODEL_d446eef7329f45b4a374347e215fec5d_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
  AND tm.code = 'MODEL-d446eef7329f45b4a374347e215fec5d'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
      AND r.target_model_code = 'MODEL-d446eef7329f45b4a374347e215fec5d'
      AND r.field_code = 'MODEL_d446eef7329f45b4a374347e215fec5d_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-6f334d0329224744ab4d8c800ea6b0e0',
  tm.id, 'MODEL-e25f3089a99d4878a1ddd326ba0910bf',
  '所属设备管理', 'MODEL_e25f3089a99d4878a1ddd326ba0910bf_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
  AND tm.code = 'MODEL-e25f3089a99d4878a1ddd326ba0910bf'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
      AND r.target_model_code = 'MODEL-e25f3089a99d4878a1ddd326ba0910bf'
      AND r.field_code = 'MODEL_e25f3089a99d4878a1ddd326ba0910bf_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-6f334d0329224744ab4d8c800ea6b0e0',
  tm.id, 'MODEL-e5af81c45bbc44d48374d8036032cc5d',
  '所属设备管理', 'MODEL_e5af81c45bbc44d48374d8036032cc5d_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
  AND tm.code = 'MODEL-e5af81c45bbc44d48374d8036032cc5d'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
      AND r.target_model_code = 'MODEL-e5af81c45bbc44d48374d8036032cc5d'
      AND r.field_code = 'MODEL_e5af81c45bbc44d48374d8036032cc5d_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-6f334d0329224744ab4d8c800ea6b0e0',
  tm.id, 'MODEL-e5c45ff8ca074102829bba872e091bd1',
  '所属设备管理', 'MODEL_e5c45ff8ca074102829bba872e091bd1_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
  AND tm.code = 'MODEL-e5c45ff8ca074102829bba872e091bd1'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
      AND r.target_model_code = 'MODEL-e5c45ff8ca074102829bba872e091bd1'
      AND r.field_code = 'MODEL_e5c45ff8ca074102829bba872e091bd1_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-6f334d0329224744ab4d8c800ea6b0e0',
  tm.id, 'MODEL-f5270d478dd849389e59a178cd512629',
  '所属设备管理', 'MODEL_f5270d478dd849389e59a178cd512629_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
  AND tm.code = 'MODEL-f5270d478dd849389e59a178cd512629'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
      AND r.target_model_code = 'MODEL-f5270d478dd849389e59a178cd512629'
      AND r.field_code = 'MODEL_f5270d478dd849389e59a178cd512629_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-6f334d0329224744ab4d8c800ea6b0e0',
  tm.id, 'MODEL-f55ce56d31f741caa5a69801556834fe',
  '所属设备管理', 'MODEL_f55ce56d31f741caa5a69801556834fe_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
  AND tm.code = 'MODEL-f55ce56d31f741caa5a69801556834fe'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
      AND r.target_model_code = 'MODEL-f55ce56d31f741caa5a69801556834fe'
      AND r.field_code = 'MODEL_f55ce56d31f741caa5a69801556834fe_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-8a091acfa63941459916e380e1e1020e',
  tm.id, 'MODEL-05e5c6a6c8fe4f3c9bd1dd86dd24a659',
  '所属设备管理', 'MODEL_05e5c6a6c8fe4f3c9bd1dd86dd24a659_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-8a091acfa63941459916e380e1e1020e'
  AND tm.code = 'MODEL-05e5c6a6c8fe4f3c9bd1dd86dd24a659'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-8a091acfa63941459916e380e1e1020e'
      AND r.target_model_code = 'MODEL-05e5c6a6c8fe4f3c9bd1dd86dd24a659'
      AND r.field_code = 'MODEL_05e5c6a6c8fe4f3c9bd1dd86dd24a659_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-8a091acfa63941459916e380e1e1020e',
  tm.id, 'MODEL-123328b7bfe745ceb337d286ac64aa17',
  '所属设备管理', 'MODEL_123328b7bfe745ceb337d286ac64aa17_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-8a091acfa63941459916e380e1e1020e'
  AND tm.code = 'MODEL-123328b7bfe745ceb337d286ac64aa17'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-8a091acfa63941459916e380e1e1020e'
      AND r.target_model_code = 'MODEL-123328b7bfe745ceb337d286ac64aa17'
      AND r.field_code = 'MODEL_123328b7bfe745ceb337d286ac64aa17_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-8a091acfa63941459916e380e1e1020e',
  tm.id, 'MODEL-17eb9be1c7404734af570ce040f1783b',
  '所属设备管理', 'MODEL_17eb9be1c7404734af570ce040f1783b_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-8a091acfa63941459916e380e1e1020e'
  AND tm.code = 'MODEL-17eb9be1c7404734af570ce040f1783b'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-8a091acfa63941459916e380e1e1020e'
      AND r.target_model_code = 'MODEL-17eb9be1c7404734af570ce040f1783b'
      AND r.field_code = 'MODEL_17eb9be1c7404734af570ce040f1783b_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-8a091acfa63941459916e380e1e1020e',
  tm.id, 'MODEL-26eabac35cb644cc84ae64dfb5ff9ba4',
  '所属设备管理', 'MODEL_26eabac35cb644cc84ae64dfb5ff9ba4_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-8a091acfa63941459916e380e1e1020e'
  AND tm.code = 'MODEL-26eabac35cb644cc84ae64dfb5ff9ba4'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-8a091acfa63941459916e380e1e1020e'
      AND r.target_model_code = 'MODEL-26eabac35cb644cc84ae64dfb5ff9ba4'
      AND r.field_code = 'MODEL_26eabac35cb644cc84ae64dfb5ff9ba4_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-8a091acfa63941459916e380e1e1020e',
  tm.id, 'MODEL-2af0515a5a36420086980b2322037edb',
  '所属设备管理', 'MODEL_2af0515a5a36420086980b2322037edb_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-8a091acfa63941459916e380e1e1020e'
  AND tm.code = 'MODEL-2af0515a5a36420086980b2322037edb'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-8a091acfa63941459916e380e1e1020e'
      AND r.target_model_code = 'MODEL-2af0515a5a36420086980b2322037edb'
      AND r.field_code = 'MODEL_2af0515a5a36420086980b2322037edb_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-8a091acfa63941459916e380e1e1020e',
  tm.id, 'MODEL-3130eba3747e4a5aa0862c94807822ba',
  '所属设备管理', 'MODEL_3130eba3747e4a5aa0862c94807822ba_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-8a091acfa63941459916e380e1e1020e'
  AND tm.code = 'MODEL-3130eba3747e4a5aa0862c94807822ba'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-8a091acfa63941459916e380e1e1020e'
      AND r.target_model_code = 'MODEL-3130eba3747e4a5aa0862c94807822ba'
      AND r.field_code = 'MODEL_3130eba3747e4a5aa0862c94807822ba_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-8a091acfa63941459916e380e1e1020e',
  tm.id, 'MODEL-44cc7511fc224085a51e2887569c933a',
  '所属设备管理', 'MODEL_44cc7511fc224085a51e2887569c933a_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-8a091acfa63941459916e380e1e1020e'
  AND tm.code = 'MODEL-44cc7511fc224085a51e2887569c933a'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-8a091acfa63941459916e380e1e1020e'
      AND r.target_model_code = 'MODEL-44cc7511fc224085a51e2887569c933a'
      AND r.field_code = 'MODEL_44cc7511fc224085a51e2887569c933a_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-8a091acfa63941459916e380e1e1020e',
  tm.id, 'MODEL-50991eab2a714ed288d00c58ff01c810',
  '所属设备管理', 'MODEL_50991eab2a714ed288d00c58ff01c810_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-8a091acfa63941459916e380e1e1020e'
  AND tm.code = 'MODEL-50991eab2a714ed288d00c58ff01c810'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-8a091acfa63941459916e380e1e1020e'
      AND r.target_model_code = 'MODEL-50991eab2a714ed288d00c58ff01c810'
      AND r.field_code = 'MODEL_50991eab2a714ed288d00c58ff01c810_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-8a091acfa63941459916e380e1e1020e',
  tm.id, 'MODEL-5a9b87c42571486998d363101c54ec8c',
  '所属设备管理', 'MODEL_5a9b87c42571486998d363101c54ec8c_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-8a091acfa63941459916e380e1e1020e'
  AND tm.code = 'MODEL-5a9b87c42571486998d363101c54ec8c'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-8a091acfa63941459916e380e1e1020e'
      AND r.target_model_code = 'MODEL-5a9b87c42571486998d363101c54ec8c'
      AND r.field_code = 'MODEL_5a9b87c42571486998d363101c54ec8c_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-8a091acfa63941459916e380e1e1020e',
  tm.id, 'MODEL-5f6f9243c7bb4e23bfed2fc01b48cf06',
  '所属设备管理', 'MODEL_5f6f9243c7bb4e23bfed2fc01b48cf06_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-8a091acfa63941459916e380e1e1020e'
  AND tm.code = 'MODEL-5f6f9243c7bb4e23bfed2fc01b48cf06'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-8a091acfa63941459916e380e1e1020e'
      AND r.target_model_code = 'MODEL-5f6f9243c7bb4e23bfed2fc01b48cf06'
      AND r.field_code = 'MODEL_5f6f9243c7bb4e23bfed2fc01b48cf06_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-8a091acfa63941459916e380e1e1020e',
  tm.id, 'MODEL-68554821800248cfacacbac1a53cdf37',
  '所属设备管理', 'MODEL_68554821800248cfacacbac1a53cdf37_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-8a091acfa63941459916e380e1e1020e'
  AND tm.code = 'MODEL-68554821800248cfacacbac1a53cdf37'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-8a091acfa63941459916e380e1e1020e'
      AND r.target_model_code = 'MODEL-68554821800248cfacacbac1a53cdf37'
      AND r.field_code = 'MODEL_68554821800248cfacacbac1a53cdf37_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-8a091acfa63941459916e380e1e1020e',
  tm.id, 'MODEL-6fd43ab74f6f425f81f0ad547603a849',
  '所属设备管理', 'MODEL_6fd43ab74f6f425f81f0ad547603a849_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-8a091acfa63941459916e380e1e1020e'
  AND tm.code = 'MODEL-6fd43ab74f6f425f81f0ad547603a849'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-8a091acfa63941459916e380e1e1020e'
      AND r.target_model_code = 'MODEL-6fd43ab74f6f425f81f0ad547603a849'
      AND r.field_code = 'MODEL_6fd43ab74f6f425f81f0ad547603a849_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-8a091acfa63941459916e380e1e1020e',
  tm.id, 'MODEL-70d2b066120c4b969f0b661d1d2fb664',
  '所属设备管理', 'MODEL_70d2b066120c4b969f0b661d1d2fb664_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-8a091acfa63941459916e380e1e1020e'
  AND tm.code = 'MODEL-70d2b066120c4b969f0b661d1d2fb664'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-8a091acfa63941459916e380e1e1020e'
      AND r.target_model_code = 'MODEL-70d2b066120c4b969f0b661d1d2fb664'
      AND r.field_code = 'MODEL_70d2b066120c4b969f0b661d1d2fb664_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-8a091acfa63941459916e380e1e1020e',
  tm.id, 'MODEL-7c4fe9b2d52d41cca5383488a4a400a5',
  '所属设备管理', 'MODEL_7c4fe9b2d52d41cca5383488a4a400a5_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-8a091acfa63941459916e380e1e1020e'
  AND tm.code = 'MODEL-7c4fe9b2d52d41cca5383488a4a400a5'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-8a091acfa63941459916e380e1e1020e'
      AND r.target_model_code = 'MODEL-7c4fe9b2d52d41cca5383488a4a400a5'
      AND r.field_code = 'MODEL_7c4fe9b2d52d41cca5383488a4a400a5_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-8a091acfa63941459916e380e1e1020e',
  tm.id, 'MODEL-7c746a60520347baaf64334625f07f31',
  '所属设备管理', 'MODEL_7c746a60520347baaf64334625f07f31_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-8a091acfa63941459916e380e1e1020e'
  AND tm.code = 'MODEL-7c746a60520347baaf64334625f07f31'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-8a091acfa63941459916e380e1e1020e'
      AND r.target_model_code = 'MODEL-7c746a60520347baaf64334625f07f31'
      AND r.field_code = 'MODEL_7c746a60520347baaf64334625f07f31_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-8a091acfa63941459916e380e1e1020e',
  tm.id, 'MODEL-81132ce00d2041c6a4ee06ec1c01dac8',
  '所属设备管理', 'MODEL_81132ce00d2041c6a4ee06ec1c01dac8_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-8a091acfa63941459916e380e1e1020e'
  AND tm.code = 'MODEL-81132ce00d2041c6a4ee06ec1c01dac8'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-8a091acfa63941459916e380e1e1020e'
      AND r.target_model_code = 'MODEL-81132ce00d2041c6a4ee06ec1c01dac8'
      AND r.field_code = 'MODEL_81132ce00d2041c6a4ee06ec1c01dac8_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-8a091acfa63941459916e380e1e1020e',
  tm.id, 'MODEL-88a38000121341e2950132da32baad90',
  '所属设备管理', 'MODEL_88a38000121341e2950132da32baad90_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-8a091acfa63941459916e380e1e1020e'
  AND tm.code = 'MODEL-88a38000121341e2950132da32baad90'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-8a091acfa63941459916e380e1e1020e'
      AND r.target_model_code = 'MODEL-88a38000121341e2950132da32baad90'
      AND r.field_code = 'MODEL_88a38000121341e2950132da32baad90_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-8a091acfa63941459916e380e1e1020e',
  tm.id, 'MODEL-990370e140aa481a82158bc96dc0b006',
  '所属设备管理', 'MODEL_990370e140aa481a82158bc96dc0b006_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-8a091acfa63941459916e380e1e1020e'
  AND tm.code = 'MODEL-990370e140aa481a82158bc96dc0b006'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-8a091acfa63941459916e380e1e1020e'
      AND r.target_model_code = 'MODEL-990370e140aa481a82158bc96dc0b006'
      AND r.field_code = 'MODEL_990370e140aa481a82158bc96dc0b006_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-8a091acfa63941459916e380e1e1020e',
  tm.id, 'MODEL-a4ce7668efe94d2fbc47a658be1a35d2',
  '所属设备管理', 'MODEL_a4ce7668efe94d2fbc47a658be1a35d2_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-8a091acfa63941459916e380e1e1020e'
  AND tm.code = 'MODEL-a4ce7668efe94d2fbc47a658be1a35d2'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-8a091acfa63941459916e380e1e1020e'
      AND r.target_model_code = 'MODEL-a4ce7668efe94d2fbc47a658be1a35d2'
      AND r.field_code = 'MODEL_a4ce7668efe94d2fbc47a658be1a35d2_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-8a091acfa63941459916e380e1e1020e',
  tm.id, 'MODEL-a62c727c0a024e19a8221c68fcf0c09a',
  '所属设备管理', 'MODEL_a62c727c0a024e19a8221c68fcf0c09a_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-8a091acfa63941459916e380e1e1020e'
  AND tm.code = 'MODEL-a62c727c0a024e19a8221c68fcf0c09a'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-8a091acfa63941459916e380e1e1020e'
      AND r.target_model_code = 'MODEL-a62c727c0a024e19a8221c68fcf0c09a'
      AND r.field_code = 'MODEL_a62c727c0a024e19a8221c68fcf0c09a_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-8a091acfa63941459916e380e1e1020e',
  tm.id, 'MODEL-b8251da851514341adef7f6f72995cf3',
  '所属设备管理', 'MODEL_b8251da851514341adef7f6f72995cf3_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-8a091acfa63941459916e380e1e1020e'
  AND tm.code = 'MODEL-b8251da851514341adef7f6f72995cf3'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-8a091acfa63941459916e380e1e1020e'
      AND r.target_model_code = 'MODEL-b8251da851514341adef7f6f72995cf3'
      AND r.field_code = 'MODEL_b8251da851514341adef7f6f72995cf3_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-8a091acfa63941459916e380e1e1020e',
  tm.id, 'MODEL-b83f05dc94f34fb4807458c0a48ba488',
  '所属设备管理', 'MODEL_b83f05dc94f34fb4807458c0a48ba488_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-8a091acfa63941459916e380e1e1020e'
  AND tm.code = 'MODEL-b83f05dc94f34fb4807458c0a48ba488'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-8a091acfa63941459916e380e1e1020e'
      AND r.target_model_code = 'MODEL-b83f05dc94f34fb4807458c0a48ba488'
      AND r.field_code = 'MODEL_b83f05dc94f34fb4807458c0a48ba488_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-8a091acfa63941459916e380e1e1020e',
  tm.id, 'MODEL-bc2cdffeab434f4d862c847fa99938ba',
  '所属设备管理', 'MODEL_bc2cdffeab434f4d862c847fa99938ba_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-8a091acfa63941459916e380e1e1020e'
  AND tm.code = 'MODEL-bc2cdffeab434f4d862c847fa99938ba'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-8a091acfa63941459916e380e1e1020e'
      AND r.target_model_code = 'MODEL-bc2cdffeab434f4d862c847fa99938ba'
      AND r.field_code = 'MODEL_bc2cdffeab434f4d862c847fa99938ba_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-8a091acfa63941459916e380e1e1020e',
  tm.id, 'MODEL-bf41bd50df044f5faf43c4759353ccd1',
  '所属设备管理', 'MODEL_bf41bd50df044f5faf43c4759353ccd1_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-8a091acfa63941459916e380e1e1020e'
  AND tm.code = 'MODEL-bf41bd50df044f5faf43c4759353ccd1'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-8a091acfa63941459916e380e1e1020e'
      AND r.target_model_code = 'MODEL-bf41bd50df044f5faf43c4759353ccd1'
      AND r.field_code = 'MODEL_bf41bd50df044f5faf43c4759353ccd1_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-8a091acfa63941459916e380e1e1020e',
  tm.id, 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9',
  '所属设备管理', 'MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-8a091acfa63941459916e380e1e1020e'
  AND tm.code = 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-8a091acfa63941459916e380e1e1020e'
      AND r.target_model_code = 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9'
      AND r.field_code = 'MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-8a091acfa63941459916e380e1e1020e',
  tm.id, 'MODEL-c6bc32646f3e4bde8409120c5c290eee',
  '所属设备管理', 'MODEL_c6bc32646f3e4bde8409120c5c290eee_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-8a091acfa63941459916e380e1e1020e'
  AND tm.code = 'MODEL-c6bc32646f3e4bde8409120c5c290eee'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-8a091acfa63941459916e380e1e1020e'
      AND r.target_model_code = 'MODEL-c6bc32646f3e4bde8409120c5c290eee'
      AND r.field_code = 'MODEL_c6bc32646f3e4bde8409120c5c290eee_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-8a091acfa63941459916e380e1e1020e',
  tm.id, 'MODEL-d446eef7329f45b4a374347e215fec5d',
  '所属设备管理', 'MODEL_d446eef7329f45b4a374347e215fec5d_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-8a091acfa63941459916e380e1e1020e'
  AND tm.code = 'MODEL-d446eef7329f45b4a374347e215fec5d'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-8a091acfa63941459916e380e1e1020e'
      AND r.target_model_code = 'MODEL-d446eef7329f45b4a374347e215fec5d'
      AND r.field_code = 'MODEL_d446eef7329f45b4a374347e215fec5d_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-8a091acfa63941459916e380e1e1020e',
  tm.id, 'MODEL-e25f3089a99d4878a1ddd326ba0910bf',
  '所属设备管理', 'MODEL_e25f3089a99d4878a1ddd326ba0910bf_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-8a091acfa63941459916e380e1e1020e'
  AND tm.code = 'MODEL-e25f3089a99d4878a1ddd326ba0910bf'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-8a091acfa63941459916e380e1e1020e'
      AND r.target_model_code = 'MODEL-e25f3089a99d4878a1ddd326ba0910bf'
      AND r.field_code = 'MODEL_e25f3089a99d4878a1ddd326ba0910bf_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-8a091acfa63941459916e380e1e1020e',
  tm.id, 'MODEL-e5af81c45bbc44d48374d8036032cc5d',
  '所属设备管理', 'MODEL_e5af81c45bbc44d48374d8036032cc5d_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-8a091acfa63941459916e380e1e1020e'
  AND tm.code = 'MODEL-e5af81c45bbc44d48374d8036032cc5d'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-8a091acfa63941459916e380e1e1020e'
      AND r.target_model_code = 'MODEL-e5af81c45bbc44d48374d8036032cc5d'
      AND r.field_code = 'MODEL_e5af81c45bbc44d48374d8036032cc5d_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-8a091acfa63941459916e380e1e1020e',
  tm.id, 'MODEL-e5c45ff8ca074102829bba872e091bd1',
  '所属设备管理', 'MODEL_e5c45ff8ca074102829bba872e091bd1_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-8a091acfa63941459916e380e1e1020e'
  AND tm.code = 'MODEL-e5c45ff8ca074102829bba872e091bd1'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-8a091acfa63941459916e380e1e1020e'
      AND r.target_model_code = 'MODEL-e5c45ff8ca074102829bba872e091bd1'
      AND r.field_code = 'MODEL_e5c45ff8ca074102829bba872e091bd1_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-8a091acfa63941459916e380e1e1020e',
  tm.id, 'MODEL-f5270d478dd849389e59a178cd512629',
  '所属设备管理', 'MODEL_f5270d478dd849389e59a178cd512629_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-8a091acfa63941459916e380e1e1020e'
  AND tm.code = 'MODEL-f5270d478dd849389e59a178cd512629'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-8a091acfa63941459916e380e1e1020e'
      AND r.target_model_code = 'MODEL-f5270d478dd849389e59a178cd512629'
      AND r.field_code = 'MODEL_f5270d478dd849389e59a178cd512629_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-8a091acfa63941459916e380e1e1020e',
  tm.id, 'MODEL-f55ce56d31f741caa5a69801556834fe',
  '所属设备管理', 'MODEL_f55ce56d31f741caa5a69801556834fe_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-8a091acfa63941459916e380e1e1020e'
  AND tm.code = 'MODEL-f55ce56d31f741caa5a69801556834fe'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-8a091acfa63941459916e380e1e1020e'
      AND r.target_model_code = 'MODEL-f55ce56d31f741caa5a69801556834fe'
      AND r.field_code = 'MODEL_f55ce56d31f741caa5a69801556834fe_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-8c85f68408244a2db7c04cfc00345e2f',
  tm.id, 'MODEL-05e5c6a6c8fe4f3c9bd1dd86dd24a659',
  '所属设备管理', 'MODEL_05e5c6a6c8fe4f3c9bd1dd86dd24a659_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-8c85f68408244a2db7c04cfc00345e2f'
  AND tm.code = 'MODEL-05e5c6a6c8fe4f3c9bd1dd86dd24a659'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-8c85f68408244a2db7c04cfc00345e2f'
      AND r.target_model_code = 'MODEL-05e5c6a6c8fe4f3c9bd1dd86dd24a659'
      AND r.field_code = 'MODEL_05e5c6a6c8fe4f3c9bd1dd86dd24a659_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-8c85f68408244a2db7c04cfc00345e2f',
  tm.id, 'MODEL-123328b7bfe745ceb337d286ac64aa17',
  '所属设备管理', 'MODEL_123328b7bfe745ceb337d286ac64aa17_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-8c85f68408244a2db7c04cfc00345e2f'
  AND tm.code = 'MODEL-123328b7bfe745ceb337d286ac64aa17'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-8c85f68408244a2db7c04cfc00345e2f'
      AND r.target_model_code = 'MODEL-123328b7bfe745ceb337d286ac64aa17'
      AND r.field_code = 'MODEL_123328b7bfe745ceb337d286ac64aa17_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-8c85f68408244a2db7c04cfc00345e2f',
  tm.id, 'MODEL-17eb9be1c7404734af570ce040f1783b',
  '所属设备管理', 'MODEL_17eb9be1c7404734af570ce040f1783b_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-8c85f68408244a2db7c04cfc00345e2f'
  AND tm.code = 'MODEL-17eb9be1c7404734af570ce040f1783b'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-8c85f68408244a2db7c04cfc00345e2f'
      AND r.target_model_code = 'MODEL-17eb9be1c7404734af570ce040f1783b'
      AND r.field_code = 'MODEL_17eb9be1c7404734af570ce040f1783b_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-8c85f68408244a2db7c04cfc00345e2f',
  tm.id, 'MODEL-26eabac35cb644cc84ae64dfb5ff9ba4',
  '所属设备管理', 'MODEL_26eabac35cb644cc84ae64dfb5ff9ba4_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-8c85f68408244a2db7c04cfc00345e2f'
  AND tm.code = 'MODEL-26eabac35cb644cc84ae64dfb5ff9ba4'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-8c85f68408244a2db7c04cfc00345e2f'
      AND r.target_model_code = 'MODEL-26eabac35cb644cc84ae64dfb5ff9ba4'
      AND r.field_code = 'MODEL_26eabac35cb644cc84ae64dfb5ff9ba4_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-8c85f68408244a2db7c04cfc00345e2f',
  tm.id, 'MODEL-2af0515a5a36420086980b2322037edb',
  '所属设备管理', 'MODEL_2af0515a5a36420086980b2322037edb_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-8c85f68408244a2db7c04cfc00345e2f'
  AND tm.code = 'MODEL-2af0515a5a36420086980b2322037edb'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-8c85f68408244a2db7c04cfc00345e2f'
      AND r.target_model_code = 'MODEL-2af0515a5a36420086980b2322037edb'
      AND r.field_code = 'MODEL_2af0515a5a36420086980b2322037edb_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-8c85f68408244a2db7c04cfc00345e2f',
  tm.id, 'MODEL-3130eba3747e4a5aa0862c94807822ba',
  '所属设备管理', 'MODEL_3130eba3747e4a5aa0862c94807822ba_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-8c85f68408244a2db7c04cfc00345e2f'
  AND tm.code = 'MODEL-3130eba3747e4a5aa0862c94807822ba'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-8c85f68408244a2db7c04cfc00345e2f'
      AND r.target_model_code = 'MODEL-3130eba3747e4a5aa0862c94807822ba'
      AND r.field_code = 'MODEL_3130eba3747e4a5aa0862c94807822ba_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-8c85f68408244a2db7c04cfc00345e2f',
  tm.id, 'MODEL-44cc7511fc224085a51e2887569c933a',
  '所属设备管理', 'MODEL_44cc7511fc224085a51e2887569c933a_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-8c85f68408244a2db7c04cfc00345e2f'
  AND tm.code = 'MODEL-44cc7511fc224085a51e2887569c933a'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-8c85f68408244a2db7c04cfc00345e2f'
      AND r.target_model_code = 'MODEL-44cc7511fc224085a51e2887569c933a'
      AND r.field_code = 'MODEL_44cc7511fc224085a51e2887569c933a_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-8c85f68408244a2db7c04cfc00345e2f',
  tm.id, 'MODEL-50991eab2a714ed288d00c58ff01c810',
  '所属设备管理', 'MODEL_50991eab2a714ed288d00c58ff01c810_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-8c85f68408244a2db7c04cfc00345e2f'
  AND tm.code = 'MODEL-50991eab2a714ed288d00c58ff01c810'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-8c85f68408244a2db7c04cfc00345e2f'
      AND r.target_model_code = 'MODEL-50991eab2a714ed288d00c58ff01c810'
      AND r.field_code = 'MODEL_50991eab2a714ed288d00c58ff01c810_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-8c85f68408244a2db7c04cfc00345e2f',
  tm.id, 'MODEL-5a9b87c42571486998d363101c54ec8c',
  '所属设备管理', 'MODEL_5a9b87c42571486998d363101c54ec8c_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-8c85f68408244a2db7c04cfc00345e2f'
  AND tm.code = 'MODEL-5a9b87c42571486998d363101c54ec8c'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-8c85f68408244a2db7c04cfc00345e2f'
      AND r.target_model_code = 'MODEL-5a9b87c42571486998d363101c54ec8c'
      AND r.field_code = 'MODEL_5a9b87c42571486998d363101c54ec8c_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-8c85f68408244a2db7c04cfc00345e2f',
  tm.id, 'MODEL-5f6f9243c7bb4e23bfed2fc01b48cf06',
  '所属设备管理', 'MODEL_5f6f9243c7bb4e23bfed2fc01b48cf06_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-8c85f68408244a2db7c04cfc00345e2f'
  AND tm.code = 'MODEL-5f6f9243c7bb4e23bfed2fc01b48cf06'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-8c85f68408244a2db7c04cfc00345e2f'
      AND r.target_model_code = 'MODEL-5f6f9243c7bb4e23bfed2fc01b48cf06'
      AND r.field_code = 'MODEL_5f6f9243c7bb4e23bfed2fc01b48cf06_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-8c85f68408244a2db7c04cfc00345e2f',
  tm.id, 'MODEL-68554821800248cfacacbac1a53cdf37',
  '所属设备管理', 'MODEL_68554821800248cfacacbac1a53cdf37_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-8c85f68408244a2db7c04cfc00345e2f'
  AND tm.code = 'MODEL-68554821800248cfacacbac1a53cdf37'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-8c85f68408244a2db7c04cfc00345e2f'
      AND r.target_model_code = 'MODEL-68554821800248cfacacbac1a53cdf37'
      AND r.field_code = 'MODEL_68554821800248cfacacbac1a53cdf37_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-8c85f68408244a2db7c04cfc00345e2f',
  tm.id, 'MODEL-6fd43ab74f6f425f81f0ad547603a849',
  '所属设备管理', 'MODEL_6fd43ab74f6f425f81f0ad547603a849_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-8c85f68408244a2db7c04cfc00345e2f'
  AND tm.code = 'MODEL-6fd43ab74f6f425f81f0ad547603a849'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-8c85f68408244a2db7c04cfc00345e2f'
      AND r.target_model_code = 'MODEL-6fd43ab74f6f425f81f0ad547603a849'
      AND r.field_code = 'MODEL_6fd43ab74f6f425f81f0ad547603a849_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-8c85f68408244a2db7c04cfc00345e2f',
  tm.id, 'MODEL-70d2b066120c4b969f0b661d1d2fb664',
  '所属设备管理', 'MODEL_70d2b066120c4b969f0b661d1d2fb664_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-8c85f68408244a2db7c04cfc00345e2f'
  AND tm.code = 'MODEL-70d2b066120c4b969f0b661d1d2fb664'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-8c85f68408244a2db7c04cfc00345e2f'
      AND r.target_model_code = 'MODEL-70d2b066120c4b969f0b661d1d2fb664'
      AND r.field_code = 'MODEL_70d2b066120c4b969f0b661d1d2fb664_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-8c85f68408244a2db7c04cfc00345e2f',
  tm.id, 'MODEL-7c4fe9b2d52d41cca5383488a4a400a5',
  '所属设备管理', 'MODEL_7c4fe9b2d52d41cca5383488a4a400a5_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-8c85f68408244a2db7c04cfc00345e2f'
  AND tm.code = 'MODEL-7c4fe9b2d52d41cca5383488a4a400a5'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-8c85f68408244a2db7c04cfc00345e2f'
      AND r.target_model_code = 'MODEL-7c4fe9b2d52d41cca5383488a4a400a5'
      AND r.field_code = 'MODEL_7c4fe9b2d52d41cca5383488a4a400a5_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-8c85f68408244a2db7c04cfc00345e2f',
  tm.id, 'MODEL-7c746a60520347baaf64334625f07f31',
  '所属设备管理', 'MODEL_7c746a60520347baaf64334625f07f31_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-8c85f68408244a2db7c04cfc00345e2f'
  AND tm.code = 'MODEL-7c746a60520347baaf64334625f07f31'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-8c85f68408244a2db7c04cfc00345e2f'
      AND r.target_model_code = 'MODEL-7c746a60520347baaf64334625f07f31'
      AND r.field_code = 'MODEL_7c746a60520347baaf64334625f07f31_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-8c85f68408244a2db7c04cfc00345e2f',
  tm.id, 'MODEL-81132ce00d2041c6a4ee06ec1c01dac8',
  '所属设备管理', 'MODEL_81132ce00d2041c6a4ee06ec1c01dac8_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-8c85f68408244a2db7c04cfc00345e2f'
  AND tm.code = 'MODEL-81132ce00d2041c6a4ee06ec1c01dac8'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-8c85f68408244a2db7c04cfc00345e2f'
      AND r.target_model_code = 'MODEL-81132ce00d2041c6a4ee06ec1c01dac8'
      AND r.field_code = 'MODEL_81132ce00d2041c6a4ee06ec1c01dac8_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-8c85f68408244a2db7c04cfc00345e2f',
  tm.id, 'MODEL-88a38000121341e2950132da32baad90',
  '所属设备管理', 'MODEL_88a38000121341e2950132da32baad90_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-8c85f68408244a2db7c04cfc00345e2f'
  AND tm.code = 'MODEL-88a38000121341e2950132da32baad90'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-8c85f68408244a2db7c04cfc00345e2f'
      AND r.target_model_code = 'MODEL-88a38000121341e2950132da32baad90'
      AND r.field_code = 'MODEL_88a38000121341e2950132da32baad90_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-8c85f68408244a2db7c04cfc00345e2f',
  tm.id, 'MODEL-990370e140aa481a82158bc96dc0b006',
  '所属设备管理', 'MODEL_990370e140aa481a82158bc96dc0b006_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-8c85f68408244a2db7c04cfc00345e2f'
  AND tm.code = 'MODEL-990370e140aa481a82158bc96dc0b006'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-8c85f68408244a2db7c04cfc00345e2f'
      AND r.target_model_code = 'MODEL-990370e140aa481a82158bc96dc0b006'
      AND r.field_code = 'MODEL_990370e140aa481a82158bc96dc0b006_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-8c85f68408244a2db7c04cfc00345e2f',
  tm.id, 'MODEL-a4ce7668efe94d2fbc47a658be1a35d2',
  '所属设备管理', 'MODEL_a4ce7668efe94d2fbc47a658be1a35d2_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-8c85f68408244a2db7c04cfc00345e2f'
  AND tm.code = 'MODEL-a4ce7668efe94d2fbc47a658be1a35d2'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-8c85f68408244a2db7c04cfc00345e2f'
      AND r.target_model_code = 'MODEL-a4ce7668efe94d2fbc47a658be1a35d2'
      AND r.field_code = 'MODEL_a4ce7668efe94d2fbc47a658be1a35d2_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-8c85f68408244a2db7c04cfc00345e2f',
  tm.id, 'MODEL-a62c727c0a024e19a8221c68fcf0c09a',
  '所属设备管理', 'MODEL_a62c727c0a024e19a8221c68fcf0c09a_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-8c85f68408244a2db7c04cfc00345e2f'
  AND tm.code = 'MODEL-a62c727c0a024e19a8221c68fcf0c09a'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-8c85f68408244a2db7c04cfc00345e2f'
      AND r.target_model_code = 'MODEL-a62c727c0a024e19a8221c68fcf0c09a'
      AND r.field_code = 'MODEL_a62c727c0a024e19a8221c68fcf0c09a_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-8c85f68408244a2db7c04cfc00345e2f',
  tm.id, 'MODEL-b8251da851514341adef7f6f72995cf3',
  '所属设备管理', 'MODEL_b8251da851514341adef7f6f72995cf3_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-8c85f68408244a2db7c04cfc00345e2f'
  AND tm.code = 'MODEL-b8251da851514341adef7f6f72995cf3'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-8c85f68408244a2db7c04cfc00345e2f'
      AND r.target_model_code = 'MODEL-b8251da851514341adef7f6f72995cf3'
      AND r.field_code = 'MODEL_b8251da851514341adef7f6f72995cf3_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-8c85f68408244a2db7c04cfc00345e2f',
  tm.id, 'MODEL-b83f05dc94f34fb4807458c0a48ba488',
  '所属设备管理', 'MODEL_b83f05dc94f34fb4807458c0a48ba488_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-8c85f68408244a2db7c04cfc00345e2f'
  AND tm.code = 'MODEL-b83f05dc94f34fb4807458c0a48ba488'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-8c85f68408244a2db7c04cfc00345e2f'
      AND r.target_model_code = 'MODEL-b83f05dc94f34fb4807458c0a48ba488'
      AND r.field_code = 'MODEL_b83f05dc94f34fb4807458c0a48ba488_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-8c85f68408244a2db7c04cfc00345e2f',
  tm.id, 'MODEL-bc2cdffeab434f4d862c847fa99938ba',
  '所属设备管理', 'MODEL_bc2cdffeab434f4d862c847fa99938ba_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-8c85f68408244a2db7c04cfc00345e2f'
  AND tm.code = 'MODEL-bc2cdffeab434f4d862c847fa99938ba'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-8c85f68408244a2db7c04cfc00345e2f'
      AND r.target_model_code = 'MODEL-bc2cdffeab434f4d862c847fa99938ba'
      AND r.field_code = 'MODEL_bc2cdffeab434f4d862c847fa99938ba_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-8c85f68408244a2db7c04cfc00345e2f',
  tm.id, 'MODEL-bf41bd50df044f5faf43c4759353ccd1',
  '所属设备管理', 'MODEL_bf41bd50df044f5faf43c4759353ccd1_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-8c85f68408244a2db7c04cfc00345e2f'
  AND tm.code = 'MODEL-bf41bd50df044f5faf43c4759353ccd1'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-8c85f68408244a2db7c04cfc00345e2f'
      AND r.target_model_code = 'MODEL-bf41bd50df044f5faf43c4759353ccd1'
      AND r.field_code = 'MODEL_bf41bd50df044f5faf43c4759353ccd1_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-8c85f68408244a2db7c04cfc00345e2f',
  tm.id, 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9',
  '所属设备管理', 'MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-8c85f68408244a2db7c04cfc00345e2f'
  AND tm.code = 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-8c85f68408244a2db7c04cfc00345e2f'
      AND r.target_model_code = 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9'
      AND r.field_code = 'MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-8c85f68408244a2db7c04cfc00345e2f',
  tm.id, 'MODEL-c6bc32646f3e4bde8409120c5c290eee',
  '所属设备管理', 'MODEL_c6bc32646f3e4bde8409120c5c290eee_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-8c85f68408244a2db7c04cfc00345e2f'
  AND tm.code = 'MODEL-c6bc32646f3e4bde8409120c5c290eee'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-8c85f68408244a2db7c04cfc00345e2f'
      AND r.target_model_code = 'MODEL-c6bc32646f3e4bde8409120c5c290eee'
      AND r.field_code = 'MODEL_c6bc32646f3e4bde8409120c5c290eee_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-8c85f68408244a2db7c04cfc00345e2f',
  tm.id, 'MODEL-d446eef7329f45b4a374347e215fec5d',
  '所属设备管理', 'MODEL_d446eef7329f45b4a374347e215fec5d_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-8c85f68408244a2db7c04cfc00345e2f'
  AND tm.code = 'MODEL-d446eef7329f45b4a374347e215fec5d'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-8c85f68408244a2db7c04cfc00345e2f'
      AND r.target_model_code = 'MODEL-d446eef7329f45b4a374347e215fec5d'
      AND r.field_code = 'MODEL_d446eef7329f45b4a374347e215fec5d_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-8c85f68408244a2db7c04cfc00345e2f',
  tm.id, 'MODEL-e25f3089a99d4878a1ddd326ba0910bf',
  '所属设备管理', 'MODEL_e25f3089a99d4878a1ddd326ba0910bf_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-8c85f68408244a2db7c04cfc00345e2f'
  AND tm.code = 'MODEL-e25f3089a99d4878a1ddd326ba0910bf'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-8c85f68408244a2db7c04cfc00345e2f'
      AND r.target_model_code = 'MODEL-e25f3089a99d4878a1ddd326ba0910bf'
      AND r.field_code = 'MODEL_e25f3089a99d4878a1ddd326ba0910bf_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-8c85f68408244a2db7c04cfc00345e2f',
  tm.id, 'MODEL-e5af81c45bbc44d48374d8036032cc5d',
  '所属设备管理', 'MODEL_e5af81c45bbc44d48374d8036032cc5d_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-8c85f68408244a2db7c04cfc00345e2f'
  AND tm.code = 'MODEL-e5af81c45bbc44d48374d8036032cc5d'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-8c85f68408244a2db7c04cfc00345e2f'
      AND r.target_model_code = 'MODEL-e5af81c45bbc44d48374d8036032cc5d'
      AND r.field_code = 'MODEL_e5af81c45bbc44d48374d8036032cc5d_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-8c85f68408244a2db7c04cfc00345e2f',
  tm.id, 'MODEL-e5c45ff8ca074102829bba872e091bd1',
  '所属设备管理', 'MODEL_e5c45ff8ca074102829bba872e091bd1_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-8c85f68408244a2db7c04cfc00345e2f'
  AND tm.code = 'MODEL-e5c45ff8ca074102829bba872e091bd1'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-8c85f68408244a2db7c04cfc00345e2f'
      AND r.target_model_code = 'MODEL-e5c45ff8ca074102829bba872e091bd1'
      AND r.field_code = 'MODEL_e5c45ff8ca074102829bba872e091bd1_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-8c85f68408244a2db7c04cfc00345e2f',
  tm.id, 'MODEL-f5270d478dd849389e59a178cd512629',
  '所属设备管理', 'MODEL_f5270d478dd849389e59a178cd512629_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-8c85f68408244a2db7c04cfc00345e2f'
  AND tm.code = 'MODEL-f5270d478dd849389e59a178cd512629'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-8c85f68408244a2db7c04cfc00345e2f'
      AND r.target_model_code = 'MODEL-f5270d478dd849389e59a178cd512629'
      AND r.field_code = 'MODEL_f5270d478dd849389e59a178cd512629_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-8c85f68408244a2db7c04cfc00345e2f',
  tm.id, 'MODEL-f55ce56d31f741caa5a69801556834fe',
  '所属设备管理', 'MODEL_f55ce56d31f741caa5a69801556834fe_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-8c85f68408244a2db7c04cfc00345e2f'
  AND tm.code = 'MODEL-f55ce56d31f741caa5a69801556834fe'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-8c85f68408244a2db7c04cfc00345e2f'
      AND r.target_model_code = 'MODEL-f55ce56d31f741caa5a69801556834fe'
      AND r.field_code = 'MODEL_f55ce56d31f741caa5a69801556834fe_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-92b4b6a9bc954beb80c3a4c248a6fc4a',
  tm.id, 'MODEL-17eb9be1c7404734af570ce040f1783b',
  '所属设备管理', 'MODEL_17eb9be1c7404734af570ce040f1783b_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-92b4b6a9bc954beb80c3a4c248a6fc4a'
  AND tm.code = 'MODEL-17eb9be1c7404734af570ce040f1783b'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-92b4b6a9bc954beb80c3a4c248a6fc4a'
      AND r.target_model_code = 'MODEL-17eb9be1c7404734af570ce040f1783b'
      AND r.field_code = 'MODEL_17eb9be1c7404734af570ce040f1783b_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-92b4b6a9bc954beb80c3a4c248a6fc4a',
  tm.id, 'MODEL-26eabac35cb644cc84ae64dfb5ff9ba4',
  '所属设备管理', 'MODEL_26eabac35cb644cc84ae64dfb5ff9ba4_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-92b4b6a9bc954beb80c3a4c248a6fc4a'
  AND tm.code = 'MODEL-26eabac35cb644cc84ae64dfb5ff9ba4'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-92b4b6a9bc954beb80c3a4c248a6fc4a'
      AND r.target_model_code = 'MODEL-26eabac35cb644cc84ae64dfb5ff9ba4'
      AND r.field_code = 'MODEL_26eabac35cb644cc84ae64dfb5ff9ba4_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-92b4b6a9bc954beb80c3a4c248a6fc4a',
  tm.id, 'MODEL-7c4fe9b2d52d41cca5383488a4a400a5',
  '所属设备管理', 'MODEL_7c4fe9b2d52d41cca5383488a4a400a5_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-92b4b6a9bc954beb80c3a4c248a6fc4a'
  AND tm.code = 'MODEL-7c4fe9b2d52d41cca5383488a4a400a5'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-92b4b6a9bc954beb80c3a4c248a6fc4a'
      AND r.target_model_code = 'MODEL-7c4fe9b2d52d41cca5383488a4a400a5'
      AND r.field_code = 'MODEL_7c4fe9b2d52d41cca5383488a4a400a5_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-92b4b6a9bc954beb80c3a4c248a6fc4a',
  tm.id, 'MODEL-88a38000121341e2950132da32baad90',
  '所属设备管理', 'MODEL_88a38000121341e2950132da32baad90_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-92b4b6a9bc954beb80c3a4c248a6fc4a'
  AND tm.code = 'MODEL-88a38000121341e2950132da32baad90'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-92b4b6a9bc954beb80c3a4c248a6fc4a'
      AND r.target_model_code = 'MODEL-88a38000121341e2950132da32baad90'
      AND r.field_code = 'MODEL_88a38000121341e2950132da32baad90_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-92b4b6a9bc954beb80c3a4c248a6fc4a',
  tm.id, 'MODEL-b83f05dc94f34fb4807458c0a48ba488',
  '所属设备管理', 'MODEL_b83f05dc94f34fb4807458c0a48ba488_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-92b4b6a9bc954beb80c3a4c248a6fc4a'
  AND tm.code = 'MODEL-b83f05dc94f34fb4807458c0a48ba488'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-92b4b6a9bc954beb80c3a4c248a6fc4a'
      AND r.target_model_code = 'MODEL-b83f05dc94f34fb4807458c0a48ba488'
      AND r.field_code = 'MODEL_b83f05dc94f34fb4807458c0a48ba488_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-92b4b6a9bc954beb80c3a4c248a6fc4a',
  tm.id, 'MODEL-f55ce56d31f741caa5a69801556834fe',
  '所属设备管理', 'MODEL_f55ce56d31f741caa5a69801556834fe_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-92b4b6a9bc954beb80c3a4c248a6fc4a'
  AND tm.code = 'MODEL-f55ce56d31f741caa5a69801556834fe'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-92b4b6a9bc954beb80c3a4c248a6fc4a'
      AND r.target_model_code = 'MODEL-f55ce56d31f741caa5a69801556834fe'
      AND r.field_code = 'MODEL_f55ce56d31f741caa5a69801556834fe_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-93c3419d952f45a0846a2608a2719b4e',
  tm.id, 'MODEL-05e5c6a6c8fe4f3c9bd1dd86dd24a659',
  '所属设备管理', 'MODEL_05e5c6a6c8fe4f3c9bd1dd86dd24a659_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
  AND tm.code = 'MODEL-05e5c6a6c8fe4f3c9bd1dd86dd24a659'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
      AND r.target_model_code = 'MODEL-05e5c6a6c8fe4f3c9bd1dd86dd24a659'
      AND r.field_code = 'MODEL_05e5c6a6c8fe4f3c9bd1dd86dd24a659_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-93c3419d952f45a0846a2608a2719b4e',
  tm.id, 'MODEL-123328b7bfe745ceb337d286ac64aa17',
  '所属设备管理', 'MODEL_123328b7bfe745ceb337d286ac64aa17_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
  AND tm.code = 'MODEL-123328b7bfe745ceb337d286ac64aa17'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
      AND r.target_model_code = 'MODEL-123328b7bfe745ceb337d286ac64aa17'
      AND r.field_code = 'MODEL_123328b7bfe745ceb337d286ac64aa17_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-93c3419d952f45a0846a2608a2719b4e',
  tm.id, 'MODEL-17eb9be1c7404734af570ce040f1783b',
  '所属设备管理', 'MODEL_17eb9be1c7404734af570ce040f1783b_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
  AND tm.code = 'MODEL-17eb9be1c7404734af570ce040f1783b'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
      AND r.target_model_code = 'MODEL-17eb9be1c7404734af570ce040f1783b'
      AND r.field_code = 'MODEL_17eb9be1c7404734af570ce040f1783b_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-93c3419d952f45a0846a2608a2719b4e',
  tm.id, 'MODEL-26eabac35cb644cc84ae64dfb5ff9ba4',
  '所属设备管理', 'MODEL_26eabac35cb644cc84ae64dfb5ff9ba4_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
  AND tm.code = 'MODEL-26eabac35cb644cc84ae64dfb5ff9ba4'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
      AND r.target_model_code = 'MODEL-26eabac35cb644cc84ae64dfb5ff9ba4'
      AND r.field_code = 'MODEL_26eabac35cb644cc84ae64dfb5ff9ba4_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-93c3419d952f45a0846a2608a2719b4e',
  tm.id, 'MODEL-2af0515a5a36420086980b2322037edb',
  '所属设备管理', 'MODEL_2af0515a5a36420086980b2322037edb_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
  AND tm.code = 'MODEL-2af0515a5a36420086980b2322037edb'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
      AND r.target_model_code = 'MODEL-2af0515a5a36420086980b2322037edb'
      AND r.field_code = 'MODEL_2af0515a5a36420086980b2322037edb_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-93c3419d952f45a0846a2608a2719b4e',
  tm.id, 'MODEL-3130eba3747e4a5aa0862c94807822ba',
  '所属设备管理', 'MODEL_3130eba3747e4a5aa0862c94807822ba_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
  AND tm.code = 'MODEL-3130eba3747e4a5aa0862c94807822ba'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
      AND r.target_model_code = 'MODEL-3130eba3747e4a5aa0862c94807822ba'
      AND r.field_code = 'MODEL_3130eba3747e4a5aa0862c94807822ba_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-93c3419d952f45a0846a2608a2719b4e',
  tm.id, 'MODEL-44cc7511fc224085a51e2887569c933a',
  '所属设备管理', 'MODEL_44cc7511fc224085a51e2887569c933a_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
  AND tm.code = 'MODEL-44cc7511fc224085a51e2887569c933a'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
      AND r.target_model_code = 'MODEL-44cc7511fc224085a51e2887569c933a'
      AND r.field_code = 'MODEL_44cc7511fc224085a51e2887569c933a_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-93c3419d952f45a0846a2608a2719b4e',
  tm.id, 'MODEL-50991eab2a714ed288d00c58ff01c810',
  '所属设备管理', 'MODEL_50991eab2a714ed288d00c58ff01c810_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
  AND tm.code = 'MODEL-50991eab2a714ed288d00c58ff01c810'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
      AND r.target_model_code = 'MODEL-50991eab2a714ed288d00c58ff01c810'
      AND r.field_code = 'MODEL_50991eab2a714ed288d00c58ff01c810_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-93c3419d952f45a0846a2608a2719b4e',
  tm.id, 'MODEL-5a9b87c42571486998d363101c54ec8c',
  '所属设备管理', 'MODEL_5a9b87c42571486998d363101c54ec8c_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
  AND tm.code = 'MODEL-5a9b87c42571486998d363101c54ec8c'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
      AND r.target_model_code = 'MODEL-5a9b87c42571486998d363101c54ec8c'
      AND r.field_code = 'MODEL_5a9b87c42571486998d363101c54ec8c_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-93c3419d952f45a0846a2608a2719b4e',
  tm.id, 'MODEL-5f6f9243c7bb4e23bfed2fc01b48cf06',
  '所属设备管理', 'MODEL_5f6f9243c7bb4e23bfed2fc01b48cf06_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
  AND tm.code = 'MODEL-5f6f9243c7bb4e23bfed2fc01b48cf06'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
      AND r.target_model_code = 'MODEL-5f6f9243c7bb4e23bfed2fc01b48cf06'
      AND r.field_code = 'MODEL_5f6f9243c7bb4e23bfed2fc01b48cf06_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-93c3419d952f45a0846a2608a2719b4e',
  tm.id, 'MODEL-68554821800248cfacacbac1a53cdf37',
  '所属设备管理', 'MODEL_68554821800248cfacacbac1a53cdf37_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
  AND tm.code = 'MODEL-68554821800248cfacacbac1a53cdf37'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
      AND r.target_model_code = 'MODEL-68554821800248cfacacbac1a53cdf37'
      AND r.field_code = 'MODEL_68554821800248cfacacbac1a53cdf37_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-93c3419d952f45a0846a2608a2719b4e',
  tm.id, 'MODEL-6fd43ab74f6f425f81f0ad547603a849',
  '所属设备管理', 'MODEL_6fd43ab74f6f425f81f0ad547603a849_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
  AND tm.code = 'MODEL-6fd43ab74f6f425f81f0ad547603a849'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
      AND r.target_model_code = 'MODEL-6fd43ab74f6f425f81f0ad547603a849'
      AND r.field_code = 'MODEL_6fd43ab74f6f425f81f0ad547603a849_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-93c3419d952f45a0846a2608a2719b4e',
  tm.id, 'MODEL-70d2b066120c4b969f0b661d1d2fb664',
  '所属设备管理', 'MODEL_70d2b066120c4b969f0b661d1d2fb664_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
  AND tm.code = 'MODEL-70d2b066120c4b969f0b661d1d2fb664'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
      AND r.target_model_code = 'MODEL-70d2b066120c4b969f0b661d1d2fb664'
      AND r.field_code = 'MODEL_70d2b066120c4b969f0b661d1d2fb664_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-93c3419d952f45a0846a2608a2719b4e',
  tm.id, 'MODEL-7c4fe9b2d52d41cca5383488a4a400a5',
  '所属设备管理', 'MODEL_7c4fe9b2d52d41cca5383488a4a400a5_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
  AND tm.code = 'MODEL-7c4fe9b2d52d41cca5383488a4a400a5'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
      AND r.target_model_code = 'MODEL-7c4fe9b2d52d41cca5383488a4a400a5'
      AND r.field_code = 'MODEL_7c4fe9b2d52d41cca5383488a4a400a5_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-93c3419d952f45a0846a2608a2719b4e',
  tm.id, 'MODEL-7c746a60520347baaf64334625f07f31',
  '所属设备管理', 'MODEL_7c746a60520347baaf64334625f07f31_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
  AND tm.code = 'MODEL-7c746a60520347baaf64334625f07f31'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
      AND r.target_model_code = 'MODEL-7c746a60520347baaf64334625f07f31'
      AND r.field_code = 'MODEL_7c746a60520347baaf64334625f07f31_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-93c3419d952f45a0846a2608a2719b4e',
  tm.id, 'MODEL-81132ce00d2041c6a4ee06ec1c01dac8',
  '所属设备管理', 'MODEL_81132ce00d2041c6a4ee06ec1c01dac8_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
  AND tm.code = 'MODEL-81132ce00d2041c6a4ee06ec1c01dac8'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
      AND r.target_model_code = 'MODEL-81132ce00d2041c6a4ee06ec1c01dac8'
      AND r.field_code = 'MODEL_81132ce00d2041c6a4ee06ec1c01dac8_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-93c3419d952f45a0846a2608a2719b4e',
  tm.id, 'MODEL-88a38000121341e2950132da32baad90',
  '所属设备管理', 'MODEL_88a38000121341e2950132da32baad90_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
  AND tm.code = 'MODEL-88a38000121341e2950132da32baad90'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
      AND r.target_model_code = 'MODEL-88a38000121341e2950132da32baad90'
      AND r.field_code = 'MODEL_88a38000121341e2950132da32baad90_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-93c3419d952f45a0846a2608a2719b4e',
  tm.id, 'MODEL-990370e140aa481a82158bc96dc0b006',
  '所属设备管理', 'MODEL_990370e140aa481a82158bc96dc0b006_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
  AND tm.code = 'MODEL-990370e140aa481a82158bc96dc0b006'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
      AND r.target_model_code = 'MODEL-990370e140aa481a82158bc96dc0b006'
      AND r.field_code = 'MODEL_990370e140aa481a82158bc96dc0b006_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-93c3419d952f45a0846a2608a2719b4e',
  tm.id, 'MODEL-a4ce7668efe94d2fbc47a658be1a35d2',
  '所属设备管理', 'MODEL_a4ce7668efe94d2fbc47a658be1a35d2_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
  AND tm.code = 'MODEL-a4ce7668efe94d2fbc47a658be1a35d2'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
      AND r.target_model_code = 'MODEL-a4ce7668efe94d2fbc47a658be1a35d2'
      AND r.field_code = 'MODEL_a4ce7668efe94d2fbc47a658be1a35d2_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-93c3419d952f45a0846a2608a2719b4e',
  tm.id, 'MODEL-a62c727c0a024e19a8221c68fcf0c09a',
  '所属设备管理', 'MODEL_a62c727c0a024e19a8221c68fcf0c09a_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
  AND tm.code = 'MODEL-a62c727c0a024e19a8221c68fcf0c09a'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
      AND r.target_model_code = 'MODEL-a62c727c0a024e19a8221c68fcf0c09a'
      AND r.field_code = 'MODEL_a62c727c0a024e19a8221c68fcf0c09a_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-93c3419d952f45a0846a2608a2719b4e',
  tm.id, 'MODEL-b8251da851514341adef7f6f72995cf3',
  '所属设备管理', 'MODEL_b8251da851514341adef7f6f72995cf3_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
  AND tm.code = 'MODEL-b8251da851514341adef7f6f72995cf3'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
      AND r.target_model_code = 'MODEL-b8251da851514341adef7f6f72995cf3'
      AND r.field_code = 'MODEL_b8251da851514341adef7f6f72995cf3_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-93c3419d952f45a0846a2608a2719b4e',
  tm.id, 'MODEL-b83f05dc94f34fb4807458c0a48ba488',
  '所属设备管理', 'MODEL_b83f05dc94f34fb4807458c0a48ba488_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
  AND tm.code = 'MODEL-b83f05dc94f34fb4807458c0a48ba488'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
      AND r.target_model_code = 'MODEL-b83f05dc94f34fb4807458c0a48ba488'
      AND r.field_code = 'MODEL_b83f05dc94f34fb4807458c0a48ba488_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-93c3419d952f45a0846a2608a2719b4e',
  tm.id, 'MODEL-bc2cdffeab434f4d862c847fa99938ba',
  '所属设备管理', 'MODEL_bc2cdffeab434f4d862c847fa99938ba_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
  AND tm.code = 'MODEL-bc2cdffeab434f4d862c847fa99938ba'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
      AND r.target_model_code = 'MODEL-bc2cdffeab434f4d862c847fa99938ba'
      AND r.field_code = 'MODEL_bc2cdffeab434f4d862c847fa99938ba_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-93c3419d952f45a0846a2608a2719b4e',
  tm.id, 'MODEL-bf41bd50df044f5faf43c4759353ccd1',
  '所属设备管理', 'MODEL_bf41bd50df044f5faf43c4759353ccd1_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
  AND tm.code = 'MODEL-bf41bd50df044f5faf43c4759353ccd1'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
      AND r.target_model_code = 'MODEL-bf41bd50df044f5faf43c4759353ccd1'
      AND r.field_code = 'MODEL_bf41bd50df044f5faf43c4759353ccd1_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-93c3419d952f45a0846a2608a2719b4e',
  tm.id, 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9',
  '所属设备管理', 'MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
  AND tm.code = 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
      AND r.target_model_code = 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9'
      AND r.field_code = 'MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-93c3419d952f45a0846a2608a2719b4e',
  tm.id, 'MODEL-c6bc32646f3e4bde8409120c5c290eee',
  '所属设备管理', 'MODEL_c6bc32646f3e4bde8409120c5c290eee_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
  AND tm.code = 'MODEL-c6bc32646f3e4bde8409120c5c290eee'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
      AND r.target_model_code = 'MODEL-c6bc32646f3e4bde8409120c5c290eee'
      AND r.field_code = 'MODEL_c6bc32646f3e4bde8409120c5c290eee_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-93c3419d952f45a0846a2608a2719b4e',
  tm.id, 'MODEL-d446eef7329f45b4a374347e215fec5d',
  '所属设备管理', 'MODEL_d446eef7329f45b4a374347e215fec5d_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
  AND tm.code = 'MODEL-d446eef7329f45b4a374347e215fec5d'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
      AND r.target_model_code = 'MODEL-d446eef7329f45b4a374347e215fec5d'
      AND r.field_code = 'MODEL_d446eef7329f45b4a374347e215fec5d_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-93c3419d952f45a0846a2608a2719b4e',
  tm.id, 'MODEL-e25f3089a99d4878a1ddd326ba0910bf',
  '所属设备管理', 'MODEL_e25f3089a99d4878a1ddd326ba0910bf_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
  AND tm.code = 'MODEL-e25f3089a99d4878a1ddd326ba0910bf'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
      AND r.target_model_code = 'MODEL-e25f3089a99d4878a1ddd326ba0910bf'
      AND r.field_code = 'MODEL_e25f3089a99d4878a1ddd326ba0910bf_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-93c3419d952f45a0846a2608a2719b4e',
  tm.id, 'MODEL-e5af81c45bbc44d48374d8036032cc5d',
  '所属设备管理', 'MODEL_e5af81c45bbc44d48374d8036032cc5d_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
  AND tm.code = 'MODEL-e5af81c45bbc44d48374d8036032cc5d'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
      AND r.target_model_code = 'MODEL-e5af81c45bbc44d48374d8036032cc5d'
      AND r.field_code = 'MODEL_e5af81c45bbc44d48374d8036032cc5d_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-93c3419d952f45a0846a2608a2719b4e',
  tm.id, 'MODEL-e5c45ff8ca074102829bba872e091bd1',
  '所属设备管理', 'MODEL_e5c45ff8ca074102829bba872e091bd1_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
  AND tm.code = 'MODEL-e5c45ff8ca074102829bba872e091bd1'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
      AND r.target_model_code = 'MODEL-e5c45ff8ca074102829bba872e091bd1'
      AND r.field_code = 'MODEL_e5c45ff8ca074102829bba872e091bd1_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-93c3419d952f45a0846a2608a2719b4e',
  tm.id, 'MODEL-f5270d478dd849389e59a178cd512629',
  '所属设备管理', 'MODEL_f5270d478dd849389e59a178cd512629_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
  AND tm.code = 'MODEL-f5270d478dd849389e59a178cd512629'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
      AND r.target_model_code = 'MODEL-f5270d478dd849389e59a178cd512629'
      AND r.field_code = 'MODEL_f5270d478dd849389e59a178cd512629_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-93c3419d952f45a0846a2608a2719b4e',
  tm.id, 'MODEL-f55ce56d31f741caa5a69801556834fe',
  '所属设备管理', 'MODEL_f55ce56d31f741caa5a69801556834fe_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
  AND tm.code = 'MODEL-f55ce56d31f741caa5a69801556834fe'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
      AND r.target_model_code = 'MODEL-f55ce56d31f741caa5a69801556834fe'
      AND r.field_code = 'MODEL_f55ce56d31f741caa5a69801556834fe_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-9ce1710bf842479db96b2e355746d289',
  tm.id, 'MODEL-05e5c6a6c8fe4f3c9bd1dd86dd24a659',
  '所属设备管理', 'MODEL_05e5c6a6c8fe4f3c9bd1dd86dd24a659_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-9ce1710bf842479db96b2e355746d289'
  AND tm.code = 'MODEL-05e5c6a6c8fe4f3c9bd1dd86dd24a659'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-9ce1710bf842479db96b2e355746d289'
      AND r.target_model_code = 'MODEL-05e5c6a6c8fe4f3c9bd1dd86dd24a659'
      AND r.field_code = 'MODEL_05e5c6a6c8fe4f3c9bd1dd86dd24a659_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-9ce1710bf842479db96b2e355746d289',
  tm.id, 'MODEL-123328b7bfe745ceb337d286ac64aa17',
  '所属设备管理', 'MODEL_123328b7bfe745ceb337d286ac64aa17_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-9ce1710bf842479db96b2e355746d289'
  AND tm.code = 'MODEL-123328b7bfe745ceb337d286ac64aa17'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-9ce1710bf842479db96b2e355746d289'
      AND r.target_model_code = 'MODEL-123328b7bfe745ceb337d286ac64aa17'
      AND r.field_code = 'MODEL_123328b7bfe745ceb337d286ac64aa17_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-9ce1710bf842479db96b2e355746d289',
  tm.id, 'MODEL-17eb9be1c7404734af570ce040f1783b',
  '所属设备管理', 'MODEL_17eb9be1c7404734af570ce040f1783b_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-9ce1710bf842479db96b2e355746d289'
  AND tm.code = 'MODEL-17eb9be1c7404734af570ce040f1783b'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-9ce1710bf842479db96b2e355746d289'
      AND r.target_model_code = 'MODEL-17eb9be1c7404734af570ce040f1783b'
      AND r.field_code = 'MODEL_17eb9be1c7404734af570ce040f1783b_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-9ce1710bf842479db96b2e355746d289',
  tm.id, 'MODEL-26eabac35cb644cc84ae64dfb5ff9ba4',
  '所属设备管理', 'MODEL_26eabac35cb644cc84ae64dfb5ff9ba4_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-9ce1710bf842479db96b2e355746d289'
  AND tm.code = 'MODEL-26eabac35cb644cc84ae64dfb5ff9ba4'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-9ce1710bf842479db96b2e355746d289'
      AND r.target_model_code = 'MODEL-26eabac35cb644cc84ae64dfb5ff9ba4'
      AND r.field_code = 'MODEL_26eabac35cb644cc84ae64dfb5ff9ba4_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-9ce1710bf842479db96b2e355746d289',
  tm.id, 'MODEL-2af0515a5a36420086980b2322037edb',
  '所属设备管理', 'MODEL_2af0515a5a36420086980b2322037edb_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-9ce1710bf842479db96b2e355746d289'
  AND tm.code = 'MODEL-2af0515a5a36420086980b2322037edb'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-9ce1710bf842479db96b2e355746d289'
      AND r.target_model_code = 'MODEL-2af0515a5a36420086980b2322037edb'
      AND r.field_code = 'MODEL_2af0515a5a36420086980b2322037edb_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-9ce1710bf842479db96b2e355746d289',
  tm.id, 'MODEL-3130eba3747e4a5aa0862c94807822ba',
  '所属设备管理', 'MODEL_3130eba3747e4a5aa0862c94807822ba_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-9ce1710bf842479db96b2e355746d289'
  AND tm.code = 'MODEL-3130eba3747e4a5aa0862c94807822ba'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-9ce1710bf842479db96b2e355746d289'
      AND r.target_model_code = 'MODEL-3130eba3747e4a5aa0862c94807822ba'
      AND r.field_code = 'MODEL_3130eba3747e4a5aa0862c94807822ba_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-9ce1710bf842479db96b2e355746d289',
  tm.id, 'MODEL-44cc7511fc224085a51e2887569c933a',
  '所属设备管理', 'MODEL_44cc7511fc224085a51e2887569c933a_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-9ce1710bf842479db96b2e355746d289'
  AND tm.code = 'MODEL-44cc7511fc224085a51e2887569c933a'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-9ce1710bf842479db96b2e355746d289'
      AND r.target_model_code = 'MODEL-44cc7511fc224085a51e2887569c933a'
      AND r.field_code = 'MODEL_44cc7511fc224085a51e2887569c933a_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-9ce1710bf842479db96b2e355746d289',
  tm.id, 'MODEL-50991eab2a714ed288d00c58ff01c810',
  '所属设备管理', 'MODEL_50991eab2a714ed288d00c58ff01c810_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-9ce1710bf842479db96b2e355746d289'
  AND tm.code = 'MODEL-50991eab2a714ed288d00c58ff01c810'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-9ce1710bf842479db96b2e355746d289'
      AND r.target_model_code = 'MODEL-50991eab2a714ed288d00c58ff01c810'
      AND r.field_code = 'MODEL_50991eab2a714ed288d00c58ff01c810_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-9ce1710bf842479db96b2e355746d289',
  tm.id, 'MODEL-5a9b87c42571486998d363101c54ec8c',
  '所属设备管理', 'MODEL_5a9b87c42571486998d363101c54ec8c_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-9ce1710bf842479db96b2e355746d289'
  AND tm.code = 'MODEL-5a9b87c42571486998d363101c54ec8c'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-9ce1710bf842479db96b2e355746d289'
      AND r.target_model_code = 'MODEL-5a9b87c42571486998d363101c54ec8c'
      AND r.field_code = 'MODEL_5a9b87c42571486998d363101c54ec8c_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-9ce1710bf842479db96b2e355746d289',
  tm.id, 'MODEL-5f6f9243c7bb4e23bfed2fc01b48cf06',
  '所属设备管理', 'MODEL_5f6f9243c7bb4e23bfed2fc01b48cf06_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-9ce1710bf842479db96b2e355746d289'
  AND tm.code = 'MODEL-5f6f9243c7bb4e23bfed2fc01b48cf06'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-9ce1710bf842479db96b2e355746d289'
      AND r.target_model_code = 'MODEL-5f6f9243c7bb4e23bfed2fc01b48cf06'
      AND r.field_code = 'MODEL_5f6f9243c7bb4e23bfed2fc01b48cf06_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-9ce1710bf842479db96b2e355746d289',
  tm.id, 'MODEL-68554821800248cfacacbac1a53cdf37',
  '所属设备管理', 'MODEL_68554821800248cfacacbac1a53cdf37_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-9ce1710bf842479db96b2e355746d289'
  AND tm.code = 'MODEL-68554821800248cfacacbac1a53cdf37'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-9ce1710bf842479db96b2e355746d289'
      AND r.target_model_code = 'MODEL-68554821800248cfacacbac1a53cdf37'
      AND r.field_code = 'MODEL_68554821800248cfacacbac1a53cdf37_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-9ce1710bf842479db96b2e355746d289',
  tm.id, 'MODEL-6fd43ab74f6f425f81f0ad547603a849',
  '所属设备管理', 'MODEL_6fd43ab74f6f425f81f0ad547603a849_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-9ce1710bf842479db96b2e355746d289'
  AND tm.code = 'MODEL-6fd43ab74f6f425f81f0ad547603a849'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-9ce1710bf842479db96b2e355746d289'
      AND r.target_model_code = 'MODEL-6fd43ab74f6f425f81f0ad547603a849'
      AND r.field_code = 'MODEL_6fd43ab74f6f425f81f0ad547603a849_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-9ce1710bf842479db96b2e355746d289',
  tm.id, 'MODEL-70d2b066120c4b969f0b661d1d2fb664',
  '所属设备管理', 'MODEL_70d2b066120c4b969f0b661d1d2fb664_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-9ce1710bf842479db96b2e355746d289'
  AND tm.code = 'MODEL-70d2b066120c4b969f0b661d1d2fb664'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-9ce1710bf842479db96b2e355746d289'
      AND r.target_model_code = 'MODEL-70d2b066120c4b969f0b661d1d2fb664'
      AND r.field_code = 'MODEL_70d2b066120c4b969f0b661d1d2fb664_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-9ce1710bf842479db96b2e355746d289',
  tm.id, 'MODEL-7c4fe9b2d52d41cca5383488a4a400a5',
  '所属设备管理', 'MODEL_7c4fe9b2d52d41cca5383488a4a400a5_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-9ce1710bf842479db96b2e355746d289'
  AND tm.code = 'MODEL-7c4fe9b2d52d41cca5383488a4a400a5'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-9ce1710bf842479db96b2e355746d289'
      AND r.target_model_code = 'MODEL-7c4fe9b2d52d41cca5383488a4a400a5'
      AND r.field_code = 'MODEL_7c4fe9b2d52d41cca5383488a4a400a5_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-9ce1710bf842479db96b2e355746d289',
  tm.id, 'MODEL-7c746a60520347baaf64334625f07f31',
  '所属设备管理', 'MODEL_7c746a60520347baaf64334625f07f31_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-9ce1710bf842479db96b2e355746d289'
  AND tm.code = 'MODEL-7c746a60520347baaf64334625f07f31'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-9ce1710bf842479db96b2e355746d289'
      AND r.target_model_code = 'MODEL-7c746a60520347baaf64334625f07f31'
      AND r.field_code = 'MODEL_7c746a60520347baaf64334625f07f31_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-9ce1710bf842479db96b2e355746d289',
  tm.id, 'MODEL-81132ce00d2041c6a4ee06ec1c01dac8',
  '所属设备管理', 'MODEL_81132ce00d2041c6a4ee06ec1c01dac8_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-9ce1710bf842479db96b2e355746d289'
  AND tm.code = 'MODEL-81132ce00d2041c6a4ee06ec1c01dac8'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-9ce1710bf842479db96b2e355746d289'
      AND r.target_model_code = 'MODEL-81132ce00d2041c6a4ee06ec1c01dac8'
      AND r.field_code = 'MODEL_81132ce00d2041c6a4ee06ec1c01dac8_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-9ce1710bf842479db96b2e355746d289',
  tm.id, 'MODEL-88a38000121341e2950132da32baad90',
  '所属设备管理', 'MODEL_88a38000121341e2950132da32baad90_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-9ce1710bf842479db96b2e355746d289'
  AND tm.code = 'MODEL-88a38000121341e2950132da32baad90'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-9ce1710bf842479db96b2e355746d289'
      AND r.target_model_code = 'MODEL-88a38000121341e2950132da32baad90'
      AND r.field_code = 'MODEL_88a38000121341e2950132da32baad90_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-9ce1710bf842479db96b2e355746d289',
  tm.id, 'MODEL-990370e140aa481a82158bc96dc0b006',
  '所属设备管理', 'MODEL_990370e140aa481a82158bc96dc0b006_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-9ce1710bf842479db96b2e355746d289'
  AND tm.code = 'MODEL-990370e140aa481a82158bc96dc0b006'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-9ce1710bf842479db96b2e355746d289'
      AND r.target_model_code = 'MODEL-990370e140aa481a82158bc96dc0b006'
      AND r.field_code = 'MODEL_990370e140aa481a82158bc96dc0b006_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-9ce1710bf842479db96b2e355746d289',
  tm.id, 'MODEL-a4ce7668efe94d2fbc47a658be1a35d2',
  '所属设备管理', 'MODEL_a4ce7668efe94d2fbc47a658be1a35d2_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-9ce1710bf842479db96b2e355746d289'
  AND tm.code = 'MODEL-a4ce7668efe94d2fbc47a658be1a35d2'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-9ce1710bf842479db96b2e355746d289'
      AND r.target_model_code = 'MODEL-a4ce7668efe94d2fbc47a658be1a35d2'
      AND r.field_code = 'MODEL_a4ce7668efe94d2fbc47a658be1a35d2_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-9ce1710bf842479db96b2e355746d289',
  tm.id, 'MODEL-a62c727c0a024e19a8221c68fcf0c09a',
  '所属设备管理', 'MODEL_a62c727c0a024e19a8221c68fcf0c09a_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-9ce1710bf842479db96b2e355746d289'
  AND tm.code = 'MODEL-a62c727c0a024e19a8221c68fcf0c09a'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-9ce1710bf842479db96b2e355746d289'
      AND r.target_model_code = 'MODEL-a62c727c0a024e19a8221c68fcf0c09a'
      AND r.field_code = 'MODEL_a62c727c0a024e19a8221c68fcf0c09a_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-9ce1710bf842479db96b2e355746d289',
  tm.id, 'MODEL-b8251da851514341adef7f6f72995cf3',
  '所属设备管理', 'MODEL_b8251da851514341adef7f6f72995cf3_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-9ce1710bf842479db96b2e355746d289'
  AND tm.code = 'MODEL-b8251da851514341adef7f6f72995cf3'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-9ce1710bf842479db96b2e355746d289'
      AND r.target_model_code = 'MODEL-b8251da851514341adef7f6f72995cf3'
      AND r.field_code = 'MODEL_b8251da851514341adef7f6f72995cf3_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-9ce1710bf842479db96b2e355746d289',
  tm.id, 'MODEL-b83f05dc94f34fb4807458c0a48ba488',
  '所属设备管理', 'MODEL_b83f05dc94f34fb4807458c0a48ba488_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-9ce1710bf842479db96b2e355746d289'
  AND tm.code = 'MODEL-b83f05dc94f34fb4807458c0a48ba488'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-9ce1710bf842479db96b2e355746d289'
      AND r.target_model_code = 'MODEL-b83f05dc94f34fb4807458c0a48ba488'
      AND r.field_code = 'MODEL_b83f05dc94f34fb4807458c0a48ba488_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-9ce1710bf842479db96b2e355746d289',
  tm.id, 'MODEL-bc2cdffeab434f4d862c847fa99938ba',
  '所属设备管理', 'MODEL_bc2cdffeab434f4d862c847fa99938ba_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-9ce1710bf842479db96b2e355746d289'
  AND tm.code = 'MODEL-bc2cdffeab434f4d862c847fa99938ba'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-9ce1710bf842479db96b2e355746d289'
      AND r.target_model_code = 'MODEL-bc2cdffeab434f4d862c847fa99938ba'
      AND r.field_code = 'MODEL_bc2cdffeab434f4d862c847fa99938ba_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-9ce1710bf842479db96b2e355746d289',
  tm.id, 'MODEL-bf41bd50df044f5faf43c4759353ccd1',
  '所属设备管理', 'MODEL_bf41bd50df044f5faf43c4759353ccd1_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-9ce1710bf842479db96b2e355746d289'
  AND tm.code = 'MODEL-bf41bd50df044f5faf43c4759353ccd1'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-9ce1710bf842479db96b2e355746d289'
      AND r.target_model_code = 'MODEL-bf41bd50df044f5faf43c4759353ccd1'
      AND r.field_code = 'MODEL_bf41bd50df044f5faf43c4759353ccd1_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-9ce1710bf842479db96b2e355746d289',
  tm.id, 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9',
  '所属设备管理', 'MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-9ce1710bf842479db96b2e355746d289'
  AND tm.code = 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-9ce1710bf842479db96b2e355746d289'
      AND r.target_model_code = 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9'
      AND r.field_code = 'MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-9ce1710bf842479db96b2e355746d289',
  tm.id, 'MODEL-c6bc32646f3e4bde8409120c5c290eee',
  '所属设备管理', 'MODEL_c6bc32646f3e4bde8409120c5c290eee_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-9ce1710bf842479db96b2e355746d289'
  AND tm.code = 'MODEL-c6bc32646f3e4bde8409120c5c290eee'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-9ce1710bf842479db96b2e355746d289'
      AND r.target_model_code = 'MODEL-c6bc32646f3e4bde8409120c5c290eee'
      AND r.field_code = 'MODEL_c6bc32646f3e4bde8409120c5c290eee_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-9ce1710bf842479db96b2e355746d289',
  tm.id, 'MODEL-d446eef7329f45b4a374347e215fec5d',
  '所属设备管理', 'MODEL_d446eef7329f45b4a374347e215fec5d_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-9ce1710bf842479db96b2e355746d289'
  AND tm.code = 'MODEL-d446eef7329f45b4a374347e215fec5d'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-9ce1710bf842479db96b2e355746d289'
      AND r.target_model_code = 'MODEL-d446eef7329f45b4a374347e215fec5d'
      AND r.field_code = 'MODEL_d446eef7329f45b4a374347e215fec5d_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-9ce1710bf842479db96b2e355746d289',
  tm.id, 'MODEL-e25f3089a99d4878a1ddd326ba0910bf',
  '所属设备管理', 'MODEL_e25f3089a99d4878a1ddd326ba0910bf_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-9ce1710bf842479db96b2e355746d289'
  AND tm.code = 'MODEL-e25f3089a99d4878a1ddd326ba0910bf'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-9ce1710bf842479db96b2e355746d289'
      AND r.target_model_code = 'MODEL-e25f3089a99d4878a1ddd326ba0910bf'
      AND r.field_code = 'MODEL_e25f3089a99d4878a1ddd326ba0910bf_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-9ce1710bf842479db96b2e355746d289',
  tm.id, 'MODEL-e5af81c45bbc44d48374d8036032cc5d',
  '所属设备管理', 'MODEL_e5af81c45bbc44d48374d8036032cc5d_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-9ce1710bf842479db96b2e355746d289'
  AND tm.code = 'MODEL-e5af81c45bbc44d48374d8036032cc5d'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-9ce1710bf842479db96b2e355746d289'
      AND r.target_model_code = 'MODEL-e5af81c45bbc44d48374d8036032cc5d'
      AND r.field_code = 'MODEL_e5af81c45bbc44d48374d8036032cc5d_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-9ce1710bf842479db96b2e355746d289',
  tm.id, 'MODEL-e5c45ff8ca074102829bba872e091bd1',
  '所属设备管理', 'MODEL_e5c45ff8ca074102829bba872e091bd1_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-9ce1710bf842479db96b2e355746d289'
  AND tm.code = 'MODEL-e5c45ff8ca074102829bba872e091bd1'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-9ce1710bf842479db96b2e355746d289'
      AND r.target_model_code = 'MODEL-e5c45ff8ca074102829bba872e091bd1'
      AND r.field_code = 'MODEL_e5c45ff8ca074102829bba872e091bd1_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-9ce1710bf842479db96b2e355746d289',
  tm.id, 'MODEL-f5270d478dd849389e59a178cd512629',
  '所属设备管理', 'MODEL_f5270d478dd849389e59a178cd512629_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-9ce1710bf842479db96b2e355746d289'
  AND tm.code = 'MODEL-f5270d478dd849389e59a178cd512629'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-9ce1710bf842479db96b2e355746d289'
      AND r.target_model_code = 'MODEL-f5270d478dd849389e59a178cd512629'
      AND r.field_code = 'MODEL_f5270d478dd849389e59a178cd512629_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-9ce1710bf842479db96b2e355746d289',
  tm.id, 'MODEL-f55ce56d31f741caa5a69801556834fe',
  '所属设备管理', 'MODEL_f55ce56d31f741caa5a69801556834fe_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-9ce1710bf842479db96b2e355746d289'
  AND tm.code = 'MODEL-f55ce56d31f741caa5a69801556834fe'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-9ce1710bf842479db96b2e355746d289'
      AND r.target_model_code = 'MODEL-f55ce56d31f741caa5a69801556834fe'
      AND r.field_code = 'MODEL_f55ce56d31f741caa5a69801556834fe_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-9f00c2ed9ca74a888364669af37523b0',
  tm.id, 'MODEL-17eb9be1c7404734af570ce040f1783b',
  '所属设备管理', 'MODEL_17eb9be1c7404734af570ce040f1783b_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-9f00c2ed9ca74a888364669af37523b0'
  AND tm.code = 'MODEL-17eb9be1c7404734af570ce040f1783b'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-9f00c2ed9ca74a888364669af37523b0'
      AND r.target_model_code = 'MODEL-17eb9be1c7404734af570ce040f1783b'
      AND r.field_code = 'MODEL_17eb9be1c7404734af570ce040f1783b_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-9f00c2ed9ca74a888364669af37523b0',
  tm.id, 'MODEL-26eabac35cb644cc84ae64dfb5ff9ba4',
  '所属设备管理', 'MODEL_26eabac35cb644cc84ae64dfb5ff9ba4_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-9f00c2ed9ca74a888364669af37523b0'
  AND tm.code = 'MODEL-26eabac35cb644cc84ae64dfb5ff9ba4'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-9f00c2ed9ca74a888364669af37523b0'
      AND r.target_model_code = 'MODEL-26eabac35cb644cc84ae64dfb5ff9ba4'
      AND r.field_code = 'MODEL_26eabac35cb644cc84ae64dfb5ff9ba4_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-9f00c2ed9ca74a888364669af37523b0',
  tm.id, 'MODEL-7c4fe9b2d52d41cca5383488a4a400a5',
  '所属设备管理', 'MODEL_7c4fe9b2d52d41cca5383488a4a400a5_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-9f00c2ed9ca74a888364669af37523b0'
  AND tm.code = 'MODEL-7c4fe9b2d52d41cca5383488a4a400a5'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-9f00c2ed9ca74a888364669af37523b0'
      AND r.target_model_code = 'MODEL-7c4fe9b2d52d41cca5383488a4a400a5'
      AND r.field_code = 'MODEL_7c4fe9b2d52d41cca5383488a4a400a5_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-9f00c2ed9ca74a888364669af37523b0',
  tm.id, 'MODEL-88a38000121341e2950132da32baad90',
  '所属设备管理', 'MODEL_88a38000121341e2950132da32baad90_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-9f00c2ed9ca74a888364669af37523b0'
  AND tm.code = 'MODEL-88a38000121341e2950132da32baad90'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-9f00c2ed9ca74a888364669af37523b0'
      AND r.target_model_code = 'MODEL-88a38000121341e2950132da32baad90'
      AND r.field_code = 'MODEL_88a38000121341e2950132da32baad90_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-9f00c2ed9ca74a888364669af37523b0',
  tm.id, 'MODEL-b83f05dc94f34fb4807458c0a48ba488',
  '所属设备管理', 'MODEL_b83f05dc94f34fb4807458c0a48ba488_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-9f00c2ed9ca74a888364669af37523b0'
  AND tm.code = 'MODEL-b83f05dc94f34fb4807458c0a48ba488'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-9f00c2ed9ca74a888364669af37523b0'
      AND r.target_model_code = 'MODEL-b83f05dc94f34fb4807458c0a48ba488'
      AND r.field_code = 'MODEL_b83f05dc94f34fb4807458c0a48ba488_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-9f00c2ed9ca74a888364669af37523b0',
  tm.id, 'MODEL-f55ce56d31f741caa5a69801556834fe',
  '所属设备管理', 'MODEL_f55ce56d31f741caa5a69801556834fe_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-9f00c2ed9ca74a888364669af37523b0'
  AND tm.code = 'MODEL-f55ce56d31f741caa5a69801556834fe'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-9f00c2ed9ca74a888364669af37523b0'
      AND r.target_model_code = 'MODEL-f55ce56d31f741caa5a69801556834fe'
      AND r.field_code = 'MODEL_f55ce56d31f741caa5a69801556834fe_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7',
  tm.id, 'MODEL-05e5c6a6c8fe4f3c9bd1dd86dd24a659',
  '所属设备管理', 'MODEL_05e5c6a6c8fe4f3c9bd1dd86dd24a659_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7'
  AND tm.code = 'MODEL-05e5c6a6c8fe4f3c9bd1dd86dd24a659'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7'
      AND r.target_model_code = 'MODEL-05e5c6a6c8fe4f3c9bd1dd86dd24a659'
      AND r.field_code = 'MODEL_05e5c6a6c8fe4f3c9bd1dd86dd24a659_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7',
  tm.id, 'MODEL-123328b7bfe745ceb337d286ac64aa17',
  '所属设备管理', 'MODEL_123328b7bfe745ceb337d286ac64aa17_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7'
  AND tm.code = 'MODEL-123328b7bfe745ceb337d286ac64aa17'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7'
      AND r.target_model_code = 'MODEL-123328b7bfe745ceb337d286ac64aa17'
      AND r.field_code = 'MODEL_123328b7bfe745ceb337d286ac64aa17_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7',
  tm.id, 'MODEL-17eb9be1c7404734af570ce040f1783b',
  '所属设备管理', 'MODEL_17eb9be1c7404734af570ce040f1783b_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7'
  AND tm.code = 'MODEL-17eb9be1c7404734af570ce040f1783b'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7'
      AND r.target_model_code = 'MODEL-17eb9be1c7404734af570ce040f1783b'
      AND r.field_code = 'MODEL_17eb9be1c7404734af570ce040f1783b_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7',
  tm.id, 'MODEL-26eabac35cb644cc84ae64dfb5ff9ba4',
  '所属设备管理', 'MODEL_26eabac35cb644cc84ae64dfb5ff9ba4_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7'
  AND tm.code = 'MODEL-26eabac35cb644cc84ae64dfb5ff9ba4'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7'
      AND r.target_model_code = 'MODEL-26eabac35cb644cc84ae64dfb5ff9ba4'
      AND r.field_code = 'MODEL_26eabac35cb644cc84ae64dfb5ff9ba4_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7',
  tm.id, 'MODEL-2af0515a5a36420086980b2322037edb',
  '所属设备管理', 'MODEL_2af0515a5a36420086980b2322037edb_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7'
  AND tm.code = 'MODEL-2af0515a5a36420086980b2322037edb'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7'
      AND r.target_model_code = 'MODEL-2af0515a5a36420086980b2322037edb'
      AND r.field_code = 'MODEL_2af0515a5a36420086980b2322037edb_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7',
  tm.id, 'MODEL-3130eba3747e4a5aa0862c94807822ba',
  '所属设备管理', 'MODEL_3130eba3747e4a5aa0862c94807822ba_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7'
  AND tm.code = 'MODEL-3130eba3747e4a5aa0862c94807822ba'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7'
      AND r.target_model_code = 'MODEL-3130eba3747e4a5aa0862c94807822ba'
      AND r.field_code = 'MODEL_3130eba3747e4a5aa0862c94807822ba_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7',
  tm.id, 'MODEL-44cc7511fc224085a51e2887569c933a',
  '所属设备管理', 'MODEL_44cc7511fc224085a51e2887569c933a_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7'
  AND tm.code = 'MODEL-44cc7511fc224085a51e2887569c933a'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7'
      AND r.target_model_code = 'MODEL-44cc7511fc224085a51e2887569c933a'
      AND r.field_code = 'MODEL_44cc7511fc224085a51e2887569c933a_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7',
  tm.id, 'MODEL-50991eab2a714ed288d00c58ff01c810',
  '所属设备管理', 'MODEL_50991eab2a714ed288d00c58ff01c810_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7'
  AND tm.code = 'MODEL-50991eab2a714ed288d00c58ff01c810'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7'
      AND r.target_model_code = 'MODEL-50991eab2a714ed288d00c58ff01c810'
      AND r.field_code = 'MODEL_50991eab2a714ed288d00c58ff01c810_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7',
  tm.id, 'MODEL-5a9b87c42571486998d363101c54ec8c',
  '所属设备管理', 'MODEL_5a9b87c42571486998d363101c54ec8c_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7'
  AND tm.code = 'MODEL-5a9b87c42571486998d363101c54ec8c'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7'
      AND r.target_model_code = 'MODEL-5a9b87c42571486998d363101c54ec8c'
      AND r.field_code = 'MODEL_5a9b87c42571486998d363101c54ec8c_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7',
  tm.id, 'MODEL-5f6f9243c7bb4e23bfed2fc01b48cf06',
  '所属设备管理', 'MODEL_5f6f9243c7bb4e23bfed2fc01b48cf06_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7'
  AND tm.code = 'MODEL-5f6f9243c7bb4e23bfed2fc01b48cf06'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7'
      AND r.target_model_code = 'MODEL-5f6f9243c7bb4e23bfed2fc01b48cf06'
      AND r.field_code = 'MODEL_5f6f9243c7bb4e23bfed2fc01b48cf06_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7',
  tm.id, 'MODEL-68554821800248cfacacbac1a53cdf37',
  '所属设备管理', 'MODEL_68554821800248cfacacbac1a53cdf37_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7'
  AND tm.code = 'MODEL-68554821800248cfacacbac1a53cdf37'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7'
      AND r.target_model_code = 'MODEL-68554821800248cfacacbac1a53cdf37'
      AND r.field_code = 'MODEL_68554821800248cfacacbac1a53cdf37_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7',
  tm.id, 'MODEL-6fd43ab74f6f425f81f0ad547603a849',
  '所属设备管理', 'MODEL_6fd43ab74f6f425f81f0ad547603a849_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7'
  AND tm.code = 'MODEL-6fd43ab74f6f425f81f0ad547603a849'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7'
      AND r.target_model_code = 'MODEL-6fd43ab74f6f425f81f0ad547603a849'
      AND r.field_code = 'MODEL_6fd43ab74f6f425f81f0ad547603a849_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7',
  tm.id, 'MODEL-70d2b066120c4b969f0b661d1d2fb664',
  '所属设备管理', 'MODEL_70d2b066120c4b969f0b661d1d2fb664_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7'
  AND tm.code = 'MODEL-70d2b066120c4b969f0b661d1d2fb664'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7'
      AND r.target_model_code = 'MODEL-70d2b066120c4b969f0b661d1d2fb664'
      AND r.field_code = 'MODEL_70d2b066120c4b969f0b661d1d2fb664_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7',
  tm.id, 'MODEL-7c4fe9b2d52d41cca5383488a4a400a5',
  '所属设备管理', 'MODEL_7c4fe9b2d52d41cca5383488a4a400a5_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7'
  AND tm.code = 'MODEL-7c4fe9b2d52d41cca5383488a4a400a5'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7'
      AND r.target_model_code = 'MODEL-7c4fe9b2d52d41cca5383488a4a400a5'
      AND r.field_code = 'MODEL_7c4fe9b2d52d41cca5383488a4a400a5_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7',
  tm.id, 'MODEL-7c746a60520347baaf64334625f07f31',
  '所属设备管理', 'MODEL_7c746a60520347baaf64334625f07f31_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7'
  AND tm.code = 'MODEL-7c746a60520347baaf64334625f07f31'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7'
      AND r.target_model_code = 'MODEL-7c746a60520347baaf64334625f07f31'
      AND r.field_code = 'MODEL_7c746a60520347baaf64334625f07f31_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7',
  tm.id, 'MODEL-81132ce00d2041c6a4ee06ec1c01dac8',
  '所属设备管理', 'MODEL_81132ce00d2041c6a4ee06ec1c01dac8_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7'
  AND tm.code = 'MODEL-81132ce00d2041c6a4ee06ec1c01dac8'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7'
      AND r.target_model_code = 'MODEL-81132ce00d2041c6a4ee06ec1c01dac8'
      AND r.field_code = 'MODEL_81132ce00d2041c6a4ee06ec1c01dac8_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7',
  tm.id, 'MODEL-88a38000121341e2950132da32baad90',
  '所属设备管理', 'MODEL_88a38000121341e2950132da32baad90_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7'
  AND tm.code = 'MODEL-88a38000121341e2950132da32baad90'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7'
      AND r.target_model_code = 'MODEL-88a38000121341e2950132da32baad90'
      AND r.field_code = 'MODEL_88a38000121341e2950132da32baad90_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7',
  tm.id, 'MODEL-990370e140aa481a82158bc96dc0b006',
  '所属设备管理', 'MODEL_990370e140aa481a82158bc96dc0b006_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7'
  AND tm.code = 'MODEL-990370e140aa481a82158bc96dc0b006'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7'
      AND r.target_model_code = 'MODEL-990370e140aa481a82158bc96dc0b006'
      AND r.field_code = 'MODEL_990370e140aa481a82158bc96dc0b006_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7',
  tm.id, 'MODEL-a4ce7668efe94d2fbc47a658be1a35d2',
  '所属设备管理', 'MODEL_a4ce7668efe94d2fbc47a658be1a35d2_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7'
  AND tm.code = 'MODEL-a4ce7668efe94d2fbc47a658be1a35d2'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7'
      AND r.target_model_code = 'MODEL-a4ce7668efe94d2fbc47a658be1a35d2'
      AND r.field_code = 'MODEL_a4ce7668efe94d2fbc47a658be1a35d2_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7',
  tm.id, 'MODEL-a62c727c0a024e19a8221c68fcf0c09a',
  '所属设备管理', 'MODEL_a62c727c0a024e19a8221c68fcf0c09a_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7'
  AND tm.code = 'MODEL-a62c727c0a024e19a8221c68fcf0c09a'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7'
      AND r.target_model_code = 'MODEL-a62c727c0a024e19a8221c68fcf0c09a'
      AND r.field_code = 'MODEL_a62c727c0a024e19a8221c68fcf0c09a_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7',
  tm.id, 'MODEL-b8251da851514341adef7f6f72995cf3',
  '所属设备管理', 'MODEL_b8251da851514341adef7f6f72995cf3_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7'
  AND tm.code = 'MODEL-b8251da851514341adef7f6f72995cf3'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7'
      AND r.target_model_code = 'MODEL-b8251da851514341adef7f6f72995cf3'
      AND r.field_code = 'MODEL_b8251da851514341adef7f6f72995cf3_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7',
  tm.id, 'MODEL-b83f05dc94f34fb4807458c0a48ba488',
  '所属设备管理', 'MODEL_b83f05dc94f34fb4807458c0a48ba488_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7'
  AND tm.code = 'MODEL-b83f05dc94f34fb4807458c0a48ba488'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7'
      AND r.target_model_code = 'MODEL-b83f05dc94f34fb4807458c0a48ba488'
      AND r.field_code = 'MODEL_b83f05dc94f34fb4807458c0a48ba488_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7',
  tm.id, 'MODEL-bc2cdffeab434f4d862c847fa99938ba',
  '所属设备管理', 'MODEL_bc2cdffeab434f4d862c847fa99938ba_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7'
  AND tm.code = 'MODEL-bc2cdffeab434f4d862c847fa99938ba'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7'
      AND r.target_model_code = 'MODEL-bc2cdffeab434f4d862c847fa99938ba'
      AND r.field_code = 'MODEL_bc2cdffeab434f4d862c847fa99938ba_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7',
  tm.id, 'MODEL-bf41bd50df044f5faf43c4759353ccd1',
  '所属设备管理', 'MODEL_bf41bd50df044f5faf43c4759353ccd1_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7'
  AND tm.code = 'MODEL-bf41bd50df044f5faf43c4759353ccd1'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7'
      AND r.target_model_code = 'MODEL-bf41bd50df044f5faf43c4759353ccd1'
      AND r.field_code = 'MODEL_bf41bd50df044f5faf43c4759353ccd1_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7',
  tm.id, 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9',
  '所属设备管理', 'MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7'
  AND tm.code = 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7'
      AND r.target_model_code = 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9'
      AND r.field_code = 'MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7',
  tm.id, 'MODEL-c6bc32646f3e4bde8409120c5c290eee',
  '所属设备管理', 'MODEL_c6bc32646f3e4bde8409120c5c290eee_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7'
  AND tm.code = 'MODEL-c6bc32646f3e4bde8409120c5c290eee'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7'
      AND r.target_model_code = 'MODEL-c6bc32646f3e4bde8409120c5c290eee'
      AND r.field_code = 'MODEL_c6bc32646f3e4bde8409120c5c290eee_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7',
  tm.id, 'MODEL-d446eef7329f45b4a374347e215fec5d',
  '所属设备管理', 'MODEL_d446eef7329f45b4a374347e215fec5d_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7'
  AND tm.code = 'MODEL-d446eef7329f45b4a374347e215fec5d'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7'
      AND r.target_model_code = 'MODEL-d446eef7329f45b4a374347e215fec5d'
      AND r.field_code = 'MODEL_d446eef7329f45b4a374347e215fec5d_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7',
  tm.id, 'MODEL-e25f3089a99d4878a1ddd326ba0910bf',
  '所属设备管理', 'MODEL_e25f3089a99d4878a1ddd326ba0910bf_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7'
  AND tm.code = 'MODEL-e25f3089a99d4878a1ddd326ba0910bf'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7'
      AND r.target_model_code = 'MODEL-e25f3089a99d4878a1ddd326ba0910bf'
      AND r.field_code = 'MODEL_e25f3089a99d4878a1ddd326ba0910bf_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7',
  tm.id, 'MODEL-e5af81c45bbc44d48374d8036032cc5d',
  '所属设备管理', 'MODEL_e5af81c45bbc44d48374d8036032cc5d_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7'
  AND tm.code = 'MODEL-e5af81c45bbc44d48374d8036032cc5d'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7'
      AND r.target_model_code = 'MODEL-e5af81c45bbc44d48374d8036032cc5d'
      AND r.field_code = 'MODEL_e5af81c45bbc44d48374d8036032cc5d_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7',
  tm.id, 'MODEL-e5c45ff8ca074102829bba872e091bd1',
  '所属设备管理', 'MODEL_e5c45ff8ca074102829bba872e091bd1_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7'
  AND tm.code = 'MODEL-e5c45ff8ca074102829bba872e091bd1'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7'
      AND r.target_model_code = 'MODEL-e5c45ff8ca074102829bba872e091bd1'
      AND r.field_code = 'MODEL_e5c45ff8ca074102829bba872e091bd1_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7',
  tm.id, 'MODEL-f5270d478dd849389e59a178cd512629',
  '所属设备管理', 'MODEL_f5270d478dd849389e59a178cd512629_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7'
  AND tm.code = 'MODEL-f5270d478dd849389e59a178cd512629'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7'
      AND r.target_model_code = 'MODEL-f5270d478dd849389e59a178cd512629'
      AND r.field_code = 'MODEL_f5270d478dd849389e59a178cd512629_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7',
  tm.id, 'MODEL-f55ce56d31f741caa5a69801556834fe',
  '所属设备管理', 'MODEL_f55ce56d31f741caa5a69801556834fe_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7'
  AND tm.code = 'MODEL-f55ce56d31f741caa5a69801556834fe'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7'
      AND r.target_model_code = 'MODEL-f55ce56d31f741caa5a69801556834fe'
      AND r.field_code = 'MODEL_f55ce56d31f741caa5a69801556834fe_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9',
  tm.id, 'MODEL-05e5c6a6c8fe4f3c9bd1dd86dd24a659',
  '所属设备管理', 'MODEL_05e5c6a6c8fe4f3c9bd1dd86dd24a659_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
  AND tm.code = 'MODEL-05e5c6a6c8fe4f3c9bd1dd86dd24a659'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
      AND r.target_model_code = 'MODEL-05e5c6a6c8fe4f3c9bd1dd86dd24a659'
      AND r.field_code = 'MODEL_05e5c6a6c8fe4f3c9bd1dd86dd24a659_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9',
  tm.id, 'MODEL-123328b7bfe745ceb337d286ac64aa17',
  '所属设备管理', 'MODEL_123328b7bfe745ceb337d286ac64aa17_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
  AND tm.code = 'MODEL-123328b7bfe745ceb337d286ac64aa17'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
      AND r.target_model_code = 'MODEL-123328b7bfe745ceb337d286ac64aa17'
      AND r.field_code = 'MODEL_123328b7bfe745ceb337d286ac64aa17_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9',
  tm.id, 'MODEL-17eb9be1c7404734af570ce040f1783b',
  '所属设备管理', 'MODEL_17eb9be1c7404734af570ce040f1783b_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
  AND tm.code = 'MODEL-17eb9be1c7404734af570ce040f1783b'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
      AND r.target_model_code = 'MODEL-17eb9be1c7404734af570ce040f1783b'
      AND r.field_code = 'MODEL_17eb9be1c7404734af570ce040f1783b_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9',
  tm.id, 'MODEL-26eabac35cb644cc84ae64dfb5ff9ba4',
  '所属设备管理', 'MODEL_26eabac35cb644cc84ae64dfb5ff9ba4_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
  AND tm.code = 'MODEL-26eabac35cb644cc84ae64dfb5ff9ba4'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
      AND r.target_model_code = 'MODEL-26eabac35cb644cc84ae64dfb5ff9ba4'
      AND r.field_code = 'MODEL_26eabac35cb644cc84ae64dfb5ff9ba4_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9',
  tm.id, 'MODEL-2af0515a5a36420086980b2322037edb',
  '所属设备管理', 'MODEL_2af0515a5a36420086980b2322037edb_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
  AND tm.code = 'MODEL-2af0515a5a36420086980b2322037edb'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
      AND r.target_model_code = 'MODEL-2af0515a5a36420086980b2322037edb'
      AND r.field_code = 'MODEL_2af0515a5a36420086980b2322037edb_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9',
  tm.id, 'MODEL-3130eba3747e4a5aa0862c94807822ba',
  '所属设备管理', 'MODEL_3130eba3747e4a5aa0862c94807822ba_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
  AND tm.code = 'MODEL-3130eba3747e4a5aa0862c94807822ba'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
      AND r.target_model_code = 'MODEL-3130eba3747e4a5aa0862c94807822ba'
      AND r.field_code = 'MODEL_3130eba3747e4a5aa0862c94807822ba_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9',
  tm.id, 'MODEL-44cc7511fc224085a51e2887569c933a',
  '所属设备管理', 'MODEL_44cc7511fc224085a51e2887569c933a_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
  AND tm.code = 'MODEL-44cc7511fc224085a51e2887569c933a'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
      AND r.target_model_code = 'MODEL-44cc7511fc224085a51e2887569c933a'
      AND r.field_code = 'MODEL_44cc7511fc224085a51e2887569c933a_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9',
  tm.id, 'MODEL-50991eab2a714ed288d00c58ff01c810',
  '所属设备管理', 'MODEL_50991eab2a714ed288d00c58ff01c810_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
  AND tm.code = 'MODEL-50991eab2a714ed288d00c58ff01c810'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
      AND r.target_model_code = 'MODEL-50991eab2a714ed288d00c58ff01c810'
      AND r.field_code = 'MODEL_50991eab2a714ed288d00c58ff01c810_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9',
  tm.id, 'MODEL-5a9b87c42571486998d363101c54ec8c',
  '所属设备管理', 'MODEL_5a9b87c42571486998d363101c54ec8c_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
  AND tm.code = 'MODEL-5a9b87c42571486998d363101c54ec8c'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
      AND r.target_model_code = 'MODEL-5a9b87c42571486998d363101c54ec8c'
      AND r.field_code = 'MODEL_5a9b87c42571486998d363101c54ec8c_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9',
  tm.id, 'MODEL-5f6f9243c7bb4e23bfed2fc01b48cf06',
  '所属设备管理', 'MODEL_5f6f9243c7bb4e23bfed2fc01b48cf06_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
  AND tm.code = 'MODEL-5f6f9243c7bb4e23bfed2fc01b48cf06'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
      AND r.target_model_code = 'MODEL-5f6f9243c7bb4e23bfed2fc01b48cf06'
      AND r.field_code = 'MODEL_5f6f9243c7bb4e23bfed2fc01b48cf06_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9',
  tm.id, 'MODEL-68554821800248cfacacbac1a53cdf37',
  '所属设备管理', 'MODEL_68554821800248cfacacbac1a53cdf37_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
  AND tm.code = 'MODEL-68554821800248cfacacbac1a53cdf37'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
      AND r.target_model_code = 'MODEL-68554821800248cfacacbac1a53cdf37'
      AND r.field_code = 'MODEL_68554821800248cfacacbac1a53cdf37_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9',
  tm.id, 'MODEL-6fd43ab74f6f425f81f0ad547603a849',
  '所属设备管理', 'MODEL_6fd43ab74f6f425f81f0ad547603a849_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
  AND tm.code = 'MODEL-6fd43ab74f6f425f81f0ad547603a849'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
      AND r.target_model_code = 'MODEL-6fd43ab74f6f425f81f0ad547603a849'
      AND r.field_code = 'MODEL_6fd43ab74f6f425f81f0ad547603a849_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9',
  tm.id, 'MODEL-70d2b066120c4b969f0b661d1d2fb664',
  '所属设备管理', 'MODEL_70d2b066120c4b969f0b661d1d2fb664_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
  AND tm.code = 'MODEL-70d2b066120c4b969f0b661d1d2fb664'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
      AND r.target_model_code = 'MODEL-70d2b066120c4b969f0b661d1d2fb664'
      AND r.field_code = 'MODEL_70d2b066120c4b969f0b661d1d2fb664_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9',
  tm.id, 'MODEL-7c4fe9b2d52d41cca5383488a4a400a5',
  '所属设备管理', 'MODEL_7c4fe9b2d52d41cca5383488a4a400a5_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
  AND tm.code = 'MODEL-7c4fe9b2d52d41cca5383488a4a400a5'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
      AND r.target_model_code = 'MODEL-7c4fe9b2d52d41cca5383488a4a400a5'
      AND r.field_code = 'MODEL_7c4fe9b2d52d41cca5383488a4a400a5_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9',
  tm.id, 'MODEL-7c746a60520347baaf64334625f07f31',
  '所属设备管理', 'MODEL_7c746a60520347baaf64334625f07f31_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
  AND tm.code = 'MODEL-7c746a60520347baaf64334625f07f31'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
      AND r.target_model_code = 'MODEL-7c746a60520347baaf64334625f07f31'
      AND r.field_code = 'MODEL_7c746a60520347baaf64334625f07f31_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9',
  tm.id, 'MODEL-81132ce00d2041c6a4ee06ec1c01dac8',
  '所属设备管理', 'MODEL_81132ce00d2041c6a4ee06ec1c01dac8_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
  AND tm.code = 'MODEL-81132ce00d2041c6a4ee06ec1c01dac8'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
      AND r.target_model_code = 'MODEL-81132ce00d2041c6a4ee06ec1c01dac8'
      AND r.field_code = 'MODEL_81132ce00d2041c6a4ee06ec1c01dac8_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9',
  tm.id, 'MODEL-88a38000121341e2950132da32baad90',
  '所属设备管理', 'MODEL_88a38000121341e2950132da32baad90_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
  AND tm.code = 'MODEL-88a38000121341e2950132da32baad90'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
      AND r.target_model_code = 'MODEL-88a38000121341e2950132da32baad90'
      AND r.field_code = 'MODEL_88a38000121341e2950132da32baad90_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9',
  tm.id, 'MODEL-990370e140aa481a82158bc96dc0b006',
  '所属设备管理', 'MODEL_990370e140aa481a82158bc96dc0b006_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
  AND tm.code = 'MODEL-990370e140aa481a82158bc96dc0b006'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
      AND r.target_model_code = 'MODEL-990370e140aa481a82158bc96dc0b006'
      AND r.field_code = 'MODEL_990370e140aa481a82158bc96dc0b006_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9',
  tm.id, 'MODEL-a4ce7668efe94d2fbc47a658be1a35d2',
  '所属设备管理', 'MODEL_a4ce7668efe94d2fbc47a658be1a35d2_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
  AND tm.code = 'MODEL-a4ce7668efe94d2fbc47a658be1a35d2'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
      AND r.target_model_code = 'MODEL-a4ce7668efe94d2fbc47a658be1a35d2'
      AND r.field_code = 'MODEL_a4ce7668efe94d2fbc47a658be1a35d2_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9',
  tm.id, 'MODEL-a62c727c0a024e19a8221c68fcf0c09a',
  '所属设备管理', 'MODEL_a62c727c0a024e19a8221c68fcf0c09a_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
  AND tm.code = 'MODEL-a62c727c0a024e19a8221c68fcf0c09a'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
      AND r.target_model_code = 'MODEL-a62c727c0a024e19a8221c68fcf0c09a'
      AND r.field_code = 'MODEL_a62c727c0a024e19a8221c68fcf0c09a_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9',
  tm.id, 'MODEL-b8251da851514341adef7f6f72995cf3',
  '所属设备管理', 'MODEL_b8251da851514341adef7f6f72995cf3_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
  AND tm.code = 'MODEL-b8251da851514341adef7f6f72995cf3'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
      AND r.target_model_code = 'MODEL-b8251da851514341adef7f6f72995cf3'
      AND r.field_code = 'MODEL_b8251da851514341adef7f6f72995cf3_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9',
  tm.id, 'MODEL-b83f05dc94f34fb4807458c0a48ba488',
  '所属设备管理', 'MODEL_b83f05dc94f34fb4807458c0a48ba488_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
  AND tm.code = 'MODEL-b83f05dc94f34fb4807458c0a48ba488'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
      AND r.target_model_code = 'MODEL-b83f05dc94f34fb4807458c0a48ba488'
      AND r.field_code = 'MODEL_b83f05dc94f34fb4807458c0a48ba488_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9',
  tm.id, 'MODEL-bc2cdffeab434f4d862c847fa99938ba',
  '所属设备管理', 'MODEL_bc2cdffeab434f4d862c847fa99938ba_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
  AND tm.code = 'MODEL-bc2cdffeab434f4d862c847fa99938ba'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
      AND r.target_model_code = 'MODEL-bc2cdffeab434f4d862c847fa99938ba'
      AND r.field_code = 'MODEL_bc2cdffeab434f4d862c847fa99938ba_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9',
  tm.id, 'MODEL-bf41bd50df044f5faf43c4759353ccd1',
  '所属设备管理', 'MODEL_bf41bd50df044f5faf43c4759353ccd1_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
  AND tm.code = 'MODEL-bf41bd50df044f5faf43c4759353ccd1'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
      AND r.target_model_code = 'MODEL-bf41bd50df044f5faf43c4759353ccd1'
      AND r.field_code = 'MODEL_bf41bd50df044f5faf43c4759353ccd1_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9',
  tm.id, 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9',
  '所属设备管理', 'MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
  AND tm.code = 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
      AND r.target_model_code = 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9'
      AND r.field_code = 'MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9',
  tm.id, 'MODEL-c6bc32646f3e4bde8409120c5c290eee',
  '所属设备管理', 'MODEL_c6bc32646f3e4bde8409120c5c290eee_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
  AND tm.code = 'MODEL-c6bc32646f3e4bde8409120c5c290eee'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
      AND r.target_model_code = 'MODEL-c6bc32646f3e4bde8409120c5c290eee'
      AND r.field_code = 'MODEL_c6bc32646f3e4bde8409120c5c290eee_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9',
  tm.id, 'MODEL-d446eef7329f45b4a374347e215fec5d',
  '所属设备管理', 'MODEL_d446eef7329f45b4a374347e215fec5d_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
  AND tm.code = 'MODEL-d446eef7329f45b4a374347e215fec5d'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
      AND r.target_model_code = 'MODEL-d446eef7329f45b4a374347e215fec5d'
      AND r.field_code = 'MODEL_d446eef7329f45b4a374347e215fec5d_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9',
  tm.id, 'MODEL-e25f3089a99d4878a1ddd326ba0910bf',
  '所属设备管理', 'MODEL_e25f3089a99d4878a1ddd326ba0910bf_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
  AND tm.code = 'MODEL-e25f3089a99d4878a1ddd326ba0910bf'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
      AND r.target_model_code = 'MODEL-e25f3089a99d4878a1ddd326ba0910bf'
      AND r.field_code = 'MODEL_e25f3089a99d4878a1ddd326ba0910bf_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9',
  tm.id, 'MODEL-e5af81c45bbc44d48374d8036032cc5d',
  '所属设备管理', 'MODEL_e5af81c45bbc44d48374d8036032cc5d_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
  AND tm.code = 'MODEL-e5af81c45bbc44d48374d8036032cc5d'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
      AND r.target_model_code = 'MODEL-e5af81c45bbc44d48374d8036032cc5d'
      AND r.field_code = 'MODEL_e5af81c45bbc44d48374d8036032cc5d_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9',
  tm.id, 'MODEL-e5c45ff8ca074102829bba872e091bd1',
  '所属设备管理', 'MODEL_e5c45ff8ca074102829bba872e091bd1_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
  AND tm.code = 'MODEL-e5c45ff8ca074102829bba872e091bd1'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
      AND r.target_model_code = 'MODEL-e5c45ff8ca074102829bba872e091bd1'
      AND r.field_code = 'MODEL_e5c45ff8ca074102829bba872e091bd1_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9',
  tm.id, 'MODEL-f5270d478dd849389e59a178cd512629',
  '所属设备管理', 'MODEL_f5270d478dd849389e59a178cd512629_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
  AND tm.code = 'MODEL-f5270d478dd849389e59a178cd512629'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
      AND r.target_model_code = 'MODEL-f5270d478dd849389e59a178cd512629'
      AND r.field_code = 'MODEL_f5270d478dd849389e59a178cd512629_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9',
  tm.id, 'MODEL-f55ce56d31f741caa5a69801556834fe',
  '所属设备管理', 'MODEL_f55ce56d31f741caa5a69801556834fe_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
  AND tm.code = 'MODEL-f55ce56d31f741caa5a69801556834fe'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
      AND r.target_model_code = 'MODEL-f55ce56d31f741caa5a69801556834fe'
      AND r.field_code = 'MODEL_f55ce56d31f741caa5a69801556834fe_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-f1abcb10cae34b459f85e4135505a6a4',
  tm.id, 'MODEL-05e5c6a6c8fe4f3c9bd1dd86dd24a659',
  '所属设备管理', 'MODEL_05e5c6a6c8fe4f3c9bd1dd86dd24a659_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-f1abcb10cae34b459f85e4135505a6a4'
  AND tm.code = 'MODEL-05e5c6a6c8fe4f3c9bd1dd86dd24a659'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-f1abcb10cae34b459f85e4135505a6a4'
      AND r.target_model_code = 'MODEL-05e5c6a6c8fe4f3c9bd1dd86dd24a659'
      AND r.field_code = 'MODEL_05e5c6a6c8fe4f3c9bd1dd86dd24a659_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-f1abcb10cae34b459f85e4135505a6a4',
  tm.id, 'MODEL-123328b7bfe745ceb337d286ac64aa17',
  '所属设备管理', 'MODEL_123328b7bfe745ceb337d286ac64aa17_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-f1abcb10cae34b459f85e4135505a6a4'
  AND tm.code = 'MODEL-123328b7bfe745ceb337d286ac64aa17'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-f1abcb10cae34b459f85e4135505a6a4'
      AND r.target_model_code = 'MODEL-123328b7bfe745ceb337d286ac64aa17'
      AND r.field_code = 'MODEL_123328b7bfe745ceb337d286ac64aa17_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-f1abcb10cae34b459f85e4135505a6a4',
  tm.id, 'MODEL-17eb9be1c7404734af570ce040f1783b',
  '所属设备管理', 'MODEL_17eb9be1c7404734af570ce040f1783b_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-f1abcb10cae34b459f85e4135505a6a4'
  AND tm.code = 'MODEL-17eb9be1c7404734af570ce040f1783b'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-f1abcb10cae34b459f85e4135505a6a4'
      AND r.target_model_code = 'MODEL-17eb9be1c7404734af570ce040f1783b'
      AND r.field_code = 'MODEL_17eb9be1c7404734af570ce040f1783b_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-f1abcb10cae34b459f85e4135505a6a4',
  tm.id, 'MODEL-26eabac35cb644cc84ae64dfb5ff9ba4',
  '所属设备管理', 'MODEL_26eabac35cb644cc84ae64dfb5ff9ba4_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-f1abcb10cae34b459f85e4135505a6a4'
  AND tm.code = 'MODEL-26eabac35cb644cc84ae64dfb5ff9ba4'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-f1abcb10cae34b459f85e4135505a6a4'
      AND r.target_model_code = 'MODEL-26eabac35cb644cc84ae64dfb5ff9ba4'
      AND r.field_code = 'MODEL_26eabac35cb644cc84ae64dfb5ff9ba4_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-f1abcb10cae34b459f85e4135505a6a4',
  tm.id, 'MODEL-2af0515a5a36420086980b2322037edb',
  '所属设备管理', 'MODEL_2af0515a5a36420086980b2322037edb_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-f1abcb10cae34b459f85e4135505a6a4'
  AND tm.code = 'MODEL-2af0515a5a36420086980b2322037edb'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-f1abcb10cae34b459f85e4135505a6a4'
      AND r.target_model_code = 'MODEL-2af0515a5a36420086980b2322037edb'
      AND r.field_code = 'MODEL_2af0515a5a36420086980b2322037edb_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-f1abcb10cae34b459f85e4135505a6a4',
  tm.id, 'MODEL-3130eba3747e4a5aa0862c94807822ba',
  '所属设备管理', 'MODEL_3130eba3747e4a5aa0862c94807822ba_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-f1abcb10cae34b459f85e4135505a6a4'
  AND tm.code = 'MODEL-3130eba3747e4a5aa0862c94807822ba'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-f1abcb10cae34b459f85e4135505a6a4'
      AND r.target_model_code = 'MODEL-3130eba3747e4a5aa0862c94807822ba'
      AND r.field_code = 'MODEL_3130eba3747e4a5aa0862c94807822ba_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-f1abcb10cae34b459f85e4135505a6a4',
  tm.id, 'MODEL-44cc7511fc224085a51e2887569c933a',
  '所属设备管理', 'MODEL_44cc7511fc224085a51e2887569c933a_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-f1abcb10cae34b459f85e4135505a6a4'
  AND tm.code = 'MODEL-44cc7511fc224085a51e2887569c933a'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-f1abcb10cae34b459f85e4135505a6a4'
      AND r.target_model_code = 'MODEL-44cc7511fc224085a51e2887569c933a'
      AND r.field_code = 'MODEL_44cc7511fc224085a51e2887569c933a_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-f1abcb10cae34b459f85e4135505a6a4',
  tm.id, 'MODEL-50991eab2a714ed288d00c58ff01c810',
  '所属设备管理', 'MODEL_50991eab2a714ed288d00c58ff01c810_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-f1abcb10cae34b459f85e4135505a6a4'
  AND tm.code = 'MODEL-50991eab2a714ed288d00c58ff01c810'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-f1abcb10cae34b459f85e4135505a6a4'
      AND r.target_model_code = 'MODEL-50991eab2a714ed288d00c58ff01c810'
      AND r.field_code = 'MODEL_50991eab2a714ed288d00c58ff01c810_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-f1abcb10cae34b459f85e4135505a6a4',
  tm.id, 'MODEL-5a9b87c42571486998d363101c54ec8c',
  '所属设备管理', 'MODEL_5a9b87c42571486998d363101c54ec8c_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-f1abcb10cae34b459f85e4135505a6a4'
  AND tm.code = 'MODEL-5a9b87c42571486998d363101c54ec8c'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-f1abcb10cae34b459f85e4135505a6a4'
      AND r.target_model_code = 'MODEL-5a9b87c42571486998d363101c54ec8c'
      AND r.field_code = 'MODEL_5a9b87c42571486998d363101c54ec8c_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-f1abcb10cae34b459f85e4135505a6a4',
  tm.id, 'MODEL-5f6f9243c7bb4e23bfed2fc01b48cf06',
  '所属设备管理', 'MODEL_5f6f9243c7bb4e23bfed2fc01b48cf06_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-f1abcb10cae34b459f85e4135505a6a4'
  AND tm.code = 'MODEL-5f6f9243c7bb4e23bfed2fc01b48cf06'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-f1abcb10cae34b459f85e4135505a6a4'
      AND r.target_model_code = 'MODEL-5f6f9243c7bb4e23bfed2fc01b48cf06'
      AND r.field_code = 'MODEL_5f6f9243c7bb4e23bfed2fc01b48cf06_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-f1abcb10cae34b459f85e4135505a6a4',
  tm.id, 'MODEL-68554821800248cfacacbac1a53cdf37',
  '所属设备管理', 'MODEL_68554821800248cfacacbac1a53cdf37_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-f1abcb10cae34b459f85e4135505a6a4'
  AND tm.code = 'MODEL-68554821800248cfacacbac1a53cdf37'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-f1abcb10cae34b459f85e4135505a6a4'
      AND r.target_model_code = 'MODEL-68554821800248cfacacbac1a53cdf37'
      AND r.field_code = 'MODEL_68554821800248cfacacbac1a53cdf37_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-f1abcb10cae34b459f85e4135505a6a4',
  tm.id, 'MODEL-6fd43ab74f6f425f81f0ad547603a849',
  '所属设备管理', 'MODEL_6fd43ab74f6f425f81f0ad547603a849_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-f1abcb10cae34b459f85e4135505a6a4'
  AND tm.code = 'MODEL-6fd43ab74f6f425f81f0ad547603a849'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-f1abcb10cae34b459f85e4135505a6a4'
      AND r.target_model_code = 'MODEL-6fd43ab74f6f425f81f0ad547603a849'
      AND r.field_code = 'MODEL_6fd43ab74f6f425f81f0ad547603a849_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-f1abcb10cae34b459f85e4135505a6a4',
  tm.id, 'MODEL-70d2b066120c4b969f0b661d1d2fb664',
  '所属设备管理', 'MODEL_70d2b066120c4b969f0b661d1d2fb664_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-f1abcb10cae34b459f85e4135505a6a4'
  AND tm.code = 'MODEL-70d2b066120c4b969f0b661d1d2fb664'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-f1abcb10cae34b459f85e4135505a6a4'
      AND r.target_model_code = 'MODEL-70d2b066120c4b969f0b661d1d2fb664'
      AND r.field_code = 'MODEL_70d2b066120c4b969f0b661d1d2fb664_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-f1abcb10cae34b459f85e4135505a6a4',
  tm.id, 'MODEL-7c4fe9b2d52d41cca5383488a4a400a5',
  '所属设备管理', 'MODEL_7c4fe9b2d52d41cca5383488a4a400a5_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-f1abcb10cae34b459f85e4135505a6a4'
  AND tm.code = 'MODEL-7c4fe9b2d52d41cca5383488a4a400a5'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-f1abcb10cae34b459f85e4135505a6a4'
      AND r.target_model_code = 'MODEL-7c4fe9b2d52d41cca5383488a4a400a5'
      AND r.field_code = 'MODEL_7c4fe9b2d52d41cca5383488a4a400a5_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-f1abcb10cae34b459f85e4135505a6a4',
  tm.id, 'MODEL-7c746a60520347baaf64334625f07f31',
  '所属设备管理', 'MODEL_7c746a60520347baaf64334625f07f31_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-f1abcb10cae34b459f85e4135505a6a4'
  AND tm.code = 'MODEL-7c746a60520347baaf64334625f07f31'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-f1abcb10cae34b459f85e4135505a6a4'
      AND r.target_model_code = 'MODEL-7c746a60520347baaf64334625f07f31'
      AND r.field_code = 'MODEL_7c746a60520347baaf64334625f07f31_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-f1abcb10cae34b459f85e4135505a6a4',
  tm.id, 'MODEL-81132ce00d2041c6a4ee06ec1c01dac8',
  '所属设备管理', 'MODEL_81132ce00d2041c6a4ee06ec1c01dac8_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-f1abcb10cae34b459f85e4135505a6a4'
  AND tm.code = 'MODEL-81132ce00d2041c6a4ee06ec1c01dac8'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-f1abcb10cae34b459f85e4135505a6a4'
      AND r.target_model_code = 'MODEL-81132ce00d2041c6a4ee06ec1c01dac8'
      AND r.field_code = 'MODEL_81132ce00d2041c6a4ee06ec1c01dac8_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-f1abcb10cae34b459f85e4135505a6a4',
  tm.id, 'MODEL-88a38000121341e2950132da32baad90',
  '所属设备管理', 'MODEL_88a38000121341e2950132da32baad90_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-f1abcb10cae34b459f85e4135505a6a4'
  AND tm.code = 'MODEL-88a38000121341e2950132da32baad90'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-f1abcb10cae34b459f85e4135505a6a4'
      AND r.target_model_code = 'MODEL-88a38000121341e2950132da32baad90'
      AND r.field_code = 'MODEL_88a38000121341e2950132da32baad90_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-f1abcb10cae34b459f85e4135505a6a4',
  tm.id, 'MODEL-990370e140aa481a82158bc96dc0b006',
  '所属设备管理', 'MODEL_990370e140aa481a82158bc96dc0b006_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-f1abcb10cae34b459f85e4135505a6a4'
  AND tm.code = 'MODEL-990370e140aa481a82158bc96dc0b006'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-f1abcb10cae34b459f85e4135505a6a4'
      AND r.target_model_code = 'MODEL-990370e140aa481a82158bc96dc0b006'
      AND r.field_code = 'MODEL_990370e140aa481a82158bc96dc0b006_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-f1abcb10cae34b459f85e4135505a6a4',
  tm.id, 'MODEL-a4ce7668efe94d2fbc47a658be1a35d2',
  '所属设备管理', 'MODEL_a4ce7668efe94d2fbc47a658be1a35d2_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-f1abcb10cae34b459f85e4135505a6a4'
  AND tm.code = 'MODEL-a4ce7668efe94d2fbc47a658be1a35d2'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-f1abcb10cae34b459f85e4135505a6a4'
      AND r.target_model_code = 'MODEL-a4ce7668efe94d2fbc47a658be1a35d2'
      AND r.field_code = 'MODEL_a4ce7668efe94d2fbc47a658be1a35d2_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-f1abcb10cae34b459f85e4135505a6a4',
  tm.id, 'MODEL-a62c727c0a024e19a8221c68fcf0c09a',
  '所属设备管理', 'MODEL_a62c727c0a024e19a8221c68fcf0c09a_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-f1abcb10cae34b459f85e4135505a6a4'
  AND tm.code = 'MODEL-a62c727c0a024e19a8221c68fcf0c09a'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-f1abcb10cae34b459f85e4135505a6a4'
      AND r.target_model_code = 'MODEL-a62c727c0a024e19a8221c68fcf0c09a'
      AND r.field_code = 'MODEL_a62c727c0a024e19a8221c68fcf0c09a_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-f1abcb10cae34b459f85e4135505a6a4',
  tm.id, 'MODEL-b8251da851514341adef7f6f72995cf3',
  '所属设备管理', 'MODEL_b8251da851514341adef7f6f72995cf3_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-f1abcb10cae34b459f85e4135505a6a4'
  AND tm.code = 'MODEL-b8251da851514341adef7f6f72995cf3'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-f1abcb10cae34b459f85e4135505a6a4'
      AND r.target_model_code = 'MODEL-b8251da851514341adef7f6f72995cf3'
      AND r.field_code = 'MODEL_b8251da851514341adef7f6f72995cf3_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-f1abcb10cae34b459f85e4135505a6a4',
  tm.id, 'MODEL-b83f05dc94f34fb4807458c0a48ba488',
  '所属设备管理', 'MODEL_b83f05dc94f34fb4807458c0a48ba488_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-f1abcb10cae34b459f85e4135505a6a4'
  AND tm.code = 'MODEL-b83f05dc94f34fb4807458c0a48ba488'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-f1abcb10cae34b459f85e4135505a6a4'
      AND r.target_model_code = 'MODEL-b83f05dc94f34fb4807458c0a48ba488'
      AND r.field_code = 'MODEL_b83f05dc94f34fb4807458c0a48ba488_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-f1abcb10cae34b459f85e4135505a6a4',
  tm.id, 'MODEL-bc2cdffeab434f4d862c847fa99938ba',
  '所属设备管理', 'MODEL_bc2cdffeab434f4d862c847fa99938ba_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-f1abcb10cae34b459f85e4135505a6a4'
  AND tm.code = 'MODEL-bc2cdffeab434f4d862c847fa99938ba'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-f1abcb10cae34b459f85e4135505a6a4'
      AND r.target_model_code = 'MODEL-bc2cdffeab434f4d862c847fa99938ba'
      AND r.field_code = 'MODEL_bc2cdffeab434f4d862c847fa99938ba_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-f1abcb10cae34b459f85e4135505a6a4',
  tm.id, 'MODEL-bf41bd50df044f5faf43c4759353ccd1',
  '所属设备管理', 'MODEL_bf41bd50df044f5faf43c4759353ccd1_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-f1abcb10cae34b459f85e4135505a6a4'
  AND tm.code = 'MODEL-bf41bd50df044f5faf43c4759353ccd1'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-f1abcb10cae34b459f85e4135505a6a4'
      AND r.target_model_code = 'MODEL-bf41bd50df044f5faf43c4759353ccd1'
      AND r.field_code = 'MODEL_bf41bd50df044f5faf43c4759353ccd1_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-f1abcb10cae34b459f85e4135505a6a4',
  tm.id, 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9',
  '所属设备管理', 'MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-f1abcb10cae34b459f85e4135505a6a4'
  AND tm.code = 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-f1abcb10cae34b459f85e4135505a6a4'
      AND r.target_model_code = 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9'
      AND r.field_code = 'MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-f1abcb10cae34b459f85e4135505a6a4',
  tm.id, 'MODEL-c6bc32646f3e4bde8409120c5c290eee',
  '所属设备管理', 'MODEL_c6bc32646f3e4bde8409120c5c290eee_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-f1abcb10cae34b459f85e4135505a6a4'
  AND tm.code = 'MODEL-c6bc32646f3e4bde8409120c5c290eee'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-f1abcb10cae34b459f85e4135505a6a4'
      AND r.target_model_code = 'MODEL-c6bc32646f3e4bde8409120c5c290eee'
      AND r.field_code = 'MODEL_c6bc32646f3e4bde8409120c5c290eee_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-f1abcb10cae34b459f85e4135505a6a4',
  tm.id, 'MODEL-d446eef7329f45b4a374347e215fec5d',
  '所属设备管理', 'MODEL_d446eef7329f45b4a374347e215fec5d_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-f1abcb10cae34b459f85e4135505a6a4'
  AND tm.code = 'MODEL-d446eef7329f45b4a374347e215fec5d'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-f1abcb10cae34b459f85e4135505a6a4'
      AND r.target_model_code = 'MODEL-d446eef7329f45b4a374347e215fec5d'
      AND r.field_code = 'MODEL_d446eef7329f45b4a374347e215fec5d_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-f1abcb10cae34b459f85e4135505a6a4',
  tm.id, 'MODEL-e25f3089a99d4878a1ddd326ba0910bf',
  '所属设备管理', 'MODEL_e25f3089a99d4878a1ddd326ba0910bf_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-f1abcb10cae34b459f85e4135505a6a4'
  AND tm.code = 'MODEL-e25f3089a99d4878a1ddd326ba0910bf'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-f1abcb10cae34b459f85e4135505a6a4'
      AND r.target_model_code = 'MODEL-e25f3089a99d4878a1ddd326ba0910bf'
      AND r.field_code = 'MODEL_e25f3089a99d4878a1ddd326ba0910bf_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-f1abcb10cae34b459f85e4135505a6a4',
  tm.id, 'MODEL-e5af81c45bbc44d48374d8036032cc5d',
  '所属设备管理', 'MODEL_e5af81c45bbc44d48374d8036032cc5d_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-f1abcb10cae34b459f85e4135505a6a4'
  AND tm.code = 'MODEL-e5af81c45bbc44d48374d8036032cc5d'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-f1abcb10cae34b459f85e4135505a6a4'
      AND r.target_model_code = 'MODEL-e5af81c45bbc44d48374d8036032cc5d'
      AND r.field_code = 'MODEL_e5af81c45bbc44d48374d8036032cc5d_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-f1abcb10cae34b459f85e4135505a6a4',
  tm.id, 'MODEL-e5c45ff8ca074102829bba872e091bd1',
  '所属设备管理', 'MODEL_e5c45ff8ca074102829bba872e091bd1_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-f1abcb10cae34b459f85e4135505a6a4'
  AND tm.code = 'MODEL-e5c45ff8ca074102829bba872e091bd1'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-f1abcb10cae34b459f85e4135505a6a4'
      AND r.target_model_code = 'MODEL-e5c45ff8ca074102829bba872e091bd1'
      AND r.field_code = 'MODEL_e5c45ff8ca074102829bba872e091bd1_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-f1abcb10cae34b459f85e4135505a6a4',
  tm.id, 'MODEL-f5270d478dd849389e59a178cd512629',
  '所属设备管理', 'MODEL_f5270d478dd849389e59a178cd512629_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-f1abcb10cae34b459f85e4135505a6a4'
  AND tm.code = 'MODEL-f5270d478dd849389e59a178cd512629'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-f1abcb10cae34b459f85e4135505a6a4'
      AND r.target_model_code = 'MODEL-f5270d478dd849389e59a178cd512629'
      AND r.field_code = 'MODEL_f5270d478dd849389e59a178cd512629_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-f1abcb10cae34b459f85e4135505a6a4',
  tm.id, 'MODEL-f55ce56d31f741caa5a69801556834fe',
  '所属设备管理', 'MODEL_f55ce56d31f741caa5a69801556834fe_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-f1abcb10cae34b459f85e4135505a6a4'
  AND tm.code = 'MODEL-f55ce56d31f741caa5a69801556834fe'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-f1abcb10cae34b459f85e4135505a6a4'
      AND r.target_model_code = 'MODEL-f55ce56d31f741caa5a69801556834fe'
      AND r.field_code = 'MODEL_f55ce56d31f741caa5a69801556834fe_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7',
  tm.id, 'MODEL-05e5c6a6c8fe4f3c9bd1dd86dd24a659',
  '所属设备管理', 'MODEL_05e5c6a6c8fe4f3c9bd1dd86dd24a659_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
  AND tm.code = 'MODEL-05e5c6a6c8fe4f3c9bd1dd86dd24a659'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
      AND r.target_model_code = 'MODEL-05e5c6a6c8fe4f3c9bd1dd86dd24a659'
      AND r.field_code = 'MODEL_05e5c6a6c8fe4f3c9bd1dd86dd24a659_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7',
  tm.id, 'MODEL-123328b7bfe745ceb337d286ac64aa17',
  '所属设备管理', 'MODEL_123328b7bfe745ceb337d286ac64aa17_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
  AND tm.code = 'MODEL-123328b7bfe745ceb337d286ac64aa17'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
      AND r.target_model_code = 'MODEL-123328b7bfe745ceb337d286ac64aa17'
      AND r.field_code = 'MODEL_123328b7bfe745ceb337d286ac64aa17_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7',
  tm.id, 'MODEL-17eb9be1c7404734af570ce040f1783b',
  '所属设备管理', 'MODEL_17eb9be1c7404734af570ce040f1783b_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
  AND tm.code = 'MODEL-17eb9be1c7404734af570ce040f1783b'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
      AND r.target_model_code = 'MODEL-17eb9be1c7404734af570ce040f1783b'
      AND r.field_code = 'MODEL_17eb9be1c7404734af570ce040f1783b_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7',
  tm.id, 'MODEL-26eabac35cb644cc84ae64dfb5ff9ba4',
  '所属设备管理', 'MODEL_26eabac35cb644cc84ae64dfb5ff9ba4_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
  AND tm.code = 'MODEL-26eabac35cb644cc84ae64dfb5ff9ba4'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
      AND r.target_model_code = 'MODEL-26eabac35cb644cc84ae64dfb5ff9ba4'
      AND r.field_code = 'MODEL_26eabac35cb644cc84ae64dfb5ff9ba4_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7',
  tm.id, 'MODEL-2af0515a5a36420086980b2322037edb',
  '所属设备管理', 'MODEL_2af0515a5a36420086980b2322037edb_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
  AND tm.code = 'MODEL-2af0515a5a36420086980b2322037edb'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
      AND r.target_model_code = 'MODEL-2af0515a5a36420086980b2322037edb'
      AND r.field_code = 'MODEL_2af0515a5a36420086980b2322037edb_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7',
  tm.id, 'MODEL-3130eba3747e4a5aa0862c94807822ba',
  '所属设备管理', 'MODEL_3130eba3747e4a5aa0862c94807822ba_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
  AND tm.code = 'MODEL-3130eba3747e4a5aa0862c94807822ba'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
      AND r.target_model_code = 'MODEL-3130eba3747e4a5aa0862c94807822ba'
      AND r.field_code = 'MODEL_3130eba3747e4a5aa0862c94807822ba_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7',
  tm.id, 'MODEL-44cc7511fc224085a51e2887569c933a',
  '所属设备管理', 'MODEL_44cc7511fc224085a51e2887569c933a_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
  AND tm.code = 'MODEL-44cc7511fc224085a51e2887569c933a'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
      AND r.target_model_code = 'MODEL-44cc7511fc224085a51e2887569c933a'
      AND r.field_code = 'MODEL_44cc7511fc224085a51e2887569c933a_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7',
  tm.id, 'MODEL-50991eab2a714ed288d00c58ff01c810',
  '所属设备管理', 'MODEL_50991eab2a714ed288d00c58ff01c810_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
  AND tm.code = 'MODEL-50991eab2a714ed288d00c58ff01c810'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
      AND r.target_model_code = 'MODEL-50991eab2a714ed288d00c58ff01c810'
      AND r.field_code = 'MODEL_50991eab2a714ed288d00c58ff01c810_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7',
  tm.id, 'MODEL-5a9b87c42571486998d363101c54ec8c',
  '所属设备管理', 'MODEL_5a9b87c42571486998d363101c54ec8c_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
  AND tm.code = 'MODEL-5a9b87c42571486998d363101c54ec8c'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
      AND r.target_model_code = 'MODEL-5a9b87c42571486998d363101c54ec8c'
      AND r.field_code = 'MODEL_5a9b87c42571486998d363101c54ec8c_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7',
  tm.id, 'MODEL-5f6f9243c7bb4e23bfed2fc01b48cf06',
  '所属设备管理', 'MODEL_5f6f9243c7bb4e23bfed2fc01b48cf06_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
  AND tm.code = 'MODEL-5f6f9243c7bb4e23bfed2fc01b48cf06'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
      AND r.target_model_code = 'MODEL-5f6f9243c7bb4e23bfed2fc01b48cf06'
      AND r.field_code = 'MODEL_5f6f9243c7bb4e23bfed2fc01b48cf06_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7',
  tm.id, 'MODEL-68554821800248cfacacbac1a53cdf37',
  '所属设备管理', 'MODEL_68554821800248cfacacbac1a53cdf37_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
  AND tm.code = 'MODEL-68554821800248cfacacbac1a53cdf37'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
      AND r.target_model_code = 'MODEL-68554821800248cfacacbac1a53cdf37'
      AND r.field_code = 'MODEL_68554821800248cfacacbac1a53cdf37_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7',
  tm.id, 'MODEL-6fd43ab74f6f425f81f0ad547603a849',
  '所属设备管理', 'MODEL_6fd43ab74f6f425f81f0ad547603a849_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
  AND tm.code = 'MODEL-6fd43ab74f6f425f81f0ad547603a849'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
      AND r.target_model_code = 'MODEL-6fd43ab74f6f425f81f0ad547603a849'
      AND r.field_code = 'MODEL_6fd43ab74f6f425f81f0ad547603a849_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7',
  tm.id, 'MODEL-70d2b066120c4b969f0b661d1d2fb664',
  '所属设备管理', 'MODEL_70d2b066120c4b969f0b661d1d2fb664_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
  AND tm.code = 'MODEL-70d2b066120c4b969f0b661d1d2fb664'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
      AND r.target_model_code = 'MODEL-70d2b066120c4b969f0b661d1d2fb664'
      AND r.field_code = 'MODEL_70d2b066120c4b969f0b661d1d2fb664_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7',
  tm.id, 'MODEL-7c4fe9b2d52d41cca5383488a4a400a5',
  '所属设备管理', 'MODEL_7c4fe9b2d52d41cca5383488a4a400a5_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
  AND tm.code = 'MODEL-7c4fe9b2d52d41cca5383488a4a400a5'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
      AND r.target_model_code = 'MODEL-7c4fe9b2d52d41cca5383488a4a400a5'
      AND r.field_code = 'MODEL_7c4fe9b2d52d41cca5383488a4a400a5_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7',
  tm.id, 'MODEL-7c746a60520347baaf64334625f07f31',
  '所属设备管理', 'MODEL_7c746a60520347baaf64334625f07f31_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
  AND tm.code = 'MODEL-7c746a60520347baaf64334625f07f31'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
      AND r.target_model_code = 'MODEL-7c746a60520347baaf64334625f07f31'
      AND r.field_code = 'MODEL_7c746a60520347baaf64334625f07f31_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7',
  tm.id, 'MODEL-81132ce00d2041c6a4ee06ec1c01dac8',
  '所属设备管理', 'MODEL_81132ce00d2041c6a4ee06ec1c01dac8_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
  AND tm.code = 'MODEL-81132ce00d2041c6a4ee06ec1c01dac8'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
      AND r.target_model_code = 'MODEL-81132ce00d2041c6a4ee06ec1c01dac8'
      AND r.field_code = 'MODEL_81132ce00d2041c6a4ee06ec1c01dac8_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7',
  tm.id, 'MODEL-88a38000121341e2950132da32baad90',
  '所属设备管理', 'MODEL_88a38000121341e2950132da32baad90_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
  AND tm.code = 'MODEL-88a38000121341e2950132da32baad90'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
      AND r.target_model_code = 'MODEL-88a38000121341e2950132da32baad90'
      AND r.field_code = 'MODEL_88a38000121341e2950132da32baad90_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7',
  tm.id, 'MODEL-990370e140aa481a82158bc96dc0b006',
  '所属设备管理', 'MODEL_990370e140aa481a82158bc96dc0b006_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
  AND tm.code = 'MODEL-990370e140aa481a82158bc96dc0b006'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
      AND r.target_model_code = 'MODEL-990370e140aa481a82158bc96dc0b006'
      AND r.field_code = 'MODEL_990370e140aa481a82158bc96dc0b006_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7',
  tm.id, 'MODEL-a4ce7668efe94d2fbc47a658be1a35d2',
  '所属设备管理', 'MODEL_a4ce7668efe94d2fbc47a658be1a35d2_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
  AND tm.code = 'MODEL-a4ce7668efe94d2fbc47a658be1a35d2'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
      AND r.target_model_code = 'MODEL-a4ce7668efe94d2fbc47a658be1a35d2'
      AND r.field_code = 'MODEL_a4ce7668efe94d2fbc47a658be1a35d2_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7',
  tm.id, 'MODEL-a62c727c0a024e19a8221c68fcf0c09a',
  '所属设备管理', 'MODEL_a62c727c0a024e19a8221c68fcf0c09a_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
  AND tm.code = 'MODEL-a62c727c0a024e19a8221c68fcf0c09a'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
      AND r.target_model_code = 'MODEL-a62c727c0a024e19a8221c68fcf0c09a'
      AND r.field_code = 'MODEL_a62c727c0a024e19a8221c68fcf0c09a_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7',
  tm.id, 'MODEL-b8251da851514341adef7f6f72995cf3',
  '所属设备管理', 'MODEL_b8251da851514341adef7f6f72995cf3_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
  AND tm.code = 'MODEL-b8251da851514341adef7f6f72995cf3'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
      AND r.target_model_code = 'MODEL-b8251da851514341adef7f6f72995cf3'
      AND r.field_code = 'MODEL_b8251da851514341adef7f6f72995cf3_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7',
  tm.id, 'MODEL-b83f05dc94f34fb4807458c0a48ba488',
  '所属设备管理', 'MODEL_b83f05dc94f34fb4807458c0a48ba488_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
  AND tm.code = 'MODEL-b83f05dc94f34fb4807458c0a48ba488'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
      AND r.target_model_code = 'MODEL-b83f05dc94f34fb4807458c0a48ba488'
      AND r.field_code = 'MODEL_b83f05dc94f34fb4807458c0a48ba488_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7',
  tm.id, 'MODEL-bc2cdffeab434f4d862c847fa99938ba',
  '所属设备管理', 'MODEL_bc2cdffeab434f4d862c847fa99938ba_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
  AND tm.code = 'MODEL-bc2cdffeab434f4d862c847fa99938ba'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
      AND r.target_model_code = 'MODEL-bc2cdffeab434f4d862c847fa99938ba'
      AND r.field_code = 'MODEL_bc2cdffeab434f4d862c847fa99938ba_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7',
  tm.id, 'MODEL-bf41bd50df044f5faf43c4759353ccd1',
  '所属设备管理', 'MODEL_bf41bd50df044f5faf43c4759353ccd1_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
  AND tm.code = 'MODEL-bf41bd50df044f5faf43c4759353ccd1'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
      AND r.target_model_code = 'MODEL-bf41bd50df044f5faf43c4759353ccd1'
      AND r.field_code = 'MODEL_bf41bd50df044f5faf43c4759353ccd1_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7',
  tm.id, 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9',
  '所属设备管理', 'MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
  AND tm.code = 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
      AND r.target_model_code = 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9'
      AND r.field_code = 'MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7',
  tm.id, 'MODEL-c6bc32646f3e4bde8409120c5c290eee',
  '所属设备管理', 'MODEL_c6bc32646f3e4bde8409120c5c290eee_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
  AND tm.code = 'MODEL-c6bc32646f3e4bde8409120c5c290eee'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
      AND r.target_model_code = 'MODEL-c6bc32646f3e4bde8409120c5c290eee'
      AND r.field_code = 'MODEL_c6bc32646f3e4bde8409120c5c290eee_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7',
  tm.id, 'MODEL-d446eef7329f45b4a374347e215fec5d',
  '所属设备管理', 'MODEL_d446eef7329f45b4a374347e215fec5d_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
  AND tm.code = 'MODEL-d446eef7329f45b4a374347e215fec5d'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
      AND r.target_model_code = 'MODEL-d446eef7329f45b4a374347e215fec5d'
      AND r.field_code = 'MODEL_d446eef7329f45b4a374347e215fec5d_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7',
  tm.id, 'MODEL-e25f3089a99d4878a1ddd326ba0910bf',
  '所属设备管理', 'MODEL_e25f3089a99d4878a1ddd326ba0910bf_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
  AND tm.code = 'MODEL-e25f3089a99d4878a1ddd326ba0910bf'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
      AND r.target_model_code = 'MODEL-e25f3089a99d4878a1ddd326ba0910bf'
      AND r.field_code = 'MODEL_e25f3089a99d4878a1ddd326ba0910bf_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7',
  tm.id, 'MODEL-e5af81c45bbc44d48374d8036032cc5d',
  '所属设备管理', 'MODEL_e5af81c45bbc44d48374d8036032cc5d_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
  AND tm.code = 'MODEL-e5af81c45bbc44d48374d8036032cc5d'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
      AND r.target_model_code = 'MODEL-e5af81c45bbc44d48374d8036032cc5d'
      AND r.field_code = 'MODEL_e5af81c45bbc44d48374d8036032cc5d_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7',
  tm.id, 'MODEL-e5c45ff8ca074102829bba872e091bd1',
  '所属设备管理', 'MODEL_e5c45ff8ca074102829bba872e091bd1_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
  AND tm.code = 'MODEL-e5c45ff8ca074102829bba872e091bd1'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
      AND r.target_model_code = 'MODEL-e5c45ff8ca074102829bba872e091bd1'
      AND r.field_code = 'MODEL_e5c45ff8ca074102829bba872e091bd1_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7',
  tm.id, 'MODEL-f5270d478dd849389e59a178cd512629',
  '所属设备管理', 'MODEL_f5270d478dd849389e59a178cd512629_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
  AND tm.code = 'MODEL-f5270d478dd849389e59a178cd512629'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
      AND r.target_model_code = 'MODEL-f5270d478dd849389e59a178cd512629'
      AND r.field_code = 'MODEL_f5270d478dd849389e59a178cd512629_id'
  );

INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  3,
  sm.id, 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7',
  tm.id, 'MODEL-f55ce56d31f741caa5a69801556834fe',
  '所属设备管理', 'MODEL_f55ce56d31f741caa5a69801556834fe_id',
  TRUE, 1, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = 1
WHERE sm.deleted = false AND sm.tenant_id = 1
  AND sm.code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
  AND tm.code = 'MODEL-f55ce56d31f741caa5a69801556834fe'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.source_model_code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
      AND r.target_model_code = 'MODEL-f55ce56d31f741caa5a69801556834fe'
      AND r.field_code = 'MODEL_f55ce56d31f741caa5a69801556834fe_id'
  );


-- dynamic_model_relation_declaration: 21 row(s), upsert by (model_code, target_business_type)

INSERT INTO dynamic_model_relation_declaration (model_id, target_business_type, tenant_id, creator)
SELECT m.id, 'equipment', 1, 'seed'
FROM dynamic_model m
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
ON CONFLICT (model_id, target_business_type, tenant_id) WHERE deleted = false
DO NOTHING;

INSERT INTO dynamic_model_relation_declaration (model_id, target_business_type, tenant_id, creator)
SELECT m.id, 'equipment', 1, 'seed'
FROM dynamic_model m
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'MODEL-92b4b6a9bc954beb80c3a4c248a6fc4a'
ON CONFLICT (model_id, target_business_type, tenant_id) WHERE deleted = false
DO NOTHING;

INSERT INTO dynamic_model_relation_declaration (model_id, target_business_type, tenant_id, creator)
SELECT m.id, 'equipment', 1, 'seed'
FROM dynamic_model m
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'MODEL-9f00c2ed9ca74a888364669af37523b0'
ON CONFLICT (model_id, target_business_type, tenant_id) WHERE deleted = false
DO NOTHING;

INSERT INTO dynamic_model_relation_declaration (model_id, target_business_type, tenant_id, creator)
SELECT m.id, 'equipment', 1, 'seed'
FROM dynamic_model m
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'MODEL-1b242833ff0e4d81b741e37ba125d543'
ON CONFLICT (model_id, target_business_type, tenant_id) WHERE deleted = false
DO NOTHING;

INSERT INTO dynamic_model_relation_declaration (model_id, target_business_type, tenant_id, creator)
SELECT m.id, 'equipment', 1, 'seed'
FROM dynamic_model m
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
ON CONFLICT (model_id, target_business_type, tenant_id) WHERE deleted = false
DO NOTHING;

INSERT INTO dynamic_model_relation_declaration (model_id, target_business_type, tenant_id, creator)
SELECT m.id, 'equipment', 1, 'seed'
FROM dynamic_model m
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
ON CONFLICT (model_id, target_business_type, tenant_id) WHERE deleted = false
DO NOTHING;

INSERT INTO dynamic_model_relation_declaration (model_id, target_business_type, tenant_id, creator)
SELECT m.id, 'equipment', 1, 'seed'
FROM dynamic_model m
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'MODEL-9ce1710bf842479db96b2e355746d289'
ON CONFLICT (model_id, target_business_type, tenant_id) WHERE deleted = false
DO NOTHING;

INSERT INTO dynamic_model_relation_declaration (model_id, target_business_type, tenant_id, creator)
SELECT m.id, 'equipment', 1, 'seed'
FROM dynamic_model m
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
ON CONFLICT (model_id, target_business_type, tenant_id) WHERE deleted = false
DO NOTHING;

INSERT INTO dynamic_model_relation_declaration (model_id, target_business_type, tenant_id, creator)
SELECT m.id, 'equipment', 1, 'seed'
FROM dynamic_model m
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
ON CONFLICT (model_id, target_business_type, tenant_id) WHERE deleted = false
DO NOTHING;

INSERT INTO dynamic_model_relation_declaration (model_id, target_business_type, tenant_id, creator)
SELECT m.id, 'equipment', 1, 'seed'
FROM dynamic_model m
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
ON CONFLICT (model_id, target_business_type, tenant_id) WHERE deleted = false
DO NOTHING;

INSERT INTO dynamic_model_relation_declaration (model_id, target_business_type, tenant_id, creator)
SELECT m.id, 'equipment', 1, 'seed'
FROM dynamic_model m
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'MODEL-8a091acfa63941459916e380e1e1020e'
ON CONFLICT (model_id, target_business_type, tenant_id) WHERE deleted = false
DO NOTHING;

INSERT INTO dynamic_model_relation_declaration (model_id, target_business_type, tenant_id, creator)
SELECT m.id, 'equipment', 1, 'seed'
FROM dynamic_model m
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'MODEL-f1abcb10cae34b459f85e4135505a6a4'
ON CONFLICT (model_id, target_business_type, tenant_id) WHERE deleted = false
DO NOTHING;

INSERT INTO dynamic_model_relation_declaration (model_id, target_business_type, tenant_id, creator)
SELECT m.id, 'equipment', 1, 'seed'
FROM dynamic_model m
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2'
ON CONFLICT (model_id, target_business_type, tenant_id) WHERE deleted = false
DO NOTHING;

INSERT INTO dynamic_model_relation_declaration (model_id, target_business_type, tenant_id, creator)
SELECT m.id, 'equipment', 1, 'seed'
FROM dynamic_model m
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad'
ON CONFLICT (model_id, target_business_type, tenant_id) WHERE deleted = false
DO NOTHING;

INSERT INTO dynamic_model_relation_declaration (model_id, target_business_type, tenant_id, creator)
SELECT m.id, 'equipment', 1, 'seed'
FROM dynamic_model m
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'MODEL-3aba3622dd75479f8dfc02228540cc80'
ON CONFLICT (model_id, target_business_type, tenant_id) WHERE deleted = false
DO NOTHING;

INSERT INTO dynamic_model_relation_declaration (model_id, target_business_type, tenant_id, creator)
SELECT m.id, 'equipment', 1, 'seed'
FROM dynamic_model m
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'MODEL-17c15377760a4656b487fb23d072c1a4'
ON CONFLICT (model_id, target_business_type, tenant_id) WHERE deleted = false
DO NOTHING;

INSERT INTO dynamic_model_relation_declaration (model_id, target_business_type, tenant_id, creator)
SELECT m.id, 'equipment', 1, 'seed'
FROM dynamic_model m
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7'
ON CONFLICT (model_id, target_business_type, tenant_id) WHERE deleted = false
DO NOTHING;

INSERT INTO dynamic_model_relation_declaration (model_id, target_business_type, tenant_id, creator)
SELECT m.id, 'equipment', 1, 'seed'
FROM dynamic_model m
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'MODEL-5de5a74896754c4e93786884405b9d89'
ON CONFLICT (model_id, target_business_type, tenant_id) WHERE deleted = false
DO NOTHING;

INSERT INTO dynamic_model_relation_declaration (model_id, target_business_type, tenant_id, creator)
SELECT m.id, 'equipment', 1, 'seed'
FROM dynamic_model m
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'MODEL-5bf9561d71624c43af36d4411520bb8d'
ON CONFLICT (model_id, target_business_type, tenant_id) WHERE deleted = false
DO NOTHING;

INSERT INTO dynamic_model_relation_declaration (model_id, target_business_type, tenant_id, creator)
SELECT m.id, 'equipment', 1, 'seed'
FROM dynamic_model m
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'MODEL-8c85f68408244a2db7c04cfc00345e2f'
ON CONFLICT (model_id, target_business_type, tenant_id) WHERE deleted = false
DO NOTHING;

INSERT INTO dynamic_model_relation_declaration (model_id, target_business_type, tenant_id, creator)
SELECT m.id, 'equipment', 1, 'seed'
FROM dynamic_model m
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'MODEL-07c955e48d9e4cc09916238a4905a36f'
ON CONFLICT (model_id, target_business_type, tenant_id) WHERE deleted = false
DO NOTHING;


-- dynamic_model_field_assignment: 315 row(s), resolve model_id/field_id by code

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, FALSE,
  NULL, NULL,
  1, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-6bfa9a8c9be648cebc21737115690e21'
  AND f.code = 'F-b035eeaa884243269178a06374ae8438'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, FALSE,
  NULL, NULL,
  2, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-6bfa9a8c9be648cebc21737115690e21'
  AND f.code = 'F-ed97f195ccbe495d8766026635e4f5e3'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, FALSE,
  NULL, NULL,
  3, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-6bfa9a8c9be648cebc21737115690e21'
  AND f.code = 'F-e8289a51a3dd41c6b7f1539efd003258'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, FALSE,
  NULL, NULL,
  4, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-6bfa9a8c9be648cebc21737115690e21'
  AND f.code = 'F-8e92b92f84e74cfd853cdd8cf4f6361a'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, FALSE,
  NULL, NULL,
  5, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-6bfa9a8c9be648cebc21737115690e21'
  AND f.code = 'F-c367374d455f4024b29560be44f7c031'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  6, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-6bfa9a8c9be648cebc21737115690e21'
  AND f.code = 'F-4f3b40defe104f1b94c3462c568bfb79'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  7, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-6bfa9a8c9be648cebc21737115690e21'
  AND f.code = 'F-1d3bcec7a5df4ff985610db69a2c467b'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  8, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-6bfa9a8c9be648cebc21737115690e21'
  AND f.code = 'F-3071c55d68ac4e03872d9758512aa17e'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  9, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-6bfa9a8c9be648cebc21737115690e21'
  AND f.code = 'F-443c8879f02b479e8af045a80c9fdcca'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  10, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-6bfa9a8c9be648cebc21737115690e21'
  AND f.code = 'F-0436a31b31dc4b4d8a5314177e254312'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, FALSE,
  NULL, NULL,
  11, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-6bfa9a8c9be648cebc21737115690e21'
  AND f.code = 'F-7e858ec039604fd5841722ddf3d3176a'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, FALSE,
  NULL, NULL,
  12, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-6bfa9a8c9be648cebc21737115690e21'
  AND f.code = 'F-8bf33d80787a411cb398f74c54b7843b'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  13, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-6bfa9a8c9be648cebc21737115690e21'
  AND f.code = 'F-40617730a826424faa4b40ccd8a58414'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  14, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-6bfa9a8c9be648cebc21737115690e21'
  AND f.code = 'F-1d9efb587ce0496aa8a967ebd7216627'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, FALSE,
  NULL, NULL,
  15, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-6bfa9a8c9be648cebc21737115690e21'
  AND f.code = 'F-209f806919c0492b97ee9a8aa98aaee9'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  16, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-6bfa9a8c9be648cebc21737115690e21'
  AND f.code = 'F-6e001ac5256943b6840f5b9a3a17ac0b'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  17, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-6bfa9a8c9be648cebc21737115690e21'
  AND f.code = 'F-524fd7c27947494384391f83aabbb9e6'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  18, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-6bfa9a8c9be648cebc21737115690e21'
  AND f.code = 'F-224b4af4e1474d5d85002c03d0bdf89a'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  19, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-6bfa9a8c9be648cebc21737115690e21'
  AND f.code = 'F-3c6a384288b84d4e9086206ff2673eb3'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  20, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-6bfa9a8c9be648cebc21737115690e21'
  AND f.code = 'F-d9b5d2f7cb524d17bb2223a9e5a56f4c'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  21, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-6bfa9a8c9be648cebc21737115690e21'
  AND f.code = 'F-ec6b7dec58434d76a6b0e9aeb79c197b'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  22, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-6bfa9a8c9be648cebc21737115690e21'
  AND f.code = 'F-92d768ce991649fca38c345cf9959e05'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  23, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-6bfa9a8c9be648cebc21737115690e21'
  AND f.code = 'F-39ab7f71e1d945fcbe6af9c4fbae1b25'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  24, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-6bfa9a8c9be648cebc21737115690e21'
  AND f.code = 'F-6116465add0949b593f1dcd3f2b6e29d'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  25, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-6bfa9a8c9be648cebc21737115690e21'
  AND f.code = 'F-cefb517417c643cdbd044929d1fe03f5'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  26, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-6bfa9a8c9be648cebc21737115690e21'
  AND f.code = 'F-3dc0dc43a3894d698a38c2550ce47fdb'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  27, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-6bfa9a8c9be648cebc21737115690e21'
  AND f.code = 'F-31d7dc332d2846c9837fb0d7f9e2ec51'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  28, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-6bfa9a8c9be648cebc21737115690e21'
  AND f.code = 'F-e30a052673b6438d86ee0a99585cb9bb'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  29, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-6bfa9a8c9be648cebc21737115690e21'
  AND f.code = 'F-c8dc3d61b1494b61a1197065fa41501d'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, FALSE,
  NULL, NULL,
  30, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-6bfa9a8c9be648cebc21737115690e21'
  AND f.code = 'F-65e1d5ebc4e4442d9483aa3f4313f7a9'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  31, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-6bfa9a8c9be648cebc21737115690e21'
  AND f.code = 'F-981ca5abfa2840eeb5b4a97918029e9f'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  32, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-6bfa9a8c9be648cebc21737115690e21'
  AND f.code = 'F-dc7aaac23a384e4b85ea70d2c35efe4a'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  33, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-6bfa9a8c9be648cebc21737115690e21'
  AND f.code = 'F-9d05b9b3a3d24a6da5a41c8143bd448e'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  34, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-6bfa9a8c9be648cebc21737115690e21'
  AND f.code = 'F-3d37caf40b634e3c9527bba283d92622'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  35, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-6bfa9a8c9be648cebc21737115690e21'
  AND f.code = 'F-0b1047d4121b4b23af936cf6e6f06b7b'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  36, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-6bfa9a8c9be648cebc21737115690e21'
  AND f.code = 'F-7c6e9af941d04fcfbb36dbe80134f119'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, FALSE,
  NULL, NULL,
  37, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-6bfa9a8c9be648cebc21737115690e21'
  AND f.code = 'F-5c4fbd6a48ca457ba09a97c4ccea8e71'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  38, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-6bfa9a8c9be648cebc21737115690e21'
  AND f.code = 'F-b8a976c165824f848daac3c7bbe0fa1f'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  1, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
  AND f.code = 'F-0054227dff284d18be8cb7a59c9f9208'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  2, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
  AND f.code = 'F-6bb56dc7ac014d1ea68632a1ac606dfc'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, FALSE,
  NULL, NULL,
  3, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
  AND f.code = 'F-ada5d166441647b39b0fa3a0aa45def7'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  4, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
  AND f.code = 'F-b20b046c96044c61998ade2424c45c20'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, FALSE,
  NULL, NULL,
  5, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
  AND f.code = 'F-e8b81a761f914a6d9384a620c4ec9565'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, FALSE,
  NULL, NULL,
  6, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
  AND f.code = 'F-b8b4fa8b0a4342348f69257aaa2101df'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  7, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
  AND f.code = 'F-c89855b21acd4cae870535cc1e29b3f9'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, FALSE,
  NULL, NULL,
  8, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
  AND f.code = 'F-24442601845a4c7eac78f96b037da45c'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  9, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
  AND f.code = 'F-95373b75983148d7aaa6b83e36cd6c5b'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, FALSE,
  NULL, NULL,
  11, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
  AND f.code = 'F-e43b835f6f59492ab90a33abd53d9062'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  12, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
  AND f.code = 'F-7afc7657a00849c99087fad813f904b6'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, FALSE,
  NULL, NULL,
  13, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
  AND f.code = 'F-59e3752f5d954a818b71effb661ed0a0'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  14, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
  AND f.code = 'F-5aa8a20200ca4227962015c4ea17e09f'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  15, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
  AND f.code = 'F-6e612c0931ad42b3b308898469497eec'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  16, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
  AND f.code = 'F-e9a39f2dbe0d425abea3ee1d4e911b6d'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  999, NULL,
  'USER_ADDED', NULL,
  (
  SELECT r.id FROM dynamic_model_relation r
  WHERE r.deleted = false AND r.tenant_id = 1
    AND r.source_model_code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
    AND r.target_model_code = 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9'
    AND r.field_code = 'MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id'
  LIMIT 1
), 'equipment', 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-51f858c2280c4935b96055fd1b60a5e0'
  AND f.code = 'REF-MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  1, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-92b4b6a9bc954beb80c3a4c248a6fc4a'
  AND f.code = 'F-b4677ba419b14289824604c3f30d2ea5'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  2, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-92b4b6a9bc954beb80c3a4c248a6fc4a'
  AND f.code = 'F-0054227dff284d18be8cb7a59c9f9208'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  3, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-92b4b6a9bc954beb80c3a4c248a6fc4a'
  AND f.code = 'F-08a164e893064fc090f611deca4654ab'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  4, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-92b4b6a9bc954beb80c3a4c248a6fc4a'
  AND f.code = 'F-c89855b21acd4cae870535cc1e29b3f9'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, FALSE,
  NULL, NULL,
  5, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-92b4b6a9bc954beb80c3a4c248a6fc4a'
  AND f.code = 'F-24442601845a4c7eac78f96b037da45c'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, FALSE,
  NULL, NULL,
  6, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-92b4b6a9bc954beb80c3a4c248a6fc4a'
  AND f.code = 'F-9489c5ee50f5496d9128e6202987f83c'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, FALSE,
  NULL, NULL,
  7, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-92b4b6a9bc954beb80c3a4c248a6fc4a'
  AND f.code = 'F-bcb94455381c4776a9f2ccadf0a04897'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, FALSE,
  NULL, NULL,
  8, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-92b4b6a9bc954beb80c3a4c248a6fc4a'
  AND f.code = 'F-1011bb8c2edb4ee38e7502fb1435b591'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, FALSE,
  NULL, NULL,
  9, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-92b4b6a9bc954beb80c3a4c248a6fc4a'
  AND f.code = 'F-771506dcac4d4ee08dc3e39ab5cf3a18'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, FALSE,
  NULL, NULL,
  10, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-92b4b6a9bc954beb80c3a4c248a6fc4a'
  AND f.code = 'F-64a9042c3a464ffda9040119e4c8f0af'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, FALSE,
  NULL, NULL,
  11, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-92b4b6a9bc954beb80c3a4c248a6fc4a'
  AND f.code = 'F-e81062a8ef8d4e8eb847543ca50d9279'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, FALSE,
  NULL, NULL,
  12, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-92b4b6a9bc954beb80c3a4c248a6fc4a'
  AND f.code = 'F-c7d5a8c7a9904e5a9d11492072e922b6'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  13, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-92b4b6a9bc954beb80c3a4c248a6fc4a'
  AND f.code = 'F-95373b75983148d7aaa6b83e36cd6c5b'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  14, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-92b4b6a9bc954beb80c3a4c248a6fc4a'
  AND f.code = 'F-8ae17b99e1b44e8dbacbb68645bf4d7c'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, FALSE,
  NULL, NULL,
  15, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-92b4b6a9bc954beb80c3a4c248a6fc4a'
  AND f.code = 'F-9abe403c0a71458586572a4ea0837b03'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  16, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-92b4b6a9bc954beb80c3a4c248a6fc4a'
  AND f.code = 'F-c9c3f651b8e048c0a78521ae34a18f60'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  17, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-92b4b6a9bc954beb80c3a4c248a6fc4a'
  AND f.code = 'F-5bf95bdfcf4b459a84aa21b76a3ad965'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  18, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-92b4b6a9bc954beb80c3a4c248a6fc4a'
  AND f.code = 'F-49e264c6d87f4498b3c8e110f423b3a0'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  19, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-92b4b6a9bc954beb80c3a4c248a6fc4a'
  AND f.code = 'F-3775e93edf70456db41c72ecc15d5c9b'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  20, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-92b4b6a9bc954beb80c3a4c248a6fc4a'
  AND f.code = 'F-f3dd829ea93b4666963866d6340d9d8a'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, FALSE,
  NULL, NULL,
  21, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-92b4b6a9bc954beb80c3a4c248a6fc4a'
  AND f.code = 'F-b86dc8d38487498d98f7e6901c459836'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  22, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-92b4b6a9bc954beb80c3a4c248a6fc4a'
  AND f.code = 'F-c22a9c186dcc452f98cd7eaa5f3f565c'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  23, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-92b4b6a9bc954beb80c3a4c248a6fc4a'
  AND f.code = 'F-684f5feb7c5d4988871dcf14ad06812f'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  24, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-92b4b6a9bc954beb80c3a4c248a6fc4a'
  AND f.code = 'F-e9a39f2dbe0d425abea3ee1d4e911b6d'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  999, NULL,
  'USER_ADDED', NULL,
  (
  SELECT r.id FROM dynamic_model_relation r
  WHERE r.deleted = false AND r.tenant_id = 1
    AND r.source_model_code = 'MODEL-92b4b6a9bc954beb80c3a4c248a6fc4a'
    AND r.target_model_code = 'MODEL-7c4fe9b2d52d41cca5383488a4a400a5'
    AND r.field_code = 'MODEL_7c4fe9b2d52d41cca5383488a4a400a5_id'
  LIMIT 1
), 'equipment', 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-92b4b6a9bc954beb80c3a4c248a6fc4a'
  AND f.code = 'REF-MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  1, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-9f00c2ed9ca74a888364669af37523b0'
  AND f.code = 'F-0054227dff284d18be8cb7a59c9f9208'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  2, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-9f00c2ed9ca74a888364669af37523b0'
  AND f.code = 'F-b5fdba42c7814e93a0a4966af64af608'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  3, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-9f00c2ed9ca74a888364669af37523b0'
  AND f.code = 'F-08a164e893064fc090f611deca4654ab'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  4, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-9f00c2ed9ca74a888364669af37523b0'
  AND f.code = 'F-c89855b21acd4cae870535cc1e29b3f9'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, FALSE,
  NULL, NULL,
  5, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-9f00c2ed9ca74a888364669af37523b0'
  AND f.code = 'F-24442601845a4c7eac78f96b037da45c'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, FALSE,
  NULL, NULL,
  6, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-9f00c2ed9ca74a888364669af37523b0'
  AND f.code = 'F-0ddaa2df8e4645fcbb71d0a425e25e5e'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  7, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-9f00c2ed9ca74a888364669af37523b0'
  AND f.code = 'F-1920066551d944b3b7fd8b0fc7e1c094'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  8, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-9f00c2ed9ca74a888364669af37523b0'
  AND f.code = 'F-e8c7851b5ade42feb7f72eb1ab59b3b5'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  9, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-9f00c2ed9ca74a888364669af37523b0'
  AND f.code = 'F-2908d7224a2341e39f413e4905c6a46a'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  10, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-9f00c2ed9ca74a888364669af37523b0'
  AND f.code = 'F-652ef6ef44c247958f9d937722aa4e18'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  11, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-9f00c2ed9ca74a888364669af37523b0'
  AND f.code = 'F-95373b75983148d7aaa6b83e36cd6c5b'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  12, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-9f00c2ed9ca74a888364669af37523b0'
  AND f.code = 'F-5d2f22057f0f4b50b6b0e24d2dc86a66'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, FALSE,
  NULL, NULL,
  13, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-9f00c2ed9ca74a888364669af37523b0'
  AND f.code = 'F-737b95fd043748b3a565edbd6735733e'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, FALSE,
  NULL, NULL,
  14, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-9f00c2ed9ca74a888364669af37523b0'
  AND f.code = 'F-cd09db6ca3134996a6468594f64bc97e'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  15, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-9f00c2ed9ca74a888364669af37523b0'
  AND f.code = 'F-4c2787cd47d74621a3a8868c9bad729b'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  16, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-9f00c2ed9ca74a888364669af37523b0'
  AND f.code = 'F-66324f09cb7e4913a6af86b1e4f7c7b7'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  17, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-9f00c2ed9ca74a888364669af37523b0'
  AND f.code = 'F-778eb98265914d7b8e65932a1aa0c355'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, FALSE,
  NULL, NULL,
  18, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-9f00c2ed9ca74a888364669af37523b0'
  AND f.code = 'F-28655458c4bc41d680b78576cc9cfd70'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, FALSE,
  NULL, NULL,
  19, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-9f00c2ed9ca74a888364669af37523b0'
  AND f.code = 'F-e43b835f6f59492ab90a33abd53d9062'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, FALSE,
  NULL, NULL,
  20, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-9f00c2ed9ca74a888364669af37523b0'
  AND f.code = 'F-aedecf6a99c248b689c6647ac230ac91'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  21, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-9f00c2ed9ca74a888364669af37523b0'
  AND f.code = 'F-e9a39f2dbe0d425abea3ee1d4e911b6d'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  999, NULL,
  'USER_ADDED', NULL,
  (
  SELECT r.id FROM dynamic_model_relation r
  WHERE r.deleted = false AND r.tenant_id = 1
    AND r.source_model_code = 'MODEL-9f00c2ed9ca74a888364669af37523b0'
    AND r.target_model_code = 'MODEL-7c4fe9b2d52d41cca5383488a4a400a5'
    AND r.field_code = 'MODEL_7c4fe9b2d52d41cca5383488a4a400a5_id'
  LIMIT 1
), 'equipment', 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-9f00c2ed9ca74a888364669af37523b0'
  AND f.code = 'REF-MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  1, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-1b242833ff0e4d81b741e37ba125d543'
  AND f.code = 'F-0054227dff284d18be8cb7a59c9f9208'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  2, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-1b242833ff0e4d81b741e37ba125d543'
  AND f.code = 'F-b5fdba42c7814e93a0a4966af64af608'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  3, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-1b242833ff0e4d81b741e37ba125d543'
  AND f.code = 'F-08a164e893064fc090f611deca4654ab'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  4, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-1b242833ff0e4d81b741e37ba125d543'
  AND f.code = 'F-c89855b21acd4cae870535cc1e29b3f9'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, FALSE,
  NULL, NULL,
  5, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-1b242833ff0e4d81b741e37ba125d543'
  AND f.code = 'F-24442601845a4c7eac78f96b037da45c'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, FALSE,
  NULL, NULL,
  6, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-1b242833ff0e4d81b741e37ba125d543'
  AND f.code = 'F-0ddaa2df8e4645fcbb71d0a425e25e5e'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  7, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-1b242833ff0e4d81b741e37ba125d543'
  AND f.code = 'F-1920066551d944b3b7fd8b0fc7e1c094'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  8, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-1b242833ff0e4d81b741e37ba125d543'
  AND f.code = 'F-e8c7851b5ade42feb7f72eb1ab59b3b5'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  9, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-1b242833ff0e4d81b741e37ba125d543'
  AND f.code = 'F-2908d7224a2341e39f413e4905c6a46a'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  10, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-1b242833ff0e4d81b741e37ba125d543'
  AND f.code = 'F-652ef6ef44c247958f9d937722aa4e18'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  11, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-1b242833ff0e4d81b741e37ba125d543'
  AND f.code = 'F-95373b75983148d7aaa6b83e36cd6c5b'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  12, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-1b242833ff0e4d81b741e37ba125d543'
  AND f.code = 'F-5d2f22057f0f4b50b6b0e24d2dc86a66'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, FALSE,
  NULL, NULL,
  13, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-1b242833ff0e4d81b741e37ba125d543'
  AND f.code = 'F-737b95fd043748b3a565edbd6735733e'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, FALSE,
  NULL, NULL,
  14, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-1b242833ff0e4d81b741e37ba125d543'
  AND f.code = 'F-cd09db6ca3134996a6468594f64bc97e'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  15, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-1b242833ff0e4d81b741e37ba125d543'
  AND f.code = 'F-4c2787cd47d74621a3a8868c9bad729b'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  16, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-1b242833ff0e4d81b741e37ba125d543'
  AND f.code = 'F-66324f09cb7e4913a6af86b1e4f7c7b7'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  17, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-1b242833ff0e4d81b741e37ba125d543'
  AND f.code = 'F-778eb98265914d7b8e65932a1aa0c355'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, FALSE,
  NULL, NULL,
  18, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-1b242833ff0e4d81b741e37ba125d543'
  AND f.code = 'F-28655458c4bc41d680b78576cc9cfd70'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, FALSE,
  NULL, NULL,
  19, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-1b242833ff0e4d81b741e37ba125d543'
  AND f.code = 'F-e43b835f6f59492ab90a33abd53d9062'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, FALSE,
  NULL, NULL,
  20, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-1b242833ff0e4d81b741e37ba125d543'
  AND f.code = 'F-aedecf6a99c248b689c6647ac230ac91'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  21, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-1b242833ff0e4d81b741e37ba125d543'
  AND f.code = 'F-e9a39f2dbe0d425abea3ee1d4e911b6d'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  999, NULL,
  'USER_ADDED', NULL,
  (
  SELECT r.id FROM dynamic_model_relation r
  WHERE r.deleted = false AND r.tenant_id = 1
    AND r.source_model_code = 'MODEL-1b242833ff0e4d81b741e37ba125d543'
    AND r.target_model_code = 'MODEL-7c4fe9b2d52d41cca5383488a4a400a5'
    AND r.field_code = 'MODEL_7c4fe9b2d52d41cca5383488a4a400a5_id'
  LIMIT 1
), 'equipment', 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-1b242833ff0e4d81b741e37ba125d543'
  AND f.code = 'REF-MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  1, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
  AND f.code = 'F-0054227dff284d18be8cb7a59c9f9208'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  2, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
  AND f.code = 'F-b5fdba42c7814e93a0a4966af64af608'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  3, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
  AND f.code = 'F-08a164e893064fc090f611deca4654ab'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  4, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
  AND f.code = 'F-c89855b21acd4cae870535cc1e29b3f9'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, FALSE,
  NULL, NULL,
  5, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
  AND f.code = 'F-24442601845a4c7eac78f96b037da45c'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, FALSE,
  NULL, NULL,
  6, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
  AND f.code = 'F-0ddaa2df8e4645fcbb71d0a425e25e5e'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  7, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
  AND f.code = 'F-1920066551d944b3b7fd8b0fc7e1c094'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  8, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
  AND f.code = 'F-e8c7851b5ade42feb7f72eb1ab59b3b5'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  9, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
  AND f.code = 'F-2908d7224a2341e39f413e4905c6a46a'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  10, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
  AND f.code = 'F-652ef6ef44c247958f9d937722aa4e18'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  11, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
  AND f.code = 'F-95373b75983148d7aaa6b83e36cd6c5b'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  12, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
  AND f.code = 'F-5d2f22057f0f4b50b6b0e24d2dc86a66'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, FALSE,
  NULL, NULL,
  13, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
  AND f.code = 'F-737b95fd043748b3a565edbd6735733e'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, FALSE,
  NULL, NULL,
  14, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
  AND f.code = 'F-cd09db6ca3134996a6468594f64bc97e'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  15, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
  AND f.code = 'F-4c2787cd47d74621a3a8868c9bad729b'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  16, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
  AND f.code = 'F-66324f09cb7e4913a6af86b1e4f7c7b7'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  17, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
  AND f.code = 'F-778eb98265914d7b8e65932a1aa0c355'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, FALSE,
  NULL, NULL,
  18, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
  AND f.code = 'F-28655458c4bc41d680b78576cc9cfd70'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, FALSE,
  NULL, NULL,
  19, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
  AND f.code = 'F-e43b835f6f59492ab90a33abd53d9062'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, TRUE,
  NULL, NULL,
  20, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
  AND f.code = 'F-67aeb9609c7945cfaa3f5806e27e6d90'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  21, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
  AND f.code = 'F-e9a39f2dbe0d425abea3ee1d4e911b6d'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  999, NULL,
  'USER_ADDED', NULL,
  (
  SELECT r.id FROM dynamic_model_relation r
  WHERE r.deleted = false AND r.tenant_id = 1
    AND r.source_model_code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
    AND r.target_model_code = 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9'
    AND r.field_code = 'MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id'
  LIMIT 1
), 'equipment', 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-fb322a4355994daa8f1b2e2c2f9adba7'
  AND f.code = 'REF-MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  1, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
  AND f.code = 'F-0054227dff284d18be8cb7a59c9f9208'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  2, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
  AND f.code = 'F-b5fdba42c7814e93a0a4966af64af608'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  3, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
  AND f.code = 'F-08a164e893064fc090f611deca4654ab'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  4, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
  AND f.code = 'F-c89855b21acd4cae870535cc1e29b3f9'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, FALSE,
  NULL, NULL,
  5, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
  AND f.code = 'F-24442601845a4c7eac78f96b037da45c'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, FALSE,
  NULL, NULL,
  6, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
  AND f.code = 'F-0ddaa2df8e4645fcbb71d0a425e25e5e'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  7, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
  AND f.code = 'F-1920066551d944b3b7fd8b0fc7e1c094'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  8, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
  AND f.code = 'F-e8c7851b5ade42feb7f72eb1ab59b3b5'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  9, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
  AND f.code = 'F-2908d7224a2341e39f413e4905c6a46a'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  10, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
  AND f.code = 'F-652ef6ef44c247958f9d937722aa4e18'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  11, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
  AND f.code = 'F-95373b75983148d7aaa6b83e36cd6c5b'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  12, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
  AND f.code = 'F-5d2f22057f0f4b50b6b0e24d2dc86a66'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, FALSE,
  NULL, NULL,
  13, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
  AND f.code = 'F-737b95fd043748b3a565edbd6735733e'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, FALSE,
  NULL, NULL,
  14, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
  AND f.code = 'F-cd09db6ca3134996a6468594f64bc97e'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  15, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
  AND f.code = 'F-4c2787cd47d74621a3a8868c9bad729b'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  16, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
  AND f.code = 'F-66324f09cb7e4913a6af86b1e4f7c7b7'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  17, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
  AND f.code = 'F-778eb98265914d7b8e65932a1aa0c355'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, FALSE,
  NULL, NULL,
  18, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
  AND f.code = 'F-28655458c4bc41d680b78576cc9cfd70'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, FALSE,
  NULL, NULL,
  19, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
  AND f.code = 'F-e43b835f6f59492ab90a33abd53d9062'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, TRUE,
  NULL, NULL,
  20, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
  AND f.code = 'F-67aeb9609c7945cfaa3f5806e27e6d90'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  21, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
  AND f.code = 'F-e9a39f2dbe0d425abea3ee1d4e911b6d'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  999, NULL,
  'USER_ADDED', NULL,
  (
  SELECT r.id FROM dynamic_model_relation r
  WHERE r.deleted = false AND r.tenant_id = 1
    AND r.source_model_code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
    AND r.target_model_code = 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9'
    AND r.field_code = 'MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id'
  LIMIT 1
), 'equipment', 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-ef8378fb5b6f41619866e90ef82bc4d9'
  AND f.code = 'REF-MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  1, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-9ce1710bf842479db96b2e355746d289'
  AND f.code = 'F-0054227dff284d18be8cb7a59c9f9208'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  2, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-9ce1710bf842479db96b2e355746d289'
  AND f.code = 'F-b5fdba42c7814e93a0a4966af64af608'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  3, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-9ce1710bf842479db96b2e355746d289'
  AND f.code = 'F-08a164e893064fc090f611deca4654ab'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  4, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-9ce1710bf842479db96b2e355746d289'
  AND f.code = 'F-c89855b21acd4cae870535cc1e29b3f9'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, FALSE,
  NULL, NULL,
  5, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-9ce1710bf842479db96b2e355746d289'
  AND f.code = 'F-24442601845a4c7eac78f96b037da45c'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, FALSE,
  NULL, NULL,
  6, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-9ce1710bf842479db96b2e355746d289'
  AND f.code = 'F-0ddaa2df8e4645fcbb71d0a425e25e5e'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  7, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-9ce1710bf842479db96b2e355746d289'
  AND f.code = 'F-1920066551d944b3b7fd8b0fc7e1c094'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  8, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-9ce1710bf842479db96b2e355746d289'
  AND f.code = 'F-e8c7851b5ade42feb7f72eb1ab59b3b5'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  9, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-9ce1710bf842479db96b2e355746d289'
  AND f.code = 'F-2908d7224a2341e39f413e4905c6a46a'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  10, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-9ce1710bf842479db96b2e355746d289'
  AND f.code = 'F-652ef6ef44c247958f9d937722aa4e18'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  11, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-9ce1710bf842479db96b2e355746d289'
  AND f.code = 'F-95373b75983148d7aaa6b83e36cd6c5b'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  12, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-9ce1710bf842479db96b2e355746d289'
  AND f.code = 'F-5d2f22057f0f4b50b6b0e24d2dc86a66'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, FALSE,
  NULL, NULL,
  13, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-9ce1710bf842479db96b2e355746d289'
  AND f.code = 'F-737b95fd043748b3a565edbd6735733e'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, FALSE,
  NULL, NULL,
  14, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-9ce1710bf842479db96b2e355746d289'
  AND f.code = 'F-cd09db6ca3134996a6468594f64bc97e'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  15, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-9ce1710bf842479db96b2e355746d289'
  AND f.code = 'F-4c2787cd47d74621a3a8868c9bad729b'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  16, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-9ce1710bf842479db96b2e355746d289'
  AND f.code = 'F-66324f09cb7e4913a6af86b1e4f7c7b7'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  17, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-9ce1710bf842479db96b2e355746d289'
  AND f.code = 'F-778eb98265914d7b8e65932a1aa0c355'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, FALSE,
  NULL, NULL,
  18, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-9ce1710bf842479db96b2e355746d289'
  AND f.code = 'F-28655458c4bc41d680b78576cc9cfd70'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, FALSE,
  NULL, NULL,
  19, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-9ce1710bf842479db96b2e355746d289'
  AND f.code = 'F-e43b835f6f59492ab90a33abd53d9062'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, TRUE,
  NULL, NULL,
  20, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-9ce1710bf842479db96b2e355746d289'
  AND f.code = 'F-67aeb9609c7945cfaa3f5806e27e6d90'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  21, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-9ce1710bf842479db96b2e355746d289'
  AND f.code = 'F-e9a39f2dbe0d425abea3ee1d4e911b6d'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  999, NULL,
  'USER_ADDED', NULL,
  (
  SELECT r.id FROM dynamic_model_relation r
  WHERE r.deleted = false AND r.tenant_id = 1
    AND r.source_model_code = 'MODEL-9ce1710bf842479db96b2e355746d289'
    AND r.target_model_code = 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9'
    AND r.field_code = 'MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id'
  LIMIT 1
), 'equipment', 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-9ce1710bf842479db96b2e355746d289'
  AND f.code = 'REF-MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  1, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
  AND f.code = 'F-0054227dff284d18be8cb7a59c9f9208'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  2, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
  AND f.code = 'F-b5fdba42c7814e93a0a4966af64af608'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  3, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
  AND f.code = 'F-08a164e893064fc090f611deca4654ab'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  4, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
  AND f.code = 'F-c89855b21acd4cae870535cc1e29b3f9'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, FALSE,
  NULL, NULL,
  5, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
  AND f.code = 'F-24442601845a4c7eac78f96b037da45c'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, FALSE,
  NULL, NULL,
  6, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
  AND f.code = 'F-0ddaa2df8e4645fcbb71d0a425e25e5e'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  7, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
  AND f.code = 'F-1920066551d944b3b7fd8b0fc7e1c094'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  8, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
  AND f.code = 'F-e8c7851b5ade42feb7f72eb1ab59b3b5'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  9, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
  AND f.code = 'F-2908d7224a2341e39f413e4905c6a46a'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  10, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
  AND f.code = 'F-652ef6ef44c247958f9d937722aa4e18'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  11, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
  AND f.code = 'F-95373b75983148d7aaa6b83e36cd6c5b'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  12, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
  AND f.code = 'F-5d2f22057f0f4b50b6b0e24d2dc86a66'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, FALSE,
  NULL, NULL,
  13, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
  AND f.code = 'F-737b95fd043748b3a565edbd6735733e'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, FALSE,
  NULL, NULL,
  14, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
  AND f.code = 'F-cd09db6ca3134996a6468594f64bc97e'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  15, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
  AND f.code = 'F-4c2787cd47d74621a3a8868c9bad729b'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  16, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
  AND f.code = 'F-66324f09cb7e4913a6af86b1e4f7c7b7'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  17, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
  AND f.code = 'F-778eb98265914d7b8e65932a1aa0c355'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, FALSE,
  NULL, NULL,
  18, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
  AND f.code = 'F-28655458c4bc41d680b78576cc9cfd70'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, FALSE,
  NULL, NULL,
  19, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
  AND f.code = 'F-e43b835f6f59492ab90a33abd53d9062'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, TRUE,
  NULL, NULL,
  20, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
  AND f.code = 'F-67aeb9609c7945cfaa3f5806e27e6d90'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  21, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
  AND f.code = 'F-e9a39f2dbe0d425abea3ee1d4e911b6d'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  999, NULL,
  'USER_ADDED', NULL,
  (
  SELECT r.id FROM dynamic_model_relation r
  WHERE r.deleted = false AND r.tenant_id = 1
    AND r.source_model_code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
    AND r.target_model_code = 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9'
    AND r.field_code = 'MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id'
  LIMIT 1
), 'equipment', 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-6f334d0329224744ab4d8c800ea6b0e0'
  AND f.code = 'REF-MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  1, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
  AND f.code = 'F-0054227dff284d18be8cb7a59c9f9208'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  2, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
  AND f.code = 'F-b5fdba42c7814e93a0a4966af64af608'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  3, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
  AND f.code = 'F-08a164e893064fc090f611deca4654ab'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  4, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
  AND f.code = 'F-c89855b21acd4cae870535cc1e29b3f9'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, FALSE,
  NULL, NULL,
  5, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
  AND f.code = 'F-24442601845a4c7eac78f96b037da45c'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, FALSE,
  NULL, NULL,
  6, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
  AND f.code = 'F-0ddaa2df8e4645fcbb71d0a425e25e5e'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  7, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
  AND f.code = 'F-1920066551d944b3b7fd8b0fc7e1c094'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  8, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
  AND f.code = 'F-e8c7851b5ade42feb7f72eb1ab59b3b5'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  9, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
  AND f.code = 'F-2908d7224a2341e39f413e4905c6a46a'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  10, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
  AND f.code = 'F-652ef6ef44c247958f9d937722aa4e18'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  11, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
  AND f.code = 'F-95373b75983148d7aaa6b83e36cd6c5b'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  12, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
  AND f.code = 'F-5d2f22057f0f4b50b6b0e24d2dc86a66'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, FALSE,
  NULL, NULL,
  13, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
  AND f.code = 'F-737b95fd043748b3a565edbd6735733e'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, FALSE,
  NULL, NULL,
  14, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
  AND f.code = 'F-cd09db6ca3134996a6468594f64bc97e'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  15, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
  AND f.code = 'F-4c2787cd47d74621a3a8868c9bad729b'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  16, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
  AND f.code = 'F-66324f09cb7e4913a6af86b1e4f7c7b7'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  17, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
  AND f.code = 'F-778eb98265914d7b8e65932a1aa0c355'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, FALSE,
  NULL, NULL,
  18, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
  AND f.code = 'F-28655458c4bc41d680b78576cc9cfd70'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, FALSE,
  NULL, NULL,
  19, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
  AND f.code = 'F-e43b835f6f59492ab90a33abd53d9062'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, TRUE,
  NULL, NULL,
  20, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
  AND f.code = 'F-67aeb9609c7945cfaa3f5806e27e6d90'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  21, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
  AND f.code = 'F-e9a39f2dbe0d425abea3ee1d4e911b6d'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  999, NULL,
  'USER_ADDED', NULL,
  (
  SELECT r.id FROM dynamic_model_relation r
  WHERE r.deleted = false AND r.tenant_id = 1
    AND r.source_model_code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
    AND r.target_model_code = 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9'
    AND r.field_code = 'MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id'
  LIMIT 1
), 'equipment', 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-93c3419d952f45a0846a2608a2719b4e'
  AND f.code = 'REF-MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  1, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
  AND f.code = 'F-0054227dff284d18be8cb7a59c9f9208'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  2, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
  AND f.code = 'F-b5fdba42c7814e93a0a4966af64af608'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  3, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
  AND f.code = 'F-08a164e893064fc090f611deca4654ab'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  4, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
  AND f.code = 'F-c89855b21acd4cae870535cc1e29b3f9'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, FALSE,
  NULL, NULL,
  5, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
  AND f.code = 'F-24442601845a4c7eac78f96b037da45c'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, FALSE,
  NULL, NULL,
  6, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
  AND f.code = 'F-0ddaa2df8e4645fcbb71d0a425e25e5e'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  7, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
  AND f.code = 'F-1920066551d944b3b7fd8b0fc7e1c094'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  8, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
  AND f.code = 'F-e8c7851b5ade42feb7f72eb1ab59b3b5'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  9, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
  AND f.code = 'F-2908d7224a2341e39f413e4905c6a46a'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  10, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
  AND f.code = 'F-652ef6ef44c247958f9d937722aa4e18'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  11, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
  AND f.code = 'F-95373b75983148d7aaa6b83e36cd6c5b'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  12, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
  AND f.code = 'F-5d2f22057f0f4b50b6b0e24d2dc86a66'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, FALSE,
  NULL, NULL,
  13, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
  AND f.code = 'F-737b95fd043748b3a565edbd6735733e'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, FALSE,
  NULL, NULL,
  14, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
  AND f.code = 'F-cd09db6ca3134996a6468594f64bc97e'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  15, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
  AND f.code = 'F-4c2787cd47d74621a3a8868c9bad729b'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  16, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
  AND f.code = 'F-66324f09cb7e4913a6af86b1e4f7c7b7'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  17, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
  AND f.code = 'F-778eb98265914d7b8e65932a1aa0c355'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, FALSE,
  NULL, NULL,
  18, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
  AND f.code = 'F-28655458c4bc41d680b78576cc9cfd70'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, FALSE,
  NULL, NULL,
  19, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
  AND f.code = 'F-e43b835f6f59492ab90a33abd53d9062'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, TRUE,
  NULL, NULL,
  20, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
  AND f.code = 'F-67aeb9609c7945cfaa3f5806e27e6d90'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  21, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
  AND f.code = 'F-e9a39f2dbe0d425abea3ee1d4e911b6d'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  999, NULL,
  'USER_ADDED', NULL,
  (
  SELECT r.id FROM dynamic_model_relation r
  WHERE r.deleted = false AND r.tenant_id = 1
    AND r.source_model_code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
    AND r.target_model_code = 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9'
    AND r.field_code = 'MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id'
  LIMIT 1
), 'equipment', 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-1418ef020a03466fbb58f73fe3e7562e'
  AND f.code = 'REF-MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  999, NULL,
  'USER_ADDED', NULL,
  (
  SELECT r.id FROM dynamic_model_relation r
  WHERE r.deleted = false AND r.tenant_id = 1
    AND r.source_model_code = 'MODEL-8a091acfa63941459916e380e1e1020e'
    AND r.target_model_code = 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9'
    AND r.field_code = 'MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id'
  LIMIT 1
), 'equipment', 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-8a091acfa63941459916e380e1e1020e'
  AND f.code = 'REF-MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  999, NULL,
  'USER_ADDED', NULL,
  (
  SELECT r.id FROM dynamic_model_relation r
  WHERE r.deleted = false AND r.tenant_id = 1
    AND r.source_model_code = 'MODEL-f1abcb10cae34b459f85e4135505a6a4'
    AND r.target_model_code = 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9'
    AND r.field_code = 'MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id'
  LIMIT 1
), 'equipment', 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-f1abcb10cae34b459f85e4135505a6a4'
  AND f.code = 'REF-MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  999, NULL,
  'USER_ADDED', NULL,
  (
  SELECT r.id FROM dynamic_model_relation r
  WHERE r.deleted = false AND r.tenant_id = 1
    AND r.source_model_code = 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2'
    AND r.target_model_code = 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9'
    AND r.field_code = 'MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id'
  LIMIT 1
), 'equipment', 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-58bc2cd163ce4ebf91bcfbfc3cb2a5e2'
  AND f.code = 'REF-MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  999, NULL,
  'USER_ADDED', NULL,
  (
  SELECT r.id FROM dynamic_model_relation r
  WHERE r.deleted = false AND r.tenant_id = 1
    AND r.source_model_code = 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad'
    AND r.target_model_code = 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9'
    AND r.field_code = 'MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id'
  LIMIT 1
), 'equipment', 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-5e55ed1fbbfb4379aba9cb25b39679ad'
  AND f.code = 'REF-MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  999, NULL,
  'USER_ADDED', NULL,
  (
  SELECT r.id FROM dynamic_model_relation r
  WHERE r.deleted = false AND r.tenant_id = 1
    AND r.source_model_code = 'MODEL-3aba3622dd75479f8dfc02228540cc80'
    AND r.target_model_code = 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9'
    AND r.field_code = 'MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id'
  LIMIT 1
), 'equipment', 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-3aba3622dd75479f8dfc02228540cc80'
  AND f.code = 'REF-MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  999, NULL,
  'USER_ADDED', NULL,
  (
  SELECT r.id FROM dynamic_model_relation r
  WHERE r.deleted = false AND r.tenant_id = 1
    AND r.source_model_code = 'MODEL-17c15377760a4656b487fb23d072c1a4'
    AND r.target_model_code = 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9'
    AND r.field_code = 'MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id'
  LIMIT 1
), 'equipment', 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-17c15377760a4656b487fb23d072c1a4'
  AND f.code = 'REF-MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  999, NULL,
  'USER_ADDED', NULL,
  (
  SELECT r.id FROM dynamic_model_relation r
  WHERE r.deleted = false AND r.tenant_id = 1
    AND r.source_model_code = 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7'
    AND r.target_model_code = 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9'
    AND r.field_code = 'MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id'
  LIMIT 1
), 'equipment', 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-e741ab7ca0de41878e9b7d8dc682e8d7'
  AND f.code = 'REF-MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  999, NULL,
  'USER_ADDED', NULL,
  (
  SELECT r.id FROM dynamic_model_relation r
  WHERE r.deleted = false AND r.tenant_id = 1
    AND r.source_model_code = 'MODEL-5de5a74896754c4e93786884405b9d89'
    AND r.target_model_code = 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9'
    AND r.field_code = 'MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id'
  LIMIT 1
), 'equipment', 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-5de5a74896754c4e93786884405b9d89'
  AND f.code = 'REF-MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  999, NULL,
  'USER_ADDED', NULL,
  (
  SELECT r.id FROM dynamic_model_relation r
  WHERE r.deleted = false AND r.tenant_id = 1
    AND r.source_model_code = 'MODEL-5bf9561d71624c43af36d4411520bb8d'
    AND r.target_model_code = 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9'
    AND r.field_code = 'MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id'
  LIMIT 1
), 'equipment', 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-5bf9561d71624c43af36d4411520bb8d'
  AND f.code = 'REF-MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  999, NULL,
  'USER_ADDED', NULL,
  (
  SELECT r.id FROM dynamic_model_relation r
  WHERE r.deleted = false AND r.tenant_id = 1
    AND r.source_model_code = 'MODEL-8c85f68408244a2db7c04cfc00345e2f'
    AND r.target_model_code = 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9'
    AND r.field_code = 'MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id'
  LIMIT 1
), 'equipment', 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-8c85f68408244a2db7c04cfc00345e2f'
  AND f.code = 'REF-MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  '', NULL,
  1, NULL,
  'USER_ADDED', NULL,
  (
  SELECT r.id FROM dynamic_model_relation r
  WHERE r.deleted = false AND r.tenant_id = 1
    AND r.source_model_code = 'MODEL-07c955e48d9e4cc09916238a4905a36f'
    AND r.target_model_code = 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9'
    AND r.field_code = 'MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id'
  LIMIT 1
), 'equipment', 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-07c955e48d9e4cc09916238a4905a36f'
  AND f.code = 'REF-MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  1, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-a8470fe38e9d4d68b7e11989e361e388'
  AND f.code = 'F-4d6aa265bbff4ed69227aa623f9dae8f'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  2, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-a8470fe38e9d4d68b7e11989e361e388'
  AND f.code = 'F-7c79907d01cc44e5bafe7fae273a336c'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  3, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-a8470fe38e9d4d68b7e11989e361e388'
  AND f.code = 'F-ce35984ef8ac41db865b11d5a7c726c2'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  4, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-a8470fe38e9d4d68b7e11989e361e388'
  AND f.code = 'F-57d6bdf06b284821bd003d0fdba16157'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  5, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-a8470fe38e9d4d68b7e11989e361e388'
  AND f.code = 'F-3fc2a30d4e4a4114878b4f77c331e76f'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  1, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-61cc9a25576a4d07b304337a3eac966a'
  AND f.code = 'F-4d6aa265bbff4ed69227aa623f9dae8f'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  2, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-61cc9a25576a4d07b304337a3eac966a'
  AND f.code = 'F-7c79907d01cc44e5bafe7fae273a336c'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  3, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-61cc9a25576a4d07b304337a3eac966a'
  AND f.code = 'F-ce35984ef8ac41db865b11d5a7c726c2'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  4, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-61cc9a25576a4d07b304337a3eac966a'
  AND f.code = 'F-57d6bdf06b284821bd003d0fdba16157'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  5, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-61cc9a25576a4d07b304337a3eac966a'
  AND f.code = 'F-3fc2a30d4e4a4114878b4f77c331e76f'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  1, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-88a38000121341e2950132da32baad90'
  AND f.code = 'F-4d6aa265bbff4ed69227aa623f9dae8f'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  2, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-88a38000121341e2950132da32baad90'
  AND f.code = 'F-7c79907d01cc44e5bafe7fae273a336c'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  3, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-88a38000121341e2950132da32baad90'
  AND f.code = 'F-ce35984ef8ac41db865b11d5a7c726c2'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  4, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-88a38000121341e2950132da32baad90'
  AND f.code = 'F-57d6bdf06b284821bd003d0fdba16157'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  5, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-88a38000121341e2950132da32baad90'
  AND f.code = 'F-3fc2a30d4e4a4114878b4f77c331e76f'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  1, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-b83f05dc94f34fb4807458c0a48ba488'
  AND f.code = 'F-4d6aa265bbff4ed69227aa623f9dae8f'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  2, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-b83f05dc94f34fb4807458c0a48ba488'
  AND f.code = 'F-7c79907d01cc44e5bafe7fae273a336c'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  3, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-b83f05dc94f34fb4807458c0a48ba488'
  AND f.code = 'F-ce35984ef8ac41db865b11d5a7c726c2'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  4, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-b83f05dc94f34fb4807458c0a48ba488'
  AND f.code = 'F-57d6bdf06b284821bd003d0fdba16157'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  5, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-b83f05dc94f34fb4807458c0a48ba488'
  AND f.code = 'F-3fc2a30d4e4a4114878b4f77c331e76f'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  1, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-f55ce56d31f741caa5a69801556834fe'
  AND f.code = 'F-4d6aa265bbff4ed69227aa623f9dae8f'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  2, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-f55ce56d31f741caa5a69801556834fe'
  AND f.code = 'F-7c79907d01cc44e5bafe7fae273a336c'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  3, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-f55ce56d31f741caa5a69801556834fe'
  AND f.code = 'F-ce35984ef8ac41db865b11d5a7c726c2'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  4, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-f55ce56d31f741caa5a69801556834fe'
  AND f.code = 'F-57d6bdf06b284821bd003d0fdba16157'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  5, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-f55ce56d31f741caa5a69801556834fe'
  AND f.code = 'F-3fc2a30d4e4a4114878b4f77c331e76f'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  '', NULL,
  3, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-947e1415c0514a9d9001eba2d58fb22c'
  AND f.code = 'F-8c030a9ea7434ae293cdabc3e379c1c2'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  '', NULL,
  3, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-4304a9f0d8fc49c9af7acb5baa74b0f0'
  AND f.code = 'F-f96d271a9acf48f894d9aff1a06d2a9e'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  TRUE, FALSE,
  FALSE, FALSE,
  '', NULL,
  4, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-6b12a00d513b4840973d647926fa422d'
  AND f.code = 'F-2018852d847d4ae3a29d143ef78a921a'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  TRUE, FALSE,
  FALSE, FALSE,
  '', NULL,
  1, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-25035f3924054db1a5763651feef6c1c'
  AND f.code = 'F-f79945273262403ca68c2894da465a63'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  '', NULL,
  2, NULL,
  'USER_ADDED', NULL,
  NULL, 'customer', 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-25035f3924054db1a5763651feef6c1c'
  AND f.code = 'F-40d48dc5caec46ac9e482f03f8102f59'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  4, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-b34597144e524aeb9b969b4b8357f6cd'
  AND f.code = 'F-67e85d7f261c43b1b99dab1cc0a8885a'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  '', NULL,
  5, NULL,
  'USER_ADDED', NULL,
  NULL, 'customer', 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-b34597144e524aeb9b969b4b8357f6cd'
  AND f.code = 'F-40d48dc5caec46ac9e482f03f8102f59'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  '', NULL,
  6, NULL,
  'USER_ADDED', NULL,
  NULL, 'customer', 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-b34597144e524aeb9b969b4b8357f6cd'
  AND f.code = 'F-e4b8535544194dcf9de2cf178e316ea6'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  '待命', NULL,
  1, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-c3470e3a96e9493880a401f79e3f98f3'
  AND f.code = 'F-f9afe7748fb040f29c8f3450e48c30d3'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  '', NULL,
  2, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-c3470e3a96e9493880a401f79e3f98f3'
  AND f.code = 'F-18d0ce546f434b9e96b799d1ade2b46b'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  '', NULL,
  3, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-c3470e3a96e9493880a401f79e3f98f3'
  AND f.code = 'F-9c447cc5208a496289c108e57289df94'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  '', NULL,
  4, NULL,
  'USER_ADDED', NULL,
  NULL, 'customer', 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-c3470e3a96e9493880a401f79e3f98f3'
  AND f.code = 'F-254fc29f33e0408096351d37ca55afd1'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  '', NULL,
  3, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-3f3d3918c11148b6b67d1470ac6e8fdc'
  AND f.code = 'F-433ed7705e5f476fa193a26b642d43ab'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  '', NULL,
  4, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-3f3d3918c11148b6b67d1470ac6e8fdc'
  AND f.code = 'F-b32284d1d6f743b288c6b3244dc668bf'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  '', NULL,
  5, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-3f3d3918c11148b6b67d1470ac6e8fdc'
  AND f.code = 'F-cb25c908b0ff485199c1800d353d3db8'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  'true', NULL,
  1, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-12fa59cf4469493a8b0a481575f5ea7e'
  AND f.code = 'F-0b5a1e6027a44d7381d4be263c4d83c6'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  'true', NULL,
  1, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-2fe725af947441cd93c590df6275e551'
  AND f.code = 'F-e78e5b908e2a461f81776991f85863ed'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  'true', NULL,
  1, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-556a2a0bd63944a79f97a936f3ffeaaa'
  AND f.code = 'F-a4922b3105df498d94557170f8b2c9da'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  1, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-0d966e1eedda42c5b3c7193571f3c2b3'
  AND f.code = 'F-7c115d3b6ced4f4d924ef114e12ada27'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  1, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-9e6a1e6e4a5d4b378a9ec7abfd0a0fd9'
  AND f.code = 'F-68ef79eb88454c8fa11a1e9baa987d88'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  'true', NULL,
  1, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-3c262bb09fed4db6bd04a13bc5ce1309'
  AND f.code = 'F-e3392e0afa374691a4795dd236cc6cb0'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  'true', NULL,
  1, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-1e1d4c6cfd724c99ab1ff9f1a9e73e98'
  AND f.code = 'F-910c98bffcab46c7a80c3def7cf5a953'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  'false', NULL,
  1, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-3ca13297c904476db91aac9880088975'
  AND f.code = 'F-ace8c92532f7469d8026253f71b94ed3'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  'true', NULL,
  1, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-58ca87771dfe4156b75aeac50211117f'
  AND f.code = 'F-8a103735cdc646d8a454e98913c1a6b4'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;
