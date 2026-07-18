package cn.cheers.x.alarm.config;

import cn.cheers.x.alarm.controller.admin.vo.alarm.*;
import cn.cheers.x.alarm.controller.admin.vo.linkage.*;
import cn.cheers.x.alarm.controller.admin.vo.rule.*;
import cn.cheers.x.alarm.dal.dataobject.*;
import cn.cheers.x.alarm.enums.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 告警模块测试数据构建器
 * 
 * <p>提供告警模块测试所需的测试数据构建方法，
 * 支持链式调用，方便快速构建测试数据。</p>
 * 
 * <h3>使用示例</h3>
 * <pre>
 * {@code
 * // 构建告警 DO
 * AlarmDO alarm = AlarmTestDataBuilder.buildAlarmDO()
 *     .alarmLevel(AlarmLevelEnum.CRITICAL.getLevel())
 *     .alarmStatus(AlarmStatusEnum.PENDING.getStatus())
 *     .build();
 * 
 * // 构建告警触发请求
 * AlarmTriggerReqVO triggerReq = AlarmTestDataBuilder.buildAlarmTriggerReqVO();
 * 
 * // 构建告警规则 DO
 * AlarmRuleDO rule = AlarmTestDataBuilder.buildAlarmRuleDO();
 * }
 * </pre>
 *
 * @author 告警管理模块
 */
public class AlarmTestDataBuilder {

    // ========== 默认值常量 ==========
    
    /** 默认租户ID */
    public static final Long DEFAULT_TENANT_ID = 1L;
    /** 默认告警类型ID */
    public static final Long DEFAULT_ALARM_TYPE_ID = 1L;
    /** 默认告警分类ID */
    public static final Long DEFAULT_ALARM_CATEGORY_ID = 1L;
    /** 默认告警模型ID */
    public static final Long DEFAULT_ALARM_MODEL_ID = 1L;
    /** 默认设备ID */
    public static final Long DEFAULT_DEVICE_ID = 100L;
    /** 默认位置ID */
    public static final Long DEFAULT_LOCATION_ID = 200L;

    /** 默认告警规则ID */
    public static final Long DEFAULT_RULE_ID = 300L;
    /** 默认联动规则ID */
    public static final Long DEFAULT_LINKAGE_RULE_ID = 400L;

    // ========== AlarmDO 构建器 ==========

    /**
     * 构建默认的告警 DO
     * 
     * @return 告警 DO 构建器
     */
    public static AlarmDOBuilder buildAlarmDO() {
        return new AlarmDOBuilder();
    }

    /**
     * 构建一个完整的告警 DO（带默认值）
     * 
     * @return 告警 DO
     */
    public static AlarmDO createDefaultAlarmDO() {
        return buildAlarmDO().build();
    }

    /**
     * 构建待确认状态的告警 DO
     * 
     * @return 告警 DO
     */
    public static AlarmDO createPendingAlarmDO() {
        return buildAlarmDO()
                .alarmStatus(AlarmStatusEnum.PENDING.getStatus())
                .build();
    }

    /**
     * 构建已确认状态的告警 DO
     * 
     * @return 告警 DO
     */
    public static AlarmDO createAcknowledgedAlarmDO() {
        return buildAlarmDO()
                .alarmStatus(AlarmStatusEnum.ACKNOWLEDGED.getStatus())
                .acknowledgeTime(LocalDateTime.now())
                .acknowledgeUserId(1L)
                .acknowledgeUserName("测试用户")
                .build();
    }

    /**
     * 构建处理中状态的告警 DO
     * 
     * @return 告警 DO
     */
    public static AlarmDO createHandlingAlarmDO() {
        return buildAlarmDO()
                .alarmStatus(AlarmStatusEnum.HANDLING.getStatus())
                .acknowledgeTime(LocalDateTime.now().minusMinutes(10))
                .acknowledgeUserId(1L)
                .acknowledgeUserName("测试用户")
                .handleTime(LocalDateTime.now())
                .handleUserId(1L)
                .handleUserName("测试用户")
                .handleMeasure("测试处理措施")
                .handleResult(AlarmHandleResultEnum.RESOLVED.getResult())
                .build();
    }

