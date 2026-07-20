package cn.cheers.x.module.platform.orchestration.template;

import cn.cheers.x.module.platform.orchestration.phase.OrchestrationPhase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 编排模板：有序阶段及可选 handler 映射（null 或缺失表示平台内建）。
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrchestrationTemplate {

    private String ref;

    private List<OrchestrationPhase> phases;

    /**
     * 阶段 → handlerId；未配置时使用平台内建逻辑。
     */
    private Map<OrchestrationPhase, String> handlerIds;

}
