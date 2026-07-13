#!/usr/bin/env python3
"""Export pr_component_props (dynamic/system data_source bindings) to Flyway seed SQL."""

from __future__ import annotations

import json
import os
import sys
from pathlib import Path

try:
    import psycopg2
    import psycopg2.extras
except ImportError:
    print("Install psycopg2-binary", file=sys.stderr)
    sys.exit(1)

OUT = (
    Path(__file__).resolve().parent.parent
    / "src/main/resources/db/migration/V7__seed_pr_component_props_dynamic.sql"
)

# V4 demo ids without data_source; skip unless Mac DB bound a data_source
SKIP_UNBOUND_DEMO_IDS = {2001}


def dollar_tag(prefix: str, body: str) -> str:
    tag = prefix
    n = 0
    while f"${tag}$" in body:
        n += 1
        tag = f"{prefix}_{n}"
    return f"${tag}${body}${tag}$"


def sql_str(value: str | None) -> str:
    if value is None:
        return "NULL"
    return dollar_tag("txt", value)


def main() -> int:
    conn = psycopg2.connect(
        host=os.environ.get("PGHOST", "127.0.0.1"),
        port=os.environ.get("PGPORT", "5432"),
        dbname=os.environ.get("PGDATABASE", "sinopec"),
        user=os.environ.get("PGUSER", "postgres"),
        password=os.environ.get("PGPASSWORD", "Coolhomer"),
        options="-c search_path=platformresource,public",
    )
    cur = conn.cursor(cursor_factory=psycopg2.extras.RealDictCursor)
    cur.execute(
        """
        SELECT id, is_template, component_id, component_code, data_source,
               template_id, schema_version, props, props_override,
               name, status, sort, description, tenant_id
        FROM pr_component_props
        WHERE deleted = FALSE
          AND (
            (data_source IS NOT NULL AND TRIM(data_source) <> '' AND data_source <> 'null')
            OR id NOT IN (1001, 1002, 2001)
          )
        ORDER BY id
        """
    )
    rows = cur.fetchall()
    cur.close()
    conn.close()

    lines: list[str] = [
        "SET search_path TO platformresource;",
        "",
        "-- Mac 开发库导出的组件 props（含 data_source 绑定动态/系统业务）",
        "-- 生成：cheers-module-platform/cheers-module-platform-resource-server/scripts/generate-seed-component-props.py",
        "-- 覆盖 V4 演示模板中已绑定业务数据来源的行（如 equipment + entity）",
        "",
    ]

    exported = 0
    for row in rows:
        row_id = int(row["id"])
        has_ds = bool((row["data_source"] or "").strip()) and row["data_source"] != "null"
        if row_id in SKIP_UNBOUND_DEMO_IDS and not has_ds:
            continue

        props = row["props"] or "{}"
        props_override = row["props_override"]
        data_source = row["data_source"]

        try:
            props = json.dumps(json.loads(props), ensure_ascii=False, separators=(",", ":"))
        except json.JSONDecodeError:
            pass
        if props_override:
            try:
                props_override = json.dumps(
                    json.loads(props_override), ensure_ascii=False, separators=(",", ":")
                )
            except json.JSONDecodeError:
                pass

        comp_code = row["component_code"]
        lines.append(f"-- propsId={row_id} {comp_code} {row['name']}")
        lines.append("INSERT INTO pr_component_props (")
        lines.append(
            "    id, is_template, component_id, component_code, data_source,"
        )
        lines.append(
            "    template_id, schema_version, props, props_override, name, status, sort, description, tenant_id"
        )
        lines.append(")")
        lines.append("SELECT")
        lines.append(
            f"    {row_id}, {str(bool(row['is_template'])).upper()}, c.id, c.component_code, "
            f"{sql_str(data_source)},"
        )
        tpl = "NULL" if row["template_id"] is None else str(row["template_id"])
        lines.append(
            f"    {tpl}, {sql_str(row['schema_version'])}, {sql_str(props)}, "
            f"{sql_str(props_override)}, {sql_str(row['name'])}, "
            f"{row['status']}, {row['sort']}, {sql_str(row['description'])}, {row['tenant_id']}"
        )
        lines.append(
            f"FROM pr_component c WHERE c.component_code = {sql_str(comp_code)} AND c.deleted = FALSE"
        )
        lines.append("ON CONFLICT (id) DO UPDATE SET")
        lines.append("    is_template = EXCLUDED.is_template,")
        lines.append("    component_id = EXCLUDED.component_id,")
        lines.append("    component_code = EXCLUDED.component_code,")
        lines.append("    data_source = EXCLUDED.data_source,")
        lines.append("    template_id = EXCLUDED.template_id,")
        lines.append("    schema_version = EXCLUDED.schema_version,")
        lines.append("    props = EXCLUDED.props,")
        lines.append("    props_override = EXCLUDED.props_override,")
        lines.append("    name = EXCLUDED.name,")
        lines.append("    status = EXCLUDED.status,")
        lines.append("    sort = EXCLUDED.sort,")
        lines.append("    description = EXCLUDED.description,")
        lines.append("    tenant_id = EXCLUDED.tenant_id,")
        lines.append("    updater = 'seed',")
        lines.append("    update_time = CURRENT_TIMESTAMP,")
        lines.append("    deleted = FALSE;")
        lines.append("")
        exported += 1

    lines.append(
        "SELECT setval(pg_get_serial_sequence('pr_component_props', 'id'), "
        "(SELECT COALESCE(MAX(id), 1) FROM pr_component_props));"
    )
    lines.append("")

    OUT.parent.mkdir(parents=True, exist_ok=True)
    OUT.write_text("\n".join(lines), encoding="utf-8")
    print(f"Wrote {OUT} ({exported} rows)")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
