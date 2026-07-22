package cn.iocoder.yudao.module.emergency.controller.admin.response.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - 启动应急响应请求")
@Data
public class ResponseStartReqVO {

    @Schema(description = "事件ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Long eventId;

    @Schema(description = "响应级别 I~V", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String responseLevel;

    @Schema(description = "预案ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Long planId;

    @Schema(description = "启动原因")
    private String reason;

    @Schema(description = "指挥机构（负责人名称）")
    private String commandOrg;
}

















