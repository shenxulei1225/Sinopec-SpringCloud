package cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 指挥页「周边资源」的一行：一条应急资源和它离事件中心的直线距离。
 * 资源挂在哪座站不参与筛选，站内资源只是位置正好在站里。
 */
@Data
public class EmergencyNearbyResourceRespVO {

    @Schema(description = "资源记录编号")
    private Long id;

    @Schema(description = "资源名称")
    private String name;

    @Schema(description = "资源分类名称，例如应急队伍、物资装备。资源没挂分类时为空")
    private String kind;

    @Schema(description = "可用数量。台账没填时为空")
    private Double availableQuantity;

    @Schema(description = "总数量。台账没填时为空")
    private Double totalQuantity;

    @Schema(description = "资源位置经度。台账没填位置时为空")
    private Double longitude;

    @Schema(description = "资源位置纬度")
    private Double latitude;

    @Schema(description = "离事件中心的直线距离（公里）。资源没有位置时为空")
    private Double distanceKm;

    @Schema(description = "存放点。场站或仓库。看不出放在哪时为空")
    private String place;

    @Schema(description = "这份预案写了需要的资源种类，并且这一条对得上")
    private Boolean needed;
}
