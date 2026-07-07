#!/usr/bin/env python3
"""Export dynamicbusiness metadata into system / corridor / station packages."""

from __future__ import annotations

import os
import shutil
from datetime import date
from pathlib import Path

import psycopg2

from seed_codegen import (
    TENANT_ID,
    compose_business_portal,
    compose_business_types,
    compose_capabilities,
    compose_field_groups,
    compose_fields,
    compose_industry_field_library,
    compose_model_categories,
    compose_model_library,
    render_base_fields,
    render_field_library,
)

ROOT = Path(__file__).resolve().parent.parent
IMPORT_ROOT = ROOT / "scripts/platform-import"
FLYWAY_DIR = (
    ROOT
    / "cheers-module-dynamicbusiness-server/src/main/resources/db/migration/dynamicbusiness"
)
V1_SCHEMA = FLYWAY_DIR / "V1__init_dynamicbusiness_schema.sql"

STATION_MODEL_IDS = (9001, 9002)
STATION_CATEGORY_IDS = tuple(range(900010, 900103))
STATION_MODEL_CODES = ("MODEL-REGION-SITE", "MODEL-REGION-TANK-GROUP")

EQUIPMENT_BUSINESS = "equipment"
# 历史上挂在独立业务类型下的通用设备模型，导出时归入 equipment 统一维护
LEGACY_DEVICE_BUSINESS = ("data_collection", "custom_6128")
SHARED_DEVICE_BUSINESS = (EQUIPMENT_BUSINESS,) + LEGACY_DEVICE_BUSINESS
CORRIDOR_BUSINESS = {
    "region",
    "pipeline",
    "customer",
    "billing",
    "patrol",
    "emergency",
    "emergency_team",
    "emergency_resource",
    "maintenance",
    "fault",
    "spare_parts",
    "inspection_item",
    "inspection_point",
    "route",
}


def connect():
    return psycopg2.connect(
        host=os.environ.get("PGHOST", "127.0.0.1"),
        port=os.environ.get("PGPORT", "5432"),
        dbname=os.environ.get("PGDATABASE", "sinopec"),
        user=os.environ.get("PGUSER", "postgres"),
        password=os.environ.get("PGPASSWORD", "Coolhomer"),
    )


def fetch_rows(cur, sql: str, params=None) -> list[dict]:
    cur.execute("SET search_path TO dynamicbusiness")
    cur.execute(sql, params or ())
    cols = [d[0] for d in cur.description]
    return [dict(zip(cols, row)) for row in cur.fetchall()]


def file_header(title: str, deps: str) -> str:
    return f"""-- ============================================================================
-- {title}
-- Generated: {date.today().isoformat()} by scripts/export-platform-import.py
--
-- 约定：不写 surrogate id；幂等键为 code / field_code / page_code。
{deps}
-- ============================================================================

SET search_path TO dynamicbusiness;

"""


def write_sql(path: Path, title: str, deps: str, body: str):
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(file_header(title, deps) + body, encoding="utf-8")


def write_schema(path: Path):
    text = V1_SCHEMA.read_text(encoding="utf-8")
    if "SET search_path TO dynamicbusiness;" in text:
        body = text.split("SET search_path TO dynamicbusiness;", 1)[-1].strip()
    else:
        body = text
    write_sql(
        path,
        "系统共用 · 01 建表（dynamicbusiness 全表 DDL）",
        "-- 与 Flyway V1__init_dynamicbusiness_schema.sql 同源\n-- 新平台第一步：先执行本文件",
        body,
    )


def fetch_all_entity_type_codes(cur) -> list[str]:
    rows = fetch_rows(
        cur,
        """
        SELECT code FROM dynamic_entity_type
        WHERE deleted = false AND tenant_id = %s
        ORDER BY code
        """,
        (TENANT_ID,),
    )
    return [r["code"] for r in rows]


def fetch_business_types(cur, codes: list[str]) -> list[dict]:
    return fetch_rows(
        cur,
        """
        SELECT * FROM dynamic_entity_type
        WHERE deleted = false AND tenant_id = %s AND code = ANY(%s)
        ORDER BY code
        """,
        (TENANT_ID, codes),
    )


