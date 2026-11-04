package cn.cheers.x.module.dynamicbusiness.controller.admin.strategy.vo;

import lombok.Data;

import java.util.List;

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

    /** 这条策略已经挂上的设备。空表示还没挂，不会套到全部设备。 */
    private List<Long> deviceIds;

    private String executionStatus;

    private String recipientUserIds;

    private String signalKind;

    private Integer priority;
}
