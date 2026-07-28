#!/usr/bin/env python3
"""
从 zhgl_import_temp.biz_region 导出管廊空间实例 → tenant 2（智慧管廊）。

定稿（2026-07-27）：
- 不创建 ent_region。
- 唯一 facility「武汉光谷管廊」+ 同名根 zone（REF_FACILITY → 该 facility）。
- **分区（zone）**：根、管廊段、防火区、**各类舱室**（Pattern C 分类树）。
- **构筑物（structure）**：口部、风亭、坑、交叉口等；**不**重复导入舱室。
- **集水坑**、**排水坑** 等业务上在舱室内：`zone_id` 挂同防火区下 **舱室 zone**（源库父级多为防火区；同防火区内按 sort/id 轮询分配到各舱室）。
"""

from __future__ import annotations

import json
import os
import re
from collections import defaultdict
from pathlib import Path

import psycopg2
import psycopg2.extras

TENANT_ID = int(os.environ.get("CORRIDOR_TENANT_ID", "2"))
ROOT_NAME = "武汉光谷管廊"
ROOT_FACILITY_CODE = "FAC-CORRIDOR-WH-GGGL"
ROOT_ZONE_CODE = "ZONE-CORRIDOR-WH-GGGL-ROOT"

# zhgl system_model.id → (entity kind, dynamic_model.code)
MODEL_MAP: dict[int, str] = {
    73: "MODEL-ZONE-UT-TUNNEL-SEGMENT",
    74: "MODEL-ZONE-UT-FIRE-COMPARTMENT",
    75: "MODEL-ZONE-UT-POWER-DEDICATED-CABIN",
    76: "MODEL-ZONE-UT-POWER-INFO-CABIN",
    77: "MODEL-ZONE-UT-INTEGRATED-PIPE-CABIN",
    78: "MODEL-ZONE-UT-PIPE-CABIN",
    79: "MODEL-ZONE-UT-GAS-CABIN",
    80: "MODEL-ZONE-UT-SEWAGE-CABIN",
    81: "MODEL-ZONE-UT-WATER-SUPPLY-CABIN",
    82: "MODEL-ZONE-UT-TELECOM-CABIN",
    83: "MODEL-ZONE-UT-INTERSECTION",
    84: "MODEL-ZONE-UT-PERSONNEL-ACCESS",
    85: "MODEL-ZONE-UT-INVERTED-SIPHON",
    86: "MODEL-ZONE-UT-SUBSTATION",
    87: "MODEL-ZONE-UT-HOIST-PORT",
    88: "MODEL-ZONE-UT-LEAD-OUT-DOOR",
    89: "MODEL-ZONE-UT-MECH-EXHAUST",
    90: "MODEL-ZONE-UT-MECH-AIR-INLET",
    91: "MODEL-ZONE-UT-END-SHAFT",
    92: "MODEL-ZONE-UT-NATURAL-AIR-INLET",
    93: "MODEL-ZONE-UT-SUMP-PIT",
    156: "MODEL-ZONE-UT-DRAIN-PIT",
}

ROOT_FACILITY_ID = 2_000_001
ROOT_ZONE_ID = 2_000_002
CAT_ROOT_ID = 2_100_001
CAT_WHGG_ID = 2_100_002
CAT_ZONE_BASE = 2_100_500
ZONE_BASE = 3_000_000
STRUCTURE_BASE = 4_000_000
LINK_BASE = 5_000_000

# zhgl system_model.id：舱室（*CABIN* / 电力信息仓等）→ zone，不在构筑物重复建
CABIN_ZHGL_MODEL_IDS = frozenset({75, 76, 77, 78, 79, 80, 81, 82})
# 舱室 zone 名称 → 管线舱室列表.json 中的「舱室」取值
CABIN_NAME_TO_PIPELINE_LABEL: dict[str, str] = {
    "管道舱": "管道仓",
    "电力信息舱": "电力信息舱",
    "高压电力舱": "高压电力仓",
    "综合管道舱": "管道仓",
    "电力专用舱": "高压电力仓",
    "燃气舱": "燃气舱",
    "污水舱": "污水舱",
    "给水舱": "给水舱",
    "通信舱": "通信舱",
}
ZONE_EMIT_MODEL_IDS = frozenset({73, 74}) | CABIN_ZHGL_MODEL_IDS
STRUCTURE_EMIT_MODEL_IDS = frozenset(MODEL_MAP.keys()) - ZONE_EMIT_MODEL_IDS

KEEP_ZONE_MODEL_CODES = (
    "MODEL-ZONE-UT-CORRIDOR-ROOT",
    "MODEL-ZONE-UT-TUNNEL-SEGMENT",
    "MODEL-ZONE-UT-FIRE-COMPARTMENT",
    *(MODEL_MAP[i] for i in sorted(CABIN_ZHGL_MODEL_IDS)),
)
# 仅 structure 使用的 zhgl 细类：不得保留 MODEL-ZONE-UT-* 注册
ZONE_RETIRE_MODEL_CODES = tuple(
    MODEL_MAP[i] for i in sorted(STRUCTURE_EMIT_MODEL_IDS)
)

