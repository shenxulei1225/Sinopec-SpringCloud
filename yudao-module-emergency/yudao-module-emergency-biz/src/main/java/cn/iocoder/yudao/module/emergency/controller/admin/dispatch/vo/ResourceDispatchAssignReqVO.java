package cn.iocoder.yudao.module.emergency.controller.admin.dispatch.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ResourceDispatchAssignReqVO {

    @Schema(description = "事件 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Long eventId;

    @Schema(description = "资源 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Long resourceId;

    @Schema(description = "响应 ID（预警阶段可空）")
    private Long responseId;

    @Schema(description = "调度阶段（预警/响应）")
    private String stage;

    @Schema(description = "可选占窗起点（ISO-8601）；有窗则须真实排程，本 MVP 会显式失败")
    private String windowStart;

    @Schema(description = "可选占窗终点")
    private String windowEnd;
}
