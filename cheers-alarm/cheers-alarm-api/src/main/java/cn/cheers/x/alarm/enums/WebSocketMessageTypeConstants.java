package cn.cheers.x.alarm.enums;

/**
 * 告警模块 WebSocket 消息类型常量
 * 
 * 定义告警模块通过 WebSocket 推送的消息类型
 *
 * @author 告警管理模块
 */
public interface WebSocketMessageTypeConstants {

    // ======================= 告警相关 =======================

    /**
     * 新告警推送
     * 
     * 当系统触发新告警或人工上报新告警时，推送给前端
     */
    String ALARM_NEW = "alarm-new";

    /**
     * 告警状态更新推送
     * 
     * 当告警状态发生变化（确认、处理、关闭）时，推送给前端
     */
    String ALARM_STATUS_UPDATE = "alarm-status-update";

    /**
     * 告警升级推送
     * 
     * 当告警因未及时处理而升级时，推送给前端
     */
    String ALARM_ESCALATION = "alarm-escalation";

    /**
     * 批量告警推送
     * 
     * 当短时间内产生多条告警时，批量推送给前端以优化性能
     */
    String ALARM_BATCH = "alarm-batch";

    // ======================= 联动相关 =======================

    /**
     * 联动执行结果推送
     * 
     * 当联动动作执行完成（成功或失败）时，推送给前端
     */
    String LINKAGE_EXECUTION_RESULT = "linkage-execution-result";

    /**
     * 联动需要人工介入推送
     * 
     * 当联动执行失败且需要人工介入时，推送给前端
     */
    String LINKAGE_MANUAL_INTERVENTION = "linkage-manual-intervention";

}
