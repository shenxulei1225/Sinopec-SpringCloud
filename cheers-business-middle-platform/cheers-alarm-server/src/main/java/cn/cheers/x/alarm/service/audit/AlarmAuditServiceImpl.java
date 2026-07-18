package cn.cheers.x.alarm.service.audit;

import cn.cheers.x.alarm.dal.dataobject.AlarmAuditLogDO;
import cn.cheers.x.alarm.dal.dataobject.AlarmDO;
import cn.cheers.x.alarm.dal.dataobject.LinkageExecutionDO;
import cn.cheers.x.alarm.dal.mysql.AlarmAuditLogMapper;
import cn.cheers.x.alarm.enums.AlarmAuditOperationTypeEnum;
import cn.cheers.x.alarm.enums.AlarmLevelEnum;
import cn.cheers.x.alarm.enums.AlarmSourceEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 告警审计服务实现类
 * 
 * <p>实现告警操作和联动控制的审计日志记录，满足以下业务规则：
 * <ul>
 *   <li>BR-BIZ-008：所有告警操作（确认、处理、关闭）必须记录操作人、操作时间、操作内容</li>
 *   <li>BR-BIZ-009：所有联动控制动作必须记录控制设备、控制动作、执行结果、触发告警</li>
 * </ul>
 * </p>
 * 
 * <p>审计日志采用异步写入方式，避免影响主业务流程性能。</p>
 *
 * @author 告警管理模块
 */
@Service
@Slf4j
public class AlarmAuditServiceImpl implements AlarmAuditService {

    @Resource
    private AlarmAuditLogMapper alarmAuditLogMapper;

    // ========== 告警操作审计 ==========

    @Override
    @Async
    public void logAlarmCreate(AlarmDO alarm, String ipAddress) {
        if (alarm == null) {
            log.warn("告警创建审计日志记录失败：告警信息为空");
            return;
        }

        try {
            String content = buildAlarmCreateContent(alarm);
            
            // 系统自动触发的告警，操作人为系统
            Long operatorId = null;
            String operatorName = "系统";
            if (AlarmSourceEnum.MANUAL.getSource().equals(alarm.getAlarmSource())) {
                // 人工上报的告警，使用创建人信息
                operatorName = alarm.getCreator();
            }

            AlarmAuditLogDO auditLog = buildAuditLog(
                    alarm.getId(),
                    AlarmAuditOperationTypeEnum.CREATE.getType(),
                    content,
                    operatorId,
                    operatorName,
                    ipAddress,
                    alarm.getTenantId()
            );

            alarmAuditLogMapper.insert(auditLog);
            log.debug("告警创建审计日志记录成功: alarmId={}, alarmCode={}", 
                    alarm.getId(), alarm.getAlarmCode());
        } catch (Exception e) {
            log.error("告警创建审计日志记录失败: alarmId={}", alarm.getId(), e);
        }
    }

    @Override
    @Async
    public void logAlarmAcknowledge(AlarmDO alarm, Long operatorId, String operatorName,
                                     String remark, String ipAddress) {
        if (alarm == null) {
            log.warn("告警确认审计日志记录失败：告警信息为空");
            return;
        }

        try {
            String content = buildAlarmAcknowledgeContent(alarm, remark);

            AlarmAuditLogDO auditLog = buildAuditLog(
                    alarm.getId(),
                    AlarmAuditOperationTypeEnum.ACKNOWLEDGE.getType(),
                    content,
                    operatorId,
                    operatorName,
                    ipAddress,
                    alarm.getTenantId()
            );

            alarmAuditLogMapper.insert(auditLog);
            log.debug("告警确认审计日志记录成功: alarmId={}, operatorId={}", 
                    alarm.getId(), operatorId);
        } catch (Exception e) {
            log.error("告警确认审计日志记录失败: alarmId={}", alarm.getId(), e);
        }
    }

