#!/usr/bin/env python3
"""把协议实体 custom_fields 收成三列：字段说明 + 指令 JSON + 样例。

负责：按页签存三卷；字段说明只保留当前空包里有的路径；样例从旧列迁到 sample_json。
不负责：改型号分组、系统字段、步骤绑定。
禁止：读路径猜默认字段；步骤信号不再写入协议实体。
禁止：把样例当成第四个页签数据另存一套。
"""

from __future__ import annotations

import json
import os
import re
import subprocess
import sys
from pathlib import Path

ROLES = ("outbound", "inboundSuccess", "inboundFailure")
DROP_KEYS = {
    "command_schema_json",
    "field_translation_rules_json",
    "step_signal_mapping_rules_json",
    "sample_messages_json",
    "protocol_command_schema_json",
    "protocol_field_translation_rules_json",
    "protocol_step_signal_mapping_rules_json",
    "protocol_sample_messages_json",
}
SEED_PATH = Path(__file__).with_name("dynamic_protocol_ground_station.sql")


def is_catalog(value: object) -> bool:
    return isinstance(value, dict) and any(role in value for role in ROLES)


def schema_to_blank(schema: object) -> dict:
    if not isinstance(schema, dict):
        return {}
    props = schema.get("properties")
    if schema.get("type") == "object" and isinstance(props, dict):
        blank: dict = {}
        for key, spec in props.items():
            child = spec if isinstance(spec, dict) else {}
            kind = child.get("type")
            if kind in {"number", "integer"}:
                blank[key] = 0
            elif kind == "boolean":
                blank[key] = False
            elif kind == "array":
                blank[key] = []
            elif kind == "object":
                blank[key] = schema_to_blank(child)
            else:
                blank[key] = ""
        return blank
    return dict(schema)


def wrap_packets(value: object) -> dict:
    if is_catalog(value):
        return {role: value[role] for role in ROLES if role in value}  # type: ignore[index]
    if isinstance(value, dict) and value.get("type") == "object" and "properties" in value:
        return {"outbound": schema_to_blank(value)}
    if isinstance(value, dict) and value:
        return {"outbound": value}
    return {}


def collect_paths(obj: object, prefix: str = "") -> set[str]:
    paths: set[str] = set()
    if isinstance(obj, dict):
        for key, child in obj.items():
            path = f"{prefix}.{key}" if prefix else str(key)
            paths.add(path)
            paths |= collect_paths(child, path)
    elif isinstance(obj, list) and obj:
        paths |= collect_paths(obj[0], f"{prefix}[]" if prefix else "")
        paths |= collect_paths(obj[0], prefix)
    return paths


def normalize_rule(raw: object) -> dict | None:
    if not isinstance(raw, dict):
        return None
    path = str(raw.get("path") or raw.get("code") or raw.get("from") or "").strip()
    if not path:
        return None
    code = str(raw.get("code") or path.split(".")[-1])
    return {
        "code": code,
        "label": raw.get("label") or code,
        "path": path,
        "type": raw.get("type"),
        "required": bool(raw.get("required")),
        "description": raw.get("description"),
    }


def rule_matches(path: str, packet_paths: set[str]) -> bool:
    variants = {
        path,
        path.replace("[]", ""),
        path.replace("[0]", ""),
    }
    return any(item in packet_paths for item in variants if item)


def wrap_fields(value: object, packets: dict) -> dict:
    if isinstance(value, dict) and any(role in value for role in ROLES):
        catalog: dict = {}
        for role in ROLES:
            rows = value.get(role)
            if not isinstance(rows, list):
                continue
            packet = packets.get(role)
            paths = collect_paths(packet) if packet is not None else set()
            kept = []
            for raw in rows:
                rule = normalize_rule(raw)
                if rule is None:
                    continue
                if paths and not rule_matches(rule["path"], paths):
                    continue
                kept.append(rule)
            catalog[role] = kept
        return catalog
    if isinstance(value, list):
        outbound = packets.get("outbound")
        paths = collect_paths(outbound) if outbound is not None else set()
        kept = []
        for raw in value:
            rule = normalize_rule(raw)
            if rule is None:
                continue
            if paths and not rule_matches(rule["path"], paths):
                continue
            kept.append(rule)
        return {"outbound": kept}
    return {}


