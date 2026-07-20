package cn.cheers.x.module.platform.orchestration.phase;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.cheers.x.module.platform.orchestration.enums.ErrorCodeConstants.ORCHESTRATION_PHASE_HANDLER_MISSING;
import static cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil.exception;

/**
 * 按 handlerId 注册阶段处理器（Spring 注入全部 PhaseHandler bean）。
 */
@Component
public class PhaseHandlerRegistry {

    private final Map<String, PhaseHandler> byId = new HashMap<>();

    public PhaseHandlerRegistry(List<PhaseHandler> handlers) {
        if (handlers == null) {
            return;
        }
        for (PhaseHandler handler : handlers) {
            if (handler == null || !StringUtils.hasText(handler.handlerId())) {
                continue;
            }
            PhaseHandler prev = byId.put(handler.handlerId(), handler);
            if (prev != null) {
                throw new IllegalStateException("Duplicate PhaseHandler id: " + handler.handlerId());
            }
        }
    }

    public PhaseHandler require(String handlerId) {
        if (!StringUtils.hasText(handlerId)) {
            throw exception(ORCHESTRATION_PHASE_HANDLER_MISSING);
        }
        PhaseHandler handler = byId.get(handlerId);
        if (handler == null) {
            throw exception(ORCHESTRATION_PHASE_HANDLER_MISSING);
        }
        return handler;
    }
}