    /**
     * 构建已关闭状态的告警 DO
     * 
     * @return 告警 DO
     */
    public static AlarmDO createClosedAlarmDO() {
        return buildAlarmDO()
                .alarmStatus(AlarmStatusEnum.CLOSED.getStatus())
                .acknowledgeTime(LocalDateTime.now().minusMinutes(20))
                .acknowledgeUserId(1L)
                .acknowledgeUserName("测试用户")
                .handleTime(LocalDateTime.now().minusMinutes(10))
                .handleUserId(1L)
                .handleUserName("测试用户")
                .handleMeasure("测试处理措施")
                .handleResult(AlarmHandleResultEnum.RESOLVED.getResult())
                .closeTime(LocalDateTime.now())
                .closeUserId(1L)
                .closeUserName("测试用户")
                .closeReason(AlarmCloseReasonEnum.HANDLED.getReason())
                .build();
    }

    /**
     * 告警 DO 构建器
     */
    public static class AlarmDOBuilder {
        private final AlarmDO alarm = new AlarmDO();
        private static int codeSequence = 1;

        public AlarmDOBuilder() {
            // 设置默认值
            alarm.setAlarmCode("ALM-" + LocalDateTime.now().toLocalDate().toString().replace("-", "") 
                    + "-" + String.format("%05d", codeSequence++));
            alarm.setAlarmTypeId(DEFAULT_ALARM_TYPE_ID);
            alarm.setAlarmCategoryId(DEFAULT_ALARM_CATEGORY_ID);
            alarm.setAlarmModelId(DEFAULT_ALARM_MODEL_ID);
            alarm.setAlarmTypePath("环境告警 > 水位告警 > 水位超标");
            alarm.setAlarmLevel(AlarmLevelEnum.WARNING.getLevel());
            alarm.setAlarmStatus(AlarmStatusEnum.PENDING.getStatus());
            alarm.setAlarmSource(AlarmSourceEnum.SYSTEM.getSource());
            alarm.setAlarmContent("测试告警内容：水位超过阈值50cm，当前水位55cm");
            alarm.setDeviceId(DEFAULT_DEVICE_ID);
            alarm.setDeviceName("水位传感器-A区1号");
            alarm.setLocationId(DEFAULT_LOCATION_ID);
            alarm.setLocationName("A区 > 1号防火分区 > 集水坑");
            alarm.setTriggerValue("55cm");
            alarm.setThresholdValue("50cm");
            alarm.setTriggerCount(1);
            alarm.setEscalationLevel(0);
            alarm.setTenantId(DEFAULT_TENANT_ID);
            alarm.setDeleted(false);
        }

        public AlarmDOBuilder id(Long id) {
            alarm.setId(id);
            return this;
        }

        public AlarmDOBuilder alarmCode(String alarmCode) {
            alarm.setAlarmCode(alarmCode);
            return this;
        }

        public AlarmDOBuilder alarmTypeId(Long alarmTypeId) {
            alarm.setAlarmTypeId(alarmTypeId);
            return this;
        }

        public AlarmDOBuilder alarmCategoryId(Long alarmCategoryId) {
            alarm.setAlarmCategoryId(alarmCategoryId);
            return this;
        }

        public AlarmDOBuilder alarmModelId(Long alarmModelId) {
            alarm.setAlarmModelId(alarmModelId);
            return this;
        }

        public AlarmDOBuilder alarmTypePath(String alarmTypePath) {
            alarm.setAlarmTypePath(alarmTypePath);
            return this;
        }

        public AlarmDOBuilder alarmLevel(String alarmLevel) {
            alarm.setAlarmLevel(alarmLevel);
            return this;
        }

        public AlarmDOBuilder alarmStatus(String alarmStatus) {
            alarm.setAlarmStatus(alarmStatus);
            return this;
        }

        public AlarmDOBuilder alarmSource(String alarmSource) {
            alarm.setAlarmSource(alarmSource);
            return this;
        }

