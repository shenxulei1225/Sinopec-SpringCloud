#!/usr/bin/env python3
"""
从管廊历史 dump 导出 equipment 实体 seed，并按当前型号库 code 重解析 model_id。

来源：smart-corridor/seed/entities_optional.sql（pg_dump，约 5342 条 ent_equipment）
目标：system/seed/dynamic_entity_equipment.generated.sql

用法:
  python export_equipment_entities_from_corridor_dump.py
  python export_equipment_entities_from_corridor_dump.py --source smart-corridor/seed/entities_optional.sql
"""

from __future__ import annotations

import argparse
import json
import re
import textwrap
from collections import defaultdict
from dataclasses import dataclass
from pathlib import Path

ROOT = Path(__file__).resolve().parent
DEFAULT_SOURCE = ROOT / "smart-corridor/seed/entities_optional.sql"
MODEL_SEED = ROOT / "system/seed/dynamic_model_equipment.generated.sql"
LEGACY_CAPABILITY = ROOT / "system/seed/business_capability.sql"
OUTPUT = ROOT / "system/seed/dynamic_entity_equipment.generated.sql"
TENANT_ID = 1
BATCH_SIZE = 100

# 历史 dump 中 numeric model_id → 清单品类（用于 JKC / BIM 十六进制名）
LEGACY_MODEL_ID_TO_DEVICE: dict[int, str] = {
    44: "网络摄像机",
    45: "报警主机",
    46: "红外双鉴探测器",
    47: "门禁控制器",
    48: "LED屏",
    49: "网络摄像机",
    50: "IP电话",
    51: "光纤收发器",
    52: "功分器",
    53: "无线远端机（光纤直放远端机）",
    54: "定向天线",
    55: "人员定位主机（全向读写器含增益天线）",
    56: "移动终端",
    57: "分区ACU柜",
    58: "温湿度传感器",
    59: "O2传感器",
    60: "CO2传感器",
    61: "企业级核心交换机",
    97: "检修箱",
    100: "应急灯",
    101: "阀门",
    102: "气溶胶",
    104: "照明",
    117: "网络摄像机",
    118: "消防",
    119: "消防泵",
    120: "CO2传感器",
    121: "O2传感器",
    122: "温湿度传感器",
    123: "IP电话",
    124: "监控控制柜（19\"机柜）",
    125: "门禁控制器",
    126: "按钮箱",
}

# BIM custom_fields.deviceTypeName / 路段实体名中间段 → 清单品类
DEVICE_SYNONYMS: dict[str, str] = {
    "红外探测器": "红外双鉴探测器",
    "O2传感器": "O2传感器",
    "CO2传感器": "CO2传感器",
    "二氧化碳传感器": "CO2传感器",
    "氧气传感器": "O2传感器",
    "ACU柜": "分区ACU柜",
    "环网交换机（安防交换机）": "环网交换机（安防交换机）",
    "LED显示屏": "LED屏",
    "监控控制柜": "监控控制柜（19\"机柜）",
    "控制箱": "监控控制柜（19\"机柜）",
    "控制柜": "监控控制柜（19\"机柜）",
    "视频监控": "网络摄像机",
    "二氧化碳检测仪": "CO2传感器",
    "氧气检测仪": "O2传感器",
    "温湿度检测仪": "温湿度传感器",
    "门禁": "门禁控制器",
    "无线远端机": "无线远端机（光纤直放远端机）",
    "人员定位主机": "人员定位主机（全向读写器含增益天线）",
}

# 无弱电清单型号时，回退到 system 遗留 MODEL-*（与 business_capability 摘要一致）
FALLBACK_MODEL_CODE_BY_DEVICE: dict[str, str] = {
    "照明": "MODEL-70d2b066120c4b969f0b661d1d2fb664",  # LED显示屏 → 一般照明占位
    "检修箱": "MODEL-0e171b9d21024fd183a1cb355e1b8c08",  # 220V电缆 → 占位
    "阀门": "MODEL-6671ca830f4742229c0a00ff531f974c",  # 消防
    "按钮箱": "MODEL-5284b71c69d946ae88c0848bfbbb0d7c",  # 测试模型占位
    "消防": "MODEL-6671ca830f4742229c0a00ff531f974c",
    "消防泵": "MODEL-5a9b87c42571486998d363101c54ec8c",
    "水泵": "MODEL-5a9b87c42571486998d363101c54ec8c",
    "气溶胶": "MODEL-3ae58694e5ab4dc4a078c6e4b548f617",
    "应急灯": "MODEL-1966e63fec48402f9b60280e66faea0f",
}