# 构筑物 zone_id 须挂舱室（非防火区）；zhgl biz_region 父级仍为防火区
STRUCTURE_ZONE_IN_CABIN_ZHGL_MODEL_IDS = frozenset({93, 156})
# 同防火区下舱室排序（轮询分配集水坑/排水坑）；管道舱优先，其次综合管道、污给水等，再电力舱
CABIN_PICK_PREFERENCE_ZHGL_MODEL_IDS = (78, 77, 80, 81, 79, 82, 75, 76)


def sql_str(value: str) -> str:
    return "'" + value.replace("'", "''") + "'"


def ent_id(legacy_id: int) -> int:
    return ZONE_BASE + legacy_id


def structure_ent_id(legacy_id: int) -> int:
    return STRUCTURE_BASE + legacy_id


def resolve_segment_and_fire_labels(
    fire_legacy: int,
    rows_by_id: dict[int, dict],
    parent_legacy: dict[int, int | None],
) -> tuple[str, str]:
    """由防火区 legacy 解析管廊段显示名、防火区显示名（用于构筑物命名）。"""
    fire_row = rows_by_id[fire_legacy]
    fire_label = (fire_row.get("name") or "").strip() or f"{fire_legacy}#防火区"
    seg_legacy: int | None = None
    cur: int | None = fire_legacy
    while cur is not None:
        row = rows_by_id.get(cur)
        if row is None:
            break
        if int(row["model_id"]) == 73:
            seg_legacy = cur
            break
        cur = parent_legacy.get(cur)
    if seg_legacy is None:
        seg_label = "未知段"
    else:
        seg_row = rows_by_id[seg_legacy]
        seg_label = (seg_row.get("name") or "").strip() or f"段-{seg_legacy}"
    return seg_label, fire_label


def structure_display_name(
    seg_label: str,
    fire_label: str,
    type_label: str,
    legacy_id: int,
    cabin_label: str | None = None,
) -> str:
    """构筑物名称：路段·防火区·[舱室·]类型。"""
    if cabin_label:
        base = f"{seg_label}·{fire_label}·{cabin_label}·{type_label}"
    else:
        base = f"{seg_label}·{fire_label}·{type_label}"
    return base if type_label else f"{base}·{legacy_id}"


def build_cabins_by_fire_legacy(
    rows_by_id: dict[int, dict],
    parent_legacy: dict[int, int | None],
) -> dict[int, list[int]]:
    by_fire: dict[int, list[int]] = {}
    for legacy, row in rows_by_id.items():
        if int(row["model_id"]) not in CABIN_ZHGL_MODEL_IDS:
            continue
        fire = parent_legacy.get(legacy)
        if fire is None:
            continue
        by_fire.setdefault(fire, []).append(legacy)
    return by_fire


def sort_cabin_legacy_ids(
    cabin_ids: list[int],
    rows_by_id: dict[int, dict],
) -> list[int]:
    pref_index = {m: i for i, m in enumerate(CABIN_PICK_PREFERENCE_ZHGL_MODEL_IDS)}
    return sorted(
        cabin_ids,
        key=lambda c: (pref_index.get(int(rows_by_id[c]["model_id"]), 999), c),
    )


def build_in_cabin_structure_assignments(
    rows: list[dict],
    parent_legacy: dict[int, int | None],
    rows_by_id: dict[int, dict],
    cabins_by_fire: dict[int, list[int]],
) -> dict[int, int]:
    """同防火区内集水坑/排水坑按 sort、id 排序后轮询挂到各舱室 zone。"""
    by_fire: dict[int, list[int]] = {}
    for row in rows:
        mid = int(row["model_id"])
        if mid not in STRUCTURE_ZONE_IN_CABIN_ZHGL_MODEL_IDS:
            continue
        legacy = int(row["id"])
        fire = parent_legacy.get(legacy)
        if fire is None:
            continue
        by_fire.setdefault(fire, []).append(legacy)

    assignment: dict[int, int] = {}
    for fire, structure_ids in by_fire.items():
        cabins = sort_cabin_legacy_ids(cabins_by_fire.get(fire, []), rows_by_id)
        if not cabins:
            continue
        structure_ids.sort(
            key=lambda s: (int(rows_by_id[s].get("sort") or 0), s),
        )
        for i, sid in enumerate(structure_ids):
            assignment[sid] = cabins[i % len(cabins)]
    return assignment


def resolve_structure_zone_legacy(
    structure_legacy: int,
    fire_legacy: int,
    structure_zhgl_model_id: int,
    in_cabin_assignments: dict[int, int],
) -> tuple[int, bool]:
    if structure_zhgl_model_id in STRUCTURE_ZONE_IN_CABIN_ZHGL_MODEL_IDS:
        cabin = in_cabin_assignments.get(structure_legacy)
        if cabin is not None:
            return cabin, True
    return fire_legacy, False


def structure_business_code(fire_legacy: int, legacy_id: int) -> str:
    return f"STRUCT-{fire_legacy}-{legacy_id}"


