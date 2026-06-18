#!/usr/bin/env python3
"""Regenerate dynamicbusiness Flyway baseline: V1 schema + V2 data seed from live DB."""

from __future__ import annotations

import os
import subprocess
import sys
from datetime import date
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent / (
    "cheers-module-dynamicbusiness-server/src/main/resources/db/migration/dynamicbusiness"
)
V1 = ROOT / "V1__init_dynamicbusiness_schema.sql"
V2 = ROOT / "V2__seed_dynamicbusiness_data.sql"

PG = os.environ.get(
    "PG_BIN",
    "/Applications/Postgres.app/Contents/Versions/latest/bin",
)
ENV = {
    **os.environ,
    "PGPASSWORD": os.environ.get("PGPASSWORD", "Coolhomer"),
}


def run_dump(args: list[str]) -> str:
    cmd = [
        f"{PG}/pg_dump",
        "-h",
        os.environ.get("PGHOST", "127.0.0.1"),
        "-p",
        os.environ.get("PGPORT", "5432"),
        "-U",
        os.environ.get("PGUSER", "postgres"),
        "-d",
        os.environ.get("PGDATABASE", "sinopec"),
        "--no-owner",
        "--no-privileges",
        *args,
    ]
    result = subprocess.run(cmd, capture_output=True, text=True, env=ENV, check=False)
    if result.returncode != 0:
        print(result.stderr, file=sys.stderr)
        sys.exit(result.returncode)
    return result.stdout


def header(kind: str) -> str:
    today = date.today().isoformat()
    return f"""-- ============================================================================
-- Dynamic Business Flyway baseline ({kind})
-- Generated: {today} by scripts/generate-flyway-baseline.py
-- Source: live sinopec.dynamicbusiness (post zhgl migration)
--
-- Fresh install: V1 (schema) -> V2 (seed). No zhgl / dblink required.
-- Regenerate after metadata changes on Mac, then commit V1/V2.
-- ============================================================================

CREATE SCHEMA IF NOT EXISTS dynamicbusiness;
SET search_path TO dynamicbusiness;

"""


def strip_pg_dump_preamble(sql: str) -> str:
    lines: list[str] = []
    skip_prefix = True
    for line in sql.splitlines():
        if line.startswith("\\"):
            continue
        if skip_prefix:
            if line.startswith("SET ") or line.startswith("SELECT pg_catalog.set_config"):
                continue
            if line.startswith("CREATE SCHEMA") or line.strip() == "":
                continue
            if line.startswith("--") and (
                "PostgreSQL database dump" in line
                or "Dumped from database" in line
                or "Dumped by pg_dump" in line
            ):
                continue
            skip_prefix = False
        lines.append(line)
    return "\n".join(lines).strip() + "\n"


def post_process_schema(sql: str) -> str:
    lines: list[str] = []
    for line in sql.splitlines():
        stripped = line.strip()
        if stripped == "CREATE SCHEMA dynamicbusiness;":
            continue
        if stripped.startswith("SELECT pg_catalog.set_config('search_path', '', false)"):
            continue
        if stripped.startswith("SET ") or stripped.startswith("SELECT pg_catalog.set_config"):
            continue
        lines.append(line)
    return "\n".join(lines).strip() + "\n"


def post_process_data(sql: str) -> str:
    lines: list[str] = []
    for line in sql.splitlines():
        if line.strip().startswith("SET ") or line.strip().startswith("SELECT pg_catalog.set_config"):
            continue
        lines.append(line)
    return "\n".join(lines).strip() + "\n"


def main() -> int:
    if not Path(PG).joinpath("pg_dump").exists():
        print(f"pg_dump not found under {PG}; set PG_BIN", file=sys.stderr)
        return 1

    ROOT.mkdir(parents=True, exist_ok=True)

    schema_sql = run_dump([
        "--schema-only",
        "--schema=dynamicbusiness",
        "--exclude-table=dynamicbusiness.flyway_schema_history_dynamicbusiness",
    ])
    V1.write_text(
        header("schema") + post_process_schema(strip_pg_dump_preamble(schema_sql)),
        encoding="utf-8",
    )

    data_sql = run_dump([
        "--data-only",
        "--column-inserts",
        "--schema=dynamicbusiness",
        "--exclude-table=dynamicbusiness.flyway_schema_history_dynamicbusiness",
    ])
    V2.write_text(
        header("data seed") + post_process_data(strip_pg_dump_preamble(data_sql)),
        encoding="utf-8",
    )

    print(f"Wrote {V1} ({V1.stat().st_size // 1024} KB)")
    print(f"Wrote {V2} ({V2.stat().st_size // 1024} KB)")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
