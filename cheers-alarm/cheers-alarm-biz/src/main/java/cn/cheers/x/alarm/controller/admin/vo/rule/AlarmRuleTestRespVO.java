package cn.cheers.x.alarm.controller.admin.vo.rule;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 管理后台 - 告警规则测试 Response VO
 */
@Schema(description = "管理后台 - 告警规则测试 Response VO")
@Data
public class AlarmRuleTestRespVO {

    @Schema(description = "是否匹配", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    private Boolean matched;

    @Schema(description = "匹配结果说明", example = "条件匹配成功：waterLevel(55) > 50")
    private String matchResult;

    @Schema(description = "生成的告警内容", example = "水位超标，当前水位55cm，阈值50cm")
    private String generatedAlarmContent;

    @Schema(description = "告警级别", example = "WARNING")
    private String alarmLevel;

    @Schema(description = "执行耗时（毫秒）", example = "15")
    private Long executionTimeMs;

    @Schema(description = "错误信息（如果有）")
    private String errorMessage;

}
