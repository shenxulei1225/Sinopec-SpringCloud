package cn.cheers.x.module.platform.routing.planner;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.cheers.x.module.platform.routing.enums.ErrorCodeConstants.ROUTE_UNREACHABLE;

/**
 * 停靠点两两最短路径代价矩阵（由 {@link DijkstraPlanner} 在 {@link GraphView} 上填充）。
 */
public final class CostMatrix {

    private final List<String> stopIds;
    private final Map<String, Map<String, Double>> costs;

    private CostMatrix(List<String> stopIds, Map<String, Map<String, Double>> costs) {
        this.stopIds = List.copyOf(stopIds);
        this.costs = costs;
    }

    public static CostMatrix from(List<String> stopIds, GraphView view, DijkstraPlanner planner) {
        Map<String, Map<String, Double>> matrix = new HashMap<>();
        if (CollectionUtils.isEmpty(stopIds)) {
            return new CostMatrix(List.of(), matrix);
        }
        for (String from : stopIds) {
            Map<String, Double> row = new HashMap<>();
            for (String to : stopIds) {
                if (from.equals(to)) {
                    row.put(to, 0D);
                } else {
                    row.put(to, shortestCost(from, to, view, planner));
                }
            }
            matrix.put(from, row);
        }
        return new CostMatrix(stopIds, matrix);
    }

    public List<String> stopIds() {
        return stopIds;
    }

    public double cost(String from, String to) {
        Map<String, Double> row = costs.get(from);
        if (row == null) {
            throw new IllegalArgumentException("Unknown stop: " + from);
        }
        Double value = row.get(to);
        if (value == null) {
            throw new IllegalArgumentException("Unknown stop: " + to);
        }
        return value;
    }

    public double totalPathCost(List<String> orderedStopIds) {
        if (CollectionUtils.isEmpty(orderedStopIds) || orderedStopIds.size() == 1) {
            return 0D;
        }
        double total = 0D;
        for (int i = 0; i < orderedStopIds.size() - 1; i++) {
            total += cost(orderedStopIds.get(i), orderedStopIds.get(i + 1));
        }
        return total;
    }

    private static double shortestCost(String from, String to, GraphView view, DijkstraPlanner planner) {
        try {
            return planner.shortestPath(from, to, view).getTotalCost();
        } catch (ServiceException ex) {
            if (ROUTE_UNREACHABLE.getCode().equals(ex.getCode())) {
                return Double.POSITIVE_INFINITY;
            }
            throw ex;
        }
    }

}
