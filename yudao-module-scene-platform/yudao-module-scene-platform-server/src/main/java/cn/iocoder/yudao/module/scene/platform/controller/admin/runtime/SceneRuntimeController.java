package cn.iocoder.yudao.module.scene.platform.controller.admin.runtime;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.scene.platform.controller.admin.actor.vo.ActorInstanceRespVO;
import cn.iocoder.yudao.module.scene.platform.controller.admin.component.vo.SceneComponentRespVO;
import cn.iocoder.yudao.module.scene.platform.controller.admin.runtime.vo.SceneRuntimePackageRespVO;
import cn.iocoder.yudao.module.scene.platform.controller.admin.scene.vo.SceneLoadRespVO;
import cn.iocoder.yudao.module.scene.platform.controller.admin.scene.vo.SceneRespVO;
import cn.iocoder.yudao.module.scene.platform.model.Rotator;
import cn.iocoder.yudao.module.scene.platform.model.Transform;
import cn.iocoder.yudao.module.scene.platform.model.Vector3;
import cn.iocoder.yudao.module.scene.platform.service.scene.SceneService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZoneId;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 场景运行时")
@RestController("sceneRuntimePackageController")
@RequestMapping("/scene-platform/scenes/{sceneCode}/runtime")
@Validated
public class SceneRuntimeController {

    @Resource
    private SceneService sceneService;

    @GetMapping("/package")
    @Operation(summary = "获得场景运行时包")
    public CommonResult<SceneRuntimePackageRespVO> getRuntimePackage(@PathVariable String sceneCode) {
        SceneLoadRespVO sceneLoad = sceneService.loadScene(sceneCode);
        SceneRespVO sceneResp = sceneService.getSceneByCode(sceneCode);

        SceneRuntimePackageRespVO respVO = new SceneRuntimePackageRespVO();
        respVO.setSceneCode(sceneLoad.getScene().getSceneCode());
        respVO.setSceneName(sceneLoad.getScene().getSceneName());
        respVO.setActorInstances(convertActorInstances(sceneLoad.getActorInstances()));
        respVO.setSceneComponents(convertSceneComponents(sceneLoad.getSceneComponents()));
        respVO.setEnvVars(Collections.emptyMap());
        respVO.setRuntimeConfig(buildRuntimeConfig(sceneLoad));
        respVO.setVersion(sceneLoad.getScene().getPublishStatus());
        if (sceneResp != null && sceneResp.getCreateTime() != null) {
            respVO.setCreateTime(sceneResp.getCreateTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        }
        if (sceneResp != null && sceneResp.getUpdateTime() != null) {
            respVO.setUpdateTime(sceneResp.getUpdateTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        }
        return success(respVO);
    }

    private List<SceneRuntimePackageRespVO.ActorInstanceRuntimeConfigRespVO> convertActorInstances(List<ActorInstanceRespVO> actorInstances) {
        if (actorInstances == null || actorInstances.isEmpty()) {
            return Collections.emptyList();
        }
        return actorInstances.stream().map(actor -> {
            SceneRuntimePackageRespVO.ActorInstanceRuntimeConfigRespVO item = new SceneRuntimePackageRespVO.ActorInstanceRuntimeConfigRespVO();
            item.setActorCode(actor.getActorCode());
            item.setActorName(actor.getInstanceName());
            item.setInstanceCode(actor.getInstanceCode());
            fillTransform(item, actor.getTransform());
            item.setBehaviorConfig(Collections.emptyMap());
            item.setAttributeConfig(Collections.singletonMap("visibleFlag", actor.getVisibleFlag()));
            item.setEnabled(Boolean.TRUE.equals(actor.getVisibleFlag()));
            if (actor.getCreateTime() != null) {
                item.setCreateTime(actor.getCreateTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
            }
            return item;
        }).toList();
    }

    private void fillTransform(SceneRuntimePackageRespVO.ActorInstanceRuntimeConfigRespVO item, Transform transform) {
        Vector3 location = transform != null && transform.getLocation() != null ? transform.getLocation() : new Vector3();
        Rotator rotation = transform != null && transform.getRotation() != null ? transform.getRotation() : new Rotator();
        item.setInitialX(location.getX());
        item.setInitialY(location.getY());
        item.setInitialZ(location.getZ());
        item.setInitialRotation(rotation.getYaw());
    }

    private List<SceneRuntimePackageRespVO.SceneComponentRuntimeConfigRespVO> convertSceneComponents(List<SceneComponentRespVO> sceneComponents) {
        if (sceneComponents == null || sceneComponents.isEmpty()) {
            return Collections.emptyList();
        }
        return sceneComponents.stream().map(component -> {
            SceneRuntimePackageRespVO.SceneComponentRuntimeConfigRespVO item = new SceneRuntimePackageRespVO.SceneComponentRuntimeConfigRespVO();
            item.setComponentCode(component.getComponentName());
            item.setComponentName(component.getDisplayName());
            item.setComponentType(component.getComponentType());
            Map<String, Object> params = new LinkedHashMap<>();
            params.put("configJson", component.getConfigJson());
            params.put("metadataJson", component.getMetadataJson());
            item.setParams(params);
            Transform transform = component.getRelativeTransform();
            Vector3 location = transform != null && transform.getLocation() != null ? transform.getLocation() : new Vector3();
            item.setPosX(location.getX());
            item.setPosY(location.getY());
            item.setPosZ(location.getZ());
            item.setEnabled(component.getEnabled());
            if (component.getCreateTime() != null) {
                item.setCreateTime(component.getCreateTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
            }
            return item;
        }).toList();
    }

    private SceneRuntimePackageRespVO.RuntimeConfigRespVO buildRuntimeConfig(SceneLoadRespVO sceneLoad) {
        SceneRuntimePackageRespVO.RuntimeConfigRespVO runtimeConfig = new SceneRuntimePackageRespVO.RuntimeConfigRespVO();
        runtimeConfig.setMaxActors(sceneLoad.getActorInstances() == null ? 0 : sceneLoad.getActorInstances().size());
        runtimeConfig.setPhysicsEnabled(Boolean.TRUE);
        runtimeConfig.setSyncInterval(50);
        runtimeConfig.setLogLevel("INFO");
        runtimeConfig.setCustomConfig(Collections.singletonMap("onlineCount", sceneLoad.getOnlineCount()));
        return runtimeConfig;
    }
}
