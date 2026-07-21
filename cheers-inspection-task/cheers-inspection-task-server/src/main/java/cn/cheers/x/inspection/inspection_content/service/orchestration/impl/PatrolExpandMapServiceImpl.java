package cn.cheers.x.inspection.inspection_content.service.orchestration.impl;

import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.inspection_content.dal.dataobject.profile.InspectionObjectProfileDO;
import cn.cheers.x.inspection.inspection_content.dal.dataobject.route.InspectionRoutePlanDO;
import cn.cheers.x.inspection.inspection_content.dal.mysql.profile.InspectionObjectProfileMapper;
import cn.cheers.x.inspection.inspection_content.dal.mysql.route.InspectionRoutePlanMapper;
import cn.cheers.x.inspection.inspection_content.service.binding.ObjectStationBindingQueryService;
import cn.cheers.x.inspection.inspection_content.service.binding.model.BindingResolveResult;
import cn.cheers.x.inspection.inspection_content.service.binding.model.ObjectStationBindingView;
import cn.cheers.x.inspection.inspection_content.service.orchestration.PatrolExpandMapService;
import cn.cheers.x.inspection.inspection_content.service.profile.ObjectProfileQueryService;
import cn.cheers.x.inspection.orchestration.dto.PatrolExpandReqDTO;
import cn.cheers.x.inspection.orchestration.dto.PatrolExpandRespDTO;
import cn.cheers.x.inspection.task.dal.dataobject.task.InspectionTaskDO;
import cn.cheers.x.inspection.task.dal.mysql.task.InspectionTaskMapper;
import cn.cheers.x.module.platform.contract.dto.work.WorkItemDTO;
import cn.cheers.x.module.platform.topology.api.PathNetworkApi;
import cn.cheers.x.module.platform.topology.api.dto.PathNetworkSummaryDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

    private final ObjectProfileQueryService objectProfileQueryService;
    private final ObjectStationBindingQueryService bindingQueryService;
    private final InspectionObjectProfileMapper profileMapper;
    private final InspectionTaskMapper taskMapper;
    private final InspectionRoutePlanMapper routePlanMapper;
    private final PathNetworkApi pathNetworkApi;
    private final ObjectMapper objectMapper;

    @Override
    public PatrolExpandRespDTO expand(PatrolExpandReqDTO request) {
        if (request == null) {
            throw ServiceExceptionUtil.invalidParamException("expand 请求不能为空");
        }
        if (Boolean.TRUE.equals(request.getFromConfirmedSnapshot()) && request.getTaskId() != null) {
            return expandFromConfirmedSnapshot(request);
        }
        return expandFromBindings(request);
    }

    private PatrolExpandRespDTO expandFromConfirmedSnapshot(PatrolExpandReqDTO request) {
        InspectionTaskDO task = taskMapper.selectById(request.getTaskId());
        if (task == null) {
            throw ServiceExceptionUtil.invalidParamException("任务不存在，taskId={}", request.getTaskId());
        }
        if (!StringUtils.hasText(task.getNetworkRef())) {
            throw ServiceExceptionUtil.invalidParamException("任务缺少已确认路网快照 networkRef");
        }
        if (!StringUtils.hasText(task.getInspectionType())) {
            throw ServiceExceptionUtil.invalidParamException("任务缺少已确认巡检类型快照");
        }
        if (task.getDurationEstimateMinutes() == null) {
            throw ServiceExceptionUtil.invalidParamException("任务缺少已确认路线时长快照");
        }
        if (!StringUtils.hasText(task.getPlannedRoute())) {
            throw ServiceExceptionUtil.invalidParamException("任务缺少已确认规划路线快照");
        }

        List<String> stopIds = resolveStopIdsFromTask(task);
        if (CollectionUtils.isEmpty(stopIds)) {
            throw ServiceExceptionUtil.invalidParamException("任务缺少停靠点快照 stopIds");
        }

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put(NETWORK_REF, task.getNetworkRef());
        payload.put(STOP_IDS, stopIds);
        payload.put(INSPECTION_TYPE, task.getInspectionType());
        payload.put(PLANNED_ROUTE, parseJsonMap(task.getPlannedRoute()));
        Integer workMinutes = extractWorkMinutes(payload.get(PLANNED_ROUTE));
        if (workMinutes != null) {
            payload.put(WORK_MINUTES, workMinutes);
        }

        WorkItemDTO workItem = WorkItemDTO.builder()
                .workId(resolveWorkId(request))
                .durationEstimateMinutes(task.getDurationEstimateMinutes())
                .payload(payload)
                .build();

        return PatrolExpandRespDTO.builder()
                .workItems(List.of(workItem))
                .networkRef(task.getNetworkRef())
                .stopIds(stopIds)
                .inspectionType(task.getInspectionType())
                .build();
    }

    private PatrolExpandRespDTO expandFromBindings(PatrolExpandReqDTO request) {
        Long facilityId = request.getFacilityId();
        List<Long> objectIds = request.getObjectIds();
        if (facilityId == null) {
            throw ServiceExceptionUtil.invalidParamException("facilityId 不能为空");
        }
        if (CollectionUtils.isEmpty(objectIds)) {
            throw ServiceExceptionUtil.invalidParamException("objectIds 不能为空");
        }

        String inspectionType = objectProfileQueryService.requireConsistentInspectionType(facilityId, objectIds);

        BindingResolveResult bindingResult = bindingQueryService.listByObjectIds(facilityId, objectIds);
        if (!CollectionUtils.isEmpty(bindingResult.getMissingObjectIds())) {
            throw ServiceExceptionUtil.invalidParamException(
                    "缺少对象↔停靠点绑定，objectIds={}", bindingResult.getMissingObjectIds());
        }

        String networkRef = selectNetworkRef(facilityId, inspectionType, request.getPreferredNetworkRef());

        List<String> stopIds = new ArrayList<>();
        int totalWorkMinutes = 0;
        Map<Long, InspectionObjectProfileDO> profileByObjectId = loadProfiles(facilityId, objectIds);

        for (Long objectId : objectIds) {
            List<ObjectStationBindingView> bindings = new ArrayList<>(
                    bindingResult.getBindingsByObjectId().get(objectId));
            if (CollectionUtils.isEmpty(bindings)) {
                throw ServiceExceptionUtil.invalidParamException(
                        "缺少对象↔停靠点绑定，objectIds=[{}]", objectId);
            }
            bindings.sort(Comparator.comparingInt(b -> b.getSortNo() != null ? b.getSortNo() : 0));
            InspectionObjectProfileDO profile = profileByObjectId.get(objectId);
            for (ObjectStationBindingView binding : bindings) {
                stopIds.add(binding.getStationNodeId());
                Integer minutes = binding.getWorkMinutes();
                if (minutes == null && profile != null) {
                    minutes = profile.getDefaultWorkMinutes();
                }
                if (minutes == null) {
                    throw ServiceExceptionUtil.invalidParamException(
                            "缺少作业时长，objectId={} stationNodeId={}", objectId, binding.getStationNodeId());
                }
                totalWorkMinutes += minutes;
            }
        }

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put(NETWORK_REF, networkRef);
        payload.put(STOP_IDS, stopIds);
        payload.put(INSPECTION_TYPE, inspectionType);
        payload.put(WORK_MINUTES, totalWorkMinutes);

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
            throw ServiceExceptionUtil.invalidParamException("多条路网需指定 preferredNetworkRef");
        }
        return matched.stream()
                .map(PathNetworkSummaryDTO::getNetworkRef)
                .filter(ref -> preferredNetworkRef.equals(ref))
                .findFirst()
                .orElseThrow(() -> ServiceExceptionUtil.invalidParamException(
                        "preferredNetworkRef 未命中已发布匹配路网：{}", preferredNetworkRef));
    }

    private static boolean applicableForInspectionType(PathNetworkSummaryDTO network, String inspectionType) {
        if (network == null || !StringUtils.hasText(inspectionType)) {
            return false;
        }
        List<String> types = network.getApplicableEquipmentTypes();
        return types != null && types.contains(inspectionType);
    }

    private Map<Long, InspectionObjectProfileDO> loadProfiles(Long facilityId, List<Long> objectIds) {
        return profileMapper.selectByFacilityAndObjectIds(facilityId, objectIds).stream()
                .collect(Collectors.toMap(InspectionObjectProfileDO::getObjectId, p -> p, (a, b) -> a));
    }

    private List<String> resolveStopIdsFromTask(InspectionTaskDO task) {
        if (task.getRoutePlanId() != null) {
            InspectionRoutePlanDO plan = routePlanMapper.selectById(task.getRoutePlanId());
            if (plan != null && StringUtils.hasText(plan.getStopIds())) {
                List<String> fromPlan = parseStringList(plan.getStopIds());
                if (!CollectionUtils.isEmpty(fromPlan)) {
                    return fromPlan;
                }
            }
        }
        Map<String, Object> planned = parseJsonMap(task.getPlannedRoute());
        if (planned != null) {
            Object raw = planned.get(STOP_IDS);
            List<String> fromPlanned = asStringList(raw);
            if (!CollectionUtils.isEmpty(fromPlanned)) {
                return fromPlanned;
            }
        }
        return List.of();
    }

    private Map<String, Object> parseJsonMap(String json) {
        if (!StringUtils.hasText(json)) {
            return null;
        }
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception ex) {
            throw ServiceExceptionUtil.invalidParamException("规划路线快照 JSON 无效");
        }
    }

    private List<String> parseStringList(String json) {
        if (!StringUtils.hasText(json)) {
            return List.of();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {
            });
        } catch (Exception ex) {
            return List.of();
        }
    }

    @SuppressWarnings("unchecked")
    private static List<String> asStringList(Object raw) {
        if (raw == null) {
            return List.of();
        }
        if (raw instanceof List<?> list) {
            return list.stream().filter(Objects::nonNull).map(String::valueOf).collect(Collectors.toList());
        }
        return List.of();
    }

    @SuppressWarnings("unchecked")
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
