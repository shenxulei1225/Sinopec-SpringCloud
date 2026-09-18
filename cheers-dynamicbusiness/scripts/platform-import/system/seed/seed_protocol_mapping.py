#!/usr/bin/env python3
"""种「指令协议对接」的型号、对照字段和对照行。

目录本体须已由创建接口建出（编码 protocol_mapping）。
本脚本只补：唯一型号、四列对照字段、有真实协议指令的对照行。
界面保存只认型号已分配的引用字段，不查旧业务类型关联许可表。
不负责：拆开跑适配器、改回写、改对端协议、给「调整拍摄角度 / 人工确认 / 智能识别」建对接行。
禁止：读路径补对照；发明没有协议指令的对接行；把何时返回写进对接表。
"""

from __future__ import annotations

import json
import os
import subprocess
import sys
import urllib.error
import urllib.request
from pathlib import Path

SEED_DIR = Path(__file__).resolve().parent
SQL_OUT = SEED_DIR / "dynamic_protocol_mapping.sql"
MODEL_CODE = "protocol_mapping_standard"
MODEL_NAME = "指令协议对照"
ENTITY_TYPE = "protocol_mapping"
CATEGORY_CODE = "protocol_mapping_root"
TENANT_ID = 1

# 对照行只覆盖现网有真实协议指令的动作。对准/人工确认/识别不建行。
# slot 必须是该动作 param_slots_json 里已有的槽；path 必须是对应空包里已有的路径。
MAPPINGS = (
    {
        "code": "map-arrive-uav-ws",
        "name": "到达指定位置 · 无人机对接协议",
        "action_code": "action-065340437bcb46788b7dc261fab81c63",
        "protocol_version": "uav-ws",
        "instruction_code": "proto-uav-200102-move",
        "slots": (
            {"slot": "location_ref", "sourcePath": "FLD-PNT-002", "path": "request.pointId"},
        ),
    },
    {
        "code": "map-arrive-robot-ws",
        "name": "到达指定位置 · 机器人对接协议",
        "action_code": "action-065340437bcb46788b7dc261fab81c63",
        "protocol_version": "robot-ws",
        "instruction_code": "proto-robot-200102-move",
        "slots": (
            {"slot": "location_ref", "sourcePath": "FLD-PNT-002", "path": "request.pointId"},
        ),
    },
    {
        "code": "map-photo-uav-ws",
        "name": "拍照 · 无人机对接协议",
        "action_code": "act-robot-shoot",
        "protocol_version": "uav-ws",
        "instruction_code": "proto-uav-200301-photo",
        "slots": ({"slot": "shot_count", "path": "request.number"},),
    },
    {
        "code": "map-photo-robot-ws",
        "name": "拍照 · 机器人对接协议",
        "action_code": "act-robot-shoot",
        "protocol_version": "robot-ws",
        "instruction_code": "proto-robot-200301-photo",
        "slots": ({"slot": "shot_count", "path": "request.number"},),
    },
    {
        "code": "map-takeoff-uav-ws",
        "name": "无人机起飞 · 无人机对接协议",
        "action_code": "action-2b44a1f882f54438b6a1864e3636c889",
        "protocol_version": "uav-ws",
        "instruction_code": "proto-uav-200101-takeoff",
        "slots": (),
    },
    {
        "code": "map-land-uav-ws",
        "name": "无人机返航 · 无人机对接协议",
        "action_code": "action-1022d966da2b45f7b73e776112e6884b",
        "protocol_version": "uav-ws",
        "instruction_code": "proto-uav-200103-land",
        "slots": (),
    },
    {
        "code": "map-gas-on-uav-ws",
        "name": "气体检测打开 · 无人机对接协议",
        "action_code": "action-647f9f172c3b489ea862a29c0e8396a2",
        "protocol_version": "uav-ws",
        "instruction_code": "proto-uav-200401-gas-on",
        "slots": (),
    },
    {
        "code": "map-gas-off-uav-ws",
        "name": "气体检测关闭 · 无人机对接协议",
        "action_code": "action-ab595a61ca164a2c9db74e1311e747a5",
        "protocol_version": "uav-ws",
        "instruction_code": "proto-uav-200402-gas-off",
        "slots": (),
    },
)

