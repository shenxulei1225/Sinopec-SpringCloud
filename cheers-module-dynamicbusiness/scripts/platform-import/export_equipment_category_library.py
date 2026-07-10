#!/usr/bin/env python3
"""
导出标准设备分类库 → dynamic_category_equipment.generated.sql

替代原 zhgl system_category 导出；形成可扩展的行业设备分类树（EQCAT-* 编码）。

用法:
  python export_equipment_category_library.py
"""

from __future__ import annotations

import pathlib
import textwrap

from equipment_category_taxonomy import TAXONOMY, flatten_taxonomy

ROOT = pathlib.Path(__file__).resolve().parent
SEED_DIR = ROOT / "system" / "seed"
OUTPUT = SEED_DIR / "dynamic_category_equipment.generated.sql"
ENTITY_TYPE = "equipment"
TENANT_ID = 1


def esc(value: str) -> str:
    return value.replace("'", "''")


def parent_expr(parent_code: str) -> str:
    if parent_code == "equipment_root":
        return (
            f"(SELECT p.id FROM dynamic_category p "
            f"WHERE p.deleted = false AND p.tenant_id = {TENANT_ID} "
            f"AND p.code = 'equipment_root' LIMIT 1)"
        )
    return (
        f"(SELECT p.id FROM dynamic_category p "
        f"WHERE p.deleted = false AND p.tenant_id = {TENANT_ID} "
        f"AND p.code = '{esc(parent_code)}' LIMIT 1)"
    )


def build_sql() -> str:
    rows = flatten_taxonomy(TAXONOMY, parent_code="equipment_root")
    leaf_count = sum(1 for _code, _name, parent, _s in rows if parent.startswith("EQCAT-DEV-") or _code.startswith("EQCAT-DEV-"))
    # count leaves: codes starting with EQCAT-DEV-
    leaf_count = sum(1 for code, _n, _p, _s in rows if code.startswith("EQCAT-DEV-"))

    blocks = [
        "-- Standard equipment category library (EQCAT-*)",
        "-- Source: equipment_category_taxonomy.py",
        "-- Regenerate: python export_equipment_category_library.py",
        f"-- Nodes: {len(rows) + 1} incl. equipment_root; leaf categories: {leaf_count}",
        "",
        "SET search_path TO dynamicbusiness;",
        "",
        textwrap.dedent(
            f"""
            INSERT INTO dynamic_category (
              parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
            ) VALUES (
              NULL, 'equipment_root', 'equipment_root',
              '{ENTITY_TYPE}', 0,
              1, {TENANT_ID}, 'seed', NULL
            )
            ON CONFLICT (code, tenant_id) WHERE deleted = false
            DO UPDATE SET
              parent_id = EXCLUDED.parent_id,
              name = EXCLUDED.name,
              category_type_code = EXCLUDED.category_type_code,
              sort = EXCLUDED.sort,
              status = EXCLUDED.status,
              parent_code = EXCLUDED.parent_code,
              updater = 'seed',
              update_time = CURRENT_TIMESTAMP;
            """
        ).strip(),
        "",
    ]

    for code, name, parent_code, sort in rows:
        parent_code_col = "NULL" if parent_code == "equipment_root" else f"'{esc(parent_code)}'"
        blocks.append(
            textwrap.dedent(
                f"""
                INSERT INTO dynamic_category (
                  parent_id, name, code, category_type_code, sort, status, tenant_id, creator, parent_code
                ) VALUES (
                  {parent_expr(parent_code)}, '{esc(name)}', '{esc(code)}',
                  '{ENTITY_TYPE}', {sort},
                  1, {TENANT_ID}, 'seed', {parent_code_col}
                )
                ON CONFLICT (code, tenant_id) WHERE deleted = false
                DO UPDATE SET
                  parent_id = EXCLUDED.parent_id,
                  name = EXCLUDED.name,
                  category_type_code = EXCLUDED.category_type_code,
                  sort = EXCLUDED.sort,
                  status = EXCLUDED.status,
                  parent_code = EXCLUDED.parent_code,
                  updater = 'seed',
                  update_time = CURRENT_TIMESTAMP;
                """
            ).strip()
        )
        blocks.append("")

    return "\n".join(blocks) + "\n"


def main() -> None:
    sql = build_sql()
    OUTPUT.write_text(sql, encoding="utf-8")
    rows = flatten_taxonomy(TAXONOMY, parent_code="equipment_root")
    leaves = [c for c, _n, _p, _s in rows if c.startswith("EQCAT-DEV-")]
    print(f"Wrote {OUTPUT.name}: {len(rows)} nodes + root, {len(leaves)} leaf categories")


if __name__ == "__main__":
    main()
