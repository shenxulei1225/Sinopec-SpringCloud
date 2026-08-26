-- ============================================================================
-- 油库 Scope 样例 · 任务 + 执行记录（tenant_id=1）
--
-- 定稿：
--   · 巡检任务、维修任务 → storage `task` / ent_task，侧边栏 DOMAIN 域入口
--   · 巡检记录、维修记录 → storage `task_excution_record` / ent_task_excution_record，DOMAIN 域入口
--   · 删除旧法：patrol 独立类型上的任务模型
--   · patrol 域已物理清除，任务/记录统一走 task + Scope
--
-- 幂等：stable code；执行后请 evict Redis category tree cache
-- ============================================================================

SET search_path TO dynamicbusiness;

-- ---------------------------------------------------------------------------
-- 0. 清理旧法 / 冒烟残留
-- ---------------------------------------------------------------------------
UPDATE dynamic_entity_type
SET deleted = true, updater = 'oil-depot-scope', update_time = CURRENT_TIMESTAMP
WHERE deleted = false AND tenant_id = 1
  AND code IN ('task_patrol_smoke', 'task_scope_empty');

UPDATE dynamic_category_type
SET deleted = true, updater = 'oil-depot-scope', update_time = CURRENT_TIMESTAMP
WHERE deleted = false AND tenant_id = 1 AND category_type_code = 'task_patrol_smoke';

-- 旧法：patrol 独立类型上的模型（patrol 域已废弃）
UPDATE dynamic_model
SET deleted = true, updater = 'oil-depot-scope', update_time = CURRENT_TIMESTAMP
WHERE deleted = false AND tenant_id = 1
  AND entity_type_code = 'patrol';

UPDATE dynamic_model
SET deleted = true, updater = 'oil-depot-scope', update_time = CURRENT_TIMESTAMP
WHERE deleted = false AND tenant_id = 1
  AND code IN (
    'MODEL-a67ee5f36213481ba97167178447ad64',
    'MODEL-6562a9115ebb42ff919bd707753826fe',
    'MODEL-fb102aef77624c2d975a545e421ad534'
  );

-- patrol 域已物理删除（见 dynamic_purge_patrol_domain.sql）

-- 任务执行记录 storage 补全
ALTER TABLE ent_task_excution_record
  ADD COLUMN IF NOT EXISTS tree_path character varying(500),
  ADD COLUMN IF NOT EXISTS sort integer DEFAULT 0;

-- 任务执行记录 storage 补全
UPDATE dynamic_entity_type
SET dedicated_table_name = 'ent_task_excution_record',
    description = '任务执行记录（巡检轮次、异常、维修作业日志）；按 DOMAIN 域入口过滤',
    updater = 'oil-depot-scope',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false AND tenant_id = 1 AND code = 'task_excution_record';

