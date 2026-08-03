package cn.cheers.x.module.dynamicbusiness.dal.mysql.datamgmt;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datamgmt.DmFiveWOrchestrationDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DmFiveWOrchestrationMapper extends BaseMapperX<DmFiveWOrchestrationDO> {

    default DmFiveWOrchestrationDO selectByEntityTypeCode(String entityTypeCode) {
        return selectOne(new LambdaQueryWrapperX<DmFiveWOrchestrationDO>()
                .eq(DmFiveWOrchestrationDO::getEntityTypeCode, entityTypeCode)
                .eq(DmFiveWOrchestrationDO::getDeleted, false));
    }
}
