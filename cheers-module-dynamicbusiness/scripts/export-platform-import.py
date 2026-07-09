#!/usr/bin/env python3
"""Export dynamicbusiness metadata into platform-import packages (modular artifacts)."""

from __future__ import annotations

import argparse
import os
import subprocess
import sys
from datetime import date
from pathlib import Path

import psycopg2

from entity_type_canonical import (
    normalize_entity_type_config_row,
    normalize_entity_type_row,
    normalize_model_row,
)
from seed_codegen import (
    TENANT_ID,
    compose_business_portal,
    id_to_code_map,
    render_base_fields,
    render_business_capabilities,
    render_business_type_configs,
    render_business_type_relations,
    render_business_types,
    render_categories,
    render_dynamic_business,
    render_dynamic_business_entries,
    render_field_group_relations,
    render_field_groups,
    render_field_library,
    render_model_category_relations,
    render_model_field_assignments,
    render_model_relation_declarations,
    render_model_relations,
    render_models,
    render_page_configs,
    render_rebuild_category_tree,
    render_category_types,
)

ROOT = Path(__file__).resolve().parent.parent
IMPORT_ROOT = ROOT / "scripts/platform-import"
SCRIPTS_DIR = Path(__file__).resolve().parent

# 独立导出单元：可按需组合，避免每次全量导出
ARTIFACT_SEEDS: dict[str, list[str]] = {
    "entity-types": [
        "seed/dynamic_entity_type.sql",
        "seed/dynamic_entity_type_config.sql",
        "seed/dynamic_entity_type_relation.sql",
        "seed/dynamic_entity_type_base_field.sql",
    ],
    "fields": [
        "seed/dynamic_field.sql",
        "seed/dynamic_group.sql",
        "seed/dynamic_group_relation.sql",
    ],
    "models": [
        "seed/dynamic_model.sql",
        "seed/dynamic_model_relation.sql",
        "seed/dynamic_model_relation_declaration.sql",
    ],
    "assignments": ["seed/dynamic_model_field_assignment.sql"],
    "categories": [
        "seed/dynamic_category_type.sql",
        "seed/dynamic_category.sql",
        "seed/dynamic_model_category_relation.sql",
        "seed/dynamic_page_config.sql",
    ],
    "portal": [
        "seed/dynamic_business.sql",
        "seed/dynamic_business_entry.sql",
        "seed/business_capability.sql",
    ],
}
ALL_ARTIFACTS = list(ARTIFACT_SEEDS.keys())

SYSTEM_SEED_ORDER = [
    "seed/dynamic_entity_type.sql",
    "seed/dynamic_entity_type_config.sql",
    "seed/dynamic_entity_type_relation.sql",
    "seed/dynamic_entity_type_base_field.sql",
    "seed/dynamic_field.sql",
    "seed/dynamic_group.sql",
    "seed/dynamic_group_relation.sql",
    "seed/dynamic_model.sql",
    "seed/dynamic_model_relation.sql",
    "seed/dynamic_model_relation_declaration.sql",
    "seed/dynamic_model_field_assignment.sql",
    "seed/dynamic_category_type.sql",
    "seed/dynamic_category.sql",
    "seed/dynamic_model_category_relation.sql",
    "seed/dynamic_page_config.sql",
    "seed/dynamic_business.sql",
    "seed/dynamic_business_entry.sql",
    "seed/business_capability.sql",
]

PRODUCT_SEED_ORDER = [
    "seed/dynamic_model.sql",
    "seed/dynamic_model_relation.sql",
    "seed/dynamic_model_relation_declaration.sql",
    "seed/dynamic_model_field_assignment.sql",
    "seed/dynamic_category_type.sql",
    "seed/dynamic_category.sql",
    "seed/dynamic_model_category_relation.sql",
    "seed/dynamic_page_config.sql",
]

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


