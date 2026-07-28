package cn.cheers.x.module.dynamicbusiness.dal.mysql.datamgmt;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datamgmt.DmModelTabCategoryDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DmModelTabCategoryMapper extends BaseMapperX<DmModelTabCategoryDO> {

    default DmModelTabCategoryDO selectByEntityTypeCode(String entityTypeCode) {
        return selectOne(new LambdaQueryWrapperX<DmModelTabCategoryDO>()
                .eq(DmModelTabCategoryDO::getEntityTypeCode, entityTypeCode)
                .eq(DmModelTabCategoryDO::getDeleted, false)
                .last("LIMIT 1"));
    }
}