def load_seed_samples() -> dict[str, dict]:
    if not SEED_PATH.exists():
        return {}
    text = SEED_PATH.read_text()
    samples: dict[str, dict] = {}
    for match in re.finditer(r"\('([^']+)',\s*'[^']*',\s*\$\$(.*?)\$\$\)", text, re.S):
        code, raw = match.group(1), match.group(2)
        try:
            data = json.loads(raw)
        except json.JSONDecodeError:
            continue
        raw_sample = data.get("sample_json") or data.get("sample_messages_json")
        wrapped = wrap_packets(raw_sample) if raw_sample else {}
        if wrapped:
            samples[code] = wrapped
    return samples


def transform(custom_fields: object, seed_sample: dict | None = None) -> dict:
    source = dict(custom_fields) if isinstance(custom_fields, dict) else {}
    raw_packets = (
        source.get("command_json")
        or source.get("command_schema_json")
        or source.get("protocol_command_schema_json")
    )
    raw_fields = (
        source.get("field_description_json")
        or source.get("field_translation_rules_json")
        or source.get("protocol_field_translation_rules_json")
    )
    raw_samples = (
        source.get("sample_json")
        or source.get("sample_messages_json")
        or source.get("protocol_sample_messages_json")
    )
    packets = wrap_packets(raw_packets)
    fields = wrap_fields(raw_fields, packets)
    samples = wrap_packets(raw_samples) if raw_samples else {}
    if not samples and seed_sample:
        samples = seed_sample
    next_fields = {key: value for key, value in source.items() if key not in DROP_KEYS}
    next_fields["command_json"] = packets
    next_fields["field_description_json"] = fields
    if samples:
        next_fields["sample_json"] = samples
    else:
        next_fields.pop("sample_json", None)
    return next_fields


def psql(sql: str) -> str:
    env = os.environ.copy()
    env.setdefault("PGPASSWORD", "Coolhomer")
    result = subprocess.run(
        [
            "psql",
            "-h",
            env.get("PGHOST", "127.0.0.1"),
            "-U",
            env.get("PGUSER", "postgres"),
            "-d",
            env.get("PGDATABASE", "sinopec"),
            "-v",
            "ON_ERROR_STOP=1",
            "-At",
            "-c",
            sql,
        ],
        check=True,
        capture_output=True,
        text=True,
        env=env,
    )
    return result.stdout


def main() -> int:
    seed_samples = load_seed_samples()
    raw = psql(
        "SELECT json_agg(json_build_object('id', id, 'code', code, 'custom_fields', custom_fields)) "
        "FROM dynamicbusiness.ent_data_protocol_t1 "
        "WHERE deleted = false AND tenant_id = 1;"
    ).strip()
    rows = json.loads(raw or "[]")
    updated = 0
    restored = 0
    for row in rows:
        seed_sample = seed_samples.get(str(row.get("code") or ""))
        nxt = transform(row.get("custom_fields"), seed_sample)
        if nxt == row.get("custom_fields"):
            continue
        if seed_sample and not wrap_packets(
            (row.get("custom_fields") or {}).get("sample_json")
            if isinstance(row.get("custom_fields"), dict)
            else None
        ):
            restored += 1
        payload = json.dumps(nxt, ensure_ascii=False).replace("'", "''")
        psql(
            "UPDATE dynamicbusiness.ent_data_protocol_t1 "
            f"SET custom_fields = '{payload}'::jsonb, updater = 'seed', update_time = CURRENT_TIMESTAMP "
            f"WHERE id = {int(row['id'])};"
        )
        updated += 1
        print(f"updated {row.get('code')}")
    print(f"done {updated}/{len(rows)} restored_samples={restored}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
