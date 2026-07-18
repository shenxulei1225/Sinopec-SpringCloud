package cn.cheers.x.module.platform.routing.planner;

import cn.cheers.x.module.platform.contract.dto.network.PathNodeDTO;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static cn.cheers.x.module.platform.routing.enums.ErrorCodeConstants.ROUTE_TOO_MANY_STOPS;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;

/**
 * 优化访问顺序：分区扫完再换区；区内外用场景距离（可达）+ Held-Karp / 最近邻+2-opt。
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

        List<List<String>> clusters = clusterByZone(stopIds, view);
        if (clusters.size() <= 1) {
            return solveCluster(stopIds, matrix);
        }

        List<List<String>> orderedClusters = orderClusters(clusters, view);
        List<String> tour = new ArrayList<>();
        String prevEnd = null;
        for (List<String> cluster : orderedClusters) {
            List<String> local = solveCluster(cluster, matrix.submatrix(cluster));
            if (prevEnd != null && local.size() >= 2) {
                double head = matrix.cost(prevEnd, local.get(0));
                double tail = matrix.cost(prevEnd, local.get(local.size() - 1));
                if (tail + 1e-9 < head) {
                    Collections.reverse(local);
                }
            }
            tour.addAll(local);
            prevEnd = tour.get(tour.size() - 1);
        }
        // 多区不再做全局 2-opt，避免把「扫完一区再换区」打散
        return List.copyOf(tour);
    }

    private List<String> solveCluster(List<String> stopIds, CostMatrix matrix) {
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
            // 有不可达时回退启发式，避免空结果
            return List.copyOf(stopIds);
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

    private static List<List<String>> clusterByZone(List<String> stopIds, GraphView view) {
        if (view == null) {
            return List.of(List.copyOf(stopIds));
        }
        Map<String, List<String>> groups = new LinkedHashMap<>();
        int zoned = 0;
        for (String stopId : stopIds) {
            PathNodeDTO node = view.getNode(stopId);
            Long zoneId = node != null ? node.getZoneId() : null;
            String key = zoneId != null ? "z:" + zoneId : "id:" + stopId;
            if (zoneId != null) {
                zoned++;
            }
            groups.computeIfAbsent(key, k -> new ArrayList<>()).add(stopId);
        }
        if (zoned < 2 || groups.size() <= 1) {
            return List.of(List.copyOf(stopIds));
        }
        return new ArrayList<>(groups.values());
    }

    private static List<List<String>> orderClusters(List<List<String>> clusters, GraphView view) {
        if (clusters.size() <= 1) {
            return clusters;
        }
        List<double[]> centroids = new ArrayList<>(clusters.size());
        for (List<String> cluster : clusters) {
            centroids.add(centroidXZ(cluster, view));
        }
        // 从离原点最远（或第一个有效质心）的簇开始，最近邻串簇，避免南北来回
        int start = 0;
        double bestR = -1;
        for (int i = 0; i < centroids.size(); i++) {
            double[] c = centroids.get(i);
            if (c == null) {
                continue;
            }
            double r = Math.hypot(c[0], c[1]);
            if (r > bestR) {
                bestR = r;
                start = i;
            }
        }

        boolean[] used = new boolean[clusters.size()];
        List<List<String>> ordered = new ArrayList<>(clusters.size());
        int current = start;
        for (int step = 0; step < clusters.size(); step++) {
            used[current] = true;
            ordered.add(clusters.get(current));
            if (step == clusters.size() - 1) {
                break;
            }
            double[] from = centroids.get(current);
            int next = -1;
            double best = Double.POSITIVE_INFINITY;
            for (int i = 0; i < clusters.size(); i++) {
                if (used[i]) {
                    continue;
                }
                double d = centroidDistance(from, centroids.get(i));
                if (d < best) {
                    best = d;
                    next = i;
                }
            }
            current = next >= 0 ? next : firstUnused(used);
        }
        return ordered;
    }

    private static int firstUnused(boolean[] used) {
        for (int i = 0; i < used.length; i++) {
            if (!used[i]) {
                return i;
            }
        }
        return 0;
    }

    private static double centroidDistance(double[] a, double[] b) {
        if (a == null || b == null) {
            return Double.POSITIVE_INFINITY;
        }
        return Math.hypot(a[0] - b[0], a[1] - b[1]);
    }

    private static double[] centroidXZ(List<String> cluster, GraphView view) {
        double sx = 0;
        double sz = 0;
        int n = 0;
        for (String stopId : cluster) {
            double[] xz = CostMatrix.horizontalXZ(view != null ? view.getNode(stopId) : null);
            if (xz == null) {
                continue;
            }
            sx += xz[0];
            sz += xz[1];
            n++;
        }
        if (n == 0) {
            return null;
        }
        return new double[]{sx / n, sz / n};
    }

}
