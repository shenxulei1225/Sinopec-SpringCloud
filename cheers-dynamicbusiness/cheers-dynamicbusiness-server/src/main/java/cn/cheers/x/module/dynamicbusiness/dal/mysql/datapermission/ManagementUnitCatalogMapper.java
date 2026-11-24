package cn.cheers.x.module.dynamicbusiness.dal.mysql.datapermission;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datapermission.ManagementUnitCatalogDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/** 可管目录（按管理点）。只读写本表。 */
@Mapper
public interface ManagementUnitCatalogMapper extends BaseMapperX<ManagementUnitCatalogDO> {

    default List<ManagementUnitCatalogDO> selectByAnchor(String unitRegistryCode, Long managementUnitId) {
        return selectList(new LambdaQueryWrapperX<ManagementUnitCatalogDO>()
                .eq(ManagementUnitCatalogDO::getManagementUnitRegistryCode, unitRegistryCode)
                .eq(ManagementUnitCatalogDO::getManagementUnitId, managementUnitId)
                .orderByAsc(ManagementUnitCatalogDO::getRegistryCode));
    }

    default void deleteByAnchor(String unitRegistryCode, Long managementUnitId) {
        delete(new LambdaQueryWrapperX<ManagementUnitCatalogDO>()
                .eq(ManagementUnitCatalogDO::getManagementUnitRegistryCode, unitRegistryCode)
                .eq(ManagementUnitCatalogDO::getManagementUnitId, managementUnitId));
    }

    default void deleteByRegistryCode(String registryCode) {
        delete(new LambdaQueryWrapperX<ManagementUnitCatalogDO>()
                .eq(ManagementUnitCatalogDO::getRegistryCode, registryCode));
    }
}