    @Override
    @Async
    public void logAlarmHandle(AlarmDO alarm, Long operatorId, String operatorName,
                                String measure, String result, String ipAddress) {
        if (alarm == null) {
            log.warn("告警处理审计日志记录失败：告警信息为空");
            return;
        }

        try {
            String content = buildAlarmHandleContent(alarm, measure, result);

            AlarmAuditLogDO auditLog = buildAuditLog(
                    alarm.getId(),
                    AlarmAuditOperationTypeEnum.HANDLE.getType(),
                    content,
                    operatorId,
                    operatorName,
                    ipAddress,
                    alarm.getTenantId()
            );

            alarmAuditLogMapper.insert(auditLog);
            log.debug("告警处理审计日志记录成功: alarmId={}, operatorId={}", 
                    alarm.getId(), operatorId);
        } catch (Exception e) {
            log.error("告警处理审计日志记录失败: alarmId={}", alarm.getId(), e);
        }
    }

    @Override
    @Async
    public void logAlarmClose(AlarmDO alarm, Long operatorId, String operatorName,
                               String closeReason, String remark, String ipAddress) {
        if (alarm == null) {
            log.warn("告警关闭审计日志记录失败：告警信息为空");
            return;
        }

        try {
            String content = buildAlarmCloseContent(alarm, closeReason, remark);

            AlarmAuditLogDO auditLog = buildAuditLog(
                    alarm.getId(),
                    AlarmAuditOperationTypeEnum.CLOSE.getType(),
                    content,
                    operatorId,
                    operatorName,
                    ipAddress,
                    alarm.getTenantId()
            );

            alarmAuditLogMapper.insert(auditLog);
            log.debug("告警关闭审计日志记录成功: alarmId={}, operatorId={}", 
                    alarm.getId(), operatorId);
        } catch (Exception e) {
            log.error("告警关闭审计日志记录失败: alarmId={}", alarm.getId(), e);
        }
    }

    @Override
    @Async
    public void logAlarmEscalate(AlarmDO alarm) {
        if (alarm == null) {
            log.warn("告警升级审计日志记录失败：告警信息为空");
            return;
        }

        try {
            String content = buildAlarmEscalateContent(alarm);

            AlarmAuditLogDO auditLog = buildAuditLog(
                    alarm.getId(),
                    AlarmAuditOperationTypeEnum.ESCALATE.getType(),
                    content,
                    null,
                    "系统",
                    null,
                    alarm.getTenantId()
            );

            alarmAuditLogMapper.insert(auditLog);
            log.debug("告警升级审计日志记录成功: alarmId={}", alarm.getId());
        } catch (Exception e) {
            log.error("告警升级审计日志记录失败: alarmId={}", alarm.getId(), e);
        }
    }

    // ========== 联动控制审计 ==========

    @Override
    @Async
    public void logLinkageExecute(LinkageExecutionDO execution, AlarmDO alarm) {
        if (execution == null) {
            log.warn("联动执行审计日志记录失败：执行记录为空");
            return;
        }

        try {
            String content = buildLinkageExecuteContent(execution, alarm);

            AlarmAuditLogDO auditLog = buildAuditLog(
                    execution.getAlarmId(),
                    AlarmAuditOperationTypeEnum.LINKAGE_EXECUTE.getType(),
                    content,
                    null,
                    "系统",
                    null,
                    execution.getTenantId()
            );

            alarmAuditLogMapper.insert(auditLog);
            log.debug("联动执行审计日志记录成功: executionId={}, alarmId={}", 
                    execution.getId(), execution.getAlarmId());
        } catch (Exception e) {
            log.error("联动执行审计日志记录失败: executionId={}", execution.getId(), e);
        }
    }

    @Override
    @Async
    public void logLinkageRetry(LinkageExecutionDO execution, AlarmDO alarm, int retryCount) {
        if (execution == null) {
            log.warn("联动重试审计日志记录失败：执行记录为空");
            return;
        }

        try {
            String content = buildLinkageRetryContent(execution, alarm, retryCount);

            AlarmAuditLogDO auditLog = buildAuditLog(
                    execution.getAlarmId(),
                    AlarmAuditOperationTypeEnum.LINKAGE_RETRY.getType(),
                    content,
                    null,
                    "系统",
                    null,
                    execution.getTenantId()
            );

            alarmAuditLogMapper.insert(auditLog);
            log.debug("联动重试审计日志记录成功: executionId={}, retryCount={}", 
                    execution.getId(), retryCount);
        } catch (Exception e) {
            log.error("联动重试审计日志记录失败: executionId={}", execution.getId(), e);
        }
    }

