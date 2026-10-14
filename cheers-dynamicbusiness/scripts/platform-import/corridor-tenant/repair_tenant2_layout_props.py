# -*- coding: utf-8 -*-
"""
补回租户 2 数据页栏位上被写成空的展示配置。

复制目录时，栏位 props_id 只认 tenant_id=1 的配置。
租户 1 很多栏位指向 tenant_id=0 的配置，复制后编号变成空，数据页显示缺少展示配置。

本脚本只补 creator=copy-from-tenant-1 的栏位。
不改租户 2 原有的管廊设施、分区、运营区域、构筑物。
已经有编号的栏位不动。
"""

from __future__ import annotations

import psycopg2
from psycopg2.extras import Json

SRC = 1
DST = 2
SCHEMA = "dynamicbusiness"
PROPS = "platformresource"


def main() -> None:
    conn = psycopg2.connect(
        host="127.0.0.1",
        dbname="sinopec",
        user="postgres",
        password="Coolhomer",
    )
    conn.autocommit = False
    cur = conn.cursor()
    cur.execute("SET statement_timeout = '15min'")
    cur.execute("SET lock_timeout = '30s'")
    try:
        repair(cur)
        conn.commit()
        print("COMMITTED")
    except Exception:
        conn.rollback()
        print("ROLLED BACK")
        raise
    finally:
        cur.close()
        conn.close()


def repair(cur) -> None:
    pairs = load_missing_pairs(cur)
    print("layout_rows_to_fill", len(pairs))
    if not pairs:
        print("nothing_to_fill")
    else:
        by_code: dict[str, int] = {}
        for _row_id, _props_id, code, _kind in pairs:
            by_code[code] = by_code.get(code, 0) + 1
        for code in sorted(by_code):
            print("fill", code, by_code[code])
        id_map = copy_props(cur, sorted({props_id for _row, props_id, _code, _kind in pairs}))
        updated = 0
        for row_id, props_id, _code, _kind in pairs:
            cur.execute(
                f"""
                UPDATE {SCHEMA}.dm_data_tab_layout
                SET props_id = %s, updater = 'copy-from-tenant-1'
                WHERE id = %s
                  AND tenant_id = %s
                  AND creator = 'copy-from-tenant-1'
                  AND (props_id IS NULL OR props_id <= 0)
                """,
                (id_map[props_id], row_id, DST),
            )
            updated += cur.rowcount
        print("layout_rows_updated", updated)
        if updated != len(pairs):
            raise RuntimeError(f"应更新 {len(pairs)} 行，实际 {updated}")

    restored = restore_wiped_template_ids(cur)
    print("template_ids_restored", restored)
    report_remaining(cur)


def load_missing_pairs(cur) -> list[tuple[int, int, str, str]]:
    cur.execute(
        f"""
        SELECT t2.id, array_agg(DISTINCT t1.props_id) AS props_ids,
               t2.entity_type_code, t2.column_kind
        FROM {SCHEMA}.dm_data_tab_layout t2
        JOIN {SCHEMA}.dynamic_entity_type et2
          ON et2.tenant_id = %s
         AND et2.deleted = false
         AND et2.code = t2.entity_type_code
         AND t2.layout_id IN (et2.data_layout_id, et2.model_layout_id)
        JOIN {SCHEMA}.dynamic_entity_type et1
          ON et1.tenant_id = %s
         AND et1.deleted = false
         AND et1.code = et2.code
        JOIN {SCHEMA}.dm_data_tab_layout t1
          ON t1.tenant_id = %s
         AND t1.deleted = false
         AND t1.entity_type_code = t2.entity_type_code
         AND t1.column_kind IS NOT DISTINCT FROM t2.column_kind
         AND t1.tab_id IS NOT DISTINCT FROM t2.tab_id
         AND (
              (t2.layout_id = et2.data_layout_id AND t1.layout_id = et1.data_layout_id)
           OR (
                et2.model_layout_id IS NOT NULL
                AND t2.layout_id = et2.model_layout_id
                AND t1.layout_id = et1.model_layout_id
              )
         )
        WHERE t2.tenant_id = %s
          AND t2.deleted = false
          AND t2.creator = 'copy-from-tenant-1'
          AND (t2.props_id IS NULL OR t2.props_id <= 0)
          AND t1.props_id IS NOT NULL
          AND t1.props_id > 0
        GROUP BY t2.id, t2.entity_type_code, t2.column_kind
        ORDER BY t2.entity_type_code, t2.id
        """,
        (DST, SRC, SRC, DST),
    )
    pairs: list[tuple[int, int, str, str]] = []
    for row_id, props_ids, code, kind in cur.fetchall():
        if len(props_ids) != 1:
            raise RuntimeError(f"栏位 {row_id}（{code}/{kind}）对上多个展示配置 {props_ids}")
        pairs.append((int(row_id), int(props_ids[0]), code, kind or ""))
    return pairs


