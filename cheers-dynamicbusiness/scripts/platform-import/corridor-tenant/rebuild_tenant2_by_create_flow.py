# -*- coding: utf-8 -*-
"""按「新建数据类型」重建智慧管廊租户（tenant 2）。

负责：
- 只清 tenant 2 的动态业务数据。tenant 1 不动。
- 四个目录（region / facility / zone / structure）走运行中的创建接口。
- 型号、字段、分类树、实体、绑定从本目录已有 seed 脚本读出后重放。
- 实例里的型号编号改成按型号编码查询，避免重建后旧编号对不上。

不负责：
- 不删除 zhgl 登录账号。
- 不 DROP 管廊三张已有物理表（只清空行），避免丢掉 Flyway 列。
- 不把东营油库、金桥厂区写回去。
- 不在创建接口之外再写一套页面布局。
"""

from __future__ import annotations

import json
import os
import re
import subprocess
import sys
import urllib.error
import urllib.request
from pathlib import Path

import psycopg2
from psycopg2 import sql

ROOT = Path(__file__).resolve().parent
SEED = ROOT / "seed"
SOURCE_SQL = SEED / "dynamic_corridor_spatial_entities.generated.sql"
REBUILT_SQL = SEED / "dynamic_corridor_spatial_entities.by_model_code.sql"

TENANT = 2
KEEP_ENTITY_TABLES = {
    "ent_region_t2",
    "ent_facility_t2",
    "ent_zone_t2",
    "ent_structure_t2",
}
# 登录账号留下，否则建不了目录。业务数据全部删。
KEEP_LOGIN_TABLES = {
    ("public", "system_tenant"),
    ("public", "system_users"),
    ("public", "system_role"),
    ("public", "system_user_role"),
    ("public", "system_role_menu"),
}
SCHEMA = "dynamicbusiness"

# 种子脚本没有目录作用域。创建接口空值会写成站场级，数据管理页就会停在选站场。
# 本租户是管廊管理，四个目录按全网写入。
TYPES = [
    {
        "code": "region",
        "name": "运营区域",
        "sort": 1,
        "alias": "区域",
        "icon": "businessIcon:区域管理.png",
        "description": "管廊运营/管理区域；分类树维护（与管网组织 tenant 的 region 语义独立）",
    },
    {
        "code": "facility",
        "name": "管廊设施",
        "sort": 2,
        "alias": "设施",
        "icon": "businessIcon:设施管理.png",
        "description": "管廊工程、监控中心等设施点；REF_REGION 挂运营区域",
    },
    {
        "code": "zone",
        "name": "分区",
        "sort": 3,
        "alias": "分区",
        "icon": "businessIcon:区域管理.png",
        "description": "管廊设施内空间分区：防火区、管廊段、舱段等；REF_FACILITY 必填",
    },
    {
        "code": "structure",
        "name": "构筑物",
        "sort": 4,
        "alias": "构筑物",
        "icon": "businessIcon:设施管理.png",
        "description": "管廊设施上的物理构筑物：舱室、口部、风亭等；REF_FACILITY 必填，可选 REF_ZONE",
    },
]

SEED_FILES = [
    "dynamic_corridor_spatial_entity_types.sql",
    "dynamic_corridor_structure.sql",
    "dynamic_corridor_zone_structure_split.sql",
    "dynamic_corridor_field_library.sql",
    "dynamic_corridor_model_fields.sql",
    "dynamic_corridor_base_field_labels.sql",
]

FIXED_MODEL_CODES = {
    "FAC-CORRIDOR-WH-GGGL": "MODEL-FACILITY-UT-TUNNEL",
    "ZONE-CORRIDOR-WH-GGGL-ROOT": "MODEL-ZONE-UT-CORRIDOR-ROOT",
}

