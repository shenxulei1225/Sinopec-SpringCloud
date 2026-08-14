#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
按「分类—实体关联」同步设备型号的适用检查项（tenant=1）。

定稿口径（2026-08-11）：
- 检查项编码为稳定数字码；含义看名称；分类只认关联边
- 禁止再用 INSP-PKG-* 包编码 / 型号名关键字推断「属于哪类」
- 本脚本只做：型号已挂的设备分类 → 读取挂在这些分类上的检查项 → 写型号—实体适用集合
- 同义检查项不合并；不创建检查项、不改分类挂接（挂接由 16 等专用脚本维护）

用法：
  python 09_apply_equipment_inspection_packages.py --dry-run
  python 09_apply_equipment_inspection_packages.py

历史：曾按专业「检查包」创建 INSP-PKG-* 并用型号名归类；已废弃该路径。
"""

from __future__ import annotations

import argparse
import sys
from collections import defaultdict
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
SKIP_MODEL_NAMES = {"测试模型", "单独"}


def main() -> int:
    ap = argparse.ArgumentParser()
    ap.add_argument("--dry-run", action="store_true")
    args = ap.parse_args()
    dry = args.dry_run

    conn = psycopg2.connect(**PKG)
    conn.autocommit = False
    cur = conn.cursor()

    # 只认设备品类树（category_type_code=equipment），排除分区/检查主题等其它种类，
    # 避免型号误挂多类时把无关检查项灌进适用集合。
    cur.execute(
        """
        SELECT m.id, m.name,
               COALESCE(
                 array_agg(DISTINCT r.category_id) FILTER (
                   WHERE r.category_id IS NOT NULL AND cat.category_type_code='equipment'
                 ),
                 '{}'
               )
        FROM dynamic_model m
        LEFT JOIN dynamic_model_category_relation_t1 r
          ON r.model_id=m.id AND r.deleted=false
        LEFT JOIN dynamic_category cat
          ON cat.id=r.category_id AND cat.deleted=false
        WHERE m.deleted=false AND m.tenant_id=%s AND m.entity_type_code='equipment'
        GROUP BY m.id, m.name
        ORDER BY m.id
        """,
        (TENANT,),
    )
    models = cur.fetchall()

    rel_add = rel_del = 0
    with_items = 0
    without_cat = 0
    without_items = 0
    samples: List[Tuple[int, str, int, int]] = []

    for mid, mname, cat_ids in models:
        if mname in SKIP_MODEL_NAMES:
            continue
        cats: List[int] = [int(c) for c in (cat_ids or []) if c]
        if not cats:
            without_cat += 1
            want: Set[int] = set()
        else:
            # 若同时挂了祖先与叶子，只取最深一层，避免父类糊项灌进型号适用集合
            cur.execute(
                """
                SELECT id, level FROM dynamic_category
                WHERE deleted=false AND id = ANY(%s)
                """,
                (cats,),
            )
            level_rows = cur.fetchall()
            max_level = max(int(lv) for _, lv in level_rows if lv is not None)
            cats = [int(cid) for cid, lv in level_rows if lv == max_level]
            cur.execute(
                """
                SELECT DISTINCT ecr.entity_id
                FROM dynamic_entity_category_relation_t1 ecr
                JOIN ent_inspection_item_t1 e
                  ON e.id=ecr.entity_id AND e.deleted=false AND e.tenant_id=%s
                JOIN dynamic_category cat
                  ON cat.id=ecr.category_id AND cat.deleted=false
                 AND cat.category_type_code='equipment'
                WHERE ecr.deleted=false AND ecr.tenant_id=%s
                  AND ecr.entity_type_code='inspection_item'
                  AND ecr.category_id = ANY(%s)
                """,
                (TENANT, TENANT, cats),
            )
            want = {int(r[0]) for r in cur.fetchall()}

        if not cats:
            pass
        elif not want:
            without_items += 1
        else:
            with_items += 1

        cur.execute(
            """
            SELECT id, entity_id FROM dynamic_model_entity_relation_t1
            WHERE deleted=false AND tenant_id=%s
              AND model_id=%s AND model_entity_type_code='equipment'
              AND entity_type_code='inspection_item'
            """,
            (TENANT, mid),
        )
        current = {int(eid): int(rid) for rid, eid in cur.fetchall()}

        if not dry:
            for eid in sorted(want - set(current)):
                cur.execute(
                    """
                    INSERT INTO dynamic_model_entity_relation_t1 (
                      model_id, model_entity_type_code, entity_id, entity_type_code,
                      domain, sort, creator, deleted, tenant_id
                    ) VALUES (%s,'equipment',%s,'inspection_item', NULL, 0, 'seed-cat-sync', false, %s)
                    """,
                    (mid, eid, TENANT),
                )
                rel_add += 1
            for eid, rid in current.items():
                if eid not in want:
                    cur.execute(
                        """
                        UPDATE dynamic_model_entity_relation_t1
                        SET deleted=true, updater='seed-cat-sync', update_time=CURRENT_TIMESTAMP
                        WHERE id=%s
                        """,
                        (rid,),
                    )
                    rel_del += 1
        else:
            rel_add += len(want - set(current))
            rel_del += len(set(current) - want)

        if len(samples) < 12 and (want or current):
            samples.append((int(mid), mname, len(cats), len(want)))

    if dry:
        conn.rollback()
    else:
        conn.commit()

    print("=== sync model applicability from category–entity links ===")
    print(f"dry_run={dry}")
    print(f"models={len(models)} with_items={with_items} no_category={without_cat} cat_but_no_items={without_items}")
    print(f"rel_add={rel_add} rel_del={rel_del}")
    print("--- samples (model_id, cats, items, name) ---")
    for mid, mname, nc, ni in samples:
        print(f"{mid}\t{nc}\t{ni}\t{mname[:60]}")
    print("NOTE: 不再创建 INSP-PKG-*；分类挂接请用 16_fix_video_monitoring_leaf_items.py 等专用脚本。")

    cur.close()
    conn.close()
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
