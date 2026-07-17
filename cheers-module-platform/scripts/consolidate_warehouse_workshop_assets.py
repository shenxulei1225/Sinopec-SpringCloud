#!/usr/bin/env python3
"""
将「仓库*」「生产车间*」从一栋一资产收敛为两条共享资产；实例保留各自 scale。

  python3 consolidate_warehouse_workshop_assets.py
"""

from __future__ import annotations

import argparse
import json

import psycopg2
from psycopg2.extras import Json

SHARED = {
    "仓库": {
        "asset_code": "LEGACY-BUILDING-WAREHOUSE",
        "asset_name": "仓库",
        "runtime_url": "/scene-models/house3/model.glb",
        "preview_url": "/scene-models/_previews/building-house3.png",
        "hint": "house3/b.fbx",
    },
    "生产车间": {
        "asset_code": "LEGACY-BUILDING-WORKSHOP",
        "asset_name": "生产车间",
        "runtime_url": "/scene-models/house2/model.glb",
        "preview_url": "/scene-models/_previews/building-house2.png",
        "hint": "house2/c.fbx",
    },
}


def name_prefix(name: str) -> str | None:
    n = (name or "").strip()
    for prefix in SHARED:
        if n == prefix or n.startswith(prefix):
            return prefix
    return None


def next_id(cur, table: str) -> int:
    cur.execute(f"SELECT COALESCE(MAX(id), 0) + 1 FROM scene_platform.{table}")
    return int(cur.fetchone()[0])


def main() -> int:
    ap = argparse.ArgumentParser()
    ap.add_argument("--db-url", default="postgresql://postgres:Coolhomer@127.0.0.1:5432/sinopec")
    ap.add_argument("--dry-run", action="store_true")
    args = ap.parse_args()

    conn = psycopg2.connect(args.db_url)
    try:
        with conn.cursor() as cur:
            for prefix, cfg in SHARED.items():
                meta = {
                    "source": "legacy-shared-building",
                    "runtimeUrl": cfg["runtime_url"],
                    "convertStatus": "ready",
                    "legacyModelHint": cfg["hint"],
                    "sharedByNamePrefix": prefix,
                }
                if args.dry_run:
                    print(f"[dry-run] upsert {cfg['asset_code']}")
                else:
                    cur.execute(
                        """
                        INSERT INTO scene_platform.asset_resource (
                          id, asset_code, asset_name, asset_type, asset_url, preview_url,
                          format, engine_profile, status, metadata_json,
                          tenant_id, creator, create_time, update_time, deleted
                        ) VALUES (
                          %s, %s, %s, 'BUILDING', %s, %s,
                          'glb', 'three-gltf', 1, %s,
                          1, 'consolidate-shared', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false
                        )
                        ON CONFLICT (asset_code) DO UPDATE SET
                          asset_name = EXCLUDED.asset_name,
                          asset_url = EXCLUDED.asset_url,
                          preview_url = EXCLUDED.preview_url,
                          metadata_json = EXCLUDED.metadata_json,
                          deleted = false,
                          update_time = CURRENT_TIMESTAMP,
                          updater = 'consolidate-shared'
                        """,
                        (
                            next_id(cur, "asset_resource"),
                            cfg["asset_code"],
                            cfg["asset_name"],
                            cfg["runtime_url"],
                            cfg["preview_url"],
                            Json(meta),
                        ),
                    )
                print(f"  asset {cfg['asset_code']} ready")

            cur.execute(
                """
                SELECT id, instance_name, metadata_json
                FROM scene_platform.actor_instance
                WHERE deleted = false
                  AND (instance_name LIKE '仓库%%' OR instance_name LIKE '生产车间%%')
                """
            )
            updated = 0
            old_codes: set[str] = set()
            for actor_id, instance_name, meta in cur.fetchall():
                prefix = name_prefix(instance_name or "")
                if not prefix:
                    continue
                target = SHARED[prefix]["asset_code"]
                meta_obj = meta if isinstance(meta, dict) else (json.loads(meta) if meta else {})
                old = meta_obj.get("renderAssetCode")
                if old and old != target:
                    old_codes.add(str(old))
                meta_obj["renderAssetCode"] = target
                meta_obj["sharedBuildingAsset"] = True
                if args.dry_run:
                    print(f"  [dry-run] instance {instance_name} -> {target}")
                else:
                    cur.execute(
                        """
                        UPDATE scene_platform.actor_instance
                        SET metadata_json = %s, update_time = CURRENT_TIMESTAMP, updater = 'consolidate-shared'
                        WHERE id = %s
                        """,
                        (Json(meta_obj), actor_id),
                    )
                updated += 1
            print(f"  instances retargeted: {updated}")

            # 软删仅服务于仓库/车间的旧「一栋一资产」
            if old_codes:
                codes = sorted(old_codes)
                if args.dry_run:
                    print(f"  [dry-run] soft-delete assets: {codes}")
                else:
                    cur.execute(
                        """
                        UPDATE scene_platform.asset_resource
                        SET deleted = true, update_time = CURRENT_TIMESTAMP, updater = 'consolidate-shared'
                        WHERE asset_code = ANY(%s)
                          AND deleted = false
                          AND asset_code NOT IN ('LEGACY-BUILDING-WAREHOUSE', 'LEGACY-BUILDING-WORKSHOP')
                        """,
                        (codes,),
                    )
                    print(f"  soft-deleted assets: {cur.rowcount}")

        if args.dry_run:
            conn.rollback()
        else:
            conn.commit()
    finally:
        conn.close()
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
