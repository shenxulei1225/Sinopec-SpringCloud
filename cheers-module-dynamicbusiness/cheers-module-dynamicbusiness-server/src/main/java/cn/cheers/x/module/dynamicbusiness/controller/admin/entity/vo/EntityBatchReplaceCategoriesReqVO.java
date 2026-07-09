package cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 批量覆盖实体分类集合请求 VO。
 */
@Schema(description = "管理后台 - 批量覆盖实体分类集合请求")
@Data
public class EntityBatchReplaceCategoriesReqVO {

    @Schema(description = "业务类型编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "equipment")
    @NotBlank(message = "业务类型编码不能为空")
    private String entityTypeCode;

    @Schema(description = "实体ID列表", requiredMode = Schema.RequiredMode.REQUIRED, example = "[1,2,3]")
    @NotEmpty(message = "实体ID列表不能为空")
    @Size(min = 1, max = 1000, message = "单次最多支持1000条")
    private List<Long> entityIds;

    @Schema(description = "目标分类ID列表", requiredMode = Schema.RequiredMode.REQUIRED, example = "[10,11]")
    @NotEmpty(message = "目标分类ID列表不能为空")
    private List<Long> categoryIds;
}
