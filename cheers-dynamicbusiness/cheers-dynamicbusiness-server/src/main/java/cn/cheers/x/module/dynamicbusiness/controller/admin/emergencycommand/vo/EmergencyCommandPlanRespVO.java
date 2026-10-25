package cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class EmergencyCommandPlanRespVO {

    @Schema(description = "预案编号")
    private String id;

    @Schema(description = "预案名称")
    private String name;

    @Schema(description = "这份预案上的响应级别")
    private List<EmergencyCommandPlanLevelRespVO> levels;

    @Schema(description = "这份预案上已经写下的启动规则。研判选中级别后显示")
    private List<EmergencyCommandStartRuleRespVO> startRules;
}
