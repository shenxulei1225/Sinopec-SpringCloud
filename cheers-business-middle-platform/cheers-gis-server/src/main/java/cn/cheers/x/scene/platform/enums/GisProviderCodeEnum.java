package cn.cheers.x.scene.platform.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * GIS 管理页「GIS 服务连接」里的五种地图服务。
 * 天地图、Cesium 只填令牌；GeoServer、超图、ArcGIS 填瓦片地址模板，令牌可选。
 * 编码与前端底图选项一致（tianditu / cesium-ion / geoserver / supermap / arcgis）。
 */
@Getter
@AllArgsConstructor
public enum GisProviderCodeEnum {

    TIANDITU("tianditu", "天地图", false),
    CESIUM_ION("cesium-ion", "Cesium", false),
    GEOSERVER("geoserver", "GeoServer", true),
    SUPERMAP("supermap", "超图", true),
    ARCGIS("arcgis", "ArcGIS", true);

    /** 前后端共用的服务编码 */
    private final String code;
    /** 没填显示名时界面用的名字 */
    private final String defaultLabel;
    /** true：按瓦片地址模板转发（本地化部署的服务） */
    private final boolean tileTemplate;

    /** 按编码找服务。编码不认识时返回 null，由调用方报错。 */
    public static GisProviderCodeEnum of(String code) {
        for (GisProviderCodeEnum value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }
}
