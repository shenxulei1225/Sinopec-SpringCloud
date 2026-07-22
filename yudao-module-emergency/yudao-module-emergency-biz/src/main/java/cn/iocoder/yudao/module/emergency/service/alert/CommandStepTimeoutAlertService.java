package cn.iocoder.yudao.module.emergency.service.alert;

import cn.iocoder.yudao.module.emergency.dal.dataobject.command.EmergencyCommandStepDO;

import java.util.List;

/**
 * 指令步骤超时告警 Service 接口
 */
public interface CommandStepTimeoutAlertService {

    /**
     * 检测并处理超时告警
     * 定时任务调用此方法
     */
    void checkAndProcessTimeoutAlerts();

    /**
     * 定时检测超时告警
     * 
     * 此方法供 XXL-Job 定时任务调用。
     * 租户级任务：每个租户可以独立配置执行周期。
     * 
     * @see cn.iocoder.yudao.module.emergency.job.CommandStepTimeoutAlertJob
     */
    void scheduledCheckTimeoutAlerts();

    /**
     * 发送黄色预警（提前15分钟）
     *
     * @param step 指令步骤
     */
    void sendYellowWarning(EmergencyCommandStepDO step);

    /**
     * 发送橙色告警（超时5分钟内）
     *
     * @param step 指令步骤
     */
    void sendOrangeAlert(EmergencyCommandStepDO step);

    /**
     * 发送红色紧急告警（超时超过30分钟）
     *
     * @param step 指令步骤
     */
    void sendRedAlert(EmergencyCommandStepDO step);

    /**
     * 标记步骤超时
     *
     * @param stepId 步骤ID
     */
    void markStepTimeout(Long stepId);

    /**
     * 获取即将超时的步骤（提前15分钟）
     *
     * @return 步骤列表
     */
    List<EmergencyCommandStepDO> getStepsNearTimeout();

    /**
     * 获取已超时的步骤
     *
     * @return 步骤列表
     */
    List<EmergencyCommandStepDO> getTimeoutSteps();
}

