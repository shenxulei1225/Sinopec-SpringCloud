package cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmergencyCommandChangeLevelReqVO {

    @Schema(description = "应急事件编号")
    @NotBlank(message = "事件编号不能为空")
    private String eventId;

    @Schema(description = "当前场站编号")
    @NotBlank(message = "请先选择场站")
    private String facilityId;

    @Schema(description = "改后的级别，取值与预案步骤该级的 methodKey 相同")
    @NotBlank(message = "请选择级别")
    private String responseLevel;
}
