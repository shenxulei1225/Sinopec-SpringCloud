-- ============================================================================
-- 系统共用 · 05 模型（通用设备模型库：equipment + 历史设备模型，仅库内关联）
-- Generated: 2026-07-05 by scripts/export-platform-import.py
--
-- 约定：不写 surrogate id；幂等键为 code / field_code / page_code。
-- 依赖：system/03_fields.sql
-- 含 data_collection/custom_6128 等历史设备模型，统一 business_type_code=equipment
-- 不含 region→equipment 等跨业务关联（见 smart-corridor/05_models.sql）
-- ============================================================================

SET search_path TO dynamicbusiness;

-- dynamic_model: 48 row(s), upsert by code

INSERT INTO dynamic_model (
  code, name, business_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'MODEL-05e5c6a6c8fe4f3c9bd1dd86dd24a659', '环网交换机', 'equipment',
  '工业环网交换机，用于管廊网络通信和数据传输', 1,
  0, '{"groups": [{"id": "group-1779986713015", "name": "基础信息", "sort": 1024, "color": "#409eff", "fields": []}]}',
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
  'MODEL-0e171b9d21024fd183a1cb355e1b8c08', '220V电缆', 'equipment',
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
  'MODEL-123328b7bfe745ceb337d286ac64aa17', '氧气传感器', 'equipment',
  '氧气浓度监测传感器，用于监测管廊内氧气浓度', 1,
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
  'MODEL-1966e63fec48402f9b60280e66faea0f', '应急灯', 'equipment',
  '设备类型: 应急灯 (设备数量: 1388)', 1,
  0, '{"groups": [{"id": "group-1779948029144", "name": "基础信息", "sort": 1024, "color": "#409eff", "fields": []}]}',
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
  'MODEL-2af0515a5a36420086980b2322037edb', '网络摄像机', 'equipment',
  '视频监控网络摄像机，用于管廊内部视频监控', 1,
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
  'MODEL-3130eba3747e4a5aa0862c94807822ba', '功分器', 'equipment',
  '信号功率分配器，用于无线信号分配', 1,
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
  'MODEL-3335affd4a814e4dba54d613d07bb0fa', '氧气检测仪', 'equipment',
  '氧气检测仪设备模型，共432个设备', 1,
  0, '{"groups": [{"id": "group-1779948024026", "name": "基础信息", "sort": 1024, "color": "#409eff", "fields": []}]}',
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
  'MODEL-3ae58694e5ab4dc4a078c6e4b548f617', '气溶胶', 'equipment',
  '设备类型: 气溶胶 (设备数量: 454)', 1,
  0, '{"groups": [{"id": "group-1779948028609", "name": "基础信息", "sort": 1024, "color": "#409eff", "fields": []}]}',
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
  'MODEL-44cc7511fc224085a51e2887569c933a', '轴流风机', 'equipment',
  '管廊/隧道通风轴流风机', 1,
  0, '{"groups": [{"id": "group-1779986707585", "name": "基础信息", "sort": 1024, "color": "#409eff", "fields": []}]}',
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
  'MODEL-50991eab2a714ed288d00c58ff01c810', 'IP电话机', 'equipment',
  'IP网络电话终端设备，用于管廊内部通讯', 1,
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
  'MODEL-5284b71c69d946ae88c0848bfbbb0d7c', '测试模型', 'equipment',
  '', 1,
  0, '{"groups": [{"id": "group-1775889520154", "name": "基本信息", "sort": 1, "color": "#409eff", "fields": []}, {"id": "group-1779947954864", "name": "基础信息", "sort": 1025, "color": "#409eff", "fields": []}]}',
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
  'MODEL-56b09ad2ef96439c87bda429e2bb9404', 'IP电话', 'equipment',
  'IP电话设备模型，共366个设备', 1,
  0, '{"groups": [{"id": "group-1779947959053", "name": "基础信息", "sort": 1024, "color": "#409eff", "fields": []}]}',
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
  'MODEL-5a9b87c42571486998d363101c54ec8c', '消防泵', 'equipment',
  '室内消火栓/喷淋系统消防泵', 1,
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
  'MODEL-6671ca830f4742229c0a00ff531f974c', '消防', 'equipment',
  '消防设备模型，共654个设备', 1,
  0, '{"groups": [{"id": "group-1779948026481", "name": "基础信息", "sort": 1024, "color": "#409eff", "fields": []}]}',
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
  'MODEL-68554821800248cfacacbac1a53cdf37', 'UPS不间断电源', 'equipment',
  '机房及重要负载供电UPS主机', 1,
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
  'MODEL-6bf0e75bec6a496ba7200b99293c1417', '单独', 'equipment',
  '', 1,
  0, '{"groups": [{"id": "group-1779886125500", "name": "基础信息", "sort": 1024, "color": "#409eff", "fields": []}]}',
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
  'MODEL-6fd43ab74f6f425f81f0ad547603a849', '交换机', 'equipment',
  '以太网交换机（接入/汇聚/核心）', 1,
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
  'MODEL-70d2b066120c4b969f0b661d1d2fb664', 'LED显示屏', 'equipment',
  'LED信息显示设备', 1,
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
  'MODEL-7c4fe9b2d52d41cca5383488a4a400a5', '电缆接头', 'equipment',
  '设备类型: 电缆接头 (设备数量: 345)', 1,
  0, '{"groups": [{"id": "group-1779948030155", "name": "基础信息", "sort": 1024, "color": "#409eff", "fields": []}]}',
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
  'MODEL-7c746a60520347baaf64334625f07f31', '定向天线', 'equipment',
  '定向无线信号天线，用于定向信号传输', 1,
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
  'MODEL-7fc9012b394749a3b30e1788e4057324', '检修箱', 'equipment',
  '设备类型: 检修箱 (设备数量: 521)', 1,
  0, '{"groups": [{"id": "group-1779948029522", "name": "基础信息", "sort": 1024, "color": "#409eff", "fields": []}]}',
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
  'MODEL-81132ce00d2041c6a4ee06ec1c01dac8', '门禁控制器', 'equipment',
  '门禁系统控制设备', 1,
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
  'MODEL-831d0957ceb748bab257d13e3b9859ce', '未分类', 'equipment',
  '未分类设备模型', 1,
  0, '{"groups": [{"id": "group-1779948027922", "name": "基础信息", "sort": 1024, "color": "#409eff", "fields": []}]}',
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
  'MODEL-9421d45e38434b73af04a09287c8f0b6', '二氧化碳采集模型', 'equipment',
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
  'MODEL-990370e140aa481a82158bc96dc0b006', 'ACU柜', 'equipment',
  '环境控制单元柜，用于管廊环境监控和控制', 1,
  0, '{"groups": [{"id": "group-1779986708242", "name": "基础信息", "sort": 1024, "color": "#409eff", "fields": []}]}',
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
  'MODEL-9b557c9404684caab2820eb0aec2cf15', '按钮箱', 'equipment',
  '按钮箱设备模型，共165个设备', 1,
  0, '{"groups": [{"id": "group-1779947955832", "name": "基础信息", "sort": 1024, "color": "#409eff", "fields": []}]}',
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
  'MODEL-a547e7b120434da0bc8d94f11447f75a', '照明', 'equipment',
  '设备类型: 照明 (设备数量: 2635)', 1,
  0, '{"groups": [{"id": "group-1779947979263", "name": "基础信息", "sort": 1024, "color": "#409eff", "fields": []}]}',
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
  'MODEL-a62c727c0a024e19a8221c68fcf0c09a', '报警主机', 'equipment',
  '入侵报警系统控制主机', 1,
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
  'MODEL-ab375d26cb6e45eba5a4acfe789a9933', '二氧化碳检测仪', 'equipment',
  '二氧化碳检测仪设备模型，共432个设备', 1,
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
  'MODEL-b75b3fcac0f54eae9226c1cf99e24595', '温度传感器', 'equipment',
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
  'MODEL-b8251da851514341adef7f6f72995cf3', '无线远端机', 'equipment',
  '无线信号远端覆盖设备，用于扩展无线信号覆盖范围', 1,
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
  'MODEL-b8ed7911c3664dafaf4073223792b5a4', '温湿度检测仪', 'equipment',
  '温湿度检测仪设备模型，共432个设备', 1,
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
  'MODEL-bc2cdffeab434f4d862c847fa99938ba', '二氧化碳传感器', 'equipment',
  '二氧化碳浓度监测传感器，用于监测管廊内CO2浓度', 1,
  0, '{"groups": [{"id": "group-1779986707947", "name": "基础信息", "sort": 1024, "color": "#409eff", "fields": []}]}',
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
  'MODEL-bd13c167e66b46bca791fae607c72ad7', '门禁', 'equipment',
  '门禁设备模型，共227个设备', 1,
  0, '{"groups": [{"id": "group-1779947956347", "name": "基础信息", "sort": 1024, "color": "#409eff", "fields": []}]}',
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
  'MODEL-bf41bd50df044f5faf43c4759353ccd1', '给水泵', 'equipment',
  '给水及循环水泵', 1,
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
  'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9', '可燃气体探测器', 'equipment',
  '可燃气体/有毒有害气体检测探头', 1,
  0, '{"groups": [{"id": "group-1779948030584", "name": "基础信息", "sort": 1024, "color": "#409eff", "fields": []}]}',
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
  'MODEL-c6bc32646f3e4bde8409120c5c290eee', '光纤收发器', 'equipment',
  '光电信号转换设备，用于光纤网络传输', 1,
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
  'MODEL-cac64b3a48254912bf410d8d2ceb748d', '阀门', 'equipment',
  '设备类型: 阀门 (设备数量: 442)', 1,
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
  'MODEL-cd14851e79e649d797cdcc4a6e6f7fe5', '氧气数据采集', 'equipment',
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
  'MODEL-d11cb8bb47e74d73b5d4233814de2a39', '水泵', 'equipment',
  '水泵设备模型，共454个设备', 1,
  0, '{"groups": [{"id": "group-1779948024599", "name": "基础信息", "sort": 1024, "color": "#409eff", "fields": []}]}',
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
  'MODEL-d446eef7329f45b4a374347e215fec5d', '温湿度传感器', 'equipment',
  '温度湿度监测传感器，用于监测管廊内温湿度环境', 1,
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
  'MODEL-d57178112ab5465eb43f926bb18fd98c', '湿度传感器', 'equipment',
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
  'MODEL-e1e2ab00b9cb41dab46f48856374ebd4', '视频监控', 'equipment',
  '视频监控设备模型，共802个设备', 1,
  0, '{"groups": [{"id": "group-1779948026902", "name": "基础信息", "sort": 1024, "color": "#409eff", "fields": []}]}',
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
  'MODEL-e25f3089a99d4878a1ddd326ba0910bf', '监控控制柜', 'equipment',
  '监控系统控制柜', 1,
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
  'MODEL-e5af81c45bbc44d48374d8036032cc5d', '红外探测器', 'equipment',
  '红外入侵探测设备', 1,
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
  'MODEL-e5c45ff8ca074102829bba872e091bd1', '人员定位主机', 'equipment',
  '人员定位系统主机，用于管廊内人员位置追踪和管理', 1,
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
  'MODEL-f5270d478dd849389e59a178cd512629', '移动终端', 'equipment',
  '人员定位移动终端设备，由工作人员携带用于位置追踪', 1,
  0, '{"groups": [{"id": "group-1779986714918", "name": "基础信息", "sort": 1024, "color": "#409eff", "fields": []}]}',
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
  'MODEL-fd9456206abe46ae972a0dc15df082ec', '控制箱', 'equipment',
  '控制箱设备模型，共315个设备', 1,
  0, '{"groups": [{"id": "group-1779947957193", "name": "基础信息", "sort": 1024, "color": "#409eff", "fields": []}]}',
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


-- dynamic_model_relation: (empty)


-- dynamic_model_relation_declaration: (empty)


-- dynamic_model_field_assignment: 201 row(s), resolve model_id/field_id by code

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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-2af0515a5a36420086980b2322037edb'
  AND f.code = 'F-18af5bff1c894e2da694e5fa35d8c7af'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-2af0515a5a36420086980b2322037edb'
  AND f.code = 'F-e18365a3a3ec4485ab699b46d6f820c2'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-2af0515a5a36420086980b2322037edb'
  AND f.code = 'F-5b561c0ac2fe41d59a7b1831a93d1637'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-2af0515a5a36420086980b2322037edb'
  AND f.code = 'F-4b75b7f9889d4f1d90afd19c03c934af'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-2af0515a5a36420086980b2322037edb'
  AND f.code = 'F-58b6dbbc32f1438b96188087aa68ae4e'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-2af0515a5a36420086980b2322037edb'
  AND f.code = 'F-c140d56fbb634e88a6a2feeb63f3b764'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-2af0515a5a36420086980b2322037edb'
  AND f.code = 'F-cc746ce0224145af88d5428d0b03213a'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-2af0515a5a36420086980b2322037edb'
  AND f.code = 'F-66264ae122c54504a8fa0679079772fc'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-2af0515a5a36420086980b2322037edb'
  AND f.code = 'F-06cf3dcb998f4c4392cb6a5a67fa1289'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-2af0515a5a36420086980b2322037edb'
  AND f.code = 'F-94807db40a4b4e59a482888fca4b40df'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-2af0515a5a36420086980b2322037edb'
  AND f.code = 'F-984f2b3cfb6b4931a4dbc4506c116522'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-a62c727c0a024e19a8221c68fcf0c09a'
  AND f.code = 'F-18af5bff1c894e2da694e5fa35d8c7af'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-a62c727c0a024e19a8221c68fcf0c09a'
  AND f.code = 'F-e18365a3a3ec4485ab699b46d6f820c2'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-a62c727c0a024e19a8221c68fcf0c09a'
  AND f.code = 'F-5b561c0ac2fe41d59a7b1831a93d1637'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-a62c727c0a024e19a8221c68fcf0c09a'
  AND f.code = 'F-4b75b7f9889d4f1d90afd19c03c934af'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-a62c727c0a024e19a8221c68fcf0c09a'
  AND f.code = 'F-58b6dbbc32f1438b96188087aa68ae4e'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-a62c727c0a024e19a8221c68fcf0c09a'
  AND f.code = 'F-c140d56fbb634e88a6a2feeb63f3b764'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-a62c727c0a024e19a8221c68fcf0c09a'
  AND f.code = 'F-cc746ce0224145af88d5428d0b03213a'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-e5af81c45bbc44d48374d8036032cc5d'
  AND f.code = 'F-18af5bff1c894e2da694e5fa35d8c7af'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-e5af81c45bbc44d48374d8036032cc5d'
  AND f.code = 'F-e18365a3a3ec4485ab699b46d6f820c2'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-e5af81c45bbc44d48374d8036032cc5d'
  AND f.code = 'F-5b561c0ac2fe41d59a7b1831a93d1637'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-e5af81c45bbc44d48374d8036032cc5d'
  AND f.code = 'F-4b75b7f9889d4f1d90afd19c03c934af'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-e5af81c45bbc44d48374d8036032cc5d'
  AND f.code = 'F-58b6dbbc32f1438b96188087aa68ae4e'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-e5af81c45bbc44d48374d8036032cc5d'
  AND f.code = 'F-c140d56fbb634e88a6a2feeb63f3b764'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-e5af81c45bbc44d48374d8036032cc5d'
  AND f.code = 'F-cc746ce0224145af88d5428d0b03213a'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-81132ce00d2041c6a4ee06ec1c01dac8'
  AND f.code = 'F-18af5bff1c894e2da694e5fa35d8c7af'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-81132ce00d2041c6a4ee06ec1c01dac8'
  AND f.code = 'F-e18365a3a3ec4485ab699b46d6f820c2'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-81132ce00d2041c6a4ee06ec1c01dac8'
  AND f.code = 'F-5b561c0ac2fe41d59a7b1831a93d1637'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-81132ce00d2041c6a4ee06ec1c01dac8'
  AND f.code = 'F-4b75b7f9889d4f1d90afd19c03c934af'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-81132ce00d2041c6a4ee06ec1c01dac8'
  AND f.code = 'F-58b6dbbc32f1438b96188087aa68ae4e'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-81132ce00d2041c6a4ee06ec1c01dac8'
  AND f.code = 'F-c140d56fbb634e88a6a2feeb63f3b764'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-81132ce00d2041c6a4ee06ec1c01dac8'
  AND f.code = 'F-cc746ce0224145af88d5428d0b03213a'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-70d2b066120c4b969f0b661d1d2fb664'
  AND f.code = 'F-18af5bff1c894e2da694e5fa35d8c7af'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-70d2b066120c4b969f0b661d1d2fb664'
  AND f.code = 'F-e18365a3a3ec4485ab699b46d6f820c2'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-70d2b066120c4b969f0b661d1d2fb664'
  AND f.code = 'F-5b561c0ac2fe41d59a7b1831a93d1637'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-70d2b066120c4b969f0b661d1d2fb664'
  AND f.code = 'F-4b75b7f9889d4f1d90afd19c03c934af'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-70d2b066120c4b969f0b661d1d2fb664'
  AND f.code = 'F-58b6dbbc32f1438b96188087aa68ae4e'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-70d2b066120c4b969f0b661d1d2fb664'
  AND f.code = 'F-c140d56fbb634e88a6a2feeb63f3b764'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-70d2b066120c4b969f0b661d1d2fb664'
  AND f.code = 'F-cc746ce0224145af88d5428d0b03213a'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-e25f3089a99d4878a1ddd326ba0910bf'
  AND f.code = 'F-18af5bff1c894e2da694e5fa35d8c7af'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-e25f3089a99d4878a1ddd326ba0910bf'
  AND f.code = 'F-e18365a3a3ec4485ab699b46d6f820c2'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-e25f3089a99d4878a1ddd326ba0910bf'
  AND f.code = 'F-5b561c0ac2fe41d59a7b1831a93d1637'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-e25f3089a99d4878a1ddd326ba0910bf'
  AND f.code = 'F-4b75b7f9889d4f1d90afd19c03c934af'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-e25f3089a99d4878a1ddd326ba0910bf'
  AND f.code = 'F-58b6dbbc32f1438b96188087aa68ae4e'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-e25f3089a99d4878a1ddd326ba0910bf'
  AND f.code = 'F-c140d56fbb634e88a6a2feeb63f3b764'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-e25f3089a99d4878a1ddd326ba0910bf'
  AND f.code = 'F-cc746ce0224145af88d5428d0b03213a'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-50991eab2a714ed288d00c58ff01c810'
  AND f.code = 'F-18af5bff1c894e2da694e5fa35d8c7af'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-50991eab2a714ed288d00c58ff01c810'
  AND f.code = 'F-e18365a3a3ec4485ab699b46d6f820c2'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-50991eab2a714ed288d00c58ff01c810'
  AND f.code = 'F-5b561c0ac2fe41d59a7b1831a93d1637'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-50991eab2a714ed288d00c58ff01c810'
  AND f.code = 'F-4b75b7f9889d4f1d90afd19c03c934af'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-50991eab2a714ed288d00c58ff01c810'
  AND f.code = 'F-58b6dbbc32f1438b96188087aa68ae4e'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-50991eab2a714ed288d00c58ff01c810'
  AND f.code = 'F-c140d56fbb634e88a6a2feeb63f3b764'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-50991eab2a714ed288d00c58ff01c810'
  AND f.code = 'F-cc746ce0224145af88d5428d0b03213a'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-c6bc32646f3e4bde8409120c5c290eee'
  AND f.code = 'F-18af5bff1c894e2da694e5fa35d8c7af'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-c6bc32646f3e4bde8409120c5c290eee'
  AND f.code = 'F-e18365a3a3ec4485ab699b46d6f820c2'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-c6bc32646f3e4bde8409120c5c290eee'
  AND f.code = 'F-5b561c0ac2fe41d59a7b1831a93d1637'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-c6bc32646f3e4bde8409120c5c290eee'
  AND f.code = 'F-4b75b7f9889d4f1d90afd19c03c934af'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-c6bc32646f3e4bde8409120c5c290eee'
  AND f.code = 'F-58b6dbbc32f1438b96188087aa68ae4e'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-c6bc32646f3e4bde8409120c5c290eee'
  AND f.code = 'F-c140d56fbb634e88a6a2feeb63f3b764'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-c6bc32646f3e4bde8409120c5c290eee'
  AND f.code = 'F-cc746ce0224145af88d5428d0b03213a'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-3130eba3747e4a5aa0862c94807822ba'
  AND f.code = 'F-18af5bff1c894e2da694e5fa35d8c7af'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-3130eba3747e4a5aa0862c94807822ba'
  AND f.code = 'F-e18365a3a3ec4485ab699b46d6f820c2'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-3130eba3747e4a5aa0862c94807822ba'
  AND f.code = 'F-5b561c0ac2fe41d59a7b1831a93d1637'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-3130eba3747e4a5aa0862c94807822ba'
  AND f.code = 'F-4b75b7f9889d4f1d90afd19c03c934af'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-3130eba3747e4a5aa0862c94807822ba'
  AND f.code = 'F-58b6dbbc32f1438b96188087aa68ae4e'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-3130eba3747e4a5aa0862c94807822ba'
  AND f.code = 'F-c140d56fbb634e88a6a2feeb63f3b764'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-3130eba3747e4a5aa0862c94807822ba'
  AND f.code = 'F-cc746ce0224145af88d5428d0b03213a'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-b8251da851514341adef7f6f72995cf3'
  AND f.code = 'F-18af5bff1c894e2da694e5fa35d8c7af'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-b8251da851514341adef7f6f72995cf3'
  AND f.code = 'F-e18365a3a3ec4485ab699b46d6f820c2'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-b8251da851514341adef7f6f72995cf3'
  AND f.code = 'F-5b561c0ac2fe41d59a7b1831a93d1637'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-b8251da851514341adef7f6f72995cf3'
  AND f.code = 'F-4b75b7f9889d4f1d90afd19c03c934af'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-b8251da851514341adef7f6f72995cf3'
  AND f.code = 'F-58b6dbbc32f1438b96188087aa68ae4e'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-b8251da851514341adef7f6f72995cf3'
  AND f.code = 'F-c140d56fbb634e88a6a2feeb63f3b764'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-b8251da851514341adef7f6f72995cf3'
  AND f.code = 'F-cc746ce0224145af88d5428d0b03213a'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-7c746a60520347baaf64334625f07f31'
  AND f.code = 'F-18af5bff1c894e2da694e5fa35d8c7af'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-7c746a60520347baaf64334625f07f31'
  AND f.code = 'F-e18365a3a3ec4485ab699b46d6f820c2'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-7c746a60520347baaf64334625f07f31'
  AND f.code = 'F-5b561c0ac2fe41d59a7b1831a93d1637'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-7c746a60520347baaf64334625f07f31'
  AND f.code = 'F-4b75b7f9889d4f1d90afd19c03c934af'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-7c746a60520347baaf64334625f07f31'
  AND f.code = 'F-58b6dbbc32f1438b96188087aa68ae4e'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-7c746a60520347baaf64334625f07f31'
  AND f.code = 'F-c140d56fbb634e88a6a2feeb63f3b764'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-7c746a60520347baaf64334625f07f31'
  AND f.code = 'F-cc746ce0224145af88d5428d0b03213a'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-e5c45ff8ca074102829bba872e091bd1'
  AND f.code = 'F-18af5bff1c894e2da694e5fa35d8c7af'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-e5c45ff8ca074102829bba872e091bd1'
  AND f.code = 'F-e18365a3a3ec4485ab699b46d6f820c2'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-e5c45ff8ca074102829bba872e091bd1'
  AND f.code = 'F-5b561c0ac2fe41d59a7b1831a93d1637'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-e5c45ff8ca074102829bba872e091bd1'
  AND f.code = 'F-4b75b7f9889d4f1d90afd19c03c934af'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-e5c45ff8ca074102829bba872e091bd1'
  AND f.code = 'F-58b6dbbc32f1438b96188087aa68ae4e'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-e5c45ff8ca074102829bba872e091bd1'
  AND f.code = 'F-c140d56fbb634e88a6a2feeb63f3b764'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-e5c45ff8ca074102829bba872e091bd1'
  AND f.code = 'F-cc746ce0224145af88d5428d0b03213a'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-f5270d478dd849389e59a178cd512629'
  AND f.code = 'F-18af5bff1c894e2da694e5fa35d8c7af'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-f5270d478dd849389e59a178cd512629'
  AND f.code = 'F-e18365a3a3ec4485ab699b46d6f820c2'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-f5270d478dd849389e59a178cd512629'
  AND f.code = 'F-5b561c0ac2fe41d59a7b1831a93d1637'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-f5270d478dd849389e59a178cd512629'
  AND f.code = 'F-4b75b7f9889d4f1d90afd19c03c934af'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-f5270d478dd849389e59a178cd512629'
  AND f.code = 'F-58b6dbbc32f1438b96188087aa68ae4e'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-f5270d478dd849389e59a178cd512629'
  AND f.code = 'F-c140d56fbb634e88a6a2feeb63f3b764'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-f5270d478dd849389e59a178cd512629'
  AND f.code = 'F-cc746ce0224145af88d5428d0b03213a'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-990370e140aa481a82158bc96dc0b006'
  AND f.code = 'F-18af5bff1c894e2da694e5fa35d8c7af'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-990370e140aa481a82158bc96dc0b006'
  AND f.code = 'F-e18365a3a3ec4485ab699b46d6f820c2'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-990370e140aa481a82158bc96dc0b006'
  AND f.code = 'F-5b561c0ac2fe41d59a7b1831a93d1637'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-990370e140aa481a82158bc96dc0b006'
  AND f.code = 'F-4b75b7f9889d4f1d90afd19c03c934af'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-990370e140aa481a82158bc96dc0b006'
  AND f.code = 'F-58b6dbbc32f1438b96188087aa68ae4e'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-990370e140aa481a82158bc96dc0b006'
  AND f.code = 'F-c140d56fbb634e88a6a2feeb63f3b764'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-990370e140aa481a82158bc96dc0b006'
  AND f.code = 'F-cc746ce0224145af88d5428d0b03213a'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-d446eef7329f45b4a374347e215fec5d'
  AND f.code = 'F-18af5bff1c894e2da694e5fa35d8c7af'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-d446eef7329f45b4a374347e215fec5d'
  AND f.code = 'F-e18365a3a3ec4485ab699b46d6f820c2'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-d446eef7329f45b4a374347e215fec5d'
  AND f.code = 'F-5b561c0ac2fe41d59a7b1831a93d1637'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-d446eef7329f45b4a374347e215fec5d'
  AND f.code = 'F-4b75b7f9889d4f1d90afd19c03c934af'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-d446eef7329f45b4a374347e215fec5d'
  AND f.code = 'F-58b6dbbc32f1438b96188087aa68ae4e'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-d446eef7329f45b4a374347e215fec5d'
  AND f.code = 'F-c140d56fbb634e88a6a2feeb63f3b764'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-d446eef7329f45b4a374347e215fec5d'
  AND f.code = 'F-cc746ce0224145af88d5428d0b03213a'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-d446eef7329f45b4a374347e215fec5d'
  AND f.code = 'F-559d5ad091e644a798aa9efef8d06c9c'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-d446eef7329f45b4a374347e215fec5d'
  AND f.code = 'F-4651095e38ca4f1cbe8cb32beb8c2142'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  AND m.code = 'MODEL-d446eef7329f45b4a374347e215fec5d'
  AND f.code = 'F-59e5799b2eaa4b0aa9c164f8d440409b'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  AND m.code = 'MODEL-d446eef7329f45b4a374347e215fec5d'
  AND f.code = 'F-3d7fedf4c352424abeb1b11805dfd331'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  AND m.code = 'MODEL-d446eef7329f45b4a374347e215fec5d'
  AND f.code = 'F-b42b6e004eb944e993e00f5fa734a37a'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  AND m.code = 'MODEL-d446eef7329f45b4a374347e215fec5d'
  AND f.code = 'F-1ee037ca81bb475f94b3526134ea85bb'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  AND m.code = 'MODEL-d446eef7329f45b4a374347e215fec5d'
  AND f.code = 'F-d55683af52444a7587242fbb3542a932'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  AND m.code = 'MODEL-d446eef7329f45b4a374347e215fec5d'
  AND f.code = 'F-672e3aca3c9c46cb8f0c8fe85217b708'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  AND m.code = 'MODEL-d446eef7329f45b4a374347e215fec5d'
  AND f.code = 'F-f2bbdc83208843e28026f785c7195ce5'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-123328b7bfe745ceb337d286ac64aa17'
  AND f.code = 'F-18af5bff1c894e2da694e5fa35d8c7af'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-123328b7bfe745ceb337d286ac64aa17'
  AND f.code = 'F-e18365a3a3ec4485ab699b46d6f820c2'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-123328b7bfe745ceb337d286ac64aa17'
  AND f.code = 'F-5b561c0ac2fe41d59a7b1831a93d1637'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-123328b7bfe745ceb337d286ac64aa17'
  AND f.code = 'F-4b75b7f9889d4f1d90afd19c03c934af'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-123328b7bfe745ceb337d286ac64aa17'
  AND f.code = 'F-58b6dbbc32f1438b96188087aa68ae4e'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-123328b7bfe745ceb337d286ac64aa17'
  AND f.code = 'F-c140d56fbb634e88a6a2feeb63f3b764'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-123328b7bfe745ceb337d286ac64aa17'
  AND f.code = 'F-cc746ce0224145af88d5428d0b03213a'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-123328b7bfe745ceb337d286ac64aa17'
  AND f.code = 'F-559d5ad091e644a798aa9efef8d06c9c'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-123328b7bfe745ceb337d286ac64aa17'
  AND f.code = 'F-4651095e38ca4f1cbe8cb32beb8c2142'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  AND m.code = 'MODEL-123328b7bfe745ceb337d286ac64aa17'
  AND f.code = 'F-59e5799b2eaa4b0aa9c164f8d440409b'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  AND m.code = 'MODEL-123328b7bfe745ceb337d286ac64aa17'
  AND f.code = 'F-3d7fedf4c352424abeb1b11805dfd331'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  AND m.code = 'MODEL-123328b7bfe745ceb337d286ac64aa17'
  AND f.code = 'F-b42b6e004eb944e993e00f5fa734a37a'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  AND m.code = 'MODEL-123328b7bfe745ceb337d286ac64aa17'
  AND f.code = 'F-1ee037ca81bb475f94b3526134ea85bb'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  AND m.code = 'MODEL-123328b7bfe745ceb337d286ac64aa17'
  AND f.code = 'F-d55683af52444a7587242fbb3542a932'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  AND m.code = 'MODEL-123328b7bfe745ceb337d286ac64aa17'
  AND f.code = 'F-672e3aca3c9c46cb8f0c8fe85217b708'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  AND m.code = 'MODEL-123328b7bfe745ceb337d286ac64aa17'
  AND f.code = 'F-f2bbdc83208843e28026f785c7195ce5'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-bc2cdffeab434f4d862c847fa99938ba'
  AND f.code = 'F-18af5bff1c894e2da694e5fa35d8c7af'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-bc2cdffeab434f4d862c847fa99938ba'
  AND f.code = 'F-e18365a3a3ec4485ab699b46d6f820c2'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-bc2cdffeab434f4d862c847fa99938ba'
  AND f.code = 'F-5b561c0ac2fe41d59a7b1831a93d1637'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-bc2cdffeab434f4d862c847fa99938ba'
  AND f.code = 'F-4b75b7f9889d4f1d90afd19c03c934af'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-bc2cdffeab434f4d862c847fa99938ba'
  AND f.code = 'F-58b6dbbc32f1438b96188087aa68ae4e'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-bc2cdffeab434f4d862c847fa99938ba'
  AND f.code = 'F-c140d56fbb634e88a6a2feeb63f3b764'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-bc2cdffeab434f4d862c847fa99938ba'
  AND f.code = 'F-cc746ce0224145af88d5428d0b03213a'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-bc2cdffeab434f4d862c847fa99938ba'
  AND f.code = 'F-559d5ad091e644a798aa9efef8d06c9c'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  0, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-bc2cdffeab434f4d862c847fa99938ba'
  AND f.code = 'F-4651095e38ca4f1cbe8cb32beb8c2142'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  AND m.code = 'MODEL-bc2cdffeab434f4d862c847fa99938ba'
  AND f.code = 'F-59e5799b2eaa4b0aa9c164f8d440409b'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  AND m.code = 'MODEL-bc2cdffeab434f4d862c847fa99938ba'
  AND f.code = 'F-3d7fedf4c352424abeb1b11805dfd331'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  AND m.code = 'MODEL-bc2cdffeab434f4d862c847fa99938ba'
  AND f.code = 'F-b42b6e004eb944e993e00f5fa734a37a'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  AND m.code = 'MODEL-bc2cdffeab434f4d862c847fa99938ba'
  AND f.code = 'F-1ee037ca81bb475f94b3526134ea85bb'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  AND m.code = 'MODEL-bc2cdffeab434f4d862c847fa99938ba'
  AND f.code = 'F-d55683af52444a7587242fbb3542a932'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  AND m.code = 'MODEL-bc2cdffeab434f4d862c847fa99938ba'
  AND f.code = 'F-672e3aca3c9c46cb8f0c8fe85217b708'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  AND m.code = 'MODEL-bc2cdffeab434f4d862c847fa99938ba'
  AND f.code = 'F-f2bbdc83208843e28026f785c7195ce5'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  '', NULL,
  1, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-05e5c6a6c8fe4f3c9bd1dd86dd24a659'
  AND f.code = 'F-18af5bff1c894e2da694e5fa35d8c7af'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  '', NULL,
  2, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-05e5c6a6c8fe4f3c9bd1dd86dd24a659'
  AND f.code = 'F-e18365a3a3ec4485ab699b46d6f820c2'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  '', NULL,
  3, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-05e5c6a6c8fe4f3c9bd1dd86dd24a659'
  AND f.code = 'F-5b561c0ac2fe41d59a7b1831a93d1637'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  'running', NULL,
  4, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-05e5c6a6c8fe4f3c9bd1dd86dd24a659'
  AND f.code = 'F-4b75b7f9889d4f1d90afd19c03c934af'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  AND m.code = 'MODEL-05e5c6a6c8fe4f3c9bd1dd86dd24a659'
  AND f.code = 'F-58b6dbbc32f1438b96188087aa68ae4e'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  AND m.code = 'MODEL-05e5c6a6c8fe4f3c9bd1dd86dd24a659'
  AND f.code = 'F-c140d56fbb634e88a6a2feeb63f3b764'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  '', NULL,
  7, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-05e5c6a6c8fe4f3c9bd1dd86dd24a659'
  AND f.code = 'F-cc746ce0224145af88d5428d0b03213a'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  '', NULL,
  8, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-05e5c6a6c8fe4f3c9bd1dd86dd24a659'
  AND f.code = 'F-984f2b3cfb6b4931a4dbc4506c116522'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  AND m.code = 'MODEL-44cc7511fc224085a51e2887569c933a'
  AND f.code = 'F-dba78a08bcb04c3ca6a6a07c62dfd671'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  AND m.code = 'MODEL-44cc7511fc224085a51e2887569c933a'
  AND f.code = 'F-e0c3c64565e748d3b5a811014384e1e6'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  AND m.code = 'MODEL-44cc7511fc224085a51e2887569c933a'
  AND f.code = 'F-43ba10972c084c44a24a22ffb0b2185f'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  AND m.code = 'MODEL-44cc7511fc224085a51e2887569c933a'
  AND f.code = 'F-1ee037ca81bb475f94b3526134ea85bb'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  AND m.code = 'MODEL-44cc7511fc224085a51e2887569c933a'
  AND f.code = 'F-d55683af52444a7587242fbb3542a932'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  AND m.code = 'MODEL-bf41bd50df044f5faf43c4759353ccd1'
  AND f.code = 'F-dba78a08bcb04c3ca6a6a07c62dfd671'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  AND m.code = 'MODEL-bf41bd50df044f5faf43c4759353ccd1'
  AND f.code = 'F-e0c3c64565e748d3b5a811014384e1e6'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  AND m.code = 'MODEL-bf41bd50df044f5faf43c4759353ccd1'
  AND f.code = 'F-43ba10972c084c44a24a22ffb0b2185f'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  AND m.code = 'MODEL-bf41bd50df044f5faf43c4759353ccd1'
  AND f.code = 'F-1ee037ca81bb475f94b3526134ea85bb'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  AND m.code = 'MODEL-bf41bd50df044f5faf43c4759353ccd1'
  AND f.code = 'F-d55683af52444a7587242fbb3542a932'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  AND m.code = 'MODEL-5a9b87c42571486998d363101c54ec8c'
  AND f.code = 'F-dba78a08bcb04c3ca6a6a07c62dfd671'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  AND m.code = 'MODEL-5a9b87c42571486998d363101c54ec8c'
  AND f.code = 'F-e0c3c64565e748d3b5a811014384e1e6'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  AND m.code = 'MODEL-5a9b87c42571486998d363101c54ec8c'
  AND f.code = 'F-43ba10972c084c44a24a22ffb0b2185f'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  AND m.code = 'MODEL-5a9b87c42571486998d363101c54ec8c'
  AND f.code = 'F-1ee037ca81bb475f94b3526134ea85bb'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  AND m.code = 'MODEL-5a9b87c42571486998d363101c54ec8c'
  AND f.code = 'F-d55683af52444a7587242fbb3542a932'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  AND m.code = 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9'
  AND f.code = 'F-59e5799b2eaa4b0aa9c164f8d440409b'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  AND m.code = 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9'
  AND f.code = 'F-3d7fedf4c352424abeb1b11805dfd331'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  AND m.code = 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9'
  AND f.code = 'F-b42b6e004eb944e993e00f5fa734a37a'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  AND m.code = 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9'
  AND f.code = 'F-1ee037ca81bb475f94b3526134ea85bb'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  AND m.code = 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9'
  AND f.code = 'F-d55683af52444a7587242fbb3542a932'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  AND m.code = 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9'
  AND f.code = 'F-672e3aca3c9c46cb8f0c8fe85217b708'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  AND m.code = 'MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9'
  AND f.code = 'F-f2bbdc83208843e28026f785c7195ce5'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-0e171b9d21024fd183a1cb355e1b8c08'
  AND f.code = 'F-72697688b36e4399b25121108ef76b9a'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  AND m.code = 'MODEL-7c4fe9b2d52d41cca5383488a4a400a5'
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
  AND m.code = 'MODEL-7c4fe9b2d52d41cca5383488a4a400a5'
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
  1, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-7fc9012b394749a3b30e1788e4057324'
  AND f.code = 'F-e0c3c64565e748d3b5a811014384e1e6'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  AND m.code = 'MODEL-7fc9012b394749a3b30e1788e4057324'
  AND f.code = 'F-43ba10972c084c44a24a22ffb0b2185f'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  AND m.code = 'MODEL-7fc9012b394749a3b30e1788e4057324'
  AND f.code = 'F-1ee037ca81bb475f94b3526134ea85bb'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  AND m.code = 'MODEL-7fc9012b394749a3b30e1788e4057324'
  AND f.code = 'F-d55683af52444a7587242fbb3542a932'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  AND m.code = 'MODEL-1966e63fec48402f9b60280e66faea0f'
  AND f.code = 'F-dba78a08bcb04c3ca6a6a07c62dfd671'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  AND m.code = 'MODEL-1966e63fec48402f9b60280e66faea0f'
  AND f.code = 'F-e0c3c64565e748d3b5a811014384e1e6'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  AND m.code = 'MODEL-1966e63fec48402f9b60280e66faea0f'
  AND f.code = 'F-1ee037ca81bb475f94b3526134ea85bb'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  AND m.code = 'MODEL-1966e63fec48402f9b60280e66faea0f'
  AND f.code = 'F-d55683af52444a7587242fbb3542a932'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  AND m.code = 'MODEL-cac64b3a48254912bf410d8d2ceb748d'
  AND f.code = 'F-51b7d63aa97942439611cff6370fe65e'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  AND m.code = 'MODEL-cac64b3a48254912bf410d8d2ceb748d'
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
  3, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-cac64b3a48254912bf410d8d2ceb748d'
  AND f.code = 'F-49523f29afa94650ac591f607043dd65'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  AND m.code = 'MODEL-3ae58694e5ab4dc4a078c6e4b548f617'
  AND f.code = 'F-2407b65c556643adb72f94c0efbbba47'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  AND m.code = 'MODEL-3ae58694e5ab4dc4a078c6e4b548f617'
  AND f.code = 'F-1ee037ca81bb475f94b3526134ea85bb'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  AND m.code = 'MODEL-3ae58694e5ab4dc4a078c6e4b548f617'
  AND f.code = 'F-d55683af52444a7587242fbb3542a932'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  AND m.code = 'MODEL-a547e7b120434da0bc8d94f11447f75a'
  AND f.code = 'F-dba78a08bcb04c3ca6a6a07c62dfd671'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  AND m.code = 'MODEL-a547e7b120434da0bc8d94f11447f75a'
  AND f.code = 'F-e0c3c64565e748d3b5a811014384e1e6'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  AND m.code = 'MODEL-a547e7b120434da0bc8d94f11447f75a'
  AND f.code = 'F-1ee037ca81bb475f94b3526134ea85bb'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
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
  AND m.code = 'MODEL-a547e7b120434da0bc8d94f11447f75a'
  AND f.code = 'F-d55683af52444a7587242fbb3542a932'
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;
