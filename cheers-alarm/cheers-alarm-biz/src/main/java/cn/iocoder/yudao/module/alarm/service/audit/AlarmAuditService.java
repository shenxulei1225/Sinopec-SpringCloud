package cn.iocoder.yudao.module.alarm.service.audit;

import cn.iocoder.yudao.module.alarm.dal.dataobject.AlarmAuditLogDO;
import cn.iocoder.yudao.module.alarm.dal.dataobject.AlarmDO;
import cn.iocoder.yudao.module.alarm.dal.dataobject.LinkageExecutionDO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 告警审计服务接口
 * 
 * <p>负责记录告警操作和联动控制的审计日志，满足以下业务规则：
 * <ul>
 *   <li>BR-BIZ-008：所有告警操作（确认、处理、关闭）必须记录操作人、操作时间、操作内容</li>
 *   <li>BR-BIZ-009：所有联动控制动作必须记录控制设备、控制动作、执行结果、触发告警</li>
 * </ul>
 * </p>
 *
 * @author 告警管理模块
 */
public interface AlarmAuditService {

    // ========== 告警操作审计 ==========

    /**
     * 记录告警创建审计日志
     *
     * @param alarm     告警信息
     * @param ipAddress IP地址
     */
    void logAlarmCreate(AlarmDO alarm, String ipAddress);

    /**
     * 记录告警确认审计日志
     *
     * @param alarm      告警信息
     * @param operatorId 操作人ID
     * @param operatorName 操作人姓名
     * @param remark     确认备注
     * @param ipAddress  IP地址
     */
    void logAlarmAcknowledge(AlarmDO alarm, Long operatorId, String operatorName, 
                             String remark, String ipAddress);

    /**
     * 记录告警处理审计日志
     *
     * @param alarm        告警信息
     * @param operatorId   操作人ID
     * @param operatorName 操作人姓名
     * @param measure      处理措施
     * @param result       处理结果
     * @param ipAddress    IP地址
     */
    void logAlarmHandle(AlarmDO alarm, Long operatorId, String operatorName, 
                        String measure, String result, String ipAddress);

    /**
     * 记录告警关闭审计日志
     *
     * @param alarm        告警信息
     * @param operatorId   操作人ID
     * @param operatorName 操作人姓名
     * @param closeReason  关闭原因
     * @param remark       关闭备注
     * @param ipAddress    IP地址
     */
    void logAlarmClose(AlarmDO alarm, Long operatorId, String operatorName, 
                       String closeReason, String remark, String ipAddress);

    /**
     * 记录告警升级审计日志
     *
     * @param alarm 告警信息
     */
    void logAlarmEscalate(AlarmDO alarm);

    // ========== 联动控制审计 ==========

    /**
     * 记录联动执行审计日志
     *
     * @param execution 联动执行记录
     * @param alarm     关联的告警信息
     */
    void logLinkageExecute(LinkageExecutionDO execution, AlarmDO alarm);

    /**
     * 记录联动重试审计日志
     *
     * @param execution  联动执行记录
     * @param alarm      关联的告警信息
     * @param retryCount 当前重试次数
     */
    void logLinkageRetry(LinkageExecutionDO execution, AlarmDO alarm, int retryCount);

    /**
     * 记录联动人工介入审计日志
     *
     * @param execution    联动执行记录
     * @param alarm        关联的告警信息
     * @param operatorId   操作人ID
     * @param operatorName 操作人姓名
     * @param ipAddress    IP地址
     */
    void logLinkageManualIntervention(LinkageExecutionDO execution, AlarmDO alarm,
                                       Long operatorId, String operatorName, String ipAddress);

    // ========== 审计日志查询 ==========

    /**
     * 根据告警ID查询审计日志
     *
     * @param alarmId 告警ID
     * @return 审计日志列表
     */
    List<AlarmAuditLogDO> getAuditLogsByAlarmId(Long alarmId);

    /**
     * 根据操作类型查询审计日志
     *
     * @param operationType 操作类型
     * @return 审计日志列表
     */
    List<AlarmAuditLogDO> getAuditLogsByOperationType(String operationType);

    /**
     * 根据操作人ID查询审计日志
     *
     * @param operatorId 操作人ID
     * @return 审计日志列表
     */
    List<AlarmAuditLogDO> getAuditLogsByOperatorId(Long operatorId);

    /**
     * 查询指定时间范围内的审计日志
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 审计日志列表
     */
    List<AlarmAuditLogDO> getAuditLogsByTimeRange(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取告警的最新操作日志
     *
     * @param alarmId 告警ID
     * @return 最新的审计日志
     */
    AlarmAuditLogDO getLatestAuditLog(Long alarmId);

    /**
     * 统计指定时间范围内的操作次数
     *
     * @param operationType 操作类型
     * @param startTime     开始时间
     * @param endTime       结束时间
     * @return 操作次数
     */
    Long countAuditLogs(String operationType, LocalDateTime startTime, LocalDateTime endTime);

}
