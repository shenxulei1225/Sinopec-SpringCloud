-- Reconcile model field metadata after zhgl import:
-- 1) Java hashCode for model UI group ids -> dynamic_model_field_assignment.field_group_id
-- 2) Remove orphan fieldId entries from field_groups_config JSON
-- 3) Drop MFA rows whose field_id no longer exists
-- 4) Report MFA assigned fields missing from field_groups_config (NOTICE only)

SET search_path TO dynamicbusiness, public;

-- Java String.hashCode() compatible with frontend group id hashing
CREATE OR REPLACE FUNCTION java_string_hash(input text)
RETURNS bigint
LANGUAGE plpgsql
IMMUTABLE
AS $$
DECLARE
  h bigint := 0;
  i int;
  c int;
  h32 bigint;
BEGIN
  IF input IS NULL OR input = '' THEN
    RETURN NULL;
  END IF;
  FOR i IN 1..length(input) LOOP
    c := ascii(substr(input, i, 1));
    h := 31 * h + c;
    -- fold to signed 32-bit like Java int
    h32 := h & 4294967295;
    IF h32 >= 2147483648 THEN
      h := h32 - 4294967296;
    ELSE
      h := h32;
    END IF;
  END LOOP;
  RETURN h;
END;
$$;

-- Remove fieldIds from field_groups_config when field or model assignment missing
CREATE OR REPLACE FUNCTION sanitize_field_groups_config(p_model_id bigint, p_config text)
RETURNS text
LANGUAGE plpgsql
AS $$
DECLARE
  cfg jsonb;
  new_groups jsonb := '[]'::jsonb;
  g jsonb;
  f jsonb;
  new_fields jsonb;
  fid bigint;
BEGIN
  IF p_config IS NULL OR btrim(p_config) = '' THEN
    RETURN p_config;
  END IF;
  BEGIN
    cfg := p_config::jsonb;
  EXCEPTION WHEN OTHERS THEN
    RETURN p_config;
  END;
  IF cfg->'groups' IS NULL THEN
    RETURN p_config;
  END IF;

  FOR g IN SELECT * FROM jsonb_array_elements(cfg->'groups') LOOP
    new_fields := '[]'::jsonb;
    FOR f IN SELECT * FROM jsonb_array_elements(COALESCE(g->'fields', '[]'::jsonb)) LOOP
      BEGIN
        fid := (f->>'fieldId')::bigint;
      EXCEPTION WHEN OTHERS THEN
        CONTINUE;
      END;
      IF NOT EXISTS (
        SELECT 1 FROM dynamic_field df
        WHERE df.id = fid AND df.deleted = false
      ) THEN
        CONTINUE;
      END IF;
      IF NOT EXISTS (
        SELECT 1 FROM dynamic_model_field_assignment mfa
        WHERE mfa.model_id = p_model_id AND mfa.field_id = fid AND mfa.deleted = false
      ) THEN
        CONTINUE;
      END IF;
      new_fields := new_fields || jsonb_build_array(f);
    END LOOP;
    new_groups := new_groups || jsonb_build_array(
      g || jsonb_build_object('fields', new_fields)
    );
  END LOOP;

  RETURN jsonb_set(cfg, '{groups}', new_groups)::text;
END;
$$;

-- 1) Sync MFA.field_group_id from field_groups_config group id hash
UPDATE dynamic_model_field_assignment mfa
SET field_group_id = mapped.group_hash,
    updater        = 'migration',
    update_time    = CURRENT_TIMESTAMP
FROM (
  SELECT
    m.id AS model_id,
    (fld->>'fieldId')::bigint AS field_id,
    java_string_hash(grp->>'id') AS group_hash
  FROM dynamic_model m
  CROSS JOIN LATERAL jsonb_array_elements((m.field_groups_config::jsonb)->'groups') grp
  CROSS JOIN LATERAL jsonb_array_elements(COALESCE(grp->'fields', '[]'::jsonb)) fld
  WHERE m.field_groups_config IS NOT NULL
    AND btrim(m.field_groups_config) <> ''
    AND fld ? 'fieldId'
) mapped
WHERE mfa.model_id = mapped.model_id
  AND mfa.field_id = mapped.field_id
  AND mfa.deleted = false
  AND mapped.group_hash IS NOT NULL;

-- Legacy rows may store abs(hash) for negative Java hashCode
UPDATE dynamic_model_field_assignment mfa
SET field_group_id = mapped.group_hash,
    updater        = 'migration',
    update_time    = CURRENT_TIMESTAMP
FROM (
  SELECT
    m.id AS model_id,
    (fld->>'fieldId')::bigint AS field_id,
    abs(java_string_hash(grp->>'id')) AS group_hash
  FROM dynamic_model m
  CROSS JOIN LATERAL jsonb_array_elements((m.field_groups_config::jsonb)->'groups') grp
  CROSS JOIN LATERAL jsonb_array_elements(COALESCE(grp->'fields', '[]'::jsonb)) fld
  WHERE m.field_groups_config IS NOT NULL
    AND btrim(m.field_groups_config) <> ''
    AND fld ? 'fieldId'
) mapped
WHERE mfa.model_id = mapped.model_id
  AND mfa.field_id = mapped.field_id
  AND mfa.deleted = false
  AND mfa.field_group_id IS NULL
  AND mapped.group_hash IS NOT NULL;

-- 2) Sanitize field_groups_config JSON
UPDATE dynamic_model m
SET field_groups_config = sanitize_field_groups_config(m.id, m.field_groups_config),
    updater             = 'migration',
    update_time         = CURRENT_TIMESTAMP
WHERE m.field_groups_config IS NOT NULL
  AND btrim(m.field_groups_config) <> ''
  AND m.deleted = false;

-- 3) Remove orphan model-field assignments (field row missing)
DELETE FROM dynamic_model_field_assignment mfa
WHERE mfa.deleted = false
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_field f
    WHERE f.id = mfa.field_id AND f.deleted = false
  );

-- 4) Diagnostics: assigned fields not listed in any model group
DO $$
DECLARE
  orphan_cnt bigint;
BEGIN
  SELECT COUNT(*) INTO orphan_cnt
  FROM dynamic_model_field_assignment mfa
  JOIN dynamic_model m ON m.id = mfa.model_id AND m.deleted = false
  WHERE mfa.deleted = false
    AND m.field_groups_config IS NOT NULL
    AND btrim(m.field_groups_config) <> ''
    AND NOT EXISTS (
      SELECT 1
      FROM jsonb_array_elements((m.field_groups_config::jsonb)->'groups') grp
      CROSS JOIN LATERAL jsonb_array_elements(COALESCE(grp->'fields', '[]'::jsonb)) fld
      WHERE (fld->>'fieldId')::bigint = mfa.field_id
    );

  IF orphan_cnt > 0 THEN
    RAISE NOTICE 'V10: % model field assignments not referenced in field_groups_config (may need UI regroup)', orphan_cnt;
  END IF;
END $$;
