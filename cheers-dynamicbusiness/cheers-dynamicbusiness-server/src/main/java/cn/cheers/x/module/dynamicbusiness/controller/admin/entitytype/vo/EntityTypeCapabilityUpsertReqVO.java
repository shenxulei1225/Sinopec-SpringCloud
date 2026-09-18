package cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - 数据目录能力开关保存 Request VO")
@Data
public class EntityTypeCapabilityUpsertReqVO {

    @Schema(description = "数据目录编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "sop")
    @NotBlank(message = "数据目录编码不能为空")
    private String entityTypeCode;

    @Schema(description = "启用能力编码列表（覆盖保存）")
    private List<String> enabledCapabilityCodes;

    @Schema(description = "步骤树允许挂哪些数据目录（启用步骤树时写入）")
    private List<String> stepTreeHangableTypeCodes;

    @Schema(description = "步骤树是否允许同一棵树混挂")
    private Boolean stepTreeAllowMixed;
}
