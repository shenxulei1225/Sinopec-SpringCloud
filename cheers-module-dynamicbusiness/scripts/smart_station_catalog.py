"""智慧站场产品包定稿：设施 / 站内分区模型与模型扩展字段分配。

基础字段（REF_REGION、boundary_geojson 等）已在 dynamic_entity_type_base_field；
本目录仅定义模型壳 + 模型专属性（model_field_assignment）。

region 模型与分类节点不在 SQL 预置（见 docs/动态业务/地理区域-设施-站内分区定稿.md）。
"""
from __future__ import annotations

from dataclasses import dataclass


@dataclass(frozen=True)
class ModelDef:
    code: str
    name: str
    entity_type_code: str
    description: str
    sort: int


@dataclass(frozen=True)
class FieldDef:
    code: str
    name: str
    type: str
    unit: str | None = None
    description: str | None = None
    options: str | None = None


@dataclass(frozen=True)
class AssignmentDef:
    model_code: str
    field_code: str
    required: bool = False
    is_searchable: bool = False
    is_filterable: bool = False
    is_sortable: bool = False
    sort: int = 0


def _enum(*labels: str) -> str:
    import json

    return json.dumps([{"label": x, "value": x} for x in labels], ensure_ascii=False)


MODELS: tuple[ModelDef, ...] = (
    ModelDef("MODEL-FACILITY-STATION", "站场", "facility", "油气储运站场等设施点", 1),
    ModelDef("MODEL-FACILITY-PLANT", "厂区", "facility", "工厂、化工园区厂区", 2),
    ModelDef("MODEL-FACILITY-DEPOT", "油库", "facility", "油库设施点", 3),
    ModelDef("MODEL-FACILITY-OFFICE", "机关/楼宇", "facility", "机关办公与楼宇设施点", 4),
    ModelDef("MODEL-ZONE-TANK-GROUP", "罐组", "zone", "储罐分区", 1),
    ModelDef("MODEL-ZONE-WAREHOUSE", "库棚", "zone", "仓储分区", 2),
    ModelDef("MODEL-ZONE-FUNCTIONAL", "功能分区", "zone", "装卸区、消防分区等", 3),
    ModelDef("MODEL-ZONE-BUILDING", "楼栋", "zone", "厂区内楼栋", 4),
)

# 模型扩展字段（写入 dynamic_field；基础 REF/边界字段走 entity_type_base_field）
EXTENSION_FIELDS: tuple[FieldDef, ...] = (
    FieldDef("FLD-FAC-EXT-001", "设计罐容", "DECIMAL", "m³", "站场储罐总设计容量"),
    FieldDef("FLD-FAC-EXT-002", "投运日期", "DATE", description="设施正式投运日期"),
    FieldDef(
        "FLD-FAC-EXT-003",
        "危化等级",
        "ENUM",
        description="厂区危险化学品等级",
        options=_enum("一般", "较大", "重大", "特别重大"),
    ),
    FieldDef("FLD-FAC-EXT-004", "库容", "DECIMAL", "t", "油库设计库容"),
    FieldDef("FLD-FAC-EXT-005", "建筑面积", "DECIMAL", "m²", "机关/楼宇建筑面积"),
    FieldDef("FLD-ZONE-EXT-001", "占地面积", "DECIMAL", "m²", "分区占地面积（模型扩展）"),
)

FIELD_GROUP = {
    "code": "FG-BIZ-SPATIAL",
    "name": "空间与设施",
    "description": "站场/设施/分区模型扩展字段",
    "sort": 15,
    "level": 1,
    "status": 1,
}

ASSIGNMENTS: tuple[AssignmentDef, ...] = (
    AssignmentDef("MODEL-FACILITY-STATION", "FLD-FAC-EXT-001", is_searchable=True, sort=10),
    AssignmentDef("MODEL-FACILITY-STATION", "FLD-FAC-EXT-002", is_searchable=True, is_sortable=True, sort=20),
    AssignmentDef("MODEL-FACILITY-PLANT", "FLD-FAC-EXT-003", is_filterable=True, sort=10),
    AssignmentDef("MODEL-FACILITY-PLANT", "FLD-FAC-EXT-002", is_searchable=True, sort=20),
    AssignmentDef("MODEL-FACILITY-DEPOT", "FLD-FAC-EXT-004", is_searchable=True, sort=10),
    AssignmentDef("MODEL-FACILITY-DEPOT", "FLD-FAC-EXT-002", is_searchable=True, sort=20),
    AssignmentDef("MODEL-FACILITY-OFFICE", "FLD-FAC-EXT-005", sort=10),
    AssignmentDef("MODEL-FACILITY-OFFICE", "FLD-FAC-EXT-002", is_searchable=True, sort=20),
    AssignmentDef("MODEL-ZONE-TANK-GROUP", "FLD-ZONE-EXT-001", sort=10),
    AssignmentDef("MODEL-ZONE-WAREHOUSE", "FLD-ZONE-EXT-001", sort=10),
    AssignmentDef("MODEL-ZONE-FUNCTIONAL", "FLD-ZONE-EXT-001", sort=10),
    AssignmentDef("MODEL-ZONE-BUILDING", "FLD-ZONE-EXT-001", sort=10),
)


def models_as_dicts() -> list[dict]:
    return [
        {
            "code": m.code,
            "name": m.name,
            "entity_type_code": m.entity_type_code,
            "description": m.description,
            "status": 1,
            "sort": m.sort,
            "field_groups_config": None,
        }
        for m in MODELS
    ]


def fields_as_dicts() -> list[dict]:
    return [
        {
            "code": f.code,
            "name": f.name,
            "type": f.type,
            "unit": f.unit,
            "description": f.description,
            "options": f.options,
            "source": "USER",
            "status": 1,
        }
        for f in EXTENSION_FIELDS
    ]


def field_group_relations() -> list[dict]:
    return [{"group_code": FIELD_GROUP["code"], "field_code": f.code} for f in EXTENSION_FIELDS]
