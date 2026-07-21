package cn.cheers.x.inspection.inspection_content.controller.admin.vo.binding;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "对象↔停靠点绑定全量替换请求")
public class ObjectStationBindingReplaceReqVO {

    @Schema(description = "设施 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "facilityId 不能为空")
    private Long facilityId;

    @Schema(description = "对象 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "objectId 不能为空")
    private Long objectId;

    @Schema(description = "停靠点节点 ID 列表（按顺序）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "stationNodeIds 不能为空")
    private List<@NotEmpty(message = "stationNodeId 不能为空") String> stationNodeIds;

    @Schema(description = "每停靠点默认作业时长（分钟）")
    private Integer workMinutesPerStop;
}
