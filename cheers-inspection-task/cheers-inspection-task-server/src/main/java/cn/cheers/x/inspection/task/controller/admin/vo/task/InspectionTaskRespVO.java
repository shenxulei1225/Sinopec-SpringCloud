package cn.cheers.x.inspection.task.controller.admin.vo.task;

import cn.cheers.x.inspection.task.model.task.InspectionContent;
import cn.cheers.x.inspection.task.model.task.ExecutionDeviceBinding;
import cn.cheers.x.inspection.task.model.task.ResourcePolicy;
import cn.cheers.x.module.platform.contract.dto.slot.ScheduleSlotDTO;
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

    @Schema(description = "业务域（任务分池）")
    private String domain;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "任务已生成并排期，将按计划到点自动开跑")
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
    @Schema(description = "排期策略ID")
    private Long schedulePolicyId;

    @Schema(description = "排期策略·冲突处理")
    private String conflictStrategy;

    @Schema(description = "排期策略·任务间隔（分钟）")
    private Integer taskGapMinutes;

    @Schema(description = "空闲不够时是否允许挪动已有任务排期")
    private Boolean allowShiftExisting;

    @Schema(description = "已有任务单侧最多挪动分钟数")
    private Integer maxShiftMinutes;

    @Schema(description = "排期模板配置（FLD-TSK-018，与前端 ScheduleConfig 对齐）")
    private Object scheduleConfig;

    @Schema(description = "资源策略")
    private ResourcePolicy resourcePolicy;

    @Schema(description = "执行设备绑定（打开任务可见具体执行设备与对接参数）")
    private ExecutionDeviceBinding executionDeviceBinding;

    @Schema(description = "巡检方式：MANUAL / UAV / ROBOT / FIXED_CAMERA")
    private String patrolExecutionMode;

    @Schema(description = "建任务已放行到哪一步：0 选对象 / 1 路线 / 2 排期与资源 / 3 核对计划")
    private Integer createUnlockedStep;

    @Schema(description = "编排运行作业 ID；试排或生成任务后写入")
    private String runtimeJobId;

    @Schema(description = "已占窗计划点（查询增强：按 runtimeJobId 读中台 L4，不写入任务草稿）")
    private List<ScheduleSlotDTO> scheduleSlots = new ArrayList<>();

    @Schema(description = "历史编排批次ID列表")
    private List<Long> planIds = new ArrayList<>();

    // ==================== 已保存路线读模型（saveRoute 写入总任务 plannedRoute 后） ====================

    @Schema(description = "已保存路网引用")
    private String networkRef;

    @Schema(description = "巡检类型：HUMAN|GROUND_ROBOT|UAV")
    private String inspectionType;

    @Schema(description = "排期总时长（分钟）= 检查项动作耗时 + 路径耗时")
    private Integer estimatedDuration;

    @Schema(description = "停靠点规划序（stopSequence）")
    private List<String> stopSequence = new ArrayList<>();

    @Schema(description = "任务创建选定的起点（无人机起飞点）")
    private String startStopId;

    @Schema(description = "任务创建选定的终点（无人机降落点）")
    private String endStopId;

    @Schema(description = "已保存路线摘要（含 segments/visitPositions 时可回显路线示意图）")
    private TaskSavedRoutePreviewVO routePreview;

    @Schema(description = "第 3 步无冲突或智能编排写入的执行步骤图（step_tree_json）；核对计划步展示动作与参数")
    private Object executionStepTree;

    // ==================== 子任务列表（轻量级） ====================
    @Schema(description = "直接子任务列表（轻量级，不含 inspectionContent）")
    private List<InspectionTaskSubTaskVO> subTasks = new ArrayList<>();

    // ==================== 时间信息 ====================
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
