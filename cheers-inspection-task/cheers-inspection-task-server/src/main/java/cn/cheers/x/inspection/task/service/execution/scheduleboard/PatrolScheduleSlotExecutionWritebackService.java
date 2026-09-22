package cn.cheers.x.inspection.task.service.execution.scheduleboard;

import cn.cheers.x.module.dynamicbusiness.api.entity.EntityRpcApi;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRespDTO;
import cn.cheers.x.module.dynamicbusiness.api.strategy.dto.StrategyTriggerEventDTO;
import cn.cheers.x.module.platform.contract.dto.reservation.ResourceReservationDTO;
import cn.cheers.x.module.platform.contract.dto.slot.ScheduleSlotDTO;
import cn.cheers.x.module.platform.contract.enums.SlotStatus;
import cn.cheers.x.module.platform.runtime.api.ProcessTimelineApi;
import cn.cheers.x.module.platform.runtime.api.RuntimeQueryApi;
import cn.cheers.x.module.platform.runtime.api.RuntimeSlotWriteApi;
import cn.cheers.x.module.platform.runtime.api.dto.ProcessTimelineActionAppendReqDTO;
import cn.cheers.x.module.platform.runtime.api.dto.RuntimeSlotStatusUpdateReqDTO;
import cn.cheers.x.inspection.task.service.task.PatrolTaskDraft;
import cn.cheers.x.inspection.task.service.task.PatrolTaskEntityStore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import static cn.cheers.x.inspection.task.service.execution.impl.InspectionDeviceUplinkServiceImpl.PATROL_RECORD_TYPE;
import static cn.cheers.x.inspection.task.service.execution.scheduleboard.ScheduleSlotExecutionTimelineSupport.ACTION_EXECUTION_COMPLETE;
import static cn.cheers.x.inspection.task.service.execution.scheduleboard.ScheduleSlotExecutionTimelineSupport.ACTION_EXECUTION_START;
import static cn.cheers.x.inspection.task.service.execution.scheduleboard.ScheduleSlotExecutionTimelineSupport.ACTION_STEP_UPDATE;

