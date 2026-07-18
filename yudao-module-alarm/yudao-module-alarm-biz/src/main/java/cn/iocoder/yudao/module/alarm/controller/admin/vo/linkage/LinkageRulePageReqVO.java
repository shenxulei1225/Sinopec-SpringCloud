package cn.iocoder.yudao.module.alarm.controller.admin.vo.linkage;

import cn.cheers.x.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 管理后台 - 联动规则分页查询 Request VO
 */
@Schema(description = "管理后台 - 联动规则分页查询 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class LinkageRulePageReqVO extends PageParam {

    @Schema(description = "规则名称", example = "水位")
    private String ruleName;

    @Schema(description = "规则编码", example = "LINKAGE-WATER")
    private String ruleCode;

    @Schema(description = "关联的告警规则ID", example = "1")
    private Long alarmRuleId;

    @Schema(description = "适用的告警类型ID", example = "1")
    private Long alarmTypeId;

    @Schema(description = "适用的告警级别", example = "WARNING")
    private String alarmLevel;

    @Schema(description = "是否启用", example = "true")
    private Boolean enabled;

}
