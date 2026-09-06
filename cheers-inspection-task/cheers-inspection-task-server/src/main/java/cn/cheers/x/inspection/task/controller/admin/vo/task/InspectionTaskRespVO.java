package cn.cheers.x.inspection.task.controller.admin.vo.task;

import cn.cheers.x.inspection.task.model.task.InspectionContent;
import cn.cheers.x.inspection.task.model.task.ExecutionDeviceBinding;
import cn.cheers.x.inspection.task.model.task.ResourcePolicy;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 巡检任务详情响应 VO。
 *
 * <p>用于任务详情查询，包含完整字段及子任务列表（轻量级）。</p>
 */
@Data
@Schema(description = "管理后台 - 巡检任务详情 Response VO")
public class InspectionTaskRespVO {

    // ==================== 基础信息 ====================
    @Schema(description = "任务ID")
    private Long id;

    @Schema(description = "父任务ID")
    private Long parentId;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "任务编码")
    private String taskCode;

    @Schema(description = "任务名称")
    private String taskName;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "是否启用")
    private Boolean enabled;

    @Schema(description = "备注")
    private String remark;

    // ==================== 继承配置 ====================
    @Schema(description = "是否继承父任务排期")
    private Boolean inheritParentSchedule;

    @Schema(description = "是否继承父任务资源策略")
    private Boolean inheritParentResourcePolicy;

    // ==================== 任务内容 ====================
    @Schema(description = "巡检内容")
    private InspectionContent inspectionContent;

    // ==================== 排期与资源 ====================
    @Schema(description = "排期需求ID")
    private Long scheduleRequirementId;

    @Schema(description = "排期策略ID")
    private Long schedulePolicyId;

    @Schema(description = "资源策略")
    private ResourcePolicy resourcePolicy;

    @Schema(description = "执行设备绑定（打开任务可见具体执行设备与对接参数）")
    private ExecutionDeviceBinding executionDeviceBinding;

    @Schema(description = "设备侧运行态：IDLE/DISPATCHED/RUNNING/COMPLETED/FAULT")
    private String deviceRunStatus;

    @Schema(description = "最近设备上行时间（epoch millis）")
    private Long deviceLastUplinkAt;

    @Schema(description = "最近设备上行外层 opcode（过渡）")
    private Integer deviceLastUplinkOpcode;

    @Schema(description = "当前激活的编排批次ID")
    private Long activePlanId;

    @Schema(description = "编排运行作业 ID；排期预占（reserve）后写入")
    private String runtimeJobId;

    @Schema(description = "历史编排批次ID列表")
    private List<Long> planIds = new ArrayList<>();

    // ==================== 已保存路线读模型（confirm 后） ====================

    @Schema(description = "关联路线方案台账 ID；无已保存路线时为空")
    private Long routePlanId;

    @Schema(description = "已确认路网引用")
    private String networkRef;

    @Schema(description = "巡检类型：HUMAN|GROUND_ROBOT|UAV")
    private String inspectionType;

    @Schema(description = "已确认路线时长估算（分钟）")
    private Integer durationEstimateMinutes;

    @Schema(description = "停靠点规划序（stopSequence）")
    private List<String> stopSequence = new ArrayList<>();

    @Schema(description = "固定起点停靠点 id；未落库时为空（routing 无独立终点字段）")
    private String startStopId;

    @Schema(description = "已保存路线摘要；无 segments 折线时前端不得假装完整三维轨迹")
    private TaskSavedRoutePreviewVO routePreview;

    // ==================== 子任务列表（轻量级） ====================
    @Schema(description = "直接子任务列表（轻量级，不含 inspectionContent）")
    private List<InspectionTaskSubTaskVO> subTasks = new ArrayList<>();

    // ==================== 时间信息 ====================
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