FIELDS = (
    {
        "code": "action_ref",
        "name": "动作",
        "type": "ENTITY_REF",
        "description": "这条对照对应哪条动作。只存动作实体 id。",
        "index_strategy": "BTREE",
        "options": "NULL",
        "provider_code": "dynamic-entity:action",
        "semantic_type": "action",
        "max_relations": 1,
        "sort": 10,
        "required": True,
        "searchable": True,
    },
    {
        "code": "mapped_protocol_version",
        "name": "协议版本",
        "type": "ENUM",
        "description": "这条对照适用哪一份对接协议。一条对照只选一种，不要复用指令上的多选协议版本字段。",
        "index_strategy": "BTREE",
        "options": "'[{\"label\":\"机器人对接协议\",\"value\":\"robot-ws\"},{\"label\":\"无人机对接协议\",\"value\":\"uav-ws\"}]'",
        "provider_code": "NULL",
        "semantic_type": "mapped_protocol_version",
        "max_relations": None,
        "sort": 20,
        "required": True,
        "searchable": True,
    },
    {
        "code": "protocol_instruction_ref",
        "name": "协议指令",
        "type": "ENTITY_REF",
        "description": "这条对照落到协议目录里的哪一条指令。只存指令实体 id。",
        "index_strategy": "BTREE",
        "options": "NULL",
        "provider_code": "dynamic-entity:data_protocol",
        "semantic_type": "data_protocol",
        "max_relations": 1,
        "sort": 30,
        "required": True,
        "searchable": True,
    },
    {
        "code": "param_slot_mapping_json",
        "name": "参数对照",
        "type": "JSON",
        "description": "动作参数填到指令哪一项。元素是 {slot, sourcePath, path}。引用参数用 sourcePath 写出取哪一项。不记何时返回。",
        "index_strategy": "NONE",
        "options": "NULL",
        "provider_code": "NULL",
        "semantic_type": "PARAM_SLOT_MAPPING",
        "max_relations": None,
        "sort": 40,
        "required": False,
        "searchable": False,
    },
)


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