    @Override
    @Async
    public void logLinkageManualIntervention(LinkageExecutionDO execution, AlarmDO alarm,
                                              Long operatorId, String operatorName, String ipAddress) {
        if (execution == null) {
            log.warn("联动人工介入审计日志记录失败：执行记录为空");
            return;
        }

        try {
            String content = buildLinkageManualInterventionContent(execution, alarm);

            AlarmAuditLogDO auditLog = buildAuditLog(
                    execution.getAlarmId(),
                    AlarmAuditOperationTypeEnum.LINKAGE_MANUAL.getType(),
                    content,
                    operatorId,
                    operatorName,
                    ipAddress,
                    execution.getTenantId()
            );

            alarmAuditLogMapper.insert(auditLog);
            log.debug("联动人工介入审计日志记录成功: executionId={}, operatorId={}", 
                    execution.getId(), operatorId);
        } catch (Exception e) {
            log.error("联动人工介入审计日志记录失败: executionId={}", execution.getId(), e);
        }
    }

    // ========== 审计日志查询 ==========

    @Override
    public List<AlarmAuditLogDO> getAuditLogsByAlarmId(Long alarmId) {
        return alarmAuditLogMapper.selectListByAlarmId(alarmId);
    }

    @Override
    public List<AlarmAuditLogDO> getAuditLogsByOperationType(String operationType) {
        return alarmAuditLogMapper.selectListByOperationType(operationType);
    }

    @Override
    public List<AlarmAuditLogDO> getAuditLogsByOperatorId(Long operatorId) {
        return alarmAuditLogMapper.selectListByOperatorId(operatorId);
    }