INSERT INTO dynamic_entity_type_config (
  entity_type_code, name, storage_type, dedicated_table_name, enable_rule_engine,
  description, status, tenant_id, creator
) VALUES (
  'task_excution_record', '任务执行记录', 'DEDICATED', 'ent_task_excution_record', true,
  '巡检/维修执行记录；storage 统一，Scope 区分域', 1, 1, 'oil-depot-scope'
)
ON CONFLICT (entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  dedicated_table_name = EXCLUDED.dedicated_table_name,
  description = EXCLUDED.description,
  updater = 'oil-depot-scope',
  update_time = CURRENT_TIMESTAMP;

-- ---------------------------------------------------------------------------
-- 1. 域入口（DOMAIN）
-- ---------------------------------------------------------------------------
INSERT INTO dynamic_entity_type (
  code, name, description, icon, alias, sort, status, type_level,
  association_fields, storage_type, dedicated_table_name, enable_rule_engine,
  tenant_id, creator, entry_kind, base_entity_type_code, domain, group_name
) VALUES
  ('task_patrol', '巡检任务', '油库巡检任务；storage=task，domain=巡检', 'ep:view', '巡检任务',
   11, 'active', 'USER', '{}', 'DEDICATED', 'ent_task', false, 1, 'oil-depot-scope',
   'DOMAIN', 'task', '巡检', '任务'),
  ('task_maintenance', '维修任务', '油库维修/抢修任务；storage=task，domain=维修', 'ep:tools', '维修任务',
   12, 'active', 'USER', '{}', 'DEDICATED', 'ent_task', false, 1, 'oil-depot-scope',
   'DOMAIN', 'task', '维修', '任务'),
  ('task_record_patrol', '巡检记录', '巡检执行记录；storage=task_excution_record，domain=巡检', 'ep:document', '巡检记录',
   13, 'active', 'USER', '{}', 'DEDICATED', 'ent_task_excution_record', false, 1, 'oil-depot-scope',
   'DOMAIN', 'task_excution_record', '巡检', '任务'),
  ('task_record_maintenance', '维修记录', '维修作业记录；storage=task_excution_record，domain=维修', 'ep:notebook', '维修记录',
   14, 'active', 'USER', '{}', 'DEDICATED', 'ent_task_excution_record', false, 1, 'oil-depot-scope',
   'DOMAIN', 'task_excution_record', '维修', '任务')
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name, description = EXCLUDED.description,
  entry_kind = 'DOMAIN', base_entity_type_code = EXCLUDED.base_entity_type_code,
  domain = EXCLUDED.domain, dedicated_table_name = EXCLUDED.dedicated_table_name,
  group_name = EXCLUDED.group_name, deleted = false,
  updater = 'oil-depot-scope', update_time = CURRENT_TIMESTAMP;

-- ---------------------------------------------------------------------------
-- 2. 分类树 · 任务（categoryTypeCode=task）
-- ---------------------------------------------------------------------------
INSERT INTO dynamic_category (parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code)
VALUES (NULL, '任务分类', 'task_root', 'task', 0, 1, 1, 'oil-depot-scope', NULL)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET name = EXCLUDED.name, updater = 'oil-depot-scope', update_time = CURRENT_TIMESTAMP;

UPDATE dynamic_category SET deleted = true, updater = 'oil-depot-scope', update_time = CURRENT_TIMESTAMP
WHERE deleted = false AND tenant_id = 1
  AND code IN ('CAT-8aefcf896470488aa56c9d61d4e3f649', 'CAT-9346c034c61e437a938a8ed0e3d03ebc');

INSERT INTO dynamic_category (parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code)
SELECT p.id, v.name, v.code, 'task', v.sort, 1, 1, 'oil-depot-scope', 'task_root'
FROM dynamic_category p
CROSS JOIN (VALUES
  ('task_cat_daily', '日常巡检', 1),
  ('task_cat_special', '专项巡检', 2),
  ('task_cat_maintenance', '维修作业', 3)
) AS v(code, name, sort)
WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'task_root'
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name, sort = EXCLUDED.sort,
  updater = 'oil-depot-scope', update_time = CURRENT_TIMESTAMP;

-- ---------------------------------------------------------------------------
-- 3. 分类树 · 执行记录（categoryTypeCode=task_excution_record）
-- ---------------------------------------------------------------------------
INSERT INTO dynamic_category_type (category_type_code, name, description, status, tenant_id, creator)
VALUES ('task_excution_record', '任务执行记录', '巡检/维修执行记录分类', 1, 1, 'oil-depot-scope')
ON CONFLICT (category_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET name = EXCLUDED.name, updater = 'oil-depot-scope', update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code)
VALUES (NULL, '执行记录', 'record_root', 'task_excution_record', 0, 1, 1, 'oil-depot-scope', NULL)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET name = EXCLUDED.name, updater = 'oil-depot-scope', update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category (parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code)
SELECT p.id, v.name, v.code, 'task_excution_record', v.sort, 1, 1, 'oil-depot-scope', 'record_root'
FROM dynamic_category p
CROSS JOIN (VALUES
  ('record_cat_patrol', '巡检记录', 1),
  ('record_cat_maint', '维修记录', 2)
) AS v(code, name, sort)
WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = 'record_root'
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET parent_id = EXCLUDED.parent_id, name = EXCLUDED.name,
  updater = 'oil-depot-scope', update_time = CURRENT_TIMESTAMP;

-- ---------------------------------------------------------------------------
-- 4. 任务模型（storage task，8 个）
-- ---------------------------------------------------------------------------
INSERT INTO dynamic_model (code, name, entity_type_code, domain, description, status, sort, field_groups_config, tenant_id, creator)
SELECT v.code, v.name, 'task', v.domain, v.description, 1, v.sort,
  COALESCE(
    (SELECT field_groups_config FROM dynamic_model WHERE code = 'patrol_task' AND deleted = false AND tenant_id = 1 LIMIT 1),
    '[]'
  ),
  1, 'oil-depot-scope'
FROM (VALUES
  ('patrol_task', '储罐日常巡检', '巡检', '油库储罐区日常巡检任务', 1),
  ('patrol_pipeline', '管廊巡检', '巡检', '输油管廊/栈桥巡检', 2),
  ('patrol_fire_facility', '消防设施巡检', '巡检', '消防泵房、泡沫站、灭火器巡检', 3),
  ('patrol_valve_special', '阀门专项巡检', '巡检', '关键阀门密封与泄漏专项', 4),
  ('patrol_adhoc', '临时巡检', '巡检', '雨前/节假日前临时加巡', 5),
  ('maint_work_order', '预防性维护工单', '维修', '泵、阀、仪表计划性保养', 10),
  ('maint_emergency', '抢修任务', '维修', '泄漏/停电等应急抢修', 11),
  ('general_station_task', '站场综合事务', NULL, '非巡检/维修域（测 Scope 隔离）', 20)
) AS v(code, name, domain, description, sort)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET name = EXCLUDED.name, entity_type_code = 'task', domain = EXCLUDED.domain,
  description = EXCLUDED.description, sort = EXCLUDED.sort, deleted = false,
  updater = 'oil-depot-scope', update_time = CURRENT_TIMESTAMP;

-- 删除旧 code general_admin_task（合并为 general_station_task）
UPDATE dynamic_model SET deleted = true, updater = 'oil-depot-scope', update_time = CURRENT_TIMESTAMP
WHERE deleted = false AND tenant_id = 1 AND code = 'general_admin_task';

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, model_code, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  default_value, target_entity_type, tenant_id, creator
)
SELECT tgt.id, src.field_id, tgt.code, src.field_code,
  src.required, src.is_searchable, src.is_filterable, src.is_sortable, src.sort,
  src.default_value, src.target_entity_type, 1, 'oil-depot-scope'
