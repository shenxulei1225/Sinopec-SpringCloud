package cn.iocoder.yudao.module.emergency.controller.admin.statistics.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 资源使用统计请求 VO
 */
@Schema(description = "管理后台 - 资源使用统计请求 VO")
@Data
public class ResourceUsageStatisticsReqVO {

    @Schema(description = "开始时间", example = "2023-01-01 00:00:00")
    private LocalDateTime startTime;

    @Schema(description = "结束时间", example = "2023-12-31 23:59:59")
    private LocalDateTime endTime;

    @Schema(description = "资源类型", example = "vehicle")
    private String resourceType;

    @Schema(description = "响应级别", example = "II")
    private String responseLevel;

    @Schema(description = "是否包含演练", example = "false")
    private Boolean includeDrills = false;
}