def seeds_for_artifacts(artifacts: list[str]) -> list[str]:
    paths: list[str] = []
    for name in artifacts:
        paths.extend(ARTIFACT_SEEDS[name])
    # preserve dependency order from SYSTEM_SEED_ORDER
    order_index = {p: i for i, p in enumerate(SYSTEM_SEED_ORDER)}
    return sorted(set(paths), key=lambda p: order_index.get(p, 999))


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
    return fetch_rows(
        cur,
        """
        SELECT e.*, b.code AS business_code, pc.page_code AS page_config_code
        FROM dynamic_business_entry e
        JOIN dynamic_business b ON b.id = e.business_id AND b.deleted = false
        LEFT JOIN dynamic_page_config pc
          ON pc.id = e.page_config_id AND pc.deleted = false AND pc.tenant_id = e.tenant_id
        WHERE e.deleted = false AND e.tenant_id = %s
        ORDER BY b.sort, b.code, e.sort, e.code
        """,
        (TENANT_ID,),
    )


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
        item = normalize_model_row(dict(row))
        if item.get("entity_type_code") in LEGACY_DEVICE_BUSINESS:
            item["entity_type_code"] = EQUIPMENT_BUSINESS
        out.append(item)
    return out


def normalize_entity_type_models(rows: list[dict]) -> list[dict]:
    """Map legacy pinyin entity_type_code on models to canonical English codes."""
    return [normalize_model_row(dict(row)) for row in rows]


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
        SELECT a.*,
               g.code AS field_group_code,
               g.group_type AS field_group_type,
               rl.entity_type_code AS ref_library_entity_type_code,
               rl.ref_target_type AS ref_library_ref_target_type,
               rl.constraint_type AS ref_library_constraint_type
        FROM dynamic_model_field_assignment a
        LEFT JOIN dynamic_group g
          ON g.id = a.field_group_id AND g.deleted = false AND g.tenant_id = a.tenant_id
        LEFT JOIN dynamic_ref_constraint_library rl
          ON rl.id = a.ref_library_id AND rl.deleted = false AND rl.tenant_id = a.tenant_id
        WHERE a.deleted = false AND a.tenant_id = %s AND a.model_id = ANY(%s)
        ORDER BY a.model_id, a.sort, a.id
        """,
        (TENANT_ID, model_ids or [-1]),
    )


def fetch_model_relations(cur, model_ids: list[int]) -> list[dict]:
    return fetch_rows(
        cur,
        """
        SELECT r.*,
               etr.source_entity_type_code AS etr_source_code,
               etr.target_entity_type_code AS etr_target_code
        FROM dynamic_model_relation r
        LEFT JOIN dynamic_entity_type_relation etr
          ON etr.id = r.entity_type_relation_id AND etr.deleted = false
        WHERE r.deleted = false AND r.tenant_id = %s
          AND (r.source_model_id = ANY(%s) OR r.target_model_id = ANY(%s))
        ORDER BY r.source_model_code, r.target_model_code
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
        SELECT r.*,
               etr.source_entity_type_code AS etr_source_code,
               etr.target_entity_type_code AS etr_target_code
        FROM dynamic_model_relation r
        LEFT JOIN dynamic_entity_type_relation etr
          ON etr.id = r.entity_type_relation_id AND etr.deleted = false
        WHERE r.deleted = false AND r.tenant_id = %s
          AND r.source_model_code = ANY(%s)
          AND r.target_model_code = ANY(%s)
        ORDER BY r.source_model_code, r.target_model_code
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