def fetch_business_type_configs(cur, codes: list[str]) -> list[dict]:
    return fetch_rows(
        cur,
        """
        SELECT * FROM dynamic_entity_type_config
        WHERE deleted = false AND tenant_id = %s AND entity_type_code = ANY(%s)
        ORDER BY entity_type_code
        """,
        (TENANT_ID, codes),
    )


def fetch_business_type_relations(cur, codes: list[str]) -> list[dict]:
    return fetch_rows(
        cur,
        """
        SELECT * FROM dynamic_entity_type_relation
        WHERE deleted = false AND tenant_id = %s
          AND source_entity_type_code = ANY(%s)
          AND target_entity_type_code = ANY(%s)
        ORDER BY source_entity_type_code, target_entity_type_code
        """,
        (TENANT_ID, codes, codes),
    )


def fetch_base_fields(cur, codes: list[str]) -> list[dict]:
    return fetch_rows(
        cur,
        """
        SELECT * FROM dynamic_entity_type_base_field
        WHERE deleted = false AND tenant_id = %s AND entity_type_code = ANY(%s)
        ORDER BY entity_type_code, field_code
        """,
        (TENANT_ID, codes),
    )


def fetch_all_base_fields(cur) -> list[dict]:
    return fetch_rows(
        cur,
        """
        SELECT * FROM dynamic_entity_type_base_field
        WHERE deleted = false AND tenant_id = %s
        ORDER BY entity_type_code, field_code
        """,
        (TENANT_ID,),
    )


def fetch_all_field_library(cur) -> list[dict]:
    """Export complete dynamic_field table (not only fields referenced by models)."""
    return fetch_rows(
        cur,
        """
        SELECT * FROM dynamic_field
        WHERE deleted = false AND tenant_id = %s
        ORDER BY code
        """,
        (TENANT_ID,),
    )


def fetch_field_groups(cur) -> list[dict]:
    return fetch_rows(
        cur,
        """
        SELECT * FROM dynamic_group
        WHERE deleted = false AND tenant_id = %s AND group_type = 'FIELD'
        ORDER BY sort, code
        """,
        (TENANT_ID,),
    )


def fetch_field_group_relations(cur) -> list[dict]:
    return fetch_rows(
        cur,
        """
        SELECT g.code AS group_code, f.code AS field_code, gr.sort
        FROM dynamic_group_relation gr
        JOIN dynamic_group g ON g.id = gr.group_id AND g.deleted = false
        JOIN dynamic_field f ON f.id = gr.target_id AND f.deleted = false
        WHERE gr.deleted = false AND gr.tenant_id = %s AND gr.group_type = 'FIELD'
        ORDER BY g.sort, gr.sort, f.code
        """,
        (TENANT_ID,),
    )


def fetch_dynamic_business(cur) -> list[dict]:
    return fetch_rows(
        cur,
        """
        SELECT * FROM dynamic_business
        WHERE deleted = false AND tenant_id = %s
        ORDER BY sort, code
        """,
        (TENANT_ID,),
    )


def fetch_dynamic_business_entries(cur) -> list[dict]:
    biz_id_code = {
        int(r["id"]): r["code"]
        for r in fetch_rows(
            cur,
            "SELECT id, code FROM dynamic_business WHERE deleted = false AND tenant_id = %s",
            (TENANT_ID,),
        )
    }
    rows = fetch_rows(
        cur,
        """
        SELECT * FROM dynamic_business_entry
        WHERE deleted = false AND tenant_id = %s
        ORDER BY business_id, sort, code
        """,
        (TENANT_ID,),
    )
    for row in rows:
        row["business_code"] = biz_id_code.get(int(row["business_id"]), "")
    return [r for r in rows if r.get("business_code")]


def fetch_business_capabilities(cur) -> list[dict]:
    return fetch_rows(
        cur,
        """
        SELECT * FROM business_capability
        WHERE deleted = false AND tenant_id = %s
        ORDER BY entity_type_code
        """,
        (TENANT_ID,),
    )


