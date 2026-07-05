#!/usr/bin/env python3
"""Normalize L0 V1: inline PK/defaults, rename legacy sequences, remove f_f_* columns."""

from __future__ import annotations

import re
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
V1 = (
    ROOT
    / "cheers-module-dynamicbusiness-server/src/main/resources/db/migration/dynamicbusiness/V1__init_dynamicbusiness_schema.sql"
)
SCHEMA_COPY = ROOT / "scripts/platform-import/00_schema.sql"

SEQ_RENAME = {
    "biz_dian_wei_id_seq": "biz_inspection_point_id_seq",
    "biz_ke_hu_id_seq": "biz_customer_id_seq",
    "biz_lu_xian_guan_li_id_seq": "biz_route_id_seq",
    "biz_shou_fei_id_seq": "biz_billing_id_seq",
    "biz_ying_ji_dui_wu_id_seq": "biz_emergency_team_id_seq",
    "biz_ying_ji_zi_yuan_id_seq": "biz_emergency_resource_id_seq",
    "biz_jian_cha_nei_rong_id_seq": "biz_inspection_item_id_seq",
    "biz_inspection_management_id_seq": "biz_patrol_id_seq",
    "biz_task_model_6bfa9a8c9be648cebc21737115690e21_id_seq": "biz_task_id_seq",
    "business_capability_seq": "business_capability_id_seq",
    "capability_component_projection_seq": "capability_component_projection_id_seq",
    "model_crud_form_definition_seq": "model_crud_form_definition_id_seq",
}

INDEX_PREFIX_RENAME = (
    ("idx_biz_jian_cha_nei_rong_", "idx_biz_inspection_item_"),
    ("idx_biz_inspection_management_", "idx_biz_patrol_"),
    (
        "idx_biz_task_model_6bfa9a8c9be648cebc21737115690e21_",
        "idx_biz_task_",
    ),
)

HEADER = """-- ============================================================================
-- L0 / Flyway V1: dynamicbusiness 表结构（纯 DDL，可直接 psql -f）
-- Generated: 2026-07-05 (normalized by scripts/normalize-v1-schema.py)
--
-- 职责: 建表 — 每张表一条 CREATE TABLE（含 PRIMARY KEY、id 序列 DEFAULT）
-- 不含: ALTER TABLE 补默认值/约束、函数、INSERT、运行时 f_f_* 物理列
--
-- 导入顺序（全平台）:
--   1) 本文件 — 建表
--   2) 01_field_library.sql — 字段库
--   3) 02_model_library.sql — 模型库
--   4) 03_model_categories.sql — 分类（产品包）
--   5) 05_entities.sql — 实体实例（按需）
-- ============================================================================

CREATE SCHEMA IF NOT EXISTS dynamicbusiness;
SET search_path TO dynamicbusiness;

"""


def canonical_seq(seq: str) -> str:
    return SEQ_RENAME.get(seq, seq)


def normalize_nextval(text: str) -> str:
    def repl(match: re.Match) -> str:
        seq = canonical_seq(match.group(1))
        return f"nextval('dynamicbusiness.{seq}'::regclass)"

    return re.sub(
        r"nextval\('dynamicbusiness\.([^']+)'::regclass\)", repl, text
    )


def parse_defaults(section: str) -> dict[str, str]:
    out: dict[str, str] = {}
    for m in re.finditer(
        r"ALTER TABLE ONLY dynamicbusiness\.(\w+) ALTER COLUMN (\w+) "
        r"SET DEFAULT nextval\('dynamicbusiness\.([^']+)'::regclass\);",
        section,
    ):
        table, col, seq = m.group(1), m.group(2), m.group(3)
        if col == "id":
            out[table] = canonical_seq(seq)
    return out


def parse_pkeys(section: str) -> dict[str, tuple[str, str]]:
    out: dict[str, tuple[str, str]] = {}
    for m in re.finditer(
        r"ALTER TABLE ONLY dynamicbusiness\.(\w+)\s+"
        r"ADD CONSTRAINT (\w+) PRIMARY KEY \(([^)]+)\);",
        section,
        re.MULTILINE,
    ):
        table, cname, cols = m.group(1), m.group(2), m.group(3).strip()
        out[table] = (cname, cols)
    return out


def parse_create_tables(text: str) -> list[tuple[str, list[str]]]:
    tables: list[tuple[str, list[str]]] = []
    for m in re.finditer(
        r"CREATE TABLE dynamicbusiness\.(\w+) \((.*?)\);",
        text,
        re.DOTALL,
    ):
        table = m.group(1)
        body = m.group(2)
        lines = [ln.rstrip() for ln in body.splitlines() if ln.strip()]
        tables.append((table, lines))
    return tables


def col_name(line: str) -> str:
    return line.strip().split()[0]


