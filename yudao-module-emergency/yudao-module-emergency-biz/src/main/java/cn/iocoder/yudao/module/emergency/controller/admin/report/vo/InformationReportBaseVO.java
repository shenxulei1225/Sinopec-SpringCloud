package cn.iocoder.yudao.module.emergency.controller.admin.report.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 信息报送 Base VO，提供给添加、修改、详细的子 VO 使用
 * 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
 */
@Data
public class InformationReportBaseVO {

    @Schema(description = "关联事件ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long eventId;

    @Schema(description = "报送类型（RECEIVE接报/PRESENT呈报/REPORT上报）", requiredMode = Schema.RequiredMode.REQUIRED, example = "RECEIVE")
    private String reportType;

    @Schema(description = "报送对象", example = "应急管理部门")
    private String reportTarget;

    @Schema(description = "报送内容", requiredMode = Schema.RequiredMode.REQUIRED)
    private String reportContent;

    @Schema(description = "报送状态（PENDING待报送/SUBMITTED已报送/CONFIRMED已确认）", example = "PENDING")
    private String status;

    @Schema(description = "报送时间", example = "2024-12-25 10:00:00")
    private LocalDateTime reportTime;

    @Schema(description = "确认时间", example = "2024-12-25 10:05:00")
    private LocalDateTime confirmTime;

    @Schema(description = "确认人", example = "张三")
    private String confirmPerson;

    @Schema(description = "确认备注", example = "已收到")
    private String confirmRemark;

    @Schema(description = "时限要求（分钟）", example = "15")
    private Integer timeLimit;

    @Schema(description = "是否超时", example = "false")
    private Boolean timeout;
}








