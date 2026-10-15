package cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 预案上用户定义的一个阶段。名称给脉络显示，键用来对上上报、研判、预警和响应步骤。
 */
@Data
public class EmergencyCommandStageRespVO {

    @Schema(description = "阶段键。report、assess、warning、response")
    private String stageKey;

    @Schema(description = "用户定义的阶段名称")
    private String name;
}
