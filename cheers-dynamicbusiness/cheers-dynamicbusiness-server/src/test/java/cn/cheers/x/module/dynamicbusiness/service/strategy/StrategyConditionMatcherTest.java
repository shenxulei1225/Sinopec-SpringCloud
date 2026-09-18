package cn.cheers.x.module.dynamicbusiness.service.strategy;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.api.strategy.dto.StrategyTriggerEventDTO;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StrategyConditionMatcherTest {

    @Test
    void hasExecutionRecord_requiresId() {
        StrategyTriggerEventDTO event = new StrategyTriggerEventDTO();
        String json = "{\"all\":[{\"type\":\"HAS_EXECUTION_RECORD\"}]}";
        assertFalse(StrategyConditionMatcher.matches(json, event));
        event.setExecutionRecordId(8L);
        assertTrue(StrategyConditionMatcher.matches(json, event));
    }

    @Test
    void fieldCompare_gt() {
        StrategyTriggerEventDTO event = new StrategyTriggerEventDTO();
        event.setFields(Map.of("pressure", 80));
        String json = "{\"all\":[{\"type\":\"FIELD_COMPARE\",\"field\":\"pressure\",\"op\":\"GT\",\"value\":\"50\"}]}";
        assertTrue(StrategyConditionMatcher.matches(json, event));
        event.setFields(Map.of("pressure", 50));
        assertFalse(StrategyConditionMatcher.matches(json, event));
    }

    @Test
    void emptyCondition_failsVisible() {
        assertThrows(ServiceException.class,
                () -> StrategyConditionMatcher.matches("{}", new StrategyTriggerEventDTO()));
    }

    @Test
    void always_matches() {
        assertTrue(StrategyConditionMatcher.matches(
                "{\"all\":[{\"type\":\"ALWAYS\"}]}", new StrategyTriggerEventDTO()));
    }

    @Test
    void hasStepUpdates_requiresNonEmpty() {
        StrategyTriggerEventDTO event = new StrategyTriggerEventDTO();
        String json = "{\"all\":[{\"type\":\"HAS_STEP_UPDATES\"}]}";
        assertFalse(StrategyConditionMatcher.matches(json, event));
        event.setStepUpdates(List.of(Map.of("stepCode", "seq-1", "status", "completed")));
        assertTrue(StrategyConditionMatcher.matches(json, event));
    }

    @Test
    void hasExecutionStatus_requiresText() {
        StrategyTriggerEventDTO event = new StrategyTriggerEventDTO();
        String json = "{\"all\":[{\"type\":\"HAS_EXECUTION_STATUS\"}]}";
        assertFalse(StrategyConditionMatcher.matches(json, event));
        event.setExecutionStatus("in_progress");
        assertTrue(StrategyConditionMatcher.matches(json, event));
    }

    @Test
    void unknownType_failsVisible() {
        assertThrows(ServiceException.class,
                () -> StrategyConditionMatcher.matches(
                        "{\"all\":[{\"type\":\"SCRIPT\"}]}", new StrategyTriggerEventDTO()));
    }
}
