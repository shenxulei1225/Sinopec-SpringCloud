-- 开发联调 · task 业务实例样例（patrol_task 巡检任务）
-- 用途：数据管理 / 任务实体列表联调；tenant_id=1 与登录态一致
-- 幂等：按 id 存在则更新；按 code 对齐 patrol_task 模型

SET search_path TO dynamicbusiness;

-- patrol_task 模型挂到任务分类（左侧分类树筛选模型用）
INSERT INTO dynamic_model_category_relation_t1 (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT m.id, c.id, 'task', m.code, c.code, 1, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'task_cat_daily'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'patrol_task'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation_t1 (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT m.id, c.id, 'task', m.code, c.code, 2, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'CAT-9346c034c61e437a938a8ed0e3d03ebc'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'patrol_task'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO ent_task (
  id,
  entity_type_code,
  model_id,
  name,
  code,
  tenant_id,
  creator,
  tree_path,
  sort,
  status,
  deleted,
  custom_fields
)
OVERRIDING SYSTEM VALUE
SELECT
  v.id,
  'task',
  m.id,
  v.name,
  v.code,
  1,
  'seed',
  v.tree_path,
  v.sort,
  1,
  false,
  v.custom_fields
FROM (
  VALUES
    (
      910001,
      'PATROL-TASK-001',
      '日常巡检-储罐A区',
      '/910001/',
      1,
      jsonb_build_object(
        'FLD-TSK-003', '巡检',
        'FLD-TSK-004', '待派工',
        'FLD-TSK-005', '中',
        'FLD-TSK-022', 'enabled',
        'FLD-TSK-023', 'POLICY-SET-DAILY-01',
        'FLD-TSK-007', '2026-07-01 08:00:00',
        'FLD-TSK-008', '2026-07-01 12:00:00',
        'FLD-TSK-024', '{"entityTypeCode":"facility","id":1}',
        'FLD-TSK-028', '{"entityTypeCode":"patrol_schedule","id":201}',
        'FLD-TSK-029', '{"entityTypeCode":"patrol_object","id":301}',
        'FLD-TSK-030', '{"entityTypeCode":"patrol_point","id":501}',
        'FLD-TSK-031', '[{"entityTypeCode":"inspection_item","id":101},{"entityTypeCode":"inspection_item","id":102}]',
        'FLD-TSK-020', '{"version":1,"policyMode":"pool","preferredAssigneeRole":"inspector","maxConcurrent":2}',
        'FLD-TSK-027', '{"version":1,"topologyRef":"topo_facility001_v1","mobilityProfileId":"ROBOT_GROUND","orderedStopIds":["n_sta_001","n_sta_002"],"totalDistanceMeters":420.5,"segments":[{"fromStopId":"n_sta_001","toStopId":"n_sta_002","distanceMeters":420.5,"polyline":[]}],"plannedAt":"2026-07-11T08:00:00+08:00"}'
      )
    ),
    (
      910002,
      'PATROL-TASK-002',
      '日常巡检-泵房管廊',
      '/910002/',
      2,
      jsonb_build_object(
        'FLD-TSK-003', '巡检',
        'FLD-TSK-004', '进行中',
        'FLD-TSK-005', '高',
        'FLD-TSK-022', 'enabled',
        'FLD-TSK-023', 'POLICY-SET-DAILY-02',
        'FLD-TSK-007', '2026-07-05 09:00:00',
        'FLD-TSK-008', '2026-07-05 17:00:00',
        'FLD-TSK-028', '{"entityTypeCode":"patrol_schedule","id":201}',
        'FLD-TSK-030', '{"entityTypeCode":"patrol_point","id":502}',
        'FLD-TSK-031', '[{"entityTypeCode":"inspection_item","id":101}]',
        'FLD-TSK-020', '{"version":1,"policyMode":"fixed","assigneeIds":[1]}'
      )
    ),
    (
      910003,
      'PATROL-TASK-003',
      '专项巡检-阀门密封',
      '/910003/',
      3,
      jsonb_build_object(
        'FLD-TSK-003', '巡检',
        'FLD-TSK-004', '待派工',
        'FLD-TSK-005', '紧急',
        'FLD-TSK-022', 'draft',
        'FLD-TSK-028', '{"entityTypeCode":"patrol_schedule","id":201}',
        'FLD-TSK-031', '[{"entityTypeCode":"inspection_item","id":102}]',
        'FLD-TSK-020', '{"version":1,"policyMode":"pool","preferredAssigneeRole":"senior_inspector"}'
      )
    ),
    (
      910004,
      'PATROL-TASK-004',
      '夜间巡检-照明与应急',
      '/910004/',
      4,
      jsonb_build_object(
        'FLD-TSK-003', '巡检',
        'FLD-TSK-004', '已完成',
        'FLD-TSK-005', '低',
        'FLD-TSK-022', 'enabled',
        'FLD-TSK-023', 'POLICY-SET-NIGHT-01',
        'FLD-TSK-007', '2026-07-08 22:00:00',
        'FLD-TSK-008', '2026-07-09 02:00:00',
        'FLD-TSK-028', '{"entityTypeCode":"patrol_schedule","id":201}',
        'FLD-TSK-031', '[{"entityTypeCode":"inspection_item","id":101}]',
        'FLD-TSK-020', '{"version":1,"policyMode":"pool","preferredAssigneeRole":"night_shift"}'
      )
    ),
    (
      910005,
      'PATROL-TASK-005',
      '周末巡检-消防设施',
      '/910005/',
      5,
      jsonb_build_object(
        'FLD-TSK-003', '巡检',
        'FLD-TSK-004', '待验收',
        'FLD-TSK-005', '中',
        'FLD-TSK-022', 'enabled',
        'FLD-TSK-028', '{"entityTypeCode":"patrol_schedule","id":201}',
        'FLD-TSK-031', '[{"entityTypeCode":"inspection_item","id":101},{"entityTypeCode":"inspection_item","id":102}]',
        'FLD-TSK-020', '{"version":1,"policyMode":"pool","preferredAssigneeRole":"inspector"}'
      )
    )
) AS v(id, code, name, tree_path, sort, custom_fields)
JOIN dynamic_model m
  ON m.code = 'patrol_task'
 AND m.entity_type_code = 'task'
 AND m.deleted = false
 AND m.tenant_id = 1
ON CONFLICT (id) DO UPDATE SET
  model_id = EXCLUDED.model_id,
  name = EXCLUDED.name,
  code = EXCLUDED.code,
  tenant_id = EXCLUDED.tenant_id,
  tree_path = EXCLUDED.tree_path,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  deleted = false,
  custom_fields = EXCLUDED.custom_fields,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

SELECT setval(
  pg_get_serial_sequence('dynamicbusiness.ent_task', 'id'),
  GREATEST(
    (SELECT COALESCE(MAX(id), 1) FROM dynamicbusiness.ent_task),
    910005
  )
);
