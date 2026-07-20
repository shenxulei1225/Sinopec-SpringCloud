#!/usr/bin/env python3
"""
站场局部场景坐标：站心东-北-天（ENU）米制（等距矩形近似）。

轴约定（与三维画布 / 老栈一致）：
  X = 东，Y = 上，Z = −北（朝南为正 Z）

历史：曾用 Web 墨卡托差分当「米」，在纬度 φ 处相对真地面放大约 sec(φ)
（金桥 ≈1.21）。现改为按站心纬度乘 cos(φ0) 的局部米，与 GIS 真经纬度尺度一致。

正变换：WGS84 → (x, y, z)
逆变换：局部 (x, z) → WGS84（忽略高度）
"""

from __future__ import annotations

import math

# WGS84 平均地球半径（米），等距矩形用
EARTH_RADIUS_M = 6378137.0

# 仅供对照/废弃路径；勿再用于新导入
WEB_MERCATOR_RADIUS = 6378137.0


def lon_lat_to_mercator(lon: float, lat: float) -> tuple[float, float]:
    """Web 墨卡托（历史对照）。新逻辑请用 lon_lat_to_scene。"""
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
    WGS84 → 局部场景 (x, y, z)。
    东向 x、北向差分映到 −z；高度 y。
    """
    lat0 = math.radians(origin_lat)
    dlon = math.radians(lon - origin_lon)
    dlat = math.radians(lat - origin_lat)
    x = EARTH_RADIUS_M * dlon * math.cos(lat0)
    north = EARTH_RADIUS_M * dlat
    return x, float(height), -north


def scene_to_lon_lat(
    x: float,
    z: float,
    *,
    origin_lon: float,
    origin_lat: float,
) -> tuple[float, float]:
    """
    局部 (x, z) → WGS84 (lon, lat)。逆变换于 lon_lat_to_scene（忽略高度）。
    """
    lat0 = math.radians(origin_lat)
    # z = -north ⇒ north = -z
    north = -float(z)
    dlat = north / EARTH_RADIUS_M
    dlon = float(x) / (EARTH_RADIUS_M * math.cos(lat0))
    lon = origin_lon + math.degrees(dlon)
    lat = origin_lat + math.degrees(dlat)
    return lon, lat
