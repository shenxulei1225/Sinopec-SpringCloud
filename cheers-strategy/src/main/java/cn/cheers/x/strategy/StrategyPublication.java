package cn.cheers.x.strategy;

import java.util.List;

/**
 * 一个域已经发布的词汇和策略。
 * 合并办法、允许的动作做法由这个域公布。判定不另编一套。
 */
public record StrategyPublication(
        String domainCode,
        StrategyCombine combine,
        List<String> allowedActionModes,
        List<PublishedStrategy> strategies
) {
}
