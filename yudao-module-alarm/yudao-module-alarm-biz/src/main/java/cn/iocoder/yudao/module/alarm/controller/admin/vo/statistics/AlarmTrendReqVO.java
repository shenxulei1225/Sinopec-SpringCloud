package cn.iocoder.yudao.module.alarm.controller.admin.vo.statistics;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY;

/**
 * 管理后台 - 告警趋势查询 Request VO
 */
@Schema(description = "管理后台 - 告警趋势查询 Request VO")
@Data
public class AlarmTrendReqVO {

    @Schema(description = "开始日期", requiredMode = Schema.RequiredMode.REQUIRED, example = "2026-01-01")
    @NotNull(message = "开始日期不能为空")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate startDate;

    @Schema(description = "结束日期", requiredMode = Schema.RequiredMode.REQUIRED, example = "2026-01-04")
    @NotNull(message = "结束日期不能为空")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate endDate;

    @Schema(description = "告警类型ID", example = "1")
    private Long alarmTypeId;

    @Schema(description = "告警分类ID", example = "1")
    private Long alarmCategoryId;

    @Schema(description = "告警级别", example = "WARNING")
    private String alarmLevel;

}
