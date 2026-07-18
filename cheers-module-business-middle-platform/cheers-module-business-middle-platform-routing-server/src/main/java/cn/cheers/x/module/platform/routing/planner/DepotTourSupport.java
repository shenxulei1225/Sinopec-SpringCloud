package cn.cheers.x.module.platform.routing.planner;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 充电出发点（depot）闭环：固定起点访问其余停靠点后回到起点。
 */
public final class DepotTourSupport {

    private DepotTourSupport() {
    }

    public static List<String> uniqueStops(List<String> stopIds) {
        if (CollectionUtils.isEmpty(stopIds)) {
            return List.of();
        }
        Set<String> seen = new LinkedHashSet<>();
        for (String stopId : stopIds) {
            if (StringUtils.hasText(stopId)) {
                seen.add(stopId);
            }
        }
        return new ArrayList<>(seen);
    }

    public static List<String> withoutHome(List<String> stopIds, String home) {
        if (!StringUtils.hasText(home)) {
            return List.copyOf(stopIds);
        }
        List<String> others = new ArrayList<>();
        for (String stopId : stopIds) {
            if (!home.equals(stopId)) {
                others.add(stopId);
            }
        }
        return others;
    }

    /**
     * 将中间访问序接到起点前后，并按代价选择中间段朝向。
     * 结果形如 home → … → home（returnToStart 时）或 home → …。
     */
    public static List<String> assemble(String home,
                                        List<String> middleOrdered,
                                        CostMatrix matrix,
                                        boolean returnToStart) {
        if (!StringUtils.hasText(home)) {
            return List.copyOf(middleOrdered);
        }
        List<String> middle = new ArrayList<>(middleOrdered == null ? List.of() : middleOrdered);
        if (middle.size() >= 2 && matrix != null) {
            double head = matrix.cost(home, middle.get(0));
            double tail = matrix.cost(home, middle.get(middle.size() - 1));
            if (tail + 1e-9 < head) {
                Collections.reverse(middle);
            }
        }
        List<String> tour = new ArrayList<>(middle.size() + 2);
        tour.add(home);
        tour.addAll(middle);
        if (returnToStart) {
            tour.add(home);
        }
        return tour;
    }

}