def fetch_all_models(cur, exclude_ids: tuple = ()) -> list[dict]:
    return fetch_rows(
        cur,
        """
        SELECT * FROM dynamic_model
        WHERE deleted = false AND tenant_id = %s AND id <> ALL(%s)
        ORDER BY code
        """,
        (TENANT_ID, list(exclude_ids) or [-1]),
    )


def fetch_models(cur, business_codes: list[str], exclude_ids: tuple = ()) -> list[dict]:
    return fetch_rows(
        cur,
        """
        SELECT * FROM dynamic_model
        WHERE deleted = false AND tenant_id = %s
          AND entity_type_code = ANY(%s)
          AND id <> ALL(%s)
        ORDER BY code
        """,
        (TENANT_ID, business_codes, list(exclude_ids) or [-1]),
    )


def normalize_shared_device_models(rows: list[dict]) -> list[dict]:
    """Merge legacy device models into equipment business type for a single device library."""
    out: list[dict] = []
    for row in rows:
        item = dict(row)
        if item.get("entity_type_code") in LEGACY_DEVICE_BUSINESS:
            item["entity_type_code"] = EQUIPMENT_BUSINESS
        out.append(item)
    return out


def fetch_shared_device_models(cur) -> list[dict]:
    rows = fetch_rows(
        cur,
        """
        SELECT * FROM dynamic_model
        WHERE deleted = false AND tenant_id = %s
          AND entity_type_code = ANY(%s)
        ORDER BY code
        """,
        (TENANT_ID, list(SHARED_DEVICE_BUSINESS)),
    )
    return normalize_shared_device_models(rows)


def fetch_models_by_codes(cur, codes: list[str]) -> list[dict]:
    return fetch_rows(
        cur,
        """
        SELECT * FROM dynamic_model
        WHERE deleted = false AND tenant_id = %s AND code = ANY(%s)
        ORDER BY code
        """,
        (TENANT_ID, codes),
    )


def fetch_fields_for_models(cur, model_ids: list[int]) -> list[dict]:
    return fetch_rows(
        cur,
        """
        SELECT DISTINCT f.* FROM dynamic_field f
        JOIN dynamic_model_field_assignment a ON a.field_id = f.id AND a.deleted = false
        WHERE f.deleted = false AND f.tenant_id = %s AND a.model_id = ANY(%s)
        ORDER BY f.code
        """,
        (TENANT_ID, model_ids or [-1]),
    )


def fetch_assignments(cur, model_ids: list[int]) -> list[dict]:
    return fetch_rows(
        cur,
        """
        SELECT * FROM dynamic_model_field_assignment
        WHERE deleted = false AND tenant_id = %s AND model_id = ANY(%s)
        ORDER BY model_id, sort, id
        """,
        (TENANT_ID, model_ids or [-1]),
    )


def fetch_model_relations(cur, model_ids: list[int]) -> list[dict]:
    return fetch_rows(
        cur,
        """
        SELECT * FROM dynamic_model_relation
        WHERE deleted = false AND tenant_id = %s
          AND (source_model_id = ANY(%s) OR target_model_id = ANY(%s))
        ORDER BY source_model_code, target_model_code
        """,
        (TENANT_ID, model_ids or [-1], model_ids or [-1]),
    )


def fetch_model_relations_within_codes(cur, model_codes: list[str]) -> list[dict]:
    """Only relations whose source and target are both in model_codes (device library internal)."""
    if not model_codes:
        return []
    return fetch_rows(
        cur,
        """
        SELECT * FROM dynamic_model_relation
        WHERE deleted = false AND tenant_id = %s
          AND source_model_code = ANY(%s)
          AND target_model_code = ANY(%s)
        ORDER BY source_model_code, target_model_code
        """,
        (TENANT_ID, model_codes, model_codes),
    )


def fetch_model_declarations(cur, model_ids: list[int]) -> list[dict]:
    return fetch_rows(
        cur,
        """
        SELECT * FROM dynamic_model_relation_declaration
        WHERE deleted = false AND tenant_id = %s AND model_id = ANY(%s)
        ORDER BY model_id
        """,
        (TENANT_ID, model_ids or [-1]),
    )


