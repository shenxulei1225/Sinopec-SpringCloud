package cn.cheers.x.scene.platform.controller.admin.component.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - Static Mesh 组件响应 VO")
@Data
public class StaticMeshComponentRespVO {

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "组件编码")
    private String componentCode;

    @Schema(description = "显示名称")
    private String displayName;

    @Schema(description = "网格编码")
    private String meshCode;

    @Schema(description = "材质 JSON")
    private String materialsJson;

    @Schema(description = "投影阴影")
    private Boolean castShadow;

    @Schema(description = "接收阴影")
    private Boolean receiveShadow;

    @Schema(description = "生成重叠事件")
    private Boolean generateOverlapEvents;

    @Schema(description = "移动方式")
    private String mobility;

    @Schema(description = "元数据 JSON")
    private String metadataJson;
}
