package cn.cheers.x.module.dynamicbusiness.service.action;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ActionParamSlotsParserTest {

    @Test
    void parsesPlatformDefinedSourceGroupsByExecutionMeans() {
        Map<String, Object> raw = Map.of(
                "version", 2,
                "methods", List.of(Map.of(
                        "executionMeans", "UAV",
                        "fields", List.of(
                                Map.of("fieldCode", "photoMode", "valueSource", "USER"),
                                Map.of("fieldCode", "targetX", "valueSource", "INTERACTION"),
                                Map.of(
                                        "fieldCode", "actionUUID",
                                        "valueSource", "SYSTEM",
                                        "generator", "UUID_V4")
                        )
                ))
        );

        ActionParamSlotsParser.Snapshot snapshot = ActionParamSlotsParser.parse(raw);

        assertEquals(
                List.of("USER", "INTERACTION", "SYSTEM"),
                snapshot.paramSourcesByMeans().get("UAV").stream()
                        .map(ActionParamSlotsParser.SourceField::sourceKind)
                        .toList());
        assertEquals(
                "UUID_V4",
                snapshot.paramSourcesByMeans().get("UAV").get(2).generator());
    }

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
    void twoUavModels_mergeFieldCodesForTheMeans() {
        Map<String, Object> raw = Map.of(
                "version", 2,
                "methods", List.of(
                        Map.of(
                                "executionMeans", "UAV",
                                "equipmentModelId", 12,
                                "fields", List.of(Map.of("fieldCode", "gimbalPitchRotateAngle"))),
                        Map.of(
                                "executionMeans", "UAV",
                                "equipmentModelId", 34,
                                "fields", List.of(Map.of("fieldCode", "angleLevel")))
                ));
        ActionParamSlotsParser.Snapshot snapshot = ActionParamSlotsParser.parse(raw);
        assertEquals(
                List.of("gimbalPitchRotateAngle", "angleLevel"),
                snapshot.paramSlotsByMeans().get("UAV"));
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

    @Test
    void unknownSourceGroup_doesNotBecomeRuntimeContract() {
        ActionParamSlotsParser.Snapshot snapshot = ActionParamSlotsParser.parse(
                Map.of("fields", List.of(Map.of(
                        "fieldCode", "photoMode",
                        "valueSource", "DEVICE_GUESS"))));

        assertEquals("USER", snapshot.paramSources().get(0).sourceKind());
    }
}