FROM dynamic_model_field_assignment src
JOIN dynamic_model src_m ON src_m.id = src.model_id AND src_m.code = 'patrol_task' AND src_m.deleted = false
JOIN dynamic_model tgt ON tgt.deleted = false AND tgt.tenant_id = 1
  AND tgt.code IN ('patrol_pipeline', 'patrol_fire_facility', 'patrol_valve_special', 'patrol_adhoc',
                   'maint_work_order', 'maint_emergency', 'general_station_task')
WHERE src.deleted = false AND src.tenant_id = 1
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET updater = 'oil-depot-scope', update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation_t1 (model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator)
SELECT m.id, c.id, 'task', m.code, c.code, v.sort, 1, 'oil-depot-scope'
FROM (VALUES
  ('patrol_task', 'task_cat_daily', 1),
  ('patrol_pipeline', 'task_cat_daily', 2),
  ('patrol_fire_facility', 'task_cat_daily', 3),
  ('patrol_valve_special', 'task_cat_special', 1),
  ('maint_work_order', 'task_cat_maintenance', 1),
  ('general_station_task', 'task_cat_daily', 9)
) AS v(model_code, category_code, sort)
JOIN dynamic_model m ON m.deleted = false AND m.tenant_id = 1 AND m.code = v.model_code
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1 AND c.code = v.category_code
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET model_id = EXCLUDED.model_id, category_id = EXCLUDED.category_id,
  updater = 'oil-depot-scope', update_time = CURRENT_TIMESTAMP;

