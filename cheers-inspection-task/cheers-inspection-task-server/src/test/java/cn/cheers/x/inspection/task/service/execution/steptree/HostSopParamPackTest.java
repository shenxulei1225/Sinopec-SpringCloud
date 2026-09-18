package cn.cheers.x.inspection.task.service.execution.steptree;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HostSopParamPackTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void paramsFor_matchesInspectionItemThenNodeKey() {
        HostSopParamPack pack = HostSopParamPack.parse(Map.of(
                "version", 1,
                "entries", List.of(
                        Map.of(
                                "subjectType", "inspection_item",
                                "subjectId", 7,
                                "paramsByNode", Map.of(
                                        "n-move", Map.of("location_ref", "P1"),
                                        "act-photo", Map.of("shot_count", 2)
                                )
                        )
                )
        ), mapper);

        assertEquals("P1", pack.paramsFor(7L, "n-move", "act-arrive").get("location_ref"));
        assertEquals(2, pack.paramsFor(7L, "n-other", "act-photo").get("shot_count"));
    }

    @Test
    void paramsFor_miss_isEmpty() {
        HostSopParamPack pack = HostSopParamPack.parse(Map.of("version", 1, "entries", List.of()), mapper);
        assertTrue(pack.paramsFor(7L, "n-move", "act-arrive").isEmpty());
    }

    @Test
    void paramsFor_doesNotStealOtherItem() {
        HostSopParamPack pack = HostSopParamPack.parse(Map.of(
                "version", 1,
                "entries", List.of(Map.of(
                        "subjectId", 7,
                        "paramsByNode", Map.of("n-photo", Map.of("shot_count", 3))
                ))
        ), mapper);

        assertTrue(pack.paramsFor(8L, "n-photo", "act-photo").isEmpty());
    }
}
