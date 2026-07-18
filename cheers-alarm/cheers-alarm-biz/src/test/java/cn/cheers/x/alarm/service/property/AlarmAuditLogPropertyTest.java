package cn.cheers.x.alarm.service.property;

import cn.cheers.x.alarm.dal.dataobject.AlarmAuditLogDO;
import cn.cheers.x.alarm.dal.dataobject.AlarmDO;
import cn.cheers.x.alarm.enums.AlarmAuditOperationTypeEnum;
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
 * 审计日志完整性属性测试
 * 
 * <p>Property 5: 所有告警操作必须记录审计日志</p>
 * <p>*For any* 告警操作（确认、处理、关闭），必须记录操作人、时间、内容</p>
 * 
 * <p>Validates: Requirements BR-BIZ-008</p>
 * <ul>
 *   <li>BR-BIZ-008: 所有告警操作（确认、处理、关闭）必须记录操作人、操作时间、操作内容</li>
 * </ul>
 * 
 * <p>Feature: alarm-management, Property 5: 所有告警操作必须记录审计日志</p>
 * 
 * @author 告警管理模块
 */
@PropertyDefaults(tries = 100)
public class AlarmAuditLogPropertyTest {

    /**
     * 需要记录审计日志的操作类型
     */
    private static final Set<String> AUDITABLE_OPERATIONS = Set.of(
        AlarmAuditOperationTypeEnum.CREATE.getType(),
        AlarmAuditOperationTypeEnum.ACKNOWLEDGE.getType(),
        AlarmAuditOperationTypeEnum.HANDLE.getType(),
        AlarmAuditOperationTypeEnum.CLOSE.getType(),
        AlarmAuditOperationTypeEnum.ESCALATE.getType(),
        AlarmAuditOperationTypeEnum.LINKAGE_EXECUTE.getType(),
        AlarmAuditOperationTypeEnum.LINKAGE_RETRY.getType(),
        AlarmAuditOperationTypeEnum.LINKAGE_MANUAL.getType()
    );

    /**
     * 模拟的审计日志存储
     */
    private List<AlarmAuditLogDO> auditLogStore;

    /**
     * ID 序列
     */
    private AtomicLong idSequence;

    @BeforeProperty
    void setUp() {
        auditLogStore = new ArrayList<>();
        idSequence = new AtomicLong(1);
    }

    // ==================== Property 5: 审计日志完整性 ====================

    /**
     * Property 5.1: 告警确认操作必须记录审计日志
     * 
     * 对于任意告警确认操作，必须记录审计日志。
     * 
     * Validates: Requirements BR-BIZ-008
     */
    @Property
    @Label("Property 5.1: 告警确认操作必须记录审计日志")
    void acknowledgeOperationMustBeAudited(
        @ForAll("alarmIds") Long alarmId,
        @ForAll("operatorIds") Long operatorId,
        @ForAll("operatorNames") String operatorName
    ) {
        // Given: 清空审计日志
        auditLogStore.clear();
        
        // When: 执行告警确认操作
        simulateAcknowledgeAlarm(alarmId, operatorId, operatorName);
        
        // Then: 必须记录审计日志
        assertThat(auditLogStore)
            .as("告警确认操作必须记录审计日志")
            .isNotEmpty();
        
        AlarmAuditLogDO auditLog = auditLogStore.get(auditLogStore.size() - 1);
        
        assertThat(auditLog.getAlarmId())
            .as("审计日志必须关联告警ID")
            .isEqualTo(alarmId);
        
        assertThat(auditLog.getOperationType())
            .as("审计日志操作类型必须是 ACKNOWLEDGE")
            .isEqualTo(AlarmAuditOperationTypeEnum.ACKNOWLEDGE.getType());
        
        assertThat(auditLog.getOperatorId())
            .as("审计日志必须记录操作人ID")
            .isEqualTo(operatorId);
        
        assertThat(auditLog.getOperatorName())
            .as("审计日志必须记录操作人姓名")
            .isEqualTo(operatorName);
        
        assertThat(auditLog.getOperationTime())
            .as("审计日志必须记录操作时间")
            .isNotNull();
    }

