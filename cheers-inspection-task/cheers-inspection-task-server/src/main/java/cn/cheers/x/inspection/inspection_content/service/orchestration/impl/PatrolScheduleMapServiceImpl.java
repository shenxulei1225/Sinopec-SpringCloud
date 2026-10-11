package cn.cheers.x.inspection.inspection_content.service.orchestration.impl;

import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.inspection_content.service.orchestration.PatrolScheduleMapService;
import cn.cheers.x.inspection.orchestration.dto.PatrolScheduleMapReqDTO;
import cn.cheers.x.inspection.orchestration.dto.PatrolScheduleMapRespDTO;
import cn.cheers.x.inspection.task.model.task.ExecutionDeviceBinding;
import cn.cheers.x.inspection.task.service.task.PatrolItemActionDurationCalculator;
import cn.cheers.x.inspection.task.service.task.PatrolItemActionDurationSupport;
import cn.cheers.x.inspection.task.service.task.PatrolPlannedRouteSupport;
import cn.cheers.x.inspection.task.service.task.PatrolScheduleOccurrenceSupport;
import cn.cheers.x.inspection.task.service.task.PatrolTaskDraft;
import cn.cheers.x.inspection.task.service.task.PatrolTaskDurationSupport;
import cn.cheers.x.inspection.task.service.task.PatrolTaskEntityStore;
import cn.cheers.x.module.platform.contract.dto.work.ResourceRequirementDTO;
import cn.cheers.x.module.platform.contract.dto.work.TimePreferencesDTO;
import cn.cheers.x.module.platform.contract.dto.work.TimeWindowDTO;
import cn.cheers.x.module.platform.contract.dto.work.WorkItemDTO;
import cn.cheers.x.module.platform.orchestration.route.RoutePayloadKeys;
import cn.cheers.x.module.platform.topology.api.PathNetworkApi;
import cn.cheers.x.module.platform.topology.api.dto.PathNetworkSummaryDTO;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
/**
 * 选网取点展开服务实现（键名与 {@code RoutePayloadKeys} 一致）。
 * <p>路线预览路径（fromSavedRouteSnapshot=false）：须选网 + stopIds，供 ROUTE 阶段算路。
 * <p>智能编排路径（fromSavedRouteSnapshot=true）：只消费已保存 plannedRoute 的 stopIds 与时长；
 * networkRef 若页面路线里有则附带，不作独立硬门槛（路线已在路径规划阶段算好）。
 * <p>previewOrchestration（expandWorkItemsFromScheduleTemplate=true）再按已保存排期模板复制工作项，每个计划时刻一条。
 * 不负责冲突求解；缺模板或展不开计划时刻必须失败，禁止再按「一次」猜一条。
 */
@Service
@RequiredArgsConstructor
public class PatrolScheduleMapServiceImpl implements PatrolScheduleMapService {

    static final String NETWORK_REF = RoutePayloadKeys.NETWORK_REF;
    static final String STOP_IDS = RoutePayloadKeys.STOP_IDS;
    static final String INSPECTION_TYPE = RoutePayloadKeys.INSPECTION_TYPE;
    static final String ESTIMATED_ACTION_DURATION = RoutePayloadKeys.ESTIMATED_ACTION_DURATION;
    static final String PLANNED_ROUTE = RoutePayloadKeys.PLANNED_ROUTE;
    static final String START_STOP_ID = RoutePayloadKeys.START_STOP_ID;
    static final String END_STOP_ID = RoutePayloadKeys.END_STOP_ID;
    static final String RETURN_TO_START = RoutePayloadKeys.RETURN_TO_START;

    private final PatrolTaskEntityStore patrolTaskEntityStore;
    private final PathNetworkApi pathNetworkApi;

    /** 向导这一页没写检查项动作耗时时，按勾选现算分钟数。单元测试不注入时，缺耗时仍按原错误抛出。 */
    @Resource
    private PatrolItemActionDurationCalculator itemActionDurationCalculator;

