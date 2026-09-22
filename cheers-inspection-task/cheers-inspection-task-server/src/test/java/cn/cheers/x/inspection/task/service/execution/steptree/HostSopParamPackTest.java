package cn.cheers.x.inspection.task.service.execution.steptree;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HostSopParamPackTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void paramsFor_readsRobotBagNotEmptyUavEntry() {
        HostSopParamPack pack = HostSopParamPack.parse(
                Map.of("version", 1, "entries", List.of(
                        Map.of("subjectId", 2354, "dimensionValue", "UAV", "paramsByNode", Map.of()),
                        Map.of("subjectId", 2354, "dimensionValue", "ROBOT", "paramsByNode", Map.of(
                                "n2", Map.of("yaw", 30, "pitch", -15))))),
                objectMapper);
        assertEquals(Map.of("yaw", 30, "pitch", -15), pack.paramsFor(2354L, "n2", "act-robot-aim", "ROBOT"));
        assertTrue(pack.paramsFor(2354L, "n2", "act-robot-aim", "UAV").isEmpty());
    }

    @Test
    void firstLocationRef_readsSelectLocationObject() {
        HostSopParamPack pack = HostSopParamPack.parse(
                Map.of("version", 1, "entries", List.of(
                        Map.of("subjectId", 2354, "dimensionValue", "ROBOT", "paramsByNode", Map.of(
                                "n1", Map.of(
                                        "F-e49f76bdca3e4e1393e8e2f1cc0e7867",
                                        Map.of("code", "tank-north", "name", "北罐区停靠点")))))),
                objectMapper);
        assertEquals("tank-north", pack.firstLocationRef(2354L, "ROBOT"));
    }
}
