"""Draft canonical base fields — for business-by-business review with stakeholders.

Principles:
- Entity universal: name (名称), code (唯一业务编码), status, guid, model_id, parent_id — NOT base fields.
- No *_name or *_code prefixed duplicates in base fields (e.g. no equipment_code, region_name).
- Base fields = all-model-shared slots (including universal REF); define once on business type.
- Real-time / derived values live in linked modules; REF slot in base fields, not ent_* columns.

apply-base-fields.py upserts only by default; use --prune only after explicit confirmation.
"""

from __future__ import annotations

from typing import Any

# type_config for REL_* : refField only (no libraryFieldCode)
REL_REGION = {
    "refField": "F-cc746ce0224145af88d5428d0b03213a",
    "dataFormat": "labeled",
    "supportLabels": True,
    "allowCustomLabels": True,
}
REL_EQUIPMENT = {
    "refField": "REF-MODEL_c55e44e65d5840c6ad73f0a0eb7e94d9_id",
    "dataFormat": "labeled",
    "supportLabels": True,
    "allowCustomLabels": True,
}
REL_DIAN_WEI = {
    "refField": "F-6c74f28ef78e4a6d84566f97e072b7cf",
    "dataFormat": "labeled",
    "supportLabels": True,
    "allowCustomLabels": True,
}
REL_KE_HU = {
    "refField": "F-9d6424ad23d94095a2421741cee572e5",
    "dataFormat": "labeled",
    "supportLabels": True,
    "allowCustomLabels": True,
}
REL_SPARE_PART = {
    "refField": "F-3d1012a56e8e448aa096bae0f353c21a",
    "dataFormat": "labeled",
    "supportLabels": True,
    "allowCustomLabels": True,
}
REL_FACILITY_REGION = {"refField": "F-spatial-facility-ref-region"}
REL_ZONE_FACILITY = {"refField": "F-spatial-zone-ref-facility"}
REL_EQUIPMENT_FACILITY = {"refField": "F-spatial-equipment-ref-facility"}
REL_EQUIPMENT_ZONE = {"refField": "F-spatial-equipment-ref-zone"}


def _f(
    field_code: str,
    field_name: str,
    data_type: str,
    *,
    required: bool = False,
    sort_order: int = 0,
    type_config: dict | None = None,
    description: str | None = None,
) -> dict[str, Any]:
    return {
        "field_code": field_code,
        "field_name": field_name,
        "data_type": data_type,
        "required": required,
        "sort_order": sort_order,
        "type_config": type_config,
        "description": description,
    }


