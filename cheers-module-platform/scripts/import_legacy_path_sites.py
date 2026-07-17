#!/usr/bin/env python3
"""
从老栈 MySQL dump 导入站场路网 + 动态业务巡检点（阶段 A）。

源表：
  bs_patrol_inspection_point  → PathNetwork STATION + ent_point（station_node_id 回填）；tank_group→zoneId
  bs_navigation_waypoint      → TRAVERSAL；kind=3 → DOOR
  bs_route_edge               → TRAVERSABLE（距离/正权重）或 BLOCKED（weight=-1）

坐标：优先 coordinate_json.sceneX/Y/Z；否则用经纬度作 x/z 占位。
设施：新 facilityId（不用旧 site_id）。

用法示例：
  python3 import_legacy_path_sites.py \\
    --dump /Users/kevin/Sinopec/洛阳数据/pre-dev-full-20260711_114237.sql.gz \\
    --site 121212:44:FAC-JINQIAO \\
    --site 121240:45:FAC-LUOYANG-SHENGRUI \\
    --publish
"""

from __future__ import annotations

import argparse
import gzip
import json
import re
import sys
import zlib
from dataclasses import dataclass
from pathlib import Path
from typing import Any, Iterable, Iterator

import psycopg2
from psycopg2.extras import Json


def map_zone_id(tank_group_id: Any) -> int | None:
    """Stable positive int from legacy tank_group_id (placeholder until real zone ids)."""
    if tank_group_id is None:
        return None
    raw = str(tank_group_id).strip()
    if not raw:
        return None
    return zlib.crc32(raw.encode("utf-8")) & 0x7FFFFFFF


def coerce_int(value: Any, default: int = 0) -> int:
    try:
        return int(value)
    except (TypeError, ValueError):
        return default

INSERT_WITH_COLS_RE = re.compile(
    r"^INSERT INTO `(?P<table>[^`]+)` \((?P<cols>[^)]+)\) VALUES (?P<values>.+);?\s*$"
)
INSERT_VALUES_ONLY_RE = re.compile(
    r"^INSERT INTO `(?P<table>[^`]+)` VALUES (?P<values>.+);?\s*$"
)
CREATE_TABLE_RE = re.compile(r"^CREATE TABLE `(?P<table>[^`]+)` \(")


@dataclass(frozen=True)
class SiteMap:
    legacy_site_id: str
    facility_id: int
    facility_code: str


def open_text(path: Path) -> Iterable[str]:
    if str(path).endswith(".gz"):
        with gzip.open(path, "rt", encoding="utf-8", errors="replace") as f:
            yield from f
    else:
        with path.open("rt", encoding="utf-8", errors="replace") as f:
            yield from f


def split_mysql_tuples(values_blob: str) -> list[str]:
    """Split `(...),(...),...` into raw tuple strings including parentheses."""
    rows: list[str] = []
    i = 0
    n = len(values_blob)
    while i < n:
        while i < n and values_blob[i] in " \t\r\n,":
            i += 1
        if i >= n:
            break
        if values_blob[i] != "(":
            raise ValueError(f"expected '(' at {i}")
        depth = 0
        in_str = False
        esc = False
        start = i
        while i < n:
            ch = values_blob[i]
            if in_str:
                if esc:
                    esc = False
                elif ch == "\\":
                    esc = True
                elif ch == "'":
                    in_str = False
            else:
                if ch == "'":
                    in_str = True
                elif ch == "(":
                    depth += 1
                elif ch == ")":
                    depth -= 1
                    if depth == 0:
                        rows.append(values_blob[start : i + 1])
                        i += 1
                        break
            i += 1
        else:
            raise ValueError("unterminated tuple")
    return rows


