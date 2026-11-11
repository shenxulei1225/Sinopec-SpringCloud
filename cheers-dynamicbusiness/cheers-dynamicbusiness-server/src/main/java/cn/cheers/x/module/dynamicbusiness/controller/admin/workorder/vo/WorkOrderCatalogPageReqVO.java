package cn.cheers.x.module.dynamicbusiness.controller.admin.workorder.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 工单中心按业务域和当前场站列出工单目录里的实体。
 */
@Data
public class WorkOrderCatalogPageReqVO {

    @NotBlank(message = "请选择业务域")
    private String domain;

    @NotNull(message = "请先选择场站")
    private Long facilityId;

    private Integer pageNo;

    private Integer pageSize;

    @Schema(description = "只看还没有执行人的单")
    private Boolean unassignedOnly;

    @Schema(description = "执行人页：派给当前登录人的单，以及派到他所在班组、还没有执行人的单")
    private Boolean forExecutor;

    @Schema(description = "只看计划开始和计划结束都有的单")
    private Boolean scheduledOnly;
}
