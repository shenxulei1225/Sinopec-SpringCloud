package cn.iocoder.yudao.module.alarm.controller.admin.vo.alarm;

import cn.cheers.x.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

import static cn.cheers.x.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

/**
 * 管理后台 - 告警分页查询 Request VO
 */
@Schema(description = "管理后台 - 告警分页查询 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class AlarmPageReqVO extends PageParam {

    @Schema(description = "告警编码", example = "ALM-20260104-00001")
    private String alarmCode;

    @Schema(description = "告警类型ID列表", example = "[1, 2, 3]")
    private List<Long> alarmTypeIds;

    @Schema(description = "告警分类ID列表", example = "[1, 2]")
    private List<Long> alarmCategoryIds;

    @Schema(description = "告警级别列表", example = "[\"WARNING\", \"CRITICAL\"]")
    private List<String> alarmLevels;

    @Schema(description = "告警状态列表", example = "[\"PENDING\", \"ACKNOWLEDGED\"]")
    private List<String> alarmStatuses;

    @Schema(description = "告警来源", example = "SYSTEM")
    private String alarmSource;

    @Schema(description = "关联设备ID", example = "1")
    private Long deviceId;

    @Schema(description = "位置ID", example = "1")
    private Long locationId;

    @Schema(description = "告警内容关键字", example = "水位")
    private String alarmContentKeyword;

    @Schema(description = "创建时间开始")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime createTimeStart;

    @Schema(description = "创建时间结束")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime createTimeEnd;

    @Schema(description = "是否只查询实时告警（未关闭）", example = "true")
    private Boolean realTimeOnly;

}
