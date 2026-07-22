#!/usr/bin/env python3
"""
场景平台 actor_instance（罐/房）→ 动态业务 ent_scene_placement 对账收编。

阶段 1：只收编 legacy_bs_3d_pot_* / legacy_bs_3d_house_*；墙/路分段不在范围。
运行时仍读场景平台；本脚本只建动态业务账。

用法：
  python reconcile_scene_placements_to_dynamicbusiness.py \\
    --scene-code SCENE-LUOYANG-SHENGRUI [--dry-run]
"""

from __future__ import annotations

import argparse
import sys
from typing import Any

import psycopg2
from psycopg2.extras import Json, RealDictCursor

MODEL_BY_PREFIX = {
    "legacy_bs_3d_pot_": "MODEL-SCENE-STRUCTURE-TANK",
    "legacy_bs_3d_house_": "MODEL-SCENE-STRUCTURE-BUILDING",
}


def connect(args: argparse.Namespace):
    return psycopg2.connect(
        host=args.host,
        port=args.port,
        dbname=args.database,
        user=args.user,
        password=args.password,
    )


def resolve_model_code(instance_code: str) -> str | None:
    for prefix, model_code in MODEL_BY_PREFIX.items():
        if instance_code.startswith(prefix):
            return model_code
    return None


def extract_render_asset_code(metadata: Any) -> str | None:
    if not isinstance(metadata, dict):
        return None
    for key in ("renderAssetCode", "render_asset_code", "assetCode", "asset_code"):
        val = metadata.get(key)
        if val:
            return str(val)
    return None


