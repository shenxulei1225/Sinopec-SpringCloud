#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
安防专业（非视频监控）：检查项按叶子分类挂接整理（tenant=1）。

与 16_fix_video_monitoring_leaf_items.py 同口径：
- 名称：「检查内容（叶子分类名）」；同义不合并
- 有叶子挂叶子；清空对应 L3 父类糊挂
- 双挂设备叶子 + 检查分类「安防系统检查」
- 按叶子同步型号适用集合

本期专业 L3：
  入侵报警、出入口控制、可视对讲、人员定位、大屏与显控

用法：
  python 18_fix_security_specialty_leaf_items.py --dry-run
  python 18_fix_security_specialty_leaf_items.py
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
UPDATER = "seed-security-leaf"

Check = Tuple[str, int]

# ---------- 公共检查块 ----------
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
    ("布撤防/权限操作正常", 35),
    ("事件可查询、无持续故障", 33),
]

ACCESS_CORE = [
    ("刷卡/识别正常、权限匹配", 23),
    ("门锁开合正常、闭锁可靠", 24),
    ("门磁与出门按钮正常", 24),
    ("门禁事件可查询、无持续通讯故障", 33),
    ("外观完好、安装牢固", 1),
]

# L3 父类名 → 叶子 → 检查内容
SPECIALTY_LEAF_CHECKS: Dict[str, Dict[str, List[Check]]] = {
    "入侵报警": {
        "入侵报警主机": HOST_COMMON,
        "入侵报警工作站": WORKSTATION,
        "入侵报警软件": SOFTWARE,
        "声光报警器": [
            ("声光触发正常、可复位", 34),
            ("上电指示正常", 28),
            ("外观完好、安装牢固", 1),
            ("接线可靠", 32),
        ],
        "复合入侵探测器": DETECTOR_COMMON,
        "红外探测器": DETECTOR_COMMON,
        "振动探测器": DETECTOR_COMMON,
        "玻璃破碎探测器": DETECTOR_COMMON,
        "报警键盘": [
            ("键盘操作正常、布撤防有效", 35),
            ("指示灯与显示正常", 35),
            ("外观完好、安装牢固", 1),
            ("与主机通讯正常", 27),
        ],
        "紧急求助按钮": [
            ("按钮触发正常、告警上传", 34),
            ("外观完好、标识清晰", 3),
            ("接线可靠", 32),
            ("与主机联动正常", 27),
        ],
    },
    "出入口控制": {
        "门禁控制器": POWER + COMM + APPEARANCE + WIRING,
        "门禁读卡器": [
            ("读卡器外观完好、指示正常", 35),
            ("刷卡/识别正常、权限匹配", 23),
            ("接线可靠", 32),
        ],
        "门禁电锁": [
            ("门锁开合正常、闭锁可靠", 24),
            ("外观完好、安装牢固", 1),
            ("接线可靠", 32),
        ],
        "开门出门按钮": [
            ("门磁与出门按钮正常", 24),
            ("外观完好、标识清晰", 3),
            ("接线可靠", 32),
        ],
        "出门释放装置": [
            ("门磁与出门按钮正常", 24),
            ("释放动作正常、复位可靠", 24),
            ("外观完好、安装牢固", 1),
        ],
        "单门门禁系统": ACCESS_CORE,
        "双门门禁系统": ACCESS_CORE,
        "人脸识别终端": [
            ("刷卡/识别正常、权限匹配", 23),
            ("按键与显示正常", 35),
            ("外观完好、安装牢固", 1),
            ("通讯状态正常、无持续掉线", 27),
        ],
        "访客机": [
            ("刷卡/识别正常、权限匹配", 23),
            ("按键与显示正常", 35),
            ("外观完好、安装牢固", 1),
            ("业务功能抽检正常", 33),
        ],
        "人行通道闸机": [
            ("闸机开合正常、通行顺畅", 24),
            ("刷卡/识别正常、权限匹配", 23),
            ("外观完好、安装牢固", 1),
            ("通讯状态正常、无持续掉线", 27),
        ],
        "三辊闸摆闸": [
            ("闸机开合正常、通行顺畅", 24),
            ("刷卡/识别正常、权限匹配", 23),
            ("外观完好、安装牢固", 1),
        ],
        "发卡器": [
            ("发卡功能正常", 23),
            ("外观完好、指示正常", 35),
            ("通讯状态正常", 27),
        ],
        "门禁管理服务器": [
            ("上电指示正常", 28),
            ("关键服务进程正常", 33),
            ("远程管理可登录", 33),
            ("网络链路正常、无持续掉线", 22),
            ("门禁事件可查询、无持续通讯故障", 33),
        ],
        "门禁管理软件": [
            ("软件可登录、功能可用", 33),
            ("权限配置与下发正常", 23),
            ("门禁事件可查询、无持续通讯故障", 33),
        ],
    },
    "可视对讲": {
        "门口对讲主机": [
            ("试呼通话清晰、无杂音断续", 21),
            ("按键与显示正常", 35),
            ("外观完好、安装牢固", 1),
            ("通讯状态正常", 27),
        ],
        "室内对讲分机": [
            ("试呼通话清晰、无杂音断续", 21),
            ("分机注册正常", 27),
            ("按键与指示正常", 35),
            ("外观完好、安装牢固", 1),
        ],
        "可视对讲管理主机": [
            ("试呼通话清晰、无杂音断续", 21),
            ("上电指示正常", 28),
            ("业务功能抽检正常", 33),
            ("外观完好、接线整齐", 1),
        ],
        "可视对讲服务器": [
            ("上电指示正常", 28),
            ("关键服务进程正常", 33),
            ("分机/SIP注册正常", 27),
            ("网络链路正常、无持续掉线", 22),
        ],
        "可视对讲管理软件": [
            ("软件可登录、功能可用", 33),
            ("试呼通话正常", 21),
            ("事件/通话记录可查询", 33),
        ],
    },
    "人员定位": {
        "定位移动终端": [
            ("外观完好、佩戴/安装可靠", 1),
            ("开机与电量正常", 26),
            ("定位/上报正常", 27),
            ("行走/定位正常", 18),
        ],
        "定位读写主机": [
            ("读写/定位功能正常", 23),
            ("上电指示正常", 28),
            ("通讯链路正常", 22),
            ("外观完好、安装牢固", 1),
        ],
        "定位配套附件": [
            ("外观完好、安装可靠", 1),
            ("供电适配可靠、标识清晰", 3),
            ("充电对接正常", 28),
        ],
        "定位系统服务器": [
            ("上电指示正常", 28),
            ("关键服务进程正常", 33),
            ("定位上报/后台可见", 27),
            ("网络链路正常、无持续掉线", 22),
        ],
        "定位系统软件": [
            ("软件可登录、功能可用", 33),
            ("定位上报/后台可见", 27),
            ("事件可查询", 33),
        ],
        "定位系统工作站": WORKSTATION,
    },
    "大屏与显控": {
        "LED显示屏": [
            ("画面显示正常、无花屏黑屏", 17),
            ("亮度与色彩正常", 17),
            ("拼接画面正常", 17),
            ("外观完好、安装牢固", 1),
            ("上电指示正常", 28),
        ],
        "拼接控制器": [
            ("拼接画面正常", 17),
            ("画面同步无撕裂", 17),
            ("控制软件可操作", 33),
            ("上电指示正常", 28),
            ("通讯状态正常", 27),
        ],
        "视频发送卡": [
            ("发送卡指示正常", 35),
            ("与显示屏通讯正常", 27),
            ("外观完好、安装牢固", 1),
            ("接线可靠", 32),
        ],
        "视频解码器": [
            ("解码画面正常", 17),
            ("切换各端口画面正常", 17),
            ("亮度与色彩正常", 17),
            ("与大屏/矩阵通讯正常", 27),
            ("外观完好、接线牢固", 32),
        ],
        "大屏安装支架": [
            ("支架牢固、无松动变形", 1),
            ("安装螺栓紧固", 1),
            ("标识清晰", 3),
        ],
        "KVM切换器": [
            ("切换控制正常", 24),
            ("键鼠操作正常", 33),
            ("显示器点亮正常", 35),
            ("各端口画面/键鼠可用", 33),
            ("外观完好、安装牢固", 1),
        ],
        "机架式控制台": [
            ("外观完好、安装牢固", 1),
            ("散热通风正常", 30),
            ("线缆整齐、标识清晰", 3),
            ("门锁/柜体开合正常", 24),
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

        # 预计算需新建数量
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

            # verify
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
