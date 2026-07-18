package cn.cheers.x.alarm.controller.admin.vo.alarm;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 管理后台 - 告警处理 Request VO
 */
@Schema(description = "管理后台 - 告警处理 Request VO")
@Data
public class AlarmHandleReqVO {

    @Schema(description = "处理措施", requiredMode = Schema.RequiredMode.REQUIRED, example = "已启动潜水泵排水，水位正在下降")
    @NotBlank(message = "处理措施不能为空")
    private String handleMeasure;

    @Schema(description = "处理结果", requiredMode = Schema.RequiredMode.REQUIRED, example = "RESOLVED")
    @NotBlank(message = "处理结果不能为空")
    private String handleResult;

}
