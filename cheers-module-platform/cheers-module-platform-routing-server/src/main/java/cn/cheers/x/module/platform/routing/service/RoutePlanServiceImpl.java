package cn.cheers.x.module.platform.routing.service;

import cn.cheers.x.module.platform.contract.ContractVersions;
import cn.cheers.x.module.platform.contract.dto.network.MobilityProfileDTO;
import cn.cheers.x.module.platform.contract.dto.network.PathEdgeDTO;
import cn.cheers.x.module.platform.contract.dto.network.PathNetworkDTO;
import cn.cheers.x.module.platform.contract.dto.network.PathNodeDTO;
import cn.cheers.x.module.platform.contract.dto.route.RoutePreviewDTO;
import cn.cheers.x.module.platform.contract.dto.route.RoutePreviewSegmentDTO;
import cn.cheers.x.module.platform.contract.dto.route.RouteRequestDTO;
import cn.cheers.x.module.platform.contract.dto.topology.TopologyPointDTO;
import cn.cheers.x.module.platform.routing.planner.DijkstraPlanner;
import cn.cheers.x.module.platform.routing.planner.GraphView;
import cn.cheers.x.module.platform.routing.planner.ProfileGate;
import cn.cheers.x.module.platform.routing.planner.ShortestPathResult;
import cn.cheers.x.module.platform.topology.api.MobilityProfileApi;
import cn.cheers.x.module.platform.topology.api.PathNetworkApi;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static cn.cheers.x.module.platform.routing.enums.ErrorCodeConstants.ROUTE_MOBILITY_PROFILE_NOT_FOUND;
import static cn.cheers.x.module.platform.routing.enums.ErrorCodeConstants.ROUTE_NETWORK_NOT_FOUND;
import static cn.cheers.x.module.platform.routing.enums.ErrorCodeConstants.ROUTE_STRATEGY_UNSUPPORTED;
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
    private DijkstraPlanner dijkstraPlanner;

    @Override
    public RoutePreviewDTO plan(RouteRequestDTO request) {
        if (request == null || !StringUtils.hasText(request.resolvedNetworkRef())
                || CollectionUtils.isEmpty(request.getStopIds())) {
            return emptyPreview(request);
        }

        String networkRef = request.resolvedNetworkRef();
        PathNetworkDTO network = pathNetworkApi.getNetwork(networkRef).getCheckedData();
        if (network == null) {
            throw exception(ROUTE_NETWORK_NOT_FOUND);
        }

        String profileId = StringUtils.hasText(request.getMobilityProfileId())
                ? request.getMobilityProfileId() : DEFAULT_PROFILE_ID;
        String strategy = StringUtils.hasText(request.getStrategy())
                ? request.getStrategy() : DEFAULT_STRATEGY;
        if (!DEFAULT_STRATEGY.equals(strategy)) {
            throw exception(ROUTE_STRATEGY_UNSUPPORTED);
        }

        MobilityProfileDTO profile = mobilityProfileApi.getProfile(profileId).getCheckedData();
        if (profile == null) {
            throw exception(ROUTE_MOBILITY_PROFILE_NOT_FOUND);
        }
        profileGate.assertAllowed(profile, network);

        GraphView view = GraphView.from(network, profileId);
        List<RoutePreviewSegmentDTO> segments = new ArrayList<>();
        double totalCost = 0D;
        List<String> stops = request.getStopIds();
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

    private static RoutePreviewDTO emptyPreview(RouteRequestDTO request) {
        String networkRef = request != null ? request.resolvedNetworkRef() : null;
        return RoutePreviewDTO.builder()
                .contractVersion(ContractVersions.MVP)
                .networkRef(networkRef)
                .topologyRef(networkRef)
                .segments(List.of())
                .totalDistanceMeters(0L)
                .build();
    }
}