def structure_model_code(zone_model_code: str) -> str:
    if not zone_model_code.startswith("MODEL-ZONE-"):
        raise ValueError(zone_model_code)
    return "MODEL-STRUCTURE-" + zone_model_code.removeprefix("MODEL-ZONE-")


def cat_id(legacy_id: int) -> int:
    return CAT_ZONE_BASE + legacy_id


def load_parent_legacy_from_category(
    cur: psycopg2.extensions.cursor,
    legacy_ids: set[int],
) -> dict[int, int | None]:
    """从 zhgl 已整理好的 region 分类树读取 biz_region 父级（非 biz_region.parent_id）。"""
    cur.execute(
        """
        SELECT c.entity_id, c.entity_model_id, p.entity_id AS parent_entity_id
        FROM system_category c
        LEFT JOIN system_category p ON p.id = c.parent_id AND p.deleted = false
        WHERE c.deleted = false
          AND c.category_type_code = 'region'
          AND c.entity_id = ANY(%s)
        """,
        (list(legacy_ids),),
    )
    parent: dict[int, int | None] = {}
    for row in cur.fetchall():
        legacy = int(row["entity_id"])
        model_id = int(row["entity_model_id"])
        pe = row["parent_entity_id"]
        if model_id == 73:
            parent[legacy] = None
        elif pe is not None:
            parent[legacy] = int(pe)
        else:
            parent[legacy] = None
    missing = legacy_ids - parent.keys()
    if missing:
        sample = sorted(missing)[:15]
        raise SystemExit(
            f"system_category(region) 未覆盖 biz_region entity_id（共 {len(missing)} 条），示例: {sample}"
        )
    return parent


def normalize_misnested_fire_zone_parents(
    parent_legacy: dict[int, int | None],
    rows_by_id: dict[int, dict],
) -> list[tuple[int, int, int]]:
    """分类树中防火区误挂另一防火区时，提升到内层防火区的父级（应为管廊段）。"""
    fixes: list[tuple[int, int, int]] = []
    for legacy, row in rows_by_id.items():
        if int(row["model_id"]) != 74:
            continue
        pl = parent_legacy.get(legacy)
        if pl is None:
            continue
        parent_row = rows_by_id.get(pl)
        if parent_row is None or int(parent_row["model_id"]) != 74:
            continue
        uplift = parent_legacy.get(pl)
        if uplift is None:
            continue
        fixes.append((legacy, pl, uplift))
        parent_legacy[legacy] = uplift
    return fixes


def zone_tree_path(entity_id: int, parent_zone_id: int, cache: dict[int, str]) -> str:
    if parent_zone_id and parent_zone_id != ROOT_ZONE_ID:
        if parent_zone_id not in cache:
            cache[parent_zone_id] = f"/{parent_zone_id}/"
        return f"{cache[parent_zone_id]}{entity_id}/"
    return f"/{ROOT_ZONE_ID}/{entity_id}/"


def build_category_paths(
    parent_legacy: dict[int, int | None],
    rows: list[dict],
) -> dict[int, str]:
    """分类节点 tree_path（与 parent 分类 id 一致）。"""
    paths: dict[int, str] = {
        CAT_ROOT_ID: f"/{CAT_ROOT_ID}/",
        CAT_WHGG_ID: f"/{CAT_ROOT_ID}/{CAT_WHGG_ID}/",
    }

    def parent_cat_for_legacy(legacy: int) -> int:
        pl = parent_legacy.get(legacy)
        if pl is None:
            return CAT_WHGG_ID
        return cat_id(pl)

    pending = {int(r["id"]) for r in rows}
    while pending:
        progressed = False
        for legacy in list(pending):
            pc = parent_cat_for_legacy(legacy)
            if pc not in paths and pc not in (CAT_ROOT_ID, CAT_WHGG_ID):
                continue
            cid = cat_id(legacy)
            paths[cid] = f"{paths[pc]}{cid}/"
            pending.remove(legacy)
            progressed = True
        if not progressed:
            break
    for legacy in pending:
        cid = cat_id(legacy)
        paths[cid] = f"/{CAT_ROOT_ID}/{CAT_WHGG_ID}/{cid}/"
    return paths


def category_level(path: str) -> int:
    return path.strip("/").count("/") + 1


def discover_ue_ref_dir() -> Path | None:
    here = Path(__file__).resolve()
    for depth in range(3, 9):
        if depth >= len(here.parents):
            break
        candidate = here.parents[depth] / "参考资料" / "UE数据"
        if candidate.is_dir():
            return candidate
    env = os.environ.get("CORRIDOR_UE_REF_DIR")
    if env:
        p = Path(env)
        if p.is_dir():
            return p
    return None


def parse_fire_number_label(fire_zone_name: str) -> str | None:
    m = re.match(r"^(\d+)#", (fire_zone_name or "").strip())
    return m.group(1) if m else None


