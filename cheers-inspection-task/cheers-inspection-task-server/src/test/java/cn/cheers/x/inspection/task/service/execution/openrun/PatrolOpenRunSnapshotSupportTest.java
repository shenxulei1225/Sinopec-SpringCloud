package cn.cheers.x.inspection.task.service.execution.openrun;

import cn.cheers.x.device.protocolgateway.api.mission.DispatchAction;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PatrolOpenRunSnapshotSupportTest {

    @Test
    void parseDispatchActions_readsFrozenActions() {
        Map<String, Object> snapshot = Map.of(
                "dispatchActions", List.of(Map.of("actionId", 11L, "params", Map.of("angle", 5))));
        List<DispatchAction> actions = PatrolOpenRunSnapshotSupport.parseDispatchActions(snapshot);
        assertEquals(1, actions.size());
        assertEquals(11L, actions.get(0).actionId());
        assertEquals(5, actions.get(0).params().get("angle"));
    }

    @Test
    void parseDispatchActions_missingActions_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> PatrolOpenRunSnapshotSupport.parseDispatchActions(new LinkedHashMap<>()));
    }
}
