#!/usr/bin/env python3
"""
从《弱电集成设备清单》Markdown 导出 equipment 设备型号模型 seed。

语义：
  - 分类：标准设备分类库（equipment_category_taxonomy.py → dynamic_category_equipment.generated.sql）
  - 模型 name：品牌 + 规格型号
  - 关联：型号挂到分类库叶子节点

用法:
  python export_equipment_category_library.py   # 先导出分类库
  python export_equipment_models_from_inventory_md.py
"""

from __future__ import annotations

import argparse
import hashlib
import pathlib
import re
import textwrap
from dataclasses import dataclass, field

from equipment_category_taxonomy import leaf_by_code, resolve_device_leaf

ROOT = pathlib.Path(__file__).resolve().parent
SEED_DIR = ROOT / "system" / "seed"
DEFAULT_MD = pathlib.Path(r"F:\XProject\参考资料\弱电集成设备清单-武汉理工光科.md")
ENTITY_TYPE_CODE = "equipment"
TENANT_ID = 1
FALLBACK_LEAF = "EQCAT-DEV-UNCATEGORIZED"

SKIP_NAME_MARKERS = ("小计", "总计", "合计")
SECTION_ROW_RE = re.compile(r"^\*\*.+\*\*$")


@dataclass
class InventoryRow:
    device_name: str
    spec: str
    brand: str
    subsystem: str
    sources: set[str] = field(default_factory=set)


@dataclass
class ModelRecord:
    code: str
    name: str
    device_name: str
    spec: str
    brand: str
    category_code: str
    category_name: str
    description: str


def esc(value: str) -> str:
    return value.replace("'", "''")


def clean_cell(value: str) -> str:
    text = value.strip().replace("<br>", " ")
    return re.sub(r"\s+", " ", text).strip()


def is_data_row(cells: list[str]) -> bool:
    if len(cells) < 8:
        return False
    seq, device_name, spec = cells[0], cells[1], cells[2]
    if not device_name or not spec:
        return False
    if SECTION_ROW_RE.match(device_name):
        return False
    if any(marker in device_name for marker in SKIP_NAME_MARKERS):
        return False
    if not re.match(r"^\d+$", seq.strip()):
        return False
    if device_name.startswith("**"):
        return False
    return True


def parse_inventory_md(md_path: pathlib.Path) -> list[InventoryRow]:
    text = md_path.read_text(encoding="utf-8")
    rows: list[InventoryRow] = []
    current_section = ""
    current_subsystem = ""
    for line in text.splitlines():
        if line.startswith("## "):
            current_section = line[3:].strip()
            current_subsystem = ""
            continue
        if not line.startswith("|") or line.startswith("| ---"):
            continue
        cells = [clean_cell(part) for part in line.strip("|").split("|")]
        if len(cells) >= 2 and SECTION_ROW_RE.match(cells[0] or cells[1]):
            title = cells[0] or cells[1]
            current_subsystem = re.sub(r"^\*\*|\*\*$", "", title).strip()
            current_subsystem = re.sub(r"^[一二三四五六七八九十]+、", "", current_subsystem).strip()
            continue
        if not is_data_row(cells):
            continue
        rows.append(
            InventoryRow(
                device_name=clean_cell(cells[1]),
                spec=clean_cell(cells[2]),
                brand=clean_cell(cells[7]),
                subsystem=current_subsystem,
                sources={current_section or "unknown"},
            )
        )
    return rows


def merge_rows(rows: list[InventoryRow]) -> dict[tuple[str, str, str], InventoryRow]:
    merged: dict[tuple[str, str, str], InventoryRow] = {}
    for row in rows:
        key = (row.device_name, row.spec, row.brand)
        if key in merged:
            merged[key].sources.update(row.sources)
        else:
            merged[key] = InventoryRow(
                device_name=row.device_name,
                spec=row.spec,
                brand=row.brand,
                subsystem=row.subsystem,
                sources=set(row.sources),
            )
    return merged


def build_model_name(device_name: str, spec: str, brand: str) -> str:
    spec_clean = clean_cell(spec)
    brand_clean = clean_cell(brand)
    generic = {"", "国产", "定制", "国标定制"}
    if brand_clean and brand_clean not in generic:
        if spec_clean.upper().startswith(brand_clean[: min(3, len(brand_clean))].upper()):
            base = spec_clean
        else:
            base = f"{brand_clean} {spec_clean}".strip()
    elif spec_clean and spec_clean not in generic:
        base = spec_clean
    else:
        base = f"{device_name}（{spec_clean or '通用'}）"
    return base[:200]


def model_code_for(device_name: str, spec: str, brand: str) -> str:
    key = f"{device_name}|{spec}|{brand}"
    digest = hashlib.sha256(key.encode("utf-8")).hexdigest()[:16]
    return f"eqm-inv-{digest}"


