package cn.iocoder.yudao.module.emergency.controller.admin.event.vo;

import cn.cheers.x.bpm.api.task.dto.BpmActivityNodeRespDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
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

    @Schema(description = "流程实例编号（只读；无实例则为空）")
    private String processInstanceId;

    @Schema(description = "告警来源类型（只读）")
    private String sourceAlertType;

    @Schema(description = "来源告警 id（只读）")
    private String sourceAlertId;

    @Schema(description = "当前运行中流程节点（只读；无实例为空列表，不伪造）")
    private List<BpmActivityNodeRespDTO> currentProcessNodes = Collections.emptyList();
}
