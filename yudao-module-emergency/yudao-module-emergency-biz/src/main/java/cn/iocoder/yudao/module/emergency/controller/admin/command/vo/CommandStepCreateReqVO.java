package cn.iocoder.yudao.module.emergency.controller.admin.command.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * 管理后台 - 指令步骤创建 Request VO
 */
@Schema(description = "管理后台 - 指令步骤创建 Request VO")
@Data
public class CommandStepCreateReqVO {

    @Schema(description = "指令ID。注意：如果ID是很大的整数（超过2^53），前端应传递字符串类型以避免JavaScript精度丢失", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "指令ID不能为空")
    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = cn.iocoder.yudao.module.emergency.framework.jackson.LongDeserializer.class)
    private Long commandId;

    @Schema(description = "步骤内容", requiredMode = Schema.RequiredMode.REQUIRED, example = "立即组织人员撤离现场")
    @NotNull(message = "步骤内容不能为空")
    private String stepContent;

    @Schema(description = "执行时限（分钟）", requiredMode = Schema.RequiredMode.REQUIRED, example = "30")
    @NotNull(message = "执行时限不能为空")
    @Positive(message = "执行时限必须大于0")
    private Integer timeLimitMinutes;

    @Schema(description = "关联预案步骤ID。注意：如果ID是很大的整数（超过2^53），前端应传递字符串类型以避免JavaScript精度丢失", example = "1")
    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = cn.iocoder.yudao.module.emergency.framework.jackson.LongDeserializer.class)
    private Long planStepId;
}

