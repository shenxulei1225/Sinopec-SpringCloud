package cn.iocoder.yudao.module.emergency.controller.admin.event.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 位置信息")
@Data
public class LocationVO {

    @Schema(description = "经度", example = "114.30")
    private Double longitude;

    @Schema(description = "纬度", example = "30.50")
    private Double latitude;

    @Schema(description = "地址")
    private String address;

    @Schema(description = "建筑/楼层/房间等补充信息")
    private String building;
    private String floor;
    private String room;
}










