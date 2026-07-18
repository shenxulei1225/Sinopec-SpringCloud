package cn.cheers.x.module.dynamicbusiness.controller.admin.capability.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 组件能力投影响应。
 */
@Schema(description = "管理后台 - 组件能力投影响应")
@Data
public class CapabilityComponentProjectionRespVO {

    @Schema(description = "业务类型编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "equipment")
    private String entityTypeCode;

    @Schema(description = "组件维度编码（list/tree/table/card）", requiredMode = Schema.RequiredMode.REQUIRED, example = "list")
    private String componentCode;

    @Schema(description = "数据种类：model / entity", requiredMode = Schema.RequiredMode.REQUIRED, example = "entity")
    private String dataKind;

    @Schema(description = "组件投影契约 JSON 字符串", requiredMode = Schema.RequiredMode.REQUIRED)
    private String componentInterface;

    @Schema(description = "投影版本号", example = "8")
    private Long version;
}