def build_models(merged: dict[tuple[str, str, str], InventoryRow]) -> tuple[list[ModelRecord], list[str]]:
    leaves = leaf_by_code()
    models: list[ModelRecord] = []
    unmapped: list[str] = []
    used_names: dict[str, int] = {}

    for (_d, _s, _b), row in sorted(merged.items(), key=lambda x: (x[1].device_name, x[1].spec)):
        category_code, category_name = resolve_device_leaf(row.device_name)
        if not category_code:
            category_code = FALLBACK_LEAF
            category_name = leaves.get(FALLBACK_LEAF, (FALLBACK_LEAF, "未分类设备"))[1]
            unmapped.append(row.device_name)
        elif not category_name:
            category_name = leaves.get(category_code, (category_code, category_code))[1]

        base_name = build_model_name(row.device_name, row.spec, row.brand)
        count = used_names.get(base_name, 0)
        name = base_name if count == 0 else f"{row.device_name} · {base_name}"[:200]
        used_names[base_name] = count + 1

        sources = "；".join(sorted(row.sources))
        description = (
            f"品类：{row.device_name}；规格：{row.spec}；品牌：{row.brand or '—'}；"
            f"分类：{category_name}；出处：{sources}"
        )[:500]

        models.append(
            ModelRecord(
                code=model_code_for(row.device_name, row.spec, row.brand),
                name=name,
                device_name=row.device_name,
                spec=row.spec,
                brand=row.brand,
                category_code=category_code,
                category_name=category_name,
                description=description,
            )
        )

    return models, sorted(set(unmapped))


def build_model_sql(models: list[ModelRecord], unmapped: list[str], md_path: pathlib.Path) -> str:
    header = [
        "-- equipment models from weak-current inventory → standard category library",
        f"-- Source MD: {md_path.as_posix()}",
        "-- Regenerate: python export_equipment_models_from_inventory_md.py",
        f"-- Models: {len(models)}; unmapped device names: {len(unmapped)}",
    ]
    if unmapped:
        header.append("-- Unmapped: " + ", ".join(unmapped))
    blocks: list[str] = header + ["", "SET search_path TO dynamicbusiness;", ""]

    for sort_idx, model in enumerate(models, start=1):
        blocks.append(
            textwrap.dedent(
                f"""
                INSERT INTO dynamic_model (
                  code, name, entity_type_code, description, status, sort, tenant_id, creator
                ) VALUES (
                  '{esc(model.code)}', '{esc(model.name)}', '{ENTITY_TYPE_CODE}',
                  '{esc(model.description)}', 1, {sort_idx}, {TENANT_ID}, 'seed'
                )
                ON CONFLICT (code, tenant_id) WHERE deleted = false
                DO UPDATE SET
                  name = EXCLUDED.name,
                  entity_type_code = EXCLUDED.entity_type_code,
                  description = EXCLUDED.description,
                  status = EXCLUDED.status,
                  sort = EXCLUDED.sort,
                  updater = 'seed',
                  update_time = CURRENT_TIMESTAMP;
                """
            ).strip()
        )
        blocks.append("")
    return "\n".join(blocks) + "\n"


def build_relation_sql(models: list[ModelRecord], md_path: pathlib.Path) -> str:
    header = [
        "-- equipment model ↔ standard category library",
        f"-- Source MD: {md_path.as_posix()}",
        f"-- Relations: {len(models)}",
        "",
        "SET search_path TO dynamicbusiness;",
        "",
    ]
    blocks: list[str] = header
    for sort_idx, model in enumerate(models, start=1):
        blocks.append(
            textwrap.dedent(
                f"""
                INSERT INTO dynamic_model_category_relation (
                  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
                )
                SELECT
                  m.id, c.id, '{ENTITY_TYPE_CODE}', m.code, c.code, {sort_idx}, {TENANT_ID}, 'seed'
                FROM dynamic_model m
                JOIN dynamic_category c
                  ON c.deleted = false AND c.tenant_id = {TENANT_ID} AND c.code = '{esc(model.category_code)}'
                WHERE m.deleted = false AND m.tenant_id = {TENANT_ID} AND m.code = '{esc(model.code)}'
                ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
                DO UPDATE SET
                  model_id = EXCLUDED.model_id,
                  category_id = EXCLUDED.category_id,
                  sort = EXCLUDED.sort,
                  updater = 'seed',
                  update_time = CURRENT_TIMESTAMP;
                """
            ).strip()
        )
        blocks.append("")
    return "\n".join(blocks) + "\n"


def resolve_md_path(arg: str | None) -> pathlib.Path:
    if arg:
        path = pathlib.Path(arg)
        if path.is_file():
            return path
        raise FileNotFoundError(f"Markdown not found: {path}")
    if DEFAULT_MD.is_file():
        return DEFAULT_MD
    raise FileNotFoundError(f"Inventory markdown not found: {DEFAULT_MD}")


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("--md", help="Path to inventory markdown")
    args = parser.parse_args()

    md_path = resolve_md_path(args.md)
    rows = parse_inventory_md(md_path)
    merged = merge_rows(rows)
    models, unmapped = build_models(merged)

    model_path = SEED_DIR / "dynamic_model_equipment.generated.sql"
    relation_path = SEED_DIR / "dynamic_model_category_equipment.generated.sql"
    model_path.write_text(build_model_sql(models, unmapped, md_path), encoding="utf-8")
    relation_path.write_text(build_relation_sql(models, md_path), encoding="utf-8")

    print(f"Parsed {len(rows)} rows -> {len(models)} models")
    if unmapped:
        print(f"Warning: {len(unmapped)} devices fell back to {FALLBACK_LEAF}: {', '.join(unmapped)}")
    else:
        print("All inventory devices mapped to standard leaf categories")
    print(f"Wrote {model_path.name}, {relation_path.name}")


if __name__ == "__main__":
    main()