def fetch_all_categories(cur) -> list[dict]:
    return fetch_rows(
        cur,
        """
        SELECT * FROM dynamic_category
        WHERE deleted = false AND tenant_id = %s
        ORDER BY level NULLS FIRST, code
        """,
        (TENANT_ID,),
    )


def fetch_categories(cur, exclude_ids: tuple = ()) -> list[dict]:
    return fetch_rows(
        cur,
        """
        SELECT * FROM dynamic_category
        WHERE deleted = false AND tenant_id = %s AND id <> ALL(%s)
        ORDER BY level NULLS FIRST, code
        """,
        (TENANT_ID, list(exclude_ids) or [-1]),
    )


def fetch_categories_for_models(cur, model_ids: list[int]) -> list[dict]:
    if not model_ids:
        return []
    rels = fetch_rows(
        cur,
        """
        SELECT DISTINCT category_id FROM dynamic_model_category_relation
        WHERE deleted = false AND tenant_id = %s AND model_id = ANY(%s)
        """,
        (TENANT_ID, model_ids),
    )
    seed_ids = [int(r["category_id"]) for r in rels if r.get("category_id")]
    if not seed_ids:
        return []
    all_cats = fetch_all_categories(cur)
    by_id = {int(c["id"]): c for c in all_cats}
    needed: set[int] = set()

    def add_chain(cat_id: int | None):
        while cat_id and cat_id not in needed:
            needed.add(cat_id)
            parent = by_id.get(cat_id)
            if not parent:
                break
            pid = parent.get("parent_id")
            cat_id = int(pid) if pid not in (None, 0) else None

    for cid in seed_ids:
        add_chain(cid)
    return [by_id[i] for i in sorted(needed) if i in by_id]


def fetch_station_categories(cur) -> list[dict]:
    return fetch_rows(
        cur,
        """
        SELECT * FROM dynamic_category
        WHERE deleted = false AND tenant_id = %s
          AND (id = ANY(%s) OR code LIKE 'CAT-OIL%%')
        ORDER BY level NULLS FIRST, code
        """,
        (TENANT_ID, list(STATION_CATEGORY_IDS)),
    )


def fetch_category_types(cur, category_type_codes: list[str]) -> list[dict]:
    if not category_type_codes:
        return []
    return fetch_rows(
        cur,
        """
        SELECT * FROM dynamic_category_type
        WHERE deleted = false AND tenant_id = %s
          AND category_type_code = ANY(%s)
        ORDER BY category_type_code
        """,
        (TENANT_ID, category_type_codes),
    )


def fetch_corridor_category_types(cur) -> list[dict]:
    return fetch_rows(
        cur,
        """
        SELECT * FROM dynamic_category_type
        WHERE deleted = false AND tenant_id = %s
          AND (top_level_category_id IS NULL OR top_level_category_id <> ALL(%s))
        ORDER BY category_type_code
        """,
        (TENANT_ID, list(STATION_CATEGORY_IDS)),
    )


def fetch_station_category_types(cur, category_ids: list[int]) -> list[dict]:
    return fetch_rows(
        cur,
        """
        SELECT * FROM dynamic_category_type
        WHERE deleted = false AND tenant_id = %s
          AND top_level_category_id = ANY(%s)
        ORDER BY category_type_code
        """,
        (TENANT_ID, category_ids or [-1]),
    )


def fetch_model_category_relations(cur, category_ids: list[int]) -> list[dict]:
    return fetch_rows(
        cur,
        """
        SELECT * FROM dynamic_model_category_relation
        WHERE deleted = false AND tenant_id = %s AND category_id = ANY(%s)
        ORDER BY model_id, category_id
        """,
        (TENANT_ID, category_ids or [-1]),
    )


def fetch_page_configs(cur, exclude_page_codes: tuple = ()) -> list[dict]:
    return fetch_rows(
        cur,
        """
        SELECT * FROM dynamic_page_config
        WHERE deleted = false AND tenant_id = %s AND page_code <> ALL(%s)
        ORDER BY page_code
        """,
        (TENANT_ID, list(exclude_page_codes) or [""]),
    )