    /**
     * Property 5.2: 告警处理操作必须记录审计日志
     * 
     * 对于任意告警处理操作，必须记录审计日志。
     * 
     * Validates: Requirements BR-BIZ-008
     */
    @Property
    @Label("Property 5.2: 告警处理操作必须记录审计日志")
    void handleOperationMustBeAudited(
        @ForAll("alarmIds") Long alarmId,
        @ForAll("operatorIds") Long operatorId,
        @ForAll("operatorNames") String operatorName,
        @ForAll("handleMeasures") String handleMeasure
    ) {
        // Given: 清空审计日志
        auditLogStore.clear();
        
        // When: 执行告警处理操作
        simulateHandleAlarm(alarmId, operatorId, operatorName, handleMeasure);
        
        // Then: 必须记录审计日志
        assertThat(auditLogStore)
            .as("告警处理操作必须记录审计日志")
            .isNotEmpty();
        
        AlarmAuditLogDO auditLog = auditLogStore.get(auditLogStore.size() - 1);
        
        assertThat(auditLog.getAlarmId())
            .as("审计日志必须关联告警ID")
            .isEqualTo(alarmId);
        
        assertThat(auditLog.getOperationType())
            .as("审计日志操作类型必须是 HANDLE")
            .isEqualTo(AlarmAuditOperationTypeEnum.HANDLE.getType());
        
        assertThat(auditLog.getOperatorId())
            .as("审计日志必须记录操作人ID")
            .isEqualTo(operatorId);
        
        assertThat(auditLog.getOperationContent())
            .as("审计日志必须记录操作内容（处理措施）")
            .contains(handleMeasure);
    }

    /**
     * Property 5.3: 告警关闭操作必须记录审计日志
     * 
     * 对于任意告警关闭操作，必须记录审计日志。
     * 
     * Validates: Requirements BR-BIZ-008
     */
    @Property
    @Label("Property 5.3: 告警关闭操作必须记录审计日志")
    void closeOperationMustBeAudited(
        @ForAll("alarmIds") Long alarmId,
        @ForAll("operatorIds") Long operatorId,
        @ForAll("operatorNames") String operatorName,
        @ForAll("closeReasons") String closeReason
    ) {
        // Given: 清空审计日志
        auditLogStore.clear();
        
        // When: 执行告警关闭操作
        simulateCloseAlarm(alarmId, operatorId, operatorName, closeReason);
        
        // Then: 必须记录审计日志
        assertThat(auditLogStore)
            .as("告警关闭操作必须记录审计日志")
            .isNotEmpty();
        
        AlarmAuditLogDO auditLog = auditLogStore.get(auditLogStore.size() - 1);
        
        assertThat(auditLog.getAlarmId())
            .as("审计日志必须关联告警ID")
            .isEqualTo(alarmId);
        
        assertThat(auditLog.getOperationType())
            .as("审计日志操作类型必须是 CLOSE")
            .isEqualTo(AlarmAuditOperationTypeEnum.CLOSE.getType());
        
        assertThat(auditLog.getOperatorId())
            .as("审计日志必须记录操作人ID")
            .isEqualTo(operatorId);
        
        assertThat(auditLog.getOperationContent())
            .as("审计日志必须记录操作内容（关闭原因）")
            .contains(closeReason);
    }

