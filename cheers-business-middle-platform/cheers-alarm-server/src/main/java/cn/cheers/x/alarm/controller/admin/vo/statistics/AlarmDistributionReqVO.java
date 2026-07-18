package cn.cheers.x.alarm.controller.admin.vo.statistics;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

import static cn.cheers.x.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY;

/**
 * 管理后台 - 告警分布统计查询 Request VO
 */
@Schema(description = "管理后台 - 告警分布统计查询 Request VO")
@Data
public class AlarmDistributionReqVO {

    @Schema(description = "分布类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "TYPE")
    @NotBlank(message = "分布类型不能为空")
    private String distributionType; // TYPE-按类型, LEVEL-按级别, LOCATION-按位置, STATUS-按状态

    @Schema(description = "开始日期", example = "2026-01-01")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate startDate;

    @Schema(description = "结束日期", example = "2026-01-04")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate endDate;

    @Schema(description = "告警分类ID", example = "1")
    private Long alarmCategoryId;

}
