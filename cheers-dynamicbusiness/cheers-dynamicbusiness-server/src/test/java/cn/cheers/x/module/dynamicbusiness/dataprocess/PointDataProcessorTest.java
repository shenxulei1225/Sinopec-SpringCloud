package cn.cheers.x.module.dynamicbusiness.dataprocess;

import cn.cheers.x.module.dynamicbusiness.devicehistory.DevicePointHistoryRules;
import cn.cheers.x.module.dynamicbusiness.devicehistory.DevicePointHistoryService;
import cn.cheers.x.module.dynamicbusiness.devicehistory.ParsedPointSample;
import cn.cheers.x.module.dynamicbusiness.service.strategy.PointProcessingRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class PointDataProcessorTest {

    private DevicePointHistoryService history;
    private FakeRules rules;
    private FakeAlarms alarms;
    private FakeBehaviors behaviors;
    private List<String> status;
    private PointDataProcessor processor;

    @BeforeEach
    void setUp() {
        history = mock(DevicePointHistoryService.class);
        rules = new FakeRules();
        alarms = new FakeAlarms();
        behaviors = new FakeBehaviors();
        status = new ArrayList<>();
        processor = new PointDataProcessor(history, rules, alarms, behaviors, (deviceId, pointCode, value, sampledAt) ->
                status.add(deviceId + ":" + pointCode + ":" + value));
    }

    @Test
    void writesTheWholeBatchOnceAndSkipsTheFirstReading() {
        rules.rows.add(flag());
        processor.accept(List.of(sample(0)));
        verify(history).appendBatch(anyList());
        assertEquals(0, alarms.raised);
        assertEquals(0, alarms.cleared);
    }

    @Test
    void firstOverThresholdRaisesWithoutAPreviousReading() {
        rules.rows.add(named(9L, "F-temp", PointProcessingRule.THRESHOLD, "GT", 30d,
                3L, "WARNING", null, null, 9L));
        processor.accept(List.of(sample("F-temp", 31)));
        assertEquals(1, alarms.raised);
        processor.accept(List.of(sample("F-temp", 32)));
        assertEquals(1, alarms.raised);
    }

    @Test
    void raiseUsesRuleNameAndHumanCompareWords() {
        rules.rows.add(named(9L, "F-temp", PointProcessingRule.THRESHOLD, "GT", 30d,
                3L, "WARNING", null, null, 9L));
        processor.accept(List.of(sample("F-temp", 31)));
        assertEquals("测试告警", alarms.lastName);
        assertEquals("温度 读数越过 大于 30.0", alarms.lastContent);
    }

    @Test
    void firstAlarmFlagOnRaisesWithoutAPreviousReading() {
        rules.rows.add(flag());
        processor.accept(List.of(sample(1)));
        assertEquals(1, alarms.raised);
    }

    @Test
    void alarmFlagRaisesThenClears() {
        rules.rows.add(flag());
        processor.accept(List.of(sample(0)));
        processor.accept(List.of(sample(1)));
        processor.accept(List.of(sample(1)));
        processor.accept(List.of(sample(0)));
        assertEquals(1, alarms.raised);
        assertEquals(1, alarms.cleared);
    }

    @Test
    void thresholdRaisesOnceAndDoesNotClearWhenItReturns() {
        rules.rows.add(named(9L, "F-temp", PointProcessingRule.THRESHOLD, "GT", 30d,
                3L, "WARNING", null, null, 9L));
        processor.accept(List.of(sample("F-temp", 20)));
        processor.accept(List.of(sample("F-temp", 31)));
        processor.accept(List.of(sample("F-temp", 32)));
        processor.accept(List.of(sample("F-temp", 20)));
        assertEquals(1, alarms.raised);
        assertEquals(0, alarms.cleared);
    }

    @Test
    void statusPublishesOnlyAfterTheValueChanges() {
        rules.rows.add(new PointProcessingRule(4L, "F-run", PointProcessingRule.STATUS, null, null,
                null, null, null, null));
        processor.accept(List.of(sample("F-run", 0)));
        processor.accept(List.of(sample("F-run", 1)));
        assertEquals(List.of("8:F-run:0.0", "8:F-run:1.0"), status);
        assertEquals(0, alarms.raised);
    }

    @Test
    void unboundStrategyDoesNotRaise() {
        rules.bindSampleDevice = false;
        rules.rows.add(flag());
        processor.accept(List.of(sample(0)));
        processor.accept(List.of(sample(1)));
        assertEquals(0, alarms.raised);
    }

    @Test
    void typeStrategyAppliesToEveryDeviceOfThatModel() {
        rules.bindSampleDevice = false;
        rules.models.put(8L, 2546L);
        rules.rows.add(named(11L, "F-temp", PointProcessingRule.THRESHOLD, "GT", 40d,
                3L, "WARNING", 2546L, null, 11L));
        processor.accept(List.of(sample("F-temp", 20)));
        processor.accept(List.of(sample("F-temp", 46)));
        assertEquals(1, alarms.raised);
    }

    @Test
    void thresholdClearsOnlyAfterRecovery() {
        rules.rows.add(named(9L, "F-temp", PointProcessingRule.THRESHOLD, "GT", 40d,
                3L, "WARNING", null, 38d, 9L));
        processor.accept(List.of(sample("F-temp", 20)));
        processor.accept(List.of(sample("F-temp", 41)));
        processor.accept(List.of(sample("F-temp", 39)));
        assertEquals(1, alarms.raised);
        assertEquals(0, alarms.cleared);
        processor.accept(List.of(sample("F-temp", 37)));
        assertEquals(1, alarms.cleared);
    }

    @Test
    void typeStrategySkipsAnotherModel() {
        rules.bindSampleDevice = false;
        rules.models.put(8L, 2547L);
        rules.rows.add(named(11L, "F-temp", PointProcessingRule.THRESHOLD, "GT", 40d,
                3L, "WARNING", 2546L, null, 11L));
        processor.accept(List.of(sample("F-temp", 20)));
        processor.accept(List.of(sample("F-temp", 46)));
        assertEquals(0, alarms.raised);
    }

    @Test
    void abnormalQualityStaysOutOfTheDecision() {
        rules.rows.add(flag());
        processor.accept(List.of(sample(0)));
        processor.accept(List.of(new ParsedPointSample(
                123L, 8L, "F-fault", "GKYB01/F", 107L, Instant.parse("2026-10-29T08:00:00Z"),
                1, DevicePointHistoryRules.QUALITY_ABNORMAL)));
        processor.accept(List.of(sample(1)));
        assertEquals(1, alarms.raised);
        assertEquals(1, alarms.abnormal);
    }

    @Test
    void stoppedStateSkipsTheSelectedRule() {
        rules.bindSampleDevice = false;
        rules.models.put(8L, 2546L);
        rules.rows.add(named(11L, "F-temp", PointProcessingRule.THRESHOLD, "GT", 40d,
                3L, "WARNING", 2546L, null, 11L));
        behaviors.states.add(new AlarmStateSetting(2546L, "F-run", "EQ", 0d, "SKIP", Set.of(11L),
                "", "", "", "", ""));
        processor.accept(List.of(sample("F-run", 0)));
        processor.accept(List.of(sample("F-temp", 20)));
        processor.accept(List.of(sample("F-temp", 46)));
        assertEquals(0, alarms.raised);
    }

    @Test
    void missingStateReadingStillJudgesTheRule() {
        rules.bindSampleDevice = false;
        rules.models.put(8L, 2546L);
        rules.rows.add(named(11L, "F-temp", PointProcessingRule.THRESHOLD, "GT", 40d,
                3L, "WARNING", 2546L, null, 11L));
        behaviors.states.add(new AlarmStateSetting(2546L, "F-run", "EQ", 0d, "SKIP", Set.of(11L),
                "", "", "", "", ""));
        processor.accept(List.of(sample("F-temp", 20)));
        processor.accept(List.of(sample("F-temp", 46)));
        assertEquals(1, alarms.raised);
    }

    @Test
    void leadingAlarmHoldsTheFollowingRule() {
        rules.rows.add(flag());
        rules.rows.add(named(9L, "F-level", PointProcessingRule.ALARM_FLAG, "EQ", 1d,
                2L, "CRITICAL", null, null, 9L));
        behaviors.suppressions.add(new AlarmSuppression(7L, Set.of(9L)));
        processor.accept(List.of(sample(0)));
        processor.accept(List.of(sample(1)));
        processor.accept(List.of(sample("F-level", 0)));
        processor.accept(List.of(sample("F-level", 1)));
        assertEquals(1, alarms.raised);
    }

    @Test
    void stoppedPumpPolicyDoesNotRaiseTheSelectedRule() {
        rules.bindSampleDevice = false;
        rules.models.put(8L, 2546L);
        rules.rows.add(named(11L, "F-temp", PointProcessingRule.THRESHOLD, "GT", 40d,
                3L, "WARNING", 2546L, null, 11L));
        behaviors.policies.add(AlarmPolicy.parse("state:2546:F-run:0", Set.of(11L)));
        processor.accept(List.of(sample("F-run", 0)));
        processor.accept(List.of(sample("F-temp", 20)));
        processor.accept(List.of(sample("F-temp", 46)));
        assertEquals(0, alarms.raised);
    }

    @Test
    void emptyRuleSelectionSkipsEveryRuleOnTheDevice() {
        rules.bindSampleDevice = false;
        rules.models.put(8L, 2546L);
        rules.rows.add(named(11L, "F-temp", PointProcessingRule.THRESHOLD, "GT", 40d,
                3L, "WARNING", 2546L, null, 11L));
        rules.rows.add(named(12L, "F-fault", PointProcessingRule.ALARM_FLAG, "EQ", 1d,
                2L, "CRITICAL", 2546L, null, 12L));
        behaviors.policies.add(AlarmPolicy.parse("state:2546:F-run:0", Set.of()));
        processor.accept(List.of(sample("F-run", 0)));
        processor.accept(List.of(sample("F-temp", 20)));
        processor.accept(List.of(sample("F-temp", 46)));
        processor.accept(List.of(sample("F-fault", 0)));
        processor.accept(List.of(sample("F-fault", 1)));
        assertEquals(0, alarms.raised);
    }

    @Test
    void missingPolicyReadingStillRaises() {
        rules.bindSampleDevice = false;
        rules.models.put(8L, 2546L);
        rules.rows.add(named(11L, "F-temp", PointProcessingRule.THRESHOLD, "GT", 40d,
                3L, "WARNING", 2546L, null, 11L));
        behaviors.policies.add(AlarmPolicy.parse("state:2546:F-run:0", Set.of(11L)));
        processor.accept(List.of(sample("F-temp", 20)));
        processor.accept(List.of(sample("F-temp", 46)));
        assertEquals(1, alarms.raised);
    }

    @Test
    void openAlarmPolicyDoesNotRaiseTheHeldRule() {
        rules.rows.add(flag());
        rules.rows.add(named(9L, "F-level", PointProcessingRule.ALARM_FLAG, "EQ", 1d,
                2L, "CRITICAL", null, null, 9L));
        behaviors.policies.add(AlarmPolicy.parse("open:7", Set.of(9L)));
        processor.accept(List.of(sample(0)));
        processor.accept(List.of(sample(1)));
        processor.accept(List.of(sample("F-level", 0)));
        processor.accept(List.of(sample("F-level", 1)));
        assertEquals(1, alarms.raised);
    }

    @Test
    void allConditionsMustHoldBeforeSkipping() {
        rules.bindSampleDevice = false;
        rules.models.put(8L, 2546L);
        rules.rows.add(named(11L, "F-temp", PointProcessingRule.THRESHOLD, "GT", 40d,
                3L, "WARNING", 2546L, null, 11L));
        behaviors.policies.add(new AlarmPolicy(List.of(
                new AlarmPolicy.Clause("STATE", Map.of(2546L, new AlarmPolicy.Expect("F-run", 0)), null),
                new AlarmPolicy.Clause("STATE", Map.of(2546L, new AlarmPolicy.Expect("F-fault", 1)), null)),
                true, Set.of(2546L), Set.of(11L), false));
        processor.accept(List.of(sample("F-run", 0)));
        processor.accept(List.of(sample("F-fault", 0)));
        processor.accept(List.of(sample("F-temp", 20)));
        processor.accept(List.of(sample("F-temp", 46)));
        assertEquals(1, alarms.raised);
    }

    @Test
    void anyOneConditionSkipsTheSelectedRule() {
        rules.bindSampleDevice = false;
        rules.models.put(8L, 2546L);
        rules.rows.add(named(11L, "F-temp", PointProcessingRule.THRESHOLD, "GT", 40d,
                3L, "WARNING", 2546L, null, 11L));
        behaviors.policies.add(new AlarmPolicy(List.of(
                new AlarmPolicy.Clause("STATE", Map.of(2546L, new AlarmPolicy.Expect("F-run", 0)), null),
                new AlarmPolicy.Clause("STATE", Map.of(2546L, new AlarmPolicy.Expect("F-fault", 1)), null)),
                false, Set.of(2546L), Set.of(11L), false));
        processor.accept(List.of(sample("F-run", 0)));
        processor.accept(List.of(sample("F-fault", 0)));
        processor.accept(List.of(sample("F-temp", 20)));
        processor.accept(List.of(sample("F-temp", 46)));
        assertEquals(0, alarms.raised);
    }

    @Test
    void specifiedModelDoesNotSkipAnotherModel() {
        rules.bindSampleDevice = false;
        rules.models.put(8L, 9999L);
        rules.rows.add(named(11L, "F-temp", PointProcessingRule.THRESHOLD, "GT", 40d,
                3L, "WARNING", 9999L, null, 11L));
        behaviors.policies.add(new AlarmPolicy(List.of(
                new AlarmPolicy.Clause("STATE", Map.of(2546L, new AlarmPolicy.Expect("F-run", 0)), null)),
                false, Set.of(2546L), Set.of(11L), false));
        processor.accept(List.of(sample("F-run", 0)));
        processor.accept(List.of(sample("F-temp", 20)));
        processor.accept(List.of(sample("F-temp", 46)));
        assertEquals(1, alarms.raised);
    }

    @Test
    void shelvedSignalDoesNotRaise() {
        rules.rows.add(flag());
        alarms.shelved.add(7L);
        processor.accept(List.of(sample(0)));
        processor.accept(List.of(sample(1)));
        assertEquals(0, alarms.raised);
    }

    private static PointProcessingRule flag() {
        return named(7L, "F-fault", PointProcessingRule.ALARM_FLAG, "EQ", 1d,
                2L, "CRITICAL", null, null, 7L);
    }

    private static PointProcessingRule named(long id, String point, String kind, String op, Double threshold,
                                            Long type, String level, Long modelId, Double recovery, long signalKey) {
        String pointName = "F-temp".equals(point) ? "温度"
                : "F-fault".equals(point) ? "故障"
                : "F-level".equals(point) ? "液位" : "测点";
        return new PointProcessingRule(id, point, kind, op, threshold, type, level,
                null, null, modelId, recovery, signalKey, "测试告警", pointName);
    }

    private static ParsedPointSample sample(double value) {
        return sample("F-fault", value);
    }

    private static ParsedPointSample sample(String field, double value) {
        return new ParsedPointSample(123L, 8L, field, "GKYB01/F", 107L,
                Instant.parse("2026-10-29T08:00:00Z"), value, DevicePointHistoryRules.QUALITY_NORMAL);
    }

    private static final class FakeRules implements PointRuleSource {
        private final List<PointProcessingRule> rows = new ArrayList<>();
        private final Map<Long, Long> models = new HashMap<>();
        private boolean bindSampleDevice = true;

        @Override
        public long version() {
            return 1L;
        }

        @Override
        public List<PointProcessingRule> load() {
            return rows;
        }

        @Override
        public Map<Long, Set<Long>> boundDevices() {
            Map<Long, Set<Long>> bound = new HashMap<>();
            if (!bindSampleDevice) {
                return bound;
            }
            for (PointProcessingRule row : rows) {
                bound.put(row.getId(), Set.of(8L));
            }
            return bound;
        }

        @Override
        public Map<Long, Long> deviceModels() {
            return models;
        }
    }

    private static final class FakeBehaviors implements AlarmBehaviorLookup {
        private final List<AlarmStateSetting> states = new ArrayList<>();
        private final List<AlarmSuppression> suppressions = new ArrayList<>();
        private final List<AlarmPolicy> policies = new ArrayList<>();

        @Override
        public List<AlarmStateSetting> states() {
            return states;
        }

        @Override
        public List<AlarmSuppression> suppressions() {
            return suppressions;
        }

        @Override
        public List<AlarmPolicy> policies() {
            return policies;
        }
    }

    private static final class FakeAlarms implements AlarmSignalGateway {
        private int raised;
        private int cleared;
        private int abnormal;
        private String lastName;
        private String lastContent;
        private final Set<Long> open = new java.util.HashSet<>();
        private final Set<Long> shelved = new java.util.HashSet<>();

        @Override
        public void raise(long tenantId, long alarmTypeId, String alarmLevel, String name, String content, long deviceId,
                          long locationId, String triggerValue, String thresholdValue, long ruleId, String pointCode) {
            raised++;
            lastName = name;
            lastContent = content;
            open.add(ruleId);
        }

        @Override
        public void eliminate(long tenantId, long deviceId, long ruleId) {
            cleared++;
            open.remove(ruleId);
        }

        @Override
        public void recordAbnormalQuality(ParsedPointSample sample) {
            abnormal++;
        }

        @Override
        public boolean shelved(long tenantId, long deviceId, long signalKey) {
            return shelved.contains(signalKey);
        }

        @Override
        public boolean leadingOpen(long tenantId, long deviceId, long ruleId) {
            return open.contains(ruleId) || open.contains(-ruleId);
        }
    }
}
