package cn.cheers.x.module.platform.capability.dal.mysql;

import cn.cheers.x.module.platform.capability.dal.dataobject.ProcessCapabilityBindingDO;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProcessCapabilityBindingMapper extends BaseMapperX<ProcessCapabilityBindingDO> {

    default ProcessCapabilityBindingDO selectByEntityTypeCode(String entityTypeCode) {
        return selectOne(new LambdaQueryWrapperX<ProcessCapabilityBindingDO>()
                .eq(ProcessCapabilityBindingDO::getEntityTypeCode, entityTypeCode));
    }
}
