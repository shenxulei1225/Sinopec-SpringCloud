package cn.cheers.x.module.platform.topology.legacy;

import cn.cheers.x.module.platform.contract.dto.topology.TopologyEdgeDTO;
import cn.cheers.x.module.platform.contract.dto.topology.TopologyNodeDTO;
import cn.cheers.x.module.platform.contract.dto.topology.TopologyPointDTO;
import cn.cheers.x.module.platform.contract.dto.topology.ZoneBoundaryDTO;
import cn.cheers.x.module.platform.topology.api.dto.TopologyGraphSaveReqDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static cn.cheers.x.module.platform.topology.enums.ErrorCodeConstants.LEGACY_TOPOLOGY_DISABLED;
import static cn.cheers.x.module.platform.topology.enums.ErrorCodeConstants.LEGACY_TOPOLOGY_EMPTY;
import static cn.cheers.x.module.platform.topology.enums.ErrorCodeConstants.LEGACY_TOPOLOGY_SOURCE_UNAVAILABLE;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;

/**
 * 从旧库 bs_check_point* 读取并转换为拓扑图草稿结构。
 */
@Slf4j
@Component
public class LegacyTopologyImportAdapter {

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Resource
    private LegacyTopologyProperties properties;

    public TopologyGraphSaveReqDTO importGraph(Long siteId) {
        if (!properties.isEnabled()) {
            throw exception(LEGACY_TOPOLOGY_DISABLED);
        }
        String legacySiteId = String.valueOf(siteId);
        List<LegacyCheckPointRow> checkPoints;
        try {
            checkPoints = loadCheckPoints(legacySiteId);
        } catch (Exception ex) {
            log.warn("legacy topology import failed for siteId={}: {}", siteId, ex.getMessage());
            throw exception(LEGACY_TOPOLOGY_SOURCE_UNAVAILABLE);
        }
        if (CollectionUtils.isEmpty(checkPoints)) {
            throw exception(LEGACY_TOPOLOGY_EMPTY);
        }

        List<double[]> lngLatPairs = new ArrayList<>();
        for (LegacyCheckPointRow row : checkPoints) {
            Double lng = parseDouble(row.longitude());
            Double lat = parseDouble(row.latitude());
            if (lng != null && lat != null) {
                lngLatPairs.add(new double[]{lng, lat});
            }
        }
        LegacyCoordinateTransform transform = LegacyCoordinateTransform.fromPoints(lngLatPairs, properties);

        List<TopologyNodeDTO> nodes = new ArrayList<>();
        Map<String, LegacyCheckPointRow> checkPointById = new LinkedHashMap<>();
        for (LegacyCheckPointRow row : checkPoints) {
            if (!StringUtils.hasText(row.checkId())) {
                continue;
            }
            checkPointById.put(row.checkId(), row);
            Double lng = parseDouble(row.longitude());
            Double lat = parseDouble(row.latitude());
            TopologyPointDTO position = (lng != null && lat != null)
                    ? transform.fromLngLat(lng, lat, resolveHeightY(row))
                    : TopologyPointDTO.builder().x(0D).y(1D).z(0D).build();
            Map<String, Object> payload = new HashMap<>();
            payload.put("legacyCheckType", row.checkType());
            payload.put("legacyTankGroupId", row.tankGroupId());
            payload.put("legacyDeviceType", row.deviceType());
            nodes.add(TopologyNodeDTO.builder()
                    .nodeId(row.checkId())
                    .nodeType(mapNodeType(row.checkType()))
                    .displayName(StringUtils.hasText(row.checkName()) ? row.checkName() : row.checkId())
                    .position(position)
                    .payload(payload)
                    .build());
        }

        List<TopologyEdgeDTO> edges = new ArrayList<>();
        Set<String> edgeKeys = new HashSet<>();
        appendDistanceRouteEdges(legacySiteId, transform, edges, edgeKeys);
        appendWeightEdges(legacySiteId, edges, edgeKeys);

        List<ZoneBoundaryDTO> zoneBoundaries = buildZoneBoundaries(checkPointById, transform);

        return TopologyGraphSaveReqDTO.builder()
                .nodes(nodes)
                .edges(edges)
                .zoneBoundaries(zoneBoundaries)
                .build();
    }

