package cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 分类拖拽排序请求
 */
@Data
public class CategoryDragReqVO {

    @Schema(description = "被拖拽的分类ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1001")
    @NotNull(message = "dragId 不能为空")
    private Long dragId;

    @Schema(description = "目标节点ID（BEFORE/AFTER 必填）", example = "1002")
    private Long targetId;

    @Schema(description = "目标父节点ID（INNER 必填；BEFORE/AFTER 可留空自动取目标父级）", example = "10")
    private Long targetParentId;

    @Schema(description = "拖拽位置 BEFORE/AFTER/INNER", requiredMode = Schema.RequiredMode.REQUIRED, example = "BEFORE")
    @NotNull(message = "position 不能为空")
    private Position position;

    @Schema(description = "分类类型编码（维度）", requiredMode = Schema.RequiredMode.REQUIRED, example = "equipment")
    @NotBlank(message = "categoryTypeCode 不能为空")
    private String categoryTypeCode;

    public enum Position {
        BEFORE,
        AFTER,
        INNER
    }
}