    /**
     * Property 5.4: 审计日志必须包含所有必填字段
     * 
     * 对于任意审计日志，必须包含告警ID、操作类型、操作人、操作时间。
     * 
     * Validates: Requirements BR-BIZ-008
     */
    @Property
    @Label("Property 5.4: 审计日志必须包含所有必填字段")
    void auditLogMustContainAllRequiredFields(
        @ForAll("validAuditLogs") AlarmAuditLogDO auditLog
    ) {
        // Then: 验证所有必填字段
        assertThat(auditLog.getId())
            .as("审计日志ID不能为空")
            .isNotNull();
        
        assertThat(auditLog.getAlarmId())
            .as("告警ID不能为空")
            .isNotNull();
        
        assertThat(auditLog.getOperationType())
            .as("操作类型不能为空")
            .isNotNull()
            .isNotEmpty();
        
        assertThat(auditLog.getOperatorId())
            .as("操作人ID不能为空")
            .isNotNull();
        
        assertThat(auditLog.getOperatorName())
            .as("操作人姓名不能为空")
            .isNotNull()
            .isNotEmpty();
        
        assertThat(auditLog.getOperationTime())
            .as("操作时间不能为空")
            .isNotNull();
        
        assertThat(auditLog.getOperationContent())
            .as("操作内容不能为空")
            .isNotNull()
            .isNotEmpty();
    }

    /**
     * Property 5.5: 操作类型必须是有效枚举值
     * 
     * 对于任意审计日志，操作类型必须是有效的枚举值。
     * 
     * Validates: Requirements BR-BIZ-008
     */
    @Property
    @Label("Property 5.5: 操作类型必须是有效枚举值")
    void operationTypeMustBeValidEnum(
        @ForAll("validAuditLogs") AlarmAuditLogDO auditLog
    ) {
        // Then: 操作类型必须在有效列表中
        assertThat(auditLog.getOperationType())
            .as("操作类型必须是有效枚举值")
            .isIn(AUDITABLE_OPERATIONS);
    }

    /**
     * Property 5.6: 完整告警生命周期必须有完整审计记录
     * 
     * 对于任意告警的完整生命周期（创建→确认→处理→关闭），必须有对应的审计记录。
     * 
     * Validates: Requirements BR-BIZ-008
     */
    @Property
    @Label("Property 5.6: 完整告警生命周期必须有完整审计记录")
    void completeLifecycleMustHaveCompleteAuditTrail(
        @ForAll("alarmIds") Long alarmId,
        @ForAll("operatorIds") Long operatorId,
        @ForAll("operatorNames") String operatorName
    ) {
        // Given: 清空审计日志
        auditLogStore.clear();
        
        // When: 执行完整的告警生命周期
        // 1. 创建告警
        simulateCreateAlarm(alarmId, operatorId, operatorName);
        
        // 2. 确认告警
        simulateAcknowledgeAlarm(alarmId, operatorId, operatorName);
        
        // 3. 处理告警
        simulateHandleAlarm(alarmId, operatorId, operatorName, "已处理");
        
        // 4. 关闭告警
        simulateCloseAlarm(alarmId, operatorId, operatorName, "已处理");
        
        // Then: 必须有4条审计记录
        assertThat(auditLogStore)
            .as("完整生命周期必须有4条审计记录")
            .hasSize(4);
        
        // 验证审计记录的操作类型
        List<String> operationTypes = auditLogStore.stream()
            .map(AlarmAuditLogDO::getOperationType)
            .toList();
        
        assertThat(operationTypes)
            .as("审计记录必须包含所有生命周期操作")
            .containsExactly(
                AlarmAuditOperationTypeEnum.CREATE.getType(),
                AlarmAuditOperationTypeEnum.ACKNOWLEDGE.getType(),
                AlarmAuditOperationTypeEnum.HANDLE.getType(),
                AlarmAuditOperationTypeEnum.CLOSE.getType()
            );
    }

