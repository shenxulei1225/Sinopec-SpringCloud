package cn.cheers.x.module.dynamicbusiness.dataprocess;

import cn.cheers.x.module.dynamicbusiness.service.strategy.PointProcessingRule;

/**
 * 用一条规则和上一次的值，决定这一次读数要做什么。
 * 当前读数已经越过限值就开告警。不要求先有一条未越过的读数。
 * 告警已经开着时，当前读数回到恢复线就消警。不要求内存里还留着上一次越限的数。
 * 状态点仍要看出变化，没有上一次读数时不推状态。
 */
final class PointSignalDecider {

    enum Outcome {
        NONE,
        RAISE,
        ELIMINATE,
        STATUS
    }

    private PointSignalDecider() {
    }

    static Outcome decide(PointProcessingRule rule, Double previous, double current) {
        if (PointProcessingRule.STATUS.equals(rule.getSignalKind())) {
            if (previous == null || Double.compare(previous, current) == 0) {
                return Outcome.NONE;
            }
            return Outcome.STATUS;
        }
        if (rule.getThreshold() == null) {
            return Outcome.NONE;
        }
        if (PointProcessingRule.ALARM_FLAG.equals(rule.getSignalKind())) {
            boolean nowOn = Double.compare(current, 1.0d) == 0;
            if (previous == null) {
                return nowOn ? Outcome.RAISE : Outcome.NONE;
            }
            boolean wasOn = Double.compare(previous, 1.0d) == 0;
            if (!wasOn && nowOn) {
                return Outcome.RAISE;
            }
            if (wasOn && !nowOn) {
                return Outcome.ELIMINATE;
            }
            return Outcome.NONE;
        }
        if (previous == null) {
            if (hits(rule, current)) {
                return Outcome.RAISE;
            }
            return onRecoverySide(rule, current) ? Outcome.ELIMINATE : Outcome.NONE;
        }
        if (recovered(rule, previous, current)) {
            return Outcome.ELIMINATE;
        }
        boolean wasHit = hits(rule, previous);
        boolean nowHit = hits(rule, current);
        if (!wasHit && nowHit) {
            return Outcome.RAISE;
        }
        return Outcome.NONE;
    }

    /** 高于的恢复是落到恢复线及以下。低于的恢复是升到恢复线及以上。没写恢复线就不消警。 */
    private static boolean recovered(PointProcessingRule rule, double previous, double current) {
        return onRecoverySide(rule, current) && !onRecoverySide(rule, previous);
    }

    private static boolean onRecoverySide(PointProcessingRule rule, double value) {
        Double recovery = rule.getRecovery();
        String op = rule.getCompareOp();
        if (recovery == null || op == null) {
            return false;
        }
        return switch (op) {
            case "GT", "GTE" -> value <= recovery;
            case "LT", "LTE" -> value >= recovery;
            default -> false;
        };
    }

    private static boolean hits(PointProcessingRule rule, double value) {
        double threshold = rule.getThreshold();
        String op = rule.getCompareOp();
        if (op == null) {
            return false;
        }
        return switch (op) {
            case "EQ" -> Double.compare(value, threshold) == 0;
            case "NEQ" -> Double.compare(value, threshold) != 0;
            case "GT" -> value > threshold;
            case "GTE" -> value >= threshold;
            case "LT" -> value < threshold;
            case "LTE" -> value <= threshold;
            default -> false;
        };
    }
}
