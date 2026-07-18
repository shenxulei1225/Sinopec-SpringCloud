package cn.cheers.x.scene.platform.dal.mysql.actor;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.scene.platform.dal.dataobject.actor.ActorInstanceComponentDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ActorInstanceComponentMapper extends BaseMapperX<ActorInstanceComponentDO> {

    default List<ActorInstanceComponentDO> selectListByActorInstanceId(Long actorInstanceId) {
        return selectList(ActorInstanceComponentDO::getActorInstanceId, actorInstanceId);
    }
}
