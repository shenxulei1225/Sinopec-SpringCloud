#!/usr/bin/env python3
"""
继续清理：布局 category_column、物理列映射、CRUD 表单、关系/索引中的 FLD-BASE-*。
各段独立 commit；唯一键冲突时删除旧 FLD-BASE 行（短码行已存在）。
"""

from __future__ import annotations

import json
import re
import sys

import psycopg2
from psycopg2.extras import Json

DSN = "host=127.0.0.1 dbname=sinopec user=postgres password=Coolhomer"
FLD_RE = re.compile(r"^FLD-BASE-([A-Za-z0-9_]+)-(.+)$", re.I)

REF_MAP = {
    ("equipment", "zone"): "zone_id",
    ("equipment", "region"): "region_ids",
    ("equipment", "facility"): "facility_id",
    ("facility", "region"): "region_id",
    ("zone", "facility"): "facility_id",
    ("structure", "facility"): "facility_id",
    ("structure", "zone"): "zone_id",
    ("customer", "equipment"): "equipment_ids",
    ("equipment", "health_score"): "health_score_id",
    ("equipment", "last_inspection"): "last_inspection_id",
    ("equipment", "last_maintenance"): "last_maintenance_id",
    ("equipment", "next_inspection"): "next_inspection_id",
    ("equipment", "next_maintenance"): "next_maintenance_id",
    ("equipment", "operation_status"): "operation_status_id",
}


def map_token(token: str) -> str | None:
    m = FLD_RE.match(token.strip())
    if not m:
        return token
    entity = m.group(1).lower()
    tail = m.group(2)
    upper = tail.upper()
    if upper.startswith("REF_"):
        ref = tail[4:].lower()
        return REF_MAP.get((entity, ref)) or f"{ref}_id"
    if upper.startswith("REL_"):
        rel = tail[4:].lower()
        return REF_MAP.get((entity, rel)) or f"{rel}_ids"
    return tail.lower()


def rewrite_mapping(obj: dict) -> dict:
    out = {}
    for k, v in obj.items():
        nk = map_token(k) if isinstance(k, str) and k.upper().startswith("FLD-BASE-") else k
        if nk is None:
            continue
        if isinstance(v, dict) and "column" in v:
            col = v.get("column")
            if isinstance(col, str) and col.lower().startswith("fld_base_"):
                v = {**v, "column": nk}
        if nk in out and isinstance(k, str) and k.upper().startswith("FLD-BASE-"):
            continue
        out[nk] = v
    return out


def rewrite_json(obj, path=""):
    if isinstance(obj, dict):
        if obj and all(isinstance(v, dict) and ("column" in v or "type" in v) for v in obj.values()):
            return rewrite_mapping(obj)
        out = {}
        for k, v in obj.items():
            if isinstance(k, str) and k.upper().startswith("FLD-BASE-"):
                nk = map_token(k)
                if nk is None:
                    continue
                k = nk
            if k in (
                "fieldKey",
                "fieldCode",
                "field_code",
                "refFieldCode",
                "code",
                "target_code",
            ) and isinstance(v, str) and v.upper().startswith("FLD-BASE-"):
                mapped = map_token(v)
                if mapped is None:
                    continue
                out[k] = mapped
                continue
            out[k] = rewrite_json(v, path)
        return out
    if isinstance(obj, list):
        out = []
        for i in obj:
            if isinstance(i, str) and i.upper().startswith("FLD-BASE-"):
                m = map_token(i)
                if m is not None:
                    out.append(m)
                continue
            cleaned = rewrite_json(i, path)
            if cleaned is not None:
                out.append(cleaned)
        return out
    return obj


