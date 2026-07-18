package cn.iocoder.yudao.module.scene.platform.controller.admin.runtime;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.module.scene.platform.controller.admin.actor.vo.ActorInstanceRespVO;
import cn.iocoder.yudao.module.scene.platform.controller.admin.component.vo.SceneComponentRespVO;
import cn.iocoder.yudao.module.scene.platform.controller.admin.coordinate.vo.CoordinateReferenceRespVO;
import cn.iocoder.yudao.module.scene.platform.controller.admin.runtime.vo.SceneRuntimePackageRespVO;
import cn.iocoder.yudao.module.scene.platform.controller.admin.scene.vo.SceneLoadRespVO;
import cn.iocoder.yudao.module.scene.platform.controller.admin.scene.vo.SceneRespVO;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.asset.AssetResourceDO;
import cn.iocoder.yudao.module.scene.platform.dal.mysql.asset.AssetResourceMapper;
import cn.iocoder.yudao.module.scene.platform.model.Rotator;
import cn.iocoder.yudao.module.scene.platform.model.Transform;
import cn.iocoder.yudao.module.scene.platform.model.Vector3;
import cn.iocoder.yudao.module.scene.platform.service.asset.impl.AssetConvertServiceImpl;
import cn.iocoder.yudao.module.scene.platform.service.coordinate.CoordinateReferenceService;
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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 场景运行时")
@RestController("sceneRuntimePackageController")
@RequestMapping("/scene-platform/scenes/{sceneCode}/runtime")
@Validated
public class SceneRuntimeController {

    @Resource
    private SceneService sceneService;

    @Resource
    private AssetResourceMapper assetResourceMapper;

    @Resource
    private CoordinateReferenceService coordinateReferenceService;

