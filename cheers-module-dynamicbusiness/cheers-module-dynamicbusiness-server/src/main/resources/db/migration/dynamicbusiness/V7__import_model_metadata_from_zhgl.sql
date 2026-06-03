-- Import model/category metadata from zhgl (field, model, assignments, category).
-- Idempotent: skips each table when local row count >= zhgl.
-- Preserves IDs from zhgl so entity.model_id stays valid.
-- Run V8 afterward to normalize legacy business_type_code values.

CREATE EXTENSION IF NOT EXISTS dblink;
SET search_path TO dynamicbusiness, public;

-- Truncate dependent metadata once if model table needs full refresh
DO $$
DECLARE
  local_models bigint;
  remote_models bigint;
  conn text := 'host=${zhglHost} dbname=${zhglDb} user=${zhglUser} password=${zhglPassword}';
BEGIN
  SELECT COUNT(*) INTO local_models FROM dynamic_model;
  BEGIN
    SELECT cnt INTO remote_models
    FROM dblink(conn, 'SELECT COUNT(*)::bigint FROM system_model WHERE deleted=false') AS t(cnt bigint);
  EXCEPTION WHEN OTHERS THEN
    RAISE NOTICE 'V7: zhgl unavailable, skip metadata import';
    RETURN;
  END;
  IF local_models >= remote_models AND local_models > 0 THEN
    RAISE NOTICE 'V7: models already synced (local=%, remote=%), per-table skip applies', local_models, remote_models;
    RETURN;
  END IF;
  TRUNCATE TABLE
    dynamic_model_field_assignment,
    dynamic_model_category_relation,
    dynamic_model_relation,
    dynamic_model_relation_declaration,
    dynamic_model,
    dynamic_category,
    dynamic_field
  RESTART IDENTITY CASCADE;
  RAISE NOTICE 'V7: truncated metadata tables for full refresh';
END $$;


-- dynamic_field
DO $$
DECLARE
  local_cnt bigint;
  remote_cnt bigint;
  conn text := 'host=${zhglHost} dbname=${zhglDb} user=${zhglUser} password=${zhglPassword}';
BEGIN
  EXECUTE format('SELECT COUNT(*) FROM dynamicbusiness.%I', 'dynamic_field') INTO local_cnt;

  BEGIN
    SELECT cnt INTO remote_cnt
    FROM dblink(conn, 'SELECT COUNT(*)::bigint AS cnt FROM system_field WHERE deleted = false') AS t(cnt bigint);
  EXCEPTION WHEN OTHERS THEN
    RAISE NOTICE 'skip dynamic_field: zhgl unavailable (%)', SQLERRM;
    RETURN;
  END;

  IF local_cnt > 0 THEN
    RAISE NOTICE 'skip dynamic_field: local already has % rows (field library pre-imported; use V11 to remap field_id by code/name)', local_cnt;
    RETURN;
  END IF;

  IF remote_cnt = 0 THEN
    RAISE NOTICE 'skip dynamic_field: no rows in zhgl';
    RETURN;
  END IF;

  EXECUTE format('TRUNCATE dynamicbusiness.%I RESTART IDENTITY CASCADE', 'dynamic_field');

  INSERT INTO dynamicbusiness.dynamic_field (id, code, name, type, unit, description, source, status, max_relations, index_strategy, options, provider_code, semantic_type, creator, create_time, updater, update_time, deleted, tenant_id)
  SELECT id, code, name, type, unit, description, source, status, max_relations, index_strategy, options, provider_code, semantic_type, creator, create_time, updater, update_time, deleted, tenant_id
  FROM dblink(conn, $dblink$SELECT id, code, name, type, unit, description, source, status, max_relations, index_strategy, options, provider_code, semantic_type, creator, create_time, updater, update_time, deleted, tenant_id
           FROM system_field WHERE deleted = false$dblink$) AS t(id bigint, code varchar, name varchar, type varchar, unit varchar, description varchar, source varchar, status integer, max_relations integer, index_strategy varchar, options varchar, provider_code varchar, semantic_type varchar, creator varchar, create_time timestamp, updater varchar, update_time timestamp, deleted boolean, tenant_id bigint);

  PERFORM setval(
    pg_get_serial_sequence('dynamicbusiness.dynamic_field', 'id'),
    COALESCE((SELECT MAX(id) FROM dynamicbusiness.dynamic_field), 1)
  );

  RAISE NOTICE 'imported dynamic_field: % rows', remote_cnt;
END $$;


-- dynamic_model
DO $$
DECLARE
  local_cnt bigint;
  remote_cnt bigint;
  conn text := 'host=${zhglHost} dbname=${zhglDb} user=${zhglUser} password=${zhglPassword}';
