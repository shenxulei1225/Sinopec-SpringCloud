package cn.cheers.x.module.platform.routing.planner;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static cn.cheers.x.module.platform.routing.enums.ErrorCodeConstants.ROUTE_TOO_MANY_STOPS;
import static cn.cheers.x.module.platform.routing.enums.ErrorCodeConstants.ROUTE_UNREACHABLE;
import static cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil.exception;

/**
 * 自动规划的访问顺序：按路网上的最短路程，求总里程最小的一圈。
 * 检查点不超过 18 个时用 Held-Karp 精确解；再多则从起点做最近邻，再用 2-opt 收短。
 * 代价矩阵由 {@link RoutePlanServiceImpl} 提供（forOrdering）；段几何仍由 Dijkstra 决定。
 */
@Component
public class OptimizeOrderStrategy implements StopOrderStrategy {

    /** Held-Karp 精确求解上限（开环路径） */
    static final int EXACT_MAX_STOPS = 18;

    static final int HARD_MAX_STOPS = 200;

    private static final int NEAREST_NEIGHBOR_SEED_CAP = 12;

    private final RefineOrderStrategy refineOrderStrategy;

    public OptimizeOrderStrategy(RefineOrderStrategy refineOrderStrategy) {
        this.refineOrderStrategy = refineOrderStrategy;
    }

    @Override
    public List<String> order(List<String> stopIds, CostMatrix matrix, GraphView view) {
        if (CollectionUtils.isEmpty(stopIds)) {
            return List.of();
        }
        if (stopIds.size() > HARD_MAX_STOPS) {
            throw exception(ROUTE_TOO_MANY_STOPS);
        }
        if (stopIds.size() <= 2) {
            return List.copyOf(stopIds);
        }
        return solveOpen(stopIds, matrix);
    }

    /**
     * 从充电桩（或其它固定起点）出发，每个检查点到一次，再回到起点或走到指定终点。
     * 比较的是路网上的最短路程之和，不是平面直线，也不是先排完检查点再把起点接到两头。
     */
    public List<String> orderDepot(String home,
                                    List<String> stops,
                                    String fixedEnd,
                                    boolean returnToStart,
                                    CostMatrix matrix) {
        if (!StringUtils.hasText(home)) {
            return solveOpen(stops, matrix);
        }
        List<String> middle = new ArrayList<>(stops == null ? List.of() : stops);
        if (!returnToStart && StringUtils.hasText(fixedEnd)) {
            middle.removeIf(fixedEnd::equals);
        }
        if (middle.isEmpty()) {
            if (StringUtils.hasText(fixedEnd) && !fixedEnd.equals(home)) {
                return List.of(home, fixedEnd);
            }
            return returnToStart ? List.of(home, home) : List.of(home);
        }
        String closeTo = returnToStart ? home : (StringUtils.hasText(fixedEnd) ? fixedEnd : null);
        List<String> orderedMiddle = middle.size() <= EXACT_MAX_STOPS
                ? heldKarpFromDepot(home, middle, closeTo, matrix)
                : improveDepotTour(home, nearestFrom(home, middle, matrix), closeTo, matrix);
        List<String> tour = new ArrayList<>(orderedMiddle.size() + 2);
        tour.add(home);
        tour.addAll(orderedMiddle);
        if (returnToStart) {
            tour.add(home);
        } else if (StringUtils.hasText(fixedEnd) && !fixedEnd.equals(tour.get(tour.size() - 1))) {
            tour.add(fixedEnd);
        }
        return List.copyOf(tour);
    }

    private List<String> solveOpen(List<String> stopIds, CostMatrix matrix) {
        if (CollectionUtils.isEmpty(stopIds)) {
            return List.of();
        }
        if (stopIds.size() <= 2) {
            return List.copyOf(stopIds);
        }
        if (stopIds.size() <= EXACT_MAX_STOPS) {
            return heldKarpOpenPath(stopIds, matrix);
        }
        return heuristicBestOrder(stopIds, matrix);
    }

