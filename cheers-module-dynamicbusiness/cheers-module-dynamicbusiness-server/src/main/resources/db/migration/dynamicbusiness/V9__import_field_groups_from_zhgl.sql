-- Import field library groups from zhgl into unified dynamic_group (group_type = FIELD).
-- Maps: system_field_group -> dynamic_group
--       system_field_group_relation -> dynamic_group_relation (target_id = field_id)
-- Idempotent: skips when dynamic_group already has FIELD rows.

CREATE EXTENSION IF NOT EXISTS dblink;
SET search_path TO dynamicbusiness, public;

DO $$
DECLARE
  local_cnt bigint;
  remote_cnt bigint;
  conn text := 'host=${zhglHost} dbname=${zhglDb} user=${zhglUser} password=${zhglPassword}';
BEGIN
  SELECT COUNT(*) INTO local_cnt
  FROM dynamic_group
  WHERE group_type = 'FIELD' AND deleted = false;

  IF local_cnt > 0 THEN
    RAISE NOTICE 'skip V9 field groups: dynamic_group(FIELD) already has % rows', local_cnt;
    RETURN;
  END IF;

  BEGIN
    SELECT cnt INTO remote_cnt
    FROM dblink(conn, 'SELECT COUNT(*)::bigint FROM system_field_group WHERE deleted=false') AS t(cnt bigint);
  EXCEPTION WHEN OTHERS THEN
    RAISE NOTICE 'skip V9 field groups: zhgl unavailable (%)', SQLERRM;
    RETURN;
  END;

  IF remote_cnt = 0 THEN
    RAISE NOTICE 'skip V9 field groups: no rows in zhgl';
    RETURN;
  END IF;

  INSERT INTO dynamic_group (
    id, group_type, code, name, description, parent_id, path, level, sort, status,
    creator, create_time, updater, update_time, deleted, tenant_id
  )
  SELECT
    id, 'FIELD', code, name, description, parent_id, path, level, sort, status,
    creator, create_time, updater, update_time, deleted, tenant_id
  FROM dblink(
    conn,
    'SELECT id, code, name, description, parent_id, path, level, sort, status,
            creator, create_time, updater, update_time, deleted, tenant_id
     FROM system_field_group WHERE deleted = false'
  ) AS t(
    id bigint, code varchar, name varchar, description varchar, parent_id bigint,
    path varchar, level integer, sort integer, status integer,
    creator varchar, create_time timestamp, updater varchar, update_time timestamp,
    deleted boolean, tenant_id bigint
  );

  PERFORM setval(
    pg_get_serial_sequence('dynamic_group', 'id'),
    COALESCE((SELECT MAX(id) FROM dynamic_group), 1)
  );

  INSERT INTO dynamic_group_relation (
    id, group_type, group_id, target_id, sort,
    creator, create_time, updater, update_time, deleted, tenant_id
  )
  SELECT
    id, 'FIELD', field_group_id, field_id, sort,
    creator, create_time, updater, update_time, deleted, tenant_id
  FROM dblink(
    conn,
    'SELECT id, field_group_id, field_id, sort, creator, create_time, updater, update_time, deleted, tenant_id
     FROM system_field_group_relation WHERE deleted = false'
  ) AS t(
    id bigint, field_group_id bigint, field_id bigint, sort integer,
    creator varchar, create_time timestamp, updater varchar, update_time timestamp,
    deleted boolean, tenant_id bigint
  );

  PERFORM setval(
    pg_get_serial_sequence('dynamic_group_relation', 'id'),
    COALESCE((SELECT MAX(id) FROM dynamic_group_relation), 1)
  );

  RAISE NOTICE 'imported field groups: % groups, % relations',
    (SELECT COUNT(*) FROM dynamic_group WHERE group_type = 'FIELD'),
    (SELECT COUNT(*) FROM dynamic_group_relation WHERE group_type = 'FIELD');
END $$;
