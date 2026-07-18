package cn.cheers.x.alarm.controller.admin.vo.linkage;

import cn.cheers.x.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.cheers.x.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

/**
 * 管理后台 - 联动执行记录分页查询 Request VO
 */
@Schema(description = "管理后台 - 联动执行记录分页查询 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class LinkageExecutionPageReqVO extends PageParam {

    @Schema(description = "告警ID", example = "1")
    private Long alarmId;

    @Schema(description = "联动规则ID", example = "1")
    private Long linkageRuleId;

    @Schema(description = "动作类型", example = "DEVICE_CONTROL")
    private String actionType;

    @Schema(description = "执行状态", example = "SUCCESS")
    private String executionStatus;

    @Schema(description = "是否需要人工介入", example = "false")
    private Boolean manualIntervention;

    @Schema(description = "开始时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
