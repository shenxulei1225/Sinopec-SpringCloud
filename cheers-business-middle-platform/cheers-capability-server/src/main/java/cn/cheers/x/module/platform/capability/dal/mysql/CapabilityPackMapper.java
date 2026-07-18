package cn.cheers.x.module.platform.capability.dal.mysql;

import cn.cheers.x.module.platform.capability.dal.dataobject.CapabilityPackDO;
import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CapabilityPackMapper extends BaseMapperX<CapabilityPackDO> {

    default List<CapabilityPackDO> selectEnabledList(String domain) {
        return selectList(new LambdaQueryWrapperX<CapabilityPackDO>()
                .eq(CapabilityPackDO::getEnabled, true)
                .eqIfPresent(CapabilityPackDO::getDomain, domain)
                .orderByAsc(CapabilityPackDO::getDisplayName));
    }
}
