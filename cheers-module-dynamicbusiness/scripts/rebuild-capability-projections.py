#!/usr/bin/env python3
"""One-off: rebuild capability_component_projection rows from model field metadata."""

from __future__ import annotations

import json
import os
import sys
from collections import OrderedDict

try:
    import psycopg2
    import psycopg2.extras
except ImportError:
    print("Install psycopg2: pip install psycopg2-binary", file=sys.stderr)
    sys.exit(1)

COMPONENT_CODES = ("list", "tree", "table", "card")
READ_ENDPOINT = "/dynamicbusiness/business/entities/page-by-filters"


def map_display_render(field_type: str) -> str:
    t = (field_type or "").strip().upper()
    if t == "BOOLEAN":
        return "boolean"
    if t == "ENUM":
        return "dict"
    if t in {"DATE", "DATETIME", "TIMESTAMP"}:
        return "datetime"
    return "text"


def map_filter_control(field_type: str) -> str:
    t = (field_type or "").strip().upper()
    if t == "BOOLEAN":
        return "boolean"
    if t == "ENUM":
        return "select"
    if t in {"DATE", "DATETIME", "TIMESTAMP"}:
        return "date"
    return "input"


def collect_field_meta(cur, entity_type_code: str) -> OrderedDict[str, dict]:
    cur.execute(
        """
        SELECT f.code, f.name, f.type,
               mfa.is_filterable, mfa.is_searchable, mfa.is_sortable, mfa.sort
        FROM dynamic_model m
        JOIN dynamic_model_field_assignment mfa
          ON mfa.model_id = m.id AND mfa.deleted = FALSE
        JOIN dynamic_field f
          ON f.id = mfa.field_id AND f.deleted = FALSE
        WHERE m.entity_type_code = %s AND m.deleted = FALSE
        """,
        (entity_type_code,),
    )
    by_key: OrderedDict[str, dict] = OrderedDict()
    for row in cur.fetchall():
        field_key = (row["code"] or "").strip()
        if not field_key:
            continue
        existing = by_key.get(field_key)
        if existing is None:
            by_key[field_key] = {
                "fieldKey": field_key,
                "label": (row["name"] or field_key).strip(),
                "fieldType": row["type"],
                "filterable": bool(row["is_filterable"]),
                "searchable": bool(row["is_searchable"]),
                "sortable": bool(row["is_sortable"]),
                "sortOrder": row["sort"] or 0,
            }
            continue
        existing["filterable"] = existing["filterable"] or bool(row["is_filterable"])
        existing["searchable"] = existing["searchable"] or bool(row["is_searchable"])
        existing["sortable"] = existing["sortable"] or bool(row["is_sortable"])

    for key, label, order in (("id", "ID", 0), ("name", "名称", 1), ("code", "编码", 2)):
        by_key.setdefault(
            key,
            {
                "fieldKey": key,
                "label": label,
                "fieldType": "TEXT",
                "filterable": False,
                "searchable": False,
                "sortable": False,
                "sortOrder": order,
            },
        )
    return by_key


def build_projection(entity_type_code: str, component_code: str, version: int, meta: OrderedDict[str, dict]) -> dict:
    display_fields = []
    for order, item in enumerate(meta.values()):
        display_fields.append(
            {
                "id": item["fieldKey"],
                "fieldKey": item["fieldKey"],
                "label": item["label"],
                "renderAs": map_display_render(item["fieldType"]),
                "sortOrder": item.get("sortOrder", order),
                "defaultVisible": True,
                "applicableViews": [component_code],
            }
        )

    filters = []
    filter_order = 0
    for item in meta.values():
        if not item["filterable"]:
            continue
        filters.append(
            {
                "id": item["fieldKey"],
                "fieldKey": item["fieldKey"],
                "label": item["label"],
                "control": map_filter_control(item["fieldType"]),
                "sortOrder": filter_order,
                "bindTo": "field-filter",
                "searchable": item["searchable"],
                "sortable": item["sortable"],
                "defaultVisible": True,
            }
        )
        filter_order += 1

    projection = {
        "entityTypeCode": entity_type_code,
        "componentCode": component_code,
        "version": version,
        "read": {
            "endpoint": READ_ENDPOINT,
            "method": "GET",
            "displayFields": display_fields,
            "filters": filters,
        },
    }
    if component_code != "tree":
        projection["write"] = {
            "create": {"endpoint": "/dynamicbusiness/business/entities/create", "method": "POST"},
            "update": {"endpoint": "/dynamicbusiness/business/entities/update", "method": "PUT"},
            "delete": {"endpoint": "/dynamicbusiness/business/entities/delete", "method": "DELETE"},
        }
    return projection


def main() -> int:
    conn = psycopg2.connect(
        host=os.environ.get("PGHOST", "127.0.0.1"),
        port=os.environ.get("PGPORT", "5432"),
        dbname=os.environ.get("PGDATABASE", "sinopec"),
        user=os.environ.get("PGUSER", "postgres"),
        password=os.environ.get("PGPASSWORD", "Coolhomer"),
        options="-c search_path=dynamicbusiness",
    )
    conn.autocommit = False
    cur = conn.cursor(cursor_factory=psycopg2.extras.RealDictCursor)

    only = sys.argv[1:] or None
    cur.execute(
        "SELECT entity_type_code, version, tenant_id FROM business_capability WHERE deleted = FALSE"
    )
    rows = cur.fetchall()
    upserted = 0
    for row in rows:
        code = row["entity_type_code"]
        if only and code not in only:
            continue
        version = int(row["version"] or 1)
        tenant_id = int(row["tenant_id"] or 1)
        meta = collect_field_meta(cur, code)
        for component_code in COMPONENT_CODES:
            projection = build_projection(code, component_code, version, meta)
            cur.execute(
                """
                INSERT INTO capability_component_projection (
                    entity_type_code, component_code, component_interface, version,
                    creator, create_time, updater, update_time, deleted, tenant_id
                ) VALUES (%s, %s, %s::jsonb, %s, 'script', NOW(), 'script', NOW(), FALSE, %s)
                ON CONFLICT DO NOTHING
                """,
                (code, component_code, json.dumps(projection, ensure_ascii=False), version, tenant_id),
            )
            cur.execute(
                """
                UPDATE capability_component_projection
                SET component_interface = %s::jsonb,
                    version = %s,
                    updater = 'script',
                    update_time = NOW()
                WHERE entity_type_code = %s
                  AND component_code = %s
                  AND tenant_id = %s
                  AND deleted = FALSE
                """,
                (json.dumps(projection, ensure_ascii=False), version, code, component_code, tenant_id),
            )
            upserted += 1
            print(f"upserted {code}/{component_code} fields={len(meta)} filters={sum(1 for m in meta.values() if m['filterable'])}")

    conn.commit()
    cur.close()
    conn.close()
    print(f"done, {upserted} projections")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
