#!/usr/bin/env python3
"""Generate Flyway V12 migration: import entity data from zhgl via dblink."""
from __future__ import annotations

import subprocess
from pathlib import Path

PG_HOST = "127.0.0.1"
PG_USER = "postgres"
PG_PASSWORD = "Coolhomer"
SOURCE_DB = "zhgl"

BIZ_TABLES = [
    "biz_equipment",
    "biz_region",
    "biz_task",
    "biz_inspection_item",
    "biz_emergency_resource",
    "biz_customer",
    "biz_inspection_point",
    "biz_emergency_team",
    "biz_route",
    "biz_billing",
    "biz_spare_part",
    "biz_pipeline",
    "biz_personnel",
    "biz_maintenance",
    "biz_patrol",
    "biz_fault",
    "biz_emergency",
]

TYPE_MAP = {
    "bigint": "bigint",
    "integer": "integer",
    "smallint": "smallint",
    "boolean": "boolean",
    "date": "date",
    "text": "text",
    "jsonb": "jsonb",
    "numeric": "numeric",
    "double precision": "double precision",
    "character varying": "varchar",
    "timestamp without time zone": "timestamp",
    "timestamp with time zone": "timestamptz",
}


def psql(db: str, sql: str) -> str:
    import os

    env = os.environ.copy()
    env["PGPASSWORD"] = PG_PASSWORD
    r = subprocess.run(
        ["psql", "-h", PG_HOST, "-U", PG_USER, "-d", db, "-tAc", sql],
        capture_output=True,
        text=True,
        env=env,
        check=True,
    )
    return r.stdout.strip()


def table_columns(db: str, table: str) -> list[tuple[str, str]]:
    rows = psql(
        db,
        f"""
        SELECT column_name, data_type
        FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = '{table}'
        ORDER BY ordinal_position;
        """,
    )
    cols: list[tuple[str, str]] = []
    for line in rows.splitlines():
        if not line.strip():
            continue
        name, dtype = line.split("|", 1)
        cols.append((name.strip(), TYPE_MAP.get(dtype.strip(), dtype.strip())))
    return cols


def import_block(table: str, cols: list[tuple[str, str]], source_table: str | None = None) -> str:
    source_table = source_table or table
    col_names = [c[0] for c in cols]
    col_list = ", ".join(col_names)
    dblink_cols = ", ".join(f"{c[0]} {c[1]}" for c in cols)
    select_cols = ", ".join(col_names)
    return f"""
-- {table}
DO $$
DECLARE
  local_cnt bigint;
  remote_cnt bigint;
  conn text := 'host=${{zhglHost}} dbname=${{zhglDb}} user=${{zhglUser}} password=${{zhglPassword}}';
BEGIN
  IF to_regclass('dynamicbusiness.{table}') IS NULL THEN
    RAISE NOTICE 'skip {table}: table not found';
    RETURN;
  END IF;

  SELECT COUNT(*) INTO local_cnt FROM dynamicbusiness.{table};
  IF local_cnt > 0 THEN
    RAISE NOTICE 'skip {table}: already has % rows', local_cnt;
    RETURN;
  END IF;

  BEGIN
    SELECT cnt INTO remote_cnt
    FROM dblink(conn, 'SELECT COUNT(*)::bigint AS cnt FROM {source_table}') AS t(cnt bigint);
  EXCEPTION WHEN OTHERS THEN
    RAISE NOTICE 'skip {table}: zhgl unavailable (%)', SQLERRM;
    RETURN;
  END;

  IF remote_cnt = 0 THEN
    RAISE NOTICE 'skip {table}: no rows in zhgl';
    RETURN;
  END IF;

  INSERT INTO dynamicbusiness.{table} ({col_list})
  OVERRIDING SYSTEM VALUE
  SELECT {select_cols}
  FROM dblink(
    conn,
    'SELECT {col_list} FROM {source_table}'
  ) AS t({dblink_cols});

  PERFORM setval(
    pg_get_serial_sequence('dynamicbusiness.{table}', 'id'),
    COALESCE((SELECT MAX(id) FROM dynamicbusiness.{table}), 1)
  );

  RAISE NOTICE 'imported {table}: % rows', remote_cnt;
END $$;
"""