# fmt: off
CANONICAL_BASE_FIELDS: dict[str, list[dict[str, Any]]] = {
    # equipment — 定稿（2026-07-05）；29 项台账 + 7 项共有 REF 槽位
    "equipment": [
        # 身份（业务编码见实体 code；名称见实体 name）
        _f("asset_code", "资产编号", "TEXT", sort_order=11),
        _f("serial_number", "出厂序列号", "TEXT", sort_order=12),
        _f("barcode", "条码/二维码", "TEXT", sort_order=13),
        _f("archive_no", "档案编号", "TEXT", sort_order=14),
        # 分类
        _f("equipment_type", "设备类型", "NUMBER", required=True, sort_order=20,
           description="equipment_type 分类维度；特种设备等通过分类体现"),
        _f("criticality", "重要等级", "ENUM", sort_order=21),
        # 制造与规格
        _f("equipment_model", "规格型号", "TEXT", sort_order=30),
        _f("manufacturer", "生产厂家", "TEXT", sort_order=31),
        _f("brand", "品牌", "TEXT", sort_order=32),
        _f("supplier", "供应商", "TEXT", sort_order=33),
        _f("place_of_origin", "产地", "TEXT", sort_order=34),
        # 位置与空间（静态台账位置，非实时轨迹）
        _f("install_location", "安装位置", "TEXT", sort_order=40),
        _f("REF_FACILITY", "所属设施", "REF", required=True, sort_order=42,
           type_config=REL_EQUIPMENT_FACILITY,
           description="设备归属设施点；区划通过设施间接得知"),
        _f("REF_ZONE", "所属分区", "REF", sort_order=43,
           type_config=REL_EQUIPMENT_ZONE,
           description="可选站内精细定位"),
        _f("REF_REGION", "所属区域", "REF_Multi", sort_order=44,
           type_config=REL_REGION,
           description="历史兼容；新数据以 REF_FACILITY 为主归属"),
        _f("coordinate_3d", "三维坐标", "TEXT", sort_order=41),
        _f("coordinate_gis", "GIS坐标", "TEXT", sort_order=42),
        _f("model_3d", "三维模型", "TEXT", sort_order=43,
           description="三维模型资源标识或路径"),
        # 人员（台账归属；若对接人员主数据可改为 REF，不在此表冗余快照）
        _f("responsible_person", "负责人", "TEXT", sort_order=50),
        _f("custodian", "使用人", "TEXT", sort_order=51),
        _f("maintainer", "维护人", "TEXT", sort_order=52),
        # 生命周期（建档时确定的日期/年限）
        _f("install_date", "安装日期", "DATE", sort_order=60),
        _f("commission_date", "投运日期", "DATE", sort_order=61),
        _f("purchase_date", "采购日期", "DATE", sort_order=62),
        _f("warranty_expiry", "质保到期日", "DATE", sort_order=63),
        _f("expected_service_life", "设计使用年限", "NUMBER", sort_order=64),
        _f("maintenance_cycle", "维保周期", "TEXT", sort_order=65,
           description="策略配置（如 90天），非实时下次维保时间"),
        # 安全（设备固有属性）
        _f("safety_level", "安全等级", "ENUM", sort_order=80),
        _f("explosion_proof_grade", "防爆等级", "ENUM", sort_order=81),
        _f("waterproof_grade", "防水等级", "ENUM", sort_order=82),
        # 其他
        _f("cost_center", "成本中心", "TEXT", sort_order=90),
        _f("remark", "备注", "TEXT", sort_order=91),
        # 共有 REF 槽位（值来自关联/外部服务，不在 ent_equipment 冗余列）
        _f("REF_OPERATION_STATUS", "运行状态", "REF", sort_order=901,
           description="运行态/SCADA；全设备列表展示"),
        _f("REF_HEALTH_SCORE", "健康度", "REF", sort_order=902,
           description="健康评估服务"),
        _f("REF_LAST_MAINTENANCE", "上次维保时间", "REF", sort_order=903,
           description="事实来源 maintenance 模块"),
        _f("REF_NEXT_MAINTENANCE", "下次维保时间", "REF", sort_order=904,
           description="事实来源 maintenance 模块；按此筛选由 maintenance API 驱动"),
        _f("REF_LAST_INSPECTION", "上次检验时间", "REF", sort_order=905),
        _f("REF_NEXT_INSPECTION", "下次检验时间", "REF", sort_order=906),
    ],
    # region — 定稿（2026-07-07）：行政区划
    "region": [
        _f("region_level", "区划级别", "ENUM", required=True, sort_order=15,
           description="country/province/city/district；与 model 对齐"),
        _f("admin_code", "区划代码", "TEXT", sort_order=16,
           description="国标行政区划码"),
        _f("region_type", "区域类型", "NUMBER", required=False, sort_order=20,
           description="历史字段；迁移后由 region_level 取代"),
        _f("description", "区域说明", "TEXT", sort_order=21),
        _f("boundary_geojson", "边界几何", "JSON", sort_order=40,
           description="地图多边形顶点集合（GeoJSON）"),
        _f("boundary_crs", "坐标系", "TEXT", sort_order=41),
        _f("boundary_status", "边界状态", "ENUM", sort_order=42),
        _f("centroid_lng", "质心经度", "NUMBER", sort_order=45,
           description="区域中心点，与边界一并保存"),
        _f("centroid_lat", "质心纬度", "NUMBER", sort_order=46),
        _f("remark", "备注", "TEXT", sort_order=91),
    ],
    # facility — 定稿（2026-07-07）：站场/厂区等设施点
    "facility": [
        _f("REF_REGION", "所属区划", "REF", required=True, sort_order=10,
           type_config=REL_FACILITY_REGION,
           description="设施挂在哪一级行政区划下"),
        _f("address", "地址", "TEXT", sort_order=20),
        _f("longitude", "经度", "NUMBER", sort_order=21),
        _f("latitude", "纬度", "NUMBER", sort_order=22),
        _f("facility_type", "设施类型", "ENUM", required=True, sort_order=25,
           description="与 model 或分类对齐"),
        _f("remark", "备注", "TEXT", sort_order=91),
    ],
    # zone — 定稿（2026-07-07）：站内空间分区
    "zone": [
        _f("REF_FACILITY", "所属设施", "REF", required=True, sort_order=10,
           type_config=REL_ZONE_FACILITY,
           description="分区归属设施点"),
        _f("zone_type", "分区类型", "ENUM", required=True, sort_order=20,
           description="与 model 对齐"),
        _f("description", "分区说明", "TEXT", sort_order=21),
        _f("boundary_geojson", "边界几何", "JSON", sort_order=40),
        _f("boundary_crs", "坐标系", "TEXT", sort_order=41),
        _f("boundary_status", "边界状态", "ENUM", sort_order=42),
        _f("min_height_m", "最小高度(m)", "NUMBER", sort_order=43),
        _f("max_height_m", "最大高度(m)", "NUMBER", sort_order=44),
        _f("centroid_lng", "质心经度", "NUMBER", sort_order=45),
        _f("centroid_lat", "质心纬度", "NUMBER", sort_order=46),
        _f("remark", "备注", "TEXT", sort_order=91),
    ],
    # ent_pipeline — 无 status 物理列（status 为实体通用字段）
    "pipeline": [
        _f("pipeline_code", "管线编号", "TEXT", required=True, sort_order=1),
        _f("pipeline_name", "管线名称", "TEXT", required=True, sort_order=2),
        _f("install_date", "安装日期", "DATE", sort_order=3),
        _f("manufacturer", "生产厂家", "TEXT", sort_order=4),
        _f("pipeline_model", "管线型号", "TEXT", sort_order=5),
    ],
    # ent_fault — 分析稿；fault_no 改用实体 code
    "fault": [
        _f("fault_type", "故障类型", "ENUM", required=True, sort_order=2),
        _f("fault_level", "故障等级", "ENUM", required=True, sort_order=3),
        _f("phenomenon", "故障现象", "TEXT", required=True, sort_order=4),
        _f("occur_time", "发生时间", "DATETIME", required=True, sort_order=5),
        _f("resolved", "是否已解决", "BOOLEAN", required=True, sort_order=6),
    ],
    # repair_request — 分析稿（2026-07-05）；独立报修业务
    "repair_request": [
        _f("target_type", "报修对象类型", "ENUM", required=True, sort_order=10,
           description="设备/区域/设施等；与 REF_TARGET 配合"),
        _f("report_status", "报修状态", "ENUM", required=True, sort_order=11),
        _f("report_time", "报修时间", "DATETIME", required=True, sort_order=12),
        _f("reporter", "报修人", "TEXT", required=True, sort_order=13),
        _f("phenomenon", "现象描述", "TEXT", required=True, sort_order=14),
        _f("priority", "优先级", "ENUM", sort_order=15),
        _f("location_description", "位置描述", "TEXT", sort_order=16,
           description="补充对象定位；各报修 model 共有"),
        _f("attachments", "附件", "FILE", sort_order=17,
           description="现场照片、凭证等；各报修 model 共有"),
        _f("case_id", "案件号", "TEXT", sort_order=18,
           description="与工单、时间线对齐"),
        _f("REF_EQUIPMENT", "报修对象", "REF_Multi", sort_order=34,
           type_config=REL_EQUIPMENT,
           description="按 target_type 解析；设备类报修必填"),
        _f("remark", "备注", "TEXT", sort_order=91),
    ],
    # maintenance — 分析稿（2026-07-05）；维修业务（dynamic 业务，与报修并列）
    # 作业执行字段不在此；维修的作业经工单系统（work_order）实现
    "maintenance": [
        _f("case_id", "案件号", "TEXT", sort_order=18,
           description="与报修、工单对齐；挂载点待确认"),
        _f("REF_REPAIR_REQUEST", "关联报修", "REF_Multi", sort_order=36,
           description="有报修环节时关联 repair_request"),
    ],
    # work_order — 分析稿（2026-07-05）；工单系统（能力层）· 工单共性基础字段
    # 维修业务的派工/执行/验收经此实现；不是与报修并列的 dynamic 业务
    "work_order": [
        _f("maintenance_type", "工单类型", "ENUM", required=True, sort_order=20,
           description="计划保养/故障消缺等；字段名待与 work_order_type 对齐"),
        _f("order_status", "工单状态", "ENUM", required=True, sort_order=22,
           description="工单流转态，非实体 status"),
        _f("source_type", "工单来源", "ENUM", sort_order=23),
        _f("plan_time", "计划时间", "DATETIME", required=True, sort_order=30),
        _f("priority", "优先级", "ENUM", sort_order=21),
        _f("case_id", "案件号", "TEXT", sort_order=19),
        _f("deadline", "要求完成时间", "DATETIME", sort_order=31),
        _f("sla_deadline", "SLA截止时间", "DATETIME", sort_order=32),
        _f("estimated_duration", "计划工时(h)", "NUMBER", sort_order=33),
        _f("maintenance_content", "作业内容", "TEXT", sort_order=34),
        _f("REF_EQUIPMENT", "维护对象", "REF_Multi", required=True, sort_order=35,
           type_config=REL_EQUIPMENT),
        _f("REF_REPAIR_REQUEST", "关联报修", "REF_Multi", sort_order=36,
           description="source_type=报修时；倒查报修人/时间/现象"),
        _f("REF_MAINTENANCE", "关联维修", "REF_Multi", sort_order=37,
           description="关联 maintenance 维修业务记录（若有独立实体）"),
        _f("dispatcher", "派单人", "TEXT", sort_order=40),
        _f("executor", "执行人", "TEXT", sort_order=41),
        _f("maintainer_team", "执行班组", "TEXT", sort_order=42),
        _f("supervisor", "监督人", "TEXT", sort_order=43),
        _f("actual_start_time", "实际开始时间", "DATETIME", sort_order=50),
        _f("actual_end_time", "实际完成时间", "DATETIME", sort_order=51),
        _f("acceptor", "验收人", "TEXT", sort_order=52),
        _f("acceptance_result", "验收结果", "ENUM", sort_order=53),
        _f("acceptance_time", "验收时间", "DATETIME", sort_order=54),
        _f("satisfaction", "满意度", "ENUM", sort_order=55),
        _f("estimated_cost", "预估费用", "NUMBER", sort_order=60),
        _f("remark", "备注", "TEXT", sort_order=91),
    ],
    # ent_spare_part
    "spare_parts": [
        _f("spare_part_code", "备件编号", "TEXT", required=True, sort_order=1),
        _f("spare_part_name", "备件名称", "TEXT", required=True, sort_order=2),
        _f("stock_quantity", "库存数量", "NUMBER", required=True, sort_order=3),
        _f("unit", "单位", "TEXT", required=True, sort_order=4),
        _f("min_stock", "最低库存", "NUMBER", sort_order=5),
        _f("REL_EQUIPMENT", "关联设备", "REF_Multi", sort_order=900, type_config=REL_EQUIPMENT),
    ],
    # ent_customer — 业务字段在 attrs
    "customer": [
        _f("lian_xi_dian_hua", "联系电话", "TEXT", required=True, sort_order=1),
        _f("REL_EQUIPMENT", "关联设备", "REF_Multi", sort_order=900, type_config=REL_EQUIPMENT),
    ],
    # ent_billing — 业务字段在 attrs
    "billing": [
        _f("shi_fou_han_shui", "是否含税", "ENUM", required=True, sort_order=1),
        _f("shou_fei_zhuang_tai", "收费状态", "ENUM", required=True, sort_order=2),
        _f("shou_fei_shi_jian", "收费时间", "DATETIME", sort_order=3),
    ],
    # ent_emergency_resource
    "emergency_resource": [
        _f("zong_shu_liang", "总数量", "NUMBER", required=True, sort_order=1),
        _f("ke_yong_shu_liang", "可用数量", "NUMBER", required=True, sort_order=2),
    ],
    # ent_inspection_point — rel_region 物理列
    "inspection_point": [
        _f("REL_REGION", "所属区域", "REF_Multi", sort_order=900, type_config=REL_REGION),
    ],
    # 以下业务表仅有通用列 + attrs，暂无专用物理列；基础字段留空，由模型自定义字段承载
    "emergency": [],
    "emergency_team": [],
    "patrol": [],
    "inspection_item": [],
    "route": [],
}
# fmt: on

