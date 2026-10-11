-- V146: 把 V145 的两张模型—模型表收成一张。
-- 同类型行的类型编码写到来源类型、目标类型。异类型行并入同一张表后删掉分表。
-- 已执行的 V145 不改。

SET search_path TO dynamicbusiness, public;

CREATE OR REPLACE FUNCTION dynamicbusiness._v146_table_exists(p_name text)
RETURNS boolean
LANGUAGE sql
STABLE
AS $$
  SELECT EXISTS (
    SELECT 1 FROM information_schema.tables
    WHERE table_schema = 'dynamicbusiness' AND table_name = lower(p_name)
  );
$$;

CREATE OR REPLACE FUNCTION dynamicbusiness._v146_has_column(p_table text, p_column text)
RETURNS boolean
LANGUAGE sql
STABLE
AS $$
  SELECT EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = 'dynamicbusiness'
      AND table_name = lower(p_table)
      AND column_name = lower(p_column)
  );
$$;

CREATE OR REPLACE FUNCTION dynamicbusiness._v146_merge_one(p_same text, p_cross text)
RETURNS void
LANGUAGE plpgsql
AS $$
DECLARE
  idx record;
  uk_name text;
  src_idx text;
  tgt_idx text;
BEGIN
  IF NOT dynamicbusiness._v146_table_exists(p_same) THEN
    RETURN;
  END IF;

  IF dynamicbusiness._v146_has_column(p_same, 'entity_type_code') THEN
    IF NOT dynamicbusiness._v146_has_column(p_same, 'source_entity_type_code') THEN
      EXECUTE format(
        'ALTER TABLE dynamicbusiness.%I ADD COLUMN source_entity_type_code VARCHAR(64)',
        p_same);
    END IF;
    IF NOT dynamicbusiness._v146_has_column(p_same, 'target_entity_type_code') THEN
      EXECUTE format(
        'ALTER TABLE dynamicbusiness.%I ADD COLUMN target_entity_type_code VARCHAR(64)',
        p_same);
    END IF;
    EXECUTE format(
      'UPDATE dynamicbusiness.%I
          SET source_entity_type_code = entity_type_code,
              target_entity_type_code = entity_type_code
        WHERE source_entity_type_code IS NULL
           OR target_entity_type_code IS NULL',
      p_same);
    EXECUTE format(
      'ALTER TABLE dynamicbusiness.%I
         ALTER COLUMN source_entity_type_code SET NOT NULL,
         ALTER COLUMN target_entity_type_code SET NOT NULL',
      p_same);

    FOR idx IN
      SELECT ic.relname AS index_name
      FROM pg_index i
      JOIN pg_class tc ON tc.oid = i.indrelid
      JOIN pg_class ic ON ic.oid = i.indexrelid
      JOIN pg_namespace n ON n.oid = tc.relnamespace
      WHERE n.nspname = 'dynamicbusiness'
        AND tc.relname = p_same
        AND NOT i.indisprimary
    LOOP
      EXECUTE format('DROP INDEX IF EXISTS dynamicbusiness.%I', idx.index_name);
    END LOOP;

    EXECUTE format(
      'ALTER TABLE dynamicbusiness.%I DROP COLUMN entity_type_code',
      p_same);
  END IF;

  uk_name := left('uk_mmr_' || p_same, 60);
  src_idx := left('idx_mmr_src_' || p_same, 60);
  tgt_idx := left('idx_mmr_tgt_' || p_same, 60);

  EXECUTE format(
    'CREATE UNIQUE INDEX IF NOT EXISTS %I
       ON dynamicbusiness.%I (
         tenant_id, source_entity_type_code, source_model_id,
         target_entity_type_code, target_model_id
       )',
    uk_name, p_same);
  EXECUTE format(
    'CREATE INDEX IF NOT EXISTS %I
       ON dynamicbusiness.%I (source_model_id, source_entity_type_code, target_entity_type_code)
       WHERE deleted = FALSE',
    src_idx, p_same);
  EXECUTE format(
    'CREATE INDEX IF NOT EXISTS %I
       ON dynamicbusiness.%I (target_model_id, target_entity_type_code, source_entity_type_code)
       WHERE deleted = FALSE',
    tgt_idx, p_same);

  IF dynamicbusiness._v146_table_exists(p_cross) THEN
    EXECUTE format(
      'INSERT INTO dynamicbusiness.%I (
         source_entity_type_code, source_model_id,
         target_entity_type_code, target_model_id,
         sort, creator, create_time, updater, update_time, deleted, tenant_id
       )
       SELECT source_entity_type_code, source_model_id,
              target_entity_type_code, target_model_id,
              sort, creator, create_time, updater, update_time, deleted, tenant_id
         FROM dynamicbusiness.%I
       ON CONFLICT (
         tenant_id, source_entity_type_code, source_model_id,
         target_entity_type_code, target_model_id
       ) DO NOTHING',
      p_same, p_cross);
    EXECUTE format('DROP TABLE dynamicbusiness.%I CASCADE', p_cross);
  END IF;
END;
$$;

SELECT dynamicbusiness._v146_merge_one(
  'dynamic_model_model_relation',
  'dynamic_model_model_cross_relation');

DO $$
DECLARE
  tid bigint;
BEGIN
  FOR tid IN
    SELECT DISTINCT tenant_id
    FROM dynamicbusiness.dynamic_entity_type
    WHERE deleted = false AND tenant_id IS NOT NULL AND tenant_id > 0
    ORDER BY tenant_id
  LOOP
    PERFORM dynamicbusiness._v146_merge_one(
      'dynamic_model_model_relation_t' || tid::text,
      'dynamic_model_model_cross_relation_t' || tid::text);
  END LOOP;
END;
$$;

COMMENT ON TABLE dynamicbusiness.dynamic_model_model_relation IS
    '模型—模型多对多。同类型、异类型都在这张表，用来源类型和目标类型区分。基表为租户物理表模板。';
COMMENT ON COLUMN dynamicbusiness.dynamic_model_model_relation.source_entity_type_code IS
    '来源模型所属存储类型';
COMMENT ON COLUMN dynamicbusiness.dynamic_model_model_relation.target_entity_type_code IS
    '目标模型所属存储类型';

DROP FUNCTION IF EXISTS dynamicbusiness._v146_merge_one(text, text);
DROP FUNCTION IF EXISTS dynamicbusiness._v146_has_column(text, text);
DROP FUNCTION IF EXISTS dynamicbusiness._v146_table_exists(text);
