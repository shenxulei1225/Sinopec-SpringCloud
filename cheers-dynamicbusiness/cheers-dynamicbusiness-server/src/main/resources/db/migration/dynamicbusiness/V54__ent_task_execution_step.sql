-- V54: 任务执行步骤（过程明细）
-- 前置：task_excution_record / ent_task_excution_record_t* 已存在（勿在本迁移重复建）
-- REF execution_record_id → task_excution_record

SET search_path TO dynamicbusiness, public;

CREATE SEQUENCE IF NOT EXISTS ent_task_execution_step_id_seq;

CREATE TABLE IF NOT EXISTS ent_task_execution_step (
  id                      BIGINT PRIMARY KEY DEFAULT nextval('ent_task_execution_step_id_seq'::regclass),
  tenant_id               BIGINT NOT NULL DEFAULT 0,
  entity_type_code        VARCHAR(64) DEFAULT 'task_execution_step',
  model_id                BIGINT NOT NULL,
  domain                  VARCHAR(128),
  name                    VARCHAR(255) NOT NULL,
  code                    VARCHAR(100),
  parent_id               BIGINT DEFAULT 0,
  tree_path               VARCHAR(500),
  sort                    INTEGER DEFAULT 0,
  status                  INTEGER DEFAULT 1,
  attrs                   JSONB DEFAULT '{}'::jsonb,
  custom_fields           JSONB DEFAULT '{}'::jsonb,
  execution_record_id     BIGINT NOT NULL,
  step_code               VARCHAR(64) NOT NULL,
  step_order              INTEGER NOT NULL DEFAULT 0,
  step_title              VARCHAR(255),
  step_type               VARCHAR(64),
  step_required           BOOLEAN NOT NULL DEFAULT TRUE,
  step_status             VARCHAR(32) NOT NULL DEFAULT 'pending',
  result_payload          JSONB,
  evidence_refs           JSONB DEFAULT '[]'::jsonb,
  completed_at            TIMESTAMP,
  completed_by            VARCHAR(64),
  creator                 VARCHAR(64),
  create_time             TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updater                 VARCHAR(64),
  update_time             TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  deleted                 BOOLEAN DEFAULT FALSE
);

COMMENT ON TABLE ent_task_execution_step IS '任务执行步骤；REF execution_record_id → task_excution_record；开跑批量创建';
COMMENT ON COLUMN ent_task_execution_step.execution_record_id IS '所属执行记录 id（REF → task_excution_record）';
COMMENT ON COLUMN ent_task_execution_step.step_status IS 'pending / in_progress / completed / skipped';

CREATE INDEX IF NOT EXISTS idx_ent_task_execution_step_tenant
  ON ent_task_execution_step (tenant_id) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_ent_task_execution_step_model
  ON ent_task_execution_step (model_id) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_ent_task_execution_step_record
  ON ent_task_execution_step (execution_record_id) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_ent_task_execution_step_record_order
  ON ent_task_execution_step (execution_record_id, step_order) WHERE deleted = FALSE;
CREATE UNIQUE INDEX IF NOT EXISTS uk_ent_task_execution_step_record_code
  ON ent_task_execution_step (execution_record_id, step_code) WHERE deleted = FALSE;

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
    physical := 'ent_task_execution_step_t' || tid::text;
    IF NOT EXISTS (
      SELECT 1 FROM information_schema.tables
      WHERE table_schema = 'dynamicbusiness' AND table_name = physical
    ) THEN
      EXECUTE format(
        'CREATE TABLE dynamicbusiness.%I (LIKE dynamicbusiness.ent_task_execution_step INCLUDING DEFAULTS INCLUDING CONSTRAINTS INCLUDING INDEXES)',
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

  TRUNCATE TABLE dynamicbusiness.ent_task_execution_step;
END $$;
