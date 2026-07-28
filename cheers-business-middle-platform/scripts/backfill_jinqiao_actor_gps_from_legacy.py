#!/usr/bin/env python3
"""
从老 MySQL dump 回填金桥站场 actor_instance 的 gps_lng / gps_lat。

- bs_3d_house / bs_3d_pot：用 dump 原始经纬度（instance_code = legacy_{table}_{id}）
- wall/road 段：无行级 GPS 时，用站心 + transform.location 反算墨卡托
- 不写 gps_height（留给地形采样核对页）

用法：
  python backfill_jinqiao_actor_gps_from_legacy.py \\
    --dump E:/Sino/pre-dev-full-20260711_114237.sql.gz \\
    --dry-run

  python backfill_jinqiao_actor_gps_from_legacy.py \\
    --dump E:/Sino/pre-dev-full-20260711_114237.sql.gz
"""

from __future__ import annotations

import argparse
import json
import re
import sys
from pathlib import Path
from typing import Any

import psycopg2

sys.path.insert(0, str(Path(__file__).resolve().parent))
from mercator_scene import scene_to_lon_lat  # noqa: E402
from refit_legacy_transforms import iter_table_rows, load_oil_depot_origins  # noqa: E402

LEGACY_FACILITY_ID = "121212"
SCENE_CODE = "SCENE-JINQIAO"
# dump 表 → instance_code 前缀
DIRECT_TABLES = {
    "bs_3d_house": "legacy_bs_3d_house_",
    "bs_3d_pot": "legacy_bs_3d_pot_",
    "bs_asset": "legacy_bs_asset_",
}


def safe_float(v: Any) -> float | None:
    try:
        if v is None or v == "":
            return None
        return float(v)
    except (TypeError, ValueError):
        return None


def load_direct_gps(dump: Path) -> dict[str, tuple[float, float]]:
    """instance_code -> (lon, lat) from dump tables with longitude/latitude."""
    out: dict[str, tuple[float, float]] = {}
    for table, prefix in DIRECT_TABLES.items():
        for row in iter_table_rows(dump, table):
            if str(row.get("facility_id") or row.get("site_id") or row.get("facilityId") or row.get("siteId") or "") != LEGACY_FACILITY_ID:
                continue
            legacy_id = row.get("id")
            if legacy_id is None:
                continue
            lon = safe_float(row.get("longitude") or row.get("lon"))
            lat = safe_float(row.get("latitude") or row.get("lat"))
            if lon is None or lat is None:
                continue
            code = f"{prefix}{legacy_id}"
            out[code] = (lon, lat)
    return out


def main() -> int:
    ap = argparse.ArgumentParser()
    ap.add_argument(
        "--dump",
        type=Path,
        default=Path(r"E:/Sino/pre-dev-full-20260711_114237.sql.gz"),
    )
    ap.add_argument("--dsn", default="host=127.0.0.1 dbname=sinopec user=postgres password=Coolhomer")
    ap.add_argument("--dry-run", action="store_true")
    ap.add_argument(
        "--origin-lon",
        type=float,
        default=None,
        help="站心经度；默认用 dump 油库原点",
    )
    ap.add_argument("--origin-lat", type=float, default=None)
    args = ap.parse_args()

    if not args.dump.exists():
        print(f"dump 不存在: {args.dump}", file=sys.stderr)
        return 2

    direct = load_direct_gps(args.dump)
    print(f"dump 直读 GPS: {len(direct)} 条 (house/pot/asset)")

    origins = load_oil_depot_origins(args.dump)
    origin = origins.get(LEGACY_FACILITY_ID)
    if args.origin_lon is not None and args.origin_lat is not None:
        origin_lon, origin_lat = args.origin_lon, args.origin_lat
    elif origin:
        origin_lon, origin_lat = origin
    else:
        # 与 facility seed 一致
        origin_lon, origin_lat = 117.238056, 34.303056
    print(f"站心 lon={origin_lon:.6f} lat={origin_lat:.6f}")

    conn = psycopg2.connect(args.dsn)
    conn.autocommit = False
    updated_direct = 0
    updated_reverse = 0
    missing = 0
    unchanged = 0

    try:
        with conn.cursor() as cur:
            cur.execute(
                """
                SELECT ai.id, ai.instance_code, ai.transform, ai.gps_lng, ai.gps_lat
                FROM scene_platform.actor_instance ai
                JOIN scene_platform.scene s ON s.id = ai.scene_id
                WHERE s.scene_code = %s AND ai.deleted = false
                """,
                (SCENE_CODE,),
            )
            rows = cur.fetchall()
            print(f"金桥场景实例: {len(rows)}")

            for instance_id, instance_code, transform, old_lng, old_lat in rows:
                lon: float | None = None
                lat: float | None = None
                source = ""

                if instance_code in direct:
                    lon, lat = direct[instance_code]
                    source = "dump"
                else:
                    # wall/road 段等：transform → lon/lat
                    tf = transform
                    if isinstance(tf, str):
                        tf = json.loads(tf)
                    loc = (tf or {}).get("location") or {}
                    x = safe_float(loc.get("x"))
                    z = safe_float(loc.get("z"))
                    if x is not None and z is not None:
                        lon, lat = scene_to_lon_lat(
                            x, z, origin_lon=origin_lon, origin_lat=origin_lat
                        )
                        source = "reverse"

                if lon is None or lat is None:
                    missing += 1
                    continue

                # 已有且相同则跳过
                if (
                    old_lng is not None
                    and old_lat is not None
                    and abs(float(old_lng) - lon) < 1e-7
                    and abs(float(old_lat) - lat) < 1e-7
                ):
                    unchanged += 1
                    continue

                if args.dry_run:
                    print(
                        f"  [dry-run] {instance_code} <- {source} "
                        f"({lon:.6f},{lat:.6f})"
                    )
                else:
                    cur.execute(
                        """
                        UPDATE scene_platform.actor_instance
                        SET gps_lng = %s,
                            gps_lat = %s,
                            update_time = CURRENT_TIMESTAMP,
                            updater = 'gps-backfill-jinqiao'
                        WHERE id = %s
                        """,
                        (lon, lat, instance_id),
                    )
                if source == "dump":
                    updated_direct += 1
                else:
                    updated_reverse += 1

        if args.dry_run:
            conn.rollback()
            print(
                f"[dry-run] would update dump={updated_direct} "
                f"reverse={updated_reverse} missing={missing} unchanged={unchanged}"
            )
        else:
            conn.commit()
            print(
                f"updated dump={updated_direct} reverse={updated_reverse} "
                f"missing={missing} unchanged={unchanged}"
            )
            with conn.cursor() as cur:
                cur.execute(
                    """
                    SELECT COUNT(*) FILTER (WHERE gps_lng IS NOT NULL AND gps_lat IS NOT NULL),
                           COUNT(*)
                    FROM scene_platform.actor_instance ai
                    JOIN scene_platform.scene s ON s.id = ai.scene_id
                    WHERE s.scene_code = %s AND ai.deleted = false
                    """,
                    (SCENE_CODE,),
                )
                with_gps, total = cur.fetchone()
                print(f"verify SCENE-JINQIAO with_gps={with_gps}/{total}")
    finally:
        conn.close()
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
