package cn.iocoder.yudao.module.inspection.task.controller.admin.vo.schedulepolicy;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 管理后台 - 排期需求基础 Request VO
 */
@Schema(description = "管理后台 - 排期需求基础 Request VO")
@Data
public class InspectionTaskScheduleRequirementBaseVO {

    // ==================== 模板管理 ====================

    @Schema(description = "是否作为模板", example = "false", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "是否作为模板不能为空")
    private Boolean isTemplate;

    @Schema(description = "模板名称（保存为模板时必填）", example = "日常巡检模板")
    @Size(max = 128, message = "模板名称长度不能超过 128 位")
    private String templateName;

    // ==================== 排期策略 ====================

    @Schema(description = "关联排期策略 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "请选择排期策略")
    private Long policyId;

    // ==================== 排期模式 ====================

    @Schema(description = "排期模式：1-固定时间点 2-间隔执行 3-自动编排", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "排期模式不能为空")
    @Min(value = 1, message = "排期模式取值范围为 1-3")
    @Max(value = 3, message = "排期模式取值范围为 1-3")
    private Integer scheduleMode;

    // ==================== 固定时间点模式 ====================

    @Schema(description = "固定执行时间点集合，排期模式=1（固定时间点）时必填", example = "[09:00, 14:00, 18:00]")
    @Size(max = 48, message = "时间点数量不能超过 48 个")
    private List<@NotNull(message = "时间点不能为空") LocalTime> timePoints = new ArrayList<>();

    // ==================== 间隔执行模式 ====================

    @Schema(description = "任务周期（分钟）。下一个任务开始时间 - 上一个任务开始时间。例如：120 表示每2小时执行一次", example = "120")
    @Min(value = 1, message = "任务周期必须大于 0")
    @Max(value = 43200, message = "任务周期不能超过 30 天（43200分钟）")
    private Integer taskCycleMinutes;

    @Schema(description = "任务间隔（分钟）。下一个任务开始时间 - 上一个任务完成时间。例如：90 表示上次完成后休息90分钟", example = "90")
    @Min(value = 1, message = "任务间隔必须大于 0")
    @Max(value = 43200, message = "任务间隔不能超过 30 天（43200分钟）")
    private Integer taskGapMinutes;

    @Schema(description = "每日允许执行开始时间，限定每天的开始时间范围，仅在间隔执行/自动编排模式下生效", example = "08:00")
    private LocalTime windowStartTime;

    @Schema(description = "每日允许执行结束时间，限定每天的结束时间范围，仅在间隔执行/自动编排模式下生效", example = "20:00")
    private LocalTime windowEndTime;

    @Schema(description = "间隔执行的基准时间，每天以此时间为起点开始计算周期", example = "08:00")
    private LocalTime anchorTime;

    // ==================== 自动编排模式 ====================

    @Schema(description = "自动编排模式：每日执行次数。排期模式=3（自动编排）时必填，算法根据此次数和策略自动计算时刻", example = "3")
    @Min(value = 1, message = "每日执行次数必须大于 0")
    @Max(value = 48, message = "每日执行次数不能超过 48 次")
    private Integer dailyExecutionCount;

    // ==================== 执行次数范围约束 ====================

    @Schema(description = "每日最少执行次数。国标要求每2小时巡检 → 每天≥12次", example = "12")
    @Min(value = 0, message = "每日最少次数不能为负数")
    @Max(value = 48, message = "每日最少次数不能超过 48")
    private Integer minExecutionsPerDay;

    @Schema(description = "每日最多次数。限流控制 → 每天≤6次", example = "6")
    @Min(value = 0, message = "每日最多次数不能为负数")
    @Max(value = 48, message = "每日最多次数不能超过 48")
    private Integer maxExecutionsPerDay;

    @Schema(description = "每周最少执行次数。每周例会巡检 → 每周≥1次", example = "1")
    @Min(value = 0, message = "每周最少次数不能为负数")
    @Max(value = 336, message = "每周最少次数不能超过 48*7")
    private Integer minExecutionsPerWeek;

    @Schema(description = "每周最多次数", example = "10")
    @Min(value = 0, message = "每周最多次数不能为负数")
    @Max(value = 336, message = "每周最多次数不能超过 48*7")
    private Integer maxExecutionsPerWeek;

    @Schema(description = "每月最少执行次数。月度巡检 → 每月≥1次", example = "1")
    @Min(value = 0, message = "每月最少次数不能为负数")
    @Max(value = 1488, message = "每月最少次数不能超过 48*31")
    private Integer minExecutionsPerMonth;

    @Schema(description = "每月最多次数", example = "30")
    @Min(value = 0, message = "每月最多次数不能为负数")
    @Max(value = 1488, message = "每月最多次数不能超过 48*31")
    private Integer maxExecutionsPerMonth;

    // ==================== 日期范围 ====================

    @Schema(description = "开始日期", requiredMode = Schema.RequiredMode.REQUIRED, example = "2026-01-01")
    @NotNull(message = "开始日期不能为空")
    private LocalDate startDate;

    @Schema(description = "结束日期。null 表示永久有效", example = "2026-12-31")
    private LocalDate endDate;

    // ==================== 重复维度 ====================

    @Schema(description = "重复维度：1-一次性 2-每日 3-每周 4-每月", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "重复模式不能为空")
    @Min(value = 1, message = "重复模式取值范围为 1-4")
    @Max(value = 4, message = "重复模式取值范围为 1-4")
    private Integer repeatMode;

    @Schema(description = "每周重复日集合（1=周一, 7=周日），重复模式=3（每周）时必填", example = "[1, 3, 5]")
    @Size(max = 7, message = "每周重复日数量不能超过 7 个")
    private List<@Min(value = 1, message = "周几取值范围必须为 1-7")
                 @Max(value = 7, message = "周几取值范围必须为 1-7") Integer> weekDays = new ArrayList<>();

    @Schema(description = "每月重复日集合（1-31），重复模式=4（每月）时必填。超过该月最大天数时取月末最后一天", example = "[1, 15]")
    @Size(max = 31, message = "每月重复日数量不能超过 31 个")
    private List<@Min(value = 1, message = "每月日期取值范围必须为 1-31")
                 @Max(value = 31, message = "每月日期取值范围必须为 1-31") Integer> monthDays = new ArrayList<>();

    // ==================== 扩展字段 ====================

    @Schema(description = "排期需求说明", example = "国标要求每2小时完成一次巡检")
    @Size(max = 500, message = "需求说明长度不能超过 500 位")
    private String description;
}
