package cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class EmergencyCommandPlanLevelRespVO {

    @Schema(description = "级别编号，与预案步骤的 methodKey 相同")
    private String level;

    @Schema(description = "预案上写下的级别名称")
    private String label;
}
