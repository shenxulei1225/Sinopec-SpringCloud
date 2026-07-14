package cn.cheers.x.module.platform.routing.planner;

import org.springframework.stereotype.Component;

import java.util.Map;

import static cn.cheers.x.module.platform.routing.enums.ErrorCodeConstants.ROUTE_STRATEGY_UNSUPPORTED;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;

@Component
public class StopOrderStrategyRegistry {

    private final Map<String, StopOrderStrategy> strategies;

    public StopOrderStrategyRegistry(AsGivenOrderStrategy asGivenOrderStrategy,
                                       OptimizeOrderStrategy optimizeOrderStrategy,
                                       RefineOrderStrategy refineOrderStrategy) {
        this.strategies = Map.of(
                "as_given", asGivenOrderStrategy,
                "optimize_order", optimizeOrderStrategy,
                "refine_order", refineOrderStrategy
        );
    }

    public StopOrderStrategy resolve(String strategy) {
        StopOrderStrategy resolved = strategies.get(strategy);
        if (resolved == null) {
            throw exception(ROUTE_STRATEGY_UNSUPPORTED);
        }
        return resolved;
    }

}
