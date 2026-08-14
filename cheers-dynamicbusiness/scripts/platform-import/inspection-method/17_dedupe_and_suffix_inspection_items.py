#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
检查项：同名克隆去重 + 按设备叶子补「（分类名）」后缀（tenant=1）。

背景：16 只整理了「视频监控」叶子。其余大量短名项是种子重复克隆
（同名多实体、挂接集合相同），点父级检查分类时会看到成片同名。

本脚本口径：
1) 无「（…）」后缀的同名实体：保留最小 id，软删其余实体及其分类/型号关联
2) 恰好挂 1 个设备叶子、且名称无后缀：改名为「原名（叶子分类名）」
3) 无叶子、但设备挂接「前沿」恰好 1 个（最深且无子挂接）：改名为「原名（该类名）」
4) 不合并「内容（叶子A）」与「内容（叶子B）」这类同义分项
5) 不推断规范包；只认现有分类—实体边

用法：
  python 17_dedupe_and_suffix_inspection_items.py --dry-run
  python 17_dedupe_and_suffix_inspection_items.py
"""

from __future__ import annotations

import argparse
import re
from typing import Dict, List, Optional, Sequence, Tuple

import psycopg2
from psycopg2.extras import DictCursor

PKG = {
    "host": "127.0.0.1",
    "dbname": "sinopec",
    "user": "postgres",
    "password": "Coolhomer",
    "options": "-c search_path=dynamicbusiness",
}

TENANT = 1
SUFFIX_RE = re.compile(r"（[^（）]+）\s*$")


def strip_suffix(name: str) -> str:
    return SUFFIX_RE.sub("", name or "").strip()


def with_suffix(base: str, label: str) -> str:
    b = strip_suffix(base)
    lab = (label or "").strip()
    if not b or not lab:
        return b
    return f"{b}（{lab}）"


def soft_delete_entities(cur, ids: Sequence[int], dry_run: bool) -> int:
    if not ids:
        return 0
    if dry_run:
        print(f"  [dry-run] soft-delete entities={len(ids)} ids={list(ids)[:12]}...")
        return len(ids)
    cur.execute(
        """
        UPDATE ent_inspection_item_t1
        SET deleted = true, updater = '17_dedupe_suffix', update_time = NOW()
        WHERE id = ANY(%s) AND deleted = false
        """,
        (list(ids),),
    )
    n = cur.rowcount
    cur.execute(
        """
        UPDATE dynamic_entity_category_relation_t1
        SET deleted = true, updater = '17_dedupe_suffix', update_time = NOW()
        WHERE entity_id = ANY(%s) AND deleted = false
        """,
        (list(ids),),
    )
    cur.execute(
        """
        UPDATE dynamic_model_entity_relation_t1
        SET deleted = true, updater = '17_dedupe_suffix', update_time = NOW()
        WHERE entity_id = ANY(%s) AND deleted = false
        """,
        (list(ids),),
    )
    return n


def dedupe_unsuffixed_clones(cur, dry_run: bool) -> int:
    cur.execute(
        """
        SELECT name, array_agg(id ORDER BY id) AS ids
        FROM ent_inspection_item_t1
        WHERE deleted = false
          AND name !~ '（.+）$'
        GROUP BY name
        HAVING count(*) > 1
        """
    )
    removed = 0
    for row in cur.fetchall():
        ids: List[int] = list(row["ids"])
        keep, drop = ids[0], ids[1:]
        print(f"  dedupe name={row['name']!r} keep={keep} drop={drop}")
        removed += soft_delete_entities(cur, drop, dry_run)
    return removed


def load_one_leaf_targets(cur) -> List[Tuple[int, str, str]]:
    """(entity_id, old_name, leaf_name) 恰好一个设备叶子。"""
    cur.execute(
        """
        WITH leaf_links AS (
          SELECT e.id AS entity_id, e.name AS old_name, c.id AS cat_id, c.name AS leaf_name
          FROM ent_inspection_item_t1 e
          JOIN dynamic_entity_category_relation_t1 r
            ON r.entity_id = e.id AND r.deleted = false
          JOIN dynamic_category c
            ON c.id = r.category_id AND c.deleted = false
           AND c.tenant_id = %s AND c.category_type_code = 'equipment'
          WHERE e.deleted = false
            AND e.name !~ '（.+）$'
            AND NOT EXISTS (
              SELECT 1 FROM dynamic_category ch
              WHERE ch.parent_id = c.id AND ch.deleted = false AND ch.tenant_id = %s
            )
        ),
        counted AS (
          SELECT entity_id, old_name, count(*) AS leaf_cnt,
                 min(leaf_name) AS leaf_name
          FROM leaf_links
          GROUP BY entity_id, old_name
        )
        SELECT entity_id, old_name, leaf_name
        FROM counted
        WHERE leaf_cnt = 1
        ORDER BY entity_id
        """,
        (TENANT, TENANT),
    )
    return [(int(r["entity_id"]), r["old_name"], r["leaf_name"]) for r in cur.fetchall()]


def load_one_frontier_targets(cur) -> List[Tuple[int, str, str]]:
    """
    无设备叶子时：设备挂接前沿恰好 1 个（该挂接类的子类未再挂到同一实体）。
    用于入侵报警等 L3 糊挂项，至少用父类名区分。
    """
    cur.execute(
        """
        WITH equip_links AS (
          SELECT e.id AS entity_id, e.name AS old_name, c.id AS cat_id, c.name AS cat_name, c.level
          FROM ent_inspection_item_t1 e
          JOIN dynamic_entity_category_relation_t1 r
            ON r.entity_id = e.id AND r.deleted = false
          JOIN dynamic_category c
            ON c.id = r.category_id AND c.deleted = false
           AND c.tenant_id = %s AND c.category_type_code = 'equipment'
          WHERE e.deleted = false
            AND e.name !~ '（.+）$'
        ),
        has_leaf AS (
          SELECT DISTINCT el.entity_id
          FROM equip_links el
          WHERE NOT EXISTS (
            SELECT 1 FROM dynamic_category ch
            WHERE ch.parent_id = el.cat_id AND ch.deleted = false AND ch.tenant_id = %s
          )
        ),
        frontier AS (
          SELECT el.entity_id, el.old_name, el.cat_id, el.cat_name, el.level
          FROM equip_links el
          WHERE el.entity_id NOT IN (SELECT entity_id FROM has_leaf)
            AND NOT EXISTS (
              SELECT 1 FROM equip_links child
              JOIN dynamic_category c2 ON c2.id = child.cat_id
              WHERE child.entity_id = el.entity_id
                AND c2.parent_id = el.cat_id
            )
        ),
        counted AS (
          SELECT entity_id, old_name, count(*) AS n, min(cat_name) AS cat_name
          FROM frontier
          GROUP BY entity_id, old_name
        )
        SELECT entity_id, old_name, cat_name
        FROM counted
        WHERE n = 1
        ORDER BY entity_id
        """,
        (TENANT, TENANT),
    )
    return [(int(r["entity_id"]), r["old_name"], r["cat_name"]) for r in cur.fetchall()]


def existing_names(cur) -> Dict[str, int]:
    cur.execute(
        """
        SELECT name, id FROM ent_inspection_item_t1 WHERE deleted = false
        """
    )
    return {r["name"]: int(r["id"]) for r in cur.fetchall()}


def rename_targets(
    cur,
    targets: Sequence[Tuple[int, str, str]],
    dry_run: bool,
    label: str,
) -> Tuple[int, int]:
    names = existing_names(cur)
    renamed = 0
    dropped = 0
    for eid, old, cat_label in targets:
        new_name = with_suffix(old, cat_label)
        if not new_name or new_name == old:
            continue
        owner = names.get(new_name)
        if owner is not None and owner != eid:
            # 叶子上已有规范名项：旧短名实体视为残留，软删
            print(
                f"  [{label}] drop stale id={eid} {old!r} "
                f"(target {new_name!r} exists id={owner})"
            )
            dropped += soft_delete_entities(cur, [eid], dry_run)
            names.pop(old, None)
            continue
        print(f"  [{label}] id={eid} {old!r} -> {new_name!r}")
        if not dry_run:
            cur.execute(
                """
                UPDATE ent_inspection_item_t1
                SET name = %s, updater = '17_dedupe_suffix', update_time = NOW()
                WHERE id = %s AND deleted = false
                """,
                (new_name, eid),
            )
        names.pop(old, None)
        names[new_name] = eid
        renamed += 1
    return renamed, dropped


def summarize(cur) -> None:
    cur.execute(
        """
        SELECT
          count(*) AS total,
          count(*) FILTER (WHERE name ~ '（.+）$') AS with_suf,
          count(*) FILTER (WHERE name !~ '（.+）$') AS no_suf
        FROM ent_inspection_item_t1 WHERE deleted = false
        """
    )
    r = cur.fetchone()
    print(f"summary total={r['total']} with_suffix={r['with_suf']} without_suffix={r['no_suf']}")
    cur.execute(
        """
        SELECT count(*) AS dup_groups FROM (
          SELECT name FROM ent_inspection_item_t1
          WHERE deleted = false
          GROUP BY name HAVING count(*) > 1
        ) t
        """
    )
    print(f"summary exact_dup_name_groups={cur.fetchone()['dup_groups']}")


def next_insp_codes(cur, n: int) -> List[str]:
    cur.execute(
        """
        SELECT code FROM ent_inspection_item_t1
        WHERE deleted = false AND code ~ '^INSP-[0-9]{7}$'
        """
    )
    used = {r["code"] for r in cur.fetchall()}
    cur.execute(
        """
        SELECT COALESCE(MAX(SUBSTRING(code FROM 6)::int), 9959000) AS mx
        FROM ent_inspection_item_t1
        WHERE code ~ '^INSP-[0-9]{7}$'
        """
    )
    start = int(cur.fetchone()["mx"]) + 1
    out: List[str] = []
    i = 0
    while len(out) < n:
        candidate = f"INSP-{start + i:07d}"
        i += 1
        if candidate not in used:
            used.add(candidate)
            out.append(candidate)
    return out


def find_or_create_named_item(
    cur,
    name: str,
    template_from: dict,
    code: Optional[str],
    dry_run: bool,
) -> int:
    cur.execute(
        """
        SELECT id FROM ent_inspection_item_t1
        WHERE deleted = false AND name = %s
        ORDER BY id LIMIT 1
        """,
        (name,),
    )
    row = cur.fetchone()
    if row:
        return int(row["id"])
    if dry_run:
        return -1
    cur.execute(
        """
        INSERT INTO ent_inspection_item_t1 (
          tenant_id, entity_type_code, model_id, name, code, status,
          creator, deleted, sort, method_template_id
        ) VALUES (
          %s, 'inspection_item', %s, %s, %s, 1,
          '17_dedupe_suffix', false, 0, %s
        ) RETURNING id
        """,
        (
            TENANT,
            template_from.get("model_id") or 549,
            name,
            code,
            template_from.get("method_template_id"),
        ),
    )
    return int(cur.fetchone()["id"])


def ensure_link(cur, entity_id: int, category_id: int, dry_run: bool) -> None:
    if entity_id <= 0 or dry_run:
        return
    cur.execute(
        """
        SELECT id, deleted FROM dynamic_entity_category_relation_t1
        WHERE tenant_id = %s AND entity_type_code = 'inspection_item'
          AND entity_id = %s AND category_id = %s
        """,
        (TENANT, entity_id, category_id),
    )
    row = cur.fetchone()
    if row:
        if row["deleted"]:
            cur.execute(
                """
                UPDATE dynamic_entity_category_relation_t1
                SET deleted = false, updater = '17_dedupe_suffix', update_time = NOW()
                WHERE id = %s
                """,
                (row["id"],),
            )
        return
    cur.execute(
        """
        INSERT INTO dynamic_entity_category_relation_t1 (
          entity_id, category_id, entity_type_code, sort,
          creator, deleted, tenant_id
        ) VALUES (%s, %s, 'inspection_item', 0, '17_dedupe_suffix', false, %s)
        """,
        (entity_id, category_id, TENANT),
    )


def split_multi_leaf_items(cur, dry_run: bool) -> Tuple[int, int]:
    """多设备叶子糊挂的短名项：按叶子拆成「内容（叶子名）」并软删原实体。"""
    cur.execute(
        """
        WITH leaf_links AS (
          SELECT e.id AS entity_id, e.name, e.model_id, e.method_template_id,
                 c.id AS leaf_id, c.name AS leaf_name
          FROM ent_inspection_item_t1 e
          JOIN dynamic_entity_category_relation_t1 r
            ON r.entity_id = e.id AND r.deleted = false
          JOIN dynamic_category c
            ON c.id = r.category_id AND c.deleted = false
           AND c.tenant_id = %s AND c.category_type_code = 'equipment'
          WHERE e.deleted = false AND e.name !~ '（.+）$'
            AND NOT EXISTS (
              SELECT 1 FROM dynamic_category ch
              WHERE ch.parent_id = c.id AND ch.deleted = false AND ch.tenant_id = %s
            )
        ),
        multi AS (
          SELECT entity_id FROM leaf_links GROUP BY entity_id HAVING count(*) > 1
        )
        SELECT ll.entity_id, ll.name, ll.model_id, ll.method_template_id,
               ll.leaf_id, ll.leaf_name
        FROM leaf_links ll
        JOIN multi m ON m.entity_id = ll.entity_id
        ORDER BY ll.entity_id, ll.leaf_id
        """,
        (TENANT, TENANT),
    )
    rows = cur.fetchall()
    if not rows:
        return 0, 0

    by_entity: Dict[int, List[dict]] = {}
    for r in rows:
        by_entity.setdefault(int(r["entity_id"]), []).append(dict(r))

    # 收集原实体上的检查分类挂接
    entity_ids = list(by_entity.keys())
    cur.execute(
        """
        SELECT entity_id, category_id
        FROM dynamic_entity_category_relation_t1 r
        JOIN dynamic_category c ON c.id = r.category_id AND c.deleted = false
        WHERE r.deleted = false AND r.entity_id = ANY(%s)
          AND c.category_type_code = 'inspection_item'
        """,
        (entity_ids,),
    )
    insp_links: Dict[int, List[int]] = {}
    for r in cur.fetchall():
        insp_links.setdefault(int(r["entity_id"]), []).append(int(r["category_id"]))

    need_codes = sum(len(v) for v in by_entity.values())
    codes = next_insp_codes(cur, need_codes)
    code_i = 0
    created = 0
    dropped = 0

    for eid, leaves in by_entity.items():
        base = leaves[0]["name"]
        tmpl = {
            "model_id": leaves[0]["model_id"],
            "method_template_id": leaves[0]["method_template_id"],
        }
        print(f"  split id={eid} {base!r} -> {len(leaves)} leaves")
        for leaf in leaves:
            new_name = with_suffix(base, leaf["leaf_name"])
            code = codes[code_i]
            code_i += 1
            new_id = find_or_create_named_item(cur, new_name, tmpl, code, dry_run)
            if new_id < 0:
                created += 1
            elif dry_run is False:
                # find_or_create returns existing or new; count only brand-new via name miss is hard
                pass
            ensure_link(cur, new_id, int(leaf["leaf_id"]), dry_run)
            for insp_cid in insp_links.get(eid, []):
                ensure_link(cur, new_id, insp_cid, dry_run)
            if new_id > 0 or dry_run:
                created += 1
        dropped += soft_delete_entities(cur, [eid], dry_run)
    return created, dropped


def drop_equipmentless_short_names(cur, dry_run: bool) -> int:
    cur.execute(
        """
        SELECT e.id, e.name
        FROM ent_inspection_item_t1 e
        WHERE e.deleted = false AND e.name !~ '（.+）$'
          AND NOT EXISTS (
            SELECT 1 FROM dynamic_entity_category_relation_t1 r
            JOIN dynamic_category c ON c.id = r.category_id AND c.deleted = false
             AND c.category_type_code = 'equipment'
            WHERE r.entity_id = e.id AND r.deleted = false
          )
        ORDER BY e.id
        """
    )
    rows = cur.fetchall()
    ids = [int(r["id"]) for r in rows]
    for r in rows:
        print(f"  drop orphan short id={r['id']} {r['name']!r}")
    return soft_delete_entities(cur, ids, dry_run)


def main() -> int:
    ap = argparse.ArgumentParser()
    ap.add_argument("--dry-run", action="store_true")
    args = ap.parse_args()
    dry = args.dry_run

    conn = psycopg2.connect(**PKG)
    conn.autocommit = False
    try:
        with conn.cursor(cursor_factory=DictCursor) as cur:
            print("=== before ===")
            summarize(cur)

            print("\n=== phase1 dedupe unsuffixed clones ===")
            removed = dedupe_unsuffixed_clones(cur, dry)
            print(f"soft_deleted={removed}")

            print("\n=== phase2 rename single equipment leaf ===")
            leaf_targets = load_one_leaf_targets(cur)
            n_leaf, d_leaf = rename_targets(cur, leaf_targets, dry, "leaf")
            print(f"renamed_leaf={n_leaf} dropped_stale={d_leaf} candidates={len(leaf_targets)}")

            print("\n=== phase3 rename single equipment frontier (no leaf) ===")
            frontier_targets = load_one_frontier_targets(cur)
            n_front, d_front = rename_targets(cur, frontier_targets, dry, "frontier")
            print(
                f"renamed_frontier={n_front} dropped_stale={d_front} "
                f"candidates={len(frontier_targets)}"
            )

            print("\n=== phase4 second-pass dedupe any exact name clones ===")
            cur.execute(
                """
                SELECT name, array_agg(id ORDER BY id) AS ids
                FROM ent_inspection_item_t1
                WHERE deleted = false
                GROUP BY name
                HAVING count(*) > 1
                """
            )
            removed2 = 0
            for row in cur.fetchall():
                ids = list(row["ids"])
                keep, drop = ids[0], ids[1:]
                print(f"  dedupe-any name={row['name']!r} keep={keep} drop={drop}")
                removed2 += soft_delete_entities(cur, drop, dry)
            print(f"soft_deleted_pass2={removed2}")

            print("\n=== phase5 split multi equipment-leaf short names ===")
            created, dropped_split = split_multi_leaf_items(cur, dry)
            print(f"split_created_or_reused={created} soft_deleted_originals={dropped_split}")

            print("\n=== phase6 drop equipment-less short names ===")
            orphans = drop_equipmentless_short_names(cur, dry)
            print(f"soft_deleted_orphans={orphans}")

            print("\n=== after ===")
            summarize(cur)

            if dry:
                conn.rollback()
                print("\n[dry-run] rolled back")
            else:
                conn.commit()
                print("\n[committed]")
    except Exception:
        conn.rollback()
        raise
    finally:
        conn.close()
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
