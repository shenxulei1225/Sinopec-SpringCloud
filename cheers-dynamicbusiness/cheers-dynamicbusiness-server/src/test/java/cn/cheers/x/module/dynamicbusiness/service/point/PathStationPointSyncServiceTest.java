package cn.cheers.x.module.dynamicbusiness.service.point;

import cn.cheers.x.module.dynamicbusiness.api.point.dto.PathStationPointSyncReqDTO;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
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

    @Test
    void buildCoordinateValue_requiresBothLongitudeAndLatitude() {
        assertNull(PathStationPointSyncService.buildCoordinateValue(null, 39.1, null));
        assertNull(PathStationPointSyncService.buildCoordinateValue(117.0, null, null));
        assertEquals(
                Map.of("longitude", 117.0193, "latitude", 39.1279),
                PathStationPointSyncService.buildCoordinateValue(117.0193, 39.1279, null));
        assertEquals(
                Map.of("longitude", 117.0, "latitude", 39.1, "height", 12.5),
                PathStationPointSyncService.buildCoordinateValue(117.0, 39.1, 12.5));
    }

    @Test
    void buildCustomFields_writesCatalogCoordinateFromNodeGps() {
        PathStationPointSyncReqDTO.StationNodeItem item = PathStationPointSyncReqDTO.StationNodeItem.builder()
                .nodeId("n1")
                .longitude(117.0193)
                .latitude(39.1279)
                .build();
        Map<String, Object> custom = PathStationPointSyncService.buildCustomFields(
                45, "n1", "STATION", List.of("UAV"), item);
        assertEquals(
                Map.of("longitude", 117.0193, "latitude", 39.1279),
                custom.get(PathStationPointSyncService.COORDINATE_FIELD_SEED_CODE));
    }
}
