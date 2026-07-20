package cn.cheers.x.module.platform.runtime.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

/**
 * 过程时间线动作读模型。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessTimelineActionRespDTO {

    private Long id;
    private String targetType;
    private String targetId;
    private OffsetDateTime occurredAt;
    private String actorId;
    private String actorName;
    private String actionCode;
    private String howSummary;
    private String payloadJson;
    private Long siteId;
    /** platform | （读侧门面可标注） */
    private String source;
}
