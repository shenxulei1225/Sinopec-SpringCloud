#!/usr/bin/env python3
"""把《OpenAPI pro 接口说明书（感知联网平台）V1.23》第三章的每条接口收成协议指令。

只收正文里写了请求方式和 /VIID/ 路径的接口。附录枚举不收。
已有的登录 v2、保活、退出、查摄像机目录、查门禁目录按路径保留，不另建一条。
"""

from __future__ import annotations

import json
import re
from pathlib import Path

from pypdf import PdfReader

PDF = Path(
    "/opt/Sinopec/智慧管廊/OpenAPI pro接口说明书（感知联网平台）V1.23/"
    "OpenAPI pro接口说明书（感知联网平台）V1.23.pdf"
)
OUT = Path(__file__).with_name("dynamic_protocol_uniview_openapi_catalog.sql")

# 这五条已经按说明书写过字段。同一方法加路径不再插入第二条。
KEEP_PATHS = {
    ("POST", "/VIID/login/v2"),
    ("POST", "/VIID/hadesadapter/user/keepalive"),
    ("POST", "/VIID/logout"),
    ("GET", "/VIID/query"),
}

HEADING = re.compile(r"^(\d+(?:\.\d+){3,})([^\d].*)$")
METHOD = re.compile(r"请求方式\s+(GET|POST|PUT|DELETE|HEAD)\b", re.I)
URL = re.compile(r"https?://[^\s]+/VIID/[^\s]*", re.I)
WATERMARK = re.compile(r"guest\S+\s+\d{4}/\d{2}/\d{2}\s+\d{2}:\d{2}:\d{2}")
REQ_FIELD = re.compile(
    r"^([A-Za-z][A-Za-z0-9_]*)\s+(是|否)\s+([A-Za-z][A-Za-z0-9]*)\s*(.*)$"
)
RESP_FIELD = re.compile(
    r"^([A-Za-z][A-Za-z0-9_]*)\s+(Int|Integer|String|Json|JsonArray|Short|Long|Boolean|Object|Number)\s*(.*)$"
)
NEST = re.compile(r"^以下是\s+([A-Za-z][A-Za-z0-9_]*)\s+中的字段")


def clean_line(line: str) -> str:
    line = WATERMARK.sub("", line)
    line = line.replace("\u00a0", " ").strip()
    if line in {"感知联网平台"}:
        return ""
    if re.fullmatch(r"\d+", line):
        return ""
    return line


def pages_text() -> str:
    reader = PdfReader(str(PDF))
    chunks: list[str] = []
    # 正文从「3 服务接口列表」到附录之前。附录从文档第 595 页起。
    for index in range(9, 600):
        raw = reader.pages[index].extract_text() or ""
        lines = [clean_line(line) for line in raw.splitlines()]
        chunks.append("\n".join(line for line in lines if line))
    return "\n".join(chunks)


def section_blocks(text: str) -> list[tuple[str, str, str]]:
    lines = text.splitlines()
    blocks: list[tuple[str, str, list[str]]] = []
    current: tuple[str, str, list[str]] | None = None
    for line in lines:
        if line.startswith("4 附录"):
            break
        match = HEADING.match(line)
        if match:
            if current:
                blocks.append(current)
            title = re.sub(r"\s+", "", match.group(2))
            title = re.sub(r"[\.。·]{2,}.*$", "", title).strip()
            current = (match.group(1), title[:80] or match.group(1), [])
            continue
        if current:
            current[2].append(line)
    if current:
        blocks.append(current)
    return [(number, title, "\n".join(body)) for number, title, body in blocks]


def viid_path(url: str) -> str:
    path = re.sub(r"^https?://[^/]+", "", url, flags=re.I)
    path = path.split("?")[0]
    path = path.rstrip(".,;，。)")
    if not path.startswith("/VIID/"):
        return ""
    return path


def slice_between(body: str, start: str, ends: tuple[str, ...]) -> str:
    at = body.find(start)
    if at < 0:
        return ""
    rest = body[at + len(start) :]
    stop = len(rest)
    for end in ends:
        found = rest.find(end)
        if found >= 0:
            stop = min(stop, found)
    return rest[:stop]


