package cn.cheers.x.inspection.task.service.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ActionParamSlotsSupportTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void fieldCodes_readsRobotAimSlotsNotManualLocation() {
        Map<String, Object> slots = Map.of(
                "version", 2,
                "fields", List.of(Map.of("fieldCode", "action_duration")),
                "methods", List.of(
                        Map.of("executionMeans", "MANUAL", "fields", List.of(
                                Map.of("fieldCode", "F-e49f76bdca3e4e1393e8e2f1cc0e7867"))),
                        Map.of("executionMeans", "ROBOT", "fields", List.of(
                                Map.of("fieldCode", "yaw"),
                                Map.of("fieldCode", "pitch"),
                                Map.of("fieldCode", "roll"),
                                Map.of("fieldCode", "focal_length"),
                                Map.of("fieldCode", "action_duration")))));
        assertEquals(
                List.of("yaw", "pitch", "roll", "focal_length", "action_duration"),
                ActionParamSlotsSupport.fieldCodes(slots, "ROBOT", objectMapper));
    }
}
