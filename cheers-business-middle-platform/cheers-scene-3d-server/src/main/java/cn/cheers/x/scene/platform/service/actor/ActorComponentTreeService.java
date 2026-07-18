package cn.cheers.x.scene.platform.service.actor;

import cn.cheers.x.scene.platform.dal.dataobject.actor.ActorDO;
import cn.cheers.x.scene.platform.dal.dataobject.actor.ActorInstanceComponentDO;
import cn.cheers.x.scene.platform.model.ComponentTree;
import cn.cheers.x.scene.platform.model.ComponentTreeNode;

import java.util.List;

public interface ActorComponentTreeService {

    List<ActorInstanceComponentDO> buildActorInstanceComponents(ActorDO actorDO, Long actorInstanceId, String instanceCode);

    ComponentTree getCopyOfTree(ComponentTree source);

    ComponentTreeNode findByCode(ComponentTree tree, String componentCode);

    List<ComponentTreeNode> traverse(ComponentTree tree);

    ComponentTree deepCopy(ComponentTree source);
}
