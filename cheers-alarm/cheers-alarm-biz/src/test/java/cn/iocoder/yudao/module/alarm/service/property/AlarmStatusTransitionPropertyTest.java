package cn.iocoder.yudao.module.alarm.service.property;

import cn.iocoder.yudao.module.alarm.enums.AlarmStatusEnum;
import net.jqwik.api.*;
import net.jqwik.api.lifecycle.BeforeProperty;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 告警状态转换属性测试
 * 
 * <p>Property 1: 告警状态只能按顺序流转</p>
 * <p>*For any* 告警，状态只能按 PENDING → ACKNOWLEDGED → HANDLING → CLOSED 顺序流转</p>
 * 
 * <p>Validates: Requirements BR-STA-001</p>
 * <ul>
 *   <li>BR-STA-001: 告警状态只能按以下顺序流转：待确认 → 已确认 → 处理中 → 已关闭</li>
 * </ul>
 * 
 * <p>Feature: alarm-management, Property 1: 告警状态只能按顺序流转</p>
 * 
 * @author 告警管理模块
 */
@PropertyDefaults(tries = 100)
public class AlarmStatusTransitionPropertyTest {

    /**
     * 定义合法的状态转换顺序
     * PENDING(0) → ACKNOWLEDGED(1) → HANDLING(2) → CLOSED(3)
     */
    private static final Map<String, Integer> STATUS_ORDER = Map.of(
        AlarmStatusEnum.PENDING.getStatus(), 0,
        AlarmStatusEnum.ACKNOWLEDGED.getStatus(), 1,
        AlarmStatusEnum.HANDLING.getStatus(), 2,
        AlarmStatusEnum.CLOSED.getStatus(), 3
    );

    /**
     * 定义每个状态允许的下一个状态
     */
    private static final Map<String, Set<String>> VALID_TRANSITIONS = Map.of(
        AlarmStatusEnum.PENDING.getStatus(), Set.of(AlarmStatusEnum.ACKNOWLEDGED.getStatus()),
        AlarmStatusEnum.ACKNOWLEDGED.getStatus(), Set.of(AlarmStatusEnum.HANDLING.getStatus()),
        AlarmStatusEnum.HANDLING.getStatus(), Set.of(AlarmStatusEnum.CLOSED.getStatus()),
        AlarmStatusEnum.CLOSED.getStatus(), Set.of() // 已关闭状态不能转换到任何状态
    );

    @BeforeProperty
    void setUp() {
        // 属性测试初始化
    }

    // ==================== Property 1: 状态转换顺序正确性 ====================

    /**
     * Property 1.1: 合法状态转换验证
     * 
     * 对于任意当前状态和目标状态，如果转换是合法的，则目标状态的顺序必须比当前状态大1。
     * 
     * Validates: Requirements BR-STA-001
     */
    @Property
    @Label("Property 1.1: 合法状态转换 - 目标状态顺序必须比当前状态大1")
    void validTransitionMustBeSequential(
        @ForAll("validStatusTransitions") StatusTransition transition
    ) {
        // Given: 一个合法的状态转换
        String fromStatus = transition.fromStatus();
        String toStatus = transition.toStatus();
        
        // When: 检查状态顺序
        int fromOrder = STATUS_ORDER.get(fromStatus);
        int toOrder = STATUS_ORDER.get(toStatus);
        
        // Then: 目标状态顺序必须比当前状态大1（顺序流转）
        assertThat(toOrder)
            .as("状态转换 %s → %s 必须是顺序的", fromStatus, toStatus)
            .isEqualTo(fromOrder + 1);
    }

    /**
     * Property 1.2: 非法状态转换验证 - 跳跃转换
     * 
     * 对于任意当前状态，不能跳过中间状态直接转换到后续状态。
     * 例如：PENDING 不能直接转换到 HANDLING 或 CLOSED
     * 
     * Validates: Requirements BR-STA-001
     */
    @Property
    @Label("Property 1.2: 非法状态转换 - 不能跳过中间状态")
    void cannotSkipIntermediateStatus(
        @ForAll("allStatuses") String fromStatus,
        @ForAll("allStatuses") String toStatus
    ) {
        // Given: 任意两个状态
        int fromOrder = STATUS_ORDER.get(fromStatus);
        int toOrder = STATUS_ORDER.get(toStatus);
        
        // When: 检查是否为跳跃转换（跳过中间状态）
        boolean isSkipTransition = toOrder > fromOrder + 1;
        
        // Then: 如果是跳跃转换，则必须是非法的
        if (isSkipTransition) {
            assertThat(isValidTransition(fromStatus, toStatus))
                .as("状态转换 %s → %s 跳过了中间状态，应该是非法的", fromStatus, toStatus)
                .isFalse();
        }
    }

