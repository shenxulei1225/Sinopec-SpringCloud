package cn.cheers.x.scene.platform.service.basemap.support;

import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import org.springframework.util.StringUtils;

import java.net.URI;

/**
 * GIS 管理页保存 GeoServer、超图、ArcGIS 时，检查填的瓦片地址模板能不能用。
 * 模板不合格直接报错，让用户改。不负责：拼瓦片地址、取瓦片（浏览器直连地图服务器）。
 */
public final class GisTileUrlTemplates {

    private GisTileUrlTemplates() {
    }

    /** 模板须是 http(s) 绝对地址，并含 {z}、{x}、{y}。 */
    public static void validate(String template) {
        String lower = template.toLowerCase();
        if (!lower.startsWith("http://") && !lower.startsWith("https://")) {
            throw ServiceExceptionUtil.invalidParamException("瓦片地址模板须为 http(s) 绝对地址");
        }
        if (!template.contains("{z}") || !template.contains("{x}") || !template.contains("{y}")) {
            throw ServiceExceptionUtil.invalidParamException("瓦片地址模板须包含 {z}/{x}/{y}");
        }
        String sample = template.replace("{z}", "0").replace("{x}", "0").replace("{y}", "0");
        try {
            URI uri = URI.create(sample);
            if (!StringUtils.hasText(uri.getHost())) {
                throw ServiceExceptionUtil.invalidParamException("瓦片地址模板缺少主机名");
            }
        } catch (IllegalArgumentException e) {
            throw ServiceExceptionUtil.invalidParamException("瓦片地址模板无法解析");
        }
    }
}
