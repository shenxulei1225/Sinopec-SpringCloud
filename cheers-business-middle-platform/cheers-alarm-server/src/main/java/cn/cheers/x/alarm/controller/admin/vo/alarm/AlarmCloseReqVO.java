package cn.cheers.x.alarm.controller.admin.vo.alarm;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 管理后台 - 告警关闭 Request VO
 */
@Schema(description = "管理后台 - 告警关闭 Request VO")
@Data
public class AlarmCloseReqVO {

    @Schema(description = "关闭原因", requiredMode = Schema.RequiredMode.REQUIRED, example = "HANDLED")
    @NotBlank(message = "关闭原因不能为空")
    private String closeReason;

    @Schema(description = "关闭备注", example = "问题已解决，水位恢复正常")
    private String closeRemark;

}
