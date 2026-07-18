package cn.cheers.x.scene.platform.controller.admin.geo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - GEO 图层响应 VO")
@Data
public class GeoLayerRespVO {

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "场景编码")
    private String sceneCode;

    @Schema(description = "图层键")
    private String layerKey;

    @Schema(description = "图层名称")
    private String layerName;

    @Schema(description = "图层类型")
    private String layerType;

    @Schema(description = "是否可见")
    private Boolean visibleFlag;

    @Schema(description = "排序")
    private Integer sortNo;

    @Schema(description = "元数据 JSON")
    private String metadataJson;
}
