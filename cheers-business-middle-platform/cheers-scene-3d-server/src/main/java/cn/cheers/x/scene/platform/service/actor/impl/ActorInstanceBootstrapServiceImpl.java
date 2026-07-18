package cn.cheers.x.scene.platform.service.actor.impl;

import cn.cheers.x.scene.platform.dal.dataobject.actor.ActorInstanceComponentDO;
import cn.cheers.x.scene.platform.dal.dataobject.actor.ActorInstanceDO;
import cn.cheers.x.scene.platform.service.actor.ActorInstanceBootstrapService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ActorInstanceBootstrapServiceImpl implements ActorInstanceBootstrapService {

    @Override
    public List<ActorInstanceComponentDO> buildDefaultComponents(ActorInstanceDO actorInstanceDO) {
        List<ActorInstanceComponentDO> result = new ArrayList<>();
        ActorInstanceComponentDO root = new ActorInstanceComponentDO();
        root.setActorInstanceId(actorInstanceDO.getId());
        root.setActorCode(actorInstanceDO.getActorCode());
        root.setInstanceCode(actorInstanceDO.getInstanceCode());
        root.setComponentCode("RootComponent");
        root.setEnabledFlag(Boolean.TRUE);
        root.setSortNo(1);
        root.setOverrideJson("{}");
        root.setMetadataJson("{}");
        result.add(root);
        return result;
    }
}
