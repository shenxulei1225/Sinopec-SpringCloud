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
    @DisplayName("非空 stopIds 视为已保存路线")
    void hasSavedRoute_withStopIds() {
        Map<String, Object> planned = new LinkedHashMap<>();
        planned.put("stopIds", List.of("sta-1", "sta-2"));
        assertTrue(PatrolPlannedRouteSupport.hasSavedRoute(planned));
    }

    @Test
    @DisplayName("空对象或无 stopIds 不算已保存路线")
    void hasSavedRoute_empty() {
        assertFalse(PatrolPlannedRouteSupport.hasSavedRoute(null));
        assertFalse(PatrolPlannedRouteSupport.hasSavedRoute(Map.of()));
        assertFalse(PatrolPlannedRouteSupport.hasSavedRoute(Map.of("stopIds", List.of())));
    }

    @Test
    @DisplayName("可从 plannedRoute 距离估算路径耗时")
    void resolveTravelMinutesOnly_fromDistance() {
        Map<String, Object> planned = new LinkedHashMap<>();
        planned.put("totalDistanceMeters", 80L);
        assertEquals(2, PatrolPlannedRouteSupport.resolveTravelMinutesOnly(planned, "ROBOT"));
    }
}