# 设备管理：明确不纳入基础字段的可选 REF（REF_REGION 等共有项见 CANONICAL_BASE_FIELDS["equipment"]）
EQUIPMENT_REF_RELATIONS: list[dict[str, str]] = [
    {"field_code": "REF_RUNNING_HOURS", "field_name": "累计运行时长", "source": "IoT/运行统计（暂缓纳入基础字段）"},
    {"field_code": "REF_KE_HU", "field_name": "关联客户", "source": "管廊入廊等；不纳入基础字段，模型/视图按需"},
    {"field_code": "REF_SPARE_PART", "field_name": "关联备件", "source": "可选关联；不纳入基础字段，模型/视图按需"},
]

REGION_REF_RELATIONS: list[dict[str, str]] = [
    {"field_code": "REF_EQUIPMENT", "field_name": "关联设备", "source": "设备关联表"},
    {"field_code": "REF_INSPECTION_POINT", "field_name": "关联巡检点位", "source": "巡检点位关联表"},
    {"field_code": "REF_EQUIPMENT_COUNT", "field_name": "设备数量", "source": "统计/聚合服务"},
    {"field_code": "REF_ALARM_COUNT", "field_name": "当前告警数", "source": "告警服务"},
]

# 工单系统：模型扩展（共性字段见 CANONICAL_BASE_FIELDS["work_order"]）
WORK_ORDER_MODEL_FIELDS: list[dict[str, str]] = [
    {"field_code": "_example_checklist", "field_name": "（示例）某类工单专有检查清单"},
]

