package cn.cheers.x.module.dynamicbusiness.service.action;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ActionParamSlotsParserTest {

    @Test
    void methodsShape_keepsUavSlotsAndMergedList() {
        String json = """
                {
                  "version": 2,
                  "methods": [
                    {"executionMeans":"MANUAL","fields":[]},
                    {"executionMeans":"UAV","fields":[
                      {"fieldCode":"yaw_angle","required":false},
                      {"fieldCode":"pitch_angle","required":false},
                      {"fieldCode":"roll_angle","required":false}
                    ]}
                  ]
                }
                """;
        ActionParamSlotsParser.Snapshot snapshot = ActionParamSlotsParser.parse(json);
        assertEquals(List.of("yaw_angle", "pitch_angle", "roll_angle"), snapshot.paramSlots());
        assertEquals(List.of(), snapshot.paramSlotsByMeans().get("MANUAL"));
        assertEquals(List.of("yaw_angle", "pitch_angle", "roll_angle"), snapshot.paramSlotsByMeans().get("UAV"));
    }

    @Test
    void legacyTopLevelFields_stillWork() {
        ActionParamSlotsParser.Snapshot snapshot = ActionParamSlotsParser.parse(
                Map.of("version", 1, "fields", List.of(Map.of("fieldCode", "photo_count"))));
        assertEquals(List.of("photo_count"), snapshot.paramSlots());
        assertEquals(Map.of(), snapshot.paramSlotsByMeans());
    }

    @Test
    void missingMethodsFields_mustNotInventSlots() {
        ActionParamSlotsParser.Snapshot snapshot = ActionParamSlotsParser.parse(Map.of("version", 2));
        assertEquals(List.of(), snapshot.paramSlots());
        assertEquals(Map.of(), snapshot.paramSlotsByMeans());
    }
}
