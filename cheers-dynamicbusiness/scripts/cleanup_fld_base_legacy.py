#!/usr/bin/env python3
"""
清理本地库中残留的 FLD-BASE-* 旧字段编码。

范围：
- platformresource.pr_component_props（含 displayContent / filter 选项等）
- dynamicbusiness.capability_component_projection.component_interface

映射：FLD-BASE-{type}-{tail} → 短码（REF_* / REL_* 按现行 *_id / *_ids）；
无法映射到现网字段库编码的条目直接删除，不在读路径编造。

用法（本机）：
  python scripts/cleanup_fld_base_legacy.py
"""

from __future__ import annotations

import json
import re
import sys

try:
    import psycopg2
    from psycopg2.extras import Json
except ImportError:
    print("需要 psycopg2：pip install psycopg2-binary", file=sys.stderr)
    sys.exit(1)

DSN = "host=127.0.0.1 dbname=sinopec user=postgres password=Coolhomer"

FLD_RE = re.compile(r"^FLD-BASE-([A-Za-z0-9_]+)-(.+)$", re.I)
BUILTIN = {"name", "code", "status", "id"}

REF_MAP = {
    ("equipment", "zone"): "zone_id",
    ("equipment", "region"): "region_ids",
    ("equipment", "facility"): "facility_id",
    ("equipment", "health_score"): "health_score_id",
    ("equipment", "last_inspection"): "last_inspection_id",
    ("equipment", "last_maintenance"): "last_maintenance_id",
    ("equipment", "next_inspection"): "next_inspection_id",
    ("equipment", "next_maintenance"): "next_maintenance_id",
    ("equipment", "operation_status"): "operation_status_id",
    ("facility", "region"): "region_id",
    ("zone", "facility"): "facility_id",
    ("structure", "facility"): "facility_id",
    ("customer", "equipment"): "equipment_ids",
}


def load_active_codes(cur) -> set[str]:
    cur.execute(
        """
        SET search_path TO dynamicbusiness, platformresource;
        SELECT lower(code) FROM dynamic_field
        WHERE deleted = false AND code IS NOT NULL AND btrim(code) <> ''
        """
    )
    return {r[0] for r in cur.fetchall()}


def map_token(token: str, active_codes: set[str]) -> tuple[str | None, str]:
    m = FLD_RE.match(token.strip())
    if not m:
        return token, "keep"
    entity = m.group(1).lower()
    tail = m.group(2)
    upper = tail.upper()
    if upper.startswith("REF_"):
        ref = tail[4:].lower()
        mapped = REF_MAP.get((entity, ref)) or f"{ref}_id"
    elif upper.startswith("REL_"):
        rel = tail[4:].lower()
        mapped = REF_MAP.get((entity, rel)) or f"{rel}_ids"
    else:
        mapped = tail.lower()

    if mapped in BUILTIN or mapped.lower() in active_codes:
        return mapped.lower() if mapped.lower() in active_codes else mapped, "mapped"
    return None, "drop"


def transform(obj, stats: dict, active_codes: set[str], path: str = ""):
    if isinstance(obj, dict):
        out: dict = {}
        for k, v in obj.items():
            if (
                k in ("fieldKey", "fieldCode", "field_code", "id", "key")
                and isinstance(v, str)
                and v.upper().startswith("FLD-BASE-")
            ):
                mapped, action = map_token(v, active_codes)
                stats[action] = stats.get(action, 0) + 1
                if action == "drop":
                    stats.setdefault("dropped_tokens", [])
                    if v not in stats["dropped_tokens"]:
                        stats["dropped_tokens"].append(v)
                    # 保留其它键时字段对象会在 list 层被丢掉
                    out[k] = v
                    continue
                out[k] = mapped
                if isinstance(obj.get("label"), str) and obj["label"] == v:
                    out["label"] = mapped
                continue
            out[k] = transform(v, stats, active_codes, f"{path}.{k}")
        return out

    if isinstance(obj, list):
        new_list = []
        seen: set[str] = set()
        for item in obj:
            if isinstance(item, str) and item.upper().startswith("FLD-BASE-"):
                mapped, action = map_token(item, active_codes)
                stats[action] = stats.get(action, 0) + 1
                if action == "drop":
                    stats.setdefault("dropped_tokens", [])
                    if item not in stats["dropped_tokens"]:
                        stats["dropped_tokens"].append(item)
                    continue
                item = mapped
            else:
                item = transform(item, stats, active_codes, f"{path}[]")

            if isinstance(item, dict):
                fk = (
                    item.get("fieldKey")
                    or item.get("fieldCode")
                    or item.get("field_code")
                    or item.get("key")
                )
                if isinstance(fk, str) and fk.upper().startswith("FLD-BASE-"):
                    mapped, action = map_token(fk, active_codes)
                    stats[action] = stats.get(action, 0) + 1
                    if action == "drop":
                        stats.setdefault("dropped_tokens", [])
                        if fk not in stats["dropped_tokens"]:
                            stats["dropped_tokens"].append(fk)
                        continue
                    for key in ("fieldKey", "fieldCode", "field_code", "key", "id"):
                        if key in item and isinstance(item[key], str) and item[key] == fk:
                            item[key] = mapped
                    if item.get("label") == fk:
                        item["label"] = mapped
                    fk = mapped
                if isinstance(fk, str):
                    dedupe = fk.lower()
                    if "field" in path.lower() or "display" in path.lower():
                        if dedupe in seen:
                            stats["deduped"] = stats.get("deduped", 0) + 1
                            continue
                        seen.add(dedupe)
                new_list.append(item)
            elif isinstance(item, str):
                if ("displayContent" in path or "SelectedOptions" in path) and item.lower() in seen:
                    stats["deduped"] = stats.get("deduped", 0) + 1
                    continue
                if "displayContent" in path or "SelectedOptions" in path:
                    seen.add(item.lower())
                new_list.append(item)
            else:
                new_list.append(item)
        return new_list

    return obj


