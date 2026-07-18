package cn.cheers.x.scene.platform.service.actor;

import cn.cheers.x.scene.platform.controller.admin.actor.vo.ActorInstanceSpawnReqVO;
import cn.cheers.x.scene.platform.dal.dataobject.actor.ActorInstanceDO;
import cn.cheers.x.scene.platform.model.Transform;

import java.util.List;

public interface ActorInstanceService {

    List<ActorInstanceDO> getActorInstanceList(Long sceneId);

    List<ActorInstanceDO> getActorInstanceSimpleList(Long sceneId, String keyword);

    List<ActorInstanceDO> getActorInstances(List<Long> ids);

    ActorInstanceDO getActorInstance(Long id);

    Long createActorInstance(ActorInstanceDO actorInstanceDO);

    Long spawnActorInstance(ActorInstanceSpawnReqVO reqVO);

    void updateActorInstance(Long id, ActorInstanceDO actorInstanceDO);

    void updateActorInstanceTransform(Long id, Transform transform);

    void updateActorInstanceDefaults(Long id, ActorInstanceDO actorInstanceDO);

    void updateActorInstanceSchema(Long id, ActorInstanceDO actorInstanceDO);

    void deleteActorInstance(Long id);

    List<ActorInstanceDO> getChildrenInstances(String parentInstanceCode);
}
