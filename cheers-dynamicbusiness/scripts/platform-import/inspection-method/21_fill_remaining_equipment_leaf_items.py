#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
补全其余设备专业空叶子检查项（tenant=1）。

与 16–20 同口径：
- 名称：「检查内容（叶子分类名）」；同义不合并
- 有叶子挂叶子；清空对应 L3 糊挂（保留叶子上已有带后缀项）
- 双挂设备叶子 + 对应检查分类
- 软删曾挂在本批 L3 上的无后缀短名
- 按叶子同步型号适用集合（叶子上全部仍挂项）

已满不处理：综合安防系统、仪器仪表与自动化。

用法：
  python 21_fill_remaining_equipment_leaf_items.py --dry-run
  python 21_fill_remaining_equipment_leaf_items.py
  python 21_fill_remaining_equipment_leaf_items.py --only 供配电系统 消防系统
"""

from __future__ import annotations

import argparse
import random
import sys
import uuid
from typing import Dict, List, Optional, Sequence, Set, Tuple

import psycopg2

PKG = {
    "host": "127.0.0.1",
    "dbname": "sinopec",
    "user": "postgres",
    "password": "Coolhomer",
    "options": "-c search_path=dynamicbusiness",
}

TENANT = 1
INSPECTION_ITEM_MODEL_CODE = "inspection_item"
INSPECTION_CAT_PARENT = "站场检查"
UPDATER = "seed-equip-leaf-fill"

Check = Tuple[str, int]
Role = List[Check]

# ---------- 角色检查块 ----------
APPEARANCE = [("外观完好、安装牢固", 1), ("标识清晰", 3)]
WIRING = [("接线可靠、标识清晰", 32)]
POWER = [("上电指示正常", 28)]
COMM = [("通讯状态正常、无持续掉线", 27)]
LEAK = [("连接处无泄漏、无渗油", 4)]

PANEL = APPEARANCE + POWER + WIRING + [("柜门开合正常、接地可靠", 1), ("指示/仪表显示正常", 13)]
HV = APPEARANCE + POWER + [("绝缘/接地状态正常", 28), ("无异常温升异味", 30), ("柜门闭锁与标识正常", 3)]
UPS = APPEARANCE + POWER + [("输入输出电压正常", 28), ("蓄电池状态正常、无鼓胀渗液", 26), ("通讯/告警正常", 27)]
GEN = APPEARANCE + [("机油冷却液正常", 4), ("试机启动正常、无异响", 24), ("燃油/电量充足", 26), ("指示仪表正常", 13)]
METER = APPEARANCE + POWER + [("读数合理、无持续异常", 13), ("二次接线紧固", 32)]
CABLE = APPEARANCE + [("外护套完好、无破损", 1), ("接头紧固、标识清晰", 32), ("接地/固定可靠", 1)]

SWITCH_GEAR = APPEARANCE + POWER + [("分合闸/动作正常", 24), ("触头/接线无过热", 30)]
DRIVE = APPEARANCE + POWER + [("运行参数正常、无过载告警", 13), ("散热风扇正常", 30), ("通讯状态正常", 27)]
MOTOR = APPEARANCE + [("轴承温升正常、无异响", 30), ("接地可靠", 1), ("运行电流正常", 13)]
LIGHT = APPEARANCE + POWER + [("点亮正常、无闪烁", 35), ("防护罩完好", 1)]
CTRL_BOX = APPEARANCE + POWER + WIRING + [("按钮/指示正常", 35)]

PUMP = APPEARANCE + LEAK + [("运转平稳、无异响过热", 30), ("进出口压力/流量正常", 13), ("润滑密封正常", 4)]
COMPRESSOR = APPEARANCE + LEAK + [("运转平稳、无异响过热", 30), ("排气压力温度正常", 13), ("润滑油位正常", 4)]
FAN = APPEARANCE + [("运转平稳、无异响", 30), ("叶轮/皮带完好", 1), ("振动无明显异常", 18)]
MIXER = APPEARANCE + [("运转平稳、无卡涩", 24), ("密封无泄漏", 4), ("润滑正常", 4)]
AIR_DRYER = APPEARANCE + POWER + [("干燥效果/露点正常", 13), ("排污正常", 24)]

TANK = APPEARANCE + [("罐体无渗漏变形", 4), ("液位/压力指示正常", 13), ("呼吸阀/附件完好", 1), ("基础与接地正常", 1)]
VESSEL = APPEARANCE + LEAK + [("压力温度在允许范围", 13), ("安全附件完好", 1), ("基础与支座正常", 1)]
HEAT_EX = APPEARANCE + LEAK + [("进出口温差合理", 13), ("管束/壳体无异常振动", 18)]
PIG = APPEARANCE + LEAK + [("快开盲板/锁紧正常", 24), ("压力指示正常", 13)]
FILTER = APPEARANCE + LEAK + [("压差正常、无堵塞告警", 13), ("排污正常", 24)]
FLARE = APPEARANCE + [("结构牢固、无倾斜", 1), ("点火/长明灯状态正常", 35), ("阻火器完好", 1)]
HEATER = APPEARANCE + POWER + [("加热温度可控、无过热", 13), ("绝缘接地正常", 28)]
PIPE = APPEARANCE + LEAK + [("支架完好、无异常振动", 1), ("防腐保温完好", 1)]
FITTING = APPEARANCE + LEAK + [("紧固件无松动", 1)]
VALVE = [
    ("阀体及法兰无泄漏", 4),
    ("开关灵活到位、无卡涩", 24),
    ("阀位指示正确", 13),
    ("手轮/执行机构完好", 1),
    ("标识清晰", 3),
]
SAFETY_VALVE = APPEARANCE + [("铅封/定压标识清晰", 3), ("排放管畅通、无阻塞", 31), ("阀体无泄漏", 4)]

FIRE_DET = APPEARANCE + [("探测区域无遮挡", 31), ("上电指示正常", 28), ("与控制器通讯正常", 27), ("报警抽检/复位正常", 34)]
FIRE_PANEL = HOST = [
    ("主机上电与自检正常", 28),
    ("防区/回路状态正常、无持续故障", 36),
    ("通讯状态正常", 27),
    ("备用电源正常", 26),
    ("机箱外观完好、接线整齐", 1),
]
FIRE_BTN = APPEARANCE + [("按钮触发正常、告警上传", 34), ("玻璃/保护罩完好", 1)]
EXTINGUISHER = APPEARANCE + [("压力指示在绿区", 13), ("铅封完好、有效期可辨", 3), ("放置位置畅通", 31)]
HYDRANT = APPEARANCE + [("阀门开合正常", 24), ("水带水枪齐全完好", 1), ("无渗漏", 4)]
SPRINKLER = APPEARANCE + [("喷头无遮挡损坏", 31), ("管网无渗漏", 4), ("末端试水正常", 24)]
GAS_FIRE = APPEARANCE + [("钢瓶压力/称重正常", 13), ("喷嘴无堵塞", 31), ("联动控制正常", 34)]
FIRE_PUMP = PUMP + [("自动启停/远程控制正常", 24)]
FIRE_TANK = APPEARANCE + [("水位正常", 13), ("池体无渗漏", 4), ("人孔盖完好", 1)]
SMOKE_FAN = FAN + [("防火阀联动正常", 34)]
EXIT_LIGHT = APPEARANCE + POWER + [("应急点亮正常", 35), ("标志清晰可辨", 3)]

NET_SW = APPEARANCE + POWER + [("端口链路灯正常", 22), ("关键业务端口可达", 22), ("配置/管理可登录", 33)]
ROUTER_FW = APPEARANCE + POWER + [("会话/策略抽检正常", 33), ("链路无持续中断", 22), ("管理可登录", 33)]
OPTIC = APPEARANCE + [("光功率/链路正常", 22), ("接头清洁、标识清晰", 3)]
CABINET = [("柜体外观完好、门锁正常", 1), ("线缆整齐、标识清晰", 3), ("散热通风正常", 30), ("接地可靠", 1)]
FIBER = APPEARANCE + [("外护套完好", 1), ("标识清晰", 3), ("弯曲半径符合要求", 1)]
ODF = APPEARANCE + [("跳线整齐、标识清晰", 3), ("适配器无松动", 32)]
RADIO = APPEARANCE + POWER + [("收发通话清晰", 21), ("信道/组呼正常", 33), ("通讯链路正常", 22)]
RADIO_SOFT = [("软件可登录、功能可用", 33), ("中继/终端状态可见", 27), ("事件可查询", 33)]
VOIP_HOST = APPEARANCE + POWER + [("注册/中继正常", 27), ("试呼通话清晰", 21), ("管理可登录", 33)]
VOIP_PHONE = APPEARANCE + [("试呼通话清晰", 21), ("按键显示正常", 35), ("注册正常", 27)]
WORKSTATION = [
    ("开机与显示正常", 35),
    ("业务软件/客户端可登录", 33),
    ("网络连接正常", 22),
    ("键鼠外设正常", 33),
    ("外观完好、线缆整齐", 1),
]
SOFTWARE = [("软件可登录、功能可用", 33), ("业务功能抽检正常", 33), ("事件可查询、无持续故障", 33)]
SERVER = [
    ("上电指示正常", 28),
    ("关键服务进程正常", 33),
    ("远程管理可登录", 33),
    ("网络链路正常、无持续掉线", 22),
]

ENV_HOST = APPEARANCE + POWER + COMM + [("测点状态正常、无持续告警", 36), ("远程管理可登录", 33)]
ENV_MOD = APPEARANCE + POWER + COMM + [("采集数据正常", 13)]
CRAC = APPEARANCE + POWER + [("温湿度设定与实测正常", 13), ("滤网清洁、风口畅通", 31), ("冷凝排水正常", 4)]

HVAC_CHILLER = APPEARANCE + [("运行电流/压力正常", 13), ("无异常振动噪音", 18), ("水系统无泄漏", 4)]
HVAC_TOWER = APPEARANCE + [("风机运转正常", 30), ("布水均匀、无堵塞", 31), ("集水盘无渗漏", 4)]
HVAC_BOILER = APPEARANCE + [("燃烧/加热正常", 35), ("压力温度正常", 13), ("安全阀完好", 1)]
AHU = APPEARANCE + POWER + [("送回风正常", 30), ("滤网清洁", 31), ("盘管无严重积水", 4)]
FCU = APPEARANCE + POWER + [("风机档位正常", 30), ("冷凝水排放正常", 4)]
DAMPER = APPEARANCE + [("开合灵活到位", 24), ("执行机构动作正常", 24)]
FIRE_DAMPER = DAMPER + [("熔断/联动功能正常", 34)]
SILENCER = APPEARANCE + [("安装牢固、无脱落", 1)]

WATER_PUMP = PUMP
WATER_TANK = APPEARANCE + [("水位正常", 13), ("箱体无渗漏", 4), ("人孔盖完好", 1)]
SUMP = APPEARANCE + [("坑体无淤堵", 31), ("水位正常、泵控正常", 13)]
WATER_PIPE = APPEARANCE + LEAK + [("支架完好", 1), ("保温完好", 1)]
WATER_VALVE = VALVE

PIPE_LEAK = APPEARANCE + POWER + COMM + [("泄漏告警抽检正常", 34), ("测点状态正常", 36)]
CP_POWER = APPEARANCE + POWER + [("输出电压电流正常", 13), ("接地可靠", 1)]
CP_ANODE = APPEARANCE + [("连接可靠、标识清晰", 3), ("测试桩读数正常", 13)]
CP_CABLE = CABLE
FIBER_WARN = APPEARANCE + POWER + COMM + [("告警/事件可查询", 33), ("光路状态正常", 22)]
GEO = APPEARANCE + POWER + COMM + [("测值合理、无持续异常", 13)]
STRAIN = APPEARANCE + COMM + [("测值合理、无持续异常", 13), ("传感器安装牢固", 1)]

PLC = APPEARANCE + POWER + COMM + [("关键程序/强制无异常", 33), ("IO 状态正常", 13)]
SCADA = SOFTWARE + [("与现场通讯正常", 27)]
ESD_SIS = APPEARANCE + POWER + [("旁路/强制状态受控", 33), ("回路测试记录可查", 33), ("通讯状态正常", 27)]
RTU = APPEARANCE + POWER + COMM + [("遥测遥信正常", 13)]
OP_STATION = WORKSTATION

PRINTER = APPEARANCE + POWER + [("打印测试页正常", 33), ("耗材/纸张状态正常", 3)]
LAPTOP = APPEARANCE + [("开机与电量正常", 26), ("网络连接正常", 22), ("键鼠触控正常", 33)]
GENERIC = APPEARANCE + POWER[:1] + [("功能抽检正常", 33)]

# L2 → 检查分类名（不存在则创建于站场检查下）
L2_INSP_CAT: Dict[str, str] = {
    "供配电系统": "配电室检查",
    "电气设备及传动": "电气传动检查",
    "工艺动设备": "泵组检查",
    "工艺静设备": "工艺静设备检查",
    "消防系统": "消防检查",
    "信息通信系统": "通信检查",
    "机房工程与动环监控": "机房检查",
    "暖通空调系统": "暖通检查",
    "给排水系统": "给排水检查",
    "管道完整性监测": "管道监测检查",
    "自动化": "生产控制检查",
    "计算与信息设备": "机房检查",
    "其他设备": "其他设备检查",
}

# (L2, L3) → 角色；该 L3 下所有叶子共用角色（名称带叶子后缀区分）
L3_ROLE: Dict[Tuple[str, str], Role] = {
    # 供配电
    ("供配电系统", "低压配电"): PANEL,
    ("供配电系统", "高压配电"): HV,
    ("供配电系统", "不间断电源"): UPS,
    ("供配电系统", "应急电源"): GEN,
    ("供配电系统", "电能计量"): METER,
    # 电气传动
    ("电气设备及传动", "变频与传动"): DRIVE,
    ("电气设备及传动", "开关电器"): SWITCH_GEAR,
    ("电气设备及传动", "控制与场站箱柜"): CTRL_BOX,
    ("电气设备及传动", "照明装置"): LIGHT,
    ("电气设备及传动", "电动机"): MOTOR,
    # 动设备
    ("工艺动设备", "泵"): PUMP,
    ("工艺动设备", "压缩机"): COMPRESSOR,
    ("工艺动设备", "空气压缩机"): COMPRESSOR,
    ("工艺动设备", "风机"): FAN,
    ("工艺动设备", "搅拌混合设备"): MIXER,
    # 静设备
    ("工艺静设备", "储罐"): TANK,
    ("工艺静设备", "压力容器"): VESSEL,
    ("工艺静设备", "换热设备"): HEAT_EX,
    ("工艺静设备", "清管收发设备"): PIG,
    ("工艺静设备", "过滤分离设备"): FILTER,
    ("工艺静设备", "加热与放空"): FLARE,
    ("工艺静设备", "工艺管道与管件"): PIPE,
    ("工艺静设备", "工艺阀门"): VALVE,
    # 消防
    ("消防系统", "火灾自动报警"): FIRE_DET,
    ("消防系统", "灭火设备"): EXTINGUISHER,
    ("消防系统", "消防供水"): FIRE_PUMP,
    ("消防系统", "防烟排烟"): SMOKE_FAN,
    ("消防系统", "应急疏散"): EXIT_LIGHT,
    # 通信
    ("信息通信系统", "数据通信网络"): NET_SW,
    ("信息通信系统", "机柜与综合布线"): CABINET,
    ("信息通信系统", "通信光缆线路"): FIBER,
    ("信息通信系统", "无线对讲"): RADIO,
    ("信息通信系统", "IP语音通信"): VOIP_HOST,
    # 机房动环
    ("机房工程与动环监控", "动力环境监控"): ENV_MOD,
    ("机房工程与动环监控", "机房精密空调"): CRAC,
    # 暖通
    ("暖通空调系统", "冷热源"): HVAC_CHILLER,
    ("暖通空调系统", "空气处理机组"): AHU,
    ("暖通空调系统", "通风与风阀"): DAMPER,
    # 给排水
    ("给排水系统", "给水设备"): WATER_PUMP,
    ("给排水系统", "排水设备"): WATER_PUMP,
    ("给排水系统", "建筑给排水管道"): WATER_PIPE,
    ("给排水系统", "建筑阀门"): WATER_VALVE,
    # 管线监测
    ("管道完整性监测", "泄漏监测"): PIPE_LEAK,
    ("管道完整性监测", "智能阴极保护"): CP_POWER,
    ("管道完整性监测", "光纤预警与光缆监测"): FIBER_WARN,
    ("管道完整性监测", "地质灾害监测"): GEO,
    ("管道完整性监测", "应力应变监测"): STRAIN,
    # 自动化
    ("自动化", "生产控制系统"): SCADA,
    ("自动化", "PLC/DCS"): PLC,
    # 计算
    ("计算与信息设备", "服务器"): SERVER,
    ("计算与信息设备", "终端与外设"): WORKSTATION,
    # 其他
    ("其他设备", "未分类设备"): GENERIC,
}

# 叶子级覆盖（同 L3 内角色不同时）
LEAF_ROLE_OVERRIDE: Dict[str, Role] = {
    "高压电缆": CABLE,
    "蓄电池组": UPS,
    "柴油发电机组": GEN,
    "空气干燥机": AIR_DRYER,
    "电加热器": HEATER,
    "火炬": FLARE,
    "放空立管": APPEARANCE + [("畅通无堵塞", 31), ("阻火器完好", 1)],
    "安全阀": SAFETY_VALVE,
    "自控阀门": VALVE + POWER + COMM,
    "垫片": FITTING,
    "垫环": FITTING,
    "法兰": FITTING,
    "三通": FITTING,
    "弯头": FITTING,
    "大小头": FITTING,
    "插板": FITTING,
    "支管台": FITTING,
    "管塞": FITTING,
    "管帽": FITTING,
    "管箍": FITTING,
    "8字盲板": FITTING,
    "感烟探测器": FIRE_DET,
    "感温探测器": FIRE_DET,
    "火焰探测器": FIRE_DET,
    "手动报警按钮": FIRE_BTN,
    "火灾报警控制器": FIRE_PANEL,
    "灭火器": EXTINGUISHER,
    "消火栓": HYDRANT,
    "消防箱": HYDRANT,
    "自动喷水灭火设备": SPRINKLER,
    "气体灭火装置": GAS_FIRE,
    "消防泵": FIRE_PUMP,
    "消防水池": FIRE_TANK,
    "排烟风机": SMOKE_FAN,
    "正压送风机": SMOKE_FAN,
    "光模块": OPTIC,
    "防火墙": ROUTER_FW,
    "路由器": ROUTER_FW,
    "上网行为管理": ROUTER_FW,
    "光纤配线架": ODF,
    "光缆接头盒": ODF,
    "通信光缆": FIBER,
    "通信电缆": CABLE,
    "对讲网管软件": RADIO_SOFT,
    "对讲手持机": RADIO + [("电量正常", 26)],
    "IP电话系统软件": SOFTWARE,
    "IP电话终端": VOIP_PHONE,
    "语音通信工作站": WORKSTATION,
    "动环监控主机": ENV_HOST,
    "动环监控软件": SOFTWARE,
    "机房监控服务器": SERVER,
    "动环管理终端": WORKSTATION,
    "冷却塔": HVAC_TOWER,
    "锅炉": HVAC_BOILER,
    "热泵机组": HVAC_CHILLER,
    "冷水机组": HVAC_CHILLER,
    "风机盘管": FCU,
    "新风机组": AHU,
    "组合式空调机组": AHU,
    "防火阀": FIRE_DAMPER,
    "调节风阀": DAMPER,
    "消声器": SILENCER,
    "水箱": WATER_TANK,
    "集水坑": SUMP,
    "加压泵站": WATER_PUMP,
    "给水泵": WATER_PUMP,
    "排水泵": WATER_PUMP,
    "牺牲阳极": CP_ANODE,
    "辅助阳极地床": CP_ANODE,
    "阴极保护测试桩": CP_ANODE,
    "阴保电缆": CP_CABLE,
    "排流装置": CP_POWER,
    "阴保电源": CP_POWER,
    "光缆在线监测管理软件": SOFTWARE,
    "应变计": STRAIN,
    "光纤应变传感器": STRAIN,
    "位移监测仪": GEO,
    "倾斜监测仪": GEO,
    "地灾监测系统": GEO + SOFTWARE[:1],
    "PLC控制器": PLC,
    "ACU控制柜": PLC,
    "DCS控制站": PLC,
    "过程控制RTU": RTU,
    "站控SCADA系统": SCADA,
    "ESD紧急停车系统": ESD_SIS,
    "SIS安全仪表系统": ESD_SIS,
    "操作员站": OP_STATION,
    "黑白打印机": PRINTER,
    "便携式计算机": LAPTOP,
    "未分类设备": GENERIC,
}


def display_name(base: str, leaf: str) -> str:
    return f"{base}（{leaf}）"


def next_numeric_codes(cur, n: int) -> List[str]:
    cur.execute(
        """
        SELECT COALESCE(MAX(SUBSTRING(code FROM 6)::bigint), 1000000)
        FROM ent_inspection_item_t1
        WHERE deleted=false AND code ~ '^INSP-[0-9]{7}$'
        """
    )
    start = int(cur.fetchone()[0]) + 1
    codes: List[str] = []
    used = set()
    i = 0
    while len(codes) < n:
        candidate = f"INSP-{start + i:07d}"
        i += 1
        cur.execute(
            """
            SELECT 1 FROM ent_inspection_item_t1
            WHERE deleted=false AND tenant_id=%s AND code=%s
            """,
            (TENANT, candidate),
        )
        if cur.fetchone() or candidate in used:
            continue
        used.add(candidate)
        codes.append(candidate)
    return codes


def ensure_item(cur, *, model_id: int, name: str, method_template_id: int, code: str, dry_run: bool) -> int:
    cur.execute(
        """
        SELECT id FROM ent_inspection_item_t1
        WHERE deleted=false AND tenant_id=%s AND name=%s
        """,
        (TENANT, name),
    )
    row = cur.fetchone()
    if row:
        iid = int(row[0])
        if not dry_run:
            cur.execute(
                """
                UPDATE ent_inspection_item_t1
                SET method_template_id=%s, updater=%s, update_time=CURRENT_TIMESTAMP
                WHERE id=%s AND (method_template_id IS DISTINCT FROM %s)
                """,
                (method_template_id, UPDATER, iid, method_template_id),
            )
        return iid
    if dry_run:
        print(f"  create item {code} {name}")
        return -abs(hash(name)) % 10_000_000
    cur.execute(
        """
        INSERT INTO ent_inspection_item_t1 (
          tenant_id, entity_type_code, model_id, name, code, status,
          parent_id, tree_path, sort, method_template_id,
          attrs, custom_fields, creator, deleted
        ) VALUES (
          %s, 'inspection_item', %s, %s, %s, 1,
          0, NULL, 0, %s,
          '{}'::jsonb, '{}'::jsonb, %s, false
        ) RETURNING id
        """,
        (TENANT, model_id, name, code, method_template_id, UPDATER),
    )
    return int(cur.fetchone()[0])


def soft_unlink_category(cur, category_id: int, dry_run: bool) -> int:
    cur.execute(
        """
        SELECT id FROM dynamic_entity_category_relation_t1
        WHERE deleted=false AND tenant_id=%s
          AND category_id=%s AND entity_type_code='inspection_item'
        """,
        (TENANT, category_id),
    )
    ids = [r[0] for r in cur.fetchall()]
    if dry_run:
        return len(ids)
    for rid in ids:
        cur.execute(
            """
            UPDATE dynamic_entity_category_relation_t1
            SET deleted=true, updater=%s, update_time=CURRENT_TIMESTAMP
            WHERE id=%s
            """,
            (UPDATER, rid),
        )
    return len(ids)


def ensure_category_link(cur, entity_id: int, category_id: int, dry_run: bool) -> bool:
    if entity_id <= 0 or category_id <= 0:
        return False
    cur.execute(
        """
        SELECT id, deleted FROM dynamic_entity_category_relation_t1
        WHERE tenant_id=%s AND entity_type_code='inspection_item'
          AND entity_id=%s AND category_id=%s
        """,
        (TENANT, entity_id, category_id),
    )
    row = cur.fetchone()
    if row:
        rid, deleted = row
        if deleted and not dry_run:
            cur.execute(
                """
                UPDATE dynamic_entity_category_relation_t1
                SET deleted=false, updater=%s, update_time=CURRENT_TIMESTAMP
                WHERE id=%s
                """,
                (UPDATER, rid),
            )
            return True
        return False
    if dry_run:
        return True
    cur.execute(
        """
        INSERT INTO dynamic_entity_category_relation_t1 (
          entity_id, category_id, entity_type_code, sort,
          creator, deleted, tenant_id
        ) VALUES (%s, %s, 'inspection_item', 0, %s, false, %s)
        """,
        (entity_id, category_id, UPDATER, TENANT),
    )
    return True


def sync_model_applicability(cur, model_id: int, want_item_ids: Sequence[int], dry_run: bool) -> Tuple[int, int]:
    want = {i for i in want_item_ids if i > 0}
    cur.execute(
        """
        SELECT id, entity_id, deleted FROM dynamic_model_entity_relation_t1
        WHERE tenant_id=%s
          AND model_id=%s AND model_entity_type_code='equipment'
          AND entity_type_code='inspection_item'
        """,
        (TENANT, model_id),
    )
    alive: Dict[int, int] = {}
    soft: Dict[int, int] = {}
    for rid, eid, deleted in cur.fetchall():
        if deleted:
            soft[int(eid)] = int(rid)
        else:
            alive[int(eid)] = int(rid)
    add = del_ = 0
    for eid in sorted(want - set(alive)):
        add += 1
        if dry_run:
            continue
        if eid in soft:
            cur.execute(
                """
                UPDATE dynamic_model_entity_relation_t1
                SET deleted=false, updater=%s, update_time=CURRENT_TIMESTAMP
                WHERE id=%s
                """,
                (UPDATER, soft[eid]),
            )
        else:
            cur.execute(
                """
                INSERT INTO dynamic_model_entity_relation_t1 (
                  model_id, model_entity_type_code, entity_id, entity_type_code,
                  domain, sort, creator, deleted, tenant_id
                ) VALUES (%s,'equipment',%s,'inspection_item', NULL, 0, %s, false, %s)
                """,
                (model_id, eid, UPDATER, TENANT),
            )
    for eid, rid in alive.items():
        if eid not in want:
            del_ += 1
            if dry_run:
                continue
            cur.execute(
                """
                UPDATE dynamic_model_entity_relation_t1
                SET deleted=true, updater=%s, update_time=CURRENT_TIMESTAMP
                WHERE id=%s
                """,
                (UPDATER, rid),
            )
    return add, del_


def ensure_inspection_category(cur, name: str, dry_run: bool) -> int:
    cur.execute(
        """
        SELECT id FROM dynamic_category
        WHERE deleted=false AND tenant_id=%s
          AND category_type_code='inspection_item' AND name=%s
        """,
        (TENANT, name),
    )
    row = cur.fetchone()
    if row:
        return int(row[0])
    cur.execute(
        """
        SELECT id, code, tree_path, level FROM dynamic_category
        WHERE deleted=false AND tenant_id=%s
          AND category_type_code='inspection_item' AND name=%s
        """,
        (TENANT, INSPECTION_CAT_PARENT),
    )
    parent = cur.fetchone()
    if not parent:
        raise RuntimeError(f"missing parent inspection category: {INSPECTION_CAT_PARENT}")
    parent_id, parent_code, parent_path, parent_level = parent
    parent_id = int(parent_id)
    parent_level = int(parent_level or 1)
    if dry_run:
        print(f"  would create inspection category {name}")
        return -1
    code = f"CAT-{uuid.uuid4().hex}"
    cur.execute(
        """
        INSERT INTO dynamic_category (
          parent_id, name, code, category_type_code, tree_path, level, sort,
          status, description, creator, tenant_id, parent_code, deleted
        ) VALUES (
          %s, %s, %s, 'inspection_item', NULL, %s, 0,
          1, %s, %s, %s, %s, false
        ) RETURNING id
        """,
        (
            parent_id,
            name,
            code,
            parent_level + 1,
            f"站场检查下：{name}",
            UPDATER,
            TENANT,
            parent_code,
        ),
    )
    new_id = int(cur.fetchone()[0])
    base = (parent_path or f"/{parent_id}/").rstrip("/") + f"/{new_id}/"
    cur.execute("UPDATE dynamic_category SET tree_path=%s WHERE id=%s", (base, new_id))
    print(f"  created inspection category id={new_id} {name}")
    return new_id


def load_l2_id(cur, l2_name: str) -> int:
    cur.execute(
        """
        SELECT id FROM dynamic_category
        WHERE deleted=false AND tenant_id=%s AND category_type_code='equipment'
          AND parent_id=1 AND name=%s
        """,
        (TENANT, l2_name),
    )
    row = cur.fetchone()
    if not row:
        raise RuntimeError(f"missing L2: {l2_name}")
    return int(row[0])


def load_l3_under_l2(cur, l2_id: int) -> List[Tuple[int, str]]:
    cur.execute(
        """
        SELECT id, name FROM dynamic_category
        WHERE deleted=false AND tenant_id=%s AND parent_id=%s
        ORDER BY name
        """,
        (TENANT, l2_id),
    )
    return [(int(r[0]), r[1]) for r in cur.fetchall()]


def load_leaves(cur, root_id: int) -> Dict[str, int]:
    """root 自身无子则视为叶子；否则取其下全部叶子。"""
    cur.execute(
        """
        SELECT 1 FROM dynamic_category
        WHERE parent_id=%s AND deleted=false AND tenant_id=%s
        LIMIT 1
        """,
        (root_id, TENANT),
    )
    if not cur.fetchone():
        cur.execute("SELECT name FROM dynamic_category WHERE id=%s", (root_id,))
        return {cur.fetchone()[0]: root_id}
    cur.execute(
        """
        WITH RECURSIVE t AS (
          SELECT id, name, parent_id FROM dynamic_category
          WHERE id=%s AND deleted=false AND tenant_id=%s
          UNION ALL
          SELECT c.id, c.name, c.parent_id
          FROM dynamic_category c
          JOIN t ON c.parent_id=t.id
          WHERE c.deleted=false AND c.tenant_id=%s
        )
        SELECT id, name FROM t tt
        WHERE tt.id <> %s
          AND NOT EXISTS (
            SELECT 1 FROM dynamic_category ch
            WHERE ch.parent_id=tt.id AND ch.deleted=false AND ch.tenant_id=%s
          )
        """,
        (root_id, TENANT, TENANT, root_id, TENANT),
    )
    return {name: int(cid) for cid, name in cur.fetchall()}


def role_for(l2: str, l3: str, leaf: str) -> Role:
    if leaf in LEAF_ROLE_OVERRIDE:
        return LEAF_ROLE_OVERRIDE[leaf]
    role = L3_ROLE.get((l2, l3))
    if role:
        return role
    return GENERIC


def collect_l3_short_ids(cur, l3_ids: Sequence[int]) -> Set[int]:
    if not l3_ids:
        return set()
    cur.execute(
        """
        SELECT DISTINCT e.id
        FROM dynamic_entity_category_relation_t1 lnk
        JOIN ent_inspection_item_t1 e
          ON e.id=lnk.entity_id AND e.deleted=false AND e.tenant_id=%s
        WHERE lnk.deleted=false AND lnk.tenant_id=%s
          AND lnk.entity_type_code='inspection_item'
          AND lnk.category_id = ANY(%s)
          AND e.name !~ '（.+）$'
        """,
        (TENANT, TENANT, list(l3_ids)),
    )
    return {int(r[0]) for r in cur.fetchall()}


def soft_delete_entities(cur, ids: Sequence[int], dry_run: bool) -> int:
    if not ids:
        return 0
    if dry_run:
        return len(ids)
    for eid in ids:
        cur.execute(
            """
            UPDATE ent_inspection_item_t1
            SET deleted=true, updater=%s, update_time=CURRENT_TIMESTAMP
            WHERE id=%s AND deleted=false
            """,
            (UPDATER, eid),
        )
        cur.execute(
            """
            UPDATE dynamic_entity_category_relation_t1
            SET deleted=true, updater=%s, update_time=CURRENT_TIMESTAMP
            WHERE entity_id=%s AND entity_type_code='inspection_item'
              AND deleted=false AND tenant_id=%s
            """,
            (UPDATER, eid, TENANT),
        )
        cur.execute(
            """
            UPDATE dynamic_model_entity_relation_t1
            SET deleted=true, updater=%s, update_time=CURRENT_TIMESTAMP
            WHERE entity_id=%s AND entity_type_code='inspection_item'
              AND deleted=false AND tenant_id=%s
            """,
            (UPDATER, eid, TENANT),
        )
    return len(ids)


def main() -> int:
    ap = argparse.ArgumentParser()
    ap.add_argument("--dry-run", action="store_true")
    ap.add_argument("--only", nargs="*", default=None, help="只处理指定 L2")
    args = ap.parse_args()
    dry = args.dry_run
    l2_list = args.only or list(L2_INSP_CAT.keys())

    conn = psycopg2.connect(**PKG)
    conn.autocommit = False
    cur = conn.cursor()

    try:
        cur.execute(
            """
            SELECT id FROM dynamic_model
            WHERE deleted=false AND tenant_id=%s AND code=%s
            """,
            (TENANT, INSPECTION_ITEM_MODEL_CODE),
        )
        row = cur.fetchone()
        if not row:
            print("ERROR: inspection_item model missing", file=sys.stderr)
            return 1
        insp_model_id = int(row[0])

        insp_cat_ids: Dict[str, int] = {}
        for l2 in l2_list:
            cat_name = L2_INSP_CAT[l2]
            insp_cat_ids[cat_name] = ensure_inspection_category(cur, cat_name, dry)

        # 预扫描：需要创建的检查项
        plans: List[Tuple[str, str, int, Dict[str, int], str]] = []
        # (l2, l3, l3_id, leaves, insp_cat_name)
        l3_ids_all: List[int] = []
        need_create = 0
        for l2 in l2_list:
            l2_id = load_l2_id(cur, l2)
            insp_name = L2_INSP_CAT[l2]
            for l3_id, l3_name in load_l3_under_l2(cur, l2_id):
                if (l2, l3_name) not in L3_ROLE and l3_name != "未分类设备":
                    # 未配置角色的 L3：仍处理，用 GENERIC
                    pass
                leaves = load_leaves(cur, l3_id)
                if not leaves:
                    continue
                l3_ids_all.append(l3_id)
                for leaf_name in leaves:
                    for base, _mid in role_for(l2, l3_name, leaf_name):
                        name = display_name(base, leaf_name)
                        cur.execute(
                            """
                            SELECT 1 FROM ent_inspection_item_t1
                            WHERE deleted=false AND tenant_id=%s AND name=%s
                            """,
                            (TENANT, name),
                        )
                        if not cur.fetchone():
                            need_create += 1
                plans.append((l2, l3_name, l3_id, leaves, insp_name))

        short_ids = collect_l3_short_ids(cur, l3_ids_all)
        print(f"l2={l2_list} plans={len(plans)} need_create≈{need_create} short_on_l3={len(short_ids)}")

        new_codes = next_numeric_codes(cur, need_create) if need_create else []
        code_iter = iter(new_codes)
        total_created = total_linked = total_model_add = total_model_del = 0

        for l2, l3_name, l3_id, leaves, insp_name in plans:
            insp_cat_id = insp_cat_ids.get(insp_name, -1)
            print(f"\n=== {l2} / {l3_name} ===")
            # L3 自身若也是叶子（未分类设备），不要先清空再找不到；仍清糊挂后重建
            unlinked = soft_unlink_category(cur, l3_id, dry)
            print(f"  L3 unlink: {unlinked}")

            created = linked = 0
            leaf_item_ids: Dict[str, List[int]] = {}
            for leaf_name, leaf_id in leaves.items():
                checks = role_for(l2, l3_name, leaf_name)
                ids: List[int] = []
                for base, method_id in checks:
                    name = display_name(base, leaf_name)
                    cur.execute(
                        """
                        SELECT id FROM ent_inspection_item_t1
                        WHERE deleted=false AND tenant_id=%s AND name=%s
                        """,
                        (TENANT, name),
                    )
                    existing = cur.fetchone()
                    if existing:
                        code = ""
                    else:
                        code = next(code_iter)
                        created += 1
                    iid = ensure_item(
                        cur,
                        model_id=insp_model_id,
                        name=name,
                        method_template_id=method_id,
                        code=code or f"INSP-{random.randint(1000000, 9999999)}",
                        dry_run=dry,
                    )
                    if ensure_category_link(cur, iid, leaf_id, dry):
                        linked += 1
                    if insp_cat_id > 0 and ensure_category_link(cur, iid, insp_cat_id, dry):
                        linked += 1
                    ids.append(iid)
                if dry:
                    leaf_item_ids[leaf_name] = ids
                else:
                    cur.execute(
                        """
                        SELECT entity_id FROM dynamic_entity_category_relation_t1
                        WHERE deleted=false AND tenant_id=%s AND category_id=%s
                          AND entity_type_code='inspection_item'
                        """,
                        (TENANT, leaf_id),
                    )
                    all_ids = [int(r[0]) for r in cur.fetchall()]
                    leaf_item_ids[leaf_name] = all_ids or ids

            print(f"  created={created} links≈{linked}")
            total_created += created
            total_linked += linked

            model_add = model_del = 0
            for leaf_name, item_ids in leaf_item_ids.items():
                leaf_id = leaves[leaf_name]
                cur.execute(
                    """
                    SELECT m.id, m.name
                    FROM dynamic_model m
                    JOIN dynamic_model_category_relation_t1 r
                      ON r.model_id=m.id AND r.deleted=false AND r.category_id=%s
                    WHERE m.deleted=false AND m.tenant_id=%s
                      AND m.entity_type_code='equipment'
                    """,
                    (leaf_id, TENANT),
                )
                for mid, mname in cur.fetchall():
                    a, d = sync_model_applicability(cur, int(mid), item_ids, dry)
                    model_add += a
                    model_del += d
                    if a or d:
                        print(f"  model {mid} {str(mname)[:36]} +{a}/-{d} -> {leaf_name}")
            print(f"  model +{model_add}/-{model_del}")
            total_model_add += model_add
            total_model_del += model_del

            # verify empty leaves after
            for leaf_name, leaf_id in sorted(leaves.items()):
                cur.execute(
                    """
                    SELECT COUNT(*) FROM dynamic_entity_category_relation_t1
                    WHERE deleted=false AND tenant_id=%s AND category_id=%s
                      AND entity_type_code='inspection_item'
                    """,
                    (TENANT, leaf_id),
                )
                n = cur.fetchone()[0]
                if n == 0 and not dry:
                    print(f"  WARN empty leaf {leaf_name}")
                elif dry:
                    pass
                else:
                    print(f"  verify {leaf_name}: {n}")

        print(f"\n=== soft-delete L3 short-name residuals ({len(short_ids)}) ===")
        n_del = soft_delete_entities(cur, sorted(short_ids), dry)
        print(f"  soft_deleted={n_del}")

        print(
            f"\nTOTAL created={total_created} links≈{total_linked} "
            f"model +{total_model_add}/-{total_model_del} short_deleted={n_del}"
        )
        if dry:
            conn.rollback()
            print("DRY-RUN: rolled back")
        else:
            conn.commit()
            print("COMMITTED")
    except Exception:
        conn.rollback()
        raise
    finally:
        cur.close()
        conn.close()
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