ENT_MODEL = re.compile(
    r"(VALUES \(\s*\d+,\s*2,\s*'(?:facility|zone|structure)',\s*)(\d+)(,)"
)
LINK_MODEL = re.compile(
    r"(INSERT INTO dynamic_category_entity_link_t2 \([^;]*?VALUES \(\s*\d+,\s*\d+,\s*\d+,\s*)(\d+)(\s*,)"
)
COMMENT_MODEL = re.compile(r"--[^\n]*(MODEL-[A-Z0-9-]+)")
NEXT_ENTITY_MODEL = re.compile(r"'(?:facility|zone|structure)',\s*(\d+),")
ENTITY_CODE_NEAR_MODEL = re.compile(
    r"'(?:facility|zone|structure)',\s*(\d+),[\s\S]{0,240}?'([A-Z0-9-]+)'"
)


def connect():
    return psycopg2.connect(
        host=os.environ.get("PGHOST", "127.0.0.1"),
        port=int(os.environ.get("PGPORT", "5432")),
        dbname=os.environ.get("PGDATABASE", "sinopec"),
        user=os.environ.get("PGUSER", "postgres"),
        password=os.environ.get("PGPASSWORD", "Coolhomer"),
    )


def model_subquery(code: str) -> str:
    safe = code.replace("'", "")
    return (
        f"(SELECT id FROM dynamic_model WHERE tenant_id = {TENANT} "
        f"AND code = '{safe}' AND deleted = false)"
    )


def build_model_map(text: str) -> dict[int, str]:
    """从现有实例脚本读出「旧型号编号 → 型号编码」。"""
    mapping: dict[int, str] = {}
    for match in ENTITY_CODE_NEAR_MODEL.finditer(text):
        entity_code = match.group(2)
        if entity_code in FIXED_MODEL_CODES:
            mapping[int(match.group(1))] = FIXED_MODEL_CODES[entity_code]
    for comment in COMMENT_MODEL.finditer(text):
        window = text[comment.end() : comment.end() + 2500]
        nxt = NEXT_ENTITY_MODEL.search(window)
        if not nxt:
            continue
        model_id = int(nxt.group(1))
        code = comment.group(1)
        previous = mapping.get(model_id)
        if previous and previous != code:
            raise SystemExit(f"型号编号 {model_id} 同时对应 {previous} 和 {code}")
        mapping[model_id] = code
    missing = []
    for match in ENT_MODEL.finditer(text):
        model_id = int(match.group(2))
        if model_id not in mapping:
            missing.append(model_id)
    if missing:
        raise SystemExit("这些型号编号在脚本里没有型号编码: " + ",".join(map(str, sorted(set(missing)))))
    return mapping


def rewrite_instances(text: str, mapping: dict[int, str]) -> str:
    """把实例插入里的旧型号编号换成按编码查询。构筑物写入租户物理表。"""

    def repl(match: re.Match) -> str:
        model_id = int(match.group(2))
        return match.group(1) + model_subquery(mapping[model_id]) + match.group(3)

    rewritten = ENT_MODEL.sub(repl, text)
    rewritten = LINK_MODEL.sub(repl, rewritten)
    rewritten = re.sub(r"\bent_structure\b(?!_t\d+)", "ent_structure_t2", rewritten)
    # 现网物理表列是 region_id / facility_id / zone_id。脚本里的 fld_base_* 列名对不上。
    rewritten = rewritten.replace("fld_base_facility_ref_region", "region_id")
    rewritten = rewritten.replace("fld_base_zone_ref_facility", "facility_id")
    rewritten = rewritten.replace("fld_base_structure_ref_facility", "facility_id")
    rewritten = rewritten.replace("fld_base_structure_ref_zone", "zone_id")
    leftover = sorted({int(item) for item in ENT_MODEL.findall(rewritten)})
    if leftover:
        raise SystemExit(f"仍有未替换的型号编号: {leftover[:12]}")
    return rewritten


def list_tables(cur, schema: str) -> list[str]:
    cur.execute(
        """
        SELECT c.relname
        FROM pg_class c
        JOIN pg_namespace n ON n.oid = c.relnamespace
        WHERE n.nspname = %s AND c.relkind = 'r'
        ORDER BY c.relname
        """,
        (schema,),
    )
    return [row[0] for row in cur.fetchall()]