def request_fields(body: str) -> list[dict]:
    chunk = slice_between(body, "3. 参数描述", ("4. 请求参数示例", "4.请求参数示例", "5. 返回结果"))
    fields: list[dict] = []
    prefix = ""
    for line in chunk.splitlines():
        nest = NEST.match(line)
        if nest:
            prefix = nest.group(1) + "."
            continue
        match = REQ_FIELD.match(line)
        if not match:
            continue
        name, required, kind, desc = match.groups()
        if name in {"请求字段", "请求值", "项目"}:
            continue
        fields.append(
            {
                "code": name,
                "path": f"{prefix}{name}",
                "type": kind.lower(),
                "label": name,
                "required": required == "是",
                "description": desc.strip() or None,
            }
        )
    return fields


def response_fields(body: str) -> list[dict]:
    chunk = slice_between(body, "6. 返回字段说明", ("3.",))
    # 下一条接口的「1. 描述」会截断。上面的 3. 可能误切本条没有的内容，改为按行停。
    fields: list[dict] = []
    prefix = ""
    for line in chunk.splitlines():
        if HEADING.match(line):
            break
        nest = NEST.match(line)
        if nest:
            prefix = nest.group(1) + "."
            continue
        match = RESP_FIELD.match(line)
        if not match:
            continue
        name, kind, desc = match.groups()
        fields.append(
            {
                "code": name,
                "path": f"{prefix}{name}",
                "type": kind.lower(),
                "label": name,
                "required": name in {"ErrCode", "ErrMsg"},
                "description": desc.strip() or None,
            }
        )
    return fields


def command_of(number: str, title: str, body: str) -> dict | None:
    method_match = METHOD.search(body)
    url_match = URL.search(body)
    if not method_match or not url_match:
        return None
    method = method_match.group(1).upper()
    path = viid_path(url_match.group(0))
    if not path:
        return None
    if (method, path) in KEEP_PATHS:
        return None
    outbound = request_fields(body)
    inbound = response_fields(body)
    header = {
        "code": "header",
        "path": "header",
        "type": "object",
        "label": "请求头",
        "required": True,
        "description": "Authorization 填登录拿到的 AccessToken。种子不预填账号。",
    }
    outbound_fields = [header, *outbound]
    query_or_body: dict = {}
    for field in outbound:
        query_or_body[field["code"]] = ""
    command_json = {
        "outbound": {
            "httpMethod": method,
            "path": path,
            "header": {"Authorization": ""},
            "body": query_or_body,
        },
        "inboundSuccess": {"ErrCode": 0, "ErrMsg": ""},
        "inboundFailure": {"ErrCode": 0, "ErrMsg": ""},
    }
    for field in inbound:
        if field["code"] not in {"ErrCode", "ErrMsg"}:
            command_json["inboundSuccess"][field["code"]] = ""
    code = "proto-uniview-openapi-" + number.replace(".", "-")
    return {
        "code": code[:100],
        "name": title[:255],
        "section": number,
        "method": method,
        "path": path,
        "fields": {
            "opcode": 0,
            "command_direction": "downlink",
            "exchange_mode": "sync_wait_result",
            "protocol_version": ["uniview-openapi-1.23"],
            "communication_protocol": "https",
            "http_method": method,
            "http_path": path,
            "command_json": command_json,
            "field_description_json": {
                "outbound": outbound_fields,
                "inboundSuccess": inbound
                or [
                    {
                        "code": "ErrCode",
                        "path": "ErrCode",
                        "type": "integer",
                        "label": "错误码",
                        "required": True,
                        "description": "0 表示成功。",
                    },
                    {
                        "code": "ErrMsg",
                        "path": "ErrMsg",
                        "type": "string",
                        "label": "错误说明",
                        "required": False,
                        "description": None,
                    },
                ],
                "inboundFailure": [
                    {
                        "code": "ErrCode",
                        "path": "ErrCode",
                        "type": "integer",
                        "label": "错误码",
                        "required": True,
                        "description": None,
                    },
                    {
                        "code": "ErrMsg",
                        "path": "ErrMsg",
                        "type": "string",
                        "label": "错误说明",
                        "required": True,
                        "description": None,
                    },
                ],
            },
            "sample_json": {
                "outbound": command_json["outbound"],
                "inboundSuccess": {"ErrCode": 0, "ErrMsg": ""},
            },
        },
    }


