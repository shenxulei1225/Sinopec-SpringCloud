package cn.cheers.x.module.dynamicbusiness.dal.mysql.orchestration;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.orchestration.BusinessOrchestrationDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 业务编排 Mapper。
 */
@Mapper
public interface BusinessOrchestrationMapper extends BaseMapperX<BusinessOrchestrationDO> {

    default BusinessOrchestrationDO selectByBusinessCode(String businessCode) {
        return selectOne(new LambdaQueryWrapperX<BusinessOrchestrationDO>()
                .eq(BusinessOrchestrationDO::getBusinessCode, businessCode));
    }
}
