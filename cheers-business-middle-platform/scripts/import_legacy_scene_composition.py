#!/usr/bin/env python3
"""
老栈构筑物 → scene-platform 资产 + ActorInstance（场景编排导入）。

步骤：
  1) 注册/更新 asset_resource（asset_type = scene_asset 分类节点 code，无坐标）
  2) 解析 bs_3d_house / pot / wall / road 与 bs_asset GPS，写入 actor_instance + Transform
  3) metadata_json 含 renderAssetCode；instance_code = legacy_{table}_{id}

用法：
  python3 import_legacy_scene_composition.py \\
    --dump /path/to/pre-dev-full.sql.gz \\
    --site 121240:45:SCENE-LUOYANG-SHENGRUI \\
    --models-root /Users/kevin/Documents/Sinopec/Sinopec_ecs/public/static/models \\
    --dry-run
"""

from __future__ import annotations

import argparse
import gzip
import json
import re
import sys
from dataclasses import dataclass
from pathlib import Path
from typing import Any, Iterable

import psycopg2
from psycopg2.extras import Json

from mercator_scene import lon_lat_to_scene

INSERT_WITH_COLS_RE = re.compile(
    r"^INSERT INTO `(?P<table>[^`]+)` \((?P<cols>[^)]+)\) VALUES (?P<values>.+);?\s*$"
)
INSERT_VALUES_ONLY_RE = re.compile(
    r"^INSERT INTO `(?P<table>[^`]+)` VALUES (?P<values>.+);?\s*$"
)
CREATE_TABLE_RE = re.compile(r"^CREATE TABLE `(?P<table>[^`]+)` \(")

# 老栈表 → scene_asset 分类节点 code
TABLE_ASSET_TYPE = {
    "bs_3d_house": "BUILDING",
    "bs_3d_pot": "TANK",
    "bs_3d_wall": "WALL",
    "bs_3d_road": "ROAD",
}

# 编排实例统一用已 seed 的 composition_structure Actor
COMPOSITION_ACTOR_CODE = "composition_structure"

NAME_KEYS = {
    "bs_3d_house": ("house_name", "name", "title"),
    "bs_3d_pot": ("pot_name", "name", "title"),
    "bs_3d_wall": ("wall_name", "name", "title"),
    "bs_3d_road": ("road_name", "name", "title"),
}

LEGACY_TABLES = tuple(TABLE_ASSET_TYPE.keys()) + ("bs_asset",)


@dataclass(frozen=True)
class SiteMap:
    legacy_facility_id: str
    facility_id: int
    scene_code: str


def open_text(path: Path) -> Iterable[str]:
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
    values: list[Any] = []
    buf: list[str] = []
    in_str = False
    escape = False
    for ch in inner:
        if in_str:
            buf.append(ch)
            if escape:
                escape = False
            elif ch == "\\":
                escape = True
            elif ch == "'":
                in_str = False
            continue
        if ch == "'":
            in_str = True
            buf.append(ch)
            continue
        if ch == ",":
            values.append(_coerce_mysql_scalar("".join(buf).strip()))
            buf = []
            continue
        buf.append(ch)
    if buf or inner.endswith(","):
        values.append(_coerce_mysql_scalar("".join(buf).strip()))
    return values


def _coerce_mysql_scalar(token: str) -> Any:
    if token.upper() == "NULL" or token == "":
        return None
    if token.startswith("'") and token.endswith("'"):
        return (
            token[1:-1]
            .replace("\\'", "'")
            .replace('\\"', '"')
            .replace("\\\\", "\\")
            .replace("\\n", "\n")
        )
    try:
        if "." in token:
            return float(token)
        return int(token)
    except ValueError:
        return token


