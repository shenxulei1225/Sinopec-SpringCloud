-- ============================================================================
-- 智慧管廊 · 06_categories.sql
-- Generated: 2026-07-05 by scripts/export-platform-import.py
--
-- 约定：不写 surrogate id；幂等键为 code / field_code / page_code。
-- 依赖：先导入 system/ 全包（设备模型库已在 system/05_models.sql）
-- 含 region→equipment 等跨业务模型关联
-- ============================================================================

SET search_path TO dynamicbusiness;

-- dynamic_category_type: 16 row(s), upsert by category_type_code

INSERT INTO dynamic_category_type (
  category_type_code, name, description, status, top_level_category_id, tenant_id, creator
) VALUES (
  'billing', '计费分类',
  '从 system_category 同步的分类维度', 1,
  (SELECT c.id FROM dynamic_category c WHERE c.deleted = false AND c.tenant_id = 1 AND c.code = 'billing_management' LIMIT 1), 1, 'seed'
)
ON CONFLICT (category_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  top_level_category_id = EXCLUDED.top_level_category_id,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category_type (
  category_type_code, name, description, status, top_level_category_id, tenant_id, creator
) VALUES (
  'customer', '客户分类',
  '从 system_category 同步的分类维度', 1,
  (SELECT c.id FROM dynamic_category c WHERE c.deleted = false AND c.tenant_id = 1 AND c.code = 'customer_management' LIMIT 1), 1, 'seed'
)
ON CONFLICT (category_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  top_level_category_id = EXCLUDED.top_level_category_id,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category_type (
  category_type_code, name, description, status, top_level_category_id, tenant_id, creator
) VALUES (
  'emergency_event', '应急事件分类',
  '从 system_category 同步的分类维度', 1,
  (SELECT c.id FROM dynamic_category c WHERE c.deleted = false AND c.tenant_id = 1 AND c.code = 'emergency_event_root' LIMIT 1), 1, 'seed'
)
ON CONFLICT (category_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  top_level_category_id = EXCLUDED.top_level_category_id,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category_type (
  category_type_code, name, description, status, top_level_category_id, tenant_id, creator
) VALUES (
  'emergency_resource', '应急资源分类',
  '从 system_category 同步的分类维度', 1,
  (SELECT c.id FROM dynamic_category c WHERE c.deleted = false AND c.tenant_id = 1 AND c.code = 'emergency_resource' LIMIT 1), 1, 'seed'
)
ON CONFLICT (category_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  top_level_category_id = EXCLUDED.top_level_category_id,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category_type (
  category_type_code, name, description, status, top_level_category_id, tenant_id, creator
) VALUES (
  'emergency_team', '应急队伍分类',
  '从 system_category 同步的分类维度', 1,
  (SELECT c.id FROM dynamic_category c WHERE c.deleted = false AND c.tenant_id = 1 AND c.code = 'emergency_team' LIMIT 1), 1, 'seed'
)
ON CONFLICT (category_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  top_level_category_id = EXCLUDED.top_level_category_id,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category_type (
  category_type_code, name, description, status, top_level_category_id, tenant_id, creator
) VALUES (
  'equipment', '设备分类',
  '从 system_category 同步的分类维度', 1,
  (SELECT c.id FROM dynamic_category c WHERE c.deleted = false AND c.tenant_id = 1 AND c.code = 'equipment_root' LIMIT 1), 1, 'seed'
)
ON CONFLICT (category_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  top_level_category_id = EXCLUDED.top_level_category_id,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category_type (
  category_type_code, name, description, status, top_level_category_id, tenant_id, creator
) VALUES (
  'fault', '故障分类',
  '从 system_category 同步的分类维度', 1,
  (SELECT c.id FROM dynamic_category c WHERE c.deleted = false AND c.tenant_id = 1 AND c.code = 'CAT-7044185a339d44b18ca887fd380bd4da' LIMIT 1), 1, 'seed'
)
ON CONFLICT (category_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  top_level_category_id = EXCLUDED.top_level_category_id,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category_type (
  category_type_code, name, description, status, top_level_category_id, tenant_id, creator
) VALUES (
  'inspection_point', '点位分类',
  '从 system_category 同步的分类维度', 1,
  (SELECT c.id FROM dynamic_category c WHERE c.deleted = false AND c.tenant_id = 1 AND c.code = 'point_management' LIMIT 1), 1, 'seed'
)
ON CONFLICT (category_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  top_level_category_id = EXCLUDED.top_level_category_id,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category_type (
  category_type_code, name, description, status, top_level_category_id, tenant_id, creator
) VALUES (
  'maintenance', '维护分类',
  '从 system_category 同步的分类维度', 1,
  (SELECT c.id FROM dynamic_category c WHERE c.deleted = false AND c.tenant_id = 1 AND c.code = 'CAT-0e463104c39b4d45abceb568ef581251' LIMIT 1), 1, 'seed'
)
ON CONFLICT (category_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  top_level_category_id = EXCLUDED.top_level_category_id,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category_type (
  category_type_code, name, description, status, top_level_category_id, tenant_id, creator
) VALUES (
  'patrol', '巡检分类',
  '从 system_category 同步的分类维度', 1,
  (SELECT c.id FROM dynamic_category c WHERE c.deleted = false AND c.tenant_id = 1 AND c.code = 'inspection_task' LIMIT 1), 1, 'seed'
)
ON CONFLICT (category_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  top_level_category_id = EXCLUDED.top_level_category_id,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category_type (
  category_type_code, name, description, status, top_level_category_id, tenant_id, creator
) VALUES (
  'pipeline', '管线分类',
  '从 system_category 同步的分类维度', 1,
  (SELECT c.id FROM dynamic_category c WHERE c.deleted = false AND c.tenant_id = 1 AND c.code = 'CAT-24a59ec705e445d8a7ae32d91cd9ebbe' LIMIT 1), 1, 'seed'
)
ON CONFLICT (category_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  top_level_category_id = EXCLUDED.top_level_category_id,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category_type (
  category_type_code, name, description, status, top_level_category_id, tenant_id, creator
) VALUES (
  'region', '区域分类',
  '油库站场 Region 分类树（Pattern C）', 1,
  (SELECT c.id FROM dynamic_category c WHERE c.deleted = false AND c.tenant_id = 1 AND c.code = 'region_root' LIMIT 1), 1, 'seed'
)
ON CONFLICT (category_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  top_level_category_id = EXCLUDED.top_level_category_id,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category_type (
  category_type_code, name, description, status, top_level_category_id, tenant_id, creator
) VALUES (
  'repair', '维修分类',
  '从 system_category 同步的分类维度', 1,
  (SELECT c.id FROM dynamic_category c WHERE c.deleted = false AND c.tenant_id = 1 AND c.code = 'repair_root' LIMIT 1), 1, 'seed'
)
ON CONFLICT (category_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  top_level_category_id = EXCLUDED.top_level_category_id,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category_type (
  category_type_code, name, description, status, top_level_category_id, tenant_id, creator
) VALUES (
  'route', '路线分类',
  '从 system_category 同步的分类维度', 1,
  (SELECT c.id FROM dynamic_category c WHERE c.deleted = false AND c.tenant_id = 1 AND c.code = 'route_management' LIMIT 1), 1, 'seed'
)
ON CONFLICT (category_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  top_level_category_id = EXCLUDED.top_level_category_id,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category_type (
  category_type_code, name, description, status, top_level_category_id, tenant_id, creator
) VALUES (
  'survey_table', 'survey_table',
  '从 system_category 同步的分类维度', 1,
  (SELECT c.id FROM dynamic_category c WHERE c.deleted = false AND c.tenant_id = 1 AND c.code = 'survey_table_root' LIMIT 1), 1, 'seed'
)
ON CONFLICT (category_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  top_level_category_id = EXCLUDED.top_level_category_id,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category_type (
  category_type_code, name, description, status, top_level_category_id, tenant_id, creator
) VALUES (
  'task', '任务分类',
  '从 system_category 同步的分类维度', 1,
  (SELECT c.id FROM dynamic_category c WHERE c.deleted = false AND c.tenant_id = 1 AND c.code = 'task_root' LIMIT 1), 1, 'seed'
)
ON CONFLICT (category_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  top_level_category_id = EXCLUDED.top_level_category_id,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;


-- dynamic_category: 1151 row(s), upsert by code; parent by parent_code

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  NULL, '维修管理', 'CAT-0e463104c39b4d45abceb568ef581251',
  'maintenance', 10,
  1, 'nWTVjuaPI+T54YeHo4WvVYTk/5xDa95pp3o/KeIKMM+3pK5Ho3slkvLOECYRLHMuMw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  NULL, '通信管线', 'CAT-24a59ec705e445d8a7ae32d91cd9ebbe',
  'pipeline', 1,
  1, '', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  NULL, '排水管', 'CAT-2dd030f14c1b417cacf1d04f0936d68e',
  'pipeline', 2,
  1, NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  NULL, '电缆类', 'CAT-3244b76d0ae8441794ec034fb2d18d24',
  'equipment', 2,
  1, NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  NULL, '排水管', 'CAT-3662feaa236c4b4f914a3b001e88cdcf',
  'pipeline', 2,
  1, NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  NULL, '污水管线', 'CAT-3dc6e5abf775401c8e796fe2af985fc1',
  'pipeline', 1,
  1, '', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  NULL, '其他管线', 'CAT-4a1d2d3d34794cb8b7ccb9c44540dd8d',
  'pipeline', 3,
  1, NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  NULL, '给水管', 'CAT-50a1f0cc2a844121bed29fd72c0fec0b',
  'pipeline', 1,
  1, NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  NULL, '按故障类型', 'CAT-547aabea233b4eda9a37c5228ed2ac2c',
  'fault', 2,
  1, NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  NULL, '按设备类型', 'CAT-7044185a339d44b18ca887fd380bd4da',
  'fault', 1,
  1, NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  NULL, '电力管线', 'CAT-7e0b3f23b59d4ec6baca7b58b16bc89a',
  'pipeline', 1,
  1, '', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  NULL, '按钮箱', 'CAT-84a8075ee569476aba6b24617c06d119',
  'equipment', 15,
  1, NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  NULL, '阀门', 'CAT-8fdfb85801254c99a890cdf97ba2c40a',
  'equipment', 8,
  1, NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  NULL, '设备类', 'CAT-998365cd36bd4e0790ceaef1e1f7fb6b',
  'equipmentt', 3,
  1, NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  NULL, '给水管', 'CAT-9999540dac00472b99a37adfb562e919',
  'pipeline', 1,
  1, NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  NULL, '温湿度检测仪', 'CAT-9ea830ae31e84d10bbc016996dae3f98',
  'equipment', 11,
  1, NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  NULL, '控制箱', 'CAT-ab0b8858ff474ece95b59fa45c39bcbd',
  'equipment', 13,
  1, NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  NULL, '其他管线', 'CAT-ab342c064d5444d885e07940fa8b9ef4',
  'pipeline', 3,
  1, NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  NULL, '冷热水管线', 'CAT-d1034e99827d4cc282ca2cfbe8573a0d',
  'pipeline', 1,
  1, '', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  NULL, '二氧化碳检测仪', 'CAT-d355a71a4fa94170bc674e3985cd1734',
  'equipment', 9,
  1, NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  NULL, '氧气检测仪', 'CAT-da780bb53e264b469803bbaa6dd6eddd',
  'equipment', 10,
  1, NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  NULL, '维修管理', 'CAT-f3160aac16414874b61409df3b0ea9ef',
  'maintenance', 10,
  1, 'rQ5IfJ3JKc4+LYcJt0KNdsHm7pWo8+Ku5xBKNzVaBTTLDYyHYS7WvEPXHiD3WJye9w==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  NULL, '按维护类型', 'CAT-f5bf103c356b42eca62b7e1428e4b036',
  'maintenance', 2,
  1, NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  NULL, '按设备类型', 'CAT-f7f8896b2fe0464a85ac16e9f471c21b',
  'maintenance', 1,
  1, NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  NULL, '费用分类', 'billing_management',
  'billing', 0,
  0, NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  NULL, '客户分类', 'customer_management',
  'customer', 0,
  0, NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  NULL, '应急事件分类', 'emergency_event_root',
  'emergency_event', 0,
  1, NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  NULL, '应急资源分类', 'emergency_resource',
  'emergency_resource', 0,
  0, NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  NULL, '应急队伍分类', 'emergency_team',
  'emergency_team', 0,
  0, NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  NULL, 'equipment_root', 'equipment_root',
  'equipment', 0,
  1, 'auto recovered missing parent', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  NULL, '检查分类', 'inspection_item',
  'inspection_item', 0,
  0, NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  NULL, '巡检任务', 'inspection_task',
  'patrol', 1,
  0, NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  NULL, '点位分类', 'point_management',
  'inspection_point', 0,
  0, NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  NULL, 'region_root', 'region_root',
  'region', 0,
  1, 'auto recovered missing parent', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  NULL, '维修分类', 'repair_root',
  'repair', 0,
  1, 'auto-created root category', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  NULL, '路线分类', 'route_management',
  'route', 0,
  0, NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  NULL, 'survey_table_root', 'survey_table_root',
  'survey_table', 0,
  1, 'auto-created root category', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  NULL, '任务分类', 'task_root',
  'task', 0,
  1, 'auto-created root category', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'emergency_team' LIMIT 1), '消防队', 'CAT-01bfd584a6a54690aaaa1ffd0186056c',
  'emergency_team', 4,
  1, '', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'inspection_item' LIMIT 1), '管线检查', 'CAT-02c0ea78ed5947b68177097fd70f70c5',
  'inspection_item', 2,
  1, '', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'emergency_team' LIMIT 1), '医疗队', 'CAT-03f5705ec37d4b6c96ebf592fdb1d678',
  'emergency_team', 2,
  1, '', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'inspection_item' LIMIT 1), '设备检查', 'CAT-045b2445cd184fbf81f418bc56fcc0f9',
  'inspection_item', 1,
  1, '', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'equipment_root' LIMIT 1), '光纤收发器', 'CAT-04d9a56142114da1bbf7be1f014033e3',
  'equipment', 3,
  1, 'DW5OLtD607ooM0IwEzmFUQAv9XOWnlu2yI4HQeR0v84RkRZlnQuPSTZ/2OD+oybBYfFJEg==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-0e463104c39b4d45abceb568ef581251' LIMIT 1), '班组', 'CAT-074c2048bd384ac4b2144ae3939ae599',
  'maintenance', 40,
  1, 'PkuaqE2okthKFMLZCVtisa8rLefnDAgyaEEg7CXJMSuf6+4KtGXo2GC93XooXX4CN4U=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'equipment_root' LIMIT 1), '安防系统', 'CAT-0e10a00013d1474da7fb182eaef57f99',
  'equipment', 1,
  1, '7lpOBWaveSBHB5zywj2WKJpLT0X78m1nU24loG1smy8xVGSzTgOq1MJ/Teo3mxRozM6bxiTaC3LsKr34sHOoe6uguvaG/kUrDrBPTDxekA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-f3160aac16414874b61409df3b0ea9ef' LIMIT 1), '维修类型', 'CAT-139dc073a16e410498345f3f875ad2fc',
  'maintenance', 20,
  1, '1x5WW2R7qVWhDSs6tm1DHikyo/EN0jrcK9lX/gVs5eHpuUcFp1QoFw/F3nCD0Zb9LTkOLSLTg60qax0=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'equipment_root' LIMIT 1), '供配电系统', 'CAT-1494db0c0d5c4bd5964843f704447472',
  'equipment', 9,
  1, 'xeuB5EsXyU81rikXmLOB/7GbzgMUJDTnxl91JfEWtoUXKgArBtMDTsqskzapZ6lJHkF0BKThwjiwh1JN4w==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'point_management' LIMIT 1), '环境巡检点位', 'CAT-19abeb5375bf484ca8e39d41b57c6394',
  'inspection_point', 2,
  1, '', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'equipment_root' LIMIT 1), '电力与配电系统', 'CAT-1cb18a0a06d347b28e0d140e6bbdfcf3',
  'equipment', 10,
  1, 'hXi34ckvCyYsjStkSyRVD7LVR6pX8u+iKY7VCq0ZSeOYoY5bX9lh7LMbaZBzNcEE3QXfmuW7nZ8daw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-0e463104c39b4d45abceb568ef581251' LIMIT 1), '工单来源', 'CAT-1df88349b51f4a5a8a5f43d005ac386e',
  'maintenance', 30,
  1, '7E8C4yIOaXvd7JO8x4YcsmQcgeD98xAzh3bqMgF1lo12jboa3xbsX64XlwNpL5+Pk2Ebtcxdb4Yc', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'equipment_root' LIMIT 1), '消防系统', 'CAT-2856506514844d17900faaa00376cb56',
  'equipment', 12,
  1, 'gWJ5ro+iodu6gsk8XlgjR9mYL5oLd/UCvw5fPTZAGbXcHuspiXGW9Si87cvYIT/VlwsSFpOlupAPnoxg6XnxsCuzdA51Gg==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'equipment_root' LIMIT 1), '人员定位系统', 'CAT-335c10c8157f48248bf72de3cd4870e8',
  'equipment', 5,
  1, '7tuub2/Scy5g+Gq3S446PphMw0xp7V0CRINdo7v3romtpSHavNgnNvfvbo7nQPOIoru6EA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-0e463104c39b4d45abceb568ef581251' LIMIT 1), '维修类型', 'CAT-3564ba3f44a84212951bbdf18458ef93',
  'maintenance', 20,
  1, 'eZMkUgxkjbXXuEb7EEgFMrxJTZAVDga7CK+sl/saDv1MkW0iT6Lc864GUUp8UNwR4wxNWYvs7YHpG84=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'region_root' LIMIT 1), '光谷六路北', 'CAT-430dac26bff34adea697bb8e748c4b40',
  'region', 2,
  1, 'BIK775Gs/p0gGBrcNiPXr5yMMerN/eEtTu0mCuT7R5gcnlwrWUWtY3dpjnpZFf9EyADDiaxPyg==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'emergency_team' LIMIT 1), '警卫队', 'CAT-45440e7cd8ba442caccb9353d2502a1c',
  'emergency_team', 3,
  1, '', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'emergency_event_root' LIMIT 1), '自然灾害', 'CAT-4b520ee5ab834570a9b8a167c4085df6',
  'emergency_event', 1,
  1, '', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'route_management' LIMIT 1), '完整巡检', 'CAT-4cf9facb4255454bbac5838a03be3482',
  'route', 3,
  1, '', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'region_root' LIMIT 1), '神墩五路', 'CAT-4ddee51e92a743148bda50c2df7e2df5',
  'region', 4,
  1, 'sbDw9A0zRRiHwPy2i80g4RlGTuwe9eqcIg/cyIiWYxkRxr1L2mnnE387Xc95eDuEYk8V0Q==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'billing_management' LIMIT 1), '服务费', 'CAT-59afede8eb2b4354ad0231f94f2dbb18',
  'billing', 3,
  1, '', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'equipment_root' LIMIT 1), '机房环境监控', 'CAT-59eba1a897b24f31ba54f346d572c79a',
  'equipment', 8,
  1, 'anK2YmVvM8x/AgZqOk6EnWDSSJrkC7R8D+ScRuu71zcL0CyKXMvnjI1MHw160aD1xnSnSg==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'region_root' LIMIT 1), '神墩三路', 'CAT-5e29c5bfd3af4a368bdd5245763971ab',
  'region', 3,
  1, 'g5tf+JVrFljelq9dhJsKeml+fHopSSJVWrvmMejRVtSmKJycutKBrEQWjChtUux/texnhQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'emergency_event_root' LIMIT 1), '公共卫生', 'CAT-5e3507659d764d74ae82370b842896ae',
  'emergency_event', 3,
  1, '', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'inspection_item' LIMIT 1), '安全检查', 'CAT-6120015764a74ee09693eb42eec15ff1',
  'inspection_item', 3,
  1, '', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'inspection_item' LIMIT 1), '消防检查', 'CAT-6202c8318f4b429e996a8fd7aa4362d5',
  'inspection_item', 4,
  1, '', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'route_management' LIMIT 1), '分段路线', 'CAT-665f0a32406a4e2f8ff5832c46378080',
  'route', 2,
  1, '', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'billing_management' LIMIT 1), '咨询费', 'CAT-72276d1161be4d18b1f629ff98094d65',
  'billing', 1,
  1, '', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'emergency_event_root' LIMIT 1), '事故灾害', 'CAT-728437fef0994ab3971af0a3e569ad38',
  'emergency_event', 2,
  1, '', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'equipment_root' LIMIT 1), '无线对讲系统', 'CAT-830d64d0d51440bdb0206722d9718532',
  'equipment', 4,
  1, '2YOhto6nwxz9P9KYEhoORoKhhTOtc9VmfiReKXuQeZ/xYNlv9QlxYPSqbqYQog==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'equipment_root' LIMIT 1), '视频监控与安防', 'CAT-85feb1443e8f42afb1a84754e7d7ce5f',
  'equipment', 13,
  1, '9oHoG8mt8CrGPj6LHvMf5lBWp4q40dpTc4iDX4IUXJReLcpcKP8d0TcW9QQ5FOAZHS2wZ3rHNTj9IjPw6A==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'customer_management' LIMIT 1), '企业客户', 'CAT-89f9edc4c1ba40e8a99aff80d0d188b9',
  'customer', 1,
  1, '', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'region_root' LIMIT 1), '虎山东街', 'CAT-8bf1e9121b6f41c89444705107a15d40',
  'region', 5,
  1, 'BwZ3m9jLIWoNoUXNky5VrBl1tVwY3oJ1epqaAuSePitF6kmlZhWgBJ2SuBdTwXHCA9bmXA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'equipment_root' LIMIT 1), '大屏显示系统', 'CAT-8e7ad58f49034d94b08732fba393a71b',
  'equipment', 7,
  1, 'px7QrUQeu5tID2asQ+vTH4ojwnveP0FTn9CahYPctQjmI6jYHE2SekU/0ujtjyCZdp+buJ7/N9LDZEDU9W9KZMUGRw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'equipment_root' LIMIT 1), '环控系统', 'CAT-a61e8a70173249e28002b05eaec9f438',
  'equipment', 6,
  1, 'I7hdUBpTh763+yapHlt9RLrrm3h8+B6qewl2vOBb7NjScV31AWeQdbBISiBoWA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'region_root' LIMIT 1), '高新大道', 'CAT-a860b6fc92ae4f8b9cd8e67345700a5e',
  'region', 6,
  1, 'G+CMEOVdHYgx0wkDZoJYIOEg+tpDdj4fVkCRb07shbMYprI0DR+Bu/hen3UXEr+XEdpH0w==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'inspection_item' LIMIT 1), '卫生检查', 'CAT-b1b0c22c3dad48eba074468e83e4ec23',
  'inspection_item', 5,
  1, '', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'region_root' LIMIT 1), '高科园路南', 'CAT-b49ea247330542a5ba3edcfb8865d2b0',
  'region', 8,
  1, 'cEDzDSVM5SEAvir22hEkg8Xn3eDAMPIgkVWBDldI4g8ZKHy55wRz1/yPRIoxICTQwGUMg9dtCA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'region_root' LIMIT 1), '高科园路北', 'CAT-ba33a80994a24cbc81da3b686c28e835',
  'region', 7,
  1, 'Ow6HbjZwTBP6XGm6X/lNzFUVKzyXUuqtQeo9ifJR1cL+g7O1lRkKyovWpFmhnWFyMcj+LlESFg==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'billing_management' LIMIT 1), '维护费', 'CAT-bdd181058ad94901a22861345859c0f9',
  'billing', 4,
  1, '', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-f3160aac16414874b61409df3b0ea9ef' LIMIT 1), '工单来源', 'CAT-c07754b9888a4c15ae0dfb39a300ceac',
  'maintenance', 30,
  1, '3Q7cBDNOLUfvmH76mgGxSw+2Ro6rHGBCM01gWXslAEiYXBkKgMPzYyyrT98WcMYdJDFCj/RIXdqh', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-f3160aac16414874b61409df3b0ea9ef' LIMIT 1), '班组', 'CAT-c866cc44366d415e80325c690b142df9',
  'maintenance', 40,
  1, 'EIpE8LjPAadeXsBvOzH8qkTtYMY+zMZOMhCfduf6Tp4inJPpLAfxmMR+RCdBB/5QjVs=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'emergency_resource' LIMIT 1), '车辆', 'CAT-c9ecb1e0a89e465e9f1ce24029c1f25f',
  'emergency_resource', 1,
  1, '', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'emergency_team' LIMIT 1), '抢险队', 'CAT-cb35dd3aa55a4ecd84b966cbc24a4b1d',
  'emergency_team', 1,
  1, '', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'inspection_task' LIMIT 1), '日常巡检', 'CAT-d28e02fa89b94c03af17a73e7dc7a671',
  'patrol', 1,
  1, '', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'route_management' LIMIT 1), '日常路线', 'CAT-d739b820188948b68cdca13b7725659a',
  'route', 1,
  1, '', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'point_management' LIMIT 1), '设备巡检点位', 'CAT-d7f919dcd5e94ff8b64a95cded5b532c',
  'inspection_point', 1,
  1, '', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'equipment_root' LIMIT 1), '通信与网络', 'CAT-e0cd4f3eecb64831b7f99e166be95bf6',
  'equipment', 14,
  1, 'qPFA4+LV+ENcYOcEdVYqL7Qqwd2/T6fNe6B/iCHuQ3llFJhUCUKQh5IplWU7ookfGIzHSI8LYnRbmkICTrhYVg==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'emergency_event_root' LIMIT 1), '社会安全', 'CAT-e3e145a3cf4e4ff595f9ed5318a629a8',
  'emergency_event', 4,
  1, '', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'billing_management' LIMIT 1), '实施费', 'CAT-e59ce49baa754113bede51f5fdb1cca4',
  'billing', 2,
  1, '', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'equipment_root' LIMIT 1), '环境与检测', 'CAT-eb4575c0015b405891a8d45565598a80',
  'equipment', 15,
  1, 'BeNThMjULIAPmhgoJc8wKMO7hkswVkh01uAcXht32+tsJX6QSxQcYpUT0KqSNk0USA3t8lwpvYg3rTZMgSJFJs/phQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'equipment_root' LIMIT 1), 'IP电话系统', 'CAT-eea3d5ebafd543b0b6f72a2f76a42cd6',
  'equipment', 2,
  1, 'SFjnvKNLjq+upL1qC8eGQSL6VI/iOL1+tMWOyq3xCvGUeJvx8z38HhePPW7qVs3VA/JApw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'region_root' LIMIT 1), '光谷五路北', 'CAT-f2861f0d6ac24813bcc35be1269538d9',
  'region', 1,
  1, 'GdAFDR9FkD4mBs6hVPFnNXR9z93gqJEU5WZM8AShj71efl+kEYxvVgsOHMzyoWmmqVsC3VzBMg==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'equipment_root' LIMIT 1), '通风与给排水系统', 'CAT-f80e2048b994430a936090f9d3e1df23',
  'equipment', 11,
  1, 'Hc8e/gTwCku3w4PVvKfEzGyRI4m7O5EMPAKTa5O6tTfxWOoO23g045WyQou30tvYAHtMUdorpjjMog==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'customer_management' LIMIT 1), '个人客户', 'CAT-fb73f2893e924cfea0adba4f33c14ea2',
  'customer', 2,
  1, '', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'emergency_resource' LIMIT 1), '物资', 'CAT-fedacea2a831481888b037f3b51092ca',
  'emergency_resource', 2,
  1, '', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'region_root' LIMIT 1), '光谷六路南', 'GG6LN',
  'region', 100,
  1, 'egL6iPXVHJchr8vApjm0zfiDpC4QxnLrMq3MTddvCtRK+gjbXbloml7yf7gc7ZPNWOUcQGswIA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'region_root' LIMIT 1), '托月路', 'TYL',
  'region', 100,
  1, 'M1Rw/cPT1t2+BB6aDK2g3Pdl2+L/Wf/GMKvA6TDTlju/TqbKD6PRIAKU2a+EQelDIQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-3564ba3f44a84212951bbdf18458ef93' LIMIT 1), '整改工单', 'CAT-009e5e3d6c884871a665b961ee99a619',
  'maintenance', 4,
  1, 'HrRY8/IbB48UTCFs5R2HcMu5X1S1IKpTQ5UZAdgLw5yfiBchs8pLQDdNB0xbT1Y=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-72276d1161be4d18b1f629ff98094d65' LIMIT 1), '专项评估费', 'CAT-04b9066ada834cf2b18feae3b63b7aa3',
  'billing', 2,
  1, '', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-0e10a00013d1474da7fb182eaef57f99' LIMIT 1), 'LED显示屏', 'CAT-0588c6878c894a62b541f1159ae0a021',
  'equipment', 5,
  1, 'BPpmppLthrKdJR/x/YDBfiw2A7nPBWz6LeE4osIxK0aLgejvgSM3Wu5bCfCuKFDGlQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-bdd181058ad94901a22861345859c0f9' LIMIT 1), '软件维护费', 'CAT-07eb52919b8944c1a22a52c0096151ac',
  'billing', 1,
  1, '', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-c9ecb1e0a89e465e9f1ce24029c1f25f' LIMIT 1), '工程作业车', 'CAT-092f482895594bb9b29018d64646ea63',
  'emergency_resource', 3,
  1, '', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-0e10a00013d1474da7fb182eaef57f99' LIMIT 1), '网络摄像机', 'CAT-0e61fad294274a4ea28e863d0cd97517',
  'equipment', 3,
  1, 'e7xALxYzBYlTzLC1gPXOV1J+gEkh04VZoqxu4fPsKA96gHenHQu+klrW8Hpb7EPBQqUUHA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-c866cc44366d415e80325c690b142df9' LIMIT 1), '电力运维班', 'CAT-17089837fc9047a68c37f112030940c7',
  'maintenance', 1,
  1, 'Lykok3/sjTt443Z+PJdGclJmgHC86UbsppuA4ScbfC+VxDPEJ3cMSwBex6leg+ZuQQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-0e10a00013d1474da7fb182eaef57f99' LIMIT 1), '监控控制柜', 'CAT-185a3ec231b745aa865bb57d1407cc55',
  'equipment', 4,
  1, 'CfPPUwYHXc+4uaDpruCE3kMgVkPSvsTmNCnC0DCo7/pyvwtjeVLwLMhc6vAwXWAhxw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-5e29c5bfd3af4a368bdd5245763971ab' LIMIT 1), '6#防火区', 'CAT-1b468a9c7b8e44d7ad664dc096fff9e5',
  'region', 6,
  1, 'vp9bAjtIXqHeBrUvdWF7Ub1TDQ9hNuWCt8dnKH/kyzi9VUGKAr/NvSognuXFTJNkVvom', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-830d64d0d51440bdb0206722d9718532' LIMIT 1), '无线远端机', 'CAT-1c7a22b5811d4059a6ed562b028f0c9f',
  'equipment', 2,
  1, 'yaVkkS8B64qnLLz9l+mc47Wq+kTSnUrpTexgdHc23S6XQq2QXn9Ng20xN7aq68/F0Pfw2PoqWRP04w==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-eb4575c0015b405891a8d45565598a80' LIMIT 1), '检修箱', 'CAT-1ca47692b77749fbb03abfe8fe15d23b',
  'equipment', 2,
  1, NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-0e10a00013d1474da7fb182eaef57f99' LIMIT 1), '红外探测器', 'CAT-1d053d73e166452cb2e118c5f74e1d02',
  'equipment', 6,
  1, 'fyhihs3BQJkQOXQTgOzxmKTc6LVFuLO5A9bhze9l/s//LLGK5lQipM4VEZM+IABes6aVqg==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-a61e8a70173249e28002b05eaec9f438' LIMIT 1), '温湿度传感器', 'CAT-1dba544cfa114d5d98513c92280cd58a',
  'equipment', 2,
  1, '/i0PxW044ZLlQ5mjzJaAri85QrcqU9KhsMWZNxKkdW/q0f3YU56p+YG0rkYeqJ3P7w6bM9Dg3A==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-c07754b9888a4c15ae0dfb39a300ceac' LIMIT 1), '巡检发现', 'CAT-2587772035474637aa33c8230bf9c3d3',
  'maintenance', 2,
  1, 'N6dvO0PLElN6pLB35M0CkR4Gw242CdKT5kJuUrDdb9UEvuE+xkn9vjHa/as9//NFQWgWcQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-1df88349b51f4a5a8a5f43d005ac386e' LIMIT 1), '巡检发现', 'CAT-27d65adcc8184e578c48678682072912',
  'maintenance', 2,
  1, 'JLFPnh4laJ9z/G1REBO6wvK664pI5EEnKegwVo3TWrqnDIjjkIAcdXdwZW6J2rA396Udaw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-074c2048bd384ac4b2144ae3939ae599' LIMIT 1), '环控运维班', 'CAT-2ed16ed10a8746be958053fc4834d172',
  'maintenance', 2,
  1, 'Nn4KDaAg32ffMwI+n8rDAeiRgbLjlh8GkWDPSnmVKgjUrv/8c7WoDgCyCdbBuBjmk85Lz9mYocqCtQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-1df88349b51f4a5a8a5f43d005ac386e' LIMIT 1), '告警联动', 'CAT-3c23b90a8e5e4126bd91f577071e61f7',
  'maintenance', 1,
  1, 'hnaB38X5D5EjdX8bR0NMlL/JoY00yFNMbZgklv4OJ717BP8e4gLc85/3dgE/UeHNiw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-1df88349b51f4a5a8a5f43d005ac386e' LIMIT 1), '人工报修', 'CAT-3c96aca8bb32466089f21b831eb30c4a',
  'maintenance', 3,
  1, 'bdza1z1LOEV2gdAtp7puiTye4SgdiWO816RL7hyixVglfrgxNuXbGMaryFhzVnHcy3ODDBP4', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-c07754b9888a4c15ae0dfb39a300ceac' LIMIT 1), '告警联动', 'CAT-4200e46bfd6a420c86f572bde9c0acf9',
  'maintenance', 1,
  1, '5ge2w822QQtVZWjXLdFq20Os9YoOLqjsGjlWBM226saLGpbERQNUrKTFVWBOUe8U8A==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-139dc073a16e410498345f3f875ad2fc' LIMIT 1), '故障维修', 'CAT-44f620f618a048b6bd968fee6430cd06',
  'maintenance', 1,
  1, 'PhOl6VOIpqCypf3sFL6qbGgPn46n+hO4wZSL5g6L/Mwzqy8meq7PXxrexMaFUa+YW4CXwn87QA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-4b520ee5ab834570a9b8a167c4085df6' LIMIT 1), '暴雨', 'CAT-498bc2edd54f4580bbf246e41144da39',
  'emergency_event', 2,
  1, '', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-830d64d0d51440bdb0206722d9718532' LIMIT 1), '定向天线', 'CAT-4b2affb3ea004405b48a723eed987835',
  'equipment', 3,
  1, 'HuPbsS8HgoTO5Q3vEYc5TUmwUaYPRYmiLfcNFq2mRYnPv11ZMNX+J0cqj2YauONE5BShtQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-3564ba3f44a84212951bbdf18458ef93' LIMIT 1), '预防性维护', 'CAT-4ba351fcb4564c18a2573783a47189fe',
  'maintenance', 2,
  1, 'iZ7qjaB7ZxGV/g6W3m4u50Trp5dgKj5YTqikSQ/fzA9Rl3ZLneHWrI/j6vdJMX/tWYs=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-bdd181058ad94901a22861345859c0f9' LIMIT 1), '驻场维护费', 'CAT-4cf7c6c692f0423082ece11620882f48',
  'billing', 2,
  1, '', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-728437fef0994ab3971af0a3e569ad38' LIMIT 1), '触电事故', 'CAT-4d5ba5bba1da4840b8a3b905d8334a3d',
  'emergency_event', 2,
  1, '', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-e59ce49baa754113bede51f5fdb1cca4' LIMIT 1), '方案设计费', 'CAT-6cd7da9b52254d2c815023e24af33df2',
  'billing', 1,
  1, '', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-c9ecb1e0a89e465e9f1ce24029c1f25f' LIMIT 1), '通讯车', 'CAT-79f9e529f98e49aba77bf235201dda66',
  'emergency_resource', 1,
  1, '', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-5e3507659d764d74ae82370b842896ae' LIMIT 1), '传染病', 'CAT-7b6e50b475994349a60d41576d8fb1a8',
  'emergency_event', 2,
  1, '', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-c9ecb1e0a89e465e9f1ce24029c1f25f' LIMIT 1), '指挥车', 'CAT-7ed264db1d52477ea90c2804f2d4d2e0',
  'emergency_resource', 2,
  1, '', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-3564ba3f44a84212951bbdf18458ef93' LIMIT 1), '紧急抢修', 'CAT-8081228828744cf7816a7c90a53935b6',
  'maintenance', 3,
  1, 'MGosycDpxjTwpE+ZL6old2LcCUCjCDZQrJW4y4bP/RiYgo/2Ufd4zSH4l4fw4PKpF5pqCmVxuKIlqXTs/X8Xavyj1AU=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-5e3507659d764d74ae82370b842896ae' LIMIT 1), '食物中毒', 'CAT-857d70fa941d4b449336e75cfc717d67',
  'emergency_event', 1,
  1, '', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-074c2048bd384ac4b2144ae3939ae599' LIMIT 1), '消防维保（外委）', 'CAT-874b02893b8142e69f473e8b7c92c14b',
  'maintenance', 3,
  1, 'Dut2ZMVSdIfDe4n5IIscONu37r8Lyb6v3V6iMlo+EqY+sBmBjP02sg==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-728437fef0994ab3971af0a3e569ad38' LIMIT 1), '火灾事故', 'CAT-8896b8082f524244870f698d4637d32b',
  'emergency_event', 1,
  1, '', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-eb4575c0015b405891a8d45565598a80' LIMIT 1), '传感器类', 'CAT-8b78a09ac93047919711c911c7bfb706',
  'equipment', 1,
  1, NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-fedacea2a831481888b037f3b51092ca' LIMIT 1), '医疗物资', 'CAT-8c75ad7dc7d3490a99bf820a6586eaf8',
  'emergency_resource', 1,
  1, '', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-3564ba3f44a84212951bbdf18458ef93' LIMIT 1), '故障维修', 'CAT-8ce4920eb9244916ac709c8578414191',
  'maintenance', 1,
  1, 'PqiuWvgqUWOd2g2IRsUVW5RozKMMA4OG5DewJE7auxBPE/9yNkFu8+KnpyNKBoyCm3IXiYje+w==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-e59ce49baa754113bede51f5fdb1cca4' LIMIT 1), '部署安装费', 'CAT-a43cb9dcc1b548b99835ac5579d779dc',
  'billing', 2,
  1, '', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-d7f919dcd5e94ff8b64a95cded5b532c' LIMIT 1), '生产设备巡检点位', 'CAT-a6bdb8f6fb1946b4be560bbe39d08d3d',
  'inspection_point', 1,
  1, '', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-1cb18a0a06d347b28e0d140e6bbdfcf3' LIMIT 1), '照明', 'CAT-a7052a7e383b4c0d93548a12a8963246',
  'equipment', 1,
  1, NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-139dc073a16e410498345f3f875ad2fc' LIMIT 1), '整改工单', 'CAT-b1450b462b9b4f70b7cdee30395cc72d',
  'maintenance', 4,
  1, 'YRXG8R7uqJCRfh1f0theuoSmRKHewmm/PTLmKGALHD9o7AiChyZ3Tx/6b4bmq6A=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-139dc073a16e410498345f3f875ad2fc' LIMIT 1), '紧急抢修', 'CAT-b36de2a166764d2da2d787f2a17a811f',
  'maintenance', 3,
  1, 'r8n2fhbbL327KdslEd0FoV1ZDq/r9+1/u2TWkxx0py+9jex1CDMvPlgxXBuM/GDAYT5c0iiVYXNKhe7ef95YeFDzhOY=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-4b520ee5ab834570a9b8a167c4085df6' LIMIT 1), '地震', 'CAT-b56c138cefbe476e831e5923770ea225',
  'emergency_event', 1,
  1, '', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-335c10c8157f48248bf72de3cd4870e8' LIMIT 1), '人员定位主机', 'CAT-b7edb01fec184cb38bcd1a47e2aa7552',
  'equipment', 1,
  1, 'JOMYSPKUqtm/YFV/vNdbkjz5obMaw+WwRCkp28M1EsSZr9Lsu8I7ryhP7k5TtHIZgp9DvA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-2856506514844d17900faaa00376cb56' LIMIT 1), '气溶胶', 'CAT-b8f4fbb9236549dca1dc05d637c77cf8',
  'equipment', 1,
  1, NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-59afede8eb2b4354ad0231f94f2dbb18' LIMIT 1), '代办服务费', 'CAT-b8fce1a81c0b4b4cad7b0b3f870c95d3',
  'billing', 1,
  1, '', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-72276d1161be4d18b1f629ff98094d65' LIMIT 1), '业务咨询费', 'CAT-b99c8239d5794a2cab0bfc5529ac84bd',
  'billing', 1,
  1, '', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-0e10a00013d1474da7fb182eaef57f99' LIMIT 1), '门禁控制器', 'CAT-bb4e3d114d69414bb0583c3ad88ce8da',
  'equipment', 1,
  1, 'GoWKRF3LLdfwrqj21hRjWtPm6uNAxrsgEupg9aAF0V3tCVPHXmCLu7lgFVb5bgjsKUyujg==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-a61e8a70173249e28002b05eaec9f438' LIMIT 1), '二氧化碳传感器', 'CAT-c116ab6c252b41fbb0cb8d593fb1cc5c',
  'equipment', 4,
  1, '10rOUmrusDMK4oTM1ZY/Du7rAuJpnBn3o17lG3Qe0WXOYiPRqUZcGhI3ywGcFsX1lh17mm7YIiOUauVf0A==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-335c10c8157f48248bf72de3cd4870e8' LIMIT 1), '移动终端', 'CAT-c3826e005fd34127a0c10ac16c988cb8',
  'equipment', 2,
  1, '3lNBawQRUFcmwTYZmlvpKh7/rK6GUMU9VKWEaMeq5uXxk38ExSQKQLlXHgLqssJRHiRd9zBgrDpgjg==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-fedacea2a831481888b037f3b51092ca' LIMIT 1), '警戒物资', 'CAT-c93e68731c8e4cef82cc60cfa84ec683',
  'emergency_resource', 2,
  1, '', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-830d64d0d51440bdb0206722d9718532' LIMIT 1), '功分器', 'CAT-d13851574c684b91af4a2e641cd352be',
  'equipment', 1,
  1, '4/3noQfE6yuMMqSDDRuIzxWo+ZHmVf8KZwk2TQU0Y5cyeOP/1ZxHXZExkRFAreaaxg==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-139dc073a16e410498345f3f875ad2fc' LIMIT 1), '预防性维护', 'CAT-d2b4c4c53519485382b1a762abf6d6b8',
  'maintenance', 2,
  1, 'O1zOTQbM65zpgySnz7YHrRYwT7VUlH6hxE0eOh8i8nAYdi85vigmcVb+uAz0fU/m7BE=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-e3e145a3cf4e4ff595f9ed5318a629a8' LIMIT 1), '群体性事件', 'CAT-d965ecb8e0ba45fb830a43c360eb7177',
  'emergency_event', 1,
  1, '', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-e0cd4f3eecb64831b7f99e166be95bf6' LIMIT 1), '网络设备', 'CAT-df84bc4e4c3d48be9ed1e4a1f075de56',
  'equipment', 1,
  1, 'ZvPc8V/zzLwmD/oHkTwkCHpU0tVjOHnMQN/8qCOyKK6oaSbWHquk4AQv82BpeSS2Cu8B8fH6WkQfmeBRwA2g3g==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-074c2048bd384ac4b2144ae3939ae599' LIMIT 1), '电力运维班', 'CAT-e0157093465649e1b45222517b0ea3e2',
  'maintenance', 1,
  1, 'Kbmc/RmxwIxQBBKtTXKajkjz896zEa1s1FFWROy7StrUM9eZ91Gh61U7rTUERgalhg==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-c07754b9888a4c15ae0dfb39a300ceac' LIMIT 1), '人工报修', 'CAT-e12ae5683f1743cda415e3069f6f0ccc',
  'maintenance', 3,
  1, 'rju69nprXoItJ/TW9cKPAhdBgp7BGi92uo02OeaZ7CzR+fFJOyqMDn8du/W59Nd1J3DYoZp/', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-a61e8a70173249e28002b05eaec9f438' LIMIT 1), 'ACU柜', 'CAT-e2f08e3a32bc43a9afb9a2207b3d62f6',
  'equipment', 1,
  1, 'tZ6AZUbpOW5A/hKsluJLwxuHM5r+sddoxPsPXENOk31BsvWtM7kjC5ijHNZvtsg6kg==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-f80e2048b994430a936090f9d3e1df23' LIMIT 1), '水泵', 'CAT-ee1126c3a9814693b048d195da938fde',
  'equipment', 1,
  1, NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-0e10a00013d1474da7fb182eaef57f99' LIMIT 1), '报警主机', 'CAT-ee163d552060451380f778c6f74b7b18',
  'equipment', 2,
  1, 'Vn/hY+BtrbfGUxRgQcnB0Zx7KKTxwx2A0fzrQAvHN0bAagCUfExNPOkZcd6FctQIhNVO1Q==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-d7f919dcd5e94ff8b64a95cded5b532c' LIMIT 1), '应急资源巡检点位', 'CAT-efb133ff6885484b8059fc312d5e083b',
  'inspection_point', 2,
  1, '', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-a61e8a70173249e28002b05eaec9f438' LIMIT 1), '氧气传感器', 'CAT-f1640b756fc2427595b93b21f89ec99d',
  'equipment', 3,
  1, 'MB5xoQZ4N58L9QpF8HRFUWXnrW5Cg/CWBz7B5wuH6KCo8Ktk7KdqkpY7zg58VDdK6a1AOe0wiw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-c866cc44366d415e80325c690b142df9' LIMIT 1), '消防维保（外委）', 'CAT-f540301dfb1e44d6b0210fda2f312076',
  'maintenance', 3,
  1, 'wh1B/GdfWjqdAnVNUMfbqEQyQ0Xz4DFJbhV1VnOy9xTMalV3DWtaxA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-728437fef0994ab3971af0a3e569ad38' LIMIT 1), '管内液体泄漏', 'CAT-f657d6cac863493087164731fdd69f21',
  'emergency_event', 3,
  1, '', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-c866cc44366d415e80325c690b142df9' LIMIT 1), '环控运维班', 'CAT-f7bced9a2aaf4a3ba739811cb9d57b50',
  'maintenance', 2,
  1, 'jOpP+6eFHbkojIrjVuVVeAmnUac24dtQEPdAUvBt6Ja0iTy1XuaKkxoMSa0kQcn6+qPBR4sQbio+DQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-f2861f0d6ac24813bcc35be1269538d9' LIMIT 1), '1#防火区', 'Region.GG5LB.01',
  'region', 1,
  1, 'tFyK2fdgnXbtxN4gc2am3hfUykT10FDI8aelaeN5XbaLT4IMFq77/O7ltq3rJbxRj5oY', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-f2861f0d6ac24813bcc35be1269538d9' LIMIT 1), '2#防火区', 'Region.GG5LB.02',
  'region', 2,
  1, 'BX1bx73VcYjPnJf2127Jid0/SY6bZsXH1szaNELmPeQpnDcHvMEMEM2c9b4VbDe31l0t', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-f2861f0d6ac24813bcc35be1269538d9' LIMIT 1), '4#防火区', 'Region.GG5LB.04',
  'region', 4,
  1, 'Y8ehlF7LDJCB4wZQK2tlodvxmSjslDo6H/9nciojoo/SCdg8dV/bJ9xQU05+GcLCTlLj', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-f2861f0d6ac24813bcc35be1269538d9' LIMIT 1), '5#防火区', 'Region.GG5LB.05',
  'region', 5,
  1, 'Fd0zUtSr0XdAf9vYnNjsXgHviunKTUIAmVrahJa4AT/lqRVwfO//7FNm51Pz7Vo9vw46', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-f2861f0d6ac24813bcc35be1269538d9' LIMIT 1), '6#防火区', 'Region.GG5LB.06',
  'region', 6,
  1, 'gYoXM7D7KYPDnGuRFFEc4aVPR50krvWJQR9NMTCdshNJjsDD1+8vn5PnPfjNPCG71X9S', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-f2861f0d6ac24813bcc35be1269538d9' LIMIT 1), '7#防火区', 'Region.GG5LB.07',
  'region', 7,
  1, '1xg+aUDAGunHqJ8WcKhypR1xjNnLTpKlT2XY+MVvbE7l25bgjKV+JyLlPBbPbn/f+6Cg', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-f2861f0d6ac24813bcc35be1269538d9' LIMIT 1), '8#防火区', 'Region.GG5LB.08',
  'region', 8,
  1, 'uwhblWSMZzmn9apnn34V66TqzY9tnXl18XHk7V1wUHDjWHV/VdwYvcc2p4/GfEEtVifn', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-f2861f0d6ac24813bcc35be1269538d9' LIMIT 1), '9#防火区', 'Region.GG5LB.09',
  'region', 9,
  1, 'QueQ9vjEGQ0L+LirA26CnBzHZq/M1iUEhGZjx29AaxYSDnTDzarqbwTPJjl4BNbQfJz+', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-f2861f0d6ac24813bcc35be1269538d9' LIMIT 1), '10#防火区', 'Region.GG5LB.10',
  'region', 10,
  1, 'HIxbvxka0LlWaD3+PiMrgHqTaoVKDljBvVJwzgQ6VMZzK299YrgM5ZcwD7AlgJTIkywS7A==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-f2861f0d6ac24813bcc35be1269538d9' LIMIT 1), '11#防火区', 'Region.GG5LB.11',
  'region', 11,
  1, 'Ftt43560W8qFtgPYL7jQBL1j2nbD6siD6d3csilqwcbejHqic00NCPPHug+nExBjTvNaDw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-f2861f0d6ac24813bcc35be1269538d9' LIMIT 1), '12#防火区', 'Region.GG5LB.12',
  'region', 12,
  1, 'F6wEePtsZeP+6+5yjQ5tdDrV/wGrKB0rOOWTrgMLV/1RDQM7ajElpf/xIcpcmPWpdS/2Og==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-f2861f0d6ac24813bcc35be1269538d9' LIMIT 1), '13#防火区', 'Region.GG5LB.13',
  'region', 13,
  1, 'Ut73jdWYIDOxhXBT7g1BmV+l+r/HTDV1sbeB7YZLzcAXRXRFBI2o/JVYL2fysunK/nCZvA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-f2861f0d6ac24813bcc35be1269538d9' LIMIT 1), '14#防火区', 'Region.GG5LB.14',
  'region', 14,
  1, 'G1WTifv8OTobfFmDLZ55cHskmkKGjjCrsK+E3Qd5Rj/liuP5nOalkdmLlO4ZQE9ptS7a5A==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-f2861f0d6ac24813bcc35be1269538d9' LIMIT 1), '15#防火区', 'Region.GG5LB.15',
  'region', 15,
  1, '0Q2IC9kmE6xDIRNcoc51ykPbXcQb067010l7XnPLlobjk3HrJapWtfSPIT6tK8I/nKPvIg==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-f2861f0d6ac24813bcc35be1269538d9' LIMIT 1), '16#防火区', 'Region.GG5LB.16',
  'region', 16,
  1, 'yujwZgxpG3rTut0a4BEJhlnW//ulclaaEe5lCeT4we9n06VL4yTdD1cYjLLCTnmNA+XcNg==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-f2861f0d6ac24813bcc35be1269538d9' LIMIT 1), '17#防火区', 'Region.GG5LB.17',
  'region', 17,
  1, 'rhXBHbvC50FGPlQQFoshVEa/tc+V0cxsRl8CTJBl1uWoIqENRZkBhcc4861QBcgUh6WIOw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-f2861f0d6ac24813bcc35be1269538d9' LIMIT 1), '18#防火区', 'Region.GG5LB.18',
  'region', 18,
  1, 'U+FyTjOrOM8H5s1VI5OEjDFLJAw0l5+NTlvnnOJC0fBEo6BjTX+1Guc54LCNFEpngb7HoQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-f2861f0d6ac24813bcc35be1269538d9' LIMIT 1), '19#防火区', 'Region.GG5LB.19',
  'region', 19,
  1, 'miqUg9Q5YUj/p5XT4A39wBFcKKDK4QzrS9GmSgbQPLEmIvFqB3FedYfZGyiw5ptK0y4xYw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-f2861f0d6ac24813bcc35be1269538d9' LIMIT 1), '20#防火区', 'Region.GG5LB.20',
  'region', 20,
  1, '85tlb5jDDcoYkk+2eUMOyxqR/WadC821l9Ttwh3ObKnFwvATm0pXwrG5flp+Q0WTkJfdZg==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-f2861f0d6ac24813bcc35be1269538d9' LIMIT 1), '21#防火区', 'Region.GG5LB.21',
  'region', 21,
  1, 'L4nTZEk2J9uP4+7vwVeGCHSOwGzON6RX/VY2UWwvBxiU58li0IpOIEyXBQzjmalUSDkH/A==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-f2861f0d6ac24813bcc35be1269538d9' LIMIT 1), '22#防火区', 'Region.GG5LB.22',
  'region', 22,
  1, 'cWr08TzuEZIQV+1oYWfIZBe/q21qE5tf2zR5hskCsgf38Ooiiqj2YjjIWZdi7/Kq0gwPJQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-f2861f0d6ac24813bcc35be1269538d9' LIMIT 1), '23#防火区', 'Region.GG5LB.23',
  'region', 23,
  1, '/GSxYR9Crwg3jURWo4/QMlxDUb26sp4eW6gVRxjiFmhh0FEGZN8wjnpHBurItVPz8ND0PQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-430dac26bff34adea697bb8e748c4b40' LIMIT 1), '1#防火区', 'Region.GG6LB.SS01',
  'region', 1,
  1, 'HvT5kOGPoJT4HiV5OarirxaEUTI9GVwLl+GG8JtNUJ6B8nj3cJpdLuBSuq/+TP7Nq8D8', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-430dac26bff34adea697bb8e748c4b40' LIMIT 1), '2#防火区', 'Region.GG6LB.SS02',
  'region', 2,
  1, 'HqMvSLGt2mWl9JSxoOSnbExrXdCy40rpRnBuAmtJ1HS1xQSgCHgHZrTwUdIBpK2EFKnn', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-430dac26bff34adea697bb8e748c4b40' LIMIT 1), '3#防火区', 'Region.GG6LB.SS03',
  'region', 3,
  1, '0uDOfglQBlAeD6Fq+DkPw4VSGucfNW1jgVAJZ00uhLpZVKfDzQAhYN2QugLptsN837Ob', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-430dac26bff34adea697bb8e748c4b40' LIMIT 1), '4#防火区', 'Region.GG6LB.SS04',
  'region', 4,
  1, 'D+H7AGh7UbL5wchq7FxrC46DGoT2NTluRemG6AaEE8E8ZIOH6pWDQ/ewtIDvgLxLPD4+', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-430dac26bff34adea697bb8e748c4b40' LIMIT 1), '5#防火区', 'Region.GG6LB.SS05',
  'region', 5,
  1, 'eH4w4ctC9RfYZUQoZqBNN9A/146wjf5x6gb7CkxJ5Nz4SfS22zfAkjOOnhc4QnLkcU7q', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-430dac26bff34adea697bb8e748c4b40' LIMIT 1), '6#防火区', 'Region.GG6LB.SS06',
  'region', 6,
  1, 'YRweou4PNzwdyw+LdDjVDgLiZ9G+9hFS75Py7AZniPyBI+DMiOtw8Ls4r2fkliKxYFF1', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-430dac26bff34adea697bb8e748c4b40' LIMIT 1), '7#防火区', 'Region.GG6LB.SS07',
  'region', 7,
  1, 'YGFhm+KpBUTK0ld5kLl6+ndQ8GBNFD7jF2zZhLaGBbtjAM3EpJoIfRmRT7zyM8rAWQAy', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-430dac26bff34adea697bb8e748c4b40' LIMIT 1), '8#防火区', 'Region.GG6LB.SS08',
  'region', 8,
  1, 'n1TIUmGSDtZ3lAmoXy9IimldssZjxxINK3YxXtivQbP4V4lxMZJbvRXNm8gN2/ANCzAU', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-430dac26bff34adea697bb8e748c4b40' LIMIT 1), '9#防火区', 'Region.GG6LB.SS09',
  'region', 9,
  1, 'V26/5qP+L9z/BY6bAKqTsiVdjRRx7wNMn1WlZUi/gisOAKl5JcLW4ugX/4uD0WsPtF7M', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-430dac26bff34adea697bb8e748c4b40' LIMIT 1), '10#防火区', 'Region.GG6LB.SS10',
  'region', 10,
  1, 'vTIiJ7306xWGjopACaymIWusTpgWlIBL2YWHIGqFsPKVM/M6ToSnvJzNN9Ax0AaftiyNog==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-430dac26bff34adea697bb8e748c4b40' LIMIT 1), '11#防火区', 'Region.GG6LB.SS11',
  'region', 11,
  1, 'QzlG5jnW7TPcGKneZoFGGvO3IdJ3Vk8t2zqYwlaNkrZRoHHAu3GQjCzg9vmbA1LczZ7OHA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'GG6LN' LIMIT 1), '1#防火区', 'Region.GG6LN.01',
  'region', 1,
  1, '9JOtAuaWMbazUl6a6cv3eM0dhQhiysPZLqyRfqLzIeRy68r5A2jiWnJRv17x4wRoIDde', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'GG6LN' LIMIT 1), '2#防火区', 'Region.GG6LN.02',
  'region', 2,
  1, 'iNW1gpupR73cczC9PhtbW33V3D104y3MgK5m/sfvvtnKCoqgZn43EA0BMhWmtUmCbL40', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'GG6LN' LIMIT 1), '3#防火区', 'Region.GG6LN.03',
  'region', 3,
  1, 'puwzAqrEzGMGFC1FcUmAQTow8hotsooEdxLiX/c96IMCF30qbe6PuIeHuZAE8pAHXZ5R', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'GG6LN' LIMIT 1), '4#防火区', 'Region.GG6LN.04',
  'region', 4,
  1, 'GogQR/gkc4jdvt/7LKnOmAF8UTjl3GyxO57VW6QTpa1jeJyo9d/0QxFqVdvYJtc8maXr', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'GG6LN' LIMIT 1), '5#防火区', 'Region.GG6LN.05',
  'region', 5,
  1, 'F3DxrwmLj63Vs+YKDG/DW5+PJ1akBKjHKPzAZmmt7TRjjMkxzmLEPLX7OaF/zPoo5gy7', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'GG6LN' LIMIT 1), '6#防火区', 'Region.GG6LN.06',
  'region', 6,
  1, 'S50HC0C3JQZ7WfCkXIYNw+Kn64e5+JDe3IIhQnc85h8jb5IW5cdfiaCvZCD1TDtojaGa', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'GG6LN' LIMIT 1), '7#防火区', 'Region.GG6LN.07',
  'region', 7,
  1, 'CbVANrT6DrXTfsOLfoB9iiNMVEePIyrdayx2dM0fu91FrBRDbKlka9H0RL46K9cs9leb', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'GG6LN' LIMIT 1), '8#防火区', 'Region.GG6LN.08',
  'region', 8,
  1, 'mbZ/WuRn7Y+HRPrTJMc4+quE4ZFxHP15wnboS2VU9esfjAQ7YPZXow1JavmnauUtH8k5', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-ba33a80994a24cbc81da3b686c28e835' LIMIT 1), '1#防火区', 'Region.GKYLB.GX01',
  'region', 1,
  1, 'qc4e307pc8OKqyKwV84NCOyz6ImVecmFP6ZuQAXZdMC1htUXHZ1XyaxZ4uiHb4sSD46a', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-ba33a80994a24cbc81da3b686c28e835' LIMIT 1), '2#防火区', 'Region.GKYLB.GX02',
  'region', 2,
  1, 'Zo+rYp1NbgyP+qi+LGDylVqZETadIvBC8+V93BXf9nP+rQ9tAgs+pqMuEG1smefdlDYF', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-ba33a80994a24cbc81da3b686c28e835' LIMIT 1), '3#防火区', 'Region.GKYLB.GX03',
  'region', 3,
  1, 'Uq+nI/P/HJ4B9QdWN/SQMkqa0rYdDvTwzDFz5gTWs+vL0w9D0OfloFgFLlgUao8vgm7y', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-ba33a80994a24cbc81da3b686c28e835' LIMIT 1), '4#防火区', 'Region.GKYLB.GX04',
  'region', 4,
  1, 'ZDagT8sKN40+bbPRb5s44sZ5bSRgFa4A/2ExVVSAGDK/B+vcATm+SAU9upP1Qf+4AGGA', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-ba33a80994a24cbc81da3b686c28e835' LIMIT 1), '5#防火区', 'Region.GKYLB.GX05',
  'region', 5,
  1, 'DDTxb1YNuCWDrcC/IAgYa0IQ+iXnD3Tna4hhxMX01NbXYQmiCKsM3W93XKzTM/C9Naqy', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-ba33a80994a24cbc81da3b686c28e835' LIMIT 1), '6#防火区', 'Region.GKYLB.GX06',
  'region', 6,
  1, 'wKy9Rp1sPaxlJnaE6w38Qt4omu7dJ18Pn5UNwWN7M6GqiWQzRzwas8eHMpOXyf5UJnJk', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-ba33a80994a24cbc81da3b686c28e835' LIMIT 1), '7#防火区', 'Region.GKYLB.GX07',
  'region', 7,
  1, 'MYA9Zq5aVE2myTtH0tETovLwi6h2tFbmV2F60H5bmsUEwy+XrAdDOETQHjZK6hZjmAX2', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-ba33a80994a24cbc81da3b686c28e835' LIMIT 1), '8#防火区', 'Region.GKYLB.GX08',
  'region', 8,
  1, 'Yn2IS0e3PIuwHKF2yESRC1xADy7WRC+icvXWeZpei2tlh8yiXAb8bX7xC4cKsgTnQbSX', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-b49ea247330542a5ba3edcfb8865d2b0' LIMIT 1), '4#防火区', 'Region.GKYLN.GX04',
  'region', 4,
  1, '1pgHRTeAluMUy8K5Pe1Q92wSt4neBOgQg3PAQPPLW8HTc28afB+u/sgGlMCvz6DsF7Ia', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-b49ea247330542a5ba3edcfb8865d2b0' LIMIT 1), '5#防火区', 'Region.GKYLN.GX05',
  'region', 5,
  1, 'ms3hNxm3zbJtDfyYkjgavADqhojUikbIsw2fTnKKH4ZfHefvWNjHMJii7m9QgtqEZVBL', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-b49ea247330542a5ba3edcfb8865d2b0' LIMIT 1), '6#防火区', 'Region.GKYLN.GX06',
  'region', 6,
  1, 'iwfY8jMT3d9E8f9gCblBupGfR91x9AvrXmCXYNAiSaG8uHXXpBmtE68IAcThjNonl1lQ', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-b49ea247330542a5ba3edcfb8865d2b0' LIMIT 1), '7#防火区', 'Region.GKYLN.GX07',
  'region', 7,
  1, 'YCoS7zkh9iCyEIMOiIVMUeAX7NoyntPbf61LLYSMqAhZNnZ+5/LDhre9eqLoQB2F6Ess', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-b49ea247330542a5ba3edcfb8865d2b0' LIMIT 1), '8#防火区', 'Region.GKYLN.GX08',
  'region', 8,
  1, 'K3gAmXtfW3FL/zl4tz/iFmFLNBGqSkAVznRzlTA7/RkOJnIw4X/OSxEiRej/4BQBz8pK', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-a860b6fc92ae4f8b9cd8e67345700a5e' LIMIT 1), '1#防火区', 'Region.GXDD.GX01',
  'region', 1,
  1, 'Gfkc9WYPAfPbszbNhkONmuSC3DCv4ru5vlwFrqeTQpujlPMOkTKbrtgW6wCE8RrEQvax', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-a860b6fc92ae4f8b9cd8e67345700a5e' LIMIT 1), '2#防火区', 'Region.GXDD.GX02',
  'region', 2,
  1, 'DxPHDb1MmCyIcoSkCtyYCIkMcN2FBnGAfeBkjzMLze1bRG4j0aEKWIweJWa50MFgxABl', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-a860b6fc92ae4f8b9cd8e67345700a5e' LIMIT 1), '3#防火区', 'Region.GXDD.GX03',
  'region', 3,
  1, 'kXqAYVKjW2gsIwRnzL+Oi1A8ObwsTs6yntiPINf1+0b1AXRk0xPZi12wkyBZ+pXHikSB', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-a860b6fc92ae4f8b9cd8e67345700a5e' LIMIT 1), '4#防火区', 'Region.GXDD.GX04',
  'region', 4,
  1, 'XMi7kCCXWFLZHCsilhMk63gAaaRZY7WkbjiQLUdgFL6NL+p9CBoFpWAIAo3mrJbavfIc', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-a860b6fc92ae4f8b9cd8e67345700a5e' LIMIT 1), '5#防火区', 'Region.GXDD.GX05',
  'region', 5,
  1, 'Ve8zyzO6G+sRCp/axaDUWXkSGONTWzWR8Emp74VWQSkjPN9b+fw/haTeWjasNNTNk3F6', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-a860b6fc92ae4f8b9cd8e67345700a5e' LIMIT 1), '6#防火区', 'Region.GXDD.GX06',
  'region', 6,
  1, 'azh6K5xHwDbTfw3ca0liTGbw5Ju6+R9CMlWsll4W7uqS9LiEm580m0Vn+eBP2KwP5kJC', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-a860b6fc92ae4f8b9cd8e67345700a5e' LIMIT 1), '7#防火区', 'Region.GXDD.GX07',
  'region', 7,
  1, 'dsrYxnXuMaEtqln4IWtBGVfOyEojPUZg+WNFrZy06x54NzkjiN3RBHlhlFoBAFe1g1Se', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-a860b6fc92ae4f8b9cd8e67345700a5e' LIMIT 1), '8#防火区', 'Region.GXDD.GX08',
  'region', 8,
  1, 'HAau26mnTPnAlFVFuf3JP2k4cR5W3bz7eJel1xeDOpagLPSVvVmz0N+ERTSouMU5JCGK', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-a860b6fc92ae4f8b9cd8e67345700a5e' LIMIT 1), '9#防火区', 'Region.GXDD.GX09',
  'region', 9,
  1, 'XMQTc5DOPMhEg7jiqGL7BVvjx72/UzNbCSJn2fyhSLpb2qCIO+p9NnmJN2TuEnQkh8iI', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-a860b6fc92ae4f8b9cd8e67345700a5e' LIMIT 1), '10#防火区', 'Region.GXDD.GX10',
  'region', 10,
  1, 'ulwlrqngHemzrvoQ5vvBDzob7/MjwCPQN6e43dSzdzACAsPFfTNY8vSb1bXevn/rtheVlA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-a860b6fc92ae4f8b9cd8e67345700a5e' LIMIT 1), '11#防火区', 'Region.GXDD.GX11',
  'region', 11,
  1, 'e0nwu7YSzw+pokfCfT0qAJA3MBpir3DLWq19vsfvDtxLJ0U4GEAtl5U56OWWOr+f7gDIIA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-a860b6fc92ae4f8b9cd8e67345700a5e' LIMIT 1), '12#防火区', 'Region.GXDD.GX12',
  'region', 12,
  1, '7O3q5Ov6pfeGJzynBqY1dCKuID0tJldnnIC/qXVPrco5XTCO2mXAiJjPcKAOfFtSVc9a3Q==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-a860b6fc92ae4f8b9cd8e67345700a5e' LIMIT 1), '13#防火区', 'Region.GXDD.GX13',
  'region', 13,
  1, '4FMjgq9u/PPj9fKSxzyhXORhfV0SmqG+eACcmAdUAWsmfqa/tnoA3tW9mbrCRBVsbUztlA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-a860b6fc92ae4f8b9cd8e67345700a5e' LIMIT 1), '14#防火区', 'Region.GXDD.GX14',
  'region', 14,
  1, 'a5ig1+3FqVxJtM4qt5lU+OsauSRHz2VJXUqMhi9fFjfyrs+brhJzbnk/MoHdKE0upDv25w==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-a860b6fc92ae4f8b9cd8e67345700a5e' LIMIT 1), '15#防火区', 'Region.GXDD.GX15',
  'region', 15,
  1, 'ypThI+2Tl9y6MHCDBGIzDbrFynaKVW5Ej4JDoBRNjqntUr9LAmdkjKUeyfJHWi1KmsO+nA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-a860b6fc92ae4f8b9cd8e67345700a5e' LIMIT 1), '16#防火区', 'Region.GXDD.GX16',
  'region', 16,
  1, 'S4NfjXAVW5LPqz8ToXGDtmngpMQZducitmUy1zhiYKWmW2pbaYTc6a9a3QyOdZDvGzhGAw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-a860b6fc92ae4f8b9cd8e67345700a5e' LIMIT 1), '17#防火区', 'Region.GXDD.GX17',
  'region', 17,
  1, 'clWlmB+GPbqdCFqdmtdITov2DrNUx7Vnta5+5Eb0oE4PvpaETs2EqCWDSkqEDxpt9zknhg==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-a860b6fc92ae4f8b9cd8e67345700a5e' LIMIT 1), '18#防火区', 'Region.GXDD.GX18',
  'region', 18,
  1, 'muTUHAHYxBgvgcYy4W2zPcH1IjUApexuKbNps63C8Q21DjpzFYKF8hSGxtKPkUZ4AGJ1CA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-a860b6fc92ae4f8b9cd8e67345700a5e' LIMIT 1), '19#防火区', 'Region.GXDD.GX19',
  'region', 19,
  1, 'd4RCprhQo5uX9AEorbSasB7LkQQ5s7dyGUOSDm0JxUbr1yHgYNWiVVJSyQLMNCaoEj3GCQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-8bf1e9121b6f41c89444705107a15d40' LIMIT 1), '1#防火区', 'Region.HSDJ.01',
  'region', 1,
  1, 'jGnI/EhAjc1dv7C1DbW2s9fYpsy/D6F5eTgAbIysc5TZNToHkrFwHXLRhDlmcIiaKlG5', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-8bf1e9121b6f41c89444705107a15d40' LIMIT 1), '2#防火区', 'Region.HSDJ.02',
  'region', 2,
  1, 'k/AMKJaiwt+YGQfJWT4ywEruESdXSUgq3YSszICkk1WgFf9ipIYg7L8ZWhMdiNKPtq29', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-8bf1e9121b6f41c89444705107a15d40' LIMIT 1), '3#防火区', 'Region.HSDJ.03',
  'region', 3,
  1, 'vPB5feeIJpvm2ofO2YFNGGNPYItQa+hGrqtY2mExBDiqbXIUPh2aQ64Un7gtUybkL9rZ', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-8bf1e9121b6f41c89444705107a15d40' LIMIT 1), '4#防火区', 'Region.HSDJ.04',
  'region', 4,
  1, 'oJp9qi26zqHPxUEsRKafLoySWOpKJtHt0SbF1gec0EGoCr4N1eOMBdEpZkmCVUv3KKgw', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-8bf1e9121b6f41c89444705107a15d40' LIMIT 1), '5#防火区', 'Region.HSDJ.05',
  'region', 5,
  1, 'lnaGfW3TLT8NaRlJH/9Srvy5Fl/jDB6pLYlbBi6YkkKrYpfcsytsxKx9WJeCj8MV6Pn2', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-8bf1e9121b6f41c89444705107a15d40' LIMIT 1), '6#防火区', 'Region.HSDJ.06',
  'region', 6,
  1, '0xWITh6IDREwA4r+qQG654RhmQwLxUoBTjcdzgAyu08W8n4aJHFGJ7wOsBV7Zhn0WeFS', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-8bf1e9121b6f41c89444705107a15d40' LIMIT 1), '7#防火区', 'Region.HSDJ.07',
  'region', 7,
  1, 'uSSfyowBkIZnAZzNekcfry9L1RntK641JcbT6e7p4lIdOlweBaZAWimHwRVRja4aABC4', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-8bf1e9121b6f41c89444705107a15d40' LIMIT 1), '8#防火区', 'Region.HSDJ.08',
  'region', 8,
  1, 'V0apNwsmNt2dmWn29vtnJnvpN3tiqUtaX90hjcTulswZxEfUJk0yyTNdMZ4O7bE0pP3x', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-8bf1e9121b6f41c89444705107a15d40' LIMIT 1), '9#防火区', 'Region.HSDJ.09',
  'region', 9,
  1, 'WLoFkzu8MB8FaO9w1SUQaU3P+McpTKBm7hYlanjHY/w4cE52eT9bw+wdP4cOx44oI01l', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-8bf1e9121b6f41c89444705107a15d40' LIMIT 1), '10#防火区', 'Region.HSDJ.10',
  'region', 10,
  1, 'SlUtLxmgQUTfBlavnT3y25+sDGuKBN9on075fjPP5KEwHklx+CvERm8Jpu1PMhXJYbhVdQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-8bf1e9121b6f41c89444705107a15d40' LIMIT 1), '11#防火区', 'Region.HSDJ.11',
  'region', 11,
  1, 'Q2H+m/nowI8tJAI1y3U6sSMZCfPClt7b01+NR0jHBpjnyKiTu/QkzmuAe1ANQpgs7LZ0nA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-8bf1e9121b6f41c89444705107a15d40' LIMIT 1), '12#防火区', 'Region.HSDJ.12',
  'region', 12,
  1, 'MEjT2jGAfCq2xVdOs+z4BgCbP1BdH3rRh05hBe4iprwIn9g90q+lFAgzBwvMaeObgRz2AA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-8bf1e9121b6f41c89444705107a15d40' LIMIT 1), '13#防火区', 'Region.HSDJ.13',
  'region', 13,
  1, 'a8FSBSpHn2CYBdeAuRVz5DdctAPYEBt1Z4dvXQRE5RmHleyEgx3ccOwZQc+UwK9O3tnRng==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-8bf1e9121b6f41c89444705107a15d40' LIMIT 1), '14#防火区', 'Region.HSDJ.14',
  'region', 14,
  1, 'fFOJca/Kmw0L2ELOcIf1EAlS1oTz6b9O0Pitrvwagu6hEeikkPCwdACmMJFgmgXi4nehkQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-5e29c5bfd3af4a368bdd5245763971ab' LIMIT 1), '1#防火区', 'Region.SD3L.SS01',
  'region', 1,
  1, 'yR4DrOgK14f8f9yozVYrzOt6HktGDE0Z//vCBQvDZK+UYK4+hjB0f/lLDqFTVzf1+4RO', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-5e29c5bfd3af4a368bdd5245763971ab' LIMIT 1), '2#防火区', 'Region.SD3L.SS02',
  'region', 2,
  1, 'YkkTsjJ0QY6236hsvKjAOB4b3udR1cXCgUjsvEJGBIOa8HmABnRPrLKzpLsTi/2LI9H2', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-5e29c5bfd3af4a368bdd5245763971ab' LIMIT 1), '3#防火区', 'Region.SD3L.SS03',
  'region', 3,
  1, 'xXJ2qnpln8Y6Y9TnESWpSUUTbiCYksJDumP6Yyzv6wj9ZWTttFuvApVe6tjj1YPkrrzc', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-5e29c5bfd3af4a368bdd5245763971ab' LIMIT 1), '4#防火区', 'Region.SD3L.SS04',
  'region', 4,
  1, 'u87OSwRZAn8JNnJMaxNCzMzpKhZZYtP+TXWCI0EipWZrtBvyF2mBhpcDP3g0NFdqLUm6', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-5e29c5bfd3af4a368bdd5245763971ab' LIMIT 1), '5#防火区', 'Region.SD3L.SS05',
  'region', 5,
  1, '4KQj2O1T5SmC0FebyAh8zXPvLr7wP3p0B8/OcziB07LLWp7Fk7buMtgLp1WXsx5+7L9D', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-5e29c5bfd3af4a368bdd5245763971ab' LIMIT 1), '7#防火区', 'Region.SD3L.SS07',
  'region', 7,
  1, 'w+NDuThE+19wCE54D0+4B7SQx1W68kcyg8kanDFYy4XR8B+ckLXL6F1zDUflzSLdKaxO', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-5e29c5bfd3af4a368bdd5245763971ab' LIMIT 1), '8#防火区', 'Region.SD3L.SS08',
  'region', 8,
  1, 'g/QMltuAUq0wbGGX8r6doDrRBWB21tLtVvSTQR4Xs0hNhLwMYk1iXrKg1tE/PI4A3Big', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-5e29c5bfd3af4a368bdd5245763971ab' LIMIT 1), '9#防火区', 'Region.SD3L.SS09',
  'region', 9,
  1, 'NVLEnNyv6bKMPHRnYq0JAl/YZuIKDeIOMqZnD0KmrjiHI+GElgu8c+S9AStDSmyJhXI/', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-5e29c5bfd3af4a368bdd5245763971ab' LIMIT 1), '10#防火区', 'Region.SD3L.SS10',
  'region', 10,
  1, '6MsGCk+X1h1G86Ar6r/bmFrYO3O+VKTbN6CsiruODz5ciCcl87CKQzkZSDtF8HNNQRJaXg==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-5e29c5bfd3af4a368bdd5245763971ab' LIMIT 1), '11#防火区', 'Region.SD3L.SS11',
  'region', 11,
  1, 'PlyKoaOK/vU0B60/f6oobSJftwoiRuO5X4tx1zZYoT6JHMIhz3ubAfPlPRpkvIzw120dPg==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-5e29c5bfd3af4a368bdd5245763971ab' LIMIT 1), '12#防火区', 'Region.SD3L.SS12',
  'region', 12,
  1, '2TPQNQ+tIMREMdYUc+V5CyRPiIBxJhuGTDmSvcTpaUlakNlLL40YYU4C1BkQ78Ig9frNdw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-5e29c5bfd3af4a368bdd5245763971ab' LIMIT 1), '13#防火区', 'Region.SD3L.SS13',
  'region', 13,
  1, '+8j/RBbqOSAuqTeCPipxMiuGlvCLoiV6R/MI8RVpXCjY/3eDkSjEqZRYk3Qt2lnangV6Ug==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-5e29c5bfd3af4a368bdd5245763971ab' LIMIT 1), '14#防火区', 'Region.SD3L.SS14',
  'region', 14,
  1, 'JHnDCl8EE9Buzd0gRTUDttDrTpthkLjKvOo4Dk1q1dWA4pUbtHqYcM6KqYDnR8QHqdnVDA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-5e29c5bfd3af4a368bdd5245763971ab' LIMIT 1), '15#防火区', 'Region.SD3L.SS15',
  'region', 15,
  1, '4CtUaEGLGAaIZIZXu/ih0wer0rT22C1x7rPD70abnmyzYFnxknjbqWolq9am83PVVNe4Ig==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-5e29c5bfd3af4a368bdd5245763971ab' LIMIT 1), '16#防火区', 'Region.SD3L.SS16',
  'region', 16,
  1, 'QOgEwLJ93X8n1VesLtw3ud5E5SfHF33ghxMvkFDi1xXPEw1TbppJtVr638XB6FgWvmmfnw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-5e29c5bfd3af4a368bdd5245763971ab' LIMIT 1), '17#防火区', 'Region.SD3L.SS17',
  'region', 17,
  1, 'Ay3lkqO8bkCt7wxRYF8Cd6PR2fEI8nyNl5ElzE6idY1Fqn4yoo38E6gIWh15lHjcNMaaXw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-5e29c5bfd3af4a368bdd5245763971ab' LIMIT 1), '18#防火区', 'Region.SD3L.SS18',
  'region', 18,
  1, '+NPsXdgZDDZMOw1KStmr5nI4b93ZyVLTPzYsGVAhkIFFlTnxguKvBvZCrBexq5O0jBqNRA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-5e29c5bfd3af4a368bdd5245763971ab' LIMIT 1), '19#防火区', 'Region.SD3L.SS19',
  'region', 19,
  1, 'VfQzlLtDg5wYhLob65chKz86ouG3yhUacyGPcR+WWPeMJ5ybVM2g0GXaZwXxcBQolkttLg==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-5e29c5bfd3af4a368bdd5245763971ab' LIMIT 1), '20#防火区', 'Region.SD3L.SS20',
  'region', 20,
  1, 'kyiaX0GLL53u/F0SkkOEsfd70mpRUEAWhPbJDboXv3MxBtrvm9kZpSB7MhhzgzDrm/W/WQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-5e29c5bfd3af4a368bdd5245763971ab' LIMIT 1), '21#防火区', 'Region.SD3L.SS21',
  'region', 21,
  1, 'B6O81uPO5IxVHbBdPWdKVxMNbzKSw6hv1YE7z/ZMzdiJgV1fmVtl/A1rlnBW12q8F9EV5g==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-5e29c5bfd3af4a368bdd5245763971ab' LIMIT 1), '22#防火区', 'Region.SD3L.SS22',
  'region', 22,
  1, 'TD6HGfTgtQfNWAKq+f22tXfQ/pNIaMbM+UJJtmTLup+LjpMu5K6Gn6uw0SxR7Y80ym38uA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-5e29c5bfd3af4a368bdd5245763971ab' LIMIT 1), '23#防火区', 'Region.SD3L.SS23',
  'region', 23,
  1, 'dgCVpYOb5oVxZvTu8G8Y0C9BkXOEIFadV2AIbgy+UwHHHts4VvvVLpkKZJKcZec8gCsZ+w==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-4ddee51e92a743148bda50c2df7e2df5' LIMIT 1), '1#防火区', 'Region.SD5L.SW01',
  'region', 1,
  1, 'E/uYD2dGjr6FsUEgakB8P4LUzJq5aSYh/7VEKBmETR9s5GIl3KvcZ2biqoz0grQwW+ze', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-4ddee51e92a743148bda50c2df7e2df5' LIMIT 1), '2#防火区', 'Region.SD5L.SW02',
  'region', 2,
  1, '2smXtYRsMW0q668WSJMPg9Ivsfpwxnx7DY7R1Eon3OIRzgFPJNXBN/bekNnoj0rrq5Tn', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-4ddee51e92a743148bda50c2df7e2df5' LIMIT 1), '3#防火区', 'Region.SD5L.SW03',
  'region', 3,
  1, 'Ck9hwGEOpcG0LT/WXD9ARWCBuNrMV96InflnMMVcWJK5jtd4+V8ZTK4BHOOispEcK/ST', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-4ddee51e92a743148bda50c2df7e2df5' LIMIT 1), '4#防火区', 'Region.SD5L.SW04',
  'region', 4,
  1, 'QU/FHAlrNGnjtwBkEk5XPcy9TExtq/bA7Bx9JV+ePrvgM1UmZeaNEGSd+2u+pzqh7S8s', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-4ddee51e92a743148bda50c2df7e2df5' LIMIT 1), '5#防火区', 'Region.SD5L.SW05',
  'region', 5,
  1, 'mVUg76LJRqlEA2bqCSZtzT83t6UwXAx5Z7JBdv6YbnmkHVgAWYgzjEnXXLC6ISu8aYsi', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-4ddee51e92a743148bda50c2df7e2df5' LIMIT 1), '6#防火区', 'Region.SD5L.SW06',
  'region', 6,
  1, 'n6ZgnBBq0N/QtKAz2rZMFdtnGB5OT6yQDu5hsJhskPOF3VdE8cCUfNYlqkQ0/mW9/sbO', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-4ddee51e92a743148bda50c2df7e2df5' LIMIT 1), '7#防火区', 'Region.SD5L.SW07',
  'region', 7,
  1, '3L+C86bRT4y6cJrqijuEauQYSayukUjUGjlvIpE5uhBlpSzSUvMf+82sDhAbh8SxljKm', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'TYL' LIMIT 1), '1#防火区', 'Region.TYL.01',
  'region', 1,
  1, 'OwP33o5Jz5Oc9yV4BtcXzt6q2kmrb9t4wcSDMu8PsEoSxdCZRKkSDGGKv8wC/ZxkEInp', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'TYL' LIMIT 1), '2#防火区', 'Region.TYL.02',
  'region', 2,
  1, 'Rcpg52XZB2+uiIM7Bc4j+LbbvKpDrbARsjxCZRaXujDeZ5JiZDBWvwOp4HBhYoLXDBXL', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'TYL' LIMIT 1), '3#防火区', 'Region.TYL.03',
  'region', 3,
  1, 'UQQvhRPaPYQAw/YjT7rHGRhyNWtDwpe2+/xdjIgi/PHywWnGg08qqgCxm8REj9ntRrxg', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'TYL' LIMIT 1), '4#防火区', 'Region.TYL.04',
  'region', 4,
  1, '+f1MMnS9GXvT8QDtmGDApIghLHlFz7ZjmKJOfFCH94gwGc/jSf4rJfjcHWjk/iveGNmh', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'TYL' LIMIT 1), '5#防火区', 'Region.TYL.05',
  'region', 5,
  1, 'jeWNwaFj8HeOeAVngSLQ2MzGkkyleaMXFB2LJ43MHDUr0TR8ZZRcAItpA7wfXs2xjE7s', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.HSDJ.08' LIMIT 1), '自然进风口1', '000B19E14B04E0A116EEF596A7AB54A5',
  'region', 1002,
  1, 'K3rVFq5aP0s5TrhyPjj4fvfXgH67J06G2sH7598aSsy2IQ+1lniUoJ12UnUNKCQsSMKuJdtTccs=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-1b468a9c7b8e44d7ad664dc096fff9e5' LIMIT 1), '集水坑5', '002564FD448CDFC0A51870A1AACDCF73',
  'region', 8,
  1, 'LqyAeIAOkTaiwGrTF8qf0U1Rov63zu6y5mfY1tgMWuXefTGcE8LLiNRfg3BwFup5u4Q=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD5L.SW01' LIMIT 1), '集水坑2', '01BB8E8241F5D3816390CEB5CF595B92',
  'region', 3,
  1, 'b9/nBEdcFWrWSwt5J2QpKnAcwqL6Ui+hs3tSu1BDj3YkXAq3tUadzLp2ySrqIlBlV7w=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.18' LIMIT 1), '吊装口1', '02946DDA401A2DA8B4A7AF924413BD5F',
  'region', 1003,
  1, 'ieb3oLXUkHV/L/D/BM07rUk3XcqtN2cGmoxj/RyIu2efjBoB60/E3ogRkxlXsOfepnI=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX10' LIMIT 1), '集水坑1', '02A1B29C42660083BA6D0CA940052247',
  'region', 1003,
  1, 'LLG8reKIQSZ4qTrk/CDW5Od3bjcLNPK49PEW9dTsAEnMNRkoBB5KrUv7XST5Toe+tlk=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX09' LIMIT 1), '自然进风口2', '02B4653A4CBA0C7832E5198C57983F46',
  'region', 8,
  1, 'zPfjHTwaeriVvP9rrlITmt043iQrGu1nE2fgMjV8H66gKuhAPivIzC9OV06Vblc3g5koJRQ4QHc=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX13' LIMIT 1), '自然进风口2', '02F7BF0148DF0A5B76D9FB9BBA11D0D5',
  'region', 4,
  1, '+Mata9bdcKiY1u0Rx5XpdjgaqCJ0QUWG+KJWNBkGLYU9BxcNjTrtoIxCMhvN6jLa5rXDyvFcMHU=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.20' LIMIT 1), '集水坑2', '0303275D434F784B1307FFA210472E38',
  'region', 4,
  1, 'R6BWgVncOFt+olg7SHd92xNEAGj7VGcmdgSBYZk99QZWp7AlBwb/d2/m9arrsqjjh78=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLN.GX05' LIMIT 1), '自然进风口1', '03B214CE4C7725E6194BDABF1BEF3BD6',
  'region', 3,
  1, 'wjEDySXD9/Yxcm1UpJDVjHBqNDS10f57Kn8GRV9bdWVPi6xYbdahi2xC3AjAGoWakY9jpw1Q9Ws=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX13' LIMIT 1), '引出门1', '03E533F241AC59ED4C7A79A18A58DEE5',
  'region', 1003,
  1, 'BxVDjXrkNNdVvFOz44H1LBnhLCy7688ekKMCiiYvrxi39hV10ay4uYIpepCnwElg1BY=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLN.GX05' LIMIT 1), '引出门1', '050A3BD84BCE702F1DEC79820518C40F',
  'region', 1002,
  1, '5s/eeHj1OQsgBnRhiYr/TTsFx4kXdOfsLSbmuUVAQBCExFOB0iNK40Pi5xR/4Lh1kvs=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLB.GX05' LIMIT 1), '吊装口1', '055297914FEAE06FA5023B8ECC233DC7',
  'region', 1004,
  1, 'qv72OqadCVjbcINB+umtMIr2D2qY87897Nw2bnXvba+EFY8RS5qwQkH7S78ApZLDNFg=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX10' LIMIT 1), '集水坑6', '06210CEF4F525F71BB227E9CF43C901E',
  'region', 10,
  1, 'uNGnsCy7BJKQHmuXBEUW3I0rsc50k/FbBn/+TWs8fShPwUD4mp+Th1CRzuO/gDEllkA=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX09' LIMIT 1), '机械排风口1', '063978E84ACE15432E0B40B7F76167F0',
  'region', 1005,
  1, 'vydOAWQjdf3LvB7jgq2lMFez90Yqp2dqfgifY6zrcx+F9ZtAyIZMCR4vJKonBo15iaFXDR+arkU=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.17' LIMIT 1), '集水坑3', '07F423A747CDE879548720968625237A',
  'region', 4,
  1, 'rpbJhl4qv1sadZ3ktOMeW7K8hdzvABGPOvcBPTDD5BSSEHOCYs4RZbpL37nr4AQpXc4=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS07' LIMIT 1), '集水坑2', '082766AC48EDBDC6BFD330B69F0A65C6',
  'region', 1004,
  1, 'ZDEJR7QlIdCmetjyObAZiqlOZH2xrYHcFp8Iq2GP1DvS6VemET73xNKosMaw5Y4uTz0=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX03' LIMIT 1), '机械排风口1', '084733E54A859D9E9059479F2D9091B0',
  'region', 3,
  1, 'MICrYweym+B3GpnaiAaJjLBKLOI1Ij34tg9LGzv/rBZnzYQ9bUslyRL+b9hDFBir+ZoQz6QBrJM=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS05' LIMIT 1), '集水坑1', '08FD0242481E870343DA1989DD308711',
  'region', 1005,
  1, 'IlZwn0EVgVGd0/OtKH5SdN4QGkh3FjAdrpixsZ98EHfDVdVdJ6hMyO1uI+J8xMnc3xk=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS09' LIMIT 1), '集水坑1', '0953FEB54520A8348B4D5B8BAFA0EFED',
  'region', 1004,
  1, 'VOmvDfow09VsSpfc+kK0HXwC6g6xlRvJMN/T3cn5OqmtPpr9QY3MQjYm6vdq8p/kW4U=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS06' LIMIT 1), '交叉口1', '0A8DBCCA4426E55DD0D0828AD5AE8917',
  'region', 3,
  1, 'qSYe2KO4oWAE6F0kjVExK8b8lnBwMUHESDFEUrr0W4y0vsP2NP0Cnfgxb5ZLuRujKBU=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS08' LIMIT 1), '集水坑6', '0AA78C154CE117B815BFB2912442E007',
  'region', 9,
  1, 'PPMX0fONj7oxAklGq2t/ETgFpPoVgkE5f8N5oT9ZYY2VelNjzcWcuazqWUB6MU7kqkA=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD5L.SW04' LIMIT 1), '自然进风口1', '0AB1C3244E0CE4383883ED9E8EC2C31B',
  'region', 3,
  1, '2JLDvkDC2Re7u6SuKIlfeZ+fi0Eo0aNLSEkxWPohWETAbP9ZviVEFtvQJGouAZCRhjhkxin2nWk=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS05' LIMIT 1), '集水坑4', '0B4C02BA47DEDED4F750848F66E3343D',
  'region', 7,
  1, 'sm+YFDcodrltwOdVmGIp+xgFkdYsFj7IeawHhVn6fotI6r6Sf8KhMeMKysPVutl6CPg=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX05' LIMIT 1), '自然进风口2', '0B77F0B9452CF67848FF5B8B77892C2D',
  'region', 1003,
  1, '2ABfZyFgbE4EX2q6f+C7hMOUOwekeGX+MI5NaIPxHIfZZ4if3CxsZzgmwDxVfSPP2z2yFjCzzFU=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.22' LIMIT 1), '集水坑9', '0CBB3EC841089F67E7E9B090D415C1B1',
  'region', 10,
  1, 'LdPhSYIsSDMC8pbRHcg56a4lHuEGObC+QWWfhFI06+1kZtliIbBSTD+9Vmtyx/ROZ2A=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX09' LIMIT 1), '集水坑1', '0D696EE64881A3240E0851A5763C84F8',
  'region', 4,
  1, '3BlZxm+bqGs1rEraJ6GjNmTcvBCyQXtGW2+vLFa2wa8yR1Td9eNtz8fzrGFLWNpoqyA=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS07' LIMIT 1), '集水坑3', '0DFC0C824D73BF731C7F6AB1A10F0D73',
  'region', 1005,
  1, 'eHmDjpKfm9QvVOumRtmetG7OGOdwPh27HqTy7E3XYv70EqSBusSNxEXBv3uDnB22jrA=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.HSDJ.02' LIMIT 1), '出入口1', '0E240FA2410ED64BB74ED58C7117C8F8',
  'region', 1003,
  1, 'Ama+4XhDTMBGPuNwwtBDzhWkry8P/oBtXP2nQ1iL54EXpBpDEjtLP5UYWMHEnz419oA=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS07' LIMIT 1), '吊装口1', '0E30EDC34D4855CFF6A9A68C41A00DDF',
  'region', 7,
  1, '2gQfYdEWKcDez+qU/g5AdD73zXP1kJ9YL6dUJVdRGV8H/imZkPP4rDFfN3dT3+8VhWE=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX17' LIMIT 1), '自然进风口2', '0E360B67449709F21F6AC383E5D8BCBF',
  'region', 7,
  1, 'xYSDBtaItRGvqFr9cPx8QJG3nNFRJGI5SjyQDud/hEdqJh2q9BVTNkzs2nQ8zU0c2SW2BQ/fINQ=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS11' LIMIT 1), '集水坑1', '0F59AAA74A6C9ACF9CFBB0A7EA9972F8',
  'region', 1003,
  1, '8ElJLAwwG+WCqnFf+MdUSV1djY0+bpUkrOIEH4nu9KtVD0fStHtJ+k1RTchoM1mGACg=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.16' LIMIT 1), '集水坑1', '0F5C8A1A4FC5D2137381A38219096AED',
  'region', 1004,
  1, 'OvAAiH7lTw+XtNmpkj6IID9ArpAhyrNR7uxhQopG2BeeiT6rh8Q6XkcQKFXibmA0nf4=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.14' LIMIT 1), '集水坑3', '0FB29E6B4CAEEDF0A5744EA308ED68BB',
  'region', 4,
  1, 'CE3ta8kthzph/XP/kECXuxxPiWtW9K/rlYgpijEqr6XFIjlJcXe5CA6WwWdkOQtclM0=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS08' LIMIT 1), '自然进风口1', '107248ED4845D82E51745EB7CEAA4723',
  'region', 4,
  1, '+raaKfol2JPc0GVuDLExncZe8CTT0hcV0uJ7XuIbRvaqkJoN4gzqatpcqJ4/W+Sa184cCIAjukY=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLB.GX01' LIMIT 1), '人员出入口1', '10EBB19D42747369E95C9580A18B83A3',
  'region', 1003,
  1, 'OJWC21jXS1GYzkYLNA+FoC6sGiRu6RtAVMsMuWbagGVP3T1fpz13xzA1M0LkdPQ2sYYpwa4dkIc=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS14' LIMIT 1), '自然进风口1', '10EC8AB84EDA530948A1018F9AC6ED41',
  'region', 5,
  1, 'zUtqtlY4tQnt7aC08VaF6SC29l5RehOB2jR0/c7vfWo5B32aCgTwyuxrEgZJDAZCgSYg40I8rMs=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX07' LIMIT 1), '机械排风口2', '111AB4C54B86E249A8F251B84AED0B9F',
  'region', 7,
  1, 'jNep6aXEVk57zLweR1Va5RlhLiiC/zCdL3vJWi/Ch7OVI6a1gwsHcQ+SDobIItaOZWYAvjZYVAI=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS03' LIMIT 1), '自然进风口1', '112053C44DA669146C9DB7AFF900617C',
  'region', 5,
  1, 'AnLKriSvu1/OnQdnEMkJgfDVZ2dPM7RQV3I+c5WVrRZVO2XPTPH0FeJZ0DapnapRzuDamdxLcQU=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX17' LIMIT 1), '引出门1', '1262135943F11546F5FCCF9325E2E395',
  'region', 1002,
  1, 'BDlUGEkd69w6tPHI4HBtU5SR9wDZ0St3JS9R6zEokLhwDX4BWsTYN1CEuuGerSI+v+M=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX19' LIMIT 1), '集水坑2', '12A761A04E90C91BC79D43B7FA69BD3B',
  'region', 7,
  1, '4HVm6TT4AQ3atlBGgYKf0dwYlqFFs7bSyrec/5bx1B9g94C95Qy1jgkY7N+hP5eVlfQ=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX02' LIMIT 1), '集水坑1', '12AD2C2548FEE92D0A6A04BA721F6A12',
  'region', 5,
  1, 'iQrKFUVotSL7THS9OVTJiq8frSL4K0x4rxUAdsc9pm/vTSPhQGQQ47+MLLJ3FmST9BY=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.04' LIMIT 1), '集水坑2', '12D222204C5C352DEB3C809EA7360F04',
  'region', 1004,
  1, '0HeoOixHAp4OFBfod8Hp8REL5tz/vufx4X+dMEw6E5e9xEt8wmYLIeRqfp0/vavVOOY=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.12' LIMIT 1), '吊装口1', '131CB47C474F5F6571B47BA466211E08',
  'region', 1003,
  1, 'i1OOmbhPuJpn8VVcyH0EXqrFXXP8pXIPSFscjPQ810qcimTh6jT8jzR7wsLIusNAJPQ=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.14' LIMIT 1), '吊装口1', '13ED3E0E4EFF808A4999BC9D209DE396',
  'region', 1003,
  1, 'T3EKQ6fa1+jiyzJvQLCV7elDbQACay3oTwfv8SizBn2SfN9qXKYrhWKJ2ORi7DoXsxo=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.HSDJ.08' LIMIT 1), '机械排风口1', '1402422A4E9E10C863DB3AB023FB7CE5',
  'region', 3,
  1, 'V/oGxHU7ePIvF6AlG6EelWRPKHTlUiUBmKAej1cKctmVG6Nd4g6lVvyzIVmIwfofit/4SwkwbJ8=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS15' LIMIT 1), '集水坑3', '14C2CF13486636698E544D9B7EAE1153',
  'region', 5,
  1, 'zadjbx4KXgCJUHmnc7/m4cHBINdJxMRn6oVC1aKvaWfh2K4P1etkuOGvxyQzm29ydmg=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.HSDJ.06' LIMIT 1), '引出门1', '14FA1E2348C6D53971290BA4911B62DC',
  'region', 1002,
  1, 'vlvSZzpv13UKWPPQDZzSH1AZqqxK1U7FDTZnrC8opL+8Qe1qzQRZlp9Lb/RBERAsDxk=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLN.GX08' LIMIT 1), '机械排风口1', '1501B2B54AF4D7EB45D1F58E6B23B7FC',
  'region', 1003,
  1, 'U7iB4dl8zcCZR5olANrciPd7VdvS36sOCnS+KGSNDA9ad6EwmmYhdGkjZv194oISlxjx3uIiK8U=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.13' LIMIT 1), '集水坑1', '1535F7B545363D41A611C2909D18AD2F',
  'region', 1004,
  1, 'H2Y1FPxMsinTKdQkEKrb/spnruIGy0B+1fEsa9p2/jWsNJoOA2u+ZYuPjDawqDdf1t4=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLN.GX06' LIMIT 1), '机械排风口2', '15795E9C4E53B781790DD0A373502428',
  'region', 5,
  1, '67KkxFT7qIdzHJJF2IZWVPPNt5hOGHK61SRPjg3088CcuJXoPiUha/w/+lHCbXwXkCTTLkQgY6k=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.10' LIMIT 1), '集水坑1', '1581F13841889A2392CB46ACF66CE1C7',
  'region', 1004,
  1, 'U0Am3CB1wrf1WH6FZonWNzH6OepKfmWuRDFOpEdouEtoernZLG19BYEcN3mnYEVXB/E=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.11' LIMIT 1), '集水坑1', '163915044F45A5474B36819D655383CA',
  'region', 1003,
  1, 'fqqcE9S/1xskVRMzmwVPsXsbe2HfYR7PBoxfrbdiKH6VNVeejHpaEdHgWrIydpWlykE=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS03' LIMIT 1), '集水坑2', '167150C34E4DBA450C3044B835752B08',
  'region', 1005,
  1, 'GdyCniMn0B+6FJ7htFdUqClofwT6pfL129IUDLLiSe24Ooo6aR9M/hwyro+BSK6lKvA=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS21' LIMIT 1), '自然进风口1', '1743C16B476786BD0B4064979B4FBB04',
  'region', 1004,
  1, 'HzNPABbDTwLsQklaPUTXh8x9b7LUlTxxdzTZANV3mFNuP/7ERuNl+GiLZDiyoadhDmZ4fXu7Awk=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX07' LIMIT 1), '自然进风口2', '17FEA37D44CCF5A11FDEB9B0C7C102A4',
  'region', 6,
  1, 'nMGYtSNhOefcMIMXw0+AuZit9Kb3bU7s7hqw+NOPh9q46DSpyi0fMq4lBHUZmoK2e5/q3WcYolE=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS03' LIMIT 1), '集水坑7', '18593E544D364DA1D0185BBC5CEF9F40',
  'region', 13,
  1, '3uj/KCGFxvvOYyn6cU1fipOudeNIhKhSdlVwwZ+VyA2aFzUBF8akRezuQSAA8qxzyDE=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.19' LIMIT 1), '集水坑2', '1938CF3C4639274C1AAC14848BCBB680',
  'region', 4,
  1, 'SfyVevR3XMlHZIABXnpJhvdJezF8ZuSU/xAB04vDEmybN80YJJ7Es8q7SXpBLeCGDQw=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS03' LIMIT 1), '机械排风口1', '1992E61641F6CAB48F7A329E7B7D52AB',
  'region', 9,
  1, 'i+LphZnYJSIFGcQ2N6UuehQOubemh/3NhA4mxqu3XXfov9yV/EBJnkvVEiCRzQxa+4h9QH9VLBs=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.HSDJ.04' LIMIT 1), '自然进风口1', '1A3ECB9E40875B392573ADB65CEFE2F7',
  'region', 1002,
  1, 'MZHp9BymvcAXIvz7dBA5m1QBUxsuQtGusPAwbYEqa4wZoB7NVox4X2MM24fsDlM35yseT6D17OA=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS12' LIMIT 1), '集水坑2', '1B327A4241B769211AB4078637673D2D',
  'region', 4,
  1, 'Tza6zak/XLzkfICFiy2NgkmyYOIsSra91A1x4H47IXdRrULYe0BoBhR+gCpzKGPsVkA=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.10' LIMIT 1), '集水坑3', '1BA9DF9C41B1D48B4FC320B4B26538F0',
  'region', 4,
  1, 'X/bVaICuSGqW4XmPNlgE4+D3YbdmuDPdHwI8W8omTgLVUipNG55GzKaYG6RdkgN98G4=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD5L.SW05' LIMIT 1), '分变电所1', '1BBAE5D14F5BAC80D8A69EB6FD74A4EF',
  'region', 1001,
  1, 'toahmyG3JLlkvjvR50gRfTY6/ypB384PJBBktp/kNlK+MTFl9yye/c5WD0IQGQmyKVSJqxA=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD5L.SW02' LIMIT 1), '引出门1', '1BBBBF8C4DCA987B30F0CCB2FA115725',
  'region', 5,
  1, 'e6+GQfCXPHnfnhdp/jaqZcI9quB+B+/TmsjKTYOnOF/ZgRZFnRAua1nRSM1xttJMDfQ=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS04' LIMIT 1), '引出门1', '1BFD119746E96E5119FA96A8BEE520CB',
  'region', 1002,
  1, '8uZG3kW/jc8PCkIU2N1H4EOqaHPsEuJgbrylNnxUnOuR2L6wBOOXXhy1BSMt7d7OB7E=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.10' LIMIT 1), '吊装口1', '1D66E55841BCDF7811C99187B0E478EB',
  'region', 1003,
  1, 'n+2eH/rJFzZIfqMuV6LL+gEtq9nVoGfw9Q6+JUcP02QGcKz4Xuhwm2dk4wZT97BJqPU=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS06' LIMIT 1), '自然进风口1', '1D6825A344C4FD6A212850853415B3EA',
  'region', 1003,
  1, 'sfTJkv/5yPOQ6WZppADBOdI3pEci2ecgLPmIIviQerxMwdnGTzwJkvfUw2kBgF6NT+JUa8mmKqM=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS05' LIMIT 1), '引出门1', '1D7601B04291569C2A5F6BAB13BF98A3',
  'region', 1003,
  1, 'YcjLMMxHnpgmBd6/TMjDgmMy8IzONbwp8NPtnlmvXXXZk5Ek7iR0e7NJA965WypqwWc=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS20' LIMIT 1), '机械排风口1', '1DBEB2A44C14423F385557ADD1DA8CFC',
  'region', 4,
  1, 'jQZTybNFqqOT8o1nbuEeAqCB7GqWmLlb+y9LOoPn2/eYMsxMYg3cIORh9LQ8NGXDthXhMWy5bqQ=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS05' LIMIT 1), '集水坑3', '1E5A7FA14BD338FE92CC1EB0E23A577A',
  'region', 6,
  1, '2Ge5hr3v7QSAFugMsiALtYX7m2nu0/M74WayWt5ASSyM2zH6ReCDuOJ8YRBpUOQnAUg=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS12' LIMIT 1), '机械通风口1', '1E7E870F41F16E889D0D8B88FA3C9E5F',
  'region', 6,
  1, 'kZe+2aU1WJYosR7Mjdd+XWX5dUPCleygzOFuDA9f7b+ej/uNHb+aP2zIpMkgXPV1lkiw326sYFc=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-1b468a9c7b8e44d7ad664dc096fff9e5' LIMIT 1), '引出门1', '1E7F05E24BC4996CFBA1F6AE8903B3ED',
  'region', 1004,
  1, 'zfdc3kWOIyN9wc4hyddJDOxrtRMjpvMGXN596Uj2zHXZWppOH+1BK6Gvj8ou0xBHQwI=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS19' LIMIT 1), '吊装口1', '1EC922464EB89361B9BE0E8C94955A64',
  'region', 4,
  1, 'c9oN6SfDWgsNUABuj7h6wxzE1rHvlqekBH1WpL0FN/UkeXObTGXFxanM6Af5cHImxSs=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.HSDJ.09' LIMIT 1), '引出门1', '1EF90603410D11300F6901AACCFC993D',
  'region', 1003,
  1, 'OIvAlW4/Aq6rq9y54k2TMbMf7dLST3gn0YRxKZxih6pgVxnEUuewk4HKwij9wj/NYRM=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-1b468a9c7b8e44d7ad664dc096fff9e5' LIMIT 1), '集水坑1', '202A903F49C66032D6D729AD61CA4818',
  'region', 4,
  1, 'OX6Lvn/uicPT9vq3UzSvCbQ+R+UXxtKGRmTj4tX+fUnlVVSrHVrlqkyXxh7uEFZk1tc=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS01' LIMIT 1), '端井1', '204670C64579F1DFCB0EAB857A4000C6',
  'region', 1003,
  1, 'PTdonVe1aPmdbU/hP/GpmAPJv+Y88WiYeqy1eNlFJfnP3EEHKBxUKGT1Bpp07ug=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS07' LIMIT 1), '集水坑4', '20B1EC01415D05615F614580134DB436',
  'region', 6,
  1, 'YRcKYUgLuRYbWidLRI+Sp3aNw+iku3hcuMzKz6gr0j9KaGzIiFVbB3puFdr1ppvPy9E=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD5L.SW06' LIMIT 1), '自然进风口2', '219061B947853693CE48F1A181C6CF88',
  'region', 4,
  1, 'OFWC85naMWyQKPpj9bsozehiZt9ZLmz9nfHQLYF4ma6yFT9GzicRZvbr2NIM4zEN6i3QAdbe15Y=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD5L.SW01' LIMIT 1), '自然进风口2', '225914054A21A3EDC49AB99B83B52515',
  'region', 9,
  1, 'tgnJ1mcNZVJvWBhWrek2t4yEqO7ssYtoIPz+/5mFraL/sT0z7yCGkOReFPl6nCgCnGdd+MYPcWM=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX09' LIMIT 1), '引出门1', '22F1E8C94F87EC42054A24A1154C379C',
  'region', 1003,
  1, '/DWEdd9fXm63+uTl0YV5V/mmGHQot2VUEgcspjuYM3X6AHtxPor0YwjB+vl7Q92R970=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD5L.SW02' LIMIT 1), '自然进风口2', '234B5B37408C871D2326DD8C3BD146AE',
  'region', 6,
  1, 'cNpmzQd6MB7kxFEppyBiCvs93qe47CTUoHtHq8M5vDi1E18KeZoLYDn5zJ9ZpTMHVNvrpokRrrg=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.22' LIMIT 1), '集水坑6', '2437625E4654497D6A2B2F8067B238B6',
  'region', 7,
  1, '3v/Pt9SqCtEFrtCM4WV/VxGnWl8XKXDL26y9qZFc1Rh3XjXiqw6V6M4etBimIgxxICQ=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.HSDJ.03' LIMIT 1), '机械排风口1', '246C31014D128975C9FD2A9411077269',
  'region', 3,
  1, 'AWFYNnDQLNfJfQNndov4TNRli9P8qb3pcq8/qHZh1FaECmONx/wgL7++cMJiKsiJ7OFLRXL8BXo=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.HSDJ.05' LIMIT 1), '机械排风口1', '249539BC46F2CEDA999151B225F82E5A',
  'region', 1003,
  1, '84/92U8pwVHzTpaJ63eUnm9fObDoqFnJEtLGYz7UnQvntvuEKaHnp2MBorU2aCrXCU1NrV3weCU=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLB.GX01' LIMIT 1), '吊装口1', '24A396A34106892F29DFD294B2D1C745',
  'region', 1005,
  1, 'SFloi+vCQL7+QUFGidHRDxg/VnnvQeddsMDYwX7Bkn8I9tVJ3QA9Wy6iyPuIQ5TsSzM=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.HSDJ.13' LIMIT 1), '出入口1', '253C7E0A44F6D0B5C8894EBA8232EF0D',
  'region', 1002,
  1, '6zB3fpQOlwAvv2/GhWYg2EYyeiLG+A8yoITjgH86SOu82I3UBZ+f6woWvbLmOSRgf0o=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS08' LIMIT 1), '集水坑5', '26270AAE46B15B7C0350B0B2EEC92179',
  'region', 8,
  1, '4roG9SwwCnyNYv1jXXGYO4QNp1j6y7Tn2ybESTO8/QiQwVLFmRsPh4xUrPBDvqv2sd4=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS10' LIMIT 1), '吊装口1', '263AB4DF4CFBB4E0400BBF9DD70FDC84',
  'region', 6,
  1, 'gzvTxRJXOkp35ETy7FIHbFEhsyGKBLg69f/IB65JckF9QMMjeiSiuud2Ak58HBjvZeU=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.16' LIMIT 1), '集水坑3', '26D8721B42BEB47A3A613A8C48ED6429',
  'region', 4,
  1, 'vj9feAfqVNDirIBJJSCXcq+yQFo+2DkORTTSzbuyBEPth87Q0xYjasSqIhvOuxkjUcY=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS13' LIMIT 1), '吊装口1', '270E101B44A427E9601B6585CFB82531',
  'region', 6,
  1, 'XWdh2CQCcRIKVWhv+gZSmJUjUSRKjAZaXR/X8uhCYJRZ4snRblcNbbX3auSeoqC9+Hg=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS01' LIMIT 1), '引出门1', '27CC4B3941862F131507959EA04971F5',
  'region', 1003,
  1, '65jcd/XGZ7vHpVY10GalZAkuG2PKRvSMyb0GhOVpRoqdUa6ui0U468XZaDbZDpUzsDw=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.22' LIMIT 1), '集水坑8', '2802605540D5AF5F47EFA4A12B208469',
  'region', 9,
  1, 'ptCCugiT9ZHCUcGf2EaK2slsFW63O8l8yLw4Z7do2oy4pZNut+o701xPfhUsDjxzb8s=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX16' LIMIT 1), '机械排风口1', '28839ED24C10A50E4D1160AF48E2ADD4',
  'region', 1002,
  1, '3Gsh/3nKbftHmI4peF/b4mKeWGO1O09GLiMNbAB4O7n3hviDSuGnSapt3mSiTg5Rr+R4pH2vdfU=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS05' LIMIT 1), '集水坑2', '28AF89DB45B9F90A8FA5A4ABE6D5D66E',
  'region', 4,
  1, 'rAZ83w9tSBeGq2n6F8w7Rkwf2Xg0Gfc1Gj/T7EqHfdKM+ENeprtCpjDtzCzjoIWSSKA=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS14' LIMIT 1), '集水坑1', '28F4EC0640788927AEA367BB9CEDE148',
  'region', 1004,
  1, 'wr7PwhrTSgfiPxuIFfjMVeI2RzqAPRr8tFmZInKSqLeKquC9B7i/BD83yvHddkoSrc4=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.05' LIMIT 1), '集水坑1', '295211B94E7F76E516F807AC139C94D2',
  'region', 1004,
  1, 'h9qNiE6Sp9osVgjTD17X+Ltp7SPyB6WqLfE/szSIkAop0Y9eh3UcvWrHa4C+WL/AV9Q=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLB.GX03' LIMIT 1), '自然进风口1', '296D1B314596EBEC5A11E29B995246E9',
  'region', 4,
  1, 'ybwScuylGgJ8Lo8UHWeDDTTI9vN1hDrNYAWP7iTgcLZeqnEdY+x3GfcJ8mIZbUyzeJOQbkSsZ4Q=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX06' LIMIT 1), '机械排风口1', '29BA47034F7FB0E2ADD270B22C5D5EF5',
  'region', 1002,
  1, 'vBd6pG2pJC4MdYtBmBPV5WFNUfOhwjvMDrH6oEs2lx5a9H0pyr+eyaF/a/O6Q6waozOSiLmj8+w=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.HSDJ.02' LIMIT 1), '自然进风口1', '2A5E5ED14A0F15C18909CFB761E00575',
  'region', 1002,
  1, 'lTQFmZXUSxqrafwsfPaQkycUzggOOKVfqlPRtS+Zy7jMlFO4mU/L4WoXFCy9oiXIxoOSxny2114=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD5L.SW01' LIMIT 1), '集水坑1', '2A8F754D4B4F996AFD53D7B6989971F1',
  'region', 2,
  1, 'a2ETdJgHjC0kXJBW3wVSL/eBySuZ596Xvd/ruVHp+iWIbPxbsOVuR/4BKqlaqb16pnc=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.12' LIMIT 1), '集水坑1', '2AD550F84FA3EBC24899538407A2FF82',
  'region', 1004,
  1, 'S2JPSn6y8lLA21ZOhuco8ydGM/rtYZnX4BZuyZU0+HxQbUXQHBf1T1yIIt7/bAcpXu4=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLB.GX01' LIMIT 1), '自然进风口1', '2AF9B65B4720B16D61C2A086F7568214',
  'region', 5,
  1, '0OaXNVlS1I/KAPTfL9sQqvtERcc5OTTvhI8IFmzT5UB30krq4lfjqf59HhFZ8AwraMSuu1EhIjE=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLN.GX04' LIMIT 1), '集水坑3', '2B3CFBF84CB160B2CC986AB28E20C073',
  'region', 9,
  1, '3awJoCVRbXJEG7sVjRYZW3ZaZ/HdzuJkl4u4HMvnEd2dQBkWahvSmgflghkvJFYj6fQ=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLN.GX06' LIMIT 1), '集水坑2', '2B93F813400FBFC7C2B5A1A239E9275E',
  'region', 3,
  1, 'PQSi011soVUVaqOOqd8AXKLqGRLFKqx3g8I4aag15f0ZvGu+PogTp2FcDIcDjSznI54=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS10' LIMIT 1), '引出门2', '2C654C0B460BE49883A4A4ACE320A820',
  'region', 1004,
  1, 'HfOBFnIqRmw2YpcuY8Jm3crIlBkgiMnDzbNQAuluvb7NOjIdYsFdH1o9xzWos2IsOkw=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX01' LIMIT 1), '集水坑1', '2C94446C42000B7982C7ED99FC388BA8',
  'region', 7,
  1, 'wWgb9SZFVHaLw+RT/kx1vqWxWemJWv74SiMV/lQ5h5ygzHxhBRLbx2aYyMJoP9IoYFc=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD5L.SW05' LIMIT 1), '自然进风口1', '2CBF31524E85A899FC2A10A99044733C',
  'region', 2,
  1, '65w2o7PEOHL5llBxNtYkoXLuOsXCMLx95XNqCBmBj969i7day1u+qYDCHOwRgxfV6DqpfuckInI=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLB.GX01' LIMIT 1), '机械排风口1', '2D0017264B392A2C2B671BAB93EC34DB',
  'region', 4,
  1, 'rDXjF/gO+7KvdSjPHpIIFuBfopB4LUmdDUabOv7InENQdL3FTAiUIEjzHTpHOkSr0LwiashFoLo=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.14' LIMIT 1), '集水坑2', '2E02FD004087E183C2A5C68846BE0904',
  'region', 1005,
  1, 'Kt0wXovIieNZga8l5ZRhr9qPWF/NcffCWjWB4GwGR/JbvesG+hGIOgCdbUSeBp+cQ/o=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS23' LIMIT 1), '机械排风口1', '2E06754542CCB75C8A3D149CD4F3FF56',
  'region', 6,
  1, 'BJTuuHdqEm5qwk7dkA6W69MB+q00NsLxP0J6GtdVT8ABjCxauLN//YSHA7wH1Pt4NQPiz9EKLKg=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX01' LIMIT 1), '人员出入口1', '2E5ECF6B43060F8314A989A295855C29',
  'region', 1001,
  1, 'U/+vR5HqBoVUzzmeloxjcGu3QoDu//Im52SsbfVJHckNiOG2sM6teCEAQo1lGInQWuLPWlAMeZU=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX08' LIMIT 1), '引出门1', '2F289C834FE22939DA1B9D803033C33A',
  'region', 1003,
  1, '/XhoB+l7yLHaOQ8bt2ukQyVC5t6sNFgTGN10YXLDSyCddrAMiHYuDV1tfsLreSoNKC0=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX13' LIMIT 1), '自然进风口1', '2F88A3DA4B895300D9ADAA8C0FAF2037',
  'region', 3,
  1, 'oet/z2+0XFxwtxFLCq8853odplCnpw7e8FD/6FfRVAIOoVXiEvrVfXTm6UHu+QULuC/8a8wHMNs=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD5L.SW05' LIMIT 1), '集水坑1', '2FCCF15B4D39AA7DE63A5B954A6BEC7E',
  'region', 4,
  1, 'ugKLP2mNiSFIj28y6DPtTMOw1625WY1PriWIXJYzuIcjm0DMVrgpgPcjBV6tEh0zh78=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLN.GX08' LIMIT 1), '引出门1', '2FE9027C4E33AE7150FD88A7BA384636',
  'region', 1002,
  1, 'CtmGVak653yZnTUse/tWurwpjWmYBzaIXqxEddg7Hn0ELVJPDjef0M6vGbGgf4FLOSM=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX01' LIMIT 1), '自然进风口1', '30CBE57A4360810C5567B193B3FA07F4',
  'region', 5,
  1, 'wok4bUOgYMHs8la7Xhdaa9OrWeO+h9daGKe5+9+wt7Y/oe9xLwVpb7UpE9xipoOs8ZN63B5CaSs=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS01' LIMIT 1), '机械排风口1', '31C68E304105107EF3B88C9F25A64272',
  'region', 8,
  1, 'pXU2wruxmwmF4wxcJc+fW2ONLfpNj7rHKmzoTqp74P/agjlPewQtxcQ7/O8mJ7N1SiqFWue5OUM=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLN.GX06' LIMIT 1), '集水坑1', '326EFAAA4B3F1E4C8D5EC0A7A37A04DA',
  'region', 1003,
  1, 'JlO7zWjWGMKhxOc7dyDAzwvSbZP5BiyaKwjjt7S5zlgbHjlI41vSmLCpAVtB40Ml+aw=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS08' LIMIT 1), '自然进风口2', '3332B8DD41E632CCBFA380960F2B29D6',
  'region', 13,
  1, 'sPZlvH+05gUR69NGa9YJ0gjj0x0jCAmE00jauzafiyKpODBMC7ZpBFCt1r0vMEvxRM2sh9sg86s=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS19' LIMIT 1), '机械排风口1', '339F074F45F8AAF273D1B592D42DF271',
  'region', 7,
  1, 'k52fZjZgNsrLlLUvbSbUil9kiJx4ovgPw5WEnj1GJiVcqG37Tmmm7QIRw2Geo1LqMH1MJzJv6/o=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS18' LIMIT 1), '机械排风口1', '33A0E5FD425E9967DC35B1957D356B98',
  'region', 1005,
  1, 'FQ8HrDGRE+rTBG4UibmJBI5uCHLwxElQQuVUKAQbJarvVNXKdysLUCkCofvSStoDqN4YMkw7fY8=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD5L.SW01' LIMIT 1), '端井1', '33AC20C94B089EEFE1FBA8A726B3F6DB',
  'region', 1001,
  1, 'o83u+HGqicCWNc5X5VdIoCWBP0t9iQ8R72yRksxHIVsutZJ3JkP1qjgwh1MEiV8=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.HSDJ.09' LIMIT 1), '机械排风口1', '33FD3AFD4EEE4D137055319158D25C86',
  'region', 4,
  1, '7NMuPsUHxGQQMwWztneadZw+tHvPJVoEnzN5h4OeD5vCEf+eUPuvSr+82ksoQQmtuqH5z/d9Zpg=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS09' LIMIT 1), '集水坑3', '346265E94DE43438C868DAA63D4504A0',
  'region', 4,
  1, 'VbMh/COmoCNOshZpzpQpiRQnTKCKxGGE0fqf72q5aPU5T7KMYGpmkkreaqvESQS2DQM=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.11' LIMIT 1), '集水坑3', '34890FCB4AFFB5EB6E9663BDFDA98BF1',
  'region', 1005,
  1, 'twMCy4XAP7jnP67Q7iNoOFiDzJ4HEMgVW/GmhHKxdbBLeBT7AsbRvd82MQ0bgUwcUHE=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX12' LIMIT 1), '引出门1', '368AFEDC4411EA401949AF807491B6AA',
  'region', 1003,
  1, 'UNeiLeJEP6UuRuSlIERZLkiKkRsf/j08E8Syq02FLOD9Oy0kMveJg66KNIeRejORWUQ=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS05' LIMIT 1), '集水坑2', '36D18BBF4A9D41ABEF4368A3844FBF6F',
  'region', 3,
  1, 'Y9PqmwBeZ4Vtnb2DJ8BU9835Fq20yPOZdpA45r8aszzvyDXeB0J7bWLtFC9MH7wLlxU=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.HSDJ.09' LIMIT 1), '自然进风口1', '38631D3C43076CCA282E3DAED4137863',
  'region', 3,
  1, 'SyDD18LECiXDG2zv4xh/aGUjudfG9d+0/67h+BAIQy08eTnct4rSVaEud8DGcB6ORWb1Nb2n+4c=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX14' LIMIT 1), '机械排风口1', '38AB7FAE4C43C37C548B54B391547F02',
  'region', 3,
  1, 'StnaD9AprePgAHO8HvOhRDRPXj3AHBkHik24L9T3NgUB7POlGDy7WVpITE3BpsOqW09lwIiH4sQ=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLB.GX06' LIMIT 1), '引出门1', '38B0EA204AC9B67283E42F8EED2367AC',
  'region', 1004,
  1, '1Jp3KeA6QI1+HtWtgHtOKBhJkVauCSAigECky+d6wTfoTJReKCjN2/c7JEbqpXgihwI=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS09' LIMIT 1), '自然进风口1', '38CBF15E4892CAEE90C23D81DBBCD156',
  'region', 6,
  1, 'EUxhLlBQjZ80Ic+e5aOc5TQOYft8V+I93EyzO9y6kXAdPY88vIrr3SpLlauPBpOhTIAdWzo/06Q=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLB.GX06' LIMIT 1), '吊装口1', '38DF5E1244F4398C5E88248BC36E9E7B',
  'region', 1005,
  1, 'hc3lcnNfX2lOlbVBZnvtDvjDUQAj0UXJVShnIHKwZiO8SEwN7fhvvOM34DJGEvr1EWc=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX03' LIMIT 1), '集水坑3', '39E774944FCBCC68B3C70283355BBEB0',
  'region', 6,
  1, 'mCP+T2eUdIzoK9jXL04BQInCvt1LqMCQVE6ylgtbc0N6m3zDm4e20xH4BJrtMVJvYTk=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS10' LIMIT 1), '引出门2', '3A315B01457EB1CAA1FEF598352355E8',
  'region', 3,
  1, 'dmEm5YCQAIz4gvtQLoNa0KQedQkva/XYnYFc/92pGJgT/GwdGSt8sxtBYontyxssnEI=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLB.GX08' LIMIT 1), '引出门1', '3B0DDA8F4FA2135A12E33F82BF45CD20',
  'region', 1005,
  1, 'UT2e0emPUipIuwACvRFXiX5fKe09DBQuMnhLNl+OvxRK9DBMeDmsis+ynqvqYyVxZ8M=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD5L.SW03' LIMIT 1), '引出门1', '3B3C873E48D7CBEA11A924879E9E6AB8',
  'region', 1001,
  1, 'Nh8CZAc07FC5tUUEqZ20d0WfFz+C4hWNZI/C66bJMHHSwE5zdppgdRUnyBec3bZdHMk=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS04' LIMIT 1), '吊装口1', '3B609E1E44F979E02404B1A0AE7EED4B',
  'region', 4,
  1, 'dvLvjjF5+iK79/NBLirXm3zGARMJcJLqkdlsbG/uQs6Je0YBgGJ3oHREyjYih8Q1/CA=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.HSDJ.12' LIMIT 1), '机械排风口1', '3BF0A9124953BC85E01BC3BC30157A55',
  'region', 3,
  1, 'kxs0CbvBIAzqggmwQ38+1QuCWKRJ5mrxvzvrIkcpSprfBv9lvFdCUmN8aWshlh8PZNstB74RTc4=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.17' LIMIT 1), '集水坑2', '3C431B6F4E274F45E105D7AE86200F7A',
  'region', 1005,
  1, 'pUwXn8/DEsJQ+zX+1wTBKIk6E7Odj9kTS1sQJZwOa7QnlrWsxPQee+91PLnYkbPZAP0=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS20' LIMIT 1), '人员出入口1', '3C7F87D1402B1705D5252ABD996C75DD',
  'region', 1003,
  1, 'l48GxfJKdRJeScPo/PWD+zPUlfQRW6tMMj9cXnKKSSWaYgLySsyykR2UqTiPm4VsTD3vhoiQ8Uo=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLB.GX02' LIMIT 1), '引出门1', '3CC84465495083E3B16F30B2E205FAA6',
  'region', 1003,
  1, 'o/aHNU/sKrI1MThVjfoj5hdcSkX9Hwnrkia9Byi5rpQLCI5dFd2nStpSLLmHnVGV4Eo=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.04' LIMIT 1), '集水坑5', '3D8260C8490D5B907062D185FDEFA3E7',
  'region', 5,
  1, 'rUfZYj/FiWau49sdJgqJt7Z0wt2oPKpl//l5eRjraaWw4amAf/xwMd8Deacjk5tWHgI=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.HSDJ.12' LIMIT 1), '自然进风口1', '3E9AD18B45B4D72D8D6359A63D793BC6',
  'region', 1003,
  1, 'GHeX9Dqu+jZqr32Rj7I524ZIuamI9++8pMtwp+eDOjPzLQ4piRAQC80wVZrjVUG38fShEo30VfA=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS10' LIMIT 1), '集水坑1', '3EA4DB79443EB7614E294782A5DDE37E',
  'region', 4,
  1, 'biJMEh5Cc0y1mI14nIuNpU0W+QZ/kxPBZLFN8BwzfjztOCJJC6wjh6osPXQQDkrwpKM=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.HSDJ.09' LIMIT 1), '自然进风口2', '3EDF3C8949FC87FEB6A7068C79EB8F55',
  'region', 5,
  1, 'yYrv7n4SmK5eNVW492Ga80UFzTwwJP4Jgln+ZzzPV/almr+XQs24Abw94KPrL9esQMAHNXYuKW4=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.09' LIMIT 1), '吊装口1', '405AFC6746AC6196A689F99C2F213925',
  'region', 1003,
  1, 'A0isshqhBEut8dv/TR/c5GX+yfwve+Jn4c5QO7oM12MfUK8XqvCD2jNnPf3ke8dzLBw=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD5L.SW07' LIMIT 1), '集水坑1', '405C234E4C7ADCF9FF8CD7AD519C9045',
  'region', 3,
  1, 'JX9AcACazkmvTqBjrynzzUgG2yT80cNGTMoghHGAA2H89copm0zDiIwLg8GG8os91h0=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLB.GX08' LIMIT 1), '自然进风口1', '412F21814A61E55D8863438161617CD4',
  'region', 4,
  1, 'aVlsEsusCZHl0xbSH1/0+o1RkpKVX/dF1m5W7JjVBmuu4Sn7N/5fgmZYdIK4mtRNJxNROt5rl/0=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS05' LIMIT 1), '引出门1', '415BC0634C3808D5F6FA178278326B79',
  'region', 1002,
  1, 'EjUrQAO6kn+TlT77w6jvoS+/oZ/ue65ho3Fuy3r9MXq0qonhbfx2STBIaQWUOyq0+ug=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLB.GX03' LIMIT 1), '引出门1', '41B8799B414A71841C04DAA311A18B04',
  'region', 1005,
  1, 'cVL6qjQjhtFf7Voe04yTHBMkYQRklW4ZGQADQtNKR2W/ovrBjXCohC18p88RT2OWbf0=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLB.GX07' LIMIT 1), '引出门1', '421345A840F5A4431588A79B294B1348',
  'region', 1003,
  1, 'lu7XP7Jim1K8rdGRm9mp/3kqvALMSFh1mzk8jGNyUaJlxbndxciq/lbRFZW0x9DGj64=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX01' LIMIT 1), '人员出入口2', '4308AE29423554204ABFED9BEC92122F',
  'region', 2,
  1, 'TrynCYh2ybosDw++REdY1Qke+6Z0e9AYjPgu+8CDH9RiCIvGdpdW0nFEZKI68mD8gz2kPYPIgi8=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLN.GX06' LIMIT 1), '集水坑3', '43BCFCAD45F4C494AB2BEB8E17E93C85',
  'region', 7,
  1, 'EtoSpkLtRMhLit5Mk0Z0iTjZw3GVSJwtFTplfMcxaHmgfOAmdz5L0FvAX6OI+YeNB6s=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.22' LIMIT 1), '集水坑5', '43E5268C4DDA01384E6414806900190E',
  'region', 6,
  1, '4422R2aPawqyHXQ7LDuAHFYa15Z9Wl/T3VtCWGGDnsfDNUkT5nUHGNklg+dN3he6Oag=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.06' LIMIT 1), '集水坑3', '43E91AF64096584CCA58A5880D442FF4',
  'region', 4,
  1, 'bjEQc3xEy53Pym8k9ZXd64sb9OhQT+C1mPozbwS38Q5XjDbJfXa1Y5oc/FK5JGLUVu8=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.01' LIMIT 1), '集水坑3', '441D9A0546A534DC6CF8F5A66D18281F',
  'region', 4,
  1, 'zxCmojI2bGVLsTlSqinb1NOls1YcJZqhK0ieHUtc2TpV0Z98tEq5qv5wNmBVH//Yc6I=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS21' LIMIT 1), '自然进风口2', '44EE178B4366DDAE192470811B434819',
  'region', 1005,
  1, 'sLIZUuBghkjgZrJfxbHw2fAYxrXgGSMZ+7LaCUieoIlnjIku0LxEsH+gGi83Uk/WUKCf1FPbTY0=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS03' LIMIT 1), '集水坑1', '44F6B8774DF47EAC3C9222A8BA3D8F92',
  'region', 1004,
  1, 'ivXi7r8KHZ3du4ltFlVZp97toIPZG1UZ/k5mmQWiBpHNON8TPKfpff29big1vEDQc9U=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.HSDJ.01' LIMIT 1), '出入口1', '4530A22C4906B6335279EEAF340B514B',
  'region', 1002,
  1, 'byL9m3LhXh6elQIb++JRisSTs1qX1WMm0glP7HklTdVuJlFwtHy59vzcx1IIEMC/kMo=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS08' LIMIT 1), '机械通风口1', '4582EED24AD5CE2FE0B116B658B9F99A',
  'region', 10,
  1, 'PGU3d5lwvYCLMyrV2mU6jA0EDPe/hRSm9SygTfWD72QYPJo/sNlbYUnQ7MEUAPCyjNkP92XZszw=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX11' LIMIT 1), '吊装口1', '45C4BF7F45075E0B271B319BFA5EBD84',
  'region', 1004,
  1, '84WuLz+u95LJUFyMjALBWu1mjFAh29KW+e0WgORkfRUwu3kBVSvUjE6DXvt/2QDc1Ws=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS14' LIMIT 1), '集水坑3', '465EA1794CC63FD1D7BFF4BC521DA022',
  'region', 4,
  1, '065a26KMSV92Y+rEVY4dXHiZueXumkWDnYDzm5aq4jenkLFVlGiyHsmQwdkHg9BxqC8=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.07' LIMIT 1), '集水坑2', '46CBBA3542439E5DE741FD8D6B6D2F06',
  'region', 1005,
  1, 'inS4u5+w76GFv/IeksyHuJOAB6hGP20P8471hFc/0JqrtFdZD7DOoDKJ8Rk7kmHcrx4=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS07' LIMIT 1), '自然进风口1', '475999A2493EF5982E1EE38EE6318A8A',
  'region', 8,
  1, 'olu5j3PN3ErFtSg0jIghmtrXn9B2Uk3JEj7vAs9BVlLAXy6jiaUDLjnXHjoB531mvx+jieEQ330=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX02' LIMIT 1), '自然进风口2', '47C00BDF4555F8AC255D03B86086866B',
  'region', 3,
  1, 'j6I3geC/V/L/+3PMS7GmZRDbZuau8zVarDoUiHti0bI0RvDArs4R5tV66IL8n3zbgxwtJyAfKs4=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS03' LIMIT 1), '集水坑6', '47E6D71441BBE699D5347A99591B6A79',
  'region', 8,
  1, 'w6t4BeZDfegEGyQbQ1dPBhhbc563dywCBPq0TTMoD2DCtJC/tRV+I74zw8PRLKffWBg=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.01' LIMIT 1), '集水坑2', '4800B94C42DC8F74A9F85A9BA048EA8B',
  'region', 1005,
  1, 'r/IZt23TAqh0H0RDSzDsSVugy2muFR7+cfQMI1mqMU6Z/UGwkTBQo3ivKoXQUQMG9js=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.20' LIMIT 1), '集水坑1', '4802562F43C48E13E2BEF2A9CCFB95E4',
  'region', 1005,
  1, 'dgAxrJUQ/kkHHXEp5nhc+EOOVDiohwj6yOsvntab6DJFeKknTUNwwrrq1CevK9mVMoA=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-1b468a9c7b8e44d7ad664dc096fff9e5' LIMIT 1), '倒虹1', '482D6FB5423BB875D1DB01BB6717FE81',
  'region', 1003,
  1, 'rrQyAutcJPM8fxFlNkSRYOzOLFWXhnCq0Zb2iM7wbbKWYoKGGO/7S/Ql4Nd4puM=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS01' LIMIT 1), '交叉口1', '486C4B7449E62C3F811B4BA1D0AF0C1C',
  'region', 9,
  1, 'iDVNgxYfU7q2cFBQvk2d2ZhPNa/B4FeaJGvCIb4Tgijq0TmP1dMYOHaikhjiF16VLPU=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS09' LIMIT 1), '机械排风口1', '48762B59449B91C99D9833BD7E92178C',
  'region', 5,
  1, '3FHbWRfnraYF6AKYAllrGFqleWgI0uInYcY2Yg7tlbCSc2SpqtQA9wM2Amd1vXEEYzGsWR1pvKQ=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.02' LIMIT 1), '引出门1', '49971726405F7ECE282953A7FA9464AB',
  'region', 6,
  1, '0OMv2gzOmyZFWM8yvFd0asxxZR5l9oL+pBzASC7U27llChSkHLM2YfEf8kt0Ovl8ADM=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS16' LIMIT 1), '交叉口1', '4A41BCF64256EC495481749988BAC7F9',
  'region', 1003,
  1, 'oxSy68IztEJpyGtjV1Knnp7z/orSzGVP5Au9h7D59LB+UoLRwSSj7LuOIRBKo7RZUC0=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS08' LIMIT 1), '集水坑2', '4ACC7EC24DE8630E81C51B8A4DA51151',
  'region', 1003,
  1, '5QESXKNMl/KibBIBJlPQapAlz/p/0xaUbU8EMO5Gf+T/8AhBdSGguT5PZsFNPMUdv/I=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.09' LIMIT 1), '集水坑2', '4B5CB4934CED50A29ED757823275838B',
  'region', 1005,
  1, 'AA+cRzNKTYvktot63DP048df6AHTR1o1veYwT3fJTZ4K4UAfLte1RGCe21QUx1iSRa0=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX10' LIMIT 1), '集水坑5', '4B8D8BD4445C949C5A56EA85E8724AF3',
  'region', 9,
  1, 'wa8fVMfiWVM3LDZ6CB/Jwpnim071GxZuOfvNYp7sjrRHrBzkHW5onksYbR0UIiwkOgw=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.07' LIMIT 1), '集水坑1', '4BF8A7EF4CCCBA5C36325CA23C5EF804',
  'region', 1004,
  1, 'Jx313P0+yu7ypXJ1HmUdiv9yCZXWyDHbN+FMaFFOpgSIknuA18tjk0K13T3RFND50Cg=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS01' LIMIT 1), '自然进风口2', '4C5A36A94ABBB45F78A937906183C9DE',
  'region', 7,
  1, '2mX0TJMQx+SK7mW2nBS1595XBPNisjayKgDbjICg3rTleldi/qYSOIDktVw6TeD4fcUcHDElakU=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD5L.SW06' LIMIT 1), '引出门2', '4CC6706B48497EBC23A27EB5D57D1735',
  'region', 7,
  1, 'hVClB9u0FKyQwIH+w0gFjTfDIV5k7ZkS1cZN3eg5JvPRuvnznZ0UgJHqwJ8IdXjQP00=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX02' LIMIT 1), '自然进风口1', '4CD2A248400817BDAC6C83A5AF72D505',
  'region', 2,
  1, 'uoh1OUa0kHzIxR20ICMcOmGnm/DsBzq0fbsLAUUKBH/P3fold8I8gJp73CWcCflb/PcRHoK4oXM=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS03' LIMIT 1), '集水坑2', '4CDB8D2343188E8C577F34AB611712FF',
  'region', 4,
  1, 'PGnVfyG8yplXLP9So9T2b8ji3KI4LAFZbyUFUW7SOmsYOUiWVG+rOcGfAoDcfFsWUVI=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.HSDJ.07' LIMIT 1), '引出门1', '4EB8A69540E962665A5776AB5DBE04FD',
  'region', 1002,
  1, '4COYkhBBcUalzvaDFl/dqczx62CVL3uUAh6tXvDZ6AMRiSLmY64XoONvqOqEJZijr7U=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS01' LIMIT 1), '自然进风口2', '4F8AF54C4151803382DF8C844A061B56',
  'region', 9,
  1, 'pd0HWRWYvu3gd7dlqMHKeRRU91V0b1T1FCb6H0Lh8CwI2QiyxFnwOAb1CsfUG/S3aAA1SAwKggA=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS08' LIMIT 1), '自然进风口1', '4FADA8E04D67C4CFB86D73AF6F46B8D4',
  'region', 12,
  1, '0lS0JqSopmQVnOMyEdroU+D+Uo4cqicksO2rtYPIfUxTXn5uCNdiMWidCA/AKRZ13lz7S/O3gZw=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS04' LIMIT 1), '集水坑1', '509D93444BAD449A60A919A8EEDE0CDB',
  'region', 5,
  1, '4zEMmu6VAPFIRQa5beFx3RNqfDq7b6z6lUejgIHPQJzs6yA1ZRIOGnZDYNx6eWL4WI4=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS05' LIMIT 1), '机械排风口1', '50C52CD54DC019010F2A24983A4B4A87',
  'region', 5,
  1, 'zRVEL5dz6U9K74DgxPNeg9xXPd4wpXWbw0ZGDd9gJnADtWqW0q4WCO3BYiqupLUMMG9oITWoiDw=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLN.GX04' LIMIT 1), '自然进风口3', '511026CA461C0FE09D431BB25AF1C1F1',
  'region', 8,
  1, 'R8b+XR3w5hizD27LKbvHX0l1dnlQMBoatk43XQ/51RojEEfzgoipC61uFRpfIqSOgu19s6HBem4=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS19' LIMIT 1), '吊装口2', '5116DEBB4C0950F240D706BED0C201AD',
  'region', 6,
  1, 'OBjC8OVZbX/gVEQUzJ6KC5O4JrNZS8xvZW6z7CDfDu000w1UHB79EMClJbVsXPAUeKc=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLN.GX06' LIMIT 1), '机械排风口1', '5147417B4E9470C76B066997C4C7505D',
  'region', 4,
  1, 'qscf5Md+3dH+kq6OaroPYTxoIEvNhmtk/Qk46f9pDkVeYJI9CRTRgbHBbAKMZSW+QDRPSTjgOLM=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLB.GX06' LIMIT 1), '自然进风口1', '5182F7E04B22F5B2ED4B268388FD1A59',
  'region', 4,
  1, 'Uz0ATBs4rngPa7S6EXV7Zjn8r2jwWfTm+cbvJi8F8WzPncu3f7NElSQ4rExlKRt8e6hHu7eWGYY=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS09' LIMIT 1), '引出门1', '51DD664A4E15C8A524ECDA85485CB1AB',
  'region', 1003,
  1, 'vENC+F1jzgFJ89astUAZvW23l1E7jFSwauCIvWBw0dLWBnRfBCX3tELbjWpj7CQAApk=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS13' LIMIT 1), '引出门1', '524A2F744D1E65ACF2173F9D609D2E2A',
  'region', 1003,
  1, 'aZD3a8UCmOS9xyg8TrdJKgqXZ/xaUNYUQB5oCLe8K+YdYQJjEHQaeHEF9gvK6TFgRKQ=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX07' LIMIT 1), '端井1', '532912E346E0C1801ADD6F85306D10B5',
  'region', 1005,
  1, '+r+Bak0DKqhOBobWaSBgvB0jseJmHQYGQJ3F9fytIwQ41S0AdHnEGJgHR8VtaIA=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLB.GX03' LIMIT 1), '机械排风口1', '53A1D21F43EA7A9C983CAE8FC8B73737',
  'region', 1003,
  1, 'NUooFHfP8uJ4pbEzUNZUaV8H2NsVV6WrlJbeq7qBfJ/0ROW0Ao8dXPMmZ4roM5XewmUrFXN6GoU=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS17' LIMIT 1), '出入口1', '53C40A5E4A05991FCF92D5B759D3C08B',
  'region', 1003,
  1, 'kyUMrN+g5dQwWotRj//IWMPwHjHX8Yfc2wxuAw3VUEeMTVjJGBa1nTA2P9HhCaV1aFg=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLN.GX06' LIMIT 1), '集水坑4', '53F6C5184D77642B40F034AF80B00A76',
  'region', 8,
  1, 'Wz5Mfb/dZBhOJq3bokd2DUmsfMlB5knWcysAF7pKTBA8f0LgPGsqb2TD/oR7VcSzJYc=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.22' LIMIT 1), '集水坑2', '54BCA2C746FB4B61CAF67B865B30A527',
  'region', 1005,
  1, 'EqIhrFBVjpmVVEy6CIoLbMeEDD8QBullWvELS8AKfsIR/q0fC9AZyq1L//xfq1udOE8=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX18' LIMIT 1), '交叉口1', '56B42726413E8102929E298F5C074B46',
  'region', 1002,
  1, 'Qwy8zS0vJQTfrcINQn4ntcLs2AKMjddcspr5RGaesEwdpd/3g9LAHjk1d2QNpCEyA2U=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-1b468a9c7b8e44d7ad664dc096fff9e5' LIMIT 1), '机械通风口1', '56D64A6A4A7222708CBB56B0D10B1745',
  'region', 11,
  1, '8y0HyWi3P94IRICtA3P3dHY0rP3K5b1IZWuziRR1q5U46Pa3OUxcP1KKqkuLmkgfVKM0GdTrJkg=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS11' LIMIT 1), '自然进风口1', '596669FB44A06D8544D4ECA19438E086',
  'region', 4,
  1, '0TyQemRoTcq8Bbd73l7/re/Mrqrxwr0FnzDtQulIOcbsgjpMiLrokIztbtVie69i/QIZluozodc=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD5L.SW02' LIMIT 1), '集水坑1', '599B63564BD20DBB9483D584748D758E',
  'region', 3,
  1, 'kevd93Vb2Ag+9dhpJ0MpgpStxW88j2rZ65aB+wWwWNTg9RCzsqLbTBbTLvYeGWx15Ys=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS08' LIMIT 1), '集水坑4', '59E0FB80413D59AC8A619883FA0F5DD3',
  'region', 7,
  1, 'CUydDga51oRfUceqzf/agxmHXCKdoD8RIVtVm6she3LjgDbNpIAA+jHQ0UEqzYWyh4M=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX17' LIMIT 1), '集水坑1', '5A0B8BC94F1346D74394DFA524CF7AE5',
  'region', 5,
  1, 'M7KCr6XSvtdTFfo/xdq3NyJl67Q/KhGEh7a+C4FNiMdVc+2fRbMOlzhOyjUCNzBfIi0=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX06' LIMIT 1), '集水坑1', '5AC2935A4C950FD6916E3EB6A0C36178',
  'region', 3,
  1, 'J6oLBN9P7jHx3/5weJhNYW6ZzHfGJZa3TLNNt4ZCqRg6emqI5/0VpSfejKqibJt0gzo=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS09' LIMIT 1), '引出门1', '5AF0FCA647F705CF2C84EA9AD738959E',
  'region', 1002,
  1, 'ug1QNdpc+9TwSCuoFOpGF0GsTq7I+X+GK7QSw0Q/65WHoZ5iLAvY4xur05Myk2Dr0vc=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLN.GX04' LIMIT 1), '集水坑2', '5B6EBB33482DB06C7471AB8687E9330B',
  'region', 4,
  1, 'U7du9Nq/FFQSQCI3Ykh/Gn2I98VuKq3kXX+HoYx27YsJ/01Pa/pZoVs8yQlUznD1ctU=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS01' LIMIT 1), '机械排风口1', '5C33D03C49A4E22DBECFF5B89ADF581E',
  'region', 8,
  1, 'hnDHdTrYLZ9sybVJ9Yz5emefl2yWj1dXhO0FgpCha2GJ11VzyRq58zHpJmkuB4GcUuYdDGQyo0Y=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS09' LIMIT 1), '集水坑3', '5CEF1630478517BBB29C65A09EA89E89',
  'region', 4,
  1, 'XTQ8FSnEQ0g0ik614rCgw1fbuKAk6GOYAt/wQBRfLB8Le5UlFXnwgwrirEQg/0/jmEk=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX07' LIMIT 1), '引出门1', '5D49C3014C54813CF7B47C86D4947ABF',
  'region', 1004,
  1, '2W5OmwTHvxFR1/ow+NR+yrr3OhhHLjCUmf2GLv4UhPO7nkFyc1nNqKnB0L9y34d441s=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD5L.SW07' LIMIT 1), '集水坑2', '5DCFD78F4A99C87C4C41FDAF2DF3141E',
  'region', 4,
  1, 'nB3cF0WD/yi2M3wGUXHmkaKrtv9OfKROiteBBjvD6qWdILdiKCeY4Xpz4VObxa2W2ik=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.05' LIMIT 1), '吊装口1', '5E5BF1174298869B72F719B1867AD4EE',
  'region', 1003,
  1, 'AYiWWGk21abTgKed5rrHfx1bBnk2IjEG7B05VNPLK+NrnHR57swGcNK5CWh6f5Pm/G8=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS07' LIMIT 1), '机械通风口1', '5EA735924EFFEFB67EDCC79593ED3532',
  'region', 5,
  1, 'JJD692DK9jvLlunh/KP4EN63GwfvqbAKJK5AbOtJzE0NxxT9l76V/DuMeqkBKcbOtCH/vCF6DYM=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS08' LIMIT 1), '集水坑1', '5EF442D1462D1BE41B3424BBD32371CF',
  'region', 1002,
  1, 'p1zBfrazHxrse0yr2xCzO+/xGFPlZUwaXjKEWou7mh2jiPiEyBZYV6HYgrCAxm992+8=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD5L.SW06' LIMIT 1), '集水坑1', '5F25B6BA4A27F34B71EAB2B8055E8AB2',
  'region', 5,
  1, 'EbeUfWIKFqBABoR5+McnPwgG9suEAYeiO+Umewy4yn3murWcjdVXCd2QhvP3c8kuMXI=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD5L.SW02' LIMIT 1), '自然进风口1', '5F2B89424BEEA399941E0CA301E3C79A',
  'region', 2,
  1, 'LvQxEewxilfyqTfb+p6Vo4+BytWKRO7besjvAwCuMkz1rzggUYtWgiI9pMM8sawGO2ff7HypIGs=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.13' LIMIT 1), '吊装口1', '5F308A49433253A4DB78A9BBF0E16473',
  'region', 1003,
  1, 'ZkXfTCDQVSsaH3dHTnRZdt7RehQ5c7K6PDMlzFDLVmJgtqGoEVpBcnzZhmdBEvx+gxQ=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX04' LIMIT 1), '集水坑2', '5F69FB984BF2976FFEDAE0A76D9B1E66',
  'region', 5,
  1, 'nquJf+ds8b3AfSV58kw5+Bidw2a9gqcTAh8PBptOSTnENUy4ayV6NcjQkxakcDLDhHs=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS11' LIMIT 1), '引出门1', '60D9D5254F790BCC221AB6964EE90334',
  'region', 1003,
  1, 'pTjgqFDzCUr8VoMUM6lJIOI6EK+WnIiWYgMbluKa5GOOBJqFdurNX97rS8BNJQYIJMU=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.21' LIMIT 1), '吊装口2', '6137FE94447966EA381714BD2CAB64DF',
  'region', 1004,
  1, 'oAm8BOze/ngVPVAd9GAqOlSH9hT8gkoGKPXNGDSKCwPm3kS1DADXoTZLuxAcUIBhJ0k=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX19' LIMIT 1), '机械排风口1', '61DEED9E4C8F47D0DF1BE19738E14C07',
  'region', 5,
  1, '8Ml6fR95ZmO01f8Xko88jJ+2x+PyFQj+S+gtlRyuMWiOVbuaxifaxdqluIQcE5RXLZ6dfo1/P0I=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD5L.SW07' LIMIT 1), '自然进风口2', '61FF7ABE42ABEF9A7A463FA37D7408B3',
  'region', 2,
  1, 'dW3z/lJ2GEqUc1vueAYz8n0w7Crc3WdXDrwLGVg+Azw1GFzzllhQhjqjGrlp9dfx6r+ubkEvsk4=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX11' LIMIT 1), '自然进风口2', '63AD3D824E9FFA4394A07AABA85FE459',
  'region', 4,
  1, '6t1/BLKXA9vqfaQMTBDLVnx7q1tW7fad/7YLY7rqUcl96WMXjDMceeZSUEKHjXd8EkYSE13KZ+Q=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS17' LIMIT 1), '机械排风口1', '63D04F8944606D7C1B91289ECFF4B387',
  'region', 4,
  1, 'tmA3jGessT784ucaVO3CmU2CIBQde75WW4FeVffSWF9fMuqSFIEMD314YzM7SFh6dB00vYtv3Sw=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLB.GX01' LIMIT 1), '交叉口1', '63D999974FC989551F253281ED11772A',
  'region', 6,
  1, 'r8gOSw3+aU1obsRxfWOei1Iwv1tgbc4Og11CIqhDjss68S/8Llj7iNQc7n9/3QeyjcI=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS10' LIMIT 1), '人员出入口2', '644896E945706D5EC39AD5BED5C3FF0E',
  'region', 4,
  1, 'MNq3el1s5dx6HHo0R8UcliDpxCTzGfJPCUYYK/tt2uib4pd6GJ5gQ92pR6b00m3eecJ9PQJ7oCc=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS13' LIMIT 1), '集水坑2', '64811EE64B68FF57AF0C0B9187DFF17C',
  'region', 1005,
  1, 'U7BnK71D3vLQO2zoPXQ1IbvEN5PW+HDj+CFmPwtd9b6UZpojbMwSdJgGDRLA7sKDclc=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.17' LIMIT 1), '吊装口1', '64B326E842D459C943BAF1AF0B8BDD70',
  'region', 1003,
  1, 'mg6pcDHfDt621xTsQupNJa1/d7xiu5vvEolTxElX09EfgvQpqgnA+HMgwbej6doosOc=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX16' LIMIT 1), '自然进风口1', '64C6054D4F7267B572C65FBE77C86181',
  'region', 1003,
  1, 'Fo3L/BzrObgGcMdo54+pakixglJso5lA3NoG3fkm6rThJGt9+WaNWAqmJaehPcs/DNh7m02Ylvc=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS12' LIMIT 1), '引出门1', '653ACDFE450128FD019BDBB22FB0A929',
  'region', 1003,
  1, 'm9yL+UB38zeJKP0SQ3nZHwt5WGuBUalfe30etZglDo0KttkP5rCabaJKueu0FbDMFfk=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.21' LIMIT 1), '吊装口1', '6570525B460638A1BA7F0691F8A26713',
  'region', 1003,
  1, 'yGFy44r8MVSd/SA+T0HJqYfEeavpuaJm3F2NhHS8FZeSKLZhnqMu8DcBEj6ocRcNkYM=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.08' LIMIT 1), '集水坑1', '65C58E8244E491CD17EAC68BFAE18071',
  'region', 1003,
  1, 'cGkdiVGqWU+lZSkg5zZbeabfvguT7aQtZnbsuZ6IREDDW5Min0o3KARBLJHPbYRcXBU=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX07' LIMIT 1), '吊装口1', '65CC96EA45AEF533B77D398353EAF490',
  'region', 1003,
  1, '5EbPsYitMBecYmEGLgeylIGleqq0A8kGjg+uF6A6dCWwNKvndvEoA1VcXld4T1MY+3E=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD5L.SW02' LIMIT 1), '集水坑2', '662212774EAB110E7D3BE79A7EBC0929',
  'region', 4,
  1, '0LnIyNOTC5EQyIEFZqCrWCRNnaDboEcYo4HE2w2B1A5bKLIhS208eDCjhiuEFO+7AEc=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD5L.SW06' LIMIT 1), '集水坑2', '6652E5684FF52C621256688DD306AE18',
  'region', 6,
  1, 'Bu+0NwO+aTIKeMJpxLm36p+0VLRZ373IbKzN9hUl98n/q35UDIHqA0hJvoU7DIAq2wU=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.01' LIMIT 1), '集水坑4', '673CEA074537EE75356C72A2F418B00E',
  'region', 5,
  1, 'aAxjdD2T4Y1kNToZFJtemeMXcH177+S94OQdtFY8r8bpULKcLk7Iw858J6ZDZqQ3wkE=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLN.GX07' LIMIT 1), '自然进风口1', '6753A99C47DE51E611F2A2B349DFD725',
  'region', 4,
  1, 'oqiaRpsY4RwjT8bPWh2A6xG/jCsK9Z1jIEAg9ZA7OUAZk13bnQ07OZpRByjVKEdxFfVB7NvmoD8=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX17' LIMIT 1), '吊装口1', '67726FB54BC42F7E80D866A718976928',
  'region', 1003,
  1, 'Hdum4EbbqR/hKzF97xWnHdroiGjCJOSgKLWy5oq2ZLGFEZqGnfGALDYZ0bN4wFJeKCU=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX07' LIMIT 1), '自然进风口1', '6874BE624DC775240B6AF7B5715A6341',
  'region', 5,
  1, 'uJw9gOU+uD147JaXap8cETvnJ3Fn6DixiqBqg1T3YQfDbm54+YcOVSspMspaThH1Zt1zyN7c8uU=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.19' LIMIT 1), '集水坑3', '6A0D5430477F5F3C3A5A13943AD5D0E2',
  'region', 5,
  1, 'geBIh3ppDfehwMQA0bGY1ucQMnrIfCMEroveZ9pSL2MnY9ushxTIfb2qhh9BFPwbin4=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS13' LIMIT 1), '自然进风口1', '6A12F3154F2639B8CE41279E8319683F',
  'region', 5,
  1, 'WsIJNk2oW7b+BeYCi6IPIacuHW/LyHJfnlJjDBv/ZDatXKf0UKX0c6Qsjkn2pEBxhQqwD8bge0I=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX04' LIMIT 1), '集水坑1', '6B2883C54DD557C4F69536902F6DE2DD',
  'region', 4,
  1, '3NiHyYMyFUh24Y+sGyqr11Em9AsjAre7dI3WI0YUwtylqsPozfJS1xeiKVxfRAgZTV4=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.21' LIMIT 1), '集水坑2', '6B3742784C3389EA4F4BC8AD9CED63B1',
  'region', 4,
  1, '7sDgUIrQWUGPEMF2gv4PU7pEFecKQMRnpi52ltJnswm8cDcHx6jdK5roYqcwPyURwdU=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS08' LIMIT 1), '机械排风口1', '6BB973724B889CC5D8F4948236AD7822',
  'region', 3,
  1, '8JK+aVybO8rEMf65dP6HhGUXfeXT5nAWSJ1bIsmaGrZ1MDI5VFNfxdeIB6qUr49Vu9L32w6AITI=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.20' LIMIT 1), '集水坑3', '6BE93EF0462D7CC9EABCCBBC8E28A789',
  'region', 5,
  1, 'jMpgc27+oj43ElV/3j5pYaI2Fkhkp2ua0koM5lR6ZrdgBmYiKfRFem9sGEhyLw3uHF4=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD5L.SW06' LIMIT 1), '倒虹1', '6CBF2CFD4847F1DADF270B976F0E0C88',
  'region', 2,
  1, 'aBB7bp9XRaMkXTBwSO35d2CYIFWyJzO7Ann9py+BzAqOulvu5XY5+MlvOF9f0Qo=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD5L.SW03' LIMIT 1), '引出门2', '6CC75943460463C940F93CB75E3165D3',
  'region', 2,
  1, '+6iWQpJKnzzEQwxtVzVWUoien5edVkv6oF0PepjyebcNFSCluQ1m88BoN6GcVpIvJ/s=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.19' LIMIT 1), '吊装口1', '6CD81E0442C8214CB28C5686ECA87D5F',
  'region', 1003,
  1, 'PZXalAnJupvOr0Y9jrmTU8QMCtCp8YNkzvbZuwuUUGvUPlveOyjfNmJcbgV+0sc1zTc=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLN.GX04' LIMIT 1), '自然进风口1', '6CFD31C7437801A3213074839BFFC164',
  'region', 1003,
  1, 'mL+GxzkcYfie62RI2GUf4nKtNPIaVohMNTTuRNFwfkqTxYjcuv4q1pxU6TUsrBtybo0EXAdh4f4=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.01' LIMIT 1), '引出门1', '6D5A40F04121E2CE08A3918B7B511E72',
  'region', 1003,
  1, 'Np8L1pPmyQhduyw1o9IhDUqInpNLls81/jXqhxuruU1rHTUd4UBQy8OYRbDg46CPfGk=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS04' LIMIT 1), '自然进风口1', '6D8BD33445C11A590ED36194936EAD03',
  'region', 5,
  1, 'mxntGG9aH06WbuoVMdWgZZT6qRFUOhvwig7d48PT8mBLpZfDgz9uh4d7bpg2HHVO2mJ9+OjrLkY=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS10' LIMIT 1), '人员出入口1', '6F0942E041483D04C23AFDABE4D88A7E',
  'region', 1002,
  1, 'iQ7VGD0QtvzIRaoJQJbgwgdL0v8o2RI3c8hr5GXYcgBN5qpemKl75yLqf7Fjy+OnGttEM7UNMdA=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX18' LIMIT 1), '自然进风口1', '6F8F2C054BAAF036B458B98895BE634D',
  'region', 5,
  1, 'YJMMezFoTh0xKYk77IFmqmXDMGiSmzaNNCSZ9cfq5TVt+jKD+QwSYLB3Cqb9xv/D1v6XLnjggBU=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS19' LIMIT 1), '引出门2', '6F8F79014849DA3FEEC7E0AEEFCC2370',
  'region', 1004,
  1, 'UnPEFlyEyv+9XnuMkAoCMUzS8/LS4LaL1bOeZE9D3BujvW0bJFYJ2Ie9Zm/t8gMVm74=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD5L.SW06' LIMIT 1), '引出门1', '6FA2E13845D5F74131926FB970F11C64',
  'region', 1001,
  1, 'M/OprDm85uqZ772RZccyo3Dyws15BPLPVM/Kx8Ed2EC/S5cKhGdJxXza8le0QApeRNw=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX08' LIMIT 1), '自然进风口1', '6FDBB56648B922EAD2950C8839D4B83F',
  'region', 1004,
  1, 'tfMs2OdV3QKlv84qLm5DrLkAjhDtJ5/7aTZmETuYvxFoF0Srv7rkVhpdeBRkJx2GXILTYZEkhdU=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS10' LIMIT 1), '自然进风口1', '712BB3204549BE8DB1E1119525F45240',
  'region', 7,
  1, 'vchV8DCMh7yrl5POuByKqJOS8LnUK1MdAY3mMhd5qgRCKrEvHn3znJKyA6VVU9IszkGQHb9y9w8=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX08' LIMIT 1), '集水坑3', '715B2BF64DBB9BC3D74099B9BD0AB50C',
  'region', 7,
  1, 'uhqnitGRkzJais/GBHosmO+q0t0Gi62mvVtbohRJQkn2KUNS/I05pbxvWq/p6KN41aM=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX10' LIMIT 1), '机械排风口1', '7169F12D4C2B04B6BD86AD94263984C8',
  'region', 7,
  1, 'HdfqncCwv1/dP8AEuBYAFKNILFuEhLWu57HZZ/MyzEwFYjilcXUTgL9qU3BjlQDTJFvoq2Zg7v0=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.12' LIMIT 1), '集水坑5', '728F780A4719FCE6C37892833C3D9DD6',
  'region', 6,
  1, '4UCE7Ac9RjMjq3f5f5OlmGhHTOYJ0PhE8xxjP+xmmcF4+1VV3Kj92TjGpV3hZy4Q+Gk=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS10' LIMIT 1), '人员出入口1', '7377EF2449939EFEF8AB228DB7CB5CDC',
  'region', 1005,
  1, 'GoOTrUKTBKIFTPvM/shK/zCRnDiKultRdt4ryAVTAC3CAwqPFcIWH/hlHcDrrxlnGIgjc/qgS3w=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD5L.SW05' LIMIT 1), '交叉口1', '739A94C747BF15DEF2AE4BA61AF53ED5',
  'region', 7,
  1, 'vXM/jf5/op52XrYkufqWp+BpApE0AyLB+aPKwvnT0R8p006WVnDgpMyzcxRKR5vZeRw=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS05' LIMIT 1), '集水坑1', '73FE80204E922117C5BF58B3640ABAE3',
  'region', 1003,
  1, 'Wt1tHtHWTy9JVe2vV0ycQmVgG83sX9XyWS4WtmHQ0Bc/AtnbwosI9ylyPTnrbSEtpwQ=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.15' LIMIT 1), '吊装口1', '747197D74EA7F35EDDD4BB908BD4F959',
  'region', 1003,
  1, 'UgRes2dBIe7sPYP6m1MJ3oP5QFy1rRhCtQ2ELUb3CxI2YIZTCpvaxYsVSmMExMw2qLs=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS01' LIMIT 1), '集水坑3', '751A44D747868B89016DBEBC9F50DD80',
  'region', 5,
  1, 'GtVst+mO/XAWgCnmrL47J6wqTtm0E2Av1tDC02aCSaYoBldhX+dlbvSkamTfeIE/b3Y=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD5L.SW04' LIMIT 1), '引出门1', '751F69514812012B820A09B437783209',
  'region', 1001,
  1, '2ztPzhTe+ixPO8tAE2Kjasid+yTc3G8zkY4S3bJKbrv0Nlcc6R60t6bdVMeB2M/nNoE=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD5L.SW01' LIMIT 1), '集水坑4', '752268364C936F484D305C842F9746F6',
  'region', 5,
  1, 'C1we0cL1tXkny8N4n82i+gY1j2Mjh6zomjvA9f9ruYmXYF44g9O9OqMqMvEkBuYZ4yU=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD5L.SW03' LIMIT 1), '集水坑1', '75693B4343C20C24E167C8B81D7D68C6',
  'region', 6,
  1, 'N7U90a4oaIXWsMJXZc04CUNPczbVDF5yXW5Jp23sISm+p+WlLXZxfa8VCnBwkAEsa3c=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS20' LIMIT 1), '自然进风口1', '757007414233C651606B4797E3B95423',
  'region', 1005,
  1, 'EFSTnpyJpjzVZjsjly3NMW9hf6iloRa7ZeLfnHcNJCQI965wmQS5V3aQqvlTFHqQkmK31YMhOKg=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX19' LIMIT 1), '自然进风口1', '758D9312459CD437813B29B71E2FF343',
  'region', 3,
  1, 'LHMKZOBVkrjHU70ZqSvEHIDkeRiw/MCi5SUsrY3PLBGYKRs0h81Nz905N6+NXKICG0a+uusVkok=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS09' LIMIT 1), '集水坑4', '75AB9E7F4BC95FED18457BAEC1D9568F',
  'region', 5,
  1, 'yFeltN7eRcn4iLitHf8YkJZkpX+UcokFtPfB2BQZcwO1k0adQkNzTlDx/y+lx9HWav4=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.15' LIMIT 1), '集水坑3', '75B223F245F42873266B74B8284FE382',
  'region', 4,
  1, '6ChpMe7cKpKaKIS35Ug66fhBekmASMryd5/3gxsXxqZh1oWKNod8zpWxsUTCwm+FDcQ=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.10' LIMIT 1), '集水坑2', '75EA0D8D45FA91BE49B14088DB803101',
  'region', 1005,
  1, 'h75iZjxDSpYiwJ5Rg8s+TWepcAs3IcL32AlTZSWDlbxkejww6FFP31rJZfSFFRiSUR8=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS01' LIMIT 1), '自然进风口1', '75F3328A47270A9B4C62C9A914000FA2',
  'region', 4,
  1, 'Smy51NUsG3uFJJusyWFHQAWqDc7U3psYsrRmFollkx3JNBk8T9ld83LnTT4LlnfiPAgHD0c2Hg8=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLB.GX02' LIMIT 1), '自然进风口1', '76055CD1488E17800D4B11B0D30DDE1E',
  'region', 1004,
  1, 'pXqJYHcbi/+e+7BBuh/JLUero1CrQjcuhQF+63YlqcrxXAKkomBqctX66UaG7tyAKYO/+1cRDWs=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLN.GX07' LIMIT 1), '人员出入口1', '76B60D114E8033B52BD1C490A7BCD7F3',
  'region', 1002,
  1, 'ICK2Gxa6SxZtUmmgyD7NfFBy/tADEzoBP+g9mX7Yc6vjn5MhE/G6hJYrshb77gNMo4dHKojQJDc=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX10' LIMIT 1), '自然进风口1', '775559394F8A3367F3CA3EAB29B98A93',
  'region', 5,
  1, 'na0FXD84HawZX+lp0wxWrK34XFzu8PJDlwRyjFWLL0ny82AEOuOvAkEVWTeZFWJ5MjpFXAiB8+0=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-1b468a9c7b8e44d7ad664dc096fff9e5' LIMIT 1), '集水坑3', '775EF5644B1BE8AC013D338E031CEBA6',
  'region', 6,
  1, '/VtPB2dbdBdjfsZwfs0/abRDv/69Eh6vd7t6rqzUW1KtYAOC5xZKVVqnDKARru4fq48=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS01' LIMIT 1), '集水坑5', '77B3A7444BC70832B6D778AA0F74CD20',
  'region', 7,
  1, 'yp2EXtgaU8qNdTMti0KAY6MNff28WKFYIhILm1ayFLXglpxHi21x57oO7cgY1BhhjeM=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLN.GX04' LIMIT 1), '集水坑1', '77C044994F6922B4E5C90E9040195EED',
  'region', 3,
  1, 'gIfglBaVAUzjf6ifrvjm4myLqKD4G4Z5mtn3VsDRlxhD4XBxYkhbAe6vUBH2MhQOtrw=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS11' LIMIT 1), '集水坑2', '792A74D84F889E8D33EFD0A80D08436E',
  'region', 3,
  1, 'CxbzsqSa7Rvk0A8mQPGK2GF+/WRNOTJWOQdg5vCBq7y6Zb/LOBSK+DdqhAhjFxPgVqY=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX14' LIMIT 1), '引出门1', '7A292684426C72033FFA7E91537DCF5F',
  'region', 1003,
  1, 'FrV7tVE8CCMgOgEbm3zjYuurIqcRx9uxJKlVDhW+SxXWrw3rDBE/3AXqs75t+NwQAGA=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLN.GX08' LIMIT 1), '自然进风口1', '7A6D1F56401CAF7C6147509D0B38FC80',
  'region', 3,
  1, 'Bl+9ZwqnB2SY3LeyxQnELpQGpr8PUkbkh3s4jOpypS98VqMlhYacyCY3gv5yd/5JlK/+1dc6zSo=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS22' LIMIT 1), '倒虹1', '7AB3E297415ED10FF1D5A88D5AC015C5',
  'region', 1004,
  1, 'IuzKVp2/fh8B1tQAlp7wN75/14fVOnbJPK/NxiFKAM1tzB1le5ygzLZx2yhn2hY=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS02' LIMIT 1), '引出门1', '7ACFBBE044927C5C01375ABECB95A11B',
  'region', 1002,
  1, '8D5YTQ/MnNs23CyHT1yWHewBPLkSII5MAf6v1fFQQ/sb4kYzc9fBxALoXwp5tHdSW5I=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS16' LIMIT 1), '引出门1', '7ADFA54C408AECF385E1ECA716B09C66',
  'region', 1004,
  1, 'uPo9xsJMsTxsWY/zbXOHZyAMo0YG4cbQfGY8lN50H7FLfvVHqZbLN6DcNEQ7hQeFk9M=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.11' LIMIT 1), '集水坑2', '7BE452A740159DDC63A66396394081EC',
  'region', 1004,
  1, '+vSGcCCl5XEo+xoaAkr9ol+KBTThW2dynq4ivCLCM9CL0gEyQvxYYXyJf+AgPjh2rwI=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLN.GX04' LIMIT 1), '自然进风口4', '7C83BF794ADC2218856BCD92CA2238DB',
  'region', 10,
  1, '+IhczsxjGwnfgunitISqjzgezRr834HO4qygQpl33izec/8f4NlTqrQo73ajXGEv6WGiELSwGW8=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS08' LIMIT 1), '引出门1', '7CC9340D4E01C977558AC288D673FA2A',
  'region', 1004,
  1, 'HO48h1QWT0ZaoAxhIEGty9EzrVdVUGy960x1XemLMsx0PFbkXpE2fd3a4EC+L38h+FQ=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD5L.SW04' LIMIT 1), '集水坑1', '7D5AD885496C3D2005DB878FF7E0E203',
  'region', 6,
  1, 'FLChzFKPgi8qqJZ6kBRaOfyZtMFzFnkOwEk2U5o4beLPrDVHxlEbu4hlXjmPV14ExBs=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.HSDJ.03' LIMIT 1), '自然进风口1', '7D74AD3A4B4301538082629F75F43205',
  'region', 1002,
  1, 'kmMoVLQtjTj21IBz2JePhrr/aF8SJ2nKWGzvXPdtwYNx0JN7wDVzv2bRv9vXiNUU2joohFUIpy0=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLN.GX04' LIMIT 1), '机械排风口2', '7DAEDFDA4C050E57C419B19809D8ABF1',
  'region', 7,
  1, 'q1QLyfsjqW0MNQKZRK+TEzaOu2GQmd8T3tnBR0C7hkJfVT5uc6V3zn6M3C0QNMLFTvo1ndCYBZE=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS12' LIMIT 1), '集水坑3', '7DB0DBD14C834ED81DEAD68C1993C320',
  'region', 5,
  1, 'KbxuijaR5VUY36YdCMzBO7Ktla89Ie3Vi8RPVHf5epNnkFhKDrxteVkakm6pGEy2lM0=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX19' LIMIT 1), '人员出入口1', '7E1C4DD54A6E20D9FE5FF3A892C27136',
  'region', 1002,
  1, 'YGatKgsPf+d1A8YOR82vqdZMytBytCWZuvTCBtVoOoLOwau6lRCES9Y0NwMNkgHYSE1L9WmuWiw=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX09' LIMIT 1), '自然进风口1', '7EA7FC8B4F1B86FECEEDCD8CD6F7DB0D',
  'region', 7,
  1, 'a5GpPD+34taPBEwKCUl+iDA7NdvrvhVxOI2U9G6mEueKxwZjI7HJJ/ZbKO4CzNPrwtDh90+WutA=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLB.GX03' LIMIT 1), '机械排风口2', '7F729E924BF78D3F46571B8C0A24867B',
  'region', 5,
  1, '15tqdL/xVPWcfG79C0Ze+gY5nwgREUZk3EnjhcwQblJD256EUGGFfqk5TfQGSJSi3LhXaTgJm+Y=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS04' LIMIT 1), '自然进风口2', '7FF3CE7D4418B44B0790A5B9C646B1B1',
  'region', 1005,
  1, '5ChfyrewQr49TDoOJeZOjHpf5o9UHCh98yaz8/jCgOY/YplaHNPgXGDhdPqEpTRrdqHCalfAS8U=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.HSDJ.07' LIMIT 1), '机械排风口1', '8053FD4A42A4B912AADFFC975959715D',
  'region', 3,
  1, 'oleBGWGGN4drhSiv+ASQ1DR52qOlhBPXphLBtJm5/1rBJWLTjMfRkTWgffuF+465gWotrjatIXc=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS04' LIMIT 1), '人员出入口1', '81215A7049B3CB224A1D3D8E1F5BE8E3',
  'region', 1003,
  1, 'ndNuZTFP+doQzzq9J82UyNd1kcBfu3QM5lkOPJntcrC7jtqWBP/53hJ2pzrTTqKbh8gWHJRF68c=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS13' LIMIT 1), '机械排风口1', '81D5F43C4DB46AE713DE0CAD1049E6FB',
  'region', 7,
  1, '7SvGQpFNimf95MZogl3zS5YH8ejG5tnQoyigyzF4LcbwXHXw3dl+ghF8JHh2GXbj+Zoaz1z1OwU=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLN.GX06' LIMIT 1), '引出门1', '8289E883416CBB92B610BB8071E6CCD1',
  'region', 1002,
  1, 'yIUq194EoAkHDu3xua8GyQXaYv6/4P5hUtxT6h8NFINsxloO+qm67bXUrFejYfrSrWc=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS08' LIMIT 1), '倒虹1', '8370F7184BCB32CA0EAABF91BD4FF8F8',
  'region', 1003,
  1, 'jnnMMEMPucmUIDlzKfUMm/LB6mLlJMP25ZbQnPdjjYWjTI8PMW3XaIe/ea64Zho=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS02' LIMIT 1), '自然进风口2', '83896D2E42C0EBDDC494D1953A29E08A',
  'region', 1005,
  1, 'yLBovCX3hiXLq42od/5e7MgJ/RGDQDS2s34/LyhkP8SKREa736nGPtHcuEz6/msei/a8X6JSukM=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD5L.SW01' LIMIT 1), '引出门1', '84CBFD0248B5D42E707309A255FD4786',
  'region', 6,
  1, '2+ck0M/bRLB1yLEa8Gx9BltQAVpBHlbiZQIgt94pNkm5XmfvkJBp3037f6Tex5QKlQU=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS07' LIMIT 1), '引出门1', '84D502E343FC02E1077F8EAB02BF4EBD',
  'region', 1002,
  1, 'kHiUhonjHvQJJ6V82trvZY5N9AkrCB1jqgkTq0tJsusatORx2VVE99uuyCo9TywZf7Y=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS05' LIMIT 1), '集水坑3', '85318E42482D65458F9B7FA314DE9372',
  'region', 5,
  1, 'GKAiDe0TLVj6CIzcZZou6ym2yVz3jDB1wpPEsvNwQSfQW+I9Rbdce9GpBhcblbgyxUk=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS15' LIMIT 1), '机械排风口1', '857E6F254E516EFB3611E6A425680981',
  'region', 6,
  1, '1ijxxC+xer/8X5xS3ttLU61mKVMWrx9/z1JWtLuYmvddk+f//UqgVrNB/VeTT73G7S+khUaitUE=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS05' LIMIT 1), '机械排风口1', '85AACCD4420F7750607F6C9DE6565F34',
  'region', 7,
  1, 'K4/fh9BdlyHQpUOgvTV+b280kwme/hMy83/JJAj5Z0TozTY/zm7PagkMoG4FaT5hJAxAGmWKltQ=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS07' LIMIT 1), '集水坑2', '85EA4A7D48B4AE37DEEE3C9188435BCD',
  'region', 4,
  1, 'NVzxKaUNNVjkoPJ59e3wviBarovBm7eCe6Q8NZEicq6uQjer0ealf2lrcGi0gSK0NsQ=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.HSDJ.10' LIMIT 1), '出入口1', '889589EA4C9A483E3A55ADB135F10797',
  'region', 1002,
  1, '72PtkikVG5mhViQN4nfMAqnPfSIQjJdRQDTLXWO9k63oPTw4o/OF3yw9TgLuZdEg2Hw=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD5L.SW05' LIMIT 1), '自然进风口2', '88FE428F4766E944C04D6DBD7DBEA7EA',
  'region', 3,
  1, 'CxvgAhbnX2FGbI19E83tjyRCNemUEdoGFIpxzJol2XT3J6tK6Y+3y2dhIns8+HwgkyorExjtETo=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD5L.SW06' LIMIT 1), '自然进风口1', '8952A36E415A5E046F21C68C0A2B83E7',
  'region', 3,
  1, 'UTjrBqD0fLECv1MAFzby9sfrQqw81Hak+p8edu4CEn6pb7IDEPJeDy69HQB8LNN6S4u/mQklVqk=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.HSDJ.02' LIMIT 1), '机械排风口1', '89C0618A49D7A40A1F85CA97CBCC0054',
  'region', 3,
  1, 'Xn0Lh1P9wL9y57F1SoUQBEzJ/dlgzQ7GlvbBrN75U+UfLv5mFRhqeqPmWXEVYxCgcvHRsGBM1+E=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX08' LIMIT 1), '自然进风口2', '8A5F59A44C9AB5A00A6C42957685BD92',
  'region', 1005,
  1, '3LpISZQ3vTH6WEdT3szdcnRo+FwYRUA206DvNxoa/zFjndqXnfUKJK5UCxg001+PlVYXQEAjTAk=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS15' LIMIT 1), '吊装口1', '8A70157E4D96861628DC3794A83E94E5',
  'region', 8,
  1, '2BSDKUSOsyCG7RWJSqRZnohJVf+pGk49R1YmEqHC++41ATJVMhTofF2sCidxggs1ywI=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.HSDJ.11' LIMIT 1), '机械排风口1', '8BB5D7D543FF29BA653582AE8F69D884',
  'region', 1003,
  1, '4nqatKRgh93GTKPvDr7gbR6jN2lVAgezpDh3D9dgeb5oDq7fbDRXa7xfOQ+sUf/qITgfTUK7Ay0=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS03' LIMIT 1), '引出门2', '8C017F09449AFFDAF6A800A142F5414A',
  'region', 1003,
  1, 'RQ6QUYA4Af87G1IJ4f6iQgBc5A93CN5RFxXDlYF1ucdJ3dNjsDVzst6Sja+uEo/sHaU=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX03' LIMIT 1), '自然进风口2', '8C143B3A4A10BA56F4E9789D976ADCA6',
  'region', 2,
  1, 't7SxRnk6cTqD8/zI78gg4tBAHSEc9Tnxy+D+KRA66tNRkyB7T0t8XvsVKS/hw0qQTdAYe1EVwWc=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS03' LIMIT 1), '引出门1', '8C6E811A496F47E4E45E169D7F5E0A24',
  'region', 1002,
  1, 'jroIV6tpZWOfeZTI0hXcrkPBIBcU8FT+GMEmPp/akIhcpH9+/ZbXfD8tHHOotuYXzfs=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS09' LIMIT 1), '集水坑2', '8D44E94942299AF8147E1483D000910C',
  'region', 1005,
  1, 'obMXZl/+idT3EDKNNFt4X9JOnlsk+M4atWVajswfdHOZWauplAM3n+N814AOXIBQFlw=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLB.GX08' LIMIT 1), '集水坑1', '8DF9AFEA45A4EF6FEC15589111D779AD',
  'region', 1003,
  1, 'CXixWhfbgHMeU7DP21I3AQ6lZvkaNKtejU0SX2zDSUhIfkHFszXsGHYSb8quvmdGU/o=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX14' LIMIT 1), '自然进风口3', '8E03E93F4BACA48B77280BB872C2248C',
  'region', 5,
  1, '4IElGqM7TvsD6Zzj4d5ORvW3xzXOk8SAfR/UaKG+Ki2Cxy6f8Fuo/nECxRPAeB2k0dh2XC59TDw=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS12' LIMIT 1), '集水坑1', '8E0EF2714F9C045ECFEC028181A9ABA6',
  'region', 1005,
  1, '6Po7MzSBF1jdsUPPP1p8vxmsGnbKG05iWU3fuSqYgFi2UYPexoLby6UDInYeEFwsqHI=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.20' LIMIT 1), '吊装口1', '8E467C9D47C6B126BF1EEDB21A8AA46F',
  'region', 1003,
  1, 'rDsoVrvKVnbykEVD5ywYGsul53OuHQEqdAl6cIFcm5Maeay7/QG8PDvso4/gpfHe2mw=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLB.GX05' LIMIT 1), '引出门1', '8E7BCC094E7929D3695A40B49BC5628C',
  'region', 1003,
  1, 'a3CHSHr9nwgTSiIkIHhhv4xbPmEeVm8o7x1X15AwEWyb1KuPW0DTqZyXixyiC85taHg=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.HSDJ.01' LIMIT 1), '机械排风口1', '8F6364F346A55BF5579B40B827973B69',
  'region', 1003,
  1, 'o9ykqZRIxuTN46BVd5MCHnauEAiuv2+oWZLZAWSkjpfO6UXuu3wn9CzQOktL2e9e8jW9SdOTXRM=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS01' LIMIT 1), '集水坑4', '8F68F5674243458E9C91BF972D04128D',
  'region', 6,
  1, 'PhJ/fAHK/fCkaGaYPI/qBQbnnv/aMRU/zUZQcjX926OYRZtqZqG6U7RskVgvwoQ9HJY=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX07' LIMIT 1), '机械排风口1', '8FB6750D44072AE4BE8C58BEF3973DDD',
  'region', 4,
  1, '41xbmpm2BQ79FXTi7y239/Q9YMEhXg7hPw05ll3j0UD+7w2q5K/Aqg+8THCTM8eSHkL+8Ownztc=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX08' LIMIT 1), '集水坑1', '90A9AF984953224995E986A908EA9D69',
  'region', 5,
  1, 'qAb+cQKsU4zPfCkGl39OvDKX97QYBzyGJZDAIKj9aBALjI0JwXKXhSK+pNHLspBRl2I=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS07' LIMIT 1), '自然进风口1', '90C8A154428EB78B221B308769F32DE7',
  'region', 4,
  1, 'RIpMWgJ2XgaPy2/Fq/0YwldzpQsYQVumA8aKRgDYqZQtWOXtseJVlFRKNa+f+7yefIewjTejEgQ=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS15' LIMIT 1), '集水坑1', '90EA028547C65A12704C26A70D993F84',
  'region', 1005,
  1, 'Tka9tvBPHfRDoEp07oFZKizycyQYytbiPRfd+yrk9/5cVtleZafqs7LfePKbNTOFn20=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS18' LIMIT 1), '自然进风口1', '91FA941A4945420986F5CD95B3F7EF0D',
  'region', 4,
  1, '8BmRGP/wM2338eTRWBuPtaBmR76ar4g71XTjeM6G7HEms1TYLztK0wr+1HY8uWY5fDRW5Kl81PI=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS09' LIMIT 1), '集水坑1', '934019624C8183C240D2D7A13B838565',
  'region', 1003,
  1, 'dsscPfdCocdgWPq7lhtAWqCaP2Yf77e3U7q8/jLMmdSNfwac3v3x6MKkLH06byYWCfg=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS11' LIMIT 1), '集水坑3', '9373770C48FADCF1095DCCB2ECD29E82',
  'region', 4,
  1, 'KEs8iv8WUo7P43ziZKhkJHOSs1wAI7rCsFaK3U3wroLyvJhe26VwbbDjGAuHmJWlgmU=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.16' LIMIT 1), '集水坑2', '953BE077496FE5CF324F0DB6CBDB4E2D',
  'region', 1005,
  1, 'H8i0uZdin/UEvoGlFI1Lc5J5Xv/MnvwnpYWEmAfrEsRVd39k7THowcZZK62lpwaxznE=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS03' LIMIT 1), '集水坑3', '9636188643BC947E7895659FF948B8F8',
  'region', 4,
  1, 'm5kfiQwufMckbBes3DKHxNYfsDshaGrKOSruaW+tihv/qErGlgZsjVRAZ5Zin+eN4po=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.14' LIMIT 1), '集水坑1', '96AF83F34DF1B0F36A060A9079535C06',
  'region', 1004,
  1, 'WV9cblb0GOJg+hM9nZ8liLAXA5BpkSJw1CsSTJ0rPDR9Lz6E1nLFJx7UY+Q0GT+04bw=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.19' LIMIT 1), '吊装口2', '9725D697465E04ABFF427F9C3AAFFDF3',
  'region', 1004,
  1, 'mAX0jJQmoRN1MsifkD+XsCDpqqkaJHKok1wM3/pWitJm4VxdGg2HqNa687HHtNwRuCI=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX04' LIMIT 1), '机械排风口2', '98130EC74FBD087063BB7C8DB11A98CB',
  'region', 3,
  1, 'Zy5icTAqfuRh6n4GFwVwqepSyy5I0Iv8cluZN5zDFth4Kd50GCQiSZotT285SPCYlkOLIdxXpBA=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS10' LIMIT 1), '自然进风口1', '983E647344C10E03450CAEBE56A34896',
  'region', 8,
  1, 'Pz917Pq9rpA4G3d/zBqJcJt76hDf10bibpCQyc7C2vjDvQZUyJCu8hgEhe1G2mGWj+5PXvdL8hs=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX07' LIMIT 1), '集水坑1', '985C9A61422C1770BC1511A5DD1E635A',
  'region', 8,
  1, 'UYLzCkFwKoI1CIBVbGO2ghPpOcldzQYfVWF1vj6/cFwI9EeVfVGslFbOUOVVWbxi/h0=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD5L.SW01' LIMIT 1), '集水坑5', '992D201F49EC3BFDF86678947E374CD7',
  'region', 10,
  1, 'YYTRdC+R3uWchvO33vPQYJMX4GLiMhdDv+lJg4qTQ4suA70r5cOm2ayG2bjdhTn2vJY=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS23' LIMIT 1), '倒虹2', '9959A91048E35397C859ABB62857FD50',
  'region', 1005,
  1, 'sc3py2vOExRHc0TAF3f3w4CeA6yXcGKdOAJUWrBXlILJM7fxouSaJ3W+5pESN6E=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.13' LIMIT 1), '集水坑3', '99FCCD094E502FC100B033AEF842BD02',
  'region', 4,
  1, 'MIZskI85E7ysR2EZiABwsjLqN4PrcxswRWvBBet/5eJ6PYrxOugP1ww0j8vIpmnN3x8=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS06' LIMIT 1), '引出门1', '9A25B17C4921D7CD1A468EBE7EBFEF0D',
  'region', 1002,
  1, '2pVEDlHXiUQA1c83i8ttu2cieoTwngPnc1Hm8G5QsyLVrygttfr8bb8jAHgfjhNc2Ps=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX11' LIMIT 1), '引出门1', '9A644FBE46FDF10C28C095950D11C6BA',
  'region', 1003,
  1, '1GHXkAdGqfhMM/Ar08llse+e3Prl4vuSk9zykh5IcHF2PqA0gmY4bTq2cZTX/0hpiHQ=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.04' LIMIT 1), '集水坑1', '9AAB905444750F49642336AEFFC4C40C',
  'region', 1003,
  1, 'UCy4fvxO25yz7ef8N8bCF0jZDv5dsgOUAiJOgptE3L15vXWZIYvf7/HoLgdDsKHXCF8=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX14' LIMIT 1), '自然进风口2', '9B421D83401CFB835D2CF2A4F95518FA',
  'region', 4,
  1, 'D1DYOT61iCFbx0Y+Mt/33Vm0l64UdlyqtxJWnd9oZoISf1k6jIyc0kskbG+IiFk9EbMyCU0UyBQ=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX17' LIMIT 1), '集水坑2', '9C5818A844889B7951E58BB9BC70F7A5',
  'region', 6,
  1, 'RaiXd+MZV/coX1kPFpOF/GJBaQmglR+XIzdwUhDZuFwxM562UplGLdADfcWj4KRVpKk=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS01' LIMIT 1), '人员出入口1', '9C83B9064783DDA1F5CC2687DCC5FDC2',
  'region', 1002,
  1, 'm+0jkLZTRT3gMveGL0oBSTF4Ji37uJ1kUm12ruVNWfw3DO/HdQvc3NPt/Riie8yzWHlYHqP4OU8=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX10' LIMIT 1), '引出门1', '9CBE40F24C666A55E229C2A0D8DB8CDC',
  'region', 4,
  1, 'NkzQOyfuZXxrnbYEnMDsxghKCCUmvJ0Sq9krWDhUkZLOaylXa2THxN5cToyGnU5NWWw=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.01' LIMIT 1), '自然进风口1', '9D41411F41FD2E08AAC72090EA6FF85F',
  'region', 8,
  1, 'p/1Az+g9GR6xr1+9m4zJg0r3K5sGQ8HguQvoVRZQSXRvu16b+3ZJGGtHPev8T8OI9LLNbaYgRz4=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.18' LIMIT 1), '集水坑2', '9D6775D14B323D54468C5DB793A84DCA',
  'region', 1005,
  1, 'ZIZxSuzXWPBiNWLRf85PdPhbInc94QCy6ywBJ6dlpgww7UmsbfdsmGviTeANgq7mE6Y=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.20' LIMIT 1), '吊装口2', '9D7E977C4F7F0340C144E9ADCC4A3985',
  'region', 1004,
  1, 'viLMsMwz5l+5OPfuPk4PdIJtwaIZ0HDB/DfR5ilTgGeVXoxJavYtp/7fU4zafrBa7BA=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.09' LIMIT 1), '集水坑1', '9DD2E9B447BD7B6B468D37BFFC86981C',
  'region', 1004,
  1, 'bDIA1+Od+xlaz46+XQAOamceEdXTOdADfB5q76Fnooe5M8MIjgmnrKmx8Q5XfItE654=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX12' LIMIT 1), '交叉口1', '9DF6E08541F778A1A3B824AFBEA3AEA3',
  'region', 1004,
  1, 'd2yUGeexZhbSLbcTbDIhFnLySexf5Qr1R8oUBqykscVFa/rtdhYasmBHBx9uXBq7c1s=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-1b468a9c7b8e44d7ad664dc096fff9e5' LIMIT 1), '吊装口1', '9EC01AE44CA8B8765DD78B84BABB45DD',
  'region', 1005,
  1, 'FCHfVEZrCWdQNr3p2YmazvKE/GkBJY9Js6G9agAsBOA5GZ8eNZcSBYjbeCctEaGM8+s=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.12' LIMIT 1), '集水坑3', '9EF9503543F50B4DD8A3F298A11BEB15',
  'region', 4,
  1, 'YMkrE7/tsOKEFu7wUY0CXOAnlS+HRTXgVGSoHcmJssIQOkO5ozhuvHNIw79ZUv9yiss=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS03' LIMIT 1), '机械排风口2', '9F1937F0481238E7479EFF8AB0A1BF7C',
  'region', 11,
  1, 'ob/iKeaILap6WS1uyLifSgdJx4DJO6gLETTuAyIcy6qa3nA3n5dc831X0ta7u4mRWUru+1uTRBg=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS15' LIMIT 1), '集水坑2', '9FA0FC1941F0DF2C054A10B8D86EA17D',
  'region', 4,
  1, 'p7nUSxkP4s9DojJzmDzGcc15IjGCOE+oQCsvQJsR+YiVC1B2q2rFL3RRWsU6kWA3nG4=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.06' LIMIT 1), '集水坑1', 'A050FAC443E8195D9ECFACBF58D3A4BC',
  'region', 1004,
  1, 'tdfcwYXWCcUUzxN4/3BW9RsA1IRW8mPgU0slI5bHekviWgZbW+2S4LwbTZCBN/lXqmE=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.HSDJ.04' LIMIT 1), '引出门1', 'A0EDA7F040324D600B1F63BC16416C42',
  'region', 1003,
  1, '37FlH6+zK9iAR7GP/7Cju9+mrlHFrEjjYR9RZBvR1pNYrViup772A6yINaj0Hwy5bfg=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS02' LIMIT 1), '自然进风口1', 'A11BA3434E3C188C7C437A8E5737E073',
  'region', 4,
  1, 'H7GG2woiAim23SeK5UbncXOp7aOGkZgaUAc7krOL5eBJOpq90idBpaZzd6ikUkQFD+K9FfA8TEc=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX01' LIMIT 1), '自然进风口2', 'A2D2377247CEDD8C33ED3E973FF67738',
  'region', 6,
  1, 'zogrfj/9v6td+8yfXRPrxzGrK+uY10mz9NPwP0N8DqvdJ9AFWQ6kNF576kE9dF/xt7vPCTApeHs=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS10' LIMIT 1), '集水坑3', 'A2DF474A4B1E8B61F0547EAF13E3B341',
  'region', 7,
  1, 'jak94Jm/HTYADnEXZibq1niAB2gY2nzXpnihS7ZoSHoLTmbnsav/PNSvTZSrypdK+mM=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS13' LIMIT 1), '集水坑3', 'A2EE3DC94108A4F16B5FF79CD9890514',
  'region', 4,
  1, 'GF2VpjrgwT9WS/LFY3L641DF5IAlo30lflO1r0ab3lKLHeEf9JjGALqjFjIQqH6PEq4=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.15' LIMIT 1), '集水坑1', 'A37D236A4C6AF4E72A4B0E8A7E0D9EF0',
  'region', 1004,
  1, 'XloDZlnAEY/B0xjwn3GwWYygX22G3S19/DUXNlmpBxylw6eqw4vzVtkR+fFu4/cizHs=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.HSDJ.04' LIMIT 1), '机械排风口1', 'A3AB814242B8EBFC627259B04AE9E572',
  'region', 3,
  1, 'rg2hs4fGo4kUUMPl/2BsSLEoJhKoGimZialjaLgDDo2sGC2Zq4Xfee1uGxsiI+vxMeVkGxvXVds=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS07' LIMIT 1), '集水坑3', 'A4CA8578487CB3557870889B413142A2',
  'region', 5,
  1, '5Hw/eSH/nIfjoqz/ZkY8xD2ExlAcJNGMn5lk2dvWc0+0DsJoRho9tROnw3Qbwe9upI8=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD5L.SW04' LIMIT 1), '集水坑2', 'A59555334D76F55E52D0D68E2F3C07CF',
  'region', 7,
  1, '1HWcB4E/UOjqLRHtA4xX8q0FujqqK2tC92EsogcoA25qLWDXLDNQcn0T4UHfXUx4OvY=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD5L.SW04' LIMIT 1), '引出门2', 'A5B9DB414A59484B5D2A119F68E018F5',
  'region', 2,
  1, 'cjbSnt15jAS2/7xxx48Tr0PpTF5gLdSqi9ryp2t+W4Np7QIIahbyk5WANAlbS44ssqQ=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX17' LIMIT 1), '自然进风口3', 'A5C5F6F14C190D13A712048C730096D3',
  'region', 8,
  1, 'avjCbBOvW+SHHxMaE+ipMSY7tdzVjCcGPe4b8bzy/ZUr25URN1QmfriFZX0FXpthaKZdi39k/Og=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX14' LIMIT 1), '自然进风口1', 'A6AB58A343359DF13DBD6985B88DF8FD',
  'region', 1002,
  1, '7txCRb848CSyNRDop/re9UHn0lYHZFJe/gPyl+obRZ+L557DTtn4WXLYG9EIm53NLBUrxSJPMoo=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD5L.SW01' LIMIT 1), '引出门2', 'A6D46FD7425911506E0F1DB73B36D9F2',
  'region', 7,
  1, 'wCVoPlSsP8aBa9TTXdIJpkiz1EWHUGa+ZUDOeqswWmSdZN7+li4ytVdR+VkSaPxQt20=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS16' LIMIT 1), '机械排风口1', 'A6EE35C14B3F6EA4332CA89817CFEDE7',
  'region', 1005,
  1, '4gS9e+Nf0GBXv/cAHa4wLHvCycmzAzDmfZvvzEC2QauCMi1UvlyzikiAHx+rZoA5OFNh38N1Xk4=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS03' LIMIT 1), '自然进风口3', 'A71C42FF4B3D0379A6B3D3B49014BB7A',
  'region', 12,
  1, '9ZHrsrhfTfYyP0XU6CWdQkBe3cLPrY8DC7gX+hsecdDtVpVJNQXg1qCnYue6owzb6bSbVyU8LDQ=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX11' LIMIT 1), '自然进风口1', 'A749EDEC42A0574F40F416A02EF52200',
  'region', 1005,
  1, 'PfE7NvLjQ3LbCmf0Yl3NN7cuqlidfUvds0MwBPcZYvvlsjEo3Wp5SthLuv9qnnUr+Zzq8A7MI9k=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS13' LIMIT 1), '集水坑1', 'A79816A1473D7E34FC26D8A03EA34C5B',
  'region', 1004,
  1, 'UnFQj8PtrjIOe61uILDJssEUJXe45639jj62h9+hosR5qdds89pMZbX9LjYxFlfJtE4=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.21' LIMIT 1), '集水坑3', 'A7C843E344E22BC568B992B5C7CF1B61',
  'region', 5,
  1, '7tWr4hXIgaa4BRxf5rpbIYk3RtctOgNu1M2fo7pNwHjEvR1YHxX/7fisDKIrMDfuroE=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS14' LIMIT 1), '集水坑2', 'A83F2D1248161A742C2EF8B534FE48F2',
  'region', 1005,
  1, 'wpESCSixcJ4+Z+JX9xZuqYmMa8GGIwmkNk0ufZCYkI/0eIFlu6MJnaj9egaSNeuFXuY=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.HSDJ.07' LIMIT 1), '自然进风口1', 'A8ED40C8499B52058687F794F8908798',
  'region', 1003,
  1, 'TlRECuTlAxGUlGLGgtdT40mUnlvwyTF9SWex2lzfpnw5spVm2MZiQl1M14+cgWA9NJyMKhxpPd0=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.16' LIMIT 1), '吊装口1', 'A9418D4E4F3C606785C54BA8CBDF1797',
  'region', 1003,
  1, 'L77iqlYsJ8qV8HKX+VtYI+r6m3H/RmOX8TKNgUykun/jZqweBEBFpicqjhYlGjPO1Sc=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS07' LIMIT 1), '集水坑1', 'AADCDC9142620320CA4605B963D4DD7D',
  'region', 1003,
  1, 'DlhYeMxjQXuVFtc1FDMUGQj8rmhuHMAHD3psog+yCPQLFyPnugxo2O2kRooaxw+efRI=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS11' LIMIT 1), '自然进风口1', 'AB6D5DE3455AA62221C87F830CEFD56F',
  'region', 6,
  1, 'sB7ggrkgGq7hesvamvGhSG1aTb6bHW2DLxb6pP5NmwlMEpmVWvSRQV6E/kb02XREAPE9tNtnmbQ=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS12' LIMIT 1), '自然进风口1', 'ABB568454D1506976C0A1A9DF69888D2',
  'region', 7,
  1, 'm4zZwOoM0OSCtQYBRd+DyNlFBXfnN7TLT02Sjdf4uFbdgHFVg3tcw8yJTJ84Wi7GtPtgQHqWoLc=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS11' LIMIT 1), '吊装口1', 'AC09C32242903D5F1B71959A1F4F46BD',
  'region', 7,
  1, 'Ybh6+bJbEPqd9qHX4WtYCOtFnOcaIzvoeE4nSe/BlX1zaCQdVP8elwwRQY/1S/lNPP0=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS17' LIMIT 1), '自然进风口1', 'AD606FB04F2BAB4F2A905D80EDF9045A',
  'region', 1004,
  1, 'efpr1ULBGbSB0zeLk3a1jIy1WMU5Mh4rY6/efxrJjL5z7snkJOGrZxl1TzY00cFgdNiBVYYXsDM=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX01' LIMIT 1), '机械排风口1', 'AD9214DA450F9330DC1156800D8A603F',
  'region', 4,
  1, '/px3yi5qLLbN0X0tFvW1uDUr+G9b/U/A/u5j8yxhZy6ERthjAihDcQiQnG6jt83aoRpVMSVKlno=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS15' LIMIT 1), '自然进风口1', 'ADF954904A9A3D2656C054823390455C',
  'region', 7,
  1, 'BFiIaZ6lO1yE1oLfirAKTf2FSOGwzyRBaS5QkBin3A/uBbWlEhiTTE4kkxIebs0VWDGekh2ty+4=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS01' LIMIT 1), '自然进风口1', 'AE034B5841DDFE506B16BB9CF9398712',
  'region', 5,
  1, '5e9ytIRaNq5gJYuDgPsaHgNUEWXDG7dzHdOHI6Nh41g3rOIqpB6vwRWI6LxZpIY2H37ngTnY1kM=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX18' LIMIT 1), '倒虹1', 'AE6FD7B14CADE3297ACC59BE81489528',
  'region', 1003,
  1, 'OhNA77b/vIiDUaozzua1WYr9uzChQBGBAuGDZqhjx61oXmZDvz/aBXh213W43lI=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.04' LIMIT 1), '集水坑6', 'AECE4BFB4384B23FE86AE48D9BCA70C0',
  'region', 6,
  1, '9vkkyn4m9l/U4z5YWo+3Xw/2hr+RMOQDv/XSaRmacXu5EN8Drdy+iR+geOY3qHFfsNk=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS15' LIMIT 1), '引出门1', 'AED9347142358ED0A9F5E58CEEDBF68C',
  'region', 1003,
  1, 'P9QWHBqmy+6fdi9fQ5yllwo6Kd6H2bV/YzWz4LHlL7cf5q9Tu0bOcGkWHPEsfPSw/ZY=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.22' LIMIT 1), '吊装口1', 'AF723AF947C91BFC6D96BFBE9F336828',
  'region', 1003,
  1, '6YBgAlgwH2XhindCSNUcURL9RDSO4hayIyvp/7xj0fb6eoRybcmrj3nKaSZ0rn29bcs=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS22' LIMIT 1), '机械通风口1', 'AF85F46A4C51C0CA9C5A53BC0A82BC0D',
  'region', 1005,
  1, 'FFbiT0n3I2mQVqaMXgTDZzuS2iG6uE91Z+ioJzZz3HFS/y/Y74HMHOwIHLVnXNgxean4ZI0ocgE=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX13' LIMIT 1), '人员出入口1', 'B14DADC34E24671D7F652BA48CC2AF9B',
  'region', 1002,
  1, 'O5Zda3hnmVdCevDpNCnz7nBQXr2gddDKYeaSOTDcj0qVb04KC9yaWbn8yMJMtsZ1Vg89vuJNFLQ=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX03' LIMIT 1), '自然进风口1', 'B1A37D004A6E712B2AF1E08DF67102AA',
  'region', 1001,
  1, 'lu9+x5TMxSDaJvv4W6fBRgJXF65I4aiX02nfb3I6Ex3F076pfj0RTv4JNStkyUKaYYgeo1KFn2M=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS01' LIMIT 1), '集水坑1', 'B1FE3EDD452A7ED248090E9D3E3AC3C0',
  'region', 1004,
  1, 'IDGMW/F6oAQfIGW2Luyvcr/5cMS47RkU35oKMiYgb0n4ocnOfTOp0uyy2HQsTLTeGlk=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.04' LIMIT 1), '集水坑3', 'B23CE839459828977EF207A3C8EA2913',
  'region', 1005,
  1, 'rRQ2DbnEAqapGFjc2i8jBrcT/fMg0PE7TfASDzlIGtOg8IK07zF/xNBYlql7VyL+oFI=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.HSDJ.10' LIMIT 1), '机械排风口1', 'B37F95684696B19DEB89FE80E393E29E',
  'region', 3,
  1, 'iZDsSWxDzIBtN2TFhju09dxgsOg3umoDwgqj8TcUqT9KH3c3ZtHBTi+QiQlRvNB86eXnRnqKFak=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS02' LIMIT 1), '引出门2', 'B391782C4E78AF048092038ED5EF158B',
  'region', 5,
  1, '/CkeDLKG0+CBs0cN8Cwht663JorJvhUUJxOkR7GlwiP9J+fzuIyE8kjSLCMCQXAMT5A=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX05' LIMIT 1), '自然进风口1', 'B3E7AD034E1DE28952CD02B5E1DF8B23',
  'region', 1002,
  1, 'QLZMZ4kNOPaODh02wmsVs2o5keH0E1JngtKeGDJQjzWHXs6LXWDdZUSYOWOSUMTd1SIr3nL9PRM=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX10' LIMIT 1), '集水坑4', 'B436FC494BF5DC6E533F1AB2019EDA47',
  'region', 8,
  1, 'vP7kKHcpQ00Azv9ZkFitUjSyYJUVL4gzVnJgQBd31Z4tRr27tydmKP5YH75PGAaMXvI=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS21' LIMIT 1), '引出门1', 'B46A5B1C479DC8F4D903BFB77EF277A7',
  'region', 1003,
  1, 'zbzlB3w+luAnqh59Ihdnz1s8qJ0tsORLFwSVV1PbxI68Fawek1/IILnIMuhxFUROCHQ=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD5L.SW01' LIMIT 1), '集水坑3', 'B47F78574A4FE845151817AA38DF183E',
  'region', 4,
  1, 'eFPWijujIHevXXL6BOjHGQ69HGSIF/cEF9Gr7lnprD1+tpFCjIzxTwEt8nzQ2qI/P88=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-1b468a9c7b8e44d7ad664dc096fff9e5' LIMIT 1), '集水坑6', 'B48F466C4A3D1E6BE1E68F89DE7C1672',
  'region', 9,
  1, 'E0Ms8C3utZCczNV3MyB9Qp+LLqbLr2+MLGMwHfh74a8PeyVehN4MD0Mt98/23axxSf8=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.01' LIMIT 1), '集水坑1', 'B593DEA04D95B2009F8120BD3EEF4971',
  'region', 1004,
  1, 'y5E+8886AcG2xw9fxizfoU1fAwNd0xy23hZWkXCi3Nfy0MOXdaxj2tg8lN+h/A80t8o=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.HSDJ.09' LIMIT 1), '交叉口1', 'B66A02744F6E919FABE049974B36957D',
  'region', 1002,
  1, 'jOB7PaEmkHVbctopz3uV3XMBqJO+WnU0En46dEzluJjsJmk8UreTduPZkRQ69QEzEK8=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.06' LIMIT 1), '集水坑2', 'B7BE09FA4AF8DEC081E5258F65F65E0F',
  'region', 1005,
  1, '3eFYNsE1BSkgrt+6iJG2tLwKzfScTTfRaqrGMHIZy/EAuAxCFU5xtzaSjrMT26gPn1A=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS18' LIMIT 1), '引出门1', 'B8FE5018457A03280D3CDD9B1CAA5823',
  'region', 1003,
  1, 'qNM7xwRRa1P7R8ucrz5IL2Plvz7S8PVrWmsuk+69bXQVDb/XYuqrelAD556sJoMjlbk=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.05' LIMIT 1), '集水坑2', 'B90266E84B18B75748422AA08E727914',
  'region', 1005,
  1, 'oVcAKL4A6v7UnhXCTO1kX8GVtqnN5YHbvlw7RLwpOZQIoSGRcxpK3L25XPfF7mO04y8=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS04' LIMIT 1), '吊装口1', 'B90BC5184607CD291AC040971EB8D665',
  'region', 4,
  1, 'TvRRFJCkwj1i6BdAZr6AZzai135tykuVJOLOSqKbiH4vz++s289XaqVkUFAPxKcXr2E=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD5L.SW07' LIMIT 1), '自然进风口1', 'B984CE75455D4624AB1282853FF68C45',
  'region', 1001,
  1, 'uQzRxnNaITdpsWztX/y8zRgZaUWOUz6WL7ZS6HqtaJUxUjGraxDRS6/ImMtRaYwTgvJMLh/bFsA=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS12' LIMIT 1), '机械排风口1', 'B9F2D59044F43E62331C76B1D27F4878',
  'region', 8,
  1, 'LmT37Qoq5uKoQL0qYiz23VzFtgvtvjQMVDh979teApbqWpwZpYhaCASZoitfKmrDEWyALwDBj6U=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS08' LIMIT 1), '集水坑2', 'BA79284B4E531A58B44A0B9BE2CEBCD2',
  'region', 5,
  1, 'ujzjLk+9ONFfryLsKO7v5NFOqhS7DxeW75PsZZ4l1YKnmtjieXv2xsCUE1yxjzBSwFk=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX15' LIMIT 1), '引出门1', 'BB0DDCA64F449FFDD6B053AC4620485D',
  'region', 1003,
  1, 'vY7ydMNx4AgsL2qEQc6XgbuPIuTtODOXYdfX+xxGX7PsXyMTn0adHh6gf4edqe42K54=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX11' LIMIT 1), '机械排风口1', 'BBFF9BE34AEBF739F36F0FB89C7045C6',
  'region', 5,
  1, 'VVf4TcONx/7+rJtoUNxLi5Wt/UP9J+y7ZZoxEK+zIOtiIMgkEPmh7/A9lWr979cGWoPkLij9w7U=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS02' LIMIT 1), '引出门1', 'BC6E3B384E8408DCF4AC71BE23A12C32',
  'region', 1003,
  1, 'C3Ja42q38N2pm9AAeytN3JIpsvxEYFi5QuBFriY3hSkwdvB3jGhlKgBn8OzfJyKj+HU=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.19' LIMIT 1), '集水坑1', 'BCD641CA44377DB1CF5BBFB8928E0089',
  'region', 1005,
  1, 'nyKA2YX6uQAtPsdhq4FVHR9b0NESDevGou520h7CBHy0nGh5kHhGNhMQarZkdrOa+S0=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS08' LIMIT 1), '吊装口1', 'BCEACFB44894FCE8CAAB8380A889CA86',
  'region', 1005,
  1, 'M7CpiL9fmzCSvJA2qMSxdrlW1TQni08D24r5ss+ehB1kVhQWrZZ1oAEoKaUasdpG67k=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS03' LIMIT 1), '集水坑1', 'BD36C7614D80C2F1135C78893ED4C565',
  'region', 3,
  1, 'lZDuvwGaHhL4MkYYxCNqhcwLrYacMjwLTrLuS2k27CWbROLlOaqaolFiksVdyPBkd20=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.HSDJ.13' LIMIT 1), '机械排风口1', 'BD4B4EAA407D82A13620D3BE739DBB3E',
  'region', 3,
  1, '1V6vOV5Pg9RY3dZbNLLtA509RmEydPjWNQ95okc4jQa0BFKlX0fPmh3J4c8TNyX/43GKU7w8Vos=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.02' LIMIT 1), '集水坑1', 'BDA0DFA14854886197B149B777D28C16',
  'region', 7,
  1, '+7B7BO9I2lN6Gvw6QiMuawCFQrwIfwrGrr8u/yPSIjP7v/0wdcZJs3tTnVMwNpw5FIQ=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.12' LIMIT 1), '集水坑2', 'BDBEE73E45931C611E4F168C5B6940EF',
  'region', 1005,
  1, 'i8hscwXOTx78qxZZKLG5GbFBGfoVzUerm+jsDtWKsfoB8v7XLQ7jS6uRu48ceX84f5U=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX02' LIMIT 1), '机械排风口1', 'BE597E0043AD25E3ADCB84B2087B8A06',
  'region', 4,
  1, 'IfX5DAcT5D7/lAeLPhYOmVP+G+UiPnwPSeV+WvmGivJwHHUDXhQLRqIANNULgQQP3m660BexqgU=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS21' LIMIT 1), '机械排风口1', 'BE93422B44A383518DA5BABAAB5BE8EC',
  'region', 5,
  1, 'J8w9THLNoREEVT/pfUVAtUAdEPNi0vP81EWkGSuRn58u9sBVKi11mpUrpVL8sgLOYwjXHmn7YME=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLB.GX04' LIMIT 1), '引出门1', 'BF1842204DB321889F81D387AFE5BB03',
  'region', 1003,
  1, 'lzJ9OpNB+gQoFTOfeJZPruM4BIUVFnotDvIw2D9QBiZZ38CQXZ3x8x+N23fC1yFIPi4=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.HSDJ.05' LIMIT 1), '自然进风口2', 'C0106B0C44483A55CE9477AACC362893',
  'region', 3,
  1, '/B8SR+0G8fAOzuUpqlmV1FLTF5yKBix7mlDsJAFe6xneaZXHvilLSXSQQjPWJKJyTJFIIwH7edg=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS19' LIMIT 1), '倒虹1', 'C1176957468DAD46098AF28F7BAE33B4',
  'region', 1005,
  1, '4SP3fH+jC30suMyIVrcaJenl1JllT4hdgKJZsd+qp/tdq9lEIG5UdUDJG9I3en0=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS07' LIMIT 1), '集水坑1', 'C1F7AC464C16DEBE9278948D516B769B',
  'region', 3,
  1, 'SDIoho3PJg+lpDJh9dO1dLx+SjVwfh04ixgRG8Aty4iExgQxoqcjuf+ZWLK6u/s+qTE=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS05' LIMIT 1), '自然进风口1', 'C2B3BE8A46662BCFDC548EB4244B9CBC',
  'region', 4,
  1, 'x8h6GNgxcRXfHzifNqXRrJBFoQZpIYXjb8uyx9Zri8n35TyDjrLW+UuMZwy3JBa6u/R99rdMfJ4=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.12' LIMIT 1), '集水坑4', 'C3D960F140B9FAC7B98D96BA5382DB53',
  'region', 5,
  1, 'IrHp6JAKaGFraTSja/8jO57hwaaJKc65IoHuacSAJpLDmkHc7pb/9WAGYxgc+4M+qvE=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX15' LIMIT 1), '机械排风口1', 'C406AFC848BEC8E6B8CA48BDF532037E',
  'region', 1004,
  1, 'DJwQCHbsSbQc7FaZIW+zyimkbvZyJq1WaZEH4Un+7onPg+AB7woyFVM6vn7oOL+RujMJMaB1F2U=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS23' LIMIT 1), '倒虹1', 'C47733B941526C2E59F1F0B4D35ED470',
  'region', 1004,
  1, 'q5VmM5P2qbWSDzb7Um1CWa0y++uIxG/+aHEAnHS8AMS9QmWChtPy9OOZ/m5jMXw=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.HSDJ.08' LIMIT 1), '人员出入口1', 'C5FDC6824A2D3843BE6FF6AD7D980765',
  'region', 1003,
  1, '6RxdOtybqR/nR7QEsZAsydNGXWerXU1lF9tEsIpdsO26RezdyYFgEltBJjbx1WmvlHOwtjh9mGg=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.22' LIMIT 1), '集水坑3', 'C627043C4277B6988B803A9C1E2A393F',
  'region', 4,
  1, 'LuQ6uf+9NT8ML/OsIUX4ERb94Awsgx9uC8UO5ExpdoEWklt8EKH4PZjRb7f29W6zR30=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX10' LIMIT 1), '自然进风口2', 'C6B9FA7F46E3952BA78A758A3A9BF4A6',
  'region', 6,
  1, '/+llvhsCgbEfN561KiVy63aKWMleCXsdirzf8eEMIFqHpe2JzLjiSYt4RqUDRQFwhx1hTufzuPM=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLN.GX07' LIMIT 1), '吊装口1', 'C6C4B66647BE754ED96C70BCD87D6814',
  'region', 3,
  1, 'agkXv0TNoGtNKzGVzhgkKoCQjD3zBP3qWju7M8nI1rH+KVihgtPiFjRUeuXmF3JKnoQ=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLN.GX05' LIMIT 1), '吊装口1', 'C6E7E69D4CB63F044D9D97AF19DF1275',
  'region', 1003,
  1, 'anmx/4b8WhH33hAA8QOjoMEHPpy7NymcLuUOBNAJyMKOkuwCgfVHIuWK1/6PChA8w8U=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS03' LIMIT 1), '自然进风口2', 'C6FB98404B5BB1BFDA11B08E0E1D91EB',
  'region', 10,
  1, 'Elvbcn+OpBBGGqGzfW8Cllpie6TBvD//qyirx5IgAIW4retb7JOlVxxADTrUfp+8Y25Id+ui9Vg=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD5L.SW04' LIMIT 1), '自然进风口3', 'C8647FB242B47DCA4C3B54AB3AD34255',
  'region', 5,
  1, '7gPiwjm3Coak9p4MNeUDuITDoJmJKjK3KQLPFbn9/mybiUsn3UBb14Iy9p/jm7pb6k5LUmgvx6M=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS05' LIMIT 1), '自然进风口1', 'C8AA973F432C71F152765EB4C091FAF7',
  'region', 6,
  1, 'jGFplixvd8zPsMDW3lSyDcsfLENxRvSYNmxLM66YXyc8Zr8kqJusqneXqCiBZaKh7Ke+1rRsLoQ=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS08' LIMIT 1), '集水坑3', 'C8C4F9AE484293DD551B5BAC0F6FF3D5',
  'region', 6,
  1, 'Rajg+hwLbO7s07uTg8XE4+1IEE4a4ZMcbCOHc+iJNZ7Q3xk2n1gk2m+1Ft2gd1uAYfY=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLB.GX02' LIMIT 1), '机械排风口1', 'C8CCE2C94BC120A2DB145AA8C07F8BE9',
  'region', 1005,
  1, 'gdTK4r3UtSVt1FBnc5AqZL+TKkmSZQQBmSDvsrWerAvn1Pzqds9BUOaG8R9aZSkjQRSBNuuPJ+E=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.HSDJ.13' LIMIT 1), '自然进风口1', 'C91E9F69480AD64221E20CB84BF9DABB',
  'region', 1003,
  1, 'vx812vApWElJT09lwpZhZOf7S7lNP+f6lwdub+NYUJEi7qVr4ArqU3hfNbZyBfUOvyFo4AwuD8M=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX01' LIMIT 1), '集水坑2', 'CA003E854096BBF808566ABDD71EECE7',
  'region', 8,
  1, 'vxliLrkvDHHFHwdvtY9vRWtAF57WNdK6OKbnhAkO5fVWzpeS9pyQu/Un2Bd4mFp51YU=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS02' LIMIT 1), '集水坑1', 'CA4A50604A26AB2A2F21939D43A66A9D',
  'region', 1003,
  1, 'HHbamcKAXkrIZIQPmiCLDqTJEN4YWGLD82YAnz+kv/+8uwVj0KZszKFUnvh5QwS6maw=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD5L.SW03' LIMIT 1), '自然进风口2', 'CA80989C4BD81AE6E216EEAC5396768B',
  'region', 4,
  1, 'imd08tqlDKPCAG1/gipGKjLrLF/neVMUXh4GojZLUv4VJEK1UP1uZbNDZIdGwkPseOOP4KyFQiY=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LN.06' LIMIT 1), '高压电力舱', 'CAT-00a06f26cf774dbebf3b1325105e2049',
  'region', 2,
  1, 's4/tZlOXk2luNAOHLRzmegVNFyrRcK1bl8pPG4XEJKQdRAT17gho23CgaWA4wQBNHEkVQA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.06' LIMIT 1), '电力信息舱', 'CAT-0286927face5415cae5b767a5075144e',
  'region', 1,
  1, 'ISn9zOV3reKL0dEAtL7B3NnCgV2SCcsUa0+h/fNxOBbpji/6t8UvtOmxN+VlndRwY2xOrw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.08' LIMIT 1), '电力信息舱', 'CAT-03de2ab0edde48af8f8b23abca7a1a74',
  'region', 1,
  1, '9pOB1aTserDD5v4s4ElZl27kNRMYCOUzaOcBaQDIOaofA0rNKcvxs//avI8OnQXg5kkTEg==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.16' LIMIT 1), '高压电力舱', 'CAT-0485d8c620b4422cbf3d7ab0e8e72ef6',
  'region', 3,
  1, 'FV4UuJiZh61mhK3bBuBXuJDSnvLKOHoLIGH97SsnhrIIH52FPdzRFshmzLy2KsntvZ97WQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX09' LIMIT 1), '高压电力舱', 'CAT-06287c0f841a4bd5a1f46ad75f3439e9',
  'region', 3,
  1, 'C9xux/GjkTPJhXCiwqUMSeqaL2qAHO2pB9dRqArL+PqxZcevGhHIIJlOHwIJyC4W+7aD0g==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS12' LIMIT 1), '电力信息舱', 'CAT-063584e059764895b647b905af7c8336',
  'region', 1,
  1, 'WaE54boPlTgEJrIdJ00ySMEfuSi4nvaEZ1INWcNBxpdO1EdwbnfuYR0IEVYrLPClgphQRg==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.13' LIMIT 1), '电力信息舱', 'CAT-065db100818e4f8087641c622de0fe98',
  'region', 1,
  1, 'gW0Rd76ASkqyj9RwIN/9DAttb6eEXsrHGfXmEgOmTTcCxHXxpj0HNVnlpnGS2hjSROBIbw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.10' LIMIT 1), '高压电力舱', 'CAT-07d01274761e4e11b69113fb015ff244',
  'region', 3,
  1, 'PLhZNLnnGuP3R6EpkZmWYPOS7Wvr/Cg00iVO4z2WrBs/BJgA7i4cQR4oTReH/FZZIgYlaA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.HSDJ.02' LIMIT 1), '管道舱', 'CAT-09ebeb487cd84e8bad36403db9d2d5d1',
  'region', 2,
  1, '5EFJKKjOd/v6A2nMBfp8eA4lkedQdnK/8934nikerjUv1lgnknvPIzA4wHWDNQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX15' LIMIT 1), '高压电力舱', 'CAT-0abe0d0bb3484ea2a453d90e86dee66d',
  'region', 3,
  1, '5dWa9gvf3dMdvlgfdnXHnLtfmqc0Sjv0Whoo9FS3n6azd9G7W1sZbedRUNcjlMCzlusULg==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.02' LIMIT 1), '管道舱', 'CAT-0bc59e07fc2a40f9aa7f3e09958c408e',
  'region', 2,
  1, 'ERvrNyFK4RfLara4JyiqCqw6hVIfWPYEHMoEfkiSMcdE5iGxgb16ZrygLts7BQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLN.GX04' LIMIT 1), '管道舱', 'CAT-0dced7aeb053447bbfd29b971621fe08',
  'region', 2,
  1, 'glASeBbk5PDJhVUoKhFhUkSlPDKd2CwIYXMtdL5+n0esTkJ6ljWzsnGtPlcrng==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD5L.SW04' LIMIT 1), '管道舱', 'CAT-0df7a266912049889ae6d45fe2af8146',
  'region', 1,
  1, '6WhEw4CjK+nngiNQhh+nKD8Hld2T85RfiJUSLg8jrvuTdMgEOsOVcI8NrffdDw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX13' LIMIT 1), '管道舱', 'CAT-0e01b8a6e3754be3bbf80ae36456e070',
  'region', 2,
  1, 'r8v/ZaGR+BK6Gp0cUFzb3lfI9ych6gXOFVf4M9t2bqJS/i2sRDokcqAj6MW0qQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.02' LIMIT 1), '3#防火区', 'CAT-0e4c92beb83c409e8b8bede15e3d4bf2',
  'region', 9,
  1, 'YoIiWcxUr+5zVk+33kkztY7GV4bOvHVU7nj3q4/G7l03yNGN1asYVC9+BDnJIOHFgkpb', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD5L.SW03' LIMIT 1), '管道舱', 'CAT-115c167855254d6eac909eea4b89b9b9',
  'region', 1,
  1, 'E4JLXUtYva4znvqkdcJYjqVikXhPKvLJHAVje2oFeCwESvAHNkIlastbibsgYw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS04' LIMIT 1), '高压电力舱', 'CAT-13176a19c93341d1bd8e4633df2f317a',
  'region', 3,
  1, 'hvQHIVh89ZhWxWUdkaZZAz1tI/9MCsYi5oreP57Z9S0Q3zCJbE80Grn52WRZEZcWq85Zmg==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLN.GX06' LIMIT 1), '管道舱', 'CAT-1349391f775047729908da35a8446046',
  'region', 2,
  1, 'JPGmwJcImiNgZt2mH3gjP8kd6cLJ0O43PPgD7jzXyJSr8p/Z1Ni8kE/7WFzwPA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.HSDJ.10' LIMIT 1), '电力信息舱', 'CAT-152a75b58cc44c0e9e614afcf3d4a5b8',
  'region', 1,
  1, '6TE+leJxtUoOtA2myLKovvMS6yQZOtkquzrJeJKOh4Zpijbpif/5LO2wBKQX/w6DcQ00sA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LN.02' LIMIT 1), '高压电力舱', 'CAT-16319fecf247452694a49f628e59723e',
  'region', 2,
  1, 'sXKwo+h+l6RhT8zDYkm5qv3d5eHESsZwGbhj1dgPyxb2/Mf1R2Pt0ap6piaczX5NF/hrgA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.HSDJ.07' LIMIT 1), '电力信息舱', 'CAT-1697fbbeccb0499ca6dfa218e40d11c0',
  'region', 1,
  1, '17yh5MW9HIVqzc9SNy6+XfFN4JCIKdzETI++pU/aAS6Ez+i0rOMPtTs+s4q4oKLMpfcuAA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS02' LIMIT 1), '电力信息舱', 'CAT-16dddc4524384c7abd86a3c9d6b74d35',
  'region', 1,
  1, 'VyyGDBHq+01swU2eyAAgN2rLQDwNiDS0EzkuP+vzL+YzdrxTjyIXZZ9mfpeEmQBDL4TaYQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.TYL.05' LIMIT 1), '管道舱', 'CAT-17cfca49e6414fa1bba6c19188ec7f08',
  'region', 2,
  1, 'xpAVhhNcEA7DoybE6fKeHqvtw878RfgKoGoazFAPpmhxrAYH7pDZtSFAcHH7qw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS10' LIMIT 1), '管道舱', 'CAT-190449fed5d149c1bff311045afd2562',
  'region', 2,
  1, 'HHZmKG90xj/UAMh8ee0HpnBA9NA/+IxiUOVL625zklamDvCp7IIUNB8lkJmtsw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS03' LIMIT 1), '管道舱', 'CAT-1a2aa2e2b73248b6a64a734579c62cb8',
  'region', 2,
  1, '0QUtr6sy1sL0hBboXdGBG520J5FCXiVNwlt1/sjjISh/h+bZTrMXy+Ts+k3QQQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.21' LIMIT 1), '管道舱', 'CAT-1b469c3156e0438884b7719286a1ed7f',
  'region', 2,
  1, '3INzBAVh++5j3uyyenpersixVHF0Unnmmn8TnU9hNs0UZdBwO5Q+/I7bzhovzA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLB.GX08' LIMIT 1), '管道舱', 'CAT-1cf31b2fc5024f05ae2ffd7f212a2f50',
  'region', 2,
  1, 'aO4DYIuL8rZye76NXYE9HtNuaamdbVBvryxo5LLK4Ww/Mj5WuLk6GYmbhTcgHA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX07' LIMIT 1), '管道舱', 'CAT-1d87212c0710404cb7f07de643dddf55',
  'region', 2,
  1, '01wuAt+GBbKVY5WMUdOU1RrlyeJg+PE2uX3RzGqjpnItN6dQgSRXot32lnu+IQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS02' LIMIT 1), '高压电力舱', 'CAT-1e43546764e54868825abf0f7b848a20',
  'region', 3,
  1, 'HpRa/b2lOAhy8nsOgbdUJR7OHHI2BdppCIEM0qH2lvm/9zRfb7S69OSL6b3qutxO17oiPA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS09' LIMIT 1), '电力信息舱', 'CAT-1eba4924553640d2ba896851e2055be7',
  'region', 1,
  1, 'anc07DddsKEPOmphN1PU//aq2hitZMVxYM4fG/b/xUBCHV+nTai6GP6mVNmnnFz9L5Qflg==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLB.GX06' LIMIT 1), '电力信息舱', 'CAT-1ec664553a65447d871559e9859c98a4',
  'region', 1,
  1, '9p7aklZM4ZCLyKIKOLyiCFI1rLwhXuj6Q1HaHAKCyi85syuVg+Gf/Fw98c07hAIm7M5H9g==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.HSDJ.08' LIMIT 1), '管道舱', 'CAT-2188364e6a194efabc024598c05f07ad',
  'region', 2,
  1, 'aUPNdf1WiVNnqxaV/FmBVBI2ATyNa4VU0wP1198eGUWCpvQon65npzEpcIwURQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLN.GX08' LIMIT 1), '电力信息舱', 'CAT-21b33e63ac1548578eb8608ee2b77900',
  'region', 1,
  1, 'jqWQM3OoqFqAtYMXgYMIsRlCGSNmxefxCj4Q8n2Z5tGiw7FCoxK9sUjeLZNtzL7yKNUIzw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.14' LIMIT 1), '电力信息舱', 'CAT-22fc1c790e0a4860b4b8cea3e23ee924',
  'region', 1,
  1, '+HSaWSUMNdhOaACjKQ9ZCiZ0Cy/lu97koLqUSgEkhdw9SDL9N93ee2vwAnNm/qHSjIXp1Q==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLB.GX02' LIMIT 1), '电力信息舱', 'CAT-238ba49c9fa042669f1f8d19728f81e2',
  'region', 1,
  1, 'XOAIvBv4WBy3vSpWd7SsiHgJxbRoIsKRThseGbT0qPikoOmbOeHrWWmS+ZJXPtN4EonlGg==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLB.GX08' LIMIT 1), '高压电力舱', 'CAT-25d9cd714baf4bfa94ca8f2efd9497f0',
  'region', 3,
  1, '1FKqynBt5smlxg6TJrLmvHryI479+vX2LZw576VhAtPWTbiPbACdqkuPQKjAJuh7WUOl8g==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.HSDJ.10' LIMIT 1), '管道舱', 'CAT-2677e9dc030a4244aee9eeccad3fb9b9',
  'region', 2,
  1, 'Wd+fmLZ4AcjO8d+3Z7WiTin0rVi9Qmj9U4HQTpYrbuojSZ1d8Aj3Yak/31Ebyw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.HSDJ.05' LIMIT 1), '电力信息舱', 'CAT-290fd1bf1b824e789eab4136f9a97ee1',
  'region', 1,
  1, 'Tqkls2+jYuDz9cYlZ+lOVDa7ki/F+FBZAOGO17vpiMjuYEVKfaQLbnwhZGmC7F5bq0cbkQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS19' LIMIT 1), '电力信息舱', 'CAT-2a1dc0477dba4250b5b86be06759244e',
  'region', 1,
  1, '6vrEhD1AGnu7huMXwpg3WDWewKnCaobdku4RGKqStYTEkublF/WR5bglkUtBSI0hOUvaBw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX12' LIMIT 1), '管道舱', 'CAT-2a77110fd5364722b10b6e4d8944e104',
  'region', 2,
  1, '+0aAJ0yFGMLP2AqsQMhDiLchIAmmkz3Q/17b7O6TruVPj7vS/gZElZSamPbt+g==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX06' LIMIT 1), '高压电力舱', 'CAT-2ac00ed548794b05a601d82d7c4e8dc2',
  'region', 2,
  1, '6gwXcOwxwH7cCs0Ysx8th55sNuMtLwNoe0kOqxQrmoSXW/izDgRMN1WHnpIphNLnU+Q/vw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.22' LIMIT 1), '管道舱', 'CAT-2aef8b880c6a4f1e9e897a501cccda0e',
  'region', 2,
  1, '906aqxP8V1FQ0zKdDQKAITYWryEx2Ge6rlVS06+Zx5v0OVS+waQgaKbzBcfxyg==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.23' LIMIT 1), '高压电力舱', 'CAT-2b49c67499424d7cb10c6243f331d9dd',
  'region', 3,
  1, 'adsUqsTH4t9+1xWEgAarLpYqP5o6dzZ2qca4IUgVdm08Rtx2XBdJfmgjZiDfGB634bJTfQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.TYL.01' LIMIT 1), '管道舱', 'CAT-2d5cb0537d9e4c3a8ace0945ba97f3fa',
  'region', 2,
  1, 'HJJkH0JoEfQuOTw58g3l2OP30KTFqVLVXuedu0Ln/G5G3NpmuE7lxGJ145xAOA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX02' LIMIT 1), '高压电力舱', 'CAT-2da4fbe263404269abbcd8cb924e1c16',
  'region', 1,
  1, 'dq3r/R+BAV0Fmf2knXYftcCZKePXyuFpNF8/QVnmuMY5yvbfWH9xNcjcM9GT5EgqaMBn8Q==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS07' LIMIT 1), '电力信息舱', 'CAT-2dbefbfe4e1b40dcac8ed564e491614f',
  'region', 1,
  1, '6n3EyK/ehs/cuy90uTTyNkUR5RRAUiFPujBdwsyGhOjbq++PeautL+mGCbFoOnIJJx/jOg==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS19' LIMIT 1), '高压电力舱', 'CAT-2f6bca01c5b94d8291e566720e521c6d',
  'region', 3,
  1, 'Wfa+uQhM7faLCa9Qn/rL4T4kzP6mhweCJo2LdQxjL8bfmjXZ/8CFMr1MZFcXEmqzgs/47A==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.11' LIMIT 1), '管道舱', 'CAT-337e06798c1e446b8dee19d1f075f967',
  'region', 2,
  1, '5VlwAM27nysXHkEW4zEkHASUYqolM44rvaMLu/ez3GR2YubHpsnaFM0mw+GHWA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX10' LIMIT 1), '高压电力舱', 'CAT-35f93f6ee1f44ae1975feb52e190c00a',
  'region', 3,
  1, 'hdWd/ncLCKubtgj9Ew12qjZF99otSWvCVpjQOwwIXPlWEzRGAiODztUMVF+Fl3hDkQ/4Nw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LN.07' LIMIT 1), '高压电力舱', 'CAT-3727def4aa78407d8ee4484aa1dec5ce',
  'region', 2,
  1, 'BsLZ//gfTfH2xS+CxhGYT/JQ9RwcjyrP6LWK6/Q2Tv9+G1FVNkaUA/V+adFRmqXaNr0ePw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS05' LIMIT 1), '高压电力舱', 'CAT-377dba6eebf34710912bfb442befc44a',
  'region', 3,
  1, 'SQLsTjB9kbtITuxBOur6deHJGGgMGrRduCPtKKVWt1eOqzrNcenPXv/QL5w79YJUirewQw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS17' LIMIT 1), '管道舱', 'CAT-387521ef9fb4439fa05ec30906d56bee',
  'region', 2,
  1, 'sb6NXYLYOPcObq08msQPZO3hU+povTOpF+tk9yrrxxZjjnkxtGg39XSo21nsGw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS01' LIMIT 1), '管道舱', 'CAT-3b080c67857648808e8f2a13d7c49003',
  'region', 2,
  1, 'H19zDUxCP6AaGLyJdG/ApUSIaMAvUy5GwI76KRxYZrnowrdzqmSsW0IBJr4NfQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.HSDJ.05' LIMIT 1), '管道舱', 'CAT-3b5bfca06cb74d9fafa43f3edac48391',
  'region', 2,
  1, 'bo99YR0Qb7zclyellaIxET4s79/k1pyZEs9HqWtKmu/5PR1tyL5QASMy5Qe+Xg==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS17' LIMIT 1), '电力信息舱', 'CAT-3c557d96becc4b3e91b90d423b9e69a3',
  'region', 1,
  1, 'PWim26Iku60IRCI0X959G/E06Wg3yy03cVNNeeEWrRI7Cc/PX6WC3QBcOw+zAw0wyYr6dQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LN.01' LIMIT 1), '综合舱', 'CAT-3d9de3441b574435b0d3f09c152142f0',
  'region', 1,
  1, 'oCAPK5Yde9ZK3/gHxTGSwwvNsltL3T4yTt8qTkEWX/6we4fRYW+YGrUryqn7CQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.HSDJ.06' LIMIT 1), '电力信息舱', 'CAT-3f38f422e2004a94baf84a76b17b8514',
  'region', 1,
  1, 'urtUsbTB0XNWB+WTsPH4zXOuummkRUz9rKj+VkVAyIgBfJtgCdEZwM6kitD71cyI3ak+mw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX15' LIMIT 1), '电力信息舱', 'CAT-3f5582f524b1432da50b2983a4e2a23e',
  'region', 1,
  1, 'Fj5LbSc7i39wj89ktItXVHJH0l6Pr6us4enTG2I5NHpfpEC0oAyIGFSSFDz9ZqPRaLScLA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS11' LIMIT 1), '电力信息舱', 'CAT-40d96d022a5e492c87c94299e7dce667',
  'region', 1,
  1, 'GrRG8udvXFee6MfaWOtWnMkaLMVOllGOlRfMJ03E1dNsreD+qj7hTNc4HQCyQ3bfGfpH4Q==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS15' LIMIT 1), '电力信息舱', 'CAT-43271df89ddb4fafb8fea76b01e11160',
  'region', 1,
  1, 'gXj8l0Rehdjmy4RrsI3AzUkdpgdSNcMeVpINtOTfwh5OVpr0DDHXCMNqGOSvAuts1rFKmQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.08' LIMIT 1), '高压电力舱', 'CAT-43d4398223314f559a6f311fcd26805e',
  'region', 3,
  1, 'LUarfKjG/VFNhv9o5fKfDckc7lEUtD0qBfYbWKbA7U4KTh13INfzLRKi/bofdpkfCwIXog==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.HSDJ.03' LIMIT 1), '管道舱', 'CAT-44bdc0e8c31c479494872ae6f00aba0f',
  'region', 2,
  1, '2BwIFSpqgbw7MX8sT3xnshMOfY1c/uMeGxCakr5KljMVN1K/QO5czEIeam3VKQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.13' LIMIT 1), '管道舱', 'CAT-452c63e7322c4f54ba4a59149c8817de',
  'region', 2,
  1, 'SFS21HstvEhj2kAXxnWNXlRi4NnGQZKQ3h+03F1x1+X5aRj8qwEk3hkLGtA0Fw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLN.GX07' LIMIT 1), '管道舱', 'CAT-45aada6bf4bd44c68a195f4e17541835',
  'region', 2,
  1, 'rB3OApCXBUPaUk/Cz9VGT/vuT6hiTYJlkFP54KgvHjhfeXoNxv0LFCHNTdl/Gg==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX13' LIMIT 1), '电力信息舱', 'CAT-463e73ef2af247c9a3c5fbb3bae9acb5',
  'region', 1,
  1, 'Qi5PmdQF+2NY/O5ZhyKXdTXMwMWIJ9jJihfEu1XPvJRF31R61O2RBbJWEFw/XiErfRpEqQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX16' LIMIT 1), '电力信息舱', 'CAT-467d8584824545d99289d215e45a7dbd',
  'region', 1,
  1, 'Iyo7caI0l8R5yyy51waOPdAn9MPk3bmuIxTAWifnruYzsWv3n9R84leUaYKIqYjj5bAbJg==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.TYL.02' LIMIT 1), '电力信息舱', 'CAT-46af1f182a7a43019ee87bcbdae8678b',
  'region', 1,
  1, 'fSZdo8o/zW7YI6hEB3971X9kFb6ND5U/8vONEHJUc9s6MZUV1aN/tZyPcbzt6uMgrT/bnA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LN.08' LIMIT 1), '高压电力舱', 'CAT-46b4c5024f38428ba8a44f4799018389',
  'region', 2,
  1, 'RhUS8oDVQCWHK6FXpUk689jD9dr3FVrL04dez9qpfky1vCFhpVmP5mDhfvSoBYmM11U9Yw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.14' LIMIT 1), '管道舱', 'CAT-46f24c80764a41859620af79a315a8d6',
  'region', 2,
  1, '/InGB8hVqcFeuAkN2R/0Sl4+307z9Z+Jp3GrYnaoJq+HbVKlG8bXea1bZxBO7Q==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS21' LIMIT 1), '电力信息舱', 'CAT-490cd0fd2dea46d7a931062177dff625',
  'region', 1,
  1, 'fMrMf2XgqhgZKeLvpsimOK0Fj9WurN6kpBTkLNfFHLTL1Hcb3j36z3pGZ2R4Swo5kERipw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLB.GX01' LIMIT 1), '电力信息舱', 'CAT-49226038a41b4ee0b105cc5f8e307391',
  'region', 1,
  1, 'meqZGH3R4IFedgWAp+XhMNgdK5plOvJbc0OA06iaLcIb+LCOcZYm+anrVdDYlWlDpVAbEw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX11' LIMIT 1), '管道舱', 'CAT-4a6603601113416a92e81e8c6c4c8672',
  'region', 2,
  1, '9DNRdTelAWI3UPn+dCysSsqtzXiG1jDlryZ1/1R5+qA2LiUTgZxfeswYKlqMSg==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS09' LIMIT 1), '高压电力舱', 'CAT-4b080146fc88420ca44343c085eb331d',
  'region', 3,
  1, 'QQJCZFf9j88aGXeYzLWuQp7B1c581MpsPUviH0Qx4UD6OiBr+ECfqym8AUPHhpxOc89OTw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLN.GX06' LIMIT 1), '电力信息舱', 'CAT-4c198b4f528249b3b3aa6edf314bb02a',
  'region', 1,
  1, 'rTw2LzImhSZq0rvKrAxvH0vWxchBh+f+Ca3Ba5KVyjVZGZERlNOhNkfVIzAB9zxVegxpMw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLB.GX02' LIMIT 1), '管道舱', 'CAT-4c961b6657ba4fecab436dce442db71b',
  'region', 2,
  1, 't7eauDFn39ZrESySkxe/9wFruMbIb3vYroxlsAbkeEZS5TvlGMRnHp6PglbIgg==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.21' LIMIT 1), '电力信息舱', 'CAT-4cf8fd97285543f98ca764940ae30fa8',
  'region', 1,
  1, 'ElW7Xvh5ye+zQ2pnVSH+gPrtgz/1wfj7ZlNfG5VYGPAtSre5N1ovm4kv7dKzX4rjweRXaw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD5L.SW07' LIMIT 1), '管道舱', 'CAT-4d549a54620246ebb32c65f5603c7005',
  'region', 1,
  1, '1uyOLQra6FlSZT4t4Io8AFBJi3XfKxwdC7A32SB5yjuThMJtthWVRe8E5GEQmA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.15' LIMIT 1), '管道舱', 'CAT-4e60de6baf314ca39a7ccbcb77028713',
  'region', 2,
  1, 'dU0whE8YvdVHe5x8ME3ECp0G/MhNW86wLDwk3AN+iC7lTv7i8BBgmI/mZZBAuQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.18' LIMIT 1), '管道舱', 'CAT-4e9b359ffa2142ac95725b4ad292c72d',
  'region', 2,
  1, 'x+vfhPXCMUTrQQ3dvJxf/SOPzJiqLQkWD4GZo8N7WrRIq5tXlViJ/wD7blQo3Q==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLB.GX03' LIMIT 1), '电力信息舱', 'CAT-4ef7100b42424b05819a666168a2e6f7',
  'region', 1,
  1, 'mVBooyblqq+fyQcFcjXfi6EOMosnOiICNMcnPZSi/QY+59dDHLE0GKistetCV+oPqCeskQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLB.GX07' LIMIT 1), '高压电力舱', 'CAT-4ff0ad23f0bf4d65a6c63535d495eaa3',
  'region', 3,
  1, 'kn5Cg07qiJbVue4yQ4JJP6SGYxASdtuxtTMqRnYhqxrARHBirgBslq6Adc9V26d84KT8bw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS08' LIMIT 1), '管道舱', 'CAT-5215b32125ff4b1d9400a9a8c8f21d71',
  'region', 2,
  1, '5l5wxHO1NBU75gsJqLk5qGQc/74rc1EZsBhnvbZ/c+HL75G39PmoBuJxOwoyfw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.02' LIMIT 1), '高压电力舱', 'CAT-5227d7cd16be406dae106bfd0b693c43',
  'region', 3,
  1, 'LCu65EG9XmycpLehBA71XBEGSTs+USPE+gGL7twGNvZInSOx79oOSQK/YqIc3UvYxN9oxg==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLB.GX06' LIMIT 1), '管道舱', 'CAT-5255eb83828a4427b85266c26719e446',
  'region', 2,
  1, 'XD1owCVqPEYRqgYm1q29IAj7wtnyYnrUTJeF2ZreC6hZWNte+kxRKM0wK9B35w==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LN.07' LIMIT 1), '综合舱', 'CAT-539e64c26b164ca6b02f2abcd097b4d1',
  'region', 1,
  1, 'EyWfPNphm10/F3gG3YJpSFiqSyru2QtqosxvtsvRxbjgpxbMmT1xdjBTRJ1jVQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.11' LIMIT 1), '电力信息舱', 'CAT-54b09edf3f154fb1943f3c12257428b0',
  'region', 1,
  1, 'sKXdR4JIr+KRegXdwmDFn55h5S/MyScQSDT5wTfaDMk/GhkivXDKkEb5utTN4q0mlSKGtA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLB.GX07' LIMIT 1), '管道舱', 'CAT-54bf8a58013344afaaab8bbd15025870',
  'region', 2,
  1, 'MKK32Ky1ckjWJqVxsyKVb0NBWu+vpsU8WcrHCDkWNIjHCWbq8T2jmSVjWMPkjg==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.HSDJ.01' LIMIT 1), '电力信息舱', 'CAT-55a59323e9f746cfb21f0612905bcce4',
  'region', 1,
  1, 'TmE5TkJf0TF9jkD61fldtIHtd3b1u53gwjw57lLlTGieyxlLeUpBwweJDIq1SKz+l5IOkg==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.05' LIMIT 1), '电力信息舱', 'CAT-55b38a587ff646ed8c5df92076d6bd52',
  'region', 1,
  1, 'DA8HZbI62T0vrYuG+ybHYNyBk8m8OcxV4BmuopCX8bMizmR0raISvfA02nF9+gQAF9CEQw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS10' LIMIT 1), '高压电力舱', 'CAT-561a8ae9e44c4cceb99c8b254e9aa2f4',
  'region', 3,
  1, 'fnpFme7qme8NpDtDCAE7auHGbF/FHVcdUxDVsAZnlzH6oRfVCtxdCYEc4n3s7032kiiOCA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD5L.SW02' LIMIT 1), '管道舱', 'CAT-57a16c2d97a4467b8620a166991e4f92',
  'region', 1,
  1, 'iWPvT5wHJJPSnUPXN//S/R4KE2EVwUVgS36uczC6TfBp846oxTyGtERK707/Sg==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.22' LIMIT 1), '高压电力舱', 'CAT-57dc020dc51045ed9f80fea064f6f400',
  'region', 3,
  1, 'bZ1THolHrEqlWQ2/P7a+iLtdBSFbvn/ZG/rQnUXfc9Yi6g+e/JOVg+A+/gavzYrtv7rD0w==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LN.04' LIMIT 1), '综合舱', 'CAT-58e078ad86c74a979b6a016f9c21edd9',
  'region', 1,
  1, 'F9cTqWJhdp08aSfsbqtlLy9+417uZ8NNV+mdBS6lQ1knovLrrnFc2Zkz1WyULA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.05' LIMIT 1), '高压电力舱', 'CAT-58fda9f48155446bb97e76984185d472',
  'region', 3,
  1, 'WTPZRQhwUXob41M94zpC31EKTHv+AkKFkGiv/KmCfQp9mQxlkvPwEIMWfB/S7zsQIqpFTw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX17' LIMIT 1), '管道舱', 'CAT-5992613ad5384aec893be73b1e0c7455',
  'region', 2,
  1, 'npXqt7qaHJMtbmHwpQWfNylnaJXdVFbLwQhNENFuSgzbLOUB+ixlGw6XuIQ5Bg==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS04' LIMIT 1), '管道舱', 'CAT-5b8fb984e89144ccb264e0a8d1a50046',
  'region', 2,
  1, '/Be2DCrCJKbfCTY/mo93Eo+4GOgWNUNPvfFGqrymIT3Hf0s/NgYsxKaSVmwNgQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS05' LIMIT 1), '管道舱', 'CAT-5c28225c85934974b7cbe6826a2b30be',
  'region', 2,
  1, 'GmPBwvrzBWtsvdYhiYlNuCzarUQPaNIWUyDncJZVl5YJ4/eqlyQoSKPjIPt1AA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.HSDJ.01' LIMIT 1), '管道舱', 'CAT-5c4a5d9a6e5e4919838a93c9d5134a8d',
  'region', 2,
  1, 'DUxKHvH8oOSnzWHC9skiAN1RVFpZzt0g3kfNJPsgQ/mwno1rvfhXUjme00NQyA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLN.GX08' LIMIT 1), '管道舱', 'CAT-5d71fbfbf7d54eadbc7528b8501a6d1c',
  'region', 2,
  1, '2wds76BWKGAIQDrZilod+KHuVN1yUC1Lr6nGdfTtYzVvaQOi4lYVl4dC406iow==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS01' LIMIT 1), '电力信息舱', 'CAT-5df1655d9aa9434ca513ae95def3d30d',
  'region', 1,
  1, 'Qd5fJAEKM8k4aVRyYF1rHqzNOR+VZMxcWb80jQPd/9ldMc/S/aJ4/R8CUYj0cs4afR1VpA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.22' LIMIT 1), '电力信息舱', 'CAT-5eaa5d0748c84c06a7bd1e6a9bc7b26f',
  'region', 1,
  1, 'OEwbjUMFNqaBLqA0rDHvN8xerbml0W1MO0stHMKjpia0JEYR6MUo45+20dn2zjqMciEUMA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS18' LIMIT 1), '高压电力舱', 'CAT-61af83bda750480eb9a4c565652596b0',
  'region', 3,
  1, 'jnwd8bNmz5iyVTE8pheYW7rf3woLNH9f8a4RpTte/vBt8YazdHdkLtK9huQCKAC1DxpHnA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS11' LIMIT 1), '高压电力舱', 'CAT-627572ddf1a745c1976cee5b194263ab',
  'region', 3,
  1, 'nKSa/TGzyb/Tb3us2iCS1JCD+KHhOSQv5811VBmH6FtHSJ3PbH09v+uuQ5vRJQizMJW6Mw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.HSDJ.12' LIMIT 1), '管道舱', 'CAT-6373cb14837f4ecab2a73ed43d5377b3',
  'region', 2,
  1, 'DarAUSBFjVVp/eyn1lxF9dCEPuIemLOmkb7gcusKLMh5xCHKfYVEN8klTvpV4g==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS21' LIMIT 1), '高压电力舱', 'CAT-63acecdd49b04a4eaaa57cb96647947e',
  'region', 3,
  1, 'oHepbhQeJahVzD4Xk7OEu40yGSj8oK9bd93sFFOSnJC1Nvx1nO+LS55sZTM7z21n4VHNWA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS01' LIMIT 1), '高压电力舱', 'CAT-66278459eb2345f794057ce36fdbc21c',
  'region', 3,
  1, '/k1zBRyI+76sCNNjOUBQBZ3d7wEOgGuJxIMi89JtE1ouwqOusHEOTbjJNGA5EPqNSNxyKw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS12' LIMIT 1), '高压电力舱', 'CAT-6705bdeb6e8b4d4d971980323d1a6182',
  'region', 3,
  1, 'ET57Wjta3Gd93NIrTpHnYJ168v+C/jIWNvHSITfNed4CaFpd5Skg/LexLMjBA+RAeegWDQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX01' LIMIT 1), '高压电力舱', 'CAT-69c4137873ea4c618c5e18ffb8156c78',
  'region', 1,
  1, 'iVGWzDvo5/5w0XIYB+cEdUVpTZ3Rfwnz65rNi+bTiglfbanfy5C4QDfp1D5Pc+7YcVdR/g==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.19' LIMIT 1), '高压电力舱', 'CAT-6a066c1bb34946758cc1812babe15a81',
  'region', 3,
  1, '8mbI5EDQrGn96ClJ6BmSbLwo4PWl5ymDGKjOCWIWp759ufkMuPXGitwy+JNhQtH7CrYCbw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.HSDJ.03' LIMIT 1), '电力信息舱', 'CAT-6abaaa5d21cc49fea1af702205e4bf11',
  'region', 1,
  1, '6DThcLBxUM23SOoT5BQj52GxXtUumtHhGUJ/lM/aJbr9H5oKbTA8OrNPJ7WZDTgKbDZ1EA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.04' LIMIT 1), '高压电力舱', 'CAT-6abc8977918b401d8bf42e3244f9a843',
  'region', 3,
  1, 'U/AWXrRDoqKR7TsTV9c9pDL/y0ycpmf6pDeqH4eHnSHIDXlDVNiUTPYEn6n2gDGmxJnyhg==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS09' LIMIT 1), '管道舱', 'CAT-6b33d6e411f54ff480c49f032f0d3fb1',
  'region', 2,
  1, 'PXG4fyC5asWBJ+glM0V89KjEcgfgUFvf5qlSP2n1982Kq9F2A/MNEucIJg6F9A==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX10' LIMIT 1), '电力信息舱', 'CAT-6b67e6f30e1f4d12a478b424fe491736',
  'region', 1,
  1, 'DOztA65qvky26iJy9J7NNCGkRZk2Y0bHHfdIyW5lUXPQOGjc7LmyR7tYxWqwbVB5nFlFdQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.01' LIMIT 1), '高压电力舱', 'CAT-6bd7aee06b5d449cbee8cdd72825ed5b',
  'region', 3,
  1, 'P6VbKKO9sWJto1vALdAs0UmykaVkVCpwedRav0nTiRGiJ/UDRuwipLkk9IymLvNPibwh1Q==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLB.GX02' LIMIT 1), '高压电力舱', 'CAT-6d4155e3cf5e4979a59962c78f26872e',
  'region', 3,
  1, 'tLvQ/km9b2xWQrkZdsHZG/IOtohKdmtoItXSU83XHLBO+DLV3lh2MaBj9/TyhJ+EdwG+iA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX11' LIMIT 1), '电力信息舱', 'CAT-6f613078d0b3410f9c1446f67e95d5a0',
  'region', 1,
  1, 'AfEmhfaJdZS9ccN5ayIDe7maC6Z2Hgb3wJAJH0AeFSvr2guuGR5pE2AOH6z7CLnEAOXJKQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-1b468a9c7b8e44d7ad664dc096fff9e5' LIMIT 1), '电力信息舱', 'CAT-6f8734a2dc71474abfa8cc4022571ebb',
  'region', 1,
  1, '6rHG3oy2f24jfj83sCePax38F5mgIjlHPlv0ntekBd556zPSKLpYNtSn9meAsIkGWfyNXA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS16' LIMIT 1), '管道舱', 'CAT-6fb5a92a0bf248df9954b133d7c1810c',
  'region', 2,
  1, 'UUmCogjNtm3xL0y8O9VDMoftSv8gJYbmtuLcwohafmYZESUdWwi13NocyIYoDg==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS01' LIMIT 1), '电力信息舱', 'CAT-6fe9eb2b04b24e1a8ad6a24ce5e2459b',
  'region', 1,
  1, 'gS1C1LpBzt1Bil04bk8K5Rz/jsVOocVp96xnpOnGveObCpDahxvoePl5AnjWtg9a5hTKBQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.11' LIMIT 1), '高压电力舱', 'CAT-7079cc6e461c423d8d964168166640ef',
  'region', 3,
  1, 'o7bqlfigv3C6hs9f3OYWP+6/qTYHZq4o5fidMRjwKaqn5dXDpC2jQbStbuDLOGLYO89o9Q==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LN.05' LIMIT 1), '综合舱', 'CAT-70d6a74220ef48eeb55a692c56c69a4e',
  'region', 1,
  1, 'ozk3NajNnkjwq51KGAmmHF/U+SPZnyRufFzMdTP8PDnAUOZsdxXbdbKzCsJwGw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLN.GX07' LIMIT 1), '电力信息舱', 'CAT-71da237c236340c08bc2f930bcad7b47',
  'region', 1,
  1, 'YTwxwu1mPPP1xLZC5bmaKgE3j64azhKA9HoGnuv6Pa3YVr+ZF14x1x26YzRV/xPCBCAMUQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.12' LIMIT 1), '高压电力舱', 'CAT-72458b4ed32947e197fe030cf0193087',
  'region', 3,
  1, 'WPLLUKg40jv7g0W/wowQyGqX47c0IHDVa/qlFtoAeLkYTgfQXpiijoOtzJ6o0WFjeOf/2w==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.HSDJ.07' LIMIT 1), '管道舱', 'CAT-732ee1f0da394c0e94ff65c220e8018f',
  'region', 2,
  1, 'hx1uF68omIKnh0io3v36YQ8YUIpbqJgn8pa0LuzpC0Ar8EvVTe7+cDa5KueSVw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.TYL.05' LIMIT 1), '电力信息舱', 'CAT-740e02e875de4665a19b9d6718c97fd7',
  'region', 1,
  1, 'm8Idh0YCn8oWfMBvCpPP2iI9tMQym/uJ+rR9V+oxG/70Os45Y2DyRlyk6WnRAUfxn6LhPQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLB.GX01' LIMIT 1), '高压电力舱', 'CAT-76186fa05c5b47eea17ed8feafc2bfe9',
  'region', 3,
  1, 'IqHBGij+6IELvTFUESEtaeeBUMJbpnLmxkguOAXWvXpr12sPMoLmRyGgyWUfJfN8Mxw2Cg==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX10' LIMIT 1), '管道舱', 'CAT-78cf2f65205e49b2b4b4a336c32b924a',
  'region', 2,
  1, 'mC4lwe6k3qCPV1vlO0Cnx5fFrKOz32NTnyYIMPgKamk6OI7Gml2Y8Fx4n9cr/g==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.TYL.04' LIMIT 1), '管道舱', 'CAT-78d4bdb8a13a4f15805721c1b487ee45',
  'region', 2,
  1, 'iozyPiYJvUcMscB+ReRFSPFP5sTUeLv8HlLbpiAqdnGdtK6jzSRdYNuAAs0s+Q==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.01' LIMIT 1), '管道舱', 'CAT-7a291d756d4c47609c9c0a0b8c8988d5',
  'region', 2,
  1, 'NdrqSDpPa1DNHRoyyVWuG8X8tb2gzh4/js7xXC0Y3HjSQZgPcgpaBrRxaPSGxQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.15' LIMIT 1), '电力信息舱', 'CAT-7ae6975333504a1bb83074bd6018d2ce',
  'region', 1,
  1, 'b1F4R0bQkv8/fm917IgoPo2w4tNPveB6YK/nb63GPvXA55wBX251hiycykOxKoBgeBTVUg==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLB.GX05' LIMIT 1), '高压电力舱', 'CAT-7b7635df21f44ab9b3fede204137c96e',
  'region', 3,
  1, 'BGaecfrNPg7SFwpa1AXxODj3OGeTvOiaDWHB4oH6zImqtsa6PAc3Ueon98Q5QX+zQtDhyg==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS05' LIMIT 1), '管道舱', 'CAT-7bd52a06f23e4edc85779dd15c114482',
  'region', 2,
  1, 'r+nno3uA34h90Q6hWcsgGVonSBvJWKaHbWSYWq6jWorFl6TgCK8bEn1vTpfwAw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS23' LIMIT 1), '电力信息舱', 'CAT-7c56d6f20374437b98b53f0d5ccfe931',
  'region', 1,
  1, 'ZjQDcp/wLu+tylWTD4fFZ29uNOHStWLmorIPc3H2pAkPRLVW/65p5TwBLdUKWt87lmYyLQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.HSDJ.13' LIMIT 1), '管道舱', 'CAT-7db54862591346cb8a2ae133ee9673c6',
  'region', 2,
  1, 'TusSdCgrNpS2TKrQk4ID3ZJnMGx+jtAVnQ5tQQWBc81RxX0V1TEMkUfd6w9tMw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX14' LIMIT 1), '管道舱', 'CAT-7ec0dbca586247e2b62538d30d2eedbc',
  'region', 2,
  1, 'zDCNVDN8LlEseY8LhN5mMmWljtR9GN1qV0WV08IEhzLXZp0jcampcbF1No6Cdw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.13' LIMIT 1), '高压电力舱', 'CAT-7f0d7a6a517f49019464b5c9870f9cbc',
  'region', 3,
  1, 'GDs3cyoTQIlq/FcJdagmexSVD/YI6m+g46hAl9QDugDlPf9OZWLiJ5ReJfqe8PGtGKLWKA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS07' LIMIT 1), '高压电力舱', 'CAT-811883d410d549fe9452d9962a83a909',
  'region', 3,
  1, 'TGQ7qnN7jUs3uA+7bQuD3ITiIgmoLGyFOUCGLPSQHTi3+zr8PV1QRYqXviIW6G3dHzu+8g==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.12' LIMIT 1), '电力信息舱', 'CAT-8138fbafca094447b28b7854e9e9091b',
  'region', 1,
  1, 'xmIIGtA4jX4xAkrE7+TAzhSbbaxrAzdpDWy1p4gxtT8cgHwaq4jxArY0uj4hPyJsX+P+CQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS13' LIMIT 1), '电力信息舱', 'CAT-826387e3c22142c596c0138f7d424ae4',
  'region', 1,
  1, 'DCtBJBT1NTWLyRPHSv/rufJZptdC0N1WJE4NT/D7wsWcWG5CLSUhWvoFc0rQEod4phB+Cg==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS17' LIMIT 1), '高压电力舱', 'CAT-8489426591c44b2195b028088b9a3d78',
  'region', 3,
  1, 'TEnFKDhs1n6Fq85engCwxHZXVvkjor7xLOeBmrZKoOOo/+Iendz8UofsPVkI3fYy5NzzgQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS20' LIMIT 1), '高压电力舱', 'CAT-86e78f84b3dd4f88a5b091b055653130',
  'region', 3,
  1, '9RidETzUa9uQ76rTFCxbFdWX8wvdu/pEzxRJgLCpc/AHo6JrVTe2b2HwLRDUbyTTJf35lA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LN.06' LIMIT 1), '综合舱', 'CAT-86f4520956c84081ab0affc053a907d5',
  'region', 1,
  1, 'Re5jSU3oV4gcl2uySABCC38C+894LJHrU1GDQ/d0ol7f8nDrAS7Omi4h/qWUVw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.HSDJ.14' LIMIT 1), '管道舱', 'CAT-87b70ee7a10243ec8896f16382270599',
  'region', 2,
  1, 'MTtX2So94ujmlJIzf72mqJmJpl/6HuF2SVWA1O7Ty6PJBsRb+YLzq3GHShlwVw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.TYL.02' LIMIT 1), '管道舱', 'CAT-8829be2f52b141a2850dbda30caf7ec4',
  'region', 2,
  1, '6N1zFtXVfkTkFuwO9yNv8uLkUePDCFM8+CwLpG8g4esqcmULqt2WxDbWi/2zfQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX18' LIMIT 1), '管道舱', 'CAT-884371dbbaea446ebb5fa27ddba28857',
  'region', 2,
  1, 'fstl8TlpmMX5kKA7bOnmV5cV38MKxme6Ou53z/5HaSZd3kJHxaTiGj0rd6Ezuw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS03' LIMIT 1), '电力信息舱', 'CAT-8851caffe5534399b56b939a54dbfea0',
  'region', 1,
  1, '2hBfwgNI+tPxmhLsXTJ4gpXEsgvixa9OmLTjFNW7vyI3jwaSfQlFOecWZUqsZ7Zgdr0vrg==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS05' LIMIT 1), '电力信息舱', 'CAT-88cfe6257b234023a84d7861877df64e',
  'region', 1,
  1, '8v9QeVaTdtrcLGAaF3X2+9QiPP0We6uwQie9DQlJXJXm7ybKe1eWl0/corlgkAe8SVNu/Q==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.12' LIMIT 1), '管道舱', 'CAT-892b96833d0c4335997d68574c706224',
  'region', 2,
  1, 'WdRrIVqFIKqBdZYCDNewmbFB8fnU8X/U34ZFDYiBJqoL5wI4F6YGcW6uyuZFqg==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.19' LIMIT 1), '电力信息舱', 'CAT-8a47dc0b1c5e400ea58d53b6b675cff2',
  'region', 1,
  1, 'ZSbOAjfxvOAcS16D8V2RVSRrQAdPn9BnZKc7lWAyvCj+Q1T+f4I9sWT3Mc9AU5HlkJUw6A==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS18' LIMIT 1), '电力信息舱', 'CAT-8b7b257b9bca482387b9c856aceeda58',
  'region', 1,
  1, 'fVv93OKiY7PvKh70jEUCQ7Ac1GnpRzi1GZyJvphwS6QGAYqhJcBXa1SKQpoEl894mu01Jg==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLB.GX04' LIMIT 1), '高压电力舱', 'CAT-8c159cdb42e743e9b52cd706a13a3b29',
  'region', 3,
  1, 'QhnCiLImNypudiO8iJ5vwUzWNTZImTnmklf1/ePjd4GmRkMewudHXsB1loeTPHCGfY0XAg==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX16' LIMIT 1), '管道舱', 'CAT-8c429c96a9374e2da0d78dc8eb76cc00',
  'region', 2,
  1, 'nnfdtYHoPcOdCwRnVavlQoANuOZ12CRxq+AL4KO+CxQ97U+pRn6IkUo3zkARIQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX14' LIMIT 1), '电力信息舱', 'CAT-8c4a11326b674731aa9e03aaf5ee5c53',
  'region', 1,
  1, 'YPZCvF6WqiG7VTWPVluX39I4iWN1c5YcV99jH1a7lnUf5pyFx03Un2WfSOyrzSWW+ZVpTA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.HSDJ.04' LIMIT 1), '电力信息舱', 'CAT-8c6f2fb18ee74f1698c8593ee06cf090',
  'region', 1,
  1, 'mujPaXOuc2zd7vWPUv8Be5leZpLQCk1SLjwhJzhCukwVS4OhMP9cmwyAAOAkJ3to4QuVBA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.01' LIMIT 1), '电力信息舱', 'CAT-8c7626c3866f41cbb9593b2a11aaa96a',
  'region', 1,
  1, 'rwx2XDw9euF75Im17XKJrKdCC/11ua1ddKv/zFlGgGkfXahlRAJB5jvkdjpX7pZa3d9e+g==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.06' LIMIT 1), '高压电力舱', 'CAT-8cbc0ee8041e477f81d627524cbbd964',
  'region', 3,
  1, 'C7HL5XcBCuVbvObfZuabuHoNV0OYq1bQf7qbFvyco1HUsE66M6AsgbXy1YIfjZMDQ5qMdw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.10' LIMIT 1), '管道舱', 'CAT-8e1a666b58e54954a2909bf163ee7f81',
  'region', 2,
  1, 'BnSZBxrP9wzoBT0cBmTjAzahjpHHBsed/hE8w3iVNTujtY/P6BmjdZY/TVtiAA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS09' LIMIT 1), '管道舱', 'CAT-8f8b28525d804a568aaa857682e6f6dc',
  'region', 2,
  1, 'b/IK5e5MTcuXwYhj7Odxj9OtLNpAIA1CBntDDlM4w1woO0/iQeKCet7/P0ofsA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS22' LIMIT 1), '电力信息舱', 'CAT-8fbf958ba55f4d1fb0f3b893e394f112',
  'region', 1,
  1, 'TAViHnynkOL27bsgklrbwPbHEBQ/LnrrRbZV1Ij/euBwrX+or4xUXcOjk4DNop1j0gWmfQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.18' LIMIT 1), '高压电力舱', 'CAT-906a90e6cd6946b1936b8ee0a5877c26',
  'region', 3,
  1, 'YRQX1AmhNv8vtszazFRv7i+4m6v6toPMw/S2yMrQe0h88TQZ+sF2ddrUCFybDzf5ZyZs6A==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS13' LIMIT 1), '管道舱', 'CAT-91de3caf359d424c933182ef30f4040d',
  'region', 2,
  1, 'H7Y/pVBXvOE84qdolI44S/VThLQoQEeMV3dQ1T8JspoNJ/7bQ7umbXT9y5S+Mw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS04' LIMIT 1), '管道舱', 'CAT-9729546090cd40769e869cbf84bec9ea',
  'region', 2,
  1, 'rBalPqlyj48hVvacMJAGVIsf/tEaKmN0mVNRgEskdEI/SZjq1DqUi9KXANeIBQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS18' LIMIT 1), '管道舱', 'CAT-97515dc7283841f995fafc672ec80b80',
  'region', 2,
  1, 'c/nAXKS112dwMKW9KGRvm6dicyjwBiQTOfmDQ9oahzpguu1ssk7VLqDfggHPlQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.07' LIMIT 1), '电力信息舱', 'CAT-97718cd6b0a84735b48731f17b197b27',
  'region', 1,
  1, 'pXS+RJL6wMz44t2YZ9la8QABhl1Ljn8+IRmwQq+w+9ismQ7j3KhkxqcUznc9LMCvsNKo+A==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.07' LIMIT 1), '管道舱', 'CAT-97d79d39b7594de2989315fc3bd11d3d',
  'region', 2,
  1, 'LiBeUp4WnZNJdHHv5BiM8PqzWE2+vN5SR0oELj2VZNdWA44UaPJ9n9Vmv9B8/g==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LN.01' LIMIT 1), '高压电力舱', 'CAT-985d2784897b40aa8cbb2cf1cbd10d0c',
  'region', 2,
  1, '9g3EJO6JBZmdwGluZRi19DikC0cECl5qNIWytWsozWWMKcuY4cwxKkfACrGjnscv1LNxFQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS07' LIMIT 1), '管道舱', 'CAT-98e89b1aca4f4e18b113b047f26babc3',
  'region', 2,
  1, 'vWuFQlC58hIPmHLPfNvJBC3XjPPhkJ1eUDJbpl+qqr+UItu48/plwVsRIxp4rA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD5L.SW05' LIMIT 1), '管道舱', 'CAT-999ec967096845bc897c36f6f65e3eef',
  'region', 1,
  1, 'Bp+jctkUo+Lp7uf7+2qP5TbMqq129NbxwdkVRKYC3ruom6KWHXXqVaZn00n4WQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS14' LIMIT 1), '电力信息舱', 'CAT-9a25618bbb7b4d92a7dd0f4a26f62100',
  'region', 1,
  1, 'ZzNRNjSxi0WE1SkfDJDQsmz1E6ZqFl95JX4TbngVJ72Zz/3OZ4D7+RpKSderWIogsQpVUw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS14' LIMIT 1), '高压电力舱', 'CAT-9a4dfe7cb8e94032b289f470cf576d3e',
  'region', 3,
  1, '8hZhwykN8hE8B3vhzklJRCBFM/U+HQo7C8qZ4LRRzpSqcMD1m/ViGdn6WWf10Ah7lZ5SnQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.17' LIMIT 1), '管道舱', 'CAT-9b1a9c61bdd44632a51de00adac89cef',
  'region', 2,
  1, '3MTDwZ0HVWynZ9QPTJDOr4LGbtzGT58t/ycFwiRoU6A5pqsJqtOcPmxQFMWkfA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.TYL.03' LIMIT 1), '电力信息舱', 'CAT-9c11e9c869434f8180384a7471a8e33d',
  'region', 1,
  1, 'waNSDlij/p9KvGA1rnEApkd4k4V5CAx/VcJWKZdtC0gDzyCD8kPea9lOBq5GgC+x43ENpA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.HSDJ.14' LIMIT 1), '电力信息舱', 'CAT-9d68d75cd0534ccaa52aace56251255a',
  'region', 1,
  1, 'eJp74q7ECGLKNjz2T/DUXN7znqp4HgK92X3EBwz/JeSM3CCQtBDvnhAOruDX1zUqhxWGEg==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.06' LIMIT 1), '管道舱', 'CAT-9de2f7bf9dc141188d9e5ad8a91b591e',
  'region', 2,
  1, 'q58LOD6RNaOdwwK5ZVBaGNCwKCsPoHdcqaAYZFcyqrg5iu5lPVq/DxbGkfxayA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.17' LIMIT 1), '高压电力舱', 'CAT-9e02ce9bc85f43fd9d733de565c4a5cc',
  'region', 3,
  1, 'Wd0scim7paGcvDxFvUJAnwpJGoxBDyoKVlZV1IqJQdr5XbCVKAzgXfqXU74t7Df9IwuSwg==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.10' LIMIT 1), '电力信息舱', 'CAT-9e4377012ccc4b38945545d2426f2b9a',
  'region', 1,
  1, 'qIoDUPxDygZtCKCX5gBWeQZX/OeFUmAy5p0rube36zlwI5IcLJHXPdlW/7+UzmBhhebjrQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS04' LIMIT 1), '电力信息舱', 'CAT-9fb2c10739f5461e85923031b6b38480',
  'region', 1,
  1, 'Mxhoy0X8nqO6je3GS06JXHeal9rlFPH89/fBK/3yY3aabGMWcRS8efh0iEesq+jOQ7qV5g==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX19' LIMIT 1), '管道舱', 'CAT-9fc3600475e040e49efdc33524d1b806',
  'region', 2,
  1, 'V2Ato4neLX/n1JP5YYXhI6IFsHc8TVaCqx7hCYesLwEWylUaf0fxG4D+2ZL4Mw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX19' LIMIT 1), '电力信息舱', 'CAT-a15406504aed4b4ba230bb75a3f37c98',
  'region', 1,
  1, '813jNfi38yQPqGZLKcwWuqyiJ0OL7rKrIg6AZApQXcaoOGXnfAD91jJS1itrsqkmqIuHvQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX18' LIMIT 1), '电力信息舱', 'CAT-a1ebb20b5f664461a39e90e254762c2f',
  'region', 1,
  1, 'h22FL2a6sVK1Qrgm5fbyAGofUfrfYMgjwWUGcAQqa84maL9AL3YMG+InpAQ8z3xZ5Z4mVg==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.07' LIMIT 1), '高压电力舱', 'CAT-a40646d0a478432697ae080089ba11b0',
  'region', 3,
  1, '0hVfAUq59wIA0ZA4iiD73akr3uD0gyVfxa/G+qd+jtHKexr+1KpD0l1p2Y1FAS7oDCGW4w==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLB.GX04' LIMIT 1), '电力信息舱', 'CAT-a54a99d8b85d463d828ecb544528ff33',
  'region', 1,
  1, '+fU/wWsX2qaS9AuxBZMvYTsIIbMrxoLxY8O0+LfLSb0PlKxIVelSmAYx+zQyMcuTMIgOPQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.HSDJ.02' LIMIT 1), '电力信息舱', 'CAT-a89e667a72be46a68f66b0a8cab84921',
  'region', 1,
  1, 'VICLPlxZMZlEft+4nq5u7LxFo59UbgHxNycUIiwSz3H/FZkP6ZwFqJ0yFtM2ZJdeuxcCOQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS10' LIMIT 1), '电力信息舱', 'CAT-aa13e8a09edf470887f29827e11a17f6',
  'region', 1,
  1, '/TT7fw523iyYb+/6g3ngrfpHESuaK31ipugW5lpoyV+cli3zdIwwaVpzL5RrDjXTuRvZvA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.HSDJ.13' LIMIT 1), '电力信息舱', 'CAT-aae4085f7e664105b7d3fd044136d361',
  'region', 1,
  1, '0hp93obmUfsgTlLOpub5epCUarU8YjMU/LS2MRLuK36JsUanELlkR5Q3e1O2fHdeQJNk8Q==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX05' LIMIT 1), '电力信息舱', 'CAT-ac2fffc5b60b45418ece9a25ba948a4f',
  'region', 1,
  1, 'gYZALCSGUHUV3vyHGkWw1sf0e9FuIp4BQZiULJedKXYdp/OrVA9HIOg9LAUTFAs6v84VzA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX06' LIMIT 1), '电力信息舱', 'CAT-ad0c711f8baf4f1a8d738bc103facebb',
  'region', 1,
  1, 'tsArvrx83dl1/fqrYnIvr9bjur9h7/cvXiIsy3q9R5VwipBDrs90NiOeO1lmq/zaib/5AA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLN.GX05' LIMIT 1), '管道舱', 'CAT-ad3e7fad1140472aacb3ed6b25288d40',
  'region', 2,
  1, 'g2lI0jJm4z6Qbr5bhAi+GETtRyaSqhO9iSuiY8BxDRURyG9lL/dHgVRxCO4S/g==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.17' LIMIT 1), '电力信息舱', 'CAT-adae47433c2f4609b33aead3bafff3f4',
  'region', 1,
  1, 'RYeKxwTywxKdGqeGzDut+8aUUhECUNv5kf9UvDwFpyDx8wCGSVHpc75vnlrwEnk8/K3gFw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX07' LIMIT 1), '电力信息舱', 'CAT-adc0b78d7e3843a48dd1536b1af51835',
  'region', 1,
  1, '8ACXQhUE5whGKxmUjn5QpYQXa+mZ8runv4/e/m3PMtSqJE4SK/yNCob09nOefbAtho3qoQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS15' LIMIT 1), '管道舱', 'CAT-adca894e2d1d4299b274a88388b44434',
  'region', 2,
  1, 'YPGMU4DPzVeRD8IEThCIlQoc6wDJf5uwfJrUwOgpgnihIMEe7Enh43errH3qVw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS22' LIMIT 1), '高压电力舱', 'CAT-ae194bd2894b44d5bf205b22ce9b0994',
  'region', 3,
  1, '9ZG6dTrmOvac/v7z7si/uuuCBdc3/8S9BQPtCs6yGN6+wSMvzp6/jPY/WBqzpJcTNNwUyw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS20' LIMIT 1), '电力信息舱', 'CAT-b00334c88b364c39a741984adb9a2144',
  'region', 1,
  1, 'PYRlmLTefRDryoudVN7X4xsxguTMcqH8kBkmiQG11rIirU6I4L1i3/55S+mlAigESgiClw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.15' LIMIT 1), '高压电力舱', 'CAT-b011758f946f4784bfb56ddebce9cbda',
  'region', 3,
  1, 'wfwoUVJE93S4JeIXI7wr9Rzdjc7N2juLEwDTtH8G3QVyJ5wPGigQcAELr70BH39nUn8f2w==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS02' LIMIT 1), '管道舱', 'CAT-b05e561502984e80b6ee1d47f9dad589',
  'region', 2,
  1, 'NHlruv0lnsqT9duxdG7BiALoCiTrTzInpprtBUzHiA+TenmW3+yRCi1q8RXNPg==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.16' LIMIT 1), '管道舱', 'CAT-b089cb5563244a3ea367b848b11c160e',
  'region', 2,
  1, 'zEXWe0hgz9SFmVMeyDP/2NSpcXZyO9ODALPPzdtReKGiuFZRpvDlJqo+KdTmRw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS11' LIMIT 1), '电力信息舱', 'CAT-b0954672f40d4f63a66f95c24dfa5b8c',
  'region', 1,
  1, 'of0RjOuUBq+Ne7ujqVvu5DHXNJ03cD0o0xEaMtdSQd6ThzPLvOYEHZO6Qf3793U4HFf3NQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LN.02' LIMIT 1), '综合舱', 'CAT-b19990d6696a4a76974860a605a0621c',
  'region', 1,
  1, 'Fas2xDd6vh8ET1dESCwz5PsUT9DUoZb10fh4U0hdpUd2yeZcRHsDUTfIBCnS9w==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.HSDJ.12' LIMIT 1), '电力信息舱', 'CAT-b2ee61ad7a734767bcc4aa3d27e4ba0c',
  'region', 1,
  1, 'nJk3i+DRkWZwjEQBhzxCao+7yuidoSqttYOvdhN1BUugyxK1Ows4idrNJnjordlnE9KCcw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLB.GX05' LIMIT 1), '电力信息舱', 'CAT-b61063d0bfe7454c9db687720d6e964c',
  'region', 1,
  1, 'YZoi002+so8JFiKb6c/4fUj2rts7gZnqdgIuozJazli0cMETi6nTgWkBp0dOx3Vpl/Cmow==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.04' LIMIT 1), '电力信息舱', 'CAT-b6a20151ce9b43fab8a00223a455168b',
  'region', 1,
  1, 'seI105cYXes2xnTr6VhgFlcNaS9B284OA3MjobXHm+r/UYXslRBgEIOlHq1xr5yP11P82w==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.HSDJ.11' LIMIT 1), '管道舱', 'CAT-b811e815a507442596ae830e4e6cfec6',
  'region', 2,
  1, 'zetEixkpx4eqlwp0V5pmXtqKMBU2GZyrAQi6v5L4qE0V00okGY/dVS3MJ7hIfQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.HSDJ.06' LIMIT 1), '管道舱', 'CAT-b8db8c4e615144169e95a96a28f0b5d4',
  'region', 2,
  1, 'fi4Ln8fwO5aJP0J+XVh+9gOITsjprz2j/uVX9tjh8yD3NxQE7TpZeKXTJWzJ9A==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX07' LIMIT 1), '高压电力舱', 'CAT-b9b9f9a5596b4b22acd5636672c70c69',
  'region', 3,
  1, 'a3NEJUTMEtMJbwjoZ1oXvEAjV/d9hF76YrY8I0q7/O1O2VF3d3a4OYyOxiO1ih8p1zY0Gg==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.20' LIMIT 1), '电力信息舱', 'CAT-b9e3e9b4c9c244cfbbdbb7ad4dda716f',
  'region', 1,
  1, 'RZNPjPnJWgIyQVI+f/x43LgcsP2xnu0vmX9ra2rTxV6RujEoPJuPuwpPjt5yts88QQH1hA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS12' LIMIT 1), '管道舱', 'CAT-bae144ddbeaa4e928041996c117c7d25',
  'region', 2,
  1, 'hiUXsPGfdig9tDSePFdP60DD853e6u++TFCawx4OrVt2tRWTlk47H3UlovzpbA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLB.GX07' LIMIT 1), '电力信息舱', 'CAT-bbaecd39acec4e1aa216036d9deee3ff',
  'region', 1,
  1, 'SOBKqjXQhcR0pdKso5TfAaL1I9uAVuqRNb20nleDq4F39aEiawmXiCLdQkywrVdqJyDe1w==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.08' LIMIT 1), '管道舱', 'CAT-bc38bf9ceb0a48b9bef834c6289b9e80',
  'region', 2,
  1, 'zcinoInC43XhQvte/SbeK54O4uyTv1RVuKqN5j6sMd1x308cSQ2YrhFMig//Mw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX08' LIMIT 1), '高压电力舱', 'CAT-bdf2264b8b5d4215bf28daf404e2d84c',
  'region', 3,
  1, 'FCQoS0X+nfX2yON8Pzl3/WI2u9WsaWQdgwo8T34rSxRWLtWw2Lwuq6OFXzdMWZXauvPKhA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX11' LIMIT 1), '高压电力舱', 'CAT-bf57a5b3bf4b4edbbf964167602ed46f',
  'region', 3,
  1, 'lQgyphDcOtzdFLkDxg99Z2EGTc6ilRCAPV2Mu3imdTCalqcxFXz9wKpUtWKbq1+0J3By1A==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS06' LIMIT 1), '电力信息舱', 'CAT-c0975324fe384a7a8d15281e322c514c',
  'region', 1,
  1, 'p9VxjRLIrCxvtMjUV8TqvA6a7TbpxoHRoneAwE9yIK6mH/DP1YQY7y3IdHgDYHNAtM6PJw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.20' LIMIT 1), '高压电力舱', 'CAT-c1bff3cd142f4c50b01be0779f476812',
  'region', 3,
  1, 'LVPeGggQcxA6xD4WEx7ur77xSyHo6EeAyTa9TKhs6mxVT1DYTMWpL+3ycWdHZlEOBerwaA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS03' LIMIT 1), '电力信息舱', 'CAT-c1f8f9b71e5146c8a9d5167e1fe0029f',
  'region', 1,
  1, 'cmx2jKY2FUKPdswhw9HBido5kN42jok57c3N8MaQuOvZplNoFTKv4o9WUqQ/NXUhjkWcNg==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.02' LIMIT 1), '电力信息舱', 'CAT-c38542aa94f545d6a814413a62dbb0f4',
  'region', 1,
  1, 'HlbsjWrX+tIuFGxhdXwS5xco7qgqX2MyclJ6MGWezgkhJJw8IRtwqo1TndXbQ18LNhep5A==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLB.GX06' LIMIT 1), '高压电力舱', 'CAT-c38cba868a394691aa725d34ed07e002',
  'region', 3,
  1, 'GaifDkNL9qhJ1XEzUHGELxOZzWSbpLnhlg8SOZtIeG+P31zhE0/1nRqdu/18XJuCV2a0IA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD5L.SW01' LIMIT 1), '管道舱', 'CAT-c50f5e98ed254c42b2da99edfba012dc',
  'region', 1,
  1, 'MxFSXBJzF06buvN5cKe+XBpmZ+b5UbamTXxUG192HFofwyqFVj8UXizHxv/O8g==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX03' LIMIT 1), '高压电力舱', 'CAT-c85e2c36b1e8434aa306cb10f0cbec68',
  'region', 1,
  1, 'YqeuuM/sxkhXC1zZWfIcIl5t4hYGDPicsNg9tNNmqRt5zxSUpcTtuEET+pjdVl8KDlYWHA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX05' LIMIT 1), '高压电力舱', 'CAT-c887d6d6030d4088ac0dbca1f2101859',
  'region', 2,
  1, 'OC9S+u1IXNTCVf5mj3qP1PnVbMmGXlPUsBeZon67pDgIvuBHgKyNZ7LVEEqPZfREuU2JtA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX09' LIMIT 1), '电力信息舱', 'CAT-c97e9bb1dbf245c88cf6ff8bf8f8dd48',
  'region', 1,
  1, '97ayRjKLdfK6GkortG40gkJVxuj0rVx4N7eNGcxbwLQXDkn3LrtE3mY3iIUYADwrQS7tog==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.14' LIMIT 1), '高压电力舱', 'CAT-c9a7657522c64354b40992b0f85b9a9b',
  'region', 3,
  1, 'L2cm69VZsGwI3v+utc2sqU2fCJPYwXF/M+3oG8WDGYp0A2+6a8nj563TFZxRtOkN0cp3wQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS01' LIMIT 1), '管道舱', 'CAT-ca4255cf99c141a69480b3d39b09d907',
  'region', 2,
  1, 'bzXYS2W2s5asGXEP4GNx3IsQenOPsBz+RUTLA3IvqJ1vYWpRH4VfekOPpvL12g==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLB.GX04' LIMIT 1), '管道舱', 'CAT-cadaea90b3234a5d9b7838bbf127b508',
  'region', 2,
  1, 'cXQlQpWL3haLf5eq2cAdcbX7neyrrgjO9vAGqI/wJnMi90URr5mQeH52u4MA/Q==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS08' LIMIT 1), '电力信息舱', 'CAT-cadef18316dd446f9c6c043e8443a2e0',
  'region', 1,
  1, 'JnClw7ospffYeXKk54RiGiEu/JPI8cOaEHL9oX07ML/fmw/K43CE31osmE2FD8Ki7Ur8PQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.20' LIMIT 1), '管道舱', 'CAT-cafac67671c6429c9c2b4c7030853fed',
  'region', 2,
  1, '1sFNYFyi4JfBNZ4dlD0jMwSUalsj0aKItFMQML0fBBPP74Di1EW0Bmbtp2rWGQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.HSDJ.11' LIMIT 1), '电力信息舱', 'CAT-cbcd65b68ec441d2bb4f75aa88eb1ab7',
  'region', 1,
  1, 'tNfKYD39IcwzceQVlDfKytD0KsZ5cX4/OJNNniXR7QjJyOIu2n5M253ZIX93pqmIMvOPNQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS08' LIMIT 1), '高压电力舱', 'CAT-cbecb20b059243a2baaa98ade5b64b8c',
  'region', 3,
  1, 'aU+5bLC9fqEEbkYVWb+/oifzg6gDOtgjwQPBjOvONvWg9zNsdctNMtxzuah0SmnXRKWs7A==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS13' LIMIT 1), '高压电力舱', 'CAT-cc29dd93b9714845bf48949f1ea5d26d',
  'region', 3,
  1, 'uxis8Nq5/x6OhvENccPL/0wT8fqzX5TZsU09Gls9GtnKYF0wIx1oFU9FWjXDgtXIU6SQmQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS11' LIMIT 1), '管道舱', 'CAT-cc457edae4a445fb8b408a0da2f894a1',
  'region', 2,
  1, '/J7qMx3LYCZyU4TPUiEQoXY7XmRw4wz+85+RMIQfuOO6zm8uwW1cJldOav/4MA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS07' LIMIT 1), '管道舱', 'CAT-ccad791f1faf42b79f99dc6854c4ebf7',
  'region', 2,
  1, 'uDnf36huFJGAKH7wOb7t85qDsRr6YF4XT8sk0YVZ6NLX2vspM9rSW12fGTce1w==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS07' LIMIT 1), '电力信息舱', 'CAT-ce016e251dcb468696a29b25c6cad862',
  'region', 1,
  1, 'I/kLMCife2XERQQP7ATnJ+5gZQyzFE5cvBUF31P9uV2Ver0IZjDi1SY3VpKPORAZix6k+A==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS15' LIMIT 1), '高压电力舱', 'CAT-cf028ed99efc40a98f24699b0e07f7d3',
  'region', 3,
  1, 'Ic4iuXzYuz/tkAo0ajMmWM0s5MoOqCRFyfXIL8rfde4y6AEd1RyxulSR62NkPv116W1ltg==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS11' LIMIT 1), '管道舱', 'CAT-d270d69f6bab43beaee340c4bd3da6b2',
  'region', 2,
  1, 'en80+VuT+W1K7yXzNO+tRs2Wj3tlC+QMyVrS2B49GSo0WZEZDwdmWAiJyPKbJQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.TYL.03' LIMIT 1), '管道舱', 'CAT-d2cb118c78044e5fa920abf06d1d5997',
  'region', 2,
  1, 'PRfkvRyS3am0R3ZAhsLCq6LBzbkuvUo1H/AePDQZGc/6+wg96bc13WzUuKE+ag==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLB.GX01' LIMIT 1), '管道舱', 'CAT-d2f2ed903ac44b9bb9b6a48591c36422',
  'region', 2,
  1, 'QWVkEAQEkbC9+KCZJ8RSm8Phr0T7hkHc/1Cnsml3jrQ4lzDiHBTzYgwO3TlWNQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS03' LIMIT 1), '高压电力舱', 'CAT-d5b5fa31e1004cbe982480a06a11eab5',
  'region', 3,
  1, 'En1kt9qEJ0LqHH5avutzSHyDrrwZi3pK3jawWk7Vol7vOVwcNIUXrYPYkAUveUU6QjAZKQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX09' LIMIT 1), '管道舱', 'CAT-d5f69d0993fa44d1bbf871c89cff3aba',
  'region', 2,
  1, 'vyZxxEslTbpXquB8g6mzVpZ8KNIaMFtHi47Aiur8x+eAFssIIEkwfKvOfSfcOg==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.18' LIMIT 1), '电力信息舱', 'CAT-d61721f7f18c4d8d96e1bc417d7ce752',
  'region', 1,
  1, '+gh+lXFwP8WuQP9WApIkZ6lcGKpeSU7lVYCCoIBiDd4swLX6YmO+RgUoK7uiYl0Xc8tlzQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS23' LIMIT 1), '管道舱', 'CAT-d716ddbfdbca45049b58bc4af7407382',
  'region', 2,
  1, 'rWPg8o2Qj4AbR4DsZJ2nIJVZr/dxJ56DurLdNwUhM6wzFfMPCsXuZTKTad/Zqg==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX17' LIMIT 1), '电力信息舱', 'CAT-d78b0962903749048af83e31cd230dcc',
  'region', 1,
  1, 'hF1bwHIHw9Hhg3opTOxqXZxa/Y7yQOHiaNYN4wDYHVPcl3vufK69JrLNgy5ls0QlKetW4g==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.09' LIMIT 1), '高压电力舱', 'CAT-d7c73fcab7394b83a1d17515e527b39d',
  'region', 3,
  1, 'Nk57/+K6L66VxrnXtmiEspcEwe5zBFwJF95Syao3l6deLo9gskfcQG08dAjXq3ZIwbIleg==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.TYL.01' LIMIT 1), '电力信息舱', 'CAT-d8cac4b1b27246cfab60d4f3520e4393',
  'region', 1,
  1, 'G8FC5AGMhDisQW4tF+3stt+JkGeR3eY1g86rlptG09YeHZqPLj9Owyehd0vlUByGK1tbUQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLB.GX03' LIMIT 1), '管道舱', 'CAT-d9ddd89db135459eae472e828960e6da',
  'region', 2,
  1, 'moQR1wlSRPb9mqT7Wf+BalUo6pg+XCsUvyFGN1MGiSxB1GM4iAdrCn475xjFzg==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS21' LIMIT 1), '管道舱', 'CAT-da50b95b7d1d4f74b7e8aabcf5010546',
  'region', 2,
  1, 'hcwNRNy/7u1cC1oRRgJPzHsr70x9fPlzdCGWHjrp+mYPnA58y/j1M6d/170FIA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-1b468a9c7b8e44d7ad664dc096fff9e5' LIMIT 1), '管道舱', 'CAT-da8f81c6d04a46d69ff21db88a99d6e9',
  'region', 2,
  1, 'jwnwRqTHFbGrmIEDgTFy7WVCQNEGhgAoDx4FOuMdJkYLopGQ2rnKdrWxNajI2w==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.21' LIMIT 1), '高压电力舱', 'CAT-dadbd6597c8442c1a5e7542be8fe440f',
  'region', 3,
  1, '+lKWBrAkHDQdIMdIrHmp4qw3KdAgzbtcODkEjA376CpI/HfT3fKGN8q4rrCeQBoH92S8mA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.16' LIMIT 1), '电力信息舱', 'CAT-daddf70eba794136b0954ef9c6acfe3c',
  'region', 1,
  1, 'gFlbA6QQyx4GDzZ6UhNe4jTyt5MKfPO1L1kAfc5SS57lwFDHPn3Gam/nOBF8/YAI2b/ufg==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLB.GX03' LIMIT 1), '高压电力舱', 'CAT-db39e973ea4d469b9ee92117e0d5621b',
  'region', 3,
  1, 'pbxTij9EERK4gaMVD0MgCJet4Q6ccBsPHMNyXhuAC53oVoYBWD3yEzh5ebR7f6oMMmcqgg==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX08' LIMIT 1), '电力信息舱', 'CAT-db6ec72a35bc42819731bed3f2715bd8',
  'region', 1,
  1, 'wUGNIjRWJlB7iUy0YFYs9gfuiocL75gOodszvJKdgddTAbHVBJzH4TU55Te+2i1gdlKX2Q==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX08' LIMIT 1), '管道舱', 'CAT-db6f8c6a6d3b4b62ae86fc9d1d4c8cae',
  'region', 2,
  1, '/xg8GcCSY9KPhmvmIPifpLIhUAbXI4+JK6irvDcuq4ee2cyhnEKLSVZe1/hjeA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LN.05' LIMIT 1), '高压电力舱', 'CAT-def66b4e80124e2b80501794da813e84',
  'region', 2,
  1, '8bWqgNm/mKcuwuKZMKg7jfKbzWFKufhtH66C46VVJmpGqmdBqIa4/FU5rgRoqUjevmnD+A==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS09' LIMIT 1), '电力信息舱', 'CAT-df4b2f775c9b46ab96f2fba533a1bdca',
  'region', 1,
  1, '433X6ZwaRjqEiMbUf9uDwJ8WmiS413I43XEuVSzEoyaMlGLMKL2Q3tdp+m98NdaFLSI2lw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS10' LIMIT 1), '电力信息舱', 'CAT-e0dd0b69bda24d57882593136c94565e',
  'region', 1,
  1, 'YMv64Cdmucmz75b5qiWIwB1SnJRDVw0JXIX0IIdOq3fHa96axw/tXglSKA9rNZsb7s4Xvw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS02' LIMIT 1), '电力信息舱', 'CAT-e14736fd8b7549e5b3483faab4ac92e2',
  'region', 1,
  1, 'fElkMMBSbCsfw5BTli/STEoAijalwmW0Tm7Lcen8bv9Bz2MkJZUZNi32crloWohIuI/IOQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS22' LIMIT 1), '管道舱', 'CAT-e5de564b83a74fc4bf0b6f1c3f4fbc6c',
  'region', 2,
  1, 'oUBLE2GsDi2siP7SILHA5W7heE7QpjT65TFIztQbpX9+lyZpPhF1U9EwrD611g==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLB.GX08' LIMIT 1), '电力信息舱', 'CAT-e750133ba7664d68b442592b1bd75aa8',
  'region', 1,
  1, 'a7kJxw98ad8zHzxDdtnC6iWBgNib9nJK0of/8F7T4Pt1WI1Y2H7btCYacbvVPE3PacJmZA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-df84bc4e4c3d48be9ed1e4a1f075de56' LIMIT 1), '环网交换机', 'CAT-e8ceb6ea3eba4b7399f654174ac1c5fa',
  'equipment', 1,
  1, 'FBIs+ICepypK5COWFBbvrXf1AxbrWxBNExK15Y25TJCWfnlbc1Srbr03oDDx/v0cUw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.09' LIMIT 1), '电力信息舱', 'CAT-e8e76783a9394ef8bd5f0da18704e0e4',
  'region', 1,
  1, 'sw0eZ0fN24pXWt8UYcSLep0fnn39+BRrInqPkWP9tPMEBa66YdBSu+57ENSIXujhDqkOaQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS06' LIMIT 1), '管道舱', 'CAT-e9b46b25f8ca402197e8cb6cd2078657',
  'region', 2,
  1, 'TbgGBo6DaYrCyWxPP9ZRfc1tjeaPAoIEP3XKb6vp2TC3loG5i/wj7pt6IlVWkQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS16' LIMIT 1), '高压电力舱', 'CAT-e9b9c8d87fbf4fc68265e19c90d8336a',
  'region', 3,
  1, '/pb+NU/R+ghI0onIJZMu10lkcigPwR8o39Pb35YMUhi11m+XdBM6BS+tG+5Whi7xr5uHGA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.19' LIMIT 1), '管道舱', 'CAT-e9e9f3f489014e8cb79912e7915ce09c',
  'region', 2,
  1, 'Uhj62Eg12PP+8XHDdy9FsVwZYZ0xTZkjtLx2iEt0S3WKUM81KAUCK2vf3lBYAw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LN.08' LIMIT 1), '综合舱', 'CAT-ea7475391af84aef906f84645da0e735',
  'region', 1,
  1, 'g8YHWcvnatQLpBAK5hc6dGZWWvdhmRoCpG58z87ORaRmky8UUWKZOpZvXUM7Mw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LN.03' LIMIT 1), '综合舱', 'CAT-eb66ee250d72481094ea3e37eb620f29',
  'region', 1,
  1, '0vsVhFL/gHBRGXiSTz2Mx9oUir+kNXAU9M14dDzFml86n/nnkWOVkleGSoif+Q==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.TYL.04' LIMIT 1), '电力信息舱', 'CAT-ee581bb8d3274b249ef71dbc2913aaad',
  'region', 1,
  1, 'FqXxKf8r6EPnJiFFQ3VRvuBpYhuLtGaCJvtVjKBMVc7FnhPmall071Qiqs34MtzsNEAgZA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD5L.SW06' LIMIT 1), '管道舱', 'CAT-ef2294920aec425c8961cc9d36f4439c',
  'region', 1,
  1, '1wy+NrX9DjNii74q2Pqeh+TIuT+/rM1LUprccZZQGyb2q1z/nfclqmCVLajTrQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX12' LIMIT 1), '高压电力舱', 'CAT-f03b9ed40eb5484ebee521ea916ddc7e',
  'region', 3,
  1, 'JCny2rSGGm/dCt9Q/G2D75x0aI+8EC8hOlyYohqBHYbIxxO4ID02iKHJ8HacYNlNUu3IRQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLN.GX04' LIMIT 1), '电力信息舱', 'CAT-f0620b5be62e49ef9db6d562e10217e8',
  'region', 1,
  1, '53NNPtvnI9/rd0BeHpkhaPfd0yy3Zzigx5kkk42zIbAALAW+PUzAALLKR34AZt/7sTfiKg==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.04' LIMIT 1), '管道舱', 'CAT-f06bacfb02144cfba28a4e2bf2d45c62',
  'region', 2,
  1, 'dbecYeuRI7kjuxUpc2Ewg0v3aQsGz2/JdWhpTwAoiT6YI0fjBcjtHDqGRbueXg==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLB.GX05' LIMIT 1), '管道舱', 'CAT-f100411f29464049b71f89ef093a13b9',
  'region', 2,
  1, 'sRR6+SSUeHMdakuFgf7Dpoirr1+GIRlrIqtgYgDiT6fu4bG8TeQzG5tRPk+HHQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS08' LIMIT 1), '管道舱', 'CAT-f2cdfb468eb24e9e8366abbfc38416b2',
  'region', 2,
  1, '+K/rJvuA6gpVdoEHjXvI/h9Jk9mIGX0kLwHmR533rtGVb+4W+OhgEOHklUJPOA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.HSDJ.09' LIMIT 1), '管道舱', 'CAT-f2e57357f914488490caafbacd3c11f4',
  'region', 2,
  1, 'XB9GuuyaH0FAgB2z6JxF4M1jiQghSsH8GXMAy7OK+Ij1wEj4lOgsI4gaFL8b5w==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.23' LIMIT 1), '管道舱', 'CAT-f3078d892cb14d868cc07587e76b6c1f',
  'region', 2,
  1, 'ok0qxyzAjvjuOPHfGVAYPEe0TXNXp1O4jVPNyrZ7aExrNioaEuzHxAaKVj1VLA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS16' LIMIT 1), '电力信息舱', 'CAT-f448e1caea2b4b32b4890900ed6cf657',
  'region', 1,
  1, 'cNhAOqWNwuDtjp3ueXIfpbRpsxtSD2EluGAqNM09XO7Ijpp1y90MmYncZGlBRHJPowPUcg==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.HSDJ.08' LIMIT 1), '电力信息舱', 'CAT-f523ab004a9b44aab516bf71cc9664af',
  'region', 1,
  1, 'x8Jl1naP5sFN80moZu5E6P6BMZHx5Z365n7q1goYbZDDOuNIdf6unyD695k4sgzZs2wVbw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS23' LIMIT 1), '高压电力舱', 'CAT-f66465507230483b9218e4171c412fd6',
  'region', 3,
  1, 'LwKY+hTIXIFgY+GfuJAmlNHkw3LpQyWGItzBAtvi7hIERZx2tY8U75zGqvloU4fIRlBJXQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX12' LIMIT 1), '电力信息舱', 'CAT-f6d1f20ff8b24eedbc864fd1721ede3a',
  'region', 1,
  1, 'IJbzLcBzfZacVpyEXVDATagH7ZVTlyAn51OUum2CfCsFaMJCUyuVLOhYC9GcRW0HuqR9bw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX15' LIMIT 1), '管道舱', 'CAT-f7f44d1d61ed47d0889afdc3ef82efaf',
  'region', 2,
  1, 'iiK7GgcN/I0Dow+aAHJAc9E/4R5ZrmzTYU1J7bF+cgiQXaLmmf/QX0A04DZoSg==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS19' LIMIT 1), '管道舱', 'CAT-f89eead01e3948588016541af59864bf',
  'region', 2,
  1, 'WwEpmwuqEbJnrE4KL8xRPJMKLidfcRwoV0Ob4Ak9/Mb9dv0hmFWKBh4VeXPRhQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LN.04' LIMIT 1), '高压电力舱', 'CAT-fa433c9ff35a4b30a2e72054fe2998f3',
  'region', 2,
  1, 'V3iObj+B7KIfYaSPbLPrjyw72Yj9M5GFwk/Olaasz3rtMudBuEMovfgfrlraSdEbkhCS2Q==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS05' LIMIT 1), '电力信息舱', 'CAT-fa4b41a530ae4f12bccd5aff403207e8',
  'region', 1,
  1, 'ceKxOyWFSfxLKP+VUMBmF8zRxer+wpnIig+zp7iDVqJgn1EySHS6yOaF2eYu1qAHuj2gqA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS08' LIMIT 1), '电力信息舱', 'CAT-fa9dce403de544fa88fd368cc6dd9748',
  'region', 1,
  1, 'KH1e3f16UYQ665k2rzv0yMVD6kZw5a22JDCukNoOtWQ8gG/JP1B7dHOIVmHyGE2sYcZaxw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX04' LIMIT 1), '高压电力舱', 'CAT-faa2b0d6a0ac4e89896983a17dde4c51',
  'region', 1,
  1, 'ZOnbad1lgDTv9F3TbuNMPdnZhtizZPKEmzAodr8tPbW+gBIOxCxCpL0HDKWP4EgeqULjnw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS20' LIMIT 1), '管道舱', 'CAT-fac14cfd37e549eb8d5a0a03273e3a29',
  'region', 2,
  1, 'WR4EY8AsFTpKNddQnu4Ahm74FsPGmhRw4Td7GkiKbsLhmShx13GXclkmyC8Q5w==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS02' LIMIT 1), '管道舱', 'CAT-facca3388fb24415ad6ad2577145a776',
  'region', 2,
  1, 'Nju6A/o3UHuL399lawcYIFjAlJv0k9IJkvJUoJTLW/G/EI61YwHaVRn3C9byYQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS04' LIMIT 1), '电力信息舱', 'CAT-fc23373544a14e3a832000ffad8adef6',
  'region', 1,
  1, '9siDCsESBOnVEFo4r3aSCR4eiS1mgbNKwlgjO7tKsdcxnMiMFKVn5SQZqbwT+Q3ei5r8CQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.05' LIMIT 1), '管道舱', 'CAT-fc622c145a6e4cdbb0c2f643ed6f5ced',
  'region', 2,
  1, 'TnlNIE50jmFMVXQcvf/ilc21jYa+xBoUi/hARnjt9kbezpbljXdh1lHVDLAMbw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLN.GX05' LIMIT 1), '电力信息舱', 'CAT-fc95044fe7be405ab616cf159ead4aa9',
  'region', 1,
  1, 'c40R7tyCwRWOB9cRcTv4cJOdqQnFVqjvDTg2OXudqglO3wez436F0JmHnLpJt+rZ9Ox1gA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS03' LIMIT 1), '管道舱', 'CAT-fc95407ff0854affb9be2ac23a757340',
  'region', 2,
  1, 'igLYhmN2xBfSUul7I7cktd3JxTzHcGYGXszUhk6gDcl7rG0d9sg1syXKstKW4Q==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.HSDJ.04' LIMIT 1), '管道舱', 'CAT-fcabb227ec0c4bc8ae1670484c5a0dcd',
  'region', 2,
  1, 'YUNHSBye+NBW9mdM1/hFR5NC70qaPVHJak+0VIybB5EYoNirOUB4DHjMLBc+6w==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS10' LIMIT 1), '管道舱', 'CAT-fda11d2d617b4062bf436da789dbbfbd',
  'region', 2,
  1, 'TmhB1AmN2nSCnPVzK1txtVA+r/Nw/2b7EJOz1qieXsrU1r/ECT9hnmNAZoEclw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.HSDJ.09' LIMIT 1), '电力信息舱', 'CAT-fe0ca57c95724a529d82d879f0a95e91',
  'region', 1,
  1, '/pNZspp9eXDsai9xynbGcZ5tBJFlEgwiNBbdYbSPrWs+pvzdiryLLFU1GnuBa/FfA7XjMQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.09' LIMIT 1), '管道舱', 'CAT-fe5dbbcd5814434b9931408aaa7014d3',
  'region', 2,
  1, 'xy1QZE+Dby4YbRLYr/3EVDOlZ6/L5tksHVOEgt2SwQJNUTZVc9++kxADxg5VQQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LN.03' LIMIT 1), '高压电力舱', 'CAT-fe781ae3356f4244b34b4f5d0b8ecb67',
  'region', 2,
  1, 'IoxZgA13muM3ydvZ4sPGZSsvHv6QZXIrUmng5devQGHTdjHYf1cQEmlb4zgQ+Qyjr2tdcQ==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS14' LIMIT 1), '管道舱', 'CAT-fe7f76c2f9e34ae291ed8ede3dca6328',
  'region', 2,
  1, 'yctff37SDh4ECg0W3gj3wgnXFyuOQEvYEPyFfUQOglEvqnk4htsHEpC/xkffyw==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.23' LIMIT 1), '电力信息舱', 'CAT-ff61e388b5d54960926b54f5f887b673',
  'region', 1,
  1, 'xfUDhPrEA1nDVVimI47Pfq+ewV5qHb0nVV99ZE5jiFSV8tlX0Hdwzlq/201Qmh1JkKgC4Q==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-1b468a9c7b8e44d7ad664dc096fff9e5' LIMIT 1), '高压电力舱', 'CAT-ffe82c9a49ef48bd831e7a8a2d3f10d4',
  'region', 3,
  1, '0u2eN+jlrPAKCQkWRicH6WauMjSWSEMUpkU2cwOd3IBsqHGsGymQYP4aaIDD6uY+izY5oA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.08' LIMIT 1), '集水坑3', 'CB53D15240CE09DC97AC2CADECF2B3D0',
  'region', 1005,
  1, 'JUF712zI8d3bOHjtWNgKorgpoRarL/YWeyvyXVLgxZTWeenG1w8JncR0nN6wpK6UR7I=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLN.GX07' LIMIT 1), '引出门1', 'CBB6F8F94C10943B7F946F8E96B83040',
  'region', 1003,
  1, 'jn/KHrpU7+q1VFJyVDeUT+gve+Qed3zSb4chlfDRWM6IYB5qSvQy8+LhywUn6ffl928=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS15' LIMIT 1), '交叉口1', 'CBD4728F49B45BC7040E73BEEC0072B3',
  'region', 1004,
  1, '4E9HNyF56vwr5SjYKEEyeOxY4cyzdAneQ9dNGdjbkqUKsgYzU3QV7YPs3giyv0kH4ns=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX01' LIMIT 1), '端井1', 'CC2CA9C34282914AFFC80491A4902F02',
  'region', 3,
  1, 'Kvjrj1gIvP51kOhkkbrNQJ9Ih9oDjbzOhVzj6RzSm2QjNZK9UK/qfELqdzen3sI=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS03' LIMIT 1), '集水坑8', 'CC3C59FF469F132CFF06AAA8D1A71E82',
  'region', 14,
  1, 'M2TraEgrmCckxvHQwZF1b8Omi8dGpE30WvqKaqVszU8ZoyqIDEKusDSascbkx2guKLU=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD5L.SW01' LIMIT 1), '自然进风口1', 'CC53C06548F95C02BEE80B9B488D5895',
  'region', 8,
  1, 'J/3dVvyLXDkEii4hnut7fqijXQSRekbheNC9Jgs3+zrQjhlcaTVIYCgkbMiM6dn4HNxVq2lVGgk=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS02' LIMIT 1), '集水坑2', 'CCD622F249A5122062922288A249C3B0',
  'region', 3,
  1, 'psrDnG9Du89R/ly2yXAE03gj4gsz8OVTAiexGlBzSUz7x0MuyP1gX/LD+vFDFco80jQ=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX08' LIMIT 1), '集水坑2', 'CD4BDD4144AB26263CA0E5A47934354B',
  'region', 6,
  1, 'nF1vQkWCHtK0VLWvS6dnJxtCAKZ42TKIfWpAxYZ0fDU/PrLpBDqXzeTSB/kL84W5yLc=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX18' LIMIT 1), '倒虹2', 'CD73D8854B4767966A30F09935822A9A',
  'region', 3,
  1, 'cWTkOnCwPaHEJ+vzVQ5uoyuPr7frm7WqxgkVm4Ld0/hmPgQurAUgiA5IQb1WgUQ=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLB.GX03' LIMIT 1), '吊装口1', 'CE021C6748F16CE4BFB10EBB455F3B44',
  'region', 1004,
  1, 'zaVMty5jGSBPA6NdqBqOJnwkYbrn6qtDMkQ8fX0wI7fdhwRzklTfi04ufWkvI3vrP2o=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX04' LIMIT 1), '机械排风口1', 'CF1401874AD92042DC6505ABE3231D2B',
  'region', 2,
  1, 'mPU4tRPBeEr0yVWtTgMLmwup9Ug6whLtcbejBPDaqbSEexgzPYLUu8SSt+/c5uLKiGz41qTdwdM=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX08' LIMIT 1), '机械排风口1', 'CF5EAC1D4BB63FDFB360FE816A9E3DD6',
  'region', 4,
  1, 'sBMv7wwB0PwRGs/UEdrmkYXyfejAeNJT/5l6P4xWplskdizpaLWKDQ3qBm4qNsMH6weadxRs6ZU=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS03' LIMIT 1), '集水坑4', 'D04DD48B49452058D45517AF4B120832',
  'region', 6,
  1, 'oTHK6mpHVl7huWIjoGlbzRWcDOusXSu85soYhJ6yh6c4y7522ooyDBHsK592OCkOh58=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.02' LIMIT 1), '集水坑2', 'D11EAFD543B590C6F6CFAB9D7B990BF2',
  'region', 8,
  1, 'g08tnkL/rGFl4UjLi+PvbGDUy9snJfMJqLV3liQyS1TGxYyCs4hFzSs91M3m6vlRjfs=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX19' LIMIT 1), '端井1', 'D1C577DC4345BCF6C5610FA9AF9CF04B',
  'region', 1003,
  1, 'ngnb69T9iw9m9xHVsPoEo6TKgCEdxOkrqI/Y6ZJZLJIzy5r/7akXg5Z9SdYxWVo=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS04' LIMIT 1), '集水坑1', 'D24F0BA64C99E517C0130292C43848D6',
  'region', 1003,
  1, 'PdiRtzh4pOhH8UsH+7vdUJaQ0sbtT6MGktM050OYSo0EZyQ6ho0VsqRKNCTkRAQ8Ivk=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS23' LIMIT 1), '端井1', 'D29B6123402E939845B1C3B11A60E6B3',
  'region', 1003,
  1, 'PjBsZs0CH16aX98ZVIlx1OupPQ5OuqhsmKnGRld7IfXzHgESYrhkzZkwwQkbPD0=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX06' LIMIT 1), '自然进风口1', 'D32D439D42F81EAC308EB9B2C39371EA',
  'region', 1003,
  1, 'FMbA8upegsDiaieCr+itOKAxiGVzDJUyXxIKuuZ1pYpifncW7i7bOGrp7SkpayDc7dFOeQOTCYg=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS01' LIMIT 1), '集水坑1', 'D3FD92804D2A434FC826AC98708C50C7',
  'region', 3,
  1, '+ZUpF828D22sXbeX2Bw3vLqmdwt/gwu6a5OC+h9+4SM8l2mKTisGDtKG4heBNuwTi34=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLB.GX08' LIMIT 1), '集水坑2', 'D4BEC131425D84DC052C23B5B451CFFD',
  'region', 1004,
  1, 'B/ZU7ScdJzjiuZrJK+i6fi8L5o2Dn4Qxftxew4bfDg3LQk7nvbKLkmhPRp80aceGF+A=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.HSDJ.11' LIMIT 1), '出入口1', 'D4C96CB148E214E19D49E2BDC18FACAF',
  'region', 1002,
  1, 'A/5HHrwlbgRT920Yoq0qU00oxENtTV37F01u/SfP1fTEwJ7X22g3odIoPEX0kD17d/A=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS01' LIMIT 1), '集水坑2', 'D4EF704F41A2F9E3245ED29A3496FEF3',
  'region', 4,
  1, 'zkpxguyWvpUHf/AVlpseDMSJ0jYKzLEjXqcdHF5T0ktxOSDV05ZoAtaLJelPQca6tqE=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS11' LIMIT 1), '机械排风口1', 'D5BBEB6F4204C517570D1A9E76D1AEAE',
  'region', 5,
  1, 'd0fEHjmAAAgGw1EQX4pSup+zOsy998nRL9uApRJNbuEu45TcwVXiIUaTaHkjZb712gwvKYTPeY8=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.02' LIMIT 1), '集水坑3', 'D6B180754190AE21FF859CA0E5EE6E05',
  'region', 4,
  1, '/o5oEt3XmhXJX3898JtvOl2hMCY2UAyq9N+DRWWHWHncMkXmaVPbQGfUo0jD8f9Tpig=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX07' LIMIT 1), '集水坑2', 'D8D0D6664C899D30F19F0CB4A044248C',
  'region', 9,
  1, '2bqF3v+p+u+0kpp+7jcAAsyfucy9MSmZvN2alfmhnKB7tiYLGZGHeisakL5TI13BP8c=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX17' LIMIT 1), '自然进风口1', 'D8F458BE48DE9E0C906CBEB80493A65F',
  'region', 4,
  1, '1UcTr53OheiACgDyi4s7l1nvST0iXYmRV2ABKwfqFxE7WIJkWCUjpETH/wF4smxr8fNhKvvNnIs=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLB.GX06' LIMIT 1), '自然进风口2', 'D90CD6E24FEBB22D72C6ED8304A8C5A2',
  'region', 5,
  1, 'KM3PDaJDugdX0ZlzxLVMhsHJU+ROIIP95P3MokgiuqenE1z0FxBhewPvdzvvFe6bj/k1b6wdQUI=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS19' LIMIT 1), '自然进风口1', 'D98FB996413399D2E47D3792B1F5A5BC',
  'region', 5,
  1, 'tDCpEEw0EziuQ/TM6//BEyGQk5x8sXyRHwetRvdXNpg2aN4h9LFSPePbGcdaMHttfAbkas2FCLs=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS14' LIMIT 1), '引出门1', 'D9DCBA584DFAB738C0C43B8DEC971FD7',
  'region', 1003,
  1, 'wuysRw5caErlWJMiOl3kIJKrdFNASDLlYzTzZtJJwhpxbL3UFSpwLJYZ/YODAZcJDs4=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.18' LIMIT 1), '集水坑3', 'DA3F29E149B09A2401FF75A37662BBA3',
  'region', 4,
  1, 'aPB2ruJ9BIiHow92TISCdSvZQwjuje9xMxVSnzX+gVZWyHisZZ2R1wb0lwVqCmnnYw4=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX05' LIMIT 1), '集水坑1', 'DAE86FFC4E1289C80FE06EAF8DA5BB3F',
  'region', 3,
  1, '2QeHjgDQHLA0Ej9Tb3aTz0Z1zesUv6alo1+ilHzBrlXwhmdTBgMsRmUVffcxTiHA0Ig=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.HSDJ.10' LIMIT 1), '自然进风口1', 'DB575085405619006E3193AE18609811',
  'region', 1003,
  1, 'tJJ06UBSPmqzGG2eugtE17cXuNeRfcNYLBD6N71fp7oJaqL70MyfEJdcXwKic7mKtHtBRcGB9DM=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS01' LIMIT 1), '吊装口1', 'DBB7B6914D2FF8DB171CBCBD4135F03F',
  'region', 6,
  1, 'jDsAbHapJ7E7BAOXEPCR+aXqT51yQPJefkvpzFKUUAsTinAcdeQyN8KeTtOemC+vUIw=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX18' LIMIT 1), '机械排风口1', 'DBFF49094A30A6A9374415B63006E876',
  'region', 4,
  1, 'sbJIwTssMjR4zpm++llH2Q/3JrJQjVv6SXjLOP3sEVSr6NJvV9ITwkMutJD13+Mu14j/LFaNykQ=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.07' LIMIT 1), '吊装口1', 'DC872A124481DFAFD0120F8B35181356',
  'region', 1003,
  1, 'xIBcQs5rGWSDe1s0QX3/ajjF1jPhUhCnjRFzBhcVfqablBMKIfMbW2WJc4kHlvYR7F4=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.HSDJ.06' LIMIT 1), '机械排风口1', 'DCC6F3474D7A217B21DA1AB446F1AD57',
  'region', 1003,
  1, 'Nga2s7bL1NGWCVC4wa91lIAA5ovJpD6y0bhYJ7uYOeBVeIQ+LJAFPfHE60GJ+QX2tqVgj62bQ4E=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX02' LIMIT 1), '吊装口1', 'DD5890F54C628A49023E61AABBEAA594',
  'region', 1001,
  1, '4AxN6BtBQlJAs/SL+MLdFNOvRmro4D52WHVIG1Cfi5XDA4lhLvkdB2pospE4G8/Vp3c=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS10' LIMIT 1), '集水坑2', 'DDC0AE3A4C092766927107AE014579FB',
  'region', 6,
  1, '8mfGVpDONbdakG2c+1wSlUt7OwKxfTwzCPuBfXttbtnlDFwiIb2pTWv+fjL2xyai310=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD5L.SW07' LIMIT 1), '自然进风口3', 'DE5ADEE44E095BDE9EA8A590B51F350E',
  'region', 5,
  1, 'BXLw/3GsWPtASFi0ytIiM/j0OE+dTi4+ZhA5JZS3ye30oMxD3aiVtvd3UNnMBKXnbNQmmPt0vDY=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD5L.SW05' LIMIT 1), '集水坑2', 'DE75AA714B5D6E3C088947BE8D6DAC25',
  'region', 5,
  1, 'm9PSm1AEP/WDaz7gMNUgCMJ9nIrg1HX2+EXm8ym7+qhBMxXBFtTDzMCym6o8lOrJ74o=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX07' LIMIT 1), '集水坑3', 'DEE2775D4349CADFC2B1729BFF3EBE5B',
  'region', 10,
  1, 'v6rzmtCAGmqxvxmAwCmSukJIQQKUEoNPkkrdD7yfXm0Rekjkp2UjQnbl57FXm43Oiso=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.06' LIMIT 1), '吊装口1', 'E01BDBB74691DC741703C5B6E2CFF0F5',
  'region', 1003,
  1, 'eVFsCv6t/NikEODpugJPv+madBI+nQRnH2XdtBsNsayavNBHpVKz5cONzw/6JMBilv0=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS01' LIMIT 1), '集水坑2', 'E03CE3B242138FDA807938B8EDE8577C',
  'region', 1005,
  1, 'tA44xJPhFzdnGmEwGKZoKlMM/5AiBY8rLYcHYq3XJ0Vo7h9ef2L+J7O+Se9orE75gZY=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.13' LIMIT 1), '集水坑2', 'E044C36546731F6988179CB38DF0CDC6',
  'region', 1005,
  1, 'ruN6bG9NlufyOrb1qLPzfA5hWdm0k2/DpoTYMSzi3h/Bf2UxYr9NB9Z4onHtLVrFC4s=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX17' LIMIT 1), '机械排风口1', 'E06DE4594D8C9B780C59BF9833013CCB',
  'region', 3,
  1, 'X0gDT51PcnYtzhDtnOFVNv1/VRt12edtwHLJfTNVVkH8YNFuQgU8K7nJ+7MD5HpSQRLwH7ZIpao=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX09' LIMIT 1), '吊装口1', 'E09100084F333DE32600769E10BF1F47',
  'region', 1004,
  1, 'BSsXCq3s8KJU00zku7MnUaflx4AIFhCJnUt96/tpMxdu9AhnXKOGa0HdkR2STrsiGJ0=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLB.GX04' LIMIT 1), '自然进风口1', 'E14AB1DE480D435FB731718837449246',
  'region', 1004,
  1, 'q8VgkVDG4SDzW+2Sp/kddcG/sRYiGRMjL6JB03DljPuIPdBUVO5cvnGS+b4dQlOis/L5N1FfqN0=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS23' LIMIT 1), '吊装口1', 'E16E0A50406EA02DB6984E8D4A40B3B3',
  'region', 4,
  1, 'qsSiYUE2CltmKEaynHvj/ASnrHlECG9AG/AnSUm4cKWfLKbovEfZAZHb+ZBvszkfu7k=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS17' LIMIT 1), '吊装口1', 'E1D2CF8945E0DE9FDCB2F9B88EB8F275',
  'region', 1005,
  1, '0/KOqzB94AK9dLC/G6vcWaZFYnG9qo05JFThXJXRKNoxC+S/X2juzceer9WN3XcEsd8=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.02' LIMIT 1), '机械排风口1', 'E228F3FF4BD67AD50E0F278DFF443467',
  'region', 5,
  1, 'n8jm4A9bpxCC9mEviX8KoKGRNQ82wRS6RU85OxAVWSAO7drjpA+o0h+JlBmsYyCRZHvQK0FpmFc=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX11' LIMIT 1), '自然进风口3', 'E23E79AD4793E759789983A7CB8C4733',
  'region', 6,
  1, 'LNKTy0KKCznN+yjOI+aqU1tOlXfoaPeBBY/2lT0WcKnWv0Ps9hdkjy84HvvQ6L477JLxycxPA8w=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD5L.SW04' LIMIT 1), '自然进风口2', 'E4E7785B48F34BF005D060813696F0B9',
  'region', 4,
  1, 'ZaMIsidEKcVwmmYC+vZQuR6XdTggU9j6J78pknXtG8j2nfS/b375t5V6toc/vNqw0cCFqPr+ars=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS08' LIMIT 1), '集水坑1', 'E51B283A420B06839FFB8EAE5CF33BA2',
  'region', 4,
  1, 'c5gRZ0DT30+dVJE/0cZdGqr7i3ENJp3B+Mv/0I+omj7q//Fh6FKULL5UM8VCZp/2E9U=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS03' LIMIT 1), '引出门1', 'E52DB6DB4606A308694951993371CF4C',
  'region', 1003,
  1, '0WU8wA/M+H/vhf0hYpRXBbqU4aUwEEFV6eV/Z4pvUzsAtBRsx4Y7KnBTHmcuSUwaqCI=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-1b468a9c7b8e44d7ad664dc096fff9e5' LIMIT 1), '集水坑2', 'E55F0D6748843DF565A719ABC16C5A08',
  'region', 5,
  1, 'a/j4IaaMtEoB80fJKvTmTkZSVtaiyOP5ZKw0u26xyVixyikxzIvgPmK2/ytrZCXdN6c=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX10' LIMIT 1), '集水坑3', 'E59570284D9C279DDAFC82882299E5C3',
  'region', 1005,
  1, '8toun9F6O1tWcAdo4CHe2gdlr/3La2zzhBNfVDDVwZ8OTxdYzY+yokrGFrk8Z5s+XHQ=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS10' LIMIT 1), '引出门1', 'E6415F9245696AAA06E1598E35F5812F',
  'region', 1003,
  1, 'lGmyzVjNIJ10ax+vJ8Zq4Yw3MD2gpeKXbZ0b5wH5DlGPnd3BMtz9bEPTYYfMeMl0tIA=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS07' LIMIT 1), '倒虹1', 'E6A0D3634822107CAB06B89A6E7A7A7F',
  'region', 6,
  1, 'mHVdRQos88rYxkxg1ptVDrICBPyXeuqYYE3hmqxRQTMKgNVNRr6o1msuQad+/AE=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD5L.SW03' LIMIT 1), '集水坑2', 'E6ACA2E344AC93AF8E363D82D954F7A4',
  'region', 7,
  1, 'LSGj8c4dzT9rveoDu+JVZBCNmRuZ5gglDbWqOXmWV4H8XQtOJ/8vcy5LC8iAWyV3uzk=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLN.GX04' LIMIT 1), '引出门1', 'E6CC4A5E4A3C6C777894ECB3FE78AD12',
  'region', 1002,
  1, 'Ykvdz1Astrm3I2swRuDD2tG0yDKV3w13tX6OcrXdxvOkQ93iyDzCl+dk8OIsnQsKFFI=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS11' LIMIT 1), '引出门1', 'E6D3078A41AC2856ACA0A4A8259B812B',
  'region', 1002,
  1, '9jcwiKlp5Fd7m7hADI+E3MREJV5l+yeNor9m1EqVyDmGg6o4/HVfPyVL/OAABzrgvU4=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS10' LIMIT 1), '引出门1', 'E764A545420D53B1C184028F591F8473',
  'region', 1003,
  1, 'vA+Lw2R3fnE3ws6KcWcV8lARPQ1G6L9giNGONlQFmQN6thgB26z+LKjJb7So3ySbYZI=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS08' LIMIT 1), '机械通风口2', 'E765CD2E40ED8FDDB6FC30A91C3D6CA8',
  'region', 11,
  1, 'BUfQDjn5X8oV9WegKKwnZnGcjUjSluLyQAVE/t78Hm/sy5jC8nK6kv8zqsa80uYBlPM3XELutfo=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS18' LIMIT 1), '引出门2', 'E7A41B8044CBE0A7A0E9C99F98234830',
  'region', 1004,
  1, 'gPul+b1yw+rTGepXv4dq3/lkHNtQY9vSGGNlxvCgbxhYoO3Jw7B6jk+PoP/gVTjEkmM=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX09' LIMIT 1), '集水坑3', 'E7F9FF3C435278859DEF66A983266069',
  'region', 6,
  1, 'a0+gIon/KqBiMB09EjSGdddxpLFL57mHcPL39EQyN7WChDHbHwjIP+puN9dwbJHnuGo=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX19' LIMIT 1), '自然进风口2', 'E928226B41AE51AE482306ABBAF6F432',
  'region', 4,
  1, '/zMwetm4a3oe/Y5IBbkpjNDA9kPmKAnNEfJBWHCso5vgEaRAmEDNoFv3zH2uNk3x9Y6YBnPzlFY=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLN.GX06' LIMIT 1), '自然进风口1', 'E9613B494068EF40E18A409C92ECC98A',
  'region', 6,
  1, '6QBcfq7fTM81HCUEseBXonJKw7GSAfAQ6zpE3b7NKGM4vnLgTCzKe6st6hTZ+T55Xd8noMmWTjk=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX19' LIMIT 1), '集水坑1', 'E9CDEB6F490E9261B0E72292CA03AAA6',
  'region', 6,
  1, 'yWN59C+0wL2oqxq/w7r8+LZ2xns5eaVQYqJXuUjzcQmqH2/tK+WtxeNB0UH7bnjRbKg=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.22' LIMIT 1), '集水坑1', 'EA061C3C45D1B34E00C141BD356B6F66',
  'region', 1004,
  1, 'tHscDFx36bDYYdPbVRtwgutjCqIBHLaIffLYWQXhEzAIeGCKcyzcVEIGC5DTxsa3x24=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS11' LIMIT 1), '集水坑1', 'EB0637CD45C4F671C1B603A5B0DEE7F2',
  'region', 1004,
  1, 'H7nqG9CGAZtluCt9qqBdM9b1jeAX6dRs3pGNhq7az78D5kk/Vh7m8BIHl2uS2YXxBoc=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.18' LIMIT 1), '集水坑1', 'EB723D914B2B8ECF522956B8798FA3DF',
  'region', 1004,
  1, 'iIzDy2wLsE/zucOQ/UwOZYSbphxbVIF2zwjleOBzruPwka5FZ2cjtzHqJw+ajheMI8c=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLN.GX04' LIMIT 1), '机械排风口1', 'EC4F5C7F4BD3CAA4F4B0D38AECDBAA5F',
  'region', 5,
  1, 'iqHdVr7ztQ4PD5hA1bnT4aiyfGCbFaDpNu5YrA+NosR3eyPZWw5TX3Z+vn7KzxQ0o87nURBqhes=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-1b468a9c7b8e44d7ad664dc096fff9e5' LIMIT 1), '集水坑4', 'EC54BE904D5984159AD4FFAEB3804E58',
  'region', 7,
  1, '0ZwoU0aB5alIplFQw4XOfJssecrcWR5Q65lOeW25rzUR/ANwckr1lpuoV+R8j49pXgg=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD5L.SW05' LIMIT 1), '自然进风口3', 'ECF35E61429D99E50B6F588F74ED098E',
  'region', 6,
  1, 'kcM37ZS6BGj9zK58XbpVHzFSrut7gk9tCpGnJo5cUafYgX404Ezz6oU6UfoOy5x5rK7OxZptww0=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS19' LIMIT 1), '引出门1', 'ED04EE97470C05E088320BA8DABDCA50',
  'region', 1003,
  1, 'uJL/h7vVBLHsgAhEXf0VpFbB/agbgy6cEEESZXm5sjJlDDiquOk/0TQvI/S9Oh3yweM=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-1b468a9c7b8e44d7ad664dc096fff9e5' LIMIT 1), '自然进风口1', 'ED1DE8B34D15EE964B3DDD8B2B5C749A',
  'region', 10,
  1, 'pKMznrDMTJNXM1e2C2+FCV4lq3VYef4T+jEWgPkFKGTQcHjCqLlHTU6gxQZiaYRZqnn22dakogA=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.15' LIMIT 1), '集水坑2', 'ED1E85074D40A269373B2CB4296304E9',
  'region', 1005,
  1, 'uxvQSI1n53/g34ZsBAWFAbIlFYRbreKz0/DnOs3kIroXytOc5pPQszSnE6jCK2MAnVA=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS09' LIMIT 1), '集水坑2', 'ED4C46554448CFABE242EAB5026DD0C7',
  'region', 3,
  1, 'KSZeOqtFeNqPOpBElhYlMySOa7Zd1o72E5iC/ZCDqjLNDR4HNSGbq+q07iCWg1Sf8tw=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX03' LIMIT 1), '集水坑1', 'EE0F9D0248F03986BC24DDAAD0CB4D42',
  'region', 4,
  1, '0RQq18+c5SE8GqA2Mw3G60ZZUiYUlxl33cYlJgLZSJ4UU5U63aIqAwDrF8IAIcF1hdU=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX09' LIMIT 1), '集水坑2', 'EE6BF49F45FEC5328EB55E91FEE425F7',
  'region', 5,
  1, 'sItpeG/rS42F+GnRTFCGCImuLH2kj3ojZVaT8ZbshS8LP2dJuF6x8k5ajaErx4C+gd0=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.04' LIMIT 1), '集水坑4', 'EE8314014372132E18DBBB8A8D63C0CB',
  'region', 4,
  1, 'jd8NwnqiToXcnfOJhUwOeazPJkZBn7TY27sKpNczPKFtbRaek2xlSZvi2cyeu7ZdCRY=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.22' LIMIT 1), '集水坑7', 'EE90234147164A3D51BADEB301DC6B01',
  'region', 8,
  1, 'w8Ydf82db9JTMZNi3cdr/0o29XDdxBEUuYUGL4VYJ8K7KWGRBn460Q+NswRXZjDUlCE=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS20' LIMIT 1), '引出门1', 'EEC2057B47931C06647865B2F8350D21',
  'region', 1004,
  1, 'bcKXtQt1r5LhrBD39rZ5moAk0mc/2gi/bonVvQwp1tf2b6oW+e926CGh8xWa/Jt/bP4=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS07' LIMIT 1), '引出门2', 'EFC487564F8B9C258FDAD6AC6B32BAC3',
  'region', 1003,
  1, 'rdhs52EEcRYWtjedGXhAPKnDUDsFjDT2trIjk866lvTOcPBfgORWQPft9GuPIMCcRWQ=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.01' LIMIT 1), '交叉口1', 'F021DF1D44FAAEB5E69C459BB2ACF36C',
  'region', 9,
  1, 'snRu+45xdMJV/Wcu5b1iT5Jj2mhukd0n8HMeO39cYXkIqZQt/i49HJ4rJJ+GKrRcR2Q=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS23' LIMIT 1), '机械通风口1', 'F0941E214F30569A1A36BE8D8ECF5027',
  'region', 5,
  1, 'becw0PB3ZPnuVwBXlzSvK6LUTUSl91sN4dEXH8OrwOAhqE0o17nV1CSFkUCNQGJSUinLzBZtDjw=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS10' LIMIT 1), '集水坑1', 'F0BBF34445FC68C6EF8184AC05A5C8DA',
  'region', 5,
  1, 'LQYJ5LSp7e53hTVLihRPQ9RemSQaFxhL5DyBlL3YrY2Io3S5Bv0uVx/o+PZjYvw7Oxk=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS05' LIMIT 1), '自然进风口2', 'F0CCE12F4676BA1A43E7ACBBCB239081',
  'region', 8,
  1, '4R+BoFvnoq/USMks85iTbp4A4cPCugn2bHHfzKLpCFNSeAz+YZtoDuXD7eF6R6HFY47WhhgxLZU=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD5L.SW02' LIMIT 1), '倒虹1', 'F14DBA9F4B1406CE4B78EF8BA5DE4420',
  'region', 1001,
  1, 'PwRrGKZsNCNNlh5RWAIwir5Aa1EDZuyKIiJ8bRdvOjYpBC/5+i7KefRfVB6XsPA=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLB.GX05' LIMIT 1), '自然进风口1', 'F2911EE645AAD5F9377B369D8B6FA6D8',
  'region', 1005,
  1, 'IYiA7dhsKcl7nXPUKsvG3P/DiNMmYCmycTQ25cx90VK6Gp/keRbESi9p5kZTEnUivzkBiakbRao=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS05' LIMIT 1), '引出门2', 'F2CEEEF040287E40EC4E81A8530EC392',
  'region', 1004,
  1, 'IP6vR2EcYsDN6S4EPjNYZvxLkJdyqKCNxENDVJdmw6eAaNLr415s9tFLn7Gtg//QFpI=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.17' LIMIT 1), '集水坑1', 'F31070E8407EE070762EA5BC22655E13',
  'region', 1004,
  1, 'we/cUJ8f/Dq4QPg6hf/I8nTl/zEN3E+sBF8iwEK7SsN6ZQAIISQXJaKOl28PKSCsjvQ=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD5L.SW03' LIMIT 1), '自然进风口3', 'F32CA41F49AA48A76BC1F0823D578CA2',
  'region', 5,
  1, 'wsDue0BzrlnPBYBV+1j8aeqE6JRStqnaJXBdRcUlQShkStGXGp/Vcw8EbSzl9hbFxxScqTCRudE=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.07' LIMIT 1), '集水坑3', 'F32E3D5F4CA84FEDFE3243AF313D44CB',
  'region', 4,
  1, 'd0qn5qDos+VhAZ+KKdaCfrGpbS9q7buBarZZXuZe0dLY78NT0pLZ7MOmXE/RE+7Qsw4=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.22' LIMIT 1), '集水坑4', 'F38C58B04FCF970F79F65A815544B2AE',
  'region', 5,
  1, '5G7rz98CalE1qUWt6ueSEppOJoxYLvMBjOgx1J+PfNBMc4Mq55RHsl+ZFL9laz15fms=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX03' LIMIT 1), '集水坑2', 'F38E54F644D86E137F672C86F0FC1ECF',
  'region', 5,
  1, 'zY90cqVPAgPBjm2Akw7aqGsRD5p0l0BFClWY1twVkIDR3DHCv8ErfawkLb63eLbxo1c=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS12' LIMIT 1), '交叉口1', 'F3C1EAA745BBE0F01CA1FCBBCC8EDE9C',
  'region', 1004,
  1, 'yFCxG/v0gCpYwuVW577eDgNlD2+dI/2V4i34NfgfWN83YFiAc1TQZEJoImEf+Q8fW7s=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS02' LIMIT 1), '自然进风口1', 'F42903764CE4AB58924871949B10FFC8',
  'region', 1004,
  1, '8m7diAWZp1Ap6w7TfYZQiK6Jz/LFm79Z/t36PkvHo5g+UOTZpXvQ35SwMtjOQglCrelD/QDglGM=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS22' LIMIT 1), '引出门1', 'F43BFBD8445B534896E0CAA78262047C',
  'region', 1003,
  1, 'CD8L7jQTpzMsxARAcBbea+OD3I6dlZph5mSgpHAFmK5dWrvaNydi+i+WG2kQMRey5YA=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.01' LIMIT 1), '集水坑5', 'F524197C4F760718A5A5B88853D5529B',
  'region', 6,
  1, 'rD5gGzVP8BiodDf26XyFke02i9lQHyqmE9TfgQ9zWdCE7/zPbj5GfVjqGIgpdF/9D0E=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS04' LIMIT 1), '自然进风口2', 'F56D24B04812A898922748B246DC8226',
  'region', 6,
  1, 'EOos8sUIdQAl7xMELGwnDLYfianCPZ51ihsVDeYj+0UhAmduAC5jq48ZWjTzlis19WEVzy70xPo=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.12' LIMIT 1), '集水坑6', 'F59060C74EE9547487F210ABA059A14E',
  'region', 7,
  1, '4AvhBbKY/lE70+i2VQg+sXIH2Utcgk9m5c5hUZ2iw2eNgiX/i2qhqeh3Ou6NzHQPh8k=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS02' LIMIT 1), '吊装口1', 'F5E3797A4B586CBFE915F48E7CD9A9B3',
  'region', 4,
  1, 'xpikzznAf7Wq4EiaKanO26Ow2VsEMJON+T4Jx4ngElwTJdYrkU5SmCbmXQk1dK1scH0=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.05' LIMIT 1), '集水坑3', 'F5F53905466FC6CA66751497918D3CC6',
  'region', 4,
  1, '918FOSYWn/1rU53kBqCT3n/ttBo8QLsU+znfwaSUkkSQCqIZ7gyJcjTfTIWUkWZO+uA=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS04' LIMIT 1), '自然进风口1', 'F7EDD5844004D30BF0EF86A89124E59D',
  'region', 1004,
  1, 'lFnvPYxQATqOKQW65lrgtWgMeTsOsWm8GJiKoZc0SBSO12M/w64w0hbONd2NW8z7QCezmGDw8mY=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLN.GX04' LIMIT 1), '自然进风口2', 'F991310140AA1757A9B582B7F6186796',
  'region', 6,
  1, 'yy8RA8Hnk5bxQPji6VX3FM8j+2kZrSrt2ocsYKLjCskfqX7AV7lle0jqOVt0bReQeKEhvxZs6uI=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD5L.SW03' LIMIT 1), '自然进风口1', 'F9ACFD824FA95B2F906103A59BDD0982',
  'region', 3,
  1, '00Fqr4aR7BzRnmJfG3uGkJKS0h1+MmHTnCkxjiufKJH1wvarOP9HXGxHSqDjN60Q4Zb8PD3cp3c=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.HSDJ.05' LIMIT 1), '自然进风口1', 'FA915631491773433EBB54B721C07D85',
  'region', 1002,
  1, '7FLylYf9rHFFNSCvAG5cLiMsexqFx9InqDrTqDjuvy2F9WKLs2A7f42tCi19blXbRzS5AUsyY6U=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLB.GX01' LIMIT 1), '引出门1', 'FABC4FE240165FE306B3A49FF9FBCBA8',
  'region', 1004,
  1, 'dR7E/KkcC3C3B/FcWcXMjskp+GxVaJBn504Q8Xv15Uo5BMuOc1cLqqwzGUWDiYDiL5k=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS03' LIMIT 1), '集水坑5', 'FB9D3BFA4DA4F78C79C067B326C10F02',
  'region', 7,
  1, 'eDympY2gXzenbRgYfip5jiowTvRwZN2XGLFRIUFFWbZC3XstwoH9WVNOKyU4bYNRIwI=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.HSDJ.12' LIMIT 1), '出入口1', 'FC3406EC44AB01C812F38CAE46BB34A9',
  'region', 1002,
  1, 'nfRq/nJAR+AQGw/c7yh8Q2DktT0pjPf5qdOqSHJxpEVQPab597Smiwsg/5ro7Qs1IIE=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.21' LIMIT 1), '集水坑1', 'FC4F93E74F9D81B97815099A92F8C737',
  'region', 1005,
  1, 'Hy/JB+fL6neOY2M1hCPlIGibpln0ybgH1pvRD/le7oZymWvNZvINRv8DA+0gzFDO5fk=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.09' LIMIT 1), '集水坑3', 'FC606A794B6D093F82C7B6A07A35E751',
  'region', 4,
  1, '0ZwXVHd7lbKmld9nLTkShUJXBOc0KY9WROL6BK08Uj/QStJEijXjeLsEroZVuACRMs4=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.HSDJ.03' LIMIT 1), '出入口1', 'FC62ABB54D0B9BB939B1F58F87E762DF',
  'region', 1003,
  1, 'B6/idSKk/+mR1rCTjxTwxsIS1GhSupXMjMlECVN/C3sdTq/MQWRu6jY0W2G/d3WYdOk=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX04' LIMIT 1), '吊装口1', 'FCA9FEA544944B77E738D1BDCD8C8A56',
  'region', 1001,
  1, '3oQOrhi4M0h/KqK4ncpOvo72foIWBcZhZGFMZo/80zD5kgJe2EayKnX0/1tgi7xjNPQ=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS04' LIMIT 1), '集水坑2', 'FD190E7A4E95AC5873FB4A82C342385E',
  'region', 3,
  1, 'sEWDm4h9Gwqh6uVKC5pDTi+A6X/6TXdM/BFcLiN2f+tfSz8n+ginkzbRshWucR79rnk=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG6LB.SS10' LIMIT 1), '集水坑2', 'FD696CCD4A1D4EFCC5E780ADE00D961E',
  'region', 5,
  1, 'jbsNIpUZQL13RWn/Y98NKa/UK55if6LjKK6OySnQM4cpVvk8dsGkdXFrH06yLnPsWZE=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GKYLB.GX06' LIMIT 1), '人员出入口1', 'FDA5DEB04137DE757D5D7685CCD1AB6C',
  'region', 1003,
  1, 'Hdy2JOJ2J6RFKdSyWQ5MCjGSIDQe7Qb6fQSBpDtO7u8hjeHBslASvfIjMeHuVkgceTBzjBKv470=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.08' LIMIT 1), '集水坑2', 'FE01B1C64B7182538740E2A370C648AF',
  'region', 1004,
  1, 'co7FT86KjZvm0iMX7EbIkNS/0cWX4cnCTtD+6tXJBc5E9ot1e97gZlfwq/puQfjEPkk=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS21' LIMIT 1), '吊装口1', 'FE5846E8450C700BE146C4BAF29FC892',
  'region', 4,
  1, '0/JBbtk2XOVkdFbr0dl30KNN6ocqC4eKiRknSCMPKN6NTBvzy4h5XOSkUYRAu1XQaUI=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GXDD.GX10' LIMIT 1), '集水坑2', 'FEF7A41045FF62CD7FADBD9945EA2CDD',
  'region', 1004,
  1, 'VVRBu5YJ2bhetbhrSMslzVa0Ia7U6fDRqTLAiTJzBgoI4PcCP6BT+gUodndZuQ78kRY=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.GG5LB.01' LIMIT 1), '集水坑6', 'FF1424634206C57B24C8AAB96C27F8C2',
  'region', 7,
  1, 'sHwcjZInAllHk+weOWEuva20h0rLgAULYFXkrEwNkgctNsa57rhaCdEtxG5BdpqLVsw=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'Region.SD3L.SS11' LIMIT 1), '集水坑2', 'FFB37685473C86090EAD99BF1DFD358A',
  'region', 1005,
  1, '1OpSYi9X0bMDjUptEtW6jOPun614no+xvmYCOUV20afYYKR+g+bRpm23OLMCmD/XLAI=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-0e4c92beb83c409e8b8bede15e3d4bf2' LIMIT 1), '集水坑2', 'A417BD104C35ABE60D2491A62A6D05F7',
  'region', 1004,
  1, 'pZH15Ktb/FyzseDvr3lGfOhFWcvLAwnqcTvdf5zKddWZsN7VmjLN2I9FktQAC6DtGPU=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-0e4c92beb83c409e8b8bede15e3d4bf2' LIMIT 1), '电力信息舱', 'CAT-3c0ee6e350314dd4bca85484dda3eff3',
  'region', 1,
  1, 'Wq4bREqfedJwCgyt/a2HCh5dSRPMJhrgQ5xTusJUJNpficDTq4QZXinwnS6Z2vS8mg7kPA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-0e4c92beb83c409e8b8bede15e3d4bf2' LIMIT 1), '管道舱', 'CAT-8d90d54098ed47a4917b4768c9c07f50',
  'region', 2,
  1, 'f8ofhQeGLepAickGgf3pAIxPDCoiP31VR7+QsD4RMaWedCDkPk5aXVuzCV9VMg==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-0e4c92beb83c409e8b8bede15e3d4bf2' LIMIT 1), '高压电力舱', 'CAT-a502cc323ecb447f8543394ab66abae0',
  'region', 3,
  1, 'bb4gqd0zKkxB5hIYyXfDi2H2N7JlqklhkOJ9uAtJCuvk8bfrD60rQoThL2yPq88ooF6XTA==', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-0e4c92beb83c409e8b8bede15e3d4bf2' LIMIT 1), '集水坑1', 'F9F4F4A343D5DC667AA41D8BC73E9B77',
  'region', 1003,
  1, 'i4fD8Wlnv5O/vkFAHLP8VX+phsszl6peeTztlPl9fcyhbq2TVvdE7FlH7CD44pDdTO8=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  (SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'CAT-0e4c92beb83c409e8b8bede15e3d4bf2' LIMIT 1), '集水坑3', 'FF7758234C6FC00955FCDA95428B87C5',
  'region', 1005,
  1, 'Y9nHfHubikQd+Fq5mmO1DiAMrOhUqZGsICtNaRo7J4/QpgjP8a5sSoom0SDV946yhgY=', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;


-- rebuild tree_path / level after category upsert (id-agnostic)
WITH RECURSIVE cat_tree AS (
  SELECT c.id, c.code, c.parent_id, ARRAY[c.id] AS path_ids, 0 AS lvl
  FROM dynamic_category c
  WHERE c.deleted = false AND c.tenant_id = 1
    AND (c.parent_id IS NULL OR c.parent_id = 0
         OR NOT EXISTS (
           SELECT 1 FROM dynamic_category p
           WHERE p.id = c.parent_id AND p.deleted = false AND p.tenant_id = 1
         ))
  UNION ALL
  SELECT c.id, c.code, c.parent_id, ct.path_ids || c.id, ct.lvl + 1
  FROM dynamic_category c
  JOIN cat_tree ct ON c.parent_id = ct.id
  WHERE c.deleted = false AND c.tenant_id = 1
)
UPDATE dynamic_category c
SET
  tree_path = '/' || array_to_string(ct.path_ids, '/') || '/',
  level = ct.lvl + 1,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
FROM cat_tree ct
WHERE c.id = ct.id;


-- dynamic_model_category_relation: 65 row(s), resolve by model_code + category_code

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, business_type_code, sort, tenant_id, creator
)
SELECT m.id, c.id, 'maintenance', 2, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-6bfa9a8c9be648cebc21737115690e21'
  AND c.code = 'CAT-f3160aac16414874b61409df3b0ea9ef'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_category_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.model_id = m.id AND r.category_id = c.id
  );

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, business_type_code, sort, tenant_id, creator
)
SELECT m.id, c.id, 'pipeline', 3, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-38b16e542f144607823a23a297b4a771'
  AND c.code = 'CAT-50a1f0cc2a844121bed29fd72c0fec0b'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_category_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.model_id = m.id AND r.category_id = c.id
  );

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, business_type_code, sort, tenant_id, creator
)
SELECT m.id, c.id, 'pipeline', 2, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-91b5154ccebb415b8a630fcec479bd0a'
  AND c.code = 'CAT-50a1f0cc2a844121bed29fd72c0fec0b'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_category_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.model_id = m.id AND r.category_id = c.id
  );

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, business_type_code, sort, tenant_id, creator
)
SELECT m.id, c.id, 'pipeline', 5, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-a8470fe38e9d4d68b7e11989e361e388'
  AND c.code = 'CAT-ab342c064d5444d885e07940fa8b9ef4'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_category_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.model_id = m.id AND r.category_id = c.id
  );

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, business_type_code, sort, tenant_id, creator
)
SELECT m.id, c.id, 'pipeline', 5, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-a8470fe38e9d4d68b7e11989e361e388'
  AND c.code = 'CAT-4a1d2d3d34794cb8b7ccb9c44540dd8d'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_category_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.model_id = m.id AND r.category_id = c.id
  );

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, business_type_code, sort, tenant_id, creator
)
SELECT m.id, c.id, 'pipeline', 4, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-61cc9a25576a4d07b304337a3eac966a'
  AND c.code = 'CAT-ab342c064d5444d885e07940fa8b9ef4'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_category_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.model_id = m.id AND r.category_id = c.id
  );

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, business_type_code, sort, tenant_id, creator
)
SELECT m.id, c.id, 'pipeline', 3, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-61cc9a25576a4d07b304337a3eac966a'
  AND c.code = 'CAT-4a1d2d3d34794cb8b7ccb9c44540dd8d'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_category_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.model_id = m.id AND r.category_id = c.id
  );

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, business_type_code, sort, tenant_id, creator
)
SELECT m.id, c.id, 'pipeline', 3, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-88a38000121341e2950132da32baad90'
  AND c.code = 'CAT-ab342c064d5444d885e07940fa8b9ef4'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_category_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.model_id = m.id AND r.category_id = c.id
  );

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, business_type_code, sort, tenant_id, creator
)
SELECT m.id, c.id, 'pipeline', 2, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-88a38000121341e2950132da32baad90'
  AND c.code = 'CAT-4a1d2d3d34794cb8b7ccb9c44540dd8d'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_category_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.model_id = m.id AND r.category_id = c.id
  );

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, business_type_code, sort, tenant_id, creator
)
SELECT m.id, c.id, 'pipeline', 1, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-26eabac35cb644cc84ae64dfb5ff9ba4'
  AND c.code = 'CAT-50a1f0cc2a844121bed29fd72c0fec0b'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_category_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.model_id = m.id AND r.category_id = c.id
  );

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, business_type_code, sort, tenant_id, creator
)
SELECT m.id, c.id, 'pipeline', 2, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-b83f05dc94f34fb4807458c0a48ba488'
  AND c.code = 'CAT-ab342c064d5444d885e07940fa8b9ef4'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_category_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.model_id = m.id AND r.category_id = c.id
  );

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, business_type_code, sort, tenant_id, creator
)
SELECT m.id, c.id, 'pipeline', 4, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-b83f05dc94f34fb4807458c0a48ba488'
  AND c.code = 'CAT-4a1d2d3d34794cb8b7ccb9c44540dd8d'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_category_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.model_id = m.id AND r.category_id = c.id
  );

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, business_type_code, sort, tenant_id, creator
)
SELECT m.id, c.id, 'pipeline', 1, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-f55ce56d31f741caa5a69801556834fe'
  AND c.code = 'CAT-ab342c064d5444d885e07940fa8b9ef4'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_category_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.model_id = m.id AND r.category_id = c.id
  );

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, business_type_code, sort, tenant_id, creator
)
SELECT m.id, c.id, 'pipeline', 6, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-f55ce56d31f741caa5a69801556834fe'
  AND c.code = 'CAT-4a1d2d3d34794cb8b7ccb9c44540dd8d'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_category_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.model_id = m.id AND r.category_id = c.id
  );

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, business_type_code, sort, tenant_id, creator
)
SELECT m.id, c.id, 'pipeline', 1, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-5f185cf77c48489192396bb3b745f692'
  AND c.code = 'CAT-9999540dac00472b99a37adfb562e919'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_category_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.model_id = m.id AND r.category_id = c.id
  );

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, business_type_code, sort, tenant_id, creator
)
SELECT m.id, c.id, 'pipeline', 1, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-7bf4135c3ca24be89d6ca7eb7f2090bb'
  AND c.code = 'CAT-3662feaa236c4b4f914a3b001e88cdcf'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_category_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.model_id = m.id AND r.category_id = c.id
  );

INSERT INTO dynamic_model_category_relation (
  model_id, category_id, business_type_code, sort, tenant_id, creator
)
SELECT m.id, c.id, 'pipeline', 1, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-d0c46c54a32f49279e49113ae9f1a6a2'
  AND c.code = 'CAT-4a1d2d3d34794cb8b7ccb9c44540dd8d'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_category_relation r
    WHERE r.deleted = false AND r.tenant_id = 1
      AND r.model_id = m.id AND r.category_id = c.id
  );


-- dynamic_page_config: (empty)
