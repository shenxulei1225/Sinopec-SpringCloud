package cn.iocoder.yudao.module.scene.platform.service.actor.impl;

import cn.iocoder.yudao.module.scene.platform.dal.dataobject.actor.ActorDO;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.actor.ActorInstanceComponentDO;
import cn.iocoder.yudao.module.scene.platform.model.ComponentTree;
import cn.iocoder.yudao.module.scene.platform.model.ComponentTreeNode;
import cn.iocoder.yudao.module.scene.platform.model.Rotator;
import cn.iocoder.yudao.module.scene.platform.model.Transform;
import cn.iocoder.yudao.module.scene.platform.model.Vector3;
import cn.iocoder.yudao.module.scene.platform.service.actor.ActorComponentTreeService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ActorComponentTreeServiceImpl implements ActorComponentTreeService {

    @Override
    public List<ActorInstanceComponentDO> buildActorInstanceComponents(ActorDO actorDO, Long actorInstanceId, String instanceCode) {
        ComponentTree tree = actorDO.getComponentTree();
        if (tree == null || tree.getRoot() == null) {
            return Collections.emptyList();
        }
        List<ComponentTreeNode> nodes = traverse(tree);
        List<ActorInstanceComponentDO> result = new ArrayList<>(nodes.size());
        for (ComponentTreeNode node : nodes) {
            ActorInstanceComponentDO comp = new ActorInstanceComponentDO();
            comp.setActorInstanceId(actorInstanceId);
            comp.setComponentCode(node.getComponentCode());
            comp.setComponentTypeName(node.getComponentTypeName());
            comp.setEnabledFlag(node.getEnabledFlag());
            comp.setSortNo(node.getSortNo());
            comp.setParentComponentCode(node.getParentComponentCode());
            comp.setInstanceCode(instanceCode);
            if (node.getTransform() != null) {
                comp.setPositionJson(toJson(node.getTransform().getLocation()));
                comp.setRotationJson(toJson(node.getTransform().getRotation()));
                comp.setScaleJson(toJson(node.getTransform().getScale()));
            }
            if (node.getPropertiesJson() != null) {
                comp.setPropertiesJson(toJson(node.getPropertiesJson()));
            }
            result.add(comp);
        }
        return result;
    }

    private String toJson(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Vector3 v) {
            return String.format("{\"x\":%.6f,\"y\":%.6f,\"z\":%.6f}", v.getX(), v.getY(), v.getZ());
        }
        if (obj instanceof Rotator r) {
            return String.format("{\"pitch\":%.6f,\"yaw\":%.6f,\"roll\":%.6f}", r.getPitch(), r.getYaw(), r.getRoll());
        }
        return obj.toString();
    }

    @Override
    public ComponentTree getCopyOfTree(ComponentTree source) {
        if (source == null || source.getRoot() == null) {
            return null;
        }
        ComponentTree copy = new ComponentTree();
        copy.setRoot(deepCopyNode(source.getRoot()));
        return copy;
    }

    @Override
    public ComponentTreeNode findByCode(ComponentTree tree, String componentCode) {
        if (tree == null || tree.getRoot() == null) {
            return null;
        }
        List<ComponentTreeNode> nodes = traverse(tree);
        for (ComponentTreeNode node : nodes) {
            if (componentCode.equals(node.getComponentCode())) {
                return node;
            }
        }
        return null;
    }

    @Override
    public List<ComponentTreeNode> traverse(ComponentTree tree) {
        if (tree == null || tree.getRoot() == null) {
            return Collections.emptyList();
        }
        List<ComponentTreeNode> result = new ArrayList<>();
        dfs(tree.getRoot(), result);
        return result;
    }

    private void dfs(ComponentTreeNode node, List<ComponentTreeNode> result) {
        result.add(node);
        if (node.getChildren() != null) {
            for (ComponentTreeNode child : node.getChildren()) {
                dfs(child, result);
            }
        }
    }

    @Override
    public ComponentTree deepCopy(ComponentTree source) {
        if (source == null || source.getRoot() == null) {
            return null;
        }
        ComponentTree copy = new ComponentTree();
        copy.setRoot(deepCopyNode(source.getRoot()));
        return copy;
    }

    private ComponentTreeNode deepCopyNode(ComponentTreeNode source) {
        ComponentTreeNode copy = new ComponentTreeNode();
        copy.setComponentCode(source.getComponentCode());
        copy.setComponentTypeName(source.getComponentTypeName());
        copy.setEnabledFlag(source.getEnabledFlag());
        copy.setSortNo(source.getSortNo());
        copy.setParentComponentCode(source.getParentComponentCode());
        copy.setPropertiesJson(source.getPropertiesJson());
        if (source.getTransform() != null) {
            Transform srcT = source.getTransform();
            Transform copyT = new Transform();
            if (srcT.getLocation() != null) {
                copyT.setLocation(new Vector3(srcT.getLocation().getX(), srcT.getLocation().getY(), srcT.getLocation().getZ()));
            }
            if (srcT.getRotation() != null) {
                Rotator rotation = new Rotator();
                rotation.setPitch(srcT.getRotation().getPitch());
                rotation.setYaw(srcT.getRotation().getYaw());
                rotation.setRoll(srcT.getRotation().getRoll());
                copyT.setRotation(rotation);
            }
            if (srcT.getScale() != null) {
                copyT.setScale(new Vector3(srcT.getScale().getX(), srcT.getScale().getY(), srcT.getScale().getZ()));
            }
            copy.setTransform(copyT);
        }
        if (source.getChildren() != null) {
            List<ComponentTreeNode> children = new ArrayList<>(source.getChildren().size());
            for (ComponentTreeNode child : source.getChildren()) {
                children.add(deepCopyNode(child));
            }
            copy.setChildren(children);
        }
        return copy;
    }
}
