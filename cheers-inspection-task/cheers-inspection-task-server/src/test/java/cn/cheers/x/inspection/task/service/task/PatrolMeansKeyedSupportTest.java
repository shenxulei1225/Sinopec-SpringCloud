package cn.cheers.x.inspection.task.service.task;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PatrolMeansKeyedSupportTest {

    @Test
    void routeSnapshotIsNotMeansKeyed() {
        assertFalse(PatrolMeansKeyedSupport.isMeansKeyedMap(
                Map.of("stopIds", List.of("a"), "networkRef", "net-1")));
    }

    @Test
    void meansMapReadsOnlyThatMeans() {
        Map<String, Object> stored = Map.of(
                "ROBOT", "gate-1",
                "UAV", "pad-a");
        assertTrue(PatrolMeansKeyedSupport.isMeansKeyedMap(stored));
        assertEquals("pad-a", PatrolMeansKeyedSupport.readSlice(stored, "UAV"));
        assertNull(PatrolMeansKeyedSupport.readSlice(stored, "MANUAL"));
    }

    @Test
    void legacyScalarBelongsToOwnerWhenWritingAnotherMeans() {
        Map<String, Object> next = PatrolMeansKeyedSupport.putSlice("gate-1", "ROBOT", "UAV", "pad-a");
        assertEquals("gate-1", next.get("ROBOT"));
        assertEquals("pad-a", next.get("UAV"));
    }

    @Test
    void primaryMeansPicksHighestUnlocked() {
        assertEquals("ROBOT", PatrolMeansKeyedSupport.primaryMeansOf(
                Map.of("ROBOT", 3, "UAV", 0)));
        assertNull(PatrolMeansKeyedSupport.primaryMeansOf(3));
    }

    @Test
    void legacyScalarDoesNotLeakToAnotherMeans() {
        assertEquals("gate-1", PatrolMeansKeyedSupport.readSlice("gate-1", "ROBOT", "ROBOT"));
        assertNull(PatrolMeansKeyedSupport.readSlice("gate-1", "UAV", "ROBOT"));
    }

    @Test
    void emptyValueRemovesOnlyThatMeans() {
        Map<String, Object> stored = PatrolMeansKeyedSupport.putSlice(
                Map.of("ROBOT", "gate-1", "UAV", "pad-a"), "ROBOT", "UAV", "");
        assertEquals("gate-1", stored.get("ROBOT"));
        assertFalse(stored.containsKey("UAV"));
    }
}
