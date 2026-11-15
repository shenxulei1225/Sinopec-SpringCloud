package cn.cheers.x.module.dynamicbusiness.service.security;

import cn.cheers.x.device.protocolgateway.api.datacollection.CollectionSample;
import cn.cheers.x.device.protocolgateway.api.datacollection.ProtocolQualifyStatus;
import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.api.security.VehiclePassEventCodes;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VehiclePassSampleTest {

    @Test
    void seen_readsPlateAndLane() {
        VehiclePassSample.Parsed parsed = VehiclePassSample.read(sample(
                VehiclePassEventCodes.SEEN,
                Map.of("车牌", "鄂A12345", "车道", 12, "识别不确定", true)));
        assertTrue(parsed.seen());
        assertEquals(12L, parsed.laneId());
        assertEquals("鄂A12345", parsed.plate());
        assertTrue(parsed.uncertain());
    }

    @Test
    void done_readsDirectionAndTime() {
        VehiclePassSample.Parsed parsed = VehiclePassSample.read(sample(
                VehiclePassEventCodes.DONE,
                Map.of("车牌", "鄂A12345", "车道", "8", "方向", "出去", "时间", "2026-11-12 16:00:00")));
        assertEquals(VehiclePassDecision.OUT, parsed.direction());
        assertEquals(8L, parsed.laneId());
        assertEquals(2026, parsed.when().getYear());
    }

    @Test
    void done_withoutTime_doesNotInventOne() {
        assertThrows(ServiceException.class, () -> VehiclePassSample.read(sample(
                VehiclePassEventCodes.DONE,
                Map.of("车牌", "鄂A12345", "车道", 8, "方向", "进场"))));
    }

    private static CollectionSample sample(String kind, Map<String, Object> fields) {
        return new CollectionSample(
                "parking",
                "gate-1",
                kind,
                ProtocolQualifyStatus.QUALIFIED,
                java.util.List.of(),
                fields,
                null,
                "m1",
                1L);
    }
}
