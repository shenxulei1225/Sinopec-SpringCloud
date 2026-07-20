#!/usr/bin/env python3
"""
按实例 gps_lng/gps_lat + 站心，用 ENU 米制重算 transform.location.x/z。

- 不改 gps_*、rotation、scale、location.y
- 站心优先 coordinate_reference.origin_lng/lat，否则设施经纬度 / 场景默认

用法：
  python recompute_actor_transform_enu_from_gps.py --dry-run
  python recompute_actor_transform_enu_from_gps.py
  python recompute_actor_transform_enu_from_gps.py --scene SCENE-JINQIAO
"""

from __future__ import annotations

import argparse
import json
import math
import sys
from pathlib import Path

import psycopg2
from psycopg2.extras import Json

sys.path.insert(0, str(Path(__file__).resolve().parent))
from mercator_scene import lon_lat_to_scene  # noqa: E402

DEFAULT_SCENES = ("SCENE-JINQIAO", "SCENE-LUOYANG-SHENGRUI")


def haversine_m(lon1: float, lat1: float, lon2: float, lat2: float) -> float:
    r = 6371000.0
    p1, p2 = math.radians(lat1), math.radians(lat2)
    dp = math.radians(lat2 - lat1)
    dl = math.radians(lon2 - lon1)
    a = math.sin(dp / 2) ** 2 + math.cos(p1) * math.cos(p2) * math.sin(dl / 2) ** 2
    return 2 * r * math.asin(math.sqrt(a))


def resolve_origin(cur, scene_id: int, scene_code: str) -> tuple[float, float]:
    cur.execute(
        """
        SELECT origin_lng, origin_lat
        FROM scene_platform.coordinate_reference
        WHERE scene_id = %s AND deleted = false
        LIMIT 1
        """,
        (scene_id,),
    )
    row = cur.fetchone()
    if row and row[0] is not None and row[1] is not None:
        return float(row[0]), float(row[1])

    cur.execute(
        """
        SELECT f.longitude, f.latitude
        FROM scene_platform.facility_scene_binding b
        JOIN dynamicbusiness.ent_facility f ON f.id = b.facility_id AND f.deleted = false
        WHERE b.scene_id = %s AND b.deleted = false
        LIMIT 1
        """,
        (scene_id,),
    )
    row = cur.fetchone()
    if row and row[0] is not None and row[1] is not None:
        return float(row[0]), float(row[1])

    # 已知种子
    defaults = {
        "SCENE-JINQIAO": (117.238056, 34.303056),
        "SCENE-LUOYANG-SHENGRUI": (112.454167, 34.619722),
    }
    if scene_code in defaults:
        return defaults[scene_code]
    raise RuntimeError(f"无法解析站心: scene={scene_code}")


