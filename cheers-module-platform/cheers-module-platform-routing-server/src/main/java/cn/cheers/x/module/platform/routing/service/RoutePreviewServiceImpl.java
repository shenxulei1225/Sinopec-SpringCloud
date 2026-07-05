package cn.cheers.x.module.platform.routing.service;

import cn.cheers.x.module.platform.contract.ContractVersions;
import cn.cheers.x.module.platform.contract.dto.route.RoutePreviewDTO;
import cn.cheers.x.module.platform.contract.dto.route.RoutePreviewSegmentDTO;
import cn.cheers.x.module.platform.contract.dto.route.RouteRequestDTO;
import cn.cheers.x.module.platform.contract.dto.topology.TopologyEdgeDTO;
import cn.cheers.x.module.platform.contract.dto.topology.TopologyGraphDTO;
import cn.cheers.x.module.platform.contract.dto.topology.TopologyNodeDTO;
import cn.cheers.x.module.platform.contract.dto.topology.TopologyPointDTO;
import cn.cheers.x.module.platform.topology.api.TopologyGraphApi;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static cn.cheers.x.module.platform.topology.enums.ErrorCodeConstants.TOPOLOGY_GRAPH_NOT_FOUND;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;

@Service
public class RoutePreviewServiceImpl implements RoutePreviewService {

    @Resource
    private TopologyGraphApi topologyGraphApi;

    @Override
    public RoutePreviewDTO preview(RouteRequestDTO request) {
        if (request == null || !StringUtils.hasText(request.getTopologyRef())
                || CollectionUtils.isEmpty(request.getStopIds())) {
            return emptyPreview(request);
        }
        TopologyGraphDTO graph = topologyGraphApi.getGraph(request.getTopologyRef()).getCheckedData();
        if (graph == null) {
            throw exception(TOPOLOGY_GRAPH_NOT_FOUND);
        }
        Map<String, TopologyNodeDTO> nodeMap = indexNodes(graph.getNodes());
        Map<String, Map<String, TopologyEdgeDTO>> edgeMap = indexEdges(graph.getEdges());

        List<RoutePreviewSegmentDTO> segments = new ArrayList<>();
        long total = 0L;
        List<String> stops = request.getStopIds();
        for (int i = 0; i < stops.size() - 1; i++) {
            String fromId = stops.get(i);
            String toId = stops.get(i + 1);
            RoutePreviewSegmentDTO segment = buildSegment(i, fromId, toId, nodeMap, edgeMap);
            segments.add(segment);
            if (segment.getDistanceMeters() != null) {
                total += segment.getDistanceMeters();
            }
        }
        return RoutePreviewDTO.builder()
                .contractVersion(ContractVersions.MVP)
                .topologyRef(request.getTopologyRef())
                .segments(segments)
                .totalDistanceMeters(total)
                .decisionTraceId("trace_" + UUID.randomUUID())
                .build();
    }

    private RoutePreviewSegmentDTO buildSegment(int fromIndex, String fromId, String toId,
                                                Map<String, TopologyNodeDTO> nodeMap,
                                                Map<String, Map<String, TopologyEdgeDTO>> edgeMap) {
        TopologyEdgeDTO edge = findEdge(edgeMap, fromId, toId);
        if (edge != null) {
            List<TopologyPointDTO> polyline = new ArrayList<>();
            TopologyNodeDTO from = nodeMap.get(fromId);
            TopologyNodeDTO to = nodeMap.get(toId);
            if (from != null && from.getPosition() != null) {
                polyline.add(from.getPosition());
            }
            if (!CollectionUtils.isEmpty(edge.getWaypoints())) {
                polyline.addAll(edge.getWaypoints());
            }
            if (to != null && to.getPosition() != null) {
                polyline.add(to.getPosition());
            }
            return RoutePreviewSegmentDTO.builder()
                    .fromStopIndex(fromIndex)
                    .toStopIndex(fromIndex + 1)
                    .polyline(polyline)
                    .distanceMeters(edge.getDistanceMeters())
                    .build();
        }
        TopologyNodeDTO from = nodeMap.get(fromId);
        TopologyNodeDTO to = nodeMap.get(toId);
        List<TopologyPointDTO> polyline = new ArrayList<>();
        if (from != null && from.getPosition() != null) {
            polyline.add(from.getPosition());
        }
        if (to != null && to.getPosition() != null) {
            polyline.add(to.getPosition());
        }
        long distance = estimateDistance(polyline);
        return RoutePreviewSegmentDTO.builder()
                .fromStopIndex(fromIndex)
                .toStopIndex(fromIndex + 1)
                .polyline(polyline)
                .distanceMeters(distance)
                .build();
    }

    private static TopologyEdgeDTO findEdge(Map<String, Map<String, TopologyEdgeDTO>> edgeMap,
                                            String fromId, String toId) {
        Map<String, TopologyEdgeDTO> out = edgeMap.get(fromId);
        if (out == null) {
            return null;
        }
        TopologyEdgeDTO direct = out.get(toId);
        return direct;
    }

    private static Map<String, TopologyNodeDTO> indexNodes(List<TopologyNodeDTO> nodes) {
        Map<String, TopologyNodeDTO> map = new HashMap<>();
        if (nodes == null) {
            return map;
        }
        for (TopologyNodeDTO node : nodes) {
            if (node != null && StringUtils.hasText(node.getNodeId())) {
                map.put(node.getNodeId(), node);
            }
        }
        return map;
    }

    private static Map<String, Map<String, TopologyEdgeDTO>> indexEdges(List<TopologyEdgeDTO> edges) {
        Map<String, Map<String, TopologyEdgeDTO>> map = new HashMap<>();
        if (edges == null) {
            return map;
        }
        for (TopologyEdgeDTO edge : edges) {
            if (edge == null || !StringUtils.hasText(edge.getFromNodeId())
                    || !StringUtils.hasText(edge.getToNodeId())) {
                continue;
            }
            map.computeIfAbsent(edge.getFromNodeId(), k -> new HashMap<>())
                    .put(edge.getToNodeId(), edge);
        }
        return map;
    }

    private static long estimateDistance(List<TopologyPointDTO> polyline) {
        if (polyline == null || polyline.size() < 2) {
            return 0L;
        }
        double total = 0;
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
        return Math.round(total);
    }

    private static double safe(Double v) {
        return v != null ? v : 0D;
    }

    private static RoutePreviewDTO emptyPreview(RouteRequestDTO request) {
        return RoutePreviewDTO.builder()
                .contractVersion(ContractVersions.MVP)
                .topologyRef(request != null ? request.getTopologyRef() : null)
                .segments(List.of())
                .totalDistanceMeters(0L)
                .build();
    }
}
