package cn.cheers.x.module.dynamicbusiness.dal.mysql.tenantcapability;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.tenantcapability.ManagementUnitBusinessCatalogDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ManagementUnitBusinessCatalogMapper extends BaseMapperX<ManagementUnitBusinessCatalogDO> {

    default List<ManagementUnitBusinessCatalogDO> selectByAnchor(
            Long tenantId, String registryCode, Long managementUnitId) {
        return selectList(new LambdaQueryWrapperX<ManagementUnitBusinessCatalogDO>()
                .eq(ManagementUnitBusinessCatalogDO::getTenantId, tenantId)
                .eq(ManagementUnitBusinessCatalogDO::getManagementUnitRegistryCode, registryCode)
                .eq(ManagementUnitBusinessCatalogDO::getManagementUnitId, managementUnitId)
                .eq(ManagementUnitBusinessCatalogDO::getDeleted, false)
                .orderByAsc(ManagementUnitBusinessCatalogDO::getCatalogRegistryCode));
    }

    default void deleteByAnchor(Long tenantId, String registryCode, Long managementUnitId) {
        delete(new LambdaQueryWrapperX<ManagementUnitBusinessCatalogDO>()
                .eq(ManagementUnitBusinessCatalogDO::getTenantId, tenantId)
                .eq(ManagementUnitBusinessCatalogDO::getManagementUnitRegistryCode, registryCode)
                .eq(ManagementUnitBusinessCatalogDO::getManagementUnitId, managementUnitId));
    }
}