package cn.iocoder.yudao.module.emergency.controller.admin.statistics.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 统计概览响应 VO
 */
@Schema(description = "管理后台 - 统计概览响应 VO")
@Data
public class StatisticsOverviewRespVO {

    @Schema(description = "总事件数", example = "156")
    private Integer totalEvents;

    @Schema(description = "活跃事件数", example = "8")
    private Integer activeEvents;

    @Schema(description = "总响应数", example = "142")
    private Integer totalResponses;

    @Schema(description = "活跃响应数", example = "5")
    private Integer activeResponses;

    @Schema(description = "总任务数", example = "456")
    private Integer totalTasks;

    @Schema(description = "进行中任务数", example = "23")
    private Integer activeTasks;

    @Schema(description = "资源调度总数", example = "234")
    private Integer totalResourceDispatches;

    @Schema(description = "当前使用中资源数", example = "12")
    private Integer activeResourceDispatches;

    @Schema(description = "平均响应时间（分钟）", example = "45.6")
    private Double averageResponseTime;

    @Schema(description = "事件处理率", example = "91.0")
    private Double eventHandlingRate;

    @Schema(description = "响应完成率", example = "94.4")
    private Double responseCompletionRate;

    @Schema(description = "资源利用率", example = "78.5")
    private Double resourceUtilizationRate;

    @Schema(description = "系统健康状态", example = "healthy")
    private String systemHealthStatus;
}




