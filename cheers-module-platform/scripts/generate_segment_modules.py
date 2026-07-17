#!/usr/bin/env python3
"""生成墙板/路面标准段 GLB（轴对齐盒），供实例化拼接。"""

from __future__ import annotations

import json
import struct
from pathlib import Path

ROOT = Path(__file__).resolve().parents[3]  # Sinopec/
OUT = ROOT / "ecs-react" / "public" / "scene-models" / "_segments"


def _box_positions(sx: float, sy: float, sz: float) -> list[float]:
    hx, hy, hz = sx / 2, sy / 2, sz / 2
    # 8 corners
    corners = [
        (-hx, -hy, -hz),
        (hx, -hy, -hz),
        (hx, hy, -hz),
        (-hx, hy, -hz),
        (-hx, -hy, hz),
        (hx, -hy, hz),
        (hx, hy, hz),
        (-hx, hy, hz),
    ]
    # 12 triangles as expanded verts (pos only) for simplicity
    faces = [
        (0, 1, 2),
        (0, 2, 3),  # -Z
        (5, 4, 7),
        (5, 7, 6),  # +Z
        (4, 0, 3),
        (4, 3, 7),  # -X
        (1, 5, 6),
        (1, 6, 2),  # +X
        (3, 2, 6),
        (3, 6, 7),  # +Y
        (4, 5, 1),
        (4, 1, 0),  # -Y
    ]
    pos: list[float] = []
    for a, b, c in faces:
        for i in (a, b, c):
            pos.extend(corners[i])
    return pos


def write_box_glb(path: Path, sx: float, sy: float, sz: float, rgb: tuple[float, float, float]) -> None:
    positions = _box_positions(sx, sy, sz)
    pos_bytes = b"".join(struct.pack("<fff", positions[i], positions[i + 1], positions[i + 2]) for i in range(0, len(positions), 3))
    # align to 4
    while len(pos_bytes) % 4:
        pos_bytes += b"\x00"

    vertex_count = len(positions) // 3
    mins = [-sx / 2, -sy / 2, -sz / 2]
    maxs = [sx / 2, sy / 2, sz / 2]

    gltf = {
        "asset": {"version": "2.0", "generator": "generate_segment_modules"},
        "scenes": [{"nodes": [0]}],
        "scene": 0,
        "nodes": [{"mesh": 0, "name": path.parent.name}],
        "meshes": [
            {
                "primitives": [
                    {
                        "attributes": {"POSITION": 0},
                        "material": 0,
                        "mode": 4,
                    }
                ]
            }
        ],
        "materials": [
            {
                "name": "segment",
                "pbrMetallicRoughness": {
                    "baseColorFactor": [rgb[0], rgb[1], rgb[2], 1.0],
                    "metallicFactor": 0.0,
                    "roughnessFactor": 0.85,
                },
            }
        ],
        "accessors": [
            {
                "bufferView": 0,
                "componentType": 5126,
                "count": vertex_count,
                "type": "VEC3",
                "max": maxs,
                "min": mins,
            }
        ],
        "bufferViews": [{"buffer": 0, "byteOffset": 0, "byteLength": len(pos_bytes), "target": 34962}],
        "buffers": [{"byteLength": len(pos_bytes)}],
    }

    json_bytes = json.dumps(gltf, separators=(",", ":")).encode("utf-8")
    while len(json_bytes) % 4:
        json_bytes += b" "

    def chunk(tag: bytes, data: bytes) -> bytes:
        return struct.pack("<I", len(data)) + tag + data

    binary = b"glTF" + struct.pack("<II", 2, 12 + 8 + len(json_bytes) + 8 + len(pos_bytes))
    binary += chunk(b"JSON", json_bytes)
    binary += chunk(b"BIN\x00", pos_bytes)

    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_bytes(binary)
    print(f"wrote {path} ({len(binary)} bytes)")


def main() -> None:
    # X=length along path, Y=up, Z=thickness/width
    write_box_glb(OUT / "wall_panel_1m" / "model.glb", 1.0, 2.5, 0.2, (0.55, 0.58, 0.62))
    write_box_glb(OUT / "road_tile_1m" / "model.glb", 1.0, 0.08, 3.0, (0.28, 0.30, 0.34))


if __name__ == "__main__":
    main()
