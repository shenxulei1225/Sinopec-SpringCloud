package cn.cheers.x.workorder.controller.admin.vo.order;

import cn.cheers.x.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 管理后台 - 工单分页查询 Request VO
 */
@Schema(description = "管理后台 - 工单分页查询 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class WorkOrderPageReqVO extends PageParam {

    @Schema(description = "业务域范围", example = "inspection")
    private String scope;

    @Schema(description = "工单状态", example = "DISPATCHED")
    private String status;

    @Schema(description = "工单编号", example = "WO-20260719-0001")
    private String woNo;

    @Schema(description = "工单标题", example = "离心泵")
    private String title;

}
