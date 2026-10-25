package cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 预案上已经写下的一级启动规则。指挥只读，不在这里改预案。
 */
@Data
public class EmergencyCommandStartRuleRespVO {

    @Schema(description = "级别，与预案步骤的 methodKey 相同")
    private String level;

    @Schema(description = "原文点名的启动机构")
    private String starterOrg;

    @Schema(description = "原文中的启动条件")
    private String condition;
}
