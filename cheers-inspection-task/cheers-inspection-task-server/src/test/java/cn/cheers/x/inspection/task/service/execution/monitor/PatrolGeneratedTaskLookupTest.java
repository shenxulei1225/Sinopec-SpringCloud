package cn.cheers.x.inspection.task.service.execution.monitor;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PatrolGeneratedTaskLookupTest {

    @Test
    void keepsGeneratedPackageFields() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> snapshot = Map.of(
                "dispatchDownlinkPieces",
                List.of(Map.of(
                        "kind", "package",
                        "templateId", "patrol-task-910101",
                        "packagesJson",
                        "[{\"opcode\":200102,\"sequence\":1,\"request\":{\"lat\":\"\",\"heading\":\"\",\"pointId\":\"P1\"}}]")));

        PatrolGeneratedTaskLookup.StoredCommandPackage stored =
                PatrolGeneratedTaskLookup.storedCommandPackage(snapshot, mapper);

        assertEquals("patrol-task-910101", stored.templateId());
        assertTrue(stored.packagesJson().contains("heading"));
        assertTrue(stored.packagesJson().contains("P1"));
    }

    @Test
    void oldRecordWithoutPackageStaysEmpty() {
        assertNull(PatrolGeneratedTaskLookup.storedCommandPackage(Map.of(), new ObjectMapper()));
    }
}
