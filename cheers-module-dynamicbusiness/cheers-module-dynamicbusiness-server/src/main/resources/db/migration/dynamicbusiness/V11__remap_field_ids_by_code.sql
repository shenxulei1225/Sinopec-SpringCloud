-- Remap field_id references from zhgl IDs to local dynamic_field IDs by stable field code (fallback: name).
-- Run after V7 model metadata import when serial IDs diverge between databases.
-- Idempotent: only remaps where zhgl_id <> local_id.

CREATE EXTENSION IF NOT EXISTS dblink;
SET search_path TO dynamicbusiness, public;

CREATE TABLE IF NOT EXISTS legacy_zhgl_field_id_map (
    zhgl_field_id  BIGINT       PRIMARY KEY,
    local_field_id BIGINT       NOT NULL,
    field_code     VARCHAR(64)  NOT NULL,
    field_name     VARCHAR(200),
    match_type     VARCHAR(16)  NOT NULL DEFAULT 'code',
    updated_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_legacy_zhgl_field_id_map_local
    ON legacy_zhgl_field_id_map (local_field_id);

-- Replace fieldId in field_groups_config JSON using legacy_zhgl_field_id_map
CREATE OR REPLACE FUNCTION remap_field_ids_in_config(p_config text)
RETURNS text
LANGUAGE plpgsql
AS $$
DECLARE
  cfg jsonb;
  new_groups jsonb := '[]'::jsonb;
  g jsonb;
  f jsonb;
  new_fields jsonb;
  old_id bigint;
  new_id bigint;
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
      IF NOT (f ? 'fieldId') THEN
        new_fields := new_fields || jsonb_build_array(f);
        CONTINUE;
      END IF;
      BEGIN
        old_id := (f->>'fieldId')::bigint;
      EXCEPTION WHEN OTHERS THEN
        CONTINUE;
      END;
      new_id := old_id;
      SELECT m.local_field_id INTO new_id
      FROM legacy_zhgl_field_id_map m
      WHERE m.zhgl_field_id = old_id;
      IF new_id IS NULL AND NOT EXISTS (SELECT 1 FROM dynamic_field df WHERE df.id = old_id AND df.deleted = false) THEN
        CONTINUE;
      END IF;
      IF new_id IS NULL THEN
        new_id := old_id;
      END IF;
      IF EXISTS (SELECT 1 FROM dynamic_field df WHERE df.id = new_id AND df.deleted = false) THEN
        new_fields := new_fields || jsonb_build_array(jsonb_set(f, '{fieldId}', to_jsonb(new_id)));
      END IF;
    END LOOP;
    new_groups := new_groups || jsonb_build_array(g || jsonb_build_object('fields', new_fields));
  END LOOP;

  RETURN jsonb_set(cfg, '{groups}', new_groups)::text;
END;
$$;

DO $$
DECLARE
  conn text := 'host=${zhglHost} dbname=${zhglDb} user=${zhglUser} password=${zhglPassword}';
  mapped_cnt bigint;
  remapped_mfa bigint;
BEGIN
  TRUNCATE legacy_zhgl_field_id_map;

  BEGIN
    INSERT INTO legacy_zhgl_field_id_map (zhgl_field_id, local_field_id, field_code, field_name, match_type)
    SELECT z.id, s.id, z.code, z.name, 'code'
    FROM dblink(
      conn,
      'SELECT id, code, name FROM system_field WHERE deleted = false'
    ) AS z(id bigint, code varchar, name varchar)
    JOIN dynamic_field s ON s.code = z.code AND s.deleted = false;
  EXCEPTION WHEN OTHERS THEN
    RAISE NOTICE 'skip V11 field id remap: zhgl unavailable (%)', SQLERRM;
    RETURN;
  END;

  GET DIAGNOSTICS mapped_cnt = ROW_COUNT;

  -- Fallback: match by name when code join missed (single local candidate only)
  INSERT INTO legacy_zhgl_field_id_map (zhgl_field_id, local_field_id, field_code, field_name, match_type)
  SELECT z.id, s.id, s.code, z.name, 'name'
  FROM dblink(
    conn,
    'SELECT id, code, name FROM system_field WHERE deleted = false'
  ) AS z(id bigint, code varchar, name varchar)
  JOIN dynamic_field s ON s.name = z.name AND s.deleted = false
  WHERE NOT EXISTS (SELECT 1 FROM legacy_zhgl_field_id_map m WHERE m.zhgl_field_id = z.id)
    AND NOT EXISTS (
      SELECT 1 FROM dynamic_field s2
      WHERE s2.name = z.name AND s2.deleted = false AND s2.id <> s.id
    );

  SELECT COUNT(*) INTO mapped_cnt FROM legacy_zhgl_field_id_map;
  RAISE NOTICE 'V11: built field id map for % zhgl fields', mapped_cnt;

  -- Warn when code matched but display name differs between zhgl and local
  RAISE NOTICE 'V11: code matched but name differs: % rows', (
    SELECT COUNT(*)
    FROM legacy_zhgl_field_id_map m
    JOIN dynamic_field f ON f.id = m.local_field_id AND f.deleted = false
    WHERE m.match_type = 'code'
      AND f.name IS DISTINCT FROM m.field_name
  );

  IF mapped_cnt = 0 THEN
    RETURN;
  END IF;

  -- Remove MFA rows that would duplicate (model_id, local_field_id) after remap
  DELETE FROM dynamic_model_field_assignment a
  USING legacy_zhgl_field_id_map m, dynamic_model_field_assignment b
  WHERE a.deleted = false
    AND a.field_id = m.zhgl_field_id
    AND m.zhgl_field_id <> m.local_field_id
    AND b.model_id = a.model_id
    AND b.field_id = m.local_field_id
    AND b.deleted = false
    AND b.id <> a.id;

  UPDATE dynamic_model_field_assignment a
  SET field_id    = m.local_field_id,
      updater     = 'migration',
      update_time = CURRENT_TIMESTAMP
  FROM legacy_zhgl_field_id_map m
  WHERE a.field_id = m.zhgl_field_id
    AND m.zhgl_field_id <> m.local_field_id
    AND a.deleted = false;

  GET DIAGNOSTICS remapped_mfa = ROW_COUNT;
  RAISE NOTICE 'V11: remapped % model field assignments', remapped_mfa;

  UPDATE dynamic_model m
  SET field_groups_config = remap_field_ids_in_config(m.field_groups_config),
      updater             = 'migration',
      update_time         = CURRENT_TIMESTAMP
  WHERE m.field_groups_config IS NOT NULL
    AND btrim(m.field_groups_config) <> ''
    AND m.deleted = false;

  UPDATE dynamic_group_relation r
  SET target_id   = m.local_field_id,
      updater     = 'migration',
      update_time = CURRENT_TIMESTAMP
  FROM legacy_zhgl_field_id_map m
  WHERE r.group_type = 'FIELD'
    AND r.target_id = m.zhgl_field_id
    AND m.zhgl_field_id <> m.local_field_id
    AND r.deleted = false;

  UPDATE dynamic_template_field_assignment a
  SET field_id    = m.local_field_id,
      updater     = 'migration',
      update_time = CURRENT_TIMESTAMP
  FROM legacy_zhgl_field_id_map m
  WHERE a.field_id = m.zhgl_field_id
    AND m.zhgl_field_id <> m.local_field_id
    AND a.deleted = false;

  UPDATE dynamic_entity_field_permission p
  SET field_id    = m.local_field_id,
      updater     = 'migration',
      update_time = CURRENT_TIMESTAMP
  FROM legacy_zhgl_field_id_map m
  WHERE p.field_id = m.zhgl_field_id
    AND m.zhgl_field_id <> m.local_field_id
    AND p.deleted = false;

  UPDATE dynamic_dynamic_table_column c
  SET field_id    = m.local_field_id,
      updater     = 'migration',
      update_time = CURRENT_TIMESTAMP
  FROM legacy_zhgl_field_id_map m
  WHERE c.field_id = m.zhgl_field_id
    AND m.zhgl_field_id <> m.local_field_id
    AND c.deleted = false;

  -- Drop assignments still pointing at unknown field ids
  DELETE FROM dynamic_model_field_assignment a
  WHERE a.deleted = false
    AND NOT EXISTS (
      SELECT 1 FROM dynamic_field f
      WHERE f.id = a.field_id AND f.deleted = false
    );

  RAISE NOTICE 'V11: unmapped zhgl fields (no local match): %', (
    SELECT COUNT(*)
    FROM dblink(conn, 'SELECT id FROM system_field WHERE deleted=false') AS z(id bigint)
    WHERE NOT EXISTS (SELECT 1 FROM legacy_zhgl_field_id_map m WHERE m.zhgl_field_id = z.id)
  );
EXCEPTION WHEN OTHERS THEN
  RAISE NOTICE 'V11: could not count unmapped zhgl fields (%)', SQLERRM;
END $$;

-- Re-sanitize configs and sync UI group hashes after field id remap
DO $$
BEGIN
  IF to_regprocedure('sanitize_field_groups_config(bigint,text)') IS NOT NULL THEN
    UPDATE dynamic_model m
    SET field_groups_config = sanitize_field_groups_config(m.id, m.field_groups_config),
        updater             = 'migration',
        update_time         = CURRENT_TIMESTAMP
    WHERE m.field_groups_config IS NOT NULL
      AND btrim(m.field_groups_config) <> ''
      AND m.deleted = false;
  END IF;
END $$;

DO $$
BEGIN
  IF to_regprocedure('java_string_hash(text)') IS NOT NULL THEN
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
  END IF;
END $$;