-- ---------------------------------------------------------------------------
-- 5. 执行记录模型（storage task_excution_record，3 个）
-- ---------------------------------------------------------------------------
INSERT INTO dynamic_model (code, name, entity_type_code, domain, description, status, sort, field_groups_config, tenant_id, creator)
VALUES
  ('exec_patrol_round', '巡检轮次记录', 'task_excution_record', '巡检', '一次巡检任务的现场轮次/签到记录', 1, 1, '[]', 1, 'oil-depot-scope'),
  ('exec_patrol_issue', '巡检异常记录', 'task_excution_record', '巡检', '巡检发现的异常/缺陷记录', 1, 2, '[]', 1, 'oil-depot-scope'),
  ('exec_maint_log', '维修作业记录', 'task_excution_record', '维修', '维修工单的过程与验收记录', 1, 10, '[]', 1, 'oil-depot-scope')
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET name = EXCLUDED.name, entity_type_code = EXCLUDED.entity_type_code,
  domain = EXCLUDED.domain, description = EXCLUDED.description, deleted = false,
  updater = 'oil-depot-scope', update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation_t1 (model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator)
SELECT m.id, c.id, 'task_excution_record', m.code, c.code, 1, 1, 'oil-depot-scope'
FROM (VALUES
  ('exec_patrol_round', 'record_cat_patrol'),
  ('exec_patrol_issue', 'record_cat_patrol'),
  ('exec_maint_log', 'record_cat_maint')
) AS v(model_code, category_code)
JOIN dynamic_model m ON m.deleted = false AND m.tenant_id = 1 AND m.code = v.model_code
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1 AND c.code = v.category_code
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET model_id = EXCLUDED.model_id, category_id = EXCLUDED.category_id,
  updater = 'oil-depot-scope', update_time = CURRENT_TIMESTAMP;

