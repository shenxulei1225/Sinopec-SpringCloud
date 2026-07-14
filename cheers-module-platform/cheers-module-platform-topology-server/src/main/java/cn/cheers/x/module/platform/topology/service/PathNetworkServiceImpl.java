package cn.cheers.x.module.platform.topology.service;

import cn.cheers.x.module.platform.contract.dto.network.PathEdgeDTO;
import cn.cheers.x.module.platform.contract.dto.network.PathNetworkDTO;
import cn.cheers.x.module.platform.contract.dto.network.PathNodeDTO;
import cn.cheers.x.module.platform.contract.dto.network.PortalDTO;
import cn.cheers.x.module.platform.contract.enums.NetworkKind;
import cn.cheers.x.module.platform.contract.enums.NetworkLayer;
import cn.cheers.x.module.platform.contract.enums.NodeType;
import cn.cheers.x.module.platform.topology.api.dto.TopologyValidateIssueDTO;
import cn.cheers.x.module.platform.topology.api.dto.TopologyValidateRespDTO;
import cn.cheers.x.module.platform.topology.dal.dataobject.PathNetworkDO;
import cn.cheers.x.module.platform.topology.dal.dataobject.PathPortalDO;
import cn.cheers.x.module.platform.topology.dal.mysql.PathNetworkMapper;
import cn.cheers.x.module.platform.topology.dal.mysql.PathPortalMapper;
import cn.cheers.x.module.platform.topology.enums.GraphStatus;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static cn.cheers.x.module.platform.topology.enums.ErrorCodeConstants.NETWORK_NOT_FOUND;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;

@Service
public class PathNetworkServiceImpl implements PathNetworkService {

    @Resource
    private PathNetworkMapper pathNetworkMapper;

    @Resource
    private PathPortalMapper pathPortalMapper;

    @Override
    public PathNetworkDTO getNetwork(String networkRef) {
        PathNetworkDO network = pathNetworkMapper.selectById(networkRef);
        if (network == null) {
            throw exception(NETWORK_NOT_FOUND);
        }
        return toDto(network);
    }

