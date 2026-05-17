package cn.iocoder.yudao.module.alarm.controller.admin.vo.rule;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 管理后台 - 告警规则测试 Request VO
 */
@Schema(description = "管理后台 - 告警规则测试 Request VO")
@Data
public class AlarmRuleTestReqVO {

    @Schema(description = "规则ID（测试已有规则时使用）", example = "1")
    private Long ruleId;

    @Schema(description = "规则类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "THRESHOLD")
    @NotBlank(message = "规则类型不能为空")
    private String ruleType;

    @Schema(description = "条件表达式（JSON格式）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "条件表达式不能为空")
    private String conditionExpression;

    @Schema(description = "测试数据（JSON格式）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "测试数据不能为空")
    private String testData;

}
