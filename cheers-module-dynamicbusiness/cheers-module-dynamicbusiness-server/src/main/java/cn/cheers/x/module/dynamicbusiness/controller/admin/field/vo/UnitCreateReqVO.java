package cn.cheers.x.module.dynamicbusiness.controller.admin.field.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class UnitCreateReqVO {

    @Schema(description = "显示顺序", example = "1")
    @NotNull(message = "顺序不能为空")
    private Integer sort = 1;

    @Schema(description = "单位名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "米")
    @NotBlank(message = "单位名称不能为空")
    private String label;

    @Schema(description = "单位编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "m")
    @NotBlank(message = "单位编码不能为空")
    private String value;

    @Schema(description = "单位类型", example = "LENGTH")
    private String unitType;

    @Schema(description = "状态", example = "1")
    private Integer status = 1;

    @Schema(description = "备注", example = "长度单位")
    private String remark;
}

