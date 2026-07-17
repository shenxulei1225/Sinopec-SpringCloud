#!/usr/bin/env python3
"""
按老栈规则回填 actor_instance 的 scale / rotation / 高度。

房屋（applyHouseOrientation）：
  - orientation.rotation 为「度」
  - orientation.scale 为倍率
  - 位置 Y 固定 2.1（与 initHouse 一致）

储罐（modelSize + applyAssetOrientation）：
  - scale = targetSize / meshBBox，target=[radius*2, real_height, radius*2]
  - orientation.rotation 为「弧度」（有则转成平台度制写入）
  - 位置 Y = planeness

用法：
  python3 refit_legacy_transforms.py \\
    --dump /Users/kevin/Sinopec/洛阳数据/pre-dev-full-20260711_114237.sql.gz \\
    --models-root .../public/scene-models
"""

from __future__ import annotations

import argparse
import json
import math
import re
import struct
import sys
from pathlib import Path
from typing import Any

import psycopg2
from psycopg2.extras import Json

from mercator_scene import lon_lat_to_scene

# 与 reconvert_legacy_models 共用的 dump 解析（精简拷贝）
INSERT_VALUES_ONLY_RE = re.compile(
    r"^INSERT INTO `(?P<table>[^`]+)` VALUES (?P<values>.+);?\s*$"
)
CREATE_TABLE_RE = re.compile(r"^CREATE TABLE `(?P<table>[^`]+)` \(")

HOUSE_Y = 2.1

# Three.js Box3 实测（勿用含 NORMAL 的 accessor 聚合，会把 Y 撑高约 1）
DEFAULT_MESH_SIZE = {
    "oiltank": (18.7273, 15.3138, 18.7389),
    "house1": (74.7095, 23.8066, 19.5494),
    "house2": (90.2415, 12.9311, 35.1964),
    "house3": (39.9677, 9.6609, 21.8231),
}


def open_text(path: Path):
    import gzip

    if str(path).endswith(".gz"):
        with gzip.open(path, "rt", encoding="utf-8", errors="replace") as f:
            yield from f
    else:
        with path.open("rt", encoding="utf-8", errors="replace") as f:
            yield from f


def split_mysql_tuples(values_blob: str) -> list[str]:
    rows: list[str] = []
    depth = 0
    start = None
    in_str = False
    escape = False
    for i, ch in enumerate(values_blob):
        if in_str:
            if escape:
                escape = False
            elif ch == "\\":
                escape = True
            elif ch == "'":
                in_str = False
            continue
        if ch == "'":
            in_str = True
            continue
        if ch == "(":
            if depth == 0:
                start = i
            depth += 1
        elif ch == ")":
            depth -= 1
            if depth == 0 and start is not None:
                rows.append(values_blob[start : i + 1])
                start = None
    return rows


def parse_mysql_tuple(raw: str) -> list[Any]:
    inner = raw.strip()
    if inner.startswith("(") and inner.endswith(")"):
        inner = inner[1:-1]
    vals: list[Any] = []
    cur: list[str] = []
    in_str = False
    escape = False
    for ch in inner:
        if in_str:
            cur.append(ch)
            if escape:
                escape = False
            elif ch == "\\":
                escape = True
            elif ch == "'":
                in_str = False
            continue
        if ch == "'":
            in_str = True
            cur.append(ch)
            continue
        if ch == ",":
            vals.append(_scalar("".join(cur).strip()))
            cur = []
            continue
        cur.append(ch)
    if cur or vals:
        vals.append(_scalar("".join(cur).strip()))
    return vals


def _scalar(token: str) -> Any:
    if token.upper() == "NULL" or token == "":
        return None
    if token.startswith("'") and token.endswith("'"):
        return (
            token[1:-1]
            .replace("\\'", "'")
            .replace('\\"', '"')
            .replace("\\\\", "\\")
        )
    try:
        if "." in token:
            return float(token)
        return int(token)
    except ValueError:
        return token


def load_table_columns(dump: Path, table: str) -> list[str]:
    cols: list[str] = []
    in_table = False
    for line in open_text(dump):
        m = CREATE_TABLE_RE.match(line.strip())
        if m and m.group("table") == table:
            in_table = True
            continue
        if not in_table:
            continue
        if line.strip().startswith(")"):
            break
        cm = re.match(r"\s*`([^`]+)`", line)
        if cm:
            cols.append(cm.group(1))
    return cols


def iter_table_rows(dump: Path, table: str):
    cols = load_table_columns(dump, table)
    if not cols:
        raise RuntimeError(f"missing table {table}")
    for line in open_text(dump):
        m = INSERT_VALUES_ONLY_RE.match(line.strip())
        if not m or m.group("table") != table:
            continue
        for tup in split_mysql_tuples(m.group("values")):
            vals = parse_mysql_tuple(tup)
            if len(vals) < len(cols):
                continue
            yield dict(zip(cols, vals[: len(cols)]))


