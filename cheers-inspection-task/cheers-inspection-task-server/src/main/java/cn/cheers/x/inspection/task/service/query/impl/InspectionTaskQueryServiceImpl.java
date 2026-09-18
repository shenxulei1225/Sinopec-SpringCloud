package cn.cheers.x.inspection.task.service.query.impl;

import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.framework.common.util.object.BeanUtils;
import cn.cheers.x.inspection.inspection_content.dal.dataobject.route.InspectionRoutePlanDO;
import cn.cheers.x.inspection.inspection_content.dal.mysql.route.InspectionRoutePlanMapper;
import cn.cheers.x.inspection.inspection_content.service.enhance.ContentEnhancementService;
import cn.cheers.x.inspection.task.controller.admin.vo.task.*;
import cn.cheers.x.inspection.task.dal.dataobject.schedule.InspectionTaskSchedulePlanDO;
import cn.cheers.x.inspection.task.dal.dataobject.task.InspectionTaskDO;
import cn.cheers.x.inspection.task.dal.mysql.schedule.InspectionTaskSchedulePlanMapper;
import cn.cheers.x.inspection.task.dal.mysql.task.InspectionTaskMapper;
import cn.cheers.x.inspection.task.model.task.InspectionContent;
import cn.cheers.x.inspection.task.service.query.InspectionTaskQueryService;
import cn.cheers.x.inspection.task.service.task.PatrolTaskDraft;
import cn.cheers.x.inspection.task.service.task.PatrolTaskEntityStore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.cheers.x.framework.common.exception.enums.GlobalErrorCodeConstants.NOT_FOUND;

