package cn.cheers.x.inspection.task.service.execution.openrun;

import cn.cheers.x.device.protocolgateway.api.protocol.ProtocolDispatchProfile;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BatteryHeadGateTest {

    @Test
    void enoughRemainTimeDoesNotBlock() {
        assertNull(BatteryHeadGate.blockPrompt(tree(180), 180, null));
    }

    @Test
    void shortRemainTimeBlocksWithoutSending() {
        String prompt = BatteryHeadGate.blockPrompt(tree(180), 100, "82.9");

        assertTrue(prompt.contains("这次无法执行"));
        assertTrue(prompt.contains("100"));
        assertTrue(prompt.contains("180"));
    }

    @Test
    void missingFullChargeTimeDoesNotTreatPercentAsSeconds() {
        String prompt = BatteryHeadGate.blockPrompt(tree(180), null, "82.9", true);

        assertTrue(prompt.contains("没有耗电曲线"));
        assertTrue(prompt.contains("最长飞行时间"));
        assertTrue(prompt.contains("82.9"));
        assertTrue(prompt.contains("这次无法执行"));
    }

    @Test
    void emptyRequiredSecondsBlocks() {
        String prompt = BatteryHeadGate.blockPrompt(tree(null), 500, null);

        assertTrue(prompt.contains("没有记下这次任务需要的秒数"));
    }

    private static Map<String, Object> tree(Integer requiredSeconds) {
        Map<String, Object> params = requiredSeconds == null ? Map.of() : Map.of("required_duration", requiredSeconds);
        return Map.of("nodes", List.of(Map.of(
                "refCode", ProtocolDispatchProfile.ACTION_UAV_BATTERY_CHECK,
                "params", params)));
    }
}
