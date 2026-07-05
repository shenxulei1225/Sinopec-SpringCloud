"""Generate platform-import SQL seeds keyed by business codes (not surrogate ids)."""

from __future__ import annotations

import json
from typing import Any

TENANT_ID = 1


def sql_literal(val: Any) -> str:
    if val is None:
        return "NULL"
    if isinstance(val, bool):
        return "TRUE" if val else "FALSE"
    if isinstance(val, (int, float)):
        return str(val)
    if isinstance(val, (dict, list)):
        return "'" + json.dumps(val, ensure_ascii=False).replace("'", "''") + "'"
    return "'" + str(val).replace("'", "''") + "'"


def id_to_code_map(rows: list[dict], id_key: str = "id", code_key: str = "code") -> dict[int, str]:
    return {int(r[id_key]): r[code_key] for r in rows if r.get(id_key) is not None and r.get(code_key)}


def parent_code(parent_id: int | None, id_code: dict[int, str]) -> str | None:
    if parent_id in (None, 0):
        return None
    return id_code.get(int(parent_id))


def render_business_types(rows: list[dict]) -> str:
    if not rows:
        return "-- dynamic_business_type: (empty)\n"
    lines = [f"-- dynamic_business_type: {len(rows)} row(s), upsert by code\n"]
    cols = [
        "code",
        "name",
        "parent_id",
        "description",
        "icon",
        "alias",
        "sort",
        "status",
        "type_level",
        "association_fields",
        "storage_type",
        "dedicated_table_name",
        "enable_rule_engine",
        "physical_column_mapping",
        "tenant_id",
        "creator",
    ]
    for row in rows:
        vals = ", ".join(sql_literal(row.get(c)) for c in cols)
        lines.append(
            f"""INSERT INTO dynamic_business_type ({", ".join(cols)})
VALUES ({vals})
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  icon = EXCLUDED.icon,
  alias = EXCLUDED.alias,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  storage_type = EXCLUDED.storage_type,
  dedicated_table_name = EXCLUDED.dedicated_table_name,
  enable_rule_engine = EXCLUDED.enable_rule_engine,
  physical_column_mapping = EXCLUDED.physical_column_mapping,
  updater = EXCLUDED.creator,
  update_time = CURRENT_TIMESTAMP;
"""
        )
    return "\n".join(lines)


def render_business_type_configs(rows: list[dict]) -> str:
    if not rows:
        return "-- dynamic_business_type_config: (empty)\n"
    lines = [f"-- dynamic_business_type_config: {len(rows)} row(s), upsert by business_type_code\n"]
    for row in rows:
        lines.append(
            f"""INSERT INTO dynamic_business_type_config (
  business_type_code, name, storage_type, dedicated_table_name, strategy_bean_name,
  enable_rule_engine, description, status, physical_column_mapping, tenant_id, creator
) VALUES (
  {sql_literal(row["business_type_code"])}, {sql_literal(row["name"])},
  {sql_literal(row["storage_type"])}, {sql_literal(row["dedicated_table_name"])},
  {sql_literal(row.get("strategy_bean_name"))}, {sql_literal(row.get("enable_rule_engine", True))},
  {sql_literal(row.get("description"))}, {sql_literal(row.get("status", 1))},
  {sql_literal(row.get("physical_column_mapping"))}, {TENANT_ID}, 'seed'
)
ON CONFLICT (business_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  storage_type = EXCLUDED.storage_type,
  dedicated_table_name = EXCLUDED.dedicated_table_name,
  physical_column_mapping = EXCLUDED.physical_column_mapping,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;
"""
        )
    return "\n".join(lines)


def render_base_fields(rows: list[dict]) -> str:
    if not rows:
        return "-- dynamic_business_type_base_field: (empty)\n"
    lines = [f"-- dynamic_business_type_base_field: {len(rows)} row(s), upsert by (business_type_code, field_code)\n"]
    for row in rows:
        lines.append(
            f"""INSERT INTO dynamic_business_type_base_field (
  business_type_code, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  {sql_literal(row["business_type_code"])}, {sql_literal(row["field_code"])},
  {sql_literal(row["field_name"])}, {sql_literal(row["data_type"])},
  {sql_literal(row.get("required", False))}, {sql_literal(row.get("default_value"))},
  {sql_literal(row.get("description"))}, {sql_literal(row.get("type_config"))},
  {sql_literal(row.get("sort_order", 0))}, {sql_literal(row.get("status", 1))},
  {TENANT_ID}, 'seed'
)
ON CONFLICT (business_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;
"""
        )
    return "\n".join(lines)


