package cn.cheers.x.workorder.controller.admin.vo.order;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 班长再派 / 指定执行人
 */
@Schema(description = "管理后台 - 工单指定执行人 Request VO")
@Data
public class WorkOrderAssignReqVO {

    @Schema(description = "执行人用户 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "执行人不能为空")
    private Long assigneeUserId;

}
