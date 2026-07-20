package cn.iocoder.yudao.module.emergency.controller.admin.plan.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * 预案步骤复制 Request VO
 * <p>
 * 用于复制预案步骤及其整个子树，支持复制到同一预案的其他位置或不同预案。
 * 支持调整计划启动时间（相对于新父节点），保持步骤的层级结构。
 *
 * @author 芋道源码
 */
@Schema(description = "管理后台 - 预案步骤复制 Request VO")
@Data
public class PlanStepCopyReqVO {

    @Schema(description = "源步骤ID - 要复制的原始步骤标识，必填。注意：如果ID是很大的整数（超过2^53），前端应传递字符串类型以避免JavaScript精度丢失", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "源步骤ID不能为空")
    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = cn.iocoder.yudao.module.emergency.framework.jackson.LongDeserializer.class)
    private Long sourceStepId;

    @Schema(description = "目标预案ID - 步骤要复制到的目标预案标识，必填。注意：如果ID是很大的整数（超过2^53），前端应传递字符串类型以避免JavaScript精度丢失", requiredMode = Schema.RequiredMode.REQUIRED, example = "2048")
    @NotNull(message = "目标预案ID不能为空")
    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = cn.iocoder.yudao.module.emergency.framework.jackson.LongDeserializer.class)
    private Long targetPlanId;

    @Schema(description = "目标预案级别 - 复制到目标预案的哪个级别，可选，不填则保持原有级别", example = "II")
    private String targetPlanLevel;

    @Schema(description = "目标父步骤ID - 复制后的步骤在目标预案中的父步骤，不填则复制为根步骤。注意：如果ID是很大的整数（超过2^53），前端应传递字符串类型以避免JavaScript精度丢失", example = "2048")
    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = cn.iocoder.yudao.module.emergency.framework.jackson.LongDeserializer.class)
    private Long targetParentId;

    @Schema(description = "时间偏移（分钟）- 相对于新父节点的计划启动时间偏移，正数表示延后，负数表示提前，不填则保持原有时间", example = "30")
    private Integer timeOffsetMinutes;
}

