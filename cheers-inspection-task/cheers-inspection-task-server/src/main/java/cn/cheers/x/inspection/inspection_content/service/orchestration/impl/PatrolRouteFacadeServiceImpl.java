package cn.cheers.x.inspection.inspection_content.service.orchestration.impl;

import cn.cheers.x.inspection.inspection_content.controller.admin.vo.orchestration.PatrolRouteRunReqVO;
import cn.cheers.x.inspection.inspection_content.controller.admin.vo.orchestration.PatrolScheduleEnableReqVO;
import cn.cheers.x.inspection.inspection_content.controller.admin.vo.orchestration.PatrolSlotWritebackReqVO;
import cn.cheers.x.inspection.inspection_content.controller.admin.vo.orchestration.PatrolTaskPauseReqVO;
import cn.cheers.x.inspection.inspection_content.controller.admin.vo.orchestration.PatrolTaskResumeReqVO;
import cn.cheers.x.inspection.inspection_content.service.orchestration.PatrolRouteFacadeService;
import cn.cheers.x.inspection.task.dal.dataobject.task.InspectionTaskDO;
import cn.cheers.x.inspection.task.dal.mysql.task.InspectionTaskMapper;
import cn.cheers.x.module.platform.contract.ContractVersions;
import cn.cheers.x.module.platform.contract.dto.schedule.ScheduleRunRequest;
import cn.cheers.x.module.platform.contract.dto.schedule.ScheduleRunResponse;
import cn.cheers.x.module.platform.contract.dto.schedule.SchedulingSpecDTO;
import cn.cheers.x.module.platform.contract.dto.slot.AssignedResourceDTO;
import cn.cheers.x.module.platform.contract.dto.slot.ScheduleSlotDTO;
import cn.cheers.x.module.platform.contract.dto.work.WorkItemDTO;
import cn.cheers.x.module.platform.contract.enums.SlotStatus;
import cn.cheers.x.module.platform.orchestration.api.ScheduleRunApi;
import cn.cheers.x.module.platform.orchestration.enums.OrchestrationRefs;
import cn.cheers.x.module.platform.orchestration.phase.OrchestrationPhase;
import cn.cheers.x.module.platform.runtime.api.RuntimeQueryApi;
import cn.cheers.x.module.platform.runtime.api.RuntimeSlotWriteApi;
import cn.cheers.x.module.platform.runtime.api.dto.RuntimeSlotReleaseReqDTO;
import cn.cheers.x.module.platform.runtime.api.dto.RuntimeSlotStatusUpdateReqDTO;
import cn.cheers.x.module.platform.runtime.enums.RuntimeSlotReleaseMode;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.cheers.x.inspection.inspection_content.enums.ErrorCodeConstants.PATROL_CONFIRM_TASK_ID_REQUIRED;
import static cn.cheers.x.inspection.inspection_content.enums.ErrorCodeConstants.PATROL_FACADE_NO_ACTIVE_SLOTS;
import static cn.cheers.x.inspection.inspection_content.enums.ErrorCodeConstants.PATROL_FACADE_NOT_RESERVED;
import static cn.cheers.x.inspection.inspection_content.enums.ErrorCodeConstants.PATROL_FACADE_OCCUPANCY_CONFLICT;
import static cn.cheers.x.inspection.inspection_content.enums.ErrorCodeConstants.PATROL_FACADE_REPLAN_CONTEXT_REQUIRED;
import static cn.cheers.x.inspection.inspection_content.enums.ErrorCodeConstants.PATROL_FACADE_ROUTE_NOT_CONFIRMED;
import static cn.cheers.x.inspection.inspection_content.enums.ErrorCodeConstants.PATROL_FACADE_RUNTIME_JOB_REQUIRED;
import static cn.cheers.x.inspection.inspection_content.enums.ErrorCodeConstants.PATROL_FACADE_TASK_NOT_FOUND;

/**
 * 巡检组路线门面：组装种子工作项并调用编排 / 运行时 API。
 */
@Service
@Validated
public class PatrolRouteFacadeServiceImpl implements PatrolRouteFacadeService {

    static final String FACILITY_ID = "facilityId";
    static final String OBJECT_IDS = "objectIds";
    static final String PREFERRED_NETWORK_REF = "preferredNetworkRef";
    static final String TASK_ID = "taskId";
    static final String FROM_CONFIRMED_SNAPSHOT = "fromConfirmedSnapshot";
    static final String TASK_ENABLED = "taskEnabled";
    static final String START_STOP_ID = "startStopId";
    static final String RETURN_TO_START = "returnToStart";

    private static final ZoneId DEFAULT_ZONE = ZoneId.of("Asia/Shanghai");