# 报修：模型扩展字段（仅 model 差异；共性字段见 CANONICAL_BASE_FIELDS["repair_request"]）
REPAIR_REQUEST_MODEL_FIELDS: list[dict[str, str]] = [
    {"field_code": "_example_inspection_item", "field_name": "（示例）某类报修专有检查项"},
]

# 报修：跨业务 REF 约定（非 repair_request 基础字段表内项）
REPAIR_REQUEST_REF_RELATIONS: list[dict[str, str]] = [
    {"field_code": "REF_WORK_ORDER", "field_name": "关联工单", "source": "派单后关联工单系统；与工单侧 REF_REPAIR_REQUEST 二选一或双向"},
    {"field_code": "REF_FAULT", "field_name": "关联故障", "source": "确认后关联 fault 业务"},
]

# 工单系统：跨业务 REF（REF_REPAIR_REQUEST 等见 CANONICAL_BASE_FIELDS["work_order"]）
WORK_ORDER_REF_RELATIONS: list[dict[str, str]] = [
    {"field_code": "REF_FAULT", "field_name": "关联故障", "source": "故障事实记录；不纳入基础字段"},
    {"field_code": "REF_REGION", "field_name": "关联区域", "source": "区域范围补充；不纳入基础字段"},
    {"field_code": "REF_SPARE_PART", "field_name": "使用备件", "source": "备件领用/关联表；不纳入基础字段"},
    {"field_code": "REF_MAINTENANCE_TOOL", "field_name": "使用工器具", "source": "工器具关联表；主数据在 equipment；不纳入基础字段"},
    {"field_code": "REF_ACTUAL_COST", "field_name": "实际费用", "source": "关单后结算汇总；不纳入基础字段"},
]

ALL_MANAGED_BUSINESS = tuple(CANONICAL_BASE_FIELDS.keys())
