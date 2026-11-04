package cn.cheers.x.module.dynamicbusiness.dataprocess;

import cn.cheers.x.module.dynamicbusiness.devicehistory.DevicePointHistoryRules;
import cn.cheers.x.module.dynamicbusiness.devicehistory.DevicePointHistoryService;
import cn.cheers.x.module.dynamicbusiness.devicehistory.DevicePointSample;
import cn.cheers.x.module.dynamicbusiness.devicehistory.DevicePointWrite;
import cn.cheers.x.module.dynamicbusiness.devicehistory.ParsedPointSample;
import cn.cheers.x.module.dynamicbusiness.devicehistory.PointSampleBatchReporter;
import cn.cheers.x.module.dynamicbusiness.service.strategy.PointProcessingRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 收下网关解析后的一批读数，自己按规则处理。
 * 整批写入历史。正常读数再按设备状态主题发给页面。
 * 规则和设备型号按版本放在内存里，版本没变不再读库。
 * 告警规则按设备型号判断。这台设备的型号和规则上的型号一致，才套这条。
 * 当前读数已经越过限值就开告警。不要求先有一条未越过的读数。
 * 告警策略先看系统条件成不成立。选了不执行时，这条不再新开。没有读到条件对应的点时，仍按规则判断。
 * 已经开着的仍可关掉。人搁置未到期的也不新开。
 * 告警位置用这条读数所在的区段。
 * 不订阅网关，也不把样本交回条件策略去跑。
 * 异常质量不发给页面，也不参加阈值判断。有位置时记一条告警。没有位置只记日志。
 */
@Service
public class PointDataProcessor implements PointSampleBatchReporter {

    private static final Logger log = LoggerFactory.getLogger(PointDataProcessor.class);

    private final DevicePointHistoryService history;
    private final PointRuleSource rules;
    private final AlarmSignalGateway alarms;
    private final AlarmBehaviorLookup behaviors;
    private final EquipmentStatusPublisher statusPublisher;
    private final Map<String, Double> lastValue = new ConcurrentHashMap<>();

    private volatile long seenVersion = -1L;
    private volatile RuleSnapshot snapshot = RuleSnapshot.empty();

    public PointDataProcessor(DevicePointHistoryService history, PointRuleSource rules,
                              AlarmSignalGateway alarms, AlarmBehaviorLookup behaviors,
                              EquipmentStatusPublisher statusPublisher) {
        this.history = history;
        this.rules = rules;
        this.alarms = alarms;
        this.behaviors = behaviors;
        this.statusPublisher = statusPublisher;
    }

    @Override
    public void accept(List<ParsedPointSample> samples) {
        if (samples == null || samples.isEmpty()) {
            return;
        }
        history.appendBatch(writesOf(samples));
        RuleSnapshot current = currentRules();
        for (ParsedPointSample sample : samples) {
            if (!DevicePointHistoryRules.QUALITY_NORMAL.equals(sample.quality())) {
                recordAbnormalQuality(sample);
                continue;
            }
            String key = sample.tenantId() + ":" + sample.deviceId() + ":" + sample.pointCode();
            Double previous = lastValue.get(key);
            for (PointProcessingRule rule : current.byField.getOrDefault(sample.pointCode(), List.of())) {
                if (PointProcessingRule.THRESHOLD.equals(rule.getSignalKind())
                        || PointProcessingRule.ALARM_FLAG.equals(rule.getSignalKind())) {
                    if (!matchesDevice(current, rule, sample.deviceId())) {
                        continue;
                    }
                }
                apply(current, sample, rule, previous);
            }
            lastValue.put(key, sample.engineeringValue());
            tellPage(sample);
        }
    }

    /** 正常读数才告诉页面。发送失败只记日志，不收回已经写入的历史。 */
    private void tellPage(ParsedPointSample sample) {
        try {
            statusPublisher.publish(
                    sample.deviceId(), sample.pointCode(), sample.engineeringValue(), sample.sampledAt());
        } catch (RuntimeException ex) {
            log.warn("设备 {} 的读数没有发给页面：{}", sample.deviceId(), ex.getMessage());
        }
    }

