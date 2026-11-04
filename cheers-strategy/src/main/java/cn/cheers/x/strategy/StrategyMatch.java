package cn.cheers.x.strategy;

/**
 * 一条策略自己的条件算法。
 * 有一条成立：勾中的条件里，事实里出现任意一条即成立。
 * 全部成立：勾中的条件都要出现在事实里。
 */
public enum StrategyMatch {
    ANY,
    ALL
}
