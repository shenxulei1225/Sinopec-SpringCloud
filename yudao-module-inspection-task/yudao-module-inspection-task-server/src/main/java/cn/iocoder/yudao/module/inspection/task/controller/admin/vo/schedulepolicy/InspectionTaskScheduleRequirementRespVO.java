package cn.iocoder.yudao.module.inspection.task.controller.admin.vo.schedulepolicy;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * 管理后台 - 排期需求 Response VO
 */
@Schema(description = "管理后台 - 排期需求 Response VO")
@Data
public class InspectionTaskScheduleRequirementRespVO {

    @Schema(description = "需求 ID", example = "1")
    private Long id;

    @Schema(description = "关联排期策略 ID")
    private Long policyId;

    @Schema(description = "关联排期策略名称")
    private String policyName;

    @Schema(description = "是否为模板")
    private Boolean isTemplate;

    @Schema(description = "模板名称")
    private String templateName;

    // ==================== 日期范围 ====================

    @Schema(description = "开始日期")
    private LocalDate startDate;

    @Schema(description = "结束日期")
    private LocalDate endDate;

    // ==================== 重复模式 ====================

    @Schema(description = "重复模式：1-一次性 2-每日 3-每周 4-每月")
    private Integer repeatMode;

    @Schema(description = "周期步长")
    private Integer cycleStep;

    @Schema(description = "每周重复日")
    private List<Integer> weekDays;

    @Schema(description = "每月重复日")
    private List<Integer> monthDays;

    @Schema(description = "固定执行时间点")
    private List<LocalTime> timePoints;

    @Schema(description = "需求说明")
    private String description;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
