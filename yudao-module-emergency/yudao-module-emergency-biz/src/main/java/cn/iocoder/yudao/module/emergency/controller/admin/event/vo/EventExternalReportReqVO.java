package cn.iocoder.yudao.module.emergency.controller.admin.event.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - 应急事件外部上报请求")
@Data
public class EventExternalReportReqVO {

    @Schema(description = "上报内容", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "上报内容不能为空")
    private String content;

    @Schema(description = "附件URL列表")
    private List<String> attachments;

    @Schema(description = "上报机构名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "上报机构名称不能为空")
    private String reportOrgName;

    @Schema(description = "上报人姓名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "上报人姓名不能为空")
    private String reporterName;

    @Schema(description = "上报人联系方式")
    private String reporterContact;
}

