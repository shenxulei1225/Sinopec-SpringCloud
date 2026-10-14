# -*- coding: utf-8 -*-
"""
打开租户 2 模型管理页上被关掉的型号栏。

分区、管廊设施、运营区域在数据页上是「分类即对象」，数据页不放型号栏。
模型管理是另一份布局。生成时误套了数据页规则，型号栏被关掉，已有模型看不见。

本脚本只改目录自己的 model_layout_id。不改 data_layout_id。
型号栏已启用且已有展示配置的不动。
"""

from __future__ import annotations

import json
from datetime import datetime

import psycopg2
from psycopg2.extras import Json

DST = 2
SCHEMA = "dynamicbusiness"
PROPS = "platformresource"
SAMPLE_PROPS_ID = 16855


def main() -> None:
    conn = psycopg2.connect(
        host="127.0.0.1",
        dbname="sinopec",
        user="postgres",
        password="Coolhomer",
    )
    conn.autocommit = False
    cur = conn.cursor()
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
    cur.execute(
        f"""
        SELECT l.id, l.entity_type_code, l.column_meta, t.name
        FROM {SCHEMA}.dm_data_tab_layout l
        JOIN {SCHEMA}.dynamic_entity_type t
          ON t.tenant_id = l.tenant_id
         AND t.deleted = false
         AND t.code = l.entity_type_code
         AND t.model_layout_id = l.layout_id
         AND t.data_layout_id IS DISTINCT FROM t.model_layout_id
        WHERE l.tenant_id = %s
          AND l.deleted = false
          AND l.column_kind = 'MODEL'
          AND l.enabled = false
          AND (l.props_id IS NULL OR l.props_id <= 0)
        ORDER BY l.entity_type_code, l.id
        """,
        (DST,),
    )
    rows = cur.fetchall()
    print("model_columns_to_enable", len(rows))
    if not rows:
        return
    sample = load_sample_props(cur)
    for row_id, code, meta, type_name in rows:
        label = type_name or code
        if isinstance(meta, dict) and meta.get("label"):
            label = str(meta["label"])
        props_id = insert_model_list_props(cur, sample, label, code)
        cur.execute(
            f"""
            UPDATE {SCHEMA}.dm_data_tab_layout
            SET enabled = true, props_id = %s, updater = 'repair-model-column'
            WHERE id = %s
              AND tenant_id = %s
              AND column_kind = 'MODEL'
              AND enabled = false
            """,
            (props_id, row_id, DST),
        )
        if cur.rowcount != 1:
            raise RuntimeError(f"型号栏 {row_id} 没有更新")
        print("enabled", code, row_id, props_id)

    cur.execute(
        f"""
        SELECT t.code, l.column_kind, l.enabled, l.props_id
        FROM {SCHEMA}.dynamic_entity_type t
        JOIN {SCHEMA}.dm_data_tab_layout l
          ON l.tenant_id = t.tenant_id
         AND l.deleted = false
         AND l.layout_id = t.model_layout_id
         AND l.column_kind = 'MODEL'
        WHERE t.tenant_id = %s
          AND t.deleted = false
          AND t.code IN ('zone', 'facility', 'region')
        ORDER BY t.code
        """,
        (DST,),
    )
    print("model_layout_model_column", cur.fetchall())
    cur.execute(
        f"""
        SELECT t.code, l.column_kind, l.enabled
        FROM {SCHEMA}.dynamic_entity_type t
        JOIN {SCHEMA}.dm_data_tab_layout l
          ON l.tenant_id = t.tenant_id
         AND l.deleted = false
         AND l.layout_id = t.data_layout_id
         AND l.column_kind = 'MODEL'
        WHERE t.tenant_id = %s
          AND t.deleted = false
          AND t.code IN ('zone', 'facility', 'region')
        """,
        (DST,),
    )
    print("data_layout_model_column", cur.fetchall())


def load_sample_props(cur) -> dict:
    cur.execute(
        """
        SELECT column_name
        FROM information_schema.columns
        WHERE table_schema = %s AND table_name = 'pr_component_props'
        ORDER BY ordinal_position
        """,
        (PROPS,),
    )
    names = [row[0] for row in cur.fetchall()]
    collist = ", ".join(names)
    cur.execute(
        f"SELECT {collist} FROM {PROPS}.pr_component_props WHERE id = %s",
        (SAMPLE_PROPS_ID,),
    )
    raw = cur.fetchone()
    if raw is None:
        raise RuntimeError("找不到型号列表展示配置样本")
    return dict(zip(names, raw))


def insert_model_list_props(cur, sample: dict, label: str, code: str) -> int:
    cur.execute("SELECT pg_get_serial_sequence('platformresource.pr_component_props', 'id')")
    seq = cur.fetchone()[0]
    cur.execute("SELECT nextval(%s)", (seq,))
    new_id = int(cur.fetchone()[0])
    payload = dict(sample)
    payload["id"] = new_id
    payload["tenant_id"] = DST
    payload["name"] = f"{label}-{code}"
    payload["creator"] = "repair-model-column"
    payload["updater"] = "repair-model-column"
    payload["create_time"] = datetime.now()
    payload["update_time"] = datetime.now()
    payload["deleted"] = False
    source = {"businessCategory": "dynamic", "entityTypeCode": code, "dataKind": "model"}
    payload["data_source"] = Json(source)
    if isinstance(payload.get("props"), str):
        payload["props"] = Json(json.loads(payload["props"]))
    elif isinstance(payload.get("props"), dict):
        payload["props"] = Json(payload["props"])
    names = list(payload.keys())
    values = [payload[name] for name in names]
    collist = ", ".join(names)
    placeholders = ", ".join(["%s"] * len(names))
    cur.execute(
        f"INSERT INTO {PROPS}.pr_component_props ({collist}) VALUES ({placeholders})",
        values,
    )
    return new_id


if __name__ == "__main__":
    main()
