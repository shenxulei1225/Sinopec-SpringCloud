package cn.cheers.x.scene.platform.service.actor.impl;

import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.cheers.x.framework.common.util.object.BeanUtils;
import cn.cheers.x.scene.platform.controller.admin.actor.vo.ActorInstanceSpawnReqVO;
import cn.cheers.x.scene.platform.controller.admin.actor.vo.ComponentTreeNodeVO;
import cn.cheers.x.scene.platform.controller.admin.actor.vo.ComponentTreeVO;
import cn.cheers.x.scene.platform.dal.dataobject.actor.ActorDO;
import cn.cheers.x.scene.platform.dal.dataobject.actor.ActorInstanceComponentDO;
import cn.cheers.x.scene.platform.dal.dataobject.actor.ActorInstanceDO;
import cn.cheers.x.scene.platform.dal.dataobject.scene.SceneDO;
import cn.cheers.x.scene.platform.dal.mysql.actor.ActorInstanceComponentMapper;
import cn.cheers.x.scene.platform.dal.mysql.actor.ActorInstanceMapper;
import cn.cheers.x.scene.platform.dal.mysql.scene.SceneMapper;
import cn.cheers.x.scene.platform.model.ComponentTree;
import cn.cheers.x.scene.platform.model.ComponentTreeNode;
import cn.cheers.x.scene.platform.model.Transform;
import cn.cheers.x.scene.platform.service.actor.ActorComponentTreeService;
import cn.cheers.x.scene.platform.service.actor.ActorInstanceComponentService;
import cn.cheers.x.scene.platform.service.actor.ActorInstanceService;
import cn.cheers.x.scene.platform.service.actor.ActorService;
import cn.cheers.x.scene.platform.service.actor.support.ActorRenderConfigValidator;
import cn.cheers.x.scene.platform.websocket.ActorInstanceWebSocketService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static cn.cheers.x.framework.common.exception.enums.GlobalErrorCodeConstants.NOT_FOUND;

@Service
public class ActorInstanceServiceImpl implements ActorInstanceService {

    @Resource
    private ActorInstanceMapper actorInstanceMapper;
    @Resource
    private ActorInstanceComponentMapper actorInstanceComponentMapper;
    @Resource
    private ActorService actorService;
    @Resource
    private ActorComponentTreeService componentTreeService;
    @Resource
    private ActorInstanceComponentService actorInstanceComponentService;
    @Resource
    private ActorInstanceWebSocketService webSocketService;
    @Resource
    private SceneMapper sceneMapper;
    @Resource
    private ActorRenderConfigValidator actorRenderConfigValidator;

    @Override
    public List<ActorInstanceDO> getActorInstanceList(Long sceneId) {
        return actorInstanceMapper.selectListBySceneId(sceneId);
    }

    @Override
    public List<ActorInstanceDO> getActorInstanceSimpleList(Long sceneId, String keyword) {
        return actorInstanceMapper.selectSimpleListBySceneIdAndKeyword(sceneId, keyword);
    }

    @Override
    public List<ActorInstanceDO> getActorInstances(List<Long> ids) {
        return actorInstanceMapper.selectListByIds(ids);
    }

