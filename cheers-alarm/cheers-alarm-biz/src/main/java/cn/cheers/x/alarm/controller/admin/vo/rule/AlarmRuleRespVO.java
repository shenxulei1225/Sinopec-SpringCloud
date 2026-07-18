package cn.cheers.x.alarm.controller.admin.vo.rule;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理后台 - 告警规则 Response VO
 */
@Schema(description = "管理后台 - 告警规则 Response VO")
@Data
public class AlarmRuleRespVO {

    @Schema(description = "规则ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "规则名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "水位超标告警")
    private String ruleName;

    @Schema(description = "规则编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "RULE-WATER-001")
    private String ruleCode;

    @Schema(description = "规则类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "THRESHOLD")
    private String ruleType;

    @Schema(description = "告警类型ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long alarmTypeId;

    @Schema(description = "告警分类ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long alarmCategoryId;

    @Schema(description = "告警模型ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long alarmModelId;

    @Schema(description = "告警级别", requiredMode = Schema.RequiredMode.REQUIRED, example = "WARNING")
    private String alarmLevel;

    @Schema(description = "适用设备类型", example = "WATER_LEVEL_SENSOR")
    private String deviceType;

    @Schema(description = "条件表达式（JSON格式）", requiredMode = Schema.RequiredMode.REQUIRED)
    private String conditionExpression;

    @Schema(description = "告警内容模板", example = "水位超标，当前水位${value}cm，阈值${threshold}cm")
    private String alarmContentTemplate;

    @Schema(description = "是否启用", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    private Boolean enabled;

    @Schema(description = "优先级", example = "0")
    private Integer priority;

    @Schema(description = "规则描述", example = "当水位超过50cm时触发告警")
    private String description;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

    @Schema(description = "更新时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime updateTime;

}