def normalize_table(
    table: str,
    lines: list[str],
    defaults: dict[str, str],
    pkeys: dict[str, tuple[str, str]],
) -> tuple[str, set[str]]:
    used_seqs: set[str] = set()
    filtered: list[str] = []
    has_id = False
    has_pk = False

    for line in lines:
        stripped = line.strip().rstrip(",")
        if not stripped or stripped.upper().startswith("CONSTRAINT "):
            if stripped.upper().startswith("CONSTRAINT ") and "PRIMARY KEY" in stripped.upper():
                has_pk = True
            if stripped:
                filtered.append(stripped)
            continue
        name = col_name(stripped)
        if name.startswith("f_f_"):
            continue
        if name == "id":
            has_id = True
        if "PRIMARY KEY" in stripped.upper():
            has_pk = True
        stripped = normalize_nextval(stripped)
        filtered.append(stripped)

    # Merge ALTER default for id
    if table in defaults and has_id:
        seq = defaults[table]
        used_seqs.add(seq)
        new_filtered: list[str] = []
        for line in filtered:
            if col_name(line) == "id" and "DEFAULT" not in line.upper():
                base = line.rstrip(",").rstrip()
                if base.endswith("NOT NULL"):
                    base = base[: -len("NOT NULL")].rstrip()
                line = (
                    f"{base} DEFAULT nextval('dynamicbusiness.{seq}'::regclass) NOT NULL"
                )
            new_filtered.append(line)
        filtered = new_filtered
    else:
        for line in filtered:
            m = re.search(
                r"nextval\('dynamicbusiness\.([^']+)'::regclass\)", line
            )
            if m:
                used_seqs.add(m.group(1))

    # Primary key
    if not has_pk:
        if table in pkeys:
            cname, cols = pkeys[table]
            if cols == "id":
                cname = f"{table}_pkey"
        elif has_id:
            cname, cols = f"{table}_pkey", "id"
        elif table == "base_field_library_name_alias":
            cname, cols = "base_field_library_name_alias_pkey", "base_field_name"
        else:
            cname = cols = ""
        if cname and cols:
            filtered.append(f"CONSTRAINT {cname} PRIMARY KEY ({cols})")

    body = ",\n    ".join(filtered)
    ddl = f"CREATE TABLE dynamicbusiness.{table} (\n    {body}\n);"
    return ddl, used_seqs


def normalize_indexes(text: str) -> str:
    out = text
    for old, new in INDEX_PREFIX_RENAME:
        out = out.replace(old, new)
    return normalize_nextval(out)


def main() -> None:
    raw = V1.read_text(encoding="utf-8")
    m = re.search(r"-- COLUMN DEFAULTS\n\n", raw)
    if not m:
        raise SystemExit("COLUMN DEFAULTS section not found")
    before = raw[: m.start()]
    rest = raw[m.end() :]
    m2 = re.search(r"-- INDEXES\n\n", rest)
    if not m2:
        raise SystemExit("INDEXES section not found")
    alter_section = rest[: m2.start()]
    tail = rest[m2.end() :]

    defaults = parse_defaults(alter_section)
    pkeys = parse_pkeys(alter_section)

    first_table = before.find("CREATE TABLE dynamicbusiness.")
    if first_table < 0:
        raise SystemExit("CREATE TABLE not found")
    tables_text = before[first_table:]

    all_used_seqs: set[str] = set()
    table_ddls: list[str] = []
    for table, lines in parse_create_tables(tables_text):
        ddl, seqs = normalize_table(table, lines, defaults, pkeys)
        all_used_seqs |= seqs
        table_ddls.append(ddl)

    seq_lines: list[str] = ["-- SEQUENCES\n"]
    for seq in sorted(all_used_seqs):
        seq_lines.append(
            f"CREATE SEQUENCE dynamicbusiness.{seq}\n"
            "    START WITH 1\n"
            "    INCREMENT BY 1\n"
            "    NO MINVALUE\n"
            "    NO MAXVALUE\n"
            "    CACHE 1;\n"
        )

    indexes = normalize_indexes(tail)
    out = (
        HEADER
        + "\n".join(seq_lines)
        + "\n-- TABLES\n\n"
        + "\n\n".join(table_ddls)
        + "\n\n-- INDEXES\n\n"
        + indexes.lstrip()
    )
    out = re.sub(r"\n{4,}", "\n\n\n", out)

    V1.write_text(out, encoding="utf-8")
    SCHEMA_COPY.write_text(out, encoding="utf-8")
    print(f"normalized {len(table_ddls)} tables, {len(all_used_seqs)} sequences")
    print(f"wrote {V1}")
    print(f"wrote {SCHEMA_COPY}")


if __name__ == "__main__":
    main()
