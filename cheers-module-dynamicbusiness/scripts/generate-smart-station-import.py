#!/usr/bin/env python3
"""生成 platform-import/smart-station 产品包 SQL（模型 + 扩展字段 + 字段分配，均按 code 幂等）。"""
from __future__ import annotations

from datetime import date
from pathlib import Path

from seed_codegen import (
    assign_model_field_sql,
    render_field_group_relations,
    render_field_groups,
    render_field_library,
    render_models,
)
from smart_station_catalog import (
    ASSIGNMENTS,
    FIELD_GROUP,
    field_group_relations,
    fields_as_dicts,
    models_as_dicts,
)

SCRIPT_DIR = Path(__file__).resolve().parent
OUT_DIR = SCRIPT_DIR / "platform-import/smart-station"
HEADER = f"""-- ============================================================================
-- 智慧站场产品包 · Generated: {date.today()} by scripts/generate-smart-station-import.py
--
-- 约定：幂等键 model.code / field.code；关联 INSERT 解析 id，不写 surrogate id。
-- 依赖：先导入 platform-import/system/（实体类型、基础字段、设备模型等）
-- ============================================================================

SET search_path TO dynamicbusiness;

"""


def main() -> None:
    OUT_DIR.mkdir(parents=True, exist_ok=True)

    groups = [{**FIELD_GROUP, "id": 1, "parent_id": None}]
    group_id_to_code = {1: FIELD_GROUP["code"]}

    ext_fields_sql = HEADER + render_field_groups(groups, group_id_to_code)
    ext_fields_sql += "\n\n" + render_field_library(fields_as_dicts())
    ext_fields_sql += "\n\n" + render_field_group_relations(field_group_relations())
    (OUT_DIR / "04_extension_fields.sql").write_text(ext_fields_sql, encoding="utf-8")

    models_sql = HEADER + render_models(models_as_dicts())
    (OUT_DIR / "05_models.sql").write_text(models_sql, encoding="utf-8")

    assign_lines = [
        HEADER.strip(),
        f"-- dynamic_model_field_assignment: {len(ASSIGNMENTS)} row(s), upsert by model_code + field_code\n",
    ]
    for item in ASSIGNMENTS:
        assign_lines.append(
            assign_model_field_sql(
                item.model_code,
                item.field_code,
                required=item.required,
                is_searchable=item.is_searchable,
                is_filterable=item.is_filterable,
                is_sortable=item.is_sortable,
                sort=item.sort,
                field_source="USER_ADDED",
            )
        )
    (OUT_DIR / "06_model_field_assignments.sql").write_text(
        "\n".join(assign_lines) + "\n", encoding="utf-8"
    )

    print(f"written: {OUT_DIR}/04_extension_fields.sql")
    print(f"written: {OUT_DIR}/05_models.sql")
    print(f"written: {OUT_DIR}/06_model_field_assignments.sql")


if __name__ == "__main__":
    main()
