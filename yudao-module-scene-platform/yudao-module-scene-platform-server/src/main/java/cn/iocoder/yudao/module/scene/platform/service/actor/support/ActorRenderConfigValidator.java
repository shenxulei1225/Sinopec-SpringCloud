package cn.iocoder.yudao.module.scene.platform.service.actor.support;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.actor.ActorInstanceComponentDO;
import cn.iocoder.yudao.module.scene.platform.model.render.ActorRenderConfig;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.scene.platform.enums.ErrorCodeConstants.SCENE_RENDER_CONFIG_INVALID;

@Component
public class ActorRenderConfigValidator {

    private static final Set<String> ALLOWED_RENDER_TYPES = Set.of(
            "gltf",
            "splat"
    );

    private static final Set<String> GLTF_EXTENSIONS = Set.of(
            ".gltf",
            ".glb"
    );

    private static final Set<String> SPLAT_EXTENSIONS = Set.of(
            ".spz",
            ".splat",
            ".ksplat",
            ".sog",
            ".ply"
    );

    /**
     * 第一阶段先限制为明确可视组件
     */
    private static final Set<String> RENDERABLE_COMPONENT_TYPES = Set.of(
            "StaticMeshComponent",
            "MeshComponent",
            "PrimitiveComponent",
            "SplineMeshComponent"
    );

    public void validateComponents(List<ActorInstanceComponentDO> components) {
        if (components == null || components.isEmpty()) {
            return;
        }
        for (ActorInstanceComponentDO component : components) {
            resolveAndValidate(component);
        }
    }

    public ActorRenderConfig resolveAndValidate(ActorInstanceComponentDO component) {
        ActorRenderConfig config = resolve(component);
        validate(config, component);
        return config;
    }

    public ActorRenderConfig resolve(ActorInstanceComponentDO component) {
        if (component == null) {
            return emptyConfig();
        }
        return resolveFromJsonChain(
                component.getOverrideJson(),
                component.getMetadataJson(),
                component.getPropertiesJson()
        );
    }

    private ActorRenderConfig resolveFromJsonChain(String overrideJson,
                                                   String metadataJson,
                                                   String propertiesJson) {
        JSONObject overrideObj = safeParse(overrideJson);
        JSONObject metadataObj = safeParse(metadataJson);
        JSONObject propertiesObj = safeParse(propertiesJson);

        ActorRenderConfig config = new ActorRenderConfig();
        config.setRenderType(normalizeRenderType(firstNonBlank(
                pickString(overrideObj, "renderType"),
                pickString(metadataObj, "renderType"),
                pickString(propertiesObj, "renderType")
        )));
        config.setModelUrl(trimToNull(firstNonBlank(
                pickString(overrideObj, "modelUrl"),
                pickString(metadataObj, "modelUrl"),
                pickString(propertiesObj, "modelUrl")
        )));
        config.setSplatUrl(trimToNull(firstNonBlank(
                pickString(overrideObj, "splatUrl"),
                pickString(metadataObj, "splatUrl"),
                pickString(propertiesObj, "splatUrl")
        )));
        config.setSource(resolveSource(overrideObj, metadataObj, propertiesObj));
        return config;
    }

    private void validate(ActorRenderConfig config, ActorInstanceComponentDO component) {
        if (config == null || config.isEmpty()) {
            return;
        }

        validateRenderType(config, component);
        validateUrlCombination(config, component);
        validateUrlExtension(config, component);
        validateComponentTypeCompatibility(config, component);
    }

    private void validateRenderType(ActorRenderConfig config, ActorInstanceComponentDO component) {
        if (isBlank(config.getRenderType())) {
            throw invalid(component, "缺少 renderType");
        }
        if (!ALLOWED_RENDER_TYPES.contains(config.getRenderType().toLowerCase())) {
            throw invalid(component, "renderType 非法: " + config.getRenderType());
        }
    }

    private void validateUrlCombination(ActorRenderConfig config, ActorInstanceComponentDO component) {
        if (config.isGltf() && isBlank(config.getModelUrl())) {
            throw invalid(component, "renderType=gltf 时 modelUrl 不能为空");
        }
        if (config.isSplat() && isBlank(config.getSplatUrl())) {
            throw invalid(component, "renderType=splat 时 splatUrl 不能为空");
        }
    }

    private void validateUrlExtension(ActorRenderConfig config, ActorInstanceComponentDO component) {
        if (config.isGltf() && !hasAllowedExtension(config.getModelUrl(), GLTF_EXTENSIONS)) {
            throw invalid(component, "modelUrl 后缀非法: " + config.getModelUrl());
        }
        if (config.isSplat() && !hasAllowedExtension(config.getSplatUrl(), SPLAT_EXTENSIONS)) {
            throw invalid(component, "splatUrl 后缀非法: " + config.getSplatUrl());
        }
    }

    private void validateComponentTypeCompatibility(ActorRenderConfig config, ActorInstanceComponentDO component) {
        if (config.isEmpty()) {
            return;
        }
        String componentType = trimToEmpty(component.getComponentTypeName());
        if (!RENDERABLE_COMPONENT_TYPES.contains(componentType)) {
            throw invalid(component, "组件类型不支持配置渲染资源: " + componentType);
        }
    }

    private RuntimeException invalid(ActorInstanceComponentDO component, String reason) {
        String componentCode = component == null ? "-" : trimToEmpty(component.getComponentCode());
        return exception(SCENE_RENDER_CONFIG_INVALID, componentCode, reason);
    }

    private ActorRenderConfig emptyConfig() {
        ActorRenderConfig config = new ActorRenderConfig();
        config.setSource("none");
        return config;
    }

    private JSONObject safeParse(String json) {
        if (isBlank(json)) {
            return new JSONObject(Collections.emptyMap());
        }
        try {
            return JSONUtil.parseObj(json);
        } catch (Exception e) {
            throw exception(SCENE_RENDER_CONFIG_INVALID, "-", "渲染配置 JSON 非法");
        }
    }

    private String resolveSource(JSONObject overrideObj,
                                 JSONObject metadataObj,
                                 JSONObject propertiesObj) {
        if (containsRenderConfig(overrideObj)) {
            return "overrideJson";
        }
        if (containsRenderConfig(metadataObj)) {
            return "metadataJson";
        }
        if (containsRenderConfig(propertiesObj)) {
            return "propertiesJson";
        }
        return "none";
    }

    private boolean containsRenderConfig(JSONObject jsonObject) {
        return notBlank(pickString(jsonObject, "renderType"))
                || notBlank(pickString(jsonObject, "modelUrl"))
                || notBlank(pickString(jsonObject, "splatUrl"));
    }

    private String pickString(JSONObject jsonObject, String key) {
        if (jsonObject == null || jsonObject.isEmpty()) {
            return null;
        }
        return trimToNull(jsonObject.getStr(key));
    }

    private String normalizeRenderType(String renderType) {
        return isBlank(renderType) ? null : renderType.trim().toLowerCase();
    }

    private boolean hasAllowedExtension(String url, Set<String> allowedExtensions) {
        if (isBlank(url)) {
            return false;
        }
        String lower = url.toLowerCase();
        for (String extension : allowedExtensions) {
            if (lower.endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

    private String firstNonBlank(String... values) {
        if (values == null) {
            return null;
        }
        for (String value : values) {
            if (!isBlank(value)) {
                return value.trim();
            }
        }
        return null;
    }

    private String trimToNull(String value) {
        return isBlank(value) ? null : value.trim();
    }

    private String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private boolean notBlank(String value) {
        return !isBlank(value);
    }
}
