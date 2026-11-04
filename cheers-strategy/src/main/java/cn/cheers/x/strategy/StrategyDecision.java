package cn.cheers.x.strategy;

import java.util.List;

/**
 * 一次判定的结果。
 * matched 是成立的策略和它们勾选的动作。skipped 是启用了但没法参与判定的策略。
 * 调用方执行动作。本结果不表示动作已经做完。
 */
public record StrategyDecision(
        List<MatchedStrategy> matched,
        List<SkippedStrategy> skipped
) {
    public record MatchedStrategy(long id, String name, String actionMode, List<String> actionIds) {
    }

    public record SkippedStrategy(long id, String name, String reason) {
    }
}
