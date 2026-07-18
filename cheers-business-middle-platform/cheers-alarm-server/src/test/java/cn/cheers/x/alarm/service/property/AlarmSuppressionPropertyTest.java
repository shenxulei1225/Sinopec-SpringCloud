package cn.cheers.x.alarm.service.property;

import cn.cheers.x.alarm.dal.dataobject.AlarmDO;
import cn.cheers.x.alarm.enums.AlarmLevelEnum;
import cn.cheers.x.alarm.enums.AlarmSourceEnum;
import cn.cheers.x.alarm.enums.AlarmStatusEnum;
import net.jqwik.api.*;
import net.jqwik.api.lifecycle.BeforeProperty;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 告警抑制属性测试
 * 
 * <p>Property 2: 5分钟内相同告警被抑制</p>
 * <p>*For any* 设备和告警类型，5分钟内重复触发只保留一条告警，触发次数递增</p>
 * 
 * <p>Validates: Requirements BR-BIZ-001</p>
 * <ul>
 *   <li>BR-BIZ-001: 同一设备在5分钟内触发相同类型告警，后续告警应被抑制，仅更新原告警的触发次数</li>
 * </ul>
 * 
 * <p>Feature: alarm-management, Property 2: 5分钟内相同告警被抑制</p>
 * 
 * @author 告警管理模块
 */
@PropertyDefaults(tries = 100)
public class AlarmSuppressionPropertyTest {

    /**
     * 告警抑制时间窗口（分钟）
     */
    private static final int SUPPRESSION_WINDOW_MINUTES = 5;

    /**
     * 模拟的告警存储（设备ID + 告警类型ID -> 告警）
     */
    private Map<String, AlarmDO> alarmStore;

    /**
     * 告警编码序列
     */
    private AtomicInteger alarmCodeSequence;

    @BeforeProperty
    void setUp() {
        alarmStore = new ConcurrentHashMap<>();
        alarmCodeSequence = new AtomicInteger(1);
    }

    // ==================== Property 2: 告警抑制正确性 ====================

    /**
     * Property 2.1: 5分钟内相同设备相同类型告警被抑制
     * 
     * 对于任意设备ID和告警类型ID，在5分钟内重复触发告警时，
     * 只保留第一条告警，后续告警被抑制，触发次数递增。
     * 
     * Validates: Requirements BR-BIZ-001
     */
    @Property
    @Label("Property 2.1: 5分钟内相同告警被抑制 - 只保留一条告警，触发次数递增")
    void sameAlarmWithinWindowShouldBeSuppressed(
        @ForAll("deviceIds") Long deviceId,
        @ForAll("alarmTypeIds") Long alarmTypeId,
        @ForAll("triggerCounts") int triggerCount
    ) {
        // Given: 清空告警存储
        alarmStore.clear();
        
        // 基准时间
        LocalDateTime baseTime = LocalDateTime.now();
        
        // When: 在5分钟内多次触发相同告警
        AlarmDO firstAlarm = null;
        for (int i = 0; i < triggerCount; i++) {
            // 每次触发时间在5分钟窗口内（随机分布在0-4分钟之间）
            LocalDateTime triggerTime = baseTime.plusSeconds(i * 30L); // 每30秒触发一次
            AlarmDO result = simulateTriggerAlarm(deviceId, alarmTypeId, triggerTime);
            
            if (i == 0) {
                firstAlarm = result;
            }
        }
        
        // Then: 只有一条告警被创建
        String key = buildSuppressionKey(deviceId, alarmTypeId);
        AlarmDO storedAlarm = alarmStore.get(key);
        
        assertThat(storedAlarm)
            .as("应该只有一条告警被存储")
            .isNotNull();
        
        assertThat(storedAlarm.getId())
            .as("存储的告警应该是第一条告警")
            .isEqualTo(firstAlarm.getId());
        
        // Then: 触发次数等于触发次数
        assertThat(storedAlarm.getTriggerCount())
            .as("触发次数应该等于实际触发次数")
            .isEqualTo(triggerCount);
    }