def main() -> int:
    ap = argparse.ArgumentParser()
    ap.add_argument("--host", default="127.0.0.1")
    ap.add_argument("--port", type=int, default=5432)
    ap.add_argument("--database", default="sinopec")
    ap.add_argument("--user", default="postgres")
    ap.add_argument("--password", default="Coolhomer")
    ap.add_argument("--scene-code", required=True, help="对账 scene_code / ent_scene.code")
    ap.add_argument("--tenant-id", type=int, default=1)
    ap.add_argument("--dry-run", action="store_true")
    args = ap.parse_args()

    conn = connect(args)
    try:
        with conn.cursor(cursor_factory=RealDictCursor) as cur:
            cur.execute(
                """
                SELECT id, code, scene_code, name
                FROM dynamicbusiness.ent_scene
                WHERE deleted = false AND tenant_id = %s
                  AND (code = %s OR scene_code = %s)
                LIMIT 1
                """,
                (args.tenant_id, args.scene_code, args.scene_code),
            )
            scene = cur.fetchone()
            if not scene:
                print(f"ERROR: ent_scene not found for {args.scene_code}", file=sys.stderr)
                return 1

            # Luoyang seed uses SCENE-CAT-LUOYANG-SHENGRUI for SCENE-LUOYANG-SHENGRUI
            cat_code = (
                "SCENE-CAT-LUOYANG-SHENGRUI"
                if args.scene_code == "SCENE-LUOYANG-SHENGRUI"
                else f"SCENE-CAT-{args.scene_code}"
            )
            cur.execute(
                """
                SELECT id, code FROM dynamicbusiness.dynamic_category
                WHERE deleted = false AND tenant_id = %s
                  AND category_type_code = 'scene' AND code = %s
                LIMIT 1
                """,
                (args.tenant_id, cat_code),
            )
            category = cur.fetchone()
            if not category:
                print(f"ERROR: scene category not found: {cat_code}", file=sys.stderr)
                return 1

            cur.execute(
                """
                SELECT code, id FROM dynamicbusiness.dynamic_model
                WHERE deleted = false AND tenant_id = %s
                  AND code = ANY(%s)
                """,
                (args.tenant_id, list(MODEL_BY_PREFIX.values())),
            )
            model_ids = {row["code"]: row["id"] for row in cur.fetchall()}
            missing = [c for c in MODEL_BY_PREFIX.values() if c not in model_ids]
            if missing:
                print(f"ERROR: missing models {missing}", file=sys.stderr)
                return 1

            cur.execute(
                """
                SELECT ai.instance_code, ai.instance_name, ai.transform, ai.metadata_json,
                       ai.visible_flag, ai.parent_instance_code,
                       ai.gps_lng, ai.gps_lat, ai.gps_height, ai.gps_height_source
                FROM scene_platform.actor_instance ai
                JOIN scene_platform.scene s ON s.id = ai.scene_id AND s.deleted = false
                WHERE ai.deleted = false
                  AND s.scene_code = %s
                  AND (
                    ai.instance_code LIKE 'legacy_bs_3d_pot_%%'
                    OR ai.instance_code LIKE 'legacy_bs_3d_house_%%'
                  )
                ORDER BY ai.instance_code
                """,
                (args.scene_code,),
            )
            rows = cur.fetchall()
            print(f"scene_id={scene['id']} category={category['code']} candidates={len(rows)}")

            upserted = 0
            linked = 0
            for row in rows:
                code = row["instance_code"]
                model_code = resolve_model_code(code)
                if not model_code:
                    continue
                model_id = model_ids[model_code]
                name = row["instance_name"] or code
                transform = row["transform"] if row["transform"] is not None else {}
                metadata = row["metadata_json"] if row["metadata_json"] is not None else {}
                asset_code = extract_render_asset_code(metadata)

                if args.dry_run:
                    upserted += 1
                    continue

                cur.execute(
                    """
                    SELECT id FROM dynamicbusiness.ent_scene_placement
                    WHERE deleted = false AND tenant_id = %s AND code = %s
                    LIMIT 1
                    """,
                    (args.tenant_id, code),
                )
                existing = cur.fetchone()
                transform_param = Json(transform) if not isinstance(transform, str) else transform
                metadata_param = Json(metadata) if not isinstance(metadata, str) else metadata

                if existing:
                    placement_id = existing["id"]
                    cur.execute(
                        """
                        UPDATE dynamicbusiness.ent_scene_placement SET
                          name = %s, model_id = %s, scene_id = %s,
                          render_asset_code = %s, transform = %s, metadata_json = %s,
                          visible_flag = COALESCE(%s, true),
                          parent_placement_code = %s,
                          gps_lng = %s, gps_lat = %s, gps_height = %s, gps_height_source = %s,
                          deleted = false, updater = 'reconcile', update_time = CURRENT_TIMESTAMP
                        WHERE id = %s
                        """,
                        (
                            name, model_id, scene["id"], asset_code,
                            transform_param, metadata_param,
                            row["visible_flag"], row["parent_instance_code"],
                            row["gps_lng"], row["gps_lat"], row["gps_height"], row["gps_height_source"],
                            placement_id,
                        ),
                    )
                else:
                    cur.execute(
                        """
                        INSERT INTO dynamicbusiness.ent_scene_placement (
                          tenant_id, entity_type_code, model_id, name, code, status,
                          scene_id, render_asset_code, transform, metadata_json,
                          visible_flag, parent_placement_code,
                          gps_lng, gps_lat, gps_height, gps_height_source,
                          creator, updater, deleted, sort
                        ) VALUES (
                          %s, 'scene_placement', %s, %s, %s, 1,
                          %s, %s, %s, %s,
                          COALESCE(%s, true), %s,
                          %s, %s, %s, %s,
                          'reconcile', 'reconcile', false, 0
                        )
                        RETURNING id
                        """,
                        (
                            args.tenant_id, model_id, name, code,
                            scene["id"], asset_code, transform_param, metadata_param,
                            row["visible_flag"], row["parent_instance_code"],
                            row["gps_lng"], row["gps_lat"], row["gps_height"], row["gps_height_source"],
                        ),
                    )
                    placement_id = cur.fetchone()["id"]
                upserted += 1

                cur.execute(
                    """
                    SELECT id FROM dynamicbusiness.dynamic_entity_category_relation
                    WHERE deleted = false AND tenant_id = %s
                      AND entity_id = %s AND category_id = %s
                    LIMIT 1
                    """,
                    (args.tenant_id, placement_id, category["id"]),
                )
                if cur.fetchone() is None:
                    cur.execute(
                        """
                        INSERT INTO dynamicbusiness.dynamic_entity_category_relation (
                          entity_id, category_id, entity_type_code, sort, tenant_id, creator
                        ) VALUES (%s, %s, 'scene_placement', 0, %s, 'reconcile')
                        """,
                        (placement_id, category["id"], args.tenant_id),
                    )
                    linked += 1

            if args.dry_run:
                print(f"dry-run: would upsert {upserted} placements")
                conn.rollback()
            else:
                conn.commit()
                print(f"upserted={upserted} newly_linked={linked}")
        return 0
    finally:
        conn.close()


if __name__ == "__main__":
    raise SystemExit(main())
