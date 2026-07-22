package cn.cheers.x.inspection.task.controller.admin.vo.task;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 任务已保存路线读模型（confirm 快照摘要）。
 *
 * <p>与编排预览 {@code RoutePreviewDTO} 不同：当前落库的 plannedRoute 含停靠点序与距离摘要，
 * 一般不含路径段折线（segments）；三维免重算折线若依赖 segments，须另补持久化。</p>
 */
@Data
@Schema(description = "已保存路线摘要（confirm 快照）")
public class TaskSavedRoutePreviewVO {

    @Schema(description = "路网引用")
    private String networkRef;

    @Schema(description = "停靠点有序 id")
    private List<String> stopIds = new ArrayList<>();

    @Schema(description = "总距离（米）")
    private Long totalDistanceMeters;

    @Schema(description = "机动剖面 id")
    private String mobilityProfileId;

    @Schema(description = "固定起点停靠点 id（若规划时传入并落库）")
    private String startStopId;

    @Schema(description = "是否回到起点")
    private Boolean returnToStart;

    @Schema(description = "决策追踪 id")
    private String decisionTraceId;
}