def render_field_library(rows: list[dict]) -> str:
    if not rows:
        return "-- dynamic_field: (empty)\n"
    lines = [f"-- dynamic_field: {len(rows)} row(s), upsert by code\n"]
    for row in rows:
        lines.append(
            f"""INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  {sql_literal(row["code"])}, {sql_literal(row["name"])}, {sql_literal(row["type"])},
  {sql_literal(row.get("unit"))}, {sql_literal(row.get("description"))},
  {sql_literal(row.get("source", "USER"))}, {sql_literal(row.get("status", 1))},
  {sql_literal(row.get("max_relations"))}, {sql_literal(row.get("index_strategy"))},
  {sql_literal(row.get("options"))}, {sql_literal(row.get("provider_code"))},
  {sql_literal(row.get("semantic_type"))}, {TENANT_ID}, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;
"""
        )
    return "\n".join(lines)


def render_field_name_aliases(rows: list[dict]) -> str:
    if not rows:
        return "-- base_field_library_name_alias: (empty)\n"
    lines = [f"-- base_field_library_name_alias: {len(rows)} row(s)\n"]
    for row in rows:
        lines.append(
            f"""INSERT INTO base_field_library_name_alias (base_field_name, library_field_code)
VALUES ({sql_literal(row["base_field_name"])}, {sql_literal(row["library_field_code"])})
ON CONFLICT DO NOTHING;
"""
        )
    return "\n".join(lines)


def render_models(rows: list[dict]) -> str:
    if not rows:
        return "-- dynamic_model: (empty)\n"
    lines = [f"-- dynamic_model: {len(rows)} row(s), upsert by code\n"]
    for row in rows:
        lines.append(
            f"""INSERT INTO dynamic_model (
  code, name, business_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  {sql_literal(row["code"])}, {sql_literal(row["name"])}, {sql_literal(row["business_type_code"])},
  {sql_literal(row.get("description"))}, {sql_literal(row.get("status", 1))},
  {sql_literal(row.get("sort", 0))}, {sql_literal(row.get("field_groups_config"))},
  {TENANT_ID}, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  field_groups_config = EXCLUDED.field_groups_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;
"""
        )
    return "\n".join(lines)


def render_model_relations(rows: list[dict]) -> str:
    if not rows:
        return "-- dynamic_model_relation: (empty)\n"
    lines = [f"-- dynamic_model_relation: {len(rows)} row(s), resolve model id by code\n"]
    for row in rows:
        lines.append(
            f"""INSERT INTO dynamic_model_relation (
  business_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  {sql_literal(row.get("business_type_relation_id"))},
  sm.id, {sql_literal(row["source_model_code"])},
  tm.id, {sql_literal(row["target_model_code"])},
  {sql_literal(row.get("relation_name"))}, {sql_literal(row.get("field_code"))},
  {sql_literal(row.get("auto_generated", False))}, {TENANT_ID}, 'seed'
FROM dynamic_model sm
JOIN dynamic_model tm ON tm.deleted = false AND tm.tenant_id = {TENANT_ID}
WHERE sm.deleted = false AND sm.tenant_id = {TENANT_ID}
  AND sm.code = {sql_literal(row["source_model_code"])}
  AND tm.code = {sql_literal(row["target_model_code"])}
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_relation r
    WHERE r.deleted = false AND r.tenant_id = {TENANT_ID}
      AND r.source_model_code = {sql_literal(row["source_model_code"])}
      AND r.target_model_code = {sql_literal(row["target_model_code"])}
      AND r.field_code = {sql_literal(row.get("field_code"))}
  );
"""
        )
    return "\n".join(lines)


