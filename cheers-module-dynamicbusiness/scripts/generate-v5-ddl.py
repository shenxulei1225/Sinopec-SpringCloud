#!/usr/bin/env python3
"""One-off generator: convert pg_dump biz_* schema to Flyway V5 migration."""
from pathlib import Path

SRC = Path(__file__).resolve().parent.parent / (
    "cheers-module-dynamicbusiness-server/src/main/resources/db/migration/dynamicbusiness/_raw_biz_schema.sql"
)
OUT = SRC.parent / "V5__create_dedicated_entity_tables.sql"

SKIP_PREFIXES = (
    "\\restrict",
    "\\unrestrict",
    "SET ",
    "SELECT pg_catalog.set_config",
)


def should_skip_add_constraint_block(lines: list[str], i: int) -> bool:
    line = lines[i].strip()
    if line.startswith("ALTER TABLE") and "ADD CONSTRAINT" in lines[i]:
        return True
    if line.startswith("ALTER TABLE") and i + 1 < len(lines) and "ADD CONSTRAINT" in lines[i + 1]:
        return True
    return False


def skip_until_semicolon(lines: list[str], i: int) -> int:
    while i < len(lines) and not lines[i].strip().endswith(";"):
        i += 1
    return i + 1


text = SRC.read_text(encoding="utf-8")
raw_lines = text.splitlines()
lines_out: list[str] = [
    "-- Dedicated entity tables (DDL from zhgl public.biz_*)",
    "SET search_path TO dynamicbusiness;",
    "",
]

i = 0
while i < len(raw_lines):
    line = raw_lines[i]
    stripped = line.strip()
    if stripped.startswith(SKIP_PREFIXES):
        i += 1
        continue
    if stripped.startswith("--") and (
        "PostgreSQL database dump" in line
        or "Dumped from database" in line
        or "Dumped by pg_dump" in line
    ):
        i += 1
        continue
    if "ALTER SEQUENCE" in line and "OWNED BY" in line:
        i += 1
        continue
    if should_skip_add_constraint_block(raw_lines, i):
        i = skip_until_semicolon(raw_lines, i)
        continue

    line = line.replace("public.", "")
    if line.startswith("CREATE TABLE "):
        line = line.replace("CREATE TABLE ", "CREATE TABLE IF NOT EXISTS ", 1)
    elif line.startswith("CREATE SEQUENCE "):
        line = line.replace("CREATE SEQUENCE ", "CREATE SEQUENCE IF NOT EXISTS ", 1)
    elif line.startswith("CREATE INDEX ") and "IF NOT EXISTS" not in line:
        line = line.replace("CREATE INDEX ", "CREATE INDEX IF NOT EXISTS ", 1)
    elif line.startswith("CREATE UNIQUE INDEX ") and "IF NOT EXISTS" not in line:
        line = line.replace("CREATE UNIQUE INDEX ", "CREATE UNIQUE INDEX IF NOT EXISTS ", 1)
    elif line.startswith("ALTER TABLE ONLY "):
        line = line.replace("ALTER TABLE ONLY ", "ALTER TABLE ", 1)
    lines_out.append(line)
    i += 1

OUT.write_text("\n".join(lines_out) + "\n", encoding="utf-8")
print(f"Wrote {OUT} ({len(lines_out)} lines)")
