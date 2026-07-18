package cn.iocoder.yudao.module.scene.platform.service.scene.impl;

import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.scene.platform.controller.admin.actor.vo.ActorInstanceComponentTreeNodeRespVO;
import cn.iocoder.yudao.module.scene.platform.controller.admin.actor.vo.ActorInstanceComponentTreeRespVO;
import cn.iocoder.yudao.module.scene.platform.controller.admin.actor.vo.ActorInstanceRespVO;
import cn.iocoder.yudao.module.scene.platform.controller.admin.component.vo.SceneComponentRespVO;
import cn.iocoder.yudao.module.scene.platform.controller.admin.scene.vo.ActorRuntimeRespVO;
import cn.iocoder.yudao.module.scene.platform.controller.admin.scene.vo.SceneDetailRespVO;
import cn.iocoder.yudao.module.scene.platform.controller.admin.scene.vo.SceneLoadRespVO;
import cn.iocoder.yudao.module.scene.platform.controller.admin.scene.vo.ScenePageReqVO;
import cn.iocoder.yudao.module.scene.platform.controller.admin.scene.vo.SceneRespVO;
import cn.iocoder.yudao.module.scene.platform.controller.admin.scene.vo.SceneSaveReqVO;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.actor.ActorInstanceComponentDO;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.actor.ActorInstanceDO;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.scene.SceneDO;
import cn.iocoder.yudao.module.scene.platform.dal.mysql.scene.SceneMapper;
import cn.iocoder.yudao.module.scene.platform.model.Rotator;
import cn.iocoder.yudao.module.scene.platform.model.Transform;
import cn.iocoder.yudao.module.scene.platform.model.Vector3;
import cn.iocoder.yudao.module.scene.platform.service.actor.ActorInstanceComponentService;
import cn.iocoder.yudao.module.scene.platform.service.actor.ActorInstanceService;
import cn.iocoder.yudao.module.scene.platform.service.component.SceneComponentService;
import cn.iocoder.yudao.module.scene.platform.service.scene.SceneRuntimeService;
import cn.iocoder.yudao.module.scene.platform.service.scene.SceneService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.scene.platform.enums.ErrorCodeConstants.SCENE_NOT_EXISTS;

/**
 * 场景服务实现类
 */
@Slf4j
@Service
@Validated
public class SceneServiceImpl implements SceneService {

    @Resource
    private SceneMapper sceneMapper;

    @Resource
    private ActorInstanceService actorInstanceService;

    @Resource
    private ActorInstanceComponentService actorInstanceComponentService;

    @Resource
    private SceneComponentService sceneComponentService;

    @Resource
    private SceneRuntimeService sceneRuntimeService;

    @Override
    public Long createScene(SceneSaveReqVO reqVO) {
        if (reqVO.getSceneCode() == null || reqVO.getSceneCode().trim().isEmpty()) {
            reqVO.setSceneCode(generateSceneCode());
        }
        SceneDO scene = convertToDO(reqVO);
        sceneMapper.insert(scene);
        log.info("[createScene] 场景创建成功, sceneCode={}, sceneName={}", scene.getSceneCode(), scene.getSceneName());
        return scene.getId();
    }

    @Override
    public void updateScene(SceneSaveReqVO reqVO) {
        SceneDO scene = convertToDO(reqVO);
        validateSceneExists(scene.getId());
        sceneMapper.updateById(scene);
        log.info("[updateScene] 场景更新成功, sceneCode={}, sceneName={}", scene.getSceneCode(), scene.getSceneName());
    }

    @Override
    public void deleteScene(Long id) {
        validateSceneExists(id);
        sceneMapper.deleteById(id);
        log.info("[deleteScene] 场景删除成功, id={}", id);
    }

    @Override
    public void deleteSceneByCode(String sceneCode) {
        SceneDO scene = sceneMapper.selectBySceneCode(sceneCode);
        if (scene == null) {
            throw exception(SCENE_NOT_EXISTS);
        }
        sceneMapper.deleteById(scene.getId());
        log.info("[deleteSceneByCode] 场景删除成功, sceneCode={}", sceneCode);
    }

