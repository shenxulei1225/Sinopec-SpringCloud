package cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class CategorySortReqVO {

    @Schema(description = "父分类ID（null 表示根分类）", example = "0")
    private Long parentId;

    @Schema(description = "子分类ID列表（按排序顺序）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "分类ID列表不能为空")
    private List<Long> categoryIds;

    @Schema(description = "分类类型编码（维度）", requiredMode = Schema.RequiredMode.REQUIRED, example = "region")
    @NotBlank(message = "分类类型编码不能为空")
    private String categoryTypeCode;
}

