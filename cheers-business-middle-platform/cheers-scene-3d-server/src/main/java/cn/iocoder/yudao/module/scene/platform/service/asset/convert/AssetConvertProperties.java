package cn.iocoder.yudao.module.scene.platform.service.asset.convert;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "yudao.scene-platform.asset-convert")
public class AssetConvertProperties {

    private boolean enabled = true;

    /** 转换工作目录 */
    private String workDir = System.getProperty("user.home") + "/uploads/scene-assets/convert-work";

    /** 转换产物可访问 URL 前缀；空则使用 file:// 或上传配置 */
    private String outputBaseUrl = "";

    private Map<String, PluginConfig> plugins = new LinkedHashMap<>();

    @Data
    public static class PluginConfig {
        private boolean enabled = false;
        /** 例: assimp export {input} {output} -fglb2 */
        private String command = "";
        private int timeoutSeconds = 120;
    }
}
