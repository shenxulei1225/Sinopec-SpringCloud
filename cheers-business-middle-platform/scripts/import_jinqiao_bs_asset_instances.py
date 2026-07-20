#!/usr/bin/env python3
"""
从老 MySQL dump 导入金桥站场 bs_asset → actor_instance（仪表 / 阀门 / 罐顶仪表）。

- asset_type 1 → LEGACY-METER → /scene-models/yibiao/model.glb
- asset_type 2 → LEGACY-VALVE → /scene-models/famen/model.glb
- asset_type 7 → LEGACY-METER（罐顶也是仪表）→ yibiao；GPS 用 dump 罐顶坐标（后续可手调）
- dump 里偏东错误的仪表/阀门经纬度：改落到站心，便于人工再调
- 写入 gps_lng / gps_lat；transform 由站心墨卡托换算（仅工作台用，GIS 页只用 GPS）
- 不写 gps_height

用法：
  python import_jinqiao_bs_asset_instances.py --dump E:/Sino/pre-dev-full-20260711_114237.sql.gz --dry-run
  python import_jinqiao_bs_asset_instances.py --dump E:/Sino/pre-dev-full-20260711_114237.sql.gz
"""

from __future__ import annotations

import argparse
import sys
from pathlib import Path
from typing import Any

import psycopg2
from psycopg2.extras import Json

sys.path.insert(0, str(Path(__file__).resolve().parent))
from mercator_scene import lon_lat_to_scene  # noqa: E402
from refit_legacy_transforms import iter_table_rows, load_oil_depot_origins  # noqa: E402

LEGACY_SITE_ID = "121212"
SCENE_CODE = "SCENE-JINQIAO"
COMPOSITION_ACTOR_CODE = "composition_structure"
INSTANCE_PREFIX = "legacy_bs_asset_"

# asset_type → (asset_code, asset_name, asset_type_node, public_url)
TYPE_MAP: dict[int, tuple[str, str, str, str]] = {
    1: ("LEGACY-METER", "仪表", "EQUIPMENT_MESH", "/scene-models/yibiao/model.glb"),
    2: ("LEGACY-VALVE", "阀门", "EQUIPMENT_MESH", "/scene-models/famen/model.glb"),
    # 罐顶巡检点实际是仪表，先落在罐顶坐标
    7: ("LEGACY-METER", "仪表", "EQUIPMENT_MESH", "/scene-models/yibiao/model.glb"),
}

# dump 中偏东错误点（仪表/阀门）相对站心东偏超过该阈值（度）则改用站心
BAD_EAST_DELTA_LON = 0.003  # ≈300m


def safe_float(v: Any) -> float | None:
    try:
        if v is None or v == "":
            return None
        return float(v)
    except (TypeError, ValueError):
        return None


def next_id(cur, table: str) -> int:
    cur.execute(f"SELECT COALESCE(MAX(id), 0) + 1 FROM scene_platform.{table}")
    return int(cur.fetchone()[0])


def upsert_shared_asset(cur, *, asset_code: str, asset_name: str, asset_type: str, url: str) -> None:
    meta = Json(
        {
            "source": "legacy-bs-asset-import",
            "runtimeUrl": url,
            "convertStatus": "ready",
        }
    )
    cur.execute(
        """
        INSERT INTO scene_platform.asset_resource (
          id, asset_code, asset_name, asset_type, asset_url, format, engine_profile, status, metadata_json,
          tenant_id, creator, create_time, update_time, deleted
        ) VALUES (
          %s, %s, %s, %s, %s, 'glb', 'three-gltf', 1, %s,
          1, 'legacy-bs-asset-import', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false
        )
        ON CONFLICT (asset_code)
        DO UPDATE SET
          asset_name = EXCLUDED.asset_name,
          asset_type = EXCLUDED.asset_type,
          asset_url = EXCLUDED.asset_url,
          format = EXCLUDED.format,
          metadata_json = EXCLUDED.metadata_json,
          deleted = false,
          update_time = CURRENT_TIMESTAMP,
          updater = 'legacy-bs-asset-import'
        """,
        (next_id(cur, "asset_resource"), asset_code, asset_name, asset_type, url, meta),
    )


def upsert_instance(
    cur,
    *,
    scene_id: int,
    instance_code: str,
    instance_name: str,
    asset_code: str,
    lon: float,
    lat: float,
    x: float,
    y: float,
    z: float,
    gps_source: str = "dump",
) -> None:
    transform = {
        "location": {"x": x, "y": y, "z": z},
        "rotation": {"pitch": 0.0, "yaw": 0.0, "roll": 0.0},
        "scale": {"x": 0.1, "y": 0.1, "z": 0.1},
    }
    meta = {
        "renderAssetCode": asset_code,
        "legacyImport": True,
        "legacySource": "bs_asset",
        "gpsSource": gps_source,
    }
    cur.execute(
        """
        INSERT INTO scene_platform.actor_instance (
          id, scene_id, actor_code, instance_code, instance_name, instance_status, visible_flag, version_no,
          transform, metadata_json, gps_lng, gps_lat,
          tenant_id, creator, create_time, update_time, deleted
        ) VALUES (
          %s, %s, %s, %s, %s, 'READY', true, 1,
          %s::jsonb, %s::jsonb, %s, %s,
          1, 'legacy-bs-asset-import', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false
        )
        ON CONFLICT (instance_code)
        DO UPDATE SET
          scene_id = EXCLUDED.scene_id,
          instance_name = EXCLUDED.instance_name,
          actor_code = EXCLUDED.actor_code,
          transform = EXCLUDED.transform,
          metadata_json = EXCLUDED.metadata_json,
          gps_lng = EXCLUDED.gps_lng,
          gps_lat = EXCLUDED.gps_lat,
          deleted = false,
          update_time = CURRENT_TIMESTAMP,
          updater = 'legacy-bs-asset-import'
        """,
        (
            next_id(cur, "actor_instance"),
            scene_id,
            COMPOSITION_ACTOR_CODE,
            instance_code,
            instance_name[:128],
            Json(transform),
            Json(meta),
            lon,
            lat,
        ),
    )


