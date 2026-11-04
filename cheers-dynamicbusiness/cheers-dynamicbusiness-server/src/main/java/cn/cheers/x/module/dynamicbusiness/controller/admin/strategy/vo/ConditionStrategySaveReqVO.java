package cn.cheers.x.module.dynamicbusiness.controller.admin.strategy.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

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

    /**
     * 采集结果这条告警要看哪些设备。
     * 空列表表示还没挂设备，收到数据也不判断。
     * 巡检事件那条仍用上面的单个设备编号，不读这个列表。
     */
    private List<Long> deviceIds;

    /** 更新这次执行的状态时写：pending / in_progress / completed / fault */
    private String executionStatus;

    /** 给相关人员发通知：用户编号，逗号分隔 */
    private String recipientUserIds;

    /**
     * 这条规则看哪一类读数：THRESHOLD 阈值，ALARM_FLAG 报警标识，STATUS 状态。
     * 空着时按比较方式推断。
     */
    private String signalKind;

    private Integer priority;
}
