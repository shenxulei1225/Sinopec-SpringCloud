package cn.iocoder.yudao.module.scene.platform.controller.admin.actor;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.scene.platform.controller.admin.actor.vo.ActorInstanceComponentTreeNodeRespVO;
import cn.iocoder.yudao.module.scene.platform.controller.admin.actor.vo.ActorInstanceComponentTreeRespVO;
import cn.iocoder.yudao.module.scene.platform.controller.admin.actor.vo.ActorInstanceRespVO;
import cn.iocoder.yudao.module.scene.platform.controller.admin.actor.vo.ActorInstanceSaveReqVO;
import cn.iocoder.yudao.module.scene.platform.controller.admin.actor.vo.ActorInstanceSimpleRespVO;
import cn.iocoder.yudao.module.scene.platform.controller.admin.actor.vo.ActorInstanceSpawnReqVO;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.actor.ActorInstanceComponentDO;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.actor.ActorInstanceDO;
import cn.iocoder.yudao.module.scene.platform.model.Rotator;
import cn.iocoder.yudao.module.scene.platform.model.Transform;
import cn.iocoder.yudao.module.scene.platform.model.Vector3;
import cn.iocoder.yudao.module.scene.platform.service.actor.ActorInstanceComponentService;
import cn.iocoder.yudao.module.scene.platform.service.actor.ActorInstanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - Actor 实例")
@RestController
@RequestMapping("/scene-platform/actor-instances")
@Validated
public class ActorInstanceController {

    @Resource
    private ActorInstanceService actorInstanceService;
    @Resource
    private ActorInstanceComponentService actorInstanceComponentService;

    @GetMapping("/list")
    @Operation(summary = "按场景 ID 获取 Actor 实例列表")
    public CommonResult<List<ActorInstanceRespVO>> getActorInstanceList(@RequestParam Long sceneId) {
        return success(convertList(actorInstanceService.getActorInstanceList(sceneId)));
    }

