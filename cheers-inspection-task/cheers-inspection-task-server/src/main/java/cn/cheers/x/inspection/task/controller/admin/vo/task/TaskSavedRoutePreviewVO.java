package cn.cheers.x.inspection.task.controller.admin.vo.task;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 任务已保存路线读模型（saveRoute 快照）。
 *
 * <p>与编排当次 live preview 同源：plannedRoute 在 ROUTE 阶段写入 stopIds、visitNodeIds、
 * visitPositions、segments；详情回显路线示意图只读这些字段，禁止读路径再算一遍冒充已保存折线。</p>
 */
@Data
@Schema(description = "已保存路线摘要（saveRoute 快照）")
public class TaskSavedRoutePreviewVO {

    @Schema(description = "路网引用")
    private String networkRef;

    @Schema(description = "停靠点有序 id")
    private List<String> stopIds = new ArrayList<>();

    @Schema(description = "总距离（米）")
    private Long totalDistanceMeters;

    @Schema(description = "机动剖面 id")
    private String mobilityProfileId;

    @Schema(description = "任务创建选定的起点（无人机起飞点）")
    private String startStopId;

    @Schema(description = "任务创建选定的终点（无人机降落点）")
    private String endStopId;

    @Schema(description = "是否回到起点")
    private Boolean returnToStart;

    @Schema(description = "决策追踪 id")
    private String decisionTraceId;

    @Schema(description = "实际经过点位序（含途径点）")
    private List<String> visitNodeIds = new ArrayList<>();

    @Schema(description = "与 visitNodeIds 对齐的坐标")
    private List<Object> visitPositions = new ArrayList<>();

    @Schema(description = "路径段折线（含 polyline）")
    private List<Object> segments = new ArrayList<>();
}