        public AlarmDOBuilder alarmContent(String alarmContent) {
            alarm.setAlarmContent(alarmContent);
            return this;
        }

        public AlarmDOBuilder deviceId(Long deviceId) {
            alarm.setDeviceId(deviceId);
            return this;
        }

        public AlarmDOBuilder deviceName(String deviceName) {
            alarm.setDeviceName(deviceName);
            return this;
        }

        public AlarmDOBuilder locationId(Long locationId) {
            alarm.setLocationId(locationId);
            return this;
        }

        public AlarmDOBuilder locationName(String locationName) {
            alarm.setLocationName(locationName);
            return this;
        }

        public AlarmDOBuilder triggerValue(String triggerValue) {
            alarm.setTriggerValue(triggerValue);
            return this;
        }

        public AlarmDOBuilder thresholdValue(String thresholdValue) {
            alarm.setThresholdValue(thresholdValue);
            return this;
        }

        public AlarmDOBuilder triggerCount(Integer triggerCount) {
            alarm.setTriggerCount(triggerCount);
            return this;
        }

        public AlarmDOBuilder ruleId(Long ruleId) {
            alarm.setRuleId(ruleId);
            return this;
        }

        public AlarmDOBuilder escalationLevel(Integer escalationLevel) {
            alarm.setEscalationLevel(escalationLevel);
            return this;
        }

        public AlarmDOBuilder escalationTime(LocalDateTime escalationTime) {
            alarm.setEscalationTime(escalationTime);
            return this;
        }

        public AlarmDOBuilder acknowledgeTime(LocalDateTime acknowledgeTime) {
            alarm.setAcknowledgeTime(acknowledgeTime);
            return this;
        }

        public AlarmDOBuilder acknowledgeUserId(Long acknowledgeUserId) {
            alarm.setAcknowledgeUserId(acknowledgeUserId);
            return this;
        }

        public AlarmDOBuilder acknowledgeUserName(String acknowledgeUserName) {
            alarm.setAcknowledgeUserName(acknowledgeUserName);
            return this;
        }

        public AlarmDOBuilder acknowledgeRemark(String acknowledgeRemark) {
            alarm.setAcknowledgeRemark(acknowledgeRemark);
            return this;
        }

        public AlarmDOBuilder handleTime(LocalDateTime handleTime) {
            alarm.setHandleTime(handleTime);
            return this;
        }

        public AlarmDOBuilder handleUserId(Long handleUserId) {
            alarm.setHandleUserId(handleUserId);
            return this;
        }

        public AlarmDOBuilder handleUserName(String handleUserName) {
            alarm.setHandleUserName(handleUserName);
            return this;
        }

        public AlarmDOBuilder handleMeasure(String handleMeasure) {
            alarm.setHandleMeasure(handleMeasure);
            return this;
        }

        public AlarmDOBuilder handleResult(String handleResult) {
            alarm.setHandleResult(handleResult);
            return this;
        }

        public AlarmDOBuilder closeTime(LocalDateTime closeTime) {
            alarm.setCloseTime(closeTime);
            return this;
        }

        public AlarmDOBuilder closeUserId(Long closeUserId) {
            alarm.setCloseUserId(closeUserId);
            return this;
        }

        public AlarmDOBuilder closeUserName(String closeUserName) {
            alarm.setCloseUserName(closeUserName);
            return this;
        }

        public AlarmDOBuilder closeReason(String closeReason) {
            alarm.setCloseReason(closeReason);
            return this;
        }

        public AlarmDOBuilder closeRemark(String closeRemark) {
            alarm.setCloseRemark(closeRemark);
            return this;
        }

        public AlarmDOBuilder tenantId(Long tenantId) {
            alarm.setTenantId(tenantId);
            return this;
        }

        public AlarmDO build() {
            return alarm;
        }
    }


    // ========== AlarmRuleDO 构建器 ==========

    /**
     * 构建默认的告警规则 DO
     * 
     * @return 告警规则 DO 构建器
     */
    public static AlarmRuleDOBuilder buildAlarmRuleDO() {
        return new AlarmRuleDOBuilder();
    }

