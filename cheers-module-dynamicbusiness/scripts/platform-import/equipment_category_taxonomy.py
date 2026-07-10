"""
标准设备分类库（equipment categoryTypeCode=equipment）。

层级：equipment_root → 一级专业域 → 二级子系统 → 三级品类（叶子，型号挂接点）

一级专业域（14 项，对齐站场数字化交付与生产监视业务域）：
  供配电系统、电气设备及传动、仪器仪表与自动化、工艺动设备、工艺静设备、
  管道完整性监测、暖通空调系统、给排水系统、消防系统、建筑智能化系统、
  信息通信系统、机房工程与动环监控、计算与信息设备、其他设备。

参考资料：
  - 站场数字化恢复技术规格书（工艺/仪表/电力/通信/消防/暖通/阴保专业划分）
  - 生产监视中心支撑网系统设计方案（完整性监测、设备诊断、综合安防）
  - 数字化总体技术导则（试行）V1.0
  - 光缆在线监测系统设备及报价清单.xlsx

编码：EQCAT-* 稳定幂等键。
"""

from __future__ import annotations

from typing import TypedDict


class CategoryDef(TypedDict, total=False):
    code: str
    name: str
    children: list["CategoryDef"]


# fmt: off
TAXONOMY: list[CategoryDef] = [
    # ── 1. 供配电系统 ─────────────────────────────────────────
    {
        "code": "EQCAT-L1-ELPW",
        "name": "供配电系统",
        "children": [
            {
                "code": "EQCAT-L2-ELPW-HV",
                "name": "高压配电",
                "children": [
                    {"code": "EQCAT-DEV-HV-SWGR", "name": "高压开关柜"},
                    {"code": "EQCAT-DEV-TRANSFORMER", "name": "电力变压器"},
                    {"code": "EQCAT-DEV-HV-CABLE", "name": "高压电缆"},
                ],
            },
            {
                "code": "EQCAT-L2-ELPW-LV",
                "name": "低压配电",
                "children": [
                    {"code": "EQCAT-DEV-LV-SWGR", "name": "低压配电柜"},
                    {"code": "EQCAT-DEV-PDU-PRECISION", "name": "精密配电柜"},
                    {"code": "EQCAT-DEV-ATS", "name": "双电源切换装置"},
                    {"code": "EQCAT-DEV-CAPACITOR", "name": "无功补偿柜"},
                ],
            },
            {
                "code": "EQCAT-L2-ELPW-UPS",
                "name": "不间断电源",
                "children": [
                    {"code": "EQCAT-DEV-UPS-CABINET", "name": "UPS电源柜"},
                    {"code": "EQCAT-DEV-BATTERY-BANK", "name": "蓄电池组"},
                ],
            },
            {
                "code": "EQCAT-L2-ELPW-EMERG",
                "name": "应急电源",
                "children": [
                    {"code": "EQCAT-DEV-GENSET", "name": "柴油发电机组"},
                ],
            },
            {
                "code": "EQCAT-L2-ELPW-METER",
                "name": "电能计量",
                "children": [
                    {"code": "EQCAT-DEV-POWER-METER", "name": "电量仪"},
                    {"code": "EQCAT-DEV-CT", "name": "电流互感器"},
                    {"code": "EQCAT-DEV-PT", "name": "电压互感器"},
                ],
            },
        ],
    },
    # ── 2. 电气设备及传动 ─────────────────────────────────────
    {
        "code": "EQCAT-L1-ELEQ",
        "name": "电气设备及传动",
        "children": [
            {
                "code": "EQCAT-L2-ELEQ-MOTOR",
                "name": "电动机",
                "children": [
                    {"code": "EQCAT-DEV-MOTOR-LV", "name": "低压电动机"},
                    {"code": "EQCAT-DEV-MOTOR-HV", "name": "高压电动机"},
                ],
            },
            {
                "code": "EQCAT-L2-ELEQ-DRIVE",
                "name": "变频与传动",
                "children": [
                    {"code": "EQCAT-DEV-VFD", "name": "变频器"},
                    {"code": "EQCAT-DEV-SOFT-STARTER", "name": "软启动器"},
                ],
            },
            {
                "code": "EQCAT-L2-ELEQ-SWITCH",
                "name": "开关电器",
                "children": [
                    {"code": "EQCAT-DEV-MCCB", "name": "断路器"},
                    {"code": "EQCAT-DEV-ISOLATOR", "name": "隔离开关"},
                    {"code": "EQCAT-DEV-CONTACTOR", "name": "接触器"},
                    {"code": "EQCAT-DEV-RELAY", "name": "继电器"},
                ],
            },
            {
                "code": "EQCAT-L2-ELEQ-LIGHT",
                "name": "照明装置",
                "children": [
                    {"code": "EQCAT-DEV-LIGHTING", "name": "一般照明灯具"},
                    {"code": "EQCAT-DEV-FLOOD-LIGHT", "name": "投光灯"},
                    {"code": "EQCAT-DEV-HIGH-MAST-LIGHT", "name": "高杆灯"},
                ],
            },
            {
                "code": "EQCAT-L2-ELEQ-PANEL",
                "name": "控制与场站箱柜",
                "children": [
                    {"code": "EQCAT-DEV-CTRL-BOX", "name": "控制箱"},
                    {"code": "EQCAT-DEV-OPERATOR-STATION", "name": "操作柱"},
                    {"code": "EQCAT-DEV-MAINT-BOX", "name": "检修箱"},
                    {"code": "EQCAT-DEV-BUTTON-BOX", "name": "按钮箱"},
                ],
            },
        ],
    },
    # ── 3. 仪器仪表与自动化 ───────────────────────────────────
    {
        "code": "EQCAT-L1-INST",
        "name": "仪器仪表与自动化",
        "children": [
            {
                "code": "EQCAT-L2-INST-PRESSURE",
                "name": "压力测量",
                "children": [
                    {"code": "EQCAT-DEV-INST-LOCAL", "name": "就地仪表"},
                    {"code": "EQCAT-DEV-PT-GAUGE", "name": "压力表"},
                    {"code": "EQCAT-DEV-PT-TX", "name": "压力变送器"},
                    {"code": "EQCAT-DEV-DPT-TX", "name": "差压变送器"},
                ],
            },
            {
                "code": "EQCAT-L2-INST-FLOW",
                "name": "流量测量",
                "children": [
                    {"code": "EQCAT-DEV-FT-TX", "name": "流量变送器"},
                    {"code": "EQCAT-DEV-FLOWMETER-ULTRA", "name": "超声波流量计"},
                    {"code": "EQCAT-DEV-FLOWMETER-TURB", "name": "涡轮流量计"},
                    {"code": "EQCAT-DEV-FLOWMETER-MASS", "name": "质量流量计"},
                ],
            },
            {
                "code": "EQCAT-L2-INST-LEVEL",
                "name": "液位测量",
                "children": [
                    {"code": "EQCAT-DEV-LT-TX", "name": "液位变送器"},
                    {"code": "EQCAT-DEV-LEVEL-GAUGE", "name": "液位计"},
                ],
            },
            {
                "code": "EQCAT-L2-INST-TEMP",
                "name": "温度测量",
                "children": [
                    {"code": "EQCAT-DEV-TT-TX", "name": "温度变送器"},
                    {"code": "EQCAT-DEV-RTD", "name": "热电阻"},
                    {"code": "EQCAT-DEV-TC", "name": "热电偶"},
                ],
            },
            {
                "code": "EQCAT-L2-INST-ENV",
                "name": "环境检测仪表",
                "children": [
                    {"code": "EQCAT-DEV-SENSOR-TH", "name": "温湿度传感器"},
                    {"code": "EQCAT-DEV-SENSOR-O2", "name": "氧气传感器"},
                    {"code": "EQCAT-DEV-SENSOR-CO2", "name": "二氧化碳传感器"},
                    {"code": "EQCAT-DEV-SENSOR-GAS", "name": "可燃气体探测器"},
                    {"code": "EQCAT-DEV-SENSOR-LEAK", "name": "漏水监测器"},
                ],
            },
            {
                "code": "EQCAT-L2-INST-ANALYZER",
                "name": "分析仪表",
                "children": [
                    {"code": "EQCAT-DEV-GAS-ANALYZER", "name": "气体分析仪"},
                    {"code": "EQCAT-DEV-WATER-ANALYZER", "name": "水质分析仪"},
                    {"code": "EQCAT-DEV-ANALYZER-HOUSE", "name": "分析小屋"},
                ],
            },
            {
                "code": "EQCAT-L2-INST-CTRL-VALVE",
                "name": "控制阀",
                "children": [
                    {"code": "EQCAT-DEV-CV-ELECTRIC", "name": "电动调节阀"},
                    {"code": "EQCAT-DEV-CV-PNEUMATIC", "name": "气动调节阀"},
                    {"code": "EQCAT-DEV-SOLENOID-VALVE", "name": "电磁阀"},
                ],
            },
            {
                "code": "EQCAT-L2-INST-METERING",
                "name": "贸易计量",
                "children": [
                    {"code": "EQCAT-DEV-METER-SKID", "name": "计量橇"},
                    {"code": "EQCAT-DEV-METER-GAS", "name": "气质分析橇"},
                ],
            },
            {
                "code": "EQCAT-L2-INST-PCS",
                "name": "生产控制系统",
                "children": [
                    {"code": "EQCAT-DEV-SCADA", "name": "站控SCADA系统"},
                    {"code": "EQCAT-DEV-SIS", "name": "SIS安全仪表系统"},
                    {"code": "EQCAT-DEV-ESD", "name": "ESD紧急停车系统"},
                    {"code": "EQCAT-DEV-PLC", "name": "PLC控制器"},
                    {"code": "EQCAT-DEV-RTU-PROC", "name": "过程控制RTU"},
                    {"code": "EQCAT-DEV-OPS-WORKSTATION", "name": "操作员站"},
                ],
            },
            {
                "code": "EQCAT-L2-INST-BACS",
                "name": "楼宇自控",
                "children": [
                    {"code": "EQCAT-DEV-ACU", "name": "ACU控制柜"},
                    {"code": "EQCAT-DEV-DCS", "name": "DCS控制站"},
                    {"code": "EQCAT-DEV-JUNCTION-BOX", "name": "仪表接线盒"},
                ],
            },
        ],
    },
    # ── 4. 工艺动设备 ─────────────────────────────────────────
    {
        "code": "EQCAT-L1-ROTD",
        "name": "工艺动设备",
        "children": [
            {
                "code": "EQCAT-L2-ROTD-PUMP",
                "name": "泵",
                "children": [
                    {"code": "EQCAT-DEV-PUMP-CENT", "name": "离心泵"},
                    {"code": "EQCAT-DEV-PUMP-RECIP", "name": "往复泵"},
                    {"code": "EQCAT-DEV-PUMP-SCREW", "name": "螺杆泵"},
                    {"code": "EQCAT-DEV-PUMP-GEAR", "name": "齿轮泵"},
                ],
            },
            {
                "code": "EQCAT-L2-ROTD-COMP",
                "name": "压缩机",
                "children": [
                    {"code": "EQCAT-DEV-COMP-CENT", "name": "离心压缩机"},
                    {"code": "EQCAT-DEV-COMP-RECIP", "name": "往复压缩机"},
                    {"code": "EQCAT-DEV-COMP-SCREW", "name": "螺杆压缩机"},
                ],
            },
            {
                "code": "EQCAT-L2-ROTD-FAN",
                "name": "风机",
                "children": [
                    {"code": "EQCAT-DEV-FAN-AXIAL", "name": "轴流风机"},
                    {"code": "EQCAT-DEV-FAN-CENT", "name": "离心风机"},
                    {"code": "EQCAT-DEV-FAN-ROOTS", "name": "罗茨风机"},
                ],
            },
            {
                "code": "EQCAT-L2-ROTD-AIR",
                "name": "空气压缩机",
                "children": [
                    {"code": "EQCAT-DEV-AIR-COMP", "name": "空气压缩机"},
                    {"code": "EQCAT-DEV-AIR-DRYER", "name": "空气干燥机"},
                ],
            },
            {
                "code": "EQCAT-L2-ROTD-MIX",
                "name": "搅拌混合设备",
                "children": [
                    {"code": "EQCAT-DEV-AGITATOR", "name": "搅拌器"},
                    {"code": "EQCAT-DEV-MIXER", "name": "混合器"},
                ],
            },
        ],
    },
    # ── 5. 工艺静设备 ─────────────────────────────────────────
    {
        "code": "EQCAT-L1-STAT",
        "name": "工艺静设备",
        "children": [
            {
                "code": "EQCAT-L2-STAT-VESSEL",
                "name": "压力容器与储罐",
                "children": [
                    {"code": "EQCAT-DEV-TANK-STORAGE", "name": "储罐"},
                    {"code": "EQCAT-DEV-PRESSURE-VESSEL", "name": "压力容器"},
                    {"code": "EQCAT-DEV-SEPARATOR", "name": "分离器"},
                ],
            },
            {
                "code": "EQCAT-L2-STAT-FILTER",
                "name": "过滤分离设备",
                "children": [
                    {"code": "EQCAT-DEV-FILTER-SEP", "name": "过滤分离器"},
                    {"code": "EQCAT-DEV-CYCLONE", "name": "旋风分离器"},
                    {"code": "EQCAT-DEV-DEMISTER", "name": "除雾器"},
                ],
            },
            {
                "code": "EQCAT-L2-STAT-PIG",
                "name": "清管收发设备",
                "children": [
                    {"code": "EQCAT-DEV-PIG-TRAP", "name": "清管器收发筒"},
                    {"code": "EQCAT-DEV-LAUNCHER", "name": "发球筒"},
                    {"code": "EQCAT-DEV-RECEIVER", "name": "收球筒"},
                ],
            },
            {
                "code": "EQCAT-L2-STAT-HEAT",
                "name": "换热设备",
                "children": [
                    {"code": "EQCAT-DEV-HEAT-EXCHANGER", "name": "换热器"},
                    {"code": "EQCAT-DEV-CONDENSER", "name": "冷凝器"},
                    {"code": "EQCAT-DEV-REBOILER", "name": "再沸器"},
                ],
            },
            {
                "code": "EQCAT-L2-STAT-HEATERS",
                "name": "加热与放空",
                "children": [
                    {"code": "EQCAT-DEV-ELECTRIC-HEATER", "name": "电加热器"},
                    {"code": "EQCAT-DEV-BLOWDOWN", "name": "放空立管"},
                    {"code": "EQCAT-DEV-FLARE", "name": "火炬"},
                ],
            },
            {
                "code": "EQCAT-L2-STAT-VALVE",
                "name": "工艺阀门",
                "children": [
                    {"code": "EQCAT-DEV-VALVE-GATE", "name": "闸阀"},
                    {"code": "EQCAT-DEV-VALVE-BALL", "name": "球阀"},
                    {"code": "EQCAT-DEV-VALVE-BUTTERFLY", "name": "蝶阀"},
                    {"code": "EQCAT-DEV-VALVE-CHECK", "name": "止回阀"},
                    {"code": "EQCAT-DEV-VALVE-GLOBE", "name": "截止阀"},
                    {"code": "EQCAT-DEV-VALVE-PLUG", "name": "旋塞阀"},
                    {"code": "EQCAT-DEV-VALVE-DIAPHRAGM", "name": "隔膜阀"},
                    {"code": "EQCAT-DEV-VALVE-SAFETY", "name": "安全阀"},
                    {"code": "EQCAT-DEV-VALVE-RELIEF", "name": "减压阀"},
                    {"code": "EQCAT-DEV-VALVE-CTRL", "name": "自控阀门"},
                ],
            },
            {
                "code": "EQCAT-L2-STAT-PIPE",
                "name": "工艺管道与管件",
                "children": [
                    {"code": "EQCAT-DEV-PIPE-PROCESS", "name": "工艺管道"},
                    {"code": "EQCAT-DEV-FITTING-ELBOW", "name": "弯头"},
                    {"code": "EQCAT-DEV-FITTING-TEE", "name": "三通"},
                    {"code": "EQCAT-DEV-FITTING-REDUCER", "name": "大小头"},
                    {"code": "EQCAT-DEV-FITTING-CAP", "name": "管帽"},
                    {"code": "EQCAT-DEV-FITTING-PLUG", "name": "管塞"},
                    {"code": "EQCAT-DEV-FITTING-COUP", "name": "管箍"},
                    {"code": "EQCAT-DEV-FITTING-OLET", "name": "支管台"},
                    {"code": "EQCAT-DEV-FLANGE", "name": "法兰"},
                    {"code": "EQCAT-DEV-GASKET", "name": "垫片"},
                    {"code": "EQCAT-DEV-BLIND-SPECTACLE", "name": "8字盲板"},
                    {"code": "EQCAT-DEV-BLIND-SLIP", "name": "插板"},
                    {"code": "EQCAT-DEV-BLIND-SPACER", "name": "垫环"},
                ],
            },
        ],
    },
    # ── 6. 管道完整性监测（生产监视/IMS 业务域）──────────────
    {
        "code": "EQCAT-L1-PIM",
        "name": "管道完整性监测",
        "children": [
            {
                "code": "EQCAT-L2-PIM-LEAK",
                "name": "泄漏监测",
                "children": [
                    {"code": "EQCAT-DEV-LEAK-GAS", "name": "气体泄漏检测系统"},
                    {"code": "EQCAT-DEV-LEAK-LIQUID", "name": "液体泄漏监测系统"},
                ],
            },
            {
                "code": "EQCAT-L2-PIM-FIBER",
                "name": "光纤预警与光缆监测",
                "children": [
                    {"code": "EQCAT-DEV-FIBER-VIB", "name": "光纤振动预警系统"},
                    {"code": "EQCAT-DEV-FIBER-RTU", "name": "光缆在线监测RTU"},
                    {"code": "EQCAT-DEV-FIBER-FCM", "name": "光缆监测耦合单元"},
                    {"code": "EQCAT-DEV-FIBER-WDM", "name": "光缆监测波分单元"},
                    {"code": "EQCAT-DEV-FIBER-OLP", "name": "光线路保护单元"},
                    {"code": "EQCAT-DEV-FIBER-OXC", "name": "光交叉连接设备"},
                    {"code": "EQCAT-DEV-FIBER-NMS", "name": "光缆在线监测管理软件"},
                ],
            },
            {
                "code": "EQCAT-L2-PIM-GEO",
                "name": "地质灾害监测",
                "children": [
                    {"code": "EQCAT-DEV-GEO-HAZARD", "name": "地灾监测系统"},
                    {"code": "EQCAT-DEV-DISPLACEMENT", "name": "位移监测仪"},
                    {"code": "EQCAT-DEV-INCLINOMETER", "name": "倾斜监测仪"},
                ],
            },
            {
                "code": "EQCAT-L2-PIM-CP",
                "name": "智能阴极保护",
                "children": [
                    {"code": "EQCAT-DEV-CP-TEST-POST", "name": "阴极保护测试桩"},
                    {"code": "EQCAT-DEV-CP-POWER", "name": "阴保电源"},
                    {"code": "EQCAT-DEV-CP-ANODE", "name": "牺牲阳极"},
                    {"code": "EQCAT-DEV-CP-GROUND-BED", "name": "辅助阳极地床"},
                    {"code": "EQCAT-DEV-CP-DRAIN", "name": "排流装置"},
                    {"code": "EQCAT-DEV-CP-CABLE", "name": "阴保电缆"},
                ],
            },
            {
                "code": "EQCAT-L2-PIM-STRESS",
                "name": "应力应变监测",
                "children": [
                    {"code": "EQCAT-DEV-STRESS-MON", "name": "应力应变监测系统"},
                    {"code": "EQCAT-DEV-STRAIN-GAGE", "name": "应变计"},
                    {"code": "EQCAT-DEV-FIBER-STRAIN", "name": "光纤应变传感器"},
                ],
            },
        ],
    },
    # ── 7. 暖通空调系统 ───────────────────────────────────────
    {
        "code": "EQCAT-L1-HVAC",
        "name": "暖通空调系统",
        "children": [
            {
                "code": "EQCAT-L2-HVAC-CHILL",
                "name": "冷热源",
                "children": [
                    {"code": "EQCAT-DEV-CHILLER", "name": "冷水机组"},
                    {"code": "EQCAT-DEV-COOLING-TOWER", "name": "冷却塔"},
                    {"code": "EQCAT-DEV-BOILER", "name": "锅炉"},
                    {"code": "EQCAT-DEV-HEAT-PUMP", "name": "热泵机组"},
                ],
            },
            {
                "code": "EQCAT-L2-HVAC-AHU",
                "name": "空气处理机组",
                "children": [
                    {"code": "EQCAT-DEV-AHU", "name": "组合式空调机组"},
                    {"code": "EQCAT-DEV-PAU", "name": "新风机组"},
                    {"code": "EQCAT-DEV-FCU", "name": "风机盘管"},
                ],
            },
            {
                "code": "EQCAT-L2-HVAC-DUCT",
                "name": "通风与风阀",
                "children": [
                    {"code": "EQCAT-DEV-DAMPER-FIRE", "name": "防火阀"},
                    {"code": "EQCAT-DEV-DAMPER-ADJ", "name": "调节风阀"},
                    {"code": "EQCAT-DEV-MUFFLER", "name": "消声器"},
                ],
            },
        ],
    },
    # ── 8. 给排水系统 ─────────────────────────────────────────
    {
        "code": "EQCAT-L1-PLUM",
        "name": "给排水系统",
        "children": [
            {
                "code": "EQCAT-L2-PLUM-SUPPLY",
                "name": "给水设备",
                "children": [
                    {"code": "EQCAT-DEV-PUMP-SUPPLY", "name": "给水泵"},
                    {"code": "EQCAT-DEV-WATER-TANK", "name": "水箱"},
                    {"code": "EQCAT-DEV-PRESSURE-STATION", "name": "加压泵站"},
                ],
            },
            {
                "code": "EQCAT-L2-PLUM-DRAIN",
                "name": "排水设备",
                "children": [
                    {"code": "EQCAT-DEV-PUMP-DRAIN", "name": "排水泵"},
                    {"code": "EQCAT-DEV-SUMP", "name": "集水坑"},
                ],
            },
            {
                "code": "EQCAT-L2-PLUM-VALVE",
                "name": "建筑阀门",
                "children": [
                    {"code": "EQCAT-DEV-PLUM-VALVE-GATE", "name": "建筑闸阀"},
                    {"code": "EQCAT-DEV-PLUM-VALVE-BALL", "name": "建筑球阀"},
                    {"code": "EQCAT-DEV-PLUM-VALVE-BUTTERFLY", "name": "建筑蝶阀"},
                    {"code": "EQCAT-DEV-PLUM-VALVE-CHECK", "name": "建筑止回阀"},
                ],
            },
            {
                "code": "EQCAT-L2-PLUM-PIPE",
                "name": "建筑给排水管道",
                "children": [
                    {"code": "EQCAT-DEV-PIPE-RECLAIM", "name": "再生水管"},
                    {"code": "EQCAT-DEV-PIPE-DOMESTIC", "name": "生活给水管"},
                    {"code": "EQCAT-DEV-PIPE-HOT-COLD", "name": "冷热供水管"},
                    {"code": "EQCAT-DEV-PIPE-DRAIN", "name": "排水管"},
                ],
            },
        ],
    },
    # ── 9. 消防系统 ───────────────────────────────────────────
    {
        "code": "EQCAT-L1-FIRE",
        "name": "消防系统",
        "children": [
            {
                "code": "EQCAT-L2-FIRE-ALARM",
                "name": "火灾自动报警",
                "children": [
                    {"code": "EQCAT-DEV-FIRE-ALARM-HOST", "name": "火灾报警控制器"},
                    {"code": "EQCAT-DEV-SMOKE-DET", "name": "感烟探测器"},
                    {"code": "EQCAT-DEV-HEAT-DET", "name": "感温探测器"},
                    {"code": "EQCAT-DEV-FLAME-DET", "name": "火焰探测器"},
                    {"code": "EQCAT-DEV-MANUAL-ALARM", "name": "手动报警按钮"},
                ],
            },
            {
                "code": "EQCAT-L2-FIRE-SUPPRESS",
                "name": "灭火设备",
                "children": [
                    {"code": "EQCAT-DEV-AEROSOL", "name": "气溶胶灭火装置"},
                    {"code": "EQCAT-DEV-GAS-SUPPRESS", "name": "气体灭火装置"},
                    {"code": "EQCAT-DEV-SPRINKLER", "name": "自动喷水灭火设备"},
                    {"code": "EQCAT-DEV-FIRE-HYDRANT", "name": "消火栓"},
                    {"code": "EQCAT-DEV-FIRE-EXT", "name": "灭火器"},
                    {"code": "EQCAT-DEV-FIRE-BOX", "name": "消防箱"},
                ],
            },
            {
                "code": "EQCAT-L2-FIRE-WATER",
                "name": "消防供水",
                "children": [
                    {"code": "EQCAT-DEV-PUMP-FIRE", "name": "消防泵"},
                    {"code": "EQCAT-DEV-FIRE-TANK", "name": "消防水池"},
                ],
            },
            {
                "code": "EQCAT-L2-FIRE-SMOKE",
                "name": "防烟排烟",
                "children": [
                    {"code": "EQCAT-DEV-EXHAUST-FAN", "name": "排烟风机"},
                    {"code": "EQCAT-DEV-PRESS-FAN", "name": "正压送风机"},
                ],
            },
            {
                "code": "EQCAT-L2-FIRE-EVAC",
                "name": "应急疏散",
                "children": [
                    {"code": "EQCAT-DEV-EMERGENCY-LIGHT", "name": "应急照明灯具"},
                    {"code": "EQCAT-DEV-EXIT-SIGN", "name": "疏散指示标志"},
                ],
            },
        ],
    },
    # ── 10. 建筑智能化系统 ────────────────────────────────────
    {
        "code": "EQCAT-L1-BMS",
        "name": "建筑智能化系统",
        "children": [
            {
                "code": "EQCAT-L2-BMS-VIDEO",
                "name": "视频监控",
                "children": [
                    {"code": "EQCAT-DEV-CAMERA-IP", "name": "网络摄像机"},
                    {"code": "EQCAT-DEV-CAMERA-DOME", "name": "半球摄像机"},
                    {"code": "EQCAT-DEV-CAMERA-PTZ", "name": "云台摄像机"},
                    {"code": "EQCAT-DEV-CAMERA-BULLET", "name": "枪型摄像机"},
                    {"code": "EQCAT-DEV-CAMERA-EX", "name": "防爆摄像机"},
                    {"code": "EQCAT-DEV-INDUSTRIAL-TV", "name": "工业电视摄像机"},
                    {"code": "EQCAT-DEV-NVR", "name": "网络硬盘录像机"},
                    {"code": "EQCAT-DEV-DVR", "name": "硬盘录像机"},
                    {"code": "EQCAT-DEV-VIDEO-SERVER", "name": "视频管理服务器"},
                    {"code": "EQCAT-DEV-STREAM-SERVER", "name": "流媒体服务器"},
                    {"code": "EQCAT-DEV-STORAGE-SERVER", "name": "视频存储服务器"},
                    {"code": "EQCAT-DEV-VIDEO-ENCODER", "name": "视频编码器"},
                    {"code": "EQCAT-DEV-VIDEO-ANALYTICS", "name": "智能视频分析服务器"},
                    {"code": "EQCAT-DEV-MACHINE-VISION-SERVER", "name": "机器视觉推理服务器"},
                    {"code": "EQCAT-DEV-VIDEO-WORKSTATION", "name": "视频监控工作站"},
                    {"code": "EQCAT-DEV-MONITOR", "name": "监视器"},
                    {"code": "EQCAT-DEV-STORAGE-DISK", "name": "监控存储硬盘"},
                    {"code": "EQCAT-DEV-MOBILE-DOME", "name": "布控球"},
                    {"code": "EQCAT-DEV-CAMERA-LIGHT", "name": "监控补光灯"},
                    {"code": "EQCAT-DEV-CAMERA-POLE", "name": "监控立杆"},
                    {"code": "EQCAT-DEV-SPD-VIDEO", "name": "视频监控防雷器"},
                ],
            },
            {
                "code": "EQCAT-L2-BMS-DISPLAY",
                "name": "大屏与显控",
                "children": [
                    {"code": "EQCAT-DEV-LED-PANEL", "name": "LED显示屏"},
                    {"code": "EQCAT-DEV-VIDEO-WALL-CTRL", "name": "拼接控制器"},
                    {"code": "EQCAT-DEV-VIDEO-SENDER", "name": "视频发送卡"},
                    {"code": "EQCAT-DEV-VIDEO-DECODER", "name": "视频解码器"},
                    {"code": "EQCAT-DEV-DISPLAY-MOUNT", "name": "大屏安装支架"},
                    {"code": "EQCAT-DEV-KVM", "name": "KVM切换器"},
                    {"code": "EQCAT-DEV-CONSOLE-LCD", "name": "机架式控制台"},
                ],
            },
            {
                "code": "EQCAT-L2-BMS-INTEG",
                "name": "综合安防管理",
                "children": [
                    {"code": "EQCAT-DEV-SEC-PLATFORM-SERVER", "name": "综合安防管理平台服务器"},
                    {"code": "EQCAT-DEV-SEC-FUSION-SERVER", "name": "综合安防融合应用服务器"},
                    {"code": "EQCAT-DEV-SEC-PLATFORM-CLIENT", "name": "综合安防管理客户端"},
                    {"code": "EQCAT-DEV-SEC-LINKAGE", "name": "安防联动控制器"},
                    {"code": "EQCAT-DEV-SEC-PLATFORM-SOFTWARE", "name": "综合安防管理平台软件"},
                ],
            },
            {
                "code": "EQCAT-L2-BMS-INTRUSION",
                "name": "入侵报警",
                "children": [
                    {"code": "EQCAT-DEV-ALARM-HOST", "name": "入侵报警主机"},
                    {"code": "EQCAT-DEV-IR-DETECTOR", "name": "红外探测器"},
                    {"code": "EQCAT-DEV-INTRUSION-DETECTOR", "name": "复合入侵探测器"},
                    {"code": "EQCAT-DEV-GLASS-BREAK", "name": "玻璃破碎探测器"},
                    {"code": "EQCAT-DEV-VIBRATION-DET", "name": "振动探测器"},
                    {"code": "EQCAT-DEV-SOUNDER", "name": "声光报警器"},
                    {"code": "EQCAT-DEV-ALARM-KEYPAD", "name": "报警键盘"},
                    {"code": "EQCAT-DEV-EMERGENCY-BUTTON", "name": "紧急求助按钮"},
                    {"code": "EQCAT-DEV-INTRUSION-SOFTWARE", "name": "入侵报警软件"},
                    {"code": "EQCAT-DEV-INTRUSION-WS", "name": "入侵报警工作站"},
                ],
            },
            {
                "code": "EQCAT-L2-BMS-PERIM",
                "name": "周界安防",
                "children": [
                    {"code": "EQCAT-DEV-PERIM-ALARM", "name": "周界报警主机"},
                    {"code": "EQCAT-DEV-PERIM-FIBER", "name": "振动光纤周界主机"},
                    {"code": "EQCAT-DEV-IR-BEAM", "name": "红外对射探测器"},
                    {"code": "EQCAT-DEV-MICROWAVE-BEAM", "name": "微波对射探测器"},
                    {"code": "EQCAT-DEV-E-FENCE", "name": "电子围栏"},
                    {"code": "EQCAT-DEV-TENSION-FENCE", "name": "张力围栏"},
                    {"code": "EQCAT-DEV-BURIED-VIB", "name": "地埋式振动传感器"},
                    {"code": "EQCAT-DEV-PERIM-RADAR", "name": "周界雷达"},
                    {"code": "EQCAT-DEV-LASER-PAN", "name": "激光云台"},
                    {"code": "EQCAT-DEV-PERIM-CAMERA", "name": "周界联动摄像机"},
                    {"code": "EQCAT-DEV-PERIM-SOFTWARE", "name": "周界安防管理软件"},
                ],
            },
            {
                "code": "EQCAT-L2-BMS-ACCESS",
                "name": "出入口控制",
                "children": [
                    {"code": "EQCAT-DEV-ACCESS-SINGLE", "name": "单门门禁系统"},
                    {"code": "EQCAT-DEV-ACCESS-DOUBLE", "name": "双门门禁系统"},
                    {"code": "EQCAT-DEV-ACCESS-CTRL", "name": "门禁控制器"},
                    {"code": "EQCAT-DEV-ACCESS-READER", "name": "门禁读卡器"},
                    {"code": "EQCAT-DEV-ACCESS-LOCK", "name": "门禁电锁"},
                    {"code": "EQCAT-DEV-ACCESS-BUTTON", "name": "开门出门按钮"},
                    {"code": "EQCAT-DEV-ACCESS-GATE", "name": "人行通道闸机"},
                    {"code": "EQCAT-DEV-TURNSTILE", "name": "三辊闸摆闸"},
                    {"code": "EQCAT-DEV-FACE-TERMINAL", "name": "人脸识别终端"},
                    {"code": "EQCAT-DEV-VISITOR-KIOSK", "name": "访客机"},
                    {"code": "EQCAT-DEV-CARD-DISPENSER", "name": "发卡器"},
                    {"code": "EQCAT-DEV-ACCESS-EXIT-DEVICE", "name": "出门释放装置"},
                    {"code": "EQCAT-DEV-ACCESS-SERVER", "name": "门禁管理服务器"},
                    {"code": "EQCAT-DEV-ACCESS-SOFTWARE", "name": "门禁管理软件"},
                ],
            },
            {
                "code": "EQCAT-L2-BMS-INTERCOM",
                "name": "可视对讲",
                "children": [
                    {"code": "EQCAT-DEV-INTERCOM-HOST", "name": "可视对讲管理主机"},
                    {"code": "EQCAT-DEV-INTERCOM-OUTDOOR", "name": "门口对讲主机"},
                    {"code": "EQCAT-DEV-INTERCOM-INDOOR", "name": "室内对讲分机"},
                    {"code": "EQCAT-DEV-INTERCOM-SERVER", "name": "可视对讲服务器"},
                    {"code": "EQCAT-DEV-INTERCOM-SOFTWARE", "name": "可视对讲管理软件"},
                ],
            },
            {
                "code": "EQCAT-L2-BMS-POSITION",
                "name": "人员定位",
                "children": [
                    {"code": "EQCAT-DEV-POS-READER", "name": "定位读写主机"},
                    {"code": "EQCAT-DEV-POS-ACCESSORY", "name": "定位配套附件"},
                    {"code": "EQCAT-DEV-POS-TERMINAL", "name": "定位移动终端"},
                    {"code": "EQCAT-DEV-POS-SERVER", "name": "定位系统服务器"},
                    {"code": "EQCAT-DEV-POS-SOFTWARE", "name": "定位系统软件"},
                    {"code": "EQCAT-DEV-POS-WS", "name": "定位系统工作站"},
                ],
            },
            {
                "code": "EQCAT-L2-BMS-PATROL",
                "name": "电子巡更",
                "children": [
                    {"code": "EQCAT-DEV-PATROL-READER", "name": "巡更读卡器"},
                    {"code": "EQCAT-DEV-PATROL-STICK", "name": "巡更棒"},
                    {"code": "EQCAT-DEV-PATROL-POINT", "name": "巡更点"},
                    {"code": "EQCAT-DEV-PATROL-SERVER", "name": "巡更管理服务器"},
                    {"code": "EQCAT-DEV-PATROL-SOFTWARE", "name": "巡更管理软件"},
                ],
            },
            {
                "code": "EQCAT-L2-BMS-UAV",
                "name": "无人机巡检",
                "children": [
                    {"code": "EQCAT-DEV-UAV", "name": "巡检无人机"},
                    {"code": "EQCAT-DEV-UAV-DOCK", "name": "无人机机库"},
                    {"code": "EQCAT-DEV-UAV-NMS", "name": "无人机巡检管理软件"},
                ],
            },
            {
                "code": "EQCAT-L2-BMS-PARKING",
                "name": "停车场管理",
                "children": [
                    {"code": "EQCAT-DEV-PARKING-BARRIER", "name": "道闸"},
                    {"code": "EQCAT-DEV-LPR-CAMERA", "name": "车牌识别摄像机"},
                    {"code": "EQCAT-DEV-PARKING-TERMINAL", "name": "停车收费终端"},
                    {"code": "EQCAT-DEV-PARKING-BOOTH", "name": "停车收费岗亭"},
                    {"code": "EQCAT-DEV-PARKING-DISPLAY", "name": "车位信息显示屏"},
                    {"code": "EQCAT-DEV-PARKING-GUIDANCE", "name": "车位引导系统"},
                    {"code": "EQCAT-DEV-PARKING-MAGNET", "name": "地磁车位探测器"},
                    {"code": "EQCAT-DEV-PARKING-SERVER", "name": "停车场管理服务器"},
                    {"code": "EQCAT-DEV-PARKING-SOFTWARE", "name": "停车场管理软件"},
                ],
            },
            {
                "code": "EQCAT-L2-BMS-PA",
                "name": "公共广播",
                "children": [
                    {"code": "EQCAT-DEV-PA-HOST", "name": "广播主机"},
                    {"code": "EQCAT-DEV-PA-AMP", "name": "广播功放"},
                    {"code": "EQCAT-DEV-PA-SPEAKER", "name": "广播扬声器"},
                    {"code": "EQCAT-DEV-PA-EX-SPEAKER", "name": "防爆扩音扬声器"},
                    {"code": "EQCAT-DEV-PA-TERMINAL", "name": "广播终端"},
                    {"code": "EQCAT-DEV-PA-EMERGENCY", "name": "应急广播话筒"},
                    {"code": "EQCAT-DEV-PA-FIRE-LINK", "name": "消防广播联动模块"},
                    {"code": "EQCAT-DEV-PA-SOFTWARE", "name": "广播管理软件"},
                ],
            },
            {
                "code": "EQCAT-L2-BMS-CARD",
                "name": "一卡通与考勤",
                "children": [
                    {"code": "EQCAT-DEV-ATTENDANCE-TERMINAL", "name": "考勤终端"},
                    {"code": "EQCAT-DEV-CONSUME-TERMINAL", "name": "消费终端"},
                    {"code": "EQCAT-DEV-ONE-CARD-SERVER", "name": "一卡通管理服务器"},
                    {"code": "EQCAT-DEV-ONE-CARD-SOFTWARE", "name": "一卡通管理软件"},
                ],
            },
            {
                "code": "EQCAT-L2-BMS-SAFETY",
                "name": "安检与违禁品检测",
                "children": [
                    {"code": "EQCAT-DEV-METAL-DETECTOR", "name": "金属探测门"},
                    {"code": "EQCAT-DEV-XRAY-SCANNER", "name": "X光安检机"},
                    {"code": "EQCAT-DEV-EXPLOSIVE-DETECTOR", "name": "爆炸物探测器"},
                ],
            },
            {
                "code": "EQCAT-L2-BMS-INFRA",
                "name": "安防传输与机柜",
                "children": [
                    {"code": "EQCAT-DEV-SECURITY-CABINET", "name": "监控控制柜"},
                    {"code": "EQCAT-DEV-MEDIA-CONVERTER", "name": "光纤收发器"},
                    {"code": "EQCAT-DEV-POWER-BOX-CCTV", "name": "监控专用电源箱"},
                    {"code": "EQCAT-DEV-JUNCTION-BOX-CCTV", "name": "监控接线箱"},
                    {"code": "EQCAT-DEV-VIDEO-PATCH-PANEL", "name": "视频配线架"},
                    {"code": "EQCAT-DEV-SECURITY-SW", "name": "安防专用交换机"},
                ],
            },
        ],
    },
    # ── 11. 信息通信系统 ──────────────────────────────────────
    {
        "code": "EQCAT-L1-ICT",
        "name": "信息通信系统",
        "children": [
            {
                "code": "EQCAT-L2-ICT-DATA",
                "name": "数据通信网络",
                "children": [
                    {"code": "EQCAT-DEV-SW-CORE", "name": "核心交换机"},
                    {"code": "EQCAT-DEV-SW-ACCESS", "name": "接入交换机"},
                    {"code": "EQCAT-DEV-SW-RING", "name": "环网交换机"},
                    {"code": "EQCAT-DEV-SW-INDUST", "name": "工业以太网交换机"},
                    {"code": "EQCAT-DEV-NET-ROUTER", "name": "路由器"},
                    {"code": "EQCAT-DEV-NET-UTM", "name": "上网行为管理"},
                    {"code": "EQCAT-DEV-NET-FW", "name": "防火墙"},
                    {"code": "EQCAT-DEV-OPTICAL-MODULE", "name": "光模块"},
                ],
            },
            {
                "code": "EQCAT-L2-ICT-VOICE",
                "name": "IP语音通信",
                "children": [
                    {"code": "EQCAT-DEV-IP-PHONE", "name": "IP电话终端"},
                    {"code": "EQCAT-DEV-VOIP-GW", "name": "语音网关"},
                    {"code": "EQCAT-DEV-VOIP-SERVER", "name": "IP语音交换服务器"},
                    {"code": "EQCAT-DEV-VOIP-CTRL", "name": "IP电话控制主机"},
                    {"code": "EQCAT-DEV-VOIP-SOFTWARE", "name": "IP电话系统软件"},
                    {"code": "EQCAT-DEV-VOIP-WS", "name": "语音通信工作站"},
                ],
            },
            {
                "code": "EQCAT-L2-ICT-RADIO",
                "name": "无线对讲",
                "children": [
                    {"code": "EQCAT-DEV-RADIO-REPEATER", "name": "对讲中继主机"},
                    {"code": "EQCAT-DEV-RADIO-REMOTE", "name": "对讲远端设备"},
                    {"code": "EQCAT-DEV-RADIO-COUPLER", "name": "对讲耦合器"},
                    {"code": "EQCAT-DEV-RADIO-SPLITTER", "name": "对讲功分器"},
                    {"code": "EQCAT-DEV-RADIO-ANTENNA", "name": "对讲天线"},
                    {"code": "EQCAT-DEV-RADIO-DUPLEXER", "name": "对讲双工器"},
                    {"code": "EQCAT-DEV-RADIO-HANDSET", "name": "对讲手持机"},
                    {"code": "EQCAT-DEV-RADIO-NMS", "name": "对讲网管软件"},
                ],
            },
            {
                "code": "EQCAT-L2-ICT-FIBER",
                "name": "通信光缆线路",
                "children": [
                    {"code": "EQCAT-DEV-CABLE-COMM", "name": "通信光缆"},
                    {"code": "EQCAT-DEV-CABLE-LV", "name": "通信电缆"},
                    {"code": "EQCAT-DEV-FIBER-JOINT", "name": "光缆接头盒"},
                    {"code": "EQCAT-DEV-ODF", "name": "光纤配线架"},
                ],
            },
            {
                "code": "EQCAT-L2-ICT-RACK",
                "name": "机柜与综合布线",
                "children": [
                    {"code": "EQCAT-DEV-RACK-SERVER", "name": "服务器机柜"},
                    {"code": "EQCAT-DEV-RACK-NET", "name": "网络机柜"},
                ],
            },
        ],
    },
    # ── 12. 机房工程与动环监控 ────────────────────────────────
    {
        "code": "EQCAT-L1-DCIM",
        "name": "机房工程与动环监控",
        "children": [
            {
                "code": "EQCAT-L2-DCIM-CRAC",
                "name": "机房精密空调",
                "children": [
                    {"code": "EQCAT-DEV-CRAC", "name": "机房专用空调"},
                    {"code": "EQCAT-DEV-INROW-AC", "name": "行间空调"},
                ],
            },
            {
                "code": "EQCAT-L2-DCIM-MONITOR",
                "name": "动力环境监控",
                "children": [
                    {"code": "EQCAT-DEV-DCIM-HVAC", "name": "空调监控模块"},
                    {"code": "EQCAT-DEV-DCIM-FRESH-AIR", "name": "新风监控模块"},
                    {"code": "EQCAT-DEV-DCIM-UPS", "name": "UPS监控模块"},
                    {"code": "EQCAT-DEV-DCIM-SMS", "name": "动环短信模块"},
                    {"code": "EQCAT-DEV-DCIM-SOFTWARE", "name": "动环监控软件"},
                    {"code": "EQCAT-DEV-DCIM-HOST", "name": "动环监控主机"},
                    {"code": "EQCAT-DEV-DCIM-TERMINAL", "name": "动环管理终端"},
                    {"code": "EQCAT-DEV-DCIM-SERVER", "name": "机房监控服务器"},
                ],
            },
        ],
    },
    # ── 13. 计算与信息设备 ────────────────────────────────────
    {
        "code": "EQCAT-L1-IT",
        "name": "计算与信息设备",
        "children": [
            {
                "code": "EQCAT-L2-IT-SERVER",
                "name": "服务器",
                "children": [
                    {"code": "EQCAT-DEV-SERVER-APP", "name": "应用服务器"},
                    {"code": "EQCAT-DEV-SERVER-DB", "name": "数据库服务器"},
                    {"code": "EQCAT-DEV-SERVER-GIS", "name": "GIS服务器"},
                    {"code": "EQCAT-DEV-SERVER-HCI", "name": "超融合服务器"},
                    {"code": "EQCAT-DEV-SERVER-GPU", "name": "GPU计算节点"},
                ],
            },
            {
                "code": "EQCAT-L2-IT-CLIENT",
                "name": "终端与外设",
                "children": [
                    {"code": "EQCAT-DEV-WS", "name": "工作站"},
                    {"code": "EQCAT-DEV-LAPTOP", "name": "便携式计算机"},
                    {"code": "EQCAT-DEV-PRINTER-COLOR", "name": "彩色打印机"},
                    {"code": "EQCAT-DEV-PRINTER-MONO", "name": "黑白打印机"},
                ],
            },
        ],
    },
    # ── 14. 其他设备 ──────────────────────────────────────────
    {
        "code": "EQCAT-L1-MISC",
        "name": "其他设备",
        "children": [
            {"code": "EQCAT-DEV-UNCATEGORIZED", "name": "未分类设备"},
        ],
    },
]
# fmt: on