def render_model_relation_declarations(rows: list[dict], model_id_to_code: dict[int, str]) -> str:
    if not rows:
        return "-- dynamic_model_relation_declaration: (empty)\n"
    lines = [f"-- dynamic_model_relation_declaration: {len(rows)} row(s), upsert by (model_code, target_business_type)\n"]
    for row in rows:
        model_code = model_id_to_code.get(int(row["model_id"]))
        if not model_code:
            continue
        lines.append(
            f"""INSERT INTO dynamic_model_relation_declaration (model_id, target_business_type, tenant_id, creator)
SELECT m.id, {sql_literal(row["target_business_type"])}, {TENANT_ID}, 'seed'
FROM dynamic_model m
WHERE m.deleted = false AND m.tenant_id = {TENANT_ID} AND m.code = {sql_literal(model_code)}
ON CONFLICT (model_id, target_business_type, tenant_id) WHERE deleted = false
DO NOTHING;
"""
        )
    return "\n".join(lines)


def _relation_lookup_sql(
    source_model_code: str,
    target_model_code: str | None,
    field_code: str | None,
) -> str:
    if not target_model_code or not field_code:
        return "NULL"
    return f"""(
  SELECT r.id FROM dynamic_model_relation r
  WHERE r.deleted = false AND r.tenant_id = {TENANT_ID}
    AND r.source_model_code = {sql_literal(source_model_code)}
    AND r.target_model_code = {sql_literal(target_model_code)}
    AND r.field_code = {sql_literal(field_code)}
  LIMIT 1
)"""


def render_model_field_assignments(
    rows: list[dict],
    model_id_to_code: dict[int, str],
    field_id_to_code: dict[int, str],
    relation_id_to_key: dict[int, tuple[str, str, str]],
) -> str:
    if not rows:
        return "-- dynamic_model_field_assignment: (empty)\n"
    lines = [
        f"-- dynamic_model_field_assignment: {len(rows)} row(s), resolve model_id/field_id by code\n"
    ]
    for row in rows:
        model_code = model_id_to_code.get(int(row["model_id"]))
        field_code = field_id_to_code.get(int(row["field_id"]))
        if not model_code or not field_code:
            continue
        rel_sql = "NULL"
        rel_id = row.get("model_relation_id")
        if rel_id and int(rel_id) in relation_id_to_key:
            src, tgt, fcode = relation_id_to_key[int(rel_id)]
            rel_sql = _relation_lookup_sql(src, tgt, fcode)
        lines.append(
            f"""INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_business_type, tenant_id, creator
)
SELECT
  m.id, f.id,
  {sql_literal(row.get("required", False))}, {sql_literal(row.get("is_searchable", False))},
  {sql_literal(row.get("is_filterable", False))}, {sql_literal(row.get("is_sortable", False))},
  {sql_literal(row.get("default_value"))}, {sql_literal(row.get("validation_rules"))},
  {sql_literal(row.get("sort", 0))}, {sql_literal(row.get("field_group_id"))},
  {sql_literal(row.get("field_source"))}, {sql_literal(row.get("ref_library_id"))},
  {rel_sql}, {sql_literal(row.get("target_business_type"))}, {TENANT_ID}, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = {TENANT_ID}
WHERE m.deleted = false AND m.tenant_id = {TENANT_ID}
  AND m.code = {sql_literal(model_code)}
  AND f.code = {sql_literal(field_code)}
ON CONFLICT (model_id, field_id, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  model_relation_id = EXCLUDED.model_relation_id,
  target_business_type = EXCLUDED.target_business_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;
"""
        )
    return "\n".join(lines)


def render_category_types(rows: list[dict], category_id_to_code: dict[int, str]) -> str:
    if not rows:
        return "-- dynamic_category_type: (empty)\n"
    lines = [f"-- dynamic_category_type: {len(rows)} row(s), upsert by category_type_code\n"]
    for row in rows:
        top_code = None
        top_id = row.get("top_level_category_id")
        if top_id:
            top_code = category_id_to_code.get(int(top_id))
        top_sql = "NULL"
        if top_code:
            top_sql = f"(SELECT c.id FROM dynamic_category c WHERE c.deleted = false AND c.tenant_id = {TENANT_ID} AND c.code = {sql_literal(top_code)} LIMIT 1)"
        lines.append(
            f"""INSERT INTO dynamic_category_type (
  category_type_code, name, description, status, top_level_category_id, tenant_id, creator
) VALUES (
  {sql_literal(row["category_type_code"])}, {sql_literal(row["name"])},
  {sql_literal(row.get("description"))}, {sql_literal(row.get("status", 1))},
  {top_sql}, {TENANT_ID}, 'seed'
)
ON CONFLICT (category_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  top_level_category_id = EXCLUDED.top_level_category_id,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;
"""
        )
    return "\n".join(lines)


