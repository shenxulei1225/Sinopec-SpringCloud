package cn.iocoder.yudao.module.emergency.service.alert;

import cn.iocoder.yudao.module.emergency.dal.dataobject.alert.AlertConfigurationDO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.command.EmergencyCommandStepDO;
import cn.iocoder.yudao.module.emergency.dal.mysql.alert.AlertConfigurationMapper;
import cn.iocoder.yudao.module.emergency.dal.mysql.command.EmergencyCommandStepMapper;
import cn.iocoder.yudao.module.emergency.service.command.EmergencyCommandStepService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * 指令步骤超时告警 Service 实现类
 */
@Service
@Slf4j
public class CommandStepTimeoutAlertServiceImpl implements CommandStepTimeoutAlertService {

    @Resource
    private EmergencyCommandStepMapper commandStepMapper;

    @Resource
    private EmergencyCommandStepService commandStepService;

    @Resource
    private AlertConfigurationMapper alertConfigurationMapper;

    @Resource
    private AlertNotificationService alertNotificationService;

    /**
     * 检测超时告警
     * 
     * 此方法供 XXL-Job 定时任务调用，不再使用 @Scheduled。
     * 租户级任务：每个租户可以独立配置执行周期。
     * 
     * @see cn.iocoder.yudao.module.emergency.job.CommandStepTimeoutAlertJob
     */
    public void scheduledCheckTimeoutAlerts() {
        try {
            checkAndProcessTimeoutAlerts();
        } catch (Exception e) {
            log.error("检测超时告警失败", e);
        }
    }

    @Override
    public void checkAndProcessTimeoutAlerts() {
        log.debug("开始检测指令步骤超时告警");

        LocalDateTime now = LocalDateTime.now();

        // 1. 检测已超时的步骤（超过时限）
        List<EmergencyCommandStepDO> timeoutSteps = getTimeoutSteps();
        for (EmergencyCommandStepDO step : timeoutSteps) {
            if (!Boolean.TRUE.equals(step.getTimeoutFlag())) {
                // 标记为超时
                markStepTimeout(step.getId());

                // 计算超时时长
                long timeoutMinutes = calculateTimeoutMinutes(step, now);

                // 根据超时时长发送不同级别的告警
                if (timeoutMinutes > 30) {
                    sendRedAlert(step);
                } else if (timeoutMinutes > 0) {
                    sendOrangeAlert(step);
                }
            }
        }

        // 2. 检测即将超时的步骤（提前15分钟）
        List<EmergencyCommandStepDO> nearTimeoutSteps = getStepsNearTimeout();
        for (EmergencyCommandStepDO step : nearTimeoutSteps) {
            sendYellowWarning(step);
        }

        log.debug("指令步骤超时告警检测完成");
    }

    @Override
    public void sendYellowWarning(EmergencyCommandStepDO step) {
        log.info("发送黄色预警: stepId={}, commandId={}", step.getId(), step.getCommandId());

        // 获取黄色预警配置
        AlertConfigurationDO config = getAlertConfiguration("command_step_timeout", "yellow");
        if (config == null || !Boolean.TRUE.equals(config.getEnabled())) {
            log.warn("黄色预警配置未启用或不存在");
            return;
        }

        // 发送通知
        alertNotificationService.sendAlert(step, config, "黄色预警：指令步骤即将超时");
    }

    @Override
    public void sendOrangeAlert(EmergencyCommandStepDO step) {
        log.info("发送橙色告警: stepId={}, commandId={}", step.getId(), step.getCommandId());

        // 获取橙色告警配置
        AlertConfigurationDO config = getAlertConfiguration("command_step_timeout", "orange");
        if (config == null || !Boolean.TRUE.equals(config.getEnabled())) {
            log.warn("橙色告警配置未启用或不存在");
            return;
        }

        // 发送通知
        alertNotificationService.sendAlert(step, config, "橙色告警：指令步骤已超时");
    }

    @Override
    public void sendRedAlert(EmergencyCommandStepDO step) {
        log.info("发送红色紧急告警: stepId={}, commandId={}", step.getId(), step.getCommandId());

        // 获取红色告警配置
        AlertConfigurationDO config = getAlertConfiguration("command_step_timeout", "red");
        if (config == null || !Boolean.TRUE.equals(config.getEnabled())) {
            log.warn("红色告警配置未启用或不存在");
            return;
        }

        // 发送通知
        alertNotificationService.sendAlert(step, config, "红色紧急告警：指令步骤严重超时");
    }

    @Override
    public void markStepTimeout(Long stepId) {
        log.info("标记指令步骤超时: stepId={}", stepId);
        commandStepService.markTimeout(stepId, "系统自动检测超时", null);
    }

    @Override
    public List<EmergencyCommandStepDO> getStepsNearTimeout() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime warningTime = now.plusMinutes(15); // 提前15分钟

        List<EmergencyCommandStepDO> steps = commandStepMapper.selectStepsNearTimeout(warningTime);
        return steps.stream()
                .filter(step -> {
                    if (step.getStartTime() == null || step.getTimeLimitMinutes() == null) {
                        return false;
                    }
                    LocalDateTime expectedEndTime = step.getStartTime().plusMinutes(step.getTimeLimitMinutes());
                    long minutesUntilTimeout = ChronoUnit.MINUTES.between(now, expectedEndTime);
                    return minutesUntilTimeout <= 15 && minutesUntilTimeout > 0;
                })
                .toList();
    }

    @Override
    public List<EmergencyCommandStepDO> getTimeoutSteps() {
        LocalDateTime now = LocalDateTime.now();
        List<EmergencyCommandStepDO> steps = commandStepMapper.selectTimeoutSteps(now);
        return steps.stream()
                .filter(step -> {
                    if (step.getStartTime() == null || step.getTimeLimitMinutes() == null) {
                        return false;
                    }
                    LocalDateTime expectedEndTime = step.getStartTime().plusMinutes(step.getTimeLimitMinutes());
                    return now.isAfter(expectedEndTime);
                })
                .toList();
    }

    private long calculateTimeoutMinutes(EmergencyCommandStepDO step, LocalDateTime now) {
        if (step.getStartTime() == null || step.getTimeLimitMinutes() == null) {
            return 0;
        }
        LocalDateTime expectedEndTime = step.getStartTime().plusMinutes(step.getTimeLimitMinutes());
        return ChronoUnit.MINUTES.between(expectedEndTime, now);
    }

    private AlertConfigurationDO getAlertConfiguration(String alertType, String alertLevel) {
        return alertConfigurationMapper.selectByTypeAndLevel(alertType, alertLevel);
    }
}