    @Override
    public PathNetworkDTO getDraft(Long facilityId, NetworkKind networkKind) {
        PathNetworkDO draft = pathNetworkMapper.selectDraftByFacilityIdAndKind(
                facilityId, networkKind.name());
        if (draft == null) {
            return emptyDraft(facilityId, networkKind);
        }
        return toDto(draft);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PathNetworkDTO saveDraft(PathNetworkDTO request) {
        Long facilityId = request.getFacilityId();
        NetworkKind networkKind = request.getNetworkKind();
        if (facilityId == null || networkKind == null) {
            throw exception(NETWORK_NOT_FOUND);
        }
        String draftId = draftId(facilityId, networkKind);
        PathNetworkDO existing = pathNetworkMapper.selectById(draftId);
        PathNetworkDO network = PathNetworkDO.builder()
                .id(draftId)
                .facilityId(facilityId)
                .networkKind(networkKind.name())
                .scopeId(request.getScopeId() != null ? String.valueOf(request.getScopeId()) : null)
                .status(GraphStatus.DRAFT)
                .version(0)
                .nodes(toJson(request.getNodes()))
                .edges(toJson(request.getEdges()))
                .build();
        if (existing == null) {
            pathNetworkMapper.insert(network);
        } else {
            pathNetworkMapper.updateById(network);
        }
        return toDto(network);
    }

    @Override
    public TopologyValidateRespDTO validate(PathNetworkDTO request) {
        return doValidate(request);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PathNetworkDTO publish(Long facilityId, NetworkKind networkKind) {
        PathNetworkDO draft = pathNetworkMapper.selectDraftByFacilityIdAndKind(
                facilityId, networkKind.name());
        if (draft == null) {
            throw exception(NETWORK_NOT_FOUND);
        }
        PathNetworkDO latest = pathNetworkMapper.selectLatestPublishedByFacilityIdAndKind(
                facilityId, networkKind.name());
        int nextVersion = latest != null ? latest.getVersion() + 1 : 1;
        String publishedId = publishedId(facilityId, networkKind, nextVersion);
        PathNetworkDO published = PathNetworkDO.builder()
                .id(publishedId)
                .facilityId(facilityId)
                .networkKind(networkKind.name())
                .scopeId(draft.getScopeId())
                .status(GraphStatus.PUBLISHED)
                .version(nextVersion)
                .nodes(draft.getNodes())
                .edges(draft.getEdges())
                .build();
        pathNetworkMapper.insert(published);
        return toDto(published);
    }

    @Override
    public List<PortalDTO> listPortals(Long facilityId) {
        return pathPortalMapper.selectListByFacilityId(facilityId).stream()
                .map(PathNetworkServiceImpl::toPortalDto)
                .toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<PortalDTO> savePortals(Long facilityId, List<PortalDTO> portals) {
        pathPortalMapper.deleteByFacilityId(facilityId);
        if (portals == null || portals.isEmpty()) {
            return List.of();
        }
        for (PortalDTO portal : portals) {
            PathPortalDO portalDO = PathPortalDO.builder()
                    .id(resolvePortalId(portal))
                    .fromNetworkId(portal.getFromNetworkRef())
                    .fromNodeId(portal.getFromNodeId())
                    .toNetworkId(portal.getToNetworkRef())
                    .toNodeId(portal.getToNodeId())
                    .allowedProfileIds(toJson(portal.getAllowedProfileIds()))
                    .build();
            pathPortalMapper.insert(portalDO);
        }
        return listPortals(facilityId);
    }

    static TopologyValidateRespDTO doValidate(PathNetworkDTO request) {
        List<TopologyValidateIssueDTO> issues = new ArrayList<>();
        List<PathNodeDTO> nodes = request.getNodes() != null ? request.getNodes() : List.of();
        List<PathEdgeDTO> edges = request.getEdges() != null ? request.getEdges() : List.of();
        Map<String, PathNodeDTO> nodeById = new HashMap<>();
        for (PathNodeDTO node : nodes) {
            if (node == null || !StringUtils.hasText(node.getNodeId())) {
                continue;
            }
            normalizeNode(node);
            nodeById.put(node.getNodeId(), node);
            if (node.getNodeType() == NodeType.DOOR && node.getLayer() != NetworkLayer.GROUND) {
                issues.add(issue("DOOR_REQUIRES_GROUND", "ERROR",
                        "门节点仅允许地面层", node.getNodeId()));
            }
            if (node.getNodeType() == NodeType.TRAVERSAL
                    && node.getZoneId() != null
                    && StringUtils.hasText(node.getDisplayName())) {
                issues.add(issue("NODE_NOT_STATION", "WARN",
                        "业务停靠点建议使用 STATION 节点类型", node.getNodeId()));
            }
        }
        for (PathEdgeDTO edge : edges) {
            if (edge == null) {
                continue;
            }
            normalizeEdge(edge);
            if (!nodeById.containsKey(edge.getFromNodeId())) {
                issues.add(issue("EDGE_ENDPOINT_MISSING", "ERROR",
                        "边起点不存在", edge.getEdgeId()));
            }
            if (!nodeById.containsKey(edge.getToNodeId())) {
                issues.add(issue("EDGE_ENDPOINT_MISSING", "ERROR",
                        "边终点不存在", edge.getEdgeId()));
            }
            if (edge.getLayer() != NetworkLayer.GROUND) {
                continue;
            }
            PathNodeDTO from = nodeById.get(edge.getFromNodeId());
            PathNodeDTO to = nodeById.get(edge.getToNodeId());
            if (from == null || to == null) {
                continue;
            }
            if (from.getZoneId() != null
                    && to.getZoneId() != null
                    && !from.getZoneId().equals(to.getZoneId())
                    && from.getNodeType() != NodeType.DOOR
                    && to.getNodeType() != NodeType.DOOR) {
                issues.add(issue("CROSS_ZONE_NO_DOOR", "ERROR",
                        "地面跨区边至少需要一端为门节点", edge.getEdgeId()));
            }
        }
        return TopologyValidateRespDTO.builder()
                .passed(issues.stream().noneMatch(issue -> "ERROR".equals(issue.getSeverity())))
                .issues(issues)
                .build();
    }

    private static TopologyValidateIssueDTO issue(String code, String severity, String message, String refId) {
        return TopologyValidateIssueDTO.builder()
                .code(code)
                .severity(severity)
                .message(message)
                .refId(refId)
                .build();
    }

    private static void normalizeNode(PathNodeDTO node) {
        if (node.getNodeType() == null && node.getPayload() != null) {
            Object raw = node.getPayload().get("nodeType");
            node.setNodeType(parseEnum(raw, NodeType.class));
        }
        if (node.getLayer() == null && node.getPayload() != null) {
            Object raw = node.getPayload().get("layer");
            node.setLayer(parseEnum(raw, NetworkLayer.class));
        }
    }

    private static void normalizeEdge(PathEdgeDTO edge) {
        if (edge.getLayer() == null && edge.getTraversability() != null) {
            edge.setLayer(parseEnum(edge.getTraversability(), NetworkLayer.class));
        }
    }

    private static <E extends Enum<E>> E parseEnum(Object raw, Class<E> enumType) {
        if (raw == null) {
            return null;
        }
        String value = String.valueOf(raw).trim();
        if (!StringUtils.hasText(value)) {
            return null;
        }
        try {
            return Enum.valueOf(enumType, value.toUpperCase());
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    private static PathNetworkDTO emptyDraft(Long facilityId, NetworkKind networkKind) {
        return PathNetworkDTO.builder()
                .networkRef(draftId(facilityId, networkKind))
                .networkKind(networkKind)
                .facilityId(facilityId)
                .status(GraphStatus.DRAFT)
                .version(0)
                .nodes(List.of())
                .edges(List.of())
                .build();
    }

    static String draftId(Long facilityId, NetworkKind networkKind) {
        return "net_" + facilityId + "_" + networkKind.name().toLowerCase() + "_draft";
    }

    static String publishedId(Long facilityId, NetworkKind networkKind, int version) {
        return "net_" + facilityId + "_" + networkKind.name().toLowerCase() + "_v" + version;
    }

    static PathNetworkDTO toDto(PathNetworkDO network) {
        return PathNetworkDTO.builder()
                .networkRef(network.getId())
                .networkKind(parseNetworkKind(network.getNetworkKind()))
                .facilityId(network.getFacilityId())
                .scopeId(parseScopeId(network.getScopeId()))
                .status(network.getStatus())
                .version(network.getVersion())
                .nodes(parseNodes(network.getNodes()))
                .edges(parseEdges(network.getEdges()))
                .build();
    }

    private static PortalDTO toPortalDto(PathPortalDO portal) {
        return PortalDTO.builder()
                .portalId(portal.getId())
                .fromNetworkRef(portal.getFromNetworkId())
                .fromNodeId(portal.getFromNodeId())
                .toNetworkRef(portal.getToNetworkId())
                .toNodeId(portal.getToNodeId())
                .allowedProfileIds(parseStringList(portal.getAllowedProfileIds()))
                .build();
    }

    private static String resolvePortalId(PortalDTO portal) {
        if (portal != null && StringUtils.hasText(portal.getPortalId())) {
            return portal.getPortalId();
        }
        return "portal_" + UUID.randomUUID().toString().replace("-", "");
    }

    private static NetworkKind parseNetworkKind(String networkKind) {
        if (!StringUtils.hasText(networkKind)) {
            return null;
        }
        return NetworkKind.valueOf(networkKind);
    }

    private static Long parseScopeId(String scopeId) {
        if (!StringUtils.hasText(scopeId)) {
            return null;
        }
        try {
            return Long.valueOf(scopeId);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    static List<PathNodeDTO> parseNodes(String json) {
        if (!StringUtils.hasText(json)) {
            return List.of();
        }
        JSONArray array = JSON.parseArray(json);
        List<PathNodeDTO> nodes = new ArrayList<>(array.size());
        for (int i = 0; i < array.size(); i++) {
            JSONObject obj = array.getJSONObject(i);
            PathNodeDTO node = obj.toJavaObject(PathNodeDTO.class);
            if (node.getNodeType() == null && obj.containsKey("nodeType")) {
                node.setNodeType(parseEnum(obj.get("nodeType"), NodeType.class));
            }
            if (node.getLayer() == null && obj.containsKey("layer")) {
                node.setLayer(parseEnum(obj.get("layer"), NetworkLayer.class));
            }
            nodes.add(node);
        }
        return nodes;
    }

    static List<PathEdgeDTO> parseEdges(String json) {
        if (!StringUtils.hasText(json)) {
            return List.of();
        }
        JSONArray array = JSON.parseArray(json);
        List<PathEdgeDTO> edges = new ArrayList<>(array.size());
        for (int i = 0; i < array.size(); i++) {
            JSONObject obj = array.getJSONObject(i);
            PathEdgeDTO edge = obj.toJavaObject(PathEdgeDTO.class);
            if (edge.getLayer() == null && obj.containsKey("layer")) {
                edge.setLayer(parseEnum(obj.get("layer"), NetworkLayer.class));
            }
            edges.add(edge);
        }
        return edges;
    }

    private static List<String> parseStringList(String json) {
        if (!StringUtils.hasText(json)) {
            return List.of();
        }
        return JSON.parseArray(json, String.class);
    }

    private static String toJson(List<?> values) {
        return JSON.toJSONString(values != null ? values : List.of());
    }
}