def fetch_station_pages(cur) -> list[dict]:
    return fetch_rows(
        cur,
        """
        SELECT * FROM dynamic_page_config
        WHERE deleted = false AND tenant_id = %s
          AND page_code = 'PAGE-REGION-OIL-DEPOT'
        """,
        (TENANT_ID,),
    )


def build_model_sql(
    cur,
    models: list[dict],
    *,
    internal_relations_only: bool = False,
) -> str:
    mids = [m["id"] for m in models]
    codes = [m["code"] for m in models]
    fields = fetch_fields_for_models(cur, mids)
    if internal_relations_only:
        relations = fetch_model_relations_within_codes(cur, codes)
    else:
        relations = fetch_model_relations(cur, mids)
    return compose_model_library(
        models,
        fetch_assignments(cur, mids),
        relations,
        fetch_model_declarations(cur, mids),
        fields,
    )


def build_category_sql(
    cur,
    categories: list[dict],
    models: list[dict],
    category_types: list[dict],
    page_configs: list[dict],
) -> str:
    cids = [c["id"] for c in categories]
    return compose_model_categories(
        category_types,
        categories,
        fetch_model_category_relations(cur, cids),
        models,
        page_configs,
    )


def export_system_package(cur, all_codes: list[str]) -> dict[str, str]:
    device_models = fetch_shared_device_models(cur)
    device_mids = [m["id"] for m in device_models]
    equipment_categories = fetch_categories_for_models(cur, device_mids)
    equipment_cat_codes = sorted(
        {c["category_type_code"] for c in equipment_categories if c.get("category_type_code")}
    )
    fields = fetch_all_field_library(cur)
    groups = fetch_field_groups(cur)
    group_relations = fetch_field_group_relations(cur)

    return {
        "02_business_types.sql": compose_business_types(
            fetch_business_types(cur, all_codes),
            fetch_business_type_configs(cur, all_codes),
            fetch_business_type_relations(cur, all_codes),
        ),
        "03_fields.sql": compose_fields(
            fetch_all_base_fields(cur),
            fields,
        ),
        "04_field_groups.sql": compose_field_groups(groups, group_relations),
        "05_models.sql": build_model_sql(cur, device_models, internal_relations_only=True),
        "06_categories.sql": build_category_sql(
            cur,
            equipment_categories,
            device_models,
            fetch_category_types(cur, equipment_cat_codes),
            [],
        ),
        "07_business_portal.sql": compose_business_portal(
            fetch_dynamic_business(cur),
            fetch_dynamic_business_entries(cur),
        ),
        "08_capabilities.sql": compose_capabilities(fetch_business_capabilities(cur)),
        "_field_library": compose_industry_field_library(fields, groups, group_relations),
        "_base_fields_only": render_base_fields(fetch_all_base_fields(cur)),
        "_field_library_only": render_field_library(fields),
    }


def export_corridor_package(cur) -> dict[str, str]:
    station_models = fetch_models_by_codes(cur, list(STATION_MODEL_CODES))
    station_ids = tuple(m["id"] for m in station_models)
    all_models = fetch_all_models(cur, exclude_ids=station_ids)
    corridor_models = [
        m
        for m in all_models
        if m.get("entity_type_code") not in SHARED_DEVICE_BUSINESS
    ]
    categories = fetch_categories(cur, exclude_ids=STATION_CATEGORY_IDS)
    # 分类类型：除站场顶部分类外全部导出
    category_type_codes = sorted(
        {c["category_type_code"] for c in categories if c.get("category_type_code")}
    )
    return {
        "05_models.sql": build_model_sql(cur, corridor_models),
        "06_categories.sql": build_category_sql(
            cur,
            categories,
            corridor_models,
            fetch_category_types(cur, category_type_codes)
            if category_type_codes
            else fetch_corridor_category_types(cur),
            fetch_page_configs(cur, exclude_page_codes=("PAGE-REGION-OIL-DEPOT",)),
        ),
    }


