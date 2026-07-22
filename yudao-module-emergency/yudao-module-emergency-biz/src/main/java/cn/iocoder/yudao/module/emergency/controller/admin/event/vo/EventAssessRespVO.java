package cn.iocoder.yudao.module.emergency.controller.admin.event.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 应急事件研判结果响应")
@Data
public class EventAssessRespVO {

    @Schema(description = "推荐响应级别 I~V")
    private String responseLevel;

    @Schema(description = "推荐预案 ID")
    private Long recommendedPlanId;

    @Schema(description = "推荐预案级别 I~V")
    private String recommendedPlanLevel;
}










