package cn.cheers.x.module.platform.orchestration.template;

import cn.cheers.x.module.platform.orchestration.enums.OrchestrationRefs;
import cn.cheers.x.module.platform.orchestration.phase.OrchestrationPhase;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.cheers.x.module.platform.orchestration.enums.ErrorCodeConstants.SCHEDULE_RUN_ORCHESTRATION_UNKNOWN;
import static cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil.exception;

/**
 * 编排模板内存注册表（本波次不落库）。
 */
@Component
public class OrchestrationTemplateRegistry {

    private final Map<String, OrchestrationTemplate> templates = new HashMap<>();

    public OrchestrationTemplateRegistry() {
        registerBuiltins();
    }

    public OrchestrationTemplate require(String ref) {
        if (!StringUtils.hasText(ref)) {
            throw exception(SCHEDULE_RUN_ORCHESTRATION_UNKNOWN);
        }
        OrchestrationTemplate template = templates.get(ref);
        if (template == null) {
            throw exception(SCHEDULE_RUN_ORCHESTRATION_UNKNOWN);
        }
        return template;
    }

    private void registerBuiltins() {
        register(OrchestrationRefs.STANDARD_EXPAND_SOLVE_PERSIST_V1, List.of(
                OrchestrationPhase.EXPAND,
                OrchestrationPhase.SOLVE,
                OrchestrationPhase.PERSIST), Map.of());

        register(OrchestrationRefs.PATROL_ROUTE_PREVIEW_V1, List.of(
                OrchestrationPhase.EXPAND,
                OrchestrationPhase.ROUTE), Map.of(
                OrchestrationPhase.ROUTE, "platform.route.plan_v1"));
        register(OrchestrationRefs.PATROL_ROUTE_CONFIRM_V1, List.of(
                OrchestrationPhase.EXPAND,
                OrchestrationPhase.ROUTE,
                OrchestrationPhase.CONFIRM), Map.of(
                OrchestrationPhase.ROUTE, "platform.route.plan_v1"));
        register(OrchestrationRefs.PATROL_SCHEDULE_ENABLE_V1, List.of(
                OrchestrationPhase.EXPAND,
                OrchestrationPhase.SOLVE,
                OrchestrationPhase.PERSIST), Map.of());
        register(OrchestrationRefs.PATROL_REPLAN_V1, List.of(
                OrchestrationPhase.EXPAND,
                OrchestrationPhase.ROUTE,
                OrchestrationPhase.SOLVE,
                OrchestrationPhase.PERSIST), Map.of(
                OrchestrationPhase.ROUTE, "platform.route.plan_v1"));
    }

    private void register(String ref, List<OrchestrationPhase> phases, Map<OrchestrationPhase, String> handlerIds) {
        templates.put(ref, OrchestrationTemplate.builder()
                .ref(ref)
                .phases(phases)
                .handlerIds(handlerIds)
                .build());
    }
}