    /**
     * 构建一个完整的告警规则 DO（带默认值）
     * 
     * @return 告警规则 DO
     */
    public static AlarmRuleDO createDefaultAlarmRuleDO() {
        return buildAlarmRuleDO().build();
    }

    /**
     * 构建阈值类型的告警规则 DO
     * 
     * @return 告警规则 DO
     */
    public static AlarmRuleDO createThresholdAlarmRuleDO() {
        return buildAlarmRuleDO()
                .ruleType(AlarmRuleTypeEnum.THRESHOLD.getType())
                .conditionExpression("{\"field\":\"waterLevel\",\"operator\":\">\",\"value\":50}")
                .build();
    }

    /**
     * 告警规则 DO 构建器
     */
    public static class AlarmRuleDOBuilder {
        private final AlarmRuleDO rule = new AlarmRuleDO();
        private static int ruleSequence = 1;

        public AlarmRuleDOBuilder() {
            // 设置默认值
            rule.setRuleName("测试告警规则-" + ruleSequence);
            rule.setRuleCode("RULE-" + String.format("%05d", ruleSequence++));
            rule.setRuleType(AlarmRuleTypeEnum.THRESHOLD.getType());
            rule.setAlarmTypeId(DEFAULT_ALARM_TYPE_ID);
            rule.setAlarmCategoryId(DEFAULT_ALARM_CATEGORY_ID);
            rule.setAlarmModelId(DEFAULT_ALARM_MODEL_ID);
            rule.setAlarmLevel(AlarmLevelEnum.WARNING.getLevel());
            rule.setDeviceType("WATER_LEVEL_SENSOR");
            rule.setConditionExpression("{\"field\":\"waterLevel\",\"operator\":\">\",\"value\":50}");
            rule.setAlarmContentTemplate("水位超过阈值，当前值：${value}cm，阈值：${threshold}cm");
            rule.setEnabled(true);
            rule.setPriority(0);
            rule.setDescription("测试告警规则描述");
            rule.setTenantId(DEFAULT_TENANT_ID);
            rule.setDeleted(false);
        }

        public AlarmRuleDOBuilder id(Long id) {
            rule.setId(id);
            return this;
        }

        public AlarmRuleDOBuilder ruleName(String ruleName) {
            rule.setRuleName(ruleName);
            return this;
        }

        public AlarmRuleDOBuilder ruleCode(String ruleCode) {
            rule.setRuleCode(ruleCode);
            return this;
        }

        public AlarmRuleDOBuilder ruleType(String ruleType) {
            rule.setRuleType(ruleType);
            return this;
        }

        public AlarmRuleDOBuilder alarmTypeId(Long alarmTypeId) {
            rule.setAlarmTypeId(alarmTypeId);
            return this;
        }

        public AlarmRuleDOBuilder alarmCategoryId(Long alarmCategoryId) {
            rule.setAlarmCategoryId(alarmCategoryId);
            return this;
        }

        public AlarmRuleDOBuilder alarmModelId(Long alarmModelId) {
            rule.setAlarmModelId(alarmModelId);
            return this;
        }

        public AlarmRuleDOBuilder alarmLevel(String alarmLevel) {
            rule.setAlarmLevel(alarmLevel);
            return this;
        }

        public AlarmRuleDOBuilder deviceType(String deviceType) {
            rule.setDeviceType(deviceType);
            return this;
        }

        public AlarmRuleDOBuilder conditionExpression(String conditionExpression) {
            rule.setConditionExpression(conditionExpression);
            return this;
        }

        public AlarmRuleDOBuilder alarmContentTemplate(String alarmContentTemplate) {
            rule.setAlarmContentTemplate(alarmContentTemplate);
            return this;
        }

        public AlarmRuleDOBuilder enabled(Boolean enabled) {
            rule.setEnabled(enabled);
            return this;
        }

        public AlarmRuleDOBuilder priority(Integer priority) {
            rule.setPriority(priority);
            return this;
        }