    @Override
    public ActorInstanceDO getActorInstance(Long id) {
        ActorInstanceDO item = actorInstanceMapper.selectById(id);
        if (item == null) {
            throw ServiceExceptionUtil.exception(NOT_FOUND, "Actor 实例不存在");
        }
        return item;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createActorInstance(ActorInstanceDO actorInstanceDO) {
        actorInstanceMapper.insert(actorInstanceDO);
        ActorDO actorDO = actorService.getActorByCode(actorInstanceDO.getActorCode());
        if (actorDO == null) {
            throw ServiceExceptionUtil.exception(NOT_FOUND, "Actor模板不存在");
        }
        List<ActorInstanceComponentDO> components = buildActorInstanceComponents(actorDO, actorInstanceDO.getId(), actorInstanceDO.getInstanceCode());
        validateRenderConfigs(components);
        components.forEach(actorInstanceComponentMapper::insert);
        return actorInstanceDO.getId();
    }

    @Override
    public void updateActorInstance(Long id, ActorInstanceDO actorInstanceDO) {
        ActorInstanceDO db = getActorInstance(id);
        BeanUtils.copyProperties(actorInstanceDO, db);
        actorInstanceMapper.updateById(db);
    }

    @Override
    public void updateActorInstanceTransform(Long id, Transform transform) {
        ActorInstanceDO db = getActorInstance(id);
        db.setTransform(transform);
        actorInstanceMapper.updateById(db);
        webSocketService.broadcastPositionUpdate(resolveSceneCode(db.getSceneId()), db.getId(), db.getInstanceCode(), transform);
    }

    @Override
    public void updateActorInstanceDefaults(Long id, ActorInstanceDO actorInstanceDO) {
        ActorInstanceDO db = getActorInstance(id);
        db.setTransform(actorInstanceDO.getTransform());
        db.setVisibleFlag(actorInstanceDO.getVisibleFlag());
        db.setInstanceStatus(actorInstanceDO.getInstanceStatus());
        db.setPath(actorInstanceDO.getPath());
        db.setLayerKeys(actorInstanceDO.getLayerKeys());
        db.setMetadataJson(actorInstanceDO.getMetadataJson());
        actorInstanceMapper.updateById(db);
        String sceneCode = resolveSceneCode(db.getSceneId());
        webSocketService.broadcastPositionUpdate(sceneCode, db.getId(), db.getInstanceCode(), db.getTransform());
        if (actorInstanceDO.getVisibleFlag() != null) {
            webSocketService.broadcastVisibilityChange(sceneCode, db.getId(), db.getInstanceCode(), actorInstanceDO.getVisibleFlag());
        }
    }

    @Override
    public void updateActorInstanceSchema(Long id, ActorInstanceDO actorInstanceDO) {
        ActorInstanceDO db = getActorInstance(id);
        db.setMetadataJson(actorInstanceDO.getMetadataJson());
        actorInstanceMapper.updateById(db);
    }

    @Override
    public void updateActorInstanceGps(Long id, java.math.BigDecimal gpsLng, java.math.BigDecimal gpsLat,
                                       java.math.BigDecimal gpsHeight, String gpsHeightSource) {
        ActorInstanceDO db = getActorInstance(id);
        if (gpsLng != null) {
            db.setGpsLng(gpsLng);
        }
        if (gpsLat != null) {
            db.setGpsLat(gpsLat);
        }
        db.setGpsHeight(gpsHeight);
        if (gpsHeightSource != null && !gpsHeightSource.isBlank()) {
            db.setGpsHeightSource(gpsHeightSource.trim());
        }
        actorInstanceMapper.updateById(db);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long spawnActorInstance(ActorInstanceSpawnReqVO reqVO) {
        ActorDO actorDO = actorService.getActorByCode(reqVO.getActorCode());
        if (actorDO == null) {
            throw ServiceExceptionUtil.exception(NOT_FOUND, "Actor 模板不存在: " + reqVO.getActorCode());
        }
        ActorInstanceDO instanceDO = new ActorInstanceDO();
        instanceDO.setSceneId(reqVO.getSceneId());
        instanceDO.setActorCode(reqVO.getActorCode());
        instanceDO.setInstanceCode(reqVO.getInstanceCode() != null ? reqVO.getInstanceCode() : UUID.randomUUID().toString().substring(0, 8));
        instanceDO.setInstanceName(reqVO.getInstanceName() != null ? reqVO.getInstanceName() : actorDO.getActorName() + "_" + instanceDO.getInstanceCode());
        instanceDO.setTransform(reqVO.getTransform());
        instanceDO.setVisibleFlag(reqVO.getVisibleFlag() != null ? reqVO.getVisibleFlag() : Boolean.TRUE);
        instanceDO.setInstanceStatus("RUNNING");
        instanceDO.setLayerKeys(reqVO.getLayerKeys());
        instanceDO.setParentInstanceCode(reqVO.getParentInstanceCode());
        actorInstanceMapper.insert(instanceDO);

        List<ActorInstanceComponentDO> components;
        ComponentTreeVO componentTreeVO = reqVO.getComponentTree();
        if (componentTreeVO != null && componentTreeVO.getRoot() != null) {
            components = convertFromFrontendComponentTree(actorDO, instanceDO, componentTreeVO.getRoot());
        } else {
            components = buildActorInstanceComponents(actorDO, instanceDO.getId(), instanceDO.getInstanceCode());
        }
        validateRenderConfigs(components);
        components.forEach(actorInstanceComponentMapper::insert);
        webSocketService.broadcastPositionUpdate(resolveSceneCode(reqVO.getSceneId()), instanceDO.getId(), instanceDO.getInstanceCode(), reqVO.getTransform());
        return instanceDO.getId();
    }

    @Override
    public void deleteActorInstance(Long id) {
        ActorInstanceDO existing = getActorInstance(id);
        List<ActorInstanceComponentDO> components = actorInstanceComponentMapper
                .selectList(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ActorInstanceComponentDO>()
                        .eq(ActorInstanceComponentDO::getActorInstanceId, id));
        components.forEach(actorInstanceComponentMapper::deleteById);
        actorInstanceMapper.deleteById(id);
        webSocketService.broadcastVisibilityChange(resolveSceneCode(existing.getSceneId()), existing.getId(), existing.getInstanceCode(), Boolean.FALSE);
    }

    @Override
    public List<ActorInstanceDO> getChildrenInstances(String parentInstanceCode) {
        return actorInstanceMapper.selectListByParentInstanceCode(parentInstanceCode);
    }

    private List<ActorInstanceComponentDO> buildActorInstanceComponents(ActorDO actorDO, Long actorInstanceId, String instanceCode) {
        List<ActorInstanceComponentDO> result = new ArrayList<>();
        ComponentTree tree = actorDO.getComponentTree();
        if (tree == null || tree.getRoot() == null) {
            ActorInstanceComponentDO root = new ActorInstanceComponentDO();
            root.setActorInstanceId(actorInstanceId);
            root.setActorCode(actorDO.getActorCode());
            root.setInstanceCode(instanceCode);
            root.setComponentCode("RootComponent");
            root.setComponentTypeName("SceneComponent");
            root.setEnabledFlag(Boolean.TRUE);
            root.setSortNo(1);
            root.setOverrideJson("{}");
            root.setMetadataJson(actorDO.getMetadataJson());
            result.add(root);
            return result;
        }
        buildComponentNodes(tree.getRoot(), result, actorDO.getActorCode(), instanceCode, actorInstanceId, 1);
        return result;
    }

    private void buildComponentNodes(ComponentTreeNode sourceNode, List<ActorInstanceComponentDO> result,
                                     String actorCode, String instanceCode, Long actorInstanceId, int sortNo) {
        ActorInstanceComponentDO comp = new ActorInstanceComponentDO();
        comp.setActorInstanceId(actorInstanceId);
        comp.setActorCode(actorCode);
        comp.setInstanceCode(instanceCode);
        comp.setComponentCode(sourceNode.getComponentCode());
        comp.setComponentTypeName(sourceNode.getComponentTypeName());
        comp.setEnabledFlag(sourceNode.getEnabledFlag());
        comp.setSortNo(sortNo);
        comp.setRelativeTransform(sourceNode.getTransform());
        comp.setPropertiesJson(sourceNode.getPropertiesJson() != null ? sourceNode.getPropertiesJson() : "{}");
        comp.setOverrideJson("{}");
        comp.setConstructArgsJson(sourceNode.getConstructArgsJson() != null ? sourceNode.getConstructArgsJson() : "{}");
        comp.setMetadataJson(sourceNode.getMetadataJson());
        result.add(comp);
        if (sourceNode.getChildren() != null) {
            int childSort = 1;
            for (ComponentTreeNode child : sourceNode.getChildren()) {
                buildComponentNodes(child, result, actorCode, instanceCode, actorInstanceId, childSort++);
            }
        }
    }

    private List<ActorInstanceComponentDO> convertFromFrontendComponentTree(ActorDO actorDO,
                                                                            ActorInstanceDO instanceDO,
                                                                            ComponentTreeNodeVO rootNode) {
        List<ActorInstanceComponentDO> result = new ArrayList<>();
        ActorInstanceComponentDO rootComp = new ActorInstanceComponentDO();
        rootComp.setActorInstanceId(instanceDO.getId());
        rootComp.setActorCode(actorDO.getActorCode());
        rootComp.setInstanceCode(instanceDO.getInstanceCode());
        rootComp.setComponentCode(rootNode.getComponentCode());
        rootComp.setComponentTypeName(rootNode.getComponentTypeName());
        rootComp.setEnabledFlag(rootNode.getEnabledFlag());
        rootComp.setSortNo(rootNode.getSortNo() != null ? rootNode.getSortNo() : 1);
        if (rootNode.getTransform() != null) {
            rootComp.setRelativeTransform(rootNode.getTransform());
        }
        rootComp.setPropertiesJson(rootNode.getPropertiesJson() != null ? rootNode.getPropertiesJson() : "{}");
        rootComp.setOverrideJson(rootNode.getOverrideJson() != null ? rootNode.getOverrideJson() : "{}");
        rootComp.setConstructArgsJson(rootNode.getConstructArgsJson() != null ? rootNode.getConstructArgsJson() : "{}");
        rootComp.setMetadataJson(rootNode.getMetadataJson());
        result.add(rootComp);
        if (rootNode.getChildren() != null) {
            int childSort = 1;
            for (ComponentTreeNodeVO childVO : rootNode.getChildren()) {
                childVO.setSortNo(childSort++);
                result.addAll(convertFromFrontendComponentTree(actorDO, instanceDO, childVO));
            }
        }
        return result;
    }

    private void validateRenderConfigs(List<ActorInstanceComponentDO> components) {
        actorRenderConfigValidator.validateComponents(components);
    }

    private String resolveSceneCode(Long sceneId) {
        if (sceneId == null) {
            return null;
        }
        SceneDO scene = sceneMapper.selectById(sceneId);
        return scene == null || scene.getSceneCode() == null || scene.getSceneCode().isBlank()
                ? String.valueOf(sceneId)
                : scene.getSceneCode();
    }
}
