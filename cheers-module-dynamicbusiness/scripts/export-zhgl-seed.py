#!/usr/bin/env python3
"""Export ZHGL legacy field pool + groups (+ optional model assignments) as idempotent seed SQL."""

from __future__ import annotations

import argparse
import os
from datetime import date
from pathlib import Path

import psycopg2

from seed_codegen import (
    TENANT_ID,
    compose_model_library,
    compose_zhgl_field_pool,
    id_to_code_map,
)

ROOT = Path(__file__).resolve().parent
OUT_DIR = ROOT / "platform-import" / "zhgl-legacy"
DEFAULT_BACKUP_DB = os.environ.get("ZHGL_SOURCE_DB", "zhgl_import_temp")


def connect(dbname: str):
    return psycopg2.connect(
        host=os.environ.get("PGHOST", "127.0.0.1"),
        port=os.environ.get("PGPORT", "5432"),
        dbname=dbname,
        user=os.environ.get("PGUSER", "postgres"),
        password=os.environ.get("PGPASSWORD", "Coolhomer"),
    )


def fetch_rows(cur, sql: str, params=None) -> list[dict]:
    cur.execute(sql, params or ())
    cols = [d[0] for d in cur.description]
    return [dict(zip(cols, row)) for row in cur.fetchall()]


def file_header(title: str, source: str, deps: str) -> str:
    return f"""-- ============================================================================
-- {title}
-- Generated: {date.today().isoformat()} by scripts/export-zhgl-seed.py
-- Source: {source}
--
-- 约定：不写 surrogate id；幂等键为 code / (group_type, code) / (model_code, field_code)。
{deps}
-- ============================================================================

SET search_path TO dynamicbusiness;

"""


def normalize_field(row: dict) -> dict:
    source = (row.get("source") or "USER").strip()
    if source not in ("SYSTEM", "USER", "CUSTOM"):
        source = "CUSTOM"
    index_strategy = row.get("index_strategy") or "NONE"
    unit = row.get("unit")
    if unit is not None and str(unit).strip() == "":
        unit = None
    description = row.get("description")
    if description is not None and str(description).strip() == "":
        description = None
    options = row.get("options")
    if options is not None and str(options).strip() == "":
        options = None
    return {
        "code": row["code"],
        "name": row["name"],
        "type": row["type"],
        "unit": unit,
        "description": description,
        "source": source,
        "status": row.get("status", 1),
        "max_relations": row.get("max_relations"),
        "index_strategy": index_strategy,
        "options": options,
        "provider_code": row.get("provider_code"),
        "semantic_type": row.get("semantic_type"),
    }


def export_fields(cur) -> list[dict]:
    rows = fetch_rows(
        cur,
        """
        SELECT code, name, type, unit, description, source, status,
               max_relations, index_strategy, options,
               NULL::varchar AS provider_code,
               NULL::varchar AS semantic_type
        FROM public.system_field
        WHERE deleted = false
        ORDER BY code
        """,
    )
    return [normalize_field(r) for r in rows]


def export_field_groups(cur) -> list[dict]:
    return fetch_rows(
        cur,
        """
        SELECT id, code, name, description, parent_id, path, level, sort, status
        FROM public.system_field_group
        WHERE deleted = false
        ORDER BY sort, code
        """,
    )


def export_field_group_relations(cur) -> list[dict]:
    return fetch_rows(
        cur,
        """
        SELECT g.code AS group_code, f.code AS field_code, r.sort
        FROM public.system_field_group_relation r
        JOIN public.system_field_group g ON g.id = r.field_group_id AND g.deleted = false
        JOIN public.system_field f ON f.id = r.field_id AND f.deleted = false
        WHERE r.deleted = false
        ORDER BY g.code, r.sort, f.code
        """,
    )


def export_models(cur) -> list[dict]:
    return fetch_rows(
        cur,
        """
        SELECT id, code, name, business_type_code AS entity_type_code,
               description, status, 0 AS sort, field_groups_config
        FROM public.system_model
        WHERE deleted = false
        ORDER BY code
        """,
    )


def export_model_field_assignments(cur) -> list[dict]:
    return fetch_rows(
        cur,
        """
        SELECT a.model_id, a.field_id,
               COALESCE(a.required, false) AS required,
               false AS is_searchable,
               false AS is_filterable,
               false AS is_sortable,
               a.default_value,
               a.validation_rules,
               0 AS sort,
               NULL::bigint AS field_group_id,
               'USER_ADDED'::varchar AS field_source,
               NULL::bigint AS ref_library_id,
               NULL::bigint AS model_relation_id,
               NULL::varchar AS target_entity_type
        FROM public.system_model_field_assignment a
        WHERE a.deleted = false
        ORDER BY a.model_id, a.field_id
        """,
    )


def write_file(path: Path, title: str, source: str, deps: str, body: str):
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(file_header(title, source, deps) + body, encoding="utf-8")


def main():
    parser = argparse.ArgumentParser(description="Export ZHGL field seeds from legacy PostgreSQL backup DB")
    parser.add_argument("--source-db", default=DEFAULT_BACKUP_DB, help="Restored ZHGL database name")
    parser.add_argument("--with-assignments", action="store_true", help="Also export model field assignments")
    args = parser.parse_args()

    with connect(args.source_db) as conn:
        with conn.cursor() as cur:
            fields = export_fields(cur)
            groups = export_field_groups(cur)
            relations = export_field_group_relations(cur)

            field_body = compose_zhgl_field_pool(fields, groups, relations)
            write_file(
                OUT_DIR / "01_field_pool.sql",
                "ZHGL 遗留 · 01 字段池与分组",
                f"PostgreSQL database `{args.source_db}` ← zhgl_20260401_132252.backup",
                "-- 依赖：dynamicbusiness schema 已初始化\n-- 含 dynamic_field、dynamic_group(FIELD)、dynamic_group_relation",
                field_body,
            )

            if args.with_assignments:
                models = export_models(cur)
                assignments = export_model_field_assignments(cur)
                all_fields = fetch_rows(
                    cur,
                    "SELECT id, code FROM public.system_field WHERE deleted = false",
                )
                assign_body = compose_model_library(models, assignments, [], [], all_fields)
                write_file(
                    OUT_DIR / "02_model_field_assignments.sql",
                    "ZHGL 遗留 · 02 模型字段分配（按 code 解析）",
                    f"PostgreSQL database `{args.source_db}`",
                    "-- 依赖：01_field_pool.sql；目标库中已存在同名 dynamic_model.code 才会写入",
                    assign_body,
                )

    print(f"Exported {len(fields)} fields, {len(groups)} groups, {len(relations)} group relations -> {OUT_DIR}")
    if args.with_assignments:
        print(f"Also exported model assignments ({len(assignments)} rows source)")


if __name__ == "__main__":
    main()