def main() -> int:
    ap = argparse.ArgumentParser()
    ap.add_argument("--dsn", default="host=127.0.0.1 dbname=sinopec user=postgres password=Coolhomer")
    ap.add_argument("--scene", action="append", dest="scenes", help="可多次；默认金桥+洛阳")
    ap.add_argument("--dry-run", action="store_true")
    args = ap.parse_args()
    scenes = tuple(args.scenes) if args.scenes else DEFAULT_SCENES

    conn = psycopg2.connect(args.dsn)
    conn.autocommit = False
    try:
        with conn.cursor() as cur:
            for scene_code in scenes:
                cur.execute(
                    """
                    SELECT id FROM scene_platform.scene
                    WHERE scene_code = %s AND deleted = false
                    """,
                    (scene_code,),
                )
                row = cur.fetchone()
                if not row:
                    print(f"skip missing scene {scene_code}")
                    continue
                scene_id = int(row[0])
                origin_lon, origin_lat = resolve_origin(cur, scene_id, scene_code)
                print(
                    f"== {scene_code} id={scene_id} "
                    f"origin=({origin_lon:.6f},{origin_lat:.6f}) "
                    f"sec(lat)={1 / math.cos(math.radians(origin_lat)):.4f}"
                )

                cur.execute(
                    """
                    SELECT id, instance_code, transform, gps_lng, gps_lat
                    FROM scene_platform.actor_instance
                    WHERE scene_id = %s AND deleted = false
                      AND gps_lng IS NOT NULL AND gps_lat IS NOT NULL
                    """,
                    (scene_id,),
                )
                rows = cur.fetchall()
                updated = 0
                unchanged = 0
                samples: list[tuple[str, float, float, float, float]] = []

                for instance_id, instance_code, transform, gps_lng, gps_lat in rows:
                    lon, lat = float(gps_lng), float(gps_lat)
                    tf = transform
                    if isinstance(tf, str):
                        tf = json.loads(tf)
                    if not isinstance(tf, dict):
                        tf = {}
                    loc = dict(tf.get("location") or {})
                    old_x = float(loc.get("x") or 0)
                    old_z = float(loc.get("z") or 0)
                    y = float(loc.get("y") or 0)

                    x, _y, z = lon_lat_to_scene(
                        lon, lat, origin_lon=origin_lon, origin_lat=origin_lat, height=y
                    )
                    if abs(old_x - x) < 1e-6 and abs(old_z - z) < 1e-6:
                        unchanged += 1
                        continue

                    if len(samples) < 3:
                        samples.append((instance_code, old_x, old_z, x, z))

                    new_tf = {
                        **tf,
                        "location": {"x": x, "y": y, "z": z},
                    }
                    if args.dry_run:
                        updated += 1
                        continue

                    cur.execute(
                        """
                        UPDATE scene_platform.actor_instance
                        SET transform = %s::jsonb,
                            update_time = CURRENT_TIMESTAMP,
                            updater = 'enu-recompute-from-gps'
                        WHERE id = %s
                        """,
                        (Json(new_tf), instance_id),
                    )
                    updated += 1

                for code, ox, oz, nx, nz in samples:
                    print(
                        f"  sample {code}: "
                        f"({ox:.2f},{oz:.2f}) -> ({nx:.2f},{nz:.2f})"
                    )
                print(
                    f"  with_gps={len(rows)} would_update={updated} "
                    f"unchanged={unchanged} dry_run={args.dry_run}"
                )

                # 校验：两栋已知房的局部距 ≈ 大圆距
                cur.execute(
                    """
                    SELECT instance_code, gps_lng::float, gps_lat::float,
                      (transform->'location'->>'x')::float,
                      (transform->'location'->>'z')::float
                    FROM scene_platform.actor_instance
                    WHERE scene_id = %s AND deleted = false
                      AND instance_code IN (
                        'legacy_bs_3d_house_1', 'legacy_bs_3d_house_4',
                        'legacy_bs_3d_pot_1', 'legacy_bs_3d_pot_11'
                      )
                    ORDER BY instance_code
                    """,
                    (scene_id,),
                )
                pts = {
                    r[0]: (float(r[1]), float(r[2]), float(r[3]), float(r[4]))
                    for r in cur.fetchall()
                }
                # dry-run 时 transform 未写回，用公式现算
                if args.dry_run:
                    for code, (lon, lat, _x, _z) in list(pts.items()):
                        x, _y, z = lon_lat_to_scene(
                            lon, lat, origin_lon=origin_lon, origin_lat=origin_lat
                        )
                        pts[code] = (lon, lat, x, z)

                pairs = [
                    ("legacy_bs_3d_house_1", "legacy_bs_3d_house_4"),
                    ("legacy_bs_3d_pot_1", "legacy_bs_3d_pot_11"),
                ]
                for a, b in pairs:
                    if a not in pts or b not in pts:
                        continue
                    lon1, lat1, x1, z1 = pts[a]
                    lon2, lat2, x2, z2 = pts[b]
                    scene_d = math.hypot(x1 - x2, z1 - z2)
                    gps_d = haversine_m(lon1, lat1, lon2, lat2)
                    ratio = scene_d / gps_d if gps_d > 0 else float("nan")
                    print(
                        f"  verify {a}↔{b}: scene={scene_d:.2f}m "
                        f"haversine={gps_d:.2f}m ratio={ratio:.4f}"
                    )

        if args.dry_run:
            conn.rollback()
            print("[dry-run] rolled back")
        else:
            conn.commit()
            print("committed")
    finally:
        conn.close()
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
