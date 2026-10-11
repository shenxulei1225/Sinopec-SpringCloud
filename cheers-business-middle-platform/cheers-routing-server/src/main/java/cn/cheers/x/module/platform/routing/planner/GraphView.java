package cn.cheers.x.module.platform.routing.planner;

import cn.cheers.x.module.platform.contract.dto.network.PathEdgeDTO;
import cn.cheers.x.module.platform.contract.dto.network.PathNetworkDTO;
import cn.cheers.x.module.platform.contract.dto.network.PathNodeDTO;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;

/**
 * 单网络内存邻接视图：只留下当前巡检方式归属网上的点，再按通断过滤边。
 * <p>人员对应 person_walk，地面机器人对应 ground_robot，无人机对应 uav_low。
 * 点的归属没写、或没勾这种设备时，这个点不进这张图。
 * 不负责：设备速度、路径耗时。耗时由规划结果的总里程除以该巡检方式的速度。
 * 边长先用这条边已经写下的米数。人员和地面机器人共用的地面边是同一份米数。
 * 没写下时，用两端点经纬度的地面距离补上，不另用场景三维直线。
 * 点上没有经纬度、也没有写下米数的边不进图，不按 1 米计。
 */
public final class GraphView {

    private final Map<String, PathNodeDTO> nodes;
    private final Map<String, PathEdgeDTO> edgesById;
    private final Map<String, List<AdjacencyEdge>> adjacency;
    private final String mobilityProfileId;

    private GraphView(Map<String, PathNodeDTO> nodes,
                      Map<String, PathEdgeDTO> edgesById,
                      Map<String, List<AdjacencyEdge>> adjacency,
                      String mobilityProfileId) {
        this.nodes = nodes;
        this.edgesById = edgesById;
        this.adjacency = adjacency;
        this.mobilityProfileId = mobilityProfileId;
    }

    public static GraphView from(PathNetworkDTO network, String mobilityProfileId) {
        Map<String, PathNodeDTO> nodeMap = new HashMap<>();
        if (network.getNodes() != null) {
            for (PathNodeDTO node : network.getNodes()) {
                if (node != null && StringUtils.hasText(node.getNodeId())
                        && nodeBelongsToProfile(node, mobilityProfileId)) {
                    nodeMap.put(node.getNodeId(), node);
                }
            }
        }

        Map<String, PathEdgeDTO> edgeMap = new HashMap<>();
        Map<String, List<AdjacencyEdge>> adj = new HashMap<>();
        if (network.getEdges() != null) {
            for (PathEdgeDTO edge : network.getEdges()) {
                if (edge == null || !StringUtils.hasText(edge.getEdgeId())
                        || !StringUtils.hasText(edge.getFromNodeId())
                        || !StringUtils.hasText(edge.getToNodeId())) {
                    continue;
                }
                if (!nodeMap.containsKey(edge.getFromNodeId()) || !nodeMap.containsKey(edge.getToNodeId())) {
                    continue;
                }
                if (!isEdgeAllowed(edge, mobilityProfileId)) {
                    continue;
                }
                OptionalDouble weight = resolveWeight(edge, nodeMap);
                if (weight.isEmpty()) {
                    continue;
                }
                edgeMap.put(edge.getEdgeId(), edge);
                adj.computeIfAbsent(edge.getFromNodeId(), k -> new ArrayList<>())
                        .add(new AdjacencyEdge(edge.getEdgeId(), edge.getToNodeId(), weight.getAsDouble()));
            }
        }
        return new GraphView(nodeMap, edgeMap, adj, mobilityProfileId);
    }

    /**
     * 由 {@link PortalGraphAssembler} 合并多网后构造的超级图视图。
     */
    static GraphView assembled(Map<String, PathNodeDTO> nodes,
                               Map<String, PathEdgeDTO> edgesById,
                               Map<String, List<AdjacencyEdge>> adjacency,
                               String mobilityProfileId) {
        return new GraphView(nodes, edgesById, adjacency, mobilityProfileId);
    }

    public PathNodeDTO getNode(String nodeId) {
        return nodes.get(nodeId);
    }

    public PathEdgeDTO getEdge(String edgeId) {
        return edgesById.get(edgeId);
    }

    public List<AdjacencyEdge> outgoing(String fromNodeId) {
        return adjacency.getOrDefault(fromNodeId, List.of());
    }

    public String getMobilityProfileId() {
        return mobilityProfileId;
    }

    /**
     * 这个点是否进入当前走法的图。
     * 人员、地面机器人、无人机按点上的归属网收：没写归属、或没勾这种设备，就不进图。
     * 其它走法不按归属网收点。
     */
    public static boolean nodeBelongsToProfile(PathNodeDTO node, String profileId) {
        if (node == null) {
            return false;
        }
        String membership = membershipForProfile(profileId);
        if (membership == null) {
            return true;
        }
        List<String> memberships = node.getMemberships();
        if (CollectionUtils.isEmpty(memberships)) {
            return false;
        }
        for (String item : memberships) {
            if (item != null && membership.equals(item.trim().toUpperCase())) {
                return true;
            }
        }
        return false;
    }

    /** 走法编号对应点上的归属：人员 / 地面机器人 / 无人机。对不上就不算这种设备的网。 */
    public static String membershipForProfile(String profileId) {
        if (!StringUtils.hasText(profileId)) {
            return null;
        }
        String normalized = profileId.trim().toLowerCase();
        if ("person_walk".equals(normalized) || "human".equals(normalized)) {
            return "HUMAN";
        }
        if ("ground_robot".equals(normalized)) {
            return "GROUND_ROBOT";
        }
        if ("uav_low".equals(normalized) || "uav".equals(normalized)) {
            return "UAV";
        }
        return null;
    }

