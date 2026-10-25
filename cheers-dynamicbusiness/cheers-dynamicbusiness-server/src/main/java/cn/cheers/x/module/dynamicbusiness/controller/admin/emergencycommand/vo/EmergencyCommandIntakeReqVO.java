package cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 人工接报。不带告警编号。
 * 写入名称、场站、发生时间和接报经过。事件进入待研判。
 */
@Data
public class EmergencyCommandIntakeReqVO {

    @Schema(description = "事件名称")
    @NotBlank(message = "事件名称不能为空")
    private String name;

    @Schema(description = "当前场站编号")
    @NotBlank(message = "请先选择场站")
    private String facilityId;

    @Schema(description = "发生时间。没填则记接报这一刻")
    private String occurTime;

    @Schema(description = "接报经过")
    private String intakeNote;
}
