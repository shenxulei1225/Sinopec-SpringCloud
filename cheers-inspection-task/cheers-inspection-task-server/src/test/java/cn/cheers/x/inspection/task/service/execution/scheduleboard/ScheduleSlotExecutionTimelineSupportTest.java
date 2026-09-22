package cn.cheers.x.inspection.task.service.execution.scheduleboard;

import cn.cheers.x.module.platform.runtime.api.dto.ProcessTimelineActionRespDTO;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ScheduleSlotExecutionTimelineSupportTest {

    @Test
    void resolve_progressAndStepStatusFromTimeline() {
        List<ProcessTimelineActionRespDTO> timeline = List.of(
                row(1L, ScheduleSlotExecutionTimelineSupport.ACTION_STEP_UPDATE,
                        "2026-09-18T10:00:00+08:00",
                        "{\"stepCode\":\"seq-1\",\"status\":\"completed\",\"progressPercent\":33}"),
                row(2L, ScheduleSlotExecutionTimelineSupport.ACTION_STEP_UPDATE,
                        "2026-09-18T10:05:00+08:00",
                        "{\"stepCode\":\"seq-2\",\"status\":\"failed\",\"progressPercent\":66}")
        );

        ScheduleSlotExecutionTimelineSupport.ExecutionSnapshot snapshot =
                ScheduleSlotExecutionTimelineSupport.resolve(timeline);

        assertEquals(66, snapshot.progressPercent());
        assertEquals("DONE", snapshot.stepStatusByCode().get("seq-1"));
        assertEquals("FAILED", snapshot.stepStatusByCode().get("seq-2"));
        assertTrue(snapshot.hasStepFailure());
    }

    private static ProcessTimelineActionRespDTO row(
            Long id, String actionCode, String occurredAt, String payloadJson) {
        return ProcessTimelineActionRespDTO.builder()
                .id(id)
                .actionCode(actionCode)
                .occurredAt(OffsetDateTime.parse(occurredAt))
                .payloadJson(payloadJson)
                .build();
    }

    @Test
    void resolveNodeStatus_matchesStepCodeOnNode() {
        Map<String, String> statuses = Map.of("seq-3", "DONE");
        Map<String, Object> node = Map.of("nodeKey", "n-move", "stepCode", "seq-3");
        assertEquals("DONE", ScheduleSlotExecutionTimelineSupport.resolveNodeStatus(node, statuses));
    }
}
