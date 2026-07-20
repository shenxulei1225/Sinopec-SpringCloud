package cn.iocoder.yudao.module.emergency.service.alert;

import cn.iocoder.yudao.module.emergency.dal.dataobject.alert.AlertConfigurationDO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.task.EmergencyTaskDO;
import cn.iocoder.yudao.module.emergency.dal.mysql.alert.AlertConfigurationMapper;
import cn.iocoder.yudao.module.emergency.dal.mysql.task.EmergencyTaskMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * 任务超时告警 Service 实现类
 * 
 * 根据任务的startTime和timeLimit计算预期完成时间，超时触发告警
 * 告警级别：
 * - 黄色预警：提前15分钟
 * - 橙色告警：超时5分钟内
 * - 红色紧急：超时超过30分钟
 * 
 * @author 系统生成
 */
@Slf4j
@Service
public class TaskTimeoutAlertServiceImpl implements TaskTimeoutAlertService {
    
    @Autowired
    private EmergencyTaskMapper taskMapper;
    
    @Autowired
    private AlertConfigurationMapper alertConfigurationMapper;
    
    @Autowired
    private TaskAlertNotificationService taskAlertNotificationService;
    
    /**
     * 检测并处理超时告警
     */
    @Override
    public void checkAndProcessTimeoutAlerts() {
        log.debug("开始检测任务超时告警");
        LocalDateTime now = LocalDateTime.now();
        
        // 1. 检测已超时的任务（超过时限）
        List<EmergencyTaskDO> timeoutTasks = getTimeoutTasks();
        for (EmergencyTaskDO task : timeoutTasks) {
            // 计算超时时长
            long timeoutMinutes = calculateTimeoutMinutes(task, now);
            
            // 根据超时时长发送不同级别的告警
            if (timeoutMinutes > 30) {
                sendRedAlert(task);
            } else if (timeoutMinutes > 0) {
                sendOrangeAlert(task);
            }
        }
        
        // 2. 检测即将超时的任务（提前15分钟）
        List<EmergencyTaskDO> nearTimeoutTasks = getTasksNearTimeout();
        for (EmergencyTaskDO task : nearTimeoutTasks) {
            sendYellowWarning(task);
        }
        
        log.debug("任务超时告警检测完成");
    }
    
    /**
     * 发送黄色预警（提前15分钟）
     */
    @Override
    public void sendYellowWarning(EmergencyTaskDO task) {
        log.info("发送黄色预警: taskId={}, taskCode={}", task.getId(), task.getTaskCode());
        
        AlertConfigurationDO config = getAlertConfiguration("task_timeout", "yellow");
        if (config == null || !Boolean.TRUE.equals(config.getEnabled())) {
            log.warn("黄色预警配置未启用或不存在");
            return;
        }
        
        taskAlertNotificationService.sendAlert(task, config, "黄色预警：任务即将超时");
    }
    
    /**
     * 发送橙色告警（超时5分钟内）
     */
    @Override
    public void sendOrangeAlert(EmergencyTaskDO task) {
        log.info("发送橙色告警: taskId={}, taskCode={}", task.getId(), task.getTaskCode());
        
        AlertConfigurationDO config = getAlertConfiguration("task_timeout", "orange");
        if (config == null || !Boolean.TRUE.equals(config.getEnabled())) {
            log.warn("橙色告警配置未启用或不存在");
            return;
        }
        
        taskAlertNotificationService.sendAlert(task, config, "橙色告警：任务已超时");
    }
    
    /**
     * 发送红色紧急告警（超时超过30分钟）
     */
    @Override
    public void sendRedAlert(EmergencyTaskDO task) {
        log.info("发送红色紧急告警: taskId={}, taskCode={}", task.getId(), task.getTaskCode());
        
        AlertConfigurationDO config = getAlertConfiguration("task_timeout", "red");
        if (config == null || !Boolean.TRUE.equals(config.getEnabled())) {
            log.warn("红色告警配置未启用或不存在");
            return;
        }
        
        taskAlertNotificationService.sendAlert(task, config, "红色紧急告警：任务严重超时");
    }
    