    private static List<DevicePointWrite> writesOf(List<ParsedPointSample> samples) {
        List<DevicePointWrite> writes = new ArrayList<>(samples.size());
        for (ParsedPointSample sample : samples) {
            writes.add(new DevicePointWrite(
                    sample.tenantId(),
                    sample.deviceId(),
                    sample.pointCode(),
                    sample.sampledAt(),
                    sample.engineeringValue(),
                    sample.quality()));
        }
        return writes;
    }

    private void apply(RuleSnapshot current, ParsedPointSample sample, PointProcessingRule rule, Double previous) {
        PointProcessingRule effective = adjusted(current, sample, rule);
        if (effective == null) {
            return;
        }
        PointSignalDecider.Outcome outcome = PointSignalDecider.decide(effective, previous, sample.engineeringValue());
        if (outcome == PointSignalDecider.Outcome.RAISE
                && (held(current, sample, effective) || policySkips(current, sample, effective))) {
            return;
        }
        try {
            switch (outcome) {
                case RAISE -> raise(sample, effective);
                case ELIMINATE -> alarms.eliminate(sample.tenantId(), sample.deviceId(), rule.getSignalKey());
                case STATUS -> {
                }
                case NONE -> {
                }
            }
        } catch (RuntimeException ex) {
            log.warn("点位 {} 规则 {} 没有处理完：{}", sample.bindingCode(), rule.getId(), ex.getMessage());
        }
    }

    /**
     * 状态点没有最近一次正常读数时，返回原来的规则。
     * 这一状态下选了不判断时返回空，调用方跳过这条。
     */
    private PointProcessingRule adjusted(RuleSnapshot current, ParsedPointSample sample, PointProcessingRule rule) {
        Long modelId = current.models.get(sample.deviceId());
        PointProcessingRule effective = rule;
        for (AlarmStateSetting setting : current.states) {
            if (modelId == null || !modelId.equals(setting.deviceModelId())) {
                continue;
            }
            if (!setting.ruleIds().contains(rule.getId())) {
                continue;
            }
            Double actual = stateValue(sample, setting.pointCode());
            if (actual == null || !setting.matches(actual)) {
                continue;
            }
            if ("SKIP".equals(setting.action())) {
                return null;
            }
            if ("LIMIT".equals(setting.action())) {
                effective = withLimit(effective, setting);
            }
            if ("LEVEL".equals(setting.action()) && setting.level() != null && !setting.level().isBlank()) {
                effective = withLevel(effective, setting.level());
            }
        }
        return effective;
    }