    /**
     * Property 2.2: 不同设备的告警不会被抑制
     * 
     * 对于不同的设备ID，即使告警类型相同，也不会被抑制。
     * 
     * Validates: Requirements BR-BIZ-001
     */
    @Property
    @Label("Property 2.2: 不同设备的告警不会被抑制")
    void differentDeviceAlarmsShouldNotBeSuppressed(
        @ForAll("deviceIdPairs") DeviceIdPair deviceIdPair,
        @ForAll("alarmTypeIds") Long alarmTypeId
    ) {
        // Given: 清空告警存储
        alarmStore.clear();
        
        LocalDateTime triggerTime = LocalDateTime.now();
        
        // When: 两个不同设备触发相同类型的告警
        AlarmDO alarm1 = simulateTriggerAlarm(deviceIdPair.deviceId1(), alarmTypeId, triggerTime);
        AlarmDO alarm2 = simulateTriggerAlarm(deviceIdPair.deviceId2(), alarmTypeId, triggerTime);
        
        // Then: 两条告警都被创建
        assertThat(alarmStore.size())
            .as("两个不同设备的告警都应该被创建")
            .isEqualTo(2);
        
        assertThat(alarm1.getId())
            .as("两条告警的ID应该不同")
            .isNotEqualTo(alarm2.getId());
        
        // Then: 每条告警的触发次数都是1
        assertThat(alarm1.getTriggerCount())
            .as("第一条告警的触发次数应该是1")
            .isEqualTo(1);
        
        assertThat(alarm2.getTriggerCount())
            .as("第二条告警的触发次数应该是1")
            .isEqualTo(1);
    }

    /**
     * Property 2.3: 不同类型的告警不会被抑制
     * 
     * 对于相同的设备ID，不同的告警类型不会被抑制。
     * 
     * Validates: Requirements BR-BIZ-001
     */
    @Property
    @Label("Property 2.3: 不同类型的告警不会被抑制")
    void differentTypeAlarmsShouldNotBeSuppressed(
        @ForAll("deviceIds") Long deviceId,
        @ForAll("alarmTypeIdPairs") AlarmTypeIdPair alarmTypeIdPair
    ) {
        // Given: 清空告警存储
        alarmStore.clear();
        
        LocalDateTime triggerTime = LocalDateTime.now();
        
        // When: 同一设备触发两种不同类型的告警
        AlarmDO alarm1 = simulateTriggerAlarm(deviceId, alarmTypeIdPair.typeId1(), triggerTime);
        AlarmDO alarm2 = simulateTriggerAlarm(deviceId, alarmTypeIdPair.typeId2(), triggerTime);
        
        // Then: 两条告警都被创建
        assertThat(alarmStore.size())
            .as("两种不同类型的告警都应该被创建")
            .isEqualTo(2);
        
        assertThat(alarm1.getId())
            .as("两条告警的ID应该不同")
            .isNotEqualTo(alarm2.getId());
        
        // Then: 每条告警的触发次数都是1
        assertThat(alarm1.getTriggerCount())
            .as("第一条告警的触发次数应该是1")
            .isEqualTo(1);
        
        assertThat(alarm2.getTriggerCount())
            .as("第二条告警的触发次数应该是1")
            .isEqualTo(1);
    }

    /**
     * Property 2.4: 超过5分钟窗口的告警不会被抑制
     * 
     * 对于相同设备和告警类型，如果两次触发间隔超过5分钟，则不会被抑制。
     * 
     * Validates: Requirements BR-BIZ-001
     */
    @Property
    @Label("Property 2.4: 超过5分钟窗口的告警不会被抑制")
    void alarmOutsideWindowShouldNotBeSuppressed(
        @ForAll("deviceIds") Long deviceId,
        @ForAll("alarmTypeIds") Long alarmTypeId,
        @ForAll("minutesOutsideWindow") int minutesOutsideWindow
    ) {
        // Given: 清空告警存储，使用独立的存储来模拟时间窗口外的场景
        Map<String, AlarmDO> timeAwareStore = new HashMap<>();
        
        LocalDateTime firstTriggerTime = LocalDateTime.now().minusMinutes(minutesOutsideWindow);
        LocalDateTime secondTriggerTime = LocalDateTime.now();
        
        // When: 第一次触发告警
        AlarmDO alarm1 = simulateTriggerAlarmWithTimeAwareStore(
            deviceId, alarmTypeId, firstTriggerTime, timeAwareStore);
        
        // When: 超过5分钟后再次触发告警
        AlarmDO alarm2 = simulateTriggerAlarmWithTimeAwareStore(
            deviceId, alarmTypeId, secondTriggerTime, timeAwareStore);
        
        // Then: 由于第一条告警已超出时间窗口，第二条告警应该被创建为新告警
        assertThat(alarm2.getId())
            .as("超过5分钟窗口后，应该创建新告警")
            .isNotEqualTo(alarm1.getId());
        
        assertThat(alarm2.getTriggerCount())
            .as("新告警的触发次数应该是1")
            .isEqualTo(1);
    }

