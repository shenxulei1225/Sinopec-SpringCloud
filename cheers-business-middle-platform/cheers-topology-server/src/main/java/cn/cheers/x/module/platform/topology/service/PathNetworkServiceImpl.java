package cn.cheers.x.module.platform.topology.service;

import cn.cheers.x.module.platform.contract.dto.network.PathEdgeDTO;
import cn.cheers.x.module.platform.contract.dto.network.PathNetworkDTO;
import cn.cheers.x.module.platform.contract.dto.network.PathNodeDTO;
import cn.cheers.x.module.platform.contract.dto.network.PortalDTO;
import cn.cheers.x.module.platform.contract.enums.NetworkKind;
import cn.cheers.x.module.platform.contract.enums.NetworkLayer;
import cn.cheers.x.module.platform.contract.enums.NodeType;
import cn.cheers.x.module.platform.topology.api.dto.PathNetworkSummaryDTO;
import cn.cheers.x.module.platform.topology.api.dto.TopologyValidateIssueDTO;
import cn.cheers.x.module.platform.topology.api.dto.TopologyValidateRespDTO;
import cn.cheers.x.module.platform.topology.controller.admin.vo.PathNetworkCreateReqVO;
import cn.cheers.x.module.platform.topology.controller.admin.vo.PathNetworkMetaUpdateReqVO;
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

import static cn.cheers.x.module.platform.topology.enums.ErrorCodeConstants.NETWORK_DRAFT_INVALID;
import static cn.cheers.x.module.platform.topology.enums.ErrorCodeConstants.NETWORK_NOT_FOUND;
import static cn.cheers.x.module.platform.topology.enums.ErrorCodeConstants.NETWORK_VALIDATE_FAILED;
import static cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil.exception;

@Service
public class PathNetworkServiceImpl implements PathNetworkService {

