package cn.iocoder.yudao.module.emergency.controller.admin.report.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - 信息报送创建 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class InformationReportCreateReqVO extends InformationReportBaseVO {

    @Schema(description = "关联事件ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "关联事件ID不能为空")
    private Long eventId;

    @Schema(description = "报送类型（RECEIVE接报/PRESENT呈报/REPORT上报）", requiredMode = Schema.RequiredMode.REQUIRED, example = "RECEIVE")
    @NotNull(message = "报送类型不能为空")
    private String reportType;

    @Schema(description = "报送内容", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "报送内容不能为空")
    private String reportContent;
}








