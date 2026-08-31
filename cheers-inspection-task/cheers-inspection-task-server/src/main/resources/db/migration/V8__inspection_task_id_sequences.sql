-- V8: PostgreSQL 主键序列（草稿创建等写入此前 id 为 null → 系统异常）
-- MyBatis-Plus 在 PG 上 id-type 智能模式为 INPUT，须配合 @KeySequence 取 nextval

CREATE SEQUENCE IF NOT EXISTS inspection_task_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS inspection_object_station_binding_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS inspection_object_profile_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS inspection_route_plan_seq START WITH 1 INCREMENT BY 1;

-- 已有手工种子 id（如 100001）时，把序列推到 max(id)+1，避免冲突
SELECT setval(
  'inspection_task_seq',
  GREATEST(1, COALESCE((SELECT MAX(id) FROM inspection_task), 0) + 1),
  false
);
SELECT setval(
  'inspection_object_station_binding_seq',
  GREATEST(1, COALESCE((SELECT MAX(id) FROM inspection_object_station_binding), 0) + 1),
  false
);
SELECT setval(
  'inspection_object_profile_seq',
  GREATEST(1, COALESCE((SELECT MAX(id) FROM inspection_object_profile), 0) + 1),
  false
);
SELECT setval(
  'inspection_route_plan_seq',
  GREATEST(1, COALESCE((SELECT MAX(id) FROM inspection_route_plan), 0) + 1),
  false
);

COMMENT ON SEQUENCE inspection_task_seq IS 'inspection_task.id；配合 InspectionTaskDO @KeySequence';
COMMENT ON SEQUENCE inspection_object_station_binding_seq IS 'inspection_object_station_binding.id';
