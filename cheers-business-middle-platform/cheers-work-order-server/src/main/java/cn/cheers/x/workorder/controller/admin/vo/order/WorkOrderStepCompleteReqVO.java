package cn.cheers.x.workorder.controller.admin.vo.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 管理后台 - 完成工单步骤 Request VO
 */
@Schema(description = "管理后台 - 完成工单步骤 Request VO")
@Data
public class WorkOrderStepCompleteReqVO {

    @Schema(description = "步骤结果 JSON")
    private String resultJson;

    @Schema(description = "附件 ID 列表 JSON")
    private String attachmentIds;

}