    @GetMapping("/simple-list")
    @Operation(summary = "按场景获取 ActorInstance 精简列表")
    public CommonResult<List<ActorInstanceSimpleRespVO>> getActorInstanceSimpleList(@RequestParam Long sceneId,
                                                                                     @RequestParam(value = "keyword", required = false) String keyword) {
        return success(actorInstanceService.getActorInstanceSimpleList(sceneId, keyword).stream().map(this::convertSimple).toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取 Actor 实例详情")
    public CommonResult<ActorInstanceRespVO> getActorInstance(@PathVariable Long id) {
        return success(convertActorInstanceWithComponents(actorInstanceService.getActorInstance(id)));
    }

    @PostMapping
    @Operation(summary = "创建 Actor 实例")
    public CommonResult<Long> createActorInstance(@Valid @RequestBody ActorInstanceSaveReqVO reqVO) {
        return success(actorInstanceService.createActorInstance(reqVO.toDO()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新 Actor 实例")
    public CommonResult<Boolean> updateActorInstance(@PathVariable Long id,
                                                     @Valid @RequestBody ActorInstanceSaveReqVO reqVO) {
        actorInstanceService.updateActorInstance(id, reqVO.toDO());
        return success(true);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除 Actor 实例")
    public CommonResult<Boolean> deleteActorInstance(@PathVariable Long id) {
        actorInstanceService.deleteActorInstance(id);
        return success(true);
    }

    @PostMapping("/spawn")
    @Operation(summary = "Spawn Actor 实例（拖拽入场景）")
    public CommonResult<ActorInstanceRespVO> spawnActorInstance(@Valid @RequestBody ActorInstanceSpawnReqVO reqVO) {
        Long instanceId = actorInstanceService.spawnActorInstance(reqVO);
        return success(convertActorInstanceWithComponents(actorInstanceService.getActorInstance(instanceId)));
    }

    private ActorInstanceRespVO convertActorInstanceWithComponents(ActorInstanceDO instance) {
        ActorInstanceRespVO vo = convertInstance(instance);
        if (instance.getId() != null) {
            List<ActorInstanceComponentDO> components = actorInstanceComponentService.listByInstanceId(instance.getId());
            if (components != null && !components.isEmpty()) {
                vo.setComponents(components.stream().map(this::convertComponent).toList());
                vo.setComponentTree(buildActorInstanceComponentTree(components));
            }
        }
        return vo;
    }

    private List<ActorInstanceRespVO> convertList(List<ActorInstanceDO> items) {
        return items.stream().map(this::convertActorInstanceWithComponents).toList();
    }

    private ActorInstanceRespVO convertInstance(ActorInstanceDO item) {
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
        vo.setPath(item.getPath());
        vo.setLayerKeys(item.getLayerKeys());
        vo.setMetadataJson(item.getMetadataJson());
        vo.setCreateTime(item.getCreateTime());
        vo.setUpdateTime(item.getUpdateTime());
        vo.setTransform(convertTransform(item.getTransform()));
        return vo;
    }

    private ActorInstanceSimpleRespVO convertSimple(ActorInstanceDO item) {
        ActorInstanceSimpleRespVO vo = new ActorInstanceSimpleRespVO();
        vo.setId(item.getId());
        vo.setSceneId(item.getSceneId());
        vo.setActorCode(item.getActorCode());
        vo.setInstanceCode(item.getInstanceCode());
        vo.setInstanceName(item.getInstanceName());
        vo.setParentInstanceCode(item.getParentInstanceCode());
        vo.setInstanceStatus(item.getInstanceStatus());
        vo.setVisibleFlag(item.getVisibleFlag());
        vo.setPath(item.getPath());
        vo.setLayerKeys(item.getLayerKeys());
        return vo;
    }

    private Transform convertTransform(Transform transform) {
        if (transform == null) {
            return null;
        }
        Transform voTransform = new Transform();
        Vector3 location = new Vector3();
        if (transform.getLocation() != null) {
            location.setX(transform.getLocation().getX());
            location.setY(transform.getLocation().getY());
            location.setZ(transform.getLocation().getZ());
        }
        voTransform.setLocation(location);
        Rotator rotation = new Rotator();
        if (transform.getRotation() != null) {
            rotation.setPitch(transform.getRotation().getPitch());
            rotation.setYaw(transform.getRotation().getYaw());
            rotation.setRoll(transform.getRotation().getRoll());
        }
        voTransform.setRotation(rotation);
        Vector3 scale = new Vector3();
        if (transform.getScale() != null) {
            scale.setX(transform.getScale().getX());
            scale.setY(transform.getScale().getY());
            scale.setZ(transform.getScale().getZ());
        }
        voTransform.setScale(scale);
        return voTransform;
    }

    private ActorInstanceRespVO.ActorInstanceComponentRespVO convertComponent(ActorInstanceComponentDO component) {
        ActorInstanceRespVO.ActorInstanceComponentRespVO vo = new ActorInstanceRespVO.ActorInstanceComponentRespVO();
        vo.setId(component.getId());
        vo.setInstanceCode(component.getInstanceCode());
        vo.setComponentCode(component.getComponentCode());
        vo.setComponentTypeName(component.getComponentTypeName());
        vo.setEnabledFlag(component.getEnabledFlag());
        vo.setSortNo(component.getSortNo());
        vo.setOverrideJson(component.getOverrideJson());
        vo.setConstructArgsJson(component.getConstructArgsJson());
        vo.setRelativeTransform(convertTransform(component.getRelativeTransform()));
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
        node.setRelativeTransform(convertTransform(component.getRelativeTransform()));
        node.setPropertiesJson(component.getPropertiesJson());
        node.setOverrideJson(component.getOverrideJson());
        node.setConstructArgsJson(component.getConstructArgsJson());
        node.setMetadataJson(component.getMetadataJson());
        return node;
    }
}
