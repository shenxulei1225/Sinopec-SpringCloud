package cn.cheers.x.alarm.service.property;

import cn.cheers.x.alarm.dal.dataobject.AlarmDO;
import cn.cheers.x.alarm.enums.AlarmLevelEnum;
import cn.cheers.x.alarm.enums.AlarmSourceEnum;
import cn.cheers.x.alarm.enums.AlarmStatusEnum;
import net.jqwik.api.*;
import net.jqwik.api.lifecycle.BeforeProperty;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 告警数据完整性属性测试
 * 
 * <p>Property 4: 告警必须包含所有必填信息</p>
 * <p>*For any* 告警，必须包含告警类型、级别、时间、内容、设备、位置、来源</p>
 * 
 * <p>Validates: Requirements BR-VAL-001</p>
 * <ul>
 *   <li>BR-VAL-001: 告警必须包含：告警ID、告警类型、告警级别、告警时间、告警内容、关联设备、位置信息、告警来源</li>
 * </ul>
 * 
 * <p>Feature: alarm-management, Property 4: 告警必须包含所有必填信息</p>
 * 
 * @author 告警管理模块
 */
@PropertyDefaults(tries = 100)
public class AlarmDataIntegrityPropertyTest {

    /**
     * ID 序列
     */
    private AtomicLong idSequence;

    @BeforeProperty
    void setUp() {
        idSequence = new AtomicLong(1);
    }

    // ==================== Property 4: 告警数据完整性 ====================

    /**
     * Property 4.1: 有效告警必须包含所有必填字段
     * 
     * 对于任意有效的告警，必须包含所有必填信息。
     * 
     * Validates: Requirements BR-VAL-001
     */
    @Property
    @Label("Property 4.1: 有效告警必须包含所有必填字段")
    void validAlarmMustContainAllRequiredFields(
        @ForAll("validAlarms") AlarmDO alarm
    ) {
        // Then: 验证所有必填字段都存在
        assertThat(alarm.getId())
            .as("告警ID不能为空")
            .isNotNull();
        
        assertThat(alarm.getAlarmCode())
            .as("告警编码不能为空")
            .isNotNull()
            .isNotEmpty();
        
        assertThat(alarm.getAlarmTypeId())
            .as("告警类型ID不能为空")
            .isNotNull();
        
        assertThat(alarm.getAlarmLevel())
            .as("告警级别不能为空")
            .isNotNull()
            .isNotEmpty();
        
        assertThat(alarm.getCreateTime())
            .as("告警时间不能为空")
            .isNotNull();
        
        assertThat(alarm.getAlarmContent())
            .as("告警内容不能为空")
            .isNotNull()
            .isNotEmpty();
        
        assertThat(alarm.getDeviceId())
            .as("关联设备ID不能为空")
            .isNotNull();
        
        assertThat(alarm.getLocationId())
            .as("位置ID不能为空")
            .isNotNull();
        
        assertThat(alarm.getAlarmSource())
            .as("告警来源不能为空")
            .isNotNull()
            .isNotEmpty();
    }

    /**
     * Property 4.2: 告警级别必须是有效枚举值
     * 
     * 对于任意告警，告警级别必须是 INFO、WARNING、CRITICAL、EMERGENCY 之一。
     * 
     * Validates: Requirements BR-VAL-002
     */
    @Property
    @Label("Property 4.2: 告警级别必须是有效枚举值")
    void alarmLevelMustBeValidEnum(
        @ForAll("validAlarms") AlarmDO alarm
    ) {
        // Given: 有效的告警级别列表
        Set<String> validLevels = Set.of(
            AlarmLevelEnum.INFO.getLevel(),
            AlarmLevelEnum.WARNING.getLevel(),
            AlarmLevelEnum.CRITICAL.getLevel(),
            AlarmLevelEnum.EMERGENCY.getLevel()
        );
        
        // Then: 告警级别必须在有效列表中
        assertThat(alarm.getAlarmLevel())
            .as("告警级别必须是有效枚举值")
            .isIn(validLevels);
    }

    /**
     * Property 4.3: 告警来源必须是有效枚举值
     * 
     * 对于任意告警，告警来源必须是 SYSTEM 或 MANUAL。
     * 
     * Validates: Requirements BR-VAL-001
     */
    @Property
    @Label("Property 4.3: 告警来源必须是有效枚举值")
    void alarmSourceMustBeValidEnum(
        @ForAll("validAlarms") AlarmDO alarm
    ) {
        // Given: 有效的告警来源列表
        Set<String> validSources = Set.of(
            AlarmSourceEnum.SYSTEM.getSource(),
            AlarmSourceEnum.MANUAL.getSource()
        );
        
        // Then: 告警来源必须在有效列表中
        assertThat(alarm.getAlarmSource())
            .as("告警来源必须是有效枚举值")
            .isIn(validSources);
    }

