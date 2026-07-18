package cn.iocoder.yudao.module.scene.platform.controller.admin.component.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - Camera 组件响应 VO")
@Data
public class CameraComponentRespVO {

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "组件编码")
    private String componentCode;

    @Schema(description = "显示名称")
    private String displayName;

    @Schema(description = "视野角")
    private Float fieldOfView;

    @Schema(description = "宽高比")
    private Float aspectRatio;

    @Schema(description = "近裁剪面")
    private Float nearClipPlane;

    @Schema(description = "远裁剪面")
    private Float farClipPlane;

    @Schema(description = "约束宽高比")
    private Boolean constrainAspectRatio;

    @Schema(description = "自动激活")
    private Boolean autoActivate;

    @Schema(description = "元数据 JSON")
    private String metadataJson;
}
