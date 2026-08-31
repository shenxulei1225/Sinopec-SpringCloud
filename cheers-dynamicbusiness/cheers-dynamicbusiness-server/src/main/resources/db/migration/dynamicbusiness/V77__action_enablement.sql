-- V77: 动作启用表（型号/实体等 owner 启用哪些动作）
-- 设计：docs/superpowers/specs/2026-08-29-action-library-sop-task-tree-design.md
-- 权威：一行 = (租户, owner 种类, owner id, 动作 id)；不在读路径兜底全库

SET search_path TO dynamicbusiness, public;

CREATE TABLE IF NOT EXISTS dynamic_action_enablement (
  id           BIGSERIAL PRIMARY KEY,
  tenant_id    BIGINT       NOT NULL,
  owner_kind   VARCHAR(64)  NOT NULL,
  owner_id     BIGINT       NOT NULL,
  action_id    BIGINT       NOT NULL,
  creator      VARCHAR(64),
  create_time  TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
  updater      VARCHAR(64),
  update_time  TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
  deleted      BOOLEAN      DEFAULT FALSE
);

COMMENT ON TABLE dynamic_action_enablement IS
  '动作启用：owner（型号/实体等）启用的动作；owner_kind + owner_id 由调用方传入，本表不写死业务类型码';
COMMENT ON COLUMN dynamic_action_enablement.owner_kind IS '启用主体种类（如 model / entity；由 API 入参约定）';
COMMENT ON COLUMN dynamic_action_enablement.owner_id IS '启用主体 id（对应 owner_kind）';
COMMENT ON COLUMN dynamic_action_enablement.action_id IS '动作实体 id（ent_action*_t{tenant}.id）';

CREATE UNIQUE INDEX IF NOT EXISTS uk_dynamic_action_enablement_identity
  ON dynamic_action_enablement (tenant_id, owner_kind, owner_id, action_id)
  WHERE deleted = FALSE;

CREATE INDEX IF NOT EXISTS idx_dynamic_action_enablement_owner
  ON dynamic_action_enablement (tenant_id, owner_kind, owner_id)
  WHERE deleted = FALSE;

CREATE INDEX IF NOT EXISTS idx_dynamic_action_enablement_action
  ON dynamic_action_enablement (tenant_id, action_id)
  WHERE deleted = FALSE;
