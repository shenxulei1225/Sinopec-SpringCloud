-- V74: SOP 模板/实例列（is_template、sop_template_id、override、default_*）
-- 设计：docs/superpowers/specs/2026-08-27-inspection-sop-template-instance-design.md
-- 保留 steps_json 供存量；新模型用 default_steps_json / default_params_json + merge

SET search_path TO dynamicbusiness, public;

DO $$
DECLARE
  tbl text;
BEGIN
  FOR tbl IN
    SELECT t.table_name
    FROM information_schema.tables t
    WHERE t.table_schema = 'dynamicbusiness'
      AND t.table_type = 'BASE TABLE'
      AND t.table_name ~ '^ent_sop(_t[0-9]+)?$'
  LOOP
    EXECUTE format(
      'ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS is_template BOOLEAN NOT NULL DEFAULT FALSE',
      tbl);
    EXECUTE format(
      'ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS sop_template_id BIGINT',
      tbl);
    EXECUTE format(
      'ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS step_override_json JSONB',
      tbl);
    EXECUTE format(
      'ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS param_override_json JSONB',
      tbl);
    EXECUTE format(
      'ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS default_steps_json JSONB NOT NULL DEFAULT ''[]''::jsonb',
      tbl);
    EXECUTE format(
      'ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS default_params_json JSONB NOT NULL DEFAULT ''{}''::jsonb',
      tbl);
    EXECUTE format(
      'ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS execution_means VARCHAR(64)',
      tbl);
    EXECUTE format(
      'ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS procedure_kind VARCHAR(64)',
      tbl);

    EXECUTE format(
      'COMMENT ON COLUMN dynamicbusiness.%I.is_template IS %L',
      tbl, '是否 SOP 模板行（true=模板；false=实例）');
    EXECUTE format(
      'COMMENT ON COLUMN dynamicbusiness.%I.sop_template_id IS %L',
      tbl, '实例引用的 SOP 模板 id（REF → sop；模板行为空）');
    EXECUTE format(
      'COMMENT ON COLUMN dynamicbusiness.%I.step_override_json IS %L',
      tbl, '实例步骤差量 JSON');
    EXECUTE format(
      'COMMENT ON COLUMN dynamicbusiness.%I.param_override_json IS %L',
      tbl, '实例参数差量 JSON');
    EXECUTE format(
      'COMMENT ON COLUMN dynamicbusiness.%I.default_steps_json IS %L',
      tbl, '模板默认步骤列表 JSON（merge 真源之一）');
    EXECUTE format(
      'COMMENT ON COLUMN dynamicbusiness.%I.default_params_json IS %L',
      tbl, '模板默认参数 JSON（merge 真源之一）');
    EXECUTE format(
      'COMMENT ON COLUMN dynamicbusiness.%I.execution_means IS %L',
      tbl, '执行手段（MANUAL/UAV/ROBOT/FIXED_CAMERA 等）');
    EXECUTE format(
      'COMMENT ON COLUMN dynamicbusiness.%I.procedure_kind IS %L',
      tbl, '流程种类（leak/corrosion 等，可选）');

    EXECUTE format(
      'CREATE INDEX IF NOT EXISTS idx_%s_is_template ON dynamicbusiness.%I (is_template) WHERE deleted = false',
      tbl, tbl);
    EXECUTE format(
      'CREATE INDEX IF NOT EXISTS idx_%s_sop_template_id ON dynamicbusiness.%I (sop_template_id) WHERE deleted = false',
      tbl, tbl);
  END LOOP;
END $$;

-- 存量 SOP 行视为模板（仍保留 steps_json 正文；新字段由 seed 或后续迁移填充）
DO $$
DECLARE
  tbl text;
BEGIN
  FOR tbl IN
    SELECT t.table_name
    FROM information_schema.tables t
    WHERE t.table_schema = 'dynamicbusiness'
      AND t.table_type = 'BASE TABLE'
      AND t.table_name ~ '^ent_sop(_t[0-9]+)?$'
  LOOP
    EXECUTE format(
      $q$
        UPDATE dynamicbusiness.%I
        SET is_template = true,
            updater = COALESCE(updater, 'flyway-v74'),
            update_time = CURRENT_TIMESTAMP
        WHERE deleted = false
          AND sop_template_id IS NULL
          AND is_template = false
      $q$,
      tbl);
  END LOOP;
END $$;
