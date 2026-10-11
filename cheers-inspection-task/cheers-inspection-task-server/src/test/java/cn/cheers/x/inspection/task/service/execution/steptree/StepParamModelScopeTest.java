package cn.cheers.x.inspection.task.service.execution.steptree;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class StepParamModelScopeTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void usesUniqueMountedModelWhenExecutorModelIsNotApplicable() {
        Map<String, Object> slots = Map.of(
                "methods", List.of(Map.of(
                        "executionMeans", "UAV",
                        "paramsPerEquipmentModel", true,
                        "equipmentModelId", 88,
                        "fields", List.of(Map.of("fieldCode", "gimbalPitchRotateAngle")))));

        Long modelId = StepParamModelScope.resolve(slots, "UAV", 1867L, List.of(88L, 99L), objectMapper);

        assertEquals(88L, modelId);
    }

    @Test
    void leavesEmptyWhenMoreThanOneMountedModelMatches() {
        Map<String, Object> slots = Map.of(
                "methods", List.of(
                        Map.of("executionMeans", "UAV", "paramsPerEquipmentModel", true,
                                "equipmentModelId", 88, "fields", List.of(Map.of("fieldCode", "focalLength"))),
                        Map.of("executionMeans", "UAV", "paramsPerEquipmentModel", true,
                                "equipmentModelId", 99, "fields", List.of(Map.of("fieldCode", "focalLength")))));

        assertNull(StepParamModelScope.resolve(slots, "UAV", 1867L, List.of(88L, 99L), objectMapper));
    }
}
