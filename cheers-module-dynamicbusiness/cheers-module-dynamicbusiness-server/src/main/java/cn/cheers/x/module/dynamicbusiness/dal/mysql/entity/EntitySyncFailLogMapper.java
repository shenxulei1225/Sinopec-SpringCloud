package cn.cheers.x.module.dynamicbusiness.dal.mysql.entity;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntitySyncFailLogDO;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * 同步失败日志 Mapper
 *
 * 注意：此表没有软删除字段（deleted），所有查询不需要过滤 deleted 条件
 *
 * @author yudao
 */
@Mapper
public interface EntitySyncFailLogMapper extends BaseMapperX<EntitySyncFailLogDO> {

    /**
     * 根据实体ID查询失败日志
     */
    default List<EntitySyncFailLogDO> selectByEntityId(Long entityId) {
        return selectList(new LambdaQueryWrapperX<EntitySyncFailLogDO>()
                .eq(EntitySyncFailLogDO::getEntityId, entityId)
                .orderByDesc(EntitySyncFailLogDO::getCreateTime));
    }

    /**
     * 根据实体ID查询最新的失败日志（单条）
     *
     * <p>用于获取实体的 entityTypeCode 等信息。</p>
     */
    default EntitySyncFailLogDO selectOneByEntityId(Long entityId) {
        return selectOne(new LambdaQueryWrapperX<EntitySyncFailLogDO>()
                .eq(EntitySyncFailLogDO::getEntityId, entityId)
                .orderByDesc(EntitySyncFailLogDO::getCreateTime)
                .last("LIMIT 1"));
    }

    /**
     * 根据状态查询失败日志
     */
    default List<EntitySyncFailLogDO> selectByStatus(String status) {
        return selectList(new LambdaQueryWrapperX<EntitySyncFailLogDO>()
                .eq(EntitySyncFailLogDO::getStatus, status)
                .orderByAsc(EntitySyncFailLogDO::getCreateTime));
    }

    /**
     * 查询待处理的失败日志（用于重试）
     */
    default List<EntitySyncFailLogDO> selectPendingLogs(int limit) {
        if (limit <= 0) {
            return Collections.emptyList();
        }
        return selectList(new LambdaQueryWrapperX<EntitySyncFailLogDO>()
                .eq(EntitySyncFailLogDO::getStatus, EntitySyncFailLogDO.STATUS_PENDING)
                .orderByAsc(EntitySyncFailLogDO::getCreateTime)
                .last("LIMIT " + limit));
    }

    /**
     * 分页查询失败日志
     */
    default PageResult<EntitySyncFailLogDO> selectPage(PageParam pageParam, String status, Long modelId, String engineType) {
        return selectPage(pageParam, new LambdaQueryWrapperX<EntitySyncFailLogDO>()
                .eqIfPresent(EntitySyncFailLogDO::getStatus, status)
                .eqIfPresent(EntitySyncFailLogDO::getModelId, modelId)
                .eqIfPresent(EntitySyncFailLogDO::getEngineType, engineType)
                .orderByDesc(EntitySyncFailLogDO::getCreateTime));
    }

    /**
     * 更新状态
     */
    default int updateStatus(Long id, String status, LocalDateTime updatedAt) {
        LambdaUpdateWrapper<EntitySyncFailLogDO> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(EntitySyncFailLogDO::getId, id)
                .set(EntitySyncFailLogDO::getStatus, status)
                .set(EntitySyncFailLogDO::getUpdateTime, updatedAt);
        return update(null, wrapper);
    }

    /**
     * 更新重试信息
     */
    default int updateRetryInfo(Long id, String status,
                                LocalDateTime lastRetryAt, LocalDateTime updatedAt) {
        LambdaUpdateWrapper<EntitySyncFailLogDO> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(EntitySyncFailLogDO::getId, id)
                .setSql("retry_count = COALESCE(retry_count, 0) + 1")
                .set(EntitySyncFailLogDO::getLastRetryAt, lastRetryAt)
                .set(EntitySyncFailLogDO::getStatus, status)
                .set(EntitySyncFailLogDO::getUpdateTime, updatedAt);
        return update(null, wrapper);
    }

    /**
     * 统计指定状态的日志数量
     */
    default Long countByStatus(String status) {
        return selectCount(new LambdaQueryWrapperX<EntitySyncFailLogDO>()
                .eq(EntitySyncFailLogDO::getStatus, status));
    }

    /**
     * 统计指定状态和业务类型的日志数量
     */
    default Long countByStatusAndEntityTypeCode(String status, String entityTypeCode) {
        return selectCount(new LambdaQueryWrapperX<EntitySyncFailLogDO>()
                .eq(EntitySyncFailLogDO::getStatus, status)
                .eq(EntitySyncFailLogDO::getEntityTypeCode, entityTypeCode));
    }

    /**
     * 查询待处理的失败日志（按业务类型过滤）
     */
    default List<EntitySyncFailLogDO> selectPendingLogsByEntityTypeCode(String entityTypeCode, int limit) {
        if (limit <= 0) {
            return Collections.emptyList();
        }
        return selectList(new LambdaQueryWrapperX<EntitySyncFailLogDO>()
                .eq(EntitySyncFailLogDO::getStatus, EntitySyncFailLogDO.STATUS_PENDING)
                .eq(EntitySyncFailLogDO::getEntityTypeCode, entityTypeCode)
                .orderByAsc(EntitySyncFailLogDO::getCreateTime)
                .last("LIMIT " + limit));
    }

    /**
     * 统计指定实体的连续失败次数
     */
    default Long countFailedByEntityId(Long entityId) {
        return selectCount(new LambdaQueryWrapperX<EntitySyncFailLogDO>()
                .eq(EntitySyncFailLogDO::getEntityId, entityId)
                .eq(EntitySyncFailLogDO::getStatus, EntitySyncFailLogDO.STATUS_FAILED));
    }

    /**
     * 根据时间范围查询失败日志（用于补同步）
     */
    default List<EntitySyncFailLogDO> selectByTimeRange(LocalDateTime startTime, LocalDateTime endTime, String status) {
        LambdaQueryWrapperX<EntitySyncFailLogDO> wrapper = new LambdaQueryWrapperX<>();
        wrapper.ge(EntitySyncFailLogDO::getCreateTime, startTime)
               .le(EntitySyncFailLogDO::getCreateTime, endTime)
               .orderByAsc(EntitySyncFailLogDO::getCreateTime);
        if (status != null) {
            wrapper.eq(EntitySyncFailLogDO::getStatus, status);
        }
        return selectList(wrapper);
    }

    /**
     * 查询指定实体的最新失败日志
     */
    default EntitySyncFailLogDO selectLatestByEntityId(Long entityId) {
        return selectOne(new LambdaQueryWrapperX<EntitySyncFailLogDO>()
                .eq(EntitySyncFailLogDO::getEntityId, entityId)
                .orderByDesc(EntitySyncFailLogDO::getCreateTime)
                .last("LIMIT 1"));
    }

    /**
     * 根据实体ID删除失败日志
     * 
     * <p>用于同步成功后清理之前的失败记录。</p>
     */
    default int deleteByEntityId(Long entityId) {
        return delete(new LambdaQueryWrapperX<EntitySyncFailLogDO>()
                .eq(EntitySyncFailLogDO::getEntityId, entityId));
    }
}
