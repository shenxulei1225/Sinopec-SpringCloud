package cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 结案只认这一起事件。不改预案。
 */
@Data
public class EmergencyCommandCloseReqVO {

    @Schema(description = "应急事件编号")
    @NotBlank(message = "事件编号不能为空")
    private String eventId;

    @Schema(description = "当前场站编号")
    @NotBlank(message = "请先选择场站")
    private String facilityId;
}