        public AlarmRuleDOBuilder description(String description) {
            rule.setDescription(description);
            return this;
        }

        public AlarmRuleDOBuilder tenantId(Long tenantId) {
            rule.setTenantId(tenantId);
            return this;
        }

        public AlarmRuleDO build() {
            return rule;
        }
    }

    // ========== LinkageRuleDO 构建器 ==========

    /**
     * 构建默认的联动规则 DO
     * 
     * @return 联动规则 DO 构建器
     */
    public static LinkageRuleDOBuilder buildLinkageRuleDO() {
        return new LinkageRuleDOBuilder();
    }

    /**
     * 构建一个完整的联动规则 DO（带默认值）
     * 
     * @return 联动规则 DO
     */
    public static LinkageRuleDO createDefaultLinkageRuleDO() {
        return buildLinkageRuleDO().build();
    }

    /**
     * 联动规则 DO 构建器
     */
    public static class LinkageRuleDOBuilder {
        private final LinkageRuleDO rule = new LinkageRuleDO();
        private static int linkageSequence = 1;

        public LinkageRuleDOBuilder() {
            // 设置默认值
            rule.setRuleName("测试联动规则-" + linkageSequence);
            rule.setRuleCode("LINKAGE-" + String.format("%05d", linkageSequence++));
            rule.setAlarmTypeId(DEFAULT_ALARM_TYPE_ID);
            rule.setAlarmCategoryId(DEFAULT_ALARM_CATEGORY_ID);
            rule.setAlarmLevel(AlarmLevelEnum.WARNING.getLevel());
            rule.setActions("[{\"type\":\"NOTIFICATION\",\"config\":{\"recipients\":[1]}}]");
            rule.setExecutionMode(LinkageExecutionModeEnum.SERIAL.getMode());
            rule.setEnabled(true);
            rule.setPriority(0);
            rule.setDescription("测试联动规则描述");
            rule.setTenantId(DEFAULT_TENANT_ID);
            rule.setDeleted(false);
        }

        public LinkageRuleDOBuilder id(Long id) {
            rule.setId(id);
            return this;
        }

        public LinkageRuleDOBuilder ruleName(String ruleName) {
            rule.setRuleName(ruleName);
            return this;
        }

        public LinkageRuleDOBuilder ruleCode(String ruleCode) {
            rule.setRuleCode(ruleCode);
            return this;
        }

        public LinkageRuleDOBuilder alarmRuleId(Long alarmRuleId) {
            rule.setAlarmRuleId(alarmRuleId);
            return this;
        }

        public LinkageRuleDOBuilder alarmTypeId(Long alarmTypeId) {
            rule.setAlarmTypeId(alarmTypeId);
            return this;
        }

        public LinkageRuleDOBuilder alarmCategoryId(Long alarmCategoryId) {
            rule.setAlarmCategoryId(alarmCategoryId);
            return this;
        }

        public LinkageRuleDOBuilder alarmLevel(String alarmLevel) {
            rule.setAlarmLevel(alarmLevel);
            return this;
        }

        public LinkageRuleDOBuilder conditionExpression(String conditionExpression) {
            rule.setConditionExpression(conditionExpression);
            return this;
        }

        public LinkageRuleDOBuilder actions(String actions) {
            rule.setActions(actions);
            return this;
        }

        public LinkageRuleDOBuilder executionMode(String executionMode) {
            rule.setExecutionMode(executionMode);
            return this;
        }

        public LinkageRuleDOBuilder enabled(Boolean enabled) {
            rule.setEnabled(enabled);
            return this;
        }

        public LinkageRuleDOBuilder priority(Integer priority) {
            rule.setPriority(priority);
            return this;
        }

        public LinkageRuleDOBuilder description(String description) {
            rule.setDescription(description);
            return this;
        }

        public LinkageRuleDOBuilder tenantId(Long tenantId) {
            rule.setTenantId(tenantId);
            return this;
        }

        public LinkageRuleDO build() {
            return rule;
        }
    }


    // ========== LinkageExecutionDO 构建器 ==========

