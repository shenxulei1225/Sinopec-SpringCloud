package cn.cheers.x.module.platform.routing.planner;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Component
public class RefineOrderStrategy implements StopOrderStrategy {

    private static final int MAX_ITERATIONS = 80;

    @Override
    public List<String> order(List<String> stopIds, CostMatrix matrix, GraphView view) {
        return improve(stopIds, matrix);
    }

    /** 供优化策略在构造初始序后做 2-opt 精化。 */
    List<String> improve(List<String> stopIds, CostMatrix matrix) {
        if (CollectionUtils.isEmpty(stopIds) || stopIds.size() <= 2) {
            return List.copyOf(stopIds);
        }

        List<String> current = new ArrayList<>(stopIds);
        double currentCost = matrix.totalPathCost(current);
        boolean improved = true;
        int iterations = 0;

        while (improved && iterations < MAX_ITERATIONS) {
            improved = false;
            iterations++;
            for (int i = 0; i < current.size() - 1; i++) {
                for (int j = i + 1; j < current.size(); j++) {
                    List<String> candidate = twoOptSwap(current, i, j);
                    double candidateCost = matrix.totalPathCost(candidate);
                    if (candidateCost + 1e-9 < currentCost) {
                        current = candidate;
                        currentCost = candidateCost;
                        improved = true;
                    }
                }
            }
        }

        return List.copyOf(current);
    }

    private static List<String> twoOptSwap(List<String> order, int i, int j) {
        List<String> next = new ArrayList<>(order);
        while (i < j) {
            String tmp = next.get(i);
            next.set(i, next.get(j));
            next.set(j, tmp);
            i++;
            j--;
        }
        return next;
    }

}
