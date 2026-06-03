-- Align dynamic_business_type_base_field with dynamic_field (field library).
-- Base fields are already imported (V2); this migration links them by field name / legacy refField.
-- Idempotent: only updates rows where libraryFieldCode / refField still differs.

SET search_path TO dynamicbusiness, public;

CREATE TABLE IF NOT EXISTS base_field_library_name_alias (
    base_field_name   VARCHAR(200) PRIMARY KEY,
    library_field_code VARCHAR(64) NOT NULL
);

INSERT INTO base_field_library_name_alias (base_field_name, library_field_code) VALUES
    ('关联设备管理', 'REF-MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id'),
    ('关联区域管理', 'F-cc746ce0224145af88d5428d0b03213a')
ON CONFLICT (base_field_name) DO UPDATE
    SET library_field_code = EXCLUDED.library_field_code;

CREATE OR REPLACE FUNCTION merge_base_field_library_config(
    p_config   text,
    p_lib_code varchar,
    p_is_ref   boolean
)
RETURNS text
LANGUAGE plpgsql
AS $$
DECLARE
  cfg jsonb;
BEGIN
  IF p_lib_code IS NULL OR btrim(p_lib_code) = '' THEN
    RETURN p_config;
  END IF;

  IF p_config IS NULL OR btrim(p_config) = '' THEN
    cfg := '{}'::jsonb;
  ELSE
    BEGIN
      cfg := p_config::jsonb;
    EXCEPTION WHEN OTHERS THEN
      cfg := '{}'::jsonb;
    END;
  END IF;

  cfg := cfg || jsonb_build_object('libraryFieldCode', p_lib_code);

  IF p_is_ref THEN
    cfg := cfg || jsonb_build_object('refField', p_lib_code);
  END IF;

  RETURN cfg::text;
END;
$$;

DO $$
DECLARE
  linked_by_name bigint;
  linked_by_alias bigint;
  linked_by_existing_ref bigint;
  unmapped bigint;
BEGIN
  -- 1) Unique field_name match against dynamic_field
  UPDATE dynamic_business_type_base_field bf
  SET type_config = merge_base_field_library_config(
        bf.type_config,
        df.code,
        bf.data_type IN ('REF_Multi', 'REFERENCE', 'ENTITY_REF', 'REF')
      ),
      updater     = 'migration',
      update_time = CURRENT_TIMESTAMP
  FROM dynamic_field df
  WHERE bf.deleted = false
    AND df.deleted = false
    AND bf.field_name = df.name
    AND (
      SELECT COUNT(*)
      FROM dynamic_field df2
      WHERE df2.deleted = false AND df2.name = bf.field_name
    ) = 1
    AND (
      bf.type_config IS NULL
      OR btrim(bf.type_config) = ''
      OR (bf.type_config::jsonb->>'libraryFieldCode') IS DISTINCT FROM df.code
      OR (
        bf.data_type IN ('REF_Multi', 'REFERENCE', 'ENTITY_REF', 'REF')
        AND (bf.type_config::jsonb->>'refField') IS DISTINCT FROM df.code
      )
    );

  GET DIAGNOSTICS linked_by_name = ROW_COUNT;
  RAISE NOTICE 'V13: linked base fields by unique field_name: %', linked_by_name;

  -- 2) Known display-name aliases (e.g. 关联设备管理 -> 所属设备管理 library code)
  UPDATE dynamic_business_type_base_field bf
  SET type_config = merge_base_field_library_config(
        bf.type_config,
        alias.library_field_code,
        bf.data_type IN ('REF_Multi', 'REFERENCE', 'ENTITY_REF', 'REF')
      ),
      updater     = 'migration',
      update_time = CURRENT_TIMESTAMP
  FROM base_field_library_name_alias alias
  WHERE bf.deleted = false
    AND bf.field_name = alias.base_field_name
    AND EXISTS (
      SELECT 1 FROM dynamic_field df
      WHERE df.deleted = false AND df.code = alias.library_field_code
    )
    AND (
      bf.type_config IS NULL
      OR btrim(bf.type_config) = ''
      OR (bf.type_config::jsonb->>'libraryFieldCode') IS DISTINCT FROM alias.library_field_code
      OR (
        bf.data_type IN ('REF_Multi', 'REFERENCE', 'ENTITY_REF', 'REF')
        AND (bf.type_config::jsonb->>'refField') IS DISTINCT FROM alias.library_field_code
      )
    );

  GET DIAGNOSTICS linked_by_alias = ROW_COUNT;
  RAISE NOTICE 'V13: linked base fields by name alias: %', linked_by_alias;

  -- 3) refField already points at a valid library code — ensure libraryFieldCode is set too
  UPDATE dynamic_business_type_base_field bf
  SET type_config = merge_base_field_library_config(
        bf.type_config,
        bf.type_config::jsonb->>'refField',
        true
      ),
      updater     = 'migration',
      update_time = CURRENT_TIMESTAMP
  WHERE bf.deleted = false
    AND bf.type_config IS NOT NULL
    AND btrim(bf.type_config) <> ''
    AND bf.type_config::jsonb ? 'refField'
    AND EXISTS (
      SELECT 1 FROM dynamic_field df
      WHERE df.deleted = false
        AND df.code = bf.type_config::jsonb->>'refField'
    )
    AND (bf.type_config::jsonb->>'libraryFieldCode') IS DISTINCT FROM (bf.type_config::jsonb->>'refField');

  GET DIAGNOSTICS linked_by_existing_ref = ROW_COUNT;
  RAISE NOTICE 'V13: normalized libraryFieldCode from existing refField: %', linked_by_existing_ref;

  SELECT COUNT(*) INTO unmapped
  FROM dynamic_business_type_base_field bf
  WHERE bf.deleted = false
    AND bf.data_type IN ('REF_Multi', 'REFERENCE', 'ENTITY_REF', 'REF')
    AND (
      bf.type_config IS NULL
      OR btrim(bf.type_config) = ''
      OR (bf.type_config::jsonb->>'libraryFieldCode') IS NULL
      OR NOT EXISTS (
        SELECT 1 FROM dynamic_field df
        WHERE df.deleted = false
          AND df.code = bf.type_config::jsonb->>'libraryFieldCode'
      )
    );

  RAISE NOTICE 'V13: REF-type base fields still without valid libraryFieldCode: %', unmapped;
END $$;
