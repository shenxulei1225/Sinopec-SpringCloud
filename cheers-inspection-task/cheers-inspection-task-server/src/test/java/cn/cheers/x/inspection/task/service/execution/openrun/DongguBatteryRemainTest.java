package cn.cheers.x.inspection.task.service.execution.openrun;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class DongguBatteryRemainTest {

    @Test
    void curveAndFullChargeMinutesBecomeRemainSeconds() {
        Integer full = DongguBatteryRemain.fullBatterySeconds(30);

        assertEquals(1800, full);
        assertEquals(1366, DongguBatteryRemain.seconds("82.9", full, conventionalCurve()));
        assertEquals(1293, DongguBatteryRemain.seconds("80%", full, conventionalCurve()));
        assertEquals(0, DongguBatteryRemain.seconds("20", full, conventionalCurve()));
    }

    @Test
    void missingCurveOrFullChargeDoesNotInventTime() {
        assertNull(DongguBatteryRemain.seconds("82.9", 1800, null));
        assertNull(DongguBatteryRemain.seconds("82.9", null, conventionalCurve()));
        assertNull(DongguBatteryRemain.seconds("82.9", DongguBatteryRemain.fullBatterySeconds(null), conventionalCurve()));
        assertNull(DongguBatteryRemain.seconds("电量", 1800, conventionalCurve()));
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
