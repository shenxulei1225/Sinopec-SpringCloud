#!/usr/bin/env python3
"""Apply canonical base fields to PostgreSQL, then optionally re-export platform-import SQL."""

from __future__ import annotations

import argparse
import json
import os
import subprocess
import sys

import psycopg2

from base_fields_canonical import CANONICAL_BASE_FIELDS

TENANT_ID = 1


def connect():
    return psycopg2.connect(
        host=os.environ.get("PGHOST", "127.0.0.1"),
        port=os.environ.get("PGPORT", "5432"),
        dbname=os.environ.get("PGDATABASE", "sinopec"),
        user=os.environ.get("PGUSER", "postgres"),
        password=os.environ.get("PGPASSWORD", "Coolhomer"),
    )


def apply(cur, dry_run: bool = False, *, prune: bool = False, business_types: list[str] | None = None) -> tuple[int, int, int]:
    upserted = removed = 0
    canonical_codes: set[tuple[str, str]] = set()
    scope = business_types if business_types else list(CANONICAL_BASE_FIELDS.keys())

    for business_type_code in scope:
        fields = CANONICAL_BASE_FIELDS.get(business_type_code, [])
        for spec in fields:
            canonical_codes.add((business_type_code, spec["field_code"]))
            type_config = spec.get("type_config")
            tc_json = json.dumps(type_config, ensure_ascii=False) if type_config else None
            if dry_run:
                print(f"  upsert {business_type_code}.{spec['field_code']}")
                upserted += 1
                continue
            cur.execute(
                """
                INSERT INTO dynamic_business_type_base_field (
                  business_type_code, field_code, field_name, data_type, required,
                  description, type_config, sort_order, status, tenant_id, creator
                ) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, 1, %s, 'seed')
                ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
                DO UPDATE SET
                  field_name = EXCLUDED.field_name,
                  data_type = EXCLUDED.data_type,
                  required = EXCLUDED.required,
                  description = EXCLUDED.description,
                  type_config = EXCLUDED.type_config,
                  sort_order = EXCLUDED.sort_order,
                  updater = 'seed',
                  update_time = CURRENT_TIMESTAMP
                """,
                (
                    business_type_code,
                    spec["field_code"],
                    spec["field_name"],
                    spec["data_type"],
                    spec.get("required", False),
                    spec.get("description"),
                    tc_json,
                    spec.get("sort_order", 0),
                    TENANT_ID,
                ),
            )
            upserted += 1

    cur.execute(
        """
        SELECT id, business_type_code, field_code
        FROM dynamic_business_type_base_field
        WHERE deleted = false AND tenant_id = %s
          AND business_type_code = ANY(%s)
        """,
        (TENANT_ID, scope),
    )
    rows = cur.fetchall()
    if not prune:
        if not dry_run:
            cur.execute(
                """
                SELECT COUNT(*) FROM dynamic_business_type_base_field
                WHERE deleted = false AND tenant_id = %s
                """,
                (TENANT_ID,),
            )
            active = cur.fetchone()[0]
        else:
            active = len(rows)
        return upserted, 0, active

    for row_id, btc, fc in rows:
        if (btc, fc) not in canonical_codes:
            if dry_run:
                print(f"  remove {btc}.{fc}")
            else:
                cur.execute(
                    """
                    UPDATE dynamic_business_type_base_field
                    SET deleted = true, updater = 'seed', update_time = CURRENT_TIMESTAMP
                    WHERE id = %s
                    """,
                    (row_id,),
                )
            removed += 1

    if not dry_run:
        cur.execute(
            """
            SELECT COUNT(*) FROM dynamic_business_type_base_field
            WHERE deleted = false AND tenant_id = %s
            """,
            (TENANT_ID,),
        )
        active = cur.fetchone()[0]
    else:
        active = len(canonical_codes)

    return upserted, removed, active


def main():
    parser = argparse.ArgumentParser(description="Apply canonical base fields")
    parser.add_argument("--dry-run", action="store_true", help="Print changes only")
    parser.add_argument(
        "--prune",
        action="store_true",
        help="Soft-delete base fields not in canonical (default: upsert only, never remove)",
    )
    parser.add_argument(
        "--business-type",
        action="append",
        dest="business_types",
        metavar="CODE",
        help="Apply/prune only these business types (repeatable). Default: all in canonical.",
    )
    parser.add_argument("--re-export", action="store_true", help="Run export-platform-import.py after apply")
    args = parser.parse_args()

    conn = connect()
    cur = conn.cursor()
    try:
        cur.execute("SET search_path TO dynamicbusiness")
        scope = args.business_types or list(CANONICAL_BASE_FIELDS.keys())
        print(f"Applying canonical base fields for: {', '.join(scope)}...")
        upserted, removed, active = apply(
            cur,
            dry_run=args.dry_run,
            prune=args.prune,
            business_types=args.business_types,
        )
        if args.dry_run:
            print(f"dry-run: would upsert {upserted}, remove {removed}, final active ~{active}")
        else:
            conn.commit()
            print(f"done: upserted {upserted}, soft-deleted {removed}, active {active}")
    except Exception:
        conn.rollback()
        raise
    finally:
        cur.close()
        conn.close()

    if args.re_export and not args.dry_run:
        script_dir = os.path.dirname(os.path.abspath(__file__))
        subprocess.check_call([sys.executable, os.path.join(script_dir, "export-platform-import.py")])


if __name__ == "__main__":
    main()
