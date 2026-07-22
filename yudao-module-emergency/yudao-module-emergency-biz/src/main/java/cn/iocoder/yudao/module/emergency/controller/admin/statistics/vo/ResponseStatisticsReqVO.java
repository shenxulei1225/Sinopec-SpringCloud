package cn.iocoder.yudao.module.emergency.controller.admin.statistics.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 响应统计请求 VO
 */
@Schema(description = "管理后台 - 响应统计请求 VO")
@Data
public class ResponseStatisticsReqVO {

    @Schema(description = "开始时间", example = "2023-01-01 00:00:00")
    private LocalDateTime startTime;

    @Schema(description = "结束时间", example = "2023-12-31 23:59:59")
    private LocalDateTime endTime;

    @Schema(description = "响应级别", example = "II")
    private String responseLevel;

    @Schema(description = "响应状态", example = "ended")
    private String status;
}