BEGIN
  EXECUTE format('SELECT COUNT(*) FROM dynamicbusiness.%I', 'dynamic_model') INTO local_cnt;

  BEGIN
    SELECT cnt INTO remote_cnt
    FROM dblink(conn, 'SELECT COUNT(*)::bigint AS cnt FROM system_model WHERE deleted = false') AS t(cnt bigint);
  EXCEPTION WHEN OTHERS THEN
    RAISE NOTICE 'skip dynamic_model: zhgl unavailable (%)', SQLERRM;
    RETURN;
  END;

  IF remote_cnt = 0 THEN
    RAISE NOTICE 'skip dynamic_model: no rows in zhgl';
    RETURN;
  END IF;

  IF local_cnt >= remote_cnt AND local_cnt > 0 THEN
    RAISE NOTICE 'skip dynamic_model: local % >= remote % (already imported)', local_cnt, remote_cnt;
    RETURN;
  END IF;

  EXECUTE format('TRUNCATE dynamicbusiness.%I RESTART IDENTITY CASCADE', 'dynamic_model');

  INSERT INTO dynamicbusiness.dynamic_model (id, code, name, business_type_code, description, status, sort, field_groups_config, creator, create_time, updater, update_time, deleted, tenant_id)
  SELECT id, code, name, business_type_code, description, status, sort, field_groups_config, creator, create_time, updater, update_time, deleted, tenant_id
  FROM dblink(conn, $dblink$SELECT id, code, name, business_type_code, description, status, sort, field_groups_config, creator, create_time, updater, update_time, deleted, tenant_id
           FROM system_model WHERE deleted = false$dblink$) AS t(id bigint, code varchar, name varchar, business_type_code varchar, description varchar, status integer, sort integer, field_groups_config text, creator varchar, create_time timestamp, updater varchar, update_time timestamp, deleted boolean, tenant_id bigint);

  PERFORM setval(
    pg_get_serial_sequence('dynamicbusiness.dynamic_model', 'id'),
    COALESCE((SELECT MAX(id) FROM dynamicbusiness.dynamic_model), 1)
  );

  RAISE NOTICE 'imported dynamic_model: % rows', remote_cnt;
END $$;


-- dynamic_model_field_assignment
DO $$
DECLARE
  local_cnt bigint;
  remote_cnt bigint;
  conn text := 'host=${zhglHost} dbname=${zhglDb} user=${zhglUser} password=${zhglPassword}';
BEGIN
  EXECUTE format('SELECT COUNT(*) FROM dynamicbusiness.%I', 'dynamic_model_field_assignment') INTO local_cnt;

  BEGIN
    SELECT cnt INTO remote_cnt
    FROM dblink(conn, 'SELECT COUNT(*)::bigint AS cnt FROM system_model_field_assignment WHERE deleted = false') AS t(cnt bigint);
  EXCEPTION WHEN OTHERS THEN
    RAISE NOTICE 'skip dynamic_model_field_assignment: zhgl unavailable (%)', SQLERRM;
    RETURN;
  END;

  IF remote_cnt = 0 THEN
    RAISE NOTICE 'skip dynamic_model_field_assignment: no rows in zhgl';
    RETURN;
  END IF;

  IF local_cnt >= remote_cnt AND local_cnt > 0 THEN
    RAISE NOTICE 'skip dynamic_model_field_assignment: local % >= remote % (already imported)', local_cnt, remote_cnt;
    RETURN;
  END IF;

  EXECUTE format('TRUNCATE dynamicbusiness.%I RESTART IDENTITY CASCADE', 'dynamic_model_field_assignment');

  INSERT INTO dynamicbusiness.dynamic_model_field_assignment (id, model_id, field_id, required, is_searchable, is_filterable, is_sortable, default_value, validation_rules, sort, field_group_id, field_source, ref_library_id, model_relation_id, target_business_type, creator, create_time, updater, update_time, deleted, tenant_id)
  SELECT id, model_id, field_id, required, is_searchable, is_filterable, is_sortable, default_value, validation_rules, sort, field_group_id, field_source, ref_library_id, model_relation_id, target_business_type, creator, create_time, updater, update_time, deleted, tenant_id
  FROM dblink(conn, $dblink$SELECT id, model_id, field_id, required, is_searchable, is_filterable, is_sortable, default_value, validation_rules, sort, field_group_id, field_source, ref_library_id, model_relation_id, target_business_type, creator, create_time, updater, update_time, deleted, tenant_id
           FROM system_model_field_assignment WHERE deleted = false$dblink$) AS t(id bigint, model_id bigint, field_id bigint, required boolean, is_searchable boolean, is_filterable boolean, is_sortable boolean, default_value varchar, validation_rules text, sort integer, field_group_id bigint, field_source varchar, ref_library_id bigint, model_relation_id bigint, target_business_type varchar, creator varchar, create_time timestamp, updater varchar, update_time timestamp, deleted boolean, tenant_id bigint);

  PERFORM setval(
    pg_get_serial_sequence('dynamicbusiness.dynamic_model_field_assignment', 'id'),
    COALESCE((SELECT MAX(id) FROM dynamicbusiness.dynamic_model_field_assignment), 1)
  );

  RAISE NOTICE 'imported dynamic_model_field_assignment: % rows', remote_cnt;
END $$;


