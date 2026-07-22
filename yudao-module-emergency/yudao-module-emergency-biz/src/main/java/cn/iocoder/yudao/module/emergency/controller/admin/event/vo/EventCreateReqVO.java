package cn.iocoder.yudao.module.emergency.controller.admin.event.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Map;

@Schema(description = "管理后台 - 应急事件上报请求")
@Data
public class EventCreateReqVO {

    @Schema(description = "事件编号（唯一、可报送）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String eventCode;

    @Schema(description = "事件分类ID（可选，关联基础服务的分类管理模块，支持多级分类）- 创建时可选（允许为空），因为事件发生时还不知道事件类型，是应急小组开会讨论决定的", example = "1024")
    private Long eventType;

    @Schema(description = "细分类型（已废弃，保留字段仅用于兼容历史数据）", deprecated = true)
    @Deprecated
    private String eventSubType;

    @Schema(description = "事件级别（特别重大/重大/较大/一般）")
    private String eventLevel;

    @Schema(description = "发生时间", example = "2025-12-24 16:30:02")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    @JsonDeserialize(using = LocalDateTimeStringDeserializer.class)
    private LocalDateTime occurredAt;

    @Schema(description = "发现时间", requiredMode = Schema.RequiredMode.REQUIRED, example = "2025-12-24 16:30:02")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    @JsonDeserialize(using = LocalDateTimeStringDeserializer.class)
    @NotNull
    private LocalDateTime discoveredAt;

    @Schema(description = "地址位置")
    private String locationAddress;

    @Schema(description = "GIS坐标（WKT格式，如POINT(116.397128 39.916527)）")
    private String locationGis;

    @Schema(description = "BIM坐标")
    private String locationBim;

    @Schema(description = "事件描述")
    private String description;

    @Schema(description = "影响概述（人员/财产/环境/秩序）")
    private Map<String, Object> impactSummary;

    @Schema(description = "上报单位")
    private String reportedBy;

    @Schema(description = "指挥机构")
    private String commandOrg;

    @Schema(description = "附件信息（兼容数组与对象），若为数组则视为 photos 列表")
    private Object attachments;

    @Schema(description = "位置信息（兼容旧接口）", deprecated = true)
    @Valid
    private LocationVO location;

    @Schema(description = "联系人")
    private String contactPerson;

    @Schema(description = "联系方式")
    private String contactPhone;

    @Schema(description = "告警来源类型（告警转事件时写入）", example = "iot_alert_record")
    private String sourceAlertType;

    @Schema(description = "来源告警 id（告警转事件时写入）")
    private String sourceAlertId;
}
