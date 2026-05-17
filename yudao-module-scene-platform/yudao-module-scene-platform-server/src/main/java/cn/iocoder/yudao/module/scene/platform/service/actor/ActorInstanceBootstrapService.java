package cn.iocoder.yudao.module.scene.platform.service.actor;

import cn.iocoder.yudao.module.scene.platform.dal.dataobject.actor.ActorInstanceComponentDO;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.actor.ActorInstanceDO;

import java.util.List;

public interface ActorInstanceBootstrapService {

    List<ActorInstanceComponentDO> buildDefaultComponents(ActorInstanceDO actorInstanceDO);
}
