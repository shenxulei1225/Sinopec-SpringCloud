#!/usr/bin/env python3
"""
老站围墙/道路折线 → 标准段角色实例（SEG-WALL-PANEL-1M / SEG-ROAD-TILE-1M）。

折线：bs_3d_points.ascription_id = wall_id / road_id；围墙闭合（末点连回首点）。

用法：
  python3 import_legacy_wall_road_segments.py \\
    --dump /Users/kevin/Sinopec/洛阳数据/pre-dev-full-20260711_114237.sql.gz \\
    --site 121212:44:SCENE-JINQIAO \\
    --site 121240:45:SCENE-LUOYANG-SHENGRUI
"""

from __future__ import annotations

import argparse
import sys
from collections import defaultdict
from dataclasses import dataclass
from pathlib import Path
from typing import Any

import psycopg2
from psycopg2.extras import Json

sys.path.insert(0, str(Path(__file__).resolve().parent))
from mercator_scene import lon_lat_to_scene  # noqa: E402
from refit_legacy_transforms import iter_table_rows, load_oil_depot_origins  # noqa: E402
from segment_polyline import split_polyline_to_segments  # noqa: E402

WALL_ASSET = "SEG-WALL-PANEL-1M"
ROAD_ASSET = "SEG-ROAD-TILE-1M"
WALL_PANEL_HEIGHT = 2.5
ROAD_TILE_WIDTH = 3.0
LEGACY_ROAD_WIDTH = 10.0
COMPOSITION_ACTOR_CODE = "composition_structure"


@dataclass(frozen=True)
class SiteMap:
    legacy_site_id: str
    facility_id: int
    scene_code: str


def next_id(cur, table: str) -> int:
    cur.execute(f"SELECT COALESCE(MAX(id), 0) + 1 FROM scene_platform.{table}")
    return int(cur.fetchone()[0])


def resolve_scene_id(cur, scene_code: str) -> int:
    cur.execute(
        "SELECT id FROM scene_platform.scene WHERE scene_code = %s AND deleted = false LIMIT 1",
        (scene_code,),
    )
    row = cur.fetchone()
    if not row:
        raise RuntimeError(f"scene_code={scene_code} 不存在")
    return int(row[0])


def safe_float(v: Any, default: float = 0.0) -> float:
    try:
        if v is None or v == "":
            return default
        return float(v)
    except (TypeError, ValueError):
        return default


def upsert_segment_instance(
    cur,
    *,
    scene_id: int,
    instance_code: str,
    instance_name: str,
    asset_code: str,
    x: float,
    y: float,
    z: float,
    yaw: float,
    scale_x: float,
    scale_y: float,
    scale_z: float,
    dry_run: bool,
) -> None:
    transform = {
        "location": {"x": x, "y": y, "z": z},
        "rotation": {"pitch": 0.0, "yaw": yaw, "roll": 0.0},
        "scale": {"x": scale_x, "y": scale_y, "z": scale_z},
    }
    meta = {
        "renderAssetCode": asset_code,
        "legacyImport": True,
        "legacySegment": True,
    }
    if dry_run:
        return
    cur.execute(
        """
        INSERT INTO scene_platform.actor_instance (
          id, scene_id, actor_code, instance_code, instance_name, instance_status, visible_flag, version_no,
          transform, metadata_json, tenant_id, creator, create_time, update_time, deleted
        ) VALUES (
          %s, %s, %s, %s, %s, 'READY', true, 1,
          %s::jsonb, %s::jsonb, 1, 'wall-road-segments', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false
        )
        ON CONFLICT (instance_code)
        DO UPDATE SET
          scene_id = EXCLUDED.scene_id,
          instance_name = EXCLUDED.instance_name,
          transform = EXCLUDED.transform,
          metadata_json = EXCLUDED.metadata_json,
          deleted = false,
          update_time = CURRENT_TIMESTAMP,
          updater = 'wall-road-segments'
        """,
        (
            next_id(cur, "actor_instance"),
            scene_id,
            COMPOSITION_ACTOR_CODE,
            instance_code,
            instance_name[:128],
            Json(transform),
            Json(meta),
        ),
    )


def load_points_by_ascription(dump: Path, site_id: str) -> dict[str, list[dict[str, Any]]]:
    by: dict[str, list[dict[str, Any]]] = defaultdict(list)
    for row in iter_table_rows(dump, "bs_3d_points"):
        if str(row.get("site_id") or "") != site_id:
            continue
        asc = str(row.get("ascription_id") or "").strip()
        if not asc:
            continue
        by[asc].append(row)
    for rows in by.values():
        rows.sort(key=lambda r: int(r.get("sequence") or 0))
    return by


def points_to_xz(
    points: list[dict[str, Any]],
    *,
    origin_lon: float,
    origin_lat: float,
) -> tuple[list[tuple[float, float]], float, float]:
    xz: list[tuple[float, float]] = []
    planeness = 0.0
    height = 1.0
    for p in points:
        lon = safe_float(p.get("longitude"))
        lat = safe_float(p.get("latitude"))
        x, _y, z = lon_lat_to_scene(lon, lat, origin_lon=origin_lon, origin_lat=origin_lat, height=0.0)
        xz.append((x, z))
        planeness = safe_float(p.get("planeness"), planeness)
        h = safe_float(p.get("modeling_height"), 0.0)
        if h > 0:
            height = h
    return xz, planeness, height