    /**
     * 获取即将超时的任务（提前15分钟）
     */
    @Override
    public List<EmergencyTaskDO> getTasksNearTimeout() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime warningTime = now.plusMinutes(15); // 提前15分钟
        
        // 查询进行中的任务（status = 'in_progress'）
        List<EmergencyTaskDO> tasks = taskMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<EmergencyTaskDO>()
                .eq(EmergencyTaskDO::getStatus, "in_progress")
                .isNotNull(EmergencyTaskDO::getStartTime)
                .isNotNull(EmergencyTaskDO::getTimeLimit)
                .eq(EmergencyTaskDO::getDeleted, false)
        );
        
        // 过滤出即将超时的任务
        List<EmergencyTaskDO> nearTimeoutTasks = new ArrayList<>();
        for (EmergencyTaskDO task : tasks) {
            LocalDateTime expectedEndTime = calculateExpectedEndTime(task);
            if (expectedEndTime != null) {
                long minutesUntilTimeout = ChronoUnit.MINUTES.between(now, expectedEndTime);
                if (minutesUntilTimeout <= 15 && minutesUntilTimeout > 0) {
                    nearTimeoutTasks.add(task);
                }
            }
        }
        
        return nearTimeoutTasks;
    }
    
    /**
     * 获取已超时的任务
     */
    @Override
    public List<EmergencyTaskDO> getTimeoutTasks() {
        LocalDateTime now = LocalDateTime.now();
        
        // 查询进行中的任务（status = 'in_progress'）
        List<EmergencyTaskDO> tasks = taskMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<EmergencyTaskDO>()
                .eq(EmergencyTaskDO::getStatus, "in_progress")
                .isNotNull(EmergencyTaskDO::getStartTime)
                .isNotNull(EmergencyTaskDO::getTimeLimit)
                .eq(EmergencyTaskDO::getDeleted, false)
        );
        
        // 过滤出已超时的任务
        List<EmergencyTaskDO> timeoutTasks = new ArrayList<>();
        for (EmergencyTaskDO task : tasks) {
            LocalDateTime expectedEndTime = calculateExpectedEndTime(task);
            if (expectedEndTime != null && now.isAfter(expectedEndTime)) {
                timeoutTasks.add(task);
            }
        }
        
        return timeoutTasks;
    }
    
    /**
     * 计算预期完成时间
     * 
     * @param task 任务
     * @return 预期完成时间，如果无法计算则返回null
     */
    private LocalDateTime calculateExpectedEndTime(EmergencyTaskDO task) {
        if (task.getStartTime() == null || task.getTimeLimit() == null || task.getTimeLimit() <= 0) {
            return null;
        }
        return task.getStartTime().plusMinutes(task.getTimeLimit());
    }
    
    /**
     * 计算超时时长（分钟）
     * 
     * @param task 任务
     * @param now 当前时间
     * @return 超时时长（分钟），如果未超时则返回0或负数
     */
    private long calculateTimeoutMinutes(EmergencyTaskDO task, LocalDateTime now) {
        LocalDateTime expectedEndTime = calculateExpectedEndTime(task);
        if (expectedEndTime == null) {
            return 0;
        }
        if (now.isBefore(expectedEndTime)) {
            return 0; // 未超时
        }
        return ChronoUnit.MINUTES.between(expectedEndTime, now);
    }
    
    /**
     * 获取告警配置
     * 
     * @param alertType 告警类型
     * @param alertLevel 告警级别
     * @return 告警配置
     */
    private AlertConfigurationDO getAlertConfiguration(String alertType, String alertLevel) {
        return alertConfigurationMapper.selectByTypeAndLevel(alertType, alertLevel);
    }
}








