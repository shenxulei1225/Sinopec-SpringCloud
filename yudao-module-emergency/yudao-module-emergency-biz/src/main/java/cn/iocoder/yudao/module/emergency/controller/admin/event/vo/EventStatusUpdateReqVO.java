package cn.iocoder.yudao.module.emergency.controller.admin.event.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "管理后台 - 应急事件状态更新请求")
@Data
public class EventStatusUpdateReqVO {

    @Schema(description = "目标状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "processing")
    @NotBlank(message = "目标状态不能为空")
    private String targetStatus;

    @Schema(description = "操作原因", example = "所有待处理任务已完成，自动进入处理中")
    private String reason;
}

