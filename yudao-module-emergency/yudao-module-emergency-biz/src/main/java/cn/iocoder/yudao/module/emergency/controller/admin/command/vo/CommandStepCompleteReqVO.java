package cn.iocoder.yudao.module.emergency.controller.admin.command.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 管理后台 - 指令步骤完成 Request VO
 */
@Schema(description = "管理后台 - 指令步骤完成 Request VO")
@Data
public class CommandStepCompleteReqVO {

    @Schema(description = "超时原因（超时步骤必填）", example = "现场情况复杂，需要更多时间")
    private String timeoutReason;

    @Schema(description = "处理措施（超时步骤必填）", example = "已增派人员，预计30分钟内完成")
    private String handlingMeasures;
}

