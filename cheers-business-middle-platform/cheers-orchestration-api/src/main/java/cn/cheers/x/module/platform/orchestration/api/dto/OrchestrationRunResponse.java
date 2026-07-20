package cn.cheers.x.module.platform.orchestration.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * 通用编排运行结果。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrchestrationRunResponse {

    private String orchestrationRef;

    private String status;

    @Builder.Default
    private Map<String, Object> result = new HashMap<>();
}
