package cn.cheers.x.inspection.task.service.execution.steptree;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class HostSopParamPackModelSliceTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void readsWholeApplicableModelSlice() {
        HostSopParamPack pack = HostSopParamPack.parse(Map.of(
                "entries", java.util.List.of(Map.of(
                        "subjectId", 10,
                        "dimensionValue", "UAV",
                        "paramsByNode", Map.of(
                                "n3", Map.of(
                                        "location_ref", "tank-top",
                                        "byApplicableModel", Map.of(
                                                "88", Map.of(
                                                        "gimbalPitchRotateAngle", -10,
                                                        "gimbalYawRotateAngle", 20,
                                                        "focalLength", 30))))))),
                objectMapper);

        Map<String, Object> params = pack.paramsFor(10L, "n3", "orientedShoot", "UAV", 88L);

        assertEquals(-10, params.get("gimbalPitchRotateAngle"));
        assertEquals(20, params.get("gimbalYawRotateAngle"));
        assertEquals(30, params.get("focalLength"));
        assertEquals("tank-top", params.get("location_ref"));
        assertFalse(params.containsKey("byApplicableModel"));
    }
}
