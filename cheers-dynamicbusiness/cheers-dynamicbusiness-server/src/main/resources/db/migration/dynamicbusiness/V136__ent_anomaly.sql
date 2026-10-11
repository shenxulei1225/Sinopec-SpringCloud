-- 异常记录独立账。
-- 设备采集和巡检检查项发现的异常都记在这里，不进任务执行记录。
-- 元数据与存量挪账在本迁移一次做完；种子脚本不得再把巡检异常型号写回执行记录。

SET search_path TO dynamicbusiness, public;

CREATE SEQUENCE IF NOT EXISTS ent_anomaly_id_seq;

CREATE TABLE IF NOT EXISTS ent_anomaly (
  id                BIGINT PRIMARY KEY DEFAULT nextval('ent_anomaly_id_seq'::regclass),
  tenant_id         BIGINT NOT NULL DEFAULT 0,
  entity_type_code  VARCHAR(64) DEFAULT 'anomaly',
  model_id          BIGINT NOT NULL,
  name              VARCHAR(255) NOT NULL,
  code              VARCHAR(100),
  status            INTEGER DEFAULT 1,
  area_id           BIGINT,
  parent_id         BIGINT DEFAULT 0,
  attrs             JSONB DEFAULT '{}'::jsonb,
  custom_fields     JSONB DEFAULT '{}'::jsonb,
  creator           VARCHAR(64),
  create_time       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updater           VARCHAR(64),
  update_time       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  deleted           BOOLEAN DEFAULT FALSE,
  tree_path         VARCHAR(500),
  sort              INTEGER DEFAULT 0,
  domain            VARCHAR(128),
  facility_id       BIGINT
);

COMMENT ON TABLE ent_anomaly IS '异常记录模板表（无后缀，不承载租户活数据）';
COMMENT ON COLUMN ent_anomaly.entity_type_code IS '底座类型 anomaly；来源写在记录上，不靠业务域拆门';

CREATE INDEX IF NOT EXISTS idx_ent_anomaly_tenant
  ON ent_anomaly (tenant_id) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_ent_anomaly_model
  ON ent_anomaly (model_id) WHERE deleted = FALSE;
CREATE UNIQUE INDEX IF NOT EXISTS uk_ent_anomaly_code_tenant
  ON ent_anomaly (code, tenant_id)
  WHERE deleted = FALSE AND code IS NOT NULL;

INSERT INTO dynamic_group (group_type, code, name, sort, status, creator, tenant_id)
SELECT 'ENTITY_TYPE', 'ETG-ANOMALY', '异常管理', 0, 1, 'flyway', 1
WHERE NOT EXISTS (
  SELECT 1 FROM dynamic_group
  WHERE deleted = false AND group_type = 'ENTITY_TYPE' AND name = '异常管理' AND tenant_id = 1
);

INSERT INTO dynamic_entity_type (
  code, name, description, icon, alias, sort, status, type_level,
  association_fields, storage_type, dedicated_table_name, enable_rule_engine,
  creator, tenant_id, group_name, entry_kind, model_workbench_mode, work_scope
)
SELECT
  'anomaly', '异常记录',
  '设备采集和巡检检查项发现的异常。不进任务执行记录。',
  'fa:exclamation-circle', '异常记录', 45, 'active', 'USER',
  '{}'::jsonb, 'DEDICATED', 'ent_anomaly_t1', false,
  'flyway', 1, '异常管理', 'NATIVE', 'MULTI', 'FACILITY'
WHERE NOT EXISTS (
  SELECT 1 FROM dynamic_entity_type
  WHERE deleted = false AND tenant_id = 1 AND code = 'anomaly'
);

INSERT INTO dynamic_entity_type_config (
  entity_type_code, name, storage_type, dedicated_table_name, enable_rule_engine,
  description, status, tenant_id, creator
)
SELECT
  'anomaly', '异常记录', 'DEDICATED', 'ent_anomaly_t1', false,
  '异常记录专用表', 1, 1, 'flyway'
WHERE NOT EXISTS (
  SELECT 1 FROM dynamic_entity_type_config
  WHERE deleted = false AND tenant_id = 1 AND entity_type_code = 'anomaly'
);

UPDATE dynamic_entity_type
SET description = '任务执行记录（巡检轮次、维修作业日志）。异常另记在异常记录。',
    updater = 'flyway',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false AND code = 'task_excution_record';

UPDATE dynamic_model
SET entity_type_code = 'anomaly',
    domain = NULL,
    description = '巡检检查项发现的异常。与设备采集的异常同属异常记录，不进任务执行记录。',
    updater = 'flyway',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false AND code = 'exec_patrol_issue';