def has_tenant_column(cur, schema: str, table: str) -> bool:
    cur.execute(
        """
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = %s AND table_name = %s AND column_name = 'tenant_id'
        """,
        (schema, table),
    )
    return cur.fetchone() is not None


def require_keep_columns(cur) -> None:
    needed = {
        "ent_facility_t2": "region_id",
        "ent_zone_t2": "facility_id",
        "ent_structure_t2": "zone_id",
    }
    for table, column in needed.items():
        cur.execute(
            """
            SELECT 1 FROM information_schema.columns
            WHERE table_schema = %s AND table_name = %s AND column_name = %s
            """,
            (SCHEMA, table, column),
        )
        if cur.fetchone() is None:
            raise SystemExit(f"{table} 缺少列 {column}，停止清理")


def save_column_mappings(conn) -> dict[str, str]:
    """记下和现网物理表一致的列映射。种子脚本会写成不存在的 fld_base_* 列名。"""
    cur = conn.cursor()
    cur.execute(
        """
        SELECT entity_type_code, physical_column_mapping::text
        FROM dynamicbusiness.dynamic_entity_type_config
        WHERE tenant_id = %s AND deleted = false
          AND entity_type_code IN ('region', 'facility', 'zone', 'structure')
          AND physical_column_mapping IS NOT NULL
        """,
        (TENANT,),
    )
    return {code: mapping for code, mapping in cur.fetchall()}


def restore_column_mappings(conn, mappings: dict[str, str]) -> None:
    if not mappings:
        return
    cur = conn.cursor()
    for code, mapping in mappings.items():
        cur.execute(
            """
            UPDATE dynamicbusiness.dynamic_entity_type_config
            SET physical_column_mapping = %s::jsonb, updater = 'corridor-seed', update_time = CURRENT_TIMESTAMP
            WHERE tenant_id = %s AND entity_type_code = %s AND deleted = false
            """,
            (mapping, TENANT, code),
        )
        cur.execute(
            """
            UPDATE dynamicbusiness.dynamic_entity_type
            SET physical_column_mapping = %s::jsonb, updater = 'corridor-seed', update_time = CURRENT_TIMESTAMP
            WHERE tenant_id = %s AND code = %s AND deleted = false
            """,
            (mapping, TENANT, code),
        )
    conn.commit()
    print(f"restored column mappings: {', '.join(sorted(mappings))}")


def wipe_tenant2(conn) -> None:
    """清 tenant 2。保留三张管廊物理表的列结构，只删行。"""
    cur = conn.cursor()
    cur.execute("SET statement_timeout = '30min'")
    require_keep_columns(cur)
    tables = list_tables(cur, SCHEMA)
    suffixed = [name for name in tables if name.endswith("_t2")]
    extra_entity = [
        name
        for name in suffixed
        if name.startswith("ent_") and name not in KEEP_ENTITY_TABLES
    ]
    cur.execute("SET session_replication_role = replica")
    if suffixed:
        cur.execute(
            sql.SQL("TRUNCATE TABLE {}").format(
                sql.SQL(", ").join(sql.Identifier(SCHEMA, name) for name in suffixed)
            )
        )
    for name in extra_entity:
        cur.execute(
            sql.SQL("DROP TABLE IF EXISTS {}.{}").format(sql.Identifier(SCHEMA), sql.Identifier(name))
        )
    cur.execute(
        """
        SELECT table_schema, table_name
        FROM information_schema.columns
        WHERE column_name = 'tenant_id'
          AND table_schema NOT IN ('pg_catalog', 'information_schema')
        GROUP BY table_schema, table_name
        ORDER BY 1, 2
        """
    )
    deleted = 0
    for schema, table in cur.fetchall():
        if (schema, table) in KEEP_LOGIN_TABLES:
            continue
        if schema == SCHEMA and table.endswith("_t2"):
            continue
        if table in {"flyway_schema_history"}:
            continue
        cur.execute(
            sql.SQL("DELETE FROM {}.{} WHERE tenant_id = %s").format(
                sql.Identifier(schema), sql.Identifier(table)
            ),
            (TENANT,),
        )
        deleted += cur.rowcount
    cur.execute("SET session_replication_role = origin")
    conn.commit()
    print(
        f"deleted tenant {TENANT} rows: {deleted}; "
        f"truncated {len(suffixed)}; dropped {len(extra_entity)}"
    )


