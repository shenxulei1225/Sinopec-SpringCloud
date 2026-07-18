package cn.cheers.x.scene.platform.service.actor;

import cn.cheers.x.scene.platform.dal.dataobject.actor.ActorInstanceComponentDO;
import cn.cheers.x.scene.platform.dal.dataobject.actor.ActorInstanceDO;

import java.util.List;

public interface ActorInstanceBootstrapService {

    List<ActorInstanceComponentDO> buildDefaultComponents(ActorInstanceDO actorInstanceDO);
}
