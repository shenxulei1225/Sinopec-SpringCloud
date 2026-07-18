package cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 业务实体批量移动请求 VO
 * 
 * 支持批量将多个实体移动到目标分类
 * 单次操作最多1000条，超过时自动分批异步执行
 * 
 * @author yudao
 */
@Schema(description = "管理后台 - 业务实体批量移动请求")
@Data
public class EntityBatchMoveReqVO {

    @Schema(description = "业务类型编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "equipment")
    @NotBlank(message = "业务类型编码不能为空")
    private String entityTypeCode;

    @Schema(description = "实体ID列表", requiredMode = Schema.RequiredMode.REQUIRED, example = "[1, 2, 3]")
    @NotEmpty(message = "实体ID列表不能为空")
    @Size(min = 1, max = 1000, message = "单次批量移动最多支持1000条记录")
    private List<Long> ids;

    @Schema(description = "目标分类ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    @NotNull(message = "目标分类ID不能为空")
    private Long targetCategoryId;

    @Schema(description = "是否替换现有分类关联（true=替换，false=追加）", example = "false")
    private Boolean replaceExisting;

    @Schema(description = "是否异步执行（当数据量大于100时建议使用异步）", example = "false")
    private Boolean async;
}
