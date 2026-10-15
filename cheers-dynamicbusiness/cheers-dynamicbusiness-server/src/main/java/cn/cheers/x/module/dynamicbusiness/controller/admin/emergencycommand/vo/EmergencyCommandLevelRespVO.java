package cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class EmergencyCommandLevelRespVO {

    @Schema(description = "响应级别，I 到 V")
    private String level;

    @Schema(description = "这一级的步骤树。子步骤带父级编号")
    private List<EmergencyCommandStepNodeRespVO> nodes;
}
