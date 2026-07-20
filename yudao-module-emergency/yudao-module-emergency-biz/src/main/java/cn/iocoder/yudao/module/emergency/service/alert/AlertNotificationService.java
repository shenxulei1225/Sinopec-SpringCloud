package cn.iocoder.yudao.module.emergency.service.alert;

import cn.iocoder.yudao.module.emergency.dal.dataobject.alert.AlertConfigurationDO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.command.EmergencyCommandStepDO;

/**
 * 告警通知 Service 接口
 */
public interface AlertNotificationService {

    /**
     * 发送告警通知
     *
     * @param step 指令步骤
     * @param config 告警配置
     * @param message 告警消息
     */
    void sendAlert(EmergencyCommandStepDO step, AlertConfigurationDO config, String message);
}

