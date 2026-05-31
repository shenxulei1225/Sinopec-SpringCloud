package cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class CategoryEnableDisableReqVO {

    @Schema(description = "分类编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "分类编号不能为空")
    private Long id;

    @Schema(description = "分类类型编码（维度）", requiredMode = Schema.RequiredMode.REQUIRED, example = "region")
    @NotBlank(message = "分类类型编码不能为空")
    private String categoryTypeCode;
}

