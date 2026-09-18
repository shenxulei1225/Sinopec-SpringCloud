package cn.cheers.x.inspection.inspection_content.service.orchestration.impl;

import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.inspection_content.service.orchestration.PatrolExpandMapService;
import cn.cheers.x.inspection.orchestration.dto.PatrolExpandReqDTO;
import cn.cheers.x.inspection.orchestration.dto.PatrolExpandRespDTO;
import cn.cheers.x.inspection.task.service.task.PatrolPlannedRouteSupport;
import cn.cheers.x.inspection.task.service.task.PatrolTaskDraft;
import cn.cheers.x.inspection.task.service.task.PatrolTaskEntityStore;
import cn.cheers.x.module.platform.contract.dto.work.WorkItemDTO;
import cn.cheers.x.module.platform.topology.api.PathNetworkApi;
import cn.cheers.x.module.platform.topology.api.dto.PathNetworkSummaryDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 选网取点展开服务实现（键名与 {@code RoutePayloadKeys} 一致）。
 */
@Service
@RequiredArgsConstructor
public class PatrolExpandMapServiceImpl implements PatrolExpandMapService {

    static final String NETWORK_REF = "networkRef";
    static final String STOP_IDS = "stopIds";
    static final String INSPECTION_TYPE = "inspectionType";
    static final String WORK_MINUTES = "workMinutes";
    static final String PLANNED_ROUTE = "plannedRoute";
    static final String START_STOP_ID = "startStopId";
    static final String END_STOP_ID = "endStopId";
    static final String RETURN_TO_START = "returnToStart";

    private final PatrolTaskEntityStore patrolTaskEntityStore;
    private final PathNetworkApi pathNetworkApi;

    @Override
    public PatrolExpandRespDTO expand(PatrolExpandReqDTO request) {
        if (request == null) {
            throw ServiceExceptionUtil.invalidParamException("expand 请求不能为空");
        }
        if (Boolean.TRUE.equals(request.getFromConfirmedSnapshot()) && request.getTaskId() != null) {
            return expandFromConfirmedSnapshot(request);
        }
        return expandFromInspectionStops(request);
    }

    /**
     * 从总任务 FLD-TSK-027 读已确认路线快照展开；不认旧固定表 networkRef / routePlanId。
     */
    private PatrolExpandRespDTO expandFromConfirmedSnapshot(PatrolExpandReqDTO request) {
        PatrolTaskDraft draft = patrolTaskEntityStore.require(request.getTaskId());
        Map<String, Object> planned = PatrolPlannedRouteSupport.asPlannedMap(draft.plannedRoute());
        if (planned == null || planned.isEmpty()) {
            throw ServiceExceptionUtil.invalidParamException("任务缺少已确认规划路线快照");
        }

        String networkRef = PatrolPlannedRouteSupport.networkRef(draft.plannedRoute());
        if (!StringUtils.hasText(networkRef)) {
            throw ServiceExceptionUtil.invalidParamException("任务缺少已确认路网快照 networkRef");
        }

        List<String> stopIds = PatrolPlannedRouteSupport.asStringList(planned.get(STOP_IDS));
        if (CollectionUtils.isEmpty(stopIds)) {
            throw ServiceExceptionUtil.invalidParamException("任务缺少停靠点快照 stopIds");
        }

        String inspectionType = normalizeInspectionType(draft.patrolExecutionMode());
        if (!StringUtils.hasText(inspectionType)) {
            throw ServiceExceptionUtil.invalidParamException("任务缺少已确认巡检类型快照");
        }

        Integer durationMinutes = PatrolPlannedRouteSupport.resolveDurationMinutes(
                draft.plannedRoute(), draft.patrolExecutionMode());
        if (durationMinutes == null) {
            throw ServiceExceptionUtil.invalidParamException("任务缺少已确认路线时长快照");
        }

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put(NETWORK_REF, networkRef);
        payload.put(STOP_IDS, stopIds);
        payload.put(INSPECTION_TYPE, inspectionType);
        payload.put(PLANNED_ROUTE, planned);
        Integer workMinutes = extractWorkMinutes(planned);
        if (workMinutes != null) {
            payload.put(WORK_MINUTES, workMinutes);
        }
        putRouteAnchors(payload, request, planned);

        WorkItemDTO workItem = WorkItemDTO.builder()
                .workId(resolveWorkId(request))
                .durationEstimateMinutes(durationMinutes)
                .payload(payload)
                .build();

        return PatrolExpandRespDTO.builder()
                .workItems(List.of(workItem))
                .networkRef(networkRef)
                .stopIds(stopIds)
                .inspectionType(inspectionType)
                .build();
    }

