#!/usr/bin/env python3
"""Audit protocol mapping vs instruction paths and USER slots vs action param_slots."""
import csv
import io
import json
import subprocess
import sys

SQL = """
SET search_path TO dynamicbusiness;
COPY (
  SELECT m.code AS map_code,
         m.custom_fields->>'mapped_protocol_version' AS ver,
         a.code AS action_code,
         a.name AS action_name,
         COALESCE(a.param_slots_json::text, '') AS param_slots,
         COALESCE((p.custom_fields->'field_description_json'->'outbound')::text, '[]') AS outbound,
         COALESCE((m.custom_fields->'param_slot_mapping_json')::text, '[]') AS slots
  FROM ent_protocol_mapping_t1 m
  JOIN ent_action_t1 a ON a.id = (m.custom_fields->'action_ref'->>'id')::bigint AND a.deleted = false
  JOIN ent_data_protocol_t1 p ON p.id = (m.custom_fields->'protocol_instruction_ref'->>'id')::bigint AND p.deleted = false
  WHERE m.deleted = false AND m.tenant_id = 1
) TO STDOUT WITH (FORMAT csv, HEADER true, QUOTE '"', ESCAPE '"');
"""


def psql_csv():
    env = {**dict(subprocess.os.environ), "PGPASSWORD": "Coolhomer"}
    p = subprocess.run(
        ["psql", "-h", "127.0.0.1", "-U", "postgres", "-d", "sinopec", "-c", SQL],
        capture_output=True,
        text=True,
        encoding="utf-8",
        errors="replace",
        env=env,
    )
    if p.returncode != 0:
        print(p.stderr, file=sys.stderr)
        sys.exit(1)
    return p.stdout


def csv_body(psql_stdout: str) -> str:
    """psql 会在 COPY 前输出 SET 等行，只保留 CSV 表头及以后的内容。"""
    lines = psql_stdout.splitlines()
    for i, line in enumerate(lines):
        if line.startswith("map_code,"):
            return "\n".join(lines[i:]) + "\n"
    return psql_stdout


def field_codes(param_slots_raw: str) -> set[str]:
    if not param_slots_raw.strip():
        return set()
    try:
        root = json.loads(param_slots_raw)
    except json.JSONDecodeError:
        return set()
    codes: set[str] = set()
    for f in root.get("fields") or []:
        c = (f.get("fieldCode") or f.get("code") or "").strip()
        if c:
            codes.add(c)
    for meth in root.get("methods") or []:
        if not isinstance(meth, dict):
            continue
        for f in meth.get("fields") or []:
            c = (f.get("fieldCode") or f.get("code") or "").strip()
            if c:
                codes.add(c)
    return codes


def main():
    out = csv_body(psql_csv())
    reader = csv.DictReader(io.StringIO(out))
    if not reader.fieldnames or "ver" not in reader.fieldnames:
        print("unexpected CSV columns: %r" % (reader.fieldnames,), file=sys.stderr)
        sys.exit(1)
    path_gaps = []
    slot_gaps = []
    empty_slot_maps = []
    for row in reader:
        ver = row["ver"] or ""
        try:
            outbound = json.loads(row["outbound"] or "[]")
            slots = json.loads(row["slots"] or "[]")
        except json.JSONDecodeError:
            continue
        covered = {(s.get("path") or "").strip() for s in slots if (s.get("slot") or "").strip()}
        if len(slots) == 0:
            req = [
                i
                for i in outbound
                if (i.get("path") or "").startswith("request.")
                and i.get("required") is not False
            ]
            if req:
                empty_slot_maps.append((ver, row["action_code"], row["map_code"], len(req)))
        for item in outbound:
            path = (item.get("path") or "").strip()
            if not path or path in ("sequence", "opcode", "request") or "." not in path:
                continue
            if item.get("required") is False:
                continue
            if path not in covered:
                path_gaps.append(
                    (ver, row["action_code"], row["map_code"], path, item.get("label") or path)
                )
        declared = field_codes(row["param_slots"])
        for s in slots:
            slot = (s.get("slot") or "").strip()
            if not slot:
                continue
            kind = (s.get("sourceKind") or "ACTION_PARAM").strip().upper()
            if kind == "USER":
                kind = "ACTION_PARAM"
            if kind != "ACTION_PARAM":
                continue
            if slot not in declared:
                slot_gaps.append((ver, row["action_code"], slot, row["map_code"]))

    print("=== 1. UI 参数未齐：指令必填路径未在对照中（共 %d 条）===" % len(path_gaps))
    for g in sorted(path_gaps):
        print("\t".join(g))

    print("\n=== 2. 空对照但有必填 request 字段（共 %d 条）===" % len(empty_slot_maps))
    for g in sorted(empty_slot_maps):
        print("\t".join(str(x) for x in g))

    print("\n=== 3. ACTION_PARAM 对照槽在动作 param_slots 未声明（共 %d 条）===" % len(slot_gaps))
    for g in sorted(set(slot_gaps)):
        print("\t".join(g))


if __name__ == "__main__":
    main()
