package cn.cheers.x.module.platform.routing.service;

import cn.cheers.x.module.platform.contract.ContractVersions;
import cn.cheers.x.module.platform.contract.dto.network.MobilityProfileDTO;
import cn.cheers.x.module.platform.contract.dto.network.PathEdgeDTO;
import cn.cheers.x.module.platform.contract.dto.network.PathNetworkDTO;
import cn.cheers.x.module.platform.contract.dto.network.PathNodeDTO;
import cn.cheers.x.module.platform.contract.dto.network.PortalDTO;
import cn.cheers.x.module.platform.contract.dto.route.RouteLegDTO;
import cn.cheers.x.module.platform.contract.dto.route.RoutePreviewDTO;
import cn.cheers.x.module.platform.contract.dto.route.RoutePreviewSegmentDTO;
import cn.cheers.x.module.platform.contract.dto.route.RouteRequestDTO;
import cn.cheers.x.module.platform.contract.dto.topology.TopologyPointDTO;
import cn.cheers.x.module.platform.contract.enums.NetworkKind;
import cn.cheers.x.module.platform.routing.planner.CostMatrix;
import cn.cheers.x.module.platform.routing.planner.DijkstraPlanner;
import cn.cheers.x.module.platform.routing.planner.DoorConstraintFilter;
import cn.cheers.x.module.platform.routing.planner.GraphView;
import cn.cheers.x.module.platform.routing.planner.PortalGraphAssembler;
import cn.cheers.x.module.platform.routing.planner.ProfileGate;
import cn.cheers.x.module.platform.routing.planner.ShortestPathResult;
import cn.cheers.x.module.platform.routing.planner.StopOrderStrategy;
import cn.cheers.x.module.platform.routing.planner.StopOrderStrategyRegistry;
import cn.cheers.x.module.platform.topology.api.MobilityProfileApi;
import cn.cheers.x.module.platform.topology.api.PathNetworkApi;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static cn.cheers.x.module.platform.routing.enums.ErrorCodeConstants.ROUTE_MOBILITY_PROFILE_NOT_FOUND;
import static cn.cheers.x.module.platform.routing.enums.ErrorCodeConstants.ROUTE_NETWORK_NOT_FOUND;
import static cn.cheers.x.module.platform.routing.enums.ErrorCodeConstants.ROUTE_REQUEST_INVALID;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;

@Service
public class RoutePlanServiceImpl implements RoutePlanService {

    private static final String DEFAULT_PROFILE_ID = "person_walk";
    private static final String DEFAULT_STRATEGY = "as_given";

    @Resource
    private PathNetworkApi pathNetworkApi;
    @Resource
    private MobilityProfileApi mobilityProfileApi;
    @Resource
    private ProfileGate profileGate;
    @Resource
    private DoorConstraintFilter doorConstraintFilter;
    @Resource
    private DijkstraPlanner dijkstraPlanner;
    @Resource
    private PortalGraphAssembler portalGraphAssembler;
    @Resource
    private StopOrderStrategyRegistry stopOrderStrategyRegistry;

    @Override
    public RoutePreviewDTO plan(RouteRequestDTO request) {
        if (request == null || CollectionUtils.isEmpty(request.getStopIds())) {
            throw exception(ROUTE_REQUEST_INVALID);
        }
        if (isMultimodal(request)) {
            return planMultimodal(request);
        }
        return planSingleNetwork(request);
    }

