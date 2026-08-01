#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
全量设备型号 × 专业检查包 对齐（tenant=1）。

- 按设备类别定义标准检查内容 + 方法模板
- 关键类（储罐/阀泵/气体/消防/UPS/视频/门禁）加深
- 严格同步：型号关联 = 检查包全集（卸错挂、补缺挂）
- 跳过：测试模型、单独

用法：
  python 09_apply_equipment_inspection_packages.py
  python 09_apply_equipment_inspection_packages.py --dry-run
"""

from __future__ import annotations

import argparse
import sys
from collections import defaultdict
from typing import Dict, List, Optional, Sequence, Tuple

import psycopg2

PKG = {
    "host": "127.0.0.1",
    "dbname": "sinopec",
    "user": "postgres",
    "password": "Coolhomer",
    "options": "-c search_path=dynamicbusiness",
}

SKIP_MODEL_NAMES = {"测试模型", "单独"}

# (code, name, method_template_code)
# 关键类加深；一般类 6～8 条；判据尽量可观察
PACKAGES: Dict[str, List[Tuple[str, str, str]]] = {
    "tank": [
        ("INSP-PKG-TANK-01", "罐体外观完好、无严重锈蚀变形", "IM-TPL-VISUAL-01"),
        ("INSP-PKG-TANK-02", "液位/温度仪表指示正常、无卡滞", "IM-TPL-LEVEL-TEMP-READ"),
        ("INSP-PKG-TANK-03", "呼吸阀与安全附件完好、无卡阻", "IM-TPL-RELIEF-VALVE"),
        ("INSP-PKG-TANK-04", "防火堤完好、排水畅通", "IM-TPL-FIRE-DIKE"),
        ("INSP-PKG-TANK-05", "进出油阀门状态正确、无泄漏", "IM-TPL-VALVE-POSITION"),
        ("INSP-PKG-TANK-06", "阀体法兰及接管无渗漏油迹", "IM-TPL-LEAK-CHECK"),
        ("INSP-PKG-TANK-07", "接地与防雷设施完好、连接可靠", "IM-TPL-GROUNDING-VISUAL"),
        ("INSP-PKG-TANK-08", "罐区通道畅通、无杂物堆积", "IM-TPL-PASSAGE-CLEAR"),
        ("INSP-PKG-TANK-09", "标识标牌清晰完整、与现场一致", "IM-TPL-MARKING-READ"),
        ("INSP-PKG-TANK-10", "保温/防腐层无明显破损脱落", "IM-TPL-VISUAL-01"),
        ("INSP-PKG-TANK-11", "罐顶/盘梯护栏完好、无严重腐蚀", "IM-TPL-VISUAL-01"),
        ("INSP-PKG-TANK-12", "泡沫产生器/消防接口外观完好", "IM-TPL-DETECTOR"),
    ],
    "valve": [
        ("INSP-PKG-VALVE-01", "阀体及法兰无泄漏", "IM-TPL-LEAK-CHECK"),
        ("INSP-PKG-VALVE-02", "阀位指示正确、与工艺状态一致", "IM-TPL-VALVE-POSITION"),
        ("INSP-PKG-VALVE-03", "开关灵活到位、无卡涩异常阻力", "IM-TPL-VALVE-OPERATE"),
        ("INSP-PKG-VALVE-04", "手轮/执行机构完好、限位有效", "IM-TPL-VALVE-OPERATE"),
        ("INSP-PKG-VALVE-05", "支撑与紧固件完好、无松动", "IM-TPL-VISUAL-01"),
        ("INSP-PKG-VALVE-06", "保温/防腐层完好", "IM-TPL-VISUAL-01"),
        ("INSP-PKG-VALVE-07", "阀门标识铭牌清晰、开关方向正确", "IM-TPL-MARKING-READ"),
        ("INSP-PKG-VALVE-08", "填料函/密封处无明显渗漏", "IM-TPL-LEAK-CHECK"),
    ],
    "pump": [
        ("INSP-PKG-PUMP-01", "泵体及基础牢固、无异常振动", "IM-TPL-VIBRATION-LISTEN"),
        ("INSP-PKG-PUMP-02", "运行声音正常、无异响撞击", "IM-TPL-VIBRATION-LISTEN"),
        ("INSP-PKG-PUMP-03", "密封处无泄漏", "IM-TPL-LEAK-CHECK"),
        ("INSP-PKG-PUMP-04", "进出口压力正常、在合理量程", "IM-TPL-PRESSURE-READ"),
        ("INSP-PKG-PUMP-05", "润滑油位正常、油质无明显乳化", "IM-TPL-LUBE-LEVEL"),
        ("INSP-PKG-PUMP-06", "电机温度正常、接线紧固", "IM-TPL-MOTOR-TEMP"),
        ("INSP-PKG-PUMP-07", "联轴器防护罩完好、紧固可靠", "IM-TPL-VISUAL-01"),
        ("INSP-PKG-PUMP-08", "接地可靠、电缆外护套无破损", "IM-TPL-GROUNDING-VISUAL"),
        ("INSP-PKG-PUMP-09", "启停控制正常、就地/远控一致", "IM-TPL-ELECTRICAL-PANEL"),
        ("INSP-PKG-PUMP-10", "泵区通道畅通、标识清晰", "IM-TPL-PASSAGE-CLEAR"),
    ],
    "camera": [
        ("INSP-PKG-CAM-01", "镜头清洁、无遮挡污染", "IM-TPL-CAM-LENS"),
        ("INSP-PKG-CAM-02", "视频画面正常、无花屏黑屏", "IM-TPL-CAM-IMAGE"),
        ("INSP-PKG-CAM-03", "云台/变焦动作正常（适用机型）", "IM-TPL-CAM-PTZ"),
        ("INSP-PKG-CAM-04", "补光灯/红外工作正常", "IM-TPL-CAM-IR"),
        ("INSP-PKG-CAM-05", "时间OSD正确、与标准时一致", "IM-TPL-CAM-OSD"),
        ("INSP-PKG-CAM-06", "设备外观完好、支架紧固", "IM-TPL-VISUAL-01"),
        ("INSP-PKG-CAM-07", "网络链路正常、无持续掉线", "IM-TPL-NETWORK-LINK"),
        ("INSP-PKG-CAM-08", "安装位置合理、监视区域无遮挡", "IM-TPL-MARKING-READ"),
    ],
    "access": [
        ("INSP-PKG-ACC-01", "刷卡/识别正常、权限匹配", "IM-TPL-ACCESS-CARD"),
        ("INSP-PKG-ACC-02", "门锁开合正常、闭锁可靠", "IM-TPL-DOOR-LOCK"),
        ("INSP-PKG-ACC-03", "门磁与出门按钮正常", "IM-TPL-DOOR-LOCK"),
        ("INSP-PKG-ACC-04", "读卡器外观完好、指示正常", "IM-TPL-INDICATOR-LIGHT"),
        ("INSP-PKG-ACC-05", "控制器通讯状态正常", "IM-TPL-LINK-COMM"),
        ("INSP-PKG-ACC-06", "供电指示正常、备用电源可用", "IM-TPL-POWER-SELFTEST"),
        ("INSP-PKG-ACC-07", "设备外观完好、安装牢固", "IM-TPL-VISUAL-01"),
        ("INSP-PKG-ACC-08", "门禁事件可查询、无持续通讯故障", "IM-TPL-SOFTWARE-LOGIN"),
    ],
    "intrusion": [
        ("INSP-PKG-ALM-01", "主机上电与自检正常", "IM-TPL-POWER-SELFTEST"),
        ("INSP-PKG-ALM-02", "防区状态正常、无异常报警", "IM-TPL-ALARM-ZONE"),
        ("INSP-PKG-ALM-03", "键盘操作正常、布撤防有效", "IM-TPL-INDICATOR-LIGHT"),
        ("INSP-PKG-ALM-04", "声光触发正常、可复位", "IM-TPL-SOUND-LIGHT"),
        ("INSP-PKG-ALM-05", "通讯与备用电源正常", "IM-TPL-BATTERY-STATUS"),
        ("INSP-PKG-ALM-06", "探测器指示正常、探测区无遮挡", "IM-TPL-DETECTOR"),
        ("INSP-PKG-ALM-07", "与上位联动/上传正常", "IM-TPL-LINK-COMM"),
        ("INSP-PKG-ALM-08", "机箱外观完好、接线整齐标识清晰", "IM-TPL-VISUAL-01"),
    ],
    "gas_o2": [
        ("INSP-PKG-O2-01", "探头清洁、无遮挡污染", "IM-TPL-DETECTOR"),
        ("INSP-PKG-O2-02", "氧气读数合理、无持续异常", "IM-TPL-GAUGE-READ"),
        ("INSP-PKG-O2-03", "指示灯与显示正常", "IM-TPL-INDICATOR-LIGHT"),
        ("INSP-PKG-O2-04", "报警阈值与联动正常", "IM-TPL-SOUND-LIGHT"),
        ("INSP-PKG-O2-05", "接线密封完好、无松动", "IM-TPL-CABLE-JOINT"),
        ("INSP-PKG-O2-06", "通讯/报警输出正常", "IM-TPL-LINK-COMM"),
        ("INSP-PKG-O2-07", "外观完好、安装牢固、标识清晰", "IM-TPL-MARKING-READ"),
    ],
    "gas_co2": [
        ("INSP-PKG-CO2-01", "探头清洁、无遮挡污染", "IM-TPL-DETECTOR"),
        ("INSP-PKG-CO2-02", "二氧化碳读数合理、无持续异常", "IM-TPL-GAUGE-READ"),
        ("INSP-PKG-CO2-03", "指示灯与显示正常", "IM-TPL-INDICATOR-LIGHT"),
        ("INSP-PKG-CO2-04", "报警阈值与联动正常", "IM-TPL-SOUND-LIGHT"),
        ("INSP-PKG-CO2-05", "接线密封完好、无松动", "IM-TPL-CABLE-JOINT"),
        ("INSP-PKG-CO2-06", "通讯/报警输出正常", "IM-TPL-LINK-COMM"),
        ("INSP-PKG-CO2-07", "外观完好、安装牢固、标识清晰", "IM-TPL-MARKING-READ"),
    ],
    "gas_lel": [
        ("INSP-PKG-LEL-01", "探头清洁、无遮挡凝露损坏", "IM-TPL-DETECTOR"),
        ("INSP-PKG-LEL-02", "可燃气体读数合理、无持续异常", "IM-TPL-GAUGE-READ"),
        ("INSP-PKG-LEL-03", "指示灯与显示正常", "IM-TPL-INDICATOR-LIGHT"),
        ("INSP-PKG-LEL-04", "报警阈值与声光/联锁正常", "IM-TPL-SOUND-LIGHT"),
        ("INSP-PKG-LEL-05", "接线密封完好、防爆件无损伤", "IM-TPL-CABLE-JOINT"),
        ("INSP-PKG-LEL-06", "通讯/报警输出正常", "IM-TPL-LINK-COMM"),
        ("INSP-PKG-LEL-07", "外观完好、安装牢固、标识清晰", "IM-TPL-MARKING-READ"),
        ("INSP-PKG-LEL-08", "探测区域无遮挡、周边无可燃堆积", "IM-TPL-PASSAGE-CLEAR"),
    ],
    "th_sensor": [
        ("INSP-PKG-TH-01", "探头清洁、无凝露损坏", "IM-TPL-DETECTOR"),
        ("INSP-PKG-TH-02", "温湿度读数合理", "IM-TPL-LEVEL-TEMP-READ"),
        ("INSP-PKG-TH-03", "通讯正常", "IM-TPL-LINK-COMM"),
        ("INSP-PKG-TH-04", "外观完好、安装牢固", "IM-TPL-VISUAL-01"),
        ("INSP-PKG-TH-05", "报警/阈值设置有效（如有）", "IM-TPL-SOUND-LIGHT"),
        ("INSP-PKG-TH-06", "接线可靠、标识清晰", "IM-TPL-CABLE-JOINT"),
    ],
    "ups": [
        ("INSP-PKG-UPS-01", "主机上电与面板指示正常", "IM-TPL-POWER-SELFTEST"),
        ("INSP-PKG-UPS-02", "市电/电池切换正常", "IM-TPL-BATTERY-STATUS"),
        ("INSP-PKG-UPS-03", "电池电压与容量正常、无鼓包漏液", "IM-TPL-BATTERY-STATUS"),
        ("INSP-PKG-UPS-04", "风扇运行正常、无异响过热", "IM-TPL-COOLING-FAN"),
        ("INSP-PKG-UPS-05", "负载指示正常、无过载告警", "IM-TPL-INDICATOR-LIGHT"),
        ("INSP-PKG-UPS-06", "输入输出接线紧固、无过热变色", "IM-TPL-CABLE-JOINT"),
        ("INSP-PKG-UPS-07", "电池柜通风散热正常、通道畅通", "IM-TPL-PASSAGE-CLEAR"),
        ("INSP-PKG-UPS-08", "监控通讯/告警上传正常", "IM-TPL-LINK-COMM"),
        ("INSP-PKG-UPS-09", "标识清晰、旁路/维修开关状态正确", "IM-TPL-MARKING-READ"),
    ],
    "network": [
        ("INSP-PKG-NET-01", "上电与指示灯正常", "IM-TPL-POWER-SELFTEST"),
        ("INSP-PKG-NET-02", "关键端口链路正常、无持续抖动", "IM-TPL-NETWORK-LINK"),
        ("INSP-PKG-NET-03", "上下行链路/光口指示正常", "IM-TPL-NETWORK-LINK"),
        ("INSP-PKG-NET-04", "风扇与温度正常", "IM-TPL-COOLING-FAN"),
        ("INSP-PKG-NET-05", "无异常告警、日志无持续严重错误", "IM-TPL-SOFTWARE-LOGIN"),
        ("INSP-PKG-NET-06", "机箱外观完好、接地可靠", "IM-TPL-GROUNDING-VISUAL"),
        ("INSP-PKG-NET-07", "线缆标识清晰、走线规范", "IM-TPL-MARKING-READ"),
        ("INSP-PKG-NET-08", "管理地址可登录（适用设备）", "IM-TPL-SOFTWARE-LOGIN"),
    ],
    "fiber": [
        ("INSP-PKG-FIB-01", "光/电口指示正常", "IM-TPL-NETWORK-LINK"),
        ("INSP-PKG-FIB-02", "光纤弯曲半径与接头完好", "IM-TPL-NETWORK-LINK"),
        ("INSP-PKG-FIB-03", "链路通讯正常", "IM-TPL-LINK-COMM"),
        ("INSP-PKG-FIB-04", "外观完好、安装牢固", "IM-TPL-VISUAL-01"),
        ("INSP-PKG-FIB-05", "光功率/链路衰减在合理范围", "IM-TPL-NETWORK-LINK"),
        ("INSP-PKG-FIB-06", "标识清晰", "IM-TPL-MARKING-READ"),
    ],
    "voip": [
        ("INSP-PKG-VOIP-01", "主机上电与指示正常", "IM-TPL-POWER-SELFTEST"),
        ("INSP-PKG-VOIP-02", "网络链路正常", "IM-TPL-NETWORK-LINK"),
        ("INSP-PKG-VOIP-03", "试呼通话清晰、无杂音断续", "IM-TPL-INTERCOM"),
        ("INSP-PKG-VOIP-04", "按键与显示正常", "IM-TPL-INDICATOR-LIGHT"),
        ("INSP-PKG-VOIP-05", "分机/SIP注册正常（适用）", "IM-TPL-LINK-COMM"),
        ("INSP-PKG-VOIP-06", "外观完好、安装牢固", "IM-TPL-VISUAL-01"),
        ("INSP-PKG-VOIP-07", "业务功能抽检正常", "IM-TPL-SOFTWARE-LOGIN"),
    ],
    "radio": [
        ("INSP-PKG-RF-01", "上电与指示正常", "IM-TPL-POWER-SELFTEST"),
        ("INSP-PKG-RF-02", "信道/组呼或中继转发正常", "IM-TPL-RADIO-RF"),
        ("INSP-PKG-RF-03", "天线与馈线完好、接头无进水", "IM-TPL-RADIO-RF"),
        ("INSP-PKG-RF-04", "驻波/链路正常", "IM-TPL-RADIO-RF"),
        ("INSP-PKG-RF-05", "音频收发正常", "IM-TPL-INTERCOM"),
        ("INSP-PKG-RF-06", "外观完好、安装牢固、接地可靠", "IM-TPL-GROUNDING-VISUAL"),
        ("INSP-PKG-RF-07", "网管/配置可访问（适用）", "IM-TPL-SOFTWARE-LOGIN"),
    ],
    "antenna": [
        ("INSP-PKG-ANT-01", "天线外观完好、安装牢固", "IM-TPL-VISUAL-01"),
        ("INSP-PKG-ANT-02", "馈线接头紧固、无进水", "IM-TPL-RADIO-RF"),
        ("INSP-PKG-ANT-03", "驻波/链路正常", "IM-TPL-RADIO-RF"),
        ("INSP-PKG-ANT-04", "接地与防雷完好", "IM-TPL-GROUNDING-VISUAL"),
        ("INSP-PKG-ANT-05", "标识清晰", "IM-TPL-MARKING-READ"),
        ("INSP-PKG-ANT-06", "周边无遮挡影响覆盖", "IM-TPL-PASSAGE-CLEAR"),
    ],
    "rf_passive": [
        ("INSP-PKG-RFP-01", "外观完好、安装牢固", "IM-TPL-VISUAL-01"),
        ("INSP-PKG-RFP-02", "接口/接头紧固、无松动", "IM-TPL-CABLE-JOINT"),
        ("INSP-PKG-RFP-03", "链路衰减/隔离/耦合指示正常", "IM-TPL-RADIO-RF"),
        ("INSP-PKG-RFP-04", "标识清晰", "IM-TPL-MARKING-READ"),
        ("INSP-PKG-RFP-05", "接地可靠（适用）", "IM-TPL-GROUNDING-VISUAL"),
    ],
    "positioning": [
        ("INSP-PKG-POS-01", "主机上电与指示正常", "IM-TPL-POWER-SELFTEST"),
        ("INSP-PKG-POS-02", "读写/定位功能正常", "IM-TPL-ACCESS-CARD"),
        ("INSP-PKG-POS-03", "网络通讯正常", "IM-TPL-NETWORK-LINK"),
        ("INSP-PKG-POS-04", "天线/支架安装牢固", "IM-TPL-VISUAL-01"),
        ("INSP-PKG-POS-05", "定位上报/后台可见", "IM-TPL-LINK-COMM"),
        ("INSP-PKG-POS-06", "供电适配可靠、标识清晰", "IM-TPL-MARKING-READ"),
    ],
    "pos_terminal": [
        ("INSP-PKG-POT-01", "开机与电量正常", "IM-TPL-BATTERY-STATUS"),
        ("INSP-PKG-POT-02", "定位/上报正常", "IM-TPL-LINK-COMM"),
        ("INSP-PKG-POT-03", "按键与指示正常", "IM-TPL-INDICATOR-LIGHT"),
        ("INSP-PKG-POT-04", "外观完好、佩戴/安装可靠", "IM-TPL-VISUAL-01"),
        ("INSP-PKG-POT-05", "通讯链路正常", "IM-TPL-NETWORK-LINK"),
    ],
    "fire_aerosol": [
        ("INSP-PKG-AER-01", "装置外观完好、安装牢固", "IM-TPL-VISUAL-01"),
        ("INSP-PKG-AER-02", "喷嘴无堵塞、保护盖完好", "IM-TPL-DETECTOR"),
        ("INSP-PKG-AER-03", "周边无可燃杂物、通道畅通", "IM-TPL-PASSAGE-CLEAR"),
        ("INSP-PKG-AER-04", "控制线路/反馈指示正常", "IM-TPL-INDICATOR-LIGHT"),
        ("INSP-PKG-AER-05", "标识铭牌清晰、有效期可辨", "IM-TPL-MARKING-READ"),
        ("INSP-PKG-AER-06", "与主机联动状态正常", "IM-TPL-LINK-COMM"),
        ("INSP-PKG-AER-07", "保护区开口/电缆孔洞封堵完好", "IM-TPL-VISUAL-01"),
    ],
    "emergency_light": [
        ("INSP-PKG-EML-01", "外观完好、标识清晰", "IM-TPL-MARKING-READ"),
        ("INSP-PKG-EML-02", "点亮与应急切换正常", "IM-TPL-EMERGENCY-LIGHT"),
        ("INSP-PKG-EML-03", "电池状态正常", "IM-TPL-BATTERY-STATUS"),
        ("INSP-PKG-EML-04", "安装牢固", "IM-TPL-VISUAL-01"),
        ("INSP-PKG-EML-05", "疏散指示方向正确、无遮挡", "IM-TPL-PASSAGE-CLEAR"),
        ("INSP-PKG-EML-06", "接线可靠", "IM-TPL-CABLE-JOINT"),
    ],
    "fire_general": [
        ("INSP-PKG-FIR-01", "设施外观完好、安装牢固", "IM-TPL-VISUAL-01"),
        ("INSP-PKG-FIR-02", "压力/状态指示正常", "IM-TPL-PRESSURE-READ"),
        ("INSP-PKG-FIR-03", "阀门开关正确、无泄漏", "IM-TPL-VALVE-POSITION"),
        ("INSP-PKG-FIR-04", "周边通道畅通、无杂物", "IM-TPL-PASSAGE-CLEAR"),
        ("INSP-PKG-FIR-05", "标识清晰完整", "IM-TPL-MARKING-READ"),
        ("INSP-PKG-FIR-06", "接地/固定可靠", "IM-TPL-GROUNDING-VISUAL"),
        ("INSP-PKG-FIR-07", "联动/反馈信号正常（适用）", "IM-TPL-LINK-COMM"),
    ],
    "server": [
        ("INSP-PKG-SRV-01", "上电与指示灯正常", "IM-TPL-POWER-SELFTEST"),
        ("INSP-PKG-SRV-02", "风扇与温度正常", "IM-TPL-COOLING-FAN"),
        ("INSP-PKG-SRV-03", "磁盘/RAID或存储状态正常", "IM-TPL-SOFTWARE-LOGIN"),
        ("INSP-PKG-SRV-04", "关键服务进程正常", "IM-TPL-SOFTWARE-LOGIN"),
        ("INSP-PKG-SRV-05", "远程管理可登录", "IM-TPL-SOFTWARE-LOGIN"),
        ("INSP-PKG-SRV-06", "无硬件/严重告警", "IM-TPL-ALARM-ZONE"),
        ("INSP-PKG-SRV-07", "网口链路正常", "IM-TPL-NETWORK-LINK"),
        ("INSP-PKG-SRV-08", "机架安装牢固、线缆规范", "IM-TPL-VISUAL-01"),
    ],
    "workstation": [
        ("INSP-PKG-WS-01", "开机与显示正常", "IM-TPL-POWER-SELFTEST"),
        ("INSP-PKG-WS-02", "网络连接正常", "IM-TPL-NETWORK-LINK"),
        ("INSP-PKG-WS-03", "业务软件/客户端可登录", "IM-TPL-SOFTWARE-LOGIN"),
        ("INSP-PKG-WS-04", "键鼠外设正常", "IM-TPL-SOFTWARE-LOGIN"),
        ("INSP-PKG-WS-05", "风扇散热正常、无异常噪音", "IM-TPL-COOLING-FAN"),
        ("INSP-PKG-WS-06", "外观完好、线缆整齐", "IM-TPL-VISUAL-01"),
    ],
    "led": [
        ("INSP-PKG-LED-01", "画面显示正常、无花屏黑屏", "IM-TPL-CAM-IMAGE"),
        ("INSP-PKG-LED-02", "亮度与色彩正常", "IM-TPL-CAM-IMAGE"),
        ("INSP-PKG-LED-03", "控制端通讯正常", "IM-TPL-LINK-COMM"),
        ("INSP-PKG-LED-04", "模组与接线牢固", "IM-TPL-CABLE-JOINT"),
        ("INSP-PKG-LED-05", "结构安装牢固、接地可靠", "IM-TPL-GROUNDING-VISUAL"),
        ("INSP-PKG-LED-06", "散热通风正常", "IM-TPL-COOLING-FAN"),
    ],
    "decoder": [
        ("INSP-PKG-DEC-01", "上电指示正常", "IM-TPL-POWER-SELFTEST"),
        ("INSP-PKG-DEC-02", "解码画面正常", "IM-TPL-CAM-IMAGE"),
        ("INSP-PKG-DEC-03", "切换各端口画面正常", "IM-TPL-CAM-IMAGE"),
        ("INSP-PKG-DEC-04", "网络链路正常", "IM-TPL-NETWORK-LINK"),
        ("INSP-PKG-DEC-05", "控制软件可操作", "IM-TPL-SOFTWARE-LOGIN"),
        ("INSP-PKG-DEC-06", "外观完好、接线牢固", "IM-TPL-CABLE-JOINT"),
    ],
    "kvm": [
        ("INSP-PKG-KVM-01", "显示器点亮正常", "IM-TPL-INDICATOR-LIGHT"),
        ("INSP-PKG-KVM-02", "键盘抽拉/操作灵活", "IM-TPL-SOFTWARE-LOGIN"),
        ("INSP-PKG-KVM-03", "切换控制正常", "IM-TPL-ELECTRICAL-PANEL"),
        ("INSP-PKG-KVM-04", "安装牢固、线缆整齐", "IM-TPL-VISUAL-01"),
        ("INSP-PKG-KVM-05", "各端口画面/键鼠可用", "IM-TPL-CAM-IMAGE"),
    ],
    "electrical": [
        ("INSP-PKG-ELC-01", "柜体外观完好、密封良好", "IM-TPL-VISUAL-01"),
        ("INSP-PKG-ELC-02", "指示灯/面板状态正常", "IM-TPL-INDICATOR-LIGHT"),
        ("INSP-PKG-ELC-03", "开关/接触器状态正确", "IM-TPL-ELECTRICAL-PANEL"),
        ("INSP-PKG-ELC-04", "按钮动作可靠、复位正常", "IM-TPL-ELECTRICAL-PANEL"),
        ("INSP-PKG-ELC-05", "接线紧固、无过热变色", "IM-TPL-CABLE-JOINT"),
        ("INSP-PKG-ELC-06", "内部元器件无过热异味", "IM-TPL-MOTOR-TEMP"),
        ("INSP-PKG-ELC-07", "标识清晰、图纸与现场一致", "IM-TPL-MARKING-READ"),
        ("INSP-PKG-ELC-08", "接地可靠、柜门开合正常", "IM-TPL-GROUNDING-VISUAL"),
    ],
    "pressure_gauge": [
        ("INSP-PKG-PG-01", "表盘清晰、指针无卡滞", "IM-TPL-GAUGE-READ"),
        ("INSP-PKG-PG-02", "读数在合理量程内", "IM-TPL-GAUGE-READ"),
        ("INSP-PKG-PG-03", "表阀开关正确、无泄漏", "IM-TPL-VALVE-POSITION"),
        ("INSP-PKG-PG-04", "取压管路完好、无堵塞", "IM-TPL-PRESSURE-READ"),
        ("INSP-PKG-PG-05", "安装牢固、标识清晰", "IM-TPL-MARKING-READ"),
        ("INSP-PKG-PG-06", "保温/防护完好（适用）", "IM-TPL-VISUAL-01"),
    ],
    "cable": [
        ("INSP-PKG-CAB-01", "外护套完好、无破损老化", "IM-TPL-CABLE-JOINT"),
        ("INSP-PKG-CAB-02", "固定牢固、弯曲半径合格", "IM-TPL-VISUAL-01"),
        ("INSP-PKG-CAB-03", "接头密封良好、无发热", "IM-TPL-CABLE-JOINT"),
        ("INSP-PKG-CAB-04", "桥架/沟槽盖板完好", "IM-TPL-VISUAL-01"),
        ("INSP-PKG-CAB-05", "标识清晰", "IM-TPL-MARKING-READ"),
        ("INSP-PKG-CAB-06", "绝缘/接地状态正常（适用）", "IM-TPL-GROUNDING-VISUAL"),
    ],
    "env_fan": [
        ("INSP-PKG-FAN-01", "风扇运转声音正常、无异响", "IM-TPL-COOLING-FAN"),
        ("INSP-PKG-FAN-02", "叶片与防护网完好", "IM-TPL-COOLING-FAN"),
        ("INSP-PKG-FAN-03", "振动与紧固正常", "IM-TPL-VIBRATION-LISTEN"),
        ("INSP-PKG-FAN-04", "启停控制正常", "IM-TPL-ELECTRICAL-PANEL"),
        ("INSP-PKG-FAN-05", "进出风口畅通", "IM-TPL-PASSAGE-CLEAR"),
        ("INSP-PKG-FAN-06", "状态采集/联动正常（适用）", "IM-TPL-LINK-COMM"),
    ],
    "env_leak": [
        ("INSP-PKG-LEK-01", "感应绳/探头完好、无移位", "IM-TPL-DETECTOR"),
        ("INSP-PKG-LEK-02", "主机指示正常", "IM-TPL-INDICATOR-LIGHT"),
        ("INSP-PKG-LEK-03", "模拟告警/复位正常", "IM-TPL-SOUND-LIGHT"),
        ("INSP-PKG-LEK-04", "通讯正常", "IM-TPL-LINK-COMM"),
        ("INSP-PKG-LEK-05", "安装牢固、标识清晰", "IM-TPL-MARKING-READ"),
    ],
    "env_ac": [
        ("INSP-PKG-AC-01", "空调运行状态采集正常", "IM-TPL-COOLING-FAN"),
        ("INSP-PKG-AC-02", "温湿度联动合理", "IM-TPL-LEVEL-TEMP-READ"),
        ("INSP-PKG-AC-03", "通讯正常", "IM-TPL-LINK-COMM"),
        ("INSP-PKG-AC-04", "上电指示正常", "IM-TPL-POWER-SELFTEST"),
        ("INSP-PKG-AC-05", "外观完好、滤网/风口无明显堵塞", "IM-TPL-VISUAL-01"),
    ],
    "env_host": [
        ("INSP-PKG-ENV-01", "主机上电与自检正常", "IM-TPL-POWER-SELFTEST"),
        ("INSP-PKG-ENV-02", "各监测通道正常", "IM-TPL-GAUGE-READ"),
        ("INSP-PKG-ENV-03", "告警与复位正常", "IM-TPL-SOUND-LIGHT"),
        ("INSP-PKG-ENV-04", "通讯/上传正常", "IM-TPL-LINK-COMM"),
        ("INSP-PKG-ENV-05", "管理界面可登录", "IM-TPL-SOFTWARE-LOGIN"),
        ("INSP-PKG-ENV-06", "外观完好、接线规范", "IM-TPL-VISUAL-01"),
    ],
    "printer": [
        ("INSP-PKG-PRT-01", "开机自检正常", "IM-TPL-POWER-SELFTEST"),
        ("INSP-PKG-PRT-02", "网络/USB连接正常", "IM-TPL-NETWORK-LINK"),
        ("INSP-PKG-PRT-03", "打印测试页正常", "IM-TPL-SOFTWARE-LOGIN"),
        ("INSP-PKG-PRT-04", "墨粉/纸张状态正常", "IM-TPL-SOFTWARE-LOGIN"),
        ("INSP-PKG-PRT-05", "外观完好、无卡纸异响", "IM-TPL-VISUAL-01"),
    ],
    "cabinet": [
        ("INSP-PKG-RACK-01", "柜体完好、门锁开合正常", "IM-TPL-DOOR-LOCK"),
        ("INSP-PKG-RACK-02", "柜内设备上电指示正常", "IM-TPL-POWER-SELFTEST"),
        ("INSP-PKG-RACK-03", "散热风扇/通风正常", "IM-TPL-COOLING-FAN"),
        ("INSP-PKG-RACK-04", "线缆绑扎规范、标识清晰", "IM-TPL-MARKING-READ"),
        ("INSP-PKG-RACK-05", "接地可靠、无过热异味", "IM-TPL-GROUNDING-VISUAL"),
        ("INSP-PKG-RACK-06", "通道畅通、承重与水平正常", "IM-TPL-PASSAGE-CLEAR"),
    ],
    "lighting": [
        ("INSP-PKG-LGT-01", "点亮正常、无闪烁", "IM-TPL-EMERGENCY-LIGHT"),
        ("INSP-PKG-LGT-02", "开关控制正常", "IM-TPL-ELECTRICAL-PANEL"),
        ("INSP-PKG-LGT-03", "灯具外观完好、安装牢固", "IM-TPL-VISUAL-01"),
        ("INSP-PKG-LGT-04", "接线可靠", "IM-TPL-CABLE-JOINT"),
        ("INSP-PKG-LGT-05", "照度区域无严重遮挡", "IM-TPL-PASSAGE-CLEAR"),
    ],
    "storage_disk": [
        ("INSP-PKG-DISK-01", "上电指示正常", "IM-TPL-POWER-SELFTEST"),
        ("INSP-PKG-DISK-02", "SMART/健康状态正常", "IM-TPL-SOFTWARE-LOGIN"),
        ("INSP-PKG-DISK-03", "读写无异常告警", "IM-TPL-SOFTWARE-LOGIN"),
        ("INSP-PKG-DISK-04", "安装牢固、振动正常", "IM-TPL-VISUAL-01"),
        ("INSP-PKG-DISK-05", "所属存储服务状态正常", "IM-TPL-SOFTWARE-LOGIN"),
    ],
    "software": [
        ("INSP-PKG-SW-01", "服务可登录访问", "IM-TPL-SOFTWARE-LOGIN"),
        ("INSP-PKG-SW-02", "关键功能可用", "IM-TPL-SOFTWARE-LOGIN"),
        ("INSP-PKG-SW-03", "授权/许可有效", "IM-TPL-SOFTWARE-LOGIN"),
        ("INSP-PKG-SW-04", "日志无持续严重错误", "IM-TPL-SOFTWARE-LOGIN"),
        ("INSP-PKG-SW-05", "备份/告警策略正常", "IM-TPL-SOFTWARE-LOGIN"),
        ("INSP-PKG-SW-06", "与关联设备通讯正常", "IM-TPL-LINK-COMM"),
    ],
    "meter": [
        ("INSP-PKG-MTR-01", "显示读数正常", "IM-TPL-GAUGE-READ"),
        ("INSP-PKG-MTR-02", "通讯正常", "IM-TPL-LINK-COMM"),
        ("INSP-PKG-MTR-03", "接线紧固、标识清晰", "IM-TPL-CABLE-JOINT"),
        ("INSP-PKG-MTR-04", "外观完好、安装牢固", "IM-TPL-VISUAL-01"),
        ("INSP-PKG-MTR-05", "量程/倍率设置正确", "IM-TPL-MARKING-READ"),
    ],
    "generic": [
        ("INSP-PKG-GEN-01", "外观完好、安装牢固", "IM-TPL-VISUAL-01"),
        ("INSP-PKG-GEN-02", "上电/指示正常（适用）", "IM-TPL-POWER-SELFTEST"),
        ("INSP-PKG-GEN-03", "功能抽检正常", "IM-TPL-VISUAL-01"),
        ("INSP-PKG-GEN-04", "通讯/链路正常（适用）", "IM-TPL-LINK-COMM"),
        ("INSP-PKG-GEN-05", "标识清晰", "IM-TPL-MARKING-READ"),
        ("INSP-PKG-GEN-06", "周边通道畅通、无异常温升", "IM-TPL-PASSAGE-CLEAR"),
    ],
}


# 位置/场景分类：不得单独决定设备专业类
_LOCATION_CAT_FRAGMENTS = (
    "储罐区",
    "阀组区",
    "办公楼",
    "机房",
    "高后果区",
    "重要设备",
    "综合安防",
    "生产区",
)


def _match(text: str, keys: Sequence[str]) -> bool:
    return any(k in text for k in keys)


def classify(model_name: str, cats: Sequence[str]) -> str:
    """以型号名称为第一优先；分类仅作补充，并忽略位置类标签。"""
    n = model_name or ""
    type_cats = [
        c
        for c in (cats or [])
        if c and not any(frag in c for frag in _LOCATION_CAT_FRAGMENTS)
    ]
    ctext = ",".join(type_cats)

    # 1) 名称硬规则（顺序即优先级）
    name_rules: List[Tuple[str, Sequence[str]]] = [
        ("tank", ["原油储罐"]),
        ("valve", ["阀门", "DN400"]),
        ("pump", ["消防泵", "给水泵", "水泵"]),
        ("camera", ["摄像机", "IPC-", "IPC_", "半球摄像"]),
        ("software", ["管理软件", "系统软件", "报警软件", "OMTApp", "SW-MA"]),
        ("access", ["门禁控制器", "门禁系统", "双门门禁", "单门门禁", "门禁"]),
        ("intrusion", ["报警主机", "报警器控制", "声光报警", "红外入侵", "EP8100", "ED690"]),
        ("gas_o2", ["氧气传感器", "氧气检测", "氧气数据", "XM-QT-O2"]),
        ("gas_co2", ["二氧化碳", "XM-QT-CO2"]),
        ("gas_lel", ["可燃气体"]),
        ("th_sensor", ["温湿度", "温度传感器", "湿度传感器", "XM-310", "SOFTPX-T/H"]),
        ("env_fan", ["轴流风机", "SOFTPX-FAN"]),
        ("env_leak", ["SOFTPX-LEAK", "漏水"]),
        ("env_ac", ["SOFTPX-KT", "空调监控"]),
        ("env_host", ["SOFTPX-SMS", "SOFTPX-V1.2", "SOFTPX-UPS", "动环监控主机", "动环短信", "航嘉 S-400"]),
        ("ups", ["UPS不间断", "UPS电源", "科士达", "理士", "YMK3330", "蓄电池"]),
        ("emergency_light", ["应急灯"]),
        ("fire_aerosol", ["气溶胶"]),
        ("fire_general", ["消防"]),
        ("network", ["交换机", "环网", "S7800", "S4820", "KIEN", "FSG10000"]),
        ("fiber", ["光纤收发", "光模块", "慧谷"]),
        ("voip", ["IP电话", "语音网关", "朗视", "畅电", "CDU-HOS", "TE200", "S1000"]),
        ("radio", ["SLR 5300", "XIR C1200", "对讲中继", "对讲手持", "对讲远端"]),
        ("antenna", ["定向天线", "对讲天线", "RHET-PA-N2", "RF50-5"]),
        ("rf_passive", ["功分器", "耦合器", "双工器", "RHET-BL", "RHET-SG", "RHET-PA-GF", "RHET-PA-OH"]),
        ("positioning", ["人员定位", "IAP2600", "定位读写", "电源适配器 · 三旺", "支架 · 三旺"]),
        ("pos_terminal", ["移动终端"]),
        ("led", ["LED显示", "全彩", "山西高科", "MW7218"]),
        ("decoder", ["解码器", "拼接控制", "发送卡", "DMC8000"]),
        ("kvm", ["KVM", "AS-9108", "AS-9100"]),
        ("electrical", ["ACU柜", "ACU", "按钮箱", "控制箱", "检修箱", "精密配电", "监控控制柜"]),
        ("pressure_gauge", ["压力表"]),
        ("cable", ["220V电缆", "电缆接头"]),
        ("printer", ["惠普", "HP7720", "HP-M1136", "打印机"]),
        ("cabinet", ["图腾", "机柜"]),
        ("lighting", ["照明灯"]),
        ("storage_disk", ["硬盘", "ST6000"]),
        ("server", ["PowerEdge", "R540", "VS-MS8800", "VS_VM5800", "VMS-B230", "IAC8500", "航嘉 SPWEB", "服务器"]),
        ("workstation", ["工作站", "optiplex", "便携式计算机", "管理终端", "DELL"]),
        ("meter", ["电量仪", "电流互感", "雅达 ET903"]),
        ("camera", ["视频监控"]),
    ]
    for pkg, keys in name_rules:
        if _match(n, keys):
            return pkg

    # 2) 类型分类补充（已剔除位置标签）
    cat_rules: List[Tuple[str, Sequence[str]]] = [
        ("tank", ["原油储罐"]),
        ("valve", ["闸阀", "自控阀门"]),
        ("pump", ["给水泵"]),
        ("camera", ["枪型摄像机", "半球摄像机"]),
        ("access", ["门禁控制器", "门禁"]),
        ("intrusion", ["入侵报警"]),
        ("gas_o2", ["氧气传感器"]),
        ("gas_co2", ["二氧化碳传感器"]),
        ("th_sensor", ["温湿度传感器"]),
        ("network", ["环网交换机", "核心交换机", "接入交换机"]),
        ("voip", ["IP电话"]),
        ("radio", ["对讲"]),
        ("antenna", ["对讲天线"]),
        ("rf_passive", ["对讲功分", "对讲耦合", "对讲双工"]),
        ("positioning", ["定位读写", "定位配套"]),
        ("pos_terminal", ["定位移动终端"]),
        ("led", ["LED显示"]),
        ("decoder", ["视频解码", "视频发送"]),
        ("electrical", ["ACU控制", "按钮箱", "精密配电", "监控控制柜"]),
        ("ups", ["UPS电源", "蓄电池"]),
        ("server", ["服务器"]),
        ("workstation", ["工作站"]),
        ("software", ["系统软件", "管理软件"]),
        ("env_host", ["动环"]),
        ("fire_aerosol", ["气溶胶"]),
        ("emergency_light", ["应急照明"]),
        ("cabinet", ["服务器机柜", "网络机柜"]),
        ("meter", ["电量仪", "电流互感"]),
        ("fiber", ["光纤收发", "光模"]),
        ("printer", ["打印"]),
    ]
    for pkg, keys in cat_rules:
        if _match(ctext, keys):
            return pkg

    if "机器人" in n:
        return "generic"
    return "generic"


def main() -> int:
    ap = argparse.ArgumentParser()
    ap.add_argument("--dry-run", action="store_true")
    args = ap.parse_args()

    conn = psycopg2.connect(**PKG)
    conn.autocommit = False
    cur = conn.cursor()

    cur.execute(
        """
        SELECT id FROM dynamic_model
        WHERE deleted=false AND tenant_id=1 AND code='inspection_item'
        """
    )
    row = cur.fetchone()
    if not row:
        print("ERROR: inspection_item model missing", file=sys.stderr)
        return 1
    insp_model_id = row[0]

    cur.execute(
        """
        SELECT code, id FROM ent_inspection_method_t1
        WHERE deleted=false AND tenant_id=1 AND is_template=true
        """
    )
    method_ids = {code: mid for code, mid in cur.fetchall()}
    missing_methods = sorted(
        {
            m
            for items in PACKAGES.values()
            for _, _, m in items
            if m not in method_ids
        }
    )
    if missing_methods:
        print("ERROR: missing method templates:", missing_methods, file=sys.stderr)
        return 1

    # ensure package items
    code_to_id: Dict[str, int] = {}
    created = updated = 0
    for pkg, items in PACKAGES.items():
        for code, name, method_code in items:
            mid = method_ids[method_code]
            cur.execute(
                """
                SELECT id, name, method_template_id FROM ent_inspection_item_t1
                WHERE deleted=false AND tenant_id=1 AND code=%s
                """,
                (code,),
            )
            ex = cur.fetchone()
            if ex:
                iid, old_name, old_mid = ex
                if old_name != name or old_mid != mid:
                    if not args.dry_run:
                        cur.execute(
                            """
                            UPDATE ent_inspection_item_t1
                            SET name=%s, method_template_id=%s, updater='seed-pkg',
                                update_time=CURRENT_TIMESTAMP
                            WHERE id=%s
                            """,
                            (name, mid, iid),
                        )
                    updated += 1
                code_to_id[code] = iid
            else:
                if args.dry_run:
                    code_to_id[code] = -1
                    created += 1
                else:
                    cur.execute(
                        """
                        INSERT INTO ent_inspection_item_t1 (
                          tenant_id, entity_type_code, model_id, name, code, status,
                          parent_id, tree_path, sort, method_template_id,
                          attrs, custom_fields, creator, deleted
                        ) VALUES (
                          1, 'inspection_item', %s, %s, %s, 1,
                          0, NULL, 0, %s,
                          '{}'::jsonb, '{}'::jsonb, 'seed-pkg', false
                        ) RETURNING id
                        """,
                        (insp_model_id, name, code, mid),
                    )
                    code_to_id[code] = cur.fetchone()[0]
                    created += 1

    # reload ids if dry-run partial
    if not args.dry_run:
        cur.execute(
            """
            SELECT code, id FROM ent_inspection_item_t1
            WHERE deleted=false AND tenant_id=1 AND code LIKE 'INSP-PKG-%%'
            """
        )
        code_to_id.update({c: i for c, i in cur.fetchall()})

    # models + categories
    cur.execute(
        """
        SELECT m.id, m.name,
               COALESCE(string_agg(DISTINCT cat.name, ','), '')
        FROM dynamic_model m
        LEFT JOIN dynamic_model_category_relation_t1 r
          ON r.model_id=m.id AND r.deleted=false
        LEFT JOIN dynamic_category cat
          ON cat.id=r.category_id AND cat.deleted=false
        WHERE m.deleted=false AND m.tenant_id=1 AND m.entity_type_code='equipment'
        GROUP BY m.id, m.name
        ORDER BY m.id
        """
    )
    models = cur.fetchall()

    class_counts: Dict[str, int] = defaultdict(int)
    rel_add = rel_del = 0
    classified: List[Tuple[int, str, str, int]] = []

    for mid, mname, cats in models:
        if mname in SKIP_MODEL_NAMES:
            continue
        pkg = classify(mname, cats.split(",") if cats else [])
        class_counts[pkg] += 1
        item_ids = [code_to_id[c] for c, _, _ in PACKAGES[pkg] if code_to_id.get(c) and code_to_id[c] > 0]
        classified.append((mid, mname, pkg, len(item_ids)))

        if args.dry_run:
            continue

        # current relations
        cur.execute(
            """
            SELECT id, entity_id FROM dynamic_model_entity_relation_t1
            WHERE deleted=false AND tenant_id=1
              AND model_id=%s AND model_entity_type_code='equipment'
              AND entity_type_code='inspection_item'
            """,
            (mid,),
        )
        current = {eid: rid for rid, eid in cur.fetchall()}
        want = set(item_ids)

        for eid in sorted(want - set(current)):
            cur.execute(
                """
                INSERT INTO dynamic_model_entity_relation_t1 (
                  model_id, model_entity_type_code, entity_id, entity_type_code,
                  domain, sort, creator, deleted, tenant_id
                ) VALUES (%s,'equipment',%s,'inspection_item', NULL, 0, 'seed-pkg', false, 1)
                """,
                (mid, eid),
            )
            rel_add += 1

        for eid, rid in current.items():
            if eid not in want:
                cur.execute(
                    """
                    UPDATE dynamic_model_entity_relation_t1
                    SET deleted=true, updater='seed-pkg', update_time=CURRENT_TIMESTAMP
                    WHERE id=%s
                    """,
                    (rid,),
                )
                rel_del += 1

    if args.dry_run:
        conn.rollback()
    else:
        conn.commit()

    print("=== apply equipment inspection packages ===")
    print(f"dry_run={args.dry_run}")
    print(f"package_items created={created} updated={updated} total_codes={len(code_to_id)}")
    print(f"models_classified={len(classified)} rel_add={rel_add} rel_del={rel_del}")
    print("--- class distribution ---")
    for k, v in sorted(class_counts.items(), key=lambda x: (-x[1], x[0])):
        depth = len(PACKAGES[k])
        print(f"{v:3d} models | {depth:2d} items | {k}")
    print("--- sample mapping ---")
    for mid, mname, pkg, n in classified[:15]:
        print(f"{mid}\t{pkg}\t{n}\t{mname[:60]}")
    # critical samples
    print("--- critical ---")
    for mid, mname, pkg, n in classified:
        if pkg in {"tank", "valve", "pump", "gas_lel", "ups", "camera", "access"}:
            print(f"{pkg:10s} {n:2d}  {mname[:70]}")

    cur.close()
    conn.close()
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
