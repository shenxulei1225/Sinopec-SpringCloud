-- V43: 现场作业标准（field_work_standard / SOP）专用表
-- 步骤正文唯一真源：steps_json；可版本发布（version_no + publish_status）
-- 按租户拆分 _t{tenantId}，基表清空作 LIKE 模板（对齐 V37 约定）

SET search_path TO dynamicbusiness, public;

CREATE SEQUENCE IF NOT EXISTS ent_field_work_standard_id_seq;

CREATE TABLE IF NOT EXISTS ent_field_work_standard (
  id                   BIGINT PRIMARY KEY DEFAULT nextval('ent_field_work_standard_id_seq'::regclass),
  tenant_id            BIGINT NOT NULL DEFAULT 0,
  entity_type_code     VARCHAR(64) DEFAULT 'field_work_standard',
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
  version_no           INTEGER NOT NULL DEFAULT 1,
  publish_status       VARCHAR(32) NOT NULL DEFAULT 'DRAFT',
  steps_json           JSONB NOT NULL DEFAULT '[]'::jsonb,
  creator              VARCHAR(64),
  create_time          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updater              VARCHAR(64),
  update_time          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  deleted              BOOLEAN DEFAULT FALSE
);

COMMENT ON TABLE ent_field_work_standard IS '现场作业标准（SOP）；entityTypeCode=field_work_standard';
COMMENT ON COLUMN ent_field_work_standard.version_no IS 'version_no（版本号）；同 code 下递增';
COMMENT ON COLUMN ent_field_work_standard.publish_status IS 'publish_status（发布状态）；DRAFT/PUBLISHED';
COMMENT ON COLUMN ent_field_work_standard.steps_json IS 'steps_json（有序步骤 JSON）；怎么做唯一正文';

CREATE INDEX IF NOT EXISTS idx_ent_field_work_standard_tenant
  ON ent_field_work_standard (tenant_id) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_ent_field_work_standard_model
  ON ent_field_work_standard (model_id) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_ent_field_work_standard_publish
  ON ent_field_work_standard (publish_status) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_ent_field_work_standard_domain
  ON ent_field_work_standard (domain) WHERE deleted = FALSE;
CREATE UNIQUE INDEX IF NOT EXISTS uk_ent_field_work_standard_code_ver_tenant
  ON ent_field_work_standard (code, version_no, tenant_id)
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
    physical := 'ent_field_work_standard_t' || tid::text;
    IF NOT EXISTS (
      SELECT 1 FROM information_schema.tables
      WHERE table_schema = 'dynamicbusiness' AND table_name = physical
    ) THEN
      EXECUTE format(
        'CREATE TABLE dynamicbusiness.%I (LIKE dynamicbusiness.ent_field_work_standard INCLUDING DEFAULTS INCLUDING CONSTRAINTS INCLUDING INDEXES)',
        physical);
    ELSE
      EXECUTE format(
        'ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS version_no INTEGER NOT NULL DEFAULT 1',
        physical);
      EXECUTE format(
        'ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS publish_status VARCHAR(32) NOT NULL DEFAULT ''DRAFT''',
        physical);
      EXECUTE format(
        'ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS steps_json JSONB NOT NULL DEFAULT ''[]''::jsonb',
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

  TRUNCATE TABLE dynamicbusiness.ent_field_work_standard;
END $$;
