package cn.cheers.x.alarm.controller.admin.vo.linkage;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 管理后台 - 联动规则测试 Response VO
 */
@Schema(description = "管理后台 - 联动规则测试 Response VO")
@Data
public class LinkageRuleTestRespVO {

    @Schema(description = "测试是否成功", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    private Boolean success;

    @Schema(description = "测试结果说明", example = "联动规则测试成功，共执行3个动作")
    private String message;

    @Schema(description = "动作执行结果列表")
    private List<ActionTestResult> actionResults;

    @Schema(description = "总执行耗时（毫秒）", example = "1500")
    private Long executionTimeMs;

    @Schema(description = "总执行耗时（毫秒）- 别名", example = "1500")
    private Long totalExecutionTimeMs;

    @Schema(description = "结果消息", example = "联动规则测试完成，共3个动作，成功3个，失败0个")
    private String resultMessage;

    @Schema(description = "错误信息（如果有）")
    private String errorMessage;

    /**
     * 动作测试结果
     */
    @Data
    @Schema(description = "动作测试结果")
    public static class ActionTestResult {

        @Schema(description = "动作类型", example = "DEVICE_CONTROL")
        private String actionType;

        @Schema(description = "目标设备名称", example = "潜水泵-B区2号")
        private String targetDeviceName;

        @Schema(description = "是否成功", example = "true")
        private Boolean success;

        @Schema(description = "执行结果/消息", example = "设备启动成功")
        private String message;

        @Schema(description = "执行结果详情", example = "设备启动成功")
        private String result;

        @Schema(description = "错误信息", example = "设备连接超时")
        private String errorMessage;

        @Schema(description = "执行耗时（毫秒）", example = "500")
        private Long executionTimeMs;

    }

}
