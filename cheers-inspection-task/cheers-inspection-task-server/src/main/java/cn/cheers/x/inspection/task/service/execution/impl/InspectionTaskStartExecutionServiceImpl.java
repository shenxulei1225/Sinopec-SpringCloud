package cn.cheers.x.inspection.task.service.execution.impl;

import cn.cheers.x.device.protocolgateway.api.DeviceProtocolMissionApi;
import cn.cheers.x.device.protocolgateway.api.dto.MissionStartRespDTO;
import cn.cheers.x.device.protocolgateway.api.mission.DeviceMissionPlan;
import cn.cheers.x.device.protocolgateway.api.mission.MissionPointActionType;
import cn.cheers.x.device.protocolgateway.api.mission.MissionWaypoint;
import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.inspection_content.dal.dataobject.binding.ObjectStationBindingDO;
import cn.cheers.x.inspection.inspection_content.dal.dataobject.route.InspectionRoutePlanDO;
import cn.cheers.x.inspection.inspection_content.dal.mysql.binding.ObjectStationBindingMapper;
import cn.cheers.x.inspection.inspection_content.dal.mysql.route.InspectionRoutePlanMapper;
import cn.cheers.x.inspection.task.dal.dataobject.task.InspectionTaskDO;
import cn.cheers.x.inspection.task.dal.mysql.task.InspectionTaskMapper;
import cn.cheers.x.inspection.task.model.task.ExecutionDeviceBinding;
import cn.cheers.x.inspection.task.model.task.InspectionContent;
import cn.cheers.x.inspection.task.service.execution.InspectionTaskStartExecutionService;
import cn.cheers.x.module.dynamicbusiness.api.execution.TaskExecutionSessionApi;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionStartReqDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionStartRespDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionWritebackReqDTO;
import cn.cheers.x.module.platform.contract.dto.network.PathNetworkDTO;
import cn.cheers.x.module.platform.contract.dto.network.PathNodeDTO;
import cn.cheers.x.module.platform.contract.dto.topology.TopologyPointDTO;
import cn.cheers.x.module.platform.topology.api.PathNetworkApi;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 巡检域「开始执行」接线。
 * <p>标准路径：创建执行记录 + 步骤（任务模块）→（可选）外部通道下发。
 * <p>权威会话 = 执行记录 id；禁止再写任务定义表 deviceDispatch*。
 * <p>过渡：步骤草稿暂由 stopIds 生成（stepCode=pointId）；收敛到 SOP 动作树后删除此分支。
 */
@Service
@RequiredArgsConstructor
public class InspectionTaskStartExecutionServiceImpl implements InspectionTaskStartExecutionService {

    /** 巡检域执行记录型号（seed：exec_patrol_round） */
    public static final String PATROL_EXEC_MODEL_CODE = "exec_patrol_round";
    public static final String PATROL_RECORD_TYPE = "task_record_patrol";

    private final InspectionTaskMapper inspectionTaskMapper;
    private final InspectionRoutePlanMapper routePlanMapper;
    private final ObjectStationBindingMapper objectStationBindingMapper;
    private final PathNetworkApi pathNetworkApi;
    private final TaskExecutionSessionApi taskExecutionSessionApi;
    private final DeviceProtocolMissionApi deviceProtocolMissionApi;
    private final ObjectMapper objectMapper;

    @Override
    public MissionStartRespDTO startExecution(Long taskId) {
        InspectionTaskDO task = requireTask(taskId);
        ExecutionDeviceBinding binding = requireCompleteBinding(task);
        String networkRef = requireNetworkRef(task);
        List<String> stopIds = requireStopIds(task);
        Set<String> photoStopIds = resolvePhotoStopIds(task);
        List<MissionWaypoint> waypoints = buildWaypoints(networkRef, stopIds, photoStopIds);

        TaskExecutionStartRespDTO session = startExecutionSession(task, stopIds);
        Long executionRecordId = session.getExecutionRecordId();

        String templateId = StringUtils.hasText(task.getTaskCode())
                ? task.getTaskCode().trim()
                : "task-" + task.getId();
        // 外部通道会话 id = 执行记录 id（不是任务定义 id）
        DeviceMissionPlan plan = new DeviceMissionPlan(
                binding.getProtocolCode().trim(),
                binding.getLogicalDeviceId().trim(),
                String.valueOf(executionRecordId),
                templateId,
                waypoints
        );

        CommonResult<MissionStartRespDTO> rpc = deviceProtocolMissionApi.dispatchAndStartup(plan);
        if (rpc == null || !rpc.isSuccess()) {
            throw ServiceExceptionUtil.invalidParamException(
                    rpc != null && StringUtils.hasText(rpc.getMsg())
                            ? rpc.getMsg()
                            : "协议网关调用失败");
        }
        MissionStartRespDTO result = rpc.getData();
        if (result == null || !result.success()) {
            String reason = result != null && StringUtils.hasText(result.failureReason())
                    ? result.failureReason()
                    : "协议网关未成功下发";
            throw ServiceExceptionUtil.invalidParamException(reason);
        }

        markExecutionInProgress(executionRecordId);
        markDispatchedTransition(task);
        return result;
    }

