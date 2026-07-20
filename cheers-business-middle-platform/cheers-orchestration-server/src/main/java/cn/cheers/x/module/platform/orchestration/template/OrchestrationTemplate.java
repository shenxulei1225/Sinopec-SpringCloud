package cn.cheers.x.module.platform.orchestration.template;

import cn.cheers.x.module.platform.orchestration.phase.OrchestrationPhase;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

/**
 * 编排模板：有序阶段及可选 handler 映射（null 或缺失表示平台内建）。
 */
@Getter
@Builder
public class OrchestrationTemplate {

    private final String ref;

    private final List<OrchestrationPhase> phases;

    /**
     * 阶段 → handlerId；未配置时使用平台内建逻辑。
     */
    private final Map<OrchestrationPhase, String> handlerIds;

}
