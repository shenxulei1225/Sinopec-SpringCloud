-- V76: 动作库专用表 ent_action（替代步骤模板）
-- 设计：docs/superpowers/specs/2026-08-29-action-library-sop-task-tree-design.md
-- 元数据 seed：scripts/platform-import/action/

SET search_path TO dynamicbusiness, public;

CREATE SEQUENCE IF NOT EXISTS ent_action_id_seq;

CREATE TABLE IF NOT EXISTS ent_action (
  id                     BIGINT PRIMARY KEY DEFAULT nextval('ent_action_id_seq'::regclass),
  tenant_id              BIGINT NOT NULL DEFAULT 0,
  entity_type_code       VARCHAR(64) DEFAULT 'action',
  model_id               BIGINT NOT NULL,
  domain                 VARCHAR(128),
  name                   VARCHAR(255) NOT NULL,
  code                   VARCHAR(100),
  parent_id              BIGINT DEFAULT 0,
  tree_path              VARCHAR(500),
  sort                   INTEGER DEFAULT 0,
  status                 INTEGER DEFAULT 1,
  attrs                  JSONB DEFAULT '{}'::jsonb,
  custom_fields          JSONB DEFAULT '{}'::jsonb,
  execution_means        VARCHAR(64),
  param_slots_json       JSONB NOT NULL DEFAULT '[]'::jsonb,
  child_action_ids_json  JSONB NOT NULL DEFAULT '[]'::jsonb,
  is_composite           BOOLEAN NOT NULL DEFAULT FALSE,
  creator                VARCHAR(64),
  create_time            TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updater                VARCHAR(64),
  update_time            TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  deleted                BOOLEAN DEFAULT FALSE
);

COMMENT ON TABLE ent_action IS '动作库实体；entityTypeCode=action；标准动作定义（参数槽 / 复合子动作）';
COMMENT ON COLUMN ent_action.execution_means IS '执行手段（MANUAL/UAV/ROBOT/FIXED_CAMERA 等）';
COMMENT ON COLUMN ent_action.param_slots_json IS '参数槽 schema JSON 数组（字段编码列表）';
COMMENT ON COLUMN ent_action.child_action_ids_json IS '复合动作的有序子动作 code 列表 JSON 数组';
COMMENT ON COLUMN ent_action.is_composite IS '是否复合动作（true=开跑时按 child_action_ids_json 展开）';

CREATE INDEX IF NOT EXISTS idx_ent_action_tenant
  ON ent_action (tenant_id) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_ent_action_model
  ON ent_action (model_id) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_ent_action_execution_means
  ON ent_action (execution_means) WHERE deleted = FALSE;
CREATE UNIQUE INDEX IF NOT EXISTS uk_ent_action_code_tenant
  ON ent_action (code, tenant_id)
  WHERE deleted = FALSE AND code IS NOT NULL;

-- 按租户创建物理表 ent_action_t{tenant}（与 V73 步骤模板同模式）
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
    physical := 'ent_action_t' || tid::text;
    IF NOT EXISTS (
      SELECT 1 FROM information_schema.tables
      WHERE table_schema = 'dynamicbusiness' AND table_name = physical
    ) THEN
      EXECUTE format(
        'CREATE TABLE dynamicbusiness.%I (LIKE dynamicbusiness.ent_action INCLUDING DEFAULTS INCLUDING CONSTRAINTS INCLUDING INDEXES)',
        physical);
    ELSE
      EXECUTE format(
        'ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS execution_means VARCHAR(64)',
        physical);
      EXECUTE format(
        'ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS param_slots_json JSONB NOT NULL DEFAULT ''[]''::jsonb',
        physical);
      EXECUTE format(
        'ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS child_action_ids_json JSONB NOT NULL DEFAULT ''[]''::jsonb',
        physical);
      EXECUTE format(
        'ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS is_composite BOOLEAN NOT NULL DEFAULT FALSE',
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

  -- 无后缀基表仅作 DDL 模板，不承载租户活数据
  TRUNCATE TABLE dynamicbusiness.ent_action;
END $$;
