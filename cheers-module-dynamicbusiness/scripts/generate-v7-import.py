#!/usr/bin/env python3
"""Generate Flyway V7: import model/category metadata from zhgl via dblink."""
from __future__ import annotations

import os
import subprocess
from pathlib import Path

PG_HOST = "127.0.0.1"
PG_USER = "postgres"
PG_PASSWORD = "Coolhomer"
SOURCE_DB = "zhgl"

OUT = Path(__file__).resolve().parent.parent / (
    "cheers-module-dynamicbusiness-server/src/main/resources/db/migration/dynamicbusiness/V7__import_model_metadata_from_zhgl.sql"
)

# target_table -> (source_table, select_sql, insert_columns, dblink_row_type)
IMPORTS: list[tuple[str, str, str, str, str]] = [
    (
        "dynamic_category_type",
        "system_category_type",
        "id, category_type_code, name, description, status, top_level_category_id, creator, create_time, updater, update_time, deleted, tenant_id",
        """SELECT id, category_type_code, name, description, status, top_level_category_id, creator, create_time, updater, update_time, deleted, tenant_id
           FROM system_category_type WHERE deleted = false""",
        "id bigint, category_type_code varchar, name varchar, description varchar, status integer, top_level_category_id bigint, creator varchar, create_time timestamp, updater varchar, update_time timestamp, deleted boolean, tenant_id bigint",
    ),
    (
        "dynamic_field",
        "system_field",
        "id, code, name, type, unit, description, source, status, max_relations, index_strategy, options, provider_code, semantic_type, creator, create_time, updater, update_time, deleted, tenant_id",
        """SELECT id, code, name, type, unit, description, source, status, max_relations, index_strategy, options, provider_code, semantic_type, creator, create_time, updater, update_time, deleted, tenant_id
           FROM system_field WHERE deleted = false""",
        "id bigint, code varchar, name varchar, type varchar, unit varchar, description varchar, source varchar, status integer, max_relations integer, index_strategy varchar, options varchar, provider_code varchar, semantic_type varchar, creator varchar, create_time timestamp, updater varchar, update_time timestamp, deleted boolean, tenant_id bigint",
    ),
    (
        "dynamic_model",
        "system_model",
        "id, code, name, business_type_code, description, status, sort, field_groups_config, creator, create_time, updater, update_time, deleted, tenant_id",
        """SELECT id, code, name, business_type_code, description, status, sort, field_groups_config, creator, create_time, updater, update_time, deleted, tenant_id
           FROM system_model WHERE deleted = false""",
        "id bigint, code varchar, name varchar, business_type_code varchar, description varchar, status integer, sort integer, field_groups_config text, creator varchar, create_time timestamp, updater varchar, update_time timestamp, deleted boolean, tenant_id bigint",
    ),
    (
        "dynamic_model_field_assignment",
        "system_model_field_assignment",
        "id, model_id, field_id, required, is_searchable, is_filterable, is_sortable, default_value, validation_rules, sort, field_group_id, field_source, ref_library_id, model_relation_id, target_business_type, creator, create_time, updater, update_time, deleted, tenant_id",
        """SELECT id, model_id, field_id, required, is_searchable, is_filterable, is_sortable, default_value, validation_rules, sort, field_group_id, field_source, ref_library_id, model_relation_id, target_business_type, creator, create_time, updater, update_time, deleted, tenant_id
           FROM system_model_field_assignment WHERE deleted = false""",
        "id bigint, model_id bigint, field_id bigint, required boolean, is_searchable boolean, is_filterable boolean, is_sortable boolean, default_value varchar, validation_rules text, sort integer, field_group_id bigint, field_source varchar, ref_library_id bigint, model_relation_id bigint, target_business_type varchar, creator varchar, create_time timestamp, updater varchar, update_time timestamp, deleted boolean, tenant_id bigint",
    ),
    (
        "dynamic_category",
        "system_category",
        "id, parent_id, name, code, category_type_code, tree_path, level, sort, status, description, creator, create_time, updater, update_time, deleted, tenant_id",
        """SELECT id, parent_id, name, code, COALESCE(NULLIF(TRIM(category_type_code), ''), 'default') AS category_type_code, tree_path, level, sort, status, description, creator, create_time, updater, update_time, deleted, tenant_id
           FROM system_category WHERE deleted = false""",
        "id bigint, parent_id bigint, name varchar, code varchar, category_type_code varchar, tree_path varchar, level integer, sort integer, status integer, description text, creator varchar, create_time timestamp, updater varchar, update_time timestamp, deleted boolean, tenant_id bigint",
    ),
    (
        "dynamic_model_category_relation",
        "system_model_category_relation",
        "id, model_id, category_id, business_type_code, sort, creator, create_time, updater, update_time, deleted, tenant_id",
        """SELECT id, model_id, category_id, business_type_code, sort, creator, create_time, updater, update_time, deleted, tenant_id
           FROM system_model_category_relation WHERE deleted = false""",
        "id bigint, model_id bigint, category_id bigint, business_type_code varchar, sort integer, creator varchar, create_time timestamp, updater varchar, update_time timestamp, deleted boolean, tenant_id bigint",
    ),
    (
        "dynamic_model_relation",
        "system_model_relation",
        "id, business_type_relation_id, source_model_id, source_model_code, target_model_id, target_model_code, relation_name, field_code, auto_generated, creator, create_time, updater, update_time, deleted, tenant_id",
        """SELECT id, business_type_relation_id, source_model_id, source_model_code, target_model_id, target_model_code, relation_name, field_code, auto_generated, creator, create_time, updater, update_time, deleted, tenant_id
           FROM system_model_relation WHERE deleted = false""",
        "id bigint, business_type_relation_id bigint, source_model_id bigint, source_model_code varchar, target_model_id bigint, target_model_code varchar, relation_name varchar, field_code varchar, auto_generated boolean, creator varchar, create_time timestamp, updater varchar, update_time timestamp, deleted boolean, tenant_id bigint",
    ),
    (
        "dynamic_model_relation_declaration",
        "system_model_relation_declaration",
        "id, model_id, target_business_type, creator, create_time, updater, update_time, deleted, tenant_id",
        """SELECT id, model_id, target_business_type, creator, create_time, updater, update_time, deleted, tenant_id
           FROM system_model_relation_declaration WHERE deleted = false""",
        "id bigint, model_id bigint, target_business_type varchar, creator varchar, create_time timestamp, updater varchar, update_time timestamp, deleted boolean, tenant_id bigint",
    ),
]

