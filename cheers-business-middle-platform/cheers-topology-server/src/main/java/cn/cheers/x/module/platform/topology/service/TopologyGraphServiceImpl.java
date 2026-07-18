package cn.cheers.x.module.platform.topology.service;

import cn.cheers.x.module.platform.contract.dto.topology.TopologyEdgeDTO;
import cn.cheers.x.module.platform.contract.dto.topology.TopologyGraphDTO;
import cn.cheers.x.module.platform.contract.dto.topology.TopologyNodeDTO;
import cn.cheers.x.module.platform.contract.dto.topology.ZoneBoundaryDTO;
import cn.cheers.x.module.platform.topology.api.dto.TopologyGraphSaveReqDTO;
import cn.cheers.x.module.platform.topology.api.dto.TopologyValidateIssueDTO;
import cn.cheers.x.module.platform.topology.api.dto.TopologyValidateRespDTO;
import cn.cheers.x.module.platform.topology.dal.dataobject.TopologyGraphDO;
import cn.cheers.x.module.platform.topology.dal.mysql.TopologyGraphMapper;
import cn.cheers.x.module.platform.topology.enums.GraphStatus;
import cn.cheers.x.module.platform.topology.legacy.LegacyTopologyImportAdapter;
import com.alibaba.fastjson2.JSON;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static cn.cheers.x.module.platform.topology.enums.ErrorCodeConstants.TOPOLOGY_GRAPH_NOT_FOUND;
import static cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil.exception;

@Service
public class TopologyGraphServiceImpl implements TopologyGraphService {

    @Resource
    private TopologyGraphMapper topologyGraphMapper;

    @Resource
    private LegacyTopologyImportAdapter legacyTopologyImportAdapter;

    @Override
    public TopologyGraphDTO getGraph(String topologyRef) {
        TopologyGraphDO graph = topologyGraphMapper.selectById(topologyRef);
        if (graph == null) {
            throw exception(TOPOLOGY_GRAPH_NOT_FOUND);
        }
        return toDto(graph);
    }

    @Override
    public TopologyGraphDTO getDraftBySiteId(Long siteId) {
        TopologyGraphDO draft = topologyGraphMapper.selectDraftBySiteId(siteId);
        if (draft == null) {
            return emptyDraft(siteId);
        }
        return toDto(draft);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TopologyGraphDTO saveDraft(Long siteId, TopologyGraphSaveReqDTO request) {
        String draftId = draftId(siteId);
        TopologyGraphDO existing = topologyGraphMapper.selectById(draftId);
        TopologyGraphDO graph = TopologyGraphDO.builder()
                .id(draftId)
                .siteId(siteId)
                .status(GraphStatus.DRAFT)
                .version(0)
                .nodes(toJson(request.getNodes()))
                .edges(toJson(request.getEdges()))
                .zoneBoundaries(toJson(request.getZoneBoundaries()))
                .build();
        if (existing == null) {
            topologyGraphMapper.insert(graph);
        } else {
            topologyGraphMapper.updateById(graph);
        }
        return toDto(graph);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TopologyGraphDTO publish(Long siteId) {
        TopologyGraphDO draft = topologyGraphMapper.selectDraftBySiteId(siteId);
        if (draft == null) {
            throw exception(TOPOLOGY_GRAPH_NOT_FOUND);
        }
        TopologyGraphDO latest = topologyGraphMapper.selectLatestPublishedBySiteId(siteId);
        int nextVersion = latest != null ? latest.getVersion() + 1 : 1;
        String publishedId = publishedId(siteId, nextVersion);
        TopologyGraphDO published = TopologyGraphDO.builder()
                .id(publishedId)
                .siteId(siteId)
                .status(GraphStatus.PUBLISHED)
                .version(nextVersion)
                .nodes(draft.getNodes())
                .edges(draft.getEdges())
                .zoneBoundaries(draft.getZoneBoundaries())
                .build();
        topologyGraphMapper.insert(published);
        return toDto(published);
    }

    @Override
    public TopologyValidateRespDTO validate(Long siteId, TopologyGraphSaveReqDTO request) {
        return doValidate(request);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TopologyGraphDTO importLegacy(Long siteId) {
        TopologyGraphSaveReqDTO imported = legacyTopologyImportAdapter.importGraph(siteId);
        return saveDraft(siteId, imported);
    }

    static TopologyValidateRespDTO doValidate(TopologyGraphSaveReqDTO request) {
        List<TopologyValidateIssueDTO> issues = new ArrayList<>();
        List<TopologyNodeDTO> nodes = request.getNodes() != null ? request.getNodes() : List.of();
        List<TopologyEdgeDTO> edges = request.getEdges() != null ? request.getEdges() : List.of();
        Set<String> nodeIds = new HashSet<>();
        for (TopologyNodeDTO node : nodes) {
            if (node == null || !StringUtils.hasText(node.getNodeId())) {
                continue;
            }
            nodeIds.add(node.getNodeId());
        }
        Set<String> connected = new HashSet<>();
        for (TopologyEdgeDTO edge : edges) {
            if (edge == null) {
                continue;
            }
            if (StringUtils.hasText(edge.getFromNodeId())) {
                connected.add(edge.getFromNodeId());
            }
            if (StringUtils.hasText(edge.getToNodeId())) {
                connected.add(edge.getToNodeId());
            }
            if (CollectionUtils.isEmpty(edge.getWaypoints()) && edge.getDistanceMeters() != null
                    && edge.getDistanceMeters() > 200) {
                issues.add(TopologyValidateIssueDTO.builder()
                        .code("EDGE_MISSING_WAYPOINTS")
                        .severity("WARN")
                        .message("长边缺少途径点")
                        .refId(edge.getEdgeId())
                        .build());
            }
        }
        for (String nodeId : nodeIds) {
            if (!connected.contains(nodeId)) {
                issues.add(TopologyValidateIssueDTO.builder()
                        .code("NODE_ISOLATED")
                        .severity("ERROR")
                        .message("孤立拓扑节点")
                        .refId(nodeId)
                        .build());
            }
        }
        return TopologyValidateRespDTO.builder()
                .passed(issues.stream().noneMatch(i -> "ERROR".equals(i.getSeverity())))
                .issues(issues)
                .build();
    }

    private static TopologyGraphDTO emptyDraft(Long siteId) {
        return TopologyGraphDTO.builder()
                .topologyRef(draftId(siteId))
                .siteId(siteId)
                .status(GraphStatus.DRAFT)
                .version(0)
                .nodes(List.of())
                .edges(List.of())
                .zoneBoundaries(List.of())
                .build();
    }

    static String draftId(Long siteId) {
        return "topo_" + siteId + "_draft";
    }

    static String publishedId(Long siteId, int version) {
        return "topo_" + siteId + "_v" + version;
    }

    static TopologyGraphDTO toDto(TopologyGraphDO graph) {
        return TopologyGraphDTO.builder()
                .topologyRef(graph.getId())
                .siteId(graph.getSiteId())
                .status(graph.getStatus())
                .version(graph.getVersion())
                .nodes(parseList(graph.getNodes(), TopologyNodeDTO.class))
                .edges(parseList(graph.getEdges(), TopologyEdgeDTO.class))
                .zoneBoundaries(parseList(graph.getZoneBoundaries(), ZoneBoundaryDTO.class))
                .build();
    }

    private static <T> List<T> parseList(String json, Class<T> clazz) {
        if (!StringUtils.hasText(json)) {
            return List.of();
        }
        return JSON.parseArray(json, clazz);
    }

    private static String toJson(List<?> values) {
        return JSON.toJSONString(values != null ? values : List.of());
    }
}