def parse_mysql_tuple(tuple_sql: str) -> list[Any]:
    """Parse a single `(a,'b',NULL,...)` into Python values."""
    assert tuple_sql[0] == "(" and tuple_sql[-1] == ")"
    s = tuple_sql[1:-1]
    out: list[Any] = []
    i = 0
    n = len(s)
    while i < n:
        while i < n and s[i] in " \t\r\n":
            i += 1
        if i >= n:
            break
        if s.startswith("NULL", i) and (i + 4 == n or s[i + 4] in ", \t"):
            out.append(None)
            i += 4
        elif s[i] == "'":
            i += 1
            buf: list[str] = []
            while i < n:
                ch = s[i]
                if ch == "\\" and i + 1 < n:
                    buf.append(s[i + 1])
                    i += 2
                    continue
                if ch == "'":
                    if i + 1 < n and s[i + 1] == "'":
                        buf.append("'")
                        i += 2
                        continue
                    i += 1
                    break
                buf.append(ch)
                i += 1
            out.append("".join(buf))
        else:
            j = i
            while j < n and s[j] != ",":
                j += 1
            token = s[i:j].strip()
            if token.isdigit() or (token.startswith("-") and token[1:].isdigit()):
                out.append(int(token))
            else:
                try:
                    out.append(float(token))
                except ValueError:
                    out.append(token)
            i = j
        while i < n and s[i] in " \t\r\n":
            i += 1
        if i < n and s[i] == ",":
            i += 1
    return out


def load_create_table_columns(dump: Path, tables: set[str]) -> dict[str, list[str]]:
    """Read column order from CREATE TABLE blocks (mysqldump)."""
    wanted = set(tables)
    found: dict[str, list[str]] = {}
    current: str | None = None
    cols: list[str] = []
    for line in open_text(dump):
        if current is None:
            m = CREATE_TABLE_RE.match(line.rstrip())
            if not m:
                continue
            name = m.group("table")
            if name not in wanted or name in found:
                continue
            current = name
            cols = []
            continue
        # inside CREATE TABLE
        stripped = line.strip()
        if stripped.startswith(")") or stripped.startswith(") ENGINE"):
            found[current] = cols
            current = None
            cols = []
            if len(found) == len(wanted):
                break
            continue
        if stripped.startswith("`"):
            col = stripped.split("`")[1]
            cols.append(col)
    missing = wanted - set(found)
    if missing:
        raise SystemExit(f"CREATE TABLE columns not found for: {sorted(missing)}")
    return found


def iter_table_rows(
    dump: Path, table: str, columns_by_table: dict[str, list[str]]
) -> Iterator[dict[str, Any]]:
    prefix = "INSERT INTO `" + table + "`"
    for line in open_text(dump):
        if not line.startswith(prefix):
            continue
        raw = line.rstrip()
        m = INSERT_WITH_COLS_RE.match(raw)
        if m:
            cols = [c.strip().strip("`") for c in m.group("cols").split(",")]
            values_blob = m.group("values").rstrip(";")
        else:
            m2 = INSERT_VALUES_ONLY_RE.match(raw)
            if not m2:
                continue
            cols = columns_by_table[table]
            values_blob = m2.group("values").rstrip(";")
        for tup in split_mysql_tuples(values_blob):
            vals = parse_mysql_tuple(tup)
            if len(vals) != len(cols):
                if len(vals) < len(cols):
                    vals = vals + [None] * (len(cols) - len(vals))
                else:
                    vals = vals[: len(cols)]
            yield dict(zip(cols, vals))


def parse_coord(row: dict[str, Any]) -> tuple[float, float, float, dict[str, Any]]:
    payload: dict[str, Any] = {}
    lon = row.get("longitude")
    lat = row.get("latitude")
    raw = row.get("coordinate_json")
    scene: dict[str, Any] = {}
    if isinstance(raw, str) and raw.strip():
        try:
            scene = json.loads(raw)
        except json.JSONDecodeError:
            scene = {}
    if scene:
        payload["coordinate_json"] = scene
    x = scene.get("sceneX")
    y = scene.get("sceneY")
    z = scene.get("sceneZ")
    if x is not None and z is not None:
        return float(x), float(y if y is not None else 0), float(z), payload
    # fallback: lon/lat as x/z (legacy GPS；场景可能偏移，仅作可见占位)
    try:
        return float(lon or 0), float(row.get("height") or 0), float(lat or 0), payload
    except (TypeError, ValueError):
        return 0.0, 0.0, 0.0, payload


