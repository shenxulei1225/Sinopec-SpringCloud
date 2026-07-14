package cn.cheers.x.module.platform.routing.planner;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static cn.cheers.x.module.platform.routing.enums.ErrorCodeConstants.ROUTE_TOO_MANY_STOPS;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;

@Component
public class OptimizeOrderStrategy implements StopOrderStrategy {

    private static final int MAX_STOPS = 10;

    @Override
    public List<String> order(List<String> stopIds, CostMatrix matrix) {
        if (CollectionUtils.isEmpty(stopIds)) {
            return List.of();
        }
        if (stopIds.size() > MAX_STOPS) {
            throw exception(ROUTE_TOO_MANY_STOPS);
        }
        if (stopIds.size() <= 2) {
            return List.copyOf(stopIds);
        }

        final List<String>[] bestOrder = new List[]{List.copyOf(stopIds)};
        final double[] bestCost = {matrix.totalPathCost(bestOrder[0])};

        List<String> working = new ArrayList<>(stopIds);
        permute(working, 0, order -> {
            double cost = matrix.totalPathCost(order);
            if (cost < bestCost[0]) {
                bestCost[0] = cost;
                bestOrder[0] = List.copyOf(order);
            }
        });

        return bestOrder[0];
    }

    private interface PermutationConsumer {
        void accept(List<String> order);
    }

    private static void permute(List<String> items, int start, PermutationConsumer consumer) {
        if (start == items.size() - 1) {
            consumer.accept(items);
            return;
        }
        for (int i = start; i < items.size(); i++) {
            Collections.swap(items, start, i);
            permute(items, start + 1, consumer);
            Collections.swap(items, start, i);
        }
    }

}
