package cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmergencyCommandFromAlertReqVO {

    @Schema(description = "告警编号")
    @NotBlank(message = "告警编号不能为空")
    private String alertId;

    @Schema(description = "事件名称")
    @NotBlank(message = "事件名称不能为空")
    private String name;

    @Schema(description = "当前场站编号。写入这起事件的所属场站")
    private String facilityId;
}
