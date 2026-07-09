"""按业务域组织的字段库定稿。

分组与平台业务场景对齐：用户配模型时按「设备管理」「告警管理」等找字段，
而不是按字段类型（日期、数值）找。

供 export-platform-import.py / generate-smart-station-import.py 等生成 seed 时引用。
"""
from __future__ import annotations

from dataclasses import dataclass


@dataclass(frozen=True)
class FieldDef:
    code: str
    name: str
    type: str
    unit: str | None = None
    description: str | None = None
    options: str | None = None


@dataclass(frozen=True)
class FieldGroupCatalog:
    code: str
    name: str
    sort: int
    description: str
    fields: tuple[FieldDef, ...]


def _enum(*labels: str) -> str:
    import json

    return json.dumps([{"label": x, "value": x} for x in labels], ensure_ascii=False)


# fmt: off
CATALOG: tuple[FieldGroupCatalog, ...] = (
    FieldGroupCatalog(
        "FG-BIZ-COMMON", "公共通用", 10, "各业务模型均可能用到的基础属性",
        (
            FieldDef("FLD-COM-001", "业务编码", "STRING", description="业务侧编码，区别于实体主键"),
            FieldDef("FLD-COM-002", "简称", "STRING"),
            FieldDef("FLD-COM-003", "描述", "TEXT"),
            FieldDef("FLD-COM-004", "备注", "LONG_TEXT"),
            FieldDef("FLD-COM-005", "状态", "ENUM", options=_enum("启用", "停用", "草稿", "归档")),
            FieldDef("FLD-COM-006", "类型", "ENUM", options=_enum("常规", "重点", "临时", "其他")),
            FieldDef("FLD-COM-007", "排序号", "INTEGER"),
            FieldDef("FLD-COM-008", "标签", "STRING", description="检索标签，逗号分隔"),
            FieldDef("FLD-COM-009", "重要等级", "ENUM", options=_enum("一般", "重要", "关键")),
            FieldDef("FLD-COM-010", "审批状态", "ENUM", options=_enum("待提交", "审批中", "已通过", "已驳回")),
            FieldDef("FLD-COM-011", "附件", "UPLOAD"),
            FieldDef("FLD-COM-012", "图片", "IMAGE"),
        ),
    ),
    FieldGroupCatalog(
        "FG-BIZ-EQUIPMENT", "设备管理", 20, "设备台账、资产与维保常用字段",
        (
            FieldDef("FLD-EQP-001", "资产编号", "STRING"),
            FieldDef("FLD-EQP-002", "设备编号", "STRING"),
            FieldDef("FLD-EQP-003", "出厂序列号", "STRING"),
            FieldDef("FLD-EQP-004", "规格型号", "STRING"),
            FieldDef("FLD-EQP-005", "生产厂家", "STRING", description="设备制造厂家"),
            FieldDef("FLD-EQP-006", "生产厂商", "STRING", description="同生产厂家，兼容不同叫法"),
            FieldDef("FLD-EQP-007", "品牌", "STRING"),
            FieldDef("FLD-EQP-008", "生产日期", "DATE"),
            FieldDef("FLD-EQP-009", "投运日期", "DATE", description="正式投入运行日期"),
            FieldDef("FLD-EQP-010", "安装日期", "DATE"),
            FieldDef("FLD-EQP-011", "购买日期", "DATE"),
            FieldDef("FLD-EQP-012", "维保到期时间", "DATE", description="质保或维保服务截止日"),
            FieldDef("FLD-EQP-013", "上次维护日期", "DATE"),
            FieldDef("FLD-EQP-014", "下次维护日期", "DATE"),
            FieldDef("FLD-EQP-015", "维护周期", "INTEGER", unit="天"),
            FieldDef("FLD-EQP-016", "维护人", "STRING", description="负责维护的人员"),
            FieldDef("FLD-EQP-017", "设备负责人", "STRING"),
            FieldDef("FLD-EQP-018", "设备类别", "ENUM", options=_enum("动设备", "静设备", "电气", "仪表", "安防", "通信", "其他")),
            FieldDef("FLD-EQP-019", "运行状态", "ENUM", options=_enum("运行", "备用", "检修", "停用", "报废")),
            FieldDef("FLD-EQP-020", "关键程度", "ENUM", options=_enum("一般", "重要", "关键")),
            FieldDef("FLD-EQP-021", "设计寿命", "INTEGER", unit="年"),
            FieldDef("FLD-EQP-022", "额定功率", "DECIMAL", unit="kW"),
            FieldDef("FLD-EQP-023", "额定电压", "DECIMAL", unit="V"),
            FieldDef("FLD-EQP-024", "额定电流", "DECIMAL", unit="A"),
            FieldDef("FLD-EQP-025", "供电方式", "ENUM", options=_enum("市电", "UPS", "直流", "其他")),
            FieldDef("FLD-EQP-026", "安装方式", "ENUM", options=_enum("壁挂", "落地", "嵌入式", "架空", "其他")),
            FieldDef("FLD-EQP-027", "安装位置说明", "TEXT"),
            FieldDef("FLD-EQP-028", "维护内容", "LONG_TEXT"),
            FieldDef("FLD-EQP-029", "故障现象", "TEXT"),
            FieldDef("FLD-EQP-030", "处理结果", "TEXT"),
            FieldDef("FLD-EQP-031", "停机时长", "DECIMAL", unit="小时"),
            FieldDef("FLD-EQP-032", "备件清单", "LONG_TEXT"),
            FieldDef("FLD-EQP-033", "使用备件", "LONG_TEXT"),
            FieldDef("FLD-EQP-034", "设备照片", "IMAGE"),
            FieldDef("FLD-EQP-035", "关联备件", "ENTITY_REF", description="关联备件库存记录"),
            FieldDef("FLD-EQP-036", "关联上级设备", "ENTITY_REF"),
        ),
    ),
    FieldGroupCatalog(
        "FG-BIZ-ALARM", "告警管理", 30, "告警事件、处置与气体监测",
        (
            FieldDef("FLD-ALM-001", "告警编号", "STRING"),
            FieldDef("FLD-ALM-002", "告警级别", "ENUM", options=_enum("提示", "一般", "重要", "紧急")),
            FieldDef("FLD-ALM-003", "告警类型", "ENUM", options=_enum("设备", "环境", "工艺", "消防", "安防", "其他")),
            FieldDef("FLD-ALM-004", "告警时间", "DATETIME"),
            FieldDef("FLD-ALM-005", "告警内容", "TEXT"),
            FieldDef("FLD-ALM-006", "处理状态", "ENUM", options=_enum("未处理", "处理中", "已处理", "已忽略")),
            FieldDef("FLD-ALM-007", "是否报警", "BOOLEAN"),
            FieldDef("FLD-ALM-008", "严重等级", "ENUM", options=_enum("轻微", "一般", "严重", "特别重大")),
            FieldDef("FLD-ALM-009", "安全等级", "ENUM", options=_enum("一级", "二级", "三级", "四级")),
            FieldDef("FLD-ALM-010", "气体类型", "ENUM", options=_enum("可燃气体", "有毒气体", "氧气", "二氧化碳", "其他")),
            FieldDef("FLD-ALM-011", "浓度阈值", "DECIMAL", unit="ppm"),
            FieldDef("FLD-ALM-012", "当前浓度", "DECIMAL", unit="ppm"),
            FieldDef("FLD-ALM-013", "二氧化碳浓度", "FLOAT", unit="ppm"),
            FieldDef("FLD-ALM-014", "处理人", "STRING"),
            FieldDef("FLD-ALM-015", "处理时间", "DATETIME"),
            FieldDef("FLD-ALM-016", "处理说明", "TEXT"),
            FieldDef("FLD-ALM-017", "应急预案", "STRING"),
            FieldDef("FLD-ALM-018", "关联设备", "ENTITY_REF"),
            FieldDef("FLD-ALM-019", "关联任务", "ENTITY_REF"),
        ),
    ),
    FieldGroupCatalog(
        "FG-BIZ-INSPECTION", "巡检管理", 40, "巡检计划、点检项与结果",
        (
            FieldDef("FLD-INS-001", "巡检项名称", "STRING"),
            FieldDef("FLD-INS-002", "检查标准", "TEXT"),
            FieldDef("FLD-INS-003", "检查方法", "ENUM", options=_enum("目视", "听音", "测温", "测振", "取样", "其他")),
            FieldDef("FLD-INS-004", "检查频次", "ENUM", options=_enum("每日", "每周", "每月", "每季", "每年", "不定期")),
            FieldDef("FLD-INS-005", "巡检结果", "ENUM", options=_enum("正常", "异常", "待确认", "不适用")),
            FieldDef("FLD-INS-006", "是否异常", "BOOLEAN"),
            FieldDef("FLD-INS-007", "异常描述", "TEXT"),
            FieldDef("FLD-INS-008", "检查项完成率", "INTEGER", unit="%"),
            FieldDef("FLD-INS-009", "检测范围", "NUMBER", unit="ppm"),
            FieldDef("FLD-INS-010", "质量检查结果", "LONG_TEXT"),
            FieldDef("FLD-INS-011", "巡检路线", "STRING"),
            FieldDef("FLD-INS-012", "点检类型", "ENUM", options=_enum("日常点检", "专业点检", "精密点检")),
            FieldDef("FLD-INS-013", "巡检员", "STRING"),
            FieldDef("FLD-INS-014", "复核人", "STRING"),
            FieldDef("FLD-INS-015", "巡检时间", "DATETIME"),
            FieldDef("FLD-INS-016", "现场照片", "IMAGE"),
            FieldDef("FLD-INS-017", "关联设备", "ENTITY_REF"),
        ),
    ),
    FieldGroupCatalog(
        "FG-BIZ-TASK", "任务管理", 50, "工单、任务派工与验收",
        (
            FieldDef("FLD-TSK-001", "任务编号", "STRING"),
            FieldDef("FLD-TSK-002", "任务名称", "STRING"),
            FieldDef("FLD-TSK-003", "任务类型", "ENUM", options=_enum("维修", "保养", "巡检", "安装", "改造", "其他")),
            FieldDef("FLD-TSK-004", "任务状态", "ENUM", options=_enum("待派工", "进行中", "待验收", "已完成", "已关闭", "已取消")),
            FieldDef("FLD-TSK-005", "优先级", "ENUM", options=_enum("低", "中", "高", "紧急")),
            FieldDef("FLD-TSK-006", "任务进度", "INTEGER", unit="%"),
            FieldDef("FLD-TSK-007", "计划开始时间", "DATETIME"),
            FieldDef("FLD-TSK-008", "计划结束时间", "DATETIME"),
            FieldDef("FLD-TSK-009", "实际开始时间", "DATETIME"),
            FieldDef("FLD-TSK-010", "实际结束时间", "DATETIME"),
            FieldDef("FLD-TSK-011", "执行人", "STRING"),
            FieldDef("FLD-TSK-012", "验收人", "STRING"),
            FieldDef("FLD-TSK-013", "验收结果", "ENUM", options=_enum("合格", "不合格", "待复验")),
            FieldDef("FLD-TSK-014", "工单来源", "ENUM", options=_enum("计划生成", "告警转单", "手工创建", "接口推送")),
            FieldDef("FLD-TSK-015", "关闭原因", "TEXT"),
            FieldDef("FLD-TSK-016", "关联设备", "ENTITY_REF"),
            FieldDef("FLD-TSK-017", "关联告警", "ENTITY_REF"),
        ),
    ),
    FieldGroupCatalog(
        "FG-BIZ-LOCATION", "位置空间", 60, "地理区域、设施、分区与定位",
        (
            FieldDef("FLD-LOC-001", "所属区域", "STRING"),
            FieldDef("FLD-LOC-002", "所属设施", "STRING"),
            FieldDef("FLD-LOC-003", "所属分区", "STRING"),
            FieldDef("FLD-LOC-004", "详细地址", "TEXT"),
            FieldDef("FLD-LOC-005", "中心桩号", "STRING"),
            FieldDef("FLD-LOC-006", "起点桩号", "STRING"),
            FieldDef("FLD-LOC-007", "终点桩号", "STRING"),
            FieldDef("FLD-LOC-008", "经度", "DECIMAL"),
            FieldDef("FLD-LOC-009", "纬度", "DECIMAL"),
            FieldDef("FLD-LOC-010", "海拔", "DECIMAL", unit="米"),
            FieldDef("FLD-LOC-011", "楼层", "STRING"),
            FieldDef("FLD-LOC-012", "房间号", "STRING"),
            FieldDef("FLD-LOC-013", "防火分区", "STRING"),
            FieldDef("FLD-LOC-014", "坐标", "COORDINATE"),
            FieldDef("FLD-LOC-015", "关联上级区域", "ENTITY_REF"),
            FieldDef("FLD-LOC-016", "关联所属设施", "ENTITY_REF"),
        ),
    ),
    FieldGroupCatalog(
        "FG-BIZ-GALLERY", "管廊管理", 70, "综合管廊与入廊管线",
        (
            FieldDef("FLD-GAL-001", "管廊名称", "STRING"),
            FieldDef("FLD-GAL-002", "舱室类型", "ENUM", options=_enum("燃气舱", "电力舱", "综合舱", "其他")),
            FieldDef("FLD-GAL-003", "入廊管线类型", "TEXT"),
            FieldDef("FLD-GAL-004", "管线规格", "STRING"),
            FieldDef("FLD-GAL-005", "敷设方式", "ENUM", options=_enum("架空", "直埋", "管沟", "管廊", "其他")),
            FieldDef("FLD-GAL-006", "共设长度", "DECIMAL", unit="米"),
            FieldDef("FLD-GAL-007", "设计压力", "DECIMAL", unit="MPa"),
            FieldDef("FLD-GAL-008", "设计温度", "DECIMAL", unit="℃"),
            FieldDef("FLD-GAL-009", "介质类型", "STRING"),
            FieldDef("FLD-GAL-010", "管线材质", "STRING"),
            FieldDef("FLD-GAL-011", "保温方式", "STRING"),
            FieldDef("FLD-GAL-012", "防火区编码", "STRING"),
            FieldDef("FLD-GAL-013", "建成日期", "DATE"),
        ),
    ),
    FieldGroupCatalog(
        "FG-BIZ-PERSONNEL", "人员组织", 80, "人员、部门与联系方式",
        (
            FieldDef("FLD-PER-001", "联系人", "STRING"),
            FieldDef("FLD-PER-002", "主要联系人", "STRING"),
            FieldDef("FLD-PER-003", "联系电话", "STRING"),
            FieldDef("FLD-PER-004", "联系邮箱", "STRING"),
            FieldDef("FLD-PER-005", "所属部门", "STRING"),
            FieldDef("FLD-PER-006", "岗位", "STRING"),
            FieldDef("FLD-PER-007", "编制人数", "INTEGER", unit="人"),
            FieldDef("FLD-PER-008", "值班人员", "STRING"),
            FieldDef("FLD-PER-009", "操作员", "STRING"),
            FieldDef("FLD-PER-010", "关联人员", "ENTITY_REF"),
        ),
    ),
    FieldGroupCatalog(
        "FG-BIZ-CUSTOMER", "客户管理", 90, "客户档案与信用",
        (
            FieldDef("FLD-CUS-001", "客户名称", "STRING"),
            FieldDef("FLD-CUS-002", "客户编码", "STRING"),
            FieldDef("FLD-CUS-003", "企业地址", "TEXT"),
            FieldDef("FLD-CUS-004", "信用评级", "ENUM", options=_enum("A", "B", "C", "D")),
            FieldDef("FLD-CUS-005", "主要联系人", "STRING"),
            FieldDef("FLD-CUS-006", "联系电话", "STRING"),
            FieldDef("FLD-CUS-007", "联系邮箱", "STRING"),
            FieldDef("FLD-CUS-008", "关联客户", "ENTITY_REF"),
        ),
    ),
    FieldGroupCatalog(
        "FG-BIZ-CONTRACT", "合同管理", 100, "合同签订与履约",
        (
            FieldDef("FLD-CON-001", "合同编号", "STRING"),
            FieldDef("FLD-CON-002", "合同名称", "STRING"),
            FieldDef("FLD-CON-003", "甲方", "STRING"),
            FieldDef("FLD-CON-004", "乙方", "STRING"),
            FieldDef("FLD-CON-005", "合同金额", "DECIMAL", unit="元"),
            FieldDef("FLD-CON-006", "签订日期", "DATE"),
            FieldDef("FLD-CON-007", "生效日期", "DATE"),
            FieldDef("FLD-CON-008", "失效日期", "DATE"),
            FieldDef("FLD-CON-009", "付款条件", "LONG_TEXT"),
            FieldDef("FLD-CON-010", "关联合同", "ENTITY_REF"),
            FieldDef("FLD-CON-011", "关联客户", "ENTITY_REF"),
        ),
    ),
    FieldGroupCatalog(
        "FG-BIZ-BILLING", "收费管理", 110, "收费、账期与票据",
        (
            FieldDef("FLD-BIL-001", "收费时间", "DATETIME"),
            FieldDef("FLD-BIL-002", "收费状态", "ENUM", options=_enum("未收", "部分收", "已收", "作废")),
            FieldDef("FLD-BIL-003", "是否含税", "ENUM", options=_enum("是", "否")),
            FieldDef("FLD-BIL-004", "购买金额", "DECIMAL", unit="元"),
            FieldDef("FLD-BIL-005", "账期", "INTEGER", unit="天"),
            FieldDef("FLD-BIL-006", "关联客户", "ENTITY_REF"),
        ),
    ),
    FieldGroupCatalog(
        "FG-BIZ-SPARE", "备件管理", 120, "备件库存与领用",
        (
            FieldDef("FLD-SPR-001", "备件编号", "STRING"),
            FieldDef("FLD-SPR-002", "备件名称", "STRING"),
            FieldDef("FLD-SPR-003", "规格型号", "STRING"),
            FieldDef("FLD-SPR-004", "库存数量", "INTEGER", unit="件"),
            FieldDef("FLD-SPR-005", "安全库存", "INTEGER", unit="件"),
            FieldDef("FLD-SPR-006", "存放位置", "STRING"),
            FieldDef("FLD-SPR-007", "供应商", "STRING"),
            FieldDef("FLD-SPR-008", "关联备件", "ENTITY_REF"),
        ),
    ),
    FieldGroupCatalog(
        "FG-BIZ-MONITOR", "运行监测", 130, "实时监测类数值（温压流等）",
        (
            FieldDef("FLD-MON-001", "温度", "DECIMAL", unit="℃"),
            FieldDef("FLD-MON-002", "压力", "DECIMAL", unit="MPa"),
            FieldDef("FLD-MON-003", "流量", "DECIMAL", unit="m³/h"),
            FieldDef("FLD-MON-004", "湿度", "DECIMAL", unit="%"),
            FieldDef("FLD-MON-005", "浓度", "DECIMAL", unit="ppm"),
            FieldDef("FLD-MON-006", "液位", "DECIMAL", unit="米"),
            FieldDef("FLD-MON-007", "转速", "INTEGER", unit="rpm"),
            FieldDef("FLD-MON-008", "功率", "DECIMAL", unit="kW"),
            FieldDef("FLD-MON-009", "电压", "DECIMAL", unit="V"),
            FieldDef("FLD-MON-010", "电流", "DECIMAL", unit="A"),
            FieldDef("FLD-MON-011", "噪声", "DECIMAL", unit="dB"),
            FieldDef("FLD-MON-012", "记录时间", "DATETIME", description="采集时间戳"),
        ),
    ),
    FieldGroupCatalog(
        "FG-BIZ-INSTRUMENT", "仪表管理", 140, "工业仪表台账、位号与校准",
        (
            FieldDef("FLD-INS-T-001", "仪表位号", "STRING", description="DCS/PLC 位号或测点编号"),
            FieldDef("FLD-INS-T-002", "仪表名称", "STRING"),
            FieldDef("FLD-INS-T-003", "仪表类型", "ENUM", options=_enum("压力表", "温度计", "流量计", "液位计", "分析仪", "变送器", "其他")),
            FieldDef("FLD-INS-T-004", "测量介质", "STRING"),
            FieldDef("FLD-INS-T-005", "量程下限", "DECIMAL"),
            FieldDef("FLD-INS-T-006", "量程上限", "DECIMAL"),
            FieldDef("FLD-INS-T-007", "精度等级", "STRING", description="如 0.5 级、1.0 级"),
            FieldDef("FLD-INS-T-008", "输出信号", "ENUM", options=_enum("4-20mA", "0-10V", "RS485", "HART", "开关量", "其他")),
            FieldDef("FLD-INS-T-009", "安装位置", "STRING"),
            FieldDef("FLD-INS-T-010", "投运日期", "DATE"),
            FieldDef("FLD-INS-T-011", "上次校准日期", "DATE"),
            FieldDef("FLD-INS-T-012", "下次校准日期", "DATE"),
            FieldDef("FLD-INS-T-013", "校准周期", "INTEGER", unit="月"),
            FieldDef("FLD-INS-T-014", "校准单位", "STRING"),
            FieldDef("FLD-INS-T-015", "仪表状态", "ENUM", options=_enum("正常", "欠准", "停用", "待检", "报废")),
            FieldDef("FLD-INS-T-016", "生产厂家", "STRING"),
            FieldDef("FLD-INS-T-017", "规格型号", "STRING"),
            FieldDef("FLD-INS-T-018", "关联设备", "ENTITY_REF"),
        ),
    ),
    FieldGroupCatalog(
        "FG-BIZ-FIRE", "消防管理", 150, "消防设施、分区与演练",
        (
            FieldDef("FLD-FIR-001", "消防设施编号", "STRING"),
            FieldDef("FLD-FIR-002", "设施类型", "ENUM", options=_enum("消火栓", "灭火器", "喷淋", "烟感", "温感", "防火门", "排烟", "其他")),
            FieldDef("FLD-FIR-003", "防火分区", "STRING"),
            FieldDef("FLD-FIR-004", "防火区编码", "STRING"),
            FieldDef("FLD-FIR-005", "安装位置", "STRING"),
            FieldDef("FLD-FIR-006", "投运日期", "DATE"),
            FieldDef("FLD-FIR-007", "有效期至", "DATE", description="灭火器换药、设施检定截止"),
            FieldDef("FLD-FIR-008", "检查周期", "INTEGER", unit="天"),
            FieldDef("FLD-FIR-009", "上次检查日期", "DATE"),
            FieldDef("FLD-FIR-010", "下次检查日期", "DATE"),
            FieldDef("FLD-FIR-011", "检查人", "STRING"),
            FieldDef("FLD-FIR-012", "检查结果", "ENUM", options=_enum("合格", "不合格", "待整改")),
            FieldDef("FLD-FIR-013", "消防等级", "ENUM", options=_enum("一级", "二级", "三级")),
            FieldDef("FLD-FIR-014", "应急预案", "STRING"),
            FieldDef("FLD-FIR-015", "演练日期", "DATE"),
            FieldDef("FLD-FIR-016", "演练记录", "LONG_TEXT"),
            FieldDef("FLD-FIR-017", "是否完好", "BOOLEAN"),
            FieldDef("FLD-FIR-018", "关联设备", "ENTITY_REF"),
        ),
    ),
    FieldGroupCatalog(
        "FG-BIZ-ENERGY", "能耗管理", 160, "水电气热等能耗计量与分析",
        (
            FieldDef("FLD-ENG-001", "能耗类型", "ENUM", options=_enum("电", "水", "天然气", "蒸汽", "压缩空气", "其他")),
            FieldDef("FLD-ENG-002", "计量点名称", "STRING"),
            FieldDef("FLD-ENG-003", "计量点编号", "STRING"),
            FieldDef("FLD-ENG-004", "表计编号", "STRING"),
            FieldDef("FLD-ENG-005", "倍率", "DECIMAL", description="电表倍率等"),
            FieldDef("FLD-ENG-006", "起始读数", "DECIMAL"),
            FieldDef("FLD-ENG-007", "终止读数", "DECIMAL"),
            FieldDef("FLD-ENG-008", "本期用量", "DECIMAL"),
            FieldDef("FLD-ENG-009", "用量单位", "ENUM", options=_enum("kWh", "m³", "t", "GJ", "其他")),
            FieldDef("FLD-ENG-010", "统计周期", "ENUM", options=_enum("日", "周", "月", "年")),
            FieldDef("FLD-ENG-011", "统计开始时间", "DATETIME"),
            FieldDef("FLD-ENG-012", "统计结束时间", "DATETIME"),
            FieldDef("FLD-ENG-013", "折标煤系数", "DECIMAL"),
            FieldDef("FLD-ENG-014", "折标煤量", "DECIMAL", unit="tce"),
            FieldDef("FLD-ENG-015", "碳排放量", "DECIMAL", unit="tCO₂"),
            FieldDef("FLD-ENG-016", "费用金额", "DECIMAL", unit="元"),
            FieldDef("FLD-ENG-017", "同比变化率", "DECIMAL", unit="%"),
            FieldDef("FLD-ENG-018", "关联设施", "ENTITY_REF"),
        ),
    ),
    FieldGroupCatalog(
        "FG-BIZ-FAULT", "故障管理", 170, "故障记录、分析与闭环",
        (
            FieldDef("FLD-FLT-001", "故障编号", "STRING"),
            FieldDef("FLD-FLT-002", "故障现象", "TEXT"),
            FieldDef("FLD-FLT-003", "故障原因", "TEXT"),
            FieldDef("FLD-FLT-004", "故障等级", "ENUM", options=_enum("轻微", "一般", "严重", "重大")),
            FieldDef("FLD-FLT-005", "发现时间", "DATETIME"),
            FieldDef("FLD-FLT-006", "恢复时间", "DATETIME"),
            FieldDef("FLD-FLT-007", "停机时长", "DECIMAL", unit="小时"),
            FieldDef("FLD-FLT-008", "处理措施", "LONG_TEXT"),
            FieldDef("FLD-FLT-009", "处理人", "STRING"),
            FieldDef("FLD-FLT-010", "是否重复故障", "BOOLEAN"),
            FieldDef("FLD-FLT-011", "关联设备", "ENTITY_REF"),
            FieldDef("FLD-FLT-012", "关联任务", "ENTITY_REF"),
        ),
    ),
    FieldGroupCatalog(
        "FG-BIZ-MAINTENANCE", "维修管理", 180, "维修工单与维保计划",
        (
            FieldDef("FLD-MNT-001", "维修单号", "STRING"),
            FieldDef("FLD-MNT-002", "维修类型", "ENUM", options=_enum("故障维修", "计划保养", "预防性维护", "改造", "其他")),
            FieldDef("FLD-MNT-003", "维修内容", "LONG_TEXT"),
            FieldDef("FLD-MNT-004", "计划开始时间", "DATETIME"),
            FieldDef("FLD-MNT-005", "计划完成时间", "DATETIME"),
            FieldDef("FLD-MNT-006", "实际完成时间", "DATETIME"),
            FieldDef("FLD-MNT-007", "维修负责人", "STRING"),
            FieldDef("FLD-MNT-008", "维修班组", "STRING"),
            FieldDef("FLD-MNT-009", "维修费用", "DECIMAL", unit="元"),
            FieldDef("FLD-MNT-010", "验收结果", "ENUM", options=_enum("合格", "不合格", "待复验")),
            FieldDef("FLD-MNT-011", "使用备件", "LONG_TEXT"),
            FieldDef("FLD-MNT-012", "关联设备", "ENTITY_REF"),
        ),
    ),
    FieldGroupCatalog(
        "FG-BIZ-EMERGENCY", "应急管理", 190, "应急事件、资源与处置",
        (
            FieldDef("FLD-EMG-001", "事件编号", "STRING"),
            FieldDef("FLD-EMG-002", "事件名称", "STRING"),
            FieldDef("FLD-EMG-003", "事件类型", "ENUM", options=_enum("泄漏", "火灾", "爆炸", "人员伤害", "自然灾害", "其他")),
            FieldDef("FLD-EMG-004", "事件等级", "ENUM", options=_enum("Ⅰ级", "Ⅱ级", "Ⅲ级", "Ⅳ级")),
            FieldDef("FLD-EMG-005", "发生时间", "DATETIME"),
            FieldDef("FLD-EMG-006", "发生地点", "STRING"),
            FieldDef("FLD-EMG-007", "事件描述", "LONG_TEXT"),
            FieldDef("FLD-EMG-008", "处置状态", "ENUM", options=_enum("响应中", "处置中", "已结束", "已归档")),
            FieldDef("FLD-EMG-009", "指挥人", "STRING"),
            FieldDef("FLD-EMG-010", "应急队伍", "STRING"),
            FieldDef("FLD-EMG-011", "资源调拨", "LONG_TEXT"),
            FieldDef("FLD-EMG-012", "结束时间", "DATETIME"),
            FieldDef("FLD-EMG-013", "复盘总结", "LONG_TEXT"),
            FieldDef("FLD-EMG-014", "关联设施", "ENTITY_REF"),
        ),
    ),
    FieldGroupCatalog(
        "FG-BIZ-PIPELINE", "管线管理", 200, "管线台账与运行参数",
        (
            FieldDef("FLD-PIP-001", "管线编号", "STRING"),
            FieldDef("FLD-PIP-002", "管线名称", "STRING"),
            FieldDef("FLD-PIP-003", "管线规格", "STRING"),
            FieldDef("FLD-PIP-004", "管线材质", "STRING"),
            FieldDef("FLD-PIP-005", "介质类型", "STRING"),
            FieldDef("FLD-PIP-006", "设计压力", "DECIMAL", unit="MPa"),
            FieldDef("FLD-PIP-007", "设计温度", "DECIMAL", unit="℃"),
            FieldDef("FLD-PIP-008", "敷设方式", "ENUM", options=_enum("架空", "直埋", "管沟", "管廊", "其他")),
            FieldDef("FLD-PIP-009", "管线长度", "DECIMAL", unit="米"),
            FieldDef("FLD-PIP-010", "起点位置", "STRING"),
            FieldDef("FLD-PIP-011", "终点位置", "STRING"),
            FieldDef("FLD-PIP-012", "投运日期", "DATE"),
            FieldDef("FLD-PIP-013", "关联管廊", "ENTITY_REF"),
        ),
    ),
)
# fmt: on


def all_fields() -> list[tuple[FieldGroupCatalog, FieldDef]]:
    out: list[tuple[FieldGroupCatalog, FieldDef]] = []
    for group in CATALOG:
        for field in group.fields:
            out.append((group, field))
    return out


def field_count() -> int:
    return sum(len(g.fields) for g in CATALOG)


def group_count() -> int:
    return len(CATALOG)
