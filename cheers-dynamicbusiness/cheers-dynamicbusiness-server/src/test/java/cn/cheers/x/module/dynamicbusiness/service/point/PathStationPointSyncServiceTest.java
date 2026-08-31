package cn.cheers.x.module.dynamicbusiness.service.point;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PathStationPointSyncServiceTest {

    @Test
    void buildPointCode_sanitizesNodeId() {
        assertEquals("PN-45-n_sta_001", PathStationPointSyncService.buildPointCode(45, "n_sta_001"));
        assertEquals("PN-1-a_b", PathStationPointSyncService.buildPointCode(1, "a/b"));
    }

    @Test
    void isSyncedPointCode_matchesFacilityPrefix() {
        assertTrue(PathStationPointSyncService.isSyncedPointCode(45, "PN-45-n1"));
        assertFalse(PathStationPointSyncService.isSyncedPointCode(45, "PN-12-n1"));
        assertFalse(PathStationPointSyncService.isSyncedPointCode(45, "OTHER"));
    }

    @Test
    void normalizePointKind_defaultsAndValidates() {
        assertEquals("STATION", PathStationPointSyncService.normalizePointKind(null));
        assertEquals("STATION", PathStationPointSyncService.normalizePointKind(""));
        assertEquals("TRAVERSAL", PathStationPointSyncService.normalizePointKind("traversal"));
        assertEquals("DOOR", PathStationPointSyncService.normalizePointKind("DOOR"));
    }

    @Test
    void normalizeMemberships_defaultsAndDedups() {
        assertEquals(
                List.of("HUMAN", "GROUND_ROBOT", "UAV"),
                PathStationPointSyncService.normalizeMemberships(null));
        assertEquals(
                List.of("UAV", "HUMAN"),
                PathStationPointSyncService.normalizeMemberships(List.of("uav", "HUMAN", "UAV")));
    }
}