    /**
     * Property 4.4: 告警状态必须是有效枚举值
     * 
     * 对于任意告警，告警状态必须是 PENDING、ACKNOWLEDGED、HANDLING、CLOSED 之一。
     * 
     * Validates: Requirements BR-VAL-001
     */
    @Property
    @Label("Property 4.4: 告警状态必须是有效枚举值")
    void alarmStatusMustBeValidEnum(
        @ForAll("validAlarms") AlarmDO alarm
    ) {
        // Given: 有效的告警状态列表
        Set<String> validStatuses = Set.of(
            AlarmStatusEnum.PENDING.getStatus(),
            AlarmStatusEnum.ACKNOWLEDGED.getStatus(),
            AlarmStatusEnum.HANDLING.getStatus(),
            AlarmStatusEnum.CLOSED.getStatus()
        );
        
        // Then: 告警状态必须在有效列表中
        assertThat(alarm.getAlarmStatus())
            .as("告警状态必须是有效枚举值")
            .isIn(validStatuses);
    }

    /**
     * Property 4.5: 告警编码格式正确性
     * 
     * 对于任意告警，告警编码必须符合格式：ALM-YYYYMMDD-XXXXX
     * 
     * Validates: Requirements BR-VAL-001
     */
    @Property
    @Label("Property 4.5: 告警编码格式正确性")
    void alarmCodeFormatMustBeCorrect(
        @ForAll("validAlarms") AlarmDO alarm
    ) {
        // Then: 告警编码必须符合格式
        String alarmCode = alarm.getAlarmCode();
        assertThat(alarmCode)
            .as("告警编码必须以 ALM- 开头")
            .startsWith("ALM-");
        
        // 验证格式：ALM-YYYYMMDD-XXXXX
        assertThat(alarmCode)
            .as("告警编码格式必须正确")
            .matches("ALM-\\d{8}-\\d{5}");
    }

    /**
     * Property 4.6: 缺少必填字段的告警无效
     * 
     * 对于任意缺少必填字段的告警，验证应该失败。
     * 
     * Validates: Requirements BR-VAL-001
     */
    @Property
    @Label("Property 4.6: 缺少必填字段的告警无效")
    void alarmWithMissingFieldsShouldBeInvalid(
        @ForAll("invalidAlarms") AlarmDO alarm
    ) {
        // When: 验证告警
        boolean isValid = validateAlarm(alarm);
        
        // Then: 缺少必填字段的告警应该无效
        assertThat(isValid)
            .as("缺少必填字段的告警应该无效")
            .isFalse();
    }

    /**
     * Property 4.7: 告警类型路径完整性
     * 
     * 对于任意有效告警，如果有告警类型ID，则应该有对应的类型路径。
     * 
     * Validates: Requirements BR-VAL-001
     */
    @Property
    @Label("Property 4.7: 告警类型路径完整性")
    void alarmTypeShouldHavePath(
        @ForAll("validAlarms") AlarmDO alarm
    ) {
        // Given: 告警有类型ID
        assertThat(alarm.getAlarmTypeId())
            .as("告警类型ID不能为空")
            .isNotNull();
        
        // Then: 应该有类型路径
        assertThat(alarm.getAlarmTypePath())
            .as("告警类型路径不能为空")
            .isNotNull()
            .isNotEmpty();
    }

    /**
     * Property 4.8: 设备信息完整性
     * 
     * 对于任意有效告警，如果有设备ID，则应该有设备名称。
     * 
     * Validates: Requirements BR-VAL-001
     */
    @Property
    @Label("Property 4.8: 设备信息完整性")
    void deviceInfoShouldBeComplete(
        @ForAll("validAlarms") AlarmDO alarm
    ) {
        // Given: 告警有设备ID
        assertThat(alarm.getDeviceId())
            .as("设备ID不能为空")
            .isNotNull();
        
        // Then: 应该有设备名称
        assertThat(alarm.getDeviceName())
            .as("设备名称不能为空")
            .isNotNull()
            .isNotEmpty();
    }

    /**
     * Property 4.9: 位置信息完整性
     * 
     * 对于任意有效告警，如果有位置ID，则应该有位置名称。
     * 
     * Validates: Requirements BR-VAL-001
     */
    @Property
    @Label("Property 4.9: 位置信息完整性")
    void locationInfoShouldBeComplete(
        @ForAll("validAlarms") AlarmDO alarm
    ) {
        // Given: 告警有位置ID
        assertThat(alarm.getLocationId())
            .as("位置ID不能为空")
            .isNotNull();
        
        // Then: 应该有位置名称
        assertThat(alarm.getLocationName())
            .as("位置名称不能为空")
            .isNotNull()
            .isNotEmpty();
    }

    /**
     * Property 4.10: 触发次数不变量
     * 
     * 对于任意告警，触发次数必须大于等于1。
     * 
     * Validates: Requirements BR-VAL-001
     */
    @Property
    @Label("Property 4.10: 触发次数不变量 - 必须大于等于1")
    void triggerCountMustBeAtLeastOne(
        @ForAll("validAlarms") AlarmDO alarm
    ) {
        // Then: 触发次数必须大于等于1
        assertThat(alarm.getTriggerCount())
            .as("触发次数必须大于等于1")
            .isGreaterThanOrEqualTo(1);
    }

    // ==================== 数据生成器 ====================