    /**
     * 构建默认的联动执行记录 DO
     * 
     * @return 联动执行记录 DO 构建器
     */
    public static LinkageExecutionDOBuilder buildLinkageExecutionDO() {
        return new LinkageExecutionDOBuilder();
    }

    /**
     * 构建一个完整的联动执行记录 DO（带默认值）
     * 
     * @return 联动执行记录 DO
     */
    public static LinkageExecutionDO createDefaultLinkageExecutionDO() {
        return buildLinkageExecutionDO().build();
    }

    /**
     * 构建执行成功的联动执行记录 DO
     * 
     * @return 联动执行记录 DO
     */
    public static LinkageExecutionDO createSuccessLinkageExecutionDO() {
        return buildLinkageExecutionDO()
                .executionStatus(LinkageExecutionStatusEnum.SUCCESS.getStatus())
                .executionResult("执行成功")
                .endTime(LocalDateTime.now())
                .durationMs(1000L)
                .build();
    }

    /**
     * 构建执行失败的联动执行记录 DO
     * 
     * @return 联动执行记录 DO
     */
    public static LinkageExecutionDO createFailedLinkageExecutionDO() {
        return buildLinkageExecutionDO()
                .executionStatus(LinkageExecutionStatusEnum.FAILED.getStatus())
                .retryCount(3)
                .errorMessage("设备通信超时")
                .manualIntervention(true)
                .endTime(LocalDateTime.now())
                .durationMs(15000L)
                .build();
    }

    /**
     * 联动执行记录 DO 构建器
     */
    public static class LinkageExecutionDOBuilder {
        private final LinkageExecutionDO execution = new LinkageExecutionDO();

        public LinkageExecutionDOBuilder() {
            // 设置默认值
            execution.setAlarmId(1L);
            execution.setLinkageRuleId(DEFAULT_LINKAGE_RULE_ID);
            execution.setActionType(LinkageActionTypeEnum.NOTIFICATION.getType());
            execution.setActionConfig("{\"recipients\":[1]}");
            execution.setTargetDeviceId(DEFAULT_DEVICE_ID);
            execution.setTargetDeviceName("测试设备");
            execution.setExecutionStatus(LinkageExecutionStatusEnum.PENDING.getStatus());
            execution.setRetryCount(0);
            execution.setStartTime(LocalDateTime.now());
            execution.setManualIntervention(false);
            execution.setTenantId(DEFAULT_TENANT_ID);
            execution.setDeleted(false);
        }

        public LinkageExecutionDOBuilder id(Long id) {
            execution.setId(id);
            return this;
        }

        public LinkageExecutionDOBuilder alarmId(Long alarmId) {
            execution.setAlarmId(alarmId);
            return this;
        }

        public LinkageExecutionDOBuilder linkageRuleId(Long linkageRuleId) {
            execution.setLinkageRuleId(linkageRuleId);
            return this;
        }

        public LinkageExecutionDOBuilder actionType(String actionType) {
            execution.setActionType(actionType);
            return this;
        }

        public LinkageExecutionDOBuilder actionConfig(String actionConfig) {
            execution.setActionConfig(actionConfig);
            return this;
        }

        public LinkageExecutionDOBuilder targetDeviceId(Long targetDeviceId) {
            execution.setTargetDeviceId(targetDeviceId);
            return this;
        }

        public LinkageExecutionDOBuilder targetDeviceName(String targetDeviceName) {
            execution.setTargetDeviceName(targetDeviceName);
            return this;
        }

        public LinkageExecutionDOBuilder executionStatus(String executionStatus) {
            execution.setExecutionStatus(executionStatus);
            return this;
        }

        public LinkageExecutionDOBuilder retryCount(Integer retryCount) {
            execution.setRetryCount(retryCount);
            return this;
        }

        public LinkageExecutionDOBuilder executionResult(String executionResult) {
            execution.setExecutionResult(executionResult);
            return this;
        }

        public LinkageExecutionDOBuilder errorMessage(String errorMessage) {
            execution.setErrorMessage(errorMessage);
            return this;
        }