    private RoutePreviewDTO planSingleNetwork(RouteRequestDTO request) {
        if (!StringUtils.hasText(request.resolvedNetworkRef())) {
            throw exception(ROUTE_REQUEST_INVALID);
        }

        String networkRef = request.resolvedNetworkRef();
        PathNetworkDTO network = pathNetworkApi.getNetwork(networkRef).getCheckedData();
        if (network == null) {
            throw exception(ROUTE_NETWORK_NOT_FOUND);
        }

        String profileId = resolveDefaultProfileId(request);
        String strategy = resolveStrategy(request);

        MobilityProfileDTO profile = loadProfile(profileId);
        profileGate.assertAllowed(profile, network);

        PathNetworkDTO routableNetwork = withFilteredEdges(network, doorConstraintFilter.filter(network, profile));
        GraphView view = GraphView.from(routableNetwork, profileId);
        List<String> stops = orderStops(request.getStopIds(), strategy, view);
        List<RoutePreviewSegmentDTO> segments = new ArrayList<>();
        double totalCost = 0D;
        for (int i = 0; i < stops.size() - 1; i++) {
            String fromId = stops.get(i);
            String toId = stops.get(i + 1);
            ShortestPathResult path = dijkstraPlanner.shortestPath(fromId, toId, view);
            RoutePreviewSegmentDTO segment = buildSegment(i, path, view, network, profileId);
            segments.add(segment);
            totalCost += path.getTotalCost();
        }

        return RoutePreviewDTO.builder()
                .contractVersion(ContractVersions.MVP)
                .networkRef(networkRef)
                .topologyRef(networkRef)
                .segments(segments)
                .totalDistanceMeters(toDistanceMeters(totalCost))
                .decisionTraceId("trace_" + UUID.randomUUID())
                .build();
    }

    private RoutePreviewDTO planMultimodal(RouteRequestDTO request) {
        String strategy = resolveStrategy(request);

        List<String> networkRefs = resolveNetworkRefs(request);
        if (networkRefs.size() < 2) {
            throw exception(ROUTE_NETWORK_NOT_FOUND);
        }

        String defaultProfileId = resolveDefaultProfileId(request);
        Map<NetworkKind, String> profileIdByKind = buildProfileIdByKind(request, defaultProfileId);
        Map<String, MobilityProfileDTO> profilesById = loadProfiles(profileIdByKind, defaultProfileId);

        List<PathNetworkDTO> networks = new ArrayList<>();
        for (String networkRef : networkRefs) {
            PathNetworkDTO network = pathNetworkApi.getNetwork(networkRef).getCheckedData();
            if (network == null) {
                throw exception(ROUTE_NETWORK_NOT_FOUND);
            }
            String profileId = profileIdByKind.getOrDefault(network.getNetworkKind(), defaultProfileId);
            MobilityProfileDTO profile = profilesById.get(profileId);
            profileGate.assertAllowed(profile, network);
            networks.add(network);
        }

        Long facilityId = resolveFacilityId(request, networks);
        List<PortalDTO> portals = facilityId != null
                ? pathNetworkApi.listPortals(facilityId).getCheckedData()
                : List.of();
        if (portals == null) {
            portals = List.of();
        }

        PortalGraphAssembler.AssembledGraph assembled = portalGraphAssembler.assemble(
                networks, portals, profileIdByKind, defaultProfileId, profilesById);

        List<String> resolvedStops = request.getStopIds().stream()
                .map(stopId -> PortalGraphAssembler.resolveStopNodeId(stopId, assembled.networksByRef()))
                .collect(Collectors.toList());
        List<String> stops = orderStops(resolvedStops, strategy, assembled.view());
        List<RoutePreviewSegmentDTO> segments = new ArrayList<>();
        double totalCost = 0D;
        boolean usesPortal = false;
        for (int i = 0; i < stops.size() - 1; i++) {
            String fromId = stops.get(i);
            String toId = stops.get(i + 1);
            ShortestPathResult path = dijkstraPlanner.shortestPath(fromId, toId, assembled.view())
                    .withMultimodalNodeMapping();
            if (pathUsesPortal(path, assembled.portalEdgeIds())) {
                usesPortal = true;
            }
            segments.addAll(buildMultimodalSegments(i, path, assembled));
            totalCost += path.getTotalCost();
        }

        if (usesPortal) {
            for (MobilityProfileDTO profile : profilesById.values()) {
                profileGate.assertPortalAllowed(profile, true);
            }
        }

        String primaryNetworkRef = networkRefs.get(0);
        return RoutePreviewDTO.builder()
                .contractVersion(ContractVersions.MVP)
                .networkRef(primaryNetworkRef)
                .topologyRef(primaryNetworkRef)
                .segments(segments)
                .totalDistanceMeters(toDistanceMeters(totalCost))
                .decisionTraceId("trace_" + UUID.randomUUID())
                .build();
    }