    /**
     * 生成有效的告警
     */
    @Provide
    Arbitrary<AlarmDO> validAlarms() {
        return Combinators.combine(
            Arbitraries.longs().between(1L, 1000L),      // deviceId
            Arbitraries.longs().between(1L, 100L),       // alarmTypeId
            Arbitraries.of(AlarmLevelEnum.values()),     // alarmLevel
            Arbitraries.of(AlarmSourceEnum.values()),    // alarmSource
            Arbitraries.of(AlarmStatusEnum.values()),    // alarmStatus
            Arbitraries.strings().alpha().ofMinLength(10).ofMaxLength(100) // alarmContent
        ).as((deviceId, alarmTypeId, level, source, status, content) -> {
            AlarmDO alarm = new AlarmDO();
            alarm.setId(idSequence.getAndIncrement());
            alarm.setAlarmCode(generateAlarmCode());
            alarm.setAlarmTypeId(alarmTypeId);
            alarm.setAlarmCategoryId(1L);
            alarm.setAlarmModelId(1L);
            alarm.setAlarmTypePath("环境告警 > 水位告警 > 水位超标");
            alarm.setAlarmLevel(level.getLevel());
            alarm.setAlarmStatus(status.getStatus());
            alarm.setAlarmSource(source.getSource());
            alarm.setAlarmContent(content);
            alarm.setDeviceId(deviceId);
            alarm.setDeviceName("设备-" + deviceId);
            alarm.setLocationId(1L);
            alarm.setLocationName("A区 > 1号防火分区");
            alarm.setTriggerValue("55");
            alarm.setThresholdValue("50");
            alarm.setTriggerCount(1);
            alarm.setEscalationLevel(0);
            alarm.setTenantId(1L);
            alarm.setDeleted(false);
            alarm.setCreateTime(LocalDateTime.now());
            return alarm;
        });
    }

    /**
     * 生成无效的告警（缺少必填字段）
     */
    @Provide
    Arbitrary<AlarmDO> invalidAlarms() {
        return Arbitraries.integers().between(0, 8).map(missingField -> {
            AlarmDO alarm = createValidAlarm();
            
            // 根据随机数决定缺少哪个必填字段
            switch (missingField) {
                case 0 -> alarm.setId(null);
                case 1 -> alarm.setAlarmCode(null);
                case 2 -> alarm.setAlarmTypeId(null);
                case 3 -> alarm.setAlarmLevel(null);
                case 4 -> alarm.setCreateTime(null);
                case 5 -> alarm.setAlarmContent(null);
                case 6 -> alarm.setDeviceId(null);
                case 7 -> alarm.setLocationId(null);
                case 8 -> alarm.setAlarmSource(null);
            }
            
            return alarm;
        });
    }

    // ==================== 辅助方法 ====================

    /**
     * 生成告警编码
     */
    private String generateAlarmCode() {
        LocalDateTime now = LocalDateTime.now();
        String dateStr = String.format("%04d%02d%02d", 
            now.getYear(), now.getMonthValue(), now.getDayOfMonth());
        return String.format("ALM-%s-%05d", dateStr, idSequence.get());
    }

    /**
     * 创建有效的告警
     */
    private AlarmDO createValidAlarm() {
        AlarmDO alarm = new AlarmDO();
        alarm.setId(idSequence.getAndIncrement());
        alarm.setAlarmCode(generateAlarmCode());
        alarm.setAlarmTypeId(1L);
        alarm.setAlarmCategoryId(1L);
        alarm.setAlarmModelId(1L);
        alarm.setAlarmTypePath("环境告警 > 水位告警 > 水位超标");
        alarm.setAlarmLevel(AlarmLevelEnum.WARNING.getLevel());
        alarm.setAlarmStatus(AlarmStatusEnum.PENDING.getStatus());
        alarm.setAlarmSource(AlarmSourceEnum.SYSTEM.getSource());
        alarm.setAlarmContent("测试告警内容");
        alarm.setDeviceId(1L);
        alarm.setDeviceName("测试设备");
        alarm.setLocationId(1L);
        alarm.setLocationName("测试位置");
        alarm.setTriggerValue("55");
        alarm.setThresholdValue("50");
        alarm.setTriggerCount(1);
        alarm.setEscalationLevel(0);
        alarm.setTenantId(1L);
        alarm.setDeleted(false);
        alarm.setCreateTime(LocalDateTime.now());
        return alarm;
    }

    /**
     * 验证告警是否有效
     */
    private boolean validateAlarm(AlarmDO alarm) {
        if (alarm.getId() == null) return false;
        if (alarm.getAlarmCode() == null || alarm.getAlarmCode().isEmpty()) return false;
        if (alarm.getAlarmTypeId() == null) return false;
        if (alarm.getAlarmLevel() == null || alarm.getAlarmLevel().isEmpty()) return false;
        if (alarm.getCreateTime() == null) return false;
        if (alarm.getAlarmContent() == null || alarm.getAlarmContent().isEmpty()) return false;
        if (alarm.getDeviceId() == null) return false;
        if (alarm.getLocationId() == null) return false;
        if (alarm.getAlarmSource() == null || alarm.getAlarmSource().isEmpty()) return false;
        return true;
    }

}
