package cn.cheers.x.module.dynamicbusiness.dataprocess;

import cn.cheers.x.module.dynamicbusiness.service.strategy.PointProcessingRule;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PointSignalDeciderTest {

    @Test
    void overLimitWithoutPreviousRaises() {
        assertEquals(PointSignalDecider.Outcome.RAISE, PointSignalDecider.decide(co2(), null, 1400d));
    }

    @Test
    void recoveryWithoutPreviousEliminatesOpenAlarm() {
        assertEquals(PointSignalDecider.Outcome.ELIMINATE, PointSignalDecider.decide(co2(), null, 800d));
    }

    @Test
    void stayOverDoesNotRaiseAgain() {
        assertEquals(PointSignalDecider.Outcome.NONE, PointSignalDecider.decide(co2(), 1400d, 1500d));
    }

    @Test
    void crossingRecoveryEliminates() {
        assertEquals(PointSignalDecider.Outcome.ELIMINATE, PointSignalDecider.decide(co2(), 1400d, 800d));
    }

    private static PointProcessingRule co2() {
        return new PointProcessingRule(9L, "F-co2", PointProcessingRule.THRESHOLD, "GT", 1000d,
                1L, "异常告警", null, null, 2535L, 1000d, 9L);
    }
}
