package cn.iocoder.yudao.module.alarm.controller.admin.vo.linkage;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理后台 - 联动规则 Response VO
 */
@Schema(description = "管理后台 - 联动规则 Response VO")
@Data
public class LinkageRuleRespVO {

    @Schema(description = "规则ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "规则名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "水位超标联动")
    private String ruleName;

    @Schema(description = "规则编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "LINKAGE-WATER-001")
    private String ruleCode;

    @Schema(description = "关联的告警规则ID", example = "1")
    private Long alarmRuleId;

    @Schema(description = "适用的告警类型ID", example = "1")
    private Long alarmTypeId;

    @Schema(description = "适用的告警分类ID", example = "1")
    private Long alarmCategoryId;

    @Schema(description = "适用的告警级别", example = "WARNING")
    private String alarmLevel;

    @Schema(description = "触发条件表达式（JSON格式）")
    private String conditionExpression;

    @Schema(description = "联动动作配置（JSON数组）", requiredMode = Schema.RequiredMode.REQUIRED)
    private String actions;

    @Schema(description = "执行模式", example = "SERIAL")
    private String executionMode;

    @Schema(description = "是否启用", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    private Boolean enabled;

    @Schema(description = "优先级", example = "0")
    private Integer priority;

    @Schema(description = "规则描述", example = "水位超标时启动潜水泵")
    private String description;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

    @Schema(description = "更新时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime updateTime;

}