def main() -> int:
    conn = psycopg2.connect(DSN)
    cur = conn.cursor()

    def reset():
        cur.execute("SET search_path TO dynamicbusiness, platformresource")

    reset()

    # 1) data tab layout
    cur.execute(
        """
        SELECT id, category_column FROM dm_data_tab_layout
        WHERE category_column::text ILIKE '%FLD-BASE%'
        """
    )
    for lid, col in cur.fetchall():
        obj = col if isinstance(col, dict) else json.loads(col)
        new_obj = rewrite_json(obj)
        cur.execute(
            "UPDATE dm_data_tab_layout SET category_column = %s, update_time = NOW() WHERE id = %s",
            (Json(new_obj), lid),
        )
        print("layout", lid, "refFieldCode ->", new_obj.get("refFieldCode"))
    conn.commit()
    reset()

    # 2) entity_type_config pcm
    cur.execute(
        """
        SELECT id, physical_column_mapping FROM dynamic_entity_type_config
        WHERE physical_column_mapping::text ILIKE '%FLD-BASE%'
        """
    )
    for cid, pcm in cur.fetchall():
        obj = pcm if isinstance(pcm, dict) else json.loads(pcm)
        new_obj = rewrite_mapping(obj)
        cur.execute(
            """
            UPDATE dynamic_entity_type_config
            SET physical_column_mapping = %s, update_time = NOW()
            WHERE id = %s
            """,
            (Json(new_obj), cid),
        )
        print("entity_type_config", cid)
    conn.commit()
    reset()

    # 3) entity_type pcm
    cur.execute(
        """
        SELECT id, physical_column_mapping FROM dynamic_entity_type
        WHERE physical_column_mapping::text ILIKE '%FLD-BASE%'
        """
    )
    for eid, pcm in cur.fetchall():
        obj = pcm if isinstance(pcm, dict) else json.loads(pcm)
        new_obj = rewrite_mapping(obj)
        cur.execute(
            """
            UPDATE dynamic_entity_type
            SET physical_column_mapping = %s, update_time = NOW()
            WHERE id = %s
            """,
            (Json(new_obj), eid),
        )
        print("entity_type", eid)
    conn.commit()
    reset()

    # 4) crud forms
    cur.execute(
        """
        SELECT id, crud_form_fields FROM model_crud_form_definition
        WHERE crud_form_fields::text ILIKE '%FLD-BASE%'
        """
    )
    n_form = 0
    for fid, fields in cur.fetchall():
        obj = fields if isinstance(fields, (dict, list)) else json.loads(fields)
        new_obj = rewrite_json(obj, "form")
        cur.execute(
            """
            UPDATE model_crud_form_definition
            SET crud_form_fields = %s, update_time = NOW()
            WHERE id = %s
            """,
            (Json(new_obj), fid),
        )
        n_form += 1
    print("crud_form updated", n_form)
    conn.commit()
    reset()

    # 5) code columns — 先删会撞唯一键的旧行，再改名
    for table, col in (
        ("dynamic_entity_relation_t1", "field_code"),
        ("dynamic_entity_field_index_t1", "field_code"),
        ("dynamic_group_relation", "target_code"),
    ):
        cur.execute(
            f'SELECT DISTINCT "{col}" FROM "{table}" WHERE "{col}" ILIKE %s',
            ("FLD-BASE-%",),
        )
        for (old,) in cur.fetchall():
            new = map_token(old)
            if not new or new == old:
                continue
            # 用 savepoint，冲突则删旧
            cur.execute("SAVEPOINT fld_map")
            try:
                cur.execute(
                    f'UPDATE "{table}" SET "{col}" = %s WHERE "{col}" = %s',
                    (new, old),
                )
                cur.execute("RELEASE SAVEPOINT fld_map")
                print(f"{table}.{col}: {old} -> {new} ({cur.rowcount})")
            except psycopg2.errors.UniqueViolation:
                cur.execute("ROLLBACK TO SAVEPOINT fld_map")
                cur.execute(f'DELETE FROM "{table}" WHERE "{col}" = %s', (old,))
                print(
                    f"{table}.{col}: deleted legacy {old} ({cur.rowcount}); "
                    f"{new} already exists"
                )
        conn.commit()
        reset()

    # 6) custom_fields
    cur.execute(
        """
        SELECT table_name FROM information_schema.columns
        WHERE table_schema='dynamicbusiness' AND column_name='custom_fields'
          AND table_name LIKE 'ent_%'
        """
    )
    for (tbl,) in cur.fetchall():
        cur.execute(
            f'SELECT id, custom_fields FROM "{tbl}" WHERE custom_fields::text ILIKE %s',
            ("%FLD-BASE%",),
        )
        rows = cur.fetchall()
        if not rows:
            continue
        for rid, cf in rows:
            obj = cf if isinstance(cf, dict) else json.loads(cf)
            new_obj = {}
            changed = False
            for k, v in obj.items():
                if isinstance(k, str) and k.upper().startswith("FLD-BASE-"):
                    nk = map_token(k)
                    changed = True
                    if nk and nk not in new_obj:
                        new_obj[nk] = v
                else:
                    new_obj[k] = v
            if changed:
                cur.execute(
                    f'UPDATE "{tbl}" SET custom_fields = %s WHERE id = %s',
                    (Json(new_obj), rid),
                )
        print(f"{tbl} custom_fields remapped rows={len(rows)}")
        conn.commit()
        reset()

    checks = [
        ("dm_data_tab_layout", "category_column"),
        ("dynamic_entity_type", "physical_column_mapping"),
        ("dynamic_entity_type_config", "physical_column_mapping"),
        ("model_crud_form_definition", "crud_form_fields"),
        ("pr_component_props", "props"),
        ("capability_component_projection", "component_interface"),
        ("dynamic_entity_relation_t1", "field_code"),
        ("dynamic_entity_field_index_t1", "field_code"),
        ("dynamic_group_relation", "target_code"),
    ]
    for tbl, col in checks:
        try:
            cur.execute(
                f'SELECT count(*) FROM "{tbl}" WHERE "{col}"::text ILIKE %s',
                ("%FLD-BASE%",),
            )
            print(f"remain {tbl}.{col}:", cur.fetchone()[0])
        except Exception as e:
            conn.rollback()
            reset()
            print(f"remain {tbl}.{col}: err {e}")

    cur.execute(
        """
        SELECT count(*) FILTER (WHERE deleted), count(*) FILTER (WHERE NOT deleted)
        FROM dynamic_field WHERE code ILIKE 'FLD-BASE-%'
        """
    )
    print("dynamic_field FLD-BASE deleted/live:", cur.fetchone())
    conn.close()
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