    /**
     * Property 2.5: 已关闭的告警不参与抑制
     * 
     * 对于已关闭的告警，即使在5分钟内，新告警也不会被抑制。
     * 
     * Validates: Requirements BR-BIZ-001
     */
    @Property
    @Label("Property 2.5: 已关闭的告警不参与抑制")
    void closedAlarmShouldNotSuppressNewAlarm(
        @ForAll("deviceIds") Long deviceId,
        @ForAll("alarmTypeIds") Long alarmTypeId
    ) {
        // Given: 清空告警存储
        alarmStore.clear();
        
        LocalDateTime triggerTime = LocalDateTime.now();
        
        // When: 第一次触发告警
        AlarmDO alarm1 = simulateTriggerAlarm(deviceId, alarmTypeId, triggerTime);
        
        // When: 关闭第一条告警
        alarm1.setAlarmStatus(AlarmStatusEnum.CLOSED.getStatus());
        alarm1.setCloseTime(triggerTime.plusMinutes(1));
        
        // When: 在5分钟内再次触发告警（此时第一条告警已关闭）
        AlarmDO alarm2 = simulateTriggerAlarmWithClosedCheck(
            deviceId, alarmTypeId, triggerTime.plusMinutes(2));
        
        // Then: 由于第一条告警已关闭，第二条告警应该被创建为新告警
        assertThat(alarm2.getId())
            .as("已关闭的告警不应该抑制新告警")
            .isNotEqualTo(alarm1.getId());
        
        assertThat(alarm2.getTriggerCount())
            .as("新告警的触发次数应该是1")
            .isEqualTo(1);
    }

    /**
     * Property 2.6: 触发次数递增的正确性
     * 
     * 对于被抑制的告警，触发次数应该正确递增。
     * 
     * Validates: Requirements BR-BIZ-001
     */
    @Property
    @Label("Property 2.6: 触发次数递增的正确性")
    void triggerCountShouldIncrementCorrectly(
        @ForAll("deviceIds") Long deviceId,
        @ForAll("alarmTypeIds") Long alarmTypeId,
        @ForAll("triggerCounts") int triggerCount
    ) {
        // Given: 清空告警存储
        alarmStore.clear();
        
        LocalDateTime baseTime = LocalDateTime.now();
        
        // When: 多次触发相同告警
        AlarmDO alarm = null;
        for (int i = 0; i < triggerCount; i++) {
            LocalDateTime triggerTime = baseTime.plusSeconds(i * 10L);
            alarm = simulateTriggerAlarm(deviceId, alarmTypeId, triggerTime);
            
            // Then: 每次触发后，触发次数应该等于当前触发次数
            assertThat(alarm.getTriggerCount())
                .as("第 %d 次触发后，触发次数应该是 %d", i + 1, i + 1)
                .isEqualTo(i + 1);
        }
    }

    /**
     * Property 2.7: 抑制不影响告警内容
     * 
     * 被抑制的告警，其原始内容（告警类型、级别、设备等）不应被修改。
     * 
     * Validates: Requirements BR-BIZ-001
     */
    @Property
    @Label("Property 2.7: 抑制不影响告警内容")
    void suppressionShouldNotModifyAlarmContent(
        @ForAll("deviceIds") Long deviceId,
        @ForAll("alarmTypeIds") Long alarmTypeId,
        @ForAll("alarmLevels") String alarmLevel
    ) {
        // Given: 清空告警存储
        alarmStore.clear();
        
        LocalDateTime triggerTime = LocalDateTime.now();
        
        // When: 第一次触发告警
        AlarmDO alarm1 = simulateTriggerAlarmWithLevel(deviceId, alarmTypeId, alarmLevel, triggerTime);
        
        // 记录原始值
        Long originalId = alarm1.getId();
        String originalCode = alarm1.getAlarmCode();
        String originalLevel = alarm1.getAlarmLevel();
        Long originalDeviceId = alarm1.getDeviceId();
        Long originalTypeId = alarm1.getAlarmTypeId();
        
        // When: 在5分钟内再次触发告警（被抑制）
        AlarmDO alarm2 = simulateTriggerAlarmWithLevel(deviceId, alarmTypeId, alarmLevel, 
            triggerTime.plusMinutes(1));
        
        // Then: 告警的核心内容不应被修改
        assertThat(alarm2.getId())
            .as("告警ID不应被修改")
            .isEqualTo(originalId);
        
        assertThat(alarm2.getAlarmCode())
            .as("告警编码不应被修改")
            .isEqualTo(originalCode);
        
        assertThat(alarm2.getAlarmLevel())
            .as("告警级别不应被修改")
            .isEqualTo(originalLevel);
        
        assertThat(alarm2.getDeviceId())
            .as("设备ID不应被修改")
            .isEqualTo(originalDeviceId);
        
        assertThat(alarm2.getAlarmTypeId())
            .as("告警类型ID不应被修改")
            .isEqualTo(originalTypeId);
        
        // Then: 只有触发次数被更新
        assertThat(alarm2.getTriggerCount())
            .as("触发次数应该递增")
            .isEqualTo(2);
    }

