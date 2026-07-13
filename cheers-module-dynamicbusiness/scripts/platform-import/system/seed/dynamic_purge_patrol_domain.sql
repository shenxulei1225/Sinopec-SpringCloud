-- ============================================================================
-- 物理清除巡检域（patrol）及子类型 · tenant_id=1
--
-- 范围：
--   entityType: patrol, patrol_schedule, patrol_object, patrol_point
--   表: ent_patrol, ent_patrol_schedule, ent_patrol_object, ent_patrol_point
--   categoryType: patrol, patrol_schedule, patrol_object, patrol_point
--   门户: patrol_mgmt 及其子菜单
--   task 模型上对 patrol_* 的关联字段分配、entityType 关系
--
-- 保留：fault（故障管理）、inspection_item（检查内容）— 仅解除 patrol 父级
-- 幂等：可重复执行
-- ============================================================================

SET search_path TO dynamicbusiness;

DO $$
DECLARE
  patrol_codes text[] := ARRAY['patrol', 'patrol_schedule', 'patrol_object', 'patrol_point'];
  cat_codes text[] := ARRAY['patrol', 'patrol_schedule', 'patrol_object', 'patrol_point'];
BEGIN
  -- 0. 子类型 fault 脱离 patrol 树（保留故障管理）
  UPDATE dynamic_entity_type
  SET parent_id = NULL, updater = 'purge-patrol-domain', update_time = CURRENT_TIMESTAMP
  WHERE deleted = false AND tenant_id = 1 AND code = 'fault';

  -- 1. task 模型字段分配：去掉指向 patrol 域的 ENTITY_REF
  DELETE FROM dynamic_model_field_assignment
  WHERE tenant_id = 1
    AND (
      target_entity_type = ANY (patrol_codes)
      OR model_code IN ('patrol_schedule', 'patrol_object', 'patrol_point')
    );

  -- 2. 实体关联
  DELETE FROM dynamic_entity_relation
  WHERE tenant_id = 1
    AND (
      source_entity_type_code = ANY (patrol_codes)
      OR target_entity_type_code = ANY (patrol_codes)
    );

  -- 3. 实体-分类
  DELETE FROM dynamic_entity_category_relation
  WHERE tenant_id = 1 AND entity_type_code = ANY (patrol_codes);

  -- 4. 分类-实体 link（Pattern C）
  DELETE FROM dynamic_category_entity_link
  WHERE tenant_id = 1
    AND category_id IN (
      SELECT id FROM dynamic_category
      WHERE tenant_id = 1 AND category_type_code = ANY (cat_codes)
    );

  -- 5. 模型-分类
  DELETE FROM dynamic_model_category_relation
  WHERE tenant_id = 1
    AND (
      entity_type_code = ANY (patrol_codes)
      OR category_code IN (
        SELECT code FROM dynamic_category
        WHERE tenant_id = 1 AND category_type_code = ANY (cat_codes)
      )
    );

  -- 6. 实体行
  DELETE FROM ent_patrol_schedule WHERE tenant_id = 1;
  DELETE FROM ent_patrol_object WHERE tenant_id = 1;
  DELETE FROM ent_patrol_point WHERE tenant_id = 1;
  DELETE FROM ent_patrol WHERE tenant_id = 1;

  -- 7. CRUD 表单 / 能力投影
  DELETE FROM model_crud_form_definition
  WHERE tenant_id = 1 AND entity_type_code = ANY (patrol_codes);

  DELETE FROM capability_component_projection
  WHERE tenant_id = 1 AND entity_type_code = ANY (patrol_codes);

  DELETE FROM business_capability
  WHERE tenant_id = 1 AND entity_type_code = ANY (patrol_codes);

  -- 8. 模型字段分配（patrol 模型自身）
  DELETE FROM dynamic_model_field_assignment
  WHERE tenant_id = 1
    AND model_code IN ('patrol_schedule', 'patrol_object', 'patrol_point');

  -- 9. 模型
  DELETE FROM dynamic_model
  WHERE tenant_id = 1 AND entity_type_code = ANY (patrol_codes);

  -- 10. 数据管理维度
  DELETE FROM dm_entity_dimension
  WHERE tenant_id = 1 AND entity_type_code = ANY (patrol_codes);

  -- 11. 类型基础字段
  DELETE FROM dynamic_entity_type_base_field
  WHERE tenant_id = 1 AND entity_type_code = ANY (patrol_codes);

  -- 12. 类型配置
  DELETE FROM dynamic_entity_type_config
  WHERE tenant_id = 1 AND entity_type_code = ANY (patrol_codes);

  -- 13. 类型关系（含 task → patrol_*）
  DELETE FROM dynamic_entity_type_relation
  WHERE tenant_id = 1
    AND (
      source_entity_type_code = ANY (patrol_codes)
      OR target_entity_type_code = ANY (patrol_codes)
    );

  -- 14. 门户菜单
  DELETE FROM dynamic_business_entry
  WHERE tenant_id = 1 AND entity_type_code = ANY (patrol_codes);

  DELETE FROM dynamic_business
  WHERE tenant_id = 1
    AND code IN ('patrol_mgmt', 'patrol_schedule', 'patrol_object', 'patrol_point');

  -- 15. 分类节点
  DELETE FROM dynamic_category
  WHERE tenant_id = 1 AND category_type_code = ANY (cat_codes);

  -- 16. 分类种类
  DELETE FROM dynamic_category_type
  WHERE tenant_id = 1 AND category_type_code = ANY (cat_codes);

  -- 17. 数据类型（子类型先于父类型）
  DELETE FROM dynamic_entity_type
  WHERE tenant_id = 1 AND code = ANY (patrol_codes);

END $$;

-- 18. 物理删表
DROP TABLE IF EXISTS ent_patrol_point CASCADE;
DROP TABLE IF EXISTS ent_patrol_object CASCADE;
DROP TABLE IF EXISTS ent_patrol_schedule CASCADE;
DROP TABLE IF EXISTS ent_patrol CASCADE;

-- 可选：删序列（若存在）
DROP SEQUENCE IF EXISTS ent_patrol_id_seq CASCADE;
DROP SEQUENCE IF EXISTS ent_patrol_schedule_id_seq CASCADE;
DROP SEQUENCE IF EXISTS ent_patrol_object_id_seq CASCADE;
DROP SEQUENCE IF EXISTS ent_patrol_point_id_seq CASCADE;
