package cn.cheers.x.inspection.task.service.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ActionDurationParamSupportTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void minutesFromSlots_readsTopLevelDefault() {
        Map<String, Object> slots = Map.of(
                "version", 1,
                "fields", List.of(
                        Map.of("fieldCode", "dwell_duration", "defaultValue", 30),
                        Map.of("fieldCode", "action_duration", "defaultValue", 2)));
        assertEquals(2, ActionDurationParamSupport.minutesFromSlots(slots, "ROBOT", objectMapper).orElseThrow());
    }

    @Test
    void minutesFromSlots_prefersMatchingMeans() {
        Map<String, Object> slots = Map.of(
                "version", 1,
                "fields", List.of(Map.of("fieldCode", "action_duration", "defaultValue", 9)),
                "methods", List.of(
                        Map.of("methodKey", "UAV", "fields", List.of(
                                Map.of("fieldCode", "action_duration", "defaultValue", 4))),
                        Map.of("methodKey", "ROBOT", "fields", List.of(
                                Map.of("fieldCode", "action_duration", "defaultValue", 1)))));
        assertEquals(1, ActionDurationParamSupport.minutesFromSlots(slots, "ROBOT", objectMapper).orElseThrow());
    }

    @Test
    void minutesFromSlots_missingDefault_isEmpty() {
        Map<String, Object> field = new java.util.LinkedHashMap<>();
        field.put("fieldCode", "action_duration");
        field.put("defaultValue", null);
        Map<String, Object> slots = Map.of("version", 1, "fields", List.of(field));
        assertTrue(ActionDurationParamSupport.minutesFromSlots(slots, "ROBOT", objectMapper).isEmpty());
    }

    @Test
    void minutesFromSlots_doesNotReadDwellDuration() {
        Map<String, Object> slots = Map.of(
                "version", 1,
                "fields", List.of(Map.of("fieldCode", "dwell_duration", "defaultValue", 30)));
        assertTrue(ActionDurationParamSupport.minutesFromSlots(slots, "ROBOT", objectMapper).isEmpty());
    }
}
