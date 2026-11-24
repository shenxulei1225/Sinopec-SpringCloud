package cn.cheers.x.module.dynamicbusiness.dal.mysql.datapermission;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datapermission.ManagementUnitRegistryDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ManagementUnitRegistryMapper extends BaseMapperX<ManagementUnitRegistryDO> {

    default List<ManagementUnitRegistryDO> selectAllOrdered() {
        return selectList(new LambdaQueryWrapperX<ManagementUnitRegistryDO>()
                .orderByAsc(ManagementUnitRegistryDO::getSortOrder)
                .orderByAsc(ManagementUnitRegistryDO::getId));
    }

    default void deleteAllRows() {
        delete(new LambdaQueryWrapperX<ManagementUnitRegistryDO>()
                .isNotNull(ManagementUnitRegistryDO::getId));
    }
}