-- dynamic_category
DO $$
DECLARE
  local_cnt bigint;
  remote_cnt bigint;
  conn text := 'host=${zhglHost} dbname=${zhglDb} user=${zhglUser} password=${zhglPassword}';
BEGIN
  EXECUTE format('SELECT COUNT(*) FROM dynamicbusiness.%I', 'dynamic_category') INTO local_cnt;

  BEGIN
    SELECT cnt INTO remote_cnt
    FROM dblink(conn, 'SELECT COUNT(*)::bigint AS cnt FROM system_category WHERE deleted = false') AS t(cnt bigint);
  EXCEPTION WHEN OTHERS THEN
    RAISE NOTICE 'skip dynamic_category: zhgl unavailable (%)', SQLERRM;
    RETURN;
  END;

  IF remote_cnt = 0 THEN
    RAISE NOTICE 'skip dynamic_category: no rows in zhgl';
    RETURN;
  END IF;

  IF local_cnt >= remote_cnt AND local_cnt > 0 THEN
    RAISE NOTICE 'skip dynamic_category: local % >= remote % (already imported)', local_cnt, remote_cnt;
    RETURN;
  END IF;

  EXECUTE format('TRUNCATE dynamicbusiness.%I RESTART IDENTITY CASCADE', 'dynamic_category');

  INSERT INTO dynamicbusiness.dynamic_category (id, parent_id, name, code, category_type_code, tree_path, level, sort, status, description, creator, create_time, updater, update_time, deleted, tenant_id)
  SELECT id, parent_id, name, code, category_type_code, tree_path, level, sort, status, description, creator, create_time, updater, update_time, deleted, tenant_id
  FROM dblink(conn, $dblink$SELECT id, parent_id, name, code, COALESCE(NULLIF(TRIM(category_type_code), ''), 'default') AS category_type_code, tree_path, level, sort, status, description, creator, create_time, updater, update_time, deleted, tenant_id
           FROM system_category WHERE deleted = false$dblink$) AS t(id bigint, parent_id bigint, name varchar, code varchar, category_type_code varchar, tree_path varchar, level integer, sort integer, status integer, description text, creator varchar, create_time timestamp, updater varchar, update_time timestamp, deleted boolean, tenant_id bigint);

  PERFORM setval(
    pg_get_serial_sequence('dynamicbusiness.dynamic_category', 'id'),
    COALESCE((SELECT MAX(id) FROM dynamicbusiness.dynamic_category), 1)
  );

  RAISE NOTICE 'imported dynamic_category: % rows', remote_cnt;
END $$;


-- dynamic_model_category_relation
DO $$
DECLARE
  local_cnt bigint;
  remote_cnt bigint;
  conn text := 'host=${zhglHost} dbname=${zhglDb} user=${zhglUser} password=${zhglPassword}';
BEGIN
  EXECUTE format('SELECT COUNT(*) FROM dynamicbusiness.%I', 'dynamic_model_category_relation') INTO local_cnt;

  BEGIN
    SELECT cnt INTO remote_cnt
    FROM dblink(conn, 'SELECT COUNT(*)::bigint AS cnt FROM system_model_category_relation WHERE deleted = false') AS t(cnt bigint);
  EXCEPTION WHEN OTHERS THEN
    RAISE NOTICE 'skip dynamic_model_category_relation: zhgl unavailable (%)', SQLERRM;
    RETURN;
  END;

  IF remote_cnt = 0 THEN
    RAISE NOTICE 'skip dynamic_model_category_relation: no rows in zhgl';
    RETURN;
  END IF;

  IF local_cnt >= remote_cnt AND local_cnt > 0 THEN
    RAISE NOTICE 'skip dynamic_model_category_relation: local % >= remote % (already imported)', local_cnt, remote_cnt;
    RETURN;
  END IF;

  EXECUTE format('TRUNCATE dynamicbusiness.%I RESTART IDENTITY CASCADE', 'dynamic_model_category_relation');

  INSERT INTO dynamicbusiness.dynamic_model_category_relation (id, model_id, category_id, business_type_code, sort, creator, create_time, updater, update_time, deleted, tenant_id)
  SELECT id, model_id, category_id, business_type_code, sort, creator, create_time, updater, update_time, deleted, tenant_id
  FROM dblink(conn, $dblink$SELECT id, model_id, category_id, business_type_code, sort, creator, create_time, updater, update_time, deleted, tenant_id
           FROM system_model_category_relation WHERE deleted = false$dblink$) AS t(id bigint, model_id bigint, category_id bigint, business_type_code varchar, sort integer, creator varchar, create_time timestamp, updater varchar, update_time timestamp, deleted boolean, tenant_id bigint);

  PERFORM setval(
    pg_get_serial_sequence('dynamicbusiness.dynamic_model_category_relation', 'id'),
    COALESCE((SELECT MAX(id) FROM dynamicbusiness.dynamic_model_category_relation), 1)
  );

  RAISE NOTICE 'imported dynamic_model_category_relation: % rows', remote_cnt;
END $$;

