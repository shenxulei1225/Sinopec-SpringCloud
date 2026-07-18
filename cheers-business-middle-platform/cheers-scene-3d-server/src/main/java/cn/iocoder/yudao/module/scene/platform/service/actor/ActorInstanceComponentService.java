package cn.iocoder.yudao.module.scene.platform.service.actor;

import cn.iocoder.yudao.module.scene.platform.dal.dataobject.actor.ActorInstanceComponentDO;

import java.util.List;

public interface ActorInstanceComponentService {

    List<ActorInstanceComponentDO> getActorInstanceComponentList(Long actorInstanceId);

    ActorInstanceComponentDO getActorInstanceComponent(Long id);

    Long createActorInstanceComponent(ActorInstanceComponentDO actorInstanceComponentDO);

    void updateActorInstanceComponent(Long id, ActorInstanceComponentDO actorInstanceComponentDO);

    void updateActorInstanceComponentDefaults(Long id, ActorInstanceComponentDO actorInstanceComponentDO);

    void updateActorInstanceComponentSchema(Long id, ActorInstanceComponentDO actorInstanceComponentDO);

    /**
     * 按实例 ID 查询组件列表
     * 用于前端渲染时一次性获取实例的所有组件
     */
    List<ActorInstanceComponentDO> listByInstanceId(Long actorInstanceId);
}