def iter_table_rows(dump: Path, table: str) -> Iterable[dict[str, Any]]:
    columns: list[str] | None = None
    collecting_create = False
    create_cols: list[str] = []
    for line in open_text(dump):
        line = line.rstrip("\n")
        m_create = CREATE_TABLE_RE.match(line)
        if m_create:
            collecting_create = m_create.group("table") == table
            create_cols = []
            continue
        if collecting_create:
            if line.startswith(")"):
                columns = create_cols
                collecting_create = False
                continue
            col_m = re.match(r"\s*`([^`]+)`", line)
            if col_m:
                create_cols.append(col_m.group(1))
            continue
        m_cols = INSERT_WITH_COLS_RE.match(line)
        if m_cols and m_cols.group("table") == table:
            cols = [c.strip().strip("`") for c in m_cols.group("cols").split(",")]
            for tup in split_mysql_tuples(m_cols.group("values")):
                vals = parse_mysql_tuple(tup)
                yield dict(zip(cols, vals))
            continue
        m_vals = INSERT_VALUES_ONLY_RE.match(line)
        if m_vals and m_vals.group("table") == table:
            if not columns:
                raise RuntimeError(f"CREATE TABLE for `{table}` not seen before INSERT")
            for tup in split_mysql_tuples(m_vals.group("values")):
                vals = parse_mysql_tuple(tup)
                yield dict(zip(columns, vals))


def resolve_oil_depot_origin(dump: Path, legacy_facility_id: str) -> tuple[float, float]:
    """站心：bs_3d_oil_depot 该站首条有效 GPS（与老栈 ensureMapOriginFromOilDepot 一致）。"""
    for row in iter_table_rows(dump, "bs_3d_oil_depot"):
        if str(row.get("facility_id") or row.get("site_id") or row.get("facilityId") or row.get("siteId") or "") != legacy_facility_id:
            continue
        lon = row.get("longitude")
        lat = row.get("latitude")
        if lon is None or lat is None or lon == "" or lat == "":
            continue
        return float(lon), float(lat)
    raise RuntimeError(f"facility {legacy_facility_id}: dump 中无 bs_3d_oil_depot 原点，请传 --origin-lon/--origin-lat")


def lonlat_to_local_meters(lon: float, lat: float, origin_lon: float, origin_lat: float) -> tuple[float, float]:
    """兼容旧名：返回 (x, z)，内部为墨卡托 + 北向映 -Z。"""
    x, _y, z = lon_lat_to_scene(lon, lat, origin_lon=origin_lon, origin_lat=origin_lat, height=0.0)
    return x, z


def resolve_scene_id(cur, scene_code: str) -> int:
    cur.execute(
        "SELECT id FROM scene_platform.scene WHERE scene_code = %s AND deleted = false LIMIT 1",
        (scene_code,),
    )
    row = cur.fetchone()
    if not row:
        raise RuntimeError(f"scene_code={scene_code} 不存在，请先执行 facility_scene_binding 迁移/seed")
    return int(row[0])


def next_id(cur, table: str) -> int:
    cur.execute(f"SELECT COALESCE(MAX(id), 0) + 1 FROM scene_platform.{table}")
    return int(cur.fetchone()[0])


def resolve_row_name(table: str, row: dict[str, Any], legacy_id: Any) -> str:
    for key in NAME_KEYS.get(table, ("name", "title")):
        val = row.get(key)
        if val is not None and str(val).strip():
            return str(val).strip()
    return f"{table}_{legacy_id}"


# 仓库 / 生产车间：库内各一条共享资产；场景里多实例靠 scale 区分（设备机房、泵组仍一栋一资产）
SHARED_BUILDING_ASSETS: dict[str, tuple[str, str]] = {
    "仓库": ("LEGACY-BUILDING-WAREHOUSE", "仓库"),
    "生产车间": ("LEGACY-BUILDING-WORKSHOP", "生产车间"),
}


def shared_building_asset(name: str) -> tuple[str, str] | None:
    """名称以「仓库」「生产车间」开头则返回 (asset_code, asset_name)，否则 None。"""
    n = (name or "").strip()
    for prefix, pair in SHARED_BUILDING_ASSETS.items():
        if n == prefix or n.startswith(prefix):
            return pair
    return None


def _parse_orientation(raw: Any) -> dict[str, Any] | None:
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


