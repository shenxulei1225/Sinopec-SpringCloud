#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
按「检查分类 ↔ 设备分类」级联边，补齐检查项 → 检查分类的分类—实体挂接（tenant=1）。

口径（2026-08-12）：
- 只认库里已有的分类—分类级联（host=inspection_item，member=equipment）；不臆造边
- 对每个级联成员设备分类：取其自身及子孙设备分类上已挂的检查项，挂到对应宿主检查分类
- 已挂过则跳过；软删边可复活
- 不改检查项正文、不改设备分类挂接、不写型号适用

用法：
  python 22_sync_inspection_category_links_from_cascade.py --dry-run
  python 22_sync_inspection_category_links_from_cascade.py
"""

from __future__ import annotations

import argparse
import sys
from typing import Dict, List, Set, Tuple

import psycopg2

PKG = {
    "host": "127.0.0.1",
    "dbname": "sinopec",
    "user": "postgres",
    "password": "Coolhomer",
    "options": "-c search_path=dynamicbusiness",
}

TENANT = 1
UPDATER = "22_sync_insp_cat_cascade"
HOST_TYPE = "inspection_item"
MEMBER_TYPE = "equipment"
ENTITY_TYPE = "inspection_item"


def load_cascade_edges(cur) -> List[Tuple[int, str, int, str]]:
    """(host_id, host_name, member_id, member_name)"""
    cur.execute(
        """
        SELECT
          r.host_category_id,
          hc.name AS host_name,
          r.member_category_id,
          mc.name AS member_name
        FROM dynamic_category_category_relation_t1 r
        JOIN dynamic_category hc
          ON hc.id = r.host_category_id AND hc.deleted = false AND hc.tenant_id = %s
        JOIN dynamic_category mc
          ON mc.id = r.member_category_id AND mc.deleted = false AND mc.tenant_id = %s
        WHERE r.deleted = false
          AND r.tenant_id = %s
          AND r.host_category_type_code = %s
          AND r.member_category_type_code = %s
        ORDER BY hc.name, mc.name, r.host_category_id, r.member_category_id
        """,
        (TENANT, TENANT, TENANT, HOST_TYPE, MEMBER_TYPE),
    )
    return [
        (int(r[0]), str(r[1]), int(r[2]), str(r[3])) for r in cur.fetchall()
    ]


def equipment_subtree_ids(cur, root_id: int) -> List[int]:
    """设备分类节点自身 + 子孙（含非叶子）。"""
    cur.execute(
        """
        WITH RECURSIVE t AS (
          SELECT id FROM dynamic_category
          WHERE id = %s AND deleted = false AND tenant_id = %s
            AND category_type_code = %s
          UNION ALL
          SELECT c.id
          FROM dynamic_category c
          JOIN t ON c.parent_id = t.id
          WHERE c.deleted = false AND c.tenant_id = %s
            AND c.category_type_code = %s
        )
        SELECT id FROM t
        """,
        (root_id, TENANT, MEMBER_TYPE, TENANT, MEMBER_TYPE),
    )
    return [int(r[0]) for r in cur.fetchall()]


def inspection_item_ids_on_equipment_cats(
    cur, equipment_cat_ids: List[int]
) -> List[int]:
    if not equipment_cat_ids:
        return []
    cur.execute(
        """
        SELECT DISTINCT e.id
        FROM dynamic_entity_category_relation_t1 r
        JOIN ent_inspection_item_t1 e
          ON e.id = r.entity_id AND e.deleted = false AND e.tenant_id = %s
        WHERE r.deleted = false
          AND r.tenant_id = %s
          AND r.entity_type_code = %s
          AND r.category_id = ANY(%s)
        ORDER BY e.id
        """,
        (TENANT, TENANT, ENTITY_TYPE, equipment_cat_ids),
    )
    return [int(r[0]) for r in cur.fetchall()]


def ensure_entity_category_link(
    cur, entity_id: int, category_id: int, dry_run: bool
) -> str:
    """
    返回：skip | revive | insert
    """
    cur.execute(
        """
        SELECT id, deleted
        FROM dynamic_entity_category_relation_t1
        WHERE tenant_id = %s
          AND entity_type_code = %s
          AND entity_id = %s
          AND category_id = %s
        """,
        (TENANT, ENTITY_TYPE, entity_id, category_id),
    )
    row = cur.fetchone()
    if row:
        rel_id, deleted = int(row[0]), bool(row[1])
        if not deleted:
            return "skip"
        if dry_run:
            return "revive"
        cur.execute(
            """
            UPDATE dynamic_entity_category_relation_t1
            SET deleted = false, updater = %s, update_time = NOW()
            WHERE id = %s
            """,
            (UPDATER, rel_id),
        )
        return "revive"
    if dry_run:
        return "insert"
    cur.execute(
        """
        INSERT INTO dynamic_entity_category_relation_t1 (
          entity_id, category_id, entity_type_code, sort,
          creator, deleted, tenant_id
        ) VALUES (%s, %s, %s, 0, %s, false, %s)
        """,
        (entity_id, category_id, ENTITY_TYPE, UPDATER, TENANT),
    )
    return "insert"


def main() -> int:
    parser = argparse.ArgumentParser(
        description="Sync inspection_item → inspection category links from category cascade"
    )
    parser.add_argument(
        "--dry-run",
        action="store_true",
        help="只统计将新增/复活的边，不写库",
    )
    args = parser.parse_args()
    dry = bool(args.dry_run)

    conn = psycopg2.connect(**PKG)
    try:
        with conn:
            with conn.cursor() as cur:
                edges = load_cascade_edges(cur)
                print(
                    f"cascade edges (host={HOST_TYPE} → member={MEMBER_TYPE}): {len(edges)}"
                )
                if not edges:
                    print(
                        "no cascade edges; nothing to sync "
                        "(will not invent inspection↔equipment links)"
                    )
                    return 0

                # host_id → set of equipment category ids (member + descendants)
                host_equip: Dict[int, Set[int]] = {}
                host_names: Dict[int, str] = {}
                for host_id, host_name, member_id, member_name in edges:
                    host_names[host_id] = host_name
                    subtree = equipment_subtree_ids(cur, member_id)
                    host_equip.setdefault(host_id, set()).update(subtree)
                    print(
                        f"  cascade: [{host_id}] {host_name} ← "
                        f"[{member_id}] {member_name} "
                        f"(equip subtree size={len(subtree)})"
                    )

                inserted = revived = skipped = 0
                host_item_counts: Dict[int, int] = {}

                for host_id, equip_ids in sorted(host_equip.items()):
                    item_ids = inspection_item_ids_on_equipment_cats(
                        cur, sorted(equip_ids)
                    )
                    host_item_counts[host_id] = len(item_ids)
                    print(
                        f"  host [{host_id}] {host_names.get(host_id)}: "
                        f"{len(item_ids)} item(s) from equip cats"
                    )
                    for eid in item_ids:
                        action = ensure_entity_category_link(
                            cur, eid, host_id, dry
                        )
                        if action == "insert":
                            inserted += 1
                        elif action == "revive":
                            revived += 1
                        else:
                            skipped += 1

                if not dry:
                    conn.commit()

                print(
                    f"{'DRY-RUN ' if dry else ''}done: "
                    f"insert={inserted} revive={revived} skip={skipped} "
                    f"hosts={len(host_equip)}"
                )
                return 0
    except Exception as exc:
        print(f"ERROR: {exc}", file=sys.stderr)
        return 1
    finally:
        conn.close()


if __name__ == "__main__":
    sys.exit(main())
