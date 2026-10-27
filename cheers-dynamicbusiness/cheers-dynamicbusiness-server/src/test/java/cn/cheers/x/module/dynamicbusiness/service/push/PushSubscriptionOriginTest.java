package cn.cheers.x.module.dynamicbusiness.service.push;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PushSubscriptionOriginTest {

    @Test
    void patrolLayerIsTheStationLiveMonitor() {
        assertEquals(
                "场站实时监控",
                PushSubscriptionOrigin.subscriberName("patrol-layer", "inspection/execution/15/#"));
        assertEquals(
                "场站实时监控",
                PushSubscriptionOrigin.subscriberName(null, "inspection/device/dock-1/#"));
    }

    @Test
    void unknownPlaceIsNotWritten() {
        assertNull(PushSubscriptionOrigin.subscriberName("other-page", "inspection/execution/15/#"));
        assertNull(PushSubscriptionOrigin.subscriberName(null, "alarm/facility/12"));
    }
}
