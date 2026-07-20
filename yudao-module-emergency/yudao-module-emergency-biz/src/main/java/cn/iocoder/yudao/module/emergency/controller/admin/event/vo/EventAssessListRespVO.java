package cn.iocoder.yudao.module.emergency.controller.admin.event.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 应急事件研判记录列表响应")
@Data
public class EventAssessListRespVO {

    @Schema(description = "研判记录ID")
    private Long id;

    @Schema(description = "建议响应级别 I~V")
    private String responseLevel;

    @Schema(description = "建议预案级别 I~V")
    private String recommendedPlanLevel;

    @Schema(description = "研判意见")
    private String comment;

    @Schema(description = "创建人")
    private String creator;

    @Schema(description = "创建人姓名")
    private String creatorName;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "研判时间")
    private LocalDateTime assessTime;
}