def render_categories(rows: list[dict], category_id_to_code: dict[int, str]) -> str:
    if not rows:
        return "-- dynamic_category: (empty)\n"

    def sort_key(r: dict) -> tuple:
        lvl = r.get("level") or 0
        return (lvl, r.get("code") or "")

    ordered = sorted(rows, key=sort_key)
    lines = [f"-- dynamic_category: {len(ordered)} row(s), upsert by code; parent by parent_code\n"]
    for row in ordered:
        pcode = parent_code(row.get("parent_id"), category_id_to_code)
        parent_sql = "NULL"
        if pcode:
            parent_sql = f"(SELECT p.id FROM dynamic_category p WHERE p.deleted = false AND p.tenant_id = {TENANT_ID} AND p.code = {sql_literal(pcode)} LIMIT 1)"
        lines.append(
            f"""INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  {parent_sql}, {sql_literal(row["name"])}, {sql_literal(row["code"])},
  {sql_literal(row["category_type_code"])}, {sql_literal(row.get("sort", 0))},
  {sql_literal(row.get("status", 1))}, {sql_literal(row.get("description"))}, {TENANT_ID}, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  name = EXCLUDED.name,
  category_type_code = EXCLUDED.category_type_code,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;
"""
        )
    return "\n".join(lines)


def render_rebuild_category_tree() -> str:
    return f"""-- rebuild tree_path / level after category upsert (id-agnostic)
WITH RECURSIVE cat_tree AS (
  SELECT c.id, c.code, c.parent_id, ARRAY[c.id] AS path_ids, 0 AS lvl
  FROM dynamic_category c
  WHERE c.deleted = false AND c.tenant_id = {TENANT_ID}
    AND (c.parent_id IS NULL OR c.parent_id = 0
         OR NOT EXISTS (
           SELECT 1 FROM dynamic_category p
           WHERE p.id = c.parent_id AND p.deleted = false AND p.tenant_id = {TENANT_ID}
         ))
  UNION ALL
  SELECT c.id, c.code, c.parent_id, ct.path_ids || c.id, ct.lvl + 1
  FROM dynamic_category c
  JOIN cat_tree ct ON c.parent_id = ct.id
  WHERE c.deleted = false AND c.tenant_id = {TENANT_ID}
)
UPDATE dynamic_category c
SET
  tree_path = '/' || array_to_string(ct.path_ids, '/') || '/',
  level = ct.lvl + 1,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
FROM cat_tree ct
WHERE c.id = ct.id;
"""


def render_model_category_relations(
    rows: list[dict],
    model_id_to_code: dict[int, str],
    category_id_to_code: dict[int, str],
) -> str:
    if not rows:
        return "-- dynamic_model_category_relation: (empty)\n"
    lines = [f"-- dynamic_model_category_relation: {len(rows)} row(s), resolve by model_code + category_code\n"]
    for row in rows:
        mcode = model_id_to_code.get(int(row["model_id"]))
        ccode = category_id_to_code.get(int(row["category_id"]))
        if not mcode or not ccode:
            continue
        lines.append(
            f"""INSERT INTO dynamic_model_category_relation (
  model_id, category_id, business_type_code, sort, tenant_id, creator
)
SELECT m.id, c.id, {sql_literal(row.get("business_type_code"))}, {sql_literal(row.get("sort", 0))}, {TENANT_ID}, 'seed'
FROM dynamic_model m
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = {TENANT_ID}
WHERE m.deleted = false AND m.tenant_id = {TENANT_ID}
  AND m.code = {sql_literal(mcode)}
  AND c.code = {sql_literal(ccode)}
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_category_relation r
    WHERE r.deleted = false AND r.tenant_id = {TENANT_ID}
      AND r.model_id = m.id AND r.category_id = c.id
  );
"""
        )
    return "\n".join(lines)


