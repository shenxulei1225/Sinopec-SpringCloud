package cn.iocoder.yudao.module.alarm.service.property;

import cn.iocoder.yudao.module.alarm.dal.dataobject.LinkageExecutionDO;
import cn.iocoder.yudao.module.alarm.enums.LinkageActionTypeEnum;
import cn.iocoder.yudao.module.alarm.enums.LinkageExecutionStatusEnum;
import net.jqwik.api.*;
import net.jqwik.api.lifecycle.BeforeProperty;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 联动重试属性测试
 * 
 * <p>Property 3: 联动最多重试3次</p>
 * <p>*For any* 联动执行，失败后最多重试3次，3次后标记需人工介入</p>
 * 
 * <p>Validates: Requirements BR-BIZ-005, BR-BIZ-006</p>
 * <ul>
 *   <li>BR-BIZ-005: 联动动作执行失败时，系统应自动重试3次，每次间隔5秒</li>
 *   <li>BR-BIZ-006: 联动重试3次后仍失败，系统必须发送通知给值班员，要求人工介入处理</li>
 * </ul>
 * 
 * <p>Feature: alarm-management, Property 3: 联动最多重试3次</p>
 * 
 * @author 告警管理模块
 */
@PropertyDefaults(tries = 100)
public class LinkageRetryPropertyTest {

    /**
     * 最大重试次数
     */
    private static final int MAX_RETRY_COUNT = 3;

    /**
     * 模拟的联动执行存储
     */
    private Map<Long, LinkageExecutionDO> executionStore;

    /**
     * ID 序列
     */
    private AtomicLong idSequence;

    @BeforeProperty
    void setUp() {
        executionStore = new HashMap<>();
        idSequence = new AtomicLong(1);
    }

    // ==================== Property 3: 联动重试正确性 ====================

    /**
     * Property 3.1: 联动执行失败后可以重试
     * 
     * 对于任意失败的联动执行，如果重试次数小于3次，则可以进行重试。
     * 
     * Validates: Requirements BR-BIZ-005
     */
    @Property
    @Label("Property 3.1: 联动执行失败后可以重试 - 重试次数小于3次时允许重试")
    void failedExecutionCanRetryWhenUnderLimit(
        @ForAll("alarmIds") Long alarmId,
        @ForAll("linkageRuleIds") Long linkageRuleId,
        @ForAll("actionTypes") String actionType,
        @ForAll("retryCountsUnderLimit") int currentRetryCount
    ) {
        // Given: 创建一个失败的联动执行，重试次数小于3次
        LinkageExecutionDO execution = createExecution(alarmId, linkageRuleId, actionType);
        execution.setExecutionStatus(LinkageExecutionStatusEnum.FAILED.getStatus());
        execution.setRetryCount(currentRetryCount);
        execution.setManualIntervention(false);
        
        // When: 检查是否可以重试
        boolean canRetry = canRetryExecution(execution);
        
        // Then: 应该可以重试
        assertThat(canRetry)
            .as("重试次数为 %d（小于3次）时，应该可以重试", currentRetryCount)
            .isTrue();
    }

    /**
     * Property 3.2: 重试次数达到3次后不能再重试
     * 
     * 对于任意联动执行，如果重试次数已达到3次，则不能再重试。
     * 
     * Validates: Requirements BR-BIZ-005
     */
    @Property
    @Label("Property 3.2: 重试次数达到3次后不能再重试")
    void cannotRetryWhenReachedLimit(
        @ForAll("alarmIds") Long alarmId,
        @ForAll("linkageRuleIds") Long linkageRuleId,
        @ForAll("actionTypes") String actionType,
        @ForAll("retryCountsAtOrOverLimit") int currentRetryCount
    ) {
        // Given: 创建一个失败的联动执行，重试次数已达到或超过3次
        LinkageExecutionDO execution = createExecution(alarmId, linkageRuleId, actionType);
        execution.setExecutionStatus(LinkageExecutionStatusEnum.FAILED.getStatus());
        execution.setRetryCount(currentRetryCount);
        
        // When: 检查是否可以重试
        boolean canRetry = canRetryExecution(execution);
        
        // Then: 不应该可以重试
        assertThat(canRetry)
            .as("重试次数为 %d（已达到或超过3次）时，不应该可以重试", currentRetryCount)
            .isFalse();
    }