def build_network(
    site: SiteMap,
    visits: list[dict[str, Any]],
    transits: list[dict[str, Any]],
    edges: list[dict[str, Any]],
) -> dict[str, Any]:
    nodes: list[dict[str, Any]] = []
    known: set[str] = set()

    for r in visits:
        if str(r.get("enabled") or 1) in ("0", "false", "False"):
            continue
        nid = str(r.get("inspection_point_id") or "").strip()
        if not nid:
            continue
        x, y, z, payload = parse_coord(r)
        tank_group_id = r.get("tank_group_id")
        payload.update(
            {
                "legacySiteId": site.legacy_site_id,
                "legacyKind": "VISIT",
                "legacyInspectionPointId": nid,
                "legacyTankGroupId": tank_group_id,
                "longitude": r.get("longitude"),
                "latitude": r.get("latitude"),
            }
        )
        nodes.append(
            {
                "nodeId": nid,
                "nodeType": "STATION",
                "layer": "GROUND",
                "zoneId": map_zone_id(tank_group_id),
                "displayName": r.get("waypoint_name") or nid,
                "position": {"x": x, "y": y, "z": z},
                "payload": payload,
            }
        )
        known.add(nid)

    for r in transits:
        if str(r.get("enabled") or 1) in ("0", "false", "False"):
            continue
        nid = str(r.get("navigation_waypoint_id") or "").strip()
        if not nid or nid in known:
            continue
        x, y, z, payload = parse_coord(r)
        kind = coerce_int(r.get("navigation_waypoint_kind"), 0)
        tank_group_id = r.get("tank_group_id")
        node_type = "DOOR" if kind == 3 else "TRAVERSAL"
        payload.update(
            {
                "legacySiteId": site.legacy_site_id,
                "legacyKind": "TRANSIT",
                "legacyNavigationWaypointId": nid,
                "legacyWaypointKind": kind,
                "legacyTankGroupId": tank_group_id,
                "longitude": r.get("longitude"),
                "latitude": r.get("latitude"),
            }
        )
        nodes.append(
            {
                "nodeId": nid,
                "nodeType": node_type,
                "layer": "GROUND",
                "zoneId": map_zone_id(tank_group_id),
                "displayName": r.get("waypoint_name") or nid,
                "position": {"x": x, "y": y, "z": z},
                "payload": payload,
            }
        )
        known.add(nid)

    out_edges: list[dict[str, Any]] = []
    skipped = 0
    blocked = 0
    for i, e in enumerate(edges):
        a = str(e.get("start_waypoint_id") or "").strip()
        b = str(e.get("end_waypoint_id") or "").strip()
        if not a or not b or a not in known or b not in known:
            skipped += 1
            continue
        raw_w = e.get("weight")
        try:
            w = float(raw_w) if raw_w is not None else None
        except (TypeError, ValueError):
            w = None
        if w is not None and w < 0:
            blocked += 1
            out_edges.append(
                {
                    "edgeId": f"e_{a}_{b}_{i}",
                    "fromNodeId": a,
                    "toNodeId": b,
                    "layer": "GROUND",
                    "traversability": "BLOCKED",
                    "impedanceByProfile": {},
                }
            )
            continue
        dist = e.get("distance")
        try:
            impedance = float(dist) if dist is not None else float(w if w is not None else 1)
        except (TypeError, ValueError):
            impedance = 1.0
        if impedance <= 0:
            impedance = 1.0
        out_edges.append(
            {
                "edgeId": f"e_{a}_{b}_{i}",
                "fromNodeId": a,
                "toNodeId": b,
                "layer": "GROUND",
                "traversability": "TRAVERSABLE",
                "distanceMeters": impedance if dist is not None else None,
                "impedanceByProfile": {"person_walk": impedance, "HUMAN": impedance},
            }
        )

    return {
        "nodes": nodes,
        "edges": out_edges,
        "stats": {
            "stations": sum(1 for n in nodes if n["nodeType"] == "STATION"),
            "traversals": sum(1 for n in nodes if n["nodeType"] == "TRAVERSAL"),
            "doors": sum(1 for n in nodes if n["nodeType"] == "DOOR"),
            "edges": len(out_edges),
            "edgesBlocked": blocked,
            "edgesSkipped": skipped,
        },
    }


