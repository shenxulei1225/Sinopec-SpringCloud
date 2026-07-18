package cn.cheers.x.module.platform.routing.planner;

import cn.cheers.x.module.platform.contract.dto.network.PathNodeDTO;
import cn.cheers.x.module.platform.contract.dto.topology.TopologyPointDTO;
import cn.cheers.x.framework.common.exception.ServiceException;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.cheers.x.module.platform.routing.enums.ErrorCodeConstants.ROUTE_UNREACHABLE;

/**
 * 停靠点两两代价矩阵。
 * <p>
 * {@link #from}：图上最短路（用于可达性 / 真实路程）。
 * {@link #forOrdering}：在图可达前提下，优先用场景水平面距离，避免「跳数相近但平面来回穿」。
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

    /**
     * 排停靠顺序用：图不可达仍为 +∞；否则用场景 XZ 欧氏距离，并用极小图代价破平。
     * 坐标不足一半时退回纯图代价。
     */
    public static CostMatrix forOrdering(List<String> stopIds, GraphView view, DijkstraPlanner planner) {
        CostMatrix graph = from(stopIds, view, planner);
        if (CollectionUtils.isEmpty(stopIds)) {
            return graph;
        }
        Map<String, double[]> horizontal = new HashMap<>();
        for (String stopId : stopIds) {
            double[] xz = horizontalXZ(view.getNode(stopId));
            if (xz != null) {
                horizontal.put(stopId, xz);
            }
        }
        if (horizontal.size() * 2 < stopIds.size()) {
            return graph;
        }

        Map<String, Map<String, Double>> matrix = new HashMap<>();
        for (String from : stopIds) {
            Map<String, Double> row = new HashMap<>();
            for (String to : stopIds) {
                if (from.equals(to)) {
                    row.put(to, 0D);
                    continue;
                }
                double graphCost = graph.cost(from, to);
                if (!Double.isFinite(graphCost)) {
                    row.put(to, Double.POSITIVE_INFINITY);
                    continue;
                }
                double[] a = horizontal.get(from);
                double[] b = horizontal.get(to);
                if (a == null || b == null) {
                    row.put(to, graphCost);
                    continue;
                }
                double dx = a[0] - b[0];
                double dz = a[1] - b[1];
                double euclid = Math.hypot(dx, dz);
                row.put(to, euclid + 1e-6 * graphCost);
            }
            matrix.put(from, row);
        }
        return new CostMatrix(stopIds, matrix);
    }

    /** 取子矩阵，供分区内求解。 */
    public CostMatrix submatrix(List<String> subset) {
        Map<String, Map<String, Double>> matrix = new HashMap<>();
        for (String from : subset) {
            Map<String, Double> row = new HashMap<>();
            for (String to : subset) {
                row.put(to, cost(from, to));
            }
            matrix.put(from, row);
        }
        return new CostMatrix(subset, matrix);
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

    /** 场景水平面：X / Z（Y 为高度）。 */
    static double[] horizontalXZ(PathNodeDTO node) {
        if (node == null) {
            return null;
        }
        TopologyPointDTO position = node.getPosition();
        if (position == null || position.getX() == null || position.getZ() == null) {
            return null;
        }
        return new double[]{position.getX(), position.getZ()};
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
