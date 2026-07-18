package cn.iocoder.yudao.module.scene.platform.service.actor;

import cn.iocoder.yudao.module.scene.platform.dal.dataobject.actor.ActorDO;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.actor.ActorInstanceComponentDO;
import cn.iocoder.yudao.module.scene.platform.model.ComponentTree;
import cn.iocoder.yudao.module.scene.platform.model.ComponentTreeNode;

import java.util.List;

public interface ActorComponentTreeService {

    List<ActorInstanceComponentDO> buildActorInstanceComponents(ActorDO actorDO, Long actorInstanceId, String instanceCode);

    ComponentTree getCopyOfTree(ComponentTree source);

    ComponentTreeNode findByCode(ComponentTree tree, String componentCode);

    List<ComponentTreeNode> traverse(ComponentTree tree);

    ComponentTree deepCopy(ComponentTree source);
}
