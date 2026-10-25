package cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 研判确认。待研判时第一次写入预案和级别。
 * 已经确认过的，只在弹框确认后改预案或级别。
 * 响应已经开始且级别变了，才追加一条级别变化。
 */
@Data
public class EmergencyCommandJudgementReqVO {

    @Schema(description = "应急事件编号")
    @NotBlank(message = "事件编号不能为空")
    private String eventId;

    @Schema(description = "当前场站编号")
    @NotBlank(message = "请先选择场站")
    private String facilityId;

    @Schema(description = "选定的预案编号")
    @NotBlank(message = "请选择预案")
    private String planId;

    @Schema(description = "修改弹窗里的阶段。warning 为预警阶段，response 为响应阶段。待研判第一次确认可不传")
    private String stage;

    @Schema(description = "响应等级。进入响应阶段时才填")
    private String responseLevel;
}
