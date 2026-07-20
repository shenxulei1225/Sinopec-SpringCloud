package cn.iocoder.yudao.module.emergency.service.alert;

import cn.iocoder.yudao.module.emergency.dal.dataobject.task.EmergencyTaskDO;

import java.util.List;

/**
 * 任务超时告警 Service 接口
 * 
 * 根据任务的startTime和timeLimit计算预期完成时间，超时触发告警
 * 
 * @author 系统生成
 */
public interface TaskTimeoutAlertService {
    
    /**
     * 检测并处理超时告警
     * 
     * 定时任务调用，检测所有进行中的任务是否超时
     */
    void checkAndProcessTimeoutAlerts();
    
    /**
     * 发送黄色预警（提前15分钟）
     * 
     * @param task 任务
     */
    void sendYellowWarning(EmergencyTaskDO task);
    
    /**
     * 发送橙色告警（超时5分钟内）
     * 
     * @param task 任务
     */
    void sendOrangeAlert(EmergencyTaskDO task);
    
    /**
     * 发送红色紧急告警（超时超过30分钟）
     * 
     * @param task 任务
     */
    void sendRedAlert(EmergencyTaskDO task);
    
    /**
     * 获取即将超时的任务（提前15分钟）
     * 
     * @return 即将超时的任务列表
     */
    List<EmergencyTaskDO> getTasksNearTimeout();
    
    /**
     * 获取已超时的任务
     * 
     * @return 已超时的任务列表
     */
    List<EmergencyTaskDO> getTimeoutTasks();
}








