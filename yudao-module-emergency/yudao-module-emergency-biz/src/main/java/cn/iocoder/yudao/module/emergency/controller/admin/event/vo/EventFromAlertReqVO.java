package cn.iocoder.yudao.module.emergency.controller.admin.event.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Map;

@Schema(description = "管理后台 - 告警转应急事件请求")
@Data
public class EventFromAlertReqVO {

    @Schema(description = "告警编号（只存关联 id）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1001")
    @NotBlank(message = "告警编号不能为空")
    private String alertId;

    @Schema(description = "告警来源类型", example = "iot_alert_record")
    private String alertSource;

    @Schema(description = "事件编号；不传则服务端生成")
    private String eventCode;

    @Schema(description = "事件分类ID")
    private Long eventType;

    @Schema(description = "事件级别")
    private String eventLevel;

    @Schema(description = "发生时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    @JsonDeserialize(using = LocalDateTimeStringDeserializer.class)
    private LocalDateTime occurredAt;

    @Schema(description = "发现时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    @JsonDeserialize(using = LocalDateTimeStringDeserializer.class)
    @NotNull(message = "发现时间不能为空")
    private LocalDateTime discoveredAt;

    @Schema(description = "地址位置")
    private String locationAddress;

    @Schema(description = "GIS坐标（WKT）")
    private String locationGis;

    @Schema(description = "BIM坐标")
    private String locationBim;

    @Schema(description = "事件描述", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "事件描述不能为空")
    private String description;

    @Schema(description = "影响概述")
    private Map<String, Object> impactSummary;

    @Schema(description = "上报单位")
    private String reportedBy;

    @Schema(description = "指挥机构")
    private String commandOrg;

    @Schema(description = "附件")
    private Object attachments;

    @Schema(description = "联系人")
    private String contactPerson;

    @Schema(description = "联系方式")
    private String contactPhone;
}