    @Override
    public SceneRespVO getScene(Long id) {
        validateSceneExists(id);
        return convertToRespVO(sceneMapper.selectById(id));
    }

    @Override
    public SceneRespVO getSceneByCode(String sceneCode) {
        SceneDO scene = sceneMapper.selectBySceneCode(sceneCode);
        if (scene == null) {
            throw exception(SCENE_NOT_EXISTS);
        }
        return convertToRespVO(scene);
    }

    @Override
    public List<SceneRespVO> listScene(ScenePageReqVO reqVO) {
        LambdaQueryWrapperX<SceneDO> wrapper = new LambdaQueryWrapperX<SceneDO>()
                .likeIfPresent(SceneDO::getSceneCode, reqVO.getSceneCode())
                .likeIfPresent(SceneDO::getSceneName, reqVO.getSceneName())
                .eqIfPresent(SceneDO::getSceneType, reqVO.getSceneType())
                .eqIfPresent(SceneDO::getStatus, reqVO.getStatus())
                .eqIfPresent(SceneDO::getPublishStatus, reqVO.getPublishStatus())
                .eqIfPresent(SceneDO::getProjectCode, reqVO.getProjectCode())
                .orderByDesc(SceneDO::getId);
        return sceneMapper.selectList(wrapper).stream().map(this::convertToRespVO).collect(Collectors.toList());
    }

    @Override
    public List<SceneRespVO> listSceneByStatus(Integer status) {
        return sceneMapper.selectByStatus(status).stream().map(this::convertToRespVO).collect(Collectors.toList());
    }

    @Override
    public List<SceneRespVO> listSceneByProjectCode(String projectCode) {
        return sceneMapper.selectByProjectCode(projectCode).stream().map(this::convertToRespVO).collect(Collectors.toList());
    }

    @Override
    public List<SceneRespVO> listSceneByPublishStatus(String publishStatus) {
        return sceneMapper.selectByPublishStatus(publishStatus).stream().map(this::convertToRespVO).collect(Collectors.toList());
    }

    @Override
    public SceneLoadRespVO loadScene(String sceneCode) {
        SceneDO scene = sceneMapper.selectByCode(sceneCode);
        if (scene == null) {
            throw exception(SCENE_NOT_EXISTS);
        }

        Long sceneId = scene.getId();
        List<ActorInstanceDO> actorInstances = actorInstanceService.getActorInstanceList(sceneId);
        Map<String, ActorRuntimeRespVO> runtimeMap = loadRuntimeData(sceneCode, actorInstances);
        List<SceneComponentRespVO> sceneComponents = sceneComponentService.listComponents(sceneId);

        SceneLoadRespVO respVO = new SceneLoadRespVO();
        respVO.setScene(convertToSceneDetailRespVO(scene));
        respVO.setActorInstances(convertActorInstancesWithRuntime(actorInstances, runtimeMap));
        respVO.setSceneComponents(sceneComponents);
        respVO.setOnlineCount(sceneRuntimeService.getSceneOnlineCount(sceneCode));

        log.info("[loadScene] 场景加载成功, sceneCode={}, actorCount={}, componentCount={}",
                sceneCode, actorInstances.size(), sceneComponents.size());
        return respVO;
    }

    @Override
    public void publishScene(String sceneCode) {
        SceneDO scene = sceneMapper.selectByCode(sceneCode);
        if (scene == null) {
            throw exception(SCENE_NOT_EXISTS);
        }
        scene.setPublishStatus("PUBLISHED");
        scene.setPublishedAt(LocalDateTime.now());
        scene.setPublishedBy(getCurrentUsername());
        sceneMapper.updateById(scene);
        log.info("[publishScene] 场景发布成功, sceneCode={}", sceneCode);
    }