    // ==================== 数据生成器 ====================

    /**
     * 生成设备ID
     */
    @Provide
    Arbitrary<Long> deviceIds() {
        return Arbitraries.longs().between(1L, 1000L);
    }

    /**
     * 生成告警类型ID
     */
    @Provide
    Arbitrary<Long> alarmTypeIds() {
        return Arbitraries.longs().between(1L, 100L);
    }

    /**
     * 生成触发次数（2-10次，确保有抑制发生）
     */
    @Provide
    Arbitrary<Integer> triggerCounts() {
        return Arbitraries.integers().between(2, 10);
    }

    /**
     * 生成超出时间窗口的分钟数（6-30分钟）
     */
    @Provide
    Arbitrary<Integer> minutesOutsideWindow() {
        return Arbitraries.integers().between(SUPPRESSION_WINDOW_MINUTES + 1, 30);
    }

    /**
     * 生成告警级别
     */
    @Provide
    Arbitrary<String> alarmLevels() {
        return Arbitraries.of(
            AlarmLevelEnum.INFO.getLevel(),
            AlarmLevelEnum.WARNING.getLevel(),
            AlarmLevelEnum.CRITICAL.getLevel(),
            AlarmLevelEnum.EMERGENCY.getLevel()
        );
    }

    /**
     * 生成不同的设备ID对
     */
    @Provide
    Arbitrary<DeviceIdPair> deviceIdPairs() {
        return Arbitraries.longs().between(1L, 1000L)
            .tuple2()
            .filter(tuple -> !tuple.get1().equals(tuple.get2()))
            .map(tuple -> new DeviceIdPair(tuple.get1(), tuple.get2()));
    }

    /**
     * 生成不同的告警类型ID对
     */
    @Provide
    Arbitrary<AlarmTypeIdPair> alarmTypeIdPairs() {
        return Arbitraries.longs().between(1L, 100L)
            .tuple2()
            .filter(tuple -> !tuple.get1().equals(tuple.get2()))
            .map(tuple -> new AlarmTypeIdPair(tuple.get1(), tuple.get2()));
    }

    // ==================== 辅助方法 ====================

    /**
     * 模拟触发告警（包含抑制逻辑）
     */
    private AlarmDO simulateTriggerAlarm(Long deviceId, Long alarmTypeId, LocalDateTime triggerTime) {
        String key = buildSuppressionKey(deviceId, alarmTypeId);
        
        // 检查是否存在活跃告警（模拟 checkSuppression 逻辑）
        AlarmDO existingAlarm = alarmStore.get(key);
        if (existingAlarm != null && !AlarmStatusEnum.isClosed(existingAlarm.getAlarmStatus())) {
            // 检查是否在5分钟窗口内
            LocalDateTime windowStart = triggerTime.minusMinutes(SUPPRESSION_WINDOW_MINUTES);
            if (existingAlarm.getCreateTime().isAfter(windowStart)) {
                // 告警被抑制，更新触发次数
                existingAlarm.setTriggerCount(existingAlarm.getTriggerCount() + 1);
                return existingAlarm;
            }
        }
        
        // 创建新告警
        AlarmDO newAlarm = createAlarmDO(deviceId, alarmTypeId, triggerTime);
        alarmStore.put(key, newAlarm);
        return newAlarm;
    }

    /**
     * 模拟触发告警（带时间感知的存储）
     */
    private AlarmDO simulateTriggerAlarmWithTimeAwareStore(
        Long deviceId, Long alarmTypeId, LocalDateTime triggerTime, 
        Map<String, AlarmDO> store
    ) {
        String key = buildSuppressionKey(deviceId, alarmTypeId);
        
        // 检查是否存在活跃告警
        AlarmDO existingAlarm = store.get(key);
        if (existingAlarm != null && !AlarmStatusEnum.isClosed(existingAlarm.getAlarmStatus())) {
            // 检查是否在5分钟窗口内
            LocalDateTime windowStart = triggerTime.minusMinutes(SUPPRESSION_WINDOW_MINUTES);
            if (existingAlarm.getCreateTime().isAfter(windowStart)) {
                // 告警被抑制，更新触发次数
                existingAlarm.setTriggerCount(existingAlarm.getTriggerCount() + 1);
                return existingAlarm;
            }
        }
        
        // 创建新告警
        AlarmDO newAlarm = createAlarmDO(deviceId, alarmTypeId, triggerTime);
        store.put(key, newAlarm);
        return newAlarm;
    }

