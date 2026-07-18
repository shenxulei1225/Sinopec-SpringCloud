package cn.cheers.x.alarm.controller.admin.vo.rule;

import cn.cheers.x.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 管理后台 - 告警规则分页查询 Request VO
 */
@Schema(description = "管理后台 - 告警规则分页查询 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AlarmRulePageReqVO extends PageParam {

    @Schema(description = "规则名称", example = "水位")
    private String ruleName;

    @Schema(description = "规则编码", example = "RULE-WATER")
    private String ruleCode;

    @Schema(description = "规则类型", example = "THRESHOLD")
    private String ruleType;

    @Schema(description = "告警类型ID", example = "1")
    private Long alarmTypeId;

    @Schema(description = "告警分类ID", example = "1")
    private Long alarmCategoryId;

    @Schema(description = "告警级别", example = "WARNING")
    private String alarmLevel;

    @Schema(description = "是否启用", example = "true")
    private Boolean enabled;

}
