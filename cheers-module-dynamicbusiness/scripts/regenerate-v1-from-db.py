#!/usr/bin/env python3
"""Regenerate Flyway V1 and platform-import 01_schema.sql from live PostgreSQL DDL."""

from __future__ import annotations

import os
import re
import subprocess
from datetime import date
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
V1 = (
    ROOT
    / "cheers-module-dynamicbusiness-server/src/main/resources/db/migration/dynamicbusiness/V1__init_dynamicbusiness_schema.sql"
)
SCHEMA_IMPORT = ROOT / "scripts/platform-import/system/01_schema.sql"
BACKUP_DIR = ROOT / "cheers-module-dynamicbusiness-server/src/main/resources/db/backup/flyway-legacy-pre-seed"

HEADER = f"""-- ============================================================================
-- Flyway V1: dynamicbusiness 全量表结构（纯 DDL）
-- Generated: {date.today().isoformat()} by scripts/regenerate-v1-from-db.py
--
-- 约定: 所有建表/索引/注释集中在本文件；数据见 platform-import/system/seed/
-- 新环境: Flyway 仅执行 V1 → 再运行 platform-import/system/import.sh
-- ============================================================================

CREATE SCHEMA IF NOT EXISTS dynamicbusiness;
SET search_path TO dynamicbusiness;

"""

UK_DYNAMIC_GROUP = """
-- seed 幂等：FIELD 等业务分组按 (group_type, code) 唯一
CREATE UNIQUE INDEX IF NOT EXISTS uk_dynamic_group_type_code_tenant
    ON dynamicbusiness.dynamic_group (tenant_id, group_type, code)
    WHERE deleted = false;
"""


def pg_dump_schema() -> str:
    env = os.environ.copy()
    env.setdefault("PGHOST", "127.0.0.1")
    env.setdefault("PGPORT", "5432")
    env.setdefault("PGDATABASE", "sinopec")
    env.setdefault("PGUSER", "postgres")
    env.setdefault("PGPASSWORD", "Coolhomer")
    proc = subprocess.run(
        [
            "pg_dump",
            "-h",
            env["PGHOST"],
            "-p",
            env["PGPORT"],
            "-U",
            env["PGUSER"],
            "-d",
            env["PGDATABASE"],
            "-n",
            "dynamicbusiness",
            "--schema-only",
            "--no-owner",
            "--no-privileges",
        ],
        capture_output=True,
        text=True,
        check=True,
        env=env,
    )
    return proc.stdout


def clean_dump(raw: str) -> str:
    lines: list[str] = []
    skip = False
    for line in raw.splitlines():
        if line.startswith("\\"):
            continue
        if re.match(r"^SET (statement_timeout|lock_timeout|idle_in_transaction|client_encoding|standard_conforming|check_function|xmloption|client_min_messages|row_security|default_table)", line):
            continue
        if line.strip() == "SELECT pg_catalog.set_config('search_path', '', false);":
            continue
        if "CREATE SCHEMA dynamicbusiness" in line:
            continue
        if "flyway_schema_history_dynamicbusiness" in line:
            skip = True
            continue
        if skip:
            if line.startswith("-- Name:") and "flyway_schema_history" not in line:
                skip = False
            else:
                continue
        lines.append(line)
    body = "\n".join(lines).strip()
    body = re.sub(r"\n{3,}", "\n\n", body)
    if "uk_dynamic_group_type_code_tenant" not in body:
        body += UK_DYNAMIC_GROUP
    return HEADER + body + "\n"


def write_schema_files(body: str):
    V1.parent.mkdir(parents=True, exist_ok=True)
    V1.write_text(body, encoding="utf-8")
    import_body = body.split("SET search_path TO dynamicbusiness;", 1)[-1].strip()
    import_header = f"""-- ============================================================================
-- 系统共用 · 01 建表（与 Flyway V1 同源）
-- Generated: {date.today().isoformat()} by scripts/regenerate-v1-from-db.py
-- ============================================================================

SET search_path TO dynamicbusiness;

"""
    SCHEMA_IMPORT.parent.mkdir(parents=True, exist_ok=True)
    SCHEMA_IMPORT.write_text(import_header + import_body + "\n", encoding="utf-8")


def archive_legacy_flyway():
    mig_dir = V1.parent
    BACKUP_DIR.mkdir(parents=True, exist_ok=True)
    for path in sorted(mig_dir.glob("V*.sql")):
        if path.name == V1.name:
            continue
        target = BACKUP_DIR / path.name
        if target.exists():
            target.unlink()
        path.rename(target)
        print(f"archived {path.name} -> {target.relative_to(ROOT)}")


def main():
    raw = pg_dump_schema()
    body = clean_dump(raw)
    write_schema_files(body)
    archive_legacy_flyway()
    print("done:", V1)
    print("done:", SCHEMA_IMPORT)


if __name__ == "__main__":
    main()
