package cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EmergencyCommandAssessReqVO {

    @Schema(description = "应急事件编号")
    @NotBlank(message = "事件编号不能为空")
    private String eventId;

    @Schema(description = "是否成立")
    @NotNull(message = "请选择是否成立")
    private Boolean real;

    @Schema(description = "事件分类。成立时必填，表示这次发生了什么")
    private String eventType;

    @Schema(description = "响应级别。成立时必填，取值与所选预案该级的 methodKey 相同")
    private String responseLevel;

    @Schema(description = "研判意见")
    private String comment;

    @Schema(description = "成立时选定的预案编号。写入事件，供预警和响应读取。不成立时不填")
    private String planId;
}