    private void appendDistanceRouteEdges(String legacySiteId, LegacyCoordinateTransform transform,
                                          List<TopologyEdgeDTO> edges, Set<String> edgeKeys) {
        List<LegacyDistanceRouteRow> routes = loadDistanceRoutes(legacySiteId);
        for (LegacyDistanceRouteRow row : routes) {
            if (!StringUtils.hasText(row.startCheckId()) || !StringUtils.hasText(row.endCheckId())) {
                continue;
            }
            String key = edgeKey(row.startCheckId(), row.endCheckId());
            if (!edgeKeys.add(key)) {
                continue;
            }
            List<TopologyPointDTO> waypoints = LegacyRouteParser.parseWaypoints(row.route(), transform);
            edges.add(TopologyEdgeDTO.builder()
                    .edgeId("legacy_dr_" + row.id())
                    .fromNodeId(row.startCheckId())
                    .toNodeId(row.endCheckId())
                    .distanceMeters(row.distance())
                    .weight(row.weight())
                    .waypoints(waypoints.isEmpty() ? null : waypoints)
                    .scope(StringUtils.hasText(row.tankGroupId()) ? "intra_zone" : "inter_zone")
                    .build());
        }
    }

    private void appendWeightEdges(String legacySiteId, List<TopologyEdgeDTO> edges, Set<String> edgeKeys) {
        List<LegacyWeightRow> weights = loadWeights(legacySiteId);
        for (LegacyWeightRow row : weights) {
            if (!StringUtils.hasText(row.startCheckId()) || !StringUtils.hasText(row.endCheckId())) {
                continue;
            }
            String key = edgeKey(row.startCheckId(), row.endCheckId());
            if (!edgeKeys.add(key)) {
                continue;
            }
            edges.add(TopologyEdgeDTO.builder()
                    .edgeId("legacy_w_" + row.id())
                    .fromNodeId(row.startCheckId())
                    .toNodeId(row.endCheckId())
                    .weight(row.weight())
                    .scope("weight_only")
                    .build());
        }
    }

    private List<ZoneBoundaryDTO> buildZoneBoundaries(Map<String, LegacyCheckPointRow> checkPointById,
                                                      LegacyCoordinateTransform transform) {
        Map<String, List<LegacyCheckPointRow>> byTankGroup = new HashMap<>();
        for (LegacyCheckPointRow row : checkPointById.values()) {
            if (!StringUtils.hasText(row.tankGroupId())) {
                continue;
            }
            byTankGroup.computeIfAbsent(row.tankGroupId(), k -> new ArrayList<>()).add(row);
        }
        List<ZoneBoundaryDTO> zones = new ArrayList<>();
        double pad = properties.getZonePaddingMeters();
        for (Map.Entry<String, List<LegacyCheckPointRow>> entry : byTankGroup.entrySet()) {
            String tankGroupId = entry.getKey();
            double minX = Double.POSITIVE_INFINITY;
            double maxX = Double.NEGATIVE_INFINITY;
            double minZ = Double.POSITIVE_INFINITY;
            double maxZ = Double.NEGATIVE_INFINITY;
            for (LegacyCheckPointRow row : entry.getValue()) {
                Double lng = parseDouble(row.longitude());
                Double lat = parseDouble(row.latitude());
                if (lng == null || lat == null) {
                    continue;
                }
                TopologyPointDTO p = transform.fromLngLat(lng, lat, 0.2D);
                minX = Math.min(minX, safe(p.getX()));
                maxX = Math.max(maxX, safe(p.getX()));
                minZ = Math.min(minZ, safe(p.getZ()));
                maxZ = Math.max(maxZ, safe(p.getZ()));
            }
            if (!Double.isFinite(minX)) {
                continue;
            }
            zones.add(ZoneBoundaryDTO.builder()
                    .zoneId("zone_" + tankGroupId)
                    .zoneCode(tankGroupId)
                    .displayName("罐组 " + tankGroupId)
                    .polygon(List.of(
                            point(minX - pad, 0.2D, minZ - pad),
                            point(maxX + pad, 0.2D, minZ - pad),
                            point(maxX + pad, 0.2D, maxZ + pad),
                            point(minX - pad, 0.2D, maxZ + pad)
                    ))
                    .build());
        }
        return zones;
    }

