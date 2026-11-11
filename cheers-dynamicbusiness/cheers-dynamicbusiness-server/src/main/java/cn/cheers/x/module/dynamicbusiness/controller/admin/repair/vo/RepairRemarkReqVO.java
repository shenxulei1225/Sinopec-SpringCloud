package cn.cheers.x.module.dynamicbusiness.controller.admin.repair.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "报修受理动作")
@Data
public class RepairRemarkReqVO {

    @NotNull(message = "报修单不能为空")
    private Long id;

    @Schema(description = "驳回或补充说明")
    private String remark;
}
