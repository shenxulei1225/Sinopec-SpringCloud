package cn.cheers.x.strategy;

import java.util.Set;

/**
 * 调用方这次送来的事实。
 * 内容是条件来源里已经出现的记录编号。没有送来的编号视为这次不成立。
 */
public record StrategyFact(Set<String> presentConditionIds) {
}
