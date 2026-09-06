package cn.cheers.x.inspection.task.controller.admin.vo.task;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 巡检任务列表响应 VO（轻量级）。
 *
 * <p>用于列表分页查询，包含子任务数量和排期结果信息。</p>
 */
@Data
@Schema(description = "管理后台 - 巡检任务列表 Response VO（轻量级）")
public class InspectionTaskSimpleRespVO {

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

    @Schema(description = "展示状态：由 enabled + runtimeJobId 派生（0 草稿 / 1 启用 / 2 停用），不读库里已废弃的 status 列")
    private Integer status;

    @Schema(description = "是否启用（启停唯一权威字段，编排 reserve/enable/abort 写入）")
    private Boolean enabled;

    @Schema(description = "编排运行作业 ID；已排过期的任务才有值，用于排期板把计划点对回任务")
    private String runtimeJobId;

    // ==================== 子任务数量 ====================
    @Schema(description = "子任务数量")
    private Integer subTaskCount;

    // ==================== 当前激活的排期结果 ====================
    @Schema(description = "激活的排期批次ID")
    private Long activePlanId;

    @Schema(description = "排期批次编码")
    private String activePlanCode;

    @Schema(description = "排期批次状态")
    private Integer activePlanStatus;

    @Schema(description = "排期窗口开始日期")
    private LocalDate horizonStartDate;

    @Schema(description = "排期窗口结束日期")
    private LocalDate horizonEndDate;

    @Schema(description = "本次排期生成的计划点数量")
    private Integer scheduleCount;

    // ==================== 时间信息 ====================
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