def build_model_seed_parts(
    cur,
    models: list[dict],
    *,
    internal_relations_only: bool = False,
) -> dict[str, str]:
    mids = [m["id"] for m in models]
    codes = [m["code"] for m in models]
    fields = fetch_fields_for_models(cur, mids)
    if internal_relations_only:
        relations = fetch_model_relations_within_codes(cur, codes)
    else:
        relations = fetch_model_relations(cur, mids)
    assignments = fetch_assignments(cur, mids)
    declarations = fetch_model_declarations(cur, mids)
    model_id_to_code = id_to_code_map(models)
    field_id_to_code = id_to_code_map(fields)
    relation_id_to_key = {
        int(r["id"]): (r["source_model_code"], r["target_model_code"], r.get("field_code"))
        for r in relations
        if r.get("id") is not None
    }
    return {
        "seed/dynamic_model.sql": render_models(models),
        "seed/dynamic_model_relation.sql": render_model_relations(relations),
        "seed/dynamic_model_relation_declaration.sql": render_model_relation_declarations(
            declarations, model_id_to_code
        ),
        "seed/dynamic_model_field_assignment.sql": render_model_field_assignments(
            assignments, model_id_to_code, field_id_to_code, relation_id_to_key
        ),
    }


def build_category_seed_parts(
    cur,
    categories: list[dict],
    models: list[dict],
    category_types: list[dict],
    page_configs: list[dict],
) -> dict[str, str]:
    cids = [c["id"] for c in categories]
    category_id_to_code = id_to_code_map(categories)
    model_id_to_code = id_to_code_map(models)
    cat_body = render_categories(categories, category_id_to_code)
    return {
        "seed/dynamic_category_type.sql": render_category_types(category_types, category_id_to_code),
        "seed/dynamic_category.sql": cat_body + "\n\n" + render_rebuild_category_tree(),
        "seed/dynamic_model_category_relation.sql": render_model_category_relations(
            fetch_model_category_relations(cur, cids),
            model_id_to_code,
            category_id_to_code,
        ),
        "seed/dynamic_page_config.sql": render_page_configs(page_configs),
    }


def export_system_seeds(cur, all_codes: list[str], artifacts: list[str]) -> dict[str, str]:
    device_models = fetch_shared_device_models(cur)
    device_mids = [m["id"] for m in device_models]
    equipment_categories = fetch_categories_for_models(cur, device_mids)
    equipment_cat_codes = sorted(
        {c["category_type_code"] for c in equipment_categories if c.get("category_type_code")}
    )
    fields = fetch_all_field_library(cur)
    groups = fetch_field_groups(cur)
    group_relations = fetch_field_group_relations(cur)
    group_id_to_code = id_to_code_map(groups)

    want = set(seeds_for_artifacts(artifacts))
    seeds: dict[str, str] = {}

    if "seed/dynamic_entity_type.sql" in want:
        seeds["seed/dynamic_entity_type.sql"] = render_business_types(
            [
                normalize_entity_type_row(row)
                for row in fetch_business_types(cur, all_codes)
            ]
        )
    if "seed/dynamic_entity_type_config.sql" in want:
        seeds["seed/dynamic_entity_type_config.sql"] = render_business_type_configs(
            [
                normalize_entity_type_config_row(row)
                for row in fetch_business_type_configs(cur, all_codes)
            ]
        )
    if "seed/dynamic_entity_type_relation.sql" in want:
        seeds["seed/dynamic_entity_type_relation.sql"] = render_business_type_relations(
            fetch_business_type_relations(cur, all_codes)
        )
    if "seed/dynamic_entity_type_base_field.sql" in want:
        seeds["seed/dynamic_entity_type_base_field.sql"] = render_base_fields(
            fetch_all_base_fields(cur)
        )
    if "seed/dynamic_field.sql" in want:
        seeds["seed/dynamic_field.sql"] = render_field_library(fields)
    if "seed/dynamic_group.sql" in want:
        seeds["seed/dynamic_group.sql"] = render_field_groups(groups, group_id_to_code)
    if "seed/dynamic_group_relation.sql" in want:
        seeds["seed/dynamic_group_relation.sql"] = render_field_group_relations(group_relations)
    if "seed/dynamic_business.sql" in want:
        seeds["seed/dynamic_business.sql"] = render_dynamic_business(fetch_dynamic_business(cur))
    if "seed/dynamic_business_entry.sql" in want:
        seeds["seed/dynamic_business_entry.sql"] = render_dynamic_business_entries(
            fetch_dynamic_business_entries(cur)
        )
    if "seed/business_capability.sql" in want:
        seeds["seed/business_capability.sql"] = render_business_capabilities(
            fetch_business_capabilities(cur)
        )

    model_keys = {
        "seed/dynamic_model.sql",
        "seed/dynamic_model_relation.sql",
        "seed/dynamic_model_relation_declaration.sql",
        "seed/dynamic_model_field_assignment.sql",
    }
    if want & model_keys:
        model_parts = build_model_seed_parts(cur, device_models, internal_relations_only=True)
        for k, v in model_parts.items():
            if k in want:
                seeds[k] = v

    category_keys = {
        "seed/dynamic_category_type.sql",
        "seed/dynamic_category.sql",
        "seed/dynamic_model_category_relation.sql",
        "seed/dynamic_page_config.sql",
    }
    if want & category_keys:
        cat_parts = build_category_seed_parts(
            cur,
            equipment_categories,
            device_models,
            fetch_category_types(cur, equipment_cat_codes),
            [],
        )
        for k, v in cat_parts.items():
            if k in want:
                seeds[k] = v

    return seeds


