#!/usr/bin/env python3
"""
将老栈 FBX/OBJ 转为前端可加载的 GLB，并回写 scene_platform.asset_resource。

根因：
  - 导入时用了 Google 样例 GLB（现网 404），画布看不到模型
  - legacyModelHint 曾误全部指向 oiltank

用法：
  python3 reconvert_legacy_models.py \\
    --dump /Users/kevin/Sinopec/洛阳数据/pre-dev-full-20260711_114237.sql.gz \\
    --models-root /Users/kevin/Documents/Sinopec/Sinopec_ecs/public/static/models \\
    --out-dir /Users/kevin/Documents/Sinopec/ecs-react/public/scene-models \\
    --public-base /scene-models
"""

from __future__ import annotations

import argparse
import gzip
import json
import re
import subprocess
import sys
from pathlib import Path
from typing import Any, Iterable

import psycopg2
from psycopg2.extras import Json

# 与老栈 modeling/index.vue switch(item.type) 一致
HOUSE_TYPE_TO_MODEL = {
    1: ("house1/a.fbx", "house1/model.glb"),
    2: ("house3/b.fbx", "house3/model.glb"),
    3: ("house2/c.fbx", "house2/model.glb"),
    6: ("xiaofang.obj", "xiaofang/model.glb"),
    7: ("aifeier.obj", "aifeier/model.glb"),
}

DEFAULT_BUILDING = ("house1/a.fbx", "house1/model.glb")
TANK_MODEL = ("oiltank/d.fbx", "oiltank/model.glb")

INSERT_VALUES_ONLY_RE = re.compile(
    r"^INSERT INTO `(?P<table>[^`]+)` VALUES (?P<values>.+);?\s*$"
)
CREATE_TABLE_RE = re.compile(r"^CREATE TABLE `(?P<table>[^`]+)` \(")


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


def iter_table_rows(dump: Path, table: str) -> Iterable[dict[str, Any]]:
    cols = load_table_columns(dump, table)
    if not cols:
        raise RuntimeError(f"dump 中未找到表结构: {table}")
    for line in open_text(dump):
        m = INSERT_VALUES_ONLY_RE.match(line.strip())
        if not m or m.group("table") != table:
            continue
        for tup in split_mysql_tuples(m.group("values")):
            vals = parse_mysql_tuple(tup)
            if len(vals) < len(cols):
                continue
            yield dict(zip(cols, vals[: len(cols)]))


def convert_with_assimp(src: Path, dest: Path) -> None:
    raise RuntimeError("deprecated: use convert_legacy_models_with_textures.py")


def ensure_models(models_root: Path, out_dir: Path) -> None:
    """调用带贴图修正的转换脚本。"""
    script = Path(__file__).with_name("convert_legacy_models_with_textures.py")
    cmd = [
        sys.executable,
        str(script),
        "--models-root",
        str(models_root),
        "--out-root",
        str(out_dir),
    ]
    proc = subprocess.run(cmd, check=False)
    if proc.returncode != 0:
        raise RuntimeError("convert_legacy_models_with_textures.py failed")


def house_key(site_id: Any, house_pk: Any) -> str:
    return f"{site_id}:{house_pk}"


def load_house_types(dump: Path) -> dict[str, int]:
    """site_id:id -> type"""
    out: dict[str, int] = {}
    for row in iter_table_rows(dump, "bs_3d_house"):
        site = row.get("site_id")
        pk = row.get("id")
        t = row.get("type")
        if site is None or pk is None or t is None:
            continue
        out[house_key(site, pk)] = int(t)
    return out


def pick_building_model(house_type: int | None) -> tuple[str, str]:
    if house_type is None:
        return DEFAULT_BUILDING
    return HOUSE_TYPE_TO_MODEL.get(house_type, DEFAULT_BUILDING)


