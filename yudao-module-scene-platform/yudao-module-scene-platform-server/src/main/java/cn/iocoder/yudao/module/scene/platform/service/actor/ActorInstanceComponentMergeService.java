package cn.iocoder.yudao.module.scene.platform.service.actor;

import cn.iocoder.yudao.module.scene.platform.dal.dataobject.actor.ActorInstanceComponentDO;

import java.util.List;

public interface ActorInstanceComponentMergeService {

    List<ActorInstanceComponentDO> getMergedActorInstanceComponents(Long actorInstanceId);
}