def upsert_asset(
    cur,
    *,
    asset_code: str,
    asset_name: str,
    asset_type: str,
    asset_url: str,
    legacy_model_hint: str | None,
    dry_run: bool,
) -> None:
    if dry_run:
        print(f"  [dry-run] asset {asset_code} type={asset_type}")
        return
    meta_obj: dict[str, Any] = {
        "source": "legacy-import",
    }
    if asset_url and asset_url.lower().endswith((".glb", ".gltf")):
        meta_obj["runtimeUrl"] = asset_url
        meta_obj["convertStatus"] = "ready"
    else:
        meta_obj["convertStatus"] = "pending"
        meta_obj["convertMessage"] = "等待本地模型转换为 GLB（见 reconvert_legacy_models.py）"
    if legacy_model_hint:
        meta_obj["legacyModelHint"] = legacy_model_hint
    meta = Json(meta_obj)
    cur.execute(
        """
        INSERT INTO scene_platform.asset_resource (
          id, asset_code, asset_name, asset_type, asset_url, format, engine_profile, status, metadata_json,
          tenant_id, creator, create_time, update_time, deleted
        ) VALUES (
          %s, %s, %s, %s, %s, %s, 'three-gltf', 1, %s,
          1, 'legacy-import', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false
        )
        ON CONFLICT (asset_code)
        DO UPDATE SET
          asset_name = EXCLUDED.asset_name,
          asset_type = EXCLUDED.asset_type,
          asset_url = EXCLUDED.asset_url,
          format = EXCLUDED.format,
          metadata_json = EXCLUDED.metadata_json,
          update_time = CURRENT_TIMESTAMP,
          updater = 'legacy-import'
        """,
        (
            next_id(cur, "asset_resource"),
            asset_code,
            asset_name[:128],
            asset_type,
            asset_url,
            Path(asset_url).suffix.lstrip(".").lower() or ("glb" if asset_url else "bin"),
            meta,
        ),
    )


def upsert_actor_instance(
    cur,
    *,
    scene_id: int,
    instance_code: str,
    instance_name: str,
    actor_code: str,
    x: float,
    y: float,
    z: float,
    pitch: float,
    yaw: float,
    roll: float,
    scale_x: float,
    scale_y: float,
    scale_z: float,
    asset_code: str,
    dry_run: bool,
) -> None:
    transform = {
        "location": {"x": x, "y": y, "z": z},
        "rotation": {"pitch": pitch, "yaw": yaw, "roll": roll},
        "scale": {"x": scale_x, "y": scale_y, "z": scale_z},
    }
    meta = {"renderAssetCode": asset_code, "legacyImport": True}
    if dry_run:
        print(f"  [dry-run] instance {instance_code} -> {asset_code} @ ({x:.2f},{y:.2f},{z:.2f})")
        return
    cur.execute(
        """
        INSERT INTO scene_platform.actor_instance (
          id, scene_id, actor_code, instance_code, instance_name, instance_status, visible_flag, version_no,
          transform, metadata_json, tenant_id, creator, create_time, update_time, deleted
        ) VALUES (
          %s, %s, %s, %s, %s, 'READY', true, 1,
          %s::jsonb, %s::jsonb, 1, 'legacy-import', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false
        )
        ON CONFLICT (instance_code)
        DO UPDATE SET
          scene_id = EXCLUDED.scene_id,
          instance_name = EXCLUDED.instance_name,
          actor_code = EXCLUDED.actor_code,
          transform = EXCLUDED.transform,
          metadata_json = EXCLUDED.metadata_json,
          deleted = false,
          update_time = CURRENT_TIMESTAMP,
          updater = 'legacy-import'
        """,
        (
            next_id(cur, "actor_instance"),
            scene_id,
            actor_code,
            instance_code,
            instance_name[:128],
            Json(transform),
            Json(meta),
        ),
    )


def guess_model_path(
    models_root: Path,
    asset_type: str,
    *,
    house_type: int | None = None,
) -> Path | None:
    """按老栈 modeling/index.vue 规则解析本地源模型路径。"""
    if asset_type == "TANK":
        p = models_root / "oiltank" / "d.fbx"
        return p if p.is_file() else None
    if asset_type == "BUILDING":
        mapping = {
            1: models_root / "house1" / "a.fbx",
            2: models_root / "house3" / "b.fbx",
            3: models_root / "house2" / "c.fbx",
            6: models_root / "xiaofang.obj",
            7: models_root / "aifeier.obj",
        }
        p = mapping.get(house_type or 1, models_root / "house1" / "a.fbx")
        return p if p.is_file() else None
    return None


