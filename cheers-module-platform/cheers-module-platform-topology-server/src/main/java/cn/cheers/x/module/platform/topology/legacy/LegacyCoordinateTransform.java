package cn.cheers.x.module.platform.topology.legacy;

import cn.cheers.x.module.platform.contract.dto.topology.TopologyPointDTO;

/**
 * 将旧库经纬度线性映射为 Scene 局部坐标（以站场中心为原点）。
 */
final class LegacyCoordinateTransform {

    private final double originLng;
    private final double originLat;
    private final double metersPerDegreeLng;
    private final double metersPerDegreeLat;

    LegacyCoordinateTransform(double originLng, double originLat,
                              double metersPerDegreeLng, double metersPerDegreeLat) {
        this.originLng = originLng;
        this.originLat = originLat;
        this.metersPerDegreeLng = metersPerDegreeLng;
        this.metersPerDegreeLat = metersPerDegreeLat;
    }

    TopologyPointDTO fromLngLat(double lng, double lat, double y) {
        double x = (lng - originLng) * metersPerDegreeLng;
        double z = (lat - originLat) * metersPerDegreeLat;
        return TopologyPointDTO.builder().x(x).y(y).z(z).build();
    }

    static LegacyCoordinateTransform fromPoints(Iterable<double[]> lngLatPairs,
                                                LegacyTopologyProperties properties) {
        double minLng = Double.POSITIVE_INFINITY;
        double maxLng = Double.NEGATIVE_INFINITY;
        double minLat = Double.POSITIVE_INFINITY;
        double maxLat = Double.NEGATIVE_INFINITY;
        boolean hasPoint = false;
        for (double[] pair : lngLatPairs) {
            if (pair == null || pair.length < 2) {
                continue;
            }
            hasPoint = true;
            minLng = Math.min(minLng, pair[0]);
            maxLng = Math.max(maxLng, pair[0]);
            minLat = Math.min(minLat, pair[1]);
            maxLat = Math.max(maxLat, pair[1]);
        }
        if (!hasPoint) {
            return new LegacyCoordinateTransform(0D, 0D,
                    properties.getMetersPerDegreeLng(), properties.getMetersPerDegreeLat());
        }
        return new LegacyCoordinateTransform((minLng + maxLng) / 2D, (minLat + maxLat) / 2D,
                properties.getMetersPerDegreeLng(), properties.getMetersPerDegreeLat());
    }
}
