package cn.cheers.x.alarm.enums;

/**
 * Alarm 模块字典类型常量
 * 
 * 定义告警模块使用的系统字典类型编码
 */
public interface DictTypeConstants {

    /**
     * 告警级别
     * 值：INFO-信息、WARNING-警告、CRITICAL-严重、EMERGENCY-紧急
     */
    String ALARM_LEVEL = "alarm_level";

    /**
     * 告警状态
     * 值：PENDING-待确认、ACKNOWLEDGED-已确认、HANDLING-处理中、CLOSED-已关闭
     */
    String ALARM_STATUS = "alarm_status";

    /**
     * 告警来源
     * 值：SYSTEM-系统自动、MANUAL-人工上报
     */
    String ALARM_SOURCE = "alarm_source";

    /**
     * 告警规则类型
     * 值：THRESHOLD-阈值告警、RATE-变化率告警、PATTERN-异常模式告警、COMBINATION-组合条件告警
     */
    String ALARM_RULE_TYPE = "alarm_rule_type";

    /**
     * 联动动作类型
     * 值：NOTIFICATION-发送通知、WORK_ORDER-创建工单、DEVICE_CONTROL-设备控制、
     *     VIDEO_LINKAGE-视频联动、ACCESS_CONTROL-门禁控制、FIRE_CONTROL-消防控制、
     *     SCRIPT-执行脚本、API_CALL-调用API
     */
    String LINKAGE_ACTION_TYPE = "linkage_action_type";

    /**
     * 联动执行状态
     * 值：PENDING-待执行、EXECUTING-执行中、SUCCESS-成功、FAILED-失败、RETRY-重试中
     */
    String LINKAGE_EXECUTION_STATUS = "linkage_execution_status";

    /**
     * 联动执行模式
     * 值：SERIAL-串行、PARALLEL-并行
     */
    String LINKAGE_EXECUTION_MODE = "linkage_execution_mode";

    /**
     * 告警处理结果
     * 值：RESOLVED-已解决、UNRESOLVED-未解决、FALSE_ALARM-误报
     */
    String ALARM_HANDLE_RESULT = "alarm_handle_result";

    /**
     * 告警关闭原因
     * 值：HANDLED-已处理、FALSE_ALARM-误报、OTHER-其他
     */
    String ALARM_CLOSE_REASON = "alarm_close_reason";

}
