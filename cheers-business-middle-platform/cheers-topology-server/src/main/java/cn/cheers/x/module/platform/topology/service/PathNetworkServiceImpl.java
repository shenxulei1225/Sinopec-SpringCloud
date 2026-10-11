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
import cn.cheers.x.module.dynamicbusiness.api.point.PathStationPointSyncApi;
import cn.cheers.x.module.dynamicbusiness.api.point.dto.PathStationPointSyncReqDTO;
import cn.cheers.x.framework.common.pojo.CommonResult;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import static cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil.exception;

@Service
public class PathNetworkServiceImpl implements PathNetworkService {

    private static final Logger log = LoggerFactory.getLogger(PathNetworkServiceImpl.class);

    // DEFERRED: /platform/topology/** delegation to PathNetworkService — TopologyGraphController unchanged.

    @Resource
    private PathNetworkMapper pathNetworkMapper;

    @Resource
    private PathPortalMapper pathPortalMapper;

    @Resource
    private PathStationPointSyncApi pathStationPointSyncApi;

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
        // 保存写回当前这一条路网，并把点写入点位数据目录。不另开一条。
        writeNetworkRow(request, networkId, pathNetworkMapper.selectById(networkId));
        PathNetworkDTO saved = toDto(pathNetworkMapper.selectById(networkId));
        syncStationPointsBestEffort(saved);
        return saved;
    }

    /**
     * 把当前几何写入指定路网编号。点位目录由保存之后的同步写入，这里不另开一条路网。
     */
    private void writeNetworkRow(
            PathNetworkDTO request, String networkId, PathNetworkDO existing) {
        Long facilityId = request.getFacilityId();
        NetworkKind networkKind = request.getNetworkKind() != null
                ? request.getNetworkKind() : NetworkKind.FACILITY;
        String status = GraphStatus.PUBLISHED;
        PathNetworkDO network = PathNetworkDO.builder()
                .id(networkId)
                .facilityId(facilityId)
                .networkKind(networkKind.name())
                .scopeId(request.getScopeId() != null ? String.valueOf(request.getScopeId()) : null)
                .status(status)
                .version(existing != null && existing.getVersion() != null ? existing.getVersion() : 0)
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
    }

    @Override
    public TopologyValidateRespDTO validate(PathNetworkDTO request) {
        return doValidate(request);
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
                .filter(row -> !isHistoricalPublishedCompanion(row.getId()))
                .map(PathNetworkServiceImpl::toSummary)
                .toList();
    }

    @Override
    public List<PathNetworkSummaryDTO> listPublished(Long facilityId) {
        // 排期读的就是路网管理这一份，不再按发布状态或 _draft 后缀另筛一份。
        return listDrafts(facilityId);
    }

    /**
     * 旧「发布另存」留下的伴侣行：id 以 _published 结尾，或 _v1 / _v2…
     * 不是用户当前在编的那条路网；选网时当成第二、第三条就会逼任务页填内部编号。
     */
    static boolean isHistoricalPublishedCompanion(String networkId) {
        if (!StringUtils.hasText(networkId)) {
            return false;
        }
        return networkId.endsWith("_published") || networkId.matches(".*_v\\d+$");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PathNetworkDTO createDraft(PathNetworkCreateReqVO request) {
        if (request == null || request.getFacilityId() == null
                || !StringUtils.hasText(request.getDisplayName())) {
            throw exception(NETWORK_DRAFT_INVALID);
        }
        // 新建就是这一条路网，编号不带草稿后缀。点在保存时写入点位数据目录。
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

    /**
     * 保存这一条路网之后：停靠站 / 途径点 / 门点写入点位数据目录，归属网写在点上。
     * 带上节点已有经度、纬度、椭球高。椭球高没填就不写，不用离地高度代替。
     * 写入失败则这次保存失败，不假装点位目录已经跟上。
     */
    private void syncStationPointsBestEffort(PathNetworkDTO network) {
        if (network == null || network.getFacilityId() == null) {
            return;
        }
        List<PathStationPointSyncReqDTO.StationNodeItem> stations = new ArrayList<>();
        List<PathNodeDTO> nodes = network.getNodes() != null ? network.getNodes() : List.of();
        for (PathNodeDTO node : nodes) {
            if (node == null || node.getNodeType() == null) {
                continue;
            }
            // 一期只落账算法认的三种节点；其它类型不进点位台账
            if (node.getNodeType() != NodeType.STATION
                    && node.getNodeType() != NodeType.TRAVERSAL
                    && node.getNodeType() != NodeType.DOOR) {
                continue;
            }
            if (!StringUtils.hasText(node.getNodeId())) {
                continue;
            }
            stations.add(PathStationPointSyncReqDTO.StationNodeItem.builder()
                    .nodeId(node.getNodeId().trim())
                    .displayName(node.getDisplayName())
                    .nodeType(node.getNodeType().name())
                    .memberships(node.getMemberships())
                    .longitude(readPayloadDouble(node.getPayload(), "longitude", "lng", "lon"))
                    .latitude(readPayloadDouble(node.getPayload(), "latitude", "lat"))
                    .height(readPayloadDouble(node.getPayload(), "ellipsoidHeight"))
                    .build());
        }
        PathStationPointSyncReqDTO req = PathStationPointSyncReqDTO.builder()
                .facilityId(network.getFacilityId())
                .stations(stations)
                .build();
        try {
            CommonResult<?> result = pathStationPointSyncApi.syncFromPathNetwork(req);
            if (result == null || result.isError()) {
                String msg = result != null ? result.getMsg() : "没有返回";
                log.error("[syncStationPoints] 点位同步失败 facilityId={} msg={}",
                        network.getFacilityId(), msg);
                throw new IllegalStateException("点位数据目录没有写上：" + msg);
            }
        } catch (RuntimeException ex) {
            log.error("[syncStationPoints] 点位同步异常 facilityId={}", network.getFacilityId(), ex);
            throw ex;
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
                .status(GraphStatus.PUBLISHED)
                .isDraft(false)
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

    /** 这次保存还没有路网编号时用。不另开带草稿后缀的编号。 */
    static String draftId(Long facilityId, NetworkKind networkKind) {
        return "net_" + facilityId + "_" + networkKind.name().toLowerCase();
    }

    static PathNetworkDTO toDto(PathNetworkDO network) {
        return PathNetworkDTO.builder()
                .networkRef(network.getId())
                .networkKind(parseNetworkKind(network.getNetworkKind()))
                .facilityId(network.getFacilityId())
                .scopeId(parseScopeId(network.getScopeId()))
                .status(network.getStatus())
                .isDraft(false)
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
                .isDraft(false)
                .version(network.getVersion())
                .applicableEquipmentTypes(parseStringList(network.getApplicableEquipmentTypes()))
                .nodeCount(nodes.size())
                .edgeCount(edges.size())
                .build();
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

    /**
     * 读路网节点 payload 上的数字。优先全称键，兼容旧缩写；读不到就空，不编 0。
     */
    static Double readPayloadDouble(Map<String, Object> payload, String... keys) {
        if (payload == null || keys == null) {
            return null;
        }
        for (String key : keys) {
            if (key == null || key.isBlank()) {
                continue;
            }
            Object raw = payload.get(key);
            if (raw instanceof Number number) {
                double value = number.doubleValue();
                return Double.isFinite(value) ? value : null;
            }
            if (raw instanceof String text && !text.isBlank()) {
                try {
                    double value = Double.parseDouble(text.trim());
                    return Double.isFinite(value) ? value : null;
                } catch (NumberFormatException ignored) {
                    return null;
                }
            }
        }
        return null;
    }
}