    // DEFERRED: /platform/topology/** delegation to PathNetworkService — TopologyGraphController unchanged.

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
        NetworkKind networkKind = request.getNetworkKind() != null
                ? request.getNetworkKind() : NetworkKind.FACILITY;
        if (facilityId == null) {
            throw exception(NETWORK_DRAFT_INVALID);
        }
        String networkId = StringUtils.hasText(request.getNetworkRef())
                ? request.getNetworkRef()
                : draftId(facilityId, networkKind);
        PathNetworkDO existing = pathNetworkMapper.selectById(networkId);
        // isDraft=true → 草稿；false/空 → 正式保存（名称无草稿后缀）
        boolean asDraft = Boolean.TRUE.equals(request.getIsDraft());
        String status = asDraft ? GraphStatus.DRAFT : GraphStatus.PUBLISHED;
        PathNetworkDO network = PathNetworkDO.builder()
                .id(networkId)
                .facilityId(facilityId)
                .networkKind(networkKind.name())
                .scopeId(request.getScopeId() != null ? String.valueOf(request.getScopeId()) : null)
                .status(status)
                .version(existing != null ? existing.getVersion() : 0)
                .displayName(resolveDisplayName(request.getDisplayName(), existing))
                .description(request.getDescription() != null
                        ? request.getDescription()
                        : (existing != null ? existing.getDescription() : null))
                .applicableEquipmentTypes(toJson(request.getApplicableEquipmentTypes() != null
                        ? request.getApplicableEquipmentTypes()
                        : parseStringList(existing != null ? existing.getApplicableEquipmentTypes() : null)))
                .nodes(toJson(request.getNodes()))
                .edges(toJson(request.getEdges()))
                .build();
        if (existing == null) {
            if (!StringUtils.hasText(network.getDisplayName())) {
                network.setDisplayName("默认路网");
            }
            pathNetworkMapper.insert(network);
        } else {
            pathNetworkMapper.updateById(network);
        }
        return toDto(pathNetworkMapper.selectById(networkId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PathNetworkDTO saveAsDraft(PathNetworkDTO request) {
        if (request == null || request.getFacilityId() == null) {
            throw exception(NETWORK_DRAFT_INVALID);
        }
        if (!StringUtils.hasText(request.getDisplayName())) {
            throw exception(NETWORK_DRAFT_INVALID);
        }
        NetworkKind networkKind = request.getNetworkKind() != null
                ? request.getNetworkKind() : NetworkKind.FACILITY;
        String id = "net_" + request.getFacilityId() + "_"
                + UUID.randomUUID().toString().replace("-", "").substring(0, 12) + "_draft";
        PathNetworkDO network = PathNetworkDO.builder()
                .id(id)
                .facilityId(request.getFacilityId())
                .networkKind(networkKind.name())
                .scopeId(request.getScopeId() != null ? String.valueOf(request.getScopeId()) : null)
                .status(GraphStatus.DRAFT)
                .version(0)
                .displayName(request.getDisplayName().trim())
                .description(request.getDescription())
                .applicableEquipmentTypes(toJson(
                        request.getApplicableEquipmentTypes() != null
                                ? request.getApplicableEquipmentTypes()
                                : List.of()))
                .nodes(toJson(request.getNodes() != null ? request.getNodes() : List.of()))
                .edges(toJson(request.getEdges() != null ? request.getEdges() : List.of()))
                .build();
        pathNetworkMapper.insert(network);
        return toDto(pathNetworkMapper.selectById(id));
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
        return publishDraftRow(draft);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PathNetworkDTO publishByRef(String networkRef) {
        PathNetworkDO draft = pathNetworkMapper.selectById(networkRef);
        if (draft == null || !GraphStatus.DRAFT.equals(draft.getStatus())) {
            throw exception(NETWORK_NOT_FOUND);
        }
        return publishDraftRow(draft);
    }

    @Override
    public List<PathNetworkSummaryDTO> listDrafts(Long facilityId) {
        if (facilityId == null) {
            return List.of();
        }
        List<PathNetworkDO> rows = pathNetworkMapper.selectAllByFacilityId(facilityId);
        if (rows.isEmpty()) {
            List<PathNetworkDO> legacy = new ArrayList<>();
            for (NetworkKind kind : NetworkKind.values()) {
                PathNetworkDO row = pathNetworkMapper.selectDraftByFacilityIdAndKind(
                        facilityId, kind.name());
                if (row != null) {
                    legacy.add(row);
                }
            }
            rows = legacy;
        }
        // 排除历史「发布伴侣」行（*_published / *_vN），列表只展示用户可编辑的路网记录
        return rows.stream()
                .filter(row -> row.getId() == null
                        || (!row.getId().endsWith("_published") && !row.getId().matches(".*_v\\d+$")))
                .map(PathNetworkServiceImpl::toSummary)
                .toList();
    }

    @Override
    public List<PathNetworkSummaryDTO> listPublished(Long facilityId) {
        if (facilityId == null) {
            return List.of();
        }
        return pathNetworkMapper.selectAllByFacilityId(facilityId).stream()
                .filter(row -> GraphStatus.PUBLISHED.equals(row.getStatus()))
                .map(PathNetworkServiceImpl::toSummary)
                .toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PathNetworkDTO createDraft(PathNetworkCreateReqVO request) {
        if (request == null || request.getFacilityId() == null
                || !StringUtils.hasText(request.getDisplayName())) {
            throw exception(NETWORK_DRAFT_INVALID);
        }
        // 新建即为正式记录（无草稿后缀）；另存为草稿才产生 DRAFT
        String id = "net_" + request.getFacilityId() + "_"
                + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        PathNetworkDO network = PathNetworkDO.builder()
                .id(id)
                .facilityId(request.getFacilityId())
                .networkKind(NetworkKind.FACILITY.name())
                .status(GraphStatus.PUBLISHED)
                .version(0)
                .displayName(request.getDisplayName().trim())
                .description(request.getDescription())
                .applicableEquipmentTypes(toJson(
                        request.getApplicableEquipmentTypes() != null
                                ? request.getApplicableEquipmentTypes()
                                : List.of()))
                .nodes("[]")
                .edges("[]")
                .build();
        pathNetworkMapper.insert(network);
        return toDto(network);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PathNetworkDTO updateMeta(String networkRef, PathNetworkMetaUpdateReqVO request) {
        PathNetworkDO existing = pathNetworkMapper.selectById(networkRef);
        if (existing == null) {
            throw exception(NETWORK_NOT_FOUND);
        }
        if (request == null || !StringUtils.hasText(request.getDisplayName())) {
            throw exception(NETWORK_DRAFT_INVALID);
        }
        existing.setDisplayName(request.getDisplayName().trim());
        existing.setDescription(request.getDescription());
        existing.setApplicableEquipmentTypes(toJson(
                request.getApplicableEquipmentTypes() != null
                        ? request.getApplicableEquipmentTypes()
                        : List.of()));
        pathNetworkMapper.updateById(existing);
        return toDto(pathNetworkMapper.selectById(networkRef));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDraft(String networkRef) {
        PathNetworkDO existing = pathNetworkMapper.selectById(networkRef);
        if (existing == null) {
            throw exception(NETWORK_NOT_FOUND);
        }
        pathNetworkMapper.deleteById(networkRef);
    }

    private PathNetworkDTO publishDraftRow(PathNetworkDO draft) {
        TopologyValidateRespDTO validation = doValidate(toDto(draft));
        if (!Boolean.TRUE.equals(validation.getPassed())) {
            throw exception(NETWORK_VALIDATE_FAILED);
        }
        NetworkKind networkKind = parseNetworkKind(draft.getNetworkKind());
        if (networkKind == null) {
            networkKind = NetworkKind.FACILITY;
        }
        // 一条草稿对应唯一一条已发布：反复发布只覆盖更新，不新增版本行
        String publishedId = publishedCompanionId(draft.getId());
        removeObsoleteVersionedPublished(draft.getFacilityId(), networkKind.name(), publishedId);

        PathNetworkDO existing = pathNetworkMapper.selectById(publishedId);
        int nextVersion = existing != null && existing.getVersion() != null
                ? existing.getVersion() + 1
                : 1;
        PathNetworkDO published = PathNetworkDO.builder()
                .id(publishedId)
                .facilityId(draft.getFacilityId())
                .networkKind(networkKind.name())
                .scopeId(draft.getScopeId())
                .status(GraphStatus.PUBLISHED)
                .version(nextVersion)
                .displayName(draft.getDisplayName())
                .description(draft.getDescription())
                .applicableEquipmentTypes(draft.getApplicableEquipmentTypes())
                .nodes(draft.getNodes())
                .edges(draft.getEdges())
                .build();
        if (existing == null) {
            pathNetworkMapper.insert(published);
        } else {
            pathNetworkMapper.updateById(published);
        }
        return toDto(pathNetworkMapper.selectById(publishedId));
    }

    /** 草稿 net_xxx_draft → 已发布 net_xxx_published（固定一对一） */
    static String publishedCompanionId(String draftId) {
        if (!StringUtils.hasText(draftId)) {
            throw exception(NETWORK_DRAFT_INVALID);
        }
        if (draftId.endsWith("_draft")) {
            return draftId.substring(0, draftId.length() - "_draft".length()) + "_published";
        }
        return draftId + "_published";
    }

    /** 清掉历史错误产生的 net_*_v1 / v2… 版本行，只保留当前这条已发布 */
    private void removeObsoleteVersionedPublished(
            Long facilityId, String networkKind, String keepPublishedId) {
        List<PathNetworkDO> published = pathNetworkMapper.selectPublishedByFacilityIdAndKind(
                facilityId, networkKind);
        if (published == null || published.isEmpty()) {
            return;
        }
        for (PathNetworkDO row : published) {
            if (row.getId() == null || row.getId().equals(keepPublishedId)) {
                continue;
            }
            // 仅删除旧版号命名（…_v数字），避免误删其它草稿配套的 _published
            if (row.getId().matches(".*_v\\d+$")) {
                pathNetworkMapper.deleteById(row.getId());
            }
        }
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
            if (node.getNodeType() != NodeType.STATION
                    && StringUtils.hasText(node.getDisplayName())) {
                issues.add(issue("NODE_NOT_STATION", "WARN",
                        "业务停靠点建议使用 STATION 节点类型", node.getNodeId()));
            }
        }
        for (PathEdgeDTO edge : edges) {
            if (edge == null) {
                continue;
            }
            if (!nodeById.containsKey(edge.getFromNodeId())) {
                issues.add(issue("EDGE_ENDPOINT_MISSING", "ERROR",
                        "边起点不存在", edge.getEdgeId()));
            }
            if (!nodeById.containsKey(edge.getToNodeId())) {
                issues.add(issue("EDGE_ENDPOINT_MISSING", "ERROR",
                        "边终点不存在", edge.getEdgeId()));
            }
            if (edge.getLayer() == NetworkLayer.AIR) {
                continue;
            }
            PathNodeDTO from = nodeById.get(edge.getFromNodeId());
            PathNodeDTO to = nodeById.get(edge.getToNodeId());
            if (from == null || to == null) {
                continue;
            }
            // 分区仅辅助展示/组织；跨区直连不再作为发布阻断（WARN 供人工核对）
            if (from.getZoneId() != null
                    && to.getZoneId() != null
                    && !from.getZoneId().equals(to.getZoneId())
                    && from.getNodeType() != NodeType.DOOR
                    && to.getNodeType() != NodeType.DOOR) {
                issues.add(issue("CROSS_ZONE_NO_DOOR", "WARN",
                        "地面跨区边两端均非门（分区仅供参考）", edge.getEdgeId()));
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
                .displayName("默认路网")
                .applicableEquipmentTypes(List.of())
                .nodes(List.of())
                .edges(List.of())
                .build();
    }

    private static String resolveDisplayName(String requested, PathNetworkDO existing) {
        if (StringUtils.hasText(requested)) {
            return requested.trim();
        }
        if (existing != null && StringUtils.hasText(existing.getDisplayName())) {
            return existing.getDisplayName();
        }
        return "默认路网";
    }

    static String draftId(Long facilityId, NetworkKind networkKind) {
        return "net_" + facilityId + "_" + networkKind.name().toLowerCase() + "_draft";
    }

    /** @deprecated 旧版按 vN 递增插行；已改为 publishedCompanionId 覆盖更新 */
    static String publishedId(Long facilityId, NetworkKind networkKind, int version) {
        return "net_" + facilityId + "_" + networkKind.name().toLowerCase() + "_v" + version;
    }

    static PathNetworkDTO toDto(PathNetworkDO network) {
        boolean draft = isDraftStatus(network.getStatus());
        return PathNetworkDTO.builder()
                .networkRef(network.getId())
                .networkKind(parseNetworkKind(network.getNetworkKind()))
                .facilityId(network.getFacilityId())
                .scopeId(parseScopeId(network.getScopeId()))
                .status(network.getStatus())
                .isDraft(draft)
                .version(network.getVersion())
                .displayName(StringUtils.hasText(network.getDisplayName())
                        ? network.getDisplayName() : "默认路网")
                .description(network.getDescription())
                .applicableEquipmentTypes(parseStringList(network.getApplicableEquipmentTypes()))
                .nodes(parseNodes(network.getNodes()))
                .edges(parseEdges(network.getEdges()))
                .build();
    }

    static PathNetworkSummaryDTO toSummary(PathNetworkDO network) {
        List<PathNodeDTO> nodes = parseNodes(network.getNodes());
        List<PathEdgeDTO> edges = parseEdges(network.getEdges());
        return PathNetworkSummaryDTO.builder()
                .networkRef(network.getId())
                .facilityId(network.getFacilityId())
                .displayName(resolveListDisplayName(network))
                .description(network.getDescription())
                .status(network.getStatus())
                .isDraft(isDraftStatus(network.getStatus()))
                .version(network.getVersion())
                .applicableEquipmentTypes(parseStringList(network.getApplicableEquipmentTypes()))
                .nodeCount(nodes.size())
                .edgeCount(edges.size())
                .build();
    }

    /** status=DRAFT（或空）视为草稿 */
    private static boolean isDraftStatus(String status) {
        if (!StringUtils.hasText(status)) {
            return true;
        }
        return GraphStatus.DRAFT.equalsIgnoreCase(status.trim());
    }

    /** 列表展示名：有名称用名称；旧槽位无名称时按种类给可读默认名 */
    private static String resolveListDisplayName(PathNetworkDO network) {
        if (StringUtils.hasText(network.getDisplayName())) {
            return network.getDisplayName();
        }
        String kind = network.getNetworkKind() != null ? network.getNetworkKind().toUpperCase() : "";
        String base = switch (kind) {
            case "FACILITY" -> "设施路网";
            case "PERIMETER" -> "站界路网";
            case "PIPELINE" -> "管线路网";
            case "ROAD" -> "道路路网";
            case "UTILITY_TUNNEL" -> "管廊路网";
            default -> "默认路网";
        };
        // 已发布无名称时带上版本，避免多条正式版同名
        if (GraphStatus.PUBLISHED.equals(network.getStatus()) && network.getVersion() != null) {
            return base + " v" + network.getVersion();
        }
        return base;
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
        // 历史库可能仍存 SITE；与枚举 FACILITY 对齐
        String normalized = "SITE".equalsIgnoreCase(networkKind.trim()) ? "FACILITY" : networkKind.trim();
        return NetworkKind.valueOf(normalized);
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
