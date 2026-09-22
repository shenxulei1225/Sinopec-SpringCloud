package cn.cheers.x.inspection.task.service.execution.scheduleboard;

import cn.cheers.x.device.protocolgateway.api.dto.MissionStartRespDTO;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.task.service.execution.InspectionTaskStartExecutionService;
import cn.cheers.x.inspection.task.service.task.PatrolTaskDraft;
import cn.cheers.x.inspection.task.service.task.PatrolTaskEntityStore;
import cn.cheers.x.module.platform.contract.dto.reservation.ResourceReservationDTO;
import cn.cheers.x.module.platform.contract.dto.slot.ScheduleSlotDTO;
import cn.cheers.x.module.platform.contract.enums.SlotStatus;
import cn.cheers.x.module.platform.runtime.api.ProcessTimelineApi;
import cn.cheers.x.module.platform.runtime.api.RuntimeQueryApi;
import cn.cheers.x.module.platform.runtime.api.dto.ProcessTimelineActionAppendReqDTO;
import cn.cheers.x.module.platform.runtime.api.dto.ProcessTimelineActionRespDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 到点自动开跑（轮询兜底）：在计划执行窗口内触发，只处理 PLANNED 且任务已启用的计划点。
 * <p>权威：L4 reservation.plannedStart/End + 任务 orchestrationCommitted；开跑仍走 {@link InspectionTaskStartExecutionService}。
 * <p>主路径为 {@link PatrolReservationAutoStartScheduler} 一次性调度；本服务每分钟扫描遗漏。
 * <p>不负责：previewOrchestration / commitOrchestration；禁止提前准备窗口开跑。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PatrolScheduleSlotAutoStartService {

    public static final String TASK_ENTITY_TYPE = "task";
    public static final String ACTION_AUTO_START_FAILED = "slot.auto_start_failed";

    static final ZoneOffset ZONE = ZoneOffset.ofHours(8);

    private final RuntimeQueryApi runtimeQueryApi;
    private final ProcessTimelineApi processTimelineApi;
    private final PatrolTaskEntityStore patrolTaskEntityStore;
    private final InspectionTaskStartExecutionService inspectionTaskStartExecutionService;
    private final ObjectMapper objectMapper;

    @Value("${inspection.schedule.auto-start.lookback-minutes:30}")
    private int lookbackMinutes;

    @Value("${inspection.schedule.auto-start.enabled:true}")
    private boolean enabled;

    /**
     * 每分钟扫描计划执行窗口内的 PLANNED 计划点并尝试开跑。
     */
    @Scheduled(fixedDelayString = "${inspection.schedule.auto-start.poll-ms:60000}")
    public void pollDueSlots() {
        if (!enabled) {
            return;
        }
        InspectionScheduledJobContext.runIgnoringTenant(this::pollDueSlotsInternal);
    }

    private void pollDueSlotsInternal() {
        OffsetDateTime now = OffsetDateTime.now(ZONE);
        OffsetDateTime from = now.minusMinutes(lookbackMinutes);
        OffsetDateTime to = now;

        List<ScheduleSlotDTO> candidates;
        try {
            CommonResult<List<ScheduleSlotDTO>> result = runtimeQueryApi.listSlots(
                    from, to, null, TASK_ENTITY_TYPE, null, List.of(SlotStatus.PLANNED));
            if (result == null || !result.isSuccess()) {
                log.warn("[auto-start] 查询计划点失败 code={} msg={}",
                        result != null ? result.getCode() : null,
                        result != null ? result.getMsg() : "null response");
                return;
            }
            candidates = result.getData();
        } catch (Exception ex) {
            log.warn("[auto-start] 查询计划点异常：{}", ex.getMessage());
            return;
        }
        if (CollectionUtils.isEmpty(candidates)) {
            return;
        }

        Map<String, PatrolTaskDraft> taskByJobId = patrolTaskEntityStore.listAll().stream()
                .filter(draft -> StringUtils.hasText(draft.runtimeJobId()))
                .filter(draft -> Boolean.TRUE.equals(draft.orchestrationCommitted()))
                .collect(Collectors.toMap(
                        draft -> draft.runtimeJobId().trim(),
                        draft -> draft,
                        (left, right) -> left));

        for (ScheduleSlotDTO slot : candidates) {
            ResourceReservationDTO reservation = ScheduleSlotDTO.toReservation(slot);
            if (!isTriggerDue(reservation, now)) {
                continue;
            }
            PatrolTaskDraft task = taskByJobId.get(trim(slot.getRuntimeJobId()));
            if (task == null) {
                continue;
            }
            String candidateId = reservation.getCandidateId();
            if (hasAlreadyStarted(candidateId)) {
                continue;
            }
            tryAutoStart(reservation, task);
        }
    }

    /**
     * 判断计划点是否进入自动开跑窗口（plannedStart ≤ now ≤ plannedEnd）。
     */
    static boolean isTriggerDue(ResourceReservationDTO reservation, OffsetDateTime now) {
        if (reservation == null || !SlotStatus.PLANNED.equals(reservation.getCandidateStatus())) {
            return false;
        }
        OffsetDateTime plannedStart = parseInstant(reservation.getPlannedStart());
        if (plannedStart == null || now.isBefore(plannedStart)) {
            return false;
        }
        OffsetDateTime plannedEnd = parseInstant(reservation.getPlannedEnd());
        return plannedEnd == null || !now.isAfter(plannedEnd);
    }

    private void tryAutoStart(ResourceReservationDTO reservation, PatrolTaskDraft task) {
        String candidateId = reservation.getCandidateId();
        log.info("[auto-start] 到点开跑 taskId={} candidateId={} plannedStart={}",
                task.id(), candidateId, reservation.getPlannedStart());
        try {
            MissionStartRespDTO result = inspectionTaskStartExecutionService.startExecution(
                    task.id(), candidateId);
            if (result == null || !result.success()) {
                String reason = result != null && StringUtils.hasText(result.failureReason())
                        ? result.failureReason()
                        : "到点自动开跑失败";
                recordAutoStartFailure(candidateId, task.id(), reason, task.facilityId());
                log.warn("[auto-start] 开跑失败 taskId={} candidateId={} reason={}",
                        task.id(), candidateId, reason);
            }
        } catch (Exception ex) {
            recordAutoStartFailure(candidateId, task.id(), ex.getMessage(), task.facilityId());
            log.error("[auto-start] 开跑异常 taskId={} candidateId={} reason={}",
                    task.id(), candidateId, ex.getMessage());
        }
    }

    /** 时间线已有开跑事件则不再重复触发（slot 状态可能尚未同步的兜底）。 */
    private boolean hasAlreadyStarted(String candidateId) {
        if (!StringUtils.hasText(candidateId)) {
            return false;
        }
        var page = processTimelineApi.pageByTarget(
                PatrolScheduleSlotExecutionWritebackService.TIMELINE_TARGET_TYPE,
                candidateId.trim(), 1, 5).getCheckedData();
        if (page == null || CollectionUtils.isEmpty(page.getList())) {
            return false;
        }
        for (ProcessTimelineActionRespDTO row : page.getList()) {
            if (row != null && ScheduleSlotExecutionTimelineSupport.ACTION_EXECUTION_START
                    .equals(row.getActionCode())) {
                return true;
            }
        }
        return false;
    }

    private void recordAutoStartFailure(String candidateId, Long taskId, String reason, Long facilityId) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("slotId", candidateId);
        payload.put("candidateId", candidateId);
        payload.put("taskId", taskId);
        payload.put("reason", reason);
        try {
            processTimelineApi.append(ProcessTimelineActionAppendReqDTO.builder()
                    .targetType(PatrolScheduleSlotExecutionWritebackService.TIMELINE_TARGET_TYPE)
                    .targetId(candidateId.trim())
                    .occurredAt(OffsetDateTime.now(ZONE))
                    .actionCode(ACTION_AUTO_START_FAILED)
                    .howSummary("到点自动开跑失败：" + (StringUtils.hasText(reason) ? reason.trim() : "未知原因"))
                    .payloadJson(objectMapper.writeValueAsString(payload))
                    .facilityId(facilityId)
                    .build()).checkError();
        } catch (JsonProcessingException ex) {
            log.error("[auto-start] 记录失败时间线序列化异常 candidateId={}", candidateId);
        }
    }

    static OffsetDateTime parseInstant(String raw) {
        if (!StringUtils.hasText(raw)) {
            return null;
        }
        try {
            return OffsetDateTime.parse(raw.trim());
        } catch (Exception ignored) {
            return null;
        }
    }

    private static String trim(String raw) {
        return raw == null ? null : raw.trim();
    }
}