# 设备名称 → 叶子分类 code（弱电清单 + 参考资料常见名称）
DEVICE_TO_LEAF: dict[str, str] = {
    # 楼宇自控 / 仪表
    "ACU柜": "EQCAT-DEV-ACU",
    "分区ACU柜": "EQCAT-DEV-ACU",
    "CO2传感器": "EQCAT-DEV-SENSOR-CO2",
    "O2传感器": "EQCAT-DEV-SENSOR-O2",
    "二氧化碳传感器": "EQCAT-DEV-SENSOR-CO2",
    "氧气传感器": "EQCAT-DEV-SENSOR-O2",
    "温湿度传感器": "EQCAT-DEV-SENSOR-TH",
    "漏水监测器": "EQCAT-DEV-SENSOR-LEAK",
    # 视频监控 / 工业电视
    "网络摄像机": "EQCAT-DEV-CAMERA-IP",
    "红外半球摄像机": "EQCAT-DEV-CAMERA-DOME",
    "枪型摄像机": "EQCAT-DEV-CAMERA-BULLET",
    "防爆摄像机": "EQCAT-DEV-CAMERA-EX",
    "网络硬盘录像机": "EQCAT-DEV-NVR",
    "NVR": "EQCAT-DEV-NVR",
    "硬盘录像机": "EQCAT-DEV-DVR",
    "DVR": "EQCAT-DEV-DVR",
    "视频管理服务器": "EQCAT-DEV-VIDEO-SERVER",
    "流媒体服务器": "EQCAT-DEV-STREAM-SERVER",
    "视频存储服务器": "EQCAT-DEV-STORAGE-SERVER",
    "视频编码器": "EQCAT-DEV-VIDEO-ENCODER",
    "智能视频分析服务器": "EQCAT-DEV-VIDEO-ANALYTICS",
    "机器视觉推理服务器": "EQCAT-DEV-MACHINE-VISION-SERVER",
    "视频监控工作站": "EQCAT-DEV-VIDEO-WORKSTATION",
    "监视器": "EQCAT-DEV-MONITOR",
    "企业级硬盘": "EQCAT-DEV-STORAGE-DISK",
    "监控补光灯": "EQCAT-DEV-CAMERA-LIGHT",
    "监控立杆": "EQCAT-DEV-CAMERA-POLE",
    "视频监控防雷器": "EQCAT-DEV-SPD-VIDEO",
    # 大屏显控
    "LED全彩显示屏": "EQCAT-DEV-LED-PANEL",
    "LED屏": "EQCAT-DEV-LED-PANEL",
    "拼接控制器": "EQCAT-DEV-VIDEO-WALL-CTRL",
    "发送卡": "EQCAT-DEV-VIDEO-SENDER",
    "高清视频解码器": "EQCAT-DEV-VIDEO-DECODER",
    "大屏支架": "EQCAT-DEV-DISPLAY-MOUNT",
    "KVM多电脑切换器": "EQCAT-DEV-KVM",
    "机架式一体化键盘显示器": "EQCAT-DEV-CONSOLE-LCD",
    # 入侵报警
    "报警主机": "EQCAT-DEV-ALARM-HOST",
    "红外入侵报警主机": "EQCAT-DEV-ALARM-HOST",
    "报警器控制盒": "EQCAT-DEV-ALARM-HOST",
    "红外双鉴探测器": "EQCAT-DEV-IR-DETECTOR",
    "红外探测器": "EQCAT-DEV-IR-DETECTOR",
    "玻璃破碎探测器": "EQCAT-DEV-GLASS-BREAK",
    "振动探测器": "EQCAT-DEV-VIBRATION-DET",
    "声光报警器": "EQCAT-DEV-SOUNDER",
    "报警键盘": "EQCAT-DEV-ALARM-KEYPAD",
    "紧急求助按钮": "EQCAT-DEV-EMERGENCY-BUTTON",
    "入侵报警系统客户端软件": "EQCAT-DEV-INTRUSION-SOFTWARE",
    "入侵报警工作站": "EQCAT-DEV-INTRUSION-WS",
    # 周界安防
    "周界报警主机": "EQCAT-DEV-PERIM-ALARM",
    "振动光纤周界主机": "EQCAT-DEV-PERIM-FIBER",
    "光纤振动预警系统": "EQCAT-DEV-PERIM-FIBER",
    "红外对射探测器": "EQCAT-DEV-IR-BEAM",
    "微波对射探测器": "EQCAT-DEV-MICROWAVE-BEAM",
    "电子围栏": "EQCAT-DEV-E-FENCE",
    "张力围栏": "EQCAT-DEV-TENSION-FENCE",
    "地埋式振动传感器": "EQCAT-DEV-BURIED-VIB",
    "周界雷达": "EQCAT-DEV-PERIM-RADAR",
    "激光云台": "EQCAT-DEV-LASER-PAN",
    # 综合安防
    "综合安防管理平台服务器": "EQCAT-DEV-SEC-PLATFORM-SERVER",
    "综合安防融合应用服务器": "EQCAT-DEV-SEC-FUSION-SERVER",
    "综合安防管理客户端": "EQCAT-DEV-SEC-PLATFORM-CLIENT",
    "综合安防管理平台软件": "EQCAT-DEV-SEC-PLATFORM-SOFTWARE",
    "安防联动控制器": "EQCAT-DEV-SEC-LINKAGE",
    # 门禁 / 出入口
    "单门门禁控制系统": "EQCAT-DEV-ACCESS-SINGLE",
    "双门门禁控制系统": "EQCAT-DEV-ACCESS-DOUBLE",
    "门禁控制器": "EQCAT-DEV-ACCESS-CTRL",
    "门禁读卡器": "EQCAT-DEV-ACCESS-READER",
    "门禁电锁": "EQCAT-DEV-ACCESS-LOCK",
    "磁力锁": "EQCAT-DEV-ACCESS-LOCK",
    "电插锁": "EQCAT-DEV-ACCESS-LOCK",
    "开门出门按钮": "EQCAT-DEV-ACCESS-BUTTON",
    "开门按钮": "EQCAT-DEV-ACCESS-BUTTON",
    "出门按钮": "EQCAT-DEV-ACCESS-BUTTON",
    "人行通道闸机": "EQCAT-DEV-ACCESS-GATE",
    "通道闸": "EQCAT-DEV-ACCESS-GATE",
    "三辊闸": "EQCAT-DEV-TURNSTILE",
    "摆闸": "EQCAT-DEV-TURNSTILE",
    "人脸识别终端": "EQCAT-DEV-FACE-TERMINAL",
    "访客机": "EQCAT-DEV-VISITOR-KIOSK",
    "发卡器": "EQCAT-DEV-CARD-DISPENSER",
    "出门释放装置": "EQCAT-DEV-ACCESS-EXIT-DEVICE",
    "门禁系统服务器": "EQCAT-DEV-ACCESS-SERVER",
    "门禁系统服务器管理软件": "EQCAT-DEV-ACCESS-SOFTWARE",
    # 可视对讲
    "可视对讲管理主机": "EQCAT-DEV-INTERCOM-HOST",
    "门口对讲主机": "EQCAT-DEV-INTERCOM-OUTDOOR",
    "室内对讲分机": "EQCAT-DEV-INTERCOM-INDOOR",
    "可视对讲服务器": "EQCAT-DEV-INTERCOM-SERVER",
    "可视对讲管理软件": "EQCAT-DEV-INTERCOM-SOFTWARE",
    # IP 语音
    "IP电话": "EQCAT-DEV-IP-PHONE",
    "IP电话机": "EQCAT-DEV-IP-PHONE",
    "数字中继网关": "EQCAT-DEV-VOIP-GW",
    "IP电话控制主机": "EQCAT-DEV-VOIP-CTRL",
    "IP电话软交换服务器": "EQCAT-DEV-VOIP-SERVER",
    "IP电话系统客户端管理软件": "EQCAT-DEV-VOIP-SOFTWARE",
    "IP电话系统服务器管理软件": "EQCAT-DEV-VOIP-SOFTWARE",
    "语音通信工作站": "EQCAT-DEV-VOIP-WS",
    # 无线对讲
    "数字无线中继调度主机": "EQCAT-DEV-RADIO-REPEATER",
    "光纤直放站近端机": "EQCAT-DEV-RADIO-REMOTE",
    "无线远端机（光纤直放远端机）": "EQCAT-DEV-RADIO-REMOTE",
    "耦合器": "EQCAT-DEV-RADIO-COUPLER",
    "功分器": "EQCAT-DEV-RADIO-SPLITTER",
    "室内定向天线": "EQCAT-DEV-RADIO-ANTENNA",
    "定向天线": "EQCAT-DEV-RADIO-ANTENNA",
    "中心射频接口缆线": "EQCAT-DEV-RADIO-ANTENNA",
    "双工器": "EQCAT-DEV-RADIO-DUPLEXER",
    "信号剥离器": "EQCAT-DEV-RADIO-SPLITTER",
    "光纤直放站网管软件": "EQCAT-DEV-RADIO-NMS",
    "无线对讲手持机": "EQCAT-DEV-RADIO-HANDSET",
    # 人员定位
    "人员定位主机": "EQCAT-DEV-POS-READER",
    "人员定位主机（全向读写器含增益天线）": "EQCAT-DEV-POS-READER",
    "支架": "EQCAT-DEV-POS-ACCESSORY",
    "电源适配器": "EQCAT-DEV-POS-ACCESSORY",
    "移动终端": "EQCAT-DEV-POS-TERMINAL",
    "人员定位系统服务器": "EQCAT-DEV-POS-SERVER",
    "人员定位系统服务器管理软件": "EQCAT-DEV-POS-SOFTWARE",
    "人员定位系统客户端软件": "EQCAT-DEV-POS-SOFTWARE",
    "人员定位系统工作站": "EQCAT-DEV-POS-WS",
    # 安防配套
    "监控控制柜（19\"机柜）": "EQCAT-DEV-SECURITY-CABINET",
    "光纤收发器": "EQCAT-DEV-MEDIA-CONVERTER",
    "监控专用电源箱": "EQCAT-DEV-POWER-BOX-CCTV",
    "监控接线箱": "EQCAT-DEV-JUNCTION-BOX-CCTV",
    "视频配线架": "EQCAT-DEV-VIDEO-PATCH-PANEL",
    "安防专用交换机": "EQCAT-DEV-SECURITY-SW",
    # 停车场
    "道闸": "EQCAT-DEV-PARKING-BARRIER",
    "车牌识别摄像机": "EQCAT-DEV-LPR-CAMERA",
    "停车收费岗亭": "EQCAT-DEV-PARKING-BOOTH",
    "车位引导系统": "EQCAT-DEV-PARKING-GUIDANCE",
    "地磁车位探测器": "EQCAT-DEV-PARKING-MAGNET",
    # 广播
    "防爆扩音扬声器": "EQCAT-DEV-PA-EX-SPEAKER",
    "应急广播话筒": "EQCAT-DEV-PA-EMERGENCY",
    # 一卡通
    "考勤终端": "EQCAT-DEV-ATTENDANCE-TERMINAL",
    "消费终端": "EQCAT-DEV-CONSUME-TERMINAL",
    # 数据网络
    "环网交换机": "EQCAT-DEV-SW-RING",
    "环网交换机（安防交换机）": "EQCAT-DEV-SW-RING",
    "企业级核心交换机": "EQCAT-DEV-SW-CORE",
    "接入交换机": "EQCAT-DEV-SW-ACCESS",
    "上网行为管理主机": "EQCAT-DEV-NET-UTM",
    "安全路由器": "EQCAT-DEV-NET-ROUTER",
    "服务器机柜": "EQCAT-DEV-RACK-SERVER",
    "网络机柜": "EQCAT-DEV-RACK-NET",
    "光模块": "EQCAT-DEV-OPTICAL-MODULE",
    # 供配电
    "三相电量仪": "EQCAT-DEV-POWER-METER",
    "交流电互感器": "EQCAT-DEV-CT",
    "UPS电源柜": "EQCAT-DEV-UPS-CABINET",
    "蓄电池柜": "EQCAT-DEV-BATTERY-BANK",
    "精密配电柜": "EQCAT-DEV-PDU-PRECISION",
    # 机房动环
    "空调智能设备监控器": "EQCAT-DEV-DCIM-HVAC",
    "新风智能设备监控器": "EQCAT-DEV-DCIM-FRESH-AIR",
    "UPS智能设备监控器": "EQCAT-DEV-DCIM-UPS",
    "短信系统": "EQCAT-DEV-DCIM-SMS",
    "环境监测服务器管理软件": "EQCAT-DEV-DCIM-SOFTWARE",
    "集中监控管理主机": "EQCAT-DEV-DCIM-HOST",
    "管理终端": "EQCAT-DEV-DCIM-TERMINAL",
    "机房环境监测系统服务器": "EQCAT-DEV-DCIM-SERVER",
    # 计算终端
    "工作站": "EQCAT-DEV-WS",
    "应用服务器": "EQCAT-DEV-SERVER-APP",
    "数据库服务器": "EQCAT-DEV-SERVER-DB",
    "GIS服务器": "EQCAT-DEV-SERVER-GIS",
    "便携式电脑": "EQCAT-DEV-LAPTOP",
    "彩色打印机": "EQCAT-DEV-PRINTER-COLOR",
    "黑色打印机": "EQCAT-DEV-PRINTER-MONO",
    # 工艺静设备（站场数字化交付常见设备）
    "过滤分离器": "EQCAT-DEV-FILTER-SEP",
    "电加热器": "EQCAT-DEV-ELECTRIC-HEATER",
    "放空立管": "EQCAT-DEV-BLOWDOWN",
    "清管器收发球筒": "EQCAT-DEV-PIG-TRAP",
    "清管器收（发）球筒": "EQCAT-DEV-PIG-TRAP",
    # 工艺阀门（未指明阀型默认闸阀）
    "阀门": "EQCAT-DEV-VALVE-GATE",
    "工艺阀门": "EQCAT-DEV-VALVE-GATE",
    "自控阀门": "EQCAT-DEV-VALVE-CTRL",
    # 光缆在线监测（参考资料）
    "RTU": "EQCAT-DEV-FIBER-RTU",
    "光缆在线监测RTU": "EQCAT-DEV-FIBER-RTU",
    "FCM": "EQCAT-DEV-FIBER-FCM",
    "光纤在线监测设备,耦合单元": "EQCAT-DEV-FIBER-FCM",
    "光纤在线监测设备,波分WDM单元": "EQCAT-DEV-FIBER-WDM",
    "OLP": "EQCAT-DEV-FIBER-OLP",
    "OLP单元1:1": "EQCAT-DEV-FIBER-OLP",
    "OXC": "EQCAT-DEV-FIBER-OXC",
    "智能光缆在线监测管理系统": "EQCAT-DEV-FIBER-NMS",
    # 阴极保护
    "阴极保护测试桩": "EQCAT-DEV-CP-TEST-POST",
    "阴保电源": "EQCAT-DEV-CP-POWER",
    "牺牲阳极": "EQCAT-DEV-CP-ANODE",
    # 表6.3-2 管件
    "弯头": "EQCAT-DEV-FITTING-ELBOW",
    "三通": "EQCAT-DEV-FITTING-TEE",
    "大小头": "EQCAT-DEV-FITTING-REDUCER",
    "法兰": "EQCAT-DEV-FLANGE",
    "垫片": "EQCAT-DEV-GASKET",
    "8字盲板": "EQCAT-DEV-BLIND-SPECTACLE",
    "话站": "EQCAT-DEV-PA-SPEAKER",
    "扬声器": "EQCAT-DEV-PA-SPEAKER",
}


def flatten_taxonomy(
    nodes: list[CategoryDef],
    parent_code: str = "equipment_root",
) -> list[tuple[str, str, str, int]]:
    """返回 (code, name, parent_code, sort) 插入顺序：父在前。"""
    rows: list[tuple[str, str, str, int]] = []
    for idx, node in enumerate(nodes, start=1):
        code = node["code"]
        name = node["name"]
        rows.append((code, name, parent_code, idx))
        children = node.get("children") or []
        if children:
            rows.extend(flatten_taxonomy(children, code))
    return rows


def leaf_by_code() -> dict[str, tuple[str, str]]:
    result: dict[str, tuple[str, str]] = {}
    for code, name, _parent, _sort in flatten_taxonomy(TAXONOMY):
        if code.startswith("EQCAT-DEV-"):
            result[code] = (code, name)
    return result


def resolve_device_leaf(device_name: str) -> tuple[str | None, str | None]:
    code = DEVICE_TO_LEAF.get(device_name)
    if not code:
        simplified = device_name.split("（")[0].strip()
        code = DEVICE_TO_LEAF.get(simplified)
    if not code:
        return None, None
    leaves = leaf_by_code()
    if code in leaves:
        return code, leaves[code][1]
    return code, None
