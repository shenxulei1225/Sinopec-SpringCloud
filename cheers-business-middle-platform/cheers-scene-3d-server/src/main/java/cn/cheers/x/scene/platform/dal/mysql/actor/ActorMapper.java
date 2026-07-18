package cn.cheers.x.scene.platform.dal.mysql.actor;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.scene.platform.dal.dataobject.actor.ActorDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ActorMapper extends BaseMapperX<ActorDO> {
}
