package cn.iocoder.yudao.module.alarm.dal.mysql;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.alarm.dal.dataobject.AlarmAuditLogDO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 告警审计日志 Mapper
 *
 * @author 告警管理模块
 */
@Mapper
public interface AlarmAuditLogMapper extends BaseMapperX<AlarmAuditLogDO> {

    /**
     * 根据告警ID查询审计日志
     *
     * @param alarmId 告警ID
     * @return 审计日志列表
     */
    default List<AlarmAuditLogDO> selectListByAlarmId(Long alarmId) {
        return selectList(new LambdaQueryWrapperX<AlarmAuditLogDO>()
                .eq(AlarmAuditLogDO::getAlarmId, alarmId)
                .orderByAsc(AlarmAuditLogDO::getOperationTime));
    }

    /**
     * 根据操作类型查询审计日志
     *
     * @param operationType 操作类型
     * @return 审计日志列表
     */
    default List<AlarmAuditLogDO> selectListByOperationType(String operationType) {
        return selectList(new LambdaQueryWrapperX<AlarmAuditLogDO>()
                .eq(AlarmAuditLogDO::getOperationType, operationType)
                .orderByDesc(AlarmAuditLogDO::getOperationTime));
    }

    /**
     * 根据操作人ID查询审计日志
     *
     * @param operatorId 操作人ID
     * @return 审计日志列表
     */
    default List<AlarmAuditLogDO> selectListByOperatorId(Long operatorId) {
        return selectList(new LambdaQueryWrapperX<AlarmAuditLogDO>()
                .eq(AlarmAuditLogDO::getOperatorId, operatorId)
                .orderByDesc(AlarmAuditLogDO::getOperationTime));
    }

    /**
     * 查询指定时间范围内的审计日志
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 审计日志列表
     */
    default List<AlarmAuditLogDO> selectListByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return selectList(new LambdaQueryWrapperX<AlarmAuditLogDO>()
                .ge(AlarmAuditLogDO::getOperationTime, startTime)
                .le(AlarmAuditLogDO::getOperationTime, endTime)
                .orderByDesc(AlarmAuditLogDO::getOperationTime));
    }

    /**
     * 根据告警ID和操作类型查询审计日志
     *
     * @param alarmId       告警ID
     * @param operationType 操作类型
     * @return 审计日志列表
     */
    default List<AlarmAuditLogDO> selectListByAlarmIdAndOperationType(Long alarmId, String operationType) {
        return selectList(new LambdaQueryWrapperX<AlarmAuditLogDO>()
                .eq(AlarmAuditLogDO::getAlarmId, alarmId)
                .eq(AlarmAuditLogDO::getOperationType, operationType)
                .orderByAsc(AlarmAuditLogDO::getOperationTime));
    }

    /**
     * 查询告警的最新操作日志
     *
     * @param alarmId 告警ID
     * @return 最新的审计日志
     */
    default AlarmAuditLogDO selectLatestByAlarmId(Long alarmId) {
        return selectOne(new LambdaQueryWrapperX<AlarmAuditLogDO>()
                .eq(AlarmAuditLogDO::getAlarmId, alarmId)
                .orderByDesc(AlarmAuditLogDO::getOperationTime)
                .last("LIMIT 1"));
    }

    /**
     * 统计指定时间范围内的操作次数
     *
     * @param operationType 操作类型
     * @param startTime     开始时间
     * @param endTime       结束时间
     * @return 操作次数
     */
    default Long selectCountByOperationTypeAndTimeRange(String operationType, 
                                                         LocalDateTime startTime, 
                                                         LocalDateTime endTime) {
        return selectCount(new LambdaQueryWrapperX<AlarmAuditLogDO>()
                .eq(AlarmAuditLogDO::getOperationType, operationType)
                .ge(AlarmAuditLogDO::getOperationTime, startTime)
                .le(AlarmAuditLogDO::getOperationTime, endTime));
    }

    /**
     * 根据IP地址查询审计日志
     *
     * @param ipAddress IP地址
     * @return 审计日志列表
     */
    default List<AlarmAuditLogDO> selectListByIpAddress(String ipAddress) {
        return selectList(new LambdaQueryWrapperX<AlarmAuditLogDO>()
                .eq(AlarmAuditLogDO::getIpAddress, ipAddress)
                .orderByDesc(AlarmAuditLogDO::getOperationTime));
    }

    /**
     * 删除指定告警的所有审计日志
     *
     * @param alarmId 告警ID
     * @return 删除数量
     */
    default int deleteByAlarmId(Long alarmId) {
        return delete(new LambdaQueryWrapperX<AlarmAuditLogDO>()
                .eq(AlarmAuditLogDO::getAlarmId, alarmId));
    }

}
