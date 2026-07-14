package cn.cheers.x.module.platform.routing.planner;

import cn.cheers.x.module.platform.contract.dto.network.MobilityProfileDTO;
import cn.cheers.x.module.platform.contract.dto.network.PathEdgeDTO;
import cn.cheers.x.module.platform.contract.dto.network.PathNetworkDTO;
import cn.cheers.x.module.platform.contract.dto.network.PathNodeDTO;
import cn.cheers.x.module.platform.contract.enums.NetworkLayer;
import cn.cheers.x.module.platform.contract.enums.NodeType;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 按机动剖面过滤边：地面剖面遵守分区门约束；空中剖面仅保留 AIR 层边。
 */
@Component
public class DoorConstraintFilter {

    public List<PathEdgeDTO> filter(PathNetworkDTO network, MobilityProfileDTO profile) {
        if (network == null || CollectionUtils.isEmpty(network.getEdges())) {
            return List.of();
        }
        if (profile == null) {
            return List.copyOf(network.getEdges());
        }

        boolean respectDoors = profile.getRespectDoors() == null || profile.getRespectDoors();
        Map<String, PathNodeDTO> nodesById = indexNodes(network);

        List<PathEdgeDTO> kept = new ArrayList<>();
        for (PathEdgeDTO edge : network.getEdges()) {
            if (edge == null) {
                continue;
            }
            if (!matchesLayerPolicy(edge, respectDoors)) {
                continue;
            }
            if (respectDoors && crossesZoneWithoutDoor(edge, nodesById)) {
                continue;
            }
            kept.add(edge);
        }
        return kept;
    }

    private static Map<String, PathNodeDTO> indexNodes(PathNetworkDTO network) {
        Map<String, PathNodeDTO> nodesById = new HashMap<>();
        if (network.getNodes() == null) {
            return nodesById;
        }
        for (PathNodeDTO node : network.getNodes()) {
            if (node != null && StringUtils.hasText(node.getNodeId())) {
                nodesById.put(node.getNodeId(), node);
            }
        }
        return nodesById;
    }

    private static boolean matchesLayerPolicy(PathEdgeDTO edge, boolean respectDoors) {
        NetworkLayer layer = edge.getLayer();
        if (respectDoors) {
            return layer == null || layer == NetworkLayer.GROUND;
        }
        return layer == NetworkLayer.AIR;
    }

    private static boolean crossesZoneWithoutDoor(PathEdgeDTO edge, Map<String, PathNodeDTO> nodesById) {
        PathNodeDTO from = nodesById.get(edge.getFromNodeId());
        PathNodeDTO to = nodesById.get(edge.getToNodeId());
        if (from == null || to == null) {
            return false;
        }
        Long fromZone = from.getZoneId();
        Long toZone = to.getZoneId();
        if (fromZone == null || toZone == null || fromZone.equals(toZone)) {
            return false;
        }
        return from.getNodeType() != NodeType.DOOR && to.getNodeType() != NodeType.DOOR;
    }
}
