package cn.iocoder.yudao.module.emergency.controller.admin.response.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "管理后台 - 取消应急响应请求")
@Data
public class ResponseCancelReqVO {

    @Schema(description = "取消原因", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String reason;
}

















