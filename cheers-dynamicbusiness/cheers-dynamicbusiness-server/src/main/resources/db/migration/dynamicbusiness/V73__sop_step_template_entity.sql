-- V73: 步骤模板（step template）专用表 ent_sop_step_template
-- 设计：docs/superpowers/specs/2026-08-27-inspection-sop-template-instance-design.md
-- 元数据 seed：scripts/platform-import/sop/10_step_template_fields.sql、11_step_template_entity_type.sql

SET search_path TO dynamicbusiness, public;

CREATE SEQUENCE IF NOT EXISTS ent_sop_step_template_id_seq;

CREATE TABLE IF NOT EXISTS ent_sop_step_template (
  id                   BIGINT PRIMARY KEY DEFAULT nextval('ent_sop_step_template_id_seq'::regclass),
  tenant_id            BIGINT NOT NULL DEFAULT 0,
  entity_type_code     VARCHAR(64) DEFAULT 'sop_step_template',
  model_id             BIGINT NOT NULL,
  domain               VARCHAR(128),
  name                 VARCHAR(255) NOT NULL,
  code                 VARCHAR(100),
  parent_id            BIGINT DEFAULT 0,
  tree_path            VARCHAR(500),
  sort                 INTEGER DEFAULT 0,
  status               INTEGER DEFAULT 1,
  attrs                JSONB DEFAULT '{}'::jsonb,
  custom_fields        JSONB DEFAULT '{}'::jsonb,
  param_slots_json     JSONB NOT NULL DEFAULT '[]'::jsonb,
  creator              VARCHAR(64),
  create_time          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updater              VARCHAR(64),
  update_time          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  deleted              BOOLEAN DEFAULT FALSE
);

COMMENT ON TABLE ent_sop_step_template IS 'SOP 步骤模板；entityTypeCode=sop_step_template；全库共用步骤块';
COMMENT ON COLUMN ent_sop_step_template.param_slots_json IS 'param_slots_json（参数槽 schema JSON 数组）';

CREATE INDEX IF NOT EXISTS idx_ent_sop_step_template_tenant
  ON ent_sop_step_template (tenant_id) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_ent_sop_step_template_model
  ON ent_sop_step_template (model_id) WHERE deleted = FALSE;
CREATE UNIQUE INDEX IF NOT EXISTS uk_ent_sop_step_template_code_tenant
  ON ent_sop_step_template (code, tenant_id)
  WHERE deleted = FALSE AND code IS NOT NULL;

DO $$
DECLARE
  tid bigint;
  physical text;
BEGIN
  FOR tid IN
    SELECT DISTINCT tenant_id
    FROM (
      SELECT tenant_id
      FROM dynamic_entity_type
      WHERE deleted = false AND tenant_id IS NOT NULL AND tenant_id > 0
      UNION
      SELECT 1
    ) t
    ORDER BY 1
  LOOP
    physical := 'ent_sop_step_template_t' || tid::text;
    IF NOT EXISTS (
      SELECT 1 FROM information_schema.tables
      WHERE table_schema = 'dynamicbusiness' AND table_name = physical
    ) THEN
      EXECUTE format(
        'CREATE TABLE dynamicbusiness.%I (LIKE dynamicbusiness.ent_sop_step_template INCLUDING DEFAULTS INCLUDING CONSTRAINTS INCLUDING INDEXES)',
        physical);
    ELSE
      EXECUTE format(
        'ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS param_slots_json JSONB NOT NULL DEFAULT ''[]''::jsonb',
        physical);
    END IF;
    EXECUTE format('CREATE SEQUENCE IF NOT EXISTS dynamicbusiness.%I', physical || '_id_seq');
    EXECUTE format(
      'ALTER TABLE dynamicbusiness.%I ALTER COLUMN id SET DEFAULT nextval(%L::regclass)',
      physical, 'dynamicbusiness.' || physical || '_id_seq');
    BEGIN
      EXECUTE format(
        'ALTER SEQUENCE dynamicbusiness.%I OWNED BY dynamicbusiness.%I.id',
        physical || '_id_seq', physical);
    EXCEPTION WHEN OTHERS THEN
      NULL;
    END;
  END LOOP;

  TRUNCATE TABLE dynamicbusiness.ent_sop_step_template;
END $$;
