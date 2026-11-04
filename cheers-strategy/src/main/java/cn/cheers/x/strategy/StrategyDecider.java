package cn.cheers.x.strategy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 策略判定。
 *
 * 管什么：按一个域已经发布的策略，对照这次事实，返回成立的策略和勾选的动作。
 * 不管什么：策略的保存、来源目录里有哪些记录、动作怎么执行。
 * 禁止：按告警、巡检或任何业务名分支。缺发布内容就拒绝，不编一套默认策略。
 */
public final class StrategyDecider {

    private StrategyDecider() {
    }

    public static StrategyDecision decide(StrategyPublication publication, StrategyFact fact) {
        if (publication == null || publication.domainCode() == null || publication.domainCode().isBlank()) {
            throw new IllegalArgumentException("没有已发布的域");
        }
        if (publication.combine() == null) {
            throw new IllegalArgumentException("这个域没有公布合并办法");
        }
        if (fact == null || fact.presentConditionIds() == null) {
            throw new IllegalArgumentException("没有这次的事实");
        }
        Set<String> allowed = new LinkedHashSet<>();
        if (publication.allowedActionModes() != null) {
            for (String mode : publication.allowedActionModes()) {
                if (mode != null && !mode.isBlank()) {
                    allowed.add(mode.trim());
                }
            }
        }
        Set<String> present = fact.presentConditionIds();
        List<PublishedStrategy> ordered = new ArrayList<>(
                publication.strategies() == null ? List.of() : publication.strategies());
        ordered.sort(Comparator.comparingInt(PublishedStrategy::order).thenComparingLong(PublishedStrategy::id));

        List<StrategyDecision.MatchedStrategy> matched = new ArrayList<>();
        List<StrategyDecision.SkippedStrategy> skipped = new ArrayList<>();
        for (PublishedStrategy strategy : ordered) {
            if (strategy == null || !strategy.enabled()) {
                continue;
            }
            String problem = problemOf(strategy, allowed);
            if (problem != null) {
                skipped.add(new StrategyDecision.SkippedStrategy(strategy.id(), strategy.name(), problem));
                continue;
            }
            if (!holds(strategy, present)) {
                continue;
            }
            matched.add(new StrategyDecision.MatchedStrategy(
                    strategy.id(),
                    strategy.name(),
                    strategy.actionMode().trim(),
                    List.copyOf(strategy.actionIds())));
        }
        if (publication.combine() == StrategyCombine.FIRST && matched.size() > 1) {
            matched = new ArrayList<>(matched.subList(0, 1));
        }
        return new StrategyDecision(List.copyOf(matched), List.copyOf(skipped));
    }

    private static String problemOf(PublishedStrategy strategy, Set<String> allowed) {
        if (strategy.match() == null) {
            return "没有条件算法";
        }
        if (strategy.conditionIds() == null || strategy.conditionIds().isEmpty()) {
            return "没有条件";
        }
        if (strategy.actionMode() == null || strategy.actionMode().isBlank()) {
            return "没有动作做法";
        }
        if (allowed.isEmpty()) {
            return "这个域没有公布动作做法";
        }
        if (!allowed.contains(strategy.actionMode().trim())) {
            return "动作做法不在这个域允许的范围内";
        }
        if (strategy.actionIds() == null || strategy.actionIds().isEmpty()) {
            return "没有动作";
        }
        return null;
    }

    private static boolean holds(PublishedStrategy strategy, Set<String> present) {
        if (strategy.match() == StrategyMatch.ALL) {
            for (String id : strategy.conditionIds()) {
                if (id == null || !present.contains(id)) {
                    return false;
                }
            }
            return true;
        }
        for (String id : strategy.conditionIds()) {
            if (id != null && present.contains(id)) {
                return true;
            }
        }
        return false;
    }
}
