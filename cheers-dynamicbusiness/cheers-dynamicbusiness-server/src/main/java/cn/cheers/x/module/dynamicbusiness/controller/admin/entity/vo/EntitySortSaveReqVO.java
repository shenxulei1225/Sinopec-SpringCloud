package cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "管理后台 - 更新实体排序（保存顺序）")
public class EntitySortSaveReqVO {

    @Schema(description = "业务类型编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "equipment")
    @NotBlank(message = "业务类型编码不能为空")
    private String entityTypeCode;

    @Schema(description = "分类 ID；有值时写入分类—实体关联 sort，无值时写入实体 sort", example = "12")
    private Long categoryId;

    @Schema(description = "按目标顺序提交的实体列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "实体排序列表不能为空")
    @Valid
    private List<Item> items;

    @Data
    public static class Item {

        @Schema(description = "实体ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        @NotNull(message = "实体ID不能为空")
        private Long entityId;

        @Schema(description = "排序位置（从0开始）", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
        @NotNull(message = "排序位置不能为空")
        private Integer index;
    }
}
