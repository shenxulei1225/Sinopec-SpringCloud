package cn.iocoder.yudao.module.emergency.controller.admin.plan.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Schema(description = "管理后台 - 应急预案更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class EmergencyPlanUpdateReqVO extends EmergencyPlanBaseVO {

    @Schema(description = "编号。注意：如果ID是很大的整数（超过2^53），前端应传递字符串类型以避免JavaScript精度丢失", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = cn.iocoder.yudao.module.emergency.framework.jackson.LongDeserializer.class)
    private Long id;

    @Schema(description = "预案步骤列表")
    private List<EmergencyPlanStepUpdateVO> steps;

    @Schema(description = "预案附件列表")
    private List<EmergencyPlanAttachmentBaseVO> attachments;

}

















