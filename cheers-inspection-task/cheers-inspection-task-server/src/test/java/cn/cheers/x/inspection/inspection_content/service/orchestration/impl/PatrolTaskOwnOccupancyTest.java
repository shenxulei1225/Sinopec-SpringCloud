package cn.cheers.x.inspection.inspection_content.service.orchestration.impl;

import cn.cheers.x.module.platform.contract.dto.slot.ScheduleSlotDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("本任务占窗身份")
class PatrolTaskOwnOccupancyTest {

    @Test
    @DisplayName("当前与历史工作项号都属于同一条任务，不误伤任务 10")
    void ownWorkId_matchesCurrentAndLegacyStems() {
        assertTrue(PatrolTaskOwnOccupancy.isOwnWorkId(1L, "patrol-task-1-0"));
        assertTrue(PatrolTaskOwnOccupancy.isOwnWorkId(1L, "patrol-reserve-1-0"));
        assertTrue(PatrolTaskOwnOccupancy.isOwnWorkId(1L, "patrol-arrange-1"));
        assertTrue(PatrolTaskOwnOccupancy.isOwnWorkId(1L, "patrol-1-0"));
        assertFalse(PatrolTaskOwnOccupancy.isOwnWorkId(1L, "patrol-task-10-0"));
        assertFalse(PatrolTaskOwnOccupancy.isOwnWorkId(1L, "patrol-reserve-10-0"));
        assertFalse(PatrolTaskOwnOccupancy.isOwnWorkId(1L, "other-1-0"));
    }

    @Test
    @DisplayName("作业号对得上时即使工作项号空也算本任务")
    void ownSlot_matchesKnownJobId() {
        ScheduleSlotDTO slot = ScheduleSlotDTO.builder()
                .runtimeJobId("job-draft")
                .build();
        assertTrue(PatrolTaskOwnOccupancy.isOwnSlot(1L, "job-draft", slot));
        assertFalse(PatrolTaskOwnOccupancy.isOwnSlot(1L, "job-other", slot));
    }
}