def upsert_path_network(
    conn,
    site: SiteMap,
    network: dict[str, Any],
    *,
    publish: bool,
    tenant_id: int,
) -> str:
    draft_id = f"net_{site.facility_id}_site_draft"
    nodes = Json(network["nodes"])
    edges = Json(network["edges"])
    with conn.cursor() as cur:
        cur.execute(
            """
            INSERT INTO platform.platform_path_network (
              id, network_kind, facility_id, scope_id, status, version,
              nodes, edges, tenant_id, creator, updater, deleted
            ) VALUES (
              %s, 'SITE', %s, NULL, 'DRAFT', 0,
              %s, %s, %s, 'legacy-import', 'legacy-import', false
            )
            ON CONFLICT (id) DO UPDATE SET
              facility_id = EXCLUDED.facility_id,
              network_kind = EXCLUDED.network_kind,
              status = 'DRAFT',
              version = 0,
              nodes = EXCLUDED.nodes,
              edges = EXCLUDED.edges,
              deleted = false,
              updater = 'legacy-import',
              update_time = CURRENT_TIMESTAMP
            """,
            (draft_id, site.facility_id, nodes, edges, tenant_id),
        )
        published_ref = draft_id
        if publish:
            pub_id = f"net_{site.facility_id}_site_v1"
            cur.execute(
                """
                INSERT INTO platform.platform_path_network (
                  id, network_kind, facility_id, scope_id, status, version,
                  nodes, edges, tenant_id, creator, updater, deleted
                ) VALUES (
                  %s, 'SITE', %s, NULL, 'PUBLISHED', 1,
                  %s, %s, %s, 'legacy-import', 'legacy-import', false
                )
                ON CONFLICT (id) DO UPDATE SET
                  facility_id = EXCLUDED.facility_id,
                  status = 'PUBLISHED',
                  version = 1,
                  nodes = EXCLUDED.nodes,
                  edges = EXCLUDED.edges,
                  deleted = false,
                  updater = 'legacy-import',
                  update_time = CURRENT_TIMESTAMP
                """,
                (pub_id, site.facility_id, nodes, edges, tenant_id),
            )
            published_ref = pub_id
    return published_ref


def upsert_patrol_points(
    conn,
    site: SiteMap,
    visits: list[dict[str, Any]],
    *,
    model_id: int,
    tenant_id: int,
) -> int:
    n = 0
    with conn.cursor() as cur:
        for r in visits:
            if str(r.get("enabled") or 1) in ("0", "false", "False"):
                continue
            nid = str(r.get("inspection_point_id") or "").strip()
            if not nid:
                continue
            code = f"PPT-{site.facility_code}-{nid}"
            name = (r.get("waypoint_name") or nid)[:255]
            custom = {
                "facility_id": site.facility_id,
                "station_node_id": nid,
            }
            cur.execute(
                """
                SELECT id FROM dynamicbusiness.ent_point
                WHERE deleted = false AND tenant_id = %s AND code = %s
                """,
                (tenant_id, code),
            )
            existing = cur.fetchone()
            if existing:
                cur.execute(
                    """
                    UPDATE dynamicbusiness.ent_point
                    SET name = %s,
                        model_id = %s,
                        custom_fields = %s,
                        updater = 'legacy-import',
                        update_time = CURRENT_TIMESTAMP
                    WHERE id = %s
                    """,
                    (name, model_id, Json(custom), existing[0]),
                )
            else:
                cur.execute(
                    """
                    INSERT INTO dynamicbusiness.ent_point (
                      tenant_id, entity_type_code, model_id, name, code,
                      status, parent_id, attrs, custom_fields,
                      creator, updater, deleted, sort
                    ) VALUES (
                      %s, 'point', %s, %s, %s,
                      1, 0, '{}'::jsonb, %s,
                      'legacy-import', 'legacy-import', false, 0
                    )
                    """,
                    (tenant_id, model_id, name, code, Json(custom)),
                )
            n += 1
    return n


