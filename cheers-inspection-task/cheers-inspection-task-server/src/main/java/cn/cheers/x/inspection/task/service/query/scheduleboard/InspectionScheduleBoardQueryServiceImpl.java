package cn.cheers.x.inspection.task.service.query.scheduleboard;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.inspection.task.controller.admin.vo.scheduleboard.ScheduleBoardExecutionDetailRespVO;
import cn.cheers.x.inspection.task.controller.admin.vo.scheduleboard.ScheduleBoardPlanPointRespVO;
import cn.cheers.x.inspection.task.model.task.ExecutionDeviceBinding;
import cn.cheers.x.inspection.task.service.execution.scheduleboard.ScheduleSlotExecutionTimelineSupport;
import cn.cheers.x.inspection.task.service.task.PatrolTaskDraft;
import cn.cheers.x.inspection.task.service.task.PatrolTaskEntityStore;
import cn.cheers.x.module.platform.contract.dto.slot.AssignedResourceDTO;
import cn.cheers.x.module.platform.contract.dto.reservation.ResourceReservationDTO;
import cn.cheers.x.module.platform.contract.dto.slot.ScheduleSlotDTO;
import cn.cheers.x.module.platform.contract.enums.SlotStatus;
import cn.cheers.x.module.platform.runtime.api.ProcessTimelineApi;
import cn.cheers.x.module.platform.runtime.api.RuntimeQueryApi;
import cn.cheers.x.module.platform.runtime.api.dto.ProcessTimelineActionRespDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 排期看板查询：L4 slot + 任务增强 + 执行态解析。
 * <p>不负责：写 slot 状态、步骤执行回写（由运行时 / 任务执行链路写入）。
 */
@Service
@RequiredArgsConstructor
public class InspectionScheduleBoardQueryServiceImpl implements InspectionScheduleBoardQueryService {

    static final String TIMELINE_TARGET_TYPE = "schedule_slot";
    static final String TASK_ENTITY_TYPE = "task";

    private final RuntimeQueryApi runtimeQueryApi;
    private final ProcessTimelineApi processTimelineApi;
    private final PatrolTaskEntityStore patrolTaskEntityStore;
    private final ObjectMapper objectMapper;

