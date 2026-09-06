package cn.cheers.x.inspection.task.controller.admin.vo.task;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 巡检任务统计响应 VO。
 *
 * <p>供首页「任务信息」卡片等聚合展示使用；各计数口径与列表展示状态一致
 * （由 enabled + runtimeJobId 派生，见 InspectionTaskQueryServiceImpl#deriveDisplayStatus）。</p>
 */
@Data
@Schema(description = "管理后台 - 巡检任务统计 Response VO")
public class InspectionTaskStatisticsRespVO {

    @Schema(description = "任务总数")
    private Long taskTotal;

    @Schema(description = "已启用任务数（enabled=true，排程在跑）")
    private Long enabledCount;

    @Schema(description = "已停用任务数（排过期但当前未启用）")
    private Long disabledCount;

    @Schema(description = "草稿任务数（从未进入排程）")
    private Long draftCount;
}