def login_password() -> str:
    env = os.environ.get("CORRIDOR_ZHGL_PASSWORD")
    if env:
        return env
    readme = (ROOT / "README.md").read_text(encoding="utf-8")
    match = re.search(r"本环境为 `([^`]+)`", readme)
    if not match:
        raise SystemExit("README 里没有 zhgl 密码")
    return match.group(1)


def http_json(url: str, payload: dict, headers: dict) -> dict:
    data = json.dumps(payload).encode("utf-8")
    request = urllib.request.Request(url, data=data, headers={"Content-Type": "application/json", **headers})
    try:
        with urllib.request.urlopen(request, timeout=120) as response:
            body = response.read().decode("utf-8")
    except urllib.error.HTTPError as exc:
        detail = exc.read().decode("utf-8", errors="replace")
        raise SystemExit(f"HTTP {exc.code} {url} {detail[:500]}") from exc
    return json.loads(body)


def unwrap(body: dict):
    if "captchaKey" in body:
        return body
    code = body.get("code")
    if code not in (0, 200, None):
        raise SystemExit(f"接口失败: {body.get('msg') or body.get('message') or body}")
    return body.get("data", body)


def gateway() -> str:
    return os.environ.get("GATEWAY", "http://127.0.0.1:15080").rstrip("/")


def redis_get(key: str) -> str:
    env = os.environ.copy()
    env["REDISCLI_AUTH"] = os.environ.get("REDIS_PASSWORD", "123456")
    result = subprocess.run(
        ["redis-cli", "-h", "127.0.0.1", "-p", "6379", "GET", key],
        check=True,
        capture_output=True,
        text=True,
        env=env,
    )
    return result.stdout.strip()


def login() -> str:
    captcha = unwrap(http_json(f"{gateway()}/admin-api/system/captcha/get", {}, {"tenant-id": str(TENANT)}))
    key = captcha["captchaKey"]
    answer = redis_get(f"system:captcha:arithmetic:{key}")
    if not answer:
        raise SystemExit("验证码没有写入 Redis")
    logged = unwrap(
        http_json(
            f"{gateway()}/admin-api/system/auth/login",
            {
                "username": "zhgl",
                "password": login_password(),
                "captchaVerification": f"{key}@{answer}",
            },
            {"tenant-id": str(TENANT)},
        )
    )
    token = logged.get("accessToken") if isinstance(logged, dict) else None
    if not token:
        raise SystemExit("登录没有返回 accessToken")
    return token


def create_types(token: str) -> None:
    headers = {"tenant-id": str(TENANT), "Authorization": f"Bearer {token}"}
    for item in TYPES:
        payload = {
            **item,
            "status": "active",
            "storageType": "DEDICATED",
            "entryKind": "NATIVE",
            "workScope": "NETWORK",
            "associationFields": "{}",
        }
        created = unwrap(
            http_json(f"{gateway()}/admin-api/dynamicbusiness/entity-type/create", payload, headers)
        )
        print(f"created {item['code']} id={created}")


