package cn.cheers.x.module.dynamicbusiness.dal.mysql.tenantcapability;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.tenantcapability.ManagementUnitCapabilityPackDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ManagementUnitCapabilityPackMapper extends BaseMapperX<ManagementUnitCapabilityPackDO> {

    default List<ManagementUnitCapabilityPackDO> selectByAnchor(
            Long tenantId, String registryCode, Long managementUnitId) {
        return selectList(new LambdaQueryWrapperX<ManagementUnitCapabilityPackDO>()
                .eq(ManagementUnitCapabilityPackDO::getTenantId, tenantId)
                .eq(ManagementUnitCapabilityPackDO::getManagementUnitRegistryCode, registryCode)
                .eq(ManagementUnitCapabilityPackDO::getManagementUnitId, managementUnitId)
                .eq(ManagementUnitCapabilityPackDO::getDeleted, false)
                .orderByAsc(ManagementUnitCapabilityPackDO::getPackCode));
    }

    default void deleteByAnchor(Long tenantId, String registryCode, Long managementUnitId) {
        delete(new LambdaQueryWrapperX<ManagementUnitCapabilityPackDO>()
                .eq(ManagementUnitCapabilityPackDO::getTenantId, tenantId)
                .eq(ManagementUnitCapabilityPackDO::getManagementUnitRegistryCode, registryCode)
                .eq(ManagementUnitCapabilityPackDO::getManagementUnitId, managementUnitId));
    }

    default long countEnabled(Long tenantId, String packCode, String registryCode, Long managementUnitId) {
        Long count = selectCount(new LambdaQueryWrapperX<ManagementUnitCapabilityPackDO>()
                .eq(ManagementUnitCapabilityPackDO::getTenantId, tenantId)
                .eq(ManagementUnitCapabilityPackDO::getPackCode, packCode)
                .eq(ManagementUnitCapabilityPackDO::getManagementUnitRegistryCode, registryCode)
                .eq(ManagementUnitCapabilityPackDO::getManagementUnitId, managementUnitId)
                .eq(ManagementUnitCapabilityPackDO::getDeleted, false));
        return count == null ? 0L : count;
    }
}