    private static boolean isMultimodal(RouteRequestDTO request) {
        if (!CollectionUtils.isEmpty(request.getNetworkRefs())) {
            return request.getNetworkRefs().size() > 1 || hasMultipleLegKinds(request.getLegs());
        }
        return hasMultipleLegKinds(request.getLegs());
    }

    private static boolean hasMultipleLegKinds(List<RouteLegDTO> legs) {
        if (CollectionUtils.isEmpty(legs)) {
            return false;
        }
        return legs.stream()
                .map(RouteLegDTO::getNetworkKind)
                .filter(Objects::nonNull)
                .distinct()
                .count() > 1;
    }

    private static List<String> resolveNetworkRefs(RouteRequestDTO request) {
        if (!CollectionUtils.isEmpty(request.getNetworkRefs())) {
            return request.getNetworkRefs().stream()
                    .filter(StringUtils::hasText)
                    .distinct()
                    .collect(Collectors.toList());
        }
        String singleRef = request.resolvedNetworkRef();
        if (StringUtils.hasText(singleRef)) {
            return List.of(singleRef);
        }
        return List.of();
    }

    private static String resolveDefaultProfileId(RouteRequestDTO request) {
        if (StringUtils.hasText(request.getMobilityProfileId())) {
            return request.getMobilityProfileId();
        }
        if (!CollectionUtils.isEmpty(request.getLegs())) {
            for (RouteLegDTO leg : request.getLegs()) {
                if (leg != null && StringUtils.hasText(leg.getMobilityProfileId())) {
                    return leg.getMobilityProfileId();
                }
            }
        }
        return DEFAULT_PROFILE_ID;
    }

    private static String resolveStrategy(RouteRequestDTO request) {
        return StringUtils.hasText(request.getStrategy()) ? request.getStrategy() : DEFAULT_STRATEGY;
    }

    private List<String> orderStops(List<String> stopIds, String strategy, GraphView view) {
        if (CollectionUtils.isEmpty(stopIds)) {
            return List.of();
        }
        StopOrderStrategy stopOrderStrategy = stopOrderStrategyRegistry.resolve(strategy);
        CostMatrix matrix = CostMatrix.from(stopIds, view, dijkstraPlanner);
        return stopOrderStrategy.order(stopIds, matrix);
    }

    private static Map<NetworkKind, String> buildProfileIdByKind(RouteRequestDTO request, String defaultProfileId) {
        Map<NetworkKind, String> profileIdByKind = new LinkedHashMap<>();
        if (!CollectionUtils.isEmpty(request.getLegs())) {
            for (RouteLegDTO leg : request.getLegs()) {
                if (leg == null || leg.getNetworkKind() == null) {
                    continue;
                }
                String profileId = StringUtils.hasText(leg.getMobilityProfileId())
                        ? leg.getMobilityProfileId() : defaultProfileId;
                profileIdByKind.put(leg.getNetworkKind(), profileId);
            }
        }
        return profileIdByKind;
    }

    private Map<String, MobilityProfileDTO> loadProfiles(Map<NetworkKind, String> profileIdByKind,
                                                         String defaultProfileId) {
        Set<String> profileIds = new HashSet<>(profileIdByKind.values());
        profileIds.add(defaultProfileId);
        Map<String, MobilityProfileDTO> profilesById = new HashMap<>();
        for (String profileId : profileIds) {
            profilesById.put(profileId, loadProfile(profileId));
        }
        return profilesById;
    }

