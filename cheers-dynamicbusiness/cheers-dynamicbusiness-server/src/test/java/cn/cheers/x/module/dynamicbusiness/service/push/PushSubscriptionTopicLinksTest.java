package cn.cheers.x.module.dynamicbusiness.service.push;

import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRespDTO;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PushSubscriptionTopicLinksTest {

    @Test
    void coversExactNamePattern() {
        List<EntityRespDTO> topics = List.of(
                topic(11L, "alarm/facility/{facilityId}"),
                topic(12L, "device/{deviceId}/position"));
        assertEquals(List.of(11L), PushSubscriptionTopicLinks.coveredIds(
                "alarm/facility/12", "name_pattern", topics));
        assertEquals(List.of(12L), PushSubscriptionTopicLinks.coveredIds(
                "device/88/position", "name_pattern", topics));
        assertTrue(PushSubscriptionTopicLinks.coveredIds(
                "device/88/alarm", "name_pattern", topics).isEmpty());
    }

    @Test
    void coversExecutionWildcard() {
        List<EntityRespDTO> topics = List.of(
                topic(21L, "inspection/execution/{executionRecordId}/trajectory"),
                topic(22L, "inspection/execution/{executionRecordId}/step"),
                topic(23L, "device/{deviceId}/status"));
        assertEquals(List.of(21L, 22L), PushSubscriptionTopicLinks.coveredIds(
                "inspection/execution/15/#", "name_pattern", topics));
    }

    @Test
    void namePatternToRegexKeepsPlaceholders() {
        Pattern pattern = Pattern.compile(PushSubscriptionTopicLinks.toRegex("alarm/facility/{facilityId}"));
        assertTrue(pattern.matcher("alarm/facility/12").matches());
        assertFalse(pattern.matcher("alarm/facility/12/extra").matches());
    }

    @Test
    void sameConnectionAndTopicKeepOneCode() {
        String first = PushCatalogCodes.subscriptionCode("page-a", "device/88/position");
        String second = PushCatalogCodes.subscriptionCode("page-a", "device/88/position");
        assertEquals(first, second);
        assertTrue(first.startsWith("ps-"));
        assertTrue(first.length() <= 100);
    }

    private static EntityRespDTO topic(long id, String namePattern) {
        EntityRespDTO row = new EntityRespDTO();
        row.setId(id);
        row.setCustomFields(Map.of("name_pattern", namePattern));
        return row;
    }
}
