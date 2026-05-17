package cn.iocoder.yudao.module.system.controller.admin.category.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - 分类拖拽 Request VO")
@Data
public class CategoryDragReqVO {

    public enum Position {
        BEFORE,
        AFTER,
        INNER
    }

    @Schema(description = "拖拽节点ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "拖拽节点ID不能为空")
    private Long id;

    @Schema(description = "目标节点ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    private Long targetId;

    @Schema(description = "目标父节点ID，可选", example = "3")
    private Long targetParentId;

    @Schema(description = "拖拽位置：BEFORE / AFTER / INNER", requiredMode = Schema.RequiredMode.REQUIRED, example = "INNER")
    @NotNull(message = "拖拽位置不能为空")
    private Position position;

    @Schema(description = "分类类型编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "equipment")
    @NotBlank(message = "分类类型编码不能为空")
    private String categoryTypeCode;
}