def main() -> None:
    out_dir = Path(__file__).resolve().parent.parent / (
        "cheers-module-dynamicbusiness-server/src/main/resources/db/migration/dynamicbusiness"
    )
    out_file = out_dir / "V12__import_entity_data_from_zhgl.sql"

    parts = [
        "-- Import entity instance data from zhgl (local dev).",
        "-- Requires: CREATE EXTENSION dblink; zhgl DB on same host with biz_* tables.",
        "-- Flyway placeholders: zhglHost, zhglDb, zhglUser, zhglPassword (see application-local.yaml).",
        "-- Idempotent: skips each table if target already has rows.",
        "",
        "CREATE EXTENSION IF NOT EXISTS dblink;",
        "SET search_path TO dynamicbusiness, public;",
        "",
    ]

    for table in BIZ_TABLES:
        cols = table_columns(SOURCE_DB, table)
        if not cols:
            print(f"warn: no columns for {table}")
            continue
        parts.append(import_block(table, cols))

    parts.extend(
        [
            "",
            "-- field index",
            """
DO $$
DECLARE
  local_cnt bigint;
  conn text := 'host=${zhglHost} dbname=${zhglDb} user=${zhglUser} password=${zhglPassword}';
BEGIN
  SELECT COUNT(*) INTO local_cnt FROM dynamic_entity_field_index;
  IF local_cnt > 0 THEN
    RAISE NOTICE 'skip dynamic_entity_field_index: already has % rows', local_cnt;
    RETURN;
  END IF;

  BEGIN
    INSERT INTO dynamic_entity_field_index
      (id, entity_id, model_id, field_code, value_string, value_number, value_date,
       value_datetime, value_boolean, creator, create_time, updater, update_time, deleted, tenant_id)
    OVERRIDING SYSTEM VALUE
    SELECT id, entity_id, model_id, field_code, value_string, value_number, value_date,
           value_datetime, value_boolean, creator, create_time, updater, update_time, deleted, tenant_id
    FROM dblink(
      conn,
      'SELECT id, entity_id, model_id, field_code, value_string, value_number, value_date,
              value_datetime, value_boolean, creator, create_time, updater, update_time, deleted, tenant_id
       FROM system_entity_field_index WHERE deleted = false'
    ) AS t(
      id bigint, entity_id bigint, model_id bigint, field_code varchar,
      value_string varchar, value_number numeric, value_date date,
      value_datetime timestamp, value_boolean boolean,
      creator varchar, create_time timestamp, updater varchar,
      update_time timestamp, deleted boolean, tenant_id bigint
    );
  EXCEPTION WHEN OTHERS THEN
    RAISE NOTICE 'skip dynamic_entity_field_index: %', SQLERRM;
    RETURN;
  END;

  PERFORM setval(
    pg_get_serial_sequence('dynamic_entity_field_index', 'id'),
    COALESCE((SELECT MAX(id) FROM dynamic_entity_field_index), 1)
  );
END $$;
""",
            "",
            "-- category relation",
            """
DO $$
DECLARE
  local_cnt bigint;
  conn text := 'host=${zhglHost} dbname=${zhglDb} user=${zhglUser} password=${zhglPassword}';
BEGIN
  SELECT COUNT(*) INTO local_cnt FROM dynamic_entity_category_relation;
  IF local_cnt > 0 THEN
    RAISE NOTICE 'skip dynamic_entity_category_relation: already has % rows', local_cnt;
    RETURN;
  END IF;

  BEGIN
    INSERT INTO dynamic_entity_category_relation
      (id, entity_id, category_id, business_type_code, sort, creator, create_time, updater, update_time, deleted, tenant_id)
    OVERRIDING SYSTEM VALUE
    SELECT id, entity_id, category_id, business_type_code, COALESCE(sort, 0), creator, create_time, updater, update_time, deleted, tenant_id
    FROM dblink(
      conn,
      'SELECT id, entity_id, category_id, business_type_code, sort, creator, create_time, updater, update_time, deleted, tenant_id
       FROM system_entity_category_relation WHERE deleted = false'
    ) AS t(
      id bigint, entity_id bigint, category_id bigint, business_type_code varchar,
      sort integer, creator varchar, create_time timestamp, updater varchar,
      update_time timestamp, deleted boolean, tenant_id bigint
    );
  EXCEPTION WHEN OTHERS THEN
    RAISE NOTICE 'skip dynamic_entity_category_relation: %', SQLERRM;
    RETURN;
  END;

  PERFORM setval(
    pg_get_serial_sequence('dynamic_entity_category_relation', 'id'),
    COALESCE((SELECT MAX(id) FROM dynamic_entity_category_relation), 1)
  );
END $$;
""",
            "",
            "-- entity relation",
            """
DO $$
DECLARE
  local_cnt bigint;
  conn text := 'host=${zhglHost} dbname=${zhglDb} user=${zhglUser} password=${zhglPassword}';
BEGIN
  SELECT COUNT(*) INTO local_cnt FROM dynamic_entity_relation;
  IF local_cnt > 0 THEN
    RAISE NOTICE 'skip dynamic_entity_relation: already has % rows', local_cnt;
    RETURN;
  END IF;

  BEGIN
    INSERT INTO dynamic_entity_relation
      (id, source_entity_id, target_entity_id, relation_type, relation_name, description,
       relation_attributes, status, field_code, source_model_code, target_model_code,
       source_business_type_code, target_business_type_code,
       creator, create_time, updater, update_time, deleted, tenant_id)
    OVERRIDING SYSTEM VALUE
    SELECT id, source_entity_id, target_entity_id, relation_type, relation_name, description,
           relation_attributes, status, field_code, source_model_code, target_model_code,
           source_business_type_code, target_business_type_code,
           creator, create_time, updater, update_time, deleted, tenant_id
    FROM dblink(
      conn,
      'SELECT id, source_entity_id, target_entity_id, relation_type, relation_name, description,
              relation_attributes, status, field_code, source_model_code, target_model_code,
              source_business_type_code, target_business_type_code,
              creator, create_time, updater, update_time, deleted, tenant_id
       FROM system_entity_relation WHERE deleted = false'
    ) AS t(
      id bigint, source_entity_id bigint, target_entity_id bigint, relation_type varchar,
      relation_name varchar, description text, relation_attributes text, status integer,
      field_code varchar, source_model_code varchar, target_model_code varchar,
      source_business_type_code varchar, target_business_type_code varchar,
      creator varchar, create_time timestamp, updater varchar,
      update_time timestamp, deleted boolean, tenant_id bigint
    );
  EXCEPTION WHEN OTHERS THEN
    RAISE NOTICE 'skip dynamic_entity_relation: %', SQLERRM;
    RETURN;
  END;

  PERFORM setval(
    pg_get_serial_sequence('dynamic_entity_relation', 'id'),
    COALESCE((SELECT MAX(id) FROM dynamic_entity_relation), 1)
  );
END $$;
""",
        ]
    )

    out_file.write_text("\n".join(parts) + "\n", encoding="utf-8")
    print(f"Wrote {out_file}")


if __name__ == "__main__":
    main()
