package cn.cheers.x.module.dynamicbusiness.controller.admin.strategy.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 新建或改一条策略。条件用结构化字段，不写脚本。
 */
@Data
public class ConditionStrategySaveReqVO {

    private Long id;

    @NotBlank
    private String name;

    @NotNull
    private Boolean enabled;

    @NotBlank
    private String eventType;

    /** 只要这件事发生 */
    private boolean requireAlways;

    /** 必须有这次执行的账 */
    private boolean requireExecutionRecord;

    /** 必须有要对回的某一步 */
    private boolean requireStepUpdates;

    /** 必须有这次执行的新状态 */
    private boolean requireExecutionStatus;

    /** 报上来的某个数字段名；空表示不比这个数 */
    private String compareField;

    /** EQ / NEQ / GT / GTE / LT / LTE */
    private String compareOp;

    private String compareValue;

    @NotBlank
    private String actionCode;

    private Long alarmTypeId;

    private String alarmLevel;

    private Long locationId;

    private Long deviceId;

    /** 更新这次执行的状态时写：pending / in_progress / completed / fault */
    private String executionStatus;

    /** 给相关人员发通知：用户编号，逗号分隔 */
    private String recipientUserIds;

    private Integer priority;
}
