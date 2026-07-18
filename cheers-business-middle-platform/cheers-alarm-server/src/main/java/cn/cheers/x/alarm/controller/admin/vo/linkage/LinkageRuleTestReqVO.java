package cn.cheers.x.alarm.controller.admin.vo.linkage;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 管理后台 - 联动规则测试 Request VO
 */
@Schema(description = "管理后台 - 联动规则测试 Request VO")
@Data
public class LinkageRuleTestReqVO {

    @Schema(description = "联动规则ID（测试已有规则时使用）", example = "1")
    private Long linkageRuleId;

    @Schema(description = "触发条件表达式（JSON格式）")
    private String conditionExpression;

    @Schema(description = "联动动作配置（JSON数组）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "联动动作配置不能为空")
    private String actions;

    @Schema(description = "执行模式", example = "SERIAL")
    private String executionMode;

    @Schema(description = "是否模拟执行（不实际控制设备）", example = "true")
    private Boolean dryRun;

}