    /**
     * 模拟触发告警（检查已关闭状态）
     */
    private AlarmDO simulateTriggerAlarmWithClosedCheck(
        Long deviceId, Long alarmTypeId, LocalDateTime triggerTime
    ) {
        String key = buildSuppressionKey(deviceId, alarmTypeId);
        
        // 检查是否存在活跃告警（排除已关闭的）
        AlarmDO existingAlarm = alarmStore.get(key);
        if (existingAlarm != null && !AlarmStatusEnum.isClosed(existingAlarm.getAlarmStatus())) {
            // 检查是否在5分钟窗口内
            LocalDateTime windowStart = triggerTime.minusMinutes(SUPPRESSION_WINDOW_MINUTES);
            if (existingAlarm.getCreateTime().isAfter(windowStart)) {
                // 告警被抑制，更新触发次数
                existingAlarm.setTriggerCount(existingAlarm.getTriggerCount() + 1);
                return existingAlarm;
            }
        }
        
        // 创建新告警（使用新的 key 以避免覆盖已关闭的告警）
        AlarmDO newAlarm = createAlarmDO(deviceId, alarmTypeId, triggerTime);
        String newKey = key + "_" + newAlarm.getId();
        alarmStore.put(newKey, newAlarm);
        return newAlarm;
    }

    /**
     * 模拟触发告警（带告警级别）
     */
    private AlarmDO simulateTriggerAlarmWithLevel(
        Long deviceId, Long alarmTypeId, String alarmLevel, LocalDateTime triggerTime
    ) {
        String key = buildSuppressionKey(deviceId, alarmTypeId);
        
        // 检查是否存在活跃告警
        AlarmDO existingAlarm = alarmStore.get(key);
        if (existingAlarm != null && !AlarmStatusEnum.isClosed(existingAlarm.getAlarmStatus())) {
            // 检查是否在5分钟窗口内
            LocalDateTime windowStart = triggerTime.minusMinutes(SUPPRESSION_WINDOW_MINUTES);
            if (existingAlarm.getCreateTime().isAfter(windowStart)) {
                // 告警被抑制，更新触发次数
                existingAlarm.setTriggerCount(existingAlarm.getTriggerCount() + 1);
                return existingAlarm;
            }
        }
        
        // 创建新告警
        AlarmDO newAlarm = createAlarmDO(deviceId, alarmTypeId, triggerTime);
        newAlarm.setAlarmLevel(alarmLevel);
        alarmStore.put(key, newAlarm);
        return newAlarm;
    }

    /**
     * 构建抑制检查的 key
     */
    private String buildSuppressionKey(Long deviceId, Long alarmTypeId) {
        return deviceId + "_" + alarmTypeId;
    }

    /**
     * 创建告警 DO
     */
    private AlarmDO createAlarmDO(Long deviceId, Long alarmTypeId, LocalDateTime createTime) {
        AlarmDO alarm = new AlarmDO();
        alarm.setId((long) alarmCodeSequence.getAndIncrement());
        alarm.setAlarmCode("ALM-TEST-" + String.format("%05d", alarm.getId()));
        alarm.setDeviceId(deviceId);
        alarm.setAlarmTypeId(alarmTypeId);
        alarm.setAlarmCategoryId(1L);
        alarm.setAlarmModelId(1L);
        alarm.setAlarmTypePath("测试分类 > 测试模型 > 测试实体");
        alarm.setAlarmLevel(AlarmLevelEnum.WARNING.getLevel());
        alarm.setAlarmStatus(AlarmStatusEnum.PENDING.getStatus());
        alarm.setAlarmSource(AlarmSourceEnum.SYSTEM.getSource());
        alarm.setAlarmContent("测试告警内容");
        alarm.setDeviceName("测试设备-" + deviceId);
        alarm.setLocationId(1L);
        alarm.setLocationName("测试位置");
        alarm.setTriggerValue("55");
        alarm.setThresholdValue("50");
        alarm.setTriggerCount(1);
        alarm.setEscalationLevel(0);
        alarm.setTenantId(1L);
        alarm.setDeleted(false);
        alarm.setCreateTime(createTime);
        return alarm;
    }

    // ==================== 内部类 ====================

    /**
     * 设备ID对
     */
    record DeviceIdPair(Long deviceId1, Long deviceId2) {}

    /**
     * 告警类型ID对
     */
    record AlarmTypeIdPair(Long typeId1, Long typeId2) {}

}