/**
 * 巡检执行 → L4 计划点写回：slot 状态、过程时间线、进度摘要。
 * <p>权威：platform_schedule_slot + platform_process_timeline_action（targetType=schedule_slot）。
 * <p>不负责：执行账步骤明细（由 TaskExecutionSession 写）；禁止读路径再猜进度。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PatrolScheduleSlotExecutionWritebackService {

    static final String TIMELINE_TARGET_TYPE = "schedule_slot";
    static final String PENDING_SLOT_FIELD = "pending_execution_id";

    private final RuntimeSlotWriteApi runtimeSlotWriteApi;
    private final ProcessTimelineApi processTimelineApi;
    private final RuntimeQueryApi runtimeQueryApi;
    private final EntityRpcApi entityRpcApi;
    private final PatrolTaskEntityStore patrolTaskEntityStore;
    private final ObjectMapper objectMapper;

    /**
     * 为总任务解析本次应对回的计划点：显式 slotId 优先，否则取 runtimeJob 下最早未完成的 PLANNED 点。
     */
    public String resolveScheduleSlotId(PatrolTaskDraft task, String explicitSlotId) {
        if (StringUtils.hasText(explicitSlotId)) {
            return explicitSlotId.trim();
        }
        if (task == null || !StringUtils.hasText(task.runtimeJobId())) {
            return null;
        }
        List<ScheduleSlotDTO> slots = runtimeQueryApi.listSlotsByJobId(task.runtimeJobId().trim()).getCheckedData();
        if (CollectionUtils.isEmpty(slots)) {
            return null;
        }
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.ofHours(8));
        return slots.stream()
                .map(ScheduleSlotDTO::legacyToReservation)
                .filter(reservation -> reservation != null
                        && (SlotStatus.PLANNED.equals(reservation.getCandidateStatus())
                        || SlotStatus.IN_PROGRESS.equals(reservation.getCandidateStatus())))
                .filter(reservation -> {
                    OffsetDateTime end = parseInstant(reservation.getPlannedEnd());
                    return end == null || !now.isAfter(end);
                })
                .map(ResourceReservationDTO::getCandidateId)
                .filter(StringUtils::hasText)
                .findFirst()
                .orElse(null);
    }

    /**
     * 开跑成功：计划点 → IN_PROGRESS，并追加「开跑」过程事件。
     */
    public void markStarted(String slotId, Long taskId, Long executionRecordId, Long facilityId) {
        if (!StringUtils.hasText(slotId)) {
            return;
        }
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.ofHours(8));
        runtimeSlotWriteApi.updateSlotStatus(RuntimeSlotStatusUpdateReqDTO.builder()
                .slotId(slotId.trim())
                .slotStatus(SlotStatus.IN_PROGRESS)
                .actualStart(now)
                .reason("巡检开跑（手动或到点）")
                .facilityId(facilityId)
                .build()).checkError();

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("slotId", slotId.trim());
        payload.put("taskId", taskId);
        payload.put("executionRecordId", executionRecordId);
        appendTimeline(slotId, facilityId, now, ACTION_EXECUTION_START,
                "巡检开跑", payload);
    }

    /**
     * 采集/策略处理后：按执行账关联的 slotId 同步步骤与整次状态。
     */
    public void syncAfterStrategyHandle(StrategyTriggerEventDTO event) {
        if (event == null || event.getExecutionRecordId() == null) {
            return;
        }
        String slotId = resolveSlotIdFromExecutionRecord(event.getExecutionRecordId(), event.getEntityTypeCode());
        if (!StringUtils.hasText(slotId)) {
            return;
        }
        ScheduleSlotDTO slot = runtimeQueryApi.getSlot(slotId).getCheckedData();
        Long facilityId = resolveFacilityId(slot, event.getTaskDefinitionId());

        if (!CollectionUtils.isEmpty(event.getStepUpdates())) {
            recordStepUpdates(slotId, event.getStepUpdates(), facilityId, slot, event.getTaskDefinitionId());
        }
        if (StringUtils.hasText(event.getExecutionStatus())) {
            syncExecutionStatus(slotId, event.getExecutionStatus().trim(), facilityId);
        }
    }

    void recordStepUpdates(
            String slotId,
            List<Map<String, Object>> stepUpdates,
            Long facilityId,
            ScheduleSlotDTO slot,
            Long taskDefinitionId) {
        if (!StringUtils.hasText(slotId) || CollectionUtils.isEmpty(stepUpdates)) {
            return;
        }
        int totalSteps = countDispatchableSteps(resolveTask(slot, taskDefinitionId));
        int doneCount = countDoneSteps(stepUpdates);
        int progress = totalSteps > 0 ? Math.min(100, doneCount * 100 / totalSteps) : 0;
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.ofHours(8));
        boolean hasFailure = false;

        for (Map<String, Object> update : stepUpdates) {
            if (update == null) {
                continue;
            }
            String stepCode = text(update.get("stepCode"));
            String status = text(update.get("status"));
            if (!StringUtils.hasText(stepCode) || !StringUtils.hasText(status)) {
                continue;
            }
            if ("failed".equalsIgnoreCase(status) || "fault".equalsIgnoreCase(status)) {
                hasFailure = true;
            }
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("slotId", slotId.trim());
            payload.put("stepCode", stepCode.trim());
            payload.put("status", status.trim());
            payload.put("progressPercent", progress);
            appendTimeline(slotId, facilityId, now, ACTION_STEP_UPDATE,
                    "步骤 " + stepCode.trim() + " → " + status.trim(), payload);
        }

        ResourceReservationDTO reservation = slot != null ? ScheduleSlotDTO.toReservation(slot) : null;
        if (hasFailure && reservation != null
                && SlotStatus.IN_PROGRESS.equals(reservation.getCandidateStatus())) {
            appendTimeline(slotId, facilityId, now, ACTION_STEP_UPDATE,
                    "存在失败步骤", Map.of("slotId", slotId.trim(), "hasStepFailure", true));
        }
    }

    private void syncExecutionStatus(String slotId, String executionStatus, Long facilityId) {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.ofHours(8));
        if ("completed".equalsIgnoreCase(executionStatus)) {
            runtimeSlotWriteApi.updateSlotStatus(RuntimeSlotStatusUpdateReqDTO.builder()
                    .slotId(slotId.trim())
                    .slotStatus(SlotStatus.COMPLETED)
                    .actualEnd(now)
                    .reason("巡检完成")
                    .facilityId(facilityId)
                    .build()).checkError();
            appendTimeline(slotId, facilityId, now, ACTION_EXECUTION_COMPLETE,
                    "巡检完成", Map.of("slotId", slotId.trim(), "progressPercent", 100));
            return;
        }
        if ("fault".equalsIgnoreCase(executionStatus)) {
            appendTimeline(slotId, facilityId, now, ACTION_EXECUTION_COMPLETE,
                    "巡检故障终止", Map.of("slotId", slotId.trim(), "hasStepFailure", true));
        }
    }

    private String resolveSlotIdFromExecutionRecord(Long executionRecordId, String entityTypeCode) {
        String type = StringUtils.hasText(entityTypeCode) ? entityTypeCode.trim() : PATROL_RECORD_TYPE;
        EntityRespDTO record = entityRpcApi.getEntity(executionRecordId, type).getCheckedData();
        if (record == null || record.getCustomFields() == null) {
            return null;
        }
        Object raw = record.getCustomFields().get(PENDING_SLOT_FIELD);
        return raw == null ? null : String.valueOf(raw).trim();
    }

    private PatrolTaskDraft resolveTask(ScheduleSlotDTO slot, Long taskDefinitionId) {
        if (taskDefinitionId != null) {
            try {
                return patrolTaskEntityStore.require(taskDefinitionId);
            } catch (Exception ignored) {
                // fall through
            }
        }
        if (slot == null || !StringUtils.hasText(slot.getRuntimeJobId())) {
            return null;
        }
        String jobId = slot.getRuntimeJobId().trim();
        return patrolTaskEntityStore.listAll().stream()
                .filter(draft -> jobId.equals(trim(draft.runtimeJobId())))
                .findFirst()
                .orElse(null);
    }

    @SuppressWarnings("unchecked")
    private static int countDispatchableSteps(PatrolTaskDraft task) {
        if (task == null || task.stepTree() == null) {
            return 0;
        }
        Object raw = task.stepTree();
        Map<String, Object> tree = raw instanceof Map<?, ?> map
                ? (Map<String, Object>) map
                : null;
        if (tree == null) {
            return 0;
        }
        Object nodesRaw = tree.get("nodes");
        if (!(nodesRaw instanceof List<?> nodes)) {
            return 0;
        }
        int count = 0;
        for (Object nodeRaw : nodes) {
            if (!(nodeRaw instanceof Map<?, ?> nodeMap)) {
                continue;
            }
            Object dispatch = nodeMap.get("dispatch");
            if (dispatch instanceof Boolean flag && flag) {
                count++;
            }
        }
        return count > 0 ? count : nodes.size();
    }

    private static int countDoneSteps(List<Map<String, Object>> stepUpdates) {
        int done = 0;
        for (Map<String, Object> update : stepUpdates) {
            if (update == null) {
                continue;
            }
            String status = text(update.get("status"));
            if ("completed".equalsIgnoreCase(status) || "failed".equalsIgnoreCase(status)) {
                done++;
            }
        }
        return done;
    }

    private Long resolveFacilityId(ScheduleSlotDTO slot, Long taskDefinitionId) {
        PatrolTaskDraft task = resolveTask(slot, taskDefinitionId);
        return task != null ? task.facilityId() : null;
    }

    private void appendTimeline(
            String slotId,
            Long facilityId,
            OffsetDateTime occurredAt,
            String actionCode,
            String howSummary,
            Map<String, Object> payload) {
        processTimelineApi.append(ProcessTimelineActionAppendReqDTO.builder()
                .targetType(TIMELINE_TARGET_TYPE)
                .targetId(slotId.trim())
                .occurredAt(occurredAt)
                .actionCode(actionCode)
                .howSummary(howSummary)
                .payloadJson(toJson(payload))
                .facilityId(facilityId)
                .build()).checkError();
    }

    private String toJson(Map<String, Object> payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("过程时间线 payload 序列化失败", ex);
        }
    }

    private static OffsetDateTime parseInstant(String raw) {
        if (!StringUtils.hasText(raw)) {
            return null;
        }
        try {
            return OffsetDateTime.parse(raw.trim());
        } catch (Exception ignored) {
            return null;
        }
    }

    private static String text(Object raw) {
        return raw == null ? null : String.valueOf(raw).trim();
    }

    private static String trim(String raw) {
        return raw == null ? null : raw.trim();
    }
}
