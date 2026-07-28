package cn.cheers.x.module.platform.runtime.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

/**
 * 过程时间线动作追加请求（五问摘要）。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessTimelineActionAppendReqDTO {

    /** 目标类型，如 emergency_event */
    @NotBlank
    private String targetType;

    /** 目标 id（字符串） */
    @NotBlank
    private String targetId;

    @NotNull
    private OffsetDateTime occurredAt;

    private String actorId;

    private String actorName;

    /** 动作码，如 event.confirm */
    @NotBlank
    private String actionCode;

    /** 怎么做摘要（必填，禁止空串冒充） */
    @NotBlank
    private String howSummary;

    /** 可选扩展 JSON 字符串 */
    private String payloadJson;

    private Long facilityId;
}
