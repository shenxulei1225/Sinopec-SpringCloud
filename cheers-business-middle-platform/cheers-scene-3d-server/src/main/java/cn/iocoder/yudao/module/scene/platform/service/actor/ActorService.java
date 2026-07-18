package cn.iocoder.yudao.module.scene.platform.service.actor;

import cn.iocoder.yudao.module.scene.platform.dal.dataobject.actor.ActorDO;
import cn.iocoder.yudao.module.scene.platform.model.ComponentTree;
import cn.iocoder.yudao.module.scene.platform.model.ComponentTreeNode;

import java.util.List;

public interface ActorService {

    List<ActorDO> getActorList();

    ActorDO getActor(Long id);

    ActorDO getActorByCode(String actorCode);

    Long createActor(ActorDO actorDO);

    void updateActor(Long id, ActorDO actorDO);

    void updateActorDefaults(Long id, ActorDO actorDO);

    void updateActorSchema(Long id, ActorDO actorDO);

    // ========== 组件树管理 ==========

    /**
     * 获取 Actor 的组件树（深拷贝，不可直接修改）
     */
    ComponentTree getComponentTree(String actorCode);

    /**
     * 在指定父组件下添加子组件
     * @param actorCode Actor 编码
     * @param parentCode 父组件编码（传入 "root" 则添加到根组件下）
     * @param newNode 要添加的组件节点
     */
    void addComponent(String actorCode, String parentCode, ComponentTreeNode newNode);

    /**
     * 删除指定组件节点及其所有子节点
     * @param actorCode Actor 编码
     * @param componentCode 要删除的组件编码
     */
    void removeComponent(String actorCode, String componentCode);

    /**
     * 更新组件节点属性
     * @param actorCode Actor 编码
     * @param componentCode 组件编码
     * @param updates 要更新的字段（只更新非 null 字段）
     */
    void updateComponent(String actorCode, String componentCode, ComponentTreeNode updates);

    /**
     * 根据组件编码查找节点
     */
    ComponentTreeNode findComponentByCode(ComponentTree tree, String componentCode);

    /**
     * 遍历获取所有组件节点
     */
    List<ComponentTreeNode> getAllNodes(ComponentTree tree);

    /**
     * 深拷贝组件树
     */
    ComponentTree copyComponentTree(ComponentTree source);
}