    private MobilityProfileDTO loadProfile(String profileId) {
        MobilityProfileDTO profile = mobilityProfileApi.getProfile(profileId).getCheckedData();
        if (profile == null) {
            throw exception(ROUTE_MOBILITY_PROFILE_NOT_FOUND);
        }
        return profile;
    }

    private static Long resolveFacilityId(RouteRequestDTO request, List<PathNetworkDTO> networks) {
        if (request.getFacilityId() != null) {
            return request.getFacilityId();
        }
        for (PathNetworkDTO network : networks) {
            if (network != null && NetworkKind.SITE == network.getNetworkKind() && network.getFacilityId() != null) {
                return network.getFacilityId();
            }
        }
        for (PathNetworkDTO network : networks) {
            if (network != null && network.getFacilityId() != null) {
                return network.getFacilityId();
            }
        }
        return null;
    }

    private static boolean pathUsesPortal(ShortestPathResult path, Set<String> portalEdgeIds) {
        if (CollectionUtils.isEmpty(path.getEdgeIds()) || CollectionUtils.isEmpty(portalEdgeIds)) {
            return false;
        }
        for (String edgeId : path.getEdgeIds()) {
            if (portalEdgeIds.contains(edgeId)) {
                return true;
            }
        }
        return false;
    }

    private List<RoutePreviewSegmentDTO> buildMultimodalSegments(
            int fromStopIndex,
            ShortestPathResult path,
            PortalGraphAssembler.AssembledGraph assembled) {
        List<String> nodeIds = path.getNodeIds();
        List<String> edgeIds = path.getEdgeIds();
        if (CollectionUtils.isEmpty(nodeIds)) {
            return List.of();
        }
        if (CollectionUtils.isEmpty(edgeIds)) {
            return List.of(buildMultimodalSubSegment(fromStopIndex, nodeIds, List.of(), path.getTotalCost(), assembled));
        }

        List<int[]> runs = splitNodeRuns(nodeIds);
        List<RoutePreviewSegmentDTO> segments = new ArrayList<>();
        for (int[] run : runs) {
            int start = run[0];
            int end = run[1];
            List<String> runNodes = nodeIds.subList(start, end);
            List<String> runEdges = new ArrayList<>();
            double runCost = 0D;
            String networkRef = ShortestPathResult.networkRefFromNodeId(nodeIds.get(start));
            String profileId = assembled.profileIdByNetworkRef().getOrDefault(networkRef, assembled.view().getMobilityProfileId());
            for (int edgeIndex = start; edgeIndex < end - 1; edgeIndex++) {
                String edgeId = edgeIds.get(edgeIndex);
                if (edgeId.startsWith(PortalGraphAssembler.PORTAL_EDGE_PREFIX)) {
                    continue;
                }
                PathEdgeDTO edge = assembled.view().getEdge(edgeId);
                runEdges.add(edgeId);
                if (edge != null) {
                    runCost += GraphView.resolveWeight(edge, profileId);
                }
            }
            segments.add(buildMultimodalSubSegment(fromStopIndex, runNodes, runEdges, runCost, assembled));
        }
        return segments;
    }

    private static List<int[]> splitNodeRuns(List<String> nodeIds) {
        List<int[]> runs = new ArrayList<>();
        int start = 0;
        String currentRef = ShortestPathResult.networkRefFromNodeId(nodeIds.get(0));
        for (int i = 1; i < nodeIds.size(); i++) {
            String ref = ShortestPathResult.networkRefFromNodeId(nodeIds.get(i));
            if (!Objects.equals(ref, currentRef)) {
                runs.add(new int[]{start, i});
                start = i;
                currentRef = ref;
            }
        }
        runs.add(new int[]{start, nodeIds.size()});
        return runs;
    }