def parse_orientation(raw: Any) -> dict[str, Any] | None:
    if raw is None:
        return None
    if isinstance(raw, dict):
        return raw
    s = str(raw).strip()
    if not s or s.upper() == "NULL":
        return None
    try:
        return json.loads(s)
    except json.JSONDecodeError:
        return None


def glb_size(path: Path) -> tuple[float, float, float] | None:
    """只读 POSITION accessor；混入 NORMAL 会把包围盒 Y 错误撑高。"""
    if not path.is_file():
        return None
    data = path.read_bytes()
    if len(data) < 20:
        return None
    jl = struct.unpack_from("<I", data, 12)[0]
    doc = json.loads(data[20 : 20 + jl].decode("utf-8"))
    accessors = doc.get("accessors") or []
    pos_indices: set[int] = set()
    for mesh in doc.get("meshes") or []:
        for prim in mesh.get("primitives") or []:
            attrs = prim.get("attributes") or {}
            pos = attrs.get("POSITION")
            if isinstance(pos, int):
                pos_indices.add(pos)
    mins: list[list[float]] = []
    maxs: list[list[float]] = []
    for i in pos_indices:
        if i < 0 or i >= len(accessors):
            continue
        a = accessors[i]
        if a.get("type") == "VEC3" and "min" in a and "max" in a:
            mins.append(a["min"])
            maxs.append(a["max"])
    if not mins:
        return None
    mn = [min(m[i] for m in mins) for i in range(3)]
    mx = [max(m[i] for m in maxs) for i in range(3)]
    return (mx[0] - mn[0], mx[1] - mn[1], mx[2] - mn[2])


def model_size(mesh_size: tuple[float, float, float], target: tuple[float, float, float]) -> dict[str, float]:
    """对齐老栈 map.js modelSize。"""
    sx = target[0] / mesh_size[0] if mesh_size[0] else 1.0
    sy = target[1] / mesh_size[1] if mesh_size[1] else 1.0
    sz = target[2] / mesh_size[2] if mesh_size[2] else 1.0
    return {"x": sx, "y": sy, "z": sz}


def safe_float(v: Any, default: float = 0.0) -> float:
    try:
        if v is None or v == "":
            return default
        return float(v)
    except (TypeError, ValueError):
        return default


def house_transform(
    row: dict,
    *,
    origin_lon: float,
    origin_lat: float,
) -> dict:
    lon = safe_float(row.get("longitude"))
    lat = safe_float(row.get("latitude"))
    x, _y, z = lon_lat_to_scene(lon, lat, origin_lon=origin_lon, origin_lat=origin_lat, height=HOUSE_Y)
    pitch = yaw = roll = 0.0
    scale = {"x": 1.0, "y": 1.0, "z": 1.0}
    orient = parse_orientation(row.get("orientation"))
    if orient:
        rot = orient.get("rotation") or {}
        pitch = safe_float(rot.get("x"))
        yaw = safe_float(rot.get("y"))
        roll = safe_float(rot.get("z"))
        sc = orient.get("scale") or {}
        if sc:
            scale = {
                "x": safe_float(sc.get("x"), 1.0),
                "y": safe_float(sc.get("y"), 1.0),
                "z": safe_float(sc.get("z"), 1.0),
            }
    return {
        "location": {"x": x, "y": HOUSE_Y, "z": z},
        "rotation": {"pitch": pitch, "yaw": yaw, "roll": roll},
        "scale": scale,
    }


def pot_transform(
    row: dict,
    mesh_size: tuple[float, float, float],
    *,
    origin_lon: float,
    origin_lat: float,
) -> dict:
    lon = safe_float(row.get("longitude"))
    lat = safe_float(row.get("latitude"))
    planeness = safe_float(row.get("planeness"), 0.0)
    x, _y, z = lon_lat_to_scene(lon, lat, origin_lon=origin_lon, origin_lat=origin_lat, height=planeness)

    radius = safe_float(row.get("radius"), 0.0)
    real_h = safe_float(row.get("real_height"), 0.0)
    target = (radius * 2.0, real_h if real_h > 0 else mesh_size[1], radius * 2.0)
    if radius <= 0:
        scale = {"x": 1.0, "y": 1.0, "z": 1.0}
    else:
        scale = model_size(mesh_size, target)

    pitch = yaw = roll = 0.0
    orient = parse_orientation(row.get("orientation"))
    if orient and orient.get("rotation"):
        rot = orient["rotation"]
        pitch = math.degrees(safe_float(rot.get("x")))
        yaw = math.degrees(safe_float(rot.get("y")))
        roll = math.degrees(safe_float(rot.get("z")))
    if orient and orient.get("scale"):
        sc = orient["scale"]
        scale = {
            "x": safe_float(sc.get("x"), scale["x"]),
            "y": safe_float(sc.get("y"), scale["y"]),
            "z": safe_float(sc.get("z"), scale["z"]),
        }

    return {
        "location": {"x": x, "y": planeness, "z": z},
        "rotation": {"pitch": pitch, "yaw": yaw, "roll": roll},
        "scale": scale,
    }