-- ---------------------------------------------------------------------------
-- 6. 任务实体 ent_task（22 条）
-- ---------------------------------------------------------------------------
INSERT INTO ent_task (id, entity_type_code, model_id, name, code, tenant_id, creator, tree_path, sort, status, deleted, custom_fields)
OVERRIDING SYSTEM VALUE
SELECT v.id, 'task', m.id, v.name, v.code, 1, 'oil-depot-scope', v.tree_path, v.sort, 1, false, v.custom_fields::jsonb
FROM (VALUES
  -- 储罐日常 5
  (910001, 'patrol_task', 'TK-PAT-001', '储罐A区日常巡检', '/910001/', 1, '{"FLD-TSK-003":"巡检","FLD-TSK-004":"待派工","FLD-TSK-022":"enabled"}'),
  (910002, 'patrol_task', 'TK-PAT-002', '储罐B区日常巡检', '/910002/', 2, '{"FLD-TSK-003":"巡检","FLD-TSK-004":"进行中","FLD-TSK-022":"enabled"}'),
  (910003, 'patrol_task', 'TK-PAT-003', '储罐C区专项巡检', '/910003/', 3, '{"FLD-TSK-003":"巡检","FLD-TSK-004":"待派工","FLD-TSK-022":"enabled"}'),
  (910004, 'patrol_task', 'TK-PAT-004', '夜间储罐巡检', '/910004/', 4, '{"FLD-TSK-003":"巡检","FLD-TSK-004":"已完成","FLD-TSK-022":"enabled"}'),
  (910005, 'patrol_task', 'TK-PAT-005', '周末储罐巡检', '/910005/', 5, '{"FLD-TSK-003":"巡检","FLD-TSK-004":"待验收","FLD-TSK-022":"enabled"}'),
  -- 管廊 3
  (910101, 'patrol_pipeline', 'PL-PAT-001', '1#输油管廊巡检', '/910101/', 1, '{"FLD-TSK-003":"巡检","FLD-TSK-004":"待派工","FLD-TSK-022":"enabled"}'),
  (910102, 'patrol_pipeline', 'PL-PAT-002', '2#输油管廊巡检', '/910102/', 2, '{"FLD-TSK-003":"巡检","FLD-TSK-004":"进行中","FLD-TSK-022":"enabled"}'),
  (910103, 'patrol_pipeline', 'PL-PAT-003', '栈桥区管廊巡检', '/910103/', 3, '{"FLD-TSK-003":"巡检","FLD-TSK-004":"待派工","FLD-TSK-022":"enabled"}'),
  -- 消防 3
  (910108, 'patrol_fire_facility', 'FR-PAT-001', '消防泵房巡检', '/910108/', 1, '{"FLD-TSK-003":"巡检","FLD-TSK-004":"待派工","FLD-TSK-022":"enabled"}'),
  (910109, 'patrol_fire_facility', 'FR-PAT-002', '泡沫站巡检', '/910109/', 2, '{"FLD-TSK-003":"巡检","FLD-TSK-004":"进行中","FLD-TSK-022":"enabled"}'),
  (910110, 'patrol_fire_facility', 'FR-PAT-003', '灭火器月度巡检', '/910110/', 3, '{"FLD-TSK-003":"巡检","FLD-TSK-004":"待派工","FLD-TSK-022":"enabled"}'),
  -- 阀门专项 2
  (910104, 'patrol_valve_special', 'VL-PAT-001', '进油阀专项巡检', '/910104/', 1, '{"FLD-TSK-003":"巡检","FLD-TSK-004":"待派工","FLD-TSK-022":"enabled"}'),
  (910105, 'patrol_valve_special', 'VL-PAT-002', '出站阀专项巡检', '/910105/', 2, '{"FLD-TSK-003":"巡检","FLD-TSK-004":"已完成","FLD-TSK-022":"enabled"}'),
  -- 临时 2（未挂分类）
  (910106, 'patrol_adhoc', 'AD-PAT-001', '雨前库内加巡', '/910106/', 1, '{"FLD-TSK-003":"巡检","FLD-TSK-004":"草稿","FLD-TSK-022":"draft"}'),
  (910107, 'patrol_adhoc', 'AD-PAT-002', '节前安全加巡', '/910107/', 2, '{"FLD-TSK-003":"巡检","FLD-TSK-004":"草稿","FLD-TSK-022":"draft"}'),
  -- 维修 5
  (910201, 'maint_work_order', 'MT-WO-001', '输油泵预防性保养', '/910201/', 1, '{"FLD-TSK-003":"维修","FLD-TSK-004":"待派工","FLD-TSK-022":"enabled"}'),
  (910202, 'maint_work_order', 'MT-WO-002', '调节阀计划更换', '/910202/', 2, '{"FLD-TSK-003":"维修","FLD-TSK-004":"进行中","FLD-TSK-022":"enabled"}'),
  (910203, 'maint_work_order', 'MT-WO-003', '液位仪年度校准', '/910203/', 3, '{"FLD-TSK-003":"维修","FLD-TSK-004":"待验收","FLD-TSK-022":"enabled"}'),
  (910204, 'maint_emergency', 'MT-EM-001', '管线法兰泄漏抢修', '/910204/', 1, '{"FLD-TSK-003":"维修","FLD-TSK-004":"进行中","FLD-TSK-022":"enabled"}'),
  (910205, 'maint_emergency', 'MT-EM-002', '配电间故障抢修', '/910205/', 2, '{"FLD-TSK-003":"维修","FLD-TSK-004":"待派工","FLD-TSK-022":"enabled"}'),
  -- 站场综合 2
  (910301, 'general_station_task', 'ST-GEN-001', 'HSE培训计划', '/910301/', 1, '{"FLD-TSK-003":"综合","FLD-TSK-004":"草稿","FLD-TSK-022":"draft"}'),
  (910302, 'general_station_task', 'ST-GEN-002', '库区卫生大扫除', '/910302/', 2, '{"FLD-TSK-003":"综合","FLD-TSK-004":"已完成","FLD-TSK-022":"enabled"}')
) AS v(id, model_code, code, name, tree_path, sort, custom_fields)
JOIN dynamic_model m ON m.deleted = false AND m.tenant_id = 1 AND m.code = v.model_code
ON CONFLICT (id) DO UPDATE SET
  model_id = EXCLUDED.model_id, name = EXCLUDED.name, code = EXCLUDED.code,
  custom_fields = EXCLUDED.custom_fields, deleted = false,
  updater = 'oil-depot-scope', update_time = CURRENT_TIMESTAMP;

