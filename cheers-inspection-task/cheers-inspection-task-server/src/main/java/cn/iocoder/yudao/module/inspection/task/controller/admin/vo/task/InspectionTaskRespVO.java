package cn.iocoder.yudao.module.inspection.task.controller.admin.vo.task;

import cn.iocoder.yudao.module.inspection.task.model.task.InspectionContent;
import cn.iocoder.yudao.module.inspection.task.model.task.ResourcePolicy;
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

    @Schema(description = "当前激活的编排批次ID")
    private Long activePlanId;

    @Schema(description = "历史编排批次ID列表")
    private List<Long> planIds = new ArrayList<>();

    // ==================== 子任务列表（轻量级） ====================
    @Schema(description = "直接子任务列表（轻量级，不含 inspectionContent）")
    private List<InspectionTaskSubTaskVO> subTasks = new ArrayList<>();

    // ==================== 时间信息 ====================
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
