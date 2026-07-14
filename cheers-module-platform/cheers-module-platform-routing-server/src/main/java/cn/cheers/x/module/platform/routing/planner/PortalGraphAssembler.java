package cn.cheers.x.module.platform.routing.planner;

import cn.cheers.x.module.platform.contract.dto.network.MobilityProfileDTO;
import cn.cheers.x.module.platform.contract.dto.network.PathEdgeDTO;
import cn.cheers.x.module.platform.contract.dto.network.PathNetworkDTO;
import cn.cheers.x.module.platform.contract.dto.network.PathNodeDTO;
import cn.cheers.x.module.platform.contract.dto.network.PortalDTO;
import cn.cheers.x.module.platform.contract.enums.NetworkKind;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 将多张路径网络与 Portal 列表合并为可规划的逻辑超级图。
 * 超级图节点 id 为 {@code networkRef::localNodeId}，见 {@link ShortestPathResult}。
 */
@Component
public class PortalGraphAssembler {

    public static final String PORTAL_EDGE_PREFIX = "portal:";

    @Resource
    private DoorConstraintFilter doorConstraintFilter;

    public AssembledGraph assemble(List<PathNetworkDTO> networks,
                                   List<PortalDTO> portals,
                                   Map<NetworkKind, String> profileIdByKind,
                                   String defaultProfileId,
                                   Map<String, MobilityProfileDTO> profilesById) {
        Map<String, PathNetworkDTO> networksByRef = new HashMap<>();
        Map<String, PathNodeDTO> nodes = new HashMap<>();
        Map<String, PathEdgeDTO> edgesById = new HashMap<>();
        Map<String, List<GraphView.AdjacencyEdge>> adjacency = new HashMap<>();
        Map<String, String> profileIdByNetworkRef = new HashMap<>();
        Set<String> portalEdgeIds = new HashSet<>();

        for (PathNetworkDTO network : networks) {
            if (network == null || !StringUtils.hasText(network.getNetworkRef())) {
                continue;
            }
            String networkRef = network.getNetworkRef();
            networksByRef.put(networkRef, network);

            String profileId = resolveProfileId(network.getNetworkKind(), profileIdByKind, defaultProfileId);
            profileIdByNetworkRef.put(networkRef, profileId);
            MobilityProfileDTO profile = profilesById.get(profileId);

            PathNetworkDTO routable = withFilteredEdges(network, doorConstraintFilter.filter(network, profile));
            indexPrefixedNodes(routable, networkRef, nodes);
            addNetworkEdges(routable, networkRef, profileId, nodes, edgesById, adjacency);
        }

        if (!CollectionUtils.isEmpty(portals)) {
            for (PortalDTO portal : portals) {
                if (portal == null || !StringUtils.hasText(portal.getFromNetworkRef())
                        || !StringUtils.hasText(portal.getToNetworkRef())
                        || !StringUtils.hasText(portal.getFromNodeId())
                        || !StringUtils.hasText(portal.getToNodeId())) {
                    continue;
                }
                PathNetworkDTO fromNetwork = networksByRef.get(portal.getFromNetworkRef());
                if (fromNetwork == null || !networksByRef.containsKey(portal.getToNetworkRef())) {
                    continue;
                }
                String sourceProfileId = profileIdByNetworkRef.getOrDefault(
                        portal.getFromNetworkRef(),
                        resolveProfileId(fromNetwork.getNetworkKind(), profileIdByKind, defaultProfileId));
                if (!isPortalAllowedForProfile(portal, sourceProfileId)) {
                    continue;
                }

                String fromPrefixed = ShortestPathResult.prefixNodeId(portal.getFromNetworkRef(), portal.getFromNodeId());
                String toPrefixed = ShortestPathResult.prefixNodeId(portal.getToNetworkRef(), portal.getToNodeId());
                if (!nodes.containsKey(fromPrefixed) || !nodes.containsKey(toPrefixed)) {
                    continue;
                }

                String portalEdgeId = portalEdgeId(portal);
                PathEdgeDTO portalEdge = PathEdgeDTO.builder()
                        .edgeId(portalEdgeId)
                        .fromNodeId(fromPrefixed)
                        .toNodeId(toPrefixed)
                        .build();
                edgesById.put(portalEdgeId, portalEdge);
                adjacency.computeIfAbsent(fromPrefixed, k -> new ArrayList<>())
                        .add(new GraphView.AdjacencyEdge(portalEdgeId, toPrefixed, 0D));
                portalEdgeIds.add(portalEdgeId);
            }
        }

        GraphView view = GraphView.assembled(nodes, edgesById, adjacency, defaultProfileId);
        return new AssembledGraph(view, networksByRef, profileIdByNetworkRef, portalEdgeIds);
    }

