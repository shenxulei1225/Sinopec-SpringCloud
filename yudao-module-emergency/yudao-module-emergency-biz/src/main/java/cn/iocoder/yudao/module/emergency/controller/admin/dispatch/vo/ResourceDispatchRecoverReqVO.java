package cn.iocoder.yudao.module.emergency.controller.admin.dispatch.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResourceDispatchRecoverReqVO {

    @Schema(description = "回收原因")
    @NotBlank
    private String reason;
}

