TRUNCATE_ORDER = [
    "dynamic_model_field_assignment",
    "dynamic_model_category_relation",
    "dynamic_model_relation",
    "dynamic_model_relation_declaration",
    "dynamic_model",
    "dynamic_category",
    "dynamic_category_type",
    "dynamic_field",
]


def psql_count(db: str, table: str) -> int:
    env = os.environ.copy()
    env["PGPASSWORD"] = PG_PASSWORD
    sql = f"SET search_path TO dynamicbusiness; SELECT COUNT(*) FROM {table};"
    r = subprocess.run(
        ["psql", "-h", PG_HOST, "-U", PG_USER, "-d", db, "-tAc", sql],
        capture_output=True,
        text=True,
        env=env,
        check=True,
    )
    return int(r.stdout.strip() or "0")


def remote_count(source_table: str) -> int:
    env = os.environ.copy()
    env["PGPASSWORD"] = PG_PASSWORD
    sql = f"SELECT COUNT(*) FROM {source_table} WHERE deleted = false;"
    r = subprocess.run(
        ["psql", "-h", PG_HOST, "-U", PG_USER, "-d", SOURCE_DB, "-tAc", sql],
        capture_output=True,
        text=True,
        env=env,
        check=True,
    )
    return int(r.stdout.strip() or "0")