    /**
     * 按请求里的检查项停靠点展开。不读对象↔停靠点绑定，缺 stopIds 直接失败。
     */
    private PatrolExpandRespDTO expandFromInspectionStops(PatrolExpandReqDTO request) {
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
        String networkRef = selectNetworkRef(facilityId, inspectionType, request.getPreferredNetworkRef());

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put(NETWORK_REF, networkRef);
        payload.put(STOP_IDS, stopIds);
        payload.put(INSPECTION_TYPE, inspectionType);
        putRouteAnchors(payload, request, null);

        WorkItemDTO workItem = WorkItemDTO.builder()
                .workId(resolveWorkId(request))
                .payload(payload)
                .build();

        return PatrolExpandRespDTO.builder()
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
    private static void putRouteAnchors(Map<String, Object> payload, PatrolExpandReqDTO request,
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

    private String selectNetworkRef(Long facilityId, String inspectionType, String preferredNetworkRef) {
        CommonResult<List<PathNetworkSummaryDTO>> result = pathNetworkApi.listPublished(facilityId);
        List<PathNetworkSummaryDTO> published = result != null ? result.getCheckedData() : List.of();
        if (published == null) {
            published = List.of();
        }

        List<PathNetworkSummaryDTO> matched = published.stream()
                .filter(network -> applicableForInspectionType(network, inspectionType))
                .collect(Collectors.toList());

        if (matched.isEmpty()) {
            throw ServiceExceptionUtil.invalidParamException("无已发布匹配路网");
        }
        if (matched.size() == 1) {
            return matched.get(0).getNetworkRef();
        }
        if (!StringUtils.hasText(preferredNetworkRef)) {
            throw ServiceExceptionUtil.invalidParamException(
                    "本设施有多条已发布路网，请先在路网管理确认当前用哪一条");
        }
        return matched.stream()
                .map(PathNetworkSummaryDTO::getNetworkRef)
                .filter(ref -> preferredNetworkRef.equals(ref))
                .findFirst()
                .orElseThrow(() -> ServiceExceptionUtil.invalidParamException(
                        "指定的路网不在本设施已发布且匹配当前巡检方式的路网中"));
    }

    private static boolean applicableForInspectionType(PathNetworkSummaryDTO network, String inspectionType) {
        if (network == null || !StringUtils.hasText(inspectionType)) {
            return false;
        }
        List<String> types = network.getApplicableEquipmentTypes();
        return types != null && types.contains(inspectionType);
    }

    private static Integer extractWorkMinutes(Object plannedRoute) {
        if (!(plannedRoute instanceof Map<?, ?> map)) {
            return null;
        }
        Object raw = map.get(WORK_MINUTES);
        if (raw instanceof Number number) {
            return number.intValue();
        }
        return null;
    }

    private static String resolveWorkId(PatrolExpandReqDTO request) {
        if (StringUtils.hasText(request.getSeedWorkId())) {
            return request.getSeedWorkId();
        }
        if (request.getTaskId() != null) {
            return "patrol-task-" + request.getTaskId();
        }
        return "patrol-expand";
    }
}