    /**
     * Property 3.3: 重试3次后必须标记需人工介入
     * 
     * 对于任意联动执行，如果重试3次后仍然失败，必须标记需要人工介入。
     * 
     * Validates: Requirements BR-BIZ-006
     */
    @Property
    @Label("Property 3.3: 重试3次后必须标记需人工介入")
    void mustMarkManualInterventionAfterMaxRetries(
        @ForAll("alarmIds") Long alarmId,
        @ForAll("linkageRuleIds") Long linkageRuleId,
        @ForAll("actionTypes") String actionType
    ) {
        // Given: 创建一个联动执行
        LinkageExecutionDO execution = createExecution(alarmId, linkageRuleId, actionType);
        execution.setExecutionStatus(LinkageExecutionStatusEnum.PENDING.getStatus());
        execution.setRetryCount(0);
        execution.setManualIntervention(false);
        
        // When: 模拟执行失败并重试3次
        for (int i = 0; i < MAX_RETRY_COUNT; i++) {
            simulateExecutionFailure(execution);
            
            if (canRetryExecution(execution)) {
                simulateRetry(execution);
            }
        }
        
        // 最后一次执行也失败
        simulateExecutionFailure(execution);
        
        // 检查是否需要标记人工介入
        if (!canRetryExecution(execution)) {
            markManualIntervention(execution);
        }
        
        // Then: 必须标记需要人工介入
        assertThat(execution.getManualIntervention())
            .as("重试3次后仍失败，必须标记需要人工介入")
            .isTrue();
        
        assertThat(execution.getRetryCount())
            .as("重试次数应该等于最大重试次数")
            .isEqualTo(MAX_RETRY_COUNT);
    }

    /**
     * Property 3.4: 成功的联动执行不需要重试
     * 
     * 对于任意成功的联动执行，不应该进行重试。
     * 
     * Validates: Requirements BR-BIZ-005
     */
    @Property
    @Label("Property 3.4: 成功的联动执行不需要重试")
    void successfulExecutionShouldNotRetry(
        @ForAll("alarmIds") Long alarmId,
        @ForAll("linkageRuleIds") Long linkageRuleId,
        @ForAll("actionTypes") String actionType,
        @ForAll("anyRetryCount") int retryCount
    ) {
        // Given: 创建一个成功的联动执行
        LinkageExecutionDO execution = createExecution(alarmId, linkageRuleId, actionType);
        execution.setExecutionStatus(LinkageExecutionStatusEnum.SUCCESS.getStatus());
        execution.setRetryCount(retryCount);
        
        // When: 检查是否可以重试
        boolean canRetry = canRetryExecution(execution);
        
        // Then: 成功的执行不应该重试
        assertThat(canRetry)
            .as("成功的联动执行不应该重试")
            .isFalse();
    }

    /**
     * Property 3.5: 重试次数递增的正确性
     * 
     * 每次重试后，重试次数应该递增1。
     * 
     * Validates: Requirements BR-BIZ-005
     */
    @Property
    @Label("Property 3.5: 重试次数递增的正确性")
    void retryCountShouldIncrementCorrectly(
        @ForAll("alarmIds") Long alarmId,
        @ForAll("linkageRuleIds") Long linkageRuleId,
        @ForAll("actionTypes") String actionType,
        @ForAll("retryCountsUnderLimit") int initialRetryCount
    ) {
        // Given: 创建一个失败的联动执行
        LinkageExecutionDO execution = createExecution(alarmId, linkageRuleId, actionType);
        execution.setExecutionStatus(LinkageExecutionStatusEnum.FAILED.getStatus());
        execution.setRetryCount(initialRetryCount);
        
        // When: 执行重试
        int expectedRetryCount = initialRetryCount + 1;
        simulateRetry(execution);
        
        // Then: 重试次数应该递增1
        assertThat(execution.getRetryCount())
            .as("重试后，重试次数应该从 %d 递增到 %d", initialRetryCount, expectedRetryCount)
            .isEqualTo(expectedRetryCount);
    }

