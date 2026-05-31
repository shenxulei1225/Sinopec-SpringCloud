package cn.cheers.x.module.dynamicbusiness.controller.admin.group.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GroupBaseVO {

    @Schema(description = "分组类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "FIELD")
    @NotBlank(message = "分组类型不能为空")
    private String groupType;

    @Schema(description = "分组名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "长度单位")
    @NotBlank(message = "分组名称不能为空")
    private String name;

    @Schema(description = "分组描述", example = "长度量纲单位")
    private String description;

    @Schema(description = "父分组ID", example = "0")
    private Long parentId;

    @Schema(description = "排序（可选，不传时由后端自动生成）", example = "10")
    private Integer sort;

    @Schema(description = "状态（可选，不传时默认启用）", example = "1")
    private Integer status;
}