ENTITY_INSERT_RE = re.compile(
    r"INSERT INTO dynamicbusiness\.ent_equipment \(.*?\) OVERRIDING SYSTEM VALUE VALUES "
    r"\((\d+), 'equipment', (\d+), '((?:''|[^'])*)', '((?:''|[^'])*)',",
    re.DOTALL,
)
ENTITY_SORT_RE = re.compile(r",\s*(\d+)\);\s*$")

MODEL_CATEGORY_RE = re.compile(
    r"'(eqm-inv-[a-f0-9]+)'.*?'品类：([^；]+)；",
    re.DOTALL,
)


@dataclass
class EntityRow:
    id: int
    legacy_model_id: int
    name: str
    custom_fields: str
    tree_path: str
    code: str | None
    sort: int


def esc(value: str) -> str:
    return value.replace("'", "''")


def unescape_sql_string(value: str) -> str:
    return value.replace("''", "'")


def load_legacy_model_by_device_name() -> dict[str, str]:
    """business_capability 摘要里的 MODEL-* 短名，与型号列展示一致。"""
    if not LEGACY_CAPABILITY.is_file():
        return {}
    text = LEGACY_CAPABILITY.read_text(encoding="utf-8", errors="ignore")
    mapping: dict[str, str] = {}
    for code, name in re.findall(r'"modelCode": "(MODEL-[^"]+)", "modelName": "([^"]+)"', text):
        mapping[name.strip()] = code
    return mapping


def load_inventory_model_index() -> dict[str, str]:
    text = MODEL_SEED.read_text(encoding="utf-8")
    index: dict[str, str] = {}
    for code, category in MODEL_CATEGORY_RE.findall(text):
        index.setdefault(category.strip(), code)
    return index


def normalize_device_name(raw: str) -> str:
    name = raw.strip()
    return DEVICE_SYNONYMS.get(name, name)


def device_from_entity_name(name: str) -> str | None:
    if name.startswith("JKC-"):
        return None
    parts = name.split("-")
    if len(parts) >= 3 and parts[-1].isdigit():
        return normalize_device_name(parts[1])
    return None


def parse_custom_fields(raw: str) -> dict:
    text = unescape_sql_string(raw)
    try:
        return json.loads(text)
    except json.JSONDecodeError:
        return {}


def resolve_device_name(entity: EntityRow) -> str | None:
    cf = parse_custom_fields(entity.custom_fields)
    from_name = device_from_entity_name(entity.name)
    if from_name:
        return from_name
    if cf.get("deviceTypeName"):
        return normalize_device_name(str(cf["deviceTypeName"]))
    if cf.get("39"):
        # JKC 等：规格字段有时即设备类型
        spec = str(cf["39"])
        if any("\u4e00" <= ch <= "\u9fff" for ch in spec) and len(spec) <= 20:
            return normalize_device_name(spec)
    return normalize_device_name(LEGACY_MODEL_ID_TO_DEVICE.get(entity.legacy_model_id, "")) or None


def resolve_model_code(
    device: str | None,
    inventory_index: dict[str, str],
    legacy_by_name: dict[str, str],
) -> str | None:
    if not device:
        return None
    # 优先 legacy MODEL-*：型号列展示「网络摄像机等短名，eqm-inv 为规格全名
    if device in legacy_by_name:
        return legacy_by_name[device]
    if device in inventory_index:
        return inventory_index[device]
    if device in FALLBACK_MODEL_CODE_BY_DEVICE:
        return FALLBACK_MODEL_CODE_BY_DEVICE[device]
    # 品类包含关系：如「红外双鉴探测器」←「红外探测器」
    for category, code in inventory_index.items():
        if device in category or category in device:
            return code
    for name, code in legacy_by_name.items():
        if device in name or name in device:
            return code
    return None


def parse_entities(source: Path) -> list[EntityRow]:
    text = source.read_text(encoding="utf-8")
    rows: list[EntityRow] = []
    for match in ENTITY_INSERT_RE.finditer(text):
        entity_id = int(match.group(1))
        legacy_model_id = int(match.group(2))
        name = unescape_sql_string(match.group(3))
        custom_fields = match.group(4)
        line_end = text.find(");", match.end())
        line = text[match.start() : line_end + 2] if line_end != -1 else ""
        sort_match = ENTITY_SORT_RE.search(line)
        sort = int(sort_match.group(1)) if sort_match else entity_id
        code = None
        rows.append(
            EntityRow(
                id=entity_id,
                legacy_model_id=legacy_model_id,
                name=name,
                custom_fields=custom_fields,
                tree_path=f"/{entity_id}/",
                code=code,
                sort=sort,
            )
        )
    return rows