    private static final SchedulingSpecDTO PLACEHOLDER_SCHEDULING_SPEC = SchedulingSpecDTO.builder()
            .mode("once")
            .conflictStrategy("none")
            .build();

    @Resource
    private ScheduleRunApi scheduleRunApi;

    @Resource
    private RuntimeQueryApi runtimeQueryApi;

    @Resource
    private RuntimeSlotWriteApi runtimeSlotWriteApi;

    @Resource
    private InspectionTaskMapper taskMapper;

    @Override
    public ScheduleRunResponse previewRoute(PatrolRouteRunReqVO reqVO) {
        return scheduleRunApi.run(ScheduleRunRequest.builder()
                .contractVersion(ContractVersions.MVP)
                .orchestrationRef(OrchestrationRefs.PATROL_ROUTE_PREVIEW_V1)
                .dryRun(true)
                .stopAfterPhase(OrchestrationPhase.ROUTE.name())
                .schedulingSpec(PLACEHOLDER_SCHEDULING_SPEC)
                .workItems(List.of(seedWorkItem(reqVO, reqVO.getTaskId(), false)))
                .build()).getCheckedData();
    }

    @Override
    public ScheduleRunResponse confirmRoute(PatrolRouteRunReqVO reqVO) {
        if (reqVO.getTaskId() == null) {
            throw exception(PATROL_CONFIRM_TASK_ID_REQUIRED);
        }
        return scheduleRunApi.run(ScheduleRunRequest.builder()
                .contractVersion(ContractVersions.MVP)
                .orchestrationRef(OrchestrationRefs.PATROL_ROUTE_CONFIRM_V1)
                .dryRun(false)
                .schedulingSpec(PLACEHOLDER_SCHEDULING_SPEC)
                .workItems(List.of(seedWorkItem(reqVO, reqVO.getTaskId(), false)))
                .build()).getCheckedData();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ScheduleRunResponse reserveSchedule(PatrolScheduleEnableReqVO reqVO) {
        InspectionTaskDO task = requireTaskWithConfirmedRoute(reqVO.getTaskId());
        ScheduleRunResponse response = scheduleRunApi.run(buildScheduleReserveRequest(reqVO)).getCheckedData();
        task.setEnabled(Boolean.FALSE);
        task.setRuntimeJobId(response.getRuntimeJobId());
        taskMapper.updateById(task);
        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ScheduleRunResponse enableSchedule(PatrolScheduleEnableReqVO reqVO) {
        InspectionTaskDO task = requireTaskWithConfirmedRoute(reqVO.getTaskId());
        if (!StringUtils.hasText(task.getRuntimeJobId())) {
            throw exception(PATROL_FACADE_NOT_RESERVED);
        }
        verifyEnableWindow(task, reqVO.getSchedulingSpec());
        task.setEnabled(Boolean.TRUE);
        taskMapper.updateById(task);
        List<ScheduleSlotDTO> slots = runtimeQueryApi.listSlotsByJobId(task.getRuntimeJobId()).getCheckedData();
        return ScheduleRunResponse.builder()
                .contractVersion(ContractVersions.MVP)
                .runtimeJobId(task.getRuntimeJobId())
                .slots(slots)
                .build();
    }

    @Override
    public void holdPause(PatrolTaskPauseReqVO reqVO) {
        releaseUnfinished(reqVO, RuntimeSlotReleaseMode.HOLD_PAUSE);
    }

    @Override
    public void yieldPause(PatrolTaskPauseReqVO reqVO) {
        releaseUnfinished(reqVO, RuntimeSlotReleaseMode.YIELD_PAUSE);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void abort(PatrolTaskPauseReqVO reqVO) {
        releaseUnfinished(reqVO, RuntimeSlotReleaseMode.ABORT);
        InspectionTaskDO task = requireTask(reqVO.getTaskId());
        task.setEnabled(Boolean.FALSE);
        taskMapper.updateById(task);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ScheduleRunResponse resume(PatrolTaskResumeReqVO reqVO) {
        if (CollectionUtils.isEmpty(reqVO.getRemainingStopIds())
                && CollectionUtils.isEmpty(reqVO.getCompletedSlotIds())) {
            throw exception(PATROL_FACADE_REPLAN_CONTEXT_REQUIRED);
        }
        requireTask(reqVO.getTaskId());

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put(TASK_ID, reqVO.getTaskId());
        payload.put(FROM_CONFIRMED_SNAPSHOT, true);
        payload.put(TASK_ENABLED, Boolean.FALSE);

        WorkItemDTO seed = WorkItemDTO.builder()
                .contractVersion(ContractVersions.MVP)
                .workId("patrol-resume-" + reqVO.getTaskId())
                .payload(payload)
                .build();

        ScheduleRunRequest request = ScheduleRunRequest.builder()
                .contractVersion(ContractVersions.MVP)
                .orchestrationRef(OrchestrationRefs.PATROL_REPLAN_V1)
                .dryRun(false)
                .schedulingSpec(reqVO.getSchedulingSpec())
                .sourceRuntimeJobId(reqVO.getSourceRuntimeJobId())
                .remainingStopIds(reqVO.getRemainingStopIds())
                .completedSlotIds(reqVO.getCompletedSlotIds())
                .workItems(List.of(seed))
                .build();

        ScheduleRunResponse response = scheduleRunApi.run(request).getCheckedData();

        InspectionTaskDO task = requireTask(reqVO.getTaskId());
        task.setEnabled(Boolean.FALSE);
        task.setRuntimeJobId(response.getRuntimeJobId());
        taskMapper.updateById(task);
        return response;
    }

    @Override
    public void writebackSlot(PatrolSlotWritebackReqVO reqVO) {
        runtimeSlotWriteApi.updateSlotStatus(RuntimeSlotStatusUpdateReqDTO.builder()
                .slotId(reqVO.getSlotId())
                .slotStatus(reqVO.getSlotStatus())
                .actualStart(reqVO.getActualStart())
                .actualEnd(reqVO.getActualEnd())
                .reason(reqVO.getReason())
                .build()).checkError();
    }

    private ScheduleRunRequest buildScheduleReserveRequest(PatrolScheduleEnableReqVO reqVO) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put(TASK_ID, reqVO.getTaskId());
        payload.put(FROM_CONFIRMED_SNAPSHOT, true);
        payload.put(TASK_ENABLED, Boolean.FALSE);

        WorkItemDTO seed = WorkItemDTO.builder()
                .contractVersion(ContractVersions.MVP)
                .workId("patrol-reserve-" + reqVO.getTaskId())
                .payload(payload)
                .build();

        return ScheduleRunRequest.builder()
                .contractVersion(ContractVersions.MVP)
                .orchestrationRef(OrchestrationRefs.PATROL_SCHEDULE_ENABLE_V1)
                .dryRun(false)
                .schedulingSpec(reqVO.getSchedulingSpec())
                .workItems(List.of(seed))
                .build();
    }

    private void releaseUnfinished(PatrolTaskPauseReqVO reqVO, RuntimeSlotReleaseMode mode) {
        String runtimeJobId = resolveRuntimeJobId(reqVO);
        runtimeSlotWriteApi.releaseUnfinished(RuntimeSlotReleaseReqDTO.builder()
                .runtimeJobId(runtimeJobId)
                .mode(mode)
                .reason(reqVO.getReason())
                .build()).checkError();
    }

    private String resolveRuntimeJobId(PatrolTaskPauseReqVO reqVO) {
        if (StringUtils.hasText(reqVO.getRuntimeJobId())) {
            return reqVO.getRuntimeJobId();
        }
        InspectionTaskDO task = requireTask(reqVO.getTaskId());
        if (!StringUtils.hasText(task.getRuntimeJobId())) {
            throw exception(PATROL_FACADE_RUNTIME_JOB_REQUIRED);
        }
        return task.getRuntimeJobId();
    }

    private void verifyEnableWindow(InspectionTaskDO task, SchedulingSpecDTO schedulingSpec) {
        String runtimeJobId = task.getRuntimeJobId();
        List<ScheduleSlotDTO> ownSlots = runtimeQueryApi.listSlotsByJobId(runtimeJobId).getCheckedData();
        if (CollectionUtils.isEmpty(ownSlots)) {
            throw exception(PATROL_FACADE_NO_ACTIVE_SLOTS);
        }
        boolean hasActive = ownSlots.stream().anyMatch(this::isActiveSlot);
        if (!hasActive) {
            throw exception(PATROL_FACADE_NO_ACTIVE_SLOTS);
        }

        OffsetDateTime from = horizonStart(schedulingSpec);
        OffsetDateTime to = horizonEnd(schedulingSpec);
        List<ScheduleSlotDTO> horizonSlots = runtimeQueryApi.listSlots(
                from, to, null, null, null, null).getCheckedData();
        if (CollectionUtils.isEmpty(horizonSlots)) {
            return;
        }

        for (ScheduleSlotDTO own : ownSlots) {
            if (!isActiveSlot(own)) {
                continue;
            }
            for (ScheduleSlotDTO other : horizonSlots) {
                if (runtimeJobId.equals(other.getRuntimeJobId())) {
                    continue;
                }
                if (!isActiveSlot(other)) {
                    continue;
                }
                if (slotsOverlapOnSharedResource(own, other)) {
                    throw exception(PATROL_FACADE_OCCUPANCY_CONFLICT);
                }
            }
        }
    }

    private boolean isActiveSlot(ScheduleSlotDTO slot) {
        return slot != null
                && (SlotStatus.PLANNED.equals(slot.getSlotStatus())
                || SlotStatus.IN_PROGRESS.equals(slot.getSlotStatus()));
    }

    private boolean slotsOverlapOnSharedResource(ScheduleSlotDTO a, ScheduleSlotDTO b) {
        OffsetDateTime aStart = parseOffset(a.getPlannedStart());
        OffsetDateTime aEnd = parseOffset(a.getPlannedEnd());
        OffsetDateTime bStart = parseOffset(b.getPlannedStart());
        OffsetDateTime bEnd = parseOffset(b.getPlannedEnd());
        if (aStart == null || aEnd == null || bStart == null || bEnd == null) {
            return false;
        }
        if (!aEnd.isAfter(bStart) || !bEnd.isAfter(aStart)) {
            return false;
        }
        Set<String> aResources = resourceKeys(a.getAssignedResources());
        Set<String> bResources = resourceKeys(b.getAssignedResources());
        if (aResources.isEmpty() || bResources.isEmpty()) {
            return true;
        }
        for (String key : aResources) {
            if (bResources.contains(key)) {
                return true;
            }
        }
        return false;
    }

    private static Set<String> resourceKeys(List<AssignedResourceDTO> resources) {
        Set<String> keys = new HashSet<>();
        if (resources == null) {
            return keys;
        }
        for (AssignedResourceDTO resource : resources) {
            if (resource == null || !StringUtils.hasText(resource.getResourceId())) {
                continue;
            }
            String type = StringUtils.hasText(resource.getResourceType()) ? resource.getResourceType() : "";
            keys.add(type + ":" + resource.getResourceId());
        }
        return keys;
    }

    private static OffsetDateTime parseOffset(String text) {
        if (!StringUtils.hasText(text)) {
            return null;
        }
        return OffsetDateTime.parse(text, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    private OffsetDateTime horizonStart(SchedulingSpecDTO spec) {
        LocalDate start = parseHorizonDate(spec != null ? spec.getHorizonStart() : null);
        if (start == null) {
            start = LocalDate.now(DEFAULT_ZONE);
        }
        return start.atStartOfDay(DEFAULT_ZONE).toOffsetDateTime();
    }

    private OffsetDateTime horizonEnd(SchedulingSpecDTO spec) {
        LocalDate end = parseHorizonDate(spec != null ? spec.getHorizonEnd() : null);
        if (end == null) {
            LocalDate start = parseHorizonDate(spec != null ? spec.getHorizonStart() : null);
            end = start != null ? start.plusWeeks(4) : LocalDate.now(DEFAULT_ZONE).plusWeeks(4);
        }
        return end.plusDays(1).atStartOfDay(DEFAULT_ZONE).toOffsetDateTime();
    }

    private static LocalDate parseHorizonDate(String text) {
        if (!StringUtils.hasText(text)) {
            return null;
        }
        return LocalDate.parse(text.substring(0, Math.min(text.length(), 10)));
    }

    private InspectionTaskDO requireTaskWithConfirmedRoute(Long taskId) {
        InspectionTaskDO task = requireTask(taskId);
        if (task.getRoutePlanId() == null) {
            throw exception(PATROL_FACADE_ROUTE_NOT_CONFIRMED);
        }
        return task;
    }

    private InspectionTaskDO requireTask(Long taskId) {
        InspectionTaskDO task = taskMapper.selectById(taskId);
        if (task == null) {
            throw exception(PATROL_FACADE_TASK_NOT_FOUND);
        }
        return task;
    }

    private static WorkItemDTO seedWorkItem(PatrolRouteRunReqVO reqVO, Long taskId, boolean fromConfirmedSnapshot) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put(FACILITY_ID, reqVO.getFacilityId());
        payload.put(OBJECT_IDS, reqVO.getObjectIds());
        if (reqVO.getPreferredNetworkRef() != null) {
            payload.put(PREFERRED_NETWORK_REF, reqVO.getPreferredNetworkRef());
        }
        if (taskId != null) {
            payload.put(TASK_ID, taskId);
        }
        if (StringUtils.hasText(reqVO.getStartStopId())) {
            payload.put(START_STOP_ID, reqVO.getStartStopId().trim());
        }
        if (reqVO.getReturnToStart() != null) {
            payload.put(RETURN_TO_START, reqVO.getReturnToStart());
        }
        payload.put(FROM_CONFIRMED_SNAPSHOT, fromConfirmedSnapshot);

        return WorkItemDTO.builder()
                .contractVersion(ContractVersions.MVP)
                .workId("patrol-seed-" + UUID.randomUUID())
                .payload(payload)
                .build();
    }
}
