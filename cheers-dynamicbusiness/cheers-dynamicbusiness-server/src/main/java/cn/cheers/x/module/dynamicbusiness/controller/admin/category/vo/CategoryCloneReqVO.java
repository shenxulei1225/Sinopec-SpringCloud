package cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 分类复制请求：默认复制为源节点同级（同一父），不复制子树。
 */
@Schema(description = "管理后台 - 分类复制请求")
@Data
public class CategoryCloneReqVO {

    @Schema(description = "源分类 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "12")
    @NotNull(message = "源分类不能为空")
    private Long sourceCategoryId;

    @Schema(description = "新分类名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "电气设备 副本")
    @NotBlank(message = "分类名称不能为空")
    @Size(max = 100, message = "分类名称长度不能超过100")
    private String name;

    @Schema(description = "描述；不传则沿用源分类")
    @Size(max = 500, message = "描述长度不能超过500")
    private String description;
}
