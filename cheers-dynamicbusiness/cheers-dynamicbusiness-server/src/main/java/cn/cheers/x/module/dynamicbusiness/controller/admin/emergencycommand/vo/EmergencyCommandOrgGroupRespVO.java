package cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 这一起事件上已经写下的一组指挥班子。
 */
@Data
public class EmergencyCommandOrgGroupRespVO {

    @Schema(description = "组名，例如现场应急指挥部、现场工作组")
    private String name;

    @Schema(description = "这一组的职责")
    private String duties;

    @Schema(description = "席位和启动时写下的人")
    private List<EmergencyCommandOrgSeatRespVO> seats;
}