    @Override
    public List<AlarmAuditLogDO> getAuditLogsByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return alarmAuditLogMapper.selectListByTimeRange(startTime, endTime);
    }

    @Override
    public AlarmAuditLogDO getLatestAuditLog(Long alarmId) {
        return alarmAuditLogMapper.selectLatestByAlarmId(alarmId);
    }

    @Override
    public Long countAuditLogs(String operationType, LocalDateTime startTime, LocalDateTime endTime) {
        return alarmAuditLogMapper.selectCountByOperationTypeAndTimeRange(operationType, startTime, endTime);
    }

    // ========== 私有方法 ==========

    /**
     * 构建审计日志对象
     */
    private AlarmAuditLogDO buildAuditLog(Long alarmId, String operationType, String content,
                                           Long operatorId, String operatorName, 
                                           String ipAddress, Long tenantId) {
        LocalDateTime now = LocalDateTime.now();
        return AlarmAuditLogDO.builder()
                .alarmId(alarmId)
                .operationType(operationType)
                .operationContent(content)
                .operatorId(operatorId)
                .operatorName(operatorName)
                .operationTime(now)
                .ipAddress(ipAddress)
                .tenantId(tenantId != null ? tenantId : 0L)
                .createTime(now)
                .build();
    }

    /**
     * 构建告警创建审计内容
     */
    private String buildAlarmCreateContent(AlarmDO alarm) {
        StringBuilder content = new StringBuilder();
        content.append("创建告警：");
        content.append("编号[").append(alarm.getAlarmCode()).append("]，");
        content.append("级别[").append(getLevelText(alarm.getAlarmLevel())).append("]，");
        content.append("类型[").append(alarm.getAlarmTypePath()).append("]，");
        content.append("来源[").append(getSourceText(alarm.getAlarmSource())).append("]");
        if (alarm.getDeviceName() != null) {
            content.append("，设备[").append(alarm.getDeviceName()).append("]");
        }
        if (alarm.getLocationName() != null) {
            content.append("，位置[").append(alarm.getLocationName()).append("]");
        }
        return content.toString();
    }

    /**
     * 构建告警确认审计内容
     */
    private String buildAlarmAcknowledgeContent(AlarmDO alarm, String remark) {
        StringBuilder content = new StringBuilder();
        content.append("确认告警：");
        content.append("编号[").append(alarm.getAlarmCode()).append("]");
        if (remark != null && !remark.isEmpty()) {
            content.append("，备注[").append(remark).append("]");
        }
        return content.toString();
    }

    /**
     * 构建告警处理审计内容
     */
    private String buildAlarmHandleContent(AlarmDO alarm, String measure, String result) {
        StringBuilder content = new StringBuilder();
        content.append("处理告警：");
        content.append("编号[").append(alarm.getAlarmCode()).append("]，");
        content.append("处理措施[").append(measure).append("]，");
        content.append("处理结果[").append(result).append("]");
        return content.toString();
    }

    /**
     * 构建告警关闭审计内容
     */
    private String buildAlarmCloseContent(AlarmDO alarm, String closeReason, String remark) {
        StringBuilder content = new StringBuilder();
        content.append("关闭告警：");
        content.append("编号[").append(alarm.getAlarmCode()).append("]，");
        content.append("关闭原因[").append(closeReason).append("]");
        if (remark != null && !remark.isEmpty()) {
            content.append("，备注[").append(remark).append("]");
        }
        return content.toString();
    }

    /**
     * 构建告警升级审计内容
     */
    private String buildAlarmEscalateContent(AlarmDO alarm) {
        StringBuilder content = new StringBuilder();
        content.append("告警升级：");
        content.append("编号[").append(alarm.getAlarmCode()).append("]，");
        content.append("级别[").append(getLevelText(alarm.getAlarmLevel())).append("]，");
        content.append("升级级别[").append(alarm.getEscalationLevel()).append("]，");
        content.append("原因[告警超时未确认]");
        return content.toString();
    }

    /**
     * 构建联动执行审计内容
     */
    private String buildLinkageExecuteContent(LinkageExecutionDO execution, AlarmDO alarm) {
        StringBuilder content = new StringBuilder();
        content.append("执行联动：");
        if (alarm != null) {
            content.append("告警编号[").append(alarm.getAlarmCode()).append("]，");
        }
        content.append("动作类型[").append(execution.getActionType()).append("]，");
        if (execution.getTargetDeviceName() != null) {
            content.append("目标设备[").append(execution.getTargetDeviceName()).append("]，");
        }
        content.append("执行状态[").append(execution.getExecutionStatus()).append("]");
        if (execution.getExecutionResult() != null) {
            content.append("，执行结果[").append(execution.getExecutionResult()).append("]");
        }
        if (execution.getDurationMs() != null) {
            content.append("，耗时[").append(execution.getDurationMs()).append("ms]");
        }
        return content.toString();
    }

    /**
     * 构建联动重试审计内容
     */
    private String buildLinkageRetryContent(LinkageExecutionDO execution, AlarmDO alarm, int retryCount) {
        StringBuilder content = new StringBuilder();
        content.append("联动重试：");
        if (alarm != null) {
            content.append("告警编号[").append(alarm.getAlarmCode()).append("]，");
        }
        content.append("动作类型[").append(execution.getActionType()).append("]，");
        if (execution.getTargetDeviceName() != null) {
            content.append("目标设备[").append(execution.getTargetDeviceName()).append("]，");
        }
        content.append("重试次数[").append(retryCount).append("/3]");
        if (execution.getErrorMessage() != null) {
            content.append("，错误信息[").append(execution.getErrorMessage()).append("]");
        }
        return content.toString();
    }

    /**
     * 构建联动人工介入审计内容
     */
    private String buildLinkageManualInterventionContent(LinkageExecutionDO execution, AlarmDO alarm) {
        StringBuilder content = new StringBuilder();
        content.append("联动人工介入：");
        if (alarm != null) {
            content.append("告警编号[").append(alarm.getAlarmCode()).append("]，");
        }
        content.append("动作类型[").append(execution.getActionType()).append("]，");
        if (execution.getTargetDeviceName() != null) {
            content.append("目标设备[").append(execution.getTargetDeviceName()).append("]，");
        }
        content.append("重试次数[").append(execution.getRetryCount()).append("]，");
        content.append("原因[联动执行失败，需人工处理]");
        if (execution.getErrorMessage() != null) {
            content.append("，错误信息[").append(execution.getErrorMessage()).append("]");
        }
        return content.toString();
    }

    /**
     * 获取告警级别文本
     */
    private String getLevelText(String level) {
        AlarmLevelEnum levelEnum = AlarmLevelEnum.getByLevel(level);
        return levelEnum != null ? levelEnum.getName() : level;
    }

    /**
     * 获取告警来源文本
     */
    private String getSourceText(String source) {
        AlarmSourceEnum sourceEnum = AlarmSourceEnum.getBySource(source);
        return sourceEnum != null ? sourceEnum.getName() : source;
    }

}
