package cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 改这一条应急事件的名称。不改预案，不改已经记下的脉络。
 */
@Data
public class EmergencyCommandRenameReqVO {

    @Schema(description = "事件编号")
    @NotBlank(message = "事件编号不能为空")
    private String eventId;

    @Schema(description = "事件名称")
    @NotBlank(message = "事件名称不能为空")
    @Size(max = 100, message = "事件名称不能超过100个字")
    private String name;
}
