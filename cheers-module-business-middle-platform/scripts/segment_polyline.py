#!/usr/bin/env python3
"""折线 → 定长标准段位姿（场景 Y-up：位置 xz，yaw 绕 Y）。"""

from __future__ import annotations

import math
from dataclasses import dataclass
from typing import Sequence


@dataclass(frozen=True)
class SegmentPose:
    x: float
    y: float
    z: float
    yaw_deg: float
    """绕 Y 轴朝向（度），使局部 +X 沿段前进方向。"""
    scale_x: float
    """沿长度方向缩放；完整段为 1，末段不足时 < 1。"""


def _dist(a: Sequence[float], b: Sequence[float]) -> float:
    return math.hypot(b[0] - a[0], b[1] - a[1])


def split_polyline_to_segments(
    points_xz: Sequence[Sequence[float]],
    *,
    segment_length: float,
    y: float = 0.0,
    min_scale: float = 0.15,
) -> list[SegmentPose]:
    """
    将水平折线 (x,z)* 切成定长段。

    每段中心放在段中点；yaw 使局部 +X 指向前进方向；
    末段长度不足 segment_length 时 scale_x = length/segment_length（不低于 min_scale）。
    """
    if segment_length <= 0:
        raise ValueError("segment_length must be > 0")
    if len(points_xz) < 2:
        return []

    # densify into cumulative polyline samples
    verts = [(float(p[0]), float(p[1])) for p in points_xz]
    poses: list[SegmentPose] = []

    for i in range(len(verts) - 1):
        x0, z0 = verts[i]
        x1, z1 = verts[i + 1]
        edge_len = math.hypot(x1 - x0, z1 - z0)
        if edge_len < 1e-6:
            continue
        dx, dz = (x1 - x0) / edge_len, (z1 - z0) / edge_len
        yaw = math.degrees(math.atan2(dx, dz))  # 0 = +Z；与 Three Y-up 常用 yaw 一致需核对
        # Three.js 默认物体朝 -Z；我们约定模板局部 +X 为长度方向。
        # yaw 绕 Y：使 +X 转到 (dx, dz) 水平方向 → yaw = atan2(dx, dz) 不对
        # 旋转 Y=θ 后局部 +X → (cosθ, 0, -sinθ) 若用默认？  
        # Object3D: rotation.y 后 local +X goes to (cos(y), 0, -sin(y)) in Three.
        # Want (cos,0,-sin) = (dx, 0, dz) ⇒ cos=dx, -sin=dz ⇒ yaw = atan2(-dz, dx)... 
        # Actually Three: Ry * (1,0,0) = (cos(y), 0, -sin(y)).
        # Target direction (dx, dz) in XZ: (dx, dz) = (cos(y), -sin(y)) ⇒ cos=dx, sin=-dz ⇒ y = atan2(-dz, dx)
        yaw = math.degrees(math.atan2(-dz, dx))

        cursor = 0.0
        while cursor < edge_len - 1e-6:
            remain = edge_len - cursor
            take = min(segment_length, remain)
            scale_x = take / segment_length
            if scale_x < min_scale and poses:
                # 并入上一段：略增上一段 scale（近似）
                break
            mid = cursor + take / 2
            cx = x0 + dx * mid
            cz = z0 + dz * mid
            poses.append(
                SegmentPose(x=cx, y=y, z=cz, yaw_deg=yaw, scale_x=max(scale_x, min_scale))
            )
            cursor += take

    return poses
