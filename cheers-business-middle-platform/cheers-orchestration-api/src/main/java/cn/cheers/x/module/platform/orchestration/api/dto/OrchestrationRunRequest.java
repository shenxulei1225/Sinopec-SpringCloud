package cn.cheers.x.module.platform.orchestration.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * 通用编排运行请求（非排程专用）。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrchestrationRunRequest {

    @NotBlank
    private String orchestrationRef;

    /** 业务域，如 inspection / emergency */
    private String scope;

    private Long facilityId;

    /** 干跑：跑阶段但不执行带副作用的 persist（本波次 expand 仍可写台账，由 Handler 自决） */
    private Boolean dryRun;

    /** 业务载荷（如 eventId / planId / responseLevel） */
    @Builder.Default
    private Map<String, Object> payload = new HashMap<>();
}
