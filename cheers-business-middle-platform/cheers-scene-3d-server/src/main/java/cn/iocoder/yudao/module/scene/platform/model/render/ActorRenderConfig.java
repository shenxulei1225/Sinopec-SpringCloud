package cn.iocoder.yudao.module.scene.platform.model.render;

import lombok.Data;

@Data
public class ActorRenderConfig {

    /**
     * 渲染类型：
     * - gltf
     * - splat
     */
    private String renderType;

    /**
     * 普通模型地址
     */
    private String modelUrl;

    /**
     * Spark / Gaussian Splat 资源地址
     */
    private String splatUrl;

    /**
     * 配置来源：
     * - overrideJson
     * - metadataJson
     * - propertiesJson
     * - none
     */
    private String source;

    public boolean isEmpty() {
        return isBlank(renderType) && isBlank(modelUrl) && isBlank(splatUrl);
    }

    public boolean isGltf() {
        return "gltf".equalsIgnoreCase(renderType);
    }

    public boolean isSplat() {
        return "splat".equalsIgnoreCase(renderType);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