/**
 * 巡检任务查询服务实现类。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InspectionTaskQueryServiceImpl implements InspectionTaskQueryService {

    private final InspectionTaskMapper inspectionTaskMapper;
    private final InspectionTaskSchedulePlanMapper schedulePlanMapper;
    private final ContentEnhancementService contentEnhancementService;
    private final InspectionRoutePlanMapper routePlanMapper;
    private final ObjectMapper objectMapper;
    private final PatrolTaskEntityStore patrolTaskEntityStore;

    // ==================== 分页查询 ====================

    @Override
    public PageResult<InspectionTaskSimpleRespVO> getTaskPage(InspectionTaskPageReqVO pageReqVO) {
        List<PatrolTaskDraft> all = patrolTaskEntityStore.listAll();
        String name = pageReqVO.getTaskName();
        List<PatrolTaskDraft> filtered = all.stream()
                .filter(draft -> !StringUtils.hasText(name)
                        || (draft.name() != null && draft.name().contains(name.trim())))
                .toList();
        int pageNo = pageReqVO.getPageNo() == null || pageReqVO.getPageNo() < 1 ? 1 : pageReqVO.getPageNo();
        int pageSize = pageReqVO.getPageSize() == null || pageReqVO.getPageSize() < 1 ? 10 : pageReqVO.getPageSize();
        int from = Math.min((pageNo - 1) * pageSize, filtered.size());
        int to = Math.min(from + pageSize, filtered.size());
        List<InspectionTaskSimpleRespVO> voList = filtered.subList(from, to).stream()
                .map(this::toSimple)
                .collect(Collectors.toList());
        return new PageResult<>(voList, (long) filtered.size());
    }

    private InspectionTaskSimpleRespVO toSimple(PatrolTaskDraft draft) {
        InspectionTaskSimpleRespVO vo = new InspectionTaskSimpleRespVO();
        vo.setId(draft.id());
        vo.setTaskName(draft.name());
        vo.setDomain(draft.domain());
        vo.setStatus("draft".equals(draft.manageStatus()) ? 0 : 0);
        vo.setEnabled(Boolean.FALSE);
        vo.setSubTaskCount(0);
        return vo;
    }

    // ==================== 详情查询 ====================

    @Override
    public InspectionTaskRespVO getTaskDetail(Long id) {
        PatrolTaskDraft draft = patrolTaskEntityStore.require(id);
        InspectionTaskRespVO respVO = new InspectionTaskRespVO();
        respVO.setId(draft.id());
        respVO.setTaskName(draft.name());
        respVO.setDomain(draft.domain());
        respVO.setStatus(0);
        respVO.setEnabled(Boolean.FALSE);
        respVO.setExecutionDeviceBinding(draft.executionDeviceBinding());
        respVO.setResourcePolicy(draft.resourcePolicy());
        respVO.setPatrolExecutionMode(draft.patrolExecutionMode());
        respVO.setCreateUnlockedStep(draft.createUnlockedStep());

        InspectionContent content = draft.inspectionContent();
        if (content != null) {
            content = contentEnhancementService.enhanceContent(content, true);
            respVO.setInspectionContent(content);
        }

        enrichSavedRouteFromDraft(respVO, draft);
        respVO.setSubTasks(Collections.emptyList());
        return respVO;
    }

    // ==================== 统计 ====================

    @Override
    public InspectionTaskStatisticsRespVO getTaskStatistics() {
        InspectionTaskStatisticsRespVO vo = new InspectionTaskStatisticsRespVO();
        long total = patrolTaskEntityStore.listAll().size();
        vo.setTaskTotal(total);
        vo.setEnabledCount(0L);
        vo.setDisabledCount(0L);
        vo.setDraftCount(total);
        return vo;
    }

    /**
     * 派生任务的展示状态（列表状态列 / 详情状态）。
     *
     * <p>启停唯一权威是 enabled 布尔（编排 reserve/enable/abort 写入）；库里 status 列只在创建时写 0，
     * 之后无人维护，属历史废弃列，读路径一律不信它。</p>
     *
     * <ul>
     *   <li>enabled = true → 1 已启用（占窗验窗通过，排程在跑）</li>
     *   <li>enabled ≠ true 且 runtimeJobId 有值 → 2 已停用（排过期后被停用/中止，或已预占未启用）</li>
     *   <li>其余 → 0 草稿（从未进入排程）</li>
     * </ul>
     *
     * <p>筛选侧同一口径见 {@code InspectionTaskMapper#selectPage} 的 status 翻译，两处必须一起改。</p>
     */
    public static Integer deriveDisplayStatus(InspectionTaskDO task) {
        if (Boolean.TRUE.equals(task.getEnabled())) {
            return 1;
        }
        if (StringUtils.hasText(task.getRuntimeJobId())) {
            return 2;
        }
        return 0;
    }

    /**
     * 已确认路线填停靠序和预览。起点终点优先已确认路线，没有再读草稿袋。
     * 还没确认过路线时，停靠序和预览保持空，起点终点仍从草稿回显。禁止编造路线。
     */
    private void enrichSavedRouteFromDraft(InspectionTaskRespVO respVO, PatrolTaskDraft draft) {
        Map<String, Object> planned = plannedMap(draft.plannedRoute());
        if (planned == null) {
            respVO.setStopSequence(Collections.emptyList());
            respVO.setStartStopId(draft.startStopId());
            respVO.setEndStopId(draft.endStopId());
            return;
        }
        List<String> stopSequence = asStringList(planned.get("stopIds"));
        respVO.setStopSequence(stopSequence);
        respVO.setStartStopId(firstNonBlank(asString(planned.get("startStopId")), draft.startStopId()));
        respVO.setEndStopId(firstNonBlank(asString(planned.get("endStopId")), draft.endStopId()));
        TaskSavedRoutePreviewVO preview = new TaskSavedRoutePreviewVO();
        preview.setNetworkRef(asString(planned.get("networkRef")));
        preview.setStopIds(new ArrayList<>(stopSequence));
        preview.setTotalDistanceMeters(asLong(planned.get("totalDistanceMeters")));
        preview.setMobilityProfileId(asString(planned.get("mobilityProfileId")));
        preview.setStartStopId(asString(planned.get("startStopId")));
        preview.setEndStopId(asString(planned.get("endStopId")));
        preview.setReturnToStart(asBoolean(planned.get("returnToStart")));
        preview.setDecisionTraceId(asString(planned.get("decisionTraceId")));
        respVO.setRoutePreview(preview);
    }

    private static String firstNonBlank(String preferred, String fallback) {
        return StringUtils.hasText(preferred) ? preferred : fallback;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> plannedMap(Object raw) {
        if (raw == null) {
            return null;
        }
        if (raw instanceof Map<?, ?> map) {
            return new java.util.LinkedHashMap<>((Map<String, Object>) map);
        }
        if (raw instanceof String json) {
            return parseJsonMap(json);
        }
        return null;
    }

    private void enrichSavedRoute(InspectionTaskRespVO respVO, InspectionTaskDO taskDO) {
        respVO.setRoutePlanId(taskDO.getRoutePlanId());
        respVO.setNetworkRef(taskDO.getNetworkRef());
        respVO.setInspectionType(taskDO.getInspectionType());
        respVO.setDurationEstimateMinutes(taskDO.getDurationEstimateMinutes());

        if (!StringUtils.hasText(taskDO.getPlannedRoute()) && taskDO.getRoutePlanId() == null) {
            respVO.setStopSequence(Collections.emptyList());
            return;
        }

        Map<String, Object> planned = parseJsonMap(taskDO.getPlannedRoute());
        List<String> stopSequence = resolveStopSequence(taskDO, planned);
        respVO.setStopSequence(stopSequence);

        String startStopId = asString(planned != null ? planned.get("startStopId") : null);
        String endStopId = asString(planned != null ? planned.get("endStopId") : null);
        respVO.setStartStopId(startStopId);
        respVO.setEndStopId(endStopId);

        if (!StringUtils.hasText(taskDO.getNetworkRef()) && CollectionUtils.isEmpty(stopSequence)
                && planned == null) {
            return;
        }

        TaskSavedRoutePreviewVO preview = new TaskSavedRoutePreviewVO();
        preview.setNetworkRef(taskDO.getNetworkRef());
        if (planned != null && StringUtils.hasText(asString(planned.get("networkRef")))) {
            preview.setNetworkRef(asString(planned.get("networkRef")));
        }
        preview.setStopIds(new ArrayList<>(stopSequence));
        preview.setTotalDistanceMeters(asLong(planned != null ? planned.get("totalDistanceMeters") : null));
        preview.setMobilityProfileId(asString(planned != null ? planned.get("mobilityProfileId") : null));
        preview.setStartStopId(startStopId);
        preview.setEndStopId(endStopId);
        preview.setReturnToStart(asBoolean(planned != null ? planned.get("returnToStart") : null));
        preview.setDecisionTraceId(asString(planned != null ? planned.get("decisionTraceId") : null));
        respVO.setRoutePreview(preview);
    }

    private List<String> resolveStopSequence(InspectionTaskDO taskDO, Map<String, Object> planned) {
        if (taskDO.getRoutePlanId() != null) {
            InspectionRoutePlanDO plan = routePlanMapper.selectById(taskDO.getRoutePlanId());
            if (plan != null && StringUtils.hasText(plan.getStopIds())) {
                List<String> fromPlan = parseStringList(plan.getStopIds());
                if (!CollectionUtils.isEmpty(fromPlan)) {
                    return fromPlan;
                }
            }
        }
        if (planned != null) {
            List<String> fromPlanned = asStringList(planned.get("stopIds"));
            if (!CollectionUtils.isEmpty(fromPlanned)) {
                return fromPlanned;
            }
        }
        return Collections.emptyList();
    }

    private Map<String, Object> parseJsonMap(String json) {
        if (!StringUtils.hasText(json)) {
            return null;
        }
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception ex) {
            log.warn("[getTaskDetail] plannedRoute JSON 无效, skip route enrich: {}", ex.getMessage());
            return null;
        }
    }

    private List<String> parseStringList(String json) {
        if (!StringUtils.hasText(json)) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {
            });
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    private static List<String> asStringList(Object raw) {
        if (!(raw instanceof List<?> list)) {
            return Collections.emptyList();
        }
        List<String> out = new ArrayList<>(list.size());
        for (Object item : list) {
            if (item != null) {
                out.add(String.valueOf(item));
            }
        }
        return out;
    }

    private static String asString(Object value) {
        if (value == null) {
            return null;
        }
        String text = String.valueOf(value);
        return StringUtils.hasText(text) ? text : null;
    }

    private static Long asLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        try {
            return Long.parseLong(String.valueOf(value));
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private static Boolean asBoolean(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Boolean bool) {
            return bool;
        }
        return Boolean.parseBoolean(String.valueOf(value));
    }
}