    /** 搁置未到期，或先成立的告警还开着，这条先不报。关闭和消除不受影响。 */
    private boolean held(RuleSnapshot current, ParsedPointSample sample, PointProcessingRule rule) {
        if (alarms.shelved(sample.tenantId(), sample.deviceId(), rule.getSignalKey())) {
            return true;
        }
        for (AlarmSuppression suppression : current.suppressions) {
            if (!suppression.heldRuleIds().contains(rule.getId())) {
                continue;
            }
            if (alarms.leadingOpen(sample.tenantId(), sample.deviceId(), suppression.leadRuleId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 设备告警策略选了不执行，并且条件已经成立。只挡住新开。
     * 适用型号指定了时，别的型号不用这条。型号上没有这句对应的点，这条也不用。
     * 没读到点时不当成成立。没选规则时，这台设备上正在判断的规则都不新开。
     */
    private boolean policySkips(RuleSnapshot current, ParsedPointSample sample, PointProcessingRule rule) {
        for (AlarmPolicy policy : current.policies) {
            if (policyMatches(current, sample, rule, policy)) {
                return true;
            }
        }
        return false;
    }

    private boolean policyMatches(RuleSnapshot current, ParsedPointSample sample, PointProcessingRule rule,
                                  AlarmPolicy policy) {
        if (!policy.allRules() && !policy.ruleIds().contains(rule.getId())) {
            return false;
        }
        Long modelId = current.models.get(sample.deviceId());
        if (!policy.modelIds().isEmpty() && (modelId == null || !policy.modelIds().contains(modelId))) {
            return false;
        }
        if (policy.clauses() == null || policy.clauses().isEmpty()) {
            return false;
        }
        int matched = 0;
        for (AlarmPolicy.Clause clause : policy.clauses()) {
            Boolean held = clauseHolds(sample, modelId, clause);
            if (held == null) {
                if (policy.allMustMatch()) {
                    return false;
                }
                continue;
            }
            if (policy.allMustMatch() && !held) {
                return false;
            }
            if (held) {
                matched++;
            }
        }
        return policy.allMustMatch() || matched > 0;
    }

    /** 没有这句对应的点，或还没读到这个点，返回空。空表示这条策略对这台设备不用。 */
    private Boolean clauseHolds(ParsedPointSample sample, Long modelId, AlarmPolicy.Clause clause) {
        if ("OPEN".equals(clause.kind())) {
            if (clause.leadRuleId() == null) {
                return null;
            }
            return alarms.leadingOpen(sample.tenantId(), sample.deviceId(), clause.leadRuleId());
        }
        if (modelId == null || clause.byModel() == null) {
            return null;
        }
        AlarmPolicy.Expect expect = clause.byModel().get(modelId);
        if (expect == null || expect.pointCode() == null) {
            return null;
        }
        Double actual = stateValue(sample, expect.pointCode());
        if (actual == null) {
            return null;
        }
        return Math.abs(actual - expect.expected()) < 1e-6;
    }

    private Double stateValue(ParsedPointSample sample, String pointCode) {
        if (pointCode.equals(sample.pointCode())) {
            return sample.engineeringValue();
        }
        Double cached = lastValue.get(sample.tenantId() + ":" + sample.deviceId() + ":" + pointCode);
        if (cached != null) {
            return cached;
        }
        try {
            List<DevicePointSample> rows = history.latest(sample.tenantId(), sample.deviceId());
            if (rows == null) {
                return null;
            }
            for (DevicePointSample row : rows) {
                if (pointCode.equals(row.getPointCode())
                        && DevicePointHistoryRules.QUALITY_NORMAL.equals(row.getQuality())) {
                    return row.getEngineeringValue();
                }
            }
        } catch (RuntimeException ex) {
            log.warn("设备 {} 的状态点 {} 没有读到：{}", sample.deviceId(), pointCode, ex.getMessage());
        }
        return null;
    }

    private static PointProcessingRule withLimit(PointProcessingRule rule, AlarmStateSetting setting) {
        boolean highSide = "GT".equals(rule.getCompareOp()) || "GTE".equals(rule.getCompareOp());
        String text = highSide ? setting.high() : setting.low();
        if (text == null || text.isBlank()) {
            return rule;
        }
        Double threshold = parseNumber(text);
        if (threshold == null) {
            return rule;
        }
        String recoveryText = highSide ? setting.highRecover() : setting.lowRecover();
        Double recovery = rule.getRecovery();
        if (recoveryText != null && !recoveryText.isBlank()) {
            Double parsed = parseNumber(recoveryText);
            if (parsed != null) {
                recovery = parsed;
            }
        }
        return copy(rule, threshold, recovery, rule.getAlarmLevel());
    }

    private static PointProcessingRule withLevel(PointProcessingRule rule, String level) {
        return copy(rule, rule.getThreshold(), rule.getRecovery(), level);
    }

    private static PointProcessingRule copy(PointProcessingRule rule, Double threshold, Double recovery, String level) {
        return new PointProcessingRule(rule.getId(), rule.getPointFieldCode(), rule.getSignalKind(),
                rule.getCompareOp(), threshold, rule.getAlarmTypeId(), level, rule.getFixedDeviceId(),
                rule.getFixedLocationId(), rule.getDeviceModelId(), recovery, rule.getSignalKey(),
                rule.getName(), rule.getPointName());
    }

    private static Double parseNumber(String text) {
        try {
            return Double.parseDouble(text.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private void raise(ParsedPointSample sample, PointProcessingRule rule) {
        Long locationId = sample.zoneId();
        if (locationId == null || rule.getAlarmTypeId() == null || rule.getAlarmLevel() == null) {
            log.warn("点位 {} 规则 {} 缺少告警类型、级别或位置，不记告警", sample.bindingCode(), rule.getId());
            return;
        }
        String alarmName = rule.getName() == null ? "" : rule.getName().trim();
        if (alarmName.isEmpty()) {
            log.warn("告警规则 {} 没有名称，不记告警", rule.getId());
            return;
        }
        String value = Double.toString(sample.engineeringValue());
        String threshold = rule.getThreshold() == null ? null : Double.toString(rule.getThreshold());
        alarms.raise(sample.tenantId(), rule.getAlarmTypeId(), rule.getAlarmLevel(), alarmName, content(sample, rule),
                sample.deviceId(), locationId, value, threshold, rule.getSignalKey(), sample.pointCode());
    }

    /** 质量不合格单独记账。没有分区时不记，避免造一条没有位置的告警。 */
    private void recordAbnormalQuality(ParsedPointSample sample) {
        if (sample.zoneId() == null) {
            log.warn("点位 {} 质量异常但没有位置，不记告警", sample.bindingCode());
            return;
        }
        try {
            alarms.recordAbnormalQuality(sample);
        } catch (RuntimeException ex) {
            log.warn("点位 {} 的质量异常没有记入告警：{}", sample.bindingCode(), ex.getMessage());
        }
    }

    /**
     * 告警说明给人看：采集点名称 + 大于/小于 + 限值。
     * 不用点位编号，也不把 GT 这类比较符写进去。
     */
    private static String content(ParsedPointSample sample, PointProcessingRule rule) {
        String point = rule.getPointName() == null ? "" : rule.getPointName().trim();
        String prefix = point.isEmpty() ? "" : point + " ";
        if (PointProcessingRule.ALARM_FLAG.equals(rule.getSignalKind())) {
            return prefix + "报警标识变为 1";
        }
        return prefix + "读数越过 " + compareLabel(rule.getCompareOp()) + " " + rule.getThreshold();
    }

    private static String compareLabel(String op) {
        if (op == null) {
            return "";
        }
        return switch (op) {
            case "GT" -> "大于";
            case "GTE" -> "大于等于";
            case "LT" -> "小于";
            case "LTE" -> "小于等于";
            case "EQ" -> "等于";
            case "NEQ" -> "不等于";
            default -> op;
        };
    }

    private RuleSnapshot currentRules() {
        long version = rules.version();
        if (version == seenVersion) {
            return snapshot;
        }
        synchronized (this) {
            if (version == seenVersion) {
                return snapshot;
            }
            Map<String, List<PointProcessingRule>> next = new HashMap<>();
            for (PointProcessingRule rule : rules.load()) {
                next.computeIfAbsent(rule.getPointFieldCode(), key -> new ArrayList<>()).add(rule);
            }
            Map<Long, Set<Long>> bound = rules.boundDevices();
            Map<Long, Long> models = rules.deviceModels();
            snapshot = new RuleSnapshot(next, bound == null ? Map.of() : bound,
                    models == null ? Map.of() : models,
                    behaviors.states(), behaviors.suppressions(), behaviors.policies());
            seenVersion = version;
            return snapshot;
        }
    }

    /**
     * 策略写了设备类型时，只看这台设备的型号是不是这一种。
     * 没写设备类型时，仍只看已经挂上的设备。两边都空着就不判断。
     */
    private static boolean matchesDevice(RuleSnapshot current, PointProcessingRule rule, long deviceId) {
        if (rule.getDeviceModelId() != null) {
            Long modelId = current.models.get(deviceId);
            return rule.getDeviceModelId().equals(modelId);
        }
        Set<Long> devices = current.bound.get(rule.getId());
        return devices != null && devices.contains(deviceId);
    }

    /** 同一版本读到的规则、设备关联和设备型号。一起换，避免规则已换、型号还是上一版。 */
    private static final class RuleSnapshot {
        private final Map<String, List<PointProcessingRule>> byField;
        private final Map<Long, Set<Long>> bound;
        private final Map<Long, Long> models;
        private final List<AlarmStateSetting> states;
        private final List<AlarmSuppression> suppressions;
        private final List<AlarmPolicy> policies;

        private RuleSnapshot(Map<String, List<PointProcessingRule>> byField, Map<Long, Set<Long>> bound,
                             Map<Long, Long> models, List<AlarmStateSetting> states,
                             List<AlarmSuppression> suppressions, List<AlarmPolicy> policies) {
            this.byField = byField;
            this.bound = bound;
            this.models = models;
            this.states = states;
            this.suppressions = suppressions;
            this.policies = policies == null ? List.of() : policies;
        }

        private static RuleSnapshot empty() {
            return new RuleSnapshot(Map.of(), Map.of(), Map.of(), List.of(), List.of(), List.of());
        }
    }
}
