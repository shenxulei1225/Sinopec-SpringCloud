#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
视频监控：检查项按叶子分类挂接整理（tenant=1）。

定稿口径（对话 2026-08-11）：
- 编码：稳定数字码 INSP-#######；不当说明书
- 含义：看名称；名称按分类区分，形如「检查内容（叶子分类名）」
- 属于哪类设备：只认分类—实体关联；禁止用编码前缀推断分类
- 同义项不合并（便于后续配不同检查方法）
- 有叶子挂叶子；父类「视频监控」不再挂检查项
- 不把父类糊清单盲拷到子类；按检查内容语义显式映射

用法：
  python 16_fix_video_monitoring_leaf_items.py --dry-run
  python 16_fix_video_monitoring_leaf_items.py
"""

from __future__ import annotations

import argparse
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
PARENT_NAME = "视频监控"
INSPECTION_ITEM_MODEL_CODE = "inspection_item"

# (检查内容基名, method_template_id) —— 映射写死，不从编码猜类
CameraCheck = Tuple[str, int]

CAMERA_BASE: List[CameraCheck] = [
    ("镜头清洁、无遮挡", 16),
    ("视频画面正常、无花屏黑屏", 17),
    ("补光灯/红外工作正常", 19),
    ("时间OSD正确、与标准时一致", 20),
    ("设备外观完好、支架紧固", 1),
    ("网络链路正常、无持续掉线", 22),
    ("安装位置合理、监视区域无遮挡", 3),
]
CAMERA_PTZ_EXTRA: List[CameraCheck] = [
    ("云台/变焦动作正常", 18),
]

# 叶子 → 检查内容列表（显式语义，非命名推断）
LEAF_CHECKS: Dict[str, List[CameraCheck]] = {
    "枪型摄像机": CAMERA_BASE,
    "半球摄像机": CAMERA_BASE,
    "防爆摄像机": CAMERA_BASE,
    "工业电视摄像机": CAMERA_BASE,
    "布控球": CAMERA_BASE,
    "云台摄像机": CAMERA_BASE + CAMERA_PTZ_EXTRA,
    "网络硬盘录像机": [
        ("上电指示正常", 28),
        ("录像回放正常", 17),
        ("网络链路正常、无持续掉线", 22),
        ("设备外观完好、安装牢固", 1),
        ("存储服务运行正常", 33),
        ("SMART/健康状态正常", 33),
        ("读写无异常告警", 33),
    ],
    "硬盘录像机": [
        ("上电指示正常", 28),
        ("录像回放正常", 17),
        ("网络链路正常、无持续掉线", 22),
        ("设备外观完好、安装牢固", 1),
        ("存储服务运行正常", 33),
        ("SMART/健康状态正常", 33),
        ("读写无异常告警", 33),
    ],
    "视频管理服务器": [
        ("上电指示正常", 28),
        ("关键服务进程正常", 33),
        ("远程管理可登录", 33),
        ("网络链路正常、无持续掉线", 22),
        ("磁盘/RAID或存储状态正常", 33),
        ("机箱外观完好、接地可靠", 13),
    ],
    "流媒体服务器": [
        ("上电指示正常", 28),
        ("关键服务进程正常", 33),
        ("远程管理可登录", 33),
        ("网络链路正常、无持续掉线", 22),
        ("机箱外观完好、接地可靠", 13),
    ],
    "视频存储服务器": [
        ("上电指示正常", 28),
        ("关键服务进程正常", 33),
        ("远程管理可登录", 33),
        ("网络链路正常、无持续掉线", 22),
        ("磁盘/RAID或存储状态正常", 33),
        ("机箱外观完好、接地可靠", 13),
    ],
    "智能视频分析服务器": [
        ("上电指示正常", 28),
        ("关键服务进程正常", 33),
        ("远程管理可登录", 33),
        ("网络链路正常、无持续掉线", 22),
        ("机箱外观完好、接地可靠", 13),
    ],
    "机器视觉推理服务器": [
        ("上电指示正常", 28),
        ("关键服务进程正常", 33),
        ("远程管理可登录", 33),
        ("网络链路正常、无持续掉线", 22),
        ("机箱外观完好、接地可靠", 13),
    ],
    "视频编码器": [
        ("上电指示正常", 28),
        ("视频画面正常、无花屏黑屏", 17),
        ("网络链路正常、无持续掉线", 22),
        ("外观完好、接线牢固", 32),
    ],
    "视频监控工作站": [
        ("开机与显示正常", 28),
        ("网络连接正常", 22),
        ("业务软件/客户端可登录", 33),
        ("键鼠外设正常", 33),
        ("外观完好、线缆整齐", 1),
    ],
    "监视器": [
        ("画面显示正常、无花屏黑屏", 17),
        ("亮度与色彩正常", 17),
        ("上电指示正常", 28),
        ("结构安装牢固、接地可靠", 13),
    ],
    "监控存储硬盘": [
        ("上电指示正常", 28),
        ("SMART/健康状态正常", 33),
        ("读写无异常告警", 33),
        ("安装牢固、振动正常", 1),
        ("所属存储服务状态正常", 33),
    ],
    "监控补光灯": [
        ("补光灯/红外工作正常", 19),
        ("上电指示正常", 28),
        ("外观完好、安装牢固", 1),
        ("接线可靠", 32),
    ],
    "监控立杆": [
        ("安装牢固、无松动变形", 1),
        ("接地与防雷设施完好", 13),
        ("标识清晰", 3),
    ],
    "视频监控防雷器": [
        ("接地与防雷设施完好、连接可靠", 13),
        ("外观完好、安装牢固", 1),
        ("标识清晰", 3),
    ],
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
        if cur.fetchone():
            continue
        if candidate in used:
            continue
        used.add(candidate)
        codes.append(candidate)
    return codes


def rename_legacy_codes(cur, dry_run: bool) -> int:
    """将 INSP-PKG-* / INS-ITEM-* / INSP-SEED-* 改为稳定数字码；不改名称、不合并。"""
    cur.execute(
        """
        SELECT id, code FROM ent_inspection_item_t1
        WHERE deleted=false AND tenant_id=%s
          AND (
            code LIKE 'INSP-PKG-%%'
            OR code LIKE 'INS-ITEM-%%'
            OR code LIKE 'INSP-SEED-%%'
          )
        ORDER BY id
        """,
        (TENANT,),
    )
    rows = cur.fetchall()
    if not rows:
        return 0
    codes = next_numeric_codes(cur, len(rows))
    for (iid, old_code), new_code in zip(rows, codes):
        if dry_run:
            print(f"  rename {old_code} -> {new_code} (id={iid})")
            continue
        cur.execute(
            """
            UPDATE ent_inspection_item_t1
            SET code=%s, updater='seed-video-leaf', update_time=CURRENT_TIMESTAMP
            WHERE id=%s
            """,
            (new_code, iid),
        )
    return len(rows)


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
                SET method_template_id=%s, updater='seed-video-leaf',
                    update_time=CURRENT_TIMESTAMP
                WHERE id=%s AND (method_template_id IS DISTINCT FROM %s)
                """,
                (method_template_id, iid, method_template_id),
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
          '{}'::jsonb, '{}'::jsonb, 'seed-video-leaf', false
        ) RETURNING id
        """,
        (TENANT, model_id, name, code, method_template_id),
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
            SET deleted=true, updater='seed-video-leaf', update_time=CURRENT_TIMESTAMP
            WHERE id=%s
            """,
            (rid,),
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
                SET deleted=false, updater='seed-video-leaf', update_time=CURRENT_TIMESTAMP
                WHERE id=%s
                """,
                (rid,),
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
        ) VALUES (%s, %s, 'inspection_item', 0, 'seed-video-leaf', false, %s)
        """,
        (entity_id, category_id, TENANT),
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
        SELECT id, entity_id FROM dynamic_model_entity_relation_t1
        WHERE deleted=false AND tenant_id=%s
          AND model_id=%s AND model_entity_type_code='equipment'
          AND entity_type_code='inspection_item'
        """,
        (TENANT, model_id),
    )
    current = {eid: rid for rid, eid in cur.fetchall()}
    add = del_ = 0
    for eid in sorted(want - set(current)):
        add += 1
        if dry_run:
            continue
        cur.execute(
            """
            INSERT INTO dynamic_model_entity_relation_t1 (
              model_id, model_entity_type_code, entity_id, entity_type_code,
              domain, sort, creator, deleted, tenant_id
            ) VALUES (%s,'equipment',%s,'inspection_item', NULL, 0, 'seed-video-leaf', false, %s)
            """,
            (model_id, eid, TENANT),
        )
    for eid, rid in current.items():
        if eid not in want:
            del_ += 1
            if dry_run:
                continue
            cur.execute(
                """
                UPDATE dynamic_model_entity_relation_t1
                SET deleted=true, updater='seed-video-leaf', update_time=CURRENT_TIMESTAMP
                WHERE id=%s
                """,
                (rid,),
            )
    return add, del_


def main() -> int:
    ap = argparse.ArgumentParser()
    ap.add_argument("--dry-run", action="store_true")
    args = ap.parse_args()
    dry = args.dry_run

    conn = psycopg2.connect(**PKG)
    conn.autocommit = False
    cur = conn.cursor()

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
          AND category_type_code='equipment' AND name=%s
        """,
        (TENANT, PARENT_NAME),
    )
    row = cur.fetchone()
    if not row:
        print(f"ERROR: parent category {PARENT_NAME!r} missing", file=sys.stderr)
        return 1
    parent_id = int(row[0])

    cur.execute(
        """
        SELECT id, name FROM dynamic_category
        WHERE deleted=false AND tenant_id=%s
          AND category_type_code='equipment' AND parent_id=%s
        """,
        (TENANT, parent_id),
    )
    leaves = {name: int(cid) for cid, name in cur.fetchall()}
    missing_leaves = sorted(set(LEAF_CHECKS) - set(leaves))
    if missing_leaves:
        print("ERROR: missing leaves:", missing_leaves, file=sys.stderr)
        return 1

    # 1) 清空父类挂接
    parent_unlinked = soft_unlink_category(cur, parent_id, dry)
    print(f"parent '{PARENT_NAME}' unlink inspection_item links: {parent_unlinked}")

    # 2) 清空各叶子旧挂接（再按映射重建，避免残留错挂/重复）
    leaf_cleared = 0
    for leaf_name, leaf_id in leaves.items():
        if leaf_name in LEAF_CHECKS:
            leaf_cleared += soft_unlink_category(cur, leaf_id, dry)
    print(f"leaf old links cleared: {leaf_cleared}")

    # 3) 按叶子创建「内容（分类）」检查项并挂接
    need_create = 0
    for leaf_name, checks in LEAF_CHECKS.items():
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
    new_codes = next_numeric_codes(cur, need_create) if need_create else []
    code_iter = iter(new_codes)

    leaf_item_ids: Dict[str, List[int]] = {}
    created = linked = 0
    for leaf_name, checks in LEAF_CHECKS.items():
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
            ids.append(iid)
        leaf_item_ids[leaf_name] = ids

    print(f"leaf items created={created} category_links_upserted≈{linked}")

    # 4) 型号适用集合：按型号所属叶子，同步为该叶子检查项（不合并旧同义项实体）
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
                print(f"  model {mid} {mname[:40]} +{a}/-{d} -> {leaf_name} ({len(item_ids)} items)")

    print(f"model applicability add={model_add} del={model_del}")

    # 5) 全局：PKG / ITEM / SEED 编码改为数字码（同义不合并）
    print("rename legacy package/seed codes -> INSP-#######")
    renamed = rename_legacy_codes(cur, dry)
    print(f"renamed_codes={renamed}")

    if dry:
        conn.rollback()
        print("DRY-RUN: rolled back")
    else:
        conn.commit()
        print("COMMITTED")

    # 验收摘要
    cur.execute(
        """
        SELECT COUNT(*) FROM dynamic_entity_category_relation_t1
        WHERE deleted=false AND tenant_id=%s AND category_id=%s
          AND entity_type_code='inspection_item'
        """,
        (TENANT, parent_id),
    )
    print(f"verify parent links remaining: {cur.fetchone()[0]}")
    for leaf_name in sorted(LEAF_CHECKS):
        lid = leaves[leaf_name]
        cur.execute(
            """
            SELECT COUNT(*) FROM dynamic_entity_category_relation_t1
            WHERE deleted=false AND tenant_id=%s AND category_id=%s
              AND entity_type_code='inspection_item'
            """,
            (TENANT, lid),
        )
        print(f"verify leaf {leaf_name}: {cur.fetchone()[0]} items")

    cur.close()
    conn.close()
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
