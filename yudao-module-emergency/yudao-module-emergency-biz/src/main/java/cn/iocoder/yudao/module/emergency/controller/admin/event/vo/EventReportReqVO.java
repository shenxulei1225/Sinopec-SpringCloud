package cn.iocoder.yudao.module.emergency.controller.admin.event.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Schema(description = "管理后台 - 应急事件追加上报请求")
@Data
public class EventReportReqVO {

    @Schema(description = "上报分类（INTERNAL内部上报/EXTERNAL外部上报）", requiredMode = Schema.RequiredMode.REQUIRED, example = "INTERNAL")
    @NotBlank(message = "上报分类不能为空")
    private String reportCategory;

    @Schema(description = "上报内容", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String content;

    @Schema(description = "附件列表（支持字符串URL或对象数组）")
    private Object attachments;

    // ==================== 外部上报专用字段 ====================
    
    @Schema(description = "上报机构名称（外部上报时使用）", example = "XX市政府")
    private String reportOrgName;

    @Schema(description = "上报人姓名（外部上报时使用）", example = "张三")
    private String reporterName;

    @Schema(description = "上报人联系方式（外部上报时使用）", example = "13800138000")
    private String reporterContact;
}










