package cn.cheers.x.module.platform.topology.legacy;

import cn.cheers.x.module.platform.contract.dto.topology.TopologyPointDTO;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 解析旧库 route 字段：{@code lng,lat>lng,lat>...}，末尾可能带 checkId。
 */
final class LegacyRouteParser {

    private LegacyRouteParser() {
    }

    static List<TopologyPointDTO> parseWaypoints(String route, LegacyCoordinateTransform transform) {
        if (!StringUtils.hasText(route) || transform == null) {
            return List.of();
        }
        List<TopologyPointDTO> points = new ArrayList<>();
        for (String segment : route.split(">")) {
            if (!StringUtils.hasText(segment)) {
                continue;
            }
            String trimmed = segment.trim();
            if (!trimmed.contains(",")) {
                continue;
            }
            String[] parts = trimmed.split(",", 2);
            if (parts.length < 2) {
                continue;
            }
            Double lng = parseDouble(parts[0]);
            Double lat = parseDouble(parts[1]);
            if (lng == null || lat == null) {
                continue;
            }
            points.add(transform.fromLngLat(lng, lat, 1D));
        }
        return points;
    }

    private static Double parseDouble(String raw) {
        if (!StringUtils.hasText(raw)) {
            return null;
        }
        try {
            return Double.parseDouble(raw.trim());
        } catch (NumberFormatException ignored) {
            return null;
        }
    }
}