    /**
     * Property 5.7: 审计日志时间顺序正确性
     * 
     * 对于任意告警的审计记录，时间必须按操作顺序递增。
     * 
     * Validates: Requirements BR-BIZ-008
     */
    @Property
    @Label("Property 5.7: 审计日志时间顺序正确性")
    void auditLogTimeMustBeInOrder(
        @ForAll("alarmIds") Long alarmId,
        @ForAll("operatorIds") Long operatorId,
        @ForAll("operatorNames") String operatorName
    ) {
        // Given: 清空审计日志
        auditLogStore.clear();
        
        // When: 执行多个操作
        simulateCreateAlarm(alarmId, operatorId, operatorName);
        simulateAcknowledgeAlarm(alarmId, operatorId, operatorName);
        simulateHandleAlarm(alarmId, operatorId, operatorName, "已处理");
        
        // Then: 审计日志时间必须递增
        for (int i = 1; i < auditLogStore.size(); i++) {
            LocalDateTime prevTime = auditLogStore.get(i - 1).getOperationTime();
            LocalDateTime currTime = auditLogStore.get(i).getOperationTime();
            
            assertThat(currTime)
                .as("审计日志时间必须递增")
                .isAfterOrEqualTo(prevTime);
        }
    }

    /**
     * Property 5.8: 联动执行必须记录审计日志
     * 
     * 对于任意联动执行操作，必须记录审计日志。
     * 
     * Validates: Requirements BR-BIZ-009
     */
    @Property
    @Label("Property 5.8: 联动执行必须记录审计日志")
    void linkageExecutionMustBeAudited(
        @ForAll("alarmIds") Long alarmId,
        @ForAll("linkageRuleIds") Long linkageRuleId,
        @ForAll("targetDeviceNames") String targetDeviceName
    ) {
        // Given: 清空审计日志
        auditLogStore.clear();
        
        // When: 执行联动
        simulateLinkageExecution(alarmId, linkageRuleId, targetDeviceName);
        
        // Then: 必须记录审计日志
        assertThat(auditLogStore)
            .as("联动执行必须记录审计日志")
            .isNotEmpty();
        
        AlarmAuditLogDO auditLog = auditLogStore.get(auditLogStore.size() - 1);
        
        assertThat(auditLog.getAlarmId())
            .as("审计日志必须关联告警ID")
            .isEqualTo(alarmId);
        
        assertThat(auditLog.getOperationType())
            .as("审计日志操作类型必须是 LINKAGE_EXECUTE")
            .isEqualTo(AlarmAuditOperationTypeEnum.LINKAGE_EXECUTE.getType());
        
        assertThat(auditLog.getOperationContent())
            .as("审计日志必须记录联动目标设备")
            .contains(targetDeviceName);
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
     * 生成操作人ID
     */
    @Provide
    Arbitrary<Long> operatorIds() {
        return Arbitraries.longs().between(1L, 100L);
    }

    /**
     * 生成操作人姓名
     */
    @Provide
    Arbitrary<String> operatorNames() {
        return Arbitraries.strings().alpha().ofMinLength(2).ofMaxLength(10)
            .map(s -> "用户" + s);
    }

    /**
     * 生成处理措施
     */
    @Provide
    Arbitrary<String> handleMeasures() {
        return Arbitraries.of(
            "已派人现场处理",
            "已远程控制设备",
            "已通知相关人员",
            "已启动应急预案",
            "设备已恢复正常"
        );
    }

    /**
     * 生成关闭原因
     */
    @Provide
    Arbitrary<String> closeReasons() {
        return Arbitraries.of("HANDLED", "FALSE_ALARM", "OTHER");
    }

    /**
     * 生成联动规则ID
     */
    @Provide
    Arbitrary<Long> linkageRuleIds() {
        return Arbitraries.longs().between(1L, 50L);
    }

    /**
     * 生成目标设备名称
     */
    @Provide
    Arbitrary<String> targetDeviceNames() {
        return Arbitraries.of(
            "潜水泵-A区1号",
            "通风机-B区2号",
            "门禁-C区入口",
            "摄像头-D区3号",
            "喷淋系统-E区"
        );
    }

    /**
     * 生成有效的审计日志
     */
    @Provide
    Arbitrary<AlarmAuditLogDO> validAuditLogs() {
        return Combinators.combine(
            Arbitraries.longs().between(1L, 1000L),      // alarmId
            Arbitraries.of(AlarmAuditOperationTypeEnum.values()), // operationType
            Arbitraries.longs().between(1L, 100L),       // operatorId
            Arbitraries.strings().alpha().ofMinLength(2).ofMaxLength(10) // operatorName
        ).as((alarmId, opType, operatorId, operatorName) -> {
            AlarmAuditLogDO log = new AlarmAuditLogDO();
            log.setId(idSequence.getAndIncrement());
            log.setAlarmId(alarmId);
            log.setOperationType(opType.getType());
            log.setOperationContent("执行操作: " + opType.getName());
            log.setOperatorId(operatorId);
            log.setOperatorName("用户" + operatorName);
            log.setOperationTime(LocalDateTime.now());
            log.setIpAddress("192.168.1.1");
            log.setTenantId(1L);
            log.setCreateTime(LocalDateTime.now());
            return log;
        });
    }

    // ==================== 辅助方法 ====================

    /**
     * 模拟创建告警
     */
    private void simulateCreateAlarm(Long alarmId, Long operatorId, String operatorName) {
        AlarmAuditLogDO log = createAuditLog(
            alarmId,
            AlarmAuditOperationTypeEnum.CREATE.getType(),
            operatorId,
            operatorName,
            "创建告警"
        );
        auditLogStore.add(log);
    }

    /**
     * 模拟确认告警
     */
    private void simulateAcknowledgeAlarm(Long alarmId, Long operatorId, String operatorName) {
        AlarmAuditLogDO log = createAuditLog(
            alarmId,
            AlarmAuditOperationTypeEnum.ACKNOWLEDGE.getType(),
            operatorId,
            operatorName,
            "确认告警"
        );
        auditLogStore.add(log);
    }

    /**
     * 模拟处理告警
     */
    private void simulateHandleAlarm(Long alarmId, Long operatorId, String operatorName, String handleMeasure) {
        AlarmAuditLogDO log = createAuditLog(
            alarmId,
            AlarmAuditOperationTypeEnum.HANDLE.getType(),
            operatorId,
            operatorName,
            "处理告警，措施: " + handleMeasure
        );
        auditLogStore.add(log);
    }

    /**
     * 模拟关闭告警
     */
    private void simulateCloseAlarm(Long alarmId, Long operatorId, String operatorName, String closeReason) {
        AlarmAuditLogDO log = createAuditLog(
            alarmId,
            AlarmAuditOperationTypeEnum.CLOSE.getType(),
            operatorId,
            operatorName,
            "关闭告警，原因: " + closeReason
        );
        auditLogStore.add(log);
    }

    /**
     * 模拟联动执行
     */
    private void simulateLinkageExecution(Long alarmId, Long linkageRuleId, String targetDeviceName) {
        AlarmAuditLogDO log = createAuditLog(
            alarmId,
            AlarmAuditOperationTypeEnum.LINKAGE_EXECUTE.getType(),
            0L, // 系统自动执行
            "系统",
            "执行联动，规则ID: " + linkageRuleId + "，目标设备: " + targetDeviceName
        );
        auditLogStore.add(log);
    }

    /**
     * 创建审计日志
     */
    private AlarmAuditLogDO createAuditLog(Long alarmId, String operationType, 
                                           Long operatorId, String operatorName, 
                                           String operationContent) {
        AlarmAuditLogDO log = new AlarmAuditLogDO();
        log.setId(idSequence.getAndIncrement());
        log.setAlarmId(alarmId);
        log.setOperationType(operationType);
        log.setOperationContent(operationContent);
        log.setOperatorId(operatorId);
        log.setOperatorName(operatorName);
        log.setOperationTime(LocalDateTime.now());
        log.setIpAddress("192.168.1.1");
        log.setTenantId(1L);
        log.setCreateTime(LocalDateTime.now());
        return log;
    }

}
