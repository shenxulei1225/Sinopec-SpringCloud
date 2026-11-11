package cn.cheers.x.module.dynamicbusiness.controller.admin.repair.vo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RepairWorkOrderReqVO {

    @NotNull(message = "报修单不能为空")
    private Long repairId;

    @NotNull(message = "工单不能为空")
    private Long workOrderId;
}
