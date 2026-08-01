-- V43: 检查方法（inspection_method）专用表
-- 核心列对齐 EntityDO；基础字段固定列 is_template / action_duration_sec（编码=列名）
-- 按租户拆分 _t{tenantId}，基表清空作 LIKE 模板（对齐 V37 约定）

SET search_path TO dynamicbusiness, public;

CREATE SEQUENCE IF NOT EXISTS ent_inspection_method_id_seq;

CREATE TABLE IF NOT EXISTS ent_inspection_method (
  id                   BIGINT PRIMARY KEY DEFAULT nextval('ent_inspection_method_id_seq'::regclass),
  tenant_id            BIGINT NOT NULL DEFAULT 0,
  entity_type_code     VARCHAR(64) DEFAULT 'inspection_method',
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
  -- 基础字段固定列（编码 = 列名）
  is_template          BOOLEAN DEFAULT FALSE,
  action_duration_sec  BIGINT,
  creator              VARCHAR(64),
  create_time          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updater              VARCHAR(64),
  update_time          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  deleted              BOOLEAN DEFAULT FALSE
);

COMMENT ON TABLE ent_inspection_method IS '检查方法（模板/实例同表）；entityTypeCode=inspection_method';
COMMENT ON COLUMN ent_inspection_method.is_template IS 'is_template（是否模板）；标准库只展示 true';
COMMENT ON COLUMN ent_inspection_method.action_duration_sec IS 'action_duration_sec（动作耗时秒）';

ALTER TABLE ent_inspection_method
  ADD COLUMN IF NOT EXISTS is_template BOOLEAN DEFAULT FALSE;
ALTER TABLE ent_inspection_method
  ADD COLUMN IF NOT EXISTS action_duration_sec BIGINT;

CREATE INDEX IF NOT EXISTS idx_ent_inspection_method_tenant
  ON ent_inspection_method (tenant_id) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_ent_inspection_method_model
  ON ent_inspection_method (model_id) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_ent_inspection_method_is_template
  ON ent_inspection_method (is_template) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_ent_inspection_method_domain
  ON ent_inspection_method (domain) WHERE deleted = FALSE;
CREATE UNIQUE INDEX IF NOT EXISTS uk_ent_inspection_method_code_tenant
  ON ent_inspection_method (code, tenant_id) WHERE deleted = FALSE AND code IS NOT NULL;

-- 为已有租户建物理分表（基表作模板后清空）
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
    physical := 'ent_inspection_method_t' || tid::text;
    IF NOT EXISTS (
      SELECT 1 FROM information_schema.tables
      WHERE table_schema = 'dynamicbusiness' AND table_name = physical
    ) THEN
      EXECUTE format(
        'CREATE TABLE dynamicbusiness.%I (LIKE dynamicbusiness.ent_inspection_method INCLUDING DEFAULTS INCLUDING CONSTRAINTS INCLUDING INDEXES)',
        physical);
    ELSE
      EXECUTE format(
        'ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS is_template BOOLEAN DEFAULT FALSE',
        physical);
      EXECUTE format(
        'ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS action_duration_sec BIGINT',
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

  TRUNCATE TABLE dynamicbusiness.ent_inspection_method;
END $$;