    @Override
    public void unpublishScene(String sceneCode) {
        SceneDO scene = sceneMapper.selectByCode(sceneCode);
        if (scene == null) {
            throw exception(SCENE_NOT_EXISTS);
        }
        scene.setPublishStatus("DRAFT");
        sceneMapper.updateById(scene);
        log.info("[unpublishScene] 场景取消发布成功, sceneCode={}", sceneCode);
    }

    private Map<String, ActorRuntimeRespVO> loadRuntimeData(String sceneCode, List<ActorInstanceDO> actorInstances) {
        if (actorInstances == null || actorInstances.isEmpty()) {
            return Collections.emptyMap();
        }
        List<ActorRuntimeRespVO> allRuntime = sceneRuntimeService.getAllActorsRuntime(sceneCode);
        Map<String, ActorRuntimeRespVO> runtimeMap = new HashMap<>();
        if (allRuntime != null) {
            for (ActorRuntimeRespVO runtime : allRuntime) {
                runtimeMap.put(runtime.getInstanceCode(), runtime);
            }
        }
        return runtimeMap;
    }

    private List<ActorInstanceRespVO> convertActorInstancesWithRuntime(List<ActorInstanceDO> actors,
                                                                       Map<String, ActorRuntimeRespVO> runtimeMap) {
        if (actors == null || actors.isEmpty()) {
            return Collections.emptyList();
        }
        return actors.stream()
                .map(actor -> convertToActorInstanceRespVO(mergeActorWithRuntime(actor, runtimeMap.get(actor.getInstanceCode()))))
                .collect(Collectors.toList());
    }

    private ActorInstanceDO mergeActorWithRuntime(ActorInstanceDO actorDef, ActorRuntimeRespVO runtimeData) {
        if (runtimeData == null) {
            return actorDef;
        }
        ActorInstanceDO merged = new ActorInstanceDO();
        merged.setId(actorDef.getId());
        merged.setSceneId(actorDef.getSceneId());
        merged.setActorCode(actorDef.getActorCode());
        merged.setInstanceCode(actorDef.getInstanceCode());
        merged.setInstanceName(actorDef.getInstanceName());
        merged.setParentInstanceCode(actorDef.getParentInstanceCode());
        merged.setInstanceStatus(runtimeData.getStatus() != null ? runtimeData.getStatus() : actorDef.getInstanceStatus());
        merged.setTransform(buildRuntimeTransform(runtimeData, actorDef.getTransform()));
        merged.setPath(actorDef.getPath());
        merged.setLayerKeys(actorDef.getLayerKeys());
        merged.setVisibleFlag(actorDef.getVisibleFlag());
        merged.setVersionNo(actorDef.getVersionNo());
        merged.setMetadataJson(actorDef.getMetadataJson());
        merged.setCreateTime(actorDef.getCreateTime());
        merged.setUpdateTime(actorDef.getUpdateTime());
        return merged;
    }

    private Transform buildRuntimeTransform(ActorRuntimeRespVO runtimeData, Transform fallback) {
        if (runtimeData == null) {
            return fallback;
        }
        Transform transform = new Transform();
        Vector3 location = runtimeData.getLocation() != null ? runtimeData.getLocation() : fallback != null ? fallback.getLocation() : null;
        Rotator rotation = runtimeData.getRotation() != null ? runtimeData.getRotation() : fallback != null ? fallback.getRotation() : null;
        Vector3 scale = runtimeData.getScale() != null ? runtimeData.getScale() : fallback != null ? fallback.getScale() : null;
        transform.setLocation(location);
        transform.setRotation(rotation);
        transform.setScale(scale);
        return transform;
    }

