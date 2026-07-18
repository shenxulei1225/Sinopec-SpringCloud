package cn.cheers.x.scene.platform.service.coordinate.support;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CoordinateMathSupport {

    private static final double WGS84_A = 6378137.0D;
    private static final double WGS84_F = 1.0D / 298.257223563D;
    private static final double WGS84_B = WGS84_A * (1.0D - WGS84_F);
    private static final double WGS84_E2 = 1.0D - (WGS84_B * WGS84_B) / (WGS84_A * WGS84_A);
    private static final double WGS84_EP2 = (WGS84_A * WGS84_A - WGS84_B * WGS84_B) / (WGS84_B * WGS84_B);

    public static double[] geographicToEcef(double lngDeg, double latDeg, double height) {
        double lng = Math.toRadians(lngDeg);
        double lat = Math.toRadians(latDeg);
        double sinLat = Math.sin(lat);
        double cosLat = Math.cos(lat);
        double sinLng = Math.sin(lng);
        double cosLng = Math.cos(lng);
        double n = WGS84_A / Math.sqrt(1.0D - WGS84_E2 * sinLat * sinLat);
        double x = (n + height) * cosLat * cosLng;
        double y = (n + height) * cosLat * sinLng;
        double z = (n * (1.0D - WGS84_E2) + height) * sinLat;
        return new double[]{x, y, z};
    }

    public static double[] ecefToGeographic(double x, double y, double z) {
        double p = Math.sqrt(x * x + y * y);
        double theta = Math.atan2(z * WGS84_A, p * WGS84_B);
        double sinTheta = Math.sin(theta);
        double cosTheta = Math.cos(theta);
        double lng = Math.atan2(y, x);
        double lat = Math.atan2(z + WGS84_EP2 * WGS84_B * sinTheta * sinTheta * sinTheta,
                p - WGS84_E2 * WGS84_A * cosTheta * cosTheta * cosTheta);
        double sinLat = Math.sin(lat);
        double n = WGS84_A / Math.sqrt(1.0D - WGS84_E2 * sinLat * sinLat);
        double height = p / Math.cos(lat) - n;
        return new double[]{Math.toDegrees(lng), Math.toDegrees(lat), height};
    }

    public static double[] ecefToEnu(double x, double y, double z,
                                     double originLngDeg, double originLatDeg, double originHeight) {
        double[] originEcef = geographicToEcef(originLngDeg, originLatDeg, originHeight);
        double dx = x - originEcef[0];
        double dy = y - originEcef[1];
        double dz = z - originEcef[2];

        double lng = Math.toRadians(originLngDeg);
        double lat = Math.toRadians(originLatDeg);
        double sinLng = Math.sin(lng);
        double cosLng = Math.cos(lng);
        double sinLat = Math.sin(lat);
        double cosLat = Math.cos(lat);

        double east = -sinLng * dx + cosLng * dy;
        double north = -sinLat * cosLng * dx - sinLat * sinLng * dy + cosLat * dz;
        double up = cosLat * cosLng * dx + cosLat * sinLng * dy + sinLat * dz;
        return new double[]{east, north, up};
    }

    public static double[] enuToEcef(double east, double north, double up,
                                     double originLngDeg, double originLatDeg, double originHeight) {
        double[] originEcef = geographicToEcef(originLngDeg, originLatDeg, originHeight);

        double lng = Math.toRadians(originLngDeg);
        double lat = Math.toRadians(originLatDeg);
        double sinLng = Math.sin(lng);
        double cosLng = Math.cos(lng);
        double sinLat = Math.sin(lat);
        double cosLat = Math.cos(lat);

        double dx = -sinLng * east - sinLat * cosLng * north + cosLat * cosLng * up;
        double dy = cosLng * east - sinLat * sinLng * north + cosLat * sinLng * up;
        double dz = cosLat * north + sinLat * up;

        return new double[]{originEcef[0] + dx, originEcef[1] + dy, originEcef[2] + dz};
    }

    public static double[] localToEngine(String engineFrameType, double x, double y, double z) {
        if ("UE".equalsIgnoreCase(engineFrameType)) {
            return new double[]{x, y, z};
        }
        return new double[]{x, y, z};
    }

    public static double[] engineToLocal(String engineFrameType, double x, double y, double z) {
        if ("UE".equalsIgnoreCase(engineFrameType)) {
            return new double[]{x, y, z};
        }
        return new double[]{x, y, z};
    }

    public static BigDecimal decimal(double value) {
        return BigDecimal.valueOf(value).setScale(6, RoundingMode.HALF_UP);
    }
}