def guess_model_url(
    models_root: Path,
    asset_type: str,
    name: str | None,
    *,
    house_type: int | None = None,
    public_base: str = "/scene-models",
) -> tuple[str, str | None]:
    """
    返回 (可加载 URL, 本地模型提示路径)。
    优先已转换的 public GLB；否则记录 hint，URL 暂空由 reconvert 回填。
    """
    src = guess_model_path(models_root, asset_type, house_type=house_type)
    if src is None:
        # 围墙/道路老栈为挤出几何，无网格
        return "", None
    stem_map = {
        "d.fbx": "oiltank/model.glb",
        "a.fbx": "house1/model.glb",
        "c.fbx": "house2/model.glb",
        "b.fbx": "house3/model.glb",
        "xiaofang.obj": "xiaofang/model.glb",
        "aifeier.obj": "aifeier/model.glb",
    }
    out_name = stem_map.get(src.name, src.stem + "/model.glb")
    public_url = f"{public_base.rstrip('/')}/{out_name}"
    return public_url, str(src)


def import_site(
    cur,
    dump: Path,
    site: SiteMap,
    models_root: Path,
    origin_lon: float | None,
    origin_lat: float | None,
    dry_run: bool,
) -> None:
    scene_id = resolve_scene_id(cur, site.scene_code)
    print(f"== facility={site.facility_id} scene={site.scene_code} id={scene_id}")

    # bs_asset: id -> lon/lat
    asset_gps: dict[Any, tuple[float, float]] = {}
    for row in iter_table_rows(dump, "bs_asset"):
        if str(row.get("facility_id") or row.get("site_id") or row.get("facilityId") or row.get("siteId") or "") != site.legacy_facility_id:
            continue
        lon = row.get("longitude") or row.get("lon")
        lat = row.get("latitude") or row.get("lat")
        if lon is None or lat is None:
            continue
        asset_gps[row.get("id")] = (float(lon), float(lat))

    # 站心：优先 CLI；否则 bs_3d_oil_depot（禁止构筑物均值冒充）
    if origin_lon is None or origin_lat is None:
        origin_lon, origin_lat = resolve_oil_depot_origin(dump, site.legacy_facility_id)
    print(f"  origin lon={origin_lon:.6f} lat={origin_lat:.6f} (oil-depot/cli)")

    for table, asset_type in TABLE_ASSET_TYPE.items():
        count = 0
        skipped = 0
        for row in iter_table_rows(dump, table):
            facility_id = str(row.get("facility_id") or row.get("site_id") or row.get("facilityId") or row.get("siteId") or "")
            if facility_id and facility_id != site.legacy_facility_id:
                continue
            legacy_id = row.get("id")
            if legacy_id is None:
                continue
            name = resolve_row_name(table, row, legacy_id)
            house_type = None
            if table == "bs_3d_house" and row.get("type") is not None:
                try:
                    house_type = int(row.get("type"))
                except (TypeError, ValueError):
                    house_type = None
            shared = shared_building_asset(name) if table == "bs_3d_house" else None
            if shared:
                asset_code, asset_display_name = shared
            else:
                asset_code = f"LEGACY-{asset_type}-{site.legacy_facility_id}-{legacy_id}"
                if len(asset_code) > 64:
                    asset_code = asset_code[:64]
                asset_display_name = name
            asset_url, legacy_hint = guess_model_url(
                models_root, asset_type, name, house_type=house_type
            )
            upsert_asset(
                cur,
                asset_code=asset_code,
                asset_name=asset_display_name,
                asset_type=asset_type,
                asset_url=asset_url,
                legacy_model_hint=legacy_hint,
                dry_run=dry_run,
            )

            lon = row.get("longitude") or row.get("lon")
            lat = row.get("latitude") or row.get("lat")
            if (lon is None or lat is None) and row.get("asset_id") in asset_gps:
                lon, lat = asset_gps[row.get("asset_id")]
            if lon is None or lat is None:
                # 跳过无坐标行（禁止编造）
                print(f"  skip {table} id={legacy_id}: missing lon/lat")
                skipped += 1
                continue
            x, z = lonlat_to_local_meters(float(lon), float(lat), float(origin_lon), float(origin_lat))
            # 位姿：与老栈 initHouse / initGeometry 对齐；完整回填也可用 refit_legacy_transforms.py
            pitch = roll = 0.0
            yaw = 0.0
            scale_x = scale_y = scale_z = 1.0
            if table == "bs_3d_house":
                y = 2.1
                orient = _parse_orientation(row.get("orientation"))
                if orient:
                    rot = orient.get("rotation") or {}
                    pitch = float(rot.get("x") or 0)
                    yaw = float(rot.get("y") or 0)
                    roll = float(rot.get("z") or 0)
                    sc = orient.get("scale") or {}
                    if sc:
                        scale_x = float(sc.get("x") or 1)
                        scale_y = float(sc.get("y") or 1)
                        scale_z = float(sc.get("z") or 1)
            elif table == "bs_3d_pot":
                y = float(row.get("planeness") or 0)
                # 罐缩放依赖 mesh 包围盒，导入阶段先置 1；请随后跑 refit_legacy_transforms.py
            else:
                y = float(row.get("modeling_height") or row.get("height") or row.get("altitude") or 0)
            instance_code = f"legacy_{table}_{legacy_id}"
            upsert_actor_instance(
                cur,
                scene_id=scene_id,
                instance_code=instance_code,
                instance_name=name,
                actor_code=COMPOSITION_ACTOR_CODE,
                x=x,
                y=y,
                z=z,
                pitch=pitch,
                yaw=yaw,
                roll=roll,
                scale_x=scale_x,
                scale_y=scale_y,
                scale_z=scale_z,
                asset_code=asset_code,
                dry_run=dry_run,
            )
            count += 1
        print(f"  {table}: {count} instances, skipped={skipped}")


