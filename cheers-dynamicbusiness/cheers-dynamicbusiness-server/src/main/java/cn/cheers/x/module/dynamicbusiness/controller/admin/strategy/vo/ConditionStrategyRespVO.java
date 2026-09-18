package cn.cheers.x.module.dynamicbusiness.controller.admin.strategy.vo;

import lombok.Data;

/**
 * 给人看的一条策略。
 */
@Data
public class ConditionStrategyRespVO {

    private Long id;

    private String name;

    private boolean enabled;

    private boolean platform;

    private String eventType;

    private String eventTypeName;

    private String conditionText;

    private String actionCode;

    private String actionName;

    private boolean requireAlways;

    private boolean requireExecutionRecord;

    private boolean requireStepUpdates;

    private boolean requireExecutionStatus;

    private String compareField;

    private String compareOp;

    private String compareValue;

    private Long alarmTypeId;

    private String alarmLevel;

    private Long locationId;

    private Long deviceId;

    private String executionStatus;

    private String recipientUserIds;

    private Integer priority;
}