def main() -> int:
    ap = argparse.ArgumentParser()
    ap.add_argument(
        "--dump",
        type=Path,
        default=Path(r"E:/Sino/pre-dev-full-20260711_114237.sql.gz"),
    )
    ap.add_argument("--dsn", default="host=127.0.0.1 dbname=sinopec user=postgres password=Coolhomer")
    ap.add_argument("--dry-run", action="store_true")
    args = ap.parse_args()

    if not args.dump.exists():
        print(f"dump 不存在: {args.dump}", file=sys.stderr)
        return 2

    origins = load_oil_depot_origins(args.dump)
    origin = origins.get(LEGACY_SITE_ID) or (117.238056, 34.303056)
    origin_lon, origin_lat = float(origin[0]), float(origin[1])
    print(f"站心 lon={origin_lon:.6f} lat={origin_lat:.6f}")

    rows = [
        r
        for r in iter_table_rows(args.dump, "bs_asset")
        if str(r.get("site_id") or "") == LEGACY_SITE_ID
    ]
    print(f"dump bs_asset 金桥: {len(rows)}")

    conn = psycopg2.connect(args.dsn)
    conn.autocommit = False
    inserted = 0
    skipped = 0

    try:
        with conn.cursor() as cur:
            cur.execute(
                "SELECT id FROM scene_platform.scene WHERE scene_code = %s AND deleted = false",
                (SCENE_CODE,),
            )
            scene_row = cur.fetchone()
            if not scene_row:
                print(f"场景不存在: {SCENE_CODE}", file=sys.stderr)
                return 2
            scene_id = int(scene_row[0])

            if not args.dry_run:
                # 去重：type 1/7 共用 LEGACY-METER
                seen_assets: set[str] = set()
                for _atype, (asset_code, asset_name, asset_type, url) in TYPE_MAP.items():
                    if asset_code in seen_assets:
                        continue
                    seen_assets.add(asset_code)
                    upsert_shared_asset(
                        cur,
                        asset_code=asset_code,
                        asset_name=asset_name,
                        asset_type=asset_type,
                        url=url,
                    )

            for row in rows:
                legacy_id = row.get("id")
                if legacy_id is None:
                    skipped += 1
                    continue
                try:
                    atype = int(row.get("asset_type"))
                except (TypeError, ValueError):
                    skipped += 1
                    print(f"  skip id={legacy_id}: bad asset_type={row.get('asset_type')}")
                    continue
                mapped = TYPE_MAP.get(atype)
                if not mapped:
                    skipped += 1
                    print(f"  skip id={legacy_id}: unsupported asset_type={atype}")
                    continue
                asset_code, _an, _at, _url = mapped
                lon = safe_float(row.get("longitude") or row.get("lon"))
                lat = safe_float(row.get("latitude") or row.get("lat"))
                if lon is None or lat is None:
                    skipped += 1
                    print(f"  skip id={legacy_id}: missing lon/lat")
                    continue
                # 仪表/阀门 dump 偏东错误坐标：先落到站心，后续人工调
                gps_source = "dump"
                if atype in (1, 2) and (lon - origin_lon) > BAD_EAST_DELTA_LON:
                    print(
                        f"  remap id={legacy_id} bad-east "
                        f"({lon:.6f},{lat:.6f}) -> site center"
                    )
                    lon, lat = origin_lon, origin_lat
                    gps_source = "site-center-pending-adjust"
                name = str(row.get("asset_name") or row.get("asset_id") or f"asset_{legacy_id}").strip()
                instance_code = f"{INSTANCE_PREFIX}{legacy_id}"
                x, y, z = lon_lat_to_scene(
                    lon, lat, origin_lon=origin_lon, origin_lat=origin_lat, height=0.0
                )
                if args.dry_run:
                    print(
                        f"  [dry-run] {instance_code} {name} type={atype} -> {asset_code} "
                        f"({lon:.6f},{lat:.6f}) [{gps_source}]"
                    )
                else:
                    upsert_instance(
                        cur,
                        scene_id=scene_id,
                        instance_code=instance_code,
                        instance_name=name,
                        asset_code=asset_code,
                        lon=lon,
                        lat=lat,
                        x=x,
                        y=y,
                        z=z,
                        gps_source=gps_source,
                    )
                inserted += 1

        if args.dry_run:
            conn.rollback()
            print(f"[dry-run] would upsert {inserted}, skipped={skipped}")
        else:
            conn.commit()
            print(f"upserted {inserted}, skipped={skipped}")
            with conn.cursor() as cur:
                cur.execute(
                    """
                    SELECT COUNT(*) FROM scene_platform.actor_instance ai
                    JOIN scene_platform.scene s ON s.id = ai.scene_id
                    WHERE s.scene_code = %s AND ai.deleted = false
                      AND ai.instance_code LIKE %s
                    """,
                    (SCENE_CODE, INSTANCE_PREFIX + "%"),
                )
                print(f"verify asset instances: {cur.fetchone()[0]}")
    finally:
        conn.close()
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
