package cn.cheers.x.module.dynamicbusiness.dataprocess;

import java.util.Set;

/**
 * 一条运行状态策略。状态点的最近一次正常读数满足比较时，对选中的规则生效。
 * 没有读数时调用方不套这条，仍按规则本身判断。
 */
public record AlarmStateSetting(long deviceModelId, String pointCode, String op, double value, String action,
                                Set<Long> ruleIds, String high, String low, String highRecover, String lowRecover,
                                String level) {

    public boolean matches(double actual) {
        return switch (op) {
            case "GT" -> actual > value;
            case "GTE" -> actual >= value;
            case "LT" -> actual < value;
            case "LTE" -> actual <= value;
            case "NEQ" -> Double.compare(actual, value) != 0;
            case "EQ" -> Double.compare(actual, value) == 0;
            default -> false;
        };
    }
}
