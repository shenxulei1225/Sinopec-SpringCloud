package cn.cheers.x.alarm.controller.admin.vo.linkage;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 管理后台 - 手动执行联动 Request VO
 * 
 * <p>用于值班员手动触发联动控制</p>
 */
@Schema(description = "管理后台 - 手动执行联动 Request VO")
@Data
public class LinkageManualExecuteReqVO {

    @Schema(description = "告警ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "告警ID不能为空")
    private Long alarmId;

    @Schema(description = "动作类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "DEVICE_CONTROL")
    @NotBlank(message = "动作类型不能为空")
    private String actionType;

    @Schema(description = "动作配置（JSON格式）", example = "{\"deviceId\":1,\"action\":\"START\"}")
    private String actionConfig;

}