    private ActorInstanceRespVO convertToActorInstanceRespVO(ActorInstanceDO item) {
        ActorInstanceRespVO vo = new ActorInstanceRespVO();
        vo.setId(item.getId());
        vo.setSceneId(item.getSceneId());
        vo.setActorCode(item.getActorCode());
        vo.setInstanceCode(item.getInstanceCode());
        vo.setInstanceName(item.getInstanceName());
        vo.setParentInstanceCode(item.getParentInstanceCode());
        vo.setInstanceStatus(item.getInstanceStatus());
        vo.setVisibleFlag(item.getVisibleFlag());
        vo.setVersionNo(item.getVersionNo());
        vo.setTransform(item.getTransform());
        vo.setMetadataJson(item.getMetadataJson());
        vo.setPath(item.getPath());
        vo.setLayerKeys(item.getLayerKeys());
        vo.setCreateTime(item.getCreateTime());
        vo.setUpdateTime(item.getUpdateTime());
        if (item.getId() != null) {
            List<ActorInstanceComponentDO> componentList = actorInstanceComponentService.listByInstanceId(item.getId());
            if (componentList != null && !componentList.isEmpty()) {
                vo.setComponents(componentList.stream().map(component -> {
                    ActorInstanceRespVO.ActorInstanceComponentRespVO componentVO = new ActorInstanceRespVO.ActorInstanceComponentRespVO();
                    componentVO.setId(component.getId());
                    componentVO.setInstanceCode(component.getInstanceCode());
                    componentVO.setComponentCode(component.getComponentCode());
                    componentVO.setComponentTypeName(component.getComponentTypeName());
                    componentVO.setRelativeTransform(component.getRelativeTransform());
                    componentVO.setEnabledFlag(component.getEnabledFlag());
                    componentVO.setSortNo(component.getSortNo());
                    componentVO.setOverrideJson(component.getOverrideJson());
                    componentVO.setConstructArgsJson(component.getConstructArgsJson());
                    return componentVO;
                }).collect(Collectors.toList()));
                vo.setComponentTree(buildActorInstanceComponentTree(componentList));
            }
        }
        return vo;
    }

    private ActorInstanceComponentTreeRespVO buildActorInstanceComponentTree(List<ActorInstanceComponentDO> components) {
        if (components == null || components.isEmpty()) {
            return null;
        }

        List<ActorInstanceComponentDO> sorted = new ArrayList<>(components);
        sorted.sort((a, b) -> {
            int sortCompare = Integer.compare(a.getSortNo() == null ? 0 : a.getSortNo(), b.getSortNo() == null ? 0 : b.getSortNo());
            if (sortCompare != 0) {
                return sortCompare;
            }
            return Long.compare(a.getId() == null ? 0L : a.getId(), b.getId() == null ? 0L : b.getId());
        });

        Map<String, ActorInstanceComponentTreeNodeRespVO> nodeMap = new HashMap<>();
        List<ActorInstanceComponentTreeNodeRespVO> rootCandidates = new ArrayList<>();

        for (ActorInstanceComponentDO component : sorted) {
            nodeMap.put(component.getComponentCode(), convertTreeNode(component));
        }

        for (ActorInstanceComponentDO component : sorted) {
            ActorInstanceComponentTreeNodeRespVO node = nodeMap.get(component.getComponentCode());
            String parentCode = component.getParentComponentCode();
            if (parentCode != null && !parentCode.trim().isEmpty() && nodeMap.containsKey(parentCode)) {
                nodeMap.get(parentCode).getChildren().add(node);
            } else {
                rootCandidates.add(node);
            }
        }

        if (rootCandidates.isEmpty()) {
            return null;
        }

        ActorInstanceComponentTreeNodeRespVO root = rootCandidates.get(0);
        if (rootCandidates.size() > 1) {
            for (int i = 1; i < rootCandidates.size(); i++) {
                root.getChildren().add(rootCandidates.get(i));
            }
        }

        ActorInstanceComponentTreeRespVO tree = new ActorInstanceComponentTreeRespVO();
        tree.setRoot(root);
        return tree;
    }

