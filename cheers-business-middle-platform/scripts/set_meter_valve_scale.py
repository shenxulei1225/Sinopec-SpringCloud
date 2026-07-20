#!/usr/bin/env python3
"""
将仪表 / 阀门 actor_instance 的 transform.scale 统一设为 0.1。

匹配：
- metadata.renderAssetCode / assetCode ∈ {LEGACY-METER, LEGACY-VALVE}
- actor_code / instance 名含 meter|valve|仪表|阀门
- metadata 含 yibiao / famen 模型路径

用法：
  python set_meter_valve_scale.py --dry-run
  python set_meter_valve_scale.py
"""

from __future__ import annotations

import argparse

import psycopg2

TARGET_SCALE = {"x": 0.1, "y": 0.1, "z": 0.1}

MATCH_SQL = """
  deleted = false
  AND (
    metadata_json->>'renderAssetCode' IN ('LEGACY-METER', 'LEGACY-VALVE')
    OR metadata_json->>'assetCode' IN ('LEGACY-METER', 'LEGACY-VALVE')
    OR metadata_json::text ILIKE '%%/yibiao/%%'
    OR metadata_json::text ILIKE '%%/famen/%%'
    OR instance_name ILIKE '%%仪表%%'
    OR instance_name ILIKE '%%阀门%%'
    OR actor_code ILIKE '%%meter%%'
    OR actor_code ILIKE '%%valve%%'
  )
"""


def main() -> int:
    ap = argparse.ArgumentParser()
    ap.add_argument("--dsn", default="host=127.0.0.1 dbname=sinopec user=postgres password=Coolhomer")
    ap.add_argument("--dry-run", action="store_true")
    args = ap.parse_args()

    conn = psycopg2.connect(args.dsn)
    conn.autocommit = False
    try:
        with conn.cursor() as cur:
            cur.execute(
                f"""
                SELECT id, instance_code, instance_name, actor_code,
                       metadata_json->>'renderAssetCode',
                       transform->'scale'
                FROM scene_platform.actor_instance
                WHERE {MATCH_SQL}
                ORDER BY id
                """
            )
            rows = cur.fetchall()
            print(f"matched {len(rows)} instances")
            for r in rows:
                print(f"  id={r[0]} code={r[1]} name={r[2]} actor={r[3]} asset={r[4]} scale={r[5]}")

            if args.dry_run:
                print("dry-run: no write")
                conn.rollback()
                return 0

            cur.execute(
                f"""
                UPDATE scene_platform.actor_instance
                SET transform = jsonb_set(
                      COALESCE(transform, '{{}}'::jsonb),
                      '{{scale}}',
                      %s::jsonb,
                      true
                    ),
                    update_time = CURRENT_TIMESTAMP,
                    updater = 'set-meter-valve-scale'
                WHERE {MATCH_SQL}
                """,
                ('{"x": 0.1, "y": 0.1, "z": 0.1}',),
            )
            print(f"updated {cur.rowcount} rows → scale={TARGET_SCALE}")
            conn.commit()
        return 0
    except Exception:
        conn.rollback()
        raise
    finally:
        conn.close()


if __name__ == "__main__":
    raise SystemExit(main())
