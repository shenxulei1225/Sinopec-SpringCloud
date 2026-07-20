package cn.iocoder.yudao.module.emergency.controller.admin.event.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Schema(description = "管理后台 - 应急事件外部上报响应")
@Data
public class EventExternalReportRespVO {

    @Schema(description = "上报记录ID")
    private Long id;

    @Schema(description = "关联的应急事件ID")
    private Long eventId;

    @Schema(description = "上报内容")
    private String content;

    @Schema(description = "上报机构名称")
    private String reportOrgName;

    @Schema(description = "上报人姓名")
    private String reporterName;

    @Schema(description = "上报人联系方式")
    private String reporterContact;

    @Schema(description = "上报时间")
    private LocalDateTime reportTime;

    @Schema(description = "附件信息")
    private Map<String, Object> attachments;
}

