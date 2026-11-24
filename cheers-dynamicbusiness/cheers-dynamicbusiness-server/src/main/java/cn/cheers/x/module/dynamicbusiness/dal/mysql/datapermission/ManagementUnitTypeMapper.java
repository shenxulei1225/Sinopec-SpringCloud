package cn.cheers.x.module.dynamicbusiness.dal.mysql.datapermission;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datapermission.ManagementUnitTypeDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Set;

@Mapper
public interface ManagementUnitTypeMapper extends BaseMapperX<ManagementUnitTypeDO> {

    default List<ManagementUnitTypeDO> selectAllOrdered() {
        return selectList(new LambdaQueryWrapperX<ManagementUnitTypeDO>()
                .orderByAsc(ManagementUnitTypeDO::getModelId));
    }

    default Set<Long> selectRegisteredModelIds() {
        return selectAllOrdered().stream().map(ManagementUnitTypeDO::getModelId)
                .collect(java.util.stream.Collectors.toCollection(java.util.LinkedHashSet::new));
    }

    default void deleteAllRows() {
        delete(new LambdaQueryWrapperX<ManagementUnitTypeDO>()
                .isNotNull(ManagementUnitTypeDO::getId));
    }

    /** 删除型号时撤掉这条管理单元类型登记。型号没了还留着登记，组织树整页打不开。 */
    default void deleteByModelId(Long modelId) {
        if (modelId == null) {
            return;
        }
        delete(new LambdaQueryWrapperX<ManagementUnitTypeDO>()
                .eq(ManagementUnitTypeDO::getModelId, modelId));
    }
}