    @Override
    public PatrolScheduleMapRespDTO expandPatrolWorkItems(PatrolScheduleMapReqDTO request) {
        if (request == null) {
            throw ServiceExceptionUtil.invalidParamException("expand 请求不能为空");
        }
        if (Boolean.TRUE.equals(request.getFromSavedRouteSnapshot()) && request.getTaskId() != null) {
            return expandFromSavedRouteSnapshot(request);
        }
        return expandFromInspectionStops(request);
    }

    @Override
    public PatrolScheduleMapRespDTO expandFromPageDraft(PatrolTaskDraft draft) {
        if (draft == null || draft.id() == null) {
            throw ServiceExceptionUtil.invalidParamException("任务 id 不能为空");
        }
        PatrolScheduleMapReqDTO request = PatrolScheduleMapReqDTO.builder()
                .taskId(draft.id())
                .fromSavedRouteSnapshot(true)
                .expandWorkItemsFromScheduleTemplate(true)
                .startStopId(draft.startStopId())
                .endStopId(draft.endStopId())
                .build();
        return expandFromDraft(request, draft);
    }

    /**
     * 智能编排专用：向导这一页带了刚重算的路线时用这一页；没带时读已保存路线。展开 stopIds + 时长供 SOLVE。
     * 固定摄像机跳过算路时无 stopIds，只认 estimatedActionDuration。
     */
    private PatrolScheduleMapRespDTO expandFromSavedRouteSnapshot(PatrolScheduleMapReqDTO request) {
        PatrolTaskDraft stored = patrolTaskEntityStore.require(request.getTaskId());
        PatrolTaskDraft draft = PatrolWizardPageDraft.overlay(stored, request, itemActionDurationCalculator);
        return expandFromDraft(request, draft);
    }

    /**
     * 用已经确定的那份任务内容展开。向导传入的是这一页，列表启用来的是已发布字段。
     */
    private PatrolScheduleMapRespDTO expandFromDraft(PatrolScheduleMapReqDTO request, PatrolTaskDraft draft) {
        if (skipsRoutePlanning(draft)) {
            return expandFromSkipRouteSchedule(request, draft);
        }

        Map<String, Object> planned = PatrolPlannedRouteSupport.asPlannedMap(draft.plannedRoute());
        if (planned == null || planned.isEmpty()) {
            throw ServiceExceptionUtil.invalidParamException("还没有路线结果，请先开始规划");
        }

        List<String> stopIds = PatrolPlannedRouteSupport.asStringList(planned.get(STOP_IDS));
        if (CollectionUtils.isEmpty(stopIds)) {
            throw ServiceExceptionUtil.invalidParamException("任务缺少停靠点快照 stopIds");
        }

        String inspectionType = normalizeInspectionType(draft.patrolExecutionMode());
        if (!StringUtils.hasText(inspectionType)) {
            throw ServiceExceptionUtil.invalidParamException("任务缺少巡检方式，无法排期");
        }

        Integer durationMinutes = PatrolTaskDurationSupport.totalMinutes(
                draft.inspectionContent(), draft.plannedRoute(), draft.patrolExecutionMode());
        if (durationMinutes == null) {
            throw ServiceExceptionUtil.invalidParamException("任务缺少检查项动作耗时或路径耗时");
        }

        String networkRef = PatrolPlannedRouteSupport.networkRef(draft.plannedRoute());

        Map<String, Object> payload = new LinkedHashMap<>();
        if (StringUtils.hasText(networkRef)) {
            payload.put(NETWORK_REF, networkRef);
        }
        payload.put(STOP_IDS, stopIds);
        payload.put(INSPECTION_TYPE, inspectionType);
        payload.put(PLANNED_ROUTE, planned);
        BigDecimal itemActionDuration = PatrolItemActionDurationSupport.minutesOf(draft.inspectionContent());
        if (itemActionDuration != null) {
            payload.put(ESTIMATED_ACTION_DURATION, itemActionDuration);
        }
        putRouteAnchors(payload, request, planned);

        WorkItemDTO template = WorkItemDTO.builder()
                .workId(resolveWorkId(request))
                .entityTypeCode(PatrolTaskEntityStore.TASK_TYPE)
                .sourceModelCode(PatrolTaskEntityStore.MODEL_PATROL)
                .sourceInstanceId(String.valueOf(draft.id()))
                .estimatedDuration(durationMinutes)
                .resourceRequirements(resolveResourceRequirements(draft))
                .payload(payload)
                .build();

        return PatrolScheduleMapRespDTO.builder()
                .workItems(cloneByScheduleOccurrences(request, draft, template))
                .networkRef(networkRef)
                .stopIds(stopIds)
                .inspectionType(inspectionType)
                .build();
    }

