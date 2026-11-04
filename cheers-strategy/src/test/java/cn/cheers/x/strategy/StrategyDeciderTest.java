package cn.cheers.x.strategy;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StrategyDeciderTest {

    @Test
    void anyMatchesOnePresentCondition() {
        StrategyDecision decision = StrategyDecider.decide(publication(StrategyCombine.ALL, List.of(
                strategy(1, true, 1, StrategyMatch.ANY, List.of("a", "b"), "通知", List.of("x"))
        )), new StrategyFact(Set.of("b")));
        assertEquals(1, decision.matched().size());
        assertEquals(List.of("x"), decision.matched().get(0).actionIds());
        assertTrue(decision.skipped().isEmpty());
    }

    @Test
    void allRequiresEveryCondition() {
        StrategyDecision decision = StrategyDecider.decide(publication(StrategyCombine.ALL, List.of(
                strategy(1, true, 1, StrategyMatch.ALL, List.of("a", "b"), "通知", List.of("x"))
        )), new StrategyFact(Set.of("a")));
        assertTrue(decision.matched().isEmpty());
    }

    @Test
    void firstKeepsOnlyTheEarliestMatch() {
        StrategyDecision decision = StrategyDecider.decide(publication(StrategyCombine.FIRST, List.of(
                strategy(2, true, 2, StrategyMatch.ANY, List.of("a"), "通知", List.of("later")),
                strategy(1, true, 1, StrategyMatch.ANY, List.of("a"), "通知", List.of("earlier"))
        )), new StrategyFact(Set.of("a")));
        assertEquals(List.of(1L), decision.matched().stream().map(StrategyDecision.MatchedStrategy::id).toList());
    }

    @Test
    void disabledStrategyIsIgnored() {
        StrategyDecision decision = StrategyDecider.decide(publication(StrategyCombine.ALL, List.of(
                strategy(1, false, 1, StrategyMatch.ANY, List.of("a"), "通知", List.of("x"))
        )), new StrategyFact(Set.of("a")));
        assertTrue(decision.matched().isEmpty());
        assertTrue(decision.skipped().isEmpty());
    }

    @Test
    void actionModeOutsideTheDomainIsSkipped() {
        StrategyDecision decision = StrategyDecider.decide(publication(StrategyCombine.ALL, List.of(
                strategy(1, true, 1, StrategyMatch.ANY, List.of("a"), "停用", List.of("x"))
        )), new StrategyFact(Set.of("a")));
        assertTrue(decision.matched().isEmpty());
        assertEquals("动作做法不在这个域允许的范围内", decision.skipped().get(0).reason());
    }

    @Test
    void missingPublicationIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> StrategyDecider.decide(null, new StrategyFact(Set.of("a"))));
    }

    private static StrategyPublication publication(StrategyCombine combine, List<PublishedStrategy> strategies) {
        return new StrategyPublication("alarm", combine, List.of("通知", "执行"), strategies);
    }

    private static PublishedStrategy strategy(
            long id, boolean enabled, int order, StrategyMatch match,
            List<String> conditions, String mode, List<String> actions) {
        return new PublishedStrategy(id, "s" + id, enabled, order, match, conditions, mode, actions);
    }
}