-- 软删 seed 范围外历史 task 行（若存在）
UPDATE ent_task SET deleted = true, updater = 'oil-depot-scope', update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1 AND deleted = false
  AND id NOT BETWEEN 910001 AND 910302;

-- ---------------------------------------------------------------------------
-- 7. 执行记录实体 ent_task_excution_record（13 条）
-- ---------------------------------------------------------------------------
INSERT INTO ent_task_excution_record (id, entity_type_code, model_id, name, code, tenant_id, creator, tree_path, sort, status, deleted, custom_fields)
OVERRIDING SYSTEM VALUE
SELECT v.id, 'task_excution_record', m.id, v.name, v.code, 1, 'oil-depot-scope', v.tree_path, v.sort, 1, false, v.custom_fields::jsonb
FROM (VALUES
  (920001, 'exec_patrol_round', 'REC-PR-001', '储罐A区第1轮签到', '/920001/', 1, '{"sourceTaskId":910001}'),
  (920002, 'exec_patrol_round', 'REC-PR-002', '储罐A区第2轮签到', '/920002/', 2, '{"sourceTaskId":910001}'),
  (920003, 'exec_patrol_round', 'REC-PR-003', '1#管廊巡检签到', '/920003/', 3, '{"sourceTaskId":910101}'),
  (920004, 'exec_patrol_round', 'REC-PR-004', '消防泵房巡检签到', '/920004/', 4, '{"sourceTaskId":910108}'),
  (920005, 'exec_patrol_issue', 'REC-PI-001', '储罐B区液位计读数异常', '/920005/', 5, '{"sourceTaskId":910002,"severity":"中"}'),
  (920006, 'exec_patrol_issue', 'REC-PI-002', '2#管廊支架腐蚀', '/920006/', 6, '{"sourceTaskId":910102,"severity":"高"}'),
  (920007, 'exec_patrol_issue', 'REC-PI-003', '泡沫液液位偏低', '/920007/', 7, '{"sourceTaskId":910109,"severity":"中"}'),
  (920008, 'exec_patrol_issue', 'REC-PI-004', '进油阀渗油痕迹', '/920008/', 8, '{"sourceTaskId":910104,"severity":"紧急"}'),
  (920101, 'exec_maint_log', 'REC-ML-001', '输油泵保养-拆检记录', '/920101/', 1, '{"sourceTaskId":910201}'),
  (920102, 'exec_maint_log', 'REC-ML-002', '调节阀更换-试压记录', '/920102/', 2, '{"sourceTaskId":910202}'),
  (920103, 'exec_maint_log', 'REC-ML-003', '液位仪校准-验收记录', '/920103/', 3, '{"sourceTaskId":910203}'),
  (920104, 'exec_maint_log', 'REC-ML-004', '法兰泄漏抢修-封堵记录', '/920104/', 4, '{"sourceTaskId":910204}'),
  (920105, 'exec_maint_log', 'REC-ML-005', '配电抢修-送电记录', '/920105/', 5, '{"sourceTaskId":910205}')
) AS v(id, model_code, code, name, tree_path, sort, custom_fields)
JOIN dynamic_model m ON m.deleted = false AND m.tenant_id = 1 AND m.code = v.model_code
ON CONFLICT (id) DO UPDATE SET
  model_id = EXCLUDED.model_id, name = EXCLUDED.name, custom_fields = EXCLUDED.custom_fields,
  deleted = false, updater = 'oil-depot-scope', update_time = CURRENT_TIMESTAMP;