    private List<LegacyCheckPointRow> loadCheckPoints(String legacySiteId) {
        String sql = """
                SELECT check_id, check_name, dept_id, tank_group_id, longitude, latitude,
                       check_type, weight, height, device_type
                FROM %s.%s
                WHERE %s = ?
                """.formatted(qualifiedSchema(), properties.getCheckPointTable(), properties.getSiteColumn());
        return jdbcTemplate.query(sql, (rs, rowNum) -> new LegacyCheckPointRow(
                rs.getString("check_id"),
                rs.getString("check_name"),
                rs.getString("dept_id"),
                rs.getString("tank_group_id"),
                rs.getString("longitude"),
                rs.getString("latitude"),
                rs.getObject("check_type") != null ? rs.getInt("check_type") : null,
                rs.getObject("weight") != null ? rs.getInt("weight") : null,
                rs.getObject("height") != null ? rs.getDouble("height") : null,
                rs.getObject("device_type") != null ? rs.getInt("device_type") : null
        ), legacySiteId);
    }

    private List<LegacyDistanceRouteRow> loadDistanceRoutes(String legacySiteId) {
        String sql = """
                SELECT id, dept_id, tank_group_id, start_check_id, end_check_id, distance, weight, route
                FROM %s.%s
                WHERE %s = ?
                """.formatted(qualifiedSchema(), properties.getDistanceRouteTable(), properties.getSiteColumn());
        return jdbcTemplate.query(sql, (rs, rowNum) -> new LegacyDistanceRouteRow(
                rs.getLong("id"),
                rs.getString("dept_id"),
                rs.getString("tank_group_id"),
                rs.getString("start_check_id"),
                rs.getString("end_check_id"),
                rs.getObject("distance") != null ? rs.getLong("distance") : null,
                rs.getObject("weight") != null ? rs.getInt("weight") : null,
                rs.getString("route")
        ), legacySiteId);
    }

    private List<LegacyWeightRow> loadWeights(String legacySiteId) {
        String sql = """
                SELECT id, dept_id, tank_group_id, start_check_id, end_check_id, weight
                FROM %s.%s
                WHERE %s = ?
                """.formatted(qualifiedSchema(), properties.getWeightTable(), properties.getSiteColumn());
        return jdbcTemplate.query(sql, (rs, rowNum) -> new LegacyWeightRow(
                rs.getLong("id"),
                rs.getString("dept_id"),
                rs.getString("tank_group_id"),
                rs.getString("start_check_id"),
                rs.getString("end_check_id"),
                rs.getObject("weight") != null ? rs.getInt("weight") : null
        ), legacySiteId);
    }

    private String qualifiedSchema() {
        return properties.getSchema();
    }

    private static String mapNodeType(Integer checkType) {
        if (checkType == null) {
            return "checkpoint";
        }
        return switch (checkType) {
            case 0 -> "waypoint";
            case 2 -> "route_node";
            case 3, 4, 7, 8, 10 -> "wall_point";
            case 5, 6, 11, 12 -> "drone_point";
            case 9 -> "bridge_point";
            case 1, 13, 14 -> "checkpoint";
            default -> "checkpoint";
        };
    }

    private static double resolveHeightY(LegacyCheckPointRow row) {
        if (row.height() != null && row.height() > 0) {
            return row.height();
        }
        return 1D;
    }

    private static String edgeKey(String from, String to) {
        return from + "->" + to;
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

    private static double safe(Double v) {
        return v != null ? v : 0D;
    }

    private static TopologyPointDTO point(double x, double y, double z) {
        return TopologyPointDTO.builder().x(x).y(y).z(z).build();
    }

    private record LegacyCheckPointRow(
            String checkId,
            String checkName,
            String deptId,
            String tankGroupId,
            String longitude,
            String latitude,
            Integer checkType,
            Integer weight,
            Double height,
            Integer deviceType
    ) {
    }

    private record LegacyDistanceRouteRow(
            Long id,
            String deptId,
            String tankGroupId,
            String startCheckId,
            String endCheckId,
            Long distance,
            Integer weight,
            String route
    ) {
    }

    private record LegacyWeightRow(
            Long id,
            String deptId,
            String tankGroupId,
            String startCheckId,
            String endCheckId,
            Integer weight
    ) {
    }
}
