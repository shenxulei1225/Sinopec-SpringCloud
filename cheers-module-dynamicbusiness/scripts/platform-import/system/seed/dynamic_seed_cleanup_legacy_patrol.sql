-- ============================================================================
-- 物理清理：退役 route / inspection_point + 样例幂等重导前置
-- 在 import 样例数据前执行；仅 tenant_id=1 开发库使用
-- ============================================================================

SET search_path TO dynamicbusiness;

BEGIN;

-- 1) 样例实体分类绑定（Pattern B relation + 误写 Pattern C link；含历史软删行）
DELETE FROM dynamic_entity_category_relation
WHERE tenant_id = 1
  AND entity_id IN (101, 102, 201, 301, 501, 502, 910001, 910002, 910003, 910004, 910005);

DELETE FROM dynamic_category_entity_link
WHERE tenant_id = 1
  AND entity_id IN (101, 102, 201, 301, 501, 502, 910001, 910002, 910003, 910004, 910005);

-- 2) 退役业务实例（旧 POC：路线 / 旧巡检点实体）
DELETE FROM ent_inspection_point WHERE tenant_id = 1;
DELETE FROM ent_route WHERE tenant_id = 1;

-- 3) 样例业务实例（重导前清空固定 id）
DELETE FROM ent_task WHERE tenant_id = 1 AND id BETWEEN 910001 AND 910005;
DELETE FROM ent_patrol_point WHERE tenant_id = 1 AND id IN (501, 502);
DELETE FROM ent_patrol_object WHERE tenant_id = 1 AND id = 301;
DELETE FROM ent_patrol_schedule WHERE tenant_id = 1 AND id = 201;
DELETE FROM ent_inspection_item WHERE tenant_id = 1 AND id IN (101, 102);

-- 4) 退役模型字段分配与模型
DELETE FROM dynamic_model_field_assignment
WHERE tenant_id = 1 AND model_code IN ('patrol_route', 'route_checkpoint');

DELETE FROM dynamic_model
WHERE tenant_id = 1 AND code IN ('patrol_route', 'route_checkpoint');

-- 5) 退役实体类型关系
DELETE FROM dynamic_entity_type_relation
WHERE tenant_id = 1
  AND (
    source_entity_type_code IN ('route', 'inspection_point')
    OR target_entity_type_code IN ('route', 'inspection_point')
  );

-- 6) 退役基础字段
DELETE FROM dynamic_entity_type_base_field
WHERE tenant_id = 1 AND entity_type_code IN ('route', 'inspection_point');

-- 7) 退役门户
DELETE FROM dynamic_business_entry
WHERE tenant_id = 1 AND entity_type_code IN ('route', 'inspection_point');

DELETE FROM dynamic_business
WHERE tenant_id = 1 AND code IN ('route', 'inspection_point');

-- 8) 退役实体类型配置与类型
DELETE FROM dynamic_entity_type_config
WHERE tenant_id = 1 AND entity_type_code IN ('route', 'inspection_point');

DELETE FROM dynamic_entity_type
WHERE tenant_id = 1 AND code IN ('route', 'inspection_point');

COMMIT;

-- 序列对齐（样例 id 重导安全）
SELECT setval(pg_get_serial_sequence('dynamicbusiness.ent_inspection_point', 'id'),
  COALESCE((SELECT MAX(id) FROM ent_inspection_point), 1));
SELECT setval(pg_get_serial_sequence('dynamicbusiness.ent_route', 'id'),
  COALESCE((SELECT MAX(id) FROM ent_route), 1));
SELECT setval(pg_get_serial_sequence('dynamicbusiness.ent_inspection_item', 'id'),
  GREATEST(COALESCE((SELECT MAX(id) FROM ent_inspection_item), 1), 102));
SELECT setval(pg_get_serial_sequence('dynamicbusiness.ent_patrol_schedule', 'id'),
  GREATEST(COALESCE((SELECT MAX(id) FROM ent_patrol_schedule), 1), 201));
SELECT setval(pg_get_serial_sequence('dynamicbusiness.ent_patrol_object', 'id'),
  GREATEST(COALESCE((SELECT MAX(id) FROM ent_patrol_object), 1), 301));
SELECT setval(pg_get_serial_sequence('dynamicbusiness.ent_patrol_point', 'id'),
  GREATEST(COALESCE((SELECT MAX(id) FROM ent_patrol_point), 1), 502));
SELECT setval(pg_get_serial_sequence('dynamicbusiness.ent_task', 'id'),
  GREATEST(COALESCE((SELECT MAX(id) FROM ent_task), 1), 910005));
