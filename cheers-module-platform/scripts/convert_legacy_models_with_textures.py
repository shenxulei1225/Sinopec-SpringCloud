#!/usr/bin/env python3
"""
用 Assimp 导出老栈 FBX/OBJ → GLB，并把贴图 URI 改成相对文件名，
纹理文件与 model.glb 同目录，供浏览器 GLTFLoader 加载。

用法见 reconvert_legacy_models.py / 本脚本 CLI。
"""

from __future__ import annotations

import argparse
import json
import re
import struct
import subprocess
from pathlib import Path


def parse_glb(data: bytes) -> tuple[dict, bytes]:
    magic, version, length = struct.unpack_from("<III", data, 0)
    if magic != 0x46546C67:
        raise ValueError("not a GLB")
    offset = 12
    json_chunk = None
    bin_chunk = b""
    while offset + 8 <= len(data):
        chunk_len, chunk_type = struct.unpack_from("<II", data, offset)
        offset += 8
        chunk = data[offset : offset + chunk_len]
        offset += chunk_len
        if chunk_type == 0x4E4F534A:  # JSON
            json_chunk = json.loads(chunk.decode("utf-8"))
        elif chunk_type == 0x004E4942:  # BIN
            bin_chunk = chunk
    if json_chunk is None:
        raise ValueError("GLB missing JSON chunk")
    return json_chunk, bin_chunk


def build_glb(doc: dict, bin_chunk: bytes) -> bytes:
    json_bytes = json.dumps(doc, separators=(",", ":"), ensure_ascii=False).encode("utf-8")
    json_pad = (4 - (len(json_bytes) % 4)) % 4
    json_bytes += b" " * json_pad
    bin_pad = (4 - (len(bin_chunk) % 4)) % 4
    bin_out = bin_chunk + (b"\x00" * bin_pad)
    total = 12 + 8 + len(json_bytes) + 8 + len(bin_out)
    out = bytearray()
    out += struct.pack("<III", 0x46546C67, 2, total)
    out += struct.pack("<II", len(json_bytes), 0x4E4F534A)
    out += json_bytes
    out += struct.pack("<II", len(bin_out), 0x004E4942)
    out += bin_out
    return bytes(out)


def basename_of_uri(uri: str) -> str:
    # d:\\Documents\\d\\M_03___Default.png or paths with /
    cleaned = uri.replace("\\", "/")
    return cleaned.rsplit("/", 1)[-1]


def rewrite_image_uris(doc: dict, texture_dir: Path, copy_dir: Path) -> int:
    """把 images[].uri 改成 basename，并从 texture_dir 复制到 copy_dir。返回成功数。"""
    images = doc.get("images") or []
    ok = 0
    copy_dir.mkdir(parents=True, exist_ok=True)
    for img in images:
        uri = img.get("uri")
        if not uri or str(uri).startswith("data:"):
            continue
        name = basename_of_uri(str(uri))
        src = texture_dir / name
        if not src.is_file():
            # try case-insensitive
            matches = list(texture_dir.glob(name))
            if not matches:
                matches = [p for p in texture_dir.iterdir() if p.name.lower() == name.lower()]
            src = matches[0] if matches else None
        if src is None or not src.is_file():
            print(f"  WARN missing texture: {name}")
            continue
        dest = copy_dir / src.name
        if not dest.exists() or dest.stat().st_size != src.stat().st_size:
            dest.write_bytes(src.read_bytes())
        img["uri"] = dest.name  # relative to GLB directory
        ok += 1
    return ok


def assimp_export(src: Path, dest_glb: Path) -> None:
    dest_glb.parent.mkdir(parents=True, exist_ok=True)
    # 在源文件目录执行，便于相对贴图解析
    proc = subprocess.run(
        ["assimp", "export", src.name, str(dest_glb), "-fglb2"],
        cwd=str(src.parent),
        capture_output=True,
        text=True,
    )
    if proc.returncode != 0 or not dest_glb.is_file():
        raise RuntimeError(f"assimp failed: {src}\n{proc.stdout}\n{proc.stderr}")


def convert_one(src: Path, out_dir: Path) -> Path:
    """
    输出 out_dir/model.glb + 同目录贴图。
    """
    out_dir.mkdir(parents=True, exist_ok=True)
    tmp = out_dir / "_tmp.glb"
    assimp_export(src, tmp)
    data = tmp.read_bytes()
    doc, bin_chunk = parse_glb(data)
    fixed = rewrite_image_uris(doc, src.parent, out_dir)
    print(f"  textures linked: {fixed}")
    final = out_dir / "model.glb"
    final.write_bytes(build_glb(doc, bin_chunk))
    tmp.unlink(missing_ok=True)
    return final


# 与老栈一致的模型清单
MODEL_SPECS = [
    ("oiltank", "oiltank/d.fbx"),
    ("house1", "house1/a.fbx"),
    ("house2", "house2/c.fbx"),
    ("house3", "house3/b.fbx"),
    ("xiaofang", "xiaofang.obj"),
    ("aifeier", "aifeier.obj"),
    # 设备类（编排暂未用，先备好）
    ("famen", "famen.fbx"),
    ("yibiao", "yibiao.fbx"),
    ("robot", "robot.fbx"),
    ("leak", "leak.fbx"),
    ("uav", "uav/e.fbx"),
]


def main() -> int:
    ap = argparse.ArgumentParser()
    ap.add_argument("--models-root", type=Path, required=True)
    ap.add_argument("--out-root", type=Path, required=True)
    args = ap.parse_args()

    for folder, rel in MODEL_SPECS:
        src = args.models_root / rel
        if not src.is_file():
            print(f"SKIP missing {src}")
            continue
        out_dir = args.out_root / folder
        print(f"== {folder} from {rel}")
        convert_one(src, out_dir)
        print(f"  -> {out_dir / 'model.glb'}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
