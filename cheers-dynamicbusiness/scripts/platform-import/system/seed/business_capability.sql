-- ============================================================================
-- 系统 · business_capability
-- Generated: 2026-07-08 by scripts/export-platform-import.py
--
-- 约定：不写 surrogate id；幂等键为 code / field_code / page_code。
-- 幂等 upsert；关联字段按 code 解析 id，不写 surrogate id
-- ============================================================================

SET search_path TO dynamicbusiness;

-- business_capability: 35 row(s), upsert by entity_type_code

INSERT INTO business_capability (
  entity_type_code, capability_full, version, business_category, tenant_id, creator
) VALUES (
  'billing', '{"components": ["list", "tree", "table", "card"], "modelSummary": {"models": [], "modelCount": 0}, "entityTypeCode": "billing", "businessCategory": "dynamic", "capabilityVersion": 1}',
  1, 'dynamic',
  1, 'seed'
)
ON CONFLICT (entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  capability_full = EXCLUDED.capability_full,
  version = EXCLUDED.version,
  business_category = EXCLUDED.business_category,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO business_capability (
  entity_type_code, capability_full, version, business_category, tenant_id, creator
) VALUES (
  'customer', '{"components": ["list", "tree", "table", "card"], "modelSummary": {"models": [], "modelCount": 0}, "entityTypeCode": "customer", "businessCategory": "dynamic", "capabilityVersion": 1}',
  1, 'dynamic',
  1, 'seed'
)
ON CONFLICT (entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  capability_full = EXCLUDED.capability_full,
  version = EXCLUDED.version,
  business_category = EXCLUDED.business_category,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO business_capability (
  entity_type_code, capability_full, version, business_category, tenant_id, creator
) VALUES (
  'dept', '{"readUrl": "/system/dept/list", "paginated": false, "components": ["card", "table", "tree", "list"], "entityTypeCode": "dept", "businessCategory": "system", "capabilityVersion": 15}',
  15, 'system',
  1, 'seed'
)
ON CONFLICT (entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  capability_full = EXCLUDED.capability_full,
  version = EXCLUDED.version,
  business_category = EXCLUDED.business_category,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO business_capability (
  entity_type_code, capability_full, version, business_category, tenant_id, creator
) VALUES (
  'dictData', '{"readUrl": "/system/dict-data/page", "paginated": true, "components": ["card", "table", "tree", "list"], "entityTypeCode": "dictData", "businessCategory": "system", "capabilityVersion": 15}',
  15, 'system',
  1, 'seed'
)
ON CONFLICT (entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  capability_full = EXCLUDED.capability_full,
  version = EXCLUDED.version,
  business_category = EXCLUDED.business_category,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO business_capability (
  entity_type_code, capability_full, version, business_category, tenant_id, creator
) VALUES (
  'dictType', '{"readUrl": "/system/dict-type/page", "paginated": true, "components": ["card", "table", "tree", "list"], "entityTypeCode": "dictType", "businessCategory": "system", "capabilityVersion": 15}',
  15, 'system',
  1, 'seed'
)
ON CONFLICT (entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  capability_full = EXCLUDED.capability_full,
  version = EXCLUDED.version,
  business_category = EXCLUDED.business_category,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO business_capability (
  entity_type_code, capability_full, version, business_category, tenant_id, creator
) VALUES (
  'equipment', '{"components": ["list", "tree", "table", "card"], "modelSummary": {"models": [{"sort": 0, "status": 1, "modelId": 1, "modelCode": "MODEL-05e5c6a6c8fe4f3c9bd1dd86dd24a659", "modelName": "环网交换机"}, {"sort": 0, "status": 1, "modelId": 2, "modelCode": "MODEL-0e171b9d21024fd183a1cb355e1b8c08", "modelName": "220V电缆"}, {"sort": 0, "status": 1, "modelId": 3, "modelCode": "MODEL-123328b7bfe745ceb337d286ac64aa17", "modelName": "氧气传感器"}, {"sort": 0, "status": 1, "modelId": 4, "modelCode": "MODEL-1966e63fec48402f9b60280e66faea0f", "modelName": "应急灯"}, {"sort": 0, "status": 1, "modelId": 5, "modelCode": "MODEL-2af0515a5a36420086980b2322037edb", "modelName": "网络摄像机"}, {"sort": 0, "status": 1, "modelId": 6, "modelCode": "MODEL-3130eba3747e4a5aa0862c94807822ba", "modelName": "功分器"}, {"sort": 0, "status": 1, "modelId": 7, "modelCode": "MODEL-3335affd4a814e4dba54d613d07bb0fa", "modelName": "氧气检测仪"}, {"sort": 0, "status": 1, "modelId": 8, "modelCode": "MODEL-3ae58694e5ab4dc4a078c6e4b548f617", "modelName": "气溶胶"}, {"sort": 0, "status": 1, "modelId": 9, "modelCode": "MODEL-44cc7511fc224085a51e2887569c933a", "modelName": "轴流风机"}, {"sort": 0, "status": 1, "modelId": 10, "modelCode": "MODEL-50991eab2a714ed288d00c58ff01c810", "modelName": "IP电话机"}, {"sort": 0, "status": 1, "modelId": 11, "modelCode": "MODEL-5284b71c69d946ae88c0848bfbbb0d7c", "modelName": "测试模型"}, {"sort": 0, "status": 1, "modelId": 12, "modelCode": "MODEL-56b09ad2ef96439c87bda429e2bb9404", "modelName": "IP电话"}, {"sort": 0, "status": 1, "modelId": 13, "modelCode": "MODEL-5a9b87c42571486998d363101c54ec8c", "modelName": "消防泵"}, {"sort": 0, "status": 1, "modelId": 14, "modelCode": "MODEL-6671ca830f4742229c0a00ff531f974c", "modelName": "消防"}, {"sort": 0, "status": 1, "modelId": 15, "modelCode": "MODEL-68554821800248cfacacbac1a53cdf37", "modelName": "UPS不间断电源"}, {"sort": 0, "status": 1, "modelId": 16, "modelCode": "MODEL-6bf0e75bec6a496ba7200b99293c1417", "modelName": "单独"}, {"sort": 0, "status": 1, "modelId": 17, "modelCode": "MODEL-6fd43ab74f6f425f81f0ad547603a849", "modelName": "交换机"}, {"sort": 0, "status": 1, "modelId": 18, "modelCode": "MODEL-70d2b066120c4b969f0b661d1d2fb664", "modelName": "LED显示屏"}, {"sort": 0, "status": 1, "modelId": 19, "modelCode": "MODEL-7c4fe9b2d52d41cca5383488a4a400a5", "modelName": "电缆接头"}, {"sort": 0, "status": 1, "modelId": 20, "modelCode": "MODEL-7c746a60520347baaf64334625f07f31", "modelName": "定向天线"}, {"sort": 0, "status": 1, "modelId": 21, "modelCode": "MODEL-7fc9012b394749a3b30e1788e4057324", "modelName": "检修箱"}, {"sort": 0, "status": 1, "modelId": 22, "modelCode": "MODEL-81132ce00d2041c6a4ee06ec1c01dac8", "modelName": "门禁控制器"}, {"sort": 0, "status": 1, "modelId": 23, "modelCode": "MODEL-831d0957ceb748bab257d13e3b9859ce", "modelName": "未分类"}, {"sort": 0, "status": 1, "modelId": 24, "modelCode": "MODEL-9421d45e38434b73af04a09287c8f0b6", "modelName": "二氧化碳采集模型"}, {"sort": 0, "status": 1, "modelId": 25, "modelCode": "MODEL-990370e140aa481a82158bc96dc0b006", "modelName": "ACU柜"}, {"sort": 0, "status": 1, "modelId": 26, "modelCode": "MODEL-9b557c9404684caab2820eb0aec2cf15", "modelName": "按钮箱"}, {"sort": 0, "status": 1, "modelId": 27, "modelCode": "MODEL-a547e7b120434da0bc8d94f11447f75a", "modelName": "照明"}, {"sort": 0, "status": 1, "modelId": 28, "modelCode": "MODEL-a62c727c0a024e19a8221c68fcf0c09a", "modelName": "报警主机"}, {"sort": 0, "status": 1, "modelId": 29, "modelCode": "MODEL-ab375d26cb6e45eba5a4acfe789a9933", "modelName": "二氧化碳检测仪"}, {"sort": 0, "status": 1, "modelId": 30, "modelCode": "MODEL-b75b3fcac0f54eae9226c1cf99e24595", "modelName": "温度传感器"}, {"sort": 0, "status": 1, "modelId": 31, "modelCode": "MODEL-b8251da851514341adef7f6f72995cf3", "modelName": "无线远端机"}, {"sort": 0, "status": 1, "modelId": 32, "modelCode": "MODEL-b8ed7911c3664dafaf4073223792b5a4", "modelName": "温湿度检测仪"}, {"sort": 0, "status": 1, "modelId": 33, "modelCode": "MODEL-bc2cdffeab434f4d862c847fa99938ba", "modelName": "二氧化碳传感器"}, {"sort": 0, "status": 1, "modelId": 34, "modelCode": "MODEL-bd13c167e66b46bca791fae607c72ad7", "modelName": "门禁"}, {"sort": 0, "status": 1, "modelId": 35, "modelCode": "MODEL-bf41bd50df044f5faf43c4759353ccd1", "modelName": "给水泵"}, {"sort": 0, "status": 1, "modelId": 36, "modelCode": "MODEL-c55e44e65d5840c6ad73f0a0eb7e94d9", "modelName": "可燃气体探测器"}, {"sort": 0, "status": 1, "modelId": 37, "modelCode": "MODEL-c6bc32646f3e4bde8409120c5c290eee", "modelName": "光纤收发器"}, {"sort": 0, "status": 1, "modelId": 38, "modelCode": "MODEL-cac64b3a48254912bf410d8d2ceb748d", "modelName": "阀门"}, {"sort": 0, "status": 1, "modelId": 39, "modelCode": "MODEL-cd14851e79e649d797cdcc4a6e6f7fe5", "modelName": "氧气数据采集"}, {"sort": 0, "status": 1, "modelId": 40, "modelCode": "MODEL-d11cb8bb47e74d73b5d4233814de2a39", "modelName": "水泵"}, {"sort": 0, "status": 1, "modelId": 41, "modelCode": "MODEL-d446eef7329f45b4a374347e215fec5d", "modelName": "温湿度传感器"}, {"sort": 0, "status": 1, "modelId": 42, "modelCode": "MODEL-d57178112ab5465eb43f926bb18fd98c", "modelName": "湿度传感器"}, {"sort": 0, "status": 1, "modelId": 43, "modelCode": "MODEL-e1e2ab00b9cb41dab46f48856374ebd4", "modelName": "视频监控"}, {"sort": 0, "status": 1, "modelId": 44, "modelCode": "MODEL-e25f3089a99d4878a1ddd326ba0910bf", "modelName": "监控控制柜"}, {"sort": 0, "status": 1, "modelId": 45, "modelCode": "MODEL-e5af81c45bbc44d48374d8036032cc5d", "modelName": "红外探测器"}, {"sort": 0, "status": 1, "modelId": 46, "modelCode": "MODEL-e5c45ff8ca074102829bba872e091bd1", "modelName": "人员定位主机"}, {"sort": 0, "status": 1, "modelId": 47, "modelCode": "MODEL-f5270d478dd849389e59a178cd512629", "modelName": "移动终端"}, {"sort": 0, "status": 1, "modelId": 48, "modelCode": "MODEL-fd9456206abe46ae972a0dc15df082ec", "modelName": "控制箱"}], "modelCount": 48}, "entityTypeCode": "equipment", "businessCategory": "dynamic", "capabilityVersion": 1}',
  1, 'dynamic',
  1, 'seed'
)
ON CONFLICT (entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  capability_full = EXCLUDED.capability_full,
  version = EXCLUDED.version,
  business_category = EXCLUDED.business_category,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO business_capability (
  entity_type_code, capability_full, version, business_category, tenant_id, creator
) VALUES (
  'fault', '{"components": ["list", "tree", "table", "card"], "modelSummary": {"models": [], "modelCount": 0}, "entityTypeCode": "fault", "businessCategory": "dynamic", "capabilityVersion": 1}',
  1, 'dynamic',
  1, 'seed'
)
ON CONFLICT (entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  capability_full = EXCLUDED.capability_full,
  version = EXCLUDED.version,
  business_category = EXCLUDED.business_category,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO business_capability (
  entity_type_code, capability_full, version, business_category, tenant_id, creator
) VALUES (
  'inspection_item', '{"components": ["list", "tree", "table", "card"], "modelSummary": {"models": [], "modelCount": 0}, "entityTypeCode": "inspection_item", "businessCategory": "dynamic", "capabilityVersion": 1}',
  1, 'dynamic',
  1, 'seed'
)
ON CONFLICT (entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  capability_full = EXCLUDED.capability_full,
  version = EXCLUDED.version,
  business_category = EXCLUDED.business_category,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO business_capability (
  entity_type_code, capability_full, version, business_category, tenant_id, creator
) VALUES (
  'loginLog', '{"readUrl": "/system/login-log/page", "paginated": true, "components": ["card", "table", "tree", "list"], "entityTypeCode": "loginLog", "businessCategory": "system", "capabilityVersion": 15}',
  15, 'system',
  1, 'seed'
)
ON CONFLICT (entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  capability_full = EXCLUDED.capability_full,
  version = EXCLUDED.version,
  business_category = EXCLUDED.business_category,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO business_capability (
  entity_type_code, capability_full, version, business_category, tenant_id, creator
) VALUES (
  'mailAccount', '{"readUrl": "/system/mail-account/page", "paginated": true, "components": ["card", "table", "tree", "list"], "entityTypeCode": "mailAccount", "businessCategory": "system", "capabilityVersion": 15}',
  15, 'system',
  1, 'seed'
)
ON CONFLICT (entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  capability_full = EXCLUDED.capability_full,
  version = EXCLUDED.version,
  business_category = EXCLUDED.business_category,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO business_capability (
  entity_type_code, capability_full, version, business_category, tenant_id, creator
) VALUES (
  'mailLog', '{"readUrl": "/system/mail-log/page", "paginated": true, "components": ["card", "table", "tree", "list"], "entityTypeCode": "mailLog", "businessCategory": "system", "capabilityVersion": 15}',
  15, 'system',
  1, 'seed'
)
ON CONFLICT (entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  capability_full = EXCLUDED.capability_full,
  version = EXCLUDED.version,
  business_category = EXCLUDED.business_category,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO business_capability (
  entity_type_code, capability_full, version, business_category, tenant_id, creator
) VALUES (
  'mailTemplate', '{"readUrl": "/system/mail-template/page", "paginated": true, "components": ["card", "table", "tree", "list"], "entityTypeCode": "mailTemplate", "businessCategory": "system", "capabilityVersion": 15}',
  15, 'system',
  1, 'seed'
)
ON CONFLICT (entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  capability_full = EXCLUDED.capability_full,
  version = EXCLUDED.version,
  business_category = EXCLUDED.business_category,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO business_capability (
  entity_type_code, capability_full, version, business_category, tenant_id, creator
) VALUES (
  'maintenance', '{"components": ["list", "tree", "table", "card"], "modelSummary": {"models": [], "modelCount": 0}, "entityTypeCode": "maintenance", "businessCategory": "dynamic", "capabilityVersion": 1}',
  1, 'dynamic',
  1, 'seed'
)
ON CONFLICT (entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  capability_full = EXCLUDED.capability_full,
  version = EXCLUDED.version,
  business_category = EXCLUDED.business_category,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO business_capability (
  entity_type_code, capability_full, version, business_category, tenant_id, creator
) VALUES (
  'menu', '{"readUrl": "/system/menu/list", "paginated": false, "components": ["card", "table", "tree", "list"], "entityTypeCode": "menu", "businessCategory": "system", "capabilityVersion": 15}',
  15, 'system',
  1, 'seed'
)
ON CONFLICT (entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  capability_full = EXCLUDED.capability_full,
  version = EXCLUDED.version,
  business_category = EXCLUDED.business_category,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO business_capability (
  entity_type_code, capability_full, version, business_category, tenant_id, creator
) VALUES (
  'notice', '{"readUrl": "/system/notice/page", "paginated": true, "components": ["card", "table", "tree", "list"], "entityTypeCode": "notice", "businessCategory": "system", "capabilityVersion": 15}',
  15, 'system',
  1, 'seed'
)
ON CONFLICT (entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  capability_full = EXCLUDED.capability_full,
  version = EXCLUDED.version,
  business_category = EXCLUDED.business_category,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO business_capability (
  entity_type_code, capability_full, version, business_category, tenant_id, creator
) VALUES (
  'notifyMessage', '{"readUrl": "/system/notify-message/page", "paginated": true, "components": ["card", "table", "tree", "list"], "entityTypeCode": "notifyMessage", "businessCategory": "system", "capabilityVersion": 15}',
  15, 'system',
  1, 'seed'
)
ON CONFLICT (entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  capability_full = EXCLUDED.capability_full,
  version = EXCLUDED.version,
  business_category = EXCLUDED.business_category,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO business_capability (
  entity_type_code, capability_full, version, business_category, tenant_id, creator
) VALUES (
  'notifyMessageMy', '{"readUrl": "/system/notify-message/my-page", "paginated": true, "components": ["card", "table", "tree", "list"], "entityTypeCode": "notifyMessageMy", "businessCategory": "system", "capabilityVersion": 15}',
  15, 'system',
  1, 'seed'
)
ON CONFLICT (entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  capability_full = EXCLUDED.capability_full,
  version = EXCLUDED.version,
  business_category = EXCLUDED.business_category,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO business_capability (
  entity_type_code, capability_full, version, business_category, tenant_id, creator
) VALUES (
  'notifyTemplate', '{"readUrl": "/system/notify-template/page", "paginated": true, "components": ["card", "table", "tree", "list"], "entityTypeCode": "notifyTemplate", "businessCategory": "system", "capabilityVersion": 15}',
  15, 'system',
  1, 'seed'
)
ON CONFLICT (entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  capability_full = EXCLUDED.capability_full,
  version = EXCLUDED.version,
  business_category = EXCLUDED.business_category,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO business_capability (
  entity_type_code, capability_full, version, business_category, tenant_id, creator
) VALUES (
  'oauth2Client', '{"readUrl": "/system/oauth2-client/page", "paginated": true, "components": ["card", "table", "tree", "list"], "entityTypeCode": "oauth2Client", "businessCategory": "system", "capabilityVersion": 15}',
  15, 'system',
  1, 'seed'
)
ON CONFLICT (entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  capability_full = EXCLUDED.capability_full,
  version = EXCLUDED.version,
  business_category = EXCLUDED.business_category,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO business_capability (
  entity_type_code, capability_full, version, business_category, tenant_id, creator
) VALUES (
  'oauth2Token', '{"readUrl": "/system/oauth2-token/page", "paginated": true, "components": ["card", "table", "tree", "list"], "entityTypeCode": "oauth2Token", "businessCategory": "system", "capabilityVersion": 15}',
  15, 'system',
  1, 'seed'
)
ON CONFLICT (entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  capability_full = EXCLUDED.capability_full,
  version = EXCLUDED.version,
  business_category = EXCLUDED.business_category,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO business_capability (
  entity_type_code, capability_full, version, business_category, tenant_id, creator
) VALUES (
  'operateLog', '{"readUrl": "/system/operate-log/page", "paginated": true, "components": ["card", "table", "tree", "list"], "entityTypeCode": "operateLog", "businessCategory": "system", "capabilityVersion": 15}',
  15, 'system',
  1, 'seed'
)
ON CONFLICT (entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  capability_full = EXCLUDED.capability_full,
  version = EXCLUDED.version,
  business_category = EXCLUDED.business_category,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO business_capability (
  entity_type_code, capability_full, version, business_category, tenant_id, creator
) VALUES (
  'post', '{"readUrl": "/system/post/page", "paginated": true, "components": ["card", "table", "tree", "list"], "entityTypeCode": "post", "businessCategory": "system", "capabilityVersion": 15}',
  15, 'system',
  1, 'seed'
)
ON CONFLICT (entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  capability_full = EXCLUDED.capability_full,
  version = EXCLUDED.version,
  business_category = EXCLUDED.business_category,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO business_capability (
  entity_type_code, capability_full, version, business_category, tenant_id, creator
) VALUES (
  'region', '{"components": ["list", "tree", "table", "card"], "modelSummary": {"models": [], "modelCount": 0}, "entityTypeCode": "region", "businessCategory": "dynamic", "capabilityVersion": 1}',
  1, 'dynamic',
  1, 'seed'
)
ON CONFLICT (entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  capability_full = EXCLUDED.capability_full,
  version = EXCLUDED.version,
  business_category = EXCLUDED.business_category,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO business_capability (
  entity_type_code, capability_full, version, business_category, tenant_id, creator
) VALUES (
  'role', '{"readUrl": "/system/role/page", "paginated": true, "components": ["card", "table", "tree", "list"], "entityTypeCode": "role", "businessCategory": "system", "capabilityVersion": 15}',
  15, 'system',
  1, 'seed'
)
ON CONFLICT (entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  capability_full = EXCLUDED.capability_full,
  version = EXCLUDED.version,
  business_category = EXCLUDED.business_category,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO business_capability (
  entity_type_code, capability_full, version, business_category, tenant_id, creator
) VALUES (
  'smsChannel', '{"readUrl": "/system/sms-channel/page", "paginated": true, "components": ["card", "table", "tree", "list"], "entityTypeCode": "smsChannel", "businessCategory": "system", "capabilityVersion": 15}',
  15, 'system',
  1, 'seed'
)
ON CONFLICT (entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  capability_full = EXCLUDED.capability_full,
  version = EXCLUDED.version,
  business_category = EXCLUDED.business_category,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO business_capability (
  entity_type_code, capability_full, version, business_category, tenant_id, creator
) VALUES (
  'smsLog', '{"readUrl": "/system/sms-log/page", "paginated": true, "components": ["card", "table", "tree", "list"], "entityTypeCode": "smsLog", "businessCategory": "system", "capabilityVersion": 15}',
  15, 'system',
  1, 'seed'
)
ON CONFLICT (entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  capability_full = EXCLUDED.capability_full,
  version = EXCLUDED.version,
  business_category = EXCLUDED.business_category,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO business_capability (
  entity_type_code, capability_full, version, business_category, tenant_id, creator
) VALUES (
  'smsTemplate', '{"readUrl": "/system/sms-template/page", "paginated": true, "components": ["card", "table", "tree", "list"], "entityTypeCode": "smsTemplate", "businessCategory": "system", "capabilityVersion": 15}',
  15, 'system',
  1, 'seed'
)
ON CONFLICT (entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  capability_full = EXCLUDED.capability_full,
  version = EXCLUDED.version,
  business_category = EXCLUDED.business_category,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO business_capability (
  entity_type_code, capability_full, version, business_category, tenant_id, creator
) VALUES (
  'socialClient', '{"readUrl": "/system/social-client/page", "paginated": true, "components": ["card", "table", "tree", "list"], "entityTypeCode": "socialClient", "businessCategory": "system", "capabilityVersion": 15}',
  15, 'system',
  1, 'seed'
)
ON CONFLICT (entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  capability_full = EXCLUDED.capability_full,
  version = EXCLUDED.version,
  business_category = EXCLUDED.business_category,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO business_capability (
  entity_type_code, capability_full, version, business_category, tenant_id, creator
) VALUES (
  'socialUser', '{"readUrl": "/system/social-user/page", "paginated": true, "components": ["card", "table", "tree", "list"], "entityTypeCode": "socialUser", "businessCategory": "system", "capabilityVersion": 15}',
  15, 'system',
  1, 'seed'
)
ON CONFLICT (entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  capability_full = EXCLUDED.capability_full,
  version = EXCLUDED.version,
  business_category = EXCLUDED.business_category,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO business_capability (
  entity_type_code, capability_full, version, business_category, tenant_id, creator
) VALUES (
  'spare_parts', '{"components": ["list", "tree", "table", "card"], "modelSummary": {"models": [], "modelCount": 0}, "entityTypeCode": "spare_parts", "businessCategory": "dynamic", "capabilityVersion": 1}',
  1, 'dynamic',
  1, 'seed'
)
ON CONFLICT (entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  capability_full = EXCLUDED.capability_full,
  version = EXCLUDED.version,
  business_category = EXCLUDED.business_category,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO business_capability (
  entity_type_code, capability_full, version, business_category, tenant_id, creator
) VALUES (
  'tenant', '{"readUrl": "/system/tenant/page", "paginated": true, "components": ["card", "table", "tree", "list"], "entityTypeCode": "tenant", "businessCategory": "system", "capabilityVersion": 15}',
  15, 'system',
  1, 'seed'
)
ON CONFLICT (entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  capability_full = EXCLUDED.capability_full,
  version = EXCLUDED.version,
  business_category = EXCLUDED.business_category,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO business_capability (
  entity_type_code, capability_full, version, business_category, tenant_id, creator
) VALUES (
  'tenantPackage', '{"readUrl": "/system/tenant-package/page", "paginated": true, "components": ["card", "table", "tree", "list"], "entityTypeCode": "tenantPackage", "businessCategory": "system", "capabilityVersion": 15}',
  15, 'system',
  1, 'seed'
)
ON CONFLICT (entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  capability_full = EXCLUDED.capability_full,
  version = EXCLUDED.version,
  business_category = EXCLUDED.business_category,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO business_capability (
  entity_type_code, capability_full, version, business_category, tenant_id, creator
) VALUES (
  'user', '{"readUrl": "/system/user/page", "paginated": true, "components": ["card", "table", "tree", "list"], "entityTypeCode": "user", "businessCategory": "system", "capabilityVersion": 15}',
  15, 'system',
  1, 'seed'
)
ON CONFLICT (entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  capability_full = EXCLUDED.capability_full,
  version = EXCLUDED.version,
  business_category = EXCLUDED.business_category,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO business_capability (
  entity_type_code, capability_full, version, business_category, tenant_id, creator
) VALUES (
  'view', '{"components": ["list", "tree", "table", "card"], "modelSummary": {"models": [], "modelCount": 0}, "entityTypeCode": "view", "businessCategory": "dynamic", "capabilityVersion": 1}',
  1, 'dynamic',
  1, 'seed'
)
ON CONFLICT (entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  capability_full = EXCLUDED.capability_full,
  version = EXCLUDED.version,
  business_category = EXCLUDED.business_category,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO business_capability (
  entity_type_code, capability_full, version, business_category, tenant_id, creator
) VALUES (
  'zone', '{"components": ["list", "tree", "table", "card"], "modelSummary": {"models": [{"sort": 1, "status": 1, "modelId": 57, "modelCode": "MODEL-ZONE-TANK-GROUP", "modelName": "罐组"}, {"sort": 2, "status": 1, "modelId": 58, "modelCode": "MODEL-ZONE-WAREHOUSE", "modelName": "库棚"}, {"sort": 3, "status": 1, "modelId": 59, "modelCode": "MODEL-ZONE-FUNCTIONAL", "modelName": "功能分区"}, {"sort": 4, "status": 1, "modelId": 60, "modelCode": "MODEL-ZONE-BUILDING", "modelName": "楼栋"}], "modelCount": 4}, "entityTypeCode": "zone", "businessCategory": "dynamic", "capabilityVersion": 1}',
  1, 'dynamic',
  1, 'seed'
)
ON CONFLICT (entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  capability_full = EXCLUDED.capability_full,
  version = EXCLUDED.version,
  business_category = EXCLUDED.business_category,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;
