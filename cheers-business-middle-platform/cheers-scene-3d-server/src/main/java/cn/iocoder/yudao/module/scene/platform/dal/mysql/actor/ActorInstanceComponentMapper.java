package cn.iocoder.yudao.module.scene.platform.dal.mysql.actor;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.actor.ActorInstanceComponentDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ActorInstanceComponentMapper extends BaseMapperX<ActorInstanceComponentDO> {

    default List<ActorInstanceComponentDO> selectListByActorInstanceId(Long actorInstanceId) {
        return selectList(ActorInstanceComponentDO::getActorInstanceId, actorInstanceId);
    }
}
