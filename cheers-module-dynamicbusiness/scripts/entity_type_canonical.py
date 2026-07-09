"""Canonical entity type codes and dedicated table names (English, aligned with Flyway V1)."""

from __future__ import annotations

# Legacy pinyin / alias entity_type_code → canonical code (dynamic_entity_type.code)
LEGACY_ENTITY_TYPE_CODE_TO_CANONICAL: dict[str, str] = {
    "shou_fei": "billing",
    "ke_hu": "customer",
    "jian_cha_nei_rong": "inspection_item",
    "dian_wei": "inspection_point",
    "lu_xian_guan_li": "route",
    "xun_jian": "patrol",
    "ying_ji_zi_yuan": "emergency_resource",
    "ying_ji_dui_wu": "emergency_team",
    "spare_part": "spare_parts",
}

# Legacy pinyin dedicated_table_name → canonical (matches V1 DDL)
LEGACY_DEDICATED_TABLE_TO_CANONICAL: dict[str, str] = {
    "ent_shou_fei": "ent_billing",
    "ent_ke_hu": "ent_customer",
    "ent_jian_cha_nei_rong": "ent_inspection_item",
    "ent_dian_wei": "ent_inspection_point",
    "ent_lu_xian_guan_li": "ent_route",
    "ent_xun_jian": "ent_patrol",
    "ent_ying_ji_zi_yuan": "ent_emergency_resource",
    "ent_ying_ji_dui_wu": "ent_emergency_team",
}


def canonical_entity_type_code(code: str | None) -> str | None:
    if not code:
        return code
    trimmed = code.strip()
    return LEGACY_ENTITY_TYPE_CODE_TO_CANONICAL.get(trimmed, trimmed)


def canonical_dedicated_table_name(entity_type_code: str, table_name: str | None = None) -> str:
    """Return English dedicated table name for entity type (V1-aligned)."""
    code = canonical_entity_type_code(entity_type_code) or entity_type_code
    if table_name:
        legacy = table_name.strip()
        if legacy in LEGACY_DEDICATED_TABLE_TO_CANONICAL:
            return LEGACY_DEDICATED_TABLE_TO_CANONICAL[legacy]
    # V1 DDL: spare_parts entity type uses ent_spare_part (singular)
    if code == "spare_parts":
        return "ent_spare_part"
    return f"ent_{code}"


def normalize_model_row(row: dict) -> dict:
    item = dict(row)
    code = item.get("entity_type_code")
    if code:
        item["entity_type_code"] = canonical_entity_type_code(str(code))
    target = item.get("target_entity_type")
    if target:
        item["target_entity_type"] = canonical_entity_type_code(str(target))
    return item


def normalize_entity_type_config_row(row: dict) -> dict:
    item = dict(row)
    etc = item.get("entity_type_code")
    if etc:
        canonical = canonical_entity_type_code(str(etc))
        item["entity_type_code"] = canonical
        item["dedicated_table_name"] = canonical_dedicated_table_name(
            str(canonical),
            str(item.get("dedicated_table_name") or ""),
        )
    return item


def normalize_entity_type_row(row: dict) -> dict:
    item = dict(row)
    code = item.get("code")
    if code:
        canonical = canonical_entity_type_code(str(code))
        item["code"] = canonical
        item["dedicated_table_name"] = canonical_dedicated_table_name(
            str(canonical),
            str(item.get("dedicated_table_name") or ""),
        )
    return item
