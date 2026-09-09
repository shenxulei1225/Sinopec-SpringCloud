-- V104: SOP 标准包去模板术语（sop_template_id -> sop_id）
-- 说明：SOP 标准包主链不再区分“模板”概念，统一按 SOP 本体维护。

SET search_path TO dynamicbusiness, public;

-- ---------------------------------------------------------------------------
-- 1) dynamic_sop_scope_rule
-- ---------------------------------------------------------------------------
DO $$
BEGIN
  IF EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = 'dynamicbusiness'
      AND table_name = 'dynamic_sop_scope_rule'
      AND column_name = 'sop_template_id'
  ) THEN
    ALTER TABLE dynamic_sop_scope_rule RENAME COLUMN sop_template_id TO sop_id;
  END IF;
END $$;

DROP INDEX IF EXISTS uk_dynamic_sop_scope_rule_identity;
DROP INDEX IF EXISTS idx_dynamic_sop_scope_rule_template;

CREATE UNIQUE INDEX IF NOT EXISTS uk_dynamic_sop_scope_rule_identity
  ON dynamic_sop_scope_rule (tenant_id, sop_id, scope_type, target_id)
  WHERE deleted = FALSE;

CREATE INDEX IF NOT EXISTS idx_dynamic_sop_scope_rule_sop
  ON dynamic_sop_scope_rule (tenant_id, sop_id, sort_no, id)
  WHERE deleted = FALSE;

COMMENT ON COLUMN dynamic_sop_scope_rule.sop_id IS 'SOP 实体 id';

-- ---------------------------------------------------------------------------
-- 2) dynamic_sop_item_pack
-- ---------------------------------------------------------------------------
DO $$
BEGIN
  IF EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = 'dynamicbusiness'
      AND table_name = 'dynamic_sop_item_pack'
      AND column_name = 'sop_template_id'
  ) THEN
    ALTER TABLE dynamic_sop_item_pack RENAME COLUMN sop_template_id TO sop_id;
  END IF;
END $$;

DROP INDEX IF EXISTS uk_dynamic_sop_item_pack_identity;
DROP INDEX IF EXISTS idx_dynamic_sop_item_pack_template;

CREATE UNIQUE INDEX IF NOT EXISTS uk_dynamic_sop_item_pack_identity
  ON dynamic_sop_item_pack (tenant_id, sop_id, inspection_item_id)
  WHERE deleted = FALSE;

CREATE INDEX IF NOT EXISTS idx_dynamic_sop_item_pack_sop
  ON dynamic_sop_item_pack (tenant_id, sop_id, sort_no, id)
  WHERE deleted = FALSE;

COMMENT ON COLUMN dynamic_sop_item_pack.sop_id IS 'SOP 实体 id';