def update_asset(
    cur,
    *,
    asset_id: int,
    public_url: str,
    hint: str,
    dry_run: bool,
) -> None:
    meta = {
        "runtimeUrl": public_url,
        "convertStatus": "ready",
        "converterName": "assimp-cli-batch",
        "source": "legacy-import",
        "legacyModelHint": hint,
    }
    if dry_run:
        print(f"  [dry-run] asset id={asset_id} -> {public_url}")
        return
    cur.execute(
        """
        UPDATE scene_platform.asset_resource
        SET asset_url = %s,
            format = 'glb',
            metadata_json = %s::jsonb,
            update_time = CURRENT_TIMESTAMP,
            updater = 'legacy-reconvert'
        WHERE id = %s
        """,
        (public_url, Json(meta), asset_id),
    )


def mark_non_mesh(
    cur,
    *,
    asset_id: int,
    message: str,
    dry_run: bool,
) -> None:
    meta = {
        "convertStatus": "pending",
        "convertMessage": message,
        "source": "legacy-import",
    }
    if dry_run:
        print(f"  [dry-run] asset id={asset_id} pending: {message}")
        return
    cur.execute(
        """
        UPDATE scene_platform.asset_resource
        SET metadata_json = %s::jsonb,
            update_time = CURRENT_TIMESTAMP,
            updater = 'legacy-reconvert'
        WHERE id = %s
        """,
        (Json(meta), asset_id),
    )


def parse_asset_code(code: str) -> tuple[str, str, str] | None:
    # LEGACY-{TYPE}-{site}-{id}
    m = re.match(r"^LEGACY-([A-Z_]+)-(\d+)-(\d+)$", code)
    if not m:
        return None
    return m.group(1), m.group(2), m.group(3)


def main() -> int:
    ap = argparse.ArgumentParser()
    ap.add_argument("--dump", type=Path, required=True)
    ap.add_argument("--models-root", type=Path, required=True)
    ap.add_argument("--out-dir", type=Path, required=True)
    ap.add_argument("--public-base", default="/scene-models")
    ap.add_argument("--db-url", default="postgresql://postgres:Coolhomer@127.0.0.1:5432/sinopec")
    ap.add_argument("--dry-run", action="store_true")
    args = ap.parse_args()

    print("== convert unique models")
    ensure_models(args.models_root, args.out_dir)

    print("== load house types from dump")
    house_types = load_house_types(args.dump)
    print(f"  houses={len(house_types)}")

    conn = psycopg2.connect(args.db_url)
    conn.autocommit = False
    updated = 0
    pending = 0
    try:
        with conn.cursor() as cur:
            cur.execute(
                """
                SELECT id, asset_code, asset_type
                FROM scene_platform.asset_resource
                WHERE asset_code LIKE 'LEGACY-%%' AND deleted = false
                ORDER BY id
                """
            )
            rows = cur.fetchall()
            for asset_id, asset_code, asset_type in rows:
                parsed = parse_asset_code(asset_code)
                if asset_type == "TANK":
                    rel, out_name = TANK_MODEL
                    hint = str(args.models_root / rel)
                    url = f"{args.public_base.rstrip('/')}/{out_name}"
                    update_asset(cur, asset_id=asset_id, public_url=url, hint=hint, dry_run=args.dry_run)
                    updated += 1
                elif asset_type == "BUILDING":
                    house_type = None
                    if parsed:
                        _, site_id, legacy_id = parsed
                        house_type = house_types.get(house_key(site_id, legacy_id))
                    rel, out_name = pick_building_model(house_type)
                    hint = str(args.models_root / rel)
                    url = f"{args.public_base.rstrip('/')}/{out_name}"
                    update_asset(cur, asset_id=asset_id, public_url=url, hint=hint, dry_run=args.dry_run)
                    updated += 1
                else:
                    mark_non_mesh(
                        cur,
                        asset_id=asset_id,
                        message="围墙/道路在老栈为挤出几何，非网格模型；暂不提供 GLB",
                        dry_run=args.dry_run,
                    )
                    pending += 1
        if not args.dry_run:
            conn.commit()
        print(f"== done updated={updated} pending_non_mesh={pending}")
    except Exception:
        conn.rollback()
        raise
    finally:
        conn.close()
    return 0


if __name__ == "__main__":
    sys.exit(main())