def load_site_slices(dump: Path, site_ids: set[str]) -> dict[str, dict[str, list]]:
    tables = {
        "bs_patrol_inspection_point",
        "bs_navigation_waypoint",
        "bs_route_edge",
    }
    print(">> reading CREATE TABLE column order …", flush=True)
    columns_by_table = load_create_table_columns(dump, tables)
    buckets = {
        sid: {"visits": [], "transits": [], "edges": []} for sid in site_ids
    }
    print(">> reading INSERT rows …", flush=True)
    for row in iter_table_rows(dump, "bs_patrol_inspection_point", columns_by_table):
        sid = str(row.get("site_id") or "")
        if sid in buckets:
            buckets[sid]["visits"].append(row)
    for row in iter_table_rows(dump, "bs_navigation_waypoint", columns_by_table):
        sid = str(row.get("site_id") or "")
        if sid in buckets:
            buckets[sid]["transits"].append(row)
    for row in iter_table_rows(dump, "bs_route_edge", columns_by_table):
        sid = str(row.get("site_id") or "")
        if sid in buckets:
            buckets[sid]["edges"].append(row)
    return buckets


def parse_site_arg(raw: str) -> SiteMap:
    # legacySiteId:facilityId:facilityCode
    parts = raw.split(":")
    if len(parts) != 3:
        raise argparse.ArgumentTypeError(
            "expected legacySiteId:facilityId:facilityCode"
        )
    return SiteMap(parts[0], int(parts[1]), parts[2])


def main() -> int:
    ap = argparse.ArgumentParser(description=__doc__)
    ap.add_argument("--dump", type=Path, required=True, help="MySQL dump (.sql or .sql.gz)")
    ap.add_argument(
        "--site",
        action="append",
        type=parse_site_arg,
        required=True,
        help="legacySiteId:facilityId:facilityCode (repeatable)",
    )
    ap.add_argument("--pg-host", default="127.0.0.1")
    ap.add_argument("--pg-port", type=int, default=5432)
    ap.add_argument("--pg-db", default="sinopec")
    ap.add_argument("--pg-user", default="postgres")
    ap.add_argument("--pg-password", default="Coolhomer")
    ap.add_argument("--tenant-id", type=int, default=1)
    ap.add_argument("--publish", action="store_true", help="also write PUBLISHED v1 snapshot")
    ap.add_argument("--dry-run", action="store_true")
    args = ap.parse_args()

    site_ids = {s.legacy_site_id for s in args.site}
    print(f">> parsing dump {args.dump} for sites {sorted(site_ids)} …", flush=True)
    buckets = load_site_slices(args.dump, site_ids)

    if args.dry_run:
        for s in args.site:
            b = buckets[s.legacy_site_id]
            net = build_network(s, b["visits"], b["transits"], b["edges"])
            print(
                f"[dry-run] site={s.legacy_site_id} -> facility={s.facility_id} "
                f"stats={net['stats']} visits={len(b['visits'])}"
            )
        return 0

    conn = psycopg2.connect(
        host=args.pg_host,
        port=args.pg_port,
        dbname=args.pg_db,
        user=args.pg_user,
        password=args.pg_password,
    )
    try:
        with conn.cursor() as cur:
            cur.execute(
                """
                SELECT id FROM dynamicbusiness.dynamic_model
                WHERE deleted = false AND tenant_id = %s AND code = 'point_patrol'
                """,
                (args.tenant_id,),
            )
            row = cur.fetchone()
            if not row:
                raise SystemExit("missing model point_patrol — run phase-0 seed first")
            model_id = int(row[0])

        for s in args.site:
            b = buckets[s.legacy_site_id]
            net = build_network(s, b["visits"], b["transits"], b["edges"])
            ref = upsert_path_network(
                conn, s, net, publish=args.publish, tenant_id=args.tenant_id
            )
            pts = upsert_patrol_points(
                conn, s, b["visits"], model_id=model_id, tenant_id=args.tenant_id
            )
            conn.commit()
            print(
                f"OK site={s.legacy_site_id} facilityId={s.facility_id} "
                f"networkRef={ref} stats={net['stats']} patrolPoints={pts}"
            )
    finally:
        conn.close()
    return 0


if __name__ == "__main__":
    sys.exit(main())