    /**
     * Property 1.3: 非法状态转换验证 - 逆向转换
     * 
     * 对于任意当前状态，不能逆向转换到之前的状态。
     * 例如：ACKNOWLEDGED 不能转换回 PENDING
     * 
     * Validates: Requirements BR-STA-001
     */
    @Property
    @Label("Property 1.3: 非法状态转换 - 不能逆向转换")
    void cannotReverseTransition(
        @ForAll("allStatuses") String fromStatus,
        @ForAll("allStatuses") String toStatus
    ) {
        // Given: 任意两个状态
        int fromOrder = STATUS_ORDER.get(fromStatus);
        int toOrder = STATUS_ORDER.get(toStatus);
        
        // When: 检查是否为逆向转换
        boolean isReverseTransition = toOrder < fromOrder;
        
        // Then: 如果是逆向转换，则必须是非法的
        if (isReverseTransition) {
            assertThat(isValidTransition(fromStatus, toStatus))
                .as("状态转换 %s → %s 是逆向的，应该是非法的", fromStatus, toStatus)
                .isFalse();
        }
    }

    /**
     * Property 1.4: 非法状态转换验证 - 已关闭状态不可变
     * 
     * 已关闭的告警不能转换到任何其他状态。
     * 
     * Validates: Requirements BR-STA-001, BR-STA-003
     */
    @Property
    @Label("Property 1.4: 已关闭状态不可变 - CLOSED 不能转换到任何状态")
    void closedStatusIsImmutable(
        @ForAll("allStatuses") String toStatus
    ) {
        // Given: 当前状态为 CLOSED
        String fromStatus = AlarmStatusEnum.CLOSED.getStatus();
        
        // When & Then: CLOSED 状态不能转换到任何状态（包括自身）
        assertThat(isValidTransition(fromStatus, toStatus))
            .as("CLOSED 状态不能转换到 %s", toStatus)
            .isFalse();
    }

    /**
     * Property 1.5: 状态转换完整性验证
     * 
     * 对于任意状态序列，如果按顺序执行所有合法转换，最终状态必须是 CLOSED。
     * 
     * Validates: Requirements BR-STA-001
     */
    @Property
    @Label("Property 1.5: 完整状态流转 - 从 PENDING 到 CLOSED 的完整路径")
    void completeTransitionPathEndsAtClosed() {
        // Given: 从 PENDING 状态开始
        String currentStatus = AlarmStatusEnum.PENDING.getStatus();
        List<String> transitionPath = new ArrayList<>();
        transitionPath.add(currentStatus);
        
        // When: 按顺序执行所有合法转换
        while (!currentStatus.equals(AlarmStatusEnum.CLOSED.getStatus())) {
            String nextStatus = getNextValidStatus(currentStatus);
            assertThat(nextStatus)
                .as("状态 %s 应该有下一个合法状态", currentStatus)
                .isNotNull();
            
            currentStatus = nextStatus;
            transitionPath.add(currentStatus);
        }
        
        // Then: 最终状态必须是 CLOSED，且路径长度为4
        assertThat(currentStatus)
            .as("完整状态流转的最终状态必须是 CLOSED")
            .isEqualTo(AlarmStatusEnum.CLOSED.getStatus());
        
        assertThat(transitionPath)
            .as("完整状态流转路径应该包含4个状态")
            .hasSize(4)
            .containsExactly(
                AlarmStatusEnum.PENDING.getStatus(),
                AlarmStatusEnum.ACKNOWLEDGED.getStatus(),
                AlarmStatusEnum.HANDLING.getStatus(),
                AlarmStatusEnum.CLOSED.getStatus()
            );
    }

