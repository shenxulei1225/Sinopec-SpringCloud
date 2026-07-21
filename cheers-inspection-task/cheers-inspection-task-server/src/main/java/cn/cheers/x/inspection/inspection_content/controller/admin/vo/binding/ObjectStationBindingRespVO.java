package cn.cheers.x.inspection.inspection_content.controller.admin.vo.binding;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "对象↔停靠点绑定响应")
public class ObjectStationBindingRespVO {

    @Schema(description = "对象 ID")
    private Long objectId;

    @Schema(description = "停靠点节点 ID")
    private String stationNodeId;

    @Schema(description = "作业时长（分钟）")
    private Integer workMinutes;

    @Schema(description = "排序号")
    private Integer sortNo;
}
