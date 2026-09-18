package cn.cheers.x.inspection.task.service.task;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("PatrolPlannedRouteSupport")
class PatrolPlannedRouteSupportTest {

    @Test
    @DisplayName("非空 stopIds 视为已确认路线")
    void hasConfirmedRoute_withStopIds() {
        Map<String, Object> planned = new LinkedHashMap<>();
        planned.put("stopIds", List.of("sta-1", "sta-2"));
        assertTrue(PatrolPlannedRouteSupport.hasConfirmedRoute(planned));
    }

    @Test
    @DisplayName("空对象或无 stopIds 不算已确认")
    void hasConfirmedRoute_empty() {
        assertFalse(PatrolPlannedRouteSupport.hasConfirmedRoute(null));
        assertFalse(PatrolPlannedRouteSupport.hasConfirmedRoute(Map.of()));
        assertFalse(PatrolPlannedRouteSupport.hasConfirmedRoute(Map.of("stopIds", List.of())));
    }

    @Test
    @DisplayName("可从 plannedRoute 距离估算时长")
    void resolveDurationMinutes_fromDistance() {
        Map<String, Object> planned = new LinkedHashMap<>();
        planned.put("totalDistanceMeters", 80L);
        assertEquals(2, PatrolPlannedRouteSupport.resolveDurationMinutes(planned, "ROBOT"));
    }
}
