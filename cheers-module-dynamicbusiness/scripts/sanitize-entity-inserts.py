#!/usr/bin/env python3
"""Strip deprecated / legacy physical columns from pg_dump INSERT lines."""

from __future__ import annotations

import re
import sys
from pathlib import Path

DROP_BY_TABLE: dict[str, set[str]] = {
    "biz_equipment": {
        "_deprecated_status",
        "_deprecated_manufacturer",
        "_deprecated_modelnumber",
        "_deprecated_serialnumber",
        "_deprecated_regionid",
        "_deprecated_purchasedate",
        "_deprecated_warrantyexpiry",
        "_deprecated_equipmentstatus",
        "_deprecated_lastmaintenancetime",
        "_deprecated_healthscore",
        "_deprecated_equipment_code",
        "_deprecated_equipment_name",
        "_deprecated_install_date",
        "_deprecated_equipment_model",
        "model_number",
        "serial_number",
        "purchase_date",
        "warranty_expiry",
        "health_score",
        "last_maintenance_time",
        "equipment_status",
        "equipment_code",
        "equipment_name",
        "status",
        "install_date",
        "manufacturer",
        "equipment_model",
        "rel_region",
        "di_zuo_lei_xing",
        "rel_ke_hu",
        "rel_spare_part",
    },
    "biz_maintenance": {
        "_deprecated_order_no",
        "_deprecated_maintenance_type",
        "_deprecated_plan_time",
        "_deprecated_executor",
        "_deprecated_order_status",
    },
    "biz_region": {
        "_deprecated_region_code",
        "_deprecated_region_name",
        "_deprecated_region_type",
    },
}

INSERT_RE = re.compile(
    r"^(INSERT INTO dynamicbusiness\.(\w+) \((.+?)\)"
    r"( OVERRIDING SYSTEM VALUE)? VALUES \((.+)\);?\s*)$",
    re.DOTALL,
)


def split_sql_values(raw: str) -> list[str]:
    parts: list[str] = []
    buf: list[str] = []
    depth = 0
    in_str = False
    i = 0
    while i < len(raw):
        ch = raw[i]
        if in_str:
            buf.append(ch)
            if ch == "'" and i + 1 < len(raw) and raw[i + 1] == "'":
                buf.append(raw[i + 1])
                i += 2
                continue
            if ch == "'":
                in_str = False
            i += 1
            continue
        if ch == "'":
            in_str = True
            buf.append(ch)
        elif ch == "(":
            depth += 1
            buf.append(ch)
        elif ch == ")":
            depth -= 1
            buf.append(ch)
        elif ch == "," and depth == 0:
            parts.append("".join(buf).strip())
            buf = []
        else:
            buf.append(ch)
        i += 1
    if buf:
        parts.append("".join(buf).strip())
    return parts


def transform_line(line: str) -> str:
    m = INSERT_RE.match(line.strip())
    if not m:
        return line
    table = m.group(2)
    drop = DROP_BY_TABLE.get(table)
    if not drop:
        return line
    cols = [c.strip() for c in m.group(3).split(",")]
    vals = split_sql_values(m.group(5))
    if len(cols) != len(vals):
        return line
    kept_cols: list[str] = []
    kept_vals: list[str] = []
    for col, val in zip(cols, vals):
        if col in drop:
            continue
        kept_cols.append(col)
        kept_vals.append(val)
    overriding = m.group(4) or ""
    return (
        f"INSERT INTO dynamicbusiness.{table} ({', '.join(kept_cols)})"
        f"{overriding} VALUES ({', '.join(kept_vals)});"
    )


def sanitize_file(path: Path) -> tuple[int, int]:
    text = path.read_text(encoding="utf-8")
    lines = text.splitlines(keepends=True)
    changed = 0
    out: list[str] = []
    for line in lines:
        if line.lstrip().startswith("INSERT INTO dynamicbusiness."):
            new_line = transform_line(line.rstrip("\n"))
            if new_line != line.rstrip("\n"):
                changed += 1
            out.append(new_line + ("\n" if line.endswith("\n") else ""))
        else:
            out.append(line)
    path.write_text("".join(out), encoding="utf-8")
    return changed, sum(1 for ln in lines if "_deprecated" in ln)


def main(argv: list[str]) -> int:
    paths = [Path(p) for p in argv[1:]] or [
        Path(__file__).resolve().parent / "platform-import/smart-corridor/05_entities.sql",
    ]
    for path in paths:
        if not path.exists():
            print(f"skip missing {path}")
            continue
        changed, remaining = sanitize_file(path)
        print(f"{path}: transformed {changed} INSERT lines, _deprecated remaining in file: {remaining}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main(sys.argv))
