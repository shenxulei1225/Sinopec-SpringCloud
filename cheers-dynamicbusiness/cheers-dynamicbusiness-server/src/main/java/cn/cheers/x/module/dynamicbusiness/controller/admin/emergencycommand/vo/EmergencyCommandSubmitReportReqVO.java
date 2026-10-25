package cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;

@Data
public class EmergencyCommandSubmitReportReqVO {

    @Schema(description = "应急事件编号")
    @NotBlank(message = "事件编号不能为空")
    private String eventId;

    @Schema(description = "当前场站编号")
    @NotBlank(message = "请先选择场站")
    private String facilityId;

    @Schema(description = "步骤所属级别。可以是当前级别，也可以是已经离开的级别")
    @NotBlank(message = "请选择级别")
    private String level;

    @Schema(description = "预案上的步骤编号")
    @NotBlank(message = "步骤编号不能为空")
    private String nodeKey;

    @Schema(description = "这次填进信息表的值。键是表单栏目标识")
    private Map<String, Object> formData;
}
