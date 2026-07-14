package cn.cheers.x.module.platform.routing.planner;

import cn.cheers.x.module.platform.contract.dto.network.PathEdgeDTO;
import cn.cheers.x.module.platform.contract.dto.network.PathNetworkDTO;
import cn.cheers.x.module.platform.contract.dto.network.PathNodeDTO;
import cn.cheers.x.module.platform.contract.dto.topology.TopologyPointDTO;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 单网络内存邻接视图：按机动剖面过滤边并计算阻抗权重。
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
                if (node != null && StringUtils.hasText(node.getNodeId())) {
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
                if (!isEdgeAllowed(edge, mobilityProfileId)) {
                    continue;
                }
                edgeMap.put(edge.getEdgeId(), edge);
                double weight = resolveWeight(edge, mobilityProfileId);
                adj.computeIfAbsent(edge.getFromNodeId(), k -> new ArrayList<>())
                        .add(new AdjacencyEdge(edge.getEdgeId(), edge.getToNodeId(), weight));
            }
        }
        return new GraphView(nodeMap, edgeMap, adj, mobilityProfileId);
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

    private static boolean isEdgeAllowed(PathEdgeDTO edge, String profileId) {
        List<String> allowed = edge.getAllowedProfileIds();
        if (CollectionUtils.isEmpty(allowed)) {
            return true;
        }
        return allowed.contains(profileId);
    }

    static double resolveWeight(PathEdgeDTO edge, String profileId) {
        Map<String, Double> impedanceByProfile = edge.getImpedanceByProfile();
        if (impedanceByProfile != null && impedanceByProfile.containsKey(profileId)) {
            return impedanceByProfile.get(profileId);
        }
        double waypointDistance = polylineDistance(edge.getWaypoints());
        if (waypointDistance > 0D) {
            return waypointDistance;
        }
        return 1.0D;
    }

    private static double polylineDistance(List<TopologyPointDTO> polyline) {
        if (polyline == null || polyline.size() < 2) {
            return 0D;
        }
        double total = 0D;
        for (int i = 0; i < polyline.size() - 1; i++) {
            TopologyPointDTO a = polyline.get(i);
            TopologyPointDTO b = polyline.get(i + 1);
            if (a == null || b == null) {
                continue;
            }
            double dx = safe(a.getX()) - safe(b.getX());
            double dy = safe(a.getY()) - safe(b.getY());
            double dz = safe(a.getZ()) - safe(b.getZ());
            total += Math.sqrt(dx * dx + dy * dy + dz * dz);
        }
        return total;
    }

    private static double safe(Double value) {
        return value != null ? value : 0D;
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
