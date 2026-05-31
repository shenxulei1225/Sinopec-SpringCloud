package cn.cheers.x.module.dynamicbusiness.dal.mysql.relation;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.relation.ModelRelationDeclarationDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Model 关联声明 Mapper
 * 
 * @author yudao
 */
@Mapper
public interface ModelRelationDeclarationMapper extends BaseMapperX<ModelRelationDeclarationDO> {

    /**
     * 根据 Model ID 查询所有关联声明
     * 
     * @param modelId Model ID
     * @return 关联声明列表
     */
    default List<ModelRelationDeclarationDO> selectByModelId(Long modelId) {
        return selectList(new LambdaQueryWrapperX<ModelRelationDeclarationDO>()
                .eq(ModelRelationDeclarationDO::getModelId, modelId)
                .orderByAsc(ModelRelationDeclarationDO::getTargetBusinessType));
    }

    /**
     * 根据 Model ID 和目标业务类型查询
     * 
     * @param modelId Model ID
     * @param targetBusinessType 目标业务类型编码
     * @return 关联声明
     */
    default ModelRelationDeclarationDO selectByModelIdAndTarget(Long modelId, String targetBusinessType) {
        return selectOne(new LambdaQueryWrapperX<ModelRelationDeclarationDO>()
                .eq(ModelRelationDeclarationDO::getModelId, modelId)
                .eq(ModelRelationDeclarationDO::getTargetBusinessType, targetBusinessType));
    }

    /**
     * 检查 Model 是否已声明某个业务类型的关联
     * 
     * @param modelId Model ID
     * @param targetBusinessType 目标业务类型编码
     * @return 是否已声明
     */
    default boolean existsByModelIdAndTarget(Long modelId, String targetBusinessType) {
        return selectCount(new LambdaQueryWrapperX<ModelRelationDeclarationDO>()
                .eq(ModelRelationDeclarationDO::getModelId, modelId)
                .eq(ModelRelationDeclarationDO::getTargetBusinessType, targetBusinessType)) > 0;
    }

    /**
     * 删除 Model 的所有关联声明
     * 
     * @param modelId Model ID
     * @return 删除数量
     */
    default int deleteByModelId(Long modelId) {
        return delete(new LambdaQueryWrapperX<ModelRelationDeclarationDO>()
                .eq(ModelRelationDeclarationDO::getModelId, modelId));
    }

    /**
     * 删除 Model 的指定关联声明
     * 
     * @param modelId Model ID
     * @param targetBusinessType 目标业务类型编码
     * @return 删除数量
     */
    default int deleteByModelIdAndTarget(Long modelId, String targetBusinessType) {
        return delete(new LambdaQueryWrapperX<ModelRelationDeclarationDO>()
                .eq(ModelRelationDeclarationDO::getModelId, modelId)
                .eq(ModelRelationDeclarationDO::getTargetBusinessType, targetBusinessType));
    }

    /**
     * 查询引用某个业务类型的所有 Model ID
     * 
     * @param targetBusinessType 目标业务类型编码
     * @return Model ID 列表
     */
    default List<Long> selectModelIdsByTarget(String targetBusinessType) {
        return selectList(new LambdaQueryWrapperX<ModelRelationDeclarationDO>()
                .eq(ModelRelationDeclarationDO::getTargetBusinessType, targetBusinessType))
                .stream()
                .map(ModelRelationDeclarationDO::getModelId)
                .distinct()
                .toList();
    }
}