def load_fire_zone_reference(ref_dir: Path) -> dict[tuple[str, str], dict[str, object]]:
    path = ref_dir / "防火区列表.json"
    if not path.is_file():
        return {}
    data = json.loads(path.read_text(encoding="utf-8"))
    out: dict[tuple[str, str], dict[str, object]] = {}
    for row in data:
        seg = (row.get("管廊段所在路段") or "").strip()
        fname = (row.get("防火区名称") or "").strip()
        if not seg or not fname:
            continue
        ue_code = (row.get("编号") or "").strip()
        route_len = row.get("路长")
        cf: dict[str, object] = {
            "FLD-UT-zone-segment_road_name": seg,
            "FLD-UT-fire_compartment_no": parse_fire_number_label(fname) or fname,
        }
        if ue_code:
            cf["FLD-UT-zone-ue_entity_code"] = ue_code
            cf["FLD-UT-fire_compartment_code"] = ue_code
        if route_len is not None:
            cf["FLD-UT-route_length_m"] = route_len
        out[(seg, fname)] = cf
    return out


def load_cabin_pipeline_counts(ref_dir: Path) -> dict[tuple[str, str, str], int]:
    path = ref_dir / "管线舱室列表(1).json"
    if not path.is_file():
        return {}
    data = json.loads(path.read_text(encoding="utf-8"))
    counts: dict[tuple[str, str, str], int] = defaultdict(int)
    for row in data:
        seg = (row.get("区域名称") or "").strip()
        cabin = (row.get("舱室") or "").strip()
        fno = row.get("防火区编号")
        if not seg or not cabin or fno is None:
            continue
        counts[(seg, str(int(fno)), cabin)] += 1
    return dict(counts)


def build_zone_custom_fields(
    model_id: int,
    legacy_id: int,
    name: str,
    row: dict,
    parent_legacy: int | None,
    rows_by_id: dict[int, dict],
    parent_legacy_map: dict[int, int | None],
    fire_ref: dict[tuple[str, str], dict[str, object]],
    cabin_pipe_counts: dict[tuple[str, str, str], int],
) -> dict[str, object]:
    cf: dict[str, object] = {}
    biz_code = (row.get("code") or "").strip()
    if biz_code:
        cf["FLD-UT-zone-ue_entity_code"] = biz_code

    if model_id == 73:
        cf["FLD-UT-zone-segment_road_name"] = name
        return cf

    if model_id == 74:
        seg_label, fire_label = resolve_segment_and_fire_labels(
            legacy_id, rows_by_id, parent_legacy_map
        )
        ref = fire_ref.get((seg_label, name)) or fire_ref.get((seg_label, fire_label))
        if ref:
            cf.update(ref)
        return cf

    if model_id in CABIN_ZHGL_MODEL_IDS and parent_legacy is not None:
        seg_label, fire_label = resolve_segment_and_fire_labels(
            parent_legacy, rows_by_id, parent_legacy_map
        )
        fno = parse_fire_number_label(fire_label)
        pipe_label = CABIN_NAME_TO_PIPELINE_LABEL.get(name, name)
        if fno and seg_label:
            cnt = cabin_pipe_counts.get((seg_label, fno, pipe_label))
            if cnt:
                cf["FLD-UT-pipeline_count"] = cnt
        if seg_label:
            cf["FLD-UT-zone-segment_road_name"] = seg_label
    return cf