    private static boolean isEdgeAllowed(PathEdgeDTO edge, String profileId) {
        String traversability = edge.getTraversability();
        if (StringUtils.hasText(traversability)) {
            String normalized = traversability.trim().toUpperCase();
            if ("BLOCKED".equals(normalized) || "CLOSED".equals(normalized)) {
                return false;
            }
        }
        List<String> allowed = edge.getAllowedProfileIds();
        if (CollectionUtils.isEmpty(allowed)) {
            return true;
        }
        return allowed.contains(profileId);
    }

    /**
     * 这条边走多远（米）。规划用它选路，段里程也是这些边长相加。
     * 先用边上已经写下的米数。人员和地面机器人走同一条边时用这一份，不按走法编号再找一份。
     * 没写下时，用两端点经纬度的地面距离，和编辑路网时写下的是同一种算法。
     * 点上没有经纬度则 empty，调用方不得改用场景直线，也不得当成 1 米。
     */
    public static OptionalDouble resolveWeight(PathEdgeDTO edge, Map<String, PathNodeDTO> nodes) {
        OptionalDouble stored = storedMeters(edge);
        if (stored.isPresent()) {
            return stored;
        }
        return endpointGpsMeters(edge, nodes);
    }

    /**
     * 边上已经写下的米数。编辑路网时把同一份长度记在各走法键上，这里认这个数，含 0。
     * 多个键数值不一致时不挑一个，交给两端点的 GPS。
     */
    private static OptionalDouble storedMeters(PathEdgeDTO edge) {
        Map<String, Double> impedanceByProfile = edge == null ? null : edge.getImpedanceByProfile();
        if (impedanceByProfile == null || impedanceByProfile.isEmpty()) {
            return OptionalDouble.empty();
        }
        Double meters = null;
        for (Double raw : impedanceByProfile.values()) {
            if (raw == null || !Double.isFinite(raw) || raw < 0D) {
                continue;
            }
            if (meters == null) {
                meters = raw;
                continue;
            }
            if (Math.abs(meters - raw) > 0.001D) {
                return OptionalDouble.empty();
            }
        }
        return meters == null ? OptionalDouble.empty() : OptionalDouble.of(meters);
    }

    /** 两端点经纬度的地面距离。没有经纬度时 empty。 */
    private static OptionalDouble endpointGpsMeters(PathEdgeDTO edge, Map<String, PathNodeDTO> nodes) {
        if (edge == null || nodes == null) {
            return OptionalDouble.empty();
        }
        double[] from = readGps(nodes.get(edge.getFromNodeId()));
        double[] to = readGps(nodes.get(edge.getToNodeId()));
        if (from == null || to == null) {
            return OptionalDouble.empty();
        }
        double meters = gpsDistanceMeters(from[0], from[1], to[0], to[1]);
        return OptionalDouble.of(Math.round(meters * 100D) / 100D);
    }

    /** 点上保存的经纬度。这是场景落点按站场原点换出来的数，边长只用它。 */
    private static double[] readGps(PathNodeDTO node) {
        if (node == null || node.getPayload() == null) {
            return null;
        }
        Double lon = payloadNumber(node.getPayload(), "longitude", "lng", "lon");
        Double lat = payloadNumber(node.getPayload(), "latitude", "lat");
        if (lon == null || lat == null) {
            return null;
        }
        if (lon < -180D || lon > 180D || lat < -90D || lat > 90D) {
            return null;
        }
        return new double[] { lon, lat };
    }

    private static Double payloadNumber(Map<String, Object> payload, String... keys) {
        for (String key : keys) {
            Object raw = payload.get(key);
            if (raw instanceof Number number) {
                double value = number.doubleValue();
                if (Double.isFinite(value)) {
                    return value;
                }
            }
        }
        return null;
    }

    /** 两点经纬度的地面距离（米）。与编辑路网时写边长用的是同一个半径。 */
    public static double gpsDistanceMeters(double lon1, double lat1, double lon2, double lat2) {
        double lat1Rad = Math.toRadians(lat1);
        double lat2Rad = Math.toRadians(lat2);
        double dLat = lat2Rad - lat1Rad;
        double dLon = Math.toRadians(lon2 - lon1);
        double h = Math.sin(dLat / 2D) * Math.sin(dLat / 2D)
                + Math.cos(lat1Rad) * Math.cos(lat2Rad) * Math.sin(dLon / 2D) * Math.sin(dLon / 2D);
        return 2D * 6378137D * Math.asin(Math.min(1D, Math.sqrt(h)));
    }

    /** 这条边在本图上的米数。段里程把路径上的边按这个相加。 */
    public OptionalDouble edgeDistanceMeters(PathEdgeDTO edge) {
        return resolveWeight(edge, nodes);
    }

    public static final class AdjacencyEdge {

        private final String edgeId;
        private final String toNodeId;
        private final double weight;

        public AdjacencyEdge(String edgeId, String toNodeId, double weight) {
            this.edgeId = edgeId;
            this.toNodeId = toNodeId;
            this.weight = weight;
        }

        public String getEdgeId() {
            return edgeId;
        }

        public String getToNodeId() {
            return toNodeId;
        }

        public double getWeight() {
            return weight;
        }
    }
}
