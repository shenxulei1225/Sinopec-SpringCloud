package cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
public class CategoryMoveReqVO {

    @Schema(description = "目标父分类ID，可为空表示移到根", example = "0")
    private Long targetParentId;

    @Schema(description = "分类类型编码（维度）", requiredMode = Schema.RequiredMode.REQUIRED, example = "region")
    @NotBlank(message = "分类类型编码不能为空")
    private String categoryTypeCode;
}