def main() -> None:
    pg_host = os.environ.get("PGHOST", "127.0.0.1")
    pg_port = os.environ.get("PGPORT", "5432")
    pg_user = os.environ.get("PGUSER", "postgres")
    pg_password = os.environ.get("PGPASSWORD", "Coolhomer")

    zhgl = psycopg2.connect(
        host=pg_host,
        port=pg_port,
        dbname="zhgl_import_temp",
        user=pg_user,
        password=pg_password,
    )
    sinopec = psycopg2.connect(
        host=pg_host,
        port=pg_port,
        dbname="sinopec",
        user=pg_user,
        password=pg_password,
    )

    with zhgl.cursor(cursor_factory=psycopg2.extras.RealDictCursor) as cur:
        cur.execute(
            """
            SELECT sm.id AS model_id, sm.name AS model_name
            FROM system_model sm
            WHERE sm.deleted = false AND sm.id = ANY(%s)
            ORDER BY sm.id
            """,
            (list(MODEL_MAP.keys()),),
        )
        sm_rows = cur.fetchall()
        cur.execute(
            """
            SELECT id, name, code, model_id, sort, tree_path, parent_id
            FROM biz_region
            WHERE deleted = false AND model_id = ANY(%s)
            ORDER BY sort, id
            """,
            (list(MODEL_MAP.keys()),),
        )
        rows = cur.fetchall()
        legacy_ids = {int(r["id"]) for r in rows}
        parent_legacy = load_parent_legacy_from_category(cur, legacy_ids)
        rows_by_id = {int(r["id"]): r for r in rows}
        fire_parent_fixes = normalize_misnested_fire_zone_parents(
            parent_legacy, rows_by_id
        )
        if fire_parent_fixes:
            print("Normalized misnested fire→fire in system_category:", fire_parent_fixes)

    ref_dir = discover_ue_ref_dir()
    fire_ref: dict[tuple[str, str], dict[str, object]] = {}
    cabin_pipe_counts: dict[tuple[str, str, str], int] = {}
    if ref_dir:
        fire_ref = load_fire_zone_reference(ref_dir)
        cabin_pipe_counts = load_cabin_pipeline_counts(ref_dir)
        print(f"Loaded UE refs from {ref_dir}: fires={len(fire_ref)}, cabin_pipe_keys={len(cabin_pipe_counts)}")
    else:
        print("WARN: 参考资料/UE数据 not found; zone custom_fields will be minimal")

    with sinopec.cursor() as cur:
        cur.execute(
            """
            SELECT code, name FROM dynamicbusiness.dynamic_model
            WHERE tenant_id = %s AND entity_type_code = 'zone' AND deleted = false
            """,
            (TENANT_ID,),
        )
        zone_models_by_name = {name: code for code, name in cur.fetchall()}

    unmapped_sm = [
        r
        for r in sm_rows
        if int(r["model_id"]) in ZONE_EMIT_MODEL_IDS
        and r["model_name"] not in zone_models_by_name
    ]
    if unmapped_sm:
        names = ", ".join(f"{r['model_id']}:{r['model_name']}" for r in unmapped_sm)
        raise SystemExit(
            f"tenant {TENANT_ID} 缺少与 zhgl system_model 同名的 zone 模型: {names}"
        )

    cat_paths = build_category_paths(
        parent_legacy,
        [r for r in rows if int(r["model_id"]) in ZONE_EMIT_MODEL_IDS],
    )

    zone_model_codes = [MODEL_MAP[i] for i in sorted(ZONE_EMIT_MODEL_IDS)] + [
        "MODEL-FACILITY-UT-TUNNEL",
        "MODEL-ZONE-UT-CORRIDOR-ROOT",
    ]
    structure_model_codes = [
        structure_model_code(MODEL_MAP[i]) for i in sorted(STRUCTURE_EMIT_MODEL_IDS)
    ]
    model_codes = zone_model_codes + structure_model_codes
    model_ids: dict[str, int] = {}
    with sinopec.cursor() as cur:
        cur.execute(
            """
            SELECT code, id FROM dynamicbusiness.dynamic_model
            WHERE tenant_id = %s AND deleted = false
              AND code = ANY(%s)
            """,
            (TENANT_ID, model_codes),
        )
        for code, mid in cur.fetchall():
            model_ids[code] = mid

    missing = [c for c in model_codes if c not in model_ids]
    if missing:
        raise SystemExit(f"tenant {TENANT_ID} 缺少模型: {missing}")

    keep_zone_codes_sql = ", ".join(sql_str(c) for c in KEEP_ZONE_MODEL_CODES)
    retire_zone_codes_sql = ", ".join(sql_str(c) for c in ZONE_RETIRE_MODEL_CODES)

    out_path = Path(__file__).resolve().parent / "seed" / "dynamic_corridor_spatial_entities.generated.sql"
    lines: list[str] = [
        "-- AUTO-GENERATED by export_corridor_spatial_from_zhgl.py — do not hand-edit",
        f"-- tenant_id={TENANT_ID}, biz_region rows={len(rows)} + root facility + root zone",
        "SET search_path TO dynamicbusiness;",
        "",
        "-- 退役旧导入：多余 facility、未在本次导出 id 范围内的构筑物、facility 段分类",
        f"""
UPDATE ent_facility
SET deleted = true, updater = 'corridor-seed', update_time = CURRENT_TIMESTAMP
WHERE tenant_id = {TENANT_ID} AND deleted = false
  AND id <> {ROOT_FACILITY_ID};
""".strip(),
        f"""
UPDATE ent_structure
SET deleted = true, updater = 'corridor-seed', update_time = CURRENT_TIMESTAMP
WHERE tenant_id = {TENANT_ID} AND deleted = false
  AND (id < {STRUCTURE_BASE} OR id >= {STRUCTURE_BASE + 500_000});
DELETE FROM ent_structure
WHERE tenant_id = {TENANT_ID}
  AND id >= {STRUCTURE_BASE} AND id < {STRUCTURE_BASE + 500_000};
UPDATE ent_zone z
SET deleted = true, updater = 'corridor-seed', update_time = CURRENT_TIMESTAMP
FROM dynamic_model dm
WHERE z.model_id = dm.id AND z.tenant_id = {TENANT_ID} AND z.deleted = false
  AND dm.code NOT IN ({keep_zone_codes_sql});
DELETE FROM ent_zone
WHERE tenant_id = {TENANT_ID}
  AND id >= {ZONE_BASE} AND id < {ZONE_BASE + 500_000};
UPDATE dynamic_model
SET deleted = true, updater = 'corridor-seed', update_time = CURRENT_TIMESTAMP
WHERE tenant_id = {TENANT_ID} AND entity_type_code = 'zone' AND deleted = false
  AND code IN ({retire_zone_codes_sql});
""".strip(),
        f"""
UPDATE dynamic_category
SET deleted = true, updater = 'corridor-seed', update_time = CURRENT_TIMESTAMP
WHERE tenant_id = {TENANT_ID} AND deleted = false
  AND (
    (category_type_code = 'facility' AND (code LIKE 'FAC-CAT%' OR code = 'corridor_facility_root'))
    OR (category_type_code = 'zone' AND (code LIKE 'ZONE-CAT%' OR code IN ('corridor_zone_root', 'zone_root')))
  );
DELETE FROM dynamic_category_entity_link
WHERE tenant_id = {TENANT_ID} AND category_id >= {CAT_ROOT_ID} AND category_id < {CAT_ZONE_BASE + 500000};
DELETE FROM dynamic_category
WHERE tenant_id = {TENANT_ID} AND id >= {CAT_ROOT_ID} AND id < {CAT_ZONE_BASE + 500000};
""".strip(),
        "",
        "-- ---------- zone 分类树（Pattern C） ----------",
        f"""
INSERT INTO dynamic_category (
  id, parent_id, name, code, category_type_code, tree_path, level, sort, status, tenant_id, creator
) VALUES (
  {CAT_ROOT_ID}, NULL, '管廊空间', 'corridor_zone_root', 'zone', '/{CAT_ROOT_ID}/', 1, 0, 1, {TENANT_ID}, 'corridor-seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET name = EXCLUDED.name, parent_id = EXCLUDED.parent_id, tree_path = EXCLUDED.tree_path,
  category_type_code = EXCLUDED.category_type_code, deleted = false,
  updater = 'corridor-seed', update_time = CURRENT_TIMESTAMP;
""".strip(),
        f"""
INSERT INTO dynamic_category (
  id, parent_id, name, code, category_type_code, tree_path, level, sort, status, tenant_id, creator
) VALUES (
  {CAT_WHGG_ID}, {CAT_ROOT_ID}, {sql_str(ROOT_NAME)}, 'ZONE-CAT-WH-GGGL', 'zone',
  '/{CAT_ROOT_ID}/{CAT_WHGG_ID}/', 2, 0, 1, {TENANT_ID}, 'corridor-seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET name = EXCLUDED.name, parent_id = EXCLUDED.parent_id, tree_path = EXCLUDED.tree_path,
  category_type_code = EXCLUDED.category_type_code, deleted = false,
  updater = 'corridor-seed', update_time = CURRENT_TIMESTAMP;
""".strip(),
        f"""
INSERT INTO dynamic_category_type (
  category_type_code, name, description, status, top_level_category_id, tenant_id, creator, category_mode
)
SELECT
  'zone', '管廊空间',
  '武汉光谷管廊及段/防火区/舱室；Pattern C（不设 region；唯一 facility 作 REF 锚点）', 1,
  {CAT_ROOT_ID}, {TENANT_ID}, 'corridor-seed', 'ADVANCED'
WHERE NOT EXISTS (
  SELECT 1 FROM dynamic_category_type
  WHERE category_type_code = 'zone' AND tenant_id = {TENANT_ID} AND deleted = false
);
UPDATE dynamic_category_type
SET top_level_category_id = {CAT_ROOT_ID}, category_mode = 'ADVANCED',
  description = '武汉光谷管廊及段/防火区/舱室；Pattern C',
  updater = 'corridor-seed', update_time = CURRENT_TIMESTAMP
WHERE category_type_code = 'zone' AND tenant_id = {TENANT_ID} AND deleted = false;
""".strip(),
        "",
        f"-- ---------- 唯一 facility：{ROOT_NAME} ----------",
        f"""
INSERT INTO ent_facility (
  id, tenant_id, entity_type_code, model_id, name, code, status, region_id, sort, tree_path, creator
) VALUES (
  {ROOT_FACILITY_ID}, {TENANT_ID}, 'facility', {model_ids['MODEL-FACILITY-UT-TUNNEL']},
  {sql_str(ROOT_NAME)}, {sql_str(ROOT_FACILITY_CODE)}, 1, NULL, 0, '/{ROOT_FACILITY_ID}/', 'corridor-seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false AND code IS NOT NULL
DO UPDATE SET name = EXCLUDED.name, model_id = EXCLUDED.model_id, region_id = NULL,
  deleted = false, updater = 'corridor-seed', update_time = CURRENT_TIMESTAMP;
""".strip(),
        "",
        f"-- ---------- 根 zone：{ROOT_NAME} ----------",
        f"""
INSERT INTO ent_zone (
  id, tenant_id, entity_type_code, model_id, name, code, status, facility_id, parent_id, sort, tree_path, creator
) VALUES (
  {ROOT_ZONE_ID}, {TENANT_ID}, 'zone', {model_ids['MODEL-ZONE-UT-CORRIDOR-ROOT']},
  {sql_str(ROOT_NAME)}, {sql_str(ROOT_ZONE_CODE)}, 1, {ROOT_FACILITY_ID}, 0, 0,
  '/{ROOT_ZONE_ID}/', 'corridor-seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false AND code IS NOT NULL
DO UPDATE SET name = EXCLUDED.name, model_id = EXCLUDED.model_id, facility_id = EXCLUDED.facility_id,
  parent_id = 0, deleted = false, updater = 'corridor-seed', update_time = CURRENT_TIMESTAMP;
""".strip(),
        f"""
INSERT INTO dynamic_category_entity_link (
  id, category_id, entity_id, entity_model_id, entity_type_code, tenant_id, creator
) VALUES (
  {LINK_BASE}, {CAT_WHGG_ID}, {ROOT_ZONE_ID}, {model_ids['MODEL-ZONE-UT-CORRIDOR-ROOT']}, 'zone', {TENANT_ID}, 'corridor-seed'
)
ON CONFLICT DO NOTHING;
""".strip(),
        "",
    ]

    link_id = LINK_BASE + 1
    zone_count = 0
    seg_count = 0
    fire_count = 0
    cabin_count = 0
    structure_count = 0

    emit_rows = sorted(rows, key=lambda r: (int(r["sort"] or 0), int(r["id"])))
    cabins_by_fire = build_cabins_by_fire_legacy(rows_by_id, parent_legacy)
    in_cabin_assignments = build_in_cabin_structure_assignments(
        rows, parent_legacy, rows_by_id, cabins_by_fire
    )
    zone_path_cache: dict[int, str] = {ROOT_ZONE_ID: f"/{ROOT_ZONE_ID}/"}
    sump_cabin_miss = 0

    for row in emit_rows:
        legacy_id = int(row["id"])
        name = (row["name"] or "").strip() or f"未命名-{legacy_id}"
        model_id = int(row["model_id"])
        model_code = MODEL_MAP[model_id]
        sort = int(row["sort"] or 0)
        attrs_obj = {"legacy_biz_region_id": legacy_id}
        attrs = json.dumps(attrs_obj, ensure_ascii=False)
        pl = parent_legacy.get(legacy_id)

        if model_id in ZONE_EMIT_MODEL_IDS:
            custom_fields_obj = build_zone_custom_fields(
                model_id,
                legacy_id,
                name,
                row,
                pl,
                rows_by_id,
                parent_legacy,
                fire_ref,
                cabin_pipe_counts,
            )
            custom_fields = json.dumps(custom_fields_obj, ensure_ascii=False)
            ent_code = (row.get("code") or "").strip() or f"ZONE-{legacy_id}"
            dm_id = model_ids[model_code]
            eid = ent_id(legacy_id)
            cid = cat_id(legacy_id)
            code = ent_code
            cat_code = f"ZONE-CAT-{legacy_id}"

            if model_id == 73:
                seg_count += 1
                parent_zone_id = ROOT_ZONE_ID
                parent_cat = CAT_WHGG_ID
            elif model_id == 74:
                fire_count += 1
                if pl is None:
                    parent_zone_id = ROOT_ZONE_ID
                    parent_cat = CAT_WHGG_ID
                else:
                    parent_zone_id = ent_id(pl)
                    parent_cat = cat_id(pl)
            else:
                cabin_count += 1
                if pl is None:
                    raise SystemExit(f"舱室 legacy={legacy_id} 无父级（system_category）")
                parent_zone_id = ent_id(pl)
                parent_cat = cat_id(pl)

            cpath = cat_paths.get(cid, f"/{CAT_ROOT_ID}/{CAT_WHGG_ID}/{cid}/")
            clevel = category_level(cpath)
            zpath = zone_tree_path(eid, parent_zone_id, zone_path_cache)
            zone_path_cache[eid] = zpath
            zone_count += 1

            lines.append(
                f"-- zone · {name} (legacy {legacy_id}, parent_legacy={pl}, {model_code})"
            )
            lines.append(
                f"""
INSERT INTO dynamic_category (
  id, parent_id, name, code, category_type_code, tree_path, level, sort, status, tenant_id, creator
) VALUES (
  {cid}, {parent_cat}, {sql_str(name)}, {sql_str(cat_code)}, 'zone',
  {sql_str(cpath)}, {clevel}, {sort}, 1, {TENANT_ID}, 'corridor-seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET name = EXCLUDED.name, parent_id = EXCLUDED.parent_id, tree_path = EXCLUDED.tree_path,
  level = EXCLUDED.level, deleted = false, updater = 'corridor-seed', update_time = CURRENT_TIMESTAMP;
""".strip()
            )
            lines.append(
                f"""
INSERT INTO ent_zone (
  id, tenant_id, entity_type_code, model_id, name, code, status, facility_id, parent_id, sort, tree_path, attrs, custom_fields, creator
) VALUES (
  {eid}, {TENANT_ID}, 'zone', {dm_id}, {sql_str(name)}, {sql_str(code)}, 1,
  {ROOT_FACILITY_ID}, {parent_zone_id}, {sort}, {sql_str(zpath)}, '{attrs}'::jsonb, '{custom_fields}'::jsonb, 'corridor-seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false AND code IS NOT NULL
DO UPDATE SET name = EXCLUDED.name, model_id = EXCLUDED.model_id, facility_id = EXCLUDED.facility_id,
  parent_id = EXCLUDED.parent_id, attrs = EXCLUDED.attrs, custom_fields = EXCLUDED.custom_fields, deleted = false,
  updater = 'corridor-seed', update_time = CURRENT_TIMESTAMP;
""".strip()
            )
            lines.append(
                f"""
INSERT INTO dynamic_category_entity_link (
  id, category_id, entity_id, entity_model_id, entity_type_code, tenant_id, creator
) VALUES (
  {link_id}, {cid}, {eid}, {dm_id}, 'zone', {TENANT_ID}, 'corridor-seed'
)
ON CONFLICT DO NOTHING;
""".strip()
            )
            link_id += 1
            continue

        if model_id not in STRUCTURE_EMIT_MODEL_IDS:
            continue

        if pl is None:
            raise SystemExit(f"构筑物 legacy={legacy_id} 无父防火区（system_category）")

        sm_code = structure_model_code(model_code)
        sm_id = model_ids[sm_code]
        sid = structure_ent_id(legacy_id)
        zone_legacy, attached_cabin = resolve_structure_zone_legacy(
            legacy_id, pl, model_id, in_cabin_assignments
        )
        if model_id in STRUCTURE_ZONE_IN_CABIN_ZHGL_MODEL_IDS and not attached_cabin:
            sump_cabin_miss += 1
        structure_zone_id = ent_id(zone_legacy)
        seg_label, fire_label = resolve_segment_and_fire_labels(pl, rows_by_id, parent_legacy)
        cabin_label: str | None = None
        if attached_cabin:
            cabin_row = rows_by_id[zone_legacy]
            cabin_label = (cabin_row.get("name") or "").strip() or f"舱室-{zone_legacy}"
        type_label = name.strip() or sm_code.removeprefix("MODEL-STRUCTURE-UT-")
        display_name = structure_display_name(
            seg_label, fire_label, type_label, legacy_id, cabin_label
        )
        scode = structure_business_code(pl, legacy_id)
        attrs_obj: dict = {
            "legacy_biz_region_id": legacy_id,
            "fire_zone_legacy_id": pl,
            "segment_label": seg_label,
            "fire_zone_label": fire_label,
        }
        if attached_cabin:
            attrs_obj["cabin_zone_legacy_id"] = zone_legacy
            attrs_obj["cabin_zone_label"] = cabin_label
        attrs = json.dumps(attrs_obj, ensure_ascii=False)
        structure_count += 1
        zone_note = (
            f"舱室 zone {structure_zone_id}"
            if attached_cabin
            else f"防火区 zone {structure_zone_id}"
        )
        lines.append(
            f"-- structure · {display_name} → {zone_note} ({sm_code})"
        )
        lines.append(
            f"""
INSERT INTO ent_structure (
  id, tenant_id, entity_type_code, model_id, name, code, status,
  facility_id, zone_id, parent_id, sort, attrs, creator
) VALUES (
  {sid}, {TENANT_ID}, 'structure', {sm_id}, {sql_str(display_name)}, {sql_str(scode)}, 1,
  {ROOT_FACILITY_ID}, {structure_zone_id}, 0, {sort}, '{attrs}'::jsonb, 'corridor-seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false AND code IS NOT NULL
DO UPDATE SET name = EXCLUDED.name, model_id = EXCLUDED.model_id,
  facility_id = EXCLUDED.facility_id, zone_id = EXCLUDED.zone_id,
  sort = EXCLUDED.sort, attrs = EXCLUDED.attrs, deleted = false,
  updater = 'corridor-seed', update_time = CURRENT_TIMESTAMP;
""".strip()
        )

    lines.extend(
        [
            "",
            "SELECT setval('dynamicbusiness.ent_facility_id_seq', (SELECT COALESCE(MAX(id),1) FROM dynamicbusiness.ent_facility), true);",
            "SELECT setval('dynamicbusiness.ent_zone_id_seq', (SELECT COALESCE(MAX(id),1) FROM dynamicbusiness.ent_zone), true);",
            "SELECT setval('dynamicbusiness.ent_structure_id_seq', (SELECT COALESCE(MAX(id),1) FROM dynamicbusiness.ent_structure), true);",
            "SELECT setval('dynamicbusiness.dynamic_category_id_seq', (SELECT COALESCE(MAX(id),1) FROM dynamicbusiness.dynamic_category), true);",
            "",
            f"-- summary: facility=1, root_zone=1, zone={zone_count} (seg={seg_count}, fire={fire_count}, cabin={cabin_count}), structure={structure_count}, sump_no_cabin={sump_cabin_miss}",
        ]
    )

    out_path.write_text("\n\n".join(lines) + "\n", encoding="utf-8")
    print(f"Wrote {out_path}")
    print(
        f"zone={zone_count}, seg={seg_count}, fire={fire_count}, cabin={cabin_count}, "
        f"structure={structure_count}, sump_no_cabin={sump_cabin_miss}"
    )

    zhgl.close()
    sinopec.close()


if __name__ == "__main__":
    main()