def write_seed_sql() -> None:
    field_values = []
    for field in FIELDS:
        max_rel = "NULL" if field["max_relations"] is None else str(field["max_relations"])
        provider = "NULL" if field["provider_code"] == "NULL" else f"'{field['provider_code']}'"
        options = field["options"]
        field_values.append(
            "  ('{code}', '{name}', '{type}', NULL, '{description}', "
            "'SYSTEM', 1, {max_rel}, '{index_strategy}', {options}, {provider}, "
            "'{semantic}', {tenant}, 'seed')".format(
                code=field["code"],
                name=field["name"],
                type=field["type"],
                description=field["description"],
                max_rel=max_rel,
                index_strategy=field["index_strategy"],
                options=options,
                provider=provider,
                semantic=field["semantic_type"],
                tenant=TENANT_ID,
            )
        )
    assign_values = ",\n".join(
        f"  ('{field['code']}', {field['sort']}, {str(field['required']).lower()}, "
        f"{str(field['searchable']).lower()})"
        for field in FIELDS
    )
    mapping_values = []
    for item in MAPPINGS:
        slots = json.dumps(list(item["slots"]), ensure_ascii=False, separators=(",", ":"))
        mapping_values.append(
            "  ('{code}', '{name}', '{action}', '{version}', '{instruction}', $${slots}$$)".format(
                code=item["code"],
                name=item["name"],
                action=item["action_code"],
                version=item["protocol_version"],
                instruction=item["instruction_code"],
                slots=slots,
            )
        )
    field_sql = ",\n".join(field_values)
    mapping_sql = ",\n".join(mapping_values)
    sql = f"""-- 指令协议对接：型号、对照字段、对照行。目录本体须已由创建接口建出。
-- 界面保存对照只认型号已分配的引用字段，不查旧业务类型关联许可表。
-- 不负责：开跑翻译、回写、对端协议。禁止发明没有协议指令的对接行。
SET search_path TO dynamicbusiness, public;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES
{field_sql}
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name, type = EXCLUDED.type, description = EXCLUDED.description,
  options = EXCLUDED.options, provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type, max_relations = EXCLUDED.max_relations,
  index_strategy = EXCLUDED.index_strategy, source = EXCLUDED.source, status = 1,
  deleted = false, updater = 'seed', update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, domain, description, status, sort, tenant_id, creator, governance_status
)
SELECT
  '{MODEL_CODE}', '{MODEL_NAME}', '{ENTITY_TYPE}', NULL,
  '动作加协议版本对应哪条协议指令，以及参数槽填到空包哪条路径。不记何时返回。',
  1, 10, {TENANT_ID}, 'seed', 'COMPANY'
WHERE EXISTS (
  SELECT 1 FROM dynamic_entity_type t
  WHERE t.deleted = false AND t.tenant_id = {TENANT_ID} AND t.code = '{ENTITY_TYPE}'
)
AND NOT EXISTS (
  SELECT 1 FROM dynamic_model m
  WHERE m.deleted = false AND m.tenant_id = {TENANT_ID} AND m.entity_type_code = '{ENTITY_TYPE}'
);

UPDATE dynamic_model
SET name = '{MODEL_NAME}',
    description = '动作加协议版本对应哪条协议指令，以及参数槽填到空包哪条路径。不记何时返回。',
    governance_status = 'COMPANY',
    status = 1,
    deleted = false,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false AND tenant_id = {TENANT_ID} AND entity_type_code = '{ENTITY_TYPE}'
  AND code <> '{MODEL_CODE}'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model x
    WHERE x.deleted = false AND x.tenant_id = {TENANT_ID} AND x.code = '{MODEL_CODE}'
  );

UPDATE dynamic_model
SET code = '{MODEL_CODE}',
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false AND tenant_id = {TENANT_ID} AND entity_type_code = '{ENTITY_TYPE}'
  AND code <> '{MODEL_CODE}'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model x
    WHERE x.deleted = false AND x.tenant_id = {TENANT_ID} AND x.code = '{MODEL_CODE}'
  );

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, model_code, field_code, required, is_searchable, is_filterable, is_sortable,
  sort, field_source, tenant_id, creator
)
SELECT m.id, f.id, m.code, f.code, mapping.required, mapping.searchable, mapping.searchable, false,
       mapping.sort_order, 'CUSTOM', {TENANT_ID}, 'seed'
FROM (VALUES
{assign_values}
) AS mapping(field_code, sort_order, required, searchable)
JOIN dynamic_model m ON m.deleted = false AND m.tenant_id = {TENANT_ID} AND m.code = '{MODEL_CODE}'
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = {TENANT_ID} AND f.code = mapping.field_code
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_id = EXCLUDED.field_id, sort = EXCLUDED.sort, required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable, is_filterable = EXCLUDED.is_filterable,
  field_source = EXCLUDED.field_source, deleted = false,
  updater = 'seed', update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation_t1 (
  model_id, category_id, entity_type_code, sort, creator, tenant_id, model_code, category_code, deleted
)
SELECT m.id, c.id, '{ENTITY_TYPE}', m.sort, 'seed', {TENANT_ID}, m.code, c.code, false
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = {TENANT_ID} AND c.code = '{CATEGORY_CODE}'
WHERE m.deleted = false AND m.tenant_id = {TENANT_ID} AND m.code = '{MODEL_CODE}'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id, category_id = EXCLUDED.category_id,
  deleted = false, updater = 'seed', update_time = CURRENT_TIMESTAMP;

INSERT INTO ent_protocol_mapping_t1 (
  tenant_id, entity_type_code, model_id, name, code, status, custom_fields, creator, deleted
)
SELECT
  {TENANT_ID}, '{ENTITY_TYPE}', m.id, v.name, v.code, 1,
  jsonb_build_object(
    'action_ref', jsonb_build_object('id', a.id, 'entityTypeCode', 'action'),
    'mapped_protocol_version', v.protocol_version,
    'protocol_instruction_ref', jsonb_build_object('id', p.id, 'entityTypeCode', 'data_protocol'),
    'param_slot_mapping_json', v.slots::jsonb
  ),
  'seed', false
FROM (VALUES
{mapping_sql}
) AS v(code, name, action_code, protocol_version, instruction_code, slots)
JOIN dynamic_model m ON m.deleted = false AND m.tenant_id = {TENANT_ID} AND m.code = '{MODEL_CODE}'
JOIN ent_action_t1 a ON a.deleted = false AND a.tenant_id = {TENANT_ID} AND a.code = v.action_code
JOIN ent_data_protocol_t1 p ON p.deleted = false AND p.tenant_id = {TENANT_ID} AND p.code = v.instruction_code
WHERE NOT EXISTS (
  SELECT 1 FROM ent_protocol_mapping_t1 e
  WHERE e.deleted = false AND e.tenant_id = {TENANT_ID} AND e.code = v.code
);

UPDATE ent_protocol_mapping_t1 e
SET
  name = v.name,
  custom_fields = jsonb_build_object(
    'action_ref', jsonb_build_object('id', a.id, 'entityTypeCode', 'action'),
    'mapped_protocol_version', v.protocol_version,
    'protocol_instruction_ref', jsonb_build_object('id', p.id, 'entityTypeCode', 'data_protocol'),
    'param_slot_mapping_json', v.slots::jsonb
  ),
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
FROM (VALUES
{mapping_sql}
) AS v(code, name, action_code, protocol_version, instruction_code, slots)
JOIN ent_action_t1 a ON a.deleted = false AND a.tenant_id = {TENANT_ID} AND a.code = v.action_code
JOIN ent_data_protocol_t1 p ON p.deleted = false AND p.tenant_id = {TENANT_ID} AND p.code = v.instruction_code
WHERE e.deleted = false AND e.tenant_id = {TENANT_ID} AND e.code = v.code;

INSERT INTO dynamic_entity_category_relation_t1 (
  tenant_id, category_id, entity_type_code, entity_id, domain, sort, creator, deleted
)
SELECT {TENANT_ID}, c.id, '{ENTITY_TYPE}', e.id, NULL, 0, 'seed', false
FROM ent_protocol_mapping_t1 e
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = {TENANT_ID} AND c.code = '{CATEGORY_CODE}'
WHERE e.deleted = false AND e.tenant_id = {TENANT_ID}
AND NOT EXISTS (
  SELECT 1 FROM dynamic_entity_category_relation_t1 r
  WHERE r.tenant_id = {TENANT_ID} AND r.entity_type_code = '{ENTITY_TYPE}'
    AND r.entity_id = e.id AND r.category_id = c.id
);

INSERT INTO dynamic_entity_field_index_t1
  (entity_id, model_id, field_code, value_string, value_number, creator, tenant_id, deleted)
SELECT e.id, e.model_id, 'action_ref', NULL, (e.custom_fields->'action_ref'->>'id')::bigint, 'seed', {TENANT_ID}, false
FROM ent_protocol_mapping_t1 e
WHERE e.deleted = false AND e.tenant_id = {TENANT_ID}
AND NOT EXISTS (
  SELECT 1 FROM dynamic_entity_field_index_t1 idx
  WHERE idx.deleted = false AND idx.tenant_id = {TENANT_ID}
    AND idx.entity_id = e.id AND idx.field_code = 'action_ref'
);

INSERT INTO dynamic_entity_field_index_t1
  (entity_id, model_id, field_code, value_string, value_number, creator, tenant_id, deleted)
SELECT e.id, e.model_id, 'mapped_protocol_version', e.custom_fields->>'mapped_protocol_version', NULL, 'seed', {TENANT_ID}, false
FROM ent_protocol_mapping_t1 e
WHERE e.deleted = false AND e.tenant_id = {TENANT_ID}
AND NOT EXISTS (
  SELECT 1 FROM dynamic_entity_field_index_t1 idx
  WHERE idx.deleted = false AND idx.tenant_id = {TENANT_ID}
    AND idx.entity_id = e.id AND idx.field_code = 'mapped_protocol_version'
);

INSERT INTO dynamic_entity_field_index_t1
  (entity_id, model_id, field_code, value_string, value_number, creator, tenant_id, deleted)
SELECT e.id, e.model_id, 'protocol_instruction_ref', NULL, (e.custom_fields->'protocol_instruction_ref'->>'id')::bigint, 'seed', {TENANT_ID}, false
FROM ent_protocol_mapping_t1 e
WHERE e.deleted = false AND e.tenant_id = {TENANT_ID}
AND NOT EXISTS (
  SELECT 1 FROM dynamic_entity_field_index_t1 idx
  WHERE idx.deleted = false AND idx.tenant_id = {TENANT_ID}
    AND idx.entity_id = e.id AND idx.field_code = 'protocol_instruction_ref'
);

UPDATE dynamic_entity_field_index_t1 idx
SET value_number = (e.custom_fields->'action_ref'->>'id')::bigint,
    value_string = NULL,
    deleted = false,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM ent_protocol_mapping_t1 e
WHERE e.deleted = false AND e.tenant_id = {TENANT_ID}
  AND idx.entity_id = e.id AND idx.field_code = 'action_ref'
  AND idx.tenant_id = {TENANT_ID};

UPDATE dynamic_entity_field_index_t1 idx
SET value_string = e.custom_fields->>'mapped_protocol_version',
    value_number = NULL,
    deleted = false,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM ent_protocol_mapping_t1 e
WHERE e.deleted = false AND e.tenant_id = {TENANT_ID}
  AND idx.entity_id = e.id AND idx.field_code = 'mapped_protocol_version'
  AND idx.tenant_id = {TENANT_ID};

UPDATE dynamic_entity_field_index_t1 idx
SET value_number = (e.custom_fields->'protocol_instruction_ref'->>'id')::bigint,
    value_string = NULL,
    deleted = false,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM ent_protocol_mapping_t1 e
WHERE e.deleted = false AND e.tenant_id = {TENANT_ID}
  AND idx.entity_id = e.id AND idx.field_code = 'protocol_instruction_ref'
  AND idx.tenant_id = {TENANT_ID};
"""
    SQL_OUT.write_text(sql, encoding="utf-8")


