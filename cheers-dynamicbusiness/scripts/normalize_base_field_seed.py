#!/usr/bin/env python3
"""将 dynamic_entity_type_base_field seed 规范为 library_field_id + 字段库 code。"""

from __future__ import annotations

import re
from pathlib import Path

ROOT = Path(__file__).resolve().parent
SEED = ROOT / "platform-import/system/seed/dynamic_entity_type_base_field.sql"

INSERT_RE = re.compile(
    r"INSERT INTO dynamic_entity_type_base_field \(\s*"
    r"entity_type_code, field_code, field_name, data_type, required, default_value,\s*"
    r"description, type_config, sort_order, status, tenant_id, creator\s*"
    r"\) VALUES \(\s*"
    r"'([^']+)',\s*'([^']+)',\s*"
    r"'((?:''|[^'])*)',\s*'([^']+)',\s*"
    r"(TRUE|FALSE),\s*([^,]*),\s*"
    r"([^,]*),\s*([^,]*),\s*"
    r"(\d+),\s*(\d+),\s*"
    r"(\d+),\s*'([^']+)'\s*"
    r"\)\s*"
    r"ON CONFLICT",
    re.DOTALL,
)


def library_code(entity_type: str, field_code: str) -> str:
    code = field_code.strip()
    if code.startswith("FLD-"):
        return code
    return f"FLD-BASE-{entity_type.strip()}-{code}"


def library_id_expr(code: str) -> str:
    escaped = code.replace("'", "''")
    return (
        "(SELECT f.id FROM dynamic_field f "
        f"WHERE f.deleted = false AND f.tenant_id = 1 AND f.code = '{escaped}' LIMIT 1)"
    )


def transform(content: str) -> str:
    def repl(match: re.Match[str]) -> str:
        entity_type, field_code, field_name, data_type = match.group(1), match.group(2), match.group(3), match.group(4)
        required, default_value = match.group(5), match.group(6).strip()
        description, type_config = match.group(7).strip(), match.group(8).strip()
        sort_order, status, tenant_id, creator = match.group(9), match.group(10), match.group(11), match.group(12)
        lib_code = library_code(entity_type, field_code)
        lib_id = library_id_expr(lib_code)
        return (
            "INSERT INTO dynamic_entity_type_base_field (\n"
            "  entity_type_code, library_field_id, field_code, field_name, data_type, required, default_value,\n"
            "  description, type_config, sort_order, status, tenant_id, creator\n"
            ") VALUES (\n"
            f"  '{entity_type}', {lib_id}, '{lib_code}',\n"
            f"  '{field_name}', '{data_type}',\n"
            f"  {required}, {default_value},\n"
            f"  {description}, {type_config},\n"
            f"  {sort_order}, {status},\n"
            f"  {tenant_id}, '{creator}'\n"
            ")\n"
            "ON CONFLICT"
        )

    header = """-- ============================================================================
-- 系统 · dynamic_entity_type_base_field
--
-- 约定：
--   library_field_id → dynamic_field.id（权威关联）
--   field_code       → dynamic_field.code（与字段库编码一致）
-- 前置：dynamic_field 中已有对应字段库条目（见 dynamic_base_field_library_fields.sql）
-- ============================================================================

SET search_path TO dynamicbusiness;

"""
    body = INSERT_RE.sub(repl, content)
    body = re.sub(
        r"-- =+.*?dynamic_entity_type_base_field:.*?\n",
        f"-- dynamic_entity_type_base_field: upsert by (entity_type_code, field_code)\n\n",
        body,
        count=1,
        flags=re.DOTALL,
    )
    body = body.replace(
        "DO UPDATE SET\n  field_name = EXCLUDED.field_name,",
        "DO UPDATE SET\n  library_field_id = EXCLUDED.library_field_id,\n  field_name = EXCLUDED.field_name,",
    )
    if "SET search_path TO dynamicbusiness;" in body:
        body = body.split("SET search_path TO dynamicbusiness;", 1)[-1].lstrip()
    return header + body


def main() -> None:
    original = SEED.read_text(encoding="utf-8")
    SEED.write_text(transform(original), encoding="utf-8")
    print(f"updated {SEED}")


if __name__ == "__main__":
    main()
