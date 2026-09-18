package cn.cheers.x.inspection.task.service.execution.steptree;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InspectionMethodSnapshotParserTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void actionsForMeans_readsOnlyThatMeans() {
        Map<String, Object> raw = Map.of(
                "version", 1,
                "methods", List.of(
                        Map.of("executionMeans", "MANUAL", "actionTree", List.of(
                                Map.of("actionId", 9, "name", "走到点"))),
                        Map.of("executionMeans", "ROBOT", "actionTree", List.of(
                                Map.of("refId", 11, "refCode", "act-photo", "title", "拍照")))
                )
        );

        List<TaskStepTreeGenerator.MethodAction> robot =
                InspectionMethodSnapshotParser.actionsForMeans(raw, "ROBOT", mapper);
        List<TaskStepTreeGenerator.MethodAction> camera =
                InspectionMethodSnapshotParser.actionsForMeans(raw, "FIXED_CAMERA", mapper);

        assertEquals(1, robot.size());
        assertEquals(11L, robot.get(0).refId());
        assertTrue(camera.isEmpty());
    }

    @Test
    void actionsForMeans_readsCurrentStepTreePackAndActionCode() {
        Map<String, Object> raw = Map.of(
                "version", 1,
                "methods", List.of(
                        Map.of("methodKey", "MANUAL", "stepTree", List.of(
                                Map.of("nodeKey", "n1", "actionId", "act-walk", "stepTitle", "走到点"))),
                        Map.of("methodKey", "ROBOT", "stepTree", List.of(
                                Map.of("nodeKey", "n2", "actionId", "act-photo", "stepTitle", "拍照",
                                        "nodeType", "STEP"),
                                Map.of("nodeKey", "g1", "actionId", "", "nodeType", "GROUP")))
                )
        );

        List<TaskStepTreeGenerator.MethodAction> robot =
                InspectionMethodSnapshotParser.actionsForMeans(raw, "ROBOT", mapper);

        assertEquals(1, robot.size());
        assertEquals("act-photo", robot.get(0).refCode());
        assertEquals("拍照", robot.get(0).title());
        assertTrue(InspectionMethodSnapshotParser.actionsForMeans(raw, "UAV", mapper).isEmpty());
    }
}
