package cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 批量实体分类关系操作请求 VO（追加/移除）。
 */
@Schema(description = "管理后台 - 批量实体分类关系操作请求")
@Data
public class EntityBatchCategoryRelationReqVO {

    @Schema(description = "业务类型编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "equipment")
    @NotBlank(message = "业务类型编码不能为空")
    private String businessTypeCode;

    @Schema(description = "实体ID列表", requiredMode = Schema.RequiredMode.REQUIRED, example = "[1,2,3]")
    @NotEmpty(message = "实体ID列表不能为空")
    @Size(min = 1, max = 1000, message = "单次最多支持1000条")
    private List<Long> entityIds;

    @Schema(description = "目标分类ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    @NotNull(message = "目标分类ID不能为空")
    private Long categoryId;
}
