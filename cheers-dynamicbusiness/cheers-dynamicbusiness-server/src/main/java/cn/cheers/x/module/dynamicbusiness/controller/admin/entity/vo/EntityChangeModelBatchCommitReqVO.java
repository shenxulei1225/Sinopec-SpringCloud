package cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Schema(description = "实体变更模型 - 批量提交 Request VO")
@Data
public class EntityChangeModelBatchCommitReqVO {

    @Schema(description = "业务类型编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "equipment")
    @NotBlank(message = "entityTypeCode 不能为空")
    private String entityTypeCode;

    @Schema(description = "要提交的实体 ID 列表（通常为预览中的可迁 + 已补填）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "entityIds 不能为空")
    private List<Long> entityIds;

    @Schema(description = "目标模型 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "28")
    @NotNull(message = "targetModelId 不能为空")
    private Long targetModelId;

    @Schema(description = "按实体 ID 的补填字段；key 为 entityId 字符串")
    private Map<String, Map<String, Object>> patches;

    @Schema(description = "是否确认归档源模型专有字段（默认 true）")
    private Boolean confirmArchive;
}