    @GetMapping("/package")
    @Operation(summary = "获得场景运行时包")
    public CommonResult<SceneRuntimePackageRespVO> getRuntimePackage(@PathVariable String sceneCode) {
        SceneLoadRespVO sceneLoad = sceneService.loadScene(sceneCode);
        SceneRespVO sceneResp = sceneService.getSceneByCode(sceneCode);

        List<SceneRuntimePackageRespVO.ActorInstanceRuntimeConfigRespVO> actors =
                convertActorInstances(sceneLoad.getActorInstances());

        SceneRuntimePackageRespVO respVO = new SceneRuntimePackageRespVO();
        respVO.setSceneCode(sceneLoad.getScene().getSceneCode());
        respVO.setSceneName(sceneLoad.getScene().getSceneName());
        respVO.setActorInstances(actors);
        respVO.setSceneComponents(convertSceneComponents(sceneLoad.getSceneComponents()));
        respVO.setAssets(buildAssets(actors));
        respVO.setCoordinateReference(buildCoordinateReference(sceneCode));
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

    private List<SceneRuntimePackageRespVO.ActorInstanceRuntimeConfigRespVO> convertActorInstances(
            List<ActorInstanceRespVO> actorInstances) {
        if (actorInstances == null || actorInstances.isEmpty()) {
            return Collections.emptyList();
        }
        return actorInstances.stream().map(actor -> {
            SceneRuntimePackageRespVO.ActorInstanceRuntimeConfigRespVO item =
                    new SceneRuntimePackageRespVO.ActorInstanceRuntimeConfigRespVO();
            item.setActorCode(actor.getActorCode());
            item.setActorName(actor.getInstanceName());
            item.setInstanceName(actor.getInstanceName());
            item.setInstanceCode(actor.getInstanceCode());
            item.setId(actor.getId());
            fillTransform(item, actor.getTransform());
            String renderAssetCode = resolveRenderAssetCode(actor);
            item.setRenderAssetCode(renderAssetCode);
            if (StrUtil.isNotBlank(renderAssetCode)) {
                AssetResourceDO asset = assetResourceMapper.selectByAssetCode(renderAssetCode);
                if (asset != null) {
                    Map<String, Object> metadata = AssetConvertServiceImpl.parseMetadata(asset.getMetadataJson());
                    Object status = metadata.get(AssetConvertServiceImpl.METADATA_CONVERT_STATUS);
                    Object runtimeUrl = metadata.get(AssetConvertServiceImpl.METADATA_RUNTIME_URL);
                    // 仅 ready + runtimeUrl 暴露给画布；勿把 FBX/OBJ 源 URL 冒充可加载模型
                    if (AssetConvertServiceImpl.STATUS_READY.equals(String.valueOf(status)) && runtimeUrl != null) {
                        item.setModelUrl(String.valueOf(runtimeUrl));
                    }
                }
            }
            item.setBehaviorConfig(Collections.emptyMap());
            Map<String, Object> attrs = new LinkedHashMap<>();
            attrs.put("visibleFlag", actor.getVisibleFlag());
            if (StrUtil.isNotBlank(renderAssetCode)) {
                attrs.put("renderAssetCode", renderAssetCode);
            }
            item.setAttributeConfig(attrs);
            item.setEnabled(Boolean.TRUE.equals(actor.getVisibleFlag()));
            if (actor.getCreateTime() != null) {
                item.setCreateTime(actor.getCreateTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
            }
            return item;
        }).toList();
    }

    private List<SceneRuntimePackageRespVO.CompositionAssetRespVO> buildAssets(
            List<SceneRuntimePackageRespVO.ActorInstanceRuntimeConfigRespVO> actors) {
        Set<String> codes = new LinkedHashSet<>();
        for (SceneRuntimePackageRespVO.ActorInstanceRuntimeConfigRespVO actor : actors) {
            if (StrUtil.isNotBlank(actor.getRenderAssetCode())) {
                codes.add(actor.getRenderAssetCode());
            }
        }
        if (codes.isEmpty()) {
            return Collections.emptyList();
        }
        return codes.stream()
                .map(assetResourceMapper::selectByAssetCode)
                .filter(Objects::nonNull)
                .map(asset -> {
                    Map<String, Object> metadata = AssetConvertServiceImpl.parseMetadata(asset.getMetadataJson());
                    SceneRuntimePackageRespVO.CompositionAssetRespVO item =
                            new SceneRuntimePackageRespVO.CompositionAssetRespVO();
                    item.setAssetCode(asset.getAssetCode());
                    item.setAssetType(asset.getAssetType());
                    item.setFormat(asset.getFormat());
                    Object status = metadata.get(AssetConvertServiceImpl.METADATA_CONVERT_STATUS);
                    item.setConvertStatus(status != null ? String.valueOf(status) : null);
                    Object runtimeUrl = metadata.get(AssetConvertServiceImpl.METADATA_RUNTIME_URL);
                    if (AssetConvertServiceImpl.STATUS_READY.equals(String.valueOf(status)) && runtimeUrl != null) {
                        item.setRuntimeUrl(String.valueOf(runtimeUrl));
                    }
                    return item;
                })
                .toList();
    }

    private Map<String, Object> buildCoordinateReference(String sceneCode) {
        try {
            CoordinateReferenceRespVO ref = coordinateReferenceService.getBySceneCode(sceneCode);
            if (ref == null) {
                return Collections.emptyMap();
            }
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("sceneCode", ref.getSceneCode());
            map.put("originLng", ref.getOriginLng());
            map.put("originLat", ref.getOriginLat());
            map.put("originHeight", ref.getOriginHeight());
            // 前端历史字段名（与 originLng/Lat 同值）
            map.put("originLongitude", ref.getOriginLng());
            map.put("originLatitude", ref.getOriginLat());
            map.put("crsCode", ref.getCrsCode());
            map.put("crsName", ref.getCrsName());
            map.put("crsType", ref.getCrsType());
            map.put("metadataJson", ref.getMetadataJson());
            map.put("configured", ref.getOriginLng() != null && ref.getOriginLat() != null);
            return map;
        } catch (Exception ignored) {
            return Collections.emptyMap();
        }
    }

    /**
     * 从实例 metadata / 组件 override|constructArgs 解析 assetCode / renderAssetCode / modelAssetCode。
     */
    @SuppressWarnings("unchecked")
    private static String resolveRenderAssetCode(ActorInstanceRespVO actor) {
        String fromMeta = extractAssetCode(actor.getMetadataJson());
        if (StrUtil.isNotBlank(fromMeta)) {
            return fromMeta;
        }
        if (actor.getComponents() != null) {
            for (ActorInstanceRespVO.ActorInstanceComponentRespVO component : actor.getComponents()) {
                String code = extractAssetCode(component.getOverrideJson());
                if (StrUtil.isBlank(code)) {
                    code = extractAssetCode(component.getConstructArgsJson());
                }
                if (StrUtil.isNotBlank(code)) {
                    return code;
                }
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static String extractAssetCode(String json) {
        if (StrUtil.isBlank(json)) {
            return null;
        }
        Map<String, Object> map = JsonUtils.parseObject(json, Map.class);
        if (map == null || map.isEmpty()) {
            return null;
        }
        for (String key : List.of("renderAssetCode", "assetCode", "modelAssetCode")) {
            Object value = map.get(key);
            if (value != null && StrUtil.isNotBlank(String.valueOf(value))) {
                return String.valueOf(value).trim();
            }
        }
        return null;
    }

    private void fillTransform(SceneRuntimePackageRespVO.ActorInstanceRuntimeConfigRespVO item, Transform transform) {
        Vector3 location = transform != null && transform.getLocation() != null ? transform.getLocation() : new Vector3();
        Rotator rotation = transform != null && transform.getRotation() != null ? transform.getRotation() : new Rotator();
        Vector3 scale = transform != null && transform.getScale() != null
                ? transform.getScale()
                : new Vector3(1D, 1D, 1D);
        // 兼容旧字段
        item.setInitialX(location.getX());
        item.setInitialY(location.getY());
        item.setInitialZ(location.getZ());
        item.setInitialRotation(rotation.getYaw());
        // 画布编排：完整 Transform（含非均匀 scale / pitch / roll）
        Transform full = new Transform();
        full.setLocation(location);
        full.setRotation(rotation);
        full.setScale(scale);
        item.setTransform(full);
    }

    private List<SceneRuntimePackageRespVO.SceneComponentRuntimeConfigRespVO> convertSceneComponents(
            List<SceneComponentRespVO> sceneComponents) {
        if (sceneComponents == null || sceneComponents.isEmpty()) {
            return Collections.emptyList();
        }
        return sceneComponents.stream().map(component -> {
            SceneRuntimePackageRespVO.SceneComponentRuntimeConfigRespVO item =
                    new SceneRuntimePackageRespVO.SceneComponentRuntimeConfigRespVO();
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