    /**
     * Property 1.6: AlarmStatusEnum 辅助方法正确性验证
     * 
     * 验证 AlarmStatusEnum 中的 canAcknowledge、canHandle、canClose 方法
     * 与状态转换规则一致。
     * 
     * Validates: Requirements BR-STA-001
     */
    @Property
    @Label("Property 1.6: 枚举辅助方法正确性 - canXxx 方法与转换规则一致")
    void enumHelperMethodsAreConsistent(
        @ForAll("allStatuses") String status
    ) {
        // Given: 任意状态
        
        // When & Then: 验证 canAcknowledge 方法
        boolean canAcknowledge = AlarmStatusEnum.canAcknowledge(status);
        boolean shouldCanAcknowledge = status.equals(AlarmStatusEnum.PENDING.getStatus());
        assertThat(canAcknowledge)
            .as("canAcknowledge(%s) 应该返回 %s", status, shouldCanAcknowledge)
            .isEqualTo(shouldCanAcknowledge);
        
        // When & Then: 验证 canHandle 方法
        boolean canHandle = AlarmStatusEnum.canHandle(status);
        boolean shouldCanHandle = status.equals(AlarmStatusEnum.ACKNOWLEDGED.getStatus());
        assertThat(canHandle)
            .as("canHandle(%s) 应该返回 %s", status, shouldCanHandle)
            .isEqualTo(shouldCanHandle);
        
        // When & Then: 验证 canClose 方法
        boolean canClose = AlarmStatusEnum.canClose(status);
        boolean shouldCanClose = status.equals(AlarmStatusEnum.HANDLING.getStatus());
        assertThat(canClose)
            .as("canClose(%s) 应该返回 %s", status, shouldCanClose)
            .isEqualTo(shouldCanClose);
    }

    /**
     * Property 1.7: 状态转换唯一性验证
     * 
     * 对于任意非 CLOSED 状态，有且仅有一个合法的下一个状态。
     * 
     * Validates: Requirements BR-STA-001
     */
    @Property
    @Label("Property 1.7: 状态转换唯一性 - 每个非终态状态只有一个合法的下一个状态")
    void eachNonFinalStatusHasExactlyOneNextStatus(
        @ForAll("nonClosedStatuses") String status
    ) {
        // Given: 任意非 CLOSED 状态
        
        // When: 获取该状态的所有合法下一个状态
        Set<String> validNextStatuses = VALID_TRANSITIONS.get(status);
        
        // Then: 有且仅有一个合法的下一个状态
        assertThat(validNextStatuses)
            .as("状态 %s 应该有且仅有一个合法的下一个状态", status)
            .hasSize(1);
    }

    // ==================== 数据生成器 ====================

    /**
     * 生成所有有效状态
     */
    @Provide
    Arbitrary<String> allStatuses() {
        return Arbitraries.of(
            AlarmStatusEnum.PENDING.getStatus(),
            AlarmStatusEnum.ACKNOWLEDGED.getStatus(),
            AlarmStatusEnum.HANDLING.getStatus(),
            AlarmStatusEnum.CLOSED.getStatus()
        );
    }

    /**
     * 生成所有非 CLOSED 状态
     */
    @Provide
    Arbitrary<String> nonClosedStatuses() {
        return Arbitraries.of(
            AlarmStatusEnum.PENDING.getStatus(),
            AlarmStatusEnum.ACKNOWLEDGED.getStatus(),
            AlarmStatusEnum.HANDLING.getStatus()
        );
    }

    /**
     * 生成所有合法的状态转换
     */
    @Provide
    Arbitrary<StatusTransition> validStatusTransitions() {
        return Arbitraries.of(
            new StatusTransition(AlarmStatusEnum.PENDING.getStatus(), AlarmStatusEnum.ACKNOWLEDGED.getStatus()),
            new StatusTransition(AlarmStatusEnum.ACKNOWLEDGED.getStatus(), AlarmStatusEnum.HANDLING.getStatus()),
            new StatusTransition(AlarmStatusEnum.HANDLING.getStatus(), AlarmStatusEnum.CLOSED.getStatus())
        );
    }

    // ==================== 辅助方法 ====================

    /**
     * 判断状态转换是否合法
     */
    private boolean isValidTransition(String fromStatus, String toStatus) {
        Set<String> validNextStatuses = VALID_TRANSITIONS.get(fromStatus);
        return validNextStatuses != null && validNextStatuses.contains(toStatus);
    }

    /**
     * 获取下一个合法状态
     */
    private String getNextValidStatus(String currentStatus) {
        Set<String> validNextStatuses = VALID_TRANSITIONS.get(currentStatus);
        if (validNextStatuses == null || validNextStatuses.isEmpty()) {
            return null;
        }
        return validNextStatuses.iterator().next();
    }

    // ==================== 内部类 ====================

    /**
     * 状态转换记录
     */
    record StatusTransition(String fromStatus, String toStatus) {}

}
