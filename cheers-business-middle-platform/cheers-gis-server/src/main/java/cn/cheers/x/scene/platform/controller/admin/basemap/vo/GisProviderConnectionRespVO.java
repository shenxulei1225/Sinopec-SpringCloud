package cn.cheers.x.scene.platform.controller.admin.basemap.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 一种地图服务的连接。GIS 管理页回显它，地图页用它拼瓦片地址、由浏览器直连地图服务器。
 * 令牌是给浏览器用的 key（天地图浏览器端 key、Cesium 只读 token），安全靠服务商后台的域名白名单。
 * 没配置的服务也返回一行，configured=false。
 */
@Schema(description = "管理后台 - 地图服务连接 Response VO")
@Data
public class GisProviderConnectionRespVO {

    @Schema(description = "地图服务编码", example = "tianditu")
    private String providerCode;

    @Schema(description = "界面显示名", example = "天地图")
    private String label;

    @Schema(description = "是否已配置。天地图、Cesium 看令牌，GeoServer、超图、ArcGIS 看瓦片地址模板")
    private Boolean configured;

    @Schema(description = "访问令牌")
    private String token;

    @Schema(description = "瓦片地址模板（GeoServer、超图、ArcGIS）")
    private String urlTemplate;

    @Schema(description = "令牌参数名（GeoServer、超图、ArcGIS），没填时为 token", example = "token")
    private String tokenQueryParam;

    @Schema(description = "Cesium Ion 影像资源编号，没填时为 2（Bing 卫星影像）", example = "2")
    private Integer imageryAssetId;
}