    /**
     * previewOrchestration：按已保存排期模板把路线模板复制成多条工作项，每条带一个计划开始时刻。
     * 重规划不复制，避免把整段日历重新占一遍。
     */
    private static List<WorkItemDTO> cloneByScheduleOccurrences(
            PatrolScheduleMapReqDTO request, PatrolTaskDraft draft, WorkItemDTO template) {
        if (!Boolean.TRUE.equals(request.getExpandWorkItemsFromScheduleTemplate())) {
            return List.of(template);
        }
        List<OffsetDateTime> starts = PatrolScheduleOccurrenceSupport.expandPlannedStarts(draft.scheduleConfig());
        if (starts.isEmpty()) {
            throw ServiceExceptionUtil.invalidParamException("任务缺少已保存的排期计划时刻，无法 previewOrchestration");
        }
        int duration = template.getEstimatedDuration() == null ? 0 : template.getEstimatedDuration();
        List<WorkItemDTO> workItems = new ArrayList<>(starts.size());
        for (int index = 0; index < starts.size(); index++) {
            OffsetDateTime start = starts.get(index);
            OffsetDateTime end = start.plusMinutes(Math.max(duration, 1));
            workItems.add(WorkItemDTO.builder()
                    .workId(template.getWorkId() + "-" + index)
                    .entityTypeCode(template.getEntityTypeCode())
                    .sourceModelCode(template.getSourceModelCode())
                    .sourceInstanceId(template.getSourceInstanceId())
                    .estimatedDuration(template.getEstimatedDuration())
                    .priority(template.getPriority())
                    .resourceRequirements(template.getResourceRequirements())
                    .timePreferences(TimePreferencesDTO.builder()
                            .allowedWindows(List.of(TimeWindowDTO.builder()
                                    .start(DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(start))
                                    .end(DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(end))
                                    .build()))
                            .build())
                    .payload(template.getPayload() == null ? null : new LinkedHashMap<>(template.getPayload()))
                    .build());
        }
        return workItems;
    }

    /**
     * 按请求里的检查项停靠点展开。不读对象↔停靠点绑定，缺 stopIds 直接失败。
     */
    private PatrolScheduleMapRespDTO expandFromInspectionStops(PatrolScheduleMapReqDTO request) {
        Long facilityId = request.getFacilityId();
        List<Long> objectIds = request.getObjectIds();
        if (facilityId == null) {
            throw ServiceExceptionUtil.invalidParamException("facilityId 不能为空");
        }
        if (CollectionUtils.isEmpty(objectIds)) {
            throw ServiceExceptionUtil.invalidParamException("objectIds 不能为空");
        }
        List<String> stopIds = normalizeStopIds(request.getStopIds());
        if (stopIds.isEmpty()) {
            throw ServiceExceptionUtil.invalidParamException("缺少检查项位置展开的停靠点");
        }

        String inspectionType = normalizeInspectionType(request.getInspectionType());
        if (!StringUtils.hasText(inspectionType)) {
            throw ServiceExceptionUtil.invalidParamException("请先选择巡检方式");
        }
        String networkRef = selectNetworkRef(facilityId, request.getPreferredNetworkRef());

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put(NETWORK_REF, networkRef);
        payload.put(STOP_IDS, stopIds);
        payload.put(INSPECTION_TYPE, inspectionType);
        if (StringUtils.hasText(request.getStrategy())) {
            payload.put(RoutePayloadKeys.STRATEGY, request.getStrategy().trim());
        }
        putRouteAnchors(payload, request, null);

        WorkItemDTO workItem = WorkItemDTO.builder()
                .workId(resolveWorkId(request))
                .payload(payload)
                .build();

        return PatrolScheduleMapRespDTO.builder()
                .workItems(List.of(workItem))
                .networkRef(networkRef)
                .stopIds(stopIds)
                .inspectionType(inspectionType)
                .build();
    }