    private RoutePreviewSegmentDTO buildMultimodalSubSegment(
            int fromStopIndex,
            List<String> prefixedNodeIds,
            List<String> edgeIds,
            double cost,
            PortalGraphAssembler.AssembledGraph assembled) {
        String networkRef = ShortestPathResult.networkRefFromNodeId(prefixedNodeIds.get(0));
        PathNetworkDTO network = assembled.networksByRef().get(networkRef);
        String profileId = assembled.profileIdByNetworkRef().getOrDefault(networkRef, assembled.view().getMobilityProfileId());
        List<String> localNodeIds = prefixedNodeIds.stream()
                .map(ShortestPathResult::localNodeId)
                .collect(Collectors.toList());
        List<String> localEdgeIds = edgeIds.stream()
                .map(edgeId -> edgeId.contains("::") ? edgeId.substring(edgeId.indexOf("::") + 2) : edgeId)
                .collect(Collectors.toList());

        ShortestPathResult subPath = ShortestPathResult.builder()
                .nodeIds(prefixedNodeIds)
                .localNodeIds(localNodeIds)
                .networkRefsByNode(prefixedNodeIds.stream()
                        .map(ShortestPathResult::networkRefFromNodeId)
                        .collect(Collectors.toList()))
                .edgeIds(edgeIds)
                .totalCost(cost)
                .build();

        return RoutePreviewSegmentDTO.builder()
                .fromStopIndex(fromStopIndex)
                .toStopIndex(fromStopIndex + 1)
                .networkKind(network != null ? network.getNetworkKind() : null)
                .mobilityProfileId(profileId)
                .nodeIds(localNodeIds)
                .edgeIds(localEdgeIds)
                .polyline(buildPolyline(subPath, assembled.view()))
                .distanceMeters(toDistanceMeters(cost))
                .build();
    }

    private RoutePreviewSegmentDTO buildSegment(int fromIndex, ShortestPathResult path, GraphView view,
                                                PathNetworkDTO network, String profileId) {
        return RoutePreviewSegmentDTO.builder()
                .fromStopIndex(fromIndex)
                .toStopIndex(fromIndex + 1)
                .networkKind(network.getNetworkKind())
                .mobilityProfileId(profileId)
                .nodeIds(path.getNodeIds())
                .edgeIds(path.getEdgeIds())
                .polyline(buildPolyline(path, view))
                .distanceMeters(toDistanceMeters(path.getTotalCost()))
                .build();
    }

    private static List<TopologyPointDTO> buildPolyline(ShortestPathResult path, GraphView view) {
        List<TopologyPointDTO> polyline = new ArrayList<>();
        List<String> nodeIds = path.getNodeIds();
        List<String> edgeIds = path.getEdgeIds();
        if (CollectionUtils.isEmpty(nodeIds)) {
            return polyline;
        }
        if (CollectionUtils.isEmpty(edgeIds)) {
            PathNodeDTO node = view.getNode(nodeIds.get(0));
            if (node != null && node.getPosition() != null) {
                polyline.add(node.getPosition());
            }
            return polyline;
        }
        for (int i = 0; i < edgeIds.size(); i++) {
            String fromNodeId = nodeIds.get(i);
            if (i == 0) {
                PathNodeDTO fromNode = view.getNode(fromNodeId);
                if (fromNode != null && fromNode.getPosition() != null) {
                    polyline.add(fromNode.getPosition());
                }
            }
            PathEdgeDTO edge = view.getEdge(edgeIds.get(i));
            if (edge != null && !CollectionUtils.isEmpty(edge.getWaypoints())) {
                polyline.addAll(edge.getWaypoints());
            }
            PathNodeDTO toNode = view.getNode(nodeIds.get(i + 1));
            if (toNode != null && toNode.getPosition() != null) {
                polyline.add(toNode.getPosition());
            }
        }
        return polyline;
    }

    private static long toDistanceMeters(double cost) {
        if (cost >= 0D && cost == Math.rint(cost) && cost <= Long.MAX_VALUE) {
            return (long) cost;
        }
        return Math.round(cost);
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

}
