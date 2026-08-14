#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
仪器仪表与自动化：检查项按叶子分类挂接整理（tenant=1）。

与 16 / 18 / 19 同口径：
- 名称：「检查内容（叶子分类名）」；同义不合并
- 有叶子挂叶子；无叶子（如「仪表接线盒」）挂 L3
- 清空对应 L3 糊挂；双挂设备叶子 + 检查分类「仪表与自动化检查」
- 软删曾挂在本专业 L3 上的无后缀短名残留
- 按叶子同步型号适用集合

用法：
  python 20_fix_instrumentation_leaf_items.py --dry-run
  python 20_fix_instrumentation_leaf_items.py
"""

from __future__ import annotations

import argparse
import random
import sys
import uuid
from typing import Dict, List, Sequence, Set, Tuple

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
INSPECTION_CAT_NAME = "仪表与自动化检查"
INSPECTION_CAT_PARENT = "站场检查"
ROOT_EQUIP_NAME = "仪器仪表与自动化"
UPDATER = "seed-instrument-leaf"

Check = Tuple[str, int]

APPEARANCE = [
    ("外观完好、安装牢固", 1),
    ("标识清晰", 3),
]
WIRING = [("接线可靠、标识清晰", 32)]
POWER = [("上电指示正常", 28)]
COMM = [("通讯状态正常、无持续掉线", 27)]

SENSOR_COMMON = [
    ("外观完好、安装牢固", 1),
    ("标识清晰", 3),
    ("接线可靠、标识清晰", 32),
    ("上电指示正常", 28),
    ("显示/读数正常、无持续异常", 13),
]

TRANSMITTER_COMMON = [
    ("外观完好、安装牢固", 1),
    ("标识清晰", 3),
    ("接线可靠、标识清晰", 32),
    ("上电指示正常", 28),
    ("输出信号正常、无持续异常", 13),
    ("通讯状态正常、无持续掉线", 27),
]

VALVE_COMMON = [
    ("阀体及法兰无泄漏", 4),
    ("开关灵活到位、无卡涩", 24),
    ("阀位指示正确、与工艺状态一致", 13),
    ("手轮/执行机构完好、限位有效", 1),
    ("支撑与紧固件完好、无松动", 1),
    ("标识清晰", 3),
]

GAS_DETECTOR = [
    ("外观完好、安装牢固", 1),
    ("探头清洁、无遮挡凝露损坏", 31),
    ("上电指示正常", 28),
    ("读数合理、无持续异常", 13),
    ("报警阈值与联动正常", 34),
    ("接线密封完好、标识清晰", 32),
]

SPECIALTY_LEAF_CHECKS: Dict[str, Dict[str, List[Check]]] = {
    "环境检测仪表": {
        "可燃气体探测器": GAS_DETECTOR,
        "氧气传感器": SENSOR_COMMON
        + [("氧气读数合理、无持续异常", 13)],
        "二氧化碳传感器": SENSOR_COMMON
        + [("二氧化碳读数合理、无持续异常", 13)],
        "温湿度传感器": SENSOR_COMMON
        + [("温湿度读数合理、无持续异常", 13)],
        "漏水监测器": [
            ("外观完好、安装牢固", 1),
            ("主机指示正常", 28),
            ("探测区域无遮挡", 31),
            ("漏水告警抽检正常、可复位", 34),
            ("接线可靠、标识清晰", 32),
        ],
    },
    "压力测量": {
        "压力变送器": TRANSMITTER_COMMON,
        "差压变送器": TRANSMITTER_COMMON,
        "压力表": [
            ("外观完好、安装牢固", 1),
            ("表盘清晰、指针无卡滞", 13),
            ("读数合理、与工况一致", 13),
            ("取压管路无泄漏", 4),
            ("标识清晰", 3),
        ],
        "就地仪表": [
            ("外观完好、安装牢固", 1),
            ("显示/指示正常", 13),
            ("读数合理、与工况一致", 13),
            ("标识清晰", 3),
        ],
    },
    "液位测量": {
        "液位变送器": TRANSMITTER_COMMON,
        "液位计": [
            ("外观完好、安装牢固", 1),
            ("显示/指示正常、无卡滞", 13),
            ("液位读数合理、与工况一致", 13),
            ("连接件无泄漏", 4),
            ("标识清晰", 3),
        ],
    },
    "温度测量": {
        "温度变送器": TRANSMITTER_COMMON,
        "热电阻": SENSOR_COMMON,
        "热电偶": SENSOR_COMMON,
    },
    "流量测量": {
        "流量变送器": TRANSMITTER_COMMON,
        "质量流量计": TRANSMITTER_COMMON + [("零点/量程抽检正常", 13)],
        "超声波流量计": TRANSMITTER_COMMON + [("探头安装牢固、耦合正常", 1)],
        "涡轮流量计": [
            ("外观完好、安装牢固", 1),
            ("转动灵活、无异响卡涩", 24),
            ("读数合理、与工况一致", 13),
            ("接线可靠、标识清晰", 32),
            ("上电指示正常", 28),
        ],
    },
    "控制阀": {
        "气动调节阀": VALVE_COMMON
        + [("气源压力正常、执行机构动作正常", 24)],
        "电动调节阀": VALVE_COMMON
        + [("上电指示正常", 28), ("电动执行机构动作正常", 24)],
        "电磁阀": [
            ("阀体及接管无泄漏", 4),
            ("通电动作正常、复位可靠", 24),
            ("上电指示正常", 28),
            ("接线可靠、标识清晰", 32),
            ("外观完好、安装牢固", 1),
        ],
    },
    "分析仪表": {
        "气体分析仪": [
            ("外观完好、安装牢固", 1),
            ("上电指示正常", 28),
            ("采样管路畅通、无泄漏", 4),
            ("分析读数合理、无持续异常", 13),
            ("通讯状态正常、无持续掉线", 27),
        ],
        "水质分析仪": [
            ("外观完好、安装牢固", 1),
            ("上电指示正常", 28),
            ("探头/电极清洁、浸泡正常", 31),
            ("分析读数合理、无持续异常", 13),
            ("通讯状态正常、无持续掉线", 27),
        ],
        "分析小屋": [
            ("小屋外观完好、门锁正常", 1),
            ("通风与温控正常", 30),
            ("供电与照明正常", 28),
            ("气体泄漏告警抽检正常", 34),
            ("线缆整齐、标识清晰", 3),
        ],
    },
    "贸易计量": {
        "计量橇": [
            ("橇体外观完好、标识清晰", 1),
            ("上电指示正常", 28),
            ("流量/计量读数正常", 13),
            ("阀门与法兰无泄漏", 4),
            ("通讯状态正常、无持续掉线", 27),
        ],
        "气质分析橇": [
            ("橇体外观完好、标识清晰", 1),
            ("上电指示正常", 28),
            ("分析读数合理、无持续异常", 13),
            ("采样管路畅通、无泄漏", 4),
            ("通讯状态正常、无持续掉线", 27),
        ],
    },
    # L3 自身即叶子
    "仪表接线盒": {
        "仪表接线盒": [
            ("盒体外观完好、门锁正常", 1),
            ("接线整齐、标识清晰", 3),
            ("端子紧固、无松动虚接", 32),
            ("密封完好、无进水受潮", 4),
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


def load_l3_and_leaves(cur, l3_name: str) -> Tuple[int, Dict[str, int], bool]:
    """返回 (l3_id, leaves_by_name, l3_is_leaf)。"""
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
        SELECT 1 FROM dynamic_category
        WHERE parent_id=%s AND deleted=false AND tenant_id=%s
        LIMIT 1
        """,
        (l3_id, TENANT),
    )
    if not cur.fetchone():
        return l3_id, {l3_name: l3_id}, True

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
    return l3_id, leaves, False


