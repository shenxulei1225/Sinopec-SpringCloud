package cn.iocoder.yudao.module.scene.platform.controller.admin.component.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 音频组件响应 VO")
@Data
public class AudioComponentRespVO {

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "组件编码")
    private String componentCode;

    @Schema(description = "显示名称")
    private String displayName;

    @Schema(description = "音效编码")
    private String soundCode;

    @Schema(description = "自动激活")
    private Boolean autoActivate;

    @Schema(description = "允许空间化")
    private Boolean allowSpatialisation;

    @Schema(description = "停止时销毁")
    private Boolean stopWhenOwnerDestroyed;

    @Schema(description = "音量倍数")
    private Float volumeMultiplier;

    @Schema(description = "音高倍数")
    private Float pitchMultiplier;

    @Schema(description = "覆盖衰减")
    private Boolean overrideAttenuation;

    @Schema(description = "衰减 JSON")
    private String attenuationJson;

    @Schema(description = "并发控制 JSON")
    private String concurrencyJson;

    @Schema(description = "元数据 JSON")
    private String metadataJson;
}
