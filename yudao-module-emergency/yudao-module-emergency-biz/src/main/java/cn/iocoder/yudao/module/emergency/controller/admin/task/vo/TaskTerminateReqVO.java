package cn.iocoder.yudao.module.emergency.controller.admin.task.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TaskTerminateReqVO {

    @Schema(description = "终止原因")
    @NotBlank
    private String reason;
}

















