-- V79: 废弃步骤模板实体（迁入动作库后软删元数据 + 物理表改名归档）
-- 设计：docs/superpowers/specs/2026-08-29-action-library-sop-task-tree-design.md
-- 前置：V76 动作表、V78 动作树列与数据迁移

SET search_path TO dynamicbusiness, public;

-- 1) 软删实体类型 / 配置 / 型号 / 分类类型
UPDATE dynamic_entity_type
SET deleted = true,
    status = 'inactive',
    updater = 'flyway-v79',
    update_time = CURRENT_TIMESTAMP
WHERE code = 'sop_step_template'
  AND deleted = false;

UPDATE dynamic_entity_type_config
SET deleted = true,
    updater = 'flyway-v79',
    update_time = CURRENT_TIMESTAMP
WHERE entity_type_code = 'sop_step_template'
  AND deleted = false;

UPDATE dynamic_model
SET deleted = true,
    updater = 'flyway-v79',
    update_time = CURRENT_TIMESTAMP
WHERE code = 'sop_step_template'
  AND deleted = false;

UPDATE dynamic_category_type
SET deleted = true,
    updater = 'flyway-v79',
    update_time = CURRENT_TIMESTAMP
WHERE category_type_code = 'sop_step_template'
  AND deleted = false;

UPDATE dynamic_category
SET deleted = true,
    updater = 'flyway-v79',
    update_time = CURRENT_TIMESTAMP
WHERE category_type_code = 'sop_step_template'
  AND deleted = false;

UPDATE dynamic_entity_type_base_field
SET deleted = true,
    updater = 'flyway-v79',
    update_time = CURRENT_TIMESTAMP
WHERE entity_type_code = 'sop_step_template'
  AND deleted = false;

UPDATE dynamic_model_field_assignment
SET deleted = true,
    updater = 'flyway-v79',
    update_time = CURRENT_TIMESTAMP
WHERE model_code = 'sop_step_template'
  AND deleted = false;

-- 2) 物理表改名归档：ent_sop_step_template → *_deprecated_v79（含租户表）
DO $$
DECLARE
  src text;
  dst text;
  seq_src text;
  seq_dst text;
BEGIN
  FOR src IN
    SELECT t.table_name
    FROM information_schema.tables t
    WHERE t.table_schema = 'dynamicbusiness'
      AND t.table_type = 'BASE TABLE'
      AND t.table_name ~ '^ent_sop_step_template(_t[0-9]+)?$'
      AND t.table_name !~ '_deprecated_v79$'
  LOOP
    dst := src || '_deprecated_v79';
    IF EXISTS (
      SELECT 1 FROM information_schema.tables
      WHERE table_schema = 'dynamicbusiness' AND table_name = dst
    ) THEN
      -- 目标已存在：仅标注注释，避免二次 rename 失败
      EXECUTE format(
        'COMMENT ON TABLE dynamicbusiness.%I IS %L',
        src, '【废弃·V79】步骤模板；请改用 ent_action；本表待清理');
      CONTINUE;
    END IF;

    EXECUTE format('ALTER TABLE dynamicbusiness.%I RENAME TO %I', src, dst);
    EXECUTE format(
      'COMMENT ON TABLE dynamicbusiness.%I IS %L',
      dst, '【废弃·V79】原步骤模板表 ent_sop_step_template；已软删类型元数据；请改用 ent_action');

    seq_src := src || '_id_seq';
    seq_dst := dst || '_id_seq';
    IF EXISTS (
      SELECT 1 FROM pg_class c
      JOIN pg_namespace n ON n.oid = c.relnamespace
      WHERE n.nspname = 'dynamicbusiness' AND c.relkind = 'S' AND c.relname = seq_src
    ) AND NOT EXISTS (
      SELECT 1 FROM pg_class c
      JOIN pg_namespace n ON n.oid = c.relnamespace
      WHERE n.nspname = 'dynamicbusiness' AND c.relkind = 'S' AND c.relname = seq_dst
    ) THEN
      EXECUTE format('ALTER SEQUENCE dynamicbusiness.%I RENAME TO %I', seq_src, seq_dst);
    END IF;
  END LOOP;
END $$;