UPDATE ent_task_excution_record SET deleted = true, updater = 'oil-depot-scope', update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1 AND deleted = false AND id NOT BETWEEN 920001 AND 920105;

-- ---------------------------------------------------------------------------
-- 8. 实体 ↔ 分类
-- ---------------------------------------------------------------------------
UPDATE dynamic_entity_category_relation_t1
SET deleted = true, updater = 'oil-depot-scope', update_time = CURRENT_TIMESTAMP
WHERE deleted = false AND tenant_id = 1
  AND entity_type_code IN ('task', 'task_excution_record')
  AND entity_id BETWEEN 910001 AND 920105;

INSERT INTO dynamic_entity_category_relation_t1 (entity_id, category_id, entity_type_code, sort, tenant_id, creator)
SELECT v.entity_id, c.id, v.etc, v.sort, 1, 'oil-depot-scope'
FROM (VALUES
  (910001, 'task', 'task_cat_daily', 1), (910002, 'task', 'task_cat_daily', 2),
  (910003, 'task', 'task_cat_special', 1), (910101, 'task', 'task_cat_daily', 3),
  (910102, 'task', 'task_cat_daily', 4), (910103, 'task', 'task_cat_daily', 5),
  (910108, 'task', 'task_cat_daily', 6), (910109, 'task', 'task_cat_daily', 7),
  (910110, 'task', 'task_cat_daily', 8), (910104, 'task', 'task_cat_special', 2),
  (910105, 'task', 'task_cat_special', 3), (910201, 'task', 'task_cat_maintenance', 1),
  (910202, 'task', 'task_cat_maintenance', 2), (910203, 'task', 'task_cat_maintenance', 3),
  (910301, 'task', 'task_cat_daily', 9), (910302, 'task', 'task_cat_daily', 10),
  (920001, 'task_excution_record', 'record_cat_patrol', 1), (920002, 'task_excution_record', 'record_cat_patrol', 2),
  (920003, 'task_excution_record', 'record_cat_patrol', 3), (920004, 'task_excution_record', 'record_cat_patrol', 4),
  (920005, 'task_excution_record', 'record_cat_patrol', 5), (920006, 'task_excution_record', 'record_cat_patrol', 6),
  (920007, 'task_excution_record', 'record_cat_patrol', 7), (920008, 'task_excution_record', 'record_cat_patrol', 8),
  (920101, 'task_excution_record', 'record_cat_maint', 1), (920102, 'task_excution_record', 'record_cat_maint', 2),
  (920103, 'task_excution_record', 'record_cat_maint', 3), (920104, 'task_excution_record', 'record_cat_maint', 4),
  (920105, 'task_excution_record', 'record_cat_maint', 5)
) AS v(entity_id, etc, category_code, sort)
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1 AND c.code = v.category_code;

SELECT setval(pg_get_serial_sequence('dynamicbusiness.ent_task', 'id'),
  GREATEST((SELECT COALESCE(MAX(id), 1) FROM dynamicbusiness.ent_task), 910302));
SELECT setval(pg_get_serial_sequence('dynamicbusiness.ent_task_excution_record', 'id'),
  GREATEST((SELECT COALESCE(MAX(id), 1) FROM dynamicbusiness.ent_task_excution_record), 920105));
