package cn.iocoder.yudao.module.emergency.service.alert;

import cn.iocoder.yudao.module.emergency.dal.dataobject.alert.AlertConfigurationDO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.task.EmergencyTaskDO;

/**
 * 任务告警通知 Service 接口
 * 
 * @author 系统生成
 */
public interface TaskAlertNotificationService {
    
    /**
     * 发送告警通知
     * 
     * @param task 任务
     * @param config 告警配置
     * @param message 告警消息
     */
    void sendAlert(EmergencyTaskDO task, AlertConfigurationDO config, String message);
}