def import_block(target: str, _source: str, cols: str, select_sql: str, dblink_types: str, *, skip_if_any_local: bool = False) -> str:
    col_list = cols
    skip_clause = ""
    if skip_if_any_local:
        skip_clause = f"""
  IF local_cnt > 0 THEN
    RAISE NOTICE 'skip {target}: local already has % rows (field library pre-imported; use V11 to remap field_id by code/name)', local_cnt;
    RETURN;
  END IF;
"""
    return f"""
-- {target}
DO $$
DECLARE
  local_cnt bigint;
  remote_cnt bigint;
  conn text := 'host=${{zhglHost}} dbname=${{zhglDb}} user=${{zhglUser}} password=${{zhglPassword}}';
BEGIN
  EXECUTE format('SELECT COUNT(*) FROM dynamicbusiness.%I', '{target}') INTO local_cnt;

  BEGIN
    SELECT cnt INTO remote_cnt
    FROM dblink(conn, 'SELECT COUNT(*)::bigint AS cnt FROM {_source} WHERE deleted = false') AS t(cnt bigint);
  EXCEPTION WHEN OTHERS THEN
    RAISE NOTICE 'skip {target}: zhgl unavailable (%)', SQLERRM;
    RETURN;
  END;
{skip_clause}
  IF remote_cnt = 0 THEN
    RAISE NOTICE 'skip {target}: no rows in zhgl';
    RETURN;
  END IF;

  IF local_cnt >= remote_cnt AND local_cnt > 0 THEN
    RAISE NOTICE 'skip {target}: local % >= remote % (already imported)', local_cnt, remote_cnt;
    RETURN;
  END IF;

  EXECUTE format('TRUNCATE dynamicbusiness.%I RESTART IDENTITY CASCADE', '{target}');

  INSERT INTO dynamicbusiness.{target} ({col_list})
  SELECT {col_list}
  FROM dblink(conn, $dblink${select_sql}$dblink$) AS t({dblink_types});

  PERFORM setval(
    pg_get_serial_sequence('dynamicbusiness.{target}', 'id'),
    COALESCE((SELECT MAX(id) FROM dynamicbusiness.{target}), 1)
  );

  RAISE NOTICE 'imported {target}: % rows', remote_cnt;
END $$;
"""


def main() -> None:
    parts = [
        "-- Import model/category metadata from zhgl (field, model, assignments, category).",
        "-- Idempotent: skips each table when local row count >= zhgl.",
        "-- dynamic_field: skips when local has any rows (pre-imported field library; run V11 after).",
        "-- Preserves IDs from zhgl so entity.model_id stays valid.",
        "-- Run V8 afterward to normalize legacy business_type_code values.",
        "",
        "CREATE EXTENSION IF NOT EXISTS dblink;",
        "SET search_path TO dynamicbusiness, public;",
        "",
        "-- Truncate dependent metadata once if model table needs full refresh",
        "DO $$",
        "DECLARE",
        "  local_models bigint;",
        "  remote_models bigint;",
        "  conn text := 'host=${zhglHost} dbname=${zhglDb} user=${zhglUser} password=${zhglPassword}';",
        "BEGIN",
        "  SELECT COUNT(*) INTO local_models FROM dynamic_model;",
        "  BEGIN",
        "    SELECT cnt INTO remote_models",
        "    FROM dblink(conn, 'SELECT COUNT(*)::bigint FROM system_model WHERE deleted=false') AS t(cnt bigint);",
        "  EXCEPTION WHEN OTHERS THEN",
        "    RAISE NOTICE 'V7: zhgl unavailable, skip metadata import';",
        "    RETURN;",
        "  END;",
        "  IF local_models >= remote_models AND local_models > 0 THEN",
        "    RAISE NOTICE 'V7: models already synced (local=%, remote=%), per-table skip applies', local_models, remote_models;",
        "    RETURN;",
        "  END IF;",
        "  TRUNCATE TABLE",
    ]
    parts.append("    " + ",\n    ".join(TRUNCATE_ORDER) + "\n  RESTART IDENTITY CASCADE;")
    parts.append("  RAISE NOTICE 'V7: truncated metadata tables for full refresh';")
    parts.append("END $$;")
    parts.append("")

    for target, source, cols, select_sql, types in IMPORTS:
        parts.append(
            import_block(
                target, source, cols, select_sql, types,
                skip_if_any_local=(target == "dynamic_field"),
            )
        )

    OUT.write_text("\n".join(parts) + "\n", encoding="utf-8")
    print(f"Wrote {OUT}")
    print(f"  local dynamic_model={psql_count('sinopec', 'dynamic_model')}, zhgl system_model={remote_count('system_model')}")


if __name__ == "__main__":
    main()
