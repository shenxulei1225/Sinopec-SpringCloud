package cn.iocoder.yudao.module.emergency.controller.admin.event.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "管理后台 - 应急事件确认请求")
@Data
public class EventConfirmReqVO {

    @Schema(description = "确认结果 real/false_alarm/ignore", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String result;

    @Schema(description = "确认备注")
    private String comment;

    @Schema(description = "预案ID，仅当 result=real(真实事件) 时必填，用于将预案的预警阶段步骤转为任务", example = "1")
    private Long planId;
}










