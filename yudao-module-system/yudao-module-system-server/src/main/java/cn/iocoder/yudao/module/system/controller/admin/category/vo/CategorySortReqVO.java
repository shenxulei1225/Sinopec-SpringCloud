package cn.iocoder.yudao.module.system.controller.admin.category.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - 分类排序 Request VO")
@Data
public class CategorySortReqVO {

    @Schema(description = "父分类ID，null 表示根分类", example = "1")
    private Long parentId;

    @Schema(description = "排序后的分类ID列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "分类编号列表不能为空")
    private List<Long> categoryIds;

    @Schema(description = "分类类型编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "equipment")
    @NotBlank(message = "分类类型编码不能为空")
    private String categoryTypeCode;
}