    private static List<String> normalizeStopIds(List<String> raw) {
        if (CollectionUtils.isEmpty(raw)) {
            return List.of();
        }
        List<String> out = new ArrayList<>();
        for (String item : raw) {
            if (!StringUtils.hasText(item)) {
                continue;
            }
            String text = item.trim();
            if (!out.contains(text)) {
                out.add(text);
            }
        }
        return out;
    }

    /**
     * 智能编排用的设备占窗需求：只认执行设备绑定，resourceType 与巡检方式对齐。
     */
    private static List<ResourceRequirementDTO> resolveResourceRequirements(PatrolTaskDraft draft) {
        String resourceType = normalizeInspectionType(draft.patrolExecutionMode());
        Long fixedResourceId = resolveFixedResourceId(draft);
        if (!StringUtils.hasText(resourceType) || fixedResourceId == null) {
            return null;
        }
        return List.of(ResourceRequirementDTO.builder()
                .resourceType(resourceType)
                .fixedResourceId(String.valueOf(fixedResourceId))
                .quantity(1)
                .build());
    }

    private static Long resolveFixedResourceId(PatrolTaskDraft draft) {
        ExecutionDeviceBinding binding = draft.executionDeviceBinding();
        if (binding == null || binding.getEquipmentId() == null) {
            return null;
        }
        return binding.getEquipmentId();
    }

    /**
     * 任务页的巡检方式落到选网用的类型。人工=HUMAN，机器人=GROUND_ROBOT。
     */
    static String normalizeInspectionType(String raw) {
        if (!StringUtils.hasText(raw)) {
            return null;
        }
        String key = raw.trim().toUpperCase();
        if ("MANUAL".equals(key) || "HUMAN".equals(key)) {
            return "HUMAN";
        }
        if ("ROBOT".equals(key) || "GROUND_ROBOT".equals(key)) {
            return "GROUND_ROBOT";
        }
        if ("UAV".equals(key)) {
            return "UAV";
        }
        throw ServiceExceptionUtil.invalidParamException("不支持的巡检方式：{}", raw.trim());
    }

    /**
     * 把任务创建选定的起点、终点写入 expand 工作项，供算路阶段读取。
     * 请求显式值优先；确认快照可从已保存路线回填。
     */
    private static void putRouteAnchors(Map<String, Object> payload, PatrolScheduleMapReqDTO request,
                                        Map<String, Object> plannedRoute) {
        String startStopId = firstText(
                request != null ? request.getStartStopId() : null,
                plannedText(plannedRoute, START_STOP_ID));
        if (StringUtils.hasText(startStopId)) {
            payload.put(START_STOP_ID, startStopId.trim());
        }

        String endStopId = firstText(
                request != null ? request.getEndStopId() : null,
                plannedText(plannedRoute, END_STOP_ID));
        if (StringUtils.hasText(endStopId)) {
            payload.put(END_STOP_ID, endStopId.trim());
        }

        Boolean returnToStart = request != null ? request.getReturnToStart() : null;
        if (returnToStart == null && plannedRoute != null) {
            Object raw = plannedRoute.get(RETURN_TO_START);
            if (raw instanceof Boolean bool) {
                returnToStart = bool;
            } else if (raw != null) {
                returnToStart = Boolean.parseBoolean(String.valueOf(raw));
            }
        }
        if (returnToStart != null) {
            payload.put(RETURN_TO_START, returnToStart);
        }
    }