def export_station_package(cur) -> dict[str, str] | None:
    models = fetch_models_by_codes(cur, list(STATION_MODEL_CODES))
    if not models:
        return None
    mids = [m["id"] for m in models]
    categories = fetch_station_categories(cur)
    cids = [c["id"] for c in categories]
    return {
        "05_models.sql": build_model_sql(cur, models),
        "06_categories.sql": build_category_sql(
            cur,
            categories,
            models,
            fetch_station_category_types(cur, cids),
            fetch_station_pages(cur),
        ),
    }


def preserve_entities(path: Path) -> str | None:
    if not path.exists():
        return None
    text = path.read_text(encoding="utf-8")
    if "SET search_path TO dynamicbusiness;" in text:
        return text.split("SET search_path TO dynamicbusiness;", 1)[-1].strip()
    idx = text.find("SET search_path")
    return text[idx:].split("SET search_path TO dynamicbusiness;", 1)[-1].strip() if idx >= 0 else text


def write_import_script(path: Path, title: str, files: list[str]):
    lines = [
        "#!/usr/bin/env bash",
        f"# {title}",
        "set -euo pipefail",
        "",
        'SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"',
        "",
        'PGHOST="${PGHOST:-127.0.0.1}"',
        'PGPORT="${PGPORT:-5432}"',
        'PGDATABASE="${PGDATABASE:-sinopec}"',
        'PGUSER="${PGUSER:-postgres}"',
        'export PGPASSWORD="${PGPASSWORD:-Coolhomer}"',
        "",
        "run() {",
        '  echo ">> psql -f $(basename "$1")"',
        '  psql -h "$PGHOST" -p "$PGPORT" -U "$PGUSER" -d "$PGDATABASE" -v ON_ERROR_STOP=1 -f "$1"',
        "}",
        "",
    ]
    for f in files:
        lines.append(f'run "${{SCRIPT_DIR}}/{f}"')
    lines.extend(["", f'echo "done: {title}"', ""])
    path.write_text("\n".join(lines), encoding="utf-8")
    path.chmod(0o755)


def write_dev_all_script():
    content = """#!/usr/bin/env bash
# 开发联调：system + corridor + station（站场包不存在时跳过）
set -euo pipefail
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
bash "${SCRIPT_DIR}/system/import.sh"
bash "${SCRIPT_DIR}/smart-corridor/import.sh"
if [[ -f "${SCRIPT_DIR}/smart-station/import.sh" ]]; then
  bash "${SCRIPT_DIR}/smart-station/import.sh"
else
  echo "skip: smart-station/import.sh not found"
fi
echo "done: dev all"
"""
    path = IMPORT_ROOT / "import-dev-all.sh"
    path.write_text(content, encoding="utf-8")
    path.chmod(0o755)


def remove_legacy_files():
    legacy = [
        "00_schema.sql",
        "system/01_field_library.sql",
        "system/02_model_library.sql",
        "smart-corridor/01_field_library.sql",
        "smart-corridor/02_model_library.sql",
        "smart-corridor/03_fields.sql",
        "smart-corridor/03_model_categories.sql",
        "smart-corridor/05_entities.sql",
        "smart-station/01_ddl.sql",
        "smart-station/02_field_library.sql",
        "smart-station/03_fields.sql",
        "smart-station/03_model_library.sql",
        "smart-station/04_model_categories.sql",
        "smart-station/05_entities.sql",
    ]
    for rel in legacy:
        p = IMPORT_ROOT / rel
        if p.exists():
            p.unlink()
            print(f"removed {rel}")


