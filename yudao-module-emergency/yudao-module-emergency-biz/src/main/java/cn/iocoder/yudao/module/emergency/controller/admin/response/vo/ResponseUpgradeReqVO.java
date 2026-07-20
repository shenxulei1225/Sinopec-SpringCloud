package cn.iocoder.yudao.module.emergency.controller.admin.response.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "管理后台 - 升级/调整响应级别请求")
@Data
public class ResponseUpgradeReqVO {

    @Schema(description = "新的响应级别 I~V", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String newResponseLevel;

    @Schema(description = "新的预案ID")
    private Long newPlanId;

    @Schema(description = "调整原因")
    private String reason;
}

















