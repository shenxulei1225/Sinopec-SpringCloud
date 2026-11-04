package cn.cheers.x.strategy;

/**
 * 一个域公布的合并办法。
 * 多条策略同时成立时，全部返回，或按顺序只取第一条。
 */
public enum StrategyCombine {
    ALL,
    FIRST
}