def purge_remaining(obj):
    """最后一道：删掉仍带 FLD-BASE- 的字符串或字段对象。"""
    if isinstance(obj, dict):
        fk = obj.get("fieldKey") or obj.get("fieldCode") or obj.get("field_code") or ""
        if isinstance(fk, str) and fk.upper().startswith("FLD-BASE-"):
            return None
        out = {}
        for k, v in obj.items():
            if isinstance(v, str) and v.upper().startswith("FLD-BASE-"):
                continue
            cleaned = purge_remaining(v)
            if cleaned is not None:
                out[k] = cleaned
        return out
    if isinstance(obj, list):
        out = []
        for i in obj:
            if isinstance(i, str) and i.upper().startswith("FLD-BASE-"):
                continue
            cleaned = purge_remaining(i)
            if cleaned is not None:
                out.append(cleaned)
        return out
    return obj


def main() -> int:
    conn = psycopg2.connect(DSN)
    cur = conn.cursor()
    active_codes = load_active_codes(cur)
    print(f"active field codes: {len(active_codes)}")

    cur.execute(
        """
        SELECT id, props, props_override FROM pr_component_props
        WHERE props::text ILIKE '%FLD-BASE%'
           OR COALESCE(props_override, '')::text ILIKE '%FLD-BASE%'
        """
    )
    props_rows = cur.fetchall()
    print(f"dirty props rows: {len(props_rows)}")
    for pid, props, override in props_rows:
        stats: dict = {}
        p = json.loads(props) if isinstance(props, str) else props
        new_p = purge_remaining(transform(p, stats, active_codes, "props"))
        new_o = override
        if override:
            o = json.loads(override) if isinstance(override, str) else override
            new_o = purge_remaining(transform(o, stats, active_codes, "override"))
        cur.execute(
            """
            UPDATE pr_component_props
            SET props = %s,
                props_override = %s,
                update_time = NOW()
            WHERE id = %s
            """,
            (
                Json(new_p),
                Json(new_o) if new_o is not None else None,
                pid,
            ),
        )
        print(f"  props#{pid}: {stats}")

    cur.execute(
        """
        SELECT id, entity_type_code, component_code, component_interface
        FROM capability_component_projection
        WHERE deleted = false AND component_interface::text ILIKE '%FLD-BASE%'
        """
    )
    cap_rows = cur.fetchall()
    print(f"dirty capability projections: {len(cap_rows)}")
    for cid, etc, cc, iface in cap_rows:
        stats = {}
        obj = iface if isinstance(iface, dict) else json.loads(iface)
        new_obj = purge_remaining(transform(obj, stats, active_codes, "cap"))
        cur.execute(
            """
            UPDATE capability_component_projection
            SET component_interface = %s, update_time = NOW()
            WHERE id = %s
            """,
            (Json(new_obj), cid),
        )
        print(f"  cap#{cid} {etc}/{cc}: mapped={stats.get('mapped',0)} drop={stats.get('drop',0)} dedupe={stats.get('deduped',0)}")

    conn.commit()

    cur.execute(
        """
        SELECT count(*) FROM pr_component_props
        WHERE props::text ILIKE '%FLD-BASE%'
           OR COALESCE(props_override, '')::text ILIKE '%FLD-BASE%'
        """
    )
    print("props remaining:", cur.fetchone()[0])
    cur.execute(
        """
        SELECT count(*) FROM capability_component_projection
        WHERE deleted = false AND component_interface::text ILIKE '%FLD-BASE%'
        """
    )
    print("capability remaining:", cur.fetchone()[0])
    cur.execute(
        """
        SELECT id, (props::jsonb)->'displayContent'
        FROM pr_component_props WHERE id IN (8599, 8750, 8755)
        """
    )
    print("displayContent after:", cur.fetchall())
    conn.close()
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
