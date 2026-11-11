package cn.cheers.x.module.dynamicbusiness.controller.admin.workorder.vo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 开工或认领时只需要这条工单的编号。
 */
@Data
public class WorkOrderCatalogIdReqVO {

    @NotNull(message = "工单无效")
    private Long id;
}