        public LinkageExecutionDOBuilder startTime(LocalDateTime startTime) {
            execution.setStartTime(startTime);
            return this;
        }

        public LinkageExecutionDOBuilder endTime(LocalDateTime endTime) {
            execution.setEndTime(endTime);
            return this;
        }

        public LinkageExecutionDOBuilder durationMs(Long durationMs) {
            execution.setDurationMs(durationMs);
            return this;
        }

        public LinkageExecutionDOBuilder manualIntervention(Boolean manualIntervention) {
            execution.setManualIntervention(manualIntervention);
            return this;
        }

        public LinkageExecutionDOBuilder tenantId(Long tenantId) {
            execution.setTenantId(tenantId);
            return this;
        }

        public LinkageExecutionDO build() {
            return execution;
        }
    }

    // ========== 请求 VO 构建方法 ==========

    /**
     * 构建告警触发请求 VO
     * 
     * @return 告警触发请求 VO
     */
    public static AlarmTriggerReqVO buildAlarmTriggerReqVO() {
        AlarmTriggerReqVO reqVO = new AlarmTriggerReqVO();
        reqVO.setAlarmTypeId(DEFAULT_ALARM_TYPE_ID);
        reqVO.setAlarmLevel(AlarmLevelEnum.WARNING.getLevel());
        reqVO.setAlarmContent("测试告警内容：水位超过阈值50cm，当前水位55cm");
        reqVO.setDeviceId(DEFAULT_DEVICE_ID);
        reqVO.setDeviceName("水位传感器-A区1号");
        reqVO.setLocationId(DEFAULT_LOCATION_ID);
        reqVO.setLocationName("A区 > 1号防火分区 > 集水坑");
        reqVO.setTriggerValue("55cm");
        reqVO.setThresholdValue("50cm");
        reqVO.setRuleId(DEFAULT_RULE_ID);
        return reqVO;
    }

    /**
     * 构建人工上报告警请求 VO
     * 
     * @return 人工上报告警请求 VO
     */
    public static AlarmReportReqVO buildAlarmReportReqVO() {
        AlarmReportReqVO reqVO = new AlarmReportReqVO();
        reqVO.setAlarmTypeId(DEFAULT_ALARM_TYPE_ID);
        reqVO.setAlarmLevel(AlarmLevelEnum.WARNING.getLevel());
        reqVO.setAlarmContent("人工上报测试告警：发现设备异常，需要检修处理，请及时安排人员处理。");
        reqVO.setDeviceId(DEFAULT_DEVICE_ID);
        reqVO.setLocationId(DEFAULT_LOCATION_ID);
        return reqVO;
    }

    /**
     * 构建告警确认请求 VO
     * 
     * @return 告警确认请求 VO
     */
    public static AlarmAcknowledgeReqVO buildAlarmAcknowledgeReqVO() {
        AlarmAcknowledgeReqVO reqVO = new AlarmAcknowledgeReqVO();
        reqVO.setAcknowledgeRemark("已确认告警，正在安排处理");
        return reqVO;
    }

    /**
     * 构建告警处理请求 VO
     * 
     * @return 告警处理请求 VO
     */
    public static AlarmHandleReqVO buildAlarmHandleReqVO() {
        AlarmHandleReqVO reqVO = new AlarmHandleReqVO();
        reqVO.setHandleMeasure("已派人到现场检查，发现水泵故障，已更换水泵并恢复正常运行");
        reqVO.setHandleResult(AlarmHandleResultEnum.RESOLVED.getResult());
        return reqVO;
    }

    /**
     * 构建告警关闭请求 VO
     * 
     * @return 告警关闭请求 VO
     */
    public static AlarmCloseReqVO buildAlarmCloseReqVO() {
        AlarmCloseReqVO reqVO = new AlarmCloseReqVO();
        reqVO.setCloseReason(AlarmCloseReasonEnum.HANDLED.getReason());
        reqVO.setCloseRemark("告警已处理完成，设备恢复正常运行");
        return reqVO;
    }