    private static String firstText(String preferred, String fallback) {
        if (StringUtils.hasText(preferred)) {
            return preferred;
        }
        return fallback;
    }

    private static String plannedText(Map<String, Object> plannedRoute, String key) {
        if (plannedRoute == null) {
            return null;
        }
        Object raw = plannedRoute.get(key);
        return raw == null ? null : String.valueOf(raw);
    }

    /**
     * 本厂只有一条路网时用它。巡检方式不拿来挑路网，人和机器人、无人机的差别在点的归属网上。
     */
    private String selectNetworkRef(Long facilityId, String preferredNetworkRef) {
        CommonResult<List<PathNetworkSummaryDTO>> result = pathNetworkApi.listPublished(facilityId);
        List<PathNetworkSummaryDTO> networks = result != null && result.getCheckedData() != null
                ? result.getCheckedData()
                : List.of();
        List<PathNetworkSummaryDTO> withRef = networks.stream()
                .filter(network -> network != null && StringUtils.hasText(network.getNetworkRef()))
                .toList();
        if (withRef.isEmpty()) {
            throw ServiceExceptionUtil.invalidParamException("本厂没有路网");
        }
        if (withRef.size() == 1) {
            return withRef.get(0).getNetworkRef();
        }
        if (!StringUtils.hasText(preferredNetworkRef)) {
            throw ServiceExceptionUtil.invalidParamException(
                    "本厂有多条路网，请先在路网管理确认用哪一条");
        }
        return withRef.stream()
                .map(PathNetworkSummaryDTO::getNetworkRef)
                .filter(preferredNetworkRef::equals)
                .findFirst()
                .orElseThrow(() -> ServiceExceptionUtil.invalidParamException(
                        "指定的路网不在本厂路网中"));
    }

    private static boolean skipsRoutePlanning(PatrolTaskDraft draft) {
        return draft != null && "FIXED_CAMERA".equals(draft.patrolExecutionMode());
    }

    /**
     * 固定摄像机跳过算路：无停靠点，时长只认巡检内容上的检查项动作耗时。
     */
    private PatrolScheduleMapRespDTO expandFromSkipRouteSchedule(PatrolScheduleMapReqDTO request, PatrolTaskDraft draft) {
        BigDecimal itemActionDuration = PatrolItemActionDurationSupport.minutesOf(draft.inspectionContent());
        if (itemActionDuration == null || itemActionDuration.signum() <= 0) {
            throw ServiceExceptionUtil.invalidParamException("任务缺少检查项动作耗时");
        }

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put(ESTIMATED_ACTION_DURATION, itemActionDuration);

        WorkItemDTO template = WorkItemDTO.builder()
                .workId(resolveWorkId(request))
                .entityTypeCode(PatrolTaskEntityStore.TASK_TYPE)
                .sourceModelCode(PatrolTaskEntityStore.MODEL_PATROL)
                .sourceInstanceId(String.valueOf(draft.id()))
                .estimatedDuration(PatrolTaskDurationSupport.occupancyMinutes(itemActionDuration))
                .resourceRequirements(resolveResourceRequirements(draft))
                .payload(payload)
                .build();

        return PatrolScheduleMapRespDTO.builder()
                .workItems(cloneByScheduleOccurrences(request, draft, template))
                .stopIds(List.of())
                .build();
    }

    private static String resolveWorkId(PatrolScheduleMapReqDTO request) {
        if (StringUtils.hasText(request.getTemplateWorkItemId())) {
            return request.getTemplateWorkItemId().trim();
        }
        if (request.getTaskId() != null) {
            return "patrol-task-" + request.getTaskId();
        }
        return "patrol-expand";
    }
}
