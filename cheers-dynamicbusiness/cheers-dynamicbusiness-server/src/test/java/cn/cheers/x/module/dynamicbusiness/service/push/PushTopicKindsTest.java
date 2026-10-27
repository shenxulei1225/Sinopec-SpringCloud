package cn.cheers.x.module.dynamicbusiness.service.push;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PushTopicKindsTest {

    @Test
    void acceptsTheThreeFixedTopics() {
        assertTrue(PushTopicKinds.matches("alarm/facility/12"));
        assertTrue(PushTopicKinds.matches("device/88/position"));
        assertTrue(PushTopicKinds.matches("device/drone-1/status"));
    }

    @Test
    void acceptsPatrolLayerFilters() {
        assertTrue(PushTopicKinds.matches("inspection/execution/15/#"));
        assertTrue(PushTopicKinds.matches("inspection/device/dock-1/#"));
        assertTrue(PushTopicKinds.matches("inspection/execution/15/trajectory"));
        assertTrue(PushTopicKinds.matches("inspection/execution/15/step"));
        assertTrue(PushTopicKinds.matches("inspection/device/dock-1/status"));
    }

    @Test
    void rejectsOtherTopics() {
        assertFalse(PushTopicKinds.matches("alarm/facility/12/extra"));
        assertFalse(PushTopicKinds.matches("device/88/alarm"));
        assertFalse(PushTopicKinds.matches("inspection/execution/0/#"));
        assertFalse(PushTopicKinds.matches(null));
    }

    @Test
    void sameConnectionAndTopicKeepOneCode() {
        String first = PushCatalogCodes.subscriptionCode("page-a", "device/88/position");
        String second = PushCatalogCodes.subscriptionCode("page-a", "device/88/position");
        assertEquals(first, second);
        assertTrue(first.startsWith("ps-"));
        assertTrue(first.length() <= 100);
    }
}
