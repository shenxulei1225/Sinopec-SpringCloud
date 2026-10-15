package cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class EmergencyCommandPlanRespVO {

    @Schema(description = "预案编号")
    private String id;

    @Schema(description = "预案名称")
    private String name;
}