def export_corridor_seeds(cur, artifacts: list[str]) -> dict[str, str]:
    want = set(seeds_for_artifacts(artifacts))
    station_models = fetch_models_by_codes(cur, list(STATION_MODEL_CODES))
    station_ids = tuple(m["id"] for m in station_models)
    all_models = fetch_all_models(cur, exclude_ids=station_ids)
    corridor_models = normalize_entity_type_models(
        [m for m in all_models if m.get("entity_type_code") not in SHARED_DEVICE_BUSINESS]
    )
    categories = fetch_categories(cur, exclude_ids=STATION_CATEGORY_IDS)
    category_type_codes = sorted(
        {c["category_type_code"] for c in categories if c.get("category_type_code")}
    )
    seeds: dict[str, str] = {}
    model_keys = {
        "seed/dynamic_model.sql",
        "seed/dynamic_model_relation.sql",
        "seed/dynamic_model_relation_declaration.sql",
        "seed/dynamic_model_field_assignment.sql",
    }
    if want & model_keys:
        for k, v in build_model_seed_parts(cur, corridor_models).items():
            if k in want:
                seeds[k] = v
    category_keys = {
        "seed/dynamic_category_type.sql",
        "seed/dynamic_category.sql",
        "seed/dynamic_model_category_relation.sql",
        "seed/dynamic_page_config.sql",
    }
    if want & category_keys:
        for k, v in build_category_seed_parts(
            cur,
            categories,
            corridor_models,
            fetch_category_types(cur, category_type_codes)
            if category_type_codes
            else fetch_corridor_category_types(cur),
            fetch_page_configs(cur, exclude_page_codes=("PAGE-REGION-OIL-DEPOT",)),
        ).items():
            if k in want:
                seeds[k] = v
    return seeds


