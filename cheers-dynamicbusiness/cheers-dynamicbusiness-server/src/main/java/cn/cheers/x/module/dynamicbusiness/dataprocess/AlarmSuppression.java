package cn.cheers.x.module.dynamicbusiness.dataprocess;

import java.util.Set;

/**
 * 一条按设计抑制。先成立的规则在同一台设备上还开着时，被压住的规则先不报。
 */
public record AlarmSuppression(long leadRuleId, Set<Long> heldRuleIds) {
}
