#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
安防剩余专业：检查项按叶子分类挂接整理（tenant=1）。

与 16 / 18 同口径：
- 名称：「检查内容（叶子分类名）」；同义不合并
- 有叶子挂叶子；清空对应 L3 父类糊挂
- 双挂设备叶子 + 检查分类「安防系统检查」
- 按叶子同步型号适用集合

本期专业 L3：
  周界安防、公共广播、电子巡更、综合安防管理、安防传输与机柜、
  停车场管理、一卡通与考勤、安检与违禁品检测、无人机巡检

用法：
  python 19_fix_remaining_security_leaf_items.py --dry-run
  python 19_fix_remaining_security_leaf_items.py
"""

from __future__ import annotations

import argparse
import random
import sys
from typing import Dict, List, Sequence, Tuple

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
INSPECTION_CAT_NAME = "安防系统检查"
UPDATER = "seed-security-leaf-remaining"

Check = Tuple[str, int]

APPEARANCE = [
    ("外观完好、安装牢固", 1),
    ("标识清晰", 3),
]
POWER = [("上电指示正常", 28)]
COMM = [("通讯状态正常、无持续掉线", 27)]
WIRING = [("接线可靠、标识清晰", 32)]

DETECTOR_COMMON = [
    ("外观完好、安装牢固", 1),
    ("探测区域无遮挡", 31),
    ("防区状态正常、无异常报警", 36),
    ("与主机通讯正常", 27),
    ("接线可靠、标识清晰", 32),
]

HOST_COMMON = [
    ("主机上电与自检正常", 28),
    ("防区状态正常、无异常报警", 36),
    ("通讯状态正常、无持续掉线", 27),
    ("机箱外观完好、接线整齐", 1),
    ("备用电源/通讯正常", 26),
]

WORKSTATION = [
    ("开机与显示正常", 35),
    ("业务软件/客户端可登录", 33),
    ("网络连接正常", 22),
    ("键鼠外设正常", 33),
    ("外观完好、线缆整齐", 1),
]

SOFTWARE = [
    ("软件可登录、功能可用", 33),
    ("业务功能抽检正常", 33),
    ("事件可查询、无持续故障", 33),
]

SERVER = [
    ("上电指示正常", 28),
    ("关键服务进程正常", 33),
    ("远程管理可登录", 33),
    ("网络链路正常、无持续掉线", 22),
]

CAMERA = [
    ("镜头清洁、无遮挡", 16),
    ("图像清晰、无花屏黑屏", 17),
    ("云台/方位控制正常", 18),
    ("通讯状态正常、无持续掉线", 27),
    ("外观完好、安装牢固", 1),
]

FENCE = [
    ("外观完好、安装牢固", 1),
    ("防区状态正常、无异常报警", 36),
    ("与主机通讯正常", 27),
    ("接线可靠、标识清晰", 32),
]

CABINET = [
    ("柜体外观完好、门锁正常", 1),
    ("线缆整齐、标识清晰", 3),
    ("散热通风正常", 30),
    ("上电/指示正常", 28),
]

SPECIALTY_LEAF_CHECKS: Dict[str, Dict[str, List[Check]]] = {
    "周界安防": {
        "周界报警主机": HOST_COMMON,
        "振动光纤周界主机": HOST_COMMON,
        "红外对射探测器": DETECTOR_COMMON,
        "微波对射探测器": DETECTOR_COMMON,
        "地埋式振动传感器": DETECTOR_COMMON,
        "周界雷达": DETECTOR_COMMON,
        "张力围栏": FENCE,
        "电子围栏": FENCE,
        "周界联动摄像机": CAMERA,
        "激光云台": CAMERA,
        "周界安防管理软件": [
            ("软件可登录、功能可用", 33),
            ("布撤防/权限操作正常", 35),
            ("防区状态正常、无异常报警", 36),
            ("事件可查询、无持续故障", 33),
        ],
    },
    "公共广播": {
        "广播主机": [
            ("主机上电与自检正常", 28),
            ("试播音质清晰、无杂音断续", 21),
            ("分区切换/优先级正常", 33),
            ("机箱外观完好、接线整齐", 1),
            ("通讯状态正常、无持续掉线", 27),
        ],
        "广播功放": [
            ("上电指示正常", 28),
            ("试播音质清晰、无杂音断续", 21),
            ("散热通风正常", 30),
            ("外观完好、接线牢固", 32),
        ],
        "广播扬声器": [
            ("试播音质清晰、无杂音断续", 21),
            ("外观完好、安装牢固", 1),
            ("接线可靠、标识清晰", 32),
        ],
        "防爆扩音扬声器": [
            ("试播音质清晰、无杂音断续", 21),
            ("外观完好、安装牢固", 1),
            ("防爆标识清晰、密封完好", 3),
            ("接线可靠", 32),
        ],
        "广播终端": [
            ("上电指示正常", 28),
            ("试播/点播正常", 21),
            ("通讯状态正常、无持续掉线", 27),
            ("外观完好、安装牢固", 1),
        ],
        "应急广播话筒": [
            ("话筒拾音清晰、无杂音", 21),
            ("应急强切/优先播放正常", 33),
            ("外观完好、标识清晰", 3),
            ("接线可靠", 32),
        ],
        "消防广播联动模块": [
            ("联动触发正常、可复位", 34),
            ("与消防/广播主机通讯正常", 27),
            ("上电指示正常", 28),
            ("接线可靠、标识清晰", 32),
        ],
        "广播管理软件": SOFTWARE,
    },
    "电子巡更": {
        "巡更棒": [
            ("外观完好、电量正常", 26),
            ("读点/打卡正常", 23),
            ("数据可上传", 27),
        ],
        "巡更点": [
            ("外观完好、安装牢固", 1),
            ("标识清晰", 3),
            ("读点感应正常", 23),
        ],
        "巡更读卡器": [
            ("读卡/识别正常", 23),
            ("指示灯正常", 35),
            ("外观完好、安装牢固", 1),
            ("通讯状态正常", 27),
        ],
        "巡更管理服务器": SERVER,
        "巡更管理软件": [
            ("软件可登录、功能可用", 33),
            ("路线/计划配置正常", 33),
            ("巡更记录可查询", 33),
        ],
    },
    "综合安防管理": {
        "综合安防管理平台服务器": SERVER,
        "综合安防融合应用服务器": SERVER,
        "综合安防平台": [
            ("软件可登录、功能可用", 33),
            ("子系统接入状态正常", 27),
            ("联动规则抽检正常", 34),
            ("事件可查询、无持续故障", 33),
        ],
        "综合安防管理平台软件": [
            ("软件可登录、功能可用", 33),
            ("子系统接入状态正常", 27),
            ("权限与配置抽检正常", 33),
            ("事件可查询、无持续故障", 33),
        ],
        "综合安防管理客户端": WORKSTATION,
        "安防联动控制器": [
            ("上电指示正常", 28),
            ("联动触发正常、可复位", 34),
            ("通讯状态正常、无持续掉线", 27),
            ("机箱外观完好、接线整齐", 1),
        ],
    },
    "安防传输与机柜": {
        "安防专用交换机": [
            ("上电指示正常", 28),
            ("端口链路灯正常、无持续掉线", 22),
            ("关键业务端口可达", 22),
            ("外观完好、安装牢固", 1),
            ("线缆整齐、标识清晰", 3),
        ],
        "光纤收发器": [
            ("上电指示正常", 28),
            ("光/电链路正常", 22),
            ("外观完好、安装牢固", 1),
            ("接线可靠、标识清晰", 32),
        ],
        "视频配线架": [
            ("线缆整齐、标识清晰", 3),
            ("跳线连接可靠、无松动", 32),
            ("外观完好、安装牢固", 1),
        ],
        "监控专用电源箱": [
            ("上电指示正常", 28),
            ("输出电压正常、无异常告警", 28),
            ("柜体外观完好、门锁正常", 1),
            ("线缆整齐、标识清晰", 3),
        ],
        "监控接线箱": CABINET[:3] + WIRING,
        "监控控制柜": CABINET,
    },
    "停车场管理": {
        "道闸": [
            ("闸机开合正常、通行顺畅", 24),
            ("地感/遥控触发正常", 24),
            ("外观完好、安装牢固", 1),
            ("通讯状态正常、无持续掉线", 27),
        ],
        "车牌识别摄像机": [
            ("镜头清洁、无遮挡", 16),
            ("车牌识别正常、结果准确", 23),
            ("图像清晰、无花屏黑屏", 17),
            ("通讯状态正常、无持续掉线", 27),
            ("外观完好、安装牢固", 1),
        ],
        "地磁车位探测器": [
            ("探测区域无遮挡", 31),
            ("车位占用状态正确", 31),
            ("与主机通讯正常", 27),
            ("外观完好、安装牢固", 1),
        ],
        "车位信息显示屏": [
            ("画面显示正常、无花屏黑屏", 17),
            ("车位信息刷新正确", 33),
            ("上电指示正常", 28),
            ("外观完好、安装牢固", 1),
        ],
        "车位引导系统": [
            ("引导指示正确、刷新及时", 33),
            ("通讯状态正常、无持续掉线", 27),
            ("上电指示正常", 28),
            ("外观完好、安装牢固", 1),
        ],
        "停车收费终端": [
            ("刷卡/缴费功能正常", 23),
            ("按键与显示正常", 35),
            ("通讯状态正常、无持续掉线", 27),
            ("外观完好、安装牢固", 1),
        ],
        "停车收费岗亭": [
            ("外观完好、门窗开合正常", 1),
            ("岗亭供电与照明正常", 28),
            ("业务终端可达、通讯正常", 22),
        ],
        "停车场管理服务器": SERVER,
        "停车场管理软件": [
            ("软件可登录、功能可用", 33),
            ("出入场/计费抽检正常", 33),
            ("事件可查询、无持续故障", 33),
        ],
    },
    "一卡通与考勤": {
        "一卡通管理服务器": SERVER,
        "一卡通管理软件": [
            ("软件可登录、功能可用", 33),
            ("卡片/权限下发正常", 23),
            ("交易/事件可查询", 33),
        ],
        "考勤终端": [
            ("刷卡/识别正常、权限匹配", 23),
            ("按键与显示正常", 35),
            ("考勤记录可上传", 27),
            ("外观完好、安装牢固", 1),
        ],
        "消费终端": [
            ("刷卡/消费正常", 23),
            ("按键与显示正常", 35),
            ("通讯状态正常、无持续掉线", 27),
            ("外观完好、安装牢固", 1),
        ],
    },
    "安检与违禁品检测": {
        "X光安检机": [
            ("上电与自检正常", 28),
            ("图像显示正常、无花屏", 17),
            ("传送带运行平稳、急停有效", 24),
            ("辐射警示标识清晰", 3),
            ("外观完好、防护齐全", 1),
        ],
        "金属探测门": [
            ("上电指示正常", 28),
            ("金属探测告警正常、可复位", 34),
            ("灵敏度抽检正常", 31),
            ("外观完好、安装牢固", 1),
        ],
        "爆炸物探测器": [
            ("上电与自检正常", 28),
            ("探测告警正常、可复位", 34),
            ("外观完好、标识清晰", 3),
            ("通讯/数据上报正常", 27),
        ],
    },
    "无人机巡检": {
        "巡检无人机": [
            ("机体外观完好、桨叶无损伤", 1),
            ("开机自检正常、电量充足", 26),
            ("图传/遥控链路正常", 22),
            ("起飞降落/返航抽检正常", 18),
        ],
        "无人机机库": [
            ("机库开合/充电对接正常", 28),
            ("上电指示正常", 28),
            ("通讯状态正常、无持续掉线", 27),
            ("外观完好、安装牢固", 1),
        ],
        "无人机巡检管理软件": [
            ("软件可登录、功能可用", 33),
            ("航线/任务配置正常", 33),
            ("飞行/巡检记录可查询", 33),
        ],
    },
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


def ensure_item(
    cur,
    *,
    model_id: int,
    name: str,
    method_template_id: int,
    code: str,
    dry_run: bool,
) -> int:
    cur.execute(
        """
        SELECT id FROM ent_inspection_item_t1
        WHERE deleted=false AND tenant_id=%s AND name=%s
        """,
        (TENANT, name),
    )
    row = cur.fetchone()
    if row:
        iid = row[0]
        if not dry_run:
            cur.execute(
                """
                UPDATE ent_inspection_item_t1
                SET method_template_id=%s, updater=%s, update_time=CURRENT_TIMESTAMP
                WHERE id=%s AND (method_template_id IS DISTINCT FROM %s)
                """,
                (method_template_id, UPDATER, iid, method_template_id),
            )
        return int(iid)
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
    if entity_id <= 0:
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


def sync_model_applicability(
    cur,
    model_id: int,
    want_item_ids: Sequence[int],
    dry_run: bool,
) -> Tuple[int, int]:
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


def load_l3_and_leaves(cur, l3_name: str) -> Tuple[int, Dict[str, int]]:
    cur.execute(
        """
        SELECT id FROM dynamic_category
        WHERE deleted=false AND tenant_id=%s
          AND category_type_code='equipment' AND name=%s
        """,
        (TENANT, l3_name),
    )
    row = cur.fetchone()
    if not row:
        raise RuntimeError(f"missing L3 equipment category: {l3_name}")
    l3_id = int(row[0])
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
        (l3_id, TENANT, TENANT, l3_id, TENANT),
    )
    leaves = {name: int(cid) for cid, name in cur.fetchall()}
    return l3_id, leaves


def main() -> int:
    ap = argparse.ArgumentParser()
    ap.add_argument("--dry-run", action="store_true")
    ap.add_argument(
        "--only",
        nargs="*",
        default=None,
        help="只处理指定 L3 名，默认全部 SPECIALTY_LEAF_CHECKS",
    )
    args = ap.parse_args()
    dry = args.dry_run
    specialties = args.only or list(SPECIALTY_LEAF_CHECKS.keys())

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

        cur.execute(
            """
            SELECT id FROM dynamic_category
            WHERE deleted=false AND tenant_id=%s
              AND category_type_code='inspection_item' AND name=%s
            """,
            (TENANT, INSPECTION_CAT_NAME),
        )
        row = cur.fetchone()
        if not row:
            print(f"ERROR: missing inspection category {INSPECTION_CAT_NAME}", file=sys.stderr)
            return 1
        insp_cat_id = int(row[0])

        need_create = 0
        plans: List[Tuple[str, int, Dict[str, int], Dict[str, List[Check]]]] = []
        for l3_name in specialties:
            mapping = SPECIALTY_LEAF_CHECKS.get(l3_name)
            if not mapping:
                print(f"ERROR: no mapping for {l3_name}", file=sys.stderr)
                return 1
            l3_id, leaves = load_l3_and_leaves(cur, l3_name)
            missing = sorted(set(mapping) - set(leaves))
            if missing:
                print(f"ERROR: {l3_name} missing leaves: {missing}", file=sys.stderr)
                return 1
            extra = sorted(set(leaves) - set(mapping))
            if extra:
                print(f"WARN: {l3_name} leaves not in mapping (skipped): {extra}")
            for leaf_name, checks in mapping.items():
                for base, _mid in checks:
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
            plans.append((l3_name, l3_id, leaves, mapping))

        new_codes = next_numeric_codes(cur, need_create) if need_create else []
        code_iter = iter(new_codes)
        print(f"specialties={specialties} need_create≈{need_create}")

        total_created = total_linked = total_model_add = total_model_del = 0

        for l3_name, l3_id, leaves, mapping in plans:
            print(f"\n=== {l3_name} ===")
            unlinked = soft_unlink_category(cur, l3_id, dry)
            print(f"  L3 unlink old paste: {unlinked}")

            leaf_cleared = 0
            for leaf_name in mapping:
                leaf_cleared += soft_unlink_category(cur, leaves[leaf_name], dry)
            print(f"  leaf old links cleared: {leaf_cleared}")

            leaf_item_ids: Dict[str, List[int]] = {}
            created = linked = 0
            for leaf_name, checks in mapping.items():
                leaf_id = leaves[leaf_name]
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
                    if ensure_category_link(cur, iid, insp_cat_id, dry):
                        linked += 1
                    ids.append(iid)
                leaf_item_ids[leaf_name] = ids

            print(f"  created={created} links_upserted≈{linked}")
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
                        print(f"  model {mid} {mname[:36]} +{a}/-{d} -> {leaf_name}")
            print(f"  model applicability +{model_add}/-{model_del}")
            total_model_add += model_add
            total_model_del += model_del

            cur.execute(
                """
                SELECT COUNT(*) FROM dynamic_entity_category_relation_t1
                WHERE deleted=false AND tenant_id=%s AND category_id=%s
                  AND entity_type_code='inspection_item'
                """,
                (TENANT, l3_id),
            )
            print(f"  verify L3 remaining links: {cur.fetchone()[0]}")
            for leaf_name in sorted(mapping):
                lid = leaves[leaf_name]
                cur.execute(
                    """
                    SELECT COUNT(*) FROM dynamic_entity_category_relation_t1
                    WHERE deleted=false AND tenant_id=%s AND category_id=%s
                      AND entity_type_code='inspection_item'
                    """,
                    (TENANT, lid),
                )
                print(f"  verify leaf {leaf_name}: {cur.fetchone()[0]}")

        print(
            f"\nTOTAL created={total_created} links≈{total_linked} "
            f"model +{total_model_add}/-{total_model_del}"
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