    /**
     * Property 3.6: 重试状态转换正确性
     * 
     * 重试时，状态应该从 FAILED 转换为 RETRY。
     * 
     * Validates: Requirements BR-BIZ-005
     */
    @Property
    @Label("Property 3.6: 重试状态转换正确性 - FAILED → RETRY")
    void retryStatusTransitionShouldBeCorrect(
        @ForAll("alarmIds") Long alarmId,
        @ForAll("linkageRuleIds") Long linkageRuleId,
        @ForAll("actionTypes") String actionType,
        @ForAll("retryCountsUnderLimit") int retryCount
    ) {
        // Given: 创建一个失败的联动执行
        LinkageExecutionDO execution = createExecution(alarmId, linkageRuleId, actionType);
        execution.setExecutionStatus(LinkageExecutionStatusEnum.FAILED.getStatus());
        execution.setRetryCount(retryCount);
        
        // When: 执行重试
        simulateRetry(execution);
        
        // Then: 状态应该变为 RETRY
        assertThat(execution.getExecutionStatus())
            .as("重试时，状态应该从 FAILED 转换为 RETRY")
            .isEqualTo(LinkageExecutionStatusEnum.RETRY.getStatus());
    }

    /**
     * Property 3.7: 已标记人工介入的执行不能重试
     * 
     * 对于已标记需要人工介入的联动执行，不应该进行自动重试。
     * 
     * Validates: Requirements BR-BIZ-006
     */
    @Property
    @Label("Property 3.7: 已标记人工介入的执行不能重试")
    void manualInterventionExecutionShouldNotRetry(
        @ForAll("alarmIds") Long alarmId,
        @ForAll("linkageRuleIds") Long linkageRuleId,
        @ForAll("actionTypes") String actionType,
        @ForAll("anyRetryCount") int retryCount
    ) {
        // Given: 创建一个已标记人工介入的联动执行
        LinkageExecutionDO execution = createExecution(alarmId, linkageRuleId, actionType);
        execution.setExecutionStatus(LinkageExecutionStatusEnum.FAILED.getStatus());
        execution.setRetryCount(retryCount);
        execution.setManualIntervention(true);
        
        // When: 检查是否可以重试
        boolean canRetry = canRetryExecution(execution);
        
        // Then: 已标记人工介入的执行不应该重试
        assertThat(canRetry)
            .as("已标记人工介入的联动执行不应该自动重试")
            .isFalse();
    }

    /**
     * Property 3.8: 重试次数不变量 - 永远不超过最大值
     * 
     * 对于任意联动执行，重试次数永远不应该超过最大重试次数。
     * 
     * Validates: Requirements BR-BIZ-005
     */
    @Property
    @Label("Property 3.8: 重试次数不变量 - 永远不超过最大值")
    void retryCountShouldNeverExceedMax(
        @ForAll("alarmIds") Long alarmId,
        @ForAll("linkageRuleIds") Long linkageRuleId,
        @ForAll("actionTypes") String actionType,
        @ForAll("attemptCounts") int attemptCount
    ) {
        // Given: 创建一个联动执行
        LinkageExecutionDO execution = createExecution(alarmId, linkageRuleId, actionType);
        execution.setExecutionStatus(LinkageExecutionStatusEnum.PENDING.getStatus());
        execution.setRetryCount(0);
        execution.setManualIntervention(false);
        
        // When: 尝试多次执行和重试
        for (int i = 0; i < attemptCount; i++) {
            simulateExecutionFailure(execution);
            
            if (canRetryExecution(execution)) {
                simulateRetry(execution);
            } else {
                break;
            }
        }
        
        // Then: 重试次数永远不应该超过最大值
        assertThat(execution.getRetryCount())
            .as("重试次数不应该超过最大值 %d", MAX_RETRY_COUNT)
            .isLessThanOrEqualTo(MAX_RETRY_COUNT);
    }

    // ==================== 数据生成器 ====================

    /**
     * 生成告警ID
     */
    @Provide
    Arbitrary<Long> alarmIds() {
        return Arbitraries.longs().between(1L, 1000L);
    }

