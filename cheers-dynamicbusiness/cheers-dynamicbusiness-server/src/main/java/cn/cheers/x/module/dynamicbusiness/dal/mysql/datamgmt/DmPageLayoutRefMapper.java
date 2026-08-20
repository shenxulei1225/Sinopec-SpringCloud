package cn.cheers.x.module.dynamicbusiness.dal.mysql.datamgmt;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datamgmt.DmPageLayoutRefDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DmPageLayoutRefMapper extends BaseMapperX<DmPageLayoutRefDO> {

    default DmPageLayoutRefDO selectByPageKey(String pageKey) {
        // 依赖租户插件 + 逻辑删除；勿再叠 deleted 条件以免与拦截器打架导致查不到已有行
        return selectOne(new LambdaQueryWrapperX<DmPageLayoutRefDO>()
                .eq(DmPageLayoutRefDO::getPageKey, pageKey)
                .orderByAsc(DmPageLayoutRefDO::getId)
                .last("LIMIT 1"));
    }
}
