package cn.cheers.x.scene.platform.controller.admin.component.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - Light 组件响应 VO")
@Data
public class LightComponentRespVO {

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "组件编码")
    private String componentCode;

    @Schema(description = "显示名称")
    private String displayName;

    @Schema(description = "强度")
    private Float intensity;

    @Schema(description = "光颜色")
    private String lightColor;

    @Schema(description = "投影阴影")
    private Boolean castShadows;

    @Schema(description = "移动方式")
    private String mobility;

    @Schema(description = "元数据 JSON")
    private String metadataJson;
}