def build_sql(entities: list[tuple[EntityRow, str]]) -> str:
    header = [
        "-- equipment entities from corridor historical dump, model_id resolved by dynamic_model.code",
        f"-- Source: smart-corridor/seed/entities_optional.sql",
        f"-- Regenerate: python export_equipment_entities_from_corridor_dump.py",
        f"-- Entities: {len(entities)}",
        "",
        "SET search_path TO dynamicbusiness;",
        "",
        "-- 退役开发联调样例（DEV-EQ-*），避免与历史实例并存",
        "UPDATE ent_equipment SET deleted = true, updater = 'seed', update_time = CURRENT_TIMESTAMP",
        "WHERE code LIKE 'DEV-EQ-%' AND deleted = false;",
        "",
    ]
    blocks: list[str] = header

    for batch_start in range(0, len(entities), BATCH_SIZE):
        batch = entities[batch_start : batch_start + BATCH_SIZE]
        value_lines: list[str] = []
        for entity, model_code in batch:
            cf_dict = parse_custom_fields(entity.custom_fields)
            code = (
                entity.code
                or str(cf_dict.get("38") or cf_dict.get("deviceCode") or f"EQ-SEED-{entity.id}")
            )
            cf = esc(unescape_sql_string(entity.custom_fields))
            value_lines.append(
                f"    ({entity.id}, '{esc(code)}', '{esc(entity.name)}', "
                f"'{esc(entity.tree_path)}', {entity.sort}, '{esc(model_code)}', '{cf}')"
            )
        values_sql = ",\n".join(value_lines)
        blocks.append(
            textwrap.dedent(
                f"""
                INSERT INTO ent_equipment (
                    id, entity_type_code, model_id, name, code, tenant_id, creator,
                    tree_path, sort, status, deleted, custom_fields
                )
                OVERRIDING SYSTEM VALUE
                SELECT
                    v.id,
                    'equipment',
                    m.id,
                    v.name,
                    v.code,
                    {TENANT_ID},
                    'seed',
                    v.tree_path,
                    v.sort,
                    1,
                    false,
                    v.custom_fields::jsonb
                FROM (
                    VALUES
                {values_sql}
                ) AS v(id, code, name, tree_path, sort, model_code, custom_fields)
                JOIN dynamic_model m
                  ON m.code = v.model_code
                 AND m.entity_type_code = 'equipment'
                 AND m.deleted = false
                 AND m.tenant_id = {TENANT_ID}
                ON CONFLICT (id) DO UPDATE SET
                    model_id = EXCLUDED.model_id,
                    name = EXCLUDED.name,
                    code = EXCLUDED.code,
                    tree_path = EXCLUDED.tree_path,
                    sort = EXCLUDED.sort,
                    status = EXCLUDED.status,
                    deleted = false,
                    custom_fields = EXCLUDED.custom_fields,
                    updater = 'seed',
                    update_time = CURRENT_TIMESTAMP;
                """
            ).strip()
        )
        blocks.append("")

    blocks.append(
        textwrap.dedent(
            f"""
            SELECT setval(
                pg_get_serial_sequence('dynamicbusiness.ent_equipment', 'id'),
                GREATEST(
                    (SELECT COALESCE(MAX(id), 1) FROM dynamicbusiness.ent_equipment),
                    1
                )
            );
            """
        ).strip()
    )
    blocks.append("")
    return "\n".join(blocks)


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("--source", type=Path, default=DEFAULT_SOURCE)
    args = parser.parse_args()

    inventory_index = load_inventory_model_index()
    legacy_by_name = load_legacy_model_by_device_name()
    entities = parse_entities(args.source)

    mapped: list[tuple[EntityRow, str]] = []
    skipped: dict[str, int] = defaultdict(int)

    for entity in entities:
        device = resolve_device_name(entity)
        model_code = resolve_model_code(device, inventory_index, legacy_by_name)
        if not model_code:
            skipped[device or f"legacy_model_id={entity.legacy_model_id}"] += 1
            continue
        mapped.append((entity, model_code))

    sql = build_sql(mapped)
    OUTPUT.write_text(sql, encoding="utf-8")

    print(f"Parsed {len(entities)} entities from dump")
    print(f"Mapped {len(mapped)} entities -> {OUTPUT.name}")
    if skipped:
        print(f"Skipped {sum(skipped.values())} unmapped entities:")
        for key, count in sorted(skipped.items(), key=lambda x: -x[1])[:15]:
            print(f"  {key}: {count}")
    print(f"Wrote {OUTPUT}")


if __name__ == "__main__":
    main()
