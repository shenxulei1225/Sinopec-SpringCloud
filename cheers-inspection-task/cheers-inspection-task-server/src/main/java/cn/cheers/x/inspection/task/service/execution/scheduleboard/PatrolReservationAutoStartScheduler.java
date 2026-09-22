package cn.cheers.x.inspection.task.service.execution.scheduleboard;

import cn.cheers.x.inspection.task.service.execution.InspectionTaskStartExecutionService;
import cn.cheers.x.module.platform.contract.dto.reservation.ResourceReservationDTO;
import cn.cheers.x.module.platform.contract.enums.CandidateType;
import cn.cheers.x.module.platform.contract.enums.SlotStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

/**
 * 计划执行开始时刻一次性调度：物化成功后按 {@link ResourceReservationDTO#getPlannedStart()} 注册开跑。
 * <p>负责：TASK_EXECUTION 占窗的 register / cancel；到点调用 {@link InspectionTaskStartExecutionService#startExecution(Long, String)}。
 * <p>不负责：轮询兜底（见 {@link PatrolScheduleSlotAutoStartService}）；禁止在读路径补 planned 时刻。
 */
@Slf4j
@Service
public class PatrolReservationAutoStartScheduler {

    private final TaskScheduler taskScheduler;
    private final InspectionTaskStartExecutionService inspectionTaskStartExecutionService;

    public PatrolReservationAutoStartScheduler(
            @Qualifier("inspectionReservationTaskScheduler") TaskScheduler taskScheduler,
            @Lazy InspectionTaskStartExecutionService inspectionTaskStartExecutionService) {
        this.taskScheduler = taskScheduler;
        this.inspectionTaskStartExecutionService = inspectionTaskStartExecutionService;
    }

    private final ConcurrentHashMap<String, ScheduledFuture<?>> scheduledByReservationId = new ConcurrentHashMap<>();

    /**
     * 为 TASK_EXECUTION 占窗注册到点开跑；同 candidateId 重复注册会先取消旧任务。
     */
    public void register(ResourceReservationDTO reservation, Long taskId) {
        if (reservation == null || taskId == null) {
            return;
        }
        if (!CandidateType.TASK_EXECUTION.equals(reservation.getCandidateType())) {
            return;
        }
        if (!SlotStatus.PLANNED.equals(reservation.getCandidateStatus())) {
            return;
        }
        String candidateId = trim(reservation.getCandidateId());
        if (!StringUtils.hasText(candidateId)) {
            return;
        }
        OffsetDateTime plannedStart = PatrolScheduleSlotAutoStartService.parseInstant(
                reservation.getPlannedStart());
        if (plannedStart == null) {
            log.warn("[reservation-scheduler] 跳过注册：缺少 plannedStart candidateId={}", candidateId);
            return;
        }

        cancel(candidateId);
        Instant fireAt = plannedStart.toInstant();
        ScheduledFuture<?> future = taskScheduler.schedule(
                () -> fire(taskId, candidateId),
                fireAt);
        scheduledByReservationId.put(candidateId, future);
        log.info("[reservation-scheduler] 已注册到点开跑 taskId={} candidateId={} at={}",
                taskId, candidateId, reservation.getPlannedStart());
    }

    /**
     * 取消尚未触发的到点开跑调度（如暂停 / 释放占窗时调用）。
     */
    public void cancel(String candidateId) {
        if (!StringUtils.hasText(candidateId)) {
            return;
        }
        String key = candidateId.trim();
        ScheduledFuture<?> existing = scheduledByReservationId.remove(key);
        if (existing != null) {
            existing.cancel(false);
            log.debug("[reservation-scheduler] 已取消 candidateId={}", key);
        }
    }

    private void fire(Long taskId, String candidateId) {
        scheduledByReservationId.remove(candidateId);
        log.info("[reservation-scheduler] 到点触发开跑 taskId={} candidateId={}", taskId, candidateId);
        InspectionScheduledJobContext.runIgnoringTenant(() -> {
            try {
                inspectionTaskStartExecutionService.startExecution(taskId, candidateId);
            } catch (Exception ex) {
                log.error("[reservation-scheduler] 开跑异常 taskId={} candidateId={} reason={}",
                        taskId, candidateId, ex.getMessage());
            }
        });
    }

    private static String trim(String raw) {
        return raw == null ? null : raw.trim();
    }
}
