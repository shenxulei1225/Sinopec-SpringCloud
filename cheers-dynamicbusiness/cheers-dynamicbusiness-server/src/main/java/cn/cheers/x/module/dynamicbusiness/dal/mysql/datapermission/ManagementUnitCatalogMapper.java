package cn.cheers.x.module.dynamicbusiness.dal.mysql.datapermission;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datapermission.ManagementUnitCatalogDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/** 可管目录。只读写本表。 */
@Mapper
public interface ManagementUnitCatalogMapper extends BaseMapperX<ManagementUnitCatalogDO> {

    default List<ManagementUnitCatalogDO> selectAllOrdered() {
        return selectList(new LambdaQueryWrapperX<ManagementUnitCatalogDO>()
                .orderByAsc(ManagementUnitCatalogDO::getManagementUnitModelId)
                .orderByAsc(ManagementUnitCatalogDO::getId));
    }

    default void deleteByRegistryCode(String registryCode) {
        delete(new LambdaQueryWrapperX<ManagementUnitCatalogDO>()
                .eq(ManagementUnitCatalogDO::getRegistryCode, registryCode));
    }

    default void deleteAllRows() {
        delete(new LambdaQueryWrapperX<ManagementUnitCatalogDO>()
                .isNotNull(ManagementUnitCatalogDO::getId));
    }
}
