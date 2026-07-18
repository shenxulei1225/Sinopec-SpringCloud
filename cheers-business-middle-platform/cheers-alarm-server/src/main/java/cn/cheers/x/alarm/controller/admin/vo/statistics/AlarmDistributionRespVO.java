package cn.cheers.x.alarm.controller.admin.vo.statistics;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 管理后台 - 告警分布统计 Response VO
 */
@Schema(description = "管理后台 - 告警分布统计 Response VO")
@Data
public class AlarmDistributionRespVO {

    @Schema(description = "分类名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "环境告警")
    private String name;

    @Schema(description = "分类编码", example = "ALARM_ENVIRONMENT")
    private String code;

    @Schema(description = "告警数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "50")
    private Integer count;

    @Schema(description = "占比（百分比）", example = "35.5")
    private Double percentage;

}
