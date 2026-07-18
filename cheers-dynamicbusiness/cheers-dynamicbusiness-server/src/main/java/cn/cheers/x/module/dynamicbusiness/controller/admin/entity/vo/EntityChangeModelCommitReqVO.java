package cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Schema(description = "实体变更模型 - 提交 Request VO")
@Data
public class EntityChangeModelCommitReqVO {

    @Schema(description = "业务类型编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "facility")
    @NotBlank(message = "entityTypeCode 不能为空")
    private String entityTypeCode;

    @Schema(description = "实体 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "35")
    @NotNull(message = "entityId 不能为空")
    private Long entityId;

    @Schema(description = "目标模型 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "28")
    @NotNull(message = "targetModelId 不能为空")
    private Long targetModelId;

    @Schema(description = "目标模型必填但源实体无值的字段补填，key 为 fieldCode")
    private Map<String, Object> patchFields;

    @Schema(description = "是否确认归档源模型专有字段（默认 true）")
    private Boolean confirmArchive;
}
