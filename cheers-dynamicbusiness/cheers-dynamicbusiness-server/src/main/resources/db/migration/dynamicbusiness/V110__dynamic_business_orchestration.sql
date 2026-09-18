-- V110: 业务编排（5W 导图）权威存储
-- 与门户 business / 目录编排 dm_catalog_orchestration 分离：本表只存「业务引用哪些功能点、怎么串」。
-- 权威：docs/动态业务/编排驱动业务/编排驱动业务权威说明.md §2

SET search_path TO dynamicbusiness, public;

CREATE TABLE IF NOT EXISTS dynamic_business_orchestration (
  id                   BIGSERIAL PRIMARY KEY,
  tenant_id            BIGINT        NOT NULL,
  business_code        VARCHAR(128)  NOT NULL,
  business_name        VARCHAR(128),
  task_domain          VARCHAR(64),
  orchestration_json   JSONB         NOT NULL,
  canvas_json          JSONB,
  creator              VARCHAR(64),
  create_time          TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
  updater              VARCHAR(64),
  update_time          TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
  deleted              BOOLEAN       DEFAULT FALSE
);

COMMENT ON TABLE dynamic_business_orchestration IS '业务编排（5W）：tenant+business_code 唯一；orchestration_json 为权威结构，canvas_json 为画布坐标（展示层）';
COMMENT ON COLUMN dynamic_business_orchestration.business_code IS '业务编码，如 business.patrol';
COMMENT ON COLUMN dynamic_business_orchestration.task_domain IS '任务分池域，如 巡检';
COMMENT ON COLUMN dynamic_business_orchestration.orchestration_json IS 'BusinessOrchestration 权威 JSON（nodes/entry/next/child）';
COMMENT ON COLUMN dynamic_business_orchestration.canvas_json IS '画布节点坐标等展示态；禁止当作业务权威';

CREATE UNIQUE INDEX IF NOT EXISTS uk_dynamic_business_orchestration_tid_code
  ON dynamic_business_orchestration (tenant_id, business_code)
  WHERE deleted = FALSE;

CREATE INDEX IF NOT EXISTS idx_dynamic_business_orchestration_domain
  ON dynamic_business_orchestration (tenant_id, task_domain)
  WHERE deleted = FALSE;
