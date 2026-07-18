package cn.iocoder.yudao.module.alarm.controller.admin.vo.rule;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 管理后台 - 告警规则更新 Request VO
 */
@Schema(description = "管理后台 - 告警规则更新 Request VO")
@Data
public class AlarmRuleUpdateReqVO {

    @Schema(description = "规则ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "规则ID不能为空")
    private Long id;

    @Schema(description = "规则名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "水位超标告警")
    @NotBlank(message = "规则名称不能为空")
    @Size(max = 128, message = "规则名称长度不能超过128个字符")
    private String ruleName;

    @Schema(description = "规则类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "THRESHOLD")
    @NotBlank(message = "规则类型不能为空")
    private String ruleType;

    @Schema(description = "告警类型ID（可选，用于统计分析）", example = "1")
    private Long alarmTypeId;

    @Schema(description = "告警分类ID（可选，用于统计分析）", example = "1")
    private Long alarmCategoryId;

    @Schema(description = "告警模型ID（可选，用于统计分析）", example = "1")
    private Long alarmModelId;

    @Schema(description = "告警级别", requiredMode = Schema.RequiredMode.REQUIRED, example = "WARNING")
    @NotBlank(message = "告警级别不能为空")
    private String alarmLevel;

    @Schema(description = "适用设备类型", example = "WATER_LEVEL_SENSOR")
    private String deviceType;

    @Schema(description = "条件表达式（JSON格式）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "条件表达式不能为空")
    private String conditionExpression;

    @Schema(description = "告警内容模板", example = "水位超标，当前水位${value}cm，阈值${threshold}cm")
    private String alarmContentTemplate;

    @Schema(description = "是否启用", example = "true")
    private Boolean enabled;

    @Schema(description = "优先级", example = "0")
    private Integer priority;

    @Schema(description = "规则描述", example = "当水位超过50cm时触发告警")
    @Size(max = 500, message = "规则描述长度不能超过500个字符")
    private String description;

}
