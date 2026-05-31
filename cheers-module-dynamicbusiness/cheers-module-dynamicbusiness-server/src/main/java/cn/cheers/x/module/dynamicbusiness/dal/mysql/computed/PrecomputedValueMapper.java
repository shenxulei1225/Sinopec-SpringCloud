package cn.cheers.x.module.dynamicbusiness.dal.mysql.computed;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.computed.PrecomputedValueDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 预计算值 Mapper
 * 
 * @author yudao
 */
@Mapper
public interface PrecomputedValueMapper extends BaseMapperX<PrecomputedValueDO> {

    /**
     * 根据实体和字段查询预计算值
     * 
     * @param modelId Model ID
     * @param entityId 实体 ID
     * @param fieldCode 字段编码
     * @return 预计算值
     */
    default PrecomputedValueDO selectByEntityAndField(Long modelId, Long entityId, String fieldCode) {
        return selectOne(new LambdaQueryWrapperX<PrecomputedValueDO>()
                .eq(PrecomputedValueDO::getModelId, modelId)
                .eq(PrecomputedValueDO::getEntityId, entityId)
                .eq(PrecomputedValueDO::getFieldCode, fieldCode));
    }

    /**
     * 根据实体 ID 查询所有预计算值
     * 
     * @param entityId 实体 ID
     * @return 预计算值列表
     */
    default List<PrecomputedValueDO> selectByEntityId(Long entityId) {
        return selectList(new LambdaQueryWrapperX<PrecomputedValueDO>()
                .eq(PrecomputedValueDO::getEntityId, entityId)
                .orderByAsc(PrecomputedValueDO::getFieldCode));
    }

    /**
     * 根据 Model ID 查询所有预计算值
     * 
     * @param modelId Model ID
     * @return 预计算值列表
     */
    default List<PrecomputedValueDO> selectByModelId(Long modelId) {
        return selectList(new LambdaQueryWrapperX<PrecomputedValueDO>()
                .eq(PrecomputedValueDO::getModelId, modelId)
                .orderByAsc(PrecomputedValueDO::getEntityId)
                .orderByAsc(PrecomputedValueDO::getFieldCode));
    }

    /**
     * 根据计算状态查询
     * 
     * @param status 计算状态
     * @param limit 限制数量
     * @return 预计算值列表
     */
    default List<PrecomputedValueDO> selectByStatus(String status, int limit) {
        return selectList(new LambdaQueryWrapperX<PrecomputedValueDO>()
                .eq(PrecomputedValueDO::getComputeStatus, status)
                .orderByAsc(PrecomputedValueDO::getCreateTime)
                .last("LIMIT " + limit));
    }

    /**
     * 查询待重算的预计算值（下次计算时间已到）
     * 
     * @param now 当前时间
     * @param limit 限制数量
     * @return 预计算值列表
     */
    default List<PrecomputedValueDO> selectPendingRecompute(LocalDateTime now, int limit) {
        return selectList(new LambdaQueryWrapperX<PrecomputedValueDO>()
                .eq(PrecomputedValueDO::getComputeStatus, PrecomputedValueDO.STATUS_PENDING)
                .le(PrecomputedValueDO::getNextComputeTime, now)
                .orderByAsc(PrecomputedValueDO::getNextComputeTime)
                .last("LIMIT " + limit));
    }

    /**
     * 查询失败且可重试的预计算值
     * 
     * @param maxRetryCount 最大重试次数
     * @param limit 限制数量
     * @return 预计算值列表
     */
    default List<PrecomputedValueDO> selectFailedRetryable(int maxRetryCount, int limit) {
        return selectList(new LambdaQueryWrapperX<PrecomputedValueDO>()
                .eq(PrecomputedValueDO::getComputeStatus, PrecomputedValueDO.STATUS_FAILED)
                .lt(PrecomputedValueDO::getRetryCount, maxRetryCount)
                .orderByAsc(PrecomputedValueDO::getUpdateTime)
                .last("LIMIT " + limit));
    }

    /**
     * 删除实体的所有预计算值
     * 
     * @param entityId 实体 ID
     * @return 删除数量
     */
    default int deleteByEntityId(Long entityId) {
        return delete(new LambdaQueryWrapperX<PrecomputedValueDO>()
                .eq(PrecomputedValueDO::getEntityId, entityId));
    }

    /**
     * 删除 Model 的所有预计算值
     * 
     * @param modelId Model ID
     * @return 删除数量
     */
    default int deleteByModelId(Long modelId) {
        return delete(new LambdaQueryWrapperX<PrecomputedValueDO>()
                .eq(PrecomputedValueDO::getModelId, modelId));
    }

    /**
     * 更新计算状态为计算中（使用乐观锁）
     * 
     * @param id 记录 ID
     * @param expectedVersion 期望的版本号
     * @return 更新行数
     */
    @Update("UPDATE dynamic_precomputed_value SET compute_status = 'COMPUTING', " +
            "version = version + 1, update_time = NOW() " +
            "WHERE id = #{id} AND version = #{expectedVersion} AND deleted = 0")
    int updateStatusToComputing(@Param("id") Long id, @Param("expectedVersion") Integer expectedVersion);

    /**
     * 批量更新状态为待计算
     * 
     * @param entityId 实体 ID
     * @return 更新行数
     */
    @Update("UPDATE dynamic_precomputed_value SET compute_status = 'PENDING', " +
            "next_compute_time = NOW(), update_time = NOW() " +
            "WHERE entity_id = #{entityId} AND deleted = 0")
    int updateStatusToPendingByEntityId(@Param("entityId") Long entityId);

    /**
     * 统计各状态的数量
     * 
     * @param status 计算状态
     * @return 数量
     */
    default long countByStatus(String status) {
        return selectCount(new LambdaQueryWrapperX<PrecomputedValueDO>()
                .eq(PrecomputedValueDO::getComputeStatus, status));
    }

    /**
     * 统计待计算的数量
     * 
     * @return 数量
     */
    default long countPending() {
        return countByStatus(PrecomputedValueDO.STATUS_PENDING);
    }

    /**
     * 统计失败的数量
     * 
     * @return 数量
     */
    default long countFailed() {
        return countByStatus(PrecomputedValueDO.STATUS_FAILED);
    }
}
