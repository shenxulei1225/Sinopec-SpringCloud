package cn.cheers.x.workorder.controller.admin.vo.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 管理后台 - 工单步骤结果 Response VO
 */
@Schema(description = "管理后台 - 工单步骤结果 Response VO")
@Data
public class WorkOrderStepResultRespVO {

    @Schema(description = "主键", example = "1")
    private Long id;

    @Schema(description = "步骤编码", example = "s1")
    private String stepCode;

    @Schema(description = "步骤顺序", example = "1")
    private Integer stepOrder;

    @Schema(description = "是否已完成", example = "false")
    private Boolean completed;

    @Schema(description = "步骤结果 JSON")
    private String resultJson;

    @Schema(description = "附件 ID 列表 JSON")
    private String attachmentIds;

}