def main():
    conn = connect()
    cur = conn.cursor()
    corridor_entities = preserve_entities(IMPORT_ROOT / "smart-corridor/07_entities.sql")
    if corridor_entities is None:
        corridor_entities = preserve_entities(IMPORT_ROOT / "smart-corridor/05_entities.sql")
    station_entities = preserve_entities(IMPORT_ROOT / "smart-station/07_entities.sql")
    if station_entities is None:
        station_entities = preserve_entities(IMPORT_ROOT / "smart-station/05_entities.sql")

    try:
        all_codes = fetch_all_entity_type_codes(cur)

        write_schema(IMPORT_ROOT / "system/01_schema.sql")

        system = export_system_package(cur, all_codes)
        write_sql(
            IMPORT_ROOT / "system/02_business_types.sql",
            "系统共用 · 02 业务类型（15 个业务 + config + 业务间关联）",
            "-- 依赖：system/01_schema.sql",
            system["02_business_types.sql"],
        )
        write_sql(
            IMPORT_ROOT / "system/03_fields.sql",
            "系统共用 · 03 字段（全部业务默认字段 + 完整字段库 dynamic_field）",
            "-- 依赖：system/02_business_types.sql\n"
            "-- 含源库全部 dynamic_field（含未挂模型的字段），字段库只在 system 维护一份",
            system["03_fields.sql"],
        )
        write_sql(
            IMPORT_ROOT / "system/04_field_groups.sql",
            "系统共用 · 04 字段分组（dynamic_group FIELD + dynamic_group_relation）",
            "-- 依赖：system/03_fields.sql\n"
            "-- 幂等 upsert；与字段库一一/多对多关联，按 group_code + field_code 解析",
            system["04_field_groups.sql"],
        )
        write_sql(
            IMPORT_ROOT / "system/05_models.sql",
            "系统共用 · 05 模型（通用设备模型库：equipment + 历史设备模型，仅库内关联）",
            "-- 依赖：system/03_fields.sql\n"
            "-- 含 data_collection/custom_6128 等历史设备模型，统一 entity_type_code=equipment\n"
            "-- 不含 region→equipment 等跨业务关联（见 smart-corridor/05_models.sql）",
            system["05_models.sql"],
        )
        write_sql(
            IMPORT_ROOT / "system/06_categories.sql",
            "系统共用 · 06 分类（equipment 常用分类）",
            "-- 依赖：system/05_models.sql",
            system["06_categories.sql"],
        )
        write_sql(
            IMPORT_ROOT / "system/07_business_portal.sql",
            "系统共用 · 07 业务门户（dynamic_business + dynamic_business_entry）",
            "-- 依赖：system/02_business_types.sql\n-- 按 business code 解析 parent 与 entry 归属",
            system["07_business_portal.sql"],
        )
        write_sql(
            IMPORT_ROOT / "system/08_capabilities.sql",
            "系统共用 · 08 能力投影（business_capability）",
            "-- 依赖：system/02_business_types.sql\n-- 按 entity_type_code 幂等 upsert",
            system["08_capabilities.sql"],
        )

        flyway_v2 = file_header(
            "V2: 系统共用 seed（Flyway 自动执行）",
            "-- 依赖：V1__init_dynamicbusiness_schema.sql\n"
            "-- 内容：system/02 + 03 + 05（不含分类/门户；字段分组见 V27）",
        ) + (
            system["02_business_types.sql"]
            + "\n\n"
            + system["03_fields.sql"]
            + "\n\n"
            + system["05_models.sql"]
        )
        (FLYWAY_DIR / "V2__seed_system_base.sql").write_text(flyway_v2, encoding="utf-8")

        flyway_v26 = file_header(
            "V26: 业务门户与能力投影 seed",
            "-- 依赖：V17 create dynamic business tables / V2 entity types\n"
            "-- 内容：system/07 + 08",
        ) + system["07_business_portal.sql"] + "\n\n" + system["08_capabilities.sql"]
        (FLYWAY_DIR / "V26__seed_business_portal_and_capabilities.sql").write_text(
            flyway_v26, encoding="utf-8"
        )

        flyway_v27 = file_header(
            "V27: 行业字段库分组 seed",
            "-- 依赖：V2 字段库\n"
            "-- 内容：dynamic_group(FIELD) + dynamic_group_relation（幂等 upsert）\n"
            "-- 字段定义已在 V2/platform-import 03；本迁移补分组与关联",
        ) + system["04_field_groups.sql"]
        (FLYWAY_DIR / "V27__init_industry_field_library.sql").write_text(
            flyway_v27, encoding="utf-8"
        )

        field_count = len(fetch_all_field_library(cur))
        group_count = len(fetch_field_groups(cur))
        rel_count = len(fetch_field_group_relations(cur))
        assign_count = fetch_rows(
            cur,
            "SELECT count(*) AS c FROM dynamic_model_field_assignment WHERE deleted=false AND tenant_id=%s",
            (TENANT_ID,),
        )[0]["c"]
        model_count = fetch_rows(
            cur,
            "SELECT count(*) AS c FROM dynamic_model WHERE deleted=false AND tenant_id=%s",
            (TENANT_ID,),
        )[0]["c"]
        print(
            f"exported: fields={field_count}, field_groups={group_count}, "
            f"group_relations={rel_count}, models={model_count}, "
            f"model_field_assignments={assign_count}, "
            f"businesses={len(fetch_dynamic_business(cur))}, "
            f"capabilities={len(fetch_business_capabilities(cur))}"
        )
        if assign_count == 0:
            print(
                "warn: dynamic_model_field_assignment is empty — "
                "models export without field bindings; re-link in model config UI"
            )

        corridor = export_corridor_package(cur)
        for name, body in corridor.items():
            write_sql(
                IMPORT_ROOT / f"smart-corridor/{name}",
                f"智慧管廊 · {name}",
                "-- 依赖：先导入 system/ 全包（设备模型库已在 system/05_models.sql）\n"
                "-- 含 region→equipment 等跨业务模型关联",
                body,
            )
        if corridor_entities:
            write_sql(
                IMPORT_ROOT / "smart-corridor/07_entities.sql",
                "智慧管廊 · 07 业务实例（可选）",
                "-- 依赖：smart-corridor/06_categories.sql\n-- 含 id/model_id，生产环境可跳过",
                corridor_entities,
            )

        station = export_station_package(cur)
        if station:
            for name, body in station.items():
                write_sql(
                    IMPORT_ROOT / f"smart-station/{name}",
                    f"智慧站场 · {name}",
                    "-- 依赖：先导入 system/ 全包（字段库已在 system/03_fields.sql）\n-- 本包仅含站场 Region 模型与分类",
                    body,
                )
            if station_entities:
                write_sql(
                    IMPORT_ROOT / "smart-station/07_entities.sql",
                    "智慧站场 · 07 示例实例（可选）",
                    "-- 依赖：smart-station/06_categories.sql",
                    station_entities,
                )
        else:
            print("skip: smart-station (MODEL-REGION-SITE / MODEL-REGION-TANK-GROUP not in DB)")

        write_import_script(
            IMPORT_ROOT / "system/import.sh",
            "系统共用包",
            [
                "01_schema.sql",
                "02_business_types.sql",
                "03_fields.sql",
                "04_field_groups.sql",
                "05_models.sql",
                "06_categories.sql",
                "07_business_portal.sql",
                "08_capabilities.sql",
            ],
        )
        corridor_files = ["05_models.sql", "06_categories.sql"]
        if (IMPORT_ROOT / "smart-corridor/07_entities.sql").exists():
            corridor_files.append("07_entities.sql")
        write_import_script(
            IMPORT_ROOT / "smart-corridor/import.sh",
            "智慧管廊产品包（需先导入 system/）",
            corridor_files,
        )
        station_files = ["05_models.sql", "06_categories.sql"]
        if (IMPORT_ROOT / "smart-station/07_entities.sql").exists():
            station_files.append("07_entities.sql")
        if (IMPORT_ROOT / "smart-station/05_models.sql").exists():
            write_import_script(
                IMPORT_ROOT / "smart-station/import.sh",
                "智慧站场产品包（需先导入 system/）",
                station_files,
            )
        write_dev_all_script()

        remove_legacy_files()

        # 删除旧 shell 脚本名
        for old in ("import-smart-corridor.sh", "import-smart-station.sh"):
            p = IMPORT_ROOT / old
            if p.exists():
                p.unlink()

    finally:
        cur.close()
        conn.close()

    print("done:", IMPORT_ROOT)


if __name__ == "__main__":
    main()