def copy_props(cur, source_ids: list[int]) -> dict[int, int]:
    cur.execute(
        """
        SELECT column_name, data_type
        FROM information_schema.columns
        WHERE table_schema = %s AND table_name = 'pr_component_props'
        ORDER BY ordinal_position
        """,
        (PROPS,),
    )
    columns = cur.fetchall()
    col_types = {name: data_type for name, data_type in columns}
    names = [name for name, _ in columns]
    collist = ", ".join(names)
    cur.execute(
        f"""
        SELECT {collist}
        FROM {PROPS}.pr_component_props
        WHERE id = ANY(%s) AND deleted = false
        ORDER BY id
        """,
        (source_ids,),
    )
    rows = [dict(zip(names, raw)) for raw in cur.fetchall()]
    found = {int(row["id"]) for row in rows}
    missing = [item for item in source_ids if item not in found]
    if missing:
        raise RuntimeError(f"源展示配置不存在: {missing[:10]}")

    id_map: dict[int, int] = {}
    copied_rows: list[dict] = []
    for row in rows:
        new_id = next_props_id(cur)
        payload = dict(row)
        payload["id"] = new_id
        payload["tenant_id"] = DST
        payload["creator"] = "copy-from-tenant-1"
        payload["updater"] = "copy-from-tenant-1"
        insert_props(cur, payload, names, col_types)
        id_map[int(row["id"])] = new_id
        copied_rows.append(row)

    remapped = 0
    for row in copied_rows:
        template_id = row.get("template_id")
        if template_id is None:
            continue
        mapped_template = id_map.get(int(template_id))
        if mapped_template is None:
            continue
        cur.execute(
            f"UPDATE {PROPS}.pr_component_props SET template_id = %s WHERE id = %s",
            (mapped_template, id_map[int(row["id"])]),
        )
        remapped += 1
    print("props_copied", len(id_map), "template_remapped_in_batch", remapped)
    return id_map


def restore_wiped_template_ids(cur) -> int:
    """
    上一轮复制把 tenant_id=1 的配置带过来时，模板编号被清空。
    模板行本身不按租户过滤，读得见。
    这里把模板编号指回源配置上的模板。只在能唯一对上源行时改。
    """
    cur.execute(
        f"""
        WITH matches AS (
            SELECT p2.id AS copy_id, p1.template_id
            FROM {PROPS}.pr_component_props p2
            JOIN {PROPS}.pr_component_props p1
              ON p1.tenant_id = %s
             AND p1.deleted = false
             AND p1.id <> p2.id
             AND p1.component_code IS NOT DISTINCT FROM p2.component_code
             AND p1.name IS NOT DISTINCT FROM p2.name
             AND p1.data_source IS NOT DISTINCT FROM p2.data_source
             AND p1.props IS NOT DISTINCT FROM p2.props
             AND p1.props_override IS NOT DISTINCT FROM p2.props_override
             AND p1.is_template IS NOT DISTINCT FROM p2.is_template
            WHERE p2.tenant_id = %s
              AND p2.deleted = false
              AND p2.creator = 'copy-from-tenant-1'
              AND p2.template_id IS NULL
              AND p1.template_id IS NOT NULL
        ),
        unique_match AS (
            SELECT copy_id, MIN(template_id) AS template_id
            FROM matches
            GROUP BY copy_id
            HAVING COUNT(DISTINCT template_id) = 1
        )
        UPDATE {PROPS}.pr_component_props p
        SET template_id = unique_match.template_id,
            updater = 'copy-from-tenant-1'
        FROM unique_match
        WHERE p.id = unique_match.copy_id
          AND p.template_id IS NULL
        """,
        (SRC, DST),
    )
    return cur.rowcount


def report_remaining(cur) -> None:
    cur.execute(
        f"""
        SELECT t2.entity_type_code, t2.column_kind, count(*)
        FROM {SCHEMA}.dm_data_tab_layout t2
        JOIN {SCHEMA}.dynamic_entity_type et2
          ON et2.tenant_id = %s
         AND et2.deleted = false
         AND et2.code = t2.entity_type_code
         AND t2.layout_id = et2.data_layout_id
        WHERE t2.tenant_id = %s
          AND t2.deleted = false
          AND t2.creator = 'copy-from-tenant-1'
          AND (t2.props_id IS NULL OR t2.props_id <= 0)
        GROUP BY t2.entity_type_code, t2.column_kind
        ORDER BY t2.entity_type_code, t2.column_kind
        """,
        (DST, DST),
    )
    rows = cur.fetchall()
    print("still_missing_on_data_layout", len(rows))
    for code, kind, count in rows:
        print("still", code, kind, count)

    cur.execute(
        f"""
        SELECT column_kind, props_id
        FROM {SCHEMA}.dm_data_tab_layout
        WHERE tenant_id = %s
          AND deleted = false
          AND entity_type_code = 'billing'
          AND layout_id = (
              SELECT data_layout_id
              FROM {SCHEMA}.dynamic_entity_type
              WHERE tenant_id = %s AND deleted = false AND code = 'billing'
          )
        ORDER BY id
        """,
        (DST, DST),
    )
    print("billing_data_layout", cur.fetchall())


def next_props_id(cur) -> int:
    cur.execute(
        "SELECT pg_get_serial_sequence('platformresource.pr_component_props', 'id')"
    )
    seq = cur.fetchone()[0]
    if not seq:
        raise RuntimeError("pr_component_props 没有 id 序列")
    cur.execute("SELECT nextval(%s)", (seq,))
    return int(cur.fetchone()[0])


def insert_props(cur, row: dict, names: list[str], col_types: dict) -> None:
    values = []
    for name in names:
        value = row[name]
        if col_types[name] in ("jsonb", "json") and isinstance(value, (dict, list)):
            value = Json(value)
        values.append(value)
    collist = ", ".join(names)
    placeholders = ", ".join(["%s"] * len(names))
    cur.execute(
        f"INSERT INTO {PROPS}.pr_component_props ({collist}) VALUES ({placeholders})",
        values,
    )


if __name__ == "__main__":
    main()
