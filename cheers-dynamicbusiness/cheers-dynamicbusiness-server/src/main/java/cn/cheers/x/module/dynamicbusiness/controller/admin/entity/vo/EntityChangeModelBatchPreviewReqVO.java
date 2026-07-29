package cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Schema(description = "实体变更模型 - 批量预览 Request VO")
@Data
public class EntityChangeModelBatchPreviewReqVO {

    @Schema(description = "业务类型编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "equipment")
    @NotBlank(message = "entityTypeCode 不能为空")
    private String entityTypeCode;

    @Schema(description = "实体 ID 列表（勾选多选，非当前高亮行）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "entityIds 不能为空")
    private List<Long> entityIds;

    @Schema(description = "目标模型 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "28")
    @NotNull(message = "targetModelId 不能为空")
    private Long targetModelId;
}
