package cn.iocoder.yudao.module.emergency.controller.admin.event.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Map;

@Schema(description = "管理后台 - 应急事件响应")
@Data
public class EventRespVO {

    private Long id;
    private String eventCode;
    private String eventType;
    private String eventSubType;
    private String eventLevel;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime occurredAt;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime discoveredAt;
    private String locationAddress;
    private String locationGis;
    private String locationBim;
    private String description;
    private Map<String, Object> impactSummary;
    private String status;
    private String reportedBy;
    private String commandOrg;
    private Boolean isGovernmentConfirmed;
    private Map<String, Object> attachments;
    private String contactPerson;
    private String contactPhone;
}