def parse_site(raw: str) -> SiteMap:
    parts = raw.split(":")
    if len(parts) != 3:
        raise argparse.ArgumentTypeError("--site expects legacySiteId:facilityId:sceneCode")
    return SiteMap(parts[0], int(parts[1]), parts[2])


def main(argv: list[str] | None = None) -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--dump", type=Path, required=True)
    parser.add_argument("--site", action="append", type=parse_site, required=True)
    parser.add_argument("--models-root", type=Path, required=True)
    parser.add_argument(
        "--origin-lon",
        type=float,
        default=None,
        help="站心经度；省略则用 dump 中 bs_3d_oil_depot",
    )
    parser.add_argument(
        "--origin-lat",
        type=float,
        default=None,
        help="站心纬度；省略则用 dump 中 bs_3d_oil_depot",
    )
    parser.add_argument("--pg-host", default="127.0.0.1")
    parser.add_argument("--pg-port", type=int, default=5432)
    parser.add_argument("--pg-db", default="sinopec")
    parser.add_argument("--pg-user", default="postgres")
    parser.add_argument("--pg-password", default="Coolhomer")
    parser.add_argument("--dry-run", action="store_true")
    args = parser.parse_args(argv)

    if not args.dump.exists():
        print(f"dump not found: {args.dump}", file=sys.stderr)
        return 2
    if not args.models_root.exists():
        print(f"models-root not found: {args.models_root}", file=sys.stderr)
        return 2

    conn = psycopg2.connect(
        host=args.pg_host,
        port=args.pg_port,
        dbname=args.pg_db,
        user=args.pg_user,
        password=args.pg_password,
    )
    try:
        with conn:
            with conn.cursor() as cur:
                for site in args.site:
                    import_site(
                        cur,
                        args.dump,
                        site,
                        args.models_root,
                        args.origin_lon,
                        args.origin_lat,
                        args.dry_run,
                    )
                    if not args.dry_run:
                        conn.commit()
    finally:
        conn.close()
    print("done")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
