package cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmergencyCommandStartReqVO {

    @Schema(description = "应急事件编号")
    @NotBlank(message = "事件编号不能为空")
    private String eventId;

    @Schema(description = "当前场站编号。响应写在该场站下")
    @NotBlank(message = "请先选择场站")
    private String facilityId;

    @Schema(description = "研判意见")
    private String comment;

    @Schema(description = "选定的预案编号")
    private String planId;

    @Schema(description = "响应级别。确认启动时以这里选中的级别为准")
    private String responseLevel;

    @Schema(description = "事件类型。待研判时必填")
    private String eventType;
}