def load_oil_depot_origins(dump: Path) -> dict[str, tuple[float, float]]:
    out: dict[str, tuple[float, float]] = {}
    for row in iter_table_rows(dump, "bs_3d_oil_depot"):
        site = str(row.get("site_id") or "")
        if not site or site in out:
            continue
        lon = row.get("longitude")
        lat = row.get("latitude")
        if lon is None or lat is None or lon == "" or lat == "":
            continue
        out[site] = (float(lon), float(lat))
    return out



def parse_asset_code(code: str) -> tuple[str, str, str] | None:
    m = re.match(r"^LEGACY-([A-Z_]+)-(\d+)-(\d+)$", code)
    if not m:
        return None
    return m.group(1), m.group(2), m.group(3)


def main() -> int:
    ap = argparse.ArgumentParser()
    ap.add_argument("--dump", type=Path, required=True)
    ap.add_argument("--models-root", type=Path, required=True, help="ecs-react/public/scene-models")
    ap.add_argument("--db-url", default="postgresql://postgres:Coolhomer@127.0.0.1:5432/sinopec")
    ap.add_argument("--dry-run", action="store_true")
    args = ap.parse_args()

    oiltank_glb = args.models_root / "oiltank" / "model.glb"
    mesh = glb_size(oiltank_glb) or DEFAULT_MESH_SIZE["oiltank"]
    print(f"oiltank mesh size = {mesh}")

    houses = {(str(r["site_id"]), str(r["id"])): r for r in iter_table_rows(args.dump, "bs_3d_house")}
    pots = {(str(r["site_id"]), str(r["id"])): r for r in iter_table_rows(args.dump, "bs_3d_pot")}
    origins = load_oil_depot_origins(args.dump)
    print(f"dump houses={len(houses)} pots={len(pots)} origins={origins}")

    conn = psycopg2.connect(args.db_url)
    updated = 0
    skipped = 0
    try:
        with conn.cursor() as cur:
            cur.execute(
                """
                SELECT id, instance_code, transform, metadata_json->>'renderAssetCode'
                FROM scene_platform.actor_instance
                WHERE deleted = false
                  AND metadata_json->>'renderAssetCode' LIKE 'LEGACY-%%'
                """
            )
            rows = cur.fetchall()
            for actor_id, instance_code, transform, asset_code in rows:
                parsed = parse_asset_code(asset_code or "")
                if not parsed:
                    skipped += 1
                    continue
                asset_type, site_id, legacy_id = parsed
                origin = origins.get(site_id)
                if not origin:
                    print(f"  skip {instance_code}: no oil-depot origin for site {site_id}")
                    skipped += 1
                    continue
                origin_lon, origin_lat = origin

                if asset_type == "BUILDING":
                    row = houses.get((site_id, legacy_id))
                    if not row:
                        skipped += 1
                        continue
                    new_tf = house_transform(row, origin_lon=origin_lon, origin_lat=origin_lat)
                elif asset_type == "TANK":
                    row = pots.get((site_id, legacy_id))
                    if not row:
                        skipped += 1
                        continue
                    new_tf = pot_transform(row, mesh, origin_lon=origin_lon, origin_lat=origin_lat)
                else:
                    skipped += 1
                    continue

                if args.dry_run:
                    loc = new_tf["location"]
                    print(
                        f"  [dry] {instance_code} -> "
                        f"({loc['x']:.2f},{loc['y']:.2f},{loc['z']:.2f}) "
                        f"scale={new_tf['scale']} yaw={new_tf['rotation']['yaw']}"
                    )
                else:
                    cur.execute(
                        """
                        UPDATE scene_platform.actor_instance
                        SET transform = %s::jsonb,
                            update_time = CURRENT_TIMESTAMP,
                            updater = 'legacy-refit-transform'
                        WHERE id = %s
                        """,
                        (Json(new_tf), actor_id),
                    )
                updated += 1
        if not args.dry_run:
            conn.commit()
        print(f"done updated={updated} skipped={skipped}")
    except Exception:
        conn.rollback()
        raise
    finally:
        conn.close()
    return 0


if __name__ == "__main__":
    sys.exit(main())
