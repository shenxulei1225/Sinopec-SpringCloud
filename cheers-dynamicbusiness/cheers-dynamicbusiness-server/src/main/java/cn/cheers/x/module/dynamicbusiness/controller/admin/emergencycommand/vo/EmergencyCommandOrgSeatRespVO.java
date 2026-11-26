package cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 指挥班子里的一个席位。人是启动当时的任职，不是现在再查的。
 */
@Data
public class EmergencyCommandOrgSeatRespVO {

    @Schema(description = "席位，例如组长、副组长、成员")
    private String role;

    @Schema(description = "事发单位或公司")
    private String scope;

    @Schema(description = "职务名称")
    private String postName;

    @Schema(description = "启动时在任的人。当时没有人则为空")
    private String personName;
}
