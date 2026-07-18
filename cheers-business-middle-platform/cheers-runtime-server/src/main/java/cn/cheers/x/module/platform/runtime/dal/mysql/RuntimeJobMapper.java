package cn.cheers.x.module.platform.runtime.dal.mysql;

import cn.cheers.x.module.platform.runtime.dal.dataobject.RuntimeJobDO;
import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RuntimeJobMapper extends BaseMapperX<RuntimeJobDO> {
}