def export_station_seeds(cur, artifacts: list[str]) -> dict[str, str] | None:
    want = set(seeds_for_artifacts(artifacts))
    models = fetch_models_by_codes(cur, list(STATION_MODEL_CODES))
    if not models:
        return None
    categories = fetch_station_categories(cur)
    cids = [c["id"] for c in categories]
    seeds: dict[str, str] = {}
    model_keys = {
        "seed/dynamic_model.sql",
        "seed/dynamic_model_relation.sql",
        "seed/dynamic_model_relation_declaration.sql",
        "seed/dynamic_model_field_assignment.sql",
    }
    if want & model_keys:
        for k, v in build_model_seed_parts(cur, models).items():
            if k in want:
                seeds[k] = v
    category_keys = {
        "seed/dynamic_category_type.sql",
        "seed/dynamic_category.sql",
        "seed/dynamic_model_category_relation.sql",
        "seed/dynamic_page_config.sql",
    }
    if want & category_keys:
        for k, v in build_category_seed_parts(
            cur,
            categories,
            models,
            fetch_station_category_types(cur, cids),
            fetch_station_pages(cur),
        ).items():
            if k in want:
                seeds[k] = v
    return seeds if seeds else None


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
# 开发联调：Flyway 已由应用执行后，导入 system + corridor + station seed
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
        "system/02_business_types.sql",
        "system/03_fields.sql",
        "system/04_field_groups.sql",
        "system/04_field_groups_industry.sql",
        "system/05_industry_field_library.sql",
        "system/05_models.sql",
        "system/06_categories.sql",
        "system/07_business_portal.sql",
        "system/08_capabilities.sql",
        "system/01_field_library.sql",
        "system/02_model_library.sql",
        "smart-corridor/05_models.sql",
        "smart-corridor/06_categories.sql",
        "smart-corridor/01_field_library.sql",
        "smart-corridor/02_model_library.sql",
        "smart-corridor/03_fields.sql",
        "smart-corridor/03_model_categories.sql",
        "smart-corridor/05_entities.sql",
        "smart-station/05_models.sql",
        "smart-station/06_categories.sql",
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


def write_seed_files(base_dir: Path, seeds: dict[str, str], table_prefix: str = ""):
    for rel_path, body in seeds.items():
        if not body.strip() or body.strip().endswith("(empty)"):
            continue
        out = base_dir / rel_path
        title = rel_path.replace("seed/", "").replace(".sql", "")
        write_sql(
            out,
            f"{table_prefix}{title}",
            "-- 幂等 upsert；关联字段按 code 解析 id，不写 surrogate id",
            body,
        )


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description="从 PostgreSQL 导出 dynamicbusiness 元数据到 platform-import（可按单元独立导出）"
    )
    parser.add_argument(
        "--package",
        choices=["system", "corridor", "station", "all"],
        default="all",
        help="导出目标包（默认 all）",
    )
    parser.add_argument(
        "--artifacts",
        default=",".join(ALL_ARTIFACTS),
        help=(
            "逗号分隔导出单元："
            + ", ".join(ALL_ARTIFACTS)
            + "（默认全部）"
        ),
    )
    parser.add_argument(
        "--schema",
        action="store_true",
        help="同时从库 pg_dump 刷新 Flyway V1（不写入 platform-import DDL）",
    )
    parser.add_argument(
        "--rewrite-import-scripts",
        action="store_true",
        help="重写 import.sh / import-dev-all.sh（全量导出时建议开启）",
    )
    return parser.parse_args()