def require_catalog() -> None:
    raw = psql(
        "SELECT id FROM dynamicbusiness.dynamic_entity_type "
        f"WHERE deleted = false AND tenant_id = {TENANT_ID} AND code = '{ENTITY_TYPE}';"
    ).strip()
    if not raw:
        raise SystemExit("目录 protocol_mapping 还不存在，须先走创建接口建目录，不能用本脚本猜建。")


def api_json(method: str, path: str, body: dict | None = None) -> dict:
    token = os.environ.get("ADMIN_TOKEN", "").strip()
    if not token:
        raise SystemExit("缺少 ADMIN_TOKEN，无法走创建接口。")
    base = os.environ.get("ADMIN_API_BASE", "http://127.0.0.1:3000/admin-api").rstrip("/")
    data = None if body is None else json.dumps(body, ensure_ascii=False).encode("utf-8")
    req = urllib.request.Request(
        f"{base}{path}",
        data=data,
        method=method,
        headers={
            "Authorization": f"Bearer {token}",
            "tenant-id": str(TENANT_ID),
            "Content-Type": "application/json;charset=UTF-8",
            "isEncrypt": "false",
        },
    )
    try:
        with urllib.request.urlopen(req, timeout=30) as resp:
            payload = json.loads(resp.read().decode("utf-8"))
    except urllib.error.HTTPError as exc:
        detail = exc.read().decode("utf-8", errors="replace")
        raise SystemExit(f"HTTP {exc.code} {path}: {detail}") from exc
    code = payload.get("code")
    if code not in (0, 200, "0", "200"):
        raise SystemExit(f"接口失败 {path}: {payload.get('message') or payload}")
    return payload