    @Override
    public List<ScheduleBoardPlanPointRespVO> listPlanPoints(
            OffsetDateTime from,
            OffsetDateTime to,
            Long facilityId) {
        List<ScheduleSlotDTO> slots = runtimeQueryApi.listSlots(
                from, to, null, TASK_ENTITY_TYPE, facilityId, null).getCheckedData();
        if (CollectionUtils.isEmpty(slots)) {
            return List.of();
        }

        Map<String, PatrolTaskDraft> taskByJobId = patrolTaskEntityStore.listAll().stream()
                .filter(draft -> StringUtils.hasText(draft.runtimeJobId()))
                .collect(Collectors.toMap(
                        draft -> draft.runtimeJobId().trim(),
                        draft -> draft,
                        (left, right) -> left,
                        LinkedHashMap::new));

        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.ofHours(8));
        Map<String, ScheduleSlotExecutionTimelineSupport.ExecutionSnapshot> snapshots =
                loadExecutionSnapshots(slots);
        List<ScheduleBoardPlanPointRespVO> result = new ArrayList<>();
        for (ScheduleSlotDTO slot : slots) {
            ResourceReservationDTO reservation = ScheduleSlotDTO.toReservation(slot);
            String candidateId = reservation != null ? reservation.getCandidateId() : null;
            ScheduleSlotExecutionTimelineSupport.ExecutionSnapshot snapshot =
                    snapshots.getOrDefault(candidateId, ScheduleSlotExecutionTimelineSupport.ExecutionSnapshot.empty());
            ScheduleBoardPlanPointRespVO vo = toPlanPoint(
                    slot, reservation, taskByJobId.get(trim(slot.getRuntimeJobId())), now, snapshot);
            if (vo != null) {
                result.add(vo);
            }
        }
        return result;
    }

    @Override
    public ScheduleBoardExecutionDetailRespVO getExecutionDetail(String slotId) {
        ScheduleSlotDTO slot = runtimeQueryApi.getSlot(slotId).getCheckedData();
        PatrolTaskDraft task = findTaskByRuntimeJobId(slot.getRuntimeJobId());

        ScheduleBoardExecutionDetailRespVO vo = new ScheduleBoardExecutionDetailRespVO();
        ResourceReservationDTO reservation = ScheduleSlotDTO.toReservation(slot);
        vo.setPointId(reservation != null && StringUtils.hasText(reservation.getCandidateId())
                ? reservation.getCandidateId()
                : slotId);

        List<ProcessTimelineActionRespDTO> timelineRows = loadTimelineRows(slotId);
        vo.setProcessTimeline(timelineRows.stream().map(this::toProcessEvent).toList());

        ScheduleSlotExecutionTimelineSupport.ExecutionSnapshot snapshot =
                ScheduleSlotExecutionTimelineSupport.resolve(timelineRows);
        List<ScheduleBoardExecutionDetailRespVO.StepNodeVO> stepTree =
                buildStepTree(task, slot, snapshot.stepStatusByCode());
        vo.setStepTree(stepTree);

        boolean hasFailure = snapshot.hasStepFailure() || containsFailedStep(stepTree);
        vo.setHasStepFailureAlert(hasFailure);
        if (hasFailure) {
            vo.setAlertMessage("存在失败步骤，请查看步骤树与执行过程。");
        }
        return vo;
    }

    private ScheduleBoardPlanPointRespVO toPlanPoint(
            ScheduleSlotDTO slot,
            ResourceReservationDTO reservation,
            PatrolTaskDraft task,
            OffsetDateTime now,
            ScheduleSlotExecutionTimelineSupport.ExecutionSnapshot snapshot) {
        if (reservation == null || !StringUtils.hasText(reservation.getPlannedStart())) {
            return null;
        }

        Integer progressOverride = snapshot.progressPercent() > 0 ? snapshot.progressPercent() : null;
        PlanPointExecutionViewSupport.ExecutionView view =
                PlanPointExecutionViewSupport.resolve(slot, now, progressOverride);
        AssignedResourceDTO resource = firstResource(slot);
        Long deviceId = parseDeviceId(resource);
        String deviceKind = mapDeviceKind(resource != null ? resource.getResourceType() : null);

        String plannedStart = reservation.getPlannedStart();
        String plannedEnd = StringUtils.hasText(reservation.getPlannedEnd())
                ? reservation.getPlannedEnd()
                : plannedStart;

        ScheduleBoardPlanPointRespVO vo = new ScheduleBoardPlanPointRespVO();
        vo.setId(reservation.getCandidateId());
        vo.setScheduleCode(reservation.getCandidateId());
        vo.setSourceTaskId(task != null ? task.id() : 0L);
        vo.setSourceTaskName(resolveTaskName(task, slot));
        vo.setDeviceId(deviceId);
        vo.setDeviceName(resolveDeviceName(task, resource, deviceId));
        vo.setDeviceCode(resolveDeviceCode(resource, deviceId));
        vo.setDeviceKind(deviceKind);
        vo.setScheduledDate(PlanPointExecutionViewSupport.scheduledDate(plannedStart));
        vo.setPlannedStart(plannedStart);
        vo.setPlannedEnd(plannedEnd);
        vo.setPlannedStart(plannedStart);
        vo.setPlannedEnd(plannedEnd);
        vo.setActualStart(reservation.getActualStart());
        vo.setActualEnd(reservation.getActualEnd());
        vo.setScheduleStatus(mapScheduleStatus(reservation.getCandidateStatus()));
        vo.setExecutionStatus(view.executionStatus());
        vo.setFailureReason(view.failureReason());
        vo.setProgressPercent(view.progressPercent());
        vo.setHasStepFailureAlert(snapshot.hasStepFailure());
        return vo;
    }

    private Map<String, ScheduleSlotExecutionTimelineSupport.ExecutionSnapshot> loadExecutionSnapshots(
            List<ScheduleSlotDTO> slots) {
        Map<String, ScheduleSlotExecutionTimelineSupport.ExecutionSnapshot> result = new HashMap<>();
        for (ScheduleSlotDTO slot : slots) {
            ResourceReservationDTO reservation = ScheduleSlotDTO.toReservation(slot);
            if (reservation == null || !StringUtils.hasText(reservation.getCandidateId())) {
                continue;
            }
            if (!SlotStatus.IN_PROGRESS.equals(reservation.getCandidateStatus())) {
                continue;
            }
            String candidateId = reservation.getCandidateId();
            result.put(candidateId, ScheduleSlotExecutionTimelineSupport.resolve(
                    loadTimelineRows(candidateId)));
        }
        return result;
    }

    private List<ScheduleBoardExecutionDetailRespVO.ProcessEventVO> loadProcessTimeline(String slotId) {
        return loadTimelineRows(slotId).stream()
                .map(this::toProcessEvent)
                .toList();
    }

    private List<ProcessTimelineActionRespDTO> loadTimelineRows(String slotId) {
        PageResult<ProcessTimelineActionRespDTO> page = processTimelineApi.pageByTarget(
                TIMELINE_TARGET_TYPE, slotId, 1, 100).getCheckedData();
        if (page == null || CollectionUtils.isEmpty(page.getList())) {
            return List.of();
        }
        return page.getList().stream()
                .sorted(Comparator.comparing(ProcessTimelineActionRespDTO::getOccurredAt,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();
    }

    private ScheduleBoardExecutionDetailRespVO.ProcessEventVO toProcessEvent(ProcessTimelineActionRespDTO row) {
        ScheduleBoardExecutionDetailRespVO.ProcessEventVO event = new ScheduleBoardExecutionDetailRespVO.ProcessEventVO();
        event.setId(row.getId() != null ? String.valueOf(row.getId()) : row.getActionCode());
        event.setAt(row.getOccurredAt() != null ? row.getOccurredAt().toString() : "");
        event.setLabel(StringUtils.hasText(row.getHowSummary()) ? row.getHowSummary() : row.getActionCode());
        event.setKind(mapTimelineKind(row.getActionCode()));
        event.setDetail(row.getPayloadJson());
        return event;
    }

    private static String mapTimelineKind(String actionCode) {
        if (!StringUtils.hasText(actionCode)) {
            return "step";
        }
        String key = actionCode.trim().toLowerCase();
        if (key.contains("execution_start") || key.contains("start") || key.contains("开跑")) {
            return "start";
        }
        if (key.contains("auto_start_failed") || key.contains("auto_start")) {
            return "alert";
        }
        if (key.contains("end") || key.contains("完成") || key.contains("结束")) {
            return "end";
        }
        if (key.contains("alert") || key.contains("fail") || key.contains("失败") || key.contains("告警")) {
            return "alert";
        }
        return "step";
    }

    @SuppressWarnings("unchecked")
    private List<ScheduleBoardExecutionDetailRespVO.StepNodeVO> buildStepTree(
            PatrolTaskDraft task,
            ScheduleSlotDTO slot,
            Map<String, String> stepStatusByCode) {
        if (task == null || task.stepTree() == null) {
            return List.of();
        }
        Object raw = task.stepTree();
        Map<String, Object> tree = raw instanceof Map<?, ?> map
                ? (Map<String, Object>) map
                : objectMapper.convertValue(raw, Map.class);
        Object nodesRaw = tree.get("nodes");
        if (!(nodesRaw instanceof List<?> nodes) || nodes.isEmpty()) {
            return List.of();
        }

        ResourceReservationDTO reservation = ScheduleSlotDTO.toReservation(slot);
        String defaultStatus = reservation != null
                && SlotStatus.COMPLETED.equals(reservation.getCandidateStatus()) ? "DONE" : "PENDING";
        Map<String, ScheduleBoardExecutionDetailRespVO.StepNodeVO> byKey = new LinkedHashMap<>();
        for (Object nodeRaw : nodes) {
            if (!(nodeRaw instanceof Map<?, ?> nodeMap)) {
                continue;
            }
            String nodeKey = text(nodeMap.get("nodeKey"));
            if (!StringUtils.hasText(nodeKey)) {
                continue;
            }
            ScheduleBoardExecutionDetailRespVO.StepNodeVO node = new ScheduleBoardExecutionDetailRespVO.StepNodeVO();
            node.setId(nodeKey);
            node.setName(firstText(nodeMap.get("title"), nodeMap.get("stepTitle"), nodeKey));
            String resolved = ScheduleSlotExecutionTimelineSupport.resolveNodeStatus(
                    castMap(nodeMap), stepStatusByCode);
            node.setStatus(StringUtils.hasText(resolved) ? resolved : defaultStatus);
            byKey.put(nodeKey, node);
        }

        List<ScheduleBoardExecutionDetailRespVO.StepNodeVO> roots = new ArrayList<>();
        for (Object nodeRaw : nodes) {
            if (!(nodeRaw instanceof Map<?, ?> nodeMap)) {
                continue;
            }
            String nodeKey = text(nodeMap.get("nodeKey"));
            ScheduleBoardExecutionDetailRespVO.StepNodeVO node = byKey.get(nodeKey);
            if (node == null) {
                continue;
            }
            String parentKey = text(nodeMap.get("parentNodeKey"));
            if (StringUtils.hasText(parentKey) && byKey.containsKey(parentKey)) {
                byKey.get(parentKey).getChildren().add(node);
            } else {
                roots.add(node);
            }
        }
        return roots.isEmpty() ? new ArrayList<>(byKey.values()) : roots;
    }

    private static boolean containsFailedStep(List<ScheduleBoardExecutionDetailRespVO.StepNodeVO> nodes) {
        if (CollectionUtils.isEmpty(nodes)) {
            return false;
        }
        for (ScheduleBoardExecutionDetailRespVO.StepNodeVO node : nodes) {
            if ("FAILED".equalsIgnoreCase(node.getStatus())) {
                return true;
            }
            if (containsFailedStep(node.getChildren())) {
                return true;
            }
        }
        return false;
    }

    private PatrolTaskDraft findTaskByRuntimeJobId(String runtimeJobId) {
        if (!StringUtils.hasText(runtimeJobId)) {
            return null;
        }
        String key = runtimeJobId.trim();
        return patrolTaskEntityStore.listAll().stream()
                .filter(draft -> key.equals(trim(draft.runtimeJobId())))
                .findFirst()
                .orElse(null);
    }

    private static String mapScheduleStatus(SlotStatus slotStatus) {
        if (SlotStatus.CANCELLED.equals(slotStatus)) {
            return "CANCELLED";
        }
        return "PLANNED";
    }

    private static AssignedResourceDTO firstResource(ScheduleSlotDTO slot) {
        if (slot.getAssignedResources() == null || slot.getAssignedResources().isEmpty()) {
            return null;
        }
        return slot.getAssignedResources().get(0);
    }

    private static Long parseDeviceId(AssignedResourceDTO resource) {
        if (resource == null || !StringUtils.hasText(resource.getResourceId())) {
            return null;
        }
        try {
            return Long.parseLong(resource.getResourceId().trim());
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private static String mapDeviceKind(String resourceType) {
        if (!StringUtils.hasText(resourceType)) {
            return "robot";
        }
        String key = resourceType.trim().toUpperCase();
        if ("UAV".equals(key)) {
            return "drone";
        }
        if ("HUMAN".equals(key) || "MANUAL".equals(key)) {
            return "manual";
        }
        return "robot";
    }

    private static String resolveTaskName(PatrolTaskDraft task, ScheduleSlotDTO slot) {
        if (task != null && StringUtils.hasText(task.name())) {
            return task.name().trim();
        }
        if (StringUtils.hasText(slot.getWorkId())) {
            return slot.getWorkId().trim();
        }
        return "未关联任务";
    }

    private static String resolveDeviceName(PatrolTaskDraft task, AssignedResourceDTO resource, Long deviceId) {
        ExecutionDeviceBinding binding = task != null ? task.executionDeviceBinding() : null;
        if (binding != null && binding.getEquipmentId() != null && binding.getEquipmentId().equals(deviceId)) {
            if (StringUtils.hasText(binding.getLogicalDeviceId())) {
                return binding.getLogicalDeviceId().trim();
            }
        }
        if (resource != null && StringUtils.hasText(resource.getResourceId())) {
            return "设备 #" + resource.getResourceId().trim();
        }
        return "未分配设备";
    }

    private static String resolveDeviceCode(AssignedResourceDTO resource, Long deviceId) {
        if (resource != null && StringUtils.hasText(resource.getResourceId())) {
            String id = resource.getResourceId().trim();
            if (id.length() == 1) {
                return id.toUpperCase();
            }
            return id.substring(id.length() - 1).toUpperCase();
        }
        return deviceId != null ? String.valueOf(deviceId % 26 + 1) : "?";
    }

    private static String trim(String raw) {
        return raw == null ? null : raw.trim();
    }

    private static String text(Object raw) {
        return raw == null ? null : String.valueOf(raw).trim();
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> castMap(Map<?, ?> nodeMap) {
        return (Map<String, Object>) nodeMap;
    }

    private static String firstText(Object... candidates) {
        for (Object candidate : candidates) {
            String text = text(candidate);
            if (StringUtils.hasText(text)) {
                return text;
            }
        }
        return "";
    }
}
