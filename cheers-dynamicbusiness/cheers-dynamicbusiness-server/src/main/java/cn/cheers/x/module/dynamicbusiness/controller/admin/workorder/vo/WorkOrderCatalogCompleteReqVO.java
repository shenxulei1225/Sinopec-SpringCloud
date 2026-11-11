package cn.cheers.x.module.dynamicbusiness.controller.admin.workorder.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 完工时写下维修型号要求的原因和措施。
 */
@Data
public class WorkOrderCatalogCompleteReqVO {

    @NotNull(message = "工单无效")
    private Long id;

    @Schema(description = "原因。维修型号完工必填")
    private String cause;

    @Schema(description = "措施。维修型号完工必填")
    private String remedy;

    @Schema(description = "工时")
    private String laborHours;

    @Schema(description = "备件实体编号")
    private Long sparePartId;

    @Schema(description = "复核工单的结果。属实、误报或重复。维修工单不填")
    private String reviewResult;
}
