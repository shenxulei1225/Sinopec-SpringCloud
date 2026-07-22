package cn.iocoder.yudao.module.emergency.controller.admin.report.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "管理后台 - 信息报送确认 Request VO")
@Data
public class InformationReportConfirmReqVO {

    @Schema(description = "确认备注", requiredMode = Schema.RequiredMode.REQUIRED, example = "已收到")
    @NotBlank(message = "确认备注不能为空")
    private String confirmRemark;
}








