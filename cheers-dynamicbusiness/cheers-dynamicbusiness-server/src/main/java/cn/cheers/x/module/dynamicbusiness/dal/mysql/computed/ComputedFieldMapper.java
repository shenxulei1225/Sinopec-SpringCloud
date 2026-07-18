package cn.cheers.x.module.dynamicbusiness.dal.mysql.computed;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.computed.ComputedFieldDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 计算字段 Mapper
 * 
 * @author yudao
 */
@Mapper
public interface ComputedFieldMapper extends BaseMapperX<ComputedFieldDO> {

    /**
     * 根据 Model ID 查询所有计算字段
     * 
     * @param modelId Model ID
     * @return 计算字段列表
     */
    default List<ComputedFieldDO> selectByModelId(Long modelId) {
        return selectList(new LambdaQueryWrapperX<ComputedFieldDO>()
                .eq(ComputedFieldDO::getModelId, modelId)
                .orderByAsc(ComputedFieldDO::getSortOrder)
                .orderByAsc(ComputedFieldDO::getFieldName));
    }

    /**
     * 根据 Model ID 和字段编码查询
     * 
     * @param modelId Model ID
     * @param fieldCode 字段编码
     * @return 计算字段
     */
    default ComputedFieldDO selectByModelIdAndFieldCode(Long modelId, String fieldCode) {
        return selectOne(new LambdaQueryWrapperX<ComputedFieldDO>()
                .eq(ComputedFieldDO::getModelId, modelId)
                .eq(ComputedFieldDO::getFieldCode, fieldCode));
    }

    /**
     * 根据计算类型查询
     * 
     * @param modelId Model ID
     * @param computeType 计算类型
     * @return 计算字段列表
     */
    default List<ComputedFieldDO> selectByComputeType(Long modelId, String computeType) {
        return selectList(new LambdaQueryWrapperX<ComputedFieldDO>()
                .eq(ComputedFieldDO::getModelId, modelId)
                .eq(ComputedFieldDO::getComputeType, computeType)
                .orderByAsc(ComputedFieldDO::getSortOrder));
    }

    /**
     * 根据计算策略查询
     * 
     * @param computeStrategy 计算策略
     * @return 计算字段列表
     */
    default List<ComputedFieldDO> selectByComputeStrategy(String computeStrategy) {
        return selectList(new LambdaQueryWrapperX<ComputedFieldDO>()
                .eq(ComputedFieldDO::getComputeStrategy, computeStrategy)
                .orderByAsc(ComputedFieldDO::getModelId)
                .orderByAsc(ComputedFieldDO::getSortOrder));
    }

    /**
     * 检查字段编码是否存在
     * 
     * @param modelId Model ID
     * @param fieldCode 字段编码
     * @param excludeId 排除的ID（用于更新时排除自身）
     * @return 是否存在
     */
    default boolean existsByFieldCode(Long modelId, String fieldCode, Long excludeId) {
        return selectCount(new LambdaQueryWrapperX<ComputedFieldDO>()
                .eq(ComputedFieldDO::getModelId, modelId)
                .eq(ComputedFieldDO::getFieldCode, fieldCode)
                .neIfPresent(ComputedFieldDO::getId, excludeId)) > 0;
    }

    /**
     * 删除 Model 的所有计算字段
     * 
     * @param modelId Model ID
     * @return 删除数量
     */
    default int deleteByModelId(Long modelId) {
        return delete(new LambdaQueryWrapperX<ComputedFieldDO>()
                .eq(ComputedFieldDO::getModelId, modelId));
    }

    /**
     * 查询引用某个目标的聚合统计字段
     * 
     * @param targetEntityType 目标业务类型
     * @param targetModelCode 目标 Model 编码
     * @return 计算字段列表
     */
    default List<ComputedFieldDO> selectByAggregateTarget(String targetEntityType, String targetModelCode) {
        return selectList(new LambdaQueryWrapperX<ComputedFieldDO>()
                .eq(ComputedFieldDO::getComputeType, ComputedFieldDO.COMPUTE_TYPE_AGGREGATE)
                .eq(ComputedFieldDO::getTargetEntityType, targetEntityType)
                .eqIfPresent(ComputedFieldDO::getTargetModelCode, targetModelCode));
    }

    /**
     * 查询需要预计算的字段
     * 
     * @return 需要预计算的字段列表
     */
    default List<ComputedFieldDO> selectPrecomputedFields() {
        return selectList(new LambdaQueryWrapperX<ComputedFieldDO>()
                .eq(ComputedFieldDO::getComputeStrategy, ComputedFieldDO.STRATEGY_PRECOMPUTED)
                .orderByAsc(ComputedFieldDO::getModelId)
                .orderByAsc(ComputedFieldDO::getSortOrder));
    }
}
