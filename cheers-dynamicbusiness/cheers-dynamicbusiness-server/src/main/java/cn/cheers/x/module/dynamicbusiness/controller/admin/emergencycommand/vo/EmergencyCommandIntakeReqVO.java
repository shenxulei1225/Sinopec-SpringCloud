package cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 人工接报。不带告警编号。
 * 写入名称、地图上点出的位置、发生时间、照片地址，以及这本目录已经声明的归属。事件进入待研判。
 * 不带告警编号。告警自己生成事件，不从这份表单转入。
 */
@Data
public class EmergencyCommandIntakeReqVO {

    @Schema(description = "事件名称")
    @NotBlank(message = "事件名称不能为空")
    private String name;

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

    @Schema(description = "地点描述，例如某阀室旁、某桩号附近。可不填")
    private String locationText;

    @Schema(description = "归属。键是应急事件目录归属声明里的字段编码，值是指向记录的编号。没声明的键不写入")
    private Map<String, String> ownership;

    @Schema(description = "发生时间，格式 yyyy-MM-dd HH:mm:ss。没填则记接报这一刻")
    private String occurTime;

    @Schema(description = "接报经过")
    private String intakeNote;

    @Schema(description = "已上传照片的地址。最多 9 张")
    private List<String> photoUrls;
}
