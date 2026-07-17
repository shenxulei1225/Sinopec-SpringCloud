#!/usr/bin/env python3
"""与老栈 Sinopec_ecs/src/utils/mapProjection.js 一致的 Web 墨卡托场景坐标。"""

from __future__ import annotations

import math

WEB_MERCATOR_RADIUS = 6378137.0


def lon_lat_to_mercator(lon: float, lat: float) -> tuple[float, float]:
    clamped = max(min(lat, 85.0511287798), -85.0511287798)
    lon_rad = math.radians(lon)
    lat_rad = math.radians(clamped)
    x = WEB_MERCATOR_RADIUS * lon_rad
    y = WEB_MERCATOR_RADIUS * math.log(math.tan(math.pi / 4 + lat_rad / 2))
    return x, y


def lon_lat_to_scene(
    lon: float,
    lat: float,
    *,
    origin_lon: float,
    origin_lat: float,
    height: float = 0.0,
) -> tuple[float, float, float]:
    """
    返回 (x, y, z)：X 东、Y 上、Z = -北向差分（老栈 lonLatToScene）。
    """
    mx, my = lon_lat_to_mercator(lon, lat)
    ox, oy = lon_lat_to_mercator(origin_lon, origin_lat)
    return mx - ox, float(height), -(my - oy)
