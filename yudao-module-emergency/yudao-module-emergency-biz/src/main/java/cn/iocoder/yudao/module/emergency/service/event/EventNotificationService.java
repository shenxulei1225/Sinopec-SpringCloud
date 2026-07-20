package cn.iocoder.yudao.module.emergency.service.event;

import cn.iocoder.yudao.module.emergency.dal.dataobject.event.EmergencyEventDO;

/**
 * 事件通知 Service 接口
 * 
 * 用于在事件创建、状态变更等关键节点发送通知
 */
public interface EventNotificationService {

    /**
     * 发送事件创建通知
     *
     * @param event 事件
     */
    void sendEventCreatedNotification(EmergencyEventDO event);

    /**
     * 发送事件状态变更通知
     *
     * @param event 事件
     * @param oldStatus 旧状态
     * @param newStatus 新状态
     */
    void sendEventStatusChangedNotification(EmergencyEventDO event, String oldStatus, String newStatus);

    /**
     * 发送事件确认通知
     *
     * @param event 事件
     * @param confirmResult 确认结果（real/false_alarm/ignore）
     */
    void sendEventConfirmedNotification(EmergencyEventDO event, String confirmResult);

    /**
     * 发送响应启动通知
     *
     * @param event 事件
     * @param responseId 响应ID
     */
    void sendResponseStartedNotification(EmergencyEventDO event, Long responseId);

    /**
     * 发送响应升级通知
     *
     * @param event 事件
     * @param responseId 响应ID
     * @param oldLevel 旧响应级别
     * @param newLevel 新响应级别
     */
    void sendResponseUpgradedNotification(EmergencyEventDO event, Long responseId, String oldLevel, String newLevel);

    /**
     * 发送响应取消通知
     *
     * @param event 事件
     * @param responseId 响应ID
     */
    void sendResponseCancelledNotification(EmergencyEventDO event, Long responseId);
}






