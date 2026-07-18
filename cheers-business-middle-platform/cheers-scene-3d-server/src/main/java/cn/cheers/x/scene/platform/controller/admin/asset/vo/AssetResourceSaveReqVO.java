package cn.cheers.x.scene.platform.controller.admin.asset.vo;

import cn.cheers.x.scene.platform.dal.dataobject.asset.AssetResourceDO;
import cn.cheers.x.scene.platform.enums.SceneAssetCategoryCodes;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 管理后台 - 资产资源保存请求 VO。
 * <p>
 * 仅承载模型库元数据（编码、类型、源文件 URL、格式、预览、引擎配置、扩展 JSON），
 * <strong>不得</strong>包含经度、纬度或场景位姿字段；坐标权威在场景编排层（Transform）。
 * {@code assetType} 为分类种类 {@link SceneAssetCategoryCodes#CATEGORY_TYPE_CODE} 的节点 code。
 */
@Schema(description = "管理后台 - 资源保存请求 VO")
@Data
public class AssetResourceSaveReqVO {

    @Schema(description = "资源编码", example = "MDL-BUILDING-01")
    private String assetCode;

    @Schema(description = "资源名称", example = "主控楼")
    private String assetName;

    @Schema(
            description = "资源类型（scene_asset 分类节点 code）",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "BUILDING"
    )
    @NotBlank(message = "资源类型不能为空")
    private String assetType;

    @Schema(description = "资源 URL", example = "https://asset.example.com/models/building.fbx")
    private String assetUrl;

    @Schema(description = "源文件格式", example = "fbx")
    private String format;

    @Schema(description = "预览图 URL", example = "https://asset.example.com/previews/building.png")
    private String previewUrl;

    @Schema(description = "引擎配置标识", example = "three-gltf")
    private String engineProfile;

    @Schema(description = "扩展元数据 JSON")
    private String metadataJson;

    public AssetResourceDO toDO() {
        AssetResourceDO item = new AssetResourceDO();
        item.setAssetCode(assetCode);
        item.setAssetName(assetName);
        item.setAssetType(assetType);
        item.setAssetUrl(assetUrl);
        item.setFormat(format);
        item.setPreviewUrl(previewUrl);
        item.setEngineProfile(engineProfile);
        item.setMetadataJson(metadataJson);
        return item;
    }
}
