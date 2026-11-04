package cn.cheers.x.strategy;

import java.util.List;

/**
 * 一条已经发布、可供判定的策略。
 * 条件编号和动作编号都是来源目录里的记录编号。本模块不解释它们是什么业务。
 */
public record PublishedStrategy(
        long id,
        String name,
        boolean enabled,
        int order,
        StrategyMatch match,
        List<String> conditionIds,
        String actionMode,
        List<String> actionIds
) {
}
