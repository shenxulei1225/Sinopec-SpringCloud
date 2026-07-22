package cn.iocoder.yudao.module.emergency.service.task;

import cn.iocoder.yudao.module.emergency.dal.dataobject.task.EmergencyTaskDO;

/**
 * 任务完成通知 Service 接口
 * 
 * 用于在任务完成时发送通知给相关人员，包括：
 * - 任务执行人
 * - 任务负责人
 * - 事件负责人
 * - 响应负责人
 * 
 * 支持多种通知渠道：
 * - 系统通知（站内信、WebSocket推送）
 * - 短信通知
 * - 邮件通知
 * 
 * @author 应急管理系统
 */
public interface TaskCompletionNotificationService {
    
    /**
     * 发送任务完成通知
     * 
     * @param task 已完成的任务
     */
    void sendCompletionNotification(EmergencyTaskDO task);
    
    /**
     * 发送任务完成通知（带自定义消息）
     * 
     * @param task 已完成的任务
     * @param message 自定义通知消息
     */
    void sendCompletionNotification(EmergencyTaskDO task, String message);
}