    /**
     * 构建告警规则创建请求 VO
     * 
     * @return 告警规则创建请求 VO
     */
    public static AlarmRuleCreateReqVO buildAlarmRuleCreateReqVO() {
        AlarmRuleCreateReqVO reqVO = new AlarmRuleCreateReqVO();
        reqVO.setRuleName("测试告警规则");
        reqVO.setRuleCode("TEST-RULE-001");
        reqVO.setRuleType(AlarmRuleTypeEnum.THRESHOLD.getType());
        reqVO.setAlarmTypeId(DEFAULT_ALARM_TYPE_ID);
        reqVO.setAlarmCategoryId(DEFAULT_ALARM_CATEGORY_ID);
        reqVO.setAlarmModelId(DEFAULT_ALARM_MODEL_ID);
        reqVO.setAlarmLevel(AlarmLevelEnum.WARNING.getLevel());
        reqVO.setDeviceType("WATER_LEVEL_SENSOR");
        reqVO.setConditionExpression("{\"field\":\"waterLevel\",\"operator\":\">\",\"value\":50}");
        reqVO.setAlarmContentTemplate("水位超过阈值，当前值：${value}cm，阈值：${threshold}cm");
        reqVO.setEnabled(true);
        reqVO.setPriority(0);
        reqVO.setDescription("测试告警规则描述");
        return reqVO;
    }

    /**
     * 构建联动规则创建请求 VO
     * 
     * @return 联动规则创建请求 VO
     */
    public static LinkageRuleCreateReqVO buildLinkageRuleCreateReqVO() {
        LinkageRuleCreateReqVO reqVO = new LinkageRuleCreateReqVO();
        reqVO.setRuleName("测试联动规则");
        reqVO.setRuleCode("TEST-LINKAGE-001");
        reqVO.setAlarmTypeId(DEFAULT_ALARM_TYPE_ID);
        reqVO.setAlarmCategoryId(DEFAULT_ALARM_CATEGORY_ID);
        reqVO.setAlarmLevel(AlarmLevelEnum.WARNING.getLevel());
        reqVO.setActions("[{\"type\":\"NOTIFICATION\",\"config\":{\"recipients\":[1]}}]");
        reqVO.setExecutionMode(LinkageExecutionModeEnum.SERIAL.getMode());
        reqVO.setEnabled(true);
        reqVO.setPriority(0);
        reqVO.setDescription("测试联动规则描述");
        return reqVO;
    }

    // ========== 批量数据构建方法 ==========

    /**
     * 批量构建告警 DO 列表
     * 
     * @param count 数量
     * @return 告警 DO 列表
     */
    public static List<AlarmDO> buildAlarmDOList(int count) {
        List<AlarmDO> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            list.add(buildAlarmDO().build());
        }
        return list;
    }

    /**
     * 批量构建不同级别的告警 DO 列表
     * 
     * @return 告警 DO 列表（包含各级别告警）
     */
    public static List<AlarmDO> buildAlarmDOListWithAllLevels() {
        List<AlarmDO> list = new ArrayList<>();
        list.add(buildAlarmDO().alarmLevel(AlarmLevelEnum.INFO.getLevel()).build());
        list.add(buildAlarmDO().alarmLevel(AlarmLevelEnum.WARNING.getLevel()).build());
        list.add(buildAlarmDO().alarmLevel(AlarmLevelEnum.CRITICAL.getLevel()).build());
        list.add(buildAlarmDO().alarmLevel(AlarmLevelEnum.EMERGENCY.getLevel()).build());
        return list;
    }

    /**
     * 批量构建不同状态的告警 DO 列表
     * 
     * @return 告警 DO 列表（包含各状态告警）
     */
    public static List<AlarmDO> buildAlarmDOListWithAllStatuses() {
        List<AlarmDO> list = new ArrayList<>();
        list.add(createPendingAlarmDO());
        list.add(createAcknowledgedAlarmDO());
        list.add(createHandlingAlarmDO());
        list.add(createClosedAlarmDO());
        return list;
    }

}
