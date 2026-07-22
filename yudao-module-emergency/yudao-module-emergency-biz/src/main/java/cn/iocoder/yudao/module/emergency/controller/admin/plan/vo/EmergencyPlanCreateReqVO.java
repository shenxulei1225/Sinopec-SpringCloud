package cn.iocoder.yudao.module.emergency.controller.admin.plan.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Schema(description = "管理后台 - 应急预案创建 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class EmergencyPlanCreateReqVO extends EmergencyPlanBaseVO {

    @Schema(description = "预案步骤列表", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<EmergencyPlanStepBaseVO> steps;

    @Schema(description = "预案附件列表")
    private List<EmergencyPlanAttachmentBaseVO> attachments;

}

















