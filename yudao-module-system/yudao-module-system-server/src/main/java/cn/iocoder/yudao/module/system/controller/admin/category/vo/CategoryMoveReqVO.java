package cn.iocoder.yudao.module.system.controller.admin.category.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "管理后台 - 分类移动 Request VO")
@Data
public class CategoryMoveReqVO {

    @Schema(description = "目标父分类ID，null 表示移动到根分类", example = "2")
    private Long targetParentId;

    @Schema(description = "分类类型编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "equipment")
    @NotBlank(message = "分类类型编码不能为空")
    private String categoryTypeCode;
}
