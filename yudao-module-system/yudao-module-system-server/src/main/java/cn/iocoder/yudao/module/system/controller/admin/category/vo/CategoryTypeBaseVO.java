package cn.iocoder.yudao.module.system.controller.admin.category.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CategoryTypeBaseVO {

    @Schema(description = "分类类型编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "project_phase")
    @NotEmpty(message = "分类类型编码不能为空")
    private String categoryTypeCode;

    @Schema(description = "分类类型名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "项目阶段")
    @NotEmpty(message = "分类类型名称不能为空")
    private String name;

    @Schema(description = "分类类型描述", example = "用于管理项目执行的不同阶段")
    private String description;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer status;
}
