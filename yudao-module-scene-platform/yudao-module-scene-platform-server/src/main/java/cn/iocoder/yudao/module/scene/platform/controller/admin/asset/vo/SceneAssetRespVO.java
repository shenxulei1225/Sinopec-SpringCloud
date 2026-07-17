package cn.iocoder.yudao.module.scene.platform.controller.admin.asset.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 场景资源响应 VO")
@Data
public class SceneAssetRespVO {

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "资源编码")
    private String assetCode;

    @Schema(description = "资源名称")
    private String assetName;

    @Schema(description = "资源类型")
    private String assetType;

    @Schema(description = "资源 URL")
    private String assetUrl;

    @Schema(description = "预览图 URL")
    private String previewUrl;

    @Schema(description = "源格式")
    private String format;

    @Schema(description = "元数据 JSON")
    private String metadataJson;

    @Schema(description = "绑定元数据 JSON")
    private String bindingMetadataJson;
}
