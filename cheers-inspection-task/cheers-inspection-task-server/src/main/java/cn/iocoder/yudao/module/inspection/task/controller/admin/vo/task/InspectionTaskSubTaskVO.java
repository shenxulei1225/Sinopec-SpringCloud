package cn.iocoder.yudao.module.inspection.task.controller.admin.vo.task;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 巡检子任务响应 VO（轻量级）。
 *
 * <p>用于任务详情中的子任务列表，不包含 inspectionContent 等复杂字段。</p>
 */
@Data
@Schema(description = "管理后台 - 巡检子任务 Response VO（轻量级）")
public class InspectionTaskSubTaskVO {

    @Schema(description = "子任务ID")
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

    @Schema(description = "排期策略ID")
    private Long schedulePolicyId;
}
