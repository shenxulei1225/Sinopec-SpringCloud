package cn.cheers.x.inspection.task.service.query.scheduleboard;

import cn.cheers.x.module.platform.contract.dto.slot.ScheduleSlotDTO;
import cn.cheers.x.module.platform.contract.enums.SlotStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("PlanPointExecutionViewSupport 单元测试")
class PlanPointExecutionViewSupportTest {

    private static final OffsetDateTime NOW = OffsetDateTime.of(2026, 3, 10, 18, 0, 0, 0, ZoneOffset.ofHours(8));

    @Test
    @DisplayName("未开始且未过计划结束 → NOT_STARTED")
    void planned_beforeEnd_notStarted() {
        var view = PlanPointExecutionViewSupport.resolve(slot(
                SlotStatus.PLANNED,
                "2026-03-10T09:00:00+08:00",
                "2026-03-10T20:00:00+08:00"), NOW);
        assertEquals(PlanPointExecutionViewSupport.EXECUTION_NOT_STARTED, view.executionStatus());
        assertNull(view.failureReason());
    }

    @Test
    @DisplayName("未开始且已过计划结束 → FAILED + EXPIRED_NOT_STARTED")
    void planned_afterEnd_expired() {
        var view = PlanPointExecutionViewSupport.resolve(slot(
                SlotStatus.PLANNED,
                "2026-03-10T09:00:00+08:00",
                "2026-03-10T10:30:00+08:00"), NOW);
        assertEquals(PlanPointExecutionViewSupport.EXECUTION_FAILED, view.executionStatus());
        assertEquals(PlanPointExecutionViewSupport.FAILURE_EXPIRED_NOT_STARTED, view.failureReason());
    }

    @Test
    @DisplayName("执行中且已过计划结束 → FAILED + TIMEOUT_INCOMPLETE")
    void inProgress_afterEnd_timeout() {
        var view = PlanPointExecutionViewSupport.resolve(slot(
                SlotStatus.IN_PROGRESS,
                "2026-03-10T09:30:00+08:00",
                "2026-03-10T10:15:00+08:00"), NOW);
        assertEquals(PlanPointExecutionViewSupport.EXECUTION_FAILED, view.executionStatus());
        assertEquals(PlanPointExecutionViewSupport.FAILURE_TIMEOUT_INCOMPLETE, view.failureReason());
    }

    @Test
    @DisplayName("已完成 → DONE 100%")
    void completed_done() {
        var view = PlanPointExecutionViewSupport.resolve(slot(
                SlotStatus.COMPLETED,
                "2026-03-10T09:00:00+08:00",
                "2026-03-10T10:00:00+08:00"), NOW);
        assertEquals(PlanPointExecutionViewSupport.EXECUTION_DONE, view.executionStatus());
        assertEquals(100, view.progressPercent());
    }

    private static ScheduleSlotDTO slot(SlotStatus status, String start, String end) {
        return ScheduleSlotDTO.builder()
                .slotId("slot-1")
                .slotStatus(status)
                .plannedStart(start)
                .plannedEnd(end)
                .build();
    }
}