def ensure_model_via_api() -> int:
    raw = psql(
        "SELECT id FROM dynamicbusiness.dynamic_model "
        f"WHERE deleted = false AND tenant_id = {TENANT_ID} AND entity_type_code = '{ENTITY_TYPE}' "
        "ORDER BY id LIMIT 1;"
    ).strip()
    if raw:
        model_id = int(raw)
        psql(
            "UPDATE dynamicbusiness.dynamic_model "
            f"SET code = '{MODEL_CODE}', name = '{MODEL_NAME}', "
            "governance_status = 'COMPANY', status = 1, updater = 'seed', "
            "update_time = CURRENT_TIMESTAMP "
            f"WHERE id = {model_id} AND code IS DISTINCT FROM '{MODEL_CODE}';"
        )
        return model_id
    category_id = int(
        psql(
            "SELECT id FROM dynamicbusiness.dynamic_category "
            f"WHERE deleted = false AND tenant_id = {TENANT_ID} AND code = '{CATEGORY_CODE}';"
        ).strip()
    )
    payload = api_json(
        "POST",
        "/dynamicbusiness/business/models/create",
        {
            "name": MODEL_NAME,
            "entityTypeCode": ENTITY_TYPE,
            "description": "动作加协议版本对应哪条协议指令，以及参数槽填到空包哪条路径。不记何时返回。",
            "status": 1,
            "categoryIds": [category_id],
            "governanceStatus": "COMPANY",
        },
    )
    model_id = int(payload["data"])
    psql(
        "UPDATE dynamicbusiness.dynamic_model "
        f"SET code = '{MODEL_CODE}', updater = 'seed', update_time = CURRENT_TIMESTAMP "
        f"WHERE id = {model_id};"
    )
    psql(
        "UPDATE dynamicbusiness.dynamic_model_category_relation_t1 "
        f"SET model_code = '{MODEL_CODE}', updater = 'seed', update_time = CURRENT_TIMESTAMP "
        f"WHERE model_id = {model_id} AND tenant_id = {TENANT_ID};"
    )
    print(f"created model id={model_id} code={MODEL_CODE}")
    return model_id