    private List<String> heuristicBestOrder(List<String> stopIds, CostMatrix matrix) {
        int seedCount = Math.min(stopIds.size(), NEAREST_NEIGHBOR_SEED_CAP);
        List<String> best = nearestNeighborTour(stopIds, matrix, 0);
        double bestCost = matrix.totalPathCost(best);
        for (int seed = 1; seed < seedCount; seed++) {
            List<String> candidate = nearestNeighborTour(stopIds, matrix, seed);
            double cost = matrix.totalPathCost(candidate);
            if (cost + 1e-9 < bestCost) {
                best = candidate;
                bestCost = cost;
            }
        }
        return refineOrderStrategy.improve(best, matrix);
    }

    /**
     * 开环 TSP Held-Karp：不固定起点，求访问全集的最小路径。
     */
    static List<String> heldKarpOpenPath(List<String> stopIds, CostMatrix matrix) {
        int n = stopIds.size();
        double[][] dist = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                dist[i][j] = i == j ? 0D : matrix.cost(stopIds.get(i), stopIds.get(j));
            }
        }

        int full = 1 << n;
        double[][] dp = new double[full][n];
        int[][] parent = new int[full][n];
        for (int mask = 0; mask < full; mask++) {
            for (int i = 0; i < n; i++) {
                dp[mask][i] = Double.POSITIVE_INFINITY;
                parent[mask][i] = -1;
            }
        }
        for (int i = 0; i < n; i++) {
            dp[1 << i][i] = 0D;
        }

        for (int mask = 0; mask < full; mask++) {
            for (int i = 0; i < n; i++) {
                if ((mask & (1 << i)) == 0 || !Double.isFinite(dp[mask][i])) {
                    continue;
                }
                for (int j = 0; j < n; j++) {
                    if ((mask & (1 << j)) != 0 || !Double.isFinite(dist[i][j])) {
                        continue;
                    }
                    int next = mask | (1 << j);
                    double cand = dp[mask][i] + dist[i][j];
                    if (cand + 1e-12 < dp[next][j]) {
                        dp[next][j] = cand;
                        parent[next][j] = i;
                    }
                }
            }
        }

        int end = 0;
        double best = Double.POSITIVE_INFINITY;
        int all = full - 1;
        for (int i = 0; i < n; i++) {
            if (dp[all][i] + 1e-12 < best) {
                best = dp[all][i];
                end = i;
            }
        }
        if (!Double.isFinite(best)) {
            throw exception(ROUTE_UNREACHABLE);
        }

        List<Integer> rev = new ArrayList<>(n);
        int mask = all;
        int cur = end;
        while (cur >= 0) {
            rev.add(cur);
            int prev = parent[mask][cur];
            if (prev < 0) {
                break;
            }
            mask ^= (1 << cur);
            cur = prev;
        }
        Collections.reverse(rev);
        List<String> ordered = new ArrayList<>(n);
        for (int idx : rev) {
            ordered.add(stopIds.get(idx));
        }
        return ordered;
    }

    static List<String> nearestNeighborTour(List<String> stopIds, CostMatrix matrix, int seedIndex) {
        if (seedIndex < 0 || seedIndex >= stopIds.size()) {
            seedIndex = 0;
        }
        List<String> tour = new ArrayList<>(stopIds.size());
        Set<String> remaining = new HashSet<>(stopIds);
        String current = stopIds.get(seedIndex);
        tour.add(current);
        remaining.remove(current);

        while (!remaining.isEmpty()) {
            String next = null;
            double best = Double.POSITIVE_INFINITY;
            for (String candidate : remaining) {
                double cost = matrix.cost(current, candidate);
                if (cost < best) {
                    best = cost;
                    next = candidate;
                }
            }
            if (next == null) {
                next = remaining.iterator().next();
            }
            tour.add(next);
            remaining.remove(next);
            current = next;
        }
        return tour;
    }

    /**
     * 固定起点的 Held-Karp。closeTo 不为空时，最后一段回到起点或指定终点也算进总里程。
     */
    static List<String> heldKarpFromDepot(String home, List<String> stops, String closeTo, CostMatrix matrix) {
        int n = stops.size();
        double[] fromHome = new double[n];
        double[][] dist = new double[n][n];
        double[] toClose = new double[n];
        for (int i = 0; i < n; i++) {
            fromHome[i] = matrix.cost(home, stops.get(i));
            toClose[i] = closeTo == null ? 0D : matrix.cost(stops.get(i), closeTo);
            for (int j = 0; j < n; j++) {
                dist[i][j] = i == j ? 0D : matrix.cost(stops.get(i), stops.get(j));
            }
        }

        int full = 1 << n;
        double[][] dp = new double[full][n];
        int[][] parent = new int[full][n];
        for (int mask = 0; mask < full; mask++) {
            for (int i = 0; i < n; i++) {
                dp[mask][i] = Double.POSITIVE_INFINITY;
                parent[mask][i] = -1;
            }
        }
        for (int i = 0; i < n; i++) {
            if (Double.isFinite(fromHome[i])) {
                dp[1 << i][i] = fromHome[i];
            }
        }

        for (int mask = 0; mask < full; mask++) {
            for (int i = 0; i < n; i++) {
                if ((mask & (1 << i)) == 0 || !Double.isFinite(dp[mask][i])) {
                    continue;
                }
                for (int j = 0; j < n; j++) {
                    if ((mask & (1 << j)) != 0 || !Double.isFinite(dist[i][j])) {
                        continue;
                    }
                    int next = mask | (1 << j);
                    double cand = dp[mask][i] + dist[i][j];
                    if (cand + 1e-12 < dp[next][j]) {
                        dp[next][j] = cand;
                        parent[next][j] = i;
                    }
                }
            }
        }

        int all = full - 1;
        int end = -1;
        double best = Double.POSITIVE_INFINITY;
        for (int i = 0; i < n; i++) {
            double cand = dp[all][i] + toClose[i];
            if (cand + 1e-12 < best) {
                best = cand;
                end = i;
            }
        }
        if (end < 0 || !Double.isFinite(best)) {
            throw exception(ROUTE_UNREACHABLE);
        }

        List<Integer> rev = new ArrayList<>(n);
        int mask = all;
        int cur = end;
        while (cur >= 0) {
            rev.add(cur);
            int prev = parent[mask][cur];
            if (prev < 0) {
                break;
            }
            mask ^= (1 << cur);
            cur = prev;
        }
        Collections.reverse(rev);
        List<String> ordered = new ArrayList<>(n);
        for (int idx : rev) {
            ordered.add(stops.get(idx));
        }
        return ordered;
    }

    private static List<String> nearestFrom(String home, List<String> stops, CostMatrix matrix) {
        List<String> tour = new ArrayList<>(stops.size());
        Set<String> remaining = new HashSet<>(stops);
        String current = home;
        while (!remaining.isEmpty()) {
            String next = null;
            double best = Double.POSITIVE_INFINITY;
            for (String candidate : remaining) {
                double cost = matrix.cost(current, candidate);
                if (cost < best) {
                    best = cost;
                    next = candidate;
                }
            }
            if (next == null) {
                next = remaining.iterator().next();
            }
            tour.add(next);
            remaining.remove(next);
            current = next;
        }
        return tour;
    }

    private List<String> improveDepotTour(String home, List<String> middle, String closeTo, CostMatrix matrix) {
        List<String> current = new ArrayList<>(middle);
        double currentCost = depotTourCost(home, current, closeTo, matrix);
        boolean improved = true;
        int iterations = 0;
        while (improved && iterations < 80) {
            improved = false;
            iterations++;
            for (int i = 0; i < current.size() - 1; i++) {
                for (int j = i + 1; j < current.size(); j++) {
                    List<String> candidate = reverseRange(current, i, j);
                    double candidateCost = depotTourCost(home, candidate, closeTo, matrix);
                    if (candidateCost + 1e-9 < currentCost) {
                        current = candidate;
                        currentCost = candidateCost;
                        improved = true;
                    }
                }
            }
        }
        return current;
    }

    private static double depotTourCost(String home, List<String> middle, String closeTo, CostMatrix matrix) {
        if (middle.isEmpty()) {
            return 0D;
        }
        double cost = matrix.cost(home, middle.get(0));
        for (int i = 0; i < middle.size() - 1; i++) {
            cost += matrix.cost(middle.get(i), middle.get(i + 1));
        }
        if (closeTo != null) {
            cost += matrix.cost(middle.get(middle.size() - 1), closeTo);
        }
        return cost;
    }

    private static List<String> reverseRange(List<String> order, int i, int j) {
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
