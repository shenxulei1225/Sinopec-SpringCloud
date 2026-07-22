package cn.iocoder.yudao.module.emergency.controller.admin.event.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "管理后台 - 应急事件研判请求")
@Data
public class EventAssessReqVO {

    @Schema(description = "建议的响应级别 I~V", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String responseLevel;

    @Schema(description = "建议的预案级别 I~V")
    private String recommendedPlanLevel;

    @Schema(description = "研判意见")
    private String comment;
}










