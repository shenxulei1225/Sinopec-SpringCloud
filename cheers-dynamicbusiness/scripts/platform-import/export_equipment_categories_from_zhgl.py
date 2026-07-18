#!/usr/bin/env python3
"""Export equipment categories from zhgl to platform-import seed SQL.

@deprecated 请改用标准分类库：python export_equipment_category_library.py
本脚本仅在与 zhgl 库同步历史 CAT-* 编码时使用。
"""
from __future__ import annotations

import csv
import io
import os
import pathlib
import subprocess
import textwrap

ROOT = pathlib.Path(__file__).resolve().parent
SEED_DIR = ROOT / "system" / "seed"

QUERY = """
SELECT c.code,
       COALESCE(p.code, ''),
       c.name,
       COALESCE(c.sort, 0)::text,
       COALESCE(c.level, 0)::text
FROM system_category c
LEFT JOIN system_category p ON p.id = c.parent_id AND p.deleted = false
WHERE c.deleted = false
  AND c.category_type_code = 'equipment'
ORDER BY c.level NULLS FIRST, c.parent_id NULLS FIRST, c.sort, c.id
"""


def esc(value: str) -> str:
    return value.replace("'", "''")


def fetch_rows() -> list[tuple[str, str, str, int, int]]:
    env = os.environ.copy()
    env["PGPASSWORD"] = env.get("PGPASSWORD", "Coolhomer")
    env["PGCLIENTENCODING"] = "UTF8"
    result = subprocess.run(
        [
            "psql",
            "-h",
            env.get("PGHOST", "127.0.0.1"),
            "-U",
            env.get("PGUSER", "postgres"),
            "-d",
            "zhgl",
            "-t",
            "-A",
            "-F",
            "\t",
            "-c",
            QUERY,
        ],
        capture_output=True,
        text=True,
        encoding="utf-8",
        env=env,
        check=True,
    )
    rows: list[tuple[str, str, str, int, int]] = []
    for line in result.stdout.splitlines():
        if not line.strip():
            continue
        code, parent_code, name, sort, level = line.split("\t", 4)
        rows.append((code, parent_code, name, int(sort), int(level)))
    return sort_for_insert(rows)


def sort_for_insert(
    rows: list[tuple[str, str, str, int, int]],
) -> list[tuple[str, str, str, int, int]]:
    """Parents before children: root first, then by level / sort."""

    def sort_key(item: tuple[str, str, str, int, int]) -> tuple:
        code, parent_code, _name, sort, level = item
        if code == "equipment_root":
            return (0, 0, sort, code)
        effective_level = level if level > 0 else 1
        return (1, effective_level, sort, code)

    return sorted(rows, key=sort_key)


def parent_expr(parent_code: str, self_code: str) -> str:
    if not parent_code:
        if self_code == "equipment_root":
            return "NULL"
        parent_code = "equipment_root"
    return (
        f"(SELECT p.id FROM dynamic_category p "
        f"WHERE p.deleted = false AND p.tenant_id = 1 AND p.code = '{esc(parent_code)}' LIMIT 1)"
    )


def build_category_inserts(rows) -> str:
    blocks: list[str] = [
        "-- equipment categories exported from zhgl.system_category (category_type_code=equipment)",
        "-- Source: corridor/tunnel reference data; id-agnostic upsert by code",
        "-- Regenerate: python export_equipment_categories_from_zhgl.py",
        "",
        "SET search_path TO dynamicbusiness;",
        "",
    ]
    for code, parent_code, name, sort, _level in rows:
        blocks.append(
            textwrap.dedent(
                f"""
                INSERT INTO dynamic_category (
                  parent_id, name, code, category_type_code, sort, status, tenant_id, creator
                ) VALUES (
                  {parent_expr(parent_code, code)}, '{esc(name)}', '{esc(code)}',
                  'equipment', {sort},
                  1, 1, 'seed'
                )
                ON CONFLICT (code, tenant_id) WHERE deleted = false
                DO UPDATE SET
                  parent_id = EXCLUDED.parent_id,
                  name = EXCLUDED.name,
                  category_type_code = EXCLUDED.category_type_code,
                  sort = EXCLUDED.sort,
                  status = EXCLUDED.status,
                  updater = 'seed',
                  update_time = CURRENT_TIMESTAMP;
                """
            ).strip()
        )
        blocks.append("")
    return "\n".join(blocks)


def build_category_type_sql() -> str:
    return textwrap.dedent(
        """
        -- dynamic_category_type: equipment (from zhgl system_category_type id=9)

        INSERT INTO dynamic_category_type (
          category_type_code, name, description, status, top_level_category_id, tenant_id, creator
        ) VALUES (
          'equipment', '设备分类',
          '设备管理模型分组（categoryTypeCode=equipment，来源 zhgl 管廊）', 1,
          (SELECT c.id FROM dynamic_category c
           WHERE c.deleted = false AND c.tenant_id = 1 AND c.code = 'equipment_root' LIMIT 1),
          1, 'seed'
        )
        ON CONFLICT (category_type_code, tenant_id) WHERE deleted = false
        DO UPDATE SET
          name = EXCLUDED.name,
          description = EXCLUDED.description,
          status = EXCLUDED.status,
          top_level_category_id = EXCLUDED.top_level_category_id,
          updater = 'seed',
          update_time = CURRENT_TIMESTAMP;
        """
    ).strip()


def main() -> None:
    rows = fetch_rows()
    equipment_sql = build_category_inserts(rows)

    category_path = SEED_DIR / "dynamic_category_equipment.generated.sql"
    category_path.write_text(equipment_sql + "\n", encoding="utf-8")

    type_path = SEED_DIR / "dynamic_category_type.sql"
    type_path.write_text(
        textwrap.dedent(
            """
            -- ============================================================================
            -- 系统 · dynamic_category_type
            -- Generated from zhgl.system_category_type (equipment)
            -- ============================================================================

            SET search_path TO dynamicbusiness;

            """
        ).lstrip()
        + build_category_type_sql()
        + "\n",
        encoding="utf-8",
    )

    print(f"Exported {len(rows)} equipment categories -> {category_path.name}")
    print(f"Wrote category type seed -> {type_path.name}")


if __name__ == "__main__":
    main()
