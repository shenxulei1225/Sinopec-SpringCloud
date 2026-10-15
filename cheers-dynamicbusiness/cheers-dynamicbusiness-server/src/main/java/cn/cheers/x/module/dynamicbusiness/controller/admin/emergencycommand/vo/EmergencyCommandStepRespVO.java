package cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class EmergencyCommandStepRespVO {

    @Schema(description = "步骤编号")
    private String nodeKey;

    @Schema(description = "步骤说明")
    private String description;

    @Schema(description = "未完成或已完成")
    private String runStatus;
}
