package cn.cheers.x.module.dynamicbusiness.controller.admin.repair.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RepairAssigneeReqVO {

    @NotNull(message = "报修单不能为空")
    private Long repairId;

    @NotNull(message = "工单不能为空")
    private Long workOrderId;

    @NotBlank(message = "领走的人不能为空")
    private String personName;

    /** 为真时进度写成「已改派给某人」。接单之后重新指定人员用这一句。 */
    private Boolean reassign;
}
