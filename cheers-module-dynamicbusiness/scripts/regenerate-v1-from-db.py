#!/usr/bin/env python3
"""Regenerate Flyway V1 schema from live PostgreSQL DDL (schema-only, no data)."""

from __future__ import annotations

import os
import re
import subprocess
from datetime import date
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
MIG_DIR = (
    ROOT
    / "cheers-module-dynamicbusiness-server/src/main/resources/db/migration/dynamicbusiness"
)
V1 = MIG_DIR / "V1__init_dynamicbusiness_schema.sql"
BACKUP_DIR = ROOT / "cheers-module-dynamicbusiness-server/src/main/resources/db/backup/flyway-legacy-pre-seed"

# Active Flyway chain for empty DB: V1 (DDL) → V2 (assignment codes) → V3 (association codes)
ACTIVE_MIGRATIONS = frozenset(
    {
        "V1__init_dynamicbusiness_schema.sql",
        "V2__model_field_assignment_codes.sql",
        "V3__cross_platform_association_codes.sql",
    }
)

HEADER = f"""-- ============================================================================
-- Flyway V1: dynamicbusiness 全量表结构（纯 DDL）
-- Generated: {date.today().isoformat()} by scripts/regenerate-v1-from-db.py
--
-- 约定: 建表/索引集中在本文件；增量版本见同目录 V2+；数据见 platform-import seed
-- 刷新本文件 = 修改已执行迁移 → 所有已有机库需 scripts/flyway-repair-local.sh（见 .cursor/rules/flyway-migration.mdc）
-- 新空库: Flyway 顺序执行 classpath 全部 V*.sql → platform-import/import-dev-all.sh（仅 seed）
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
        if re.match(
            r"^SET (statement_timeout|lock_timeout|idle_in_transaction|client_encoding|standard_conforming|check_function|xmloption|client_min_messages|row_security|default_table)",
            line,
        ):
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


def archive_stray_migrations():
    """Move any V*.sql not in ACTIVE_MIGRATIONS to backup (historical zhgl/seed scripts)."""
    BACKUP_DIR.mkdir(parents=True, exist_ok=True)
    for path in sorted(MIG_DIR.glob("V*.sql")):
        if path.name in ACTIVE_MIGRATIONS:
            continue
        target = BACKUP_DIR / path.name
        if target.exists():
            target.unlink()
        path.rename(target)
        print(f"archived {path.name} -> {target.relative_to(ROOT)}")


def main():
    raw = pg_dump_schema()
    body = clean_dump(raw)
    V1.parent.mkdir(parents=True, exist_ok=True)
    V1.write_text(body, encoding="utf-8")
    archive_stray_migrations()
    print("done:", V1)
    print("active flyway:", ", ".join(sorted(ACTIVE_MIGRATIONS)))
    print("warn: 已有机库请执行 scripts/flyway-repair-local.sh 对齐 V1 checksum")


if __name__ == "__main__":
    main()
