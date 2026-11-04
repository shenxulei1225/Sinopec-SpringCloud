package cn.cheers.x.module.dynamicbusiness.service.event;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PlatformEventChannelTopicsTest {

    @Test
    void fillsDeviceAndFacilityPlaceholders() {
        assertEquals("alarm/co2-high/13644",
                PlatformEventChannelTopics.fill("alarm/co2-high/{deviceId}", 1L, 13644L));
        assertEquals("alarm/facility/1",
                PlatformEventChannelTopics.fill("alarm/facility/{facilityId}", 1L, 13644L));
        assertEquals("inspection/device/88/status",
                PlatformEventChannelTopics.fill("inspection/device/{设备编号}/status", 1L, 88L));
    }

    @Test
    void leftoverPlaceholderRefusesToFill() {
        assertNull(PlatformEventChannelTopics.fill("inspection/execution/{执行记录编号}/run", 1L, 88L));
        assertNull(PlatformEventChannelTopics.fill("alarm/co2-high/{deviceId}", 0L, 13644L));
        assertNull(PlatformEventChannelTopics.fill(null, 1L, 88L));
    }

    @Test
    void mqttFilterTurnsEachPlaceholderIntoOneLevel() {
        assertEquals("alarm/co2-high/+",
                PlatformEventChannelTopics.mqttFilter("alarm/co2-high/{deviceId}"));
        assertEquals("alarm/meter/+/cleared",
                PlatformEventChannelTopics.mqttFilter("alarm/meter/{deviceId}/cleared"));
    }
}
