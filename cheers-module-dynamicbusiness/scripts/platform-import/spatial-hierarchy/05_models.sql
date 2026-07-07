-- ============================================================================
-- 空间层级 · 05 模型（facility / zone）
-- Generated: 2026-07-07
-- region 模型不在 SQL 预置：由 category 树 + Pattern C 在产品中配置
-- 依赖：system/03_fields.sql、Flyway V22+
-- ============================================================================

SET search_path TO dynamicbusiness;

-- 设施（facility）
INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES
  ('MODEL-FACILITY-STATION', '站场', 'facility', '油气储运站场等设施点', 1, 1, NULL, 1, 'seed'),
  ('MODEL-FACILITY-PLANT', '厂区', 'facility', '工厂、化工园区厂区', 1, 2, NULL, 1, 'seed'),
  ('MODEL-FACILITY-DEPOT', '油库', 'facility', '油库设施点', 1, 3, NULL, 1, 'seed'),
  ('MODEL-FACILITY-OFFICE', '机关/楼宇', 'facility', '机关办公与楼宇设施点', 1, 4, NULL, 1, 'seed')
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

-- 站内分区（zone）
INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES
  ('MODEL-ZONE-TANK-GROUP', '罐组', 'zone', '储罐分区', 1, 1, NULL, 1, 'seed'),
  ('MODEL-ZONE-WAREHOUSE', '库棚', 'zone', '仓储分区', 1, 2, NULL, 1, 'seed'),
  ('MODEL-ZONE-FUNCTIONAL', '功能分区', 'zone', '装卸区、消防分区等', 1, 3, NULL, 1, 'seed'),
  ('MODEL-ZONE-BUILDING', '楼栋', 'zone', '厂区内楼栋', 1, 4, NULL, 1, 'seed')
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

-- 废弃旧站场包模型
UPDATE dynamic_model
SET status = 0,
    description = COALESCE(description, '') || ' [已废弃：改用 MODEL-FACILITY-* / MODEL-ZONE-*]',
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE code IN ('MODEL-REGION-SITE', 'MODEL-REGION-TANK-GROUP')
  AND tenant_id = 1
  AND deleted = false;