    private TaskExecutionStartRespDTO startExecutionSession(InspectionTaskDO task, List<String> stopIds) {
        TaskExecutionStartReqDTO req = new TaskExecutionStartReqDTO();
        req.setTaskDefinitionId(task.getId());
        req.setEntityTypeCode(PATROL_RECORD_TYPE);
        req.setModelCode(PATROL_EXEC_MODEL_CODE);
        req.setPendingRef(task.getRuntimeJobId());
        req.setName(StringUtils.hasText(task.getTaskName())
                ? task.getTaskName() + "-执行"
                : "巡检执行-" + task.getId());
        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("source", "inspection-start-execution");
        snapshot.put("taskDefinitionId", task.getId());
        snapshot.put("stopIds", stopIds);
        req.setStandardSnapshot(snapshot);
        req.setSteps(draftStepsFromStopIds(stopIds));

        CommonResult<TaskExecutionStartRespDTO> rpc = taskExecutionSessionApi.start(req);
        if (rpc == null || !rpc.isSuccess() || rpc.getData() == null
                || rpc.getData().getExecutionRecordId() == null) {
            throw ServiceExceptionUtil.invalidParamException(
                    rpc != null && StringUtils.hasText(rpc.getMsg())
                            ? rpc.getMsg()
                            : "创建执行记录失败");
        }
        return rpc.getData();
    }

    /**
     * 过渡：用停靠点生成步骤草稿；SOP 动作树就绪后改为从 merge 结果展开。
     */
    private static List<TaskExecutionStartReqDTO.StepDraft> draftStepsFromStopIds(List<String> stopIds) {
        List<TaskExecutionStartReqDTO.StepDraft> steps = new ArrayList<>();
        for (int i = 0; i < stopIds.size(); i++) {
            String stopId = stopIds.get(i);
            TaskExecutionStartReqDTO.StepDraft draft = new TaskExecutionStartReqDTO.StepDraft();
            draft.setName("停靠 " + stopId);
            draft.setStepCode(stopId);
            draft.setStepOrder(i);
            draft.setStepTitle(draft.getName());
            draft.setStepType("move");
            draft.setStepRequired(true);
            steps.add(draft);
        }
        return steps;
    }

    private void markExecutionInProgress(Long executionRecordId) {
        TaskExecutionWritebackReqDTO writeback = new TaskExecutionWritebackReqDTO();
        writeback.setExecutionRecordId(executionRecordId);
        writeback.setEntityTypeCode(PATROL_RECORD_TYPE);
        writeback.setExecutionStatus("in_progress");
        CommonResult<Boolean> rpc = taskExecutionSessionApi.writeback(writeback);
        if (rpc == null || !rpc.isSuccess()) {
            throw ServiceExceptionUtil.invalidParamException(
                    rpc != null && StringUtils.hasText(rpc.getMsg())
                            ? rpc.getMsg()
                            : "回写执行记录状态失败");
        }
    }

    /**
     * 过渡展示字段；权威状态在执行记录。
     */
    private void markDispatchedTransition(InspectionTaskDO task) {
        InspectionTaskDO update = new InspectionTaskDO();
        update.setId(task.getId());
        update.setDeviceRunStatus("DISPATCHED");
        inspectionTaskMapper.updateById(update);
    }

    private InspectionTaskDO requireTask(Long taskId) {
        if (taskId == null) {
            throw ServiceExceptionUtil.invalidParamException("任务 id 不能为空");
        }
        InspectionTaskDO task = inspectionTaskMapper.selectById(taskId);
        if (task == null) {
            throw ServiceExceptionUtil.invalidParamException("任务不存在");
        }
        return task;
    }

    private static ExecutionDeviceBinding requireCompleteBinding(InspectionTaskDO task) {
        ExecutionDeviceBinding binding = task.getExecutionDeviceBinding();
        if (binding == null
                || binding.getEquipmentId() == null
                || !StringUtils.hasText(binding.getProtocolCode())
                || !StringUtils.hasText(binding.getLogicalDeviceId())) {
            throw ServiceExceptionUtil.invalidParamException(
                    "任务未绑定执行设备（须含 equipmentId、protocolCode、logicalDeviceId）");
        }
        return binding;
    }