def render_page_configs(rows: list[dict]) -> str:
    if not rows:
        return "-- dynamic_page_config: (empty)\n"
    lines = [f"-- dynamic_page_config: {len(rows)} row(s), upsert by page_code\n"]
    cols = ["config_code", "page_code", "menu_id", "page_type", "config"]
    for row in rows:
        vals = ", ".join(sql_literal(row.get(c)) for c in cols)
        lines.append(
            f"""INSERT INTO dynamic_page_config ({", ".join(cols)}, tenant_id, creator)
VALUES ({vals}, {TENANT_ID}, 'seed')
ON CONFLICT (page_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  config_code = EXCLUDED.config_code,
  menu_id = EXCLUDED.menu_id,
  page_type = EXCLUDED.page_type,
  config = EXCLUDED.config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;
"""
        )
    return "\n".join(lines)


def render_business_type_relations(rows: list[dict]) -> str:
    if not rows:
        return "-- dynamic_business_type_relation: (empty)\n"
    lines = [
        f"-- dynamic_business_type_relation: {len(rows)} row(s), upsert by (source, target, tenant_id)\n"
    ]
    for row in rows:
        lines.append(
            f"""INSERT INTO dynamic_business_type_relation (
  source_business_type_code, target_business_type_code, relation_name,
  auto_create_field, default_field_name, tenant_id, creator
) VALUES (
  {sql_literal(row["source_business_type_code"])}, {sql_literal(row["target_business_type_code"])},
  {sql_literal(row.get("relation_name"))}, {sql_literal(row.get("auto_create_field", True))},
  {sql_literal(row.get("default_field_name"))}, {TENANT_ID}, 'seed'
)
ON CONFLICT (source_business_type_code, target_business_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  relation_name = EXCLUDED.relation_name,
  auto_create_field = EXCLUDED.auto_create_field,
  default_field_name = EXCLUDED.default_field_name,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;
"""
        )
    return "\n".join(lines)


def compose_business_types(
    business_types: list[dict],
    business_type_configs: list[dict],
    business_type_relations: list[dict] | None = None,
) -> str:
    parts = [
        render_business_types(business_types),
        render_business_type_configs(business_type_configs),
    ]
    if business_type_relations:
        parts.append(render_business_type_relations(business_type_relations))
    return "\n\n".join(parts)


def compose_fields(base_fields: list[dict], fields: list[dict]) -> str:
    return "\n\n".join([render_base_fields(base_fields), render_field_library(fields)])


def compose_field_library(
    business_types: list[dict],
    business_type_configs: list[dict],
    base_fields: list[dict],
    fields: list[dict],
) -> str:
    parts = [
        render_business_types(business_types),
        render_business_type_configs(business_type_configs),
        render_base_fields(base_fields),
        render_field_library(fields),
    ]
    return "\n\n".join(parts)


def compose_model_library(
    models: list[dict],
    assignments: list[dict],
    relations: list[dict],
    declarations: list[dict],
    all_fields: list[dict],
) -> str:
    model_id_to_code = id_to_code_map(models)
    field_id_to_code = id_to_code_map(all_fields)
    relation_id_to_key = {
        int(r["id"]): (r["source_model_code"], r["target_model_code"], r.get("field_code"))
        for r in relations
        if r.get("id") is not None
    }
    parts = [
        render_models(models),
        render_model_relations(relations),
        render_model_relation_declarations(declarations, model_id_to_code),
        render_model_field_assignments(
            assignments, model_id_to_code, field_id_to_code, relation_id_to_key
        ),
    ]
    return "\n\n".join(parts)


def compose_model_categories(
    category_types: list[dict],
    categories: list[dict],
    model_category_relations: list[dict],
    models: list[dict],
    page_configs: list[dict],
) -> str:
    category_id_to_code = id_to_code_map(categories)
    model_id_to_code = id_to_code_map(models)
    parts = [
        render_category_types(category_types, category_id_to_code),
        render_categories(categories, category_id_to_code),
        render_rebuild_category_tree(),
        render_model_category_relations(
            model_category_relations, model_id_to_code, category_id_to_code
        ),
        render_page_configs(page_configs),
    ]
    return "\n\n".join(parts)