def import_site(
    cur,
    dump: Path,
    site: SiteMap,
    origins: dict[str, tuple[float, float]],
    *,
    segment_length: float,
    dry_run: bool,
) -> None:
    scene_id = resolve_scene_id(cur, site.scene_code)
    origin = origins.get(site.legacy_site_id)
    if not origin:
        raise RuntimeError(f"site {site.legacy_site_id}: 无油库原点")
    origin_lon, origin_lat = origin
    points_by = load_points_by_ascription(dump, site.legacy_site_id)
    print(f"== {site.scene_code} site={site.legacy_site_id} points_groups={len(points_by)}")

    if not dry_run:
        cur.execute(
            """
            UPDATE scene_platform.actor_instance
            SET deleted = true, update_time = CURRENT_TIMESTAMP, updater = 'wall-road-segments'
            WHERE scene_id = %s
              AND deleted = false
              AND (
                instance_code LIKE 'legacy_wall_seg_%%'
                OR instance_code LIKE 'legacy_road_seg_%%'
                OR metadata_json->>'legacySegment' = 'true'
              )
            """,
            (scene_id,),
        )

    wall_segs = 0
    for wall in iter_table_rows(dump, "bs_3d_wall"):
        if str(wall.get("site_id") or "") != site.legacy_site_id:
            continue
        wall_id = str(wall.get("wall_id") or "").strip()
        name = str(wall.get("wall_name") or wall_id or wall.get("id"))
        pts = points_by.get(wall_id) or []
        if len(pts) < 2:
            print(f"  skip wall {name}: points={len(pts)}")
            continue
        xz, planeness, height = points_to_xz(pts, origin_lon=origin_lon, origin_lat=origin_lat)
        if len(xz) >= 3 and (abs(xz[0][0] - xz[-1][0]) > 1e-3 or abs(xz[0][1] - xz[-1][1]) > 1e-3):
            xz = list(xz) + [xz[0]]
        scale_y = max(height / WALL_PANEL_HEIGHT, 0.15)
        y_center = planeness + (WALL_PANEL_HEIGHT * scale_y) / 2.0
        poses = split_polyline_to_segments(xz, segment_length=segment_length, y=y_center)
        for i, pose in enumerate(poses):
            code = f"legacy_wall_seg_{site.legacy_site_id}_{wall.get('id')}_{i}"
            upsert_segment_instance(
                cur,
                scene_id=scene_id,
                instance_code=code,
                instance_name=f"{name}#{i + 1}",
                asset_code=WALL_ASSET,
                x=pose.x,
                y=pose.y,
                z=pose.z,
                yaw=pose.yaw_deg,
                scale_x=pose.scale_x,
                scale_y=scale_y,
                scale_z=1.0,
                dry_run=dry_run,
            )
            wall_segs += 1
        print(f"  wall {name}: {len(poses)} segments (h={height})")

    road_segs = 0
    road_scale_z = LEGACY_ROAD_WIDTH / ROAD_TILE_WIDTH
    for road in iter_table_rows(dump, "bs_3d_road"):
        if str(road.get("site_id") or "") != site.legacy_site_id:
            continue
        try:
            rtype = int(road.get("type") or 1)
        except (TypeError, ValueError):
            rtype = 1
        if rtype != 1:
            print(f"  skip road bridge type={rtype} {road.get('road_name')}")
            continue
        road_id = str(road.get("road_id") or "").strip()
        name = str(road.get("road_name") or road_id or road.get("id"))
        pts = points_by.get(road_id) or []
        if len(pts) < 2:
            print(f"  skip road {name}: points={len(pts)}")
            continue
        xz, planeness, _h = points_to_xz(pts, origin_lon=origin_lon, origin_lat=origin_lat)
        y_center = planeness + 0.04
        poses = split_polyline_to_segments(xz, segment_length=segment_length, y=y_center)
        for i, pose in enumerate(poses):
            code = f"legacy_road_seg_{site.legacy_site_id}_{road.get('id')}_{i}"
            upsert_segment_instance(
                cur,
                scene_id=scene_id,
                instance_code=code,
                instance_name=f"{name}#{i + 1}",
                asset_code=ROAD_ASSET,
                x=pose.x,
                y=pose.y,
                z=pose.z,
                yaw=pose.yaw_deg,
                scale_x=pose.scale_x,
                scale_y=1.0,
                scale_z=road_scale_z,
                dry_run=dry_run,
            )
            road_segs += 1
        print(f"  road {name}: {len(poses)} segments")

    print(f"  total wall_segs={wall_segs} road_segs={road_segs}")


def parse_site(raw: str) -> SiteMap:
    parts = raw.split(":")
    if len(parts) != 3:
        raise argparse.ArgumentTypeError("--site expects legacySiteId:facilityId:sceneCode")
    return SiteMap(parts[0], int(parts[1]), parts[2])


def main() -> int:
    ap = argparse.ArgumentParser(description=__doc__)
    ap.add_argument("--dump", type=Path, required=True)
    ap.add_argument("--site", action="append", type=parse_site, required=True)
    ap.add_argument("--segment-length", type=float, default=1.0)
    ap.add_argument("--db-url", default="postgresql://postgres:Coolhomer@127.0.0.1:5432/sinopec")
    ap.add_argument("--dry-run", action="store_true")
    args = ap.parse_args()

    origins = load_oil_depot_origins(args.dump)
    conn = psycopg2.connect(args.db_url)
    try:
        with conn.cursor() as cur:
            for code in (WALL_ASSET, ROAD_ASSET):
                cur.execute(
                    "SELECT 1 FROM scene_platform.asset_resource WHERE asset_code=%s AND deleted=false",
                    (code,),
                )
                if not cur.fetchone():
                    raise RuntimeError(f"缺少标准段资产 {code}")
            for site in args.site:
                import_site(
                    cur,
                    args.dump,
                    site,
                    origins,
                    segment_length=args.segment_length,
                    dry_run=args.dry_run,
                )
        if args.dry_run:
            conn.rollback()
            print("dry-run: rolled back")
        else:
            conn.commit()
            print("committed")
    finally:
        conn.close()
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