    private static String requireNetworkRef(InspectionTaskDO task) {
        if (!StringUtils.hasText(task.getNetworkRef())) {
            throw ServiceExceptionUtil.invalidParamException("任务缺少已确认路网 networkRef");
        }
        return task.getNetworkRef().trim();
    }

    private List<String> requireStopIds(InspectionTaskDO task) {
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
            List<String> fromPlanned = asStringList(planned.get("stopIds"));
            if (!CollectionUtils.isEmpty(fromPlanned)) {
                return fromPlanned;
            }
        }
        throw ServiceExceptionUtil.invalidParamException("任务缺少已确认停靠点 stopIds");
    }

    private Set<String> resolvePhotoStopIds(InspectionTaskDO task) {
        Long facilityId = resolveFacilityId(task);
        List<Long> objectIds = extractObjectIds(task.getInspectionContent());
        if (facilityId == null || objectIds.isEmpty()) {
            return Set.of();
        }
        List<ObjectStationBindingDO> bindings =
                objectStationBindingMapper.selectByFacilityAndObjectIds(facilityId, objectIds);
        return bindings.stream()
                .map(ObjectStationBindingDO::getStationNodeId)
                .filter(StringUtils::hasText)
                .collect(Collectors.toCollection(HashSet::new));
    }

    private Long resolveFacilityId(InspectionTaskDO task) {
        if (task.getRoutePlanId() != null) {
            InspectionRoutePlanDO plan = routePlanMapper.selectById(task.getRoutePlanId());
            if (plan != null && plan.getFacilityId() != null) {
                return plan.getFacilityId();
            }
        }
        return null;
    }

    private static List<Long> extractObjectIds(InspectionContent content) {
        if (content == null) {
            return List.of();
        }
        List<Long> ids = new ArrayList<>();
        if (content.getGroups() != null) {
            for (InspectionContent.ObjectGroup group : content.getGroups()) {
                if (group == null || group.getObjects() == null) {
                    continue;
                }
                for (InspectionContent.ObjectContent object : group.getObjects()) {
                    if (object != null && object.getObjectId() != null) {
                        ids.add(object.getObjectId());
                    }
                }
            }
        }
        if (content.getCustomObjects() != null) {
            for (InspectionContent.ObjectContent object : content.getCustomObjects()) {
                if (object != null && object.getObjectId() != null) {
                    ids.add(object.getObjectId());
                }
            }
        }
        return ids;
    }

    private List<MissionWaypoint> buildWaypoints(
            String networkRef,
            List<String> stopIds,
            Set<String> photoStopIds) {
        CommonResult<PathNetworkDTO> networkResult = pathNetworkApi.getNetwork(networkRef);
        if (networkResult == null || !networkResult.isSuccess() || networkResult.getData() == null) {
            throw ServiceExceptionUtil.invalidParamException("无法读取路网：" + networkRef);
        }
        PathNetworkDTO network = networkResult.getData();
        Map<String, PathNodeDTO> nodesById = new HashMap<>();
        if (network.getNodes() != null) {
            for (PathNodeDTO node : network.getNodes()) {
                if (node != null && StringUtils.hasText(node.getNodeId())) {
                    nodesById.put(node.getNodeId(), node);
                }
            }
        }

        List<MissionWaypoint> waypoints = new ArrayList<>();
        for (String stopId : stopIds) {
            PathNodeDTO node = nodesById.get(stopId);
            if (node == null) {
                throw ServiceExceptionUtil.invalidParamException("路网缺少停靠点节点：" + stopId);
            }
            TopologyPointDTO position = node.getPosition();
            if (position == null || position.getX() == null || position.getY() == null) {
                throw ServiceExceptionUtil.invalidParamException("停靠点缺少坐标：" + stopId);
            }
            String lng = String.valueOf(position.getX());
            String lat = String.valueOf(position.getY());
            MissionPointActionType action = photoStopIds.contains(stopId)
                    ? MissionPointActionType.PHOTO
                    : MissionPointActionType.NONE;
            waypoints.add(new MissionWaypoint(stopId, lat, lng, null, null, action));
        }
        return waypoints;
    }

    private Map<String, Object> parseJsonMap(String json) {
        if (!StringUtils.hasText(json)) {
            return null;
        }
        try {
            return objectMapper.readValue(json, new TypeReference<>() {
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
            return objectMapper.readValue(json, new TypeReference<>() {
            });
        } catch (Exception ex) {
            return List.of();
        }
    }

    private static List<String> asStringList(Object raw) {
        if (!(raw instanceof List<?> list)) {
            return List.of();
        }
        return list.stream().filter(Objects::nonNull).map(String::valueOf).toList();
    }
}
