package cn.cheers.x.module.dynamicbusiness.dal.mysql.datamgmt;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datamgmt.DmCatalogOrchestrationDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DmCatalogOrchestrationMapper extends BaseMapperX<DmCatalogOrchestrationDO> {

    default DmCatalogOrchestrationDO selectByEntityTypeCode(String entityTypeCode) {
        return selectOne(new LambdaQueryWrapperX<DmCatalogOrchestrationDO>()
                .eq(DmCatalogOrchestrationDO::getEntityTypeCode, entityTypeCode)
                .eq(DmCatalogOrchestrationDO::getDeleted, false));
    }
}
