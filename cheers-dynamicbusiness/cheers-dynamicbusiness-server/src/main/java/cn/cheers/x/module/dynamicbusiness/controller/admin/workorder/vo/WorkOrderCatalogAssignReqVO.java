package cn.cheers.x.module.dynamicbusiness.controller.admin.workorder.vo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 把这条工单指定给本班组的一名成员。
 */
@Data
public class WorkOrderCatalogAssignReqVO {

    @NotNull(message = "工单无效")
    private Long id;

    @NotNull(message = "请选择执行人")
    private Long assigneeUserId;
}
