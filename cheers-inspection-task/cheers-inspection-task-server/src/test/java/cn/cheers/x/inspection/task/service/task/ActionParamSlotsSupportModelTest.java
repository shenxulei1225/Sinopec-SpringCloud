package cn.cheers.x.inspection.task.service.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ActionParamSlotsSupportModelTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void modelRow_doesNotBorrowAnotherModel() {
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

        assertEquals(
                List.of("gimbalPitchRotateAngle"),
                ActionParamSlotsSupport.fieldCodes(raw, "UAV", 12L, objectMapper));
        assertEquals(
                List.of("angleLevel"),
                ActionParamSlotsSupport.fieldCodes(raw, "UAV", 34L, objectMapper));
        assertEquals(List.of(), ActionParamSlotsSupport.fieldCodes(raw, "UAV", 99L, objectMapper));
    }

    @Test
    void soleModelRow_usedWhenExecutorModelDoesNotMatch() {
        Map<String, Object> raw = Map.of(
                "version", 2,
                "methods", List.of(
                        Map.of(
                                "executionMeans", "ROBOT",
                                "applicableModelIds", List.of("2580")),
                        Map.of(
                                "executionMeans", "ROBOT",
                                "equipmentModelId", 2580,
                                "fields", List.of(
                                        Map.of("fieldCode", "yaw", "defaultValue", 0),
                                        Map.of("fieldCode", "shot_count", "defaultValue", 3)))));

        assertEquals(
                List.of("yaw", "shot_count"),
                ActionParamSlotsSupport.fieldCodes(raw, "ROBOT", 1773L, objectMapper));
        assertEquals(0, ActionParamSlotsSupport.defaultValues(raw, "ROBOT", 1773L, objectMapper).get("yaw"));
        assertEquals(3, ActionParamSlotsSupport.defaultValues(raw, "ROBOT", 1773L, objectMapper).get("shot_count"));
    }

    @Test
    void unscopedRow_stillUsedWhenNoModelSplit() {
        Map<String, Object> raw = Map.of(
                "methods", List.of(Map.of(
                        "executionMeans", "UAV",
                        "fields", List.of(Map.of("fieldCode", "yaw_angle")))));

        assertEquals(
                List.of("yaw_angle"),
                ActionParamSlotsSupport.fieldCodes(raw, "UAV", 12L, objectMapper));
    }

    @Test
    void legacySharedCopies_readOneRowRegardlessOfExecutorModel() {
        Map<String, Object> raw = Map.of(
                "version", 2,
                "methods", List.of(
                        Map.of(
                                "executionMeans", "UAV",
                                "paramsSharedAcrossModels", true,
                                "fields", List.of()),
                        Map.of(
                                "executionMeans", "UAV",
                                "equipmentModelId", 1895,
                                "fields", List.of(Map.of(
                                        "fieldCode", "action_duration",
                                        "defaultValue", 5))),
                        Map.of(
                                "executionMeans", "UAV",
                                "equipmentModelId", 1896,
                                "fields", List.of(Map.of(
                                        "fieldCode", "action_duration",
                                        "defaultValue", 5)))));

        assertEquals(
                List.of("action_duration"),
                ActionParamSlotsSupport.fieldCodes(raw, "UAV", 9999L, objectMapper));
        assertEquals(
                new BigDecimal("5"),
                ActionDurationParamSupport.secondsFromSlots(raw, "UAV", 9999L, objectMapper).orElseThrow());
    }

    @Test
    void explicitNotPerModel_readsUnscopedEvenWhenModelRowsExist() {
        Map<String, Object> raw = Map.of(
                "version", 2,
                "methods", List.of(
                        Map.of(
                                "executionMeans", "UAV",
                                "paramsPerEquipmentModel", false,
                                "fields", List.of(Map.of("fieldCode", "action_duration"))),
                        Map.of(
                                "executionMeans", "UAV",
                                "equipmentModelId", 12,
                                "fields", List.of(Map.of("fieldCode", "other_slot")))));

        assertEquals(
                List.of("action_duration"),
                ActionParamSlotsSupport.fieldCodes(raw, "UAV", 12L, objectMapper));
    }
}
