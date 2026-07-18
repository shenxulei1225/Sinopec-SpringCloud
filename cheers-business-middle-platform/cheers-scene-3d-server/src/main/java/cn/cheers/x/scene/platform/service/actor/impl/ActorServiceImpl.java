package cn.cheers.x.scene.platform.service.actor.impl;

import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.cheers.x.framework.common.util.object.BeanUtils;
import cn.cheers.x.scene.platform.dal.dataobject.actor.ActorDO;
import cn.cheers.x.scene.platform.dal.mysql.actor.ActorMapper;
import cn.cheers.x.scene.platform.model.ComponentTree;
import cn.cheers.x.scene.platform.model.ComponentTreeNode;
import cn.cheers.x.scene.platform.service.actor.ActorComponentTreeService;
import cn.cheers.x.scene.platform.service.actor.ActorService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static cn.cheers.x.framework.common.exception.enums.GlobalErrorCodeConstants.NOT_FOUND;

@Service
public class ActorServiceImpl implements ActorService {

    @Resource
    private ActorMapper actorMapper;

    @Resource
    private ActorComponentTreeService componentTreeService;

    @Override
    public List<ActorDO> getActorList() {
        return actorMapper.selectList();
    }

    @Override
    public ActorDO getActor(Long id) {
        ActorDO item = actorMapper.selectById(id);
        if (item == null) {
            throw ServiceExceptionUtil.exception(NOT_FOUND, "Actor 不存在");
        }
        return item;
    }

    @Override
    public ActorDO getActorByCode(String actorCode) {
        ActorDO item = actorMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ActorDO>()
                        .eq(ActorDO::getActorCode, actorCode));
        if (item == null) {
            throw ServiceExceptionUtil.exception(NOT_FOUND, "Actor 不存在");
        }
        return item;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createActor(ActorDO actorDO) {
        // 初始化默认组件树（根节点）
        if (actorDO.getComponentTree() == null) {
            ComponentTree tree = new ComponentTree();
            ComponentTreeNode root = new ComponentTreeNode();
            root.setComponentCode("RootComponent");
            root.setEnabledFlag(Boolean.TRUE);
            root.setSortNo(1);
            root.setComponentTypeName("SceneComponent");
            tree.setRoot(root);
            actorDO.setComponentTree(tree);
        }
        actorMapper.insert(actorDO);
        return actorDO.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateActor(Long id, ActorDO actorDO) {
        ActorDO db = getActor(id);
        BeanUtils.copyProperties(actorDO, db);
        actorMapper.updateById(db);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateActorDefaults(Long id, ActorDO actorDO) {
        ActorDO db = getActor(id);
        db.setActorClass(actorDO.getActorClass());
        db.setActorCategory(actorDO.getActorCategory());
        db.setEngineProfile(actorDO.getEngineProfile());
        db.setAbstractFlag(actorDO.getAbstractFlag());
        db.setLifecycleStatus(actorDO.getLifecycleStatus());
        db.setMetadataJson(actorDO.getMetadataJson());
        actorMapper.updateById(db);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateActorSchema(Long id, ActorDO actorDO) {
        ActorDO db = getActor(id);
        db.setMetadataJson(actorDO.getMetadataJson());
        actorMapper.updateById(db);
    }

    // ========== 组件树管理 ==========

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ComponentTree getComponentTree(String actorCode) {
        ActorDO actorDO = getActorByCode(actorCode);
        return componentTreeService.getCopyOfTree(actorDO.getComponentTree());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addComponent(String actorCode, String parentCode, ComponentTreeNode newNode) {
        ActorDO db = getActorByCode(actorCode);
        ComponentTree tree = db.getComponentTree();
        if (tree == null) {
            tree = new ComponentTree();
            tree.setRoot(new ComponentTreeNode());
            tree.getRoot().setComponentCode("RootComponent");
            tree.getRoot().setComponentTypeName("SceneComponent");
            db.setComponentTree(tree);
        }

        ComponentTreeNode parentNode;
        if ("root".equalsIgnoreCase(parentCode) || parentCode == null) {
            parentNode = tree.getRoot();
        } else {
            parentNode = findComponentByCode(tree, parentCode);
            if (parentNode == null) {
                throw ServiceExceptionUtil.exception(NOT_FOUND, "父组件不存在: " + parentCode);
            }
        }

        parentNode.getChildren().add(newNode);
        actorMapper.updateById(db);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeComponent(String actorCode, String componentCode) {
        ActorDO db = getActorByCode(actorCode);
        ComponentTree tree = db.getComponentTree();
        if (tree == null || tree.getRoot() == null) {
            return;
        }

        // 不能删除根节点
        if ("root".equals(componentCode) && tree.getRoot().getComponentCode().equals(componentCode)) {
            throw ServiceExceptionUtil.exception(NOT_FOUND, "根组件不可删除");
        }

        removeNode(tree.getRoot(), componentCode);
        actorMapper.updateById(db);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateComponent(String actorCode, String componentCode, ComponentTreeNode updates) {
        ActorDO db = getActorByCode(actorCode);
        ComponentTree tree = db.getComponentTree();
        if (tree == null) {
            throw ServiceExceptionUtil.exception(NOT_FOUND, "Actor 没有组件树");
        }

        ComponentTreeNode target = findComponentByCode(tree, componentCode);
        if (target == null) {
            throw ServiceExceptionUtil.exception(NOT_FOUND, "组件不存在: " + componentCode);
        }

        // 只更新非 null 字段
        if (updates.getComponentTypeName() != null) {
            target.setComponentTypeName(updates.getComponentTypeName());
        }
        if (updates.getEnabledFlag() != null) {
            target.setEnabledFlag(updates.getEnabledFlag());
        }
        if (updates.getSortNo() != null) {
            target.setSortNo(updates.getSortNo());
        }
        if (updates.getTransform() != null) {
            target.setTransform(updates.getTransform());
        }
        if (updates.getMetadataJson() != null) {
            target.setMetadataJson(updates.getMetadataJson());
        }

        actorMapper.updateById(db);
    }

    // ========== 辅助方法 ==========

    @Override
    public ComponentTreeNode findComponentByCode(ComponentTree tree, String componentCode) {
        return componentTreeService.findByCode(tree, componentCode);
    }

    @Override
    public List<ComponentTreeNode> getAllNodes(ComponentTree tree) {
        return componentTreeService.traverse(tree);
    }

    @Override
    public ComponentTree copyComponentTree(ComponentTree source) {
        return componentTreeService.deepCopy(source);
    }

    /**
     * 从节点列表中移除指定 componentCode 的子节点
     */
    private void removeNode(ComponentTreeNode parent, String componentCode) {
        if (parent.getChildren() == null) {
            return;
        }
        for (Iterator<ComponentTreeNode> it = parent.getChildren().iterator(); it.hasNext(); ) {
            ComponentTreeNode child = it.next();
            if (componentCode.equals(child.getComponentCode())) {
                it.remove();
                return;
            }
            removeNode(child, componentCode);
        }
    }
}