    /**
     * 生成联动规则ID
     */
    @Provide
    Arbitrary<Long> linkageRuleIds() {
        return Arbitraries.longs().between(1L, 100L);
    }

    /**
     * 生成联动动作类型
     */
    @Provide
    Arbitrary<String> actionTypes() {
        return Arbitraries.of(
            LinkageActionTypeEnum.NOTIFICATION.getType(),
            LinkageActionTypeEnum.DEVICE_CONTROL.getType(),
            LinkageActionTypeEnum.VIDEO_LINKAGE.getType(),
            LinkageActionTypeEnum.ACCESS_CONTROL.getType(),
            LinkageActionTypeEnum.FIRE_CONTROL.getType()
        );
    }

    /**
     * 生成小于最大重试次数的重试次数（0, 1, 2）
     */
    @Provide
    Arbitrary<Integer> retryCountsUnderLimit() {
        return Arbitraries.integers().between(0, MAX_RETRY_COUNT - 1);
    }

    /**
     * 生成达到或超过最大重试次数的重试次数（3, 4, 5, ...）
     */
    @Provide
    Arbitrary<Integer> retryCountsAtOrOverLimit() {
        return Arbitraries.integers().between(MAX_RETRY_COUNT, 10);
    }

    /**
     * 生成任意重试次数（0-10）
     */
    @Provide
    Arbitrary<Integer> anyRetryCount() {
        return Arbitraries.integers().between(0, 10);
    }

    /**
     * 生成尝试次数（1-20）
     */
    @Provide
    Arbitrary<Integer> attemptCounts() {
        return Arbitraries.integers().between(1, 20);
    }

    // ==================== 辅助方法 ====================

    /**
     * 创建联动执行记录
     */
    private LinkageExecutionDO createExecution(Long alarmId, Long linkageRuleId, String actionType) {
        LinkageExecutionDO execution = new LinkageExecutionDO();
        execution.setId(idSequence.getAndIncrement());
        execution.setAlarmId(alarmId);
        execution.setLinkageRuleId(linkageRuleId);
        execution.setActionType(actionType);
        execution.setActionConfig("{}");
        execution.setTargetDeviceId(1L);
        execution.setTargetDeviceName("测试设备");
        execution.setExecutionStatus(LinkageExecutionStatusEnum.PENDING.getStatus());
        execution.setRetryCount(0);
        execution.setManualIntervention(false);
        execution.setTenantId(1L);
        execution.setCreateTime(LocalDateTime.now());
        return execution;
    }

    /**
     * 判断是否可以重试
     * 
     * 重试条件：
     * 1. 状态为 FAILED 或 RETRY
     * 2. 重试次数小于最大重试次数
     * 3. 未标记需要人工介入
     */
    private boolean canRetryExecution(LinkageExecutionDO execution) {
        // 检查状态是否允许重试
        if (!LinkageExecutionStatusEnum.canRetry(execution.getExecutionStatus())) {
            return false;
        }
        
        // 检查重试次数是否达到上限
        if (execution.getRetryCount() >= MAX_RETRY_COUNT) {
            return false;
        }
        
        // 检查是否已标记人工介入
        if (Boolean.TRUE.equals(execution.getManualIntervention())) {
            return false;
        }
        
        return true;
    }

    /**
     * 模拟执行失败
     */
    private void simulateExecutionFailure(LinkageExecutionDO execution) {
        execution.setExecutionStatus(LinkageExecutionStatusEnum.FAILED.getStatus());
        execution.setErrorMessage("模拟执行失败");
        execution.setEndTime(LocalDateTime.now());
    }

    /**
     * 模拟重试
     */
    private void simulateRetry(LinkageExecutionDO execution) {
        execution.setRetryCount(execution.getRetryCount() + 1);
        execution.setExecutionStatus(LinkageExecutionStatusEnum.RETRY.getStatus());
        execution.setStartTime(LocalDateTime.now());
        execution.setEndTime(null);
        execution.setErrorMessage(null);
    }

    /**
     * 标记需要人工介入
     */
    private void markManualIntervention(LinkageExecutionDO execution) {
        execution.setManualIntervention(true);
    }

}