def run_psql(path: Path) -> None:
    env = os.environ.copy()
    env.setdefault("PGPASSWORD", "Coolhomer")
    env["PGCLIENTENCODING"] = "UTF8"
    print(f">> psql {path.name}")
    subprocess.run(
        [
            "psql",
            "-h",
            env.get("PGHOST", "127.0.0.1"),
            "-p",
            env.get("PGPORT", "5432"),
            "-U",
            env.get("PGUSER", "postgres"),
            "-d",
            env.get("PGDATABASE", "sinopec"),
            "-v",
            "ON_ERROR_STOP=1",
            "-v",
            f"corridor_tenant_id={TENANT}",
            "-f",
            str(path),
        ],
        check=True,
        env=env,
    )


def advance_sequences(conn) -> None:
    """显式编号插入后，只把落后的序列抬到当前最大编号。"""
    cur = conn.cursor()
    tables = [
        "dynamic_category",
        "dynamic_category_type",
        "dynamic_category_entity_link_t2",
        "ent_facility_t2",
        "ent_zone_t2",
        "ent_structure_t2",
        "dynamic_model",
    ]
    for table in tables:
        cur.execute("SELECT pg_get_serial_sequence(%s, 'id')", (f"{SCHEMA}.{table}",))
        seq = cur.fetchone()[0]
        if not seq:
            continue
        cur.execute(
            sql.SQL("SELECT COALESCE(MAX(id), 1) FROM {}.{}").format(
                sql.Identifier(SCHEMA), sql.Identifier(table)
            )
        )
        max_id = cur.fetchone()[0]
        cur.execute(sql.SQL("SELECT last_value FROM {}").format(sql.Identifier(*seq.split("."))))
        last_value = cur.fetchone()[0]
        if max_id > last_value:
            cur.execute("SELECT setval(%s, %s)", (seq, max_id))
    conn.commit()


def verify(conn) -> None:
    cur = conn.cursor()
    cur.execute(
        """
        SELECT code, name, work_scope, dedicated_table_name, data_layout_id, model_layout_id
        FROM dynamicbusiness.dynamic_entity_type
        WHERE tenant_id = %s AND deleted = false
        ORDER BY sort, code
        """,
        (TENANT,),
    )
    rows = cur.fetchall()
    print("entity types:")
    for row in rows:
        print(" ", row)
    cur.execute(
        """
        SELECT code, name FROM dynamicbusiness.ent_facility_t2
        WHERE tenant_id = %s AND deleted = false
        """,
        (TENANT,),
    )
    print("facilities:", cur.fetchall())
    cur.execute(
        "SELECT COUNT(*) FROM dynamicbusiness.ent_zone_t2 WHERE tenant_id = %s AND deleted = false",
        (TENANT,),
    )
    print("zones:", cur.fetchone()[0])
    cur.execute(
        "SELECT COUNT(*) FROM dynamicbusiness.ent_structure_t2 WHERE tenant_id = %s AND deleted = false",
        (TENANT,),
    )
    print("structures:", cur.fetchone()[0])
    cur.execute(
        """
        SELECT category_mode, top_level_category_id
        FROM dynamicbusiness.dynamic_category_type
        WHERE tenant_id = %s AND category_type_code = 'zone' AND deleted = false
        """,
        (TENANT,),
    )
    print("zone category type:", cur.fetchone())


def main() -> None:
    text = SOURCE_SQL.read_text(encoding="utf-8")
    mapping = build_model_map(text)
    print(f"model codes from script: {len(mapping)}")
    REBUILT_SQL.write_text(rewrite_instances(text, mapping), encoding="utf-8")
    print(f"wrote {REBUILT_SQL.name}")

    token = login()
    print("login ok")

    conn = connect()
    try:
        mappings = save_column_mappings(conn)
        print(f"saved column mappings: {', '.join(sorted(mappings))}")
        wipe_tenant2(conn)
        create_types(token)
        for name in SEED_FILES:
            run_psql(SEED / name)
        restore_column_mappings(conn, mappings)
        run_psql(REBUILT_SQL)
        advance_sequences(conn)
        verify(conn)
    finally:
        conn.close()


if __name__ == "__main__":
    try:
        main()
    except subprocess.CalledProcessError as exc:
        sys.exit(exc.returncode)
