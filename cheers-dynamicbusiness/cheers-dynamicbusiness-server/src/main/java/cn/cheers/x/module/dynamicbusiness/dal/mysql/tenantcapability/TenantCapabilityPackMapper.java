package cn.cheers.x.module.dynamicbusiness.dal.mysql.tenantcapability;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.tenantcapability.TenantCapabilityPackDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TenantCapabilityPackMapper extends BaseMapperX<TenantCapabilityPackDO> {

    default List<TenantCapabilityPackDO> selectListByTenantId(Long tenantId) {
        return selectList(new LambdaQueryWrapperX<TenantCapabilityPackDO>()
                .eq(TenantCapabilityPackDO::getTenantId, tenantId)
                .eq(TenantCapabilityPackDO::getDeleted, false)
                .orderByAsc(TenantCapabilityPackDO::getId));
    }

    default void deleteByTenantId(Long tenantId) {
        delete(new LambdaQueryWrapperX<TenantCapabilityPackDO>()
                .eq(TenantCapabilityPackDO::getTenantId, tenantId));
    }
}