    private ActorInstanceComponentTreeNodeRespVO convertTreeNode(ActorInstanceComponentDO component) {
        ActorInstanceComponentTreeNodeRespVO node = new ActorInstanceComponentTreeNodeRespVO();
        node.setId(component.getId());
        node.setInstanceCode(component.getInstanceCode());
        node.setComponentCode(component.getComponentCode());
        node.setComponentTypeName(component.getComponentTypeName());
        node.setEnabledFlag(component.getEnabledFlag());
        node.setSortNo(component.getSortNo());
        node.setParentComponentCode(component.getParentComponentCode());
        node.setRelativeTransform(component.getRelativeTransform());
        node.setPropertiesJson(component.getPropertiesJson());
        node.setOverrideJson(component.getOverrideJson());
        node.setConstructArgsJson(component.getConstructArgsJson());
        node.setMetadataJson(component.getMetadataJson());
        return node;
    }

    private String getCurrentUsername() {
        return "admin";
    }

    private void validateSceneExists(Long id) {
        if (sceneMapper.selectById(id) == null) {
            throw exception(SCENE_NOT_EXISTS);
        }
    }

    private String generateSceneCode() {
        return "SCENE_" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }

    private SceneDO convertToDO(SceneSaveReqVO reqVO) {
        SceneDO scene = new SceneDO();
        scene.setId(reqVO.getId());
        scene.setSceneCode(reqVO.getSceneCode());
        scene.setSceneName(reqVO.getSceneName());
        scene.setSceneType(reqVO.getSceneType());
        scene.setEngineProfile(reqVO.getEngineProfile());
        scene.setCapabilitiesJson(reqVO.getCapabilitiesJson());
        scene.setLayerConfigJson(reqVO.getLayerConfigJson());
        scene.setDefaultViewpointJson(reqVO.getDefaultViewpointJson());
        scene.setProjectCode(reqVO.getProjectCode());
        scene.setBusinessKey(reqVO.getBusinessKey());
        scene.setStatus(reqVO.getStatus());
        scene.setPublishStatus(reqVO.getPublishStatus());
        return scene;
    }

    private SceneRespVO convertToRespVO(SceneDO scene) {
        SceneRespVO respVO = new SceneRespVO();
        respVO.setId(scene.getId());
        respVO.setSceneCode(scene.getSceneCode());
        respVO.setSceneName(scene.getSceneName());
        respVO.setSceneType(scene.getSceneType());
        respVO.setEngineProfile(scene.getEngineProfile());
        respVO.setCapabilitiesJson(scene.getCapabilitiesJson());
        respVO.setLayerConfigJson(scene.getLayerConfigJson());
        respVO.setDefaultViewpointJson(scene.getDefaultViewpointJson());
        respVO.setProjectCode(scene.getProjectCode());
        respVO.setBusinessKey(scene.getBusinessKey());
        respVO.setStatus(scene.getStatus());
        respVO.setPublishStatus(scene.getPublishStatus());
        respVO.setPublishedAt(scene.getPublishedAt());
        respVO.setPublishedBy(scene.getPublishedBy());
        respVO.setActorInstanceCodesJson(scene.getActorInstanceCodesJson());
        respVO.setCreateTime(scene.getCreateTime());
        respVO.setUpdateTime(scene.getUpdateTime());
        return respVO;
    }

    private SceneDetailRespVO convertToSceneDetailRespVO(SceneDO scene) {
        SceneDetailRespVO respVO = new SceneDetailRespVO();
        respVO.setId(scene.getId());
        respVO.setSceneCode(scene.getSceneCode());
        respVO.setSceneName(scene.getSceneName());
        respVO.setSceneType(scene.getSceneType());
        respVO.setEngineProfile(scene.getEngineProfile());
        respVO.setCapabilitiesJson(scene.getCapabilitiesJson());
        respVO.setLayerConfigJson(scene.getLayerConfigJson());
        respVO.setDefaultViewpointJson(scene.getDefaultViewpointJson());
        respVO.setStatus(scene.getStatus());
        respVO.setPublishStatus(scene.getPublishStatus());
        respVO.setPublishedAt(scene.getPublishedAt());
        respVO.setPublishedBy(scene.getPublishedBy());
        return respVO;
    }
}
