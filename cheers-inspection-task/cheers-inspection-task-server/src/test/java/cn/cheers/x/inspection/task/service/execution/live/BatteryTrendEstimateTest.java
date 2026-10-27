package cn.cheers.x.inspection.task.service.execution.live;

import cn.cheers.x.inspection.task.controller.admin.vo.execution.BatteryTrendPointVO;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BatteryTrendEstimateTest {

    @Test
    void requiredSecondsAndCurveBecomeRemainingPercentLine() {
        List<BatteryTrendPointVO> points = BatteryTrendEstimate.points("80%", 60, 1800, conventionalCurve());

        assertEquals(2, points.size());
        assertEquals(0, points.get(0).getMinute());
        assertEquals(80.0, points.get(0).getRemainingPercent(), 0.05);
        assertEquals(1, points.get(1).getMinute());
        assertTrue(points.get(1).getRemainingPercent() < 80.0);
    }

    @Test
    void missingCurveUsesUniformDeclineOverFullChargeTime() {
        List<BatteryTrendPointVO> points = BatteryTrendEstimate.points("80%", 60, 1800, null);

        assertEquals(2, points.size());
        assertEquals(80.0, points.get(0).getRemainingPercent(), 0.05);
        assertEquals(76.67, points.get(1).getRemainingPercent(), 0.05);
    }

    @Test
    void taskShorterThanFullChargeEndsAboveZero() {
        List<BatteryTrendPointVO> points = BatteryTrendEstimate.points("100", 840, 14400, null);

        assertEquals(15, points.size());
        assertEquals(100.0, points.get(0).getRemainingPercent(), 0.05);
        assertEquals(94.17, points.get(14).getRemainingPercent(), 0.05);
    }

    @Test
    void missingStartElectricityDoesNotInventAFullChargeLine() {
        assertTrue(BatteryTrendEstimate.points(null, 60, 1800, null).isEmpty());
        assertTrue(BatteryTrendEstimate.points("  ", 60, 1800, null).isEmpty());
    }

    @Test
    void missingSecondsOrFullChargeDoesNotInventALine() {
        assertTrue(BatteryTrendEstimate.points("80%", null, 1800, conventionalCurve()).isEmpty());
        assertTrue(BatteryTrendEstimate.points("80%", 60, null, conventionalCurve()).isEmpty());
        assertTrue(BatteryTrendEstimate.points("80%", 60, null, null).isEmpty());
    }

    private static List<Map<String, Object>> conventionalCurve() {
        return List.of(
                Map.of("percent", 0, "ratio", 0),
                Map.of("percent", 10, "ratio", 0),
                Map.of("percent", 20, "ratio", 0),
                Map.of("percent", 30, "ratio", 0.0915),
                Map.of("percent", 40, "ratio", 0.2031),
                Map.of("percent", 50, "ratio", 0.3237),
                Map.of("percent", 60, "ratio", 0.4506),
                Map.of("percent", 70, "ratio", 0.5825),
                Map.of("percent", 80, "ratio", 0.7183),
                Map.of("percent", 90, "ratio", 0.8576),
                Map.of("percent", 100, "ratio", 1));
    }
}