def run_export(
    *,
    package: str,
    artifacts: list[str],
    schema: bool,
    rewrite_import_scripts: bool,
):
    if schema:
        subprocess.run([sys.executable, str(SCRIPTS_DIR / "regenerate-v1-from-db.py")], check=True)

    conn = connect()
    cur = conn.cursor()
    corridor_entities = preserve_entities(IMPORT_ROOT / "smart-corridor/07_entities.sql")
    if corridor_entities is None:
        corridor_entities = preserve_entities(IMPORT_ROOT / "smart-corridor/seed/entities_optional.sql")
    station_entities = preserve_entities(IMPORT_ROOT / "smart-station/07_entities.sql")
    if station_entities is None:
        station_entities = preserve_entities(IMPORT_ROOT / "smart-station/seed/entities_optional.sql")

    try:
        all_codes = fetch_all_entity_type_codes(cur)
        packages = ["system", "corridor", "station"] if package == "all" else [package]

        if "system" in packages:
            system_seeds = export_system_seeds(cur, all_codes, artifacts)
            write_seed_files(IMPORT_ROOT / "system", system_seeds, "系统 · ")
            print(f"system: wrote {len(system_seeds)} seed file(s)")

        if "corridor" in packages:
            corridor_seeds = export_corridor_seeds(cur, artifacts)
            write_seed_files(IMPORT_ROOT / "smart-corridor", corridor_seeds, "管廊 · ")
            if corridor_entities and "entities" in artifacts:
                write_sql(
                    IMPORT_ROOT / "smart-corridor/seed/entities_optional.sql",
                    "管廊 · 业务实例（可选，含 id）",
                    "-- 依赖：smart-corridor seed 全包\n-- 生产环境可跳过",
                    corridor_entities,
                )
            print(f"corridor: wrote {len(corridor_seeds)} seed file(s)")

        if "station" in packages:
            station_seeds = export_station_seeds(cur, artifacts)
            if station_seeds:
                write_seed_files(IMPORT_ROOT / "smart-station", station_seeds, "站场 · ")
                if station_entities and "entities" in artifacts:
                    write_sql(
                        IMPORT_ROOT / "smart-station/seed/entities_optional.sql",
                        "站场 · 示例实例（可选）",
                        "-- 依赖：smart-station seed 全包",
                        station_entities,
                    )
                print(f"station: wrote {len(station_seeds)} seed file(s)")
            else:
                print("skip: station (MODEL-REGION-SITE / MODEL-REGION-TANK-GROUP not in DB)")

        if "system" in packages:
            field_count = len(fetch_all_field_library(cur))
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
                f"summary: fields={field_count}, models={model_count}, "
                f"model_field_assignments={assign_count}"
            )
            if assign_count == 0 and "assignments" in artifacts:
                print(
                    "warn: dynamic_model_field_assignment is empty — "
                    "models export without field bindings"
                )

        if rewrite_import_scripts:
            system_import_files = [
                p for p in SYSTEM_SEED_ORDER if (IMPORT_ROOT / "system" / p).exists()
            ]
            write_import_script(
                IMPORT_ROOT / "system/import.sh",
                "系统共用 seed（前置：Flyway V1→V2→V3）",
                system_import_files,
            )
            corridor_files = [
                p for p in PRODUCT_SEED_ORDER if (IMPORT_ROOT / "smart-corridor" / p).exists()
            ]
            if (IMPORT_ROOT / "smart-corridor/seed/entities_optional.sql").exists():
                corridor_files.append("seed/entities_optional.sql")
            write_import_script(
                IMPORT_ROOT / "smart-corridor/import.sh",
                "智慧管廊产品包 seed（需先 system/ + Flyway）",
                corridor_files,
            )
            station_files = [
                p for p in PRODUCT_SEED_ORDER if (IMPORT_ROOT / "smart-station" / p).exists()
            ]
            if (IMPORT_ROOT / "smart-station/seed/dynamic_model.sql").exists():
                if (IMPORT_ROOT / "smart-station/seed/entities_optional.sql").exists():
                    station_files.append("seed/entities_optional.sql")
                write_import_script(
                    IMPORT_ROOT / "smart-station/import.sh",
                    "智慧站场产品包 seed（需先 system/ + Flyway）",
                    station_files,
                )
            write_dev_all_script()
            remove_legacy_files()
            for old in ("import-smart-corridor.sh", "import-smart-station.sh"):
                p = IMPORT_ROOT / old
                if p.exists():
                    p.unlink()

    finally:
        cur.close()
        conn.close()

    print("done:", IMPORT_ROOT)


def main():
    args = parse_args()
    raw = [a.strip() for a in args.artifacts.split(",") if a.strip()]
    unknown = [a for a in raw if a not in ALL_ARTIFACTS and a != "entities"]
    if unknown:
        raise SystemExit(f"unknown artifacts: {unknown}; allowed: {ALL_ARTIFACTS + ['entities']}")
    artifacts = raw if raw else ALL_ARTIFACTS
    rewrite = args.rewrite_import_scripts or (args.package == "all" and set(artifacts) == set(ALL_ARTIFACTS))
    run_export(
        package=args.package,
        artifacts=artifacts,
        schema=args.schema,
        rewrite_import_scripts=rewrite,
    )


if __name__ == "__main__":
    main()
