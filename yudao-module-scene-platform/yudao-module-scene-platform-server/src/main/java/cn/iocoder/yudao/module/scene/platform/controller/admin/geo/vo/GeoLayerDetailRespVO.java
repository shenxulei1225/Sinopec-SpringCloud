package cn.iocoder.yudao.module.scene.platform.controller.admin.geo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - GEO 图层详情响应 VO")
@Data
public class GeoLayerDetailRespVO {

    @Schema(description = "图层基础信息")
    private GeoLayerRespVO layer;

    @Schema(description = "图层关联资源")
    private List<cn.iocoder.yudao.module.scene.platform.controller.admin.asset.vo.SceneAssetRespVO> assets;
}
