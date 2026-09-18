-- ============================================================================
-- data_protocol 租户物理表
--
-- 权威：实体专用表必须是 ent_{code}_t{tenantId}；无后缀基表只作 DDL 模板。
-- 协议内容可对各租户种子同步，但不得共用无后缀实体表当写路径。
-- ============================================================================

SET search_path TO dynamicbusiness, public;

CREATE SEQUENCE IF NOT EXISTS ent_data_protocol_id_seq;

CREATE TABLE IF NOT EXISTS ent_data_protocol (
  id                BIGINT PRIMARY KEY DEFAULT nextval('ent_data_protocol_id_seq'::regclass),
  tenant_id         BIGINT NOT NULL DEFAULT 0,
  entity_type_code  VARCHAR(64) DEFAULT 'data_protocol',
  model_id          BIGINT NOT NULL,
  domain            VARCHAR(128),
  name              VARCHAR(255) NOT NULL,
  code              VARCHAR(100),
  parent_id         BIGINT DEFAULT 0,
  tree_path         VARCHAR(500),
  sort              INTEGER DEFAULT 0,
  status            INTEGER DEFAULT 1,
  custom_fields     JSONB DEFAULT '{}'::jsonb,
  creator           VARCHAR(64),
  create_time       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updater           VARCHAR(64),
  update_time       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  deleted           BOOLEAN DEFAULT FALSE
);

COMMENT ON TABLE ent_data_protocol IS '数据协议指令实体模板表（无后缀，不承载租户活数据）';
COMMENT ON COLUMN ent_data_protocol.custom_fields IS '协议检索字段与结构化 JSON 存扩展列；可查询字段同步索引表';

CREATE INDEX IF NOT EXISTS idx_ent_data_protocol_tenant
  ON ent_data_protocol (tenant_id) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_ent_data_protocol_model
  ON ent_data_protocol (model_id) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_ent_data_protocol_status
  ON ent_data_protocol (status) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_ent_data_protocol_custom_fields
  ON ent_data_protocol USING GIN (custom_fields);
CREATE UNIQUE INDEX IF NOT EXISTS uk_ent_data_protocol_code_tenant
  ON ent_data_protocol (code, tenant_id)
  WHERE deleted = FALSE AND code IS NOT NULL;

-- 按已有租户复制物理表（与动作库 V76 同一模式）
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
    physical := 'ent_data_protocol_t' || tid::text;
    IF NOT EXISTS (
      SELECT 1 FROM information_schema.tables
      WHERE table_schema = 'dynamicbusiness' AND table_name = physical
    ) THEN
      EXECUTE format(
        'CREATE TABLE dynamicbusiness.%I (LIKE dynamicbusiness.ent_data_protocol INCLUDING DEFAULTS INCLUDING CONSTRAINTS INCLUDING INDEXES)',
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

  TRUNCATE TABLE dynamicbusiness.ent_data_protocol;
END $$;

-- 元数据必须指向当前租户物理表，禁止停在无后缀名
UPDATE dynamic_entity_type
SET dedicated_table_name = 'ent_data_protocol_t' || tenant_id::text,
    updater = 'flyway',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND code = 'data_protocol'
  AND tenant_id IS NOT NULL
  AND tenant_id > 0
  AND dedicated_table_name IS DISTINCT FROM ('ent_data_protocol_t' || tenant_id::text);

UPDATE dynamic_entity_type_config
SET dedicated_table_name = 'ent_data_protocol_t' || tenant_id::text,
    updater = 'flyway',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND entity_type_code = 'data_protocol'
  AND tenant_id IS NOT NULL
  AND tenant_id > 0
  AND dedicated_table_name IS DISTINCT FROM ('ent_data_protocol_t' || tenant_id::text);
