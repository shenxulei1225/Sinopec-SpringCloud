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
    void rejectsOtherTopics() {
        assertFalse(PushTopicKinds.matches("alarm/facility/12/extra"));
        assertFalse(PushTopicKinds.matches("device/88/alarm"));
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
