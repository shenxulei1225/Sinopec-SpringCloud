package cn.cheers.x.scene.platform.service.actor;

import cn.cheers.x.scene.platform.dal.dataobject.actor.ActorInstanceComponentDO;

import java.util.List;

public interface ActorInstanceComponentMergeService {

    List<ActorInstanceComponentDO> getMergedActorInstanceComponents(Long actorInstanceId);
}