def ensure_inspection_category(cur, dry_run: bool) -> int:
    cur.execute(
        """
        SELECT id FROM dynamic_category
        WHERE deleted=false AND tenant_id=%s
          AND category_type_code='inspection_item' AND name=%s
        """,
        (TENANT, INSPECTION_CAT_NAME),
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
        print(f"  would create inspection category {INSPECTION_CAT_NAME} under {INSPECTION_CAT_PARENT}")
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
            INSPECTION_CAT_NAME,
            code,
            parent_level + 1,
            "站场检查下：仪器仪表与自动化设备检查内容",
            UPDATER,
            TENANT,
            parent_code,
        ),
    )
    new_id = int(cur.fetchone()[0])
    base = (parent_path or f"/{parent_id}/").rstrip("/") + f"/{new_id}/"
    cur.execute(
        "UPDATE dynamic_category SET tree_path=%s WHERE id=%s",
        (base, new_id),
    )
    print(f"  created inspection category id={new_id} {INSPECTION_CAT_NAME}")
    return new_id


def collect_l3_short_name_ids(cur, l3_ids: Sequence[int]) -> Set[int]:
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

        insp_cat_id = ensure_inspection_category(cur, dry)
        if insp_cat_id < 0 and not dry:
            print("ERROR: failed to create inspection category", file=sys.stderr)
            return 1

        need_create = 0
        plans: List[Tuple[str, int, Dict[str, int], Dict[str, List[Check]], bool]] = []
        l3_ids: List[int] = []
        for l3_name in specialties:
            mapping = SPECIALTY_LEAF_CHECKS.get(l3_name)
            if not mapping:
                print(f"ERROR: no mapping for {l3_name}", file=sys.stderr)
                return 1
            l3_id, leaves, l3_is_leaf = load_l3_and_leaves(cur, l3_name)
            missing = sorted(set(mapping) - set(leaves))
            if missing:
                print(f"ERROR: {l3_name} missing leaves: {missing}", file=sys.stderr)
                return 1
            extra = sorted(set(leaves) - set(mapping))
            if extra:
                print(f"WARN: {l3_name} leaves not in mapping (skipped): {extra}")
            l3_ids.append(l3_id)
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
            plans.append((l3_name, l3_id, leaves, mapping, l3_is_leaf))

        short_ids = collect_l3_short_name_ids(cur, l3_ids)
        print(
            f"root={ROOT_EQUIP_NAME} specialties={specialties} "
            f"need_create≈{need_create} short_names_on_l3={len(short_ids)}"
        )

        new_codes = next_numeric_codes(cur, need_create) if need_create else []
        code_iter = iter(new_codes)
        total_created = total_linked = total_model_add = total_model_del = 0

        for l3_name, l3_id, leaves, mapping, l3_is_leaf in plans:
            print(f"\n=== {l3_name} {'(L3-leaf)' if l3_is_leaf else ''} ===")
            # 只清 L3 糊挂；叶子上已有带后缀项（如可燃气体探测器）保留，仅补核心清单
            unlinked = soft_unlink_category(cur, l3_id, dry)
            print(f"  L3 unlink old paste: {unlinked}")

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
                    if insp_cat_id > 0 and ensure_category_link(cur, iid, insp_cat_id, dry):
                        linked += 1
                    ids.append(iid)
                # 型号同步：叶子上全部仍挂的检查项（核心集 + 历史带后缀项）
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

            if not l3_is_leaf:
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

        print(f"\n=== soft-delete L3 short-name residuals ({len(short_ids)}) ===")
        n_del = soft_delete_entities(cur, sorted(short_ids), dry)
        print(f"  soft_deleted_entities={n_del}")

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
