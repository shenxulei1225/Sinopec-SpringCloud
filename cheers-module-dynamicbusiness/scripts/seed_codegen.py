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
        return "-- dynamic_entity_type: (empty)\n"
    lines = [f"-- dynamic_entity_type: {len(rows)} row(s), upsert by code\n"]
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
            f"""INSERT INTO dynamic_entity_type ({", ".join(cols)})
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
        return "-- dynamic_entity_type_config: (empty)\n"
    lines = [f"-- dynamic_entity_type_config: {len(rows)} row(s), upsert by entity_type_code\n"]
    for row in rows:
        lines.append(
            f"""INSERT INTO dynamic_entity_type_config (
  entity_type_code, name, storage_type, dedicated_table_name, strategy_bean_name,
  enable_rule_engine, description, status, physical_column_mapping, tenant_id, creator
) VALUES (
  {sql_literal(row["entity_type_code"])}, {sql_literal(row["name"])},
  {sql_literal(row["storage_type"])}, {sql_literal(row["dedicated_table_name"])},
  {sql_literal(row.get("strategy_bean_name"))}, {sql_literal(row.get("enable_rule_engine", True))},
  {sql_literal(row.get("description"))}, {sql_literal(row.get("status", 1))},
  {sql_literal(row.get("physical_column_mapping"))}, {TENANT_ID}, 'seed'
)
ON CONFLICT (entity_type_code, tenant_id) WHERE deleted = false
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
        return "-- dynamic_entity_type_base_field: (empty)\n"
    lines = [f"-- dynamic_entity_type_base_field: {len(rows)} row(s), upsert by (entity_type_code, field_code)\n"]
    for row in rows:
        library_field_id = row.get("library_field_id")
        library_field_sql = str(library_field_id) if library_field_id is not None else "NULL"
        lines.append(
            f"""INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, library_field_id, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
) VALUES (
  {sql_literal(row["entity_type_code"])}, {library_field_sql}, {sql_literal(row["field_code"])},
  {sql_literal(row["field_name"])}, {sql_literal(row["data_type"])},
  {sql_literal(row.get("required", False))}, {sql_literal(row.get("default_value"))},
  {sql_literal(row.get("description"))}, {sql_literal(row.get("type_config"))},
  {sql_literal(row.get("sort_order", 0))}, {sql_literal(row.get("status", 1))},
  {TENANT_ID}, 'seed'
)
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  library_field_id = EXCLUDED.library_field_id,
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


def render_models(rows: list[dict]) -> str:
    if not rows:
        return "-- dynamic_model: (empty)\n"
    lines = [f"-- dynamic_model: {len(rows)} row(s), upsert by code\n"]
    for row in rows:
        lines.append(
            f"""INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  {sql_literal(row["code"])}, {sql_literal(row["name"])}, {sql_literal(row["entity_type_code"])},
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
    lines = [f"-- dynamic_model_relation: {len(rows)} row(s), resolve ids by code\n"]
    for row in rows:
        etr_sql = _entity_type_relation_lookup_sql(
            row.get("etr_source_code") or row.get("source_entity_type_code"),
            row.get("etr_target_code") or row.get("target_entity_type_code"),
        )
        lines.append(
            f"""INSERT INTO dynamic_model_relation (
  entity_type_relation_id, source_model_id, source_model_code,
  target_model_id, target_model_code, relation_name, field_code, auto_generated, tenant_id, creator
)
SELECT
  {etr_sql},
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
    lines = [f"-- dynamic_model_relation_declaration: {len(rows)} row(s), upsert by (model_code, target_entity_type)\n"]
    for row in rows:
        model_code = model_id_to_code.get(int(row["model_id"]))
        if not model_code:
            continue
        lines.append(
            f"""INSERT INTO dynamic_model_relation_declaration (model_code, model_id, target_entity_type, tenant_id, creator)
SELECT {sql_literal(model_code)}, m.id, {sql_literal(row["target_entity_type"])}, {TENANT_ID}, 'seed'
FROM dynamic_model m
WHERE m.deleted = false AND m.tenant_id = {TENANT_ID} AND m.code = {sql_literal(model_code)}
ON CONFLICT (model_code, target_entity_type, tenant_id) WHERE deleted = false
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


def _entity_type_relation_lookup_sql(
    source_entity_type_code: str | None,
    target_entity_type_code: str | None,
) -> str:
    if not source_entity_type_code or not target_entity_type_code:
        return "NULL"
    return f"""(
  SELECT etr.id FROM dynamic_entity_type_relation etr
  WHERE etr.deleted = false AND etr.tenant_id = {TENANT_ID}
    AND etr.source_entity_type_code = {sql_literal(source_entity_type_code)}
    AND etr.target_entity_type_code = {sql_literal(target_entity_type_code)}
  LIMIT 1
)"""


def _field_group_lookup_sql(group_code: str | None, group_type: str = "FIELD") -> str:
    if not group_code:
        return "NULL"
    return f"""(
  SELECT g.id FROM dynamic_group g
  WHERE g.deleted = false AND g.tenant_id = {TENANT_ID}
    AND g.group_type = {sql_literal(group_type)} AND g.code = {sql_literal(group_code)}
  LIMIT 1
)"""


def _page_config_lookup_sql(page_code: str | None) -> str:
    if not page_code:
        return "NULL"
    return f"""(
  SELECT pc.id FROM dynamic_page_config pc
  WHERE pc.deleted = false AND pc.tenant_id = {TENANT_ID}
    AND pc.page_code = {sql_literal(page_code)}
  LIMIT 1
)"""


def _ref_library_lookup_sql(
    entity_type_code: str | None,
    ref_target_type: str | None,
    constraint_type: str | None,
) -> str:
    if not entity_type_code or not ref_target_type or not constraint_type:
        return "NULL"
    return f"""(
  SELECT rl.id FROM dynamic_ref_constraint_library rl
  WHERE rl.deleted = false AND rl.tenant_id = {TENANT_ID}
    AND rl.entity_type_code = {sql_literal(entity_type_code)}
    AND rl.ref_target_type = {sql_literal(ref_target_type)}
    AND rl.constraint_type = {sql_literal(constraint_type)}
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
        f"-- dynamic_model_field_assignment: {len(rows)} row(s), upsert by model_code + field_code\n"
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
        fg_sql = _field_group_lookup_sql(
            row.get("field_group_code"),
            row.get("field_group_type") or "FIELD",
        )
        ref_sql = _ref_library_lookup_sql(
            row.get("ref_library_entity_type_code"),
            row.get("ref_library_ref_target_type"),
            row.get("ref_library_constraint_type"),
        )
        lines.append(
            f"""INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  {sql_literal(model_code)}, {sql_literal(field_code)}, m.id, f.id,
  {sql_literal(row.get("required", False))}, {sql_literal(row.get("is_searchable", False))},
  {sql_literal(row.get("is_filterable", False))}, {sql_literal(row.get("is_sortable", False))},
  {sql_literal(row.get("default_value"))}, {sql_literal(row.get("validation_rules"))},
  {sql_literal(row.get("sort", 0))}, {fg_sql},
  {sql_literal(row.get("field_source"))}, {ref_sql},
  {rel_sql}, {sql_literal(row.get("target_entity_type"))}, {TENANT_ID}, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = {TENANT_ID}
WHERE m.deleted = false AND m.tenant_id = {TENANT_ID}
  AND m.code = {sql_literal(model_code)}
  AND f.code = {sql_literal(field_code)}
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;
"""
        )
    return "\n".join(lines)


def assign_model_field_sql(
    model_code: str,
    field_code: str,
    *,
    required: bool = False,
    is_searchable: bool = False,
    is_filterable: bool = False,
    is_sortable: bool = False,
    sort: int = 0,
    field_source: str = "USER_ADDED",
    target_entity_type: str | None = None,
) -> str:
    """单条 model↔field 分配 SQL；幂等键 model_code + field_code（需已执行 V2 迁移）。"""
    return f"""INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  {sql_literal(model_code)}, {sql_literal(field_code)}, m.id, f.id,
  {sql_literal(required)}, {sql_literal(is_searchable)},
  {sql_literal(is_filterable)}, {sql_literal(is_sortable)},
  NULL, NULL,
  {sql_literal(sort)}, NULL,
  {sql_literal(field_source)}, NULL,
  NULL, {sql_literal(target_entity_type)}, {TENANT_ID}, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = {TENANT_ID}
WHERE m.deleted = false AND m.tenant_id = {TENANT_ID}
  AND m.code = {sql_literal(model_code)}
  AND f.code = {sql_literal(field_code)}
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;
"""


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
  parent_id, parent_code, name, code, category_type_code, sort, status, description, tenant_id, creator
) VALUES (
  {parent_sql}, {sql_literal(pcode) if pcode else "NULL"}, {sql_literal(row["name"])}, {sql_literal(row["code"])},
  {sql_literal(row["category_type_code"])}, {sql_literal(row.get("sort", 0))},
  {sql_literal(row.get("status", 1))}, {sql_literal(row.get("description"))}, {TENANT_ID}, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  parent_id = EXCLUDED.parent_id,
  parent_code = EXCLUDED.parent_code,
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
  model_code, category_code, model_id, category_id, entity_type_code, sort, tenant_id, creator
)
SELECT {sql_literal(mcode)}, {sql_literal(ccode)}, m.id, c.id,
  {sql_literal(row.get("entity_type_code"))}, {sql_literal(row.get("sort", 0))}, {TENANT_ID}, 'seed'
FROM dynamic_model m
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = {TENANT_ID}
WHERE m.deleted = false AND m.tenant_id = {TENANT_ID}
  AND m.code = {sql_literal(mcode)}
  AND c.code = {sql_literal(ccode)}
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;
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
        return "-- dynamic_entity_type_relation: (empty)\n"
    lines = [
        f"-- dynamic_entity_type_relation: {len(rows)} row(s), upsert by (source, target, tenant_id)\n"
    ]
    for row in rows:
        lines.append(
            f"""INSERT INTO dynamic_entity_type_relation (
  source_entity_type_code, target_entity_type_code, relation_name,
  auto_create_field, default_field_name, tenant_id, creator
) VALUES (
  {sql_literal(row["source_entity_type_code"])}, {sql_literal(row["target_entity_type_code"])},
  {sql_literal(row.get("relation_name"))}, {sql_literal(row.get("auto_create_field", True))},
  {sql_literal(row.get("default_field_name"))}, {TENANT_ID}, 'seed'
)
ON CONFLICT (source_entity_type_code, target_entity_type_code, tenant_id) WHERE deleted = false
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


def render_field_groups(rows: list[dict], group_id_to_code: dict[int, str] | None = None) -> str:
    """字段池分组 -> dynamic_group (group_type=FIELD)，幂等键 code。"""
    if not rows:
        return "-- dynamic_group(FIELD): (empty)\n"
    id_code = group_id_to_code or id_to_code_map(rows)
    lines = [f"-- dynamic_group(FIELD): {len(rows)} row(s), upsert by (group_type, code)\n"]
    for row in rows:
        pcode = parent_code(row.get("parent_id"), id_code)
        parent_sql = "NULL"
        if pcode:
            parent_sql = f"""(
  SELECT pg.id FROM dynamic_group pg
  WHERE pg.deleted = false AND pg.tenant_id = {TENANT_ID}
    AND pg.group_type = 'FIELD' AND pg.code = {sql_literal(pcode)}
  LIMIT 1
)"""
        lines.append(
            f"""INSERT INTO dynamic_group (
  group_type, code, name, description, parent_id, parent_code, path, level, sort, status, tenant_id, creator
)
SELECT
  'FIELD', {sql_literal(row["code"])}, {sql_literal(row["name"])},
  {sql_literal(row.get("description"))}, {parent_sql}, {sql_literal(pcode) if pcode else "NULL"},
  {sql_literal(row.get("path"))}, {sql_literal(row.get("level", 1))},
  {sql_literal(row.get("sort", 0))}, {sql_literal(row.get("status", 1))},
  {TENANT_ID}, 'zhgl-seed'
WHERE NOT EXISTS (
  SELECT 1 FROM dynamic_group g
  WHERE g.deleted = false AND g.tenant_id = {TENANT_ID}
    AND g.group_type = 'FIELD' AND g.code = {sql_literal(row["code"])}
);

UPDATE dynamic_group g SET
  name = {sql_literal(row["name"])},
  description = {sql_literal(row.get("description"))},
  parent_id = {parent_sql},
  parent_code = {sql_literal(pcode) if pcode else "NULL"},
  sort = {sql_literal(row.get("sort", 0))},
  status = {sql_literal(row.get("status", 1))},
  updater = 'zhgl-seed',
  update_time = CURRENT_TIMESTAMP
WHERE g.deleted = false AND g.tenant_id = {TENANT_ID}
  AND g.group_type = 'FIELD' AND g.code = {sql_literal(row["code"])};
"""
        )
    return "\n".join(lines)


def render_field_group_relations(rows: list[dict]) -> str:
    """字段-分组关联 -> dynamic_group_relation，按 group_code + field_code 解析 id。"""
    if not rows:
        return "-- dynamic_group_relation(FIELD): (empty)\n"
    lines = [
        f"-- dynamic_group_relation(FIELD): {len(rows)} row(s), resolve by group_code + field_code\n"
    ]
    for row in rows:
        lines.append(
            f"""INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT
  'FIELD', g.code, f.code, g.id, f.id, {sql_literal(row.get("sort", 0))}, {TENANT_ID}, 'zhgl-seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = {TENANT_ID}
WHERE g.deleted = false AND g.tenant_id = {TENANT_ID} AND g.group_type = 'FIELD'
  AND g.code = {sql_literal(row["group_code"])}
  AND f.code = {sql_literal(row["field_code"])}
ON CONFLICT (group_type, group_code, target_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  group_id = EXCLUDED.group_id,
  target_id = EXCLUDED.target_id,
  sort = EXCLUDED.sort,
  updater = 'zhgl-seed',
  update_time = CURRENT_TIMESTAMP;
"""
        )
    return "\n".join(lines)


def compose_fields(base_fields: list[dict], fields: list[dict]) -> str:
    return "\n\n".join([render_base_fields(base_fields), render_field_library(fields)])


def compose_zhgl_field_pool(
    fields: list[dict],
    groups: list[dict],
    group_relations: list[dict],
) -> str:
    group_id_to_code = id_to_code_map(groups)
    parts = [
        render_field_library(fields),
        render_field_groups(groups, group_id_to_code),
        render_field_group_relations(group_relations),
    ]
    return "\n\n".join(parts)


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


def _sort_business_tree(rows: list[dict]) -> list[dict]:
    """Parent rows before children for dynamic_business inserts."""
    by_id = {int(r["id"]): r for r in rows if r.get("id") is not None}
    depth_cache: dict[int, int] = {}

    def depth(row_id: int) -> int:
        if row_id in depth_cache:
            return depth_cache[row_id]
        row = by_id.get(row_id)
        pid = row.get("parent_id") if row else None
        if pid in (None, 0) or int(pid) not in by_id:
            depth_cache[row_id] = 0
        else:
            depth_cache[row_id] = depth(int(pid)) + 1
        return depth_cache[row_id]

    return sorted(rows, key=lambda r: (depth(int(r["id"])), r.get("sort") or 0, r.get("code") or ""))


def render_dynamic_business(rows: list[dict]) -> str:
    if not rows:
        return "-- dynamic_business: (empty)\n"
    id_code = id_to_code_map(rows)
    lines = [f"-- dynamic_business: {len(rows)} row(s), upsert by code\n"]
    for row in _sort_business_tree(rows):
        pcode = parent_code(row.get("parent_id"), id_code)
        parent_sql = "NULL"
        if pcode:
            parent_sql = f"""(
  SELECT pb.id FROM dynamic_business pb
  WHERE pb.deleted = false AND pb.tenant_id = {TENANT_ID} AND pb.code = {sql_literal(pcode)}
  LIMIT 1
)"""
        lines.append(
            f"""INSERT INTO dynamic_business (
  code, name, parent_id, node_kind, description, icon, alias, sort, status, tenant_id, creator
) VALUES (
  {sql_literal(row["code"])}, {sql_literal(row["name"])}, {parent_sql},
  {sql_literal(row.get("node_kind"))}, {sql_literal(row.get("description"))},
  {sql_literal(row.get("icon"))}, {sql_literal(row.get("alias"))},
  {sql_literal(row.get("sort", 0))}, {sql_literal(row.get("status", "1"))},
  {TENANT_ID}, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  parent_id = EXCLUDED.parent_id,
  node_kind = EXCLUDED.node_kind,
  description = EXCLUDED.description,
  icon = EXCLUDED.icon,
  alias = EXCLUDED.alias,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;
"""
        )
    return "\n".join(lines)


def render_dynamic_business_entries(rows: list[dict]) -> str:
    if not rows:
        return "-- dynamic_business_entry: (empty)\n"
    lines = [f"-- dynamic_business_entry: {len(rows)} row(s), upsert by (business code, entry code)\n"]
    for row in rows:
        page_sql = _page_config_lookup_sql(row.get("page_config_code"))
        lines.append(
            f"""INSERT INTO dynamic_business_entry (
  business_id, code, name, entry_type, entity_type_code, scope_config, page_config_id,
  sort, status, tenant_id, creator
)
SELECT
  b.id, {sql_literal(row["code"])}, {sql_literal(row["name"])},
  {sql_literal(row.get("entry_type"))}, {sql_literal(row.get("entity_type_code"))},
  {sql_literal(row.get("scope_config"))}, {page_sql},
  {sql_literal(row.get("sort", 0))}, {sql_literal(row.get("status", "1"))},
  {TENANT_ID}, 'seed'
FROM dynamic_business b
WHERE b.deleted = false AND b.tenant_id = {TENANT_ID}
  AND b.code = {sql_literal(row["business_code"])}
ON CONFLICT (business_id, code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entry_type = EXCLUDED.entry_type,
  entity_type_code = EXCLUDED.entity_type_code,
  scope_config = EXCLUDED.scope_config,
  page_config_id = EXCLUDED.page_config_id,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;
"""
        )
    return "\n".join(lines)


def render_business_capabilities(rows: list[dict]) -> str:
    if not rows:
        return "-- business_capability: (empty)\n"
    lines = [f"-- business_capability: {len(rows)} row(s), upsert by entity_type_code\n"]
    for row in rows:
        lines.append(
            f"""INSERT INTO business_capability (
  entity_type_code, capability_full, version, business_category, tenant_id, creator
) VALUES (
  {sql_literal(row["entity_type_code"])}, {sql_literal(row.get("capability_full"))},
  {sql_literal(row.get("version"))}, {sql_literal(row.get("business_category"))},
  {TENANT_ID}, 'seed'
)
ON CONFLICT (entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  capability_full = EXCLUDED.capability_full,
  version = EXCLUDED.version,
  business_category = EXCLUDED.business_category,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;
"""
        )
    return "\n".join(lines)


def compose_field_groups(groups: list[dict], group_relations: list[dict]) -> str:
    group_id_to_code = id_to_code_map(groups)
    return "\n\n".join(
        [
            render_field_groups(groups, group_id_to_code),
            render_field_group_relations(group_relations),
        ]
    )


def compose_business_portal(
    businesses: list[dict],
    entries: list[dict],
) -> str:
    return "\n\n".join([render_dynamic_business(businesses), render_dynamic_business_entries(entries)])


def compose_capabilities(rows: list[dict]) -> str:
    return render_business_capabilities(rows)


def compose_industry_field_library(
    fields: list[dict],
    groups: list[dict],
    group_relations: list[dict],
) -> str:
    """Full field pool + FIELD groups for platform-import seed / smart-station 04."""
    return compose_zhgl_field_pool(fields, groups, group_relations)