def sql_literal(value: str) -> str:
    return value.replace("'", "''")


def main() -> None:
    text = pages_text()
    commands: list[dict] = []
    seen_codes: set[str] = set()
    seen_ops: set[tuple[str, str]] = set()
    skipped = 0
    for number, title, body in section_blocks(text):
        command = command_of(number, title, body)
        if command is None:
            if METHOD.search(body) or URL.search(body):
                skipped += 1
            continue
        key = (command["method"], command["path"])
        if command["code"] in seen_codes or key in seen_ops:
            continue
        seen_codes.add(command["code"])
        seen_ops.add(key)
        commands.append(command)

    rows = []
    for command in commands:
        payload = json.dumps(command["fields"], ensure_ascii=False, separators=(",", ":"))
        rows.append(
            "  ('{code}', '{name}', '{method}', '{path}', $uv${payload}$uv$::jsonb)".format(
                code=sql_literal(command["code"]),
                name=sql_literal(command["name"]),
                method=sql_literal(command["method"]),
                path=sql_literal(command["path"]),
                payload=payload,
            )
        )

    sql = f"""-- OpenAPI pro 接口说明书（感知联网平台）V1.23 的服务接口。
-- 这一本型号叫 OpenAPI pro V1.23。分类叫感知联网平台。
-- 通讯方式列用平台已有的 https，说明书上的传输是 HTTP，基址 http://VM:8088/VIID。
-- 登录 v2、保活、退出、查摄像机目录、查门禁目录已在 dynamic_protocol_uniview_https.sql，这里不重复插入。
-- 附录里的取值表不是接口，不写入。
SET search_path TO dynamicbusiness, public;

BEGIN;

CREATE TEMP TABLE uniview_openapi_command (
  code text,
  name text,
  http_method text,
  http_path text,
  custom_fields jsonb
) ON COMMIT DROP;

INSERT INTO uniview_openapi_command (
  code, name, http_method, http_path, custom_fields
) VALUES
{",\n".join(rows)};

DO $$
DECLARE
  tid bigint;
  ent_table text;
  cat_rel_table text;
  idx_table text;
  model_id bigint;
  cat_id bigint;
BEGIN
  FOREACH tid IN ARRAY ARRAY[1::bigint, 123::bigint]
  LOOP
    ent_table := format('ent_data_protocol_t%s', tid);
    cat_rel_table := format('dynamic_entity_category_relation_t%s', tid);
    idx_table := format('dynamic_entity_field_index_t%s', tid);
    IF to_regclass('dynamicbusiness.' || ent_table) IS NULL THEN
      RAISE NOTICE 'skip tenant %', tid;
      CONTINUE;
    END IF;

    UPDATE dynamic_category
    SET name = '感知联网平台',
        description = '宇视感知联网平台 OpenAPI pro V1.23 的全部服务接口',
        updater = 'seed',
        update_time = CURRENT_TIMESTAMP
    WHERE deleted = false
      AND tenant_id = tid
      AND code = 'cat-protocol-uniview-video';

    UPDATE dynamic_model
    SET name = 'OpenAPI pro V1.23',
        description = '宇视《OpenAPI pro 接口说明书（感知联网平台）V1.23》的服务接口。基址 http://VM:8088/VIID。通讯方式列用 HTTPS，表示这组 HTTP 接口。',
        updater = 'seed',
        update_time = CURRENT_TIMESTAMP
    WHERE deleted = false
      AND tenant_id = tid
      AND code = 'data_protocol_uniview_https';

    SELECT id INTO model_id
    FROM dynamic_model
    WHERE deleted = false AND tenant_id = tid AND code = 'data_protocol_uniview_https'
    LIMIT 1;
    SELECT id INTO cat_id
    FROM dynamic_category
    WHERE deleted = false AND tenant_id = tid AND code = 'cat-protocol-uniview-video'
    LIMIT 1;
    IF model_id IS NULL OR cat_id IS NULL THEN
      RAISE NOTICE 'skip tenant %: model or category missing', tid;
      CONTINUE;
    END IF;

    EXECUTE format(
      'INSERT INTO %I (
         tenant_id, entity_type_code, model_id, name, code, status, custom_fields,
         command_direction, protocol_version, communication_protocol, creator, deleted
       )
       SELECT
         $1, ''data_protocol'', $2, c.name, c.code, 1,
         c.custom_fields - ''protocol_version'' - ''communication_protocol'',
         ''downlink'', ''["uniview-openapi-1.23"]''::jsonb, ''https'', ''seed'', false
       FROM uniview_openapi_command c
       WHERE NOT EXISTS (
         SELECT 1 FROM %I e
         WHERE e.tenant_id = $1 AND e.code = c.code AND e.deleted = false
       )',
      ent_table, ent_table
    ) USING tid, model_id;

    EXECUTE format(
      'UPDATE %I e
       SET
         name = c.name,
         model_id = $1,
         command_direction = ''downlink'',
         protocol_version = ''["uniview-openapi-1.23"]''::jsonb,
         communication_protocol = ''https'',
         status = 1,
         deleted = false,
         custom_fields = c.custom_fields - ''protocol_version'' - ''communication_protocol'',
         updater = ''seed'',
         update_time = CURRENT_TIMESTAMP
       FROM uniview_openapi_command c
       WHERE e.tenant_id = $2 AND e.code = c.code',
      ent_table
    ) USING model_id, tid;

    EXECUTE format(
      'INSERT INTO %I (
         tenant_id, category_id, entity_type_code, entity_id, domain, sort, creator, deleted
       )
       SELECT $1, $2, ''data_protocol'', e.id, NULL, 0, ''seed'', false
       FROM uniview_openapi_command c
       JOIN %I e ON e.deleted = false AND e.tenant_id = $1 AND e.code = c.code
       WHERE NOT EXISTS (
         SELECT 1 FROM %I r
         WHERE r.tenant_id = $1
           AND r.entity_type_code = ''data_protocol''
           AND r.entity_id = e.id
           AND r.category_id = $2
           AND r.deleted = false
       )',
      cat_rel_table, ent_table, cat_rel_table
    ) USING tid, cat_id;

    EXECUTE format(
      'INSERT INTO %I (
         entity_id, model_id, field_code, value_string, value_number, creator, tenant_id, deleted
       )
       SELECT e.id, e.model_id, v.field_code, NULLIF(v.value_string, ''''), NULL, ''seed'', $1, false
       FROM uniview_openapi_command c
       JOIN %I e ON e.deleted = false AND e.tenant_id = $1 AND e.code = c.code
       CROSS JOIN LATERAL (VALUES
         (''http_method'', c.http_method),
         (''http_path'', c.http_path),
         (''exchange_mode'', ''sync_wait_result'')
       ) AS v(field_code, value_string)
       WHERE NOT EXISTS (
         SELECT 1 FROM %I idx
         WHERE idx.deleted = false AND idx.tenant_id = $1
           AND idx.entity_id = e.id AND idx.field_code = v.field_code
       )',
      idx_table, ent_table, idx_table
    ) USING tid;

    EXECUTE format(
      'UPDATE %I idx
       SET value_string = v.value_string, model_id = e.model_id, deleted = false,
           updater = ''seed'', update_time = CURRENT_TIMESTAMP
       FROM uniview_openapi_command c
       JOIN %I e ON e.deleted = false AND e.tenant_id = $1 AND e.code = c.code
       CROSS JOIN LATERAL (VALUES
         (''http_method'', c.http_method),
         (''http_path'', c.http_path),
         (''exchange_mode'', ''sync_wait_result'')
       ) AS v(field_code, value_string)
       WHERE idx.tenant_id = $1 AND idx.entity_id = e.id AND idx.field_code = v.field_code',
      idx_table, ent_table
    ) USING tid;

    RAISE NOTICE 'openapi catalog tenant % commands %', tid, (SELECT count(*) FROM uniview_openapi_command);
  END LOOP;
END $$;

COMMIT;
"""
    OUT.write_text(sql, encoding="utf-8")
    print(f"commands {len(commands)} skipped_partial {skipped} bytes {OUT.stat().st_size}")
    for command in commands[:8]:
        print(command["section"], command["method"], command["path"], command["name"])
    print("--- tail ---")
    for command in commands[-5:]:
        print(command["section"], command["method"], command["path"], command["name"])


if __name__ == "__main__":
    main()
