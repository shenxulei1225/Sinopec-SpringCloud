package cn.cheers.x.scene.platform.controller.admin.basemap.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * GIS 管理页保存一种地图服务。只改这一种，别的服务不动。
 * 天地图、Cesium：令牌为空表示清除这一种。
 * GeoServer、超图、ArcGIS：瓦片地址模板为空表示清除这一种。
 */
@Schema(description = "管理后台 - 地图服务连接保存 Request VO")
@Data
public class GisProviderConnectionSaveReqVO {

    @Schema(description = "访问令牌")
    @Size(max = 1024, message = "令牌不能超过 1024 个字符")
    private String token;

    @Schema(description = "瓦片地址模板，须含 {z}/{x}/{y}")
    @Size(max = 1024, message = "瓦片地址模板不能超过 1024 个字符")
    private String urlTemplate;

    @Schema(description = "令牌参数名", example = "token")
    @Size(max = 64, message = "令牌参数名不能超过 64 个字符")
    private String tokenQueryParam;

    @Schema(description = "界面显示名")
    @Size(max = 64, message = "显示名不能超过 64 个字符")
    private String label;

    @Schema(description = "Cesium Ion 影像资源编号", example = "2")
    @Positive(message = "影像资源编号须为正整数")
    private Integer imageryAssetId;
}