    public static String resolveStopNodeId(String stopId, Map<String, PathNetworkDTO> networksByRef) {
        if (!StringUtils.hasText(stopId)) {
            return stopId;
        }
        if (ShortestPathResult.isPrefixedNodeId(stopId)) {
            return stopId;
        }
        for (PathNetworkDTO network : networksByRef.values()) {
            if (network == null || !StringUtils.hasText(network.getNetworkRef())
                    || CollectionUtils.isEmpty(network.getNodes())) {
                continue;
            }
            for (PathNodeDTO node : network.getNodes()) {
                if (node != null && stopId.equals(node.getNodeId())) {
                    return ShortestPathResult.prefixNodeId(network.getNetworkRef(), stopId);
                }
            }
        }
        return stopId;
    }

    private static String resolveProfileId(NetworkKind networkKind,
                                           Map<NetworkKind, String> profileIdByKind,
                                           String defaultProfileId) {
        if (networkKind != null && profileIdByKind != null && profileIdByKind.containsKey(networkKind)) {
            return profileIdByKind.get(networkKind);
        }
        return defaultProfileId;
    }

    private static boolean isPortalAllowedForProfile(PortalDTO portal, String profileId) {
        List<String> allowed = portal.getAllowedProfileIds();
        if (CollectionUtils.isEmpty(allowed)) {
            return true;
        }
        return allowed.contains(profileId);
    }

    private static void indexPrefixedNodes(PathNetworkDTO network, String networkRef, Map<String, PathNodeDTO> nodes) {
        if (CollectionUtils.isEmpty(network.getNodes())) {
            return;
        }
        for (PathNodeDTO node : network.getNodes()) {
            if (node == null || !StringUtils.hasText(node.getNodeId())) {
                continue;
            }
            String prefixedId = ShortestPathResult.prefixNodeId(networkRef, node.getNodeId());
            nodes.put(prefixedId, node);
        }
    }

    private static void addNetworkEdges(PathNetworkDTO network,
                                        String networkRef,
                                        String profileId,
                                        Map<String, PathNodeDTO> nodes,
                                        Map<String, PathEdgeDTO> edgesById,
                                        Map<String, List<GraphView.AdjacencyEdge>> adjacency) {
        if (CollectionUtils.isEmpty(network.getEdges())) {
            return;
        }
        for (PathEdgeDTO edge : network.getEdges()) {
            if (edge == null || !StringUtils.hasText(edge.getEdgeId())
                    || !StringUtils.hasText(edge.getFromNodeId())
                    || !StringUtils.hasText(edge.getToNodeId())) {
                continue;
            }
            String fromPrefixed = ShortestPathResult.prefixNodeId(networkRef, edge.getFromNodeId());
            String toPrefixed = ShortestPathResult.prefixNodeId(networkRef, edge.getToNodeId());
            if (!nodes.containsKey(fromPrefixed) || !nodes.containsKey(toPrefixed)) {
                continue;
            }
            if (!isEdgeAllowed(edge, profileId)) {
                continue;
            }
            String edgeId = networkRef + "::" + edge.getEdgeId();
            PathEdgeDTO prefixedEdge = PathEdgeDTO.builder()
                    .edgeId(edgeId)
                    .fromNodeId(fromPrefixed)
                    .toNodeId(toPrefixed)
                    .layer(edge.getLayer())
                    .traversability(edge.getTraversability())
                    .impedanceByProfile(edge.getImpedanceByProfile())
                    .allowedProfileIds(edge.getAllowedProfileIds())
                    .waypoints(edge.getWaypoints())
                    .build();
            edgesById.put(edgeId, prefixedEdge);
            double weight = GraphView.resolveWeight(edge, profileId);
            adjacency.computeIfAbsent(fromPrefixed, k -> new ArrayList<>())
                    .add(new GraphView.AdjacencyEdge(edgeId, toPrefixed, weight));
        }
    }

    private static boolean isEdgeAllowed(PathEdgeDTO edge, String profileId) {
        List<String> allowed = edge.getAllowedProfileIds();
        if (CollectionUtils.isEmpty(allowed)) {
            return true;
        }
        return allowed.contains(profileId);
    }

    private static PathNetworkDTO withFilteredEdges(PathNetworkDTO network, List<PathEdgeDTO> edges) {
        return PathNetworkDTO.builder()
                .networkRef(network.getNetworkRef())
                .networkKind(network.getNetworkKind())
                .facilityId(network.getFacilityId())
                .scopeId(network.getScopeId())
                .status(network.getStatus())
                .version(network.getVersion())
                .nodes(network.getNodes())
                .edges(edges)
                .portals(network.getPortals())
                .build();
    }

    private static String portalEdgeId(PortalDTO portal) {
        if (StringUtils.hasText(portal.getPortalId())) {
            return PORTAL_EDGE_PREFIX + portal.getPortalId();
        }
        return PORTAL_EDGE_PREFIX + portal.getFromNetworkRef() + "::"
                + portal.getFromNodeId() + "->" + portal.getToNetworkRef() + "::" + portal.getToNodeId();
    }

    public record AssembledGraph(
            GraphView view,
            Map<String, PathNetworkDTO> networksByRef,
            Map<String, String> profileIdByNetworkRef,
            Set<String> portalEdgeIds) {
    }
}
