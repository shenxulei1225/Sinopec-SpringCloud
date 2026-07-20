package cn.iocoder.yudao.module.emergency.controller.admin.plan.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 应急预案附件 Response VO")
@Data
public class EmergencyPlanAttachmentRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "关联预案ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    private Long planId;

    @Schema(description = "附件名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "通讯录")
    private String name;

    @Schema(description = "附件类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "contact_list")
    private String type;

    @Schema(description = "附件内容 (JSONB)")
    private Object content;

}

















