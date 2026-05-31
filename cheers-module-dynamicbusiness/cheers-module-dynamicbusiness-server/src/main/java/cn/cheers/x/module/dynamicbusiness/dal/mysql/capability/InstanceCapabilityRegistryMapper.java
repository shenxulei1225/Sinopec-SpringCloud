package cn.cheers.x.module.dynamicbusiness.dal.mysql.capability;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.capability.InstanceCapabilityRegistryDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface InstanceCapabilityRegistryMapper extends BaseMapperX<InstanceCapabilityRegistryDO> {

    default InstanceCapabilityRegistryDO selectByInstanceKey(String instanceKey) {
        return selectOne(new LambdaQueryWrapperX<InstanceCapabilityRegistryDO>()
                .eq(InstanceCapabilityRegistryDO::getInstanceKey, instanceKey)
                .eq(InstanceCapabilityRegistryDO::getStatus, 1));
    }

    default List<InstanceCapabilityRegistryDO> selectSummaryList(String businessTypeCode, String domain) {
        return selectList(new LambdaQueryWrapperX<InstanceCapabilityRegistryDO>()
                .eqIfPresent(InstanceCapabilityRegistryDO::getBusinessTypeCode, businessTypeCode)
                .eqIfPresent(InstanceCapabilityRegistryDO::getDomain, domain)
                .eq(InstanceCapabilityRegistryDO::getStatus, 1)
                .orderByAsc(InstanceCapabilityRegistryDO::getDomain)
                .orderByAsc(InstanceCapabilityRegistryDO::getLabel));
    }

    default List<InstanceCapabilityRegistryDO> selectByModelId(Long modelId) {
        return selectList(new LambdaQueryWrapperX<InstanceCapabilityRegistryDO>()
                .eq(InstanceCapabilityRegistryDO::getModelId, modelId)
                .eq(InstanceCapabilityRegistryDO::getStatus, 1));
    }
}