UPDATE dynamic_model_category_relation_t1
SET deleted = true, updater = 'flyway', update_time = CURRENT_TIMESTAMP
WHERE deleted = false AND model_code = 'exec_patrol_issue' AND entity_type_code = 'task_excution_record';

DO $$
DECLARE
  tid bigint;
  physical text;
  source_physical text;
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
    physical := 'ent_anomaly_t' || tid::text;
    IF NOT EXISTS (
      SELECT 1 FROM information_schema.tables
      WHERE table_schema = 'dynamicbusiness' AND table_name = physical
    ) THEN
      EXECUTE format(
        'CREATE TABLE dynamicbusiness.%I (LIKE dynamicbusiness.ent_anomaly INCLUDING DEFAULTS INCLUDING CONSTRAINTS INCLUDING INDEXES)',
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

    source_physical := 'ent_task_excution_record_t' || tid::text;
    IF EXISTS (
      SELECT 1 FROM information_schema.tables
      WHERE table_schema = 'dynamicbusiness' AND table_name = source_physical
    ) THEN
      IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'dynamicbusiness' AND table_name = source_physical AND column_name = 'facility_id'
      ) THEN
        EXECUTE format(
          'INSERT INTO dynamicbusiness.%I (
             id, tenant_id, entity_type_code, model_id, name, code, status, area_id, parent_id,
             attrs, custom_fields, creator, create_time, updater, update_time, deleted,
             tree_path, sort, domain, facility_id)
           SELECT src.id, src.tenant_id, ''anomaly'', src.model_id, src.name, src.code, src.status,
                  src.area_id, src.parent_id, src.attrs,
                  COALESCE(src.custom_fields, ''{}''::jsonb) || ''{"source":"inspection_check"}''::jsonb,
                  src.creator, src.create_time, ''flyway'', CURRENT_TIMESTAMP, false,
                  src.tree_path, src.sort, NULL, src.facility_id
           FROM dynamicbusiness.%I src
           WHERE src.deleted = false
             AND src.model_id IN (
               SELECT id FROM dynamicbusiness.dynamic_model
               WHERE deleted = false AND code = ''exec_patrol_issue'')
             AND NOT EXISTS (
               SELECT 1 FROM dynamicbusiness.%I dst WHERE dst.id = src.id)',
          physical, source_physical, physical);
      ELSE
        EXECUTE format(
          'INSERT INTO dynamicbusiness.%I (
             id, tenant_id, entity_type_code, model_id, name, code, status, area_id, parent_id,
             attrs, custom_fields, creator, create_time, updater, update_time, deleted,
             tree_path, sort, domain, facility_id)
           SELECT src.id, src.tenant_id, ''anomaly'', src.model_id, src.name, src.code, src.status,
                  src.area_id, src.parent_id, src.attrs,
                  COALESCE(src.custom_fields, ''{}''::jsonb) || ''{"source":"inspection_check"}''::jsonb,
                  src.creator, src.create_time, ''flyway'', CURRENT_TIMESTAMP, false,
                  src.tree_path, src.sort, NULL, NULL
           FROM dynamicbusiness.%I src
           WHERE src.deleted = false
             AND src.model_id IN (
               SELECT id FROM dynamicbusiness.dynamic_model
               WHERE deleted = false AND code = ''exec_patrol_issue'')
             AND NOT EXISTS (
               SELECT 1 FROM dynamicbusiness.%I dst WHERE dst.id = src.id)',
          physical, source_physical, physical);
      END IF;
      EXECUTE format(
        'UPDATE dynamicbusiness.%I src
         SET deleted = true, updater = ''flyway'', update_time = CURRENT_TIMESTAMP
         WHERE src.deleted = false
           AND src.model_id IN (
             SELECT id FROM dynamicbusiness.dynamic_model
             WHERE deleted = false AND code = ''exec_patrol_issue'')
           AND EXISTS (
             SELECT 1 FROM dynamicbusiness.%I dst WHERE dst.id = src.id AND dst.deleted = false)',
        source_physical, physical);
      EXECUTE format(
        'SELECT setval(%L::regclass, GREATEST((SELECT COALESCE(MAX(id), 1) FROM dynamicbusiness.%I), 1))',
        'dynamicbusiness.' || physical || '_id_seq', physical);
    END IF;
  END LOOP;

  TRUNCATE TABLE dynamicbusiness.ent_anomaly;
END $$;

UPDATE dynamic_entity_category_relation_t1 rel
SET deleted = true, updater = 'flyway', update_time = CURRENT_TIMESTAMP
WHERE rel.deleted = false
  AND rel.entity_type_code = 'task_excution_record'
  AND rel.entity_id IN (
    SELECT id FROM dynamicbusiness.ent_anomaly_t1 WHERE deleted = false
  );
