package cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 给事件补上或改正中心点。告警转入的事件没有位置，要人在这里补上，之后才能看周边资源。
 */
@Data
public class EmergencyCommandLocateReqVO {

    @Schema(description = "事件编号")
    @NotBlank(message = "事件编号不能为空")
    private String eventId;

    @Schema(description = "事件中心点经度")
    @NotNull(message = "请在地图上点出事件位置")
    @DecimalMin(value = "-180", message = "经度不正确")
    @DecimalMax(value = "180", message = "经度不正确")
    private Double longitude;

    @Schema(description = "事件中心点纬度")
    @NotNull(message = "请在地图上点出事件位置")
    @DecimalMin(value = "-90", message = "纬度不正确")
    @DecimalMax(value = "90", message = "纬度不正确")
    private Double latitude;

    @Schema(description = "地点描述。可不填")
    private String locationText;
}
