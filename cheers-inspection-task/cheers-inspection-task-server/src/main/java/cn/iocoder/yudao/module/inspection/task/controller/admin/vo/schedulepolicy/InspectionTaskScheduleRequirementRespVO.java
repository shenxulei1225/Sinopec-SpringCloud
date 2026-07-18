package cn.iocoder.yudao.module.inspection.task.controller.admin.vo.schedulepolicy;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 管理后台 - 排期需求 Response VO
 */
@Schema(description = "管理后台 - 排期需求 Response VO")
@Data
public class InspectionTaskScheduleRequirementRespVO {

    // ==================== 基础信息 ====================
    @Schema(description = "需求 ID", example = "1")
    private Long id;

    @Schema(description = "需求编码")
    private String requirementCode;

    @Schema(description = "需求名称")
    private String requirementName;

    @Schema(description = "关联的任务 ID")
    private Long taskId;

    @Schema(description = "关联的排期策略 ID")
    private Long schedulePolicyId;

    @Schema(description = "关联的排期策略名称")
    private String schedulePolicyName;

    // ==================== 模板组合配置 ====================
    @Schema(description = "模板组合配置列表（一个排期需求可包含多个模板）")
    private List<ScheduleTemplateConfigVO> scheduleTemplates;

    // ==================== 扩展字段 ====================
    @Schema(description = "需求说明")
    private String description;

    // ==================== 时间信息 ====================
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
