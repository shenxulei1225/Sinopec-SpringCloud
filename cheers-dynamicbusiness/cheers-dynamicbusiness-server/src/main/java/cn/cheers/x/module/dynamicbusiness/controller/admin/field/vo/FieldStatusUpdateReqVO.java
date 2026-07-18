package cn.cheers.x.module.dynamicbusiness.controller.admin.field.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - 字段更新状态 Request VO")
@Data
public class FieldStatusUpdateReqVO {

    @Schema(description = "字段编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "字段编号不能为空")
    private Long id;

    @Schema(description = "状态（0-禁用，1-启用）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "状态不能为空")
    private Integer status;

}












































