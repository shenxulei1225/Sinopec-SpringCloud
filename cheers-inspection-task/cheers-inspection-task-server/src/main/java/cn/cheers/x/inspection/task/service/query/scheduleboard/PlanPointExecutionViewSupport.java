package cn.cheers.x.inspection.task.service.query.scheduleboard;

import cn.cheers.x.module.platform.contract.dto.reservation.ResourceReservationDTO;
import cn.cheers.x.module.platform.contract.dto.slot.ScheduleSlotDTO;
import cn.cheers.x.module.platform.contract.enums.SlotStatus;
import org.springframework.util.StringUtils;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;

/**
 * L4 计划点 → 看板执行态读模型（查询侧权威）。
 * <p>过期/超时在此层判定并写入 FAILED + failureReason；展示层只消费结果，禁止再按 plannedEnd 推导。
 */
public final class PlanPointExecutionViewSupport {

    public static final String EXECUTION_NOT_STARTED = "NOT_STARTED";
    public static final String EXECUTION_EXECUTING = "EXECUTING";
    public static final String EXECUTION_DONE = "DONE";
    public static final String EXECUTION_FAILED = "FAILED";

    public static final String FAILURE_EXPIRED_NOT_STARTED = "EXPIRED_NOT_STARTED";
    public static final String FAILURE_TIMEOUT_INCOMPLETE = "TIMEOUT_INCOMPLETE";

    private PlanPointExecutionViewSupport() {
    }

    public record ExecutionView(
            String executionStatus,
            String failureReason,
            int progressPercent
    ) {
    }

    public static ExecutionView resolve(ScheduleSlotDTO slot, OffsetDateTime now) {
        return resolve(slot, now, null);
    }

    /**
     * @param progressOverride 写回链路写入过程时间线的进度摘要；非 null 时 IN_PROGRESS 优先展示该值。
     */
    public static ExecutionView resolve(ScheduleSlotDTO slot, OffsetDateTime now, Integer progressOverride) {
        if (slot == null) {
            return new ExecutionView(EXECUTION_NOT_STARTED, null, 0);
        }
        ResourceReservationDTO reservation = ScheduleSlotDTO.toReservation(slot);
        OffsetDateTime plannedEnd = parseInstant(reservation.getPlannedEnd());
        boolean pastEnd = plannedEnd != null && now.isAfter(plannedEnd);
        SlotStatus slotStatus = reservation.getCandidateStatus();

        if (SlotStatus.COMPLETED.equals(slotStatus)) {
            return new ExecutionView(EXECUTION_DONE, null, 100);
        }
        if (SlotStatus.CANCELLED.equals(slotStatus)) {
            return new ExecutionView(EXECUTION_NOT_STARTED, null, 0);
        }
        if (SlotStatus.IN_PROGRESS.equals(slotStatus)) {
            if (pastEnd) {
                return new ExecutionView(EXECUTION_FAILED, FAILURE_TIMEOUT_INCOMPLETE,
                        progressOverride != null ? progressOverride : 0);
            }
            int progress = progressOverride != null ? Math.max(0, Math.min(100, progressOverride)) : 0;
            return new ExecutionView(EXECUTION_EXECUTING, null, progress);
        }
        if (pastEnd) {
            return new ExecutionView(EXECUTION_FAILED, FAILURE_EXPIRED_NOT_STARTED, 0);
        }
        return new ExecutionView(EXECUTION_NOT_STARTED, null, 0);
    }

    public static OffsetDateTime parseInstant(String raw) {
        if (!StringUtils.hasText(raw)) {
            return null;
        }
        try {
            return OffsetDateTime.parse(raw.trim());
        } catch (DateTimeParseException ignored) {
            return null;
        }
    }

    public static String scheduledDate(String plannedStart) {
        OffsetDateTime instant = parseInstant(plannedStart);
        if (instant == null) {
            return plannedStart != null && plannedStart.length() >= 10 ? plannedStart.substring(0, 10) : "";
        }
        return instant.toLocalDate().toString();
    }
}
