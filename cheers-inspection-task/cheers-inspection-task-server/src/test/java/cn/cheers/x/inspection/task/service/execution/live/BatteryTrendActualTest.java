package cn.cheers.x.inspection.task.service.execution.live;

import cn.cheers.x.inspection.task.controller.admin.vo.execution.BatteryTrendPointVO;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BatteryTrendActualTest {

    @Test
    void stepResultsBecomeRemainingPercentByMinute() {
        Map<String, Object> results = Map.of(
                "a", Map.of(
                        "status", "completed",
                        "resultPayload", Map.of(
                                "receivedAtEpochMs", 1_000_000L,
                                "result", Map.of("electricity", "90"))),
                "b", Map.of(
                        "status", "completed",
                        "resultPayload", Map.of(
                                "receivedAtEpochMs", 1_000_000L + 120_000L,
                                "result", Map.of("electricity", "80%"))));

        List<BatteryTrendPointVO> points = BatteryTrendActual.fromStepResults(results);

        assertEquals(2, points.size());
        assertEquals(0, points.get(0).getMinute());
        assertEquals(90.0, points.get(0).getRemainingPercent());
        assertEquals(2, points.get(1).getMinute());
        assertEquals(80.0, points.get(1).getRemainingPercent());
    }

    @Test
    void missingElectricityOrTimeDoesNotInventAPoint() {
        Map<String, Object> results = Map.of(
                "a", Map.of("resultPayload", Map.of("receivedAtEpochMs", 1L)),
                "b", Map.of("resultPayload", Map.of("result", Map.of("electricity", "80"))));

        assertTrue(BatteryTrendActual.fromStepResults(results).isEmpty());
        assertTrue(BatteryTrendActual.fromStepResults(null).isEmpty());
    }
}
