package cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;

@Data
public class EmergencyCommandFromAlertReqVO {

    @Schema(description = "告警编号")
    @NotBlank(message = "告警编号不能为空")
    private String alertId;

    @Schema(description = "事件名称")
    @NotBlank(message = "事件名称不能为空")
    private String name;

    @Schema(description = "告警记录上读不到时补上的归属。键必须是应急事件目录已经声明的字段编码，没声明的不写入")
    private Map<String, String> ownership;
}
