package cn.cheers.x.module.dynamicbusiness.dal.mysql.dynamictable;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.dynamictable.DynamicSqlAuditLogDO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 动态 SQL 执行审计日志 Mapper
 * 
 * @author yudao
 */
@Mapper
public interface DynamicSqlAuditLogMapper extends BaseMapperX<DynamicSqlAuditLogDO> {

    /**
     * 根据表名查询审计日志
     */
    default List<DynamicSqlAuditLogDO> selectByTableName(String tableName) {
        return selectList(new LambdaQueryWrapperX<DynamicSqlAuditLogDO>()
                .eq(DynamicSqlAuditLogDO::getTableName, tableName)
                .orderByDesc(DynamicSqlAuditLogDO::getExecutionTime));
    }

    /**
     * 根据操作类型查询审计日志
     */
    default List<DynamicSqlAuditLogDO> selectByOperationType(String operationType) {
        return selectList(new LambdaQueryWrapperX<DynamicSqlAuditLogDO>()
                .eq(DynamicSqlAuditLogDO::getOperationType, operationType)
                .orderByDesc(DynamicSqlAuditLogDO::getExecutionTime));
    }

    /**
     * 根据时间范围查询审计日志
     */
    default List<DynamicSqlAuditLogDO> selectByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return selectList(new LambdaQueryWrapperX<DynamicSqlAuditLogDO>()
                .ge(DynamicSqlAuditLogDO::getExecutionTime, startTime)
                .le(DynamicSqlAuditLogDO::getExecutionTime, endTime)
                .orderByDesc(DynamicSqlAuditLogDO::getExecutionTime));
    }

    /**
     * 查询失败的审计日志
     */
    default List<DynamicSqlAuditLogDO> selectFailedLogs() {
        return selectList(new LambdaQueryWrapperX<DynamicSqlAuditLogDO>()
                .eq(DynamicSqlAuditLogDO::getSuccess, false)
                .orderByDesc(DynamicSqlAuditLogDO::getExecutionTime));
    }

    /**
     * 根据操作人查询审计日志
     */
    default List<DynamicSqlAuditLogDO> selectByOperatorId(Long operatorId) {
        return selectList(new LambdaQueryWrapperX<DynamicSqlAuditLogDO>()
                .eq(DynamicSqlAuditLogDO::getOperatorId, operatorId)
                .orderByDesc(DynamicSqlAuditLogDO::getExecutionTime));
    }

    /**
     * 删除指定时间之前的审计日志（用于日志清理）
     */
    default int deleteBeforeTime(LocalDateTime beforeTime) {
        return delete(new LambdaQueryWrapperX<DynamicSqlAuditLogDO>()
                .lt(DynamicSqlAuditLogDO::getExecutionTime, beforeTime));
    }
}