def apply_sql_seed() -> None:
    write_seed_sql()
    env = os.environ.copy()
    env.setdefault("PGPASSWORD", "Coolhomer")
    subprocess.run(
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
            "-f",
            str(SQL_OUT),
        ],
        check=True,
        env=env,
    )


def verify() -> None:
    rows = psql(
        "SELECT e.code || '=' || e.name || ' action=' || (e.custom_fields->'action_ref'->>'id') "
        "|| ' version=' || (e.custom_fields->>'mapped_protocol_version') "
        "|| ' instruction=' || (e.custom_fields->'protocol_instruction_ref'->>'id') "
        "FROM dynamicbusiness.ent_protocol_mapping_t1 e "
        f"WHERE e.deleted = false AND e.tenant_id = {TENANT_ID} "
        "ORDER BY e.code;"
    )
    print(rows.rstrip() or "(no mapping rows)")
    count = psql(
        "SELECT count(*) FROM dynamicbusiness.ent_protocol_mapping_t1 "
        f"WHERE deleted = false AND tenant_id = {TENANT_ID};"
    ).strip()
    if int(count) != len(MAPPINGS):
        raise SystemExit(f"对照行数量不对：期望 {len(MAPPINGS)}，实际 {count}")
    forbidden = psql(
        "SELECT e.code FROM dynamicbusiness.ent_protocol_mapping_t1 e "
        "JOIN dynamicbusiness.ent_action_t1 a "
        "  ON a.id = COALESCE((e.custom_fields->'action_ref'->>'id')::bigint, (e.custom_fields->>'action_ref')::bigint) "
        "WHERE e.deleted = false AND e.tenant_id = 1 "
        "  AND a.code IN ('act-robot-aim');"
    ).strip()
    if forbidden:
        raise SystemExit(f"禁止给对准动作建对接行：{forbidden}")


def apply_live() -> None:
    require_catalog()
    if os.environ.get("ADMIN_TOKEN", "").strip():
        ensure_model_via_api()
    apply_sql_seed()
    verify()
    print(f"done mappings={len(MAPPINGS)}")


def main() -> None:
    cmd = sys.argv[1] if len(sys.argv) > 1 else "write_sql"
    if cmd == "write_sql":
        write_seed_sql()
        print(SQL_OUT)
        return
    if cmd == "apply_live":
        apply_live()
        return
    raise SystemExit("用法：seed_protocol_mapping.py write_sql|apply_live")


if __name__ == "__main__":
    main()
